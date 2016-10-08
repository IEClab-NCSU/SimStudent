/**
 * Created: Mar 6, 2014 9:34:44 PM
 * @author mazda
 * 
 */
package SimStudent2.LearningComponents;

/**
 * @author mazda
 *
 */
public class SAI {
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	WmePath selectionWmePath = null;
	String selection = "";
	String action = "";
	String input = "";

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	

	/**
	 * @param selection
	 * @param action
	 * @param input
	 */
	public SAI(WmePath selectionWmePath, String action, String input) {
		// super();
		this.selectionWmePath = selectionWmePath;
		this.selection = selectionWmePath.getName();
		this.action = action;
		this.input = input;
	}
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods 
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	@Override
	public String toString() {
		return "SAI [selection=" + getSelection() + ", action=" + getAction()
				+ ", input=" + getInput() + "]";
	}
	

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Getters and Setters
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public WmePath getSelectionWmePath() { return this.selectionWmePath; }
	
	private String getAction() { return this.action; }
	// private void setAction(String action) { this.action = action; }

	public String getInput() { return this.input; }
	// private void setInput(String input) { this.input = input; }

	public String getSelection() { return this.selection; }
	// private void setSelection(String selection) { this.selection = selection; }
	


	
	
}
