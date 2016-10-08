/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

//import pact.CommWidgets.StudentInterfaceConnectionStatus;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Utilities.EventLogger;
import edu.cmu.pact.Utilities.TextOutput;
//import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.model.ProblemSummary;
import edu.cmu.pact.ctat.model.Skill;

/**
 * @author sewall
 *
 */
public class SkillsConsoleDialog extends JPanel {

	private static final long serialVersionUID = 7118394287684359356L;

	/** Name for author logging event. */
	public static final String CLEAR = "CLEAR";

	/** Name for author logging event. */
	private static final String CLEAR_WINDOW = "Clear Window";
	
	/** Maximum length for {@link TextOutput.TextArea} */
	private static int MAXSIZE = 32768;
	
	/** Back link to BR_Controller. */
	private BR_Controller controller;
	
	/** The original launcher with global information. */
	private CTAT_Launcher server;
	
	/** Displays the data. */
	private JTextArea textArea;
	
	/** Next write position in the {@link #textArea}. */
	private int caretPosition;

	/** Writer beneath the PrintWriter {@link #pw}. */
	private StringWriter sw = null;	

	/** Writer for formatting lines for {@link #textArea}. */
	private PrintWriter pw = null;

	/**
	 * Calls the constructor using owner frame from {@link BR_Controller#getActiveWindow()}.
	 * @param controller
	 * @return the new Dialog
	 */
    	JFrame ownerFrame = (controller != null ? controller.getDockedFrame() : null);
    	public static SkillsConsoleDialog create(CTAT_Launcher server) {
    	SkillsConsoleDialog scd = new SkillsConsoleDialog(server);
    	return scd;
	}
	
	/**
	 * Lay out the Panel; also attach the Rete object to
	 * the input and output text components.
	 * @param owner
	 * @param title
	 */
	private SkillsConsoleDialog(CTAT_Launcher server) {
		setName("Skills Console");
		this.server = server;
		refresh();
	}
	
	public void refresh() {
		this.controller = this.server.getFocusedController();

		// Set up the GUI elements
		textArea = new JTextArea(10, 40);
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(textArea); }
//		textArea.setPreferredSize(new Dimension(200,100)); set no size to get scrolling in dock
		textArea.setName("Skills Console Output");
		textArea.setOpaque(false);
		textArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(textArea);
		sw = new StringWriter();
		pw = new PrintWriter(sw);

		textArea.setEditable(false);

		JButton bClear = new JButton(CLEAR_WINDOW);
		bClear.setActionCommand(CLEAR_WINDOW);
		bClear.setPreferredSize(new Dimension(60,25));

		// Assemble the GUI
		setLayout(new BorderLayout());

		add("Center", scrollPane);

		JPanel p = new JPanel();       // could put more buttons on this panel
		p.setLayout(new BorderLayout());
		p.setBorder (new EmptyBorder (4, 2, 2, 2));
		p.add("Center", bClear);

		add("South", p);

		/**
		 * Handle the Clear button
		 */
		bClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				textArea.setText("");
				caretPosition = 0;
				EventLogger eventLogger = new EventLogger(getController().getLoggingSupport());
				eventLogger.log(true, 
						AuthorActionLog.SKILLS_CONSOLE,
						CLEAR,
						CLEAR_WINDOW, "", "");
			}
		});

		addListener();
		validate();
		setVisible(true);
	}
	
	private void addListener() {
		if (controller != null)
			controller.addSkillsConsole(this);
	}

	/** Latest string set from an {@link TextOutput.OutputEvent}. */
	private StringBuffer lastTextOutputData = new StringBuffer();

	/**
	 * @param lastTextOutputString new value for {@link #lastTextOutputData}
	 */
	private synchronized void addLastTextOutputData(String s) {
		lastTextOutputData.append(s);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.TextListener#textValueChanged(java.awt.event.TextEvent)
	 */
	public void textValueChanged(TextEvent arg0) {
		trace.out ("mps","event = " + arg0);
		update();
	}
	
	/**
	 * Clear the output area.
	 */
	void clearOutputArea() {
		textArea.setText("");
		caretPosition = 0;
	}

	/**
	 * 
	 */
	public void update() {
		int newCaretPosition = textArea.getText().length();
	    if (newCaretPosition <= caretPosition)
	        return;
	    trace.out ("mps", "old pos = " + 
	    	    caretPosition + 
	    	    " new pos = " + newCaretPosition);
	    textArea.setSelectionStart(caretPosition);
	    textArea.setSelectionEnd(newCaretPosition);
	    
		caretPosition = newCaretPosition;
	}

	/**
	 * @return Returns the textArea.
	 */
	JTextArea getTextArea() {
		return textArea;
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

	/**
	 * Format each Skill and {@link #append(String)} the result.
	 * @param assocRulesResp has tutor s,a,i and skills.
	 * @return
	 */
	public String append(MessageObject assocRulesResp) {
		if (assocRulesResp == null)
			return null;
		Vector s = (Vector) assocRulesResp.getProperty("Selection");
		Vector a = (Vector) assocRulesResp.getProperty("Action");
		Vector i = (Vector) assocRulesResp.getProperty("Input");
		Vector<String> skillBars = (Vector<String>) assocRulesResp.getProperty("Skills");
		
		sw.getBuffer().setLength(0);                // clear the buffer
		pw.printf("\n%s %s %s\n", (s == null ? "[]" : s), (a == null ? "[]" : a), (i == null ? "[]" : i));
		if (skillBars == null)
			pw.println();
		else {
			ProblemSummary ps = (controller == null ? null : controller.getProblemSummary());
			String delimiter = (ps == null || ps.getSkills() == null ?
					null : ps.getSkills().getSkillBarDelimiter());
			for (String skillBarStr : skillBars) {
				Skill.SkillBar sb = Skill.parseSkillBarString(skillBarStr, delimiter);
				pw.printf("%-15s %-35s %5.2f%%\n", sb.getCategory(), sb.getName(),
						(sb.getPKnown() == null ? 0f : sb.getPKnown().floatValue()*100));
			}
		}
		String result = sw.toString();
		append(result);
		return result;
	}
	
    /**
     * Implementation of {@link JTextArea#append(String)} method. Allows length to grow
     * to 2*{@link #MAXSIZE} before deleting MAXSIZE characters from
     * the beginning of the TextArea.
     * @param  s String to pass to {@link #delegate}'s method
     */
    protected void append(String s) {
        int len = textArea.getText().length() + s.length();
        if (len > MAXSIZE*2)
            textArea.replaceRange("", 0, MAXSIZE);
        textArea.append(s);
        // Make sure the last line is always visible
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
    
    public void dispose() {
    	removeListener();
    }

	private void removeListener() {
		if (controller != null)
			controller.removeSkillsConsole();
	}

	/**
	 * @return the {@link #controller}
	 */
	private BR_Controller getController() {
		return controller;
	}
}
