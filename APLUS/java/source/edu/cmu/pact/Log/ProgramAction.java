package edu.cmu.pact.Log;

import org.jdom.Element;
import org.jdom.JDOMException;

public class ProgramAction extends AuthorActionLog  {
	/**
	 * for author action log
	 */
	
	/** Element type of author action. */
	public static final String PROGRAM_MSG_ELEMENT = "program_action_message";
		
	
	/**
	 * Constructor using given data.
	 * @param text value for text; uses "" if null
	 * @param currentHintNumber value for the current hint number
	 * @param totalHintsAvailable value for total number of hints in sequence
	 */
	public  ProgramAction(String actionType, 
						String argument,
						String result,
						String result2) {
		
		super(actionType, argument, result, result2);
		
		this.setTopElementType(PROGRAM_MSG_ELEMENT);
	}
	
	/**
	 * Construct from XML Element.
	 *
	 * @param  elt event_descriptor element
	 * @exception JDOMException if elt is not an action_evaluation or
	 *                a hint number
	 */
	private ProgramAction (Element elt) throws JDOMException {
		super();
		
		AuthorActionLog.checkElementName(elt, PROGRAM_MSG_ELEMENT);
		
		parseActionMsgElement(elt);
		
		return;
	}
	
	/** Generate an element for this object.
	 @return DOM {@link org.jdom.Element Element} */
	public Element getElement() {
		Element rootEle = new Element(PROGRAM_MSG_ELEMENT);
		
		addChildElements (rootEle);
		
		return rootEle;
	}
}
