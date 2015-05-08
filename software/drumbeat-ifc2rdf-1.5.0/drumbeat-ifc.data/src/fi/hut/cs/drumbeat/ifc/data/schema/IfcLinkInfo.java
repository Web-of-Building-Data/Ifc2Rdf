package fi.hut.cs.drumbeat.ifc.data.schema;

import java.util.ArrayList;
import java.util.List;

public class IfcLinkInfo extends IfcAttributeInfo {

	private static final long serialVersionUID = 1L;
	
	private List<IfcInverseLinkInfo> inverseLinkInfos;	

	public IfcLinkInfo(IfcEntityTypeInfo sourceEntityTypeInfo, String name, IfcTypeInfo destinationTypeInfo) {
		super(sourceEntityTypeInfo, name, destinationTypeInfo);
	}
	
	public IfcEntityTypeInfo getSourceTypeInfo() {
		return getEntityTypeInfo();
	}
	
	public IfcTypeInfo getDestinationTypeInfo() {
		return getAttributeTypeInfo();
	}	
	
	public List<IfcInverseLinkInfo> getInverseLinkInfos() {
		return inverseLinkInfos;
	}

	public void addInverseLinkInfo(IfcInverseLinkInfo inverseLinkInfo) {
		if (inverseLinkInfos == null) {
			inverseLinkInfos = new ArrayList<>();
		}
		inverseLinkInfos.add(inverseLinkInfo);
	}
}
