package pact.CommWidgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import edu.cmu.pact.ctat.MessageObject;     // import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.pact.Utilities.ComponentDescription;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.EditListItemDialog;


//////////////////////////////////////////////////////
/**
 * <p>Title: JCommComboBox </p>
 * <p>Description: A text field which automatically sends user's input to Lisp via Comm </p>
 */
//////////////////////////////////////////////////////

//////////////////////////////////////////////////////
/**
    This class will automatically send user's input to Lisp.
*/
/////////////////////////////////////////////////////

public class JCommChooser extends JCommWidget implements ActionListener, MouseListener  {

	protected JList leftList; // to hold choice of values
	protected JList rightList; // to hold the selected values
	protected JScrollPane leftScrollPane;
	protected JScrollPane rightScrollPane;
	protected String newValue;
	protected Vector initialListStrings,
		listStringsStatus,
		listStringsColor,
		listStringIds;
	protected int sizeOfInitialListStrings, sizeOfLeftList, sizeOfRightList;
	protected String LEFTSIDE = "Left", RIGHTSIDE = "Right";
	protected Vector leftListIds, rightListIds;
	protected int valueIndex;
	protected JButton toRightButton;
	protected Font startFont;
	protected boolean locked;
	protected boolean objectsAdded = false;
	protected Dimension widgetDimension;
	
	protected Rectangle currentRectangle;

	//////////////////////////////////////////////////////
	/**
	   Constructor
	   */
	//////////////////////////////////////////////////////

	public JCommChooser() {

//		trace.setTraceLevel(5);
		
		leftList = new JList();
		leftList.addMouseListener(this);
		leftScrollPane = new JScrollPane(leftList);

		toRightButton = new JButton(">>");

		rightList = new JList();
		rightScrollPane = new JScrollPane(rightList);

		leftList.setCellRenderer(new MyCellRenderer());
		rightList.setCellRenderer(new MyCellRenderer());

		initialListStrings = new Vector();
		listStringsStatus = new Vector();
		listStringsColor = new Vector();
		listStringIds = new Vector();

		actionName = UPDATE_CHOOSER;

		backgroundNormalColor = leftList.getBackground();

		DefaultListModel m = new DefaultListModel();
		leftList.setModel(m);
		m = new DefaultListModel();
		rightList.setModel(m);

		toRightButton.addActionListener(this);
		
		widgetDimension = new Dimension(350, 120);
		setSize(widgetDimension);
				
		locked = false;

		addFocusListener(this);
	}
	
	public Dimension getSize() {
		return this.widgetDimension;
	}

	
	public void setBounds(int x, int y, int width, int height) {
	
		super.setBounds(x, y, width, height);
		createWidget();
		
		return;
	}
	
	public void setSize(Dimension d) {
		
		if (d.width == 0 || d.height == 0)
			return;
		
		trace.out(5, this, "setSize() is called");
		trace.out(5, this, "d = " + d.toString());
		super.setSize(d);
		
		createWidget();
		
		return;
	}

	
	// set layout of lists and button
	private void createWidget() {
		Rectangle bounds = this.getBounds();
		this.widgetDimension = bounds.getSize();
		
		Dimension d = this.widgetDimension;
		
		trace.out(5, this, "widgetDimension = " + widgetDimension.toString());
		
		int spacing = 6;

		setLayout(null);

		if (!objectsAdded) {
			objectsAdded = true;
			add(leftScrollPane);
			add(toRightButton);
			add(rightScrollPane);
		}

		leftScrollPane.setSize(2 * d.width / spacing, d.height);
		leftScrollPane.setPreferredSize(new Dimension(2 * d.width / spacing, d.height));
		
		rightScrollPane.setSize(2 * d.width / spacing, d.height);
		rightScrollPane.setPreferredSize(new Dimension(2 * d.width / spacing, d.height));

		int buttonWidth = java.lang.Math.min(50, d.width / spacing - 10);

		toRightButton.setSize(buttonWidth, 20);

		leftScrollPane.setLocation(0, 0);
		leftScrollPane.setVisible(true);
		
		toRightButton.setLocation(
			2 * d.width / spacing + (d.width / spacing - buttonWidth) / 2,
			d.height / 2 - 10);
		toRightButton.setVisible(true);
		
		rightScrollPane.setLocation(3 * d.width / spacing, 0);
		rightScrollPane.setVisible(true);

		setVisible(true);

		validate();
		
		repaint();
		
		return;
	}

	//////////////////////////////////////////////////////
	/**
		Called when the user clicks one of buttons
	*/
	//////////////////////////////////////////////////////

	public void actionPerformed(ActionEvent e) {
		String tempItem;
                
                removeHighlight("");
                
		if (e.getSource() == toRightButton && !locked) {
			String selectedItem = (String) leftList.getSelectedValue();
			if (selectedItem != null) {
				if(trace.getDebugCode("dw"))
					trace.out("dw", "JCChooser.actionPerformed("+e.paramString()+
							"): isDefiningStartState() "+getController().isDefiningStartState());
				newValue = selectedItem;
				if (getController().isDefiningStartState())
					doInterfaceAction("", UPDATE_CHOOSER, selectedItem);
				else
					locked = true;
				dirty = true;          // sendValue() a no-op unless dirty
				sendValue();
				//setValueRightStatus(selectedItem, startColor);
			}
		}
	}

        
 
        
	//////////////////////////////////////////////////////
	/**
		Sets the data that will be shown in the left drop-down menu
		of the list.  values is a comma-seperated list of strings
		to be displayed.
	*/
	//////////////////////////////////////////////////////
	public void setValues(String values) {
		StringTokenizer st = new StringTokenizer(values, ",");

		//trace.out (5, this, "start construct initial list strings");
		String itemString;
		
		initialListStrings = new Vector();
		listStringsStatus = new Vector();
		listStringsColor = new Vector();
		listStringIds = new Vector();
		
		sizeOfInitialListStrings = 0;

		while (st.hasMoreElements()) {
			itemString = (String) st.nextElement();
			if (itemString != null) {
				itemString = itemString.trim();
				//trace.out (5, this, "itemString = " + itemString);
				if (itemString.length() > 0) {
					initialListStrings.addElement(itemString);
					listStringsStatus.addElement(LEFTSIDE);
					listStringsColor.addElement(startColor);
					listStringIds.addElement(
						new Integer(sizeOfInitialListStrings));
					sizeOfInitialListStrings++;
				}
			}
		}

		setListsStrings();
	}

	public String getValues() {
		String initialListValuesString = "";

		if (sizeOfInitialListStrings > 0) {
			initialListValuesString = (String) initialListStrings.elementAt(0);

			if (sizeOfInitialListStrings > 1) {
				for (int i = 1; i < sizeOfInitialListStrings; i++)
					initialListValuesString =
						initialListValuesString
							+ ","
							+ (String) initialListStrings.elementAt(i);
			}
		}

		return initialListValuesString;
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setFont(Font f) {

		if (f != null) {
			//trace.out(5, this, "Font = " + f.toString());

			startFont = f;
			super.setFont(f);
		} else
			startFont = super.getFont();
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
	
	*/
	//////////////////////////////////////////////////////
	public void setFocus(String subWidgetName) {
		leftList.requestFocus();
		return;
	}

	
	//////////////////////////////////////////////////////
	/**
		Returns a comm message which describes this interface
		element.
	*/
	//////////////////////////////////////////////////////
	public MessageObject getDescriptionMessage() {

        MessageObject mo = MessageObject.create("InterfaceDescription");
        mo.setVerb("SendNoteProperty");

		if (!initialize(getController())) {
			trace.out(
				5,
				this,
				"ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return null;
		}

		mo.setProperty("WidgetType", "JCommChooser");
		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));
		mo.setProperty("ChooserItems", getValues());
		
		Vector deftemplates = createJessDeftemplates();
		Vector instances = createJessInstances();
		
		if(deftemplates != null)  mo.setProperty("jessDeftemplates", deftemplates);

		
		if(instances != null)    mo.setProperty("jessInstances", instances);

		
		serializeGraphicalProperties(mo);

		return mo;
	}
	
	public Vector createJessDeftemplates() {
		Vector deftemplates = new Vector();
		
		String deftemplateStr = "(deftemplate chooser (slot name) (slot value) (multislot left-values) (multislot right-values))";
		deftemplates.add(deftemplateStr);
		
		return deftemplates;
	}
	
	public Vector createJessInstances() {
		Vector instances = new Vector();
		
		String valuesStr = "";

		for (int i = 0; i < initialListStrings.size(); i++)
			valuesStr += " " + "\""+ initialListStrings.elementAt(i) + "\"";

		String instanceStr = "(assert (chooser (name " + commName + ") (left-values " + valuesStr + ")))";
		instances.add(instanceStr);
		
		return instances;
	}

	void setValueLeftStatus(String value, Color setColor) {
		int i;

		String tempItem;
		for (i = 0; i < sizeOfInitialListStrings; i++) {
			tempItem = (String) initialListStrings.elementAt(i);
			if (tempItem.equals(value)) {
				listStringsStatus.setElementAt(LEFTSIDE, i);
				listStringsColor.setElementAt(setColor, i);

				valueIndex = i;

				break;
			}
		}

		if (i == sizeOfInitialListStrings)
			trace.out(
				5,
				this,
				"value = " + value + " is not in the original list.");
		else {
			Integer tempInteger;
			boolean tryAddFlag = true;
			// added the value to the leftList
			for (i = 0; i < sizeOfLeftList && tryAddFlag; i++) {
				tempInteger = (Integer) leftListIds.elementAt(i);
				if (valueIndex == tempInteger.intValue()) {
					leftList.repaint();
					tryAddFlag = false;
				} else if (valueIndex < tempInteger.intValue()) {
					((DefaultListModel) leftList.getModel()).insertElementAt(
						value,
						i);
					leftListIds.insertElementAt(new Integer(valueIndex), i);
					sizeOfLeftList++;

					for (int j = 0; j < sizeOfLeftList; j++) {

					}

					tryAddFlag = false;
				}
			}

			// need to add the new value at the end
			if (tryAddFlag) {
				((DefaultListModel) leftList.getModel()).addElement(value);
				leftListIds.addElement(new Integer(valueIndex));
				sizeOfLeftList++;
			}
		}

		boolean flagUpdateList = true;

		for (i = 0; i < sizeOfLeftList; i++) {
			tempItem =
				(String) ((DefaultListModel) leftList.getModel()).elementAt(i);
			if (tempItem.equals(value)) {
				flagUpdateList = false;
				break;
			}
		}

		if (flagUpdateList)
			setListsStrings();
		else
			leftList.repaint();
	}

	void setValueRightStatus(String value, Color setColor) {
		doInterfaceAction("", "", value);

	
	}

	/**
	 * Used to process an InterfaceDescription message.
	 * @param mo
	 * @see pact.CommWidgets.JCommWidget#doInterfaceDescription(edu.cmu.pact.ctat.MessageObject)
	 */
    public void doInterfaceDescription(MessageObject mo) {
    	super.doInterfaceDescription(mo);
    	
    	Object chooserItems = mo.getProperty("ChooserItems");
    	if(!(chooserItems instanceof String))
    		return;
    	setValues(chooserItems.toString());
    }

	/**
	 * Processing for CorrectAction messages
	 * @param selection
	 * @param action
	 * @param input
	 * @see pact.CommWidgets.JCommWidget#doCorrectAction(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void doCorrectAction(String selection, String action, String input) {
		doCorrectOrInterfaceAction(selection, action, input, correctColor);
	}

	/**
	 * Common processing for CorrectAction and InterfaceAction messages.
	 * @param selection
	 * @param action
	 * @param input
	 * @param rightSideColor color for new right-side list element
	 */
	private void doCorrectOrInterfaceAction(String selection, String action, String input,
			Color rightSideColor) {

		// trace.out(5, this, "process correctAction: input = " + input);
		// trace.out(5, this, "locked = " + locked);
		if (SET_VISIBLE.equalsIgnoreCase(action)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		} else {
			boolean findFlag = false;

			String tempItem;
			for (int i = 0; i < sizeOfInitialListStrings; i++) {
				tempItem = (String) initialListStrings.elementAt(i);
				if (tempItem.equals(input)) {
					listStringsStatus.setElementAt(RIGHTSIDE, i);
					listStringsColor.setElementAt(rightSideColor, i);

					findFlag = true;
					break;
				}
			}

			if (!findFlag) {
				trace.out(5, "doCorrectAction in Chooser", input
						+ " is not in the original list");
				locked = false;
				return;
			}

			// remove input from the leftList and add it to the rightList
			for (int i = 0; i < sizeOfLeftList; i++) {
				tempItem = (String) ((DefaultListModel) leftList.getModel())
						.getElementAt(i);
				if (tempItem.equals(input)) {
					((DefaultListModel) leftList.getModel()).removeElementAt(i);
					sizeOfLeftList--;

					((DefaultListModel) rightList.getModel()).addElement(input);

					sizeOfRightList++;

					break;
				}
			}

			// setValueRightStatus(input, correctColor);
			locked = false;
		}
	}

	public void doLISPCheckAction(String selection, String input) {
		//trace.out(5, this, "process LISPCheckAction: input = " + input);
		//trace.out(5, this, "locked = " + locked);

		String tempItem;
		for (int i = 0; i < sizeOfInitialListStrings; i++) {
			tempItem = (String) initialListStrings.elementAt(i);
			if (tempItem.equals(input)) {
				listStringsColor.setElementAt(LISPCheckColor, i);

				break;
			}
		}

		leftList.repaint();
		//trace.out(5, this, "finish doLISPCheckAction");
		//trace.out(5, this, "locked = " + locked);
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void doIncorrectAction(String selection, String input) {
		//trace.out(5, this, "process incorrectAction: input = " + input);
		//trace.out(5, this, "locked = " + locked);

		String tempItem;
		for (int i = 0; i < sizeOfInitialListStrings; i++) {
			tempItem = (String) initialListStrings.elementAt(i);
			if (tempItem.equals(input)) {
				listStringsColor.setElementAt(incorrectColor, i);

				break;
			}
		}

		leftList.repaint();

		//setValueLeftStatus(input, incorrectColor);
		locked = false;
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

		//trace.out(5, this, "process interfaceAction: input = " + input);

		boolean findFlag = false;
		String tempItem = "";
		
		if (action.equalsIgnoreCase(UPDATE_INVISIBLE)) {
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
		else if (UPDATE_CHOOSER.equalsIgnoreCase(action))
			doCorrectOrInterfaceAction(selection, action, input, startColor);

		//setValueRightStatus(input, startColor);

	}

	//////////////////////////////////////////////////////
	/**
	*/
	//////////////////////////////////////////////////////
	public void setProperty(MessageObject o) {
		//trace.out (5, this, "setProperty: message = " + o);
	}

	public void focusGained(FocusEvent e) {

		super.focusGained(e);

	}

	public void focusLost(FocusEvent e) {

		leftList.clearSelection();
		leftList.repaint();

		rightList.clearSelection();
		rightList.repaint();

		super.focusLost(e);
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public Object getValue() {
		
		if (getActionName().equalsIgnoreCase(UPDATE_INVISIBLE))
			return isVisible();
//		else if (getController().isStartStateModified())
//			return initialListStrings;
		else
			return newValue;
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public String getText() {
		return "";
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setText(String text) {
	}

	public Vector getCurrentState() {

		Vector v = new Vector();

		if (getController().isStartStateModified()) {
			v.addElement(getCurrentStateMessage());
		} else {
			String tempStatus;
			for (int i = 0; i < sizeOfInitialListStrings; i++) {
				tempStatus = (String) listStringsStatus.elementAt(i);

				if (tempStatus.equals(RIGHTSIDE)) {
					newValue = (String) initialListStrings.elementAt(i);
					v.addElement(getCurrentStateMessage());
				}
			}
		}
		return v;
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public boolean isChangedFromResetState() {
		boolean changedFlag = false;
		String tempStatus;
		
		if (getController().isStartStateModified()) {
			changedFlag = true;
			return changedFlag;
		}
		
		for (int i = 0; i < sizeOfInitialListStrings; i++) {
			tempStatus = (String) listStringsStatus.elementAt(i);

			if (tempStatus.equals(RIGHTSIDE)) {
				changedFlag = true;
				break;
			}
		}

		return changedFlag;
	}

	void setListsStrings() {
		if (sizeOfLeftList != 0)
			 ((DefaultListModel) leftList.getModel()).removeAllElements();

		if (sizeOfRightList != 0)
			 ((DefaultListModel) rightList.getModel()).removeAllElements();

		String tempItem;
		String tempStatus;

		sizeOfLeftList = 0;
		sizeOfRightList = 0;

		//leftListIds = new Vector(); 
		//rightListIds = new Vector();

		//trace.out(5, this, "================================================================");
		//trace.out(5, this, "Start reconstruct leftList and rightList");

		for (int i = 0; i < sizeOfInitialListStrings; i++) {
			tempItem = (String) initialListStrings.elementAt(i);
			tempStatus = (String) listStringsStatus.elementAt(i);

			if (tempStatus.equals(LEFTSIDE)) {
				//trace.out(5, this, "## " + tempItem + " ## to the leftList: " + ((DefaultListModel) leftList.getModel()).toString());

				 ((DefaultListModel) leftList.getModel()).addElement(tempItem);
				//trace.out(5, this, "--> " + tempItem + " to the leftList: " + ((DefaultListModel) leftList.getModel()).toString());
				//trace.out(5, this,"[                                                     ]");
				//leftListIds.addElement(new Integer(i));
				sizeOfLeftList++;
			} else {
				//trace.out(5, this, "### " + tempItem + " ### to the rightList: " + ((DefaultListModel) rightList.getModel()).toString());
				((DefaultListModel) rightList.getModel()).addElement(tempItem);
				//trace.out(5, this, "---> " + tempItem + " to the rightList: " + ((DefaultListModel) rightList.getModel()).toString());
				//trace.out(5, this,"[                                                      ]");
				//rightListIds.addElement(new Integer(i));
				sizeOfRightList++;
			}
		}
		
		validate();
		repaint();
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void reset(TutorController controller) {
		//trace.out(5, this, "process reset()");
		//trace.out(5, this, "locked = " + locked);

		initialize(controller);

		for (int i = 0; i < sizeOfInitialListStrings; i++) {
			listStringsStatus.setElementAt(LEFTSIDE, i);
			listStringsColor.setElementAt(startColor, i);
		}

		setListsStrings();

		locked = false;
	}

	public boolean getLock(String selection) {
		return (sizeOfLeftList == sizeOfInitialListStrings);
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setBackground(Color c) {
		if (leftList != null)
			leftList.setBackground(c);

		if (rightList != null)
			rightList.setBackground(c);
	}

	class MyCellRenderer extends JLabel implements ListCellRenderer {
		public MyCellRenderer() {
			setOpaque(true);
			//this.setFont(startFont);
		}

		public Component getListCellRendererComponent(
			JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean cellHasFocus) {
			Color cellColor;
			String valueString = value.toString();

			this.setText(valueString);
			this.setBackground(isSelected ? Color.lightGray : Color.white);
			String tempItem;
			for (int i = 0; i < sizeOfInitialListStrings; i++) {
				tempItem = (String) initialListStrings.elementAt(i);
				if (tempItem.equals(valueString)) {
					cellColor = (Color) listStringsColor.elementAt(i);

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
	
    public void mousePressed(MouseEvent e) {

   		if (e.isControlDown() && getController().isDefiningStartState()) {
			JFrame frame = new JFrame("Edit List Items");
			
			String[] items = new String[sizeOfLeftList];
			
			for (int i = 0; i < sizeOfLeftList; i++) {
				items[i] = (String) ((DefaultListModel) leftList.getModel()).elementAt(i);
//				trace.out("dw", "items[" + i + "] =" + items[i]);
			}
			String title = "Please set the Values for widget " + commName + " : ";
			EditListItemDialog t = new EditListItemDialog(frame, title, true, 
					items,  isInvisible());
			((DefaultListModel) leftList.getModel()).removeAllElements();

//			for (int i = 0; i < t.getList().getModel().getSize(); i++)
//				((DefaultListModel) leftList.getModel()).addElement((String) t.getList().getModel().getElementAt(i));
//
//			sizeOfLeftList = t.getList().getModel().getSize();
			
			String tempItem = "";
			String tempValues = "";
			for (int i = 0; i < t.getList().getModel().getSize(); i++) {
				tempItem = (String) t.getList().getModel().getElementAt(i);
				((DefaultListModel) leftList.getModel()).addElement(tempItem);
				tempItem = tempItem.trim();
					
					if (!tempItem.equals("")) {
				        if (tempValues.equals(""))    
				        	tempValues = tempItem;
				        else {       
				        	tempValues = tempValues + "," + tempItem;
				            
				        }
					}
			}
			dirty = true;
			setValues(tempValues);
			sendValue();
//			for (int i = 0; i < sizeOfLeftList; i++) {
//				trace.out("dw", "AfterModified --- LeftList[" + i + "] ="
//						+ (String) ((DefaultListModel) leftList.getModel()).elementAt(i));
//			}

	        
				if (isInvisible()  !=  t.isInvisible()) {
					setInvisible(t.isInvisible());
					setVisible(!isInvisible());
					dirty = true;
					setActionName(UPDATE_INVISIBLE);
					sendValue();
				}
				setActionName(UPDATE_CHOOSER);
			
		} 

}
    
   	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	    public void mouseClicked(MouseEvent e) {
	//	trace.out("dw", "Enter ModifyListsListener mouseClicked");	
	}
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
	//	trace.out("dw", "Enter ModifyListsListener mouseReleased");	
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
	//	trace.out("dw", "Enter ModifyListsListener mouseEntered");	
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
	//	trace.out("dw", "Enter ModifyListsListener mouseExited");	
	}
}
