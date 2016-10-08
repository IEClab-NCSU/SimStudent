/*
 * Created on Apr 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.RangeMatcher;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.trace;


public class RangeMatcherPanel extends MatcherPanel {

/*	private JTextField actionField;
	private JTextField selectionField;
*/	JTextField maximumField;
	JTextField minimumField;
/*    private EdgeData edgeData;
    private JRadioButton toolButton;
	private JRadioButton studentButton;
    private JList sOptions;
	private String[] students; */
	public RangeMatcherPanel (EdgeData edgeData, boolean allow, int max_students ) {
		this(edgeData, allow, max_students, 0);
	}

	public RangeMatcherPanel (EdgeData edgeData, boolean allow, int max_students, int selectionIndex ) {
		super(edgeData, allow, max_students, "<html>Range Match will match numeric values for the student's input <br>" +
				"against a minimum and maximum value for the given selection and action.<br><br>" +
				"Example: minimum 0 maximum 150 would match 0, 10, 22.3213232, and 150, <br>" +
				"but not -1, 150.001, or 100,000", new Box(BoxLayout.X_AXIS)); 

		if (trace.getDebugCode("range")) trace.out("range", "RangeMatcherPanel("+edgeData.getMatcher()+", "+edgeData.getInput()+")");		
		
		JLabel minimumLabel = new JLabel ("Minimum input value: ");
		minimumLabel.setFont(new Font(null, Font.PLAIN, 11));
		c.gridx = 0;
		c.gridy = 2;
		SAIPane.add(minimumLabel, c);
		
		minimumField = new JTextField();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(minimumField); }
		minimumField.setName("minimumField");
		minimumField.setColumns (30);
		minimumField.setFont(new Font(null, Font.PLAIN, 11));
		c.gridx = 1;
		SAIPane.add(minimumField, c);
		
		JLabel maximumLabel = new JLabel ("Maximum input value: ");
		maximumLabel.setFont(new Font(null, Font.PLAIN, 11));
		c.gridx = 0;
		c.gridy = 3;
		SAIPane.add(maximumLabel, c);
		
		maximumField = new JTextField();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(maximumField); }
		maximumField.setName("maximumField");
		maximumField.setColumns (30);
		minimumField.setFont(new Font(null, Font.PLAIN, 11));
		c.gridx = 1;
		SAIPane.add(maximumField, c);
		
		/*
		super (BoxLayout.Y_AXIS);
		this.edgeData = edgeData;
        
		JLabel instructions = new JLabel(
				"<html>Range Match will match numeric values for the student's input <br>" +
				"against a minimum and maximum value for the given selection and action.<br><br>" +
				"Example: minimum 0 maximum 150 would match 0, 10, 22.3213232, and 150, <br>" +
				"but not -1, 150.001, or 100,000");
		
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
		
		JLabel minimumLabel = new JLabel ("Minimum input value: ");
		minimumField = new JTextField();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(minimumField); }
		minimumField.setName("minimumField");
		minimumField.setColumns (30);
		Box minimumBox = new Box (BoxLayout.X_AXIS);
		minimumBox.add(minimumLabel);
		minimumBox.add(minimumField);
		
		JLabel maximumLabel = new JLabel ("Maximum input value: ");
		maximumField = new JTextField();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(maximumField); }
		maximumField.setName("maximumField");
		maximumField.setColumns (30);
		Box maximumBox = new Box (BoxLayout.X_AXIS);
		maximumBox.add(maximumLabel);
		maximumBox.add(maximumField);
		*/
		
        if (edgeData.getMatcher() instanceof RangeMatcher) {
            RangeMatcher r = (RangeMatcher) edgeData.getMatcher();
            actionField.setText(r.getAction());
            selectionField.setText(r.getSelection());
            minimumField.setText(r.getMinimumStr());
            maximumField.setText(r.getMaximumStr());
        } else {
    		actionField.setText(edgeData.getAction().get(0).toString());
    		selectionField.setText(edgeData.getSelection().get(selectionIndex).toString());
    		if (edgeData.getInput() != null && edgeData.getInput().size() > 0) {
    			String inputStr = edgeData.getInput().get(0).toString();
    			minimumField.setText(inputStr);
    			maximumField.setText(inputStr);
    		} else {
    			minimumField.setText(Double.toString(RangeMatcher.DEFAULT_MINIMUM));
    			maximumField.setText(Double.toString(RangeMatcher.DEFAULT_MAXIMUM));
    		}
        }
//        Box actorBox= new Box(BoxLayout.X_AXIS);
		/*if(allow)
		{
		JLabel actorLabel = new JLabel ("Actor: ");
		toolButton = new JRadioButton("Tutor");		
		studentButton = new JRadioButton("");
		ButtonGroup tvs = new ButtonGroup();
		tvs.add(toolButton);
		tvs.add(studentButton);
	//	students = new String[max_students];
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
        //actorBox.add(Students);
		}*/
		/*add (WindowUtils.wrapLeft(instructions));
		add (Box.createVerticalStrut(10));
		add (WindowUtils.wrapLeft(minimumBox));
		add (Box.createVerticalStrut(5));
		add (WindowUtils.wrapLeft(maximumBox));
		add (Box.createVerticalStrut(5));
		add (WindowUtils.wrapLeft(selectionBox));
		add (Box.createVerticalStrut(5));
		add (WindowUtils.wrapLeft(actionBox));
		add (Box.createVerticalStrut(5));
		if (allow) 
		
			add (WindowUtils.wrapLeft(actorBox));*/
	
	}
	
	/**
	 * This is a scaled-down version of the Range matcher panel
	 * It includes only the input JLabels and JTextfields
	 */
	public RangeMatcherPanel(String text)
	{
		super(BoxLayout.Y_AXIS);
		
		if (trace.getDebugCode("range")) trace.out("range", "RangeMatcherPanel("+text+")");		
		String minText = (text == null || text.length() == 0 ? Double.toString(RangeMatcher.DEFAULT_MINIMUM) : text);
		String maxText = null;
		int minIndex, commaIndex, maxIndex;
		if((minIndex = text.indexOf("[")) >= 0 &&
				(commaIndex = text.indexOf(",", minIndex + 1)) >= 0 &&
				(maxIndex = text.indexOf("]", commaIndex + 1)) >= 0)
		{
			minText = text.substring(minIndex + 1, commaIndex);
			maxText = text.substring(commaIndex + 1, maxIndex);
		}
		if (maxText == null)
			maxText = minText;
		
		SAIPane = new JPanel(new GridBagLayout());
		c = new GridBagConstraints();
		
		c.ipadx = 0;
		c.ipady = 0;
		
		JLabel minimumLabel = new JLabel ("Min:");
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 2;
		SAIPane.add(minimumLabel, c);
		
		minimumField = new JTextField();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(minimumField); }
		minimumField.setName("minimumField");
		minimumField.setColumns(17);
		minimumField.setText(minText);
		minimumField.setSelectionStart(0);
		minimumField.setSelectionEnd(0);
		c.insets = new Insets(5, 0, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 1;
		SAIPane.add(minimumField, c);
				
		JLabel maximumLabel = new JLabel("Max:");
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 3;
		SAIPane.add(maximumLabel, c);
		
		maximumField = new JTextField();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(maximumField); }
		maximumField.setName("maximumField");
		maximumField.setColumns(17);
		maximumField.setText(maxText);
		maximumField.setSelectionStart(0);
		maximumField.setSelectionEnd(0);
		c.insets = new Insets(5, 0, 5, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 1;
		SAIPane.add(maximumField, c);
		
		add(SAIPane);
	}

	/* (non-Javadoc)
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels.MatcherPanel#getMatcher()
	 */
	public Matcher createMatcher() {
		RangeMatcher rm  = new RangeMatcher();
		rm.setAction(actionField.getText());
		rm.setSelection(((JTextField)selectionField).getText());
		rm.setMinimum(minimumField.getText());
		rm.setMaximum(maximumField.getText());
       
       /* if (toolButton!=null)
        {
        	if (toolButton.isSelected())
        		rm.setActor("Tool");
        	else {
        		rm.setActor("Student");
        	}
        }
        else
        */ rm.setActor("Student");
        	
        
		Matcher m = edgeData.getMatcher();
        if (m != null) {
            rm.setDefaultInput(m.getDefaultInput());
            rm.setDefaultAction(m.getDefaultAction());
            rm.setDefaultSelection(m.getDefaultSelection());
            rm.setDefaultActor(m.getDefaultActor());
        }

		return rm;
	}
	
	public String getMatcherType()
	{
		return Matcher.RANGE_MATCHER;
	}
}
