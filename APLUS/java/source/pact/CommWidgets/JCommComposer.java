package pact.CommWidgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import edu.cmu.pact.ctat.MessageObject;     // import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.EditListItemDialog;
import edu.cmu.pact.Utilities.ComponentDescription;
import edu.cmu.pact.Utilities.StringTokenizerItemValues;
import edu.cmu.pact.Utilities.trace;

//////////////////////////////////////////////////////
/**
 * This class will automatically send user's input to Lisp.
 */
// ///////////////////////////////////////////////////
public class JCommComposer extends JCommWidget implements ActionListener,	ItemListener, MouseListener {
	protected String newValue;
	protected JList sentenceJList;
	protected int sizeOfSentenceJList;
	protected JScrollPane listScrollPane;
	protected Vector sentenceJListColors;

	protected JComboBox composeJComboBox[];

	protected int comboBoxesWidth = 0;

	protected String comboBoxesWidths = "0";
	protected int comboBoxesWidths_Number = 0;
	protected int comboBoxWidthsArray[];

	// upon Ben's request for Genetic Tutor
	protected int maxRowItemsView = 40;

	protected int sentencePanelHeight = 60;

	protected int comboBoxesNumber = 4;

	protected String itemValues = "";

	protected String values[];

	protected boolean enableItemToolTips = false;

	protected JButton addJButton = new JButton("  Add  ");
	protected JButton deleteJButton = new JButton("  Delete  ");

	protected JPanel selectsPanel = new JPanel();
	protected JPanel comboBoxesPanel = new JPanel();
	protected JPanel buttonsPanel = new JPanel();
	protected Font startFont;
	protected boolean locked;
	protected boolean setValueDone = true;
	protected boolean allowDeletes = false;

	// ////////////////////////////////////////////////////
	/**
	 * Constructor
	 */
	// ////////////////////////////////////////////////////
	public JCommComposer() {
		sentenceJList = new JList();
		listScrollPane = new JScrollPane(sentenceJList);
		sizeOfSentenceJList = 0;
		sentenceJListColors = new Vector();

		comboBoxesPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		setLayout(new BorderLayout());
		add(listScrollPane, BorderLayout.NORTH);
		add(comboBoxesPanel, BorderLayout.CENTER);

		setSize(new Dimension(500, 120));
		createButtonPanel();
		createComboBoxes();
		sentenceJList.setCellRenderer(new MyCellRenderer());

		backgroundNormalColor = sentenceJList.getBackground();
		DefaultListModel lm = new DefaultListModel();
		sentenceJList.setModel(lm);
		addJButton.addActionListener(this);
		deleteJButton.addActionListener(this);

		locked = false;

		setSentencePanelHeight(this.sentencePanelHeight);

		addFocusListener(this);
	}

	public void setSize(Dimension d) {
		this.setPreferredSize(d);
	}

	public void setEnableItemToolTips(boolean enableItemToolTips) {
		this.enableItemToolTips = enableItemToolTips;
	}

	public boolean getEnableItemToolTips() {
		return this.enableItemToolTips;
	}

	protected void createButtonPanel() {
		if (buttonsPanel != null)
			buttonsPanel.removeAll();

		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		add(buttonsPanel, BorderLayout.SOUTH);
		if (allowDeletes) {
			buttonsPanel.add(deleteJButton);
			if (enableItemToolTips)
				deleteJButton.setToolTipText("Remove the selected line");
		}

		buttonsPanel.add(addJButton);

	}

	protected void createComboBoxes() {
		if (comboBoxesPanel != null)
			comboBoxesPanel.removeAll();

		composeJComboBox = new JComboBox[comboBoxesNumber];
		values = new String[comboBoxesNumber];

		for (int i = 0; i < comboBoxesNumber; i++) {
			values[i] = "";
			composeJComboBox[i] = new JComboBox();
			composeJComboBox[i].addItemListener(this);

			if (enableItemToolTips)
				composeJComboBox[i].setRenderer(new MyComboBoxRenderer());

			composeJComboBox[i].setMaximumRowCount(maxRowItemsView);

			if (comboBoxWidthsArray != null && i < comboBoxWidthsArray.length
					&& comboBoxWidthsArray[i] != 0)
				composeJComboBox[i].setPreferredSize(new Dimension(comboBoxWidthsArray[i], 20));

			composeJComboBox[i].setEditable(true);
			composeJComboBox[i].addActionListener(this);
            composeJComboBox[i].addMouseListener(this);
            composeJComboBox[i].setName("ComboBox" + i);
            composeJComboBox[i].getComponent(0).addMouseListener(this);
			composeJComboBox[i].getComponent(0).setName("Button" + i);
			comboBoxesPanel.add(composeJComboBox[i]);
		}
	}

	public void setComboBoxesWidths(String comboBoxesWidths) {
		this.comboBoxesWidths = comboBoxesWidths;

		// parsing out each comboBox's width setting value
		StringTokenizer st = new StringTokenizer(comboBoxesWidths, ",");

		comboBoxesWidths_Number = st.countTokens();

		// int maxWidths_Number = Math.max(comboBoxesWidths_Number,
		// comboBoxesNumber);
		comboBoxWidthsArray = new int[comboBoxesWidths_Number];

		String widthString;
		int n = 0;
		while (st.hasMoreElements()) {
			widthString = (String) st.nextElement();
			widthString = widthString.trim();
			if (widthString.length() != 0)
				comboBoxWidthsArray[n] = Integer.parseInt(widthString);
			n++;
		}
		// only one width is set and it's nonzero.
		if (comboBoxesWidths_Number == 1 && comboBoxWidthsArray[0] != 0) {
			int tempWidth = comboBoxWidthsArray[0];
			comboBoxWidthsArray = new int[composeJComboBox.length];
			for (int i = 0; i < composeJComboBox.length; i++)
				comboBoxWidthsArray[i] = tempWidth;
		}

		createComboBoxes();
	}

	public String getComboBoxesWidths() {
		return this.comboBoxesWidths;
	}

	public void setMaxRowItemsView(int maxRowItemsView) {
		this.maxRowItemsView = maxRowItemsView;

		if (composeJComboBox != null) {
			for (int i = 0; i < comboBoxesNumber; i++)
				composeJComboBox[i].setMaximumRowCount(maxRowItemsView);
		}
	}

	public int getMaxRowItemsView() {
		return maxRowItemsView;
	}

	public void setSentencePanelHeight(int sentencePanelHeight) {
		this.sentencePanelHeight = sentencePanelHeight;

		Dimension d = getSize();
		repaint();
		listScrollPane.setPreferredSize(new Dimension(d.width, sentencePanelHeight));
	}

	public int getSentencePanelHeight() {
		return this.sentencePanelHeight;
	}

	public void setItemValues(String itemValues) {
		this.itemValues = itemValues;

		values = new String[this.comboBoxesNumber];

		setComboBoxesNumber(comboBoxesNumber);

		int i = 0;
		// dfferent comboBox items are separated by ";".
		StringTokenizerItemValues st = new StringTokenizerItemValues(itemValues, ';', '/');
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			setValues(str, i);
			i++;
		}
	}

	public void setValue1(String valueItem) {
		valueItem = valueItem.trim();
		if (valueItem.length() > 0)
			setValues(valueItem, 0);
	}

	public void setValue2(String valueItem) {
		valueItem = valueItem.trim();
		if (valueItem.length() > 0)
			setValues(valueItem, 1);
	}

	public void setValue3(String valueItem) {
		valueItem = valueItem.trim();
		if (valueItem.length() > 0)
			setValues(valueItem, 2);
	}

	public void setValue4(String valueItem) {
		valueItem = valueItem.trim();
		if (valueItem.length() > 0)
			setValues(valueItem, 3);
	}

	public void setComboBoxesNumber(int comboBoxesNumber) {
		this.comboBoxesNumber = comboBoxesNumber;
		createComboBoxes();
	}

	public String getItemValues() {
		return this.itemValues;
	}

	public int getComboBoxesNumber() {
		return this.comboBoxesNumber;
	}

	public void setEditable(boolean setFlag) {
		if (composeJComboBox == null) return;

		for (int i = 0; i < comboBoxesNumber; i++) {
			composeJComboBox[i].setEditable(setFlag);
			if (setFlag)
				composeJComboBox[i].addActionListener(this);
			else
				composeJComboBox[i].removeActionListener(this);
		}
	}

	public boolean getallowDeletes() {
		return this.allowDeletes;
	}

	public void setallowDeletes(boolean allowDeletes) {
		this.allowDeletes = allowDeletes;
		if (trace.getDebugCode("dw")) trace.out("dw", "set allowDeletes to" + allowDeletes);
		createButtonPanel();
	}

	void addItem(int index, String addedItem) {
		String comboBoxValues = "";
		if (addedItem == null || addedItem.equals("")) return;
		if (!testInValues(this.values[index], addedItem)) {
			composeJComboBox[index].addItem(addedItem);
			if (this.values[index].equals(""))
				this.values[index] = addedItem;
			else {
				this.values[index] = this.values[index] + "," + addedItem;
				composeJComboBox[index].setSelectedIndex(0);
			}
			MessageObject mo = getDescriptionMessage();
			getUniversalToolProxy().sendMessage(mo);
		}
	}

	boolean testInValues(String comboBoxValues, String testString) {
		if (comboBoxValues.equals("")) return false;

		String tempItem;
		StringTokenizer st = new StringTokenizer(comboBoxValues, ",");
		while (st.hasMoreElements()) {
			tempItem = (String) st.nextElement();
			tempItem = tempItem.trim();
			if (testString.equals(tempItem)) return true;
		}
		return false;
	}

	//////////////////////////////////////////////////////
	/**
	 * Called when the user clicks one of buttons
	 */
	//////////////////////////////////////////////////////
	public void unlockWidget() {
		this.locked = false;
	}

	public boolean existingItem(String NewString) {
		String itemString;
		for (int i = 0; i < sizeOfSentenceJList; i++) {
			itemString = (String) ((DefaultListModel) sentenceJList.getModel()).elementAt(i);
			if (itemString.equals(NewString)) return true;
		}
		return false;
	}

	public void actionPerformed(ActionEvent e) {
		removeHighlight(commName);

		String itemString;
		if (e.getSource() == deleteJButton) {
			if (trace.getDebugCode("dw")) trace.out("dw", "Delete Button clicked");
			String selectedItem = (String) sentenceJList.getSelectedValue();
			// remove input from the sentenceJList

			if (selectedItem == null) return;

			doInterfaceAction("", "UpdateDeleteLines", selectedItem);
			dirty = true;
			sendValue();

		} else if (e.getSource() == addJButton) {
			if (trace.getDebugCode("dw")) trace.out("dw", "Add Button clicked");
			String tempString = "";

			for (int i = 0; i < comboBoxesNumber - 1; i++) {
				itemString = (String) composeJComboBox[i].getSelectedItem();
				if (itemString != null) tempString += itemString.trim() + " ";
			}

			itemString = (String) composeJComboBox[comboBoxesNumber - 1].getSelectedItem();
			if (itemString != null) tempString += itemString.trim();

//			if (existingItem(tempString)) return;  // prior out-of-order answer could be correct later 

			doInterfaceAction("", UPDATE_COMPOSER, tempString);
			dirty = true;
			sendValue();

		} else
			for (int i = 0; i < comboBoxesNumber; i++) {
		    	if(trace.getDebugCode("dw"))
		    		trace.out("dw", "JCComposer.actionPerformed("+e.paramString()+
		    				"): ctlr.isDefiningStartState() "+getController().isDefiningStartState());
				if (e.getSource() == composeJComboBox[i] && setValueDone
						&& getController().isDefiningStartState()) {
					itemString = (String) composeJComboBox[i].getSelectedItem();
					addItem(i, itemString);
					return;
				}
			}
	}

	//////////////////////////////////////////////////////
	/**
	 */
	//////////////////////////////////////////////////////
	public void highlight(String subElement, Border highlightBorder) {
		setBorder(highlightBorder);
	}

	//////////////////////////////////////////////////////
	/**
	 */
	//////////////////////////////////////////////////////
	public void removeHighlight(String subElement) {
		setBorder(originalBorder);
	}

	//////////////////////////////////////////////////////
	/**
	 * 
	 */
	//////////////////////////////////////////////////////
	public void setFocus(String subWidgetName) {
		addJButton.requestFocus();
	}

	private void setValues(String valuesString, int comboIndex) {
		if (comboIndex < 0 || comboIndex >= comboBoxesNumber) {
			trace.out(5, this, "invalid comboIndex value: " + comboIndex);
			return;
		}

		setValueDone = false;

		valuesString = valuesString.trim();

		values[comboIndex] = valuesString;

		if (composeJComboBox[comboIndex].getItemCount() != 0)
			composeJComboBox[comboIndex].removeAllItems();
		if (valuesString.length() == 0) {
			setValueDone = true;
			return;
		}

		StringTokenizerItemValues st = new StringTokenizerItemValues(valuesString, ',', '/');
		String tempItem;
		while (st.hasMoreElements()) {
			tempItem = (String) st.nextElement();
			tempItem = tempItem.trim();
			if (!tempItem.equals(""))
				((DefaultComboBoxModel) composeJComboBox[comboIndex].getModel()).addElement(tempItem);
		}
		composeJComboBox[comboIndex].setSelectedIndex(0);

		setValueDone = true;
	}

	//////////////////////////////////////////////////////
	/**
	 * 
	 */
	//////////////////////////////////////////////////////
	public void setFont(Font f) {
		if (f != null) {
			startFont = f;
			super.setFont(f);
		} else
			startFont = super.getFont();
	}

	public void doInterfaceDescription(MessageObject messageObject) {
		ComponentDescription cd = new ComponentDescription(this);
		cd.executeGraphicalProperties(messageObject);

		String comboBoxItems;
		Object obj;
		String valuesString;

		// parsing "ComboBoxesNumber"
		obj = messageObject.getProperty("ComboBoxesNumber");
		if (obj instanceof Integer) {
			comboBoxesNumber = ((Integer) obj).intValue();
			setComboBoxesNumber(comboBoxesNumber);
		} else if (obj instanceof String) {
			comboBoxesNumber = Integer.parseInt((String) obj);
			setComboBoxesNumber(comboBoxesNumber);
		} else
			trace.out("Unexpected TYPE of comboBoxesNumber");
		// parsing "ComboBoxesWidths"
		obj = messageObject.getProperty("ComboBoxesWidths");

		if (obj instanceof Integer) {
			setComboBoxesWidths(obj.toString());
		} else if (obj instanceof String) {
			setComboBoxesWidths((String) obj);
		} else
			trace.out("Unexpected TYPE of comboBoxesWidths");
		// parsing Value1, ... ValueN
		for (int i = 0; i < comboBoxesNumber; i++) {
			valuesString = "Values" + (i + 1);
			comboBoxItems = (String) messageObject.getProperty(valuesString);
				if (comboBoxItems != null && comboBoxItems.length() > 0)
					setValues(comboBoxItems, i);
				else
					setValues("", i);
			
		}
		setEditable(false);

		repaint();
	}

	//////////////////////////////////////////////////////
	/**
	 * Returns a comm message which describes this interface element.
	 */
	//////////////////////////////////////////////////////
	public MessageObject getDescriptionMessage() {
        MessageObject mo = MessageObject.create("InterfaceDescription");
        mo.setVerb("SendNoteProperty");

		if (getUniversalToolProxy() == null) {
			return mo;
		}
		if (!initialize(getController())) {
			trace.out(5, this, "ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return null;
		}
		mo = formDescriptionMessage();

		return mo;
	}

	public MessageObject formDescriptionMessage() {
        MessageObject mo = MessageObject.create("InterfaceDescription");
        mo.setVerb("SendNoteProperty");

		Vector pNames = new Vector();
		Vector pValues = new Vector();
		pValues.addElement(Integer.toString(this.comboBoxesNumber));
		pNames.addElement("ComboBoxesWidths");
		pValues.addElement(getComboBoxesWidths());
		
		mo.setProperty("WidgetType", "JCommComposer");
		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));
		mo.setProperty("ComboBoxesNumber", Integer.toString(this.comboBoxesNumber));
		mo.setProperty("ComboBoxesWidths", getComboBoxesWidths());

		

		for (int i = 0; i < this.comboBoxesNumber; i++) mo.setProperty("Values" + (i + 1), getValues(i));

		Vector deftemplates = createJessDeftemplates();
		Vector instances = createJessInstances();

		if (deftemplates != null) mo.setProperty("jessDeftemplates", deftemplates);

		if (instances != null) mo.setProperty("jessInstances", instances);

		serializeGraphicalProperties(mo);

		return mo;
	}

	// sanket@cs.wpi.edu

	public Vector createJessDeftemplates() {
		Vector deftemplates = new Vector();

		String deftemplateStr = "(deftemplate composer (slot name) (slot current-input)";
		for (int i = 0; i < comboBoxesNumber; i++)
			deftemplateStr += " (multislot list" + (i + 1) + ")";

		deftemplateStr += ")";
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
		// String instanceStr = "(assert (composer (name " + commName + ")
		// (list1 " + getValues1() + ") (list2 " + getValues2() + ") (list3 " +
		// getValues3() + ") (list4 " + getValues4() + ")))";

		String instanceStr = "(assert (composer (name " + commName + ")";
		for (int i = 0; i < comboBoxesNumber; i++)
			instanceStr += " (list" + (i + 1) + " " + getValues(i) + ")";

		instanceStr += "))";
		instances.add(instanceStr);

		return instances;
	}

	//////////////////////////////////////////////////////
	/**
	 * 
	 */
	//////////////////////////////////////////////////////
/*
	public void doCorrectAction(String selection, String input) {
		trace.out("doCorrectAction [ " + selection + " , " + input + "]");
		doInterfaceAction(selection, actionName, input);
		setValueColor(input, correctColor);
		if (getUniversalToolProxy().lockWidget()) locked = true;
	}
*/

	public void doCorrectAction(String selection, String action, String input) {
		// public void doCorrectAction(String selection, String input) {
		if (trace.getDebugCode("dw"))
			trace.out("dw", "doCorrectAction [ " + selection + " , " + action + " , " + input + "]");
		if (SET_VISIBLE.equalsIgnoreCase(action)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		} else {
			doInterfaceAction(selection, action, input);
			setValueColor(input, correctColor);
			if (getUniversalToolProxy().lockWidget())
				locked = true;
		}
	}

/*
	public void doLISPCheckAction(String selection, String input) {
		trace.out("doLISPCheckAction [ " + selection + " , " + input + "]");
		doInterfaceAction(selection, actionName, input);
		setValueColor(input, LISPCheckColor);
		if (getUniversalToolProxy().lockWidget()) locked = true;
	}
*/

	public void doLISPCheckAction(String selection, String action, String input) {
		trace.out("doLISPCheckAction [ " + selection + " , " + action + " , " + input + "]");
		doInterfaceAction(selection, action, input);
		setValueColor(input, LISPCheckColor);
		if (getUniversalToolProxy().lockWidget()) locked = true;
	}

	//////////////////////////////////////////////////////
	/**
	 * 
	 */
	//////////////////////////////////////////////////////
	public void doIncorrectAction(String selection, String input) {
		trace.out("doInCorrectAction [ " + selection + " , " + input + "]");
		undoInterfaceAction(selection, actionName, input);
		setValueColor(input, incorrectColor);
		locked = false;
	}

	public void doIncorrectAction(String selection, String action, String input) {
		trace.out("doInCorrectAction [ " + selection + " , " + action + " , " + input + "]");
		undoInterfaceAction(selection, action, input);
		setValueColor(input, incorrectColor);
		locked = false;
	}

	private void setValueColor(String value, Color setColor) {
		String tempItem;
		for (int i = 0; i < sizeOfSentenceJList; i++) {
			tempItem = (String) ((DefaultListModel) sentenceJList.getModel()).elementAt(i);
			if (tempItem.equals(value)) {
				sentenceJListColors.setElementAt(setColor, i);
				break;
			}
		}
		sentenceJList.repaint();
	}

	//////////////////////////////////////////////////////
	/**
	 * Used to process an InterfaceAction message
	 */
	//////////////////////////////////////////////////////
	public void doInterfaceAction(String selection, String action, String input) {
		// check that the input is new
		String tempItem = "";
		int size = sizeOfSentenceJList;
		newValue = input;
		actionName = action;
		if (trace.getDebugCode("dw")) trace.out("dw", "doInterfaceAction with " + action + " [ " + input + " ]");
    	if (action.equals("UpdateDeleteLines"))
			for (int i = 0; i < size; i++) {
				tempItem = (String) ((DefaultListModel) sentenceJList.getModel()).getElementAt(i);
				if (tempItem.equals(input)) {
					((DefaultListModel) sentenceJList.getModel()).removeElementAt(i);
					sizeOfSentenceJList--;
					break;
				}
			}
		else if (action.equals(UPDATE_COMPOSER)) {
			int index = sentenceJList.getSelectedIndex();

			if (!existingItem(input)) {
				if ((index == -1) || (index + 1 == size)) {
					((DefaultListModel) sentenceJList.getModel()).addElement(input);
					sentenceJList.setSelectedIndex(size);
				} else {
					((DefaultListModel) sentenceJList.getModel()).insertElementAt(input, index + 1);
					sentenceJList.setSelectedIndex(index + 1);
				}
				sizeOfSentenceJList++;
				sentenceJListColors.addElement(startColor);
			}
		}
		else if (action.equalsIgnoreCase(UPDATE_INVISIBLE)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "Set InVisible: "+input);
			if (input.equalsIgnoreCase("true"))
				setInvisible(true);
			else setInvisible(false);
			setVisible(!isInvisible());
			// setInvisible(input);
			return;
		}
		else if (SET_VISIBLE.equalsIgnoreCase(action)) {   // suppress feedback
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
			return;
		}
		sentenceJList.repaint();
		if (getController().isStartStateInterface())
			locked = true;
	}

	public void undoInterfaceAction(String selection, String action, String input) {
		// check that the input is new
		newValue = input;
		if (trace.getDebugCode("dw")) trace.out("dw", "undoInterfaceAction with " + action + " [ " + input + " ]");

		int index = sentenceJList.getSelectedIndex();
		int size = sizeOfSentenceJList;
		newValue = input;
		if (action.equals("UpdateDeleteLines")) {
			if ((index == -1) || (index + 1 == size)) {
				((DefaultListModel) sentenceJList.getModel()).addElement(newValue);
				sentenceJList.setSelectedIndex(size);
			} else {
				((DefaultListModel) sentenceJList.getModel()).insertElementAt(newValue, index + 1);
				sentenceJList.setSelectedIndex(index + 1);
			}
			sizeOfSentenceJList++;
			sentenceJListColors.addElement(startColor);
		}

		sentenceJList.repaint();
		if (getController().isStartStateInterface())
			locked = true;
	}

	//////////////////////////////////////////////////////
	/**
	 */
	//////////////////////////////////////////////////////
	public void setProperty(MessageObject o) {
		// trace.out (5, this, "setProperty: message = " + o);
	}

	public void focusGained(FocusEvent e) {
		super.focusGained(e);
	}

	public void focusLost(FocusEvent e) {
		super.focusLost(e);
	}

	//////////////////////////////////////////////////////
	/**
	 * 
	 */
	//////////////////////////////////////////////////////
	public Object getValue() {
		return newValue;
	}

	protected String getValues(int comboIndex) {
		return values[comboIndex];
	}

	public Vector getCurrentState() {
		Vector v = new Vector();
		if (trace.getDebugCode("dw")) trace.out("dw", "getCurrentState");
		String tempItem;
		for (int i = 0; i < sizeOfSentenceJList; i++) {
			newValue = (String) ((DefaultListModel) sentenceJList.getModel()).elementAt(i);
			v.addElement(getCurrentStateMessage());
		}
		return v;
	}

	//////////////////////////////////////////////////////
	/**
	 * 
	 */
	//////////////////////////////////////////////////////
	public boolean isChangedFromResetState() {
		if (trace.getDebugCode("dw")) trace.out("dw", "isChangedFromResetState: size = " + sizeOfSentenceJList);
		return sizeOfSentenceJList > 0;
	}

	public boolean hasValidValue() {
		DefaultListModel model = (DefaultListModel) sentenceJList.getModel();
		String value;
		for (int i = 0; i < model.getSize(); i++) {
			value = (String) model.elementAt(i);
			if (!(value == null || value.contains("--"))) return true;
		}
        return false;
	}

	//////////////////////////////////////////////////////
	/**
	 * 
	 */
	//////////////////////////////////////////////////////
	public void reset(TutorController controller) {
		if (sizeOfSentenceJList > 0) {
			((DefaultListModel) sentenceJList.getModel()).removeAllElements();
			sizeOfSentenceJList = 0;
			sentenceJListColors = new Vector();
		}
		locked = false;
	}

	public boolean getLock(String selection) {
		return locked;
	}

	//////////////////////////////////////////////////////
	/**
	 * 
	 */
	//////////////////////////////////////////////////////
	public void setBackground(Color c) {
		if (sentenceJList != null) sentenceJList.setBackground(c);
	}

	// zz add for Genetic tutors (Ben).
	class MyComboBoxRenderer extends BasicComboBoxRenderer {
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
				if (-1 < index)
					list.setToolTipText((value == null) ? "" : value.toString());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setFont(list.getFont());
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	class MyCellRenderer extends JLabel implements ListCellRenderer {
		public MyCellRenderer() {
			setOpaque(true);
			// this.setFont(startFont);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			Color cellColor;
			String valueString = value.toString();
			this.setText(valueString);
			if (allowDeletes)
				this.setBackground(isSelected ? Color.lightGray : Color.white);
			String tempItem;
			for (int i = 0; i < sizeOfSentenceJList; i++) {
				if (!allowDeletes) {
					if (valueString.equals(newValue))
						this.setBackground(Color.lightGray);
					else
						this.setBackground(Color.white);
				}
				tempItem = (String) ((DefaultListModel) sentenceJList.getModel()).elementAt(i);
				if (tempItem.equals(valueString)) {
					cellColor = (Color) sentenceJListColors.elementAt(i);
					if (cellColor == startColor)
						this.setFont(startFont);
					else if (cellColor == correctColor) {
						if (correctFont != null)
							this.setFont(correctFont);
						else
							this.setFont(startFont);
					} else {
						if (correctFont != null)
							this.setFont(incorrectFont);
						else
							this.setFont(startFont);
					}
					this.setForeground(cellColor);
					break;
				}
			}
			return this;
		}
	}

	/**
	 * Listener for state changes on the combo boxes in
	 * {@link #composeJComboBox}. Passes these changes on to other listeners.
	 * Since the combo boxes get reinstantiated with each call to
	 * {@link #setValues(String, int)}, it's hard for outside code to listen to
	 * them directly.
	 * 
	 * @param e
	 *            event generated by combo box
	 */
	public void itemStateChanged(ItemEvent e) {
		fireItemStateChanged(e);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type.
	 * 
	 * @param e
	 *            the event of interest
	 * @see EventListenerList
	 */
	protected void fireItemStateChanged(ItemEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ItemListener.class) {
				((ItemListener) listeners[i + 1]).itemStateChanged(e);
			}
		}
	}

	/**
	 * Adds an <code>ItemListener</code>.
	 * <p>
	 * <code>aListener</code> will receive one or two <code>ItemEvent</code>s
	 * when the selected item changes.
	 * 
	 * @param aListener
	 *            the <code>ItemListener</code> that is to be notified
	 * @see #setSelectedItem
	 */
	public void addItemListener(ItemListener aListener) {
		listenerList.add(ItemListener.class, aListener);
	}

	/**
	 * Removes an <code>ItemListener</code>.
	 * 
	 * @param aListener
	 *            the <code>ItemListener</code> to remove
	 */
	public void removeItemListener(ItemListener aListener) {
		listenerList.remove(ItemListener.class, aListener);
	}

	public void mousePressed(MouseEvent e) {
		 if (trace.getDebugCode("dw")) trace.out("dw", "Enter ModifyListsListener mousePressed");

		if (e.isControlDown() && getController().isDefiningStartState()) {

			JComponent eventSource = (JComponent) e.getSource();
			int selectedComboBoxIndex = 0;

			for (int i = 0; i < comboBoxesNumber; i++) {
				if (eventSource.getName().equals(("Button" + i)))
					selectedComboBoxIndex = i;
                if (eventSource.getName().equals(("ComboBox" + i)))
                    selectedComboBoxIndex = i;
			}

			JComboBox comboBox = composeJComboBox[selectedComboBoxIndex];
			JFrame frame = new JFrame("Modify List Items");
			String[] items = new String[comboBox.getItemCount()];
			for (int i = 0; i < comboBox.getItemCount(); i++) {
				items[i] = (String) comboBox.getItemAt(i);
				// trace.out("dw", "items[" + i + "] =" + items[i]);
			}
			String title = "Please set the Values for widget " + commName	+ " : ";
			EditListItemDialog t = new EditListItemDialog(frame, title, true, items,  isInvisible());
			composeJComboBox[selectedComboBoxIndex].removeAllItems();

			values[selectedComboBoxIndex] = "";
			String tempItem = "";
			String tempValues = "";
			for (int i = 0; i < t.getList().getModel().getSize(); i++) {
				tempItem = (String) t.getList().getModel().getElementAt(i);
				tempItem = tempItem.trim();

				if (!tempItem.equals("")) {
					if (this.values.equals(""))
						tempValues = tempItem;
					else
						tempValues = tempValues + "," + tempItem;
				}
				comboBox.addItem(tempItem);
			}

			dirty = true;
			this.setValues(tempValues, selectedComboBoxIndex);
			sendValue();
			comboBox.setEditable(false);
			// for (int i = 0; i < composeJComboBox[0].getItemCount(); i++) {
			// trace.out("dw", "AfterModified --- comboBox[" + i +"] =" +
			// JComboBox.getItemAt(i));
			// }
			if (isInvisible()  !=  t.isInvisible()) {
				setInvisible(t.isInvisible());
				setVisible(!isInvisible());
				dirty = true;
				setActionName(UPDATE_INVISIBLE);
				sendValue();
			}
			setActionName(UPDATE_COMPOSER);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		if (trace.getDebugCode("dw")) trace.out("dw", "Enter ModifyListsListener mouseClicked");
		boolean editable = false;
		if (getController().isDefiningStartState()) editable = true;

		for (int i = 0; i < comboBoxesNumber; i++) {
			composeJComboBox[i].setEditable(editable);
			composeJComboBox[i].addActionListener(this);
		}

		// trace.out("dw", "Enter ModifyListsListener mouseClicked");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		// trace.out("dw", "Enter ModifyListsListener mouseReleased");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		// trace.out("dw", "Enter ModifyListsListener mouseEntered");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		// trace.out("dw", "Enter ModifyListsListener mouseExited");
	}

}
