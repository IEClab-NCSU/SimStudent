package servlet;
import java.util.*;

/**
 *  Container class for the current message to be sent to the client by SimStudentBaseServlet.
 *  Messages come as pairs, with a session id and the actual message. 
 *  
 *  @author Patrick Nguyen
*/
public class MessageManager
{
    private Map<String,Queue<String>> queues;
    
    public MessageManager(){
    	queues=new HashMap<String,Queue<String>>();
    }
    
    /**
     * Adds a queue to the manager associated with the sessionId
     * @param sessionId
     */
    public void addSession(String sessionId){
    	queues.put(sessionId, new LinkedList<String>());
    }
    
    /** 
     * Sets a new message. This message is associated with the given sessionId.
     * 
     * @param sessionId - the ID of the session to receive the message
     * @param message - the message we are holding
     */
    public void addMessage(String sessionId,String message){
//    	System.out.println("New message added for session "+sessionId+": "+message);
    	Queue<String> q;
    	if(queues.get(sessionId) == null){
    		q = new LinkedList<String>();
    		queues.put(sessionId, q);
    	}else{
    		q = queues.get(sessionId);
    	}
    	q.add(message);
    }
    
    /**
     *  Gets the next message for the given sessionId.
     *  @return message - the next message this object is holding
     */
    public String getMessage(String sessionId){
    	Queue<String> q = queues.get(sessionId);
    	if(q == null || q.isEmpty())return null;
    	return q.remove();
    }

}