/*
 * HighlightThread.java
 *
 * Created on April 23, 2004, 5:16 PM
 */

package pact.CommWidgets;
import edu.cmu.pact.client.HintMessagesManagerForClient;
import edu.cmu.pact.Utilities.trace;

/**
 *
 * @author  zzhang
 */
/////////////////////////////////////////////////////////////////////////////////////
/**
 *
 */
/////////////////////////////////////////////////////////////////////////////////////
final public class HighlightThread extends Thread implements Runnable {

	static int delay = 250;
	JCommWidget w;
	String subElement;

	/////////////////////////////////////////////////////////////////////////////////////
	/**
	 *
	 */
	/////////////////////////////////////////////////////////////////////////////////////
	public HighlightThread(JCommWidget w, String subElement) {
		super();
		this.w = w;
		this.subElement = subElement;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	/**
	 *
	 */
	/////////////////////////////////////////////////////////////////////////////////////
	public void run() {

		for (int i = 0; i < 3; i++) {
			try {
				w.highlight(subElement, HintMessagesManagerForClient.defaultBorder);
				sleep(delay);
				w.removeHighlight(subElement);
				sleep(delay);
			} catch (InterruptedException e) {
				trace.out(5, this, "thread interrupted");
				return;
			}
		}

		try {
			w.highlight(subElement, HintMessagesManagerForClient.defaultBorder);
			sleep(delay * 16);
			w.removeHighlight(subElement);
		} catch (InterruptedException e) {
			trace.out(5, this, "thread interrupted");
			return;
		}
	}

}

