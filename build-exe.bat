@echo off
setlocal EnableExtensions
chcp 65001 >nul
cd /d "%~dp0"

if not defined JAVA_HOME (
    echo [ERROR] JAVA_HOME не задан
    exit /b 1
)

if not exist "%JAVA_HOME%\bin\jpackage.exe" (
    echo [ERROR] Не найден jpackage:
    echo %JAVA_HOME%\bin\jpackage.exe
    exit /b 1
)

if not exist "target\AirportIS-1.0.0.jar" (
    echo [ERROR] Не найден target\AirportIS-1.0.0.jar
    echo Сначала запусти Maven package в IDEA
    exit /b 1
)

if not exist "target\jpackage-input" (
    echo [ERROR] Не найдена папка target\jpackage-input
    echo Сначала запусти Maven package в IDEA
    exit /b 1
)

if not exist "dist" mkdir "dist"

set "MP=%JAVA_HOME%\jmods;target\AirportIS-1.0.0.jar;target\jpackage-input"

"%JAVA_HOME%\bin\jpackage.exe" ^
  --type exe ^
  --dest dist ^
  --name AirportIS ^
  --app-version 1.0.0 ^
  --vendor "Soul_KRT" ^
  --description "Airport information system" ^
  --install-dir "AirportIS" ^
  --module-path "%MP%" ^
  --module com.airport/com.airport.AirportApplication ^
  --java-options "-Dfile.encoding=UTF-8" ^
  --win-menu ^
  --win-menu-group "AirportIS" ^
  --win-shortcut ^
  --win-dir-chooser ^
  --win-per-user-install ^
  --icon app.ico ^
  --verbose

if errorlevel 1 exit /b 1

echo [OK] Готово. Результат в папке dist
endlocal