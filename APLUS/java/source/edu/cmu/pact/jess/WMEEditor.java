package edu.cmu.pact.jess;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import jess.Context;
import jess.Deftemplate;
import jess.Fact;
import jess.FactIDValue;
import jess.Funcall;
import jess.JessException;
import jess.PrettyPrinter;
import jess.RU;
import jess.Rete;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.BehaviorRecorder.Dialogs.DialogUtilities;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.MessageEvent;
import edu.cmu.pact.Utilities.MessageEventListener;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.view.AbstractCtatWindow;
import edu.cmu.pact.ctatview.JHorizontalTable;

/**
 * The author tool for viewing and editing templates and facts in
 * working memory.
 * @author sanket
 */
public class WMEEditor implements MessageEventListener, PropertyChangeListener,
			TreeSelectionListener, ListSelectionListener {
	private static final String DISPLAY_FACT_NAMES = "Display Fact Names";
	private static final String DISPLAY_FACT_IDS = "Display Fact IDs";
	private static final String PROBLEM_FOLDER = "Problem Directory";
	/**
	 * Pattern to match slot definition lines in deftemplate pretty-print.
	 * Matches one slot definition on the line, where that definition includes a
	 * numeric type specifier. Why Jess can't print a symbolic type specifier I
	 * don't know.
	 */
	private static final Pattern slotTypePattern =
		Pattern.compile("(\\p{Space}*\\(slot )(.*)(\\(type\\p{Space}+)([1-9][0-9]*)\\)(\\)+)\\p{Space}*$");

	/**
	 * Pattern to match <Fact-NN> entries when modifying slot values.
	 */
	private static final Pattern factDisplayPattern =
		Pattern.compile("<[fF][aA][cC][tT]-([0-9]+)>");

	/**
	 * Problem name.
	 */
	private String problemName = "";

	JLabel type, slots, types, slotValues, value, description;
	JTree wmeTree;
	Deftemplate selectedTemplate;
	Fact selectedFact;
	JMenuItem addSlotMenu, deleteSlotMenu;
	JHorizontalTable slotTable;
	int selectedSlotNum;
	JLabel templateLabel, factLabel;
	JTextField templateField;
	SlotTableModel slotTableModel;
	JComboBox typeCbo;
	DefaultTreeModel wmeTreeModel;
	private MTRete r;
	/** Alternate Rete for 2-state display. */
	private MTRete preRete;
	private MTRete postRete;
	JCheckBox reactive;
	private WMEEditorPanel editorPanel;
	private final boolean setSize;
	private final boolean isWhyNot;

	/**
	 * Specialized TableModel to hold a {@link jess.Fact}. Columns are 
	 * slot name, type, value; rows are slots.
	 */
	class SlotTableModel extends DefaultTableModel
								 implements TableModelListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6552842063544081938L;
		/**
		 * Constants for the column types.
		 */
		final int SLOTNAME_COL = 0;
		final int SLOTTYPE_COL = 1;
		final int SLOTVALUE_COL = 2;

		/**
		 * Fact currently in model. Null when the model is displaying a
		 * deftemplate instead of a fact.
		 */
		private Fact fact = null;

		/**
		 * Original fact we're editing, if intent is {@link #MODIFY}.
		 */
		private Fact originalFact = null;

		/**
		 * Deftemplate currently in model.
		 */
		private Deftemplate dt = null;

		/**
		 * Constructor calls
		 * {@link javax.swing.table.DefaultTableModel#DefaultTableModel(Object[],int)}
		 * constructor.
		 *
		 * @param  columnNames names for the columns
		 * @param  rowCount initial count of rows
		 */
		SlotTableModel(String[] columnNames, int rowCount) {
			super(columnNames, rowCount);
			addTableModelListener(this);
		}

		/**
		 * Remove all data from the model. Removes all rows,
		 * clears {@link #fact}, {@link #dt}.
		 */
		void clear() {
			setRowCount(0);
			originalFact = null;
			fact = null;
			dt = null;
		}

		/**
		 * Override superclass method to make only Slot Value column editable.
		 *
		 * @param  row cell's row number
		 * @param  col cell's column number
		 * @return false for all cols if {@link #intent} is {@link #DISPLAY};
         *         otherwise false for columns [0..1], true for other columns
		 *             
		 */
		public boolean isCellEditable(int row,int col){
			return true;
		}

		/**
		 * Method called when this table model is updated. Ignores INSERT and
		 * DELETE events, which are generated when the display is redrawn.
		 * For UPDATE events, this method gets the changed value 
		 *
		 * @param  evt event information
		 */
		public void tableChanged(TableModelEvent evt) {

			if (evt.getType() != TableModelEvent.UPDATE)
				return;

			int row = evt.getFirstRow();
			int col = evt.getColumn();

			SlotTableModel stm = (SlotTableModel) evt.getSource();

			if (stm.dt == null) {
				if (trace.getDebugCode("wme"))
					trace.out("wme", "event source state bad on tableChanged:" +
						  ", fact " + stm.fact +
						  ", dt:\n " + stm.dt);
				return;
			}

			try {
				if(col == SLOTVALUE_COL && selectedFact != null){
					//changing the value of a slot in a particular fact
					if (stm.fact == null)
						stm.fact = new Fact(stm.dt);
	
					String slotName = (String) stm.getValueAt(row, SLOTNAME_COL);
					int slotIdx = stm.dt.getSlotIndex(slotName);
					if (slotIdx < 0) {
						throw new ArrayIndexOutOfBoundsException("Unknown slot name " +
								slotName + " in deftemplate:\n" +
							    stm.dt.toString());
					}
					int type = stm.dt.getSlotType(slotIdx); // RU.SLOT or MULTISLOT
					int dataType = stm.dt.getSlotDataType(slotIdx); // RU.ATOM, FACT, ...
					Object newValObj = stm.getValueAt(row, col);
					if (trace.getDebugCode("wme"))
						trace.out("wme", "tbl chg update dt " +
							  stm.dt.getBaseName() + ", slot " + slotName +
							  ": source " + stm + ", newValObj " +
							  newValObj.toString() + ";"); 
					
					mt.getRete().getEventLogger().log(true, AuthorActionLog.WORKING_MEMORY_EDITOR, 
							WMEEditor.EDIT_FACT, "Fact ID: " + selectedFact.getFactId()
							+ ", Fact name: " + selectedFact.getSlotValue("name")
							+ ", Slot name: " + selectedFact.getDeftemplate().getSlotName(selectedSlotNum),
							newValObj.toString(), "");									
					Value newVal = makeValue(type, dataType, newValObj);
					if (newVal == null) 
						return;
	
					stm.fact.setSlotValue(slotName, newVal); // fact yet unasserted
	
					modify(); //assert fact
				} else{
					//template changed somehow, write templates/facts out to strings
					StringWriter swt = new StringWriter();
					StringWriter swf = new StringWriter();
					String dft = null;
					//get default value for this slot, if we're lookinng at a template
					if(selectedFact == null && stm.getValueAt(row, SLOTVALUE_COL) != null) dft = stm.getValueAt(row, SLOTVALUE_COL).toString();

					//save out templates and facts, making the slot change in the process
					saveTemplatesChangeSlot(selectedTemplate, selectedTemplate.getSlotName(row), stm.getValueAt(row, SLOTNAME_COL).toString(), 
						stm.getValueAt(row, SLOTTYPE_COL).toString(), dft, swt);
					saveFactsAsJessCodeChangeSlot(selectedTemplate, selectedTemplate.getSlotName(row), stm.getValueAt(row, SLOTNAME_COL).toString(), 
						stm.getValueAt(row, SLOTTYPE_COL).toString(), r.listFacts(), swf);

					//clear rule engine and parse them back in
					try{
						r.clear();
						r.parse(new BufferedReader(new StringReader(swt.toString())));
						r.parse(new BufferedReader(new StringReader(swf.toString())));
					} catch(JessException je){
						if (trace.getDebugCode("wme"))
							trace.out("wme", je.toString());
					}
				}
				
			} catch (ArrayIndexOutOfBoundsException aie) {
				aie.printStackTrace();
			} catch (JessException je) {
				System.err.println("Error in slot table update, cause: " +
								   je.getCause());
				je.printStackTrace();
			}			
		}
		
		/**Tracks which facts you've recently visited.  For the "back" button*/
		private ArrayList<Fact> factHistory = new ArrayList<Fact>();
		private int currentHistoryIndex = -1;
		
		void goBackInHistory()
		{
			if (currentHistoryIndex == 0 || factHistory.isEmpty())
				return;

			currentHistoryIndex--;
			Fact factToLoad = (Fact)(factHistory.get(currentHistoryIndex));
			selectedFact = factToLoad;
			setSelectedTemplate(factToLoad.getDeftemplate());
			getPanel().refresh();
			
			mt.getRete().getEventLogger().log(true, AuthorActionLog.WORKING_MEMORY_EDITOR,
					"BACK", factToLoad.toString(), "", "");
		}
		
		void goForwardInHistory()
		{
			if (currentHistoryIndex >= factHistory.size() - 1 || factHistory.isEmpty())
				return;
			
			currentHistoryIndex++;
			Fact factToLoad = (Fact)(factHistory.get(currentHistoryIndex));
			selectedFact = factToLoad;
			setSelectedTemplate(factToLoad.getDeftemplate());
			getPanel().refresh();
			
			mt.getRete().getEventLogger().log(true, AuthorActionLog.WORKING_MEMORY_EDITOR,
					"FORWARD", factToLoad.toString(), "", "");
		}

		/**
		 * Insert the given fact into {@link #factHistory}, so that the Back and Forward keystrokes
		 * will retrieve it.
		 * @param fact fact to insert
		 */
		public void enterFactIntoHistory(Fact fact) {
			int oldIndex = currentHistoryIndex;
			int nRemoved = 0;
			if (factHistory.isEmpty() 
					|| !factHistory.get(currentHistoryIndex).equals(fact))
			{
				while ((factHistory.size() - 1) > currentHistoryIndex)
				{
					//cut off all of the list after the currentHistoryIndex
					factHistory.remove(factHistory.size() - 1);
					nRemoved++;
				}
				factHistory.add(fact);
				currentHistoryIndex++;
			}
			if(trace.getDebugCode("wme"))
				trace.outA("WMEEditor.SlotTableModel.enterFactIntoHistory():"+
						" index "+oldIndex+" => "+currentHistoryIndex+", nRemoved "+nRemoved+
						" new fact-"+fact.getFactId()+" "+fact.toStringWithParens());
		}

		/**
		 * Load all the slot values of a fact into this slotTable.
		 * Sets {@link #fact}, {@link #dt}, {@link #intent},
		 * {@link #originalFact}.
		 *
		 * @param  fact Fact to display or modify
		 * @param  toModify true means caller plans to modify the fact
		 */
		void loadFact(Fact fact) {

			Object[] rowData = new Object[3];
			String slotName = null;
			int i = 0;

			clear();              // remove all rows
			try {
				dt = fact.getDeftemplate();
				originalFact = fact;
				this.fact = new Fact(dt);
			} catch (JessException je) {
				String errMsg = "Jess error creating fact; cause: " +
					je.getCause();
				System.err.println(errMsg);
				je.printStackTrace();
			}
			try {
				for (i = 0; i < dt.getNSlots(); ++i) {
					slotName = dt.getSlotName(i);
					rowData[0] = slotName;
					Value val = fact.getSlotValue(slotName).resolveValue(null);
					this.fact.setSlotValue(slotName, val);
					rowData[1] = getSlotTypeString(dt, i);
					if (val.type() == RU.STRING && val.toString().length() > 1 && !val.toString().equals("nil"))
						rowData[2] = val.toString(); // .substring(1, val.toString().length()-1);
					else
						rowData[2] = val;

					addRow(rowData);
				}
			} catch (JessException je) {
				String errMsg = "Jess error on slot " + i + "; cause: " +
					je.getCause();
				System.err.println(errMsg);
				je.printStackTrace();
			}
			enterFactIntoHistory(fact);
			//TODO: insert logging call for "looking at a fact" here, if desired
		}

		/**
		 * Load all the slot definitions from a deftemplate into this
		 * slotTable. Nulls {@link #fact}, sets {@link #dt}, {@link #intent}.
		 *
		 * @param  dt Deftemplate for this fact
		 * @param  toCreate true means caller plans to create a fact;
		 *             false means just display a template
		 */
		void loadTemplate(Deftemplate dt) {
			Object[] data = new Object[3];

			clear();              // remove all rows
			this.dt = dt;
			this.fact = null;
			for (int i = 0; i < dt.getNSlots(); i++) {
				try {
					data[0] = dt.getSlotName(i);
					data[1] = getSlotTypeString(dt, i);
					data[2] = dt.getSlotDefault(i);
					if(dt.getSlotDataType(i) == RU.STRING && data[2].toString().length() > 1 && !data[2].toString().equals("nil"))
						data[2] = data[2].toString();  // .substring(1, data[2].toString().length()-1);
					if(data[2].equals("nil")) data[2] = "";
					addRow(data);
				} catch (JessException je) {
					System.err.println("Error on slot " + i + ": cause " +
									   je.getCause());
					je.printStackTrace();
					break;
				}
			}
		}

		/**
		 * Modify the {@link #originalFact} to match the slot values
		 * in {@link #fact}. Then copies {@link #originalFact} to
		 * {@link #fact} so that that field contains an asserted fact.
		 *
		 * @return the modified fact
		 * @exception JessException from {@link Rete.modify(Fact,String,Value)}
		 */
		private Fact modify() throws JessException {
			Fact result = null;
			String slotName = null;
			int i = 0;
			Value newVal = null;

			try {
				for (i = 0; i < dt.getNSlots(); i++) {
					slotName = dt.getSlotName(i);
					Value oldVal = originalFact.getSlotValue(slotName);
					newVal = fact.getSlotValue(slotName);
					if (!oldVal.equals(newVal)){
						if(trace.getDebugCode("mt")) trace.out("mt", "calling Rete.modify");
						result = r.modify(originalFact, slotName, newVal);
                                        }
				}
			} catch (JessException je) {
				throw new JessException("WMEEditor.SlotTableModel.modify()",
										je.getMessage() + " slotName " +
										slotName + ", new value \"" +
										newVal + "\"", je);
			}
			getPanel().refresh();
			return result;
		}
	}

	/**
	 * Specialized CellRenderer that displays empty templates as folders 
	 * rather than using the leaf icon used for facts.
	 */
	private class WMETreeCellRenderer extends DefaultTreeCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 983323185387528060L;
		protected Icon emptyTemplateIcon = closedIcon;
	    public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {

	    	super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus);
	    	if (leaf && isEmptyTemplate(value)) {
	    		setIcon(emptyTemplateIcon);
	    	} 
	    	return this;
	    }
	}
	
	/**
	 * Constant meaning no problem has been established yet.
	 */
	public static final int MTSTATE_NO_PROBLEM_YET = 0;

	/**
	 * Constant meaning that the start state facts are currently being loaded.
	 */
	public static final int MTSTATE_LOADING_PROBLEM = 1;

	/**
	 * Constant meaning that the model tracing is at the start state.
	 */
	public static final int MTSTATE_START_STATE = 2;

	/**
	 * Constant meaning student action to try model tracing has begun.
	 */
	public static final int MTSTATE_TRYING_MODEL_TRACE = 3;

	/**
	 * Constant meaning that a student action has been tried and at least one
	 * rule has fired.
	 */
	public static final int MTSTATE_MODEL_TRACING_BEGUN = 4;

	/**
	 * Names for the MT statuses. This array is indexed by the values
	 * {@link #mtState}.
	 */
	private static final String[] mtStateNames = {"No problem defined",
			"Loading problem definition", "In start state",
			"Trying first intermediate state", "Intermediate or done state"};

	/**
	 * Warnings about saving WM for each of the MT statuses. This array is
	 * indexed by the values {@link #mtState}.
	 */
	private static final String[] mtStateSaveWarnings = {
			"",
			"Warning: The system is loading the problem definition and working memory\n"
					+ " may be changing. This save may not capture a consistent state.",
			"If saved under the same name as the problem graph, the current facts will\n"
					+ " replace the starting state for this problem.",
			"Warning: The rule engine is active and working memory may be changing.\n"
					+ " This save may not capture a consistent state.",
			"Warning: The rule engine has changed working memory from the starting state.\n"
					+ " Do not save this working memory under the same name as the problem graph's."};

	/**
	 * Warnings about editing WM for each of the MT statuses. This array is
	 * indexed by the values {@link #mtState}.
	 */
	private static final String[] mtStateEditWarnings = {
			"Warning: your edits will be lost if you do not save them before loading\n"
					+ " a problem graph.",
			"Warning: The system is loading the problem definition and working memory\n"
					+ " may be changing.",
			"",
			"Warning: The rule engine is active and working memory may be changing.",
			""};

	/**
	 * The state in the model tracing engine, as far as this class knows it. One
	 * of {@link MTSTATE_TRYING_MODEL_TRACE},
	 * {@link MTSTATE_MODEL_TRACING_BEGUN},{@link MTSTATE_START_STATE},
	 * {@link MTSTATE_LOADING_PROBLEM},{@link MTSTATE_NO_PROBLEM_YET}.
	 */
	private int mtState = MTSTATE_NO_PROBLEM_YET;

	/**
	 * Count of the rules fired since the last (clear) or (reset).
	 */
	//private int nRulesFired = 0; // unused

	/**
	 * Type of the last message sent to or received from the student interface.
	 */
	//private String lastMsgType = null; // unused

	/**
	 * boolean indicating whether the user has created new wmeTypes and if so
	 * then prompt the user to save the deftemplates before resetting the
	 * working memory
	 */
	private boolean dirtyTypes = false;

	/**
	 * boolean indicating whether the user has creted new wme instances and if
	 * so then prompt the user to save the wme instances before resetting the
	 * Working memory
	 */
	private boolean dirtyInstances = false;
	/**
	 * Listener to get window events.
	 */
	//private WindowListener mainWindowListener = null; // unused

	/** Name of default templates file. */
	static final String wmeTypeFileName = "wmeTypes.clp";
	
	/** Name of default production rules file. */
	static final String rulesFileName = "productionRules.pr";

	/** Model tracer top-level object. */
	private MT mt;

	public static final String ADD_SLOT = "ADD_SLOT";
	public static final String DELETE_SLOT = "DELETE_SLOT";
	public static final String CANCEL_DELETE_TEMPLATE = "CANCEL_DELETE_TEMPLATE";
	public static final String RENAME_TEMPLATE = "RENAME_TEMPLATE";
	public static final String DELETE_TEMPLATE = "DELETE_TEMPLATE";
	public static final String EDIT_TEMPLATE = "EDIT_TEMPLATE";
	public static final String ADD_TEMPLATE = "ADD_TEMPLATE";
	public static final String DELETE_FACT = "DELETE_FACT";
	public static final String EDIT_FACT = "EDIT_FACT";
	//	FILTER_FACTS = "FILTER_FACTS",
							//	INSPECT_FACT = "INSPECT_FACT",
							//	INSPECT_TEMPLATE = "INSPECT_TEMPLATE",
	public static final String ADD_FACT = "ADD_FACT";
	public static final String SAVE_TEMPLATES = "SAVE_TEMPLATES";
	public static final String SAVE_FACTS = "SAVE_FACTS";
	static final String INSPECT_FACT = "INSPECT_FACT";

	private CTAT_Controller controller;
    private AbstractCtatWindow parentFrame;
    
    //private boolean shouldSetSize;

	/**
	 * Full constructor.
	 * @param rete      engine; if postRete defined, state before firing
	 * @param mt        controller
	 * @param postRete  engine state after firing rule
	 * @param doSetSize if true, set the size of the window
	 */
	public WMEEditor(MTRete rete, MT mt, MTRete postRete, boolean doSetSize,
			boolean isWhyNot) {
        this.controller = mt.getController();
        parentFrame = controller.getActiveWindow();
		this.mt = mt;
		this.setSize = doSetSize;
		this.isWhyNot = isWhyNot;
		setRete(rete); // register to get Jess events
		setPreRete(rete);
		setPostRete(postRete);
		
		controller.getPreferencesModel().addPropertyChangeListener(this);
		//this.shouldSetSize = doSetSize;


		wmeTreeModel = new DefaultTreeModel(new DefaultMutableTreeNode());
		wmeTree = new JTree(wmeTreeModel);
		wmeTree.setName("WME Tree");
		wmeTree.addTreeSelectionListener(this);
		wmeTree.setDragEnabled(true);
		wmeTree.setRootVisible(false);
		wmeTree.setBackground(new Color(240, 240, 240));	
		wmeTree.setShowsRootHandles(true);
		wmeTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		WMETreeCellRenderer renderer = new WMETreeCellRenderer();
		wmeTree.setCellRenderer(renderer);

		/**
		 * Column names for the slot table.
		 */
		String[] columnNames = {"Slot", "Type", "Slot Value"};
		
		typeCbo = new JComboBox();
		typeCbo.setName("WME Slot Type");
	//	typeCbo.addItem("atom");
	//	typeCbo.addItem("string");
	//	typeCbo.addItem("integer");
	//	typeCbo.addItem("float");
		typeCbo.addItem("slot");
		typeCbo.addItem("multislot");
	//	typeCbo.addItem("long");

		slotTableModel = new SlotTableModel(columnNames, 5);
		slotTable = new JHorizontalTable(slotTableModel);
		slotTable.setName("WME Slot Table");
		slotTable.setTransferHandler(new FactTransferHandler());
		slotTable.addKeyListener(getPanel());
		slotTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		slotTable.setCellSelectionEnabled(true);
		slotTable.getSelectionModel().addListSelectionListener(this);
		for (int i = 0; i < 3; i++) {
			TableColumn column = slotTable.getColumnModel().getColumn(i);

			if (i == 0) {
				column.setMinWidth(100);
			}
			if (i == 1) {
				column.setMinWidth(50);
			}
			if (i == 2) {
				column.setMinWidth(200);
			}
		}
		TableColumn tc = slotTable.getColumnModel().getColumn(1);
		tc.setCellEditor(new DefaultCellEditor(typeCbo));
		slotTable.setPreferredScrollableViewportSize(new Dimension(500, 110));
	}
	
	/*
	public void init() {
		
		
		
		

		validate();
		setVisible(true);
	}
	*/

	private void displayCognitiveModelFolder() {
		getPanel().displayCognitiveModelFolder();
	}

	void checkDirtyInstances() {
		// check to see if the user has created new instances if so then prompt
		// the user to save the state.
		if (dirtyInstances) {
			// prompt to save the instances
			int n = JOptionPane.showConfirmDialog(parentFrame,
					"You have modified working memory. Do you wish to save?\n",
					"Confirm", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (n == JOptionPane.YES_OPTION) {
				// make a call to save the instances
				saveFacts();
			}
			dirtyInstances = false;
		}
	}

	public void checkDirtyTypes() {
		// check to see if the user has created new types if so then prompt the
		// user to save the types.
		if (dirtyTypes) {
			// prompt to save the types
			int n = JOptionPane.showConfirmDialog(parentFrame,
					"You have modified the deftemplates.\n" +
					"Do you want to save the changes and make the new wme types\n" +
					"a part of the start state?\nOtherwise all changes that you" +
					"made will be undone.", "Confirm",
					JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
			if(n == JOptionPane.YES_OPTION){
				// make a call to save the types
				saveTemplates(true);
			}
			dirtyTypes = false;
		}
	}

	/**
	 * Connect this editor to a new instance of the rule engine.
	 * @param rete new engine instance
	 */
	void setRete(MTRete rete) {
		r = rete;
	}

	/**
	 * Sets the problem name {@link #problemName}. 
	 * 
	 * @param name
	 *            the problem name
	 */
	public void setProblemName(String name) {
		if(name == null)
			problemName = "";
		else
			problemName = name;
	}
    
	void removeTemplate(Deftemplate template){
		//remove a template by writing out all other templates and facts to strings
		StringWriter swt = new StringWriter();
		StringWriter swf = new StringWriter();
		saveTemplatesExcept(template, swt);
		saveFactsAsJessCodeExcept(template, r.listFacts(), swf);
		//parse them back in
		try{
			r.clear();
			r.parse(new BufferedReader(new StringReader(swt.toString())));
			r.parse(new BufferedReader(new StringReader(swf.toString())));
		} catch(JessException je){
			trace.errStack("removeTemplate("+template+")", je);
		}
	}

	void setSelection(Fact fact, Deftemplate template){
		//select a fact or template in the tree
		//all non-error branches trigger mouseReleased to update the rest of the interface
		if(fact == null && template == null){
			//nothing selected
			wmeTree.setSelectionPath(null);
			getPanel().mouseReleased((MouseEvent)null);
			return;
		}
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)wmeTreeModel.getRoot();
		String factID = "";
		try{
			if(fact != null) factID = new FactIDValue(fact).toString();
		} catch(JessException je){
			trace.errStack("setSelection("+factID+")", je);
			return;
		}
		String templateName = template.getBaseName();
		Enumeration templates = root.children();
		//find the node for this template/fact
		while(templates.hasMoreElements()){
			DefaultMutableTreeNode templateNode = (DefaultMutableTreeNode)templates.nextElement();
			if(templateNode.toString().equals(templateName)){
				TreePath selectionPath = new TreePath(wmeTreeModel.getPathToRoot(templateNode));
				if(fact != null){
					//find the node for this fact
					Enumeration facts = templateNode.children();
					while(facts.hasMoreElements()){
						DefaultMutableTreeNode factNode = (DefaultMutableTreeNode)facts.nextElement();
						if(factNode.toString().indexOf(factID) != -1){
							selectionPath = new TreePath(wmeTreeModel.getPathToRoot(factNode));
							break;
						}
					}
				}
				wmeTree.setSelectionPath(selectionPath);
				wmeTree.scrollPathToVisible(selectionPath);
				break;
			}
		}
		getPanel().mouseReleased((MouseEvent)null);
	}

	/**
	 * @param ae
	 */
	private void setShowFactNames(boolean show) {
/*		if (show) {
			listPanel.add(instanceNamePanel);
			nameIdLabelPanel.add(instanceLabel);
		} else {
			listPanel.remove(instanceNamePanel);
			nameIdLabelPanel.remove(instanceLabel);
		}
		this.validate();
*/	}

	/**
	 * @param ae
	 */
	private void setShowFactIDs(boolean show) {
/*		if (show == false) {
			listPanel.remove(instanceIdPanel);
			nameIdLabelPanel.remove(idLabel);
		} else {
			listPanel.add(instanceIdPanel);
			nameIdLabelPanel.add(idLabel);
		}
		this.validate();
*/	}

	/**
	 * Saves the wmeTypes.
	 * @param  prompt true means ask the user whether to save the templates
	 * @param  showLocation true means display the filename to which
	 *             the deftemplates were saved
	 * @return path of file written; null if save unsuccessful
	 */
	//public because it's now called from CtatMenuBar
	public String saveTemplates(boolean showLocation) {
		String path = mt.findCognitiveModelDirectory();
		File dir = new File(path);
		if (!dir.exists())
			dir = Utils.getFileAsResource(path, this);
		return saveTemplates(false, showLocation, dir);
	}
	
	/**
	 * Saves the wmeTypes.
	 * @param  prompt true means ask the user whether to save the templates
	 * @param  showLocation true means display the filename to which
	 *             the deftemplates were saved
	 * @param  dir directory in which to save; no-op if null
	 * @return path of file written; null if save unsuccessful
	 */
	private String saveTemplates(boolean prompt, boolean showLocation, File dir) {
		if (dir == null)
			return null;
		if (prompt) {
			int confirm = JOptionPane.showConfirmDialog(parentFrame,
					"Would you like also to save your deftemplates to file "+wmeTypeFileName+"?",
					"Save Jess Deftemplates?", JOptionPane.YES_NO_OPTION);
			if (confirm != JOptionPane.YES_OPTION)
				return null;
		}
		File f = new File(dir, wmeTypeFileName);
		Writer out = null;
		try {
			out = openFile(f);
			if (out == null)
				return null;
			boolean success = saveTemplates(out, r);
			if (success && showLocation) {
				JOptionPane.showMessageDialog(parentFrame,
						"Template definitions (deftemplates) saved to\n" + f,
						"Templates Saved.", JOptionPane.INFORMATION_MESSAGE);
			}
			return (success ? f.toString() : null);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(parentFrame,
					"Error writing file " + f + ":\n" + e, 
					"Exception.", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return null;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Saves the wmeTypes.
	 *
	 * @param w stream to get Jess deftemplate commands
	 * @param r Rete to dump
	 * @return true if write was successful
	 */
	private boolean saveTemplates(Writer w, Rete r) throws IOException {
			
		BufferedWriter out = null;
		try {
			out = (w instanceof BufferedWriter ?
				   (BufferedWriter) w : new BufferedWriter(w));

			Iterator it = r.listDeftemplates();
	
			
			while (it.hasNext()) {
				Deftemplate dt = (Deftemplate) it.next();


				
				
				// omit templates used by Jess internally
				//
				if (dt.getBaseName().startsWith("_")
						|| dt.getBaseName().equalsIgnoreCase("initial-fact")) {
					continue;
				}

				StringBuffer sb = new StringBuffer();
				String template = (new PrettyPrinter(dt)).toString();
				StringTokenizer tkzr = new StringTokenizer(template, "\n");
		
				while (tkzr.hasMoreTokens()) {
					String line = tkzr.nextToken();
					Matcher m = slotTypePattern.matcher(line);
					if (m.matches()) {
						sb.replace(0, sb.length(), m.group(1));
						sb.append(m.group(2));
						int typeNum = -1;
						try {
							typeNum = Integer.parseInt(m.group(4));
							String type = RU.getTypeName(typeNum);
							if (type == null) {
								if (trace.getDebugCode("mt"))
									trace.out("mt", "bad slot type number "	+ typeNum);
							} else {
								sb.append(m.group(3)); // "(type "
								sb.append(RU.getTypeName(typeNum));
								sb.append(')');
							}
						} catch (NumberFormatException nfe) {
							if (trace.getDebugCode("mt")) trace.out("mt", "regex should prevent this "
									+ typeNum);
						}
						sb.append(m.group(5));
						line = sb.toString();
					}
					out.write(line);
					out.newLine();
				}
				if (dt.getBackwardChaining()) {
					out.write("(do-backward-chaining " + dt.getBaseName()
									+ ")");
					out.newLine();
				}
				out.flush();
			}

			out.newLine();
			out.write("; tell productionRules file that templates have been parsed");
			out.newLine();
			out.write("(provide wmeTypes)");
			out.newLine();

			dirtyTypes = false;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {}
			}
		}
	}

	private boolean saveTemplatesExcept(Deftemplate deleted, Writer w) {

		//saves templates, skipping one
			
		BufferedWriter out = null;
		try {
			out = (w instanceof BufferedWriter ?
				   (BufferedWriter) w : new BufferedWriter(w));

			Iterator it = r.listDeftemplates();

			while (it.hasNext()) {
				Deftemplate dt = (Deftemplate) it.next();
				//skip if it matches the template passed in
				if(dt.getBaseName().equals(deleted.getBaseName())) continue;

				// omit templates used by Jess internally
				//
				if (dt.getBaseName().startsWith("_")
						|| dt.getBaseName().equalsIgnoreCase("initial-fact")) {
					continue;
				}

				StringBuffer sb = new StringBuffer();
				String template = (new PrettyPrinter(dt)).toString();
				StringTokenizer tkzr = new StringTokenizer(template, "\n");
				while (tkzr.hasMoreTokens()) {
					String line = tkzr.nextToken();
					Matcher m = slotTypePattern.matcher(line);
					if (m.matches()) {
						sb.replace(0, sb.length(), m.group(1));
						sb.append(m.group(2));
						int typeNum = -1;
						try {
							typeNum = Integer.parseInt(m.group(4));
							String type = RU.getTypeName(typeNum);
							if (type == null) {
								if (trace.getDebugCode("wme"))
									trace.out("wme", "bad slot type number " + typeNum);		
							} else {
								sb.append(m.group(3)); // "(type "
								sb.append(RU.getTypeName(typeNum));
								sb.append(')');
							}
						} catch (NumberFormatException nfe) {
							if (trace.getDebugCode("wme"))
								trace.out("wme", "regex should prevent this " + typeNum);
						}
						sb.append(m.group(5));
						line = sb.toString();
					}
					out.write(line);
					out.newLine();
				}
				if (dt.getBackwardChaining()) {
					out.write("(do-backward-chaining " + dt.getBaseName() + ")");
					out.newLine();
				}
				out.flush();
			}

			dirtyTypes = false;
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(parentFrame, "Error writing file.",
					"I/O Error.", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	boolean saveTemplatesRename(Deftemplate changed, String newName, Writer w) {
			
		//save templates, renaming one

		BufferedWriter out = null;
		try {
			out = (w instanceof BufferedWriter ?
				   (BufferedWriter) w : new BufferedWriter(w));

			Iterator it = r.listDeftemplates();

			while (it.hasNext()) {
				Deftemplate dt = (Deftemplate) it.next();

				// omit templates used by Jess internally
				//
				if (dt.getBaseName().startsWith("_")
						|| dt.getBaseName().equalsIgnoreCase("initial-fact")) {
					continue;
				}

				StringBuffer sb = new StringBuffer();
				String template = (new PrettyPrinter(dt)).toString();
				StringTokenizer tkzr = new StringTokenizer(template, "\n");
				int lineNum = 1;
				while (tkzr.hasMoreTokens()) {
					String line = tkzr.nextToken();
					//on the first line of the renamed template, make the substitution
					if(dt == changed && lineNum == 1){
						line = line.substring(0, line.indexOf(changed.getBaseName())) + newName + 
							line.substring(line.indexOf(changed.getBaseName()) + changed.getBaseName().length(), line.length() - 1);
					}
					Matcher m = slotTypePattern.matcher(line);
					if (m.matches()) {
						sb.replace(0, sb.length(), m.group(1));
						sb.append(m.group(2));
						int typeNum = -1;
						try {
							typeNum = Integer.parseInt(m.group(4));
							String type = RU.getTypeName(typeNum);
							if (type == null) {
								if (trace.getDebugCode("wme"))
									trace.out("wme", "bad slot type number "
										+ typeNum);
							} else {
								sb.append(m.group(3)); // "(type "
								sb.append(RU.getTypeName(typeNum));
								sb.append(')');
							}
						} catch (NumberFormatException nfe) {
							if (trace.getDebugCode("wme"))
								trace.out("wme", "regex should prevent this "
									+ typeNum);
						}
						sb.append(m.group(5));
						line = sb.toString();
					}
					out.write(line);
					out.newLine();
					lineNum++;
				}
				if (dt.getBackwardChaining()) {
					out.write("(do-backward-chaining " + dt.getBaseName()
									+ ")");
					out.newLine();
				}
				out.flush();
			}

			dirtyTypes = false;
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(parentFrame, "Error writing file.",
					"I/O Error.", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean saveTemplatesChangeSlot(Deftemplate changed, String oldSlotName, String newSlotName, String slotType, String dft, Writer w) {

		//save templates, changing a slot in one
			
		BufferedWriter out = null;
		try {
			out = (w instanceof BufferedWriter ?
				   (BufferedWriter) w : new BufferedWriter(w));

			Iterator it = r.listDeftemplates();

			while (it.hasNext()) {
				Deftemplate dt = (Deftemplate) it.next();

				// omit templates used by Jess internally
				//
				if (dt.getBaseName().startsWith("_")
						|| dt.getBaseName().equalsIgnoreCase("initial-fact")) {
					continue;
				}

				StringBuffer sb = new StringBuffer();
				String template = (new PrettyPrinter(dt)).toString();
				StringTokenizer tkzr = new StringTokenizer(template, "\n");
				while (tkzr.hasMoreTokens()) {
					String line = tkzr.nextToken();
					//set flag if this is the changed template and changed slot
					boolean changedLine = (dt.getBaseName().equals(changed.getBaseName()) && line.indexOf("slot " + oldSlotName) != -1);
					Matcher m = slotTypePattern.matcher(line);
					if (m.matches()) {
						sb.replace(0, sb.length(), m.group(1));
						sb.append(m.group(2));
						int typeNum = -1;
						try {
							typeNum = Integer.parseInt(m.group(4));
							String type = RU.getTypeName(typeNum);
							if (type == null) {
								if (trace.getDebugCode("wme"))
									trace.out("wme", "bad slot type number "
										+ typeNum);
							} else {
								sb.append(m.group(3)); // "(type "
								sb.append(RU.getTypeName(typeNum));
								sb.append(')');
							}
						} catch (NumberFormatException nfe) {
							if (trace.getDebugCode("wme"))
								trace.out("wme", "regex should prevent this "
									+ typeNum);
						}
						sb.append(m.group(5));
						line = sb.toString();
					}
					if(changedLine){
						String newLine;
						if(!slotType.equalsIgnoreCase("multislot")) newLine = "(slot " + newSlotName;
						else newLine = "(multislot " + newSlotName;
						//specify type if it's not "slot" or "multislot"
						if(!slotType.equalsIgnoreCase("slot") && !slotType.equalsIgnoreCase("multislot")) newLine += " (type " + slotType + ")";
						if(dft != null && dft.trim().length() > 0){
							//default value is specified
							if(!slotType.equalsIgnoreCase("multislot"))
								if(dft.indexOf(' ') != -1)
									//multislot changed to slot - retain first default value
									newLine += " (default " + dft.substring(0, dft.indexOf(' ')) + ")";
								else
									//normal slot
									newLine += " (default " + dft + ")";
							else
								//multislot
								newLine += " (default (create$ " + dft + "))";
						} else if(line.indexOf("(default ") != -1){
							//default used to be there, but it was removed by the user
							newLine += " " + line.substring(line.indexOf("(default "), line.indexOf(")", line.indexOf("(default ")));
						}
						//double parens if this is the last slot
						newLine += (tkzr.hasMoreTokens()) ? ")" : "))";
						line = newLine;
					}
					out.write(line);
					out.newLine();
				}
				if (dt.getBackwardChaining()) {
					out.write("(do-backward-chaining " + dt.getBaseName()
									+ ")");
					out.newLine();
				}
				out.flush();
			}

			dirtyTypes = false;
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(parentFrame, "Error writing file.",
					"I/O Error.", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	boolean saveTemplatesAddSlot(Deftemplate changed, Writer w) {

		//save templates, adding a slot to one
			
		BufferedWriter out = null;
		try {
			out = (w instanceof BufferedWriter ?
				   (BufferedWriter) w : new BufferedWriter(w));

			Iterator it = r.listDeftemplates();

			while (it.hasNext()) {
				Deftemplate dt = (Deftemplate) it.next();

				// omit templates used by Jess internally
				//
				if (dt.getBaseName().startsWith("_")
						|| dt.getBaseName().equalsIgnoreCase("initial-fact")) {
					continue;
				}

				StringBuffer sb = new StringBuffer();
				String template = (new PrettyPrinter(dt)).toString();
				//set flag if this is the changed template
				boolean addToThis = dt.getBaseName().equalsIgnoreCase(changed.getBaseName());
				String newSlotName = "newslot";
				int i = -1;
				StringTokenizer tkzr = new StringTokenizer(template, "\n");
				while (tkzr.hasMoreTokens()) {
					String line = tkzr.nextToken();
					//if we're adding a slot, but this slot already has the name we were going to use,
					//pick a different name for the new slot
					if(addToThis && line.indexOf(newSlotName) != -1){
						if(i == -1){
							newSlotName = newSlotName + "1";
							i = 1;
						}else{
							newSlotName = newSlotName.substring(0, newSlotName.length()-1) + i;
						}
						i++;
						if(i > 10) i -= 10;
					}
					Matcher m = slotTypePattern.matcher(line);
					if (m.matches()) {
						sb.replace(0, sb.length(), m.group(1));
						sb.append(m.group(2));
						int typeNum = -1;
						try {
							typeNum = Integer.parseInt(m.group(4));
							String type = RU.getTypeName(typeNum);
							if (type == null) {
								if (trace.getDebugCode("wme"))
									trace.out("wme", "bad slot type number "
										+ typeNum);
							} else {
								sb.append(m.group(3)); // "(type "
								sb.append(RU.getTypeName(typeNum));
								sb.append(')');
							}
						} catch (NumberFormatException nfe) {
							if (trace.getDebugCode("wme"))
								trace.out("wme", "regex should prevent this "
									+ typeNum);
						}
						sb.append(m.group(5));
						line = sb.toString();
					}
					if(addToThis && !tkzr.hasMoreTokens()){
						//back off by one to eliminate the end paren
						out.write(line.substring(0, line.length()-1));
						out.newLine();
						//add the new slot
						out.write("(slot " + newSlotName + "))");
						out.newLine();
					} else{
						out.write(line);
						out.newLine();
					}
				}
				if (dt.getBackwardChaining()) {
					out.write("(do-backward-chaining " + dt.getBaseName()
									+ ")");
					out.newLine();
				}
				out.flush();
			}

			dirtyTypes = false;
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(parentFrame, "Error writing file.",
					"I/O Error.", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	boolean saveTemplatesDeleteSlot(Deftemplate changed, String slotName, Writer w) {

		//save templates, deleting a slot from one
			
		BufferedWriter out = null;
		try {
			out = (w instanceof BufferedWriter ?
				   (BufferedWriter) w : new BufferedWriter(w));

			Iterator it = r.listDeftemplates();

			while (it.hasNext()) {
				Deftemplate dt = (Deftemplate) it.next();

				// omit templates used by Jess internally
				//
				if (dt.getBaseName().startsWith("_")
						|| dt.getBaseName().equalsIgnoreCase("initial-fact")) {
					continue;
				}

				StringBuffer sb = new StringBuffer();
				String template = (new PrettyPrinter(dt)).toString();
				//set flag if this is the changed template
				boolean deleteFromThis = dt.getBaseName().equalsIgnoreCase(changed.getBaseName());
				StringTokenizer tkzr = new StringTokenizer(template, "\n");
				while (tkzr.hasMoreTokens()) {
					String line = tkzr.nextToken();
					//skip this line if it's the slot we're deleting
					if(deleteFromThis && line.indexOf(slotName) != -1){
						if(!tkzr.hasMoreTokens()){
							out.write(")");
							out.newLine();
						}
						continue;
					}
					Matcher m = slotTypePattern.matcher(line);
					if (m.matches()) {
						sb.replace(0, sb.length(), m.group(1));
						sb.append(m.group(2));
						int typeNum = -1;
						try {
							typeNum = Integer.parseInt(m.group(4));
							String type = RU.getTypeName(typeNum);
							if (type == null) {
								if (trace.getDebugCode("wme"))
									trace.out("wme", "bad slot type number "
										+ typeNum);
							} else {
								sb.append(m.group(3)); // "(type "
								sb.append(RU.getTypeName(typeNum));
								sb.append(')');
							}
						} catch (NumberFormatException nfe) {
							if (trace.getDebugCode("wme"))
								trace.out("wme", "regex should prevent this "
									+ typeNum);
						}
						sb.append(m.group(5));
						line = sb.toString();
					}
					out.write(line);
					out.newLine();
				}
				if (dt.getBackwardChaining()) {
					out.write("(do-backward-chaining " + dt.getBaseName()
									+ ")");
					out.newLine();
				}
				out.flush();
			}

			dirtyTypes = false;
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(parentFrame, "Error writing file.",
					"I/O Error.", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Saves the instances to a file.
	 *
	 * @return filename of saved file; null if save unsuccessful
	 */
	public String saveFacts() {
		if (!mtStateSaveWarnings[mtState].equals("")) {
			int n = JOptionPane.showConfirmDialog(parentFrame,
					mtStateSaveWarnings[mtState], "Confirm before saving.",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if(n != JOptionPane.OK_OPTION)
				return null;
		}
		
		String path = mt.findCognitiveModelDirectory(); // default path is dir only

		File f = new File(path);

		File selectedFile = f;
		if (mtState == MTSTATE_START_STATE) // default file if in start state
		{
			String filename = null;
			if (mt != null)
				filename = mt.getProblemName() + ".wme";
			else if (problemName != null && problemName.length() > 0)
				filename = problemName + ".wme";
			else
				filename = controller.getProblemName().replaceFirst(".brd", ".wme");
			//dtasse added the "else" bit 7/19/06: it's sort of a hack, but I
			//was getting null pointer exceptions
			selectedFile = new File(path, filename);
		}
		if (trace.getDebugCode("wme"))
			trace.out("wme", "saveFacts() mtState " + mtState  + ", path" +
				  path + ", file f " + f + ", selectedFile " + selectedFile);

		f = DialogUtilities.chooseFile(selectedFile.getParent(), selectedFile.getName(),
				factFileFilter, "Save Facts", "Save", controller);
		if (f == null)
			return null;

		if (trace.getDebugCode("wme"))
			trace.out("wme", "saveFacts() chosen path " + f.getAbsoluteFile());

		// now save the instances
		boolean saved = saveFactsAsJessCode(r.listFacts(), f);
		if (saved) {
			dirtyInstances = false;
			return f.getPath();
		}
        ObjectOutputStream out = null;
        Iterator it;
        try {
        	out = new ObjectOutputStream(new FileOutputStream(f));
        	it = r.getWMEEditorFactsList().iterator();
        	while (it.hasNext()) {
        		out.writeObject(it.next());
        		out.flush();
        	}
        	dirtyInstances = false;
        	return f.getPath();
        } catch (FileNotFoundException e) {
        	JOptionPane.showMessageDialog(parentFrame, "File not found.",
        			"I/O Error.", JOptionPane.ERROR_MESSAGE);
        	e.printStackTrace();
        	return null;
        } catch (IOException e) {
        	JOptionPane.showMessageDialog(parentFrame, "Error reading file.",
        			"I/O Error.", JOptionPane.ERROR_MESSAGE);
        	e.printStackTrace();
        	return null;
        } finally {
        	if (out != null) {
        		try {
        			out.close();
        			it = null;
        			f = null;
        			out = null;
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        	}
        }
	}

	private boolean isEmptyTemplate(Object val) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)val;
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)wmeTreeModel.getRoot();
		//if the leaf node's parent is root, then node is a template with no facts
		if (root.isNodeChild(node)) {
			return true;
		}
		return false;
	}
	

	/**
	 * Method called when source notifies listener of event. Updates mtState.
	 * State machine:
	 * 
	 * <pre>
	 *  LispCheck:               MODEL_TRACING_BEGUN -&gt; (no change)
	 *                           (other states)      -&gt; TRYING_MODEL_TRACE
	 *  Go_To_WM_State:          (any state)         -&gt; MODEL_TRACING_BEGUN
	 *  LispCheckResult SUCCESS: (any state)         -&gt; MODEL_TRACING_BEGUN
	 *  LispCheckResult other:
	 *  ShowHintsMessage:        TRYING_MODEL_TRACE  -&gt; START_STATE
	 *                           (other states)      -&gt; (no change)
	 *  StartProblem:            (any state)         -&gt; LOADING_PROBLEM
	 *  StartStateComplete:      LOADING_PROBLEM     -&gt; START_STATE
	 *                           (other states)      -&gt; (error)
	 *  all other messages:      (no state change)
	 * </pre>
	 * 
	 * @param me
	 *            {@link edu.cmu.pact.Utilities.MessageEvent}containing the message sent or
	 *            received
	 */
	public void messageEventOccurred(MessageEvent me) {
		String msgType = me.getMessageType();
		String result = me.getResult();
		int oldState = mtState;

		if ("LISPCheck".equalsIgnoreCase(msgType)) {
			if (mtState != MTSTATE_MODEL_TRACING_BEGUN)
				mtState = MTSTATE_TRYING_MODEL_TRACE;
		}
		else if ("Go_To_WM_State".equalsIgnoreCase(msgType)) {
			mtState = MTSTATE_MODEL_TRACING_BEGUN;
		}
		else if ("LISPCheckResult".equalsIgnoreCase(msgType)) {
			if ("SUCCESS".equalsIgnoreCase(result))
				mtState = MTSTATE_MODEL_TRACING_BEGUN;
			else {
				if (mtState == MTSTATE_TRYING_MODEL_TRACE)
					mtState = MTSTATE_START_STATE;
			}
		} else if ("ShowHintsMessage".equalsIgnoreCase(msgType)) {
			if (mtState == MTSTATE_TRYING_MODEL_TRACE)
				mtState = MTSTATE_START_STATE;
		} else if ("StartProblem".equalsIgnoreCase(msgType))
			mtState = MTSTATE_LOADING_PROBLEM;
		else if ("StartStateComplete".equalsIgnoreCase(msgType)) {
			if (mtState != MTSTATE_LOADING_PROBLEM)
				trace.err("mtStateMachine error: StartStateComplete in state "
								+ mtStateNames[mtState]);
			mtState = MTSTATE_START_STATE;
		}

		//mtStateLabel.setText(mtStatePrefix + mtStateNames[mtState]);
		getPanel().refresh();
		if (trace.getDebugCode("wme")) trace.out("wme", "WMEEditor msg evt: msgType " + msgType
				+ ", result " + result + "\n   oldState "
				+ mtStateNames[oldState] + " -> " + mtStateNames[mtState]);
	}

	/**
	 * Open the given File for writing. If it or any part of its path doesn't
	 * exist, create it. Opens for overwrite if file exists.
	 * 
	 * @param  f File to open
	 * @return Writer on file; null if open fails
	 */
	private FileWriter openFile(File f) {
		FileWriter result = null;
		try {
			if (!f.exists()) {
				File parentFile = f.getParentFile();
				parentFile.mkdirs();
				f.createNewFile();
			}
			result = new FileWriter(f, false);
			return result;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(parentFrame, "Error opening file " +
										  f.getName()+ ":\n" + e, "I/O Error.",
										  JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Save working memory in a Jess- and human-readable form. Writes a series
	 * of Jess statements to a file which will assert the given facts.
	 * 
	 * @param factsIt
	 *            iterator comprising the Facts to save
	 * @param file
	 *            file to save to; will create (including entire path) if
	 *            doesn't exist
	 * @return result of {@link #saveFactsAsJessCode(Iterator,Writer)}
	 */
	public boolean saveFactsAsJessCode(Iterator factsIt, File file) {

		PrintWriter out = new PrintWriter(openFile(file));
		if (out == null)
			return false;
		boolean result = saveFactsAsJessCode(factsIt, out);
		out.close();
		return result;
	}

	/**
	* Sets the fact selected in the WME Editor.
	* @param fact the fact to set as the selected fact
	*/
	// unused?
	/*
	private void setSelectedFact2(Fact fact){
		selectedFact = fact;
		selectedTemplate = fact.getDeftemplate();
		setSelection(selectedFact, selectedTemplate);
	}
	*/

	/**
	 * Save working memory in a Jess- and human-readable form. Writes a series
	 * of Jess statements to a file which will assert the given facts.
	 * JS: I had to include values for slots named "name" in the assert statements.  Else
	* Jess would complain of duplicate facts as I asserted multiple instances
	* of columns or cells in a column or, for that matter, multiple
	* CommTextFields, etc. That is, since Jess refuses to assert a fact
	* that's identical (same deftemplate, same slot values) to an existing
	* asserted fact, we print
	*    (bind ?var1 (assert(cell (name "table1_C1R1"))))
	*    (bind ?var2 (assert(cell (name "table1_C1R2"))))
	* because we can't say simply
	*    (bind ?var1 (assert(cell)))
	*    (bind ?var2 (assert(cell)))
	* This is a serious limitation on the general case:  it means this
	* algorithm only works when facts built from the same deftemplate can be
	* uniquely distinguished solely by a name slot (in the general case, of
	* course, no fact has to have a name slot).  It's our convention that most
	* of our facts are student interface elements and those all have name
	* slots.
	 * @param factsIt iterator comprising the Facts to save; if null
	 *         gets iterator from {@link jess.Rete#listFacts()};
	 * @param w stream to save to
	 * @return true if successful; reasons for failure may include i/o errors,
	 *         Jess errors or the presence of slot values of type
	 *         EXTERNAL_ADDRESS, which are references to Java objects
	 */
	boolean saveFactsAsJessCode(Iterator factsIt, Writer w) {

		String extRefErrMsg = "One or more facts "
				+ "in the working memory contains a reference to an "
				+ "external object as a slot value.\n Hence the facts"
				+ "cannot be stored in a text file.";

		PrintWriter out = null;
		try {
			out = (w instanceof PrintWriter ?
				   (PrintWriter) w : new PrintWriter( w ) );
			if (out == null)
				return false;
			
			out.println(";; for Eclipse, etc., specify templates to read");
			out.println("(require* wmeTypes \"wmeTypes.clp\")");
			out.println("");

			Context context = r.getGlobalContext();

			class FactVar { // bundle a Fact and a variable name
				Fact f;
				String v;
				FactVar(Fact f, int i) {
					this.f = f;
					this.v = "?var" + i;
				}
			}
			Map<Integer, FactVar> map = new LinkedHashMap<Integer, FactVar>();   // keys fact-ids, values FactVars

			if (factsIt == null)        // default is all facts in Rete engine
				factsIt = r.listFacts();

			for (int i = 1; factsIt.hasNext(); i++) {
				Fact f = (Fact) factsIt.next();
				map.put(new Integer(f.getFactId()), new FactVar(f, i));
			}

			out.println(";;;; Fact assertions: slot assignments are below.");
			out.println("");
			for (Iterator<FactVar> it = map.values().iterator(); it.hasNext();) {
				FactVar fv = (FactVar) it.next();
				String nameAssgnmt = "";
				int n = fv.f.getDeftemplate().getSlotIndex("name");
				if (n >= 0) { // use name to make assertion unique
					Value nameVal = fv.f.getSlotValue("name");
					nameAssgnmt = " (name " + nameVal.resolveValue(context)
							+ ")";
				}
				out.println("(bind " + fv.v + " (assert(" + fv.f.getName()
						+ nameAssgnmt + ")))");
			}

			out.println(); // (modify ...) to set each slot
			out.println(";;;; Slot assignments");
			out.println();
			for (Iterator<FactVar> it = map.values().iterator(); it.hasNext();) {
				FactVar fv = (FactVar) it.next();
				Deftemplate dt = fv.f.getDeftemplate();
				int nSlots = dt.getNSlots();
				if (nSlots < 1) // no slots to assign=>no modify
					continue;
				out.println("; " + fv.f.getName());
				out.println("(modify " + fv.v);
				for (int i = 0; i < nSlots; i++) {
					String slotName = dt.getSlotName(i);
					Value val = fv.f.getSlotValue(slotName).resolveValue(
							context);
					out.print("    (" + slotName);
					switch (val.type()) {
					case RU.FACT :
						{
							Fact valFact = val.factValue(context);
							FactVar valFV = (FactVar) map.get(new Integer(
									valFact.getFactId()));
							out.print(" " + valFV.v);
						}
						break;
					case RU.LIST :
						{
							ValueVector vv = val.listValue(context);
							for (int j = 0; j < vv.size(); j++) {
								Value listVal = vv.get(j).resolveValue(context);
								if (listVal.type() == RU.FACT) {
									Fact valFact = listVal.factValue(context);
									FactVar valFV = (FactVar) map
											.get(new Integer(valFact
													.getFactId()));
									out.print(" " + valFV.v);
								} else if (listVal.type() == RU.EXTERNAL_ADDRESS) {
									JOptionPane.showMessageDialog(parentFrame,
											extRefErrMsg, "ERROR",
											JOptionPane.ERROR_MESSAGE);
								} else {
									out.print(" " + listVal.toString());
								}
							}
						}
						break;
					case RU.EXTERNAL_ADDRESS :
						JOptionPane.showMessageDialog(parentFrame, extRefErrMsg,
								"ERROR", JOptionPane.ERROR_MESSAGE);
						return false;
					default :
					{
						String valString = val.toString();
						if ((valString.length() < 3) && isOperator(valString))	
							out.print(" " + "\"" + val.toString() + "\"");
						else out.print(" " + val.toString());
					}
						break;
					}
					out.println(")"); // end slot
				}
				out.println(")"); // end modify
			}
			return true;
		} catch (JessException je) {
			JOptionPane.showMessageDialog(parentFrame, "Jess error while saving: "
					+ je + " Cause: " + je.getCause(), "Jess Error.",
					JOptionPane.ERROR_MESSAGE);
			je.printStackTrace();
			return false;
		} finally {
			out.close();
		}
	}
	
	private boolean isOperator(String str)
	{
		boolean blnNumericAlpha = false;

		
		char chr[] = null;
		if(str != null)
			chr = str.toCharArray();
	 
		for(int i=0; i<chr.length; i++)
		{
			if((chr[i] >= '0' && chr[i] <= '9') || 
			   (chr[i] >= 'A' && chr[i] <= 'Z') || 
			   (chr[i] >= 'a' && chr[i] <= 'z'))
			{
				blnNumericAlpha = true;
				break;
			}
			
		}
		return (!blnNumericAlpha);
	}
	private boolean saveFactsAsJessCodeExcept(Deftemplate deleted, Iterator factsIt, Writer w) {

		//save facts, except for those derived from a given template

		String extRefErrMsg = "One or more facts "
				+ "in the working memory contains a reference to an "
				+ "external object as a slot value.\n Hence the facts"
				+ "cannot be stored in a text file.";

		PrintWriter out = null;
		try {
			out = (w instanceof PrintWriter ?
				   (PrintWriter) w : new PrintWriter( w ) );
			if (out == null)
				return false;

			Context context = r.getGlobalContext();

			class FactVar { // bundle a Fact and a variable name
				Fact f;
				String v;
				FactVar(Fact f, int i) {
					this.f = f;
					this.v = "?var" + i;
				}
			}
			Map<Integer, FactVar> map = new LinkedHashMap<Integer, FactVar>();   // keys fact-ids, values FactVars

			if (factsIt == null)        // default is all facts in Rete engine
				factsIt = r.listFacts();

			for (int i = 1; factsIt.hasNext(); i++) {
				Fact f = (Fact) factsIt.next();
				//skip if it's the deleted template
				if(f.getDeftemplate().getBaseName().equals(deleted.getBaseName())) continue;
				map.put(new Integer(f.getFactId()), new FactVar(f, i));
			}

			out.println(";;;; Fact assertions: slot assignments are below.");
			out.println("");
			for (Iterator<FactVar> it = map.values().iterator(); it.hasNext();) {
				FactVar fv = (FactVar) it.next();
				String nameAssgnmt = "";
				int n = fv.f.getDeftemplate().getSlotIndex("name");
				if (n >= 0) { // use name to make assertion unique
					Value nameVal = fv.f.getSlotValue("name");
					nameAssgnmt = " (name " + nameVal.resolveValue(context)
							+ ")";
				}
				out.println("(bind " + fv.v + " (assert(" + fv.f.getName()
						+ nameAssgnmt + ")))");
			}

			out.println(); // (modify ...) to set each slot
			out.println(";;;; Slot assignments");
			out.println();
			for (Iterator<FactVar> it = map.values().iterator(); it.hasNext();) {
				FactVar fv = (FactVar) it.next();
				Deftemplate dt = fv.f.getDeftemplate();
				int nSlots = dt.getNSlots();
				if (nSlots < 1) // no slots to assign=>no modify
					continue;
				out.println("; " + fv.f.getName());
				out.println("(modify " + fv.v);
				for (int i = 0; i < nSlots; i++) {
					String slotName = dt.getSlotName(i);
					Value val = fv.f.getSlotValue(slotName).resolveValue(
							context);
					out.print("    (" + slotName);
					switch (val.type()) {
					case RU.FACT :
						{
							Fact valFact = val.factValue(context);
							FactVar valFV = (FactVar) map.get(new Integer(
									valFact.getFactId()));
							if(valFV == null)
								out.print(" nil");
							else
								out.print(" " + valFV.v);
						}
						break;
					case RU.LIST :
						{
							ValueVector vv = val.listValue(context);
							for (int j = 0; j < vv.size(); j++) {
								Value listVal = vv.get(j).resolveValue(context);
								if (listVal.type() == RU.FACT) {
									Fact valFact = listVal.factValue(context);
									FactVar valFV = (FactVar) map
											.get(new Integer(valFact
													.getFactId()));
									if(valFV != null)
										out.print(" " + valFV.v);
								} else if (listVal.type() == RU.EXTERNAL_ADDRESS) {
									JOptionPane.showMessageDialog(parentFrame,
											extRefErrMsg, "ERROR",
											JOptionPane.ERROR_MESSAGE);
								} else {
									out.print(" " + listVal.toString());
								}
							}
						}
						break;
					case RU.EXTERNAL_ADDRESS :
						JOptionPane.showMessageDialog(parentFrame, extRefErrMsg,
								"ERROR", JOptionPane.ERROR_MESSAGE);
						return false;
					default :
						out.print(" " + val.toString());
						break;
					}
					out.println(")"); // end slot
				}
				out.println(")"); // end modify
			}
			return true;
		} catch (JessException je) {
			JOptionPane.showMessageDialog(parentFrame, "Jess error while saving: "
					+ je + " Cause: " + je.getCause(), "Jess Error.",
					JOptionPane.ERROR_MESSAGE);
			je.printStackTrace();
			return false;
		} finally {
			out.close();
		}
	}

	boolean saveFactsAsJessCodeRename(Deftemplate changed, String newName, Iterator factsIt, Writer w) {

		//save facts, changing references to a given template

		String extRefErrMsg = "One or more facts "
				+ "in the working memory contains a reference to an "
				+ "external object as a slot value.\n Hence the facts"
				+ "cannot be stored in a text file.";

		PrintWriter out = null;
		try {
			out = (w instanceof PrintWriter ?
				   (PrintWriter) w : new PrintWriter( w ) );
			if (out == null)
				return false;

			Context context = r.getGlobalContext();

			class FactVar { // bundle a Fact and a variable name
				Fact f;
				String v;
				FactVar(Fact f, int i) {
					this.f = f;
					this.v = "?var" + i;
				}
			}
			Map<Integer, FactVar> map = new LinkedHashMap<Integer, FactVar>();   // keys fact-ids, values FactVars

			if (factsIt == null)        // default is all facts in Rete engine
				factsIt = r.listFacts();

			for (int i = 1; factsIt.hasNext(); i++) {
				Fact f = (Fact) factsIt.next();
				map.put(new Integer(f.getFactId()), new FactVar(f, i));
			}

			out.println(";;;; Fact assertions: slot assignments are below.");
			out.println("");
			for (Iterator<FactVar> it = map.values().iterator(); it.hasNext();) {
				FactVar fv = (FactVar) it.next();
				String nameAssgnmt = "";
				int n = fv.f.getDeftemplate().getSlotIndex("name");
				if (n >= 0) { // use name to make assertion unique
					Value nameVal = fv.f.getSlotValue("name");
					nameAssgnmt = " (name " + nameVal.resolveValue(context)
							+ ")";
				}
				if(fv.f.getDeftemplate() == changed){
					//renamed template
					out.println("(bind " + fv.v + " (assert(" + fv.f.getName().substring(0, fv.f.getName().indexOf(fv.f.getDeftemplate().getBaseName())) + newName
							+ nameAssgnmt + ")))");
				} else{
					out.println("(bind " + fv.v + " (assert(" + fv.f.getName()
							+ nameAssgnmt + ")))");
				}
			}

			out.println(); // (modify ...) to set each slot
			out.println(";;;; Slot assignments");
			out.println();
			for (Iterator<FactVar> it = map.values().iterator(); it.hasNext();) {
				FactVar fv = (FactVar) it.next();
				Deftemplate dt = fv.f.getDeftemplate();
				int nSlots = dt.getNSlots();
				if (nSlots < 1) // no slots to assign=>no modify
					continue;
				out.println("; " + fv.f.getName());
				out.println("(modify " + fv.v);
				for (int i = 0; i < nSlots; i++) {
					String slotName = dt.getSlotName(i);
					Value val = fv.f.getSlotValue(slotName).resolveValue(
							context);
					out.print("    (" + slotName);
					switch (val.type()) {
					case RU.FACT :
						{
							Fact valFact = val.factValue(context);
							FactVar valFV = (FactVar) map.get(new Integer(
									valFact.getFactId()));
							out.print(" " + valFV.v);
						}
						break;
					case RU.LIST :
						{
							ValueVector vv = val.listValue(context);
							for (int j = 0; j < vv.size(); j++) {
								Value listVal = vv.get(j).resolveValue(context);
								if (listVal.type() == RU.FACT) {
									Fact valFact = listVal.factValue(context);
									FactVar valFV = (FactVar) map
											.get(new Integer(valFact
													.getFactId()));
									out.print(" " + valFV.v);
								} else if (listVal.type() == RU.EXTERNAL_ADDRESS) {
									JOptionPane.showMessageDialog(parentFrame,
											extRefErrMsg, "ERROR",
											JOptionPane.ERROR_MESSAGE);
								} else {
									out.print(" " + listVal.toString());
								}
							}
						}
						break;
					case RU.EXTERNAL_ADDRESS :
						JOptionPane.showMessageDialog(parentFrame, extRefErrMsg,
								"ERROR", JOptionPane.ERROR_MESSAGE);
						return false;
					default :
						out.print(" " + val.toString());
						break;
					}
					out.println(")"); // end slot
				}
				out.println(")"); // end modify
			}
			return true;
		} catch (JessException je) {
			JOptionPane.showMessageDialog(parentFrame, "Jess error while saving: "
					+ je + " Cause: " + je.getCause(), "Jess Error.",
					JOptionPane.ERROR_MESSAGE);
			je.printStackTrace();
			return false;
		} finally {
			out.close();
		}
	}

	private boolean saveFactsAsJessCodeChangeSlot(Deftemplate changed, String oldSlotName, String newSlotName, String slotType, Iterator factsIt, Writer w) {

		//save facts, changing some aspect of one slot for facts derived from one template

		String extRefErrMsg = "One or more facts "
				+ "in the working memory contains a reference to an "
				+ "external object as a slot value.\n Hence the facts"
				+ "cannot be stored in a text file.";

		PrintWriter out = null;
		try {
			out = (w instanceof PrintWriter ?
				   (PrintWriter) w : new PrintWriter( w ) );
			if (out == null)
				return false;

			Context context = r.getGlobalContext();

			class FactVar { // bundle a Fact and a variable name
				Fact f;
				String v;
				FactVar(Fact f, int i) {
					this.f = f;
					this.v = "?var" + i;
				}
			}
			Map<Integer, FactVar> map = new LinkedHashMap<Integer, FactVar>();   // keys fact-ids, values FactVars

			if (factsIt == null)        // default is all facts in Rete engine
				factsIt = r.listFacts();

			for (int i = 1; factsIt.hasNext(); i++) {
				Fact f = (Fact) factsIt.next();
				map.put(new Integer(f.getFactId()), new FactVar(f, i));
			}

			out.println(";;;; Fact assertions: slot assignments are below.");
			out.println("");
			for (Iterator<FactVar> it = map.values().iterator(); it.hasNext();) {
				FactVar fv = (FactVar) it.next();
				String nameAssgnmt = "";
				int n = fv.f.getDeftemplate().getSlotIndex("name");
				if (n >= 0) { // use name to make assertion unique
					Value nameVal = fv.f.getSlotValue("name");
					nameAssgnmt = " (name " + nameVal.resolveValue(context)
							+ ")";
				}
				out.println("(bind " + fv.v + " (assert(" + fv.f.getName()
						+ nameAssgnmt + ")))");
			}

			out.println(); // (modify ...) to set each slot
			out.println(";;;; Slot assignments");
			out.println();
			for (Iterator<FactVar> it = map.values().iterator(); it.hasNext();) {
				FactVar fv = (FactVar) it.next();
				Deftemplate dt = fv.f.getDeftemplate();
				int nSlots = dt.getNSlots();
				if (nSlots < 1) // no slots to assign=>no modify
					continue;
				out.println("; " + fv.f.getName());
				out.println("(modify " + fv.v);
				for (int i = 0; i < nSlots; i++) {
					String slotName = dt.getSlotName(i);
					Value val = fv.f.getSlotValue(slotName).resolveValue(
							context);
					if(dt.getBaseName().equals(changed.getBaseName()) && slotName.equals(oldSlotName))
						//slot name changed
						slotName = newSlotName;
					out.print("    (" + slotName);
					switch (val.type()) {
					case RU.FACT :
						{
							Fact valFact = val.factValue(context);
							FactVar valFV = (FactVar) map.get(new Integer(
									valFact.getFactId()));
							out.print(" " + valFV.v);
						}
						break;
					case RU.LIST :
						{
							ValueVector vv = val.listValue(context);
							for (int j = 0; j < vv.size(); j++) {
								Value listVal = vv.get(j).resolveValue(context);
								if (listVal.type() == RU.FACT) {
									Fact valFact = listVal.factValue(context);
									FactVar valFV = (FactVar) map
											.get(new Integer(valFact
													.getFactId()));
									out.print(" " + valFV.v);
								} else if (listVal.type() == RU.EXTERNAL_ADDRESS) {
									JOptionPane.showMessageDialog(parentFrame,
											extRefErrMsg, "ERROR",
											JOptionPane.ERROR_MESSAGE);
								} else {
									out.print(" " + listVal.toString());
								}
							}
						}
						break;
					case RU.EXTERNAL_ADDRESS :
						JOptionPane.showMessageDialog(parentFrame, extRefErrMsg,
								"ERROR", JOptionPane.ERROR_MESSAGE);
						return false;
					default :
						out.print(" " + val.toString());
						break;
					}
					out.println(")"); // end slot
				}
				out.println(")"); // end modify
			}
			return true;
		} catch (JessException je) {
			JOptionPane.showMessageDialog(parentFrame, "Jess error while saving: "
					+ je + " Cause: " + je.getCause(), "Jess Error.",
					JOptionPane.ERROR_MESSAGE);
			je.printStackTrace();
			return false;
		} finally {
			out.close();
		}
	}

	boolean saveFactsAsJessCodeDeleteSlot(Deftemplate changed, String deletedSlotName, Iterator factsIt, Writer w) {

		//save facts, deleting one slot from facts derived from one template

		String extRefErrMsg = "One or more facts "
				+ "in the working memory contains a reference to an "
				+ "external object as a slot value.\n Hence the facts"
				+ "cannot be stored in a text file.";

		PrintWriter out = null;
		try {
			out = (w instanceof PrintWriter ?
				   (PrintWriter) w : new PrintWriter( w ) );
			if (out == null)
				return false;

			Context context = r.getGlobalContext();

			class FactVar { // bundle a Fact and a variable name
				Fact f;
				String v;
				FactVar(Fact f, int i) {
					this.f = f;
					this.v = "?var" + i;
				}
			}
			Map<Integer, FactVar> map = new LinkedHashMap<Integer, FactVar>();   // keys fact-ids, values FactVars

			if (factsIt == null)        // default is all facts in Rete engine
				factsIt = r.listFacts();

			for (int i = 1; factsIt.hasNext(); i++) {
				Fact f = (Fact) factsIt.next();
				map.put(new Integer(f.getFactId()), new FactVar(f, i));
			}

			out.println(";;;; Fact assertions: slot assignments are below.");
			out.println("");
			for (Iterator<FactVar> it = map.values().iterator(); it.hasNext();) {
				FactVar fv = (FactVar) it.next();
				String nameAssgnmt = "";
				int n = fv.f.getDeftemplate().getSlotIndex("name");
				if (n >= 0) { // use name to make assertion unique
					Value nameVal = fv.f.getSlotValue("name");
					nameAssgnmt = " (name " + nameVal.resolveValue(context)
							+ ")";
				}
				out.println("(bind " + fv.v + " (assert(" + fv.f.getName()
						+ nameAssgnmt + ")))");
			}

			out.println(); // (modify ...) to set each slot
			out.println(";;;; Slot assignments");
			out.println();
			for (Iterator<FactVar> it = map.values().iterator(); it.hasNext();) {
				FactVar fv = (FactVar) it.next();
				Deftemplate dt = fv.f.getDeftemplate();
				int nSlots = dt.getNSlots();
				if (nSlots < 1) // no slots to assign=>no modify
					continue;
				out.println("; " + fv.f.getName());
				out.println("(modify " + fv.v);
				for (int i = 0; i < nSlots; i++) {
					String slotName = dt.getSlotName(i);
					Value val = fv.f.getSlotValue(slotName).resolveValue(
							context);
					if(dt.getBaseName().equals(changed.getBaseName()) && slotName.equals(deletedSlotName)){
						//skip this slot
						continue;
					}
					out.print("    (" + slotName);
					switch (val.type()) {
					case RU.FACT :
						{
							Fact valFact = val.factValue(context);
							FactVar valFV = (FactVar) map.get(new Integer(
									valFact.getFactId()));
							out.print(" " + valFV.v);
						}
						break;
					case RU.LIST :
						{
							ValueVector vv = val.listValue(context);
							for (int j = 0; j < vv.size(); j++) {
								Value listVal = vv.get(j).resolveValue(context);
								if (listVal.type() == RU.FACT) {
									Fact valFact = listVal.factValue(context);
									FactVar valFV = (FactVar) map
											.get(new Integer(valFact
													.getFactId()));
									out.print(" " + valFV.v);
								} else if (listVal.type() == RU.EXTERNAL_ADDRESS) {
									JOptionPane.showMessageDialog(parentFrame,
											extRefErrMsg, "ERROR",
											JOptionPane.ERROR_MESSAGE);
								} else {
									out.print(" " + listVal.toString());
								}
							}
						}
						break;
					case RU.EXTERNAL_ADDRESS :
						JOptionPane.showMessageDialog(parentFrame, extRefErrMsg,
								"ERROR", JOptionPane.ERROR_MESSAGE);
						return false;
					default :
						out.print(" " + val.toString());
						break;
					}
					out.println(")"); // end slot
				}
				out.println(")"); // end modify
			}
			return true;
		} catch (JessException je) {
			JOptionPane.showMessageDialog(parentFrame, "Jess error while saving: "
					+ je + " Cause: " + je.getCause(), "Jess Error.",
					JOptionPane.ERROR_MESSAGE);
			je.printStackTrace();
			return false;
		} finally {
			out.close();
		}
	}

	/**
	 * Command-line arguments are strings to test with
	 * {@link edu.cmu.pact.jess.Utils#escapeString(String)}.
	 */
	public static void main(String[] args) {
		int i = 0;
		for (i = 0; i < args.length; i++) {
			System.out.println(edu.cmu.pact.jess.Utils.escapeString(args[i]));
		}
	}

	/**
	 * Parse an escaped String.
	 *
	 * @param  s escaped String; must not be null
	 * @return s with framing double quotes removed and embedded quotes and
	 *             backslashes unescaped; null if bad format
	 */
	public static String unescapeString(String s) {
		if (s == null)
			return null;
		int len = s.length();
		if (len < 2 || s.charAt(0) != '\"' || s.charAt(--len) != '\"')
			return null;
		StringBuffer result = new StringBuffer("");
		for (int i = 1; i < len; i++) {
			char c = s.charAt(i);
			if (c == '\\') {
				if (++i >= len)
					return null;
				result.append((char) s.charAt(i));
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	/**
	 * Create a Value object from the object returned from a SlotTable changed
	 * event.
	 *
	 * @param  slotType RU.SLOT or RU.MULTISLOT
	 * @param  dataType RU.ATOM, INTEGER, STRING, ...
	 * @param  newValObj new value
	 * @param  new value as Value; null if can't create value
	 */
	public Value makeValue(int slotType, int dataType, Object newValObj) {
		Value result = null;
		String newValStr = null;

		if (trace.getDebugCode("wme"))
			trace.out("wme", "makeValue() newValObj class " +
				  (newValObj == null ? "(null)" : newValObj.getClass().getName()));
		if (newValObj instanceof Value)    // occurs after Fact selection, e.g.
			return (Value) newValObj;
		if(newValObj instanceof Fact){
			try{
				return new FactIDValue((Fact)newValObj);
			}catch(JessException je){
				trace.errStack("makeValue(Fact)", je);
			}
		}
		try {
			newValStr = (newValObj == null ? null : (String) newValObj);
		} catch (ClassCastException cce) {
			JOptionPane.showMessageDialog(parentFrame,
					"Data type of new value is not String:\n" +
					" update not supported",
					"ERROR", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		try {
			if (slotType == RU.MULTISLOT) {
				if (newValStr == null || newValStr.length() < 1) {
					result = new Value(Funcall.NILLIST);
				} else {
					StringTokenizer tkzr = new StringTokenizer(newValStr);
					ValueVector vv = new ValueVector();
					Value val = null;
					while (tkzr.hasMoreTokens()) {
						val = makeValue(RU.SLOT, RU.ATOM, tkzr.nextToken());
						if (val == null)
							return null;
						vv.add(val);
					}
					result = new Value(vv, RU.LIST);
				}
			} else if (newValStr == null || newValStr.length() < 1 ||
					   newValStr.equalsIgnoreCase("nil")) {
				result = new Value(Funcall.NIL);
			} else if (newValStr.charAt(0) == '\"' && newValStr.length() > 1) {
				String unescaped = unescapeString(newValStr);
				if (unescaped == null)
						JOptionPane.showMessageDialog(parentFrame, "Malformed string " +
													  newValStr + ".",
													  "Input Error.",
													  JOptionPane.ERROR_MESSAGE);
				else
					result = new Value(unescaped, RU.STRING);
			} else {

				Matcher m = factDisplayPattern.matcher(newValStr);
				if (m.matches()) {
					int factId = Integer.parseInt(m.group(1));
					Fact fact = r.findFactByID(factId);
					if (fact == null) {
						JOptionPane.showMessageDialog(parentFrame, "<Fact-" +
													  factId + "> not found",
													  "Input Error.",
													  JOptionPane.ERROR_MESSAGE);
					} else
						result = new FactIDValue(fact);
				} else {
					result = new Value(newValStr, RU.ATOM);
				}
			}
		} catch (NumberFormatException nfe) {
			System.err.println("shouldn't happen: fix the " +
							   "factDisplayPattern; newValStr \"" +
							   newValStr + "\"");
			nfe.printStackTrace();
		} catch (JessException je) {
			String errMsg = "Jess error connverting input \"" + newValStr +
				"\" to value: " + je + " Cause: " + je.getCause();
			JOptionPane.showMessageDialog(parentFrame, errMsg, "Jess Error.",
										  JOptionPane.ERROR_MESSAGE);
			System.err.println("WMEEditor.makeValue() " + errMsg);
			je.printStackTrace();
		}
		return result;
	}

	/**
	 * Filter for JFileChooser to select template (*.clp) files.
	 */
	static class TemplateFileFilter extends javax.swing.filechooser.FileFilter {
		public boolean accept(File f) {
			if (f == null)
				return false;
			else if (f.isDirectory())
				return true;
			else
				return f.toString().endsWith(".clp");
		}
		public String getDescription() {
			return "Template files (*.clp)";
		}
	}

	/**
	 * Instance of template file filter for general use.
	 */
	public static final javax.swing.filechooser.FileFilter templateFileFilter =
		new TemplateFileFilter();

	/**
	 * Filter for JFileChooser to select fact (*.wme) files.
	 */
	static class FactFileFilter extends javax.swing.filechooser.FileFilter {
		public boolean accept(File f) {
			if(trace.getDebugCode("wme"))
				trace.out("wme", "WMEEditor.FactFileFilter.accept("+f+")");
			if (f == null)
				return false;
			else if (f.isDirectory())
				return true;
			else
				return f.toString().endsWith(".wme");
		}
		public String getDescription() {
			return "Fact files (*.wme)";
		}
	}

	/**
	 * Instance of fact file filter for general use.
	 */
	public static final javax.swing.filechooser.FileFilter factFileFilter =
		new FactFileFilter();
	
	public class SaveFactsAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 927991208867294707L;

		public SaveFactsAction() {
			putValue(NAME, "Save Facts");
			putValue(SHORT_DESCRIPTION, "Saves fact instances");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S,
					ActionEvent.CTRL_MASK));
		}

		public void actionPerformed(ActionEvent event) {
			String filename = saveFacts();
			// r.getEventLogger().log("Saved Facts", "file", filename);
			
			mt.authorActionLog(AuthorActionLog.WORKING_MEMORY_EDITOR,
					WMEEditor.SAVE_FACTS,	filename, "", "");
		}
	}

	public class SaveTemplatesAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SaveTemplatesAction() {
			putValue(NAME, "Save Templates");
			putValue(SHORT_DESCRIPTION, "Saves fact definitions (deftemplates)");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_T));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_T,
					ActionEvent.CTRL_MASK));
		}
		
		public void actionPerformed( ActionEvent event )
		{
		    String filename = saveTemplates(true);
			// r.getEventLogger().log("Saved Templates", "file", filename);
			
		    mt.authorActionLog(AuthorActionLog.WORKING_MEMORY_EDITOR,
		    		WMEEditor.SAVE_TEMPLATES, filename, "", "");
		}
	}

	public class RefreshAction extends AbstractAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 6477130125081984667L;

		public RefreshAction()
		{
			putValue( NAME, "Refresh" );
			putValue( SHORT_DESCRIPTION, "Refreshes window with current data" );
			putValue( MNEMONIC_KEY, new Integer(KeyEvent.VK_R) );
			putValue( ACCELERATOR_KEY,
					  KeyStroke.getKeyStroke( KeyEvent.VK_R,
											  ActionEvent.CTRL_MASK ) );
		}
		
		public void actionPerformed( ActionEvent event )
		{
			getPanel().refresh();
		}
	}

	public void valueChanged(ListSelectionEvent e){
		//selection changed in slot table - keep track of which slot is selected, enable delete slot button
		if(e.getValueIsAdjusting()) return;
		if(slotTable.getSelectedRow() != -1) {
			selectedSlotNum = slotTable.getSelectedRow();
			getPanel().constructSlotPopupMenu(true);
		} else {
			getPanel().constructSlotPopupMenu(false);
		}
		getPanel().repaint();
	}

	public void valueChanged(TreeSelectionEvent e){
	}

	//////////////////////////////////////////////////////
	/**
	 * Called when a PropertyChangeEvent is sent from the PreferencesModel.
	 *
	 * @param  evt PropertyChangeEvent detailing change
	 */
	//////////////////////////////////////////////////////
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		Object newValue = evt.getNewValue();
		if (name.equals(DISPLAY_FACT_IDS)) {
			setShowFactIDs(((Boolean)newValue).booleanValue());
		}
		if (name.equals(DISPLAY_FACT_NAMES)) {
			setShowFactNames(((Boolean)newValue).booleanValue());
		}
		if (name.equals(PROBLEM_FOLDER)) {
			displayCognitiveModelFolder();
			getPanel().refresh();
		}
		getPanel().validate();
		getPanel().repaint();
	}


	private String getSlotTypeString(Deftemplate dt, int slot){
		try{
			if (dt.getSlotType(slot) == RU.MULTISLOT) {
				return "multislot";
			} else if(dt.getSlotDataType(slot) == RU.ATOM){
				return "atom";
			} else if(dt.getSlotDataType(slot) == RU.STRING){
				return "string";
			} else if(dt.getSlotDataType(slot) == RU.INTEGER){
				return "integer";
			} else if(dt.getSlotDataType(slot) == RU.FLOAT){
				return "float";
			} else if(dt.getSlotDataType(slot) == RU.LONG){
				return "long";
			}
		} catch(JessException e){
			trace.errStack("getSlotTypeString("+dt+","+slot+")", e);
		}
		return "slot";
	}

	//transfer handler to import data dropped into slot table
	private class FactTransferHandler extends TransferHandler{

		/**
		 * 
		 */
		private static final long serialVersionUID = 7434741040775489474L;

		public int getSourceActions(JComponent c) {
			return MOVE;
		}

		public boolean importData(JComponent c, Transferable t) {
			if (canImport(c, t.getTransferDataFlavors())) {
				try {
					String str = (String)t.getTransferData(DataFlavor.stringFlavor);
					Iterator it = r.listFacts();
					Fact fact;
					String factID = str.substring(str.indexOf('<'));
					try {
						//find fact that matches string passed in
						while (it.hasNext()) {
							fact = (Fact) it.next();
							Deftemplate dt = fact.getDeftemplate();
							if (dt == null) {
								continue;
							}
							if (factID.equals((new FactIDValue(fact)).toString())) {
								//found it
								JTable table = (JTable)c;
								int index = table.getSelectedRow();
								Value oldVal = (Value)table.getModel().getValueAt(index, 2);
								if(oldVal.type() != RU.LIST){
									//single-value slot, simply set value to this fact
									table.getModel().setValueAt(fact, index, 2);
								} else{
									//construct string representation of current list,
									//concatenate string dragged in,
									//reconstruct value vector from string
									String factList = oldVal.toString();
									factList += (new FactIDValue(fact)).toString();
									StringTokenizer tkzr = new StringTokenizer(factList);
									ValueVector vv = new ValueVector();
									Value val = null;
									while (tkzr.hasMoreTokens()) {
										val = makeValue(RU.SLOT, RU.FACT, tkzr.nextToken());
										if (val == null)
											return false;
										vv.add(val);
									}
									table.getModel().setValueAt(new Value(vv, RU.LIST), index, 2);
								}
								return true;
							}
						}
					} catch(JessException je){
						trace.errStack("importData()", je);
						return false;
					}
					return false;
				} catch (UnsupportedFlavorException ufe) {
				} catch (IOException ioe) {
				}
			}

			return false;
		}

		public boolean canImport(JComponent c, DataFlavor[] flavors) {
			for (int i = 0; i < flavors.length; i++) {
				if (DataFlavor.stringFlavor.equals(flavors[i])) {
					return true;
				}
			}
			return false;
		}

	}

	/**
	 * @param postRete new value for {@link #postRete}
	 */
	MTRete getPostRete() {
		return postRete;
	}
	private void setPostRete(MTRete postRete) {
		this.postRete = postRete;
	}
	MTRete getPreRete() {
		return preRete;
	}
	private void setPreRete(MTRete preRete) {
		this.preRete = preRete;
	}
	
	JTree getWMETree() {
		return this.wmeTree;
	}
	
	JHorizontalTable getSlotTable() {
		return this.slotTable;
	}
	
	public WMEEditorPanel getPanel() {
		if(trace.getDebugCode("wme"))
			trace.out("wme", "WMEEditor.getPanel() inWhyNot "+isWhyNot+", postRete "+postRete+", editorPanel "+editorPanel);

		if(!isWhyNot){ 					
			return this.controller.getServer().getCtatFrameController().getDockManager().getMainWMEEditorPanel();
		}
		else {              // Why Not		        
			if(this.editorPanel == null)
				this.editorPanel = new WMEEditorPanel(this.controller.getServer(),
						 this, this.setSize, isWhyNot);
			return this.editorPanel;
		}
	}
	
	Fact getSelectedFact() {
		return this.selectedFact;
	}
	
	void setSelectedFact(Fact fact) {
		this.selectedFact = fact;
	}
	
	Deftemplate getSelectedTemplate() {
		return this.selectedTemplate;
	}
	
	void setSelectedTemplate(Deftemplate template) {
		this.selectedTemplate = template;
	}
	
	int getSelectedSlotNumber() {
		return this.selectedSlotNum;
	}
	
	MTRete getRete() {
		return this.r;
	}
	
	SlotTableModel getNewTableModel(String[] columnNames, int numCols) {
		return new SlotTableModel(columnNames, numCols);
	}
}
