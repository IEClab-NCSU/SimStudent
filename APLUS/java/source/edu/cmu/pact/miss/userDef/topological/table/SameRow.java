package edu.cmu.pact.miss.userDef.topological.table;

import java.util.Vector;

import jess.Fact;
import jess.JessException;
import jess.Rete;

public class SameRow extends TableConstraint {

	public SameRow() {
		
		setName("same-row");
		setArity(2);
	}

	public String apply(Vector args,Rete rete) {
		
		try
		{
		return sameRow((Fact)args.get(0),(Fact)args.get(1), rete);
		}
		catch(JessException e)
		{
			e.printStackTrace();
			return null;
		}
	
		
	}


}
