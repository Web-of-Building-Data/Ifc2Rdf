package fi.hut.cs.drumbeat.ifc.data.schema;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import fi.hut.cs.drumbeat.ifc.data.LogicalEnum;

public class IfcLogicalTypeInfo extends IfcEnumerationTypeInfo {

	private static final long serialVersionUID = 1L;

	public IfcLogicalTypeInfo(IfcSchema schema, String name, List<LogicalEnum> values) {
		super(schema, name, values.stream().map(value -> value.toString()).collect(Collectors.toList()));
	}
	
	@Override
	public EnumSet<IfcTypeEnum> getValueTypes() {
		return EnumSet.of(IfcTypeEnum.LOGICAL);
	}	
	
	

}
