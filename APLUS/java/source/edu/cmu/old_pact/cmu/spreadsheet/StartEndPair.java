//the StartEndPair class represents a range of text and provides an easy way to tell
//whether the insertion point is within that range
package edu.cmu.old_pact.cmu.spreadsheet;

final class StartEndPair {
	public int lineNumber;
	public int start;
	public int end;
	
	public StartEndPair(int s, int e, int l) {
		start = s;
		end = e;
		lineNumber = l;
   	}
   	
   	public boolean inside (int i) {
   		return ((start <= i) && (i<= end));
   	}
   	public boolean precedes (int i) {
   		return ((start > i));
   	}
   	public boolean follows (int i) {
   		return ((i > end));
   	}
}
