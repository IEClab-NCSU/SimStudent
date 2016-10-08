package edu.cmu.pact.Log.LogDifferences;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.TreeSet;

import edu.cmu.pact.Log.LogDifferences.Content.ActionEvaluationContent;
import edu.cmu.pact.Log.LogDifferences.Content.Content;
import edu.cmu.pact.Log.LogDifferences.Content.ContentCell;
import edu.cmu.pact.Log.LogDifferences.Content.CustomContent;
import edu.cmu.pact.Log.LogDifferences.Content.NameContent;
import edu.cmu.pact.Log.LogDifferences.Content.SAIContent;
import edu.cmu.pact.Log.LogDifferences.Content.SkillContent;
import edu.cmu.pact.Log.LogDifferences.Content.TutorAdviceContent;

/** 
 * AlphabetizedCustomsColumn makes the custom_field columns sort by the custom_field's name. 
 * This is due to {@link edu.cmu.pact.Log.LogDifferences.Content.
 * CustomContent.AlphabetizedCustomColumnFormatter its formatter}
 * placing the name in the column name and the {@link edu.cmu.pact.Log.LogDifferences.
 * Content.CustomContent.AlphabetizedCustomComparator comparator's} ordering.
 * 
 * @see Column
 * */
public class AlphabetizedCustomsColumn implements Column {
	
	//=========================================================================
	//	Fields
	//=========================================================================
	
	private final List<Contents> originalContents;
	private final List<Contents> comparisonContents;
	
	private TreeSet<ContentCell> names = new TreeSet<ContentCell>(new NameContent.NameComparator());
	private TreeSet<ContentCell> sai = new TreeSet<ContentCell>(new SAIContent.SAIComparator());
	private TreeSet<ContentCell> actionEval = new TreeSet<ContentCell>(new ActionEvaluationContent.AEComparator());
	private TreeSet<ContentCell> advice = new TreeSet<ContentCell>(new TutorAdviceContent.TutorAdviceComparator());
	private TreeSet<ContentCell> skills = new TreeSet<ContentCell>(new SkillContent.SkillComparator());
	
	//I believe that ALL of the special ordering in customs are due to the 
	//formatter and comparator, but nothing in this class deal with it specifically.
	private TreeSet<ContentCell> customs = new TreeSet<ContentCell>(new CustomContent.AlphabetizedCustomComparator());
	
	private final LinkedHashMap<String, Integer> indicies;

	//=========================================================================
	//	Constructors
	//=========================================================================
	
	public AlphabetizedCustomsColumn(
			List<Contents> originalContents,
			List<Contents> comparisonContents) {
		this.originalContents = originalContents;
		this.comparisonContents = comparisonContents;
		
		indicies = makeColumnsMap();
	}

	//=========================================================================
	//	Override methods
	//=========================================================================

	@Override
	public Integer getIndex(String columnName) {
		return indicies.get(columnName);
	}

	@Override
	public String[] toArray() {
		return indicies.keySet().toArray(new String[indicies.size()]);
	}

	@Override
	public int getNumColumns() {
		return indicies.size();
	}

	//=========================================================================
	//	Private helper methods
	//=========================================================================

	/**
	 * Adds the first few leading columns, then creates column headers for
	 * the columns that are found in the two lists of TutorMessageContents passed.
	 * It then stores the sorted columns in the indicies map which holds the
	 * (column, index) pair.
	 */
	private LinkedHashMap<String, Integer> makeColumnsMap() {
		LinkedHashSet<String> columns = new LinkedHashSet<String>();
		
		addLeadingColumns(columns);
		addContentsListToTreeMaps(originalContents);
		addContentsListToTreeMaps(comparisonContents);
		
		addTreeMapsToColumns(columns);
		
		return setToNumberedMap(columns);
	}

	/** Adds leading columns that are not explicitly part of the TutorMessageContents */
	private void addLeadingColumns(LinkedHashSet<String> columns) {
		//these are the first few columns that aren't part of the contents
		columns.add("Has differences");
		columns.add("# of differences");
		columns.add("old/new");
		columns.add("transaction_id");
	}

	/** For every TutorMessageContents, add all their Content pieces to sorted
	 * tree maps to ensure columns are ordered correctly */
	private void addContentsListToTreeMaps(List<Contents> contentsList) {
		for(Contents contents : contentsList){
			addContentToTree(names, contents.getName());
			addContentToTree(sai, contents.getSAI());
			addContentToTree(actionEval, contents.getActionEval());
			
			addContentListToTree(advice, contents.getTutorAdvices());
			addContentListToTree(skills, contents.getSkills());
			addContentListToTree(customs, contents.getCustomFields());
		}
	}

	private void addContentToTree(TreeSet<ContentCell> sortedContents, Content content) {
		if(content == null){ return; }
		
		for(ContentCell cell : content){
			sortedContents.add(cell);
		}
	}

	private void addContentListToTree(TreeSet<ContentCell> sortedContents,
			List<? extends Content> contentList) {
		if(contentList == null){ return; }
		for(Content content : contentList){
			addContentToTree(sortedContents, content);
		}
	}
	
	/** Takes the now sorted columns in TreeMaps, converts them to sets,
	 * and then adds them to the columns list */
	private void addTreeMapsToColumns(LinkedHashSet<String> columns){
		addTreeAsColumnName(columns, names);
		addTreeAsColumnName(columns, sai);
		addTreeAsColumnName(columns, actionEval);
		addTreeAsColumnName(columns, advice);
		addTreeAsColumnName(columns, skills);
		addTreeAsColumnName(columns, customs);
	}
	
	private void addTreeAsColumnName(LinkedHashSet<String> columns, TreeSet<ContentCell> sortedSet){
		LinkedHashSet<String> columnNames = new LinkedHashSet<String>();
		for(ContentCell cell : sortedSet){
			columnNames.add(cell.getColumnName());
		}
		
		columns.addAll(columnNames);
	}

	/** Converts the set of column names to a numbered map to enable easy
	 * column index finding */
	private LinkedHashMap<String, Integer> setToNumberedMap(
			LinkedHashSet<String> columns) {
		LinkedHashMap<String, Integer> tempIndicies = new LinkedHashMap<String, Integer>();
		
		int counter = 0;
		for(String columnName : columns){
			tempIndicies.put(columnName, counter);
			counter++;
		}
		
		return tempIndicies;
	}
}
