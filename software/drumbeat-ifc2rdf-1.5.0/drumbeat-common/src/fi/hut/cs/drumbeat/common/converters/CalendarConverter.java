package fi.hut.cs.drumbeat.common.converters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

//import javax.xml.bind.DatatypeConverter;

public class CalendarConverter {
	
	public static final TimeZone TIME_ZONE_GMT = TimeZone.getTimeZone("GMT"); 
	
	public static DateFormat XSD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	static {
		XSD_DATE_FORMAT.setTimeZone(TIME_ZONE_GMT);
	}
	
	public static Date xsdDateTimeToDate(String xsdDateTime) throws ParseException {
		return XSD_DATE_FORMAT.parse(xsdDateTime);
	}
	
	public static Calendar xsdDateTimeToCalendar(String xsdDateTime) throws ParseException {
		Calendar calendar = Calendar.getInstance(TIME_ZONE_GMT);
		calendar.setTime(xsdDateTimeToDate(xsdDateTime));
		return calendar;
		//return DatatypeConverter.parseDateTime(xsdDateTime);
	}
}
