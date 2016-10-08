/*
 * Created on Nov 10, 2003
 *
 */
package edu.cmu.pact.jess;

/**
 * @author sanket
 *
 */
public class ResumeBreak {
	boolean resume;
	private boolean available = false;
	
	/**
	 * @return
	 */
	public synchronized boolean isResume() {
		while(!available){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		available = false;
		notifyAll();
		return resume;
	}

	/**
	 * @param resume
	 */
	public synchronized void setResume(boolean resume) {
//		while(available){
//			try {
//				wait();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		available = true;
		notifyAll();
		this.resume = resume;
	}
}
