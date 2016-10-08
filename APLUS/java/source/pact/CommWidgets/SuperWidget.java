/**
 * 
 */
package pact.CommWidgets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.cmu.pact.Utilities.trace;

/**
 * A composite of several CommWidgets that comprise a logical step
 * in the problem-solving space.
 */
public class SuperWidget implements PropertyChangeListener {
	
	/** 
	 * Class to represent a set of {@link SuperWidget} instances.
	 */
	public static class Several implements HasAllQuestionsAnswered {
		
		/** The set of instances. */
		private SuperWidget[] swArr;
		
		public Several(SuperWidget[] swArr) {
			this.swArr = swArr;
		}

		/**
		 * Tell whether all of these instances have been answered.
		 * @return true if all instances in {@link #swArr} report hasBeenAnswered() true
		 */
		public boolean allQuestionsAnswered() {
			for (int i = 0; i < swArr.length; ++i) {
				if (!(swArr[i].hasBeenAnswered()))
					return false;
			}
			if (trace.getDebugCode("dw")) trace.out("dw", "Several.allQuestionsAnswered: returns true");
			return true;
		}

		public void addAllQuestionsAnsweredListener(PropertyChangeListener listener) {
			for (int i = 0; i < swArr.length; ++i) {
				List wscList = swArr[i].wscList;
				for (Iterator it = wscList.iterator(); it.hasNext(); ) {
					((WidgetSwingComponent) it.next()).addPropertyChangeListener(listener);
				}
			}
		}
	}

	/** The widgets that comprise this SuperWidget. */
	private List wscList = new ArrayList();
	
	/** Name this instance for debugging. */
	private String name;
	
	/** 
	 * An alternative instance that represents a different
	 * answer to the same logical step. If null, no such instance.
	 */
	private SuperWidget altSW;
	
	/**
	 * Create with widgets, name.
	 * @param dwArr
	 * @param name
	 */
	public SuperWidget(JCommWidget[] dwArr, String name) {
		for (int i = 0; i < dwArr.length; ++i)
			wscList.add(new WidgetSwingComponent(dwArr[i]));
		this.name = name;
	}
	
	/**
	 * Tell whether this step has been performed.
	 * @return true if {@link #altSW}.hasBeenAnswered() is true
	 *         or if, for all elements in {@link #wscList}, their
	 *         hasBeenAnswered() methods return true
	 */
	public boolean hasBeenAnswered() {
		boolean answered = hasBeenAnswered(this);
		if (trace.getDebugCode("dw")) trace.out("dw", "SW " + name + " hasBeenAnswered: answered = " + answered);
		return answered;
	}

	/**
	 * Tell whether this step has been performed.
	 * @param caller the calling instance, to avoid infinite recursion
	 * @return true if {@link #altSW}.hasBeenAnswered() is true
	 *         or if, for all elements in {@link #wscList}, their
	 *         hasBeenAnswered() methods return true
	 */
	private boolean hasBeenAnswered(SuperWidget caller) {
		if (altSW != null && altSW != caller && altSW.hasBeenAnswered(caller)) return true;
		if (trace.getDebugCode("dw")) trace.out("dw", "SW " + name + " caller: " + caller
				+ " hasBeenAnswered wscList.size() "+ wscList.size());
		for (Iterator it = wscList.iterator(); it.hasNext(); ) {
			WidgetSwingComponent wsc = (WidgetSwingComponent) it.next();
			if (!wsc.hasBeenAnswered()) return false;
		}
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * Sets {@link #altSW}
	 * @param altSW
	 */
	public void setAltSuperWidget(SuperWidget altSW) {
		this.altSW = altSW;
		altSW.addPropertyChangeListener(this);
	}

	private void addPropertyChangeListener(PropertyChangeListener widget) {
		((WidgetSwingComponent) wscList.get(0)).addPropertyChangeListener(widget);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		Boolean ov = (Boolean) evt.getOldValue();
		Boolean nv = (Boolean) evt.getNewValue();
		if (!ov.booleanValue() && nv.booleanValue())
			((WidgetSwingComponent) wscList.get(0)).setAnswered(false);
	}

}
