package edu.cmu.pact.miss.PeerLearning.GameShow;

import java.awt.event.MouseEvent;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;

public class ProblemBankTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] columnNames;
    private Object[][] data;
    
    protected String[] columnToolTips = {
    	    "Generated Problems to Try Solving", 
    	    "Number of Times Students Tried Problems Similar to This One", 
    	    //"Number of Times Students Got Problems Like This One Correct",
    	    //"Percentage of the Time Students Got Problems Like This One Correct",
    	    "Estimated Difficulty of the Problem Based on Percentage Correct\n" +
    	    "1 Star is Easiest and 5 Stars is Hardest"
    };

    
    
    public ProblemBankTableModel(String[] columns, Object[][] datas)
    {
    	columnNames = columns;
    	data = datas;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }




}
