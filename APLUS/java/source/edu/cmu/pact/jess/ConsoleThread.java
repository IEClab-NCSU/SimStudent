/*
 * Created on Jun 24, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.cmu.pact.jess;

import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.trace;

/**
 * @author sanket
 *
 */
public class ConsoleThread extends Thread{
	
	/** The rule engine to monitor. */
	private volatile MTRete m_rete;

	JessConsole c = null;
			
	public ConsoleThread(MTRete r, CTAT_Controller controller){
		super();
		m_rete = r;
		c = new JessConsole("Jess Console", m_rete, true, controller);
	}
	
	public void run() {
		while (m_rete != null) {
			try {
				String[] str = new String[0];
				c.getJessPanel().validate();
				c.execute(str);
			} catch (Exception e) {
				trace.err("should be interrupted exception: "+e);
				e.printStackTrace();
			}
		}
	}
	
	public void destroy(){
		setRete(null);
//		c.dispose();
	}
	
	/**
	 * Change the engine monitored by this console.
	 * @param rete
	 */
	void setRete(MTRete rete) {
		m_rete = rete;
		c.setRete(rete);
		if (trace.getDebugCode("mt")) trace.out("mt", "setRete() about to enter sync");
		synchronized(this) {
			if (isAlive()) {
				long waitTimeMs = 1000;
				if (trace.getDebugCode("mt")) trace.out("mt", "setRete() about to interrupt");
				interrupt();
//				trace.out("mt", "setRete() waiting "+waitTimeMs+" ms for run() exit");
//				long now = (new Date()).getTime();
//				try {
//					join(waitTimeMs);
//					trace.out("mt", "setRete() wait ended after "+
//							((new Date()).getTime() - now)+" ms for run() exit");
//				} catch (InterruptedException ie) {
//					trace.err("setRete() caught "+ie+" waiting "+waitTimeMs+
//							" ms for run() exit");
//				}
			}
//			if (m_rete != null) {
//				trace.out("mt", "setRete() about to restart");
//				start();
//			}
		}
	}
	
	/**
	 * Accessor for JessConsole object.
	 * @return value of {@link #c}
	 */
	public JessConsole getConsole() {
		return c;
	}
}
