package net.linkedbuildingdata.ifc.data.schema;

import java.util.EnumSet;

import net.linkedbuildingdata.ifc.IfcVocabulary;


public class IfcLiteralTypeInfo extends IfcDefinedTypeInfo {
	
	private static final long serialVersionUID = 1L;
	
	public static void addPredefinedTypesToSchema(IfcSchema schema) {
		schema.addDefinedTypeInfo(new IfcLiteralTypeInfo(schema, IfcVocabulary.TypeNames.REAL, IfcTypeEnum.REAL));
		schema.addDefinedTypeInfo(new IfcLiteralTypeInfo(schema, IfcVocabulary.TypeNames.NUMBER, IfcTypeEnum.REAL));
		schema.addDefinedTypeInfo(new IfcLiteralTypeInfo(schema, IfcVocabulary.TypeNames.INTEGER, IfcTypeEnum.INTEGER));
		schema.addDefinedTypeInfo(new IfcLiteralTypeInfo(schema, IfcVocabulary.TypeNames.BINARY, IfcTypeEnum.INTEGER));
		schema.addDefinedTypeInfo(new IfcLiteralTypeInfo(schema, IfcVocabulary.TypeNames.BINARY32, IfcTypeEnum.INTEGER));
		schema.addDefinedTypeInfo(new IfcLiteralTypeInfo(schema, IfcVocabulary.TypeNames.GUID, IfcTypeEnum.GUID));
		schema.addDefinedTypeInfo(new IfcLiteralTypeInfo(schema, IfcVocabulary.TypeNames.STRING, IfcTypeEnum.STRING));
		schema.addDefinedTypeInfo(new IfcLiteralTypeInfo(schema, IfcVocabulary.TypeNames.STRING255, IfcTypeEnum.STRING));
		schema.addDefinedTypeInfo(new IfcLiteralTypeInfo(schema, IfcVocabulary.TypeNames.LOGICAL, IfcTypeEnum.LOGICAL)); // true, false or null
		schema.addDefinedTypeInfo(new IfcLiteralTypeInfo(schema, IfcVocabulary.TypeNames.BOOLEAN, IfcTypeEnum.LOGICAL)); // true or false
		schema.addDefinedTypeInfo(new IfcLiteralTypeInfo(schema, IfcVocabulary.TypeNames.DATETIME, IfcTypeEnum.DATETIME));
	}	
	
	private IfcTypeEnum valueType;

	public IfcLiteralTypeInfo(IfcSchema schema, String name, IfcTypeEnum valueType) {
		super(schema, name);
		this.valueType = valueType;
	}

	@Override
	public boolean isCollectionType() {
		return false;
	}

	@Override
	public boolean isEntityOrSelectType() {
		return false;
	}
	
	
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
