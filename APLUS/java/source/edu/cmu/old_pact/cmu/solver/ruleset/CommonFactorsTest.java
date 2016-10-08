package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.BadExpressionError;
import edu.cmu.old_pact.cmu.sm.SymbolManipulator;
import edu.cmu.old_pact.cmu.sm.query.ArrayQuery;
import edu.cmu.old_pact.cmu.sm.query.Queryable;

/*this tests whether any of the expressions in 'terms' has a factor in
  common with any of the other expressions.*/

public class CommonFactorsTest extends Test{
	boolean enforceOrder; //if true, {2,2*3} doesn't pass (this is the default behaviour)
	static SymbolManipulator sm = new SymbolManipulator();

	static{
		sm.setMaintainVarList(true);
	}

	public CommonFactorsTest(String[] terms,boolean enforceOrderVal){
		propertyString = terms;
		enforceOrder = enforceOrderVal;
	}

	public CommonFactorsTest(String[] terms){
		this(terms,true);
	}

	public CommonFactorsTest(String terms,boolean enforceOrderVal){
		this(new String[] {terms},enforceOrderVal);
	}

	public CommonFactorsTest(String terms){
		this(new String[] {terms},true);
	}

	/*for each factor of each term, check for an exactequal factor in
      any of the other terms*/
	public boolean passes(Queryable info){
		try{
			Queryable terms = info.evalQuery(propertyString);
			if(terms instanceof ArrayQuery){
				boolean ret = false;
				Queryable[] termarr = terms.getArrayValue();
				for(int i=0;i<termarr.length && !ret;i++){
					Queryable thisterm = termarr[i];
					int thisfactorcnt = thisterm.evalQuery("length of factors").getNumberValue().intValue();
					for(int j=(enforceOrder ? (thisfactorcnt-1) : 0);j<thisfactorcnt && !ret;j++){
						Queryable thisfact = thisterm.evalQuery("item " + (j+1) + " of factors");
						for(int k=i+1;k<termarr.length && !ret;k++){
							Queryable thatterm = termarr[k];
							int thatfactorcnt = thatterm.evalQuery("length of factors").getNumberValue().intValue();
							for(int l=(enforceOrder ? (thatfactorcnt-1) : 0);l<thatfactorcnt && !ret;l++){
								ret = sm.exactEqual(thisfact.getStringValue(),
													thatterm.evalQuery("item " + (l+1) + " of factors").getStringValue());
							}
						}
					}
				}
				return ret;
			}
			else{
				return false;
			}
		}
		catch(NoSuchFieldException err){
			if(Rule.debug()){
				System.out.println("Error resolving test:"+err+" info = "+info.getStringValue()+" class = "+getClass());
			}
			return false;
		}
		catch(BadExpressionError err){
			if(Rule.debug()){
				System.out.println("Error resolving test:"+err+" info = "+info.getStringValue()+" class = "+getClass());
			}
			return false;
		}
	}

	public String toString(){
		return "[CommonFactorsTest: \"" + ofString(propertyString) + "\" (enforceOrder: " + enforceOrder + ")]";
	}
}
