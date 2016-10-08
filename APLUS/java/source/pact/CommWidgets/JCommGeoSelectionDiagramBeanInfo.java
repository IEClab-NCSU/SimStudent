package pact.CommWidgets;

import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;

public class JCommGeoSelectionDiagramBeanInfo extends SimpleBeanInfo {
	
	public java.beans.BeanDescriptor getBeanDescriptor() {
        BeanDescriptor desc = new BeanDescriptor(JCommGeoSelectionDiagram.class);
        desc.setValue("isContainer", Boolean.FALSE);
        return desc;
    }
	
	//Generated BeanInfo just gives the bean its icons.
	// Small icon is in JCommTextField.gif
	// Large icon is in CommTextFieldL.gif
	// It is expected that the contents of the icon files will be changed to suit your bean.
	public java.awt.Image getIcon(int iconKind)
	{
		java.awt.Image icon = null;
		switch (iconKind)
		{
			case ICON_COLOR_16x16:
	    		icon = loadImage("JCommGeoSelectionDiagram.png");
				break;			
			case ICON_COLOR_32x32:
				icon = loadImage("JCommGeoSelectionDiagramL.png");
			default:
				break;
		}
		return icon;
	}
	
}
