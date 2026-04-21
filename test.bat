@echo off
setlocal enabledelayedexpansion

set JAVA_HOME=C:\Program Files\Java\jdk-23
set BUILD=build\classes
set TEST_BUILD=build\test-classes

echo === Tests Automatizados — Padron Electoral ===

if not exist build mkdir build
if not exist "%BUILD%" mkdir "%BUILD%"

echo Compilando fuentes principales...
set SOURCES=
for /r "src" %%f in (*.java) do set SOURCES=!SOURCES! "%%f"
"%JAVA_HOME%\bin\javac" --enable-preview --release 23 -encoding UTF-8 -d "%BUILD%" !SOURCES!

if %ERRORLEVEL% neq 0 (
    echo ERROR: Fallo la compilacion de fuentes.
    pause
    exit /b 1
)

echo Compilando tests...
if not exist "%TEST_BUILD%" mkdir "%TEST_BUILD%"
set TEST_SOURCES=
for /r "test" %%f in (*.java) do set TEST_SOURCES=!TEST_SOURCES! "%%f"
"%JAVA_HOME%\bin\javac" --enable-preview --release 23 -encoding UTF-8 -cp "%BUILD%" -d "%TEST_BUILD%" !TEST_SOURCES!

if %ERRORLEVEL% neq 0 (
    echo ERROR: Fallo la compilacion de tests.
    pause
    exit /b 1
)

echo Ejecutando pruebas...
"%JAVA_HOME%\bin\java" --enable-preview -cp "%BUILD%;%TEST_BUILD%" padron.test.TestRunner

endlocal
