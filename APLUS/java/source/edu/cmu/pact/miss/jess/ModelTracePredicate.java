package edu.cmu.pact.miss.jess;

import java.util.Vector;

import jess.Context;
import jess.JessException;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.miss.FeaturePredicate;

/**
 * 
 */
public abstract class ModelTracePredicate extends FeaturePredicate {

	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		Vector argv = new Vector();
		for(int i=1; i < vv.size(); i++) {
			argv.add(vv.get(i).factValue(context));
		}
		return FeaturePredicate.applyReturnToValue(apply(argv, context));
	}
	
	public String apply(Vector argv) {
		throw new UnsupportedOperationException("Method not implemented yet");
	}
	
	public abstract String apply(Vector argv, Context context);
}
