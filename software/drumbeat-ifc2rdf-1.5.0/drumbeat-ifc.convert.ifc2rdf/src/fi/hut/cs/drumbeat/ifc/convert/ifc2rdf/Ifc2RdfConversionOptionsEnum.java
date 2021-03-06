package fi.hut.cs.drumbeat.ifc.convert.ifc2rdf;

public enum Ifc2RdfConversionOptionsEnum {
	PrintPropertyCardinality,
	PrintPropertyDomainAndRange,		
	AvoidDuplicationOfPropertyNames,
	PrintPropertyDomainAndRangeAsUnion,
	ForceConvertRdfListToOloOrderedList,
	ForceConvertEnumerationValuesToString,
	ForceConvertBooleanValuesToString,
	ForceConvertPropertyToObjectProperty,
	ExportDebugInfo,
	ExportSelectTypes,
	ExportEnumerationTypes,
	ExportProperties,
	ExportInverseProperties,
}
