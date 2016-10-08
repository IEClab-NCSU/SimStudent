package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.Utilities.trace;


/*
 * Input conventions are 
 * key = selection.get(0), value = input.get(0)
 * key = selection.get(0)+".action", value = action.get(0)
 */
public class VariableTable extends LinkedHashMap<String, Object> {
   
	private static final long serialVersionUID = 201403071830L;
	
	//this is a private VariableTableModel used for displaying the variable table to the author in author time
	private transient VariableTableModel model;
	//each newly instantiated VariableTable will receive a unique counter, used in tracing
	private static int count = 0;
	private int instance;
	
	//default constructor because it didn't have one before
	public VariableTable (){
		super();
		if (trace.getDebugCode("vtm")) trace.outNT("vtm", "VariableTable default constructor");
		instance = count++;
		if (trace.getDebugCode("vtm")) trace.outNT("vtm", "VariableTable #"+(instance));
	}
	
	/*
	 * non-default constructor that allows for the use of the VariableTableModel based on 
	 * whether or not it is in student time
	 */
	public VariableTable (boolean isStudentTime){
		super();
		if (trace.getDebugCode("vtm")) trace.outNT("vtm", "VariableTable overloaded constructor");
		instance = count++;
		if (trace.getDebugCode("vtm")) trace.outNT("vtm", "VariableTable #"+(instance));
		//if we're in student time then there is no need to instantiate a VariableTableModel
		//as it is part of the authorTime GUI
		if(isStudentTime){
			model = null;
			if (trace.getDebugCode("vtm")) trace.out("vtm","model is null");
		}
		else{
			model = new VariableTableModel(this);
			if (trace.getDebugCode("vtm")) trace.out("vtm","model is not null");
		}
	}
	
	private VariableTable (VariableTable original){
		super(original);
		instance = count++;
	}
	
    // check to see if input specifies a number
    public static boolean isFloatingPoint(Object input) {
        final String Digits     = "(\\p{Digit}+)";
        final String HexDigits  = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally 
        // signed decimal integer.
        final String Exp        = "[eE][+-]?"+Digits;
        final String fpRegex    = ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
                                   "[+-]?(" + // Optional sign character
                                   "NaN|" +           // "NaN" string
                                   "Infinity|" +      // "Infinity" string

                                   // A decimal floating-point string representing a finite positive
                                   // number without a leading sign has at most five basic pieces:
                                   // Digits . Digits ExponentPart FloatTypeSuffix
                                   // 
                                   // Since this method allows integer-only strings as input
                                   // in addition to strings of floating-point literals, the
                                   // two sub-patterns below are simplifications of the grammar
                                   // productions from the Java Language Specification, 2nd 
                                   // edition, section 3.10.2.

                                   // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                                   "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

                                   // . Digits ExponentPart_opt FloatTypeSuffix_opt
                                   "(\\.("+Digits+")("+Exp+")?)|"+

                                   // Hexadecimal strings
                                   "((" +
                                   // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                                   "(0[xX]" + HexDigits + "(\\.)?)|" +

                                   // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                                   "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                                   ")[pP][+-]?" + Digits + "))" +
                                   "[fFdD]?))" +
                                   "[\\x00-\\x20]*");// Optional trailing "whitespace"
        return (input instanceof String && Pattern.matches(fpRegex, (String)input));
    }

    // check to see if input specifies a number
    private Object evaluateInput(Object input) {
        if (isFloatingPoint(input))
            return Double.valueOf((String)input); // Will not throw NumberFormatException
        else
            return input;
    }

    static final Pattern STUDENT_SELECTION_KEY = Pattern.compile("link[0-9]+\\.selection");
    static final Pattern STUDENT_ACTION_KEY = Pattern.compile("link[0-9]+\\.action");
    static final Pattern STUDENT_INPUT_KEY = Pattern.compile("link[0-9]+\\.input");
    static final Pattern STUDENT_ACTOR_KEY = Pattern.compile("link[0-9]+\\.actor");

    private boolean isStudentSAI(String keyStr) {
        return (STUDENT_SELECTION_KEY.matcher(keyStr).matches() ||
                STUDENT_ACTION_KEY.matcher(keyStr).matches() ||
                STUDENT_INPUT_KEY.matcher(keyStr).matches() ||
                STUDENT_ACTOR_KEY.matcher(keyStr).matches());
    }
    
    public void clearStudentSAI() 
    {
        if (trace.getDebugCode("functions")) trace.outln("functions", "clearStudentSAI");
        Set<String> keys = new HashSet<String>(keySet());
        
        for (String keyStr : keys) 
        {
            /* if (STUDENT_SELECTION_KEY.matcher(keyStr).matches() ||
                STUDENT_ACTION_KEY.matcher(keyStr).matches() ||
                STUDENT_INPUT_KEY.matcher(keyStr).matches() ||
                STUDENT_ACTOR_KEY.matcher(keyStr).matches()) */
            if (isStudentSAI(keyStr))
                remove(keyStr);
        }
        
        if (model!=null)
        	model.updateTableCleared();
    }

    public void clearNotStudentSAI() 
    {
        if (trace.getDebugCode("functions")) trace.outln("functions", "clearNotStudentSAI");
        
        Set<String> keys = new HashSet<String>(keySet());
        
        for (String keyStr : keys) 
        {
            if (!isStudentSAI(keyStr))
                remove(keyStr);
        }
        
        if (model !=null)
        	model.updateTableCleared();
    }

    /**
     * Override superclass to call {@link #get(Object, ProblemModel) get(key, null)}
     * @param key
     * @return value for key; null if none
     * @see java.util.HashMap#get(java.lang.Object)
     */
    public Object get(Object key) {
    	return get(key, null);
    }

    /** Used to detect reference cycles in {@link #get(Object, ProblemModel)}. */
    private transient Set<String> _keyPath;

    /**
     * Retrieve a value from this table or, if suffixed with "authorSelection", "authorAction",
     * etc., from the links in the given {@link ProblemModel}'s graph.
     * @param key variable name
     * @param problemModel used only for author link keys; may be null
     * @return value for variable
     */
    public Object get(Object key, ProblemModel problemModel) {
        if (trace.getDebugCode("functions")) trace.outln("functions", "VariableTable.get(" + key + ")");
        if (containsKey(key))
            return super.get(key);

        if (key instanceof String && problemModel != null) {
            Object value = null;
            
            if (_keyPath==null)
                _keyPath = new LinkedHashSet<String>();
            if (_keyPath.contains(key))
                throw new IllegalStateException("ERROR: reference cycle on variable \"" + key +
                		"\"; path\n " + _keyPath);
            _keyPath.add((String) key);
            if (trace.getDebugCode("functions")) trace.outln("functions", "keypath is " + _keyPath.toString());
            
            String[] path = ((String)key).split("\\.");
            if (path.length>1) {
                String edgeName = path[0];
                String suffix = path[1];
                boolean isAuthorVar = suffix.startsWith("author");

                for (Enumeration<ProblemEdge> edges = problemModel.getProblemGraph().edges();
                		isAuthorVar && edges.hasMoreElements(); ) {
                    EdgeData edge = ((ProblemEdge)edges.nextElement()).getEdgeData();
                    if (edgeName.equals(edge.getName())) {
                        if (suffix.equals("authorSelection")) {
                            value = edge.getMatcher().getDefaultSelection();
                            break;
                        } else if (suffix.equals("authorAction")) {
                            value = edge.getMatcher().getDefaultAction();
                            break;
                        } else if (suffix.equals("authorInput")) {
                            if (trace.getDebugCode("functions")) trace.outln("functions", "Evaluating authorInput on " + edgeName);
                            value = evaluateInput(edge.getMatcher().evaluate());
                            break;
                        }
                    }
                }
            }
            
            _keyPath = null;
            return value;
        }
        

        return null;
    }
    
    public Object put(String key, Object value){
    	if (trace.getDebugCode("vt")) trace.outNT("vt","VariableTable #"+instance+": **KEY** " + key + "**VALUE** " + value);
    	/*return super.put(key, value);
    }*/
    	//this system is necessary to update the VariableTableModel with every new put -Erik
    	boolean replace = this.containsKey(key);
    	Object returnVal = super.put(key, value);
    	if (model != null){
    		//find out the conventions for sending specific cell update messages instead of
    		//generic dataChange messages
    		model.updateTable(replace, key, instance);
    	}
    	return returnVal;
    }
    
    //returns the current VariableTableModel, which exists in this VariableTable
    public VariableTableModel getModel() {
    	return model;
    }
    
    //this should only be called in author time -Erik
    public void setModel(VariableTableModel mod){
    	model=mod;
    	return;
    }
    
    public boolean hasModel(){
    	return model!=null;
    }
    
    /*
     * returns the instance number of this VariableTable,
     * I only used it for tracing purposes but it might have other uses in the future -Erik
     */
    public int getInstance(){
    	return instance;
    }
    
    //basically the superclass call it just needs to inform the TableModel that things have been removed
    public void clear(){
    	if (trace.getDebugCode("vt")) trace.printStack("vt","clear() VariableTable #"+getInstance());
    	super.clear();
    	if(model!=null)
    		model.fireTableDataChanged();
    }
    
    //making sure the remove statement informs the TableModel
    public Object remove(Object key){
    	if (trace.getDebugCode("vt")) trace.outNT("vt", "remove("+key+") VariableTable #"+getInstance());
		Object returnVal=super.remove(key);
    	if (model!= null)
    		model.fireTableDataChanged();
    	return returnVal;
    }
    
    //override of clone that passes on the VariableTableModel to the new copy
    public Object clone(){
    	if (trace.getDebugCode("vt")) trace.printStack("vt","calling clone() on VariableTable #"+getInstance()+" which has "+(model==null? "no TableModel":"TableModel #"+getModel().getInstance()));
    	VariableTable copy = new VariableTable(this);
    	if (trace.getDebugCode("vt")) trace.outNT("vt","returning VariableTable #"+copy.getInstance());
    	return copy;
    }

    
}
