/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2011-07-19 15:54:33 -0400 (Tue, 19 Jul 2011) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/*
 * 
 */
class CTATSheetColorEditor extends AbstractCellEditor implements TableCellEditor 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JButton delegate = new JButton();

	Color savedColor;

	/**
	 *
	 */  
	public CTATSheetColorEditor() 
	{
		ActionListener actionListener = new ActionListener() 
		{
			public void actionPerformed(ActionEvent actionEvent) 
			{
				Color color = JColorChooser.showDialog(delegate, "Color Chooser", savedColor);
				changeColor(color);
			}
		};
		
		delegate.addActionListener(actionListener);
	}
	/**
	 * Implement the one CellEditor method that AbstractCellEditor doesn't.
	 */
	public Object getCellEditorValue() 
	{
		return savedColor;
	}
	/**
	 *
	 */
	private void changeColor(Color color) 
	{
		if (color != null) 
		{
			savedColor = color;
			delegate.setBackground(color);
		}
	}
	/**
	 *
	 */
	public Component getTableCellEditorComponent (JTable table, Object value, boolean isSelected,int row, int column) 
	{
		changeColor((Color) value);
		return delegate;
	}
}
