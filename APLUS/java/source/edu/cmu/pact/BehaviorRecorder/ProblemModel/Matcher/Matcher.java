/*
 * Created on Nov 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jdom.Element;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemStateReader;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerSAI;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.MTRete;
import fri.patterns.interpreter.parsergenerator.Parser;


/**
 * @author mpschnei
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class Matcher implements Cloneable, Serializable {
	
	/* For single matchers, defines what vector type it is associated with, 
	 * will be -1 for non-singles, and VECTOR for VectorMatchers
	 * Note that we use the SAI constants for traversing well constructed arrays
	 * Don't use the other two for it ...
	 * */
	public static final int NON_SINGLE = -1, SELECTION = 0, ACTION = 1, INPUT = 2, VECTOR = 3;
	public static final int SHORT_TEXT_TRUNCATION_CHARS = 18;
	public static final int LONG_TEXT_TRUNCATION_CHARS = 23;
	protected Object lastResult;
	protected final int vector;
	
	public Matcher(boolean concat, int vector)
	{
		this.single = true;
		this.concat = concat;
		this.vector = vector;
	}
	
	public Matcher()
	{
		this.single = false;
		this.concat = false;
		this.vector = NON_SINGLE;
	}
	
	/**
	 * Copy this instance.
	 * @return copy
	 */
	public abstract Object clone();
	
	/**
	 * For subclass implementations of {@link #clone()}, to get the private fields of the superclass.
	 * @param m instance to copy from
	 */
	protected void copyFrom(Matcher m) {
		this.selectionIndex = m.selectionIndex;
		this.actionIndex = m.actionIndex;
		this.inputIndex = m.inputIndex;

		this.caseInsensitive = m.caseInsensitive;

		this.defaultSelection = m.defaultSelection;
		this.defaultAction = m.defaultAction;
		this.defaultInput = m.defaultInput;
		this.defaultActor = m.defaultActor;
		
		this.lastResult = null;
		this.linkTriggered = m.linkTriggered;
		this.paramNotSpecifiedList = (m.paramNotSpecifiedList == null ?
				null : new ArrayList(m.paramNotSpecifiedList));
		this.replacementFormula = m.replacementFormula;
		this.sessionStorage = m.sessionStorage;
		
		this.singleValue = m.singleValue;
		this.useAlgebraicEquivalence = m.useAlgebraicEquivalence;
	}
	
    /**
     * Hold a matcher parameter's name and value.
     */
    public static class MatcherParameter {
        private final String name;
        private final Object value;
        public MatcherParameter(String name, Object value) {
            this.name = (name == null ? "" : name);
            this.value = (value == null ? "" : value);
        }
        public String getName() { return name; }
        public Object getValue() { return value; }
    }
	
    /**
     * The version of the graph file format which first implemented a
     * standardization (with CTAT version 2.1) in the order of parameters for
     * {@link edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher}
     * subclasses.
     */
    public static final String MATCHER_PARAMETER_BRD_VERSION = "2.1";
	
    /** Numeric equivalent of {@link #CURRENT_BRD_VERSION}. */
    private static final double matcherParameterStdBrdVersion =
	Double.parseDouble(MATCHER_PARAMETER_BRD_VERSION);

    /**
     * Convenience for {@link #versionIsBeforeMatcherParameterStd(String)} to
     * get version attribute out of an XML element.
     * @param matcherParamElt element possibly holding attribute
     * @return result of {@link #versionIsBeforeMatcherParameterStd(String)} on attribute value
     */
    protected static boolean versionIsBeforeMatcherParameterStd(Element matcherParamElt) {
    	return versionIsBeforeMatcherParameterStd(matcherParamElt.getAttributeValue(ProblemStateReader.VERSION_ATTR));
    }

    public final boolean isConcat()
    {
    	return concat;
    }
    
    /**
     * Tell whether the given version number attribute is older than the change,
     * implemented in CTAT 2.1, to the order of Matcher parameters.
     * @param version attribute from root element of brd, as a string; returns
     *        true if null or non-numeric
     * @return true if this version is previous to the change 
     */
    public static boolean versionIsBeforeMatcherParameterStd(String version) {
    	if (version == null || version.length() < 1)
    		return true;            // older brds might not define this at all
    	try {
    		double v = Double.parseDouble(version);
    		return v < matcherParameterStdBrdVersion;
    	} catch (NumberFormatException nfe) {
    		if (trace.getDebugCode("br")) trace.out("br", "non-numeric BRD version \""+version+"\": "+nfe);
    		return true;            // older brds might not define this properly
    	}
    }
    
    /** Actor property in message. */
    static public final String ACTOR = "Actor";
    /** Default student value "Student" for actor in match tuple. */
    static public final String DEFAULT_STUDENT_ACTOR = "Student";
    /** Default tool-actor value "Tool" for actor in match tuple. */
    static public final String DEFAULT_TOOL_ACTOR = "Tutor";
    /** Tool-actor value for tutor-performed actions that shouldn't be graded Correct. */
    static public final String UNGRADED_TOOL_ACTOR = "Tutor (unevaluated)";
    /** Any-actor value "Any" for actor in match tuple. */
    static public final String ANY_ACTOR = "Any";
    /** Default value {@link #DEFAULT_STUDENT_ACTOR} for actor in match tuple. */
    static public final String DEFAULT_ACTOR = DEFAULT_STUDENT_ACTOR; 
    
    /** Tooltip for Student Actor button. */
    public static final String DEFAULT_STUDENT_ACTOR_TOOLTIP = "Only the student can perform this step.";
    /** Tooltip for Tutor Actor button. */
    public static final String DEFAULT_TOOL_ACTOR_TOOLTIP = "Tutor will perform and evaluate this step.";
    /** Tooltip for Tutor Actor button. */
    public static final String UNGRADED_TOOL_ACTOR_TOOLTIP = "Tutor will perform this step but leave it unevaluated.";
    /** Tooltip for Student Actor button. */
    public static final String ANY_ACTOR_TOOLTIP = "The student or the tutor may perform this step.";

    static public final String WILDCARD_MATCHER = "Wildcard Match";
    static public final String REGULAR_EXPRESSION_MATCHER = "Regular Expression Match";
    static public final String RANGE_MATCHER = "Range Match";
    static public final String ANY_MATCHER = "Any Match";
    static public final String EXACT_MATCHER = "Exact Match";
    static public final String EXPRESSION_MATCHER = "Formula Match";
    static public final String SOLVER_MATCHER = "Equation Solver";
    static public final String MULTIPLE_VECTORS_MATCHER = "Multiple Vectors Match";
    
    public static final String[] MATCHER_NAMES = {
        WILDCARD_MATCHER, REGULAR_EXPRESSION_MATCHER, RANGE_MATCHER, ANY_MATCHER, EXACT_MATCHER, EXPRESSION_MATCHER
    };

    public abstract String toXML();
    public abstract String toXML(String prefix);
	
    private String defaultSelection = "";
    private String defaultAction = "";
    private String defaultInput = "";
    
    //as an invariant, the first of the defaultSelectionVector should be the defaultSelection string
    private Vector<String> defaultSelectionVector = new Vector<String>();
    private Vector<String> defaultActionVector = new Vector<String>();
    private Vector<String> defaultInputVector  = new Vector<String>();
    
    //true for the newer matching methods
    final boolean single;
    
    //true if we are using concatenation matching (doesn't make sense if we're not using single matching)
    final boolean concat;
    
    //
    
    private String defaultActor = DEFAULT_ACTOR;
    private boolean caseInsensitive;
    private boolean useAlgebraicEquivalence;
    
    protected static final int SHORT_DISPLAY_LENGTH = 8;
    
    /** Default index for selection parameter.*/
    protected static final int SELECTION_INDEX = 0; 
    
    /** Default index for action parameter.*/
    protected static final int ACTION_INDEX = 1; 
    
    /** Default index for input parameter.*/
    protected static final int INPUT_INDEX = 2;
    
    /** Default index for actor parameter.*/
    protected static final int ACTOR_INDEX = 3;
    
    private int selectionIndex = SELECTION_INDEX;
    
    private int actionIndex = ACTION_INDEX;
    
    private int inputIndex = INPUT_INDEX;

    /** Value for {@link #paramNotSpecifiedList} that indicates a parameter is unspecified. */
    public static final String NOT_SPECIFIED = MTRete.NOT_SPECIFIED;

    /** Value for {@link #paramNotSpecifiedList} that indicates any value matches. */
    public static final String DONT_CARE = MTRete.DONT_CARE;
    
    public abstract boolean match(Vector selection, Vector action, Vector input);

    public boolean match(Vector selection, Vector action, Vector input, String actor) {
        return match(selection, action, input);
    }
    public boolean match(Vector selection, Vector action, Vector input, String actor, VariableTable vt) {
        return match(selection, action, input);
    }
    public boolean match(Vector selection, Vector action, String actor, VariableTable vt){
    	return matchForHint(selection, action, actor);
    }
    
    /**
     * Match on selection, action and actor only. Used when finding hint links.
     * @param selection
     * @param actor
     * @return true if link matches on selection and actor
     */
    public abstract boolean matchForHint(Vector selection, Vector action, String actor);
    
    /**
     * Test whether the given actor matches our {@link #getActor()} element.
     * This default implementation mimics the test of {@link ExactMatcher}.
     * @param actor
     * @return true if match ok
     */
    protected boolean matchActor(String actor) {
    	String myActor = getActor();
    	if (ANY_ACTOR.equalsIgnoreCase(myActor))
    		return true;
    	if (ANY_ACTOR.equalsIgnoreCase(actor))  // ANY may never occur as argument
    		return true;
    	if (myActor == null)
    		myActor = getDefaultActor();
    	if (myActor == null)                    // shouldn't happen
    		return actor == null;
    	if (UNGRADED_TOOL_ACTOR.equalsIgnoreCase(myActor) && this.DEFAULT_TOOL_ACTOR.equalsIgnoreCase(actor))
    		return true;
    	if (UNGRADED_TOOL_ACTOR.equalsIgnoreCase(actor) && this.DEFAULT_TOOL_ACTOR.equalsIgnoreCase(myActor))
    		return true;
		return myActor.equalsIgnoreCase(actor);
    }

    /**
     * Test a parameter by position. Most parameters are Selection, Action
     * or Input values to match, but subclasses define their own semantics.
     * @see #setParameterByIndex(String, int).
     * @param i index position
     * @param v parameter value to test
     */
    public boolean match(int i, Vector v) {
        if (i == getSelectionIndex())
            return matchSelection(v);
        if (i == getActionIndex())
            return matchAction(v);
        if (i == getInputIndex())
            return matchInput(v);
        if (i == getActorIndex())
            return matchActor(v == null || v.size() < 1 ? null : (String) v.get(0));
        trace.err("Matcher.match("+v+") parameter index undefined: "+i);
        return false;
    }

    /**
     * This default implementation mimics the test of {@link ExactMatcher}.
     * Override this to match an entire vector, the default implementation
     * is to match the first element to the single expected String
     * @param student value
     * @return true if value matches expected value
     */
    protected boolean matchSelection(Vector v) {
        String actualSelection = (v == null || v.size() < 1 ? "" : v.elementAt(0).toString());
        String expectedSelection = getSelection().toString();
        boolean comparisonValue = false;
        if (getCaseInsensitive())
            comparisonValue = expectedSelection.equalsIgnoreCase(actualSelection);
        else
            comparisonValue = expectedSelection.equals(actualSelection);
        return comparisonValue;
    }
    
    protected String singleValue;
    
    /**
     * For single element matching, this is a default (only used by exact matcher in implementation)
     */
    protected String getSingle()
    {
    	return singleValue;
    }
    
    /**
     * Stores the current input
     */
    protected void setSingle(String text)
    {
    	singleValue = text; 
    }
    
    /**
     * Imitates the exact match for the entirety of a single vector
     * Overridden by Expression and Range matchers for which it only makes sense
     * to take in a single element of a vector
     */
    public boolean matchConcatenation(Vector v)
    {
    	return matchSingle(vector2ConcatString(v));
    }
    
    
    boolean matchSingle(String s)
    {
    	return s.equals(singleValue);
    }
    
    /**
     * This default implementation mimics the test of {@link ExactMatcher}.
     * @param student value for Action
     * @return true if value matches expected value
     */
    protected boolean matchAction(Vector v) {
        String actualAction = (v == null || v.size() < 1 ? "" : v.elementAt(0).toString());
        String expectedAction = getAction().toString();
        boolean comparisonValue = false;
        if (getCaseInsensitive())
            comparisonValue = expectedAction.equalsIgnoreCase(actualAction);
        else
            comparisonValue = expectedAction.equals(actualAction);
        return comparisonValue;
    }

    /**
     * This default implementation mimics the test of {@link ExactMatcher}.
     * @param student value for Input
     * @return true if value matches expected value
     */
    protected boolean matchInput(Vector v) {
	String actualInput = (v == null || v.size() < 1 ? "" : v.elementAt(0).toString());
	String expectedInput = getInput().toString();
	boolean comparisonValue = false;
	if (getCaseInsensitive())
	    comparisonValue = expectedInput.equalsIgnoreCase(actualInput);
        else
	    comparisonValue = expectedInput.equals(actualInput);
        return comparisonValue;
    }

    /**
     * Set a parameter by position. Most parameters are Selection, Action
     * or Input values to match, but subclasses define their own semantics.
     * @see #getParameter(int).
     * @param parameter value to store
     * @param index position
     */
    public void setParameter(Element element, int index) {
    	//we keep this for singles in case the ExpressionMatcher needs the default sai...
    	
        String name = element.getAttributeValue("name");
        if (name == null || name.length() == 0)
        	setParameterInternal(element, index);
        else if(name.equalsIgnoreCase("selection"))
        	setDefaultSelection(element.getText());
        else if(name.equalsIgnoreCase("action"))
            setDefaultAction(element.getText());
        else if(name.equalsIgnoreCase("input"))
            setDefaultInput(element.getText());
        else if(name.equalsIgnoreCase("actor"))
            setDefaultActor(element.getText());
        else if(name.equalsIgnoreCase("single"))
        	setSingle(element.getText());
        else
        	setParameterInternal(element, index);
        return;
    }
	
    /**
     * Internal method for setting parameters. This allows for version checking
     * through the element's {@link ProblemStateReader#VERSION_ATTR} attribute.
     * @param element
     * @param index
     */
    protected abstract void setParameterInternal(Element element, int index);
	
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
    public abstract void setParameterByIndex(String stringValue, int index);
	
    /**
     * Return a named parameter by position. Most parameters are Selection, Action
     * or Input values to match, but subclasses define their own semantics.
     * 
     * With single matchers, all subclasses define their own semantics, however
     * for backwards compatibility, we call super's in non-single cases (older)
     * 
     * @see #setParameter(Object, int).
     * @param index parameter position, 0-based
     * @return parameter value, with name attribute, for the given index;
     *         returns null if p exceeds the result of {@link #getParameterCount()}
     */
    public MatcherParameter getMatcherParameter(int index) {
        switch(index) {
            case 0:
                return new MatcherParameter("selection", getParameter(index));
            case 1:
                return new MatcherParameter("action", getParameter(index));
            case 2:
                return new MatcherParameter("input", getParameter(index));
            case 3:
                return new MatcherParameter("actor", getParameter(index));
            default:
                return null;
        }
    }
	
    /**
     * Return a parameter by position. Most parameters are Selection, Action
     * or Input values to match, but subclasses define their own semantics.
     * @see #setParameter(Object, int).
     * @param index parameter position, 0-based
     * @return parameter for the given index; returns null if p exceeds the
     *         result of {@link #getParameterCount()}
     */
    public abstract Object getParameter(int index);

    /**
     * Return the total number of parameters to save and read. 
     * @return number of parameters available from {@link #getParameter(int)}
     */
    public abstract int getParameterCount();

    /**
     * Get the Selection parameter as a scalar String. Subclasses may want
     * to override this implementation.
     * @return result of {@link #getDefaultSelection()}
     */
    public String getSelection() {
        return getDefaultSelection();
    }
    
    /**
     * 
     */
    public Vector<String> getSelectionVector()
    {
    	return defaultSelectionVector;
    }

    /**
     * Get the Action parameter as a scalar String. Subclasses may want
     * to override this implementation.
     * @return result of {@link #getDefaultAction()}
     */
    public String getAction() {
        return getDefaultAction();
    }

    /**
     * Get the Input parameter as a scalar String. Subclasses may want
     * to override this implementation.
     * @return result of {@link #getDefaultInput()}
     */
    public String getInput() {
        return getDefaultInput();
    }

	/**
	 * Get the value of a formula calculation specified for the input element.
	 * Override this implementation in {@link ExpressionMatcher} to return the
	 * result of the formula.
	 * @return result of {@link #getInput()}
	 */
	public String getEvaluatedInput() {
		return getInput();
	}

	/**
	 * @return {@link #getDefaultActor()}
	 */
    public String getActor(){
    	return getDefaultActor();
    }
    
    /**
     * @param view 
     * @param mouseEntered 
     * @return
     */
    //Sandy forgive me!
    public String getActionLabelText(int view) {
    		return truncateText(getSelectionLabelText(), SHORT_TEXT_TRUNCATION_CHARS) +", "
    			/*+ truncateText(getActionLabelText(), SHORT_TEXT_TRUNCATION_CHARS) + ", "*/
    			+ truncateText(getInputLabelText(), SHORT_TEXT_TRUNCATION_CHARS);
    }
    
    public abstract String getMatcherTypeText();
    public abstract String getSelectionLabelText();
    public abstract String getActionLabelText();
    public abstract String getInputLabelText();
    public abstract String getSingleLabelText();
    public String getToolTipText() { return getSingleLabelText(); }
    public String getSelectionToolTipText() { return getSelectionLabelText(); } 
    public String getActionToolTipText() { return getActionLabelText(); }
    public String getInputToolTipText() { return getInputLabelText(); }
    
    /**
     * Make sure maxChars is at least 4
     * @param s
     * @param maxChars
     * @return
     */
    public String truncateText(String s, int maxChars) {
    	if(s.length()>maxChars)
    		return s.substring(0, maxChars-3) + "...";
    	else
    		return s;
    }
    /**
     * @return
     */
    public abstract String getMatcherType();
    /**
     * Added by Kim K.C. on 09/11/05
     * Returns the class name of Matcher
     * @return
     */
    public abstract String getMatcherClassType();
    /**
     * @param defaultSelection The defaultSelection to set.
     */
    public void setDefaultSelection(String defaultSelection) {
    	this.defaultSelection = defaultSelection;
        this.defaultSelectionVector.clear();
        this.defaultSelectionVector.add(defaultSelection);
    }
    
    /**
     * Override if we actually deal with an entire vector
     * @param selection The entire vector of selections
     */
    public void setDefaultSelectionVector(Vector selection)
    {
    	setDefaultSelection((String)selection.get(0));
    	this.defaultSelectionVector = selection;
    }
    
    /**
     * @return Returns the defaultSelection.
     */
    public String getDefaultSelection() {
        return defaultSelection;
    }
    
    /**
     * Set the action vector
     * @param action Vector of actions
     */
    public void setDefaultActionVector(Vector action) {
    	setDefaultAction((String)action.get(0));
    	this.defaultActionVector = action;
    }
    
    /**
     * @param defaultAction The defaultAction to set.
     */
    public void setDefaultAction(String defaultAction) {
        this.defaultAction = defaultAction;
        this.defaultActionVector.clear();
        this.defaultActionVector.add(defaultAction);
    }
    /**
     * @return Returns the defaultAction.
     */
    public String getDefaultAction() {
        return defaultAction;
    }
    
    /**
     * Set the input vector
     * @param input Vector of inputs
     */
    public void setDefaultInputVector(Vector input) {
    	setDefaultInput((String)input.get(0));
    	this.defaultInputVector = input;
    }
    
    /**
     * @param defaultInput The defaulInput to set.
     */
    public void setDefaultInput(String defaultInput) {
        this.defaultInput = defaultInput;
        this.defaultInputVector.clear();
        this.defaultInputVector.add(defaultInput);
    }
    
    /**
     * @return Returns the defaulInput.
     */
    public String getDefaultInput() {
        return defaultInput;
    }
    
    public void setDefaultActor(String defaultActor){
    	if(trace.getDebugCode("actor"))
    		trace.out("actor", "Matcher.setDefaultActor("+defaultActor+")");
    	if(defaultActor.equals("Tool"))
    		defaultActor="Tutor";
    	this.defaultActor= defaultActor;	
    }
    public String getDefaultActor(){
    	return defaultActor;
    }
    
    public Vector getDefaultActionVector() {
    	if(defaultActionVector.size() == 0)
        {
    		Vector v = new Vector();
            v.add(defaultAction);
            return v;
        }
    	return defaultActionVector;    	
    }
    
    public Vector getDefaultInputVector() {
        
    	/* play it safe for now and do a check */
    	if(defaultInputVector.size() == 0)
        {
    		Vector v = new Vector();
            v.add(defaultInput);
            return v;
        }
    	return defaultInputVector;
    }
	
    public Vector getDefaultSelectionVector() {
    	if(defaultSelectionVector.size() == 0)
        {
    		Vector v = new Vector();
            v.add(defaultSelection);
            return v;
        }
    	return defaultSelectionVector;
    }
    
    public Vector getDefaultActorVector(){
		Vector v = new Vector();
		v.add(defaultActor);
		return v;
    }
    
    public void setCaseInsensitive(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }
    
    public boolean getCaseInsensitive () {
        return caseInsensitive;
    }
	

    /**
     * @return
     */
    public boolean getUseAlgebraicEquivalence() {
        return useAlgebraicEquivalence;
    }

    /**
     * @return
     */
    public void setUseAlgebraicEquivalence(boolean useAlgebraicEquivalence) {
        this.useAlgebraicEquivalence = useAlgebraicEquivalence;
    }
    
    /**
     * List of String that tells whether the predicted value of a parameter
     * is unspecified or matches any value ("don't care"). Unspecified
     * parameters may be excluded from match tests. DONT_CARE parameters
     * always return "match".
     * The index to this list is the same as that for
     * {@link #setParameterByIndex(String, int)}.
     */
    private List paramNotSpecifiedList = null;
    
    /** Access to persistent storage. Used to cache compiled regular expressions, e.g. */
	private transient Map<String, Object> sessionStorage = null;

    /** See {@link #isLinkTriggered()}. */
	private boolean linkTriggered = false;
	
	/** Available for replacing entered student input to send back to student interface. */
	private String replacementFormula;

    public void setParamNotSpecified(int i, String spec) {
        if (paramNotSpecifiedList == null)
            paramNotSpecifiedList = new ArrayList();
        if (paramNotSpecifiedList.size() > i)
            paramNotSpecifiedList.set(i, spec);
        else {
            for (int j = paramNotSpecifiedList.size(); j < i; ++j)
                paramNotSpecifiedList.add("");
            paramNotSpecifiedList.add(spec);
        }
    }

    public boolean isParamNotSpecified(int i) {
        if (paramNotSpecifiedList == null)
            return false;
        if (paramNotSpecifiedList.size() <= i)
            return false;
        return NOT_SPECIFIED.equals((String) paramNotSpecifiedList.get(i));
    }

    public boolean isParamDontCare(int i) {
        if (paramNotSpecifiedList == null)
            return false;
        if (paramNotSpecifiedList.size() <= i)
            return false;
        return DONT_CARE.equals((String) paramNotSpecifiedList.get(i));
    }

    /**
     * @return {@link #SELECTION_INDEX}
     */
    public int getSelectionIndex() {
        return selectionIndex;
    }

    /**
     * @return {@link #ACTION_INDEX}
     */
    public int getActionIndex() {
        return actionIndex;
    }

    /**
     * @return {@link #INPUT_INDEX}
     */
    public int getInputIndex() {
        return inputIndex;
    }

    /**
     * @return {@link #ACTOR_INDEX}
     */
    public int getActorIndex() {
        return ACTOR_INDEX;
    }

    /**
     * Supply external resources: these are provided because defined outside the Matchers. 
     * @param variableTable
     * @param problemModel
     * @param parser
     */
    public void setExternalResources(VariableTable variableTable, ProblemModel problemModel, Parser parser) {
        // nop
    }

    public Object evaluate() {
        return evaluate(null, null, null);
    }

    public Object evaluate(String selection, String action, String input) {
        return getDefaultInput();
    }
    
    /**
     * This is only used in the Expression Matcher
     */
    public boolean checkExpression() {
        return true;
    }

    public String error() {
        return null;
    }
    
    static final Class matcherPrecedenceOrder[] = {
        ExactMatcher.class,
        RangeMatcher.class,
        ExpressionMatcher.class,
        WildcardMatcher.class,
        RegexMatcher.class,
        AnyMatcher.class
    };
    
    /** Attribute name in brd file for link trigger. */
	public static final String TRIGGER_ATTR = "linkTriggered";

	/**
	 * Compare {@link Matcher} instances for specificity. When comparing 2 VectorMatchers,
	 * compare the individual element matchers in the order input, selection, action. 
	 * @param m1 one Matcher to compare
	 * @param m2 other Matcher to compare
	 * @return 1, -1, 0 as m1 is more specific, less specific or neither, relative to m2
	 */
    static public int compare(Matcher m1, Matcher m2) {
    	int top = compare(m1.getClass(), m2.getClass());
    	if (top != 0)                 // compare main matchers
    		return top;
    	if (m1.getClass() != VectorMatcher.class || m2.getClass() != VectorMatcher.class)
    		return top;
    	VectorMatcher v1 = (VectorMatcher) m1;
    	VectorMatcher v2 = (VectorMatcher) m2;
    	int input = compare(v1.getSingleMatcher("input").getClass(),
    			v2.getSingleMatcher("input").getClass());
    	if (input != 0)
    		return input;
    	int selection = compare(v1.getSingleMatcher("selection").getClass(),
    			v2.getSingleMatcher("selection").getClass());
    	if (selection != 0)
    		return selection;
    	return compare(v1.getSingleMatcher("action").getClass(),
    			v2.getSingleMatcher("action").getClass());
	}

    private static int compare(Class class1, Class class2) {
		int i, j;
		for (i = 0; i < 5; i++) {
			try { // if  (m1.getClass() ==  matcherPrecedenceOrder[i])  break;
				if (class1.asSubclass(matcherPrecedenceOrder[i]) != null)
					break;
			} catch (ClassCastException e) {
			}
		}

		for (j = 0; j < 5; j++) {
			try { //    if (m2.getClass() == matcherPrecedenceOrder[j])  	break; 
				if (class2.asSubclass(matcherPrecedenceOrder[j]) != null)
					break;
			} catch (ClassCastException e) {
			}
		}
		//trace.out("Compare i = " + i + " j = " + j);
		if (i < j)
			return 1;
		else if (i > j)
			return -1;
		else
			return 0;
	}

	protected String shortString(String s) {
        return s.substring(0, Math.min(SHORT_DISPLAY_LENGTH, s.length()));
    }
    
    /**
     * The concated string we use for exact, regex, and wildcard matchers
     * Also used to display the sai vectors in EditStudentInputDialog 
     * @param v - the vector
     * @return - element each on their own line
     */
    public static String vector2ConcatString(Vector v)
    {
    	String concat = "";
    	for(Object o : v)
    		concat += o.toString() + "\n";
    	return concat.substring(0, (concat.length() > 0 ? concat.length() - 1 : 0)); //don't need the last \n
    }
    
    /**
     * For displaying this matcher's values in the EditStudentInputDialog
     * Override in subclasses
     * @return - a String array of the values (these will be separated into new lines)
     */
    public String[] getValuesVector() {
		return new String[] {""};
	}

	public abstract String getSelectionMatcherType();
	public abstract String getActionMatcherType();
	public abstract String getInputMatcherType();
	
	public String getLastResult() {
		return lastResult == null ? "" : lastResult.toString();
	}

	/**
	 * @param sessionStorage new value for {@link #sessionStorage}.
	 */
	public void setSessionStorage(Map<String, Object> sessionStorage) {
		this.sessionStorage = sessionStorage; 
	}

	/**
	 * @return the {@link #sessionStorage}
	 */
	protected Map<String, Object> getSessionStorage() {
		return sessionStorage;
	}

    /**
     * If this step could be performed automatically by the tutor, tell whether it should
     * be link-triggered (when its source state is the destination state of a link just
     * traversed) or state-triggered (when its source state becomes the current state). 
     * @return true if link-triggered, false if state-triggered
     */
	public boolean isLinkTriggered() {
		return linkTriggered;
	}

	/**
	 * @param linkTriggered new value for {@link #linkTriggered}
	 */
	public void setLinkTriggered(boolean linkTriggered) {
		this.linkTriggered = linkTriggered;
	}

	/**
	 * Reset this matcher to its initial state. This is a no-op for matchers that
	 * have no internal state. But see, e.g., SolverMatcher#reset().
	 */
	public void reset() {}

	/**
	 * Whether to replace the student input with a formula result.
	 * @return {@link #replacementFormula} != null
	 */
	public boolean replaceInput() {
		return getReplacementFormula() != null;
	}

	/**
	 * @return {@link #replacementFormula}
	 */
	public String getReplacementFormula() {
		return replacementFormula;
	}

	/**
	 * @param replacementFormula new value for {@link #replacementFormula}
	 */
	public void setReplacementFormula(String replacementFormula) {
		this.replacementFormula = replacementFormula;
	}

	/**
	 * Evaluate the {@link #getReplacementFormula()} with the given arguments. 
	 * @param selection element 0 is arg to {@link CTATFunctions#evaluate(String, String, String, String)}
	 * @param action element 0 is arg to {@link CTATFunctions#evaluate(String, String, String, String)}
	 * @param input element 0 is arg to {@link CTATFunctions#evaluate(String, String, String, String)}
	 * @param vt arg for {@link CTATFunctions#CTATFunctions(VariableTable, ProblemModel, Parser)}
	 * @param pm
	 * @return
	 */
	public Vector evaluateReplacement(Vector selection, Vector action, Vector input,
			VariableTable vt, ProblemModel pm) {
		if (getReplacementFormula() == null || vt == null)
			return input;
    	CTATFunctions tempfunc = new CTATFunctions(vt, pm, pm.getFormulaParser());
    	String s = (selection != null && selection.size() > 0 ? selection.get(0).toString() : null);
    	String a = (action != null && action.size() > 0 ? action.get(0).toString() : null);
    	String i = (input != null && input.size() > 0 ? input.get(0).toString() : null);
    	
        try {
            if (trace.getDebugCode("functions")) trace.outln("functions", "evaluating "+getReplacementFormula()+" with s="+s+", a="+a+", i="+i);
            Object result = tempfunc.evaluate(getReplacementFormula(), s, a, i);
            if (result == null) {
            	trace.err("Null result from "+getReplacementFormula()+" with s="+s+", a="+a+", i="+i);
            	return input;
            }
            Vector<String> resultV = new Vector<String>();
            resultV.add(result.toString());
            return resultV;
        } catch (Exception e) {
        	trace.errStack(" Error from "+getReplacementFormula()+" with s="+s+", a="+a+", i="+i+": ", e);
            return input;
        }
	}

	/**
	 * Tell how many traversals a visit to this link represents. For some
	 * matchers, such as {@link SolverMatcher}, a visit may not be the same as a traversal.
	 * @return constant 1 for this default implementation
	 */
	public int getTraversalIncrement() {
		return 1;
	}

	/**
	 * @return XML element with this matcher's parameters
	 */
	public Element toElement() {
		Element elt = new Element("matcher");
		if (getReplacementFormula() != null)
			elt.setAttribute("replacementFormula", getReplacementFormula());
		elt.addContent(new Element("matcherType").setText(getMatcherClassType()));
		for (int p = 0; p < getParameterCount(); ++p) {
			MatcherParameter mp = getMatcherParameter(p);
			Element mpElt = new Element("matcherParameter");
			mpElt.setAttribute("name", mp.getName());
			mpElt.setText(mp.getValue().toString());
			elt.addContent(mpElt);
		}
		return elt;
	}

	/**
	 * Tell whether the tutor is the actor.
	 * @param actor
	 * @param acceptAny if true, also return true for {@value #ANY_ACTOR} 
	 * @return true if actor matches {@value DEFAULT_TOOL_ACTOR}
	 *         or {@value UNGRADED_TOOL_ACTOR}
	 */
	public static boolean isTutorActor(String actor, boolean acceptAny) {
		if (DEFAULT_TOOL_ACTOR.equalsIgnoreCase(actor))
			return true;
		if (UNGRADED_TOOL_ACTOR.equalsIgnoreCase(actor))
			return true;
		if (acceptAny && ANY_ACTOR.equalsIgnoreCase(actor))
			return true;
		return false;
	}
}
