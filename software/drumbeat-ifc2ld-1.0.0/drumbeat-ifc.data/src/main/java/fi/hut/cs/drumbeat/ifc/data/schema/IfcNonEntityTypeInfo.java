package fi.hut.cs.drumbeat.ifc.data.schema;

/**
 * Abstract superclass for all non-entity types (defined types, select types, enum types) 
 * 
 * @author vuhoan1
 *
 */
public abstract class IfcNonEntityTypeInfo extends IfcTypeInfo {

	private static final long serialVersionUID = 1L;

	public IfcNonEntityTypeInfo(IfcSchema schema, String name) {
		super(schema, name);
	}

}
