package edu.cmu.pact.miss.userDef.algebra;
import java.util.Vector;
public class NotNull extends EqFeaturePredicate 
{
    /**
     * Creates a new <code>NotNull</code> instance.
     *
     */
    public NotNull() {

	setName( "not-null" );
	setArity( 1 );
	setArgValueType(new int[]{TYPE_ARITH_EXP});
	setFeatureDescription(getName(),"is not null/empty/blank");

    }

    public String apply( Vector /* String */ args ) 
    {
    
    return !(((String)args.get(0)).equals("") || ((String) args.get(0)).contains("nil")) ? "T" : null;
    }
}
