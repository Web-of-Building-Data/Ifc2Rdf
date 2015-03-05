package net.linkedbuildingdata.ifc.data.schema;

import java.util.EnumSet;

import net.linkedbuildingdata.ifc.IfcNotFoundException;
import net.linkedbuildingdata.ifc.IfcVocabulary;
import net.linkedbuildingdata.ifc.data.Cardinality;


public abstract class IfcCollectionTypeInfo extends IfcDefinedTypeInfo {
	
	private static final long serialVersionUID = 1L;
	
	private String itemTypeInfoName;
	private IfcTypeInfo itemTypeInfo;
	private boolean itemsAreUnique;
	private Cardinality cardinality;
	
	public IfcCollectionTypeInfo(IfcSchema schema, String typeName, IfcTypeInfo itemTypeInfo) {
		super(schema, typeName);
		this.itemTypeInfo = itemTypeInfo;
		this.itemTypeInfoName = itemTypeInfo.getName();
	}

	public IfcCollectionTypeInfo(IfcSchema schema, String typeName, String itemTypeInfoName) {
		super(schema, typeName);
		this.itemTypeInfoName = itemTypeInfoName;
	}
	
	abstract public boolean isSorted();
	
	/**
	 * @return the itemsAreUnique
	 */
	public boolean itemsAreUnique() {
		return itemsAreUnique;
	}

	/**
	 * @param itemsAreUnique the itemsAreUnique to set
	 */
	public void setItemsUnique(boolean itemsAreUnique) {
		this.itemsAreUnique = itemsAreUnique;
	}
	
	public IfcTypeInfo getItemTypeInfo() {
		if (itemTypeInfo == null) {			
			try {
				itemTypeInfo = getSchema().getTypeInfo(itemTypeInfoName);
			} catch (IfcNotFoundException e) {
				throw new RuntimeException(e.getMessage(), e);
			}			
		}
		return itemTypeInfo;
	}

	@Override
	public boolean isCollectionType() {
		return true;
	}

	@Override
	public boolean isEntityOrSelectType() {
		return getItemTypeInfo().isEntityOrSelectType();
	}

	/**
	 * @return the cardinality
	 */
	public Cardinality getCardinality() {
		return cardinality;
	}

	/**
	 * @param cardinality the cardinality to set
	 */
	public void setCardinality(Cardinality cardinality) {
		this.cardinality = cardinality;
	}
	
	/**
	 * Gets the basic type of literal values involved inside the type (if there is only one) 
	 * @return 
	 * @throws IfcValueTypeConflictException 
	 */
	public EnumSet<IfcTypeEnum> getValueTypes() {
		return getItemTypeInfo().getValueTypes();	
	}		
	
	@Override
	public String getShortDescription(String typeNameFormat) {
		String prefix = isSorted() ? IfcVocabulary.ExpressFormat.LIST : IfcVocabulary.ExpressFormat.SET;
		
		return String.format("%s [%s,%s] %s " + typeNameFormat,
				prefix,
				cardinality.getMinType(),
				cardinality.getMaxType(),
				IfcVocabulary.ExpressFormat.OF,itemTypeInfo.getName());		
	}
	
}
