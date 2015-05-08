package fi.hut.cs.drumbeat.ifc.data.schema;

import java.util.EnumSet;

public enum IfcTypeEnum {
	
	ENTITY,
	GUID,
	ENUM,
	INTEGER,
	REAL,
	STRING,
	LOGICAL,
	DATETIME;
	
	public static final EnumSet<IfcTypeEnum> LITERAL	= EnumSet.of(GUID, ENUM, INTEGER, REAL, STRING, LOGICAL, DATETIME);
	public static final EnumSet<IfcTypeEnum> LINK		= EnumSet.of(ENTITY);
	public static final EnumSet<IfcTypeEnum> NUMBER	= EnumSet.of(INTEGER, REAL);
	
}
