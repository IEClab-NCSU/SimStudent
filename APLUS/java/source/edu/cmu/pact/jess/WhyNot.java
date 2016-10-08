package edu.cmu.pact.jess;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolTip;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import jess.Activation;
import jess.ConditionalElement;
import jess.Context;
import jess.Defrule;
import jess.Deftemplate;
import jess.Fact;
import jess.FactIDValue;
import jess.Funcall;
import jess.Group;
import jess.JessException;
import jess.Pattern;
import jess.PrettyPrinter;
import jess.RU;
import jess.Rete;
import jess.Test1;
import jess.Token;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Log.AuthorLogListener;
import edu.cmu.pact.Utilities.EventLogger;
import edu.cmu.pact.Utilities.trace;

/**
 * This class finds out why a rule did not fire for a given set of WME's
 * 
 * @author sanket@wpi.edu
 */
public class WhyNot extends JFrame implements ListSelectionListener, ActionListener {

	private static final long serialVersionUID = 1334042918027485168L;
	private static final String REQUIRED_VALUE_WAS = ", the required value was ";
    private static final String FOR_THE_VARIABLE_OR_EXPRESSION = "For the variable or expression ";
    private static final String ALL_VARIABLES_BOUND_SUCCESSFULLY = "LHS matched.";
    /**
	* main text area displaying the contents of the rule
	*/
	JTextArea ruleDisplay;
	/**
	* summary statement for currently selected instantiation
	*/
	JLabel summary;
	/**
	* table to display variable bindings
	*/
	JTable varTable;
	/**
	* table to display selection/action/input
	*/
	JTable saiTable;
	/**
	* list of instantiations
	*/
	JList instList;
	/**
	* column headers for varTable and saiTable
	*/
	Vector saiColumnNames;
	/**
	* list of currently active filters
	* each element is a Vector with the following elements:
	* [0] - JPanel containing filter
	* [1] - variable pull-down
	* [2] - value pull-down
	* [3] - apply/remove JButton
	*/
	Vector filters;
	/**
	* as above, but for the filter being specified by the user
	*/
	Vector newFilter;
	/**
	* area at the bottom of the interface containing the filter panels
	*/
	Box filterBox;
	/**
	* scrollpane for filterBox
	*/
	JScrollPane filterScroll;

	/**
	* list of colors for variable bindings
	*/
	Color colors[];

    /**
     * the name of the rule to be debugged
     */
    String ruleName;
    /**
     * The Defrule that is to be debugged
     */
    Defrule rule;

    //Gustavo 8 May 2007
    public Defrule getRule(){
        return rule; 
    }

    /**
     * the list of all current WME's 
     */
    ArrayList facts;

    /**
     * list of all the variables in the rule
     */
    ArrayList vMap = new ArrayList();
    /**
     * list of list of facts from the current WM for each pattern
     */
    ArrayList factsForPatterns = new ArrayList();
    /**
     * list of patterns of this rule. FIXME: should be a tree!!
     */
    ArrayList patterns = new ArrayList();
    /**
     * the list of current values of all the variables in the rule
     */
    ArrayList currentValues = new ArrayList();
    /**
	 * the list of conditional tests on the variables
	 */
	ArrayList conditionalList = new ArrayList();
    /**
     * this is a list of VariableBindingNodes that have not yet been expanded
     */
    Stack open = new Stack();

	Context context;

    /**
     * Text Area in which the rule instantiations have to be printed
     */
    JTextArea textArea;
	/**
	 * required selection action input string
	 */   
    Vector reqSAI;
	/**
	 * actual selection action input string in case the rule can be fired
	 */   
    Vector actualSAI;
	/**
	* actualSAI as determined by the node in the conflict tree (in case we can't determine the actualSAI)
	*/
	Vector nodeSAI;
	/**
	 * list of rule instantiations
	 */    
    ArrayList resultsList = new ArrayList();
	/**
	* list of instantiation result summary statements
	*/
	ArrayList summaries = new ArrayList();
	/**
	* current filtered list of instantiations
	*/
	ArrayList resultsListFiltered = new ArrayList();
	/**
	* current filtered list of summary statements
	*/
	ArrayList summaryListFiltered = new ArrayList();
	/**
	 * List of variable bindings for the instantiation currently displayed.
	 */
	VBNTableModel vbnTableModel;
        
        //Gustavo 26 April 2007
        public VBNTableModel getVbnTableModel(){
            return vbnTableModel;            
        }
        
	/**
	* row in the instantiation table containing an error
	*/
	int errorRow = -1;
	/**
	* row in the instantiation table containing the variable currently being hovered over
	* in the rule display
	*/
	int mouseOverRow = -1;
    
	ArrayList index_depthList = new ArrayList();    
	ArrayList index_varsList = new ArrayList();
	
	MTRete rete;
	
	static String SUCCESS = "SUCCESSFUL MATCH OF THE LHS OF THE RULE";
	static String FAILURE = "FAILED TO SUCCESSFULLY MATCH THE LHS OF THE RULE";
	
	boolean stopWhyNot = false;
	boolean showFull = true;
    
    /** If nonempty, reason(s) why we cannot provide Activation information. */
	private java.util.List analysisErrors = new LinkedList();
	
	/** {@link WhyNot.RuleParser} instance for this rule. */
	private RulePrinter rulePrinter = null;   // will create if needed
	
	/** The {@link edu.cmu.pact.jess.WhyNot.RuleAgenda} for this instance. */
	RuleAgenda ruleAgenda = new RuleAgenda(null, null);
	
	/** Logger for author time events. */
	private EventLogger eventLogger;
	
	/**
	 * Represents a {@link Rete} activation list (agenda) for single rule.
	 * This class resembles a read-only set.
	 */
	private class RuleAgenda {

	    /** Set of Sets of Facts. */
	    private Set factSetsSet = new HashSet();
	    
	    /** For debug printing. */
	    private String agendaDump = "(empty)";
	    
	    /**
	     * Grab the {@link Activation} list from the Rete and filter out
	     * entries that 1) are inactive or 2) aren't for this rule. 
	     * @param r the Rete whose agenda we'll get
	     * @param ruleName the single rule we're interested in 
	     */
	    RuleAgenda(Rete r, String ruleName) {
	        if (r == null || ruleName == null)
	            return;
	        int count = 0;
            StringBuffer sb = new StringBuffer("  ");
	        try {
	            Iterator agenda = r.listActivations();  // ?current module only
	            while (agenda.hasNext()) {
	                Activation a = (Activation) agenda.next();
	                if (a.isInactive())
	                    continue;
	                Defrule rule = a.getRule();
	                if (!ruleName.equals(rule.getName()))
	                    continue;
	                Set factSet = new HashSet();
	                Token token = a.getToken();
	                if (++count > 1)           // newline between Token strings
	                    sb.append("\n  ");
	                sb.append(a.toString());
                    sb.append("\n    ").append(token.toString());
	                for (int i = 0; i < token.size(); ++i) {
	                    factSet.add(token.fact(i));
	                }
	                factSetsSet.add(factSet);
	            }
	        } catch (Exception e) {
	            analysisErrors.add("Cannot list activations: " + e.toString());
	            e.printStackTrace();
	        }
	        if (size() > 0)
	            agendaDump = sb.toString();
	        if (trace.getDebugCode("mt")) trace.outNT("mt", "new RuleAgenda:\n"+this.toString());
	    }
	    
	    /**
	     * Return the number activations in this set.
	     * @return {@link #factSetsSet}.size()
	     */
	    public int size() {
	        return factSetsSet.size();
	    }
	    
	    /**
	     * Return the tokens in these activations.
	     * @return {@link #tokenStrings}.toString()
	     */
	    public String toString() {
	        return agendaDump;
	    }
	}
	
    /**
     * the constructor for the class
     * 
     * @param defruleName The rule name that did not fire
     * @param facts   The set of WME's
     * @param defrulesMap a map of defrules with key rule name, value Defrule
     * @param eventLogger for logging author events
     */
    public WhyNot(String defruleName, ArrayList facts, Map defrulesMap, EventLogger eventLogger){
		super("Why not?: " + defruleName);
		ruleName = defruleName;
		if (trace.getDebugCode("mt")) trace.outNT("mt", "WhyNot("+defruleName+") before cloneFactsList(): "+facts);
		this.facts = facts;
		this.eventLogger = eventLogger;
//		this.facts = new ArrayList();
//		MTRete.cloneFactsList(facts, this.facts);
//		trace.outNT("mt", "after  cloneFactsList(): "+this.facts);
		rule = (Defrule) defrulesMap.get(defruleName);

        Image image = new ImageIcon("ctaticon.png").getImage();
        
        if (image != null && image.getHeight(null) != -1)
            setIconImage(image);

		stopWhyNot = false;
		colors = new Color[8];
        colors[0] = new Color(226, 229, 159);
        colors[1] = new Color(223, 212, 219);
        colors[2] = new Color(249, 223, 121);
        colors[3] = new Color(204, 229, 162);
        colors[4] = new Color(167, 230, 196);
        colors[5] = new Color(189, 208, 238);
        colors[6] = new Color(228, 210, 231);
        colors[7] = new Color(235, 198, 211);
        
        addWindowListener(new AuthorLogListener(getEventLogger()));
    }
    
//    public String parseRuleName(String ruleName){
//        
//    }
    

    //Gustavo 3 May 2007: constructs a WhyNot for the given RuleActivationNode
    public static WhyNot makeWhyNot(RuleActivationNode ran, MTRete mtRete, BR_Controller brController) {

        
        MTRete wnRete;
        wnRete = new MTRete(brController.getEventLogger(), null);  // else use node Rete  
        String errMsg = null;
        if (!ran.isRoot() || ran.getChildCount() > 0)
                errMsg = ran.loadPriorState(wnRete);
        else
                errMsg = RuleActivationTree.copyRete(mtRete, wnRete);
        

        ArrayList currentState = new ArrayList();
        for(Iterator fi = wnRete.listFacts(); fi.hasNext(); )
                currentState.add(fi.next());

        String rule = ran.getName();
//        System.out.println("constructing WhyNot with rule = " + rule + ", getRuleBaseName(rule) = " + getRuleBaseName(rule));
        WhyNot wn = new WhyNot(rule, currentState, wnRete.allRulesMap(),
                wnRete.getEventLogger());     
        

        Vector reqSAI = brController.getRuleActivationTree().getReqSAI(ran, true);
        Vector actualSAI = brController.getRuleActivationTree().getActualSAI(ran, true, rule);

        wn.setReqSAI(reqSAI);
        wn.setNodeSAI(actualSAI);
        wn.setRete(wnRete);

//      missing a way to get to the CTAT_Controller
        //MT mt = new MT(simSt.getBrController().getCtatFrameController()tBR_Frame().getCtattCtatFrameController(), wnRete);

        MT mt = brController.getModelTracer().getRete().getMT();
        WMEEditor wmeeditor = new WMEEditor(wnRete, mt, null, true, true);
        wn.reasonOut(wmeeditor);

        return wn;
    }


    /**
     * Override to free resources.
     */
    public void dispose() {
    	

    	
    	super.dispose();
    }
    
    public void setRete(MTRete r){
    	this.rete = r;
    	this.context = r.getGlobalContext();  //FIXME : push a new context?
    	ruleAgenda = new RuleAgenda(r, ruleName);
    }

	public void setShowFull(boolean _sf){
		showFull = _sf;
	}
    
    /**
     * this method does preprocessing on the rule like extracting the 
     * variables 
     */
    public void doPreprocessing(){

		getPatterns(rule);
		Iterator it = patterns.iterator();
		getVariables(rule);
		while (it.hasNext()) {
			factsForPatterns.add(factsForPattern(((Pattern)it.next()).getName(),facts));
		}
    }

	/**
	 * Method getRuleInstantiations.
	 * @param vbn 
	 */
    public void getRuleInstantiations(VariableBindingNode vbn, ArrayList tempList){
		Stack localOpen = new Stack();
		int patternIndex = vbn.getDepth();
		if (trace.getDebugCode("mt")) trace.outNT("mt", "instantiations for "+vbn.dump());
		boolean[] wasPattern = {false};
		if (tempList == null)
			tempList = new ArrayList((ArrayList) factsForPatterns.get(patternIndex));
		int oldStackDepth = localOpen.size();
		int n = getVariableValues(vbn,tempList,localOpen,wasPattern);
		int newStackDepth = localOpen.size();
		if (n != newStackDepth - oldStackDepth)
			if (trace.getDebugCode("mt")) trace.outNT("mt", "getVariableValues("+vbn.dump()+","+tempList+
					","+localOpen+") returns n="+n+" while stackDepthDiff="+
					(newStackDepth - oldStackDepth));
		n = newStackDepth - oldStackDepth;
    	if (n <= 0){
    		return;
    	}
    	
		if (trace.getDebugCode("mt")) trace.outNT("mt", "getVariableValues() rtns n "+n+", stack depth "+
		        localOpen.size()+", currentValues size "+
		        (currentValues==null?"(null)":Integer.toString(currentValues.size())));
    	
    	while (!localOpen.empty() && !stopWhyNot){
    		// for all the variables that can have certain values together not in isolation
    		for (int j = 0; j < n; j++){
    			// FIXME why replace the arg vbn?
    			TestResult testResult = (TestResult) localOpen.pop();
    			int nVars = 0;
				boolean assignOk = true;
    			if (testResult.isList()) {
    				java.util.List multiVar = testResult.vbnList;
    				for (Iterator it = multiVar.iterator(); assignOk && it.hasNext(); ) 
    					assignOk = assignCurrentValue((VariableBindingNode) it.next());
    				nVars = multiVar.size();
    			}else{
    				vbn = (VariableBindingNode) testResult.vbn;
    				if (trace.getDebugCode("mt")) trace.outNT("mt", "wasPattern "+wasPattern[0]+
    						", localOpen.pop("+j+") gets vbn "+vbn.dump());
    				assignOk = assignCurrentValue(vbn);
    				nVars = 1;
    			}
    			if (assignOk)  // if (!wasPattern[0])
    				advanceOrEndInstantiation(vbn, nVars, patternIndex,
    						new ArrayList(testResult.factList));
    		}
//			if (wasPattern[0])
//				advanceOrEndInstantiation(vbn, n, patternIndex, new ArrayList(tempList));
    	}
    }
    
    /**
     * Put the value of the given {@link VariableBindingNode} into the
     * {@link #currentValues} list and set it in the context.
	 * @param vbn node with value to set
	 * @return true if the assignment was successful
	 */
	private boolean assignCurrentValue(VariableBindingNode vbn) {
		int i = containsVariable(0,currentValues,vbn.getVariableName());
		// check to see if the variable's already been stored in CurrentValues list
		// if yes then set change the value of the variable to the new value
		// FIXME: ?replace only if a variable--not a funcall or literal
		if (i != -1) {
			// replace the current value of the variable with the new 
			// value ie replace the value of the variable in the currentValues 
			// with the value of the variable in vbn
			VariableBindingNode oldVbn = (VariableBindingNode) currentValues.get(i);
			if (trace.getDebugCode("mt")) trace.outNT("mt", "pop() replaces currentValues["+i+
					"] oldVbn "+oldVbn.dump());
			currentValues.remove(i);
			currentValues.add(i,vbn);
		}else{
			// else if the variable is not present in the currentValues list then add it to the list of CurrentValues
			currentValues.add(vbn);
		}
		if (vbn.getVariableType() == VariableBindingNode.LITERAL ||
				vbn.getVariableType() == VariableBindingNode.FUNCALL ||
				vbn.isVariableReference())
			return true;                   // no need to assign
		try {
			String vbnName = vbn.getExtVariableName();
			Value vbnVal = vbn.getVariableValue();
			if (trace.getDebugCode("mt")) trace.outNT("mt", "assignCurrentValue: about to setVariable("+
					vbnName+","+vbnVal+")");
			context.setVariable(vbnName, vbnVal);
			return true;
		} catch (JessException je) {
			String errMsg = "Error setting value of "+vbn.getExtVariableName()+
					" in Rete";
			analysisErrors.add(errMsg+": "+je);
			System.err.println(errMsg+"; vbn "+vbn.dump());
			je.printStackTrace();
			return false;
		}
	}

	/**
     * Decide whether to advance work on this instantiation to the next
     * variable or end it here.  This method is logically part of 
     * {@link #getRuleInstantiations(VariableBindingNode)}; if it decides
     * to advance, it recurses to that method.
     * @param  vbn vbn on which we're currently working
     * @param  n number of VariableBindingNodes by which to advance
     * @param  patternIndex index of pattern we're processing now
     * @param  tempList list of Facts for pattern
     */
    private void advanceOrEndInstantiation(VariableBindingNode vbn, int n,
    		int patternIndex, ArrayList tempList) {
    	
    	if (trace.getDebugCode("mt")) trace.outNT("mt", "advanceOrEndInstantiation() n="+n+
    			"after replace or add currentValues.size() "+
    			(currentValues==null?"(null)":Integer.toString(currentValues.size())));
    	// all the variables are numbered in the order of appearance in the rule
    	// remove all the elements from the currentValues that have srNo > the srNo of 
    	// the currently selected variable
    	// FIXME (fixed?) why include n in SrNo test? was > (vbn.getSrNo() + n - 1)
    	// 
    	ListIterator lit = currentValues.listIterator();
    	while (lit.hasNext()) {
    		if (((VariableBindingNode)lit.next()).getSrNo() > (vbn.getSrNo() + n - 1)) {
    			lit.remove();
    		}
    	}
    	lit = null;
    	if (trace.getDebugCode("mt")) trace.outNT("mt", "after remove vars w/ srNo too high currentValues.size() "+
    			(currentValues==null?"(null)":Integer.toString(currentValues.size()))+
				", rtn SUCCESS if vbn.getSrNo "+vbn.getSrNo()+"==vMap.size "+
				vMap.size()+"-1");
    	// check if this is the last variable in the variable table
    	// if so then print the rule instantiations
    	// then it seems that the rule can be instantiated successfully
    	if (vbn.getSrNo() >= vMap.size() - 1) {
    		printRuleInstantiations(-1,null,SUCCESS);
    		return;
    	}
    	else{
    		// get the next variable from the variable list and extract their values
    		// check to see if there is atleast one variable remaining in the list
    		// FIXME (fixed?) was k = vbn.getSrNo() + n;
    		int k = vbn.getSrNo() + n;
    		if (trace.getDebugCode("mt")) trace.outNT("mt", "k="+k+"=vbn.getSrNo()+n "+n+
    				"; recurse if k<vMap.size()="+vMap.size()+
    				" && !(stopWhyNot="+stopWhyNot+"); else SUCCESS");
    		if(k < vMap.size() && !stopWhyNot){
    			vbn = (VariableBindingNode)vMap.get(k);
    			if (vbn.getDepth() == patternIndex)
    				getRuleInstantiations(vbn, tempList);
    			else
    				getRuleInstantiations(vbn, null);
    		}else{
    			printRuleInstantiations(-1,null,SUCCESS);
    			return;
    		}
    	}
    }

    /**
     * This method finds the facts for a pattern
     * 
     * @param patternName
     *               The pattern Name for which the facts have to be found
     * @param currentState The list of facts representing the current state
     * @return A List of facts for the pattern 
     */
    
    public ArrayList factsForPattern(String patternName, ArrayList currentState){
		ArrayList ffp = new ArrayList();
		Fact fact;
		Iterator it = currentState.iterator();
		StringBuffer traceSb = new StringBuffer("factsForPattern "+patternName);
		while (it.hasNext()) {
		    fact = (Fact)it.next();
		    if (fact.getName().equals(patternName)) {
				ffp.add(fact);
				traceSb.append("\n  ").append(Integer.toString(fact.getFactId()));
				traceSb.append(" ").append(fact.toString());
		    }
		}
		if (trace.getDebugCode("mt")) trace.outNT("mt",traceSb.toString());
		return ffp;
    }
    
    /**
     * This method is used to get the list of {@link #patterns} for a rule.
     * @param rule The rule whose patterns are to be found
     */
    public void getPatterns(Defrule rule){
		ConditionalElement ce;
                System.out.println("getPatterns: rule = " + rule);
		ce  = (ConditionalElement) rule.getConditionalElements();
		addPattern(ce);
    }

    /**
     * Add a single pattern to {@link #patterns}. This method recurses if
     * {@link ConditionalElement#isGroup()} returns true.
     * @param  ce the {@link Pattern} or {@link Group} to add
     */
	public void addPattern(ConditionalElement ce){
		ConditionalElement conditionalElement;
		if(ce.isGroup()){
			Group group = (Group)ce;
			int size = group.getGroupSize();
			for(int i = 0; i < size; i++){
				conditionalElement = group.getConditionalElement(i);
				addPattern(conditionalElement);
			}
		}else{
			patterns.add(ce);  // FIXME: patterns should be a tree!!!
		}
	}

	/**
	 * Create a {@link VariableBindingNode} instance for a pattern-binding
	 * variable or a pattern template test.  This method will create at most
	 * 2 nodes: if the pattern has a bound name and no variable with that
	 * name aleady exists, creates a BOUND_NAME node.  If the pattern has a
	 * deftemplate, a TEMPLATE node will also be created.  Test and slot 
	 * info in the node will be undefined. 
	 * @param  pattern no-op if no boundName
	 * @param  patternIndex index of pattern in rule
	 * @param  vMap list to which to add new node; no-op if in list already 
	 * @return last node created
	 */
    private VariableBindingNode makeVBNforTemplate(Pattern pattern,
            int patternIndex, ArrayList vMap){
        VariableBindingNode result = null;
        String boundName = pattern.getBoundName();
        if (boundName != null) {
            int i = containsVariable(0, vMap, boundName);
            VariableBindingNode oldVbn =
                (i < 0 ? null : (VariableBindingNode) vMap.get(i));
            if (trace.getDebugCode("mt")) trace.outNT("mt", "makeVBNforTemplate() oldVbn at vMap["+i+"] "+
                    (oldVbn != null ? oldVbn.dump() : null));
            if (oldVbn == null) {      // this var has not appeared previously
                VariableBindingNode vbn = new VariableBindingNode(boundName,
                        patternIndex,vMap.size(),-1,-1,
                        VariableBindingNode.BOUND_NAME,
                        VariableBindingNode.MATCHES_SINGLE,Test1.EQ);
                vMap.add(vbn);
    	        if (trace.getDebugCode("mt")) trace.outNT("mt", "new vbn for boundName at vMap["+
    	                (vMap.size()-1)+"] "+vbn.dump());
    	        result = vbn;
            }
        }
        try {      // first check to see whether this is a conditional element
            Deftemplate dt = pattern.getDeftemplate();
            String templateName = (dt != null ? dt.getName() : null);
            String templateBaseName = (dt != null ? dt.getBaseName() : null);
            Userfunction uf = rete.findUserfunction(templateName);
            Userfunction ubf = rete.findUserfunction(templateBaseName);
            String ufName = (uf != null ? uf.getName() : null);
            String ubfName = (ubf != null ? ubf.getName() : null);
            int s = pattern.getNSlots();
            int t = (s > 0 ? pattern.getNTests(0) : -99);
            Test1 test = (s > 0 && t > 0 ? pattern.getTest(0,0) : null);
            Value testValue = (test != null ? test.getValue() : null);
            if (trace.getDebugCode("mt")) trace.outNT("mt", "makeVBNforTemplate(): template="+templateName+
                    " templateBase="+templateBaseName+" uf="+ufName+", ubf="+
                    ubfName+" NSlots="+s+", NTests[0]="+t+
                    ", test[0,0] "+test+", valueType "+
                    (testValue!=null ? RU.getTypeName(testValue.type()):null));
            if (testValue != null && testValue.type() == RU.FUNCALL)
                return result;  // this a conditional element: no template test
            
            Value val = new Value(templateName, RU.STRING);
            VariableBindingNode vbn = new VariableBindingNode(templateName,
                    patternIndex,vMap.size(),-1,-1,
                    VariableBindingNode.TEMPLATE,
                    VariableBindingNode.MATCHES_SINGLE,Test1.EQ);
            vbn.setValue(val);
            vMap.add(vbn);
	        if (trace.getDebugCode("mt")) trace.outNT("mt", "makeVBNforTemplate(): new vbn at vMap["+
	                (vMap.size()-1)+"] "+vbn.dump());
            result = vbn;
        } catch (JessException je) {
            analysisErrors.add("Error adding pattern-template test: "+je);
        }
        return result;
    }
	
    /**
     * This method extracts the variable names from the rules.
     * Its output is a populated {@link #vMap}.
     * @param rule 	The rule whose variables have to be found
     */
    public void getVariables(Defrule rule){
		
		int patternIndex;
		Iterator it = patterns.iterator();
		Test1 test;
		StringBuffer sb;
		int slotType, testType;
		// for each pattern
		while (it.hasNext()) {
		    Pattern p = (Pattern)it.next();
		    patternIndex = patterns.indexOf(p);
		    
		    String patternString = (new PrettyPrinter(p)).toString();
		    Deftemplate template = p.getDeftemplate();
		    if (trace.getDebugCode("mt")) trace.outNT("mt", "getVariables() top: p"+patternIndex+": \""+
		            patternString+"\", template "+template.getName()+", NSlots"+
		            p.getNSlots());
		    if (template == null) { // null for (test), other conditional elements?
		        analysisErrors.add("Cannot get deftemplate for pattern:\n  " +
		                patternString);
		        continue;
		    }
		    makeVBNforTemplate(p, patternIndex, vMap);
		    
		    // get the variables from the patterns and give an ordering for the variables in the rule
		    // the ordering is same as the order in which they appear in the rule
		    for (int j = 0; j < p.getNSlots(); j++){
				try{
				    if (template.getSlotType(j) == RU.SLOT) { 
						slotType = VariableBindingNode.SLOT;
				    }else{
				    	slotType = VariableBindingNode.MULTISLOT;
				    }
				    if (trace.getDebugCode("mt")) trace.outNT("mt", "getVariables(): p"+patternIndex+"s"+j+
				            " templateSlotType="+RU.getTypeName(template.getSlotType(j))+
				            " slotType="+
							(slotType==VariableBindingNode.SLOT?"SLOT":"MULTISLOT")+
				            ", NTests "+p.getNTests(j));
				    for (int k = 0; k < p.getNTests(j); k++) {
				    	int varType = VariableBindingNode.BAD_VARIABLE;
				    	boolean varIsLocal = false;
						test = p.getTest(j,k);
						testType = test.getTest();
				        if (trace.getDebugCode("mt")) trace.outNT("mt", "p"+patternIndex+",s"+j+",t"+k+
				                " test \""+test+"\", testTest="+testType+
				                " testValue=\""+test.getValue()+"\""+
								" testValueType="+test.getValue().type()+
				                " slotType="+(slotType==RU.SLOT?"SLOT":"MULTISLOT"));
						int i;
						// get the type of the variable
						if(test.getValue().type() != RU.FUNCALL){
							sb = new StringBuffer(test.getValue().toString());
							
							if (sb.charAt(0) == '$') {
							    if (sb.length() > 1 && sb.charAt(1) == '?') {
									// multi variable
									if (VariableBindingNode.isBlank(sb.toString()))
										varIsLocal = true;
									sb.delete(0,2);
									// this represents a variable that matches multiple values and it should
									// also be stored in the variable list
									varType = VariableBindingNode.MATCHES_MULTI;			
							    }else{
							    	// error invalid variable
							    	varType = VariableBindingNode.BAD_VARIABLE;
							    	String err = "Expected \"$?\" but found \"$\" in pattern \""+
							    			patternString+"\"";
							    	analysisErrors.add(err);
							    }
							}else if (sb.charAt(0) == '?'){
							    // variable matches a single value
							    // add it to the list of variables
								if (VariableBindingNode.isBlank(sb.toString()))
									varIsLocal = true;
							    sb.deleteCharAt(0);
							    varType = VariableBindingNode.MATCHES_SINGLE;
							} else {
									// else its a literal also add the literals in the list of variables but mark them as literals
									// ie. set the type of the variable as literals							
							    	varType = VariableBindingNode.LITERAL;
							}
						}else{
							// test is a funcall ie of the form (neq ?var ?var)
							// or of the form (eq ?var ?var)
							// instead while extracting the values of the variables test the 
							// funcall ie substitute the value of the variable that has already been 
							// instantiated and see the result of the test. if true then proceed otherwise stop.
							varType = VariableBindingNode.FUNCALL;
							
							sb = new StringBuffer(test.getValue().toString());
						}
					    if (trace.getDebugCode("mt")) trace.outNT("mt", "p"+patternIndex+",s"+j+",t"+k+
					            " testValueType="+RU.getTypeName(test.getValue().type())+
					            ", varType="+varType+", sb=\""+sb+"\"");
						
						// add the variable to the variable list if not present already
						// do not add local and bad variables to the list of variables
						if (
// FIXME include locals			(varType != VariableBindingNode.LOCAL ) &&
						        (varType != VariableBindingNode.BAD_VARIABLE)) { 
// FIXME include multivars   && (varType != VariableBindingNode.MATCHES_MULTI)) { 
						    	// !sb.equals("nil")
							VariableBindingNode varBindingNode = null;
							// FIXME (fixed) give literals and funcalls unique names
							if((varType == VariableBindingNode.LITERAL) || (varType == VariableBindingNode.FUNCALL)){
								varBindingNode = new VariableBindingNode(VariableBindingNode.LIT,
										sb.toString(),
										patternIndex,vMap.size(),j,test.getMultiSlotIndex(),
										slotType,varType,testType);								
								varBindingNode.setValue(test.getValue());
								if(varType == VariableBindingNode.FUNCALL){
									varBindingNode.setTest(test);
								}
							}
							else if ((i = containsVariable(0,vMap,sb.toString())) == -1) {
								varBindingNode = new VariableBindingNode(sb.toString(),
										patternIndex,vMap.size(),j,test.getMultiSlotIndex(),
										slotType,varType,testType);								
								if(varBindingNode.getVariableValue() != null)
									context.setVariable(varBindingNode.getExtVariableName(), varBindingNode.getVariableValue());
						    }else{
// FIXME  if (slotType != VariableBindingNode.MULTISLOT){
						    	// if the variable already exists in the list of variables
						    	// then add it as a reference of the existing variable;
						    	// omit this in multislots since match_pattern appears
						    	// to handle this and it causes repeated evaluation of
						    	// the same multifield;
						    	// FIXME ensure at least one var per multislot pattern
						    	varBindingNode = new VariableBindingNode(VariableBindingNode.REF,
						    			sb.toString(), patternIndex,vMap.size(), j,
										test.getMultiSlotIndex(), slotType, varType, testType);
						    }
						    if (varBindingNode != null) {
						    	vMap.add(varBindingNode);
						    	if (trace.getDebugCode("mt")) trace.outNT("mt", "added vbn at vMap["+
						    			(vMap.size()-1)+"] "+varBindingNode.dump());
						    }
						} // else the variable is either a local or bad variable hence do not add them to the list of variables
				    }// end for k
				}catch (Exception ex){
				    analysisErrors.add("Error on pattern \""+patternString+"\": "+ex);
				    ex.printStackTrace();
				}
		    }// end for j
		}// end while
    }// end getVariables
        
    /**
     * Find a fact on the given list whose fact-id matches that of the fact in
     * the given vbn.  If found, reduce the factList to just that entry.  Else
     * report that the variable is not bound to a fact of type in the list. 
     * @param vbn variable whose value is of type FACT
     * @param factList list of facts all of one type; will modify if successful
     * @return true if finds the vbn's fact in the list; in this case factList
     *         is reduced to the single matching entry; else factList will be
     * 		   returned empty
     */
    private boolean testForFactOnList(VariableBindingNode vbn, java.util.List factList) {
    	if (factList.isEmpty())
    		return false;
    	Fact fact = null;
    	try {
    		fact = vbn.getVariableValue().factValue(context);
    	} catch (JessException je) {
    		String errMsg = "Error testing variable "+vbn.getExtVariableName()+
    			", cannot retrieve value: "+je;
		    analysisErrors.add(errMsg);
		    System.err.println("\n"+errMsg+"\n");
		    je.printStackTrace();
		    return false;
    	}
    	int fId = fact.getFactId();
    	for (ListIterator lit = factList.listIterator(); lit.hasNext(); ) {
    		Fact factFromList = (Fact) lit.next();
			if (factFromList.getFactId() != fId)
				lit.remove();
    	}
    	return (factList.size() > 0);
    }
    
    /**
     * this method extracts the variables values from the list of facts for the current pattern and adds them to open list
     * 
     * @param vbn       The VariableBindingNode
     * @param factList  The list of facts for the current pattern
     * @param localOpen the stack of variable bindings
     * @param wasPattern whether we just matched a multifield pattern 
     * @return the number of variables added to open
     */
    public int getVariableValues(VariableBindingNode vbn, ArrayList factList, Stack localOpen,
    		boolean[] wasPattern){
		VariableBindingNode newVbn;
		//VariableBindingNode currentVariableBindingNode; // unused
		Fact fact;
		//ValueVector vv; // unused
		//int index; // unused
		int n = 0;	// number of variables added to open
		wasPattern[0] = false;
	
		// this list holds the list of relevant facts for the current pattern
		// ie depending on the values of the variables that have been seen before in other patterns 
		// removes irrelevant facts from the current list of facts
		// FIXME (fixed?) use the real list; don't clone it
		// FIXME again:  use a shallow copy: must preserve the real list for later steps
		// FIXME 3rd time:  see advanceOrEndInstantiations()
		// ArrayList tempList = new ArrayList(factList);
		ArrayList tempList = factList;
		if (trace.getDebugCode("mt")) trace.outNT("mt", "tempList or depth "+vbn.getDepth()+
				" at top of getVariableValues:\n"+tempList);
			
		Value val,currentVal;
	
		// get all the variables from the current pattern and if the variable is already in the 
		// current values list then remove all facts that do not have the same values as the 
		// vairables current value.
		int patternIndex = vbn.getDepth();
		Pattern p = (Pattern)patterns.get(patternIndex);
		String patternName = p.getName(); 
		Deftemplate patternTemplate = p.getDeftemplate();
		boolean mathFact = false;
		if("MAIN::MATHFACT".equals(patternName)){
			mathFact = true;
		}
		// for each slot in the pattern before the current variable
		//Test1 test; // unused
		//StringBuffer sb; // unused
		VariableBindingNode v;
		//int testType; // unused
		int lastSlot = vbn.getSlotIndex();
		if(mathFact){
			lastSlot = p.getNSlots();
		}
		if (trace.getDebugCode("mt")) trace.outNT("mt", "getVariableValues() top for vbn "+vbn.dump()+" on p"+
		        patternIndex+" "+patternName+", template "+
		        (patternTemplate==null?"(null)":patternTemplate.getName())+
		        ", lastSlot"+lastSlot+", tempList.size() "+tempList.size());
		
		// get the bound name for the pattern
		// ?bound-name <- (pattern)
		// if the bound name is not null and the variable is bound to a
		// fact value, then consider just that one fact for this pattern.
		//
		String boundName = p.getBoundName();
		if(boundName != null){
			int h = containsVariable(0,currentValues,boundName);
			if (trace.getDebugCode("mt")) trace.outNT("mt", "boundName "+boundName+" at h="+h+" in currentValues");
			if (h != -1) {
				v = (VariableBindingNode)currentValues.get(h);
				currentVal = v.getVariableValue();
				if (trace.getDebugCode("mt")) trace.outNT("mt", "boundName varBN "+v.dump()+
				        " currentVal.type() "+RU.getTypeName(currentVal.type()));
				if(currentVal.type() == RU.FACT) {
					if (!testForFactOnList(v, tempList)) {
						// print the error message that the ?bound-name is not bound to a fact.
						String diagMsg = "?"+boundName+" is not bound to a fact of the proper type";
						if (patternTemplate != null)
							diagMsg = "?"+boundName+" is not bound to a fact of type "+
									patternTemplate.getName();
						printRuleInstantiations(patternIndex, diagMsg, FAILURE);
						return 0;
					}
				}else{
					printRuleInstantiations(patternIndex,
							"?"+boundName+" is not bound to a fact", FAILURE);
					
				}
			}
		}
		if (trace.getDebugCode("mt")) trace.outNT("mt", "tempList after bound-name:\n  "+tempList);
//		
//		// remove facts whose type doesn't match this pattern's deftemplate
//		for (int m = 0; m < tempList.size(); m++) {
//		    Fact f = (Fact) tempList.get(m);
//		    Deftemplate dt = f.getDeftemplate();
//		    if ((patternTemplate == null && dt != null) ||
//		            !patternTemplate.equals(dt)) {
//				trace.outNT("mt", "to remove tempList["+m+"] template "+
//				        (dt==null?"(null)":dt.getName()));
//		        tempList.remove(m--);
//		    }
//		}
//		if (tempList.isEmpty()) {
//		    printRuleInstantiations(null,null,null,FAILURE);
//		    return 0;
//		}
//		trace.outNT("mt", "tempList size after template test "+tempList.size());
		// FIXME not sure the following code accomplishes anything
//		for (int t = 0; (t < lastSlot) && !stopWhyNot; t++) {
//		    // check to see if it is in the current Variable List
//		    for (int j = 0; (j < p.getNTests(t)) && !stopWhyNot; j++) {
//				test = p.getTest(t,j);
//				testType = test.getTest();
//
//				boolean testResult = this.doTest(test, context);  // FIXME why throw the result away?
//				trace.outNT("mt", "getVariableValues() pattern "+p+" s"+t+"t"+j+
//						" test "+test+", result "+testResult);
//								
//				if (test.getValue().type() != RU.FUNCALL){
//					sb = new StringBuffer(test.getValue().toString());
//					if (sb.charAt(0) == '$') {
//					    if (sb.charAt(1) == '?') {
//							sb.delete(0,2);
//					    }else{
//							sb.deleteCharAt(0);
//					    }
//					}else if (sb.charAt(0) == '?') {
//					    sb.deleteCharAt(0);
//					}
//					int h = containsVariable(0,currentValues,sb.toString());
//					trace.outNT("mt", "getVariableValues() pattern "+p+" s"+t+"t"+j+
//							" test.getValue() sans'?' "+sb+
//							(h >= 0 ? " at currentValues["+h+"]" : ""));
//					if (h >= 0) {
//					    v = (VariableBindingNode)currentValues.get(h);
//					    currentVal = v.getVariableValue();
//						// prune the list of facts to consider for instantiation for this pattern depending
//						// on the values of the variables that have occurred before in the previous patterns.
//					    for (int m = 0; m < tempList.size(); m++) {
//					        String templateName = "(unknown)";
//							try {
//							    templateName =
//						            ((Fact) tempList.get(m)).getDeftemplate().getName();
//							    val = ((Fact)tempList.get(m)).get(t);
//								trace.outNT("mt", "getVariableValues() pattern "+p+" s"+t+"t"+j+
//										" fact["+m+"] "+templateName+" slot val "+val+
//										" ?= currentVal "+currentVal);
//							    if (v.getSlotType() == VariableBindingNode.MULTISLOT &&
//							    		(val.type() == RU.LIST || val.type() == RU.MULTISLOT)) {
//									vv = val.listValue(null);
//									if (v.getSubSlotIndexes() < vv.size()) {
//									    val = vv.get(v.getSubSlotIndexes());
//									}
//							    }
//							    // if the test == EQ and value is not equal to the current slot value then remove the fact from the list
//							    if (testType == Test1.EQ){
//								    if (!val.equals(currentVal)) {
//										tempList.remove(m);
//										m--;
//								    }
//							    }else if(testType == Test1.NEQ){
//							    // if the test == NEQ and value is not equal to the current slot value then remove the fact from the list
//								    if (val.equals(currentVal)) {
//										tempList.remove(m);
//										m--;
//								    }
//							    }
//							}catch(Exception ex){
//							    analysisErrors.add("Error testing fact of type "+
//							            templateName+" on pattern "+patternName+
//							            ": "+ ex);
//							    ex.printStackTrace();
//							}
//					    }
//					}
//				}else{
//					// test is a funcall
//					// do nothing					
//				}
//		    }
//		}
		if (trace.getDebugCode("mt")) trace.outNT("mt", "tempList after doTest()s "+tempList);
		
		if(vbn.getVariableType() == VariableBindingNode.FUNCALL){
			// execute the test and if it is false then break here
			
			boolean testResult = this.doTest(vbn.getTest(), context);
			if (trace.getDebugCode("mt")) trace.outNT("mt", "getVariableValues() pattern "+p+" FUNCALL vbn "+
					vbn.dump()+" test result "+testResult);
			
			if (!testResult || stopWhyNot){
				newVbn = new VariableBindingNode(vbn);
				newVbn.setValue(Funcall.FALSE);
				currentValues.add(newVbn);
				printRuleInstantiations(patternIndex,
						vbn.getExtVariableName()+" did not return TRUE", FAILURE);
				currentValues.remove(newVbn);
				return 0;
			}else{
				// test is true and push the VariableBindingNode on the open List
				newVbn = new VariableBindingNode(vbn);
				newVbn.setValue(Funcall.TRUE);
				n = 1;
				localOpen.push(new TestResult(newVbn, tempList));
			}
		}else if (tempList.isEmpty()) {
		    printRuleInstantiations(patternIndex,"no facts match",FAILURE);
		    return 0;
		}else if (vbn.getSlotType() == VariableBindingNode.TEMPLATE) {
			testPatternTemplate(patternIndex, vbn, localOpen, tempList, n);
			if (tempList.isEmpty()) {
				printRuleInstantiations(patternIndex,
						"no facts of type "+vbn.getVariableName(),FAILURE);
				return 0;
			}else{
				n = 1;
				newVbn = new VariableBindingNode(vbn);
//				localOpen.push(new TestResult(newVbn, tempList));
				n = pushInstancePerFact(vbn, localOpen, tempList);
				if (trace.getDebugCode("mt")) trace.outNT("mt", "tempList size after testPatternTemplate "+
						tempList.size());
			}
		}
	    else if (vbn.isVariableReference()) {
	    	testVariableReference(patternIndex, vbn, localOpen, tempList, n);
			if (tempList.isEmpty())
				return 0;
			else{
				// test succeeds so push as a literal match onto the list of open
				n = 1; // only one value to push
				newVbn = new VariableBindingNode(vbn);
				newVbn.setVariableType(VariableBindingNode.LITERAL);
//				localOpen.push(new TestResult(newVbn, tempList));
				n = pushInstancePerFact(vbn, localOpen, tempList);
			}
		}else if (vbn.getVariableType() == VariableBindingNode.LITERAL &&
				vbn.getSlotType() != VariableBindingNode.MULTISLOT) {
	    	testLiteral(patternIndex, vbn, localOpen, tempList, n);
			if (tempList.isEmpty()) {
				return 0;
			}else{
				// test succeeds so push as a literal match onto the list of open
				newVbn = new VariableBindingNode(vbn);
				newVbn.setVariableType(VariableBindingNode.LITERAL);
//				localOpen.push(new TestResult(newVbn, tempList));
				n = pushInstancePerFact(vbn, localOpen, tempList);
			}
		}else{
			for (int i = 0; (i < tempList.size()) && !stopWhyNot; i++){
			try{
			    fact = (Fact)tempList.get(i);
			    String templateName = fact.getDeftemplate().getName(); 
			    if (trace.getDebugCode("mt")) trace.outNT("mt", "tempList["+i+"] template "+
			            templateName+" NSlots "+
			            fact.getDeftemplate().getNSlots()+" vbn.SlotIndex "+
			            vbn.getSlotIndex());
			    
			    // if this var is a pattern-binding or a template test,
			    // test the fact type
			    // FIXME: return w/ delta-n <= 1 to avoid skipping var
			    if (vbn.getSlotType() == VariableBindingNode.BOUND_NAME) {
			        n = testPatternBinding(patternIndex, vbn, localOpen, fact, n);
					continue;
			    }
			    if(fact.getDeftemplate().getNSlots() <= vbn.getSlotIndex()){
				    printRuleInstantiations(patternIndex,"error binding variable",FAILURE);
			    	return n;
			    }
			    String slotName = fact.getDeftemplate().getSlotName(vbn.getSlotIndex());
			    val = fact.getSlotValue(slotName);
			    if (trace.getDebugCode("mt")) trace.outNT("mt", "tempList["+i+"] slot "+slotName+" val "+val+
			            " vbn"+vbn.dump());
			    if (vbn.getSlotType() == VariableBindingNode.MULTISLOT ) {
			    	n = bindMultislotPattern(val, vbn, localOpen, fact, slotName);
//					n = match_pattern(val,(Pattern)patterns.get(vbn.getDepth()),vbn.getSlotIndex(),localOpen);
					wasPattern[0] = true;
					if (trace.getDebugCode("mt")) trace.outNT("mt", "match_pattern()=>"+n);
			    }else{
			    	if (vbn.getVariableType() == VariableBindingNode.MATCHES_SINGLE) {
					    n++;	// was n = 1 - changes cuz we need to get all possible values for the variable from all the facts of this type
					    newVbn = new VariableBindingNode(vbn);
					    //Value testValue = new Value(MTRete.NOT_SPECIFIED, RU.SYMBOL);

                                            newVbn.setValue(val);
                                            
						if(val != null)
							context.setVariable(newVbn.getExtVariableName(), val);
					    localOpen.push(new TestResult(newVbn, fact));
						if (trace.getDebugCode("mt")) trace.outNT("mt", "MATCHES_SINGLE n "+n+", var "+
						        newVbn.getVariableName()+", val"+val);
					}
			    }
			    // construct the variableBindingNode and put it on the list of open
			}catch (Exception ex){
			    analysisErrors.add("Error setting variables on pattern "+
			            patternName+": "+ ex);
			    ex.printStackTrace();
			}
		    }// for
		}// else
		return n;
    }
    
    /**
     * A {@link VariableBindingNode} test has matched for an entire fact list. 
     * Push one copy of the test onto the localOpen stack for each fact.
     * @param vbn
     * @param localOpen
     * @param factList
     * @return  number of instances pushed
     */
    private int pushInstancePerFact(VariableBindingNode vbn, Stack localOpen,
    		java.util.List factList) {
    	int n = 0;
    	for (Iterator it = factList.iterator(); it.hasNext(); ++n) {
			VariableBindingNode newVbn = new VariableBindingNode(vbn);
			localOpen.push(new TestResult(newVbn, (Fact) it.next()));
    	}
    	return n;
    }
	
	/**
	 * Test whether a literal matches the current variable value.
	 * @param  patternIndex for error message
	 * @param  refVbn varible-reference variable
	 * @param  localOpen push new binding on this Stack if test succeeds
	 * @param  factList List of Facts to test
	 * @param  slotName name of slot to test
	 * @param  fVal value of this slot
	 * @param  n increment this value if test succeeds
     * @return new value for n
     */
    private int testLiteral(int patternIndex, VariableBindingNode vbn,
            Stack localOpen, java.util.List factList, int n) {
        try {
            Value vVal = vbn.getVariableValue();
            if (vVal == null)
                throw new Exception("literal's value was null");
            for (ListIterator lit = factList.listIterator(); lit.hasNext();) {
                Fact fact = (Fact) lit.next();
                Deftemplate dt = fact.getDeftemplate(); 
                String slotName = (dt != null ? dt.getSlotName(vbn.getSlotIndex()) : "");
                Value fVal = fact.getSlotValue(slotName);
                Value rfVal = fact.getSlotValue(slotName).resolveValue(context);
                if (trace.getDebugCode("mt")) trace.outNT("mt", "testLiteral n="+n+", fact "+fact+
                        " slot "+slotName+" fVal "+fVal+" rfVal "+rfVal+" var "+vbn.dump());
                VariableBindingNode newVbn;
                if (vbn.getTestType() == Test1.EQ){
                    if (!vVal.equals(fVal)) {
                        lit.remove();
                        newVbn = new VariableBindingNode(vbn);
                        //newVbn.setValue(fVal);
                        currentValues.add(newVbn);
                        printRuleInstantiations(patternIndex,
                                "failed to match literal "+vVal,FAILURE);
                        currentValues.remove(newVbn);
                    } else{
                        if (trace.getDebugCode("mt")) trace.outNT("mt", "match LITERAL EQ n "+n+", var "+
                                vbn.dump()+", val"+fVal);
                    }
                }else if (vbn.getTestType() == Test1.NEQ){
                    if (vVal.equals(fVal)) {
                        lit.remove();
                        newVbn = new VariableBindingNode(vbn);
                        //newVbn.setValue(fVal);  // was using vbn; made same as Test1.EQ clause?
                        currentValues.add(newVbn);
                        printRuleInstantiations(patternIndex,
                                "failed on literal test ~"+vVal, FAILURE);
                        currentValues.remove(newVbn);
                    }else{

                        if (trace.getDebugCode("mt")) trace.outNT("mt", "match LITERAL NEQ n "+n+", var "+
                                vbn.dump()+", fVal"+fVal);
                    }
                }
            }
        	return factList.size();
        } catch (Exception e) {
        	String errMsg = "In "+indexToLineNo(patternIndex)+": error testing literal \""+
				vbn.getVariableValue()+"\": "+e.getMessage();
        	analysisErrors.add(errMsg);
        	System.err.println("\n"+errMsg+"\n for refVbn "+vbn.dump()+"\n");
        	factList.clear();
        	return 0;
        }
    }
        
	/**
	 * Test whether a variabe reference matches the current variable value.
	 * @param  patternIndex for error message
	 * @param  refVbn varible-reference variable
	 * @param  localOpen push new binding on this Stack if test succeeds
	 * @param  fact Fact to test
	 * @param  slotName name of slot to test
	 * @param  fVal value of this slot
	 * @param  n increment this value if test succeeds
     * @return new value for n
     */
    private int testVariableReference(int patternIndex, VariableBindingNode refVbn,
    		Stack localOpen, java.util.List factList, int n) {
    	String varName = refVbn.getExtVariableName();
        try {
        	int h = containsVariable(0, currentValues, varName);
        	if (h < 0) {
        		String errMsg = "Cannot reference variable ?"+varName;
    	        System.err.println("\n"+errMsg+"\n");  // this a bug, probably
    	        throw new Exception(errMsg);
        	}
        	VariableBindingNode vbn = (VariableBindingNode) currentValues.get(h);
            Value vVal = vbn.getVariableValue();
            if (vVal == null) {
            	throw new Exception("No value for variable ?"+varName+
    					": value should have been bound already");
            }
        	for (ListIterator lit = factList.listIterator(); lit.hasNext();) {
        		Fact fact = (Fact) lit.next();
        		Deftemplate dt = fact.getDeftemplate(); 
        	    String slotName = (dt != null ? dt.getSlotName(vbn.getSlotIndex()) : "");
        	    Value fVal = fact.getSlotValue(slotName);
                if (trace.getDebugCode("mt")) trace.outNT("mt", "testVariableReference n="+n+", fact "+fact+
                		" slot "+slotName+" fVal "+fVal+" var "+refVbn.dump());
        		VariableBindingNode newVbn;
				if (refVbn.getTestType() == Test1.EQ){
				    if (!vVal.equals(fVal)) {
	        			lit.remove();
						newVbn = new VariableBindingNode(vbn);
						newVbn.setValue(vVal);
						currentValues.add(newVbn);
						printRuleInstantiations(patternIndex,
								"failed to match value of ?"+vbn.getVariableName(),FAILURE);
						currentValues.remove(newVbn);
						newVbn = null;
				    } else{
//				    	// test succeeds so push as a literal match onto the list of open
//				    	n++; // was n = 1 - changes cuz we need to get all possible values for the variable from all the facts of this type
//						newVbn = new VariableBindingNode(vbn);
//				    	newVbn.setVariableType(VariableBindingNode.LITERAL);
//				    	localOpen.push(newVbn);
						if (trace.getDebugCode("mt")) trace.outNT("mt", "match REF VARIABLE EQ n "+n+", var "+
						        vbn.dump());
				    }
				}else if (refVbn.getTestType() == Test1.NEQ){
				    if (vVal.equals(fVal)) {
	        			lit.remove();
						newVbn = new VariableBindingNode(vbn);
						newVbn.setValue(vVal);  // was using vbn; made same as Test1.EQ clause?
						currentValues.add(newVbn);
						printRuleInstantiations(patternIndex,
								"failed on test ~?"+vbn.getVariableName(),FAILURE);
						currentValues.remove(newVbn);
						newVbn = null;
				    }else{
//				    	// test succeeds and hence push the reference as a literal onto the open list and continue
//				    	n++;	// was n = 1 - changes cuz we need to get all possible values for the variable from all the facts of this type
//				    	newVbn = new VariableBindingNode(vbn);
//				    	newVbn.setVariableType(VariableBindingNode.LITERAL);
//				    	localOpen.push(newVbn);
						if (trace.getDebugCode("mt")) trace.outNT("mt", "match REF VARIABLE NEQ n "+n+", var "+
						        vbn.dump());
				    }
				}else{
					String errMsg = "Unable to interpret test";
			        throw new Exception(errMsg);
				}
        	}
        	return factList.size();
        } catch (Exception e) {
    		String errMsg = "In "+indexToLineNo(patternIndex)+": error referring to variable ?"+
				refVbn.getExtVariableName()+": "+e.getMessage();
    		analysisErrors.add(errMsg);
	        System.err.println("\n"+errMsg+"\n for refVbn "+refVbn.dump()+"\n");
    		factList.clear();
    		return 0;
        }
    }
	
	/**
	 * Test a pattern-binding {@link VariableBindingNode} against the
	 * pattern's template type.
	 * @param  patternIndex for error message
	 * @param  vbn pattern-binding variable
	 * @param  localOpen push new binding on this Stack if test succeeds
	 * @param  fact Fact to test
	 * @param  n increment this value if test succeeds
     * @return new value for n
     */
    private int testPatternBinding(int patternIndex, VariableBindingNode vbn,
    		Stack localOpen, Fact fact, int n) throws JessException {
    	if (trace.getDebugCode("mt")) trace.outNT("mt", "testPatternBinding("+patternIndex+","+vbn.dump()+
    			",stackSize "+localOpen.size()+","+fact+","+n+")");
        VariableBindingNode newVbn;
        Value val = new FactIDValue(fact);
        Value currentVal = vbn.getVariableValue();
        newVbn = new VariableBindingNode(vbn);
        newVbn.setValue(val);
    	if (trace.getDebugCode("mt")) trace.outNT("mt", "testPatternBinding{newVbn val "+val+", fact "+
    			val.factValue(context)+"}");
        if (currentVal != null) {
        	currentValues.add(newVbn);
        	printRuleInstantiations(patternIndex,
        			"?"+vbn.getVariableName()+" should be unbound at start of pattern",
					FAILURE);
        	currentValues.remove(newVbn);
            if (trace.getDebugCode("mt")) trace.outNT("mt", "testPatternBinding failure n="+n+", vbn "+
                    vbn.dump()+", val"+val);
        	newVbn = null;
        } else{
            // else if the match then push it on the list of open
            n++; // was n = 1 - changes cuz we need to get all possible values for the variable from all the facts of this type
            localOpen.push(new TestResult(newVbn, fact));
            if (trace.getDebugCode("mt")) trace.outNT("mt", "testPatternBinding success n="+n+", var "+
                    newVbn.getVariableName()+", val"+val);
        }
        return n;
    }
	
	/**
	 * Test a list of facts against a template {@link VariableBindingNode}
	 * @param  patternIndex for error message
	 * @param  vbn pattern-binding variable
	 * @param  localOpen push new binding on this Stack if test succeeds
	 * @param  factList Facts to test 
     * @param  n increment this count if test succeeds
     * @return new value for n
     */
    private int testPatternTemplate(int patternIndex, VariableBindingNode vbn,
    		Stack localOpen, java.util.List factList, int n) {
        VariableBindingNode newVbn;
        Value val = vbn.getVariableValue();
        String vbnTemplateName;
        try {
        	vbnTemplateName = val.stringValue(context);
        	for (ListIterator lit = factList.listIterator(); lit.hasNext();) {
        		Fact fact = (Fact) lit.next();
        		Deftemplate dt = fact.getDeftemplate(); 
        		String templateName = (dt != null ? dt.getName() : ""); 
        		if (!templateName.equals(vbnTemplateName)) {
        			lit.remove();
        			if (trace.getDebugCode("mt")) trace.outNT("mt", "testPatternTemplate: vbn "+vbn.dump()+
        					" removed fact "+fact);
        			//            	currentValues.add(newVbn);
        			//            	printRuleInstantiations(patternIndex,
        			//            			"fact is not of type "+vbnTemplateName, FAILURE);
        			//            	currentValues.remove(newVbn);
        			//            	newVbn = null;
        		} else{
        			//                // else if the match then push it on the list of open
        			//                n++; // was n = 1 - changes cuz we need to get all possible values for the variable from all the facts of this type
        			//                localOpen.push(newVbn);
        			if (trace.getDebugCode("mt")) trace.outNT("mt", "testPatternTemplate: vbn "+vbn.dump()+
        					" matched fact "+fact);
        		}
        	}
        	return factList.size();
        } catch (Exception e) {
    		String errMsg = "Internal error testing facts' deftemplate types: "+e;
    		analysisErrors.add(errMsg);
    		System.err.println("\n"+errMsg+"\n");
    		factList.clear();
    		return 0;
        }
    }

    private String extractVariableName(String varName){
		if (varName.charAt(0) == '?'){
			varName = varName.substring(1);
			int i;
			if ((i = containsVariable(0, currentValues, varName)) != -1){
				return ((VariableBindingNode)currentValues.get(i)).getVariableValue().toString();
			}else{
				return null;
			}
		}else if (varName.charAt(0) == '$'){
			if (varName.charAt(1) == '?'){
				return null;
			}
		}
		return varName;
	}

	/**
	 * execute funcall using jess's context object. Create a dummy context with the 
	 * current variables and the values so far. Then use Test1.doTest()
	 * @param value
	 * @return
	 */
	
	public boolean doTest(Test1 test, Context context){

		boolean result = false;
		try {
			// perform doTest()
			result = test.doTest(context);
			
		} catch (JessException e) {
		}
		return result;
	}

    /**
	 * @param string
	 */
	private void printError(String string) {
		this.textArea.append("\n***************************************************************************\n");
		this.textArea.append(string);		
		this.textArea.append("\n***************************************************************************\n");
	}

	/**
     * this method checks to see if the variable is present in the 
     * variable list 
     * 
     * @param variableList
     *               The list of variables
     * @param name   name of the variable
     * @param startIndex - the index in the Variable List from which the search should be done
     * @return the index of the variable found in the list -1 otherwise
     */
    public int containsVariable(int startIndex,ArrayList variableList, String name){
		VariableBindingNode vbn;
		for (int i = startIndex; i < variableList.size(); i++){
		    vbn = (VariableBindingNode)variableList.get(i);
		    if (vbn.getVariableName().equals(name)) {
				return i;
		    }
		}
		return -1;
    }
    
	/**
	* Called whenever a new instantiation is found.
	* @param  patternIndex 0-based index of error pattern in rule; -1 if success
	* @param  errorDesc text of what's wrong
	* @param  result either {@link #SUCCESS} or {@link #FAILURE}
	*/
    public void printRuleInstantiations(int patternIndex, String errorDesc,
    		String result) {

        if (trace.getDebugCode("mt")) trace.outNT("mt", "?print patternIndex="+patternIndex+", result="+
        		result+", "+errorDesc);
		if(!showFull && result.equals(FAILURE)){
			return;
		}

		// get all the patterns from the rule LHS....then for each pattern get all 
		// the tests from the pattern.....for each test get the variables value from the 
		// currentValues and substitute it in the pattern and store it
		Pattern pattern = null;
		Test1 test;
		String variableName;

		StringBuffer outputString = new StringBuffer();		

		VariableBindingNode vbn;
		String varName = null;

		VBNTableModel vbnTableModel = new VBNTableModel(this, null);  // empty model 
		for (int i = 0; i < currentValues.size(); i++) {
		    vbn = (VariableBindingNode)currentValues.get(i);
		    if (trace.getDebugCode("mt")) trace.outNT("mt", "?print currentValues["+i+"] vbn "+vbn.dump());
			if (vbn.getVariableType() == VariableBindingNode.LITERAL ||
			        vbn.getSlotType() == VariableBindingNode.TEMPLATE ||
					vbn.isVariableReference() ||
			        vbn.getVariableName().indexOf("_blank_") != -1)
				continue;
			vbnTableModel.add(vbn);
		}		
		int start, end;
		start = 0;
		end = 0;
		boolean found = false;
		int i = 0;
		int j = 0;
		int k = 0;
outerMost:for (i = 0; i < patterns.size();i++){
			pattern = (Pattern)patterns.get(i);
			boolean mathFact = false;
			if(pattern.getName().equals("MAIN::MATHFACT")){
				mathFact = true;
			}

			// replace the bound name with the value bound previously.
			variableName = pattern.getBoundName();

		}

			//skip this one if it's exactly the same as one already in the list

			boolean alreadyListed = false;
			if (trace.getDebugCode("mt")) trace.outNT("mt","before duplicate skip: resultsList.size() "+resultsList.size());

//			for(ListIterator it = resultsList.listIterator(); it.hasNext();){
//				Vector h = (Vector)it.next();
//				for(ListIterator vars_it = h.listIterator(), bind_it = bindings.listIterator(); vars_it.hasNext();){
//					Vector var = (Vector)vars_it.next();
//					Vector bind = (Vector)bind_it.next();
//					trace.outNT("mt", "var "+var+", bind "+bind);
//					if(!(var.get(0).equals(bind.get(0)) && var.get(1).equals(bind.get(1)))) break;
//					if(!vars_it.hasNext()) alreadyListed = true;
//				}
//				if(alreadyListed) break;
//			}

			if(!alreadyListed && vbnTableModel.getRowCount() > 0){
				resultsList.add(vbnTableModel);
				if(result == SUCCESS){     // FIXME check to see if in agenda
					summaries.add(ALL_VARIABLES_BOUND_SUCCESSFULLY);
				} else{
					StringBuffer errorSumm = new StringBuffer();
					if (0 <= patternIndex && patternIndex < patterns.size())
						errorSumm.append("In "+indexToLineNo(patternIndex)+": ");
					if (errorDesc == null)
						errorDesc = "Error matching pattern";
					errorSumm.append(errorDesc).append("."); 
					summaries.add(errorSumm.toString());
//					if(valueFound != null && valueRequired != null){
//						//variable bound incorrectly
//						summaries.add(FOR_THE_VARIABLE_OR_EXPRESSION+vName+REQUIRED_VALUE_WAS+valueRequired+".");
//					}
//					else if(pattern != null && !pattern.getName().equalsIgnoreCase("test")){
//						//pattern not matched
//						String p = new PrettyPrinter(pattern).toString();
//						summaries.add("Could not match pattern for " + p.substring(0, p.indexOf(' ')) + ".");
//					}
//					else{
//						//are there other cases?  haven't seen any yet
//						summaries.add("Error binding variables."); //CHANGE THIS
//					}
				}
				//store number of variables bound for later sorting
				int[] a = new int[2];
				a[0] = resultsList.size() - 1;
				a[1] = i + j + k;
				index_depthList.add(a);
				int[] vl = new int[2];
				vl[0] = resultsList.size() - 1;
				vl[1] = vbnTableModel.getRowCount();
				index_varsList.add(vl);
			}
    }
	
    /**
     * Convert a 0-based index to a line number.
     * @param  i increment this by one to get a natural number
     * @return "line 1", "line 22", etc.; empty string if negative
     */
    private String indexToLineNo(int i) {
    	if (rulePrinter == null)
    		rulePrinter = new RulePrinter(rule);
    	return rulePrinter.patternIndexToLineNo(i);
    }
    
    /**
     * Return the current context.
     * @return {@link #context}
     */
    Context getContext() {
    	return context;
    }

    /**
     * Create a list of {@link VariableBindingNode}s for the tests in multislot
     * pattern.  Will draw one VBN for each test from the {@link #vMap}
     * starting at index vbn0.getSrNo().  
     * @param pattern   Pattern with the list of variables or literals to be
     *                  matched from entries in val;
     * @param slotIndex index of the multislot in the pattern
     * @param vbn0      {@link VariableBindingNode} at start of slot test
     * @return          array of {@link VariableBindingNode}s
     */
    private VariableBindingNode[] makeMultislotVbnList(Pattern pattern, int slotIndex,
    	     VariableBindingNode vbn0) {
    	int nTests = pattern.getNTests(slotIndex);
    	int vMapIdx = vbn0.getSrNo();
    	VariableBindingNode[] result = new VariableBindingNode[nTests];
		for (int t = 0; t < nTests; ++t, ++vMapIdx) {
			Test1 test = pattern.getTest(slotIndex, t);
			int testType = test.getTest();
			int testValueType = test.getValue().type();
			VariableBindingNode vbn = (VariableBindingNode) vMap.get(vMapIdx);
	        if (trace.getDebugCode("mt")) trace.outNT("mt", "makeMultislotVbnList s"+slotIndex+",t"+t+
	                " test \""+test+"\", testTest="+testType+
	                " testValue=\""+test.getValue()+"\""+
					" testValueType="+testValueType+"="+RU.getTypeName(testValueType));
	        if (trace.getDebugCode("mt")) trace.outNT("mt", "vbn "+vbn.dump());
	        result[t] = vbn;
		}
    	return result;
    }
    
    /**
     * Count the number of scalar tests in an array returned by
     * {@link #makeMultislotVbnList(Pattern, int, VariableBindingNode)}.
     * Check to see that the given ValueVector has at least enough values to
     * fill this number.
     * @param vbnArr array returned by makeMultislotVbnList() 
     * @param vv number of values available for these tests
     * @param patternIndex for error messages
     * @param slotName  name of multislot for diagnostic msgs
     * @return number of tests that are not of type
     *         {@link VariableBindingNode#MATCHES_MULTI}; returns -1 for
     *         error if vv.size() is too less than this count
     */
    private int countScalarTests(VariableBindingNode[] vbnArr, ValueVector vv,
    		int patternIndex, String slotName) {
    	int result = 0;
    	for (int i = 0; i < vbnArr.length; ++i) {
    		if (vbnArr[i].getVariableType() == VariableBindingNode.MATCHES_MULTI)
    			continue;
    		++result;
    	}
    	if (vv.size() >= result)
    		return result;
		printRuleInstantiations(patternIndex, "the fact's multislot "+slotName+
				" has "+vv.size()+
				" entries, but the pattern's tests require at least "+result,
				FAILURE);
    	return -1;                                
    }
    
    /**
     * Assign an element or a range of elements from a ValueVector to a
     * {@link VariableBindingNode}. 
     * an empty list 
     * @param vv     multislot value to read
     * @param start  index of starting position in vv;
     * @param end    index+1 of desired ending position in vv
     * @param vbn    variable node to assign
     * @param patternIndex pattern index for error messages
     * @return index of next vv element to assign; returns -1 if vbn
     *               is not MATCHES_MULTI and start is at the end of vv
     */
    private int assignScalarOrList(ValueVector vv, int start, int end,
    		VariableBindingNode vbn, int patternIdx) throws Exception {
    	String vName = vbn.getVariableName();
    	if (vbn.getVariableType() != VariableBindingNode.MATCHES_MULTI) {
    		if (start >= vv.size() || start >= end)
    			return -1;
			Value newVal = vv.get(start).resolveValue(context);
			if (vbn.getVariableType() == VariableBindingNode.FUNCALL) {
	    		String errMsg = "At line "+indexToLineNo(patternIdx)+
						": Why Not? cannot analyze function calls in multislot patterns";
	    		analysisErrors.add(errMsg);
	    		trace.err(errMsg);
				return -1;
			} else if (vbn.getVariableType() == VariableBindingNode.LITERAL) {
				Value litVal = vbn.getVariableValue();
    			if (!newVal.equals(litVal)) {
    				// FIXME creates too many partial instantiations?
//    				printRuleInstantiations(patternIdx, "failed to match literal \""+
//    						litVal+"\" in the multislot pattern", FAILURE);
    				return -1;
    			}
    		} else if (vbn.isVariableReference()) {
    	    	String varName = vbn.getExtVariableName();
    	    	int h = containsVariable(0, currentValues, varName);
    	    	if (h < 0) {
    	    		String errMsg = "Cannot reference variable ?"+varName;
    	    		System.err.println("\n"+errMsg+"\n");  // this a bug, probably
    	    		throw new Exception(errMsg);
    	    	}
    	    	VariableBindingNode refVbn = (VariableBindingNode) currentValues.get(h);
    	    	Value vVal = refVbn.getVariableValue();
    	    	if (vVal == null) {
    	    		throw new Exception("No value for variable ?"+varName+
    	    		": value should have been bound already");
    	    	}
    	    	if (!newVal.equals(vVal))
    	    		return -1;
    	    	else
    	    		vbn.setValue(newVal);
    		} else {
    			vbn.setValue(newVal);
    		}
    		// context.setVariable(vName, newVal);  //FIXME needed for funcalls in pattern?
    		// FIXME test literal and function call matches here
    		if (trace.getDebugCode("mt")) trace.outNT("mt", "assignScalarOrList: vbn "+vbn.getVariableName()+
    				" gets newVal "+newVal+" from vv["+start+"]");
    		return start+1;
    	}
    	if (end > vv.size())
    		end = vv.size();
    	ValueVector newVv = new ValueVector(end - start);
    	int v = start;
    	for (; v < end; ++v) {
    		Value newVal = vv.get(v).resolveValue(context);
    		newVv.add(newVal);
    		if (trace.getDebugCode("mt")) trace.outNT("mt", "assignScalarOrList: vbn "+vbn.getVariableName()+
    				" adds newVal "+newVal+" from vv["+v+"]");
    	}
    	Value newListVal = new Value(newVv, RU.LIST);
		//context.setVariable(vName, newListVal);  //FIXME needed for funcalls in pattern?
    	vbn.setValue(newListVal);
		if (trace.getDebugCode("mt")) trace.outNT("mt", "assignScalarOrList: vbn "+vbn.getVariableName()+
				" list length "+newVv.size());
		return v;
    }
    
    /**
     * Class to hold the state of the multislot pattern match during the
     * execution of
     * {@link #bindMultislotPattern(Value, VariableBindingNode, Stack, Fact, String)}.
     */
    private class MultislotMatch implements Cloneable {
    	
    	/** Fact from which the {@link #vv} value came. */
    	final Fact fact;
    	
    	/** ValueVector from fact's multislot to match against pattern. */
    	final ValueVector vv;
    	
    	/** Index of next entry to use in {@link #vv}. */
    	int vvIdx = 0;
    	
    	/** Array of {@link VariableBindingNode}s encoding the tests. */
    	final VariableBindingNode[] vbnArr;
    	
    	/** Index of next test to use in {@link #vbnArr}. */
    	int vbnIdx = 0;
    	
    	/**
    	 *  Number of tests yet to execute in {@link #vbnArr} that are not of
    	 *  type MATCHES_MULTI. Decremented after each successful scalar test.
    	 */
    	int nScalarTests;
    	
    	/** Pattern index, for failure messages and maybe other purposes. */
    	final int patternIdx;
    	
    	/** Description of failure; null value indicates no failure so far. */
    	String failureDesc = null;
    	
    	/**
    	 * Constructor sets final fields.
    	 * @param vv
    	 * @param vbnArr
    	 * @param patternIndex
    	 * @param nTests
    	 */
    	MultislotMatch(ValueVector vv, VariableBindingNode[] vbnArr,
    			int patternIdx, int nScalarTests, Fact fact) {
    		this.vv = vv;
			this.vbnArr = vbnArr;
    		this.patternIdx = patternIdx;
    		this.nScalarTests = nScalarTests;
    		this.fact = fact;
    	}
    	
    	/**
    	 * Return a copy of this object.
    	 */
    	MultislotMatch copy() {
    		MultislotMatch result = new MultislotMatch(vv, vbnArr, patternIdx,
    				nScalarTests, fact);
    		result.failureDesc = failureDesc;
    		result.vbnIdx = vbnIdx;
    		result.vvIdx = vvIdx;
    		return result;
    	}
    	
    	/**
    	 * Print the contents for debugging.
    	 * @return formatted String
    	 */
    	String dump() {
    		StringBuffer sb = new StringBuffer("[ mm");
    		sb.append(" patternIdx=").append(Integer.toString(patternIdx));
    		sb.append(" nScalarTests=").append(Integer.toString(nScalarTests));
    		sb.append(" vv=").append(vv.toString());
    		sb.append(" vvIdx=").append(Integer.toString(vvIdx));
    		sb.append(" vbnArr.length=").append(Integer.toString(vbnArr.length));
    		sb.append(" vbnIdx=").append(Integer.toString(vbnIdx));
    		sb.append(" ]");
    		return sb.toString();
    	}
    	
    	/**
    	 * Tell how many tests of type {@link VariableBindingNode#MATCHES_MULTI}
    	 * remain to be executed.  Counts the current test as one.
    	 * @return number of multi tests 
    	 */
    	int getNMultiTestsRemaining() {
    		return vbnArr.length - vbnIdx - nScalarTests;
    	}
    	
    	/**
    	 * Return the next test from {@link #vbnArr}.
    	 * Increments {@link #vbnIdx} if returns a test.
    	 * @return entry at {@link #vbnArr}[{@link #vbnIdx}]; null if at end
    	 */
    	VariableBindingNode nextTest() {
    		if (vbnIdx >= vbnArr.length)
    			return null;
    		VariableBindingNode result = vbnArr[vbnIdx];
    		++vbnIdx;
    		return result;
    	}
    	
    	/**
    	 * Return the next test from {@link #vbnArr} so long as it is not of
    	 * type {@link VariableBindingNode.MATCHES_MULTI}.
    	 * Increments {@link #vbnIdx} if returns a test.
    	 * @return entry at {@link #vbnArr}[{@link #vbnIdx}]; null if at end
    	 *         or next entry not a scalar test
    	 */
    	VariableBindingNode nextScalarTest() {
    		if (vbnIdx >= vbnArr.length)
    			return null;
    		VariableBindingNode result = vbnArr[vbnIdx];
    		if (result.getVariableType() == VariableBindingNode.MATCHES_MULTI)
    			return null;
    		++vbnIdx;
    		return result;
    	}
    }
    
    /**
     * Test and assign a series of consecutive scalar variables in a multislot
     * pattern.
     * @param mm current match status 
     * @param valList List of {@link VariableBindingNode}s assigned values so far
     * @return true if assignment successful
     */
	private boolean doScalarTests(MultislotMatch mm, java.util.List valList) {
   		if (trace.getDebugCode("mt")) trace.outNT("mt", "doScalarTests top: mm "+mm.dump());
		for(VariableBindingNode vbn = mm.nextScalarTest();
				vbn != null; vbn = mm.nextScalarTest()) {
			try {
				vbn = new VariableBindingNode(vbn);
				int vvIdx = assignScalarOrList(mm.vv, mm.vvIdx, mm.vvIdx+1, vbn,
						mm.patternIdx);
				if (vvIdx < 0)
					return false;
				mm.vvIdx = vvIdx;         // increments by one if no failure
				--mm.nScalarTests;        // decrement after successful test
				valList.add(vbn);
			} catch (Exception e) {
	    		String errMsg = "Jess error assigning multislot entry to variable at line "+ 
						indexToLineNo(mm.patternIdx)+": "+e.toString();
	    		analysisErrors.add(errMsg);
	    		trace.err(errMsg);
	    		e.printStackTrace();
				return false;
			}
		}
		return true;
	}
    
    /**
     * Recursive method to assign all possible values to a single multivariable
     * in a multislot pattern.
     * @param mm0 current match status 
     * @param valList0 List of {@link VariableBindingNode}s assigned values so far
     * @param localOpen Stack for results
     * @return true if assignment successful
     */
	private int doMultiTest(MultislotMatch mm0, java.util.List valList0,
			Stack localOpen) {
		VariableBindingNode vbn = mm0.nextTest();
		if (vbn == null)
			return 0;
		int maxSize = mm0.vv.size() - mm0.vvIdx - mm0.nScalarTests;
		int minSize = 0;
		if (mm0.getNMultiTestsRemaining() < 1)  // last multi test constrained
			minSize = maxSize;
   		if (trace.getDebugCode("mt")) trace.outNT("mt", "doMultiTest top: mm0 "+mm0.dump()+", maxSize "+
   				maxSize+", minSize "+minSize);
		try {
			int n = 0;
			for (int size = maxSize; size >= minSize; --size, ++n) {
				// FIXME push context?
				MultislotMatch mm = mm0.copy();
				ArrayList valList = new ArrayList(valList0);
				vbn = new VariableBindingNode(vbn);
				int vvIdx = assignScalarOrList(mm.vv, mm.vvIdx, mm.vvIdx+size,
						vbn, mm.patternIdx);
				if (vvIdx < 0)
					continue;             // FIXME pop context?
				mm.vvIdx = vvIdx;         // increments by size if no failure
				valList.add(vbn);
				if (!doScalarTests(mm, valList))
					continue;             // FIXME pop context?
				if (mm.vbnIdx < mm.vbnArr.length) {  // next test, if any, must be multi
					doMultiTest(mm, valList, localOpen);
					continue;
				}
		   		if (trace.getDebugCode("mt")) trace.outNT("mt", "doMultiTest to push valList: "+valList);
	    		localOpen.push(new TestResult(valList, mm.fact));
			}
    		return n;
		} catch (Exception e) {
    		String errMsg = "Error assigning multislot entry to variable at line "+ 
			indexToLineNo(mm0.patternIdx)+": "+e.toString();
    		analysisErrors.add(errMsg);
    		trace.err(errMsg);
    		e.printStackTrace();
    		return 0;
		}
	}

	/**
	 * Class to bind a fact and a variable or variable list.
	 */
	private class TestResult {
		
		/** Single {@link VariableBindingNode} result. */
		final VariableBindingNode vbn;
		
		/** List of {@link VariableBindingNode} results. */
		final java.util.List vbnList;
		
		/** List of facts this result applies to. */
		final java.util.List factList;

		/**
		 * Constructor for single {@link VariableBindingNode} result and fact.
		 * @param  vbn value for {@link #vbn}
		 * @param  fact for {@link #factList}
		 */
		TestResult(VariableBindingNode vbn, Fact fact) {
			this.vbn = vbn;
			this.vbnList = null;
			factList = new ArrayList();
			factList.add(fact);
		}

		/**
		 * Constructor for single {@link VariableBindingNode} result and list
		 * of facts.
		 * @param  vbn value for {@link #vbn}
		 * @param  factList list to become {@link #factList}
		 */
		TestResult(VariableBindingNode vbn, java.util.List factList) {
			this.vbn = vbn;
			this.vbnList = null;
			this.factList = factList;
		}

		/**
		 * Constructor for list of {@link VariableBindingNode}s result and fact.
		 * @param  vbnList value for {@link #vbnList}
		 * @param  fact for {@link #factList}
		 */
		TestResult(java.util.List vbnList, Fact fact) {
			this.vbn = null;
			this.vbnList = vbnList;
			factList = new ArrayList();
			factList.add(fact);
		}

		/**
		 * Constructor for list of {@link VariableBindingNode}s result and list
		 * of facts.
		 * @param  vbnList value for {@link #vbnList}
		 * @param  factList list to become {@link #factList}
		 */
		TestResult(java.util.List vbnList, java.util.List factList) {
			this.vbn = null;
			this.vbnList = vbnList;
			this.factList = factList;
		}
		
		/**
		 * Whether a list-valued or single {@link VariableBindingNode} result.
		 * @return true if list-valued, else false
		 */
		boolean isList() {
			return vbnList != null;
		}
	}
	
    /**
     * Generate a list of alternative-binding lists that comprise all the
     * different ways the given multislot value could match the pattern's
     * slot test. 
     * @param val       Value from the multislot of the fact to be tested;
     *                  should be list-valued
     * @param vbn0      {@link VariableBindingNode} at start of slot
     * @param localOpen Stack onto which to push the result
     * @param fact      Fact whence the multislot value came
     * @param slotName  name of multislot for diagnostic msgs
     * @return          number of variables matched in this slot
     */
    private int bindMultislotPattern(Value val, VariableBindingNode vbn0,
    		Stack localOpen, Fact fact, String slotName){
    	int oldStackDepth = localOpen.size();
    	int slotIndex = vbn0.getSlotIndex();
    	String patternString = "(unset)";
		int patternIndex = -1;
		int n = 0;
		VariableBindingNode[] vbnArr = null;
    	java.util.List result = new ArrayList();
    	int nScalarTests = 0;
		try {
	    	Pattern pattern = (Pattern)patterns.get(vbn0.getDepth());
	    	patternString = (new PrettyPrinter(pattern)).toString();
			patternIndex = patterns.indexOf(pattern);
			vbnArr = makeMultislotVbnList(pattern, slotIndex, vbn0);
	    	ValueVector vv = val.listValue(context);
	    	nScalarTests = countScalarTests(vbnArr, vv, patternIndex, slotName);
	    	if (nScalarTests < 0)
	    		return 0;
	    	MultislotMatch mm = new MultislotMatch(vv, vbnArr, patternIndex,
	    			nScalarTests, fact);
	    	if (!doScalarTests(mm, result))
	    		return 0;
			if (mm.vbnIdx < mm.vbnArr.length) {
				int vType = vbnArr[mm.vbnIdx].getVariableType(); 
				if (vType == VariableBindingNode.MATCHES_MULTI) {
					n = doMultiTest(mm, result, localOpen);
		    		return localOpen.size() - oldStackDepth;
				} else {
		    		String errMsg = "Error analyzing multislot pattern at line "+ 
							indexToLineNo(patternIndex)+": unexpected test type";
		    		analysisErrors.add(errMsg);
		    		trace.err(errMsg+" "+vType+", mm.vbnIdx "+mm.vbnIdx);
					return 0;
				}
			} else if (mm.vbnIdx < vv.size()) {
				this.printRuleInstantiations(patternIndex,
						"multislot "+slotName+" has too many elements",
						FAILURE);
				return 0;
			} else {
	    		localOpen.push(new TestResult(result, fact));
	    		++n;
		    	return localOpen.size() - oldStackDepth;
	    	}
    	} catch (JessException je) {
    		String errMsg = "Jess error analyzing multislot pattern at line "+ 
					indexToLineNo(patternIndex)+": "+je.toString();
    		analysisErrors.add(errMsg);
    		trace.err(errMsg);
		    je.printStackTrace();
		    return 0;
    	} catch (Exception ex) {
    		String errMsg = "Error analyzing multislot pattern at line "+
					indexToLineNo(patternIndex)+": "+ex.toString();
	        analysisErrors.add(errMsg);
    		trace.err(errMsg);
		    ex.printStackTrace();
		    return 0;
		}
    }
    
    /**
     * this function matches the pattern against the list and extracts the values of the variables
     * and creates a VariableBindingNode for each variable value and adds it to the list of open nodes
     * this function returns the # of variables added to open list
     * 
     * @param val       The Value object from the fact corresponding to this multislot
     * @param pattern   The pattern that contains the variables to be matched
     * @param slotIndex The index of the multi slot in the pattern
     * 
     * @return number of variables matched in this slot
     */

    private int match_pattern(Value val, Pattern pattern, int slotIndex, Stack localOpen){
		VariableBindingNode vbn;
		ValueVector vv;
		ArrayList variableList = new ArrayList();
		int patternIndex = patterns.indexOf(pattern);
		int i = 0;	// the index of the variable in the pattern
		int n = 0;	// number of variables matched and added to the open list
		try {
			String patternString = (new PrettyPrinter(pattern)).toString();

			vv = val.listValue(null);

		    if (trace.getDebugCode("mt")) trace.outNT("mt", "match_pattern[p,slot,test "+patternIndex+","+slotIndex+","+0+
		            "] "+pattern+", val "+(val==null?"(null)":val.toStringWithParens())+
		            ", localOpen.size() "+
		            (localOpen==null?"(null)":Integer.toString(localOpen.size())));

		    // for each test in the slot corresponding to slotIndex
		    // One test for each variable
		    {
		        Test1 test = pattern.getTest(slotIndex,0);
		        vbn = getVariableType(test.getValue().toString(), patternIndex,test.getTest());
		    }
		    // pattern ($? ? ? ? ? $?) or pattern ($? ? ? ? ?)
			if (trace.getDebugCode("mt")) trace.outNT("mt","match_pattern top "+vbn.dump());
			
		    if (vbn.getVariableType() == VariableBindingNode.MATCHES_MULTI) {
//		    	|| vbn.getVariableType() == VariableBindingNode.LOCAL) {
			// get next variable
			    i = 1;
			    
					// look for the $ following first $ skip all the consecutive $ in the start of the pattern
			    {
			        Test1 test = pattern.getTest(slotIndex,i++);
			        vbn = getVariableType(test.getValue().toString(),patternIndex,
			                test.getTest());
			    }
				if (vbn.getVariableType() == VariableBindingNode.MATCHES_MULTI) { 
//				     || vbn.getVariableType() == VariableBindingNode.LOCAL){
				    // skip all subsequent $
				    do{
						if (trace.getDebugCode("mt")) trace.outNT("mt","match_pattern skip0 MULTI/LOCAL at test["+(i-1)+"] "+vbn.dump());
				        if (i < pattern.getNTests(slotIndex)) {
				            Test1 test = pattern.getTest(slotIndex,i++);
				            vbn = getVariableType(test.getValue().toString(),patternIndex,
				                    test.getTest());
				        }else{
				            break;
				        }
				    }while(vbn.getVariableType() == VariableBindingNode.MATCHES_MULTI);
//				    		|| vbn.getVariableType() == VariableBindingNode.LOCAL);
				}

				// look for the ? ? ? ? following the first $
				if (vbn.getVariableType() == VariableBindingNode.MATCHES_SINGLE ||
				        vbn.getVariableType() == VariableBindingNode.LITERAL) {
				    do{
						if (trace.getDebugCode("mt")) trace.outNT("mt","match_pattern add SINGLE/LITERAL at test["+(i-1)+"] "+vbn.dump());
						variableList.add(vbn);
						if (i < pattern.getNTests(slotIndex)) {
						    Test1 test = pattern.getTest(slotIndex,i++);
						    vbn = getVariableType(test.getValue().toString(),patternIndex,test.getTest());
						}else{
						    break;
						}
				    }while(vbn.getVariableType() == VariableBindingNode.MATCHES_SINGLE ||
				            vbn.getVariableType() == VariableBindingNode.LITERAL);
				}
				if (trace.getDebugCode("mt")) trace.outNT("mt","match_pattern at test["+i+"] of NTests "+pattern.getNTests(slotIndex)+
				        ", variableList size "+variableList.size());

				    // look for a $ following the $ ? ? ?...
				if (vbn.getVariableType() == VariableBindingNode.MATCHES_MULTI) {
//						|| vbn.getVariableType() == VariableBindingNode.LOCAL) {
				    // then this should be the last variable in the pattern or may 
				    // contain a $ - doesnt make sense to write a pattern $ $
				    do {
						if (trace.getDebugCode("mt")) trace.outNT("mt","match_pattern skip1 MULTI/LOCAL at test["+(i-1)+"] "+vbn.dump());
						if (i < pattern.getNTests(slotIndex)) {
						    Test1 test = pattern.getTest(slotIndex,i++);
						    vbn = getVariableType(test.getValue().toString(),patternIndex,test.getTest());
						}else{
						    break;
						}
				    } while (vbn.getVariableType() == VariableBindingNode.MATCHES_MULTI);
//				    		|| vbn.getVariableType() == VariableBindingNode.LOCAL);
				    
					if (trace.getDebugCode("mt")) trace.outNT("mt","match_pattern after 2nd multi at test["+i+"] of NTests "+
							pattern.getNTests(slotIndex)+", variableList size "+
							variableList.size()+"; last vbn "+vbn.dump());
		
				    if (i >= pattern.getNTests(slotIndex) &&
				    		(vbn.getVariableType() == VariableBindingNode.MATCHES_MULTI)) {
//				    				|| vbn.getVariableType() == VariableBindingNode.LOCAL)) {
						// now all the variables have been seen and the entire pattern is seen
						// assign the values to all variables
						// the variables can be matches to all the values in the fact sequentially
						// eg. If two variables are found and the list has six elements
						// then they can take on the values 
						// 0-1,1-2,2-3,3-4,4-5
						VariableBindingNode valueNode;
						// put the set of values in the open list
						// if the list is empty then put the value of each variable in the pattern as nil
						if (vv.size() == 0){
							// check to see if there is any ? variable ie the list should contain at least
							// one element if yes then pattern does not match print the rule instantiations
							boolean singleVar = false;
						    for (int m = (variableList.size() - 1); m >= 0 && !singleVar; m--) {
								vbn = (VariableBindingNode)variableList.get(m);
								if(vbn.getVariableType() == VariableBindingNode.MATCHES_SINGLE){
									singleVar = true;
									printRuleInstantiations(patternIndex,
											"there is no list entry for ?"+vbn.getVariableName(), FAILURE);
								}else if(vbn.getVariableType() == VariableBindingNode.LITERAL){
									singleVar = true;
									printRuleInstantiations(patternIndex,
											"there is no list entry to match the literal \""+
											vbn.getVariableValue()+"\"", FAILURE);
								}else{
									valueNode = new VariableBindingNode(vbn);
									valueNode.setValue(new Value(vv));
									if(valueNode.getVariableValue() != null)
										context.setVariable(valueNode.getExtVariableName(), valueNode.getVariableValue());
									valueNode.setDepth(patternIndex);
									valueNode.setSlotIndex(slotIndex);
									valueNode.setSubIndex(-1);
									currentValues.add(valueNode);
								}
						    }
						}
						for (int j = 0; j <= (vv.size() - variableList.size()); j++) {
						    // for all the consecutive variables found create a new value object for each 
						    // and add it to the list of open nodes.
						    n = 0;
						    int first,end;
						    end = vv.size();
						    first = 0;
						    for (int m = 0; m <variableList.size(); m++) {
								vbn = (VariableBindingNode)variableList.get(m);
								if(vbn.getVariableType() == VariableBindingNode.MATCHES_SINGLE ){
									valueNode = new VariableBindingNode(vbn);
									valueNode.setValue(vv.get(j + m));
									if(valueNode.getVariableValue() != null)
										context.setVariable(valueNode.getExtVariableName(), valueNode.getVariableValue());
									valueNode.setDepth(patternIndex);
									valueNode.setSlotIndex(slotIndex);
									valueNode.setSubIndex(j + m);
									n++;
									localOpen.push(valueNode);
								}else if(vbn.getVariableType() == VariableBindingNode.LITERAL){
									// compare the value of the literal with the value in the slot
									// if they match then proceed with the next variable in the list
									// else remove the previous n variables inserted in the local open
									// and continue with the next value from the list ie next value of j
									if(!vv.get(j + m).equals(vbn.getVariableValue())){
										for (int z = 0; z < n; z++){
											localOpen.pop();
										}
										break;
									}						
								}
						    }
						}
				    }else{
				        analysisErrors.add("At "+indexToLineNo(patternIndex)+
								": WhyNot cannot analyze multislot patterns of the form \"" +
				                patternString.replaceAll(RulePrinter.BLANK_VAR, "?")+"\"");
                		if (trace.getDebugCode("mt")) trace.outNT("mt", "not at end of MULTI list w/ n="+n+
				        		": found nonMULTI after 2nd MULT group at test["
				                +(i-1)+"] "+vbn.dump());
				    }
				}else if (i >= pattern.getNTests(slotIndex)) {
				    // pattern of the form ($? ? ? ?)
				    // now all the variables have been seen and the entire pattern is seen
				    // assign the values to all variables
				    // the variables can be matches to all the values in the fact sequentially
				    // eg. If two variables are found and the list has six elements
				    // then they can take on the values 
				    // ....N-2,N-1,N.
				    VariableBindingNode valueNode;
					// if the list is empty then put the value of each variable in the pattern as nil
					if (vv.size() == 0){
						printRuleInstantiations(patternIndex,
								"the list should not be empty",FAILURE);
						return 0;
					}
				    int m = 0;
				    for (int j = (vv.size() - variableList.size()); j < vv.size(); j++,m++) {
					// for all the consecutive variables found create a new value object for each 
					// and add it to the list of open nodes.
					
						vbn = (VariableBindingNode)variableList.get(m);
						if(vbn.getVariableType() == VariableBindingNode.MATCHES_SINGLE ){				
							valueNode = new VariableBindingNode(vbn);
							valueNode.setValue(vv.get(j));
							if(valueNode.getVariableValue() != null)
								context.setVariable(valueNode.getExtVariableName(), valueNode.getVariableValue());
							valueNode.setDepth(patternIndex);
							valueNode.setSlotIndex(slotIndex);
							valueNode.setSubIndex(j);
							n++;
							localOpen.push(valueNode);
						}else if(vbn.getVariableType() == VariableBindingNode.LITERAL){
							// compare the value of the literal with the value in the slot
							// if they match then proceed with the next variable in the list
							// else remove the previous n variables inserted in the local open
							// and continue with the next value from the list ie next value of j
							if(!vv.get(j).equals(vbn.getVariableValue())){
								for (int z = 0; z < n; z++){
									localOpen.pop();
								}
								break;
							}						
						}
				    }
				}else{
			        analysisErrors.add("At "+indexToLineNo(patternIndex)+
							": WhyNot cannot analyze multislot patterns of the form: \"" +
			                patternString.replaceAll(RulePrinter.BLANK_VAR, "?")+"\"");
            		if (trace.getDebugCode("mt")) trace.outNT("mt", "not MULTI, not LOCAL, not at end w/ i="+i+
            		        "?< pattern.getNTests(slotIndex="+slotIndex+")="+
            		        pattern.getNTests(slotIndex));
				    printRuleInstantiations(patternIndex,null,FAILURE);
				    return 0;
				}
		    }else if (vbn.getVariableType() == VariableBindingNode.MATCHES_SINGLE) {
				// pattern (? ? ? ? $?)
				// look for the ? ? ? ? in the start of the pattern
				i = 1;
				do{
				    variableList.add(vbn);
				    if (i < pattern.getNTests(slotIndex)) {
						Test1 test = pattern.getTest(slotIndex,i++);
						vbn = getVariableType(test.getValue().toString(),patternIndex,test.getTest());
				    }else{
						break;
				    }
				}while(vbn.getVariableType() == VariableBindingNode.MATCHES_SINGLE);
		
				// now look for the $ following the ? ? ? ? ? ?
				if (vbn.getVariableType() == VariableBindingNode.MATCHES_MULTI) {
//					    || vbn.getVariableType() == VariableBindingNode.LOCAL) {
				    // then this should be the last variable in the pattern or may 
				    // contain a $ - doesnt make sense to write a pattern $ $
				    do {
						if (i < pattern.getNTests(slotIndex)) {
						    Test1 test = pattern.getTest(slotIndex,i++);
						    vbn = getVariableType(test.getValue().toString(),patternIndex,test.getTest());
						}else{
						    break;
						}
				    } while (vbn.getVariableType() == VariableBindingNode.MATCHES_MULTI);
//				    		|| vbn.getVariableType() == VariableBindingNode.LOCAL);
					if (!(vbn.getVariableType() == VariableBindingNode.MATCHES_MULTI)) {
//						    || vbn.getVariableType() == VariableBindingNode.LOCAL)) {
				        analysisErrors.add("At "+indexToLineNo(patternIndex)+
								": WhyNot cannot analyze multislot patterns of the form: \"" +
				                patternString.replaceAll(RulePrinter.BLANK_VAR, "?")+"\"");
	            		if (trace.getDebugCode("mt")) trace.outNT("mt", "Pattern not of type ? ? ? ? ? $ $ $ w/ i="+i+
	            		        "?< pattern.getNTests(slotIndex="+slotIndex+")="+
	            		        pattern.getNTests(slotIndex));
						printRuleInstantiations(patternIndex,null,"FAILED to Match pattern");
						return 0;
					}
				}
				if (i >= pattern.getNTests(slotIndex)) {
				    // now all the variables have been seen and the entire pattern is seen
				    // assign the values to all variables and create instances of VariableBindingNode and 
				    // put them in the current variables list
		
				    VariableBindingNode valueNode;
				    // for all the consecutive variables found create a new value object for each 
				    // and add it to the list of currentValues.
				    // 0 1 2 3 .....
					// if the list is empty then put the value of each variable in the pattern as nil
					if (vv.size() == 0){
						boolean singlevar = false;
						
					    for (int m = (variableList.size() - 1); m >= 0; m--) {
					    	
							vbn = (VariableBindingNode)variableList.get(m);
							if(vbn.getVariableType() == VariableBindingNode.MATCHES_SINGLE ||
									vbn.getVariableType() == VariableBindingNode.LITERAL){
								printRuleInstantiations(patternIndex,
										"Need a non-empty list", FAILURE);
							}else{
								valueNode = new VariableBindingNode(vbn);
								valueNode.setValue(new Value(vv));
								if(valueNode.getVariableValue() != null)
									context.setVariable(valueNode.getExtVariableName(), valueNode.getVariableValue());
								valueNode.setDepth(patternIndex);
								valueNode.setSlotIndex(slotIndex);
								valueNode.setSubIndex(-1);
								currentValues.add(valueNode);
							}
					    }
					}else{
					    for (int m = 0; m < variableList.size(); m++) {
							vbn = (VariableBindingNode)variableList.get(m);
							if(vbn.getVariableType() == VariableBindingNode.MATCHES_SINGLE ){
								valueNode = new VariableBindingNode(vbn);
								valueNode.setValue(vv.get(m));
								if(valueNode.getVariableValue() != null)
									context.setVariable(valueNode.getExtVariableName(), valueNode.getVariableValue());
								valueNode.setDepth(patternIndex);
								valueNode.setSlotIndex(slotIndex);
								valueNode.setSubIndex(m);
								n++;
								localOpen.push(valueNode);
							}else if (vbn.getVariableType() == VariableBindingNode.LITERAL){
								// compare the value of the literal with the value in the slot
								// if they match then proceed with the next variable in the list
								// else remove the previous n variables inserted in the local open
								// and continue with the next value from the list ie next value of j
								if(!vv.get(m).equals(vbn.getVariableValue())){
									for (int z = 0; z < n; z++){
										localOpen.pop();
									}
									break;
								}						
							}
					    }
					}
				}else{
			        analysisErrors.add("At "+indexToLineNo(patternIndex)+
							": WhyNot cannot analyze multislot patterns of the form: \"" +
			                patternString.replaceAll(RulePrinter.BLANK_VAR, "?")+"\"");
            		if (trace.getDebugCode("mt")) trace.outNT("mt", "Pattern not of type ? ? ? ? ? $ $ $ w/ i="+i+
            		        "?< pattern.getNTests(slotIndex="+slotIndex+")="+
            		        pattern.getNTests(slotIndex));
					printRuleInstantiations(patternIndex,null,"FAILED to Match pattern");
				}
		    }else if (vbn.getVariableType() == VariableBindingNode.LITERAL) {
			// not a variable but a literal. FIXME "nil" handling?
				VariableBindingNode valueNode;
				if(vv.size() == 0 && vbn.getVariableValue().stringValue(context).equalsIgnoreCase("nil")){
					n = 1;
					valueNode = new VariableBindingNode(vbn);
					localOpen.push(valueNode);
				}else if(vv.size() == 0 && !vbn.getVariableValue().stringValue(context).equalsIgnoreCase("nil")){
					printRuleInstantiations(patternIndex,
					        "There is no list entry to match "+vbn.getVariableValue().toString(),FAILURE);
				}else if (!vbn.getVariableValue().toString().equals(vv.get(vbn.getSubSlotIndexes()).toString())){
					printRuleInstantiations(patternIndex,
					        "No list entry matches "+vbn.getVariableValue().toString(),FAILURE);
				}else{
					n = 1;
					valueNode = new VariableBindingNode(vbn);
					localOpen.push(valueNode);
				}
		    }
		}catch(Exception ex){
	        analysisErrors.add("Error analyzing pattern at line "+
					indexToLineNo(patternIndex)+": "+ex);
		    ex.printStackTrace();
		}
		return n;
    }

    /**
     * This method returns variableBindingNode for this variable
     * 
     * @param variableName
     *               The name of the variable
     * 
     * @return The variableBindingNode object for this variable
     */
    public VariableBindingNode getVariableType(String variableName, int depth, int testType){
		
		int i,variableType;
		VariableBindingNode varBindingNode = null;
		StringBuffer sb = new StringBuffer(variableName);
		// get the type of the variable
		if(testType == RU.FUNCALL){
			variableType = VariableBindingNode.FUNCALL;
		}else
		if (sb.charAt(0) == '$') {
		    if (sb.length() > 1 && sb.charAt(1) == '?') {
		        // multi variable
		        sb.delete(0,2);
//		        if (sb.charAt(0) == '_') {
//		            // local variable do not use this as this notation might change
//		            // i think the type of the local variable is set as RU.LOCAL
//		            // do not add this variable in the variable list
//		            variableType = VariableBindingNode.LOCAL;
//		        }
//		        else
		            // this represents a variable that matches multiple values and it should
		            // also be stored in the variable list
		            variableType = VariableBindingNode.MATCHES_MULTI;
		    }else{
		        variableType = VariableBindingNode.BAD_VARIABLE;
		    }
		}else if (sb.charAt(0) == '?'){
		    // variable matches a single value
		    // add it to the list of variables
		    variableType = VariableBindingNode.MATCHES_SINGLE;
		    sb.deleteCharAt(0);
		    // check to see if the variable already present in the list of current variables 
		    // if not then add it to the list of variables otherwise leave it do nothing
		} // else its a literal and not a variable hence do not add it to the variable list
		else {
		    // do nothing with it now
		    variableType = VariableBindingNode.LITERAL;
		}
	    if ((i = containsVariable(0,vMap,sb.toString())) == -1) {
			varBindingNode = new VariableBindingNode(sb.toString(),variableType,
			        VariableBindingNode.MULTISLOT,testType);  // FIXME : why multislot?
			if (trace.getDebugCode("mt")) trace.outNT("mt", "getVariableType(): new vbn "+varBindingNode.dump());  // FIXME : add new vbn to vMap?
	    }else{
			varBindingNode = (VariableBindingNode)vMap.get(i);
			if (trace.getDebugCode("mt")) trace.outNT("mt", "getVariableType(): existing vMap["+i+"] "+varBindingNode.dump());
	    }
	    if(variableType == VariableBindingNode.LITERAL){
			try {
			    varBindingNode.setValue(new Value(variableName,RU.SYMBOL));
				if (trace.getDebugCode("mt")) trace.outNT("mt", "context.< for LITERAL:"+varBindingNode.dump());
				// FIXME (fixed?) don't set LITERALs in context
			    // if(varBindingNode.getVariableValue() != null)
			    //	context.setVariable(varBindingNode.getVariableName(), varBindingNode.getVariableValue());
			} catch (JessException e) {
				e.printStackTrace();
			}
	    }
	    if ((i = containsVariable(0,currentValues,sb.toString())) != -1){
	    	// FIXME ? if currentValues[i] not yet bound, how will this LITERAL ever get a value?
	    	variableType = VariableBindingNode.LITERAL;
			varBindingNode = new VariableBindingNode((VariableBindingNode)currentValues.get(i));
			if (trace.getDebugCode("mt")) trace.outNT("mt", "new LITERAL for vbn at currentValues["+i+"] "+varBindingNode.dump());
			varBindingNode.setVariableType(variableType);
	    }
	    String fIdVstr = "not a fact";
	    try {
	    	if (varBindingNode.getVariableValue() instanceof FactIDValue) {
	    		FactIDValue fIdV = (FactIDValue) varBindingNode.getVariableValue();
	    		fIdVstr = fIdV.factValue(context).toString();
	    	}
	    } catch (JessException je) {
	    	fIdVstr = je.toString();
	    }
	    if (trace.getDebugCode("mt")) trace.outNT("mt", "getVariableType returning "+varBindingNode.dump()+":\n  "+fIdVstr);
		return varBindingNode;
    }
    public void setTextArea(JTextArea ta){
		this.textArea = ta;
    }
	/**
	 * Method reasonOut.
	* Performs the why-not and displays the Why-Not window.
	 * @return Arraylist - returns the list of rule instantiations
	 */
	public ArrayList reasonOut(WMEEditor wmeeditor) {
		doPreprocessing();
		// get the first variable
		StringBuffer traceSB = new StringBuffer("vMap after doPreprocessing():");
		for (Iterator it = vMap.iterator(); it.hasNext(); )
		    traceSB.append("\n  ").append(((VariableBindingNode)it.next()).dump());
		if (trace.getDebugCode("mt")) trace.outNT("mt", traceSB.toString());
		VariableBindingNode vbn = (VariableBindingNode)vMap.get(0);
		try {
			getRuleInstantiations(vbn, null);
		} catch (Exception e) {
			String errMsg = "Error analyzing rule";
			analysisErrors.add(errMsg+": "+e);
			trace.err(errMsg);
			e.printStackTrace();
		}

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		if (rulePrinter == null)
			rulePrinter = new RulePrinter(rule);
		JLabel ruleLabel = new JLabel("Rule definition:");
		ruleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		getContentPane().add(ruleLabel);

		ruleDisplay = new JTextArea(40, 100) {
			/**
			 * Tool tips for highlighted variables in the rule. The
			 * mouse-over action also marks the corresponding row in
			 * the variable table below.
			 * @param e mouse event from which to find the highlight
			 *         value of var
			 */
		    public String getToolTipText(MouseEvent e) {
		    	String tipText = null;
		    	int oldMouseOverRow = mouseOverRow;
				int offset = ruleDisplay.viewToModel(e.getPoint());
				String varName = getHighlightedVar(offset);
				if (vbnTableModel == null)
					mouseOverRow = -1;
				else {
					mouseOverRow = vbnTableModel.findDisplayName(varName);
					if (mouseOverRow >= 0)
						tipText = vbnTableModel.getToolTipText(mouseOverRow);
				}
				if(oldMouseOverRow != mouseOverRow)
					varTable.repaint();
				if (trace.getDebugCode("mt")) trace.out("mt", "getToolTipText("+varName+") rtns "+tipText);
		    	return tipText;
			}
		};
		ruleDisplay.setToolTipText("rule text area tool tip");
		ruleDisplay.setText(rulePrinter.toString());  //double-space rule
		ruleDisplay.setEditable(false);
		ruleDisplay.setFont(Font.decode("Courier-normal-12"));
		ruleDisplay.setCaretPosition(0);
		JScrollPane ruleScroll = new JScrollPane(ruleDisplay);
		ruleScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
		ruleScroll.setPreferredSize(new Dimension(700, 480));
		ruleScroll.setMinimumSize(new Dimension(700, 250));
		split.add(ruleScroll);
		
		if (analysisErrors.size() > 0) {
			JEditorPane errorDisplay = new JEditorPane();
			errorDisplay.setContentType("text/html");
			errorDisplay.setAutoscrolls(true);
			errorDisplay.setEditable(false);
			StringBuffer sb = new StringBuffer("<b>Unable to analyze rule. Reason(s):</b><ul>");
			for (Iterator it = analysisErrors.iterator(); it.hasNext();)
			    sb.append("\n<li>").append((String)it.next()).append("</li>");
		    sb.append("\n</ul></html>");
			errorDisplay.setText(sb.toString());
			errorDisplay.setCaretPosition(0);
			JScrollPane errorScroll = new JScrollPane(errorDisplay);
			errorScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
			errorScroll.setPreferredSize(new Dimension(700, 250));
			errorScroll.setMinimumSize(new Dimension(700, 100));
			split.add(errorScroll);
			getContentPane().add(split);
			setSize(new Dimension(700, 600));
			show();
			return resultsList;
		}

		//sort instantiations by number of variables bound
		ArrayList sortedResults = new ArrayList();
		ArrayList sortedSummaries = new ArrayList();
		int[] a, b;
		int temp;
		for (int p = 0; p < index_varsList.size(); p++) {
			a = (int[]) index_varsList.get(p);
			for (int q = p; q < index_varsList.size(); q++) {
				b = (int[]) index_varsList.get(q);
				if (a[1] < b[1]) {
					temp = a[0];
					a[0] = b[0];
					b[0] = temp;

					temp = a[1];
					a[1] = b[1];
					b[1] = temp;
				}
			}
		}
		for (int p = 0; p < index_varsList.size(); p++) {
			a = (int[]) index_varsList.get(p);
			sortedResults.add(resultsList.get(a[0]));
			sortedSummaries.add(summaries.get(a[0]));
		}
		resultsList = resultsListFiltered = sortedResults;
		summaries = summaryListFiltered = sortedSummaries;
		
		Box listBox = new Box(BoxLayout.Y_AXIS);
		JLabel instLabel = new JLabel("Partial Activations:");
		instLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		Vector instText = new Vector();
		for(ListIterator li = resultsListFiltered.listIterator(), sum = summaryListFiltered.listIterator(); li.hasNext();){
			if(sum.next().toString().equals(ALL_VARIABLES_BOUND_SUCCESSFULLY))
				//black (was green) text for all vars bound
				instText.add("<html><font color=\"black\">" +
						((VBNTableModel)li.next()).getRowCount() + " variables bound</font></html>");
			else
				//red text for some vars not bound
				instText.add("<html><font color=\"red\">" +
						((VBNTableModel)li.next()).getRowCount() + " variables bound</font></html>");
		}
		if(instText.isEmpty()){
			//for some reason we don't have any instantiations
			instText.add("No variables bound");
		}
		instList = new JList(instText);
		instList.setPrototypeCellValue("999 variables bound");
		instList.setAlignmentX(Component.RIGHT_ALIGNMENT);
		instList.setSelectionBackground(Color.LIGHT_GRAY);
		instList.addListSelectionListener(this);
		JScrollPane instListScroll = new JScrollPane(instList);
		listBox.add(instLabel);
		listBox.add(instListScroll);

		Box instBox = new Box(BoxLayout.PAGE_AXIS);
		
		// FIXME:  Create wide blank label so box will align to left.
		summary = new JLabel(" ");
		summary.setAlignmentX(0);
		instBox.add(summary);

		TableModel varTableModel = new VBNTableModel(this, null);
		class VarTable extends JTable {
			//Constructor
			VarTable(TableModel tableModel) {
				super(tableModel);
			}
		    //Implement table cell tool tips.
		    public String getToolTipText(MouseEvent e) {
		        String tip = null;
		        java.awt.Point p = e.getPoint();
		        int row = rowAtPoint(p);
		        int column = columnAtPoint(p);
		        int mColumn = convertColumnIndexToModel(column);
		        TableModel tm = this.getModel();
		        if (tm instanceof VBNTableModel) {
		        	Object cellContent = ((VBNTableModel) tm).getObjectAt(row, mColumn);
		        	if (cellContent instanceof VBNTableModel.Entry)
			        	return ((VBNTableModel.Entry) cellContent).getToolTipText();
		        }
		        Object cellContent = tm.getValueAt(row, mColumn);
		        return cellContent.getClass().getName();
		    }
			//Replace tool tip
			public JToolTip createToolTip() {
				if (trace.getDebugCode("mt")) trace.out("mt", "table createToolTip()");
				return super.createToolTip();
			}
		};
		class VarTableCellRenderer extends DefaultTableCellRenderer {
			//cell renderer for background colors, red foreground for error, black border for mouse-over
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				Component c =
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				c.setBackground(colors[row % colors.length]);
				c.setForeground(Color.BLACK);
				if(row == errorRow)
					c.setForeground(Color.RED);
				((JComponent)c).setBorder(null);
				if(row == mouseOverRow)
					((JComponent)c).setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
				return c;
			}
		};
		varTable = new VarTable(varTableModel);
		varTable.setDefaultRenderer(Object.class, new VarTableCellRenderer());
		varTable.setAlignmentX(0);

		JScrollPane varTableScroll = new JScrollPane(varTable);

		saiColumnNames = new Vector();
		saiColumnNames.add(" ");  //need a space, otherwise the header row displays extremely short
		saiColumnNames.add("Rule");
		saiColumnNames.add("Student Action");
		saiTable = new JTable(new DefaultTableModel(null, saiColumnNames){
			public boolean isCellEditable(int r, int c){return false;}
		});
		saiTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer(){
			//cell renderer shows green background for match, red for no match
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if(value.toString().length() > 0){
					if(reqSAI.get(row).equals(actualSAI.get(row)) || actualSAI.get(row).toString().equalsIgnoreCase("DONT-CARE")){
						c.setBackground(new Color(0.3f, 1f, 0.3f));
					} else{
						c.setBackground(new Color(1f, 0.3f, 0.3f));
					}
				}
				return c;
			}
		});
		saiTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer(){
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if(value.toString().length() > 0){
					if(reqSAI.get(row).equals(actualSAI.get(row)) || actualSAI.get(row).toString().equalsIgnoreCase("DONT-CARE")){
						c.setBackground(new Color(0.3f, 1f, 0.3f));
					} else{
						c.setBackground(new Color(1f, 0.3f, 0.3f));
					}
				}
				return c;
			}
		});

		if (false) {  // FIXME restore when can put s/a/i table here
			JScrollPane saiTableScroll = new JScrollPane(saiTable);
			saiTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
			saiTableScroll.setMinimumSize(new Dimension(0, 70));
			instBox.add(Box.createVerticalStrut(10));
			instBox.add(saiTableScroll);
		}
		
		wmeeditor.getPanel().setSize(new Dimension(600,300));
		wmeeditor.getPanel().setAlignmentX(0);
		// instBox.add(wmeeditor);    see splitW below 
		instList.setSelectedIndex(0);
        wmeeditor.getPanel().refresh();

        JSplitPane splitW = new JSplitPane(JSplitPane.VERTICAL_SPLIT, varTableScroll, wmeeditor.getPanel());
        splitW.setResizeWeight(0.5);
		instBox.add(splitW);
        
		Box detailBox = new Box(BoxLayout.X_AXIS);
		detailBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		detailBox.add(listBox);
		detailBox.add(Box.createHorizontalStrut(10));
		detailBox.add(instBox);
//		detailBox.setPreferredSize(new Dimension(880, 300));
//		detailBox.setMaximumSize(new Dimension(700, 300));
//		detailBox.setMinimumSize(new Dimension(700, 300));
		split.add(detailBox);

		getContentPane().add(split);

		if (false) {        // FIXME disable filter box until fixed

			//construct first filter panel

			Vector vars = new Vector();
			if (resultsList != null && resultsList.size() > 0) {
			    for(Iterator it = ((VBNTableModel)resultsList.get(0)).nameIterator(); it.hasNext();){
			        vars.add((String)it.next());
			    }
			}
			Vector vals = new Vector();
			for(ListIterator li = resultsList.listIterator(); li.hasNext();){
				Vector binding = (Vector)li.next();
				for(ListIterator var_it = binding.listIterator(); var_it.hasNext();){
					Vector v = (Vector)var_it.next();
					if(v.get(0).equals(vars.get(0)) && !vals.contains(v.get(1))){
						vals.add(v.get(1));
					}
				}
			}
			filterBox = new Box(BoxLayout.Y_AXIS);
		    
		    if(!vars.isEmpty()){
		        filters = new Vector();
		        newFilter = new Vector();
		        Box newFilterPanel = new Box(BoxLayout.X_AXIS);
		        newFilterPanel.add(new JLabel("Show only cases where "));
		        JComboBox newFilterVar = new JComboBox(vars);
		        newFilterVar.setSelectedIndex(0);
		        newFilterVar.setActionCommand(WhyNot.FILTER_VARIABLE);
		        newFilterVar.addActionListener(this);
		        newFilterPanel.add(newFilterVar);
		        newFilterPanel.add(new JLabel(" equals "));
		        JComboBox newFilterVal = new JComboBox(vals);
		        newFilterVal.setSelectedIndex(0);
		        newFilterVal.setActionCommand(WhyNot.FILTER_VALUE);
		        newFilterVal.addActionListener(this);
		        newFilterPanel.add(newFilterVal);
		        JButton apply = new JButton("Apply");
		        apply.setActionCommand(WhyNot.FILTER_APPLY);
		        apply.addActionListener(this);
		        newFilterPanel.add(apply);
		        newFilterPanel.add(Box.createVerticalGlue());
		        newFilter.add(newFilterPanel);
		        newFilter.add(newFilterVar);
		        newFilter.add(newFilterVal);
		        newFilter.add(apply);
		        newFilterPanel.setMaximumSize(new Dimension(700, 25));	
		        filterBox.add(newFilterPanel);
		    }
		    
		    filterScroll = new JScrollPane(filterBox);
		    filterScroll.setPreferredSize(new Dimension(700, 100));
		    filterScroll.setMaximumSize(new Dimension(700, 100));
		    filterScroll.setMinimumSize(new Dimension(700, 100));
		    filterScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
		    getContentPane().add(filterScroll);
		}
		setSize(new Dimension(760, 800));
		validate();
		setVisible(true);
		return resultsList;
	}
	
	/**
	 * Sets the actualSAI.
	 * @param actualSAI The actualSAI to set
	 */
	private void setActualSAI(Vector actualSAI) {
		this.actualSAI = actualSAI;
	}

	/**
	 * Sets the reqSAI.
	 * @param reqSAI The reqSAI to set
	 */
	public void setReqSAI(Vector reqSAI) {
		this.reqSAI = reqSAI;
	}

	/**
	 * Sets the reqSAI.
	 * @param nodeSAI The nodeSAI to set
	 */
	public void setNodeSAI(Vector nodeSAI) {
		this.nodeSAI = nodeSAI;
	}

	/**
	 * Returns the index_depthList.
	 * @return ArrayList
	 */
	public ArrayList getIndex_depthList() {
		return index_depthList;
	}

	public ArrayList getIndex_varsList(){
		return index_varsList;
	}

	/**
	 * Sets the index_depthList.
	 * @param index_depthList The index_depthList to set
	 */
	public void setIndex_depthList(ArrayList index_depthList) {
		this.index_depthList = index_depthList;
	}

	public void valueChanged(ListSelectionEvent e){

		//new instantiation selected
		int selectedIndex = instList.getSelectedIndex();
	   // getEventLogger().log("WhyNot", "instantiation list index", 
	   // 	    new Integer(selectedIndex));
		
	    if(selectedIndex < 0 || resultsListFiltered.size() <= selectedIndex) {
	    	if (resultsListFiltered.size() <= selectedIndex)
		    	System.err.println("\nError in WhyNot.valueChanged(): selected index "+
		    			selectedIndex+" out of list range "+resultsListFiltered.size()+".\n");
			//no instantiation selected, clear everything
			summary.setText(resultsListFiltered.size() < 1 ?
			        "No partial instantiations." : " ");
			varTable.setModel(new VBNTableModel(this, null));
			saiTable.setModel(new DefaultTableModel(null, saiColumnNames){
				public boolean isCellEditable(int r, int c){return false;}
			});
			repaint();
			return;
		}

	    vbnTableModel = (VBNTableModel)resultsListFiltered.get(instList.getSelectedIndex());
		if(vbnTableModel.getRowCount() < 1) {
			summary.setText("No variables could be bound for this partial activation.");
			varTable.setModel(vbnTableModel);
			saiTable.setModel(new DefaultTableModel(null, saiColumnNames){
				public boolean isCellEditable(int r, int c){return false;}
			});
			repaint();
			return;
		}

		java.util.regex.Pattern p;
		Matcher m;

		//try to figure out SAI from rule, if applicable
		p = java.util.regex.Pattern.compile("\\(modify \\?special-tutor-fact \\(selection ([^\\)]*)\\) \\(action ([^\\)]*)\\) \\(input ([^\\)]*)\\)");
		m = p.matcher(ruleDisplay.getText());
		actualSAI = new Vector();
		if(!(m.find())) {
			if (trace.getDebugCode("mt")) trace.outNT("mt", "actualSAI: not match (modify ...)");
		} else {
		    for(int i=1; i<=3; i++){
		        String g = m.group(i);
				if (trace.getDebugCode("mt")) trace.outNT("mt", "actualSAI["+i+"] match \""+g+"\"");
		        if(g.charAt(0) == '?'){
		            //variable - determine value
		        	int varIndex = vbnTableModel.findDisplayName(g);
		        	if (varIndex >= 0)
		        		actualSAI.add(vbnTableModel.getDisplayValue(varIndex));
					if (trace.getDebugCode("mt")) trace.outNT("mt", "actualSAI["+i+"] varIndex="+varIndex+
							" for \""+g+"\"");
		        }
		        else if(g.charAt(0) == '"'){
		            //string - strip quotes
		            actualSAI.add(g.substring(1, g.length()-1));
		        }
		        else{
		            //literal - use as is
		            actualSAI.add(g);
		        }
		    }
		}

		String summaryString = summaryListFiltered.get(instList.getSelectedIndex()).toString();

		boolean showSAI = false;
		summary.setForeground(Color.RED);
		if(summaryString.equals(ALL_VARIABLES_BOUND_SUCCESSFULLY)) {
			Color[] summaryColor = {new Color(0f, 0.5f, 0f)}; 
	        summaryString = makeSAISummary(summaryString, actualSAI, reqSAI,
	        		nodeSAI, summaryColor);
			summary.setForeground(summaryColor[0]);
//	        if (actualSAI.size() == 3 && reqSAI.size() >= 3) {
//		        showSAI = true;
//		    }
		}
		summary.setText(summaryString);

		ruleDisplay.getHighlighter().removeAllHighlights();

		errorRow = -1;
		p = java.util.regex.Pattern.compile(FOR_THE_VARIABLE_OR_EXPRESSION+"(.*)"+REQUIRED_VALUE_WAS+".*");
		m = p.matcher(summaryString);
		if(m.matches()){
			//variable mismatch - determine error row
			int varNum = 0;
			for(Iterator vn_it = vbnTableModel.nameIterator(); vn_it.hasNext(); varNum++) {
				String varName = (String) vn_it.next();
				if (varName != null && varName.equals(m.group(1))){
					errorRow = varNum;
					break;
				}
			}			
		} else{
			p = java.util.regex.Pattern.compile("Could not match pattern for (.*)\\.");
			m = p.matcher(summaryString);
			if(m.matches()){
				//pattern match failure - highlight pattern in red
				java.util.regex.Pattern var_p = java.util.regex.Pattern.compile("\\Q" + m.group(1) + " <- \\E");
				Matcher var_m = var_p.matcher(ruleDisplay.getText());
				while(var_m.find()){
					try{
						ruleDisplay.getHighlighter().addHighlight(var_m.start(), var_m.end() - 1,
							new DefaultHighlighter.DefaultHighlightPainter(new Color(1f, 0.3f, 0.3f)));
					} catch(BadLocationException exc){
						if (trace.getDebugCode("mt")) trace.outNT("mt", "WhyNot.valueChanged() error while highlighting pattern: "+
						        exc.toString());
					}
				}
			}
		}

		if (vbnTableModel == null){
			vbnTableModel = new VBNTableModel(this, null);
        }
		varTable.setModel(vbnTableModel);
		int varNum = 0;
		//highlight variable names in rule text
		for(Iterator varName_it = vbnTableModel.nameIterator(); varName_it.hasNext();){
			String varName = (String) varName_it.next();
			java.util.regex.Pattern var_p = java.util.regex.Pattern.compile("\\Q" + varName + "\\E[^a-zA-Z0-9\\-_]");
			Matcher var_m = var_p.matcher(ruleDisplay.getText());
			while(var_m.find()){
				try{
					ruleDisplay.getHighlighter().addHighlight(var_m.start(), var_m.end() - 1,
						new DefaultHighlighter.DefaultHighlightPainter(colors[varNum % colors.length]));
				} catch(BadLocationException exc){
					if (trace.getDebugCode("mt")) trace.outNT("mt", "WhyNot.valueChanged() error while highlighting variable: "+
					        exc.toString());
				}
			}
			varNum++;
		}

		if(showSAI && actualSAI.size() == 3 && reqSAI.size() == 3){
			//populate SAI table
			Vector saiRowData = new Vector();
			Vector selectionRow = new Vector();
			selectionRow.add("<HTML><B>Selection</B></HTML>");
			selectionRow.add(actualSAI.get(0).toString());
			selectionRow.add(reqSAI.get(0).toString());
			saiRowData.add(selectionRow);
			Vector actionRow = new Vector();
			actionRow.add("<HTML><B>Action</B></HTML>");
			actionRow.add(actualSAI.get(1).toString());
			actionRow.add(reqSAI.get(1).toString());
			saiRowData.add(actionRow);
			Vector inputRow = new Vector();
			inputRow.add("<HTML><B>Input</B></HTML>");
			inputRow.add(actualSAI.get(2).toString());
			inputRow.add(reqSAI.get(2).toString());
			saiRowData.add(inputRow);
			saiTable.setModel(new DefaultTableModel(saiRowData, saiColumnNames){
				public boolean isCellEditable(int r, int c){return false;}
			});
		} else{
			//no SAI to display
			saiTable.setModel(new DefaultTableModel(null, saiColumnNames){
				public boolean isCellEditable(int r, int c){return false;}
			});
		}

		saiTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer(){
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if(value.toString().length() > 0){
					if(reqSAI.get(row).equals(actualSAI.get(row)) || actualSAI.get(row).toString().equalsIgnoreCase("DONT-CARE")){
						c.setBackground(new Color(0.3f, 1f, 0.3f));
					} else{
						c.setBackground(new Color(1f, 0.3f, 0.3f));
					}
				}
				return c;
			}
		});
		saiTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer(){
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if(value.toString().length() > 0){
					if(reqSAI.get(row).equals(actualSAI.get(row)) || actualSAI.get(row).toString().equalsIgnoreCase("DONT-CARE")){
						c.setBackground(new Color(0.3f, 1f, 0.3f));
					} else{
						c.setBackground(new Color(1f, 0.3f, 0.3f));
					}
				}
				return c;
			}
		});

		StringBuffer sb = new StringBuffer();
		if (vbnTableModel != null)
			for(int i = 0; i < vbnTableModel.getRowCount(); i++)
			{
				sb.append(vbnTableModel.getValueAt(i, 0) + " = " + 
						vbnTableModel.getValueAt(i, 1) + "\n");
			}

		// if e.getValueIsAdjusting() is true, that means this is one of a couple of the
		// same log entry, so only log it once.
		if (!e.getValueIsAdjusting())
			getEventLogger().log(true, 
					AuthorActionLog.WHY_NOT_WINDOW,
					WhyNot.INSTANTIATION_INDEX,
					selectedIndex + ": " + summary.getText(),
					ruleName, sb.toString());

		repaint();
	}

	/**
	 * Revise the summary statement according to which of selection, action
	 * or input (s/a/i) match between the model's expected values and the
	 * student request's values. 
     * @param  result summary preset to desired "all s/a/i match" text
     * @param  actualSAI rule's expected s/a/i, in entries 0,1,2 respectively
     * @param  reqSAI s/a/i from student request, in entries 0,1,2 respectively
     * @param  nodeSAI model's expected s/a/i; maybe null
     * @param  summaryColor to return a color for this summary
     * @return result, revised if s, a or i failed to match
     */
    private String makeSAISummary(String result,
            java.util.List actualSAI, java.util.List reqSAI,
			java.util.List nodeSAI, Color[] summaryColor){
    	if (reqSAI == null || reqSAI.size() < 3 ||
    			nodeSAI == null || nodeSAI.size() < 3 ||
				MTRete.isSAIUnspecified(nodeSAI))  //CTAT1188
    		return result;                         // student s/a/i to match
    	if (true) {  // see note below: rest disabled
    		summaryColor[0] = Color.BLACK;              
    		return result+" Click on S/A/I cells in the Conflict Tree to check selection, action, input.";
    	}
    	/*
    	 * *** Rest of this disabled until can display correct s/a/i in WhyNot.
    	 */
    	if (actualSAI == null || actualSAI.size() < 3) {//CTAT1189
    		summaryColor[0] = Color.BLACK;              
    		return result+" Click on the S/A/I cells in the Conflict Tree to check selection, action, input.";
    	}
    		
        ArrayList saiMatchFail = new ArrayList();  // CTAT1066
        
        if(!actualSAI.get(0).equals(reqSAI.get(0)) && !actualSAI.get(0).toString().equalsIgnoreCase("DONT-CARE")){
            saiMatchFail.add("Selection");
        }
        if(!actualSAI.get(1).equals(reqSAI.get(1)) && !actualSAI.get(1).toString().equalsIgnoreCase("DONT-CARE")){
            saiMatchFail.add("Action");
        }
        if(!actualSAI.get(2).equals(reqSAI.get(2)) && !actualSAI.get(2).toString().equalsIgnoreCase("DONT-CARE")){
            saiMatchFail.add("Input");
        }
        if (saiMatchFail.size() > 0) {
            int i = 0;
            summaryColor[0] = Color.RED;
            StringBuffer smfSummary =
                new StringBuffer((String) saiMatchFail.get(i));
            while ((++i) < saiMatchFail.size())
                smfSummary.append(", ").append((String) saiMatchFail.get(i));
            smfSummary.append(i > 1 ? " were" : " was");
            smfSummary.append(" not as predicted.");
            result = smfSummary.toString(); 
        }
        return result;
    }

    public void actionPerformed(ActionEvent e){
	    //getEventLogger().log("WhyNot edit filter", "actionCommand",
	    //	    e.getActionCommand());
		
		getEventLogger().
					log(true, 
						AuthorActionLog.WHY_NOT_WINDOW,
						e.getActionCommand(),
						"", "", "");
		
		//some kind of filter action took place
		if(e.getActionCommand().equals(WhyNot.FILTER_VARIABLE)){
			//variable changed - repopulate value pull-down
			String var = ((JComboBox)newFilter.get(1)).getSelectedItem().toString();
			Vector vals = new Vector();
			for(ListIterator li = resultsList.listIterator(); li.hasNext();){
				Vector binding = (Vector)li.next();
				for(ListIterator var_it = binding.listIterator(); var_it.hasNext();){
					Vector v = (Vector)var_it.next();
					if(v.get(0).equals(var) && !vals.contains(v.get(1))){
						vals.add(v.get(1));
					}
				}
			}
			((JComboBox)newFilter.get(2)).setModel(new DefaultComboBoxModel(vals));
			repaint();
		}
		else if(e.getActionCommand().equals(WhyNot.FILTER_APPLY)){

			//new filter applied

			//maintain selection
			Object oldSelection = null;
			if(instList.getSelectedIndex() != -1)
				oldSelection = resultsListFiltered.get(instList.getSelectedIndex());

			//filter out instantiations in currently filtered list that don't match new filter
			String var = ((JComboBox)newFilter.get(1)).getSelectedItem().toString();
			String val = ((JComboBox)newFilter.get(2)).getSelectedItem().toString();
			ArrayList newFilteredList = new ArrayList();
			ArrayList newSummaryList = new ArrayList();
			for(ListIterator li = resultsListFiltered.listIterator(), sum_it = summaryListFiltered.listIterator();li.hasNext();){
				Vector binding = (Vector)li.next();
				Object summary = sum_it.next();
				boolean match = false;
				for(ListIterator var_it = binding.listIterator();var_it.hasNext();){
					Vector v = (Vector)var_it.next();
					if(v.get(0).equals(var) && v.get(1).equals(val)){
						match = true;
						break;
					}
				}
				if(match){
					newFilteredList.add(binding);
					newSummaryList.add(summary);
				}
			}
			resultsListFiltered = newFilteredList;
			summaryListFiltered = newSummaryList;
			//repopulate instantiation list
			Vector instText = new Vector();
			int i = 0, selection = -1;
			for(ListIterator li = resultsListFiltered.listIterator(), sum = summaryListFiltered.listIterator(); li.hasNext();){
				Vector result = (Vector)li.next();
				if(sum.next().toString().equals(ALL_VARIABLES_BOUND_SUCCESSFULLY))
					instText.add("<HTML><FONT COLOR=#00CC00>" + result.size() + " variables bound</FONT></HTML>");
				else
					instText.add("<HTML><FONT COLOR=#FF0000>" + result.size() + " variables bound</FONT></HTML>");
				if(result == oldSelection) selection = i;
				i++;
			}
			instList.setListData(instText);
			//restore old selection
			if(selection != -1) instList.setSelectedIndex(selection);

			//deactivate filter interface, store in filters vector
			((JButton)newFilter.get(3)).setText("Remove");
			((JButton)newFilter.get(3)).setActionCommand(WhyNot.FILTER_REMOVE);
			((JComboBox)newFilter.get(1)).setEnabled(false);
			((JComboBox)newFilter.get(2)).setEnabled(false);

			filters.add(newFilter);

			//create new filter panel
			Vector vars = new Vector();
			for(ListIterator li = ((Vector)resultsList.get(0)).listIterator(); li.hasNext();){
				vars.add(((Vector)li.next()).get(0));
			}
			for(ListIterator li = filters.listIterator(); li.hasNext();){
				Vector filter = (Vector)li.next();
				String usedVar = (String)((JComboBox)filter.get(1)).getSelectedItem();
				vars.remove(usedVar);
			}
	
			//FIXME: don't insert duplicate values (see similar code above)
			Vector vals = new Vector();
			for(ListIterator li = resultsList.listIterator(); li.hasNext();){
				Vector binding = (Vector)li.next();
				for(ListIterator var_it = binding.listIterator(); var_it.hasNext();){
					Vector v = (Vector)var_it.next();
					if(v.size() > 1 && vars.size() > 0 && v.get(0).equals(vars.get(0))){
						vals.add(v.get(1));
					}
				}
			}

			newFilter = new Vector();
		 Box newFilterPanel = new Box(BoxLayout.X_AXIS);
			newFilterPanel.add(new JLabel("Show only cases where "));
			JComboBox newFilterVar = new JComboBox(vars);
			newFilterVar.setSelectedIndex(0);
			newFilterVar.setActionCommand(WhyNot.FILTER_VARIABLE);
			newFilterVar.addActionListener(this);
			newFilterPanel.add(newFilterVar);
			newFilterPanel.add(new JLabel(" equals "));
			JComboBox newFilterVal = new JComboBox(vals);
			newFilterVal.setSelectedIndex(0);
			newFilterVal.setActionCommand(WhyNot.FILTER_VALUE);
			newFilterVal.addActionListener(this);
			newFilterPanel.add(newFilterVal);
			JButton apply = new JButton("Apply");
			apply.setActionCommand(WhyNot.FILTER_APPLY);
			apply.addActionListener(this);
			newFilterPanel.add(apply);
			newFilter.add(newFilterPanel);
			newFilter.add(newFilterVar);
			newFilter.add(newFilterVal);
			newFilter.add(apply);
			newFilterPanel.setMaximumSize(new Dimension(700, 25));
			filterBox.add(newFilterPanel);
			repaint();
		}
		else if(e.getActionCommand().equals(WhyNot.FILTER_REMOVE)){
			//remove a currently active filter
			//find the panel for the filter clicked, remove it
			Box filterPanel = null;
			Component[] children = filterBox.getComponents();
			for(int i=0; i<children.length; i++){
				if(((JComponent)children[i]).getComponents()[4] == e.getSource()) filterPanel = (Box)children[i];
			}
			if(filterPanel == null) return;
			filterBox.remove(filterPanel);
			filterScroll.revalidate();
			//find the filter vector, remove it
			Vector filterToRemove = null;
			for(ListIterator li = filters.listIterator(); li.hasNext();){
				Vector filter = (Vector)li.next();
				if(filter.get(0) == filterPanel){
					filterToRemove = filter;
				}
			}
			if(filterToRemove != null) filters.remove(filterToRemove);
			//re-filter instantiation list based on remaining filters
			ArrayList newFilteredList = new ArrayList();
			ArrayList newSummaryList = new ArrayList();
			for(ListIterator li = resultsList.listIterator(), sum_it = summaries.listIterator();li.hasNext();){
				Vector binding = (Vector)li.next();
				Object summary = sum_it.next();
				boolean matchAll = true;
				for(ListIterator filter_it = filters.listIterator(); filter_it.hasNext() && matchAll;){
					Vector filter = (Vector)filter_it.next();
					String var = ((JComboBox)filter.get(1)).getSelectedItem().toString();
					String val = ((JComboBox)filter.get(2)).getSelectedItem().toString();
					boolean match = false;
					for(ListIterator var_it = binding.listIterator();var_it.hasNext();){
						Vector v = (Vector)var_it.next();
						if(v.get(0).equals(var) && v.get(1).equals(val)){
							match = true;
							break;
						}
					}
					if(!match) matchAll = false;
				}
				if(matchAll){
					newFilteredList.add(binding);
					newSummaryList.add(summary);
				}
			}
			resultsListFiltered = newFilteredList;
			summaryListFiltered = newSummaryList;
			Vector instText = new Vector();
			for(ListIterator li = resultsListFiltered.listIterator(), sum = summaryListFiltered.listIterator(); li.hasNext();){
				if(sum.next().toString().equals(ALL_VARIABLES_BOUND_SUCCESSFULLY))
					instText.add("<HTML><FONT COLOR=#00CC00>" + ((Vector)li.next()).size() + " variables bound</FONT></HTML>");
				else
					instText.add("<HTML><FONT COLOR=#FF0000>" + ((Vector)li.next()).size() + " variables bound</FONT></HTML>");
			}
			instList.setListData(instText);

			Vector vars = new Vector();
			for(ListIterator li = ((Vector)resultsList.get(0)).listIterator(); li.hasNext();){
				vars.add(((Vector)li.next()).get(0));
			}
			for(ListIterator li = filters.listIterator(); li.hasNext();){
				Vector filter = (Vector)li.next();
				String usedVar = (String)((JComboBox)filter.get(1)).getSelectedItem();
				vars.remove(usedVar);
			}
	
			Vector vals = new Vector();
			for(ListIterator li = resultsList.listIterator(); li.hasNext();){
				Vector binding = (Vector)li.next();
				for(ListIterator var_it = binding.listIterator(); var_it.hasNext();){
					Vector v = (Vector)var_it.next();
					if(v.get(0).equals(vars.get(0))){
						vals.add(v.get(1));
					}
				}
			}

			((JComboBox)newFilter.get(1)).setModel(new DefaultComboBoxModel(vars));
			((JComboBox)newFilter.get(2)).setModel(new DefaultComboBoxModel(vals));

			repaint();
		}
	}

    /** Cache the last highlighted variable that was under the mouse. */
    private String mouseOverHighlightedVarName = null;

	/** Cache for last highlight under mouse. */
	private Highlighter.Highlight mouseOverHighlight = null;
	public static final String FILTER_REMOVE = "FILTER_REMOVE";
	public static final String FILTER_APPLY = "FILTER_APPLY";
	public static final String FILTER_VALUE = "FILTER_VALUE";
	public static final String FILTER_VARIABLE = "FILTER_VARIABLE";
	public static final String INSTANTIATION_INDEX = "INSTANTIATION_INDEX";
	public static final String CLOSE = "CLOSE";
	// WHY_NOT_WINDOW
	public static final String INSPECT_PARTIAL_ACTIVITION = "INSPECT_PARTIAL_ACTIVITION";

	/**
	 * Given a position in the rule's {@link JTextArea}, return the name
	 * of the highlighted variable. Maintains {@link #mouseOverHighlight}.
	 * @param offset position in <code>TextArea</code> displaying rule
	 * @return name of variable at that position; null if none found
	 */
	private String getHighlightedVar(int offset) {
		String varName = null;
		if (mouseOverHighlight == null ||
				offset < mouseOverHighlight.getStartOffset() ||
				mouseOverHighlight.getEndOffset() < offset) {
			//cycle through highlights until we find the one we're over
			mouseOverHighlight = null;
			Highlighter.Highlight highlights[] = ruleDisplay.getHighlighter().getHighlights();
			for(int i=0; mouseOverHighlight == null && i < highlights.length; i++){
				Highlighter.Highlight h = highlights[i];
				if(h.getStartOffset() <= offset && offset <= h.getEndOffset())
					mouseOverHighlight = h;
			}
		}
		if (mouseOverHighlight != null)
			varName = ruleDisplay.getText().substring(mouseOverHighlight.getStartOffset(),
					mouseOverHighlight.getEndOffset());
		return varName;
	}


	/**
	 * @return the {@link #eventLogger}
	 */
	EventLogger getEventLogger() {
		return eventLogger;
	}
}

