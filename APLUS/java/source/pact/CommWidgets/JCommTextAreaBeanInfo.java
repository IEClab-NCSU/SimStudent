
/* 
	JCommTextAreaBeanInfo.java

	Author:			mpschnei
	Description:	BeanInfo for class JCommTextArea
*/

package pact.CommWidgets;

import java.beans.BeanDescriptor;

public class JCommTextAreaBeanInfo extends java.beans.SimpleBeanInfo
{

	/**
	 * @return default {@link BeanDescriptor#BeanDescriptor(Class)}
	 *         with "isContainer" set false
	 * @see java.beans.SimpleBeanInfo#getBeanDescriptor()
	 */
    public java.beans.BeanDescriptor getBeanDescriptor() {
        BeanDescriptor desc = new BeanDescriptor(JCommTextArea.class);
        desc.setValue("isContainer", Boolean.FALSE);
        return desc;
    }

	// Generated BeanInfo just gives the bean its icons.
	// Small icon is in JCommTextArea.gif
	// Large icon is in CommTextAreaL.gif
	// It is expected that the contents of the icon files will be changed to suit your bean.

    public java.awt.Image getIcon(int iconKind)
	{
		java.awt.Image icon = null;
		switch (iconKind)
		{
			case ICON_COLOR_16x16:
	    		icon = loadImage("JCommTextArea.gif");
				break;			
			case ICON_COLOR_32x32:
				icon = loadImage("JCommTextAreaL.gif");
			default:
				break;
		}
		return icon;
	}
}

/* JCommTextAreaBeanInfo.java */
