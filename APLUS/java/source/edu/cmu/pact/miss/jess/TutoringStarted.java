package edu.cmu.pact.miss.jess;

import java.util.Vector;

import jess.Context;
import jess.Fact;
import jess.JessException;

public class TutoringStarted extends ModelTracePredicate{

	
	public TutoringStarted() {
		setName("tutoring-started");
	}
	
	@Override
	public String apply(Vector argv, Context context) {
		
		String slotValue;
		try {
			slotValue = ((Fact)argv.get(0)).getSlotValue("APlusLaunched").stringValue(context);
			if(slotValue.equalsIgnoreCase(WorkingMemoryConstants.TRUE)) {
				return "T";
			}
		} catch (JessException e) {
			e.printStackTrace();
		}

		return null;
	}

}
