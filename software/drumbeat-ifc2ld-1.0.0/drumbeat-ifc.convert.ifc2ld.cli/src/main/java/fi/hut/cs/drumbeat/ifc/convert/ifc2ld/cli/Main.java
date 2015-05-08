package fi.hut.cs.drumbeat.ifc.convert.ifc2ld.cli;

import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

import fi.hut.cs.drumbeat.common.cli.DrumbeatOption;
import fi.hut.cs.drumbeat.common.cli.DrumbeatOptionComparator;
import fi.hut.cs.drumbeat.ifc.common.IfcCommandLineOptions;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//
		// Define command line options 
		//
		Options options = createCommandLineOptions();

		CommandLineParser commandParser = new PosixParser();
		
		Ifc2RdfExporter exporter;
		
		try {
			CommandLine commandLine = commandParser.parse(options, args);
			
			Ifc2RdfExporter.init(
				commandLine.getOptionValue(IfcCommandLineOptions.LOGGER_CONFIG_FILE_SHORT),
				commandLine.getOptionValue(IfcCommandLineOptions.CONFIG_FILE_SHORT));
			
			exporter = new Ifc2RdfExporter(
				commandLine.getOptionValue(IfcCommandLineOptions.INPUT_SCHEMA_FILE_SHORT),
				commandLine.getOptionValue(IfcCommandLineOptions.INPUT_MODEL_FILE_1_SHORT),			
				commandLine.getOptionValue(IfcCommandLineOptions.OUTPUT_LAYER_NAME_SHORT),			
				commandLine.getOptionValue(IfcCommandLineOptions.OUTPUT_SCHEMA_FILE_SHORT),			
				commandLine.getOptionValue(IfcCommandLineOptions.OUTPUT_SCHEMA_NAME_SHORT),			
				commandLine.getOptionValue(IfcCommandLineOptions.OUTPUT_MODEL_FILE_1_SHORT),			
				commandLine.getOptionValue(IfcCommandLineOptions.OUTPUT_MODEL_NAME_1_SHORT),
				commandLine.getOptionValue(IfcCommandLineOptions.OUTPUT_META_MODEL_FILE_1_SHORT),			
				commandLine.getOptionValue(IfcCommandLineOptions.OUTPUT_META_MODEL_NAME_1_SHORT),
				commandLine.getOptionValue(IfcCommandLineOptions.OUTPUT_FILE_FORMAT_SHORT, IfcCommandLineOptions.OUTPUT_FILE_FORMAT_DEFAULT));
			
		} catch (Exception e) {
			
			Options helpOptions = createHelpOptions();			
			
			try {				
				commandParser.parse(helpOptions, args);				
			} catch (ParseException e2) {
				// print original error
				System.out.printf("Unexpected error: %s%n%n", e.getMessage());
			}
			
			printHelp(options, helpOptions);
			
			return;
		}
		
		try {
			exporter.run();
		} catch (Throwable e) {
			Logger logger = Logger.getRootLogger();
			if (logger != null) {
				logger.error("Unexpected error: " + e.getMessage(), e);
			} else {
				e.printStackTrace();
				while ((e = e.getCause()) != null) {
					e.printStackTrace();
				}	
			}
		}
		
	}

	private static Options createCommandLineOptions() {
		
		int index = 0;
		Option option;
		
		Options options = new Options();
		
		// option --log-properties-file|-lf <file>
		option = new DrumbeatOption(
				++index,
				IfcCommandLineOptions.LOGGER_CONFIG_FILE_SHORT,
				IfcCommandLineOptions.LOGGER_CONFIG_FILE_LONG,
				true,
				IfcCommandLineOptions.LOGGER_CONFIG_FILE_DESCRIPTION);
		option.setArgName(IfcCommandLineOptions.ARG_FILE);
		option.setRequired(true);
		options.addOption(option);
		
		// option --config-file|-cf <file>
		option = new DrumbeatOption(
				++index,
				IfcCommandLineOptions.CONFIG_FILE_SHORT,
				IfcCommandLineOptions.CONFIG_FILE_LONG,
				true,
				IfcCommandLineOptions.CONFIG_FILE_DESCRIPTION);
		option.setArgName(IfcCommandLineOptions.ARG_FILE);
		option.setRequired(true);
		options.addOption(option);
		
		// option --output-layer-name|-oln <name>
		option = new DrumbeatOption(
				++index,
				IfcCommandLineOptions.OUTPUT_LAYER_NAME_SHORT,
				IfcCommandLineOptions.OUTPUT_LAYER_NAME_LONG,
				true,
				IfcCommandLineOptions.OUTPUT_LAYER_NAME_DESCRIPTION);
		option.setArgName(IfcCommandLineOptions.ARG_FILE);
		option.setRequired(true);
		options.addOption(option);		

		// option --input-schema-file|-isf <file>
		option = new DrumbeatOption(
				++index,
				IfcCommandLineOptions.INPUT_SCHEMA_FILE_SHORT,
				IfcCommandLineOptions.INPUT_SCHEMA_FILE_LONG,
				true,
				IfcCommandLineOptions.INPUT_SCHEMA_FILE_DESCRIPTION);
		option.setArgName(IfcCommandLineOptions.ARG_FILE);
		option.setRequired(true);
		options.addOption(option);
		
		// option --input-model-file|-imf <file>
		option = new DrumbeatOption(
				++index,
				IfcCommandLineOptions.INPUT_MODEL_FILE_1_SHORT,
				IfcCommandLineOptions.INPUT_MODEL_FILE_1_LONG,
				true,
				IfcCommandLineOptions.INPUT_MODEL_FILE_1_DESCRIPTION);
		option.setArgName(IfcCommandLineOptions.ARG_FILE);
		option.setRequired(false);
		options.addOption(option);		
		
		OptionGroup optionGroup = new OptionGroup();
		

		// option --output-schema-file|-osf <name>
		option = new DrumbeatOption(
				++index,
				IfcCommandLineOptions.OUTPUT_SCHEMA_NAME_SHORT,
				IfcCommandLineOptions.OUTPUT_SCHEMA_NAME_LONG,
				true,
				IfcCommandLineOptions.OUTPUT_SCHEMA_NAME_DESCRIPTION + IfcCommandLineOptions.SUFFIX_OPTIONAL);
		option.setArgName(IfcCommandLineOptions.ARG_NAME);
		optionGroup.addOption(option);
		
		// option --output-schema-file|-osf <file>
		option = new DrumbeatOption(
				++index,
				IfcCommandLineOptions.OUTPUT_SCHEMA_FILE_SHORT,
				IfcCommandLineOptions.OUTPUT_SCHEMA_FILE_LONG,
				true,
				IfcCommandLineOptions.OUTPUT_SCHEMA_FILE_DESCRIPTION + IfcCommandLineOptions.SUFFIX_OPTIONAL);
		option.setArgName(IfcCommandLineOptions.ARG_FILE);
		optionGroup.addOption(option);


		optionGroup.setRequired(false);		
		options.addOptionGroup(optionGroup);
		
		
		
		optionGroup = new OptionGroup();

		// option --output-model-name|-omn <name>
		option = new DrumbeatOption(
				++index,
				IfcCommandLineOptions.OUTPUT_MODEL_NAME_1_SHORT,
				IfcCommandLineOptions.OUTPUT_MODEL_NAME_1_LONG,
				true,
				IfcCommandLineOptions.OUTPUT_MODEL_NAME_1_DESCRIPTION  + IfcCommandLineOptions.SUFFIX_OPTIONAL);
		option.setArgName(IfcCommandLineOptions.ARG_NAME);
		optionGroup.addOption(option);
		
		// option --output-model-file|-omf <file>
		option = new DrumbeatOption(
				++index,
				IfcCommandLineOptions.OUTPUT_MODEL_FILE_1_SHORT,
				IfcCommandLineOptions.OUTPUT_MODEL_FILE_1_LONG,
				true,
				IfcCommandLineOptions.OUTPUT_MODEL_FILE_1_DESCRIPTION  + IfcCommandLineOptions.SUFFIX_OPTIONAL);
		option.setArgName(IfcCommandLineOptions.ARG_FILE);
		optionGroup.addOption(option);		
		
		optionGroup.setRequired(false);		
		options.addOptionGroup(optionGroup);
		
		
		
		
		optionGroup = new OptionGroup();

		// option --output-meta-model-name|-ommn <name>
		option = new DrumbeatOption(
				++index,
				IfcCommandLineOptions.OUTPUT_META_MODEL_NAME_1_SHORT,
				IfcCommandLineOptions.OUTPUT_META_MODEL_NAME_1_LONG,
				true,
				IfcCommandLineOptions.OUTPUT_META_MODEL_NAME_1_DESCRIPTION  + IfcCommandLineOptions.SUFFIX_OPTIONAL);
		option.setArgName(IfcCommandLineOptions.ARG_NAME);
		optionGroup.addOption(option);
		
		// option --output-meta-model-file|-ommf <file>
		option = new DrumbeatOption(
				++index,
				IfcCommandLineOptions.OUTPUT_META_MODEL_FILE_1_SHORT,
				IfcCommandLineOptions.OUTPUT_META_MODEL_FILE_1_LONG,
				true,
				IfcCommandLineOptions.OUTPUT_META_MODEL_FILE_1_DESCRIPTION  + IfcCommandLineOptions.SUFFIX_OPTIONAL);
		option.setArgName(IfcCommandLineOptions.ARG_FILE);
		optionGroup.addOption(option);
		
		optionGroup.setRequired(false);		
		options.addOptionGroup(optionGroup);
		
		

		
		// option --config-file|-cf <file>
		option = new DrumbeatOption(
				++index,
				IfcCommandLineOptions.OUTPUT_FILE_FORMAT_SHORT,
				IfcCommandLineOptions.OUTPUT_FILE_FORMAT_LONG,
				true,
				IfcCommandLineOptions.OUTPUT_FILE_FORMAT_DESCRIPTION);
		option.setArgName(IfcCommandLineOptions.ARG_NAME);
		option.setRequired(false);
		options.addOption(option);
		return options;
	}
	
	private static Options createHelpOptions() {
		Options options = new Options();
		Option option = new Option(
				IfcCommandLineOptions.HELP_SHORT,
				IfcCommandLineOptions.HELP_LONG,
				false,
				IfcCommandLineOptions.HELP_DESCRIPTION);
		option.setRequired(true);
		options.addOption(option);
		return options;
	}
	
	private static void printHelp(Options options, Options helpOptions) {		
		HelpFormatter helpFormatter = new HelpFormatter();
		String className = Main.class.getName();
		helpFormatter.printHelp(className, helpOptions, true);
		helpFormatter.setOptionComparator(new DrumbeatOptionComparator());
		helpFormatter.printHelp(IfcCommandLineOptions.FORMATTER_WIDTH, className, null, options, null, true);
	}

	
}
