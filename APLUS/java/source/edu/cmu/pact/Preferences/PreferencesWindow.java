/*
 * Created on Jul 20, 2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package edu.cmu.pact.Preferences;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
//import java.util.LinkedList;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
//import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
//import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.ctat.view.AbstractCtatWindow;

//////////////////////////////////////////////////////////////////////
/**
 * @author mpschnei
 * 
 * This window is used to display the preferences panels for all the CTAT tools
 * in one place.
 */
//////////////////////////////////////////////////////////////////////
public class PreferencesWindow extends AbstractCtatWindow
							   implements TreeSelectionListener,
										  PropertyChangeListener,
										  ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1373883450671892925L;

	/** Increment with each constructor call to constructor. */
	private static int serialNoGenerator = 0;
	
	/** Save incremented serial number. */
	private int serialNo = 0;

	/**
	 * Generates an editor for a single preference.
	 */
	private class EditorPanel extends JPanel
							  implements ActionListener, DocumentListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1245568631165998269L;

		/**
		 * A JTextField that will not stretch vertically when its 
		 * container is taller than needed.
		 */
		private class FixedHeightTextField extends JTextField {
			/**
			 * 
			 */
			private static final long serialVersionUID = -472664190301771333L;
			FixedHeightTextField(String v, int cols) {
				super(v, cols);
				this.setName(name);  //set name for Marathon tests
			}
			public Dimension getMaximumSize() {
				int width = super.getMaximumSize().width;
				
				int height = super.getPreferredSize().height;
				return (new Dimension(width, height));
			}
		}

		/**
		 * A JComboBox that will not stretch horizontally when its 
		 * container is wider than needed.
		 */
		private class FixedWidthComboBox extends JComboBox{
			/**
			 * 
			 */
			private static final long serialVersionUID = 2382681863052680778L;
			FixedWidthComboBox(Object[] choices) {
				super(choices);
				this.setName(name);  //set name for Marathon tests
			}
			public Dimension getMaximumSize() {
				int width = super.getPreferredSize().width;
				
				int height = super.getMaximumSize().height;
				return (new Dimension(width, height));
			}
		}

		/** Property name, as known to callers. */
		private final String name;

		/** Property class, as known to callers. */
		private final Class cls;

		/** Property label, as seen by user. */
		private final String editorLabel;

		/** Caller's listener, set from constructor. */
		private PropertyChangeListener externListener = null;

		/** Widget to hold text of item description. */
		private JLabel descriptionLabel = null;

		/** Widget for Enum item. */
		JComboBox comboBox = null;
		
		/** Widget to hold Boolean item. */
		JCheckBox cb = null;

		/** Widget to hold non-Boolean item, displayed as a String. */
		JTextField tf = null;

		/**
		 * Create the panel with the editor widget and add the provided
		 * listener to listen on that widget.
		 *
		 * @param  pref preference item to edit
		 * @param  externListener caller's listener for widget actions
		 */
		public EditorPanel(PreferencesModel.Preference pref,
						   PropertyChangeListener externListener) {
			super();
			name = pref.getName();
			cls = pref.getCls();
			editorLabel = pref.getEditorLabel();
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.PAGE_START;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.weightx = c.weighty = 1;
			setLayout(new GridBagLayout());
			Border margin = BorderFactory.createEmptyBorder(MARGIN/2, 0,
															MARGIN/2, 0);
			setBorder(margin);

			// Add html formatting to fold long lines into a paragraph.
			//
			String description = pref.getDescription();
			
			if (trace.getDebugCode("pr")) trace.out ("pr", "description = " + description);
			
			if (description != null && description.length() > 0)
				descriptionLabel =
					new JLabel("<html><p>" +
							   Utils.expandPropertyReferences(description) +
							   "</p></html>");
			else
				descriptionLabel = new JLabel("<html><p>&nbsp;</p></html>");
			descriptionLabel.setName(name + " Description");  //set name for Marathon tests
			this.externListener = externListener;

			if (pref.getCls().isEnum()) {
				JLabel title = new JLabel(editorLabel);
				title.setName(name + " Title");  //set name for Marathon tests
				comboBox = getEnumComboBox(pref);
				comboBox.setActionCommand(pref.getName());
				comboBox.addActionListener(this);
				add(title, c);
				add(comboBox, c);
			} else if (Boolean.class == pref.getCls()) {
				Boolean value = (model != null ? model.getBooleanValue(name) :
												 (Boolean)pref.getValue());
				cb = new JCheckBox(editorLabel, value.booleanValue());
				cb.setIconTextGap(10);         // no. pixels betw button & text
				cb.setActionCommand(pref.getName());
				cb.addActionListener(this);
				add(cb, c);
			} else if (FileBrowser.class == pref.getCls()){
				Object value  = (model != null ? model.getValue(name) :
												 pref.getValue());
				JLabel title = new JLabel(editorLabel);
				title.setName(name + " Title");  //set name for Marathon tests
				tf = new FixedHeightTextField(value.toString(), 20);
				FileBrowser browseButton = new FileBrowser (tf, name, editorLabel);
				tf.setEditable(true);
				tf.setActionCommand(pref.getName());
				tf.addActionListener(this);
				tf.getDocument().addDocumentListener(this);
				add(title, c);
				browseButton.setActionCommand(pref.getName());
				browseButton.addActionListener(this);
				browseButton.setName(name + " Button");  //set name for Marathon tests
				add (tf, c);
				int anchor = c.anchor;
				int fill = c.fill;
				c.anchor = GridBagConstraints.LINE_END;
				c.fill = GridBagConstraints.NONE;
				add(browseButton, c);
				c.fill = fill;
				c.anchor = anchor;
			} else {
				Object value  = (model != null ? model.getValue(name) :
												 pref.getValue());
				JLabel title = new JLabel(editorLabel);
				title.setName(name + " Title");  //set name for Marathon tests
				tf = new FixedHeightTextField(value.toString(), 20);
				tf.setActionCommand(pref.getName());
				tf.addActionListener(this);
				tf.getDocument().addDocumentListener(this);
				add(title, c);
				add(tf, c);
			}
			if (trace.getDebugCode("pr"))
				trace.out ("pr", "description max  size " + descriptionLabel.getMaximumSize());
			if (trace.getDebugCode("pr"))
				trace.out ("pr", "description pref size " + descriptionLabel.getPreferredSize());
			c.weighty *= 3;
			c.fill = GridBagConstraints.BOTH;
			add(descriptionLabel, c);
		}
		
		public Dimension getMaximumSize() {
			return new Dimension (300, 240);
		}
		
		public Dimension getPreferredSize() {
			Dimension pref = super.getPreferredSize();
			Dimension max = getMaximumSize();
			return new Dimension (Math.min(pref.width, max.width), pref.height);
		}

		/**
		 * Create a combo box with the choices for a preference of enum type.
		 * @param pref 
		 * @return combo box populated with enum strings, preset to pref value
		 */
		private JComboBox getEnumComboBox(PreferencesModel.Preference pref) {
			if (trace.getDebugCode("pr"))
				trace.out("pr", "getEnumComboBox("+pref+")");
			Class cls = pref.getCls();
			if (!cls.isEnum())
				return null;
			EnumSet enumSet = EnumSet.allOf(cls);
			comboBox = new FixedWidthComboBox(enumSet.toArray());
//	        comboBox.setPreferredSize(new Dimension (150,20));
//	        comboBox.setMaximumSize(new Dimension (150,20));
			Enum value = (model != null ? model.getEnumValue(name) : (Enum) pref.getValue());
			comboBox.setSelectedItem(value);
			comboBox.setName(name + " ComboBox"); // name for Marathon
			return comboBox;
		}

		/** Required by {@link DocumentListener}.
			Just calls {@link #changedUpdate(DocumentEvent)}. */
		public void removeUpdate(DocumentEvent evt) { changedUpdate(evt); }

		/** Required by {@link DocumentListener}.
			Just calls {@link #changedUpdate(DocumentEvent)}. */
		public void insertUpdate(DocumentEvent evt) { changedUpdate(evt); }

		/**
		 * Called each time the user edits the field. Checks String from
		 * {@link #tf}.getText(), instead of data from event argument, with
		 * {@link #checkText(String)}.  Relies on
		 * {@link PreferencesWindow.propertyChange(PropertyChangeEvent)}
		 * to filter duplicate updates.
		 *
		 * @param  evt DocumentEvent; actually uses string from tf.getText()
		 */
		public void changedUpdate(DocumentEvent evt) {
			if (trace.getDebugCode("pr")) trace.out("pr", "DocumentEvent on " + name + ": " +
					  evt.getType() + ", text now " + tf.getText());
			Object nv = tf.getText();
			if (null == (nv = checkText((String) nv)))
				return;
			PropertyChangeEvent extEvt = new PropertyChangeEvent(this, name,
																 null, nv);
			externListener.propertyChange(extEvt);  // fire event
		}

		/**
		 * ActionListener on editor widgets emits PropertyChangeEvents to
		 * externListener.
		 *
		 * @param  evt ActionEvent from editor widget (JTextField, etc.)
		 */
		public void actionPerformed(ActionEvent evt) {
			Object nv = null;
			Object source = evt.getSource();

			if (source instanceof JComboBox) {
				nv = ((JComboBox) source).getSelectedItem();
			} else if (source instanceof JCheckBox)
				nv = new Boolean(((JCheckBox)source).isSelected());
			else if (source instanceof JTextField){
				nv = ((JTextField) source).getText();
				if (null == (nv = checkText((String) nv)))
					return;
			} else if (source instanceof FileBrowser) {
				FileBrowser fb = (FileBrowser) source;
				String currentDir = getDirFromFileBrowser(fb);
				nv = fb.chooseDirectory(PreferencesWindow.this, currentDir);
				if (nv == null)
					return;				
				tf.setText((String) nv);
			} else {
				throw new IllegalArgumentException ("Unknown source type " + evt.getSource().getClass().getName());
			}
			PropertyChangeEvent extEvt = new PropertyChangeEvent(this, name,
																 null, nv);
			externListener.propertyChange(extEvt);  // fire event
		}

		/**
		 * Get an absolute directory from a {@link FileBrowser#getTextField()} as follows: <ul>
		 * <li>if the field has a relative path, try to convert to absolute using the classpath;</li>
		 * <li>if the path doesn't exist, walk up its chain of parents to the 1st directory that does exist;</li>
		 * <li>if none of that works, use the current runtime directory.</li>
		 * </ul>
		 * @param fb browser with text field to read
		 * @return path
		 */
		private String getDirFromFileBrowser(FileBrowser fb) {
			JTextField tf = fb.getTextField();
			String currentDir = (tf == null ? null : tf.getText());
			if (currentDir != null && currentDir.length() > 0) {
				File d = new File(currentDir);
				if (!d.isAbsolute())
					d = Utils.getFileAsResource(currentDir, this);
				if (d != null) {
					while (!d.exists() && d.getParentFile() != null)
						d = d.getParentFile();
				}
				if (d != null && d.exists())
					currentDir = d.getAbsolutePath();
				else
					currentDir = null;
			}
			if (currentDir == null || currentDir.length() < 1)
				currentDir = System.getProperty("user.dir");
			return currentDir;
		}

		/**
		 * Check whether a text field entry is valid. This method ensures
		 * that Integer fields have numeric entries.
		 *
		 * @param  text content to validate
		 * @return Object of correct type if content is ok; null if invalid
		 */
		private Object checkText(String text) {
			Object result = text;               // default result is "ok"
			if (Integer.class.equals(cls)) {
				try {
					if (text.length() < 1)
						text = "0";
					int i = Integer.parseInt(text);
					result = new Integer(i);
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(this,
												  "Value for \"" +
												  editorLabel +
												  "\" must be an integer",
												  editorLabel,
												  JOptionPane.ERROR_MESSAGE);
					result = null;
				}
			}
			return result;
		}

		/**
		 * Called when another part of the application has updated this
		 * panel's Preference in the model: updates the display for this
		 * change.
		 *
		 * @param  newValue changed value for the editor widget
		 */
		void update(Object newValue) {
			if (newValue == null)        // strange case where application has
				return;                  // deleted this Preference
			try {
				if (cls.isEnum())
					comboBox.setSelectedItem(newValue);
				else if (Boolean.class == cls)
					cb.setSelected(((Boolean) newValue).booleanValue());
				else
					tf.setText(newValue.toString());
			} catch (ClassCastException cce) {
				cce.printStackTrace();
			}
		}
	}

	/** Margin width and height. */
	private static final int MARGIN = 10;

	private PreferencesModel model;

    private Element rootElement;

    private JSplitPane splitPane;

    private JButton okButton, applyButton, cancelButton;

    private JTree jtree;

    private DefaultMutableTreeNode topNode;

    private JPanel rightPanel;

	/**
	 * Set of EditorPanels created. Key is Preference name, value is
	 * EditorPanel for that Preference.
	 */
	private Map editors = null;

	/**
	 * Set of PropertyChangeEvents to fire after an editor session is
	 * complete. Key is Preference name, value is PropertyChangeEvent for
	 * that Preference.
	 */
	private Map editorEvents = null;

	/**
	 * Return a new instance of this class. Calls 
	 * {@link PreferencesModel#checkForMaxScopeInstance()} before constructor.
	 * @param model the model to display
	 * @param server UI context
	 * @return new instance
	 */
	public static PreferencesWindow create(PreferencesModel model, CTAT_Launcher server) {
		model.checkForMaxScopeInstance();
		return new PreferencesWindow(model, server);
	}

    //////////////////////////////////////////////////////////////////////
    //
    //
    //////////////////////////////////////////////////////////////////////
    PreferencesWindow(PreferencesModel model_a, CTAT_Launcher server) throws HeadlessException {
        super(server);
        serialNo = ++serialNoGenerator;
		model = model_a;
		if (trace.getDebugCode("pr")) trace.out ("pr", "model = " + model);
		if (model != null) {
			topNode = getDisplayTree();
			model.addPropertyChangeListener(this);
		}
		else
			loadXML();
		editors = new HashMap();
		editorEvents = new LinkedHashMap();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		if (trace.getDebugCode("pr")) trace.out("pr", "PreferencesWindow() constructor: serialNo " +serialNo);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if (!editorEvents.isEmpty()) {
					String warning = "There are uncommitted changes. Press\n" +
									 "Yes to commit the changes, No to cancel them.";
					int n =	JOptionPane.showConfirmDialog(PreferencesWindow.this,
														  warning,
														  "Commit Changes",
														  JOptionPane.YES_NO_OPTION,
														  JOptionPane.WARNING_MESSAGE);
					if (n == JOptionPane.YES_OPTION) {
						if (model != null)
							model.fireEditorChanges(this,
													editorEvents.values());
					}
					editorEvents.clear();
				}
				if (model != null)
					model.removePropertyChangeListener(PreferencesWindow.this);
				clearNodeEditorPanels();
			}
		});

        setupWindow();
        setDockable(false);
    }

    //////////////////////////////////////////////////////////////////////
    //
    //
    //////////////////////////////////////////////////////////////////////
    private void setupWindow() {

        splitPane = new JSplitPane();

        JPanel topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.setLayout(new BorderLayout());

        JPanel bottomPanel = new JPanel();

        okButton = new JButton("OK");
		okButton.addActionListener(this);
        applyButton = new JButton("Apply");
		applyButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);

        bottomPanel.add(okButton);
        bottomPanel.add(applyButton);
        bottomPanel.add(cancelButton);

        jtree = new JTree(topNode);
        //jtree.setRootVisible (false);
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        renderer.setLeafIcon(null);
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        jtree.setCellRenderer(renderer);
        jtree.putClientProperty("JTree.lineStyle", "Angled");
        jtree.addTreeSelectionListener(this);
        jtree.setMinimumSize(new Dimension (150, 50));
        for (int i = 0; i < jtree.getRowCount(); i++) {
            jtree.expandRow(i);
        }
        rightPanel = new JPanel();
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jtree,
								   rightPanel);
        topPanel.add(splitPane, BorderLayout.CENTER);

        splitPane.setSize(200, 100);

        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(topPanel, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        setSize(580, 480);
        setLocationRelativeTo(null);

        setTitle("CTAT Preferences");
        setName("Preferences Window");
        super.applyPreferences();
        super.storeLocation();
        super.storeSize();

        setCurrentPanel(model.getLatestCategory());
    }

    private void loadXML() {
        if (trace.getDebugCode("pr")) trace.out("pr", "reading xml");
        URL xmlURL = ClassLoader
                .getSystemResource("pact/Preferences/PreferencesPanel.xml");

        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        try {
            doc = builder.build(xmlURL);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        rootElement = doc.getRootElement();
        if (trace.getDebugCode("pr")) trace.out("pr", "root element = " + rootElement.getName());

        topNode = new DefaultMutableTreeNode("Category");

        Iterator children = rootElement.getChild("NodeList").getChildren()
                .iterator();

        while (children.hasNext()) {
            Element element = (Element) children.next();
            String name = element.getAttributeValue("id");
            if (trace.getDebugCode("pr")) trace.out("pr", "name = " + name);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
            topNode.insert(node, topNode.getChildCount());
            Iterator childChildren = element.getChildren().iterator();
            while (childChildren.hasNext()) {
                Element childElement = (Element) childChildren.next();
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
                        childElement.getAttributeValue("id"));
                node.add(childNode);
            }
        }
    }

    /**
     * Set the given panel name as the current panel.
     * If the panel name is not found, do nothing.
     * 
     * @param panelName
     */
    void setCurrentPanel (String panelName) {

    	Enumeration elements = topNode.breadthFirstEnumeration();
    	if (trace.getDebugCode("pr")) trace.out ("pr", "set current panel: panel name = " + panelName);
    	DefaultMutableTreeNode node = null;
    	int count = 0;
    	while (elements.hasMoreElements()) {
    		node = (DefaultMutableTreeNode) elements.nextElement();
    		String name =  ((GroupNode) node.getUserObject()).getName();
    		if (trace.getDebugCode("pr")) trace.out ("pr", "name = " + name + " count = " + count);
    		if (name.equals(panelName))
    			break;
    		count++;
    	}
    	if (node == null)
    		return;
    	jtree.setSelectionPath(new TreePath(node.getPath()));
    	setupRightPanel (node);
    	    	
    }
    
	/**
	 * Called when one of the {@link #okButton}, {@link #applyButton} or
	 * {@link #cancelButton}.
	 *
	 * @param  evt ActionEvent from button
	 */
	public void actionPerformed(ActionEvent evt) {
		if (trace.getDebugCode("pr")) trace.out("pr", ((JButton)evt.getSource()).getText() +
				  " button pressed, editorEvents.size() " +
				  editorEvents.size() +
				  ", .hashCode() " + editorEvents.hashCode() +
				  ", serialNo " + serialNo +
				  ", this.model " + this.model);

		// OK and Apply: fire all change events
		if (evt.getSource() == okButton || evt.getSource() == applyButton) {
			if (model != null)
				model.fireEditorChanges(this, editorEvents.values());
			editorEvents.clear();
		}

		// Cancel: for each change event, reset its EditorPanel to model value
		if (evt.getSource() == cancelButton) {
			for (Iterator it = editorEvents.values().iterator(); it.hasNext(); ) {
				Object obj = it.next();
				if (!(obj instanceof PropertyChangeEvent))
					continue;
				PropertyChangeEvent edEvt = (PropertyChangeEvent) obj;
				String propName = edEvt.getPropertyName();
				EditorPanel editor = (EditorPanel) editors.get(propName);
				if (editor != null && model != null)
					editor.update(model.getValue(propName));
			}
			editorEvents.clear();
		}
			
		if (evt.getSource() == okButton || evt.getSource() == cancelButton) {
			if (model != null)
				model.removePropertyChangeListener(this);
			clearNodeEditorPanels();
			setVisible(false);
		}
	}

	/**
	 * Call this method to force the tree to reread the model.
	 * Walks the tree and removes all the editorsPanel members
	 * from the gNodes. Then clears the rightPanel and calls setupRightPanel()
	 * with the last selected node to initialize the display.
	 */
	void rereadModel() {
		clearNodeEditorPanels();
		rightPanel.removeAll();
		rightPanel.validate();
		setupRightPanel((DefaultMutableTreeNode) jtree.getLastSelectedPathComponent());
	}

	/**
	 * Walk the tree and clear the editors panel from each group node.
	 */
    private void clearNodeEditorPanels() {
    	if(topNode == null)
    		return;
		for (Enumeration en = topNode.depthFirstEnumeration();
				 en.hasMoreElements(); )
			{
				DefaultMutableTreeNode node =
					(DefaultMutableTreeNode) en.nextElement();
				if (!(node.getUserObject() instanceof GroupNode))
					continue;
				GroupNode gNode = (GroupNode) node.getUserObject();
				if (trace.getDebugCode("pr")) trace.out("pr", "about to null gNode.editorsPanel " +
						  gNode.getEditorsPanel());
				gNode.setEditorsPanel(null);             // force rebuild on display
			}
	}

	/**
     * Called when user clicks on a node of the tree. If it's a leaf node,
	 * then display that node's editor panel. This saves the custom panel
	 * created in the tree so that it can be redisplayed later: this
	 * preserves the user's latest edits without writing them back to the
	 * source {@link PreferenceModel.Preference} object.
     * 
	 * @param arg0 triggering event 
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    public void valueChanged(TreeSelectionEvent arg0) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) jtree
                .getLastSelectedPathComponent();

        if (node == null)
            return;
        setupRightPanel(node);
	}

    /**
     * Rebuild the right-hand panel of the window. If argument is a leaf node,
	 * then display that node's editor panel. This saves the custom panel
	 * created in the tree so that it can be redisplayed later: this
	 * preserves the user's latest edits without writing them back to the
	 * source {@link PreferenceModel.Preference} object.
     * 
	 * @param  node TreeNode to expand
     */
	private void setupRightPanel(DefaultMutableTreeNode node) {
        if (node == null) return;

        if (!(node.getUserObject() instanceof GroupNode))
			return;

        if (node.isLeaf() == false) {
        	return;
        }

        DefaultMutableTreeNode parentNode =
			(DefaultMutableTreeNode) node.getParent();

        GroupNode gNode = (GroupNode) node.getUserObject();
                
        String name = (String) gNode.getName();
        String nameToShow = name;
        String parentName =
			((GroupNode) parentNode.getUserObject()).toString();
        model.setLatestCategory(name);
        if (! parentName.equals ("Category"))
            nameToShow = parentName + "--" + name;

		Border titleB = BorderFactory.createTitledBorder("<html><b>"+nameToShow+"</b></html>");
		Border marginB = BorderFactory.createEmptyBorder(MARGIN, MARGIN,
														 MARGIN, MARGIN);
		//Border border = BorderFactory.createCompoundBorder(titleB, marginB);
        rightPanel.setBorder(titleB);
		rightPanel.removeAll();
		rightPanel.validate();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		
        String desc = (String) gNode.getDescription();
        if (desc == null || desc.isEmpty()) {
        	// no description
        } else {
        	JPanel descPanel = new JPanel();
        	JLabel descLabel = new JLabel("<html><p>"+desc+"</p></html>");
        	descLabel.setName("descLabel");
        	descPanel.add(descLabel);
        	descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.X_AXIS));
        	descPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        	rightPanel.add(descPanel);
        }

		JPanel editorsPanel = null;
		if(trace.getDebugCode("pr"))
			trace.out("pr", "setupRightPanel() gNode "+gNode+" .editorsPanel "+
					(gNode == null || gNode.getEditorsPanel() == null ? null : gNode.getEditorsPanel().hashCode())+
					"; serialNo "+serialNo);
		if (gNode.getEditorsPanel() instanceof javax.swing.JPanel) {
			editorsPanel = (JPanel) gNode.getEditorsPanel();
		} else {
			editorsPanel = new JPanel();
			editorsPanel.setLayout(new BoxLayout(editorsPanel,
					 BoxLayout.Y_AXIS));
			for (Iterator it = gNode.iterator(); it.hasNext(); ) {
				PreferencesModel.Preference pref =
					(PreferencesModel.Preference) it.next();
				EditorPanel editor = new EditorPanel(pref, this);
				if (trace.getDebugCode("pr"))
					trace.out ("pr", "editor panel min, pref, max size = "+
							editor.getMinimumSize()+", "+editor.getPreferredSize()+", "+editor.getMaximumSize());
				editorsPanel.add(editor);
//				/editorsPanel.add (Box.createVerticalStrut(10));
				editors.put(pref.getName(), editor);
			}
			gNode.setEditorsPanel(editorsPanel);
		}
		
		JScrollPane sp = new JScrollPane (editorsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
		        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		rightPanel.add(sp);
		//rightPanel.add(editorsPanel);
		rightPanel.validate();
    }

	/**
	 * Return the tree of nodes for display. From the root of the tree
	 * given by {@link #getDisplayRoot()}, build a tree of
	 * {@link javax.swing.tree.DefaultMutableTreeNode}s.
	 *
	 * @return displayRoot as TreeNode 
	 */
	DefaultMutableTreeNode getDisplayTree() {
		GroupNode root = model.getDisplayRoot();
		if (root == null)
			return null;
		if (trace.getDebugCode("pr")) trace.out("pr", root.prettyPrint());
		return getDisplayTree(root, null);
	}

	/**
	 * Build a subtree of nodes for display. Given a node and the parent
	 * output node, add this node as a child of the parent and, for each
	 * child node, do likewise.
	 *
	 * @param  node GroupNode to add; must be either GroupNode or Preference
	 * @param  outputParent parent's output node
	 * @return displayRoot as TreeNode 
	 */
	private DefaultMutableTreeNode getDisplayTree(GroupNode node,
												  DefaultMutableTreeNode outputParent) {
		DefaultMutableTreeNode outputNode =
			new DefaultMutableTreeNode(node);
		if (outputParent != null)
			outputParent.insert(outputNode, outputParent.getChildCount());
		for (Iterator it = node.iterator(); it.hasNext(); ) {
			Object child = it.next();
			if (trace.getDebugCode("pr")) trace.out("pr", "getDisplayTree: parent " + outputParent +
					  ", node " + node +
					  ", child " + child);
			if (child instanceof GroupNode)                      // omit prefs
				getDisplayTree((GroupNode) child, outputNode);
		}
		return outputNode;
	}

	/**
	 * Called by <ul>
	 * <li>editor widget when individual property has been changed: adds
	 *     this change to the set to be sent when user presses OK or Apply;
	 * <li>model when setting a Preference in response to the OK or Apply
	 *     button: in this case the source of the change is this object;
	 * <li>model when some other object in the application has changed a
	 *     Preference item: in this case we revise the editor (if any)
	 *     for that item.
	 * </ul>
	 *
	 * @param  evt property change event
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();

		if (evt.getSource() instanceof EditorPanel) { // user just changed item
			PropertyChangeEvent oldEvt =
				(PropertyChangeEvent) editorEvents.put(propName, evt);
			if (trace.getDebugCode("pr"))
				trace.out("pr", "editorEvents.size() " + editorEvents.size() +
						", .hashCode() " + editorEvents.hashCode() +
						", serialNo " + serialNo +
						", this.model " + this.model +
						": changed " + propName + ", new value " +
						evt.getNewValue() +
						(oldEvt == null ?
								"" : ", prior change " + oldEvt.getNewValue()));

		} else if (evt.getSource() == this) {
			;          // no-op: callback from model on change from this window

		} else {     // other part of application changed value: update display
			EditorPanel editor = (EditorPanel) editors.get(propName);
			if (editor != null)
				editor.update(evt.getNewValue());
		}
	}
}
