package edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree;

import java.util.ArrayList;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTreeProperties;
import edu.cmu.pact.Utilities.trace;

public class AdditionNode extends ExpressionTreeNode {
	
	/**
	 * Creates a new AdditionNode which is the summation of multiple expressionTreeNodes.
	 * 
	 * @param children An array of at least two ExpressionTreeNodes, representing the addends.
	 * @param negated negative or not.
	 */
	public AdditionNode(ArrayList<ExpressionTreeNode> children, Boolean negated,  ExpressionTreeProperties properties){
		super(children, negated, properties);
		if(this.children.size() < 2)
			throw new Error("AdditionNode constructor being called with children.size < 2");
	}
	
	/**
	 * Creates an structurally-identical duplicate of this node recursively.
	 * 
	 * @return a structural-duplicate of this node 
	 */
	public AdditionNode clone()
	{
		ArrayList<ExpressionTreeNode> newChildren = new ArrayList<ExpressionTreeNode>();
		
		for(int i = 0; i < children.size(); i++){
			newChildren.add(children.get(i).clone());
		}
		
		return new AdditionNode(newChildren, negated,properties);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("AN ");
		if(stringNeedsRecreation) {
			sb.append('{');
			if(negated)
				sb.append('!');
			sb.append("addends").append(children);
			sb.append('}');
		} else
			sb.append(myString);
		return sb.toString();
		
	}

	/**
	 * Remove any addend children that are 0. If only one addend remains, return it.
	 * @param simpleTermsOnly if true, modify only simple terms; else all terms
	 * @return this node or single remaining child.
	 */
	protected ExpressionTreeNode collapseSubTree(boolean simpleTermsOnly) {
		ArrayList<ExpressionTreeNode> addends = new ArrayList<ExpressionTreeNode>();
		for(ExpressionTreeNode child : children){
			if(child.getClass() == AdditionNode.class){
				ArrayList<ExpressionTreeNode> grandKids = ((AdditionNode)child).collectAddends();
				if(child.negated)
				{
					for(int i = 0; i < grandKids.size(); i++){
						grandKids.get(i).negate();
					}
				}
				
				addends.addAll(grandKids);
			}else{
				addends.add(child/*.clone()(*/);
			}
		}
		this.children = addends;
		this.stringNeedsRecreation = true;
		ExpressionTreeNode result = removeIdentityOperandsAndDemote(simpleTermsOnly);
		return result;
	}

	/**
	 * This method would normally remove any 0 addends, except the last. But except
	 * for calling {@link ExpressionTreeNode#removeIdentityOperandsAndDemote()} on its
	 * children, this method is a no-op, for we want to keep the number of terms the same
	 * for functions like
	 * {@link edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.algEquivTerms}.
	 * @param simpleTermsOnly if true, modify only simple terms; else all terms
	 * @return this
	 */
	protected ExpressionTreeNode removeIdentityOperandsAndDemote(boolean simpleTermsOnly) {
		int i;

		if(trace.getDebugCode("functions"))
			trace.out("functions", "AN.removeIdentityOperandsAndDemote() "+getNegatedString()+
					" recursing down on "+children.size()+" children");

		for(i = 0; i < children.size(); ++i)
			children.set(i, children.get(i).removeIdentityOperandsAndDemote(simpleTermsOnly));
		
		stringNeedsRecreation = true;

		return this;   // leave all addends in place
	}

	/**
	 * Format a canonical string. Enclose the result in () only if the caller's
	 * level is below ours.
	 * @param callerLevel caller's index in {@link #NodeLevels} 
	 * @return {@link #children} in index order separated by operators 
	 */
	protected String toCanonicalString(int callerLevel) {
		StringBuilder sb = new StringBuilder();
		if(callerLevel < getNodeLevel())
			sb.append('(');
		for(int i=0; i < children.size(); ++i) {
			if(i > 0)
				sb.append('+');
			sb.append('(').append(children.get(i).toCanonicalString(getNodeLevel())).append(')');
		}
		if(callerLevel < getNodeLevel())
			sb.append(')');
		return sb.toString();
	}

	/**
	 * Tell whether the given node is the identity element in this operator's domain.
	 * @param operand node to test
	 * @param negated if not null, return in negated[0] false, since 0 has no effective sign for addition
	 * @return true if operand is a {@link NumberNode} and its
	 *         {@link NumberNode#getNonNegatedValue()} == 0.0 
	 */
	protected boolean isIdentity(ExpressionTreeNode operand, boolean[] negated) {
		if(!(operand instanceof NumberNode))
			return false;
		if(negated != null && negated.length > 0)
			negated[0] = false;
		return ((NumberNode) operand).getNonNegatedValue() == 0.0; 
	}
	
	/**
	 * Performs 'basic' simplification. 
	 * 
	 * For additionNodes that entails collapsing the addends of consecutive additionNodes
	 * and ordering the resulting order.
	 * 
	 * i.e. 3+(2+(1-4))=>(-4+3+2+1).
	 * @param reorderTerms if true, put terms in canonical order
	 * @return void
	 */
	public void performBasicSimplification(boolean reorderTerms){
		this.children = collectAddends();
		
		for(int i = 0; i < children.size(); i++)
		{
			children.get(i).performBasicSimplification(reorderTerms);
		}

		if(reorderTerms)
			this.orderTerms();
		this.stringNeedsRecreation = true;
		
		if(trace.getDebugCode("functions"))
			trace.out("functions", getClass().getSimpleName()+".performBasicSimplification() result "+this);
		return;
	}
	
	/**
	 * Creates a new node that is the canonical form of this node.
	 * 
	 * On top of basic-simplification (ordering,collapsing), complex simplification
	 * for AdditionNodes ends up merging similar addends.
	 * 
	 * i.e. simplifiedNode : "x+x2-4-3" -> ComplexNode: "x3-7".
	 * 
	 * NOTE: performingComplexSimplification should NOT alter the original node.
	 * 
	 * @return A canonical version/copy of the current node.
	 */
	public ExpressionTreeNode performComplexSimplification(){
		performBasicSimplification(true);
		
		ArrayList<ExpressionTreeNode> newKids = new ArrayList<ExpressionTreeNode>();
		
		int i = 0;
		for(i = 0; i < children.size(); i++)
		{
			newKids.add(children.get(i).performComplexSimplification());
		}
		
		AdditionNode cannon = new AdditionNode(newKids, this.negated,properties);
		
		cannon.orderTerms();
		
		ExpressionTreeNode result = cannon.combineAddends();
		return result;
	}
	
	/**
	 * Distributes multiplyByMe through all the addends.
	 * 
	 * This creates a new additionNode that is the equivalent of all the addends
	 * times multiplyByMe.
	 * 
	 * @param multiplyByMe
	 * @return
	 */
	public AdditionNode distribute(ExpressionTreeNode multiplyByMe)
	{
		ArrayList<ExpressionTreeNode> products = new ArrayList<ExpressionTreeNode>();
		
		int i,j;
		for(i = 0; i < children.size(); i++)
		{
			if(multiplyByMe.getClass() == AdditionNode.class)
			{
				for(j = 0; j < multiplyByMe.children.size(); j++)
				{
					products.add(children.get(i).cloneAndMultiplyBy(multiplyByMe.children.get(j)));
				}
			}
			else
			{
				products.add(children.get(i).cloneAndMultiplyBy(multiplyByMe));
			}
		}
		return new AdditionNode(products, negated,properties);
	}
	
	/**
	 * Merges any addends that can be combined.
	 * 
	 * i.e. 2x+3x+3-1 + y + xy => 5x + 2 + y + xy
	 * 
	 * @return This node, or possibly a child node or 0.0 if all terms cancel.
	 */
	private ExpressionTreeNode combineAddends()
	{
		ExpressionTreeNode left, right;
		ExpressionTreeNode sum;
		int a = 0;
		while(a < children.size()-1)
		{
			int n = a + 1;
			Boolean removedSummator = false;//For the case when terms cancel : 2.0 and -2.0.
			sum = null;
			while (n < children.size())
			{
				left = children.get(a);
				right = children.get(n);
				
				sum = left.additiveCombine(right);
				
				if(sum == null){
					n++;
					continue;
				}else if(sum.getNonNegatedString().equals("0.0")){
					children.remove(a);
					children.remove(n-1);//Array shifted left so we have to remove n-1
					removedSummator = true;
					break;
				}else{
					children.remove(n);
					children.set(a, sum);
				}
			}
			
			if(!removedSummator)
				a++;
			
		}
		if(children.size() == 0)
			return new NumberNode("0.0",properties);
		if(children.size() == 1){
			if(this.negated)
				children.get(0).negate();
			return children.get(0);
		}
		
		return this;
	}
	
	/**
	 * Converts an series of summations into an array of all the addends.
	 * 
	 * ex: x + (a + ( b + c)) => x+a+b+c
	 * 
	 * @return An array containing all of the addends.
	 */
	public ArrayList<ExpressionTreeNode> collectAddends(){
		ArrayList<ExpressionTreeNode> addends = new ArrayList<ExpressionTreeNode>();
		for(ExpressionTreeNode child : children){
			if(child.getClass() == AdditionNode.class){
				ArrayList<ExpressionTreeNode> grandKids = ((AdditionNode)child).collectAddends();
				if(child.negated)
				{
					for(int i = 0; i < grandKids.size(); i++){
						grandKids.get(i).negate();
					}
				}
				
				addends.addAll(grandKids);
			}else{
				addends.add(child/*.clone()(*/);
			}
		}
		return addends;
	}
	
	/**
	 * Calculates and returns the string representation of this tree, except for this
	 * node's negation symbol.
	 * @param preserveSimpleTerms if true, output simple terms as <i>Nx</i>, without the '*' operator
	 * @return The string representation of this tree.
	 */
	protected String getNonNegatedString(boolean preserveSimpleTerms){
		if(!stringNeedsRecreation)
			return myString;
		String tot ="";
		tot += "(";
			
		if(children.size() < 2)
			return "ERROR";
		//Go over first n-1 members of the array inserting plus signs after each one
		for(int i = 0; i < children.size() - 1; i++){
			ExpressionTreeNode node = children.get(i);
			tot += node.getNegatedString(preserveSimpleTerms) + " + ";
		}
		
		//Last member doesn't need a plus sign.
		tot += children.get(children.size() - 1).getNegatedString(preserveSimpleTerms) +  ")";

		setString(tot);
		stringNeedsRecreation = false;
		
		return myString;
	}
	
	/**
	 * Recursive function that performs the actual solving of the expression.
	 * 
	 * @return true on success (variableTable has corresponding numerical values for all the x's and y's)
	 */
	protected Boolean eval_internal(){
		Boolean eval_success = true;
		
		int i = 0;
		for(i = 0; i <children.size(); i++){
			eval_success = children.get(i).eval_internal() && eval_success;
		}
		Double total = 0.0;
		
		if(eval_success){
			for(i = 0; i <children.size(); i++){
				total += children.get(i).getEvalValue();
			}
			if(negated) 
				total = -1.0*total;
			this.setEvalValue(total);
		}
		return eval_success;
	}
	
}
