@echo off
echo ========================================
echo 翻译助手 - 构建脚本
echo ========================================
echo.

cd /d "%~dp0"

echo [1/3] 清理构建缓存...
if exist gradlew.bat (
    gradlew.bat clean --no-daemon
) else (
    echo 警告: gradlew.bat 不存在，跳过清理
)
echo.

echo [2/3] 开始构建 Debug APK...
if exist gradlew.bat (
    gradlew.bat assembleDebug --no-daemon --console=plain
) else (
    echo 错误: gradlew.bat 不存在
    echo 请使用 Android Studio 打开项目构建，或手动安装 Gradle
    exit /b 1
)
echo.

if %ERRORLEVEL% EQU 0 (
    echo [3/3] 构建成功！
    echo.
    echo APK 文件位置: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo 安装到设备:
    echo   adb install app\build\outputs\apk\debug\app-debug.apk
    echo.
) else (
    echo [3/3] 构建失败！
    echo.
    echo 常见问题:
    echo   1. 确保已安装 JDK 11 或更高版本
    echo   2. 确保已安装 Android SDK
    echo   3. 检查网络连接（需要下载依赖）
    echo   4. 尝试使用 Android Studio 构建项目
    echo.
)

pause
