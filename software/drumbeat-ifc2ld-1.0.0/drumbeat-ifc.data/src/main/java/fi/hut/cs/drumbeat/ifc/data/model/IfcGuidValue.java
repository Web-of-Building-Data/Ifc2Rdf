package fi.hut.cs.drumbeat.ifc.data.model;

import fi.hut.cs.drumbeat.ifc.common.guid.Guid;
import fi.hut.cs.drumbeat.ifc.common.guid.GuidCompressor;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcDefinedTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcTypeEnum;

public class IfcGuidValue extends IfcLiteralValue {

	private static final long serialVersionUID = 1L;
	
	public IfcGuidValue(String value) {
		super(value, null, IfcTypeEnum.GUID);
	}
	
	public String getShortGuidId() {
		return super.getValue().toString();
	}
	
	public Guid getStandardGuid() {
		Guid guid = new Guid();
		GuidCompressor.getGuidFromCompressedString(getShortGuidId(), guid);
		return guid;
	}
	
}
