
package pact.CommWidgets;
import java.beans.BeanDescriptor;

public class TimeLineWidgetBeanInfo extends java.beans.SimpleBeanInfo
{
	public java.beans.BeanDescriptor getBeanDescriptor()
	{
		BeanDescriptor desc = new BeanDescriptor(TimeLineWidget.class);
		desc.setValue("isContainer", Boolean.FALSE);
		return desc;
	}

	public java.awt.Image getIcon(int iconKind)
	{
		return null;
	}		
}	
