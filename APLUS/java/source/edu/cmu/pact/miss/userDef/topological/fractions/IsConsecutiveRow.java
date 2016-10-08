package edu.cmu.pact.miss.userDef.topological.fractions;

import java.util.Vector;

import jess.Fact;
import jess.JessException;
import jess.Rete;

public class IsConsecutiveRow extends FractionConstraint {

	public IsConsecutiveRow() {
		setArity(2);
		setName("is-consecutive-row");
	}
	
	
	public String apply(Vector args, Rete rete) {

		try
		{

			
			return (isConsecuitiveRow((Fact)args.get(0),(Fact)args.get(1), rete) ? "T" : null);
		}
		catch (JessException e) 
		{
			e.printStackTrace();
			
			return null;
		}
		
	}
}


