package net.linkedbuildingdata.ifc.data.schema;

import java.util.EnumSet;

import net.linkedbuildingdata.ifc.IfcNotFoundException;


public class IfcRedirectTypeInfo extends IfcDefinedTypeInfo {
	
	private static final long serialVersionUID = 1L;
	
	private IfcTypeInfo redirectTypeInfo;
	private String redirectTypeInfoName;

	public IfcRedirectTypeInfo(IfcSchema schema, String name, String redirectTypeInfoName) {
		super(schema, name);
		this.redirectTypeInfoName = redirectTypeInfoName;
	}
	
	/**
	 * @return the redirectTypeInfoName
	 */
	public String getRedirectTypeInfoName() {
		return redirectTypeInfoName;
	}
	
	/**
	 * @return the redirectTypeInfo
	 * @throws IfcNotFoundException 
	 */
	public IfcTypeInfo getRedirectTypeInfo() {
		if (redirectTypeInfo == null) {
			try {
				redirectTypeInfo = getSchema().getTypeInfo(redirectTypeInfoName);
			} catch (IfcNotFoundException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		return redirectTypeInfo;
	}	

	@Override
	public boolean isCollectionType() {
		return getRedirectTypeInfo().isCollectionType();
	}

	@Override
	public boolean isEntityOrSelectType() {
		return getRedirectTypeInfo().isEntityOrSelectType();
	}
	
	/**
	 * Gets the basic type of literal values involved inside the type (if there is only one) 
	 * @return Literal value type of the internal literal value
	 * @throws IfcValueTypeConflictException 
	 */
	@Override
	public EnumSet<IfcTypeEnum> getValueTypes() {
		return getRedirectTypeInfo().getValueTypes();	
	}	
	
	
	@Override
	public String getShortDescription(String typeNameFormat) {
		return String.format(typeNameFormat, getRedirectTypeInfoName());		
	}

	@Override
	public boolean isLiteralContainerType() {
		return getRedirectTypeInfo().isLiteralContainerType();
	}	
	
}
