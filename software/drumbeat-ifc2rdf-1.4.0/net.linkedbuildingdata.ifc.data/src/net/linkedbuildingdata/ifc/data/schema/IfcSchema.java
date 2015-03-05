package net.linkedbuildingdata.ifc.data.schema;

import java.io.Serializable;
import java.util.*;

import net.linkedbuildingdata.ifc.IfcNotFoundException;


/**
 * Encapsulates all information about a schema.
 * 
 * @author vuhoan1
 *
 */
public class IfcSchema implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String version;
	private Map<String, IfcTypeInfo> allTypeInfoDictionary = new HashMap<String, IfcTypeInfo>();
	private Map<String, IfcDefinedTypeInfo> definedTypeInfoDictionary = new HashMap<String, IfcDefinedTypeInfo>();
	private Map<String, IfcEntityTypeInfo> entityTypeInfoDictionary = new HashMap<String, IfcEntityTypeInfo>();
	
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
		IfcLiteralTypeInfo.addPredefinedTypesToSchema(this);
		IfcEntityTypeInfo.addPredefinedTypesToSchema(this);
	}
	
	public String getVersion() {
		return version;
	}
	
	public IfcTypeInfo getTypeInfo(String typeName) throws IfcNotFoundException {
		String upperTypeName = typeName.toUpperCase();
		IfcTypeInfo result = allTypeInfoDictionary.get(upperTypeName);
		if (result == null) {
			throw new IfcNotFoundException(String.format("Type not found: '%s'", typeName));
		}
		return result;
	}

	public IfcEntityTypeInfo getEntityTypeInfo(String typeName) throws IfcNotFoundException {
		String upperTypeName = typeName.toUpperCase();
		IfcEntityTypeInfo result = entityTypeInfoDictionary.get(upperTypeName);
		if (result == null) {
			throw new IfcNotFoundException(String.format("Entity type not found: '%s'", typeName));
		}
		return result;
	}
	
	public IfcDefinedTypeInfo getDefinedTypeInfo(String typeName) throws IfcNotFoundException {
		String upperTypeName = typeName.toUpperCase();
		IfcDefinedTypeInfo result = definedTypeInfoDictionary.get(upperTypeName);
		if (result == null) {
			throw new IfcNotFoundException(String.format("Defined type not found: '%s'", typeName));
		}
		return result;
	}
	
	public void addDefinedTypeInfo(IfcDefinedTypeInfo typeInfo) {
		String upperTypeName = typeInfo.getName().toUpperCase();
		allTypeInfoDictionary.put(upperTypeName, typeInfo);
		definedTypeInfoDictionary.put(upperTypeName, typeInfo);
	}
	
	public void addEntityTypeInfo(IfcEntityTypeInfo typeInfo) {
		String upperTypeName = typeInfo.getName().toUpperCase();
		allTypeInfoDictionary.put(upperTypeName, typeInfo);
		entityTypeInfoDictionary.put(upperTypeName, typeInfo);
	}
	
	public Collection<IfcTypeInfo> getAllTypeInfos() {
		return allTypeInfoDictionary.values();
	}
	
	public Collection<IfcDefinedTypeInfo> getDefinedTypeInfos() {
		// Note: There are 336 non-entity types in IFC2X3
		return definedTypeInfoDictionary.values();
	}

	public Collection<IfcEntityTypeInfo> getEntityTypeInfos() {	
		// Note: There are 654 entity types in IFC2X3
		return entityTypeInfoDictionary.values();
	}
	
}
