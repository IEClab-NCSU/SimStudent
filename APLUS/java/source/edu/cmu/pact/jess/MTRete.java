package edu.cmu.pact.jess;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import jess.Activation;
import jess.Context;
import jess.Defquery;
import jess.Defrule;
import jess.Deftemplate;
import jess.Fact;
import jess.FactIDValue;
import jess.Funcall;
import jess.HasLHS;
import jess.Jesp;
import jess.JessEvent;
import jess.JessException;
import jess.JessListener;
import jess.JessToken;
import jess.PrettyPrinter;
import jess.QueryResult;
import jess.RU;
import jess.Rete;
import jess.Strategy;
import jess.Token;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import jess.WatchConstants;
import jess.WorkingMemoryMarker;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.EventLogger;
import edu.cmu.pact.Utilities.TextOutput;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.model.ProblemSummary;
import edu.cmu.pact.jess.SimStRete.ConflictResolutionStrategy;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.console.controller.MissController;

/**
 * This class extends the Rete class from jess package and 
 * add functionality to selectively fire rules
 * 
 * @author sanket@wpi.edu
 * @see jess.Rete
 */
public class MTRete extends Rete implements Serializable, JessParser,
											PropertyChangeListener{

	/**
	 * System property controlling whether to modify the student values fact at the
	 * start of each trace, for LHS matching.
	 */
	public static final String USE_STUDENT_VALUES_FACT = "UseStudentValuesFact";

	/** The name of the i/o router for most Jess input. */
	public static final String DEFAULT_IN_ROUTER = "WSTDIN"; 

	/** The name of the i/o router for most Jess output. */
	public static final String DEFAULT_IO_ROUTER = "WSTDOUT"; 

	/** The name of the i/o router for Jess error output. */
	public static final String DEFAULT_ERR_ROUTER = "WSTDERR"; 

	/** Special value means selection, action or input element is not predicted. */
	public static final String NOT_SPECIFIED = "NotSpecified";

	/** System property to choose single-pass trace */
	public static final String SINGLE_PASS_PREFERENCE = "Single Pass Search";

	/** Rule name pattern for fireable-buggy rules */
	private static final Pattern fireableBuggyPrefix =
	    Pattern.compile("^([^:][^:]*::)?[fF][iI][rR][eE]?[aA][bB][lL][eE]-?[bB][uU][gG]([gG][yY]|[^a-zA-Z]).*");

	/** Rule name pattern for buggy rules */
	private static final Pattern buggyPrefix =
	    Pattern.compile("^([^:][^:]*::)?[bB][uU][gG]([gG][yY]|[^a-zA-Z]).*");
	
	/** The list of facts that are created using the WME editor */
	private ArrayList factsList = new ArrayList();

	/** The list of deftemplates that are created using the WME editor */
	private ArrayList deftemplateList = new ArrayList();

	private static final String USE_SALIENCE = "Use Salience";
//	private static final String MAX_BUGGY_RULES = "Max Buggy Rules";
	static final String TREE_DEPTH = "Tree Depth";

    /** The name of the selection-action-input Deftemplate. */
	static final String SAINAME = "special-tutor-fact";
    /** The name of the Deftemplate for a correct selection-action-input. */
	static final String CORRECTSAINAME = SAINAME + "-correct";
    /** The name of the Deftemplate for an incorrect selection-action-input. */
	static final String BUGGYSAINAME = SAINAME + "-buggy";
    /** The name of the selection-action-input Deftemplate. */
	static final String OLDSAINAME = "selection-action-input";
	static final String SPECIALWME = "special-wme";
	
	/** Return value for {@link #findActivation(Activation)}. */
	static final String NOT_ON_AGENDA = "not on agenda";
	
	/** Return value for {@link #findActivation(Activation)}. */
	static final String ACTIVATION_INACTIVE = "inactive";
	
	/** Return value for {@link #findActivation(Activation)}. */
	static final String ACTIVATION_ACTIVE = "active";

	/** preferences to control the trace displayed in the jess prompt window. */
	static boolean displayChain = true;
	static boolean displayFired = false;
	static boolean instantiations = false;
	static boolean breakOnExceptions = true;
	/** constant string to indicate the dont care condition for the selection, 
	 *  action and input in the rules. */
	public static final String DONT_CARE = "DONT-CARE";
	/** The list of rules that have fired */	
	private ArrayList firedRuleList = new ArrayList();    

	/** For LHS matching, added by Bo Chen for Ido Roll's study Spring 2009. */
	private transient jess.Fact studentValuesFact = null;
	
	/** Map to store the buggy rules: key is rule name, value is {@link jess.Defrule} */
	// Map buggyRules = new Hashtable();

	/** boolean variable to indicate logging */
	public static boolean doLog = true;
	
	/** Default value for {@link #maxDepth}. */
	static int DEFAULT_MAX_DEPTH = 5;
	
	/** Maximun depth of the iterative deepening search during model-tracing */
	private int maxDepth = DEFAULT_MAX_DEPTH;

	boolean useBackwardChaining = false;
	
	static boolean stopModelTracing = false;
	
	

	public static boolean useSalience = true;
  
	/** List of the deftemplate names of facts in the cache. */
	private static final String[] cacheKeys = {
		CORRECTSAINAME,
		BUGGYSAINAME,
		SAINAME,
		OLDSAINAME,
		SPECIALWME
	};

	/** Number of facts cached. */
	private static final int factCount = cacheKeys.length;

	/** Event mask for listener: FACT, RESET and CLEAR events. */
	private static final int EVENT_MASK =
		JessEvent.FACT | JessEvent.RESET | JessEvent.CLEAR |
		JessEvent.REMOVED;

	/** Name of Jess's default {@link Strategy}. */
	private static final String DEFAULT_STRATEGY_NAME = (new Rete()).getStrategy().getName();
	
	/** Watch options to set in each engine created. */
	private static java.util.List watchOptions = null;
 	
//	private CTAT_Launcher server;
	
	/**
	 * A special type of JessException thrown to intentionally halt
	 * the Rete engine during execution of a rule's RHS.
	 * @see MTRete#haltRete()
	 */
	public static class HaltReteException extends JessException {
		
		/**
		 * Constructor with message.
		 * @param routine name of calling routine 
		 * @param message value for {@link JessException#getMessage()}
		 */
		public HaltReteException(String routine, String message) {
			super(routine, "Rete interrupted", message);
		}
	}
	
	/**
	 * A class to save input and output routers, which are transient in the
	 * {@link jess.Rete} class.
	 */
	public static class Routers {
		/** The "t" input router. */
		private final Reader tReader;
		/** Whether the "t" reader is console-like. */
		private final boolean tConsoleLike;
		/** The "t" output router. */
		private final Writer tWriter;
		/** Reader for stdin, usually same as {@link #tReader}. */
		private final Reader stdinReader;
		/** Whether the "stdin" reader is console-like. */
		private final boolean stdinConsoleLike;
		/** Writer for stdout, usually same as {@link #tWriter}. */
		private final Writer stdoutWriter;
		/** Writer for stderr, usually same as {@link #tWriter}. */
		private final Writer stderrWriter;
		
		/**
		 * Constructor sets all fields.
		 * @param rete rule engine whose routers we're to save
		 */
		Routers(Rete rete) {
			tReader = rete.getInputRouter("t");
			tConsoleLike = rete.getInputMode("t");
			tWriter = rete.getOutputRouter("t");
			stdinReader = rete.getInputRouter(DEFAULT_IN_ROUTER);
			stdinConsoleLike = rete.getInputMode(DEFAULT_IN_ROUTER);
			stdoutWriter = rete.getOutputRouter(DEFAULT_IO_ROUTER);
			stderrWriter = rete.getOutputRouter(DEFAULT_ERR_ROUTER);
		}
		
		/**
		 * Set engine's routers from own contents.
		 * @param rete rule engine whose routers we're to set
		 */
		void setRouters(Rete rete) {
			rete.addInputRouter("t", tReader, tConsoleLike);
			rete.addInputRouter(DEFAULT_IN_ROUTER, stdinReader, stdinConsoleLike);
			rete.addOutputRouter("t", tWriter);
			rete.addOutputRouter(DEFAULT_IO_ROUTER, stdoutWriter);
			rete.addOutputRouter(DEFAULT_ERR_ROUTER, stderrWriter);
			dumpRouters("setRouters()", rete);
		}
		
		/**
		 * Print the routers for debugging.
		 * @param label tag for the prints
		 * @param rete Rete to query
		 */
		static void dumpRouters(String label, Rete rete) {
			if (!trace.getDebugCode("mt"))
				return;
			if (trace.getDebugCode("mt")) trace.out("mt", (label==null?"(null)":label)+ " rete "+rete.hashCode()+
					", context rete "+((Context) rete.getGlobalContext()).getEngine().hashCode());
			if (trace.getDebugCode("mt")) trace.out("mt", "t input router="+rete.getInputRouter("t"));
			if (trace.getDebugCode("mt")) trace.out("mt", DEFAULT_IN_ROUTER+" input router="+
					rete.getInputRouter(DEFAULT_IN_ROUTER));
			if (trace.getDebugCode("mt")) trace.out("mt", "t output router="+rete.getOutputRouter("t"));
			if (trace.getDebugCode("mt")) trace.out("mt", DEFAULT_IO_ROUTER+" output router="+
					rete.getOutputRouter(DEFAULT_IO_ROUTER));
			if (trace.getDebugCode("mt")) trace.out("mt", DEFAULT_ERR_ROUTER+" output router="+
					rete.getOutputRouter(DEFAULT_ERR_ROUTER));
		}
	}
	
	/**
	 * Implementation of {@link Strategy} interface to prefer one particular Activation,
	 * chosen externally, above all.
	 */
	static class BuggyRulesNormalSalienceStrategy implements Strategy, Serializable {
		
		/** For {@link java.io.Serializable} */
		private static final long serialVersionUID = 201309131040L;

		/** Returned by {@link #getName()}. */
		public static final String NAME = "buggy-rules-normal-salience";
		
		/** The distinguished Activation to fire. */
		private Activation actToFire = null;
		
		/** Cached reference to {@link BuggyRulesNormalSalienceStrategy#actToFire}. */
		private Activation cachedActToFire = null;
		
		/** Debug: track cache use. */
		protected int cacheUse = 0;

		/** Place to retrieve the original strategy. */
		protected final Strategy originalStrategy;
		
		/**
		 * Sets {@link #originalStrategy} from a new instance of {@link Rete#getStrategy()}.
		 */
		BuggyRulesNormalSalienceStrategy() {
			originalStrategy = (new Rete()).getStrategy();					
		}
		
		/**
		 * Set {@link BuggyRulesNormalSalienceStrategy#actToFire}.
		 * Nulls {@link BuggyRulesNormalSalienceStrategy#actToFire} to force
		 * reevaluation during next call to
		 * {@link BuggyRulesNormalSalienceStrategy#compare(Object,Object)}.
		 * @param act new value; may be null
		 */
		void setActToFire(Activation act, Rete rete) {
			if (trace.getDebugCode("mtt")) trace.out("mtt", "setActToFire() old, new["+
					getActivationIndex(act, rete)+"]:\n "+actToFire+"\n "+act);
			actToFire = act;
			cachedActToFire = null;
			cacheUse = 0;
		}
		
		/**
		 * Install an instance of this strategy in the given Rete. 
		 * @param r Rete to set
		 * @return prior strategy
		 */
		protected Strategy install(Rete r) {
			Strategy oldStrategy = null;
			try {
				oldStrategy = r.getStrategy();
				r.setStrategy(this);
				return oldStrategy;
			} catch (JessException je) {
				je.printStackTrace();
				return oldStrategy;
			}
		}
		
		/**
		 * Return a display name for this strategy.
		 * @return class name
		 * @see Strategy#getName()
		 */
		public String getName() {
			return NAME;
		}
		
		/**
		 * Compares 2 activations w.r.t. whether one is preferred according to
		 * {@link #testCachedActs(Activation, Activation)}.
		 * @param a0 left-hand operand
		 * @param a1 right-hand operand
		 * @return -1 if a0 is preferred over a1; 1 if vice versa;
		 *         else result of {@link #originalStrategy}.compare(a0, a1)
		 * @see Strategy#compare(jess.Activation, jess.Activation)
		 */
		public int compare(Activation a0, Activation a1) {
			int result = testCachedActs(a0, a1);
			if (result == 0)
				result = getOriginalStrategy().compare(a0, a1);

			if (trace.getDebugCode("strat")) {
				Defrule r0 = a0.getRule();
				Defrule r1 = a1.getRule();
				trace.out("strat", getName()+".compare("+r0.getName()+","+r1.getName()+
					")->"+result+", cacheUse "+cacheUse);
			}
			return result;
		}

		/**
		 * @return {@link #originalStrategy}
		 */
		protected Strategy getOriginalStrategy() {
			return originalStrategy;
		}

		/**
		 * Test whether the given activations are either {@link #cachedActToFire} or {@link #actToFire}.
		 * @param a0
		 * @param a1
		 * @return -1 if a0 matches {@link #actToFire}; 1 if a1 matches {@link #actToFire}; 
		 */
		protected int testCachedActs(Activation a0, Activation a1) {
			int result = 0;
			if (cachedActToFire != null) {
				cacheUse++;
				if (a0 == cachedActToFire)
					result = -1;
				if (a1 == cachedActToFire)
					result = 1;
			} else if (actToFire != null) {
				if (a0.equals(actToFire)) { // test was == w/ Jess 7.0b7
					result = -1;
					cachedActToFire = a0;
				}
				if (a1.equals(actToFire)) {  // test was == w/ Jess 7.0b7
					result = 1;
					cachedActToFire = a1;
				}
			}
			return result;
		}
	}
	
	/**
	 * Implementation of {@link Strategy} interface to prefer ordinary rules
	 * over buggy rules. Prefers one particular Activation, if set, above all.
	 */
	static class BuggyRulesLaterStrategy extends BuggyRulesNormalSalienceStrategy {
		
		/** For {@link java.io.Serializable}. */
		private static final long serialVersionUID = 201309141315L;

		/** Returned by {@link #getName()}. */
		public static final String NAME = "buggy-rules-later";
		
		/**
		 * Just call superclass constructor.
		 */
		BuggyRulesLaterStrategy() {
			super();
		}
		
		/**
		 * Return a display name for this strategy.
		 * @return class name
		 * @see Strategy#getName()
		 */
		public String getName() {
			return NAME;
		}
		
		/**
		 * Compares 2 activations w.r.t. bugginess. After ordinary correct rules, prefers
		 * {@link JessModelTracing#FIREABLEBUG} rules over {@link JessModelTracing#BUG} rules.
		 * Otherwise behaves like
		 * {@link MTRete.BuggyRulesNormalSalienceStrategy#compare(Activation, Activation)}.
		 * @param a0 left-hand operand
		 * @param a1 right-hand operand
		 * @return -1 if a0 ordinary and a1 buggy; 1 if vice versa;
		 *         else result of {@link originalStrategy}.compare(a0, a1)
		 * @see Strategy#compare(jess.Activation, jess.Activation)
		 */
		public int compare(Activation a0, Activation a1) {
			Defrule r0 = a0.getRule();
			Defrule r1 = a1.getRule();
			int result = testCachedActs(a0, a1);
			if (result == 0) {
				if (isCorrectRule(r0) && !isCorrectRule(r1))         // prefer correct over all others
					result = -1;
				else if (!isCorrectRule(r0) && isCorrectRule(r1))
					result = 1;
				else if (!isBuggyRule(r0) && isBuggyRule(r1))        // prefer fireable buggy over buggy
					result = -1;
				else if (isBuggyRule(r0) && !isBuggyRule(r1))
					result = 1;
				else
					result = getOriginalStrategy().compare(a0, a1);  // need singleton
			}
			if (trace.getDebugCode("strat")) trace.out("strat", getName()+".compare("+r0.getName()+","+r1.getName()+
					")->"+result+", cacheUse "+cacheUse);
			return result;
		}
	}
	
	/**
	 * Tell our Strategy which Activation we are firing now.
	 * Calls {@link BuggyRulesLaterStrategy#setActToFire(Activation).
	 * @param act the Activation to fire next; null to restore normal strategy
	 */
	void setActivationToFire(Activation act) {
		Strategy s = getStrategy();
		if (!(s instanceof BuggyRulesNormalSalienceStrategy))
			return;
		((BuggyRulesNormalSalienceStrategy) s).setActToFire(act, this);
		try {
			setStrategy(s);  // prompt Rete to resort the agenda?
			if (trace.getDebugCode("mt")) trace.out("mt", "setActivationToFire() a["+
					getActivationIndex(act, this)+"]: "+act);
		} catch (JessException je) {
			je.printStackTrace();
		}
	}
	
    public void setResolutionStrategy(Strategy newStrategy) throws JessException{
		setStrategy(newStrategy);
	}
	
	
	/**
	 * Class to listen for Rete modifications.
	 */
	private static class ReteChangedListener implements JessListener {
		
		/**
		 * Mask of JessEvent types we're interested in.  These include all
		 * events that can affect the agenda.
		 */
		public static final int EVENT_MASK =
			JessEvent.ACTIVATION |    // defrule activated or deactivated
			JessEvent.DEFRULE_FIRED | // rule fired
			JessEvent.FACT |          // fact asserted or retracted
			JessEvent.MODIFIED |      // or'd to other flag for modified fact
			JessEvent.RESET;          // reset executed

		/** MTRete we're listening to. */
		private final MTRete rete;
		
		/**
		 * Constructor sets {@link MTRete.ReteChangedListener#rete} and
		 * registers self as listener.
		 */
		ReteChangedListener(MTRete rete) {
			this.rete = rete;
			rete.addJessListener(this);
			rete.setEventMask(rete.getEventMask() | EVENT_MASK);
		}
		
		/**
		 * Increments {@link MTRete#reteChangeCount} or
		 * {@link MTRete#rulesFiredCount}.
		 * @param je 
		 * @see jess.JessListener#eventHappened(jess.JessEvent)
		 */
		public void eventHappened(JessEvent je) {
//			trace.out("mt", "ReteChangedListener event: "+je+", obj "+je.getObject());
			if (je.getType() == JessEvent.DEFRULE_FIRED)
				++rete.rulesFiredCount;
			else
				++rete.reteChangeCount;
		}
	}
	
	/** Count of Rete changes during the last rule firing. */
	private transient int reteChangeCount = 0;
	
	/** Count of rule firings during the last call to {@link #run(int)}. */
	private transient int rulesFiredCount = 0;

	/** Console output for exceptions and Jess prints. */
	private transient TextOutput textOutput = TextOutput.getNullOutput();
	
	/** Reference to the model tracer. */
	protected transient JessModelTracing jmt = null;
	
	/** Reference to the top-level model tracing object. */
	private transient MT mt = null;

	/** The {@link Strategy#getName()} of the last author-set Strategy instance. */
	private String lastAuthorChosenStrategyName = BuggyRulesLaterStrategy.NAME;
	
	/** If true, provide rule access to {@link ProblemSummary} info. */
	private Boolean psAccess = null;

	/** For logging author actions. */
	private transient EventLogger eventLogger; 

	/**
	 * Constructor for callers who need author logging and preference listening.
	 * Equivalent to {@link #MTRete(EventLogger, PreferencesModel, CTAT_Controller) MTRete(null, null, null)}
	 */
	public MTRete() {
		this(null, null, null);
	}

	/**
	 * Constructor for callers who need author logging and preference listening.
	 * Equivalent to {@link #MTRete(EventLogger, PreferencesModel, CTAT_Controller) MTRete(eventLogger, pm, controller)}
	 * @param controller must be non-null
	 */
	public MTRete(CTAT_Controller controller) {
		this(controller.getEventLogger(), controller.getPreferencesModel(), controller);
	}
	
	/**
	 * Constructor for {@link WhyNot}, others who need author logging but no preference listening.
	 * Equivalent to {@link #MTRete(EventLogger, PreferencesModel, CTAT_Controller) MTRete(eventLogger, pm, null)}
	 * @param eventLogger argument for {@link #init(EventLogger, PreferencesModel)}
	 * @param pm argument for {@link #init(EventLogger, PreferencesModel)}
	 */
	public MTRete(EventLogger eventLogger, PreferencesModel pm) {
		this(eventLogger, pm, null);
	}
	/**
	 * The constructor for this class
	 * @param eventLogger argument for {@link #init(EventLogger, PreferencesModel)}
	 * @param pm argument for {@link #init(EventLogger, PreferencesModel)}
	 * @param controller needed only for {@link CTAT_Controller#getApplet()} until Jess no longer needs it
	 */
	private MTRete(EventLogger eventLogger, PreferencesModel pm, CTAT_Controller controller) {
        super();
        if (trace.getDebugCode("mt")) trace.out("mt", "entered MTRete's constructor");
        if(controller != null)
        	setApplet(controller.getApplet());
		setUpListener();
		init(eventLogger, pm);
	    (new BuggyRulesLaterStrategy()).install(this);  // default strategy
		if (trace.getDebugCode("mt")) trace.out("mt", "no-arg constructor");
    }
   
	/** 
	 * Access for this package and subclasses to package's event logger.
	 * This logger is for author event logging, not ordinary message logging.
	 * @return {@link #eventLogger}
	 */
	public EventLogger getEventLogger() {
	    return eventLogger;
  	}
	
	/**
	 * Debug routine to show activation state.
	 * @param label tag for debug message
	 */
	void showActivations(String label) {
		if (!trace.getDebugCode("mt"))
			return;
		int nActs = 0, nFacts = 0, nRules = 0, nTemplates = 0;
		for (Iterator it = listActivations(); it.hasNext(); ++nActs)
			it.next();
		for (Iterator it = listFacts(); it.hasNext(); ++nFacts)
			it.next();
		for (Iterator it = listDefrules(); it.hasNext(); ++nRules)
			it.next();
		for (Iterator it = this.listDeftemplates(); it.hasNext(); ++nTemplates)
			it.next();
		if (trace.getDebugCode("mt"))
			trace.out("mt", label+" nActs="+nActs+" nFacts="+nFacts+" nRules="+nRules+
					" nTemplates="+nTemplates+" "+trace.nh(this));
	}


	/**
	 * Common code for constructors.  All constructors must call this method.
	 * Sets up the event listener.
	 */
	/*PRIVATE*/ void setUpListener() {
            if (trace.getDebugCode("mt")) trace.out("mt", "entered setUpListener()");
		registerWatchOptions();
		new ReteChangedListener(this);
		if (true)
			return;  //FIXME!!!
		trace.out("mt" , "setUpListener() event mask "+
				Integer.toHexString(getEventMask()));
		setEventMask(getEventMask() | EVENT_MASK);
	}
	
	/**
	 * Set the Jess (watch ...) options wanted for rule engine instances.
	 * @param watchOptions list of one or more Integer values <ul>
	 *        <li>{@link WatchConstants#ACTIVATIONS}</li>
	 *        <li>{@link WatchConstants#COMPILATIONS}</li>
	 *        <li>{@link WatchConstants#FACTS}</li>
	 *        <li>{@link WatchConstants#FOCUS}</li>
	 *        <li>{@link WatchConstants#RULES}</li>
	 *        </ul>
	 *        or the lower-case String equivalents of these, which are the
	 *        legal arguments to the (watch) Userfunction.
	 */
	static void setWatchOptions(java.util.List watchOptions) {
		MTRete.watchOptions = watchOptions;
	}

	/**
	 * For the current instance of the engine, set the watch options stored
	 * in {@link #watchOptions}.  If the field is null, then call
	 * {@link Rete#unwatchAll()}; if the field exists but is empty, call
	 * {@link Rete#watchAll()}.
	 * @see #setWatchOptions(java.util.List)
	 */
	private void registerWatchOptions() {
		if (watchOptions == null)
			unwatchAll();
		else {
			if (watchOptions.isEmpty())
				watchAll();
			else {
				for (Iterator it = watchOptions.iterator(); it.hasNext(); ) {
					Object watchOption = it.next();
					try {
						if (watchOption instanceof Integer) {
							int watchInt = ((Integer) watchOption).intValue();
							watch(watchInt);
						}
						else if (watchOption instanceof String) {
							Userfunction watchFn = findUserfunction("watch");
							ValueVector vv = new ValueVector(2);
							vv.add("watch");
							vv.add((String) watchOption);
							watchFn.call(vv, getGlobalContext());
						}
					} catch (Exception e) {
						trace.err("error setting watchOption "+watchOption+": "+e);
					}
				}
			}
		}
	}

	/**
	 * Listen to the {@link PreferencesModel} for preferences of interest here.
	 * Also set {@link #eventLogger}. Call this from any constructor.
	 * @param eventLogger value for {@link #eventLogger}
	 * @param pm to register self as listener for {@value #TREE_DEPTH}, {@value #USE_SALIENCE} events 
	 */
	private void init(EventLogger eventLogger, PreferencesModel pm) {
		if (eventLogger == null)
			this.eventLogger = new EventLogger(null);
		else
			this.eventLogger = eventLogger;

		if (pm == null)
			return;
		pm.addPropertyChangeListener(TREE_DEPTH, this);
		pm.addPropertyChangeListener(USE_SALIENCE, this);
		getPreferencesFromModel(pm);
	}

	/**
	 * Remove the listeners set up by {@link #init(EventLogger, PreferencesModel)}.
	 * @param pm
	 */
	void removeListeners(PreferencesModel pm) {
		if (pm == null)
			return;
		pm.removePropertyChangeListener(TREE_DEPTH, this);
		pm.removePropertyChangeListener(USE_SALIENCE, this);
	}

	/**
	 * Initialize our preference settings.
	 */
	private void getPreferencesFromModel(PreferencesModel model) {
		
		final Integer integerValue = model.getIntegerValue(TREE_DEPTH);
        if (integerValue != null)
            setMaxDepth (integerValue.intValue());
//		numberOfBuggyRules = model.getIntegerValue(MAX_BUGGY_RULES).intValue();
		final Boolean booleanValue = model.getBooleanValue(USE_SALIENCE);
        if (booleanValue != null)
            useSalience  = booleanValue.booleanValue();
	}
	
	/**
	 * Method to receive {@link PropertyChangeEvent}s from
	 * {@link PreferencesModel}.
	 * @param  arg0 event to process
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		String name = arg0.getPropertyName();
		Object newValue = arg0.getNewValue();
		if (trace.getDebugCode("mps")) trace.out("mps", "Changed " + name + " from " + arg0.getOldValue() +
				  " to " + newValue);
		if (name.equals (TREE_DEPTH))
			setMaxDepth (((Integer)newValue).intValue());
//		if (name.equals(MAX_BUGGY_RULES))
//			numberOfBuggyRules = ((Integer)newValue).intValue();
		if (name.equals(USE_SALIENCE))
			MTRete.useSalience  = ((Boolean) newValue).booleanValue();
	}

	/**
	 * Connect the console output of this Rete to the given output sink.
	 * @param  textOutput new value for {@link #textOutput}
	 */
	public void setTextOutput(TextOutput textOutput) {
		this.textOutput = textOutput;
	}

	/**
	 * Return the output sink to which this Rete's console output is connected
	 * @return {@link #textOutput}
	 */
	public TextOutput getTextOutput() {
		return textOutput;
	}

	
	 public void updateChunkValues(String selection,String input){

	 }
	 
	/**
	 * Access for rules to the {@link JessModelTracing} instance
	 * @return Returns the jmt.
	 */
	public JessModelTracing getJmt() {
		return jmt;
	}

	/**
	 * Sets {@link #jmt}. Also replaces Strategy with new instance if existing strategy was an
	 * instance of {@link MTRete.BuggyRulesNormalSalienceStrategy}.
	 * @param jmt The jmt to set.
	 */
	protected void setJmt(JessModelTracing jmt) {
		this.jmt = jmt;
	}

	/**
	 * @return {@link #lastAuthorChosenStrategyName}
	 */
	public String getLastAuthorChosenStrategyName() {
		return lastAuthorChosenStrategyName;
	}

	/**
	 * @param strategyName new value for {@link #lastAuthorChosenStrategyName}
	 * @return old value of {@link #lastAuthorChosenStrategyName}
	 */
	public String setLastAuthorChosenStrategyName(String strategyName) {
		String result = this.lastAuthorChosenStrategyName;
		this.lastAuthorChosenStrategyName = strategyName;
		return result;
	}

	
	@Override
	public Fact modify(Fact arg0, String arg1, Value arg2) throws JessException{
			//if (trace.getDebugCode("mt11")) trace.printStack("mt11", "mt rete modify " + arg2.toString());
			super.modify(arg0, arg1, arg2);
			return arg0;			
	}
	
	/**
	 * Override to also clear {@link #buggyRules}, restore listener.
	 */
	public synchronized void clear() throws JessException {
		super.clear();
		setStrategyByName(getLastAuthorChosenStrategyName());
	
		//buggyRules.clear();
	
		
		// setUpListener();
	}

	/**
	 * Instantiate a strategy of the given name and effect it with {@link #setStrategy(Strategy)}.
	 * @param strategyName; if null, restore original default strategy
	 * @return true if succeeds; false (no-op) if 
	 */
	public boolean setStrategyByName(String strategyName) throws JessException {
		boolean result = true;               // default return is success
		Strategy strategyResult;
		
		if(BuggyRulesLaterStrategy.NAME.equalsIgnoreCase(strategyName))
		{
			strategyResult = new BuggyRulesLaterStrategy(); 
			((BuggyRulesLaterStrategy) strategyResult).install(this);
		}
		else if(BuggyRulesNormalSalienceStrategy.NAME.equalsIgnoreCase(strategyName))
		{
			strategyResult = new BuggyRulesNormalSalienceStrategy();
			((BuggyRulesNormalSalienceStrategy) strategyResult).install(this);
		}
		else if("".equals(strategyName) || strategyName == null || DEFAULT_STRATEGY_NAME.equalsIgnoreCase(strategyName))
		{
			setStrategy(strategyResult = new Rete().getStrategy());  // restore default from Jess
		}
		else
		{
			trace.err("MTRete.setStrategyByName() unknown strategy name argument "+strategyName);
			strategyResult = null;
			result = false;    // error return
		}
		if(trace.getDebugCode("mt"))
			trace.out("mt", "MTRete.setStrategyByName("+strategyName+") returns "+result+
					"; new strategy name "+(strategyResult == null ? null : strategyResult.getName()));
		return result;
	}

	/**
	 * Override to maintain fact cache, save list of buggy rules.
	 */
	public void bload(InputStream is)
			throws java.io.IOException, java.lang.ClassNotFoundException {
		if(trace.getDebugCode("mtt"))
			trace.out("mtt", "MTRete.bload("+is+") before super.bload(is)");
		EventLogger eventLogger = this.eventLogger;
		JessModelTracing jmt = this.jmt;
		MT mt = this.mt;
		super.bload(is);
		this.mt = mt;
		this.jmt = jmt;
		this.eventLogger = eventLogger; 
//		jmt.setRete(this);  2013/09/04: jmt can be null!

		if(trace.getDebugCode("mtt"))
			trace.out("mtt", "MTRete.bload("+is+") before after.bload(is)");
		setUpListener();
//		ObjectInputStream ois = new ObjectInputStream(is);
//		buggyRules = (Hashtable) ois.readObject();
//		trace.out("mt", "buggyRules.size() after bload=" + buggyRules.size());
		// setUpListener();
	}

	/**
	 * Override to maintain list of buggy rules.
	 * @return marker for {@link #resetToMark(WorkingMemoryMarker)}
	 */
	public void bsave(OutputStream os)
			throws java.io.IOException {
            
//		Routers.dumpRouters("before Rete.bsave(", this);
		this.dumpAgenda("!!before Rete.bsave()");
		super.bsave(os);
//		WorkingMemoryMarker result = mark();
		this.dumpAgenda("!!after Rete.bsave()");
//		ObjectOutputStream oos = new ObjectOutputStream(os);
//		oos.writeObject(buggyRules);
//		oos.flush();
	}
	
	/**
	 * Finds a activation on the agenda.  Unlike {@link #findActivation(Activation)},
	 * requires that given Activation and object on the agenda be the same object.
	 * @param act Activation to find
	 * @param rete engine to check
	 * @return 0-based index on agenda if found; -1 if not found; -2 if null 
	 */
	public static int getActivationIndex(Activation act, Rete rete) {
		if (act == null)
			return -2;
		int result = 0;
		for (Iterator it = rete.listActivations(); it.hasNext(); ++result) {
			if (act.equals(it.next()))
				return result;
		}
		return -1;
	}
	
	
	/**
	 * Debug method to dump the activation list.  No-op if debug code "mt" unset. 
	 * @param  label print this String before the agenda; "dumpAgenda" if null
	 */
	public void dumpAgenda(String label) {
	    dumpAgenda("mt", label, false);
	}
	
	/**
	 * Debug method to dump the activation list.  No-op if debug code not set. 
	 * @param  debugCode if "err", use {@link trace#err(String)};
	 *             use {@link trace#out(String, String) trace#out(debugCode, String)}
	 * @param  label print this String before the agenda; "dumpAgenda" if null
	 * @param  verbose true means long format of activation entries
	 */
	public void dumpAgenda(String debugCode, String label, boolean verbose) {
	    if (debugCode == null)
	        return;
	    boolean err = "err".equals(debugCode); 
	    if (!err && !trace.getDebugCode(debugCode))
	    	return;
	    StringBuffer sb =
	        new StringBuffer(label == null ? "dumpAgenda()" : label);
	    sb.append(":");
//	    Map actMap = new HashMap();
	    Iterator it = listActivations(); 
	    for (int i = 0; it.hasNext(); ++i) {
	        Activation a = (Activation) it.next(); 
//	        actMap.put(new Integer(a.hashCode()), a);
	        sb.append("\n ");
	        if (i < 10) sb.append(" ");
	        sb.append(i).append(".");
	        sb.append(a.isInactive() ? "IN " : "AC ");
	        if (!verbose)
	        	sb.append(a.toString());
	        else {
	        	sb.append('[').append(a.getRule() == null ? "(null rule)" : a.getRule().getName());
	        	sb.append(' ').append(a.getToken() == null ? "(null token)" : a.getToken().toString());
	        	sb.append("; salience ").append(a.getSalience()).append(']');
	        }
	    }
	    sb.append(" <").append(getClass().getName()).append(".dumpAgenda>");
	    if (err)
	    	trace.err(sb.toString());
	    else
	    	trace.out(debugCode, sb.toString());

//	    it = listActivations(); 
//	    for (int i = 0; it.hasNext(); ++i) {
//	        Activation a = (Activation) it.next();
//	        Activation aa = (Activation) actMap.get(new Integer(a.hashCode()));
//	        trace.outNT("mtt", "agenda a["+i+"]"+
//	                (aa == null ? " not" : "")+" in map,"+  // not null in both 
//	                (a == aa ? "" : " not")+ " same obj");  // == in 7.0b7, != in 7.0
//	    }
	}
	
	/**
	 * Find a given {@link Activation} on the agenda.
	 * @param  a Activation to lookup
	 * @return one of {@link #ACTIVATION_ACTIVE}, {@link #ACTIVATION_INACTIVE},
	 *         or {@link #NOT_ON_AGENDA}
	 */
	String findActivation(Activation a) {
		if (a == null)
			return NOT_ON_AGENDA;
		for (Iterator it = this.listActivations(); it.hasNext();) {
			Activation aa = (Activation) it.next();
			if (activationsEqual(a, aa)) {
				if (aa.isInactive())
					return ACTIVATION_INACTIVE;
                return ACTIVATION_ACTIVE; 
			}
		}
		return NOT_ON_AGENDA;
	}

    
    /**
     * Return some initial sequence of the agenda onto a list.
     * Copies all {@link jess.Activation} entries, regardless of whether active.
     * @param stopAct if not null, stop just before adding this activation
     */
    public List /* Activation */ getAgendaAsList(Activation stopAct) {
        List result = new ArrayList();
        int i = 0;
        for (Iterator it = listActivations(); it.hasNext(); ++i) {
            Activation act = (Activation) it.next();
            if (trace.getDebugCode("mt")) trace.out("mt", "agenda["+i+"]= "+
                    (act.isInactive() ? "IN "  : "AC ")+
                    act);
            if (stopAct != null && stopAct == act)
                break;
            result.add(act);
        }
        return result;
    }
    
    /**
     * Test whether 2 activations are equal.
     * @param  a1
     * @param  a2
     * @return true if equal
     * @throws JessException
     */
    private boolean activationsEqual(Activation a1, Activation a2) {
    	if (a1 == null)
    		return (a2 == null);
    	else if (a2 == null)
    		return false;
    	String name1 = a1.getRule().getName(); 
    	String name2 = a2.getRule().getName();
    	boolean tokensEq = a1.getToken().dataEquals(a2.getToken());
    	if (trace.getDebugCode("mt")) trace.out("mt", "activationsEqual()"+name1+"?="+name2+" && tokensEq"+tokensEq);
    	return name1.equals(name2) && tokensEq;
    }
    
	/**
	 * Method removeAllRules.
	 * This method removes all the rules currently in the rete engine
	 */
    public void removeAllRules() throws JessException{
    	Iterator it = listDefrules();
    	ArrayList ruleNames = new ArrayList();
    	
    	while (it.hasNext()) {
    		Object obj =  it.next();
    		if(obj.getClass().getName().equalsIgnoreCase("jess.defrule")){
				ruleNames.add(((Defrule)obj).getName());
    		}
		}
		it = ruleNames.iterator();
		while (it.hasNext()) {
			unDefrule((String) it.next());
		}
		ruleNames.removeAll(ruleNames);
		ruleNames = null;
    }
    
	/**
	 * Method getFacts.
	 * This method returns the list of facts in the rete engine
	 * Callers should regard this list as *read-only*.
	 * @return ArrayList - the list of facts
	 */
    public ArrayList getFacts(){
    	ArrayList facts = new ArrayList();
    	for (Iterator it = this.listFacts(); it.hasNext(); )
    		facts.add(it.next());
		return facts;
    }

	private HashMap	saveStates = new HashMap();

	/**
	 * Whether the Conflict Tree should include only new activations among the
	 * children of a chain node.  @see #getPrunePriorActivations().
	 */
	private boolean prunePriorActivations = false;

	/**
	 * Whether to modify the student values fact at the start of each trace, for LHS matching.
	 */
	private Boolean useStudentValuesFact = null;

	/** Regex pattern to find embedded whitespace. */
	private static final Pattern spacePattern = Pattern.compile("\\p{Space}"); 
 
	void clearState()
	{
		saveStates.clear();
	}

	/**
	 * Save the current state to a byte array.
	 * @param  baos output stream into which to save
	 */
	Routers saveState(ByteArrayOutputStream baos) throws IOException {

	    Routers routers = new Routers(this);
	    bsave(baos);
	    return routers;
	}

	/**
	 * Load the state from a byte array.
	 * @param  bais input stream into which to save
	 * @param  routers for restoring routers
	 */
	void loadState(ByteArrayInputStream bais, Routers routers)
			throws IOException, ClassNotFoundException {
		bload(bais);
		if (routers != null)
			routers.setRouters(this);
	}

	/**
	 * Save the current state to a file.
	 *
	 * @param  filename output file to get the state, will place on the
	 *             classpath
	 */
	void saveState( String filename ) {
		File file = null;
		try {
			file = Utils.getFileAsResource(filename, this);
			if (file != null)
				{ if (trace.getDebugCode("mt")) trace.out("mt", "saving state to " + file.getAbsolutePath()); }
			else
				return;
			OutputStream binOutStrm =
				new BufferedOutputStream(new FileOutputStream(file));
			bsave(binOutStrm);
			binOutStrm.close();
		} catch (Exception e) {
			if (trace.getDebugCode("mt")) trace.out("mt", "Error saving binary state: " + e);
			try {
				file.delete();       // try to delete in case incompletely saved
			} catch (Exception ne) {}
		}
	}

	/**
	 * Save state ({@link Rete#bsave(java.io.OutputStream)} to memory.
	 * Enters reference to this image in {@link #saveStates}.
	 * 
	 * @param dirName prefix of key name in {@link #saveStates}
	 * @param fileName suffix of key name in {@link #saveStates}
	 */
  	void saveState( String dirName, String fileName ) {
  	    saveState(dirName, fileName, saveStates);
  	}
  	
  	/**
  	 * Save the Rete to a binary array, named in a hash table meant to
  	 * resemble a file system.
  	 * @param  dirName prefix of state name
  	 * @param  fileName suffix of state name
  	 * @param  statesMap map with key dirName+fileName, value byte array
  	 */
  	void saveState( String dirName, String fileName, Map statesMap ) {
 		try
 		{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();

			bsave( stream );
			byte[] buffer = stream.toByteArray();
			statesMap.put( dirName+fileName, buffer );
			if (trace.getDebugCode("mt")) trace.out("mt", "saved state to " + dirName+fileName +
					"; size="+buffer.length);
		}
		catch( IOException exception )
		{
			exception.printStackTrace();
		}
	}
	

	/**
	 * Read state ({@link Rete#bload(java.io.InputStream)} from memory.
	 * Finds reference to this image in {@link #saveStates}.
	 * 
	 * @param dirName prefix of key name in {@link #saveStates}
	 * @param fileName suffix of key name in {@link #saveStates}
	 */
	void loadState( String dirName, String fileName ) {
	    loadState(dirName, fileName, saveStates);
	}
	
  	/**
  	 * Load the Rete from a named binary array retreived from a hash table
  	 * meant to resemble a file system.
  	 * @param  dirName prefix of state name
  	 * @param  fileName suffix of state name
  	 * @param  statesMap map with key dirName+fileName, value byte array
  	 */
	void loadState( String dirName, String fileName, Map statesMap )
	{
 		try
 		{
			bload( new ByteArrayInputStream( (byte[])(statesMap.get( dirName+fileName )) ) );
			if (trace.getDebugCode("mt")) trace.out("mt", "loaded state from " + dirName+fileName);
		}
		catch( Exception exception )
		{
			exception.printStackTrace();
		}
	}

	/**
	 * Process the {@link #interfaceTemplatesList}. This will execute the
	 * deftemplates that were sent in InterfaceDescription messages: these
	 * templaes define types for the user interface elements (widgets) in
	 * working memory. Each entry is a single deftemplate command.
	 * @param interfaceTemplatesList list of commands to execute
	 * @return number of commands executed
	 */
	private int loadInterfaceTemplates(List interfaceTemplatesList) {
		

    
	    int count = 0;

	    if (interfaceTemplatesList == null) {
	        trace.err("MT.loadInterfaceTemplates(): interfaceTemplatesList is null");
	        return 0;
	    }
	    for (Iterator it = interfaceTemplatesList.iterator(); it.hasNext(); ++count) {
	        String deftemplateCmd = (String) it.next();
	        try {
	            if (count < 1 && textOutput != null)
	                textOutput.append("\nLoading deftemplates from interface definitions.");
	            Value val = eval(deftemplateCmd);
	            if (trace.getDebugCode("mt")) trace.out("mt", "rete "+this.hashCode()+" deftemplate, result "+val+", type "+
	                    RU.getTypeName(val.type())+":\n"+deftemplateCmd);					
	        } catch (JessException e1) {
	            String errMsg = "Error executing deftemplate command "+(count+1)+
	            ":\n  "+deftemplateCmd+":\n  "+e1+
	            (e1.getCause() == null ? "" : ";\n  "+e1.getCause().toString());
	            trace.err(errMsg);
	            e1.printStackTrace();
	            textOutput.append("\n"+errMsg+"\n");
	        }
	    }
	    return count;
	}

	/**
	 * Load the given deftemplates, facts and rules files. Any null file
	 * argument will be skipped.
	 *
	 * @param  bloadName name of a binary load Name to use
	 * @param  templatesName name of a deftemplates file to use
	 * @param  rulesName name of rules file to load; will call
	 * 	       {@link #parse(Reader, boolean) parse(rdr, true)} to remove
	 *         buggy rules after parse
	 * @param  factsName name of working memory instances file to load
	 * @return array showing which files were read
	 * @exception JessException on bload() or parse()
	 */
	boolean[] loadJessFiles(String bloadName, String templatesName, String rulesName,
	         String problemFactsName, List interfaceTemplatesList) 
	throws JessException {
		
        resetLoadInterfacetemplatesFailed();
        HintFact.setHintFact(false, this);         // initialize before reading rules   
        
	    String[] filenames = {bloadName, templatesName,	rulesName, problemFactsName};
        final int templateFileIndex = 1;
	    final int rulesFileIndex = 2;
	    final int factsFileIndex = 3;  // 

	    Object[] files = findFiles(filenames);

	    File bloadFile = null;
	    boolean[] results = new boolean[files.length];  // init'zes all false
	    boolean doBload = false;

	    if (files[0] instanceof File && ((File) files[0]).exists()) {
	        bloadFile = (File) files[0];
	        long bloadTime = bloadFile.lastModified();
	        doBload = true;
	        for (int i = 1; doBload && i < files.length; i++) {
	            if (!(files[i] instanceof File))
	                doBload = false;               // breaks loop
	            else {
	                File fi = (File) files[i];
	                if (!fi.exists() || bloadTime <= fi.lastModified())
	                    doBload = false;
	                if (trace.getDebugCode("mt")) trace.out("mt", "doBload " + doBload + ", files[" + i +
	                        "] " + fi + ", exists " + fi.exists());
	            }
	        }
	    }
	    if (doBload) {
	        try {
	            BufferedInputStream in =
	                new BufferedInputStream(new FileInputStream(bloadFile));
	            String fileLoadMsg = "\nLoading saved start state from binary file " +
	            bloadFile.getAbsolutePath();
	            if (trace.getDebugCode("mt")) trace.out("mt", fileLoadMsg); 	    				
	            textOutput.append(fileLoadMsg);
	            bload(in);
	            in.close();
	            results[0] = true;
	            textOutput.append("\n");
	            return results;
	        } catch (Exception e) {
	            trace.err("Error trying to load file " +
	                    bloadFile.getAbsolutePath() + ": " + e);
	            clear();  // clean partially-loaded state, go on to parse files
	        }
	    }
	    if (bloadFile != null && bloadFile.exists())  // remove obsolete cache
	        bloadFile.delete();

        CTAT_Controller ctlr = (getMT() != null ? getMT().getController() : null);
        boolean popupError =
        		(ctlr == null
        		|| (!Utils.isRuntime()
        				&& (ctlr.getMissController() instanceof MissController
        						&& !ctlr.getMissController().isPLEon())));
        if(trace.getDebugCode("mt"))
        	trace.outNT("mt", "MTRete.loadJessFiles() popupError "+popupError+": ctlr "+ctlr+", Utils.isRuntime() "+Utils.isRuntime()+
        			", ctlr.getMissController() "+(ctlr == null ? null : ctlr.getMissController()));

        String errorMessage = null;
	    for (int i = 1; i < files.length; i++) {
	        Reader rdr = null;
	        String resource = null;
                // trace.out("miss", "loadJessFiles: globalContext<1> = " + this.getGlobalContext());
	        // trace.out("miss", "@@@@@ i = " + i + ": parse(" + files[i] + ", " + (i == rulesFileIndex) + ", true)");
	        //trace.out("eep","getMT() not null:"+(getMT()!=null));

	        results[i] = parse(files[i], (i == rulesFileIndex), popupError, (i == factsFileIndex));

	        // trace.out("miss", "&&&&& MTRete.hashCode() = " + this.hashCode());
	        // trace.out("miss", "@@@@@ results[" + i + "]  = " + results[i]);
	        // trace.out("miss", "loadJessFiles: globalContext<2> = " + this.getGlobalContext());
//	        Value v = batch(resource);
	        if(!results[i]) {
				// if bad templates, use list from interface
				if (i < rulesFileIndex) {
					if (useInterfaceTemplates) {
						loadInterfaceTemplates(interfaceTemplatesList);
					} else {
						setLoadInterfacetemplatesFailed(true);
					}
					if (findDeftemplate("problem") == null) {
						String deftemplateStr = "(deftemplate problem (slot name) (multislot interface-elements) (multislot subgoals) (slot done) (slot description))";
						eval(deftemplateStr);
					}
				}
			}

	    }
	    textOutput.append("\n");
	    return results;
	}

        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        // There static fields are necessary for SimSt.gatherActivationList() 
        // BR_Controller.checkProductionRulesChainNew(), which 
        // eventually calls loadJessFiles(), occasionally cause Jess error 
        // [Cannot redefine deftemplate] when parse(files[1], ...) failed.
        // This failure apparently happens some sort of failure in synchronization
        // To my best knowledge, only overcome this problem is to have 
        // gatherActivationList() retry checkProductionRulesChainNew()
        // 
        private static boolean useInterfaceTemplates = true;
        public static void setUseInterfaceTemplates(boolean b) {
            MTRete.useInterfaceTemplates = b;
        }
        public static boolean getUseInterfaceTemplates() {
            return MTRete.useInterfaceTemplates;
        }
        
        private static boolean loadInterfacetemplatesFailed = false;
        private void setLoadInterfacetemplatesFailed(boolean b) {
            MTRete.loadInterfacetemplatesFailed = b;
        }
        private void resetLoadInterfacetemplatesFailed() {
            MTRete.loadInterfacetemplatesFailed = false;
        }
        public static boolean loadInterfacetemplatesFailed() {
            return MTRete.loadInterfacetemplatesFailed;
        }
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /**
     * Turn on or off access to {@link ProblemSummary} information.
     * @param enable if true, access is enabled
     * @return previous value
     */
    public boolean useProblemSummary(Boolean enable) {
    	boolean result = getUseProblemSummary();
    	psAccess = enable;
    	return result;
    }

    /**
     * @return true if {@link #psAccess} is not null and equal to {@link Boolean#TRUE}
     */
    public boolean getUseProblemSummary() {
    	return (psAccess != null && psAccess.booleanValue());
    }

	/**
	 * Find the given list of filenames on the classpath.  For each
	 * filename, tries to resolve as first as File, then as URL.
	 *
	 * @param  filenames filenames (Strings) to try to find
	 * @return array of File or URL entries with result for each file
	 *             requested; null entries for files not found
	 */
	private Object[] findFiles(String[] filenames) {
            
		Object[] results = new Object[filenames.length];

		for (int i = 0; i < filenames.length; i++) {
                    
			File f = null;
			if (filenames[i] != null && filenames[i].length() > 0)
				f = new File(filenames[i]);
			if (f != null && /* f.isAbsolute() && */ f.exists()) {
				results[i] = f;
				if (trace.getDebugCode("mt"))
					trace.out("mt", "filenames["+i+"]="+filenames[i]+" is absolute file "+f.getAbsolutePath());
				continue;
			}
			URL url = tryToReadURL(filenames[i]);
			if(url != null) {
				results[i] = url;				
				if (trace.getDebugCode("mt"))
					trace.out("mt", "filenames["+i+"]="+filenames[i]+": URL "+url);
				continue;
			}
			f = Utils.getFileAsResource(filenames[i], this);  // read as file relative to classpath
			if(f != null && f.exists()) {
				results[i] = f;
				if (trace.getDebugCode("mt"))
					trace.out("mt", "filenames["+i+"]="+filenames[i]+" is relative: absolute is "+f.getAbsolutePath());
				continue;
			}
			url = tryToReadURL(Utils.getURL(filenames[i], this));  // read from jar file?
			if(url != null) {
				results[i] = url;				
				if (trace.getDebugCode("mt"))
					trace.out("mt", "filenames["+i+"]="+filenames[i]+": classpath URL "+url);
				continue;
			}
		}
		return results;
  	}

	/**
	 * Test whether a pathname can be read as a URL.
	 * @param path name to convert to URL
	 * @return readable URL, if successful; else null
	 */
	private URL tryToReadURL(String path) {
		try {
			return tryToReadURL(new URL(path));
		} catch(MalformedURLException mue) {
			if(trace.getDebugCode("mt"))
				trace.outNT("mt", "MTRete.tryToRead() error converting \""+path+"\" to URL: "+mue);
			return null;
		}
  	}

	/**
	 * Test whether a URL is readable.
	 * @param url URL to test
	 * @return readable URL, if successful; else null
	 */
	private URL tryToReadURL(URL url) {
		String err = "";
		if(url != null) {
			try {
				InputStream is = url.openStream();
				is.close();
				return url;
			} catch(Exception e) {
				err = e.toString();
			}
		}
		if(trace.getDebugCode("mt"))
			trace.outNT("mt", "MTRete.tryToReadURL() error opening \""+url+"\" as URL: "+err);
		return null;
	}

	/**
	 * Parse an input file. Equivalent to
	 * {@link #parse(Reader, boolean) parse(rdr, false)}.
	 *
	 * @param  rdr Reader opened on file to parse; 
	 * @return result of the last parsed entity
	 * @exception JessException from {@link Jesp.parse(boolean,Context)}
	 */
	public Value parse(Reader rdr) throws JessException {
		return parse(rdr, false);
	}
	
	/**
	 * Parse the contents of a file or URL into Jess.
	 * @param file File or URL with input
	 * @param fullPath if not null, return the full pathname or URL
	 * @param popupError whether to popup an error dialog on error 
	 * @param fileNotFoundOk if true, don't show an error if the file isn't found
	 * @return true if read the file; false on error or no file found
	 */
	private Reader openJessFile(Object file, String[] fullPath, boolean popupError, boolean fileNotFoundOk) {
		Reader rdr = null;   // result
	    //trace.out("miss",file + "   "  + (file instanceof File));
	    try {
	        if (file instanceof File) {
	        	//trace.out("miss", "file instanceof File: " + file);
	            File f = (File) file;
	            if(fullPath != null)
	            	fullPath[0] = f.getCanonicalPath();
	            rdr = new FileReader(f);
	        } else if (file instanceof URL) {
	        	//trace.out("miss", "file instanceof URL: " + file);
	            URL url = (URL) file;
	            if(fullPath != null)
	            	fullPath[0] = url.toString();
	            rdr = new InputStreamReader(url.openStream());
	        } else
	            return null;
	    } catch (Exception e) {
	        String errorMessage = "Error reading file "+fullPath+":\n  "+e;
	        if (trace.getDebugCode("mt"))
	        	trace.errStack(errorMessage, e);
	        else
	        	trace.err(errorMessage);
	        if (fileNotFoundOk && e instanceof FileNotFoundException)
		        return null;
	        textOutput.append("\n"+errorMessage);
	        /*
    		boolean simStRunningIL = false;
                if (SimStInteractiveLearning.simSt != null) {
                    simStRunningIL = true;
                }
	         */
	        if(popupError)
	        	Utils.showExceptionOccuredDialog(e, errorMessage, "Jess File Evaluation Error");
	        getEventLogger().log(true, AuthorActionLog.BEHAVIOR_RECORDER, 
	                "JESS_FILE_EVALUATION_ERROR", errorMessage, "", "");
	        return null;
	    }
	    return rdr;
	}
	
	/**
	 * Parse the contents of a file or URL into Jess.
	 * @param file File or URL with input
	 * @param removeBuggyRules argument to {@link #parse(Reader, boolean)}
	 * @param popupError whether to popup an error dialog on error
	 * @return true if read the file; false on error or no file found
	 */
	private boolean parse(Object file, boolean removeBuggyRules, boolean popupError) {
		return parse(file, removeBuggyRules, popupError, false);
	}
	
	/**
	 * Parse the contents of a file or URL into Jess.
	 * @param file File or URL with input
	 * @param removeBuggyRules argument to {@link #parse(Reader, boolean)}
	 * @param popupError whether to popup an error dialog on error
	 * @param fileNotFoundOk if true, don't show an error if the file isn't found
	 * @return true if read the file; false on error or no file found
	 */
	private boolean parse(Object file, boolean removeBuggyRules, boolean popupError,
			boolean fileNotFoundOk) {
		if(file == null)
			return false;
		String[] fullPath = new String[1];
	    Reader rdr = openJessFile(file, fullPath, popupError, fileNotFoundOk);
	    if(rdr == null)
	    	return false;
	    return parse(rdr, fullPath[0], removeBuggyRules, popupError);
	}

	/**
	 * Parse an input file. Calls {@link #parse(Reader, boolean)}. Presumes that input
	 * stream is from a file, not a console. Displays results on JessConsole and in
	 * popup error msg.
	 *
	 * @param  rdr Reader opened on file to parse
	 * @param  fullPath filename for diagnostic msgs 
	 * @param  removeBuggyRules if true, will remove buggy rules after the parse;
	 *         see {@link #extractBuggyRules()}
	 * @return true if parse successful
	 * @exception JessException from {@link Jesp.parse(boolean,Context)}
	 */
	private boolean parse(Reader rdr, String fullPath, boolean removeBuggyRules,
	        boolean popupError) {
	    try {
	        String fileParseMsg = "\nReading "+Utils.getBaseName(fullPath, false)+
	        " ("+fullPath+")"; 
	        if (trace.getDebugCode("mt")) trace.out("mt", fileParseMsg);
	        textOutput.append(fileParseMsg);
	        Value lastV = parse(rdr, removeBuggyRules);
	        //trace.out("miss", "File: " + fullPath + "returning value: " + lastV);
	        return true;
	    } catch (JessException je) {
	    	je.printStackTrace();
	        String errorMessage = "Error parsing file "+fullPath+" at line "+
	        je.getLineNumber()+":\n"+
	        (je.getDetail() == null ? "" : je.getDetail()+". ")+
	        (je.getData() == null ? "" : je.getData());
	        if (trace.getDebugCode("mt")) trace.out("mt", errorMessage);
	        textOutput.append("\nError parsing file "+fullPath+":\n  "+je+"\n");
	        if (popupError)
	        	Utils.showExceptionOccuredDialog(je, errorMessage, "Jess File Evaluation Error");
	        getEventLogger().log(true, AuthorActionLog.BEHAVIOR_RECORDER, 
	                "JESS_FILE_EVALUATION_ERROR", errorMessage, "", "");
	        return false;
	    } catch (Exception e) {
	        String errorMessage = "Error parsing file "+fullPath+":\n  "+e;
	        if (trace.getDebugCode("mt")) trace.out("mt", errorMessage);
	        textOutput.append("\n"+errorMessage);
	        if (popupError)
	        	Utils.showExceptionOccuredDialog(e, errorMessage, "Jess File Evaluation Error");
	        getEventLogger().log(true, AuthorActionLog.BEHAVIOR_RECORDER, 
	                "JESS_FILE_EVALUATION_ERROR", errorMessage, "", "");
	        return false;
	    } finally {
	        try {
	            if (rdr != null)
	                rdr.close();
	        } catch (Exception e) {}
	    }
	}
	
	/**
	 * Parse an input file. Uses {@link jess.Jesp}. Presumes that input
	 * stream is from a file, not a console.
	 *
	 * @param  rdr Reader opened on file to parse
	 * @param  removeBuggyRules if true, will remove buggy rules after the parse;
	 *         see {@link #extractBuggyRules()}
	 * @return result of the last parsed entity
	 * @exception JessException from {@link Jesp.parse(boolean,Context)}
	 */
	public Value parse(Reader rdr, boolean removeBuggyRules) throws JessException {

	    UID uid = new UID();                         // for unique router name
	    if (!(rdr instanceof BufferedReader || rdr instanceof StringReader))
	        rdr = new BufferedReader(rdr);
//	    addInputRouter(uid.toString(), rdr, false);  // false=>not consoleLike
	    Jesp jesp = new Jesp(rdr, this);

            // trace.out("miss", "@@@@@ calling jesp.parse(false" + this.getGlobalContext() + ")");
            Value result = jesp.parse(false, this.getGlobalContext());
	    // removeInputRouter(uid.toString());
	    if (removeBuggyRules)
	        unloadBuggyRules();
	    // trace.out("miss", "@@@@@ parse result = " + result);
	    return result;
	}

    /**
     * Sun Oct 09 23:01:05 2005 :: Noboru
     * Simply reload "productionRules.pr" Needed for Sim. St. to
     * update a model
     *
     **/
	boolean reloadProductionRulesFile(String filename, boolean popupError) {
    	if (getFacts().size() < 2)
    		return false;                      // FIXME why this exclusion?  

    	String[] productionRulesFile = { filename };
    	Object[] file = findFiles(productionRulesFile);

    	if (file[0] != null) {
    		boolean result = parse(file[0], true, popupError);
    		deleteBload();
        	if (textOutput != null)
        		textOutput.append("\n");
    		return result;
    	} else {
    		String errorMessage = "Could not read production rules file \""+filename+"\"";
    		textOutput.append("\n" + errorMessage + "\n");
    		if (popupError)
	        	Utils.showExceptionOccuredDialog(null, errorMessage, "Load Production Rules file Error");
    		return false;
    	}
    }

    /**
	 * Describe <code>reloadProductionRulesFile</code> method here.
	 * 
	 * @param fileName
	 *            a <code>String</code> value
	 */
    public boolean reloadProductionRulesFile(File prFile, boolean popupError) {
    	boolean result = parse(prFile, false, popupError);
    	deleteBload();
    	if (textOutput != null)
    		textOutput.append("\n");
    	return result;
    }
    
    /**
     * Delete the .bload file. Tries to delete
     * {@link MT#findCognitiveModelDirectory()}/{MT#getProblemName()}+".bload".
     * No-op if {@link #mt} is null.
     */
    void deleteBload() {
    	if (mt == null)
    		return;
    	try {
    		File bf = new File(mt.findCognitiveModelDirectory()+mt.getProblemName()+".bload");
    		if (bf.exists())
    			bf.delete();
    	} catch (Exception e) {
    		trace.err("error deleting bload file on reload production rules: "+e);
    	}
    }

	/**
	 * Return an Iterator over all rules, including buggy rules not currently loaded.
	 * @return Iterator with element type Defrule
	 */
	public Iterator<Defrule> allRulesIterator() {
	    return allRulesMap().values().iterator();
	}

	/**
	 * Return a Map of all rules, including buggy rules not currently loaded.
	 * @return Map with key (String) rule name, value Defrule
	 */
	public Map<String, Defrule> allRulesMap() {
	    Map<String, Defrule> result = new HashMap<String, Defrule>();
	    for (Iterator it = listDefrules(); it.hasNext(); ) {
	        Object obj = it.next();
	        if (obj instanceof Defrule) {
	            Defrule rule = (Defrule) obj;
	            result.put(rule.getName(), rule);
	        }
	    }
//	    for (Iterator it = buggyRules.keySet().iterator(); it.hasNext(); ) {
//	        String ruleName = (String) it.next();
//	        if (result.get(ruleName) == null)
//	            result.put(ruleName, buggyRules.get(ruleName));
//	    }
	    return result;
	}
	
	/**
	 * Load the rules from {@link #buggyRules} into the Rete.
	 * @param  ta TextArea to write errors; ignored if null
	 * @return number of rules loaded
	 * @exception throws last Exception detected
	 */
	int loadBuggyRules(TextOutput ta) throws Exception {
		int count = 0;
//		long startTime = (new Date()).getTime();
//		Exception lastExc = null;
//		Iterator it = buggyRules.values().iterator();
//		for(count = 0; it.hasNext(); ++count){
//			try {
//			    Defrule rule = (Defrule)it.next();
//				dumpAgenda("before loading buggy rule " + rule.getName());
//				addDefrule(rule);
//				dumpAgenda("after loading buggy rule " + rule.getName());
//			} catch (JessException e) {
//				if (ta != null)
//					ta.append("\n" + e.toString());
//				e.printStackTrace();
//				lastExc = e;
//			}
//		}
//		trace.out("mtt", "time(loadBuggyRules) = " +
//				   ((new Date()).getTime() - startTime) + " ms");
//		if (lastExc != null)
//			throw lastExc;
        return count;
	}

	/**
	 * Tell whether the given rule is a correct rule, that is,
	 * to an ordinary rule that models correct problem-solving behavior.
	 * @param  rule to test
	 * @return result of {@link #isCorrectRuleName(String)} on rule name;
	 *             returns false if rule is null
	 */
	static boolean isCorrectRule(Defrule rule) {
	    if (rule == null)
	        return false;
		return isCorrectRuleName(rule.getName());
	}
	
	/**
	 * Tell whether the given rule name belongs to a correct rule, that is,
	 * to an ordinary rule that models correct problem-solving behavior.
	 * @param  ruleName value returned by {@link Defrule#getName()}
	 * @return true if ruleName matches neither {@link #isFireableBuggyRuleName(String)} nor
	 *             {@link #isBuggyRuleName(String)}; returns false if ruleName is null
	 */
	static boolean isCorrectRuleName(String ruleName) {
	    if (ruleName == null)
	        return false;
	    return (!isFireableBuggyRuleName(ruleName) && !isBuggyRuleName(ruleName));
	}

	/**
	 * Tell whether the given rule is a buggy rule.
	 * @param  rule to test
	 * @return result of {@link #isBuggyRuleName(String)} on rule name;
	 *             returns false if rule is null
	 */
	static boolean isBuggyRule(Defrule rule) {
	    if (rule == null)
	        return false;
		return isBuggyRuleName(rule.getName());
	}
	
	/**
	 * Tell whether the given rule name belongs to a buggy rule.
	 * @param  ruleName value returned by {@link Defrule#getName()}
	 * @return true if ruleName matches the Pattern {@link #buggyPrefix}
	 *             returns false if ruleName is null
	 */
	static boolean isBuggyRuleName(String ruleName) {
	    if (ruleName == null)
	        return false;
	    Matcher m = buggyPrefix.matcher(ruleName);
	    return m.matches();
	}

	/**
	 * Tell whether the given rule is a fireable-buggy rule.
	 * @param  rule to test
	 * @return result of {@link #isFireableBuggyRuleName(String)} on rule name;
	 *             returns false if rule is null
	 */
	static boolean isFireableBuggyRule(Defrule rule) {
	    if (rule == null)
	        return false;
		return isFireableBuggyRuleName(rule.getName());
	}
	
	/**
	 * Tell whether the given rule name belongs to a fireable-buggy rule.
	 * @param  ruleName value returned by {@link Defrule#getName()}
	 * @return true if ruleName matches the Pattern {@link #fireableBuggyPrefix} 
	 *             returns false if ruleName is null
	 */
	static boolean isFireableBuggyRuleName(String ruleName) {
	    if (ruleName == null)
	        return false;
	    Matcher m = fireableBuggyPrefix.matcher(ruleName);
	    return m.matches();
	}
	
	/**
	 * Now a no-op. <i>Former description</i>:
	 * Extract the buggy rules from the Rete. Walks the list given by
	 * {@link jess.Rete#listDefrules()}: for each rule whose name matches the
	 * pattern {@link #buggyPrefix}, adds rule to map {@link #buggyRules} & calls
	 * {@link jess.Rete#unDefrule(java.lang.String)}. 
	 */
	void unloadBuggyRules(){
		if (trace.getDebugCode("mt")) trace.out("mt", "unloadBuggyRules(): singlePassTrace "+useSinglePassTrace());
	    if (useSinglePassTrace())
	        return;
//		for (Iterator it = listDefrules(); it.hasNext(); ) {
//			Object obj = it.next();
//			if(obj instanceof jess.Defrule){
//				Defrule rule = (Defrule)obj;
//				String ruleName = rule.getName();
//				if (isBuggyRuleName(ruleName))
//					buggyRules.put(ruleName, rule); // replaces rule already in set
//			}
//		}
//		for (Iterator it = buggyRules.keySet().iterator(); it.hasNext(); ) {
//			try {
//			    String ruleName = (String) it.next();
//			    trace.out("mt", "removing rule " + ruleName + ";");
//				unDefrule(ruleName);
//			} catch (JessException e) {
//				e.printStackTrace();
//			}
//		}
//		StringBuffer sb = new StringBuffer("remaining rules:");
//		for (Iterator traceIt = listDefrules(); traceIt.hasNext(); ) {
//		    Object next = traceIt.next();
//		    if (next instanceof HasLHS)
//		        sb.append(" ").append(((HasLHS) next).getName());
//		    else
//		        sb.append(" ").append(next.getClass().getName());
//		}
//		trace.out("mt", sb.toString());
	}

	/**
	 * Tell whether the model tracer should use a single- or double-pass
	 * algorithm. Used to be dependent on value of System property {@value #SINGLE_PASS_PREFERENCE}.  See 
	 * {@link JessModelTracing#modelTrace(String, String, String, Vector)}.
	 * @return true -- now a constant function
	 */
	public boolean useSinglePassTrace() {
		return true;
//	    Boolean result =
//	        controller.getPreferencesModel().getBooleanValue(SINGLE_PASS_PREFERENCE);		
//	    if (result== null)
//	        return false;
//	    else
//	        return result.booleanValue(); 
	}
//	
//	public Activation cloneActivation(Activation a){
//		Activation aClone = new Activation(a.getToken(),a.getRule());
//		return aClone;
//	}
	
	/**
	 * Method addFacts.
	 * This method add the facts from the list to the rete engine without removing the existing 
	 * @param facts
	 */
//	public void addFacts(ArrayList facts){
//		ArrayList cloneList = new ArrayList();
//		this.cloneFactsList(facts, cloneList);
//		int size = cloneList.size();
//		for(int i = 0; i < size; i++){
//			try {
//				this.assertFact((Fact)cloneList.get(i));
//			} catch (JessException e) {
//				e.printStackTrace();
//			}
//		}

//		int size = facts.size();
//		Map map = new HashMap();
//		for(int i = 0; i < size; i++){
//			try {
////				this.assertFact((Fact)((Fact)facts.get(i)).clone());
//				this.assertFact(this.cloneFact((Fact)facts.get(i),map));
//			} catch (JessException e) {
//				e.printStackTrace();
//			}finally{
//				map.clear();
//				map = null;
//			}
//		}
//	}
	
	/**
	 * Method addDeftemplates.
	 * This method adds the deftemplates from the list to the rete engine
	 * @param deftemplates
	 */
	public void addDeftemplates (ArrayList deftemplates) throws JessException {
		int size = deftemplates.size();
		for(int i = 0; i < size; i++){
			addDeftemplate((Deftemplate) deftemplates.get(i));
		}
	}
	/**
	 * @return Returns the reteChangeCount.
	 */
	int getReteChangeCount() {
		return reteChangeCount;
	}
	
	/**
	 * Override to clear {@link #reteChangeCount} and
	 * {@link MTRete#rulesFiredCount} before running rules.
	 * @param maxRules maximum number of rules to execute
	 * @return result of superclass method 
	 * @see {@link Rete.run(int)}
	 */
	public int run(int maxRules) throws JessException {
		reteChangeCount = 0;
		rulesFiredCount = 0;
		try {
			return super.run(maxRules);
		} catch (HaltReteException hre) {
			if (trace.getDebugCode("mt")) trace.out("mt", hre.toString());
			return rulesFiredCount;
		}
	}

	/**
	 * Halt the Rete. Throws a {@link JessException} to stop Rete execution
	 * immediately.  N.B. {@link Rete#halt()} appears to only prevent the
	 * execution of further rules.
	 * @param routine name of calling routine
	 * @throws JessException to halt Rete
	 */
	void haltRete(String routine) throws JessException {
		throw new HaltReteException(routine, "change count "+getReteChangeCount());
//		try {
//			getRete().halt();  this doesn't work: see header comment
//		} catch (JessException je) {
//			trace.err("Exception halting Rete : "+je);
//			je.printStackTrace();
//			if (nodeNowFiring != null)
//				je.printStackTrace(nodeNowFiring.getErrorOutput().getWriter());		
//		}
	}
	
	/**
	 * Returns the deftemplateList.
	 * @return ArrayList
	 */
	public ArrayList getWMEEditorDeftemplateList() {
		return deftemplateList;
	}

	/**
	 * Returns the factsList.
	 * @return ArrayList
	 */
	public ArrayList getWMEEditorFactsList() {
		return factsList;
	}

	/**
	 * Sets the deftemplateList.
	 * @param deftemplateList The deftemplateList to set
	 */
	public void setWMEEditorDeftemplateList(ArrayList deftemplateList) {
		this.deftemplateList = deftemplateList;
	}
	/**
	 * @param dt
	 * @return
	 */
	public String getDeftemplateString(Deftemplate dt) {
		String deftemplateString = "\n(deftemplate";
		deftemplateString += " " + dt.getBaseName() + " ";
		if(dt.getParent() != null && dt.getParent().getBaseName().charAt(0) != '_'){
			deftemplateString += "extends " + dt.getParent().getBaseName() + " ";
		}
		for(int i = 0; i < dt.getNSlots(); i++){
			
			try {
				String typeStr =  RU.getTypeName(dt.getSlotType(i));
				String dataType;
				typeStr = typeStr.toLowerCase();
				deftemplateString += " (" + typeStr + " " + dt.getSlotName(i);
				if(!typeStr.equalsIgnoreCase("multislot")){
					deftemplateString += " (default " + dt.getSlotDefault(i) + ")";
					dataType = RU.getTypeName(dt.getSlotDataType(i));
					if(dataType != null && !dataType.equalsIgnoreCase("null")){
						deftemplateString += " (type " + dataType + "))";
					}else{
						deftemplateString += ")";
					}
				}else{
					deftemplateString += ")";
				}
			} catch (JessException e) {
				e.printStackTrace();
				return "";
			}
		}
		deftemplateString += ")";
		return deftemplateString;
	}

	
	/**
	 * Method addWMEEditorFact. - adds a fact to the list of facts created by WME Editor
	 * @param fact
	 */
	public void addWMEEditorFact(Fact fact){
//		factsList.add(fact.clone());
		Map map = new HashMap();
		Fact f;
		Integer id;
		for(int i = 0; i < factsList.size(); i++){
			f = (Fact)factsList.get(i);
			id = new Integer(f.getFactId());
			map.put(id, f);
		}
		factsList.add(MTRete.cloneFact(fact, map));
		map.clear();
		map = null;
		f = null;
		id = null;
	}
	
	/**
	 * Method removeWMEEditorFact. - removes a fact from the list of facts created by the WME editor
	 * @param fact
	 */
	public void removeWMEEditorFact(Fact fact){
		factsList.remove(fact);
	}
	
	/**
	 * Method addWMEEditorDeftemplate. - adds a deftemplate to the list of deftemplates created by the WME editor
	 * @param dt
	 */
	public void addWMEEditorDeftemplate(Deftemplate dt){
		deftemplateList.add(dt);
	}
	
	/**
	 * Method removeWMEEditorDeftemplate. - removes a deftemplate from the list of deftemplates created by the WME editor
	 * @param dt
	 */
	public void removeWMEEditorDeftemplate(Deftemplate dt){
		deftemplateList.remove(dt);	
	}

	/**
	 * Tell whether a given s/a/i vector is in the Unspecified state.
	 * @param  sai List with selection, action, input, respectively; returns
	 *         false if sai list is null or incomplete 
	 * @return true if all values "unspecified"
	 */
	static boolean isSAIUnspecified(java.util.List sai) {
		if (sai == null || sai.size() < 3)
			return false;
		return isSAIUnspecified((String) sai.get(0), (String) sai.get(1),
				(String) sai.get(2));
	}
	

	/**
	 * Tell whether the given s/a/i values are in the Unspecified state.
	 * @param  selection
	 * @param  action
	 * @param  input
	 * @return true if all values "unspecified"
	 */
	static boolean isSAIUnspecified(String selection, String action, String input) {
		return (MTRete.NOT_SPECIFIED.equals(selection) &&
				MTRete.NOT_SPECIFIED.equals(action) &&
				MTRete.NOT_SPECIFIED.equals(input));
	}

	/* 
	 * 
	 */
	protected void aboutToFire(Activation act) {
		this.firedRuleList.add(act.getRule().getName());		
	}

	/**
	 * clears the list of firedRules
	 */
	public void clearFiredRuleList(){
		this.firedRuleList.removeAll(firedRuleList);
	}

	/**
	 * returns the listoffired rules
	 * @return ArrayList
	 */
	public ArrayList getFiredRuleList(){
		return this.firedRuleList;
	}

	/**
	 * this method clones a fact object and also updates the ids of the facts in its slots 
	 * @param fact
	 * @return the modified clone of the fact object
	 */
	static Fact cloneFact(Fact fact, Map map) {
		fact = (Fact)fact.clone();
		Deftemplate dt = fact.getDeftemplate();
		int nSlots = dt.getNSlots();  
		for (int j = 0; j < nSlots; j++) {
			Value v;
			try {
				v = fact.getSlotValue(dt.getSlotName(j));
				if (v.type() == RU.FACT) {
					Fact slotFact = v.factValue(null);
					Integer i2 = new Integer(slotFact.getFactId());
					if (map.get(i2) == null)
						map.put(i2, cloneFact(slotFact, map));
	
					fact.setSlotValue(dt.getSlotName(j), new FactIDValue((Fact)map.get(i2)));
				}
			} catch (JessException e) {
				e.printStackTrace();
			}
		}
//		if (map.get(i) == null)
//			map.put(i, fact);

		return fact;
	}
	
	public boolean compareTokens(Token t1, Token t2){
		if(t1.size() != t2.size()){
			return false;
		}
		boolean returnValue = true;
		for(int i = 0; i < t1.size(); i++){
			int factId1 = t1.fact(i).getFactId();
			int factId2 = t2.fact(i).getFactId();
			if(factId1 != factId2){
				returnValue = false;
				break;
			}
//			System.out.println("t1[i]: " + factId1 + " t2[i]" + factId2);
		}
		return returnValue;
	}
	
	/**
	 * Set-accessor.
	 * @param maxDepth new value for {@link #maxDepth}
	 */
	public void setMaxDepth(int maxDepth){
		PropertyChangeEvent pce = new PropertyChangeEvent(this, TREE_DEPTH, this.maxDepth, maxDepth);
		this.maxDepth = maxDepth;
		if(trace.getDebugCode("mt"))
			trace.out("mt", "MTRete.setMaxDepth() sending "+pce+" to jmt "+getJmt()+
					".tree "+(getJmt() == null ? null : getJmt().getRuleActivationTree())); 
		if(getJmt() != null && getJmt().getRuleActivationTree() != null)
			getJmt().getRuleActivationTree().propertyChange(pce);
	}
	
	/**
	 * Get-accessor.
	 * @return {@link #maxDepth}
	 */
	public int getMaxDepth(){
		return maxDepth;
	}
        
	/**
	 * @return
	 */
	public boolean isUseBackwardChaining() {
		return useBackwardChaining;
	}

	/**
	 * @param useBackwardChaining
	 */
	public void setUseBackwardChaining(boolean useBackwardChaining) {
		this.useBackwardChaining = useBackwardChaining;
	}

	/**
	 * Get the value of <i>assertStudentValuesFact</i> from
	 * {@link System#getProperty(String) System#getProperty("UseStudentValuesFact")} and call
	 * {@link #setGlobalSAI(String,String,String,boolean) setGlobalSAI(selection, action, input, assertStudentValuesFact)}.
	 * @param selection
	 * @param action
	 * @param input
	 */
	public void setGlobalSAI(String selection, String action, String input){
		jmt.setupForNewRequest(!JessModelTracing.isSAIToBeModelTraced(selection, action));
		Fact cff = GetCustomFieldsFact.clear(this);
		HintFact.setHintFact(JessModelTracing.isHintRequest(selection, action), this);
		setGlobalSAI(selection, action, input, getUseStudentValuesFact());
	}

	/**
	 * Define the defglobals for student input and the deftemplate for the
	 * student input fact, but create the fact only if the boolean argument
	 * is true.
	 *
	 * @param selection
	 * @param action
	 * @param input
	 * @param assertStudentValuesFact if true, will assert the student input
	 *            fact.
	 */
	private void setGlobalSAI(String selection, String action, String input,
					  Boolean assertStudentValuesFact){

//            new Exception("who is calling setGlobalSAI").printStackTrace();
//            trace.out("wmefacts" , "entered setGlobalSAI");
		try {
			// set the defglobal variables
			// sewall 2013-09-11 true args mean coerce symbols to strings 
			eval("(defglobal ?*sSelection* = " +
								edu.cmu.pact.jess.Utils.escapeString(selection, true) + ")");
			eval("(defglobal ?*sAction* = " +
								edu.cmu.pact.jess.Utils.escapeString(action, true) + ")");
			eval("(defglobal ?*sInput* = " +
								edu.cmu.pact.jess.Utils.escapeString(input, true) + ")");
			eval("(printout t crlf crlf "+
					"\"?*sSelection*: \" ?*sSelection* \", \""+
					"\"?*sAction*: \" ?*sAction* \", \""+
					"\"?*sInput*: \" ?*sInput* \";\" crlf)");
					
			
			if(trace.getDebugCode("mt"))
				trace.out("mt", "MTR.setGlobalSAI() assertStudentValuesFact "+assertStudentValuesFact);
				
			if (assertStudentValuesFact != null && !assertStudentValuesFact.booleanValue())
				return;
			Deftemplate svfTemplate = findDeftemplate("studentValues");
			if(svfTemplate == null) {
				if(assertStudentValuesFact == null)
					return;
				eval("(deftemplate studentValues (slot selection) (slot action) (slot input))");
				svfTemplate = findDeftemplate("studentValues");
			}
			if (studentValuesFact == null)
				studentValuesFact = getStudentValuesFact();  // Bo Chen for Ido Spring 2009
			if(studentValuesFact != null) {
				try {
					retract(studentValuesFact);
				} catch (Exception e) {
					trace.err("error retracting studentValues fact: "+e);
					e.printStackTrace();
				}
			}
			studentValuesFact = new Fact(svfTemplate);
			setSlotValue(studentValuesFact, "selection", selection);
			setSlotValue(studentValuesFact, "action", action);
			setSlotValue(studentValuesFact, "input", input);

			this.assertFact(studentValuesFact);

			if(trace.getDebugCode("mt"))
				trace.out("mt", "MTR.setGlobalSAI() asserted studentValuesFact "+studentValuesFact);

		} catch (JessException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Set the given value into the named slot in the given fact. 
	 * @param fact
	 * @param name slot name
	 * @param value new value for the slot; will use Jess type from
	 *        {@link edu.cmu.pact.jess.Utils#getJessType(String, Value[], boolean) WMEEditor.getJessType(name, value, true)}
	 * @throws JessException
	 */
	private void setSlotValue(Fact fact, String name, String value) 
			throws JessException {
		Value[] v = new Value[1];
		edu.cmu.pact.jess.Utils.getJessType(value, v, true);
		fact.setSlotValue(name, v[0]);
	}

	/**
	 * Retrieve the studentValues fact using the defquery
	 * <tt>(defquery get-studentValues "Retrieve the fact holding the student SAI." (studentValues))</tt>
	 * @return
	 */
	public Fact getStudentValuesFact() throws JessException {
		for(int trial = 0; trial < 2; trial++) {   // try up to 2x
			try {
				QueryResult qr = this.runQueryStar("get-studentValues", new ValueVector(0));
				if(!qr.next())
					return null;
				Value sv = qr.get("?sv");
				Fact result = sv.factValue(getGlobalContext());
				if(trace.getDebugCode("sv"))
					trace.out("sv", "query found student values fact "+result);
				return result;
			} catch(JessException je) {
				if(trial > 0) {
					trace.err("Error running defquery get-studentValues: "+je+"; cause "+je.getCause()+
						".\n  "+je.getProgramText()+
						"\n  "+je.getDetail());
					continue;
				}
			}
			synchronized(this) {
				HasLHS q = findDefrule("get-studentValues");
				if(!(q instanceof Defquery))
					eval("(defquery get-studentValues "+
							"\"Retrieve the fact holding the student SAI.\"" +
							"?sv <- (studentValues))");
			}
		}
		return null;
	}

	/**
	 * Find a {@link jess.Fact} whose name slot matches the given value.
	 * If multiple Facts have this name, will choose first one it comes to.
	 * @param  name value of the name slot
	 * @return Fact with that name; null if none 
	 */
	public Fact getFactByName(String name) {
		Fact fact;
		Deftemplate dt;
		if (name == null || name.length() < 1)
			return null;
		Iterator it = this.listFacts();
		
		int i=0;

		if (trace.getDebugCode("boots15")) trace.out("boots15", "searching for fact named " + name);

		while(it.hasNext()){
			if (trace.getDebugCode("boots15")) trace.out("boots15", "iterator loop: i = " + i);
			i++;

			fact = (Fact)it.next();
			dt = fact.getDeftemplate();
			if(dt == null)
				continue;
			int si = dt.getSlotIndex("name"); 
			if (si < 0)
				continue;
			try {
				Value nv = fact.getSlotValue("name");
				String nvs = nv.stringValue(getGlobalContext());

				if (trace.getDebugCode("boots15")) trace.out("boots15", "iterator loop:                nvs = " + nvs);

				if (name.equalsIgnoreCase(nvs)){
					if (trace.getDebugCode("boots15")) trace.out("boots15", "iterator loop: returning fact named " + nvs);
					return fact;
				}
			} catch (JessException je) {
				String errMsg = "Error getting fact by name \""+name+"\": "+je;
				trace.err(errMsg);
				return null;
			}
		}
		return null;
	}

        
        public boolean setSAIDirectly(ProblemEdge brdEdge) {
            Sai sai = brdEdge.getSai();
            String selection = sai.getS();
            String action = sai.getA();
            String input = sai.getI();
            if (trace.getDebugCode("gusmiss")) trace.out("gusmiss","calling setSaiDirectly with s="+selection+", a="+action+", i="+ input);

            boolean b = setSAIDirectly(selection, action, input);            
            return b;
        }
        
//        
//        public Fact factReader1(String selection){
//        
//            Fact f = getFactByName(selection);
//
////            try {
////                trace.out("gusmiss", "f.value = " + f.getSlotValue("value"));
////            }
////            catch (JessException je) {
////            }
//            return f;
//        }
//        
        
	/**
	 * Update an interface fact with the given selection, action and input.
	 * No-op if finds no fact with name slot matching the selection.
	 * @param selection value to match against facts' name slots
	 * @param action currently unused
	 * @param input value to set into the found fact's value slot
	 * @return true if found and modified fact successfully
	 */
	public boolean setSAIDirectly(String selection, String action, String input) {

	    if (trace.getDebugCode("missInput")) trace.out("missInput", "entered setSAIDirectly(" + selection + "," + action + "," + input + ")");
	 
            Fact f = getFactByName(selection);
         trace.out("modifying directly : " + selection + input);
	    if (f == null) {
	        if (trace.getDebugCode("missInput")) trace.out("missInput", "getFactByName(" + selection + ") returned null");
	        return false;
            }
            
            // See if the fact has "value" slot
            try {
                f.getSlotValue("value");
            } catch (JessException je) {
            	// If not, then return
            	return false;
            }
            
            try {
                //input="3x";////////////////////////////HACK
                Value iv=stringToValue(input);
                if (trace.getDebugCode("missInput")) trace.out("missInput", "iv = " + iv);
                if (trace.getDebugCode("missInput")) trace.out("missInput", "before, f.value = " + f.getSlotValue("value"));
                modify(f, "value", iv);
                if (trace.getDebugCode("missInput")) trace.out("missInput", "after, f.value = " + f.getSlotValue("value"));
                if (trace.getDebugCode("missInput")) trace.out("missInput", "f.hashCode() = " + f.hashCode());

                if (trace.getDebugCode("missInput")) trace.out("missInput", "Here is the list of facts");
                ArrayList factsCurrent = getFacts();
                for(int i=0; i< factsCurrent.size(); i++){
                	
                	if (trace.getDebugCode("missInput")) trace.out("missInput", "Fact value at " + i + "is" + factsCurrent.get(i));
                }
                	
//                showJessFacts();
                
//                trace.out("gusmiss", "returning true");
                return true;
            } catch (JessException je) {
	        String errMsg = "Error setting value slot in fact with name \""+
	        selection+"\": "+je;
	        trace.err(errMsg);
	        je.printStackTrace();
//                trace.out("gusmiss", "returning false");
	        return false;
	    }
	}
	/**
	 * Convert a string into a Jess {@link Value} object.
	 * @param s String to convert
	 * @return Value of proper type; type is RU.SYMBOL if s is null
	 * @throws JessException
	 */
	Value stringToValue(String s) throws JessException {
		return stringToValue(s, getGlobalContext());
	}
	
	/**
	 * Convert a string into a Jess {@link Value} object.
	 * @param s String to convert
	 * @param ctx context for {@link JessToken#valueOf(jess.Context)}
	 * @return Value of proper type;  if s is null,type is RU.SYMBOL and value is "nil"
	 * @throws JessException
	 */
	public static Value stringToValue(String s, Context ctx) throws JessException {
		Value iv = null;
		if(s==null)
			iv= new Value("nil",RU.SYMBOL);
		else
		{
			Value[] returnValue=new Value[1];
			edu.cmu.pact.jess.Utils.getJessType(s,returnValue);
			iv=returnValue[0]; 
		}
		return iv;
	}
		
		 
	/**
	 * This method returns true if the state corresponding to the dirName+fileName 
	 * has been created and stored in the hashmap
	 * @param fileName
	 * @return
	 */
	public boolean statePending(String dirName, String fileName) {
		if(saveStates.get( dirName+fileName ) != null){
			return true;
		}
		return false;
	}

	/**
	 * Saves the wmeTypes.
	 *
	 * @param  w stream to get Jess deftemplate commands
	 * @return true if write was successful
	 */
	public boolean logTemplates(File f) {
		Writer w;
		try {
			w = new FileWriter(f);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		/**
		 * Pattern to match slot definition lines in deftemplate pretty-print.
		 * Matches one slot definition on the line, where that definition includes a
		 * numeric type specifier. Why Jess can't print a symbolic type specifier I
		 * don't know.
		 */
		java.util.regex.Pattern slotTypePattern =
			java.util.regex.Pattern.compile("(\\p{Space}*\\(slot )(.*)(\\(type\\p{Space}+)([1-9][0-9]*)\\)(\\)+)\\p{Space}*$");
		BufferedWriter out = null;
		try {
			out = (w instanceof BufferedWriter ?
				   (BufferedWriter) w : new BufferedWriter(w));

			Iterator it = this.listDeftemplates();

			while (it.hasNext()) {
				Deftemplate dt = (Deftemplate) it.next();

				// omit templates used by Jess internally
				//
				if (dt.getBaseName().startsWith("_")
						|| dt.getBaseName().equalsIgnoreCase("initial-fact")) {
					continue;
				}

				StringBuffer sb = new StringBuffer();
				String template = (new PrettyPrinter(dt)).toString();
				StringTokenizer tkzr = new StringTokenizer(template, "\n");
				while (tkzr.hasMoreTokens()) {
					String line = tkzr.nextToken();
					Matcher m = slotTypePattern.matcher(line);
					if (m.matches()) {
						sb.replace(0, sb.length(), m.group(1));
						sb.append(m.group(2));
						int typeNum = -1;
						try {
							typeNum = Integer.parseInt(m.group(4));
							String type = RU.getTypeName(typeNum);
							if (type == null){
								trace.out(5, this, "bad slot type number "
										+ typeNum);
							}
							else {
								sb.append(m.group(3)); // "(type "
								sb.append(RU.getTypeName(typeNum));
								sb.append(')');
							}
						} catch (NumberFormatException nfe) {
							trace.out(5, this, "regex should prevent this "
									+ typeNum);
						}
						sb.append(m.group(5));
						line = sb.toString();
					}
					out.write(line);
					out.newLine();
				}
				if (dt.getBackwardChaining()) {
					out
							.write("(do-backward-chaining " + dt.getBaseName()
									+ ")");
					out.newLine();
				}
				out.flush();
			}
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Save working memory in a Jess- and human-readable form. Writes a series
	 * of Jess statements to a file which will assert the given facts.
	 * 
	 * @param factsIt iterator comprising the Facts to save; if null
	 *         gets iterator from {@link jess.Rete#listFacts()};
	 * @param w stream to save to
	 * @return true if successful; reasons for failure may include i/o errors,
	 *         Jess errors or the presence of slot values of type
	 *         EXTERNAL_ADDRESS, which are references to Java objects
	 */
	public boolean logFacts( File file) {

		Writer w;
		try {
			w = new FileWriter(file);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}

		Iterator factsIt = this.listFacts();
		PrintWriter out = null;
		try {
			out = (w instanceof PrintWriter ?
				   (PrintWriter) w : new PrintWriter( w ) );
			if (out == null)
				return false;

			Context context = this.getGlobalContext();

			class FactVar { // bundle a Fact and a variable name
				Fact f;
				String v;
				FactVar(Fact f, int i) {
					this.f = f;
					this.v = "?var" + i;
				}
			}
			Map map = new LinkedHashMap();   // keys fact-ids, values FactVars

			if (factsIt == null)        // default is all facts in Rete engine
				factsIt = this.listFacts();

			for (int i = 1; factsIt.hasNext(); i++) {
				Fact f = (Fact) factsIt.next();
				map.put(new Integer(f.getFactId()), new FactVar(f, i));
			}

			out.println(";;;; Fact assertions: slot assignments are below.");
			out.println("");
			for (Iterator it = map.values().iterator(); it.hasNext();) {
				FactVar fv = (FactVar) it.next();
				String nameAssgnmt = "";
				int n = fv.f.getDeftemplate().getSlotIndex("name");
				if (n >= 0) { // use name to make assertion unique
					Value nameVal = fv.f.getSlotValue("name");
					nameAssgnmt = " (name " + nameVal.resolveValue(context)
							+ ")";
				}
				out.println("(bind " + fv.v + " (assert(" + fv.f.getName()
						+ nameAssgnmt + ")))");
			}

			out.println(); // (modify ...) to set each slot
			out.println(";;;; Slot assignments");
			out.println();
			for (Iterator it = map.values().iterator(); it.hasNext();) {
				FactVar fv = (FactVar) it.next();
				Deftemplate dt = fv.f.getDeftemplate();
				int nSlots = dt.getNSlots();
				if (nSlots < 1) // no slots to assign=>no modify
					continue;
				out.println("; " + fv.f.getName());
				out.println("(modify " + fv.v);
				for (int i = 0; i < nSlots; i++) {
					String slotName = dt.getSlotName(i);
					Value val = fv.f.getSlotValue(slotName).resolveValue(
							context);
					out.print("    (" + slotName);
					switch (val.type()) {
					case RU.FACT :
						{
							Fact valFact = val.factValue(context);
							FactVar valFV = (FactVar) map.get(new Integer(
									valFact.getFactId()));
							out.print(" " + valFV.v);
						}
						break;
					case RU.LIST :
						{
							ValueVector vv = val.listValue(context);
							for (int j = 0; j < vv.size(); j++) {
								Value listVal = vv.get(j).resolveValue(context);
								if (listVal.type() == RU.FACT) {
									Fact valFact = listVal.factValue(context);
									FactVar valFV = (FactVar) map
											.get(new Integer(valFact
													.getFactId()));
									out.print(" " + valFV.v);
								} else {
									out.print(" " + listVal.toString());
								}
							}
						}
						break;
					case RU.EXTERNAL_ADDRESS :
						return false;
					default :
						out.print(" " + val.toString());
						break;
					}
					out.println(")"); // end slot
				}
				out.println(")"); // end modify
			}
			return true;
		} catch (JessException je) {
			je.printStackTrace();
			return false;
		} finally {
			out.close();
		}
	}

	/**
	 * Whether the Conflict Tree should include only new activations among the
	 * children of a chain node.  If false, all activations on the agenda will
	 * appear as children of each chain node.  If true, only new activations
	 * will appear as children.
	 * @return {@link prunePriorActivations}
	 */
	public boolean getPrunePriorActivations() {
		return prunePriorActivations;
	}

	/**
	 * Whether the Conflict Tree should include only new activations among the
	 * children of a chain node.  @see #getPrunePriorActivations()
	 * @param prunePriorActivations 
	 */
	public void setPrunePriorActivations(boolean prunePriorActivations) {
		this.prunePriorActivations = prunePriorActivations;
	}

	/**
	 * @return Returns the MT.
	 */
	public MT getMT() {
		return mt;
	}

	/**
	 * @param mt The MT to set.
	 */
	void setMT(MT mt) {
		this.mt = mt;
	}    
    
    /*Wrapper functions for making adding/removing functions from production rules easy*/
    
    /**
     * Add a @link ModelTracingUserfunction to be called on each element of the start state
     * if a function with the same underlying class already exists, do nothing
     * The function will be added at the end of the calling queue
     * @param function 
     * @return a boolean indicating whether the function was actually added to the list
     */
     public boolean addStartStateHookCall(ModelTracingUserfunction function)
     {
         return getMT().addStartStateHookCall(function);
     }
     /**
      * Remove a function with the same underlying class from the queue of functions
      * to be called on the start state
      * @param functio
      * @return a boolean indicating whether it was removed or not
      */
     boolean removeStartStateHook(ModelTracingUserfunction function)
     {
         return getMT().removeStartStateHook(function);
     }
    /**
     * Add a @link ModelTracingUserfunction to be called after every input
     * Functions are called in queue order
     * If another function of the same underlying class is already in the list
     * the function is not added
     * @param function 
     * @return a boolean indicating whether the function was actually added
     */
     public boolean addHookCall(ModelTracingUserfunction function)
     {
         return getJmt().addHookCall(function);
     }
     
     /**
      * Remove a @link ModelTracingUserfunction with the same underlying class in it is in the list of hook functions to call
      * @param function
      * @return a boolean indicating whether the function was removed or not
      */
     public boolean removeHookCall(ModelTracingUserfunction function)
     {
         return getJmt().removeHookCall(function);
     }
    
    /**
     * 
     * @param f a jess.Userfunction
     * @param list a List of Userfunctions
     * @return an int index or -1 if not found
     */
    public static int findUserfuction(Userfunction f, List list)
    {
        Iterator iter=list.listIterator();
        int pos=0;
        while(iter.hasNext())
        {
            Userfunction curFunction=(Userfunction)iter.next();
            if(curFunction.getClass().equals(f.getClass()))
                return pos;
                pos++;
        }
        return -1;
    }

    /**
     * Gustavo 20 Sep 2007
     * @param edge
     */
    public boolean clearJessWmeFact(String commName) {
        return setSAIDirectly(commName, null, "nil");       
    }	

    /**
     * Tell whether to modify the student values fact at the start of each trace, for LHS matching.
     * If the argument is--<ul>
     * <li>false, the fact will not be asserted or modified;</li>
     * <li>true, a deftemplate for the fact will be created if needed and the fact will be used;</li>
     * <li>null, the fact will be modified only if a studentValues deftemplate already exists.</li>
     * </ul>
     * @param b new value for {@link #useStudentValuesFact}
     * @return prior value of {@link #useStudentValuesFact}
     */
    public Boolean setUseStudentValuesFact(Boolean b) {
    	Boolean oldValue = useStudentValuesFact;
    	useStudentValuesFact  = b;
    	return oldValue;
    }

    /**
     * Tell whether to modify the student values fact at the start of each trace, for LHS matching.
   	 * For backwards compatibility, the value in {@link #useStudentValuesFact} can be overridden
   	 * by the System property {@value #USE_STUDENT_VALUES_FACT}. For the meaning of the result, see
   	 * {@link #setUseStudentValuesFact(Boolean)}.
   	 * Called by {@link #setGlobalSAI(String, String, String)} to get last argument for
   	 * {@link #setGlobalSAI(String, String, String, Boolean)}.
     * @return value of the System property, if defined, or of {@link #useStudentValuesFact}
     */
    public Boolean getUseStudentValuesFact() {
    	Boolean result = useStudentValuesFact;
   		try {          // System.getProperty() could throw SecurityManagerException in applets
   			String prop = System.getProperty(USE_STUDENT_VALUES_FACT);
   			if(null != prop && prop.length() > 0)
   				result = Boolean.valueOf(prop);
   		} catch(Exception e) {
   			trace.err("Error from System.getProperty(\""+USE_STUDENT_VALUES_FACT+"\"): "+e+
   					"; returning "+result);
   		}
   		return result;
    }

	/**
	 * @param skipWhyNotSaves new value for {@link #skipWhyNotSaves} 
	 */
	public void setSkipWhyNotSaves(boolean skipWhyNotSaves) {
		if(getJmt() != null)
			getJmt().setSkipWhyNotSaves(skipWhyNotSaves);
	}
}
