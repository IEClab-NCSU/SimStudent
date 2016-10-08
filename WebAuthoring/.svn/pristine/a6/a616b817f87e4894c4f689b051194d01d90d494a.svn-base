package tracer;

/**
 * Class representing an sai used by the model tracer.
 * @author Alex Xiao
 *
 */
public class MTSAI {

	private String selection;
	private String action;
	private String input;
	
	public MTSAI(String selection, String action, String input) {
		this.setSelection(selection);
		this.action = action;
		this.input = input;
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}
	
	public boolean equals(MTSAI other) {
		return other.getSelection().equals(this.selection) &&
				other.getAction().equals(this.action) &&
				other.getInput().equals(this.input);
	}
	
	public String toString() {
		return "Selection: " + selection + "  Action: " + action + "  Input: " + input;
	}
}
