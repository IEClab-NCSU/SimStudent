/**
 * $RCSfile$
 *
 * For a pair of variable and expression that corresponds to a (bind
 * ?var (expression ...)) statement in RHS.
 *
 *
 * Created: Tue Jan 04 16:03:34 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version $Id: BindPair.java 10599 2010-01-04 21:55:19Z keiser $
 */

package edu.cmu.pact.miss;
public class BindPair {

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    private String var = null;
    String getVar() { return this.var; }
    void setVar( String var ) { this.var = var; }

    private FeaturePredicate exp = null;
    FeaturePredicate getExp() { return this.exp; }
    void setExp( FeaturePredicate exp ) { this.exp = exp; }
    
    // Type of the "var".  If this is for a seed (a.k.a., focus of attention), 
    // then its type is determined by the seed value, otherwise, 
    // the exp (FeaturePredicate) knows its type
    private int argType = -1;
    void setArgType(int argType) { this.argType = argType; }
    int getArgType() { 
    	if(argType == -1 && getExp() == null)
    		return argType;
	return argType != -1 ? this.argType : getExp().getReturnValueType();
    }
    
    
    // -
    // - Constructor - - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    //G: this constructor does not set the arg-type. When getArgType is called, it will look
    //return getExp().getReturnValueType() .
    public BindPair( String var, FeaturePredicate exp ) {
	setVar( var );
	setExp( exp );
    }
    
    //G: this constructor sets the arg-type. When getArgType is called, it will return this value.
    public BindPair( String var, int argType ) {
	setVar( var );
	setArgType(argType);
    }
    

    // -
    // - Methods - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    public String toString() {
	
	return "<BindPair " + var + " " + exp + " " +
	argType + ">" ; //Gustavo 1Nov2006 : returning the arg-type
    }
}

//
// end of $RCSfile$
// 
