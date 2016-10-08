package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExactMatcher;
import edu.cmu.pact.BehaviorRecorder.View.BRPanel;

public class PasteSpecialDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	public enum EdgeAttribute
	{
		HINT_MESSAGE("Hint Message"),
		SUCCESS_MESSAGE("Success Message"),
		BUG_MESSAGE("Bug Message"),
		MATCHERS("Matchers"),
		ACTION_TYPE("Action Type"),
		ACTOR("Actor"),
		MIN_TRAVERSALS("Min Traversals"),
		SKILLS("Skills"),
		MAX_TRAVERSALS("Max Traversals");
		
		private final String label;
		
		EdgeAttribute(final String label)
		{
			this.label = label;
		}
		
		String getLabel()
		{
			return this.label;
		}
	}
	
	private static final String PASTE_COMMAND = "Paste";
	private static final String CANCEL_COMMAND = "Cancel";
	
	private static final String DIALOG_TITLE = "Paste Special...";
	private static final String LABEL_TEXT = "Select which attributes to paste";
	private static final String SELECT_ALL_MESSAGE = "Select all";
	private static final String DESELECT_ALL_MESSAGE = "Deselect all";
	private static final String[] TOGGLE_ALL_MESSAGES = { SELECT_ALL_MESSAGE, DESELECT_ALL_MESSAGE };
	private static final String SELECT_ALL_COMMAND = "Select All";
	
	private static final int DIALOG_WIDTH = 250;
	private static final int DIALOG_HEIGHT = 400;
	
	private EnumMap<EdgeAttribute, JCheckBox> checkBoxes;
	
	private EnumMap<EdgeAttribute, Boolean> selectionMap;
	private JCheckBox toggleAllBox;
	
	private JButton pasteButton;
	// used to persist selections on multiple uses of the dialog
	static EnumMap<EdgeAttribute, Boolean> lastSelectionMap = null;
	
	
	public PasteSpecialDialog(BR_Controller controller)
	{
		super(controller.getActiveWindow(), DIALOG_TITLE, true);
		this.setTitle(DIALOG_TITLE);
		this.selectionMap = null;
		
		setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
		setLocationRelativeTo(null);
		setResizable(false);
		initUI();
		pack();
	}
	
	private void initUI()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		
		JPanel labelPanel = buildLabelPanel();
		
		mainPanel.add(labelPanel);
		
		JPanel checkboxPanel = new JPanel();
		
		checkboxPanel.setLayout(new GridLayout(10,1));
		
		JCheckBox toggleAll = new JCheckBox(DESELECT_ALL_MESSAGE);
		toggleAll.setFont(BRPanel.BOLD_FONT);
		toggleAll.setActionCommand(SELECT_ALL_COMMAND);
		toggleAll.addActionListener(this);
		toggleAll.setSelected(true);
		//mainPanel.add(toggleAll);
		checkboxPanel.add(toggleAll);
		this.toggleAllBox = toggleAll;
		
		// initialize each text box
		EnumMap<EdgeAttribute, JCheckBox> checkboxMap = new EnumMap<EdgeAttribute, JCheckBox>(EdgeAttribute.class);
		for (EdgeAttribute ea : EdgeAttribute.values())
		{
			
			JCheckBox newBox = new JCheckBox(ea.getLabel());
			if (PasteSpecialDialog.lastSelectionMap != null && !PasteSpecialDialog.lastSelectionMap.get(ea).booleanValue())
			{
				newBox.setSelected(false);
			}
			else
			{
				newBox.setSelected(true);
			}
			newBox.addActionListener(this);

			newBox.setAlignmentX(Component.CENTER_ALIGNMENT);
			newBox.setMargin(new Insets(0,15,0,5)); // top, left, bottom, right
			checkboxMap.put(ea, newBox);
			//mainPanel.add(newBox);
			checkboxPanel.add(newBox);
		}
		
		mainPanel.add(checkboxPanel);
		
		this.checkBoxes = checkboxMap;
		
		JPanel buttonPanel = buildButtonPanel();
		
		mainPanel.add(buttonPanel);
		this.getContentPane().add(mainPanel);
		setVisible(true);
	}
	
	private JPanel buildLabelPanel()
	{
		JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel topLabel = new JLabel(LABEL_TEXT);
		topLabel.setFont(BRPanel.BOLD_FONT);
		labelPanel.add(topLabel);
		return labelPanel;
	}
	
	private JPanel buildButtonPanel()
	{
		// make the buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton pasteButton = new JButton(PASTE_COMMAND);
		pasteButton.setActionCommand(PASTE_COMMAND);
		pasteButton.addActionListener(this);
		
		JButton cancelButton = new JButton(CANCEL_COMMAND);
		cancelButton.setActionCommand(CANCEL_COMMAND);
		cancelButton.addActionListener(this);
		
		buttonPanel.add(pasteButton);
		buttonPanel.add(cancelButton);
		this.pasteButton = pasteButton;
		
		return buttonPanel;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		boolean shouldClose = false;
		if (arg0.getActionCommand().equals(SELECT_ALL_COMMAND))
		{
			boolean allSelectedStatus = this.toggleAllBox.isSelected();
			int msgIdx = allSelectedStatus? 1 : 0;
			this.toggleAllBox.setText(TOGGLE_ALL_MESSAGES[msgIdx]);
			for (JCheckBox box : this.checkBoxes.values())
			{
				box.setSelected(allSelectedStatus);
			}
			if (!allSelectedStatus)
			{
				pasteButton.setEnabled(false);
			}
			else
			{
				pasteButton.setEnabled(true);
			}
			return;
		}
		else if (arg0.getActionCommand().equals(PASTE_COMMAND))
		{
			EnumMap<EdgeAttribute, Boolean> newMap = new EnumMap<EdgeAttribute, Boolean>(EdgeAttribute.class);
			
			for (EdgeAttribute ea : this.checkBoxes.keySet())
			{
				JCheckBox curBox = checkBoxes.get(ea);
				Boolean isSelected = new Boolean(curBox.isSelected());
				newMap.put(ea, isSelected);
			}
			PasteSpecialDialog.lastSelectionMap = newMap;
			this.selectionMap = newMap;
			shouldClose = true;
		}
		else if (arg0.getActionCommand().equals(CANCEL_COMMAND))
		{
			shouldClose = true;
		}
		
		// make sure at least one attribute is selected
		boolean atLeastOneSelected = false;
		for (JCheckBox chkbox : this.checkBoxes.values())
		{
			if (chkbox.isSelected())
			{
				atLeastOneSelected=true;
				break;
			}
		}
		this.pasteButton.setEnabled(atLeastOneSelected);
		if (!atLeastOneSelected)
		{
			this.toggleAllBox.setSelected(false);
			this.toggleAllBox.setText(TOGGLE_ALL_MESSAGES[0]);
		}
		else
		{
			this.toggleAllBox.setSelected(true);
			this.toggleAllBox.setText(TOGGLE_ALL_MESSAGES[1]);
		}
		if (shouldClose)
		{
			setVisible(false);
			dispose();
		}
		
	}
	
	private void applyEdgeAttribute(EdgeAttribute ea, EdgeData ed, Object value)
	{
		try
		{
			switch (ea)
			{
			case HINT_MESSAGE:
				ed.setHints((Vector<String>)value);
				break;
			case SUCCESS_MESSAGE:
				ed.setSuccessMsg((String)value);
				break;
			case BUG_MESSAGE:
				ed.setBuggyMsg((String)value);
				break;
			case MATCHERS:
				ed.setMatcher((ExactMatcher)value);
				break;
			case ACTION_TYPE:
				ed.setActionType((String)value);
				break;
			case ACTOR:
				ed.setActor((String)value);
				break;
			case MIN_TRAVERSALS:
				ed.setMinTraversals((Integer)value);
				break;
			case SKILLS:
				
				Vector<String> skills = (Vector<String>) ed.getRuleNames().clone();
				System.out.println("Skills from the original edge: " + skills.toString());
				for (String s : skills)
				{
					ed.removeRuleName(s);
				}
				Vector<String> newSkills = null;
				try
				{
					 newSkills = (Vector<String>)value;
					 System.out.println("New skills " + newSkills);
				}
				catch (Exception ex)
				{
					System.err.println("Exception " + ex + " thrown when trying to overwrite skills in EdgeData " + ed);
				}
				
				for (String s : newSkills)
				{
					System.out.println("Adding the following skill: " + s);
					ed.addRuleName(s);
				}
				break;
			case MAX_TRAVERSALS:
				ed.setMaxTraversals((Integer)value);
				break;
			}
		}
		catch (ClassCastException ex)
		{
			System.err.println("Error casting intended edge values in PasteSpecialDialog");
		}
		
	}
	
	public void applyDialogSelection(ProblemEdge edge, EnumMap<EdgeAttribute, Object> valueMap, boolean invertSelection)
	{
		Collection<ProblemEdge> tempCollection = new ArrayList<ProblemEdge>();
		tempCollection.add(edge);
		applyDialogSelection(tempCollection, valueMap, invertSelection);
	}
	
	public void applyDialogSelection(Collection<ProblemEdge> edges, EnumMap<EdgeAttribute, Object> valueMap, boolean invertSelection)
	{
		if (this.selectionMap == null) return;
    	
    	// get the set of not selected attributes
    	EnumSet<EdgeAttribute> attributesNotPasted = EnumSet.allOf(EdgeAttribute.class);
    	for (EdgeAttribute key : this.selectionMap.keySet())
    	{
    		if (!invertSelection && this.selectionMap.get(key).booleanValue())
    		{
    			attributesNotPasted.remove(key);
    		}
    		else if (invertSelection && !this.selectionMap.get(key).booleanValue())
    		{
    			attributesNotPasted.remove(key);
    		}
    	}
    	
    	for (ProblemEdge ed : edges)
    	{
    		for (EdgeAttribute ea : EnumSet.complementOf(attributesNotPasted))
    		{
    			this.applyEdgeAttribute(ea, ed.getEdgeData(), valueMap.get(ea));
    		}
    	}
	}
	
	public EnumMap<EdgeAttribute, Boolean> getSelectedAttributes()
	{
		return this.selectionMap;
	}
	
	public void setSelectedAttributes(EnumMap<EdgeAttribute, Boolean> newSelectedAttributes)
	{
		this.selectionMap = newSelectedAttributes;
	}
}
