package fi.hut.cs.drumbeat.rdf.data;

import java.util.Comparator;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;

public class RdfComparator {
	
	public static final Comparator<Statement> TRIPLE_COMPARATOR = new Comparator<Statement>() {

		@Override
		public int compare(Statement o1, Statement o2) {
			
			int result = NODE_COMPARATOR.compare(o1.getSubject(), o2.getSubject());
			if (result != 0) {
				return result;
			}
			
			result = NODE_COMPARATOR.compare(o1.getPredicate(), o2.getPredicate());
			if (result != 0) {
				return result;
			}
			return NODE_COMPARATOR.compare(o1.getObject(), o2.getObject());
		}
	};
	
//	public static final Comparator<IRdfLink> LINK_COMPARATOR = new Comparator<IRdfLink>() {
//
//		@Override
//		public int compare(IRdfLink o1, IRdfLink o2) {
//			int result = NODE_COMPARATOR.compare(o1.getPredicate(), o2.getPredicate());
//			if (result != 0) {
//				return result;
//			}
//			return NODE_COMPARATOR.compare(o1.getObject(), o2.getObject());
//		}
//	};
	
//	public static final Comparator<IRdfNode> NODE_COMPARATOR = new Comparator<IRdfNode>() {
//
//		@Override
//		public int compare(IRdfNode o1, IRdfNode o2) {
//			RdfNodeTypeEnum type1 = o1.getRdfNodeType();
//			RdfNodeTypeEnum type2 = o2.getRdfNodeType();
//			int result = type1.compareTo(type2);
//			if (result != 0) {
//				return result;			
//			}
//			
//			return o1.getNode().toString().compareTo(o2.toString());
//		}
//	};
	
	
	public static final Comparator<RDFNode> NODE_COMPARATOR = new Comparator<RDFNode>() {

		@Override
		public int compare(RDFNode o1, RDFNode o2) {
			RdfNodeTypeEnum type1 = RdfNodeTypeEnum.getType(o1);
			RdfNodeTypeEnum type2 = RdfNodeTypeEnum.getType(o2);
			int result = type1.compareTo(type2);
			if (result != 0) {
				return result;			
			}
			
//			if (type1.equals(RdfNodeTypeEnum.Uri)) {
//				return o1.asResource().getURI().compareTo(o2.asResource().getURI());
//			}
			
			return o1.toString().compareTo(o2.toString());
		}
	};
	

}
