package edu.cmu.old_pact.dormin;
import java.io.PrintStream;

public class StreamTarget extends Target{
	PrintStream outputStream;
	
	public StreamTarget(){ super();};
	public StreamTarget(String newName,PrintStream newStream){
		super(newName);
		outputStream = newStream;
	}

	public void transmitEvent(MessageObject inEvent){
		//trace.out (10, this, "transmitting event");
		synchronized (outputStream) {
			outputStream.flush();
		}
	}
	
	public void close() {
		outputStream.close();
	}
}