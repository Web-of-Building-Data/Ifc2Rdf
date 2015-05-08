package fi.hut.cs.drumbeat.common.cli;

import java.util.Comparator;

import org.apache.commons.cli.Option;

public class DrumbeatOptionComparator implements Comparator<Option> {

	@Override
	public int compare(Option o1, Option o2) {
		if (o1 instanceof DrumbeatOption && o2 instanceof DrumbeatOption) {
			return Integer.compare(((DrumbeatOption)o1).getIndex(), ((DrumbeatOption)o2).getIndex()); 
		}
		return o1.getOpt().compareTo(o2.getOpt());
	}

}
