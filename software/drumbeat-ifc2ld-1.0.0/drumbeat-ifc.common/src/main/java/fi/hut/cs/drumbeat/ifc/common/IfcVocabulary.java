package fi.hut.cs.drumbeat.ifc.common;

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
		public static final String BAG = "BAG";
		public static final String SELECT = "SELECT";
		public static final String ENUMERATION = "ENUMERATION";
		public static final String[] COLLECTION_HEADERS = new String[]{LIST, SET, ARRAY, BAG};
		
		public static final String UNBOUNDED = StringUtils.QUESTION;		
	}
	
	public static class StepFormat {
		public static final String FILE_EXTENSION = "ifc";	

		public static final String HEADER = "HEADER";
		public static final String ENDSEC = "ENDSEC";
		public static final String EQUAL = StringUtils.EQUAL;
		
		public static final String END_LINE = StringUtils.SEMICOLON;
		public static final String STRING_VALUE = StringUtils.APOSTROPHE;
		
		public static final char LINE_NUMBER_SYMBOL = StringUtils.SHARP_CHAR;
		public static final char STRING_VALUE_SYMBOL = StringUtils.APOSTROPHE_CHAR;
		public static final char ENUMERATION_VALUE_SYMBOL = StringUtils.DOT_CHAR; 
		public static final char NULL_SYMBOL = StringUtils.DOLLAR_CHAR;
		public static final char ANY_SYMBOL = StringUtils.STAR_CHAR;
		
		public static final String LINE_NUMBER = StringUtils.SHARP;	
		public static final String NULL = StringUtils.DOLLAR;
		public static final String ANY = StringUtils.STAR;
		
		public static final class Header {
			public static final String SCHEMA_STRING = 				
				"SCHEMA STEP;" +
				"TYPE schema_name = STRING; END_TYPE;" +
				"TYPE time_stamp_text = STRING; END_TYPE;" +
				"ENTITY file_description; description : LIST [1:?] OF STRING; implementation_level : STRING; END_ENTITY;" +
				"ENTITY file_name; name : STRING; time_stamp : time_stamp_text; author : LIST [ 1 : ? ] OF STRING; organization : LIST [ 1 : ? ] OF STRING; preprocessor_version : STRING; originating_system : STRING; authorization : STRING; END_ENTITY;" +
				"ENTITY file_schema; schema_identifiers : LIST [1:?] OF UNIQUE schema_name; END_ENTITY;" +
				"END_SCHEMA;";
			
			public static final class FileDescription {
				public static final String TYPE_NAME = "file_description"; 
				public static final String DESCRIPTION = "description";
				public static final String IMPLEMENTATION_LEVEL = "implementation_level";
			}
			
			public static final class FileName {
				public static final String TYPE_NAME = "file_name"; 
				public static final String NAME = "name";				
				public static final String TIME_STAMP = "time_stamp";				
				public static final String AUTHOR = "author";				
				public static final String ORGANIZATION = "organization";				
				public static final String PREPROCESSOR_VERSION = "preprocessor_version";				
				public static final String ORIGINATING_SYSTEM = "originating_system";				
				public static final String AUTHORIZATION = "authorization";				
			}
			
			public static final class FileSchema {
				public static final String TYPE_NAME = "file_schema"; 
				public static final String SCHEMA_IDENTIFIERS = "schema_identifiers";
			}
			
		}
		
//		public static final String HEADER_SCHEMA = 				
//		"SCHEMA STEP;" +
//		"TYPE schema_name = STRING(1024); END_TYPE;" +
//		"TYPE time_stamp_text = STRING(256); END_TYPE;" +
//		"ENTITY file_description; description : LIST [1:?] OF STRING(256); implementation_level : STRING(256); END_ENTITY;" +
//		"ENTITY file_name; name : STRING(256); time_stamp : time_stamp_text; author : LIST [ 1 : ? ] OF STRING(256); organization : LIST [ 1 : ? ] OF STRING(256); preprocessor_version : STRING(256); originating_system : STRING(256); authorization : STRING(256); END_ENTITY;" +
//		"ENTITY file_schema; schema_identifiers : LIST [1:?] OF UNIQUE schema_name; END_ENTITY;" +
//		"END_SCHEMA;";
		
	}
	
	public static class TypeNames {
		
		public static final String IFC_ELEMENT = "IfcElement";
		public static final String IFC_OBJECT = "IfcObject";
		public static final String IFC_OBJECT_DEFINITION = "IfcObjectDefinition";
		public static final String IFC_OWNER_HISTORY = "IfcOwnerHistory";
		public static final String IFC_PRODUCT = "IfcProduct";
		public static final String IFC_PROJECT = "IfcProject";
		public static final String IFC_ROOT = "IfcRoot";
		public static final String IFC_SPACIAL_STRUCTURAL_ELEMENT = "IfcSpacialStructuralElement";
		public static final String IFC_RELATIONSHIP = "IfcRelationship";
		public static final String IFC_PROPERTY_DEFINITION = "IfcPropertyDefinition";
		
		public static final String IFC_BOOLEAN = "IfcBoolean";
		public static final String IFC_INTEGER = "IfcInteger";
		public static final String IFC_LOGICAL = "IfcLogical";
		public static final String IFC_REAL = "IfcReal";
		public static final String IFC_TEXT = "IfcText";
		public static final String IFC_TIME_STAMP = "IfcTimeStamp";	
		
		public static final String BINARY = "BINARY";
//		public static final String BINARY32 = "BINARY32";
		public static final String BOOLEAN = "BOOLEAN";
		public static final String DATETIME = "DATETIME";		
		public static final String GUID = "STRING22";
		public static final String INTEGER = "INTEGER";
		public static final String LOGICAL = "LOGICAL";
		public static final String NUMBER = "NUMBER";
		public static final String REAL = "REAL";
		public static final String STRING = "STRING";
//		public static final String STRING255 = "STRING255";
		
	}
	
	public static class AttributeNames {
		
		public static final String GLOBAL_ID = "globalId";
		
	}
	
}
