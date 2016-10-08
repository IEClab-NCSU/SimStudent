/*
 * Created on Dec 18, 2003
 *
 */
package pact.CommWidgets;
import java.beans.BeanDescriptor;

/**
 * @author sanket
 *
 */
public class JCommFeedbackWidgetBeanInfo extends java.beans.SimpleBeanInfo{
	public java.beans.BeanDescriptor getBeanDescriptor() {
		BeanDescriptor desc = new BeanDescriptor(JCommFeedbackWidgetBeanInfo.class);
		desc.setValue("isContainer", Boolean.FALSE);
		return desc;
	}
}
