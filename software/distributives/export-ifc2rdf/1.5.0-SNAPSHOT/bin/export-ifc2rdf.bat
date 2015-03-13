@ECHO OFF

rem
rem Created by: Nam Vu Hoang, nam.vuhoang@aalto.fi
rem Creation date: 22.10.2013
rem 


SET MAIN_CLASS=fi.hut.cs.drumbeat.ifc.convert.ifc2rdf.cli.Main
IF "%1"=="" GOTO help
CALL bin/java-run.bat -ea %MAIN_CLASS% %*

GOTO :exit

:help
CALL bin/java-run.bat -ea %MAIN_CLASS% -?

:exit

pause