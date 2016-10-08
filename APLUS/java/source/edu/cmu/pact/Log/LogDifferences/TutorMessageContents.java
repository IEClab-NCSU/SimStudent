package edu.cmu.pact.Log.LogDifferences;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import edu.cmu.pact.Log.TutorActionLogV4;
import edu.cmu.pact.Log.LogDifferences.Content.ActionEvaluationContent;
import edu.cmu.pact.Log.LogDifferences.Content.Content;
import edu.cmu.pact.Log.LogDifferences.Content.ContentCell;
import edu.cmu.pact.Log.LogDifferences.Content.CustomContent;
import edu.cmu.pact.Log.LogDifferences.Content.NameContent;
import edu.cmu.pact.Log.LogDifferences.Content.SAIContent;
import edu.cmu.pact.Log.LogDifferences.Content.SkillContent;
import edu.cmu.pact.Log.LogDifferences.Content.TutorAdviceContent;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pslc.logging.TutorMessage;
import edu.cmu.pslc.logging.element.ActionEvaluationElement;
import edu.cmu.pslc.logging.element.CustomFieldElement;
import edu.cmu.pslc.logging.element.EventDescriptorElement;
import edu.cmu.pslc.logging.element.SkillElement;

/**
 * Takes a TutorActionLogV4 and extracts all its' information into Contents.
 * TutorMessageContents and all of it's stored Contents are iterable.
 */
public class TutorMessageContents implements Iterable<ContentCell>, Contents{
	//=========================================================================
	//	Fields
	//=========================================================================

	private final NameContent name;
	private final SAIContent sai;
	private final ActionEvaluationContent actionEval;
	
	/** Will at least be an empty list */
	private final List<TutorAdviceContent> tutorAdvices;
	
	/** Will at least be an empty list */
	private final List<SkillContent> skills;
	
	/** Will at least be an empty list */
	private final List<CustomContent> customs;
	
	private final String transactionId;
	
	//=========================================================================
	// Constructor
	//=========================================================================

	@SuppressWarnings("unchecked")
	public TutorMessageContents(TutorActionLogV4 actionLog){
		TutorMessage logMsg = (TutorMessage) actionLog.getMsg();

		transactionId = getMessageTransactionId(actionLog);
		name = getMessageName(logMsg.getProblemName());
		sai = getMessageSAI(logMsg.getEventDescriptorElement());
		actionEval = getMessageActionEvaluation(logMsg.getActionEvaluationElement());
		
		//apparently these will always give lists of size 0 even if there is nothing in the message.
		tutorAdvices = getMessageTutorAdviceList(logMsg.getTutorAdviceList());
		skills = getMessageSkillList(logMsg.getSkillList());
		customs = getMessageCustomList(logMsg.getCustomFieldList());
	}
	
	//=========================================================================
	// Content extractors
	//=========================================================================

	protected String getMessageTransactionId(TutorActionLogV4 actionLog){
		return actionLog.getTransactionId();
	}
	
	protected NameContent getMessageName(String newName){
		if(newName == null){ return null; }
		
		return new NameContent(newName);
	}
	
	protected SAIContent getMessageSAI(EventDescriptorElement event){
		if(event == null){ return null; }
		
		Element newSAI = parseXML(event.toString());
		@SuppressWarnings("unchecked")
		List<Element> newSAIChildren = ((List<Element>) newSAI.getChildren());
		
		SAIContent messageSAI = new SAIContent();
		for(Element element : newSAIChildren){
			messageSAI.addOneSAI(element.getName(), element.getText());
		}
		
		return messageSAI;
	}
	
	protected ActionEvaluationContent getMessageActionEvaluation(ActionEvaluationElement newActionEval){
		if(newActionEval == null){ return null; }
		
		String newEval = newActionEval.getEvaluation();
		String newHintNum = newActionEval.getCurrentHintNumber();
		String newHintsAvailable = newActionEval.getTotalHintsAvailable();
		String newHintId = newActionEval.getHintId();
		String newClassification = newActionEval.getClassification();

		return new ActionEvaluationContent(newEval, newHintNum, 
				newHintsAvailable, newHintId, newClassification);
	}
	
	
	protected List<TutorAdviceContent> getMessageTutorAdviceList(List<String> newTutorAdvice){
		if(newTutorAdvice == null){ return null; }
		
		ArrayList<TutorAdviceContent> adviceList = new ArrayList<TutorAdviceContent>();
		
		for(int i = 0; i < newTutorAdvice.size(); i++){
			String advice = newTutorAdvice.get(i);
			adviceList.add(new TutorAdviceContent(advice, i));
		}
		
		return Collections.unmodifiableList(adviceList);
	}
	
	@SuppressWarnings("unchecked")
	protected List<SkillContent> getMessageSkillList(List<SkillElement> newSkillsElements){
		if(newSkillsElements == null){ return null; }
		
		ArrayList<SkillContent> skillList = new ArrayList<SkillContent>();
		
		for(int i = 0; i < newSkillsElements.size(); i++){
			SkillElement advice = newSkillsElements.get(i);
			
			ArrayList<String> model_names = new ArrayList<String>();
			for(String model_name : (List<String>) advice.getModelNameList()){
				model_names.add(model_name);
			}
			
			SkillContent newSkill = new SkillContent(
						i,
						advice.getProbability(),
						advice.getName(),
						advice.getCategory(),
						model_names,
						advice.getBuggyFlag());

			skillList.add(newSkill);
		}
		
		return Collections.unmodifiableList(skillList);
	}
	
	protected List<CustomContent> getMessageCustomList(List<CustomFieldElement> customElementsList){
		if(customElementsList == null){ return null; }
		
		ArrayList<CustomContent> customList = new ArrayList<CustomContent>();

		for(int i = 0; i < customElementsList.size(); i++){
			CustomFieldElement customFieldElement = customElementsList.get(i);
			
			CustomContent newCustom = new CustomContent(
					i, customFieldElement.getName(), customFieldElement.getValue());

			customList.add(newCustom);
		}
		
		return Collections.unmodifiableList(customList);
	}
	
	
	protected Element parseXML(String xml){
		SAXBuilder saxBuilder = new SAXBuilder();
		
		Element element = null;
		try {
			Document doc = saxBuilder.build(new StringReader(xml));
			element = doc.getRootElement();
		} catch (JDOMException e) {
			trace.out("log","LogComparison parseXML JDOMException");
			e.printStackTrace();
		} catch (IOException e) {
			trace.out("log","LogComparison parseXML IOException");
			e.printStackTrace();
		}
		
		return element;
	}

	//=========================================================================
	// Iterator
	//=========================================================================
	
	public Iterator<ContentCell> iterator(){
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

	public NameContent getName() {
		return name;
	}
	public SAIContent getSAI() {
		return sai;
	}
	public ActionEvaluationContent getActionEval() {
		return actionEval;
	}
	
	/** @return list of TutorAdviceContents or an empty list */
	public List<TutorAdviceContent> getTutorAdvices() {
		return tutorAdvices;
	}
	/** @return list of SkillContents or an empty list */
	public List<SkillContent> getSkills() {
		return skills;
	}
	/** @return list of CustomContents or an empty list */
	public List<CustomContent> getCustomFields() {
		return customs;
	}
	public String getTransactionId(){
		return transactionId;
	}
}
