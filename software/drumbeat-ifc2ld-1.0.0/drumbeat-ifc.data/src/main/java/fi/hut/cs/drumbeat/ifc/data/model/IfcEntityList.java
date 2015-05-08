package fi.hut.cs.drumbeat.ifc.data.model;

import java.util.List;

public class IfcEntityList extends IfcCollectionValue<IfcEntityBase> {

	private static final long serialVersionUID = 1L;

	public IfcEntityList() {
	}

	public IfcEntityList(List<IfcEntityBase> values) {
		super(values);
	}

	@Override
	public Boolean isLiteralType() {
		return false;
	}

}
