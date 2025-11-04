@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM

@echo off
setlocal enabledelayedexpansion

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@REM Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

if not "%JAVA_HOME%" == "" (
    set JAVA_EXE=%JAVA_HOME%\bin\java.exe
    if exist "!JAVA_EXE!" (
        goto init
    )
)

echo Error: JAVA_HOME is not properly set. Please install Java 17 or higher.
exit /b 1

:init
@REM Find the project root (where pom.xml is located)
setlocal
cd /d "%DIRNAME%..\..\"

@REM Try to download Maven if not found
mvn --version >nul 2>&1
if errorlevel 1 (
    echo Maven not found. Attempting to use embedded or install...
    call powershell -ExecutionPolicy Bypass -File "%DIRNAME%..\..\install-maven.ps1"
    if errorlevel 1 (
        echo.
        echo ERROR: Maven installation failed.
        echo Please install Maven manually from: https://maven.apache.org/
        exit /b 1
    )
)

@REM Execute Maven
mvn %*
exit /b %ERRORLEVEL%

