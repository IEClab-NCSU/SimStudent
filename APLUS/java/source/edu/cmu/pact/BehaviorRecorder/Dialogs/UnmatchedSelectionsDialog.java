package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageBuilder;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelListener;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ExampleTracerLink;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.model.StartStateModel;

/**
 * A dialog to locate references in a .brd file to components not found in the currently-connected
 * student interface. The original name for this dialogue was "ObsoleteSelectionDialog"; hence many of
 * the identifiers use "obsolete" for what we now call "unmatched."
 * External API to this dialog is meant to avoid having runtime server code link the Swing
 * libraries and to build the dialog in a separate thread.  
 */
public class UnmatchedSelectionsDialog implements StartStateModel.Listener, ProblemModelListener {

	/** Dialog title, perhaps also useful for menu label. */
	public static final String SHOW_UNMATCHED_COMPONENT_REFERENCES = "Show Unmatched Component References";
	
	/** Thread id generator. */
	private static int threadId = 0;

	/** Linkage to {@link ProblemModel}. */
	private final UniversalToolProxy utp;

	/** The UI. */
	private volatile Dialog dialog = null;

	/** Thread currently building the UI, if any. */
	private volatile Thread creatingThread = null;

	/** True if {@link #launch()} has been called while the display was under construction. */
	private volatile boolean launchRequested = false;

	/** Number of requests outstanding to rerun the creation thread. */
	private volatile int rerunRequests = 0;

	/** Whether the dialog is ready to display. */
	private volatile boolean dialogReady = false;

	/** Component names found in the student interface: key is component instance name, value is type. */
	private Map<String, String> interfaceCompsMap = null;

	/** Component names found in the graph but not in the interface. */
	private Map<String, List<Reference>> obsoleteReferencesMap =
			new LinkedHashMap<String, List<Reference>>();

	/**
	 * Registers this instance as a {@link StartStateModel.Listener}. 
	 * @param utp where the listener list is maintained 
	 */
	public UnmatchedSelectionsDialog(UniversalToolProxy utp) {
		this.utp = utp;
		utp.addStartStateListener(this);
		if(trace.getDebugCode("obssel"))
			trace.out("obssel", "OSD() utp "+trace.nh(utp));
	}

	/**
	 * Spawn a new thread to create the dialog. The {@link #dialog} field is not set
	 * until the constructor finishes, for the analysis may reveal that the dialog has
	 * nothing to show. Method {@link #problemModelEventOccurred(ProblemModelEvent)}
	 * can interrupt this thread.
	 * @param ssm StartStateModel to query
	 */
	void checkForUnmatchedReferences(final StartStateModel ssm) {
		if(!utp.getStudentInterfaceConnectionStatus().isConnected())
			return;
		if(utp.getController().getProblemModel() == null ||
				utp.getController().getProblemModel().getStartNode() == null)
			return;				

		Thread t = new Thread(new Runnable() {
			public void run() {
				int runCount = 0, exceptionCount = 0;
				do {
					++runCount;
					try {
						if(trace.getDebugCode("obssel"))
							trace.out("obssel", "USD.checkForUnmatchedReferences() top of loop: rerunRequests "+
									rerunRequests+", runCount "+
									runCount+", exceptionCount "+exceptionCount);

						rerunRequests = 0;
						int[] refCounts = buildObsoleteReferenceMap(utp, ssm);
						if(trace.getDebugCode("obssel"))
							trace.out("obssel", String.format("USD.checkForUnmatchedReferences() after build():"+
									" rerunRequests %d, runCount %d, map size %d, refCounts[%d,%d]",
									rerunRequests, runCount, obsoleteReferencesMap.size(), refCounts[0], refCounts[1]));

					} catch(Exception ie) {  // see interruptCreateThread()
						++exceptionCount;
						trace.err("UnmatchedSelectionsDialog create thread "+Thread.currentThread()+
								" stopped on exception #"+exceptionCount+":"+ie+", cause "+ie.getCause());
						if(dialog != null)
							dialog.dispose();
						dialog = null;
					}
					if(trace.getDebugCode("obssel"))
						trace.out("obssel", "USD.checkForUnmatchedReferences() bottom of loop: rerunRequests "+
								rerunRequests+", runCount "+
								runCount+", exceptionCount "+exceptionCount);
					synchronized(this) {
						if(rerunRequests == 0) {
							utp.notifyUnmatchedSelectionsDialogAvailable(hasDialog());
							creatingThread = null;            // thread about to exit
							return;
						}
					}
				} while(true);
			}
		}, "UnmatchedSelectionsDialog"+(++threadId));
		synchronized(this) {
			if(trace.getDebugCode("obssel"))
				trace.out("obssel", "OSD.checkForUnmatchedReferences() ssm "+trace.nh(ssm)+", thread "+t);
			creatingThread = t;
			t.start();
		}
	}

	/**
	 * Display {@link #dialog} unless already displayed. If null, show a message instead
	 * telling the user there's nothing to show. If {@link #creatingThread} is not null,
	 * just return awaiting completion of the current display.
	 */
	public synchronized void launch() {
		if(trace.getDebugCode("obssel"))
			trace.out("obssel", "OSD.launch() dialog "+trace.nh(dialog)+
					", dialogReady "+dialogReady+", creatingThread "+creatingThread);
		if(creatingThread != null)
			launchRequested = true;              // dialog will get launched from other thread
		else if(dialog != null) {
			if(dialog.isVisible())
				return;
			if(!dialogReady)
				launchRequested = true;          // dialog will get launched from other thread
		} else if(!hasDialog()) {
			JOptionPane.showMessageDialog(utp.getController().getCtatFrameController().getDockedFrame(),
					"Found no unmatched component references in the graph.",
					"No Unmatched Component References", JOptionPane.INFORMATION_MESSAGE);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JFrame parent = utp.getController().getCtatFrameController().getDockedFrame();
					dialog = new Dialog(parent, utp.getStartStateModel());
					dialog.setVisible(true);
				}
			});
		}
	}

	/**
	 * @return true if {@link #obsoleteReferencesMap} is nonempty
	 */
	public boolean hasDialog() {
		return obsoleteReferencesMap.size() > 0;
	}

	/**
	 * Set {@link #dialog} and notify the author UI that the dialog is available.
	 * @param dialogReady new value for {@link #dialogReady}
	 */
	public synchronized void setDialogReady(boolean dialogReady) {
		boolean launchRequested = this.launchRequested;  // get prior value, then clear the field 
		this.launchRequested = false;                  
		this.dialogReady = dialogReady;
		utp.notifyUnmatchedSelectionsDialogAvailable(dialogReady);
		if(dialog != null && dialogReady && launchRequested) {
			if(!dialog.isVisible()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						dialog.setVisible(true);
					}
				});
			}				
		}
	}

	/**
	 * Clear the {@link #dialog}, then asynchronously redo the analysis and recreate the dialog.
	 * Call this after a user edit. 
	 */
	void recalculate() {
		setDialogReady(false);                         // notifies listeners
		init(utp.getStartStateModel());                // completes in other thread
	}

	/**
	 * Call {@link #init(StartStateModel)} to create or recreate the dialog. 
	 * @param e not used: gets {@link StartStateModel} for init() from {@link #utp}
	 */
	public void problemModelEventOccurred(ProblemModelEvent e) {
		if(trace.getDebugCode("obssel"))
			trace.out("obssel", "OSD.problemModelEventOccurred() evt "+trace.nh(e)+", source "+e.getSource());
		init(utp.getStartStateModel());
	}

	/**
	 * Call {@link #init(StartStateModel)} to create or recreate the dialog. 
	 * @param evt {@link EventObject#getSource()} must be of type {@link StartStateMode}
	 * @see edu.cmu.pact.ctat.model.StartStateModel.Listener#startStateReceived(java.util.EventObject)
	 */
	public void startStateReceived(EventObject evt) {
		if(trace.getDebugCode("obssel"))
			trace.out("obssel", "OSD.startStateReceived() evt.source "+trace.nh(evt.getSource()));
		init((StartStateModel) evt.getSource());
	}

	/**
	 * If a dialog is currently being displayed, do nothing, since {@link #recalculate()} will
	 * be called when the user closes the dialog. Otherwise if a dialog exists, discard and
	 * see if we need to replace it. If a check for unmatched components is underway,
	 * {@link #interruptCreateThread(int)} and restart it.
	 * @param ssm
	 */
	private void init(StartStateModel ssm) {
		if(trace.getDebugCode("obssel"))
			trace.out("obssel", "USD.init("+trace.nh(ssm)+") dialog "+trace.nh(dialog)+
					", .visible "+(dialog == null ? "null" : String.valueOf(dialog.isVisible()))+
					", creatingThread "+creatingThread+", rerunRequests "+rerunRequests);
		if(interruptCreatingThread())              // no-op if creatingThread null
			return;
		if(dialog != null) {
			if(dialog.isVisible())
				return;                            // will recalculate when dialog closes
			setDialogReady(false);
			dialog.dispose();
			dialog = null;                         // fall through to call checkForUnmatchedReferences()
		}
		checkForUnmatchedReferences(ssm);
	}

	/**
	 * If a thread to create the dialog is running, stop it and call {@link #recalculate()}.
	 * @return true if {@link #creatingThread} was set; false (no-op) if not
	 */
	private synchronized boolean interruptCreatingThread() {
		if(creatingThread == null)
			return false;
		rerunRequests++;
		creatingThread.interrupt();
		return true;
	}

	/**
	 * Populate {@link #obsoleteReferencesMap} via
	 * {@link #checkAndAddReference(int, String, MessageObject)}.
	 * @param utp
	 * @param ssm
	 * @return int[{@value #LINK}] with count of link references;
	 *         int[{@value #SS}] with count of start state references
	 */
	private int[] buildObsoleteReferenceMap(UniversalToolProxy utp, StartStateModel ssm) {
		obsoleteReferencesMap.clear();
		int[] result = new int[NTBLS];

		interfaceCompsMap = ssm.getInterfaceComponentsMap();
		ProblemModel pm = utp.getController().getProblemModel();
		if(trace.getDebugCode("obssel"))
			trace.out("obssel", "OSD.buildObsoleteReferenceMap() interfaceCompsMap size "+
					(interfaceCompsMap == null ? -1 : interfaceCompsMap.size())+
					", pm "+trace.nh(pm)+" name "+(pm == null ? "" : pm.getProblemName()));

		if(interfaceCompsMap.size() < 1)
			return result;                            // no components in interface?
		if(pm == null)
			return result;

		result[SS] = 0;
		Iterator<MessageObject> pmIter = pm.startNodeMessagesIterator();
		while(pmIter.hasNext())
			result[SS] += checkAndAddReference(SS, "Start State", pmIter.next());

		result[LINK] = 0;
		if(pm.getExampleTracerGraph() != null) {
			for(ExampleTracerLink link: pm.getExampleTracerGraph().getLinks()) {
				EdgeData linkData = link.getEdge();
				result[LINK] += checkAndAddReference(LINK, "Link "+link.getID(), 
						PseudoTutorMessageBuilder.buildToolInterfaceAction(linkData.getSelection(),
								linkData.getAction(), linkData.getInput(),
								PseudoTutorMessageBuilder.TRIGGER_DATA, null));
			}
		}
		return result;
	}

	/**
	 * Create or update an entry in {@link #obsoleteReferencesMap}.
	 * @param ssOrLink {@value #SS} if this is a start state message; {@value #LINK} if from a link
	 * @param location name for this row in the table, giving the msg's location in the graph
	 * @param msg {@value MsgType#INTERFACE_ACTION} message whose selection is the map key
	 * @return 1 if an obsolete reference; 0 if not InterfaceAction or selection in {@link #interfaceCompsMap}
	 */
	private int checkAndAddReference(int ssOrLink, String location, MessageObject msg) {
		String key = null;
		int result = -1;
		List<Reference> refs = null;
		if(!MsgType.INTERFACE_ACTION.equalsIgnoreCase(msg.getMessageType()))
			result = 0;
		else {			
			if(null == (key = msg.getSelection0()))
				result = 0;
			else if(interfaceCompsMap.containsKey(key))
				result = 0;                        // in student interface => not an obsolete selection
			else {
				refs = obsoleteReferencesMap.get(key);
				if(refs == null) {
					refs = new ArrayList<Reference>();
					obsoleteReferencesMap.put(key, refs);
				}
				refs.add(new Reference(key, ssOrLink, location, msg));
				result = 1;
			}
		}
		if(trace.getDebugCode("obssel"))
			trace.out("obssel", String.format("OSD.checkAndAddReference(%b, %s, %s) returns %d, %d refs, map size %d",
					ssOrLink, location, msg.summary(), result, (refs == null ? -1 : refs.size()),
					obsoleteReferencesMap.size()));
		return result;
	}

	/** A button to indicate whether this dialog has data. Initialized as if no data is available. */
	private static final JButton defaultButton = new JButton(" ") {
		public Dimension getMinimumSize() { return getPreferredSize(); }
		public Dimension getMaximumSize() { return getPreferredSize(); }
	};

	/** A graphic to indicate that this dialog has data to show and should be enabled. */
	private static ImageIcon dialogAvailableIcon = null;
	
	static {                  // initialize dialogAvailableIcon, defaultButton
		final String path = "/"+UnmatchedSelectionsDialog.class.getPackage().getName().replace('.', '/');
		String baseName = "xmark.png";
		URL url = null;
		String fileName = path+"/"+baseName;
		try {
			if(null != (url = UnmatchedSelectionsDialog.class.getResource(fileName)))
				dialogAvailableIcon = new ImageIcon(url);
			if(trace.getDebugCode("obssel"))
				trace.out("obssel", "ObsSelDialog: image \""+fileName+"\"; url "+url+", icon "+dialogAvailableIcon);
		} catch(Exception e) {
			trace.errStack("Error loading image \""+fileName+"\"; url "+url+", icon "+dialogAvailableIcon, e);
		}
		Dimension size = new Dimension(dialogAvailableIcon.getIconWidth(), dialogAvailableIcon.getIconHeight());
		defaultButton.setPreferredSize(size);
		defaultButton.setHorizontalTextPosition(JButton.CENTER);
		defaultButton.setIconTextGap(0);
		defaultButton.setBorderPainted(false);
		defaultButton.setFocusPainted(false);
		defaultButton.setBorder(null);
		defaultButton.setMargin(new Insets(0, 0, 0, 0));
		
		updateDialogAvailableButton(false, defaultButton);
	}

	/**
	 * @return {@link #defaultButton}
	 */
	public static JButton getDefaultButton() {
		return defaultButton;
	}

	/**
	 * Alter the appearance of the given button depending on whether this dialog is enabled.
	 * @param enabled true if the {@link #dialogAvailableIcon} should be enabled; false if not
	 * @param button button to update
	 * @return  {@link #button}
	 */
	public static JButton updateDialogAvailableButton(boolean enabled, JButton button) {
		button.setEnabled(enabled);
		if(enabled) {
			button.setText(null);
			button.setIcon(dialogAvailableIcon);
			button.setPressedIcon(dialogAvailableIcon);
			button.setDisabledIcon(dialogAvailableIcon);
			button.setDisabledSelectedIcon(dialogAvailableIcon);
			button.setToolTipText("<html>Click to display <i>"+SHOW_UNMATCHED_COMPONENT_REFERENCES+"</i>"+
					"<br />to find selections in the graph but not in the student interface.</html>");
		} else {
			button.setText(" ");
			button.setIcon(null);
			button.setPressedIcon(null);
			button.setDisabledIcon(null);
			button.setDisabledSelectedIcon(null);
			button.setToolTipText(null);
		}
		button.setVisible(enabled);
		return button;
	}

	/** A reference to a selection name. */
	class Reference {
		
		/**
		 * {@value UnmatchedSelectionsDialog#SS} if msg from start state;
		 * {@value UnmatchedSelectionsDialog#LINK} if from link.
		 */                                 final int    ssOrLink;
		/** Selection[0] from msg. */       final String key;
		/** Link name. */                   final String location;
		/** Selection[0] from msg. */       final String selection;
		/** Action[0] from msg. */          final String action;
		/** Input[0] from msg. */           final String input;
		/** Whether marked for removal. */  boolean removed = false;
		/** Original message. */            MessageObject msg = null;

		/**
		 * Set all fields.
		 * @param key value for #key
		 * @param ssOrLink value for {@link #ssOrLink}
		 * @param location value for {@link #location}
		 * @param msg message with {@link #action} and {@link #input} values
		 */
		Reference(String key, int ssOrLink, String location, MessageObject msg) {
			this.key = key;
			this.ssOrLink = ssOrLink;
			this.location = location;
			selection = msg.getSelection0();
			action = msg.getAction0();
			input = msg.getInput0();
			this.msg = msg;
		}

		/**
		 * @param value new value for {@link #removed}; if null, value is false
		 */
		public void setRemoved(Object value) {
			if(value == null)
				removed = false;
			else {
				removed = Boolean.parseBoolean(value.toString());
				okButton.setEnabled(true);
			}
		}
	}

	/** Index for table showing references from the graph's start state. */
	private static final int SS = 0;

	/** Index for table showing references from links in the graph. */
	private static final int LINK = 1;

	/** Max number of tables. */
	private static final int NTBLS = LINK+1;

	/** Sections of the .brd file to analyze. */
	private static final String Sources[] = { "StartState", "Links" };

	static final int SELECTION_COL = 0;
	static final int ACTION_COL = 1;
	static final int INPUT_COL = 2;
	static final int LINK_OR_REMOVED_COL = 3;

	static final String[][] columnLabels = {
		{ "Selection" },           // SELECTION_COL = 0;
		{ "Action" },              // ACTION_COL = 1;
		{ "Input" },               // INPUT_COL = 2;
		{ "Remove?", "Link " }     // LINK_OR_REMOVED_COL = 3: 
	};

	/** Button to commit changes and close the dialog. */
	private JButton okButton;

	/** Button to close the dialog with changes uncommitted. */
	private JButton cancelButton; 
	
	/**
	 * The UI itself.
	 */
	private class Dialog extends JDialog implements ActionListener {

		class ReferenceTableModel extends AbstractTableModel {

			private static final long serialVersionUID = 201404250229L;

			/** The table data. */
			final List<Reference> refs;

			/**
			 * {@value UnmatchedSelectionsDialog#SS} for a table of start state messages;
			 * {@value UnmatchedSelectionsDialog#LINK} for table of link references.
			 */
			final int ssOrLink;

			/**
			 * @param key to retrieve {@link Reference} from {@link UnmatchedSelectionsDialog#obsoleteReferencesMap}
			 * @param ssOrLink true for a table of start state messages; false for table of link references
			 */
			ReferenceTableModel(String key, int ssOrLink) {
				this.ssOrLink = ssOrLink;
				refs = new ArrayList<Reference>();
				List<Reference> allRefs = obsoleteReferencesMap.get(key);
				if(allRefs == null || allRefs.isEmpty())
					return;
				for(Reference ref : allRefs) {
					if(ref.ssOrLink == ssOrLink)
						refs.add(ref);
				}
			}

			public String getColumnName(int col) {
				if(col == LINK_OR_REMOVED_COL)
					return columnLabels[col][ssOrLink];
				return columnLabels[col][0];
			}

			public int getRowCount() {
				return refs.size();
			}

			public int getColumnCount() {
				return columnLabels.length;
			}

			public boolean isCellEditable(int row, int col) {
				return (col == LINK_OR_REMOVED_COL && refs.get(row).ssOrLink == SS);
			}

			public void setValueAt(Object value, int row, int col) {
				if(col != LINK_OR_REMOVED_COL || refs.get(row).ssOrLink != SS)
					return;
				refs.get(row).setRemoved(value);
			}

			public Object getValueAt(int row, int col) {
				Reference ref = refs.get(row);
				switch(col) {
				case SELECTION_COL:       return ref.selection;
				case ACTION_COL:          return ref.action;
				case INPUT_COL:           return ref.input;
				case LINK_OR_REMOVED_COL: return (ssOrLink == SS ? ref.removed : ref.location);
				default:
					throw new IllegalArgumentException("Invalid column "+col+
							" to ReferenceTableModel.getValueAt(); should be in range 0-"+(getColumnCount()-1));
				}
			}

			public Class<?> getColumnClass(int col) {
				switch(col) {
				case SELECTION_COL:       return String.class;
				case ACTION_COL:          return String.class;
				case INPUT_COL:           return String.class;
				case LINK_OR_REMOVED_COL: return (ssOrLink == SS ? Boolean.class : String.class);
				default:
					throw new IllegalArgumentException("Invalid column "+col+
							" to ReferenceTableModel.getColumnClass(); should be in range 0-"+(getColumnCount()-1));
				}
			}
		}


		/** For compiler warning. */
		private static final long serialVersionUID = 201404241730L;

		/** Name of action for author logging. */
		protected static final String SELECT_COMPONENT = "SELECT_COMPONENT";

		/** Start state model used. */
		private final StartStateModel ssm;

		/** List of obsolete component names, with means for choosing one of interest. */
		private JComboBox obsoleteComponentsCB;

		/** Panel holding 1 or more tables of {@link Reference}s. */
		private Component tablesPanel;

		/** Holds all the components. */
		private JPanel mainPanel;

		/** Height of a line of text in the chosen font, for sizing labels. */
		private int lineHeight;

		/** Width of a line of text in the chosen font, for sizing labels. */
		private int lineWidth;

		/** Desired number of characters on a line, for sizing labels. */
		private int lineLength;

		/** Panel for intro text and combobox. */
		private JPanel cbPanel = null;

		/** Panel for OK, Cancel buttons. */
		private JPanel buttonPanel = null;

		/** Most-recently selected component. */
		private int selectedIndex  = 0;

		/** HTML opening tag in labels. */
		private static final String labelHtmlTag0 = "<html><p><font face=\"sans-serif\"/>";

		/** HTML closing tag in labels. */
		private static final String labelHtmlTag1 = "</font></p></html>";

		/**
		 * @param parent owning frame for {@link JDialog#JDialog(JFrame, String)}
		 * @param ssm source of interface components
		 */
		public Dialog(final JFrame parent, StartStateModel ssm) {
			super(parent, SHOW_UNMATCHED_COMPONENT_REFERENCES);
			if(trace.getDebugCode("obssel"))
				trace.out("obssel", "UnmatchedSelectionsDialog() constructor");
			this.ssm = ssm;

			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);  // see WindowAdapter below
			addWindowListener(new WindowAdapter() {
				/**
				 * Simulate user pressing the {@link UnmatchedSelectionsDialog#cancelButton}.
				 * @param evt unused
				 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
				 */
				public void windowClosing(WindowEvent evt) {
					int nChanges = commitChanges(false);
					if(nChanges > 0) {
						int okCancel = JOptionPane.showConfirmDialog(parent,
								"You have unsaved changes to the list of unmatched components. Discard them?",
								"Discard Changes to Unmatched Components",
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
						if(okCancel != JOptionPane.OK_OPTION)
							return;
					}
					cancelButton.doClick();
				}
			});

			Point p = parent.getLocation();  // display dialog at 60-pixel offset from parent window
			p.move(p.x+60, p.y+60);
			setLocation(p);

			int n = obsoleteReferencesMap.size();
			String line1 = String.format("The graph contains references to %d component %s not in ",
					n, (n > 1 ? "names that are" : "name that is"));
			lineLength = line1.length();
			JLabel obsCompCaption =
					new JLabel(labelHtmlTag0+line1+
							"the student interface. This can result from modifying components in the "+
							"interface after the graph was created or from accidentally opening a graph "+
							"built for a different interface. Select a component name from this drop-down "+
							"list to find where it is referenced in the graph."+
							labelHtmlTag1);
			obsCompCaption.setAlignmentX(CENTER_ALIGNMENT);
			FontMetrics fm = obsCompCaption.getFontMetrics(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
			lineWidth = fm.stringWidth(line1);
			lineHeight = fm.getHeight();
			int markup = labelHtmlTag0.length()+labelHtmlTag1.length();
			obsCompCaption.setPreferredSize(new Dimension(lineWidth,
					((obsCompCaption.getText().length()-markup+lineLength-1)/lineLength)*lineHeight));
			obsCompCaption.setBorder(BorderFactory.createEmptyBorder(0,0,4,0));
			obsCompCaption.setOpaque(true);

			ComboBoxModel obsCompModel =
					new DefaultComboBoxModel(obsoleteReferencesMap.keySet().toArray(new String[0]));
			obsoleteComponentsCB = new JComboBox(obsCompModel) {
				public Dimension getMaximumSize() {
					return getPreferredSize();
				}
			};
			obsoleteComponentsCB.setFont(new Font("Dialog", Font.PLAIN, 12));
			obsoleteComponentsCB.setBorder(BorderFactory.createEmptyBorder(0,4,0,4));
			obsoleteComponentsCB.setName("obsoleteComponentComboBox");
			obsoleteComponentsCB.setToolTipText("Click on a component name to see where it is referenced in the graph.");
			obsoleteComponentsCB.setSelectedIndex(selectedIndex  );

			cbPanel = new JPanel(new GridBagLayout());
			cbPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(8,4,4,4),
					BorderFactory.createCompoundBorder(
							BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
									"Unmatched Component Names", TitledBorder.LEADING, TitledBorder.TOP,
									new Font(Font.SANS_SERIF, Font.BOLD, 12)),
									BorderFactory.createEmptyBorder(0,6,4,6))));
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			cbPanel.add(obsCompCaption, c);
			c.gridy = 1;
			c.weightx = c.weighty = 0;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.CENTER;
			cbPanel.add(obsoleteComponentsCB, c);

			okButton = new JButton("OK");
			okButton.setEnabled(false);
			okButton.addActionListener(this);

			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(this);

			buttonPanel  = new JPanel(new GridLayout(1,2)) {
				public Dimension getMaximumSize() {
					return getPreferredSize();
				}
			};
			buttonPanel.add(okButton);
			buttonPanel.add(cancelButton);
			buttonPanel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));

			mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
			mainPanel.add(cbPanel);
			tablesPanel = createReferenceTablesPanel(selectedIndex);
			mainPanel.add(tablesPanel);
			mainPanel.add(buttonPanel);


			getContentPane().add(mainPanel);
			//		setSize(new Dimension(350,250)); FIXME?
			pack();

			obsoleteComponentsCB.addActionListener(new ActionListener() {
				/**
				 * Rebuild {@link UnmatchedSelectionsDialog#tablesPanel} for the newly-selected item in
				 * {@link UnmatchedSelectionsDialog#obsoleteComponentsCB}.
				 */
				public void actionPerformed(ActionEvent evt) {
					selectedIndex = obsoleteComponentsCB.getSelectedIndex();
					Object selectedItem = obsoleteComponentsCB.getSelectedItem();
					if(selectedItem == null)
						return;
					rebuildTables();
					utp.getController().getServer().getLoggingSupport().authorActionLog(
							AuthorActionLog.BEHAVIOR_RECORDER, SELECT_COMPONENT,
							obsoleteComponentsCB.getSelectedItem().toString());
				}

			});

			setDialogReady(true);  // delay until ready to setVisible(); see launch()
		}

		/**
		 * Remove, recreate and add again the tables of references for the currently selected item. 
		 */
		private void rebuildTables() {
			final Component newTblsPanel = createReferenceTablesPanel(selectedIndex);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					mainPanel.remove(buttonPanel);
					mainPanel.remove(tablesPanel);
					tablesPanel = newTblsPanel;
					mainPanel.add(tablesPanel);
					mainPanel.add(buttonPanel);
					pack();
				}
			});
		}

		/**
		 * Generate tables of {@link Reference}s for a given component name.
		 * @param selection component name
		 * @param lineWidth preferred width of labels
		 * @param lineHeight height of single label line
		 * @return replacement for {@link #tablesPanel}
		 */
		private Component createReferenceTablesPanel(int selectedIndex) {
			String selection = obsoleteComponentsCB.getItemAt(selectedIndex).toString();
			JPanel panel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1;
			c.weighty = 1;

			JLabel[] captions = {
					new JLabel(labelHtmlTag0+
							"Click the check boxes below to remove start-state information "+
							"from the graph for component <b>"+selection+"</b>, "+
							"which may have been removed or renamed. After removing references, "+
							"you can re-demonstrate the start state for the problem as needed."+
							labelHtmlTag1),                    // SS
							new JLabel(labelHtmlTag0+
									"See the Link column below to find links in the graph for component "+
									"<b>"+selection+"</b>. Revise the links if the component was renamed; "+
									"delete the links if the component was removed from the interface."+
									labelHtmlTag1)                     // LINK
			};
			String[] titles = {
					"References in the Start State",           // SS
					"References in Links "	                   // LINK
			};

			for(int i = 0; i < NTBLS; ++i) {
				Box box = new Box(BoxLayout.PAGE_AXIS);

				TableModel refTblModel = new ReferenceTableModel(selection, i);
				//			if(refTblModel.getRowCount() < 1)
				//				continue;                                  show empty tables

				captions[i].setName("CaptionForTableOfReferencesFrom"+Sources[i]);
				captions[i].setAlignmentX(CENTER_ALIGNMENT);
				captions[i].setBorder(BorderFactory.createEmptyBorder(4,6,4,6));
				int markup = labelHtmlTag0.length()+labelHtmlTag1.length()+"<b></b>".length();
				captions[i].setPreferredSize(new Dimension(lineWidth,
						((captions[i].getText().length()-markup+lineLength-1)/lineLength)*lineHeight));
				captions[i].setOpaque(true);

				JTable refTbl = new JTable(refTblModel, new ImmovableColumnColumnModel());
				refTbl.setAutoCreateColumnsFromModel(true);  // needed since specified column model in constructor);
				refTbl.setName("TableOfReferencesFrom"+Sources[i]);
				refTbl.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				refTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

				JScrollPane refTblPane = new JScrollPane(refTbl);
				refTblPane.setPreferredSize(new Dimension(250,100));  //FIXME?
				refTblPane.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(4,4,4,4),
						BorderFactory.createCompoundBorder(
								BorderFactory.createLineBorder(Color.black),
								BorderFactory.createEmptyBorder(0,4,0,4))));

				box.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(4,2,0,2),
						BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),
								titles[i], TitledBorder.LEADING, TitledBorder.TOP,
								new Font(Font.SANS_SERIF, Font.BOLD, 12))));
				box.add(captions[i]);
				box.add(refTblPane);

				c.gridy = i;
				panel.add(box, c);
			}

			panel.setOpaque(true);
			return panel;
		}

		/**
		 * Handle the OK and Cancel buttons. OK calls {@link #commitChanges()} to send changes
		 * to {@link StartStateModel#discardMessage(MessageObject)}. Both buttons call {@link #dispose()}.
		 * @param evt
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent evt) {
			int nChanges = 0;
			if("OK".equalsIgnoreCase(evt.getActionCommand()))
				nChanges = commitChanges(true);
			else if(!"Cancel".equalsIgnoreCase(evt.getActionCommand()))
				return;
			dispose();
			dialog = null;
			recalculate();                // revise to show changes
		}

		/**
		 * For each {@link Reference} in {@link #obsoleteReferencesMap}, if {@link Reference#removed},
		 * add to a list for {@link StartStateModel#applyEditsToProblemModel(ProblemModel, Set, String)}
		 * @param execute if false, just count the uncommitted changes; don't call
		 *        {@link StartStateModel#applyEditsToProblemModel(ProblemModel, Set, String)}
		 * @return number of deletions performed
		 */
		private int commitChanges(boolean execute) {
			Set<MessageObject> changes = new LinkedHashSet<MessageObject>();
			for(Map.Entry<String, List<Reference>> entry: obsoleteReferencesMap.entrySet()) {
				for(Reference ref : entry.getValue()) {
					if(ref.removed)
						changes.add(ref.msg);
				}
			}
			if(execute)
				ssm.applyEditsToProblemModel(utp.getController().getProblemModel(), changes,
						"Remove unmatched selections");
			return changes.size();
		}
	}
}
