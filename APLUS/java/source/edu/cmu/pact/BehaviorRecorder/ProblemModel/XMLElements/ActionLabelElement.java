package edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements;
import java.util.Vector;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.megginson.sax.DataWriter;


public class ActionLabelElement {
	private String elementName = "actionLabel";

	private String attrpreferPathMark;
	private String minTraversals;
	private String maxTraversals;
	
	private String studentHintRequest;
	private String stepSuccessfulCompletion;
	private String stepStudentError;
	
	private String uniqueID;
	private MessageElement message;
	private String buggyMessage;
	private String successMessage;
	
	private Vector hintMessages;
	
	private String actionType;
	private String oldActionType;
	private MatcherElement matcher;
	
	private String callbackFn;
	
	//for a vector matcher
	private VectorMatcherElement vectorMatchers;
	
	private String checkedStatus;
	
	public ActionLabelElement() {
			
	}
	
	// print in XML format
	public void printXML(DataWriter w) throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		if (attrpreferPathMark != null)
			atts.addAttribute("", "preferPathMark", "", "String", attrpreferPathMark);
		
		if (minTraversals != null)
			atts.addAttribute("", "minTraversals", "", "String", minTraversals);
		if (maxTraversals != null)
			atts.addAttribute("", "maxTraversals", "", "String", maxTraversals);
				
		w.startElement("", elementName, "", atts);	
			
			if (studentHintRequest != null)
				w.dataElement("studentHintRequest", studentHintRequest);
			if (stepSuccessfulCompletion != null)
				w.dataElement("stepSuccessfulCompletion", stepSuccessfulCompletion);
			if (stepStudentError != null)
				w.dataElement("stepStudentError", stepStudentError);
			
			if (uniqueID != null)
				w.dataElement("uniqueID", uniqueID);
			if (message != null)
				message.printXML(w);
			if (buggyMessage != null)
				w.dataElement("buggyMessage", buggyMessage);			
			if (successMessage != null)
				w.dataElement("successMessage", successMessage);
			if (hintMessages != null) {
				String Hintmsg;
				for (int i=0; i<hintMessages.size(); i++) {
					Hintmsg = (String) hintMessages.elementAt(i);
					w.dataElement("hintMessage", Hintmsg);
				}
			}
			if(callbackFn !=null)
				w.dataElement("callbackFn", callbackFn);
			if (actionType != null)
				w.dataElement("actionType", actionType);
			if (oldActionType != null)
				w.dataElement("oldActionType", oldActionType);
			if (checkedStatus != null)
				w.dataElement("checkedStatus", checkedStatus);
			if (matcher != null) 
				matcher.printXML(w);
			else if (vectorMatchers != null) //don't print them both ...
				vectorMatchers.printXML(w);
			
		w.endElement(elementName);
	}
	
	// methods to add sub elements to actionLabel	
	public void addpreferPathMark(boolean in) {
		attrpreferPathMark = (new Boolean(in)).toString();
	}
	
	public void addstudentHintRequest(String in) {
		studentHintRequest = in;
	}
	
	public void addstepSuccessfulCompletion(String in) {
		stepSuccessfulCompletion = in;
	}
	
	public void addstepStudentError(String in) {
		stepStudentError = in;
	}	
	
	public void adduniqueID(int in) {
		uniqueID = new Integer(in).toString();
	}
	
	public void addmessage(MessageElement in) {
		message = in;
	}

	public void addbuggyMessage(String in) {
		buggyMessage = in;
	}
	
	public void addsuccessMessage(String in) {
		successMessage = in;
	}
	
	public void addhintMessage(String in) {
		if (hintMessages == null)
			hintMessages = new Vector();
		
		hintMessages.addElement(in);
	}
	

	public void setCallbackFn(String fn) {
		
		callbackFn = fn;
		
	}
	public void addactionType(String in) {
		actionType = in;
	}
	
	public void addoldActionType(String in) {
		oldActionType = in;
	}
	
	public void addcheckedStatus(String in) {
		checkedStatus = in;
	}
	
	public void addMatcher(MatcherElement in) {
		matcher = in;
	}
	
	public void addVectorMatcher(VectorMatcherElement in)
	{
		vectorMatchers = in;
	}
	
	public void setMinTraversals(String minTraversals) {
		this.minTraversals = minTraversals;
	}
	
	public void setMaxTraversals(String maxTraversals) {
		this.maxTraversals = maxTraversals;
	}
}
