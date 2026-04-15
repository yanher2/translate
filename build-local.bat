@echo off
echo ========================================
echo Using Local Gradle at E:\gradle
echo ========================================
echo.

E:\gradle\bin\gradle.bat assembleDebug --no-daemon

echo.
echo ========================================
echo Build complete!
echo APK location: app\build\outputs\apk\debug\app-debug.apk
echo ========================================
pause
