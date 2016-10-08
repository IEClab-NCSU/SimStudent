package edu.cmu.pact.Log.LogDifferences.Content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import edu.cmu.pact.Log.LogDifferences.Content.ContentCell.ContentCellBuilder;

/**
 * This class stores the skill index, probability, name, category, model_name, 
 * and buggy information extracted from an action log. Also has an index for
 * the order it was read in.
 * </p> NOTE: This class is left immutable instead of making  SkillBuilder for 
 * convenience and conformity with other Content objects*/
public class SkillContent implements Content {
	//=========================================================================
	//	Column Formatter
	//=========================================================================

	static class SkillColumnFormatter implements ColumnFormatter{
		@Override
		public String makeColumnName(String fieldType, int index,
				String propertyName) {
			return fieldType+index+" "+propertyName;
		}
	}

	//=========================================================================
	//	Column name comparator
	//=========================================================================

	public static class SkillComparator implements Comparator<ContentCell>{
		@Override
		public int compare(ContentCell cell1, ContentCell cell2) {
			if(!cell1.getFieldType().equals("skill")
					|| !cell2.getFieldType().equals("skill")){
				throw new ClassCastException("Must compare two skill cells");
			}
			
			//compare the index numbers first
			if(cell1.getIndex() != cell2.getIndex()){ return cell1.getIndex() - cell2.getIndex(); }
			
			//compare the property names
			List<String> indicies = Collections.unmodifiableList(
					Arrays.asList("probability", "name", "category", "model_name", "buggy"));
			
			int index1 = indicies.indexOf(cell1.getPropertyName());
			int index2 = indicies.indexOf(cell2.getPropertyName());
			
			return index1 - index2;
		}
	}
	
	//=========================================================================
	//	Fields
	//=========================================================================

	private static final String SKILL = "skill"; 

	private final int index;

	private final String probability;
	private final String name;
	private final String category;
	private final List<String> model_names;
	private final Boolean buggy;
	
	//=========================================================================
	//	Constructors
	//=========================================================================
	
	public SkillContent(int index, String probability, String name,
			String category, List<String> model_names, Boolean buggy) {
		this.index = index;
		this.probability = probability;
		this.name = name;
		this.category = category;
		this.model_names = model_names;
		this.buggy = buggy;
	}

	//=========================================================================
	//	Implemented Methods
	//=========================================================================

	@Override
	public Iterator<ContentCell> iterator() {
		ArrayList<ContentCell> skillList = new ArrayList<ContentCell>();

		addCell(skillList, SKILL, index, "probability", probability);
		addCell(skillList, SKILL, index, "name", 		name);
		addCell(skillList, SKILL, index, "category", 	category);

		if(model_names != null){
			addCell(skillList, SKILL, index, "model_name", model_names.toString());
		}
		if(buggy != null){
			addCell(skillList, SKILL, index, "buggy", buggy.toString());
		}

		//prevent removal
		return Collections.unmodifiableList(skillList).iterator();
	}

	private boolean addCell(ArrayList<ContentCell> skillList, String fieldType, 
			int index, String property, String content){
		//postcondition: properties with no content will be ignored
		if(content == null){ return false; }

		ContentCell cell = makeCell(fieldType, index, property, content);
		skillList.add(cell);

		return true;
	}

	private ContentCell makeCell(String fieldType, int index, String property, String content){
		ContentCellBuilder builder = new ContentCellBuilder();

		builder.fieldType(fieldType)
		.index(index)
		.propertyName(property)
		.content(content)
		.formatter(new SkillColumnFormatter());

		return builder.buildCell();
	}

	//=========================================================================
	//	Accessors
	//=========================================================================

	public String getProbability() {
		return probability;
	}

	public String getName() {
		return name;
	}
	public String getCategory() {
		return category;
	}
	public List<String> getModel_names() {
		return model_names;
	}
	public Boolean getBuggy() {
		return buggy;
	}
}
