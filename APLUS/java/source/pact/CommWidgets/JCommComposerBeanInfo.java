
/* 
	JCommComposerBeanInfo.java

	Author:			mpschnei
	Description:	BeanInfo for class CommSpreadsheet
*/

package pact.CommWidgets;
import java.beans.BeanDescriptor;

public class JCommComposerBeanInfo extends java.beans.SimpleBeanInfo
{

	// Generated BeanInfo just gives the bean its icons.
	// Small icon is in JCommComposer.gif
	// Large icon is in JCommComposer.gif
	// It is expected that the contents of the icon files will be changed to suit your bean.

    
    public java.beans.BeanDescriptor getBeanDescriptor() {
        BeanDescriptor desc = new BeanDescriptor(JCommComposer.class);
        desc.setValue("isContainer", Boolean.FALSE);
        return desc;
    }

    public java.awt.Image getIcon(int iconKind)
	{
		java.awt.Image icon = null;
		switch (iconKind)
		{
			case ICON_COLOR_16x16:
	    		icon = loadImage("JCommComposer.gif");
				break;			
			case ICON_COLOR_32x32:
				icon = loadImage("JCommComposer.gif");
			default:
				break;
		}
		return icon;
	}	
	
}

