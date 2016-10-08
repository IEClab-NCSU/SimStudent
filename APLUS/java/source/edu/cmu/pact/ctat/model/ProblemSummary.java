package edu.cmu.pact.ctat.model;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.model.ProblemSummary.StepResult;

/**
 * This object carries the information needed to update a problem's status (in CL, TS, and CTAT)
 */
public class ProblemSummary {

	/** Names of XML attributes in {@link #PROBLEM_SUMMARY} element. */
	private enum AttrName {
		CompletionStatus,
		Correct,
		UniqueCorrect,
		UniqueCorrectUnassisted,
		Hints,
		UniqueHints,
		HintsOnly,
		Errors,
		UniqueErrors,
		ErrorsOnly,
		UniqueSteps,
		TimeElapsed
	}
	
	/** Values for {@link #completionStatus}. */
	public enum CompletionValue {
		incomplete,
		complete
	}

	/**
	 * Local value for the possible evaluations of student action with respect to a step.
	 */
	enum StepResult {
		UNTRIED,    // no result for this step
		INCORRECT,  // a wrong answer for this step
		HINT,       // a hint request for this step
		CORRECT     // a right answer for this step
	}
	
	/**
	 * A summary of the student's performance on an individual step.
	 */
	public static class Step {
		
		/** XML element name. */
		public static final String ELEMENT_NAME = "step";
		
		/** Unique identifier for this step within this problem. */
		private final String id;
		
		/** Result of student's first action on this step. */
		private final StepResult result;
		
		/** 
		 * Number of correct responses on this step. In most cases this will be no more than 1.
		 */
		private int nCorrect = 0;
		
		/** Number of top-level hint requests for this step. */
		private int nFirstHints = 0;
		
		/** Number of last or bottom-out hint requests for this step. */
		private int nLastHints = 0;
		
		/** Number of errors charged to this step. */
		private int nErrors = 0;

		/** The last recorded result for this step, used for scoring with feedback suppressed. */
		private StepResult lastResult = null;

		/**
		 * Explicitly initialize required fields.
		 * @param id unique identifier for this step
		 * @param result whether the first action on this step was a hint, error or correct
		 */
		public Step(String id, StepResult result) {
			this.id = id;
			this.result = result;
			switch (result) {
			case CORRECT: incrementNCorrect(); break;
			case INCORRECT: incrementErrors(); break;
			case HINT: incrementFirstHints(); break;
			}
		}
		
		/**
		 * Parse from an XML <{@value #ELEMENT_NAME}> element: see {@link #toXMLElement()}.
		 * @param elt the element; id and result attributes required
		 * @throws IllegalArgumentException
		 */
		public Step(Element elt) throws IllegalArgumentException {
			String aName = "element_name", aVal = null;
			try {
				if (!ELEMENT_NAME.equals(aVal = elt.getName()))
					throw new IllegalArgumentException("not match "+ELEMENT_NAME);
				id = aVal = elt.getAttributeValue(aName = "id");
				if (null == id || id.length() < 1)
					throw new IllegalArgumentException("id null or empty");
				result = StepResult.valueOf(aVal = elt.getAttributeValue(aName = "result"));
				if (null != (aVal = elt.getAttributeValue(aName = "nFirstHints")))
					nFirstHints = Integer.parseInt(aVal);
				if (null != (aVal = elt.getAttributeValue(aName = "nLastHints")))
					nLastHints = Integer.parseInt(aVal);
				if (null != (aVal = elt.getAttributeValue(aName = "nErrors")))
					nErrors = Integer.parseInt(aVal);
			} catch (Exception e) {
				throw new IllegalArgumentException("error on "+aName+"="+aVal+": "+e, e);
			}
		}
		
		/**
		 * Serialize to format compatible with {@link #StepSummary(Element)}.
		 * @return element
		 */
		public Element toXMLElement() {
			Element elt = new Element(ELEMENT_NAME);
			elt.setAttribute("id", id);
			elt.setAttribute("result", result.toString());
			elt.setAttribute("nFirstHints", Integer.toString(nFirstHints));
			elt.setAttribute("nLastHints", Integer.toString(nLastHints));
			elt.setAttribute("nErrors", Integer.toString(nErrors));
			return elt;
		}
		
		/** Add 1 to the number of correct attempts. */
		public void incrementNCorrect() { nCorrect++; }
		
		/** Add 1 to the number of top-level hint requests. */
		public void incrementFirstHints() { nFirstHints++; }
		
		/** Add 1 to the number of bottom-out hint requests. */
		public void incrementLastHints() { nLastHints++; }
		
		/** Add 1 to the number of errors. */
		public void incrementErrors() { nErrors++; }

		/**
		 * @return "[id result:nFirstHints,nErrors]"
		 */
		public String toString() {
			StringBuffer sb = new StringBuffer("[");
			sb.append(id).append(" ").append(result).append(":");
			sb.append(nFirstHints).append(",").append(nErrors).append("]");
			return sb.toString();
		}

		/**
		 * @return the {@link #id}
		 */
		public String getId() {
			return id;
		}

		/**
		 * @return the {@link #result}
		 */
		public StepResult getResult() {
			return result;
		}

		/**
		 * @return the {@link #nFirstHints}
		 */
		public int getNFirstHints() {
			return nFirstHints;
		}

		/**
		 * @return the {@link #nLastHints}
		 */
		public int getNLastHints() {
			return nLastHints;
		}

		/**
		 * @return the {@link #nErrors}
		 */
		public int getNErrors() {
			return nErrors;
		}

		/**
		 * Add the counts in the given Step to this one. Could use this when we get a summary
		 * for the same problem session from a different source, as from the client when we're
		 * executing on the server. 
		 * @param sameStep
		 */
		void merge(Step sameStep) {
			nFirstHints += sameStep.getNFirstHints();
			nLastHints += sameStep.getNLastHints();
			nErrors += sameStep.getNErrors();
		}

		/**
		 * @return the {@link #nCorrect}
		 */
		public int getNCorrect() {
			return nCorrect;
		}

		/**
		 * @param result new value for {@link #lastResult}
		 */
		public void setLastResult(StepResult result) {
			lastResult  = result;
		}

		/**
		 * @return the {@link #lastResult}
		 */
		public StepResult getLastResult() {
			return lastResult;
		}
	}
	
	/** Tag name for element. */
	public static final String PROBLEM_SUMMARY = "ProblemSummary";

	/** Name for problem name attribute. */
	public static final String PROBLEM_NAME = "ProblemName";
	
	/** Formatter for toXML(): compact XML. */
	private static XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat());

	/**Name of the problem */
	private String problemName;
	
	/**Skills from the problem, or updated to the problem */
	private Skills skills;
	
	/** Total number of correct responses returned during the problem. */
	private int correct = 0;
	
	/** Total number of first hints requested during the problem. */
	private int hints = 0;
	
	/** Total number of total times student requested the bottom-out hint during the problem. */
	private int lastHints = 0;

	/** Total number of errors charged during the problem. */
	private int errors = 0;
	
	/** Whether the student has finished the problem. Values in {@link ProblemSummary.CompletionValue}. */
	private CompletionValue completionStatus = CompletionValue.incomplete;
	
	/**
	 * Number of steps on which the first student attempt was a correct answer. The student may
	 * have requested a hint but did not commit an error on this step.
	 */
	private int uniqueCorrect = 0;

	/**
	 * Number of steps on which the first student action was a correct answer. The student
	 * performed these steps without requesting a hint or committing an error.
	 */
	private int uniqueCorrectUnassisted = 0;
	
	/**
	 * Number of steps on which the student requested a hint. Since CTAT2193, this matches
	 * unique hints in the CL sense. I.e., not how many times a specific hint was asked for but
	 * instead how many different hints were asked for. If a student erred on a step, then
	 * requested a hint, we previously wouldn't increment this field but, like CL, now we do.
	 */
	private int uniqueHints = 0;

	/**
	 * Number of steps on which the student committed an error. The student may have requested
	 * a hint. 
	 */
	private int uniqueErrors = 0;
	
	/** Keeps track of results for each step performed. */
	private Map<String, Step> stepMap = new LinkedHashMap<String, Step>();

	/**
	 * Number of unique steps. This count is nonzero if this object was created from
	 * summary data only.
	 */
	private int uniqueSteps = 0;

	/** Timestamp for start of problem-solution time interval. */
	private Date startTime = new Date();
	
	/** Time spent on this problem, in milliseconds. */
	private long timeElapsed = 0;

	/**
	 * Initial number of different steps on which the student requested a hint but never erred.
	 * This is only and initial value, not incremented as steps are added. See {@link #getHintsOnly()}.
	 */
	private int initialHintsOnly = 0;

	/**
	 * Initial number of different steps on which the student erred but never requested a hint.
	 * This is only and initial value, not incremented as steps are added. See {@link #getErrorsOnly()}.
	 */
	private int initialErrorsOnly = 0;

	/**
	 * Whether {@link #getUniqueCorrect()}, {@link #getUniqueHints()} and
	 * {@link #getUniqueErrors()} count only the last result recorded for each step;
	 */
	private boolean countOnlyLastResults = false;
	
	/**
	 * For {@link #factory(String)}.
	 * @param problemName
	 * @throws IllegalArgumentException if problemName null or empty
	 */
	private ProblemSummary(String problemName)
	{
		if (problemName == null || problemName.length() < 1)
			throw new IllegalArgumentException("problemName null or empty");
		this.problemName = problemName;
	}

	/**
	 * Create an empty (all counts 0) summary data instance with given skill info.
	 * @param problemName
	 * @param skills
	 * @param countOnlyLastResults
	 */
	public ProblemSummary(String problemName, Skills skills, boolean countOnlyLastResults)
	{
		this(problemName);
		this.skills = skills;
		this.countOnlyLastResults = countOnlyLastResults;
	}
	
	/**Creates an instance of ProblemSummary from the information stored in a Document */
	//if this is modified to add more attributes later on, be sure to have a method name that is "set" + attribute name
	/**
	 * NOTE: parameter xml should NOT include beginning stuff ... will be added on (suppose could make it test)
	 * [following note now obsolete -- js]
	 * also, JDom xml does not allow starting with '/' or any other nonalphabet chars ... so I just appended one on in creating the thing
	 */
	public static ProblemSummary factory(String xmlParam)
			throws Exception {
		if(xmlParam == null)
			return null;
		try
		{
			String xml = "<?xml version=\"1.0\"?>";  // encoding? UTF-16 if already a (Unicode) String?
			if (xmlParam.trim().startsWith("<?"))
				xml = xmlParam;
			else
				xml = xml + xmlParam;
			SAXBuilder bob = new SAXBuilder();
			Reader rdr = new StringReader(xml); //bad things happen if xml is null
			
			Document doc = bob.build(rdr);
			
			Element root = doc.getRootElement();
			ProblemSummary ps = new ProblemSummary(root.getAttributeValue(ProblemSummary.PROBLEM_NAME));
			for (Object child : root.getChildren()) {
				if (!(child instanceof Element))
					continue;
				Element elt = (Element) child;
				if (Step.ELEMENT_NAME.equals(elt.getName())) {
					ps.addStep(new Step(elt));
					continue;
				} else if (Skills.SKILLS.equalsIgnoreCase(elt.getName())) {
					try {
						Skills skills = Skills.factory(elt);
						ps.setSkills(skills);
					} catch (Exception e) {
						String errMsg = "Error parsing skills from XML for ProblemSummary "+
							ps.getProblemName()+": "+e;
						trace.err(errMsg);
						throw new Exception(errMsg, e);
					}
				}
			}
			
			if (ps.getUniqueSteps() < 1)  {  // if XML had no step elements, get summary counts 

				for (AttrName attrName : AttrName.values()) {
					String attrVal = root.getAttributeValue(attrName.toString());
					if (attrVal == null)
						continue;
					long iVal = -1;
					try {
						iVal = Long.parseLong(attrVal);
					} catch (NumberFormatException nfe) {
						if (trace.getDebugCode("ps"))
							trace.outNT("ps", "Warning: non-integer value "+attrVal+" on "+attrName+
								" attribute of ProblemSummary for "+ps.getProblemName()+": "+nfe);
						continue;
					}
					switch (attrName) {
					case CompletionStatus: ps.completionStatus = CompletionValue.valueOf(attrVal); break; 
					case Correct: ps.correct = (int) iVal; break;
					case UniqueCorrect: ps.uniqueCorrect = (int) iVal; break;
					case UniqueCorrectUnassisted: ps.uniqueCorrectUnassisted = (int) iVal; break;
					case Hints: ps.hints = (int) iVal; break;
					case UniqueHints: ps.uniqueHints = (int) iVal; break;
					case HintsOnly: ps.initialHintsOnly = (int) iVal; break;
					case Errors: ps.errors = (int) iVal; break;
					case UniqueErrors: ps.uniqueErrors = (int) iVal; break;
					case ErrorsOnly: ps.initialErrorsOnly = (int) iVal; break;
					case UniqueSteps: ps.uniqueSteps  = (int) iVal; break;
					case TimeElapsed: ps.timeElapsed = iVal; break;
					}
				}
			}
			return ps;
		} catch(JDOMException je) {
			je.printStackTrace();
		} catch(IOException ioe) {
			ioe.printStackTrace(); 
		}
		return null; //hopefully never get here
	}

	/**
	 * Add a step, as one parsed from XML. Could use this when we get a summary
	 * for the same problem session from a different source, as from the client when we're
	 * executing on the server. 
	 * @param step
	 */
	private void addStep(Step step) {
		Step matchingStep = stepMap.get(step.getId());
		if (matchingStep != null)
			matchingStep.merge(step);
		else {                                  // new step
			stepMap.put(step.getId(), step);
			switch(step.getResult()) {
			case HINT: uniqueHints++; break;
			case INCORRECT: uniqueErrors++; break;
			}
		}
		hints += step.getNFirstHints();
		lastHints += step.getNLastHints();
		errors += step.getNErrors();
	}

	/**
	 * Replace the skill information for this summary.
	 * @param skills new value for {@link #skills}
	 */
	public void setSkills(Skills skills) { this.skills = skills; }
	
	/**
	 * Record that the tutor evaluated the student's attempt on the given step as incorrect.
	 * @param stepID a unique ID for the step to which the error was charged
	 */
	public void addError(String stepID)	{
		Step trial = stepMap.get(stepID);
		if (trial != null) {
			if (trial.getNCorrect() < 1 && trial.getNErrors() < 1)
				++uniqueErrors;  // increment if no attempt on this step already
			trial.incrementErrors();
		} else {
			trial = new Step(stepID, StepResult.INCORRECT);
			stepMap.put(stepID, trial);
			uniqueErrors++;
		}
		errors++;
		trial.setLastResult(StepResult.INCORRECT);
	}

	/**
	 * Record that the student requested a hint on the given step. 
	 * So far, this method does not count student requests to show 2nd or further hints on the step. 
	 * @param stepID a unique ID for the step whose hint was delivered
	 */
	public void addHint(String stepID)	{
		Step trial = stepMap.get(stepID);
		if (trial != null) {
			if (trial.getNFirstHints() < 1)
				++uniqueHints;  // increment if no hinting on this step already
			trial.incrementFirstHints();
		} else {
			trial = new Step(stepID, StepResult.HINT);
			stepMap.put(stepID, trial);
			uniqueHints++;
		}
		hints++;
		trial.setLastResult(StepResult.HINT);
	}

	/**
	 * Record that the tutor evaluated the student's attempt on the given step as correct.
	 * @param stepID a unique ID for the step
	 */
	public void addCorrect(String stepID)	{
		Step trial = stepMap.get(stepID);
		if (trial != null) {
			if (trial.getNCorrect() < 1 && trial.getNErrors() < 1) {
				++uniqueCorrect;            // increment if no attempt on this step already
				if (trial.getNFirstHints() < 1)
					uniqueCorrectUnassisted++;  // can't happen? priorTrial would not exist					
			}
			trial.incrementNCorrect();
			if (trace.getDebugCode("solverdebug"))
				trace.out("solverdebug", "addCorrect prior trial "+trial);
		} else {
			trial = new Step(stepID, StepResult.CORRECT);
			stepMap.put(stepID, trial);
			uniqueCorrect++;
			uniqueCorrectUnassisted++;
			if (trace.getDebugCode("solverdebug"))
				trace.out("solverdebug", "addCorrect new trial "+trial);
		}
		correct++;
		trial.setLastResult(StepResult.CORRECT);
	}
	
	//Get methods
	
	/**
	 * @return problem name
	 */
	public String getProblemName() { return problemName; }
	
	/**
	 * Getter for skills and values of the problem.
	 * Depending on when its called, could have different fields filled in.
	 * @return skills Skills object
	 */
	public Skills getSkills() { return skills; }
	
	/**
	 * @return hints number of hints on current problem
	 */
	public int getHints() { return hints; }

	/**
	 * If {@link #getCountOnlyLastResults()} is false, return {@link #uniqueHints},
	 * the number of unique steps on which a hint was requested. Otherwise return
	 * the count of steps in {@link #stepMap} whose {@link Step#getLastResult()} is
	 * {@value StepResult#HINT}.
	 * @return {@link #uniqueHints} or count from {@link #stepMap}
	 */
	public int getUniqueHints() {
		if (!getCountOnlyLastResults())
			return uniqueHints;
		int n = 0;
		for (Step s : stepMap.values()) {
			if (s.getLastResult() == StepResult.HINT) n++; 
		}
		return n;
	}
	
	/**
	 * @return errors number of errors for this problem
	 */
	public int getErrors() { return errors;	}

	/**
	 * If {@link #getCountOnlyLastResults()} is false, return {@link #uniqueErrors},
	 * the number of unique steps with errors in this problem. Otherwise return
	 * the count of steps in {@link #stepMap} whose {@link Step#getLastResult()} is
	 * {@value StepResult#INCORRECT}.
	 * @return {@link #uniqueErrors} or count from {@link #stepMap}
	 */
	public int getUniqueErrors() {
		if (!getCountOnlyLastResults())
			return uniqueErrors;
		int n = 0;
		for (Step s : stepMap.values()) {
			if (s.getLastResult() == StepResult.INCORRECT) n++; 
		}
		return n;
	}

	/**
	 * If {@link #getCountOnlyLastResults()} is false, return {@link #uniqueCorrect},
	 * the number of steps attempted without error for this problem. Otherwise return
	 * the count of steps in {@link #stepMap} whose {@link Step#getLastResult()} is
	 * {@value StepResult#CORRECT}.
	 * @return {@link #uniqueCorrect} or count from {@link #stepMap}
	 */
	public int getUniqueCorrect() {
		if (!getCountOnlyLastResults())
			return uniqueCorrect;
		int n = 0;
		for (Step s : stepMap.values()) {
			if (s.getLastResult() == StepResult.CORRECT) n++; 
		}
		return n;
	}

	/**
	 * @return {@link #uniqueCorrectUnassisted} number of steps attempted without error and without hints
	 */
	public int getUniqueCorrectUnassisted() { return uniqueCorrectUnassisted; }

	/**
	 * @return {@link #correct} number of correct attempts for this problem
	 */
	public int getCorrect() {
		return correct;
	}
	
	/**
	 * @return {@link #stepMap}.size()
	 */
	public int getUniqueSteps() { return uniqueSteps + stepMap.size(); }
	
	//for xml
	//for reference see TutorActionLog.java
	public String toXML()
	{
		Element root = new Element(PROBLEM_SUMMARY);
		
		// Need URLEncoder/decoder if will put this in a GET request
		root.setAttribute(ProblemSummary.PROBLEM_NAME, problemName);
		root.setAttribute(AttrName.CompletionStatus.toString(), completionStatus.toString());
		root.setAttribute(AttrName.Correct.toString(), Integer.toString(correct));
		root.setAttribute(AttrName.UniqueCorrect.toString(), Integer.toString(getUniqueCorrect()));
		root.setAttribute(AttrName.UniqueCorrectUnassisted.toString(), Integer.toString(uniqueCorrectUnassisted));
		root.setAttribute(AttrName.Hints.toString(), Integer.toString(hints));
		root.setAttribute(AttrName.UniqueHints.toString(), Integer.toString(getUniqueHints()));
		root.setAttribute(AttrName.HintsOnly.toString(), Integer.toString(this.getHintsOnly()));
		root.setAttribute(AttrName.Errors.toString(), Integer.toString(errors));
		root.setAttribute(AttrName.UniqueErrors.toString(), Integer.toString(getUniqueErrors()));
		root.setAttribute(AttrName.ErrorsOnly.toString(), Integer.toString(this.getErrorsOnly()));
		root.setAttribute(AttrName.UniqueSteps.toString(), Integer.toString(getUniqueSteps()));
		root.setAttribute(AttrName.TimeElapsed.toString(), Long.toString(timeElapsed));

		if (skills != null)
			root.addContent(skills.toXMLElement());
		
		String xml = outputter.outputString(root);
		return xml;
	}

	/**
	 * Set the {@link #startTime} to the current wall-clock time.
	 */
	public void startTimer() {
		startTime = new Date();
	}

	/**
	 * Increment {@link #timeElapsed} by the number of milliseconds elapsed since
	 * {@link #startTime}. We increment instead of simply setting in case user wants
	 * to call {@link #startTimer()}-{@link #stopTimer()} more than once.
	 * @return current date (stop time)
	 */
	public Date stopTimer() {
		Date stopTime = new Date();
		timeElapsed += stopTime.getTime() - startTime.getTime();
		return stopTime;
	}

	/**
	 * Stop the timer and restart it, to record the {@link #timeElapsed} so far.
	 * @return revised {@link #timeElapsed}
	 */
	public long restartTimer() {
		startTime = stopTimer();
		return timeElapsed;
	}

	/**
	 * @return the {@link #timeElapsed}
	 */
	public long getTimeElapsed() {
		return timeElapsed;
	}

	/**
	 * @return Number of different steps on which the student requested a hint but never erred. 
	 */
	public int getHintsOnly() {
		int s = initialHintsOnly;
		for (ProblemSummary.Step step : stepMap.values()) {
			if (StepResult.HINT != step.getResult())
				continue;
			if (step.getNErrors() < 1)
				++s;
		}
		return s;
	}

	/**
	 * @return Number of different steps on which the student erred but never requested a hint. 
	 */
	public int getErrorsOnly() {
		int s = initialErrorsOnly;
		for (ProblemSummary.Step step : stepMap.values()) {
			if (StepResult.INCORRECT != step.getResult())
				continue;
			if (step.getNFirstHints() < 1)
				++s;
		}
		return s;
	}

	/**
	 * @return the {@link #completionStatus}
	 */
	public CompletionValue getCompletionStatus() {
		return completionStatus;
	}

	/**
	 * @param completionStatus new value for {@link #completionStatus}
	 * @param canRevert true if allowed to change {@link CompletionValue#complete} to
	 *        {@link CompletionValue#incomplete}
	 */
	public void setCompletionStatus(CompletionValue completionStatus, boolean canRevert) {
		if(CompletionValue.complete == this.completionStatus) {
			if(!canRevert)
				return;        // no-op if already complete and can't go back
		}
		this.completionStatus = completionStatus;
	}

	/**
	 * @return the {@link #countOnlyLastResults}
	 */
	public boolean getCountOnlyLastResults() {
		return countOnlyLastResults;
	}

	/**
	 * @param countOnlyLastResults new value for {@link #countOnlyLastResults}
	 */
	public void setCountOnlyLastResults(boolean countOnlyLastResults) {
		this.countOnlyLastResults = countOnlyLastResults;
	}
}