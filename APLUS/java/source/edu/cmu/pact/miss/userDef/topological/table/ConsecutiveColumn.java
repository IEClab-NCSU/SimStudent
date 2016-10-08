package edu.cmu.pact.miss.userDef.topological.table;

import java.util.Vector;

import jess.Fact;
import jess.JessException;
import jess.Rete;

public class ConsecutiveColumn extends TableConstraint {

	public ConsecutiveColumn() {
		setName("consecutive-column");
		setArity(2);
	}

	public String apply(Vector args,Rete rete) {
		try
		{
		return consecutiveColumn((Fact)args.get(0),(Fact)args.get(1),rete);
		}
		catch(JessException e)
		{
			e.printStackTrace();
			return null;
		}

	}

	

}
