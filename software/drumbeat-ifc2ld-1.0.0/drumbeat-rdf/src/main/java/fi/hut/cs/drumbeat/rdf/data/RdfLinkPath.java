package fi.hut.cs.drumbeat.rdf.data;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Deque;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class RdfLinkPath implements Comparable<RdfLinkPath> {
	
	private Deque<Statement> statements;
	private Resource headNode;

	public RdfLinkPath(Resource headNode) {
		this.headNode = headNode;
		statements = new ArrayDeque<>();
	}

	public RdfLinkPath(Resource headNode, Deque<Statement> statements) {
		this(headNode);
		this.statements.addAll(statements);
	}

	public Resource getHeadNode() {
		return headNode;
	}
	
	public Deque<Statement> getStatements() {
		return statements;
	}
	
	public RdfLinkPath getCopy() {
		return new RdfLinkPath(headNode, statements);
	}
	
	public void addFirst(Statement statement) {
		statements.addFirst(statement);
	}
	
	public void addLast(Statement statement) {
		statements.addLast(statement);
	}
	
	public void removeFirst() {
		statements.removeFirst();
	}
	
	public void removeLast() {
		statements.removeLast();
	}
	
	@Override
	public int compareTo(RdfLinkPath o) {
		int result = RdfComparator.NODE_COMPARATOR.compare(headNode, o.headNode);
		if (result != 0) {
			return result;
		}
		
		Iterator<Statement> iterator = statements.iterator();
		Iterator<Statement> otherIterator = o.statements.iterator();
		
		while (iterator.hasNext()) {
			if (otherIterator.hasNext()) {
				Statement statement = iterator.next();
				Statement otherStatement = otherIterator.next();
				result = RdfComparator.NODE_COMPARATOR.compare(statement.getPredicate(), otherStatement.getPredicate());
				if (result != 0) {
					return result;
				}
			} else {
				return 1;
			}
		}
		
		if (otherIterator.hasNext()) {
			return -1;
		} else {
			return 0;
		}
				
	}
	
}
