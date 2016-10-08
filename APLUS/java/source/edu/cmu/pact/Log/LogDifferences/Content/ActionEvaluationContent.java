package edu.cmu.pact.Log.LogDifferences.Content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import edu.cmu.pact.Log.LogDifferences.Content.ContentCell.ContentCellBuilder;

/**
 * This class stores the action_evaluation evaluation, current_hint_number, 
 * total_hints_available, hintId, and classification extracted from an action log.
 */
public class ActionEvaluationContent implements Content {
	//=========================================================================
	//	Column Formatter
	//=========================================================================

	static class AEColumnFormatter implements ColumnFormatter{
		@Override
		public String makeColumnName(String fieldType, int index,
				String propertyName) {
			return fieldType+" "+propertyName;
		}
	}
	
	//=========================================================================
	//	Column name comparator
	//=========================================================================

	public static class AEComparator implements Comparator<ContentCell>{
		@Override
		public int compare(ContentCell cell1, ContentCell cell2) {
			if(!cell1.getFieldType().equals("action_evaluation")
					|| !cell2.getFieldType().equals("action_evaluation")){
				throw new ClassCastException("Must compare two action_evaluation cells");
			}

			//compare the property names
			List<String> indicies = Collections.unmodifiableList(
					Arrays.asList("evaluation", "current_hint_number", 
							"total_hints_available", "hintId", "classification"));
			
			int index1 = indicies.indexOf(cell1.getPropertyName());
			int index2 = indicies.indexOf(cell2.getPropertyName());
			
			return index1 - index2;
		}
	}
	
	//=========================================================================
	//	Fields
	//=========================================================================
	
	private static final String ACTION_EVALUATION = "action_evaluation";

	private final String evaluation;
	private final String current_hint_number;
	private final String total_hints_available;
	private final String hintId;
	private final String classification;
	
	//=========================================================================
	//	Constructors
	//=========================================================================
	
	public ActionEvaluationContent(String evaluation,
			String current_hint_number, String total_hints_available,
			String hintId, String classification) {
		super();
		this.evaluation = evaluation;
		this.current_hint_number = current_hint_number;
		this.total_hints_available = total_hints_available;
		this.hintId = hintId;
		this.classification = classification;
	}

	//=========================================================================
	//	Implemented Methods
	//=========================================================================

	@Override
	public Iterator<ContentCell> iterator() {
		ArrayList<ContentCell> aeList = new ArrayList<ContentCell>();
		
		addCell(aeList, ACTION_EVALUATION, "evaluation", 			evaluation);
		addCell(aeList, ACTION_EVALUATION, "current_hint_number", 	current_hint_number);
		addCell(aeList, ACTION_EVALUATION, "total_hints_available", total_hints_available);
		addCell(aeList, ACTION_EVALUATION, "hintId", 				hintId);
		addCell(aeList, ACTION_EVALUATION, "classification", 		classification);
		
		//prevent removal
		return Collections.unmodifiableList(aeList).iterator();
	}
	
	private boolean addCell(ArrayList<ContentCell> aeList, String fieldType, 
			String property, String content){
		//postcondition: properties with no content will be ignored
		if(content == null){ return false; }
		
		ContentCell cell = makeCell(fieldType, property, content);
		aeList.add(cell);
		
		return true;
	}
	
	private ContentCell makeCell(String fieldType, String property, String content){
		ContentCellBuilder builder = new ContentCellBuilder();
		
		builder.fieldType(fieldType)
				.propertyName(property)
				.content(content)
				.formatter(new AEColumnFormatter());
		
		return builder.buildCell();
	}
	
	//=========================================================================
	//	Accessors
	//=========================================================================
	
	public String getEvaluation() {
		return evaluation;
	}
	public String getHintNum() {
		return current_hint_number;
	}
	public String getTotalHints() {
		return total_hints_available;
	}
	public String getHintId() {
		return hintId;
	}
	public String getClassification() {
		return classification;
	}
}
