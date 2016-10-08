package tracer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import tracer.MTException.MTExceptionType;
import tracer.MTResult.MTResultType;
import jess.Activation;
import jess.Defglobal;
import jess.Defrule;
import jess.Fact;
import jess.Funcall;
import jess.JessEvent;
import jess.JessException;
import jess.JessListener;
import jess.QueryResult;
import jess.Rete;
import jess.Value;
import jess.ValueVector;
import jess.RU;
import jess.WorkingMemoryMarker;

/**
 * The model tracer. Use sendSAI to trace student sai's with the working memory,
 * use getHint to get hints from the tutor.
 * @author Alex Xiao
 *
 */

public class MTSolver {

	private Rete engine;
	private MTInputFunction traceFunction;
	private HintFunction hintFunction;
	private BuggyFunction buggyFunction;
	// SimStudent creates production rules that require the use of this *sInput*
	private static final String inputDefglobalName = "*sInput*";
	public String recentRuleFired = "";
	public boolean logProductionRules = false;
	public boolean logMessages = true;
	private boolean sendingHint = false;
	private boolean sendingSai = false;

	/**
	 * Creates a tracer that uses Jess files located on the file system or classpath
	 * @param init Path of wm init file
	 * @param types Path of wm type file
	 * @param production Path of production file
	 * @throws JessException
	 */
	public MTSolver(String init, String types, String production) throws JessException {
		this.engine = new Rete();
		// ?*sInput* is used in the production rules to store student's last input
		Defglobal studentInput = new Defglobal(inputDefglobalName, new Value("", RU.STRING));
		this.engine.addDefglobal(studentInput);

		addUserFunctions();

		this.engine.batch(types);
		this.engine.batch(init);
		this.engine.batch(production);
	}

	public void MTLog(Object message) {
		if (logMessages) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			System.out.println(dateFormat.format(date) + " ModelTracer Message: " + message);
		}
	}

	private void addUserFunctions() {
		// initialize user functions
		// trace function needs a reference to solver in order to update ?*sInput*
		this.traceFunction = new MTInputFunction(this);
		this.buggyFunction = new BuggyFunction();
		this.hintFunction = new HintFunction();

		this.engine.addUserfunction(this.traceFunction);
		this.engine.addUserfunction(this.hintFunction);
		this.engine.addUserfunction(this.buggyFunction);
		this.engine.addUserfunction(new FoaFunction());
	}

	/**
	 * Directly modifies the wme with an sai,  use with care, since this could cause the
	 * working memory to not match the student interface
	 * @param sai The sai that will modify the working memory
	 */
	public void modifyWithSAI(MTSAI sai) {
		try {
			MTLog("Modifyin wm!!!");
			getEngine().modify(findFact(sai.getSelection()), "value", new Value(sai.getInput(), RU.STRING));
		} catch (JessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sets the defglobal ?*sInput to the latest student input, required for
	 * SimStudent's generated production rules
	 * @param input The student's most recent input
	 */
	public void setStudentInput(String input) {
		try {
			getEngine().getGlobalContext().setVariable(this.inputDefglobalName, new Value(input, RU.STRING));
		} catch (JessException e) {
			// TODO Auto-generated catch block
			MTLog("setstudent input error");
			e.printStackTrace();
		}
	}

	/**
	 * Compares a student SAI with a list of activated production rules. We iterate
	 * over the list of activations, and popping each one off by running the Jess engine
	 * once. The production rules and Jess userfunctions will then provide us with feedback
	 * on the comparison between the SAI and activated rule. If they don't, then we found a
	 * match.
	 * @param sai The student SAI to be compared
	 * @return Returns a MTResult that either corresponds to the student being correct,
	 * the student being incorrect, or the student having triggered a buggy production
	 * rule (made a common error that we caught)
	 * @throws MTException These exceptions should always be caught, they are used in
	 * jess Userfunctions to signal information such as that we incorrectly matched a
	 * student sai with a tutor sai from an activated production rule.
	 * @throws JessException
	 */
	public MTResult sendSAI(MTSAI sai) throws MTException, JessException {
		if (!prepareSending(sai)) {
			return new MTResult(MTResultType.UNAVAILABLE, "Model tracer is busy!");
		}

		Iterator it = this.engine.listActivations();
		while(it.hasNext()) {
			try {
				int i=this.getEngine().run(1);
				if (i==0)
					throw new MTException(MTExceptionType.FAIL, "Emtpy agenda");
				
				// if here then student input is correct
				if (logProductionRules)
					MTLog("Fired production rule: " + it.next());
				this.hintFunction.resetHint();
				finishSending();
				return new MTResult(MTResultType.CORRECT, sai.toString());
			} catch (MTException e) {
				if (e instanceof MTException) {
					switch(e.getType()) {
					case BUGGYFAIL:
					case FAIL:
						break;
					case BUGGYMATCH:
						if (logProductionRules)
							MTLog("Matched buggy production rule: " + it.next());
						finishSending();
						return new MTResult(MTResultType.BUGGY, e.getMessage());
					default:
						MTLog("unknown exception case");
						e.printStackTrace();
					}

				} else {
					e.printStackTrace();
				}

			}
			it.next();
		}
		finishSending();
		return new MTResult(MTResultType.INCORRECT, sai.toString());
	}

	/**
	 * Attempts to find a hint for the given problem state. Similar to sendSAI, we list over
	 * current activations and fine one that has a hint userfunction on its RHS. 
	 * @return Returns the hint, or a blank string if the model tracer is already modifying
	 * working memory.
	 * @throws JessException
	 * @throws MTException
	 */
	public ArrayList<String> getHint() throws JessException, MTException {
		ArrayList<String> hintList = null;
		if (!prepareHint()) {
			return hintList;
		}

		Iterator it = this.engine.listActivations();

		
		while(it.hasNext()) {
			MTLog("Finding hint at production rule " + it.next());
			try {
				
				int i=this.getEngine().run(1);
	
				
				// ideally should never reach this point, every production rule must have buggy or hint function in them
				finishHint();
				throw new MTException(MTExceptionType.OTHER, "Missing buggy or hint function in production rule");
			} catch (MTException e) {
				switch (e.getType()) {
				case HINT:
					finishHint();
					String[] levels = e.getMessage().split("\\$");
					//System.out.println("$$$$$$ levels are " + e.getMessage());
					
			        hintList = new ArrayList<String>();
			        for (String s : levels){
			        
			        	hintList.add(s);
			        }
					return hintList;
				case NOHINT:
				case AUTO:
					break;
				default:
					MTLog("Unexpected exception case!");
					e.printStackTrace();
					return hintList;
				}

			}

		}
		finishHint();
//		hintList = new ArrayList<String>();
//		hintList.add("no hint available!");
		return hintList;
	}

	/**
	 * Activates the relevant userfunctions before tracing a sai.
	 * @param sai SAI that is about to be sent to tracer
	 * @return Returns false if already tracing an sai or finding a hint, true
	 * otherwise
	 */
	private boolean prepareSending(MTSAI sai) {
		if (sendingHint || sendingSai)
			return false;
		sendingSai = true;
		this.traceFunction.setSai(sai);
		this.buggyFunction.setSai(sai);
		this.traceFunction.activate(true);
		this.buggyFunction.activate(true);
		return true;
	}

	/**
	 * Deactivates the relevant userfunctinos after tracing a sai.
	 * @throws JessException
	 */
	private void finishSending() throws JessException {
		sendingSai = false;
		this.traceFunction.activate(false);
		this.buggyFunction.activate(false);
		resetAgenda();
	}

	/**
	 * Activates the relevant userfunctions before tracing a sai.
	 * @return Returns false if already tracing a sai or getting a hint,
	 * otherwise returns true.
	 */
	private boolean prepareHint() {
		if (sendingSai || sendingHint)
			return false;
		sendingHint = true;
		this.hintFunction.activate(true);
		this.buggyFunction.setAutoThrowException(true);
		return true;
	}

	/**
	 * Deactivates the relevant userfunctions after tracing a sai.
	 * @throws JessException
	 */
	private void finishHint() throws JessException {
		sendingHint = false;
		resetAgenda();
		this.hintFunction.activate(false);
		this.buggyFunction.setAutoThrowException(false);
	}

	/**
	 * After every attempt to trace the model for hints/comparing SAIs, we
	 * need to make sure former production rules remain activated. Since we
	 * run the production rules in sendSAI and getHint, which makes them no
	 * longer activated, this method reactivates them by "changing" working
	 * memory by retracting and asserting the problem fact.
	 * @throws JessException
	 */
	private void resetAgenda() throws JessException {
		Iterator it = getEngine().listFacts();
		while(it.hasNext()) {
			Fact fact = (Fact) it.next();
			if (fact.getName().indexOf("problem") != -1) {
//				MTLog("reset problem fact");
				this.engine.retract(fact);
				this.engine.assertFact(fact);

				break;
			}
		}
	}


	public void printActivations() throws JessException {
		Iterator it = this.engine.listActivations();
		if (!it.hasNext()) {
			MTLog("Rete has no activations!");
			return;
		}

		MTLog("Current Activations:");
		while(it.hasNext()) {
			MTLog(it.next());
		}
	}

	public void printFacts() throws JessException {
		Iterator it = this.engine.listFacts();
		if (!it.hasNext()) {
			MTLog("Rete has no asserted facts!");
			return;
		}

		MTLog("Asserted facts:");
		while(it.hasNext()) {
			MTLog(it.next());
		}
	}

	private Fact findFact(String name) {
		Iterator it = this.engine.listFacts();

		while(it.hasNext()) {
			Fact fact = (Fact) it.next();

			try {
				if (fact.getSlotValue("name").equals(name)) {
					return fact;
				}
			} catch (JessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public Rete getEngine() {
		return engine;
	}

	public void setEngine(Rete engine) {
		this.engine = engine;
	}
}
