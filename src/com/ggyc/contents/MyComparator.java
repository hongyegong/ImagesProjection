package com.ggyc.contents;

import java.util.Comparator;
import java.util.Map;

public class MyComparator implements Comparator<Object>{

	Map map;
	public MyComparator(Map map) {
		this.map = map;
	}
	
	public int compare(Object o1, Object o2) {
		if(map.get(o1) == map.get(o2)) {
			return 1;
		}else {
			return ((Integer) map.get(o2)).compareTo((Integer) map.get(o1));
		}
	}
}