package edu.cmu.pact.miss;

import java.util.Comparator;

/* Author Tasmia
 * */

public class StringLengthListSort implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		// TODO Auto-generated method stub
		return o1.length() - o2.length();
	}
	
}
