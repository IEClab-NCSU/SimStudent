package pact.CommWidgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.net.URL;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import pact.CommWidgets.event.HelpEvent;
import pact.CommWidgets.event.StudentActionEvent;
import edu.cmu.pact.ctat.MessageObject;     // import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.BehaviorRecorder.Dialogs.EditLabelNameDialog;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.Hints;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

public class JCommButton extends JCommWidget  implements ActionListener {

	/** Property name for button label. */
	private static final String TEXT = "Text";

	/** Property name for button icon. */
	private static final String ICON = "Icon";

	//	private static final String UPDATE_ICON = "UpdateIcon";
//	private static final String UPDATE_TEXT = "UpdateText";
//	public static final String BUTTON_PRESSED = "ButtonPressed";
//	private static final String UPDATE_INVISIBLE = "UpdateInVisible";
	protected JButton button;
	protected JPanel container;
	protected boolean changeColor;
	protected Font startFont;
	protected boolean locked;
	protected String imageName;
	//public HintMessagesManager messagesManager;
	
	//////////////////////////////////////////////////////
	/**
		Constructor
	*/
	//////////////////////////////////////////////////////
	public JCommButton() {
		//messagesManager= new HintMessagesManager(null);
		setLayout(new GridLayout(1, 1));
		button = new JButton();
		button.addActionListener(this);
		add(button);

		backgroundNormalColor = button.getBackground();

		actionName = JCommButton.BUTTON_PRESSED;

		locked = false;

		changeColor = true;
		addFocusListener(this);
		addMouseListener(this);
		originalBorder = button.getBorder();
		setText ("Comm Button");
	}
        

	//////////////////////////////////////////////////////
	/**
		Returns a comm message which describes this interface
		element.
	*/
	//////////////////////////////////////////////////////
	public MessageObject getDescriptionMessage() {

		if (!initialize(getController())) {
			trace.out(
				5,
				this,
				"ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return null;
		}

        MessageObject mo = MessageObject.create("InterfaceDescription");
        mo.setVerb("SendNoteProperty");

		mo.setProperty("WidgetType", "JCommButton");
		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));
		mo.setProperty(TEXT, getText());
		mo.setProperty(ICON, getImageName());
		
		Vector deftemplates = createJessDeftemplates();
		Vector instances = createJessInstances();
		
		if(deftemplates != null)  mo.setProperty("jessDeftemplates", deftemplates);

		
		if(instances != null)    mo.setProperty("jessInstances", instances);

		
		serializeGraphicalProperties(mo);

		return mo;
	}

	

	/* (non-Javadoc)
	 * @see pact.CommWidgets.JCommWidget#createJessDeftemplates()
	 */
	public Vector createJessDeftemplates() {
		Vector deftemplates = new Vector();
		
		String deftemplateStr = "(deftemplate button (slot name))";
		deftemplates.add(deftemplateStr);
		
		return deftemplates;
	}
	
	/* (non-Javadoc)
	 * @see pact.CommWidgets.JCommWidget#createJessInstances()
	 */
	public Vector createJessInstances() {
		Vector instances = new Vector();
		
		String instanceStr = "(assert (button (name " + commName + ")))";
		instances.add(instanceStr);
		
		return instances;
	}

	public void addMouseListener(MouseListener l) {
		if (button != null)
			button.addMouseListener(l);
	}

	public void addMouseMotionListener(MouseMotionListener l) {
		if (button != null)
			button.addMouseMotionListener(l);
	}

	public MouseListener[] getMouseListeners() {
		if (button != null)
			return button.getMouseListeners();
		return null;
	}

	public MouseMotionListener[] getMouseMotionListeners() {
		if (button != null)
			return button.getMouseMotionListeners();
		return null;
	}

	public void removeMouseMotionListener(MouseMotionListener l) {
		if (button != null)
			button.removeMouseMotionListener(l);
	}

	public void removeMouseListener(MouseListener l) {
		if (button != null)
			button.removeMouseListener(l);
	}

	/**
     * Used to process an InterfaceDescription message.
     * @param messageObject
     */
    public void doInterfaceDescription(MessageObject messageObject) {
    	super.doInterfaceDescription(messageObject);
    	Object prop;
    	if((prop = messageObject.getProperty(TEXT)) != null)
    		setText(prop.toString());
    	if((prop = messageObject.getProperty(ICON)) != null)
    		setImageName(prop.toString());
    }

	//////////////////////////////////////////////////////
	/**
		Used to process an InterfaceAction message
	*/
	//////////////////////////////////////////////////////
	public void doInterfaceAction(
		String selection,
		String action,
		String input) {

		if (action.equalsIgnoreCase(UPDATE_ICON)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "Load icon: " + input);
			setImageName(input);
		}
	    if (action.equalsIgnoreCase(UPDATE_TEXT)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "UpdateText: "+input);
			setText(input);
		}
	    if (action.equalsIgnoreCase(UPDATE_INVISIBLE)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "Set InVisible: "+input);
			if (input.equalsIgnoreCase("true"))
				setInvisible(true);
			else setInvisible(false);
			setVisible(!isInvisible());
			// setInvisible(input);
		}
		else if (SET_VISIBLE.equalsIgnoreCase(action)) {   // suppress feedback
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		}
		
	}

	public void doLISPCheckAction(String selection, String input) {
		//if (changeColor && !commName.equalsIgnoreCase("Done"))
		if (changeColor)
			button.setForeground(LISPCheckColor);
                
		if (getController()!=null && getController().getUniversalToolProxy().lockWidget()) {
			if (!(commName.equalsIgnoreCase("Hint")
				|| commName.equalsIgnoreCase("Help")
				|| commName.equalsIgnoreCase("Done"))) {
                                setFocusable(false);
				locked = true;
                        }
		}
	}

	public void setChangeButtonColor(boolean changeFlag) {
		this.changeColor = changeFlag;

	}

	public boolean getChangeButtonColor() {
		return this.changeColor;
	}

	public void doCorrectAction(String selection, String action, String input) {
		    	
		if (SET_VISIBLE.equalsIgnoreCase(action)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		} else {
			if (changeColor) {
				// if (!commName.equalsIgnoreCase("Done"))
				button.setForeground(correctColor);
				
				if (correctFont != null)
					button.setFont(correctFont);
			}
			
			if (getController() != null
					&& getController().getUniversalToolProxy().lockWidget()) {
				if (!(commName.equalsIgnoreCase("Hint")
						|| commName.equalsIgnoreCase("Help") || commName
						.equalsIgnoreCase("Done"))) {		    	
					setFocusable(false);
					locked = true;
				}
			}

			// Added 9/9/06 for CL 2006 integration.
			fireStudentAction(new StudentActionEvent(this));
		}
	}

	public void doIncorrectAction(String selection, String input) {
		if (changeColor) {
			button.setForeground(incorrectColor);

			if (incorrectFont != null)
				button.setFont(incorrectFont);
		}

		if (locked)
			locked = false;
                
                setFocusable(true);
	}

	//////////////////////////////////////////////////////
	/**
		Called when button pressed.  Sends event to Lisp.
	*/
	//////////////////////////////////////////////////////
	public void actionPerformed(ActionEvent e) {
		trace.out ("sp", "action performed");
	    removeHighlight(commName);
	    
	    
	    if (commName==null)
	    	commName=this.getName();
	    	
		if (locked)
			return;
/*
		if (getController()!=null && getController().getCtatModeModel().isDefiningStartState()) {
			
		     JFrame frame = new JFrame("Modify Label Text");

		     String currentDir =  System.getProperty("user.dir");
		     
		     String title = "Please set the Label for widget " + commName + " : ";
		     EditLabelNameDialog t = 
		 //   	 new EditLabelNameDialog(frame, title, getText(), getIcon(), currentDir, true);
		     new EditLabelNameDialog(frame, title, getText(), getImageName(), currentDir, isInvisible(), true);

//		     trace.out("inter",">>> " +  getText() + " -> " + t.getNewLabel());
//		     if (t.getIcon() != null) 
//		    	 trace.out("inter",">>> " +  getIcon() + " -> " + t.getIcon().toString());
		     
		     if (!t.getNewLabel().equals(getText())) {
			setText(t.getNewLabel());
			dirty = true;
			setActionName(UPDATE_TEXT);
			sendValue(); 
		     }
			
			if (t.getIcon() != null) {
			setIcon(t.getIcon());
			setImageName(t.getIcon().toString());
			
			dirty = true;
			setActionName(UPDATE_ICON);
			sendValue();
			}
			if (isInvisible()  !=  t.isInvisible()) {
				setInvisible(t.isInvisible());
				dirty = true;
				setActionName(UPDATE_INVISIBLE);
				sendValue();
			}
		//	trace.out ("mps", "JCommButton New Label = " + t.getNewLabel());
			setActionName(BUTTON_PRESSED); // set to default action to get default input (-1)
		} else {
		*/
			dirty = true;
			sendValue();
			if (this.commName.equalsIgnoreCase("hint") || this.commName.equalsIgnoreCase("help")) {
				this.fireHelpEvent(new HelpEvent(this));
//			}
		}
	}

	//////////////////////////////////////////////////////
	/**
		Return true if any cells are not empty, otherwise false
	*/
	//////////////////////////////////////////////////////
	public boolean isChangedFromResetState() {
		if (getImageName() != null)
			return true;
		else if (getText() != null && getText().length() > 0)
			return true;
		else
			return false;
	}
	
	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////

	
	public Object getValue() {
		String tempValue = getText();

		if (getActionName().equalsIgnoreCase(UPDATE_ICON)
				&& getImageName() != null)
			return imageName;

		else if (getActionName().equalsIgnoreCase(UPDATE_TEXT)
				&& getText() != null && getText().length() > 0)
			return getText();
		else if (getActionName().equalsIgnoreCase(UPDATE_INVISIBLE))
			return isInvisible();
		else {
			if (commName.equalsIgnoreCase("Hint")
					|| commName.equalsIgnoreCase("Help")
					|| commName.equalsIgnoreCase("Done"))

				return "-1";

			else
				return tempValue;
		}

	}

	/**
	 * Set the button back to its starting condition: <ul>
	 * <li>{@link #button}.{@link JButton#setForeground(Color) setForeground}({@link #startColor})</li>
	 * <li>{@link #button}.{@link JButton#setFont(Font) setFont}({@link #startFont})</li>
	 * <li>clears {@link #locked}</li>
	 * <li>{@link #setFocusable(boolean) setFocusable(true)}</li>
	 * </ul>
	 * @param controller if not null, also call {@link #initialize(TutorController)}
	 * @see pact.CommWidgets.JCommWidget#reset(edu.cmu.pact.ctat.TutorController)
	 */
	public void reset(TutorController controller) {
		if (controller != null)
			initialize(controller);

		if (changeColor) {
			button.setForeground(startColor);

			if (startFont != null)
				button.setFont(startFont);
		}
		locked = false;
		setFocusable(true);
	}

	public boolean getLock(String selection) {
		return locked;
	}
	
	//////////////////////////////////////////////////////
	/**
		Creates a vector of comm messages which describe the
		current state of this object relative to the start state
	*/
	//////////////////////////////////////////////////////
	public Vector getCurrentState() {

		Vector v = new Vector();

	    if (getText() != null && getText().length() > 0) {
	    	setActionName(UPDATE_TEXT);
			v.addElement(getCurrentStateMessage());
	    }
		if (imageName != null) {
			setActionName(UPDATE_ICON);
			v.addElement(getCurrentStateMessage());
		}
		if (isInvisible()) {

			setActionName(UPDATE_INVISIBLE);
			v.addElement(getCurrentStateMessage());
		}
		setActionName(BUTTON_PRESSED); // set to default action to get default input (-1)

		return v;
	}

	/**
	 * Set the label on the button. Side effect: if text matches {@link Hints#isHintLabel(String)},
	 * calls {@link #setHintBtn(boolean) setHintBtn(true)}.
	 * @param text
	 */
	public void setText(String text) {
		button.setText(text);
    	if (Hints.isHintLabel(text))
    		setHintBtn(true);
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public String getText() {
		return button.getText();
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setToolTipText(String text) {
		button.setToolTipText(text);
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public String getToolTipText() {
		return button.getToolTipText();
	}
	//////////////////////////////////////////////////////
	/**
	 */
	//////////////////////////////////////////////////////
	public void highlight(String subElement, Border highlightBorder) {
	    	button.setBorder(highlightBorder);
	}

	//////////////////////////////////////////////////////
	/**
	 */
	//////////////////////////////////////////////////////
	public void removeHighlight(String subElement) {
	    button.setBorder(originalBorder);
	}
	

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setImageName(String imageName) {
		this.imageName = imageName;
		if (imageName == null || imageName.length() < 1) {
			if (button != null)
				button.setIcon(null);
//			setActionName(UPDATE_TEXT);
			return;
		}
		
		Image image = null;
 		if (new File(imageName).canRead())
			image = Toolkit.getDefaultToolkit().getImage(imageName);
 		else {
 			URL imageURL = Utils.getURL(imageName, this);
 			if (imageURL != null)
				image = Toolkit.getDefaultToolkit().getImage(imageURL);
 			else {
 				trace.err("Error: cannot find icon " + imageName);
 				return;
 			}
 		}
 		
		ImageIcon icon = new ImageIcon(image, imageName);
		if (button != null) {
			button.setIcon(icon);
//			setActionName(UPDATE_ICON);
		}
	}
	
	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public String getImageName() {
		return imageName;
	}
	
	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setFocus(String subWidgetName) {
		button.requestFocus();
		return;
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setBackground(Color c) {
		if (button != null)
			button.setBackground(c);
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setIcon(Icon icon) {
		if (button != null)
			button.setIcon(icon);
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public Icon getIcon() {
		if (button != null)
			return button.getIcon();
		else
			return null;
	}

        
        public void moveFocus() {
            button.transferFocus();
        }
	
        
        //////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setFont(Font f) {
		if (f != null) {
			startFont = f;
		} else
			startFont = super.getFont();

		super.setFont(startFont);

		if (button != null)
			button.setFont(startFont);
	}

	/**
	 * Set the {@link #button} enabled or disabled. Also calls superclass method.
	 * @param enabled true=>enabled, false=>disabled
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (button != null)
			button.setEnabled(enabled);		
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void addFocusListener(FocusListener l) {
		super.addFocusListener(l);
		if (button != null)
			button.addFocusListener(l);
	}


    public void doClick() {
        button.doClick();
    }
	
	
	public void mousePressed(MouseEvent e) {
		// trace.out("dw", "Enter ModifyListsListener mousePressed");

		if (locked)
			return;

		if (e.isControlDown() && getController()!=null && getController().isDefiningStartState()) {

			JFrame frame = new JFrame("Modify Label Text");

			String currentDir =  System.getProperty("user.dir");

			String title = "Please set the Label for widget " + commName + " : ";
			EditLabelNameDialog t = 
					//   	 new EditLabelNameDialog(frame, title, getText(), getIcon(), currentDir, true);
					new EditLabelNameDialog(frame, title, getText(), getImageName(), currentDir, isInvisible(), true);

			//		     trace.out("inter",">>> " +  getText() + " -> " + t.getNewLabel());
			//		     if (t.getIcon() != null) 
			//		    	 trace.out("inter",">>> " +  getIcon() + " -> " + t.getIcon().toString());
			
			boolean sendLabel = (!t.getNewLabel().equals(getText()));
			boolean sendIcon = (t.getIcon() != null);
			boolean sendVisible = (isInvisible()  !=  t.isInvisible());
			if(sendLabel || sendIcon || sendVisible)
				getUniversalToolProxy().sendMessage(getDescriptionMessage());

			if (sendLabel) {
				setText(t.getNewLabel());
				dirty = true;
				setActionName(UPDATE_TEXT);
				sendValue(); 
			}
			if (sendIcon) {
				setIcon(t.getIcon());
				setImageName(t.getIcon().toString());

				dirty = true;
				setActionName(UPDATE_ICON);
				sendValue();
			}
			if (sendVisible) {
				setInvisible(t.isInvisible());
				dirty = true;
				setActionName(UPDATE_INVISIBLE);
				sendValue();
			}
			//	trace.out ("mps", "JCommButton New Label = " + t.getNewLabel());
			setActionName(BUTTON_PRESSED); // set to default action to get default input (-1)
		} 
	}
    
    // ////////////////////////////////////////////////////
    /**
     * The commName is the string used in Comm messages to refer to this
     * object.
     * @param controller 
     */
    // ////////////////////////////////////////////////////
    public void setCommName(String commName, TutorController controller) {
    	super.setCommName(commName, controller);
    	if (Hints.isHintLabel(commName))
    		setHintBtn(true);
    }
	
    /**
     * @param isHintBtn
     *            The isHintBtn to set.
     */
    public void setHintBtn(boolean isHintBtn) {
    	super.setHintBtn(isHintBtn);
    }
}
