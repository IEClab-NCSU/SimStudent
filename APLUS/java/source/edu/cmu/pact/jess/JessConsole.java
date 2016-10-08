package edu.cmu.pact.jess;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;
import java.io.Serializable;

import javax.swing.text.PlainDocument;

import jess.JessException;
import jess.Main;
import jess.Rete;
import jess.Value;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.TextOutput;
import edu.cmu.pact.Utilities.trace;

/* Formerly a JComponent, but separated from GUI elements 07/2013 in order to
 * support the ability to switch between console output for multiple open graphs.
 */
public class JessConsole implements Serializable, TextOutput.OutputEventListener, 
	PropertyChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3004760790112650076L;
	private static final String BREAK_ON_EXCEPTIONS = "Break on Exceptions";
	private static final String FIRABLE = "Firable";
	private static final String CHAIN = "Chain";
	private volatile MTRete m_rete;
	boolean m_doEcho = true;
	private TextOutput textOutput = TextOutput.getNullOutput();
	public static final String USER_COMMAND = "USER_COMMAND";
	public static final String STOP = "STOP";
	public static final String CLEAR = "CLEAR";
	public static final String RELOAD = "RELOAD";
	
	/** Writer for text area. */
	private PrintWriter pw;
	/** Latest string set from an {@link TextOutput.OutputEvent}. */
	private StringBuffer lastTextOutputData = new StringBuffer();
	
	/**
	 * Create a Console, using a prexisting Rete object, that optionally doesn't echo commands.
	 * @param title The title for the frame
	 * @param engine A Rete object
	 * @param doEcho  True only if the typed commands should be echoed to the window.
	 */
	public JessConsole(String title, MTRete engine, boolean doEcho, CTAT_Controller controller) {
//        super (controller);
//		setTitle(title);
		// ### 
		m_rete = engine;
		textOutput = TextOutput.getTextOutput(new PlainDocument());
		pw = textOutput.getWriter();
		m_doEcho = doEcho;

		trace.out ("mps", "CREATING WINDOW HELPER");

		if (controller != null) { 
			PreferencesModel pm = controller.getPreferencesModel();
			if (pm != null) {
				pm.addPropertyChangeListener(CHAIN, this);
				pm.addPropertyChangeListener(FIRABLE, this);
				pm.addPropertyChangeListener(BREAK_ON_EXCEPTIONS, this);
				getPreferencesFromModel(pm);
			}
		}
		connectToRete(engine);
	}
	
	/**
	 * Accessor for {@link edu.cmu.pact.Utilities.TextOutput}. 
	 * @return value of {@link #textOutput}
	 */
	public TextOutput getTextOutput() {
	    return textOutput;
	}
	
	/**
	 * Clear the output area.
	 */
	public void clearOutputArea() {
		getTextOutput().clear();
	}

	/**
	 * 
	 */
	private void getPreferencesFromModel(PreferencesModel model) {
		final Boolean booleanValue = model.getBooleanValue(CHAIN);
        if (booleanValue != null)
            MTRete.displayChain = booleanValue.booleanValue();
		final Boolean booleanValue2 = model.getBooleanValue(FIRABLE);
        if (booleanValue2 != null)
            MTRete.displayFired = booleanValue2.booleanValue();
        
		final Boolean booleanValue3 = model.getBooleanValue(BREAK_ON_EXCEPTIONS);
        if (booleanValue3 != null)
            MTRete.breakOnExceptions = booleanValue3.booleanValue();
	}
	

	/**
	 * Return the Rete engine being used by this Console.
	 * @return The Rete object used by this console.
	 */
	public Rete getEngine() {
		return m_rete;
	}

	/**
	 * Pass the argument array on to an instance of jess.Main connected
	 * to this Console, and call Main.execute().
	 * @param argv Arguments for jess.Main.initialize().
	 */
	void execute(String[] argv) {
		Main m = new Main();
		m.initialize(argv, m_rete);
		getJessPanel().setFocus();
		m.execute(m_doEcho);
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 * 
	 * Called by the PreferencesModel which we're listening to
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		String name = arg0.getPropertyName();
		Object newValue = arg0.getNewValue();
		if (trace.getDebugCode("mps")) trace.out("mps", "Changed " + name + " from " + arg0.getOldValue() +
				  " to " + newValue);
		
		if (name.equals (CHAIN))
			MTRete.displayChain = ((Boolean) newValue).booleanValue();
		if (name.equals(FIRABLE))
			MTRete.displayFired = ((Boolean) newValue).booleanValue();
		if (name.equals(BREAK_ON_EXCEPTIONS))
			MTRete.breakOnExceptions = ((Boolean) newValue).booleanValue();
		
		
		
	}

	/**
	 * @param rete The Rete to set.
	 */
	void setRete(MTRete rete) {
		m_rete = rete;
		connectToRete(m_rete);
	}	
	
	public JessConsolePanel getJessPanel() {
		if(m_rete.getMT().getController() == null)
			return null;
		else
			return m_rete.getMT().getController().getServer().getCtatFrameController().getDockManager().getJessPanel();
	}
    
	/**
	 * Connect the given Rete engine's default input and output routers to
	 * this panel's text areas.  
	 * @param rete engine to connect; no-op if null
	 */
	void connectToRete(MTRete rete) {
		if (rete == null)
			return;
		rete.addOutputRouter("t", pw);
		rete.addOutputRouter(MTRete.DEFAULT_IO_ROUTER, pw);
		rete.addOutputRouter(MTRete.DEFAULT_ERR_ROUTER, pw);
		try {
			rete.setWatchRouter(MTRete.DEFAULT_IO_ROUTER);
		} catch (JessException je) {
			pw.println("Error connecting watch router to this window: "+je);
			je.printStackTrace();
		}
		rete.setTextOutput(getTextOutput());
	}
	
	/**
	 * Action handler for command text field {@link #m_tf}.
	 * @param ae
	 */
	public void executeCommand(String cmd) {
		try {
			
			textOutput.addOutputEventListener(this);
			Value result = m_rete.eval(cmd);
			textOutput.removeOutputEventListener(this);
			String resultStr = result.toStringWithParens();
			
			textOutput.append(resultStr);
			m_rete.getEventLogger().log(true, 
					AuthorActionLog.JESS_CONSOLE,
					JessConsole.USER_COMMAND,
					cmd, resultStr, getLastTextOutputData());
			
			textOutput.prompt(JessConsolePanel.PROMPT);
		} catch (JessException je) {
			PrintWriter err = textOutput.getWriter();
            displayException(err, je);
            if (je.getCause() != null) {
                err.write("\nNested exception is:\n");
                displayException(err, je.getCause());
            }
		}
	}

	/**
	 * Display an exception.
	 */
    private void displayException(PrintWriter err, Throwable ex) {
    	if (ex instanceof JessException)
    		err.println(ex.toString());
    	else
    		err.println(ex.getMessage());
    	ex.printStackTrace();
    }
	
	public void doEcho(String cmd) {
		
		textOutput.prompt(JessConsolePanel.PROMPT);
		textOutput.append(cmd);
		textOutput.append("\n");
	}

	/**
	 * Get and clear the {@link #lastTextOutputData}.
	 * @return contents of {@link #lastTextOutputData} at time of call;
	 *         empty string if null
	 */
	private synchronized String getLastTextOutputData() {
		String result = lastTextOutputData.toString();
		lastTextOutputData = new StringBuffer();
		return result;
	}

	/**
	 * @param lastTextOutputString new value for {@link #lastTextOutputData}
	 */
	private synchronized void addLastTextOutputData(String s) {
		lastTextOutputData.append(s);
	}
	/**
	 * Capture the output from the {@link #textOutput} with
	 * {@link #setLastTextOutputData(String)}.
	 * @param e event with output
	 * @see edu.cmu.pact.Utilities.TextOutput.OutputEventListener#outputOccurred(edu.cmu.pact.Utilities.TextOutput.OutputEvent)
	 */
	public void outputOccurred(TextOutput.OutputEvent e) {
		addLastTextOutputData(e.getOutput());
	}
}
