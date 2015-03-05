package net.linkedbuildingdata.common.cli;

import java.util.Comparator;

import org.apache.commons.cli.Option;

public class LbdOptionComparator implements Comparator<Option> {

	@Override
	public int compare(Option o1, Option o2) {
		if (o1 instanceof LbdOption && o2 instanceof LbdOption) {
			return Integer.compare(((LbdOption)o1).getIndex(), ((LbdOption)o2).getIndex()); 
		}
		return o1.getOpt().compareTo(o2.getOpt());
	}

}
