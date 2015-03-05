@echo off

cd ../..

SET INPUT_SCHEMA_FILE_PATH=resources\IFC2X3_TC1.exp
SET INPUT_MODEL_FILE_PATH=resources\sample.ifc
SET OUTPUT_MODEL_FILE_PATH=output\sample
SET FORMAT=NTRIPLES.GZ
SET CONVERSION_OPTION=Lite
bin/export-ifc2rdf.bat -lcf config\log4j.xml -cf config\IfcAnalysis.xml -isf %INPUT_SCHEMA_FILE_PATH% -imf %INPUT_MODEL_FILE_PATH%  -omf %OUTPUT_MODEL_FILE_PATH% -off %FORMAT% -oln %CONVERSION_OPTION%
pause
