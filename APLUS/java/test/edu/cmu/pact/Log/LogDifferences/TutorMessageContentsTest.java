package edu.cmu.pact.Log.LogDifferences;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import edu.cmu.pact.Log.DataShopMessageObject;
import edu.cmu.pact.Log.TutorActionLogV4;
import edu.cmu.pact.Log.LogDifferences.Content.CustomContent;
import edu.cmu.pact.Log.LogDifferences.Content.SkillContent;
import edu.cmu.pact.Log.LogDifferences.Content.TutorAdviceContent;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pslc.logging.TutorMessage;

public class TutorMessageContentsTest extends TestCase {
	
	String testXMLString = "%3Ctutor_message+context_message_id%3D%22Cdb4b7d38-c822-4e71-ba80-b1b62d9ac699%22%3E%0A%09%3Cproblem_name%3E1416%3C%2Fproblem_name%3E%0A%09%3Csemantic_event+transaction_id%3D%2241CDEE6F9E4C6714%22+name%3D%22RESULT%22%2F%3E%0A%09%3Cevent_descriptor%3E%0A%09%09%3Cselection%3EfirstDenConv%3C%2Fselection%3E%0A%09%09%3Caction%3EUpdateTextField%3C%2Faction%3E%0A%09%09%3Cinput%3E24%3C%2Finput%3E%0A%09%3C%2Fevent_descriptor%3E%0A%09%3Caction_evaluation%3ECorrect%3C%2Faction_evaluation%3E%0A%09%3Ctutor_advice%3EGood+job%21%3C%2Ftutor_advice%3E%0A%09%3Cskill%3E%0A%09%09%3Cname%3Eunnamed%3C%2Fname%3E%0A%09%3C%2Fskill%3E%0A%09%3Ccustom_field%3E%0A%09%09%3Cname%3Etutor_event_time%3C%2Fname%3E%0A%09%09%3Cvalue%3E2014-05-21+17%3A46%3A11.528+UTC%3C%2Fvalue%3E%0A%09%3C%2Fcustom_field%3E%0A%09%3Ccustom_field%3E%0A%09%09%3Cname%3Estep_id%3C%2Fname%3E%0A%09%09%3Cvalue%3E19%3C%2Fvalue%3E%0A%09%3C%2Fcustom_field%3E%0A%3C%2Ftutor_message%3E";
	TutorMessageContents test = new TutorMessageContents(stringToTutorActionLog(testXMLString));
	
	
	public void testNotNull(){
		assertNotNull("LogDiff was given a message. It shouldn't be null.", test);
	}
	
	public void testName(){
		String name = test.getName().getName();
		assertEquals(name, "1416");
	}
	
	public void testSelections(){
		ArrayList<String> selections = test.getSAI().getSelection();
		assertEquals("Selections should have one entry 'firstDenConv'", selections.get(0), "firstDenConv");
	}
	
	public void testActions(){
		ArrayList<String> actions = test.getSAI().getAction();
		assertEquals("Actions should have one entry 'UpdateTextField'", actions.get(0), "UpdateTextField");
	}
	
	public void testInputs(){
		ArrayList<String> inputs = test.getSAI().getInput();
		assertEquals("Actions should have one entry '24'", inputs.get(0), "24");
	}
	
	public void testActionEvaluation(){
		String evaluation = test.getActionEval().getEvaluation();
		String hintNum = test.getActionEval().getHintNum();
		String totalHints = test.getActionEval().getTotalHints();
		String hintId = test.getActionEval().getHintId();
		String classification = test.getActionEval().getClassification();
		
		assertTrue("",
				evaluation.equals("Correct") &&
				hintNum == null &&
				totalHints == null &&
				hintId == null &&
				classification == null);
	}
	
	public void testTutorAdvice(){
		List<TutorAdviceContent> advice = test.getTutorAdvices();
		
		assertEquals(advice.get(0).getTutorAdvice(), "Good job!");
	}
	
	public void testSkill(){
		List<SkillContent> skills = test.getSkills();
		SkillContent skill1 = skills.get(0);
		
//		trace.out(skill1.getDiffName());
//		trace.out(skill1.getDiffBuggy());
//		trace.out(skill1.getDiffCategory());
//		trace.out(skill1.getDiffProbability());
//		trace.out(skill1.getDiffModel_names());
		
		assertTrue(skill1.getName().equals("unnamed") &&
				skill1.getBuggy() == false &&
				skill1.getCategory().equals("") && //skill elements are made with at least a name and category, so no category is empty string
				skill1.getProbability() == null &&
				skill1.getModel_names().size() == 0);
	}
	
	public void testCustom(){
		List<CustomContent> custom = test.getCustomFields();
		
		assertTrue(custom.get(0).getName().equals("tutor_event_time") &&
				custom.get(1).getName().equals("step_id"));
	}
	
	
	
	
	
	
	//=========================================================================
	//	testing when everything is missing in the action log
	//=========================================================================
	
	String testXMLString2 = "%3Ctutor_message+context_message_id%3D%22Cdb4b7d38-c822-4e71-ba80-b1b62d9ac699%22%3E%0A%3C%2Ftutor_message%3E";
	TutorMessageContents test2 = new TutorMessageContents(stringToTutorActionLog(testXMLString2));
	
	public void testNoTransactionId(){
		assertNull(test2.getTransactionId());
	}
	
	public void testNoName(){
		assertNull(test2.getName());
	}
	
	public void testNoSAI(){
		assertNull(test2.getSAI());
	}
	
	public void testNoActionEvaluation(){
		assertNull(test2.getActionEval());
	}
	
	public void testNoTutorAdvice(){
		assertEquals(0, test2.getTutorAdvices().size());
	}
	
	public void testNoSkill(){
		assertEquals(0, test2.getSkills().size());
	}
	
	public void testNoCustom(){
		assertEquals(0, test2.getCustomFields().size());
	}
	
	//=========================================================================
	//	Utility Methods
	//=========================================================================

		private static String escapeXML(String input){
		String result = null;
		try {
			result = URLDecoder.decode(input, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			trace.out("Failed escapeXML");
		}
		
		return result;
	}
	
	private static TutorActionLogV4 stringToTutorActionLog(String unescapedXML){
		String escapedString = escapeXML(unescapedXML);
		Element element = parseXML(escapedString);
		TutorActionLogV4 result = createTutorActionLogFromXML(element);
		
		return result;
	}
	
	//Taken from LogComparison
	private static Element parseXML(String xml){
		Element element = null;
		try {
			Document doc = new SAXBuilder().build(new StringReader(xml));
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

	private static TutorActionLogV4 createTutorActionLogFromXML(Element toolMessageElement){
		XMLOutputter outputter = new XMLOutputter();
		//turn XML element into DSMO and then get its TutorMessage
		Element tutorMessageElement = getOnlyToolTutorMessage(toolMessageElement);
		DataShopMessageObject tutorMessageObject = new DataShopMessageObject(
				outputter.outputString(tutorMessageElement), null); //outputter.outputString(tutorMessageElement), controller.getLogger());
		TutorMessage tutorMessage = ((TutorMessage) tutorMessageObject.getLogMsg().getMsg());
		
		//Add all custom fields to the TutorMessage 
		LinkedHashMap<String,String> customFields = getCustomFieldsFromXML(tutorMessageElement);
		Set<String> customFieldsKeys = customFields.keySet();
		Iterator<String> itr = customFieldsKeys.iterator();
		while(itr.hasNext()){
			String customFieldName = itr.next();
			String customFieldValue = customFields.get(customFieldName);
			
			tutorMessage.addCustomField(customFieldName, customFieldValue);
		}
		
		TutorActionLogV4 tutorMessageLog = tutorMessageObject.getLogMsg();
		return tutorMessageLog;
	}
	
	private static LinkedHashMap<String, String> getCustomFieldsFromXML(Element message){
		Element tutorToolMessage = getOnlyToolTutorMessage(message);
		LinkedHashMap<String, String> customFields = new LinkedHashMap<String, String>();
		@SuppressWarnings("unchecked")
		List<Element> customFieldElements = tutorToolMessage.getChildren("custom_field");
		
		for(Element elt : customFieldElements){
			String name = elt.getChild("name").getText();
			String value = elt.getChild("value").getText();
			
			customFields.put(name, value);
		}
		return customFields;
	}
	
	private static Element getOnlyToolTutorMessage(Element message) {
		//Extract the tool/tutor message element from any Element
		if(message.getName().equals("log_action")){
			message = message.getChild("tutor_related_message_sequence");
		}
		if(message.getName().equals("tutor_related_message_sequence")){
			if(message.getChild("tool_message") != null){
				message = message.getChild("tool_message");
			}
			else if(message.getChild("tutor_message") != null){
				message = message.getChild("tutor_message");
			}
			else{
				return null;
			}
		}
		return message;
	}
}
