package fi.hut.cs.drumbeat.common.statistics;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import fi.hut.cs.drumbeat.common.numbers.IntegerWrapper;

public class GrouppedCounterTree {
	
	private Comparator<?>[] comparators;
	private GroupedCounter<?> rootGroupCounter;
	
	public GrouppedCounterTree(Comparator<?>... comparators) {
		this.comparators = comparators;
		rootGroupCounter = createGroupCounter(0);
	}
	
	public void addItem(Object... itemElements) {
		rootGroupCounter.addItem(itemElements);
	}
	
	@SuppressWarnings("unchecked")
	public <T> GroupedCounter<T> getRootGroupedCounter() {
		return (GroupedCounter<T>) rootGroupCounter;
	}

	public int getTotalCount() {
		return rootGroupCounter.getTotalCount();
	}
	
	private GroupedCounter<?> createGroupCounter(int level) {
		return new GroupedCounter<>(level, comparators[level]);
	}
	
	
	/**
	 * Internal class, map of key-count pairs.
	 * @author vuhoan1
	 *
	 * @param <T>
	 */
	public class GroupedCounter<T> extends TreeMap<T, IntegerWrapper> {
		
		private static final long serialVersionUID = 1L;

		private final int level;

		private Map<T, GroupedCounter<?>> subGroupCounterMap;
		
		public GroupedCounter(int level, Comparator<T> comparator) {
			super(comparator);
			this.level = level;
			if (!isLeafLevel()) {
				subGroupCounterMap = new TreeMap<>(comparator);
			}
		}
		
		public int getLevel() {
			return level;
		}
		
		public boolean isLeafLevel() {
			return level == comparators.length - 1;
		}
		
		public void addItem(Object[] itemElements) {
			
			assert(itemElements.length == comparators.length);
			
			@SuppressWarnings("unchecked")
			T key = (T)itemElements[level];
			assert(key != null);

			IntegerWrapper count = this.get(key);
			if (count == null) { // new key
				count = new IntegerWrapper(1);
				this.put(key, count);
				if (!isLeafLevel()) {
					GroupedCounter<?> subTreeCounter = createGroupCounter(level + 1);
					subGroupCounterMap.put(key, subTreeCounter);
					subTreeCounter.addItem(itemElements);				
				}
			} else { // old key
				count.increase();				
				if (!isLeafLevel()) {
					GroupedCounter<?> subTreeCounter = subGroupCounterMap.get(key);
					subTreeCounter.addItem(itemElements);				
				}
			}
		}
		
		public int getTotalCount() {
			int sum = 0;
			for (IntegerWrapper count : values()) {
				sum += count.getValue();
			}
			return sum;
		}
		
		public Map<T, GroupedCounter<?>> getSubGroupedCounterMap() {
			return subGroupCounterMap;
		}
		
	}	
	
}
