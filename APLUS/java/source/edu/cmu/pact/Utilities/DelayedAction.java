package edu.cmu.pact.Utilities;



import java.util.Date;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.TutoringService.TSLauncherServer;

/**
 * @author chc
 *
 * A queue of messages to be processed by the Behavior Recorder. Runs in its
 * own thread, so all operations on the list must be synchronized. Waits
 * whenever list is empty.
 */
public class DelayedAction  implements Runnable {

	private Thread  thread;	
	
	Runnable r;
	
	private  int      delayTime;  // Delay the action by xx milliseconds 
	
	private String cancelAction = null;   // cancel the delayed action

	
	//	Display a message, preceded by the name of the current thread
    static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }
    
	public DelayedAction(Runnable r) {
		this.r = r;
		thread = new Thread (this);		
	}
	
	public void start() {
	   thread.start();
   }

    public void stop() {
    	
    }
	/**
	 * Process the queue of messages until QUIT_MSG is received.
	 */
	public  void run() {
		try {
			if (delayTime > 0) {
//				threadMessage("Thread " + thread.currentThread().getName() + "Sleep  " + delayTime);
				Thread.sleep(delayTime);
			}
		} catch (InterruptedException ie){
			ie.printStackTrace();
		}

//		System.out.println("Thread " + thread.currentThread().getName() + "After Wake up => " + cancelAction);
    	if (trace.getDebugCode("ls")) trace.out("ls", "DelayedAction run() about to reach r.run()");
    	
			if (getCancelAction() == null) {
				try {
					threadMessage("run ");
			    	if (trace.getDebugCode("ls")) trace.out("ls", "r runnable is instanceOf TSLauncherServer: "+(r instanceof TSLauncherServer));
			    	if (trace.getDebugCode("ls")) trace.out("ls", "r runnable is instanceOf TSLauncherServer: "+(r instanceof CTAT_Launcher));
					r.run();
				} catch (Exception ie) {
					ie.printStackTrace();
				}
			}
			else threadMessage("Thread " + thread.currentThread().getName() + "wakw up and has been cancelled");

	}


	/**
	 * Remove and return the message at the head of the queue. Calls wait()
	 * until queue is nonempty.
	 * 
	 * @return message from head of queue
	 */
	 public String cancel() {
		cancelAction = "Thread " + thread.currentThread().getName() + "cancelled at " + new Date();
		System.out.println(cancelAction);
		return cancelAction;
	}

	public int getDelayTime() {
		return delayTime;
	}

	public  void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}

	synchronized public String getCancelAction() {
		return cancelAction;
	}

	public void setCancelAction(String cancelAction) {
		this.cancelAction = cancelAction;
	}


}