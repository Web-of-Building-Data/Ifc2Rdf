package fi.hut.cs.drumbeat.ifc.convert.ifc2ld;

public enum Ifc2RdfConversionOptionsEnum {
	IgnoreEntityTypes,
	IgnoreSelectTypes,
	IgnoreEnumerationTypes,
	IgnoreDefinedTypes,
	IgnoreCollectionTypes,
	ExportProperties,
	ExportInverseProperties,
	ExportPropertyCardinalities,
	ExportDebugInfo,
//	PrintPropertyDomainAndRange,		
//	AvoidDuplicationOfPropertyNames,
//	PrintPropertyDomainAndRangeAsUnion,
//	ForceConvertRdfListToOloOrderedList,
	ForceConvertEnumerationValuesToString,
	ForceConvertBooleanValuesToString,
	UseSpecificDoubleTypes,
//	ForceConvertPropertyToObjectProperty,
}
