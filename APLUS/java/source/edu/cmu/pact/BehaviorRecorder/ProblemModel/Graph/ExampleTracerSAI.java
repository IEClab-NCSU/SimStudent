package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph;
import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;

public class ExampleTracerSAI {
	
	private Vector selection;
	private Vector input;
	private Vector action;
	private String actor = Matcher.DEFAULT_ACTOR;
	boolean hintRequest;
	
	/**
	 * Constructor which doesnt take any arguments
	 * Just initializes the vectors.
	 */
	ExampleTracerSAI() {
		this(new Vector(), new Vector(), new Vector(), null);
	}
	
	/**
	 * Constructor which takes the parameters as strings
	 * @param selection
	 * @param action
	 * @param input
	 */
	public ExampleTracerSAI(String selection, String action, String input) {
		this(selection, action, input, null);
	}
	
	/**
	 * Constructor which takes the parameters as strings
	 * @param selection
	 * @param action
	 * @param input
	 * @param actor
	 */
	ExampleTracerSAI(String selection, String action, String input, String actor) {
		this.selection = new Vector();
		this.selection.add(selection);
		this.action = new Vector();
		this.action.add(action);
		this.input = new Vector();
		this.input.add(input);
		setActor(actor);
	}
	
	/**
	 * Constructor which takes the parameters as vectors
	 * @param selection
	 * @param action
	 * @param input
	 */
	public ExampleTracerSAI(Vector selection, Vector action, Vector input, String actor) {
		this.selection = (selection == null ? null : (Vector) selection.clone());
		this.action = (action == null ? null : (Vector) action.clone());
		this.input = (input == null ? null : (Vector) input.clone());;
		setActor(actor);
	}
	
	/**
	 * @return - returns the selection as a String
	 */
	public String getSelectionAsString() {
		if (selection == null || selection.size() < 1)
			return null;
		Object obj = selection.get(0);
		return (obj == null ? null : obj.toString());
	}
	
	/**
	 * @return - returns the selection as a Vector
	 */
	public Vector getSelectionAsVector() {
		return selection;
	}
	
	/**
	 * Sets the selection with the given Selection as a String
	 * @param selection
	 */
	void setSelection(String selection) {
		if (this.selection == null)
			this.selection = new Vector();
		else
			this.selection.clear();
		this.selection.add(selection);
	}
	
	/**
	 * Sets the selection with the given selection as a Vector
	 * @param selection
	 */
	void setSelection(Vector selection) {
		this.selection = selection;
	}
	
	/**
	 * @return - returns the action as a String
	 */
	String getActionAsString() {
		if (action == null || action.size() < 1)
			return null;
		Object obj = action.get(0);
		return (obj == null ? null : obj.toString());
	}
	
	/**
	 * @return - returns the action as a Vector
	 */
	public Vector getActionAsVector() {
		return action;
	}
	
	/**
	 * Sets the action with the given String
	 * @param action
	 */
	void setAction(String action) {
		if (this.action == null)
			this.action = new Vector();
		else
			this.action.clear();
		this.action.add(action);
	}
	
	/**
	 * Sets the action with the given Vector
	 * @param action
	 */
	void setAction(Vector action) {
		this.action = action;
	}
	
	/**
	 * @return - returns the input as a String
	 */
	String getInputAsString() {
		if (input == null || input.size() < 1)
			return null;
		Object obj = input.get(0);
		return (obj == null ? null : obj.toString());
	}
	
	/**
	 * 
	 * @return - returns the input as a Vector
	 */
	public Vector getInputAsVector() {
		return input;
	}	
	
	/**
	 * Sets the input with the given String
	 * @param input
	 */
	void setInput(String input) {
		if (this.input == null)
			this.input = new Vector();
		else
			this.input.clear();
		this.input.add(input);
	}
	
	/**
	 * Sets the input with the geiven Vector
	 * @param input
	 */
	void setInput(Vector input) {
		this.input = input;
	}
	
	/**
	 * Dump contents for debugging.
	 * @return concatenated results of {@link #selection}.toString(),
	 *        {@link #action}, etc.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("[");
		sb.append(selection).append(",");
		sb.append(action).append(",");
		sb.append(input).append(",");
		sb.append(actor).append("]");
		return sb.toString();
	}

	/**
	 * @return the {@link #actor}
	 */
	public String getActor() {
		return actor;
	}

	/**
	 * @param actor new value for {@link #actor}
	 */
	void setActor(String actor) {
		if (Matcher.DEFAULT_TOOL_ACTOR.equalsIgnoreCase(actor))
			this.actor = Matcher.DEFAULT_TOOL_ACTOR;
		else if (Matcher.UNGRADED_TOOL_ACTOR.equalsIgnoreCase(actor))
			this.actor = Matcher.UNGRADED_TOOL_ACTOR;
		else if (Matcher.DEFAULT_STUDENT_ACTOR.equalsIgnoreCase(actor))
			this.actor = Matcher.DEFAULT_STUDENT_ACTOR;
		else
			this.actor = Matcher.DEFAULT_ACTOR;
	}

	/**
	 * Copy this instance.
	 * @return copy with clones of {@link #selection}, (@link #action}, {@link #input}
	 */
	public Object clone() {
		Vector s = (Vector) (getSelectionAsVector() == null ? null : getSelectionAsVector().clone());
		Vector a = (Vector) (getActionAsVector() == null ? null : getActionAsVector().clone());
		Vector i = (Vector) (getInputAsVector() == null ? null : getInputAsVector().clone());
		ExampleTracerSAI result = new ExampleTracerSAI(s, a, i, getActor());
		result.hintRequest = hintRequest;
		return result;
	}
}
