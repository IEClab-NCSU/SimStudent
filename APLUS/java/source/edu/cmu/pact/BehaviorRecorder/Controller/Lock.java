package edu.cmu.pact.BehaviorRecorder.Controller;

import pact.CommWidgets.StudentInterfaceWrapper;
import edu.cmu.pact.BehaviorRecorder.View.BRPanel;
import edu.cmu.pact.Utilities.trace;

public class Lock extends Object {
	public boolean m_bLocked = false;
    public BRPanel brPanel;
    private BR_Controller controller;
	public synchronized void lock() {
		// if some other thread locked this object then we need to wait
		// until they release the lock
		if (m_bLocked) {
			do {
				try {
					if (trace.getDebugCode("br")) trace.out("br", " Lock waits for a notify ....");
					// this releases the synchronized that we are in
					// then waits for a notify to be called in this object
					// then does a synchronized again before continuing
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} while (m_bLocked); // we can't leave until we got the lock,
			// which
			// we may not have got if an exception occured
		}

		m_bLocked = true;
		if (trace.getDebugCode("br")) trace.out("br", " Locked");
	}

	public synchronized boolean lock(BR_Controller controller, long milliSeconds) {
        this.controller = controller;
		if (m_bLocked) {
			try {
				wait(milliSeconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (m_bLocked) {
				return false;
			}
		}
//        Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
//        brPanel.setCursor(hourglassCursor);
    	StudentInterfaceWrapper siw = controller.getStudentInterface();
//    	if (siw != null) {
//    		JFrame f = siw.getActiveWindow();
  //    		f.getRootPane().setCursor(new Cursor(Cursor.WAIT_CURSOR));
 //  		f.setCursor(new Cursor(Cursor.WAIT_CURSOR));
//   		f.setCursor(Frame.WAIT_CURSOR);
 //   		f.getContentPane().setCursor(new Cursor(Cursor.WAIT_CURSOR));
//    		f.repaint();
//    	}
		m_bLocked = true;
		return true;
	}

	public synchronized boolean lock(long milliSeconds, int nanoSeconds) {
		if (m_bLocked) {
			try {
				wait(milliSeconds, nanoSeconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (m_bLocked) {
				return false;
			}
		}

		m_bLocked = true;
		return true;
	}

	public synchronized void releaseLock() {
		if (m_bLocked) {
			if (trace.getDebugCode("br")) trace.out("br", " Release Lock and send a notify");
			m_bLocked = false;
			notify();
		}
//        Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
//        brPanel.setCursor(normalCursor);
		
//    	StudentInterfaceWrapper siw = controller.getStudentInterface();
//    	if (siw != null) {
//    		JFrame f = siw.getActiveWindow();
//    	//	f.getRootPane().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//    		f.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//    		f.repaint();
//    	}
		if (trace.getDebugCode("br")) trace.out("br", "Lock Released [ " + m_bLocked + " ]");
	}

	public synchronized boolean isLocked() {
		return m_bLocked;
	}



}

