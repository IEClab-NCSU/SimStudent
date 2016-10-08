
/* 
	CommSpreadsheetBeanInfo.java

	Author:			mpschnei
	Description:	BeanInfo for class CommSpreadsheet
*/

package pact.CommWidgets;
import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;

public class JCommComboBoxBeanInfo extends java.beans.SimpleBeanInfo
{

	// Generated BeanInfo just gives the bean its icons.
	// Small icon is in CommSpreadsheet.gif
	// Large icon is in CommSpreadsheetL.gif
	// It is expected that the contents of the icon files will be changed to suit your bean.

    public PropertyDescriptor[] getPropertyDescriptor() {

		try {
			PropertyDescriptor pd1 = new PropertyDescriptor ("XYZ", JCommComboBox.class);
			
			PropertyDescriptor[] pda = { pd1 };
			
			return pda;
			
		} catch (Exception e) {
			return null;
		}

    }
    
    
    public java.beans.BeanDescriptor getBeanDescriptor() {
        BeanDescriptor desc = new BeanDescriptor(JCommComboBox.class);
        desc.setValue("isContainer", Boolean.FALSE);
        return desc;
    }

    public java.awt.Image getIcon(int iconKind)
	{
		java.awt.Image icon = null;
		switch (iconKind)
		{
			case ICON_COLOR_16x16:
	    		icon = loadImage("JCommComboBox.gif");
				break;			
			case ICON_COLOR_32x32:
				icon = loadImage("JCommComboBox.gif");
			default:
				break;
		}
		return icon;
	}
	
	
		
	
}

/* CommSpreadsheetBeanInfo.java */
