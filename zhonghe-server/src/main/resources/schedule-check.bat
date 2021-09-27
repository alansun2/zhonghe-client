@echo off
SETLOCAL ENABLEDELAYEDEXPANSION
chcp 65001

set installDir=${installDir}
set p2=0
for %%n in (8200) do (
    for /f "tokens=4" %%i in ('netstat -ano ^| findstr ":%%n"') do (
	echo find the process which use port [%%n] PID [%%i]
	set p2=%%i
    )
    echo !p2!

    if !p2! ==0 (
	    start "" "%installDir%\server\服务器软件.exe"
    )
)

set p3=0
for %%n in (8607) do (
    for /f "tokens=4" %%i in ('netstat -ano ^| findstr ":%%n"') do (
	echo find the process which use port [%%n] PID [%%i]
	set p3=%%i
    )
    echo !p3!

    if !p3! ==0 (
	    start "" "%installDir%\MiddleWare.exe"
    )
)

set p1=0
for %%n in (8084) do (
    for /f "tokens=5" %%i in ('netstat -ano ^| findstr ":%%n"') do (
	echo find the process which use port [%%n] PID [%%i]
	set p1=%%i
    )
    echo !p1!

    if !p1! ==0 (
	    %installDir%\jszn-middleware\jszn-middleware-start.bat
    )
)