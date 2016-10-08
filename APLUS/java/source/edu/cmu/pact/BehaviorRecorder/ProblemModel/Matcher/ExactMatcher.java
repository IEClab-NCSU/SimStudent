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

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerSAI;
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
public class ExactMatcher extends Matcher {

	Element element;

	private Document doc;

	private Element selectionElement;

	private Element actionElement;

	private Element inputElement;
	
	private Element actorElement;
	
	public ExactMatcher(boolean concat, int vector, String value)
	{
		super(concat, vector);
		
		setSingle(value);
	}
	
	/**
	 * Constructor for old Exact Matcher
	 */
	public ExactMatcher()
	{
		super();
		initXML();
	}

	private void initXML() {
		element = new Element("matcher");
		Element type = new Element("matcherType");
		type.addContent("ExactMatcher");
		element.addContent(type);
		
		selectionElement = new Element("matcherParameter");
		selectionElement.setAttribute("name","Selection");
		actionElement = new Element("matcherParameter");
		actionElement.setAttribute("name","Action");
		inputElement = new Element ("matcherParameter");
		inputElement.setAttribute("name","Input");
		actorElement = new Element("matcherParameter");
		actorElement.setAttribute("name", "Actor");
		
		element.addContent(selectionElement);
		element.addContent(actionElement);
		element.addContent(inputElement);
		element.addContent(actorElement);
		
		doc = new Document(element);
	}

	/**
     * @param currentSelection
     * @param currentAction
     * @param currentInput
     */
	 public ExactMatcher(Vector currentSelection, Vector currentAction, Vector currentInput) {
	        this();
	        setDefaultSelection((String) currentSelection.elementAt(0));
	        setDefaultAction ((String) currentAction.elementAt(0));
	        setDefaultInput ((String) currentInput.elementAt(0));
	        setDefaultActor ("Student");
	    }
	 /*
	  * EJ: Another constructor in case it's collarboration action
	  * @see the default constructor
	  */
    public ExactMatcher(Vector currentSelection, Vector currentAction, Vector currentInput, Vector currentActor) {
        this();
        setDefaultSelection((String) currentSelection.elementAt(0));
        setDefaultAction ((String) currentAction.elementAt(0));
        setDefaultInput ((String) currentInput.elementAt(0));
        setDefaultActor (currentActor == null || currentActor.size() < 1 ?
        		"Student" : (String) currentActor.elementAt(0));
    }

	/**
	 * @return deep clone of this instance
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#clone()
	 */
	public Object clone() {
		ExactMatcher m = new ExactMatcher(this.concat, this.vector, this.getSingle());
		m.copyFrom(this);
		return m;
	}

	/**
	 * Service method for {@link #clone()} here and in subclasses. Copies private fields.
	 * @param m instance to copy from
	 */
	protected void copyFrom(ExactMatcher m) {
		super.copyFrom(m);
		initXML();
		setDefaultSelection(m.getDefaultSelection());
		setDefaultAction(m.getDefaultAction());
		setDefaultInput(m.getDefaultInput());
		setDefaultActor(m.getDefaultActor());
	}

    protected Document doc() {
        return doc;
    }
    
    /*
	 * (non-Javadoc)
	 * 
	 * @see pact.BehaviorRecorder.Matcher.Matcher#toXML()
	 */
	public String toXML() {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			XMLOutputter outp = new XMLOutputter(Format.getPrettyFormat());
			outp.output(doc(), out);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see pact.BehaviorRecorder.Matcher.Matcher#match(java.util.Vector,
	 *      java.util.Vector, java.util.Vector)
	 */
	public boolean match(Vector selection, Vector action, Vector input) {
		return match(selection, action, input, getActor());
	}
	public boolean match(Vector selection, Vector action, Vector input, String actor) {
        boolean comparisonValue = false;

        String actualInput = input.elementAt(0).toString();
		String actualSelection = selection.elementAt(0).toString();
		String actualAction = action.elementAt(0).toString();
		String expectedInput = getInput().toString();
		String expectedSelection = getSelection().toString();
		String expectedAction = getAction().toString();
		
		//trace.out ("actual: " + actualInput + ", " + actualSelection + ", " + actualAction);
		//trace.out ("expected: " + expectedInput + ", " + expectedSelection + ", " + expectedAction);
		//trace.out ("case insensitive = " + getCaseInsensitive());
		
		if (getCaseInsensitive())
            comparisonValue = (
                    expectedInput.equalsIgnoreCase(actualInput) &&
                    expectedSelection.equalsIgnoreCase (actualSelection) &&
                    expectedAction.equalsIgnoreCase(actualAction) 
                     );
        else
            comparisonValue = (
                    expectedInput.equals(actualInput) &&
                    expectedSelection.equals(actualSelection) &&
                    expectedAction.equals(actualAction) 
                    
            );
        if (comparisonValue) trace.out("matcher", getActor() + " vs " + actor);
        comparisonValue= comparisonValue && matchActor(actor);

        return comparisonValue;
	}
	
	public boolean matchSingle(String s)
	{
		return singleValue.equals(s);
	}

	public boolean matchForHint(Vector selection, Vector action, String actor)
	{
		if (!matchActor(actor))
			return false;
		if(selection==null)
			return false;
		if(selection.elementAt(0)==null)
			return false;
	 String actualSelection = selection.elementAt(0).toString();
	 String expectedSelection = getSelection().toString();
	 boolean caseInsensitve = getCaseInsensitive();
	 boolean matchesSelection = (caseInsensitve) ? expectedSelection.equals(actualSelection) : 
		 											expectedSelection.equalsIgnoreCase(actualSelection);
	if (!matchesSelection)
		return false;
	 
	 boolean matchesAction = true;
	 if (action != null && action.elementAt(0) != null) {
		 String actualAction = action.elementAt(0).toString();
		 String expectedAction = getAction().toString();
		 matchesAction = (caseInsensitve) ? expectedAction.equals(actualAction) :
			 									expectedAction.equalsIgnoreCase(actualAction);
	 }
	 
	 return matchesAction;

	}
	
    /*
     * (non-Javadoc)
     * 
     * @see pact.BehaviorRecorder.Matcher.Matcher#setActionLabel(pact.BehaviorRecorder.ActionLabel)
     */
    public void setDefaultAction(String action) {
    	if(!single)
    	{
	        actionElement.removeContent();
	        actionElement.addContent(action);
    	}
        super.setDefaultAction (action);
    }

    public void setDefaultInput(String inputValue) {
    	if(!single)
    	{
    		inputElement.removeContent();
    		inputElement.addContent(inputValue);
    	}
        
        super.setDefaultInput (inputValue);
	}

    public void setDefaultSelection(String defaultSelection) {
    	if(!single)
    	{
	    	selectionElement.removeContent();
	        selectionElement.addContent(defaultSelection);
    	}
        super.setDefaultSelection(defaultSelection);
    }
    
    public void setDefaultActor(String defaultActor){
    	if(!single)
    	{
    		actorElement.removeContent();
    		actorElement.addContent(defaultActor);
    	}
    	super.setDefaultActor(defaultActor);
    }

    public void setSingle(String single)
    {
    	if(single == null)
    		return;
    	super.setSingle(single);
    }
    
	/**
	 * Sets parameter by position. Meanings of index values:
	 * <table border="0" cellspacing="2">
	 *   <tr><th>index</th><th>current version</th><th>versionIsBeforeMatcherParameterStd</th></tr>
	 *   <tr><td>0</td>    <td>selection</td>      <td>action</td></tr>
	 *   <tr><td>1</td>    <td>action</td>         <td>input</td></tr>
	 *   <tr><td>2</td>    <td>input</td>          <td>selection</td></tr>
	 *   <tr><td>3</td>    <td>actor</td>          <td>actor</td></tr>
	 * </table>
	 * @param element
	 * @param index
	 * @return true if assigned a parameter
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#setParameterInternal(org.jdom.Element, int)
	 */
	protected void setParameterInternal(Element element, int index) {
		if (versionIsBeforeMatcherParameterStd(element)) {
			switch(index) {
			case 0:
				setDefaultAction (element.getText()); return;
			case 1:
				setDefaultInput(element.getText()); return;
			case 2:
				setDefaultSelection(element.getText()); return;
			case 3:
				setDefaultActor(element.getText()); return;
			default:
				return;
			}
		} else               // std order of parameters
			setParameterByIndex(element.getText(), index);
	}
	
	/**
	 * Sets parameter by position. Meanings of index values:
	 * <table border="0" cellspacing="2">
	 *   <tr><th>index</th><th>current version</th></tr>
	 *   <tr><td>0</td>    <td>selection</td>      </tr>
	 *   <tr><td>1</td>    <td>action</td>         </tr>
	 *   <tr><td>2</td>    <td>input</td>          </tr>
	 *   <tr><td>3</td>    <td>actor</td>          </tr>
	 * </table>
	 * @param stringValue
	 * @param index
	 * @return
	 */
	public void setParameterByIndex(String stringValue, int index) {
		switch(index) {
		case 0:
			setDefaultSelection(stringValue); return;
		case 1:
			setDefaultAction (stringValue); return;
		case 2:
			setDefaultInput(stringValue); return;
		case 3:
			setDefaultActor(stringValue); return;
		default:
			trace.err("ExactMatcher.setParameterByIndex(): unknown index "+index);
			return;
		}
	}
	
	public Object getParameter(int index) {
	    if(single)
	    {
	    	if(index == 0)
	    		return singleValue;
	    	return null;
	    }
	    
		switch(index) {
		case 0:
            return getDefaultSelection();
		case 1:
            return getDefaultAction();
		case 2:
            return getDefaultInput();
		case 3:
			return getDefaultActor();
		default:
			return null;
		}
	}
	public int getParameterCount()
	{
		return single ? 1 : 4;
	}
	
	public MatcherParameter getMatcherParameter(int index) {
		if(single)
			return new MatcherParameter("single", getParameter(index));
		return super.getMatcherParameter(index);
	}

	/* (non-Javadoc)
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#getType()
	 */
	public String getMatcherType() {
		return EXACT_MATCHER;
	}
	
	public String getMatcherTypeText() {
		return "Exact";
	}
	
	public String getMatcherClassType() {
		return "ExactMatcher";
	}
	
	public String getSelectionLabelText() {
		return getSelection();
	}
    public String getActionLabelText() {
    	return getAction();
    }
    public String getInputLabelText() {
    	return getInput();
    }
    public String getSingleLabelText() {
    	return singleValue;
    }
	
	public String toString() {
		if(single && concat)
			return singleValue;
		else
			return super.toString();
	}
	
	public String[] getValuesVector() {
		//include empty strings, but not the last one ...
		String[] arr = singleValue.split("\n", -1);
		return arr;
	}
	public String getActionMatcherType() {
		return getMatcherTypeText();}
	public String getInputMatcherType() {
		return getMatcherTypeText();}
	public String getSelectionMatcherType() {
		return getMatcherTypeText();}
}
