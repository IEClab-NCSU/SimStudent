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
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.RegexMatcher;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;


public class RegexMatcherPanel extends MatcherPanel {

	/*private JTextField actionField;
	private JTextField selectionField;
	*/
	private JTextField inputField;
	/*private EdgeData edgeData;
	private JRadioButton toolButton;
	private JRadioButton studentButton;
    private JList sOptions;
	private String[] students;	*/
	
	public RegexMatcherPanel (EdgeData edgeData, boolean allow, int max_students) {
		this(edgeData, allow, max_students, 0);
	}
	
	public RegexMatcherPanel (EdgeData edgeData, boolean allow, int max_students, int selectionIndex)
	{
		
		super(edgeData, allow, max_students, "<html>Regular Expression Match will match student's input using <br>" +
				"the java regular expression engine (similar to perl-style regular expressions).<br><br>" +
				"For a tutorial on using regular expressions see: <br>"  + 
                "http://www.regular-expressions.info/tutorial.html<br><br>" +
                "For a complete description of regular expressions in java see: <br>" + 
                "http://java.sun.com/docs/books/tutorial/extra/regex/<br>", new Box(BoxLayout.X_AXIS));
		
		//note ... the actor box was in original code ... doesn't seem to do anything, but is left in
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
		
		/*super (BoxLayout.Y_AXIS);
		
        this.edgeData = edgeData;
		JLabel instructions = new JLabel(
				"<html>Regular Expression Match will match student's input using <br>" +
				"the java regular expression engine (similar to perl-style regular expressions).<br><br>" +
				"For a tutorial on using regular expressions see: <br>"  + 
                "http://www.regular-expressions.info/tutorial.html<br><br>" +
                "For a complete description of regular expressions in java see: <br>" + 
                "http://java.sun.com/docs/books/tutorial/extra/regex/<br>");
		
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
		inputBox.add(inputField); 
		*/
        if (edgeData.getMatcher() != null && edgeData.getMatcher() instanceof RegexMatcher) {
            RegexMatcher m = (RegexMatcher) edgeData.getMatcher();
            actionField.setText (m.getActionPattern());
            selectionField.setText (m.getSelectionPattern());
            inputField.setText (m.getInputPattern());
        } else {
    		actionField.setText (edgeData.getAction().get(0).toString());
    		selectionField.setText (edgeData.getSelection().get(selectionIndex).toString());
    		inputField.setText (edgeData.getInput().get(0).toString());
        }
//        Box actorBox= new Box(BoxLayout.X_AXIS);
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
		add (WindowUtils.wrapLeft(selectionBox));
		add (Box.createVerticalStrut(5));
		add (WindowUtils.wrapLeft(actionBox));
		add (Box.createVerticalStrut(5));
		add (WindowUtils.wrapLeft(inputBox));
		add (Box.createVerticalStrut(5));
		if (allow) 
		
			add (WindowUtils.wrapLeft(actorBox));
	*/
	}

	/* (non-Javadoc)
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels.MatcherPanel#getMatcher()
	 */
	public Matcher createMatcher() {
		RegexMatcher am  = new RegexMatcher();
		am.setActionPattern(actionField.getText());
		am.setSelectionPattern(((JTextField)selectionField).getText());
		am.setInputPattern(inputField.getText());
		/*  if (toolButton!=null)
	        {
	        	if (toolButton.isSelected())
	        		am.setActorPattern("Tool");
	        	else {
	        		am.setActorPattern("Student");
	        	}
	        }
	        else 
	        */am.setActorPattern("Student");
	        	
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
		return Matcher.REGULAR_EXPRESSION_MATCHER;
	}
}
