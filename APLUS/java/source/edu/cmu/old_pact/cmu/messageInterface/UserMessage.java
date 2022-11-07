package edu.cmu.old_pact.cmu.messageInterface;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.pact.Utilities.trace;

/**
 * A class that contains all the information about a single user message.
 * It includes text of a message and zero or more Pointers.
 */
 
public class UserMessage {
	protected Pointer[] pointers;
	protected String messageText;
	private String title = null;
	private String imageBase = null;
	
	/**
 	* Constructs UserMessage.
 	* @param pointers - an array of Pointers
 	* @param messageText - a text of a specified message
 	* @param title - a title for a message
 	* @param imageBase - an imageBase for a message
 	*/
	public UserMessage(Pointer[] pointers, String messageText, String title, String imageBase){
		this.pointers = pointers;
		this.messageText = messageText;
		this.title = title;
		this.imageBase = imageBase;
	}
	
	/**
 	* Constructs UserMessage.
 	* @param pointers - an array of Pointers
 	* @param messageText - a text of a specified message
 	*/
	public UserMessage(Pointer[] pointers, String messageText){
		this(pointers, messageText, null, null);
	}
	
	/**
 	* Constructs UserMessage without Pointers.
 	* @param messageText - a text of a specified message
 	* @param imageBase - an imageBase for a message
 	*/
	public UserMessage(String messageText, String imageBase){
		this(null, messageText, null, imageBase);
	}
	
	/**
 	* Constructs UserMessage without Pointers.
 	* @param messageText - a text of a specified message
 	*/
	public UserMessage(String messageText){
		this(null, messageText, null, null);
	}
	
	/**
	* Returns a text of the message.
	*/
	public  String getText(){
		return messageText;
	}
	
	/**
	* Sends point() to all Pointers of this message.
	*/
    public synchronized void point() {
//    	trace.out("in UserMessage::point, pointers are "+pointers);
		try {
	    	if(pointers == null)
	    		return;
	    	int numPointers = pointers.length;
	    	for(int i=0; i<numPointers; i++) {
	    		pointers[i].point();
	    	}
	    }
	    catch (DorminException ex) {
	    	System.out.println("invalid pointer in UserMessage::point");
	    }
    }
    
    /**
	* Releases all Pointers of this message.
	*/
    public synchronized void unPoint() {
    	try {
	    	if(pointers == null)
	    		return;
	    	int numPointers = pointers.length;
	    	for(int i=0; i<numPointers; i++) 
	    		pointers[i].unPoint();
	    }
	    catch (DorminException ex) {
	    	trace.out("invalid pointer in UserMessage::unpoint");
	    }
    }
    
    public String getTitle(){
    	return title;
    }
    
    public String getImageBase(){
    	return imageBase;
    }
    
}
	
	