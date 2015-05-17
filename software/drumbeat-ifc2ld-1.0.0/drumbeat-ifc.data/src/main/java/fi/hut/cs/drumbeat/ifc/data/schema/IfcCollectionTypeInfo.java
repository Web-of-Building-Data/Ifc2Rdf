package fi.hut.cs.drumbeat.ifc.data.schema;

import java.util.EnumSet;

import fi.hut.cs.drumbeat.common.string.StringUtils;
import fi.hut.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;
import fi.hut.cs.drumbeat.ifc.data.Cardinality;

public class IfcCollectionTypeInfo extends IfcNonEntityTypeInfo {

	private static final long serialVersionUID = 1L;

	private IfcCollectionKindEnum collectionKind;
	private String itemTypeInfoName;
	private IfcTypeInfo itemTypeInfo;
	private Cardinality cardinality;

	public IfcCollectionTypeInfo(IfcSchema schema, String typeName,
			IfcCollectionKindEnum collectionKind, IfcTypeInfo itemTypeInfo) {
		super(schema, typeName);
		this.collectionKind = collectionKind;
		this.itemTypeInfo = itemTypeInfo;
		this.itemTypeInfoName = itemTypeInfo.getName();
	}

	public IfcCollectionTypeInfo(IfcSchema schema, String typeName,
			IfcCollectionKindEnum collectionKind, String itemTypeInfoName) {
		super(schema, typeName);
		this.collectionKind = collectionKind;
		this.itemTypeInfoName = itemTypeInfoName;
	}

	public IfcCollectionKindEnum getCollectionKind() {
		return collectionKind;
	}

	public boolean isSorted() {
		return collectionKind.isSorted();
	}

	/**
	 * @return the itemsAreUnique
	 */
	public boolean allowsRepeatedItems() {
		return collectionKind.allowsDuplicatedItems();
	}

	public IfcTypeInfo getItemTypeInfo() {
		if (itemTypeInfo == null && !StringUtils.EMPTY.equals(itemTypeInfoName)) {
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

//	@Override
//	public boolean isEntityOrSelectType() {
//		return getItemTypeInfo().isEntityOrSelectType();
//	}

	/**
	 * @return the cardinality
	 */
	public Cardinality getCardinality() {
		return cardinality;
	}

	/**
	 * @param cardinality
	 *            the cardinality to set
	 */
	public void setCardinality(Cardinality cardinality) {
		this.cardinality = cardinality;
	}

	/**
	 * @return the superTypeInfo
	 */
	public IfcCollectionTypeInfo getSuperCollectionTypeWithItemTypeAndNoCardinalities() {
		
		if (cardinality == null) {
			return null;
		}

		IfcTypeInfo itemTypeInfo = getItemTypeInfo();		
		if (itemTypeInfo == null) {
			return null; 
		}

		String superTypeName = formatCollectionTypeName(collectionKind,
				itemTypeInfo.getName(), null);

		return new IfcCollectionTypeInfo(
				getSchema(), superTypeName, collectionKind, itemTypeInfo);
		
	}

	/**
	 * @return the superTypeInfo
	 */
	public IfcCollectionTypeInfo getSuperCollectionTypeWithCardinalitiesAndNoItemType() {
		
		if (cardinality == null) {
			return null;
		}

		IfcTypeInfo itemTypeInfo = getItemTypeInfo();		
		if (itemTypeInfo == null) {
			return null; 
		}
		
		String superTypeName = formatCollectionTypeName(collectionKind, null,
				cardinality);

		IfcCollectionTypeInfo superType = new IfcCollectionTypeInfo(
				getSchema(), superTypeName, collectionKind, StringUtils.EMPTY);

		superType.setCardinality(cardinality);

		return superType;
	}

	/**
	 * Gets the basic type of literal values involved inside the type (if there
	 * is only one)
	 * 
	 * @return
	 * @throws IfcValueTypeConflictException
	 */
	public EnumSet<IfcTypeEnum> getValueTypes() {
		return getItemTypeInfo().getValueTypes();
	}

	@Override
	public String getShortDescription(String typeNameFormat) {
		String prefix = isSorted() ? IfcVocabulary.ExpressFormat.LIST
				: IfcVocabulary.ExpressFormat.SET;

		return String.format("%s [%s,%s] %s " + typeNameFormat, prefix,
				cardinality.getMinType(), cardinality.getMaxType(),
				IfcVocabulary.ExpressFormat.OF, itemTypeInfo.getName());
	}

	public static String formatCollectionTypeName(IfcCollectionKindEnum collectionKind, String itemTypeInfoName, Cardinality cardinality) {
		if (itemTypeInfoName != null && cardinality != null) {
			return String.format("%s_%s_%s_%s_%s",
					collectionKind,
					cardinality.getMinCardinality(),
					cardinality.getMaxCardinality() == Cardinality.UNBOUNDED ? "UNBOUNDED" : cardinality.getMaxCardinality(),
					IfcVocabulary.ExpressFormat.OF,
					itemTypeInfoName); 
		} else if (cardinality != null) {
			return String.format("%s_%s_%s",
					collectionKind,
					cardinality.getMinCardinality(),
					cardinality.getMaxCardinality() == Cardinality.UNBOUNDED ? "UNBOUNDED" : cardinality.getMaxCardinality()); 
		} else { // itemTypeInfoName
			return String.format("%s_%s_%s",
					collectionKind,
					IfcVocabulary.ExpressFormat.OF,
					itemTypeInfoName);
		}

	}

	@Override
	public boolean isLiteralContainerType() {
		return getItemTypeInfo().isLiteralContainerType();
	}

}
