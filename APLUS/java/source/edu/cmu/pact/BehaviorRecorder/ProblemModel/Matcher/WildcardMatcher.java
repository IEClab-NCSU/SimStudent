/*
 * Created on Nov 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher;



/**
 * 
 * This class matches the student's input against a number range in a pseudotutor.
 * 
 * It can be stored and read out of a brd file.
 * 
 * @author mpschnei
 * 
 * modified by Ko
 *	- the constructors are not similar ... 
 *
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WildcardMatcher extends RegexMatcher {

    private String simpleActionPattern;
    private String simpleSelectionPattern;
    private String simpleInputPattern;
    private String simpleSinglePattern;

    public WildcardMatcher(boolean concat, int vector, String value) {
    	super(concat, vector, value);
    	this.simpleSinglePattern = value;
    }
    
    public WildcardMatcher()
    {
    	super();
    }

    /**
     * @return deep copy of this instance
     * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.RegexMatcher#clone()
     */
    public Object clone() {
    	WildcardMatcher m = new WildcardMatcher(this.concat, this.vector, this.simpleSinglePattern);
    	m.copyFrom(this);
    	m.simpleSelectionPattern = this.simpleSelectionPattern;
    	m.simpleActionPattern = this.simpleActionPattern;
    	m.simpleInputPattern = this.simpleInputPattern;
    	return m;
    }
    
    /**
     * @param selectionPattern new value for {@link #simpleSelectionPattern}
     * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.RegexMatcher#setSelectionPattern(java.lang.String)
     */
    public void setSelectionPattern(String selectionPattern) {
        simpleSelectionPattern = selectionPattern;
        super.setSelectionPattern(convertToFullRegex(selectionPattern));
        selectionElement.removeContent();
        selectionElement.addContent(simpleSelectionPattern);

    }
    
    /* (non-Javadoc)
     * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.RegexMatcher#setActionPattern(java.lang.String)
     */
    public void setActionPattern(String actionPattern) {
        simpleActionPattern = actionPattern;
        super.setActionPattern(convertToFullRegex(actionPattern));
        actionElement.removeContent();
        actionElement.addContent(simpleActionPattern);
        
    }
    
    /* (non-Javadoc)
     * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.RegexMatcher#setInputPattern(java.lang.String)
     */
    public void setInputPattern(String inputPattern) {
        simpleInputPattern = inputPattern;
        super.setInputPattern(convertToFullRegex(inputPattern));
        inputElement.removeContent();
        inputElement.addContent(simpleInputPattern);
    }
    
    /**
     * 
     */
    public void setSinglePattern(String inputPattern) {
        simpleSinglePattern = inputPattern;
        super.setSinglePattern(convertToFullRegex(inputPattern));
    }
 
	/**
	 * Override returns {@link #getSimpleSelectionPattern()},
	 * {@link #getSimpleActionPattern()}, {@link #getSimpleInputPattern()}
	 * instead of the full regex patterns. Cf. the superclass implementation.
	 * @param index parameter position, 0-based
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#getParameter(int)
	 */
	public Object getParameter(int index) {
		if(single)
		{
			if(index == 0)
				return getSinglePattern();
			return null;
		}
		
        if (index == 0)
            return getSimpleSelectionPattern();
        else if (index == 1)
            return getSimpleActionPattern();
        else if (index == 2)
            return getSimpleInputPattern();
        else if (index == 3)
        	return getActorPattern();
        else
            return null;
	}

    /**
     * Replaces * with .*
     * Static so we can use it in the constructor
     * @param inputPattern
     * @return
     */
    public static String convertToFullRegex(String inputPattern) {
    	if(inputPattern == null)
    		return null;
        inputPattern = inputPattern.replaceAll("\\.", "\\."); //what is this supposed to do?
        inputPattern = inputPattern.replaceAll("\\*", "\\.\\*"); //input to replaceAll is a regex, so we escaped the * ...
        inputPattern = inputPattern.replaceAll("\\.\\.\\*", "\\.\\*"); //put it back to usual ... if the author really wants ..*, use a normal regex, not the wildcard
        return inputPattern;
    }

	/* (non-Javadoc)
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#getMatcherType()
	 */
	public String getMatcherType() {
		return WILDCARD_MATCHER;
	}
	
	public String getMatcherTypeText() {
		return "Wildcard";
	}
	
	public String getMatcherClassType() {
		return "WildcardMatcher";
	}

    /**
     * @return
     */
    public String getSimpleActionPattern() {
        return simpleActionPattern;
    }


    /**
     * @return
     */
    public String getSimpleSelectionPattern() {
        return simpleSelectionPattern;
    }

    /**
     * @return
     */
    public String getSimpleInputPattern() {
        return simpleInputPattern;
    }
    
    public String toString()
    {
    	if(single)
			return simpleSinglePattern;
    	return super.toString();
    }
    
    public String[] getValuesVector() {
		//include empty strings, but not the last one ...
    	return simpleSinglePattern.split("\n", -1);
	}
    public String getSelectionLabelText() {
    	return getSimpleSelectionPattern();
    }
    public String getActionLabelText() {
    	return getSimpleActionPattern();
    }
    public String getInputLabelText() {
    	return getSimpleInputPattern();
    }
    public String getSingleLabelText() {
    	return simpleSinglePattern;
    }
}
