/*
 * Created on Apr 11, 2004
 */
package pact.CommWidgets;

/**
 * @author supaleka
 * @see pact.CommWidgets
 * @version Apr 11, 2004
 */


import java.beans.BeanDescriptor;

/**
 * @author sanket
 *
 */
public class JCommMultipleChoiceCheckBoxBeanInfo extends java.beans.SimpleBeanInfo{

	public java.beans.BeanDescriptor getBeanDescriptor() {
		BeanDescriptor desc = new BeanDescriptor(JCommMultipleChoiceCheckBox.class);
		desc.setValue("isContainer", Boolean.FALSE);
		return desc;
	}

	public java.awt.Image getIcon(int iconKind)
	{
		java.awt.Image icon = null;
		switch (iconKind)
		{
			case ICON_COLOR_16x16:
				icon = loadImage("JCommMultipleChoiceCheckBox.gif");
				break;			
			case ICON_COLOR_32x32:
				icon = loadImage("JCommMultipleChoiceCheckBox.gif");
			default:
				break;
		}
		return icon;
	}
	

}
