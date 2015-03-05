package net.linkedbuildingdata.ifc.convert.ifc2rdf;

/**
 * A vocabulary that contains keywords used in the IFC ontology  
 * 
 * @author vuhoan1
 *
 */
public class Ifc2RdfVocabulary {
	
	public static final String DEFAULT_ONTOLOGY_PREFIX = "ifc:";
	public static final String DEFAULT_ONTOLOGY_NAMESPACE_FORMAT = "http://net.linkedbuildingdata.ifc/schema/%s#";	
	public static final String DEFAULT_MODEL_PREFIX = ":";
	public static final String DEFAULT_MODEL_NAMESPACE_FORMAT = "http://net.linkedbuildingdata.ifc/model/";

	public static class IFC {
		
		public static final String EMPTY_LIST = "EMPTY_LIST";		
		
		public static final String BLANK_NODE_ENTITY_URI_FORMAT = "_LINE_%d";
		
		public static final String PROPERTY_RAW_NAME = "rawName";
		public static final String PROPERTY_DEBUG_MESSAGE = "debugMessage";
		public static final String PROPERTY_LINE_NUMBER_PROPERTY = "lineNumber";

		public static final String TYPE_LITERAL_VALUE_CONTAINER_ENTITY = "LiteralValueContainer";
		public static final String TYPE_SUPER_ENTITY = "SuperEntity";
		
	}

}
