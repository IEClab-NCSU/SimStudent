package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherPanels;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;

import pact.CommWidgets.JCommWidget;
import pact.CommWidgets.RemoteToolProxy;
import pact.CommWidgets.StudentInterfaceConnectionStatus;
import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeEvent;
import edu.cmu.pact.BehaviorRecorder.Dialogs.EditStudentInputDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.PackageEnumerator;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeUpdatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.AnyMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExactMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExpressionMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.MatcherFactory;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.RangeMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.RegexMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.SolverMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.VectorMatcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.WildcardMatcher;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.WindowUtils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.model.ActionListModel;
import edu.cmu.pact.ctat.model.CtatListModel;

/**
 * This class allows for independent element matching on each of the elements of the 
 * selection, action, and input vectors.
 * 
 * It also allows for independent concatenation matching on the concatenation of the 
 * selection vector, action vector, and input vectors.
 * 
 * It provides three views to the author a concat view (concatenation), simple view, and
 * complex view where the last two are for independent element matching
 * 
 * This class also contains two simple dialogs for getting input to the Range matcher and
 * Expression/Functions matcher
 * 
 * Note that for objects which belong to selection, action, or input, we only allow array version
 * when they are mutable, singletons
 * 
 * Written by Ko
 */
public class VectorMatcherPanel extends MatcherPanel implements ActionListener {

	private static final long serialVersionUID = 201403112100L;
	
	private static final String DONT_REPLACE = "(don't replace)";
	/** Formatting stuff */
	private static final int TEXT_AREA_LENGTH = 250, TEXT_AREA_NUM_LINES = 12, LINE_HEIGHT = 15; //not sure why, but adding to a JPanel reduces by a single line
	private static final Dimension TEXT_AREA_DIM = new Dimension(TEXT_AREA_LENGTH, TEXT_AREA_NUM_LINES * LINE_HEIGHT);
	private static final Dimension DEMO_SEL_ACT_DIM = new Dimension(TEXT_AREA_DIM.width*6/10, TEXT_AREA_DIM.height/2);	
	/* Default matchers for selection, action, and input */
	private static final String[] DEFAULT_SELECTION_MATCHERS = {Matcher.EXACT_MATCHER, Matcher.WILDCARD_MATCHER, Matcher.REGULAR_EXPRESSION_MATCHER, Matcher.RANGE_MATCHER, Matcher.ANY_MATCHER, Matcher.EXPRESSION_MATCHER};
	private static final String[] DEFAULT_ACTION_MATCHERS = {Matcher.EXACT_MATCHER, Matcher.WILDCARD_MATCHER, Matcher.REGULAR_EXPRESSION_MATCHER, Matcher.RANGE_MATCHER, Matcher.ANY_MATCHER, Matcher.EXPRESSION_MATCHER};
	private static final String[] DEFAULT_INPUT_MATCHERS =
		(VersionInformation.includesCL()
			? new String[] {Matcher.EXACT_MATCHER, Matcher.WILDCARD_MATCHER, Matcher.REGULAR_EXPRESSION_MATCHER, Matcher.RANGE_MATCHER, Matcher.ANY_MATCHER, Matcher.EXPRESSION_MATCHER, Matcher.SOLVER_MATCHER}
			: new String[] {Matcher.EXACT_MATCHER, Matcher.WILDCARD_MATCHER, Matcher.REGULAR_EXPRESSION_MATCHER, Matcher.RANGE_MATCHER, Matcher.ANY_MATCHER, Matcher.EXPRESSION_MATCHER}
		);
	private static final String[][] DEFAULT_MATCHERS = {DEFAULT_SELECTION_MATCHERS, DEFAULT_ACTION_MATCHERS, DEFAULT_INPUT_MATCHERS};
	
	/* If a matcher is not selected, we use this matcher type */
	private static final String DEFAULT_MATCHER = Matcher.ANY_MATCHER;
	
	/* General indexing for selection, action, and input */
	private static final int SELECTION = VectorMatcher.SELECTION, ACTION = VectorMatcher.ACTION, INPUT = VectorMatcher.INPUT;
	
	/* Input corresponds to the sai vectors, output refers to the matchers as indexed in the above array
	 * Note that for Concat View, we only use the key 0 to this map (as if we were accessing a single element) */
	final Map<Integer, String> selectionMatchersTypes = new HashMap<Integer, String>(),
						actionMatchersTypes = new HashMap<Integer, String>(),
						inputMatchersTypes = new HashMap<Integer, String>();
	Map[] matchersTypes = {selectionMatchersTypes, actionMatchersTypes, inputMatchersTypes};
	
	/* Input corresponds to the sai vectors, output refers to the actual matchers
	 * This is necessary since we need to store the ExpressionMatcher created from the Expression dialog
	 * See getMatcher and the Expression dialog's actionPerformed for more notes
	 */
	final Map<Integer, Matcher> selectionMatchers = new HashMap<Integer, Matcher>(),
						actionMatchers = new HashMap<Integer, Matcher>(),
						inputMatchers = new HashMap<Integer, Matcher>();
	Map[] matchers = {selectionMatchers, actionMatchers, inputMatchers};
	
	/* Actual values for selections, actions, and inputs */
	final Vector<String> selectionValues = new Vector<String>(),
				actionValues = new Vector<String>(),
				inputValues = new Vector<String>();
	Vector[] values = {selectionValues, actionValues, inputValues};
	
	/* General JButtons */
	JButton okButton;
	JButton cancelButton;
	
	/* Parent JDialog, we need this for the Expression dialog */
	EditStudentInputDialog parent;
	
	/* Actor selection buttons */
	private ButtonGroup tvs;
	private JRadioButton anyButton;
	private JRadioButton ungradedButton;
	private Box actorBox;
	private JLabel actorLabel;
	private JLabel triggerLabel;
	private JRadioButton stateTriggerBtn;
	private JRadioButton linkTriggerBtn;
	private ButtonGroup triggerBtns;
	private Box triggerBox;
	
	/** UI for Replace Input feature: check box to enable {@link #replaceInputFormulaField}. */
	private JCheckBox replaceInputCheckBox;
	/** UI for Replace Input feature: choose or enter a Matcher's replacement formula. */
	private JComboBox replaceInputFormulaField;
	/** Model data for {@link #replaceInputFormulaField}. */
	private static final String[] replaceInputFormulas;
	static {
		if (VersionInformation.includesCL())
			replaceInputFormulas = new String[] { DONT_REPLACE, "simplify(input)" };
		else
			replaceInputFormulas = new String[] { DONT_REPLACE, "algEval(input)" };
	}
	
	/* The views */
	private JPanel view;
	private MatcherView curView; //points to one of the following
	private SimpleView simpleView;
	private ComplexView complexView;
	private ConcatView concatView;
	
	private final Vector defaultSelectionVector, defaultActionVector, defaultInputVector;
	private Vector[] defaults;
	private Matcher existingMatcher;
	private EdgeData previousEdgeData;
	
	public static final String CONCAT_VIEW = "Concat View", SIMPLE_VIEW = "Simple View", COMPLEX_VIEW = "Complex View";

	/** Contents of {@link #triggerList}. */
	private static final String TRIGGER = "Trigger:";
	private static final String STATE_TRIGGER = " On current state ";
	private static final String LINK_TRIGGER = " On previous link ";
	
	/** Background for text areas where user can't type. */
	private static final Color UneditableBackgroundColor = new Color(0xF4, 0xF4, 0xF4);  // very light gray
	
	/** Show or hide the trigger UI according to whether the actor is not "student." */
	private class ActorListener implements ActionListener {
		public void actionPerformed(ActionEvent ae) {
			String cmd = ae.getActionCommand();
			boolean enable = !(studentButton.getActionCommand().equals(cmd));
			triggerLabel.setEnabled(enable);
			stateTriggerBtn.setEnabled(enable);
			linkTriggerBtn.setEnabled(enable);
		}		
	}
     
	/**
	 * @param parent - the parent JDialog
	 * @param edgeData - edgeData associated with this matcher
	 * @param allowToolReportedActions - unused
	 * @param max_students - unused
	 */
	public VectorMatcherPanel(EditStudentInputDialog parent, EdgeData edgeData, boolean allowToolReportedActions, int max_students )
	{
		super(BoxLayout.Y_AXIS);
		this.previousEdgeData = edgeData.cloneEdgeData();
		this.parent = parent;
		this.edgeData = edgeData;
		
		//Setup the defaults
		defaultSelectionVector = edgeData.getSelection();
        defaultActionVector = edgeData.getAction();
        defaultInputVector = edgeData.getInput();
        defaults = new Vector[3];
        defaults[SELECTION] = defaultSelectionVector;
        defaults[ACTION] = defaultActionVector;
        defaults[INPUT] = defaultInputVector;

		triggerLabel = new JLabel(TRIGGER);
		stateTriggerBtn = new JRadioButton(STATE_TRIGGER);
		stateTriggerBtn.setName("stateTriggerBtn");
		linkTriggerBtn = new JRadioButton(LINK_TRIGGER);
		linkTriggerBtn.setName("linkTriggerBtn");
		triggerBtns = new ButtonGroup();
		triggerBtns.add(stateTriggerBtn);
		triggerBtns.add(linkTriggerBtn);
		stateTriggerBtn.setSelected(true);
		triggerBox = new Box(BoxLayout.X_AXIS);
		triggerBox.add(stateTriggerBtn);
		triggerBox.add(linkTriggerBtn);
		
		replaceInputCheckBox = new JCheckBox("When correct, replace student input with:");
		replaceInputCheckBox.setName("replaceInputCheckBox");
		replaceInputCheckBox.setBackground(getBackground());
		replaceInputFormulaField = new JComboBox();
		replaceInputFormulaField.setName("replaceInputFormulaField");
		
        //put the choices for actors in
		actorLabel = new JLabel ("Actor:");
		toolButton = new JRadioButton(Matcher.DEFAULT_TOOL_ACTOR);
		ungradedButton = new JRadioButton(Matcher.UNGRADED_TOOL_ACTOR);
		studentButton = new JRadioButton(Matcher.DEFAULT_STUDENT_ACTOR);
		anyButton = new JRadioButton(Matcher.ANY_ACTOR);
		toolButton.setActionCommand(toolButton.getText());
		ungradedButton.setActionCommand(ungradedButton.getText());
		studentButton.setActionCommand(studentButton.getText());
		anyButton.setActionCommand(anyButton.getText());
		toolButton.setToolTipText(Matcher.DEFAULT_TOOL_ACTOR_TOOLTIP);
		ungradedButton.setToolTipText(Matcher.UNGRADED_TOOL_ACTOR_TOOLTIP);
		studentButton.setToolTipText(Matcher.DEFAULT_STUDENT_ACTOR_TOOLTIP);
		anyButton.setToolTipText(Matcher.ANY_ACTOR_TOOLTIP);
		tvs = new ButtonGroup();
		tvs.add(toolButton);
		tvs.add(ungradedButton);
		tvs.add(studentButton);
		tvs.add(anyButton);
		studentButton.setSelected(true);
		ActorListener al = new ActorListener();
		toolButton.addActionListener(al);
		ungradedButton.addActionListener(al);
		studentButton.addActionListener(al);
		anyButton.addActionListener(al);
		
		actorBox = new Box(BoxLayout.X_AXIS);
		actorBox.add(toolButton);
		actorBox.add(ungradedButton);
		actorBox.add(studentButton);
		actorBox.add(anyButton);
		//we add the actorBox in the views, so it aligns with the sai inputs
		
		existingMatcher = edgeData.getMatcher();
		if(existingMatcher == null) //create a default if none existed before
		{
			edgeData.setMatcher((existingMatcher = new VectorMatcher()));
			for(int vector = 0; vector < 3; vector ++)
				this.matchersTypes[vector].put(0, Matcher.EXACT_MATCHER);
		}
		
		if(existingMatcher instanceof VectorMatcher)
		{
            VectorMatcher vm = (VectorMatcher)edgeData.getMatcher();
            for(int vector = 0; vector < 3; vector ++)
            	for(int j = 0; j < vm.getMatchers(vector).size(); j ++)
            	{
            		Matcher m = vm.getMatchers(vector).get(j);
            		matchers[vector].put(j, m);
            		matchersTypes[vector].put(j, m.getMatcherType());
            	}
            
            if(vm.isConcat())
            {
	            for(int vector = 0; vector < 3; vector ++)
				{
	            	Matcher m = (Matcher)matchers[vector].get(0);
	            	if(m == null)
	            		continue;
	            	values[vector] = new Vector((List<String>)Arrays.asList(m.getValuesVector()));
				}
            }
            else
            {
            	for(int vector = 0; vector < 3; vector ++)
					for(int j = 0; matchers[vector].get(j) != null; j ++)
					{
						Matcher m = (Matcher)matchers[vector].get(j);
		            	if(m == null)
		            		continue;
						values[vector].add(m.toString());
					}
            }
        }
		else
		{
			//set up default values
			String[] oldMatchersTypes = new String[3];
			if(existingMatcher instanceof ExpressionMatcher)
			{
				oldMatchersTypes[SELECTION] = Matcher.EXACT_MATCHER;
				oldMatchersTypes[ACTION] = Matcher.EXACT_MATCHER;
				oldMatchersTypes[INPUT] = Matcher.EXPRESSION_MATCHER;
				
				values[SELECTION].add(existingMatcher.getSelection());
				values[ACTION].add(existingMatcher.getAction());
				values[INPUT].add(((ExpressionMatcher)existingMatcher).toString());
			}
			else if(existingMatcher instanceof ExactMatcher)
			{
				oldMatchersTypes[SELECTION] = Matcher.EXACT_MATCHER;
				oldMatchersTypes[ACTION] = Matcher.EXACT_MATCHER;
				oldMatchersTypes[INPUT] = Matcher.EXACT_MATCHER;
				
				values[SELECTION].add(existingMatcher.getSelection());
				values[ACTION].add(existingMatcher.getAction());
				values[INPUT].add(existingMatcher.getInput());
			}
			else if(existingMatcher instanceof RegexMatcher)
			{
				oldMatchersTypes[SELECTION] = Matcher.REGULAR_EXPRESSION_MATCHER;
				oldMatchersTypes[ACTION] = Matcher.REGULAR_EXPRESSION_MATCHER;
				oldMatchersTypes[INPUT] = Matcher.REGULAR_EXPRESSION_MATCHER;
				
				values[SELECTION].add(((RegexMatcher)existingMatcher).getSelectionPattern());
				values[ACTION].add(((RegexMatcher)existingMatcher).getActionPattern());
				values[INPUT].add(((RegexMatcher)existingMatcher).getInputPattern());
			}
			else if(existingMatcher instanceof WildcardMatcher)
			{
				oldMatchersTypes[SELECTION] = Matcher.WILDCARD_MATCHER;
				oldMatchersTypes[ACTION] = Matcher.WILDCARD_MATCHER;
				oldMatchersTypes[INPUT] = Matcher.WILDCARD_MATCHER;
				
				values[SELECTION].add(((WildcardMatcher)existingMatcher).getSimpleSelectionPattern());
				values[ACTION].add(((WildcardMatcher)existingMatcher).getSimpleActionPattern());
				values[INPUT].add(((WildcardMatcher)existingMatcher).getSimpleInputPattern());
			}
			else if(existingMatcher instanceof AnyMatcher)
			{
				oldMatchersTypes[SELECTION] = Matcher.EXACT_MATCHER;
				oldMatchersTypes[ACTION] = Matcher.EXACT_MATCHER;
				oldMatchersTypes[INPUT] = Matcher.ANY_MATCHER;
				
				values[SELECTION].add(existingMatcher.getSelection());
				values[ACTION].add(existingMatcher.getAction());
			}
			else if(existingMatcher instanceof RangeMatcher)
			{
				oldMatchersTypes[SELECTION] = Matcher.EXACT_MATCHER;
				oldMatchersTypes[ACTION] = Matcher.EXACT_MATCHER;
				oldMatchersTypes[INPUT] = Matcher.RANGE_MATCHER;
				
				values[SELECTION].add(existingMatcher.getSelection());
				values[ACTION].add(existingMatcher.getAction());
				values[INPUT].add(((RangeMatcher)existingMatcher).toString());
			}
			for(int vector = 0; vector < 3; vector ++)
				matchersTypes[vector].put(0, oldMatchersTypes[vector]);
		}
		
        //put old actor back in
        String actor = edgeData.getActor();
		if(actor.equals(Matcher.UNGRADED_TOOL_ACTOR))
			ungradedButton.doClick();
		else if(actor.equals(Matcher.DEFAULT_TOOL_ACTOR))
			toolButton.doClick();
		else if(actor.equals(Matcher.ANY_ACTOR))
			anyButton.doClick();
		else if(actor.equals(Matcher.DEFAULT_STUDENT_ACTOR))
			studentButton.doClick();
		
		if (existingMatcher != null) {
			if (existingMatcher.isLinkTriggered())
				linkTriggerBtn.setSelected(true);
			else
				stateTriggerBtn.setSelected(true);
			
		}
		initReplaceInput(existingMatcher);
		
		concatView = new ConcatView(true);
		curView = concatView;
		view = new JPanel(new BorderLayout());
		view.add(curView, BorderLayout.CENTER);
		
		//put the buttons on
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		Box b3 = new Box(BoxLayout.X_AXIS);
		b3.add(okButton);
		b3.add(Box.createHorizontalStrut(12));
		b3.add(cancelButton);
		//keep the buttons separate so they don't show up aligned to right of top JLabels
		view.add(WindowUtils.wrapRight(b3), BorderLayout.SOUTH);
		
		add(view);
	}
	
	/**
	 * Make the custom focus traversal policy available.
	 */
	public FocusTraversalPolicy getCustomFocusTraversalPolicy() {
		return concatView.getCustomFocusTraversalPolicy();
	}
	
	/**
	 * Initialize the {@link #replaceInputCheckBox} and {@link #replaceInputFormulaField}.
	 * @param m source instance: uses {@link Matcher#replaceInput()} and
	 *          {@link Matcher#getReplacementFormula()} to set UI fields
	 */
	private void initReplaceInput(Matcher m) {
		String existingFormula =
			(m != null && m.replaceInput() ? m.getReplacementFormula() : null);
		List<String> formulas = new ArrayList<String>();
		formulas.addAll(Arrays.asList(replaceInputFormulas));
		
		int selectedItemIndex = 0;
		if (existingFormula != null && existingFormula.length() > 0) {
			selectedItemIndex = formulas.indexOf(existingFormula);
			if (selectedItemIndex < 0) {
				formulas.add(existingFormula);
				selectedItemIndex = formulas.size()-1;
			}
		} else
			existingFormula = null;   // used below

		formulas.add("");             // blank item for author entry
		ComboBoxModel model = new DefaultComboBoxModel(formulas.toArray());
		model.setSelectedItem(formulas.get(selectedItemIndex));

		replaceInputFormulaField.setModel(model);
		replaceInputFormulaField.setEditable(true);
		replaceInputFormulaField.setEnabled(existingFormula != null);
		replaceInputCheckBox.getModel().setSelected(existingFormula != null);
		
		replaceInputCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				boolean checked = replaceInputCheckBox.getModel().isSelected();
				if (trace.getDebugCode("pm")) trace.outNT("pm", "checkBoxActionListener: checkbox.isSelected() "+
						checked+", field "+
						replaceInputFormulaField.getModel().getSelectedItem().toString());
				replaceInputFormulaField.setEnabled(checked);
			}
		});
		replaceInputFormulaField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (trace.getDebugCode("pm")) trace.outNT("pm", "fieldActionListener: checkbox.isSelected() "+
						replaceInputCheckBox.getModel().isSelected()+", field "+
						replaceInputFormulaField.getModel().getSelectedItem().toString());
			}
		});
		{
			if(trace.getDebugCode("editstudentinput"))
				trace.out("editstudentinput", String.format("VMP.initReplaceInput() editor %s, component %s, transferHandler %s",
						replaceInputFormulaField.getEditor(), 
						(replaceInputFormulaField.getEditor() != null ? replaceInputFormulaField.getEditor().getEditorComponent() : null), 
						(replaceInputFormulaField.getEditor().getEditorComponent() instanceof JTextComponent ?
								((JTextComponent) replaceInputFormulaField.getEditor().getEditorComponent()).getTransferHandler() : null)));
			Component edCmp = null;
			ComboBoxEditor ed = replaceInputFormulaField.getEditor();
			if((edCmp = ed.getEditorComponent()) != null && edCmp instanceof JTextComponent) {
				TransferHandler edTxtCmpTH = null;
				JTextComponent edTxtCmp = (JTextComponent) edCmp; 
				if((edTxtCmpTH = edTxtCmp.getTransferHandler()) != null)
					edTxtCmp.setTransferHandler(new FunctionTransferHandler(edTxtCmpTH));
			}
		}
	}

	public String getDemonstratedValue(int vector)
	{
		Vector defaultVector = null;
		switch(vector){
			case SELECTION:
				defaultVector = defaultSelectionVector;
				break;
			case ACTION:
				defaultVector = defaultActionVector;
				break;
			case INPUT:
				defaultVector = defaultInputVector;
				break;
		}
		return Matcher.vector2ConcatString(defaultVector);
	}
	
	/**
	 * Utility method to create a combo box
	 * @param matchers - the string arr of matchers we want to allow
	 * @return a new JComboBox instance
	 */
	public JComboBox createCombo(String[] values, boolean editable, ActionListener listener, String name)
	{
		return createCombo(new Vector(Arrays.asList(values)), editable, listener, name);
	}
	
	/**
	 * Utility method to create a combo box
	 * @param values - the string vector of values to begin with
	 * @return a new JComboBox instance
	 */
	public JComboBox createCombo(Vector values, boolean editable, ActionListener listener, String name)
	{
		JComboBox combo = new JComboBox(values);
		combo.setName(name);
		combo.setFont(new Font(null, Font.PLAIN, 11));
		combo.setEditable(editable);
		if(listener != null)
			combo.addActionListener(listener);
		return combo;
	}
	
	/**
	 * Returns a scroll pane wrapping the JTextArea instance given
	 * @param area - area of text
	 * @return - a scroll pane
	 */
	public JScrollPane scrollerizeTextArea(JTextArea area, Dimension d)
	{
		JScrollPane s = new JScrollPane(area);
		if(d != null) {
			s.setPreferredSize(d);
			s.setMaximumSize(d);
		}
		if(trace.getDebugCode("editstudentinput"))
			trace.printStack("editstudentinput", "VMP.scrollerizeTextArea("+area.getName()+","+d+
					") area.prefScrVwprtSize "+area.getPreferredScrollableViewportSize()+
					", scrollPane prefSize "+s.getPreferredSize());
		return s;
	}

	/** Contains the Tab keystroke, for use as focus traversal from {@link JTextArea}s. */
	private static final Set<AWTKeyStroke> TabKey = new HashSet<AWTKeyStroke>(
			Arrays.asList(new AWTKeyStroke[] { AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, 0)}));

	/** Contains the Shift-Tab keystroke, for use as focus traversal from {@link JTextArea}s. */
	private static final Set<AWTKeyStroke> ShiftTabKey = new HashSet<AWTKeyStroke>(
			Arrays.asList(new AWTKeyStroke[] { AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_DOWN_MASK)}));

	/**
	 * Only creates an empty text area, the setting of text is done in updateView and
	 * the methods it calls
	 * @return - A JTextArea
	 */
	public static JTextArea createTextArea(String name)
	{
		final JTextArea a = new JTextArea() {
			public void setText(String s)
			{
				String s0 = getText();
				super.setText(s);
				setSelectionStart(0);
				setSelectionEnd(0);
				firePropertyChange("text", s0, s);  // not always called by super.setText()
			}
		};
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(a); }
		a.setName(name);
		a.setFont(new Font(null, Font.PLAIN, 11));
		if(trace.getDebugCode("editstudentinput"))
			trace.out("editstudentinput", "createTextArea("+name+") focusTravKeys:"+
					"\n  FORWARD    "+a.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS)+
					"\n  BACKWARD   "+a.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS)+
					"\n  UP_CYCLE   "+a.getFocusTraversalKeys(KeyboardFocusManager.UP_CYCLE_TRAVERSAL_KEYS)+
					"\n  DOWN_CYCLE "+a.getFocusTraversalKeys(KeyboardFocusManager.DOWN_CYCLE_TRAVERSAL_KEYS));
		a.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, TabKey);
		a.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, ShiftTabKey);
		a.setDragEnabled(true);
		a.setDropMode(DropMode.USE_SELECTION);
		a.setTransferHandler(new FunctionTransferHandler(a.getTransferHandler()));
		a.addFocusListener(new FocusListener() {  // added since property change listener on text never fired
			/**
			 * No-op.
			 * @param e
			 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
			 */
			public void focusGained(FocusEvent e) {}
			/**
			 * Generate a {@link PropertyChangeEvent} to alert listeners of a text change when traverse out.
			 * This method would use {@link JTextArea#firePropertyChange(String, Object, Object)}, but
			 * it's protected, while {@link JTextArea#firePropertyChange(String, char, char)} is public.
			 * @param e
			 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
			 */
			public void focusLost(FocusEvent e) {
				a.firePropertyChange("text", 'O', 'N');  // junk old, newValues; listener uses getText()
			}
		});
		return a;
	}
	
	/** Regular expression to remove */
	
	public static String vector2name(int vector)
	{
		switch(vector)
		{
			case(SELECTION):
				return "selection";
			case(ACTION):
				return "action";
			case(INPUT):
				return "input";
		}
		return null;
	}
	
	/**
	 * This will return a MultipleVectorsMatcher with each of the chosen matchers set for
	 * with its corresponding data
	 * 
	 * @return - a new VectorMatcher with the appropriate fields set
	 * @throws Exception - if an exception occurs while creating the matcher
	 */
	public VectorMatcher createMatcher() throws Exception
	{
		List<Matcher> sList = new LinkedList<Matcher>();
		List<Matcher> aList = new LinkedList<Matcher>();
		List<Matcher> iList = new LinkedList<Matcher>();
		List<Matcher>[] lists = new List[3];
		lists[SELECTION] = sList;
		lists[ACTION] = aList;
		lists[INPUT] = iList;

		/*
		 * Note that in general for instantiating matchers, use the factory if you come from a
		 * single text area, with the toString formats, use the MatcherFactory.createSingle
		 * Else (when using special panels, i.e. range and expr), directly instantiate and add
		 */
		if(curView == concatView)
		{
			//iterate through sai
			for(int vector = 0; vector < 3; vector ++)
			{
				Matcher m = null;
				try
				{
					String matcherName = (String)matchersTypes[vector].get(0);
					
					if(matcherName == Matcher.EXPRESSION_MATCHER)
					{
						ExpressionMatcherPanel exprPanel = (ExpressionMatcherPanel)concatView.valuePanels[vector].getComponent(0);
						m = new ExpressionMatcher(true, vector, exprPanel.relationOptions.getSelectedIndex(), exprPanel.inputArea.getText());
					}
					else if(matcherName == Matcher.RANGE_MATCHER)
					{
						RangeMatcherPanel rangePanel = (RangeMatcherPanel)concatView.valuePanels[vector].getComponent(0);
						String min = rangePanel.minimumField.getText(), max = rangePanel.maximumField.getText();
						m = new RangeMatcher(true, vector, min, max);
					}
					else if(matcherName == Matcher.SOLVER_MATCHER)
					{
						SolverMatcherPanel solverPanel = (SolverMatcherPanel)concatView.valuePanels[vector].getComponent(0);
						String input = solverPanel.getText();//exact and the regexes
						m = MatcherFactory.createSingleMatcher(Matcher.EXACT_MATCHER, true, vector, input);						
					}
					else //exact, regex, and wildcard, doesn't really matter for any ...
					{
						String input = concatView.valueAreas[vector].getText();//exact and the regexes
						m = MatcherFactory.createSingleMatcher(matcherName, true, vector, input);
					}
				}
				catch(Exception e)
				{
					//an error occurred in one of the matchers
					concatView.checkButtons[vector].doClick(); //to show what went wrong
					throw e;
				}
				lists[vector].add(m);
			}
		}
		else //the last any match acts as a wildcard ...
		{
			//iterate through sai
			for(int vector = 0; vector < 3; vector ++)
				for(int j = values[vector].size() - 1; j >= 0; j ++)
				{
					String matcherName = (String)matchersTypes[vector].get(j);
					//So the last matcher should not be an Any
					if(matcherName.equals(Matcher.ANY_MATCHER))
						continue;
					lists[vector].add(0, MatcherFactory.createSingleMatcher(matcherName, false, vector, (String)values[vector].get(j)));
				}
		}
		
		VectorMatcher mvm = null;
		if (Matcher.SOLVER_MATCHER.equals(matchersTypes[INPUT].get(0)))
		{
			SolverMatcherPanel solverPanel = (SolverMatcherPanel)concatView.valuePanels[INPUT].getComponent(0);
			Boolean autoSimplify = solverPanel.getAutoSimplify();
			Boolean typeinMode = solverPanel.getTypeinMode();
			String goalName = solverPanel.getGoalName();
			mvm = new SolverMatcher(curView == concatView, sList, aList, iList, getActorFromTvs(), 
					autoSimplify.toString(), typeinMode.toString(), goalName);
		}
		else
		{
			mvm = new VectorMatcher(curView == concatView, sList, aList, iList);
			mvm.setDefaultActor(getActorFromTvs());
		}

		mvm.setLinkTriggered(linkTriggerBtn.isSelected());
		
		/**
		 * Set the defaults back in ...
		 */
		mvm.setDefaultSelection(concatView.defaultAreas[SELECTION].getText());
		mvm.setDefaultAction(concatView.defaultAreas[ACTION].getText());
		mvm.setDefaultInput(concatView.defaultAreas[INPUT].getText());

		saveReplaceInput(mvm);   // save changes to replaceInput
		
		return mvm;
	}
	
	/**
	 * Like {@link #createMatcher()}, but uses @link ExactMatcher for each s, a, i. Assumes concatView.
	 * @return VectorMatcher with ExactMatchers initialized from {@link #values}
	 */
	private VectorMatcher createTempVectorMatcher() {
		List<Matcher> sList = new LinkedList<Matcher>();
		List<Matcher> aList = new LinkedList<Matcher>();
		List<Matcher> iList = new LinkedList<Matcher>();
		List<Matcher>[] lists = new List[] {sList, aList, iList};
		
		for(int vector = 0; vector < 3; vector++) {
			String input = concatView.valueAreas[vector].getText(); //exact and the regexes
			Matcher m = MatcherFactory.createSingleMatcher(Matcher.EXACT_MATCHER, true, vector, input);
			lists[vector].add(m);
		}
		VectorMatcher mvm = new VectorMatcher(curView == concatView, sList, aList, iList);
		mvm.setDefaultActor(getActorFromTvs());

		mvm.setLinkTriggered(linkTriggerBtn.isSelected());

		// Set the defaults in ...
		mvm.setDefaultSelection(concatView.defaultAreas[SELECTION].getText());
		mvm.setDefaultAction(concatView.defaultAreas[ACTION].getText());
		mvm.setDefaultInput(concatView.defaultAreas[INPUT].getText());

		saveReplaceInput(mvm);   // save changes to replaceInput
		return mvm;
	}

	/**
	 * Get the actor from the current settings of the {@link #tvs} buttons.
	 * @return one of @link {@link Matcher#DEFAULT_STUDENT_ACTOR}, {@link Matcher#DEFAULT_TOOL_ACTOR}, etc.
	 */
	private String getActorFromTvs() {
		ButtonModel bm = tvs.getSelection();
		/* borrowed from ExactMatcherPanel */
		String actor = Matcher.DEFAULT_ACTOR;
		if(toolButton.getModel() == bm)
			actor = Matcher.DEFAULT_TOOL_ACTOR;
		else if(anyButton.getModel() == bm)
			actor = Matcher.ANY_ACTOR;
		else if(ungradedButton.getModel() == bm)
			actor = Matcher.UNGRADED_TOOL_ACTOR;
		else if(studentButton.getModel() == bm)
			actor = Matcher.DEFAULT_STUDENT_ACTOR;
		return actor;
	}
	
	/* The matcher type */
	public String getMatcherType() {
		return Matcher.MULTIPLE_VECTORS_MATCHER;
	}
	
/*	private boolean wasSAIChanged(){
		VectorMatcher oldMatcher = (VectorMatcher)previousEdgeData.getMatcher();
		VectorMatcher newMatcher = (VectorMatcher)edgeData.getMatcher();
		if(newMatcher.getSelectionMatcher().)
		return false;
	}*/
	/**
	 * Called when okButton or cancelButton are pressed
	 */
	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();
		
		if(src == okButton)
			commitChanges(edgeData);
		else if(src != cancelButton)
			return;
		
		parent.close();
	}

	/**
	 * Call {@link #curView}.{@link MatcherView#updateInfo() updateInfo()} and
	 * write the changes made in the UI to the {@link #edgeData} instance.
	 * @param ed instance to change
	 * @return argument ed, after changes
	 */
	public EdgeData commitChanges(EdgeData ed) {
		curView.updateInfo(ed);
		Matcher m = null;
		try
		{
			m = createMatcher();
		}
		catch(Exception e)
		{
			String errMsg = "Error creating matcher: "+e+(e.getCause() == null ? "" : ";\n  cause "+e.getCause());
			JOptionPane.showMessageDialog(parent, errMsg, "Error editing student input matching",
					JOptionPane.ERROR_MESSAGE);
			trace.errStack(errMsg, e);
			return ed;
		}

		if (m != null)
			ed.setMatcher(m);
		ed.getActionLabel().resetForeground(); //show the new label
		ed.getActionLabel().update();		
		ed.getController().fireCtatModeEvent(CtatModeEvent.REPAINT);
		if(ed == edgeData)        // suppress events if not changing the real object
			ed.getController().getProblemModel().fireProblemModelEvent(
					new EdgeUpdatedEvent(this, edgeData.getEdge(), true));
		return ed;
	}

	/**
	 * Save the contents of the {@link #replaceInputFormulaField} to
	 * {@link Matcher#setReplacementFormula(String)}.
	 * @param m Matcher to set
	 */
	private void saveReplaceInput(Matcher m) {
		boolean checked = replaceInputCheckBox.getModel().isSelected();
		if (trace.getDebugCode("pm")) trace.outNT("pm", "saveInputReplace: checkbox.isSelected() "+checked+
				", field "+replaceInputFormulaField.getModel().getSelectedItem().toString());
		if (!checked)
			m.setReplacementFormula(null);
		else {
			Object item = replaceInputFormulaField.getModel().getSelectedItem();
			String formula = (item == null ? null : item.toString());
			if (formula == null)
				m.setReplacementFormula(null);
			else if ((formula = formula.trim()).length() < 1)
				m.setReplacementFormula(null);
			else if (formula.equalsIgnoreCase(DONT_REPLACE))
				m.setReplacementFormula(null);
			else 
				m.setReplacementFormula(formula);
		}
	}

	public void switchView(String viewType)
	{
		view.removeAll();
		if(viewType == CONCAT_VIEW)
		{
			if(concatView == null)
				concatView = new ConcatView(false);
			curView = concatView;
		}
		else if(viewType == SIMPLE_VIEW)
		{
			if(simpleView == null)
				simpleView = new SimpleView(false);
			curView = simpleView;
		}
		else if(viewType == COMPLEX_VIEW)
		{
			if(complexView == null)
				complexView = new ComplexView(false);
			curView = complexView;
		}
		
		curView.updateView();
		view.add(curView);
		validate();
		repaint();
	}
	
	//FOLLOWING ARE ALL PRIVATE CLASSES
	
	/**
	 * Abstract class for all matcher views
	 * In general, they have been implemented such that only one needs to be constructed
	 * per instance of VectorMatcherPanel
	 * @author wko2
	 */
	private abstract class MatcherView extends JPanel
	{
		/**
		 * Updates the new view
		 */
		public abstract void updateView();
		
		/**
		 * Uploads all information in the view into the appropriate
		 * data structures (sai values vectors, sai matchers vectors)
		 * @param ed instance to write
		 */
		public abstract void updateInfo(EdgeData ed);
	}
	
	/**
	 * This view is for general matching on the concatenation of an entire vector
	 * It uses an editable values JTextField and matcher Combo box for each of selection,
	 * action, and input
	 * 
	 * The concatenation is done with \n delimiters.  There are no sub-dialogs for this view,
	 * so the values vectors are only updated upon hitting Othor wko2
	 */
	private class ConcatView extends MatcherView implements ActionListener, MouseListener
	{		
		final JTextArea defaultSelectionArea = createTextArea("defaultSelectionArea"),
					defaultActionArea = createTextArea("defaultActionArea"),
					defaultInputArea = createTextArea("defaultInputArea");
		JTextArea[] defaultAreas = {defaultSelectionArea, defaultActionArea, defaultInputArea};
		
		final JTextArea selectionValueArea = createTextArea("selectionValueArea"),
					actionValueArea = createTextArea("actionValueArea"),
					inputValueArea = createTextArea("inputValueArea");
		JTextArea[] valueAreas = {selectionValueArea, actionValueArea, inputValueArea};
		
		CtatListModel actionsLM = listActions(selectionValueArea);
		CtatListModel selectionsLM = listSelections();  // register selection listener last: triggers request
		
		final JComponent selectionValueList = createValueList("SelectionValueList", selectionsLM,
				"Drag names to the Demonstrated Value or Matcher Settings area for this selection.");
		final JComponent actionValueList = createValueList("ActionValueList", actionsLM,
				"<html>Drag Action names from this list to the Demonstrated Value or Matcher Settings area.<br/>"+
				"<b>Boldface</b> actions are known to supported by the component named in Selection Matcher Settings.</html>");
		final JComponent variableNameList = createValueList("InputValueList", listVariables(selectionsLM),
				"Drag variable names from this list for use in formulas.");
		
		final JPanel selectionValuePanel = createValueAreaPanel(selectionValueArea,
						new Dimension(TEXT_AREA_DIM.width, DEMO_SEL_ACT_DIM.height)),
				actionValuePanel = createValueAreaPanel(actionValueArea,
						new Dimension(TEXT_AREA_DIM.width, DEMO_SEL_ACT_DIM.height)),
				inputValuePanel = createValueAreaPanel(inputValueArea, TEXT_AREA_DIM);
		JPanel[] valuePanels = {selectionValuePanel, actionValuePanel, inputValuePanel};
		
		final JComboBox selectionMatcherCombo = createCombo(DEFAULT_SELECTION_MATCHERS, false, this, "selectionMatcherCombo"),
				actionMatcherCombo = createCombo(DEFAULT_ACTION_MATCHERS, false, this, "actionMatcherCombo"),
				inputMatcherCombo = createCombo(DEFAULT_INPUT_MATCHERS, false, this, "inputMatcherCombo");
		JComboBox[] matcherCombos = {selectionMatcherCombo, actionMatcherCombo, inputMatcherCombo};
		
		/** These are for status/check output */
		final JTextArea selectionMatcherArea = createTextArea("selectionMatcherArea"),
					actionMatcherArea = createTextArea("actionMatcherArea"),
					inputMatcherArea = createTextArea("inputMatcherArea");
		JTextArea[] matcherAreas = {selectionMatcherArea, actionMatcherArea, inputMatcherArea};
		{ for(JTextArea ma : matcherAreas) ma.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11)); }
		
		final JButton selectionCheckButton = createCheckButton("Check", "selectionCheckButton"),
					actionCheckButton = createCheckButton("Check", "actionCheckButton"),
					inputCheckButton = createCheckButton("Check", "inputCheckButton");
		JButton[] checkButtons = {selectionCheckButton, actionCheckButton, inputCheckButton};
		
		final JButton checkAllButton = createCheckButton("Check All", "checkAllButton");
		
		final JButton selectionLastEvalButton = createLastEvalButton("selectionLastEvalButton"),
					actionLastEvalButton = createLastEvalButton("actionLastEvalButton"),
					inputLastEvalButton = createLastEvalButton("inputLastEvalButton");
		JButton[] lastEvalButtons = {selectionLastEvalButton, actionLastEvalButton, inputLastEvalButton};
		
		/** These JPanels should only contain a single component */
		final JPanel selectionMatcherPanel = createMatcherPanel(SELECTION),
					actionMatcherPanel = createMatcherPanel(ACTION),
					inputMatcherPanel = createMatcherPanel(INPUT);
		JPanel[] matcherPanels = {selectionMatcherPanel, actionMatcherPanel, inputMatcherPanel};
		
		//for the actor box
		GridBagConstraints c; 
		
		/**
		 * This constructor creates a view with an argument of whether to set default values
		 * @param initialize - true if we want to set default values to the matchers, false otherwise
		 */
		public ConcatView(boolean initialize)
		{
			super();
			
			SAIPane = new JPanel(new GridBagLayout());
			SAIPane.setBorder(new EmptyBorder(0, 0, 4, 0));  // space above OK & Cancel buttons
//			add(WindowUtils.wrapLeft(SAIPane)); //stick the gridbag into the box
			
			for(int vector = 0; vector < 3; vector ++)
			{
				valueAreas[vector].addMouseListener(this);
				matcherAreas[vector].setBackground(UneditableBackgroundColor);
				matcherAreas[vector].setRows(2);
				matcherAreas[vector].setColumns(20);
			}
			
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.LINE_START;
			c.ipadx = 5;
			c.ipady = 5;
			
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.LINE_END;
			c.insets = new Insets(0, 0, 10, 0);
			c.gridx = 0;
			c.gridy = 0;
			SAIPane.add(new JLabel("<html><b>Link #"+edgeData.getUniqueID()+"</b></html>"), c);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.LINE_START;
			c.insets = new Insets(0, 10, 10, 0);
			c.gridx = 1;
			SAIPane.add(new JLabel("<html><b>Demonstrated Value</b></html>"), c);
			c.gridx = 2;
			c.weightx = 1;
			SAIPane.add(new JLabel("<html><b>Matcher Settings</b></html>"), c);
			c.weightx = 0;
			c.gridx = 3;
			SAIPane.add(new JLabel("<html><b>Matcher Type and Check</b></html>"), c);
			c.gridx = 4;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.LINE_END;
			SAIPane.add(checkAllButton, c);
			
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.FIRST_LINE_END;
			c.insets = new Insets(0, 0, 10, 0);
			c.gridx = 0;
			c.gridy = 1;
			c.weighty = 0.2;
			SAIPane.add(new JLabel("Selection:"), c);
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.insets = new Insets(0, 10, 10, 0);
			c.gridx = 1;
			SAIPane.add(createValueAreaPanel(defaultSelectionArea, selectionValueList, DEMO_SEL_ACT_DIM), c);
			c.gridx = 2;
			c.weightx = 1;
			SAIPane.add(selectionValuePanel, c);
			c.weightx = 0;
			c.gridx = 3;
			c.gridwidth = GridBagConstraints.REMAINDER;
			SAIPane.add(selectionMatcherPanel, c);
			c.gridwidth = 1;
			
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.FIRST_LINE_END;
			c.insets = new Insets(0, 0, 10, 0);
			c.gridx = 0;
			c.gridy = 2;
			c.weighty = 0.2;
			SAIPane.add(new JLabel("Action:"), c);
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.insets = new Insets(0, 10, 10, 0);
			c.gridx = 1;
			SAIPane.add(createValueAreaPanel(defaultActionArea, actionValueList, DEMO_SEL_ACT_DIM), c);
			c.gridx = 2;
			c.weightx = 1;
			SAIPane.add(actionValuePanel, c);
			c.weightx = 0;
			c.gridx = 3;
			c.gridwidth = GridBagConstraints.REMAINDER;
			SAIPane.add(actionMatcherPanel, c);
			c.gridwidth = 1;
			
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.FIRST_LINE_END;
			c.insets = new Insets(0, 0, 10, 0);
			c.gridx = 0;
			c.gridy = 3;
			c.weighty = 0.6;
			SAIPane.add(new JLabel("Input:"), c);
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.insets = new Insets(0, 10, 10, 0);
			c.gridx = 1;
			SAIPane.add(createValueAreaPanel(defaultInputArea, variableNameList,
					new Dimension(DEMO_SEL_ACT_DIM.width, TEXT_AREA_DIM.height)), c);
			c.gridx = 2;
			c.weightx = 1;
			SAIPane.add(inputValuePanel, c);
			c.weightx = 0;
			c.gridx = 3;
			c.gridwidth = GridBagConstraints.REMAINDER;
			SAIPane.add(inputMatcherPanel, c);
			c.gridwidth = 1;
			c.weighty = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			
			for(int i = 0; i < 3; i ++)
				setDefaultAreaValues(i);
			
			updateView(); //sticks actor box on too ...

			setLayout(new BorderLayout());
			add(SAIPane, BorderLayout.CENTER); //stick the gridbag into the MatchView
		}
		
		/**
		 * Make the custom focus traversal policy available.
		 */
		FocusTraversalPolicy getCustomFocusTraversalPolicy() {
			return new SAITraversalPolicy(new JComponent[] {
					defaultSelectionArea, selectionValueArea,
					defaultActionArea, actionValueArea,
					defaultInputArea, inputValueArea
			});
		}

		/**
		 * So that we tab through only the most useful components.
		 */
		class SAITraversalPolicy extends FocusTraversalPolicy {
			private final JComponent[] components;
			SAITraversalPolicy(JComponent[] components) {
				this.components = components;
			}
			public Component getComponentAfter(Container aContainer, Component aComponent) {
				for(int i = 0; i < components.length-1; ++i)
					if(components[i].equals(aComponent))
						return components[i+1];
				return components[0];
			}
			public Component getComponentBefore(Container aContainer, Component aComponent) {
				for(int i = components.length-1; i > 0; --i)
					if(components[i].equals(aComponent))
						return components[i-1];
				return components[components.length-1];
			}
			public Component getFirstComponent(Container aContainer) {
				return components[0];
			}
			public Component getLastComponent(Container aContainer) {
				return components[components.length-1];
			}
			public Component getDefaultComponent(Container aContainer) {
				return getFirstComponent(aContainer);
			}
		}
		
		/**
		 * Generate a {@link JList} with the given items, with drag enabled so that the user
		 * can drag values from the list to insert into other fields.
		 * @param name Swing component name; <i>also used for tooltip</i>
		 * @param listModel data for the JList
		 * @param toolTip if not null, use this toolTip
		 * @return scroll pane containing JList of items
		 */
		private JComponent createValueList(String name, final CtatListModel listModel, String toolTip) {
			final JList result = new JList(listModel);
			listModel.addListDataListener(new ListDataListener() {

				/** Not called by {@link CtatListModel}. */
			    public void intervalAdded(ListDataEvent e) {}

			    /** Not called by {@link CtatListModel}. */
			    public void intervalRemoved(ListDataEvent e) {}

			    /**
			     * Called by {@link CtatListModel} when its data come in. 
			     * @param e event information
			     */
			    public void contentsChanged(ListDataEvent e) {
			    	if(trace.getDebugCode("editstudentinput"))
			    		trace.out("editstudentinput","contentsChanged("+e+") receieved: to setModel()");
			    	result.setModel(listModel);        // reset appears to prompt JList to reread the model FIXME
			    	result.setEnabled(listModel.hasData());
			    }
			});
			result.addMouseListener(new MouseAdapter() {
				/** Mask to tell whether any mouse button is down, to detect drags. */
				final int mask = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK |
						MouseEvent.BUTTON3_DOWN_MASK;
				/**
				 * If no drag is in progress, change the cursor to the {@link Cursor#MOVE_CURSOR} on entry. 
				 * @param e compares {@link MouseEvent#getModifiersEx()} against {@value #mask}
				 */
				public void mouseEntered(MouseEvent e) {
					if((e.getModifiersEx() & mask) == 0)
						result.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				}
				/**
				 * If no drag is in progress, change the cursor back to the default on exit. 
				 * @param e compares {@link MouseEvent#getModifiersEx()} against {@value #mask}
				 */
				public void mouseExited(MouseEvent e) {
					if((e.getModifiersEx() & mask) == 0)
						result.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			});
			result.setCellRenderer(new StyledStringRenderer(result.getCellRenderer()));
			result.setEnabled(listModel.hasData());
			result.setName(name);
			result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			result.setLayoutOrientation(JList.VERTICAL);
			result.setBackground(Color.WHITE);
			result.setFont(new Font(null, Font.PLAIN, 11));
			result.setVisibleRowCount(3);
			result.setDragEnabled(true);
			String saiElt = name.replace("ValueList", "");
			if(listModel.getCannotFill())
				result.setToolTipText("Cannot retrieve names of selections because no student interface is connected.");
			else
				result.setToolTipText(toolTip);
			JScrollPane scroller = new JScrollPane(result);
			scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			return scroller;
		}		

		/**
		 * This method returns a list of action names depending on whether the tools are set to
		 * connect to Java or Flash interfaces.
		 * @return sorted list of available SAI action names; null if no interface connected
		 */
		private CtatListModel listSelections() {
			CtatListModel result = new CtatListModel(null);
			BR_Controller ctlr;
			UniversalToolProxy utp;
			if((ctlr = edgeData.getController()) == null || (utp = ctlr.getUniversalToolProxy()) == null)
				result.setCannotFill(true);
			else if(utp.getStudentInterfaceConnectionStatus() == StudentInterfaceConnectionStatus.Disconnected)
				result.setCannotFill(true);
			else
				edgeData.getController().getUniversalToolProxy().listSelectionNames(result);
			return result;
		}

		/**
		 * This method returns a list of action names depending on whether the tools are set to
		 * connect to Java or Flash interfaces.
		 * @return sorted list of available SAI action names, encapsulated in a {@link ListModel}
		 */
		private CtatListModel listActions(JTextComponent textArea) {
			String studentInterfacePlatform = UniversalToolProxy.FLASH;
			UniversalToolProxy utp = null;
			ActionListModel result;
			if(edgeData.getController() != null && (utp = edgeData.getController().getUniversalToolProxy()) != null)
				studentInterfacePlatform = utp.getStudentInterfacePlatform();
			if(utp != null) {
				result = new ActionListModel(utp.getAllActionNames(), utp.getStartStateModel());
				utp.addStartStateListener(result);
			} else if(UniversalToolProxy.JAVA.equalsIgnoreCase(studentInterfacePlatform))
				result = new ActionListModel(JCommWidget.listActionNames(), null);
			else
				result = new ActionListModel(RemoteToolProxy.getFlashActionNames(), null);
			if(textArea != null)
				textArea.addPropertyChangeListener(result);  // only for Actions
			return result;
		}

		/**
		 * This method returns a list of action names depending on whether the tools are set to
		 * connect to Java or Flash interfaces.
		 * @return sorted list of available SAI action names, encapsulated in a {@link ListModel}
		 */
		private CtatListModel listVariables(CtatListModel selectionsLM) {
			
			/** A list model that gets updates from another list model, when more selections are available. */
			class VarListModel extends CtatListModel implements ListDataListener {

				private static final long serialVersionUID = 201403071920L;
				/**
				 * @param selectionsLM argument for {@link #refresh(CtatListModel)}
				 */
				VarListModel(CtatListModel selectionsLM) {
					super(null);
					refresh(selectionsLM);
				}
				/**
				 * Populate the list model from the minimal {"selection", "action", "input"},
				 * the {@link ProblemModel#getVariableTable()} and {@link #getFunctionList()}.
				 * @param selectionsLM remove variables named for selections in this list model
				 */
				private synchronized void refresh(CtatListModel selectionsLM) {
					BR_Controller ctlr;
					ProblemModel pm;
					VariableTable vt;
					List<String> minVList = new ArrayList<String>(Arrays.asList(new String[] {"selection", "action", "input"}));
					if((ctlr = edgeData.getController()) == null ||
							(pm = ctlr.getProblemModel()) == null ||
							(vt = pm.getVariableTable()) == null) {
						minVList.addAll(VectorMatcherPanel.getFunctionList());
						addAll(minVList, false);  // false => don't resort the list
						return;
					}
					Set<String> vars = new HashSet<String>(vt.keySet());
					int n = vars.size(), k = 0;  // for tracing
					for(Iterator<String> it = selectionsLM.iterator(); it.hasNext();) {
						if(vars.remove(it.next()))  // set difference {vars} - {selections}
							++k;
					}
					vars.addAll(minVList);           // just in case there's a component named "selection"
					if(trace.getDebugCode("editstudentinput"))
						trace.out("editstudentinput", "VarListModel.refresh("+selectionsLM+") "+
								n+" vars, "+k+" removed, "+minVList.size()+" minVars,"+
								" final size "+vars.size());
					List<String> vList = new ArrayList<String>(vars);
					Collections.sort(vList, String.CASE_INSENSITIVE_ORDER);
					vList.addAll(VectorMatcherPanel.getFunctionList());
					addAll(vList, false);  // false => don't resort the list
				}
				/** No-op. */
				public void intervalAdded(ListDataEvent e) {}
				/** No-op. */
				public void intervalRemoved(ListDataEvent e) {}
				/**
				 * Call {@link #refresh(CtatListModel)}, passing the event source, which should be a selections
				 * {@link CtatListModel}.
				 * @param e {@link ListDataEvent#getSource() getSource()} is assumed to be a {@link CtatListModel}
				 * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
				 */
				public void contentsChanged(ListDataEvent e) {
					if(!(e.getSource() instanceof CtatListModel))
						throw new IllegalArgumentException("Event source "+trace.nh(e.getSource())+
								" is not a CtatListModel; event "+e);
					refresh((CtatListModel) e.getSource());
				}
			}
			VarListModel result = new VarListModel(selectionsLM);
			selectionsLM.addListDataListener(result);
			return result;
		}

		//end constructor
		
		/**
		 * Creates a last evaluation button hooked up to this thing ...
		 * @param name - the name to be used for testing
		 * @return - a JButton for Last Evaluation
		 */
		private JButton createLastEvalButton(String name)
		{
			JButton b = new JButton("Last Evaluation");
			b.setName(name);
			b.setFont(new Font(null, Font.PLAIN, 11));
			b.addActionListener(this);
			b.setVisible(false); //default off
			return b;
		}
		
		/**
		 * Creates the panel for a value using the default dimensions.
		 * @param area text area for entering values
		 * @param dim preferred size of the JPanel
		 * @return JPanel holding the text area
		 */
		private JPanel createValueAreaPanel(JTextArea area, Dimension dim)
		{
			area.setRows(4);
//			area.setColumns(32);
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
			p.setPreferredSize(dim);  // TEXT_AREA_DIM
//			p.setMaximumSize(getPreferredSize()); // (TEXT_AREA_DIM);
			p.setMinimumSize(dim);
			p.add(scrollerizeTextArea(area, null));
			p.add(Box.createRigidArea(new Dimension(dim.width, 0)));
			return p;
		}
		
		/**
		 * Creates the panel for a value using dimensions.
		 * @param area text area for value entry
		 * @param list scroll pane holding a list of choices
		 * @param dim preferred size
		 * @return - a JPanel holding the text area
		 */
		private JPanel createValueAreaPanel(JTextArea area, JComponent list, Dimension dim)
		{
//			area.setRows(3);   instead set scroll pane height below.
//			area.setColumns(32);
			JPanel p = new JPanel();
//			p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
			p.setLayout(new GridBagLayout());
			p.setPreferredSize(dim);
			p.setMinimumSize(dim); // (TEXT_AREA_DIM);
//			p.setMaximumSize(getPreferredSize()); // (TEXT_AREA_DIM);
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1;
			c.weighty = 0;
			
			FontMetrics fm = area.getFontMetrics(area.getFont());
			JScrollPane pane = scrollerizeTextArea(area, new Dimension(dim.width, fm.getHeight()*3));
			
			p.add(pane, c);
//			p.add(Box.createRigidArea(new Dimension(dim.width, 2)));
			c.insets = new Insets(2, 0, 0, 0);
			c.fill = GridBagConstraints.BOTH;
			c.gridy = 1;
			c.gridheight = GridBagConstraints.REMAINDER;
			c.weighty = 1;
			list.setPreferredSize(new Dimension(dim.width, dim.height-pane.getPreferredSize().height-2));
			p.add(list, c);
			return p;
		}
		
		/**
		 * Creates a check button
		 * @param text - the text displayed on the button
		 * @param name - the name for testing
		 * @return - the button
		 */
		private JButton createCheckButton(String text, String name)
		{
			JButton b = new JButton(text);
			b.setName(name);
			b.setFont(new Font(null, Font.PLAIN, 11));
			b.addActionListener(this);
			return b;
		}
		
		/**
		 * Creates a panel which contains the matcher combo, its status area, and a check button
		 * @param vector - S A or I
		 * @return - the panel
		 */
		private JPanel createMatcherPanel(int vector)
		{
			matcherAreas[vector].setEditable(false);
			matcherAreas[vector].setBackground(UneditableBackgroundColor);
			matcherAreas[vector].setRows(2);
			matcherAreas[vector].setColumns(20);
			JPanel pane = new JPanel();
			pane.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			
			c.insets = new Insets(0, 0, 2, 0);
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.gridx = 0;
			
			c.fill = GridBagConstraints.NONE;
			c.gridy = 0;
			pane.add(matcherCombos[vector], c);
			
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1;
			c.weighty = 1;
			c.gridy = 1;
			matcherAreas[vector].setRows(4);
			JScrollPane masp = scrollerizeTextArea(matcherAreas[vector], null);
			pane.add(masp, c);

			c.insets = new Insets(0, 0, 0, 0);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1;
			c.weighty = 0;
			c.gridy = 2;
			Box buttons = new Box(BoxLayout.LINE_AXIS);
			buttons.add(checkButtons[vector]);
			buttons.add(Box.createHorizontalGlue());
			buttons.add(lastEvalButtons[vector]);
			pane.add(buttons, c);

			return pane;
		}
		
		/**
		 * Creates an area for an Expression matcher
		 * @param vector - s a or i
		 * @return
		 */
		private ExpressionMatcherPanel createExpressionArea(int vector, String name)
		{
			ExpressionMatcherPanel expr = new ExpressionMatcherPanel(edgeData, valueAreas[vector].getText());
			expr.setName(name);
			expr.setPreferredSize(TEXT_AREA_DIM);
//			expr.setMaximumSize(TEXT_AREA_DIM);
			return expr;
		}
		
		/**
		 * Creates an area for a Range matcher
		 * @param vector - s a or i
		 * @return
		 */
		private RangeMatcherPanel createRangeArea(int vector, String name)
		{
			RangeMatcherPanel rmp = new RangeMatcherPanel(valueAreas[vector].getText());
			rmp.setName(name);
			return rmp;
		}
		
		/**
		 * Creates an area for a Solver matcher panel.
		 * @param vector - s a or i
		 * @return
		 */
		private MatcherPanel createSolverArea(Matcher vm, String name)
		{
			if (!(vm instanceof VectorMatcher)) {
				trace.err("createSolverArea() needs a VectorMatcher but got "+vm.getClass().getSimpleName());
				vm = createTempVectorMatcher();
			}
			MatcherPanel vmp = new SolverMatcherPanel((VectorMatcher) vm, getBackground());
			vmp.setName(name);
			return vmp;
		}
		
		//end constructor helper methods
		
		public void setDefaultAreaValues(int vector)
		{
			defaultAreas[vector].setText(getDemonstratedValue(vector)); //sets appropriately
			defaultAreas[vector].setEditable(true);
			defaultAreas[vector].setBackground(Color.WHITE);
		}
		
		/**
		 * Executes a check for the matcher settingss on the demonstrated values
		 * This method will print necessary content to the status areas
		 * Return true if the syntax validated and the created matcher matches on
		 * the demonstrated values
		 */
		private boolean check(int vector)
		{
			if(getDemonstratedValue(vector) == null)
			{
				matcherAreas[vector].setText("No demonstrated values given");
				return false;
			}
			
			String matcherName = (String)matchersTypes[vector].get(0);
			
			Matcher m = null;
			
			//For now, we deal with Expression Matcher separately since errors can occur after instantiation as opposed to the rest
			if(matcherName == Matcher.EXPRESSION_MATCHER) 
			{
				ExpressionMatcherPanel exprPanel = (ExpressionMatcherPanel)valuePanels[vector].getComponent(0);
				exprPanel.setSyntaxCheck(matcherAreas[vector]);
				//this will create the matcher, run a syntax check, check against values, and update the status area
				return exprPanel.checkDemonstratedValues(true, true, vector, defaultSelectionVector, defaultActionVector, defaultInputVector);
			}
			//range is only different in setting up the matcher itself
			else if(matcherName == Matcher.RANGE_MATCHER)
			{
				RangeMatcherPanel rangePanel = (RangeMatcherPanel)valuePanels[vector].getComponent(0);
				String min = rangePanel.minimumField.getText(), max = rangePanel.maximumField.getText();
				try
				{
					m = new RangeMatcher(true, vector, min, max);
				}
				catch(NumberFormatException e)
				{
					matcherAreas[vector].setText("Invalid syntax: \"" + min + "\" or \"" + max + "\"");
					return false;
				}
			}
			else
			{
				//this is almost the same code as from VectorMatcherPanel.getMatcher()
				String input = valueAreas[vector].getText();
				try
				{
					m = MatcherFactory.createSingleMatcher(matcherName, true, vector, input);
				}
				catch(Exception e)
				{
					matcherAreas[vector].setText("Invalid syntax: " + input);
					return false;
				}
			}
			
			boolean checkAgainst = m.matchConcatenation(defaults[vector]);
			if(checkAgainst)
				matcherAreas[vector].setText("Matches demonstrated value(s)");
			else
				matcherAreas[vector].setText("Did not match demonstrated value(s)");
			
			return checkAgainst;
		}
		
		/**
		 * Listens on the matcher combos
		 */
		public void actionPerformed(ActionEvent ae) {
			Object src = ae.getSource();
			if(src instanceof JComboBox) //change if we end up with more combo boxes than just matcher selection ...
			{
				int vector;
				String matcherName = null;
				
				for(vector = 0; vector < 3; vector ++) {
					if(src == matcherCombos[vector])
					{
						if (vector == INPUT) {    // cf. SOLVER_MATCHER case below
							matcherCombos[SELECTION].setEnabled(true);
							matcherCombos[ACTION].setEnabled(true);
						}
							
						matcherAreas[vector].setText(""); //clear the status
						//leaving an expression matcher
						if(matchersTypes[vector].get(0) == Matcher.EXPRESSION_MATCHER)
						{
							lastEvalButtons[vector].setVisible(false);
							Component panel;
							/*
							 * This check should only fail on when the dialog is first opened with an
							 * expression matcher already set.  This happens because in this case, we 
							 * check the old type from the matchersTypes map, which is  already set to 
							 * ExpressionMatcher, but
							 *  there is no actual ExpressionMatcherPanel instantiated yet
							 */
							if((panel = valuePanels[vector].getComponent(0)) instanceof ActionListener) {							
								lastEvalButtons[vector].removeActionListener((ActionListener)panel);
							}
						}
						matchersTypes[vector].put(0, (matcherName = (String)matcherCombos[vector].getSelectedItem()));
						break;
					}
				}
				if(vector == 3) //check in case this was none of our combo boxes
					return;
				valuePanels[vector].removeAll();
//				if(existingMatcher instanceof SolverMatcher && vector == INPUT)
				if(matcherName == Matcher.SOLVER_MATCHER)
				{
					matcherCombos[SELECTION].setEnabled(false);
					matcherCombos[ACTION].setEnabled(false);					
					valuePanels[vector].add(createSolverArea(existingMatcher, vector2name(vector)+"SolverArea"));
				}
				else if(matcherName == Matcher.EXPRESSION_MATCHER)
				{
					ExpressionMatcherPanel exprPanel = createExpressionArea(vector, vector2name(vector) + "ExpressionArea");
					valuePanels[vector].add(exprPanel);
					
					if(existingMatcher instanceof ExpressionMatcher){
						exprPanel.setLastMatcher((ExpressionMatcher)existingMatcher);
					}
					else if(existingMatcher instanceof VectorMatcher) {
						if(((VectorMatcher)existingMatcher).getMatchers(vector).get(0) instanceof ExpressionMatcher)
							exprPanel.setLastMatcher((ExpressionMatcher)((VectorMatcher)existingMatcher).getMatchers(vector).get(0));
					}
					lastEvalButtons[vector].addActionListener(exprPanel);
					lastEvalButtons[vector].setVisible(true);
					exprPanel.setSyntaxCheck(matcherAreas[vector]);
				}
				else if(matcherName == Matcher.RANGE_MATCHER)
					valuePanels[vector].add(createRangeArea(vector, vector2name(vector) + "RangeArea"));
				else {
//					valuePanels[vector].add(scrollerizeTextArea(valueAreas[vector], TEXT_AREA_DIM));
					valuePanels[vector].add(scrollerizeTextArea(valueAreas[vector], null));
					valuePanels[vector].add(Box.createRigidArea(new Dimension(TEXT_AREA_DIM.width, 0)));
				}
					
				parent.validate();
				parent.repaint();
			}
			else if(src == selectionCheckButton)
				check(SELECTION);
			else if(src == actionCheckButton)
				check(ACTION);
			else if(src == inputCheckButton)
				check(INPUT);
			else if(src == checkAllButton)
			{
				for(int vector = 0; vector < 3; vector ++)
					check(vector);
			}
		}
		
		private void updateVectorInfo(int vector)
		{
			String[] vals = valueAreas[vector].getText().split("\n", -1); //apparently String.split only allows zero-length array elements when the second argument is negative
			Vector<String> s = new Vector();
			for(int i = 0; i < vals.length; i ++)
				s.add(vals[i]);
			values[vector] = s;
			matchersTypes[vector].put(0, matcherCombos[vector].getSelectedItem());
		}
		
		// Saaved edited demonstrated value
		private void updateDemonstrateValueInfo(JTextArea[] demonstrateValue, EdgeData ed)
		{
			for(int i = 0; i < demonstrateValue.length; i ++) {
				String newValue = demonstrateValue[i].getText();
				if (trace.getDebugCode("br")) trace.out("br", "ConcatView.demonstrateValue["+i+"]="+newValue);
				Vector<String> currentValueVector = new Vector<String>();
				currentValueVector.add(newValue);
				if (demonstrateValue[i] == defaultSelectionArea) ed.setSelection(currentValueVector);
				else if (demonstrateValue[i] == defaultActionArea) ed.setAction(currentValueVector);
				else if (demonstrateValue[i] == defaultInputArea) ed.setInput(currentValueVector);
			}
			ed.resetCommMessage();
		}
		
		/**
		 * Uploads all information in the view into the appropriate
		 * data structures (sai values vectors, sai matchers vectors)
		 */
		public void updateInfo(EdgeData ed) {
			updateVectorInfo(SELECTION);
			updateVectorInfo(ACTION);
			updateVectorInfo(INPUT);

			updateDemonstrateValueInfo(defaultAreas, ed); // chc added 10/17/2008 for bug CTAT2096
			
			if(ed == edgeData) {
				SAIPane.remove(actorLabel);
				SAIPane.remove(actorBox);
				SAIPane.remove(triggerLabel);
				SAIPane.remove(triggerBox);
				SAIPane.remove(replaceInputCheckBox);
				SAIPane.remove(replaceInputFormulaField);
			}
		}

		private void updateVectorView(int vector)
		{
			/*
			 * if(matchersTypes[vector].get(0).equals(Matcher.RANGE_MATCHER))
			{
				((RangeMatcherPanel)valuePanels[vector].getComponent(0)).
			}
			else if(matchersTypes[vector].get(0).equals(Matcher.EXPRESSION_MATCHER))
			{
				
			}
			else
			{
			 */
			String text = Matcher.vector2ConcatString(values[vector]);
			valueAreas[vector].setText(text);
			if (existingMatcher instanceof SolverMatcher && vector == INPUT)
				matcherCombos[vector].setSelectedItem(Matcher.SOLVER_MATCHER);
			else
				matcherCombos[vector].setSelectedItem(matchersTypes[vector].get(0));
		}
		
		/**
		 * Updates the new view
		 */
		public void updateView() {
			updateVectorView(SELECTION);
			updateVectorView(ACTION);
			updateVectorView(INPUT);
			
			//actor box, trigger choice, replace student input entry
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.LINE_END;
			c.insets = new Insets(0, 0, 2, 0);
			c.gridy = 4;
			c.gridx = 0;
			SAIPane.add(actorLabel, c);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.LINE_START;
			c.insets = new Insets(0, 10, 2, 0);
			c.gridx = 1;
			c.gridwidth = 2;
			SAIPane.add(actorBox, c);
			c.gridx = 3;
			c.gridwidth = GridBagConstraints.REMAINDER;
			SAIPane.add(replaceInputCheckBox, c);
			c.gridwidth = 1;
			
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.LINE_END;
			c.insets = new Insets(0, 0, 0, 0);
			c.gridy = 5;
			c.gridx = 0;
			SAIPane.add(triggerLabel, c);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.LINE_START;
			c.insets = new Insets(0, 10, 0, 0);
			c.gridx = 1;
			c.gridwidth = 2;
			SAIPane.add(triggerBox, c);
			c.gridx = 3;
			c.gridwidth = GridBagConstraints.REMAINDER;
			SAIPane.add(replaceInputFormulaField, c);
			c.gridwidth = 1;
		}
		
		public void mouseClicked(MouseEvent me) {
			Object src = me.getSource();
		}
		
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		/**
		 * Listens on the text areas for a right click
		 */
		public void mouseReleased(MouseEvent me) {
			/*Object src = me.getSource();
			if(SwingUtilities.isRightMouseButton(me))
			{
				int vector = -1;
				for(int i = 0; i < areas.length; i ++)
					if(src == areas[i])
					{
						vector = i;
						break;
					}
				
				if(vector == -1) //didn't find anything, should never get here ...
					return;
				
				Object selectedItem = matcherCombos[vector].getSelectedItem(); 
				if(selectedItem == Matcher.EXPRESSION_MATCHER)
					new ExpressionDialog(parent, this, areas[vector].getText(), vector, true, edgeData);
				else if(selectedItem == Matcher.RANGE_MATCHER)
					new RangeDialog(parent, this, areas[vector].getText(), vector);
				else if(selectedItem == Matcher.WILDCARD_MATCHER)
					new RegexDialog(parent, this, areas[vector].getText(), vector, true);
				else if(selectedItem == Matcher.REGULAR_EXPRESSION_MATCHER)
					new RegexDialog(parent, this, areas[vector].getText(), vector, false);
			}*/			
		}
	}
	
	/**
	 * This view is based on JComboBoxes
	 * It allows attributing a single matcher to each element of selection, action, and input
	 * It does not allow one to add new value/matcher pairs for selection, action, or input
	 * 
	 * Note that the setDefaultValues method was written in mind that the Simple View would be
	 * the first view shown.  Default values are set here since the Exact Matcher is not in the same
	 * index in the default matcher arrays
	 * 
	 * @author wko2
	 */
	private class SimpleView extends MatcherView implements ActionListener
	{
		/* To keep track of which selection, action, and input we are currently at
		 * In between editing, these should reflect the appropriate JComboBox's getSelectedIndex() 
		 */
		private int[] currentChoices = {0, 0, 0};
		
		JPanel SAIPane;
		GridBagConstraints c; 
		
		final JComboBox selectionValuesCombo = createCombo(selectionValues, true, this, "selectionValuesCombo"),
				actionValuesCombo = createCombo(actionValues, true, this, "actionValuesCombo"),
				inputValuesCombo = createCombo(inputValues, true, this, "inputValuesCombo"); 
		JComboBox[] valuesCombos = {selectionValuesCombo, actionValuesCombo, inputValuesCombo};
		
		final JComboBox selectionMatchersCombo = createCombo(DEFAULT_SELECTION_MATCHERS, false, this, "selectionMatchersCombo"),
				actionMatchersCombo = createCombo(DEFAULT_ACTION_MATCHERS, false, this, "actionMatchersCombo"),
				inputMatchersCombo = createCombo(DEFAULT_INPUT_MATCHERS, false, this, "inputMatchersCombo");
		JComboBox[] matchersCombos = {selectionMatchersCombo, actionMatchersCombo, inputMatchersCombo};
		
		/**
		 * This constructor creates a view with an argument of whether to set default values
		 * @param initialize - true if we want to set default values to the matchers, false otherwise
		 */
		public SimpleView(boolean initialize)
		{
			super();
			
			SAIPane = new JPanel(new GridBagLayout());
			add(WindowUtils.wrapLeft(SAIPane)); //stick the gridbag into the box
			
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.ipadx = 5;
			c.ipady = 5;
			c.insets = new Insets(5, 5, 5, 5);
			
			c.gridx = 0;
			c.gridy = 0;
			SAIPane.add(new JLabel("Selection: "), c);
			c.gridx = 1;
			SAIPane.add(selectionValuesCombo, c);
			c.gridx = 2;
			SAIPane.add(selectionMatchersCombo, c);
			
			c.gridx = 0;
			c.gridy = 1;
			SAIPane.add(new JLabel("Action: "), c);
			c.gridx = 1;
			SAIPane.add(actionValuesCombo, c);
			c.gridx = 2;
			SAIPane.add(actionMatchersCombo, c);
			
			c.gridx = 0;
			c.gridy = 2;
			SAIPane.add(new JLabel("Input: "), c);
			c.gridx = 1;
			SAIPane.add(inputValuesCombo, c);
			c.gridx = 2;
			SAIPane.add(inputMatchersCombo, c);
			
			if(initialize)
			{
				setDefaultValues(selectionMatchersTypes, selectionMatchersCombo, 
						edgeData.getSelection().size());
				setDefaultValues(actionMatchersTypes, actionMatchersCombo,
						edgeData.getAction().size());
				setDefaultValues(inputMatchersTypes, inputMatchersCombo,
						edgeData.getInput().size());
			}
			
			updateView();
		}
		
		/**
		 * Sets default values (we don't usually update the display until update is called)
		 * @param matcherTypes - map of types of matchers
		 * @param matchersCombo - combo box for matchers
		 * @param length - total number of matchers we need
		 */
		private void setDefaultValues(Map<Integer, String> matcherTypes, JComboBox matchersCombo,
				int length)
		{
			//set the first one to Exact Match (note that the default arrays have different indices for the matchers)
			matchersCombo.setSelectedItem(Matcher.EXACT_MATCHER);
			matcherTypes.put(0, (String)matchersCombo.getSelectedItem());
			
			//set the rest to any match, -1 actually means no selection, see DEFAULT_MATCHER
			for(int vector = 1; vector < length; vector ++) //default everything else AnyMatch 
				matcherTypes.put(vector, DEFAULT_MATCHER);
			
			matcherTypes.put(-1, DEFAULT_MATCHER); //correspond unselected value with unselected matcher
		}
		
		
		/**
		 * Re-synchs with whatever happened in the other view
		 */
		public void updateView()
		{
			selectionMatchersCombo.setSelectedItem(selectionMatchersTypes.get(selectionValuesCombo.getSelectedIndex()));
			actionMatchersCombo.setSelectedItem(actionMatchersTypes.get(actionValuesCombo.getSelectedIndex()));
			inputMatchersCombo.setSelectedItem(inputMatchersTypes.get(inputValuesCombo.getSelectedIndex()));
			
			//put the actor stuff (back) in
			c.gridx = 0;
			c.gridy = 3;
			SAIPane.add(actorLabel, c);
			c.gridx = 1;
			SAIPane.add(actorBox, c);
			c.gridx = 2;
			SAIPane.add(triggerBox, c);
		}
		
		/* An update when creating new Simple View or switching from Complex View */
		public void setCurrentSelectedItems(int s, int a, int i)
		{
			currentChoices[SELECTION] = s;
			currentChoices[ACTION] = a;
			currentChoices[INPUT] = i;
			
			selectionValuesCombo.setSelectedIndex(s);
			actionValuesCombo.setSelectedIndex(a);
			inputValuesCombo.setSelectedIndex(i);
		}
		
		/* Listener on all combo boxes
		 * Note that for editable combo boxes, two actionEvents are sent when a field is edited
		 * One is for switching selectedIndex to -1 (so the new value is then selectedItem),
		 * the second is for switching selectedIndex back to its previous value
		 */
		public void actionPerformed(ActionEvent ae)
		{
			Object src = ae.getSource();
			String selC = selectionValuesCombo.getItemAt(0).toString();
			String selM = selectionMatchersCombo.getItemAt(0).toString();
			Vector selV = selectionValues;
			int s = currentChoices[SELECTION];
			
			if(src == selectionValuesCombo)
				updateVector(SELECTION);
			else if(src == actionValuesCombo)
				updateVector(ACTION);
			else if(src == inputValuesCombo)
				updateVector(INPUT);
			else if(src == selectionMatchersCombo)
				selectionMatchersTypes.put(currentChoices[SELECTION], (String)selectionMatchersCombo.getSelectedItem());
			else if(src == actionMatchersCombo)
				actionMatchersTypes.put(currentChoices[ACTION], (String)actionMatchersCombo.getSelectedItem());
			else if(src == inputMatchersCombo)
				inputMatchersTypes.put(currentChoices[INPUT], (String)inputMatchersCombo.getSelectedItem());
			else if(src instanceof ExpressionDialog)
			{
				/* Note that since the Expression dialog is modal, the current sai should not have changed
				 * Also, if we ever want to implement functions for selection or action, we'll need some way
				 * to know where it came from ... a field in the Expression dialog itself would work well
				 * For now, we assume it came from the input
				 */
				if(ae.getActionCommand().equals(ExpressionDialog.CANCEL))
					return;
				
				ExpressionMatcher em = new ExpressionMatcher();
				em.setInputExpression(((ExpressionDialog)src).getText());
		        em.setRelation(((ExpressionDialog)src).getRelation());
				inputMatchers.put(currentChoices[INPUT], em);
			}
		}
		
		/**
		 * Updates either s, a, or i values, types, and matchers from the given Combo boxes and map
		 * (We update the actual current choice in the callee since ints are by pass by value in Java)
		 */
		public void updateVector(int vector)
		{
			int newChoice = valuesCombos[vector].getSelectedIndex();
			
			if(newChoice == -1) //save the old selection value (this is usually from editing a field)
			{
				values[vector].set(currentChoices[vector], valuesCombos[vector].getSelectedItem().toString());
				return;
			}
			
			if(currentChoices[vector] != newChoice) //change to a new selection
			{
				currentChoices[vector] = newChoice;
				
				//update to new selection
				matchersCombos[vector].setSelectedItem(matchersTypes[vector].get(newChoice));
				return;
			}
		}
		
		/**
		 * Uploads all information in the view into the appropriate
		 * data structures (sai values vectors, sai matchers vectors)
		 * @param ed no updates unless arg == {@link VectorMatcherPanel#edgeData}
		 */
		public void updateInfo(EdgeData ed) {
			if(ed != edgeData)
				return;
			updateVector(SELECTION);
			updateVector(ACTION);
			updateVector(INPUT);
			
			SAIPane.remove(actorLabel);
			SAIPane.remove(actorBox);
			SAIPane.remove(triggerBox);
		}
	}
	
	/**
	 * This representation is based on a JTable
	 * It allows attributing a single matcher to each element of selection, action, and input
	 * Apparently CellEditors are assigned per Column, hence a private CellEditor class here
	 * see http://www.java2s.com/Code/Java/Swing-Components/EachRowwithdifferentEditorExample.htm
	 * @author wko2
	 */
	private class ComplexView extends MatcherView implements MouseListener, ActionListener
	{
		/* These pertain to the JTable */
		DefaultTableModel dtm;
		JTable table;
		EachRowEditor indexEditor, valueEditor, matcherEditor;
		
		/* For the popup menu */
		private JMenuItem insertAbove, insertBelow, remove;
		private final String[] COLUMN_HEADERS = {"Index", "Values", "Matchers"};
		
		private int INDEX_COL_WIDTH = 30;
		
		/* Indices for the selection, action, and input separators */
		int sIndex, aIndex, iIndex;
		
		/* For the actor */
		Box actor;
		
		/**
		 * This constructor creates a view with an argument of whether to set default values
		 * @param initialize - true if we want to set default values to the matchers, false otherwise
		 */
		public ComplexView(boolean initialize)
		{
			super();
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			dtm = new DefaultTableModel();
			dtm.setColumnIdentifiers(COLUMN_HEADERS);
			
			table = new JTable(dtm);
			
			/* Sets up the popup on a right click
			 * See http://forum.java.sun.com/thread.jspa?forumID=57&threadID=625217 */
			table.addMouseListener(this);
			
			//Set up the Editors for columns
			indexEditor = new EachRowEditor(table, null);
			valueEditor = new EachRowEditor(table, new DefaultCellEditor(new JTextField()));
			matcherEditor = new EachRowEditor(table, new DefaultCellEditor(new JTextField()));
			
			table.getColumn("Index").setCellEditor(indexEditor);
			table.getColumn("Values").setCellEditor(valueEditor);
		    table.getColumn("Matchers").setCellEditor(matcherEditor); //so long as the matcher column is named "Matchers"
		    
		    table.getColumnModel().getColumn(0).setPreferredWidth(INDEX_COL_WIDTH);
	        
		    table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		    
		    add(table);
		    add(Box.createVerticalStrut(5));
		    
		    actor = new Box(BoxLayout.X_AXIS);
		    actor.add(actorLabel);
		    actor.add(actorBox);
		    actor.add(triggerBox);
			add(WindowUtils.wrapLeft(actor));
			
		    if(initialize)
		    {
		    	//unimplemented, but we'd get the CellEditor from the appropriate cell, and provide default matchers
		    }
		    
		    //set it all up (data model, values) and the actor box
			updateView();
		    
			//JScrollPane scrollPane = new JScrollPane(table);
			//add(scrollPane);
		}
		
		private Vector getTitle(String name)
		{
			Vector row = new Vector();
			row.add("");
			row.add(name);
			row.add("");
			return row;
		}
		
		/**
		 * Converts from a row number to an index
		 * This causes indexing to start at 1
		 * @param rowNum - the row we are manipulating
		 * @return - its corresponding index number
		 */
		private int row2index(int rowNum)
		{
			if(rowNum < aIndex)
				return rowNum - sIndex;
			if(rowNum < iIndex)
				return rowNum - aIndex;
			return rowNum - iIndex;
		}
		
		/**
		 * Returns the group to which the row belongs, as an integer
		 * @param rowNum - row we are at
		 * @return - either SELECTION, ACTION, or INPUT
		 */
		private int row2type(int rowNum)
		{
			if(rowNum < aIndex)
				return SELECTION;
			if(rowNum < iIndex)
				return ACTION;
			return INPUT;
		}
		
		/**
		 * Re-synchs the view with any changes
		 */
		public void updateView()
		{
			int rowNum = 0;
			
			//clear it in an ugly way ... easier way would be to subclass, then clear dataVector and fireTableDataChanged
			for(int i = dtm.getRowCount() - 1; i >= 0; i --)
				dtm.removeRow(i);
			
			dtm.addRow(getTitle("Selections"));
			valueEditor.setEditorAt(rowNum, null);
			matcherEditor.setEditorAt(rowNum, null);
			sIndex = rowNum;
			rowNum ++;
			
			for(int i = 0; i < selectionValues.size(); i ++)
			{
				Vector row = new Vector();
				String matcherType = (String)selectionMatchersTypes.get(i);
				row.add("Sel " + row2index(rowNum));
				row.add(selectionValues.get(i));
				row.add(matcherType);
				dtm.addRow(row);
				JComboBox selCombo = createCombo(DEFAULT_SELECTION_MATCHERS, false, null, "sel " + i + " matcher combo");
				selCombo.setSelectedItem(matcherType);
				matcherEditor.setEditorAt(rowNum, new DefaultCellEditor(selCombo));
				rowNum ++;
			}
			
			dtm.addRow(getTitle("Actions"));
			valueEditor.setEditorAt(rowNum, null);
			matcherEditor.setEditorAt(rowNum, null);
			aIndex = rowNum;
			rowNum ++;
			
			dtm.addRow(getTitle("Inputs"));
			valueEditor.setEditorAt(rowNum, null);
			matcherEditor.setEditorAt(rowNum, null);
			sIndex = rowNum;
			rowNum ++;
			
			//actor stuff
			actor = new Box(BoxLayout.X_AXIS);
		    actor.add(actorLabel);
		    actor.add(actorBox);
		    actor.add(triggerBox);
			add(WindowUtils.wrapLeft(actor));
		}
		
		//inserts into row, pushing previous values to row + 1
		private void insertAboveRow(int row, String newValue, JComboBox matcherCombo)
		{
			for(int i = dtm.getRowCount(); i >= row + 1; i ++)
				matcherEditor.setEditorAt(i, matcherEditor.getEditorAt(i - 1));
			
			matcherEditor.setEditorAt(row, new DefaultCellEditor(matcherCombo));
			
			Vector<String> v = new Vector<String>();
			v.add(row2index(row) + "");
			v.add(newValue);
			v.add(matcherCombo.getSelectedItem().toString()); //if this is null, might have problems?
			dtm.insertRow(row, v);
		}
		
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		//see http://forum.java.sun.com/thread.jspa?forumID=57&threadID=625217
		public void mouseReleased(MouseEvent me) {
			if (SwingUtilities.isRightMouseButton(me))
			{
				JTable source = (JTable)me.getSource();
				int row = source.rowAtPoint(me.getPoint());
				int column = source.columnAtPoint(me.getPoint());
				source.changeSelection(row, column, false, false);
				JPopupMenu popup = new JPopupMenu("Choose an action");
				insertAbove = new JMenuItem("Insert above");
				insertAbove.addActionListener(this);
				popup.add(insertAbove);
				insertBelow = new JMenuItem("Insert below");
				insertBelow.addActionListener(this);
				popup.add(insertBelow);
				remove = new JMenuItem("Remove");
				remove.addActionListener(this);
				popup.add(remove);
				popup.show(me.getComponent(), me.getX(), me.getY());
			}
		}

		/**
		 * These don't work yet ...
		 */
		public void actionPerformed(ActionEvent ae) {
			Object src = ae.getSource();
			if(src == insertAbove)
				insertAboveRow(table.getSelectedRow(), "", createCombo(DEFAULT_MATCHERS[row2type(table.getSelectedRow())], true, this, "a good name goes here"));
			else if(src == insertBelow)
				insertAboveRow(table.getSelectedRow(), "", createCombo(DEFAULT_MATCHERS[row2type(table.getSelectedRow() + 1)], true, this, "and here as well"));
			else if(src == remove)
				dtm.removeRow(table.getSelectedRow());
		}
		
		/**
		 * Updates the maps
		 * @param ed no updates unless arg == {@link VectorMatcherPanel#edgeData}
		 */
		public void updateInfo(EdgeData ed) {
			
			//TODO: update the values and the matchersTypes
			if(ed != edgeData)
				return;
			actor.remove(actorLabel);
			actor.remove(actorBox);
			actor.remove(triggerBox);
			remove(actor);
		}
		
		/**
		 * each row TableCellEditor
		 * 
		 * @version 1.1 09/09/99
		 * @author Nobuo Tamemasa
		 * 
		 * Edited by Ko
		 * 		- Constructor allows setting the default editor
		 * 		- A null editor on a cell makes it uneditable (can use default to make all null)
		 * 		- A get editor on the cell
		 * 
		 * TODO: To be truly rigorous, we should catch any class cast exceptions ... but one caller should
		 * always check isCellEditable before making those calls anyway ...
		 */

		private class EachRowEditor implements TableCellEditor {
		  protected Hashtable editors;

		  protected Object editor, defaultEditor;

		  JTable table;
		  
		  private Object[] nullEditor = new Object[0]; //need this since Hashtable can't store null
		  
		  /**
		   * Constructs a EachRowEditor. create default editor
		   * 
		   * @see TableCellEditor
		   * @see DefaultCellEditor
		   */
		  public EachRowEditor(JTable table, Object defaultEditor) {
		    this.table = table;
		    editors = new Hashtable();
		    this.defaultEditor = defaultEditor;
		  }

		  /**
		   * @param row
		   *            table row
		   * @param editor
		   *            table cell editor
		   */
		  public void setEditorAt(int row, Object editor) {
			  if(editor == null)
				  editor = nullEditor;
			  editors.put(new Integer(row), editor);
		  }
		  
		  /**
		   * added by ko
		   */
		  public TableCellEditor getEditorAt(int row)
		  {
			  Object e = editors.get(new Integer(row));
			  if(e == null)
				  e = defaultEditor;
			  
			  if(e == nullEditor)
				  e = null;
			  
			  return (TableCellEditor)e;
		  }
		  
		  public Component getTableCellEditorComponent(JTable table, Object value,
		      boolean isSelected, int row, int column) {
		    //editor = (TableCellEditor)editors.get(new Integer(row));
		    //if (editor == null) {
		    //  editor = defaultEditor;
		    //}
		    return ((TableCellEditor)editor).getTableCellEditorComponent(table, value, isSelected,
		        row, column);
		  }

		  public Object getCellEditorValue() {
		    return ((TableCellEditor)editor).getCellEditorValue();
		  }

		  public boolean stopCellEditing() {
		    return ((TableCellEditor)editor).stopCellEditing();
		  }

		  public void cancelCellEditing() {
			  ((TableCellEditor)editor).cancelCellEditing();
		  }

		  public boolean isCellEditable(EventObject anEvent) {
		    selectEditor((MouseEvent) anEvent);
		    if(editor == null || editor == nullEditor)
		    	return false;
		    
		    return ((TableCellEditor)editor).isCellEditable(anEvent);
		  }

		  public void addCellEditorListener(CellEditorListener l) {
			  ((TableCellEditor)editor).addCellEditorListener(l);
		  }

		  public void removeCellEditorListener(CellEditorListener l) {
			  ((TableCellEditor)editor).removeCellEditorListener(l);
		  }

		  public boolean shouldSelectCell(EventObject anEvent) {
		    selectEditor((MouseEvent) anEvent);
		    return ((TableCellEditor)editor).shouldSelectCell(anEvent);
		  }

		  protected void selectEditor(MouseEvent e) {
		    int row;
		    if (e == null) {
		      row = table.getSelectionModel().getAnchorSelectionIndex();
		    } else {
		      row = table.rowAtPoint(e.getPoint());
		    }
		    editor = editors.get(new Integer(row));
		    if (editor == null) {
		      editor = defaultEditor;
		    }
		  }
		}
	}
	
	/**
	 * Creates a dialog that contains a simplified Expression Matcher Panel
	 * @author wko2
	 */
	private class ExpressionDialog extends JDialog implements ActionListener
	{
		/* Vars pertinent to the Expression dialog */
		private ExpressionMatcherPanel exprPanel;
		private JTextComponent syntaxCheck;
		private JButton checkButton, saveButton, cancelExprButton; //not to be confused with VectorMatcherPanel's cancel button
		
		private final Dimension SYNTAX_CHECK_DIM = new Dimension(300, LINE_HEIGHT * 5);
		
		final static String CHECK = "Check", SAVE = "Save", CANCEL = "Cancel";
		
		/* Parent so we can tell it we're done */
		ActionListener actionListener;
		
		/* Either selection, action, or input */
		public int vector;
		
		/* Concatenation matching or not */
		private boolean concat;
		
		/**
		 * Expression dialog constructor
		 * @param owner - the JDialog we are modal to
		 * @param listener - listener for when finished
		 * @param text - text to be initialized with
		 */
		public ExpressionDialog(JDialog owner, ActionListener listener, String text, int vector, boolean concat, EdgeData edgeData)
		{
			super(owner, "Create a function", true);
			setTitle("Expression Dialog");
			
			this.actionListener = listener;
			this.vector = vector;
			this.concat = concat; //need to know for the check on demonstrated values
			
			//to hold the expression matcher and its buttons
			Box b = new Box(BoxLayout.Y_AXIS);
			b.setBorder(new EmptyBorder(12, 12, 12, 12));
			
			exprPanel = new ExpressionMatcherPanel(edgeData, text); //so every Expression Dialog from this VMPanel instance will use the same edgeData
			exprPanel.setVisible(true);
			b.add(exprPanel);
	        b.add(Box.createVerticalStrut(5));
			
	        syntaxCheck = createTextArea("Syntax Check");
	        syntaxCheck.setBackground(getBackground());
	        syntaxCheck.setEditable(false);
	        exprPanel.setSyntaxCheck(syntaxCheck);
	        b.add(scrollerizeTextArea((JTextArea)syntaxCheck, SYNTAX_CHECK_DIM));
	        b.add(Box.createVerticalStrut(5));
	        
	        checkButton = new JButton(CHECK);
	        checkButton.addActionListener(this);
	        saveButton = new JButton(SAVE);
			saveButton.addActionListener(this);
	        cancelExprButton = new JButton(CANCEL);
	        cancelExprButton.addActionListener(this);
	        
	        Box b3 = new Box(BoxLayout.X_AXIS);
			b3.add(checkButton);
			b3.add(Box.createHorizontalStrut(12));
			b3.add(saveButton);
			b3.add(Box.createHorizontalStrut(12));
			b3.add(cancelExprButton);
			b3.add(Box.createHorizontalStrut(12));
			
			b.add(WindowUtils.wrapRight(b3));
			
			getContentPane().add(b);
			
			b.validate();
			
			pack();
	    	repaint();
			setVisible(true);
		}
		
		public String getText()
		{
			if(exprPanel == null)
				return null;
			return exprPanel.inputArea.getText();
		}
		
		public String getRelation()
		{
			if(exprPanel == null)
				return null;
			return exprPanel.relationOptions.getSelectedValue().toString();
		}
		
		/**
		 * Called when buttons are pressed in the Expression dialog
		 * We set the ExpressionMatcher and its appropriate fields
		 */
		public void actionPerformed(ActionEvent ae) {
			Object src = ae.getSource();
			
			if(src == checkButton) //check() will fill in the text area
			{
				exprPanel.checkDemonstratedValues(true, concat, vector, defaultSelectionVector,
						defaultActionVector, defaultInputVector);
				return;
			}
			actionListener.actionPerformed(new ActionEvent(this, -1, ((JButton)src).getText()));
			
			setVisible(false);
			dispose();
		}
	}
	
	/**
	 * Creates a dialog for a simplified Range Matcher Panel
	 * 
	 * @author wko2
	 *
	 */
	private class RangeDialog extends JDialog implements ActionListener
	{
		ActionListener actionListener;
		
		public int vector;

		JButton okButton, cancelRngButton;
		
		private static final String OK = "OK", CANCEL = "Cancel";
		
		private RangeMatcherPanel rmp;
		
		public RangeDialog(JDialog owner, ActionListener listener, String text, int vector)
		{
			super(owner, "Create a Range", true);
			actionListener = listener;
			this.vector = vector;
			
			Box b = new Box(BoxLayout.Y_AXIS);
			b.add((rmp = new RangeMatcherPanel(text)));
			b.add(Box.createVerticalStrut(5));
			
			okButton = new JButton(OK);
			okButton.addActionListener(this);
			cancelRngButton = new JButton(CANCEL);
	        cancelRngButton.addActionListener(this);
	        
	        Box b3 = new Box(BoxLayout.X_AXIS);
			b3.add(okButton);
			b3.add(Box.createHorizontalStrut(12));
			b3.add(cancelRngButton);
			b3.add(Box.createHorizontalStrut(12));
			b.add(WindowUtils.wrapRight(b3));
			b.add(Box.createVerticalStrut(5));
			
			getContentPane().add(b);
			
			pack();
			validate();
			setVisible(true);
		}
		
		public String getMin()
		{
			return rmp.minimumField.getText();
		}
		
		public String getMax()
		{
			return rmp.maximumField.getText();			
		}
		
		public void actionPerformed(ActionEvent ae) {
			Object src = ae.getSource();
			actionListener.actionPerformed(new ActionEvent(this, -1, ((JButton)src).getText()));
		}
	}
	
	/**
	 * Creates a dialog for a simplified Range Matcher Panel
	 * 
	 * @author wko2
	 *
	 */
	private class RegexDialog extends JDialog implements ActionListener
	{
		ActionListener actionListener;
		
		public int vector;

		JButton okButton, cancelRegexButton, validateButton;
		
		private static final String OK = "Save", CANCEL = "Cancel", VALIDATE = "Check";
		
		private JTextArea regexArea;
		private JTextArea validateArea;
		
		private boolean wild;
		
		private final Dimension STATUS_DIM = new Dimension(180, LINE_HEIGHT * 4);
		
		/**
		 * Constructor for a Regex or Wildcard matcher ... only difference is that in
		 * Wildcard matching, a * is replaced with .*
		 * @param owner
		 * @param listener
		 * @param text
		 * @param vector
		 * @param wildcard - true if is a wildcard matcher
		 */
		public RegexDialog(JDialog owner, ActionListener listener, String text, int vector, boolean wildcard)
		{
			super(owner, "Create a Regular Expression", true);
			actionListener = listener;
			this.vector = vector;
			this.wild = wildcard;
			
			Box b = new Box(BoxLayout.Y_AXIS);
			
			JLabel regexText = new JLabel("<html>"
					+ "Please insert a regular expression match<br>"
					+ "Greedy quantifiers include:<br>"
					+ "* - 0 or more<br>"
					+ "+ - 1 or more<br>"
					+ "? - 0 or 1<br><br>"
					+ "Character classes include:<br>"
					+ ". - any character<br>"
					+ "\\d - any digit<br>"
					+ "\\D - any non-digit<br>"
					+ "\\s - any white space<br>"
					+ "\\S(any non-white space)<br><br>"
					+ "For more on Java's regular expression usage,see<br>"
					+ "http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html<br><br></html>");
			
			JLabel wildcardText = new JLabel("<html>"
					+ "Use a * where you would like to insert a wildcard<br>"
					+ "Any number of characters can be matched there<br><br></html>");
			
			JPanel SAIPane = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.ipadx = 5;
			c.ipady = 5;
			c.insets = new Insets(5, 5, 5, 5);
			
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 2;
			SAIPane.add(wildcard ? wildcardText : regexText, c);
			
			c.gridwidth = 1;
			c.gridx = 0;
			c.gridy = 1;
			JLabel regexInputLabel = new JLabel("Regular expression:");
			SAIPane.add(regexInputLabel, c);
			
			regexArea = createTextArea("regexArea");
			regexArea.setText(text);
			c.gridx = 1;
			SAIPane.add(scrollerizeTextArea(regexArea, TEXT_AREA_DIM), c);
			
			c.gridx = 0;
			c.gridy = 2;
			JLabel checkStatusLabel = new JLabel("Check status:");
			SAIPane.add(checkStatusLabel, c);
			
			c.gridx = 1;
			validateArea = createTextArea("ValidateArea");
			validateArea.setBackground(getBackground());
			validateArea.setEditable(false);
			SAIPane.add(scrollerizeTextArea(validateArea, STATUS_DIM), c);
			
			b.add(SAIPane);
			b.add(Box.createVerticalStrut(5));
			
			okButton = new JButton(OK);
			okButton.addActionListener(this);
			cancelRegexButton = new JButton(CANCEL);
			cancelRegexButton.addActionListener(this);
			validateButton = new JButton(VALIDATE);
			validateButton.addActionListener(this);
			
	        Box b3 = new Box(BoxLayout.X_AXIS);
	        b3.add(validateButton);
	        b3.add(Box.createHorizontalStrut(12));
			b3.add(okButton);
			b3.add(Box.createHorizontalStrut(12));
			b3.add(cancelRegexButton);
			b3.add(Box.createHorizontalStrut(12));
			b.add(WindowUtils.wrapRight(b3));
			b.add(Box.createVerticalStrut(5));
			
			getContentPane().add(b);
			
			pack();
			validate();
			setVisible(true);
		}
		
		public String getRegex()
		{
			return regexArea.getText();
		}
		
		public boolean validateRegex()
		{
			//see getMatcher() under the regex and wildcard if
			String regex = regexArea.getText().replaceAll("\n", ""), demonstrated;
			if(wild)
				regex = WildcardMatcher.convertToFullRegex(regex);
			boolean againstDefault = false;
			try
			{
				demonstrated = getDemonstratedValue(vector);
				againstDefault = Pattern.compile(regex).matcher(demonstrated).matches();
			}
			catch(PatternSyntaxException pse)
			{
				validateArea.setText("Invalid pattern: \n" + regex);
				return false;
			}
			validateArea.setText((againstDefault ? "Matches to " : "Does not match to ") + "Demonstrated Value");
			if(!againstDefault)
				validateArea.append("\n" + regex + "\n" + demonstrated);
			return againstDefault;
		}
		
		public void actionPerformed(ActionEvent ae) {
			Object src = ae.getSource();
			
			if(src == validateButton)
				validateRegex();
			else
				actionListener.actionPerformed(new ActionEvent(this, -1, ((JButton)src).getText()));
		}
	}
	
	/** A renderer that permits Strings styled according to the capabilities of {@link JLabel}. */
	private static class StyledStringRenderer implements ListCellRenderer {
		
		/** The default renderer. */
		private final JLabel plainLabel;
		
		/** Renderer for bold text. */
		private final JLabel boldLabel;
		
		/**
		 * Sets {@link #plainFont} and {@link #boldFont} from plainLabel's font.
		 * @param plainLabel expected to be of type {@link JLabel}, to set {@link #plainLabel}
		 * @throws {@link IllegalArgumentException} if plainLabel not a JLabel instance
		 */
		StyledStringRenderer(ListCellRenderer delegate) {
			if(!(delegate instanceof JLabel))
				throw new IllegalArgumentException("Unexpected plainLabel class "+trace.nh(delegate)+
						" for StyledStringRenderer: should be JLabel");						
			 plainLabel = (JLabel) delegate;
			 Font plainFont = this.plainLabel.getFont();
			 boldLabel = new JLabel();
			 Font boldFont = new Font(plainFont.getFontName(), plainFont.getStyle() | Font.BOLD,
					 plainFont.getSize());
			 boldLabel.setFont(boldFont);
			 // FIXME other settings to borrow?
		}

		/**
		 * Call {@link ListCellRenderer#getListCellRendererComponent(JList, Object, int, boolean, boolean)}
		 * and then adjust the font before returning its result.
		 * @param list 
		 * @param value
		 * @param index
		 * @param isSelected
		 * @param cellHasFocus
		 * @return
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 * @throws {@link IllegalArgumentException} if result from plainLabel call is not a JLabel instance
		 */
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			Component result = ((ListCellRenderer) plainLabel).getListCellRendererComponent(list,
					value, index, isSelected, cellHasFocus);
			boolean shouldBold = ((list.getModel() instanceof ActionListModel) &&
					((ActionListModel) list.getModel()).isActionForSelection((String) value));
			if(trace.getDebugCode("editstudentinputrenderer"))
				trace.out("editstudentinputrenderer", "StyledStringRenderer.getListCellRendererComponent("+
						value+", "+index+") result "+trace.nh(plainLabel)+", shouldBold "+shouldBold+
						", listModel "+list.getModel());
			if(shouldBold && value != null) {
				boldLabel.setText(value.toString());
				boldLabel.setForeground(result.getForeground());
				boldLabel.setBackground(result.getBackground());
				result = boldLabel;
			}
			return result;
		}
	}

	/**
	 * Test whether any changes were made. If none, simulate the {@link #cancelButton} to exit.
	 * Else warn the user and simulate {@link #okButton} or {@link #cancelButton} or neither
	 * based on the response. 
	 */
	public void closeViaOkCancel() {
		EdgeData newEdgeData = commitChanges(previousEdgeData.cloneEdgeData());  // write junk edgeData
		if(EdgeData.sameSettings(previousEdgeData, newEdgeData))
			cancelButton.doClick();
		else {
			int ync = JOptionPane.showConfirmDialog(parent.getActiveWindow(),
					"You have changed some settings. Apply changes before closing?",
					"Edited Student Input Matching", JOptionPane.YES_NO_CANCEL_OPTION, 
					JOptionPane.WARNING_MESSAGE);
			if(JOptionPane.NO_OPTION == ync)
				cancelButton.doClick();
			else if(JOptionPane.YES_OPTION == ync)
				okButton.doClick();
			else
				;  // no-op: don't close the dialog; instead, continue editing
		}
	}

	/** Name of Java package with author- or CTAT-supplied matcher functions. */
	private static final String FUNCTIONS_PACKAGE =
			"edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions";

	/**
	 * List the public methods in the {@link String} and {@link Math} classes, plus those in
	 * the package {@value #FUNCTIONS_PACKAGE}.
	 * @return sorted list
	 */
	private static List<String> getFunctionList() {
		Set<String> sigSet = new HashSet<String>(); 
		sigSet.addAll(PackageEnumerator.getSelectedMethodSignatures(Math.class, true, "[Math] "));
		sigSet.addAll(PackageEnumerator.getSelectedMethodSignatures(String.class, true, "[String] "));
		for(Class<?> cls : PackageEnumerator.getClassesForPackage(FUNCTIONS_PACKAGE)) {
			if(!(cls.getSimpleName().contains("$")))         // exclude inner classes
				sigSet.addAll(PackageEnumerator.getSelectedMethodSignatures(cls, false, null));
		}
		List<String> result = new ArrayList<String>(sigSet);
		Collections.sort(result, String.CASE_INSENSITIVE_ORDER);
		return result;
	}
}
