package edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree;

import java.util.ArrayList;
import java.util.Collections;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTreeProperties;
import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.ExpressionTreeNode.CompareForSort;
import edu.cmu.pact.Utilities.trace;

public class ExponentNode extends ExpressionTreeNode {
	
	/**
	 * Creates a new Exponent node consisting of a base and a power.
	 * 
	 * Currently only allows ONE base and ONE power but could be expanded
	 * in the future to allow nested exponentiation. i.e. 2^x^y^z..
	 * 
	 * DOES NOT currently : 
	 * 
	 * ->convert x^0 to 1.
	 * ->factor in negativity to simplification i.e. (-x)^even_number is always positive.
	 * ->(-x)^3 * x^2 -> -(x^5)... weird edge-case
	 * 
	 * @param children An array of exactly two ExpressionTreeNodes, representing the base and power.
	 * @param negated negative or not.
	 */
	public ExponentNode(ArrayList<ExpressionTreeNode> children, Boolean negated,  ExpressionTreeProperties properties) {
		super(children, negated,properties);
		if(this.children.size() !=2)
			throw new Error("ExponentNode constructor being called with children.size !=2");
	}

	/**
	 * Creates an structurally-identical duplicate of this node recursively.
	 * 
	 * @return a structural-duplicate of this node 
	 */
	public ExponentNode clone()
	{
		ArrayList<ExpressionTreeNode> newChildren = new ArrayList<ExpressionTreeNode>();
		
		newChildren.add(getBase().clone());
		newChildren.add(getPower().clone());
		
		return new ExponentNode(newChildren, negated,properties);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("XN ");
		if(stringNeedsRecreation) {
			sb.append('{');
			if(negated)
				sb.append('!');
			sb.append(getBase()).append('^').append(getPower());
			sb.append('}');
		} else
			sb.append(myString);
		return sb.toString();
	}
	
	/**
	 * No-op since the {@link #children} elements must stay in (base, power).
	 */
	protected void orderTerms()	{}
	
	/**
	 * Depth-first recursive function to order the terms in a deterministic fashion.
	 * Reorders child terms in {@link #getBase()}, {@link #getPower()}.
	 */
	protected void orderTermsRecursive()
	{
		getBase().orderTermsRecursive();
		getPower().orderTermsRecursive();
		stringNeedsRecreation = true;
		if(trace.getDebugCode("functions"))
			trace.out("functions", getClass().getSimpleName()+".orderTermsRecursive() result: "+
					toCanonicalString(XTNindex));
	}

	/**
	 * This method removes any 1 factors, except the last. If there's only one factor
	 * left, return it with adjusted sign.
	 * @param simpleTermsOnly if true, modify only simple terms; else all terms boolean simpleTermsOnly
	 * @return this or child node
	 */
	protected ExpressionTreeNode removeIdentityOperandsAndDemote(boolean simpleTermsOnly) {
		int i;

		if(trace.getDebugCode("functions"))
			trace.out("functions", "XN.removeIdentityOperandsAndDemote() "+getNegatedString()+
					" recursing down on base "+getBase().getNegatedString()+", power "
					+getPower().getNegatedString());

		setBase(getBase().removeIdentityOperandsAndDemote(simpleTermsOnly));
		setPower(getPower().removeIdentityOperandsAndDemote(simpleTermsOnly));
		
		stringNeedsRecreation = true;

		if(isIdentity(getPower(), null)) { // can call isIdentity() only on the exponent
			boolean newNegated = getBase().negated ^ this.negated;
			if(trace.getDebugCode("functions"))
				trace.out("functions", "XN.removeIdentityOperandsAndDemote() "+getNegatedString()+
						" returning base node "+getBase().getNegatedString()+" with base.negated "+
						getBase().negated+" ^ this.negated"+this.negated+" = "+newNegated);
			getBase().negated = newNegated;
			return getBase();
		} else {
			if(trace.getDebugCode("functions"))
				trace.out("functions", "XN.removeIdentityOperandsAndDemote() "+getNegatedString()+
						" returning original node with base "+getBase().getNegatedString()+", power "
						+getPower().getNegatedString());
			return this;
		}
	}

	/**
	 * Tell whether the given node is the identity element for the exponential operator.
	 * <b>N.B.:</b> call this only on the {@link #getPower()} element of this node.
	 * @param operand node to test
	 * @param negated if not null, return in negated[0] the sign of the {@link #getPower()} element
	 * @return true if operand is a {@link NumberNode} and its
	 *         {@link NumberNode#getNegatedValue()} == 1.0 
	 */
	protected boolean isIdentity(ExpressionTreeNode operand, boolean[] negated) {
		if(!(operand instanceof NumberNode))
			return false;
		double value = ((NumberNode) operand).getNegatedValue();
		if(negated != null && negated.length > 0)
			negated[0] = (value < 0);
		return (value == 1.0); 
	}

	/**
	 * Format a canonical string. Enclose the result in () only if the caller's
	 * level is below ours.
	 * @return {@link #children} in index order separated by operators 
	 */
	protected String toCanonicalString(int callerLevel) {
		StringBuilder sb = new StringBuilder();
		if(callerLevel < getNodeLevel())
			sb.append('(');
		sb.append('(').append(getBase().toCanonicalString(getNodeLevel())).append(')');
		sb.append('^');
		sb.append('(').append(getPower().toCanonicalString(getNodeLevel())).append(')');
		if(callerLevel < getNodeLevel())
			sb.append(')');
		return sb.toString();
	}

	/**
	 * Complex simplification for an exponent node.
	 * 
	 * Performs recursive complexSimplification on the children, and possibly
	 * merges the base/power to a numerical value if possible.
	 *
	 * @return A node (possibly the same one) representing the simplified version of this exponent.
	 */
	public ExpressionTreeNode performComplexSimplification(){
		/*Recursive call on base*/
		ExpressionTreeNode result = getBase().performComplexSimplification();
		if(result != null)
			setBase(result);
		/*Recursive call on power*/
		result = getPower().performComplexSimplification();
		if(result!=null)
			setPower(result);
		
		if(getBase().getClass().equals(NumberNode.class) &&
			getPower().getClass().equals(NumberNode.class)){
			
			Double base = ((NumberNode)getBase()).getNegatedValue();
			Double power = ((NumberNode)getPower()).getNegatedValue();
			
			return new NumberNode(Double.toString(Math.pow(base, power)),properties);
		}else{
			return this;
		}
	}
	
	
	/**
	 * Attempt to merge an ExpressionTreeNode with this ExponentNode
	 * 
	 * @param candidate the candidate node for merging.
	 * @return null or a modified exponentNode 
	 */
	protected ExponentNode mergeMultiplicands(ExpressionTreeNode candidate){
		
		/* Setup candidate for merging */
		ExpressionTreeNode cbase = candidate;
		ExpressionTreeNode cpower = new NumberNode("1",properties);
		if(cbase.getClass().equals(ExponentNode.class)){
			//cbase will not be negated... we should use candidate for checking negation
			cbase = ((ExponentNode)candidate).getBase();
			cpower=  ((ExponentNode)candidate).getPower();
		}
		//We don't handle (-x)^3 * x^2..this could be an edge case but (-x)^a could 
		//result in a complex number.
		if(this.getBase().getNegatedString().equals(cbase.getNegatedString()))
		{	
			/*Start performing a Merge*/
			stringNeedsRecreation = true;
				
			if(this.getPower().getClass().equals(NumberNode.class) 
				&& cpower.getClass().equals(NumberNode.class))
			{
				Double sum = ((NumberNode)getPower()).getNegatedValue();
				sum += ((NumberNode)cpower).getNegatedValue();
				this.setPower(new NumberNode(Double.toString(sum),properties));
			}
			else
			{
				ArrayList<ExpressionTreeNode> addends = new ArrayList<ExpressionTreeNode>();
				addends.add(getPower());
				addends.add(cpower);
				AdditionNode newPower = new AdditionNode(addends,false,properties);
				this.setPower(newPower.performComplexSimplification());
			}
			
			if(candidate.negated)//using candidate to check negated rather than the base 
				this.negate();
			return this;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Attempts to divide the current node by 'divisor', returning the result
	 * if  'divisor' is a factor of this node and null otherwise.
	 * 
	 * @param divisor the node to divide by
	 * @return the result of the division or null if the terms don't 'cancel'.
	 */
	protected ExpressionTreeNode attemptCancelTerms(ExpressionTreeNode divisor){
		
		ExpressionTreeNode dbase = divisor;
		ExpressionTreeNode dpower = new NumberNode("1.0",properties);
		if(dbase.getClass().equals(ExponentNode.class)){
			dbase = ((ExponentNode)divisor).getBase();
			dpower=  ((ExponentNode)divisor).getPower();
		}
		ExpressionTreeNode result;
		
		if(this.getBase().getNegatedString().equals(dbase.getNegatedString())){
			Boolean isNegated = dbase.negated ^ this.negated;
			
			if(this.getPower().getNegatedString().equals(dpower.getNegatedString())){
				result = new NumberNode("1.0",properties);
				if(isNegated)
					result.negate();
				return result;
			}
			else if((this.getPower().getClass() == NumberNode.class) &&
				(dpower.getClass() == NumberNode.class))
			{
				Double sum = ((NumberNode)getPower()).getNegatedValue() - ((NumberNode)dpower).getNegatedValue();

				if(sum == 0){
					throw new Error("WTF sum = 0 divideby.. exponentNode");
				}
				if(sum == 1.0){
					this.getBase().negated =  isNegated;
					return getBase();
				}
				this.setPower(new NumberNode(Double.toString(sum),properties));
				this.negated = isNegated;
				return this;
			}else{
				ArrayList<ExpressionTreeNode> children = new ArrayList<ExpressionTreeNode>();
				children.add(getPower());
				dpower.negate();//we are doing this.power - divisor.power (i.e. negate divisor).
				children.add(dpower);
				ExpressionTreeNode power = new AdditionNode(children, false,properties);
				power = power.performComplexSimplification();
				
				this.setPower(power);
				this.negated = isNegated;
				
				return this;
			}
		}
		
		return null;
	}

	
	/**
	 * Attempts to parse the sub-expression into a numerical value.
	 * 
	 * Returns true on success, false otherwise. The only failure mode is if 
	 * there is a variable in the expression that isn't in the variable.
	 */
	protected Boolean eval_internal(){
		Boolean eval_success = true;
		
		eval_success = getBase().eval_internal() && eval_success;
		eval_success = getPower().eval_internal() && eval_success;
		
		Double total = 0.0;
		
		if(eval_success){
			total = Math.pow(getBase().getEvalValue(),getPower().getEvalValue());
			if(negated) 
				total = -1.0*total;
			this.setEvalValue(total);
		}
		return eval_success;
	}
	
	/**
	 * Calculates and returns the string representation of this tree, except for this
	 * nodes negation symbol.
	 * @param preserveSimpleTerms if true, output simple terms as <i>Nx</i>, without the '*' operator 
	 * @return The string representation of this tree.
	 */
	protected String getNonNegatedString(boolean preserveSimpleTerms){
		
		if(!stringNeedsRecreation)
			return myString;
		
		String tot = "";
		tot += "(";
		
		if(children.size() < 2)
			return "ERROR";

		tot += getBase().getNegatedString(preserveSimpleTerms) + "^" + getPower().getNegatedString(preserveSimpleTerms) +")";

		setString(tot);
		stringNeedsRecreation = false;
		
		return myString;
	}
	
	public ExpressionTreeNode getBase(){
		return children.get(0);
	}
	
	public ExpressionTreeNode getPower(){
		return children.get(1);
	}
	public void setBase(ExpressionTreeNode x){
		children.set(0, x);
		stringNeedsRecreation = true;
	}
	
	public void setPower(ExpressionTreeNode x){
		children.set(1, x);
		stringNeedsRecreation = true;
	}
}
