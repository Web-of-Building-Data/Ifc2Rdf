package fi.hut.cs.drumbeat.common.collections;

import java.util.Map.Entry;


public class ComparablePair< K extends Comparable<K>, V extends Comparable<V> >
			extends Pair<K, V> implements Comparable<Entry<K, V>> {
	
	public ComparablePair(K key, V value) {
		super(key, value);
	}	

	public ComparablePair(Entry<K, V> entry) {
		super(entry);
	}
	
	@Override
	public int compareTo(Entry<K, V> o) {
		int result = getKey().compareTo(o.getKey());
		if (result != 0) {
			return result;
		}
		return getValue().compareTo(o.getValue());
	}	

}
