package edu.cmu.pact.Log.LogDifferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import edu.cmu.pact.Log.LogDifferences.Content.ActionEvaluationContent;
import edu.cmu.pact.Log.LogDifferences.Content.Content;
import edu.cmu.pact.Log.LogDifferences.Content.ContentCell;
import edu.cmu.pact.Log.LogDifferences.Content.CustomContent;
import edu.cmu.pact.Log.LogDifferences.Content.NameContent;
import edu.cmu.pact.Log.LogDifferences.Content.SAIContent;
import edu.cmu.pact.Log.LogDifferences.Content.SkillContent;
import edu.cmu.pact.Log.LogDifferences.Content.TutorAdviceContent;

public class MockTestingContents implements Contents {
	//=========================================================================
	//	Fields
	//=========================================================================

	private NameContent name;
	private SAIContent sai;
	private ActionEvaluationContent actionEval;
	
	/** Will at least be an empty list */
	private List<TutorAdviceContent> tutorAdvices = new ArrayList<TutorAdviceContent>();
	
	/** Will at least be an empty list */
	private List<SkillContent> skills = new ArrayList<SkillContent>();
	
	/** Will at least be an empty list */
	private List<CustomContent> customs = new ArrayList<CustomContent>();
	
	private String transactionId;
	
	//=========================================================================
	//	Iterator
	//=========================================================================
	
	@Override
	public Iterator<ContentCell> iterator() {
		//Taken from TutorMessageContents
		ArrayList<ContentCell> allContentCells = new ArrayList<ContentCell>();

		addContentCells(allContentCells, 		name);
		addContentCells(allContentCells, 		sai);
		addContentCells(allContentCells, 		actionEval);
		
		addListOfContentCells(allContentCells, 	tutorAdvices);
		addListOfContentCells(allContentCells,	skills);
		addListOfContentCells(allContentCells,	customs);
		
		//prevent removal
		return Collections.unmodifiableList(allContentCells).iterator();
	}
	private void addContentCells(List<ContentCell> allContentCells, Content content){
		if(content == null){ return; }
		
		for(ContentCell cell : content){
			allContentCells.add(cell);
		}
	}
	
	private void addListOfContentCells(List<ContentCell> allContentCells,
			List<? extends Content> contentList){
		if(contentList == null){ return; }
		
		for(Content content : contentList){
			addContentCells(allContentCells, content);
		}
	}
	
	
	//=========================================================================
	//	Accessors
	//=========================================================================
	
	public void resetFields(){
		name = null;
		sai = null;
		actionEval = null;
		tutorAdvices = new ArrayList<TutorAdviceContent>();
		skills = new ArrayList<SkillContent>();
		customs = new ArrayList<CustomContent>();
		transactionId = null;
	}
	
	@Override
	public NameContent getName() {
		return name;
	}
	@Override
	public SAIContent getSAI() {
		return sai;
	}
	@Override
	public ActionEvaluationContent getActionEval() {
		return actionEval;
	}
	@Override
	public List<TutorAdviceContent> getTutorAdvices() {
		return tutorAdvices;
	}
	@Override
	public List<SkillContent> getSkills() {
		return skills;
	}
	@Override
	public List<CustomContent> getCustomFields() {
		return customs;
	}
	@Override
	public String getTransactionId() {
		return transactionId;
	}
	
	public void setName(NameContent name) {
		this.name = name;
	}
	public void setSai(SAIContent sai) {
		this.sai = sai;
	}
	public void setActionEval(ActionEvaluationContent actionEval) {
		this.actionEval = actionEval;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	public void addTutorAdvice(TutorAdviceContent advice){
		tutorAdvices.add(advice);
	}
	public void addSkill(SkillContent skill){
		skills.add(skill);
	}
	public void addCustom(CustomContent custom){
		customs.add(custom);
	}
}
