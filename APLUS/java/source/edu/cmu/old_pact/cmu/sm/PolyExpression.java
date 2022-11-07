package edu.cmu.old_pact.cmu.sm;

//polynomials

import java.util.Vector;

import edu.cmu.old_pact.cmu.sm.query.ArrayQuery;
import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.pact.Utilities.trace;

public class PolyExpression extends Expression implements CompoundExpression {
	private Expression terms[] = new Expression[10]; //terms can be any type of expression
	private int signs[] = new int[10]; //first sign comes BEFORE first term
	private int numTerms=0;
	
	public static final int POSITIVE = 1; //positive sign
	public static final int NEGATIVE = 0; //negative sign

	public PolyExpression() {
//		terms = new Vector();
	}		

	//constructor
	//put together two expressions -- if sign is 0, negative, otherwise, positive
	public PolyExpression(Expression t1,Expression t2,int sign1,int sign2) {
		this(); //call constructor with no args
		terms[numTerms] = t1;
		terms[numTerms+1] = t2;
		signs[numTerms++] = sign1;
		signs[numTerms++] = sign2;
	}
	
	//If only one sign is given, we interpret it as the sign for the SECOND expression
	//(that is, it is the connector)
	public PolyExpression(Expression t1,Expression t2,int sign2) {
		this(t1,t2,POSITIVE,sign2); //call constructor with full args, passing Positive for 1st
	}
	
	//you can also create a PolyExpression by passing a vector of expressions
	//This constructor is smart about setting the signs
	public PolyExpression(Vector expressions) {
		this();
		for (int i=0;i<expressions.size();++i) {
			Expression ex = (Expression)(expressions.elementAt(i));
			if (ex.isNegative() && i>0)
				insert(ex.negate(),NEGATIVE);
			else
				insert(ex,POSITIVE);
		}
	}
	
	public PolyExpression(ExpressionArray expressions) {
		this();
		for (int i=0;i<expressions.size();++i) {
			Expression ex = expressions.expressionAt(i);
			if (ex.isNegative() && i>0)
				insert(ex.negate(),NEGATIVE);
			else
				insert(ex,POSITIVE);
		}
	}
	
	//This constructor passes a vector of expressions, all of which are inserted
	//using the given sign
	public PolyExpression(Vector expressions, int sign) {
		this();
		for (int i=0;i<expressions.size();++i) {
			Expression ex = (Expression)(expressions.elementAt(i));
			insert(ex,sign);
		}
	}

	//This constructor passes a vector of expressions and a vector of signs
	public PolyExpression(Vector expressions, Vector signs) {
		this();
		for (int i=0;i<expressions.size();++i) {
			Expression ex = (Expression)(expressions.elementAt(i));
			Integer sign = (Integer)(signs.elementAt(i));
			insert(ex,sign.intValue());
		}
	}
	
	public PolyExpression(Expression expressions[], int signs[],int numExpressions) {
		this();
		for (int i=0;i<numExpressions;++i)
			insert(expressions[i],signs[i]);
	}
	
	//Signs can also be given as an array of ints
	public PolyExpression(Vector expressions, int signs[]) {
		this();
		for (int i=0;i<expressions.size();++i) {
			Expression ex = (Expression)(expressions.elementAt(i));
			insert(ex,signs[i]);
		}
	}

	public void finalize() throws Throwable{
		try{
//			if(terms != null){
//				terms.removeAllElements();
//			}
			for (int i=0;i<numTerms;++i) {
				terms[i]=null;
			}
			terms = null;
			signs = null;
		}
		finally{
			super.finalize();
		}
	}

	public Expression removeRedundantFencesWhole(){
		//trace.out("PE.rRFW:     begin: " + debugForm());
		Expression unfenced = (new PolyExpression(terms,signs,numTerms)).cleanExpression();
		/*loop over the subterms, looking for a fencedexpression.
          each time we find one, unfence it and see if the parens
          still pop out of termString().  If they do, then they're
          mathematically necessary and we can stick with the modified
          version of the subterm.*/
		ExpressionArray comps = unfenced.getComponentArray();
		for(int i=0;i<comps.size();i++){
			if(comps.expressionAt(i) instanceof FencedExpression){
				String oldExp = unfenced.toASCII("(",")");
				/*unfence it*/
				comps.setExpressionAt((comps.expressionAt(i)).unfence(),i);
				unfenced = (new PolyExpression(comps.getExpressions(),signs,comps.size())).cleanExpression();
				/*check if we've broken the parenthesization*/
				/*trace.out("PE.rRFW: unfenced comp " + i + "; comparing: " + oldExp +
				  " =?= " + unfenced.toASCII("(",")"));*/
				if(!oldExp.equals(unfenced.toASCII("(",")"))){
					/*we did; put it back in the fences*/
					comps.setExpressionAt(new FencedExpression(comps.expressionAt(i)),i);
					unfenced = (new PolyExpression(comps.getExpressions(),signs,comps.size())).cleanExpression();
					//trace.out("PE.rRFW: comparison failed, returned to: " + unfenced.toASCII("(",")"));
				}
			}
		}

		ExpressionArray.deallocate(comps);

		//trace.out("PE.rRFW: returning: " + unfenced.debugForm());
		return unfenced;
	}

	protected Expression buildFromComponents(Vector components) {
		//should throw some error
		trace.out("ERROR: polynomial always needs to build with component info");
		Expression ex = new PolyExpression(components);
		return ex.cleanExpression();
	}
	
	protected Expression buildFromComponents(ExpressionArray components) {
		//should throw some error
		trace.out("ERROR: polynomial always needs to build with component info");
		Expression ex = new PolyExpression(components.getExpressions(),signs,components.size());
		return ex.cleanExpression();
	}
		
	protected Expression buildFromComponents(Vector components,Object signs) {
		Vector signVec = (Vector)signs;
		Expression ex = new PolyExpression(components,signVec);
		return ex.cleanExpression();
	}
	
	protected Expression buildFromComponents(ExpressionArray components,Object signs) {
		Vector signVec = (Vector)signs; //We should really make sure that the component info is an array, not a vector...
		int signRa[] = new int[signVec.size()];
		for (int i=0;i<signVec.size();++i) {
			Integer signInt = (Integer)(signVec.elementAt(i));
			signRa[i] = signInt.intValue();
		}
		Expression ex = new PolyExpression(components.getExpressions(),signRa,components.size());
		return ex.cleanExpression();
	}

	//getFullComponents returns the components with their signs
	public Vector getFullComponents() {
		Vector comps = new Vector();
		for (int i=0;i<numTerms;++i) {
			comps.addElement(getTermAt(i));
		}
		return comps;
	}
	
	public ExpressionArray getFullComponentArray() {
		ExpressionArray compArray = ExpressionArray.allocate();
		for (int i=0;i<numTerms;++i)
			compArray.addExpression(getTermAt(i));
		return compArray;
	}
	
	//getComponents returns the components without signs
	public Vector getComponents() {
		Vector comps = new Vector();
		for (int i=0;i<numTerms;++i) {
			comps.addElement(getTermNoSign(i));
		}
		return comps;
	}
	
	public ExpressionArray getComponentArray() {
		ExpressionArray compArray = ExpressionArray.allocate(terms,numTerms);
		return compArray;
	}

	
	public Object getComponentInfo() {
		Vector signVec = new Vector();
		for (int i=0;i<numTerms;++i)
			signVec.addElement(new Integer(signs[i]));
		return signVec;
	}
	
	public int numberOfTerms() {
		return numTerms;
	}

	public boolean isNegative() {
		return getTermNoSign(0).isNegative();
	}
	
	//Internal add to poly
	public void insert(Expression ex,int sign) {
//		trace.out("about to addElement to terms\n");
//		terms[numTerms] = ex;
		terms = Expression.addToArray(ex,terms,numTerms);
//		trace.out("after addElement to terms\n");
		signs = Expression.addToIntArray(sign,signs,numTerms++);
//		signs[numTerms++] = sign;
	}
	
	//insertSimpleSign inserts and checks the sign of the to-be-inserted expression
	//This prevents double signs (of the +- variety)
	public void insertSimpleSign(Expression ex) {
		if (ex.isNegative()) {
			terms = Expression.addToArray(ex.negate(),terms,numTerms);
//			terms[numTerms] = ex.negate();
			signs = Expression.addToIntArray(NEGATIVE,signs,numTerms++);
//			signs[numTerms++] = NEGATIVE;
		}
		else {
			terms = Expression.addToArray(ex,terms,numTerms);
//			terms[numTerms] = ex;
			signs = Expression.addToIntArray(POSITIVE,signs,numTerms++);
//			signs[numTerms++] = POSITIVE;
		}
	}
	
	//removeTermAt removes a term from the polynomial
	public void removeTermAt (int index) {
		for (int i=index;i<numTerms-1;++i) {
			if (i < (signs.length-1)) {
				signs[i] = signs[i+1];
				terms[i] = terms[i+1];
			}
		}
		numTerms--;
	}
	
	//mergeTerms merges the expressions together
	//If the expression to be merged in is a PolyExpression, we add it term-by-term
	//Otherwise, we add it whole
	private void mergeTerms(Expression ex) {
		if (ex instanceof PolyExpression) {
			PolyExpression pEx = (PolyExpression)ex;
			for (int i=0;i<pEx.numberOfTerms();++i)
				insertSimpleSign(pEx.getTermAt(i));
		}
		else
			insert(ex,POSITIVE);
	}

	
	///Add methods
	//Add any expression
	public Expression add(Expression ex) {
		PolyExpression newEx = new PolyExpression(terms,signs,numTerms);
		newEx.mergeTerms(ex); //use merge instead of insert, so adding a Poly won't create nesting
		return newEx;
	}
	
	///Subtract methods
	//Subtract any expression
	public Expression subtract(Expression ex) {
		PolyExpression newEx = new PolyExpression(terms,signs,numTerms);
		newEx.insert(ex,NEGATIVE); //Here, use insert instead of merge, since we do want to create nesting
		return newEx;
	}
	
	//hmm -- what should the coefficient of a polyExpression be?
	//I guess "1"
	public Expression simplifiedCoefficient() {
		return new NumberExpression(1);
	}
	
	public Expression exceptSimplifiedCoefficient() {
		return this;
	}
		
	//removeCombinableTerms returns a vector of terms that can be combined with the given one
	//It destructively modifies the vector to remove this terms
	//The resulting vector will include the original (comparison) term
	private Vector removeCombinableTerms(Expression comparison) {
		Vector result = new Vector();
		Vector termsToRemove = new Vector();
		for (int i=0;i<numTerms;++i) {
			Expression thisTerm = getTermAt(i);
			//trace.out("PE.rCT: checking "+thisTerm.debugForm()+" against "+comparison.debugForm());
			if (comparison.isLike(thisTerm)) {
				//trace.out("        removing");
//				if (signs[i] == NEGATIVE)
//					thisTerm = thisTerm.negate();//make sure this simplifies...
				result.addElement(thisTerm);
				termsToRemove.insertElementAt(new Integer(i),0);
			}
		}
		//now, remove all the combinable terms
		for (int i=0;i<termsToRemove.size();++i) {
			//trace.out("Asking to remove term at "+((Integer)(termsToRemove.elementAt(i))).intValue());
			this.removeTermAt(((Integer)(termsToRemove.elementAt(i))).intValue());
		}

		termsToRemove.removeAllElements();
		termsToRemove = null;

		return result;
	}

	public Expression getTermNoSign(int num) {
		return terms[num];
	}
	
	//getTermAt is the public version of random access to polynomial terms
	//This takes account of the sign (unlike getTermNoSign)
	public Expression getTermAt(int num) {
		Expression term = terms[num];
		if (signs[num] == NEGATIVE)
			term = term.negate();
		return term;
	}
	
	public int getSign(int num) {
		return signs[num];
	}
	
	public Queryable getProperty(String prop) throws NoSuchFieldException {
//		trace.out("in getProperty for Poly: "+prop);
		if (prop.equalsIgnoreCase("terms")) {
			Vector comp = new Vector();
			for (int i=0;i<numTerms;++i)
				comp.addElement(new TermInPoly(getTermAt(i),this,i+1));
			return new ArrayQuery(comp);
		}
		else if (prop.equalsIgnoreCase("variable terms")) {
			Vector found = new Vector();
			for (int i=0;i<numTerms;++i) {
				Expression thisEx = getTermAt(i);
				if (thisEx.variablesUsed().size() > 0)
					found.addElement(new TermInPoly(thisEx,this,i+1));
			}
			return new ArrayQuery(found);
		}
                /*target variable terms gets only those terms with the specified variable*/
                else if (prop.length() > 21 && prop.substring(0,21).equalsIgnoreCase("target variable terms")){
                    Vector found = new Vector();
                    String v = prop.substring(22);
                    for(int i=0;i<numTerms;i++){
                        Vector vars = getTermAt(i).variablesUsed();
                        for(int j=0;j<vars.size();j++){
                            if(((String)vars.elementAt(j)).equalsIgnoreCase(v)){
                                found.addElement(new TermInPoly(getTermAt(i),this,i+1));
                            }
                        }
                    }
                    return new ArrayQuery(found);
                }
		else if (prop.equalsIgnoreCase("constant terms")) {
			Vector found = new Vector();
			for (int i=0;i<numTerms;++i) {
				Expression thisEx = getTermAt(i);
				if (thisEx.variablesUsed().size() == 0)
					found.addElement(new TermInPoly(thisEx,this,i+1));
			}
			return new ArrayQuery(found);
		}
		//"term matching" returns the first term whose form matches the given form
		else if (prop.length() > 13 && prop.substring(0,13).equalsIgnoreCase("term matching")) {
			Expression found = null;
			try {
				Equation matchForm = Equation.makeForm(prop.substring(14));
				Vector comp = getFullComponents();
				for (int i=0;i<comp.size() && found==null;++i) {
					Expression thisEx = (Expression)(comp.elementAt(i));
					Equation thisExInfo = new Equation(thisEx,null);
					if (matchForm.patternMatches(thisExInfo))
						found = thisEx;
				}
				comp.removeAllElements();
				comp = null;
			}
			catch (BadExpressionError err) {
				trace.out("bad expression in term matching: "+prop.substring(14));
			}
			if (found == null)
				throw new NoSuchFieldException("No term matching "+prop.substring(14)+" in "+this);
			else
				return found;
		}
		//"operator n" is an expressionpart -- note that, in x^2+2x-3,  operator 1 is the plus sign
		//and operator 2 is the minus sign
		else if (prop.length() > 8 && prop.substring(0,8).equalsIgnoreCase("operator")) {
			int signNum = Integer.parseInt(prop.substring(9));
			if (signNum < numTerms) {
				int sign = signs[signNum];
	//			trace.out("sign "+signNum+" is "+sign);
				String signString;
				if (sign == NEGATIVE)
					signString = "-";
				else
					signString = "+";
				return new ExpressionPart(prop,this,signString);
			}
			else
				throw new NoSuchFieldException("No operator "+signNum+" in "+this);
		}
		//"term with degree n" returns an expression with the appropriate exponent
		//We need to combine like terms, so that we combine terms of equal degree
		//If the term found exists, we create a TermInPoly for it; otherwise, we create one
		else if (prop.length() > 16 && prop.substring(0,16).equalsIgnoreCase("term with degree")) {
			if (canCombineLikeTerms())
				return this.combineLikeTerms().getProperty(prop);
			else {
				int desiredDegree = Integer.parseInt(prop.substring(17));
				Expression foundTerm = null;
				int foundTermNumber=-1;
				for (int i=0;i<numTerms && foundTerm==null;++i) {
					Expression tryEx = getTermAt(i);
					if (tryEx.degree() == desiredDegree) {
						foundTerm = tryEx;
						foundTermNumber = i+1;
					}
				}
				if (foundTerm != null)
					return new TermInPoly(foundTerm,this,foundTermNumber);
				else //create 0x^exp
					return new TermExpression(new NumberExpression(0),
											  new ExponentExpression(new VariableExpression("X"),
															   new NumberExpression(desiredDegree)));
			}
		}
		//"term n" is a TermInPoly
		else if (prop.length() > 4 && prop.substring(0,4).equalsIgnoreCase("term")) {
			int termNum = Integer.parseInt(prop.substring(5));
			if (termNum <= numTerms)
				return new TermInPoly(getTermAt(termNum-1),this,termNum);
			else
				throw new NoSuchFieldException("No term "+termNum+" in "+this);
		}
		/*returns the terms that are unmodified after a CLT operation*/
		else if(prop.equalsIgnoreCase("uncombinable terms")){
			Expression combined = combineLikeTerms();
			Vector combTerms;
			if(combined instanceof PolyExpression){
				combTerms = ((PolyExpression)combined).getFullComponents();
			}
			else{
				combTerms = new Vector();
				combTerms.addElement(combined);
			}
			Vector myTerms = getFullComponents();
			Vector uncombTerms = new Vector();
			for(int i=0;i<myTerms.size();i++){
				for(int j=0;j<combTerms.size();j++){
					if(((Expression)myTerms.elementAt(i)).exactEqual((Expression)combTerms.elementAt(j))){
						uncombTerms.addElement(myTerms.elementAt(i));
						break;
					}
				}
			}
			combTerms.removeAllElements();
			combTerms = null;
			myTerms.removeAllElements();
			myTerms = null;
			if(uncombTerms.size() == 0){
				uncombTerms.removeAllElements();
				uncombTerms = null;
				throw new NoSuchFieldException("all terms of " + toString() + " can be combined");
			}
			else{
				PolyExpression ret = new PolyExpression(uncombTerms);
				uncombTerms.removeAllElements();
				uncombTerms = null;
				return ret;
			}
		}
		else
			return super.getProperty(prop);
	}
	
	//setProperty allows the operator of a term to be changed
	public void setProperty (String prop, String value) throws NoSuchFieldException {
//		trace.out("in setProperty of PolyExpression: "+prop+"::"+value);
		if (prop.length() > 8 && prop.substring(0,8).equalsIgnoreCase("operator")) {
			int signNum = Integer.parseInt(prop.substring(9));
			if (value.equals("+"))
				signs[signNum] = POSITIVE;
			else
				signs[signNum] = NEGATIVE;
		}
		else if (prop.length() > 4 && prop.substring(0,4).equalsIgnoreCase("term")) {
			int termNum = Integer.parseInt(prop.substring(5));
			//mmmBUG: does it matter that this never calls setMaintainVarList(true)?
			SymbolManipulator sm = new SymbolManipulator();
			sm.setMaintainVarList(Expression.getMaintainVars());
			try {
				Expression theExp = sm.parse(value);
				terms[termNum-1] = theExp;
			}
			catch (ParseException err) {
				System.out.println("Error trying to set term "+termNum+": "+err);
			}
		}
		else
			super.setProperty(prop,value);
	}
		
		
	public Expression combineLikeTermsWhole() {
		//trace.out("PE.cLTW: " + debugForm());
		Expression firstTerm;
		Vector termsToCombine;
		PolyExpression newExp;
		PolyExpression finalExp = new PolyExpression();
		
		newExp = this.flatten();
		while (newExp.numberOfTerms() > 0) {
			firstTerm = newExp.getTermAt(0);
			newExp.removeTermAt(0);
			termsToCombine = newExp.removeCombinableTerms(firstTerm);
			for (int i=0;i<termsToCombine.size();++i) {
				Expression thisEx = (Expression)(termsToCombine.elementAt(i));
				//trace.out("PE.cLTW: Combining "+thisEx.toString()+" with "+firstTerm.toString());
				firstTerm = firstTerm.addLikeTerms(thisEx);
			}
			if (!(firstTerm.isZero() ||
				  firstTerm.numericSimplifiedCoefficient().isZero()))
				finalExp.insertSimpleSign(firstTerm);
			//trace.out("PE.cLTW: inserted "+firstTerm.toString()+" final exp is now "+finalExp.toString());
			termsToCombine.removeAllElements();
			termsToCombine = null;
		}
		return finalExp.cleanExpression();
	}
	
	public boolean canCombineLikeTermsWhole() {
		boolean canCombine=false;
		for (int i=0;i<numTerms && !canCombine;++i) {
			//check if thisTerm can be combined with any others
			Expression thisTerm = getTermAt(i); //(Expression)(terms.elementAt(i));
			if (thisTerm.isZero() ||
				thisTerm.numericSimplifiedCoefficient().isZero())
				canCombine = true;
			else {
				for (int j=0;j<numTerms && !canCombine;++j) {
					if (i != j) {
						Expression otherTerm = getTermAt(j);  //(Expression)(terms.elementAt(j));
						if (thisTerm.isLike(otherTerm))
							canCombine = true;
					}
				}
			}
		}
		return canCombine;
	}
				
	/*For now we define this to remove double signs ... in the future
      we'll remove these methods and provide a separate menu item to
      remove double signs*/
	protected Expression multiplyThroughWhole(){
		return removeDoubleSigns();
	}

	protected boolean canMultiplyThroughWhole(){
		return canRemoveDoubleSigns();
	}

	//PolyExpressions defines standardizeWhole to sort the terms
	public Expression standardizeWhole(int type) {
            //trace.out("PolyExpression.standardizeWhole(): " + debugForm());
		Expression simpEx = this.simplify();
                //trace.out("PolyExpression.standardizeWhole(): ck 1");
		if (simpEx instanceof PolyExpression) {
			PolyExpression pEx = (PolyExpression)simpEx;
                        //trace.out("PolyExpression.standardizeWhole(): ck 2");
			return pEx.sortPoly();
		}
		else{
                    //trace.out("PolyExpression.standardizeWhole(): ck 3");
			return simpEx.standardize(type);
                }
	}
	
//	public Vector variablesUsed() {
//		Vector vars = new Vector();
//		Vector subvars;
//		for (int i=0;i<numberOfTerms();++i) {
//			Expression subterm = getTermNoSign(i);
//			subvars = subterm.variablesUsed();
//			for (int subitem=0;subitem<subvars.size();++subitem) {
//				String thisItem = (String)subvars.elementAt(subitem);
//				boolean found=vars.contains(thisItem);
//				if (!found)
//					vars.addElement(thisItem);
//			}
//		}
//		return vars;
//	}

	/*when checking for similarity between two PolyExpressions, we
      need to disregard whether the sign is in the term or in the
      polynomial.  this is important for cases like
      similar(x+-4*3,x-4*3) (which should be true).*/
	public boolean similar(Expression ex){
		if(!(ex instanceof PolyExpression)){
			return super.similar(ex);
		}
		boolean sim = true;
		if(algebraicEqual(ex)){
			Vector myComps = removeDoubleSigns().sort().getFullComponents();
			Vector otherComps = ex.removeDoubleSigns().sort().getFullComponents();
			if(myComps.size() == otherComps.size()){
				for(int i=0;i<myComps.size() && sim;i++){
					Expression myPart = (Expression)(myComps.elementAt(i));
					Expression otherPart = (Expression)(otherComps.elementAt(i));
					if(!myPart.similar(otherPart)){
						sim = false;
					}
				}
			}
			else{
				sim = false;
			}
			myComps.removeAllElements();
			myComps = null;
			otherComps.removeAllElements();
			otherComps = null;
		}
		else{
			sim = false;
		}

		return sim;
	}

	//mmmBUG: should implement this at some point
	public String whyNotSimilar(Expression ex){
		trace.out("PE.wNS: warning: not implemented");
		return super.whyNotSimilar(ex);
	}

	public boolean exactEqual(Expression ex) {
		if (ex instanceof PolyExpression) {
			PolyExpression pEx = (PolyExpression)ex;
			if (numberOfTerms() == pEx.numberOfTerms()) {
				boolean same = true;
				for (int i=0;i<numberOfTerms()&&same==true;++i) {
					if (!getTermAt(i).exactEqual(pEx.getTermAt(i)))
						same = false;
				}
				return same;
			}
			else
				return false;
		}
		else
			return false;
	}
		
	//polyExpressions sort after everything except other PolyExpressions
	//when comparing with other polyExpressions, we only consider the first term
	public boolean termSortBefore(Expression ex) {
		if (ex instanceof PolyExpression) {
			PolyExpression pEx = (PolyExpression)ex;
			return getTermNoSign(0).termSortBefore(pEx.getTermNoSign(0));
		}
		else
			return false;
	}
	
	protected Expression sortPolyWhole(){
		//used to be sortPolyWhole(false), but that was generating odd patterns
		Expression ret;
		/*trace.out("sortPolyWhole(): start level " + level);
		  level++;*/
		ret = sortPolyWhole(true);
		/*level--;
              trace.out("sortPolyWhole():  end  level " + level);*/
		return ret;
	}

	//sortPolyWhole sorts a polynomial
	//Polynomials sort in the opposite direction of "termSortBefore",
	//which gives the sort order within terms
	//(e.g. 3 "sorts before" x, so we get 3x, but in a polynomial, we want x+3)
	protected Expression sortPolyWhole(boolean sepsigns) {
		Vector outterms = new Vector();
		Vector outsigns = new Vector();
		Expression temp;
		/*trace.out(debugForm() + ".sortPolyWhole(): begin");
		  trace.out("      sPW: processing term " + getTermAt(0).debugForm());*/
		if(sepsigns){
			if(getTermNoSign(0).isNegative() &&
			   getSign(0) == POSITIVE){
				if(getTermNoSign(0) instanceof RatioExpression &&
				   !((RatioExpression)getTermNoSign(0)).numerator().isNegative()){
					/*special handling of expressions like x/-y,
                      because negating that gives -x/-y, not x/y*/
					//trace.out("PE.sPW: not negating initial ratio w/ positive numerator: " + getTermNoSign(0).debugForm());
					outterms.addElement(getTermNoSign(0));
					outsigns.addElement(new Integer(getSign(0)));
				}
				else{
					outterms.addElement(getTermNoSign(0).negate());
					/*trace.out("PE.sPW: negating first term: " + getTermNoSign(0).debugForm()
					  + " --> " + getTermNoSign(0).negate().debugForm());*/
					outsigns.addElement(new Integer(NEGATIVE));
				}
			}
			else{
				outterms.addElement(getTermNoSign(0));
				outsigns.addElement(new Integer(getSign(0)));
			}
		}
		else{
			outterms.addElement(getTermAt(0));
		}
		//trace.out("           inserted at: " + 0);
		for (int i=1;i<numberOfTerms();++i) {
			boolean inserted=false;
			temp = (Expression)(getTermAt(i));
			if(sepsigns){
				temp = (Expression)(getTermNoSign(i));
			}
			/*trace.out("      sPW: processing term " + temp.debugForm());
			  if(sepsigns) trace.out("           which has the sign: " + getSign(i));*/
			for (int j=0;j<outterms.size() && !inserted;++j) {
				/*trace.out("           comparing to term:  " + outterms.elementAt(j));
				  if(sepsigns) trace.out("           which has the sign: " + outsigns.elementAt(j));*/
				/*if (sepsigns ? 
				  (temp.polySortBefore(tempj)) :
				  (temp.polySortBefore((Expression)(outterms.elementAt(j))))){*/
				if(temp.polySortBefore((Expression)(outterms.elementAt(j)))){
					if(sepsigns){
						/*if the terms are equal, polySortBefore is
                          true.  For equal terms, we sort the positive
                          ones before the negative ones: x-x, not
                          -x+x*/
						/*trace.out("           equality check: " + temp.debugForm());
						  trace.out("                           " + ((Expression)outterms.elementAt(j)).debugForm());*/
						if(temp.exactEqual((Expression)outterms.elementAt(j))){
							//trace.out("           equal: checking signs");
							if(getSign(i) == POSITIVE ||
							   ((Integer)outsigns.elementAt(j)).intValue() == NEGATIVE){
								outterms.insertElementAt(getTermNoSign(i),j);
								outsigns.insertElementAt(new Integer(getSign(i)),j);
								inserted=true;
								//trace.out("           inserted at: " + j);
							}
						}
						else{
							outterms.insertElementAt(getTermNoSign(i),j);
							outsigns.insertElementAt(new Integer(getSign(i)),j);
							inserted=true;
							//trace.out("           inserted at: " + j);
						}
					}
					else{
						outterms.insertElementAt(getTermAt(i),j);
						inserted=true;
						//trace.out("           inserted at: " + j);
					}
				}
			}
			if (!inserted){
				if(sepsigns){
					outterms.addElement(getTermNoSign(i));
					outsigns.addElement(new Integer(getSign(i)));
				}
				else{
					outterms.addElement(getTermAt(i));
				}
				//trace.out("           inserted at (fallthru): " + i);
			}
		}

		PolyExpression ret;
		if(sepsigns){
			/*PolyExpression ret = new PolyExpression(outterms,outsigns);
			  trace.out("      PE.sPW: returning: " + ret.debugForm());*/
			ret = new PolyExpression(outterms,outsigns);
		}
		else{
			ret = new PolyExpression(outterms); //hmm, this will remove double-negatives
			// -- and re-structure expressions like "a-x=b"
		}

		outterms.removeAllElements();
		outterms = null;
		outsigns.removeAllElements();
		outsigns = null;

		//trace.out("      PE.sPW: returning: " + ret.debugForm());
		return ret;
	}
	
	//negating a polynomial is different from multiplying it by -1, since we negate each term
	public Expression negate() {
		Vector outterms = new Vector();
		for (int i=0;i<numberOfTerms();++i) {
			Expression thisExp = getTermAt(i);
			outterms.addElement(thisExp.negate());
		}
		return new PolyExpression(outterms);
	}

	/*this takes care of negated sub-expressions that are polynomials
      (e.g. 1-(2+3) --> 1-2-3); other distributions are taken care of
      generically by the Expression class*/
	public Expression distributeWhole(int type){
		if((type & DISTNUM) != 0){
			Vector dist = new Vector(numTerms);
			int[] newSigns = new int[numTerms];
			for(int i=0;i<numTerms;i++){
				if(getSign(i) == NEGATIVE &&
				   getTermNoSign(i) instanceof PolyExpression){
					dist.addElement(getTermAt(i).distribute(type));
					newSigns[i] = POSITIVE;
				}
				else{
					dist.addElement(getTermNoSign(i));
					newSigns[i] = getSign(i);
				}
			}
			Expression ret = (new PolyExpression(dist,newSigns)).cleanExpression();
			dist.removeAllElements();
			dist = null;
			return ret;
		}
		else{
			return super.distributeWhole(type);
		}
	}

	protected boolean canDistributeWhole(int type){
		if((type & DISTNUM) != 0){
			boolean ret = false;
			for(int i=0;i<numTerms && !ret;i++){
				ret = getSign(i) == NEGATIVE &&
					getTermNoSign(i) instanceof PolyExpression;
			}
			return ret;
		}
		else{
			return false;
		}
	}

//	public Expression substitute(String var,Expression newVal) {
//		trace.out("substituting in "+toString());
//		PolyExpression outExpression = new PolyExpression();
//		for (int i=0;i<numberOfTerms();++i) {
//			Expression thisExp = getTermAt(i);
//			Expression subEx = thisExp.substitute(var,newVal);
//			trace.out("  "+thisExp.toString()+" becomes "+subEx.toString());
//			outExpression.insertSimpleSign(subEx);
//		}
//		trace.out("after substitution: "+outExpression.toString());
//		return outExpression;
//	}
	
	//factor pulls the given expression out of each term in the PolyExpression
	public Expression factor(Expression fact) {
		//trace.out("PolyExpression: factor(): " + debugForm());
		PolyExpression outExpression = new PolyExpression();
		for (int i=0;i<numberOfTerms();++i) {
			Expression thisTerm = getTermAt(i);
			Expression newTerm = thisTerm.divide(fact).reduceFractions().multiplyThrough();
			outExpression.insertSimpleSign(newTerm);
		}
		//trace.out("PolyExpression: factor(): returning " + fact.debugForm() + " * " + outExpression.debugForm());
		return fact.multiply(outExpression);
	}
	
	public Expression factorPiecemeal(Expression fact){
		//trace.out("PolyExpression: factorPM(" + fact.debugForm() + "): " + debugForm());
		PolyExpression factoredTerms = new PolyExpression();
		PolyExpression otherTerms = new PolyExpression();
		int oTcount = 0;
		for(int i=0;i<numberOfTerms();i++){
			Expression thisTerm = getTermAt(i);
			Expression newTerm = thisTerm.divide(fact).reduceFractions().multiplyThrough();
			if(newTerm instanceof RatioExpression ||
			   newTerm instanceof FractionExpression){
				otherTerms.insertSimpleSign(thisTerm);
				oTcount++;
			}
			else{
				factoredTerms.insertSimpleSign(newTerm);
			}
		}
		if(oTcount > 0){
			Expression rest = otherTerms.cleanExpression();
			return fact.multiply(factoredTerms).add(rest).cleanExpression();
		}
		else{
			return fact.multiply(factoredTerms);
		}
	}

	public boolean canFactorPiecemeal(Expression fact){
		//trace.out("PolyExpression: canFactorPM(" + fact.debugForm() + "): " + debugForm());
		boolean canFactor = false;
		for(int i=0;i<numberOfTerms() && !canFactor;i++){
			Expression thisTerm = getTermAt(i);
			Expression newTerm = thisTerm.divide(fact).reduceFractions().multiplyThrough();
			if(!(newTerm instanceof RatioExpression ||
				 newTerm instanceof FractionExpression)){
				canFactor = true;
			}
		}
		return canFactor;
	}

	//factorNumeric factors a number out of a polynomial
	public Expression factorNumeric() {
		Expression result = this;
		long coeffs[];
		coeffs = new long[numTerms];
		boolean factorOK=true;
		boolean allNegative = true;
		for (int i=0;i<numTerms&&factorOK;++i) {
			Expression thisTerm = (Expression)getTermAt(i);
			NumericExpression thisCoeff = thisTerm.numericUnsimplifiedCoefficient();
			if (thisCoeff.isIntegerType() && !thisCoeff.isOne()) {
				coeffs[i] = ((NumericExpression)thisCoeff).getValue().intValue();
				if (coeffs[i]>0)
					allNegative=false;
			}
			else
				factorOK = false;
		}
		if (factorOK) {
			long gcf = FractionExpression.gcf(coeffs);
			/*have to check here to prevent div by zero in
			  sm.subtract("(a+b)/c","(a+b)/c")*/
			if (gcf != 0 && gcf != 1) {
				//GCF is always positive, but if all numbers are negative, we want to factor out a negative number
				if (allNegative)
					gcf = -gcf;
				Vector newTerms = new Vector();
				NumberExpression gcfNum = new NumberExpression(gcf);
				for (int i=0;i<numTerms;++i) {
					newTerms.addElement(((Expression)getTermAt(i)).divide(gcfNum).reduceFractions().cleanExpression()); //should really only reduce numbers
				}
				result = new TermExpression(gcfNum,new PolyExpression(newTerms));
				newTerms.removeAllElements();
				newTerms = null;
			}
		}
		coeffs = null;
		return result;
	}
	
	//getCommonFactor returns a factor in common between the two expressions
	//It returns null if there is no such factor
	private Expression getCommonFactor(Expression first, Expression second) {
		Expression ret = null;
		Vector firstExp = first.getExpandedForm();
		Vector secondExp = second.getExpandedForm();
		
//		trace.out("expanded form of "+first+" is "+firstExp);
//		trace.out("expanded form of "+second+" is "+secondExp);
		Vector commonTerms = new Vector();
		for (int i=0;i<firstExp.size();++i) {
			boolean foundMatch = false;
			for (int j=0;j<secondExp.size()&&!foundMatch;++j) {
				Expression firstPart = (Expression)(firstExp.elementAt(i));
				Expression secondPart = (Expression)(secondExp.elementAt(j));
				if (firstPart.algebraicEqual(secondPart)) {
					commonTerms.addElement(firstPart);
					foundMatch = true;
					secondExp.removeElementAt(j);
				}
			}
		}
		if (commonTerms.size() > 0) {
			Expression commonTerm = new TermExpression(commonTerms).simplify();
//			trace.out("common term between "+first+" and "+second+" is "+commonTerm);
			ret = commonTerm;
		}

		firstExp.removeAllElements();
		firstExp = null;
		secondExp.removeAllElements();
		secondExp = null;
		commonTerms.removeAllElements();
		commonTerms = null;

		return ret;
	}
	
	public Expression factor() {
		Expression ret;
		PolyExpression toFactor = this;
		Vector factorsFound = new Vector();
		Vector possibleFactors = new Vector();
		Expression numberFound = null;

		//First, check to see if we can factor out a number
		Expression numberFirst = factorNumeric();
		if (numberFirst instanceof TermExpression) { //we were able to factor out a number
			TermExpression numberFactor = (TermExpression)numberFirst;
			numberFound = numberFactor.getTerm(0);
			toFactor = (PolyExpression)(numberFactor.getTerm(1));
		}
				
		//pull out the first term of the polynomial
		Expression firstTerm = toFactor.getTermAt(0).unfence();
		if (firstTerm instanceof TermExpression)
			possibleFactors = firstTerm.getFullComponents();
		else
			possibleFactors.addElement(firstTerm);

		//now, look for matches in the other terms of the polynomial
		for (int i=0;i<possibleFactors.size();++i) {
			boolean factorFound=true;
			Expression thisFactor = (Expression)(possibleFactors.elementAt(i));
			Expression negativeFactor = thisFactor.negate();
			for (int j=1;j<toFactor.numberOfTerms()&&factorFound;++j) {
				Expression matchTerm = toFactor.getTermAt(j).unfence();
				Expression commonFactor = getCommonFactor(thisFactor,matchTerm);
				if (commonFactor != null)
					thisFactor = commonFactor;
				else
					factorFound=false;
			}
			if (factorFound)
				factorsFound.addElement(thisFactor);
		}
		if (factorsFound.size() > 0) {
			//finally, reconstruct the polynomial and factors
			PolyExpression polyLeft = new PolyExpression();
			TermExpression factorExpression = new TermExpression(factorsFound);
			for (int i=0;i<toFactor.numberOfTerms();++i) {
				Expression thisTerm = toFactor.getTermAt(i);
				Expression newTerm = thisTerm.divide(factorExpression).multiplyThrough().reduceFractions();
				polyLeft.insertSimpleSign(newTerm);
			}
			factorsFound.addElement(polyLeft);
			if (numberFound != null)
				factorsFound.insertElementAt(numberFound,0); //number is already factored out of other terms
			ret = new TermExpression(factorsFound);
		}
		//factored number but nothing else...
		else if (numberFound != null)
			ret = numberFirst;
		else
			ret = this;

		factorsFound.removeAllElements();
		factorsFound = null;
		possibleFactors.removeAllElements();
		possibleFactors = null;
		return ret;
	}

	public boolean canFactor() {
		Expression factored = factor();
		if (factored instanceof TermExpression)
			return true;
		else
			return false;
	}			

	/*technically we can pull any factor out of any expression, but
	  here we want to know if pulling 'fact' out will result in a
	  "simpler" expression.  For example, if we have 3x+4x and we ask
	  canFactor(3), the answer is false, even though we could do
	  factor(3) and get 3(x+4x/3).*/
	public boolean canFactor(Expression fact){
		//Expression f = factor();
		//trace.out("PE.cF: " + debugForm());
		//trace.out("       " + f.debugForm());
		//trace.out("PE.cF: " + fact.debugForm());
		for(int i=0;i<numTerms;i++){
			Expression div = getTermAt(i).divide(fact).simplify();
			//trace.out("PE.cF: div: " + div.debugForm());
			if(div instanceof RatioExpression ||
			   div instanceof FractionExpression){
				return false;
			}
		}

		return true;
	}
	
	//factorQuadratic produces the factors for a quadratic
	//This returns the original expression if the given expression is not a quadratic, or if
	//the roots are not integers (though it should probably throw an error in such cases)
	//We assume that there is only 1 variable
	public Expression factorQuadratic() {
		Expression simpEx = standardize(DISTBOTH);
		if (simpEx instanceof PolyExpression) {
			PolyExpression pEx = (PolyExpression)simpEx;
			if (pEx.numberOfTerms() > 1) {
				Expression quadraticTerm = pEx.getTermAt(0);
				Expression linearTerm;
				Expression constantTerm;
				if (quadraticTerm.degree() == 2.0) {
					if (pEx.numberOfTerms() == 3 && pEx.getTermAt(1).degree() == 1.0) {
						linearTerm = pEx.getTermAt(1);
						constantTerm = pEx.getTermAt(2);
					}
					else if (pEx.getTermAt(1).degree() == 1.0) { //linear term but 0 constant
						linearTerm = pEx.getTermAt(1);
						constantTerm = new NumberExpression(0);
					}
					else { //constant but no linear term
						linearTerm = new NumberExpression(0);
						constantTerm = pEx.getTermAt(1);
					}
					double a = quadraticTerm.numericSimplifiedCoefficient().doubleValue();
					double b = linearTerm.numericSimplifiedCoefficient().doubleValue();
					double c = constantTerm.numericSimplifiedCoefficient().doubleValue();
					double dTerm = Math.sqrt(Math.pow(b,2.0)-4.0*a*c);
					double posRoot = (-b+dTerm)/(2*a);
					double negRoot = (-b-dTerm)/(2*a);
					NumberExpression posRootNum = new NumberExpression(-posRoot);
					NumberExpression negRootNum = new NumberExpression(-negRoot);
					String theVar = (String)(quadraticTerm.variablesUsed().elementAt(0));
					VariableExpression vEx = new VariableExpression(theVar);
					//before finishing, we remove double signs and combine like terms (CLT eliminates X+0)
					Expression posRootEx = new PolyExpression(vEx,posRootNum,POSITIVE).removeDoubleSigns().combineLikeTerms();
					Expression negRootEx = new PolyExpression(vEx,negRootNum,POSITIVE).removeDoubleSigns().combineLikeTerms();
					return new TermExpression(posRootEx,negRootEx);
				}
				else //no quadratic term
					return this;
			}
			else //only 1 term (or no terms)
				return this;
		}
		else //simplified expression is not a PolyExpression
			return this;
	}
					
	
	public double degree() {
		double highDegree = -99999.0;
		
		for (int i=0;i<numberOfTerms();++i) {
			if (getTermNoSign(i).degree() > highDegree)
				highDegree = getTermNoSign(i).degree();
		}
		return highDegree;
	}
	
	public String toASCII(String openParen, String closeParen) {
		if (Expression.printStruct)
			return debugForm();

		StringBuffer finalString = new StringBuffer(asciiSBsize);
		for (int i=0;i<numTerms;++i) {
			Expression exp1 = terms[i];
			if (exp1 != null) {
				if (i>0) {
					if (signs[i] == NEGATIVE)
						finalString.append("-");
					else
						finalString.append("+");
				}
				else if (signs[i] == NEGATIVE)
					finalString.append("-");
				boolean closeit = false;
				if (exp1 instanceof PolyExpression ||
					(finalString.length() > 0 && exp1.isNegative() && !(exp1 instanceof FencedExpression))){
					finalString.append(openParen);
					closeit = true;
				}
				finalString.append(exp1.toASCII(openParen,closeParen));
				if(closeit){
					finalString.append(closeParen);
				}
			}
			else
				finalString.append("NULL");
		}

		//trace.out(debugForm() + ".toASCII(): " + finalString);
		return finalString.toString();
	}
	
	public String toMathML() {
		StringBuffer finalString = new StringBuffer(mathmlSBsize);
		for (int i=0;i<numTerms;++i) {
			Expression exp1 = terms[i];
			if (exp1 != null) {
				if (i>0) {
					if (signs[i] == NEGATIVE)
						finalString.append(addMathMLPartAttributes("operator "+i,"<mo form='infix'>-</mo>"));
					else
						finalString.append(addMathMLPartAttributes("operator "+i,"<mo>+</mo>"));
				}
				else if (signs[i] == NEGATIVE) //first sign is negative -- this isn't allowed but, just in case...
					finalString.append(addMathMLPartAttributes("operator 0","<mo form='prefix'>-</mo>"));
				boolean closeit = false;
				/*parens go around negative expressions and around
                  subtracted polynomials*/
				if(finalString.length() > 0 && ((exp1.isNegative() && 
												 !(exp1 instanceof FencedExpression)) ||
												((signs[i] == NEGATIVE) &&
												 exp1 instanceof PolyExpression))){
					finalString.append("<mfenced>");
					closeit = true;
				}
				finalString.append(exp1.toMathML());
				if(closeit){
					finalString.append("</mfenced>");
				}
			}
			else
				finalString.append("NULL");
		}
		return addMathMLAttributes(finalString.insert(0,"<mrow>").append("</mrow>").toString());
	}

	
	public String debugForm() {
		StringBuffer finalString = new StringBuffer(asciiSBsize);
		finalString.append("[Poly(").append(numberOfTerms()).append("): ");
		for (int i=0;i<numberOfTerms();++i) {
			Expression exp1 = getTermNoSign(i);
			String signString;
			if (getSign(i) == 0)
				signString = "-";
			else
				signString = "+";
			finalString.append(" ").append((i+1)).append(":").append("{");
			finalString.append(signString).append("}").append(exp1.debugForm());
		}
		finalString.append("]");
		return finalString.toString();
	}

	//cleanExpression deals with PolyExpressions having 0 or 1 subterms
	//It also flattens in any nested polynomials
	//and makes sure that the first sign in the polynomial is positive
	public Expression cleanExpression() {
		Expression ret;
		Vector outterms = new Vector();
		Vector outsigns = new Vector();
		for (int i=0;i<numberOfTerms();++i) {
			Expression clean = getTermNoSign(i).cleanExpression();
			int sign = getSign(i);
			if (!clean.isEmpty()) {
				if (clean instanceof PolyExpression && sign == POSITIVE) {
					PolyExpression polyClean = (PolyExpression)clean;
					for (int j=0;j<polyClean.numberOfTerms();++j) {
						Expression subterm = polyClean.getTermNoSign(j);
						int cleanSign = polyClean.getSign(j);
						outterms.addElement(subterm);
						outsigns.addElement(new Integer(cleanSign));
					}
				}
				//SR - don't "clean" zeroes
				else { //if (!clean.isZeroSimplified()) {
					outterms.addElement(clean);
					outsigns.addElement(new Integer(sign));
				}
			}
		}
		if (outterms.size() == 1 && ((Integer)(outsigns.elementAt(0))).intValue() == NEGATIVE) {
			outsigns.setElementAt(new Integer(POSITIVE),0);
			outterms.setElementAt(((Expression)(outterms.elementAt(0))).negate(),0);
		}
		if (outterms.size() == 1)
			ret = (Expression)(outterms.elementAt(0));
		else if (outterms.size() == 0)
			ret = new NumberExpression(0);
		else
			ret = new PolyExpression(outterms,outsigns);

		outterms.removeAllElements();
		outterms = null;
		outsigns.removeAllElements();
		outsigns = null;

		return ret;
	}
	
	//flatten removes any nested PolyExpressions
	private PolyExpression flatten() {
		Vector outterms = new Vector();
		Vector outsigns = new Vector();
		for (int i=0;i<numberOfTerms();++i) {
			if (getTermNoSign(i) instanceof PolyExpression && getSign(i) == POSITIVE) {
				PolyExpression inside = ((PolyExpression)(getTermNoSign(i))).flatten();
				for (int j=0;j<inside.numberOfTerms();++j) {
					Expression inTerm = inside.getTermNoSign(j);
					int sign = inside.getSign(j);
					outterms.addElement(inTerm);
					outsigns.addElement(new Integer(sign));
				}
			}
			else {
				outterms.addElement(getTermNoSign(i));
				outsigns.addElement(new Integer(getSign(i)));
			}
		}
		PolyExpression ret = new PolyExpression(outterms,outsigns);

		outterms.removeAllElements();
		outterms = null;
		outsigns.removeAllElements();
		outsigns = null;

		return ret;
	}
	
	//flattenOne removes immediately nested PolyExpressions
	private PolyExpression flattenOne() {
		Vector outterms = new Vector();
		Vector outsigns = new Vector();
		for (int i=0;i<numberOfTerms();++i) {
			if (getTermNoSign(i) instanceof PolyExpression) {
				int outerSign = getSign(i);
				PolyExpression inside = (PolyExpression)(getTermNoSign(i));
				for (int j=0;j<inside.numberOfTerms();++j) {
					Expression inTerm = inside.getTermNoSign(j);
					if (outerSign == NEGATIVE)
						inTerm = inTerm.negate();
					int inSign = inside.getSign(j);
					outterms.addElement(inTerm);
					outsigns.addElement(new Integer(inSign));
				}
			}
			else {
				outterms.addElement(getTermNoSign(i));
				outsigns.addElement(new Integer(getSign(i)));
			}
		}
		PolyExpression ret = new PolyExpression(outterms,outsigns);

		outterms.removeAllElements();
		outterms = null;
		outsigns.removeAllElements();
		outsigns = null;

		return ret;
	}
	
	
	//nested polynomials [3+(4+5)] always have extranneous parens, so we remove them here...
	public boolean canRemoveParensWhole() {
		boolean canRem = false;
		for (int i=0;i<numberOfTerms()&&!canRem;++i) {
			if (getTermNoSign(i) instanceof PolyExpression)
				canRem=true;
		}
		return canRem;
	}
	
	public Expression removeParensWhole() {
		return flattenOne();
	}
	
	public Expression removeDoubleSignsWhole() {
		//getTermAt removes the signs
		Vector outterms = new Vector();
		for (int i=0;i<numberOfTerms();++i) {
			outterms.addElement(getTermAt(i));
		}
		PolyExpression pEx = new PolyExpression(outterms);
		outterms.removeAllElements();
		outterms = null;
		return pEx;
	}
	
	public boolean canRemoveDoubleSignsWhole() {
		boolean dub = false;
		for (int i=0;i<numberOfTerms() && !dub;++i) {
			if (signs[i]==NEGATIVE && getTermNoSign(i).isNegative())
				dub = true;
			else if (i>0 && getTermNoSign(i).isNegative()) //first sign is always positive, so a negative first is OK
				dub = true;
		}
		return dub;
	}
					
	public boolean isEmpty() {
		if (numberOfTerms() == 0)
			return true;
		else
			return false;
	}
}
