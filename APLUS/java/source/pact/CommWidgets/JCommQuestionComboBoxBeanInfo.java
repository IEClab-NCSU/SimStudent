/*
 * Created on Mar 16, 2004
 *
 */
package pact.CommWidgets;

import java.beans.BeanDescriptor;

/**
 * @author sanket
 *
 */
public class JCommQuestionComboBoxBeanInfo extends java.beans.SimpleBeanInfo{
	
	public java.beans.BeanDescriptor getBeanDescriptor() {
		BeanDescriptor desc = new BeanDescriptor(JCommQuestionComboBox.class);
		desc.setValue("isContainer", Boolean.FALSE);
		return desc;
	}
	
	public java.awt.Image getIcon(int iconKind)
	{
		java.awt.Image icon = null;
		switch (iconKind)
		{
			case ICON_COLOR_16x16:
				icon = loadImage("JCommQuestionComboBox.gif");
				break;			
			case ICON_COLOR_32x32:
				icon = loadImage("JCommQuestionComboBoxL.gif");
			default:
				break;
		}
		return icon;
	}
}
