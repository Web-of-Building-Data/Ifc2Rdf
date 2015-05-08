package fi.hut.cs.drumbeat.ifc.common;

import fi.hut.cs.drumbeat.common.string.RegexUtils;

public class IfcHelper {
	
	public static String getFormattedTypeName(String typeName) {
		return RegexUtils.removeNonSafeUrlSymbols(typeName);		
	}

	public static String getFormattedAttributeName(String attributeName) {
		return attributeName.substring(0, 1).toLowerCase() + attributeName.substring(1);
	}
	
}
