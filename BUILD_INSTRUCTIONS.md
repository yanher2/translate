# 翻译助手 - Android 应用

## 项目简介
这是一个运行在 Android 13 (API 33) 及以上版本的翻译应用，支持：
- 在其他应用中长按选中文本，选择"翻译助手"进行翻译
- 本地单词本功能，方便保存和复习单词

## 构建说明

### 方法 1: 使用 Android Studio (推荐)
1. 用 Android Studio 打开项目
2. 等待 Gradle 同步完成
3. 连接 Android 设备或启动模拟器
4. 点击 Run 按钮 (绿色三角形)

### 方法 2: 使用命令行构建

#### Windows:
```bash
# 清理构建
gradlew.bat clean

# 构建 Debug APK
gradlew.bat assembleDebug

# 构建 Release APK
gradlew.bat assembleRelease
```

#### Linux/Mac:
```bash
# 清理构建
./gradlew clean

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease
```

### 方法 3: 使用本地 Gradle (如果没有 gradlew)
```bash
# 安装 Gradle (如果尚未安装)
# 下载: https://gradle.org/install/

# 构建
gradle assembleDebug
```

## 构建产物位置

- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK**: `app/build/outputs/apk/release/app-release.apk`

## 安装 APK 到设备

### 方法 1: 使用 ADB
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 方法 2: 直接传输
将 APK 文件传输到手机，然后在手机上打开安装

## 项目依赖

- Kotlin 2.0.21
- Android Gradle Plugin 8.10.0-alpha05
- Compose Material3
- Room Database 2.6.1
- OkHttp 4.12.0

## 使用说明

### 翻译文本
1. 在任何应用中长按选中文本
2. 点击"翻译助手"选项
3. 查看翻译结果
4. 可选择加入单词本

### 单词本
1. 打开应用
2. 点击"单词本"卡片
3. 查看已保存的单词
4. 支持搜索、删除单词

## 常见问题

### Q: gradlew.bat 命令不存在？
A: 这表示 Gradle Wrapper 文件缺失。可以：
   - 使用 Android Studio 打开项目（会自动下载）
   - 或者使用本地安装的 Gradle

### Q: 构建失败？
A: 请确保：
   - JDK 11 或更高版本已安装
   - Android SDK 已安装
   - 网络连接正常（需要下载依赖）

### Q: 应用图标不显示？
A: 所有图标资源已创建，应该正常显示。如仍有问题，请清理后重新构建。

## 技术支持

如有问题，请检查：
1. Android SDK 版本是否匹配
2. JDK 版本是否为 11+
3. 网络连接是否正常
4. 是否有足够的磁盘空间
