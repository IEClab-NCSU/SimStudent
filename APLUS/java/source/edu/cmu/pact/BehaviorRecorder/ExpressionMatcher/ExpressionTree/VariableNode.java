package edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree;

import java.util.ArrayList;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTreeProperties;
import edu.cmu.pact.Utilities.trace;

public class VariableNode extends ExpressionTreeNode {
		
	/** True by default. Can be false if this expression was parenthesized. */
	private boolean simpleTerm = true;

	/**
	 * Creates a new VariableNode.
	 * 
	 * @param children Any children of this node.
	 * @param negated is this node negated or not
	 */
	public VariableNode(String value, ExpressionTreeProperties properties) {
		super(null, false, properties);		
		if(value.charAt(0) == '-'){
			this.negated = true;
			setString(value.substring(1));
		}else{
			setString(value);
		}
	}

	/**
	 * No-op in this leaf node.
	 * @param simpleTermsOnly if true, modify only simple terms; else all terms
	 * @return this
	 */
	protected ExpressionTreeNode removeIdentityOperandsAndDemote(boolean simpleTermsOnly) {
		return this;
	}
	
	/**
	 * No-op since there's only a single term.
	 */
	protected void orderTermsRecursive() {}
	
	/**
	 * No-op since there's only a single term.
	 */
	protected void orderTerms()	{}
	
	/**
	 * Creates an structurally-identical duplicate of this node recursively.
	 * 
	 * @return a structural-duplicate of this node 
	 */
	public VariableNode clone()
	{
		return new VariableNode(this.getNegatedString(), properties);
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
	
	protected String getNonNegatedString(boolean preserveSimpleTerms){
		return myString;
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
	protected ExpressionTreeNode mergeMultiplicands(ExpressionTreeNode candidate){
		if(candidate.getClass().equals(ExponentNode.class)){
			return candidate.mergeMultiplicands(this);
		}
		
		if(this.getNonNegatedString().equals(candidate.getNonNegatedString())){
			ArrayList<ExpressionTreeNode> args = new ArrayList<ExpressionTreeNode>();
			
			Boolean exponentNegated = this.negated^candidate.negated;
			this.negated = candidate.negated = false;
			
			args.add(candidate);
			args.add(new NumberNode("2",properties));
			
			return new ExponentNode(args, exponentNegated,properties);
		}
		
		return null;
	}
	
	/**
	 * WARNING: Will only combine with other VariableNodes, possibly skipping a combination of x with 2x.
	 *
	 * It is assumed that any combination algorithm over addends will first sort and then combine left 
	 * to right (i.e. "2x +x" not "x + 2x", which would fail)
	 * 
	 */
	protected ExpressionTreeNode additiveCombine(ExpressionTreeNode x){
		if(this.getClass() == VariableNode.class && x.getClass() == VariableNode.class){
			if(this.getNonNegatedString().equals(x.getNonNegatedString())){
				
				if(this.negated ^ x.negated)//Opposite signs cancel out.
					return new NumberNode("0.0",properties);
				
				//At this point we have -x - x or x + x...
				
				ArrayList<ExpressionTreeNode> multiplicands = new ArrayList<ExpressionTreeNode>();
				multiplicands.add(this);
				NumberNode multiplier = new NumberNode("2.0",properties);
				multiplicands.add(multiplier);
				
				
				return new MultiplicationNode(multiplicands, null, this.negated,properties);
			}
		}
		return null;
	}
	
	/**
	 * Recursive function that performs the actual solving of the expression.
	 * 
	 * @return true on success (variableTable has corresponding numerical values for all the x's and y's)
	 */
	protected Boolean eval_internal(){
		if(properties.variableTable.containsKey(myString)){
			Object result = properties.variableTable.get(myString);
			Double value = Double.parseDouble(result.toString());
			if(negated)
				value =-value;
			this.setEvalValue(value);
			return true;
		}else{
			return false;
		}
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
