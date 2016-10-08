package pact.CommWidgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import cl.ui.tools.tutorable.geometrypad.GeometryPad;
import cl.ui.tools.tutorable.geometrypad.PointsListing;
import cl.ui.tools.tutorable.geometrypad.SelectionEvent;
import cl.ui.tools.tutorable.geometrypad.SelectionListener;
import edu.cmu.pact.ctat.MessageObject;     // import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.ComponentDescription;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;

// Java XML stuff
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.transform.dom.DOMSource;
//import java.io.StringWriter;
//import javax.xml.transform.stream.StreamResult;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerException;

public class JCommGeoDiagramEditor extends JCommWidget implements ActionListener, SelectionListener {

	private GeometryPad diagram;
	
	private static final String UPDATE_PATH = "UpdatePath";
	private static final String HAS_LEFT_BAR = "HasLeftBar";
	private static final String LEFT_BAR_BUTTONS = "LeftBarButtons";
	private static final String HAS_TOP_BAR = "HasTopBar";
	private static final String TOP_BAR_BUTTONS = "TopBarButtons";
	private static final String HAS_MENU_BAR = "HasMenuBar";
	private static final String MENU_BAR_ITEMS = "MenuBareItems";
	private static final String HAS_SUBMIT_BUTTON = "HasSubmitButton";
	private static final String BUTTON_SUBMIT_ACTION = "ButtonSubmitAction";
	private static final String SUBMIT_ACTION = "SubmitAction";
	private static final String CREATE_AUTOMATIC_INTERSECTION_POINTS = "CreateAutomaticIntsectionPoints";
	private static final String UPDATE_ACTION = "update";
	private static final String BUTTON_COLOR_CHANGE_ACTION = "ButtonColorChangeAction";
	
	private static final String POINT_DELIM = ",";
	
	private boolean hasMenuBar = false;
	private String menuBarItems = "";
	private boolean hasLeftBar = false;
	private String leftBarItems = "";
	private boolean hasTopBar = false;
	private String topBarItems = "";
	private boolean hasSubmitButton = false;	// Whether or not this has a submit button, or it submits automatically every student clic
	private boolean createAutomaticIntersectionPoints = false;
	//selection
	private boolean hasColorChoice = false;
	private boolean sendSegmentsOnly = false;
	private Vector<String> colorList = new Vector<String>();
	private Vector<JRadioButton> colorButtons = new Vector<JRadioButton>();	// The buttons containing the colors in the colorList, which are visible if hasColorChoice is true
	private int currentColorIndex; // The index of the currently selected color
	// Stores pre-defined names with colors
	private HashMap<String,Color> colorMap = new HashMap<String,Color>();
	private HashMap<String,Vector<String>> selectionMap = new HashMap<String,Vector<String>>(); // Map storing what the student has currently selected
	
	private JPanel submitPanel = new JPanel();
	private JButton submitButton = new JButton("Submit");	// The submit button to click if hasSubmitButton is true
	
	private String path;
	
	private Color correctColor = Color.green;
	private Color incorrectColor = Color.red;
	private Color normalColor = Color.black;
	
	// xml stuff
	public static final String XML_ELEMENT = "element";
	public static final String XML_HIGHLIGHT = "highlight";
	public static final String XML_TYPE = "type";
	public static final String XML_POINT = "point";
	public static final String XML_NAME = "name";
	public static final String XML_DIAGRAM_CONFIGURATION = "diagramConfiguration";
	
	public JCommGeoDiagramEditor() {
		setLayout(new BorderLayout());
		//setLayout(new java.awt.BorderLayout());
		
		diagram = createNewDiagram();
		diagram.removeAllMenusAndMenuItems();
		
		submitButton.setActionCommand(BUTTON_SUBMIT_ACTION);
		submitButton.addActionListener(this);
		submitPanel.add(submitButton);
		
		add(submitPanel, BorderLayout.SOUTH);
		add(diagram, BorderLayout.CENTER);
		
		initColorMap();
		
		setColorList("Green");
		
		//add(diagram, java.awt.BorderLayout.CENTER);
		add(diagram);
		diagram.addSelectionListener(this);
	}
	
//	 Set up the color map with the default colors
	private void initColorMap() {
		colorMap.put("black", Color.black);
		colorMap.put("blue", Color.blue);
		colorMap.put("cyan", Color.cyan);
		colorMap.put("darkgray", Color.darkGray);
		colorMap.put("gray", Color.gray);
		colorMap.put("green",Color.green);
		colorMap.put("lightgray", Color.lightGray);
		colorMap.put("magenta", Color.magenta);
		colorMap.put("orange", Color.orange);
		colorMap.put("pink", Color.pink);
		colorMap.put("red", Color.red);
		colorMap.put("white", Color.white);
		colorMap.put("yellow", Color.yellow);
	}
	
	private GeometryPad createNewDiagram() {
		GeometryPad diagram = GeometryPad.createDiagramPad(null, GeometryPad.EDITMODEPROP_LIMITED_EDITING, GeometryPad.LOCATION_NORMAL);
		diagram.removeAllButtons();
		diagram.removeAllModes();
		diagram.removeAllMenusAndMenuItems();
		return diagram;
	}
	

	public void reset(TutorController controller) {
		String oldPath = path;
		setPath(null);
		setPath(oldPath);
		submitButton.setForeground(normalColor);
		colorButtons.get(0).setSelected(true);
		diagram.setSelectionColor(colorButtons.get(0).getBackground());
		for (String s : selectionMap.keySet()) {
			selectionMap.put(s, new Vector<String>());
		}
	}
	
//	 When a new color list is provided, the buttons for choosing the color must be recreated
	private void createColorButtons() {
		ButtonGroup group = new ButtonGroup();
		colorButtons = new Vector<JRadioButton>();
		
		for (String colorString : colorList) {
			JRadioButton button = new JRadioButton();
			button.setActionCommand(BUTTON_COLOR_CHANGE_ACTION);
			
			Color color = colorMap.get(colorString.toLowerCase());
			if (color == null) {
				try {
					color = Color.decode(colorString);
				} catch (NumberFormatException e) {
					color = Color.white; // Default color if we can't parse the color
				}
			}
			
			button.setOpaque(true);
			button.setBackground(color);
			button.setActionCommand(BUTTON_COLOR_CHANGE_ACTION);
			button.addActionListener(this);
			
			group.add(button);
			colorButtons.add(button);
		}
		
		// Select the first button
		colorButtons.get(0).setSelected(true);
		diagram.setSelectionColor(colorButtons.get(0).getBackground());
		currentColorIndex = 0;
	}
	
	public void setPath(String path) {
		if (path != null && path.equals(this.path))
			return;
		
		this.path = path;
		
		diagram.loadDiagram(path, GeometryPad.EDITMODEPROP_LIMITED_EDITING, GeometryPad.LOCATION_NORMAL);
		//remove(diagram);
		//GeometryPad newDiagram = GeometryPad.createDiagramPad(path, GeometryPad.EDITMODEPROP_SELECTION, GeometryPad.LOCATION_CTAT, false);
		
		//if (newDiagram != null) {
		//	diagram = newDiagram;
		//}
		//else {
			//trace.err("Error: cannot find diagram " + path);
			//File tmp = new File(path);
		//}
		
		//add(diagram, java.awt.BorderLayout.CENTER);
		add(diagram);
		diagram.addSelectionListener(this);
		
		if (createAutomaticIntersectionPoints != diagram.getAutoIntersectingPointsMode())
			diagram.setAutoIntersectingPointsMode(createAutomaticIntersectionPoints);
		//diagram.setSize(getSize());
		revalidate();
	}

	public String getPath() {
		return path;
	}
	
	public void setCreateAutomaticIntersectionPoints(boolean createAutomaticIntersectionPoints) {
		if (createAutomaticIntersectionPoints != this.createAutomaticIntersectionPoints)
			diagram.setAutoIntersectingPointsMode(createAutomaticIntersectionPoints);
		
		this.createAutomaticIntersectionPoints = createAutomaticIntersectionPoints;
	}
	
	public boolean getCreateAutomaticIntersectionPoints() {
		return createAutomaticIntersectionPoints;
	}
	
	public boolean getHasLeftBar() {
		return hasLeftBar;
	}
	
	public void setHasLeftBar(boolean hasLeftBar) {
		if (this.hasLeftBar != hasLeftBar) {
			if (hasLeftBar) {
				diagram.addModeBar();
			}
			else {
				diagram.removeModeBar();
			}
		}
		
		this.hasLeftBar = hasLeftBar;
	}
	
	public String getLeftBarItems() {
		return leftBarItems;
	}
	
	public void setLeftBarItems(String leftBarItems) {
		if (leftBarItems == null)
			return;
		
		if (!this.leftBarItems.equalsIgnoreCase(leftBarItems)) {
			diagram.removeAllModes();
			
			String parts[] = leftBarItems.split(",");
			for (int i = 0; i < parts.length; i++) {
				diagram.addMode(parts[i].trim().toLowerCase());
			}
			
			//diagram.revalidate();
		}
		
		this.leftBarItems = leftBarItems;
	}
	
	public boolean getHasTopBar() {
		return hasTopBar;
	}
	
	public void setHasTopBar(boolean hasTopBar) {
		if (this.hasTopBar != hasTopBar) {
			if (hasTopBar) {
				diagram.addButtonBar();
			}
			else {
				diagram.removeButtonBar();
			}
		}
		
		this.hasTopBar = hasTopBar;
	}
	
	public String getTopBarItems() {
		return topBarItems;
	}
	
	public void setTopBarItems(String topBarItems) {
		if (topBarItems == null)
			return;
		
		if (!this.topBarItems.equalsIgnoreCase(topBarItems)) {
			diagram.removeAllButtons();
			
			String parts[] = topBarItems.split(",");
			for (int i = 0; i < parts.length; i++) {
				diagram.addButton(parts[i].trim().toLowerCase());
			}
			
			//diagram.revalidate();
		}
		
		this.topBarItems = topBarItems;
	}
	
	public boolean getHasMenuBar() {
		return hasMenuBar;
	}
	
	public void setHasMenuBar(boolean hasMenuBar) {
		if (this.hasMenuBar != hasMenuBar) {
			if (hasMenuBar) {
				diagram.addMenuBar();
			}
			else {
				diagram.removeMenuBar();
			}
		}
		
		this.hasMenuBar = hasMenuBar;
	}
	
	public String getMenuBarItems() {
		return menuBarItems;
	}
	
	public void setMenuBarItems(String menuBarItems) {
		if (menuBarItems == null)
			return;
		
		if (!this.menuBarItems.equalsIgnoreCase(menuBarItems)) {
			diagram.removeAllMenusAndMenuItems();
			
			String sections[] = menuBarItems.split("\\|");
			
			for (int j = 0; j < sections.length; j++) {
			
				String parts[] = sections[j].split(",");
				String menu = "";
				
				for (int i = 0; i < parts.length; i++) {
					if (i == 0) {
						menu = parts[i].trim().toLowerCase();
						diagram.addMenu(menu);
					}
					else
						diagram.addMenuItem(menu, parts[i].trim().toLowerCase());
					
				}
			}
			//diagram.revalidate();
		}
		
		this.menuBarItems = menuBarItems;
	}
	
	public void setColorList(String colorList) {
		Vector<String> newColorList = parseColorList(colorList);
		
		if (!isEqualColorLists(newColorList, this.colorList)) {
			this.colorList = newColorList;
			
			if (hasColorChoice) {
				for (JRadioButton button : colorButtons) {
					submitPanel.remove(button);
				}
			}
			// Set up the hash map
			selectionMap = new HashMap<String,Vector<String>>();
			for (String color : newColorList) {
				selectionMap.put(color, new Vector<String>());
			}
			createColorButtons();
			
			if (hasColorChoice) {
				for (JRadioButton button : colorButtons) {
					submitPanel.add(button);
				}
			}
		}
	}
	
	//	Get whether or not these two color lists are equal
	private boolean isEqualColorLists(Vector<String> list1, Vector<String> list2) {
		if (list1 == null && list2 == null)
			return true;
		else if (list1 == null || list2 == null || list1.size() != list2.size())
			return false;
		
		for (int i = 0; i < list1.size(); i++)
			if (!list1.get(i).equalsIgnoreCase(list2.get(i)))
				return false;
		
		return true;
	}
		
	
	public String getColorList() {
		return colorListToString(colorList);
	}
	
	// Parse a string representing a color list and return the vector of colors
	private Vector<String> parseColorList(String list) {
		if (list != null && list.length() > 0) {
			String parts[] = list.split(",");
			Vector<String> result = new Vector<String>();
			
			for (int i = 0; i < parts.length; i++) 
				result.add(parts[i].trim());
			
			return result;
		}
		
		return null;
	}
	
	// Convert a vector of strings representing a color list to a single string to represent the list
	private String colorListToString(Vector<String> list) {
		if (list != null) {
			StringBuffer buf = new StringBuffer();

			for (int i = 0; i < list.size(); i++) {
				 buf.append(list.get(i));
				
				if (i != list.size()-1)
					buf.append(",");
			}
			
			return buf.toString();
		}
		
		return null;
	}
	
//	 Get the currently selected color... could 
	private int getCurrentlySelectedColorIndex() {
		return currentColorIndex;
	}
	
	// Find out which color button is selected, returns the index
	private int calculateCurrentlySelectedColorIndex() {
		for (int i = 0; i < colorButtons.size(); i++) {
			if (colorButtons.get(i).isSelected()) {
				return i;
			}
		}
		
		return -1;
	}
	
	// Get the index of the given color string 
	private int getColorIndex(String color) {
		for (int i = 0; i < colorList.size(); i++) {
			if (colorList.get(i).equalsIgnoreCase(color))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Set whether or not this diagram gives a color choice.  If true, messages sent to CTAT will reflect this.
	 * 
	 * @param hasColorChoice Whether or not this diagram gives the student a color choice.
	 */
	public void setHasColorChoice(boolean hasColorChoice) {
		if (this.hasColorChoice != hasColorChoice) {
			this.hasColorChoice = hasColorChoice;
			
			if (hasColorChoice) {
				for (JRadioButton button : colorButtons) {
					submitPanel.add(button);
				}
			}
			else {
				for (JRadioButton button : colorButtons) {
					submitPanel.remove(button);
				}
			}
		}
	}
	
	// Change a string version of whether or not this has a color choice to a boolean
	private void setHasColorChoice(String hasColorChoice) {
		setHasColorChoice(Boolean.valueOf(hasColorChoice));
	}
	
	public boolean getHasColorChoice() {
		return hasColorChoice;
	}
	
	private void setSendSegmentsOnly(String sendSegsOnly) {
		setSendSegmentsOnly(Boolean.valueOf(sendSegsOnly));
	}
	
	public void setSendSegmentsOnly(boolean sendSegmentsOnly) {
		this.sendSegmentsOnly = sendSegmentsOnly;
	}
	
	public boolean getSendSegmentsOnly() {
		return sendSegmentsOnly;
	}
	
	///////////////////////////////////////////////////////
	/**
		Creates a vector of comm messages which describe the
		current state of this object relative to the start state
	*/
	//////////////////////////////////////////////////////
	public Vector getCurrentState() {

		Vector v = new Vector();

	    if (getPath() != null && getPath().length() > 0) {
	    	setActionName(UPDATE_PATH);
			v.addElement(getCurrentStateMessage());
	    }

		return v;
	}
	
	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////

	
	public Object getValue() {
		String tempValue = getPath();

		if (getActionName().equalsIgnoreCase(UPDATE_PATH)
				&& getPath() != null && !getPath().equals(""))
			return getPath();

		else if (getActionName().equalsIgnoreCase(UPDATE_ACTION)) {
			String points = formatPointLists(diagram.getPointLists(POINT_DELIM));
			
			StringBuffer buff = new StringBuffer();
			String curr = getCurrentStudentSelection();
			String currColor = colorList.get(getCurrentlySelectedColorIndex());
			Vector<String> currSelection = new Vector<String>();
			
			if (curr != null && curr.length() > 0) {
				String[] parts = curr.split(",");
				
				for (int i = 0; i < parts.length; i++) {
					currSelection.add(parts[i].trim());
				}
			}
				
			selectionMap.put(currColor, currSelection);
				
			for (String color : colorList) {
				String[] selections = selectionMap.get(color).toArray(new String[0]);
				Arrays.sort(selections);
				for (int i = 0; i < selections.length; i++) {
					String selection = selections[i];
						
					if (points.length() > 0) {
						buff.append("\n");
					}
					buff.append(XML_HIGHLIGHT + "\n");
						
					buff.append(color);
					buff.append("-");
					buff.append(selection);
				}
			}
			
			return points + buff.toString();
		}
		else {
			if (commName.equalsIgnoreCase("Hint")
					|| commName.equalsIgnoreCase("Help")
					|| commName.equalsIgnoreCase("Done"))

				return "-1";

			else
				return tempValue;
		}

	}
	
//	 Helper method to get the current selection
	private String getCurrentStudentSelection() {
		if (sendSegmentsOnly)
			return diagram.getCurrentSegmentedStudentSelection();
		else {
			return diagram.getCurrentStudentSelection();
		}
	}
	
	/*
	 * Returns an string
	 */
	private String formatPointLists(ArrayList<PointsListing> lists) {
		StringBuffer buff = new StringBuffer();
		
		for (PointsListing list : lists) {
			if (buff.length() > 0)
				buff.append("\n");
			buff.append(XML_ELEMENT + "\n" + list.getType());
			
			String[] parts = list.getPoints().split(",");
			for (int i = 0; i < parts.length; i++) {
				buff.append("\n"+ XML_POINT + "\n" + parts[i]);
			}
		}
		
		return buff.toString();
	}
	
	/* Done in Jess
	 * private void parseDiagramConfiguration(String config) {
		String result = "";
		String[] parts = config.split("\n");
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].equalsIgnoreCase("element")) {
				i++;
				String type = parts[i];
				result +="\n" + type + "|";
				for (i++;i<parts.length && !parts[i].equalsIgnoreCase("element"); i++) {
					if (parts[i].equalsIgnoreCase("point")) {
						i++;
						String point = parts[i];
						result += point +",";
					}
				}
				i--;
			}
		}
	}*/
	
	//////////////////////////////////////////////////////
	/**
		Used to process an InterfaceAction message
	*/
	//////////////////////////////////////////////////////
	public void doInterfaceAction(
		String selection,
		String action,
		String input) {

		if (action.equalsIgnoreCase(UPDATE_PATH)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "UpdatePath: " + input);
			setPath(input);
		}
		
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


		mo.setProperty("WidgetType", "CommGeoDiagram");


		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));
		
		mo.setProperty(HAS_LEFT_BAR, hasLeftBar);

		mo.setProperty(LEFT_BAR_BUTTONS, leftBarItems);

		mo.setProperty(HAS_TOP_BAR, hasTopBar);

		mo.setProperty(TOP_BAR_BUTTONS, topBarItems);

		mo.setProperty(HAS_MENU_BAR, hasMenuBar);

		mo.setProperty(MENU_BAR_ITEMS, menuBarItems);

		mo.setProperty(CREATE_AUTOMATIC_INTERSECTION_POINTS, createAutomaticIntersectionPoints);

		Vector deftemplates = createJessDeftemplates();
		Vector instances = createJessInstances();
		
		
		if(deftemplates != null)  mo.setProperty("jessDeftemplates", deftemplates);

		
		if(instances != null)    mo.setProperty("jessInstances", instances);

		serializeGraphicalProperties(mo);

		return mo;
	}
	
	public Vector createJessDeftemplates() {
		Vector deftemplates = new Vector();
		
		String deftemplateStr = "(deftemplate geoDiagramEditor (slot name) (slot value))";
		deftemplates.add(deftemplateStr);
		
		deftemplateStr = "(deftemplate diagramElement (slot name) (slot type) (multislot points))";
		deftemplates.add(deftemplateStr);

		return deftemplates;
	}
	
	/* (non-Javadoc)
	 * @see pact.CommWidgets.JCommWidget#createJessInstances()
	 */
	public Vector createJessInstances() {
		Vector instances = new Vector();
		
		String instanceStr = "(assert (geoDiagramEditor (name " + commName + ")))";
		instances.add(instanceStr);
		
		return instances;
	}
	
	public void doInterfaceDescription(MessageObject messageObject) {

        ComponentDescription cd = new ComponentDescription(this);
    	cd.executeGraphicalProperties(messageObject);
        
    	
        Object hasTopBar = 
        	messageObject.getProperty(HAS_TOP_BAR);
    	
        String topBarItems = 
                (String) messageObject.getProperty(TOP_BAR_BUTTONS);

        Object hasLeftBar = 
            messageObject.getProperty(HAS_LEFT_BAR);
        
        String leftBarItems = 
                (String) messageObject.getProperty(LEFT_BAR_BUTTONS);

        Object hasMenuBar = 
            messageObject.getProperty(HAS_MENU_BAR);

        String menuBarItems = 
                (String) messageObject.getProperty(MENU_BAR_ITEMS);

        Object createAutomaticIntersectionPoints = 
            messageObject.getProperty(CREATE_AUTOMATIC_INTERSECTION_POINTS);

        
        if (hasLeftBar == null
        		|| hasTopBar == null
        		|| hasMenuBar == null)
            return;
        
        if (hasLeftBar instanceof String)
        	setHasLeftBar(Boolean.valueOf((String)hasLeftBar));
        else
        	setHasLeftBar((Boolean)hasLeftBar);
        
        setLeftBarItems(leftBarItems);
        
        if (hasTopBar instanceof String)
        	setHasTopBar(Boolean.valueOf((String)hasTopBar));
        else
        	setHasTopBar((Boolean)hasTopBar);
        
        setTopBarItems(topBarItems);
        
        if (createAutomaticIntersectionPoints instanceof String)
        	setCreateAutomaticIntersectionPoints(Boolean.valueOf((String)createAutomaticIntersectionPoints));
        else
        	setCreateAutomaticIntersectionPoints((Boolean)createAutomaticIntersectionPoints);
    }
	
	//////////////////////////////////////////////////////
	/**
		Return true if any cells are not empty, otherwise false
	*/
	//////////////////////////////////////////////////////
	public boolean isChangedFromResetState() {
		if (diagram.hasChanged())
			return true;
		else
			return false;
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase(BUTTON_SUBMIT_ACTION)) {
			submitDiagram();
		}
		else if (e.getActionCommand().equalsIgnoreCase(BUTTON_COLOR_CHANGE_ACTION)) {
			int newIndex = calculateCurrentlySelectedColorIndex();
			
			if (newIndex != currentColorIndex) {
			
				String selection = getCurrentStudentSelection();
				String currColor = colorList.get(currentColorIndex);
				selectionMap.put(currColor, new Vector<String>());
				
				if (selection.length() > 0) {
					String[] parts = selection.split(",");
					
					for (int i = 0; i < parts.length; i++) {
						selectionMap.get(currColor).add(parts[i].trim());
					}
				}

				currentColorIndex = newIndex;
				
				highlightCurrentDiagramSelection();
			}
			
			
		}
	}
	
	private void highlightCurrentDiagramSelection() {
		String currColor = colorList.get(getCurrentlySelectedColorIndex());
		diagram.deselectAll();
		diagram.unHighlightAll();
		
		for (String color : selectionMap.keySet()) {
			if (color.equalsIgnoreCase(currColor))
				continue;
			
			String toHighlight = colorElementsToDiagramString(color);
			
			if (toHighlight.length() > 0) {
				diagram.setSelectionColor(colorButtons.get(getColorIndex(color)).getBackground());
				if (sendSegmentsOnly)
					diagram.highlightBySegmentedSelectedText(toHighlight, null);
				else
					diagram.highlightBySelectedText(toHighlight, null);
			}
		}
		
		String toHighlight = colorElementsToDiagramString(currColor);
		
		diagram.setSelectionColor(colorButtons.get(getCurrentlySelectedColorIndex()).getBackground());
		if (toHighlight.length() > 0) {
			diagram.selectBySelectedText(toHighlight, null);
		}
	}
	
	// Change the elements of the given color to a string that the diagram can use to highlight or select
	private String colorElementsToDiagramString(String color) {
		if (selectionMap.get(color) != null) {
			StringBuffer buff = new StringBuffer();
			
			for (String element : selectionMap.get(color)) {
				if (buff.length() != 0) {
					buff.append(", ");
				}
				
				buff.append(element);
			}
			
			return buff.toString();
		}
		
		
		return null;
	}
	
	/*
	 * Send the current diagram configuration to CTAT, to be interpreted by Jess
	 */
	private void submitDiagram() {
		setActionName(UPDATE_ACTION);
		dirty = true;
		sendValue();
	}
	
	public void selectionOccurred(SelectionEvent e) {
		if (e.getSelection() == null) {
			selectionMap = new HashMap<String, Vector<String>>();
			for (String color : colorList) {
				selectionMap.put(color, new Vector<String>());
			}
		}
	}

}
