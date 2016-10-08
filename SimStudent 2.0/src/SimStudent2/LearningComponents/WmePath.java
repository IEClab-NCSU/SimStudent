/**
 * Created: Dec 23, 2013 4:55:27 PM
 * @author mazda
 * 
 */
package SimStudent2.LearningComponents;

import java.util.ArrayList;

import jess.Context;
import jess.Deftemplate;
import jess.Fact;
import jess.JessException;
import jess.RU;
import jess.Value;
import jess.ValueVector;
import SimStudent2.TraceLog;
import SimStudent2.ProductionSystem.SsRete;

/**
 * A hierarchical structure representing a WME retrieval path in a given task.
 * Used to compose WME retrieval pattern in the production LHS
 *
 * ?var0 <- (problem  (interface-elements ? ? ? ? ? ? ? ?var8 ? ? ?)   )
 * ?var8 <- (table  (columns ?var9))
 * ?var9 <- (column  (cells $?m ?var11 $?)  )
 * ?var11 <- (cell (name ?foa0) (value ?val0&~nil)   )
 *
 * @author mazda
 */
public class WmePath {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constants
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	// Determines the value of the terminal Wme Path node
	// FoA should have a non-NIL value hence
	// ?var0 <- (MAIN::cell (name dorminTable1_C1R1) (value ?val1&~nil))
	// Selection should have a NIL value hence
	// ?var0 <- (MAIN::cell (name dorminTable1_C1R1) (value ?val1&nil))
	public static final int SELECTION = 1;
	public static final int FOA = 2;

	public static final String MULTIFIELD_VAR_PREFIX = "$?";
	public static final String FIELD_VAR_PREFIX = "?";
	public static final char FIELD_VAR_PREFIX_CHAR = '?';
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	// WME Name given by the user as a label of the GUI widget. E.g., "dorminTable1_C1R1"
	// 
	private String name = "";
	
	// Instead of the name of the GUI element (e.g., "dorminTable1_C1R1") it returns the 
	// variable name in the "name" slot (e.g., "?foa21")
	// Set by makeNewWmePathTerminalNode()
	//
	private String nameVariable = "";

	// Keep the the value variable, e.g., "?val169" in the following: 
	// ?var168 <- (MAIN::cell (name ?foa167) (value ?val169&~nil))
	// Used for variable bindings for RHS search
	// 
	private String valueVariable = "";
	
	// Wme Path Nodes
	/*
	 * WME path looks like this:
	 *  
	 * ?var0 <- (problem  (interface-elements ? ? ? ? ? ? ? ?var8 ? ? ?))
	 * ?var8 <- (table  (columns ?var9))
	 * ?var9 <- (column  (cells $?m ?var11 $?))
	 * ?var11 <- (cell (name ?foa0) (value ?val0&~nil))
	 * 
	 * Each node is stored in wmePathNodes as String
	 * 
	 */
	private String[] wmePathNodes = null;
	
	// The type of terminal Wme Path node (FoA or Selection)
	private int terminalType = -1;
	
	// Rete
	private SsRete rete = null;

	// The WME object of the terminal node
	private Fact wme = null;
	

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	
	/**
	 * @param name
	 * @param rete
	 * @param terminalType	Either SELECTION or FOA.  See the constants section above.
	 * @param wmePathNodes
	 * @param nameVar
	 */
	public WmePath(String name, SsRete rete, int terminalType, String[] wmePathNodes, String nameVar, String valueVar) {
		
		this.name = name;
		this.terminalType = terminalType;
		this.rete = rete;
		this.nameVariable = nameVar;
		this.valueVariable = valueVar;
		
		setWmePathNodes(wmePathNodes != null ? wmePathNodes : initializeWmePath(rete));
	}

	public WmePath(String name, SsRete rete, int terminalType, String[] wmePathNodes) {
		this(name, rete, terminalType, wmePathNodes, null, null);
	}

	
	public WmePath(String name, SsRete rete, int terminalType) {
		
		this(name, rete, terminalType, null);
	}


	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods 
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * @param rete
	 */
	private String[] initializeWmePath(SsRete rete) {

		Fact problem = rete.lookupProblemWme();
		String[] wmePath = searchForWmePath(getName(), problem, rete);
		
		return wmePath;
		
		// trace.out(toString());
	}
	
	/**
	 * Find a WME retrieval path as a Vector of Facts, which is then converted into 
	 * a WME path such as
	 *  
	 * ?var0 <- (problem  (interface-elements ? ? ? ? ? ? ? ?var8 ? ? ?)   )
	 * ?var8 <- (table  (columns ?var9))
	 * ?var9 <- (column  (cells $?m ?var11 $?)  )
	 * ?var11 <- (cell (name ?foa0) (value ?val0&~nil)   )
	 * 
	 * Each recursive call generates String[] of WME Path nodes, where each node
	 * represents a line of wme path (e.g., "?var8 <- (table  (columns ?var9))")
	 * The caller will then take String[0] of the return and add a new node to the 
	 * very front of the String[] by replacing an element of a slot with the 
	 * variable at the front of String[0]. 
	 * 
	 * @param targetWmeName
	 * @param fact
	 * @param rete
	 * @return
	 */
	private String [] searchForWmePath(String targetWmeName, Fact fact, SsRete rete) {
		
		String[] wmePath = null;
		String factName = rete.getWmeLabelName(fact);

		// System.out.println("targetWmeName: " + targetWmeName + ", fact name: " + factName);
		if (targetWmeName.equals(factName)) {
		
			// The <fact> is the target (i.e., terminal) fact.
			// Make a string like "?var11 <- (cell (name ?foa0) (value ?val0&~nil))"
			wmePath = makeNewWmePathTerminalNode(fact, rete);
			// System.out.println("new WME path node made: " + wmePath[0]);

		} else {
			
			try {
				
				Context c = rete.getGlobalContext();

				Deftemplate deftemplate = fact.getDeftemplate();
				// System.out.println("deftemplate=" + deftemplate);
				
				String [] slotNames = deftemplate.getSlotNames();
				for ( String slotName : slotNames ) {
					
					// System.out.println("Expanding slot " + slotName + "...");
					Value slotValue = fact.getSlotValue(slotName);
					
					// If the slot value is not a Fact nor a LIST (of Facts), then skip...
					if (slotValue.type() != RU.FACT && slotValue.type() != RU.LIST)	continue;
					
					// Make a "vector" of slot elements
					ValueVector slotValueVector; 
					
					if (slotValue.type() == RU.LIST) {
						
						slotValueVector = slotValue.listValue(c);
					
					} else {
						
						slotValueVector = new ValueVector();
						slotValueVector.add(slotValue);
					}
					
					int n = slotValueVector.size();
					
					for (int i = 0; i < n; i++) { 
						
						Fact childFact = slotValueVector.get(i).factValue(c);
						
						// System.out.println("Nesting to " + childFact + "...");
						String[] childWmePath = searchForWmePath(targetWmeName, childFact, rete);
						
						if (childWmePath != null) {
							
							String theVar = getWmePathNodeVar(childWmePath[0]);
							String wmePathNode = makeNewWmePathNode(fact, slotName, childFact, theVar, rete);
							
							wmePath = new String[childWmePath.length + 1];
							wmePath[0] = wmePathNode;
							int k = 1;
							for( String childNode : childWmePath ) {
								wmePath[k++] = childNode;
							}
							
							break;
						}
					}
				}

			} catch (JessException e) {	e.printStackTrace(); }
		}
		
		return wmePath;
	}
	
	/**
	 * Returns the variable name on the left-hand side of a WME path node:
	 * 
	 * ?var11 <- (cell (name ?foa0) (value ?val0&~nil))
	 * 
	 * which, in this case, is "?var11"
	 * 
	 * @param string
	 * @return
	 */
	private String getWmePathNodeVar(String string) {
		
		int idx = string.indexOf(' ');
		return string.substring(0, idx);
	}

	/**
	 * Given a fact whose slot value element (childFact) has been expanded into a WME Path, 
	 * return a WME path node representation with the slot value replaced with the given variable (theVar)
	 * 
	 * ?var0 <- (problem  (interface-elements ? ? ? ? ? ? ? ?var8 ? ? ?))
	 * 
	 * @param fact
	 * @param slotName
	 * @param childFact
	 * @param theVar
	 * @return
	 */
	// ToDo use the chunk name (e.g., problem) as the stem of the variable; e.g., ?problem82
	private String makeNewWmePathNode(Fact fact, String slotName, Fact childFact, String theVar, SsRete rete) {

		String slotStr = "";
		try {
			
			Context c = rete.getGlobalContext();

			Value slotValue = fact.getSlotValue(slotName);
			ValueVector vVector = slotValue.listValue(c);
			
			slotStr += "(" + slotName;
			for (int i = 0; i < vVector.size(); i++) {

				Value v = vVector.get(i);
				Fact vFact = v.factValue(c);
				
				slotStr += " " + (vFact.equals(childFact) ? theVar : FIELD_VAR_PREFIX);
				
			}
			slotStr += ")";
			
		} catch (JessException e) {	e.printStackTrace(); }
		
		String factTypeStr = fact.getName();

		String wmePathNode = Bindings.genVarSym() + " <- (" + factTypeStr + " " + slotStr + ")";
		return wmePathNode;
	}

	/**
	 * Make a terminal WME Path node that, by definition, always comes to the end of a 
	 * WME Path hence has "name" and "value" as its slots.
	 * 
	 * @param fact
	 * @param rete
	 * @return
	 */
	private String[] makeNewWmePathTerminalNode(Fact fact, SsRete rete) {

		String returnStr[] = new String[1];
		
		String factTypeStr = fact.getName();
		
		String nameStr = 
				isTerminalTypeFoA() ? Bindings.genVarSym(Bindings.FOA_VAR_STEM) : Bindings.genVarSym(Bindings.SELECTION_VAR_STEM);
		setNameVariable(nameStr);
		
		String valueVar = Bindings.genVarSym(Bindings.VAL_VAR_STEM);
		setValueVariable(valueVar);
				
		// Start from "?var01 <- "
		String wmePathNode = Bindings.genVarSym() + " <- ";
		wmePathNode += "(" + factTypeStr + " (name " + nameStr + ") ";
		wmePathNode += "(value " + valueVar + (isTerminalTypeFoA() ? "&~nil" : "&nil") + "))";
		
		returnStr[0] = wmePathNode;
		
		return returnStr;
	}
	
	/**
	 * @return True if this WmePath is representing an WME used to specify focus of attention
	 */
	private boolean isTerminalTypeFoA() { 
		
		return getTerminalType() == FOA;
	}
	
	public String getValue() {
		
		return getSlotElementByName("value");
	}
	
	private String getSlotElementByName(String slotName) {
		
		String value = null;
		
		try {
			Value wmeValue = getWme().getSlotValue(slotName);
			value = wmeValue.stringValue(getRete().getGlobalContext());
			
		} catch (JessException e) {	e.printStackTrace(); }
		
		return value;
	}

	
	
	
	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// Applicability
	//
	/**
	 * See if the current wmePath is a generalization (or equivalent) pattern of targetWmePath
	 * 
	 *       WmePath might be: ?var5 <- (MAIN::problem (interface-elements $? ?var4 $?))
	 * targetWmePath might be: ?var5 <- (MAIN::problem (interface-elements ? ? ? ? ? ? ? ?var4 ? ? ?))
	 *  
	 * @param gargetWmePath
	 * @return
	 */
	boolean isApplicable(WmePath targetWmePath) {
		
		// TraceLog.out("isApplicable: wmePath = " + targetWmePath);
		
		boolean isApplicable = true;
		
		String[] wmePathNodes = getWmePathNodes();
		String[] targetWmePathNodes = targetWmePath.getWmePathNodes();
		
		/*
		ArrayList<String> w1 = stringArrayToArrayList(wmePathNodes);
		ArrayList<String> w2 = stringArrayToArrayList(targetWmePathNodes);
		TraceLog.out("      wmePathNodes: " + w1);
		TraceLog.out("targetWmePathNodes: " + w2);
		*/

		if (wmePathNodes.length != targetWmePathNodes.length) {
		
			isApplicable = false;

		} else {

			for (int i = 0; i < wmePathNodes.length; i++) {
			
				String wmePathNode = wmePathNodes[i];
				String targetWmePathNode = targetWmePathNodes[i];
				isApplicable = isApplicable(wmePathNode, targetWmePathNode);
				
				if (!isApplicable) {
					break;
				}
			}
		}
		
		// TraceLog.out("isApplicable = " + isApplicable);
		return isApplicable;
	}
	
	private ArrayList<String> stringArrayToArrayList(String[] stringArray) {
		
		ArrayList<String> arrayList = new ArrayList<String>();
		
		for (int i = 0; i < stringArray.length; i++) {
			arrayList.add(stringArray[i]);
		}
		
		return arrayList;
	}

	/**
	 * Verify if the current generalization of WmePath covers a given WmePath in an example
	 * WmePath is broken down into each individual WmePathNode to call this method
	 * 
	 *       WmePathNode might be: ?var5 <- (MAIN::problem (interface-elements $? ?var4 $?))
	 * targetWmePathNode might be: ?var5 <- (MAIN::problem (interface-elements ? ? ? ? ? ? ? ?var4 ? ? ?))
	 * 
	 * @param wmePathNode			A wmePathNode from a current generalization of a wmePath
	 * @param targetWmePathNode		A wmePathNode from a candidate example
	 * @return
	 */
	private boolean isApplicable(String wmePathNode, String targetWmePathNode) {
		
		boolean isApplicable = true;
		
		ArrayList<String> slots = readSlots(wmePathNode);
		ArrayList<String> targetSlots = readSlots(targetWmePathNode);
		
		// slot: (interface-elements $? ?var8 $?)
		// targetSlot: (interface-elements ? ? ? ? ? ? ? ?var8 ? ? ?)
		
		for (int i = 0; i < slots.size(); i++) {
			
			String slot = slots.get(i);
			ArrayList<String> slotFields = extractFields(slot);
			
			String targetSlot = targetSlots.get(i);
			ArrayList<String> targetFields = extractFields(targetSlot);
			
			//   slotField: [$? ?var8 $?]
			// targetField: [? ? ? ? ? ? ? ?var8 ? ? ?]
			/*
			TraceLog.out("  slotFields: " + slotFields);
			TraceLog.out("targetFields: " + targetFields);
			*/
			
			int k = 0;
			// Loop through the current slot fields, see if the target fields have matched field tokens
			for (String slotField : slotFields) {
				
				// if the field pattern is "$?", then move through the target field as far as possible, ...
				if (slotField.equals(MULTIFIELD_VAR_PREFIX)) {
					
					while (k < targetFields.size()) {
						
						String targetField = targetFields.get(k);
						
						if (!targetField.equals(FIELD_VAR_PREFIX)) {
							break;
						} else {
							k++;
						}
					}
					
				// if the field pattern is "?", move to the next target field
				} else {
					
					String targetField = targetFields.get(k);
					
					if ((slotField.equals(FIELD_VAR_PREFIX) && targetField.equals(FIELD_VAR_PREFIX)) ||
						(!slotField.equals(FIELD_VAR_PREFIX) && !targetField.equals(FIELD_VAR_PREFIX)) ) {

						k++; 
					
					} else {

						isApplicable = false;
						break;
					}
				}
			}
		}
		
		return isApplicable;
	}

	/**
	 * @param slot		"(interface-elements ? ? ? ? ? ? ? ?var8 ? ? ?)"
	 * @return			String array [? ? ? ? ? ? ? ?var8 ? ? ?]
	 */
	private ArrayList<String> extractFields(String slot) {
		
		ArrayList<String> fields = new ArrayList<String>();
		
		int i = 0;

		// Skipping whatever comes before the first slot token...
		while (slot.charAt(i) != ' ') i++;
		
		String field = "";

		// No need to read the last char, which must be ')'
		for ( ; i < slot.length() - 1; i++) {
			
			char c = slot.charAt(i);
			if (c == ' ') {
				if (!field.isEmpty()) {
					fields.add(field);
					field = "";
				}
			} else {
				field += c;
			}
		}
		
		if (!field.isEmpty()) {
			fields.add(field);
		}
		
		return fields;
	}

	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// Generalization 
	// 
	/**
	 * Find WME Path Nodes that can be generalized and make a new WmePath for each of those 
	 * generalizable WME Path Node by actually generalizing it. 
	 * 
	 * @return	A list of WME Path each of which is a one-step generalization by 
	 * 			generalizing a single WME Path Node in this WME Path
	 */
	public ArrayList<WmePath> generalizeSingleWmePathNode() {
		
		// TraceLog.out("generalizeSingleWmePathNode() for " + this);
		
		ArrayList<WmePath> generalizedWmePath = new ArrayList<WmePath>();
		
		// Loop through WME path nodes of the current WME path...
		for (String currentWmePathNode : getWmePathNodes()) {
			
			// Try to generalize the WME path node...
			String genWmePathNode = generalizeWmePathNode(currentWmePathNode);
			if (genWmePathNode != null) {
				
				// If generalizable, then make a "generalized" WME path by replacing the
				// current WME path node with the generalized one
				String[] genWmePathNodes = replaceWmePathNode(getWmePathNodes(), currentWmePathNode, genWmePathNode);
				WmePath tmpWmePath = new WmePath(getName(), getRete(), getTerminalType(), genWmePathNodes);
				generalizedWmePath.add(tmpWmePath);
			}
		}

		return generalizedWmePath;
	}
	
	/**
	 * Version Space ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ 
	 * 
	 * Given a WME path node, replace a most specific slot value, which looks something like this:
	 *         ?var0 <- (problem  (interface-elements ? ? ? ? ? ? ? ?var8 ? ? ?))
	 * with more specific value, which looks something like this:
	 *         ?var0 <- (problem  (interface-elements $? ?var8 $?))
	 * 
	 * By definition, WME path nodes that can be generalized should only have one slot. 
	 * In other words, if there are more than one slots, the WME path node is a "terminal" node,
	 * which looks something like this:
	 * 			?var7 <- (MAIN::cell (name ?foa6) (value ?val8&~nil))
	 * 
	 * @param currentWmePathNode
	 * @return
	 */
	private String generalizeWmePathNode(String currentWmePathNode) {
		
		// TraceLog.out("generalizeWmePathNode(" + currentWmePathNode + ") called.");
		// ?var118 <- (MAIN::table (columns ?var117))
		
		String generalizedWmePathNode = null;
		
		// Given a WME node, e.g., "?var7 <- (MAIN::cell (name ?foa6) (value ?val8&~nil))",
		// retreive all losts (e.g., "(name ?foa6)" and "(value ?val8&~nil)")
		ArrayList<String> slots = readSlots(currentWmePathNode);
		if (!slots.isEmpty()) {
			
			// A WME path node should only contain one slot (as an intermediate path to the terminal WME) or
			// only two slots, which should be for the terminal WMW, representing "name" and "value".
			if (slots.size() > 2 || (slots.size() == 2 && !hasNameAndValueSlotsOnly(slots))) {
			
				new Exception("WME path node should have only one slot or just \"name\" & \"value\" slots").printStackTrace();
			
			} else {
				
				String slot = slots.get(0);
				
				if (canBeGeneralized(slot)) {
					String genSlotValue = generalizeSlotValue(slot);
					// slot might not contain "?"s
					if (genSlotValue != null) {
						generalizedWmePathNode = currentWmePathNode.replace(slot, genSlotValue);
					}
				}
			}
		}
		
		return generalizedWmePathNode;
	}

	// For the current implementation, a WME path node is either most specific or most general (which has
	// a multi-field variable ('$?'). 
	private boolean canBeGeneralized(String slot) {
		return !slot.contains(MULTIFIELD_VAR_PREFIX);
	}

	// See if the given slots, which has been extracted from a WME path node, contains 
	// only two slots representing "name" and "value" which means that the WME path node 
	// is a terminal WME
	//
	// slots -> {"(name ?foa6)", "(value ?val8&~nil)"}
	// 
	private boolean hasNameAndValueSlotsOnly(ArrayList<String> slots) {
		
		boolean hasNameAndValueSlotsOnly = false;
		
		if (slots.size() == 2) {
			
			hasNameAndValueSlotsOnly = true;
			for (String slot : slots) {
				
				if (!slot.startsWith("(name ") && !slot.startsWith("(value ")) {
					hasNameAndValueSlotsOnly = false;
					break;
				}
			}
		}
		
		return hasNameAndValueSlotsOnly;
	}


	// Given a WME node, e.g., "?var7 <- (MAIN::cell (name ?foa6) (value ?val8&~nil))",
	// retreive all losts (e.g., "(name ?foa6)" and "(value ?val8&~nil)")
	//
	private ArrayList<String> readSlots(String wmePathNode) {
		
		// TraceLog.out("readSlots(" + wmePathNode + ")...");
		
		ArrayList<String> slots = new ArrayList<String>();

		String slot = "";
		
		int openParenthesisLevel = 0;
		for (int i = 0; i < wmePathNode.length(); i++) {
			
			char c = wmePathNode.charAt(i);
			
			if (c == '(') {

				openParenthesisLevel++;
				
			} else if (c == ')') {
				
				if (openParenthesisLevel == 2) {
					slot += c;
					slots.add(slot);
					slot = "";
				}
				
				openParenthesisLevel--;
			}
			
			if (openParenthesisLevel == 2) {
				slot += c;
			}
		}
		
		// TraceLog.out("returning " + slots);
		
		return slots;
	}

	// Converting (interface-elements ? ? ? ? ? ? ? ?var8 ? ? ?) into (interface-elements $? ?var8 $?)
	// 
	private String generalizeSlotValue(String slotValue) {
		
		String generalizedSlotValue = null;
		
		int numWildcard = countNumWildcard(slotValue);
		// If the number of "?" is more than one,
		if (numWildcard > 1) {
			
			// then replace "? ... ?varX ? ... ?" with "$? ?varX $?"
			int wildcardIdx = slotValue.indexOf(FIELD_VAR_PREFIX_CHAR);
			String varName = scanSlotVarName(slotValue);
			String generalizedVarPattern = MULTIFIELD_VAR_PREFIX + " " + varName + " " + MULTIFIELD_VAR_PREFIX;
			
			generalizedSlotValue = slotValue.substring(0, wildcardIdx) + generalizedVarPattern + ")";
		}

		return generalizedSlotValue;
	}


	// Given a slotValue like (interface-elements ? ? ? ? ? ? ? ?var8 ? ? ?),
	// return the var name "?var8"
	private String scanSlotVarName(String slotValue) {
		
		String slotVarName = null;
		
		boolean tokenizing = false;
		for (int i = 0; i < slotValue.length(); i++) {
			
			char c = slotValue.charAt(i);
			
			if (tokenizing) {
				
				if (c == ' ') {
					
					break;
				
				} else {
					
					slotVarName += c;
				}
			
			} else if ( c == FIELD_VAR_PREFIX_CHAR && Character.isAlphabetic(slotValue.charAt(i+1))) {
			
				tokenizing = true;
				slotVarName = "" + c;
			}
		}
		return slotVarName;
	}


	// Cound number of "?"s in a given slotValue, which looks like (interface-elements ? ? ? ? ? ? ? ?var8 ? ? ?)
	private int countNumWildcard(String slotValue) {
		
		int numWildcard = 0;

		for (int i = 0; i < slotValue.length(); i++) {

			if (slotValue.charAt(i) == FIELD_VAR_PREFIX_CHAR) numWildcard++;
		}
		
		return numWildcard;
	}

	// Replace currentWmePathNode in wmePathNodes with genWmePathNode
	// 
	private String[] replaceWmePathNode(String[] wmePathNodes, String currentWmePathNode, String genWmePathNode) {
		
		String[] generalizedWmePathNodes = new String[wmePathNodes.length];
		
		for (int i = 0; i < wmePathNodes.length; i++) {
			generalizedWmePathNodes[i] = (wmePathNodes[i].equals(currentWmePathNode) ? genWmePathNode : wmePathNodes[i]);
		}

		return generalizedWmePathNodes;
	}


	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// String conversion
	// 
	/**
	 * Generate literal representation of the WME path appearing in the LHS of a production. 
	 */
	public String toString() {

		String wmePathStr = "";
		
		for (int i = 0; i < getWmePathNodes().length; i++) {
			
			wmePathStr += getWmePathNodes()[i] + "\n";
		}
		
		return wmePathStr;
	}
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Getters / Setters 
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public String getName() { return name; }

	private Fact getWme() { 
		if (wme == null) this.wme = getRete().lookupFactByName(getName());
		return wme; 
	}

	public String[] getWmePathNodes() { return wmePathNodes; }
	private void setWmePathNodes(String[] wmePath) { this.wmePathNodes = wmePath; }

	private int getTerminalType() { return terminalType; }

	SsRete getRete() { return rete;	}

	public String getNameVariable() {
		return this.nameVariable;
	}
	
	void setNameVariable(String nameVariable) {
		this.nameVariable = nameVariable;
	}

	private void setValueVariable(String valueVariable) {
		this.valueVariable = valueVariable;
	}

	public String getValueVariable() {
		return this.valueVariable;
	}



}
