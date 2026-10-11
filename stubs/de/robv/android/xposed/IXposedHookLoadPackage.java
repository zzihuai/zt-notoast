package de.robv.android.xposed;

/**
 * 编译期 stub —— 只为 javac 提供签名，不会打进 APK。
 * 运行时的实现由 LSPosed 提供。
 */
public interface IXposedHookLoadPackage {
    void handleLoadPackage(de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam lpparam)
            throws Throwable;
}
