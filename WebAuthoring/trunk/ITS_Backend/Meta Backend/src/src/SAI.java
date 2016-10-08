package interaction;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing SAI (selection, action, input). They are represented as lists but usually will
 * contain only one String.
 * 
 * @author Patrick Nguyen
 *
 */
public class SAI {
	private List<String> selection;
	private List<String> action;
	private List<String> input;
	
	/**
	 * Creates an SAI object with empty lists for selection, action, and input
	 */
	public SAI(){
		this(new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>());
	}
	/**
	 * Creates an SAI object with the specified selection, action, and input
	 * @param selection List of selections
	 * @param action List of actions
	 * @param input List of inputs
	 */
	public SAI(List<String> selection, List<String> action, List<String> input){
		this.selection=selection;
		this.action=action;
		this.input=input;
	}
	/**
	 * Convenience constructor for SAI's with only one selection, action, and input
	 * @param selection String of name of component
	 * @param action Action to perform
	 * @param input Input to use
	 */
	public SAI(String selection, String action, String input) {
		List<String> selections = new ArrayList<String>();
		List<String> actions = new ArrayList<String>();
		List<String> inputs = new ArrayList<String>();
		selections.add(selection);
		actions.add(action);
		inputs.add(input);
		this.selection = selections;
		this.action = actions;
		this.input = inputs;
	}

	/**
	 * Gets the selection.
	 * @return List of selections
	 */
	public List<String> getSelection() {
		return selection;
	}

	/**
	 * Sets the selection.
	 * @param selection List of selections
	 */
	public void setSelection(List<String> selection) {
		this.selection = selection;
	}

	/**
	 * Gets the action.
	 * @return List of actions
	 */
	public List<String> getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 * @param action List of actions
	 */
	public void setAction(List<String> action) {
		this.action = action;
	}

	/**
	 * Gets the input.
	 * @return List of inputs
	 */
	public List<String> getInput() {
		return input;
	}

	/**
	 * Sets the input.
	 * @param input List of inputs
	 */
	public void setInput(List<String> input) {
		this.input = input;
	}
	
	/**
	 * Convinience method to get first input
	 * @return First input
	 */
	public String getFirstInput() {
		if (this.input != null) {
			return this.input.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * Convinience method to get first action
	 * @return First action
	 */
	public String getFirstAction() {
		if (this.action != null) {
			return this.action.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * Convinience method to get first selection
	 * @return First selection
	 */
	public String getFirstSelection() {
		if (this.selection != null) {
			return this.selection.get(0);
		} else {
			return null;
		}
	}
	
	public String description() {
		return "S: " + getFirstSelection() + " I: " + getFirstInput() + " A: " + getFirstAction();
	}
}
