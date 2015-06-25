package fi.hut.cs.drumbeat.ifc.convert.ifc2ld;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import fi.hut.cs.drumbeat.common.config.document.ConfigurationDocument;
import fi.hut.cs.drumbeat.common.config.document.ConfigurationParserException;
import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;
import fi.hut.cs.drumbeat.ifc.data.LogicalEnum;
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
		
		public static final Resource BOOLEAN = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + IfcVocabulary.TypeNames.BOOLEAN);
		public static final Resource LOGICAL = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + IfcVocabulary.TypeNames.LOGICAL);
		public static final Resource TRUE = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + LogicalEnum.TRUE);
		public static final Resource FALSE = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + LogicalEnum.FALSE);
		public static final Resource UNKNOWN = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + LogicalEnum.UNKNOWN);
		
		public static final Resource Array = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "Array");		
		public static final Resource Bag = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "Bag");		
		public static final Resource Collection = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "Collection");
		public static final Resource Defined = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "Defined");		
		public static final Resource Entity = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "Entity");		
		public static final Resource EntityProperty = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "EntityProperty");
		public static final Resource Enum = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "Enum");		
		public static final Resource InverseEntityProperty = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "InverseEntityProperty");
		public static final Resource List = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "List");		
		public static final Resource Select = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "Select");		
		public static final Resource Set = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "Set");		
		public static final Resource Slot = RdfVocabulary.DEFAULT_MODEL.createResource(getBaseUri() + "Slot");

		public static final Property endIndex = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "endIndex");
		public static final Property index = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "index");
		public static final Property isOrdered = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "isOrdered");
		public static final Property item = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "item");
		public static final Property itemType = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "itemType");
//		public static final Property hasBinary = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "hasBinary");
//		public static final Property hasBoolean = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "hasBoolean");
//		public static final Property hasLogical = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "hasLogical");
//		public static final Property hasInteger = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "hasInteger");
//		public static final Property hasNumber = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "hasNumber");
//		public static final Property hasReal = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "hasReal");
//		public static final Property hasString = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "hasString");
		public static final Property hasValue = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "hasValue");
		public static final Property next = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "next");
		public static final Property previous = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "previous");
//		public static final Property propertyIndex = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "propertyIndex");
		public static final Property slot = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "slot");
		public static final Property size = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "size");
		public static final Property startIndex = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "startIndex");
//		public static final Property value = RdfVocabulary.DEFAULT_MODEL.createProperty(getBaseUri() + "value");
		
		public static Resource getCollectionClass(IfcCollectionKindEnum collectionKind) {
			if (collectionKind == IfcCollectionKindEnum.List) {
				return Ifc2RdfVocabulary.EXPRESS.List;				
			} else if (collectionKind == IfcCollectionKindEnum.Set) {
				return Ifc2RdfVocabulary.EXPRESS.Set;				
			} else if (collectionKind == IfcCollectionKindEnum.Array) {
				return Ifc2RdfVocabulary.EXPRESS.Array;				
			} else {
				return Ifc2RdfVocabulary.EXPRESS.Bag;				
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
