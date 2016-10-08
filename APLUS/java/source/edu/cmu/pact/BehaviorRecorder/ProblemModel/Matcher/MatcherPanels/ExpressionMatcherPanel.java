/*
 * Created on Apr 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.text.JTextComponent;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExpressionMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.WindowUtils;
import edu.cmu.pact.Utilities.trace;

public class ExpressionMatcherPanel extends MatcherPanel implements ActionListener {
	JTextField inputField;
	JTextArea inputArea; //the new replacement for inputField
    private JTextComponent syntaxCheckText;
    private EdgeData edgeData;
    JList relationOptions;
    private JButton checkButton, lastButton;
    private ExpressionMatcher lastMatcher;

    private static final DateFormat fmt = new SimpleDateFormat("h:mm:ss a");
    
    private JTextField field(String name) {
        JTextField field = new JTextField();
        { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(field); }

        field.setName(name);
        field.setColumns(30);

        return field;
    }
    
    private void addLabelFieldBox(String labelStr, JComponent[] components) {
        JLabel label = new JLabel(labelStr);
        Box box = new Box(BoxLayout.X_AXIS);

        box.add(label);
        for (JComponent component : components)
            box.add(component);
        add(WindowUtils.wrapLeft(box));
        add(Box.createVerticalStrut(5));
    }
    
    private void addLabelFieldBox(String labelStr, JTextField field) {
        addLabelFieldBox(labelStr, new JComponent[] { field });
    }

    public ExpressionMatcherPanel(EdgeData edgeData, boolean allow, int max_students) {
    	this(edgeData, allow, max_students, 0);
    }
    
    public ExpressionMatcherPanel(EdgeData edgeData, boolean allow, int max_students, int selectionIndex) {
    	super(edgeData, allow, max_students, "<html>Enter a formula below.<br>", null);
    	
    	relationOptions = new JList(ExpressionMatcher.RELATIONS);
        relationOptions.setSelectedIndex(0);
        relationOptions.setVisibleRowCount(1);
        inputField = field("inputField");
        syntaxCheckText = new JTextArea();//new JLabel();
        { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(syntaxCheckText); }

        if(edgeData.getMatcher() != null && edgeData.getMatcher() instanceof ExpressionMatcher) {
            ExpressionMatcher m = (ExpressionMatcher)edgeData.getMatcher();
            actionField.setText(m.getAction());
            selectionField.setText(m.getSelection());
            inputField.setText(m.getInputExpression());
            relationOptions.setSelectedValue(m.getRelation(), true);
            lastMatcher = m;
            showLast();
         } else {
            actionField.setText(edgeData.getAction().get(0).toString());
            selectionField.setText(edgeData.getSelection().get(selectionIndex).toString());
            inputField.setText(edgeData.getInput().get(0).toString());
        }
        
    	Box actorBox = new Box(BoxLayout.X_AXIS);

        checkButton = new JButton("Check");
        checkButton.setHorizontalAlignment(SwingConstants.CENTER);
        checkButton.addActionListener(this);
        lastButton = new JButton("Last Evaluation");
        lastButton.setHorizontalAlignment(SwingConstants.CENTER);
        lastButton.addActionListener(this);

        javax.swing.JPanel btnBox = new javax.swing.JPanel(new java.awt.GridLayout(2, 1));
        btnBox.add(checkButton);
        btnBox.add(lastButton);
        
        addLabelFieldBox("Input: ", new JComponent[] { relationOptions, inputField, btnBox });
        
    	if (allow) 
            add(WindowUtils.wrapLeft(actorBox));
    }
    
    
    /**
     * This is a scaled down version of the original Expression Matcher Panel
     * It retains only the relations, the input field, and the side buttons
     * @param text - The input text
     */
    public ExpressionMatcherPanel(EdgeData edgeData, String text) {
    	super(BoxLayout.Y_AXIS);
    	
        //Parse the incoming text in a semi-intelligent way
        int relation = 0; //ExpressionMatcher.EQ_RELATION
        String expression;
        int beginQuote, endQuote;
 //       if((beginQuote = text.indexOf("\"")) >= 0 && (endQuote = text.indexOf("\"", beginQuote + 1)) >= 0)
        if((beginQuote = text.indexOf("\"")) >= 0 &&  (endQuote = text.lastIndexOf("\"")) >= 0)
        {
        	expression = text.substring(beginQuote + 1, endQuote);
        	String testRel = text.substring(0, text.indexOf(" "));
        	for(int i = 0; i < ExpressionMatcher.RELATIONS.length; i ++)
        		if(testRel.equals(ExpressionMatcher.RELATIONS[i]))
        		{
        			relation = i;
        			break;
        		}
        }
        else
        	expression = text;
    	
    	this.edgeData = edgeData;
/*
		JLabel minimumLabel = new JLabel ("Min:");
		SAIPane.add(minimumLabel, c);
		
		minimumField = new JTextField();
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(minimumField); }
		minimumField.setName("minimumField");
		minimumField.setColumns(17);
		minimumField.setText(minText);
		minimumField.setSelectionStart(0);
		minimumField.setSelectionEnd(0);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 1;
		SAIPane.add(minimumField, c);
     	
 */
		SAIPane = new JPanel(new GridBagLayout());
		c = new GridBagConstraints();
		c.ipadx = 0;
		c.ipady = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
    	
		c.insets = new Insets(0, 0, 0, 2);
		c.fill = GridBagConstraints.NONE;
		c.weightx = c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
    	relationOptions = new JList(ExpressionMatcher.RELATIONS);
        relationOptions.setSelectedIndex(relation);
        relationOptions.setVisibleRowCount(1);
        relationOptions.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
        SAIPane.add(relationOptions, c);

		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = c.weighty = 1;
		c.gridwidth = c.gridheight = GridBagConstraints.REMAINDER;
		c.gridx = 1;
        inputArea = new JTextArea(expression);
        { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(inputArea); }
        inputArea.setEditable(true);
        inputArea.setLineWrap(false);
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        inputArea.setTransferHandler(new FunctionTransferHandler(inputArea.getTransferHandler()));

        JScrollPane scrollInputArea = new JScrollPane(inputArea,
        		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        if(trace.getDebugCode("editstudentinput"))
        	trace.out("editstudentinput", "EMP.inputArea border "+trace.nh(inputArea.getBorder()));
        SAIPane.add(scrollInputArea, c);
        
        add(SAIPane);
        
        checkButton = new JButton("Check");
        checkButton.setHorizontalAlignment(SwingConstants.CENTER);
        checkButton.addActionListener(this);
        lastButton = new JButton("Last Evaluation");
        lastButton.setHorizontalAlignment(SwingConstants.CENTER);
        lastButton.addActionListener(this);

//      JPanel btnBox = new JPanel(new GridLayout(2, 1));
//      btnBox.add(checkButton);
//      btnBox.add(lastButton);
        
//      add(relationOptions);
//      add(Box.createRigidArea(new Dimension(2,0)));
//      add(inputArea);
        //add(btnBox);
    }
    
    /**
     * Set one from external
     * @param area
     */
    public void setSyntaxCheck(JTextComponent area)
    {
    	this.syntaxCheckText = area;
    }
    
    /* (non-Javadoc)
     * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels.MatcherPanel#getMatcher()
     */
    public Matcher createMatcher() {
        ExpressionMatcher exm  = new ExpressionMatcher();
        
        if(actionField == null || selectionField == null)
        {
        	exm.setDefaultAction(null);
	        exm.setDefaultSelection(null);    
        }
        else
        {
	        exm.setDefaultAction(actionField.getText());
	        exm.setDefaultSelection(selectionField.getText());
        }
        
        exm.setInputExpression(inputField == null ? inputArea.getText() : inputField.getText());
        exm.setRelation((String)relationOptions.getSelectedValue());
        exm.setDefaultActor("Student");
        exm.setLinkTriggered(false);
	    
        Matcher m;
        if(edgeData != null && (m = edgeData.getMatcher()) != null) {
            exm.setDefaultInput(m.getDefaultInput());
            exm.setDefaultAction(m.getDefaultAction());
            exm.setDefaultSelection(m.getDefaultSelection());
            exm.setDefaultActor(m.getDefaultActor());
            exm.setLinkTriggered(m.isLinkTriggered());
        }
	
        return exm;
    }

    private String beautifyErrorMessage(String msg) {
        String[] msgLines = msg.split("\n");
        StringBuffer buf = new StringBuffer();
        boolean foundERROR = false;

        for (String msgLine : msgLines) {
            String displayLine = msgLine.replaceAll(" ", "&nbsp;");
            displayLine = displayLine.replaceAll("<", "&lt;");
            displayLine = displayLine.replaceAll(">", "&gt;");

            if (displayLine.startsWith("ERROR:")) {
                if (foundERROR)
                    break;
                foundERROR = true;
                buf.append("ERROR:");
            } else
                buf.append(displayLine);
            buf.append("<br>");
        }

        return buf.toString();
    }
    
    private void showSyntaxCheckMessage(String msg, String colorName) {
        //fancy html doesn't work since we're using a textarea now
    	//String displayMsg = beautifyErrorMessage(msg);
        //displayMsg = "<html><font color='" + colorName + "' face='courier'>" +  displayMsg + "</font></html>";
        syntaxCheckText.setText(msg);
        syntaxCheckText.setSelectionStart(0);
        syntaxCheckText.setSelectionEnd(0);
        syntaxCheckText.repaint();
    }
    
    public void showSyntaxCheckMessage(String msg, boolean correct) {
        showSyntaxCheckMessage(msg, correct ? "green" : "red");
    }
    
    /**
     * @param single
     * @param concat
     * @param vector
     * @param selection
     * @param action
     * @param input
     * @return
     */
    public boolean checkDemonstratedValues(boolean single, boolean concat,
    		int vector, Vector selection, Vector action, Vector input)
    {
    	ExpressionMatcher m = null;
    	Matcher tmp = null;
    	if(single)
    	{
    		m = new ExpressionMatcher(concat, vector, "");
    		m.setInputExpression(inputField == null ? inputArea.getText() : inputField.getText());
            m.setRelation((String)relationOptions.getSelectedValue());
    	}
    	else
    		m = (ExpressionMatcher)createMatcher();
        
        if(edgeData != null)
        {
        	tmp = edgeData.getMatcher();
        	edgeData.setMatcher(m); //why do we add it to the edgeData in the first place, if we're just going to set it back ..
        }
        
        boolean check = m.checkExpression();
        boolean demoValues = true;
        showSyntaxCheckMessage(check ? "OK" : m.error(), check);
        if (check)
        {
            relationOptions.setSelectedValue(((ExpressionMatcher)m).getRelation(), true);
            
            if(selection != null && action != null && input != null)
        	{
            	if(concat)
            	{
	        		demoValues = ((ExpressionMatcher)m).matchConcatenation(selection, action, input);
	        		showSyntaxCheckMessage(syntaxCheckText.getText() + "\r\n" + (demoValues ? "Demonstrated values matched" : "Demonstrated values did not match"), demoValues);
            	}
        	}
        }
        
        validate();
        if(edgeData != null) //or i guess edgeData ...
        	edgeData.setMatcher(tmp);

        return check && demoValues;
    }
    
    /**
     * Checks whether the expression is valid, prints result to the syntaxCheckText
     * area
     * @return - true if the check passed, false otherwise
     */
    public boolean check() {
        return checkDemonstratedValues(false, false, -1, null, null, null);
    }

    private void showLast() {
        String msg = "This expression has not been evaluated.", color = "blue";
        ExpressionMatcher m = lastMatcher;
        
        if (m!=null && m.lastResult()!=null) {
            String evalTime = lastMatcher.lastEvaluationTime()==null ? "" : fmt.format(lastMatcher.lastEvaluationTime());

            msg = "Last evaluation (" + evalTime + "):";
            msg += "\n\nObserved input (student): " + m.lastInput();
            if (trace.getDebugCode("functions")) trace.outln("functions", "relation is " + m.getRelation());
            String expected = m.lastResult().toString();
            if (!(m.isEqualRelation() || m.isBooleanRelation()))
                expected = m.getRelation() + " " + expected;
            msg += "\nExpected input (formula): " + expected;
            msg += "\n\n" + (m.lastComparison() ? "Match!" : "No match!");
        } else if (m!=null && m.lastError()!=null) {
            msg = m.lastError();
            color = "red";
        }
            
        showSyntaxCheckMessage(msg, color);
    }
    
    public void setLastMatcher(ExpressionMatcher m) {
    	lastMatcher = m;
    }
    
	public void actionPerformed(ActionEvent evt) {
		Object src = evt.getSource();
		if(src instanceof JButton && ((JButton)src).getText().equals("Last Evaluation"))
			showLast();
        else if(src == checkButton)
            check();
        else if(src == lastButton)
            showLast();
    }
	
	public String getMatcherType()
	{
		return Matcher.EXPRESSION_MATCHER;
	}
}
