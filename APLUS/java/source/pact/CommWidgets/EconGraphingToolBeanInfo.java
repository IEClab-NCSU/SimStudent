package pact.CommWidgets;

/* 
	JCommComposerBeanInfo.java

	Author:			mpschnei
	Description:	BeanInfo for class CommSpreadsheet
*/

import java.beans.BeanDescriptor;

public class EconGraphingToolBeanInfo extends java.beans.SimpleBeanInfo
{

    
    public java.beans.BeanDescriptor getBeanDescriptor() {
        BeanDescriptor desc = new BeanDescriptor(EconGraphingTool.class);
        desc.setValue("isContainer", Boolean.FALSE);
        return desc;
    }
/*
    public java.awt.Image getIcon(int iconKind)
	{
		java.awt.Image icon = null;
		switch (iconKind)
		{
			case ICON_COLOR_16x16:
	    		icon = loadImage("EconGraphingTool.gif");
				break;			
			case ICON_COLOR_32x32:
				icon = loadImage("EconGraphingTool.gif");
			default:
				break;
		}
		return icon;
	}	
*/	
}

