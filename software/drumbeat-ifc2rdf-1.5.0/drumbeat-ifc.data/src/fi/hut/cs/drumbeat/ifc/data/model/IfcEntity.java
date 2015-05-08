package fi.hut.cs.drumbeat.ifc.data.model;

import java.util.*;

import fi.hut.cs.drumbeat.common.collections.IteratorComparer;
import fi.hut.cs.drumbeat.common.string.StringUtils;
import fi.hut.cs.drumbeat.ifc.data.schema.*;


public class IfcEntity extends IfcEntityBase implements Comparable<IfcEntity> { // , IRdfEntityNode {

	private static final long serialVersionUID = 1L;

	private IfcEntityTypeInfo typeInfo;
	private long lineNumber;
	private String name;
	private String rawName;
	private IfcAttributeList<IfcLiteralAttribute> literalAttributes = new IfcAttributeList<>();
	private IfcAttributeList<IfcLink> outgoingLinks = new IfcAttributeList<>();
	private IfcAttributeList<IfcLink> incomingLinks = new IfcAttributeList<>();
	private List<IfcUniqueKeyValue> uniqueKeyValues;
	private IfcEntity parent;
	private boolean isSharedBlankNode;
	private boolean isLiteralValueContainer;
	private IfcEntity sameAsEntity;
	private String debugMessage;
	
//	private transient List<IRdfLink> links;
	
	public IfcEntity(long lineNumber) {
		this.lineNumber = lineNumber;
	}

	public IfcEntity(IfcEntityTypeInfo typeInfo, long lineNumber) {
		this(lineNumber);
		this.typeInfo = typeInfo;
	}

	public IfcEntityTypeInfo getTypeInfo() {
		return typeInfo;
	}

	public void setTypeInfo(IfcEntityTypeInfo typeInfo) {
		this.typeInfo = typeInfo;
	}


	public long getLineNumber() {
		return lineNumber;
	}

	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public boolean hasName() {
		return name != null;
	}

	/**
	 * @return the rawName
	 */
	public String getRawName() {
		return rawName != null ? rawName : name;
	}

	/**
	 * @param rawName the rawName to set
	 */
	public void setRawName(String rawName) {
		this.rawName = rawName;
	}

//	public void setAttributes(List<IfcAttribute> attributes) {
////		List<IfcAttributeInfo> inheritedAttributeInfos = typeInfo.getInheritedAttributeInfos();
//		for (int i = 0; i < attributes.size(); ++i) {
//			IfcAttribute attribute = attributes.get(i);
//			if (attribute instanceof IfcLink) {
//				addOutgoingLink((IfcLink) attribute);
//			} else {
//				assert attribute instanceof IfcLiteralAttribute;
//				addLiteralAttribute((IfcLiteralAttribute) attribute);
//			}
//		}
//	}

//	
//	public IfcAttribute getAttribute(IfcAttributeInfo attributeInfo) {
//		IfcAttribute attribute = literalAttributes.get(attributeInfo);
//		if (attribute == null) {
//			attribute = outgoingLinks.get(attributeInfo);
//		}
//		return attribute;
//	}

	public IfcAttributeList<IfcLiteralAttribute> getLiteralAttributes() {
		return literalAttributes;
	}
	
	public void addLiteralAttribute(IfcLiteralAttribute literalAttribute) {
		literalAttributes.add(literalAttribute);
	}	

//	public List<IfcLiteralAttribute> getLiteralAttributes(IfcAttributeInfo attributeInfo) {
//		return literalAttributes.selectAll(attributeInfo);
//	}

	public IfcAttributeList<IfcLink> getOutgoingLinks() {
		return outgoingLinks;
	}	

	public void addOutgoingLink(IfcLink link) {
		
		assert(link != null);

		outgoingLinks.add(link);
	}
	
	public void bindInverseLinks() {
		
		for (IfcLink link : outgoingLinks) {		
			//
			// bind link with inverse link (if any)
			//
			IfcLinkInfo linkInfo = link.getLinkInfo();

			// Bind inverse outgoingLinks with direct outgoingLinks
			for (IfcEntityBase destination : link.getDestinations()) {

				if (destination instanceof IfcEntity) {
					List<IfcInverseLinkInfo> inverseLinkInfos = linkInfo
							.getInverseLinkInfos();

					IfcInverseLinkInfo inverseLinkInfo = findInverseLinkInfo(
							(IfcEntity) destination, inverseLinkInfos);
					if (inverseLinkInfo != null) {
						link.setInverseLinkInfo(inverseLinkInfo);
					}

					((IfcEntity) destination).addIncomingLink(link);
				}

			}
		}
		
	}
	
	private void addIncomingLink(IfcLink link) {
		incomingLinks.add(link); 
	}
	
	private IfcInverseLinkInfo findInverseLinkInfo(IfcEntity destination, List<IfcInverseLinkInfo> inverseLinkInfos) {

		if (inverseLinkInfos != null) {
			if (inverseLinkInfos.size() == 1) {
				IfcInverseLinkInfo inverseLinkInfo = inverseLinkInfos.get(0);
				IfcEntityTypeInfo destionationEntityTypeInfo = inverseLinkInfo.getDestinationEntityTypeInfo();
				if (destination.isInstanceOf(destionationEntityTypeInfo)) {
					return inverseLinkInfo;
				}
			} else {
				for (IfcInverseLinkInfo inverseLinkInfo : inverseLinkInfos) {
					IfcEntityTypeInfo destionationEntityTypeInfo = inverseLinkInfo.getDestinationEntityTypeInfo();
					if (destination.isInstanceOf(destionationEntityTypeInfo)) {
						return inverseLinkInfo;
					}
				}
			}
		}
		return null;
	}
	
	public IfcAttributeList<IfcLink> getIncomingLinks() {
		return incomingLinks;
	}
	
	@Override
	public int compareTo(IfcEntity o) {
		int compare = Long.compare(lineNumber, o.lineNumber);
		if (compare == 0) {
			compare = typeInfo.compareTo(o.typeInfo);
		}
		return compare;
	}

	public boolean isInstanceOf(IfcEntityTypeInfo typeInfo) {
		return this.typeInfo.isTypeOf(typeInfo);
	}
	
	@Override
	public Boolean isLiteralType() {
		return Boolean.FALSE;
	}
	
	public List<IfcUniqueKeyValue> getUniqueKeyValues() {
		if (uniqueKeyValues == null) {
			uniqueKeyValues = getUniqueKeyValues(typeInfo.getUniqueKeyInfos());
		}
		return uniqueKeyValues;
	}
	
	public List<IfcUniqueKeyValue> getUniqueKeyValues(List<IfcUniqueKeyInfo> uniqueKeyInfos) {		
		List<IfcUniqueKeyValue> uniqueKeyValues = new ArrayList<>();
		if (uniqueKeyInfos != null) { 
			for (IfcUniqueKeyInfo uniqueKeyInfo : uniqueKeyInfos) {
				uniqueKeyValues.add(getUniqueKeyValue(uniqueKeyInfo));
			}
		}
		return uniqueKeyValues;
	}
	
	public IfcUniqueKeyValue getUniqueKeyValue(IfcUniqueKeyInfo uniqueKeyInfo) {
		IfcUniqueKeyValue uniqueKeyValue = new IfcUniqueKeyValue();
		for (IfcAttributeInfo attributeInfo : uniqueKeyInfo.getAttributeInfos()) {
			IfcLiteralAttribute attribute = literalAttributes.selectFirst(attributeInfo);
			if (attribute != null) {
				uniqueKeyValue.addValue(attributeInfo, attribute.getValue());
			} else {
				assert(attributeInfo.isOptional());
//				logger.warn(String.format("Unique literal attribute not found: %s.%s", getTypeInfo(), attributeInfo));
				uniqueKeyValue.addValue(attributeInfo, IfcValue.NULL);				
			}
		}
		return uniqueKeyValue;
	}
	
	public IfcEntity getParent() {
		return parent;
	}

	public void setParent(IfcEntity parent) {
		this.parent = parent;
	}

	public boolean isSharedBlankNode() {
		return isSharedBlankNode;
	}
	
	public void setSharedBlankNode(boolean isSharedBlankNode) {
		this.isSharedBlankNode = isSharedBlankNode;
	}

	/**
	 * @return the isLiteralValueContainer
	 */
	public boolean isLiteralValueContainer() {
		return isLiteralValueContainer;
	}

	/**
	 * @param isLiteralValueContainer the isLiteralValueContainer to set
	 */
	public void setLiteralValueContainer(boolean isLiteralValueContainer) {
		this.isLiteralValueContainer = isLiteralValueContainer;
	}
	
	/**
	 * @return the isDuplicated
	 */
	public boolean isDuplicated() {
		return sameAsEntity != null;
	}
	
	public void setSameAs(IfcEntity other) {
		this.sameAsEntity = other;
		
		for (IfcLink incomingLink : incomingLinks) {
			List<IfcEntityBase> destinations = incomingLink.getDestinations();
			int index = destinations.indexOf(this);
			assert(index >= 0) : "Expected: index >= 0";
			destinations.remove(index);
			destinations.add(index, other);
			other.addIncomingLink(incomingLink);
		}
		
	}
	
	public IfcEntity getSameAsEntity() {
		return sameAsEntity;
	}
	
	public void appendDebugMessage(String s) {
		if (debugMessage != null) {
			debugMessage = debugMessage + StringUtils.SEMICOLON + s;
		} else {
			debugMessage = s;
		}
	}
	
	public String getDebugMessage() {
		return debugMessage;
	}

	/**
	 * Checks if two entities have the same attribute values
	 * @param other
	 * @return
	 */
	public boolean isIdenticalTo(IfcEntityBase other1) {
		if (!(other1 instanceof IfcEntity)) {
			return false;
		}
		
		IfcEntity other = (IfcEntity)other1;
		
		//
		// compare types
		//		
		if (!typeInfo.equals(other.typeInfo)) {
			return false;
		}
		
		//
		// compare literal attributes
		//
		if (!IteratorComparer.areEqual(literalAttributes, other.literalAttributes)) {
			return false;
		}
		
		//
		// compare outgoing links
		//
		Iterator<IfcLink> outgoingLinkIterator1 = outgoingLinks.iterator();
		Iterator<IfcLink> outgoingLinkIterator2 = other.outgoingLinks.iterator();
		
		while (outgoingLinkIterator1.hasNext()) {
			if (!outgoingLinkIterator2.hasNext()) {
				return false;
			}
			
			Iterator<IfcEntityBase> destinations1 = outgoingLinkIterator1.next().getDestinations().iterator();
			Iterator<IfcEntityBase> destinations2 = outgoingLinkIterator2.next().getDestinations().iterator();
			
			while (destinations1.hasNext()) {
				if (!destinations2.hasNext()) {
					return false;
				}
				
				IfcEntityBase destination1 = destinations1.next();
				IfcEntityBase destination2 = destinations2.next();				
				
				if (!destination1.equals(destination2) && !destination1.isIdenticalTo(destination2)) {
					return false;
				}				
			}
			
			if (destinations2.hasNext()) {
				return false;
			}			
			
		}
		
		if (outgoingLinkIterator2.hasNext()) {
			return false;
		}

		
		return true;
	}

//	@Override
//	public RdfNodeTypeEnum getRdfNodeType() {
//		return hasName() ? RdfNodeTypeEnum.Uri : RdfNodeTypeEnum.BlankNode;
//	}
//
//	@Override
//	public RdfUri toRdfUri() {
//		return Ifc2RdfConverter.getDefaultConverter().convertEntityToUri(this);
//	}
	
	@Override
	public String toString() {
		assert (typeInfo != null);
		return String.format("%s_%d", typeInfo.getName(), lineNumber);
	}

//	@Override
//	public List<IRdfLink> getRdfLinks() {
//		if (links == null) {
//			links = new ArrayList<>();
//			IRdfLink classLink = new RdfProperty(new RdfPredicate(RdfVocabulary.OWL_CLASS), getEntityTypeInfo());
//			links.add(classLink);
//			
//			links.addAll(literalAttributes);
//			links.addAll(outgoingLinks);
//		}
//		return links;
//	}

	@Override
	public boolean equals(Object o) {
		return ((IfcEntity)o).lineNumber == lineNumber;
	}
	
	@Override
	public int hashCode() {
		return (int)lineNumber;
	}

//	@Override
//	public List<? extends IRdfTriple> getIncomingRdfTriples() {
//		return incomingLinks;
//	}
//	
//	@Override
//	public IRdfNode getRdfClass() {
//		return typeInfo;
//	}
	
	
}
