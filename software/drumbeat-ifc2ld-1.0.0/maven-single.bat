@ECHO OFF

ECHO.
ECHO *****************************************
ECHO BUILDING %1 (started)
ECHO *****************************************
ECHO.

CALL mvn clean install -f %1\pom.xml

ECHO.
ECHO *****************************************
ECHO BUILDING %1 (completed)
ECHO *****************************************
ECHO.

PAUSE
