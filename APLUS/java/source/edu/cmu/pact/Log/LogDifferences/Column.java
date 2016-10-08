package edu.cmu.pact.Log.LogDifferences;

/**
 * Takes TutorMessageContents and creates ordered columns based on their
 * information. Use only one column as the basis for of all rows.
 */
public interface Column {
	public Integer getIndex(String columnName);//returns an Integer, so it can return null
	public String[] toArray();
	public int getNumColumns();
}
