@echo off

cd ../..

bin/export-ifc2rdf.bat -lcf config\log4j.xml -cf config\IfcAnalysis.xml -isf resources\IFC2X3_TC1.exp -imf resources\sample.ifc -omf output\sample.rdf -ofl N3

pause