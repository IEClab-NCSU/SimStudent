/*
 * Created on Mar 21, 2004
 *
 */
package pact.CommWidgets;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * @author sanket
 *
 */
public class ComboboxComponent extends QuestionComponent implements ActionListener{
	/** model for the combox to hold the choices */
	DefaultComboBoxModel model = new DefaultComboBoxModel();
	/** stores the default background color */
	Color backgroundNormalColor;
	/** indicates when the widget should be locked */
//	private boolean locked = false;
		
	public ComboboxComponent(){
		this.answer = new JComboBox();
		// add the action listener
		((JComboBox)this.answer).addActionListener(this);
		// set the default font for the choices
		this.answer.setFont(JCommWidget.getDefaultFont());
		// set the model
		((JComboBox)this.answer).setModel(model);
		// put the label and the combobox on the panel
		this.createInterface();
		// get the back color
		this.backgroundNormalColor = this.answer.getBackground();
//		((JComboBox)this.answer)
	}
	
	/**
	 * returns the currently selected value
	 * @see pact.CommWidgets.QuestionComponent#getValue()
	 */
	public String getValue() {
		Object obj = ((JComboBox)this.answer).getSelectedItem();
		if(obj != null){ 
			return obj.toString();
		}
		return "";
	}

	/** 
	 * Sets the foreground color as c and also the back color to the default value 
	 * @see pact.CommWidgets.QuestionComponent#setColor(java.awt.Color)
	 */
	public void setColor(Color c) {
		((JComboBox)this.answer).setForeground(c);
		((JComboBox)this.answer).setBackground(backgroundNormalColor);
	}

	/**
	 * Sets the current selected value to str
	 * @see pact.CommWidgets.QuestionComponent#setValue(java.lang.String)
	 */
	public void setValue(String str) {
		((JComboBox)this.answer).setSelectedItem(str);
	}

	/**
	 * This method parses a comma separated list of strings and 
	 * adds them to the combobox model.
	 * @param string
	 * @param combobox
	 */
	public void addTextToCombobox(String string) {
		this.model.removeAllElements();
		this.model.addElement("");
		StringTokenizer st = new StringTokenizer(string, ",");
		while(st.hasMoreTokens()){
			this.model.addElement(st.nextToken().trim());
		}
	}

	
/*	public void addItemListener(ItemListener obj){
		((JComboBox)this.answer).addItemListener(obj);
	}
*/
	/**
	 * set the lock on the widget
	 * @see pact.CommWidgets.QuestionComponent#setLock(boolean)
	 */
//	public void setLock(boolean b) {
//		this.locked = b;
//	}

	/**
	 * @returns the previously selected value
	 */
/*	public synchronized String getPrevValue() {
		return prevValue;
	}
*/
	/**
	 * set the previous selected value
	 * @param prevValue
	 */
/*	public synchronized void setPrevValue(String prevValue) {
		this.prevValue = prevValue;
	}
*/	
	/**
	 * @returns the lock status of the widget
	 */
//	public boolean getLocked(){
//		return this.locked;
//	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
	}

	/* (non-Javadoc)
	 * @see pact.CommWidgets.QuestionComponent#addActionListener(java.awt.event.ActionListener)
	 */
	public void addActionListener(ActionListener obj) {
		((JComboBox)this.answer).addActionListener(obj);
	}

	/* (non-Javadoc)
	 * @see pact.CommWidgets.QuestionComponent#setLock(boolean)
	 */
//	public void setLock(boolean b) {
		// TODO Auto-generated method stub
		
//	}

}
