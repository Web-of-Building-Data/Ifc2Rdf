package fi.hut.cs.drumbeat.ifc.convert.ifc2ld;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import fi.hut.cs.drumbeat.ifc.common.IfcException;
import fi.hut.cs.drumbeat.ifc.data.LogicalEnum;
import fi.hut.cs.drumbeat.ifc.data.model.*;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcAttributeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcTypeEnum;
import fi.hut.cs.drumbeat.rdf.RdfVocabulary;
import fi.hut.cs.drumbeat.rdf.RdfVocabulary.OLO;
import fi.hut.cs.drumbeat.rdf.export.RdfExportAdapter;


public class Ifc2RdfModelExporter extends Ifc2RdfExporterBase {
	
	private IfcSchema ifcSchema;
	private IfcModel ifcModel;
	private Model jenaModel;
	
	private Ifc2RdfConversionContext context;
	private RdfExportAdapter adapter;
	
	private boolean convertEnumerationValueToString;
	private boolean convertBooleanValueToString;
	
	public Ifc2RdfModelExporter(IfcModel ifcModel, Ifc2RdfConversionContext context, RdfExportAdapter rdfExportAdapter) {
		super(context, rdfExportAdapter);
		
		this.ifcSchema = ifcModel.getSchema();
		this.ifcModel = ifcModel;		
		this.context = context;
		this.jenaModel = getJenaModel();
		adapter = rdfExportAdapter;
		
		String ontologyNamespacePrefix = context.getOntologyPrefix();
		String ontologyNamespaceUri = String.format(context.getOntologyNamespaceFormat(), ifcSchema.getVersion(), context.getName());

		String modelNamespacePrefix = context.getModelPrefix();
		String modelNamespaceUri = String.format(context.getModelNamespaceFormat(), ifcSchema.getVersion(), context.getName());
		
		super.setOntologyNamespacePrefix(ontologyNamespacePrefix);
		super.setOntologyNamespaceUri(ontologyNamespaceUri);
		super.setModelNamespacePrefix(modelNamespacePrefix);
		super.setModelNamespaceUri(modelNamespaceUri);
		
		convertEnumerationValueToString = context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ForceConvertEnumerationValuesToString);
		convertBooleanValueToString = context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ForceConvertBooleanValuesToString);		
	}
	
	public Model export() throws IfcException {
		
		//
		// write header and prefixes
		//
		adapter.startExport();
		
		adapter.setNamespacePrefix(RdfVocabulary.OWL.BASE_PREFIX, OWL.getURI());
		adapter.setNamespacePrefix(RdfVocabulary.RDF.BASE_PREFIX, RDF.getURI());
		adapter.setNamespacePrefix(RdfVocabulary.RDFS.BASE_PREFIX, RDFS.getURI());
		adapter.setNamespacePrefix(RdfVocabulary.XSD.BASE_PREFIX, XSD.getURI());	
		
		
		if (context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ForceConvertRdfListToOloOrderedList)) {
			adapter.setNamespacePrefix(RdfVocabulary.OLO.BASE_PREFIX, RdfVocabulary.OLO.BASE_URI);
		}
		
		adapter.setNamespacePrefix(getOntologyNamespacePrefix(), getOntologyNamespaceUri());
		adapter.exportEmptyLine();
		
		adapter.setNamespacePrefix(getModelNamespacePrefix(), getModelNamespaceUri());
		adapter.exportEmptyLine();

		String conversionOptionsString = context.getConversionOptions().toString()
				.replaceFirst("\\[", "[\r\n\t\t\t ")
				.replaceFirst("\\]", "\r\n\t\t]")
				.replaceAll(",", "\r\n\t\t\t");
		
		conversionOptionsString = String.format("OWL profile: %s.\r\n\t\tConversion options: %s",
				context.getOwlProfileId(),
				conversionOptionsString); 
		
		adapter.exportOntologyHeader(getModelNamespaceUri(), "1.0", conversionOptionsString);		
		
		for (IfcEntity entity : ifcModel.getEntities()) {
			writeEntity(entity);
		}
		
		adapter.endExport();
		
		return jenaModel;
	}
	
	private void writeEntity(IfcEntity entity) {
		if (entity.isDuplicated()) {
			return;
		}
		
		Resource entityResource = convertEntityToResource(entity);
		
		IfcEntityTypeInfo entityTypeInfo = entity.getTypeInfo();		
		entityResource.addProperty(RDF.type, createUriResource(super.formatTypeName(entityTypeInfo)));
		
		for (IfcLink link : entity.getOutgoingLinks()) {
			writeAttribute(entityResource, link);
		}
		
		for (IfcLiteralAttribute attribute : entity.getLiteralAttributes()) {
			writeAttribute(entityResource, attribute);
		}
		
		if (context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ExportDebugInfo)) {
		
//			if (entity.isLiteralValueContainer()) {
//				adapter.exportTriple(entityResource, RDF.type, super.createUriResource(
//						super.formatOntologyName(Ifc2RdfVocabulary.IFC.LITERAL_VALUE_CONTAINER_ENTITY)));
//			}
			
//			if (entity.isSharedBlankNode()) {
//				adapter.exportTriple(
//						entityResource,
//						RDF.type,
//						super.createUriResource(super.formatOntologyName(Ifc2RdfVocabulary.IFC.SUPER_ENTITY)));				
//			}		
//			
//			
//			if (entity.hasName()) {
//				String entityRawName = entity.getRawName();
//				if (!entityRawName.equals(entity.getName())) {
//					adapter.exportTriple(
//							entityResource,
//							super.createUriResource(super.formatOntologyName(Ifc2RdfVocabulary.IFC.RAW_NAME)).as(Property.class),
//									jenaModel.createTypedLiteral(entityRawName));
//				}
//			}
			
			String debugMessage = entity.getDebugMessage();
			if (debugMessage != null) {
				adapter.exportTriple(
				entityResource,
				super.createUriResource(super.formatOntologyName(Ifc2RdfVocabulary.IFC.PROPERTY_DEBUG_MESSAGE)).as(Property.class),
						jenaModel.createTypedLiteral(debugMessage));				
			}
			
			adapter.exportTriple(
					entityResource,
					super.createUriResource(super.formatOntologyName(Ifc2RdfVocabulary.IFC.PROPERTY_LINE_NUMBER_PROPERTY)).as(Property.class),
					jenaModel.createTypedLiteral(entity.getLineNumber()));
		}		

		adapter.exportEmptyLine();
	}
	
	private void writeAttribute(Resource entityResource, IfcAttribute attribute) {		
		IfcAttributeInfo attributeInfo = attribute.getAttributeInfo();
		Property attributeResource = convertAttributeInfoToResource(attributeInfo);
		IfcValue value = attribute.getValue();
		adapter.exportTriple(entityResource, attributeResource, convertValueToNode(value));
	}
	
	public RDFNode convertValueToNode(IfcValue value) {
		if (value instanceof IfcCollectionValue) {
			return convertListToResource((IfcCollectionValue<?>) value);
		} else {
			if (value instanceof IfcEntityBase) {
				return convertEntityToResource((IfcEntityBase) value);
			} else {
				return convertLiteralToNode((IfcLiteralValue) value);
			}
		}
	}
	
	public Resource convertListToResource(IfcCollectionValue<? extends IfcValue> listValue) {
		List<RDFNode> nodeList = new ArrayList<>();
		for (IfcValue value : listValue.getSingleValues()) {
			nodeList.add(convertValueToNode(value));
		}
		
		if (context.isEnabledOption(Ifc2RdfConversionOptionsEnum.ForceConvertRdfListToOloOrderedList)) {
			
			int length = nodeList.size();
			
			Resource listResource = super.createAnonResource();
			listResource.addProperty(RDF.type, OLO.OrderedList);
			listResource.addProperty(OLO.length, jenaModel.createTypedLiteral(length));
			
			for (int i = 0; i < nodeList.size(); ++i) {
				Resource slotResource = super.createAnonResource();
				slotResource.addProperty(OLO.index, jenaModel.createTypedLiteral(i + 1));
				slotResource.addProperty(OLO.item, nodeList.get(i));
				listResource.addProperty(OLO.slot, slotResource);
			}
			
			return listResource;
			
		} else {
			return super.createList(nodeList);
		}
	}
	
	public Resource convertEntityToResource(IfcEntityBase value) {
		if (value instanceof IfcEntity) {
			IfcEntity entity = (IfcEntity)value;
			if (entity.hasName()) {
				return super.createUriResource(super.formatModelName(entity.getName()));
			} else {
				return super.createAnonResource(String.format(Ifc2RdfVocabulary.IFC.BLANK_NODE_ENTITY_URI_FORMAT, entity.getLineNumber()));
			}
		} else { // entityBase instanceof IfcShortEntity
			Resource resource = super.createAnonResource();
			resource.addProperty(RDF.type, createUriResource(super.formatTypeName(value.getTypeInfo())));
			IfcLiteralValue literalValue = ((IfcShortEntity)value).getValue();
			resource.addProperty(RDF.value, convertLiteralToNode(literalValue));
			return resource;
		}
	}
	
	public Property convertAttributeInfoToResource(IfcAttributeInfo attributeInfo) {
		return super.createUriResource(super.formatAttributeName(attributeInfo)).as(Property.class);		
	}

	public RDFNode convertLiteralToNode(IfcLiteralValue literalValue) {
		
		IfcTypeEnum valueType = literalValue.getValueType();
		
		if (valueType == IfcTypeEnum.STRING) {
			
			return jenaModel.createTypedLiteral((String)literalValue.getValue());
			
		} else if (valueType == IfcTypeEnum.GUID) {
			
			return jenaModel.createTypedLiteral(literalValue.getValue().toString());
			
		} else if (valueType == IfcTypeEnum.ENUM) {
			
			if (convertEnumerationValueToString) {
				return jenaModel.createTypedLiteral(literalValue.getValue().toString());
			} else {
				return super.createUriResource(super.formatOntologyName(literalValue.getValue().toString()));
			}
			
		} else if (valueType == IfcTypeEnum.REAL) {
			
			return jenaModel.createTypedLiteral((double)literalValue.getValue());
			
		} else if (valueType == IfcTypeEnum.INTEGER) {
			
			return jenaModel.createTypedLiteral((long)literalValue.getValue());
			
		} else if (valueType == IfcTypeEnum.LOGICAL) {
			
			if (convertBooleanValueToString) {
				return jenaModel.createTypedLiteral((literalValue.getValue().toString()));				
			} else {			
				LogicalEnum logicalValue = (LogicalEnum)literalValue.getValue();
				if (logicalValue == LogicalEnum.TRUE) {
					return jenaModel.createTypedLiteral(true);
				} else if (logicalValue == LogicalEnum.FALSE) {
					return jenaModel.createTypedLiteral(false);					
				} else {
					assert(logicalValue != LogicalEnum.UNKNOWN) : logicalValue.toString();
					throw new RuntimeException(logicalValue.toString());
				}
			}
			
		} else {
			
			assert (valueType == IfcTypeEnum.DATETIME) : "Expected: (valueType == IfcTypeEnum.DATETIME)";
			return jenaModel.createTypedLiteral((Calendar)literalValue.getValue());
			
		}			
			
	}
	
}
