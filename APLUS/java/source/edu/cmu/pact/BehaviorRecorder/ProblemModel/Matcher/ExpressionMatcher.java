/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher;

import java.util.Date;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.equals;
import edu.cmu.pact.Utilities.XMLSpecialCharsTransform;
import edu.cmu.pact.Utilities.trace;
import fri.patterns.interpreter.parsergenerator.Parser;

/**
 * @author sewall
 *
 */
public class ExpressionMatcher extends ExactMatcher {

    private CTATFunctions functions;
    private String expression;
    private int relation;

    private static final int EQ_RELATION = 0;
    private static final int LT_RELATION = 1;
    private static final int GT_RELATION = 2;
    private static final int NOT_EQ_RELATION = 3;
    private static final int GTE_RELATION = 4;
    private static final int LTE_RELATION = 5;
    private static final int BOOL_RELATION = 6;

    public static final String[] RELATIONS = {
        "=", "<", ">", "!=", ">=", "<=", "boolean"
    };


    private String lastInput, lastError;
    private Date lastEvaluationTime;
    private boolean lastComparison;
    
    /**
     * 
     * @param concat - concatenation matching
     * @param vector - s a or i
     * @param text - the matcher's toString()
     */
    public ExpressionMatcher(boolean concat, int vector, String text) {
    	super(concat, vector, text);
        relation = EQ_RELATION; // default
        
        //parse the text, almost same as ExpressionMatcherPanel's parser
        if(text == null)
        	return;
        
        String expression;
        int beginQuote, endQuote;
//        if((beginQuote = text.indexOf("\"")) >= 0 &&  (endQuote = text.indexOf("\"", beginQuote + 1)) >= 0)      		
          if((beginQuote = text.indexOf("\"")) >= 0 &&  (endQuote = text.lastIndexOf("\"")) >= 0)  // Fixed CTAT2078
        {  
        	expression = text.substring(beginQuote + 1, endQuote);
        	String testRel = text.substring(0, text.indexOf(" "));
        	for(int i = 0; i < RELATIONS.length; i ++)
        		if(testRel.equals(RELATIONS[i]))
        		{
        			relation = i;
        			break;
        		}
        }
        else
        	expression = text;
        
        setInputExpression(expression);
    }
    
    /**
     * For instantiation from the ExpressionMatcherPanel
     * @param concat - concatenation matching or not
     * @param vector - s a or i
     * @param relation - one of the RELATIONS
     * @param input - the input
     */
    public ExpressionMatcher(boolean concat, int vector, int relation, String expression)
    {
    	super(concat, vector, null);
        setRelation(relation);
    	setInputExpression(expression);
    }

    /**
     * @return deep copy of this instance; leaves {@link #functions} unset
     * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExactMatcher#clone()
     */
    public Object clone() {
    	ExpressionMatcher m = new ExpressionMatcher(this.concat, this.vector, this.relation,
    			this.expression);
    	m.copyFrom(this);
    	if (this.functions != null)
    		m.setExternalResources(functions.getVariableTable(), problemModel, functions.getParser());
    	return m;
    }
    
    /**
     * Old constructor
     */
    public ExpressionMatcher()
    {
    	super();
        relation = EQ_RELATION; // default        
    }
    
    /** The problem model for {@link CTATFunctions}. */
    private ProblemModel problemModel;
    
    public void setExternalResources(VariableTable variableTable, ProblemModel problemModel,
    		Parser parser) {
    	this.problemModel = problemModel;
        functions = new CTATFunctions(variableTable, problemModel, parser);
    }
    
    public String getInputExpression() {
        return expression;
    }
    public void setInputExpression(String value) {
        expression = value;
    }

    public String getMatcherType() {
        return EXPRESSION_MATCHER;
    }
    
    public String getMatcherTypeText() {
		return "Formula";
	}
	
    public String getMatcherClassType() {
        return "ExpressionMatcher";
    }
    
    public String getSingleLabelText() {
    	return expression;
    }
    
    public String getToolTipText() {
    	return XMLSpecialCharsTransform.transformSpecialChars(getRelation() + " " + expression);
    }

    public Object getParameter(int index) {
        if(single)
        {
        	if(index == 0)
        		return getInputExpression();
        	else if(index == 1)
        		return getRelation();
        }
    	
    	switch(index) {
            case 0:
                return getDefaultSelection();
            case 1:
                return getDefaultAction();
            case 2:
                return getInputExpression();
            case 3:
                return getDefaultActor();
            case 4:
                return getInputExpression();
            case 5:
                return getRelation();
            default:
                return null;
        }
    }

    public void setParameterByIndex(String stringValue, int index) {
        if (trace.getDebugCode("functions")) trace.outln("functions", "setParameterByIndex(" + stringValue + ", " + index + ")");
        
        if(single)
        {
        	 switch(index) {
	        	 case 0:
	                 setInputExpression(stringValue);
	             case 1:
	                 setRelation(stringValue);
	             return;
	        }
        }
        switch(index) {
            case 0:
                setDefaultSelection(stringValue); return;
            case 1:
                setDefaultInput(stringValue); return;
            case 2:
                setDefaultAction(stringValue); return;
            case 3:
                setDefaultActor(stringValue); return;
            case 4:
                setInputExpression(stringValue); return;
            case 5:
                setRelation(stringValue); return;
            default:
                trace.err("ExactMatcher.setParameterByIndex(): unknown index "+index);
                return;
        }
    }

    /**
     * @param element
     * @param index
     * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#setParameterInternal(org.jdom.Element, int)
     */
    protected void setParameterInternal(Element element, int index) {
        setParameterByIndex(element.getText(), index);
    }

    /**
     * @param selection student's selection
     * @param action student's action
     * @param input student's input
     * @param vt if !null then given interp's vt, otherwise problem model(bestinterp)'s vt/
     * @return string, double, int, etc., that corresponds to the parsed version of either
     * the students selection action or input in the context of the interp's VT.
     */
    public Object evaluate(String selection, String action, String input, VariableTable vt) {
    	CTATFunctions tempfunc;
    	if(vt!=null)
    		tempfunc = new CTATFunctions(vt, problemModel, functions.getParser());
    	else
    		tempfunc = functions;
        try {
            if (trace.getDebugCode("functions")) trace.outln("functions", "evaluating with vt#"+
            		vt.getInstance()+": "+getInputExpression());
            return tempfunc.evaluate(getInputExpression(), selection, action, input);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            lastError = e.getMessage();
            return null;
        }
    }

    public Object interpolate(String expression, String selection, String action, String input) {
        try {
            trace.out("interpolating " + getInputExpression());
            return functions.interpolate(expression, selection, action, input);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            lastError = e.getMessage();
            return null;
        }
    }

    private boolean testInput(String selection, String action, String input) throws Exception {
    	return testVector(selection, action, input, INPUT, null);
    }
    /**
     * 
     * @param selection
     * @param action
     * @param input
     * @param vector
     * @param vt proper variable table for {@link #evaluate(String, String, String, VariableTable)}
     * @return
     */
    private boolean testVector(String selection, String action, String input, int vector, VariableTable vt)
    {
        Object result = evaluate(selection, action, input, vt);
        if(trace.getDebugCode("functions"))
        	trace.outNT("functions", "ExpressionMatcher.testVector() evaluate "+result);
        
        String comparee = null;
        switch(vector) {
        	case SELECTION:
        		comparee = selection;
        		break;
        	case ACTION:
        		comparee = action;
        		break;
        	case INPUT:
        	case NON_SINGLE:
        		comparee = input; //the old default
    	}
        
        lastResult = result;
        lastInput = comparee; //this is used in the old ExpressionMatcherPanel
        lastEvaluationTime = new Date();
        
        //trace.out("result: " + result);
       // trace.out("input: " + comparee);
       // trace.out("relation: " + getRelation() + "\n\n");

        try {
			equals eq = new equals();
			if (relation==EQ_RELATION || relation==NOT_EQ_RELATION) {
				boolean rtnVal = eq.equals(comparee, result);
				return (relation==NOT_EQ_RELATION ? !rtnVal : rtnVal);
			}
			if (relation==BOOL_RELATION) {
				if (result instanceof Boolean)
					return ((Boolean)result).booleanValue();
				else if (result instanceof String)
					return Boolean.parseBoolean((String)result);
				else
					return false;
			}
			Double resultVal = CTATFunctions.toDouble(result);
			Double compareeVal = CTATFunctions.toDouble(comparee);
			if (resultVal != null && compareeVal != null) {
				switch (relation) {
				case EQ_RELATION:        return compareeVal==resultVal;
				case NOT_EQ_RELATION:    return compareeVal!=resultVal;
				case LT_RELATION:        return compareeVal < resultVal;
				case GT_RELATION:        return compareeVal > resultVal;
				case LTE_RELATION:       return compareeVal <= resultVal;
				case GTE_RELATION:       return compareeVal >= resultVal;
				}
			}
			if (comparee == null) {
				switch (relation) {
				case EQ_RELATION:            return result == null;
				case NOT_EQ_RELATION:        return result != null;
				case LT_RELATION:            return false;
				case GT_RELATION:            return false;
				case LTE_RELATION:           return result == null;
				case GTE_RELATION:           return result == null;
	            }
			}
	        if (result==null)  // sewall 2012/01/13: moved here from top of method
	            return false;
			int comparison = comparee.compareTo(result.toString());
			switch (relation) {
			case EQ_RELATION:            return comparison==0;
			case NOT_EQ_RELATION:        return comparison!=0;
			case LT_RELATION:            return comparison < 0;
			case GT_RELATION:            return comparison > 0;
			case LTE_RELATION:           return (comparison < 0 || eq.equals(comparee, result));
			case GTE_RELATION:           return (comparison > 0 || eq.equals(comparee, result));	
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return false;
    }
    
    public boolean matchConcatenation(Vector s, Vector a, Vector i)
    {
    	return matchConcatenation(s,a,i, null);
    }
    public boolean matchConcatenation(Vector s, boolean b, VariableTable vt)
    {
    	lastComparison = testVector((String)s.get(0), null, null, vector, vt);
    	return lastComparison;
    }
    
    public boolean matchConcatenation(Vector s, Vector a, Vector i, VariableTable vt)
    {
    	lastComparison = testVector((String)s.get(0), (String)a.get(0), (String)i.get(0), vector, vt);
    	return lastComparison;
    }
    
    public boolean match(Vector selection, Vector action, Vector input, String actor, VariableTable vt) {
    	return matchConcatenation(selection, action, input, vt);
    }
    
    //only way to implement this is if we had access to the selection, action, and input vectors originally ...
    public boolean matchSingle(String s)
    {
    	throw new UnsupportedOperationException();
    }
    
    //only way to implement this is if we had access to the selection, action, and input vectors originally ...
    public boolean matchConcatenation(Vector s)
    {
    	throw new UnsupportedOperationException();
    }
    
    public boolean match(Vector selection, Vector action, Vector input, String actor) {
        boolean comparisonValue = false;

        // String actualInput = input.elementAt(0).toString();
        String actualSelection = selection.elementAt(0).toString();
        String actualAction = action.elementAt(0).toString();
        String actualInput = (input == null || input.elementAt(0) == null ? null : input.elementAt(0).toString());
        String expectedSelection = getSelection().toString();
        String expectedAction = getAction().toString();
		
        //trace.out ("actual: " + actualInput + ", " + actualSelection + ", " + actualAction);
        //trace.out ("expected: " + expectedInput + ", " + expectedSelection + ", " + expectedAction);
        //trace.out ("case insensitive = " + getCaseInsensitive());

        try {
            if (getCaseInsensitive())
                comparisonValue = (expectedSelection.equalsIgnoreCase(actualSelection) &&
                                   expectedAction.equalsIgnoreCase(actualAction) &&
                                   testInput(actualSelection, actualAction, actualInput));
            else
                comparisonValue = (expectedSelection.equals(actualSelection) &&
                				   expectedAction.equals(actualAction) &&
                                   testInput(actualSelection, actualAction, actualInput));
        } catch (Exception ex) {
            trace.err(ex.toString());
            ex.printStackTrace(System.err);
        }
        if (comparisonValue) trace.out("matcher", getActor() + " vs " + actor);
        comparisonValue = comparisonValue && matchActor(actor);

        lastComparison = comparisonValue;
        return comparisonValue;
    }

    public boolean checkExpression() {
        trace.out("validating " + getInputExpression());
        boolean check = functions.validate(getInputExpression());
        if (functions.getReturnType()==boolean.class)
            setRelation("boolean");
        else if (getRelation().equals("boolean"))
            setRelation("=");

        return check;
    }
    
    public String error() {
        return functions.errorString();
    }

    private void addMatcherParameter(Element matcherElt, String nameAttr, String content) {
		Element paramElt = new Element("matcherParameter");
		paramElt.setAttribute("name", nameAttr);
        paramElt.addContent(content);
		matcherElt.addContent(paramElt);
    }
    
    protected Document doc() {
		Element element = new Element("matcher");
		Element type = new Element("matcherType");
		type.addContent("ExpressionMatcher");
		element.addContent(type);

        addMatcherParameter(element, "Selection", getSelection());
        addMatcherParameter(element, "Action", getAction());
        addMatcherParameter(element, "Input", getInput());
        addMatcherParameter(element, "Actor", getActor());
        addMatcherParameter(element, "InputExpression", getInputExpression());
        if (trace.getDebugCode("sp")) trace.printStack("sp", "are we writing relation parameter? " + getRelation());
        addMatcherParameter(element, "relation", getRelation());
		
        return new Document(element);
    }

    public MatcherParameter getMatcherParameter(int index) {
        if (trace.getDebugCode("sp")) trace.outln("sp", getClass() + ".getMatcherParameter(" + index + ")");
        if(single)
        {
        	switch(index) {
	        	case 0:
	                return new MatcherParameter("InputExpression", getParameter(index));
	            case 1:
	                return new MatcherParameter("relation", getParameter(index));
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
                return new MatcherParameter("input", getParameter(index));
            case 3:
                return new MatcherParameter("actor", getParameter(index));
            case 4:
                return new MatcherParameter("InputExpression", getParameter(index));
            case 5:
                return new MatcherParameter("relation", getParameter(index));
            default:
                return null;
        }
    }
	public int getParameterCount()
	{
		return single ? 2 : 6;
	}

	/**
	 * Set {@link #relation} from an integer. Default setting is {@link #EQ_RELATION} if illegal value.
	 * @param relationIndex
	 */
    public void setRelation(int relationIndex) {
        if (0 <= relationIndex && relationIndex < RELATIONS.length) {
        	relation = relationIndex;
        	return;
        }
        trace.err("ExpressionMatcher.setRelation("+relationIndex+") arg outside [0,"+RELATIONS.length+
        		"), setting default "+RELATIONS[EQ_RELATION]);
        relation = EQ_RELATION;
    }
    public void setRelation(String relationName) {
        for (int i=0; i<RELATIONS.length; i++) {
            if (RELATIONS[i].equals(relationName)) {
                relation = i;
                break;
            }
        }
    }
    public String getRelation() {
        return RELATIONS[relation];
    }
    public boolean isEqualRelation() {
        return relation==EQ_RELATION;
    }
    public boolean isBooleanRelation() {
        return relation==BOOL_RELATION;
    }

    public Object lastResult() {
        return lastResult;
    }
    public String lastInput() {
        return lastInput;
    }
    public String lastError() {
        return lastError;
    }
    public Date lastEvaluationTime() {
        return lastEvaluationTime;
    }
    public boolean lastComparison() {
        return lastComparison;
    }

	/**
	 * @return {@link #lastResult} as a String
	 */
	public String getEvaluatedInput() {
		if (lastResult == null)
			return "";
		else
			return lastResult.toString();
	}
	
	/**
	 * All print statements from the Functions panel will go here ...
	 * By default, they are shown in stdout as originally coded,
	 * however, the VectorMatcherPanel's sub-dialogs will print to the
	 * a text area
	 */
	public void print(String s)
	{
		
	}
	
	/**
	 * This is a String representation of the expression matcher
	 * It can be converted back by using the ExpressionMatcherPanel(EdgeData edgeData, String text)
	 * constructor
	 */
	public String toString()
	{
		return RELATIONS[relation] + " \"" + expression + "\"";
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

	/**
	 * @return the last error from the {@link #functions} evaluator
	 */
	public String errorString() {
		if (functions == null)
			return null;
		return functions.errorString();
	}
}
