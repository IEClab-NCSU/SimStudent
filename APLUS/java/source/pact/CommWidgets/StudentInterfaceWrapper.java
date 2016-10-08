/*
 * Copyright 2005 Carnegie Mellon University.
 */
package pact.CommWidgets;

import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JFrame;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintWindowInterface;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pslc.logging.LogContext;

/**
 * @author sewall
 *
 */
public interface StudentInterfaceWrapper extends MouseListener {

    public static final String[] cleanUpMessages = new String[] {
        "InterfaceAction", "StartProblem", "StartStateEnd", "StartNewProblem", "CorrectAction", "ResetAction", "LISPCheckAction", "IncorrectAction", "UnlockComposer",
        "HintList", "LoadProblem", "ShowHintsMessage", "SuccessMessage", "BuggyMessage", "WrongUserMessage", "NoHintMessage", "HighlightMsg", "ShowHintsMessageFromLisp",
        "ShowLoginWindow"
    };
	
	/** Content of {@link ActionEvent#actionCommand} on successful Done step. */
	public static final String COMPLETE_ALL_ITEMS = "Complete all items";

	public static final String[] tutorEvaluationMessages = new String[] {
		"CorrectAction", "IncorrectAction"
	};

	/**
	 * Access to the student interface panel in the wrapper's container.
	 * @return tutorPanel the student interface panel
	 */
    public JComponent getTutorPanel();

	/**
	 * Place the student interface panel in the wrapper's container.
	 * @param tutorPanel the student interface panel
	 * @return options created by or for tutorPanel
	 */
    public CTAT_Options setTutorPanel(JComponent tutorPanel);
    
    /**
     * Swing equivalent to display the component.
     * @param visible true to display the component
     */
    public void setVisible(boolean visible);


    /**
     * Swing equivalent to tell whether the component is displayed.
     * @return true if displaying the component
     */
    public boolean isVisible();

    /**
     * Swing equivalent to place the component.
     * @param x horiz coordinate
     * @param y vert coordinate
     */
	public void setLocation(int x, int y);

    /**
     * Swing equivalent to get the size of the component.
     * @return height
     */
	public int getHeight();

    /**
     * Swing equivalent to get the size of the component.
     * @return width
     */
	public int getWidth();

    /**
     * Convenience method for access to hint interface.
     * @return {@link #wrapperSupport}.getHintInterface()
     */
    public HintWindowInterface getHintInterface();

    /**
     * Access to the {@link WrapperSupport} object with common methods.
     * @return WrapperSupport 
     */
    public WrapperSupport getWrapperSupport();

    /**
     * Whether the student interface should prompt the student for a userid.
     * @param loginEnabled
     */
	public void enableLMSLogin(boolean loginEnabled);

	/**
	 * Direct the student interface to include a menu pick for the student
	 * to advance to the next problem.
	 */
	public void showAdvanceProblemMenuItem();
	
	/**
	 * Return a reference to the top-level UI object, the Window.
	 * @return enclosing JFrame for calling component; may be null
	 */
	public JFrame getActiveWindow();
	
	/**
	 * Return a Logger instance, useful for message conversion.
	 */
	public LogContext getLogger();
	
	/**
	 * Request a hint from the tutoring system.
	 */
	public void requestHint();
	
	/**
	 * Request a done from the tutoring system.
	 */
	public void requestDone();
}
