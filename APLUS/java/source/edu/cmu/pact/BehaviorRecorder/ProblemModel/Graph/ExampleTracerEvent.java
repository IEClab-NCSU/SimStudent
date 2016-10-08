package edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EventObject;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.Utilities.trace;

public class ExampleTracerEvent extends EventObject {
	
	/**
	 * Data needed for InterfaceAction messages to be returned as a part of a transaction.
	 * This class was first added for the interface to the CL SolverTutor in Spring 2010.
	 */
	public class InterfaceAction implements Comparable {

		public synchronized void addSolverProperty(String name, String value) {
			makeSolverProperties().put(name, value);
		}

		public synchronized void addSolverProperties(Map<String, Object> newProps) {
			makeSolverProperties().putAll(newProps);
		}

		/** Used by solver: display this string to request the student's next step. */
		private String prompt;

		/** For SolverMatcher: properties set by the solver. */
		private Map<String, Object> solverProperties;

		/** SAI action for an InterfaceAction message to be sent in response to the UI. */
		private Vector<String> iaAction;

		/** SAI input value for an InterfaceAction message to be sent in response to the UI. */
		private Vector<String> iaOutput;

		/**
		 * @return the solverProperties
		 */
		public Map<String, Object> getSolverProperties() {
			return solverProperties;
		}

		/**
		 * @param key property name in {@link #solverProperties}
		 * @return the solverProperties entry for the given key
		 */
		public Object getSolverProperty(String key) {
			if (solverProperties == null)
				return null;
			else
				return solverProperties.get(key.toLowerCase());
		}
		
		private synchronized Map<String, Object> makeSolverProperties() {
			if (getSolverProperties() == null)
				solverProperties = new LinkedHashMap<String, Object>();
			return getSolverProperties();
		}

		/**
		 * Used by solver: display this string to request the student's next step.
		 * @return the {@link #prompt}
		 */
		public String getPrompt() {
			return prompt;
		}

		/**
		 * Used by solver: display this string to request the student's next step.
		 * @param prompt new value for {@link #prompt}
		 */
		public void setPrompt(String prompt) {
			this.prompt = prompt;
		}

		/**
		 * SAI action for an InterfaceAction message to be sent in response to the UI.
		 * @return the {@link #iaAction}
		 */
		public Vector<String> getIaAction() {
			return iaAction;
		}
		
		/**
		 * SAI action for an InterfaceAction message to be sent in response to the UI.
		 * @param iaAction value for element 0 of {@link #iaAction}
		 */
		public void setIaAction(String iaAction) {
			setIaAction(s2v(iaAction));
		}
		
		/**
		 * SAI action for an InterfaceAction message to be sent in response to the UI.
		 * @param iaAction new value for {@link #iaAction}
		 */
		public void setIaAction(Vector<String> iaAction) {
			this.iaAction = iaAction;
		}

		/**
		 * SAI input value for an InterfaceAction message to be sent in response to the UI.
		 * @return the {@link #iaOutput}
		 */
		public Vector<String> getIaOutput() {
			return iaOutput;
		}
		
		/**
		 * SAI input for an InterfaceAction message to be sent in response to the UI.
		 * @param iaOutput value for element 0 of {@link #iaOutput}
		 */
		public void setIaOutput(String iaOutput) {
			setIaOutput(s2v(iaOutput));
		}

		/**
		 * SAI input value for an InterfaceAction message to be sent in response to the UI.
		 * @param iaOutput new value for {@link #iaOutput}
		 */
		private void setIaOutput(List<String> iaOutput) {
			this.iaOutput = new Vector<String>(iaOutput);
		}
		
		/**
		 * Add an SAI input value for an InterfaceAction message to be sent in response to the UI.
		 * @param iaOutput new element to add
		 */
		public void addIaOutput(String iaOutput) {
			if (trace.getDebugCode("solverdebug")) trace.printStack("solverdebug", "ExampleTracerEvent.addIaOutput("+iaOutput+")");
			if (this.iaOutput == null)
				setIaOutput(iaOutput);
			else
				this.iaOutput.add(iaOutput);
		}

		/**
		 * Criteria for ordering these objects for display to the student: display prompts last.
		 * @param other other instance to compare
		 * @return -1 if this instance has no prompt and other has one; 1 if the reverse; 0 if same
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object other) {
			 if (!(other instanceof InterfaceAction)) {
				 trace.errStack("compareTo("+other+") argument wrong type",
						 new ClassCastException(other == null ? "null" : other.getClass().getName()));
				 return 0;
			 }
			 InterfaceAction that = (InterfaceAction) other;
			 boolean thisG = this.isGradeable();
			 boolean thatG = that.isGradeable();
			 if (thisG != thatG) {
				 if (thisG)
					 return -1;
				 else
					 return 1;
			 }
			 boolean thisP = this.promptsForInput();
			 boolean thatP = that.promptsForInput();
			 if (thisP == thatP)
				 return 0;
			 else if (thisP)
				 return 1;
			 else
				 return -1;
		}
		
		/** For {@link #promptsForInput()}. */
		private Boolean promptNeedsInput = null;

		/**
		 * @return state of {@link #promptNeedsInput}; if null, true if {@link #prompt} nonempty
		 */
		private boolean promptsForInput() {
			if (promptNeedsInput != null)
				return promptNeedsInput.booleanValue();
			if (prompt == null)
				return false;
			else
				return (prompt.length() >  0);
		}
		
		/**
		 * @return the {@link #promptNeedsInput}
		 */
		public Boolean getPromptNeedsInput() {
			return promptNeedsInput;
		}

		/**
		 * @param promptNeedsInput new value for {@link #promptNeedsInput}
		 */
		public void setPromptNeedsInput(Boolean promptNeedsInput) {
			this.promptNeedsInput = promptNeedsInput;
		}

		/**
		 * @return contents as string for debugging.
		 */
		public String toString() {
			StringBuffer sb = new StringBuffer("{ ");
			sb.append(getIaAction()).append(", ");
			sb.append(getIaOutput()).append(", \"");
			sb.append(getPrompt()).append("\", ");
			sb.append(getSolverProperties()).append(" }");
			return sb.toString();
		}

		/**
		 * Tell whether an interface action is gradeable.
		 * @return true if {@link #getIaAction()}
		 */
		public boolean isGradeable() {
			List<String> iaAction = getIaAction();
			if (iaAction == null || iaAction.size() < 1)
				return false;
			else
				return "promptLabel".equalsIgnoreCase(iaAction.get(0));
		}
	}
	
	/** True if the step matched a done step but the path includes undone steps. */
	private boolean doneStepFailed = false;

	/** The result of the trace attempt, one of NULL_MODEL, .... */
	private String result = ExampleTracerTracer.NULL_MODEL;

	/** Student's selection, action, input. */
	private ExampleTracerSAI studentSAI;

	/** Tutor's selection, action, input. */
	private ExampleTracerSAI tutorSAI;

	/** The link whose type, message or skills should be reported to the student. */
	private ExampleTracerLink reportableLink;

	/** True if the attempt violated ordering constraints. */
	private boolean isOutOfOrder;
	
   	/** Link matches chosen ahead of time, as with tutor-performed actions. */
	private ArrayList<ExampleTracerLink> preloadedLinkMatches;

	/** Tutor input after formula evaluation. */
	private Vector evaluatedInput;

	/** The variable settings from the reportable interpretation. */
	private VariableTable reportableVariableTable;
	
	/** The current number of viable {@link ExampleTracerInterpretations}s. Value -1 if unset. */
	private int numberOfInterpretations = -1;

	/** Buggy or success messages or hint texts to display to the student. */
	private Vector<String> tutorAdvice;

	/** Skills exercised on this step. */
	private Set<String> skillNames;

	/** Whether the solver has reached the Done state. */
	private boolean solverDone = false;

	/** Whether this result was from the solver. */
	private boolean fromSolver;

	/** A flag to prevent further changes to {@link #tutorSAI}, as during a later search. */
	private boolean tutorSAILocked = false;
	
	/** Data for InterfaceAction messages to generate, if any. */
	private List<InterfaceAction> iaMessages;

	/** Hint texts found on the reportable link, evaluated by {@link #reportableVariableTable}. */
	private List<String> reportableHints;

	/** Whether the current trace action needs hint texts. */
	private boolean wantReportableHints = false;
	
	/**
	 * Convenience for creating vector from string
	 * @param s
	 * @return Vector with s as first element; null if s is null 
	 */
	public static Vector<String> s2v(String s) {
		if (s == null)
			return null;
		Vector<String> result = new Vector<String>();
		result.add(s);
		return result;
	}
	/**
	 * Default constructor wants only source.
	 * @param source
	 */
	public ExampleTracerEvent(Object source) {
		this(source, null);
	}

	/**
	 * Accepts a prebuilt student SAI.
	 * @param source
	 * @param studentSAI
	 */
	public ExampleTracerEvent(Object source, ExampleTracerSAI studentSAI) {
		super(source);
		if (studentSAI != null)
			setStudentSAI(studentSAI);
	}

	/**
	 * @return true if {@link #studentSAI} is not null and
	 *              its actor is {@link Matcher#DEFAULT_TOOL_ACTOR}
	 */
	public boolean isTutorPerformed() {
		if (studentSAI == null)
			return false;
		else
			return Matcher.isTutorActor(studentSAI.getActor(), false);
	}

	/**
	 * @return the doneStepFailed
	 */
	public boolean isDoneStepFailed() {
		return doneStepFailed;
	}

	/**
	 * @param doneStepFailed new value for {@link #doneStepFailed}
	 */
	void setDoneStepFailed(boolean doneStepFailed) {
		if (trace.getDebugCode("et")) trace.out("et", "setting doneStepFailed "+doneStepFailed);
		this.doneStepFailed = doneStepFailed;
	}

	/**
	 * Set the student trace elements.
	 * @param sai
	 */
	void setStudentSAI(ExampleTracerSAI sai) {
		studentSAI = (ExampleTracerSAI) sai.clone();
	}
	
	/**
	 * Set the student trace elements.
	 * @param selection
	 * @param action
	 * @param input
	 * @param actor
	 */
	void setStudentSAI(Vector selection, Vector action, Vector input, String actor) {
		studentSAI = new ExampleTracerSAI(selection, action, input, actor);
	}

	/**
	 * Set the tutor's model tracing elements.
	 * @param sai
	 */
	void setTutorSAI(ExampleTracerSAI sai) {
		tutorSAI = (ExampleTracerSAI) sai.clone();
		if (trace.getDebugCode("solverdebug")) trace.printStack("solverdebug", "setTutorSAI("+sai+")");
	}
	
	/**
	 * Set the tutor's model tracing elements.
	 * @param selection
	 * @param action
	 * @param input
	 * @param actor
	 */
	void setTutorSAI(Vector selection, Vector action, Vector input, String actor) {
		tutorSAI = new ExampleTracerSAI(selection, action, input, actor);
		if (trace.getDebugCode("solverdebug"))
			trace.printStack("solverdebug", "setTutorSAI("+selection+","+action+","+input+","+actor+")");
	}

	/**
	 * Set {@link #result}.
	 * @param result
	 */
	public void setResult(String result) {
		if (trace.getDebugCode("solverdebug")) trace.outNT("solverdebug", "ExampleTracerEvent.setResult("+result+") replaces old "+this.result);
		this.result = result;
	}

	/**
	 * Get {@link #result}.
	 * @return result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param reportableLink new value for {@link #reportableLink}
	 */
	void setReportableLink(ExampleTracerLink reportableLink) {
		this.reportableLink = reportableLink;
	}

	/**
	 * @return value {@link #reportableLink}
	 */
	public ExampleTracerLink getReportableLink() {
		return reportableLink;
	}
	
	/**
	 * 
	 * @return {@link #studentSAI}
	 */
	public ExampleTracerSAI getStudentSAI() {
		return studentSAI;
	}
	
	/**
	 * 
	 * @return {@link #tutorSAI}
	 */
	public ExampleTracerSAI getTutorSAI() {
		return tutorSAI;
	}
	
	/**
	 * Dump for debugging.
	 * @return string with values of fields
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("[");
		sb.append(getResult());
		if (getStudentSAI() != null)
			sb.append(", StudentSAI ").append(getStudentSAI());
		if (getReportableLink() != null)
			sb.append(", reportableLink ").append(getReportableLink().getEdge().getEdge().getActionLabel());
		if (isDoneStepFailed())
			sb.append(", doneStepFailed");
		if (getTutorSAI() != null)
			sb.append(", TutorSAI ").append(getTutorSAI());
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Public access to the first selection element.
	 * @return result of {@link #getStudentSAI()}.getSelectionAsString()
	 */
	public String getSelectionAsString() {
		return getStudentSAI().getSelectionAsString();
	}

	/**
	 * @return {@link #preloadedLinkMatches}
	 */
	ArrayList<ExampleTracerLink> getPreloadedLinkMatches() {
		return preloadedLinkMatches;
	}

	/**
	 * Preselect an {@link ExampleTracerLinkMatch} to bypass matching. Used with
	 * tutor-performed actions.
	 * @param link link to add
	 */
	void addPreloadedLinkMatch(ExampleTracerLink link) {
		if (preloadedLinkMatches == null)
			preloadedLinkMatches = new ArrayList<ExampleTracerLink>();
		preloadedLinkMatches.add(link);
	}

	/**
	 * Set {@link #evaluatedInput}
	 * @param evaluatedInput new value
	 */
	void setEvaluatedInput(String evaluatedInput) {
		if (this.evaluatedInput == null)
			this.evaluatedInput = new Vector();
//		this.evaluatedInput.set(0, evaluatedInput);
		this.evaluatedInput.addElement(evaluatedInput);
	}

	/**
	 * @return the {@link #evaluatedInput}
	 */
	public Vector getEvaluatedInputAsVector() {
		return evaluatedInput;
	}

	/**
	 * @return {@link #tutorSAI}.getSelectionAsVector()
	 */
	public Vector getTutorSelection() {
		return (tutorSAI == null ? null : tutorSAI.getSelectionAsVector());
	}

	/**
	 * @return {@link #tutorSAI}.getActionAsVector()
	 */
	public Vector getTutorAction() {
		return (tutorSAI == null ? null : tutorSAI.getActionAsVector());
	}

	/**
	 * @return {@link #tutorSAI}.getInputAsVector()
	 */
	public Vector getTutorInput() {
		if (evaluatedInput != null)
			return getEvaluatedInputAsVector();
		else if (tutorSAI != null)
			return tutorSAI.getInputAsVector();
		else
			return null;
	}

	/**
	 * @return {@link #tutorSAI}.getActor()
	 */
	public String getTutorActor() {
		return (tutorSAI == null ? null : tutorSAI.getActor());
	}

	/**
	 * @return the {@link #isOutOfOrder}
	 */
	public boolean isOutOfOrder() {
		return isOutOfOrder;
	}

	/**
	 * @param isOutOfOrder new value for {@link #isOutOfOrder}
	 */
	void setOutOfOrder(boolean isOutOfOrder) {
		this.isOutOfOrder = isOutOfOrder;
	}

	/**
	 * This event's variable table should be a clone of the best interpretation's variable table.
	 * @param bestInterpVT new value for {@link #reportableVariableTable}
	 */
	void setReportableVariableTable(VariableTable bestInterpVT) {
		this.reportableVariableTable = (VariableTable) bestInterpVT.clone();
	}

	/**
	 * @return the {@link #reportableVariableTable}
	 */
	public VariableTable getReportableVariableTable() {
		return reportableVariableTable;
	}

	/**
	 * @return the {@link #numberOfInterpretations}
	 */
	public int getNumberOfInterpretations() {
		return numberOfInterpretations;
	}

	/**
	 * @param numberOfInterpretations new value for {@link #numberOfInterpretations}
	 */
	void setNumberOfInterpretations(int numberOfInterpretations) {
		this.numberOfInterpretations = numberOfInterpretations;
	}

	/**
	 * Convenience method combines {@link #setResult(String) setResult(INCORRECT_ACTION)}
	 * and {@link #setTutorAdvice(String) setTutorAdvice(buggyMessage)}.
	 * @param buggyMessage
	 */
	public void setIncorrectMsg(String buggyMessage) {
		setResult(ExampleTracerTracer.INCORRECT_ACTION);
		setTutorAdvice(buggyMessage);
	}

	/**
	 * @param tutorAdvice new values for {@link #tutorAdvice}
	 */
	public void setTutorAdvice(String[] tutorAdvice) {
		if (tutorAdvice == null || tutorAdvice.length < 1) {
			this.tutorAdvice = null;
			return;
		}
		this.tutorAdvice = new Vector<String>(Arrays.asList(tutorAdvice));
	}

	/**
	 * @param tutorAdvice new value for single element of {@link #tutorAdvice}
	 */
	public void setTutorAdvice(String tutorAdvice) {
		if (tutorAdvice == null) {
			this.tutorAdvice = null;
			return;
		}
		if (this.tutorAdvice == null)
			this.tutorAdvice = new Vector<String>();
		else
			this.tutorAdvice.clear();
		this.tutorAdvice.add(tutorAdvice);
	}

	/**
	 * @return the {@link #tutorAdvice}
	 */
	public Vector<String> getTutorAdvice() {
		return tutorAdvice;
	}

	/**
	 * Create or revise the {@link #tutorSAI} from the given parameters.
	 * @param selection
	 * @param action
	 * @param input
	 */
	public void makeTutorSAI(String selection, String action, String input) {
		if (!isTutorSAILocked()) {
			if (tutorSAI == null && studentSAI == null)
				tutorSAI = new ExampleTracerSAI(selection, action, input);
			else {
				if (tutorSAI == null && studentSAI != null)  // initialize from student, if available
					tutorSAI = (ExampleTracerSAI) studentSAI.clone();
				if (selection != null)
					tutorSAI.setSelection(selection);
				if (action != null)
					tutorSAI.setAction(action);
				if (input != null)
					tutorSAI.setInput(input);
			}
		}
		if (trace.getDebugCode("solverdebug")) trace.outNT("solverdebug",
				(isTutorSAILocked() ? "locked " : "")+
				"makeTutorSAI("+selection+","+action+","+input+") result "+tutorSAI);
	}

	/**
	 * Record a skill to pass on to the outside. If {@link #getResult()} indicates success,
	 * then increment the skill; else decrement it. Creates {@link #skillNames} if null.
	 * @param skillName unique name for skill
	 */
	public void addSkillName(String skillName) {
		if (skillNames == null)
			skillNames = new LinkedHashSet<String>();
		skillNames.add(skillName);
	}

	/**
	 * @return {@link #skillNames}; creates empty set if null
	 */
	public Set<String> getSkillNames() {
		if (skillNames == null)
			skillNames = new LinkedHashSet<String>();
		return skillNames;
	}

	/**
	 * @param b new value for {@link #solverDone}
	 */
	public void setSolverDone(boolean b) {
		solverDone  = b;
	}

	/**
	 * @return the {@link #solverDone}
	 */
	public boolean isSolverDone() {
		return solverDone;
	}

	/**
	 * @return {@link #fromSolver}
	 */
	public boolean isSolverResult() {
		return fromSolver;
	}

	/**
	 * @param fromSolver new value for {@link #fromSolver}
	 */
	public void setFromSolver(boolean fromSolver) {
		this.fromSolver = fromSolver;
	}

	/**
	 * @return true unless {@link #result} is {@link ExampleTracerTracer#NOT_A_TRANSACTION}
	 */
	public boolean isTransaction() {
		return !(ExampleTracerTracer.NOT_A_TRANSACTION.equalsIgnoreCase(result));
	}
	
	/**
	 * Add an InterfaceAction message to {@link #iaMessages}.
	 * @param action
	 * @param input
	 * @param prompt
	 * @return new InterfaceAction
	 */
	public InterfaceAction addIaMessage(String action, Object input, String prompt) {
		if (input == null || input instanceof List)
			return addIaMessage(action, (List)input, prompt);
		else
			return addIaMessage(action, s2v(input.toString()), prompt);
	}

	/**
	 * Add an InterfaceAction message to {@link #iaMessages}.
	 * @param action
	 * @param input
	 * @param prompt
	 * @return new InterfaceAction
	 */
	public InterfaceAction addIaMessage(String action, List input, String prompt) {
		return addIaMessage(action, input, prompt, true);
	}

	/**
	 * Add an InterfaceAction message to {@link #iaMessages}.
	 * @param action
	 * @param input
	 * @param prompt
	 * @param promptNeedsInput
	 * @return new InterfaceAction
	 */
	public InterfaceAction addIaMessage(String action, Object input, String prompt, boolean promptNeedsInput) {
		if (input == null || input instanceof List)
			return addIaMessage(action, (List)input, prompt, promptNeedsInput);
		else
			return addIaMessage(action, s2v(input.toString()), prompt, promptNeedsInput);
	}

	/**
	 * Add an InterfaceAction message to {@link #iaMessages}.
	 * @param action
	 * @param input
	 * @param prompt
	 * @param promptNeedsInput
	 * @return new InterfaceAction
	 */
	public InterfaceAction addIaMessage(String action, List input, String prompt, boolean promptNeedsInput) {
		if (trace.getDebugCode("solverdebug")) trace.out("solverdebug", "addIaMessage("+action+","+input+","+prompt+","+promptNeedsInput+")");
		InterfaceAction ia = new InterfaceAction();
		ia.setIaAction(action);
		if (input != null) {
			for (Object i : input) {
				if (i != null)
					ia.addIaOutput(i.toString());
			}
		}
		ia.setPrompt(prompt);
		ia.setPromptNeedsInput(new Boolean(promptNeedsInput));
		if (iaMessages == null)
			iaMessages = new ArrayList<InterfaceAction>();
		iaMessages.add(ia);
		Collections.sort(iaMessages);
		return ia;
	}
	
	/**
	 * Remove the first ExampleTracerEvent.InterfaceAction message from {@link #iaMessages}.
	 * @return result of {@link #iaMessages}.remove(0); null if iaMessages null or empty
	 */
	public InterfaceAction dequeueInterfaceAction() {
		if (iaMessages == null || iaMessages.size() < 1)
			return null;
		else
			return iaMessages.remove(0);
	}
	
	/**
	 * @return {@link #iaMessages}: caller must not modify list
	 */
	public List<InterfaceAction> getInterfaceActions() {
		return iaMessages;
	}
	
	/**
	 * @return result of {@link ExampleTracerEvent.InterfaceAction#promptsForInput()} on
	 *         1st entry in {@link #iaMessages}
	 */
	public boolean hasGradeableIAMsgs() {
		if (iaMessages == null || iaMessages.isEmpty())
			return false;
		InterfaceAction ia = iaMessages.get(0);
		return ia.isGradeable();
	}
	
	/**
	 * Public access for getting values of variables in {@link #getReportableVariableTable()}.
	 * @param vName variable name
	 * @return null if vName null or empty or no variable table; else value of vName
	 */
	public Object getVariableValue(String vName) {
        VariableTable vt = getReportableVariableTable();
        if (vt == null || vName == null || vName.length() < 1)
        	return null;
        else
        	return vt.get(vName);
	}
	
	/**
	 * Create a step identifier appropriate to the Equation Solver from these event details.
	 * The resulting string concatenates the given string with the selection and action and
	 * skills from the tutor.
	 * @param stepID
	 * @return <i>stepID_selection_action_rule0_rule1_</i>...
	 */
	public String getSolverStepID(String stepID) {
		StringBuffer result = new StringBuffer(stepID == null ? "" : stepID);
		result.append('_').append(getTutorSelection());
		result.append('_').append(getTutorAction());
		for (String skillName : getSkillNames())
			result.append('_').append(skillName);
		return result.toString();
	}

	/**
	 * @return the {@link #tutorSAILocked}
	 */
	public boolean isTutorSAILocked() {
		return tutorSAILocked;
	}

	/**
	 * @param tutorSAILocked new value for {@link #tutorSAILocked}
	 */
	public void setTutorSAILocked(boolean tutorSAILocked) {
		this.tutorSAILocked = tutorSAILocked;
	}

	/**
	 * @return {@link #reportableHints}
	 */
	public List<String> getReportableHints() {
		return reportableHints;
	}

	/**
	 * @param new value for {@link #wantReportableHints}
	 */
	void setWantReportableHints(boolean wantReportableHints) {
		this.wantReportableHints = wantReportableHints; 
	}

	/**
	 * @return {@link #reportableHints}
	 */
	boolean getWantReportableHints() {
		return wantReportableHints;
	}

	/**
	 * @hints new value for {@link #reportableHints}
	 */
	void setReportableHints(Vector<String> hints) {
		reportableHints = hints;
	}
	
	/**
	 * @param actor new value for {@link #studentSAI}.{@link ExampleTracerSAI#setActor(String)}
	 */
	public void setActor(String actor) {
		studentSAI.setActor(actor);
	}
}
