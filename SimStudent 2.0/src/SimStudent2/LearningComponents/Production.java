/**
 * Created: Dec 24, 2013 7:47:28 PM
 * @author mazda
 * 
 */
package SimStudent2.LearningComponents;

import SimStudent2.TraceLog;


/**
 * @author mazda
 *
 */
public class Production {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * Name of this production 
	 */
	private String name;

	/**
	 * WME Perception 
	 */
	private WmePerception wmePerception;

	/**
	 * LHS conditions
	 */
	private LhsConditions lhsConditions;
	
	/**
	 * RhsOperators
	 */
	private RhsOperators rhsOperators;
	
	
	/**
	 * @param wmePerception
	 * @param lhsConditions
	 * @param rhsOperators
	 */
	public Production(String ruleName, WmePerception wmePerception, LhsConditions lhsConditions, RhsOperators rhsOperators) {
		
		setName(ruleName);
		setWmePerception(wmePerception);
		setLhsConditions(lhsConditions);
		setRhsOperators(rhsOperators);
		
		TraceLog.out("**lhsConditions: " + lhsConditions);
		
	}

	@Override
	public String toString() {
		return "Production [name=" + name + ", " + 
				"wmePerception=" + wmePerception + ", " + 
				"lhsConditions=" + lhsConditions + ", " + 
				"rhsOperators="	+ rhsOperators + 
				"]";
	}
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .  
	// Formated string for production generation
	// 
	
	public String getFormattedLhsWmePath() {
		
		WmePerception wmePerception = getWmePerception();
		String foa = wmePerception.getFormattedFoA();
		String selection = wmePerception.getFormattedSelection();
		
		String formattedLhsWmePath = foa + "\n" + selection + "\n";
		return formattedLhsWmePath;
	}
	
	public String getLhsWmePath() {
		WmePerception wmePerception = getWmePerception();
		String foa = wmePerception.getFocusOfAttention().toString();
		String selection = wmePerception.getSelection().toString();
		return foa + selection;
	}

	/**
	 * @return
	 */
	public String getFormattedLhsConditions() {
		
		
		String formattedLhsConditions = "";
		
		LhsConditions lhsConditions = getLhsConditions();
		
		if (lhsConditions != null) {
			
			formattedLhsConditions = lhsConditions.toFormattedString();
		}
		
		return formattedLhsConditions;
	}

	
	/**
	 * @return
	 */
	public String getFormattedRhsOperators() {
		
		RhsOperators rhsOperators = getRhsOperators();
		String formattedRhsOperator = rhsOperators.toStringForProduction();
		
		return formattedRhsOperator;
	}


	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Getter & Setter
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	public WmePerception getWmePerception() { return wmePerception; }
	public void setWmePerception(WmePerception wmePerception) {	this.wmePerception = wmePerception;	}
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public LhsConditions getLhsConditions() { return lhsConditions; }
	public void setLhsConditions(LhsConditions lhsConditions) { this.lhsConditions = lhsConditions; }

	public RhsOperators getRhsOperators() { return rhsOperators; }
	public void setRhsOperators(RhsOperators rhsOperators) { this.rhsOperators = rhsOperators; }

}
