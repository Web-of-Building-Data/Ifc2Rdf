package fi.hut.cs.drumbeat.ifc.data.metamodel;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import fi.hut.cs.drumbeat.common.converters.CalendarConverter;
import fi.hut.cs.drumbeat.ifc.common.IfcVocabulary;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntity;

public class IfcStepFileName extends IfcStepEntity {
	
	public IfcStepFileName(IfcEntity entity) {
		super(entity);
	}
	
	public String getName() {
		return getSingleValue(IfcVocabulary.StepFormat.Header.FileName.NAME);
	}

	public Calendar getTimeStamp() {
		try {
			return CalendarConverter.xsdDateTimeToCalendar(getSingleValue(IfcVocabulary.StepFormat.Header.FileName.TIME_STAMP));
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		} 
	}

	public List<String> getAuthors() {
		return getListValue(IfcVocabulary.StepFormat.Header.FileName.AUTHOR);
	}
	
	public List<String> getOrganizations() {
		return getListValue(IfcVocabulary.StepFormat.Header.FileName.ORGANIZATION);
	}
	
	public String getPreprocessorVersion() {
		return getSingleValue(IfcVocabulary.StepFormat.Header.FileName.PREPROCESSOR_VERSION);
	}

	public String getOriginatingSystem() {
		return getSingleValue(IfcVocabulary.StepFormat.Header.FileName.ORIGINATING_SYSTEM);
	}

	public String getAuthorization() {
		return getSingleValue(IfcVocabulary.StepFormat.Header.FileName.AUTHORIZATION);
	}

}
