/*
 * Created on Mar 28, 2004
 *
 */
package pact.CommWidgets;

import java.util.EventListener;

import javax.swing.JPanel;

import pact.CommWidgets.event.IncorrectActionEvent;
import pact.CommWidgets.event.IncorrectActionListener;
import pact.CommWidgets.event.StudentActionEvent;
import pact.CommWidgets.event.StudentActionListener;

/**
 * Question panel allows the assistment creators to put one JCommWidget in it
 * along with as many other interface elements (one's which are jsut for display
 * and the student does not interact with it)
 * All the elements in the QuestionPanel will be displayed at once, how ever if the 
 * questionPanel is put in the JCommPanel then the questionPanel will be displayed 
 * only when the previous question is answered correctly and when this is answered 
 * correctly then the next JCommQuestion or the QuestionPanel will be displayed.
 * 
 * @author sanket
 *
 */
public class QuestionPanel extends JPanel implements QuestionWidgetInterface {


	/* (non-Javadoc)
	 * @see pact.CommWidgets.QuestionWidgetInterface#addStudentActionListener(pact.CommWidgets.event.StudentActionListener)
	 */
	public void addStudentActionListener(StudentActionListener l) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see pact.CommWidgets.QuestionWidgetInterface#removeStudentActionListener(pact.CommWidgets.event.StudentActionListener)
	 */
	public void removeStudentActionListener(StudentActionListener l) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see pact.CommWidgets.QuestionWidgetInterface#getStudentActionListener()
	 */
	public EventListener[] getStudentActionListener() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pact.CommWidgets.QuestionWidgetInterface#fireStudentAction(pact.CommWidgets.event.StudentActionEvent)
	 */
	public void fireStudentAction(StudentActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see pact.CommWidgets.QuestionWidgetInterface#setQuestionText(java.lang.String)
	 */
	public void setQuestionText(String questionText) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see pact.CommWidgets.QuestionWidgetInterface#getQuestionText()
	 */
	public String getQuestionText() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see pact.CommWidgets.QuestionWidgetInterface#getDialogOrder()
	 */
	public int getScaffoldingOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see pact.CommWidgets.QuestionWidgetInterface#setDialogOrder(int)
	 */
	public void setScaffoldingOrder(int dialogOrder) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see pact.CommWidgets.QuestionWidgetInterface#hideAllComponents(boolean)
	 */
	public void hideAllComponents(boolean b) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see pact.CommWidgets.QuestionWidgetInterface#addIncorrectActionListener(pact.CommWidgets.event.IncorrectActionListener)
	 */
	public void addIncorrectActionListener(IncorrectActionListener l) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see pact.CommWidgets.QuestionWidgetInterface#removeIncorrectActionListener(pact.CommWidgets.event.IncorrectActionListener)
	 */
	public void removeIncorrectActionListener(IncorrectActionListener l) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see pact.CommWidgets.QuestionWidgetInterface#fireIncorrectAction(pact.CommWidgets.event.IncorrectActionEvent)
	 */
	public void fireIncorrectAction(IncorrectActionEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see pact.CommWidgets.QuestionWidgetInterface#getCommName()
	 */
	public String getCommName() {
		// TODO Auto-generated method stub
		return null;
	}

}
