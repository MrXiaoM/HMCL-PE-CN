# HMCL-PE SweetRice 服务器版

懒得写说明文件。

可以借用本项目开发你服务器的安卓客户端，说明代码来源于本仓库即可。

# 编译调试
```
./gradlew assembleDebug
```
输出路径 `HMCLPE/build/outputs/apk/debug/`

# 编译发布
fork 本仓库，自己找教程新建一个APK签名文件 (`.jks`)，  
按照[这里的教程](https://github.com/r0adkll/sign-android-release)获取4个参数 (`SIGNING_KEY`, `ALIAS`, `KEY_PASSWORD`, `KEY_STORE_PASSWORD`)，  
然后在 `Settings` -> `Secrets and variables` -> `Actions` 新建这四个secrets。

然后到 `Actions` 选项卡手动运行工作流 `Build Release`，等完成后在 `Artifacts` 下载即可。
