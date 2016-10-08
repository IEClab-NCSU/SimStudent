package edu.cmu.pact.jess;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import jess.Context;
import jess.Deftemplate;
import jess.Fact;
import jess.Funcall;
import jess.JessException;
import jess.LongValue;
import jess.QueryResult;
import jess.RU;
import jess.Rete;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.TextOutput;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.model.ProblemSummary;
import edu.cmu.pact.ctat.model.Skill;
import edu.cmu.pact.ctat.model.Skills;

/**
 * Provide rule authors access to the information in the {@link ProblemSummary}. 
 */
class ProblemSummaryAccess implements Serializable {

	/**	Timestamp yyyymmddHHMM for serial id. */
	private static final long serialVersionUID = 201309271200L;

	/** Deftemplate name for {@link ProblemSummary} fact. */
	private static final String PROBLEM_SUMMARY = "ProblemSummary";

	/** Deftemplate name for {@link Skill} facts. */
	static final String SKILL = "Skill";
	
	/** Defquery name to retrieve {@link ProblemSummary} fact. */
	public static final String GET_PROBLEM_SUMMARY = "get-problem-summary";
	
	/** Defquery name to retrieve {@link Skill} facts. */
	public static final String GET_SKILL = "get-skill";

	/** Slot name for skills in {@value PROBLEM_SUMMMARY} fact. */
	static final String SKILLS = "skills";

	/** Jess script to create templates and queries. */
	private static final String script = ""+
			"(deftemplate "+SKILL+"\n"+
			"    (slot name)\n"+
			"    (slot category)\n"+
			"    (slot description)\n"+
			"    (slot label)\n"+
			"    (slot opportunityCount)\n"+
			"    (slot pGuess)\n"+
			"    (slot pKnown)\n"+
			"    (slot pLearn)\n"+
			"    (slot pSlip))\n"+
			"(deftemplate "+PROBLEM_SUMMARY+"\n"+
			"    (slot ProblemName)\n"+
			"    (slot CompletionStatus)\n"+
			"    (slot Correct)\n"+
			"    (slot UniqueCorrect)\n"+
			"    (slot UniqueCorrectUnassisted)\n"+
			"    (slot Hints)\n"+
			"    (slot UniqueHints)\n"+
			"    (slot HintsOnly)\n"+
			"    (slot Errors)\n"+
			"    (slot UniqueErrors)\n"+
			"    (slot ErrorsOnly)\n"+
			"    (slot UniqueSteps)\n"+
			"    (slot TimeElapsed)\n"+
			"    (multislot "+SKILLS+"))\n"+
			"(defquery "+GET_PROBLEM_SUMMARY+"\n"+
			"    \"Find the single ProblemSummary fact\"\n"+
			"    ?ps <- ("+PROBLEM_SUMMARY+"))\n"+
			"(defquery "+GET_SKILL+"\n"+
			"    \"Find the Skill fact with the given name and category\"\n"+
			"    (declare (variables ?skill-name ?skill-category))\n"+
			"    ?sk <- ("+SKILL+" (name ?n&:(= ?n ?skill-name))\n"+
			"	       (category ?c&:(= ?c ?skill-category))))";
	
	/** Cached result of {@link #findSkillGetMethods(String[], TextOutput)}. */
	private static Map<String, Method> skillGetMethods = null;
	
	/** Cached result of {@link #findProblemSummaryGetMethods(String[], TextOutput)}. */
	private static Map<String, Method> problemSummaryGetMethods = null;

	/**
	 * Create the templates if necessary and update or assert the facts with the current data
	 * @param ps
	 * @param rete
	 * @param textOutput for error reporting
	 */	
	Fact updateProblemSummaryFacts(ProblemSummary ps, Rete rete, TextOutput textOutput) {
		if(textOutput == null) {
			if(rete instanceof MTRete)
				textOutput = ((MTRete) rete).getTextOutput();
			else
				textOutput = TextOutput.getTextOutput(System.out);
		}
		if(!createTemplates(rete, textOutput))
			return null;
		retractProblemSummary(rete, textOutput);
		if(ps == null)
			return null;
		ValueVector skillsVV = updateSkills((ps == null ? null : ps.getSkills()), rete, textOutput);
		return assertProblemSummary(ps, skillsVV, rete, textOutput);
	}

	/**
	 * Find the single {@value #PROBLEM_SUMMARY} fact.
	 * @param rete
	 * @param textOutput
	 * @return the number retracted; 0 if none found
	 */
	private int retractProblemSummary(Rete rete, TextOutput textOutput) {
		QueryResult qr = null;
		int result = 0;
		try {
			qr = rete.runQueryStar(GET_PROBLEM_SUMMARY, new ValueVector(0));
			while (qr.next()) {
				Value v = qr.get("ps");
				Fact f = v.factValue(rete.getGlobalContext());
				if(trace.getDebugCode("ps"))
					trace.out("ps", String.format("retractProblemSummary() to retract fact-id %d %s", f.getFactId(), f.toString()));
				rete.retract(f);
				result++;
			}
		} catch(JessException je) {
			String errorMessage = "Error running defquery "+GET_PROBLEM_SUMMARY+": "+je+"; cause "+je.getCause()+
					".\n  "+je.getProgramText()+
					"\n  "+je.getDetail();
	        trace.errStack(errorMessage, je);
	        textOutput.append("\n").append(errorMessage).append("\n");
		} catch(Exception e) {
			String errorMessage = "Error running defquery "+GET_PROBLEM_SUMMARY+": "+e+"; cause "+e.getCause();
	        trace.errStack(errorMessage, e);
	        textOutput.append("\n").append(errorMessage).append("\n");
		} finally {
			if(qr != null)
				qr.close();
		}
		if(trace.getDebugCode("ps"))
			trace.out("ps", "retractProblemSummary() returning count "+result);
		return result;
	}

	/**
	 * Assert a fact of type {@value #PROBLEM_SUMMARY} matching the given {@link ProblemSummary}.
	 * @param ps source of values
	 * @param skills list of {@value #SKILL} facts already asserted
	 * @param rete
	 * @param textOutput for error reporting
	 * @return newly-asserted fact
	 */
	private Fact assertProblemSummary(ProblemSummary ps, ValueVector skills, Rete rete, TextOutput textOutput) {
		try {
			Deftemplate problemSummaryDT = rete.findDeftemplate(PROBLEM_SUMMARY);
			Fact fact = new Fact(problemSummaryDT);
			List<String> slotNames = new ArrayList<String>(Arrays.asList(problemSummaryDT.getSlotNames()));
			List<Value> slotValues = createSlotValues(ps, skills, slotNames);
			for(int i = 0; i < slotNames.size(); ++i)
				fact.setSlotValue(slotNames.get(i), slotValues.get(i));
			if(trace.getDebugCode("ps"))
				trace.out("ps", String.format("assertProblemSummary() problemName %s, fact %s",
						(ps == null ? null : ps.getProblemName()), fact.toString()));
			return rete.assertFact(fact, rete.getGlobalContext());
		} catch(JessException je) {
			String errorMessage = "Error asserting new problem summary: "+je+"; cause "+je.getCause()+
					".\n  "+je.getProgramText()+
					"\n  "+je.getDetail();
	        trace.errStack(errorMessage, je);
	        textOutput.append("\n").append(errorMessage).append("\n");
		} catch(Exception e) {
			String errorMessage = "Error asserting new problem summary: "+e+"; cause "+e.getCause();
	        trace.errStack(errorMessage, e);
	        textOutput.append("\n").append(errorMessage).append("\n");
		}
		return null;
	}

	/**
	 * For each slot <i>X</i>, call the appropriate get<i>X</i>() accessor in the given
	 * {@link ProblemSummary} instance. 
	 * @param ps
	 * @param skills special value for {@value #SKILLS} multislot
	 * @param slotNames will remove {@value #SKILLS} element if skills arg is null
	 * @return array of Value objects, 1:1 with slotNames[]
	 */
	private List<Value> createSlotValues(ProblemSummary ps, ValueVector skills,	List<String> slotNames)
			throws Exception {
		List<Value> result = new ArrayList<Value>();
		Map<String, Method> problemSummaryGetMethods = findProblemSummaryGetMethods(slotNames, null);
		for(ListIterator<String> it = slotNames.listIterator(); it.hasNext(); ) {
			String slot = it.next();
			if(SKILLS.equals(slot)) {
				if(skills != null)
					result.add(new Value(skills, RU.LIST));
				else
					it.remove();  // remove the skills
			} else {
				Method m = problemSummaryGetMethods.get(slot);
				result.add(invokeGetMethod(ps, m, slot));
			}
		}
		if(trace.getDebugCode("ps"))
			trace.out("ps", String.format("createSlotValues() slotNames %s,\n  result %s",
					slotNames.toString(), result.toString()));
		return result;
	}

	/**
	 * For each skill, find an existing fact with the data and modify it or assert a new fact.
	 * @param skills source of data for each skill
	 * @param rete
	 * @param textOutput
	 * @return
	 */
	private ValueVector updateSkills(Skills skills, Rete rete, TextOutput textOutput) {
		ValueVector result = new ValueVector();
		if(trace.getDebugCode("ps"))
			trace.out("ps", String.format("updateSkills() given %d skills",
					(skills == null ? -1 : skills.getAllSkills().size())));
		if(skills == null)
			return result;
		List<Skill> skillsToAssert = new LinkedList<Skill>();
		for(Skill sk : skills.getAllSkills()) {
			QueryResult qr = null;
			try {
				ValueVector queryArgs = new ValueVector(2);
				queryArgs.add(sk.getName());
				queryArgs.add(sk.getCategory());
				qr = rete.runQueryStar(GET_SKILL, queryArgs);
				boolean qResult = qr.next();
				if(trace.getDebugCode("ps"))
					trace.out("ps", String.format("updateSkills() query result %b for skill %s, category %s",
							qResult, sk.getName(), sk.getCategory()));
				if(qResult)
					result.add(modifySkill(sk, qr.get("sk"), rete, textOutput));
				else
					skillsToAssert.add(sk);
			} catch(JessException je) {
				String errorMessage = "Error running defquery "+GET_SKILL+": "+je+"; cause "+je.getCause()+
						".\n  "+je.getProgramText()+
						"\n  "+je.getDetail();
		        trace.errStack(errorMessage, je);
		        textOutput.append("\n").append(errorMessage).append("\n");
			} finally {
				if(qr != null)
					qr.close();
			}
		}
		for(Skill sk : skillsToAssert) {
			try {
				result.add(assertSkill(sk, rete));
			} catch(JessException je) {
				String errorMessage = "Error asserting new skill "+sk.getName()+": "+je+"; cause "+je.getCause()+
						".\n  "+je.getProgramText()+
						"\n  "+je.getDetail();
		        trace.errStack(errorMessage, je);
		        textOutput.append("\n").append(errorMessage).append("\n");
			} catch(Exception e) {
				String errorMessage = "Error asserting new skill "+sk.getName()+": "+e+"; cause "+e.getCause();
		        trace.errStack(errorMessage, e);
		        textOutput.append("\n").append(errorMessage).append("\n");
			}
		}
		return result;
	}

	/**
	 * Assert a fact of type {@value #SKILL} matching the given {@link Skill}.
	 * @param sk source of values
	 * @param skills list of {@value #SKILL} facts already asserted
	 * @param rete
	 * @return newly-asserted fact
	 */
	private Fact assertSkill(Skill sk, Rete rete) throws Exception {
		Deftemplate skillDT = rete.findDeftemplate(SKILL);
		Fact fact = new Fact(skillDT);
		String[] slotNames = skillDT.getSlotNames();
		Value[] slotValues = createSlotValues(sk, slotNames);
		for(int i = 0; i < slotNames.length; ++i)
			fact.setSlotValue(slotNames[i], slotValues[i]);
		if(trace.getDebugCode("ps"))
			trace.out("ps", String.format("assertSkill() name %s, fact %s", sk.getName(), fact.toString()));
		return rete.assertFact(fact, rete.getGlobalContext());
	}

	/**
	 * Call {@link Rete#modify(Fact, String[], Value[], Context)} to update all the slots in the
	 * given fact, which must be an instance of deftemplate {@value #SKILL}. 
	 * @param sk
	 * @param factV
	 * @param rete
	 * @param textOutput
	 * @return
	 */
	private Fact modifySkill(Skill sk, Value factV, Rete rete, TextOutput textOutput) {
		if(trace.getDebugCode("ps"))
			trace.out("ps", String.format("modifySkill() sk %s, factV %s",
					sk.getName(), factV.toString()));
		try {
			if(factV.type() != RU.FACT)
				throw new IllegalArgumentException("Value argument "+factV+" has wrong type "+
						RU.getTypeName(factV.type()));
			Fact fact = factV.factValue(rete.getGlobalContext());
			Deftemplate dt = fact.getDeftemplate();
			if(!dt.getName().endsWith(SKILL))
				throw new IllegalArgumentException("Fact argument "+factV+" has wrong deftemplate "+
						dt.toString()+"; should be "+SKILL);
			String[] slotNames = dt.getSlotNames();
			return rete.modify(fact, slotNames, createSlotValues(sk, slotNames), rete.getGlobalContext());
		} catch(Exception e) {
			String errorMessage = String.format("%s.modifySkill() error on skill %s, category %s: %s",
					getClass().getSimpleName(), sk.getName(), sk.getCategory(), e.toString());
			trace.errStack(errorMessage, e);
	        textOutput.append("\n").append(errorMessage).append("\n");			
		}
		return null;
	}

	/**
	 * Create an array of {@link Value}s from the fields in the given {@link Skill}.
	 * @param sk source of values
	 * @param slotNames required order
	 * @return array suitable for {@link Rete#modify(Fact, String[], Value[])}
	 * @throws JessException 
	 */
	private Value[] createSlotValues(Skill sk, String[] slotNames) throws Exception {
		Value[] result = new Value[slotNames.length];
		Map<String, Method> skillGetMethods = findSkillGetMethods(slotNames, null);
		for(int i = 0; i < slotNames.length; ++i) {
			Method m = skillGetMethods.get(slotNames[i]);
			result[i] = invokeGetMethod(sk, m, slotNames[i]);
		}
/*
			if("name".equalsIgnoreCase(slot))
				result.add(new Value(sk.getName(), RU.STRING));
			else if("category".equalsIgnoreCase(slot))
				result.add(new Value(sk.getCategory(), RU.STRING));
			else if("description".equalsIgnoreCase(slot))
				result.add(new Value(sk.getDescription(), RU.STRING));
			else if("label".equalsIgnoreCase(slot))
				result.add(new Value(sk.getLabel(), RU.STRING));
			else if("opportunityCount".equalsIgnoreCase(slot))
				result.add(new Value(sk.getOpportunityCount(), RU.INTEGER));
			else if("pGuess".equalsIgnoreCase(slot))
				result.add(new Value(sk.getPGuess(), RU.FLOAT));
			else if("pKnown".equalsIgnoreCase(slot))
				result.add(new Value(sk.getPKnown(), RU.FLOAT));
			else if("pLearn".equalsIgnoreCase(slot))
				result.add(new Value(sk.getPLearn(), RU.FLOAT));
			else if("pSlip".equalsIgnoreCase(slot))
				result.add(new Value(sk.getPSlip(), RU.FLOAT));
*/
		if(trace.getDebugCode("ps"))
			trace.out("ps", String.format("createSlotValues() slotNames %s,\n  result %s",
					Arrays.toString(slotNames), Arrays.toString(result)));
		return result;
	}

	/**
	 * Call the method <i>m</i> on the instance <i>that</i> and convert the result into a
	 * {@link Value} object. The method is presumed to be the get<i>X</i>() accessor for
	 * the property named by <i>slot</i> and take no arguments. Throws exception if the
	 * method return type is not {@link String}, {@link Float}, {@link Integer}, {@link Long}
	 * or null.
	 * @param that <i>this</i> instance for call
	 * @param m getX() accessor for property
	 * @param slot property name
	 * @return {@link Value} object
	 * @throws Exception
	 */
	private Value invokeGetMethod(Object that, Method m, String slot) throws Exception {
		Value result = null;
		if(that == null)
			return result;
		if(m == null)
			throw new IllegalArgumentException("No getX() method found for slot "+slot);
		Object value = m.invoke(that, (Object[]) null);
		if(value instanceof Float)
			result = new Value(((Float) value).doubleValue(), RU.FLOAT);
		else if(value instanceof Integer)
			result = new Value(((Integer) value).intValue(), RU.INTEGER);
		else if(value instanceof Long)
			result = new LongValue(((Long) value).longValue());
		else if(value == null)
			result = new Value(Funcall.NIL);
		else
			result = new Value(value.toString(), RU.STRING);
		return result;
	}

	/**
	 * Find the appropriate getX() methods for the {@link ProblemSummary} class. Populates
	 * {@link #problemSummaryGetMethods} if not already populated.
	 * @param slotNames find the accessors for these properties
	 * @param textOutput for error reporting
	 * @return reference to {@link #problemSummaryGetMethods}
	 */
	private Map<String, Method> findProblemSummaryGetMethods(List<String> slotNames,
			TextOutput textOutput) {
		if(problemSummaryGetMethods != null)
			return problemSummaryGetMethods;
		synchronized(getClass()) {       // recheck inside mutex in case another thread was 
			if(problemSummaryGetMethods != null)  // populating the map while we were blocked on sync
				return problemSummaryGetMethods;
			problemSummaryGetMethods = new HashMap<String, Method>();
			int nMethods = findGetMethods(ProblemSummary.class, slotNames.toArray(new String[slotNames.size()]),
					problemSummaryGetMethods, textOutput);
			if(nMethods < slotNames.size() && textOutput != null)
				textOutput.append("\nWarning: will update only ").append(Integer.toString(nMethods))
						.append(" of ").append(Integer.toString(slotNames.size())).append(" ProblemSummary slots.\n");
		}
		return problemSummaryGetMethods;
	}

	/**
	 * Find the appropriate getX() methods for the {@link Skill} class. Populates
	 * {@link #skillGetMethods} if not already populated.
	 * @param slotNames find the accessors for these properties
	 * @param textOutput for error reporting
	 * @return reference to {@link #skillGetMethods}
	 */
	private Map<String, Method> findSkillGetMethods(String[] slotNames,
			TextOutput textOutput) {
		if(skillGetMethods != null)
			return skillGetMethods;
		synchronized(getClass()) {       // recheck inside mutex in case another thread was 
			if(skillGetMethods != null)  // populating the map while we were blocked on sync
				return skillGetMethods;
			skillGetMethods = new HashMap<String, Method>();
			int nMethods = findGetMethods(Skill.class, slotNames, skillGetMethods, textOutput);
			if(nMethods < slotNames.length && textOutput != null)
				textOutput.append("\nWarning: will update only ").append(Integer.toString(nMethods))
						.append(" of ").append(Integer.toString(slotNames.length)).append(" Skill slots.\n");
		}
		return skillGetMethods;
	}

	/**
	 * For each property name <i>X</i>, find the method get<i>X</i>() in the given class and
	 * insert it into the map with the property name as its key. Proceeds through errors,
	 * stuffing null into the map as the method for that property name instead.
	 * @param cls class to scan
	 * @param propertyNames find method "getXxx()" for each property named "xxx"
	 * @param getMethodsMap stuff the method references here
	 * @param textOutput if not null, write summary error here
	 * @return count of methods actually found: compare to propertyNames.length to check for errors
	 */
	private static int findGetMethods(Class cls, String[] propertyNames,
			Map<String, Method> getMethodsMap, TextOutput textOutput) {
		StringBuilder errorMessage = null;
		String lastError = null;
		int count = 0;
		for(String property : propertyNames) {
			try {
				StringBuilder fn = new StringBuilder("get");
				fn.append(Character.toUpperCase(property.charAt(0))).append(property.substring(1));

				if(trace.getDebugCode("ps"))
					trace.out("ps", "findGetMethods("+cls.getName()+") to look up method for name \""+fn+"\"");
				
				Method m = cls.getDeclaredMethod(fn.toString(), (Class[]) null);
				getMethodsMap.put(property, m);
				count++;
			} catch (Exception e) {
				if(errorMessage == null)
					errorMessage = new StringBuilder("Error finding "+cls.getName()+" get method for property(ies)");
				errorMessage.append(' ').append(property);
				lastError = e.toString()+(e.getCause() == null ? "" : "; cause "+e.getCause().toString());
				trace.err(errorMessage.append(": last error ").append(lastError).toString());
				getMethodsMap.put(property, (Method) null);
			}
		}
		if(errorMessage != null && textOutput != null)
			textOutput.append("\n").append(errorMessage.append(": last error ").append(lastError).toString()).append("\n");
		return count;
	}

	/**
	 * Check whether ProblemSummary deftemplate exists: if not, run {@link #script} to create.
	 * @param rete
	 * @param textOutput for error reporting
	 * @return true if deftemplate found or created; false on error
	 */
	boolean createTemplates(Rete rete, TextOutput textOutput) {
		try {
			Deftemplate psdt = rete.findDeftemplate(PROBLEM_SUMMARY);
			if(trace.getDebugCode("ps"))
				trace.out("ps", String.format("createTemplate() found %s deftemplate\n  %.50s ...",
						PROBLEM_SUMMARY, (psdt == null ? "null" : psdt.toString())));
		if(psdt != null)
			return true;
		} catch(JessException je) {
	        String errorMessage = "Error finding deftemplate "+PROBLEM_SUMMARY+": "+je;
	        textOutput.append("\n").append(errorMessage).append("\n");
			trace.errStack(errorMessage, je);
			// fall through to try to create anyway
		}
		try {
			Value result = rete.eval(script);
			if(Funcall.TRUE.equals(result))          // good result
				return true;
			String errorMessage = "Unexpected return value "+result+" from script to create "+
					PROBLEM_SUMMARY+" template";
	        textOutput.append("\n").append(errorMessage).append("\n");
			trace.err(errorMessage);			
		} catch(JessException je) {
			String errorMessage = "Error finding running script to define "+PROBLEM_SUMMARY+" template at line "+
	    	        je.getLineNumber()+":\n"+
	    	        (je.getDetail() == null ? "" : je.getDetail()+". ")+
	    	        (je.getData() == null ? "" : je.getData());
	        trace.errStack(errorMessage, je);
	        textOutput.append("\n").append(errorMessage).append("\n");
		}
		return false;
	}
}
