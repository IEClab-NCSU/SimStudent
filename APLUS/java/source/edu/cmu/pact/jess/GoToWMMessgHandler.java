/*
 * 	Created on Nov 11, 2003
 *	Donot start this in a new thread. The main thread ie MT should be blocking. As this
 *	message is always followed by lispCheckMessage and the lispCheckMessage handler 
 *	assumes that the working memory is in a proper state before it is called. Otherwise 
 *	the working memory state for the lispCheckMessage will not be correct.
 */
package edu.cmu.pact.jess;

import java.util.Enumeration;
import java.util.Vector;

import jess.Fact;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

/**
 * @author sanket
 *
 */
public class GoToWMMessgHandler extends MessageHandler {
    
    // zz add: to construct the Comm Message for this process
    MessageObject resultMessageObject = null;
    
    Vector authorIntent;
    Vector uniqueIDList;
    CheckLinksList checkLinksList = new CheckLinksList();
    Vector singleCheckedLink;

        
    /**
     * @param o
     */
    public GoToWMMessgHandler(MessageObject o, JessModelTracing jmt, CTAT_Controller jController) {
    	// Mon Oct 24 15:10:52 2005:: Noboru
    	// jController has been added so that updating model on failure
    	// works for Sim. St. validation
    	super(o, jmt, jController);
    	if (trace.getDebugCode("mt")) trace.out("mt", "GoToWMMessgHandler: request msg:\n "+o);
    	selectionList = (Vector) o.getProperty("SelectionList");
    	inputList = (Vector) o.getProperty("InputList");
    	actionList = (Vector) o.getProperty("ActionList");
    	authorIntent = (Vector) o.getProperty("AuthorIntentList");
    	
    	uniqueIDList = (Vector) o.getProperty("UniqueIDList");
    }

    public static void main(String[] args) {
    }

    /* (non-Javadoc)
     * @see jess.MessageHandler#processMessage()
     */
    public String processMessage() {
        
        String selItem;
        Enumeration listEnum = selectionList.elements();
        Enumeration enumerator;
        boolean notApplicable = false;
        jmt.setSkipTree(true);  // suspend conflict tree processing: restore before return

        int index_currentSelList = 0;
        Integer currentUniqueID = null;

        for (; listEnum.hasMoreElements(); ++index_currentSelList) {

            // list of selection for one arc
            Vector currentSelList = (Vector)listEnum.nextElement();
            if (!(listEnum.hasMoreElements()))
            	jmt.setSkipTree(false);       // sewall 2012/03/05: restore tree on last trace

            // corresponding action list
            Vector currentActionList = (Vector)actionList.elementAt(index_currentSelList);
            // corresponding input list
            Vector currentInputList = (Vector)inputList.elementAt(index_currentSelList);
            
            // corresponding authorIntent list
            String currentAuthorIntent = (String)authorIntent.elementAt(index_currentSelList);
            currentUniqueID = (Integer) uniqueIDList.elementAt(index_currentSelList);
            enumerator = currentSelList.elements();

            // for each selection element get the appropriate action and update the WME's
            //while (enumerator.hasMoreElements() && continueCheckFlag) {

            final String sel = (String) enumerator.nextElement();

            // the index of the selection element in the selection vector		   
            int selectionIndex = currentSelList.indexOf(sel); 
            // get the action for the selection element				
            final String act = (String)currentActionList.elementAt(selectionIndex);
            // 
            final String inp = (String)currentInputList.elementAt(selectionIndex);
            
            
          
          
           
            jmt.getRete().setGlobalSAI(sel, act, inp);
            //now that selection wme has been update, update any chunks that contain this selection.
            jmt.getRete().updateChunkValues(sel, inp);
            
            try {
                // jmt.getRete().showActivations("GoToWMMessgHandler "+sel+", "+act+", "+inp+", ");
                if ( JessModelTracing.isSAIToBeModelTraced(sel, act) ) {

                    if ((inp != null) && (!inp.equals(""))) {
                    
                        checkResult = jmt.runModelTrace(false, false, sel, act, inp, null);
                        
                        // If training Sim Student, update WM even on failure
                        if(!(JessModelTracing.SUCCESS.equals(checkResult))){
                            if (getController() != null &&
                                    getController().updateModelOnTraceFailure()) {
                            	//jmt.getRete().updateChunkValues(sel, inp);
                                jmt.getRete().setSAIDirectly(sel, act, inp);
                            }
                        }

                        // see if the author intent is same as the result of model tracing
                        // if yes then treat this link as passed traversed
                        // 
                        // Mon Oct 24 15:08:11 2005:: Noboru
                        // The 3rd condition on updating model on failure has been added
                        // for Sim. St. validation
                        checkLinksList.addLink(currentUniqueID.intValue(), checkResult, jmt.getRuleSeq());
                    }
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }

        // NOTAPPLICABLE links
        for (int i=index_currentSelList; i<uniqueIDList.size(); i++) {
        	currentUniqueID = (Integer) uniqueIDList.elementAt(i);
        	checkLinksList.addLink(currentUniqueID.intValue(),
        			EdgeData.NOTAPPLICABLE, null);
        }

        // construct the Comm Message for this "Go_To_WM_State" process
        resultMessageObject = MessageObject.create(MsgType.CHANGE_WM_STATE, "SetProperty");
        resultMessageObject.setProperty(CheckLinksList.PROPERTYNAME, checkLinksList, true);

        jmt.setSkipTree(false);    // restore conflict tree
        return checkResult;
    }

    /**
     * return the Comm Message for this "Go_To_WM_State" process
     * so that MT.handleCommMessage will return this Comm Message
     */
    public MessageObject getMessageObject() {
    	return resultMessageObject;
    }
        
    /* (non-Javadoc)
     * @see jess.MessageHandler#sendMessage()
     */
    public void sendMessage() {}
}
