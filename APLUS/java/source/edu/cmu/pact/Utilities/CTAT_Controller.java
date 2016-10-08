/*
 * Copyright 2005 Carnegie Mellon University.
 */
package edu.cmu.pact.Utilities;

import java.applet.Applet;
import java.util.Vector;

import pact.CommWidgets.StudentInterfaceWrapper;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeListener;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.View.BRPanel;
import edu.cmu.pact.BehaviorRecorder.jgraphwindow.JGraphPanel;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.model.CtatModeModel;
import edu.cmu.pact.ctat.model.ProblemSummary;
import edu.cmu.pact.ctat.view.AbstractCtatWindow;
import edu.cmu.pact.jess.MT;
import edu.cmu.pact.jess.RuleActivationTree;
import edu.cmu.pact.miss.MissControllerExternal;
import edu.cmu.pslc.logging.LogContext;

/**
 * Interface to abstract out those elements of the rest of the system
 * that the Jess code needs to know about.
 */
public interface CTAT_Controller {

	/** Preference name for directory holding the student interface or brd file. */
	public static final String PROBLEM_DIRECTORY = "Problem Directory";
	
	/** Examples subdirectory name for BRD files. */
	public static final String PROBLEMS = "problems";

	/** Problem set subdirectory name for BRD files. */
	public static final String FINAL_BRDS = "FinalBRDs";

	/** Problem set subdirectory name for templates, rules, facts files. */
	public static final String COGNITIVE_MODEL = "CognitiveModel";

	/**
	 * Tell whether to update working memory even when a model trace has
	 * failed.  This can be used for training rule-generation mechanisms.
	 * @return true if should update WM when a trace attempt has failed
	 */
	public boolean updateModelOnTraceFailure();
	
	public EventLogger getEventLogger();
	
	public LogContext getLogger();
	/**
	 * Provide a reference to the {@link PreferencesModel} instance for this
	 * execution.
	 * @return PreferencesModel instance
	 */
	public PreferencesModel getPreferencesModel();
    
    /**
     * Get a reference to the top-level class of the Jess model tracer.
     * @return reference to the Jess model tracer
     */
    public MT getModelTracer();
	
	/**
	 * Retrieve the Conflict Tree window from the controller.
	 * @return reference to the {@link RuleActivationTree}; null if none
	 */
	public RuleActivationTree getRuleActivationTree();
	
	/**
	 * Return the current {@link LoggingSupport} instance
	 * @return Controller's {@link LoggingSupport} instance; null if none
	 */
	public LoggingSupport getLoggingSupport();

    /**
     * Get the current student interface window.
     * @return null if no interface loaded
     */
    public StudentInterfaceWrapper getStudentInterface();
    
    /**
     * Return the parent frame for dialogs to present to authors.
     * Use {@link #getStudentInterface()} to return the equivalent for students.
     * @return top-level docked frame or {@link BRPanel} if not docked
     */
    public AbstractCtatWindow getActiveWindow();

    /**
     * Shut down CTAT.
     * @param promptToSaveBrd if true, prompt author to save before exiting;
     *        else close silently
     */
    public void closeApplication(boolean b);
    
    /**
     * Return the {@link BRPanel} window.
     * @return reference to the window 
     */
    public BRPanel getBR_Frame();


    /**
     * Get the problem model.
     * @return controller's problem model
     */
    public ProblemModel getProblemModel();

    public AbstractCtatWindow getDockedFrame();
    public JGraphPanel getJGraphWindow();

    public MissControllerExternal getMissController();

    public CtatModeModel getCtatModeModel();
    

    public void addCtatModeListener (CtatModeListener listener);

    /**
     * Send a tool action message to the student interface.
     * @param selection
     * @param action
     * @param input
     */
	public void enqueueToolActionToStudent(Vector selection, Vector action, Vector input);
    
    /**
     * Accept a message for processing by the tutor controller.
     * @param mo the message
     */
    public void handleCommMessage(MessageObject mo);

    /**
     * @return path to the currently-loaded problem
     */
	public String getProblemFullName();

	/**
	 * @return current problem name
	 */
	public String getProblemName();
	
	/**
	 * @return current problem summary
	 */
	public ProblemSummary getProblemSummary();

	/**
	 * @return applet instance; null if not applet
	 */
	public Applet getApplet();

	public CTAT_Launcher getServer();
}
