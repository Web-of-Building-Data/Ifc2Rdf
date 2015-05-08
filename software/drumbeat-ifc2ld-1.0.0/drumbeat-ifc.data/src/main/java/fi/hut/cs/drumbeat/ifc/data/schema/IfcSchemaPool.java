package fi.hut.cs.drumbeat.ifc.data.schema;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains different schema versions
 * 
 * @author Nam Vu
 *
 */
public class IfcSchemaPool implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static Map<String, IfcSchema> schemas = new HashMap<>();
	
	/**
	 * Returns the {@link IfcSchema} with the specified version.
	 * 
	 * @param version - the version of the schema 
	 * @return the {@link IfcSchema} with the specified version, or <code>null</code> if the version is not found.
	 */
	public static IfcSchema getSchema(String version) {
		return schemas.get(version);
	}
	
	
	/**
	 * Add a new {@link IfcSchema} using its default version string.
	 * 
	 * @param schema - the schema to be added.
	 */
	public static void addSchema(IfcSchema schema) {
		addSchema(schema.getVersion(), schema);
	}
	

	/**
	 * Add a new {@link IfcSchema} using the specified version string.
	 * 
	 * @param version - the version to be used as key of the schema.
	 * @param schema - the schema to be added.
	 */
	public static void addSchema(String version, IfcSchema schema) {
		schemas.put(version, schema);
	}
}
