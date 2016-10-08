/*
 * Created on Apr 7, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cl.tutors.solver.rule.SolverGoal;
import cl.utilities.TestableTutor.InitializationException;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.SolverMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.VectorMatcher;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;


public class SolverMatcherPanel extends MatcherPanel {

	private static final String GOAL_PREFIX = "Goal: ";

	/** Choices for solver goals. See {@link SolverGoal}. */
	private static final Vector<String> goalNameChoices = new Vector<String>();
	static {
		goalNameChoices.add(GOAL_PREFIX+"default");    // use entry 0 to preserve default
		for (String goalName : SolverMatcher.getAllGoalNames())
			goalNameChoices.add(GOAL_PREFIX+goalName);
	}
	
	private static String goalIndex2GoalName(int i) {
		if (i == 0)
			return null;
		String goalName = goalNameChoices.get(i);
		return goalName.substring(GOAL_PREFIX.length());
	}
	
	private static int goalName2GoalIndex(String goalName) {
		int i = 0;   // default
		if (goalName != null && goalName.length() > 0)
			i = goalNameChoices.indexOf(GOAL_PREFIX+goalName);
		return i;
	}

	/** Choices for {@link #autoSimplifyTypeinList}. */
	private static String[] autoSimplifyTypeinChoices = {
		"Simplify subexpressions automatically",
		"Require simplification, but no type-in",
		"Require student to type in each result"		
	};

	/**
	 * Translate a choice to booleans for auto-simplify and typein modes.
	 * @param i index into {@link #autoSimplifyTypeinChoices}
	 * @return array with element[0] = auto-simplify and element[1] = typein mode
	 */
	private static boolean[] choice2BooleanModes(int i) {
		boolean autoSimplify = false;
		boolean typeinMode = false;
		switch (i) {
		case 0:  autoSimplify = true;  typeinMode = false; break;
		case 1:  autoSimplify = false; typeinMode = false; break;
		case 2:  autoSimplify = false; typeinMode = true;  break;
		default: autoSimplify = false; typeinMode = false; break;
		}
		return new boolean[] { autoSimplify, typeinMode };
	}

	/**
	 * Translate booleans for auto-simplify and typein modes to a choice.
	 * @param autoSimplify
	 * @param typeinMode
	 * @return index into {@link #autoSimplifyTypeinChoices}
	 */
	private static int booleanModes2Choice(boolean autoSimplify, boolean typeinMode) {
		if (autoSimplify)
			return 0;
		if (typeinMode)
			return 2;
		return 1;
	}
	
	/** UI for auto-simplify and typein modes. */
	private JComboBox autoSimplifyTypeinList;
	
	/** To preserve goalName. */
	private JComboBox goalNameList;

	/** FIXME space for initializer */
	private JTextField inputInitializer;
	
	/** Underlying Matcher instance which this Panel might replace. */
	private VectorMatcher vm = null;
	
	/**
	 * Create a panel for setting up a @link SolverMatcher}
	 * @param vm Matcher to replace
	 * @param autoSimplify for SolverMatcher#getau
	 * @param typeinMode
	 */
	public SolverMatcherPanel(VectorMatcher vm, Color bgColor)
	{
		super(BoxLayout.Y_AXIS);
		if (bgColor == null)
			bgColor = Color.WHITE;
		
		this.vm = vm;
		
		boolean autoSimplify = false;
		boolean typeinMode = false;
		String goalName = null;
		String initialInput = null;
		if (vm instanceof SolverMatcher) {
			autoSimplify = ((SolverMatcher) vm).getAutoSimplify();
			typeinMode = ((SolverMatcher) vm).getTypeinMode();
			goalName = ((SolverMatcher) vm).getGoalName();
			initialInput = ((SolverMatcher) vm).getProblemSpec();
		}
		
		SAIPane = new JPanel(new GridBagLayout());
		c = new GridBagConstraints();
		int width = 0;   // for sizing
		
		c.fill = GridBagConstraints.NONE; // CTAT2945: HORIZONTAL doesn't work well on Mac
		c.anchor = GridBagConstraints.CENTER;   // CTAT2945: WEST doesn't work well on Mac
		c.ipadx = 0;
		c.ipady = 0;
		c.insets = new Insets(5, 10, 5, 10);

		// shift the real display right
		c.gridx = 0;
//		for (c.gridy=1; c.gridy < 5; c.gridy++) {
//			JLabel blank = new JLabel(" ");
//			SAIPane.add(blank, c);
//		}
//		c.gridx = 1;
		c.gridy = 1;

		autoSimplifyTypeinList = new JComboBox(autoSimplifyTypeinChoices);
		autoSimplifyTypeinList.setName("autoSimplifyTypeinList");
		autoSimplifyTypeinList.setEditable(false);
		autoSimplifyTypeinList.setSelectedIndex(booleanModes2Choice(autoSimplify, typeinMode));
		width = autoSimplifyTypeinList.getPreferredSize().width;
		SAIPane.add(autoSimplifyTypeinList, c);
//		add(autoSimplifyTypeinList);

		c.gridy = 2;
		goalNameList = new JComboBox(goalNameChoices);
		goalNameList.setName("goalNameList");
		goalNameList.setEditable(false);
		goalNameList.setVisible(false);   // FIXME : learn to use Solver goals; also SolverMatcher
		goalNameList.setSelectedIndex(goalName2GoalIndex(goalName));
		if (width < goalNameList.getPreferredSize().width)
			width = goalNameList.getPreferredSize().width;
//		SAIPane.add(goalNameList, c);
//		add(goalNameList);

		c.gridy = 3;
		JLabel inputInitializerLabel = new JLabel("Initial expression or equation:");
		if (width < inputInitializerLabel.getPreferredSize().width)
			width = inputInitializerLabel.getPreferredSize().width;
		SAIPane.add(inputInitializerLabel, c);
//		add(inputInitializerLabel);
		
		c.gridy = 4;
		c.insets = new Insets(0, 10, 5, 10);   // top==0: closer to label above
		inputInitializer = new JTextField(20);
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(inputInitializer); }
		inputInitializer.setName("inputInitializer");
		inputInitializer.setText(initialInput);
		if (width < inputInitializer.getPreferredSize().width)
			width = inputInitializer.getPreferredSize().width;
		inputInitializer.setMinimumSize(new Dimension(width, inputInitializer.getPreferredSize().height));
		SAIPane.add(inputInitializer, c);
//		add(inputInitializer);
		
//		autoSimplifyTypeinList.setPreferredSize(new Dimension(width, autoSimplifyTypeinList.getPreferredSize().height));
//		goalNameList.setPreferredSize(new Dimension(width, goalNameList.getPreferredSize().height));
//		inputInitializerLabel.setPreferredSize(new Dimension(width, inputInitializerLabel.getPreferredSize().height));
//		inputInitializer.setPreferredSize(new Dimension(width, inputInitializer.getPreferredSize().height));
		
		add(SAIPane);
	}

	/**
	 * @return new {@link SolverMatcher} instance
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels.MatcherPanel#createMatcher()
	 */
	public Matcher createMatcher() throws IllegalArgumentException, InitializationException {
		boolean[] autoSimplifyTypeinModes = choice2BooleanModes(autoSimplifyTypeinList.getSelectedIndex());
		boolean autoSimplify = autoSimplifyTypeinModes[0];
		boolean typeinMode = autoSimplifyTypeinModes[1];
		String goalName = goalIndex2GoalName(goalNameList.getSelectedIndex());
		
		SolverMatcher sm  = new SolverMatcher(vm.isConcat(), vm.getMatchers(Matcher.SELECTION),
				vm.getMatchers(Matcher.ACTION), vm.getMatchers(Matcher.INPUT), vm.getActor(),
				Boolean.toString(autoSimplify), Boolean.toString(typeinMode), goalName);

		Matcher m = edgeData.getMatcher();
        if (m != null) {
            sm.setDefaultInput(m.getDefaultInput());
            sm.setDefaultAction(m.getDefaultAction());
            sm.setDefaultSelection(m.getDefaultSelection());
            sm.setDefaultActor(m.getDefaultActor());
        }

		return sm;
	}

	/**
	 * @return {@value Matcher#SOLVER_MATCHER}
	 * @see edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels.MatcherPanel#getMatcherType()
	 */
	public String getMatcherType() {
		return Matcher.SOLVER_MATCHER;
	}

	/**
	 * @return content of {@link #inputInitializer}
	 */
	public String getText() {
		return inputInitializer.getText();
	}

	/**
	 * @return auto-simplify mode calculated from {@link #autoSimplifyTypeinChoices}
	 */
	public Boolean getAutoSimplify() {
		boolean[] autoSimplifyTypeinModes = choice2BooleanModes(autoSimplifyTypeinList.getSelectedIndex());
		boolean autoSimplify = autoSimplifyTypeinModes[0];
		return new Boolean(autoSimplify);
	}

	/**
	 * @return typein mode calculated from {@link #autoSimplifyTypeinChoices}
	 */
	public Boolean getTypeinMode() {
		boolean[] autoSimplifyTypeinModes = choice2BooleanModes(autoSimplifyTypeinList.getSelectedIndex());
		boolean typeinMode = autoSimplifyTypeinModes[1];
		return new Boolean(typeinMode);
	}

	/**
	 * @return state of {@link #goalNameList}; returns null if on default goal
	 */
	public String getGoalName() {
		int i = goalNameList.getSelectedIndex();
		return goalIndex2GoalName(i);
	}
}
