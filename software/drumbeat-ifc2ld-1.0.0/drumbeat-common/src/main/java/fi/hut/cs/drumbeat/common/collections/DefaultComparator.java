package fi.hut.cs.drumbeat.common.collections;

import java.util.Comparator;


/**
 * A comparator that compares two {@link Comparable} objects in a default way. 
 * 
 * @author Nam Vu
 *
 * @param <T>
 */
public class DefaultComparator<T extends Comparable<T>> implements Comparator<T> {
	
	@Override
	public int compare(T o1, T o2) {
		return ((Comparable<T>)o1).compareTo(o2);
	}	
	
}
