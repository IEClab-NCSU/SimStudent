package edu.cmu.old_pact.cmu.spreadsheet;
//CaretThread is a thread that does the blinking caret in an AltTextField

final class CaretThread implements Runnable {
	static Thread theThread;
	static AltTextField atf;
	
	static public void initCaretThread() {
		if (theThread == null) {
			theThread = new Thread(new CaretThread());
			theThread.setDaemon(true);
			theThread.start();
		}
	}
	
	static public void takeCaretThread(AltTextField in) {
		synchronized (theThread) {
			atf = in;
			theThread.resume();
			theThread.interrupt();
		}
	}
	
	static public void releaseCaretThread() {
		synchronized (theThread) {
			atf = null;
			theThread.suspend();
		}
	}
	
	public void run() {
		while (true) {
			try {
				Thread.sleep(500);
			}
			catch (InterruptedException e) {
			}
			synchronized (theThread) {
				if (atf != null) {
					atf.showCaret = !atf.showCaret;
					atf.repaint();
				}
			}
		}
	}
}
		
