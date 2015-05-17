@ECHO ON

@SET OUTPUT_FILE=maven-output.txt
@SET ERROR_FILE=maven-error.txt

@del %OUTPUT_FILE%
@del %ERROR_FILE%

@ECHO ON
CALL maven-single.bat drumbeat-test123 1>> %OUTPUT_FILE% 2>> %ERROR_FILE%

PAUSE


