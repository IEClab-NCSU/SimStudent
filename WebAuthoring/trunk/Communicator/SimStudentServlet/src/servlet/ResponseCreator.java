package servlet;
import interaction.SAI;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for creating responses from ResponseMessage objects
 * @author Patrick Nguyen
 *
 */
public class ResponseCreator {
	/**
	 * Given an array of ResponseMessage objects, creates an xml string to send as a response to the client
	 * @param responses
	 * @return The xml string representing the response objects
	 */
	public static String createXML(ResponseMessage[] responses)
	{
//		System.out.println("Response message length: " + responses.length);
		if(responses.length==0) return "";
		if(responses.length==1 && responses[0] != null) 
			return responses[0].toXML();

		
		String response="<MessageBundle>";
		for(ResponseMessage resp : responses)
			if(resp != null)
				response+=resp.toXML();
		response+="</MessageBundle>";
		return response;
	}
	
	public static String createTutorPerformedResponse(SAI sai){
		TutorPerformResponse resp1=new TutorPerformResponse();
		
		resp1.setSai(sai);
		
		resp1.setTrigger("DATA");
		resp1.setSubtype("tutor-performed");
		resp1.setTransactionID("123456789");
		
		AssociatedRulesTutorResponse resp2=new AssociatedRulesTutorResponse();
		
		resp2.setIndicator("Correct");
		resp2.setSai(sai);
		
		List<String> selections2 = new ArrayList<String>(sai.getSelection());
		List<String> actions2 = new ArrayList<String>();
		actions2.add("UpdateTextArea");
		List<String> inputs2 = new ArrayList<String>();
		inputs2.add("-1");
		SAI sai2 = new SAI(selections2,actions2,inputs2);
		resp2.setStudentSAI(sai2);
		
		List<String> rules=new ArrayList<String>();
		rules.add("unnamed");
		resp2.setRules(rules);
		
		resp2.setActor("Tutor (unevaluated)");
		resp2.setSkillBarDelimiter("");
		resp2.setTransactionID("123456789");
		resp2.setLogAsResult(true);
		resp2.setEndOfTransaction(true);
		
		return createXML(new ResponseMessage[]{resp1});
	}
	
	/**
	 * Method to create the tutor response for the hints.
	 * @param hint
	 * @return
	 */
	public static String createTutorPerformedResponseHint(ArrayList<String> hintLint){
		//String tmp="<message><verb>SendNoteProperty</verb><properties><MessageType>BuggyMessage</MessageType><BuggyMsg>I'm sorry, but you are not done yet. Please continue working.</BuggyMsg><transaction_id>FD510A231DE71670</transaction_id><end_of_transaction>true</end_of_transaction></properties></message>";
	
		//String tmp="<message><verb>SendNoteProperty</verb><properties><MessageType>ShowHintsMessage</MessageType><HintsMessage><value>level 1</value></HintsMessage><HintsMessage><value>level 2</value></HintsMessage><transaction_id>FD510A231DE71670</transaction_id><end_of_transaction>true</end_of_transaction></properties></message>";
		
		
	
		TutorPerformResponse resp1=new TutorPerformResponse();
		resp1.setMessageType("ShowHintsMessage");
		resp1.setHintMessages(hintLint);
		//resp1.setHint(hint);
		

		resp1.setTransactionID("123456789");
		
		return createXML(new ResponseMessage[]{resp1});
	}

	
	
}
