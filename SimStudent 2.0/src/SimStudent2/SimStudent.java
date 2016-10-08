/**
 * 
 * SimStudent 2.0 is a computational model of inductive rule learning.  
 * It induces if-then rules from given examples. Examples are categorized by rule names.
 * 
 * Fundamental concepts (aka definitions) must be given.  
 *  
 * Created: Dec 21, 2013 9:40:04 PM
 * @author mazda
 * 
 * (c) Noboru Matsuda 2013-2014
 * 
 */
package SimStudent2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import weka.core.Instances;
import SimStudent2.LearningComponents.Example;
import SimStudent2.LearningComponents.InputMatcher;
import SimStudent2.LearningComponents.J48Instances;
import SimStudent2.LearningComponents.LhsConditions;
import SimStudent2.LearningComponents.LhsConditionsLearner;
import SimStudent2.LearningComponents.Production;
import SimStudent2.LearningComponents.RhsExhaustiveGoalTest;
import SimStudent2.LearningComponents.RhsOperators;
import SimStudent2.LearningComponents.RhsOperatorsLearner;
import SimStudent2.LearningComponents.RhsSearchSuccessorFn;
import SimStudent2.LearningComponents.SAI;
import SimStudent2.LearningComponents.WmePath;
import SimStudent2.LearningComponents.WmePerception;
import SimStudent2.LearningComponents.WmePerceptionLearner;
import SimStudent2.ProductionSystem.Solver;
import SimStudent2.ProductionSystem.SsRete;

/**
 * @author mazda
 *
 */
public class SimStudent {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Field
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public static final int MAX_WME_PERCEPTION_SEARCH_DEPTH = 20;
	private static final int RHS_MAX_SEARCH_DEPTH = 4;
	
	// By default, the length of an rhs opeartor sequence is no longer than 4.
	static int rhsMaxSearchDepth = RHS_MAX_SEARCH_DEPTH;
	
	// A vector of steps demonstrated containing both positive and negative examples
	// The object Step has a flag that differentiate positive and negative example
	//
	private ArrayList<Example> givenExamples = new ArrayList<Example>();

	// A vector of given examples for a particular rule name
	//
	private Hashtable<String, ArrayList<Example>> examplesFor = new Hashtable<String, ArrayList<Example>>();
	
	//By default, SimStuent only updates production when it can't explain ("model trace") a given example.
	//
	private boolean learnOnTraceFailure = true;

	// Rete production engine
	//
	private Solver solver;

	// Learned production paired with a rule name
	//
	private ArrayList<Production> learnedProductions = new ArrayList<Production>();

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
	// Domain dependent learning components
	//
	// WME Perception 
	private WmePerceptionLearner wmePerceptionLearner;
	//
	// LHS Conditions
	//
	private LhsConditionsLearner lhsConditionsLearner;
	
	
	// Rhs Search
	// 
	private Class<RhsOperatorsLearner> rhsOperatorsLearnerClass;
	//
	// A search agent is reserved for individual production (by rule name)
	private Hashtable<String, RhsOperatorsLearner> rhsOperatorsLearnersHash = new Hashtable<String, RhsOperatorsLearner>();
	//
	// Input Matcher
	private InputMatcher inputMatcher;
	// 
	// Goal Test
	private Class<RhsExhaustiveGoalTest> rhsGoalTestClass;
	//
	// A hashtable for RHS Goal Test
	private Hashtable<String, RhsExhaustiveGoalTest> rhsGoalTestHash = new Hashtable<String, RhsExhaustiveGoalTest>();
	//
	// Search Successor Function
	private RhsSearchSuccessorFn rhsSearchSuccessorFn;
	
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public SimStudent (Solver solver, 
			           WmePerceptionLearner wmePerceptionLearner,
			           LhsConditionsLearner lhsConditionsLearner,
			           Class<RhsOperatorsLearner> rhsOperatorsLearnerClass,
			           Class<RhsExhaustiveGoalTest> rhsGoalTestClass,
			           InputMatcher inputMatcher,
			           RhsSearchSuccessorFn rhsSearchSuccessorFn ) {
		
		TraceLog.addDebugCode("Research");
		setSolver(solver);
		setWmePerceptionLearner(wmePerceptionLearner);
		setLhsConditionsLearner(lhsConditionsLearner);
		setRhsOperatorsLearnerClass(rhsOperatorsLearnerClass);
		setRhsGoalTestClass(rhsGoalTestClass);
		setInputMatcher(inputMatcher);
		setRhsSearchSuccessorFn(rhsSearchSuccessorFn);
	}

	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * Activate SimStudent learning 
	 */
	/*
	public void dispatch() {
		
		Enter Problem
		
		while (kill) {
			
			sai = getNextStep();
			
			if (modelTrace(sai)) {
				
				reinforce(sai);
				
			} else {
				
				generalize(sai);
			}
		}
	}
	*/
	
	// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * -
	// Batch mode
	//
	
	public void generalizeExamplesFromFile(String fileName) {
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			
			String line = br.readLine();
			while (line != null) {

				switch (line.charAt(0)) {
				case 'p':
				case 'P':
					generalizeExamplesFromFileEnterProblem(line.split("\\t"));
					break;
				case 'a':
				case 'A':
					generalizeExamplesFromFileActionPerformed(line.split("\\t"));
				}
				
				line = br.readLine();
			}
			
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The argument is a list of string containing an ID "A" followed by an SAI tuple.
	 * @param line	{"A", Skill, Selection, Action, Input, FoA1, FoA2, FoA3, ...}
	 */
	private void generalizeExamplesFromFileActionPerformed(String[] line) {
		
		String skill = line[1];
		String selection = line[2];
		String action = line[3];
		String input = line[4];

		ArrayList<String> foaStr = new ArrayList<String>();
		
		for (int i = 5; i < line.length; i++) {
			if (!line[i].isEmpty()) {
				foaStr.add(line[i]);
			} else {
				break;
			}
		}
		
		Example example = makeExampleFromWithSAI(selection, action, input, skill, foaStr);
	
		Production production = generalizeGivenExample(example);
		TraceLog.out("production: " + production);
	}
	
	private Example makeExampleFromWithSAI(String selection, String action, String input, String skill, ArrayList<String> foaStrs) {

		SsRete rete = getSolver().getRete();
		
		WmePath selectionWmePath = new WmePath(selection, rete, WmePath.SELECTION);
		SAI sai = new SAI(selectionWmePath, action, input);
		getSolver().enterStep(sai);

		ArrayList<WmePath> foa = new ArrayList<WmePath>();
		for (String foaStr : foaStrs) {
			foa.add(new WmePath(foaStr, rete, WmePath.FOA));
		}

		return new Example(skill, foa, sai);
	}
	

	/**
	 * The argument is a list of string containing an ID "P" followed by a string representing start state elements
	 * @param line	{"P", Problem} 
	 */
	private void generalizeExamplesFromFileEnterProblem(String[] line) {

		// ToDo This is an obvious ad-hoc code only targeting the Algebra interface
		// The start state must be composed by a list of start state elements
		getSolver().enterProblem(line[1]);
	}


	// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * -
	// Top-level Learning function
	//
	
	
	/**
	 * @param string
	 * @return
	 */
	public static boolean isRunningOutOfTime(String string) {
		// ToDo isRunningOutOfTime()
		return false;
	}
	
	
	// SimSt:: boolean generateRuleFor(String skillName)
	
	/**
	 * Takes a step to be generalized and invoke learning modules
	 * @param example	An instance of a domain dependent class <code>Example</code>. 
	 * @return		A production learned
	 */
	public Production generalizeGivenExample(Example example) {

		TraceLog.out("Research", "generalizeGivenExample: " + example);
		
		/*
		ArrayList<String> foaList = new ArrayList<String>();
		for (String foa : example.getFoaValues()) { foaList.add(foa); }
		TraceLog.out("generalizeGivenExample: FoA = " + foaList + ", SAI = " + example.getSAI());
		*/
		
		Production production = null;

		String ruleName = example.getRuleName();

		// Add the example to a pool of examples
		addExample(example);
		addExampleFor(ruleName, example); 
		
		Production currentProduction = getProductionFor(ruleName);

		// If there is a production for the given example, then see if the production can be refined...
		if (currentProduction != null) {
			
			TraceLog.out("Research", "Refining a production for " + currentProduction.getName());

			if (!isLearnOnTraceFailure() || !canModelTrace(example)) {
				production = generalizeProduction(example, currentProduction);
				
				if (production != null) {
					replaceProduction(currentProduction, production);
				}
			}
		}

		// If the production couldn't be refined or didn't existed at all, then make a new one...
		if (production == null) {
			production = makeNewProduction(example);
			// ToDo @ disjunction
			addProduction(production);
		}
		
		return production;
	}
	
	/**
	 * @param example
	 * @return
	 */
	public Production makeNewProduction(Example example) {
		
		TraceLog.out("makeNewProduction: " + example);
		
		String ruleName = example.getRuleName();
		
		WmePerception wmePerception = initialWmePerception(example);
		LhsConditions lhsConditions = initialLhsConditions(example);
		
		InputMatcher inputMatcher = getInputMatcher();
		RhsOperators rhsOperators = searchInitialRhsOperators(example, wmePerception, inputMatcher);
		
		return new Production(ruleName, wmePerception, lhsConditions, rhsOperators);
	}
	
	/**
	 * @param example
	 * @param currentProduction
	 * @return
	 */
	private Production generalizeProduction(Example example, Production currentProduction) {

		Production production = null;
		
		// We need positive and negative examples...
		// Learning must be accumulative...
		WmePerception wmePerception = generalizeLhsWmePerception(example, currentProduction);
		TraceLog.out("Research", "generalizeProduction LHS WME Perception learning " + (wmePerception == null ? "filed" : "succeeded"));
		
		if (wmePerception != null) {

			LhsConditions lhsConditions = updateLhsConditionsForAllProductions(example, currentProduction);
			TraceLog.out("Research", "generalizeProduction LHS Condition learning " + (lhsConditions == null ? "filed" : "succeeded"));
		
			if (lhsConditions != null) {
				
				RhsOperators rhsOperators = null;
				RhsOperators currentRhsOperators = currentProduction.getRhsOperators();

				if (currentRhsOperators.isConsistentWith(example, wmePerception.getFoaValueVariables())) {
					
					rhsOperators = currentRhsOperators;
					TraceLog.out("Research", "generalizeProduction the current RHS Operator is consistent with the given example");
					
				
				} else {
					
					// TraceLog.out("Research", "generalizeProduction(" + currentProduction + ") the current RHS Operator is NOT consistent with the given example: " + example);
					rhsOperators = refineRhsOperators(example);
					TraceLog.out("Research", "generalizeProduction RHS Operator learning " + (rhsOperators == null ? "filed" : "succeeded"));
					
				}
				
				if (rhsOperators != null) {
						
					String ruleName = example.getRuleName();
					production = new Production(ruleName, wmePerception, lhsConditions, rhsOperators);
					TraceLog.out("Research", "generalizeProduction updating production for " + ruleName + (production == null ? "filed" : "succeeded"));
				}
			}
		}
		
		return production;
	}


	// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *
	// LHS WME Perception 
	//
	/**
	 * @param ruleName
	 * @param currentExamples
	 * @return
	 */
	private WmePerception generalizeLhsWmePerception(Example newExample, Production currentProduction) {
		
		return getWmePerceptionLearner().generalizeLhsWmePerception(newExample, currentProduction);
	}

	/**
	 * @param example
	 * @return
	 */
	private WmePerception initialWmePerception(Example example) {
		
		// SsRete rete = getSolver().getRete();
		
		return getWmePerceptionLearner().initialWmePerception(example);
	}
	
	// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *
	// LHS Feature Tests
	//
	
	/**
	 * @param example
	 * @return
	 */
	private LhsConditions initialLhsConditions(Example example) {
		
		return getLhsConditionsLearner().initialLhsConditions(example);
	}
	
	/**
	 * Refine LHS conditions for all learned productions. 
	 * 
	 * @param example
	 * @param currentProduction
	 * @return						An updated LhsConditions for the given currentProduction
	 */
	private LhsConditions updateLhsConditionsForAllProductions(Example example, Production currentProduction) {

		// First, signal an implicit negative example to all already learned productions
		for (Production otherProduction : getLearnedProductions()) {
			
			if (otherProduction != currentProduction) {
			
				Example negativeExample = new Example(example);
				negativeExample.setAsNegativeExample();
				
				refineLhsConditions(negativeExample, otherProduction);
			}
		}
		
		LhsConditions lhsConditions = refineLhsConditions(example, currentProduction);
		
		return lhsConditions;
	}
	
	private LhsConditions refineLhsConditions(Example example, Production currentProduction) {
		
		LhsConditions lhsConditions = null;

		Instances j48Instances = currentProduction.getLhsConditions().getJ48Instances();
		lhsConditions = refineLhsConditions(example, j48Instances);
		
		return lhsConditions;
	}
	
	/**
	 * @param examples
	 * @param wmePerception
	 * @return 
	 */
	private LhsConditions refineLhsConditions(Example example, Instances j48Instances) {

		return getLhsConditionsLearner().refineLhsConditions(example, j48Instances);
	}
	
	// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *
	// RHS Operator Sequence
	//
	
	/**
	 * @param vExample
	 * @param wmePerception
	 * @return
	 */
	private RhsOperators searchInitialRhsOperators(Example example, WmePerception wmePerception, InputMatcher inputMatcher) {

		RhsOperators rhsOperators = null;

		String ruleName = example.getRuleName();
		RhsOperatorsLearner rhsOperatorsLearner = getRhsOperatorsLearnerFor(ruleName);
		rhsOperators = rhsOperatorsLearner.initialSearch(example, wmePerception, inputMatcher);
		
		return rhsOperators;
	}

	/**
	 * @param examples
	 * @param wmePerception
	 * @return 
	 */
	private RhsOperators refineRhsOperators(Example example) {
		
		RhsOperators rhsOperators = null;
		
		String ruleName = example.getRuleName();
		
		RhsOperatorsLearner rhsOperatorsLearner = getRhsOperatorsLearnerFor(ruleName);
		rhsOperators = rhsOperatorsLearner.search(example);
		
		return rhsOperators;
	}

	// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *
	// Model Trace a step demonstrated
	/**
	 * @param example
	 * @return
	 */
	private boolean canModelTrace(Example example) {
		// ToDo canModelTrace()
		return false;
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Getters & Setters
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public void addExample(Example example) { givenExamples.add(example); }
	public ArrayList<Example> getGivenExamples() { return this.givenExamples; }

	/**
	 * @return A vector of examples given for <code>ruleName</code>
	 */
	public ArrayList<Example> getExamplesFor(String ruleName) {
		return examplesFor.get(ruleName);
	}

	/**
	 * @param theRuleName
	 * @param example
	 */
	public void addExampleFor(String theRuleName, Example example){
		
		String ruleName = example.getRuleName();
		
		if (theRuleName == null || !theRuleName.equals(ruleName)) {
			String msg = "addStepDemonstratedFor expected a skill name " + theRuleName;
			       msg += ", but got " + ruleName + ".  Continue to process anyway...\n";
			new Exception(msg).printStackTrace();
		}
		
		ArrayList<Example> examples = getExamplesFor(ruleName);
		
		if (examples == null) {
			examples = new ArrayList<Example>();
			this.examplesFor.put(ruleName, examples);
		}
		examples.add(example);
	}

	public boolean isLearnOnTraceFailure() { return this.learnOnTraceFailure; }
	public void setLearnOnTraceFailure(boolean learnOnTraceFailure) {
		this.learnOnTraceFailure = learnOnTraceFailure;
	}

	public Solver getSolver() {	return this.solver; } 
	public void setSolver(Solver solver) { this.solver = solver; }

	public WmePerceptionLearner getWmePerceptionLearner() { return wmePerceptionLearner; }
	public void setWmePerceptionLearner(WmePerceptionLearner wmePerceptionLearner) {
		this.wmePerceptionLearner = wmePerceptionLearner;
	}
	
	public ArrayList<Production> getLearnedProductions() {
		return this.learnedProductions;
	}
	
	public void addProduction(Production production) { this.learnedProductions.add(production); }
	
	public Production getProductionFor(String ruleName) { 
		
		// TraceLog.out("getProductionFor( " + ruleName + " )...");
		
		Production production = null;
		
		for(Production pr : this.learnedProductions) {
			
			// TraceLog.out("... " + pr.getName());
			
			if (ruleName.equals(pr.getName())) {
				production = pr;
				// TraceLog.out("Got it....");
				break;
			}
		}
		
		return production;
	}

	private void replaceProduction(Production currentProduction, Production newProduction) {
		this.learnedProductions.remove(currentProduction);
		this.learnedProductions.add(newProduction);
	}

	private RhsOperatorsLearner getRhsOperatorsLearnerFor(String ruleName) {
		
		RhsOperatorsLearner rhsOperatorsLearner = null;
		
		if (this.rhsOperatorsLearnersHash.containsKey(ruleName)) {
			
			rhsOperatorsLearner = this.rhsOperatorsLearnersHash.get(ruleName);
			
		} else {
			
			Class<RhsOperatorsLearner> rhsOperatorsLearnerClass = getRhsOperatorsLearnerClass();
			
			try {
				rhsOperatorsLearner = (RhsOperatorsLearner)rhsOperatorsLearnerClass.newInstance();
				setRhsOperatorsLearnerFor(ruleName, rhsOperatorsLearner);
				
				// TraceLog.out("RhsOperatorsLearner (" + rhsOperatorsLearner + ") created for " + ruleName);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Initialize the search agent
			rhsOperatorsLearner.init(getRhsGoalTestFor(ruleName), getRhsSearchSuccessorFn());
		}
		
		return rhsOperatorsLearner;
	}
	
	private void setRhsOperatorsLearnerFor(String ruleName, RhsOperatorsLearner rhsOperatorsLearner) { 
		this.rhsOperatorsLearnersHash.put(ruleName, rhsOperatorsLearner);
	}

	private InputMatcher getInputMatcher() { return inputMatcher; }
	private void setInputMatcher(InputMatcher inputMatcher) { this.inputMatcher = inputMatcher; }

	private Class<RhsOperatorsLearner> getRhsOperatorsLearnerClass() { return rhsOperatorsLearnerClass; }
	private void setRhsOperatorsLearnerClass(Class<RhsOperatorsLearner> rhsOperatorsLearnerClass) {
		this.rhsOperatorsLearnerClass = rhsOperatorsLearnerClass;
	}

	public static int getRhsMaxSearchDepth() { return rhsMaxSearchDepth; }
	public void setRhsMaxSearchDepth(int newRhsMaxSearchDepth) { rhsMaxSearchDepth = newRhsMaxSearchDepth; }

	/*
	private Hashtable<String, RhsOperatorsLearner> getRhsOperatorsLearners() { return rhsOperatorsLearners; }
	private void setRhsOperatorsLearners(Hashtable<String, RhsOperatorsLearner> rhsOperatorsLearners) {
		this.rhsOperatorsLearners = rhsOperatorsLearners;
	}
	*/

	private RhsSearchSuccessorFn getRhsSearchSuccessorFn() { return rhsSearchSuccessorFn; }
	private void setRhsSearchSuccessorFn(RhsSearchSuccessorFn rhsSearchSuccessorFn) {
		this.rhsSearchSuccessorFn = rhsSearchSuccessorFn;
	}

	/**
	 * @param ruleName
	 * @return
	 */
	private RhsExhaustiveGoalTest getRhsGoalTestFor(String ruleName) {
		RhsExhaustiveGoalTest rhsGoalTest = null;
		
		if (this.rhsGoalTestHash.containsKey(ruleName)) {
			rhsGoalTest = this.rhsGoalTestHash.get(ruleName);
		} else {
			Class<RhsExhaustiveGoalTest> rhsGoalTestClass = getRhsGoalTestClass();
			try {
				rhsGoalTest = (RhsExhaustiveGoalTest)rhsGoalTestClass.newInstance();
				rhsGoalTest.setInputMatcher(getInputMatcher());
				setRhsGoalTestFor(ruleName, rhsGoalTest);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return rhsGoalTest;
	}

	/**
	 * @param ruleName
	 * @param rhsGoalTest
	 */
	private void setRhsGoalTestFor(String ruleName, RhsExhaustiveGoalTest rhsGoalTest) {
		this.rhsGoalTestHash.put(ruleName, rhsGoalTest);
	}

	private Class<RhsExhaustiveGoalTest> getRhsGoalTestClass() { return rhsGoalTestClass; }
	private void setRhsGoalTestClass(Class<RhsExhaustiveGoalTest> rhsGoalTestClass) { this.rhsGoalTestClass = rhsGoalTestClass; }

	private LhsConditionsLearner getLhsConditionsLearner() {
		return lhsConditionsLearner;
	}

	private void setLhsConditionsLearner(LhsConditionsLearner lhsConditionsLearner) {
		this.lhsConditionsLearner = lhsConditionsLearner;
	}

}
