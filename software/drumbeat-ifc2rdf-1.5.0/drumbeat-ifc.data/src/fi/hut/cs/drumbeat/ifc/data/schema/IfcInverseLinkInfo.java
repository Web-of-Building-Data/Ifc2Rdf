package fi.hut.cs.drumbeat.ifc.data.schema;

public class IfcInverseLinkInfo extends IfcAttributeInfo {
	
	private static final long serialVersionUID = 1L;
	
	IfcLinkInfo outgoingLinkInfo;

	public IfcInverseLinkInfo(IfcEntityTypeInfo entityTypeInfo, String name, IfcEntityTypeInfo linkSourceEntityTypeInfo, IfcLinkInfo outgoingLinkInfo) {
		super(entityTypeInfo, name, linkSourceEntityTypeInfo);
		this.outgoingLinkInfo = outgoingLinkInfo;
		outgoingLinkInfo.addInverseLinkInfo(this);
	}
	
	public IfcEntityTypeInfo getSourceEntityTypeInfo() {
		return (IfcEntityTypeInfo)getAttributeTypeInfo();
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
	
}
