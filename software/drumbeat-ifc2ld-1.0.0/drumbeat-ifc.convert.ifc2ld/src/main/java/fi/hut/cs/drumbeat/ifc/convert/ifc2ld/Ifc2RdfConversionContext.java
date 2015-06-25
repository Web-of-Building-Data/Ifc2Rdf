package fi.hut.cs.drumbeat.ifc.convert.ifc2ld;

import java.util.*;
import java.util.regex.Matcher;

import fi.hut.cs.drumbeat.common.config.ConfigurationItemEx;
import fi.hut.cs.drumbeat.common.config.document.ConfigurationParserException;
import fi.hut.cs.drumbeat.common.config.document.ConverterPoolConfigurationSection;
import fi.hut.cs.drumbeat.rdf.*;


public class Ifc2RdfConversionContext {
	
	/////////////////////////
	// STATIC MEMBERS
	/////////////////////////
	
	public static final String CONFIGURATION_SECTION_CONVERTER_TYPE_NAME = "Ifc2Rdf";
	public static final String CONFIGURATION_PROPERTY_OWL_VERSION = "OwlVersion";
	
	private static final String CONFIGURATION_PROPERTY_OWL_PROFILE = "OwlProfile";
	private static final String CONFIGURATION_PROPERTY_CONVERSION_OPTIONS_PREFIX = "Options.";
	private static final String CONFIGURATION_PROPERTY_ONTOLOGY_PREFIX = "Ontology.Prefix";
	private static final String CONFIGURATION_PROPERTY_ONTOLOGY_NAMESPACE_FORMAT = "Ontology.NamespaceFormat";	
	private static final String CONFIGURATION_PROPERTY_MODEL_PREFIX = "Model.Prefix";
	private static final String CONFIGURATION_PROPERTY_MODEL_NAMESPACE_FORMAT = "Model.NamespaceFormat";
	
	private static final String CONFIGURATION_NAMESPACE_FORMAT_VARIABLE_SCHEMA_VERSION = Matcher.quoteReplacement("$Schema.Version$");
	private static final String CONFIGURATION_VARIABLE_CONVERTER_CONTEXT_NAME = Matcher.quoteReplacement("$Converter.Context.Name$");
	
	
	public static Ifc2RdfConversionContext loadFromConfigurationFile(String contextName) throws ConfigurationParserException {
		ConverterPoolConfigurationSection configurationSection =
				ConverterPoolConfigurationSection.getInstance(Ifc2RdfConversionContext.CONFIGURATION_SECTION_CONVERTER_TYPE_NAME);
		
		ConfigurationItemEx configuration;
		if (contextName != null) {
			configuration = configurationSection.getConfigurationPool().getByName(contextName);
		} else {
			configuration = configurationSection.getConfigurationPool().getDefault(); 
		}		
				
		Ifc2RdfConversionContext context = new Ifc2RdfConversionContext(configuration);
		return context;		
	}
	
	
	/////////////////////////
	// NON-STATIC MEMBERS
	/////////////////////////

	private String owlVersion; 
	private OwlProfileList owlProfileList;
	private String name;
	private String ontologyPrefix;
	private String ontologyNamespaceFormat;
	private String modelPrefix;
	private String modelNamespaceFormat;
	
	private EnumSet<Ifc2RdfConversionOptionsEnum> conversionOptions;
	
	public Ifc2RdfConversionContext(ConfigurationItemEx configuration) {
		
		setOwlVersion(configuration.getProperties().getProperty(CONFIGURATION_PROPERTY_OWL_VERSION, "1.0.0"));
		
		String[] owlProfileNames = configuration.getProperties().getProperty(CONFIGURATION_PROPERTY_OWL_PROFILE).split(",");
		owlProfileList = new OwlProfileList(owlProfileNames);		
		
		name = configuration.getName();
		
		Properties properties = configuration.getProperties();
		
		conversionOptions = EnumSet.noneOf(Ifc2RdfConversionOptionsEnum.class);
		for (String propertyName : properties.stringPropertyNames()) {
			if (propertyName.startsWith(CONFIGURATION_PROPERTY_CONVERSION_OPTIONS_PREFIX) &&
					Boolean.parseBoolean(properties.getProperty(propertyName))) {
				String conversionOptionValue = propertyName.substring(CONFIGURATION_PROPERTY_CONVERSION_OPTIONS_PREFIX.length()); 
				conversionOptions.add(Ifc2RdfConversionOptionsEnum.valueOf(conversionOptionValue));
			}
		}
		
		ontologyPrefix = properties.getProperty(CONFIGURATION_PROPERTY_ONTOLOGY_PREFIX, Ifc2RdfVocabulary.DEFAULT_ONTOLOGY_PREFIX);
		ontologyNamespaceFormat = properties.getProperty(CONFIGURATION_PROPERTY_ONTOLOGY_NAMESPACE_FORMAT, Ifc2RdfVocabulary.DEFAULT_ONTOLOGY_NAMESPACE_FORMAT)
				.replaceAll(CONFIGURATION_NAMESPACE_FORMAT_VARIABLE_SCHEMA_VERSION, "%1s")
				.replaceAll(CONFIGURATION_VARIABLE_CONVERTER_CONTEXT_NAME, "%2s");
				
		modelPrefix = properties.getProperty(CONFIGURATION_PROPERTY_MODEL_PREFIX, Ifc2RdfVocabulary.DEFAULT_MODEL_PREFIX);
		modelNamespaceFormat = properties.getProperty(CONFIGURATION_PROPERTY_MODEL_NAMESPACE_FORMAT, Ifc2RdfVocabulary.DEFAULT_MODEL_NAMESPACE_FORMAT)
				.replaceAll(CONFIGURATION_NAMESPACE_FORMAT_VARIABLE_SCHEMA_VERSION, "%1s")
				.replaceAll(CONFIGURATION_VARIABLE_CONVERTER_CONTEXT_NAME, "%2s");		
	}
	
	public String getOwlVersion() {
		return owlVersion;
	}

	public void setOwlVersion(String owlVersion) {
		this.owlVersion = owlVersion;
	}

	public OwlProfileList getOwlProfileList() {
		return owlProfileList;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the ontologyPrefix
	 */
	public String getOntologyPrefix() {
		return ontologyPrefix;
	}
	
	/**
	 * @param ontologyPrefix the ontologyPrefix to set
	 */
	public void setOntologyPrefix(String ontologyPrefix) {
		this.ontologyPrefix = ontologyPrefix;
	}
	
	/**
	 * @return the ontologyNamespaceFormat
	 */
	public String getOntologyNamespaceFormat() {
		return ontologyNamespaceFormat;
	}
	
	/**
	 * @param ontologyNamespaceFormat the ontologyNamespaceFormat to set
	 */
	public void setOntologyNamespaceFormat(String ontologyNamespaceFormat) {
		this.ontologyNamespaceFormat = ontologyNamespaceFormat;
	}
	
	/**
	 * @return the modelPrefix
	 */
	public String getModelPrefix() {
		return modelPrefix;
	}
	
	/**
	 * @param modelPrefix the modelPrefix to set
	 */
	public void setModelPrefix(String modelPrefix) {
		this.modelPrefix = modelPrefix;
	}
	
	/**
	 * @return the modelNamespaceFormat
	 */
	public String getModelNamespaceFormat() {
		return modelNamespaceFormat;
	}
	
	/**
	 * @param modelNamespaceFormat the modelNamespaceFormat to set
	 */
	public void setModelNamespaceFormat(String modelNamespaceFormat) {
		this.modelNamespaceFormat = modelNamespaceFormat;
	}
	
	/**
	 * @return the conversionOptions
	 */
	public EnumSet<Ifc2RdfConversionOptionsEnum> getConversionOptions() {
		return conversionOptions;
	}

	/**
	 * @param conversionOptions the conversionOptions to set
	 */
	public void setConversionOptions(EnumSet<Ifc2RdfConversionOptionsEnum> conversionOptions) {
		this.conversionOptions = conversionOptions;
	}	
	
	public boolean isEnabledOption(Ifc2RdfConversionOptionsEnum conversionOption) {
		return conversionOptions.contains(conversionOption);
	}
	
}
