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
public class JCommQuestionBeanInfo extends java.beans.SimpleBeanInfo{
	public java.beans.BeanDescriptor getBeanDescriptor() {
		BeanDescriptor desc = new BeanDescriptor(JCommQuestion.class);
		desc.setValue("isContainer", Boolean.FALSE);
		return desc;
	}
}
