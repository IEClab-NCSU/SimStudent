/*
 * Created on Mar 21, 2004
 *
 */
package pact.CommWidgets;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class represents a component of a question
 * This component consists of a panel containing a label
 * and some other component like text box, combobox as per 
 * the needs of the different question types.
 * This class also controls the layout within this component
 * so ideally one question can be made up of many other 
 * question components
 * @author sanket
 *
 */
abstract public class QuestionComponent extends JPanel {
	/**
	 * answer field like text field or combobox
	 */
	JComponent answer;
	/**
	 * Label for this component
	 */
	JLabel label;
	/**
	 * layout for this panel
	 * 1 - flow layout horizontal on the same line
	 * 2 - vertical label on one line and answer below it
	 */
	int componentLayout = 1;
	/**
	 * indicates whether the answer and the label should be of the same 
	 * size when laying them vertically.
	 */
	boolean sameSize = false;
	
	public QuestionComponent(){
		this.label = new JLabel("");
		this.label.setFont(JCommWidget.getDefaultFont());
		this.label.setFocusable(false);
	}

	public void createInterface(){
		// set the layout
		if(this.componentLayout != 1){
			// use vertical layout
			if(!this.sameSize){
				// answer size not equal to the size of the label
				GridBagLayout gb = new GridBagLayout();
				GridBagConstraints gbc = new GridBagConstraints();
				this.setLayout(gb);
			
				// add the label
				gbc.gridx = 0;
				gbc.gridy = 0;
				gbc.gridwidth = 3;
				gb.setConstraints(label, gbc);
				this.add(label);
			
				// add the answer field
				gbc.gridx = 0;
				gbc.gridy = 1;
				gbc.gridwidth = 1;
				gb.setConstraints(answer, gbc);
				this.add(answer);
				
				this.repaint();
				return;
			}else{
				// make horizontal size of the answer equal to that of the 
				// label
				this.setLayout(new GridLayout(2,1));
			}
		}else if(this.componentLayout == 1){
			// use flow layout
			this.setLayout(new FlowLayout());
		}
		// add the label
		this.add(this.label);
		// add the answer
		this.add(this.answer);
		this.repaint();
	}

	abstract public String getValue();
	
	abstract public void setColor(Color c);

	abstract public void setValue(String str);
				
	public static void main(String[] args) {
	}
	/**
	 * @return
	 */
	public synchronized String getLabelTxt() {
		return label.getText();
	}

	/**
	 * @param labelTxt
	 */
	public synchronized void setLabelTxt(String labelTxt) {
		this.label.setText(labelTxt);
		this.repaint();
		this.validate();
	}

	/**
	 * @return
	 */
	public synchronized int getComponentLayout() {
		return componentLayout;
	}

	/**
	 * @param layout
	 */
	public synchronized void setComponentLayout(int layout) {
		this.componentLayout = layout;
	}

	/**
	 * @return
	 */
	public synchronized boolean isSameSize() {
		return sameSize;
	}

	/**
	 * @param sameSize
	 */
	public synchronized void setSameSize(boolean sameSize) {
		this.sameSize = sameSize;
	}

	/**
	 * @param obj
	 */
	abstract public void addActionListener(ActionListener obj);

	/**
	 * @param b
	 */

	public void setEnabled(boolean b){
		for(int i = 0; i < this.getComponents().length; i++){
			this.getComponent(i).setEnabled(b);
		}
	}
}
