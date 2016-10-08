package pact.CommWidgets;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import pact.CommWidgets.event.StudentActionEvent;

import edu.cmu.pact.ctat.MessageObject;     // import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.EditListItemDialog;
import edu.cmu.pact.Utilities.ComponentDescription;
import edu.cmu.pact.Utilities.StringTokenizerItemValues;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

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
public class JCommComboBox extends JCommWidget implements ActionListener, MouseListener {

	protected JComboBox comboBox;
	protected String previousValue = "", resetValue = "", values = "", initialValues = "";
	protected Vector comboBoxStrings, comboBoxStringColors;
	protected int sizeOfComboBoxStrings;
	protected Font startFont;
	protected JPanel container;
	protected boolean actionFromBR;
	protected Color currentColor;
	protected boolean locked;
	protected boolean setValuesDone = true;
    protected boolean isFirstCallSetValues = true;
	
	protected int maxRowItemsView = 40;

	//////////////////////////////////////////////////////
	/**
		Constructor
	*/
	//////////////////////////////////////////////////////
	public JCommComboBox() {

		comboBox = new JComboBox();
		
		comboBox.setMaximumRowCount(maxRowItemsView);

		add(comboBox);
		GridLayout g = new GridLayout(1, 1);
		setLayout(g);

		actionName = UPDATE_COMBO_BOX;

		comboBox.addActionListener((ActionListener) this);
        addMouseListener(this);
		backgroundNormalColor = comboBox.getBackground();

		actionFromBR = false;
		locked = false;

		comboBox.addFocusListener((FocusListener) this);

		//addFocusListener (this);
		comboBox.setEditable(true); //can now intercept all mouse clicks
		//comboBox.setEditable(false);
		currentColor = startColor;
                
                comboBoxStrings = new Vector();
		comboBoxStringColors = new Vector();
		sizeOfComboBoxStrings = 0;
		originalBorder = comboBox.getBorder();	
	}

	
	//////////////////////////////////////////////////////
	/**
	 */
	//////////////////////////////////////////////////////
	public void highlight(String subElement, Border highlightBorder) {
	    comboBox.setBorder(highlightBorder);
	}

	//////////////////////////////////////////////////////
	/**
	 */
	//////////////////////////////////////////////////////
	public void removeHighlight(String subElement) {
		comboBox.setBorder(originalBorder);
	}
	
	public void setMaxRowItemsView(int maxRowItemsView) {
		this.maxRowItemsView = maxRowItemsView;
		if (comboBox != null)
			comboBox.setMaximumRowCount(maxRowItemsView);
	}
	
	public int getMaxRowItemsView() {
		return this.maxRowItemsView;
	}
	
	//////////////////////////////////////////////////////
	/**
		Called when the user selects an item from the drop-down menu
	*/
	//////////////////////////////////////////////////////
	public void actionPerformed(ActionEvent e) {
		if (trace.getDebugCode("dw")) trace.out("dw", "JCommComboBox.actionPerformed("+e+") actionFromBR "+
				actionFromBR+", locked "+locked+", initialized "+initialized);
		removeHighlight(commName);
		String selectedItem = (String) comboBox.getSelectedItem();

    	if(trace.getDebugCode("dw"))
    		trace.out("dw", "JCComboBox.actionPerformed("+e.paramString()+
    				"): ctlr.isDefiningStartState() "+getController().isDefiningStartState());
		if (setValuesDone && getController().isDefiningStartState()) {
			addItem(selectedItem);
            previousValue = selectedItem;
			return;
		}

		if (actionFromBR)
			return;

		if (locked) {
			comboBox.setSelectedItem(previousValue);
			return;
		}

		if (initialized) {
			dirty = true;

			// Hack to make sure a message is sent to lisp only when the user
			// changes the current selection directly, not as a result of recieving a comm message

			previousValue = selectedItem;
			if (!e.getActionCommand().equalsIgnoreCase("don't send"))
				sendValue();
			comboBox.setActionCommand("send");
		}
	}

	void addItem(String addedItem) {

        if (addedItem == null)
            return;
            
		if (addedItem.equals(resetValue))
			return;

		if (testInValues(addedItem))
			return;   
                
        comboBox.addItem(addedItem);
                
		comboBoxStrings.addElement(addedItem);
		
		comboBoxStringColors.addElement(startColor);

		sizeOfComboBoxStrings++;
                
                
        if (this.values.equals(resetValue))    
            this.values = addedItem;
        else {       
            this.values = this.values + "," + addedItem;
            comboBox.setSelectedIndex(0);
        }
		

		return;
	}

	boolean testInValues(String testString) {
            if (this.values.equals(resetValue))
                return false;
            
            String tempItem;
            StringTokenizer st = new StringTokenizer(this.values, ",");
            while (st.hasMoreElements()) {
                    tempItem = (String) st.nextElement();
                    tempItem = tempItem.trim();
                    if (testString.equals(tempItem))
                            return true;
            }

            return false;
	}

	public void setEditable(boolean setFlag) {
		comboBox.setEditable(setFlag);
	}

        //////////////////////////////////////////////////////////////////////
	/**
            set the drop-down menu as the initial values in the design option 
	*/
	//////////////////////////////////////////////////////////////////////
        
        public void setInitialValues() {
            setValues(initialValues);
        }
        
	//////////////////////////////////////////////////////
	/**
		Sets the data that will be shown in the drop-down menu
		of the combo box.  values is a comma-seperated list of strings
		to be displayed.
	*/
	//////////////////////////////////////////////////////
	public void setValues(String values) {
            
        if (isFirstCallSetValues) {
            initialValues = values;
            isFirstCallSetValues = false;
        }
        
        setValuesDone = false;
        
        comboBox.removeActionListener(this);
		this.values = values;
		
		if (comboBox.getItemCount() != 0)
			comboBox.removeAllItems();

		comboBoxStrings = new Vector();
		comboBoxStringColors = new Vector();
		sizeOfComboBoxStrings = 0;

		////	comboBox.setRenderer(new MyCellRenderer());

		String tempItem;

		StringTokenizerItemValues st = new StringTokenizerItemValues(values, ',', '/');
		while (st.hasMoreElements()) {
			tempItem = (String) st.nextElement();
			tempItem = tempItem.trim();
			if (!tempItem.equals("")) {
				comboBox.addItem(tempItem);
				comboBoxStrings.addElement(tempItem);
				comboBoxStringColors.addElement(startColor);
				sizeOfComboBoxStrings++;
			}
		}

		setValuesDone = true;
                comboBox.addActionListener((ActionListener) this);
		//initialized = true;
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
		if (comboBox != null)
			comboBox.setFont(startFont);
	}

	//////////////////////////////////////////////////////
	/**
		Returns a comm message which describes this interface
		element.
	*/
	//////////////////////////////////////////////////////
	public MessageObject getDescriptionMessage() {

// chc		MessageObject mo = new MessageObject("SendNoteProperty");
        MessageObject mo = MessageObject.create("InterfaceDescription");
        mo.setVerb("SendNoteProperty");
        
		if (!initialize(getController())) {
			trace.out(
				"ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return null;
		}

		// chc		Vector pNames = new Vector();
		// chc      Vector pValues = new Vector();

		// chc		pNames.addElement("MessageType");
		// chc      pValues.addElement("InterfaceDescription");

// chc		pNames.addElement("WidgetType");
// chc		pValues.addElement("JCommComboBox");
		mo.setProperty("WidgetType", "JCommComboBox");
// chc		pNames.addElement("CommName");
// chc		pValues.addElement(commName);
		mo.setProperty("CommName", commName);



// chc		pNames.addElement("UpdateEachCycle");
// chc		pValues.addElement(new Boolean(updateEachCycle));
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));

        Vector v = new Vector();
		int size = comboBox.getItemCount();
		for (int i = 0; i < size; i++)
			v.addElement(comboBox.getItemAt(i));
                
        if (v.size() > 0) {
// chc            pNames.addElement("ComboBoxItems");
// chc            pValues.addElement(v);
    		mo.setProperty("ComboBoxItems", v);

        }

		Vector deftemplates = createJessDeftemplates();
		Vector instances = createJessInstances();
		
		if(deftemplates != null){
// chc			pNames.addElement("jessDeftemplates");
// chc			pValues.addElement(deftemplates);
    		mo.setProperty("jessDeftemplates", deftemplates);

		}
		
		if(instances != null){
// chc			pNames.addElement("jessInstances");
// chc			pValues.addElement(instances);
    		mo.setProperty("jessInstances", instances);

		}
		
//		serializeGraphicalProperties(pNames, pValues);
		serializeGraphicalProperties(mo);

//		mo.addParameter("PROPERTYNAMES", pNames);
//		mo.addParameter("PROPERTYVALUES", pValues);

		return mo;
	}

    
	public Vector createJessDeftemplates() {
		Vector deftemplates = new Vector();
		
		String deftemplateStr = "(deftemplate comboBox (slot name) (slot value) (multislot values))";
		deftemplates.add(deftemplateStr);
		
		return deftemplates;
	}
	
	/* (non-Javadoc)
	 * @see pact.CommWidgets.JCommWidget#createJessInstances()
	 */
	public Vector createJessInstances() {
		Vector instances = new Vector();
		
		String valuesStr = "";
		int size = comboBox.getItemCount();
		for (int i = 0; i < size; i++)
			valuesStr += " " + "\""+ comboBox.getItemAt(i) + "\"";

		String instanceStr = "(assert (comboBox (name " + commName + ") (values " + valuesStr + ")))";
		instances.add(instanceStr);
		
		return instances;
	}
    
	public String getValues() {
		return values;
	}

	public void doCorrectAction(String selection, String action, String input) {
		
		if (SET_VISIBLE.equalsIgnoreCase(action)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		} else {
			actionFromBR = true;

			previousValue = input;

			if (getController().getUniversalToolProxy().lockWidget())
				locked = true;

			currentColor = correctColor;
			comboBox.setForeground(correctColor);
			comboBox.repaint();

			comboBox.setSelectedItem(input);
			moveFocus();

			actionFromBR = false;
	    	fireStudentAction(new StudentActionEvent(this));
		}
	}

	public void doLISPCheckAction(String selection, String input) {
		actionFromBR = true;

		previousValue = input;
                
                if (getController().getUniversalToolProxy().lockWidget())
                    locked = true;
                

		comboBox.setSelectedItem(input);
                moveFocus();
                
		comboBox.repaint();

		actionFromBR = false;
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void doIncorrectAction(String selection, String input) {

		actionFromBR = true;

		previousValue = input;
		locked = false;
		////	setValueColor(input, incorrectColor);

		currentColor = incorrectColor;
		comboBox.setForeground(incorrectColor);
		comboBox.repaint();

		comboBox.setSelectedItem(input);
                moveFocus();
                
		actionFromBR = false;
	}

    public void doInterfaceDescription(MessageObject messageObject) {

    	ComponentDescription cd = new ComponentDescription(this);
 //   	cd.executeGraphicalProperties(propertyNames, propertyValues);
    	cd.executeGraphicalProperties(messageObject);

// chc    	Object obj = MessageObject.getValue(propertyNames, propertyValues,
// chc                "ComboBoxItems");
    	// chc    	Object obj = messageObject.getProperty("ComboBoxItems");
    	// chc        if (obj == null)
    	// chc            return;

// chc        Object items = MessageObject.getValue(propertyNames, propertyValues,
// chc                "ComboBoxItems");
    	Object items = messageObject.getProperty("ComboBoxItems");

        // If it's a string instead of a vector then we'll assume it's an empty
        // message
        if (items instanceof String) {
            setEditable(false);
            return;
        }

        Vector comboBoxItems = (Vector) items;

        if (comboBoxItems != null && comboBoxItems.size() > 0) {
            String resetValues = (String) comboBoxItems.elementAt(0);

            for (int i = 1; i < comboBoxItems.size(); i++)
                resetValues = resetValues + ","
                        + Utils.cleanup((String) comboBoxItems.elementAt(i));

            setValues(resetValues);
        }

        setEditable(false);

        return;
    }


	// ////////////////////////////////////////////////////
	/**
     * Used to process an InterfaceAction message
     */
	// ////////////////////////////////////////////////////
	public void doInterfaceAction(
		String selection,
		String action,
		String input) {
		
		System.out.println("EEEEEEP0");
		
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

		System.out.println("EEEEEEP1");
    	actionFromBR = true;

		previousValue = input;
		locked = false;
		
		currentColor = startColor;
		comboBox.setForeground(startColor);

		comboBox.setSelectedItem(input);
                moveFocus();
                
		comboBox.repaint();
		System.out.println("EEEEEEP2");
		// This is a hack to prevent this object from sending another comm message
		// saying that this widget was changed, since that should only happen when the
		// user changes it directly.
		//comboBox.addActionListener (this);

		if (getController().isStartStateInterface())
			locked = true;
		

		actionFromBR = false;
	}

	//////////////////////////////////////////////////////
	/**
		*/
	//////////////////////////////////////////////////////
	public void setProperty(MessageObject o) {
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public Object getValue() {
//		System.err.println("JCommComboBox getValue");
		if (getActionName().equalsIgnoreCase(SET_VISIBLE))
			return isVisible();
		return previousValue;
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
		if (comboBox.getSelectedIndex() == 0)
			return false;
		else {
			locked = true;
			return true;
		}
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void reset(TutorController controller) {

		initialize(controller);

		for (int i = 0; i < sizeOfComboBoxStrings; i++) {
			comboBoxStringColors.setElementAt(startColor, i);
		}

		comboBox.removeActionListener(this);
		if (comboBox.getItemCount() > 0) {
			comboBox.setSelectedIndex(0);
			previousValue = "";
		}

		actionFromBR = false;
		locked = false;

		currentColor = startColor;
		comboBox.setForeground(startColor);

		comboBox.addActionListener(this);
	}

        
        public void moveFocus() {
            comboBox.transferFocus();
        }
        
	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setBackground(Color c) {
		if (comboBox != null)
			comboBox.setBackground(c);
	}
	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setToolTipText(String text) {
		if (comboBox != null)
			comboBox.setToolTipText(text);
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public String getToolTipText() {
		return comboBox.getToolTipText();
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void addFocusListener(FocusListener l) {
		super.addFocusListener(l);
		if (comboBox != null)
			comboBox.addFocusListener(l);
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void addMouseListener(MouseListener l) {
	    
//	    if (trace.getDebugCode("miss")) trace.out("miss", "JCommComboBox: addMouseListener called with " + l);
	 //   new Exception().printStackTrace();
	    
	    super.addMouseListener(l);
	    if (comboBox != null){
	        comboBox.addMouseListener(l);
	    	comboBox.getComponent(0).addMouseListener(l);
	    }
	}

    public void mousePressed(MouseEvent e) {
    	if (trace.getDebugCode("dw")) trace.out("dw", "Enter ModifyListsListener for "+getCommName()+
    			" mousePressed "+e.getSource()+
    			", "+e.paramString());	

   		if (e.isControlDown() && getController().isDefiningStartState()) {
   	    		setValuesDone = false;
   			    JFrame frame = new JFrame("Modify List Items");
   				String[] items = new String[comboBox.getItemCount()];
   				for (int i = 0; i < comboBox.getItemCount(); i++) {
   					items[i] = (String) comboBox.getItemAt(i);
   					if (trace.getDebugCode("dw")) trace.out("dw", "items[" + i +"] =" + items[i]);
   				}
   				String title = "Please set the Values for widget " + commName + " : ";
   				EditListItemDialog t = 	new EditListItemDialog(frame, title, true, items, isInvisible());

   				this.values = "";
   				String tempItem;
   				for (int i = 0; i < t.getList().getModel().getSize(); i++) 
   				{

   					tempItem = (String) t.getList().getModel().getElementAt(i);
   					tempItem = tempItem.trim();
   					
   					if (!tempItem.equals("")) {
   				        if (this.values.equals(resetValue))    
   				            this.values = tempItem;
   				        else {       
   				            this.values = this.values + "," + tempItem;
   				            
   				        }
   					}
   				}
   				this.setValues(values);
   				
   				for (int i = 0; i < comboBox.getItemCount(); i++) {
   					if (trace.getDebugCode("dw")) trace.out("dw", "AfterModified --- comboBox[" + i +"] =" + comboBox.getItemAt(i));
   				}
   		    	
   		        comboBox.setEditable(false);
   				setValuesDone = true;	        

   				if (isInvisible()  !=  t.isInvisible()) {
   					setInvisible(t.isInvisible());
   					setVisible(!isInvisible());
   				}
   	   				
   				if(getUniversalToolProxy() instanceof UniversalToolProxy) {
   					UniversalToolProxy utp = (UniversalToolProxy) getUniversalToolProxy();
   					utp.chooseSISettingsInStartState(getDescriptionMessage(),
   							utp.getController().getProblemModel());
   				}
   				setActionName("UpdateComboBox");
		}
    }
    
   	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	    public void mouseClicked(MouseEvent e) {
	//	trace.out("dw", "Enter ModifyListsListener mouseClicked");
	    	if (getController().isDefiningStartState()) 
	    		comboBox.setEditable(true);
	    	else 
	    		comboBox.setEditable(false);
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
	
	//////////////////////////////////////////////////////
	/**
		Creates a vector of comm messages which describe the
		current state of this object relative to the start state
	*/
	//////////////////////////////////////////////////////
	public Vector getCurrentState() {
  System.err.println("JCommComboBox getCurrentState");
		Vector v = new Vector();

		if (isInvisible()) {

			setActionName(SET_VISIBLE);
			v.addElement(getCurrentStateMessage());
		}

		return v;
	}
}
