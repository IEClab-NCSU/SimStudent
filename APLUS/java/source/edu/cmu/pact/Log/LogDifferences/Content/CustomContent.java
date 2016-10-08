package edu.cmu.pact.Log.LogDifferences.Content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * This class stores the custom_field name and value extracted from an action log.
 * Also includes a positional index (not used by Alphabetized ordering)
 */
public class CustomContent implements Content {
	//=========================================================================
	//	Column Formatter
	//=========================================================================

	/**
	 * Takes an extra name argument and prints out the column name in the format:
	 * </p>fieldType customName
	 */
	static class AlphabetizedCustomColumnFormatter implements ColumnFormatter{
		String customName;

		public AlphabetizedCustomColumnFormatter(String customName) {
			this.customName = customName;
		}

		@Override
		public String makeColumnName(String fieldType, int index,
				String propertyName) {
			if(customName == null){throw new IllegalStateException(); }

			return fieldType+" "+customName;
		}
	}
	
	//=========================================================================
	//	Column name comparator
	//=========================================================================

	/**
	 * This comparator orders with the column name in mind from 
	 * {@link AlphabetizedCustomColumnFormatter its formatter}
	 */
	public static class AlphabetizedCustomComparator implements Comparator<ContentCell>{
		@Override
		public int compare(ContentCell cell1, ContentCell cell2) {
			if(!cell1.getFieldType().equals("custom_field")
					|| !cell2.getFieldType().equals("custom_field")){
				throw new ClassCastException("Must compare two custom_field cells");
			}

			//Because alphabetized column headers include the problem name, I 
			//need to compare that field. That is the only field to compare, 
			//so I can just use getColumnName()
			return cell1.getColumnName().compareTo(cell2.getColumnName());
		}
	}

	//=========================================================================
	//	Fields
	//=========================================================================

	private static final String CUSTOM_FIELD = "custom_field";
	private final int index;

	private final String name;
	private final String value;

	//=========================================================================
	//	Constructors
	//=========================================================================
	
	public CustomContent(int index, String name, String value){
		this.index = index;
		this.name = name;
		this.value = value;
	}
	
	//=========================================================================
	//	Implemented Methods
	//=========================================================================

	@Override
	public Iterator<ContentCell> iterator() {
		//custom only exists if found in the file, but null check anyway
		if(name == null || value == null){ return Collections.<ContentCell>emptyList().iterator(); }
		
		ContentCell customCell = new ContentCell.ContentCellBuilder()
		.fieldType(CUSTOM_FIELD)
		.index(index)
		.propertyName(name)//unused, but include anyway
		.content(value)
		.formatter(new AlphabetizedCustomColumnFormatter(name))
		.buildCell();

		ArrayList<ContentCell> customList = new ArrayList<ContentCell>();
		customList.add(customCell);

		//prevent removal
		return Collections.unmodifiableList(customList).iterator();
	}

	//=========================================================================
	//	Accessors
	//=========================================================================
	
	public String getName() {
		return name;
	}
	public String getValue() {
		return value;
	}

}
