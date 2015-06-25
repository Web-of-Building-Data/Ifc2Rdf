package fi.hut.cs.drumbeat.ifc.processing.grounding;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import fi.hut.cs.drumbeat.ifc.processing.IfcAnalyserException;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingMainProcessor;
import fi.hut.cs.drumbeat.ifc.processing.grounding.IfcGroundingProcessor;
import fi.hut.cs.drumbeat.ifc.common.IfcNotFoundException;
import fi.hut.cs.drumbeat.ifc.data.model.IfcAttributeList;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntity;
import fi.hut.cs.drumbeat.ifc.data.model.IfcLink;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcEntityTypeInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcInverseLinkInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcLinkInfo;
import fi.hut.cs.drumbeat.ifc.data.schema.IfcSchema;


/**
 * Replace outgoing links by their inverse ones.
 * 
 *  Sample syntax:
 *  
 *		<processor name="ReplaceByInverseLinks" enabled="true">
 *			<class>fi.hut.cs.drumbeat.ifc.util.grounding.ReplaceByInverseLinks</class>
 *			<params>
 *				<param name="inverseLinkNames" value="type1.link1, type2.link2, ..." />
 *			</params>
 *		</processor>
 *  
 * @author vuhoan1
 *
 */
public class ReplaceByInverseLinks extends IfcGroundingProcessor {

	private static final String PARAM_INVERSE_LINK_NAMES = "inverseLinkNames";
	
	private Map<IfcEntityTypeInfo, IfcInverseLinkInfo> inverseLinkInfoMap;
	
	public ReplaceByInverseLinks(IfcGroundingMainProcessor mainProcessor, Properties properties) {
		super(mainProcessor, properties);
	}

	@Override
	public InputTypeEnum getInputType() {
		return InputTypeEnum.Any;
	}

	@Override
	void initialize() throws IfcAnalyserException {

		inverseLinkInfoMap = new TreeMap<>();
		
		String inverseLinkNameString = getProperties().getProperty(PARAM_INVERSE_LINK_NAMES);
		
		if (inverseLinkNameString == null) {
			throw new IfcAnalyserException(String.format("Param %s is undefined", PARAM_INVERSE_LINK_NAMES));
		}
		
		IfcSchema schema = getMainProcessor().getAnalyzer().getModel().getSchema();

		for (String subInverseLinkNameString : inverseLinkNameString.split(",")) {			
			String[] tokens = subInverseLinkNameString.split("\\.");
			IfcEntityTypeInfo entityTypeInfo;
			try {
				entityTypeInfo = schema.getEntityTypeInfo(tokens[0].trim());
			} catch (IfcNotFoundException e) {
				throw new IfcAnalyserException(String.format("Entity type not found: '%s'", tokens[0]));
			}
			
			String inverseLinkName = tokens[1];
			boolean found = false;
			for (IfcInverseLinkInfo inverseLinkInfo : entityTypeInfo.getInheritedInverseLinkInfos()) {
				if (inverseLinkInfo.getName().equalsIgnoreCase(inverseLinkName)) {
					inverseLinkInfoMap.put(entityTypeInfo, inverseLinkInfo);
					found = true;
					continue;
				}
			}
			
			if (!found) {
				throw new IfcAnalyserException(String.format("Inverse link not found: '%s'", subInverseLinkNameString));				
			}
		}
		
	}

	@Override
	boolean process(IfcEntity entity) throws IfcAnalyserException {
		
		for (IfcEntityTypeInfo entityTypeInfo : inverseLinkInfoMap.keySet()) {
			if (entity.isInstanceOf(entityTypeInfo)) {
				IfcInverseLinkInfo inverseLinkInfo = inverseLinkInfoMap.get(entityTypeInfo); 
				IfcLinkInfo outgoingLinkInfo = inverseLinkInfo.getOutgoingLinkInfo();
				List<IfcLink> incomingLinks = entity.getIncomingLinks(); 
				for (int i = 0; i < incomingLinks.size(); ++i) {
					IfcLink incomingLink = incomingLinks.get(i);
					if (incomingLink.getLinkInfo().equals(outgoingLinkInfo)) {
						IfcAttributeList<IfcLink> outgoingLinksOfSource = incomingLink.getSource().getOutgoingLinks();
						List<IfcLink> links = outgoingLinksOfSource.selectAll(outgoingLinkInfo);
						if (links.size() == 1) {
							links.get(0).setUseInverseLink(true);
							return true;
						} else {
							throw new IfcAnalyserException(String.format("Incoming link %s is a multiple link", entityTypeInfo.getName(), outgoingLinkInfo.getName()));
						}						
					}
				}
				
//				if (!inverseLinkInfo.isOptional()) {
//					throw new IfcAnalyserException(String.format("Incoming link %s.%s-->%s is not found for entity %s",
//							incomingLinkInfo.getEntityTypeInfo().getName(), incomingLinkInfo.getName(), entityTypeInfo.getName(), entity));
//				}
			}
		}
		
		
		return false;
	}

	@Override
	boolean checkNameDuplication() {
		return false;
	}

	@Override
	boolean allowNameDuplication() {
		return false;
	}

}
