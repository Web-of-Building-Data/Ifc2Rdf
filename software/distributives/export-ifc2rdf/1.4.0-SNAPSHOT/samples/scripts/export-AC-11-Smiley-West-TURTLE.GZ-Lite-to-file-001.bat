@echo off

cd ../..

bin/export-ifc2rdf.bat -lcf config\log4j.xml -cf config\IfcAnalysis.xml -isf resources\IFC2X3_TC1.exp -imf "c:\DRUM\!git_local\DRUM\Test\Models\drumbeat.cs.hut.fi\AC-11-Smiley-West-04-07-2007.ifc"  -omf "c:\DRUM\!git_local\DRUM\Test\Models\drumbeat.cs.hut.fi\AC-11-Smiley-West-04-07-2007" -off TURTLE.GZ -oln Lite

pause