/*
 * AnyMatcher.java
 *
 * Created on February 16, 2005, 6:57 PM
 */

package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher;

import java.io.ByteArrayOutputStream;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.cmu.pact.Utilities.trace;

/**
 * 
 * This class matches the student's input against a number range in a
 * pseudotutor.
 * 
 * It can be stored and read out of a brd file.
 * 
 * @author mpschnei
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class AnyMatcher extends Matcher {

	Element element;

	private Document doc;

	private String selectionValue;

	private String actionValue;
	
	private String actorValue;

	private Element selectionElement;

	private Element actionElement;

	private Element actorElement;	
	
	public AnyMatcher(boolean concat, int vector, String value) {
		super(concat, vector);
	}

	public AnyMatcher()
	{
		super();
		initXML();
	}

	/**
	 * Create {@link #doc}, {@link #element} and their children.
	 */
	private void initXML() {
		element = new Element("matcher");
		Element type = new Element("matcherType");
		type.addContent("AnyMatcher");
		element.addContent(type);

		selectionElement = new Element("matcherParameter");
		actionElement = new Element("matcherParameter");
		actorElement = new Element("matcherParameter");
		
		element.addContent(selectionElement);
		element.addContent(actionElement);
		element.addContent(actorElement);
		doc = new Document(element);
	}

	/**
	 * @return deep clone of this instance
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#clone()
	 */
	public Object clone() {
		AnyMatcher m = new AnyMatcher(this.concat, this.vector, "dummy");
		m.copyFrom(this);
		m.initXML();
		m.setSelection(this.selectionValue);
		m.setAction(this.actionValue);
		m.setActor(this.actorValue);
		return m;
	}

	/** 
	 * @see pact.BehaviorRecorder.Matcher.Matcher#toXML()
	 */
	public String toXML() {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			XMLOutputter outp = new XMLOutputter(Format.getPrettyFormat());
			outp.output(doc, out);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		// Remove the first line
		String newString = out.toString();
		newString = newString.substring(newString.indexOf("\n"), newString
				.length());
		return newString;
	}

	public String toXML(String prefix) {
		String xml = toXML();

		xml = prefix + xml;

		xml = xml.replaceAll("\n", "\n" + prefix);
		return xml;
	}

	/**
	 * Check whether the given selection matches.
	 * @param selection: checks only first element
	 * @param action: checks only first element
	 * @param input unchecked
	 * @see pact.BehaviorRecorder.Matcher.Matcher#match(java.util.Vector, java.util.Vector, java.util.Vector)
	 */
	public boolean match(Vector selection, Vector action, Vector input) {
        trace.out ("mps", "any matcher: matching " + selection + ", " + action + " input = " + input);
		if (selection == null || action == null || input == null)
			return false;

		if (!selection.get(0).toString().equals(selectionValue)
				|| !action.get(0).toString().equals(actionValue)) {
            trace.out ("mps", "return false: selectionValue = " + selectionValue + 
                    " action value = " + actionValue);
			return false;
		}
		return true;
	}
	
	public String getSelectionLabelText() {
		return selectionValue;
	}
    public String getActionLabelText() {
    	return actionValue;
    }
    public String getInputLabelText() {
    	return "*";
    }
    public String getSingleLabelText() {
    	return "*";
    }

	/**
	 * Check whether the given selection matches.
	 * @param selection: checks only first element
	 * @param actor require this match even if others are wildcards
	 * @see pact.BehaviorRecorder.Matcher.Matcher#match(java.util.Vector)
	 */
	public boolean matchForHint(Vector selection, Vector action, String actor)
	{
        if (trace.getDebugCode("mps")) trace.out ("mps", "any matcher: matching " + selection );
        if (!matchActor(actor))
        	return false;
		if (selection == null )
			return false;

		if (!selection.get(0).toString().equals(selectionValue)
				) {
            trace.out ("mps", "return false: selectionValue = " + selectionValue);
			return false;
		}
		
		if (action != null &&
			action.get(0).toString().equals(actionValue))
			return false;
		
		return true;
	}
	
	/**
	 * Override of superclass method for any-match of input.
	 * @param v student input: ignored
	 * @return always return true
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#matchInput(java.util.Vector)
	 */
	protected boolean matchInput(Vector v) {
		return true;
	}

	/**
	 * Anything works
	 */
	protected boolean matchSingle(String s) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pact.BehaviorRecorder.Matcher.Matcher#setActionLabel(pact.BehaviorRecorder.ActionLabel)
	 */
	public void setAction(String action) {
        actionElement.addContent(action);
        trace.out ("mps", "   set action: " + action);
        actionValue = action;

	}

	public String getAction() {
		// TODO Auto-generated method stub
		return actionValue;
	}
	public void setActor(String actor){
		actorElement.addContent(actor);
		actorValue = actor;
		
	}
	
	public String getActor()
	{	
		return actorValue;
	}
	public void setSelection(String selection) {
        trace.out ("mps", "   set selection: " + selection);
        selectionElement.addContent(selection);
        selectionValue = selection;
	}

	public String getSelection() {
		return selectionValue;
	}

	/**
	 * Sets parameter by position. Meanings of index values:<ul>
	 *   <li>index==0: selection</li>
	 *   <li>index==1: action</li>
	 *   <li>index==2: actor</li>
	 * </ul>
	 * @param element
	 * @param index
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#setParameterInternal(org.jdom.Element, int)
	 */
	public void setParameterByIndex(String stringValue, int index) {
		if (index == 0)
			setSelection(stringValue);
		if (index == 1)
			setAction (stringValue);
		if (index == 2)
			setActor (stringValue);
	}
	
	/**
	 * Override to account for this class's disuse of getDefaultSelection()
	 * et al.
	 * @param element
	 * @param index
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#setParameter(org.jdom.Element, int)
	 */
	public void setParameter(Element element, int index) {
		super.setParameter(element, index);
		String name = element.getAttributeValue("name");
		if (name != null && name.length() > 0) {
			if (name.equalsIgnoreCase("selection")) {
				setSelection( element.getText());
				return;
			}
			if (name.equalsIgnoreCase("action")) {
				setAction( element.getText());
				return;
			}
			if (name.equalsIgnoreCase("actor")){
				setActor( element.getText());
				return;
			}
		}
		setParameterInternal(element, index);
	}
	
	/**
	 * Equivalent to {@link #setParameterByIndex(String, int)}
	 * called with the text of the given element.
	 * @param element
	 * @param index
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#setParameterInternal(org.jdom.Element, int)
	 */
	protected void setParameterInternal(Element element, int index) {
		setParameterByIndex(element.getText(), index);
	}
	
	/*
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#getParameter(int)
	 */
	public MatcherParameter getMatcherParameter(int index) {
		if (index == 0)
			return new MatcherParameter("selection", getParameter(index));
		if (index == 1)
			return new MatcherParameter("action", getParameter(index));
		if (index == 2)
			return new MatcherParameter("actor", getParameter(index));
		return null;
	}
	
	/*
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#getParameter(int)
	 */
	public Object getParameter(int index) {
		if (index == 0)
			return getSelection();
		if (index == 1)
			return getAction();
		if (index == 2)
			return getActor();
		return null;
	}
	/*
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#getParameterCount()
	 */
	public int getParameterCount() {
		return single ? 0 : 3;
	}
	/* (non-Javadoc)
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#getType()
	 */
	public String getMatcherType() {
		return ANY_MATCHER;
	}
	
	public String getMatcherTypeText() {
		return "Any";
	}
	
	public String getMatcherClassType() {
		return "AnyMatcher";
	}
	public String getActionMatcherType() {
		return "Exact";}
	public String getInputMatcherType() {
		return getMatcherTypeText();}
	public String getSelectionMatcherType() {
		return "Exact";}
}
