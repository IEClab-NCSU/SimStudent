/**
 * Jess Bind Sequence
 * 
 * A list of JessBindClause each of which represents a pair of variable and expression (bind ?var (operator ...)) used in RHS.
 * The "?var" is the output variable from the operator call
 * 
 * This object is used during an RHS operator search.  
 * This object will be then converted into RhsOperators to compose a Production object. 
 * 
 *
 * Created: July 18 08:26:00 2014
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * 
 * (c) Noboru Matsuda 2014
 *  
 */

package SimStudent2.ProductionSystem;

import java.util.ArrayList;

import jess.JessException;
import SimStudent2.TraceLog;
import SimStudent2.LearningComponents.Bindings;
import SimStudent2.LearningComponents.Example;

/**
 * 
 * @author mazda
 *
 */
public class JessBindSequence implements Cloneable {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	private ArrayList<JessBindClause> jessBindClauseList = new ArrayList<JessBindClause>();
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * 
	 */
	public JessBindSequence() {
	}

	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods 
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// For state expansion used for the successor function
	// 
	
	public JessBindSequence clone() {
		
		JessBindSequence jessBindSequence = new JessBindSequence();
		@SuppressWarnings("unchecked")
		ArrayList<JessBindClause> jessBindClauses = (ArrayList<JessBindClause>)getJessBindClauseList().clone();
		jessBindSequence.setJessBindClauseList(jessBindClauses);
		
		return jessBindSequence;
	}
	
	
	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// Validating the consistency of the operator sequence against an example
	// 
	
	public String apply(Example example, ArrayList<String> foaVarNames) {
		
		// RhsSearchSuccessorFn proposes JessBindSequence with the seedVarBindings that represent 
		// variable bindings for the "primary" FoA.  For example:
		//          jessBindSequence: (bind ?val66 (get-operand ?foa61))(bind ?val71 (sub-term ?foa60 ?val66)), 
		//          seedVarBindingsStr: (?foa60,2x+2)(?foa61,subtract 2)(?val66,2)(?val71,2x)
		// To evaluate the JessBindSequence with a given example, the chain of variable bindings needs to start
		// with the same FoA variables (i.e., ?foa60 and ?foa61 in the above case). 
		Bindings initialBindings = new Bindings();

		initialBindings.initializeFoAvar(example.getFoaValues(), foaVarNames);
		
		// TraceLog.out("foaVarNames: " + foaVarNames);
		// TraceLog.out("applyJessBindSequenceTo: initialBindings = " + initialBindings);
		// TraceLog.out("JessBindSequence: " + this);
		
		String returnValue = apply(initialBindings);
		return returnValue;
	}


	/**
     * Iteratively call a sequence of operator starting with the FoA in the given example
     *  
	 * @param example	An example contains FoA
	 * @return			Null if an operator invocation is invalid (likely due to an argument type mismatch)
	 */
	public String apply(Bindings bindings) {
		
		String returnValue = null;
		
		// jessBindSequence: (bind ?val66 (get-operand ?foa61))(bind ?val71 (sub-term ?foa60 ?val66)), seedVarBindingsStr: (?foa60,2x+2)(?foa61,subtract 2)(?val66,2)(?val71,2x) <SimStudent2.LearningComponents.RhsSearchSuccessorFn:184>main
		// 
		for (JessBindClause jessBindClause : getJessBindClauseList()) {
			
			try {
				// eval() should return null if the operator invocation is invalid (e.g., wrong argument type)
				returnValue = jessBindClause.eval(bindings);
			} catch (Exception e) {
				e.printStackTrace();
				TraceLog.out("******** bindings: " + bindings);
			}
			
			if (returnValue != null && !returnValue.equals(UserDefJessSymbol.FALSE_VALUE)) {
			
				String varName = jessBindClause.getVar();
				bindings.addNewBinding(varName, returnValue);
			
			} else {
			
				// The operator invocation is not valid hence an exception must be thrown
				// new Exception("Illegal operator invocation").printStackTrace();
				break;
			}
		}
		
		return returnValue;
	}
	
	
	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// Maintaining the list of Jess Bind Clauses 
	// 
	
	/**
	 * Returns the most recently added JessBindClause (i.e., last element, by definition, in the vector) 
	 * 
	 */
	public JessBindClause lastJessBindClause() {

		return getJessBindClauseList().get(this.size()-1);
	}

	/**
	 * Make a new JessBindClause and add it to the list
	 * 
	 * @param op
	 * @param argV
	 */
	public JessBindClause addJessBindClause(UserDefJessSymbol op, ArrayList<String> argV) {
		
		JessBindClause jessBindClause = new JessBindClause(op, argV);
		getJessBindClauseList().add(jessBindClause);
		
		return jessBindClause;
	}
	
	/**
	 * @return the Jess Bind expression, i.e., "(bind " + getExpVar(op) + " " + actionStr(argV) + ")";
	 */
	public String getLastActionString() {
		
		JessBindClause jessBindClause = lastJessBindClause();
		return jessBindClause.toString();
	}

	/**
	 * @return	The search depth, which is the size of the JessBindSequence vector
	 */
	public int size() {
		return getJessBindClauseList().size();
	}

	
	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// Printing out the list of Jess Bind Clauses 
	// 

	public String toString() {
		
		String str = "";
		
		for (JessBindClause jessBindClause : getJessBindClauseList()) {
			str += jessBindClause;
		}
		return str;
	}
	
	/**
	 * @return
	 */
	public String toStringForProduction() {
		
		StringBuilder sb = new StringBuilder();
		
		for (JessBindClause jessBindClause : getJessBindClauseList()) {
			sb.append(jessBindClause.toString() + "\n");
		}
		
		return sb.toString();
	}

	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Getters and setters
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * @param jessBindClauses
	 */
	private ArrayList<JessBindClause> getJessBindClauseList() {
		return jessBindClauseList;
	}

	private void setJessBindClauseList(ArrayList<JessBindClause> jessBindClauses) {
		this.jessBindClauseList = jessBindClauses;
	}

	// - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - * - *  - * - * - *  - * - * - * 
	// Class JessBindClause  * - * - * - * - * - * - * - * - * - * - * - * - *  - * - * - *  - * - * - * 
	// - * - * - * - * - * - *	
	// - * - * - * - * - * - *  An individual Jess Bind expression 
	// - * - * - * - * - * - *  I.e., (bind ?var (operator ...))
	// - * - * - * - * - * - *
	
	public class JessBindClause {
	
		// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
		// Fields 
		// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
		
		// The "?var" part of the Jess bind expression, i.e., the output of the operator call, which 
		// is an input for a future operator call
		private String var = null;
		
		// The predicate expression following the "?var"
		private UserDefJessSymbol operator = null;
		
		// The list of arguments for the operator invocation
		private ArrayList<String> argV = null;
		
		// Type of the "?var"
		// private int argType = -1;
		
		// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
		// Constructor
		// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
		
		// A (bind ?var (expression ...)) statement in RHS.
		// 
		// Example: (bind ?var (skill-subtract ?val2))
		// 
		private JessBindClause(UserDefJessSymbol predicate, ArrayList<String> argV) {
			setVar(Bindings.genVarSym(Bindings.VAL_VAR_STEM));
			setOperator(predicate);
			setArgV(argV);
			// setArgType(predicate.getReturnValueType());
		}
		
		// This BindPair instance is created for a FoA value, which is not associated with any predicates 
		/*
	    public BindPair(String var, int argType) {
		  setVar( var );
		  setArgType(argType);
	    }
		*/
		
		// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
		// Methods
		// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
		
		/**
		 * Given a bindings (that has been created so far), evaluate the operator call for this instance of Jess Bind Clause
		 * 
		 * @param bindings
		 * @return
		 */
		public String eval(Bindings bindings) throws JessException {
			
			String result = null;
			
			UserDefJessSymbol op = getOperator();
			
			// If the operator does not take any argument, then simply call it
			if (getArgV() == null) {
				
				result = op.apply(null);

			// otherwise, compose the argument list...
			} else {
			
				ArrayList<String> boundArgV = new ArrayList<String>();
				// Instantiate arguments
				for (String arg : getArgV()) {
					
					// boundArg == null means that the argument must be an FoV value, which by definition
					// in the bindings has "null" as the variable name
					String boundArg = bindings.lookupValueFor(arg);
					if (boundArg == null) {
						bindings.registerFoaValue(arg);
						boundArg = bindings.lookupValueFor(arg);
					}
					boundArgV.add(boundArg);
				}
				
				// - - - - -
				/*
			    String opCall = op.toString() + "(";
			    for (String arg : boundArgV) {
				    opCall += arg + ", ";
			    }
			    opCall = opCall.substring(0, opCall.length()-2) + ")";
			    TraceLog.out("eval: " + opCall);
				*/
				// - - - - - - - -
				
				if (op.isValidArgument(boundArgV)) {
					// trace.out("Calling " + op);
					result = op.apply(boundArgV);
				}
			}
			
			return result;
		}
		

		/**
		 * Generates a string representing a Jess expression
		 */
		public String toString() {
			
			String argV = "";
			
			if (getArgV() != null) {
				for (String arg : getArgV()) {
					argV = argV + " " + arg;
				}
			}
			
			return "(bind " + getVar() + " (" + getOperator().getName() + argV + "))" ;
		}
		
		// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
		// Getters and Setters
		// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
		
		public String getVar() { return this.var; }
		void setVar( String var ) { this.var = var; }
		
		public UserDefJessSymbol getOperator() { return this.operator; }
		void setOperator( UserDefJessSymbol predicate ) { this.operator = predicate; }
		
		private ArrayList<String> getArgV() {
			return argV;
		}
		private void setArgV(ArrayList<String> argV) {
			this.argV = argV;
		}
		
		/*
        void setArgType(int argType) { this.argType = argType; }
        public int getArgType() { return argType; }
		*/
	}


}