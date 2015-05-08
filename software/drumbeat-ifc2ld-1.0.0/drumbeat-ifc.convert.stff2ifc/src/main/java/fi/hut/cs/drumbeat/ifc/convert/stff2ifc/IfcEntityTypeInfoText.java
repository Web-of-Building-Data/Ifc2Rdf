package fi.hut.cs.drumbeat.ifc.convert.stff2ifc;

import java.util.ArrayList;
import java.util.List;

import fi.hut.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;


class IfcEntityTypeInfoText {
	
	private IfcEntityTypeInfo entityTypeInfo;
	private String superTypeName;
	private List<String> attributeStatements = new ArrayList<>();	
	private List<String> inverseLinkStatements = new ArrayList<>();
	private List<String> uniqueKeysStatements = new ArrayList<>();

	public IfcEntityTypeInfoText(IfcEntityTypeInfo entityTypeInfo) {
		this.entityTypeInfo = entityTypeInfo;
	}
	

	/**
	 * @return the superTypeName
	 */
	public String getSuperTypeName() {
		return superTypeName;
	}


	/**
	 * @param superTypeName the superTypeName to set
	 */
	public void setSuperTypeName(String superTypeName) {
		this.superTypeName = superTypeName;
	}


	/**
	 * @return the entityTypeInfo
	 */
	public IfcEntityTypeInfo getEntityTypeInfo() {
		return entityTypeInfo;
	}

	/**
	 * @return the attributeStatements
	 */
	public List<String> getAttributeStatements() {
		return attributeStatements;
	}

	/**
	 * @return the inverseLinkStatements
	 */
	public List<String> getInverseLinkStatements() {
		return inverseLinkStatements;
	}


	/**
	 * @return the uniqueKeysStatements
	 */
	public List<String> getUniqueKeysStatements() {
		return uniqueKeysStatements;
	}


}
