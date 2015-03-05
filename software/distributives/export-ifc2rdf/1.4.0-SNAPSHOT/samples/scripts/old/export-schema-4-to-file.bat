@echo off

cd ../..

bin/export-ifc2rdf.bat -lcf config\log4j.xml -cf config\IfcAnalysis.xml -isf resources\IFC4.exp -osf output\IFC4.rdf -ofl N3

pause