
/* 
	JCommListBeanInfo.java

	Author:			mpschnei
	Description:	BeanInfo for class CommSpreadsheet
*/

package pact.CommWidgets;
import java.beans.BeanDescriptor;

public class JCommChooserBeanInfo extends java.beans.SimpleBeanInfo
{

	// Generated BeanInfo just gives the bean its icons.
	// Small icon is in JCommList.gif
	// Large icon is in CommListL.gif
	// It is expected that the contents of the icon files will be changed to suit your bean.

    
    public java.beans.BeanDescriptor getBeanDescriptor() {
        BeanDescriptor desc = new BeanDescriptor(JCommChooser.class);
        desc.setValue("isContainer", Boolean.FALSE);
        return desc;
    }

    public java.awt.Image getIcon(int iconKind)
	{
		java.awt.Image icon = null;
		switch (iconKind)
		{
			case ICON_COLOR_16x16:
	    		icon = loadImage("JCommChooser.gif");
				break;			
			case ICON_COLOR_32x32:
				icon = loadImage("JCommChooser.gif");
			default:
				break;
		}
		return icon;
	}
	
	
		
	
}

/* JCommListBeanInfo.java */
