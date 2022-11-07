package edu.cmu.old_pact.dormin;
import edu.cmu.pact.Utilities.trace;

import java.io.PrintStream;

public class FilterTarget extends StreamTarget{
	public Translator myTranslator;
	
	public FilterTarget(){};
	public FilterTarget(String newName,PrintStream newStream,Translator newFilter){
		super(newName,newStream);
		myTranslator = newFilter;
	}
	
	public void transmitEvent(MessageObject inEvent){
		String toSend = myTranslator.toDestination(inEvent);
	trace.out("FilterTarget to send "+toSend);
		synchronized (outputStream) {
			outputStream.println(toSend);
			outputStream.flush();
		}
	}

}