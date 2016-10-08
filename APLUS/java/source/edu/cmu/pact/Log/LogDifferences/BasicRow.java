package edu.cmu.pact.Log.LogDifferences;

import edu.cmu.pact.Log.LogDifferences.Content.ContentCell;

public class BasicRow implements Row {
	//=========================================================================
	//	Fields
	//=========================================================================
	
	private final Contents content;
	private final Column column;
	private final String[] row;
	
	//=========================================================================
	//	Constructor
	//=========================================================================
	
	public BasicRow(Contents content, Column column) {
		this.content = content;
		this.column = column;
		
		this.row = makeRowFromContents();
		
		addLeadingData();
	}

	//=========================================================================
	//	Public methods
	//=========================================================================

	@Override
	public String[] toArray() {
		return row;
	}
	
	//=========================================================================
	//	Helper methods
	//=========================================================================

	private void addToRow(ContentCell cell, String[] tempRow){
		String columnName = cell.getColumnName();
		int index = column.getIndex(columnName);
		
		tempRow[index] = cell.getContent();
	}
	
	private String[] makeRowFromContents(){
		String[] tempRow = new String[column.getNumColumns()];

		for(ContentCell cell : content){
			addToRow(cell, tempRow);
		}
		
		return tempRow;
	}
	
	private void addLeadingData(){
		row[column.getIndex("transaction_id")] = content.getTransactionId();
		row[column.getIndex("old/new")] = "old";
	}
	
	//ComparisonRow calls this to set the differences rows
	void setDiffsColumns(DifferencesCounter diffs){
		row[column.getIndex("Has differences")] = diffs.hasDiffs();
		row[column.getIndex("# of differences")] = diffs.getNumDiffs();
	}
}
