/*
 * Created on Nov 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher;

import java.io.ByteArrayOutputStream;
import java.security.InvalidParameterException;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.cmu.pact.Utilities.trace;

/**
 * 
 * This class matches the student's input against a number range in a pseudotutor.
 * 
 * It can be stored and read out of a brd file.
 * 
 * @author mpschnei
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RangeMatcher extends Matcher {

	public static final int DEFAULT_MINIMUM = 0;
	public static final int DEFAULT_MAXIMUM = 100;
	private double minimum = DEFAULT_MINIMUM;
	private double maximum = DEFAULT_MAXIMUM;
	private String minimumStr = Double.toString(DEFAULT_MINIMUM);
	private String maximumStr = Double.toString(DEFAULT_MAXIMUM);
	Element element;
	private Document doc;
	private Element minimumElement;
    private Element maximumElement;
    private Element selectionElement;
    private Element actionElement;
    private Element actorElement;
    private String action;
    private String selection;
    private String actor;
	
    /**
     * Creates a single matching RangeMatcher
     * @param concat - whether it is a concatenation matching
     * @param vector - the (sai) vector associated
     * @param text - text in the form of [ (min) , (max) ], leave null to have no effect
     */
	public RangeMatcher(boolean concat, int vector, String text) throws NumberFormatException
	{
		super(concat, vector);
		
		String minText = (text == null || text.length() == 0) ? "0" : text, maxText = "0";
		if(text != null)
		{
			int minIndex, commaIndex, maxIndex;
			if((minIndex = text.indexOf("[")) >= 0 &&
					(commaIndex = text.indexOf(",", minIndex + 1)) >= 0 &&
					(maxIndex = text.indexOf("]", commaIndex + 1)) >= 0)
			{
				minText = text.substring(minIndex + 1, commaIndex);
				maxText = text.substring(commaIndex + 1, maxIndex);
			}
			setMinimum(minText);
			setMaximum(maxText);
		}
	}
	
	/**
	 * Creates a single matching RangeMatcher
	* @param concat - whether it is a concatenation matching
     * @param vector - the (sai) vector associated
	 * @param minText - minimum text
	 * @param maxText - maximum text
	 */
	public RangeMatcher(boolean concat, int vector, String minText, String maxText)
	{
		super(concat, vector);
		setMinimum(minText);
		setMaximum(maxText);
	}
	
	/**
	 * Constructor for the old Range Matcher
	 */
	public RangeMatcher()
	{
		initXML();
		setMinimum(Double.toString(DEFAULT_MINIMUM));
		setMaximum(Double.toString(DEFAULT_MAXIMUM));
	}

	private void initXML() {
		element = new Element ("matcher");
		Element type = new Element ("matcherType");
		type.addContent("RangeMatcher");
		element.addContent(type);
		
		minimumElement = new Element ("matcherParameter");
		minimumElement.addContent(minimumStr);
		maximumElement = new Element ("matcherParameter");
		maximumElement.addContent(maximumStr);
		selectionElement = new Element ("matcherParameter");
        actionElement = new Element ("matcherParameter");
        actorElement = new Element("matcherParameter");
        element.addContent (minimumElement);
        element.addContent (maximumElement);
        element.addContent (selectionElement);
        element.addContent (actionElement);
        element.addContent (actorElement);
        
        doc = new Document(element);
	}

	/**
	 * @return deep clone of this instance
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#clone()
	 */
	public Object clone() {
		RangeMatcher m = new RangeMatcher(this.concat, this.vector,
				this.minimumStr, this.maximumStr);
		m.copyFrom(this);
		m.initXML();
		m.setSelection(this.getSelection());
		m.setAction(this.getAction());
		m.setActor(this.getActor());
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
			return "";
		} 
		// Remove the first line
		String newString = out.toString();
		newString = newString.substring (newString.indexOf("\n"), newString.length());
		return newString;
	}

	public String toXML(String prefix) {
		String xml = toXML();
		
		xml = prefix + xml;
		
		xml = xml.replaceAll("\n", "\n" + prefix);
		return xml;
	}
	

	public double getMaximum() {
		return maximum;
	}

	public double getMinimum() {
		return minimum;
	}

	public String getMaximumStr() {
		return maximumStr;
	}

	public String getMinimumStr() {
		return minimumStr;
	}
	
	/**
	 * Sets the minimum. If text is not a parseable Double, then handle the exception internally
	 * and set {@link #minimum} = {@link #DEFAULT_MINIMUM}. 
	 * @param min - the input text; no-op if value is null
	 */
	public void setMinimum(String min) {
		if (trace.getDebugCode("range")) trace.out("range", "setMinimum() old \""+minimumStr+"\"="+minimum+", new "+min);		
        if(min == null)
        	return;
		minimumStr = min;
		try {
            minimum = Double.parseDouble (minimumStr);
        } catch (NumberFormatException e) {
        	// Allow the reading of the BRD file to continue, 
        	// but it will not allow the link to be successfully traced
        	trace.err("Bad numeric \""+min+"\"for range minimum: "+e);
        	minimum = DEFAULT_MINIMUM;
        }
        if(minimumElement != null)
        	minimumElement.addContent(minimumStr);
	}
	
	/**
	 * Sets the maximum. If text is not a parseable Double, then handle the exception internally
	 * and set {@link #maximum} = {@link #minimum}. 
	 * @param max - the input text; no-op if value is null
	 */
	public void setMaximum(String max)
	{
		if (trace.getDebugCode("range")) trace.out("range", "setMaximum() old \""+maximumStr+"\"="+maximum+", new "+max);
		if(max == null)
        	return;
		maximumStr = max;
		try {
			maximum = Double.parseDouble (max);
        } catch (NumberFormatException e) {
        	// Allow the reading of the BRD file to continue, 
        	// but it will not allow the link to be successfully traced
        	trace.err("Bad numeric \""+max+"\"for range maximum: "+e);
        	maximum = minimum;
        }
        if(maximumElement != null)
        	maximumElement.addContent(maximumStr);
	}

	/**
	 * Performs an exact match on {@link #selection} and {@link #action}, then calls
	 * {@link #matchInput(Vector)} to check the input is within the range.
	 * @param selection
	 * @param action
	 * @param input
	 * @return true if a match
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#match(java.util.Vector, java.util.Vector, java.util.Vector)
	 */
	public boolean match(Vector selection, Vector action, Vector input) {
		if (trace.getDebugCode("br")) trace.out("br", "RangeMatcher.match("+selection+", "+action+","+input+
				") min "+minimumStr+", max "+maximumStr);
        if (selection == null || action == null || minimum > maximum) {
            throw new InvalidParameterException ("Error: mimimum (" 
                    + minimum + ") must be less than maximum (" + maximum + ")");
        }

        if ( !(selection.elementAt(0).equals(this.selection)) || 
                !(action.elementAt(0).equals(this.action)))
            return false;
                
        if (matchInput(input))
        	return true;
		
		return false;
	}
	
	public String getSelectionLabelText() {
		return selection;
	}
    public String getActionLabelText() {
    	return action;
    }
    public String getInputLabelText() {
    	return minimumStr+", "+maximumStr;
    }
    public String getSingleLabelText() {
    	return "<"+minimumStr+", "+maximumStr+">";
    }

	/* (non-Javadoc)
	 * @see pact.BehaviorRecorder.Matcher.Matcher#match(java.util.Vector)
	 */
	public boolean matchForHint(Vector selection, Vector action, String actor) {
		
        if (selection == null || minimum > maximum) {
            throw new InvalidParameterException ("Error: mimimum (" 
                    + minimum + ") must be less than maximum (" + maximum + ")");
        }

        if ( !(selection.elementAt(0).equals(this.selection))) 
            return false;

		if (action != null &&
			!action.elementAt(0).equals(this.action))
			return false;
		
		return matchActor(actor);
	}
	
	/**
	 * We can only match one value through range, so take the first out
	 * if we're doing concatenation
	 */
	public boolean matchConcatenation(Vector v)
	{
		return matchSingle(v.get(0).toString());
	}
	
	/**
	 * So if its invalid (max < min), we'll always return false
	 */
	public boolean matchSingle(String s)
	{
		try {
			double answer = Double.parseDouble(s);
            if (answer >= minimum && 
					answer <= maximum)
				return true;
			
		} catch (NumberFormatException e) {
			if (trace.getDebugCode("br")) trace.out("br", "Error: number format exception in Range Matcher");
		}
		
		return false;
	}
	
	/**
	 * Override of superclass method for any-match of input.
	 * @param input student input: check whether within range
	 * @return true if within range
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#matchInput(java.util.Vector)
	 */
	protected boolean matchInput(Vector input) {
		
		try {
			double answer = Double.parseDouble((String) input.elementAt(0));
            if (answer >= minimum && 
					answer <= maximum)
				return true;
			
		} catch (NumberFormatException e) {
			if (trace.getDebugCode("br")) trace.out("br", "Error: number format exception in Range Matcher");
		}
		
		return false;
	}
	
	/**
	 * Use index == 0 for the range minimum
	 * Use index == 1 for the range maximum
	 * Use index == 2 for the Selection
	 * Use index == 3 for the Action
	 * @see pact.BehaviorRecorder.Matcher.Matcher#setParameter(java.lang.Object, int)
	 */
	protected void setParameterInternal(Element element, int index) {
		if(concat || versionIsBeforeMatcherParameterStd(element)) {
			if (index == 0)
				setMinimum (element.getText());
			else if (index == 1)
				setMaximum (element.getText());
			else if(concat)
				throw new UnknownError("Parameter index " + index + " not recognized for a single element.");
			else if (index == 2)
				setSelection (element.getText());
			else if (index == 3)
				setAction (element.getText());
			else if (index == 4)
				setActor (element.getText());
			else
				throw new UnknownError("Parameter index " + index + " not recognized.");
		} else {
			setParameterByIndex(element.getText(), index);
		}
	}
	/*
			if (index == 0)
				
				setMinimum (element.getText());
			else if (index == 1)
				setMaximum (element.getText());
			else if (index == 2)
				setSelection (element.getText());
			else if (index == 3)
				setAction (element.getText());
			else if (index == 4)
				setActor (element.getText());
			else
				throw new UnknownError("Parameter index " + index + " not recognized.");
		 * 
	 */
	/**
	 * Sets parameter by position. Meanings of index values:
	 * <table border="0" cellspacing="2">
	 *   <tr><th>index</th><th>current version</th></tr>
	 *   <tr><td>0</td>    <td>selection</td>      </tr>
	 *   <tr><td>1</td>    <td>action</td>         </tr>
	 *   <tr><td>2</td>    <td>minimum input</td>  </tr>
	 *   <tr><td>3</td>    <td>maximum input</td>  </tr>
	 *   <tr><td>4</td>    <td>actor</td>          </tr>
	 * </table>
	 * @param stringValue
	 * @param index
	 * @return
	 */
	public void setParameterByIndex(String stringValue, int index) {
		switch(index) {
		case 0:
			setSelection(stringValue); return;
		case 1:
			setAction (stringValue); return;
		case 2:
			setMinimum(stringValue); return;
		case 3:
			setMaximum(stringValue); return;
		case 4:
			setActor(stringValue); return;
		default:
			trace.err("ExactMatcher.setParameterByIndex(): unknown index "+index);
			return;
		}
	}

	/**
	 * Set a parameter by position. Most parameters are Selection, Action
	 * or Input values to match, but subclasses define their own semantics.
	 * @see #getParameter(int).
	 * @param parameter value to store
	 * @param index position
	 */
	public void setParameter(Element element, int index) {
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
			if (name.equalsIgnoreCase("minimum")) {
				setMinimum( element.getText());
				return;
			}
			if (name.equalsIgnoreCase("maximum")) {
				setMaximum( element.getText());
				return;
			}
			if (name.equalsIgnoreCase("actor")){
				setActor( element.getText());
				return;
			}
		}
		setParameterInternal(element, index);
	};
	
	/**
	 * Return a named parameter by position. 
	 * @see #setParameter(Object, int).
	 * @param index parameter position, 0-based
	 * @return parameter value, with name attribute, for the given index;
	 *         returns null if p exceeds the result of {@link #getParameterCount()}
	 */
	public MatcherParameter getMatcherParameter(int index) {
		if(single)
		{
			switch(index) {
				case 0:
					return new MatcherParameter("minimum", getParameter(index));
				case 1:
					return new MatcherParameter("maximum", getParameter(index));
				default:
					return null;
			}
		}
		
		switch(index) {
		case 0:
			return new MatcherParameter("selection", getParameter(index));
		case 1:
			return new MatcherParameter("action", getParameter(index));
		case 2:
			return new MatcherParameter("minimum", getParameter(index));
		case 3:
			return new MatcherParameter("maximum", getParameter(index));
		case 4:
			return new MatcherParameter("actor", getParameter(index));
		default:
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#getParameter(int)
	 */
	public Object getParameter(int index) {
		if(single)
		{
			if(index == 0)
				return getMinimumStr();
			else if(index == 1)
				return getMaximumStr();
			return null;
		}
		if (index == 0)
            return getSelection();
        else if (index == 1)
            return getAction();
        else if (index == 2)
			return getMinimumStr();
        else if (index == 3)
        	return getMaximumStr();
        else if (index == 4)
        	return getActor();
		else
			return null;
	}
	/* (non-Javadoc)
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#getParameterCount()
	 */
	public int getParameterCount() {
		return single ? 2 : 5;
	}

	/* (non-Javadoc)
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#getMatcherType()
	 */
	public String getMatcherType() {
		return RANGE_MATCHER;
	}
	
	public String getMatcherTypeText() {
		return "Range";
	}
	
	public String getMatcherClassType() {
		return "RangeMatcher";
	}
	
    /**
     * @param text
     */
    public String getAction() {
        return action;
    }


    /**
     * @param text
     */
    public String getSelection() {
        return selection;
    }

    public String getActor(){
    	return actor;
    }
	/**
	 * @param action
	 */
	public void setAction(String action) {
		this.action = action;
        actionElement.addContent(action);
	}

	/**
	 * @param text
	 */
	public void setSelection(String text) {
		this.selection = text;
        selectionElement.addContent(selection);
	}
	public void setActor(String actor){
		this.actor = actor;
		actorElement.addContent(actor);
		
	}
	
	/**
	 * This is a String representation of the range matcher
	 * It can be converted back by using the RangeMatcherPanel(String text)
	 * constructor
	 */
	public String toString()
	{
		return "[ " + getMinimumStr() + " , " + getMaximumStr() + " ]";
	}
	
	public String[] getValuesVector()
	{
		String[] arr = new String[1];
		arr[0] = toString();
		return arr;
	}
	public String getActionMatcherType() {
		return "Exact";}
	public String getInputMatcherType() {
		return getMatcherTypeText();}
	public String getSelectionMatcherType() {
		return "Exact";}
}
