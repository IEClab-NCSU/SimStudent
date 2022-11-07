/*
 * This is the Rule Activation Tree window, 
 * more commonly called the Conflict Tree
 * 
 * Created on Oct 6, 2003
 *
 */
package edu.cmu.pact.jess;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import jess.Defrule;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.LoggingSupport;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.MTRete.Routers;
import edu.cmu.pact.ctatview.JHorizontalTable;

/**
 * @author sanket
 *
 */
public class RuleActivationTree implements PropertyChangeListener{
	/**
	 * tree containing the expanded nodes so far
	 */
	TreeTableTree activationTree;
	JHorizontalTable treeTable;
	/*
	 * Data model for the tree containing the nodes for the activation tree
	 */
	TreeTableModel activationModel;
	public TreeTableModel getActivationModel() { return this.activationModel; }
	TreeTableModel tempActivationModel;

	private MTRete rete;
	
	/** Map of our saved states. */
	//private HashMap savedStatesMap = new HashMap(); // unused
	
	/** List of WhyNot instances, to enable us to close them all. */
	private List<WhyNot> whyNotList = new LinkedList<WhyNot>();

	/** Saved s/a/i label, which contains HTML table. */
	JLabel saiLabel = null;

	/** Saved s/a/i popup. */
	JDialog saiDialog = null;
	
	TreePath treePath;

	String restoreState = "dbgRstrState.tmp";

	/**
	 * Vector containing the rules on which breakpoints have been set.
	 */
	Vector breakPointRules = new Vector();
	/**
	 * vector containing the rules on which the breakpoints have not been set
	 */
	Vector<String> remainingRuleList = new Vector<String>();
	/**
	 * semaphore - used to synchronize the model-tracing and the breakpoints - shared between RuleActivationTree and the JessModelTracing classes
	 * it is set when the user selects Resume from the Cognitive Model menu
	 */
	ResumeBreak resumeBreak;
	/**
	 * constructor for the tree. Sets up the tree and the underlying datamodel
	 * constructs the panel and displays the tree in it.
	 *
	 */
	int minDepth = 1;
	/**
	 * Status bar indicating if a breakpoint was set
	 */
	JLabel statusLbl;
	JLabel maxDepthLbl = new JLabel();
	private final CTAT_Controller controller;

	/** Object to actually do the logging. */
	private LoggingSupport loggingSupport;
	public static final String RULE_BREAK_POINTS = "RULE_BREAK_POINTS";
	public static final String WHY_NOT = "WHY_NOT";
	public static final String SHOW_ACTIVATION = "SHOW_ACTIVATION";
	// CONFLICT_TREE
	public static final String SHOW_SAI = "SHOW_SAI";
	

	public RuleActivationTree(final CTAT_Controller controller) {

		this.controller = controller;
		
		loggingSupport = controller.getLoggingSupport();
		init(this.controller);
		//trace.addDebugCode("sdc");
		//trace.addDebugCode("mt");
		if (trace.getDebugCode("sdc")) trace.out("sdc", "constructor");
		trace.printStack("sdc");
		// rat = this;           sewall 6/18/04: now mutexed; see instance()
		this.activationModel =
			new TreeTableModel(RuleActivationNode.create(null, 0));
		this.tempActivationModel =
			new TreeTableModel(RuleActivationNode.create(null, 0));
		this.activationTree = new TreeTableTree(activationModel);

		this.treeTable = new JHorizontalTable(new TreeTableModelAdapter(new TreeTableModel(RuleActivationNode.create(null, 0)), activationTree)){
			
			private static final long serialVersionUID = 1L;

			public int getEditingRow() {
				return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 : editingRow;  
			}
		};
		// set name of useful components for Marathon tests
		this.treeTable.setName("Rule Activation Tree");
		this.maxDepthLbl.setName("Max Depth Label");
		this.activationTree.setName("Activation Tree");

		for(int i=0; i<3; i++){
			treeTable.getColumnModel().getColumn(i).setPreferredWidth(25);
			treeTable.getColumnModel().getColumn(i).setMaxWidth(25);
		}
		
		activationTree.setRowHeight(treeTable.getRowHeight());
		treeTable.setDefaultRenderer(TreeTableModel.class, activationTree);
		treeTable.setDefaultRenderer(String.class, new MatchCellRenderer());
		treeTable.setDefaultEditor(TreeTableModel.class, activationTree);

		// handler for clicks on the table
		treeTable.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				if (!(me.getComponent() instanceof JTable))
					return;
				if (!(((JTable) me.getComponent()).getModel() instanceof
						TreeTableModelAdapter))
					return;
				TreeTableModelAdapter tm =
					(TreeTableModelAdapter) ((JTable) me.getComponent()).getModel();
				int row = treeTable.rowAtPoint(me.getPoint());
				int column = treeTable.columnAtPoint(me.getPoint());
				RuleActivationNode node = (RuleActivationNode) tm.nodeForRow(row);
				if (trace.getDebugCode("mt")) trace.out("mt", "table mouse event node "+node+", column"+column+
						": me"+me);
				if (column > 2) 
					return;
				
				String htmlText = node.getNodeToolTipText();
				if (htmlText == null || htmlText.length() < 1)
					return;
				
				// format the comparison tabel dialog
				if (saiLabel == null) {
					saiLabel = new JLabel(htmlText);
					saiLabel.setBackground(Color.WHITE);
					saiLabel.setOpaque(true);
					JOptionPane optionPane = new JOptionPane();
					optionPane.setMessage(new Object[] {saiLabel });
					optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);
					optionPane.setOptionType(JOptionPane.CLOSED_OPTION);
					saiDialog =
						optionPane.createDialog(controller.getActiveWindow(),
								node.toString());
					
					loggingSupport.authorActionLog(AuthorActionLog.CONFLICT_TREE, 
							RuleActivationTree.SHOW_SAI, 
							node.toString(), htmlText, "");
					
					// upon Vincent's request: the dialog should be non-modal. CTAT #1181.
					saiDialog.setModal(false);
				}
				else {
					saiLabel.setText(htmlText);
					saiDialog.setTitle(node.toString());
				}
				//saiDialog.show();
				saiDialog.setVisible(true);
			}
		});
		
		final RuleActivationTree rat = this;
		
		//handler for clicks on the conflict tree (either opens why-not popup or performs why-not on fired rule)
		this.activationTree.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent me) {
				JTree t = (JTree) me.getComponent();
				if(t.getRowForLocation(me.getX(), me.getY()) != -1){
					treePath = t.getClosestPathForLocation(me.getX(), me.getY());
					RuleActivationNode node = (RuleActivationNode) treePath.getLastPathComponent();
					if (node == null)
						return;

					if (node.isChainNode()) {   //why-not popup on chain node

						// load all the rules from the rete netwok in to popup menu
						Iterator<Defrule> it = rete.allRulesIterator();
						String title = "Why Not?";
						JPopupMenu popup = new JPopupMenu(title);
						popup.add(new JMenuItem(title));
						popup.addSeparator();
						ArrayList<Defrule> addedRules = new ArrayList<Defrule>();
						while (it.hasNext()) {
							Defrule dfr = (Defrule) it.next();
  							addedRules.add(dfr);
						}
						class DefruleComparator implements Comparator<Defrule> {
							public int compare(Defrule r0, Defrule r1) {
								return r0.getName().compareToIgnoreCase(r1.getName());
							}
						}
						Collections.sort(addedRules, new DefruleComparator());
						for(Defrule dfr : addedRules) {
							JMenuItem menuItem = new JMenuItem(dfr.getName());
  							menuItem.addActionListener(new PopupListener(node));
  							popup.add(menuItem);
						}
						popup.show(me.getComponent(), me.getX(), me.getY());

					} else {              //rule activation selected, show why-not
						
						String rule = node.getName();
						Object[] nodes = treePath.getPath();

						loggingSupport.
							authorActionLog(AuthorActionLog.CONFLICT_TREE, 
											RuleActivationTree.SHOW_ACTIVATION, 
											node.toString(), "", "");

						int nodePathLength = nodes.length - 1; // don't fire last node
						if (trace.getDebugCode("mt")) trace.out("mt", "nodePathLength "+nodePathLength+
								", path selected " + dumpPath(nodes));
						ArrayList currentState = new ArrayList();
						try {
						    MTRete wnRete = new MTRete(controller.getEventLogger(), null);					    
						    String errMsg = node.loadPriorState(wnRete);
						    if (errMsg != null) {
						    	JOptionPane.showMessageDialog(controller.getActiveWindow(),
						    			errMsg, "Error loading state",
						    			JOptionPane.ERROR_MESSAGE);
						    } else {
						    	for(Iterator fi = wnRete.listFacts(); fi.hasNext(); )
						    		currentState.add(fi.next());
						    	Vector reqSAI = getReqSAI(node, false);
						    	Vector actualSAI = getActualSAI(node, false, null);
						    	
//                                                        trace.out("constructing WhyNot with rule = " + rule + ", getRuleBaseName(rule) = " + WhyNot.getRuleBaseName(rule));
						    	// get all the rules 
						    	WhyNot wn = new WhyNot(rule, currentState, wnRete.allRulesMap(),
						        		wnRete.getEventLogger());
								housekeepWhyNotList(me, wn);
						    	wn.setReqSAI(reqSAI);
						    	wn.setNodeSAI(actualSAI);
						    	wn.setRete(wnRete);
						    	MT mt = new MT(controller, wnRete);
						    	WMEEditor wmeeditor =
						    		new WMEEditor(wnRete, mt, node.getState(), false, true);
						    	wn.reasonOut(wmeeditor);
						        wn.requestFocus();
						    }
						}
						catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
				activationTree.revalidate();
				treeTable.revalidate();
				getDisplayPanel().repaint(rat);
			}
		});

		/*
		JScrollPane panel = new JScrollPane(treeTable);
		panel.setHorizontalScrollBarPolicy(
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.getContentPane().add(panel);
		this.getContentPane().add(constructBtnPanel(), BorderLayout.SOUTH);
		 */
		
	}
        
        
        
	
	/**
	 * Maintain the {@link #whyNotList} list.  Preserves existing windows on
	 * shift-click; otherwise closes them.
	 * @param me most recent mouse event to invoke the WhyNot window
	 * @param wn new instance to add to the list
	 */
	private void housekeepWhyNotList(MouseEvent me, WhyNot wn) {
		housekeepWhyNotList(me.isShiftDown(), wn);
	}
	
	/**
	 * Maintain the {@link #whyNotList} list.  Preserves existing windows on
	 * shift-click; otherwise closes them.
	 * @param ae most recent action event to invoke the WhyNot window
	 * @param wn new instance to add to the list
	 */
	private void housekeepWhyNotList(ActionEvent ae, WhyNot wn) {
		housekeepWhyNotList((ae.getModifiers() & ActionEvent.SHIFT_MASK) != 0, wn);
	}
	
	/**
	 * Maintain the {@link #whyNotList} list.
	 * @param preserveOld if false, will close old windows and clear list
	 * @param wn new instance to add to the list
	 */
	private void housekeepWhyNotList(boolean preserveOld, WhyNot wn) {
		if (!preserveOld) {  
			for (Iterator<WhyNot> it = whyNotList.iterator(); it.hasNext(); )
				((WhyNot) it.next()).dispose();
			whyNotList.clear();
		}
		whyNotList.add(wn);
	}

	/**
	 * 
	 * @param rb
	 */
	public void setResumeBreak(ResumeBreak rb) {
		this.resumeBreak = rb;
	}

//
//	public static RuleActivationTree instance(CTAT_Controller controller) {
//		return controller.getRuleActivationTree();
//	}

	public void displayBreakPointsPanel() {
		//constructBreakPointsPanel().show();
		constructBreakPointsPanel().setVisible(true);
	}

	private JFrame constructBreakPointsPanel() {
		final JFrame breakPointsFrame = new JFrame("BreakPoints");
		final JTextField depth = new JTextField(3);
		//{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(depth); }
		JUndo.makeTextUndoable(depth);
		depth.setText("" + minDepth);
		// get the remaining rules list
		remainingRuleList.clear();
		Iterator it = rete.listDefrules();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj.getClass().getName().equalsIgnoreCase("jess.defrule")) {
				Defrule dr = (Defrule) obj;
				if (!breakPointRules.contains(dr.getName())) {
					remainingRuleList.add(dr.getName());
				}
			}
		}

		final DefaultListModel allRuleModel = new DefaultListModel();
		final JList allRulesList = new JList(allRuleModel);
		for (int i = 0; i < remainingRuleList.size(); i++) {
			allRuleModel.addElement(remainingRuleList.get(i));
		}
		allRulesList.setMinimumSize(new Dimension(200, 200));
		allRulesList.setMaximumSize(new Dimension(200, 200));
		allRulesList.setPreferredSize(new Dimension(200, 200));

		final DefaultListModel breakPointsModel = new DefaultListModel();
		final JList breakPointsList = new JList(breakPointsModel);
		for (int i = 0; i < breakPointRules.size(); i++) {
			breakPointsModel.addElement(breakPointRules.get(i));
		}
		breakPointsList.setMinimumSize(new Dimension(200, 200));
		breakPointsList.setMaximumSize(new Dimension(200, 200));
		breakPointsList.setPreferredSize(new Dimension(200, 200));

		JButton addBtn = new JButton(">");
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// add the selected rule from the allRulesList to the breakpoints list and remove it from the all rulesList
				int indices[] = allRulesList.getSelectedIndices();
				for (int i = 0; i < indices.length; i++) {
					Object obj = allRuleModel.get(indices[i]);
					// removing the rule from the allrulesList
					breakPointsModel.addElement(obj);
					// adding the rule to the breakpoints list
				}
				for (int i = 0; i < indices.length; i++) {
					allRuleModel.remove(indices[i]);
					// removing the rule from the allrulesList
				}
				allRulesList.updateUI();
				breakPointsList.updateUI();
				//				breakPointsFrame.validate();
			}
		});

		JButton removeBtn = new JButton("<");
		removeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// add the selected rule from the breakpoints list to the allRulesList and remove it from the breakPoints list
				int indices[] = breakPointsList.getSelectedIndices();
				breakPointsList.setSelectionMode(
					ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				for (int i = 0; i < indices.length; i++) {
					Object obj = breakPointsModel.get(indices[i]);
					// removing the rule from the allrulesList
					allRuleModel.addElement(obj);
					// adding the rule to the breakpoints list
				}
				for (int i = 0; i < indices.length; i++) {
					breakPointsModel.remove(indices[i]);
					// removing the rule from the allrulesList
				}
				allRulesList.updateUI();
				breakPointsList.updateUI();
			}
		});

		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
		btnPanel.add(addBtn);
		btnPanel.add(removeBtn);

		JPanel listPanel = new JPanel();
		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		listPanel.setLayout(gridBag);

		JPanel southBtnPanel = new JPanel();
		JButton okBtn = new JButton("Set");
		breakPointsFrame.getRootPane().setDefaultButton(okBtn);
		okBtn.setActionCommand("Set Rule Breakpoints");
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// add the breakpoint to the breakpoints list
				breakPointRules.clear();
				for (int i = 0; i < breakPointsModel.size(); i++) {
					breakPointRules.addElement(breakPointsModel.get(i));
				}
				//rete.getEventLogger().log("Set Rule Breakpoints",
				//        "rules", breakPointRules);
				
				loggingSupport.
					authorActionLog(AuthorActionLog.CONFLICT_TREE, 
									RuleActivationTree.RULE_BREAK_POINTS, 
									breakPointRules.toString(), "", "");
				
				minDepth = Integer.parseInt(depth.getText());
				breakPointsFrame.dispose();
			}
		});

		JButton cancelBtn = new JButton("Cancel");
		cancelBtn.setActionCommand("Cancel Set Rule Breakpoints");
		cancelBtn.addActionListener(rete.getEventLogger());
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// dispose the breakpoints frame
				breakPointsFrame.dispose();
			}
		});

		southBtnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		southBtnPanel.add(okBtn);
		southBtnPanel.add(cancelBtn);

		// ScrollPanel for the allRulesList
		JScrollPane allRuleSp = new JScrollPane(allRulesList);
		allRuleSp.setPreferredSize(new Dimension(200, 200));
		allRuleSp.setMaximumSize(new Dimension(200, 200));

		// scrollPanel for the breakpoints list
		JScrollPane breakRuleSp = new JScrollPane(breakPointsList);
		breakRuleSp.setPreferredSize(new Dimension(200, 200));
		breakRuleSp.setMaximumSize(new Dimension(200, 200));

		JLabel allRuleLabel = new JLabel("Rule Names");
		JLabel breakOnLabel = new JLabel("Break On");

		JLabel depthLbl = new JLabel("Number of rules already fired >= ");
		JPanel depthPanel = new JPanel();
		depthPanel.add(depthLbl);
		depthPanel.add(depth);

		// adding the labels on top of the list box
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;
		gridBag.setConstraints(allRuleLabel, gbc);
		listPanel.add(allRuleLabel);

		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 0.5;
		gridBag.setConstraints(breakOnLabel, gbc);
		listPanel.add(breakOnLabel);

		// adding the allRulesList
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 0.5;
		gridBag.setConstraints(allRuleSp, gbc);
		listPanel.add(allRuleSp);

		// adding the ButtonsPanel
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gridBag.setConstraints(btnPanel, gbc);
		listPanel.add(btnPanel);

		// adding the breakpointsList
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gridBag.setConstraints(breakRuleSp, gbc);
		listPanel.add(breakRuleSp, gbc);

		// adding the southBtn panel
		gbc.weightx = 0.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gridBag.setConstraints(southBtnPanel, gbc);
		listPanel.add(southBtnPanel);

		// adding the depth text field panel		
		gbc.weightx = 0.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gridBag.setConstraints(depthPanel, gbc);
		listPanel.add(depthPanel);

		breakPointsFrame.getContentPane().add(listPanel);
		breakPointsFrame.pack();

		RuleActivationTreePanel displayPanel = getDisplayPanel();
		int x = displayPanel.getX() + displayPanel.getWidth() / 2 - breakPointsFrame.getWidth() / 2;
		int y = displayPanel.getY() + displayPanel.getHeight() / 2 - breakPointsFrame.getHeight() / 2;
		breakPointsFrame.setLocation(x, y);
		return breakPointsFrame;
	}

	/**
	 * Adds a child to the tree.
	 * @param parent existing node in the tree
	 * @param node the node to be added to the tree
	 */
	public void addNode(RuleActivationNode parent, RuleActivationNode node) {
		//tempActivationModel is the tree we're maintaining for the current cycle
		//it will get shown at the end of the cycle
		tempActivationModel.insertNodeInto(node, parent, parent.getChildCount());
	}

	public void displayTree() {
	    if (trace.getDebugCode("mt")) trace.out("mt", "see if Conflict Tree repaints");
		//copy tempActivationModel into actual activationModel, revalidate tree/table
		activationModel = new TreeTableModel((RuleActivationNode)tempActivationModel.getRoot());
		activationTree.setModel(activationModel);
		activationTree.revalidate();
		treeTable.revalidate();
		expandTree((RuleActivationNode)activationModel.getRoot());
		getDisplayPanel().repaint(this);
	}


	/**
	 * Initialize data structures and display the tree.
	 * @param root root for new tree-in-progress {@link #tempActivationModel}
	 */
	public void clearTree(RuleActivationNode root) {
//		this.activationModel = new TreeTableModel(new RuleActivationNode(null, 0));
//		this.tempActivationModel = new TreeTableModel(new RuleActivationNode(null, 0));
//		activationTree.setModel(activationModel);
		reset(root);
		activationTree.revalidate();
		treeTable.revalidate();
		expandTree((RuleActivationNode)activationModel.getRoot());
		if(getDisplayPanel() != null)
			getDisplayPanel().repaint(this);
	}

	private void expandTree(RuleActivationNode node){
		if (trace.getDebugCode("mtt")) trace.out("mtt", "expandTree("+node+")");
		activationTree.expandPath(new TreePath(node.getPath()));
		java.util.Enumeration e = node.children();
		while(e.hasMoreElements()){
			expandTree((RuleActivationNode)e.nextElement());
		}
	}

	public void setRete(MTRete r) {
		this.rete = r;
	}

	/**
	 * Initialize data structures for the start of a new search.
	 * @param root root for new tree-in-progress {@link #tempActivationModel}
	 */
	public void reset(RuleActivationNode root) {
		((RuleActivationNode) this.activationModel.getRoot()).removeAllChildren();
		this.activationModel.reload();
		if (root == null)
			root = RuleActivationNode.create(null, 0);
		tempActivationModel = new TreeTableModel(root);
	}

	/**
	 * A listener on the popup menu displayed when the user clicks on chain
	 * nodes. Invokes {@link WhyNot} on the chosen rule.
	 */
	class PopupListener implements ActionListener {
		private final RuleActivationNode node;
		
		/** Constructor sets chain node. */
		PopupListener(RuleActivationNode node) {
			this.node = node;
		}
		
		public void actionPerformed(ActionEvent e) {
			//rule selected from why-not popup

		    String rule = e.getActionCommand();
		    		
		    String selectedPath = dumpPath(treePath.getPath());
			if (trace.getDebugCode("mt")) trace.out("mt", "PopupListener path selected " + selectedPath);
			loggingSupport.authorActionLog(AuthorActionLog.CONFLICT_TREE, 
					RuleActivationTree.WHY_NOT, rule, selectedPath, "");
			
		    ArrayList currentState = new ArrayList();
		    try {
		        MTRete wnRete;
		        wnRete = new MTRete(controller.getEventLogger(), null);  // else use node Rete
		        String errMsg = null;
		        if (!node.isRoot() || node.getChildCount() > 0)
		        	errMsg = node.loadPriorState(wnRete);
		        else
		        	errMsg = copyRete(rete, wnRete);
		        if (errMsg != null) {
		        	JOptionPane.showMessageDialog(controller.getActiveWindow(),
		        			errMsg, "Error loading state",
		        			JOptionPane.ERROR_MESSAGE);
		        	return;
		        }
		        for(Iterator fi = wnRete.listFacts(); fi.hasNext(); )
		        	currentState.add(fi.next());
		        Vector reqSAI = getReqSAI(node, true);
		        Vector actualSAI = getActualSAI(node, true, rule);

                        trace.out("constructing WhyNot with rule = " + rule);

		        WhyNot wn = new WhyNot(rule, currentState, wnRete.allRulesMap(),
		        		rete.getEventLogger());
				housekeepWhyNotList(e, wn);
		        wn.setReqSAI(reqSAI);
		        wn.setNodeSAI(actualSAI);
		        wn.setRete(wnRete);
		        MT mt = new MT(controller, wnRete);
		        WMEEditor wmeeditor = new WMEEditor(wnRete, mt, null, false, true);
		        wn.reasonOut(wmeeditor);
		    } catch (Exception ex) {
		        ex.printStackTrace();
		    }
		    getDisplayPanel().repaint(controller.getRuleActivationTree());
		}
	}

	/**
	 * @return
	 */
	public Vector getBreakPointRules() {
		return this.breakPointRules;
	}

	/**
	 * Copy the contents of a Rete to another Rete.
	 * @param src copy-from engine
	 * @param dest copy-to engine
	 * @return error message; null if no Exception
	 */
	public static String copyRete(MTRete src, MTRete dest) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Routers routers = src.saveState(baos);
			byte[] reteState = baos.toByteArray(); 
			ByteArrayInputStream bais = new ByteArrayInputStream(reteState);
			dest.loadState(bais, routers);
			return null;
		} catch (Exception e) {
			return "Error copying Rete state: "+e;
		}
	}

	/**
	 * @return
	 */
	public int getMinDepth() {
		return this.minDepth;
	}


	//info on TreeTables at http://java.sun.com/products/jfc/tsc/articles/treetable1/

	/**
	 * Display a path.
     * @param  node array from tree.getPath()
     * @return list of path elements, preceded by "L:" and the path length
     */
    private String dumpPath(Object[] nodes) {
        StringBuffer result = new StringBuffer("L:" + nodes.length);
        for (int i = 0; i < nodes.length; ++i)
            result.append(" ").append(nodes[i].toString());
        return result.toString();
    }

    /**
     * From a {@link RuleActivationNode} or its children, get the actual
     * selection, action, input: these are the model's values.
     * @param  node query this node or its children
     * @param  scanChildren whether to query this node or its children
     * @param  name of rule to match
     * @return Vector with reqSelection, reqAction, reqInput
     */
    public Vector getActualSAI(RuleActivationNode node, boolean scanChildren, String rule) {
    	Vector actualSAI = new Vector();
        if (!scanChildren) {
            actualSAI.add(node.getActualSelection());
            actualSAI.add(node.getActualAction());
            actualSAI.add(node.getActualInput());
        } else {
            for (int z = 0; z < node.getChildCount(); z++) {
                RuleActivationNode dn = (RuleActivationNode) node.getChildAt(z);
                String nodeName = dn.toString();
                if (nodeName.equals(rule)) {
                    actualSAI.add(dn.getActualSelection());
                    actualSAI.add(dn.getActualAction());
                    actualSAI.add(dn.getActualInput());
                }
            }
        }
        return actualSAI;
    }
    
    /**
     * From a {@link RuleActivationNode} or its children, get the request's
     * selection, action, input: these are the student values.
     * @param  node query this node or its children
     * @param  scanChildren whether to query this node or its children
     * @return Vector with reqSelection, reqAction, reqInput
     */
    public Vector getReqSAI(RuleActivationNode node, boolean scanChildren) {
        Vector reqSAI = new Vector();
        if (!scanChildren) {
            reqSAI.add(node.getReqSelection());
            reqSAI.add(node.getReqAction());
            reqSAI.add(node.getReqInput());
            return reqSAI;
        }
		for (int z = 0; z < node.getChildCount(); z++) {
		    RuleActivationNode dn = (RuleActivationNode) node.getChildAt(z);
		    if (reqSAI.size() == 0){
		        String s = dn.getReqSelection();
		        String a = dn.getReqAction();
		        String in = dn.getReqInput();
		        if(!(s.equals(MTRete.NOT_SPECIFIED) && a.equals(MTRete.NOT_SPECIFIED) && in.equals(MTRete.NOT_SPECIFIED))){
		            reqSAI.add(s);
		            reqSAI.add(a);
		            reqSAI.add(in);
		        }
			}
		}
        return reqSAI;
    }

    public class TreeTableTree extends JTree implements TableCellRenderer, TableCellEditor { 

		/**
		 * 
		 */
		private static final long serialVersionUID = 8443720787360882405L;

		public TreeTableTree(TreeModel m){
			super(m);
		}

		protected int visibleRow; 

		public void setBounds(int x, int y, int w, int h) { 
			super.setBounds(x, 0, w, treeTable.getHeight());          
		} 

		public void paint(Graphics g) { 
			g.translate(0, -visibleRow * getRowHeight());          
			super.paint(g); 
		} 

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) { 
			if(isSelected)
				setBackground(table.getSelectionBackground());
			else
				setBackground(table.getBackground());
			visibleRow = row; 
			return this; 
		} 

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
			return this;
		}

		protected EventListenerList listenerList = new EventListenerList();

		public Object getCellEditorValue() { return null; }
		public boolean isCellEditable(EventObject e) { return true; }
		public boolean shouldSelectCell(EventObject anEvent) { return false; }
		public boolean stopCellEditing() { return true; }
		public void cancelCellEditing() {}

		public void addCellEditorListener(CellEditorListener l) {
			listenerList.add(CellEditorListener.class, l);
		}

		public void removeCellEditorListener(CellEditorListener l) {
			listenerList.remove(CellEditorListener.class, l);
		}

		protected void fireEditingStopped() {
			Object[] listeners = listenerList.getListenerList();
			for (int i = listeners.length-2; i>=0; i-=2) {
				if (listeners[i]==CellEditorListener.class) {
					((CellEditorListener)listeners[i+1]).editingStopped(new ChangeEvent(this));
				}	       
			}
		}

		protected void fireEditingCanceled() {
			Object[] listeners = listenerList.getListenerList();
			for (int i = listeners.length-2; i>=0; i-=2) {
				if (listeners[i]==CellEditorListener.class) {
					((CellEditorListener)listeners[i+1]).editingCanceled(new ChangeEvent(this));
				}	       
			}
		}

	} 

	public class MatchCellRenderer extends DefaultTableCellRenderer{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 2490846083534325752L;

		/**
		 * Widen the match cells by 50% to fix problem on PCs where they're too
		 * narrow.
		 * @return super.getMinimumSize() with width changed
		 */
//		public Dimension getMinimumSize() {
//			Dimension result = super.getMinimumSize();
//			result.setSize(result.getWidth()*1.5, result.getHeight());
//			return result;
//		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			setForeground(Color.BLACK);
			if(value == null) return this;
			setForeground(new Color(0.0f, 0.5f, 0.0f));
			if(value.toString().equals("?")){
				setForeground(Color.BLACK);
			} else if(value.toString().equals("X")){
				setForeground(Color.RED);
			}
			return this;
		}

	}

	public class TreeTableModel extends DefaultTreeModel{
		/**
		 * 
		 */
		private static final long serialVersionUID = -7494300815613177157L;
		protected String cNames[] = {"S", "A", "I", "Rule"};

		public TreeTableModel(TreeNode root){
			super(root);
		}
  
		public boolean isCellEditable(Object node, int column) { 
			return getColumnClass(column) == TreeTableModel.class; 
		}

		public void setValueAt(Object aValue, Object node, int column) {}

		public int getColumnCount() {
			return cNames.length;
		}

		public String getColumnName(int column) {
			return cNames[column];
		}

		public Class getColumnClass(int column) {
			if(column  == 3) return TreeTableModel.class;
			return String.class;
		}

		/**
		 * Return the content to be displayed at for the given node and column.
		 * @param  node for this row
		 * @param  column index of one of the 4 table columns
		 * @return String w/ the content 
		 */
		public Object getValueAt(Object node, int column) {
			int match = RuleActivationNode.NOT_SPEC;
			switch(column){
				case 0:
					match = ((RuleActivationNode)node).selectionMatches();
					break;
				case 1:
					match = ((RuleActivationNode)node).actionMatches();
					break;
				case 2:
					match = ((RuleActivationNode)node).inputMatches();
					break;
				case 3:
					return ((RuleActivationNode)node).getName();
			}
			if(match == RuleActivationNode.MATCH){
				return "\u2713"; //checkmark
			}
			if(match == RuleActivationNode.NO_MATCH){
				return "X";
			}
			if(match == RuleActivationNode.ANY_MATCH){
				return "*";
			}
			return null;        // NOT_SPEC
		}
	}

	public class TreeTableModelAdapter extends AbstractTableModel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 2811872749348512828L;
		JTree tree;
		TreeTableModel tableModel;

		public TreeTableModelAdapter(TreeTableModel tableModel, JTree tree) {
			this.tree = tree;
			this.tableModel = tableModel;

			tree.addTreeExpansionListener(new TreeExpansionListener() {
				public void treeExpanded(TreeExpansionEvent event) {  
					fireTableDataChanged(); 
				}
				public void treeCollapsed(TreeExpansionEvent event) {  
					fireTableDataChanged(); 
				}
			});
			tableModel.addTreeModelListener(new TreeModelListener() {
				public void treeNodesChanged(TreeModelEvent e) {
					delayedFireTableDataChanged();
				}
				public void treeNodesInserted(TreeModelEvent e) {
					delayedFireTableDataChanged();
				}
				public void treeNodesRemoved(TreeModelEvent e) {
					delayedFireTableDataChanged();
				}
				public void treeStructureChanged(TreeModelEvent e) {
					delayedFireTableDataChanged();
				}
			});
		}

		public int getColumnCount() {
			return tableModel.getColumnCount();
		}

		public String getColumnName(int column) {
			return tableModel.getColumnName(column);
		}

		public Class getColumnClass(int column) {
			return tableModel.getColumnClass(column);
		}

		public int getRowCount() {
			return tree.getRowCount();
		}

		protected Object nodeForRow(int row) {
			TreePath treePath = tree.getPathForRow(row);
			//trace.out("mt", "TreeTableModel.nodeForRow("+row+") gets "+treePath);
			return treePath.getLastPathComponent();         
		}

		public Object getValueAt(int row, int column) {
			return tableModel.getValueAt(nodeForRow(row), column);
		}

		public boolean isCellEditable(int row, int column) {
			return tableModel.isCellEditable(nodeForRow(row), column); 
		}

		public void setValueAt(Object value, int row, int column) {
			tableModel.setValueAt(value, nodeForRow(row), column);
		}

		protected void delayedFireTableDataChanged() {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					fireTableDataChanged();
				}
			});
		}
	}

	private void getPreferencesFromModel(PreferencesModel model) {
		
		final Integer integerValue = model.getIntegerValue("Tree Depth");
        if (integerValue != null)
        	maxDepthLbl.setText("Max Depth: " + integerValue);

	}
	/**
	 * Listen to the {@link PreferencesModel} for preferences of interest here.
	 * Call this from any constructor. No-op if {@link #controller} is null.
	 */
	private void init(CTAT_Controller controller) {
		if (controller == null)
			return;
		PreferencesModel pm = controller.getPreferencesModel();
		if (pm == null)
			return;
		pm.addPropertyChangeListener("Tree Depth", this);  // rete.getTreeDepth()
		getPreferencesFromModel(pm);
	}

	/**
	 * Method to receive {@link PropertyChangeEvent}s from {@link PreferencesModel}.
	 * @param  arg0 event to process
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		String name = arg0.getPropertyName();
		Object newValue = arg0.getNewValue();
		if (trace.getDebugCode("mt"))
			trace.out("mt", "Changed " + name + " from " + arg0.getOldValue() + " to " + newValue);
		if (MTRete.TREE_DEPTH.equals(name))
			maxDepthLbl.setText("Max Depth: " + newValue);
	}
	
	JTable getTreeTable() {
		return this.treeTable;
	}
	
	JLabel getDepthLabel() {
		return this.maxDepthLbl;
	}
	
	public RuleActivationTreePanel getDisplayPanel() {
		if(controller != null && controller.getServer() != null)
			return controller.getServer().getDockManager().getConflictTreePanel();
		else
			return null;
	}

}
