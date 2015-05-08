package fi.hut.cs.drumbeat.ifc.data.model;

import java.util.List;

public class IfcLiteralValueList extends IfcListValue<IfcLiteralValue> {

	private static final long serialVersionUID = 1L;

	public IfcLiteralValueList() {
	}

	public IfcLiteralValueList(List<IfcLiteralValue> values) {
		super(values);
	}

	@Override
	public Boolean isLiteralType() {
		return Boolean.TRUE;
	}

}
