/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Comparator;
import java.util.EventObject;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.html.HTMLDocument;

import pact.CommWidgets.StudentInterfaceConnectionStatus;
import pact.CommWidgets.UniversalToolProxy;
import sun.plugin.dom.exception.InvalidStateException;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;
import edu.cmu.pact.ctat.model.StartStateModel;
import edu.cmu.pact.ctat.model.StartStateModel.Choice;
import edu.cmu.pact.ctat.model.StartStateModel.CompareInterfaceDescriptions;
import edu.cmu.pact.ctat.view.AbstractCtatWindow;

/**
 * Dialog to permit author to compare user interface component settings as defined in the
 * student interface against those defined in the ProblemModel.  
 */
public class EditComponentStartStateSettingsDialog extends JDialog implements ActionListener,
		ChangeListener, PropertyChangeListener, StartStateModel.Listener {

	/** Dialog title. */
	public static final String START_STATE_COMPONENT_SETTINGS = "Edit Start State Component Settings";
	
	/** Title of warning about use of this dialog. */
	protected static final String NO_STUDENT_INTERFACE_CONNECTED = "No Student Interface Connected";
	
	/** Text of warning about use of this dialog. */
	protected static final String MUST_HAVE_INTERFACE_CONNECTED_TO_USE_FEATURE =
			"<html>You must have a student interface connected<br/>"+
			"to this graph panel to use this dialogue.</html>";	

	private static final long serialVersionUID = 201401231830L;

	/** Button label to clear all check boxes in the {@value #PM_NAME_COL} column. */
	private static final String OMIT_ALL_FROM_GRAPH = "Omit All from Graph";

	/** Tool tip for clearing all check boxes in the {@value #PM_NAME_COL} column. */
	private static final String OMIT_ALL_FROM_GRAPH_TOOLTIP = "Discard any existing graph settings and use only settings already in the interface.";

	/** Button label to set all check boxes in the {@value #PM_NAME_COL} column. */
	private static final String RETAIN_ALL_FROM_GRAPH = "Retain All from Graph";

	/** Tool tip for setting all check boxes in the {@value #PM_NAME_COL} column. */
	private static final String RETAIN_ALL_FROM_GRAPH_TOOLTIP = "Retain all existing graph settings, overriding settings already in the interface.";

	/** Button label to set all check boxes in the {@value #SI_NAME_COL} column. */
	private static final String RETAIN_ALL_FROM_INTERFACE = "Retain All from Interface";

	/** Tool tip for setting all check boxes in the {@value #SI_NAME_COL} column. */
	private static final String RETAIN_ALL_FROM_INTERFACE_TOOLTIP = "Copy all settings in the interface into the graph, discarding any existing graph settings.";

	/** Button label to clear all check boxes in the {@value #SI_NAME_COL} column. */
	private static final String OMIT_ALL_FROM_INTERFACE = "Omit All from Interface";

	/** Tool tip for clearing all check boxes in the {@value #SI_NAME_COL} column. */
	private static final String OMIT_ALL_FROM_INTERFACE_TOOLTIP = "Do not copy or retain any settings from the interface in the graph.";

	/** Button to remove all InterfaceDescription msgs currently in the graph. */
	private JButton omitAllGraphIDMsgs;

	/** Button to keep all InterfaceDescription msgs currently in the graph. */
	private JButton retainAllGraphIDMsgs;

	/** Button to prevent copying any InterfaceDescription msgs from the student interface to the graph. */
	private JButton omitAllInterfaceIDMsgs;

	/** Button to copy all InterfaceDescription msgs from the student interface to the graph. */
	private JButton retainAllInterfaceIDMsgs;
	
	/** OK button copies changes to the @link ProblemModel and closes the dialog. */
	private JButton okButton;
	
	/** Cancel button discards changes and closes the dialog. */
	private JButton cancelButton;
	
	/** To effect changes in the actual start state. */
	private StartStateModel model;
	
	/** List generated by {@link StartStateModel#compareInterfaceDescriptionMessages(ProblemModel)} . */
	private List<CompareInterfaceDescriptions> cidList;

	/** To save changes to the graph. */
	private final ProblemModel problemModel; 

	/** Column index for widget type. */
	private static final int TYPE_COL = 0;

	/** Column index for match result. */
	private static final int MATCH_COL = 1;

	/** Column index for problem model instance name. */
	private static final int PM_NAME_COL = 2;

	/** Column index for student interface instance name. */
	private static final int SI_NAME_COL = 3;
	
	private static final String[] columnLabels = {
		"Type", "Match", "Keep in Graph As Is", "Copy from Interface to Graph"
	};

	/**
	 * A table model for {@link StartStateModel.CompareInterfaceDescriptions}.
	 */
	class CIDTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 201401281200L;
		
		public String getColumnName(int col) {
			return columnLabels[col];
		}

		public int getRowCount() {
			return cidList.size();
		}

		public int getColumnCount() {
			return columnLabels.length;
		}
		
		public boolean isCellEditable(int row, int col) {
			return (col == PM_NAME_COL || col == SI_NAME_COL);
		}

		public Object getValueAt(int row, int col) {
			CompareInterfaceDescriptions cid = cidList.get(row);
			switch(col) {
			case TYPE_COL:
				return cid.getWidgetType();
			case MATCH_COL:
				if(StartStateModel.DIFFER.equals(cid.getMatch()))
					return settingsDifferIcon;
				else if(StartStateModel.SAME.equals(cid.getMatch()))
					return settingsSameIcon;
				else
					return null;
			case PM_NAME_COL:
				if(cid.getPmCommName() == null || cid.getPmCommName().length() < 1)
					return null;
				else
					return cid.getPmCommName();
			case SI_NAME_COL:
				if(cid.getSiCommName() == null || cid.getSiCommName().length() < 1)
					return null;
				else
					return cid.getSiCommName();
			default:
				throw new IllegalArgumentException("Invalid column "+col+
						" to CIDTableModel.getValueAt(); should be in range 0-"+(getColumnCount()-1));
			}
		}
		
		public Class getColumnClass(int col) {
			switch(col) {
			case TYPE_COL:          return String.class;
			case MATCH_COL:         return ImageIcon.class;
			case PM_NAME_COL:       return String.class;     // content for these two
			case SI_NAME_COL:       return String.class;     //     can be text of checkbox or null
			default:
				throw new IllegalArgumentException("Invalid column "+col+
						" to CIDTableModel.getColumnClass(); should be in range 0-"+(getColumnCount()-1));
			}
		}

		/**
		 * Set all checkboxes in column {@value EditComponentStartStateSettingsDialog#PM_NAME_COL}.
		 */
		public void retainAllGraphDescriptions() {
			int nChanged = 0;
			for(CompareInterfaceDescriptions cid : cidList) {
				if(cid.getPmCommName() != null && cid.getPmCommName().length() > 0) {
					cid.setChoice(Choice.keepPM);
					++nChanged;
				}
			}
			if(nChanged > 0)
				fireTableDataChanged();
		}

		/**
		 * Clear all checkboxes in column {@value EditComponentStartStateSettingsDialog#PM_NAME_COL}.
		 */
		public void omitAllGraphDescriptions() {
			int nChanged = 0;
			for(CompareInterfaceDescriptions cid : cidList) {
				if(cid.getChoice() == Choice.keepPM) {
					cid.setChoice(Choice.omit);
					++nChanged;
				}
			}
			if(nChanged > 0)
				fireTableDataChanged();
		}

		/**
		 * Set all checkboxes in column {@value EditComponentStartStateSettingsDialog#SI_NAME_COL}.
		 */
		public void retainAllInterfaceDescriptions() {
			int nChanged = 0;
			for(CompareInterfaceDescriptions cid : cidList) {
				if(cid.getSiCommName() != null && cid.getSiCommName().length() > 0) {
					cid.setChoice(Choice.saveSI);
					++nChanged;
				}
			}
			if(nChanged > 0)
				fireTableDataChanged();
		}

		/**
		 * Clear all checkboxes in column {@value EditComponentStartStateSettingsDialog#SI_NAME_COL}.
		 */
		public void omitAllInterfaceDescriptions() {
			int nChanged = 0;
			for(CompareInterfaceDescriptions cid : cidList) {
				if(cid.getChoice() == Choice.saveSI) {
					cid.setChoice(Choice.omit);
					++nChanged;
				}
			}
			if(nChanged > 0)
				fireTableDataChanged();
		}
	}
	
	/** Display this in the "match" column when the graph and interface settings are the same. */
	private static ImageIcon settingsSameIcon = null;
	
	/** Display this in the "match" column when the graph and interface settings differ. */
	private static ImageIcon settingsDifferIcon = null;
	
	static {                  // initialize settingsSameLabel, settingsDifferLabel
		final String path =
				"/"+EditComponentStartStateSettingsDialog.class.getPackage().getName().replace('.', '/');
		String[] baseNames = { "checkmark.png", "xmark.png" };
		for(String baseName : baseNames) {
			URL url = null;
			ImageIcon icon = null;
			String fileName = path+"/"+baseName;
			try {
				url = EditComponentStartStateSettingsDialog.class.getResource(fileName);
				if(url != null)
					icon = new ImageIcon(url);
				if(trace.getDebugCode("startstate"))
					trace.out("startstate", "CCSDialog: image \""+fileName+"\"; url "+url+", icon "+icon);
				if("checkmark.png".equals(baseName))
					settingsSameIcon = icon;
				else
					settingsDifferIcon = icon;
			} catch(Exception e) {
				trace.errStack("Error loading image \""+fileName+"\"; url "+url+", icon "+icon, e);
			}
		}
	}

	/** For {@link InstanceNameCheckBoxRenderer}. */
	private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	
	/**
	 * Borrowed from {@link JTable.BooleanRenderer}. 
	 */
	class InstanceNameCheckBoxRenderer extends JCheckBox implements TableCellRenderer {

		private static final long serialVersionUID = 201401301030L;

        public InstanceNameCheckBoxRenderer() {
            super();
            setBorderPainted(true);
        }

        /**
         * Retrieve and update the {@link CompareInterfaceDescriptions} object indicated
         * by the row argument with the changes wrought by this new 
         * @param table
         * @param value should be String; current text
         * @param isSelected not sure of the difference with hasFocus
         * @param hasFocus
         * @param row index into {@link EditComponentStartStateSettingsDialog#cidList}
         * @param col should be {@link EditComponentStartStateSettingsDialog#PM_NAME_COL}
         *            or {@link EditComponentStartStateSettingsDialog#SI_NAME_COL}
         * @return this checkbox
         * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
         */
        public Component getTableCellRendererComponent(JTable table, Object value,
        		boolean isSelected, boolean hasFocus, int row, int col) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            }
            else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }
            CompareInterfaceDescriptions cid = updateCBFromCID(cidTable.convertRowIndexToModel(row), col, this);
            setToolTipText(null);
            if(cid.getAlert() != null)
            	setToolTipText(cid.getAlert());
            
            if(trace.getDebugCode("startstate"))
            	trace.out("startstate", String.format("CCSD.InstNameCBRend.getTableCellRendererComponent()"+
            			" value %s, isSelected %b, hasFocus %b at (%d,%d): cid[%d] now %s; cb.isSelected %b",
            			value, isSelected, hasFocus, row, col, cidTable.convertRowIndexToModel(row), cid,
            			isSelected()));
            if (hasFocus)
                setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            else
                setBorder(noFocusBorder);

            return this;
        }
	}
	
	class InstanceNameCheckBox extends DefaultCellEditor
			implements TableCellEditor, ItemListener {

		private static final long serialVersionUID = 201401291905L;
		
		/** The table row, index into {@link EditComponentStartStateSettingsDialog#cidList}. */
		private final int row;
		
		/** The column referenced, should be PM_NAME_COL or SI_NAME_COL. */
		private final int col;

		public InstanceNameCheckBox(int row, int col) {
			super(new JCheckBox());
			JCheckBox cb = (JCheckBox) getComponent();
			this.row = row;
			this.col = col;
			cb.addItemListener(this);
		}

		/**
		 * Send a {@value MsgType#INTERFACE_DESCRIPTION} to the student interface to demonstrate
		 * the effect of the selected cell.
		 * @param evt
		 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
		 */
		public void itemStateChanged(ItemEvent evt) {
			boolean selected = (evt.getStateChange() == ItemEvent.SELECTED);
			CompareInterfaceDescriptions cid = updateCID(row, col, selected);
			if(trace.getDebugCode("startstate"))
				trace.out("startstate", String.format("CCSD.InstNameCB.itemStateChanged(%s,%s) at (%d,%d): cid[%d] now %s",
						trace.nh(evt.getItem()), (selected ? "SELECTED" : "DESELECTED"), row, col,
						row, cid));
			if(cid.getChoice() == Choice.keepPM)
				sendToInterface(cid.getPmMsg());
			else
				sendToInterface(cid.getSiMsg());
			fireEditingStopped();  // make the renderer reappear
		}

		/**
		 * Override to alter the JCheckBox from {@link #getComponent()}) with
		 * {@link EditComponentStartStateSettingsDialog#updateCBFromCID(int, int, JCheckBox)}
		 * before returning the value.
		 * @return result from {@link JCheckBox#isSelected()}
		 * @see javax.swing.DefaultCellEditor#getCellEditorValue()
		 */
		public Object getCellEditorValue() {
			JCheckBox cb = null;
			CompareInterfaceDescriptions cid = null;
			String result = null;
			Component c = getComponent();
			if(c instanceof JCheckBox) {
//				result = (((JCheckBox) c).isSelected() ? Boolean.TRUE : Boolean.FALSE);
				cb = (JCheckBox) c;
				cid = updateCBFromCID(row, col, cb);
				result = cb.getText();
			}
			if(trace.getDebugCode("startstate"))
				trace.out("startstate", String.format("CCSD.INCB.getCellEditorValue() at (%d,%d) cid %s "+
						" returning cb.isSelected() %s, component %s",
						row, col, cid, result, trace.nh(c)));
			return result;
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int col) {
			JCheckBox cb = null;
			CompareInterfaceDescriptions cid = null;
			Component c = super.getTableCellEditorComponent(table, value, isSelected, row, col);
			int modelRow = cidTable.convertRowIndexToModel(row);

			if(c != getComponent())
				throw new IllegalArgumentException("CCSD.INCB.getTableCellEditorComponent() "+
						" component mismatch: c "+trace.nh(c)+" vs. getComponent() "+trace.nh(getComponent()));
			if(modelRow != this.row)
				throw new IllegalArgumentException("CCSD.INCB.getTableCellEditorComponent() "+
						" row mismatch: convert("+row+")="+modelRow+" vs. this.row "+this.row);
			if(col != this.col)
				throw new IllegalArgumentException("CCSD.INCB.getTableCellEditorComponent() "+
						" col mismatch: col "+col+" vs. this.col "+this.col);

			if(c instanceof JCheckBox) {
				cb = (JCheckBox) c;
				cid = updateCBFromCID(modelRow, col, cb);
			}
			if(trace.getDebugCode("startstate"))
				trace.out("startstate", "CCSD.INCB.getTableCellEditorComponent() value "+value+
						", isSelected "+isSelected+" at ("+row+","+col+"); cid["+modelRow+"] "+cid+
						" returning cb.isSelected() "+(cb == null ? null : cb.isSelected())+
						", c "+trace.nh(c));
			return c;
		}

		/**
		 * Create a new {@link JCheckBox} if there's an instance name.
		 * @param cid set checked state from {@link CompareInterfaceDescriptions#getChoice()}
		 * @param col column index; returns null if not PM_NAME_COL or SI_NAME_COL
		 * @return JCheckBox with name 
		 */
		private JCheckBox cidToCheckBox(CompareInterfaceDescriptions cid, int col) {
			String instanceName;
			boolean cbState;
			if(col == PM_NAME_COL) {
				instanceName = cid.getPmCommName();
				cbState = (cid.getChoice() == Choice.keepPM);
			} else if(col == SI_NAME_COL) {
				instanceName = cid.getSiCommName();
				cbState = (cid.getChoice() == Choice.saveSI);				
			} else
				throw new IllegalArgumentException("Invalid column "+col+
						" to InstanceNameCheckBox; should be "+PM_NAME_COL+" or "+SI_NAME_COL);
			if(instanceName == null || instanceName.length() < 1)
				return null;
			else
				return new JCheckBox(instanceName, cbState);
		}
	}
	
	/** To sort the {@value EditComponentStartStateSettingsDialog#MATCH_COL}. */
	class CheckBoxComparator implements Comparator<Object> {
		
		/** Column value to retrieve. */
		private final int column;

		/**
		 * @param column value for {@link #column}
		 */
		CheckBoxComparator(int column) {
			this.column = column;
		}

		/**
		 * Order values as<ol>
		 * <li>null,</li>
		 * <li>{@link EditComponentStartStateSettingsDialog#settingsDifferIcon},</li> 
		 * <li>{@link EditComponentStartStateSettingsDialog#settingsSameIcon}.</li>
		 * </ol> 
		 * @param o1
		 * @param o2
		 * @return 0 if equal; else 1 if o1 is {@link EditComponentStartStateSettingsDialog#settingsSameIcon}.
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(Object o1, Object o2) {
			if(trace.getDebugCode("startstatell"))
				trace.out("startstatell", "CheckBoxComparator.compare("+trace.nh(o1)+", "+trace.nh(o2)+")");
			if(o1 instanceof JCheckBox && o2 instanceof JCheckBox)
				return String.CASE_INSENSITIVE_ORDER.compare(((JCheckBox) o1).getText(), ((JCheckBox) o2).getText());
			else
				return String.CASE_INSENSITIVE_ORDER.compare(o1.toString(), o2.toString());
		}
	}
	
	/** To sort the {@value EditComponentStartStateSettingsDialog#MATCH_COL}. */
	class MatchComparator implements Comparator<ImageIcon> {

		/**
		 * Order values as<ol>
		 * <li>null,</li>
		 * <li>{@link EditComponentStartStateSettingsDialog#settingsDifferIcon},</li> 
		 * <li>{@link EditComponentStartStateSettingsDialog#settingsSameIcon}.</li>
		 * </ol> 
		 * @param o1
		 * @param o2
		 * @return 0 if equal; else 1 if o1 is {@link EditComponentStartStateSettingsDialog#settingsSameIcon}.
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(ImageIcon o1, ImageIcon o2) {
			if(o1 == o2)
				return 0;
			else
				return (o1 == settingsSameIcon ? 1 : -1);
		}
		
	}

	/** The single renderer instance for all instance name cells. */
	private InstanceNameCheckBoxRenderer incbRenderer = new InstanceNameCheckBoxRenderer(); 

	/** The table model, for generating events. */
	private CIDTableModel cidTableModel; 
	
	/** The table itself. */
	private JTable cidTable = null; 

	/**
	 * A {@link JTable} with a custom cell renderer and editor for instance name columns.
	 */
	class CIDTable extends JTable {
		private static final long serialVersionUID = 201402211705L;

		CIDTable(TableModel model) {
			super(model, new ImmovableColumnColumnModel());
			setAutoCreateColumnsFromModel(true);  // needed since specified column model in constructor
			TableRowSorter sorter = new TableRowSorter(model);
			sorter.setComparator(TYPE_COL, String.CASE_INSENSITIVE_ORDER);
			sorter.setComparator(MATCH_COL, new MatchComparator());
			sorter.setComparator(PM_NAME_COL, new CheckBoxComparator(PM_NAME_COL));
			sorter.setComparator(SI_NAME_COL, new CheckBoxComparator(SI_NAME_COL));
			setRowSorter(sorter);
			
			FontMetrics fm = getFontMetrics(getTableHeader().getFont());
			int matchWidth = fm.stringWidth(model.getColumnName(MATCH_COL)) + 2*6;  // 6 appears to be padding
			if(trace.getDebugCode("startstate"))
				trace.out("startstate", String.format("CCSD.CIDTable<> matchWidth %d=%d(%s)+2*%d renderer %s",
						matchWidth,
						fm.stringWidth(model.getColumnName(MATCH_COL)),
						model.getColumnName(MATCH_COL),
						6,
						getTableHeader().getColumnModel().getColumn(MATCH_COL).getHeaderRenderer()));
			getColumnModel().getColumn(MATCH_COL).setPreferredWidth(matchWidth);  // header is wider than icon
			getColumnModel().getColumn(MATCH_COL).setMaxWidth(matchWidth);

			setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		
		public TableCellRenderer getCellRenderer(int row, int col) {
			if(trace.getDebugCode("startstate"))
				trace.out("startstate", String.format("CIDTable.getCellRenderer(%d,%d) toModel(%d,%d)=>cid %s",
						row, col, cidTable.convertRowIndexToModel(row), cidTable.convertColumnIndexToModel(col),
						cidList.get(cidTable.convertRowIndexToModel(row))));
			
			row = cidTable.convertRowIndexToModel(row);
			col = cidTable.convertColumnIndexToModel(col);
			String instanceName = null;
			if(col == PM_NAME_COL)
				instanceName = cidList.get(row).getPmCommName();
			else if(col == SI_NAME_COL)
				instanceName = cidList.get(row).getSiCommName();
			if(instanceName == null || instanceName.length() < 1)
				return super.getCellRenderer(row, col);
			else
				return incbRenderer;
		}
		
		/**
		 * Return a new editor for each instance name check box that might need editing.
		 * This is less efficient than the default scheme, which uses the same editor instance
		 * for all. In our case, we have to know the row and column at itemChanged() and cannot rely
		 * on {@link TableCellEditor#getTableCellEditorComponent(JTable, Object, boolean, int, int)}
		 * to supply the row and column needed.
		 * @param row
		 * @param col
		 * @return
		 * @see javax.swing.JTable#getCellEditor(int, int)
		 */
		public TableCellEditor getCellEditor(int row, int col) {
			if(trace.getDebugCode("startstate"))
				trace.out("startstate", String.format("CIDTable.getCellEditor(%d,%d) toModel(%d,%d)=>cid %s",
						row, col, cidTable.convertRowIndexToModel(row), cidTable.convertColumnIndexToModel(col),
						cidList.get(cidTable.convertRowIndexToModel(row))));
			
			row = cidTable.convertRowIndexToModel(row);
			col = cidTable.convertColumnIndexToModel(col);
			String instanceName = null;
			if(col == PM_NAME_COL)
				instanceName = cidList.get(row).getPmCommName();
			else if(col == SI_NAME_COL)
				instanceName = cidList.get(row).getSiCommName();
			if(instanceName == null || instanceName.length() < 1)
				return super.getCellEditor(row, col);
			else
				return new InstanceNameCheckBox(row, col);
		}
	}

	/**
	 * Send this message to the student interface.
	 * @param msg message to send
	 */
	public void sendToInterface(MessageObject msg) {
		if(msg == null)
			return;
		if(problemModel == null)
			return;
		if(problemModel.getController() == null)
			return;
		problemModel.getController().handleMessageUTP(msg);
	}

	/**
	 * Revise a checkbox with information from a {@link CompareInterfaceDescriptions}.
	 * Sets text and selected properties.
	 * @param row index into {@link EditComponentStartStateSettingsDialog#cidList}
	 * @param col should be {@link EditComponentStartStateSettingsDialog#PM_NAME_COL}
	 *            or {@link EditComponentStartStateSettingsDialog#SI_NAME_COL}
	 * @param cb object to update
	 * @return {@link CompareInterfaceDescriptions} object used
	 */
	public CompareInterfaceDescriptions updateCBFromCID(int row, int col, JCheckBox cb) {
		CompareInterfaceDescriptions cid = cidList.get(row);
		if(col == PM_NAME_COL) {
			cb.setText(cid.getPmCommName());
			if(cid.getAlert() != null)
				cb.setForeground(Color.red);
			cb.setSelected(cid.getChoice() == Choice.keepPM);
		} else if(col == SI_NAME_COL) {
			cb.setText(cid.getSiCommName());
			if(cid.getAlert() != null)
				cb.setForeground(Color.red);
			cb.setSelected(cid.getChoice() == Choice.saveSI);
		} else
			throw new IllegalArgumentException("Invalid column "+col+" in "+
					getClass().getSimpleName()+".udpateCBFromCID(); should be "+
					PM_NAME_COL+" or "+SI_NAME_COL);
		return cid;
	}

	/**
	 * Revise a {@link CompareInterfaceDescriptions} object with a new choice from a check box.
	 * @param row index into {@link EditComponentStartStateSettingsDialog#cidList}
	 * @param col should be {@link EditComponentStartStateSettingsDialog#PM_NAME_COL}
	 *            or {@link EditComponentStartStateSettingsDialog#SI_NAME_COL}
	 * @param newState true if check box checked, false if unchecked
	 * @return object updated
	 */
	private CompareInterfaceDescriptions updateCID(int row, int col, boolean newState) {
		Choice previous = null;
		CompareInterfaceDescriptions cid = cidList.get(row);
		if(col == PM_NAME_COL) {
			if(newState)
				previous = cid.setChoice(Choice.keepPM);
			else
				previous = cid.setChoice(Choice.omit);
		} else if(col == SI_NAME_COL) {
			if(newState)
				previous = cid.setChoice(Choice.saveSI);
			else
				previous = cid.setChoice(Choice.omit);				
		} else
			throw new InvalidStateException("Invalid column "+col+
					" in InstanceNameCheckBox.itemStateChanged(); should be "+
					PM_NAME_COL+" or "+SI_NAME_COL);
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "CCSD.updateCID("+row+","+col+","+newState+") now "+
					cid+"; previous choice "+previous.toChar());
		if(previous != cid.getChoice()) {
			int ok = JOptionPane.OK_OPTION;
			if(previous == Choice.keepPM) {
				if(ProblemModel.interpolatable(cid.getPmMsg()) &&      // pm msg used mass prod and 
						(cid.getChoice() == Choice.omit ||                // will be omitted or
						 !ProblemModel.interpolatable(cid.getSiMsg()))) { // replaced w/o mass prod
					String name = (cid.getPmCommName() == null ? cid.getSiCommName() : cid.getPmCommName());
					ok = JOptionPane.showConfirmDialog(getParent(),
							"Warning: about to discard settings in "+name+" configured for Mass Production",
							"Mass Production Warning",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				}
			}
			if(ok != JOptionPane.OK_OPTION)
				cid.setChoice(previous);
			cidTableModel.fireTableCellUpdated(row, (col == PM_NAME_COL ? SI_NAME_COL : PM_NAME_COL));
		}
		return cid;
	}

	/**
	 * Create a dialog if we've something to show. Does all work in a separate thread.
	 * @param controller the source of all knowledge
	 * @param activeWindow parent frame for the dialog
	 */
	public static void create(final BR_Controller controller, final AbstractCtatWindow activeWindow) {
		if(controller == null)
			return;
		final UniversalToolProxy utp = controller.getUniversalToolProxy();
		if(utp == null)
			return;
		
		Thread interfaceRebooter = new Thread(new Runnable() {
			public void run() {
				StartStateModel model = utp.getStartStateModel();
				if(trace.getDebugCode("startstate"))
					trace.out("startstate", "CCSD interfaceRebooter: nIntDescFrInt "+model.nInterfaceDescriptionsFromInterface());
				if(model.nInterfaceDescriptionsFromInterface() < 1) {
					boolean proceed = false;
					if(controller.isStudentInterfaceLocal())
						proceed = utp.rebootInterface(new AwaitCancel(Thread.currentThread(), activeWindow));
					else
						proceed = utp.getAllInterfaceDescriptions();
					if(!proceed) {
						Utils.showExceptionOccuredDialog(null, MUST_HAVE_INTERFACE_CONNECTED_TO_USE_FEATURE,
								NO_STUDENT_INTERFACE_CONNECTED);
						return;
					}
				}
		    	if(trace.getDebugCode("startstate"))
					trace.printStack("startstate", "***about to display***");

				javax.swing.SwingUtilities.invokeLater(new Runnable() {
		        	public void run() {
		        		JDialog ccsd = new EditComponentStartStateSettingsDialog(controller, activeWindow);
		        		ccsd.setVisible(true);
		        	}
		        });
			}
		}, "interfaceRebooter");
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "CCSD.create(): about to start interfaceRebooter");
		interfaceRebooter.start();
	}

	/**
	 * Create a display and show it.
	 * @param controller
	 * @param activeWindow
	 * @param compareInterfaceDescriptionMessages
	 */
	private EditComponentStartStateSettingsDialog(BR_Controller controller,
			AbstractCtatWindow activeWindow) {
		super(activeWindow, START_STATE_COMPONENT_SETTINGS);
		Point p = activeWindow.getLocation();
		p.move(p.x+100, p.y+100);
		setLocation(p);

		controller.addChangeListener(this);
		this.problemModel = controller.getProblemModel();
		init(controller.getUniversalToolProxy().getStartStateModel(), "CCSD.<init>");
		cidTable = new CIDTable(cidTableModel);

//		HTMLDocument doc = new HTMLDocument();
		JEditorPane msgLabel = new JEditorPane();
		msgLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
		msgLabel.setBorder(new EmptyBorder(5,5,5,5));
		msgLabel.setBackground(new Color(245,245,245));
		msgLabel.setEditable(false);
		msgLabel.setContentType("text/html");
		msgLabel.setText("<html>"+
				"<h3 font-family=\"sans-serif\">Tool for controlling component parameters from graph (.brd) files</h3>"+
				"<p font-family=\"sans-serif\">This display lists components in the student interface and shows whether the graph (.brd file) "+
				"includes settings for them. The Match column tells whether existing settings in the graph are "+
				"identical to those in the interface. Check a box to save those settings in the graph; uncheck "+
				"a box to revert to the original settings in the interface.</p>"+
				"<p>Use this tool when you need to select interface components whose settings may be controlled "+
				"from the graph. For example, if you want to set the choices in a CommComboBox "+
				"using Mass Production, you will need to copy the settings for that CommComboBox instance to "+
				"the graph, where the Mass Production %(variables)% can be replaced in each distinct problem.</p>"+
				"<p>This tool can be useful also when you have changed the student interface and need to revise "+
				"component settings in a previously-built graph. For example, if you remove "+
				"a component from the student interface whose settings were controlled in the graph, you can omit "+
				"those settings from the graph here.</p>"+
				"</html>");
		msgLabel.setCaretPosition(0);
		JScrollPane explPanel = new JScrollPane(msgLabel);
		explPanel.setPreferredSize(new Dimension(600, 200));
		explPanel.setMinimumSize(new Dimension(600, 200));
		
		retainAllGraphIDMsgs = new JButton(RETAIN_ALL_FROM_GRAPH);
		retainAllGraphIDMsgs.setToolTipText(RETAIN_ALL_FROM_GRAPH_TOOLTIP);
		retainAllGraphIDMsgs.addActionListener(this);
		
		omitAllGraphIDMsgs = new JButton(OMIT_ALL_FROM_GRAPH);
		omitAllGraphIDMsgs.setToolTipText(OMIT_ALL_FROM_GRAPH_TOOLTIP);
		omitAllGraphIDMsgs.addActionListener(this);
		
		retainAllInterfaceIDMsgs = new JButton(RETAIN_ALL_FROM_INTERFACE);
		retainAllInterfaceIDMsgs.setToolTipText(RETAIN_ALL_FROM_INTERFACE_TOOLTIP);
		retainAllInterfaceIDMsgs.addActionListener(this);
		
		omitAllInterfaceIDMsgs = new JButton(OMIT_ALL_FROM_INTERFACE);
		omitAllInterfaceIDMsgs.setToolTipText(OMIT_ALL_FROM_INTERFACE_TOOLTIP);
		omitAllInterfaceIDMsgs.addActionListener(this);
		
		JPanel doAllButtonPanel = new JPanel(new GridLayout(1, 4));
		doAllButtonPanel.add(retainAllGraphIDMsgs);
		doAllButtonPanel.add(omitAllGraphIDMsgs);
		doAllButtonPanel.add(retainAllInterfaceIDMsgs);
		doAllButtonPanel.add(omitAllInterfaceIDMsgs);

		JScrollPane pane = new JScrollPane(cidTable);

		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setPreferredSize(new Dimension(600,
				Math.max(cidList.size(), 25)*cidTable.getRowHeight()));
		tablePanel.setMinimumSize(new Dimension(600,
				Math.max(cidList.size(), 2)*cidTable.getRowHeight()));
		tablePanel.add(pane, BorderLayout.CENTER);

		okButton = new JButton("OK");
		okButton.setToolTipText("Copy the changes indicated above to the graph and close this dialog.");
		okButton.addActionListener(this);

		cancelButton = new JButton("Cancel");
		cancelButton.setToolTipText("Discard any changes made here and close this dialog.");
		cancelButton.addActionListener(this);
		
		JPanel okButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		okButtonPanel.add(okButton);
		okButtonPanel.add(cancelButton);
		
		JPanel topLevelPanel = new JPanel(new BorderLayout());
//		topLevelPanel.add(explPanel, BorderLayout.CENTER);
		topLevelPanel.add(doAllButtonPanel, BorderLayout.NORTH);
		topLevelPanel.add(tablePanel, BorderLayout.CENTER);
		topLevelPanel.add(okButtonPanel, BorderLayout.SOUTH);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				explPanel, topLevelPanel);
		splitPane.setResizeWeight(0.25);
		splitPane.setOpaque(true);  // content pane must be opaque

		setContentPane(splitPane);
		setModalityType(DEFAULT_MODALITY_TYPE);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
	}

	/**
	 * Replace {@link #model}, {@link #cidList} and {@link #cidTableModel} from the given {@link StartStateModel}. 
	 * @param startStateModel
	 * @param caller calling method's name, for debugging
	 */
	private void init(StartStateModel startStateModel, String caller) {
		this.model = startStateModel;
		this.cidList = model.compareInterfaceDescriptionMessages(problemModel);
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "CCSD.init() from "+caller+" cidList.size() "+(cidList == null ? -1 : cidList.size()));

		if(cidList == null || cidList.size() < 1) {
    		Utils.showExceptionOccuredDialog(null,
    				"<html><p>No component settings are available for comparison. You need to have an open"+
    				" graph and a connected student interface.</p></html>", "No component settings");
    		dispose();    		
    		return;
    	}
    	model.addPropertyChangeListener(this);
		cidTableModel = new CIDTableModel();
	}

	/**
	 * Extra housekeeping before {@link Window#dispose()}: remove this listener from
	 * {@link ProblemModel#getController()}'s list via {@link BR_Controller#removeChangeListener(ChangeListener)}.
	 * @see java.awt.Window#dispose()
	 */
	public void dispose() {
		if(problemModel != null && problemModel.getController() != null)
			problemModel.getController().removeChangeListener(this);
		super.dispose();
	}
	
	/**
	 * Called when a control button is clicked.
	 * @param e event from control
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == retainAllGraphIDMsgs)
			cidTableModel.retainAllGraphDescriptions();
		else if(e.getSource() == omitAllGraphIDMsgs)
			cidTableModel.omitAllGraphDescriptions();
		else if(e.getSource() == retainAllInterfaceIDMsgs)
			cidTableModel.retainAllInterfaceDescriptions();
		else if(e.getSource() == omitAllInterfaceIDMsgs)
			cidTableModel.omitAllInterfaceDescriptions();
		else {  // source should be ok or cancel button
			if(e.getSource() == okButton)
				model.applyEditsToProblemModel(problemModel, cidList, "Revised Component Settings");
//			if(problemModel.getController().getUniversalToolProxy() != null)
//				problemModel.getController().getUniversalToolProxy().resetCompareStartStateMessages();
			if(model != null)
		    	model.removePropertyChangeListener(this);
			dispose();
		}
	}

	/**
	 * On any change from {@link #model}, refresh {@link #cidList} and notify {@link #cidTableModel}.
	 * @param evt the event
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "CCSD.propertyChange("+evt+")");
		cidList = model.compareInterfaceDescriptionMessages(problemModel);
		cidTableModel.fireTableDataChanged();
	}

	public void stateChanged(ChangeEvent e) {
		UniversalToolProxy utp = (UniversalToolProxy)e.getSource();
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "STATE CHANGED: "+trace.nh(e.getSource())+" "+
					utp.getStudentInterfaceConnectionStatus());
		if(utp.getStudentInterfaceConnectionStatus().isConnected())
			utp.addStartStateListener(this);
		utp.getAllInterfaceDescriptions();
	}

	/**
	 * Replace {@link EditComponentStartStateSettingsDialog#cidList} with the result of
	 * {@link StartStateModel#compareInterfaceDescriptionMessages(ProblemModel)} and
	 * recreate {@link EditComponentStartStateSettingsDialog#cidTableModel}.
	 * @param e {@link EventObject#getSource()} must be a {@link StartStateModel}
	 * @see edu.cmu.pact.ctat.model.StartStateModel.Listener#startStateReceived(java.util.EventObject)
	 */
	public void startStateReceived(EventObject e) {
		init((StartStateModel) e.getSource(), "CCSD.startStateReceived()");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				cidTable.setModel(cidTableModel);				
			}
		});
	}
}

/**
 * A dialog to prompt the user to restart the student interface.
 */
class AwaitCancel extends Thread implements UniversalToolProxy.RebootInterfaceDialog {

	/** Component to dispose of when finished. */
	private JDialog dialog = null;
	
	/** Thread to interrupt if user cancels. */
	private final Thread threadToInterrupt;
	
	/** Parent frame. */
	private final Frame parent;
	
	/**
	 * @param threadToInterrupt value for {@link #threadToInterrupt}
	 */
	AwaitCancel(Thread threadToInterrupt, Frame parent) {
		super("AwaitCancel"+System.currentTimeMillis());
		this.threadToInterrupt = threadToInterrupt;
		this.parent = parent;
	}

	/**
	 * Display the dialog.
	 */
	public void invoke() {
		SwingUtilities.invokeLater(this);
	}
	
	/**
	 * Display a dialog that tells the author the student interface must be restarted.
	 * @return true if author wants to proceed; false to quit 
	 * @see pact.CommWidgets.UniversalToolProxy.RebootInterfaceDialog#confirm()
	 */
	public boolean confirm() {
		int proceed = JOptionPane.showConfirmDialog(parent,
				"To ensure that you have the initial settings of all"+
				"\ncomponents, unchanged by actions from the graph,"+
				"\nyou must close and restart the student interface."+
				"\nPlease press OK to proceed, Cancel to quit.",
				"Must restart student interface",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		return (proceed == JOptionPane.OK_OPTION);
	}

	/**
	 * Call {@link JDialog#dispose()} on {@link #dialog}.
	 */
	public void dispose() {
		dialog.dispose();		
	}
	
	/**
	 * Interrupt {@link #uiThread} and dispose of {@link #dialog}.
	 * @param evt
	 */
	private void cancelUI(EventObject evt) {
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "AwaitCancel.cancelUI("+evt.getClass().getSimpleName()+")");
		threadToInterrupt.interrupt();
		dispose();
	}

	/**
	 * Display the gui.
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "AwaitCancel.run() at top");

		JLabel msg = new JLabel("Waiting for you to restart the student interface.");
		JLabel req = new JLabel("Press Cancel to abort...");
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
		labelPanel.add(msg);
		labelPanel.add(Box.createRigidArea(new Dimension(0,5)));
		labelPanel.add(req);
		labelPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cancelUI(evt);
			};
		});
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(cancel);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(labelPanel, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		panel.setOpaque(true);
		
		dialog = new JDialog(parent, "Awaiting student interface restart");
		Point p = parent.getLocation();
		p.move(p.x+100, p.y+100);
		dialog.setLocation(p);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent evt) {
		    	cancelUI(evt);
		    }
		});
		dialog.setContentPane(panel);
		dialog.pack();
		dialog.setVisible(true);

		if(trace.getDebugCode("startstate"))
			trace.out("startstate", "AwaitCancel.run() just called setVisible()");
	}
}
