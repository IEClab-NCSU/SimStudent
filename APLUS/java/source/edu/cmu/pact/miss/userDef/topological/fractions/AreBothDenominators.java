package edu.cmu.pact.miss.userDef.topological.fractions;

import java.util.Vector;

import jess.Fact;
import jess.JessException;
import jess.Rete;

/*this needs to be a constraint and not a feature predicate because we also want to extract the position of the cells*/
public class AreBothDenominators extends FractionConstraint {

	public AreBothDenominators() {
		setArity(2);
		setName("are-both-denominators");
	}

	public String apply(Vector args, Rete rete) {

		try
		{

			return (areBothDenominators((Fact)args.get(0),(Fact)args.get(1), rete) ? "T" : null);
		}
		catch (JessException e) 
		{
			e.printStackTrace();
			
			return null;
		}
		
	}

}


