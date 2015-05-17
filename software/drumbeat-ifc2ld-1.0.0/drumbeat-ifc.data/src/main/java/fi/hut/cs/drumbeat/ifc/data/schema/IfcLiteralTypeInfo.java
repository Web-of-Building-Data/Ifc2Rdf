package fi.hut.cs.drumbeat.ifc.data.schema;

import java.util.EnumSet;


public class IfcLiteralTypeInfo extends IfcNonEntityTypeInfo {
	
	private static final long serialVersionUID = 1L;
	
	private IfcTypeEnum valueType;

	public IfcLiteralTypeInfo(IfcSchema schema, String name, IfcTypeEnum valueType) {
		super(schema, name);
		this.valueType = valueType;
	}

	@Override
	public boolean isCollectionType() {
		return false;
	}

//	@Override
//	public boolean isEntityOrSelectType() {
//		return false;
//	}
	
	
	public IfcTypeEnum getValueType() {
		return valueType;
	}
	
	/**
	 * Gets the basic type of literal values involved inside the type (if there is only one) 
	 * @return itself
	 */
	public EnumSet<IfcTypeEnum> getValueTypes() {
		return EnumSet.of(valueType);
	}
	
	@Override
	public String getShortDescription(String typeNameFormat) {
		return String.format(typeNameFormat, getName());
	}
	

	@Override
	public boolean isLiteralContainerType() {
		return true;
	}

}
