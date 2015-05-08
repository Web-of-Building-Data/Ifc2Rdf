package fi.hut.cs.drumbeat.ifc.convert.ifc2ld;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

import fi.hut.cs.drumbeat.common.config.document.ConfigurationDocument;
import fi.hut.cs.drumbeat.common.config.document.ConfigurationParserException;
import fi.hut.cs.drumbeat.rdf.RdfVocabulary;

/**
 * A vocabulary that contains keywords used in the IFC ontology  
 * 
 * @author vuhoan1
 *
 */
public class Ifc2RdfVocabulary {
	
	public static final String DEFAULT_ONTOLOGY_BASE = "http://drumbeat.cs.hut.fi/owl/";
	
	public static final String DEFAULT_ONTOLOGY_PREFIX = "ifc:";
	public static final String DEFAULT_ONTOLOGY_NAMESPACE_FORMAT = DEFAULT_ONTOLOGY_BASE + "%s#";	
	public static final String DEFAULT_MODEL_PREFIX = ":";
	public static final String DEFAULT_MODEL_NAMESPACE_FORMAT = "http://drumbeat.cs.hut.fi/model/";

	public static class IFC {
		
		public static final String EMPTY_LIST = "EMPTY_LIST";		
		
		public static final String BLANK_NODE_ENTITY_URI_FORMAT = "_LINE_%d";
		
		public static final String PROPERTY_RAW_NAME = "rawName";
		public static final String PROPERTY_DEBUG_MESSAGE = "debugMessage";
		public static final String PROPERTY_LINE_NUMBER_PROPERTY = "lineNumber";

		public static final String TYPE_LITERAL_VALUE_CONTAINER_ENTITY = "LiteralValueContainer";
		public static final String TYPE_SUPER_ENTITY = "SuperEntity";
		
	}
	
	public static class EXPRESS {
		
		private static String baseUri;

		
		public static final String BASE_PREFIX = "expr";
		
		public static final String getBaseUri() {
			if (baseUri == null) {
				Properties properties;
				try {
					properties = ConfigurationDocument.getInstance().getProperties();
				} catch (ConfigurationParserException e) {
					Logger.getRootLogger().error("Error properties section in the configuration file", e);
					return null;
				}
				baseUri = properties.getProperty("express.baseuri", DEFAULT_ONTOLOGY_BASE + "EXPRESS#");
			}
			return baseUri;
		}
		
		public static final Resource DefinedClass = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "DefinedClass");		
		public static final Resource EnumerationClass = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "EnumerationClass");		
		public static final Resource SelectClass = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "SelectClass");		
		public static final Resource EntityClass = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "EntityClass");		
		public static final Resource CollectionClass = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "CollectionClass");
		public static final Resource ArrayClass = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "DefinedClass");		
		public static final Resource ListClass = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "ListClass");		
		public static final Resource SetClass = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "SetClass");		
		public static final Resource BagClass = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "BagClass");		
		public static final Resource CollectionSlotClass = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "CollectionSlotClass");

		public static final Property item = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "item");
		public static final Property slot = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "slot");
		
//		public static final String CLASS_DEFINED = "DefinedClass";		
//		public static final String CLASS_ENUMERATION = "EnumerationClass";
//		public static final String CLASS_SELECT = "SelectClass";
//		public static final String CLASS_ENTITY = "EntityClass";	
//		
//		public static final String CLASS_COLLECTION = "CollectionClass";
//		public static final String CLASS_ARRAY = "ArrayClass";
//		public static final String CLASS_LIST = "ListClass";
//		public static final String CLASS_SET = "SetClass";
//		public static final String CLASS_BAG = "BagClass";
		
	}
	
	public static class STEP {
		
		public static final String BASE_PREFIX = "step";

		private static String baseUri; 
		
		public static final String getBaseUri() {
			if (baseUri == null) {
				Properties properties;
				try {
					properties = ConfigurationDocument.getInstance().getProperties();
				} catch (ConfigurationParserException e) {
					Logger.getRootLogger().error("Error properties section in the configuration file", e);
					return null;
				}
				baseUri = properties.getProperty("step.baseuri", DEFAULT_ONTOLOGY_BASE + "STEP#");
			}
			return baseUri;
		}
		
		
		
		
	}

}
