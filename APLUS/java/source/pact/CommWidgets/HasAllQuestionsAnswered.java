/**
 * 
 */
package pact.CommWidgets;

import java.beans.PropertyChangeListener;

/**
 * Abstract type for student interfaces that report whether all
 * questions have been answered.
 */
public interface HasAllQuestionsAnswered {

	/**
	 * @return true if student has answered all logical questions
	 */
	public boolean allQuestionsAnswered();
	
	/**
	 * Accommodate listeners for the all questioned answered event.
	 * @param listener property change listener
	 */
	public void addAllQuestionsAnsweredListener(PropertyChangeListener listener);
}
