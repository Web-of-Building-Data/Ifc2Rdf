package fi.hut.cs.drumbeat.rdf.data.msg;

import java.util.Map;

import fi.hut.cs.drumbeat.common.digest.ByteArray;


public interface IRdfMsgContainer extends Iterable<RdfMsg> {
	
//	Collection<RdfMsg> getAllMsgsByType(RdfMsgType type);
	
//	SortedMap<Resource, Statement> getClassDefinitionTriples();

//	SortedMap<Resource, Collection<Statement>> getSingleTriples();
	
	Map<ByteArray, RdfMsg> getSingleTreeMsgs();
	
	Map<ByteArray, RdfMsg> getMultiTreeMsgs();

}
