/**
 * Created: Dec 23, 2013 2:45:58 PM
 * @author mazda
 * 
 */
package SimStudent2.LearningComponents;

import java.util.ArrayList;
import java.util.List;

import SimStudent2.TraceLog;
import SimStudent2.ProductionSystem.SsRete;

/**
 * A data structure representing WME perception for output from WmePerceptionLearner.
 * 
 * WME perception represents focus of attention among working memory elements that
 * are essential to perform a step.
 * 
 * It also represent a working memory element that corresponds to the "selection".
 * 
 * @author mazda
 *
 */
public class WmePerception {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Field
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	// See flatString()
	private static final char FLAT_STRING_DELIM = '@';
	
	// WmePath represents an hierarchical WME retrieval path
	// It also contains the leaf Fact
	
	/**
	 * Focus of Attention: a list of WME paths each path shows a single instance of focus of attention 
	 */
	private ArrayList<WmePath> focusOfAttention = new ArrayList<WmePath>();


	/*
	 * A list of FoA Variable Names, e.g., {?foa14, ?foa31}
	 */
	private ArrayList<String> foaNameVars = null;
	
	/*
	 * A list of variables representing FoA values, e.g.,?val12
	 */
	private ArrayList<String> foaValueVariables = null;
	
	/**
	 * The "Selection" of the step demonstrated
	 */
	private WmePath selection;

	// A list of (test ...) statements representing the constraint among FoA and Selection
	private ArrayList<LhsTest> wmeConditions;
	

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor 
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public WmePerception() {}
	
	public WmePerception(ArrayList<WmePath> foa, WmePath selection) {
		
		// System.out.println("WmePerception() called...");

		setFocusOfAttention(foa);
		setSelection(selection);
	}
	
	/**
	 * @param wmePerceptionFlatString
	 *
	 * A '@' separates FoA entries
	 * A '%' separates Selection from FoA's
	 *
	 * ?var65 <- (MAIN::problem (interface-elements ? ? ? ? ? ? ? ?var64 ? ? ?))
	 * ?var64 <- (MAIN::table (columns ?var63))
	 * ?var63 <- (MAIN::column (cells ?var61 ? ? ? ? ?))
	 * ?var61 <- (MAIN::cell (name ?foa60) (value ?val62&~nil))
	 * @ 
	 * ?var71 <- (MAIN::problem (interface-elements ? ? ? ? ? ? ? ? ? ?var70 ?))
	 * ?var70 <- (MAIN::table (columns ?var69))
	 * ?var69 <- (MAIN::column (cells ?var67 ? ? ? ? ?))
	 * ?var67 <- (MAIN::cell (name ?foa66) (value ?val68&~nil))
	 * @
	 * ?var59 <- (MAIN::problem (interface-elements ? ? ? ? ? ? ? ?var58 ? ? ?))
	 * ?var58 <- (MAIN::table (columns ?var57))
	 * ?var57 <- (MAIN::column (cells $? ?var55 $?))
	 * ?var55 <- (MAIN::cell (name ?selection54) (value ?val56&nil))
	 * 
	 */
	public WmePerception(String wmePerceptionFlatString, ArrayList<String> foaValueVariables) {
		
		// TraceLog.out("WmePerception(List<String>) with " + wmePerceptionFlatString);

		ArrayList<WmePath> foas = new ArrayList<WmePath>();
		WmePath selection = null;
		
		String wmePathString = "";
		ArrayList<String> wmePathNodes = new ArrayList<String>();
		
		for (int i = 0; i < wmePerceptionFlatString.length(); i++) {
			
			char c = wmePerceptionFlatString.charAt(i);
			switch (c) {
			// Reading a line break for a WME path node
			case '\n': 
				wmePathNodes.add(wmePathString);
				wmePathString = "";
				break;
			// Reading a "@"
			case FLAT_STRING_DELIM:
				wmePathNodes.add(wmePathString);
				WmePath foa = new WmePath("", null, WmePath.FOA, arrayListToStringArray(wmePathNodes));
				foas.add(foa);
				wmePathString = "";
				wmePathNodes = new ArrayList<String>();
				// Skip the line break...
				i++;
				break;
			default: 
				wmePathString += c;
			}
		}
		// The last chunk of WmePath is for the selection
		wmePathNodes.add(wmePathString);
		selection = new WmePath("", null, WmePath.SELECTION, arrayListToStringArray(wmePathNodes));
		
		setFocusOfAttention(foas);
		setSelection(selection);
		setFoaValueVariables(foaValueVariables);
	}

	// Converts ArrayList to String Array
	private String[] arrayListToStringArray(ArrayList<String> wmePathNodes) {
		
		String[] stringArray = wmePathNodes.toArray(new String[wmePathNodes.size()]);
		
		/*
		String[] stringArray = new String[wmePathNodes.size()];
		for (int i = 0; i < wmePathNodes.size(); i++) {
			stringArray[i] = wmePathNodes.get(i);
		}
		*/
		return stringArray;
	}

	/**
	 * @param wmePaths
	 *
	 * ?var119 <- (MAIN::problem (interface-elements ? ? ? ? ? ? ? ?var118 ? ? ?))
	 * ?var118 <- (MAIN::table (columns ?var117))
	 * ?var117 <- (MAIN::column (cells ?var115 ? ? ? ? ?))
	 * ?var115 <- (MAIN::cell (name ?foa114) (value ?val116&~nil))
	 * , 
	 * ?var125 <- (MAIN::problem (interface-elements ? ? ? ? ? ? ? ? ? ?var124 ?))
	 * ?var124 <- (MAIN::table (columns ?var123))
	 * ?var123 <- (MAIN::column (cells ?var121 ? ? ? ? ?))
	 * ?var121 <- (MAIN::cell (name ?foa120) (value ?val122&~nil))
	 * , 
	 * ?var113 <- (MAIN::problem (interface-elements ? ? ? ? ? ? ? ?var112 ? ? ?))
	 * ?var112 <- (MAIN::table (columns ?var111))
	 * ?var111 <- (MAIN::column (cells ? ?var109 ? ? ? ?))
	 * ?var109 <- (MAIN::cell (name ?selection108) (value ?val110&nil))
	 */

	public WmePerception(ArrayList<WmePath> wmePaths) {
		
		int wmePathsLength = wmePaths.size();
		
		for (int i = 0; i < wmePathsLength; i++) {
			
			WmePath wmePath = wmePaths.get(i);
			String[] wmePathNodes = wmePath.getWmePathNodes();
			String name = wmePath.getName();
			SsRete rete = wmePath.getRete();
			String nameVar = wmePath.getNameVariable();
			String valueVar = wmePath.getValueVariable();
			
			// This is for the selection
			if (i == wmePathsLength-1) {
				
				WmePath wmePathClone = new WmePath(name, rete, WmePath.SELECTION, wmePathNodes, nameVar, valueVar);
				// WmePath wmePathClone = new WmePath(name, rete, WmePath.SELECTION, wmePathNodes);
				setSelection(wmePathClone);
				
			} else {
				
				WmePath wmePathClone = new WmePath(name, rete, WmePath.FOA, wmePathNodes, nameVar, valueVar);
				// WmePath wmePathClone = new WmePath(name, rete, WmePath.FOA, wmePathNodes);
				addFocusOfAttention(wmePathClone);
			}
		}
	}
	

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * @param wmePath
	 * @param genWmePath
	 * @return
	 */
	public void replaceWmePath(String name, WmePath genWmePath) {

		// TraceLog.out("replaceWmePath for " + name + " with\ngenWmePath = " + genWmePath);

		boolean replaced = false;
		
		for (int i = 0; i < foaSize(); i++) {
			
			WmePath foaInstance = getFocusOfAttention().get(i);
			// TraceLog.out("foaInstance name = " + foaInstance.getName());
			if (foaInstance.getName().equals(name)) {
				getFocusOfAttention().set(i, genWmePath);
				replaced = true;
				break;
			}
		}
		
		if (!replaced) {
			if (getSelection().getName().equals(name)) {
				setSelection(genWmePath);
				replaced = true;
			}
		}

		if (!replaced) {
			new Exception("wmePath never appeared").printStackTrace();
		}
	}
	
	/**
	 * @return		The number of FoA
	 */
	private int foaSize() {
		return getFocusOfAttention().size();
	}

	/**
	 * @param example	A step to be validated if it unifies with the current WME Perception
	 * @return 		True if the step is unifiable with the WME Perception
	 */
	public boolean isApplicable(Example example) {
		
		if (!example.isPositiveExample()) {
			new Exception("isApplicable() got a negative example").printStackTrace();
		}

		boolean isApplicable = true;
	
		ArrayList<WmePath> exampleFoa = example.getFoA();
		WmePath stepSelection = example.getSAI().getSelectionWmePath();

		ArrayList<WmePath>modelFoa = getFocusOfAttention();
		WmePath modelSelection = getSelection();
		
		for (int i = 0; i < exampleFoa.size(); i++) {
			if (!modelFoa.get(i).isApplicable(exampleFoa.get(i))) {
				isApplicable = false;
				break;
			}
		}
		
		if (isApplicable) {
			if (!modelSelection.isApplicable(stepSelection)) {
				isApplicable = false;
			}
		}
		
		return isApplicable;
	}
	
	/**
	 * @param positiveExamples
	 * @return
	 */
	public boolean isConsistentWith(ArrayList<Example> positiveExamples) {
		
		// TraceLog.out("isConsistentWith: positiveExamples = " + positiveExamples);
		
		boolean isConsistentWith = true;
		
		for (Example example : positiveExamples) {
		
			if (!isApplicable(example)) {
				isConsistentWith = false;
				// TraceLog.out("Not applicable (" + example.getSAI().getSelection() + ": " + example.getFoA());
				break;
			}
		}

		return isConsistentWith;
	}
	
	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
	// String output
	// 
	
	/**
	 * @return
 	 * @param wmePerceptionFlatString
	 *
	 * A '@' separates FoA entries
	 *
	 * ?var65 <- (MAIN::problem (interface-elements ? ? ? ? ? ? ? ?var64 ? ? ?))
	 * ?var64 <- (MAIN::table (columns ?var63))
	 * ?var63 <- (MAIN::column (cells ?var61 ? ? ? ? ?))
	 * ?var61 <- (MAIN::cell (name ?foa60) (value ?val62&~nil))
	 * @ 
	 * ?var71 <- (MAIN::problem (interface-elements ? ? ? ? ? ? ? ? ? ?var70 ?))
	 * ?var70 <- (MAIN::table (columns ?var69))
	 * ?var69 <- (MAIN::column (cells ?var67 ? ? ? ? ?))
	 * ?var67 <- (MAIN::cell (name ?foa66) (value ?val68&~nil))
	 * @
	 * ?var59 <- (MAIN::problem (interface-elements ? ? ? ? ? ? ? ?var58 ? ? ?))
	 * ?var58 <- (MAIN::table (columns ?var57))
	 * ?var57 <- (MAIN::column (cells $? ?var55 $?))
	 * ?var55 <- (MAIN::cell (name ?selection54) (value ?val56&nil))
	 * 
	 */
	public String toFlatString() {
		
		String flatString = "";

		for (WmePath foa : getFocusOfAttention()) {
			flatString += foa.toString() + FLAT_STRING_DELIM + "\n";
		}
		flatString += getSelection().toString();
		
		return flatString;
	}
	
	/**
	 * @return
	 */
	public String getFormattedFoA() {
		
		String formattedFoa = "";
		
		ArrayList<WmePath> foaList = getFocusOfAttention();
		for (WmePath foa : foaList) {
			
			formattedFoa += foa + "\n";
		}
		
		return formattedFoa;
	}

	/**
	 * @return
	 */
	public String getFormattedSelection() {
		
		return "" + getSelection();
	}

	
	/**
	 * This method must generate line of Strings to compose the WME Perception in LHS of a production
	 */
	public String toString() {

		String wmePerception = "[WmePerception ";
		wmePerception += "foa=" + getFocusOfAttention() + ", ";
		wmePerception += "selection=" + getSelection() + "]";

		return wmePerception;
	}
	
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Getters & Setters 
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public ArrayList<WmePath> getFocusOfAttention() { return this.focusOfAttention; }
	private void setFocusOfAttention(ArrayList<WmePath> focusOfAttention) {
		this.focusOfAttention = focusOfAttention;
	}
	public WmePath getSelection() { return selection; }
	private void setSelection(WmePath selection) { this.selection = selection; }

	private void addFocusOfAttention(WmePath wmePath) {
		this.focusOfAttention.add(wmePath);
	}
	
	// private ArrayList<LhsTest> getWmeConditions() {	return wmeConditions; }
	public void setWmeConditions(ArrayList<LhsTest> wmeConditions) { this.wmeConditions = wmeConditions; }

	public ArrayList<String> getFoaNameVariables() {
		
		if (foaNameVars == null) {
			
			foaNameVars = new ArrayList<String>();
			for (WmePath wmePath : getFocusOfAttention()) {
				
				TraceLog.out("wmePath = " + wmePath);
				
				String foaVarName = wmePath.getNameVariable();
				this.foaNameVars.add(foaVarName);
			}
		}

		TraceLog.out("foaNameVars = " + foaNameVars);
		
		return this.foaNameVars;
	}

	/**
	 * @return
	 */
	public ArrayList<String> getFoaValueVariables() {
		
		if (this.foaValueVariables == null) {
			
			this.foaValueVariables = new ArrayList<String>();
			for (WmePath wmePath : getFocusOfAttention()) {
				
				String foaValueVar = wmePath.getValueVariable();
				this.foaValueVariables.add(foaValueVar);
			}
		}
		
		// TraceLog.out("foaValueVariables = " + foaValueVariables);
		
		return this.foaValueVariables;
	}

	private void setFoaValueVariables(ArrayList<String> foaValueVariables) {
		this.foaValueVariables = foaValueVariables;
	}

}
