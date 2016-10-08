/*
 * Created on Nov 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

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
public class RegexMatcher extends Matcher {

    Element element;
    protected Document doc;
    protected Element selectionElement;
    protected Element actionElement;
    protected Element inputElement;
    protected Element actorElement;
    protected Element singleElement;
    private String inputPattern;
    private String selectionPattern;
    private String actionPattern;
    private String singlePattern;
    private String actor;
    private Pattern inputPatternObj;
    private Pattern selectionPatternObj;
    private Pattern actionPatternObj;
    private Pattern singlePatternObj; //for single element matching
    
    protected Element type; //since Wildcard extends this ...
    
    public RegexMatcher(boolean concat, int vector, String value) {
    	super(concat, vector);
    	setSinglePattern(value);
    }
    
    public RegexMatcher()
    {
    	super();
    	initXML();
    }
    
    private void initXML() {
    	element = new Element ("matcher");
        type = new Element ("matcherType");
        type.addContent("RegexMatcher");
        element.addContent(type);
        
        selectionElement = new Element ("matcherParameter");
        actionElement = new Element ("matcherParameter");
        inputElement = new Element ("matcherParameter");
        actorElement = new Element ("matcherParameter");
        
        element.addContent (selectionElement);
        element.addContent (actionElement);
        element.addContent (inputElement);
        element.addContent (actorElement);
    	
    	doc = new Document(element);
	}

	/**
	 * @return deep clone of this instance
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#clone()
	 */
	public Object clone() {
		RegexMatcher m = new RegexMatcher(this.concat, this.vector, this.getSinglePattern());
		m.copyFrom(this);
		return m;
	}
	
	/**
	 * Service method for {@link #clone()} here and in subclasses. Copies private fields.
	 * @param m instance to copy from
	 */
	protected void copyFrom(RegexMatcher m) {	
		super.copyFrom(m);
		initXML();
		setSelectionPattern(m.getSelectionPattern());
		setActionPattern(m.getActionPattern());
		setInputPattern(m.getInputPattern());
		setActorPattern(m.getActorPattern());
	}

	/* (non-Javadoc)
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
        newString = newString.substring (newString.indexOf("\n"), newString.length());
        return newString;
    }

    public String toXML(String prefix) {
        String xml = toXML();
        
        xml = prefix + xml;
        
        xml = xml.replaceAll("\n", "\n" + prefix);
        return xml;
    }
    
    /**
	 * Test student action as done for {@link #match(Vector, Vector, Vector)}.
	 * @param action student action
	 * @return true if initial action element matches {@link #actionPatternObj}
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#matchAction(java.util.Vector)
	 */
	protected boolean matchAction(Vector action) {
    	java.util.regex.Matcher actionMatcher = actionPatternObj.matcher((String) action.get(0));
        return actionMatcher.matches();
	}

	/**
	 * Test student input as done for {@link #match(Vector, Vector, Vector)}.
	 * @param input student input
	 * @return true if initial input element matches {@link #inputPatternObj}
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#matchInput(Vector)
	 */
	protected boolean matchInput(Vector input) {
    	java.util.regex.Matcher inputMatcher = inputPatternObj.matcher((String) input.get(0));
        return inputMatcher.matches();
	}

	/**
	 * Test student selection as done for {@link #match(Vector, Vector, Vector)}.
	 * @param selection student selection
	 * @return true if initial selection element matches {@link #selectionPatternObj}
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#matchSelection(java.util.Vector)
	 */
	protected boolean matchSelection(Vector selection) {
    	java.util.regex.Matcher selectionMatcher = selectionPatternObj.matcher((String) selection.get(0));
		return selectionMatcher.matches();
	}

	/* (non-Javadoc)
     * @see pact.BehaviorRecorder.Matcher.Matcher#match(java.util.Vector, java.util.Vector, java.util.Vector)
     */
    public boolean match(Vector selection, Vector action, Vector input) {
    	java.util.regex.Matcher selectionMatcher = selectionPatternObj.matcher((String) selection.get(0));
    	java.util.regex.Matcher actionMatcher = actionPatternObj.matcher((String) action.get(0));
    	java.util.regex.Matcher inputMatcher = inputPatternObj.matcher((String) input.get(0));
        
        boolean value = (selectionMatcher.matches() && actionMatcher.matches() && inputMatcher.matches());
                
        return  value;
        
    }

    /**
     * Test just the selection and actor elements. Used for hints.
     * @param selection
     * @param actor
     * @return
     * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#matchforHint(java.util.Vector, java.lang.String)
     */
    public boolean matchForHint(Vector selection, Vector action, String actor) {
    	java.util.regex.Matcher selectionMatcher = selectionPatternObj.matcher((String) selection.get(0));
    	java.util.regex.Matcher actionMatcher =
    			(action == null ? null : actionPatternObj.matcher((String) action.get(0)));
        return (matchActor(actor) && 
        		selectionMatcher.matches() &&
        		(action == null || actionMatcher.matches()));
         
    }
    
    public boolean matchConcatenation(Vector s)
    {
    	return matchSingle(vector2ConcatString(s));
    }
    
    public boolean matchSingle(String s)
    {
    	java.util.regex.Matcher m = singlePatternObj.matcher(s);
    	boolean match = m.matches();
    	return match;
    }
    
	/**
	 * Sets parameter from element. Override of
	 * {@link Matcher#setParameter(Element, int)} to set regex patterns.
	 * @param element MatcherParameter element with regex pattern
	 * @param index positional index
	 */
    /* see the super class matcher does the exact saem thing ... calls setParameterInternal, which goes to byIndex 
    public void setParameter(Element element, int index) {
    	String name = element.getAttributeValue("name");
    	if (name != null && name.length() > 0) {
    		if (name.equalsIgnoreCase("selection")) {
    			setSelectionPattern( element.getText());
    			return;
    		}
    		if (name.equalsIgnoreCase("action")) {
    			setActionPattern( element.getText());
    			return;
    		}
    		if (name.equalsIgnoreCase("input")) {
    			setInputPattern( element.getText());
    			return;
    		}
    		if (name.equalsIgnoreCase("actor")){
    			setActorPattern(element.getText());
    			return;
    		}
    	}
    	setParameterByIndex(element.getText(), index);
    }*/
    
    /**
     * Equivalent to {@link #setParameterByIndex(String, int)} passing the element text.
     * @param element
     * @param index
     * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#setParameterInternal(org.jdom.Element, int)
     */
	protected void setParameterInternal(Element element, int index) {
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
	 */
    public void setParameterByIndex(String stringValue, int index) {
    	if(concat && index == 0)
    	{
    		setSinglePattern(stringValue);
    		return;
    	}
    	
    	if (index == 0)
    		setSelectionPattern (stringValue);
    	else if (index == 1)
    		setActionPattern (stringValue);
    	else if (index == 2)
    		setInputPattern(stringValue);
    	else if (index ==3)
    		setActorPattern(stringValue);
    	else
    		throw new UnknownError("Parameter index " + index + " not recognized.");
    }

    /**
     * Here, the parameter is the regex for the matcher
     * @see #getParameter(int).
     * @param parameter value to store
     * @param index position
     */
    public void setParameter(Element element, int index) {
        String name = element.getAttributeValue("name");
        if (name == null || name.length() == 0)
        	setParameterInternal(element, index);
        else if(name.equalsIgnoreCase("selection"))
        	setSelectionPattern(element.getText());
        else if(name.equalsIgnoreCase("action"))
            setActionPattern(element.getText());
        else if(name.equalsIgnoreCase("input"))
            setInputPattern(element.getText());
        else if(name.equalsIgnoreCase("actor"))
            setActorPattern(element.getText()); //why is actor a pattern?
        else
        	setParameterByIndex(element.getText(), index);
    }
    
	/* (non-Javadoc)
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#getParameter(int)
	 */
	public Object getParameter(int index) {
		if(single)
		{
			if(index == 0)
				return getSinglePattern();
			return null;
		}
		switch(index) {
			case 0:
				return getSelectionPattern();
			case 1:
				return getActionPattern();
			case 2:
				return getInputPattern();
			case 3:
				return getActorPattern(); //?? this isn't a pattern ...
			default:
				return null;
		}
		/*
        if (index == 0)
            return getSelectionPattern();
        else if (index == 1)
            return getActionPattern();
        else if (index == 2)
            return getInputPattern();
        else if (index == 3)
        	return getActorPattern();
        else
            return null;
            */
	}
	/* (non-Javadoc)
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#getParameterCount()
	 */
	public int getParameterCount() {
		return single ? 1 : 4;
	}
	
	public MatcherParameter getMatcherParameter(int index) {
		if(single)
			return new MatcherParameter("single", getParameter(index));
		return super.getMatcherParameter(index);
	}
	
	public void setSinglePattern(String singlePattern) {
		if(singlePattern == null)
			return;
		
		this.singlePattern = singlePattern;
		this.singlePatternObj = cacheCompile(singlePattern, Pattern.DOTALL);
	}
	
    /**
     * @param inputPattern
     */
    public void setInputPattern(String inputPattern) {
        this.inputPattern = inputPattern;
        this.inputPatternObj = cacheCompile(inputPattern);
        inputElement.addContent(inputPattern);
    }
    
    /**
     * To regex and wildcard, single is the regular expression
     */
    public void setSingle(String single)
    {
    	setSinglePattern(single);
    }
    
    /**
     * @param inputCombo
     */
    public void setActionPattern(String actionPattern) {
        this.actionPattern = actionPattern;
        this.actionPatternObj = cacheCompile(actionPattern);
        actionElement.addContent(actionPattern);
    }

    /**
     * @param inputCombo
     */
    public void setSelectionPattern(String selectionPattern) {
        this.selectionPattern = selectionPattern;
        this.selectionPatternObj = cacheCompile(selectionPattern);
        selectionElement.addContent(selectionPattern);
    }

    /**
     * Equivalent to {@link #cacheCompile(String, int) cacheCompile(pattern, 0)}.
     * @param regexp regular expression
     * @return compiled regexp, created anew or from cache
     */
    protected Pattern cacheCompile(String regexp) {
    	return cacheCompile(regexp, 0);
	}

    /**
     * Try to retrieve, from {@link #getSessionStorage()}, a compiled regular expression
     * matching the given one. If none found, create and store for later calls. 
     * @param regexp regular expression
     * @param flags see {@link Pattern#compile(String, int)}
     * @return compiled regexp, created anew or from cache
     */
	private Pattern cacheCompile(String regexp, int flags) 
			throws IllegalArgumentException {
		Map<String, Object> ss = getSessionStorage();
		try {
		if (ss == null)
			return Pattern.compile(regexp, flags);
		} catch (Exception e) {
			throw new IllegalArgumentException("Bad regular expression \""+regexp+"\"", e);
		}

		String psName = getClass().getName();
		Map<String, Pattern> ps = null;                       // shared pattern storage map
        synchronized (getClass()) {             // mutex against other instances of matcher
            ps = (Map<String, Pattern>) ss.get(psName);
            if (ps == null) {                        // on 1st call, create pattern storage
                ps = Collections.synchronizedMap(new HashMap<String, Pattern>());
            	ss.put(psName, ps);
            }
        }
        String key = Integer.toString(flags)+' '+regexp;
        Pattern p = ps.get(key);   // now, in pattern storage, see if I have this regexp
        if (p == null) {                                // if not, compile & store it there
			try {
				p = Pattern.compile(regexp, flags);
			} catch (Exception e) {
				throw new IllegalArgumentException("Bad regular expression \""+regexp+"\"", e);
			}
        	ps.put(key, p);
        	if (trace.getDebugCode("matchers")) trace.outNT("matchers", "RegexMatcher.cacheCompile(\""+regexp+"\"): new");
        } else
        	if (trace.getDebugCode("matchers")) trace.outNT("matchers", "RegexMatcher.cacheCompile(\""+regexp+"\"): existing");
        return p;
	}

	public void setActorPattern(String actor){
    	this.actor= actor;
    	actorElement.removeContent();
    	actorElement.addContent(actor);
    }
    
    
    public String getActionPattern () {
        return actionPattern;
    }

    public String getSelectionPattern () {
        return selectionPattern;
    }

    public String getInputPattern () {
        return inputPattern;
    }
    public String getSelectionLabelText() {
    	return getSelectionPattern();}
    public String getActionLabelText() {
    	return getActionPattern();}
    public String getInputLabelText() {
    	return getInputPattern();}
    public String getSingleLabelText() {
    	return getSinglePattern();
    }
    
    public String getActorPattern(){
    	return actor;
    }
    
    public String getSinglePattern(){
    	return singlePattern;
    }

	/* (non-Javadoc)
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#getMatcherType()
	 */
	public String getMatcherType() {
		return REGULAR_EXPRESSION_MATCHER;
	}
	
	public String getMatcherTypeText() {
		return "Regex";
	}
	
	public String getMatcherClassType() {
		return "RegexMatcher";
	}

	public String toString()
	{
		if(single)
			return singlePattern;
		return super.toString();
	}
	
	/**
	 * Same as exact's ...
	 */
	public String[] getValuesVector() {
		//include empty strings, but not the last one ...
    	return singlePattern.split("\n", -1);
	}
	public String getActionMatcherType() {
		return getMatcherTypeText();}
	public String getInputMatcherType() {
		return getMatcherTypeText();}
	public String getSelectionMatcherType() {
		return getMatcherTypeText();}
}
