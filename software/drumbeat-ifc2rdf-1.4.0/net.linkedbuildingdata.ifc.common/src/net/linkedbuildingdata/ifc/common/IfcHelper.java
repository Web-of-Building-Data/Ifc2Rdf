package net.linkedbuildingdata.ifc.common;

import net.linkedbuildingdata.common.string.RegexUtils;

public class IfcHelper {
	
	public static String getFormattedTypeName(String typeName) {
		return RegexUtils.getHtmlSafeName(typeName);		
	}

	public static String getFormattedAttributeName(String attributeName) {
		return attributeName.substring(0, 1).toLowerCase() + attributeName.substring(1);
	}
	
}
