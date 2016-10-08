/* 
	JCommButtonBeanInfo.java

	Author:			mpschnei
	Description:	BeanInfo for class JCommButton
*/

package pact.CommWidgets;
import java.beans.BeanDescriptor;
public class JCommButtonBeanInfo extends java.beans.SimpleBeanInfo
{

	// Generated BeanInfo just gives the bean its icons.
	// Small icon is in JCommButton.gif
	// Large icon is in CommButtonL.gif
	// It is expected that the contents of the icon files will be changed to suit your bean.

    
    public java.beans.BeanDescriptor getBeanDescriptor() {
        BeanDescriptor desc = new BeanDescriptor(JCommButton.class);
        desc.setValue("isContainer", Boolean.FALSE);
        return desc;
    }

    public java.awt.Image getIcon(int iconKind)
	{
		java.awt.Image icon = null;
		switch (iconKind)
		{
			case ICON_COLOR_16x16:
	    		icon = loadImage("JCommButton.gif");
				break;			
			case ICON_COLOR_32x32:
				icon = loadImage("JCommButtonL.gif");
			default:
				break;
		}
		return icon;
	}

        public boolean isContainer () { return false; }
}

/* JCommButtonBeanInfo.java */
