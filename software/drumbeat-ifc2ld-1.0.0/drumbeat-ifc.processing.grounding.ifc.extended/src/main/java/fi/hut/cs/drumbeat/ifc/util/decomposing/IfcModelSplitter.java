package fi.hut.cs.drumbeat.ifc.util.decomposing;

import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import fi.hut.cs.drumbeat.common.collections.Pair;
import fi.hut.cs.drumbeat.ifc.data.model.IfcEntity;
import fi.hut.cs.drumbeat.rdf.RdfException;
import fi.hut.cs.drumbeat.rdf.RdfUtils;
import fi.hut.cs.drumbeat.rdf.data.*;
import fi.hut.cs.drumbeat.rdf.data.msg.RdfMsg;
import fi.hut.cs.drumbeat.rdf.data.msg.RdfMsgContainer;
import fi.hut.cs.drumbeat.rdf.data.msg.RdfMsgType;
import fi.hut.cs.drumbeat.rdf.data.msg.RdfSharedTree;
import fi.hut.cs.drumbeat.rdf.data.msg.RdfTopTree;
import fi.hut.cs.drumbeat.rdf.data.msg.RdfTree;


public class IfcModelSplitter {

	private static final Logger logger = Logger.getLogger(IfcModelSplitter.class);
	
	private static final int MAX_ERROR_COUNT = 10;

	private IRdfModel model;

	private final RdfMsgContainer rdfMsgContainer = new RdfMsgContainer();
	private final List<RdfMsg> singleTreeMsgs;
	private final Map<Resource, Pair<RdfMsg, Boolean>> multiTreeMsgs;

	private IfcModelSplitter(IRdfModel model) {
		this.model = model;
		singleTreeMsgs = new ArrayList<>();
		multiTreeMsgs = new TreeMap<>(RdfComparator.NODE_COMPARATOR);
	}

	public static RdfMsgContainer split(IRdfModel model) throws RdfException {
		return new IfcModelSplitter(model).split();
	}	
	
	private RdfMsgContainer split() throws RdfException {
		//
		// Step 1: detect all top and shared nodes, and create corresponding MSGs with them 
		//
		logger.debug("Splitting model to structured MSGs (step 1: detecting all top and shared nodes)");
		Iterator<Resource> entityNodes = model.getAllSubjects();
		while (entityNodes.hasNext()) {
			Resource entityNode = entityNodes.next();
			createTreeForTopOrSharedNode(entityNode);			
		}
		
		//
		// Step 2: 
		//
		logger.debug("Splitting model to structured MSGs (step 2: building shared trees)");

		for (Entry<Resource, Pair<RdfMsg, Boolean>> entry : multiTreeMsgs.entrySet()) {
			Resource sharedNode = entry.getKey();
			RdfMsg msg = entry.getValue().getKey();

			RdfTree tree = msg.getSharedTrees().get(sharedNode);
			buildTree(msg, tree, true, new RdfLinkPath(sharedNode), tree);
		}

		logger.debug("Splitting model to structured MSGs (step 3: building top trees)");

		for (Iterator<RdfMsg> it = singleTreeMsgs.iterator(); it.hasNext();) {
			RdfMsg msg = it.next(); 
			for (RdfTree tree : msg.getTopTrees()) {
				Resource headNode = tree.getHeadNode();
				boolean isMultiTreeMsg = buildTree(msg, tree, false, new RdfLinkPath(headNode), tree); 
				if (isMultiTreeMsg) {
					it.remove();
				}
			}
		}		

		logger.debug("Splitting model to structured MSGs (step 4: adding single-tree MSGs)");
		
		for (RdfMsg msg : singleTreeMsgs) {
			RdfMsg conflictedMsg = rdfMsgContainer.tryAddSingleTreeMsg(msg);
			if (conflictedMsg != null) {				
				String message = String.format("Single-tree MSG duplicates another and is ignored:%nMSG1:%n%s%nMSG2:%n%s", conflictedMsg.toString(), msg.toString()); 
				logger.warn(message);
			}
		}


		logger.debug("Splitting model to structured MSGs (step 5: adding multi-tree MSGs)");
		
		for (Entry<Resource, Pair<RdfMsg, Boolean>> entry : multiTreeMsgs.entrySet()) {
			RdfMsg msg = entry.getValue().getKey();
			Boolean isDuplicated = entry.getValue().getValue();
			
			if (!isDuplicated) {
				RdfMsg conflictedMsg = rdfMsgContainer.tryAddMultiTreeMsg(msg);
				if (conflictedMsg != null) {				
					String message = String.format("Multi-tree MSG duplicates another and is ignored:%nMSG1:%n%s%nMSG2:%n%s", conflictedMsg.toString(), msg.toString());
					logger.error(message);
					throw new RdfException(message);
				}
			}
		}		
		
		logger.debug("Splitting model to structured MSGs (step 6)");
		
		
//		verify();		
		
		return rdfMsgContainer;		
	}
	
	private void createTreeForTopOrSharedNode(Resource entity) {
		if (entity.isURIResource()) {			
			
			//
			// this entity is URI
			//
			
			// get all outgoing statements with this entity as subject
			Iterator<Statement> outgoingStatements = model.getOutgoingStatements(entity);
			
			while (outgoingStatements.hasNext()) {
				Statement statement = outgoingStatements.next();
				
				RDFNode object = statement.getObject();				
				
				if (!object.isAnon()) {
					
					// 
					//both subject and object are URIs
					//
					if (statement.getPredicate().equals(RDF.type)) {
						// object is a type --> this triple is class definition
						assert(object.asResource().getLocalName().startsWith("Ifc") || object.equals(OWL.Ontology)) : statement.getObject();
						rdfMsgContainer.addClassDefinitionTriple(statement);
					} else {
						// object is not a type --> this triple is closed by two URIs
						rdfMsgContainer.addSingleTriple(statement);
					}
					
				} else {
					
					//
					// subject is URI, object is blank --> this entity is (one of) head node(s) of a MSG
					//
					RdfMsg msg = new RdfMsg();
					RdfTopTree tree = new RdfTopTree(entity);
					tree.put(statement, null);
					msg.addTopTree(tree);
					
					singleTreeMsgs.add(msg);
					
					//logger.trace(String.format("Added single-tree MSG with top URI-node: %s", RdfUtils.getShortStringWithType(entity)));					
				}				
			}
			

		} else {
			
			//
			// this entity is a blank node
			//
			
			// get all incoming statements with this entity as object
			Iterator<Statement> incomingStatements = model.getIncomingStatements(entity); 

			if (incomingStatements.hasNext()) {
				
				// 
				// Check if there is more than one incoming statement.
				// If so then consider this entity as a shared blank node,
				// otherwise ignore this entity because it will be added to a tree later (see method buildTree).
				//				
				incomingStatements.next();				
				if (incomingStatements.hasNext()) {
					
					//
					// this entity is a shared blank node which can be consider as the head node of a shared tree
					//
					RdfMsg msg = new RdfMsg();
					RdfSharedTree tree = new RdfSharedTree(entity);
					
					// add all outgoing statements to the shared tree
					Iterator<Statement> outgoingStatements = model.getOutgoingStatements(entity);
					while (outgoingStatements.hasNext()) {					
						tree.put(outgoingStatements.next(), null);
					}

					msg.addSharedTree(tree);
					
					multiTreeMsgs.put(entity, new Pair<RdfMsg, Boolean>(msg, Boolean.FALSE));
					
					//logger.trace(String.format("Added multi-tree MSG with shared node: %s", RdfUtils.getShortStringWithType(entity)));					

				}

			} else {
				
				//
				// There is no incoming statement to this blank node,
				// so it can be considered as a top node of a tree like an URI-node
				//
				RdfMsg msg = new RdfMsg();
				RdfTopTree tree = new RdfTopTree(entity);
				
				Iterator<Statement> outgoingStatements = model.getOutgoingStatements(entity);
				while (outgoingStatements.hasNext()) {					
					tree.put(outgoingStatements.next(), null);
				}

				msg.addTopTree(tree);
				singleTreeMsgs.add(msg);

				//logger.trace(String.format("Added single-tree MSG with top blank-node: %s", RdfUtils.getShortStringWithType(entity)));					
			}
		}			
	}

	// @SuppressWarnings("unchecked")
	private boolean buildTree(RdfMsg msg, RdfTree tree, boolean isMultiTreeMsg, RdfLinkPath linkPath, RdfTree ownerTree) {

		//
		// browse all blank-node objects of all statements defined at the 1st level of the tree
		//
		for (Statement statement : tree.keySet()) {

			RDFNode object = statement.getObject();

			if (object.isAnon()) {
				
				Resource objectResource = object.asResource();
				
				linkPath.addLast(statement);

				// check if the object is a shared blank node
				if (!isSharedBlankNode(objectResource)) {
					
					// the object is NOT a shared blank node
					RdfTree subTree = new RdfTree(object.asResource());
					
					Iterator<Statement> outgoingStatements = model.getOutgoingStatements(objectResource);
					while (outgoingStatements.hasNext()) {
						subTree.put(outgoingStatements.next(), null);
					}
					
					isMultiTreeMsg = buildTree(msg, subTree, isMultiTreeMsg, linkPath, ownerTree);
					tree.put(statement, subTree);

				} else {

					Pair<RdfMsg, Boolean> msg2Info = multiTreeMsgs.get(object); 
					RdfMsg msg2 = msg2Info.getKey();
					assert (msg2 != null) :
						String.format("Expected: msg2 != null, object: %s, isShared: %b, isDuplicated: %b", object, isSharedBlankNode(objectResource), ((IfcEntity)object).isDuplicated());

					assert (msg2.getSharedTrees() != null) :
						String.format("Expected: msg2.getSharedTrees() != null, object: %s, shared: %b", object, isSharedBlankNode(objectResource));

					RdfSharedTree subTree = msg2.getSharedTrees().get(object);
					assert (subTree != null);

					tree.put(statement, subTree);
					subTree.addOwnerTree(linkPath, ownerTree);

					if (msg != msg2) {						
						
						msg.mergeWith(msg2);						

						Boolean isDuplicated = isMultiTreeMsg;
						SortedMap<Resource, RdfSharedTree> sharedTrees = msg2.getSharedTrees();
						for (Resource sharedBlankNode : sharedTrees.keySet()) {
							multiTreeMsgs.put(sharedBlankNode, new Pair<RdfMsg, Boolean>(msg, isDuplicated));
							isDuplicated = Boolean.TRUE;
						}

					}
					
					isMultiTreeMsg = true;
				}
				
				linkPath.removeLast();
			}

		}

		return isMultiTreeMsg;

	}

	private boolean isSharedBlankNode(Resource object) {
		
		return multiTreeMsgs.containsKey(object);
		
	}
	
	
	public void verify() {
		
		Iterator<Statement> stmtIterator = model.getAllStatements();
		
		int errorCount = 0;
		
		while (stmtIterator.hasNext()) {
			Statement statement = stmtIterator.next();
			
			int matchingCount = 0;
			
			List<RdfMsg> msgs = new ArrayList<>();
			
			for (RdfMsg rdfMsg : rdfMsgContainer) {
				if (rdfMsg.contains(statement)) {
					msgs.add(rdfMsg);
					matchingCount += 1;
				}
			}
			
			if (matchingCount != 1) {
				
				++errorCount;
				
				logger.warn(String.format("Statement %s exists in %d MSGs",
					RdfUtils.getShortStringWithTypes(statement),
					matchingCount));
				
				if (matchingCount == 0) {
					for (RdfMsg rdfMsg : rdfMsgContainer.getAllMsgsByType(RdfMsgType.SingleTreeMsg)) {
						RdfTopTree tree = rdfMsg.getTopTrees().first();
						if (tree.getHeadNode().toString().equals("_LINE_65293")) {
							Model model = tree.toModel();
							StmtIterator it = model.listStatements();
							while (it.hasNext()) {
								logger.warn(String.format("\t\tTop statement: %s", RdfUtils.getShortStringWithTypes(it.next())));
							}
						}
					}
				} else {
				
					for (int i = 0; i < msgs.size(); ++i) {
						logger.warn(String.format("\tMSG %d:", i));	
						for (RdfTree tree : msgs.get(i).getTopTrees()) {
							logger.warn(String.format("\t\tTop node %s:", RdfUtils.getShortStringWithType(tree.getHeadNode())));
						}
						for (Resource sharedNode : msgs.get(i).getSharedTrees().keySet()) {
							logger.warn(String.format("\t\tShared node %s:", RdfUtils.getShortStringWithType(sharedNode)));
						}
					}
					
				}
				

				if (errorCount > MAX_ERROR_COUNT) {
					return;
				}
				
			}
			
		}
		
	}
	
	
	
}
