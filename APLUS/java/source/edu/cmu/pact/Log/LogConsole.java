/*
 * Copyright 2007 Carnegie Mellon University.
 */

package edu.cmu.pact.Log;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.oli.log.client.Log;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.DialogUtilities;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeCreatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeCreationFailedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelListener;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTabManager;
import edu.cmu.pact.Log.TutorActionLog.ActionEvaluation;
import edu.cmu.pact.Log.TutorActionLog.SemanticEvent;
import edu.cmu.pact.Log.LogDifferences.LogDifferences;
import edu.cmu.pact.Log.LogDifferences.TutorMessageContents;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.OLIMessageObject;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MessagePlayer;
import edu.cmu.pact.ctat.MessagePlayerEvent;
import edu.cmu.pact.ctat.MessagePlayerListener;
import edu.cmu.pact.ctat.model.ProblemSummary;
import edu.cmu.pact.ctat.model.Skills;
import edu.cmu.pslc.logging.LogContext;
import edu.cmu.pslc.logging.OliDiskLogger;
import edu.cmu.pslc.logging.ToolMessage;
import edu.cmu.pslc.logging.TutorMessage;

/**
 * A table for viewing and replaying messages from a log.
 */
public class LogConsole extends Box 
implements MessagePlayerListener, ProblemModelListener {

	/** Format for time with milliseconds using hours 00-23. */
	private static final DateFormat timeFmtLong
	= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSSSS");
	
	private Date parseDate(Element elmt) throws ParseException
	{
		timeFmtLong.setTimeZone(TimeZone.getTimeZone(elmt.getAttributeValue("timezone")));
		trace.out("log","Chris, the date is "+elmt.getAttributeValue("date_time"));
		return timeFmtLong.parse((elmt.getAttributeValue("date_time")));
	}
	
	/** Format for time with milliseconds using hours 00-23. */
	private static final DateFormat timeFmt 
	= new SimpleDateFormat("HH:mm:ss.SSS");

	/** Whether the connected student interface 
	 * is accepts CommMessages. */
	private boolean isCtatTutor = true;

	/** Whether the user wants to only bootstrap 
	 * until an untraced step is encountered.*/
	private boolean bootstrappedUntilErrorMode = false;
	
	/**
	 * Stores a reference to the CTATTabManager so LogConsole can
	 * tell it if LogConsole is still exists.
	 */
	private static CTATTabManager tabManager;

	/** The table. */
	private JTable table;

	/** Button to process marked table entries. */
	private JButton sendBtn;

	/** Button to select all the rows in the table. */
	private JButton selectAllBtn;

	/** Button to unselect all the rows in the table. */
	private JButton unselectAllBtn;

	/** Button to invert the table row selection (select the unselected rows and
	 * unselect the selected ones). */
	private JButton invertSelectionBtn;

	/** Button to mark the last attempts for each selection/action pair. */
	private JButton markLastAttemptBtn;

	/** Button to bootstrap until an error is encountered. */
	private JButton bootstrapUntilErrorBtn;
	
	/** Button to export all difference between an old log and the replay. */
	private JButton exportLogDiffsBtn;

	/** Whether the student interface gets CommMessages or not. */
	private JCheckBox isCtatTutorCheckBox;

	/** Button to open a VLab file. */
	// private JButton openVLabFileBtn;

	/** */
	private HashMap<DataShopMessageObject, Integer> playedMessages;

	private BR_Controller controller;

	private MessagePlayer messagePlayer;
	
	/** Stores the log console replies to be added to MessageRows */
	private LinkedHashSet<MessageObject> results = null;

	/** Status of current bootstrapping operation. */
	private class StatusLabel implements MessagePlayerListener {
		JLabel label = new JLabel("Total 0 log entries");
		JPanel panel = new JPanel(new BorderLayout());

		StatusLabel() {
			label.setName("StatusLabel");
		}
		public void messagePlayerEventOccurred(MessagePlayerEvent e) {
			int total = e.getTotalCount();
			int sent = e.getSentCount();
			boolean stopping = e.isStopping();
			panel.add(label, BorderLayout.CENTER);
			setText("Sent "+sent+" of "+total+" message"+(total==1 ? "":"s")+
					(stopping ? " (stopped)" : ""));

		}
		JPanel getPanel() {
			return panel;
		}
		void setText(final String text) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					label.setText(text);
					panel.validate();
				}
			});
		}
		void showTotal(int total) {
			setText("Total "+total+" log "+(total == 1 ? "entry" : "entries"));
		}
	}

	/** Status line. */
	private StatusLabel statusLabel = new StatusLabel();

	private OliDiskLogger oli;


	/**
	 * Create the table model and the visual components.
	 * @param logEntries argument for
	 *        {@link LogConsole.MessageObjectTableModel#setData(List)}
	 * @param controller2 to set the initial controller object
	 * @throws Exception on error from setDataFromFile(logFileName)
	 */
	public LogConsole(List logEntries, BR_Controller controller2) throws Exception {
		super(BoxLayout.PAGE_AXIS);
		this.controller = controller2;

		table = new JTable(new MessageObjectTableModel());
		((MessageObjectTableModel) table.getModel()).setData(logEntries);

		init();
	}

	/**
	 * Create the table model and the visual components.
	 * @param logFileName argument for
	 *        {@link LogConsole.MessageObjectTableModel#setDataFromFile(String)}
	 * @param convert if true, will direct {@link LogFormatUtils} to remove XML escaping 
	 * @param controller2 to set the initial controller object
	 * @throws Exception on error from setDataFromFile(logFileName)
	 */
	public LogConsole(String logFileName, boolean convert, BR_Controller controller2) throws Exception {
		super(BoxLayout.PAGE_AXIS);

		this.controller = controller2;

		if(trace.getDebugCode("log"))
			trace.out("log", "logconsole w/ 3 variables passed");

		//Give Logger a reference to LogConsole so it can send messages back
		controller.getLogger().setLogConsole(this);
		
		table = new JTable(new MessageObjectTableModel());
		((MessageObjectTableModel) table.getModel()).setDataFromFile(logFileName, convert);

		init();
	}

	/**
	 * Common code for constructors. Edits the table and adds elements to log GUI
	 */
	private void init() {
		// table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		// table.setFillsViewportHeight(true);

		// Add the log console as a listener to ProblemModel events
		if (controller != null)
			getController().getProblemModel().addProblemModelListener(this);

		table.setDefaultRenderer(JButton.class, new RowSendBtnRenderer());
		if (trace.getDebugCode("log")) trace.out("log", "init setdefaultrenderer");
		table.setDefaultEditor(JButton.class, new RowSendBtnEditor());

		JLabel renderer = (JLabel)table.getDefaultRenderer(String.class);
		renderer.setHorizontalAlignment(SwingConstants.CENTER);

		setName("LogConsole");   // for GUI testing

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);

		// Add the scroll pane to this panel.
		add(scrollPane);

		add(createVerticalStrut(4));

		Box btnsPanel = new Box( BoxLayout.X_AXIS );

		sendBtn = new JButton("Send");
		sendBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				bootstrappedUntilErrorMode = false;
				sendRows();
			}
		});
		//sendBtn.setEnabled(getController() != null);
		sendBtn.setEnabled(false);
		btnsPanel.add(sendBtn);

		selectAllBtn = new JButton("Select All");
		selectAllBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				selectAll();
			}
		});
		selectAllBtn.setEnabled(true);
		btnsPanel.add(selectAllBtn);

		unselectAllBtn = new JButton("Unselect All");
		unselectAllBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				selectAll();
				invertSelection();
			}
		});
		unselectAllBtn.setEnabled(true);
		btnsPanel.add(unselectAllBtn);

		invertSelectionBtn = new JButton("Invert Selection");
		invertSelectionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				invertSelection();
			}
		});
		invertSelectionBtn.setEnabled(true);
		btnsPanel.add(invertSelectionBtn);

		markLastAttemptBtn = new JButton("Mark Last Attempts");
		markLastAttemptBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				markLastAttempts();
			}
		});
		markLastAttemptBtn.setEnabled(true);
		btnsPanel.add(markLastAttemptBtn);

		bootstrapUntilErrorBtn = new JButton("Bootstrap until Error");
		bootstrapUntilErrorBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				bootstrappedUntilErrorMode = true;
				sendRows();
			}
		});
		bootstrapUntilErrorBtn.setEnabled(true);
		btnsPanel.add(bootstrapUntilErrorBtn);

		
		exportLogDiffsBtn = new JButton("Export Differences");
		exportLogDiffsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				outputLogDifferenceFile();
			}
		});
		exportLogDiffsBtn.setEnabled(true);
		btnsPanel.add(exportLogDiffsBtn);
		
		
		isCtatTutorCheckBox = new JCheckBox("CTAT Tutor", isCtatTutor);
		isCtatTutorCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				isCtatTutor = (e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		isCtatTutorCheckBox.setEnabled(true);
		btnsPanel.add(isCtatTutorCheckBox);
		//  openVLabFileBtn = new JButton("Open VLab File");
		//  openVLabFileBtn.addActionListener(new ActionListener() {
		//  		public void actionPerformed(ActionEvent ae) {
		//				try{
		//					openVLabFile();
		//				}
		//				catch (Exception e) {}
		//
		//			}
		//		});
		//		openVLabFileBtn.setEnabled(true);
		//		btnsPanel.add(openVLabFileBtn);

		add( btnsPanel );

		add(statusLabel.getPanel());				
	}

	/**
	 * Stop the message player on certain {@link ProblemModelEvent}s. 
	 * Also sets {@link #messagePlayer} to null.  In the event that a 
	 * compound event is supplied this code will extract all of the 
	 * EdgeCreated and EdgeCreationFailed events from it and then 
	 * call recursively.  Note that as both cases will stop the message 
	 * player and set it to null this code will check on that.
	 * @param e
	 * @see ProblemModel.ProblemModelListener#problemModelEventOccurred(ProblemModelEvent)
	 */
	public void problemModelEventOccurred(ProblemModelEvent e) {
		/* No point running if the message player is missing. */
		if (this.messagePlayer != null) {

			/* On Edge created events with the 
			 * bootstrap mode stop the messaging. */
			if ((e instanceof EdgeCreatedEvent) 
					&& (bootstrappedUntilErrorMode == true)) {
				//this.stopMessagePlayer();
				if(trace.getDebugCode("mp")){trace.out("mp", "would have called stopMessagePlayer()"); }
			}
			/* Ditto on any edge creation failed event with the 
			 * done state case. */
			else if (e instanceof EdgeCreationFailedEvent) {
				EdgeCreationFailedEvent ecf = (EdgeCreationFailedEvent) e;
				if (EdgeCreationFailedEvent.Reason.LINK_AFTER_DONE_STATE 
						== ecf.getCause()) {
					this.stopMessagePlayer();
				}
			}
			/* Finally for compound events this will recursively
			 * call on each of the subevents and execute them 
			 * in turn.*/
			if (e.isCompoundEventP()) {
				for (ProblemModelEvent V : e.getSubevents()) {
					this.problemModelEventOccurred(V);
				}
			}
		}
	}


	/**
	 * On a signal stop the running message player if it exists
	 * and then set it to null freeing the pointer.
	 */
	private void stopMessagePlayer() {
		if (messagePlayer != null) { messagePlayer.setStopping(true); }
		messagePlayer = null;
	}


	/**
	 *
	 */
	public void messagePlayerEventOccurred(MessagePlayerEvent e)
	{
		MessageObjectTableModel motm = (MessageObjectTableModel) table.getModel();
		ArrayList list = (ArrayList)motm.getData();
		int rowNumber = playedMessages.get( e.getDataShopMessageObject() ).intValue();
		MessageRow row = (MessageRow) list.get( rowNumber );
		row.setBootstrapped( row.getBootstrapped() + 1 );
		row.setToSend(Boolean.FALSE);
		motm.fireTableDataChanged();
	}

	/**
	 * Selects all rows in the table.
	 */
	private void selectAll() {
		MessageObjectTableModel motm = (MessageObjectTableModel) table.getModel();
		motm.setAllSend( true );
		motm.fireTableDataChanged();
	}

	/**
	 * Invert the table row selection.
	 */
	private void invertSelection() {
		MessageObjectTableModel motm = (MessageObjectTableModel) table.getModel();
		ArrayList msgs = motm.getData();
		for( int i = 0; i < msgs.size(); i++ ) {
			MessageRow row = ( (MessageRow) msgs.get( i ) );
			row.setToSend( row.getToSend() == false );
		}
		motm.fireTableDataChanged();
	}

	/**
	 * Mark last
	 */
	private void markLastAttempts() {
		Map<String, Integer> tbl = new HashMap();
		MessageObjectTableModel motm 
		= (MessageObjectTableModel) table.getModel();
		ArrayList msgs = motm.getData();
		for( int i = 0; i < msgs.size(); i++ ) {
			MessageObject msg = ( (MessageRow) msgs.get( i ) ).getMessage();
			Vector sv = (Vector) msg.getProperty( "Selection" );
			if( sv == null )
				sv = (Vector) msg.getProperty( "selection" );
			String selection 
			= ( sv == null || sv.size() < 1 ? "" : (String) sv.get(0) );

			Vector av = (Vector) msg.getProperty( "Action" );
			if( av == null )
				av = (Vector) msg.getProperty( "action" );
			String action = ( av == null || av.size() < 1 ? "" : (String) av.get(0) );

			String key = selection + " " + action;
			tbl.put( key, new Integer( i ) );
		}
		trace.out( "boot", "table.size = " + tbl.size() );
		motm.setAllSend( false );
		for( Iterator<Integer> it = tbl.values().iterator(); it.hasNext(); ) {
			Integer i = it.next();
			MessageRow row = ( (MessageRow) msgs.get( i.intValue() ) );
			row.setToSend( true );
		}
		motm.fireTableDataChanged();
	}

	/**
	 * Play all selected messages.
	 */
	private void sendRows() {
		MessageObjectTableModel motm = (MessageObjectTableModel) table.getModel();
		List msgs = motm.getMessagesToSend();
		sendRows(motm, msgs);
	}

	/**
	 * Play the message on the row whose number is passed as argument.
	 * @param row the number of the row where the message to be played is
	 */
	private void sendRow( int row ) {
		MessageObjectTableModel motm 
		= (MessageObjectTableModel) table.getModel();
		List msgs = motm.getMessageToSend( row );
		sendRows(motm, msgs);
	}

	/**
	 * Send the given list of messages
	 * @param motm
	 * @param msgs
	 */
	private void sendRows(MessageObjectTableModel motm, List msgs) {

		//TODO remove this debug print statement once skills are added correctly to the replay skills
		ProblemSummary ps = controller.getProblemModel().getProblemSummary();
		if (trace.getDebugCode("log")) trace.out("log", "ProblemSummary: "+ps.toXML());
		
		if (trace.getDebugCode("log")) trace.out("log", "LogConsole sending messages "+msgs);
		
		motm.fireTableDataChanged();
		messagePlayer = new MessagePlayer(getController(), msgs, isCtatTutor);

		// Add the log console as a listener to MessagePlayer events
		oli = OliDiskLogger.create("logfiles/"+controller.getProblemModel().getProblemName()+".log","UTF-8");
		messagePlayer.setLogger(oli);
		messagePlayer.addMessagePlayerListener(this);
		messagePlayer.addMessagePlayerListener(statusLabel);

		UniversalToolProxy utp =
				(getController() == null ? null : getController().getUniversalToolProxy());
		messagePlayer.setForwardToClientProxy(utp);
		(new Thread(messagePlayer)).start();
	}

	/**
	 * Choose a log file to replay, create the GUI and show it.
	 * @param controller2 reference for {@link MessagePlayer}
	 */
	public static void createConsole(BR_Controller controller2) {
		if(controller2.getProblemName() == null || controller2.getProblemName().trim().length() < 1) {
			Utils.showExceptionOccuredDialog(null, "Please open a problem", "No problem loaded");
			return;
		}
		File inf = DialogUtilities.chooseFile(null, null,
				"Please choose the log file name", "Load", controller2);

		String infName = (inf == null ? null : inf.getPath());
		if (infName == null || infName.length() < 1) {
			trace.out("inter", "No file chosen.");
			infName = null;
		}

		LogConsole logConsole = null;
		try {
			if( infName == null )
				logConsole = new LogConsole( new ArrayList(), controller2);
			else{
				//Find the file extension passed in. Either XML or .log
				//XML files are left as is, but log files must be formatted to be read
				String filePath = inf.getAbsolutePath();
				int periodLocation = filePath.lastIndexOf('.');
				String fileExtension = filePath.substring(periodLocation + 1);
				
				if (trace.getDebugCode("log")) trace.out("log", "file extension = "+fileExtension);
				boolean needsFormatting = false;
				
				if(fileExtension.equals("xml"))
					needsFormatting = false;
				else if(fileExtension.equals("log"))
					needsFormatting = true;
				
				if (trace.getDebugCode("log")) trace.out("log", "LogConsole createConsole needsFormatting " + needsFormatting);

				logConsole = new LogConsole(infName, needsFormatting, controller2);
			}
		} catch (Exception e) {
			String msg = "Could not read log file"+infName+" for replay:\n"+e;
			trace.errStack(msg, e);
			JOptionPane.showMessageDialog(null, msg, "Log File Processing Error", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		createAndShowGUI(logConsole, (controller2 != null ? controller2.getDockedFrame() : null));
	}

	/**
	 *
	 */
	class RowSendBtnEditor extends AbstractCellEditor
	implements TableCellEditor, ActionListener {
		protected static final String SEND_ROW = "Send";
		private JButton button;
		private int lastPressedSendRowBtn;

		public RowSendBtnEditor() {
			lastPressedSendRowBtn = -1;
			button = new JButton(SEND_ROW);
			button.setActionCommand(SEND_ROW);
			button.addActionListener(this);
			trace.out( "RowSendBtnEditor constructor" );
		}

		/**
		 * Handles events from the editor button and from
		 * the dialog's OK button.
		 */
		public void actionPerformed(ActionEvent e) {
			trace.out( "RowSendBtnEditor.actionPerformed" );
			sendRow( lastPressedSendRowBtn );
		}

		//Implement the one CellEditor method that AbstractCellEditor doesn't.
		public Object getCellEditorValue() {
			trace.out( "RowSendBtnEditor.getCellEditorValue" );
			return button;
		}

		//Implement the one method defined by TableCellEditor.
		public Component getTableCellEditorComponent(JTable table,
				Object value,
				boolean isSelected,
				int row,
				int column) {
			trace.out( "RowSendBtnEditor.getTableCellEditorComponent row = " + row + " column = " + column);
			lastPressedSendRowBtn = row;
			return button;
		}
	}

	class RowSendBtnRenderer extends JButton implements TableCellRenderer {
		public RowSendBtnRenderer() {
			this.setText("Send");
			setOpaque(true); //MUST do this for background to show up.
			trace.out( "RowSendBtnEditor constructor" );
		}

		public Component getTableCellRendererComponent(
				JTable table, 
				Object color,
				boolean isSelected, 
				boolean hasFocus,
				int row, int column) {
			//trace.out( "RowSendBtnRenderer.getTableCellRendererComponent" );
			return this;
		}
	}

	/**
	 * A row in {@link LogConsole.MessageObjectTableModel}
	 */
	class MessageRow {

		private Date time;

		private final DataShopMessageObject omo;

		private int rowNumber;

		private Boolean toSend = new Boolean(true);

		private JButton rowSend = new JButton("Send");

		private int bootstrapped = 0;

		/** Holds the original XML tutor_message of this row*/
		private Element tutorMessageElement = null;
		
		private TutorActionLogV4 oldActionLog;
		private TutorActionLogV4 newActionLog;
		
		
		
		public MessageRow(DataShopMessageObject omo, int rowNumber) {
			time = omo.getTimeStamp();
			this.omo = omo;
			this.rowNumber = rowNumber;
		}
		
		//////////////// Getters and Setters ////////////////////
		public String getTime() {
			return timeFmt.format(time);
		}

		public String getSelection() {
			Object result = omo.getProperty("selection");
			if (result == null)
				result = omo.getProperty("Selection");
			return (result == null ? "" : result.toString());
		}

		public String getAction() {
			Object result = omo.getProperty("action");
			if (result == null)
				result = omo.getProperty("Action");
			return (result == null ? "" : result.toString());
		}

		public String getInput() {
			Object result = omo.getProperty("input");
			if (result == null)
				result = omo.getProperty("Input");
			return (result == null ? "" : result.toString());
		}

		public int getRowNumber() {
			return rowNumber;
		}

		public Boolean getToSend() {
			return toSend;
		}

		public JButton getRowSend() {
			return rowSend;
		}

		public int getBootstrapped() {
			return bootstrapped;
		}

		public void setRowNumber( int rowNumber ) {
			this.rowNumber = rowNumber;
		}

		public void setToSend(Boolean toSend) {
			this.toSend = toSend;
		}

		public void setRowSend(JButton rowSend) {
			this.rowSend = rowSend;
		}

		public void setBootstrapped(int bootstrapped) {
			this.bootstrapped = bootstrapped;
		}

		public DataShopMessageObject getMessage() {
			return omo;
		}

		public Element getTutorMessageElement() {
			return tutorMessageElement;
		}

		public void setTutorMessageElement(Element tutorMessageElement) {
			this.tutorMessageElement = tutorMessageElement;
		}

		
		public TutorActionLogV4 getOldActionLog() {
			return oldActionLog;
		}

		public void setOldActionLog(TutorActionLogV4 oldActionLog) {
			this.oldActionLog = oldActionLog;
		}

		public TutorActionLogV4 getNewActionLog() {
			return newActionLog;
		}

		public void setNewActionLog(TutorActionLogV4 newActionLog) {
			this.newActionLog = newActionLog;
		}
	}

	/**
	 * A {@link TableModel} for {@link MessageObject}s.
	 */
	class MessageObjectTableModel extends AbstractTableModel {

		/** Userid, which might come from log_session_start element. */
		private String userId = "";

		/** Session id, from sess_ref attribute of log_action element. */
		private String sessionId = "";

		/** Session id, from date_time attribute of log_action element. */
		private String dateTime = "1970/01/01 00:00:00";

		/** Session id, from timezone attribute of log_action element. */
		private String timeZone = "UTC";

		private static final int ROW_NUMBER = 0;

		private static final int TIME_COLUMN = 1;

		private static final int SELECTION_COLUMN = 2;

		private static final int ACTION_COLUMN = 3;

		private static final int INPUT_COLUMN = 4;

		private static final int SEND_COLUMN = 5;

		private static final int ROW_SEND_COLUMN = 6;

		private static final int BOOTSTRAPPED = 7;

		private final XMLOutputter xmlout = new XMLOutputter();

		private String[] columnNames = {"#",
				"Time",
				"Selection",
				"Action",
				"Input",
				"Select",
				"Send Row",
		"Bootstrapped"};

		/**
		 * Data for this table model. List element type is
		 * {@link LogConsole.MessageObject.MessageRow}.
		 */
		private ArrayList data = new ArrayList();

		private int lastBootstrappedRow = -1;

		private String fileName;
		
		/** Stores the (problem name, index) pair for the list of log_action elements */
		private HashMap<String, Integer> problemIndicies = new HashMap<String, Integer>();
		
		private List<Element> logElements = null;
		
		private HashSet<String> skillNameAndCategory = new HashSet<String>();

		/**
		 * Populate the table rows with data.
		 * Sets {@link LogConsole.MessageObjectTableModel#data}.
		 * @param omoList List of DataShopMessageObject instances
		 */
		public void setData(List omoList) {
			if (trace.getDebugCode("log")) trace.out("log", "running setData(List): "+omoList);
			
			data = new ArrayList();
			int rowNumber = 1;
			for (Iterator it = omoList.iterator(); it.hasNext(); ) {
				DataShopMessageObject omo = (DataShopMessageObject) it.next();
				MessageRow row = new MessageRow(omo, rowNumber++);
				data.add(row);
			}
			LogConsole.this.statusLabel.showTotal(data.size());
		}

		/**
		 * Set or clear all the ToSend values in the table.
		 * @param b true to set, false to clear
		 **/
		public void setAllSend( boolean b ) {
			ArrayList msgs = ( (MessageObjectTableModel) table.getModel() ).getData();
			for( int i = 0; i < msgs.size(); i++ )
			{
				MessageRow row = (MessageRow) msgs.get(i);
				row.setToSend( b ? Boolean.TRUE : Boolean.FALSE );
			}
		}

		/**
		 * Read a log file and call {@link #setDataFromLogElements(List)}.
		 * Also sets {@link #fileName}.
		 * @param logFileName
		 * @param convert if true, will direct {@link LogFormatUtils} to remove XML escaping 
		 * @throws Exception
		 */
		public void setDataFromFile(String logFileName, boolean convert)
				throws Exception {
			List<Element> logEntries = null;
			Element[] getRoot = new Element[1];
			fileName = logFileName;
			logEntries = LogFormatUtils.readLogFile(logFileName, convert, getRoot);

			if (trace.getDebugCode("log")) trace.out("log", "setDataFromFile logEntries list:" + logEntries);

			logElements = logEntries;//save the array to use later
			setNewProblemIndicies(logEntries);
			
			setDataFromLogElements(logEntries, getRoot[0]);
		}

		private void setNewProblemIndicies(List<Element> logEntries) {
			if (trace.getDebugCode("log")) trace.out("log", "setNewProblemIndicies logEntries size "+logEntries.size());
			for(int i = 0; i < logEntries.size(); i++){
				Element currentElt = logEntries.get(i);
				if(trace.getDebugCode("log"))
					trace.out("log", String.format("setNewProblemIndicies[%2d]: name %s, child %s",
							i, currentElt.getName(),
							currentElt.getChild("tutor_related_message_sequence")));

				if((currentElt.getName().equals("log_action")) 
						&& (currentElt.getChild("tutor_related_message_sequence") != null)){
					Element trms = currentElt.getChild("tutor_related_message_sequence");
					if(trace.getDebugCode("log"))
						trace.out("log", String.format("setNewProblemIndicies[%2d]: trms %s, children %s",
								i, trms.getName(), trms.getChildren()));
					
					if(trms.getChild("context_message") != null){
						String name = findProblemName(trms.getChild("context_message"));
						String escapedContext = unescapeXMLNameToProblem(name);
						
						putWithoutOverwrite(escapedContext, i);
					}
				}
			}
		}

		/**
		 * Used to make a log file's context(name) match that of the BRD's.</p>
		 * Unescape a string so that all <ul>
		 * <li>" " (spaces) -> "+"</li>
		 * <li>"=" -> "eq"</li>
		 * </ul>
		 * @param escapedString
		 * @return string that should match the BRD problem name
		 */
		private String unescapeXMLNameToProblem(String escapedString) {
			String unescapeSpace = escapedString.replaceAll(" ", "+");
			String unescapedEquals = unescapeSpace.replaceAll("=", "eq");
			
			return unescapedEquals;
		}

		/** 
		 * Traverse the context_message in a specific order to find the problem context (name). </p>
		 * <b>context_message</b> -> dataset -> level -> level -> 
		 * problem -> <b>context</b></p>
		 * @return context which should be the unique 'problem context' in a 
		 * context_message; null if failed to traverse to the node element*/
		private String findProblemName(Element child) {
			try{
				Element dataset = child.getChild("dataset");
				Element level = dataset.getChild("level");
				Element level2 = level.getChild("level");
				Element problem = level2.getChild("problem");
				Element name = problem.getChild("context");
				
				return name.getText();
			}
			catch(NullPointerException e){
				System.err.println("Couldn't find the context_message's problem context");
				return null;
			}
		}
		
		private boolean putWithoutOverwrite(String key, int value){
			//Don't overwrite any existing values.
			//Sort of a hack to prevent random extra context messages from writing the wrong index
			if(problemIndicies.get(key) != null){ return false; }
			
			problemIndicies.put(key, value);
			return true;
		}

		/**
		 * Returns the data in the table model.
		 */
		private ArrayList getData()
		{
			return data;
		}

		/**
		 * Get all the message whose to-send flag is set.
		 * @return List of {@link DataShopMessageObject}s
		 */
		public List getMessagesToSend() {
			int i = 0;
			List result = new ArrayList();
			playedMessages = new HashMap();
			for (Iterator it = data.iterator(); it.hasNext(); ++i) {
				MessageRow row = (MessageRow) it.next();
				if (row.getToSend().booleanValue())
				{
					result.add(row.getMessage());
					playedMessages.put(row.getMessage(), i);
				}
			}
			return result;
		}

		/**
		 * Get the message where the pressed send button is.
		 * @return List of {@link DataShopMessageObject}s
		 */
		public List getMessageToSend( int pressedRow ) {
			List result = new ArrayList();
			playedMessages = new HashMap();
			MessageRow row = (MessageRow) data.get( pressedRow );
			result.add(row.getMessage());
			playedMessages.put(row.getMessage(), pressedRow);
			return result;
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int r, int c) {
			MessageRow row = (MessageRow) data.get(r);
			switch (c) {
			case ROW_NUMBER:
				return row.getRowNumber();
			case TIME_COLUMN:
				return row.getTime();
			case SELECTION_COLUMN:
				return row.getSelection();
			case ACTION_COLUMN:
				return row.getAction();
			case INPUT_COLUMN:
				return row.getInput();
			case SEND_COLUMN:
				return row.getToSend();
			case ROW_SEND_COLUMN:
				return row.getRowSend();
			case BOOTSTRAPPED:
				return row.getBootstrapped();
			default:
				trace.err("MessageObjectTableModel.getValueAt(" + r + "," + c
						+ ") bad column index " + c);
				return "";
			}
		}

		/*
		 * JTable uses this method to determine the default renderer/ editor for
		 * each cell. If we didn't implement this method, then the last column
		 * would contain text ("true"/"false"), rather than a check box.
		 */
		public Class getColumnClass(int c) {
			switch (c) {
			case SEND_COLUMN:
				return Boolean.class;
			case ROW_SEND_COLUMN:
				return JButton.class;
			default:
				return String.class;
			}
		}

		/*
		 * Don't need to implement this method unless your table is editable.
		 */
		public boolean isCellEditable(int row, int col) {
			// Note that the data/cell address is constant,
			// no matter where the cell appears onscreen.
			return (col == SEND_COLUMN || col == ROW_SEND_COLUMN);
		}

		/*
		 * Don't need to implement this method unless your table's data can
		 * change.
		 */
		public void setValueAt(Object value, int r, int c) {
			trace.out("log", "Setting value at " + r + "," + c + " to " + value
					+ " (an instance of " + value.getClass() + ")");
			if (c != SEND_COLUMN && c != ROW_SEND_COLUMN ) {
				trace.err("MessageObjectTableModel.setValueAt(" + r + "," + c
						+ ") bad column index " + c);
				return;
			}
			if( c == SEND_COLUMN )
			{
				MessageRow row = (MessageRow) data.get(r);
				row.setToSend((Boolean) value);
			}
			else if( c == ROW_SEND_COLUMN )
			{
				MessageRow row = (MessageRow) data.get(r);
				row.setRowSend((JButton) value);
			}
			fireTableCellUpdated(r, c);

			trace.out("log", "New value of data:\n" + dumpData());
		}

		private String dumpData() {
			if (!trace.getDebugCode("log"))
				return null;
			StringBuffer sb = new StringBuffer();
			int numRows = getRowCount();
			int numCols = getColumnCount();
			for (int i = 0; i < numRows; i++) {
				sb.append("    row ").append(i).append(":");
				for (int j = 0; j < numCols; j++) {
					sb.append("  ").append(getValueAt(i, j));
				}
				sb.append("\n");
			}
			return sb.toString();
		}

		/**
		 * @return the {@link #userId}
		 */
		public String getUserId() {
			return userId;
		}

		/**
		 * @param new
		 *            value for {@link #userId}; sets "" if value is null
		 */
		private void setUserId(String attributeValue) {
			if (attributeValue == null || attributeValue.length() < 1)
				return;
			userId = attributeValue;
		}

		/**
		 * @return the {@link #sessionId}
		 */
		public String getSessionId() {
			return sessionId;
		}

		/**
		 * @param attributeValue
		 *            new value for {@link #sessionId}
		 */
		public void setSessionId(String attributeValue) {
			if (attributeValue == null)
				return;
			sessionId = attributeValue;
		}

		/**
		 * @return the {@link #dateTime}
		 */
		public String getDateTime() {
			return dateTime;
		}

		/**
		 * @param attributeValue
		 *            new value for {@link #dateTime}
		 */
		public void setDateTime(String attributeValue) {
			if (attributeValue == null || attributeValue.length() < 1)
				return;
			dateTime = attributeValue;
		}

		/**
		 * @return the {@link #timeZone}
		 */
		public String getTimeZone() {
			return timeZone;
		}

		/**
		 * @param attributeValue
		 *            new value for {@link #timeZone}
		 */
		private void setTimeZone(String attributeValue) {
			if (attributeValue == null || attributeValue.length() < 1)
				return;
			timeZone = attributeValue;
		}

		/**
		 * Stores header elements into {@link MessageObjectTableModel}.
		 * Stores the corresponding elements into their rows. </p>
		 * 
		 * Populate the {@link #data} with MessageRows from a list of OLI log elements.
		 * Calls {@link #getToolMessageElement(Element)} to make DataShopMessageObjects to send.
		 * @param logEntries
		 * @param versionElt
		 */
		public void setDataFromLogElements(List<Element> logEntries, Element versionElt) {
			Attribute versionAttr =
					(versionElt == null ? null : versionElt.getAttribute("version_number"));
			trace.out("log", "setDataFromLogElements(): logEntries.size() "+logEntries.size()+
					", versionAttr "+versionAttr);

			
			setOnlyCurrentProblemRows(logEntries);
			createSkillsForReplay();
			
			trace.out("log", "setDataFromLogElements(): data.size() "
					+ data.size());
			LogConsole.this.statusLabel.showTotal(data.size());
			fireTableDataChanged();
		}

		private void createSkillsForReplay() {
			if (trace.getDebugCode("log")) trace.out("log", "createSkillsForReplay "+new XMLOutputter().outputString(makeSkillsElement()));
			
			Skills skillObj = null;
			try {
				skillObj = Skills.factory(makeSkillsElement());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			skillObj.setExternallyDefined(true);
			ProblemSummary ps = controller.getProblemModel().getProblemSummary();
			ps.setSkills(skillObj);
			
			if (trace.getDebugCode("log")) trace.out("log", "createSkillsForReplay projectSummary obj "+ps.toXML());
		}

		private Element makeSkillsElement() {
			Element skills = new Element("skills");
			
			for(String nameCategory : skillNameAndCategory){
				//split the string based on the space in the middle
				int spaceIndex = nameCategory.indexOf(" ");
				String name = (spaceIndex == 0) ? null : nameCategory.substring(0, spaceIndex);
				String category = (spaceIndex == nameCategory.length() - 1) 
						? null : nameCategory.substring(spaceIndex + 1, nameCategory.length() - 1);

				//create and add the annotated skill to the skills element
				Element skill = new Element("skill");
				if(name != null) { skill.setAttribute("name", name); }
				if(category != null) { skill.setAttribute("category", category); }
				
				skills.addContent(skill);
			}
			return skills;
		}

		/**
		 * Match the open BRD's problem name to a section of a log. Only add
		 * the rows that are part of the same problem. Adds one tool_message
		 * and one tutor_message per row before moving onto the next row.
		 * @param logEntries
		 */
		private void setOnlyCurrentProblemRows(List<Element> logEntries) {
			data = new ArrayList<MessageRow>();//reset the table's rows before changing
			
			String problemName = controller.getProblemName();
			
			if (trace.getDebugCode("log")) trace.out("log", "problemIndicies "+problemIndicies);
			if (trace.getDebugCode("log")) trace.out("log", "problemName "+problemName);

			int elementIndex = problemIndicies.get(problemName) + 1;//NOTE: +1 to move from context_message to the next message
			int rowNum = 1;//start numbering at 1, not 0
			MessageRow row = null;
			
			while(elementIndex < logEntries.size()){
				Element currentElement = logEntries.get(elementIndex);
				
				if(currentElement.getName().equals("log_session_start")){ return; }
				if(isTutorPerformed(currentElement)){ elementIndex++; continue; } //skip all tutor performed messages
				
				String elementType = getMessageType(currentElement);

				if(elementType.equals("tool_message")){
					DataShopMessageObject omo = createDataShopMessageObject(currentElement);
					row = new MessageRow(omo, rowNum);
				}
				else if(elementType.equals("tutor_message")){
					row.setTutorMessageElement(currentElement);
										
					data.add(row);
					
					extractSkills(currentElement);
					
					rowNum++;
				}
				elementIndex++;
			}
		}

		/** Finds all the skill elements and adds them to a hash so that the 
		 * replay can use the same set of skills as the original log */
		private void extractSkills(Element currentElement) {
			//FIXME this doesn't seem to be accomplishing what we want. Skills aren't really appearing correctly
			//assumes this will be a log_action with a tutor_message
			List<Element> skills = null;
			try{
				Element trms = currentElement.getChild("tutor_related_message_sequence");
				Element tutorMessage = trms.getChild("tutor_message");
				skills = tutorMessage.getChildren("skill");
			}
			catch(NullPointerException e){
				if (trace.getDebugCode("log")) trace.out("log", "extractSkills can't traverse to skills");
			}
			
			for(Element skill : skills){
				String name = skill.getChildText("name");
				String category = skill.getChildText("category");
				
				if(name == null && category == null){ continue; } //skip adding a skill if nothing is found
				
				String nameAndCategoryConcat = "";
				if(name != null){ nameAndCategoryConcat += name; }
				nameAndCategoryConcat += " ";
				if(category != null){ nameAndCategoryConcat += category; }
				
				//add the skill name+category to a hashset (avoids duplicates)
				skillNameAndCategory.add(nameAndCategoryConcat);
			}
		}

		/** @return true if the message is a tutor performed action, else false*/
		private boolean isTutorPerformed(Element element) {
			if (trace.getDebugCode("log")) trace.out("log", "isTutorPerformed "+new XMLOutputter().outputString(element));
			
			Element trms = element.getChild("tutor_related_message_sequence");
			
			if(trms.getChild(getMessageType(element)) == null){ return false; }
			Element msg = trms.getChild(getMessageType(element));
			
			if(msg.getChild("semantic_event") == null){ return false; }
			Element semanticEvent = msg.getChild("semantic_event");
			
			String attributeValue = semanticEvent.getAttributeValue("subtype");
			
			if(attributeValue != null
					&& attributeValue.equals("tutor-performed")){
				return true;
			}
			return false;
		}

		/**Refactored code from setDataFromLogElements. </p>
		 * Take an Element tool_message and convert it to a DataShopMessageObject
		 * 
		 * @param elt Element of tool_message
		 * @return DataShopMessageObject
		 */
		private DataShopMessageObject createDataShopMessageObject(Element elt){
			String logEltStr = xmlout.outputString(elt);
			if (trace.getDebugCode("log")) trace.out("log", "logElt is\n" + logEltStr);
			
			Element toolMsgElt = getToolMessageElement(elt);
			String eltStr = xmlout.outputString(toolMsgElt);
			
			if (trace.getDebugCode("log")) trace.out("log", "tool_message element is\n" + eltStr);
			if (trace.getDebugCode("log")) trace.out("log", "controller.getLogger()" + controller.getLogger());

			DataShopMessageObject omo = new DataShopMessageObject(eltStr, controller.getLogger());
			omo.setOriginalElementString(logEltStr);
			omo.setUserId(getUserId());
			omo.setSessionId(getSessionId());
			omo.setTimeStamp(TutorActionLog.getDate(getDateTime(), getTimeZone()));
			
			trace.out("log", "DataShopMessageObject made from Elements of the file:"+omo.toString());
			
			return omo;
		}
		
		/**Takes a JDOM Element read from a log and returns its message type.
		 * 
		 * @param Element elt
		 * @return String for the type of message: tool_message, tutor_message, or message.
		 */
		private String getMessageType(Element elt){
			Element message = elt.getChild("tutor_related_message_sequence");

			if(message.getChild("tool_message") != null){
				return "tool_message";
			}
			else if(message.getChild("tutor_message") != null){
				return "tutor_message";
			}
			else{
				return "message";
			}
		}
		
		/**
		 * Get the tool_message element from the body of a log_action element.
		 * Tries to find tool_message
		 *
		 * @param elt
		 *            log element
		 * @return tutor_related_message_sequence element, if tool_message is a
		 *         child of it; null if not
		 */
		private Element getToolMessageElement(Element elt) {
			if (trace.getDebugCode("log")) trace.out("log", "getToolMessageElement() elt.getName() is "+elt.getName());
			if (trace.getDebugCode("log")) trace.out("log", "getToolMessageElement() elt pprint is "+xmlout.outputString(elt));
			if (trace.getDebugCode("log")) trace.out("log", "getToolMessageElement() elt.getText() is "+elt.getText());
			if ("log_session_start".equals(elt.getName())) {
				setUserId(elt.getAttributeValue(Logger.STUDENT_NAME_PROPERTY));
				return null;
			}
			if ("tutor_related_message_sequence".equals(elt.getName()))
				return elt;
			if ("tool_message".equals(elt.getName()))
				return elt;
			if (!"log_action".equals(elt.getName()))
				return null;
			setUserId(elt.getAttributeValue(Logger.STUDENT_NAME_PROPERTY));
			setSessionId(elt.getAttributeValue("session_id"));
			setDateTime(elt.getAttributeValue("date_time"));
			setTimeZone(elt.getAttributeValue("timezone"));
			Element child = elt.getChild("tutor_related_message_sequence");
			if (trace.getDebugCode("log")) trace.out("log", "getToolMessageElement() children are "+
					elt.getChildren());
			if (child == null)
				return null;
			Element grandchild = child.getChild("tool_message");
			if (grandchild== null)
				return null;
			return child;    // return tutor_related_message_sequence to provide log version
		}

		public String getFileName() {
			return fileName;
		}

		public HashSet<String> getSkillNameAndCategory() {
			return skillNameAndCategory;
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method
	 * invokes the UI from the event-dispatching thread.
	 * @param logConsole console to display
	 * @param ownerFrame owner of JDialog created here
	 */
	public static void createAndShowGUI(LogConsole logConsole, final JFrame ownerFrame) {

		class ConsoleThread implements Runnable {
			private LogConsole logConsole;

			ConsoleThread(LogConsole logConsole) {
				this.logConsole = logConsole;
			}
			public void run() {
				//Inform the TabManager that this window is running
				tabManager.createdLogConsole();
				
				// Create and set up the window.
				String title = "Log Console"+(logConsole.getFileName() == null ?
						"" : ": "+logConsole.getFileName());
				final JDialog window = new JDialog(ownerFrame, title);
				
				//Set the jDialog to dispose on closing and inform the CTATTabManager
				window.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				window.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent evt) {
						tabManager.closedLogConsole();
						window.dispose();
					}
				});
				
				//				sewall 2007/10/01: JDialog inherits icon from ownerFrame, so remove icon logic
				// CTAT logo in icon at left end of title bar.
				// Image image = new ImageIcon("ctaticon.png").getImage();
				// if (image != null && image.getHeight(null) != -1)
				//	frame.setIconImage(image);

				// Set up the content pane.
				logConsole.setOpaque(true); // content panes must be opaque
				window.setContentPane(logConsole);

				// Display the window.
				window.pack();
				window.setVisible(true);
			}
		}
		ConsoleThread ct = new ConsoleThread(logConsole);
		(new Thread(ct)).start();
	}

	public String getFileName() {
		MessageObjectTableModel tm = (MessageObjectTableModel) table.getModel();
		return tm.getFileName();
	}

	public static void main(String[] args) {
		LogConsole logConsole = null;
		String infName = null;
		int i = 0;
		boolean convert = true;
		try {
			if (args.length < 1 || args[0].length() < 1)
				throw new IllegalArgumentException("missing filename");
			if ("-c".equalsIgnoreCase(args[i])) {
				convert = false;
				++i;
			}
			infName = args[i];
			File f = new File(infName);
			logConsole = new LogConsole(infName, convert, null);
		} catch (Exception e) {
			System.err.println("Error reading log file "+infName+": "+e);
			System.err.println("Usage:\n"+
					"  java -cp ... "+LogConsole.class.getName()+" [-c] logFileName\n"+
					"where--\n"+
					"  -c means do not convert the log file from the OLI escaped format;\n"+
					"  logFileName is a file in OLI disk log format or DataShop file format.");
			return;
		}
		createAndShowGUI(logConsole, null);
	}

	/**
	 * Supply data from a list of messages.
	 * Sets {@link LogConsole.MessageObjectTableModel#data}.
	 * @param omoList List of DataShopMessageObject instances
	 */
	public void setData(List omoList) {
		MessageObjectTableModel tm = (MessageObjectTableModel) table.getModel();
		tm.setData(omoList);
		validate();
	}

	public BR_Controller getController() {
		return controller;
	}

	private CTATTabManager getTabManager() {
		return tabManager;
	}

	public static void setTabManager(CTATTabManager tab) {
		tabManager = tab;
	}

	/**
	 * Called inside {@link Logger} to pass Associated Rules.
	 * Passes a MessageObject to LogConsole for comparison to log entries.
	 * @param messageObject
	 */
	public void sendMsgToLogConsole(MessageObject messageObject){
		if(trace.getDebugCode("log"))trace.out("log", "LogConsole sendMsgToLogConsle "+messageObject.toString());
		findLogDifferences(messageObject);
	}
	
	/**
	 * Compares a {@link MessageObject} generated with replay with it's matching
	 * {@link MessageRow} to find differences.
	 * @param messageObject generated from log replay
	 */
	private void findLogDifferences(MessageObject messageObject){
		MessageRow match = findMatchingTransactionID(messageObject);
		if(match == null){
			if(trace.getDebugCode("log"))trace.out("log", "LogConsole findLogDifferences didn't find a match");
			return;
		}
		
		if(trace.getDebugCode("log"))trace.out("log", "LogConsole findLogDifferences row message "+match.getMessage().toXML());
		
		
		TutorActionLogV4 tutorMessageOld = xmlToTutorActionLog(match.getTutorMessageElement());
		TutorActionLogV4 tutorMessageNew = convertAssociatedRulesToTutorActionLog(messageObject);
		if(trace.getDebugCode("log"))trace.out("log", "Old time stamp "+tutorMessageOld.getTimeStamp());
		//The time stamp was incorrect, so we fixed it below.
		tutorMessageNew.setTimeStamp(tutorMessageOld.getTimeStamp());
		tutorMessageNew.setTimezone(tutorMessageOld.getTimezone());
		match.setOldActionLog(tutorMessageOld);
		match.setNewActionLog(tutorMessageNew);
		
		if (trace.getDebugCode("month"))
		{
			trace.out("month","tutorMessageNew: "+tutorMessageNew.getTimeStamp());
			trace.out("month","tutorMessageOld: "+tutorMessageOld.getTimeStamp());
			//These are correct.
		}
		
		if (oli != null) oli.log(tutorMessageNew.getMsg(),tutorMessageOld.getTimeStamp());
	}
	
	/**
	 * Compares the new message transactionId with every 
	 * MessageRow's message transactionId until a match is found.
	 * @param messageObject generated from log replay
	 * @return MessageRow of the matching ID, else returns null
	 */
	private MessageRow findMatchingTransactionID(MessageObject messageObject){
		String transactionId = messageObject.getTransactionId();
		
		@SuppressWarnings("unchecked")
		ArrayList<MessageRow> tableRows = ((MessageObjectTableModel) table.getModel()).getData();
		Iterator<MessageRow> iterator = tableRows.iterator();
		
		while(iterator.hasNext()){
			MessageRow row = (MessageRow) iterator.next();
			String rowId = row.getMessage().getTransactionId();
			
			if(rowId.equals(transactionId)){
				if(trace.getDebugCode("log"))trace.out("log", "LogConsole findMatchingTransactionID found");
				return row;
			}
		}
		
		if(trace.getDebugCode("log"))trace.out("log", "LogConsole findMatchingTransactionID not found");
		return null;
	}
	
	private TutorActionLogV4 convertAssociatedRulesToTutorActionLog(MessageObject messageObject){
		Object test = messageObject.getProperty("TutorAdvice");
		
		if (trace.getDebugCode("log")) trace.out("log", "messageObject "+messageObject.toXML());
		if (trace.getDebugCode("log")) trace.out("log", "LogConsole getPropertyNames()1 "+(test == null ? "null":test));
		
		DataShopMessageObject convert = new DataShopMessageObject(messageObject, true, controller.getLogger());
		TutorActionLogV4 tutorMessage = convert.getLogMsg();
		//FIXME This one is logging the current time, not the original time.
		trace.out("log","WRONG TUTORMESSAGE TIME: "+tutorMessage.getTimeStamp()); 
		
		return tutorMessage;
	}
	
	/**
	 * Parses through XML tool_message or tutor_message to get all the custom fields
	 * of the element. This will ignore log_action and tutor_related_message_sequence,
	 * so it is safe to pass in elements that have not been fully formatted into
	 * message form yet.
	 * @param message must be XML of tool_message or tutor_message.
	 * @return Map (Name, Value) of all custom fields
	 */
	private LinkedHashMap<String, String> getCustomFieldsFromXML(Element message){
		Element tutorToolMessage = getOnlyToolTutorMessage(message);
		LinkedHashMap<String, String> customFields = new LinkedHashMap<String, String>();
		@SuppressWarnings("unchecked")
		List<Element> customFieldElements = tutorToolMessage.getChildren("custom_field");
		
		for(Element elt : customFieldElements){
			String name = elt.getChild("name").getText();
			String value = elt.getChild("value").getText();
			
			customFields.put(name, value);
		}
		return customFields;
	}

	/**
	 * Takes an element of root log_action, tutor_related_message_sequence, 
	 * or tool/tutor_message and returns an Element with the root of
	 * tool/tutor_message.
	 * @param message
	 * @return tool_message or tutor_message
	 */
	private Element getOnlyToolTutorMessage(Element message) {
		//Extract the tool/tutor message element from any Element
		if(message.getName().equals("log_action")){
			message = message.getChild("tutor_related_message_sequence");
		}
		if(message.getName().equals("tutor_related_message_sequence")){
			if(message.getChild("tool_message") != null){
				message = message.getChild("tool_message");
			}
			else if(message.getChild("tutor_message") != null){
				message = message.getChild("tutor_message");
			}
			else{
				return null;
			}
		}
		return message;
	}
	
	/**
	 * Takes the XML Element from a log tutor_message and create 
	 * a fully formatted TutorActionLogV4 with the same information.
	 * @param toolMessageElement
	 * @return
	 */
	private TutorActionLogV4 xmlToTutorActionLog(Element toolMessageElement){
		XMLOutputter outputter = new XMLOutputter();
		
		//turn XML element into DSMO and then get its TutorMessage
		Element tutorMessageElement = getOnlyToolTutorMessage(toolMessageElement);
		DataShopMessageObject tutorMessageObject = new DataShopMessageObject(
				outputter.outputString(tutorMessageElement), controller.getLogger());
		TutorMessage tutorMessage = ((TutorMessage) tutorMessageObject.getLogMsg().getMsg());
		
		//Add all custom fields to the TutorMessage 
		LinkedHashMap<String,String> customFields = getCustomFieldsFromXML(tutorMessageElement);
		Set<String> customFieldsKeys = customFields.keySet();
		Iterator<String> itr = customFieldsKeys.iterator();
		while(itr.hasNext()){//FIXME this is where custom fields are being added manually by me. Not sure if I need this anymore?
			String customFieldName = itr.next();
			String customFieldValue = customFields.get(customFieldName);
			
			tutorMessage.addCustomField(customFieldName, customFieldValue);
		}
		
		TutorActionLogV4 tutorMessageLog = tutorMessageObject.getLogMsg();
		//This time was being logged wrongly. We fix it below with a date parser.
		try {
			tutorMessageLog.setTimeStamp(parseDate(toolMessageElement));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (trace.getDebugCode("month"))
			trace.out("month","The new tutormessagelog: "+tutorMessageObject.getTimeStamp());
		// All the elements that are being sent here are returning the correct date.
		return tutorMessageLog;
	}
	
	public void outputLogDifferenceFile(){
		LogDifferences differences = new LogDifferences();
		
		MessageObjectTableModel tempTable = ((MessageObjectTableModel) table.getModel());
		
		ArrayList<MessageRow> tableRows = tempTable.getData();
		for(MessageRow row : tableRows){

			TutorActionLogV4 oldActionLog = row.getOldActionLog();
			TutorActionLogV4 newActionLog = row.getNewActionLog();
			
			if(oldActionLog == null || newActionLog == null){ continue; }
			
			differences.addTutorMessagePair(oldActionLog, newActionLog);
		}

		differences.writeToFile(getController(), this, tempTable.getSessionId());
	}
}
