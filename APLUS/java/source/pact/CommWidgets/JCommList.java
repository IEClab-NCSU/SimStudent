package pact.CommWidgets;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.border.Border;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.EditListItemDialog;
import edu.cmu.pact.Utilities.ComponentDescription;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.ctat.MessageObject;


//////////////////////////////////////////////////////
/**
 * <p>Title: JCommList </p>
 */
//////////////////////////////////////////////////////

//////////////////////////////////////////////////////
/**
    Allows the user to select from a list of strings
*/
/////////////////////////////////////////////////////
public class JCommList extends JCommWidget {

	public static final String UpdateList = "UpdateList";
	private static final String UPDATE_INVISIBLE = "UpdateInVisible";
	protected javax.swing.JList list;
	protected javax.swing.JScrollPane scrollPane;
	protected String previousValue = "", resetValue = "", values ="", newValue="";
	protected Vector listStrings, listStringColors;
	protected int sizeOfListStrings;
	protected java.awt.Font startFont;
	protected javax.swing.JPanel container;
	protected Vector selectedList = new Vector();
	//////////////////////////////////////////////////////
	/**
	 Constructor
	*/
	//////////////////////////////////////////////////////
	public JCommList() {

		list = new javax.swing.JList();
		scrollPane = new javax.swing.JScrollPane(list);
		add(scrollPane);
		java.awt.GridLayout g = new java.awt.GridLayout(1, 1);
		setLayout(g);

    	setActionName(UpdateList);
		backgroundNormalColor = list.getBackground();

		DefaultListModel m = new DefaultListModel();
		list.setModel(m);

		// event handling
		list.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2) {
					newValue = (String) list.getSelectedValue();
					// Don't create BR state with the same selected value 
					if (isAlreadySelected(newValue))
						   dirty = false;
	                   else
	                	   dirty = true;
					sendValue();
	//				setSelectedList(newValue);
					//list.removeSelectionInterval(0, sizeOfListStrings);
					list.clearSelection();
				}

			}
			
			public void mousePressed(MouseEvent e) {
		    	if (trace.getDebugCode("dw")) trace.out("dw", "Enter ModifyListsListener mousePressed");	

		   		if (e.isControlDown() && getController().isDefiningStartState()
		   				&& list.getModel().getSize() > 0) {
//		   	    		setValuesDone = false;
		   			    JFrame frame = new JFrame("Modify List Items");
		   				String[] items = new String[list.getModel().getSize()];
		   				for (int i = 0; i < list.getModel().getSize(); i++) {
		   					items[i] = (String) list.getModel().getElementAt(i);
		   					if (trace.getDebugCode("dw")) trace.out("dw", "items[" + i +"] =" + items[i]);
		   				}
		   				String title = "Please set the Values for widget " + commName + " : ";
		   				EditListItemDialog t = 	new EditListItemDialog(frame, title, true, items, isInvisible());

		   				values = "";
		   				String tempItem;
		   				for (int i = 0; i < t.getList().getModel().getSize(); i++) 
		   				{

		   					tempItem = (String) t.getList().getModel().getElementAt(i);
		   					tempItem = tempItem.trim();
		   					
		   					if (!tempItem.equals("")) {
		   				        if (values.equals(resetValue))    
		   				            values = tempItem;
		   				        else {       
		   				            values = values + "," + tempItem;
		   				            
		   				        }
		   					}
		   				}
		   				dirty = true;
		   				setValues(values);
		   				
						getUniversalToolProxy().sendMessage(getDescriptionMessage());
						sendValue();
		   				
		   				for (int i = 0; i < list.getModel().getSize(); i++) {
		   					if (trace.getDebugCode("dw")) trace.out("dw", "AfterModified --- List[" + i +"] =" + list.getModel().getElementAt(i));
		   				}
		 //  		    	setValuesDone = true;
		   		    	
		   				if (isInvisible()  !=  t.isInvisible()) {
		   					setInvisible(t.isInvisible());
		   					setVisible(!isInvisible());
		   					dirty = true;
		   					setActionName(UPDATE_INVISIBLE);
		   					sendValue();
		   				}
		   				setActionName(UpdateList);
		   			} 

			}

		});

		addFocusListener(this);
	}


	public void addMouseListener(MouseListener l) {
		if (list != null)
		list.addMouseListener(l);
	}

	public void addMouseMotionListener(MouseMotionListener l) {
		if (list != null)
		list.addMouseMotionListener (l);
	}

	public MouseListener[] getMouseListeners() {
		if (list != null)
		return list.getMouseListeners();
		return null;
	}
	
	public MouseMotionListener[] getMouseMotionListeners() {
		if (list != null)
			return list.getMouseMotionListeners();
		return null;
	}
	
	public void removeMouseMotionListener (MouseMotionListener l) {
		if (list != null)
			list.removeMouseMotionListener (l);		
	}
	
	public void removeMouseListener (MouseListener l) {
		if (list != null)
			list.removeMouseListener (l);
	}

	//////////////////////////////////////////////////////
	/**
		Sets the data that will be shown in the drop-down menu
		of the list.  values is a comma-seperated list of strings
		to be displayed.
	*/
	//////////////////////////////////////////////////////
	public void setValues(String values) {
		trace.out(5, this, "values: " + values);
		this.values = values;
		//trace.out (5, this, "num items = " + comboBox.getItemCount());

		int sizeOfList = list.getModel().getSize();

		if (sizeOfList != 0)
			 ((DefaultListModel) list.getModel()).removeAllElements();

		list.setCellRenderer(new MyCellRenderer());

		listStrings = new Vector();
		listStringColors = new Vector();
		sizeOfListStrings = 0;

		java.util.StringTokenizer st =
			new java.util.StringTokenizer(values, ",");

		trace.out(5, this, "start construct list");
		String tempString;

		while (st.hasMoreElements()) {
			tempString = (String) st.nextElement();
			if (tempString != null) {
				tempString = tempString.trim();
				if (!tempString.equals("")) {
					((DefaultListModel) list.getModel()).addElement(tempString);

					listStrings.addElement(tempString);
					listStringColors.addElement(startColor);
					sizeOfListStrings++;
				}
			}
		}

		trace.out(5, this, "construct list done");

	}

	public void removeSelectedIndex() {
		((DefaultListModel) list.getModel()).remove(list.getSelectedIndex());
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setFont(java.awt.Font f) {
		if (f != null) {

			startFont = f;
		} else
			startFont = super.getFont();

		super.setFont(startFont);
	}

	//////////////////////////////////////////////////////
	/**   Returns a comm message which describes this interface
	     *   element.
	     * @return
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



	mo.setProperty("WidgetType", "JCommList");
	mo.setProperty("CommName", commName);
	mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));
	mo.setProperty("Values", getValues());
	
	Vector deftemplates = createJessDeftemplates();
	Vector instances = createJessInstances();
	
	if(deftemplates != null)  mo.setProperty("jessDeftemplates", deftemplates);

	
	if(instances != null)    mo.setProperty("jessInstances", instances);

	
	serializeGraphicalProperties(mo);


		return mo;
	}

	// sanket@cs.wpi.edu
    
	public Vector createJessDeftemplates() {
		Vector deftemplates = new Vector();
		
		String deftemplateStr =
			"(deftemplate list (slot name) (slot value) (multislot values))";
		deftemplates.add(deftemplateStr);
		
		return deftemplates;
	}
	
	/* (non-Javadoc)
	 * @see pact.CommWidgets.JCommWidget#createJessInstances()
	 */
	public Vector createJessInstances() {
		Vector instances = new Vector();
		
		String valuesStr = "";
		if (values != null) {
			valuesStr = "";
			java.util.StringTokenizer st =
				new java.util.StringTokenizer(values, ",");
			for(; st.hasMoreTokens();)
				valuesStr += " \"" + st.nextToken() + "\"";
		}
		String instanceStr =
			"(assert (list (name " +commName+ ") (values " +valuesStr+ ")))";
		instances.add(instanceStr);
		return instances;
	}
    
	// sanket@cs.wpi.edu

	public String getValues() {
		return values;
	}

        public void highlight(String subElement, Border highlightBorder) {
	    list.setBorder(highlightBorder);
	}
        
        //////////////////////////////////////////////////////
	/**
	 */
	//////////////////////////////////////////////////////
	public void removeHighlight(String subElement) {
		list.setBorder(originalBorder);
	}
        
	public boolean getLock(String selection) {
		return false;
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void doCorrectAction(String selection, String action, String input) {

		if (SET_VISIBLE.equalsIgnoreCase(action)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		} else {
			setValueColor(input, correctColor);
			setSelectedList(input);
			if (getUniversalToolProxy().lockWidget())
				locked = true;
		}
	}

	public void doLISPCheckAction(String selection, String input) {
		setValueColor(input, LISPCheckColor);
                if (getUniversalToolProxy().lockWidget())
                    locked = true;
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void doIncorrectAction(String selection, String input) {
		setValueColor(input, incorrectColor);
                locked = false;
	}

	private void setValueColor(String value, Color setColor) {
		String tempItem;

		for (int i = 0; i < sizeOfListStrings; i++) {
			tempItem = (String) listStrings.elementAt(i);
			if (tempItem.equals(value)) {
				listStringColors.setElementAt(setColor, i);

				break;
			}
		}

		list.repaint();
	}

	/**
     * Used to process an InterfaceDescription message.
     * @param messageObject
     */
    public void doInterfaceDescription(MessageObject messageObject) {
    	super.doInterfaceDescription(messageObject);
    	Object prop;
    	if((prop = messageObject.getProperty("Values")) != null)
    		setValues(prop.toString());
    	selectedList.clear();
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

    	setValueColor(input, startColor);
    	if (action.equalsIgnoreCase(UpdateList)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "UpdateList: " + values);
			setText(input);
			setValues(input);
		}

    	else if (action.equalsIgnoreCase(UPDATE_INVISIBLE)) {
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

	//////////////////////////////////////////////////////
	/**
		*/
	//////////////////////////////////////////////////////
	public void setProperty(MessageObject o) {
		//trace.out (5, this, "setProperty: message = " + o);
	}

	public void focusGained(java.awt.event.FocusEvent e) {

		super.focusGained(e);

	}

	//////////////////////////////////////////////////////
	/**
		If focus lost permanently, and text has changed, send
		value to lisp
	*/
	//////////////////////////////////////////////////////
	public void focusLost(java.awt.event.FocusEvent e) {
		//list.removeSelectionInterval(0, sizeOfListStrings);
		list.clearSelection();
		list.repaint();

		super.focusLost(e);
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public Object getValue() {
//	if (getController().getCtatModeModel().isDefiningStartState())

		if (getActionName().equalsIgnoreCase(UPDATE_INVISIBLE))
			return isVisible();
	else if (list.getSelectedIndex() == -1)
		return values;
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

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public boolean isChangedFromResetState() {
		trace.out ("mps", "!!! COMM LIST: selectedIndex = " + list.getSelectedIndex());
		if(getController().isDefiningStartState())
			return true;
		else if (list.getSelectedIndex() == -1 )
			return false;
		else
			return true;
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void reset(TutorController controller) {
		trace.out(5, this, "list here");
		initialize(controller);
                
                locked = false;
                
		for (int i = 0; i < sizeOfListStrings; i++)
			listStringColors.setElementAt(startColor, i);

		list.setForeground(startColor);

		list.removeSelectionInterval(0, sizeOfListStrings);

		list.repaint();
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setBackground(Color c) {
		if (list != null)
			list.setBackground(c);
	}

	class MyCellRenderer
		extends javax.swing.JLabel
		implements javax.swing.ListCellRenderer {
		public MyCellRenderer() {
			setOpaque(true);
			//this.setFont(startFont);
		}

		public java.awt.Component getListCellRendererComponent(
			javax.swing.JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean cellHasFocus) {
			Color cellColor;

			this.setText(value.toString());
			this.setBackground(isSelected ? Color.lightGray : Color.white);
			String tempItem;
			for (int i = 0; i < sizeOfListStrings; i++) {
				tempItem = (String) listStrings.elementAt(i);
				if (tempItem.equals(value.toString())) {
					cellColor = (Color) listStringColors.elementAt(i);
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



	public Vector getSelectedList() {
		return selectedList;
	}


	public void setSelectedList(String value) {
		selectedList.addElement(value);
		
	}
	
	public boolean isAlreadySelected(String value) {
		for(int index = 0; index < selectedList.size(); index++)
			if (selectedList.elementAt(index) == value) return true;		
		return false;
	}
	
	public void mousePressed(MouseEvent e) {
	}
}
