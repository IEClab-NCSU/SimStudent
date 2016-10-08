package edu.cmu.pact.jess;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.trace;

/**
 * 
 */

/**
 * A single {@link JPanel} for the dock, displaying output from one of
 * multiple problem-specific {@link JessConsole}.
 */
public class JessConsolePanel extends JPanel implements TextListener,
		ActionListener , KeyListener {

	private static final long serialVersionUID = -5766160217867156983L;

	private static final String CLEAR_WINDOW = "Clear Window";

	/** Command prompt. */
	public static final String PROMPT = "\n\nJess> ";

    private final int BACKWARD = 0;
    private final int FORWARD  = 1;
    
	// Members used for getting input
	private JTextField m_tf;

	private JTextArea textArea;
	private int caretPosition;
	
	private CTAT_Launcher server;


	/** If true, echo commands in the main window. */
	private boolean doEcho = false;
	
	
	/** Store all user entered Jess commands up to 50 commands */
	private commandQueue JessCommandQueue = new commandQueue();

	
	public JessConsolePanel(CTAT_Launcher server, boolean doEcho) {
		this.server = server;
		this.doEcho = doEcho;

		// Set up the GUI elements
		setName ("Jess Console"); // added 07/29/2013
		setSize (500, 300); // added 07/29/2013
		setLayout (new BorderLayout());
		
		textArea = new JTextArea(10, 40);
		textArea.setName("Jess Console Output");
        textArea.setOpaque(false);
		textArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		
		m_tf = new JTextField(40);
		//{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(m_tf); }
		JUndo.makeTextUndoable(m_tf);
		m_tf.setName("Jess Console Input");
		textArea.setEditable(false);
		
		m_tf.addKeyListener(this);
		
	//	JButton bLoad = new JButton("Load Production Rules");
	//	bLoad.setActionCommand(JessConsole.RELOAD);
	//	bLoad.addActionListener(MTRete.getEventLogger());
		

		JButton bClear = new JButton(CLEAR_WINDOW);
		bClear.setActionCommand(JessConsole.CLEAR);
       
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		
		JPanel left = new JPanel();
		

//		** Removed until get a better strategy for this **
//		JButton cancelBtn = new JButton("Stop");
//		cancelBtn.setActionCommand(AuthorActionLog.STOP);
//		cancelBtn.addActionListener(m_rete.getEventLogger());


		// Assemble the GUI
		setLayout(new BorderLayout());
		//add("Center", sp);
		
		add("Center", scrollPane);
		//textArea.setVisible(false);
		
		
		p.add(m_tf, BorderLayout.CENTER);
		p.add (left, BorderLayout.EAST);
		
		p.setBorder (new EmptyBorder (4, 2, 2, 2));
	//	left.add(bLoad);
		left.add(bClear);
//		left.add(cancelBtn);
		
//		cancelBtn.addActionListener(new ActionListener(){
//			/* (non-Javadoc)
//			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
//			 */
//			public void actionPerformed(ActionEvent ae) {
//				try {
//					if (m_rete == null)
//						return;
//					m_rete.halt();
//					m_rete.stopModelTracing = true;
//				} catch (JessException e) {
//					e.printStackTrace();
//				}
//			}
//		});
		// sanket@cs.wpi.edu


		add("South", p);


		m_tf.addActionListener(this);

//		bLoad.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent ae) {
				// Load productionRules.pr
//				if (m_rete == null || (m_rete.getFacts().size() < 2))
//					return;
//				m_rete.reloadProductionRulesFile();
//			}
//		});

		bClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				m_tf.setText("");
				textArea.setText("");
				caretPosition = 0;
			}
		});
		
		validate();
		repaint();
	}

	/**
	 * Action handler for command text field {@link #m_tf}.
	 * @param ae
	 */
	public void actionPerformed(ActionEvent ae) {
		// get the entered command
		String cmd = m_tf.getText();
		JessConsole jessConsole = this.server.getFocusedController().getModelTracer().getConsole();
		
		// if necessary, echo the command in the problem console
		if (doEcho) {
			jessConsole.doEcho(cmd);
		}
		JessCommandQueue.saveCommand(cmd);
		jessConsole.executeCommand(cmd);	// actually carry out the command

		// clear the command entry
		m_tf.setText("");
		
		
		scrollToBottom();
		this.textArea.repaint();
	}

	/**
	 * Move focus to the input area. Helps to call this whenever a button is clicked, etc.
	 */

	final public void setFocus() {
		m_tf.requestFocus();
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
		m_tf.setText("");
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
	 * @param rete The Rete to set.
	 */
	/*
	void setRete(MTRete rete) {
		m_rete = rete;
		//connectToRete(m_rete);
	}
	*/


	/**
	 * Interface for unit testing. Submit a command as if typed in the text
	 * field {@link #m_tf}.
	 * @param source event source
	 * @param cmd Jess command to submit
	 */
	void submitCommand(String cmd) {
		m_tf.setText(cmd);
		actionPerformed(new ActionEvent(m_tf, 1, "submitCommand()"));
	}
	
	private class commandQueue {
	    private int queueLength = 50;
		private int pick;
		private int save = 0;
		private String[] historyQueue;

		public commandQueue() {
			historyQueue = new String[queueLength];
		}
		
		public commandQueue(int queueLength) {
			this.queueLength = queueLength;
			historyQueue = new String[queueLength];
		}
		
		public String getCommand(int direction) {
			
		    if (direction == BACKWARD && pick > 0) pick--;
			else if (direction == FORWARD && pick < save) pick++;


			if (pick == save)  return ("");

//			trace.out("mps", "Pick  (" + (pick % queueLength) + ") =" + historyQueue[pick % queueLength]);	
		     return (historyQueue[pick % queueLength]);
		}
		
		public  void saveCommand(String newCommand) {
		    
		     historyQueue[save % queueLength] = newCommand;
//		     trace.out("mps", "Save  (" + (save % queueLength) + ") =" + newCommand);	
		     save++;
		     pick = save;

		}
	}
	
	/** Handle the key released event from the text field. */
	public void keyReleased(KeyEvent e) {
//		trace.out("Key released:  = " + e);
	}
	/** Handle the key typed event from the text field. */
	public void keyTyped(KeyEvent e) {
//		trace.out("Key typed:  = " + e);
	}
        
	
	/** Handle the key pressed event from the text field. */        
	public void keyPressed(KeyEvent e) {

		if ((e.getKeyCode() == KeyEvent.VK_DOWN)
				|| (e.getKeyCode() == KeyEvent.VK_KP_DOWN)) { // VK_UP = 38
			m_tf.setText(JessCommandQueue.getCommand(FORWARD));

		} else if ((e.getKeyCode() == KeyEvent.VK_UP)
				|| (e.getKeyCode() == KeyEvent.VK_KP_UP)) { // VK_DOWN = 40

			m_tf.setText(JessCommandQueue.getCommand(BACKWARD));

		}

	}
	
	/**
	 * Update the display when switching problems or loading new output for a problem. 
	 * @param controller		The controller for the currently focused problem
	 */
	public void refresh() {
		BR_Controller controller = this.server.getFocusedController();
		Document doc = controller.getModelTracer().getConsole().getTextOutput().getDocument();
		
		this.textArea.setDocument(doc);
		scrollToBottom();
		this.textArea.validate();
		this.textArea.repaint();
	}
	
	public void scrollToBottom() {
		this.textArea.setCaretPosition(this.textArea.getText().length());
	}

}
