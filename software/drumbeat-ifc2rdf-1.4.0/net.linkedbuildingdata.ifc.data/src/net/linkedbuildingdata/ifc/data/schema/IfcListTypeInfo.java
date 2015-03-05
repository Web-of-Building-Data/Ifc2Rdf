package net.linkedbuildingdata.ifc.data.schema;

import net.linkedbuildingdata.ifc.data.Cardinality;

public class IfcListTypeInfo extends IfcCollectionTypeInfo {

	private static final long serialVersionUID = 1L;
	
	private IfcListTypeInfo superTypeInfo;

	public IfcListTypeInfo(IfcSchema schema, String typeName, IfcTypeInfo itemTypeInfo) {
		super(schema, typeName, itemTypeInfo);
	}

	public IfcListTypeInfo(IfcSchema schema, String typeName, String itemTypeInfoName) {
		super(schema, typeName, itemTypeInfoName);
	}
	
	public static String formatListTypeName(String itemTypeInfoName, Cardinality cardinality) {
		if (cardinality != null) {
			return String.format("%s_List_%s_%s", itemTypeInfoName, cardinality.getMin(),
					cardinality.getMax() == Cardinality.UNBOUNDED ? "UNBOUNDED" : cardinality.getMax());
		} else {
			return String.format("%s_List", itemTypeInfoName);			
		}
	}
	

	@Override
	public boolean isSorted() {
		return true;
	}

	/**
	 * @return the superTypeInfo
	 */
	public IfcListTypeInfo getSuperTypeInfo() {
		return superTypeInfo;
	}

	/**
	 * @param superTypeInfo the superTypeInfo to set
	 */
	public void setSuperTypeInfo(IfcListTypeInfo superTypeInfo) {
		this.superTypeInfo = superTypeInfo;
	}

	@Override
	public boolean isLiteralContainerType() {
		return getItemTypeInfo().isLiteralContainerType();
	}

}
