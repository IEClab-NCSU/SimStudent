/* 
	CommSpreadsheetBeanInfo.java

	Author:			mpschnei
	Description:	BeanInfo for class CommSpreadsheet
*/

package pact.CommWidgets;
import java.beans.BeanDescriptor;

public class JCommTableBeanInfo extends java.beans.SimpleBeanInfo
{
    
    public java.beans.BeanDescriptor getBeanDescriptor() {
        BeanDescriptor desc = new BeanDescriptor(JCommTable.class);
        desc.setValue("isContainer", Boolean.FALSE);
        return desc;
    }


	// Generated BeanInfo just gives the bean its icons.
	// Small icon is in CommSpreadsheet.gif
	// Large icon is in CommSpreadsheetL.gif
	// It is expected that the contents of the icon files will be changed to suit your bean.

    public java.awt.Image getIcon(int iconKind)
	{
		java.awt.Image icon = null;
		switch (iconKind)
		{
			case ICON_COLOR_16x16:
	    		icon = loadImage("JCommTable.gif");
				break;			
			case ICON_COLOR_32x32:
				icon = loadImage("JCommTableL.gif");
			default:
				break;
		}
		return icon;
	}
	
	
		
	
}

/* CommSpreadsheetBeanInfo.java */
