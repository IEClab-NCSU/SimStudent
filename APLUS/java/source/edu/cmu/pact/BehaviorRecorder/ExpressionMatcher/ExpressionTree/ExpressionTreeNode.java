package edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTreeProperties;
import edu.cmu.pact.Utilities.trace;

public class ExpressionTreeNode 
{	
	private Double eval_value;//Holds the result to the most recent Evaluate call
	protected ArrayList<ExpressionTreeNode> children;//all the sub-nodes. MultiplicationNode works differently.
	protected boolean negated;//Is this node positive/negative
	protected boolean stringNeedsRecreation;//Should be set to true whenever a function alters this node.
	protected String myString;//Holds the most recent result of an to-string on this node.
	protected ExpressionTreeProperties properties;
	/**
	 * Creates a new ExpressionTreeNode.
	 * 
	 * @param children Any children of this node.
	 * @param negated is this node negated or not
	 */
	public ExpressionTreeNode(ArrayList<ExpressionTreeNode> children, Boolean negated, ExpressionTreeProperties properties){
		if(children != null)
			this.children = children;
		else
			this.children = new ArrayList<ExpressionTreeNode>();
		myString = "";
		stringNeedsRecreation = true;
		this.properties = properties;
		this.negated = negated;
	}

	/**
	 * @return {@link #myString}, with suffix "!" if {@link #stringNeedsRecreation}
	 */
	public String toString() {
		return (stringNeedsRecreation ? myString + '!' : myString);
	}

	/**
	 * Whether this node is a "simple term": variable references, numbers and products of the form
	 * <i>Nx</i> (<i>N</i> a number, <i>x</i> a variable, no operator or parentheses) are simple terms.
	 * @return false in this base class
	 */
	public boolean isSimpleTerm() { return false; }

	/**
	 * No-op in this superclass.
	 * @param false if this node should not be considered a simple term
	 */
	public void setSimpleTerm(boolean b) {
		if(trace.getDebugCode("simpleterm"))
			trace.outNT("simpleterm", getClass().getSimpleName()+".setSimpleTerm("+b
					+") isSimpleTerm() "+isSimpleTerm());
	}	
	
	/**
	 * Creates an structurally-identical duplicate of this node recursively.
	 * 
	 * ExpressionTreeProperties are not duplicated.
	 * 
	 * @return a structural-duplicate of this node 
	 */
	public ExpressionTreeNode clone()
	{
		try {
			throw new CloneNotSupportedException("Superclass ExpressionTreeNode.clone() called");
		} catch(Exception e) {
			trace.errStack(e.getMessage()+": returning null", e);
		}
		return null;
	}
	
	/**
	 * Performs 'basic' simplification. 
	 * 
	 * Collapses nodes of the same type into a super-node and orders the resulting children.
	 * 
	 * 'Collapsing' refers to 3+(2+(1-4))=>(-4+3+2+1) but NOT adding the terms.
	 * Similarly, for multiplication x*(x*(x/x)) would 'collapse' to x*x*x/x
	 * but NOT x^2 or x*x.
	 * 
	 * @param reorderTerms if true, put terms in canonical order
	 * @return void
	 */
	public void performBasicSimplification(boolean reorderTerms){
		int i = 0;
		for(i = 0; i < children.size(); i++){
			children.get(i).performBasicSimplification(reorderTerms);
			if(trace.getDebugCode("functions"))
				trace.out("functions", getClass().getSimpleName()+".performBasicSimplification(child "+i+") result "+
						children.get(i));
		}
		if(trace.getDebugCode("functions"))
			trace.out("functions", getClass().getSimpleName()+".performBasicSimplification() result "+this);
	}
	
	/**
	 * Final step of basic simplification: 
	 * Order the terms in a deterministic fashion
	 *
	 * @return
	 */
	protected void orderTerms()
	{
		if(children!=null && children.size() > 1)
			Collections.sort(children, new CompareForSort());
	}
	
	/**
	 * First step of basic simplification: 
	 * Collapse nodes of the same class into one 'super-node'
	 * 
	 * 'Collapsing' refers to 3+(2+(1-4))=>(-4+3+2+1) but NOT adding the terms.
	 * Similarly, for multiplication x*(x*(x/x)) would 'collapse' to x*x*x/x
	 * but NOT x^2 or x*x.
	 *
	 * @return A collapsed superNode.
	 */
	protected ExpressionTreeNode collapseSubTree(){
		try {
			throw new IllegalStateException("Superclass ExpressionTreeNode.collapseSubTree() called");
		} catch(Exception e) {
			trace.errStack(e.getMessage()+": returning null", e);
		}
		return null;
	}

	/**
	 * Creates a new node that is the canonical form of this node.
	 * 
	 * On top of basic-simplification (ordering,collapsing), complex simplification
	 * ends up merging nodes together, complex distribution of products, canceling of terms.
	 * 
	 * Examples:
	 * 
	 * (x+x+x)       => 3x
	 * (x^3*y*y*y/x) => x^2*y^3
	 * (x+1)(a+b+c)  => xc + xb + xa + c + b + a
	 * 
	 * NOTE: performingComplexSimplification should NOT alter the original node.
	 * 
	 * @return A canonical version/copy of the current node.
	 */
	public ExpressionTreeNode performComplexSimplification(){
		return this.clone();
	}
	

	/**
	 * Attempts to divide the current node by 'x', returning the result
	 * if 'x' is a factor of this node and null otherwise.
	 * 
	 * @param x the node to divide by
	 * @return the result of the division or null if the terms don't 'cancel'.
	 */
	protected ExpressionTreeNode attemptCancelTerms(ExpressionTreeNode x){
		if(x.getNonNegatedString().equals(this.getNonNegatedString())){
			//plus/minus testing maybe redundant given the timing of the call to divideout.
			if(x.negated ^ this.negated)
			{
				return new NumberNode("-1.0",properties);
			}
			else
			{
				return new NumberNode("1.0",properties);
			}
		}else{
			return null;
		}
	}
	
	/**
	 * Attempts to merge the current node with node x using addition.
	 * 
	 * This method will only return true if one can add the two nodes together.
	 * i.e. 2x,xy, will return null. 2x,5x will return 7x.
	 * 
	 * @param x the candidate node to be added
	 * @return A new node containing the sum of the two nodes, or null on failure.
	 */
	protected ExpressionTreeNode additiveCombine(ExpressionTreeNode x){
		
		return null;
	}
	
	/**
	 * Attempts to merge the current node with node x using multiplication.
	 * 
	 * i.e. x,y=> null. x^2,x => x^3
	 * 
	 * @param x the candidate node for merging.
	 * @return null or a new or modified exponentNode 
	 */
	protected ExpressionTreeNode mergeMultiplicands(ExpressionTreeNode x){
		return null;
	}
	
	
	/**
	 * Helper function for distribution. 
	 * 
	 * @param x node to multiply by.
	 * @return a new node that represents the product of this node and x.
	 */
	protected ExpressionTreeNode cloneAndMultiplyBy(ExpressionTreeNode x)
	{
		ArrayList<ExpressionTreeNode> products = new ArrayList<ExpressionTreeNode>();
		
		products.add(this.clone());
		products.add(x.clone());
		
		MultiplicationNode result = new MultiplicationNode(products,null, false,properties);
		
		return result.performComplexSimplification();
	}
	
	/**
	 *	Attempts to evaluate this expression by interpolating the values from the 
	 *	VariableTable into the expression.
	 *
	 *	@return True on success (variableTable has corresponding numerical values for all the x's and y's)
	 */
	public Boolean evaluate()
	{
		return eval_internal();
	}
	
	/**
	 * Negates the current node so positive becomes negative and vice-versa.
	 * @return void
	 */
	public void negate(){
		negated = !negated;
	}
	
	/**
	 * Returns the evaluated value based on a prior call to evaluate(). 
	 * May be incorrect if evaluate didn't return true and will be undefined
	 * if evaluate() hasn't been called.
	 * 
	 * @return a value representing this expression, or undefined if evaluate() failed.
	 */
	public Double getEvalValue(){
		return eval_value;
	}
	
	protected void setEvalValue(Double newVal){
		this.eval_value = newVal;
	}

	/**
	 * Recursive function that performs the actual solving of the expression.
	 * 
	 * @return true on success (variableTable has corresponding numerical values for all the x's and y's)
	 */
	protected Boolean eval_internal(){
		return false;
	}
	
	protected void setString(String s){
		this.myString = s;
		stringNeedsRecreation = false;
	}
	
	/** 
	 * 	Calculates and returns the string representation of this tree, including
	 *	this nodes negation symbol.
	 * 
	 * @return The string representation of this tree.
	 */
	public String getNegatedString(){
		return getNegatedString(false);
	}
	
	/** 
	 * 	Calculates and returns the string representation of this tree, including
	 *	this nodes negation symbol.
	 * @param sortCoefficientsFirst if true, output simple terms as <i>Nx</i>, without the '*' operator
	 * @return The string representation of this tree.
	 */
	protected String getNegatedString(boolean preserveSimpleTerms){
		String res = getNonNegatedString(preserveSimpleTerms);
		if(res!=null){
			if(negated)
				return "-"+res;
			else
				return res;
		}
		return null;		
	}
	
	/** 
	 * Calculates and returns the string representation of this tree, except for this
	 * nodes negation symbol.
	 * 
	 * @return The string representation of this tree.
	 */
	public String getNonNegatedString(){
		return getNonNegatedString(false);
	}
	
	/** 
	 * Calculates and returns the string representation of this tree, except for this
	 * nodes negation symbol.
	 * @param sortCoefficientsFirst if true, output simple terms as <i>Nx</i>, without the '*' operator 
	 * @return The string representation of this tree.
	 */
	protected String getNonNegatedString(boolean preserveSimpleTerms){
		throw new IllegalStateException("superclass ExpressionTreeNode.getNonNegatedString("+
				preserveSimpleTerms+") called");
	}
	
	

	/**
	 * Comparator for sorting ExpressionTreeNodes.
	 * 
	 * @author blojasie
	 */
	protected class CompareForSort implements Comparator<ExpressionTreeNode>{

		/**
		 * Compares two objects of type ExpressionTreeNode.
		 * 
		 * @param arg0 ExpressionTreeNode
		 * @param arg1 ExpressionTreeNode
		 * 
		 * @return int representing the result of the comparison.
		 */
		public int compare(ExpressionTreeNode c0, ExpressionTreeNode c1) {

			String s0 = removeParenMinusSign(c0.getNonNegatedString());
			String s1 = removeParenMinusSign(c1.getNonNegatedString());
			
			if(trace.getDebugCode("exprtree"))
				trace.outNT("exprtree", "ExpressionTreeNode.CompareForSort("+s0+","+s1+")");
			
			// sewall 2013-07-21 Trac #228: changed test from if(s0 == s1), which should always fail
			if(s0 != null && s0.equals(s1)){//Edge-case for -expression vs expression.
				if(c0.negated == c1.negated)
					return 0;
				if(c1.negated)
					return 1;
				return -1;
			}
			
			
			//All these negated comparisons make me suspect...
			//Also we don't test for VariableNodes (only numberNodes).
			//We used to test for both before leafNode divided into number/variablenode.
			
			//LeafNodes
			Boolean is0NumberNode = (c0.getClass().equals(NumberNode.class));// && ((LeafNode)c0).isNumber();
			Boolean is1NumberNode = (c1.getClass().equals(NumberNode.class));// && ((LeafNode)c1).isNumber();;
			if((is0NumberNode ^ is1NumberNode)){//x-or
				if(is0NumberNode)
					return 1;
				else
					return -1;
			}
			//Reverse compare places numerical values at the back!
			return s1.compareTo(s0);
			//return c0.collapseToString().compareTo(c1.collapseToString());
		}
	}

	/**
	 * Comparator for sorting ExpressionTreeNodes.
	 */
	protected class CompareTerms implements Comparator<ExpressionTreeNode>{
		
		/** If true, sort coefficients ahead of variables. */
		private boolean sortCoefficientsFirst = false;
		
		public CompareTerms(boolean sortCoefficientsFirst) {
			this.sortCoefficientsFirst = sortCoefficientsFirst;
		}

		/**
		 * Compares two objects of type ExpressionTreeNode.
		 * 
		 * @param arg0 ExpressionTreeNode
		 * @param arg1 ExpressionTreeNode
		 * 
		 * @return int representing the result of the comparison.
		 */
		public int compare(ExpressionTreeNode c0, ExpressionTreeNode c1) {
			Integer result;

			String s0 = removeParenMinusSign(c0.toCanonicalString(NodeLevels.length));
			String s1 = removeParenMinusSign(c1.toCanonicalString(NodeLevels.length));
			
			// sewall 2013-07-21 Trac #228: changed test from if(s0 == s1), which should always fail
			if(s0 != null && s0.equals(s1)){//Edge-case for -expression vs expression.
				if(c0.negated == c1.negated)
					result = new Integer(0);
				if(c1.negated)
					result = new Integer(1);
				else
					result = new Integer(-1);
			} else {

				//Borg's notes
				//All these negated comparisons make me suspect...
				//Also we don't test for VariableNodes (only numberNodes).
				//We used to test for both before leafNode divided into number/variablenode.

				//LeafNodes
				Boolean is0NumberNode = (c0.getClass().equals(NumberNode.class));// && ((LeafNode)c0).isNumber();
				Boolean is1NumberNode = (c1.getClass().equals(NumberNode.class));// && ((LeafNode)c1).isNumber();;

				if(is0NumberNode && is1NumberNode){   // both are numeric nodes
					NumberNode n0 = (NumberNode) c0, n1 = (NumberNode) c1;
					// put constants in decreasing order
					result = new Integer(Double.compare(n1.getNonNegatedValue(), n0.getNonNegatedValue()));

				} else if(is0NumberNode ^ is1NumberNode){          // only one is a numeric node
					if(sortCoefficientsFirst)
						result = new Integer(is0NumberNode ? -1 : 1);  // put numerical values in the front
					else
						result = new Integer(is0NumberNode ? 1 : -1);  // put numerical values in the rear
				} else
					result = new Integer(s1.compareTo(s0));
			}
			
			if(trace.getDebugCode("exprtree"))
				trace.outNT("exprtree", "ExpressionTreeNode.CompareTerms("+s0+","+s1+") returns "+result.intValue());
			return result.intValue();
		}
	}
	
	/**
	 * Modifies the string to remove all parenthesis and a preceding minus sign.
	 * 
	 * @param s
	 * @return s modified without (,) or preceding '-'.
	 */
	private String removeParenMinusSign(String orig){
		String s = orig.replace("(", "");
		s = s.replace(")", "");
		if(s.charAt(0) == '-'){
			s = s.substring(1, s.length());
		}
		if(trace.getDebugCode("exprtree"))
			trace.outNT("exprtree", "XTN.removeParenMinusSign("+orig+") => "+s+";");
		return s;
	}
	
	/** Available subclasses. <b>N.B.:</b> keep these consistent with {@link #NNindex} etc. below. */
	static final String[] NodeLevels = {
		NumberNode.class.getSimpleName()+" "+VariableNode.class.getSimpleName(),
		ExponentNode.class.getSimpleName(),
		MultiplicationNode.class.getSimpleName(),
		AdditionNode.class.getSimpleName(),
		ExpressionTreeNode.class.getSimpleName()
	};
	/** {@link #NodeLevels} index for {@link NumberNode}. */         static final int NNindex  = 0;
	/** {@link #NodeLevels} index for {@link VariableNode}. */       static final int VNindex  = 0;
	/** {@link #NodeLevels} index for {@link ExponentNode}. */       static final int XNindex  = 1;
	/** {@link #NodeLevels} index for {@link MultiplicationNode}. */ static final int MNindex  = 2;
	/** {@link #NodeLevels} index for {@link AdditionNode}. */       static final int ANindex  = 3;
	/** {@link #NodeLevels} index for {@link ExpressionTreeNode}. */ static final int XTNindex = 4;

	/**
	 *  Maximum number of loops through {@link #makeCanonical(boolean)},
	 *  {@link #makeCanonicalWithoutCombining(boolean)}. Currently {@value #MAX_SIMPLIFY_PASSES}.
	 */
	private static final int MAX_SIMPLIFY_PASSES = 5;

	/**
	 * Transform a copy of this node into a canonical structure, for comparing with other expressions,
	 * but don't combine expressions: e.g., leave x+x as is, not as 2x.
	 * @param sort whether to sort the nodes' contents, so that different orders
	 *        of commutative operands are changed to a standard order
	 * @return string from {@link #toCanonicalString(int) toCanonicalString(ANindex)}
	 */
	public String makeCanonicalWithoutCombining(boolean sort) {
		ExpressionTreeNode copy = (ExpressionTreeNode) clone();  // single deep copy at start
		String lastResult = "";
		String result = copy.getNegatedString();
		int oldCount = Integer.MAX_VALUE, count = copy.countNodes();
		for(int i = 0; !lastResult.equals(result) && i < MAX_SIMPLIFY_PASSES; ++i) {
			lastResult = result;
			copy.performBasicSimplification(false);                  // false: don't reorder, yet
			copy = copy.removeIdentityOperandsAndDemote(false);
			oldCount = count;
			count = copy.countNodes();
			if(sort)
				copy.orderTermsRecursive();
			result = copy.getNegatedString();
			if(trace.getDebugCode("functions"))
				trace.out("functions", "XTN.makeCanonicalWithoutCombining["+i+
						"] count "+oldCount+" => "+count+
						", result "+lastResult+" => "+result);
		}
		return result;
	}

	/**
	 * Simplify this node, calling {@link #removeIdentityOperandsAndDemote()}
	 * but limiting its effect to simple terms, for stricter comparing with other expressions.
	 * Don't combine expressions: e.g., leave x+x as is, not as 2x.
	 * @param sort whether to sort the nodes' contents, so that different orders
	 *        of commutative operands are changed to a standard order
	 * @return string from {@link #toCanonicalString(int) toCanonicalString(ANindex)}
	 */
	public String simplifyWithoutCanonicalWithoutCombining(boolean sort) {
		ExpressionTreeNode copy = (ExpressionTreeNode) clone();  // single deep copy at start
		String lastResult = "";
		String result = copy.getNegatedString();
		int oldCount = Integer.MAX_VALUE, count = copy.countNodes();
		for(int i = 0; !lastResult.equals(result) && i < MAX_SIMPLIFY_PASSES; ++i) {
			lastResult = result;
			copy.performBasicSimplification(false);                  // false: don't reorder, yet
			copy = copy.removeIdentityOperandsAndDemote(true);
			oldCount = count;
			count = copy.countNodes();
			if(sort)
				copy.orderTermsRecursive();
			result = copy.getNegatedString(true);
			if(trace.getDebugCode("functions"))
				trace.out("functions", "XTN.simplifyWithoutCanonicalWithoutCombining["+i+
						"] count "+oldCount+" => "+count+
						", result "+lastResult+" => "+result);
		}
		return result;
	}

	/**
	 * Transform a copy of this node into a canonical structure, for comparing with other expressions.
	 * @param sort whether to sort the nodes' contents, so that different orders
	 *        of commutative operands are changed to a standard order
	 * @return string from {@link #getNegatedString()}
	 */
	public String makeCanonical(boolean sort) {
		ExpressionTreeNode copy = (ExpressionTreeNode) clone();  // single deep copy at start
		String lastResult = "";
		String result = copy.getNegatedString();
		int oldCount = Integer.MAX_VALUE, count = copy.countNodes();
		for(int i = 0; !lastResult.equals(result) && i < MAX_SIMPLIFY_PASSES; ++i) {
//			result = copy.removeIdentityOperandsAndDemote();        doesn't help
			lastResult = result;
			copy = copy.performComplexSimplification();
			oldCount = count;
			count = copy.countNodes();
			if(sort)
				copy.orderTermsRecursive();
			result = copy.getNegatedString();
			if(trace.getDebugCode("functions"))
				trace.out("functions", "XTN.makeCanonical["+i+
						"] count "+oldCount+" => "+count+
						", result "+lastResult+" => "+result);
		}
		return result; // was result.toCanonicalString(XTNindex);
	}

	/**
	 * Count the nodes in this subtree.
	 * @return number of nodes
	 */
	public int countNodes() {
		int[] total = new int[1];
		return count(total);
	}

	/**
	 * Count the nodes in this subtree.
	 * @param total amass the total in element[0] of this array
	 * @return total[0]
	 */
	protected int count(int[] total) {
		for(int i = 0; i < children.size(); ++i)
			children.get(i).count(total);
		total[0] += 1;
		return total[0];
	}

	/**
	 * If this node has an operator, remove any of its operands that are the identity element
	 * of its domain.
	 * @param simpleTermsOnly if true, modify only simple terms; else all terms boolean simpleTermsOnly
	 * @return null in this superclass, after throwing {@link IllegalStateException}
	 */
	protected ExpressionTreeNode removeIdentityOperandsAndDemote(boolean simpleTermsOnly) {
		try {
			throw new IllegalStateException("Superclass ExpressionTreeNode.removeIdentityOperandsAndDemote() called");
		} catch(Exception e) {
			trace.errStack(e.getMessage()+": returning null", e);
		}
		return null;
	}

	/**
	 * Tell whether the given node is the identity element in this operator's domain.
	 * @param operand node to test
	 * @param negated if not null, return in negated[0] the operand's {@link #negated} property
	 * @return false in this superclass, after throwing {@link IllegalStateException}
	 */
	protected boolean isIdentity(ExpressionTreeNode operand, boolean[] negated) {
		try {
			throw new IllegalStateException(getClass().getSimpleName()+".isIdentity("+operand+") called");
		} catch(Exception e) {
			trace.errStack(e.getMessage()+": returning false", e);
		}
		return false;
	}

	/**
	 * Format a canonical string. This top-level form dumps a stack trace.
	 * @param callerLevel caller's index in {@link #NodeLevels} 
	 * @return null
	 */
	protected String toCanonicalString(int callerLevel) {
		try {
			throw new IllegalStateException("Superclass ExpressionTreeNode.toCanonicalString("+callerLevel+") called");
		} catch(Exception e) {
			trace.errStack(e.getMessage()+": returning null", e);
		}
		return null;
	}

	/** This class's level in the {@link #nodeLevel} hierarchy. */
	protected final int nodeLevel;
	{
		int i; for(i = 0; !NodeLevels[i].contains(getClass().getSimpleName()); i++);
		nodeLevel = i;
		if(nodeLevel >= NodeLevels.length)
			throw new IllegalStateException("Class "+getClass().getCanonicalName()+" not found in NodeLevels");
	}
	public int getNodeLevel() {
		return nodeLevel;
	}

	/**
	 * Depth-first recursive function to order the terms in a deterministic fashion.
	 * Reorders {@link #multiplicands}, {@link #divisors}.
	 */
	protected void orderTermsRecursive() {
		for(int i = 0; i < children.size(); ++i)
			children.get(i).orderTermsRecursive();
		Collections.sort(children, new CompareTerms(false));
		stringNeedsRecreation = true;
		if(trace.getDebugCode("functions"))
			trace.out("functions", getClass().getSimpleName()+".orderTermsRecursive() result: "+
					toCanonicalString(XTNindex));
	}

}
