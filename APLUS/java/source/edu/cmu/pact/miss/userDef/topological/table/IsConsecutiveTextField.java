package edu.cmu.pact.miss.userDef.topological.table;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

import jess.Context;
import jess.Fact;
import jess.JessException;
import jess.Rete;
import jess.Value;

public class IsConsecutiveTextField extends TableConstraint {

	public IsConsecutiveTextField() {
		setArity(2);
		setName("consecutive-textfield");
	}

	@Override
	public String apply(Vector args, Rete rete) {
		// TODO Auto-generated method stub
		trace.out("miss", "IsConsecutiveTextField: " + args.get(0) + "   " + args.get(1));
		try {
			return isConsecutiveTextField((Fact) args.get(0), (Fact) args.get(1));
		} catch (JessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
