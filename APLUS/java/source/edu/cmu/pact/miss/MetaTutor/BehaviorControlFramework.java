package edu.cmu.pact.miss.MetaTutor;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.SimSt;
import jess.Filter;
import jess.JessException;
import jess.Rete;

/**
 * 
 */
public class BehaviorControlFramework {

	/** File containing the rules and the deftemplates */
	private static final String JESS_RULES_FILE= "behavior.clp";
	
	/** {@link Rete} instance to run the rule engine */
	private Rete engine;
	
	private SimSt simSt;
	
	public SimSt getSimSt() {
		return simSt;
	}

	public void setSimSt(SimSt simSt) {
		this.simSt = simSt;
	}

	private int checkStepCorrectness = 0;
	
	public int getCheckStepCorrectness() {
		return checkStepCorrectness;
	}

	public void setCheckStepCorrectness(int checkStepCorrectness) {
		this.checkStepCorrectness = checkStepCorrectness;
	}

	public BehaviorControlFramework(SimSt ss) {
		this.simSt = ss;
	}
	
	public Iterator run() {
		
		File jessInputFile = null;
		jessInputFile = new File(simSt.getProjectDir(), JESS_RULES_FILE);
		
		// Create a jess Rule engine
		engine = new Rete();
		
		// Load the behavior rules
		try {
			engine.reset();
			engine.batch(jessInputFile.getCanonicalPath());
			engine.add(this);
			int result = engine.run();
			return engine.getObjects(new Filter() {
				@Override
				public boolean accept(Object arg0) {
					// TODO Auto-generated method stub
					if(arg0 instanceof ListSelection) {
						return true;
					}
					else {
						return false;
					}
				}
			});
		} catch (JessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
