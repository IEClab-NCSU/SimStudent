/*
 * Created on Apr 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.WindowUtils;

/**
 * @author mpschnei
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class MatcherPanel extends Box {

	protected JTextField actionField;
	protected JTextField selectionField;
	protected JComponent postComponent; //reference to any JComponent that comes after textfields
	protected EdgeData edgeData;
	protected JRadioButton toolButton;
	protected JRadioButton ungradedButton;
	protected JRadioButton studentButton;
	protected JList sOptions;
	protected String[] students;
	
	protected JPanel SAIPane; //used to include Input field(s), label(s), and other changes
	protected GridBagConstraints c; //used for the SAIPane
	
	/**
	 * 
	 * @param edgeData - data for edges
	 * @param allow
	 * @param max_students
	 * @param instructions - how to use the panel
	 * @param postComponent - any JComponent that comes after the textfields
	 * @param index - index corresponding to the sai vectors we want to display
	 */

	public MatcherPanel(EdgeData edgeData, boolean allow, int max_students,
			String instructions, JComponent postComponent, int index)
	{
		super(BoxLayout.Y_AXIS);
		
		this.edgeData = edgeData;
		
		JLabel instructionsLabel = new JLabel(instructions);
		add(WindowUtils.wrapLeft(instructionsLabel));
		
		SAIPane = new JPanel(new GridBagLayout());
		add(WindowUtils.wrapLeft(SAIPane)); //stick the gridbag into the box
		
		//by here, this.gbPane = new 
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipadx = 5;
		c.ipady = 5;
		c.insets = new Insets(5, 5, 5, 5);
		
		JLabel selectionLabel = new JLabel ("Selection: ");
		c.gridx = 0;
		c.gridy = 0;
		SAIPane.add(selectionLabel, c);
		
		selectionField = new JTextField();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(selectionField); }
		selectionField.setName("selectionField");
		((JTextField)selectionField).setColumns(30);
		((JTextField)selectionField).setText(edgeData.getSelection().get(index).toString());
		c.gridx = 1;
		SAIPane.add(selectionField, c);
		
		JLabel actionLabel = new JLabel ("Action: ");
		c.gridx = 0;
		c.gridy = 1;
		SAIPane.add(actionLabel, c);
		
		actionField = new JTextField();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(actionField); }
		actionField.setName("actionField");
		actionField.setColumns(30);
		actionField.setText(edgeData.getAction().get(index).toString());
		c.gridx = 1;
		SAIPane.add(actionField, c);
		
		this.postComponent = postComponent;
		if(postComponent != null)
			add(WindowUtils.wrapLeft(postComponent));
	}
	
	/**
	 * Default to 0th index
	 * @param edgeData
	 * @param allow
	 * @param max_students
	 * @param instructions
	 * @param postComponent
	 */
	public MatcherPanel(EdgeData edgeData, boolean allow, int max_students,
			String instructions, JComponent postComponent)
	{
		this(edgeData, allow, max_students, instructions, postComponent, 0);
	}
	
	//default constructor
	public MatcherPanel(int arg0)
	{
		super(arg0);
	}

	/**
	 * @return
	 */
	public abstract Matcher createMatcher() throws Exception;

    public void showSyntaxCheckMessage(String msg, boolean correct) {
        // nop
    }
    
    public abstract String getMatcherType();
}
