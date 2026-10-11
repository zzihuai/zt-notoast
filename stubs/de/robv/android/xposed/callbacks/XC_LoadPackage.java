package de.robv.android.xposed.callbacks;

/** 编译期 stub，签名对齐 LSPosed 的 XC_LoadPackage.LoadPackageParam。 */
public abstract class XC_LoadPackage {

    public static final class LoadPackageParam {
        public String packageName;
        public String processName;
        public ClassLoader classLoader;
        public boolean isFirstApplication;
    }
}
