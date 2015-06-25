package fi.hut.cs.drumbeat.ifc.data.schema;

import java.io.Serializable;

//import fi.hut.cs.drumbeat.ifc.data.Cardinality;

public class IfcAttributeInfo implements Comparable<IfcAttributeInfo>,
		Serializable {

	private static final long serialVersionUID = 1L;

	private IfcEntityTypeInfo entityTypeInfo;
	private String name;
	private String uniqueName;
	private int attributeIndex;
	private IfcTypeInfo attributeTypeInfo;
	private boolean isOptional;
	// private Cardinality cardinality;
	private boolean isFunctional;
	private boolean isInverseFunctional;

	public IfcAttributeInfo(IfcEntityTypeInfo entityTypeInfo, String name, IfcTypeInfo attributeTypeInfo) {
		this.entityTypeInfo = entityTypeInfo;
		this.name = name;
		this.attributeTypeInfo = attributeTypeInfo;
	}

	public IfcEntityTypeInfo getEntityTypeInfo() {
		return entityTypeInfo;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return the uniqueName
	 */
	public String getUniqueName() {
		return uniqueName != null ? uniqueName : name;
	}

	/**
	 * @param uniqueName
	 *            the uniqueName to set
	 */
	public void setUniqueName(String rdfName) {
		this.uniqueName = rdfName;
	}

	public int getAttributeIndex() {
		return attributeIndex;
	}

	public void setAttributeIndex(int attributeIndex) {
		this.attributeIndex = attributeIndex;
	}

	public IfcTypeInfo getAttributeTypeInfo() {
		return attributeTypeInfo;
	}

	public boolean isOptional() {
		return isOptional;
	}

	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
		// if (cardinality != null) {
		// cardinality.setOptional(isOptional);
		// }
	}

	// public Cardinality getCardinality() {
	// return cardinality;
	// }
	//
	// public void setCardinality(Cardinality cardinality) {
	// this.cardinality = cardinality;
	// cardinality.setOptional(isOptional);
	// }
	//
	// public boolean isMultiple() {
	// return cardinality.isMultiple();
	// }

	public boolean isCollection() {
		return getAttributeTypeInfo() instanceof IfcCollectionTypeInfo;
	}

	/**
	 * @return the isFunctional
	 */
	public boolean isFunctional() {
		return isFunctional;
	}

	/**
	 * @param isFunctional
	 *            the isFunctional to set
	 */
	public void setFunctional(boolean isFunctional) {
		this.isFunctional = isFunctional;
	}

	/**
	 * @return the isInverseFunctional
	 */
	public boolean isInverseFunctional() {
		return isInverseFunctional;
	}

	/**
	 * @param isInverseFunctional
	 *            the isInverseFunctional to set
	 */
	public void setInverseFunctional(boolean isInverseFunctional) {
		this.isInverseFunctional = isInverseFunctional;
	}

	@Override
	public String toString() {
		return name; // String.format("%s.%s", entityTypeInfo.getName(), name);
	}

	public boolean equals(IfcAttributeInfo o) {
		return name.equals(o.name);
	}

	@Override
	public int compareTo(IfcAttributeInfo o) {
		int compare = name.compareTo(o.name);
		if (compare == 0) {
			compare = entityTypeInfo.compareTo(o.entityTypeInfo);
		}
		return compare;
	}

	// @Override
	// public RdfNodeTypeEnum getRdfNodeType() {
	// return RdfNodeTypeEnum.Uri;
	// }
	//
	// @Override
	// public RdfUri toRdfUri() {
	// return
	// Ifc2RdfConverter.getDefaultConverter().convertAttributeToRdfUri(this);
	// }
	//
	// @Override
	// public List<IRdfLink> getRdfLinks() {
	// // TODO: Implement this
	// throw new RuntimeException("Not implemented");
	// }
	//
	// @Override
	// public StatusFlag getStatusFlag() {
	// return statusFlag;
	// }

}
