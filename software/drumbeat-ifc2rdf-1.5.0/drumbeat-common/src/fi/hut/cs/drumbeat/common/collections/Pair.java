package fi.hut.cs.drumbeat.common.collections;

import java.util.Map.Entry;

public class Pair<K, V> implements Entry<K, V> {
	
	private K key;
	private V value;
	
	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	public Pair(Entry<K, V> entry) {
		this.key = entry.getKey();
		this.value = entry.getValue();
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		V oldValue = this.value;
		this.value = value;
		return oldValue;
	}
	
	@Override
	public int hashCode() {
		return key.hashCode() ^ value.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		@SuppressWarnings("unchecked")
		Pair<K,V> o = (Pair<K,V>)obj;
		return key.equals(o.key) && value.equals(o.value);
	}

}
