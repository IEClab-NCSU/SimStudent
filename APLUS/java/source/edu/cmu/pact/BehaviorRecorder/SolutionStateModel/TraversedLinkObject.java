/*
 * TraversedLinkObject.java
 *
 * Created on April 28, 2004, 6:11 PM
 */

package edu.cmu.pact.BehaviorRecorder.SolutionStateModel;

/**
 *
 * @author  zzhang
 */



import edu.cmu.pact.ctat.MessageObject;

        //////////////////////////////////////////////////////////////////////
	/**
         *  to hold single traversed link' the necessary information
         *
	 */
	//////////////////////////////////////////////////////////////////////


public class TraversedLinkObject {
    private String linkName;
    private MessageObject CommMsgObj;
    private String authorIntent;
    private int uniqueID;
    
    
    //////////////////////////////////////////////////////////////////////
    /**
     *  Creates a new instance of TraversedLinkObject
     *
     */
    //////////////////////////////////////////////////////////////////////
  
   
    public TraversedLinkObject(String linkNameP, MessageObject CommMsgObjP, String authorIntentP, int uniqueIDP) {
        
        this.linkName = linkNameP;     
        
        this.CommMsgObj = CommMsgObjP;
        
        this.authorIntent = 
                authorIntentP;
        
        this.uniqueID = 
                uniqueIDP;
    }

	public String getLinkName() {
        return this.linkName;
    }
                
    public MessageObject getCommMsgObj() {
        return this.CommMsgObj;
    }
    
    public String getAuthorIntent() {
        return this.authorIntent;
    }
    
    public int  getUniqueID() {
        return this.uniqueID;
    }
}
