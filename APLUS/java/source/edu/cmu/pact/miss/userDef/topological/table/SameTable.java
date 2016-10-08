package edu.cmu.pact.miss.userDef.topological.table;

import java.util.Vector;

import jess.Fact;
import jess.JessException;
import jess.Rete;

public class SameTable extends TableConstraint {

	public SameTable() {
		setArity(2);
		setName("same-table");
	}

	public String apply(Vector args, Rete rete) {
		try
		{
			return sameTable((Fact)args.get(0),(Fact)args.get(1),rete);
		}
		catch (JessException e) 
		{
			e.printStackTrace();
			return null;
		}
		
	}

}
