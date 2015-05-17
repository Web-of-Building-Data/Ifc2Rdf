package fi.hut.cs.drumbeat.ifc.data.schema;

import java.util.EnumSet;

import fi.hut.cs.drumbeat.ifc.common.IfcNotFoundException;


public class IfcDefinedTypeInfo extends IfcNonEntityTypeInfo {
	
	private static final long serialVersionUID = 1L;
	
	private IfcTypeInfo superTypeInfo;
	private String superTypeInfoName;

	public IfcDefinedTypeInfo(IfcSchema schema, String name, String superTypeInfoName) {
		super(schema, name);
		this.superTypeInfoName = superTypeInfoName;
	}
	
	/**
	 * @return the superTypeInfoName
	 */
	public String getSuperTypeInfoName() {
		return superTypeInfoName;
	}
	
	/**
	 * @return the superTypeInfo
	 * @throws IfcNotFoundException 
	 */
	public IfcTypeInfo getSuperTypeInfo() {
		if (superTypeInfo == null) {
			try {
				superTypeInfo = getSchema().getTypeInfo(superTypeInfoName);
			} catch (IfcNotFoundException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		return superTypeInfo;
	}	

	@Override
	public boolean isCollectionType() {
		return getSuperTypeInfo().isCollectionType();
	}

//	@Override
//	public boolean isEntityOrSelectType() {
//		return getSuperTypeInfo().isEntityOrSelectType();
//	}
	
	/**
	 * Gets the basic type of literal values involved inside the type (if there is only one) 
	 * @return Literal value type of the internal literal value
	 * @throws IfcValueTypeConflictException 
	 */
	@Override
	public EnumSet<IfcTypeEnum> getValueTypes() {
		return getSuperTypeInfo().getValueTypes();	
	}	
	
	
	@Override
	public String getShortDescription(String typeNameFormat) {
		return String.format(typeNameFormat, getSuperTypeInfoName());		
	}

	@Override
	public boolean isLiteralContainerType() {
		return getSuperTypeInfo().isLiteralContainerType();
	}	
	
}
