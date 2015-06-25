package fi.hut.cs.drumbeat.ifc.data.schema;

import java.util.*;

import fi.hut.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;


public class IfcEntityTypeInfo extends IfcTypeInfo { // implements IRdfNode {
	
	private static final long serialVersionUID = 1L;
	
	private IfcEntityTypeInfo superTypeInfo;
	private List<IfcEntityTypeInfo> subTypeInfos;
	private List<IfcAttributeInfo> attributeInfos = new ArrayList<>();
	private List<IfcInverseLinkInfo> inverseLinkInfos = new ArrayList<>();
	private List<IfcAttributeInfo> inheritedAttributeInfos;
	private List<IfcInverseLinkInfo> inheritedInverseLinkInfos;
	private List<IfcUniqueKeyInfo> uniqueKeyInfos;
	private boolean isAbstractSuperType;
	private Boolean isLiteralContainerType;

	public IfcEntityTypeInfo(IfcSchema schema, String name) {
		super(schema, name);
	}
	
	public IfcEntityTypeInfo getSuperTypeInfo() {
		return superTypeInfo;
	}
	
	/**
	 * Sets the super entity type
	 * Note: This method should be called not more than once for every entity type
	 * @param superTypeInfo
	 */
	public void setSuperTypeInfo(IfcEntityTypeInfo superTypeInfo) {
		assert(this.superTypeInfo == null);
		this.superTypeInfo = superTypeInfo;
		
		if (superTypeInfo.subTypeInfos == null) {
			superTypeInfo.subTypeInfos = new ArrayList<>();
		}
		superTypeInfo.subTypeInfos.add(this);
	}
	
	public List<IfcEntityTypeInfo> getSubTypeInfos() {
		return subTypeInfos;
	}
	
	/**
	 * Gets the list of attributeInfos
	 * @return
	 */
	public List<IfcAttributeInfo> getAttributeInfos() {
		return attributeInfos;
	}
	
	public void addAttributeInfo(IfcAttributeInfo attributeInfo) {
		attributeInfos.add(attributeInfo);		
	}
	
	
	/**
	 * Gets list of all attributes including inherited ones from the super type. 
	 * @return List of all attributes
	 */
	public List<IfcAttributeInfo> getInheritedAttributeInfos() {
		if (inheritedAttributeInfos == null) {
			inheritedAttributeInfos = new ArrayList<>();
			if (superTypeInfo != null) {
				inheritedAttributeInfos.addAll(superTypeInfo.getInheritedAttributeInfos());
			}
			inheritedAttributeInfos.addAll(attributeInfos);
		}
		return inheritedAttributeInfos;
	}
	
	/**
	 * Gets list of all attributes including inherited ones from the super type. 
	 * @return List of all attributes
	 */
	public List<IfcInverseLinkInfo> getInheritedInverseLinkInfos() {
		if (inheritedInverseLinkInfos == null) {
			inheritedInverseLinkInfos = new ArrayList<>();
			if (superTypeInfo != null) {
				inheritedInverseLinkInfos.addAll(superTypeInfo.getInheritedInverseLinkInfos());
			}
			inheritedInverseLinkInfos.addAll(inverseLinkInfos);
		}
		return inheritedInverseLinkInfos;
	}

	/**
	 * @return the isAbstractSuperType
	 */
	public boolean isAbstractSuperType() {
		return isAbstractSuperType;
	}

	/**
	 * @param isAbstractSuperType the isAbstractSuperType to set
	 */
	public void setAbstractSuperType(boolean isAbstractSuperType) {
		this.isAbstractSuperType = isAbstractSuperType;
	}

	/**
	 * Checks if the current type is a subtype of another type
	 * @param typeInfo
	 * @return True if the current type is a subtype of typeInfo, False otherwise 
	 */
	public boolean isSubtypeOf(IfcEntityTypeInfo typeInfo) {
		return superTypeInfo != null && (superTypeInfo.equals(typeInfo) || superTypeInfo.isSubtypeOf(typeInfo));
	}
	
	/**
	 * Checks if the current type is the same as or a subtype of another type
	 * @param typeInfo
	 * @return True if the current type is the same or a subtype of typeInfo, False otherwise 
	 */
	public boolean isTypeOf(IfcEntityTypeInfo typeInfo) {
		return this.equals(typeInfo) || isSubtypeOf(typeInfo);
	}
	
//	@Override
//	public boolean isEntityOrSelectType() {
//		return true;
//	}		
	
	@Override
	public boolean isCollectionType() {
		return false;
	}
	
	public IfcAttributeInfo getAttributeInfo(String name) throws IfcNotFoundException {
		for (IfcAttributeInfo attributeInfo : attributeInfos) {
			if (attributeInfo.getName().equals(name)) {
				return attributeInfo;
			}
		}
		
		if (superTypeInfo != null) {
			return superTypeInfo.getAttributeInfo(name);
		}
		
		throw new IfcNotFoundException(String.format("Attribute not found: '%s'", name));
	}

	public List<IfcInverseLinkInfo> getInverseLinkInfos() {
		return inverseLinkInfos;
	}
	
	public void addInverseLinkInfo(IfcInverseLinkInfo inverseLinkInfo) {
		inverseLinkInfos.add(inverseLinkInfo);		
	}
	
	public IfcInverseLinkInfo getInverseLinkInfo(IfcLinkInfo linkInfo) {
		for (IfcInverseLinkInfo inverseLinkInfo : inverseLinkInfos) {
			if (inverseLinkInfo.isInverseTo(linkInfo))
			return inverseLinkInfo;
		}
		return null;
	}	
	
	
	/**
	 * Gets the basic type of literal values involved inside the type (if there is only one) 
	 * @return IfcTypeEnum.ENTITY
	 */
	@Override
	public EnumSet<IfcTypeEnum> getValueTypes() {
		return EnumSet.of(IfcTypeEnum.ENTITY);
	}	
	
	@Override
	public String getShortDescription(String typeNameFormat) {
		return IfcVocabulary.ExpressFormat.ENTITY;
	}	

	public List<IfcUniqueKeyInfo> getUniqueKeyInfos() {
		return uniqueKeyInfos;		
	}

	public void addUniqueKey(IfcUniqueKeyInfo uniqueKeyInfo) {
		if (uniqueKeyInfos == null) {
			uniqueKeyInfos = new ArrayList<>();
		}
		uniqueKeyInfos.add(uniqueKeyInfo);
	}

//	@Override
//	public RdfNodeTypeEnum getRdfNodeType() {
//		return RdfNodeTypeEnum.Uri;
//	}
//
//	@Override
//	public RdfUri toRdfUri() {
//		return Ifc2RdfConverter.getDefaultConverter().convertEntityTypeToRdfUri(this);
//	}
//
//	@Override
//	public List<IRdfLink> getRdfLinks() {
//		return null;
//	}
//
//	@Override
//	public StatusFlag getStatusFlag() {		
//		return statusFlag ;
//	}
	
	public boolean isLiteralContainerType() {
		if (isLiteralContainerType == null) {
			if (superTypeInfo == null || superTypeInfo.isLiteralContainerType()) {
				isLiteralContainerType = true;
				for (IfcAttributeInfo attributeInfo : this.getAttributeInfos()) {
					if (!attributeInfo.getAttributeTypeInfo().isLiteralContainerType()) {
						isLiteralContainerType = false;
						break;
					}
				}
			} else {
				isLiteralContainerType = false;
			}
		}
		
		return isLiteralContainerType.booleanValue();
	}
	
}
