@echo off
REM Invoice2X Simple Pro Build Script for Windows
REM This script compiles the Java source code and creates an executable JAR

echo ================================================
echo Invoice2X Simple Pro - Build Script
echo ================================================
echo.

REM Check if Java is installed
where javac >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java compiler (javac) not found!
    echo Please install Java JDK 11 or higher
    echo Download from: https://adoptium.net/
    pause
    exit /b 1
)

echo Java compiler found
javac -version
echo.

REM Create directories
echo Creating build directories...
if not exist build\classes mkdir build\classes
if not exist dist mkdir dist
if not exist lib mkdir lib

REM Check for required libraries
echo.
echo Checking for required libraries...
set MISSING_LIBS=0

if not exist lib\sqlite-jdbc-3.43.0.0.jar (
    echo WARNING: sqlite-jdbc-3.43.0.0.jar not found in lib\
    set MISSING_LIBS=1
)

if not exist lib\poi-5.2.3.jar (
    echo WARNING: Apache POI libraries not found in lib\
    echo Required: poi-5.2.3.jar, poi-ooxml-5.2.3.jar, etc.
    set MISSING_LIBS=1
)

if %MISSING_LIBS% EQU 1 (
    echo.
    echo ERROR: Missing required libraries!
    echo Please download and place in lib\ directory:
    echo   - SQLite JDBC: https://github.com/xerial/sqlite-jdbc/releases
    echo   - Apache POI: https://poi.apache.org/download.html
    pause
    exit /b 1
)

echo All libraries found!
echo.

REM Compile source files
echo Compiling source files...
javac -d build\classes -cp "lib\*" ^
    src\com\invoice2x\*.java ^
    src\com\invoice2x\model\*.java ^
    src\com\invoice2x\service\*.java ^
    src\com\invoice2x\ui\*.java ^
    src\com\invoice2x\ui\panels\*.java ^
    src\com\invoice2x\util\*.java

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo Compilation successful!
echo.

REM Create manifest file
echo Creating manifest...
(
echo Manifest-Version: 1.0
echo Main-Class: com.invoice2x.Main
echo Class-Path: lib/sqlite-jdbc-3.43.0.0.jar lib/poi-5.2.3.jar lib/poi-ooxml-5.2.3.jar lib/poi-ooxml-lite-5.2.3.jar lib/xmlbeans-5.1.1.jar lib/commons-compress-1.21.jar lib/commons-collections4-4.4.jar
) > build\MANIFEST.MF

REM Create JAR file
echo Creating JAR file...
cd build\classes
jar cfm ..\..\dist\invoice2x.jar ..\MANIFEST.MF com\
cd ..\..

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: JAR creation failed!
    pause
    exit /b 1
)

REM Copy libraries to dist
echo Copying libraries...
xcopy /E /I /Y lib dist\lib

REM Create run batch file
echo Creating run script...
(
echo @echo off
echo java -Xms256m -Xmx1024m -jar invoice2x.jar
echo pause
) > dist\run.bat

echo.
echo ================================================
echo Build completed successfully!
echo ================================================
echo.
echo Output files in: dist\
echo   - invoice2x.jar
echo   - lib\ ^(dependencies^)
echo   - run.bat ^(launch script^)
echo.
echo To run the application:
echo   cd dist
echo   run.bat
echo.
echo Or directly:
echo   java -jar dist\invoice2x.jar
echo.
pause