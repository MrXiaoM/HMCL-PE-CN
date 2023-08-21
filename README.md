# HMCL-PE SweetRice 服务器版

可以借用本项目开发你服务器的安卓客户端，说明代码来源于本仓库即可。

# 更改说明

* 移除了联机模块
* 添加游戏启动时背景图
* 开屏界面进度条样式优化
* 背景图片显示方式从拉伸改为自适应
* 游戏内悬浮菜单默认显示在屏幕右上方而不是中间

# 快速上手

吃水不忘挖井人，希望使用这个包的同时，不要抹除“关于”页面的本仓库开源地址以及作者信息。

* 第一个 [up](https://github.com/MrXiaoM/HMCL-PE-CN/commit/306f638105f7b04e26acb47ac00a5f3689002d3e) commit 包含了基本上全部的修改，后面的都是完善工作流和修改包名。
* 在 `HMCLPE/src/main/assets/config.properties` 有一些前人留下的设置
* 目前这个包只留了一个 `java 8`，毕竟有一个 java 就够了。java 在 `HMCLPE\src\main\assets\app_runtime\java\default` 里，需要的话自己找方法换。
* 要解压的客户端目录在 `HMCLPE/src/main/assets/.minecraft`
* 为了照顾到低配置的手机，请不要往客户端目录打包 `assets` 和 `libraries`，并对不需要的文件做好清理，以免造成过多的储存空间占用。那些都可以在启动时下载。
* 图片资源在 `HMCLPE/src/main/res/drawable`
* 配色方案在 `HMCLPE/src/main/res/values/colors.xml`
* 默认键位在 `HMCLPE/src/main/assets/control/Default`，可以在软件编辑好之后，导出复制到这里
* 游戏内右下角小字在 `Boat/src/main/res/layout/activity_boat.yml` 和 `PojavLauncher/src/main/res/layout/activity_pojav.yml`
* 默认包名为 `com.tungsten.hmclpe.sweetrice`，已经可以与原版 HMCLPE 共存。如果你想更改包名，请到 `HMCLPE/build.gradle` 更改，并重构代码，将旧包名改为新包名 (包括代码的包名和代码里硬编码的旧包名)。
* 设置里的 `帮助`、`反馈`、`赞助`、`关于` 页面分别在 `HMCLPE/src/main/res/layout` 里面的 `ui_setting_help.xml`、`ui_setting_feedback.xml`、`ui_setting_donate.xml`、`ui_setting_about.xml`，逻辑代码在 `HMCLPE/src/main/java/com/tungsten/hmclpe/sweetrice/launcher/uis/universal/setting/right` 内
* 几乎所有的文字都在 `PojavLauncher/src/main/res/values-xx/strings.xml`，`xx` 代表语言
* 我在离线模式添加账户对话框那里加了“第一次启动游戏需要下载大约 320MB 的资源文件”，如果需要修改，`HMCLPE/src/main/res/layout/dialog_add_account.yml`
* 启动检查流程在 `HMCLPE/src/main/java/com/tungsten/hmclpe/launcher/launch/check/CheckLibTask.java`，我在那里加了个自动检查资源包更新，如果还想下载其它东西可以以它为参考。

# 编译调试
```
./gradlew assembleDebug
```
输出路径 `HMCLPE/build/outputs/apk/debug/`

# 编译发布
fork 本仓库，自己找教程新建一个APK签名文件 (`.jks`)，  

然后按照[这里的教程](https://github.com/r0adkll/sign-android-release)获取4个参数 (`SIGNING_KEY`, `ALIAS`, `KEY_PASSWORD`, `KEY_STORE_PASSWORD`)，  
然后在 `Settings` -> `Secrets and variables` -> `Actions` 新建这四个secrets。

然后到 `Actions` 选项卡手动运行工作流 `Build Release`，等完成后在 `Artifacts` 下载即可。
