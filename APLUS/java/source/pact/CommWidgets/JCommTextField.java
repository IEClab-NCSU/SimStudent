package pact.CommWidgets;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Highlighter;

import pact.CommWidgets.JCommTable.TableCell;
import pact.CommWidgets.InputMethodFramework.ActiveClient;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;

//////////////////////////////////////////////////////
/**
 * This class will automatically send user's input to Lisp.
 */
/////////////////////////////////////////////////////


public class JCommTextField extends JCommWidget implements FocusListener, MouseListener, KeyListener {

    protected JTextField textField;
    protected String previousValue, resetValue = "";
    protected static int textFieldsCreated;
    protected JPanel container;
    
    protected Font startFont;
    protected Font previousFont;
    protected Color previousColor;
    protected Highlighter defaultHighlighter;
    
    protected boolean keyUsed = false;
    private boolean showBorder = true;
    // Borders to show the correct/incorrect answers.
    private Border correctBorder, inCorrectBorder, 
    			   LISPCheckBorder, highlightedBorder;
    
    protected boolean selected;



	static Vector selectedCellsList = new Vector();
	static Vector selectedValues = new Vector();
	static JFrame selectedCellsFrame;
	static JList nameList, valuesList;
	static DefaultListModel nameModel;
	static DefaultListModel valuesModel;
    //////////////////////////////////////////////////////
    /**
     * Constructor
     */
    //////////////////////////////////////////////////////
    public JCommTextField() {
    	selected = false;
        textField = new JTextField();
        { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(textField); }
		textField.setDocument(new JCommDocument());
		add(textField);
		
		GridLayout g = new GridLayout(1, 1);
		setLayout(g);
		try {
			textField.addFocusListener((FocusListener) this);
			textField.addKeyListener(this);
			initialized = false;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		actionName = UPDATE_TEXT_FIELD;
		defaultHighlighter = textField.getHighlighter();

		originalBorder = textField.getBorder();
		
		// corretBorder is set to CommExpressionTextField when the user value
        // is correct, typically correctColor is dark green.
        correctBorder = BorderFactory.createLineBorder(correctColor, 2);

        // inCorretBorder is set to CommExpressionTextField when the user value
        // is incorrect, typically correctColor is red.
        inCorrectBorder = BorderFactory.createLineBorder(incorrectColor, 2);

        LISPCheckBorder = BorderFactory.createLineBorder(LISPCheckColor, 2);
        //Provide a border for highlighting as well
        highlightedBorder = BorderFactory.createLineBorder(Color.blue, 2);
		constructSelectedCellsFrame();
		this.textField.addMouseListener(new MouseListenerClass());
		this.textField.add(new ActiveClient(this.textField.getHeight(), this.textField.getWidth()));

    }
    
    
	private void constructSelectedCellsFrame() {
			selectedCellsFrame = new JFrame();

			nameModel = new DefaultListModel();
			valuesModel = new DefaultListModel();
			nameList = new JList(nameModel);
			valuesList = new JList(valuesModel);

			selectedCellsFrame.getContentPane().setLayout(new BorderLayout());

			JPanel centerPanel = new JPanel();

			JPanel namePanel = new JPanel();
			namePanel.setLayout(new BorderLayout());
			namePanel.add(new JLabel("Name"), BorderLayout.NORTH);
			namePanel.add(nameList, BorderLayout.CENTER);
			namePanel.setBorder(BorderFactory.createEtchedBorder());

			JPanel valuesPanel = new JPanel();
			valuesPanel.setLayout(new BorderLayout());
			valuesPanel.add(new JLabel("Value"), BorderLayout.NORTH);
			valuesPanel.add(valuesList, BorderLayout.CENTER);
			valuesPanel.setBorder(BorderFactory.createEtchedBorder());


			JButton okBtn = new JButton("Send");
			okBtn.addActionListener(new ActionListener(){
			/* (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent arg0) {
			// send the selected cells and values to ESE_Frame
				sendSelectedCells();
				selectedCellsFrame.hide();
			}
			});
			JButton cancelBtn = new JButton("Cancel");
			cancelBtn.addActionListener(new ActionListener(){
				/* (non-Javadoc)
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent arg0) {
					selectedCellsFrame.hide();
				}

			});

			JPanel btnPanel = new JPanel();
			btnPanel.add(okBtn);
			btnPanel.add(cancelBtn);

			centerPanel.setLayout(new GridLayout(1,1));
			centerPanel.add(namePanel);
			centerPanel.add(valuesPanel);

			selectedCellsFrame.getContentPane().add(centerPanel,BorderLayout.CENTER);
			selectedCellsFrame.getContentPane().add(btnPanel,BorderLayout.SOUTH);
	}

	 private void sendSelectedCells() {

		   if(getUniversalToolProxy() == null){
			   JOptionPane.showMessageDialog (null, "Warning: The Connection to the Production System should be made before sending the selection elements. \n Open the Behavior Recorder to establish a connection.","Warning", JOptionPane.WARNING_MESSAGE);
		   }else{

			   // construct the Comm Message Containing the cell selections
	        	MessageObject mo = MessageObject.create("SendSelectedElements");
	        	mo.setVerb("SendSelectedElements");
	    		mo.setProperty("SelectedElements", selectedCellsList);
	    		mo.setProperty("SelectedElementsValues", selectedValues);

			   getUniversalToolProxy().sendMessage(mo);

		   }

   }
    
    public boolean getLock(String selection)
    {
        return ((JCommDocument) textField.getDocument()).locked;
    }
    //////////////////////////////////////////////////////
    /**
     * Used to process an InterfaceAction message
     */
    //////////////////////////////////////////////////////
    public void doInterfaceAction(String selection, String action, String input) {

    	// reset the border.
        changeBorder(null); //        changeBorder(originalBorder);
 
    	if (action.equalsIgnoreCase("UpdateTextField")) {
            setText(input);
            
            if (getController().isStartStateInterface()) {
                ((JCommDocument) textField.getDocument()).locked = true;
                textField.setHighlighter(null);
                setFocusable(false);
            }
            
            return;
        }
    	else if (SET_VISIBLE.equalsIgnoreCase(action)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		} 
    	else if (UPDATE_BACKGROUNDCOLOR.equalsIgnoreCase(action)) {
			trace.out("inter", "UPDATE_BACKGROUNDCOLOR: " + input);
			setBackground(C.get(input));
			
		} 
    	
        
        trace.out(5, this, "**Error**: don't know interface action " + action);
    }
    

    //////////////////////////////////////////////////////
    /**
     * Returns a comm message which describes this interface
     * element.
     */
    //////////////////////////////////////////////////////
	public MessageObject getDescriptionMessage() {

		MessageObject mo = MessageObject.create("InterfaceDescription");

		if (!initialize(getController())) {
			trace
					.out(
							5,
							this,
							"ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return null;
		}

		mo.setVerb("SendNoteProperty");

		mo.setProperty("WidgetType", "JCommTextField");
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
		
		String deftemplateStr = "(deftemplate textField (slot name) (slot value))";
		deftemplates.add(deftemplateStr);
		
		return deftemplates;
	}
	
	/* (non-Javadoc)
	 * @see pact.CommWidgets.JCommWidget#createJessInstances()
	 */
	public Vector createJessInstances() {
		Vector instances = new Vector();
		
		String instanceStr = "(assert (textField (name " + commName + ")))";
		instances.add(instanceStr);
		
		return instances;
	}
    
    
    // sanket@cs.wpi.edu

	public void highlight(String commComponentName, Border highlightBorder) {
		textField.setBorder(highlightBorder);
		setHighlighted(true);
	}
	
	public void highlight() {
		textField.setBorder(highlightedBorder);
		setHighlighted(true);
	}
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
	public void doCorrectAction(String selection, String action, String input) {
		

	        
		if (SET_VISIBLE.equalsIgnoreCase(action)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		}
	   else if (UPDATE_BACKGROUNDCOLOR.equalsIgnoreCase(action)) {
				trace.out("inter", "UPDATE_BACKGROUNDCOLOR: " + input);
				setBackground(C.get(input));
	
		} else {
			// Change the border to Correct Answer Color to represent correct
			// answer.
			changeBorder(correctBorder);

			textField.setForeground(correctColor);
			((JCommDocument) textField.getDocument()).locked = false;
			textField.setText(input);

			if (getController().getUniversalToolProxy().lockWidget()) {
				((JCommDocument) textField.getDocument()).locked = true;
				// textField.setEditable (false);
				textField.setHighlighter(null);

				// textField.setBackground(backgroundNormalColor);
				removeHighlight("");

				setFocusable(false);
			}

			if (correctFont != null)
				textField.setFont(correctFont);
		}
	}
    
    public void removeHighlight() {
    	textField.setBorder(originalBorder);
    	setHighlighted(false);
    }
    
    public void removeHighlight(String subElement) {
        //super.removeHighlight(subElement);
        textField.setBorder(originalBorder);
        setHighlighted(false);
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void doLISPCheckAction(String selection, String input) {
        // Change the border color to LISPCheckColor
        changeBorder(LISPCheckBorder);

        textField.setForeground(LISPCheckColor);
        textField.setText(input);
        
        if (getController().getUniversalToolProxy().lockWidget()) {
            ((JCommDocument) textField.getDocument()).locked = true;
            //textField.setEditable (false);
            textField.setHighlighter(null);

            //textField.setBackground(backgroundNormalColor);
            removeHighlight("");

            setFocusable(false);
        }
        
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void doIncorrectAction(String selection, String input) {
        // Change the border color to Incorrect Answer Color.
        changeBorder(inCorrectBorder);

        textField.setForeground(incorrectColor);
        textField.setText(input);
        ((JCommDocument) textField.getDocument()).locked = false;
        if (incorrectFont != null)
            textField.setFont(incorrectFont);
        //textField.setEditable (true);
        textField.setHighlighter(defaultHighlighter);
        setFocusable(true);
    }
    
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void setBackground(Color c) {
        super.setBackground(c);
        if (textField != null) {
        	textField.setOpaque(true);
            textField.setBackground(c);
        }
       
    }
    
    //////////////////////////////////////////////////////
    /**
     */
    //////////////////////////////////////////////////////
    public void setProperty(MessageObject o) {
    }
    
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void focusGained(FocusEvent e) {
        
        trace.out ("inter", "text field " + commName + " gained focus");
        if (!((JCommDocument) textField.getDocument()).locked) {
            previousValue = textField.getText();
            
            previousFont = textField.getFont();
            previousColor = textField.getForeground();
            
        }
        
        super.focusGained(e);
    }
    
    
    public void setFocus(String subWidgetName) {
        textField.requestFocus();
    }

    
  
    
    //////////////////////////////////////////////////////
    /**
                If focus lost permanently, and text has changed, send
                value to lisp
     */
    //////////////////////////////////////////////////////
    public void focusLost(FocusEvent e) {
     
        if (!((JCommDocument) textField.getDocument()).locked) {
            if (e.isTemporary() || !keyUsed)
                return;
            
            Component oppComponent = e.getOppositeComponent();
            if (trace.getDebugCode("inter")) trace.out("inter", "JCommTextField: focusLost oppComponent "+
            		(oppComponent instanceof JCommWidget ? ((JCommWidget) oppComponent).getCommName() : oppComponent));
            if (!focusTriggersBackGrading(oppComponent))
            	return;
            
            if (getController().getUniversalToolProxy().getAutoCapitalize() == true
				|| getAutoCapitalize() == true) {
                textField.setText(textField.getText().toUpperCase());
            }
            
            
            //if (!textField.getText().equals(previousValue))
//            if (!textField.getText().trim().equals(""))
//                dirty = true;
            

            
            dirty = true;            
            if (!textField.getText().equals(previousValue)) {
//              dirty = true;
              if (getController().isDefiningStartState())
            	  getController().setStartStateModified(true); // in case of delete cell value
            } 
           else if (textField.getText().equals("")) dirty = false; //  CTAT2066 -- Ctrl-click on state creates a new link

            
//            else
//            {
//            	textField.setFont(previousFont);
//            	textField.setForeground(previousColor);
//            }
            
            if (dirty)
                sendValue();
         
            previousValue = textField.getText();
        }
        
        super.focusLost(e);
        keyUsed = false;
        dirty = false;
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public Object getValue() {
        // Reset the border
        changeBorder(null); //        changeBorder(originalBorder);
        textField.setForeground(startColor);
        
        return textField.getText().trim();
    }
    
    
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public String getText() {
        return textField.getText();
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void setText(String text) {
        textField.setText(text);
        previousValue = text;
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void setFont(Font f) {
        startFont = f;
        
        if (textField != null)
            textField.setFont(f);
        super.setFont(f);
    }

    /**
     * Override {@link CommWidgets#setCorrectColor(Color)} to also change {@link #correctBorder}.
	 * @param correctColor new value for {@link #correctColor}
     */
    public void setCorrectColor(Color correctColor) {
        this.correctColor = correctColor;
		correctBorder = BorderFactory.createLineBorder(correctColor, 2);
    }

    /**
     * Override {@link CommWidgets#setIncorrectColor(Color)} to also change {@link #inCorrectBorder}.
	 * @param incorrectColor new value for {@link #incorrectColor}
     */
    public void setIncorrectColor(Color incorrectColor) {
        this.incorrectColor = incorrectColor;
		inCorrectBorder = BorderFactory.createLineBorder(incorrectColor, 2);
    }

    /**************************************************************************
     *   Getter and Setter methods for 'displayBorders' boolean variable.     *
     *   displayBorders is specifies whether or not border should be changed  *
     *   with corretc/incorrect answers.                                      *
     **************************************************************************/

    public boolean isShowBorder() {
        return showBorder;
    }

    public void setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
    }

    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public boolean isChangedFromResetState() {
        if (!textField.getText().equals(resetValue)) {
            ((JCommDocument) textField.getDocument()).locked = true;
            textField.setHighlighter(null);
            setFocusable(false);
            
            return true;
        }
        
        return false;
    }
    
    public boolean resetStartStateLock(boolean startStateLock) {
        if (!textField.getText().equals(resetValue)) {
        	System.out.println("Reset TextField [" + commName + "] - " + textField.getText() + startStateLock);
            ((JCommDocument) textField.getDocument()).locked = startStateLock;
            textField.setHighlighter(null);
            setFocusable(!startStateLock);
            
            return true;
        }
        
        return false;
    }

    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void reset(TutorController controller) {
        initialize(controller);
        ((JCommDocument)textField.getDocument()).locked = false;
        setFocusable(true);
        textField.setText(resetValue);
        //trace.out (5, this, "reset value = " + resetValue + " commName = " + commName  + " text field text = " + textField.getText());
        
        textField.setForeground(startColor);
        textField.setEditable(true);
        textField.setHighlighter(defaultHighlighter);

        // Reset the border
        changeBorder(null); //        changeBorder(originalBorder);
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void keyReleased(KeyEvent e) {
        keyUsed = true;
        if (e.getKeyCode() == 10) {
            //trace.out (5, this, "enter was pressed");
            getRootPane().requestFocus();
        }
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void keyTyped(KeyEvent e) {
        keyUsed = true;   
		
        if (!((JCommDocument) textField.getDocument()).locked) {
            textField.setForeground (startColor);
            // *** reset the border *** //
            changeBorder(null); //            changeBorder(originalBorder);
            if (startFont != null)
                textField.setFont (startFont);
        }
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void keyPressed(KeyEvent e) {
        keyUsed = true;
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void changedUpdate(DocumentEvent e) {
        //trace.out (5, this, "changedUpdate: event = " + e);
    }
    
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void insertUpdate(DocumentEvent e) {
        //trace.out (5, this, "insertUpdate: event = " + e);
        dirty = true;
        
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void removeUpdate(DocumentEvent e) {
        //trace.out (5, this, "removeUpdate: event = " + e);
        dirty = true;
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void setToolTipText(String text) {
        textField.setToolTipText(text);
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public String getToolTipText() {
        return textField.getToolTipText();
    }
    
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void addFocusListener(FocusListener l) {
        super.addFocusListener(l);
        if (textField != null)
            textField.addFocusListener(l);
    }
    //////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////
    public void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
        if (textField != null)
            textField.addMouseListener(l);
    }
    
    /***********************************************************************
     *  Method: changeborder(Border)                                       *
     *  Description:                                                       *
     *       Border is changed to show the correctness of the user value.  *
     *  If the user value is a correct answer, then this method is called  *
     *  by passing correctAnswerColor, which is usually Darker Green.      *
     *  Usually red color is used to demote the inCorrect answer. To reset *
     * the border 'null' object is passed.                                 *
     ***********************************************************************/
    public void changeBorder(Border border)
    {
        if(isShowBorder())
            this.setBorder(border);
    }

    
    private class MouseListenerClass implements MouseListener {

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent me) {

	    

	    if(me.isControlDown()){

		JCommTextField dt = (JCommTextField)me.getComponent().getParent();

		if(dt.selected){
		    dt.setBackground(Color.WHITE);
		    dt.selected = false;
		    selectedCellsList.remove(getCommNameToSend());
		    selectedValues.remove(getValue());
		    nameModel.removeElement(getCommNameToSend());
		    valuesModel.removeElement(getValue());
		}else{
		    dt.setBackground(Color.PINK);
		    dt.selected = true;
		    addToSelectedList(getCommNameToSend());
		    addToSelectedValues(getValue());
		}
		selectedCellsFrame.validate();
		selectedCellsFrame.pack();
		selectedCellsFrame.show();
	    }
	}

	/**
	 *
	 */
	private void clearSelectedValues() {
	    selectedValues.removeAll(selectedValues);
	}

	/**
	 * @param object
	 */
	private void addToSelectedValues(Object o) {
	    selectedValues.add(o);
	    valuesModel.addElement(o);
	}

	/**
	 *
	 */
	private void clearSelectedList() {
	    selectedCellsList.removeAll(selectedCellsList);
	}

	/**
	 * @param string
	 */
	private void addToSelectedList(String cellName) {
	    selectedCellsList.add(cellName);
	    nameModel.addElement(cellName);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
	}
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent arg0) {
	removeHighlight("");
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
    
    public String toString() {
        return "JCommTextField: " + textField.getText();
    }
}
