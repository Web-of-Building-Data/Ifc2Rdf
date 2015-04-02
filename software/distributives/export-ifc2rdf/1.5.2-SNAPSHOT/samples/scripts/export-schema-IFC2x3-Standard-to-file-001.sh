#! /bin/sh

cd ../..

INPUT_SCHEMA_FILE_PATH=resources/IFC2X3_TC1.exp
OUTPUT_SCHEMA_FILE_PATH=output/IFC2X3_Standard

# sample formats: TURTLE, TURTLE.GZ, NQUADS, NQUADS.GZ, NTRIPLES, NTRIPLES.GZ, JSONLD, RDFXML, etc.
FORMAT=TURTLE_PRETTY
CONVERSION_OPTION=Standard

bash bin/export-ifc2rdf.sh -lcf config/log4j.xml -cf config/ifc2rdf-config.xml -isf $INPUT_SCHEMA_FILE_PATH -osf $OUTPUT_SCHEMA_FILE_PATH -off $FORMAT -oln $CONVERSION_OPTION
