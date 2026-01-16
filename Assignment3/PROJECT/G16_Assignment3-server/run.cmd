@echo off
setlocal enabledelayedexpansion

REM =========================
REM Bistro Server runner
REM =========================

REM ---- Defaults (change if you want) ----
set "DEFAULT_PORT=8080"
set "DEFAULT_DB_URL=jdbc:mysql://db-bistro-g16.cbe862egq27l.eu-north-1.rds.amazonaws.com:3306"
set "DEFAULT_DB_USER=admin"
set "DEFAULT_DB_PASSWORD=TOKEN"

REM ---- Set env vars (use existing values if already set) ----
if not defined PORT set "PORT=%DEFAULT_PORT%"
if not defined DB_URL set "DB_URL=%DEFAULT_DB_URL%"
if not defined DB_USER set "DB_USER=%DEFAULT_DB_USER%"
if not defined DB_PASSWORD set "DB_PASSWORD=%DEFAULT_DB_PASSWORD%"

REM ---- Jar path (allow override) ----
if "%~1"=="" (
    set "JAR_PATH=.\target\server-1.0-SNAPSHOT.jar"
) else (
    set "JAR_PATH=%~1"
)

if not exist "%JAR_PATH%" (
    echo ERROR: JAR not found: %JAR_PATH%
    echo Usage: %~nx0 path\to\server.jar
    exit /b 1
)

echo Starting server...
echo PORT=%PORT%
echo DB_URL=%DB_URL%
echo DB_USER=%DB_USER%
echo DB_PASSWORD=(hidden)
echo JAR=%JAR_PATH%

REM Optional JVM opts via JAVA_OPTS env var (e.g. set JAVA_OPTS=-Xms256m -Xmx512m)
if not defined JAVA_OPTS set "JAVA_OPTS="

java %JAVA_OPTS% -jar "%JAR_PATH%"