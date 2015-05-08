package fi.hut.cs.drumbeat.common.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * This class is a List implementation which sorts the elements using the
 * comparator specified when constructing a new instance.
 * 
 * @param <T>
 */
public class SortedList<T> extends LinkedList<T> {
	
    /**
     * Needed for serialization.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Comparator used to sort the list.
     */
    private Comparator<? super T> comparator = null;
    
    /**
     * Constructs a new instance with the list elements sorted in their
     * {@link java.lang.Comparable} natural ordering.
     */
    public SortedList() {
    }
    
    /**
     * Constructs a new instance using the given comparator.
     * 
     * @param comparator
     */
    public SortedList(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }
    
    /**
     * Adds a new entry to the list. The insertion point is calculated using the
     * comparator.
     * 
     * @param t
     */
    @Override
    public boolean add(T t) {
        int insertionIndex = Collections.binarySearch(this, t, comparator);
        super.add((insertionIndex > -1) ? insertionIndex : (-insertionIndex) - 1, t);
        return true;
    }
    
    /**
     * Adds all elements in the specified collection to the list. Each element
     * will be inserted at the correct position to keep the list sorted.
     * 
     * @param paramCollection
     */
    @Override
    public boolean addAll(Collection<? extends T> ts) {
        boolean result = false;
        for (T t : ts) {
            result |= add(t);
        }
        return result;
    }
    
    /**
     * Checks, if this list contains the given Element. This is faster than the
     * {@link #contains(Object)} method, since it is based on binary search.
     * 
     * @param t
     * @return <code>true</code>, if the element is contained in this list;
     * <code>false</code>, otherwise.
     */
    public boolean containsElement(T t) {
        return (Collections.binarySearch(this, t, comparator) > -1);
    }
}