package fi.hut.cs.drumbeat.ifc.data.schema;

import fi.hut.cs.drumbeat.ifc.data.Cardinality;

public class IfcInverseLinkInfo extends IfcAttributeInfo {

	private static final long serialVersionUID = 1L;

	IfcLinkInfo outgoingLinkInfo;
	private Cardinality cardinality;

	public IfcInverseLinkInfo(IfcEntityTypeInfo entityTypeInfo, String name,
			IfcEntityTypeInfo linkSourceEntityTypeInfo,
			IfcLinkInfo outgoingLinkInfo) {
		super(entityTypeInfo, name, linkSourceEntityTypeInfo);
		this.outgoingLinkInfo = outgoingLinkInfo;
		outgoingLinkInfo.addInverseLinkInfo(this);
	}

	public IfcEntityTypeInfo getSourceEntityTypeInfo() {
		return (IfcEntityTypeInfo) getAttributeTypeInfo();
	}

	public IfcLinkInfo getOutgoingLinkInfo() {
		return outgoingLinkInfo;
	}

	public IfcEntityTypeInfo getDestinationEntityTypeInfo() {
		return getEntityTypeInfo();
	}

	public boolean isInverseTo(IfcLinkInfo outgoingLinkInfo) {
		return this.outgoingLinkInfo.equals(outgoingLinkInfo);
	}

	public Cardinality getCardinality() {
		return cardinality;
	}

	public void setCardinality(Cardinality cardinality) {
		this.cardinality = cardinality;
	}

}
