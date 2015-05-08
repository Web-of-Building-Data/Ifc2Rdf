package fi.hut.cs.drumbeat.ifc.common;

public class IfcCommandLineOptions {
	
	public static final String URL_RIOT_FORMAT = "https://jena.apache.org/documentation/javadoc/arq/org/apache/jena/riot/RDFFormat.html";
	
	public static final int FORMATTER_WIDTH = 80;

	public static final String ARG_NAME = "name";
	public static final String ARG_FILE = "file";
	public static final String ARG_DIR = "dir";

	public static final String SUFFIX_OPTIONAL = " (optional)";
	
	public static final String HELP_SHORT = "?";
	public static final String HELP_LONG = "help";
	public static final String HELP_DESCRIPTION = "Help";		

	public static final String LOGGER_CONFIG_FILE_SHORT = "lcf";
	public static final String LOGGER_CONFIG_FILE_LONG = "log-config-file";
	public static final String LOGGER_CONFIG_FILE_DESCRIPTION = "Logger configuration file (.properties or .xml)";
	
	public static final String CONFIG_FILE_SHORT = "cf";
	public static final String CONFIG_FILE_LONG = "config";
	public static final String CONFIG_FILE_DESCRIPTION = "Configuration file";
	
	public static final String OUTPUT_LAYER_NAME_SHORT = "oln";
	public static final String OUTPUT_LAYER_NAME_LONG = "output-layer-name";
	public static final String OUTPUT_LAYER_NAME_DESCRIPTION = "Layer name";

	public static final String INPUT_SCHEMA_FILE_SHORT = "isf";
	public static final String INPUT_SCHEMA_FILE_LONG = "input-schema-file";
	public static final String INPUT_SCHEMA_FILE_DESCRIPTION = "Source IFC schema file or folder";
	
	public static final String INPUT_MODEL_FILE_1_SHORT = "imf";
	public static final String INPUT_MODEL_FILE_1_LONG = "input-model-file";
	public static final String INPUT_MODEL_FILE_1_DESCRIPTION = "Source IFC model file";
	
	public static final String INPUT_MODEL_FILE_2_SHORT = "imf2";
	public static final String INPUT_MODEL_FILE_2_LONG = "input-model-file-2";
	public static final String INPUT_MODEL_FILE_2_DESCRIPTION = "Source IFC model file 2";

	public static final String OUTPUT_SCHEMA_FILE_SHORT = "osf";
	public static final String OUTPUT_SCHEMA_FILE_LONG = "output-schema-file";
	public static final String OUTPUT_SCHEMA_FILE_DESCRIPTION = "Target RDF file for the ontology";
	
	public static final String OUTPUT_FILE_FORMAT_SHORT = "off";
	public static final String OUTPUT_FILE_FORMAT_LONG = "output-file-format";
	public static final String OUTPUT_FILE_FORMAT_DEFAULT = "TURTLE";
	public static final String OUTPUT_FILE_FORMAT_DESCRIPTION =
			"RDF format (add suffix .GZ to gzip file), e.g.: TURTLE (default), TURTLE.GZ, NTRIPLES, NQUADS, RDFXML_ABBREV, RDFXML_PRETTY, JSONLD, etc. (see more: " + URL_RIOT_FORMAT + ")";

	public static final String OUTPUT_MODEL_FILE_1_SHORT = "omf";
	public static final String OUTPUT_MODEL_FILE_1_LONG = "output-model-file";
	public static final String OUTPUT_MODEL_FILE_1_DESCRIPTION = "Target RDF file for the model";		
	
	public static final String OUTPUT_MODEL_FILE_2_SHORT = "omf2";
	public static final String OUTPUT_MODEL_FILE_2_LONG = "output-model-file-2";
	public static final String OUTPUT_MODEL_FILE_2_DESCRIPTION = "Target RDF file for the model 2";		

	public static final String OUTPUT_META_MODEL_FILE_1_SHORT = "ommf";
	public static final String OUTPUT_META_MODEL_FILE_1_LONG = "output-meta-model-file";
	public static final String OUTPUT_META_MODEL_FILE_1_DESCRIPTION = "Target RDF file for the meta-model";		
	
	public static final String OUTPUT_META_MODEL_FILE_2_SHORT = "ommf2";
	public static final String OUTPUT_META_MODEL_FILE_2_LONG = "output-meta-model-file-2";
	public static final String OUTPUT_META_MODEL_FILE_2_DESCRIPTION = "Target RDF file for the meta-model 2";		

	public static final String OUTPUT_SCHEMA_NAME_SHORT = "osn";
	public static final String OUTPUT_SCHEMA_NAME_LONG = "output-schema-name";
	public static final String OUTPUT_SCHEMA_NAME_DESCRIPTION = "Target RDF store for the ontology";
	
	public static final String OUTPUT_MODEL_NAME_1_SHORT = "omn";
	public static final String OUTPUT_MODEL_NAME_1_LONG = "output-model-name";
	public static final String OUTPUT_MODEL_NAME_1_DESCRIPTION = "Target RDF store for the model";		

	public static final String OUTPUT_MODEL_NAME_2_SHORT = "omn2";
	public static final String OUTPUT_MODEL_NAME_2_LONG = "output-model-name-2";
	public static final String OUTPUT_MODEL_NAME_2_DESCRIPTION = "Target RDF store for the model 2";		

	public static final String OUTPUT_META_MODEL_NAME_1_SHORT = "ommn";
	public static final String OUTPUT_META_MODEL_NAME_1_LONG = "output-meta-model-name";
	public static final String OUTPUT_META_MODEL_NAME_1_DESCRIPTION = "Target RDF store for the meta-model";		

	public static final String OUTPUT_META_MODEL_NAME_2_SHORT = "ommn2";
	public static final String OUTPUT_META_MODEL_NAME_2_LONG = "output-meta-model-name-2";
	public static final String OUTPUT_META_MODEL_NAME_2_DESCRIPTION = "Target RDF store for the meta-model 2";		

	public static final String COMPARE_MODELS_SHORT = "cpm";
	public static final String COMPARE_MODELS_LONG = "compare-models";
	public static final String COMPARE_MODELS_DESCRIPTION = "Compare models";
	
	public static final String COMPARE_MODELS_DIR_SHORT = "cpd";
	public static final String COMPARE_MODELS_DIR_LONG = "compare-models-dir";
	public static final String COMPARE_MODELS_DIR_DESCRIPTION = "Compare model output directory";

	public static final String GROUNDING_RULE_SET_SHORT = "grs";	
	public static final String GROUNDING_RULE_SET_LONG = "grounding-rule-set";	
	public static final String GROUNDING_RULE_SET_DESCRIPTION = "Grounding rule set name";

}
