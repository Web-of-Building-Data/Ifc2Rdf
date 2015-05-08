package fi.hut.cs.drumbeat.ifc.data.model;

import java.util.List;

public class IfcLiteralValueCollection extends IfcCollectionValue<IfcLiteralValue> {

	private static final long serialVersionUID = 1L;

	public IfcLiteralValueCollection() {
	}

	public IfcLiteralValueCollection(List<IfcLiteralValue> values) {
		super(values);
	}

	@Override
	public Boolean isLiteralType() {
		return Boolean.TRUE;
	}

}
