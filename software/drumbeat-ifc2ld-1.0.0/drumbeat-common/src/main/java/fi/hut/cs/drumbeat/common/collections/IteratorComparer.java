package fi.hut.cs.drumbeat.common.collections;

import java.util.Iterator;

public class IteratorComparer {
	
	public static boolean areEqual(Iterable<?> it1, Iterable<?> it2) {
		return areEqual(it1.iterator(), it2.iterator());		
	}
	
	public static boolean areEqual(Iterator<?> it1, Iterator<?> it2) {
		while (it1.hasNext()) {
			if (it2.hasNext()) {
				Object o1 = it1.next();
				Object o2 = it2.next();
				if (o1 != null) {
					if (!o1.equals(o2)) {
						return false;
					}
				} else if (o2 != null) {
					return false;
				}
			} else {
				return false;
			}
		}
		
		if (it2.hasNext()) {
			return false;
		}
		
		return true;
	}
	
}
