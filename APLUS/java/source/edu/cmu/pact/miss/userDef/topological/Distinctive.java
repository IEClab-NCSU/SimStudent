package edu.cmu.pact.miss.userDef.topological;

import java.util.Vector;

import jess.Fact;
import jess.JessException;
import jess.Rete;
import edu.cmu.pact.miss.WMEConstraintPredicate;

public class Distinctive extends WMEConstraintPredicate {

	public Distinctive() {
		setName("distinctive");
		setArity(2);
		
	}
	final String ROW_INDEX_SLOT="row-number";
	public String apply(Vector args,Rete rete) {
		Fact f1=(Fact)args.get(0);
		Fact f2=(Fact)args.get(1);
		if(f1.getFactId()!=f2.getFactId())
					return "T";
	
			return null;
				
	}

		
	

}
