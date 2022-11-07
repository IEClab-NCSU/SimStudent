package pact.CommWidgets;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;

import pact.CommWidgets.event.IncorrectActionEvent;
import pact.CommWidgets.event.StudentActionEvent;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.EditLabelNameDialog;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;

//////////////////////////////////////////////////////
/**
 * A radio button that automatically tracks the user's actions
 */
//////////////////////////////////////////////////////
public class JCommRadioButton extends JCommWidget implements ActionListener {

//	private static final String UPDATE_TEXT = "UpdateText";
//	private static final String UPDATE_ICON = "UpdateIcon";
//	private static final String UPDATE_INVISIBLE = "UpdateInVisible";
	
	protected JRadioButton radioButton;

	protected boolean previousValue = false, resetValue = false;

	protected static Hashtable buttonGroupTable;

	protected static Hashtable buttonGroupMembersTable;

	protected Vector buttonGroupMembers = new Vector();

	protected String buttonGroupName;

	protected ButtonGroup buttonGroup;

	protected JPanel container;

	protected boolean actionFromBRFlag;

	protected boolean locked;

	/**
	 * Boolean variable that is used to make sure that only one widget is
	 * displayed for a correct action since doCorrectAction message is sent
	 * twice for some reason for one action.
	 */
	private boolean alreadyDone = false;

	protected boolean selected;

	static Vector selectedCellsList = new Vector();

	static Vector selectedValues = new Vector();

	static JFrame selectedCellsFrame;

	static JList nameList, valuesList;

	static DefaultListModel nameModel;

	static DefaultListModel valuesModel;

	private boolean useOldMessageFormat = true;
	
	protected String imageName;
	
    public ImageIcon image;

	public String imageFile = "NoImage.gif";
	
	//////////////////////////////////////////////////////
	/**
	 * Constructor
	 */
	//////////////////////////////////////////////////////
	public JCommRadioButton() {
		selected = false;
		radioButton = new JRadioButton();
		add(radioButton);
		GridLayout g = new GridLayout(1, 1);
		setLayout(g);

		try {
			radioButton.addActionListener(this);
			initialized = false;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		locked = false;

		actionName = UPDATE_RADIO_BUTTON;

		buttonGroupTable = new Hashtable();
		buttonGroupMembersTable = new Hashtable();

		backgroundNormalColor = radioButton.getBackground();

		addFocusListener(this);
		originalBorder = getBorder();
		buttonGroupName = "RadioGroup1";
		setText("Comm Radio Button");

		constructSelectedCellsFrame();
//		this.radioButton.addMouseListener(new MouseListenerClass());
		addFocusListener(this);
		addMouseListener(this);

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
		okBtn.addActionListener(new ActionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent arg0) {
				// send the selected cells and values to ESE_Frame
				sendSelectedCells();
				selectedCellsFrame.hide();
			}
		});
		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(new ActionListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent arg0) {
				selectedCellsFrame.hide();
			}

		});

		JPanel btnPanel = new JPanel();
		btnPanel.add(okBtn);
		btnPanel.add(cancelBtn);

		centerPanel.setLayout(new GridLayout(1, 1));
		centerPanel.add(namePanel);
		centerPanel.add(valuesPanel);

		selectedCellsFrame.getContentPane().add(centerPanel,
				BorderLayout.CENTER);
		selectedCellsFrame.getContentPane().add(btnPanel, BorderLayout.SOUTH);
	}

	private void sendSelectedCells() {

		if (getUniversalToolProxy() == null) {
			JOptionPane
					.showMessageDialog(
							null,
							"Warning: The Connection to the Production System should be made before sending the selection elements. \n Open the Behavior Recorder to establish a connection.",
							"Warning", JOptionPane.WARNING_MESSAGE);
		} else {

			// construct the Comm Message Containing the cell selections
        	MessageObject mo = MessageObject.create("SendSelectedElements");
        	mo.setVerb("SendSelectedElements");
    		mo.setProperty("SelectedElements", selectedCellsList);
    		mo.setProperty("SelectedElementsValues", selectedValues);

			getUniversalToolProxy().sendMessage(mo);

		}

	}

	//////////////////////////////////////////////////////
	/**
	 * Used to process an InterfaceAction message
	 */
	//////////////////////////////////////////////////////
	public void doInterfaceAction(String selection, String action, String input) {
		if (action.equalsIgnoreCase("UpdateRadioButton")) {

			actionFromBRFlag = true;
			radioButton.setEnabled(true);

			//radioButton.removeItemListener ((ItemListener) this);
			if (input.equalsIgnoreCase("true")) {
				previousValue = true;
				radioButton.setSelected(true);
			} else if (input.equalsIgnoreCase("false"))
				radioButton.setSelected(false);

			//radioButton.addItemListener ((ItemListener) this);

			// radioButton.setForeground(startColor);
			JCommRadioButton tempCommRadioButton;
			for (int i = 0; i < buttonGroupMembers.size(); i++) {
				tempCommRadioButton = (JCommRadioButton) buttonGroupMembers
						.elementAt(i);

				tempCommRadioButton.setEnabled(true);
				tempCommRadioButton.setLock(false);

				tempCommRadioButton.setForeground(startColor);
			}
			return;
		} else if (action.equalsIgnoreCase(UPDATE_TEXT)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "UpdateText: "+input);
			setText(input);
		} else if (action.equalsIgnoreCase(UPDATE_ICON)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "update Image: " + input);
			setImageFile(input);
//			image = oldcreateImageIcon(getImageFile().toString(), UPDATE_ICON);
		} else if (action.equalsIgnoreCase(UPDATE_INVISIBLE)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "Set InVisible: "+input);
			if (input.equalsIgnoreCase("true"))
				setInvisible(true);
			else setInvisible(false);
			setVisible(!isInvisible());
			// setInvisible(input);
		}

		trace.out(5, this, "**Error**: don't know interface action " + action);
	}

	//////////////////////////////////////////////////////
	/**
	 * Returns a comm message which describes this interface element.
	 */
	//////////////////////////////////////////////////////
	public MessageObject getDescriptionMessage() {

		MessageObject mo = MessageObject.create("InterfaceDescription");

		if (!initialize()) {
			trace
					.out(
							5,
							this,
							"ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return null;
		}

		mo.setVerb("SendNoteProperty");

		mo.setProperty("WidgetType", "JCommRadioButton");
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

		String deftemplateStr = "(deftemplate radioButton (slot name) (slot value))";
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

		String instanceStr = "(assert (radioButton (name " + commName + ")))";
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
			actionFromBRFlag = true;

			radioButton.setSelected(previousValue);
			radioButton.setForeground(correctColor);

			JCommRadioButton tempCommRadioButton;
			for (int i = 0; i < buttonGroupMembers.size(); i++) {
				tempCommRadioButton = (JCommRadioButton) buttonGroupMembers
						.elementAt(i);

				// set all of CommRadioButtons (with the same groupName)
				// locked.
				if (getController().getUniversalToolProxy().lockWidget())
					tempCommRadioButton.setLock(true);

				// set all of other radioButtons (with ths same groupName)
				// Foreground as startColor;
				if (tempCommRadioButton != this) {
					tempCommRadioButton.setForeground(startColor);

					if (getController().getUniversalToolProxy().lockWidget())
						tempCommRadioButton.setEnabled(false);
				}
			}

			actionFromBRFlag = false;
			if (trace.getDebugCode("inter")) trace.out("inter", "DRB.doCorrectAction: !" + this.alreadyDone);
			if (!this.alreadyDone) {
				// trace.out("firing studentAction in
				// JCommQuestionTextField: " + this.alreadyDone + "-
				// JCommQuestionTextField.java ");
				this.fireStudentAction(new StudentActionEvent(this));
				alreadyDone = true;
			}

		}
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void doLISPCheckAction(String selection, String input) {
		actionFromBRFlag = true;

		if (input.equalsIgnoreCase("true"))
			previousValue = true;
		else if (input.equalsIgnoreCase("false"))
			previousValue = false;

		radioButton.setSelected(previousValue);
		radioButton.setForeground(LISPCheckColor);

		// set all of other radioButtons (with ths same groupName) Foreground as
		// startColor;
		JCommRadioButton tempCommRadioButton;
		for (int i = 0; i < buttonGroupMembers.size(); i++) {
			tempCommRadioButton = (JCommRadioButton) buttonGroupMembers
					.elementAt(i);

			// set all of CommRadioButtons (with the same groupName) locked.
			if (getController().getUniversalToolProxy().lockWidget())
				tempCommRadioButton.setLock(true);

			if (tempCommRadioButton != this) {
				tempCommRadioButton.setForeground(startColor);
				if (getController().getUniversalToolProxy().lockWidget())
					tempCommRadioButton.setEnabled(false);
			}
		}
		actionFromBRFlag = false;

		this.alreadyDone = false;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void doIncorrectAction(String selection, String input) {
		actionFromBRFlag = true;

		if (input.equalsIgnoreCase("true"))
			previousValue = true;
		else if (input.equalsIgnoreCase("false"))
			previousValue = false;

		radioButton.setEnabled(true);
		radioButton.setSelected(previousValue);
		radioButton.setForeground(incorrectColor);

		// set all of other radioButtons (with ths same groupName) Foreground as
		// startColor;
		JCommRadioButton tempCommRadioButton;
		for (int i = 0; i < buttonGroupMembers.size(); i++) {
			tempCommRadioButton = (JCommRadioButton) buttonGroupMembers
					.elementAt(i);

			tempCommRadioButton.setEnabled(true);
			tempCommRadioButton.setLock(false);

			if (tempCommRadioButton != this)
				tempCommRadioButton.setForeground(startColor);
		}

		actionFromBRFlag = false;

		alreadyDone = false;
		fireIncorrectAction(new IncorrectActionEvent(this));

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
	public Object getValue() {
		//TODO make this check for old/new mode
		
		if (getActionName().equalsIgnoreCase(UPDATE_TEXT)
				&& getText() != null && getText().length() > 0)
			return getText();
		else if (getActionName().equalsIgnoreCase(UPDATE_ICON))
			return getImageFile();
		else if (getActionName().equalsIgnoreCase(UPDATE_INVISIBLE))
			return isInvisible();
		else if (useOldMessageFormat)
			return new Boolean (radioButton.isSelected()).toString();
		
		return commName;	
	}


	public String getCommNameToSend() {
		//TODO make this check for old/new mode
		if (useOldMessageFormat)
			return commName;
		
		return buttonGroupName;
	}
	
	public boolean getUseOldMessageFormat() {
		return useOldMessageFormat;
	}
	
	public void setUseOldMessageFormat(boolean useOldMessageFormat) {
		this.useOldMessageFormat = useOldMessageFormat;
	}
	
	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void setFont(Font f) {
		if (radioButton != null)
			radioButton.setFont(f);
		super.setFont(f);
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public boolean isChangedFromResetState() {
		return radioButton.isSelected();
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void reset(TutorController controller ) {
		actionFromBRFlag = false;
		initialize();
		//previousValue = true;

		JCommRadioButton tempCommRadioButton;
		for (int i = 0; i < buttonGroupMembers.size(); i++) {
			tempCommRadioButton = (JCommRadioButton) buttonGroupMembers
					.elementAt(i);

			tempCommRadioButton.setEnabled(true);
			tempCommRadioButton.setLock(false);

			tempCommRadioButton.setForeground(startColor);
		}

		radioButton.setSelected(resetValue);
		//		radioButton.setSelected (true);
		previousValue = resetValue;
		//radioButton.setForeground(startColor);

		locked = false;

		this.alreadyDone = false;

	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void actionPerformed(ActionEvent e) {
		/*
		removeHighlight("");
		//if (radioButton.isSelected() == true && previousValue == false &&
		// !locked) {
		if (getController().getCtatModeModel().isDefiningStartState()) {
			
		     JFrame frame = new JFrame("Modify Label Text");

		     String currentDir =  System.getProperty("user.dir");
		     String title = "Please set the Label for widget " + commName + " : ";
		     EditLabelNameDialog t = new EditLabelNameDialog(frame, title, getText(), getImageName(), currentDir, true);

		     if (!t.getNewLabel().equals(getText())) {
					setText(t.getNewLabel());
					dirty = true;
					setActionName(UPDATE_TEXT);
					sendValue(); 
				     }
		//	trace.out ("mps", "JCommButton New Label = " + t.getNewLabel());

		} else {*/
/*		
		if (!getController().getCtatModeModel().isDefiningStartState() && radioButton.isSelected() == true && !locked) {
			if (actionFromBRFlag)
				actionFromBRFlag = false;
			else {
				dirty = true;
				sendValue();
			}
			
		}

		previousValue = radioButton.isSelected();
		*/
//		}
	}
	


	//////////////////////////////////////////////////////
	/**
	 * The button group is what chains several Radio Buttons together in a
	 * mutually-exclusive group
	 */
	//////////////////////////////////////////////////////
	public void setGroup(String buttonGroupName) {
		if (trace.getDebugCode("mps")) trace.out("mps", "button group = " + buttonGroupName);

		this.buttonGroupName = buttonGroupName;
	}

	public boolean initialize() {
		boolean b = super.initialize(getController());
		updateGroup();
		return b;
	}

	//////////////////////////////////////////////////////
	/**
	 * Called during initialization
	 */
	//////////////////////////////////////////////////////
	private void updateGroup() {

		if (buttonGroupTable == null || buttonGroupName == null)
			return;

		if (buttonGroupTable.containsKey(buttonGroupName)) {
			ButtonGroup bg = (ButtonGroup) buttonGroupTable
					.get(buttonGroupName);
			bg.add(radioButton);
			buttonGroup = bg;
		} else {
			ButtonGroup bg = new ButtonGroup();
			bg.add(radioButton);
			buttonGroupTable.put(buttonGroupName, bg);
			buttonGroup = bg;

		}

		// add buttonGroupMembersTable to trace all of CommradioButtons with
		// the same buttonGroupName
		if (buttonGroupMembersTable.containsKey(buttonGroupName)) {
			Vector bGroupMembers = (Vector) buttonGroupMembersTable
					.get(buttonGroupName);
			bGroupMembers.addElement(this);
			buttonGroupMembers = bGroupMembers;
		} else {
			Vector bGroupMembers = new Vector();
			bGroupMembers.addElement(this);
			buttonGroupMembersTable.put(buttonGroupName, bGroupMembers);
			buttonGroupMembers = bGroupMembers;
		}

	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public String getGroup() {
		return buttonGroupName;
	}

	public boolean getLock(String selection) {
		return locked;
	}

	public void setLock(boolean lockFlag) {
		this.locked = lockFlag;
	}

	//////////////////////////////////////////////////////
	/**
		Creates a vector of comm messages which describe the
		current state of this object relative to the start state
	*/
	//////////////////////////////////////////////////////
	public Vector getCurrentState() {

		Vector v = new Vector();
		
		JCommRadioButton tempCommRadioButton;
		for (int i = 0; i < buttonGroupMembers.size(); i++) {   // work, but have redundant messages, need to be fixed later
			tempCommRadioButton = (JCommRadioButton) buttonGroupMembers
					.elementAt(i);
			
			if (tempCommRadioButton.getImageFile() != null) {
				tempCommRadioButton.setActionName(UPDATE_ICON);
				v.addElement(tempCommRadioButton.getCurrentStateMessage());
			}
		    if (tempCommRadioButton.getText() != null && tempCommRadioButton.getText().length() > 0) {
				tempCommRadioButton.setActionName(UPDATE_TEXT);
				v.addElement(tempCommRadioButton.getCurrentStateMessage());
			}  
			if (isInvisible()) {

				setActionName(UPDATE_INVISIBLE);
				v.addElement(getCurrentStateMessage());
			} 
		    setActionName("UpdateRadioButton"); // set to default action to get default input (-1)

		}
		

		return v;
	}
	
	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void setText(String text) {
		radioButton.setText(text);
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public String getText() {
		return radioButton.getText();
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void setSelected(boolean flag) {
		radioButton.setSelected(flag);
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public boolean getSelected() {
		return radioButton.isSelected();
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void setBackground(Color c) {
		super.setBackground(c);
		if (radioButton != null)
			radioButton.setBackground(c);
		backgroundNormalColor = c;
	}

	public void setBackgroundTemporary(Color c) {
		super.setBackground(c);
		if (radioButton != null)
			radioButton.setBackground(c);
	}

	public void setForeground(Color c) {
		super.setForeground(c);
		if (radioButton != null)
			radioButton.setForeground(c);
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void setToolTipText(String text) {
		radioButton.setToolTipText(text);
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public String getToolTipText() {
		return radioButton.getToolTipText();
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void addFocusListener(FocusListener l) {
		super.addFocusListener(l);
		if (radioButton != null)
			radioButton.addFocusListener(l);
	}

	
	//////////////////////////////////////////////////////
	/**
	 * radioButton highlight should be in the group level set all of
	 * radioButtons (with ths same groupName) highlight
	 */
	//////////////////////////////////////////////////////
	public void startHighlight(String subElement) {

		JCommRadioButton tempCommRadioButton;
		for (int i = 0; i < buttonGroupMembers.size(); i++) {
			tempCommRadioButton = (JCommRadioButton) buttonGroupMembers
					.elementAt(i);
			HighlightThread t = new HighlightThread(tempCommRadioButton,
					tempCommRadioButton.getCommName());
			t.start();
		}
	}

	public void focusGained(FocusEvent e) {
		super.focusGained(e);
	}

	// for tutor mode highlight in group
	public void highlight(String commComponentName, Border highlightBorder) {
		if (trace.getDebugCode("mps")) trace.out("mps", "highlight");
		JCommRadioButton tempCommRadioButton;
		for (int i = 0; i < buttonGroupMembers.size(); i++) {
			tempCommRadioButton = (JCommRadioButton) buttonGroupMembers
					.elementAt(i);
			if (trace.getDebugCode("mps")) trace.out("mps", "radio button = "
					+ tempCommRadioButton.getCommName());
			tempCommRadioButton.setBorder(highlightBorder);
		}
	}

	// for tutor mode none highlight in group
	public void removeHighlight(String commComponentName) {
		JCommRadioButton tempCommRadioButton;
		for (int i = 0; i < buttonGroupMembers.size(); i++) {
			tempCommRadioButton = (JCommRadioButton) buttonGroupMembers
					.elementAt(i);
			tempCommRadioButton.setBorder(originalBorder);
		}
	}

	public void setEnabled(boolean b) {
		radioButton.setEnabled(b);
	}

	public void addMouseListener(MouseListener l) {
		if (radioButton != null)
			radioButton.addMouseListener(l);

	}

	public void addMouseMotionListener(MouseMotionListener l) {
		if (radioButton != null)
			radioButton.addMouseMotionListener(l);
	}

	public MouseListener[] getMouseListeners() {
		if (radioButton != null)
			return radioButton.getMouseListeners();
		return null;
	}

	public MouseMotionListener[] getMouseMotionListeners() {
		if (radioButton != null)
			return radioButton.getMouseMotionListeners();
		return null;
	}

	public void removeMouseMotionListener(MouseMotionListener l) {
		if (radioButton != null)
			radioButton.removeMouseMotionListener(l);
	}

	public void removeMouseListener(MouseListener l) {
		if (radioButton != null)
			radioButton.removeMouseListener(l);
	}

	private class MouseListenerClass implements MouseListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent me) {
			if (me.isControlDown()) {

				JCommRadioButton dt = (JCommRadioButton) me.getComponent()
						.getParent();

				if (dt.selected) {
					dt.setBackground(Color.WHITE);
					dt.selected = false;
					selectedCellsList.remove(getCommNameToSend());
					selectedValues.remove(getValue());
					nameModel.removeElement(getCommNameToSend());
					valuesModel.removeElement(getValue());
				} else {
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		public void mouseEntered(MouseEvent arg0) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		public void mouseExited(MouseEvent arg0) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent arg0) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent arg0) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
		if (trace.getDebugCode("dw")) trace.out("dw", "mouseClicked: isDefiningStartState = " + getController().isDefiningStartState()
				+ " isSelected = " + radioButton.isSelected() + " locked = " + locked
				+ " actionFromBRFlag = " + actionFromBRFlag);
		removeHighlight("");
		if (arg0.isControlDown() && getController().isDefiningStartState()) {
			if (trace.getDebugCode("dw")) trace.out("dw", " in mouseClicked first branch");
		     JFrame frame = new JFrame("Modify Label Text");

		     String currentDir =  System.getProperty("user.dir");
		     String title = "Please set the Label for widget " + commName + " : ";
		     EditLabelNameDialog t = new EditLabelNameDialog(frame, title, getText(), imageFile, currentDir, isInvisible(), true);

		     if (!t.getNewLabel().equals(getText())) {
					setText(t.getNewLabel());
					dirty = true;
					setActionName(UPDATE_TEXT);
					sendValue(); 
				     }
		     
				if (t.getIcon() != null) {
					// setIcon(t.getIcon());
//					setImageFile(t.getImageName()); // t.getIcon().toString());
					setImage(t.getIcon());
					this.imageFile = t.getImageName();
	// System.err.println("imageFile = " + imageFile);
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
		} else if (!getController().isDefiningStartState() && radioButton.isSelected() == true && !locked) {
			if (trace.getDebugCode("dw")) trace.out("dw", " in mouseClicked second branch");
		    if (actionFromBRFlag)
				actionFromBRFlag = false;
			else {
				dirty = true;
				setActionName("UpdateRadioButton");
				sendValue();
			}
			previousValue = radioButton.isSelected();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setIcon(Icon icon) {
		if (radioButton != null)
			radioButton.setIcon(icon);
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public Icon getIcon() {
		if (radioButton != null)
			return radioButton.getIcon();
		else
			return null;
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public String getImageName() {
		return imageName;
	}
	
	private ImageIcon getImage() {
		return image;
	}

	public void setImage(ImageIcon image) {
		trace.out("image = " + image);
		this.image = image;
//		this.repaint();
	}
	
	public String getImageFile() {
		return imageFile;
	}

	public void setImageFile(String imageFile) {
		if (imageFile != null)
		  this.imageFile = imageFile;
		else this.imageFile = "NoImage";

		trace.out("imageFile = " + imageFile);
		setImage(loadImage(imageFile));

	}
	
	protected ImageIcon loadImage(String imageName) {

		if (imageName == null || imageName.length() < 1)
			return null;

		File imgFile = null;
		URL imageURL = null;

		try {
			if (!imageName.startsWith("file:")) {
				// Try to get image from physical path (from browser) or
				// relative path
				imgFile = new File(imageName);
				if (imgFile.exists()) {
					// String newName = imgFile.getCanonicalPath().replace('\\',
					// '/');
					System.err.println("Creat icon from physical/relative address");
					// return new ImageIcon(newName);
					return new ImageIcon(imageName);
				}
			}
		} catch (Exception e) {
			trace.err("Can't find file " + imageName);
		}

		try {
			// Try to get image from URL address which start with file:
			imageURL = new URL(imageName);
			System.err.println("Creat icon from URL address");
		} catch (MalformedURLException mal) {
			if (trace.getDebugCode("log")) trace.out("log", "MalformedURLException message = "
					+ mal.getMessage());
		}

		if (imageURL == null) {
			// Get image from current directory
			imageURL = Utils.getURL(imageName, this);
			System.err.println("Creat icon from resource .jar file");
		}

		if (imageURL == null) {
			// Get image from resource .jar file ??? may be redundant, need to verified from webstart
			imageURL = JCommPicture.class.getResource(imageName);
			System.err.println("Creat icon from resource .jar file");
		}

		if (imageURL == null) {
			trace.err("Error: cannot find image "
					+ new File(imageName).getAbsolutePath());

			return null;
		}

		Image image = Toolkit.getDefaultToolkit().getImage(imageURL);
		/*
		 * if (new File(imageName).canRead()) image =
		 * Toolkit.getDefaultToolkit().getImage(imageName); else { imageURL =
		 * Utils.getURL(imageName, this); if (imageURL != null) image =
		 * Toolkit.getDefaultToolkit().getImage(imageURL); else { // try to load
		 * resource which contained in .jar file imageURL =
		 * Thread.currentThread().getContextClassLoader()
		 * .getResource(imageName);
		 * 
		 * if (imageURL == null) { trace.err("Error: cannot find image " + new
		 * File(imageName).getAbsolutePath());
		 * 
		 * return null; } } }
		 */
		ImageIcon imageIcon = new ImageIcon(image, imageName);

		return imageIcon;
	}

	protected ImageIcon oldloadImage(String imageName) {

		if (imageName == null || imageName.length() < 1) 	return null;
		URL imageURL = null;


		try {
		if (imageName.startsWith("file:")) 
			imageURL = new URL(imageName);
		else 
			imageURL = Utils.getURL(imageName, this);     // Get image from current directory
		} catch (MalformedURLException mal) {

			if (trace.getDebugCode("log")) trace.out("log", "MalformedURLException message = "
					+ mal.getMessage());
		};
		
		if (imageURL == null) 
			imageURL = JCommPicture.class.getResource(imageName); // Get image from resource .jar file
		
		if (imageURL == null)  {
			trace.err("Error: cannot find image "
					+ new File(imageName).getAbsolutePath());

			return null;
		};

		Image image = Toolkit.getDefaultToolkit().getImage(imageURL);		
/* 		if (new File(imageName).canRead())
			image = Toolkit.getDefaultToolkit().getImage(imageName);
 		else {
			imageURL = Utils.getURL(imageName, this);
			if (imageURL != null)
				image = Toolkit.getDefaultToolkit().getImage(imageURL);
			else { // try to load resource which contained in .jar file
				imageURL = Thread.currentThread().getContextClassLoader()
						.getResource(imageName);

				if (imageURL == null) {
					trace.err("Error: cannot find image "
							+ new File(imageName).getAbsolutePath());

					return null;
				}
			}
		}*/
 		ImageIcon imageIcon = new ImageIcon(image, imageName);
 		
 		return imageIcon;
	}
}
