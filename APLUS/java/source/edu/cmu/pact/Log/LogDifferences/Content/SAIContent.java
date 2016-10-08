package edu.cmu.pact.Log.LogDifferences.Content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
/**
 * This class stores the selection, action, and input information extracted 
 * from an action log. It is possible that multiple S, A, or I exist in a single message.
 * Unlike the other contents, this is mutable because it can hold multiple pieces of info.
 */
public class SAIContent implements Content{
	//=========================================================================
	//	Column Formatter
	//=========================================================================

	static class SAIColumnFormatter implements ColumnFormatter{
		@Override
		public String makeColumnName(String fieldType, int index,
				String propertyName) {
			return fieldType+index;
		}
	}

	//=========================================================================
	//	Column name comparator
	//=========================================================================

	public static class SAIComparator implements Comparator<ContentCell>{
		@Override
		public int compare(ContentCell cell1, ContentCell cell2) {
			//make sure both cells are the right type
			if(!cell1.getFieldType().equals("selection") && 
					!cell1.getFieldType().equals("action") && 
					!cell1.getFieldType().equals("input")){
				throw new ClassCastException("First cell is not an SAI");
			}
			if(!cell2.getFieldType().equals("selection") && 
					!cell2.getFieldType().equals("action") && 
					!cell2.getFieldType().equals("input")){
				throw new ClassCastException("Second cell is not an SAI");
			}
			
			//compare the field type
			List<String> type = Collections.unmodifiableList(
					Arrays.asList("selection", "action", "input"));
			
			int type1 = type.indexOf(cell1.getFieldType());
			int type2 = type.indexOf(cell2.getFieldType());
			
			if(type1 != type2){ return type1 - type2; }
			
			//compare the index numbers
			return cell1.getIndex() - cell2.getIndex();
		}
	}
	
	//=========================================================================
	//	Fields
	//=========================================================================

	private ArrayList<String> selection = new ArrayList<String>();
	private ArrayList<String> action = new ArrayList<String>();
	private ArrayList<String> input = new ArrayList<String>();
	
	//=========================================================================
	//	Implemented Methods
	//=========================================================================

	@Override
	public Iterator<ContentCell> iterator() {
		//Don't need to null check here because if it doesn't exist,
		//it wouldn't be added in the first place
		ArrayList<ContentCell> saiList = new ArrayList<ContentCell>();

		saiList.addAll(stringListToContentCellList(selection, "selection"));
		saiList.addAll(stringListToContentCellList(action, "action"));
		saiList.addAll(stringListToContentCellList(input, "input"));

		//prevent removal
		return Collections.unmodifiableList(saiList).iterator();
	}

	private ArrayList<ContentCell> stringListToContentCellList(ArrayList<String> stringList, String fieldType){
		ArrayList<ContentCell> contentCellList = new ArrayList<ContentCell>();

		for(int i = 0; i < stringList.size(); i++){
			String content = stringList.get(i);

			ContentCell cell = new ContentCell.ContentCellBuilder()
									.fieldType(fieldType)
									.index(i)
									.content(content)
									.formatter(new SAIColumnFormatter())
									.buildCell();
			contentCellList.add(cell);
		}
		return contentCellList;
	}

	//=========================================================================
	//	Accessors
	//=========================================================================
	
	public void addOneSAI(String name, String value){ //adding an S, A, or I depending on input
		if(name.equals("selection")){
			addSelection(value);
		}
		else if(name.equals("action")){
			addAction(value);
		}
		else if(name.equals("input")){
			addInput(value);
		}
	}

	public ArrayList<String> getSelection() {
		return selection;
	}
	public void addSelection(String selection) {
		getSelection().add(selection);
	}

	public ArrayList<String> getAction() {
		return action;
	}
	public void addAction(String action) {
		getAction().add(action);
	}

	public ArrayList<String> getInput() {
		return input;
	}
	public void addInput(String input) {
		getInput().add(input);
	}
}
