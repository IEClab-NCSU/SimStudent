
/* 
	JCommComposerBeanInfo.java

	Author:			mpschnei
	Description:	BeanInfo for class CommSpreadsheet
*/

package pact.CommWidgets;
import java.beans.BeanDescriptor;

public class JCommLabelBeanInfo extends java.beans.SimpleBeanInfo
{

    
    public java.beans.BeanDescriptor getBeanDescriptor() {
        BeanDescriptor desc = new BeanDescriptor(JCommLabel.class);
        desc.setValue("isContainer", Boolean.FALSE);
        return desc;
    }

    public java.awt.Image getIcon(int iconKind)
	{
		java.awt.Image icon = null;
		switch (iconKind)
		{
			case ICON_COLOR_16x16:
	    		icon = loadImage("JCommLabel.gif");
				break;			
			case ICON_COLOR_32x32:
				icon = loadImage("JCommLabelL.gif");
			default:
				break;
		}
		return icon;
	}	
	
}

