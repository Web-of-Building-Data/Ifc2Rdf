package fi.hut.cs.drumbeat.ifc.data.schema;

import java.io.Serializable;
import java.util.EnumSet;

/**
 * Represents type information for IFC types (Defined Type, Enumeration, Select Type and Entity).
 *  
 * Abstract class for {@link IfcNonEntityTypeInfo}, {@link IfcEntityTypeInfo}
 * 
 * @author Nam
 *
 */
public abstract class IfcTypeInfo implements Comparable<IfcTypeInfo>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final IfcSchema schema;
	private final String name;
	
	/**
	 * Creates a new type info.
	 * @param name The type name, can be null.
	 */
	public IfcTypeInfo(IfcSchema schema, String name) {
		this.schema = schema;
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	public IfcSchema getSchema() {
		return schema;
	}
	
//	@Override
//	public boolean equals(Object other) {
//		return name.equals(((IfcTypeInfo)other).name);
//	}
	
	/**
	 * Checks if the value is represented as a single value or as a collection of values.
	 * @return True if the value is represented as a collection, otherwise False.
	 */
	public abstract boolean isCollectionType();
	
	/**
	 * Checks if the value type is entity or literal.
	 * @return True if the value is entity, False if the value is literal.
	 */
//	public abstract boolean isEntityOrSelectType();
	
	
	/**
	 * Gets the basic type of literal values involved inside the type (if there is only one) 
	 * @return 
	 * @throws IfcValueTypeConflictException 
	 */
	public abstract EnumSet<IfcTypeEnum> getValueTypes();
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	@Override
	public int compareTo(IfcTypeInfo o) {
		return name.compareTo(o.name);
	}
	
	public abstract String getShortDescription(String typeNameFormat);
	
	public abstract boolean isLiteralContainerType();	
}
