package edu.cmu.pact.Log.LogDifferences.Content;

import java.util.List;

/**
 * Utility class to help compare lists and properties.
 * These methods act like compareTo and are null safe.
 */
public class CompareProperties {
	protected int compareToProperty(String property1, String property2){
		if(property1 == null && property2 == null){ return 0; }
		if(property1 != null && property2 == null){ return -1; }
		if(property1 == null && property2 != null){ return 1; }

		return property1.compareToIgnoreCase(property2);
	}

	protected int compareToLists(List<String> list1, List<String> list2){
		if(list1 == null && list2 != null){ return -1; }
		if(list1 != null && list2 == null){ return 1; }
		if(list1 == null && list2 == null){ return 0; }
		
		//assuming that neither list can be null, must at least be size 0
		if(list1.size() > list2.size()){ return -1; }
		if(list1.size() < list2.size()){ return 1; }

		for(int i = 0; i < list1.size(); i ++){
			String value1 = list1.get(i);
			String value2 = list2.get(i);

			int compareVal = compareToProperty(value1, value2);
			if(compareVal != 0){ return compareVal; } //TODO Don't know if this is returning the expected value
		}
		return 0;
	}
}
