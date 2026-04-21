@echo off
setlocal enabledelayedexpansion

set JAVA_HOME=C:\Program Files\Java\jdk-23
set BUILD=build\classes
set JAR=build\ClientePadron.jar

echo === Cliente Padron Electoral ===

if not exist build mkdir build
if not exist "%BUILD%" mkdir "%BUILD%"

echo Compilando fuentes...
set SOURCES=
for /r "src" %%f in (*.java) do set SOURCES=!SOURCES! "%%f"

"%JAVA_HOME%\bin\javac" --enable-preview --release 23 -encoding UTF-8 -d "%BUILD%" !SOURCES!

if %ERRORLEVEL% neq 0 (
    echo ERROR: Fallo la compilacion.
    pause
    exit /b 1
)
echo Compilacion exitosa.

echo Empaquetando JAR...
echo Main-Class: cliente.Main> manifest.txt
"%JAVA_HOME%\bin\jar" cfm "%JAR%" manifest.txt -C "%BUILD%" .
del manifest.txt

if %ERRORLEVEL% neq 0 (
    echo ERROR: Fallo al crear el JAR.
    pause
    exit /b 1
)

echo Iniciando interfaz grafica...
"%JAVA_HOME%\bin\java" --enable-preview -jar "%JAR%"

endlocal
