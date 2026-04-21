@echo off
setlocal EnableExtensions
chcp 65001 >nul
cd /d "%~dp0"

set "APP_NAME=AirportIS"
set "APP_VENDOR=Soul_KRT"
set "APP_MODULE=com.airport/com.airport.AirportApplication"
set "INSTALL_DIR=%APP_NAME%"
set "UPGRADE_UUID=9d1f4f9d-8c4c-4e4d-bf6d-5f1e7d6b8c21"

for /f "usebackq delims=" %%V in (`powershell -NoProfile -Command "[xml]$pom = Get-Content 'pom.xml'; $pom.project.version"`) do set "APP_VERSION=%%V"

if not defined APP_VERSION (
    echo [ERROR] Не удалось прочитать version из pom.xml
    exit /b 1
)

set "APP_JAR=target\%APP_NAME%-%APP_VERSION%.jar"

if not defined JAVA_HOME (
    echo [ERROR] JAVA_HOME не задан
    exit /b 1
)

if not exist "%JAVA_HOME%\bin\jpackage.exe" (
    echo [ERROR] Не найден jpackage:
    echo %JAVA_HOME%\bin\jpackage.exe
    exit /b 1
)

if not exist "%APP_JAR%" (
    echo [ERROR] Не найден %APP_JAR%
    echo Сначала запусти Maven package в IDEA
    exit /b 1
)

if not exist "target\jpackage-input" (
    echo [ERROR] Не найдена папка target\jpackage-input
    echo Сначала запусти Maven package в IDEA
    exit /b 1
)

if not exist "dist" mkdir "dist"

set "MP=%JAVA_HOME%\jmods;%APP_JAR%;target\jpackage-input"

echo [INFO] APP_VERSION=%APP_VERSION%
echo [INFO] APP_JAR=%APP_JAR%

"%JAVA_HOME%\bin\jpackage.exe" ^
  --type exe ^
  --dest dist ^
  --name "%APP_NAME%" ^
  --app-version "%APP_VERSION%" ^
  --vendor "%APP_VENDOR%" ^
  --description "Airport information system" ^
  --install-dir "%INSTALL_DIR%" ^
  --module-path "%MP%" ^
  --module %APP_MODULE% ^
  --java-options "-Dfile.encoding=UTF-8" ^
  --win-menu ^
  --win-menu-group "%APP_NAME%" ^
  --win-shortcut ^
  --win-dir-chooser ^
  --win-upgrade-uuid "%UPGRADE_UUID%" ^
  --icon app.ico ^
  --verbose

if errorlevel 1 exit /b 1

echo [OK] Готово. Результат в папке dist
endlocal