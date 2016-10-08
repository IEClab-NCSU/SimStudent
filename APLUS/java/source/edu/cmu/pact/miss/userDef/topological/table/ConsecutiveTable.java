package edu.cmu.pact.miss.userDef.topological.table;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

import jess.Fact;
import jess.JessException;
import jess.Rete;

public class ConsecutiveTable extends TableConstraint {

	public ConsecutiveTable() {
		setName("consecutive-table");
		setArity(2);
	}

	public String apply(Vector args,Rete rete) {
		try
		{
			//trace.out(5, "ctable Predicate called\n");
			return consecutiveTable((Fact)args.get(0),(Fact)args.get(1),rete);
		}
		catch(JessException e)
		{
			e.printStackTrace();
			return null;
		}

	}
}