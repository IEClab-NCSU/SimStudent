/*
 * Copyright 2005 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.util.regex.Matcher;

import jess.Context;
import jess.Deftemplate;
import jess.Fact;
import jess.FactIDValue;
import jess.JessException;
import jess.Test1;
import jess.Value;

/**
 * This class holds the variable, its value, serial number.....
 * There will be an instance of this class for each variable
 * and each possible value of the variable
 * 
 * @author sanket@wpi.edu
 */
class VariableBindingNode{

    /** Prefix for references to existing variables, to distinguish their names. */
    static final String REF = "REF_";

    /** Prefix for literals and funcalls, to ensure they're unique. */
    static final String LIT = "LIT_";
    
    /** Pattern to test whether this is a unnamed, i.e. "blank" variable. */
    private static final java.util.regex.Pattern BLANK_VAR_PATTERN =
		java.util.regex.Pattern.compile(RulePrinter.BLANK_VAR);
	
	/** Pattern to test whether this is a LITERAL or FUNCALL. See constructor. */
    private static final java.util.regex.Pattern LIT_PATTERN =
		java.util.regex.Pattern.compile("^"+LIT+"[0-9][0-9]*_(.*)$");
	
	/** Pattern to test whether this is a variable reference. See constructor. */
	private static final java.util.regex.Pattern REF_PATTERN =
		java.util.regex.Pattern.compile("^"+REF+"[0-9][0-9]*_(.*)$");
	
    /**
     * indicates that the variable belongs to slot and not multislot
     */
    public static final int SLOT = 0;
    /**
     * indicates that the slot belongs to Multislot
     */
    public static final int MULTISLOT = 1;
    /**
     * indicates that the variable belongs to a bound fact: a pattern binding
     */
    public static final int BOUND_NAME = 2;
    /**
     * indicates that the value is a template type, not a variable
     */
    public static final int TEMPLATE = 3;
    /**
     * indicates the variable matches  with a single value
     * a variable not preceded with a $ in the pattern
     */
    public static final int MATCHES_SINGLE = 0;
    /**
     * indicates that the variable can match multiple values depending on the fact
     * a variable preceded with a $ in the pattern
     */
    public static final int MATCHES_MULTI = 1;
    /**
     * indicates that the variable is a local variable
     */
//    public static final int LOCAL = -1;
    /**
     * indicates that the value is a literal and not a variable
     */
    public static final int LITERAL = 2;
	/**
	 * 	variable representing a funcall in a pattern
	 */    
    public static final int FUNCALL = 3;
    /**
	 * invalid variable type
	 */
    public static final int BAD_VARIABLE = 4;
    /**
     * The name of the variable
     */
    private String variableName;
    /** Whether this is a unnamed or "blank" variable. */
    private boolean isBlank = false;
    /**
     * The value of the variable
     */
    Value value;
    /**
     * The serial number of the variable from the list of variables in the rule
     */
    int srNo;
    /**
     * The depth of the rule in terms of the pattern in the rule ie the pattern index from the list of patterns
     */
    int depth;
    /**
     * the slot index of the variable
     */
    int slotIndex;
    /**
     * type of variable
     * either MATCHES_SINGLE or MATCHES_MULTI or LITERAL or BAD_VARIABLE
     */
    int typeOfVariable;
    /**
     * type of slot the variable belongs to
     * either SLOT or MULTISLOT or BOUND_NAME or TEMPLATE
     */
    int slotType;
	/**
	 * the sub slot index within the multi slot
	 */
    int subSlotIndex;
	/**
	 * indicates the type of the test can be either Test.EQ or Test.NEQ
	 * @see jess.Test
	 */
    int testType;
    
    Test1 test;

    /**
	 * @param test
	 */
	public void setTest(Test1 test) {
		this.test = test;
	}

	public Test1 getTest(){
		return this.test;
	}
	/**
	 * constructor 
	 * @param vbn - instance of VariableBindingNode
	 * @author sanket
	 */
    public VariableBindingNode(VariableBindingNode vbn){
		setVariableName(vbn.getVariableName());
		this.value = vbn.getVariableValue();
		this.slotIndex = vbn.getSlotIndex();
		this.subSlotIndex = vbn.getSubSlotIndexes();
		this.typeOfVariable = vbn.getVariableType();
		this.srNo = vbn.getSrNo();
		this.testType = vbn.getTestType();
		this.slotType = vbn.getSlotType();
    }
    
    /**
     * Constructor for references to variables. Renames to match
     * {@link #REF_PATTERN} using srNo arg for uniqueness.
     * @param prefix to distinguish references, literals, funcalls; also appends srNo
     * @param varName - name of the variable
     * @param depth - depth of the variable in the rule
     */
    public VariableBindingNode(String prefix, String varName, int depth,
    		int srNo, int index,int subIndex,int slotType, int varType,
			int testType){
    	if (prefix != null)        // keep following expr consistent with REF_PATTERN
    		setVariableName(prefix+Integer.toString(srNo)+"_"+varName);
    	else
    		setVariableName(varName);
		this.depth = depth;
		this.slotIndex = index;
		this.subSlotIndex = subIndex;
		this.srNo = srNo;
		this.slotType = slotType;
		this.typeOfVariable = varType;
		this.testType = testType;
    }

    /**
     * constructor
     * 
     * @param name
     * @param slotType
     */
    public VariableBindingNode(String name,int typeOfVariable,int slotType, int testType){
    	this(null, name, 0, 0, 0, 0, slotType, typeOfVariable, testType);
    }    
    /**
     * Usual constructor.
     * 
     * @param varName - name of the variable
     * @param depth - depth of the variable in the rule
     */
    public VariableBindingNode(String varName, int depth,int srNo, int index,int subIndex,int slotType, int varType, int testType){
		this(null, varName, depth, srNo, index, subIndex,
				slotType, varType, testType);
    }

    public void setVariableName(String name){
		variableName = name;
		isBlank = isBlank(name);
    }
    
    /**
     * Whether the given identifier is an unnamed, i.e. "blank", variable.
     * @param  s identifier to test, with leading ?
     * @return true if s matches {@link #BLANK_VAR_PATTERN}
     */
    public static boolean isBlank(String s) {
		Matcher m = BLANK_VAR_PATTERN.matcher(s);
		return m.matches();
    }
	
    /**
     * Whether this is an unnamed, i.e. "blank", variable.
     * @return {@link #isBlank}
     */
    public boolean isBlank() {
    	return isBlank;
    }
    
    public String getVariableName(){
		return variableName;
    }

    public Value getVariableValue(){
		return value;
    }
    public int getDepth(){
		return depth;
    }
    public int getSrNo(){
		return srNo;
    }
    public int getSubSlotIndexes(){
		return subSlotIndex;
    }
    public int getSlotIndex(){
		return slotIndex;
    }
    public int getSlotType(){
		return slotType;
    }
    public void setDepth(int d){
		this.depth = d;
    }
    public void setValue(Value v){
		this.value = new Value(v);
    }
    public void setSrNo(int s){
		this.srNo = s;
    }
    public String toString(){
		return variableName;
    }
    public int getVariableType(){
		return typeOfVariable;
    }
    public void setSlotIndex(int index){
		this.slotIndex = index;
    }
    public void setSubIndex(int index){
		this.subSlotIndex = index;
    }
    public void setTestType(int t){
    	this.testType = t;
    }
    public int getTestType(){
    	return testType;
    }
    public void setVariableType(int type){
    	this.typeOfVariable = type;
    }
    /**
     * Return the name of the variable addressed by this reference.
     * @return referenced variable name if {@link #isVariableReference()};
     *         otherwise {@link #getVariableName()}
     */
    String getExtVariableName() {
    	Matcher m = REF_PATTERN.matcher(variableName);
    	if (m.matches())            // must init'ze Matcher with matches()
        	return m.group(1);
    	m = LIT_PATTERN.matcher(variableName);
    	if (m.matches())            // must init'ze Matcher with matches()
        	return m.group(1);
    	return getVariableName();
    }
	
    /**
     * Test whether this node is a variable reference.
     * @return true if {@link #variableName} matches {@link 
     */
	boolean isVariableReference() {
    	Matcher m = REF_PATTERN.matcher(variableName);
    	boolean result = m.matches();
    	return m.matches();
	}

	/** For {@link #getTypeOfVariableAsString()}. */
	private static final String[] typeOfVariableNames = {
		"MATCHES_SINGLE",	// 0
		"MATCHES_MULTI",	// 1
		"LITERAL",			// 2
		"FUNCALL",			// 3
		"BAD_VARIABLE"		// 4
	};

	/**
	 * Stringify {@link #typeOfVariable}.
	 * @return name of typeOfVariable or "(unkn TYPE)"
	 */
	public String getTypeOfVariableAsString() {
		int i = typeOfVariable;
		if (0 <= i && i < typeOfVariableNames.length)
			return typeOfVariableNames[i];
		else
			return "(unkn TYPE)";
	}

	/**
	 * Stringify {@link #slotType}.
	 * @return "SLOT", "MULTISLOT" or "(unkn SLOT)"
	 */
	public String getSlotTypeAsString() {
		switch(slotType) {
		case SLOT:
			return "SLOT";
		case MULTISLOT:
			return "MULTISLOT";
		case BOUND_NAME:
			return "BOUND_NAME";
		case TEMPLATE:
		    return "TEMPLATE";
		default:
			return "(unkn SLOT)";
		}
	}

	/**
	 * Dump contents of a node for debugging.
	 * @return one-line string
	 */
	public String dump() {
		String NS = "(null)";
		StringBuffer sb = new StringBuffer("[");
		sb.append("name ").append(variableName == null ? NS : variableName);
		sb.append(" ").append(getTypeOfVariableAsString());
		sb.append(" ").append(getSlotTypeAsString());
		sb.append(", value ").append(value == null ? NS : value.toString());
		sb.append(", srNo ").append(Integer.toString(srNo));
		sb.append(", depth ").append(Integer.toString(depth));
		sb.append(", slot ").append(Integer.toString(slotIndex));
		return sb.append("]").toString();
	}
    
    /**
     * Format a {@link Value} for printing. If the value is a Fact, return the
     * value of its "name" slot if any; then print "<Fact-id>".  Otherwise just
     * return {@link Value val}.toString().
     * @param  val the value to print
     * @return fact name and id if val a Fact; else {@link Value val}.toString()
     */
    String formatValue(Context context) {
        String result = "(null)";
        Value val = getVariableValue();
        if (val == null)
            return result;
        try {
            Fact vbnFact = val.factValue(context);
            Deftemplate dt = vbnFact.getDeftemplate();
            int nameSlotIdx = (dt == null ? -1 : dt.getSlotIndex("name"));
            if (nameSlotIdx < 0)
                result = val.toString();  // no slot "name"
            else
                result = vbnFact.getSlotValue("name") + " " +
                	(new FactIDValue(vbnFact)).toString();
        } catch (JessException e) {
            result = val.toString();  // value is not a Fact
        }
        return result;
    }
	
}

