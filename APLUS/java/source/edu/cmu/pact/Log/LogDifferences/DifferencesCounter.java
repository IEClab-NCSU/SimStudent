package edu.cmu.pact.Log.LogDifferences;

/** Counts the number of differences between rows */
public class DifferencesCounter {
	private int numDiffs = 0;
	
	public void increment(){
		numDiffs++;
	}
	
	public String hasDiffs(){
		return (numDiffs > 0) ? "true" : "false";
	}
	
	public String getNumDiffs(){
		return Integer.toString(numDiffs);
	}
}
