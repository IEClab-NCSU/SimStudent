/**
 * An implementation of a successor function
 * 
 * Created: May 29, 2014 11:12:55 PM
 * @author mazda
 * (c) Noboru Matsuda
 * 
 */
package SimStudent2.LearningComponents;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import jess.JessException;
import SimStudent2.SimStudent;
import SimStudent2.TraceLog;
import SimStudent2.ProductionSystem.JessBindSequence.JessBindClause;
import SimStudent2.ProductionSystem.UserDefJessSymbol;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

/**
 * @author mazda
 *
 */
public class RhsSearchSuccessorFn implements SuccessorFunction {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	ArrayList<UserDefJessSymbol> rhsOpList = null;

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * @param rhsOpNames
	 */
	/*
	public RhsSearchSuccessorFn(ArrayList<String> rhsOpNames) {
		this.rhsOpList = initRhsOpList(rhsOpNames);
	}
	*/
	
	public RhsSearchSuccessorFn(String rhsOpNameFile) {
		
		ArrayList<String> opList = readOpList(rhsOpNameFile);
		this.rhsOpList = initRhsOpList(opList);
	}

	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * Read lines from the given text file <opListFile> and compose a String vector
	 * 
	 * @param opListFile
	 * @return
	 */
	private ArrayList<String> readOpList(String opListFile) {
		
		ArrayList<String> opList = new ArrayList<String>();
		
		try {
			
			BufferedReader br = new BufferedReader( new FileReader(opListFile) );
			String opName = br.readLine();
			
			while ( opName != null ) {
				
				if (opName.charAt(0) != ';') {
					opList.add(opName);
				}
				opName = br.readLine();
			}
			
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return opList;
	}

	
	/**
	 * @param rhsOpList
	 * @return
	 */
	private ArrayList<UserDefJessSymbol> initRhsOpList(ArrayList<String> rhsOpNames) {
		
		ArrayList<UserDefJessSymbol> rhsOpList = new ArrayList<UserDefJessSymbol>();
		
		for (String opName : rhsOpNames) {
			
			try {
				
				@SuppressWarnings("unchecked")
				Class<UserDefJessSymbol> opClass = (Class<UserDefJessSymbol>) Class.forName(opName);
				UserDefJessSymbol userDefOp = opClass.newInstance();
				rhsOpList.add(userDefOp);
				// TraceLog.out("Operator [" + userDefOp.getClass().getSimpleName() + "] added...");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return rhsOpList;
	}

	/* (non-Javadoc)
	 * @see aima.search.framework.SuccessorFunction#getSuccessors(java.lang.Object)
	 */
	@Override
    public List<Successor> getSuccessors(Object state) {

    	// A list of successor states
		List<Successor> successors = new ArrayList<Successor>();

    	// Sat Sep 30 21:20:15 LDT 2006 :: Noboru
    	// Search must fail when it took too long...
    	if (!SimStudent.isRunningOutOfTime("RHS")) {

        	// See if the search could go any further... 
        	RhsState currentState = (RhsState)state;
    		// TraceLog.out("------ getSuccessors: " + currentState);

        	if (currentState.numRhsOperators() <= SimStudent.getRhsMaxSearchDepth()) {
    			
				ArrayList<String> freeVarList = currentState.getFreeVarList();
				if (!freeVarList.isEmpty()) {
				
					ArrayList<UserDefJessSymbol> rhsOpList = getRhsOpList();
					// TraceLog.out("rhsOpList = " + rhsOpList);
					for (UserDefJessSymbol rhsOp : rhsOpList) {
						
						int arity = rhsOp.getArity();
						// Temporal successors for a particular operator -- will be accumulated
						List<Successor> children = null;
						
						// * * * * * DEBUG * * * * * *
						// TraceLog.out("Operator: " + rhsOp + ", atiry = " + arity + ", freeVarList " + freeVarList);
						// * * * * * DEBUG * * * * * *
						
						// Consider the operator only when there are enough number of free variables left...
						if ( arity <= freeVarList.size() ) {
							
							// Generate successors for the given operator... 
							switch (arity) {
							case 0:
								children = getSuccessorWithNoArg(currentState, rhsOp);
								// TraceLog.out("getSuccessorWithNoArg returns " + (children != null ? children.size() : 0) + " kid");
								break;
							case 1:
								children = getSuccessorsStraight(currentState, rhsOp);
								// TraceLog.out("getSuccessorsStraight returns " + (children != null ? children.size() : 0) + " kid");
								break;
							default: 
								// The operator takes 2 or more arguments
								children = getSuccessorsComb(currentState, rhsOp, arity);
								// TraceLog.out("getSuccessorsComb returns " + (children != null ? children.size() : 0) + " kid");
								break;
							}
						}
						
						// * * * * * DEBUG * * * * * *
						// int numChildren = (children == null ? 0 : children.size());
						// TraceLog.out("RhsSearchSuccessorFn: " + numChildren + " successors found...");
						// * * * * * DEBUG * * * * * *
						
						// Accumulating the temporal successors
						if (children != null) {
							for (Successor child : children) {
								// TraceLog.out("getSuccessor: child = " + child.getState());
								successors.add( child );
							}}}}}
    	}

    	return successors;
    }

	/**
	 * Make a single successor for the operator rhsOp that does not take argument 
	 * (i.e., a zero argument operator) 
	 * 
	 * @param currentRhs
	 * @param newOp
	 * @return
	 */
	private ArrayList<Successor> getSuccessorWithNoArg(RhsState currentRhs, UserDefJessSymbol newOp) {
        
		ArrayList<Successor> children = new ArrayList<Successor>();
		RhsState child = makeRhsChildState( currentRhs, newOp );
		
		if (child != null) {
            String action = child.getLastActionString();
            children.add( new Successor( action, child ) );
        }
		
        return children;
    }
    
	/**
	 * Make successors for the given single-argument operator. That is, make as many successors as the number of
	 * free (unbound) variables.  
	 * 
	 * @param currentState		The current RHS search state
	 * @param newOp		The single-argument operator
	 * @param freeVarList	A list of free (unbound) variables
	 * @return
	 */
    private ArrayList<Successor> getSuccessorsStraight(RhsState currentState, UserDefJessSymbol newOp) {

    	ArrayList<Successor> children = new ArrayList<Successor>();
    	
    	ArrayList<String> freeVarList = currentState.getFreeVarList();
    	for (String arg : freeVarList) {

    		// Replace "arg" in the "varList" with a new variable that bind the output value of the rhsOp call
    		ArrayList<String> argV = new ArrayList<String>();
    		argV.add( arg );
    		
    		// rhsOp.setArgs(argV); ***** need this?
    		RhsState child = makeRhsChildState(currentState, newOp, argV);
    		
    		if (child != null) {
    			String action = child.getLastActionString();
    			children.add( new Successor( action, child ) );
    		}
    		
    	}
    	return children;
    }

    /**
     * Make successors for multi-argument (more than one) operator.  
     * 
     * @param currentState		The current RHS search state
     * @param rhsOp		The multi-argument operator
     * @param arity		The number of arguments
     * @return
     */
    private ArrayList<Successor> getSuccessorsComb( RhsState currentState, UserDefJessSymbol rhsOp, int arity) {

    	// TraceLog.out("getSuccessorsComb(" + rhsOp + ", " + arity + ") - - - - - -");
    	
    	ArrayList<Successor> children = new ArrayList<Successor>();
    	
		// The current free (unbound) variables from which all combination of <arity> arguments will be computed
    	ArrayList<String> varList = currentState.getFreeVarList();
		boolean isCommutative = rhsOp.isCommutative();
		
    	// If the arity matches with the varList, then ...
    	@SuppressWarnings("unchecked")
		Enumeration<String[]> varPair = enumerate(varList, arity, isCommutative);

    	while ( varPair.hasMoreElements() ) {

    		Object[] argArray = varPair.nextElement();

    		ArrayList<String> argV = arrayToList(argArray);
    		// rhsOp.setArgs( argV );  ***** Need this?
    		RhsState child = makeRhsChildState(currentState, rhsOp, argV);
    		// TraceLog.out("makeRhsChildState -->" + child + (child != null ? "@-@-@-@-@-@-@-@" : "X-X-X-X-X-X-X"));
    		
    		if (child != null) {
    			// System.out.println("XX");
    			String action = child.getLastActionString();
    			children.add( new Successor( action, child ) );
    		}
    	}
    	
    	// * * * * * DEBUG * * * * * *
    	/*
    	for (Successor s : children) {
    		System.out.println("Successor: " + s);
    	}
    	*/
    	// * * * * * DEBUG * * * * * *
    	
    	return children;
    }

    private ArrayList<String> arrayToList( Object[] array ) {
    	
    	ArrayList<String> list = new ArrayList<String>();
    	for (int i = 0; i < array.length; i++) {
    		list.add((String)array[i]);
    	}
    	return list;
    }
    
    @SuppressWarnings("rawtypes")
	private Enumeration enumerate( ArrayList<String> varList, int arity, boolean isCommutative ) {

		Enumeration varComb = null;

    	if ( arity == 1 ) {
    		
    		new Exception("Enumerate called with arity 1").printStackTrace();
    		
    	} else {
    		
    		// Calculate all different combinations for variable assignments
    		try {
    			// Initialize an array used to calculate combinations of variables for polynomial operators
    			Object[] varArray = varList.toArray();

    			if ( isCommutative ) {
    				varComb = new Combinations( varArray, arity );
    			} else {
    				varComb = new Permutations( varArray, arity );
    			}
    		} catch (CombinatoricException e) {
    			e.printStackTrace();
    		}
    	}
    	return varComb;
    }
    
    /**
     * Make a new successor state by adding a new RHS operator with the specified arguments (which
     * are indeed represented as variables). 
     * The validity of the operator invocation is tested using the "seed" binding. The operator
     * invocation is valid when actual values bound to argument variables match the operator 
     * argument type constraints. 
     * 
     * If the operator invocation is valid, then a new Jess Bind clause is added to the child state. 
     * A new binding is made for the "seed" operator invocation and added to the "seed" binding list.
     * The new variable is also added to the free variable list. 
     * 
     * Since this method would vary from an algorithm to an algorithm
     * for a particular search schema, this can't be 'private' so that
     * above private methods can call an appropriate makeRhsChildState()
     * 
     * @param currentState
     * @param rhsOp
     * @param argV			Will be null if called for an rhsOp that takes no argument
     * @return
     */
    RhsState makeRhsChildState(RhsState currentState, UserDefJessSymbol rhsOp, ArrayList<String> argV) {
    	
    	if (argV.isEmpty()) {
    		new Exception("makeRhsChildState(RhState, UserDefJessSymbol, ArrayList<String>) called with an emply array list").printStackTrace();
    	}
    	
    	/*
    	String argStr = "";
    	for (String arg : argV) {
    		argStr += arg;
    	}
    	*/
    	// TraceLog.out("makeRhsChildState: rhsOp: " + rhsOp + ", argV: " + argV);
    	
    	RhsState child = null;
    	RhsState tmpChild = (RhsState)currentState.duplicate();
    	
    	// First, the validity of the operator call (i.e., argument type much) must be validated.
    	Bindings seedBindings = currentState.getSeedVarBindings();
    	boolean validOpInvocation = validateOpInvocation(rhsOp, argV, seedBindings);

    	// Second, only if the operator call is valid, make a child state. 
    	if (validOpInvocation) {
    		
    		tmpChild.removeFreeVarList(argV);
    		
    		JessBindClause newJessBindClause = tmpChild.addJessBindClause(rhsOp, argV);
    		String newVar = newJessBindClause.getVar();
    		
    		ArrayList<String> instArgV = tmpChild.getSeedVarBindings().instantiate(argV);

    		String seedValue = rhsOp.apply(instArgV);
    		// TraceLog.out("calling " + rhsOp + "(" + instArgV + ") returning " + seedValue + ".");
    		if (!seedValue.equals(UserDefJessSymbol.FALSE_VALUE)) {
    			tmpChild.addFreeVarList(newVar);
    			tmpChild.addSeedVarBindings(newVar, seedValue);
    			child = tmpChild;
    		}
    	}
    		
    	return child;
    }
    
	/**
     * Make a new RHS search state for an RHS operator that does not take any argument  
     * @param currentState
     * @param rhsOp
     * @return
     */
    RhsState makeRhsChildState(RhsState currentState, UserDefJessSymbol rhsOp) {
    	
    	RhsState child = (RhsState)currentState.duplicate();
    	
    	child.clearFreeVarList();

    	JessBindClause newJessBindClause = child.addJessBindClause(rhsOp, null);
    	String newVar = newJessBindClause.getVar();
    	
    	String seedValue = rhsOp.apply(null);
    	child.addSeedVarBindings(newVar, seedValue);
    	
    	return child;
    }
    
    
	private boolean validateOpInvocation(UserDefJessSymbol rhsOp, ArrayList<String> argV, Bindings seedBindings) {
		
		boolean isValidArgument = false;
		
		// TraceLog.out("validateOpInvocation: argV = " + argV + ", seedBindings = " + seedBindings);
		
		ArrayList<String> args = seedBindings.instantiate(argV);
		
		try {
			isValidArgument = rhsOp.isValidArgument(args);
		} catch (JessException e) {
			e.printStackTrace();
		}
		
		return isValidArgument; 
	}

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Getters and setters
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	private ArrayList<UserDefJessSymbol> getRhsOpList() {
		return this.rhsOpList;
	}
	/*
	private void setRhsOpList(Vector<UserDefJessSymbol> rhsOpList) {
		this.rhsOpList = rhsOpList;
	}
	*/
	
}
