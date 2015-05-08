package fi.hut.cs.drumbeat.rdf.data.msg;

import java.util.*;

import com.hp.hpl.jena.rdf.model.Statement;

import fi.hut.cs.drumbeat.common.collections.SortedList;
import fi.hut.cs.drumbeat.common.digest.ByteArray;
import fi.hut.cs.drumbeat.rdf.RdfException;


public class RdfMsgContainer implements IRdfMsgContainer {
	
//	private static final Logger logger = Logger.getRootLogger();	
	
	private SortedList<RdfMsg> classDefinitionTripleMsgs;	
	private SortedList<RdfMsg> singleTripleMsgs;	
	private Map<ByteArray, RdfMsg> singleTreeMsgs;
	private Map<ByteArray, RdfMsg> multiTreeMsgs;
	
	public RdfMsgContainer() {
		classDefinitionTripleMsgs = new SortedList<>();
		singleTripleMsgs = new SortedList<>();
		singleTreeMsgs = new HashMap<>();
		multiTreeMsgs = new HashMap<>();
	}
	
	public int getTotalSize() {
		return classDefinitionTripleMsgs.size() + singleTripleMsgs.size() + singleTreeMsgs.size() + multiTreeMsgs.size();
	}
	
	public Collection<RdfMsg> getAllMsgsByType(RdfMsgType type) {
		if (type == RdfMsgType.ClassDefinitionTripleMsg) {
			return getClassDefinitionTripleMsgs();
		} else if (type == RdfMsgType.SingleTripleMsg) {
			return getSingleTripleMsgs();
		} else if (type == RdfMsgType.SingleTreeMsg) {
			return singleTreeMsgs.values();
		} else {
			return multiTreeMsgs.values();
		}
	}
	
	public void addClassDefinitionTriple(Statement statement) {
		RdfTopTree tree = new RdfTopTree(statement.getSubject());
		tree.put(statement, null);
		RdfMsg msg = new RdfMsg();
		msg.addTopTree(tree);
		addClassDefinitionTripleMsg(msg);
	}
	
	public void addClassDefinitionTripleMsg(RdfMsg msg) {
		classDefinitionTripleMsgs.add(msg);		
	}

	public void addSingleTriple(Statement statement) {
		RdfTopTree tree = new RdfTopTree(statement.getSubject());
		tree.put(statement, null);
		RdfMsg msg = new RdfMsg();
		msg.addTopTree(tree);
		addSingleTripleMsg(msg);
	}
	
	public void addSingleTripleMsg(RdfMsg msg) {
		singleTripleMsgs.add(msg);
	}	

	public RdfMsg tryAddSingleTreeMsg(RdfMsg msg) throws RdfException {		
		ByteArray checksum = msg.getChecksum();
		return tryAddSingleTreeMsg(checksum, msg);
	}
	
	public RdfMsg tryAddSingleTreeMsg(ByteArray checksum, RdfMsg msg) {
		RdfMsg conflictedMsg = singleTreeMsgs.put(checksum, msg);
		if (conflictedMsg != null) {
			singleTreeMsgs.put(checksum, conflictedMsg);
		}
		return conflictedMsg;
	}

	public RdfMsg tryAddMultiTreeMsg(RdfMsg msg) {
		ByteArray checksum = msg.getChecksum();
		return tryAddMultiTreeMsg(checksum, msg);
	}
	
	public RdfMsg tryAddMultiTreeMsg(ByteArray checksum, RdfMsg msg) {
		RdfMsg conflictedMsg = multiTreeMsgs.put(checksum, msg);
		if (conflictedMsg != null) {
			multiTreeMsgs.put(checksum, conflictedMsg);
		}
		return conflictedMsg;
	}
		
	public Collection<RdfMsg> getClassDefinitionTripleMsgs() {
		return classDefinitionTripleMsgs;
	}

	public Collection<RdfMsg> getSingleTripleMsgs() {
		return singleTripleMsgs;
	}

	public Map<ByteArray, RdfMsg> getSingleTreeMsgs() {
		return singleTreeMsgs;
	}
	
	public Map<ByteArray, RdfMsg> getMultiTreeMsgs() {
		return multiTreeMsgs;
	}
	
	@Override
	public Iterator<RdfMsg> iterator() {
		return new StructuredMsgContainerIterator();
	}
	
	
	public class StructuredMsgContainerIterator implements Iterator<RdfMsg> {
		
		private Iterator<RdfMsg> currentIterator;
		private RdfMsgType nextIteratorType;
		
		public StructuredMsgContainerIterator() {
			nextIteratorType = RdfMsgType.ClassDefinitionTripleMsg;
		}
		
		@Override
		public boolean hasNext() {
			if (currentIterator != null && currentIterator.hasNext()) {
				return true;
			}
			
			while (nextIteratorType != null) {
				if (nextIteratorType == RdfMsgType.ClassDefinitionTripleMsg) {
					
					currentIterator = getClassDefinitionTripleMsgs().iterator();
					nextIteratorType = RdfMsgType.SingleTripleMsg;
					
				} else if (nextIteratorType == RdfMsgType.SingleTripleMsg) {
					
					currentIterator = getSingleTripleMsgs().iterator();
					nextIteratorType = RdfMsgType.SingleTreeMsg;
					
				} else if (nextIteratorType == RdfMsgType.SingleTreeMsg) {
					
					currentIterator = getSingleTreeMsgs().values().iterator();
					nextIteratorType = RdfMsgType.MultiTreeMsg;
					
				}  else if (nextIteratorType == RdfMsgType.MultiTreeMsg) {
					
					currentIterator = getMultiTreeMsgs().values().iterator();
					nextIteratorType = null;
				}
				
				if (currentIterator.hasNext()) {
					return true;
				}
				
			}
			return false;
		}

		@Override
		public RdfMsg next() {
			if (currentIterator != null) {
				return currentIterator.next();
			} else {
				throw new NoSuchElementException();
			}
		}

		@Override
		public void remove() {
			currentIterator.remove();
		}
		
	}


}
