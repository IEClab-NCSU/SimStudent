/**
 * A state representation for searching RHS of the production rules.
 *
 * Created: Fri Dec 31 22:34:01 2004
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * (c) Noboru Matsuda  2004-2014
 */

package SimStudent2.LearningComponents;

import java.util.ArrayList;
import java.util.Vector;

import SimStudent2.TraceLog;
import SimStudent2.ProductionSystem.JessBindSequence;
import SimStudent2.ProductionSystem.JessBindSequence.JessBindClause;
import SimStudent2.ProductionSystem.UserDefJessSymbol;

public class RhsState {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields 
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	// Vector<Example> examples = new Vector<Example>();
	
    // The SsRete network
    // private SsRete ssRete;

    // The JessBindSequence object maintains a list of the pairs of variable and operator invocation 
	// each corresponds to a (bind ?var (operation ... )) statement in RHS
	// private Vector<JessBindSequence> jessBindClauseList = new Vector<JessBindSequence>();
	// 
	private JessBindSequence jessBindSequence = new JessBindSequence();

    // A list of free (i.e., unbound) variables.  
    // At the initial state, the list only contains variables that correspond with FoA.
    // When a new state is made for a particular operator, a new variable that represent 
    // an output from the newly added operator is added to this list.
    // 
    private ArrayList<String> freeVarList = new ArrayList<String>();

	// A list of variable bindings for the "seed" (i.e., the principal) example. 
    // This is used by the successor function to test argument types
    //
    private Bindings seedVarBindings = new Bindings();
    
    // A list of initial FoA's.  This is used to check if all FoA values are used, if necessary.
    // 
    private Vector<String> foaVarList = new Vector<String>();
    
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /**
	 * For duplicate()
	 * 
	 */
	public RhsState() {}

    /**
     * Make an initial state for a novel skill (associated with the given "seed" example)
     * 
     * @param example	This is the "seed" (i.e., the principal) example. 
     */
    public RhsState(Example example, ArrayList<WmePath> foa){

    	ArrayList<WmePath> exampleFoas = example.getFoA();
    	
    	// Register FoA for "seed" bindings
    	for (int i = 0; i < foa.size(); i++) {
    		
    		WmePath foaWmePath = foa.get(i);
    		WmePath exampleFoaWmePath = exampleFoas.get(i);
    		
    		// Make a binding for the value of wmePath
    		String foaValueVar = foaWmePath.getValueVariable();
    		String exampleFoaValue = exampleFoaWmePath.getValue();
    		getSeedVarBindings().addNewBinding(foaValueVar, exampleFoaValue);
    		
    		// TraceLog.out("adding foaValueVar " + foaValueVar + ".........");
    		
    		addFreeVarList(foaValueVar);
    		
    		// Also register varName to the foaVarList, which is used for isAllFoaUsed()
    		addFoaVarList(foaValueVar);
    	}
    }
    
    

	/**
	 * @param jessBindSequence
	 * @param freeVarList
	 * @param seedVarBindings
	 */
    public RhsState(JessBindSequence jessBindSequence, ArrayList<String> freeVarList, Bindings seedVarBindings) {
		
		setJessBindSequence(jessBindSequence);
		setFreeVarList(freeVarList);
		setSeedVarBindings(seedVarBindings);
	}

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
    // Cloning for search state expansion 
    // 
	
	/**
     * 
     */
    @SuppressWarnings("unchecked")
	public RhsState duplicate() {

    	JessBindSequence jessBindSequence = (JessBindSequence)getJessBindSequence().clone();
    	ArrayList<String> freeVarList = (ArrayList<String>)getFreeVarList().clone();
    	Bindings seedVarBindings = (Bindings)getSeedVarBindings().clone();

    	return new RhsState(jessBindSequence, freeVarList, seedVarBindings);
    }

    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
    // Free variable list
    //
    
    void addFreeVarList(String var) {
    	
    	this.freeVarList.add(var);
    }
    
    
	/**
	 * Returns false if freeVarList contains any FoA variables, which is preserved in foaVarList.
	 * 
	 */
	public boolean isAllFoaUsed() {
		
		boolean isAllFoaUsed = true;
		
		for (String foaVar : getFoaVarList()) {
			
			if (getFreeVarList().contains(foaVar)) {
				isAllFoaUsed = false;
				break;
			}
		}
		
		return isAllFoaUsed;
	}

    
    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
    // Seed example variable bindings
    //
    /*
    void addSeedVarBindings(Binding binding) {
    	
    	this.seedVarBindings.add(binding);
    }
    */
    
    
    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
    // Jess Bind Sequence
    // 
	
	/**
	 * Add a new Jess Bind Clause with a given operator and argV pair
	 * 
	 * @param op
	 * @param argV
	 */
	public JessBindClause addJessBindClause(UserDefJessSymbol op, ArrayList<String> argV) {
    	return getJessBindSequence().addJessBindClause(op, argV);
    }
    
	/** 
	 * @return	A string expression of the Jess Bind clause for the most recently added one
	 */
	public String getLastActionString() {
		
		// String action = "(bind " + child.getExpVar(newOp) + " " + newOp.actionStr(argV) + ")";
		return getJessBindSequence().getLastActionString();
	}

	

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
    // Search depth 
    // 
    
    /**
     * Number of RHS operators found so far, which is basically the size of the jessBindClauseList.
     * 
     */
    int numRhsOperators() {
    	return getJessBindSequence().size();
    }
    
    
    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
    // Printing out the state object 
    // 
    
    public String toString() {

    	String jessBindSequence = getJessBindSequence().toString();
    	String freeVarList = getFreeVarList().toString();
    	String seedVarBindingsStr = getSeedVarBindings().toString();
    	
    	return "<RhsState> jessBindSequence: " + jessBindSequence + ", freeVarList: " + freeVarList + ", seedVarBindingsStr: " + seedVarBindingsStr;
    }

	
    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
    // 
    // 
    

    /*
    // - 
    // - Matching function for hasValidOperations . . . . . . . . . . . . . . . . .
    // -

    // A matcher method must take two String arguments and return a non-null String, 
    // which by definition means that the two Strings are semantically equivalent
    //
    private String matcher = null;
    private String getMatcher() { return matcher; }
    public void setMatcher(String theMatcher) { matcher = theMatcher; }

    private Class<?> matcherClass = null;
    private Class<?> getMatcherClass() {
    
    	if (matcherClass == null) {

    		try {
    			
    			matcherClass = Class.forName(getMatcher());
            
    		} catch (Exception e) {
            
    			e.printStackTrace();
            }
        }
        return matcherClass;
    }
    
    private UserDefJessSymbol matcherInstance = null;
    private UserDefJessSymbol getMatcherInstance() {
    	
    	if (matcherInstance == null) {
        
    		try {
            
    			if(trace.getDebugCode("miss"))trace.out("miss", "matcherClass = " + getMatcherClass());
                matcherInstance = (UserDefJessSymbol)getMatcherClass().newInstance();
            
    		} catch (Exception e) {
            
    			e.printStackTrace();
            }
        }
        return matcherInstance;
    }
    */
    
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods 
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    /**
     * Test if the RHS-state explains the given example
     * 
     * It computes the sequence of operator applications given the "foa" value of the given example.
     * Then, compares it with the "input" value of the given example.
     * 
     * @param example
     * @return
     */
    /*
    boolean hasValidOperations(Example example) {
    	
    	// For variable unification
    	Hashtable<String, String> unifier = new Hashtable<String, String>();

    	// Extract the seeds from the instruction
    	Vector<WmePath> foaList = example.getFoA();
    	// For FoA bindPairs, to assign an foa value
    	int foaIndex = 0;
    	
    	String lastVal = null;
    	
    	// Apply a chain of expressions to see if the "input" hold
    	Vector<BindPair> opBindList = getJessBindClauseList();
    	for ( BindPair bindPair : opBindList) {
    		
    		UserDefJessSymbol operator = bindPair.getPredicate();
    		String val = null;
    		String var = bindPair.getVar();

    		// If the "operator" is null, which means that the "var" is an independent (i.e., foa) variable
    		if ( operator == null  ) {

    			// Get the "input" value from a corresponding FoA
    			WmePath foa = foaList.get(foaIndex++);
    			val = foa.getValue();
    		
    		} else {
    		
    			val = operator.evalExp( unifier );
    			lastVal = val;
    		}
    		
    		if ( val != null ) {
    			// Update the unification
    			unifier.put( var, val );
    		}
    	}
    	
    	return compareInput(lastVal, example.getInput());
    }
    */
    
    /*
    private boolean compareInput(String lastVar, String stInput) {

    	boolean isValid = (lastVar != null);

    	if (isValid) {

    		if (getMatcher() == null) {

    			isValid = lastVar.equals(stInput);

    		} else {

    			String cachedResult = cachedCompairInput(lastVar, stInput);
    			// String cachedResult = null;

    			if (cachedResult != null) {
    				isValid = cachedResult.equals("T");

    			} else {
    				try {
    					// Method matcher = getMatcherMethod();
    					UserDefJessSymbol matcherInstance = getMatcherInstance();
    					// Object[] args = new Object[] {lastVal, stInput};
    					//String result = (String)matcher.invoke(matcherInstance, args);
    					String result = matcherInstance.inputMatcher(lastVar, stInput);
    					putCachedCompairInput(lastVar, stInput, result);
    					isValid = (result != null);
    					//remove 'result'
    					//isValid = matcherInstance.inputMatcher(lastVal, stInput);
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    			}
    		}
    	}
    	return isValid;
    }
    */
    
    /*
    // private static edu.cmu.pact.miss.HashMap compairInputCache = new edu.cmu.pact.miss.HashMap();
    private static HashMap<String,HashMap<String,String>> compairInputCache = new HashMap<String,HashMap<String,String>>();

    private String cachedCompairInput(String lastVar, String stInput) {
        
        String cachedCompairInput = null;
        
        // edu.cmu.pact.miss.HashMap inputCache = 
        //    (edu.cmu.pact.miss.HashMap)compairInputCache.get(lastVar);
        HashMap<String,String> inputCache = (HashMap<String,String>)compairInputCache.get(lastVar);
        
        if (inputCache != null) {
            cachedCompairInput = (String)inputCache.get(stInput);
        }
        
        return cachedCompairInput;
    }

    private void putCachedCompairInput(String lastVar, String stInput, String result) {
        
        // edu.cmu.pact.miss.HashMap inputCache = 
        //    (edu.cmu.pact.miss.HashMap)compairInputCache.get(lastVar);

        HashMap<String,String> inputCache = (HashMap<String,String>)compairInputCache.get(lastVar);
        
        if (inputCache == null) {
            // inputCache = new edu.cmu.pact.miss.HashMap();
            inputCache = new HashMap<String,String>();
            compairInputCache.put(lastVar, inputCache);
        }

        inputCache.put(stInput, result == null ? "F" : "T");
    }

    public static void printInputCache() {
        
    	Iterator<String> keys = compairInputCache.keySet().iterator();
        
        while (keys.hasNext()) {
            String key = (String)keys.next();
            HashMap<String,String> hashMap = (HashMap<String,String>)compairInputCache.get(key);
            Iterator<String> values = hashMap.keySet().iterator();
            while (values.hasNext()) {
                String value = (String)values.next();
                String match = (String)hashMap.get(value);
                System.out.println(key + (match.equals("T") ? " == " : " != ") + value);
            }
        }
    }
    */
    

//  // What's this?? ************************************** * * * * * * * * * * * * * * * *
//  private double likelihood;
//
//  // assumes independence: computes the product of the probability of each wme
//  //
//  private double foaLikelihood(Vector /*of String*/ foaWmes) {
//      double product=1;        
//      for (int i=0; i<foaWmes.size(); i++){
//          String wme = (String) foaWmes.get(i);
//          product*=wmeLikelihood(wme);
//      }        
//      return product;
//  }
//  
//  private double wmeLikelihood(String wme) {
//      int howManyBack = FoaGetter.howManyBack(wme);
//      return 1 - 0.01*howManyBack;
//  }

	
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Getters and Setters 
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    /*
    private void setSsRete( SsRete SsRete ) { this.ssRete = SsRete; }
    SsRete getSsRete() { return this.ssRete; }
    */
 
    /*
    private Vector<JessBindSequence> getJessBindClauseList() { return this.jessBindClauseList; }
	private void setJessBindClauseList(Vector<JessBindSequence> jessBindClauseList) {
    	this.jessBindClauseList = jessBindClauseList;
    }
    */
    // private void addOpBindList(BindPair bp) { this.jessBindClauseList.add(bp); }
    
    JessBindSequence getJessBindSequence() {
		return jessBindSequence;
	}
    private void setJessBindSequence(JessBindSequence jessBindSequence) {
    	this.jessBindSequence = jessBindSequence;
    }

    void pushFreeVarList( String var ){ this.freeVarList.add( var ); }
    ArrayList<String> getFreeVarList() { return this.freeVarList; }
    void setFreeVarList( ArrayList<String> varList ) { this.freeVarList = varList; }
    void removeFreeVarList( String arg ) { this.freeVarList.remove( arg ); }
    void removeFreeVarList( ArrayList<String> vars ) {
    	for (String var : vars) {
    		removeFreeVarList( var );
    	}
    }

	public void clearFreeVarList() {
		this.freeVarList.clear();
	}

	Bindings getSeedVarBindings() {
		return seedVarBindings;
	}
	private void setSeedVarBindings(Bindings seedVarBindings) {
		this.seedVarBindings = seedVarBindings;
	}
	public void addSeedVarBindings(String newVar, String seedValue) {
		this.seedVarBindings.addNewBinding(newVar, seedValue);
	}

	
	
	private Vector<String> getFoaVarList() {
		return this.foaVarList;
	}
	private void addFoaVarList(String varName) {
		this.foaVarList.add(varName);
	}


}
