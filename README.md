# ZT NoToast

一个极小的 **LSPosed / Xposed 模块**，只做一件事：

> 屏蔽 **ZeroTermux**（包名 `com.termux`）在系统剪贴板变成空内容时弹出的
> **「复制内容不能为空噢」** Toast。

## 背景

ZeroTermux 注册了系统剪贴板监听（`com.termux.zerocore.utils.ClipBoardUtil`）来维护它自己的
剪贴板历史。当 KDE Connect 把电脑端的空内容/非文本内容同步到手机剪贴板时，监听器读到空值，
就会弹这条 Toast。

本模块**不动剪贴板监听本身**，剪贴板历史功能照常工作，只是把这条提示吞掉。

## 实现了两个 hook（互为兜底）

| 位置 | 说明 |
|---|---|
| `android.widget.Toast#show()` | 读 Toast 的 `mText`，命中「复制内容不能为空」就跳过。不依赖应用内部类名，最稳。 |
| `com.example.xh_lib.utils.UUtils#showMsg(String)` | ZeroTermux 自己的弹窗入口，命中同样跳过。 |

只对 `com.termux` 进程生效，其它应用不受影响。

## 安装

1. 手机上装好 **LSPosed**（KernelSU 用户还需要 Zygisk 方案）。
2. 安装本模块 APK。
3. LSPosed 管理器里启用模块，**作用域勾选 `com.termux`**。
4. 强制停止 / 重启 ZeroTermux。

## 验证

```bash
# 抓零号 Termux 自己的日志（uid 相同，可直接看）
logcat -s 'ZeroTermux--ClipBoardUtil:V' '*:S'
```

把系统剪贴板设为空，如果日志里出现
`blocked Toast.show: 复制内容不能为空噢`，说明模块生效了。

## 构建

GitHub Actions 自动构建（见 `.github/workflows/build.yml`），产物是 workflow artifact。
用 JDK + Android SDK 的 `aapt2 / d8 / zipalign / apksigner` 手工打包，不需要 Gradle。

`stubs/` 里是 Xposed API 的**编译期 stub**（签名已对照 LSPosed 源码核对），
不会打进 APK —— 运行时的 `de.robv.android.xposed.*` 由 LSPosed 提供。
