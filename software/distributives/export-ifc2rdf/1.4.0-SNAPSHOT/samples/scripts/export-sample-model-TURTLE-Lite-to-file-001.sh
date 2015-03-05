#! /bin/sh

cd ../..

bash bin/export-ifc2rdf.sh -lcf config/log4j.xml -cf config/IfcAnalysis.xml -isf resources/IFC2X3_TC1.exp -imf resources/sample.ifc -omf output/sample.rdf -off TURTLE -oln Lite