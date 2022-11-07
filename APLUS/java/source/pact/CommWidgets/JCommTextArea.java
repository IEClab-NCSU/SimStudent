package pact.CommWidgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Highlighter;

import pact.CommWidgets.InputMethodFramework.ActiveClient;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;

//////////////////////////////////////////////////////
/**
 * <p>
 * Title: JCommTextArea
 * </p>
 * <p>
 * Description: A text area which automatically sends user's input to Lisp via
 * Comm
 * </p>
 */
//////////////////////////////////////////////////////
//////////////////////////////////////////////////////
/**
 * This class will automatically send user's input to Lisp.
 */
/////////////////////////////////////////////////////
public class JCommTextArea extends JCommWidget implements FocusListener, MouseListener, KeyListener {
	
    protected JTextArea textArea;

    protected JScrollPane textAreaScrollPane;

    protected String previousValue, resetValue = "";

    protected Highlighter defaultHighlighter;
    
    private Font previousFont;

    private Color previousColor;

    //////////////////////////////////////////////////////
    /**
     * Constructor
     */
    //////////////////////////////////////////////////////
    public JCommTextArea() {

        textArea = new JTextArea(5,5); // instead of JTextArea() to get rid of the default Perferred size which keeps show on property width
        { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(textArea); }
        textArea.addKeyListener(this);
		textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        textArea.setDocument(new JCommDocument());

        textAreaScrollPane = new JScrollPane(textArea);
		textAreaScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		textAreaScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(textAreaScrollPane);
        //add(textArea);
        GridLayout g = new GridLayout(1, 1);
        setLayout(g);
        try {
            textArea.addFocusListener((FocusListener) this);
            initialized = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        actionName = UPDATE_TEXT_AREA;
        defaultHighlighter = textArea.getHighlighter();
        originalBorder = textArea.getBorder();
        textArea.addMouseListener (this);
		textArea.add(new ActiveClient(textArea.getHeight(), textArea.getWidth()));

    }

    //////////////////////////////////////////////////////
    /**
     * Used to process an InterfaceAction message
     */
    //////////////////////////////////////////////////////
	public void doInterfaceAction(String selection, String action, String input) {

		if (action.equalsIgnoreCase("UpdateTextArea")) {
			((JCommDocument) textArea.getDocument()).locked = false;  // sewall 2010/02/09: so can setText()
			setText(input);
			if (getController().isStartStateInterface() && input != null && input.length() > 0) {
				((JCommDocument) textArea.getDocument()).locked = true;
				textArea.setHighlighter(null);
				setFocusable(false);
			}

			return;
		}
		else if (SET_VISIBLE.equalsIgnoreCase(action)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		}
		trace.out("**Error**: don't know interface action " + action);
	}

    //////////////////////////////////////////////////////
    /**
     * Returns a comm message which describes this interface element.
     */
    //////////////////////////////////////////////////////
	public MessageObject getDescriptionMessage() {

		MessageObject mo = MessageObject.create("InterfaceDescription");

		if (!initialize(getController())) {
			trace
					.out("ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return mo;
		}

		mo.setVerb("SendNoteProperty");

		mo.setProperty("WidgetType", "JCommTextArea");
		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));

		Vector deftemplates = createJessDeftemplates();
		Vector instances = createJessInstances();

		if (deftemplates != null)
			mo.setProperty("jessDeftemplates", deftemplates);

		if (instances != null)
			mo.setProperty("jessInstances", instances);

		serializeGraphicalProperties(mo);

		return mo;
	}

    // sanket@cs.wpi.edu

    public Vector createJessDeftemplates() {
        Vector deftemplates = new Vector();

        String deftemplateStr = "(deftemplate textArea (slot name) (slot value))";
        deftemplates.add(deftemplateStr);

        return deftemplates;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pact.CommWidgets.JCommWidget#createJessInstances()
     */
    public Vector createJessInstances() {
        Vector instances = new Vector();

        String instanceStr = "(assert (textArea (name " + commName + ")))";
        instances.add(instanceStr);

        return instances;
    }

    // sanket@cs.wpi.edu


    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
	public void doCorrectAction(String selection, String action, String input) {
		if (SET_VISIBLE.equalsIgnoreCase(action)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		} else {
			((JCommDocument) textArea.getDocument()).locked = false;

			textArea.setText(input);
			textArea.setForeground(correctColor);

			if (getController().getUniversalToolProxy().lockWidget()) {
				((JCommDocument) textArea.getDocument()).locked = true;
				textArea.setHighlighter(null);
				removeHighlight("");
				setFocusable(false);
			}

			if (correctFont != null)
				textArea.setFont(correctFont);
		}
	}

    public boolean getLock(String selection) {
        return ((JCommDocument) textArea.getDocument()).locked;
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void doLISPCheckAction(String selection, String input) {
        textArea.setText(input);
        textArea.setForeground(LISPCheckColor);
        
        if (getController().getUniversalToolProxy().lockWidget())
            ((JCommDocument) textArea.getDocument()).locked = true;
        
        textArea.setHighlighter(null);
        setFocusable(false);
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void doIncorrectAction(String selection, String input) {
        textArea.setText(input);

        textArea.setForeground(incorrectColor);
        ((JCommDocument) textArea.getDocument()).locked = false;
        textArea.setHighlighter(defaultHighlighter);
        setFocusable(true);

        if (incorrectFont != null) textArea.setFont(incorrectFont);
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void focusGained(FocusEvent e) {

        if (!((JCommDocument) textArea.getDocument()).locked) {
            previousValue = textArea.getText();

            previousFont = textArea.getFont();
            previousColor = textArea.getForeground();

            /*
             * zz 09/11/04 textArea.setForeground(startColor); if (startFont !=
             * null) textArea.setFont(startFont);
             */
        }

        super.focusGained(e);
        //textArea.setBackground(backgroundNormalColor);
    }

	public void highlight(String commComponentName, Border highlightBorder) {
		setBorder(highlightBorder);
	}
    
	//////////////////////////////////////////////////////
	/**
	 */
	//////////////////////////////////////////////////////
	public void removeHighlight(String subElement) {
		setBorder(originalBorder);
	}
    
    public void setFocus(String subWidgetName) {
        textArea.requestFocus();
    }

    //////////////////////////////////////////////////////
    /**
     * If focus lost permanently, and text has changed, send value to lisp
     */
    //////////////////////////////////////////////////////
    public void focusLost(FocusEvent e) {

    	Component oppComponent = e.getOppositeComponent();
    	if (trace.getDebugCode("inter")) trace.out("inter", "JCommTextArea: focusLost oppComponent "+
    			(oppComponent instanceof JCommWidget ? ((JCommWidget) oppComponent).getCommName(): oppComponent)+
    			"\n locked "+((JCommDocument) textArea.getDocument()).locked+
    			", definingStartState "+getController().isDefiningStartState()+
    			", previousValue "+previousValue+
    			", getText() "+getText());
    	
        if (!((JCommDocument) textArea.getDocument()).locked) {
        	if (getController().isDefiningStartState()) {
        		if (!previousValue.equals(getText())) {
        			setText(getText());
        			dirty = true;
        			setActionName("UpdateTextArea");
        			getController().setStartStateModified(true); // CTAT2861: in case of delete cell value
        			sendValue();
        			return;
        		}
        	}
        	if (e.isTemporary()) return;

        	if (trace.getDebugCode("inter")) trace.out("inter", "JCommTextArea: focusLost oppComponent "+
        			(oppComponent instanceof JCommWidget ? ((JCommWidget) oppComponent).getCommName() : oppComponent));
        	if (!focusTriggersBackGrading(oppComponent))
        		return;

        	if (getController().getUniversalToolProxy().getAutoCapitalize() == true
        			|| getAutoCapitalize() == true) {
        		textArea.setText(textArea.getText().toUpperCase());
        	}

        	//       if (!textArea.getText().equals(previousValue))  //  it doesn't work in ordered mode, see CTAT1951
        	dirty = true;
            if (!textArea.getText().equals(previousValue)) {  // CTAT2861: copied from JCommTextField
//              dirty = true;
            	if (getController().isDefiningStartState())
            		getController().setStartStateModified(true); // in case of delete cell value
            } 
            else if (textArea.getText().equals("")) dirty = false; // CTAT2066 -- Ctrl-click on state creates a new link

        	if (dirty) sendValue();

        	dirty = false;
        	//previousValue = textArea.getText();
        }
        super.focusLost(e);
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public Object getValue() {
        textArea.setForeground(startColor);

        return textArea.getText().trim();
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public String getText() {
        return textArea.getText();
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void setText(String text) {
        textArea.setText(text);
        previousValue = text;
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void setFont(Font f) {
        if (textArea != null) textArea.setFont(f);
        super.setFont(f);
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public boolean isChangedFromResetState() {
        if (!textArea.getText().equals(resetValue)) {
            ((JCommDocument) textArea.getDocument()).locked = true;
            textArea.setHighlighter(null);
            setFocusable(false);

            return true;
        }

        return false;
    }

    public boolean resetStartStateLock(boolean startStateLock) {
        if (!textArea.getText().equals(resetValue)) {
 //       	trace.out("Reset TextField [" + commName + "] - " + textArea.getText() + startStateLock);
            ((JCommDocument) textArea.getDocument()).locked = startStateLock;
            textArea.setHighlighter(null);
            setFocusable(!startStateLock);
            
            return true;
        }
        
        return false;
    }

    public void setFocusable(boolean focusFlag) {
        super.setFocusable(focusFlag);
        textArea.setFocusable(focusFlag);
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void reset(TutorController controller) {
        initialize(controller);
        ((JCommDocument) textArea.getDocument()).locked = false;
        textArea.setHighlighter(defaultHighlighter);
        setFocusable(true);
        textArea.setText(resetValue);
        previousValue = resetValue;
        textArea.setForeground(startColor);
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void changedUpdate(DocumentEvent e) {
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void insertUpdate(DocumentEvent e) {
        dirty = true;
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void removeUpdate(DocumentEvent e) {
        dirty = true;
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void setBackground(Color c) {
        if (textArea != null) textArea.setBackground(c);
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void setToolTipText(String text) {
        textArea.setToolTipText(text);
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public String getToolTipText() {
        return textArea.getToolTipText();
    }

    //////////////////////////////////////////////////////
    /**
     *  
     */
    //////////////////////////////////////////////////////
    public void addFocusListener(FocusListener l) {
        super.addFocusListener(l);
        if (textArea != null) textArea.addFocusListener(l);
    }

    public void addMouseListener(MouseListener l) {
        if (textArea != null) textArea.addMouseListener(l);
    }

    public void addMouseMotionListener(MouseMotionListener l) {
        if (textArea != null) textArea.addMouseMotionListener(l);
    }

    public MouseListener[] getMouseListeners() {
        if (textArea != null) return textArea.getMouseListeners();
        return null;
    }

    public MouseMotionListener[] getMouseMotionListeners() {
        if (textArea != null)

        return textArea.getMouseMotionListeners();
        return null;
    }

    public void removeMouseMotionListener(MouseMotionListener l) {
        if (textArea != null) textArea.removeMouseMotionListener(l);
    }

    public void removeMouseListener(MouseListener l) {
        if (textArea != null) textArea.removeMouseListener(l);
    }

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent me) {
		removeHighlight ("");
		if(me.isControlDown() && getController().isDefiningStartState()){
			((JCommDocument) textArea.getDocument()).locked = false;
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return true if {@link #textArea}.{@link JTextArea#getLineWrap()
	 *         getLineWrap()} and {@link #textArea}.
	 *         {@link JTextArea#getWrapStyleWord() getWrapStyleWord()} are true
	 */
	public boolean getWordWrap() {
		return textArea.getLineWrap() && textArea.getWrapStyleWord();
	}

	/**
	 * @param b true to turn on line-wrapping at word boundaries
	 */
	public void setWordWrap(boolean b) {
		if (b) {
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
		} else {
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(false);
		}
	}
	
    /**
     * Currently a no-op.
     * @param e
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
	public void keyReleased(KeyEvent e) {}
 
	/**
	 * If field unlocked, sets foreground to {@link #startColor}.
	 * @param e
	 */
    public void keyTyped(KeyEvent e) {
        if (!((JCommDocument) textArea.getDocument()).locked)
            textArea.setForeground (startColor);
    }
    
    /**
     * Currently a no-op.
     * @param e
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void keyPressed(KeyEvent e) {}
}
