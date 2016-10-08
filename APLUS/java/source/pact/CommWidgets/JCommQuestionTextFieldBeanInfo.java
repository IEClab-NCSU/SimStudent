/*
 * Created on Dec 18, 2003
 *
 */
package pact.CommWidgets;
import java.beans.BeanDescriptor;

/**
 * @author sanket
 *
 */
public class JCommQuestionTextFieldBeanInfo extends java.beans.SimpleBeanInfo{
	public java.beans.BeanDescriptor getBeanDescriptor() {
		BeanDescriptor desc = new BeanDescriptor(JCommQuestionTextField.class);
		desc.setValue("isContainer", Boolean.FALSE);
		return desc;
	}
    public java.awt.Image getIcon(int iconKind)
	{
		java.awt.Image icon = null;
		switch (iconKind)
		{
			case ICON_COLOR_16x16:
	    		icon = loadImage("JCommQuestionTextField.gif");
				break;			
			case ICON_COLOR_32x32:
				icon = loadImage("JCommQuestionTextFieldL.gif");
			default:
				break;
		}
		return icon;
	}
	

}
