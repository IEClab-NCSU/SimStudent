package edu.cmu.pact.miss.userDef.topological.fractions;

import java.util.Vector;

import jess.Fact;
import jess.JessException;
import jess.Rete;

public class SamePosition extends FractionConstraint {

	public SamePosition() {
		setArity(2);
		setName("same-position-in-fraction-addition");
	}

	public String apply(Vector args, Rete rete) {

		try
		{
			return samePosition((Fact)args.get(0),(Fact)args.get(1),rete);
		}
		catch (JessException e) 
		{
			e.printStackTrace();
			return null;
		}
		
	}

}
