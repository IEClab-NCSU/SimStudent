package edu.cmu.pact.Log.LogDifferences.Content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * This class stores the name extracted from an action log.
 */
public class NameContent implements Content{
	//=========================================================================
	//	Column Formatter
	//=========================================================================
	
	static class NameColumnFormatter implements ColumnFormatter{
		@Override
		public String makeColumnName(String fieldType, int index,
				String propertyName) {
			return fieldType;
		}
	}
	
	//=========================================================================
	//	Column name comparator
	//=========================================================================

	public static class NameComparator implements Comparator<ContentCell>{
		@Override
		public int compare(ContentCell cell1, ContentCell cell2) {
			if(!cell1.getFieldType().equals("problem_name")
					|| !cell2.getFieldType().equals("problem_name")){
				throw new ClassCastException("Must compare two problem_name cells");
			}

			//the fieldType will always be problem_name, but compare it anyway.
			return cell1.getFieldType().compareTo(cell2.getFieldType());
		}
	}
	
	//=========================================================================
	//	Fields
	//=========================================================================
	
	private final String name;
	private final String PROBLEM_NAME = "problem_name";
	
	//=========================================================================
	//	Constructor
	//=========================================================================
	
	public NameContent(String name){
		this.name = name;
	}
	
	//=========================================================================
	//	Implemented Methods
	//=========================================================================
	
	@Override
	public Iterator<ContentCell> iterator() {
		ArrayList<ContentCell> nameList = new ArrayList<ContentCell>();
		
		if(name != null){
			ContentCell nameCell = new ContentCell.ContentCellBuilder()
										.fieldType(PROBLEM_NAME)
										.content(name)
										.formatter(new NameColumnFormatter())
										.buildCell();
			nameList.add(nameCell);
		}
		
		//prevent removal
		return Collections.unmodifiableList(nameList).iterator();
	}

	//=========================================================================
	//	Accessors
	//=========================================================================
	
	public String getName() {
		return name;
	}
}
