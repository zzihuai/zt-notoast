package de.robv.android.xposed;

/** 编译期 stub，签名对齐 Xposed API（XC_MethodHook / MethodHookParam / Unhook）。 */
public abstract class XC_MethodHook {

    public static final class MethodHookParam {
        public Object thisObject;
        public Object[] args;
        public Object result;
        public Throwable throwable;

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
        }
    }

    public static class Unhook {
        public void unhook() {
        }
    }

    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
    }

    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
    }
}
