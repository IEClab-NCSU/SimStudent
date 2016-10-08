/*
 * Created on Apr 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExactMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.WindowUtils;


public class ExactMatcherPanel extends MatcherPanel {

	/*private JTextField actionField;
	private JTextField selectionField;
	*/
	private JTextField inputField;
	
    /*private EdgeData edgeData;
    private JRadioButton toolButton;
	private JRadioButton studentButton;
	*/
	private JRadioButton anyButton;
    
	/*private JList sOptions;
	private String[] students; */

	public ExactMatcherPanel (EdgeData edgeData, boolean allowToolReportedActions, int max_students) {
		this(edgeData, allowToolReportedActions, max_students, 0);
	}
	
	public ExactMatcherPanel (EdgeData edgeData, boolean allowToolReportedActions, int max_students, int selectionIndex)
	{
		super(edgeData, allowToolReportedActions, max_students, "<html>Exact Matcher will match the exact selection, action, and input<br>" +
				"specified below.", new Box(BoxLayout.X_AXIS));
		
		JLabel inputLabel = new JLabel ("Input: ");
		inputLabel.setName("inputLabel");
		c.gridx = 0;
		c.gridy = 2;
		SAIPane.add(inputLabel, c);
		
		inputField = new JTextField();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(inputField); }
		inputField.setName("inputField");
		inputField.setColumns (30);
		c.gridx = 1;
		SAIPane.add(inputField, c);
		
		/*
		super (BoxLayout.Y_AXIS);
		this.edgeData = edgeData;
        
		JLabel instructions = new JLabel(
				"<html>Exact Matcher will match the exact selection, action, and input<br>" +
				"specified below.");
		
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
		inputField = new JTextField ();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(inputField); }
		inputField.setName("inputField");
		inputField.setColumns (30);
		Box inputBox = new Box (BoxLayout.X_AXIS);
		inputBox.add(inputLabel);
		inputBox.add(inputField);
		Box actorBox= new Box(BoxLayout.X_AXIS);
		
		*/
		Box actorBox = (Box)postComponent;
		if(allowToolReportedActions)
		{
			JLabel actorLabel = new JLabel ("Actor: ");
			toolButton = new JRadioButton(Matcher.DEFAULT_TOOL_ACTOR);		
			ungradedButton = new JRadioButton(Matcher.UNGRADED_TOOL_ACTOR);		
			studentButton = new JRadioButton(Matcher.DEFAULT_STUDENT_ACTOR);
			anyButton = new JRadioButton(Matcher.ANY_ACTOR);
			toolButton.setToolTipText(Matcher.DEFAULT_TOOL_ACTOR_TOOLTIP);
			ungradedButton.setToolTipText(Matcher.UNGRADED_TOOL_ACTOR_TOOLTIP);
			studentButton.setToolTipText(Matcher.DEFAULT_STUDENT_ACTOR_TOOLTIP);
			anyButton.setToolTipText(Matcher.ANY_ACTOR_TOOLTIP);
			ButtonGroup tvs = new ButtonGroup();
			tvs.add(toolButton);
			tvs.add(ungradedButton);
			tvs.add(studentButton);
			tvs.add(anyButton);
			//students = new String[max_students];
			/*for (int i=0; i<max_students; i++)
			 {
			 students[i]= new String("Student" + (i+1));
			 }
			 sOptions = new JList(students);
			 sOptions.setSelectedIndex(0);
			 sOptions.setVisibleRowCount(3);
			 sOptions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			 */
			JScrollPane Students = new JScrollPane(sOptions);
			actorBox = new Box(BoxLayout.X_AXIS);
			actorBox.add(actorLabel);
			actorBox.add(toolButton);
			actorBox.add(ungradedButton);
			actorBox.add(studentButton);
			actorBox.add(anyButton);
			//  actorBox.add(Students);
			add(WindowUtils.wrapLeft(actorBox));
		}
		
		if (edgeData.getMatcher() == null || ! (edgeData.getMatcher() instanceof ExactMatcher)) {
			actionField.setText(edgeData.getAction().get(0).toString());
			selectionField.setText(edgeData.getSelection().get(selectionIndex).toString());
	        inputField.setText(edgeData.getInput().get(0).toString());
            if (studentButton != null)
                studentButton.setSelected(true);
		} else {
			ExactMatcher em = (ExactMatcher) edgeData.getMatcher();
			actionField.setText(em.getAction());
			selectionField.setText(em.getSelection());
			inputField.setText(em.getInput());
			if (allowToolReportedActions) {
				if (em.getActor().equals(Matcher.DEFAULT_TOOL_ACTOR))
					toolButton.setSelected(true);
				else if (em.getActor().equals(Matcher.UNGRADED_TOOL_ACTOR))
					ungradedButton.setSelected(true);
				else if (em.getActor().equals(Matcher.ANY_ACTOR))
					anyButton.setSelected(true);
				else
					studentButton.setSelected(true);
			}
		}
		/*
		add (WindowUtils.wrapLeft(instructions));
		add (Box.createVerticalStrut(10));
		add (WindowUtils.wrapLeft(selectionBox));
		add (Box.createVerticalStrut(5));
		add (WindowUtils.wrapLeft(actionBox));
		add (Box.createVerticalStrut(5));
		add (WindowUtils.wrapLeft(inputBox));
		add (Box.createVerticalStrut(5));
		if (allowToolReportedActions) 
		
			add (WindowUtils.wrapLeft(actorBox));
		*/
		
		}

	/* (non-Javadoc)
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels.MatcherPanel#getMatcher()
	 */
	public Matcher createMatcher() {
		ExactMatcher am  = new ExactMatcher();
		am.setDefaultAction(actionField.getText());
		am.setDefaultSelection(selectionField.getText());
		am.setDefaultInput (inputField.getText());
		if (toolButton!=null && toolButton.isSelected())
			am.setDefaultActor(Matcher.DEFAULT_TOOL_ACTOR);
		else if (ungradedButton !=null && ungradedButton.isSelected())
			am.setDefaultActor(Matcher.UNGRADED_TOOL_ACTOR);
		else if (anyButton!=null && anyButton.isSelected())
			am.setDefaultActor(Matcher.ANY_ACTOR);
		else if (studentButton!=null && studentButton.isSelected())
			am.setDefaultActor(Matcher.DEFAULT_STUDENT_ACTOR);
		else
			am.setDefaultActor(Matcher.DEFAULT_ACTOR);
		
		return am;
	}
	
	public String getMatcherType()
	{
		return Matcher.EXACT_MATCHER;
	}
}
