package edu.cmu.pact.miss.userDef.topological.fractions;

import java.util.Vector;

import jess.Fact;
import jess.JessException;
import jess.Rete;

public class IsSameRow extends FractionConstraint {

	public IsSameRow() {
		setArity(2);
		setName("is-same-row");
	}
	
	
	public String apply(Vector args, Rete rete) {

		try
		{

			return (isSameRow((Fact)args.get(0),(Fact)args.get(1), rete) ? "T" : null);
		}
		catch (JessException e) 
		{
			e.printStackTrace();
			
			return null;
		}
		
	}
}
