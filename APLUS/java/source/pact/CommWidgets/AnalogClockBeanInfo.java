
package pact.CommWidgets;
import java.beans.BeanDescriptor;

public class AnalogClockBeanInfo extends java.beans.SimpleBeanInfo
{
	public java.beans.BeanDescriptor getBeanDescriptor()
	{
		BeanDescriptor desc = new BeanDescriptor(AnalogClock.class);
		desc.setValue("isContainer", Boolean.FALSE);
		return desc;
	}

	public java.awt.Image getIcon(int iconKind)
	{
		return null;
	}
}
