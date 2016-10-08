package edu.cmu.pact.miss.userDef.topological.table;

import java.util.Vector;

import jess.Fact;
import jess.JessException;
import jess.Rete;

public class SameColumn extends TableConstraint {
	public SameColumn()
	{
		setArity(2);
		setName("same-column");
	}
	public String apply(Vector args,Rete rete) {
		
		try
		{
		return sameColumn((Fact)args.get(0),(Fact)args.get(1),rete);
		}
		catch(JessException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	
}
