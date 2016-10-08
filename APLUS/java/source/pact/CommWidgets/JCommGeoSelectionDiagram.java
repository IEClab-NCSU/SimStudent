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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import pact.CommWidgets.event.IncorrectActionEvent;
import cl.ui.tools.tutorable.geometrypad.GeometryPad;
import cl.ui.tools.tutorable.geometrypad.PointsListing;
import cl.ui.tools.tutorable.geometrypad.SelectionEvent;
import cl.ui.tools.tutorable.geometrypad.SelectionListener;
import edu.cmu.pact.ctat.MessageObject;     // import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeEvent;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeListener;
import edu.cmu.pact.Utilities.ComponentDescription;
import edu.cmu.pact.Utilities.trace;

public class JCommGeoSelectionDiagram extends JCommWidget implements ActionListener, CtatModeListener, SelectionListener {

	private GeometryPad diagram;
	
	private JPanel colorSubmitPanel = new JPanel();
	
	private static final String UPDATE_PATH = "Path";
	private static final String HAS_SUBMIT_BUTTON = "HasSubmitButton";
	private static final String HAS_DEMONSTRATE_SUBMIT_BUTTON = "HasDemonstrateSubmitButton";
	private static final String HAS_COLOR_CHOICE = "HasColorChoice";
	private static final String IS_SELECTION = "IsSelection";
	private static final String SEND_SEGMENTS_ONLY = "SendSegmentsOnly";
	private static final String COLOR_LIST = "ColorList";
	public static final String SELECTED_ACTION = "SelectedAction";
	private static final String BUTTON_SUBMIT_ACTION = "ButtonSubmitAction";
	private static final String BUTTON_COLOR_CHANGE_ACTION = "ButtonColorChangeAction";
	
	private String path;	// The location of the diagram in this widget
	private boolean hasSubmitButton = false;	// Whether or not this has a submit button, or it submits automatically every student clic
	private boolean hasColorChoice = false;		// Whether or not the student must use only one color, or has a choice of colors to select from
	private boolean hasDemonstrateSubmitButton = false; // Whether or not this has a submit button in demonstrate mode *only*
	private boolean sendSegmentsOnly = false; 	// Whether or not this sends just segments to the brd, as opposed to angles, triangles etc
	private Vector<String> colorList = new Vector<String>();
	//private String colorList = "Green";			// The choice of colors a student can select from (only the first is used if hasColorChoice is false)
	private JButton submitButton = new JButton("Submit");	// The submit button to click if hasSubmitButton is true
	private JButton demonstrateSubmitButton = new JButton("<html><i>Send</i></html>");// the submit button if hasSubmitButton is false but hasDemonstrateSubmitButton is true
	private Vector<JRadioButton> colorButtons = new Vector<JRadioButton>();	// The buttons containing the colors in the colorList, which are visible if hasColorChoice is true
	private int currentColorIndex; // The index of the currently selected color
	private boolean isSelection = true;
	
	private boolean isInDemonstrateMode = false; // A way to keep track of whether or not this is in demonstrate mode
	
	private JLabel blank = new JLabel(""); //  A blank JLabel to make sure the colorSubmitPanel height is correct;
	
	private Color correctColor = Color.green;
	private Color incorrectColor = Color.red;
	private boolean isCurrentlyIncorrect = false; // Whether or not the diagram is in an "incorrect" state at the moment
	
	// Stores pre-defined names with colors
	private HashMap<String,Color> colorMap = new HashMap<String,Color>();
	
	private HashMap<String,Vector<String>> selectionMap = new HashMap<String,Vector<String>>(); // Map storing what the student has currently selected
	
	private ArrayList<PointsListing> pointListings;	// pointListings for the diagrams so we can do canonical naming
	
	public JCommGeoSelectionDiagram() {
		//java.awt.GridLayout g = new java.awt.GridLayout(1, 1);
		//setLayout(g);
		setLayout(new BorderLayout());
		
		diagram = GeometryPad.createDiagramPad(null, GeometryPad.EDITMODEPROP_SELECTION, GeometryPad.LOCATION_CTAT);
		if (trace.getDebugCode("inter")) trace.out("inter", "JCommGeoSelectionDiagram: diagram = " + diagram);
		pointListings = diagram.getPointLists();
		
		initColorMap();
		
		submitButton.setActionCommand(BUTTON_SUBMIT_ACTION);
		submitButton.addActionListener(this);
		
		demonstrateSubmitButton.setActionCommand(BUTTON_SUBMIT_ACTION);
		demonstrateSubmitButton.addActionListener(this);
		
		setColorList("Green");
		
		doNormalVisualCue();
		
		//colorSubmitPanel.add(new JLabel(""));
		blank.setPreferredSize(colorButtons.get(0).getPreferredSize());
		colorSubmitPanel.add(blank);
		this.add(colorSubmitPanel, BorderLayout.SOUTH);
		
		setHasDemonstrateSubmitButton(true);
		
		add(diagram, BorderLayout.CENTER);
		
		diagram.addSelectionListener(this);
		
		/*if (getController() != null) {
			getController().addCtatModeListener(this);
			ctatModeEventOccured(null);
			System.out.println("Zhounds!");
		}*/
	}
	
	// Set up the color map with the default colors
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
	
	public GeometryPad getDiagram() {
		return diagram;
	}
	
	public void reset(TutorController controller) {
		if (trace.getDebugCode("dw")) trace.out("dw", "reset: controller = " + controller);
		diagram.deselectAll();
		diagram.unHighlightAll();
		
		for (String s : selectionMap.keySet()) {
			selectionMap.put(s, new Vector<String>());
		}
		
		submitButton.setForeground(startColor);
		submitButton.setEnabled(true);
		colorButtons.get(0).setSelected(true);
		diagram.setSelectionColor(colorButtons.get(0).getBackground());
		currentColorIndex = 0;
		
		doNormalVisualCue();
		
		isCurrentlyIncorrect = false;
	}
	
	// Need to know when we get a controller
    public void setCommName(String commName, BR_Controller controller) {
    	super.setCommName(commName, controller);
    	getController().addCtatModeListener(this);
    }
	
	// When a new color list is provided, the buttons for choosing the color must be recreated
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
		if (trace.getDebugCode("dw")) trace.out("dw", "setPath: path = " + path + " this.path = " + this.path);
		if (path != null && path.equals(this.path)) return;
		
		this.path = path;
		
		remove(diagram);
		GeometryPad newDiagram = GeometryPad.createDiagramPad(path, isSelection ? GeometryPad.EDITMODEPROP_SELECTION : GeometryPad.EDITMODEPROP_NO_EDITING, GeometryPad.LOCATION_CTAT);
		if (trace.getDebugCode("dw")) trace.out("dw", "newDiagram = " + newDiagram);
		
		if (newDiagram != null) {
			diagram.removeSelectionListener(this);
			diagram = newDiagram;
			diagram.addSelectionListener(this);
			pointListings = diagram.getPointLists();
		}
		
		add(diagram, BorderLayout.CENTER);
		revalidate();
	}

	public String getPath() {
		return path;
	}
	
	public void setColorList(String colorList) {
		Vector<String> newColorList = parseColorList(colorList);
		
		if (!isEqualColorLists(newColorList, this.colorList)) {
			this.colorList = newColorList;
			
			if (hasColorChoice) {
				for (JRadioButton button : colorButtons) {
					colorSubmitPanel.remove(button);
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
					colorSubmitPanel.add(button);
				}
			}
		}
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
	
	// Get whether or not these two color lists are equal
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
	
	/**
	 * Set whether or not this diagram allows selection
	 * 
	 * @param isSelection True if this is to allow selection
	 */
	public void setIsSelection(boolean isSelection) {
		if (trace.getDebugCode("dw")) trace.out("dw", "setIsSelection: isSelection = " + isSelection);
		if (this.isSelection != isSelection) {
			if (isSelection) {
				diagram.enableStudentSelection();
			}
			else {
				diagram.unHighlightAll();
				diagram.deselectAll();
				diagram.disableStudentSelection();
				diagram.unsetWrongStudentSelection();
			}
			
			this.isSelection = isSelection;
		}
	}
	
	/**
	 * Get whether or not this diagram allows selection
	 */
	public boolean getIsSelection() {
		return isSelection;
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
					colorSubmitPanel.add(button);
				}
				if (!hasSubmitButton)
					fixColorSubmitPanelVisibility();
			}
			else {
				for (JRadioButton button : colorButtons) {
					colorSubmitPanel.remove(button);
				}
				if (!hasSubmitButton)
					fixColorSubmitPanelVisibility();
			}
			
			fixColorSubmitPanelVisibility();
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
	
	// Change a string version of whether or not this has a submit button to a boolean
	private void setHasSubmitButton(String hasSubmitButton) {
		setHasSubmitButton(Boolean.valueOf(hasSubmitButton));
	}
	
	public void setHasSubmitButton(boolean hasSubmitButton) {
		if (this.hasSubmitButton != hasSubmitButton) {
			this.hasSubmitButton = hasSubmitButton;
			
			if (hasSubmitButton) {
				colorSubmitPanel.add(submitButton);
				if (!hasColorChoice)
					fixColorSubmitPanelVisibility();
				else { // Have to make sure the submit button is in front
					for (JRadioButton button : colorButtons) {
						colorSubmitPanel.remove(button);
						colorSubmitPanel.add(button);
					}
				}
			}
			else {
				colorSubmitPanel.remove(submitButton);
				if (!hasColorChoice)
					fixColorSubmitPanelVisibility();
			}
		}
	}
	
//	 Change a string version of whether or not this has a submit button to a boolean
	private void setHasDemonstrateSubmitButton(String hasSubmitButton) {
		setHasDemonstrateSubmitButton(Boolean.valueOf(hasSubmitButton));
	}
	
	public void setHasDemonstrateSubmitButton(boolean hasDemonstrateSubmitButton) {
		if (this.hasDemonstrateSubmitButton != hasDemonstrateSubmitButton) {
			this.hasDemonstrateSubmitButton = hasDemonstrateSubmitButton;
			
			if (hasDemonstrateSubmitButton && !hasSubmitButton && (getController() != null ) 
					&& getController().isDemonstratingSolution()) {
				addDemonstrateSubmitButton();
			}
			else {
				if (!hasSubmitButton && (getController() != null) && getController().isDemonstratingSolution()) {
					removeDemonstrateSubmitButton();
				}
			}
		}
	}
	
	// For now, turn border red
	private void doIncorrectVisualCue() {
		if (hasSubmitButton) {
	    	submitButton.setForeground(incorrectColor);
		}
		
		LineBorder border = new LineBorder(incorrectColor);
		this.setBorder(border);
	}
	
	// For now, turn border green
	private void doCorrectVisualCue() {
		LineBorder border = new LineBorder(correctColor);
		this.setBorder(border);
	}
	
	// For now do nothing to border
	private void doNormalVisualCue() {
		if (hasSubmitButton) {
	    	submitButton.setForeground(startColor);
		}
		
		EmptyBorder border = new EmptyBorder(1,1,1,1);
		this.setBorder(border);
	}
	
	// Add or remove the colorSubmitPanel if necessary 
	private void fixColorSubmitPanelVisibility() {
		if (hasSubmitButton || hasColorChoice || hasDemonstrateSubmitButton) {
			//add(colorSubmitPanel, BorderLayout.SOUTH);
			colorSubmitPanel.remove(blank);
		}
		else if (!hasSubmitButton && !hasColorChoice && !hasDemonstrateSubmitButton) {
			//remove(colorSubmitPanel);
			colorSubmitPanel.add(blank);
		}
		
	}
	
	// Add the demonstrate submit button when in demonstrate mode
	private void addDemonstrateSubmitButton() {
		if (hasDemonstrateSubmitButton && !hasSubmitButton) {
			colorSubmitPanel.add(demonstrateSubmitButton);
			if (!hasColorChoice)
				fixColorSubmitPanelVisibility();
			else { // Have to make sure the submit button is in front
				for (JRadioButton button : colorButtons) {
					colorSubmitPanel.remove(button);
					colorSubmitPanel.add(button);
				}
			}
			colorSubmitPanel.revalidate();
		}
	}
	
	// Remove the demonstrate submit button when in demonstrate mode
	private void removeDemonstrateSubmitButton() {
		colorSubmitPanel.remove(demonstrateSubmitButton);
		if (!hasColorChoice)
			fixColorSubmitPanelVisibility();
		colorSubmitPanel.revalidate();
	}
	
	public boolean getHasSubmitButton() {
		return hasSubmitButton;
	}
	
	public boolean getHasDemonstrateSubmitButton() {
		return hasDemonstrateSubmitButton;
	}
	
	// Get the currently selected color... could 
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
	
	// Clear the selection map of everything
	private void clearSelectionMap() {
		for (String color : selectionMap.keySet()) {
			selectionMap.put(color, new Vector<String>());
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
	
	// Change the name of the current selection to a canonical name, to make it easier to create BRDs
	// For now, this only names angles by their nearest points.  In this case, it's a REGULAR EXPRESSION
	// representing all possible values, with the value that it actually is first in the list 
	private String canonicallyName(String selection) {
		StringBuffer buff = new StringBuffer();

		if (selection.contains("Angle")) {
			String[] parts = selection.split(" ");
			// Angles are of the form CDG
			String vertex = parts[1].substring(1,2);
			String p1 = parts[1].substring(0,1);
			String p2 = parts[1].substring(2,3);
			String newp1 = p1;
			String newp2 = p2;
			String s1 = "";
			String s2 = "";
			
			for (PointsListing list : pointListings) {
				//System.out.println(list.type + " | " + list.points);
				if (list.type.equalsIgnoreCase("LINE") && list.points.contains(vertex) && list.points.contains(p1)) {
					//System.out.println("p1 : " + list.points);
					String halves[] = list.points.split(vertex);
					int index = halves[0].contains(p1) ? 0 : 1;
					s1 = p1;
					if (halves[index].length() > 1) {
						String[] halves2 = halves[index].split(p1);
						s1 += halves2[0];
						if (halves2.length == 2) s1 += halves2[1];
					} 
				}
				else if (list.type.equalsIgnoreCase("LINE") && list.points.contains(vertex) && list.points.contains(p2)) {
					//System.out.println("p2 : " + list.points);
					String halves[] = list.points.split(vertex);
					int index = halves[0].contains(p2) ? 0 : 1;
					s2 = p2;
					if (halves[index].length() > 1) {
						String[] halves2 = halves[index].split(p2);
						s2 += halves2[0];
						if (halves2.length == 2) s2 += halves2[1];
					}
				}
			}
			
			newp1 = "[" + s1 + s2 + "]";
			newp2 = "[" + s2 + s1 + "]";
			
			buff.append(parts[0]);
			buff.append(" ");
			buff.append(newp1);
			buff.append(vertex);
			buff.append(newp2);
		
			//System.out.println(selection + " | " + buff.toString());
			return buff.toString();
		}
		
		return selection;
	}
	
	// Transform what the brd sent, which could be a regular expression, to something that can be interpreted
	// and highlighted.  This is the inverse of the canonicallyName function
	private String deCanonicallyName(String selection) {
		StringBuffer buff = new StringBuffer();
		
		if (selection.contains("Angle") && selection.contains("[")) {
			String[] parts = selection.split(" ");
			// Angles are of the form CDG
			String[] sections = parts[1].split("[\\[\\]]");
			StringBuffer toAppend = new StringBuffer();
			
			if (sections.length == 3) { 							// when only the first part is a regex, 
				toAppend.append(sections[1].charAt(0));				// 3 because of empty string at beginning due to splitting
				toAppend.append(sections[2]);
			}
			else if (sections.length == 2) { // when only the second part is a regex
				toAppend.append(sections[0]);
				toAppend.append(sections[1].charAt(0));
			}
			else if (sections.length == 4){	// both parts are regex
				toAppend.append(sections[1].charAt(0));
				toAppend.append(sections[2]);
				toAppend.append(sections[3].charAt(0));
			} else {System.out.println("ERROR PARSING: " + selection); }
			
			buff.append(parts[0]);
			buff.append(" ");
			buff.append(toAppend);
			
			//System.out.println(selection + " | " + buff.toString());
			return buff.toString();
		}
		
		return selection;
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
	    
	    String studentSelection = getCurrentStudentSelection();
	    if (studentSelection != null && studentSelection.length() > 0) {
	    	setActionName(SELECTED_ACTION);
	    	v.addElement(getCurrentStateMessage());
	    }
	    
	    //if (colorList != null && !colorList.equals("")) {
	    //	setActionName(UPDATE_COLOR_LIST);
	    //	v.addElement(getCurrentStateMessage());
	    //}
	    
	    //setActionName(UPDATE_HAS_COLOR_CHOICE);
	    //v.addElement(getCurrentStateMessage());
	    
	    //setActionName(UPDATE_HAS_SUBMIT_BUTTON);
	    //v.addElement(getCurrentStateMessage());

		return v;
	}
	
	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////

	
	public Object getValue() {
		String tempValue = getPath();

		if (getActionName().equalsIgnoreCase(UPDATE_PATH) && getPath() != null && !getPath().equals(""))
			return getPath();
		else if (getActionName().equalsIgnoreCase(SELECTED_ACTION)) {

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
						
					// Canonical naming has some... issues.. with the BRD moving between states.
					//System.out.println("----------");
					//System.out.println("mpn: " + getController() + " " + getController().getCtatModeModel().isDemonstratingSolution());
					if (getController() != null && getController().isDemonstratingSolution())
						selection = canonicallyName(selection);
						
					if (buff.length() > 0) buff.append(", ");						
					buff.append(color);
					buff.append("-");
					buff.append(selection);
				}
			}
				
			return buff.toString();
		}
		else {
			if (commName.equalsIgnoreCase("Hint") || commName.equalsIgnoreCase("Help") || commName.equalsIgnoreCase("Done"))
				return "-1";
			else
				return tempValue;
		}

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

		if (action.equalsIgnoreCase(UPDATE_PATH)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "UpdatePath: " + input);
			setPath(input);
		}
		if (action.equalsIgnoreCase(SELECTED_ACTION)) {
			if (trace.getDebugCode("inter")) trace.out("inter", SELECTED_ACTION + ": " + input);

			clearSelectionMap();
				
			String[] sections = input.split(",");
			for (int i = 0; i < sections.length; i++) {
				String parts[] = sections[i].split("-");
				String color = parts[0].trim();
				String element = parts[1].trim();
					
				element = deCanonicallyName(element);
					
				selectionMap.get(color).add(element);
			}
				
			highlightCurrentDiagramSelection();
		}
		//else if (action.equalsIgnoreCase(UPDATE_COLOR_LIST)) {
		//	trace.out("inter", action + ":" + input);
		//	setColorList(input);
		//}
		//else if (action.equalsIgnoreCase(UPDATE_HAS_COLOR_CHOICE)) {
		//	trace.out("inter", action + ":" + input);
		//	setHasColorChoice(input);
		//}
		//else if (action.equalsIgnoreCase(UPDATE_HAS_SUBMIT_BUTTON)) {
		//	trace.out("inter", action + ":" + input);
		//	setHasSubmitButton(input);
		//}
	}
	
	/**
	 * This method is called when the state changes in the behavior recorder and
	 * the action type is a correct action
	 */
	public void doCorrectAction (String selection, String input) {
		// ??? 
		// this ensures that the next question is displayed only when the student 
		// has correctly answered the current question
		//if(!this.alreadyDone) {
		//	this.fireStudentAction(new StudentActionEvent(this));
		//	this.alreadyDone = true;
		//}

        if (getUniversalToolProxy().lockWidget()) {
            locked = true;
            diagram.highlightBySelectedText(diagram.getCurrentStudentSelection(), null);
            diagram.deselectAll();
            diagram.disableStudentSelection();
        }
        
        if (hasSubmitButton) { //what to do color-wise here?
        	//submitButton.setBackground(correctColor);
        	if (locked) submitButton.setEnabled(false);
        }	
        doCorrectVisualCue();
	}

	/**
	 * This method is called when the state changes in the behavior recorder and
	 * the action type is an incorrect action
	 */
	public void doIncorrectAction (String selection, String input) {
		locked = false;
        fireIncorrectAction(new IncorrectActionEvent(this));
        doIncorrectVisualCue();
        isCurrentlyIncorrect = true;
	}
	
	/**
     * Method called on receipt of correct action 
     * @param selection
     * @param action
     * @param input
     */
    public void doCorrectAction(String selection, String action, String input) {
    	if (trace.getDebugCode("dw")) trace.out("dw", "doCorrectAction: selection = " + selection + " action = " + action + " input = " + input);
    	if (SET_VISIBLE.equalsIgnoreCase(action))
    		setVisible(input);
    	else if (RESET.equalsIgnoreCase(action))
    		reset(getController());
    	else {
    		doInterfaceAction(selection, action, input);
    		doCorrectAction(selection, input);
    	}
    }
    
    /**
     * Method called on receipt of incorrect action 
     * @param selection
     * @param action
     * @param input
     */
    public void doIncorrectAction(String selection, String action, String input) {
    	if (trace.getDebugCode("dw")) trace.out("dw", "doIncorrectAction: selection = " + selection + " action = " + action + " input = " + input);
    	doInterfaceAction(selection, action, input);
    	doIncorrectAction(selection, input);
    }
	
	//////////////////////////////////////////////////////
	/**
		Returns a comm message which describes this interface
		element.
	*/
	//////////////////////////////////////////////////////
	public MessageObject getDescriptionMessage() {

		if (!initialize(getController())) {
			trace.out(5, this, "ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return null;
		}

        MessageObject mo = MessageObject.create("InterfaceDescription");
        mo.setVerb("SendNoteProperty");



		mo.setProperty("WidgetType", "CommGeoSelection");  // CommGeoDiagram ??
		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));
	
		mo.setProperty(HAS_SUBMIT_BUTTON, getHasSubmitButton());

		mo.setProperty(HAS_DEMONSTRATE_SUBMIT_BUTTON, getHasDemonstrateSubmitButton());

		mo.setProperty(HAS_COLOR_CHOICE, getHasColorChoice());

		mo.setProperty(COLOR_LIST, getColorList());

		mo.setProperty(SEND_SEGMENTS_ONLY, getSendSegmentsOnly());

		mo.setProperty(IS_SELECTION, getIsSelection());

		
		
		// jess stuff was here
		
		serializeGraphicalProperties(mo);


		return mo;
	}
	
	public void doInterfaceDescription(MessageObject messageObject) {
		ComponentDescription cd = new ComponentDescription(this);
    	cd.executeGraphicalProperties(messageObject);

        String colorList = (String) messageObject.getProperty(COLOR_LIST);
        Object hasColorChoice = messageObject.getProperty(HAS_COLOR_CHOICE);
        Object hasSubmitButton = messageObject.getProperty(HAS_SUBMIT_BUTTON);
        Object hasDemonstrateSubmitButton = messageObject.getProperty(HAS_DEMONSTRATE_SUBMIT_BUTTON);
        Object sendSegmentsOnly = messageObject.getProperty(SEND_SEGMENTS_ONLY);
        Object isSelection = messageObject.getProperty(IS_SELECTION);
        if (trace.getDebugCode("inter")) trace.out("inter", "doInterfaceDescription: colorList = " + colorList + " hasColorChoice = " + hasColorChoice
        		+ " hasSubmitButton = " + hasSubmitButton + " hasDemonstrateSubmitButton = " + hasDemonstrateSubmitButton
        		+ " sendSegmentsOnly = " + sendSegmentsOnly + " isSelection = " + isSelection);
        
        if (colorList == null || hasColorChoice == null || hasSubmitButton == null || isSelection == null)
            return;
        
        if (hasSubmitButton instanceof String)
        	setHasSubmitButton((String)hasSubmitButton);
        else
        	setHasSubmitButton((Boolean)hasSubmitButton);
        
        if (hasDemonstrateSubmitButton instanceof String)
        	setHasDemonstrateSubmitButton((String)hasDemonstrateSubmitButton);
        else
        	setHasDemonstrateSubmitButton((Boolean)hasDemonstrateSubmitButton);
        
        setColorList(colorList);
        
        if (hasColorChoice instanceof String)
        	setHasColorChoice((String)hasColorChoice);
        else
        	setHasColorChoice((Boolean)hasColorChoice);
        
        if (sendSegmentsOnly instanceof String)
        	setSendSegmentsOnly((String)sendSegmentsOnly);
        else
        	setSendSegmentsOnly((Boolean)sendSegmentsOnly);
        
        if (isSelection instanceof String)
        	setIsSelection(Boolean.valueOf(((String)isSelection)));
        else
        	setIsSelection((Boolean)isSelection);
    }
	
	//////////////////////////////////////////////////////
	/**
		Return true if any cells are not empty, otherwise false
	*/
	//////////////////////////////////////////////////////
	public boolean isChangedFromResetState() {
		String selection = getCurrentStudentSelection();
		return (selection != null && selection.length() > 0) || (path != null && path.length() > 0);
	}
	
	public void selectionOccurred(SelectionEvent e) {
		if (isCurrentlyIncorrect) {
			isCurrentlyIncorrect = false;
			doNormalVisualCue();
		}
		
		if (!hasSubmitButton && (!hasDemonstrateSubmitButton || getController() == null )|| !getController().isDemonstratingSolution()) {
			sendSelection();
		}
	}
	
	/**
	 * Send the current selection to CTAT, called from the GeoDiagram
	 *
	 */
	public void sendSelectionToCTAT() {
		if (isCurrentlyIncorrect) {
			isCurrentlyIncorrect = false;
			doNormalVisualCue();
		}
		
		if (!hasSubmitButton && (!hasDemonstrateSubmitButton || getController() == null )|| !getController().isDemonstratingSolution()) {
			sendSelection();
		}
	}
	
	// Helper method to get the current selection
	public String getCurrentStudentSelection() {
		if (sendSegmentsOnly)
			return diagram.getCurrentSegmentedStudentSelection();
		else
			return diagram.getCurrentStudentSelection();
	}
	
	// Actually sends the selection... used locally
	private void sendSelection() {
		setActionName(SELECTED_ACTION);
		dirty = true;
		sendValue();
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equalsIgnoreCase(BUTTON_SUBMIT_ACTION))
			sendSelection();
		else if (e.getActionCommand().equalsIgnoreCase(BUTTON_COLOR_CHANGE_ACTION)) {
			int newIndex = calculateCurrentlySelectedColorIndex();
			
			if (newIndex != currentColorIndex) {
			
				String selection = getCurrentStudentSelection();
				String currColor = colorList.get(currentColorIndex);
				selectionMap.put(currColor, new Vector<String>());
				
				if (selection.length() > 0) {
					String[] parts = selection.split(",");
					for (int i = 0; i < parts.length; i++) selectionMap.get(currColor).add(parts[i].trim());
				}

				currentColorIndex = newIndex;
				highlightCurrentDiagramSelection();
			}
		}
	}
	
	public void ctatModeEventOccured(CtatModeEvent e) {
		if (!getController().isDemonstratingSolution() && isInDemonstrateMode) {
			isInDemonstrateMode = false;
			if (hasDemonstrateSubmitButton && !hasSubmitButton)	removeDemonstrateSubmitButton();
		}
	}
}
