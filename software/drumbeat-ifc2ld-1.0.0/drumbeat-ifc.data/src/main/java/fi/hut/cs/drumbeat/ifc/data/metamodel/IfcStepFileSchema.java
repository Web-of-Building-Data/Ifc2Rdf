package fi.hut.cs.drumbeat.ifc.data.metamodel;

import java.util.List;

import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntity;

public class IfcStepFileSchema extends IfcStepEntity {
	
	public IfcStepFileSchema(IfcEntity entity) {
		super(entity);
	}

	public List<String> getSchemas() {
		return getListValue(IfcVocabulary.StepFormat.Header.FileSchema.SCHEMA_IDENTIFIERS);
	}	
}
