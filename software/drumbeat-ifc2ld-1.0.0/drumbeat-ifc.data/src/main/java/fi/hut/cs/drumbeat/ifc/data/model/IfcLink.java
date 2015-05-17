package fi.hut.cs.drumbeat.ifc.data.model;

import java.util.List;

import fi.hut.cs.drumbeat.ifc.data.schema.IfcInverseLinkInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcLinkInfo;


public class IfcLink extends IfcAttribute { // implements IRdfTriple {
	
	private static final long serialVersionUID = 1L;

	protected IfcEntity source;
	protected IfcInverseLinkInfo inverseLinkInfo;
	private boolean useInverseLink;
	
	public IfcLink(IfcLinkInfo linkInfo, int attributeIndex, IfcEntity source, IfcEntityBase destination) {
		super(linkInfo, attributeIndex, destination);
		this.source = source;
	}
	
	public IfcLink(IfcLinkInfo linkInfo, int attributeIndex, IfcEntity source, IfcEntityCollection destinations) {
		super(linkInfo, attributeIndex, destinations);
		this.source = source;
	}
	
	public IfcEntity getSource() {
		return source;
	}
	
	public IfcEntity getSingleDestination() {
		return (IfcEntity)getValue(); 
	}
	
	public List<IfcEntityBase> getDestinations() {
		return super.getSingleValues();
	}
	
	public IfcLinkInfo getLinkInfo() {
		return (IfcLinkInfo)getAttributeInfo();
	}
	
	public IfcInverseLinkInfo getInverseLinkInfo() {
		return inverseLinkInfo;
	}

	public void setInverseLinkInfo(IfcInverseLinkInfo inverseLinkInfo) {
		this.inverseLinkInfo = inverseLinkInfo;
	}

	@Override
	public boolean isLiteralType() {
		return false;
	}
	
	/**
	 * @return the useInverseLink
	 */
	public boolean isUseInverseLink() {
		return useInverseLink;
	}

	/**
	 * @param useInverseLink the useInverseLink to set
	 */
	public void setUseInverseLink(boolean useInverseLink) {
		this.useInverseLink = useInverseLink;
	}

//	@Override
//	public IRdfNode getRdfSubject() {
//		return source;
//	}

}
