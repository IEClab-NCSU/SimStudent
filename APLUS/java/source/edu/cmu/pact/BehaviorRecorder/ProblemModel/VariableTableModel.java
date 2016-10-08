package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.util.Comparator;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import edu.cmu.pact.Utilities.trace;

public class VariableTableModel extends AbstractTableModel {
	
	private VariableTable vt;
	private String[] keys;
	private Comparator<String> listCompare;
	
	//Every VaraibleTableModel is stamped with a unique int value to tell them apart in tracing
	private static int count =0;
	private int instance;

	public VariableTableModel(VariableTable vartab) {
		//I think most of this code is actually here to facilitate tracing and may not be all that necessary -Erik
		if (trace.getDebugCode("vtm")) trace.outNT("vtm", "VariableTableModel Constructor");
		instance = count++;
		if (trace.getDebugCode("vtm")) trace.outNT("vtm", "VariableTableModel #"+instance);
		vt=vartab;
		vartab.setModel(this);
		Set<String> set=vt.keySet();
		keys = new String[0];
		keys = set.toArray(keys);
		if (trace.getDebugCode("vtm")) trace.outNT("vtm","vt = "+vt);
		if (trace.getDebugCode("vtm")) trace.outNT("vtm", "table/model circle is "+(vt.getModel()==this ? "true":"false"));
		for(int i=0;i<keys.length;i++) {
			if (trace.getDebugCode("vtm")) trace.outNT("vtm", "keys["+i+"] = "+keys[i]);
		}
	}
	
	@Override
	//there should never be more than 2 columns in the VariableTable
	public int getColumnCount() {
		//trace.outNT("vtm", "getColumnCount()");
		return 2;
	}

	@Override
	//the number of rows should correspond to the number of Variables in the table
	public int getRowCount() {
		//trace.outNT("vtm", "getRowCount = "+keys.length);
		return keys.length;
	}

	@Override
	//returns the value at each index for filling the table
	public Object getValueAt(int rowIndex, int columnIndex) {
		//trace.outNT("vtm","getValueAt("+rowIndex+","+columnIndex+")");
		switch (columnIndex) {
		case 0:
			return keys[rowIndex];
		case 1:
			Object value = vt.get(keys[rowIndex]);
			return (value == null ? null : value.toString());
		default:
			return null;
		}
	}
	
	@Override
	//sets the names of each column, made it non-dynamic because I saw no point in making it an option
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Variable";
		case 1:
			return "Value";
		default:
			return "Other";
		}
	}
	
	@Override
	//don't want cells to be editable, it would only be visually anyway
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		//trace.outNT("vtm", "isCellEditable()");
		return false;
	}
	@Override
	//this will only ever be a table of Strings so return type String regardless of column
	public Class getColumnClass(int index){
		return String.class;	
	}
	
	//this function is called by the variableTable to tell the model that the data has changed
	//the argument is purely for tracing purposes
	public void updateTable(boolean replace, Object key,int vtNum) {
		if (trace.getDebugCode("vtm")) trace.outNT("vtm","updateTableModel #"+instance+" which is part of vt #"+vt.getInstance()+" called from vt #"+vtNum);
		Set<String> set=vt.keySet();
		keys = new String[set.size()];
		keys = set.toArray(keys);
		if (trace.getDebugCode("vtm")) trace.outNT("vtm", "keys.length = "+keys.length);
		if (trace.getDebugCode("vtm")) trace.outNT("vtm", "getRowCount = "+getRowCount());
		//look up the TableModelListener fire conventions
		if (replace)
			fireTableCellUpdated(indexOf(key.toString()), 1);
		else
			fireTableRowsInserted(keys.length,keys.length);
	}
	
	public void updateTableCleared(){
		if (trace.getDebugCode("vtm")) trace.outNT("vtm","TableModel #"+instance+"'s vt #"+vt.getInstance()+" cleared");
		this.fireTableDataChanged();
	}
	
	private int indexOf(String key){
		for (int i=0;i<keys.length;i++){
			if(keys[i].equalsIgnoreCase(key))
				return i;
		}
		return -1;
	}
	
	//sets the variabletable within this variabltTableModel
	public void setVariableTable(VariableTable vtNew){
		vt = vtNew;
		fireTableDataChanged();
	}
	
	public int getVTInstance(){
		return vt.getInstance();
	}
	
	//returns the instance number of this specific VariableTableModel
	public int getInstance(){
		return instance;
	}
}



