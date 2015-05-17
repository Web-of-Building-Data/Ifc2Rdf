package fi.hut.cs.drumbeat.ifc.convert.ifc2ld;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import fi.hut.cs.drumbeat.common.config.document.ConfigurationDocument;
import fi.hut.cs.drumbeat.common.config.document.ConfigurationParserException;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcCollectionKindEnum;
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
		
		public static final String BASE_PREFIX = "ifc";

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

		public static final Property index = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "index");
		public static final Property item = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "item");
		public static final Property itemType = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "itemType");
		public static final Property slot = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "slot");
		public static final Property size = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "size");
		
		public static Resource getCollectionClass(IfcCollectionKindEnum collectionKind) {
			if (collectionKind == IfcCollectionKindEnum.List) {
				return Ifc2RdfVocabulary.EXPRESS.ListClass;				
			} else if (collectionKind == IfcCollectionKindEnum.Set) {
				return Ifc2RdfVocabulary.EXPRESS.SetClass;				
			} else if (collectionKind == IfcCollectionKindEnum.Array) {
				return Ifc2RdfVocabulary.EXPRESS.ArrayClass;				
			} else {
				return Ifc2RdfVocabulary.EXPRESS.BagClass;				
			}	
			
		}
		
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
		
		public static final Property fileDescription = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "fileDescription");		
		public static final Property fileName = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "fileName");		
		public static final Property fileSchema = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "fileSchema");
		
		public static class FileDescription {
			public static final Resource FileDescriptionClass = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "FileDescription");
			public static final Property description = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "description");
			public static final Property implementation_level = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "implementation_level");
		}
		
		public static class FileName {
			public static final Resource FileNameClass = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "FileName");
			public static final Property name = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "name");
			public static final Property time_stamp = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "time_stamp");
			public static final Property author = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "author");
			public static final Property organization = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "organization");
			public static final Property preprocessor_version = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "preprocessor_version");
			public static final Property originating_system = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "originating_system");
			public static final Property authorization = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "authorization");
		}
		
		public static class FileSchema {
			public static final Resource FileSchemaClass = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "FileSchema");
			public static final Property schema_identifiers = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "schema_identifiers");
		}
	}

}
