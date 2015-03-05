package net.linkedbuildingdata.ifc.util;

import java.util.*;

import net.linkedbuildingdata.common.config.ComplexProcessorConfiguration;
import net.linkedbuildingdata.common.config.ComplexProcessorConfigurationPool;
import net.linkedbuildingdata.common.config.ProcessorConfiguration;
import net.linkedbuildingdata.common.config.document.ConfigurationParserException;
import net.linkedbuildingdata.ifc.IfcException;
import net.linkedbuildingdata.ifc.data.model.*;
import net.linkedbuildingdata.ifc.data.schema.IfcEntityTypeInfo;
import net.linkedbuildingdata.ifc.data.schema.IfcSchema;
import net.linkedbuildingdata.ifc.util.grounding.IfcGroundingMainProcessor;
import net.linkedbuildingdata.ifc.util.grounding.config.IfcGroundingProcessorPoolConfigurationSection;

import org.apache.log4j.Logger;


public class IfcModelAnalyser {

	private static final Logger logger = Logger.getLogger(IfcModelAnalyser.class);	

	private IfcModel model;
	private IfcSchema schema;
	private List<IfcEntity> namedEntities = new ArrayList<>();
	private Map<IfcEntityTypeInfo, List<IfcEntity>> typeEntityDictionary;

	public IfcModelAnalyser(IfcModel model) {

		this.model = model;
		schema = model.getSchema();

		for (IfcEntity entity : model.getEntities()) {
			if (entity.hasName()) {
				namedEntities.add(entity);
			}
		}
	}

	public IfcModel getModel() {
		return model;
	}
	
	public IfcSchema getSchema() {
		return schema;
	}
	
	public List<IfcEntity> getNamedEntities() {
		return namedEntities;
	}
	
	public static ComplexProcessorConfiguration getDefaultGroundingRuleSets() throws ConfigurationParserException {
		
		IfcGroundingProcessorPoolConfigurationSection groundingProcessorPoolConfigurationSection =
				IfcGroundingProcessorPoolConfigurationSection.getInstance();
		
		ComplexProcessorConfigurationPool groundingComplexProcessorConfigurationPool =
				groundingProcessorPoolConfigurationSection.getComplexProcessorConfigurationPool();
		
		ComplexProcessorConfiguration groundingComplexProcessorConfiguration =
				groundingComplexProcessorConfigurationPool.getDefault();
		
		return groundingComplexProcessorConfiguration;
	}	

	public void groundNodes(ComplexProcessorConfiguration groundingProcessorConfigurations) throws IfcException {		
		logger.debug(String.format("Applying set of grounding rules '%s'", groundingProcessorConfigurations.getName()));
		IfcGroundingMainProcessor processor = new IfcGroundingMainProcessor(this);
		processor.process(groundingProcessorConfigurations.getProcessorConfigurations());
		logger.debug("Applying set of grounding rules is completed");
	}
	
	public Map<IfcEntityTypeInfo, List<IfcEntity>> getTypeEntityDictionary() {
		if (typeEntityDictionary == null) {
			typeEntityDictionary = new TreeMap<IfcEntityTypeInfo, List<IfcEntity>>();
			for (IfcEntity entity : model.getEntities()) {
				IfcEntityTypeInfo typeInfo = entity.getTypeInfo();
				List<IfcEntity> typeEntityList = typeEntityDictionary.get(typeInfo);
				if (typeEntityList == null) {
					typeEntityList = new ArrayList<>();
					typeEntityDictionary.put(typeInfo, typeEntityList);
				}
				typeEntityList.add(entity);
			}		
		}
		return typeEntityDictionary;
	}
	
	public List<IfcEntity> getEntitiesByType(IfcEntityTypeInfo entityTypeInfo, boolean checkSubtypes) {
		List<IfcEntity> typeEntityList = getTypeEntityDictionary().get(entityTypeInfo);
		List<IfcEntity> result = new ArrayList<>();
		if (typeEntityList != null) {
			result.addAll(typeEntityList);
		}
		
		if (checkSubtypes) {
			List<IfcEntityTypeInfo> subtypes = entityTypeInfo.getSubTypeInfos();
			if (subtypes != null) {
				for (IfcEntityTypeInfo subtype : subtypes) {
					typeEntityList = getEntitiesByType(subtype, checkSubtypes);
					if (typeEntityList != null) {
						result.addAll(typeEntityList);
					}
				}
			}
		}
		
		return result;
	}

//	public List<IfcEntity> getEntitiesByType(String entityTypeInfoName, boolean checkSubtypes) throws IfcNotFoundException {
//		IfcEntityTypeInfo entityTypeInfo = model.getSchema().getEntityTypeInfo(entityTypeInfoName);
//		return getEntitiesByType(entityTypeInfo, checkSubtypes);
//	}
}
