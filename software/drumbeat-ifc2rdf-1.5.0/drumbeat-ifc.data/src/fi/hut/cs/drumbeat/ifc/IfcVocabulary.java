package fi.hut.cs.drumbeat.ifc;

import fi.hut.cs.drumbeat.common.string.StringUtils;

public class IfcVocabulary {
	
	public static class ExpressFormat {	
	
		public static final String FILE_EXTENSION = "exp";	

		public static final String SCHEMA = "SCHEMA"; 
		public static final String END_SCHEMA = "END_SCHEMA"; 
		public static final String TYPE = "TYPE"; 
		public static final String END_TYPE = "END_TYPE"; 
		public static final String ENTITY = "ENTITY"; 
		public static final String END_ENTITY = "END_ENTITY";
		public static final String FUNCTION = "FUNCTION";
		public static final String END_FUNCTION = "END_FUNCTION";
	
		public static final String DERIVE = "DERIVE";
		public static final String WHERE = "WHERE"; 
		public static final String SUBTYPE = "SUBTYPE"; 
		public static final String SUPERTYPE = "SUPERTYPE"; 
		public static final String OF = "OF"; 
		public static final String FOR = "FOR";
		public static final String INVERSE = "INVERSE";
		public static final String ABSTRACT = "ABSTRACT";
		public static final String UNIQUE = "UNIQUE";
		public static final String OPTIONAL = "OPTIONAL";
		public static final String EQUAL = StringUtils.EQUAL;
		public static final String REAL = "REAL";
		public static final String STRING = "STRING";
		public static final String INTEGER = "INTEGER";
		public static final String LIST = "LIST";
		public static final String SET = "SET";
		public static final String ARRAY = "ARRAY";
		public static final String SELECT = "SELECT";
		public static final String ENUMERATION = "ENUMERATION";
	
		public static final String FILE_SCHEMA = "FILE_SCHEMA";
		public static final String IFCPROJECT = "IFCPROJECT";
		public static final String IFCTIMESTAMP = "IfcTimeStamp";	
		
		public static final char LINE_NUMBER_SYMBOL = StringUtils.SHARP_CHAR;
		public static final char STRING_VALUE_SYMBOL = StringUtils.APOSTROPHE_CHAR;
		public static final char ENUMERATION_VALUE_SYMBOL = StringUtils.DOT_CHAR; 
		public static final char NULL_SYMBOL = StringUtils.DOLLAR_CHAR;
		public static final char ANY_SYMBOL = StringUtils.STAR_CHAR;
		
		public static final String LINE_NUMBER = StringUtils.SHARP;	
		public static final String NULL = StringUtils.DOLLAR;
		public static final String ANY = StringUtils.STAR;
		public static final String UNBOUNDED = StringUtils.QUESTION;		
		
	}
	
	public static class TypeNames {
		
		public static final String IFC_ROOT = "IfcRoot";
		public static final String IFC_PROJECT = "IfcProject";
		public static final String IFC_OBJECT = "IfcObject";
		public static final String IFC_OBJECT_DEFINITION = "IfcObjectDefinition";
		public static final String IFC_PRODUCT = "IfcProduct";
		public static final String IFC_ELEMENT = "IfcElement";
		public static final String IFC_SPACIAL_STRUCTURAL_ELEMENT = "IfcSpacialStructuralElement";
		public static final String IFC_RELATIONSHIP = "IfcRelationship";
		public static final String IFC_PROPERTY_DEFINITION = "IfcPropertydefinition";
		
		public static final String REAL = "REAL";
		public static final String NUMBER = "NUMBER";
		public static final String INTEGER = "INTEGER";
		public static final String BINARY = "BINARY";
		public static final String BINARY32 = "BINARY32";
		public static final String GUID = "STRING22";
		public static final String STRING = "STRING";
		public static final String STRING255 = "STRING255";
		public static final String LOGICAL = "LOGICAL";
		public static final String BOOLEAN = "BOOLEAN";
		public static final String DATETIME = "DATETIME";		
		
	}
	
	public static class AttributeNames {
		
		public static final String GLOBAL_ID = "globalId";
		
	}
	
}
