package fi.hut.cs.drumbeat.ifc.data.metamodel;

import java.util.List;

import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntity;

public class IfcStepFileDescription extends IfcStepEntity {
	
	public IfcStepFileDescription(IfcEntity entity) {
		super(entity);
	}
	
	public List<String> getDescriptions() {
		return getListValue(IfcVocabulary.StepFormat.Header.FileDescription.DESCRIPTION);
	}
	
	public String getImplementationLevel() {
		return getSingleValue(IfcVocabulary.StepFormat.Header.FileDescription.IMPLEMENTATION_LEVEL);
	}
	
	
}
