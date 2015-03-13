@echo off

cd ../..

SET INPUT_SCHEMA_FILE_PATH=resources/
SET INPUT_MODEL_FILE_PATH=resources/sample.ifc
SET OUTPUT_MODEL_FILE_PATH=output/sample

rem sample formats: TURTLE, TURTLE.GZ, NQUADS, NQUADS.GZ, NTRIPLES, NTRIPLES.GZ, JSONLD, RDFXML, etc.
SET FORMAT=TURTLE_PRETTY
SET CONVERSION_OPTION=Standard

bin/export-ifc2rdf.bat -lcf config/log4j.xml -cf config/ifc2rdf-config.xml -isf %INPUT_SCHEMA_FILE_PATH% -imf %INPUT_MODEL_FILE_PATH%  -omf %OUTPUT_MODEL_FILE_PATH% -off %FORMAT% -oln %CONVERSION_OPTION%
pause
