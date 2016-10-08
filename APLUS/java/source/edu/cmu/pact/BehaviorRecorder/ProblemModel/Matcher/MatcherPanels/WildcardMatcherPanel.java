/*
 * Created on Apr 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.WildcardMatcher;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;


public class WildcardMatcherPanel extends MatcherPanel {

	/*private JTextField actionField;
	private JTextField selectionField;
	*/
	private JTextField inputField;
    /*private EdgeData edgeData;
    private JRadioButton toolButton;
	private JRadioButton studentButton;
    private JList sOptions;
	private String[] students;
	*/
	public WildcardMatcherPanel(EdgeData edgeData, boolean allow, int max_students) {
		this(edgeData, allow, max_students, 0);
	}
	
	public WildcardMatcherPanel(EdgeData edgeData, boolean allow, int max_students, int selectionIndex) {
		
		super(edgeData, allow, max_students, "<html>Wildcard Match will match student actions using patterns <br>" +
				"you can specify below.  It uses a simple regular expression pattern where * <br>" +
				"represents any number of characters.  Special characters such as {, \\, or & should be avoided. <br><br>" +
		"Example 1:  \"*_text\" would match \"_text\", \"this_text\", \"xxx_text\", or \"123abc_text\"<br>" +
		"Example 2:  \"abc*xyz\" would match \"abcxyz\", or \"abc123xyz\"", new Box(BoxLayout.X_AXIS));
		
		JLabel inputLabel = new JLabel ("Input: ");
		inputLabel.setName("inputLabel");
		c.gridx = 0;
		c.gridy = 2;
		SAIPane.add(inputLabel, c);
		
		inputField = new JTextField();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(inputField); }
		inputField.setName("inputField");
		inputField.setColumns(30);
		c.gridx = 1;
		SAIPane.add(inputField, c);
		
		/*super (BoxLayout.Y_AXIS);
		this.edgeData = edgeData;
        
		JLabel instructions = new JLabel(
				"<html>Wildcard Match will match student actions using patterns <br>" +
				"you can specify below.  It uses a simple regular expression pattern where * <br>" +
				"represents any number of characters.  Special characters such as {, \\, or & should be avoided. <br><br>" +
		"Example 1:  \"*_text\" would match \"_text\", \"this_text\", \"xxx_text\", or \"123abc_text\"<br>" +
		"Example 2:  \"abc*xyz\" would match \"abcxyz\", or \"abc123xyz\"");
		
		JLabel selectionLabel = new JLabel ("Selection: ");
		selectionField = new JTextField();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(selectionField); }
		selectionField.setName("selectionField");
		selectionField.setColumns (30);
		Box selectionBox = new Box (BoxLayout.X_AXIS);
		selectionBox.add(selectionLabel);
		selectionBox.add(selectionField);
		
		JLabel actionLabel = new JLabel ("Action: ");
		actionField = new JTextField();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(actionField); }
		actionField.setName("actionField");
		actionField.setColumns(30);
		Box actionBox = new Box (BoxLayout.X_AXIS);
		actionBox.add(actionLabel);
		actionBox.add(actionField);
		
		JLabel inputLabel = new JLabel ("Input: ");
		inputField = new JTextField();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(inputField); }
		inputField.setName("inputField");
		inputField.setColumns (30);
		Box inputBox = new Box (BoxLayout.X_AXIS);
		inputBox.add(inputLabel);
		inputBox.add(inputField);*/
		
        if (edgeData.getMatcher() instanceof WildcardMatcher) {
            WildcardMatcher m = (WildcardMatcher) edgeData.getMatcher();
            actionField.setText(m.getSimpleActionPattern());
            ((JTextField)selectionField).setText(m.getSimpleSelectionPattern());
            inputField.setText(m.getSimpleInputPattern());
        } else {
        	selectionField.setText(edgeData.getSelection().get(selectionIndex).toString());
        	actionField.setText(edgeData.getAction().get(0).toString());
            inputField.setText(edgeData.getInput().get(0).toString());
        }
        Box actorBox = new Box(BoxLayout.X_AXIS);
		/*if(allow)
		{
		JLabel actorLabel = new JLabel ("Actor: ");
		toolButton = new JRadioButton("Tutor");		
		studentButton = new JRadioButton("");
		ButtonGroup tvs = new ButtonGroup();
		tvs.add(toolButton);
		tvs.add(studentButton);
		//students = new String[max_students];
		studentButton.setSelected(true);
		/*for (int i=0; i<max_students; i++)
		{
			students[i]= new String("Student" + (i+1));
		}
		sOptions = new JList(students);
		sOptions.setSelectedIndex(0);
        sOptions.setVisibleRowCount(3);
        sOptions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
       
        JScrollPane Students = new JScrollPane(sOptions);
        actorBox= new Box(BoxLayout.X_AXIS);
        actorBox.add(actorLabel);
        actorBox.add(toolButton);
        actorBox.add(studentButton);
       // actorBox.add(Students);
		}*/
		/*add (WindowUtils.wrapLeft(instructions));
		add (Box.createVerticalStrut(10));
		add (WindowUtils.wrapLeft(selectionBox));
		add (Box.createVerticalStrut(5));
		add (WindowUtils.wrapLeft(actionBox));
		add (Box.createVerticalStrut(5));
		add (WindowUtils.wrapLeft(inputBox));
		add (Box.createVerticalStrut(5));
		if (allow) 
		
			add (WindowUtils.wrapLeft(actorBox));*/
	
	}

	/* (non-Javadoc)
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels.MatcherPanel#getMatcher()
	 */
	public Matcher createMatcher() {
		WildcardMatcher am  = new WildcardMatcher();
		am.setActionPattern(actionField.getText());
		am.setSelectionPattern(((JTextField)selectionField).getText());
		am.setInputPattern(inputField.getText());
		/*  if (toolButton!=null)
	        {
	        	if (toolButton.isSelected())
	        		{
	        		am.setActorPattern("Tool");
	        		
	        		}
	        	else {
	        		am.setActorPattern("Student");
	        	}
	        }
	        else */
		am.setActorPattern("Student");
	        	
        Matcher m = edgeData.getMatcher();
        if (m != null) {
            am.setDefaultInput(m.getDefaultInput());
            am.setDefaultAction(m.getDefaultAction());
            am.setDefaultSelection(m.getDefaultSelection());
            am.setDefaultActor(m.getDefaultActor());
        }

		return am;
	}
	
	public String getMatcherType()
	{
		return Matcher.WILDCARD_MATCHER;
	}
}
