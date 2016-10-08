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

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.AnyMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.Utilities.WindowUtils;


public class AnyMatcherPanel extends MatcherPanel {

	/*private JTextField actionField;
	private JTextField selectionField;
    private EdgeData edgeData;
    private JRadioButton toolButton;
	private JRadioButton studentButton;
    private JList sOptions;
	private String[] students;*/ 
	
	//we ignore selection index since it doesn't matter ...
	public AnyMatcherPanel (EdgeData edgeData, boolean allow, int max_students, int selectionIndex ) {
		this(edgeData, allow, max_students);
	}
	
	public AnyMatcherPanel (EdgeData edgeData, boolean allow, int max_students ) {
	
		super(edgeData, allow, max_students, "<html>Any Match will match any input for the <br>" +
				"given selection and action.", null);
		
		JLabel inputLabel = new JLabel ("Input: ");
		inputLabel.setName("inputLabel");
		c.gridx = 0;
		c.gridy = 2;
		SAIPane.add(inputLabel, c);
		
		c.gridx = 1;
		SAIPane.add(new JLabel("<html><b>Any</b>"), c);
		
		Box actorBox= new Box(BoxLayout.X_AXIS);
		if(allow)
			add(WindowUtils.wrapLeft(actorBox));
		
		/*
	 	super (BoxLayout.Y_AXIS);
		this.edgeData = edgeData;
        
		JLabel instructions = new JLabel(
				"<html>Any Match will match any input for the <br>" +
				"given selection and action.");
		
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
		
		JLabel inputLabel = new JLabel ("<html>Input: <b>Any</b>");
		inputLabel.setName("inputLabel");
		
		actionField.setText (edgeData.getAction().get(0).toString());
		selectionField.setText (edgeData.getSelection().get(0).toString());
		Box actorBox= new Box(BoxLayout.X_AXIS);
		
		*/
		/*if(allow)
		{
		JLabel actorLabel = new JLabel ("Actor: ");
		toolButton = new JRadioButton("Tutor");		
		studentButton = new JRadioButton("Student");
		ButtonGroup tvs = new ButtonGroup();
		tvs.add(toolButton);
		tvs.add(studentButton);
		//students = new String[max_students];
		studentButton.setSelected(true);
		for (int i=0; i<max_students; i++)
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
        //actorBox.add(Students);
		}*/
		/*add (WindowUtils.wrapLeft(instructions));
		add (Box.createVerticalStrut(10));
		add (WindowUtils.wrapLeft(selectionBox));
		add (Box.createVerticalStrut(5));
		add (WindowUtils.wrapLeft(actionBox));
		add (Box.createVerticalStrut(5));
		add (WindowUtils.wrapLeft(inputLabel));
		add (Box.createVerticalStrut(5));*/
		/*if (allow) 
		
			add (WindowUtils.wrapLeft(actorBox));*/
	
	}

	/* (non-Javadoc)
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels.MatcherPanel#getMatcher()
	 */
	public Matcher createMatcher() {
		AnyMatcher am  = new AnyMatcher();
		am.setAction(actionField.getText());
		am.setSelection(selectionField.getText());
		/*  if (toolButton!=null)
	        {
	        	if (toolButton.isSelected())
	        		am.setActor("Tool");
	        	else {
	        		am.setActor("Student");
	        	}
	        }
	        else */
		am.setActor("Student");
	    
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
		return Matcher.ANY_MATCHER;
	}
}
