/**
 * Created on Dec 17, 2003
 *
 */
package pact.CommWidgets;
import java.beans.BeanDescriptor;

public class JCommPanelBeanInfo extends java.beans.SimpleBeanInfo{

	public java.beans.BeanDescriptor getBeanDescriptor() {
		BeanDescriptor desc = new BeanDescriptor(JCommPanelBeanInfo.class);
		desc.setValue("isContainer", Boolean.TRUE);
		return desc;
	}

    public java.awt.Image getIcon(int iconKind)
	{
		java.awt.Image icon = null;
		switch (iconKind)
		{
			case ICON_COLOR_16x16:
	    		icon = loadImage("JCommPanel.gif");
				break;			
			case ICON_COLOR_32x32:
				icon = loadImage("JCommPanel.gif");
			default:
				break;
		}
		return icon;
	}
	
}
