package edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree;

import java.util.ArrayList;
import java.util.Collections;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTreeProperties;
import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.ExpressionTreeNode.CompareTerms;
import edu.cmu.pact.Utilities.trace;

public class MultiplicationNode extends ExpressionTreeNode {
	//Used *only* for returning information when creating a productNode with many children.
	private class Properties
	{
		public ArrayList<ExpressionTreeNode> multiplicands;
		public ArrayList<ExpressionTreeNode> divisors;
		public boolean negated;
		public boolean simpleTerm;
		public boolean numeratorIsSimpleTerm;
		public boolean denominatorIsSimpleTerm;
		
		public String toString() {
			StringBuilder sb = new StringBuilder("MN.Properties ");
			if(negated)
				sb.append('!');
			sb.append("mult").append(multiplicands);
			sb.append(" divs").append(divisors);
			return sb.toString();
		}
	}
		
	protected ArrayList<ExpressionTreeNode> multiplicands;
	protected ArrayList<ExpressionTreeNode> divisors;

	/** See {@link #isSimpleTerm()}. */
	private boolean simpleTerm = false;
	private boolean numeratorIsSimpleTerm = false;
	private boolean denominatorIsSimpleTerm = true; //?

	/**
	 * Creates a new MultiplicationNode which is the product/division of 
	 * multiple ExpressionTreeNodes.
	 * 
	 * For simplicity this can contain both multiplicands and divisors.
	 * 
	 * i.e a*b*c/d/e/f can be held in one MultiplicationNode
	 * 
	 * A product is not a single term, but it can be a single divisor. 
	 * (i.e. x is rejected even though /x is accepted).
	 * 
	 * @param multiplicands must be at least 2 if there are no divisors.
	 * @param divisors divisors must be at least 1 if their are less than 2 multiplicands.
	 * @param negated is this node negated?
	 */
	public MultiplicationNode(ArrayList<ExpressionTreeNode> multiplicands, ArrayList<ExpressionTreeNode> divisors, Boolean negated,  ExpressionTreeProperties properties){
		super(null, negated, properties);
		
		this.multiplicands = new ArrayList<ExpressionTreeNode>(multiplicands);
		this.divisors = new ArrayList<ExpressionTreeNode>();
		if(divisors != null){
			this.divisors.addAll(divisors);
		}
		
		if(this.divisors.size() < 1 && this.multiplicands.size() < 2)
			throw new Error("MultiplicationNode() constructor with "+divisors.size()+ " divisors, "+
					multiplicands.size()+" multiplicands");
		
		children = null;//Please throw null pointers!
	}
	
	/**
	 * Creates an structurally-identical duplicate of this node recursively.
	 * @return a structural-duplicate of this node 
	 */
	public MultiplicationNode clone()
	{
		ArrayList<ExpressionTreeNode> newMultiplicands = new ArrayList<ExpressionTreeNode>();
		ArrayList<ExpressionTreeNode> newDivisors = new ArrayList<ExpressionTreeNode>();
		
		int i = 0;
		for(i = 0; i < multiplicands.size(); i++){
			newMultiplicands.add(multiplicands.get(i).clone());
		}
		for(i = 0; i < divisors.size(); i++){
			newDivisors.add(divisors.get(i).clone());
		}
		
		MultiplicationNode result = new MultiplicationNode(newMultiplicands, newDivisors, negated,
				properties);
		result.setSimpleTerm(isSimpleTerm());
		result.numeratorIsSimpleTerm = this.numeratorIsSimpleTerm;
		result.denominatorIsSimpleTerm = this.denominatorIsSimpleTerm;
		return result;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("MN ");
		if(stringNeedsRecreation) {
			sb.append('{');
			if(negated)
				sb.append('!');
			sb.append("mult").append(multiplicands);
			sb.append(" divis").append(divisors);
			sb.append('}');
		} else
			sb.append(myString);
		return sb.toString();
	}

	/**
	 * Count the nodes in this subtree.
	 * @param total amass the total in element[0] of this array
	 * @return total[0]
	 */
	protected int count(int[] total) {
		for(int i = 0; i < multiplicands.size(); ++i)
			multiplicands.get(i).count(total);
		for(int i = 0; i < divisors.size(); ++i)
			divisors.get(i).count(total);
		total[0] += 1;
		return total[0];
	}

	/**
	 * @param reorderTerms ignored for multiplication nodes
	 */
	public void performBasicSimplification(boolean reorderTerms){
		
		Properties factors = collectFactors();
		
		//Order the factors!
		this.multiplicands = factors.multiplicands;
		this.divisors = factors.divisors;
		this.negated = factors.negated;
		this.simpleTerm = factors.simpleTerm;
		this.numeratorIsSimpleTerm = factors.numeratorIsSimpleTerm;
		this.denominatorIsSimpleTerm = factors.denominatorIsSimpleTerm;
		this.orderTerms();  // reorderTerms ignored for multiplication nodes
		this.stringNeedsRecreation = true;

		if(trace.getDebugCode("functions"))
			trace.out("functions", getClass().getSimpleName()+".performBasicSimplification() result "+this);

		//ExpressionTreeNode result = new MultiplicationNode(factors.multiplicands, factors.divisors, factors.negated);
		
		//result.orderTerms();
		
		//return result;
	}

	/**
	 * This method removes any 1 factors, except the last. If there's only one factor
	 * left, return it with adjusted sign.
	 * @param simpleTermsOnly if true, modify only simple terms; else all terms
	 * @return this or child node
	 */
	protected ExpressionTreeNode removeIdentityOperandsAndDemote(boolean simpleTermsOnly) {
		int i;

		if(trace.getDebugCode("functions"))
			trace.out("functions", "MN.removeIdentityOperandsAndDemote() "+getNegatedString()+
					" recursing down on "+multiplicands.size()+" mults, "+divisors.size()+" divis");

		for(i = 0; i < multiplicands.size(); ++i)
			multiplicands.set(i, multiplicands.get(i).removeIdentityOperandsAndDemote(simpleTermsOnly));
		for(i = 0; i < divisors.size(); ++i)
			divisors.set(i, divisors.get(i).removeIdentityOperandsAndDemote(simpleTermsOnly));
		
		stringNeedsRecreation = true;

		boolean[] myNegated = { negated };
		ArrayList<ExpressionTreeNode> newMultiplicands =
				removeIdentityOperands(multiplicands, numeratorIsSimpleTerm || isSimpleTerm(), simpleTermsOnly, myNegated);
		ArrayList<ExpressionTreeNode> newDivisors =
				removeIdentityOperands(divisors, denominatorIsSimpleTerm, simpleTermsOnly, myNegated);

		if(newMultiplicands.size() > 1 || hasDivisor(newDivisors)
				|| (simpleTermsOnly && newDivisors.size() > 0)) {
			if(trace.getDebugCode("functions"))
				trace.out("functions", "MN.removeIdentityOperandsAndDemote("+simpleTermsOnly+")"+
						" nMults "+multiplicands.size()+" => "+newMultiplicands.size()+
						" nDivis "+divisors.size()+" => "+newDivisors.size()+
						" negated "+negated+" => "+myNegated[0]);
			multiplicands = newMultiplicands;
			divisors = newDivisors;
			negated = myNegated[0];
			return this;
		}
		ExpressionTreeNode singleChild = newMultiplicands.get(0);
		boolean newNegated = (singleChild.negated ^ myNegated[0]); 
		if(trace.getDebugCode("functions"))
			trace.out("functions", "MN.removeIdentityOperandsAndDemote("+simpleTermsOnly+") demotes to "+
					singleChild.getClass().getSimpleName()+" value "+singleChild.getNegatedString()+
					", sign "+singleChild.negated+" => "+newNegated);
		singleChild.negated = newNegated;
		return singleChild;
	}
	
	/**
	 * Remove identity nodes from the given child array, but always leave at least one node.  
	 * @param operands array of nodes to cull
	 * @param wasSimpleTerm whether these operands were originally a simple term
	 * @param simpleTermsOnly if true, make this call a no-op unless this is a simple term
	 * @param myNegated if not null, return here the sign change due to this operation
	 * @return
	 */
	private ArrayList<ExpressionTreeNode> removeIdentityOperands(ArrayList<ExpressionTreeNode> operands,
			boolean wasSimpleTerm, boolean simpleTermsOnly, boolean[] myNegated) {
		if(trace.getDebugCode("simpleterm"))
			trace.outNT("simpleterm", "MN.removeIdentityOperands("+operands+","+wasSimpleTerm+
					","+simpleTermsOnly+")");
		if(simpleTermsOnly && !wasSimpleTerm)
			return operands;
		ArrayList<ExpressionTreeNode> newOperands = new ArrayList<ExpressionTreeNode>();
		boolean oldNegated = myNegated[0];  
		boolean[] childNegated = new boolean[1];
		int i;
		for(i = 0; i < operands.size(); ++i) {
			ExpressionTreeNode child = operands.get(i);
			if(isIdentity(child, childNegated)) {
				if(childNegated[0])
					myNegated[0] = !(myNegated[0]);
				continue;
			}
			newOperands.add(child);
		}
		if(newOperands.isEmpty() && i > 0)          // if all prior children were identities, 
			newOperands.add(operands.get(i-1));     // preserve last child
		
		if(trace.getDebugCode("functions"))
			trace.out("functions", "MN.removeIdentityOperands() size of input "+operands.size()+
					", output "+newOperands.size()+"; negated was "+oldNegated+", is "+myNegated[0]);
		return newOperands;
	}

	/**
	 * Tell whether the given node is the identity element in this operator's domain.
	 * <b>N.B.:</b> This method returns true even if the operand's {@link #negated} property is set.
	 * @param operand node to test
	 * @param negated if not null, return in negated[0] the operand's {@link #negated} property
	 * @return true if operand is a {@link NumberNode} and its
	 *         {@link NumberNode#getNonNegatedValue()} == 1.0 
	 */
	protected boolean isIdentity(ExpressionTreeNode operand, boolean[] negated) {
		if(!(operand instanceof NumberNode))
			return false;
		if(negated != null && negated.length > 0)
			negated[0] = (((NumberNode) operand).getNegatedValue() < 0);
		return ((NumberNode) operand).getNonNegatedValue() == 1.0; 
	}

	/**
	 * Performs 'complex' simplification. 
	 * 
	 * For multiplication this results in x*(y(/z)*(a(b))) => y*x*b*a/z.
	 * 
	 * @return a canonical clone of this multiplication node. 
	 */
	public ExpressionTreeNode performComplexSimplification() {
		Properties factors = collectFactors();
		
		MultiplicationNode copy = new MultiplicationNode(factors.multiplicands, factors.divisors, factors.negated,properties);
		
		ExpressionTreeNode result = copy.mergeAndCancelTerms();
		
		return result;
	}
	
	
	/** 
	 * Starting with the currentNode, converts a nested series of multiplication/division nodes into 
	 * a single object that holds all the multiplicands,divisors, and whether its negative.
	 * 
	 * @return a 'Properties' object that contains the divisors,multiplicands, and negativity.
	 */
	public Properties collectFactors(){
		Boolean localNegated = this.negated;
		
		ArrayList<ExpressionTreeNode> newMultiplicands = new ArrayList<ExpressionTreeNode>();
		ArrayList<ExpressionTreeNode> newDivisors = new ArrayList<ExpressionTreeNode>();
		boolean numeratorIsSimpleTerm = false, denominatorIsSimpleTerm = false;
		
		
		
		//Collecting the multiplicands..
		int i = 0; 
		for(i = 0; i < multiplicands.size(); i++){
			ExpressionTreeNode child = multiplicands.get(i);
			if(child.getClass() == MultiplicationNode.class){
				Properties newProps = ((MultiplicationNode)child).collectFactors();
				newMultiplicands.addAll(newProps.multiplicands);
				newDivisors.addAll(newProps.divisors);
				if(newProps.negated){
					localNegated = !localNegated;
				}
				numeratorIsSimpleTerm = ((i == 0) && child.isSimpleTerm());
			}
			else{
				newMultiplicands.add(child);
				if(child.negated){
					localNegated = !localNegated;
					child.negate();//negating a negative makes a positive. 
									//What does negating an already negated negative do!?
				}
//				numeratorIsSimpleTerm &= child.isSimpleTerm();
			}
		}
		
		
		//Collecting the divisors
		for(i = 0; i < divisors.size(); i++){
			ExpressionTreeNode child = divisors.get(i);
			if(child.getClass() == MultiplicationNode.class){
				Properties newProps = ((MultiplicationNode)child).collectFactors();
				newMultiplicands.addAll(newProps.divisors);
				newDivisors.addAll(newProps.multiplicands);
				if(newProps.negated){
					localNegated = !localNegated;
				}
				denominatorIsSimpleTerm = ((i == 0) && newProps.denominatorIsSimpleTerm);
			}
			else
			{
				newDivisors.add(child);
				if(child.negated){
					localNegated = !localNegated;
					child.negate();//double-negative!! :)
				}
//				denominatorIsSimpleTerm &= child.isSimpleTerm();
			}
		}
		
		Properties totProps = new Properties();
		totProps.multiplicands = newMultiplicands;
		totProps.divisors = newDivisors;
		totProps.negated = localNegated;
		totProps.simpleTerm = isSimpleTerm();
		totProps.numeratorIsSimpleTerm = numeratorIsSimpleTerm;
		totProps.denominatorIsSimpleTerm = denominatorIsSimpleTerm;
	
		if(trace.getDebugCode("functions"))
			trace.outNT("functions", "MN.collectFactors() "+totProps);
		//totProps.dCoefficient = dCoefficient;
		return totProps;
	}
	
	/**
	 * Final step of basic simplification: 
	 * Order the terms in a deterministic fashion
	 *
	 * @return
	 */
	protected void orderTerms()
	{
		Collections.sort(this.multiplicands, new CompareForSort());
		Collections.sort(this.divisors, new CompareForSort());
	}
	
	/**
	 * Depth-first recursive function to order the terms in a deterministic fashion.
	 * Reorders children of {@link #multiplicands}, {@link #divisors}, then the lists themselves..
	 */
	protected void orderTermsRecursive()
	{
		for(int i = 0; i < multiplicands.size(); ++i)
			multiplicands.get(i).orderTermsRecursive();
		Collections.sort(multiplicands, new CompareTerms(false));

		for(int i = 0; i < divisors.size(); ++i)
			divisors.get(i).orderTermsRecursive();
		Collections.sort(divisors, new CompareTerms(false));

		stringNeedsRecreation = true;
		if(trace.getDebugCode("functions"))
			trace.out("functions", getClass().getSimpleName()+".orderTermsRecursive() result: "+
					toCanonicalString(XTNindex));
	}
	
	/**
	 * Merges all the products from an array, while extracting all the numerical values.
	 * 
	 * @param terms An array of terms to be merged/edited.
	 * @param num_coefficient The current numerical coefficient of the product.
	 * 
	 * @return The new numerical coefficient for the updated array of terms.
	 */
	public NumberNode mergeProductArray(ArrayList<ExpressionTreeNode> terms, NumberNode num_coefficient){
		int j = 0;
		
		while(j < terms.size())
		{
			ExpressionTreeNode mergeResult;
			ExpressionTreeNode mergeInto = terms.get(j);
			int i = j+1;
			
			while(i < terms.size()){
				ExpressionTreeNode candidate = terms.get(i);
				if(candidate.getNonNegatedString().equals("0.0")){
					terms.clear();
					return new NumberNode("0.0",properties);
				}
					
				mergeResult = mergeInto.mergeMultiplicands(candidate);
				if(mergeResult != null)
				{
					terms.remove(j);
					mergeInto = mergeResult;
				}
				else
				{
					i++;
				}
			}
			//If its a number we're going to yoink it and re-add it at the tail.
			if(mergeInto.getClass() == NumberNode.class)
			{
				num_coefficient = num_coefficient.mergeMultiplicands(mergeInto);
				terms.remove(j);
			}
			else
			{
				terms.set(j, mergeInto);
				j++;
			}
		}
		return num_coefficient;
	}
	
	/**
	 * The primary function for performing complexSimplification.
	 * 
	 * On top of collecting and merging all the products/divisors, this function 
	 * cancels similar terms in the numerator/divisor and finally distributes
	 * any summations found in the product.
	 * 
	 * @return The canonicalNode representing this product/division.
	 */
	public ExpressionTreeNode mergeAndCancelTerms()
	{
		ArrayList<ExpressionTreeNode> newMultiplicands = new ArrayList<ExpressionTreeNode>();
		ArrayList<ExpressionTreeNode> newDivisors = new ArrayList<ExpressionTreeNode>();
		NumberNode mCoefficient = new NumberNode("1.0",properties);
		NumberNode dCoefficient = new NumberNode("1.0",properties);
		
		
		orderTerms();
		//Above may be unnecessary before doing the combination step.
		//Sorting AFTERWARDS might be better and NECASSARY!
				
	
		//	Combining all the numerators:  Term (y*x*x*y) -> (y^2*x^2) 
		for(ExpressionTreeNode c : multiplicands){
			newMultiplicands.add(c.performComplexSimplification());
		}
		mCoefficient = mergeProductArray(newMultiplicands, mCoefficient);
		
		//	Combining all the Denominators: Term( 1/(y*2*x*x*y*3) )-> 1/(y^2*x^2*6)
		for(ExpressionTreeNode c : divisors){
			newDivisors.add(c.performComplexSimplification());
		}
		dCoefficient = mergeProductArray(newDivisors, dCoefficient);
		
		//Cancel out or merge any 'matching' terms in divisors/multiplicands
		//The combination of the mCoefficinet and dCoefficient should result
		//in at most one single NumberNode in the numerator.
		int m = 0, d = 0;
		boolean getNextMultiplicand;
		if(properties.cancelTerms)
		{
			while (m < newMultiplicands.size() ){
				ExpressionTreeNode result = null;
				d = 0;
				getNextMultiplicand = true;
				
				while(d < newDivisors.size()){
					result = newMultiplicands.get(m).attemptCancelTerms(newDivisors.get(d));
					if(result != null){
						if(result.getClass() == NumberNode.class){
							
							newMultiplicands.remove(m);
							getNextMultiplicand = false;//m remains the same, since we just did a remove
							newDivisors.remove(d);
							
							mCoefficient = mCoefficient.mergeMultiplicands(result);
							//if(result.negated)
							//	this.negate();
							break;
						} else {
							newMultiplicands.set(m, result);
							newDivisors.remove(d);
							break;
						}
					}else{
						d++;
					}
				}
				if(getNextMultiplicand)
					m++;
			}
		}
		//Basically making sure the exponents are positive. Might be overkill.
		for(m = 0; m < newMultiplicands.size();){
			ExpressionTreeNode x = newMultiplicands.get(m);
			if(x.getClass()== ExponentNode.class){
				ExponentNode e = (ExponentNode)x;
				if(e.getPower().negated){
					newMultiplicands.remove(m);
					e.getPower().negate();
					newDivisors.add(e);
				}else{
					m++;//next Multiplicand
				}
			}else{
				m++;//next Multiplicand
			}
		}
		//Basically making sure the exponents are positive. Might be overkill.
		for(d = 0; d < newDivisors.size();){
			ExpressionTreeNode x = newDivisors.get(d);
			if(x.getClass()== ExponentNode.class){
				ExponentNode e = (ExponentNode)x;
				if(e.getPower().negated){
					newDivisors.remove(d);
					e.getPower().negate();
					newMultiplicands.add(e);
				}else{
					d++;//next Divisor
				}
			}else{
				d++;//next Divisor
			}
		}
		
		//Divide by Zero possiblity. This should be handled as a NaN numberNode.
		NumberNode finalCoefficient = new NumberNode(
										Double.toString(mCoefficient.getNegatedValue()/dCoefficient.getNegatedValue()),properties);
		
		if(finalCoefficient.negated){
			this.negate();
			finalCoefficient.negate();
		}
		if(finalCoefficient.getNegatedValue() != 1.0){
			newMultiplicands.add(finalCoefficient);
		}
		
		//Edge case for a term in which everything cancels out.
		if(newMultiplicands.size() == 0 && newDivisors.size() == 0){
			if(this.negated)
				return new NumberNode("-1.0",properties);
			return new NumberNode("1.0",properties);
		}
		
		//Or a term that is only a single item
		if(newMultiplicands.size() == 1 && newDivisors.size() == 0){
			if(this.negated)
				newMultiplicands.get(0).negate();
			return newMultiplicands.get(0);
		}
		
		//Let's make it pretty so rather than '/x' we'll have '1/x'
		if(newMultiplicands.size() == 0){
			newMultiplicands.add(new NumberNode("1.0",properties));
		}
		
		
		if(properties.distributeSums){
			return new MultiplicationNode(newMultiplicands, newDivisors, this.negated,properties).distributeSums();
		}
		
		return new MultiplicationNode(newMultiplicands, newDivisors, this.negated,properties);
	//	stringNeedsRecreation = true;
//		return null;
	}
	
/*	public ExpressionTreeNode handleEdgeCases()
	{
		
	}*/
	
	/**
	 * Performs distribution of summations over the factors.
	 * i.e. a*(x+2) => ax + a2
	 * 
	 * @return An expressionTreeNode representing this expression after performing distribution. 
	 */
	public ExpressionTreeNode distributeSums()
	{
		ExpressionTreeNode productResult = distributeOverArray(multiplicands);
		ExpressionTreeNode divisorResult = distributeOverArray(divisors);
		
		if(productResult != null)
			this.multiplicands = productResult.children;
		if(divisorResult != null)
			this.divisors = divisorResult.children;
		
		if(divisors.size() == 0 && productResult != null)
			return productResult;
		
		return this;
	}
	
	/**
	 * Subroutine of distributeSums, performed over an array to both the numerator/divisors.
	 * 
	 * @param terms an array of nodes multiplied together.
	 * @return null, if cannot perform distribution, or a new ExpressionTree representing the product.
	 */
	private ExpressionTreeNode distributeOverArray(ArrayList<ExpressionTreeNode> terms)
	{
		if(terms.size() < 2)
			return null;
		ArrayList<AdditionNode> sums = new ArrayList<AdditionNode>();
		int i = 0;
		
		/*Collecting all the sums*/
		while(i < terms.size())
		{
			if(terms.get(i).getClass() == AdditionNode.class){
				sums.add((AdditionNode)terms.remove(i));
			}else{
				i++;
			}
		}
		//Nothing to distribute, return as is
		if(sums.size() == 0)
			return null;
		
		AdditionNode partialExpansion = sums.remove(0);
		for(i = 0; i < sums.size(); i++){
			partialExpansion = partialExpansion.distribute(sums.get(i));
		}
		if(terms.size() > 1)
			partialExpansion = partialExpansion.distribute(new MultiplicationNode(terms, null, false,properties));
		if(terms.size() == 1)
			partialExpansion = partialExpansion.distribute(terms.get(0));
		
		return partialExpansion.performComplexSimplification();
	}
	
	/**
	 * Tries to add a node to this multiplication node.
	 * Currently additive combination only works for single variables with coefficients.
	 * 
	 * This method could be improved to handle combining any two products.
	 * If the node is a cannonicalNode, a simple string comparison minus the numericalCoefficient
	 * should suffice.
	 * 
	 * i.e. 2x + 4x = 6x, but  NOT 2x^2 + x^2.
	 * 
	 * @param addMe the node that we are attempting to add.
	 * @return null if can't combine, or a node representing the sum of this product and addMe.
	 */
	protected ExpressionTreeNode additiveCombine(ExpressionTreeNode addMe)
	{
		
		//We could implement this. Currently we don't combine fractions.
		if(this.divisors.size() > 0)
			return null;
		
		/*Getting theCoefficient and nonCoefficient for 'this' */
		orderTerms();
		VariableNode thisNonCoefficient = null;
		NumberNode thisCoefficient = null;
		if(this.multiplicands.size()==2){
			if(this.multiplicands.get(1).getClass() == NumberNode.class){
				thisCoefficient = (NumberNode)multiplicands.get(1);
			}
			if(this.multiplicands.get(0).getClass() == VariableNode.class){
				thisNonCoefficient = (VariableNode)multiplicands.get(0);
			}
		}
		
		if(thisNonCoefficient == null || thisCoefficient == null)
			return null;
		
		/*Getting the Coefficient and nonCoefficient for the node we are merging*/
		addMe.orderTerms();
		VariableNode mergeNonCoefficient = null;
		NumberNode mergeCoefficient = null;
		if(addMe.getClass() == MultiplicationNode.class){
			MultiplicationNode mNode = ((MultiplicationNode)addMe);
			if( mNode.multiplicands.size() != 2 || mNode.divisors.size() > 0)
				return null;
			if(mNode.multiplicands.get(0).getClass() == VariableNode.class)
				mergeNonCoefficient = (VariableNode)mNode.multiplicands.get(0);
			if(mNode.multiplicands.get(1).getClass() == NumberNode.class)
				mergeCoefficient = (NumberNode)mNode.multiplicands.get(1);
		} 
		else if(addMe.getClass() == VariableNode.class)
		{
			mergeNonCoefficient = (VariableNode)addMe;
			mergeCoefficient = new NumberNode("1.0",properties);
		}else{
			return null;
		}
		
		/*Actually attempting to perform the merge*/
		if(thisNonCoefficient.getNonNegatedString().equals(mergeNonCoefficient.getNonNegatedString())){
			double thisNegated = 1.0;
			double mergeNegated = 1.0;
			if(this.negated){
				this.negate();
				thisNegated = -1.0;
			}
			if(addMe.negated)
				mergeNegated = -1.0;
			
			NumberNode newSum = new NumberNode( Double.toString(
													mergeCoefficient.getNegatedValue()*mergeNegated 
													+ thisCoefficient.getNegatedValue()*thisNegated
												),properties);
			
			if(newSum.getNonNegatedValue() == 0.0)
				return new NumberNode("0.0",properties);
			
			if(newSum.negated){
				this.negate();
				newSum.negate();
			}
			this.multiplicands.set(1, newSum);
			this.stringNeedsRecreation = true;
			return this;
		}
		//Un-mergeable...
		return null;
	}
	
	/**
	 * Helper function for distribution
	 * 
	 * @param x what to multiplyBY
	 * @return this*x, cloned since the node will be used in many places via distribution.
	 */
	protected ExpressionTreeNode cloneAndMultiplyBy(ExpressionTreeNode x)
	{
		MultiplicationNode copyOfMe = this.clone();
		ExpressionTreeNode copyOfX = x.clone();
		
		if(copyOfX.getClass() == MultiplicationNode.class)
		{
			copyOfMe.multiplicands.addAll(((MultiplicationNode)copyOfX).multiplicands);
			copyOfMe.divisors.addAll(((MultiplicationNode)copyOfX).divisors);
		}
		else
		{
			copyOfMe.multiplicands.add(copyOfX);
		}
		
		if(copyOfX.negated)
			copyOfMe.negate();
		
		return copyOfMe;
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
		toCanonicalString(multiplicands, sb);
		if(hasDivisor(divisors)) {
			sb.append('/');
			toCanonicalString(divisors, sb);
		}
		if(callerLevel < getNodeLevel())
			sb.append(')');
		return sb.toString();
	}

	/**
	 * @param divisorsToCheck list of child nodes to scan
	 * @return true if there's a value in divisorsToCheck other than the identity 1
	 */
	public boolean hasDivisor(ArrayList<ExpressionTreeNode> divisorsToCheck) {
		for(int i = 0; i < divisorsToCheck.size(); i++) {
			ExpressionTreeNode divisorNode = divisorsToCheck.get(i);
			if(!(divisorNode instanceof NumberNode))
				return true;
			NumberNode divisor = (NumberNode) divisorNode;
			if(divisor.getNegatedValue() != 1.0)
				return true;
		}
		return false;
	}

	/**
	 * Helper for {@link #toCanonicalString()}: format a numerator or denominator.
	 * @param members list of factors in numerator or denominator; if empty, result is "1"
	 * @param sb builder to which to append results
	 */
	private void toCanonicalString(ArrayList<ExpressionTreeNode> members, StringBuilder sb) {
		if(members.size() < 1) {
			sb.append('1');  // empty numerator or denominator
			return;
		}
		int i;
		sb.append('(');
		for(i = 0; i < members.size() - 1; i++)
			sb.append(members.get(i).toCanonicalString(getNodeLevel())).append("*");
		sb.append(members.get(i).toCanonicalString(getNodeLevel()));
		sb.append(')');
	}

	/** 
	 * Calculates and returns the string representation of this tree, except for this
	 * nodes negation symbol.
	 * @param preserveSimpleTerms if true, output simple terms as <i>Nx</i>, without the '*' operator
	 * @return The string representation of this tree.
	 */
	protected String getNonNegatedString(boolean preserveSimpleTerms){
		if(trace.getDebugCode("simpleterm"))
			trace.out("simpleterm", String.format("MN.getNonNegatedString(%b) isSimpleTerm %b, myString %s",
					preserveSimpleTerms, isSimpleTerm(), myString));

		if(preserveSimpleTerms && isSimpleTerm())
			return makeSimpleTermNonNegatedString();
		
		if(!stringNeedsRecreation)
			return myString;
		
		StringBuilder tot = new StringBuilder();
		tot.append("(");
		
		int i;
		for(i = 0; i < multiplicands.size() - 1; i++){
			ExpressionTreeNode node = multiplicands.get(i);
			tot.append(node.getNegatedString(preserveSimpleTerms)).append("*");
		}
		if(multiplicands.size() > 0)
			tot.append(multiplicands.get(i).getNegatedString(preserveSimpleTerms));
		
		for(i = 0; i < divisors.size(); i++){
			ExpressionTreeNode node = divisors.get(i);
			tot.append("/").append(node.getNegatedString(preserveSimpleTerms));
		}
		tot.append(")");
		setString( tot.toString() );
		
		stringNeedsRecreation = false;
		
		return myString;
	}
	
	private String makeSimpleTermNonNegatedString() {
		ArrayList<ExpressionTreeNode> multiplicands = new ArrayList<ExpressionTreeNode>(this.multiplicands);
		Collections.sort(multiplicands, new CompareTerms(true));
		
		StringBuilder tot = new StringBuilder();
		tot.append("(");
		int i;
		for(i = 0; i < multiplicands.size(); i++){
			ExpressionTreeNode node = multiplicands.get(i);
			tot.append(node.getNegatedString(true));
		}
		
		if(divisors.size() > 0) {
			tot.append(")/(");            // close off numerator, begin denominator
			for(i = 0; i < divisors.size(); i++){
				ExpressionTreeNode node = divisors.get(i);
				tot.append(node.getNegatedString(true));
			}
		}
		tot.append(")");
		
		return tot.toString();
	}

	/**
	 * Currently unused....
	 * Should only be called after doing a sort first (That pushes coefficients to the tail).
	 * 
	 * @return
	 */
	public String getNonCoefficientString(){
		String full = this.getNonNegatedString();
		int i;
		for(i = 0; i < full.length(); i++){
			if(full.charAt(i) >= '0' && full.charAt(i) <= '9' && full.charAt(i)!='.')
				break;
		}
		return full.substring(0, i);
	}

	
	/**
	 * Currently unused....
	 * Should only be called after doing a sort first (That pushes coefficients to the tail).
	 * 
	 * @return
	 */
	public NumberNode getCoefficient(){
		int i;
		for(i = 0; i <multiplicands.size(); i++){
			if(multiplicands.get(i).getClass() == NumberNode.class){
				if(i != multiplicands.size()-1){
					throw new Error("Coefficient in multiplciative Expression should be last...");
				}
				return (NumberNode)multiplicands.get(i);
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
		Boolean eval_success = true;
		
		int i = 0;
		for(i = 0; i <multiplicands.size(); i++){
			eval_success = multiplicands.get(i).eval_internal() && eval_success;
		}
		for(i = 0; i <divisors.size(); i++){
			eval_success = divisors.get(i).eval_internal() && eval_success;
		}
		Double numerator_total = 1.0;
		Double denominator_total = 1.0;
		
		if(eval_success){
			for(i = 0; i <multiplicands.size(); i++){
				numerator_total *= multiplicands.get(i).getEvalValue();
			}
			for(i = 0; i <divisors.size(); i++){
				denominator_total *= divisors.get(i).getEvalValue();
			}
			Double total = (numerator_total/denominator_total);
			if(negated) 
				total = -1.0*total;
			this.setEvalValue(total);
		}
		return eval_success;
	}

	/**
	 * Whether this node is a "simple term": variable references, numbers and products of the form
	 * <i>Nx</i> (<i>N</i> a number, <i>x</i> a variable, no operator or parentheses) are simple terms.
	 * @return false in this base class
	 */
	public boolean isSimpleTerm() { return simpleTerm; }

	/**
	 * See {@link #isSimpleTerm()}.
	 * @param simpleTerm new value for {@link #simpleTerm}
	 */
	public void setSimpleTerm(boolean simpleTerm) {
		super.setSimpleTerm(simpleTerm);
		this.simpleTerm = simpleTerm;
		this.numeratorIsSimpleTerm = simpleTerm;
	}
}
