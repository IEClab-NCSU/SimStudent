/*
 * Copyright 2005 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import jess.Deftemplate;
import jess.Fact;
import jess.JessException;
import jess.Value;
import edu.cmu.pact.Utilities.XMLSpecialCharsTransform;
import edu.cmu.pact.Utilities.trace;

/**
 * A {@link TableModel} built on a list of {@link VariableBindingNode}s.
 */
public class VBNTableModel extends AbstractTableModel {
	
	/**
	 * An immutable iterator over variable names.
	 */
	private class NameIterator implements Iterator {

		/** Underlying iterator on {@link VBNTableModel#entries}. */
		private Iterator delegate;
		
		/**
		 * Sets {@link #delegate}.
		 */
		public NameIterator() {
			delegate = entries.iterator();
		}

		/**
		 * @throws UnsupportedOperationException
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException("iterator is immutable");
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return delegate.hasNext();
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		public Object next() {
			Entry entry = (Entry) delegate.next();
			return entry.getDisplayName();
		}

	}
	/**
	 * Group together the attributes we want from a variable.
	 */
	public class Entry {
		
		/** The input list of {@link VariableBindingNode}s. */
		private final VariableBindingNode vbn;

		/** The list of variable names for display. */
		private String displayName = null;

		/** The list of variable values for display. Entries are Strings. */
		private String displayValue = null;
		
		/** A tool tip for this value. */
		private String toolTipText = null; 
		
		/** Constructor initializes final field. */
		private Entry(VariableBindingNode vbn) {
			this.vbn = vbn;
		}
		
		/** Return a display field. */
		private String getDisplayField(int columnIndex) {
			if (columnIndex == 0)
				return getDisplayName();
			else if (columnIndex == 1)
				return getDisplayValue();
			else
				throw new ArrayIndexOutOfBoundsException("columnIndex "+columnIndex+
						" while columnCount is "+getColumnCount());
		}
		
		/** Return the display name. Calls {@link #init()} if name is null. */
		public String getDisplayName() {
			if (displayName == null)
				init();
			return displayName;
		}
		
		/** Return the display value. Calls {@link #init()} if {@link #displayName} is null. */
                public String getDisplayValue() {
			if (displayName == null)
				init();
			return displayValue;
		}
                
                
		
		/** Return the tool tip. Calls {@link #init()} if name is null. */
		public String getToolTipText() {
			if (toolTipText != null)
				return toolTipText;
			if (displayName == null)
				init();
			try {
				Fact fact = vbn.getVariableValue().factValue(whyNot.getContext());
				StringBuffer sb = new StringBuffer("<html>");
				sb.append(factToHtmlTable(fact));
				sb.append("</html>");
				toolTipText = sb.toString();
//                                    System.out.println("getToolTipText: did not enter JessException");
			} catch (JessException je) {
//			    System.out.println("getToolTipText: entered JessException");
				toolTipText = displayValue;
			}
//                        System.out.println("getToolTipText: displayName = " + displayName);
//                        System.out.println("getToolTipText: toolTipText = " + toolTipText);
			return toolTipText;
		}
		
		/**
		 * Initialize the display fields from the {@link #vbn}.
		 * Sets {@link #displayName}, {@link #displayValue}.  
		 */
		private void init() {
			StringBuffer sb = new StringBuffer(vbn.getExtVariableName());
			if (sb.charAt(0) != '(')           // if not an expression
				sb.insert(0, '?');             // FIXME this copied from WhyNot
			displayName = sb.toString();
			displayValue = vbn.formatValue(whyNot.getContext());
		}
	}
	
	/** How many columns we can display. */
	private static final int COLUMN_COUNT = 2; 
	
	/** The column names: "Name", "Value". */
	private static final String[] columnNames = { "Name", "Value" };
	
	/** The {@link WhyNot} instance we serve. */
	private final WhyNot whyNot;
	
	/** List of entries. Element type is {@link #e */
	private List entries = new ArrayList();

        public List getEntries() {
            return entries;
        }

        
	/**
	 * Create the table model from the list of {@link VariableBindingNode}s.
	 * @param whyNot the {@link WhyNot} instance to serve
	 * @param vbnList List of nodes; if null, creates empty list.
	 */
	public VBNTableModel(WhyNot whyNot, List vbnList) {
		super();
		this.whyNot = whyNot;
		if (vbnList == null)
			return;
		for (Iterator it = vbnList.iterator(); it.hasNext();)
			entries.add(new Entry((VariableBindingNode) it.next()));
	}
	
	/**
	 * Add an entry to {@link #entries}.
	 * @param vbn 
	 */
	void add(VariableBindingNode vbn) {
		entries.add(new Entry(vbn));
	}
	
	/**
	 * Return an iterator over the {@link #entries} list.
	 * @return entries.iterator()
	 */
	Iterator nameIterator() {
		return new NameIterator();
	}
	
	/**
	 * Given a variable's display name, find its entry in the table.
	 * @param displayName display name for variable
	 * @return index in {@link #entries}, if found; else -1
	 */
	public int findDisplayName(String displayName) {
		if (displayName == null)
			return -1;
		int rowIndex = 0;
		for (Iterator it = entries.iterator(); it.hasNext(); ++rowIndex) {
			Entry entry = (Entry) it.next();
			if (displayName.equals(entry.getDisplayName()))
				return rowIndex;
		}
		return -1;
	}
	
	/** Return the display name. Calls {@link #init()} if name is null. */
	public String getDisplayName(int rowIndex) {
		return getEntry(rowIndex).getDisplayName();
	}
	
	/** Return the display value. Calls {@link #init()} if {@link #displayName} is null. */
	public String getDisplayValue(int rowIndex) {
		return getEntry(rowIndex).getDisplayValue();
	}
	
	/**
	 * Get the tool tip for the given row. The text can be HTML.
	 * @param rowIndex
	 * @return tool tip text; null if none 
	 */
	public String getToolTipText(int rowIndex) {
		Entry entry = getEntry(rowIndex);
		return entry.getToolTipText();
	}
	
	/**
	 * Return the name for the indexed column.
	 * @see AbstractTableModel#findColumn(java.lang.String)
	 */
	public int findColumn(String columnName) {
		for (int i = 0; i < columnNames.length; ++i) {
			if (columnNames[i].equalsIgnoreCase(columnName))
				return i;
		}
		return -1;
	}
	
	/**
	 * Return the class for each column.
	 * @return String.class
	 * @see AbstractTableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int columnIndex) {
		return String.class;
	}
	
	/**
	 * Always returns false.
	 * @return false
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	/**
	 * Return the name for the indexed column.
	 * @see AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int columnIndex) {
		if (columnIndex < 0 || columnNames.length <= columnIndex)
			return "";
		else
			return columnNames[columnIndex];
	}

	/**
	 * Constant function for name and value columns.
	 * @return 2
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return COLUMN_COUNT;
	}

	/**
	 * Length of list of variables.
	 * @return {@link #vbnList}.size()
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return entries.size();
	}
	
	/**
	 * Check a rowIndex against the size of the {@link #entries} table.
	 * @param rowIndex
	 * @return Entry at rowIndex
	 * @throws ArrayIndexOutOfBoundsException if argument out of bounds
	 */
	private Entry getEntry(int rowIndex) {
		if (rowIndex < 0 || getRowCount() <= rowIndex)
			throw new ArrayIndexOutOfBoundsException("rowIndex "+rowIndex+
					" while rowCount is "+getRowCount());
		return (Entry) entries.get(rowIndex);
	}

	/**
	 * Returns the {@link VBNTableModel.Entry#displayName} or displayValue.
	 * @param rowIndex index into List {@link #entries}
	 * @param columnIndex see {@link #columnNames} for meanings
	 * @return value at given index
	 * @throws ArrayIndexOutOfBoundsException if argument out of bounds
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		Entry entry = getEntry(rowIndex);
		if (columnIndex < 0 || getColumnCount() <= columnIndex)
			throw new ArrayIndexOutOfBoundsException("columnIndex "+columnIndex+
					" while columnCount is "+getColumnCount());
		return entry.getDisplayField(columnIndex);
	}
	
	/**
	 * Returns the {@link VBNTableModel.Entry#displayName} or displayValue.
	 * @param rowIndex index into List {@link #entries}
	 * @param columnIndex see {@link #columnNames} for meanings
	 * @return value at given index
	 * @throws ArrayIndexOutOfBoundsException if argument out of bounds
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getObjectAt(int rowIndex, int columnIndex) {
		Entry entry = getEntry(rowIndex);
		if (columnIndex < 0 || getColumnCount() <= columnIndex)
			throw new ArrayIndexOutOfBoundsException("columnIndex "+columnIndex+
					" while columnCount is "+getColumnCount());
		return entry;
	}
	
	/**
	 * Format the fields of a {@link Fact} in an HTML table.
	 * @param fact Fact to format
	 * @return String beginning with opening tag <code>&lt;table&gt;</code>
	 *         and ending with closing tag.
	 */
	public static String factToHtmlTable(Fact fact) {
		StringBuffer sb = new StringBuffer("<html><table border=\"1\" cellpadding=\"1\">");
		int id = fact.getFactId();
		Deftemplate dt = fact.getDeftemplate();
		sb.append("\n<caption>"+dt.getBaseName()+" fact-").append(id).append("</caption>");
		for (int i = 0; i < dt.getNSlots(); ++i) {
			String sName = "", sVal = "";
			try {
				sName = dt.getSlotName(i);
			} catch (JessException je) {
				trace.err("Error getting name for slot "+i+" in Fact-"+id+": "+je);
				continue;
			}
			try {
				Value val = fact.get(i);
				sVal = XMLSpecialCharsTransform.transformSpecialChars(val.toString());
			} catch (JessException je) {
				trace.err("Error getting value for slot "+i+"("+sName+") in Fact-"+id+": "+je);
				sVal = "(error)";
			}
			sb.append("\n<tr><td>").append(sName).append("</td>");
			sb.append("<td>").append(sVal).append("</td></tr>");
		}
		return sb.append("\n</table>").toString();
	}	
}
