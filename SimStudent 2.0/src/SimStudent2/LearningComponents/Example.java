/**
 * Positive and negative example showing a step demonstrated
 * 
 * Created: Dec 23, 2013 11:39:34 AM
 * @author mazda
 * (c) Noboru Matsuda 2013-2014
 * 
 */
package SimStudent2.LearningComponents;

import java.util.ArrayList;

/**
 * @author mazda
 *
 */
public class Example {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	// Name of the skill that demonstrates this step
	private String ruleName;
	// A list of focus of attention
	private ArrayList<WmePath> foa;
	// The input for the step
	private SAI sai;
	// Type of example (positive / negative)
	private boolean isPositiveExample = true;
	
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * @param ruleName
	 * @param foa
	 * @param input
	 * @param isPositiveExample
	 */
	public Example(String ruleName, ArrayList<WmePath> foa, SAI sai, boolean isPositiveExample) {
		this.ruleName = ruleName;
		this.foa = foa;
		this.sai = sai;
		this.isPositiveExample = isPositiveExample;
	}
	
	public Example(String ruleName, ArrayList<WmePath> foa, SAI sai) {
		this(ruleName, foa, sai, true);
	}

	/**
	 * @param example
	 */
	public Example(Example example) {
		this(example.getRuleName(), example.getFoA(), example.getSAI());
	}

	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * @return The "input" value represented as SAI
	 */
	public String getInput() {
		
		return getSAI().getInput();
	}
	
	/**
	 * @return
	 */
	private ArrayList<String> foaValuesCache = null;
	public ArrayList<String> getFoaValues() {
		
		ArrayList<String> foaValues = getFoaValuesCache();
		
		if (foaValues == null) {
		
			foaValues = new ArrayList<String>();
			for (WmePath wmePath : getFoA()) {
				
				String value = wmePath.getValue();
				foaValues.add(value);
			}
			
			setFoaValuesCache(foaValues);
		}
		
		return foaValues;
	}
	
	/**
	 * @return
	 */
	private ArrayList<String> foaNameVariablesCashe = null;
	public ArrayList<String> getFoaNameVariables() {
		
		if (this.foaNameVariablesCashe == null) {
			
			this.foaNameVariablesCashe = new ArrayList<String>();
			
			for (WmePath foa : getFoA()) {
				this.foaNameVariablesCashe.add(foa.getNameVariable());
			}
		}
		
		return this.foaNameVariablesCashe;
	}

	/**
	 * @return
	 */
	private ArrayList<String> foaValueVariables = null;
	public ArrayList<String> getFoaValueVariables() {
		
		if (this.foaValueVariables == null) {
			
			this.foaValueVariables = new ArrayList<String>();
			
			for (WmePath foa : getFoA()) {
				this.foaValueVariables.add(foa.getValueVariable());
			}
		}
		return this.foaValueVariables;
	}
	
	
	/**
	 * @return
	 */
	public int getNumFoA() {
		
		return getFoA().size();
	}
	
	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// Printing
	// 
	public String toString() {
		
		String str = "Example [";
		
		str += "Name: " + getRuleName() + " ";
		str += getSAI();
		
		str += "]";
		return str;
	}
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Getter & Setter
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public boolean isPositiveExample() { return this.isPositiveExample; }
	// public void setAsPositiveExample() { this.isPositiveExample = true; }
	public void setAsNegativeExample() { this.isPositiveExample = false; }
	
	public String getRuleName() { return this.ruleName; }
	// public void setRuleName(String conceptName) { this.ruleName = conceptName; }

	public ArrayList<WmePath> getFoA() { return foa; }
	// public void setFoA(Vector<WmePath> foa) { this.foa = foa; }

	public SAI getSAI() { return sai; }
	// public void setSAI(SAI sai) { this.sai = sai; }

	private ArrayList<String> getFoaValuesCache() {
		return foaValuesCache;
	}
	private void setFoaValuesCache(ArrayList<String> foaValuesCache) {
		this.foaValuesCache = foaValuesCache;
	}

}
