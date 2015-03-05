@ECHO OFF

rem
rem Created by: Nam Vu Hoang, nam.vuhoang@aalto.fi
rem Creation date: 22.10.2013
rem 

SET JAVA=java
IF EXIST "%JAVA_HOME%\bin\java.exe" SET JAVA="%JAVA_HOME%\bin\java"

SET WORKING_DIR=%~p0\..
SET TMP_CP="."

rem see http://ss64.com/nt/for_r.html for more details about this command
rem FOR /R lib %%I IN (*.*) DO CALL bin\java-add-cp.bat "%%I"
rem FOR /D /R %%I IN ("lib*\*") DO echo CALL bin\java-add-cp.bat %%I

CALL bin\java-add-cp.bat lib
FOR /F "tokens=*" %%I IN ('dir /b /s /a:d lib') DO CALL bin\java-add-cp.bat %%I

%JAVA% -cp %TMP_CP% %*