/**
 * Copyright (c) 2013 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import jess.Defquery;
import jess.Deftemplate;
import jess.Fact;
import jess.Funcall;
import jess.HasLHS;
import jess.JessException;
import jess.QueryResult;
import jess.Rete;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.trace;

/**
 * Static class to maintain the hint fact.
 */
public class HintFact {

	/**
	 * This class doesn't need instantiations.
	 */
	private HintFact() {}


	/**
	 * Create or modify the hint fact.
	 * @param hintRequest true if this is a hint request
	 * @param r the rule engine
	 */
	static void setHintFact(boolean hintRequest, Rete r) {
		if(trace.getDebugCode("hints"))
			trace.out("hints", "setHintFact("+hintRequest+")");
		Fact hFact = getFact(r);
		if(hFact == null)
			assertFact(hintRequest, r);
		else
			modify(hFact, hintRequest, r);
	}

	/**
	 * Modify the given hint fact with the new slot value.
	 * @param hFact 
	 * @param hintRequest value for slot
	 * @param r the rule engine
	 */
	private static void modify(Fact hFact, boolean hintRequest, Rete r) {
		try {
			if(trace.getDebugCode("hints"))
				trace.out("hints", "HintFact.modify("+hintRequest+") fact "+hFact.getFactId()+
						" before change: "+hFact);
			r.modify(hFact, "now", (hintRequest ? Funcall.TRUE : Funcall.FALSE));
		} catch(JessException je) {
			trace.err("Error modifying hint fact: "+je+"; cause "+je.getCause()+
					".\n  "+je.getProgramText()+
					"\n  "+je.getDetail());
		}
	}

	/**
	 * Create the deftemplate, if not already available, and assert the hint fact.
	 * @param hintRequest value for slot
	 * @param r the rule engine 
	 */
	private static void assertFact(boolean hintRequest, Rete r) {
		try {
			Fact hFact = new Fact(getDeftemplate(r));
			hFact.setSlotValue("now", (hintRequest ? Funcall.TRUE : Funcall.FALSE));
			r.assertFact(hFact);
			if(trace.getDebugCode("hints"))
				trace.out("hints", "HintFact.assertFact("+hintRequest+") new fact "+hFact.getFactId()+
						": "+hFact);			
		} catch(JessException je) {
			trace.err("Error asserting hint fact: "+je+"; cause "+je.getCause()+
					".\n  "+je.getProgramText()+
					"\n  "+je.getDetail());
		}
	}

	/**
	 * Retrieve the hint fact using a defquery. Create the query if necessary.
	 * @param r the rule engine
	 * @return the first (should be only) hint fact returned by the query
	 */
	private static Fact getFact(Rete r) {
		for(int trial = 0; trial < 2; trial++) {   // try up to 2x
			try {
				QueryResult qr = r.runQueryStar("get-hint", new ValueVector(0));
				if(!qr.next())
					return null;
				Object result = qr.getObject("h");
				if(trace.getDebugCode("hints"))
					trace.out("hints", "HintFact.getFact() found object "+result+
							", type "+(result == null ? null : result.getClass()));			
				if(result instanceof Fact)
					return (Fact) result;
				else
					return null;
			} catch(JessException je) {
				if(trial > 0) {
					trace.err("Error running defquery get-hint: "+je+"; cause "+je.getCause()+
							".\n  "+je.getProgramText()+
							"\n  "+je.getDetail());
					break;
				}
			}
			synchronized(r) {
				getDeftemplate(r);
				try {
					HasLHS q = r.findDefrule("get-hint");
					if(!(q instanceof Defquery))               // if query undefined
						r.eval("(defquery get-hint "+
								"\"Retrieve the fact holding the hint status.\"" +
								"?h <- (hint))");
				} catch(JessException je) {
					trace.err("Error creating hint query: "+je+"; cause "+je.getCause()+
							".\n  "+je.getProgramText()+
							"\n  "+je.getDetail());
				}
			}
		}
		return null;
	}

	/**
	 * @param r the rule engine
	 * @return (deftemplate hint (slot now)) ; creates if not defined 
	 */
	private static Deftemplate getDeftemplate(Rete r) {
		try {
			Deftemplate hTemplate = r.findDeftemplate("hint");
			if(hTemplate == null) {
				r.eval("(deftemplate hint (slot now))");
				hTemplate = r.findDeftemplate("hint");
			}
			return hTemplate;
		} catch(JessException je) {
			trace.err("Error finding or creating hint deftemplate: "+je+"; cause "+je.getCause()+
					".\n  "+je.getProgramText()+
					"\n  "+je.getDetail());
			return null;
		}
	}	
}
