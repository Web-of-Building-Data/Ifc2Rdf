@echo off

cd ../..

SET INPUT_SCHEMA_FILE_PATH=resources/IFC2X3_TC1.exp
SET INPUT_MODEL_FILE_PATH=C:\DRUM\!git_local\DRUM\Test\Models\drumbeat.cs.hut.fi\linked\TS-Linked-1\Structural.ifc
SET OUTPUT_MODEL_FILE_PATH=C:\DRUM\!git_local\DRUM\Test\Models\drumbeat.cs.hut.fi\linked\TS-Linked-1\Structural

rem sample formats: TURTLE, TURTLE.GZ, NQUADS, NQUADS.GZ, NTRIPLES, NTRIPLES.GZ, JSONLD, RDFXML, etc.
SET FORMAT=TURTLE
SET CONVERSION_OPTION=Standard

bin/export-ifc2rdf.bat -lcf config/log4j.xml -cf config/ifc2rdf-config.xml -isf %INPUT_SCHEMA_FILE_PATH% -imf %INPUT_MODEL_FILE_PATH%  -omf %OUTPUT_MODEL_FILE_PATH% -off %FORMAT% -oln %CONVERSION_OPTION%
pause
