/*
 * Created on Mar 27, 2004
 *
 */
package pact.CommWidgets;

import java.util.EventListener;

import pact.CommWidgets.event.IncorrectActionEvent;
import pact.CommWidgets.event.IncorrectActionListener;
import pact.CommWidgets.event.StudentActionEvent;
import pact.CommWidgets.event.StudentActionListener;

/**
 * @author sanket
 *
 */
public interface QuestionWidgetInterface {

	public String getCommName();
	
	public void addStudentActionListener(StudentActionListener l);
	
	public void removeStudentActionListener(StudentActionListener l);

	public EventListener[] getStudentActionListener();

	public void fireStudentAction(StudentActionEvent e);

	public void setQuestionText(String questionText);

	public String getQuestionText();

	public int getScaffoldingOrder();

	public void setScaffoldingOrder(int dialogOrder);

	public void hideAllComponents(boolean b);

	public void addIncorrectActionListener(IncorrectActionListener l);

	public void removeIncorrectActionListener(IncorrectActionListener l);

	public void fireIncorrectAction(IncorrectActionEvent e);

}
