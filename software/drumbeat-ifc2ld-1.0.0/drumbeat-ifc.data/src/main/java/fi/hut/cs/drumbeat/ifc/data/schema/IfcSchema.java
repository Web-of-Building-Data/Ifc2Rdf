package fi.hut.cs.drumbeat.ifc.data.schema;

import java.io.Serializable;
import java.util.*;

import fi.hut.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;
import fi.hut.cs.drumbeat.ifc.data.LogicalEnum;


/**
 * Represents an IFC-EXPRESS schema.
 * 
 * @author vuhoan1
 *
 */
public class IfcSchema implements Serializable {
	
	/**
	 * Predefined schema entity types
	 *
	 */
	public final IfcEntityTypeInfo IFC_ELEMENT = new IfcEntityTypeInfo(this, IfcVocabulary.TypeNames.IFC_ELEMENT);
	public final IfcEntityTypeInfo IFC_OBJECT = new IfcEntityTypeInfo(this, IfcVocabulary.TypeNames.IFC_OBJECT);
	public final IfcEntityTypeInfo IFC_OBJECT_DEFINITION = new IfcEntityTypeInfo(this, IfcVocabulary.TypeNames.IFC_OBJECT_DEFINITION);
	public final IfcEntityTypeInfo IFC_PRODUCT = new IfcEntityTypeInfo(this, IfcVocabulary.TypeNames.IFC_PRODUCT);
	public final IfcEntityTypeInfo IFC_PROJECT = new IfcEntityTypeInfo(this, IfcVocabulary.TypeNames.IFC_PROJECT);
	public final IfcEntityTypeInfo IFC_PROPERTY_DEFINITION = new IfcEntityTypeInfo(this, IfcVocabulary.TypeNames.IFC_PROPERTY_DEFINITION);
	public final IfcEntityTypeInfo IFC_RELATIONSHIP = new IfcEntityTypeInfo(this, IfcVocabulary.TypeNames.IFC_RELATIONSHIP);
	public final IfcEntityTypeInfo IFC_ROOT = new IfcEntityTypeInfo(this, IfcVocabulary.TypeNames.IFC_ROOT);	
	public final IfcEntityTypeInfo IFC_SPACIAL_STRUCTURAL_ELEMENT = new IfcEntityTypeInfo(this, IfcVocabulary.TypeNames.IFC_SPACIAL_STRUCTURAL_ELEMENT);
	
	/**
	 * Predefined literal types
	 */
	public final IfcLiteralTypeInfo BINARY = new IfcLiteralTypeInfo(this, IfcVocabulary.TypeNames.BINARY, IfcTypeEnum.INTEGER);
//	public final IfcLiteralTypeInfo BINARY32 = new IfcLiteralTypeInfo(this, IfcVocabulary.TypeNames.BINARY32, IfcTypeEnum.INTEGER);
//	public final IfcLiteralTypeInfo BOOLEAN = new IfcLiteralTypeInfo(this, IfcVocabulary.TypeNames.BOOLEAN, IfcTypeEnum.LOGICAL); // true or false
	public final IfcLiteralTypeInfo DATETIME = new IfcLiteralTypeInfo(this, IfcVocabulary.TypeNames.DATETIME, IfcTypeEnum.DATETIME);
//	public final IfcLiteralTypeInfo GUID = new IfcLiteralTypeInfo(this, IfcVocabulary.TypeNames.GUID, IfcTypeEnum.GUID);
	public final IfcLiteralTypeInfo INTEGER = new IfcLiteralTypeInfo(this, IfcVocabulary.TypeNames.INTEGER, IfcTypeEnum.INTEGER);
//	public final IfcLiteralTypeInfo LOGICAL = new IfcLiteralTypeInfo(this, IfcVocabulary.TypeNames.LOGICAL, IfcTypeEnum.LOGICAL); // true, false or null
	public final IfcLiteralTypeInfo NUMBER = new IfcLiteralTypeInfo(this, IfcVocabulary.TypeNames.NUMBER, IfcTypeEnum.NUMBER);
	public final IfcLiteralTypeInfo REAL = new IfcLiteralTypeInfo(this, IfcVocabulary.TypeNames.REAL, IfcTypeEnum.REAL);
	public final IfcLiteralTypeInfo STRING = new IfcLiteralTypeInfo(this, IfcVocabulary.TypeNames.STRING, IfcTypeEnum.STRING);
//	public final IfcLiteralTypeInfo STRING255 = new IfcLiteralTypeInfo(this, IfcVocabulary.TypeNames.STRING255, IfcTypeEnum.STRING);
	
	public final IfcDefinedTypeInfo IFC_BOOLEAN = new IfcDefinedTypeInfo(this, IfcVocabulary.TypeNames.IFC_BOOLEAN, IfcVocabulary.TypeNames.BOOLEAN);
	public final IfcDefinedTypeInfo IFC_INTEGER = new IfcDefinedTypeInfo(this, IfcVocabulary.TypeNames.IFC_INTEGER, IfcVocabulary.TypeNames.INTEGER);
	public final IfcDefinedTypeInfo IFC_LOGICAL = new IfcDefinedTypeInfo(this, IfcVocabulary.TypeNames.IFC_LOGICAL, IfcVocabulary.TypeNames.LOGICAL);
	public final IfcDefinedTypeInfo IFC_REAL = new IfcDefinedTypeInfo(this, IfcVocabulary.TypeNames.IFC_REAL, IfcVocabulary.TypeNames.REAL);
	public final IfcDefinedTypeInfo IFC_TIME_STAMP = new IfcDefinedTypeInfo(this, IfcVocabulary.TypeNames.IFC_TIME_STAMP, IfcVocabulary.TypeNames.DATETIME);
	public final IfcDefinedTypeInfo IFC_TEXT = new IfcDefinedTypeInfo(this, IfcVocabulary.TypeNames.IFC_TEXT, IfcVocabulary.TypeNames.STRING);
	
	public final IfcLogicalTypeInfo BOOLEAN = new IfcLogicalTypeInfo(this, IfcVocabulary.TypeNames.BOOLEAN, Arrays.asList(LogicalEnum.TRUE, LogicalEnum.FALSE));
	public final IfcLogicalTypeInfo LOGICAL = new IfcLogicalTypeInfo(this, IfcVocabulary.TypeNames.LOGICAL, Arrays.asList(LogicalEnum.TRUE, LogicalEnum.FALSE, LogicalEnum.UNKNOWN));
	
	
	private static final long serialVersionUID = 1L;
	
	private String version;
	private Map<String, IfcTypeInfo> allTypeInfoDictionary = new HashMap<String, IfcTypeInfo>();
	private Map<String, IfcNonEntityTypeInfo> nonEntityTypeInfoDictionary = new HashMap<>();
	private Map<String, IfcEntityTypeInfo> entityTypeInfoDictionary = new HashMap<>();
	
	/**
	 * Initializes a new {@link IfcSchema}.
	 * 
	 * @param schemaVersion - the version of the schema.
	 */
	public IfcSchema(String schemaVersion) {
		this.version = schemaVersion;

		//
		// add some predefined types
		//
		addEntityTypeInfo(IFC_ELEMENT);
		addEntityTypeInfo(IFC_OBJECT);
		addEntityTypeInfo(IFC_OBJECT_DEFINITION);
		addEntityTypeInfo(IFC_PRODUCT);
		addEntityTypeInfo(IFC_PROJECT);
		addEntityTypeInfo(IFC_PROPERTY_DEFINITION);
		addEntityTypeInfo(IFC_RELATIONSHIP);
		addEntityTypeInfo(IFC_ROOT);
		addEntityTypeInfo(IFC_SPACIAL_STRUCTURAL_ELEMENT);
		
		addNonEntityTypeInfo(BINARY);
//		addNonEntityTypeInfo(BINARY32);
		addNonEntityTypeInfo(DATETIME);
//		addNonEntityTypeInfo(GUID);
		addNonEntityTypeInfo(INTEGER);
		addNonEntityTypeInfo(NUMBER);
		addNonEntityTypeInfo(REAL);
		addNonEntityTypeInfo(STRING);
//		addNonEntityTypeInfo(STRING255);
		
		addNonEntityTypeInfo(IFC_BOOLEAN);
		addNonEntityTypeInfo(IFC_INTEGER);
		addNonEntityTypeInfo(IFC_LOGICAL);
		addNonEntityTypeInfo(IFC_REAL);
		addNonEntityTypeInfo(IFC_TEXT);
		addNonEntityTypeInfo(IFC_TIME_STAMP);
		
		addNonEntityTypeInfo(BOOLEAN); // true or false
		addNonEntityTypeInfo(LOGICAL); // true, false or null
	}
	
	
	
	/**
	 * Gets the version string of {@link IfcSchema} 
	 * 
	 * @return the version string of the schema 
	 */
	public String getVersion() {
		return version;
	}
	
	
	/**
	 * Gets a type (entity or non-entity) by its name 
	 * 
	 * @param typeName the type name
	 * @return an instance of {@link IfcTypeInfo} with the specified name
	 * @throws IfcNotFoundException
	 */
	public IfcTypeInfo getTypeInfo(String typeName) throws IfcNotFoundException {
		String upperTypeName = typeName.toUpperCase();
		IfcTypeInfo result = allTypeInfoDictionary.get(upperTypeName);
		if (result == null) {
			throw new IfcNotFoundException(String.format("Type not found: '%s'", typeName));
		}
		return result;
	}

	/**
	 * Gets an entity type by its name
	 * 
	 * @param typeName the type name
	 * @return an instance of {@link IfcTypeInfo} with the specified name
	 * @throws IfcNotFoundException
	 */
	public IfcEntityTypeInfo getEntityTypeInfo(String typeName) throws IfcNotFoundException {
		String upperTypeName = typeName.toUpperCase();
		IfcEntityTypeInfo result = entityTypeInfoDictionary.get(upperTypeName);
		if (result == null) {
			throw new IfcNotFoundException(String.format("Entity type not found: '%s'", typeName));
		}
		return result;
	}
	
	/**
	 * Gets a non-entity type by its name
	 * 
	 * @param typeName the type name
	 * @return an instance of {@link IfcTypeInfo} with the specified name
	 * @throws IfcNotFoundException
	 */
	public IfcNonEntityTypeInfo getNonEntityTypeInfo(String typeName) throws IfcNotFoundException {
		String upperCaseTypeName = typeName.toUpperCase();
		IfcNonEntityTypeInfo result = nonEntityTypeInfoDictionary.get(upperCaseTypeName);
		if (result == null) {
			throw new IfcNotFoundException(String.format("Non-entity type not found: '%s'", typeName));
		}
		return result;
	}
	
	public IfcDefinedTypeInfo getEquivalentDefinedType(IfcLiteralTypeInfo literalTypeInfo) throws IfcNotFoundException {
		if (literalTypeInfo.equals(INTEGER)) {
			return IFC_INTEGER;			
		} else if (literalTypeInfo.equals(REAL)) {
			return IFC_REAL;
		} else if (literalTypeInfo.equals(STRING)) {
			return IFC_TEXT;
		} else if (literalTypeInfo.equals(BOOLEAN)) {
			return IFC_BOOLEAN;
		} else if (literalTypeInfo.equals(LOGICAL)) {
			return IFC_LOGICAL;
		} else {
			throw new IfcNotFoundException(String.format("Equivalent defined type for %s is not found", literalTypeInfo));
		}
	}
	
	public void addNonEntityTypeInfo(IfcNonEntityTypeInfo typeInfo) {
		String upperCaseTypeName = typeInfo.getName().toUpperCase();
		allTypeInfoDictionary.put(upperCaseTypeName, typeInfo);
		nonEntityTypeInfoDictionary.put(upperCaseTypeName, typeInfo);
	}
	
	public void addEntityTypeInfo(IfcEntityTypeInfo typeInfo) {
		String upperCaseTypeName = typeInfo.getName().toUpperCase();
		allTypeInfoDictionary.put(upperCaseTypeName, typeInfo);
		entityTypeInfoDictionary.put(upperCaseTypeName, typeInfo);
	}
	
	public Collection<IfcTypeInfo> getAllTypeInfos() {
		return allTypeInfoDictionary.values();
	}
	
	public Collection<IfcNonEntityTypeInfo> getNonEntityTypeInfos() {
		// Note: There are 336 non-entity types in IFC2X3
		return nonEntityTypeInfoDictionary.values();
	}

	public Collection<IfcEntityTypeInfo> getEntityTypeInfos() {	
		// Note: There are 654 entity types in IFC2X3
		return entityTypeInfoDictionary.values();
	}
	
}
