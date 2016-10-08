package edu.cmu.pact.Log.LogDifferences.Content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import edu.cmu.pact.Log.LogDifferences.Content.ContentCell.ContentCellBuilder;
/** This content stores the advice and index from an action log */
public class TutorAdviceContent implements Content {
	//=========================================================================
	//	Column Formatter
	//=========================================================================

	static class TutorAdviceColumnFormatter implements ColumnFormatter{
		@Override
		public String makeColumnName(String fieldType, int index,
				String propertyName) {
			return fieldType+index;
		}
	}
	
	//=========================================================================
	//	Column name comparator
	//=========================================================================

	public static class TutorAdviceComparator implements Comparator<ContentCell>{
		@Override
		public int compare(ContentCell cell1, ContentCell cell2) {
			if(!cell1.getFieldType().equals("tutor_advice")
					|| !cell2.getFieldType().equals("tutor_advice")){
				throw new ClassCastException("Must compare two tutor_advice cells");
			}

			return Double.compare(cell1.getIndex(), cell2.getIndex());
		}
	}

	//=========================================================================
	//	Fields
	//=========================================================================

	private static final String TUTOR_ADVICE = "tutor_advice";
	private final String advice;
	private final int index;

	//=========================================================================
	//	Constructor
	//=========================================================================

	public TutorAdviceContent(String tutorAdvice, int index){
		this.advice = tutorAdvice;
		this.index = index;
	}

	//=========================================================================
	//	Implemented Methods
	//=========================================================================

	@Override
	public Iterator<ContentCell> iterator() {
		//AdviceContent objects will never be null, they will only be missing.
		//But null check anyway.
		ArrayList<ContentCell> adviceList = new ArrayList<ContentCell>();
		if(advice == null){ return adviceList.iterator(); }
		
		ContentCellBuilder builder = new ContentCellBuilder();
		builder.fieldType(TUTOR_ADVICE)
		.index(index)
		.content(advice)
		.formatter(new TutorAdviceColumnFormatter());

		adviceList.add(builder.buildCell());

		//prevent removal
		return Collections.unmodifiableList(adviceList).iterator();
	}

	//=========================================================================
	//	Accessors
	//=========================================================================
	public String getTutorAdvice() {
		return advice;
	}
}
