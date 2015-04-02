@echo off

cd ../..

SET INPUT_SCHEMA_FILE_PATH=resources/IFC2X3_TC1.exp
SET OUTPUT_SCHEMA_FILE_PATH=output/IFC2X3_Standard

rem sample formats: TURTLE, TURTLE.GZ, NQUADS, NQUADS.GZ, NTRIPLES, NTRIPLES.GZ, JSONLD, RDFXML, etc.
SET FORMAT=TURTLE_PRETTY
SET CONVERSION_OPTION=Standard

bin/export-ifc2rdf.bat -lcf config/log4j.xml -cf config/ifc2rdf-config.xml -isf %INPUT_SCHEMA_FILE_PATH% -osf %OUTPUT_SCHEMA_FILE_PATH% -off %FORMAT% -oln %CONVERSION_OPTION%
pause
