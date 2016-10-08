package edu.cmu.pact.Log.LogDifferences;

import edu.cmu.pact.Log.LogDifferences.Content.ContentCell;


public class ComparisonRow implements Row {
	//=========================================================================
	//	Fields
	//=========================================================================
	
	private final Contents content;
	private final Row originalRow;
	private final Column column;
	private final String[] row;
	
	private final DifferencesCounter diffs = new DifferencesCounter();
	
	private final static String ditto = "\"\"\"\"";
	
	//=========================================================================
	//	Constructor
	//=========================================================================
	
	public ComparisonRow(Contents content, Row originalRow, Column column) {
		this.content = content;
		this.originalRow = originalRow;
		this.column = column;
		
		this.row = makeRowFromContents();

		addLeadingData();
		((BasicRow) originalRow).setDiffsColumns(diffs);
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

	private String[] makeRowFromContents(){
		String[] tempRow = new String[column.getNumColumns()];

		for(ContentCell cell : content){
			String columnName = cell.getColumnName();
			int index = column.getIndex(columnName);
			
			addCellIfDifferent(index, cell, tempRow);
		}
		
		return tempRow;
	}
	
	private void addCellIfDifferent(int index, ContentCell comparisonCell, String[] tempRow){
		String newContent = comparisonCell.getContent();
		String oldContent = originalRow.toArray()[index];
		
		if(newContent == null && oldContent == null){ return; }
		if(newContent == null && oldContent != null){ diffs.increment(); return; }
		if(newContent != null && oldContent == null){
			diffs.increment(); 
			tempRow[index] = comparisonCell.getContent();
			return;
		}
		
		
		if(newContent.equalsIgnoreCase(oldContent) || //case shouldn't matter
				(comparisonCell.getPropertyName() != null 
				&& comparisonCell.getPropertyName().equals("tutor_event_time")))//make tutor_event_time always ditto
		{
			tempRow[index] = ditto;
			return;
		}
		
		tempRow[index] = comparisonCell.getContent();
		diffs.increment();
	}

	private void addLeadingData(){
		row[column.getIndex("Has differences")] = diffs.hasDiffs();
		row[column.getIndex("# of differences")] = diffs.getNumDiffs();
		row[column.getIndex("transaction_id")] = content.getTransactionId();
		row[column.getIndex("old/new")] = "new";
	}
}
