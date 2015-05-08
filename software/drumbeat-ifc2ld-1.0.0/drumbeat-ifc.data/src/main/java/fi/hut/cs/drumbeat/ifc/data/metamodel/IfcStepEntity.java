package fi.hut.cs.drumbeat.ifc.data.metamodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fi.hut.cs.drumbeat.ifc.data.model.IfcEntity;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLiteralValue;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLiteralValueCollection;
import fi.hut.cs.drumbeat.ifc.data.model.IfcValue;

public abstract class IfcStepEntity {

	private IfcEntity entity;
	private Map<String, IfcValue> values;
	
	public IfcStepEntity(IfcEntity entity) {
		this.entity = entity;
		values = new HashMap<>();
	}
	
	@SuppressWarnings("unchecked")
	protected <T> List<T> getListValue(String attributeName) {
		IfcLiteralValueCollection value = (IfcLiteralValueCollection)values.get(attributeName);

		if (value == null) {
			value = (IfcLiteralValueCollection)entity.getLiteralAttributes().selectFirstByName(attributeName)
					.getValue();
			values.put(attributeName, value);
		}
		return value.getSingleValues().stream().map(x -> (T)x.getValue()).collect(Collectors.toList());		
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T getSingleValue(String attributeName) {
		IfcLiteralValue value = (IfcLiteralValue)values.get(attributeName);

		if (value == null) {
			value = (IfcLiteralValue)entity.getLiteralAttributes().selectFirstByName(attributeName)
					.getValue();
			values.put(attributeName, value);
		}
		return (T)value.getValue();
	}
	

}
