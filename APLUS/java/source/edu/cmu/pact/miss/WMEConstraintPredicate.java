package edu.cmu.pact.miss;

import java.util.Vector;

import jess.Context;
import jess.Fact;
import jess.JessException;
import jess.Rete;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
/**
 * Abstract class representing a topological constr 
 * @author ajzana
 *
 */
public abstract class WMEConstraintPredicate extends FeaturePredicate implements Userfunction {
/**
 * apply this predicate to args, using the working memory of rete
 * @param args
 * @param rete
 * @return "T" if this predicate holds, false otherwise
 */
public String apply(Fact[] args,Rete rete)
{
	Vector v=new Vector();
	for(int i=0; i<args.length; i++)
		v.add(args[i]);
		
	
	return apply(v,rete);
	
}

/**
 * calls this WMEConstraintPredicate on jessArgs which will be treated as jess.Facts
 * the first argument is ignored because jess passes the funcntion name as the first argument 
 */
public Value call( ValueVector jessArgs, Context c ) throws JessException {

	Vector argv = new Vector();
	for (int i = 1; i < jessArgs.size(); i++) {
	    argv.add( jessArgs.get(i).factValue(c) );
	}

	return FeaturePredicate.applyReturnToValue(apply(argv,c.getEngine()));

	 }
/**
 * apply this predicate to args, using the working memory of rete
 * @param args
 * @param rete
 * @return "T" if this predicate holds, false otherwise
 */
public abstract String apply(Vector args,Rete rete);

public String apply(Vector args)
{
	throw new UnsupportedOperationException();
}


}
