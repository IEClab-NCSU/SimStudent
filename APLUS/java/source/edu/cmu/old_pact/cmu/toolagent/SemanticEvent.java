//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/SemanticEvent.java
package edu.cmu.old_pact.cmu.toolagent;


import java.io.DataInputStream;
import java.io.IOException;
import java.net.UnknownHostException;

//semanticEvent should extend Socket, but that's final
//so this just contains a socket
public class SemanticEvent {
	String theClass; //event class and ID identify the kind of event
	String messages;
	String sername;
	int numParameters;
	
	EventTarget target;
	
	SemanticEvent (String eventClass, String eventId) throws IOException{
		theClass = new String(eventClass);
		String theId = new String(eventId);
		numParameters = 0;
		messages = new String("");
	}
	
	SemanticEvent () throws IOException{
		numParameters = 0;
		messages = new String("");
	}
		
 	public void addMessage(String keyword, String value) throws IOException{
 		if (numParameters > 0)
 			messages = messages+"&";
 		messages = messages+keyword+"="+value;
 		numParameters++;
 	}
 	
 	public void setMessage(String message){
 		messages = message;
 		numParameters = 1;
 	}
 		
 	
 	public void send(EventTarget target) throws UnknownHostException, IOException{
 		target.send(messages);
 	}
 	
 	public DataInputStream receive(EventTarget target) throws UnknownHostException, IOException{
 		 return target.receive(messages);
 	}
 	
 	public String toString() {
 		return messages;
 	}
 }