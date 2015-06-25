package fi.hut.cs.drumbeat.ifc.util.versioning.structuredmsg;

import java.util.Iterator;
import java.util.Map.Entry;

import fi.hut.cs.drumbeat.common.digest.ByteArray;
import fi.hut.cs.drumbeat.rdf.RdfException;
import fi.hut.cs.drumbeat.rdf.data.msg.RdfMsg;
import fi.hut.cs.drumbeat.rdf.data.msg.RdfMsgContainer;


public class StructuredMsgContainerUpdate {
	
	private RdfMsgContainer version1;
	private RdfMsgContainer version2;
	private RdfMsgContainer unchangedMsgContainer;
	private RdfMsgContainer addedMsgContainer;
	private RdfMsgContainer removedMsgContainer;
	
	public StructuredMsgContainerUpdate(RdfMsgContainer version1, RdfMsgContainer version2) throws RdfException {
		this.version1 = version1;
		this.version2 = version2;
		unchangedMsgContainer = new RdfMsgContainer();
		addedMsgContainer = new RdfMsgContainer();
		removedMsgContainer = new RdfMsgContainer();
		
		compareDefinitionTripleMsgs();
		compareSingleTripleMsgs();
		compareSingleTreeMsgs();
		compareMultiTreeMsgs();
	}
	
	private void compareDefinitionTripleMsgs() {
		
		Iterator<RdfMsg> definitionTripleMsgs1 = version1.getClassDefinitionTripleMsgs().iterator();
		Iterator<RdfMsg> definitionTripleMsgs2 = version2.getClassDefinitionTripleMsgs().iterator();
		
		RdfMsg definitionTripleMsg1 = null;
		RdfMsg definitionTripleMsg2 = null;
		
		while (definitionTripleMsg1 != null || definitionTripleMsgs1.hasNext()) {
			
			if (definitionTripleMsg1 == null) {
				definitionTripleMsg1 = definitionTripleMsgs1.next();
			}
			
			while (definitionTripleMsg2 != null || definitionTripleMsgs2.hasNext()) {
				
				if (definitionTripleMsg2 == null) {
					definitionTripleMsg2 = definitionTripleMsgs2.next();
				}
				
				int compare = definitionTripleMsg1.compareTo(definitionTripleMsg2);
				
				if (compare == 0) {
					unchangedMsgContainer.addClassDefinitionTripleMsg(definitionTripleMsg1);
					definitionTripleMsg1 = null;
					definitionTripleMsg2 = null;
					break;
				} else if (compare < 0) {
					removedMsgContainer.addClassDefinitionTripleMsg(definitionTripleMsg1);					
					definitionTripleMsg1 = null;
					break;
				} else { // compare > 0					
					addedMsgContainer.addClassDefinitionTripleMsg(definitionTripleMsg2);
					definitionTripleMsg2 = null;
				}
				
			}
			
			if (definitionTripleMsg1 != null) { // msg2 == null && !definitionTripleMsgs2.hasNext()
				removedMsgContainer.addClassDefinitionTripleMsg(definitionTripleMsg1);
				while (definitionTripleMsgs1.hasNext()) {
					definitionTripleMsg1 = definitionTripleMsgs1.next();
					removedMsgContainer.addClassDefinitionTripleMsg(definitionTripleMsg1);					
				}
				definitionTripleMsg1 = null;
			}
			
		}
		
		
		if (definitionTripleMsg2 != null) { // msg1 == null && !definitionTripleMsgs1.hasNext()
			addedMsgContainer.addClassDefinitionTripleMsg(definitionTripleMsg2);
			while (definitionTripleMsgs2.hasNext()) {
				definitionTripleMsg2 = definitionTripleMsgs2.next();
				addedMsgContainer.addClassDefinitionTripleMsg(definitionTripleMsg2);					
			}
		}		
		
		
	}
	
	private void compareSingleTripleMsgs() {
		
		Iterator<RdfMsg> singleTripleMsgs1 = version1.getSingleTripleMsgs().iterator();
		Iterator<RdfMsg> singleTripleMsgs2 = version2.getSingleTripleMsgs().iterator();
		
		RdfMsg singleTripleMsg1 = null;
		RdfMsg singleTripleMsg2 = null;
		
		while (singleTripleMsg1 != null || singleTripleMsgs1.hasNext()) {
			
			if (singleTripleMsg1 == null) {
				singleTripleMsg1 = singleTripleMsgs1.next();
			}
			
			while (singleTripleMsg2 != null || singleTripleMsgs2.hasNext()) {
				
				if (singleTripleMsg2 == null) {
					singleTripleMsg2 = singleTripleMsgs2.next();
				}
				
				int compare = singleTripleMsg1.compareTo(singleTripleMsg2);
				
				if (compare == 0) {
					unchangedMsgContainer.addSingleTripleMsg(singleTripleMsg1);
					singleTripleMsg1 = null;
					singleTripleMsg2 = null;
					break;
				} else if (compare < 0) {
					removedMsgContainer.addSingleTripleMsg(singleTripleMsg1);					
					singleTripleMsg1 = null;
					break;
				} else { // compare > 0					
					addedMsgContainer.addSingleTripleMsg(singleTripleMsg2);
					singleTripleMsg2 = null;
				}
				
			}
			
			if (singleTripleMsg1 != null) { // msg2 == null && !singleTripleMsgs2.hasNext()
				removedMsgContainer.addSingleTripleMsg(singleTripleMsg1);
				while (singleTripleMsgs1.hasNext()) {
					singleTripleMsg1 = singleTripleMsgs1.next();
					removedMsgContainer.addSingleTripleMsg(singleTripleMsg1);					
				}
				singleTripleMsg1 = null;
			}
			
		}
		
		
		if (singleTripleMsg2 != null) { // msg1 == null && !singleTripleMsgs1.hasNext()
			addedMsgContainer.addSingleTripleMsg(singleTripleMsg2);
			while (singleTripleMsgs2.hasNext()) {
				singleTripleMsg2 = singleTripleMsgs2.next();
				addedMsgContainer.addSingleTripleMsg(singleTripleMsg2);					
			}
		}		
		
		
	}
	
	private void compareSingleTreeMsgs() throws RdfException {
		
		Iterator<Entry<ByteArray, RdfMsg>> singleTreeMsgs1 = version1.getSingleTreeMsgs().entrySet().iterator();
		Iterator<Entry<ByteArray, RdfMsg>> singleTreeMsgs2 = version2.getSingleTreeMsgs().entrySet().iterator();
		
		Entry<ByteArray, RdfMsg> singleTreeMsg1 = null;
		Entry<ByteArray, RdfMsg> singleTreeMsg2 = null;
		
		while (singleTreeMsg1 != null || singleTreeMsgs1.hasNext()) {
			
			if (singleTreeMsg1 == null) {
				singleTreeMsg1 = singleTreeMsgs1.next();
			}
			
			while (singleTreeMsg2 != null || singleTreeMsgs2.hasNext()) {
				
				if (singleTreeMsg2 == null) {
					singleTreeMsg2 = singleTreeMsgs2.next();
				}
				
				int compare = singleTreeMsg1.getKey().compareTo(singleTreeMsg2.getKey());
				
				if (compare == 0) {
					unchangedMsgContainer.tryAddSingleTreeMsg(singleTreeMsg1.getKey(), singleTreeMsg1.getValue());
					singleTreeMsg1 = null;
					singleTreeMsg2 = null;
					break;
				} else if (compare < 0) {
					removedMsgContainer.tryAddSingleTreeMsg(singleTreeMsg1.getKey(), singleTreeMsg1.getValue());					
					singleTreeMsg1 = null;
					break;
				} else { // compare > 0					
					addedMsgContainer.tryAddSingleTreeMsg(singleTreeMsg2.getKey(), singleTreeMsg2.getValue());
					singleTreeMsg2 = null;
				}
				
			}
			
			if (singleTreeMsg1 != null) { // msg2 == null && !singleTreeMsgs2.hasNext()
				removedMsgContainer.tryAddSingleTreeMsg(singleTreeMsg1.getKey(), singleTreeMsg1.getValue());
				while (singleTreeMsgs1.hasNext()) {
					singleTreeMsg1 = singleTreeMsgs1.next();
					removedMsgContainer.tryAddSingleTreeMsg(singleTreeMsg1.getKey(), singleTreeMsg1.getValue());					
				}
				singleTreeMsg1 = null;
			}
			
		}
		
		
		if (singleTreeMsg2 != null) { // msg1 == null && !singleTreeMsgs1.hasNext()
			addedMsgContainer.tryAddSingleTreeMsg(singleTreeMsg2.getKey(), singleTreeMsg2.getValue());
			while (singleTreeMsgs2.hasNext()) {
				singleTreeMsg2 = singleTreeMsgs2.next();
				addedMsgContainer.tryAddSingleTreeMsg(singleTreeMsg2.getKey(), singleTreeMsg2.getValue());					
			}
		}		
		
		
	}		
	
	private void compareMultiTreeMsgs() {
		
		Iterator<Entry<ByteArray, RdfMsg>> multiTreeMsgs1 = version1.getMultiTreeMsgs().entrySet().iterator();
		Iterator<Entry<ByteArray, RdfMsg>> multiTreeMsgs2 = version2.getMultiTreeMsgs().entrySet().iterator();
		
		Entry<ByteArray, RdfMsg> multiTreeMsg1 = null;
		Entry<ByteArray, RdfMsg> multiTreeMsg2 = null;
		
		while (multiTreeMsg1 != null || multiTreeMsgs1.hasNext()) {
			
			if (multiTreeMsg1 == null) {
				multiTreeMsg1 = multiTreeMsgs1.next();
			}
			
			while (multiTreeMsg2 != null || multiTreeMsgs2.hasNext()) {
				
				if (multiTreeMsg2 == null) {
					multiTreeMsg2 = multiTreeMsgs2.next();
				}
				
				int compare = multiTreeMsg1.getKey().compareTo(multiTreeMsg2.getKey());
				
				if (compare == 0) {
					unchangedMsgContainer.tryAddMultiTreeMsg(multiTreeMsg1.getKey(), multiTreeMsg1.getValue());
					multiTreeMsg1 = null;
					multiTreeMsg2 = null;
					break;
				} else if (compare < 0) {
					removedMsgContainer.tryAddMultiTreeMsg(multiTreeMsg1.getKey(), multiTreeMsg1.getValue());					
					multiTreeMsg1 = null;
					break;
				} else { // compare > 0					
					addedMsgContainer.tryAddMultiTreeMsg(multiTreeMsg2.getKey(), multiTreeMsg2.getValue());
					multiTreeMsg2 = null;
				}
				
			}
			
			if (multiTreeMsg1 != null) { // msg2 == null && !multiTreeMsgs2.hasNext()
				removedMsgContainer.tryAddMultiTreeMsg(multiTreeMsg1.getKey(), multiTreeMsg1.getValue());
				while (multiTreeMsgs1.hasNext()) {
					multiTreeMsg1 = multiTreeMsgs1.next();
					removedMsgContainer.tryAddMultiTreeMsg(multiTreeMsg1.getKey(), multiTreeMsg1.getValue());					
				}
				multiTreeMsg1 = null;
			}
			
		}
		
		
		if (multiTreeMsg2 != null) { // msg1 == null && !multiTreeMsgs1.hasNext()
			addedMsgContainer.tryAddMultiTreeMsg(multiTreeMsg2.getKey(), multiTreeMsg2.getValue());
			while (multiTreeMsgs2.hasNext()) {
				multiTreeMsg2 = multiTreeMsgs2.next();
				addedMsgContainer.tryAddMultiTreeMsg(multiTreeMsg2.getKey(), multiTreeMsg2.getValue());					
			}
		}		
		
		
	}		
	
	public RdfMsgContainer getStructuredMsgContainerByUpdateType(UpdateType updateType) {
		if (updateType == UpdateType.Unchanged) {
			return unchangedMsgContainer;
		} else if (updateType == UpdateType.Added) {
			return addedMsgContainer;
		} else { // updateType == UpdateType.Removed
			return removedMsgContainer;
		}
	}
	
	

}
