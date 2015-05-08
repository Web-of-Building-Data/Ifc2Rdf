package fi.hut.cs.drumbeat.ifc.data.schema;

import java.util.EnumSet;

import fi.hut.cs.drumbeat.common.string.StringUtils;
import fi.hut.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;
import fi.hut.cs.drumbeat.ifc.data.Cardinality;

public class IfcCollectionTypeInfo extends IfcDefinedTypeInfo {

	private static final long serialVersionUID = 1L;

	private IfcCollectionKindEnum collectionKind;
	private String itemTypeInfoName;
	private IfcTypeInfo itemTypeInfo;
	private Cardinality cardinality;
	private IfcCollectionTypeInfo superCollectionTypeWithoutCardinalities;

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
		return collectionKind.allowsRepeatedItems();
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
	 * @param cardinality
	 *            the cardinality to set
	 */
	public void setCardinality(Cardinality cardinality) {
		this.cardinality = cardinality;
	}

	/**
	 * @return the superTypeInfo
	 */
	public IfcCollectionTypeInfo getSuperCollectionTypeWithoutCardinalities() {
		
		IfcTypeInfo itemTypeInfo = getItemTypeInfo();
		if (itemTypeInfo != null) {

			String superTypeName = formatCollectionTypeName(collectionKind,
					itemTypeInfo.getName(), null);
	
			return new IfcCollectionTypeInfo(
					getSchema(), superTypeName, collectionKind, itemTypeInfo);
		} else {
			return null;
		}

		// return superCollectionTypeWithoutCardinalities;
	}

	/**
	 * @return the superTypeInfo
	 */
	public IfcCollectionTypeInfo getSuperCollectionTypeWithoutItemType() {

		String superTypeName = formatCollectionTypeName(collectionKind, null,
				cardinality);

		IfcCollectionTypeInfo superType = new IfcCollectionTypeInfo(
				getSchema(), superTypeName, collectionKind, StringUtils.EMPTY);

		superType.setCardinality(cardinality);

		return superType;

		// return superCollectionTypeWithoutCardinalities;
	}

	// TODO: remove this method
	/**
	 * @param superTypeInfo
	 *            the superTypeInfo to set
	 */
	public void setSuperCollectionTypeWithoutCardinalities(
			IfcCollectionTypeInfo superCollectionTypeWithUnlimitedCardinality) {
		this.superCollectionTypeWithoutCardinalities = superCollectionTypeWithUnlimitedCardinality;
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

	public static String formatCollectionTypeName(
			IfcCollectionKindEnum collectionKind, String itemTypeInfoName,
			Cardinality cardinality) {
		String collectionTypeName = String.format("%s_%s", collectionKind,
				itemTypeInfoName);
		if (cardinality == null) {
			return collectionTypeName;
		}

		return String.format("%s_%s_%s", collectionTypeName, cardinality
				.getMin(),
				cardinality.getMax() == Cardinality.UNBOUNDED ? "UNBOUNDED"
						: cardinality.getMax());
	}

	@Override
	public boolean isLiteralContainerType() {
		return getItemTypeInfo().isLiteralContainerType();
	}

}
