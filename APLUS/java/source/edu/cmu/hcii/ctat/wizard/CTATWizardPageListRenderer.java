package edu.cmu.hcii.ctat.wizard;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * 
 */
public class CTATWizardPageListRenderer extends JLabel implements ListCellRenderer 
{
	private static final long serialVersionUID = -3192181979337623468L;

	/**
	 * 
	 */
	public CTATWizardPageListRenderer() 
    {
		setFont(new Font("Dialog", 1, 10));
		setHorizontalAlignment (SwingConstants.CENTER); 
        setOpaque (true);
        setBorder (new EmptyBorder(3,3,3,3));
    }
	/**
	 * 
	 */
    public Component getListCellRendererComponent (JList list, 
    											   Object value, 
    											   int index, 
    											   boolean isSelected, 
    											   boolean cellHasFocus) 
    {
        setText(value.toString());
     
        if (cellHasFocus==true)
        {
        	setBackground(new Color (49,106,197));
        }
        else
        {
        	if (isSelected==true)
        	{
        		setBackground (new Color (49,106,197));
        	}
        	else
        	{
        		if (index % 2 == 0) 
        			setBackground(new Color (250,250,250));
        		else 
        			setBackground(new Color (238,238,238));
        	}	
        }	

        return this;
    }
}
