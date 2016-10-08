package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.Utilities.trace;
import fri.patterns.interpreter.parsergenerator.Parser;

/**
 * This class supports two ways of matching, by concatenating a vector into a single string,
 * and by matching on individual elements of a vector
 * @author wko2
 *
 */
public class VectorMatcher extends Matcher {
	
	private final List<Matcher> selectionMatchers, actionMatchers, inputMatchers;
	private List<Matcher>[] matchers;
	
	/**
	 * Default with no inputs
	 */
	public VectorMatcher()
	{
		this(true, new LinkedList<Matcher>(), new LinkedList<Matcher>(), new LinkedList<Matcher>());
	}
	
	public VectorMatcher(boolean concat, List<Matcher> sMatchers, List<Matcher> aMatchers,
			List<Matcher> iMatchers)
	{
		this(concat, sMatchers, aMatchers, iMatchers, Matcher.DEFAULT_ACTOR);
	}
	
	public VectorMatcher(boolean concat, List<Matcher> sMatchers, List<Matcher> aMatchers,
			List<Matcher> iMatchers, String actor)
	{
		super(concat, VECTOR);
		selectionMatchers = sMatchers;
		actionMatchers = aMatchers;
		inputMatchers = iMatchers;
		
		matchers = new List[3];
		matchers[SELECTION] = selectionMatchers;
		matchers[ACTION] = actionMatchers;
		matchers[INPUT] = inputMatchers;
		
		if(actor == null)
			actor = Matcher.DEFAULT_ACTOR;
		setDefaultActor(actor);
	}
	
	/**
	 * Create a deep copy of this instance.
	 * @return copy
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#getMatcherClassType()
	 */
	public Object clone() {
		List<Matcher> sMatchers = new ArrayList<Matcher>(selectionMatchers.size());
		List<Matcher> aMatchers = new ArrayList<Matcher>(actionMatchers.size());
		List<Matcher> iMatchers = new ArrayList<Matcher>(inputMatchers.size());
		for (Matcher m : selectionMatchers) sMatchers.add(m); 
		for (Matcher m : actionMatchers) aMatchers.add(m); 
		for (Matcher m : inputMatchers) iMatchers.add(m); 
		VectorMatcher m = new VectorMatcher(this.concat, sMatchers, aMatchers, iMatchers,
				getDefaultActor());
		m.copyFrom(this);
		return m;
	}

	public String getMatcherClassType() {
		return "MultipleVectorsMatcher";
	}
	/**
	 * If all three matchers are the same type, return their type
	 * Else, return "mixed"
	 */
	public String getMatcherTypeText() {
		if(actionMatchers.get(0).getMatcherTypeText().equals(
				inputMatchers.get(0).getMatcherTypeText())
			&& actionMatchers.get(0).getMatcherTypeText().equals(
					selectionMatchers.get(0).getMatcherTypeText()))
			return actionMatchers.get(0).getMatcherTypeText();
		else
			return "Mixed";
	}

	public String getMatcherType() {
		return Matcher.MULTIPLE_VECTORS_MATCHER;
	}

	@Override
	public Object getParameter(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getParameterCount() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private boolean matchElements(List<Matcher> matchers, Vector<String> values)
	{
		//matchers should be a minimal requirement set
		if(values.size() < matchers.size())
			return false;
		
		for(int i = 0; i < matchers.size(); i ++)
		{
			if(matchers.get(i).getMatcherType().equals(Matcher.ANY_ACTOR))
				continue;
			matchers.get(i).matchSingle(values.get(i));
		}
		return true;
	}
	
	public boolean match(Vector selection, Vector action, Vector input){
		VariableTable temp = null;
		return match(selection, action, input, temp);
	}
	public boolean match(Vector selection, Vector action, Vector input, String actor, VariableTable vt){
		// CTAT2238: code used to test actor first, since that was the cheapest test;
		// but sometimes we're in here to run the matcher to evaluate a formula, and
		// we need to do that regardless of whether we match, as when we're traversing
		// steps defined as student-performed on our way to a student-begins-here state.
		boolean matched = match(selection, action, input, vt);
		if (trace.getDebugCode("matchers"))
			trace.out("matchers", "match("+selection+","+action+","+input+","+actor+",vt): matched "+
					matched+", defaultActor "+getDefaultActor());
		if (!matched)
			return false;
		if (actor == null || (
				!actor.equalsIgnoreCase(getDefaultActor()) &&
				!Matcher.ANY_ACTOR.equalsIgnoreCase(getDefaultActor())))
			return false;
		return true;
	}
	
	
	 /**Performs a match on each of selection, action, and input using the specified matchers
	  *for each (concatenation or by elements)
	  * @param selection
	  * @param action
	  * @param input
	  * @param interp's vt to be used in case of expression matching.
	  * @return returns whether link matches or not
	  */
	 public boolean match(Vector selection, Vector action, Vector input, VariableTable vt) {

		// set to exact on the default values if we have nothing available (this
		// is necessary when in demonstrate mode)
		if (selectionMatchers.size() == 0 || actionMatchers.size() == 0
				|| inputMatchers.size() == 0) 
			if (concat) {
				selectionMatchers.add(new ExactMatcher(concat, SELECTION,
						vector2ConcatString(getDefaultSelectionVector())));
				actionMatchers.add(new ExactMatcher(concat, ACTION,
						vector2ConcatString(getDefaultActionVector())));
				inputMatchers.add(new ExactMatcher(concat, INPUT,
						vector2ConcatString(getDefaultInputVector())));
			}

		Vector[] values = new Vector[3];
		values[SELECTION] = selection;
		values[ACTION] = action;
		values[INPUT] = input;

		if (concat) {
			for (int i = 0; i < 3; i++) {
				Matcher m = matchers[i].get(0);
				if (m instanceof ExpressionMatcher) {
					if (!((ExpressionMatcher) m).matchConcatenation(selection,
							action, input, vt))
						return false;
				} else {
					boolean t = m.matchConcatenation(values[i]);
					if (t == false)
						return false;
				}
			}
			return true;
		}

		return matchElements(selectionMatchers, selection)
				&& matchElements(actionMatchers, action)
				&& matchElements(inputMatchers, input);

	}

	 /**
	  * This method should pass in a Variable Table.
	  * @param selection
	  * @param action
	  * @param input
	  * @param actor
	  * @return
	  * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher#match(java.util.Vector, java.util.Vector, java.util.Vector, java.lang.String)
	  */
	 public boolean match(Vector selection, Vector action, Vector input, String actor) {
		 // CTAT2238: don't optimize by checking actor 1st: caller may just want ExpressionMatcher to evaluate
		 boolean mResult = match(selection, action, input, (VariableTable) null);
		if (!mResult)
			return false;
		if(getDefaultActor() != actor && getDefaultActor()!=Matcher.ANY_ACTOR)
			return false;
        return true;
    }
	
	public boolean matchForHint(Vector selection, Vector action, String actor){
		return match(selection, action, actor, null);
	}
	
	/**
	 * As I understand, this is only used to match hints in an example tracer tutor
	 * and the matcher used is always Exact ...
	 * 
	 * Well, we'll do what it says ... test a match on selection
	 */
	public boolean match(Vector selection, Vector action, String actor, VariableTable vt) {
		if (!matchActor(actor))
			return false;
		
		boolean selectionsMatch;
		boolean actionsMatch = true;
		
		if(concat){
			Matcher sm = selectionMatchers.get(0);
			Matcher am = actionMatchers.get(0);
			if (sm instanceof ExpressionMatcher) {
				selectionsMatch = ((ExpressionMatcher) sm).matchConcatenation(selection, true, vt);
				if (action != null && am instanceof ExpressionMatcher)
					actionsMatch = ((ExpressionMatcher) am).matchConcatenation(action, true, vt);
			} else if (selection != null) {
				selectionsMatch = sm.matchSingle(vector2ConcatString(selection));
				if (action != null) {
					actionsMatch = am.matchSingle(vector2ConcatString(action));
				}
			} else
				return false;
		} else {
			selectionsMatch = matchElements(selectionMatchers, selection);
			if (action != null)
				actionsMatch = matchElements(actionMatchers, action);
		}
		return (selectionsMatch && actionsMatch);
	}

	public boolean match(int i, Vector v) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * 
	 * @param vector - either SELECTION, ACTION, or INPUT
	 * @return - list of the matchers
	 */
	public List<Matcher> getMatchers(int vector)
	{
		return matchers[vector];
	}
	
	public void setExternalResources(VariableTable variableTable, ProblemModel problemModel,
			Parser parser) {
		for(Matcher matcher : selectionMatchers){
			matcher.setExternalResources(variableTable, problemModel, parser);
		}
		for(Matcher matcher : actionMatchers){
			matcher.setExternalResources(variableTable, problemModel, parser);
		}
		for(Matcher matcher : inputMatchers){
			matcher.setExternalResources(variableTable, problemModel, parser);
		}
	}
	
	@Override
	public void setParameterByIndex(String stringValue, int index) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	protected void setParameterInternal(Element element, int index) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toXML(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getActionLabelText() {
		return actionMatchers.get(0).getSingleLabelText();
	}

	public String getInputLabelText() {
		return inputMatchers.get(0).getSingleLabelText();
	}

	public String getSelectionLabelText() {
		return selectionMatchers.get(0).getSingleLabelText();
	}

	public String getActionToolTipText() {
		return actionMatchers.get(0).getToolTipText();
	}

	public String getInputToolTipText() {
		return inputMatchers.get(0).getToolTipText();
	}

	public String getSelectionToolTipText() {
		return selectionMatchers.get(0).getToolTipText();
	}

    public String getToolTipText() { return getSingleLabelText(); }
    
	public String getSingleLabelText() {
		return "";
	}
	public String getActionMatcherType() {
		return actionMatchers.get(0).getMatcherTypeText();}
	public String getInputMatcherType() {
		return inputMatchers.get(0).getMatcherTypeText();}
	public String getSelectionMatcherType() {
		return selectionMatchers.get(0).getMatcherTypeText();}
	
	/**
	 * Get the value of a formula calculation specified for the input element.
	 * Override this implementation in {@link ExpressionMatcher} to return the
	 * result of the formula.
	 * @return result of {@link #getInput()}
	 */
	public String getEvaluatedInput() {
		if (inputMatchers.size() == 0)
			return getInput();
		Matcher inputMatcher = inputMatchers.get(0);
		if (!(inputMatcher instanceof ExpressionMatcher))
			return getInput();
		ExpressionMatcher exInputMatcher = (ExpressionMatcher) inputMatcher;
		if (exInputMatcher.isEqualRelation())
			return exInputMatcher.getEvaluatedInput();
		else
			return getInput();
	}

    public String getSelection() {
		if (selectionMatchers.size() == 0)
			return getDefaultSelection();
		else
			return selectionMatchers.get(0).toString();
	}
    


    public String getAction() {
		if (actionMatchers.size() == 0)
			return getDefaultAction();
		else
			return actionMatchers.get(0).toString();
	}


    public String getInput() {
		if (inputMatchers.size() == 0)
			return getDefaultInput();
		else
			return inputMatchers.get(0).toString();
	}

    /**
     * Return one of the internal matchers.
     * @param which one of "selection", "action", "input"
     * @return the 0th matcher for the requested SAI element
     */
    public Matcher getSingleMatcher(String which) {
    	if ("selection".equalsIgnoreCase(which))
    		return selectionMatchers.get(0);
    	else if ("action".equalsIgnoreCase(which))
    		return actionMatchers.get(0);
    	else
    		return inputMatchers.get(0);
    }

    public String getInputMatcher() {
		Matcher inputMatcher = inputMatchers.get(0);
		if ((inputMatcher instanceof ExactMatcher)
				&& (inputMatcher.toString().equals("")))
			return getDefaultInput();
		else
			return inputMatcher.toString();
	}


	public String getSelectionMatcher() {
    	Matcher selectionMatcher = selectionMatchers.get(0);
		if ((selectionMatcher instanceof ExactMatcher) && (selectionMatcher.toString().equals(""))) 
			return getDefaultSelection();
		else
			return selectionMatcher.toString();
	}


	public String getActionMatcher() {
    	Matcher actionMatcher = actionMatchers.get(0);
		if ((actionMatcher instanceof ExactMatcher) && (actionMatcher.toString().equals(""))) 
			return getDefaultAction();
		else
			return actionMatcher.toString();
	}

	/**
	 * Override returns {@link Matcher#replaceInput()} for {@link #inputMatchers}[0].
	 * @return false if inputMatchers.get(0) is null; else that Matcher's result.
	 */
	public boolean replaceInput() {
		Matcher inputMatcher = inputMatchers.get(0);
		if (inputMatcher == null)
			return false;
		return inputMatcher.replaceInput();
	}

	/**
	 * Override returns {@link Matcher#getReplacementFormula()} for {@link #inputMatchers}[0].
	 * @return null if inputMatchers.get(0) is null; else that Matcher's result.
	 */
	public String getReplacementFormula() {
		Matcher inputMatcher = inputMatchers.get(0);
		if (inputMatcher == null)
			return null;
		return inputMatcher.getReplacementFormula();
	}

	/**
	 * Override calls {@link Matcher#setReplacementFormula(String)} on {@link #inputMatchers}[0].
	 * No-op if inputMatchers.get(0) is null.
	 * @param replacementFormula new value for input matcher replacementFormula
	 */
	public void setReplacementFormula(String replacementFormula) {
		Matcher inputMatcher = inputMatchers.get(0);
		if (inputMatcher != null)
			inputMatcher.setReplacementFormula(replacementFormula);
	}
	
	/**
	 * @return XML element with this matcher's parameters
	 */
	public Element toElement() {
		Element elt = new Element("matchers");
		setXMLAttributes(elt);
		Element mElt = null;

		mElt = new Element("Selection");
		for (Matcher m : getMatchers(VectorMatcher.SELECTION))
			mElt.addContent(m.toElement());
		elt.addContent(mElt);
		
		mElt = new Element("Action");
		for (Matcher m : getMatchers(VectorMatcher.ACTION))
			mElt.addContent(m.toElement());
		elt.addContent(mElt);
		
		mElt = new Element("Input");
		for (Matcher m : getMatchers(VectorMatcher.INPUT))
			mElt.addContent(m.toElement());
		elt.addContent(mElt);
		
		mElt = new Element("Actor").setText(getDefaultActor());
		mElt.setAttribute(TRIGGER_ATTR, Boolean.toString(isLinkTriggered()));
		elt.addContent(mElt);
		
		return elt;
	}

	/**
	 * Set attributes for the top-level XML element.
	 * @param elt Element to receive the attributes
	 */
	protected void setXMLAttributes(Element elt) {
		elt.setAttribute("Concatenation", Boolean.toString(isConcat()));
	}	
}
