package edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree;


import java.util.ArrayList;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTreeProperties;
import edu.cmu.pact.Utilities.trace;

public class NumberNode extends ExpressionTreeNode {

	public Double numericalValue;
	
	/** True by default. Can be false if this expression was parenthesized. */
	private boolean simpleTerm = true;

	/**
	 * Creates a new NumberNode.
	 * 
	 * Must be a standard representation of a number excepted by the 
	 * Double.parseDouble method.
	 * 
	 * @param value String representation of number. Must correspond to an actual Number.
	 * @param negated is this node negated or not
	 */
	public NumberNode(String value, ExpressionTreeProperties properties) {//Not sure if we should make an exception for 4/3
		super(null, false, properties);//Negated should probably be always sent as false
							//and recalculated based on parsing 'value'. Is it -2.01 or 2.01?
		String tempString = "";
		if(value.charAt(0)=='-'){
			this.negated = true;
			tempString = value.substring(1, value.length()-1);
			//setString(value.substring(1, value.length()-1));
			
		}else{
			this.negated = false;
			tempString = value;
		}
		
		
		try{
			numericalValue = Double.parseDouble(tempString);
			setString(Double.toString(numericalValue));
		}catch(NumberFormatException e){
			if (trace.getDebugCode("functions"))
				trace.out("functions", "Error " + value + ": Result not double");
		}
	}
	
	/**
	 * Creates an structurally-identical duplicate of this node recursively.
	 * 
	 * @return a structural-duplicate of this node 
	 */
	public NumberNode clone()
	{
		return new NumberNode(this.getNegatedString(),properties);
	}

	/**
	 * No-op since there's only a single term.
	 */
	protected void orderTermsRecursive() {}
	
	/**
	 * No-op since there's only a single term.
	 */
	protected void orderTerms()	{}
	
	protected ExpressionTreeNode attemptCancelTerms(ExpressionTreeNode x){
		//If not number-node, return null
		if(!x.getClass().equals(NumberNode.class))
			return null;
		
		NumberNode num = (NumberNode)x;
		
		return new NumberNode(Double.toString(getNegatedValue()/num.getNegatedValue()),properties);
	}
	
	public double getNonNegatedValue(){
		return numericalValue;
	}
	
	public double getNegatedValue(){
		if(negated)
			return -1*numericalValue;
		else
			return numericalValue;
	}

	/**
	 * Format a canonical string.
	 * @return {@link #getNegatedString()
	 */
	protected String toCanonicalString(int callerLevel) {
		if(negated)
			return "(-"+getNonNegatedString()+")";
		else
			return getNonNegatedString();
	}

	/**
	 * No-op in this leaf node.
	 * @param simpleTermsOnly if true, modify only simple terms; else all terms boolean simpleTermsOnly
	 * @return this
	 */
	protected ExpressionTreeNode removeIdentityOperandsAndDemote(boolean simpleTermsOnly) {
		return this;
	}

	/**
	 * Attempt to merge a leaf with an ExpressionTreeNode.
	 * 
	 * Merge is only possible with another leaf (or exponent w/ base) that has same
	 * the same toString value.
	 * 
	 * @param candidate the candidate node for merging.
	 * @return null or a new or modified exponentNode 
	 */
	protected NumberNode mergeMultiplicands(ExpressionTreeNode candidate){
		
		//Anything times zero is zero.
		if(this.numericalValue == 0.0)
			return this;
				
		
		if(candidate.getClass().equals(NumberNode.class)){
			Double candidateValue = ((NumberNode)candidate).getNegatedValue();
		
			return new NumberNode(Double.toString(this.getNegatedValue()*candidateValue),properties);
		}
		
		return null;
	}
	
	protected ExpressionTreeNode additiveCombine(ExpressionTreeNode x){
		if(x.getClass() == NumberNode.class){
			return new NumberNode(Double.toString(this.getNegatedValue() + ((NumberNode)x).getNegatedValue()),properties);
		}
		
		return null;
	}
	protected Boolean eval_internal(){
		setEvalValue(this.getNegatedValue());
		return true;
	}
	protected String getNonNegatedString(boolean preserveSimpleTerms){
		return myString;
	}

	/**
	 * Whether this node is a "simple term": variable references, numbers and products of the form
	 * <i>Nx</i> (<i>N</i> a number, <i>x</i> a variable, no operator or parentheses) are simple terms.
	 * @return {@link NumberNode#simpleTerm}
	 */
	public boolean isSimpleTerm() { return simpleTerm; }

	/**
	 * See {@link #isSimpleTerm()}.
	 * @param simpleTerm new value for {@link #simpleTerm}
	 */
	public void setSimpleTerm(boolean simpleTerm) {
		super.setSimpleTerm(simpleTerm);
		this.simpleTerm = simpleTerm;
	}
}
