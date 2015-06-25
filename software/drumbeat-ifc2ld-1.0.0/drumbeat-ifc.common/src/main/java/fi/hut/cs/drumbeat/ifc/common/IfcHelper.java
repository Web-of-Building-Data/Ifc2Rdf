package fi.hut.cs.drumbeat.ifc.common;

import fi.hut.cs.drumbeat.common.string.StringUtils;

public class IfcHelper {
	
	public static String getFormattedTypeName(String typeName) {
		int indexOfOpeningBracket = typeName.indexOf(StringUtils.OPENING_ROUND_BRACKET_CHAR); 
		return indexOfOpeningBracket < 0 ? typeName : typeName.substring(0, indexOfOpeningBracket);
		
//		return RegexUtils.removeNonSafeUrlSymbols(typeName);		
	}

	public static String getFormattedAttributeName(String attributeName) {
		return attributeName.substring(0, 1).toLowerCase() + attributeName.substring(1);
	}
	
}
