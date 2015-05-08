@ECHO OFF

CALL maven-single.bat drumbeat-common

CALL maven-single.bat drumbeat-ifc.common

CALL maven-single.bat drumbeat-ifc.data

CALL maven-single.bat drumbeat-rdf

CALL maven-single.bat drumbeat-rdf.modelfactory

CALL maven-single.bat drumbeat-ifc.convert.stff2ifc

CALL maven-single.bat drumbeat-ifc.processing.grounding.ifc

CALL maven-single.bat drumbeat-ifc.convert.ifc2ld

CALL maven-single.bat drumbeat-ifc.convert.ifc2ld.cli




