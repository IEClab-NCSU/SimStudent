/**
 * Created: Sat Mar 26, 2011
 * Noboru Matsuda
 * 
 * Production Interpreter
 * 
 * Part of this class is domain dependent (e.g., initProblem(String)) hence this class is defined as 
 * an abstract class.  
 * 
 */
package SimStudent2.ProductionSystem;

import java.util.Iterator;

import jess.Activation;
import jess.Fact;
import jess.JessException;
import jess.RU;
import jess.Value;
import SimStudent2.LearningComponents.SAI;

public abstract class Solver {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Domain dependent methods
	// These domain dependent methods must be implemented in the extended class
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	// initialize a problem by entering appropriate values in specific WMEs
	// 
	public abstract void enterProblem(String problemPiece);

	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Fields
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	SsRete rete;

	private String productionRuleFileName = "productionRules.pr";

	private String wmeDeftemplateFileName = "wmeTypes.clp";
	
	private String initWmeFileName = "init.wme";

	
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	/*
	public Solver () {
        
    	// Initialize a Rete instance
        initRete();
    }
	 */
    
    /**
	 * @param wmeDeftemplateFileName2
	 * @param initWmeFileName2
	 * @param productionFileName
	 */
	public Solver(String wmeDeftemplateFileName, String initWmeFileName, String productionFileName) {
		setWmeDeftemplateFileName(wmeDeftemplateFileName);
		setInitWmeFileName(initWmeFileName);
		setProductionRuleFileName(productionFileName);
		initRete();
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods 
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 


	// Initialization
    void initRete() {
        setRete(new SsRete());
        resetRete();
    }

    public void resetRete() {

        try {
			getRete().reset();
			loadWmeDeftemplate();
			loadInitWme();
			loadProductionRule();
			
		} catch (JessException e) {
			e.printStackTrace();
		}
    }

    
	// Enter a step using the specified SAI . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// 
	public void enterStep(SAI sai) {

		Fact wme = lookupFactByName(sai.getSelection());
		SsRete rete = getRete();
		
		try {
			rete.modify(wme, "value", new Value(sai.getInput(), RU.SYMBOL));

		} catch (JessException e) {
			e.printStackTrace();
		}
	}
    
    // Working Memory Elements . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
    //

    /**
     * Read WME Deftemplate, initial WME values, and production rules from a file
     * 
     * @param wmeFile
     */
    public void loadWmeDeftemplate(String wmeFile) {
    	getRete().loadBatchFile(wmeFile);
    }
    
    public void loadWmeDeftemplate() {
    	loadWmeDeftemplate(getWmeDeftemplateFileName());
    }
    
    public void loadInitWme(String wmeFile) {
    	getRete().loadBatchFile(wmeFile);
	}
    
    public void loadInitWme() {
    	loadInitWme(getInitWmeFileName());
    }
    
	public void loadPR(String ruleFile) {
		getRete().loadBatchFile(ruleFile);
	}
	
	public void loadProductionRule() {
		loadPR(getProductionRuleFileName());
	}
	
	/*
	public void reloadPR() {
		loadPR(getPrFileName());
	}
	*/
	
	@SuppressWarnings("unchecked")
	public void printFacts() {

		Iterator<Fact> facts = getRete().listFacts();
		while(facts.hasNext()) {
			Fact fact = facts.next();
			System.out.println(fact + "\n");
		}
	}
	
	/**
	 * Test if the target slot value is the one expected
	 * 
	 * @param name
	 * @param slot
	 * @param expectedValue
	 * @return True if the value of the <slot> of the Fact with <name> is <expectedValue> 
	 */
	public boolean isSlotValueOfFact(String name, String slot, Value expectedValue) {

		boolean isSlotValueOfFact = false;
		
		Fact wme = lookupFactByName(name);
		
		// System.out.println("isSlotValueOfFact: found WME " + wme + "\n");
		
		if (wme != null) {
			
			try {
				Value slotValue = wme.getSlotValue(slot);
				// System.out.println("Value = " + slotValue + "\n");
				// System.out.println("expectedValue = " + expectedValue + "\n");
				isSlotValueOfFact = expectedValue.equals(slotValue);
			} catch (JessException e) {
				e.printStackTrace();
			}
		}
		return isSlotValueOfFact;
	}

	// WME lookup . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	//
	public Fact lookupFactByName(String name) {
		
		return getRete().lookupFactByName(name); 
	}

	// Rule Engine . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	//
	@SuppressWarnings("unchecked")
	public Iterator<Activation> getRuleActivations() {
		
		Iterator<Activation> ruleActivations = null;

		try {
			ruleActivations = getRete().listActivations();
		} catch (JessException e) {
			e.printStackTrace();
		}
		
		return ruleActivations;
	}
	
	// Number of fireable productions
	//
	public int sizeRuleActivation() {
		
		int size = 0;
		
		Iterator<Activation> ruleActivations = getRuleActivations();
		while (ruleActivations.hasNext()) {
			size++;
			ruleActivations.next();
		}
		
		return size;
	}
	
	// Printing rules . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	//
	@SuppressWarnings("unchecked")
	public void printPR() {

		System.out.println("printing Production Rules defrules...\n");
		Iterator<Object> facts = getRete().listDefrules();
		while(facts.hasNext()) {
			Object fact = facts.next();
			System.out.println(fact + "\n");
		}
		System.out.println("done.\n");
	}

	/**
	 * Print out rule activations
	 */
	public void printRuleActivations() {
		Iterator<Activation> ruleActivations = getRuleActivations();
    	while (ruleActivations.hasNext()) {
    		Activation activation = (Activation)ruleActivations.next();
    		System.out.println(activation);
    	}
    	System.out.println("... done\n");
	}

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Getters / Setters
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
	public SsRete getRete() { return rete; }
    private void setRete(SsRete rete) { this.rete = rete; }

    private String getProductionRuleFileName() { return this.productionRuleFileName; } 
    private void setProductionRuleFileName(String fileName) { this.productionRuleFileName = fileName; }

	private String getWmeDeftemplateFileName() { return wmeDeftemplateFileName; }
	private void setWmeDeftemplateFileName(String wmeDeftemplateFileName) { this.wmeDeftemplateFileName = wmeDeftemplateFileName; }

	private String getInitWmeFileName() { return initWmeFileName; }
	private void setInitWmeFileName(String initWmeFileName) { this.initWmeFileName = initWmeFileName; }
	
}
