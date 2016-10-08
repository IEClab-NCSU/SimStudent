/**
 * A container holding a list of variable bindings
 * 
 * Created: Jun 26, 2014 10:15:30 PM
 * @author mazda
 * (c) Noboru Matsuda 2014
 * 
 */
package SimStudent2.LearningComponents;

import java.util.ArrayList;
import java.util.Vector;

import SimStudent2.TraceLog;

/**
 * @author mazda
 *
 */
public class Bindings {
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	private static final String VAR_PRE_FIX = "?";
	private static final String VAR_STEM = "var";
	public static final String FOA_VAR_STEM = "foa";
	public static final String SELECTION_VAR_STEM = "selection";
	public static final String VAL_VAR_STEM = "val";

	private Vector<Binding> bindingList = new Vector<Binding>();
	
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	public Bindings() {}

    /**
     * Make an initial bindings that, by definition, represent FoA values.
     * They are used for evaluation of operator sequence in this order. 
     * The initial bindings do not have variable names, because they
     * will be given (as the name of variables used as arguments of 
     * operator call) when an operator sequence is evaluated. 
     * 
	 * @param foaValues
	 */
	public Bindings(Vector<String> foaValues) {
		
		for (String foaValue : foaValues) {
			addNewBinding(null, foaValue);
		}
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// Initializing bindings for evaluating JessBindSequence with a particular examples
	// 
	
	/**
	 * RhsSearchSuccessorFn proposes JessBindSequence with the seedVarBindings that represent
	 * variable bindings for the "primary" FoA.  For example:
	 *        jessBindSequence: (bind ?val66 (get-operand ?foa61))(bind ?val71 (sub-term ?foa60 ?val66)), 
	 *        seedVarBindingsStr: (?foa60,2x+2)(?foa61,subtract 2)(?val66,2)(?val71,2x)
	 * To evaluate the JessBindSequence with a given example, the chain of variable bindings needs to start
	 * with the same FoA variables (i.e., ?foa60 and ?foa61 in the above case).
	 *  
	 * @param foaValues
	 * @param seedVarBindings
	 */
	public void initializeFoAvar(ArrayList<String> foaValues, ArrayList<String> foaVarNames) {
		
		int idx = 0;
		for (String foaVarName : foaVarNames) {
			
			addNewBinding(foaVarName, foaValues.get(idx++));
		}
	}

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// Variables 
	// 
	
	/**
     * Generate a new variable, which is "?valN" where N is a number maintained by (int)numVar
     * @return
     */
    // Number of variables in the state
	private static int numVar = 0;

    public static String genVarSym() {
    	return genVarSym(VAR_STEM);
    }
    
    public static String genVarSym(String stem) {
    	return VAR_PRE_FIX + stem + String.valueOf( numVar++ );
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
    // Bindings list
    //
    
	/**
	 * @param varName
	 * @param value
	 */
	public void addNewBinding(String varName, String value) {
		
		this.bindingList.add(new Binding(varName,value));
	}
	
	public Bindings clone() {
		
		Bindings clone = new Bindings();
		for (Binding binding : this.bindingList) {
			clone.addNewBinding(binding.getVarName(), binding.getValue());
			
		}

		return clone;
	}
	
	
	/**
	 * @param varName
	 * @return
	 */
	public String lookupValueFor(String varName) {

		String value = null;
		
		for (Binding binding : this.bindingList) {
			
			if (binding.hasVariableName(varName)) {
				value = binding.getValue();
				break;
			}
		}
		
		return value;
	}
	
	/**
	 * @param varList
	 * @return
	 */
	public ArrayList<String> instantiate(ArrayList<String> varList) {
		
		ArrayList<String> args = new ArrayList<String>();
		
		for (String arg : varList) {
			
			String argValue = lookupValueFor(arg);
			args.add(argValue);
		}
		
		return args;
	}
	
	/**
	 * Argument bindings for FoA values initially have "null" as its variable name.
	 * This method find the first bindings in the bindingList that has "null" as its variable name
	 * and replace the "null" with the given arg.
	 * 
	 * @param arg
	 * @return		The value (not varName) of the replaced binding.
	 */
	public String registerFoaValue(String arg) {
		
		String value = null;
		
		for (Binding binding : this.bindingList) {
			
			if (binding.getVarName() == null) {
				binding.setVarName(arg);
				value = arg;
				break;
			}
		}
		return value;
	}
	
	
	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// Output
	// 
	
	public String toString() {
		
		String str = "";
		
		for (Binding binding : this.bindingList) {
			str += binding;
		}
		
		return str;
	}
	
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Getters and Setters
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private Vector<Binding> getBindingList() {
		return bindingList;
	}
	

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// The class representing a single binding
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	class Binding {
		
		//
		// Fields
		//
		private String varName = null;
		private String value = null;

		
		// 
		// Constructor
		//
		Binding(String varName, String value) {
			this.varName = varName;
			this.value = value;
		}
		

		// 
		// Methods
		// 
		public boolean hasVariableName(String theVarName) {
			
			boolean hasVariableName = false;
			
			if (theVarName != null) {
				hasVariableName = theVarName.equals(getVarName());
			}
			return hasVariableName;
		}
		
		public boolean isFoAVar(String varName) {
			
			return (varName != null && varName.startsWith(VAR_PRE_FIX + FOA_VAR_STEM));
		}
		
		public String toString() {
			return "(" + varName + "," + value + ")";
		}
		
		public String getVarName() { return this.varName; }
		public void setVarName(String varName) {
			this.varName = varName;
		}
		public String getValue() { return this.value; }
		
	}


}
