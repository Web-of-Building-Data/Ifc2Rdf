package net.linkedbuildingdata.ifc.convert.step2ifc;

import java.io.*;
import java.util.*;

import net.linkedbuildingdata.common.string.RegexUtils;
import net.linkedbuildingdata.common.string.StrBuilderWrapper;
import net.linkedbuildingdata.common.string.StringUtils;
import net.linkedbuildingdata.ifc.*;
import net.linkedbuildingdata.ifc.common.guid.Guid;
import net.linkedbuildingdata.ifc.common.guid.GuidCompressor;
import net.linkedbuildingdata.ifc.data.LogicalEnum;
import net.linkedbuildingdata.ifc.data.model.*;
import net.linkedbuildingdata.ifc.data.schema.*;

import org.apache.commons.lang3.text.StrMatcher;
import org.apache.log4j.Logger;


public class IfcModelParser extends IfcParser {

	private static final Logger logger = Logger.getLogger(IfcModelParser.class);	

	private IfcModel model;
	private IfcSchema schema;
	private Map<Long, IfcEntity> entityMap = new HashMap<>(); 	// map of entities indexed by line numbers
	
	private class IfcTemporaryCollectionValueWrapper extends IfcValue {
		
		private static final long serialVersionUID = 1L;

		private List<IfcValue> values;

		public IfcTemporaryCollectionValueWrapper(List<IfcValue> values) {
			this.values = values;
		}
		
		public List<IfcValue> getValues() {
			return values;
		}

		@Override
		public String toString() {
			return null;
		}
		
		@Override
		public Boolean isLiteralType() {
			Boolean isLiteralValue = null;
			for (IfcValue value : values) {
				Boolean b = value.isLiteralType(); 
				if (b != isLiteralValue && b != null) {
					assert isLiteralValue == null : "Mixed literal and link type: " + StringUtils.collectionToString(values, null, null, null, ",");
					isLiteralValue = b;
				}
			}
			return isLiteralValue;
		}

		@Override
		public boolean isNullOrAny() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean equals(Object other) {
			return false;
		}

	}
	

	protected IfcModelParser(InputStream input) {
		super(input);
	}

	public static IfcModel parse(InputStream input) throws IfcParserException {
		IfcModelParser parser = new IfcModelParser(input);
		return parser.parseModel();
	}

	private IfcModel parseModel() throws IfcParserException {

		try {

			String statement;

			//
			// detect the schema version info and get the schema from the schema pool
			//
			for (;;) {
				statement = getNextStatement();
				if (statement != null) {
					if (statement.startsWith(IfcVocabulary.ExpressFormat.FILE_SCHEMA)) {
						statement = statement.substring(statement.indexOf("("));
						String schemaVersion = statement.replaceAll("[\\(\\)\\s']", "");
						schema = IfcSchemaPool.getSchema(schemaVersion);
						break;
					}
				} else {
					throw new IfcFormatException(String.format("Expected '%s'", IfcVocabulary.ExpressFormat.FILE_SCHEMA));
				}
			}

			//
			// create a new model 
			//
			model = new IfcModel(schema.getVersion());

			//
			// parse entities
			//
			parseEntities();

			return model;

		} catch (IfcParserException e) {
			throw e;
		} catch (Exception e) {
			throw new IfcParserException(e);
		}
	}
	
	/**
	 * Reads line by line and creates new entities
	 * @throws IOException
	 * @throws IfcNotFoundException
	 * @throws IfcParserException
	 */
	private void parseEntities() throws IOException, IfcNotFoundException, IfcParserException {
		String statement;
		String[] tokens;
		//
		// getting entity headers
		//
		while ((statement = getNextStatement()) != null) {
		
			tokens = RegexUtils.split2(statement, IfcVocabulary.ExpressFormat.LINE_NUMBER);
			if (tokens.length != 2) {
				continue;
			}
		
			tokens = RegexUtils.split2(tokens[1], IfcVocabulary.ExpressFormat.EQUAL);
		
			//
			// create entity
			//
			long lineNumber = Long.parseLong(tokens[0].trim());
			IfcEntity entity = getEntity(lineNumber);
		
			//
			// set entity type
			//
			String entityAttributesString = tokens[1].trim();		
			int indexOfOpeningBracket = entityAttributesString.indexOf(StringUtils.OPENING_ROUND_BRACKET);		
			String entityTypeInfoName = entityAttributesString.substring(0, indexOfOpeningBracket);
			
			IfcEntityTypeInfo entityTypeInfo = schema.getEntityTypeInfo(entityTypeInfoName);			
			entity.setTypeInfo(entityTypeInfo);
			
			entityAttributesString = entityAttributesString.substring(indexOfOpeningBracket + 1,
					entityAttributesString.length() - 1);
			
			List<IfcAttributeInfo> attributeInfos = entityTypeInfo.getInheritedAttributeInfos();

			//
			// parse attribute string to get attribute values
			//
			List<IfcValue> attributeValues = parseAttributeValues(new StrBuilderWrapper(entityAttributesString), entity, attributeInfos, null);

			setEntityAttributeValues(entity, attributeInfos, attributeValues);
			
			//
			// add entity to the model
			//
			model.addEntity(entity);
		
			//
			// set IfcProject entity in the model
			//
			if (entityTypeInfoName.equalsIgnoreCase(IfcVocabulary.ExpressFormat.IFCPROJECT)) {
				model.setProjectEntity(entity);
			}	
		}
		
		for (IfcEntity entity : model.getEntities()) {
			entity.bindInverseLinks();
		}
	}

	/**
	 * Gets an entity from the map by its line number, or creates a new entity if it doesn't exist 
	 * @param lineNumber
	 * @return
	 */
	private IfcEntity getEntity(long lineNumber) {
		IfcEntity entity = entityMap.get(lineNumber);
		if (entity == null) {
			entity = new IfcEntity(null, lineNumber);
			entityMap.put(lineNumber, entity);
		}
		return entity;
	}

	/**
	 * Parses an entity's attribute string to get attribute values
	 * 
	 * @param attributeStringBuilder
	 * @param attributeValueType
	 * @return a single attribute value or list of attribute values
	 * @throws IfcFormatException
	 * @throws IfcNotFoundException
	 * @throws IfcValueTypeConflictException 
	 */
	private List<IfcValue> parseAttributeValues(StrBuilderWrapper attributeStrBuilderWrapper, IfcEntity entity,
			List<IfcAttributeInfo> entityAttributeInfos, EnumSet<IfcTypeEnum> commonValueTypes) throws IfcFormatException, IfcNotFoundException {

		logger.debug(String.format("Parsing entity '%s'", entity));			

		List<IfcValue> attributeValues = new ArrayList<>();
		
		for (int attributeIndex = 0; !attributeStrBuilderWrapper.trimLeft().isEmpty(); ++attributeIndex) {

			EnumSet<IfcTypeEnum> attributeValueTypes;
			IfcAttributeInfo attributeInfo;
			if (commonValueTypes == null) {
				attributeInfo = entityAttributeInfos.get(attributeIndex);
				IfcTypeInfo attributeTypeInfo = attributeInfo.getAttributeTypeInfo();
				attributeValueTypes = attributeTypeInfo.getValueTypes();
			} else {
				attributeInfo = entityAttributeInfos.get(0);
				attributeValueTypes = commonValueTypes;
			}

			switch (attributeStrBuilderWrapper.charAt(0)) {

			case IfcVocabulary.ExpressFormat.LINE_NUMBER_SYMBOL: // Entity
				attributeStrBuilderWrapper.skip(1);
				Long remoteLineNumber = attributeStrBuilderWrapper.getLong();
				IfcEntity remoteEntity = getEntity(remoteLineNumber);
				if (remoteEntity == null) {
					throw new IfcNotFoundException("Entity not found: #" + remoteLineNumber);
				}
				attributeValues.add(remoteEntity);
				break;

			case IfcVocabulary.ExpressFormat.STRING_VALUE_SYMBOL:
				String s = attributeStrBuilderWrapper.getStringBetweenSingleQuotes();
				assert attributeValueTypes.size() == 1 : "Expect attributeValueTypes.size() == 1"; 
				if (!attributeValueTypes.contains(IfcTypeEnum.GUID)) {
					attributeValues.add(new IfcLiteralValue(s, IfcTypeEnum.STRING));
					break;
				} else {
					Guid guid = new Guid();
					GuidCompressor.getGuidFromCompressedString(s, guid);
					attributeValues.add(new IfcLiteralValue(guid, IfcTypeEnum.GUID));
					break;
				}

			case IfcVocabulary.ExpressFormat.ENUMERATION_VALUE_SYMBOL:

				s = attributeStrBuilderWrapper.getStringBetweenSimilarCharacters(IfcVocabulary.ExpressFormat.ENUMERATION_VALUE_SYMBOL);

				assert attributeValueTypes.size() == 1 : "Expect attributeValueTypes.size() == 1"; 
				if (!attributeValueTypes.contains(IfcTypeEnum.LOGICAL)) {
					attributeValues.add(new IfcLiteralValue(s, IfcTypeEnum.ENUM));
				} else {
					switch (s) {
					case "T":
					case "TRUE":
						attributeValues.add(new IfcLiteralValue(LogicalEnum.TRUE, IfcTypeEnum.LOGICAL));
						break;
					case "F":
					case "FALSE":
						attributeValues.add(new IfcLiteralValue(LogicalEnum.FALSE, IfcTypeEnum.LOGICAL));
						break;
					default:
						attributeValues.add(new IfcLiteralValue(LogicalEnum.UNKNOWN, IfcTypeEnum.LOGICAL));
						break;

					}
				}
				break;

			case IfcVocabulary.ExpressFormat.NULL_SYMBOL: // $
				attributeValues.add(IfcValue.NULL);
				attributeStrBuilderWrapper.skip(1);
				break;

			case IfcVocabulary.ExpressFormat.ANY_SYMBOL: // *
				attributeValues.add(IfcValue.ANY);
				attributeStrBuilderWrapper.skip(1);
				break;

			case StringUtils.OPENING_ROUND_BRACKET_CHAR: // List or Set

				String stringBetweenBrackets = attributeStrBuilderWrapper.getStringBetweenRoundBrackets();

				StrBuilderWrapper sbWrapper = new StrBuilderWrapper(stringBetweenBrackets);
				
				List<IfcAttributeInfo> attributeInfos = new ArrayList<>(1);
				attributeInfos.add(attributeInfo);

				List<IfcValue> values = parseAttributeValues(sbWrapper, null, attributeInfos, attributeValueTypes);
				attributeValues.add(new IfcTemporaryCollectionValueWrapper(values));
				break;

			default:

				if (Character.isAlphabetic(attributeStrBuilderWrapper.charAt(0))) {

					// 
					// parsing sub entity
					//
					String subEntityTypeInfoName = attributeStrBuilderWrapper.getIdentifierName();
					IfcDefinedTypeInfo subDefinedTypeInfo = schema.getDefinedTypeInfo(subEntityTypeInfoName);
					attributeValueTypes = subDefinedTypeInfo.getValueTypes();
					s = attributeStrBuilderWrapper.getStringBetweenRoundBrackets();

					attributeInfos = new ArrayList<>(1);
					attributeInfos.add(attributeInfo);

					values = parseAttributeValues(new StrBuilderWrapper(s), null, attributeInfos, attributeValueTypes);
					assert values.size() == 1 : "Expect only 1 argument: " + entity;
					attributeValues.add(new IfcShortEntity(subDefinedTypeInfo, (IfcLiteralValue)values.get(0)));
				} else {
					
					//
					// parsing number or datetime
					//
					assert attributeValueTypes.size() == 1 : "Expect attributeValueTypes.size() == 1";
					IfcTypeEnum attributeValueType = (IfcTypeEnum)attributeValueTypes.toArray()[0];
					Object value;
					if (attributeValueType == IfcTypeEnum.INTEGER) {
						value = attributeStrBuilderWrapper.getLong();
					} else if (attributeValueType == IfcTypeEnum.REAL) {
						value = attributeStrBuilderWrapper.getDouble();
					} else {
						assert attributeValueType == IfcTypeEnum.DATETIME;
						long timeStamp = attributeStrBuilderWrapper.getLong();
						value = new Date(timeStamp * 1000);
					}
					
					attributeValues.add(new IfcLiteralValue(value, attributeValueType));						
				}

				break;
			}
			
			attributeStrBuilderWrapper.trimLeft();
			attributeStrBuilderWrapper.getFirstMatch(StrMatcher.commaMatcher());
		}

		return attributeValues;
	}
	
	/**
	 * Set entity attribute values
	 * @param entity
	 * @param attributeInfos
	 * @param attributeValues
	 * @throws IfcParserException
	 */
	private void setEntityAttributeValues(IfcEntity entity, List<IfcAttributeInfo> attributeInfos, List<IfcValue> attributeValues) throws IfcParserException {
		try {
			
			if (attributeValues.size() == attributeInfos.size()) {
				boolean isLiteralValueContainer = true;
				for (int attributeIndex = 0; attributeIndex < attributeValues.size(); ++attributeIndex) {
					
					IfcAttributeInfo attributeInfo = attributeInfos.get(attributeIndex);
					IfcValue attributeValue = attributeValues.get(attributeIndex);
					
					Boolean isLiteralValue = attributeValue.isLiteralType();
					
					if (isLiteralValue != null) {
						isLiteralValueContainer = isLiteralValueContainer && isLiteralValue == Boolean.TRUE;							
						if (isLiteralValue) {
						
							if (attributeValue instanceof IfcTemporaryCollectionValueWrapper) {
								
								if (attributeInfo.isSortedList()) {
									
									IfcLiteralValueList values = new IfcLiteralValueList();										
									for (IfcValue value : ((IfcTemporaryCollectionValueWrapper)attributeValue).getValues()) {
										values.add((IfcLiteralValue)value);
									}
									
									entity.addLiteralAttribute(new IfcLiteralAttribute(attributeInfo, attributeIndex, values));
									
								} else {										
									
									for (IfcValue value : ((IfcTemporaryCollectionValueWrapper)attributeValue).getValues()) {
										entity.addLiteralAttribute(new IfcLiteralAttribute(attributeInfo, attributeIndex, value));
									}										
									
								}
								
							} else {
								
								assert(attributeValue instanceof IfcLiteralValue) :
									String.format("Object is not a literal value, line number: %d, attributeInfo: %s, attribute value: %s, value type: %s",
											entity.getLineNumber(), attributeInfo.getName(), attributeValue, attributeInfo.getAttributeTypeInfo().getValueTypes()); 
								entity.addLiteralAttribute(new IfcLiteralAttribute(attributeInfo, attributeIndex, attributeValue));
								
							}
							
						} else { // attributeInfo instanceof IfcLinkInfo								
							
							if (attributeValue instanceof IfcTemporaryCollectionValueWrapper) {
								
								if (attributeInfo.isSortedList()) {
									
									IfcEntityList destinations = new IfcEntityList();										
									for (IfcValue destination : ((IfcTemporaryCollectionValueWrapper)attributeValue).getValues()) {
										destinations.add((IfcEntity)destination);
									}
									
									entity.addOutgoingLink(new IfcLink((IfcLinkInfo)attributeInfo, attributeIndex, entity, destinations));
									
								} else {										
									
									for (IfcValue destination : ((IfcTemporaryCollectionValueWrapper)attributeValue).getValues()) {
										IfcLink link = new IfcLink((IfcLinkInfo)attributeInfo, attributeIndex, entity, (IfcEntityBase)destination);
										entity.addOutgoingLink(link);
									}										
									
								}
								
							} else {
								
								assert (attributeValue instanceof IfcEntityBase) :
									String.format("Object is not an entity, line number: %d, attributeInfo: %s, attribute value: %s, value type: %s",
											entity.getLineNumber(), attributeInfo.getName(), attributeValue, attributeInfo.getAttributeTypeInfo().getValueTypes());
								IfcLink link = new IfcLink((IfcLinkInfo)attributeInfo, attributeIndex, entity, (IfcEntityBase)attributeValue);
								entity.addOutgoingLink(link);
								
							}
						}
					}
					
				}						
				entity.setLiteralValueContainer(isLiteralValueContainer);
				
			} else {
				throw new IfcParserException(String.format("Expected %d attributes, but %d were found",
						entity.getTypeInfo().getName(), attributeInfos.size(), attributeValues.size()));						
			}

		} catch (Exception e) {
			throw new IfcParserException(String.format("Error parsing entity %s: %s", entity.toString(),
					e.getMessage()), e);
		}
	}
		
	
//	private static IfcLiteralTypeInfo getLiteralTypeInfo(IfcAttributeInfo attributeInfo) {
//		IfcTypeInfo typeInfo = attributeInfo.getAttributeTypeInfo();				
//
//		if (typeInfo instanceof IfcCollectionTypeInfo) {
//			typeInfo = ((IfcCollectionTypeInfo)typeInfo).getItemTypeInfo();
//		}
//		
//		while (typeInfo instanceof IfcRedirectTypeInfo) {
//			typeInfo = ((IfcRedirectTypeInfo)typeInfo).getRedirectTypeInfo();
//		}
//		
//		if (typeInfo instanceof IfcLiteralTypeInfo) {
//			return (IfcLiteralTypeInfo)typeInfo;			
//		} else {
//			return null;
//		}
//	}
	
	
	
	/**
	 * Gets the next statement from the input stream   
	 * @return Tokens of the next statement in the file (without semicolon)
	 * @throws IOException
	 * @throws IfcFormatException
	 */
//	@Override
//	protected String getNextStatement() throws IOException, IfcFormatException {
//		String line;
//		while ((line = reader.readLine()) != null) {
//			if (line.endsWith(StringUtils.SEMICOLON)) {
//				return line.substring(0, line.length()-1);
//			} else if (!line.isEmpty()) {
//				throw new IfcFormatException(String.format("Line is not ended with '%s'", StringUtils.SEMICOLON));
//			}
//		}
//		return line;		
//	}

}
