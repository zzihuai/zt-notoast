package com.zerotermux.notoast;

import android.widget.Toast;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 只做一件事：吞掉 ZeroTermux 在系统剪贴板变成“空”时弹出的
 * 「复制内容不能为空噢」Toast。
 *
 * 不碰剪贴板监听本身，所以 ZeroTermux 的剪贴板历史功能照常工作。
 */
public class Hook implements IXposedHookLoadPackage {

    private static final String TAG = "[ZTNoToast] ";

    /** 只对 ZeroTermux 生效（com.termux）。 */
    private static final String TARGET_PACKAGE = "com.termux";

    /** 要屏蔽的文案片段（实际文案是「复制内容不能为空噢」）。 */
    private static final String BLOCKED_TEXT = "复制内容不能为空";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam == null || !TARGET_PACKAGE.equals(lpparam.packageName)) {
            return;
        }

        XposedBridge.log(TAG + "injected into " + lpparam.packageName
                + " process=" + lpparam.processName);

        // 1) 主 hook：框架层，任何走 Toast 的路径都能拦到
        hookToastShow();

        // 2) 备用 hook：应用层入口（类名是 ZeroTermux 自带的 xh_lib）
        hookAppShowMsg(lpparam.classLoader);
    }

    private void hookToastShow() {
        try {
            XposedHelpers.findAndHookMethod(Toast.class, "show", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    Object text = readToastText(param.thisObject);
                    if (isBlocked(text)) {
                        XposedBridge.log(TAG + "blocked Toast.show: " + text);
                        param.setResult(null);
                    }
                }
            });
            XposedBridge.log(TAG + "hooked Toast.show()");
        } catch (Throwable t) {
            XposedBridge.log(TAG + "hook Toast.show() failed: " + t);
        }
    }

    private void hookAppShowMsg(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod(
                    "com.example.xh_lib.utils.UUtils", classLoader, "showMsg",
                    String.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            Object msg = (param.args != null && param.args.length > 0)
                                    ? param.args[0] : null;
                            if (isBlocked(msg)) {
                                XposedBridge.log(TAG + "blocked UUtils.showMsg: " + msg);
                                param.setResult(null);
                            }
                        }
                    });
            XposedBridge.log(TAG + "hooked UUtils.showMsg(String)");
        } catch (Throwable t) {
            // 找不到就算了，Toast.show 那条已经能兜住
            XposedBridge.log(TAG + "hook UUtils.showMsg failed (ignored): " + t);
        }
    }

    /** Toast 的文字在私有字段 mText 里，AOSP 各版本都叫这个名字。 */
    private static Object readToastText(Object toast) {
        if (toast == null) {
            return null;
        }
        try {
            return XposedHelpers.getObjectField(toast, "mText");
        } catch (Throwable t) {
            return null;
        }
    }

    private static boolean isBlocked(Object candidate) {
        if (candidate == null) {
            return false;
        }
        try {
            return candidate.toString().contains(BLOCKED_TEXT);
        } catch (Throwable t) {
            return false;
        }
    }
}
