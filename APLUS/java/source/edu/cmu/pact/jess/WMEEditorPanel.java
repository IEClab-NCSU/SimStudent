package edu.cmu.pact.jess;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import jess.Deftemplate;
import jess.Fact;
import jess.FactIDValue;
import jess.JessException;
import jess.RU;
import jess.Value;
import jess.ValueVector;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.jess.WMEEditor.SlotTableModel;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.TutoringService.TSLauncherServer;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.view.AbstractCtatWindow;
import edu.cmu.pact.ctatview.JHorizontalTable;

public class WMEEditorPanel extends JRootPane implements
								ActionListener,
								KeyListener,
								MouseListener,
								MouseMotionListener,
								DocumentListener {
	
	private static final long serialVersionUID = -8607450228214776058L;

	/** Static portion of text for this window's title bar. */
	//private static final String WINDOW_TITLE = "Working Memory Editor";	
	//private static final String mtStatePrefix = "Model Tracing Status: ";
	private static final String cmFolderPrefix = "Cognitive Model Folder: ";
	private static final String NO_TEMPLATE_TEXT = "No template selected";
	private static final String NO_FACT_TEXT = "No fact selected";
	private MT mt;
	private CTAT_Launcher server;
	private BR_Controller controller;
	private JLabel cmFolderLabel;
	private final boolean inWhyNot;
	private WMEEditor wmeEditor;
    private AbstractCtatWindow parentFrame;
	JTextField filter;
	JTextField findFactByID;
	JLabel templateLabel, factLabel;
	JTextField templateField;
	JCheckBox reactive;
	JMenuItem deleteMenu;
	JMenuItem newFactMenu;
	JMenuItem newSlotMenu;
	JPopupMenu wmeTreePopup;
	JPopupMenu slotPopup;
	JSplitPane splitPane;
	Box top_pane;
	String lastProblem;
	String lastMode;

	//private JLabel mtStateLabel;
	//private JLabel mtStateLabel;
	
	/**
	 * @param server		the launcher server
	 * @param editor		the base WME editor, if in whyNot
	 * @param doSetSize		
	 * @param whyNot		true if for a Why Not panel, false otherwise
	 */
	public WMEEditorPanel(CTAT_Launcher server, WMEEditor editor,
			boolean doSetSize, boolean whyNot) {
		if(trace.getDebugCode("wme"))
			trace.out("wme", String.format("WMEEditorPanel(%s, %s, %b, %b)", server, editor, doSetSize, whyNot));
		this.server = server;
		this.parentFrame = this.server.getActiveWindow();
		this.inWhyNot = whyNot;
		this.lastProblem = "";
		this.lastMode = "";
		// Why Not
		if(editor != null) {
			this.wmeEditor = editor;
			this.controller = this.server.getFocusedController();
		}
		// Dock instance
		else {
			this.wmeEditor = this.server.getFocusedController().getModelTracer().getWmeEditor();
		}
		
		
		setName("WM Editor Window");
		cmFolderLabel = new JLabel();
		cmFolderLabel.setName("WME Folder Label");
		cmFolderLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		if(this.server.isDoneIntializing()) {
			displayCognitiveModelFolder();
		}
		

		
		//Box top_pane = new Box(BoxLayout.X_AXIS);
		top_pane = new Box(BoxLayout.X_AXIS);
		
		JRadioButton PreButton = new JRadioButton("Show Pre");
		JRadioButton PostButton = new JRadioButton("Show Post");
		
		if (getPostRete() == null) {
		PreButton.setVisible(false);
		PostButton.setVisible(false);
		} else {
			PreButton.setVisible(true);
			PostButton.setVisible(true);
			}
		
			
		ButtonGroup ShowWMEButtonGroup = new ButtonGroup();

		PreButton.addActionListener(this);
		PostButton.addActionListener(this);
		PreButton.setActionCommand("ShowPre");
		PostButton.setActionCommand("ShowPost");
		ShowWMEButtonGroup.add(PreButton);
		ShowWMEButtonGroup.add(PostButton);
		PreButton.setSelected(true);
		top_pane.add(PreButton);
		top_pane.add(PostButton);
		
		filter = new JTextField(25);
		//{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(filter); }
		JUndo.makeTextUndoable(filter);
		filter.setName("WME Filter");
		filter.setMaximumSize(new Dimension(50, 20));
		filter.getDocument().addDocumentListener(this);
		top_pane.add(Box.createHorizontalGlue());
		top_pane.add(new JLabel(" Search by name:"));
		top_pane.add(filter);
		
		findFactByID = new JTextField(5);
		//{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(findFactByID); }
		JUndo.makeTextUndoable(findFactByID);
		findFactByID.setName("WME Find Fact");
		findFactByID.setMaximumSize(new Dimension(5, 20));
		findFactByID.setActionCommand("findFactByID");
		findFactByID.addActionListener(this);
//		findFactByID.getDocument().addDocumentListener(this);
		top_pane.add(new JLabel(" Search by fact ID:"));
		top_pane.add(findFactByID);

		JTree wmeTree = this.wmeEditor.getWMETree();
		wmeTree.addMouseListener(this);
		wmeTree.addMouseMotionListener(this);
		wmeTree.addKeyListener(this);
		JScrollPane treeScroll = new JScrollPane(wmeTree);
		treeScroll.setPreferredSize(new Dimension(500, 600));
		treeScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// complete wme panel
		JPanel panel4 = new JPanel();
		panel4.setLayout(new BoxLayout(panel4, BoxLayout.Y_AXIS));
		panel4.add(cmFolderLabel);
		panel4.add(treeScroll);
		panel4.setMinimumSize(new Dimension(600, 100));
		panel4.setPreferredSize(new Dimension(600, 300));
		
		JPanel panel7 = new JPanel();
		panel7.setLayout(new BoxLayout(panel7, BoxLayout.Y_AXIS));
		JScrollPane slotTableScroll = new JScrollPane(this.wmeEditor.getSlotTable());
		slotTableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
		slotTableScroll.setMinimumSize(new Dimension(400,150));
		slotTableScroll.setPreferredSize(new Dimension(600, 250));
		slotTableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		Box templateBox = new Box(BoxLayout.X_AXIS);
		templateBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		templateLabel = new JLabel(NO_TEMPLATE_TEXT);
		templateLabel.setName("WME Template Label");
		templateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		templateField = new JTextField(20);
		//{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(templateField); }
		JUndo.makeTextUndoable(templateField);
		templateField.setName("WME Template Name");
		templateField.setMaximumSize(new Dimension(150, 20));
		templateField.setVisible(false);
		templateField.addActionListener(this);
		templateField.setActionCommand("rename");
		reactive = new JCheckBox("Backwards Reactive");
		reactive.setName("WME Reactive");
		reactive.setEnabled(false);
		reactive.setAlignmentX(Component.RIGHT_ALIGNMENT);
		reactive.setActionCommand("reactive");
		reactive.addActionListener(this);
		templateBox.add(templateLabel);
		templateBox.add(templateField);
		templateBox.add(Box.createRigidArea(new Dimension(10, 0)));
		templateBox.add(reactive);
		templateBox.setMaximumSize(new Dimension(500, 20));
		templateBox.add(Box.createHorizontalGlue());
		
		factLabel = new JLabel(NO_FACT_TEXT);
		factLabel.setName("WME Fact Label");
		factLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		panel7.add(templateBox);
		panel7.add(factLabel);
		panel7.add(slotTableScroll);
		panel7.setMinimumSize(new Dimension(600, 100));
		panel7.setPreferredSize(new Dimension(600, 200));

		// panel containing all elements
		this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel4, panel7);

		setLayout(new BorderLayout());
		add(this.splitPane);
		add(top_pane, BorderLayout.NORTH);

		this.constructTreePopupMenu();
		
		int width = 600;
		int height = 500;
		
		if (doSetSize)
			this.setSize(width, height);	
	}
	
	private void refreshPanel(boolean newProblem) {
		if(trace.getDebugCode("wme"))
			trace.out("wme", "WMEEditorPanel.refreshPanel("+newProblem+")");
		if(newProblem) {
			this.splitPane.removeAll();
			getContentPane().removeAll();
			removeAll();
			if(!isInWhyNot()) {
				this.wmeEditor = this.controller.getModelTracer().getWmeEditor();
			}
			JTree wmeTree = this.wmeEditor.getWMETree();
	
			// don't add a duplicate listener - remove first
			wmeTree.removeKeyListener(this);
			wmeTree.removeMouseListener(this);
			wmeTree.removeMouseMotionListener(this);
			wmeTree.addMouseListener(this);
			wmeTree.addMouseMotionListener(this);
			wmeTree.addKeyListener(this);
			
			JScrollPane treeScroll = new JScrollPane(wmeTree);
			treeScroll.setPreferredSize(new Dimension(500, 600));
			
			// complete wme panel
			JPanel panel4 = new JPanel();
			panel4.setLayout(new BoxLayout(panel4, BoxLayout.Y_AXIS));
			panel4.add(cmFolderLabel);
			panel4.add(treeScroll);
			panel4.setMinimumSize(new Dimension(600, 100));
			panel4.setPreferredSize(new Dimension(600, 300));
	
			JPanel panel7 = new JPanel();
			panel7.setLayout(new BoxLayout(panel7, BoxLayout.Y_AXIS));
			JHorizontalTable slotTable = this.wmeEditor.getSlotTable();
	
			JScrollPane slotTableScroll = new JScrollPane(slotTable);
			slotTableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
			
			if (slotTable.getPreferredScrollableViewportSize().getWidth() > 
			  ((JViewport)slotTable.getParent()).getPreferredSize().getWidth()) {
				slotTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
				slotTable.doLayout();
			}
	
			
			Box templateBox = new Box(BoxLayout.X_AXIS);
			templateBox.setAlignmentX(Component.LEFT_ALIGNMENT);
			templateLabel = new JLabel(NO_TEMPLATE_TEXT);
			templateLabel.setName("WME Template Label");
			templateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			templateField = new JTextField(20);
			//{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(templateField); }
			JUndo.makeTextUndoable(templateField);
			templateField.setName("WME Template Name");
			templateField.setMaximumSize(new Dimension(150, 20));
			templateField.removeActionListener(this);
			templateField.addActionListener(this);
			templateField.setActionCommand("rename");
			//reactive.setEnabled(false);
			templateBox.add(templateLabel);
			templateBox.add(templateField);
			templateBox.add(Box.createRigidArea(new Dimension(10, 0)));
			templateBox.add(reactive);
			templateBox.setMaximumSize(new Dimension(500, 20));
			templateBox.add(Box.createHorizontalGlue());
	
			panel7.add(templateBox);
			panel7.add(factLabel);
			panel7.add(slotTableScroll);
			panel7.setMinimumSize(new Dimension(600, 100));
			panel7.setPreferredSize(new Dimension(600, 200));
	
			// panel containing all elements
			this.splitPane.removeAll();
			this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel4, panel7);
			
			getContentPane().add(top_pane, BorderLayout.NORTH);
			getContentPane().add(this.splitPane);
			add(getContentPane());
		}

		constructTreePopupMenu();

		this.splitPane.revalidate();
		this.splitPane.repaint();
		
		//getContentPane().revalidate();
		//getContentPane().repaint();
		
		revalidate();
		repaint();
	}
	
	/**
	 * @return		true if changing controller focus or controller graph; false otherwise
	 */
	private boolean refreshController() {
		BR_Controller newController = (server == null ? null : server.getFocusedController());
		if(trace.getDebugCode("wme"))
			trace.out("wme", "WMEEditorPanel.refreshController() old ctlr@"+
					(controller == null ? -1 : controller.hashCode())+", new ctlr@"+
					(server == null ? -2 : (newController == null ? -1 : newController.hashCode())));
//		if(trace.getDebugCode("wmee"))
//			JOptionPane.showMessageDialog(server.getActiveWindow(), "WMEEditorPanel.refreshController()",
//					"Trace(wme)", JOptionPane.WARNING_MESSAGE);

		// potential situations: change graph focus/controller, change problem, change tutor mode
		if(isInWhyNot()) return false;
		// change focus
		BR_Controller oldController = this.controller;
		this.controller = newController ;
		if(oldController != this.controller) {
			this.lastProblem = this.controller.getProblemFullName();
			this.lastMode = this.controller.getCtatModeModel().getCurrentMode();
			return true;
		}
		// change mode
		String oldMode = this.lastMode;
		this.lastMode = this.controller.getCtatModeModel().getCurrentMode();
		if(!(this.lastMode.equals(oldMode))) {
			this.lastProblem = this.controller.getProblemFullName();
			return true;
		}
		// change problem
		String oldProblem = this.lastProblem;
		this.lastProblem = this.controller.getProblemFullName();
		return (!(this.lastProblem.equals(oldProblem)));
		
		
		/*
		this.lastProblem = (oldController == null ? "" : oldController.getProblemFullName());
		this.controller = this.server.getFocusedController();
		String newProblem = this.controller.getProblemFullName();
		return (oldController != this.controller || !(this.lastProblem.equals(newProblem)));
		*/
	}

	/**
	 * @return value of {@link #inWhyNot}
	 */
	public boolean isInWhyNot() {
		if(trace.getDebugCode("wme"))
			trace.out("wme", "isInWhyNot() returns "+inWhyNot);
		return inWhyNot;
	}

	/**
	 * This method displays the location of Jess files in the WME Editor.
	 */
	public void displayCognitiveModelFolder() {
		refreshController();
		this.mt = this.controller.getModelTracer();
		String folderName;
		String path;
		String cmDir = mt.findCognitiveModelDirectory();
		if (cmDir == null || cmDir.length() < 1) {
			//this should never happen...
			folderName = "none";
			cmFolderLabel.setText(cmFolderPrefix + folderName);
			cmFolderLabel.setToolTipText("No folder is set for the location of cognitive model files");
			return;
		} else {
			File f = new File(cmDir);
			path = f.getAbsolutePath();
			// display current dir instead of single "." (ignore ".." endings) 
			if (path.endsWith(".") && !(path.substring(0, path.length() - 1).endsWith("."))) {
				// strip off trailing single "." and get path again without it
				path = path.substring(0, path.length() - 1);
				f = new File(path);
				path = f.getAbsolutePath();
			}
			folderName = f.getName();
			// if root directory, there's no folder name
			if (folderName.length() < 1) {
				folderName+= "none";
			}
			if (trace.getDebugCode("eep")) trace.out("eep","f file path:"+f.getAbsolutePath());
			cmFolderLabel.setText(cmFolderPrefix + folderName);
			cmFolderLabel.setToolTipText(path);
		}
	}
	
	/**
	 * This method updates the Cognitive Model Folder display once the brd is located
	 * to display the location of the Jess Files (brd, clp, wme)
	 * epfeifer 7/12/11
	 */
	public void updateCognitiveModelFolder(String folderName, String path)
	{
		cmFolderLabel.setText(cmFolderPrefix + folderName);
		cmFolderLabel.setToolTipText(path);
	}

	/**
	 * refreshes the current wme editor interface
	 */
	public synchronized void refresh() {
		if(trace.getDebugCode("wme"))
			trace.out("wme", "WMEEditorPanel.refresh() ");
		if(!isInWhyNot()) {
			// if changing problem focus/information, just change the display
			if(refreshController()) {
				refreshPanel(true);
				return;
			}
			this.wmeEditor = this.controller.getModelTracer().getWmeEditor();
		}

		JTree wmeTree = this.wmeEditor.getWMETree();
		// set the panel as a listener; redo if it's already been done
		JHorizontalTable slotTable = this.wmeEditor.getSlotTable();
		slotTable.removeMouseListener(this);
		slotTable.addMouseListener(this);
		
		if (getMainRete() == null)
			return;

		this.wmeEditor.checkDirtyTypes();
		this.wmeEditor.checkDirtyInstances();

		//retain selection state
		Fact lastSelectedFact = this.wmeEditor.getSelectedFact();
		Deftemplate lastSelectedTemplate = this.wmeEditor.getSelectedTemplate();
		
		DefaultTreeModel wmeTreeModel = (DefaultTreeModel)wmeTree.getModel(); 

		DefaultMutableTreeNode root = (DefaultMutableTreeNode)wmeTreeModel.getRoot();

		//retain expansion state
		ArrayList<String> expanded = new ArrayList<String>();
		Enumeration templates = root.children();
		while(templates.hasMoreElements()){
			DefaultMutableTreeNode template = (DefaultMutableTreeNode)templates.nextElement();
			if(wmeTree.isExpanded(new TreePath(wmeTreeModel.getPathToRoot(template)))){
				expanded.add(template.toString());
			}
		}

		//store the nodes we're removing, to pass to nodesWereRemoved
		int indices[] = new int[root.getChildCount()];
		Object children[] = new Object[root.getChildCount()];
		for(int i=0; i<root.getChildCount(); i++){
			indices[i] = i;
			children[i] = root.getChildAt(i);
		}
		root.removeAllChildren();
		wmeTreeModel.nodesWereRemoved(root, indices, children);

		//add templates back in
		Iterator it = getMainRete().listDeftemplates();
		Deftemplate dt;
		while (it.hasNext()) {
			dt = (Deftemplate) it.next();
			if ((!dt.getBaseName().startsWith("_"))
					&& (!dt.getBaseName().equals("test"))
					&& (!dt.getBaseName().equals("initial-fact"))) {
				wmeTreeModel.insertNodeInto(new DefaultMutableTreeNode(dt.getBaseName()), root, root.getChildCount());
			}
		}
		wmeTree.expandPath(new TreePath(root));

		//remove children from each template node
		//is this still necessary?  I think we just created these nodes, so they should all be empty
		root = (DefaultMutableTreeNode)wmeTreeModel.getRoot();
		templates = root.children();
		while(templates.hasMoreElements()){
			DefaultMutableTreeNode template = (DefaultMutableTreeNode)templates.nextElement();
			if(template.getChildCount() == 0) continue;
			indices = new int[template.getChildCount()];
			children = new Object[template.getChildCount()];
			for(int i=0; i<template.getChildCount(); i++){
				indices[i] = i;
				children[i] = template.getChildAt(i);
			}
			template.removeAllChildren();
			wmeTreeModel.nodesWereRemoved(template, indices, children);
		}

		//add facts back in (if they match the filter)
		it = getMainRete().listFacts();
		Fact fact;
		try{
			while (it.hasNext()) {
				fact = (Fact) it.next();
				dt = fact.getDeftemplate();
				if(dt == null) continue;
				if(filter.getText().length() > 0){
					//check against filter
					if(dt.getSlotIndex("name") == -1) continue;
					String factName = fact.getSlotValue("name").toString().toLowerCase();
					if(factName == null || factName.indexOf(filter.getText().toLowerCase()) == -1) continue;
				}
				if(findFactByID.getText().length() > 0)
				{
					//check against "find fact by ID" box
					if(fact.getFactId() == Integer.parseInt(findFactByID.getText()))
					{
						expanded.add(fact.getDeftemplate().getBaseName());
						lastSelectedFact = fact;
						lastSelectedTemplate = dt;
					}
					else
						continue;
				}
				
				//find the right template
				templates = root.children();
				DefaultMutableTreeNode template = null;
				while(templates.hasMoreElements() && template == null){
					template = (DefaultMutableTreeNode)templates.nextElement();
					if(!template.toString().equals(dt.getBaseName())) template = null;
				}
				if(template == null) continue;
				if (dt.getNSlots() > 0
						&& (dt.getSlotIndex("name") != -1)) {
					//named fact, display name + factID
					wmeTreeModel.insertNodeInto(new DefaultMutableTreeNode(fact.getSlotValue("name") + " " + new FactIDValue(fact)),
											template, template.getChildCount());
				} else {
					//unnamed fact, just display factID
					wmeTreeModel.insertNodeInto(new DefaultMutableTreeNode(new FactIDValue(fact)),
											template, template.getChildCount());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//restore expansion and selection state
		root = (DefaultMutableTreeNode)wmeTreeModel.getRoot();
		templates = root.children();
		while(templates.hasMoreElements()){
			DefaultMutableTreeNode template = (DefaultMutableTreeNode)templates.nextElement();
			String templateName = template.toString();
			if(expanded.contains(templateName)){
				wmeTree.expandPath(new TreePath(wmeTreeModel.getPathToRoot(template)));
				expanded.remove(templateName);
			}
		}
		this.wmeEditor.setSelection(lastSelectedFact, lastSelectedTemplate);

		refreshPanel(false);
	}
	
	/**
	 * This method adds a popup menu to the WME Editor.
	 */
	private void constructTreePopupMenu() {
        int keyMask;
        if ((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
            keyMask = KeyEvent.META_MASK;
        else
            keyMask = KeyEvent.CTRL_MASK;
        
		wmeTreePopup = new JPopupMenu();
		
        JMenuItem goToProblemMenu = new JMenuItem("Go To Problem Fact");
        goToProblemMenu.setActionCommand("goToProblem");
		goToProblemMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, keyMask));
        wmeTreePopup.add(goToProblemMenu);
        goToProblemMenu.addActionListener(this);

		JMenuItem backMenu = new JMenuItem("Back");
		backMenu.setActionCommand("back");
		backMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET, keyMask));
        wmeTreePopup.add(backMenu);
        backMenu.addActionListener(this);
		
		JMenuItem forwardMenu = new JMenuItem("Forward");
		forwardMenu.setActionCommand("forward");
		forwardMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET, keyMask));
        wmeTreePopup.add(forwardMenu);
        forwardMenu.addActionListener(this);
        
		wmeTreePopup.addSeparator();
		
        JMenuItem refreshMenu = new JMenuItem("Refresh");
		refreshMenu.setActionCommand("refresh");
		refreshMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, keyMask));
        wmeTreePopup.add(refreshMenu);
        refreshMenu.addActionListener(this);
		
		JMenuItem expandAllMenu = new JMenuItem("Expand All");
		expandAllMenu.setActionCommand("expand");
        wmeTreePopup.add(expandAllMenu);
        expandAllMenu.addActionListener(this);
		
		JMenuItem collapseAllMenu = new JMenuItem("Collapse All");
		collapseAllMenu.setActionCommand("collapse");
        wmeTreePopup.add(collapseAllMenu);
        collapseAllMenu.addActionListener(this);
		
		wmeTreePopup.addSeparator();

		JMenuItem newTemplateMenu = new JMenuItem("New Template");
        newTemplateMenu.setActionCommand("newTemplate");
        wmeTreePopup.add(newTemplateMenu);
        newTemplateMenu.addActionListener(this);

        newFactMenu = new JMenuItem("New Fact");
		newFactMenu.setActionCommand("newFact");
        wmeTreePopup.add(newFactMenu);
		newFactMenu.removeActionListener(this);
		newFactMenu.addActionListener(this);
		newFactMenu.setEnabled(false);

        newSlotMenu = new JMenuItem("New Slot");
        newSlotMenu.setActionCommand("addSlot");
        wmeTreePopup.add(newSlotMenu);
        newSlotMenu.removeActionListener(this);
        newSlotMenu.addActionListener(this);
        newSlotMenu.setEnabled(false);

		deleteMenu = new JMenuItem ("Delete");
		deleteMenu.setActionCommand("delete");
		deleteMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        wmeTreePopup.add(deleteMenu);
		deleteMenu.removeActionListener(this);
		deleteMenu.addActionListener(this);
		deleteMenu.setEnabled(false);
	}
    
	//display popup menu for wmeTree if appropriate
	private void evaluatePopup(MouseEvent e){
		JTree wmeTree = this.wmeEditor.getWMETree();
    	if (e == null) return;
    	if ((e.getSource() == wmeTree) && (wmeTree.getRowCount() > 0)) {
    		if (e.isPopupTrigger()) {
    			wmeTreePopup.show(e.getComponent(), e.getX(), e.getY());
    		}
    	}
	}

	public void actionPerformed(ActionEvent ae) {
		String actionCommand = ae.getActionCommand();
		if (actionCommand.equals("findFactByID")) {
			doFindFactByID();
		} else if (actionCommand.equalsIgnoreCase("refresh")) {
			doRefresh();
		} else if(actionCommand.equals("expand")){
			doExpand();
		} else if(actionCommand.equals("collapse")){
			doCollapse();
		} else if (actionCommand.equals("back")) {
			doBack();
		} else if (actionCommand.equals("forward")) {
			doForward();
		} else if(actionCommand.equals("newFact")){
			doNewFact();
		} else if(actionCommand.equals("newTemplate")){
			doNewTemplate();
		} else if(actionCommand.equals("goToProblem")) {
			doGoToProblemFact();
		} else if(actionCommand.equals("delete")){
			doDeleteFactOrTemplate();
		} else if(actionCommand.equals("addSlot")){
			doAddSlot();
		} else if(actionCommand.equals("deleteSlot")){
			doDeleteSlot();
		} else if(actionCommand.equals("reactive")){
			doReactive();
		} else if(actionCommand.equals("rename")){
			doRename();
		} else if(actionCommand.equals("ShowPre")){
		//	trace.out("Show Pre firing");
			setRete(getPreRete()); 
			refresh();
		} else if(actionCommand.equals("ShowPost")){
		//	trace.out("Show Post firing");
			setRete(getPostRete());
			refresh();
		}
		
	}

	public void mouseReleased(MouseEvent e){
		JTable slotTable = this.wmeEditor.getSlotTable();
		SlotTableModel slotTableModel = (SlotTableModel)this.wmeEditor.getSlotTable().getModel();
		JTree wmeTree = this.wmeEditor.getWMETree();
		DefaultTreeModel wmeTreeModel = (DefaultTreeModel)wmeTree.getModel();
		
		//selection change in the tree is processed here, as processing it in valueChanged would prevent
		//drag-and-drop from working correctly
		if(e != null && e.getSource() == slotTable) return;
		
		evaluatePopup(e);

		DefaultMutableTreeNode selection = (DefaultMutableTreeNode)wmeTree.getLastSelectedPathComponent();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)wmeTreeModel.getRoot();
		if(selection != null) {
			if(selection.getParent() == root){
				//template selected
				slotTableModel.clear();
				try{
					//find template, load it into slot table, populate labels and such
					Deftemplate dt = getMainRete().findDeftemplate(selection.toString());
					slotTableModel.loadTemplate(dt);
					reactive.setSelected(dt.getBackwardChaining());
					reactive.setEnabled(false);
					newFactMenu.setEnabled(true);
					newSlotMenu.setEnabled(true);
					deleteMenu.setEnabled(true);
					templateLabel.setText("Template: ");
					templateField.setText(selection.toString());
					templateField.setVisible(true);
					factLabel.setText(NO_FACT_TEXT);
					this.wmeEditor.setSelectedTemplate(dt);
					this.wmeEditor.setSelectedFact(null);
				} catch(JessException je){
					trace.errStack("mouseReleased("+e+")", je);
				}
			}
			else {
				//fact selected
				slotTableModel.clear();
				Iterator it = getMainRete().listFacts();
				Fact fact;
				String template = selection.getParent().toString();
				String factID = selection.toString().substring(selection.toString().indexOf('<'));
				try {
					//loop through facts, find one whose factID matches the selection
					while (it.hasNext()) {
						fact = (Fact) it.next();
						Deftemplate dt = fact.getDeftemplate();
						if (dt == null) {
							continue;
						}
						if (dt.getBaseName().equals(template)) {
							if (factID.equals((new FactIDValue(fact)).toString())) {
								//populate slot table
								slotTableModel.loadFact(fact);
								reactive.setSelected(dt.getBackwardChaining());
								reactive.setEnabled(false);
								this.wmeEditor.setSelectedTemplate(dt);
								this.wmeEditor.setSelectedFact(fact);
								break;
							}
						}
					}
					//populate labels, enable menus
					newFactMenu.setEnabled(true);
					newSlotMenu.setEnabled(true);
					deleteMenu.setEnabled(true);
					templateLabel.setText("Template: ");
					templateField.setText(selection.getParent().toString());
					templateField.setVisible(true);
					factLabel.setText("Fact: " + selection.toString());
				} catch (JessException je) {
					trace.errStack("mouseReleased("+e+")", je);
				}
			}
			// automatically resize table according to columns; don't collapse/hide content
			// in the final column
			final int numRows = slotTable.getRowCount();
			final int finalColNumber = slotTable.getColumnCount() - 1;
			int maxWidth = 200;
			for(int row = 0; row < numRows; row++) {
				TableCellRenderer renderer = slotTable.getCellRenderer(row, finalColNumber);
			     Component comp = slotTable.prepareRenderer(renderer, row, finalColNumber);
			     maxWidth = Math.max(comp.getPreferredSize().width, maxWidth);
			}
			slotTable.getColumnModel().getColumn(finalColNumber).setPreferredWidth(maxWidth + 5);
			slotTable.setMinimumSize(slotTable.getPreferredSize());
			
		} else{
			//no selection - clear slot table and disable menus
			newFactMenu.setEnabled(false);	
			newSlotMenu.setEnabled(false);	
			deleteMenu.setEnabled(false);
			slotTableModel.clear();
			this.wmeEditor.setSelectedTemplate(null);
			this.wmeEditor.setSelectedFact(null);
		}
	}
	
	public void mouseClicked(MouseEvent e){
		Fact selectedFact = this.wmeEditor.getSelectedFact();
		Deftemplate selectedTemplate = this.wmeEditor.getSelectedTemplate();
		JTable slotTable = this.wmeEditor.getSlotTable();
		SlotTableModel slotTableModel = (SlotTableModel)slotTable.getModel();
		JTree wmeTree = this.wmeEditor.getWMETree();
		
		//check for double-click on fact in tree, right-click on slot name,
		// or right-click on fact value in table
		String selection = null;
		if(e.getClickCount() == 2 && e.getSource() == wmeTree){
			if(selectedFact == null) selection = selectedTemplate.toString();
			else selection = selectedFact.toString();
			//double-click on fact, display pop-up
			JFrame singleView = new JFrame(selection);
			singleView.getContentPane().setLayout(new BoxLayout(singleView.getContentPane(), BoxLayout.Y_AXIS));
			JLabel newTemplateLabel = new JLabel("Template: " + selectedTemplate.getBaseName());
			newTemplateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			singleView.getContentPane().add(newTemplateLabel);
			if(selectedFact != null){
				JLabel newFactLabel = new JLabel("Fact: " + ((DefaultMutableTreeNode)wmeTree.getLastSelectedPathComponent()).toString());
				newFactLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
				singleView.getContentPane().add(newFactLabel);
			}
			String[] columnNames = {"Slot", "Type", "Slot Value"};
			SlotTableModel newSlotTableModel = this.wmeEditor.getNewTableModel(columnNames, 5);
			JTable newSlotTable = new JTable(newSlotTableModel);
			for (int i = 0; i < 3; i++) {
				TableColumn column = newSlotTable.getColumnModel().getColumn(i);
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
			if(selectedFact != null) newSlotTableModel.loadFact(selectedFact);
			else newSlotTableModel.loadTemplate(selectedTemplate);
			newSlotTable.setAlignmentX(Component.LEFT_ALIGNMENT);
			singleView.getContentPane().add(newSlotTable);
			singleView.pack();
			//singleView.show();
			singleView.setVisible(true);
			
			//Log that the user looked at this fact
			mt.getRete().getEventLogger().log(true, AuthorActionLog.WORKING_MEMORY_EDITOR,
					WMEEditor.INSPECT_FACT, selectedFact.toString(), "", "");
		
		} else if((e.getButton() == MouseEvent.BUTTON3 || e.isControlDown()) && e.getSource() == slotTable){
			int row = slotTable.rowAtPoint(new Point(e.getX(), e.getY()));
			int col = slotTable.columnAtPoint(new Point(e.getX(), e.getY()));
			if(col == slotTableModel.SLOTVALUE_COL && selectedFact != null){
				//right-click on fact value
				try{
					Value val = (Value)slotTableModel.getValueAt(row, col);
					if(val instanceof FactIDValue){
						//single fact contained in cell, go to that one
						Fact f = val.factValue(null);
						Deftemplate dt = f.getDeftemplate();
						this.wmeEditor.setSelection(f, dt);
					} else if(val.type() == RU.LIST){
						//multiple facts in cell - determine which one was clicked
						ValueVector vv = val.listValue(null);
						String valString = val.toString();
						//compute x offset of click
						Rectangle cellRect = slotTable.getCellRect(row, col, false);
						int offset = e.getX() - (int)cellRect.getMinX();
						StringBuffer partial = new StringBuffer();
						int pos=0;
						int i;
						//loop through facts, stopping when one passes the x offset of the click
						for(i=1; i<=vv.size(); i++){
							if(!(vv.get(i-1) instanceof FactIDValue)) continue;
							partial.append(valString.substring(pos, valString.indexOf('>', pos)+1));
							pos = valString.indexOf('>', pos)+1;
							int partialWidth = slotTable.getFontMetrics(slotTable.getFont()).stringWidth(partial.toString());
							if(partialWidth >= offset) break;
						}
						if(i <= vv.size()){
							//clicked on a fact
							FactIDValue factVal = (FactIDValue)vv.get(i-1);
							Fact f = factVal.factValue(null);
							Deftemplate dt = f.getDeftemplate();
							this.wmeEditor.setSelection(f, dt);							
						} //else we clicked to the right of all the facts, ignore
					}
				} catch(JessException je){
					trace.errStack("mouseClicked() in row "+row+", col "+col, je);
				}
			} else if (col == slotTableModel.SLOTNAME_COL) {
				//right-click on slot name
				if (slotPopup != null) {
					slotPopup.show(e.getComponent(), e.getX(), e.getY());					
				}
			}
		}
	}

	public void mousePressed(MouseEvent e){
		evaluatePopup(e);
	}
	
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}

	public void mouseDragged(MouseEvent e){
		JTree wmeTree = this.wmeEditor.getWMETree();
		//export drag from tree
		if(e.getSource() == wmeTree){
			int row = wmeTree.getRowForLocation(e.getX(), e.getY());
			if(row != wmeTree.getMinSelectionRow())
				wmeTree.getTransferHandler().exportAsDrag(wmeTree, e, TransferHandler.COPY);
		}
	}
		
	private void displayAssociatedFact(KeyEvent e) {
		JTable slotTable = this.wmeEditor.getSlotTable();
		JTree wmeTree = this.wmeEditor.getWMETree();
		DefaultTreeModel wmeTreeModel = (DefaultTreeModel)wmeTree.getModel();
		if(e != null && e.getSource() == slotTable) return;
	
		SlotTableModel slotTableModel = (SlotTableModel)this.wmeEditor.getSlotTable().getModel();
		DefaultMutableTreeNode selection = (DefaultMutableTreeNode)wmeTree.getLastSelectedPathComponent();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)wmeTreeModel.getRoot();
		if(selection != null && selection.getParent() == root){
			//template selected
			slotTableModel.clear();
			try{
				//find template, load it into slot table, populate labels and such
				Deftemplate dt = getMainRete().findDeftemplate(selection.toString());
				slotTableModel.loadTemplate(dt);
				reactive.setSelected(dt.getBackwardChaining());
				reactive.setEnabled(false);
				newFactMenu.setEnabled(true);
				newSlotMenu.setEnabled(true);
				deleteMenu.setEnabled(true);
				templateLabel.setText("Template: ");
				templateField.setText(selection.toString());
				templateField.setVisible(true);
				factLabel.setText(NO_FACT_TEXT);
				this.wmeEditor.setSelectedTemplate(dt);
				this.wmeEditor.setSelectedFact(null);
			} catch(JessException je){
				trace.errStack("displayAssociatedFact("+e+")", je);
			}
		} else if(selection != null){
			//fact selected
			slotTableModel.clear();
			Iterator it = getMainRete().listFacts();
			Fact fact;
			String template = selection.getParent().toString();
			String factID = selection.toString().substring(selection.toString().indexOf('<'));
			try {
				//loop through facts, find one whose factID matches the selection
				while (it.hasNext()) {
					fact = (Fact) it.next();
					Deftemplate dt = fact.getDeftemplate();
					if (dt == null) {
						continue;
					}
					if (dt.getBaseName().equals(template)) {
						if (factID.equals((new FactIDValue(fact)).toString())) {
							//populate slot table
							slotTableModel.loadFact(fact);
							reactive.setSelected(dt.getBackwardChaining());
							reactive.setEnabled(false);
							this.wmeEditor.setSelectedTemplate(dt);
							this.wmeEditor.setSelectedFact(fact);
							break;
						}
					}
				}
				//populate labels, enable menus
				newFactMenu.setEnabled(true);
				newSlotMenu.setEnabled(true);
				deleteMenu.setEnabled(true);
				templateLabel.setText("Template: ");
				templateField.setText(selection.getParent().toString());
				templateField.setVisible(true);
				factLabel.setText("Fact: " + selection.toString());
			} catch (JessException je) {
				trace.errStack("displayAssociatedFact("+e+")", je);
			}
		} else{
			//no selection - clear slot table and disable menus
			newFactMenu.setEnabled(false);	
			newSlotMenu.setEnabled(false);	
			deleteMenu.setEnabled(false);
			slotTableModel.clear();
			this.wmeEditor.setSelectedTemplate(null);
			this.wmeEditor.setSelectedFact(null);
		}
	}
	
	private void setRete(MTRete rete) {
		this.wmeEditor.setRete(rete);
	}
	


	private void doAddSlot() {
			MTRete r = getMainRete();
			Deftemplate selectedTemplate = this.wmeEditor.getSelectedTemplate();
		   // mt.getRete().getEventLogger().log("WME Editor Add Slot", "template",
		   //         selectedTemplate);
			
			mt.getRete().getEventLogger().
						log(true, 
							AuthorActionLog.WORKING_MEMORY_EDITOR,
							WMEEditor.ADD_SLOT,
							selectedTemplate.getName(),
							"", "");
						
			//add a slot to the currently displayed template
			//write out templates and facts to strings, adding slot as we go
			StringWriter swt = new StringWriter();
			StringWriter swf = new StringWriter();
			this.wmeEditor.saveTemplatesAddSlot(selectedTemplate, swt);
			this.wmeEditor.saveFactsAsJessCode(r.listFacts(), swf);
			//parse them back in
			try{
				r.clear();
				r.parse(new BufferedReader(new StringReader(swt.toString())));
				r.parse(new BufferedReader(new StringReader(swf.toString())));
			} catch(JessException je){
				if (trace.getDebugCode("wme"))
					trace.out("wme", je.toString());
			}
			refresh();
	}
	
	private void doBack() {
		SlotTableModel slotTableModel = (SlotTableModel)this.wmeEditor.getSlotTable().getModel();
		slotTableModel.goBackInHistory();
	}
	
	private void doCollapse() {
		JTree wmeTree = this.wmeEditor.getWMETree();
		DefaultTreeModel wmeTreeModel = (DefaultTreeModel)wmeTree.getModel();
		//collapse all template nodes
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)wmeTreeModel.getRoot();
		Enumeration templates = root.children();
		while(templates.hasMoreElements()){
			DefaultMutableTreeNode template = (DefaultMutableTreeNode)templates.nextElement();
			wmeTree.collapsePath(new TreePath(wmeTreeModel.getPathToRoot(template)));
		}			
		mt.getRete().getEventLogger().log(true, AuthorActionLog.WORKING_MEMORY_EDITOR,
				"COLLAPSE_ALL", "", "", "");
	}
	
	private void doDeleteFactOrTemplate() {
		MTRete r = getMainRete();
		JTree wmeTree = this.wmeEditor.getWMETree();
		Fact selectedFact = this.wmeEditor.getSelectedFact();
		Deftemplate selectedTemplate = this.wmeEditor.getSelectedTemplate();
		//delete whatever is selected
		if(selectedTemplate != null && selectedFact == null){
		   // mt.getRete().getEventLogger().log("WME Editor Delete Template", "template",
		   //         selectedTemplate.getBaseName());
			
			mt.getRete().getEventLogger().
						log(true, 
							AuthorActionLog.WORKING_MEMORY_EDITOR,
							WMEEditor.DELETE_TEMPLATE,
							selectedTemplate.getBaseName(),
							"", "");
			
			//template selected
			DefaultMutableTreeNode selection = (DefaultMutableTreeNode)wmeTree.getLastSelectedPathComponent();
			//ask for confirmation if facts will be deleted
			if(selection.getChildCount() == 0 || 
				JOptionPane.showConfirmDialog(parentFrame,
										"All of the facts defined under this template will be deleted.  Are you sure you want to delete this template?",
				        "Delete template", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				this.wmeEditor.removeTemplate(selectedTemplate);
			} else {
			    //mt.getRete().getEventLogger().log("WME Editor Cancel Delete Template");
				
				mt.getRete().getEventLogger().
							log(true, 
								AuthorActionLog.WORKING_MEMORY_EDITOR,
								WMEEditor.CANCEL_DELETE_TEMPLATE,
								selectedTemplate.getBaseName(),
								"", "");
				
				return;
			}
		} else if(selectedFact != null){
			//fact selected
		    // mt.getRete().getEventLogger().log("WME Editor Delete Fact", "template",
		    //        selectedFact.getDeftemplate().getBaseName());
			
			mt.getRete().getEventLogger().
						log(true, 
							AuthorActionLog.WORKING_MEMORY_EDITOR,
							WMEEditor.DELETE_FACT,
							selectedFact.getDeftemplate().getBaseName(),
							"", "");
			
			try{
				r.retract(selectedFact);
				r.removeWMEEditorFact(selectedFact);
			} catch(JessException je){
				if (trace.getDebugCode("wme"))
					trace.out("wme", je.toString());
			}
		} else{
			//should never get here - menu should have been grayed out
			deleteMenu.setEnabled(false);
		}
		//refresh tree
		refresh();
		//clear selection
		wmeTree.setSelectionPath(null);
		SlotTableModel slotTableModel = (SlotTableModel)this.wmeEditor.getSlotTable().getModel();
		slotTableModel.clear();
		templateLabel.setText(NO_TEMPLATE_TEXT);
		templateField.setVisible(false);
		factLabel.setText(NO_FACT_TEXT);
		reactive.setEnabled(false);

	}
	
	private void doDeleteSlot() {
		MTRete r = getMainRete();
		Deftemplate selectedTemplate = this.wmeEditor.getSelectedTemplate();
		SlotTableModel slotTableModel = (SlotTableModel)this.wmeEditor.getSlotTable().getModel();
	    // mt.getRete().getEventLogger().log("WME Editor Delete Slot", "template",
		   //         selectedTemplate);
			
			mt.getRete().getEventLogger().
						log(true, 
							AuthorActionLog.WORKING_MEMORY_EDITOR,
							WMEEditor.DELETE_SLOT,
							selectedTemplate.getName(),
							"", "");
			
			//delete the currently selected slot
			//write templates and facts out to strings, omitting the slot in question
			StringWriter swt = new StringWriter();
			StringWriter swf = new StringWriter();
			String slotName = slotTableModel.getValueAt(this.wmeEditor.getSelectedSlotNumber(), slotTableModel.SLOTNAME_COL).toString();
			this.wmeEditor.saveTemplatesDeleteSlot(selectedTemplate, slotName, swt);
			this.wmeEditor.saveFactsAsJessCodeDeleteSlot(selectedTemplate, slotName, r.listFacts(), swf);
			//parse them back in
			try{
				r.clear();
				r.parse(new BufferedReader(new StringReader(swt.toString())));
				r.parse(new BufferedReader(new StringReader(swf.toString())));
			} catch(JessException je){
				if (trace.getDebugCode("wme"))
					trace.out("wme", je.toString());
			}
			refresh();
	}
	
	private void doExpand() {
		JTree wmeTree = this.wmeEditor.getWMETree();
		DefaultTreeModel wmeTreeModel = (DefaultTreeModel)wmeTree.getModel();
		//expand all template nodes
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)wmeTreeModel.getRoot();
		Enumeration templates = root.children();
		while(templates.hasMoreElements()){
			DefaultMutableTreeNode template = (DefaultMutableTreeNode)templates.nextElement();
			wmeTree.expandPath(new TreePath(wmeTreeModel.getPathToRoot(template)));
		}
		mt.getRete().getEventLogger().log(true, AuthorActionLog.WORKING_MEMORY_EDITOR,
				"EXPAND_ALL", "", "", "");
	}
	
	private void doFindFactByID() {
		Iterator facts = getMainRete().listFacts();
		while (facts.hasNext())
		{
			Fact fact = (Fact) facts.next();
			if(fact.getFactId() == Integer.parseInt(findFactByID.getText()))
			{
				this.wmeEditor.setSelectedFact(fact);
				this.wmeEditor.setSelectedTemplate(fact.getDeftemplate());
			}
		}
		mt.authorActionLog(AuthorActionLog.WORKING_MEMORY_EDITOR, "Find Fact by ID", 
				findFactByID.getText(), "", "");
		findFactByID.setText("");
		refresh();

	}
	
	private void doForward() {
		SlotTableModel slotTableModel = (SlotTableModel)this.wmeEditor.getSlotTable().getModel();
		slotTableModel.goForwardInHistory();
	}
	
	private void doGoToProblemFact() {
		Iterator facts = getMainRete().listFacts();
		while (facts.hasNext())
		{
			Fact fact = (Fact) facts.next();
			if(fact.getDeftemplate().getBaseName().equals("problem"))
			{
				this.wmeEditor.setSelectedFact(fact);
				this.wmeEditor.setSelectedTemplate(fact.getDeftemplate());
				break;
			}
		}
		refresh();

	}
	
	private void doReactive() {
		//currently non-functional
		if(this.wmeEditor.getSelectedTemplate() != null){
			if(reactive.isSelected()){
				trace.err("NOT IMPLEMENTED: backward chaining not yet functional");
//				try {
//					//selectedTemplate.doBackwardChaining(r);
//				} catch(JessException je){
//					trace.err(je.toString());
//				}
			} else{
				//remove backward chaining somehow?
			}
		}
	}
	
	private void doRefresh() {
		mt.authorActionLog(AuthorActionLog.WORKING_MEMORY_EDITOR, "refresh",
				"", "", "");
		refresh();
		mt.getRete().getEventLogger().log(true, AuthorActionLog.WORKING_MEMORY_EDITOR,
				"REFRESH", "", "", "");
	}
	
	private void doRename() {
		MTRete r = getMainRete();
		Deftemplate selectedTemplate = this.wmeEditor.getSelectedTemplate();
		   // mt.getRete().getEventLogger().log("WME Editor Rename Template", "template",
		   //         selectedTemplate);
			
			mt.getRete().getEventLogger().
						log(true, 
							AuthorActionLog.WORKING_MEMORY_EDITOR,
							WMEEditor.RENAME_TEMPLATE,
							selectedTemplate.getName(),
							"", "");
			
			//rename the currently displayed template
			//write out templates/facts to strings, changing the template's name
			StringWriter swt = new StringWriter();
			StringWriter swf = new StringWriter();
			//String dft = null;
			this.wmeEditor.saveTemplatesRename(selectedTemplate, templateField.getText().replace(' ', '_'), swt);
			this.wmeEditor.saveFactsAsJessCodeRename(selectedTemplate, templateField.getText().replace(' ', '_'), r.listFacts(), swf);
			//parse them back in
			try{
				r.clear();
				r.parse(new BufferedReader(new StringReader(swt.toString())));
				r.parse(new BufferedReader(new StringReader(swf.toString())));
			} catch(JessException je){
				trace.errStack("Error renaming deftemplate", je);
			}
			refresh();

	}
	
	private void doNewFact() {
		Deftemplate selectedTemplate = this.wmeEditor.getSelectedTemplate();
		//create a new fact
		   // mt.getRete().getEventLogger().log("WME Editor New Fact", "template",
		   //         selectedTemplate.getBaseName());
			
			mt.getRete().getEventLogger().
						log(true, 
							AuthorActionLog.WORKING_MEMORY_EDITOR,
							WMEEditor.ADD_FACT,
							selectedTemplate.getBaseName(),
							"", "");
			
			
			if(selectedTemplate == null){
				//this shouldn't have happened - the menu items should have been grayed out
				newFactMenu.setEnabled(false);
				newSlotMenu.setEnabled(false);
				return;
			}
			try{
				Fact newFact = new Fact(selectedTemplate);
				int i=-1;
				//attempt to assert the fact
				if (trace.getDebugCode("wme")) trace.out("wme", "assertFact() called");
				getMainRete().assertFact(newFact);
				//if name is already taken, the factID will be set to -1
				//we stay in this loop until a unique name is found
				while(newFact.getFactId() == -1){
					if(i == -1){
						newFact.setSlotValue("name", new Value(newFact.getSlotValue("name").toString() + "1", RU.ATOM));
						i = 1;
					}
					else newFact.setSlotValue("name",
						new Value(newFact.getSlotValue("name").toString().substring(0, newFact.getSlotValue("name").toString().length()-1) + i, RU.ATOM));
					i++;
					if(i > 10) i -= 10;
					if (trace.getDebugCode("wme")) trace.out("wme", "assertFact() called");
					getMainRete().assertFact(newFact);
				}
				getMainRete().addWMEEditorFact(newFact);
				Deftemplate prevSelected = selectedTemplate;
				refresh();
				//select new fact
				this.wmeEditor.setSelection(newFact, prevSelected);
			} catch(JessException je){
				trace.errStack("doNewFact()", je);
				return;
			}

	}
	private void doNewTemplate() {
		MTRete r = getMainRete();
		//create a new template
		try{
			String name = "NewTemplate";
			int i=-1;
			//generate a name until it's unique
			while(r.findDeftemplate(name) != null){
				if(i == -1){
					name = name + " 1";
					i = 1;
				}else{
					name = name.substring(0, name.length()-1) + i;
				}
				i++;
				if(i > 10) i -= 10;
			}
			Deftemplate dt = new Deftemplate(name, null, r);
			//template has "name" slot by default
			dt.addSlot("name", new Value("nil", RU.ATOM), "ANY");
		    //mt.getRete().getEventLogger().log("WME Editor New Template", "name", name);
			
			mt.getRete().getEventLogger().
						log(true, 
							AuthorActionLog.WORKING_MEMORY_EDITOR,
							WMEEditor.ADD_TEMPLATE,
							name,
							"", "");
			
			r.addDeftemplate(dt);
			r.addWMEEditorDeftemplate(dt);
			refresh();
			//select new template
			this.wmeEditor.setSelection(null, dt);
		} catch(JessException je){
			trace.errStack("doNewTemplate()", je);
		}
	}
	
	private MTRete getMainRete() {
		return this.wmeEditor.getRete();
	}
	
	private MTRete getPreRete() {
		return this.wmeEditor.getPreRete();
	}
	
	private MTRete getPostRete() {
		return this.wmeEditor.getPostRete();
	}

	void constructSlotPopupMenu(boolean isDeleteEnabled) {
        
		slotPopup = new JPopupMenu();
		
        JMenuItem addSlotMenu = new JMenuItem("Add Slot");
        addSlotMenu.setActionCommand("addSlot");
		slotPopup.add(addSlotMenu);
		addSlotMenu.addActionListener(this);
		
        JMenuItem deleteSlotMenu = new JMenuItem("Delete Slot");
        deleteSlotMenu.setActionCommand("deleteSlot");
        deleteSlotMenu.setEnabled(isDeleteEnabled);
		slotPopup.add(deleteSlotMenu);
		deleteSlotMenu.addActionListener(this);
	}

    
    public void keyAction(KeyEvent e) {}
    public void keyActionRelease(KeyEvent e) {}

    /** Handle the key typed event from the text field. */
	public void keyTyped(KeyEvent e) {}
        
	/** Handle the key pressed event from the text field. */
	public void keyPressed(KeyEvent e) {}
        
	/** Handle the key released event from the text field. */
	public void keyReleased(KeyEvent e) {
        int keyMask;
        if ((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
            keyMask = KeyEvent.META_MASK;
        else
            keyMask = KeyEvent.CTRL_MASK;

        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
		  	displayAssociatedFact(e);
        }
    	if (e.getKeyCode() == KeyEvent.VK_DELETE) {
    		doDeleteFactOrTemplate();
    	}
        if (e.getModifiers() == keyMask) {
        	if (e.getKeyCode() == KeyEvent.VK_P) {
        		doGoToProblemFact();
        	}
        	if (e.getKeyCode() == KeyEvent.VK_R) {
        		doRefresh();
        	}
        	if (e.getKeyCode() == KeyEvent.VK_OPEN_BRACKET) {
        		doBack();
        	}
        	if (e.getKeyCode() == KeyEvent.VK_CLOSE_BRACKET) {
        		doForward();
        	}
        }
	}

	public void changedUpdate(DocumentEvent e){
		refresh();
	}

	public void insertUpdate(DocumentEvent e){
		refresh();
	}

	public void removeUpdate(DocumentEvent e){
		refresh();
	}

}
