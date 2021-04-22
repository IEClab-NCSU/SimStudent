/**
 * 
 */
package edu.cmu.pact.miss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

/**
 * @author mazda
 *
 */
public class InquiryClAlgebraTutor {

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Field
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    // Wait as long as 60sec to any kind of feedback/message sent back from CLT
    protected static final int MAX_NUM_WAIT = 10;
    protected final int WAIT_DURATION = 2000;

    /* 
     * Read the TutoringServiceManager class to know more about the protocol 
     * 
     */
    
    // A communication channel to TutoringServiceManger
    Socket socket = null;
    BufferedReader in = null;
    PrintWriter out = null;
    
    protected static final String FLAG = "FLAG";
    protected static final String APPROVE = "APPROVE";
    protected static final String HINTMESSAGE = "HINTMESSAGE";
    private static final Object GOODBYE_GREETING = "%BYE%";
    
    private static final String YOU_HAVE_SOLVED = "You have solved the equation";
    private static final String YOU_ARE_DONE = "You are done with all steps";
    
    private String currentProblem = null;

    public void setCurrentProblem(String problemName) {
        sendInquiryCreateProblem(problemName);
        currentProblem = problemName;
    }

    public String getCurrentProblem() {
        return currentProblem ;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    public InquiryClAlgebraTutor(){
    	
    }
    
    public InquiryClAlgebraTutor(String serverName, String serverPort) {
        
        socket = null;
        while (socket == null) {
            try {
                socket = new Socket(serverName, Integer.parseInt(serverPort));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (socket == null) {
                try {
                    wait(WAIT_DURATION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            activateIncomingStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Receiving a message from the TutoringServiceManager
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    Thread incomingStreamMonitor = null;
    private Boolean dontReadSocket = new Boolean(false);
    private void resetDontReadSocket() {
        synchronized (dontReadSocket) {
            dontReadSocket = new Boolean(false);
        }
    }
    
    private void setDontReadSocket() {
        synchronized (dontReadSocket) {
            dontReadSocket = new Boolean(true);
        }
    }
    
    private boolean dontReadSocket() {
        synchronized (dontReadSocket) {
            return dontReadSocket.booleanValue();
        }
    }

    private void activateIncomingStream() {
        resetDontReadSocket();
        incomingStreamMonitor = new Thread() {
            public void run() {
                while (!dontReadSocket()) {
                    String msg = null;
                    try {
                        msg = in.readLine();
                    } catch (IOException e) {
                        if (!socket.isClosed()) {
                            e.printStackTrace();
                        }
                    }
                    if (msg != null) {
                        handleMessage(msg);
                    }
                }
            }
        };
        incomingStreamMonitor.start();
    }

    // Set by handleMessage(). isCorrectStep() is waiting for this to be set
    String msgFromTutoringService = null;
    protected void resetMsgFromTutoringService() { msgFromTutoringService = null; }

    // "msg" has a header and a message body wiht a ';' in between
    void handleMessage(String msg) {

        String msgHeader = getMsgHeader(msg);
        String msgBody = getMsgBody(msg);
        
        while (msgFromTutoringService != null) {
            try {
                //wait(WAIT_DURATION);
            	Thread.sleep(WAIT_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (HINTMESSAGE.equals(msgHeader)) {
            msgFromTutoringService = msgBody;
            //notifyAll();
        }
        else if (FLAG.equals(msgHeader)) {
            msgFromTutoringService = FLAG;
            //notifyAll();
        }
        else if (APPROVE.equals(msgHeader)) {
            msgFromTutoringService = APPROVE;
            //notifyAll();
        }
    }
    
    protected String getMsgBody(String msg) {
        int delimIndex = msg.indexOf(';');
        return delimIndex > 0 ? msg.substring(msg.indexOf(';') +1) : null;
    }

    protected String getMsgHeader(String msg) {
        int delimIndex = msg.indexOf(';');
        return delimIndex >0 ? msg.substring(0, delimIndex) : msg;
    }


    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Seeking for a hint
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    Vector /* String */ hintMessage = new Vector();
    private void addHint(String hintMessage) {
        this.hintMessage.add(hintMessage);
    }
    private String getHint(int numPrevSteps) {
        return (String)hintMessage.get(numPrevSteps);
    }
    
    // GetHint
    // numPrevSteps represents a number of steps made to reach a "currentNode" since a 
    // problem is set.  Note that the problem is not necessarily the original equation. 
    // It is the closest intermediate expression to the current state.
    public String askHint(BR_Controller brController, ProblemNode currentNode, int numPrevSteps) {
        return getHint(numPrevSteps);
    }

    /**
     * @param numPrevSteps
     */
    protected String waitForHintMessage() {
        
        String hintMsg = null;
        
        int numWait = 0;
        long startTime = (new Date()).getTime();
        try {
            while (msgFromTutoringService == null) {
                //wait(WAIT_DURATION);
                Thread.sleep(WAIT_DURATION);
                if (++numWait > MAX_NUM_WAIT) break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = (new Date()).getTime();
        long duration = endTime - startTime;

        // Something wrong should have been happened...
        if (msgFromTutoringService == null) {
            // shutdown();
        	// System.exit(-1);
        }
        
        hintMsg = msgFromTutoringService;
        resetMsgFromTutoringService();

        return hintMsg;
    }
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Sending an inquiry to the TuotringServiceManager
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    
    /*
    : actualSelection: commTable1_C3R1 <edu.cmu.pact.miss.SimSt:4616>main
    :    actualAction: UpdateTable <edu.cmu.pact.miss.SimSt:4617>main
    :     actualInput: subtract -2.85 <edu.cmu.pact.miss.SimSt:4618>main
    
    : actualSelection: commTable1_C1R2 <edu.cmu.pact.miss.SimSt:4616>main
    :    actualAction: UpdateTable <edu.cmu.pact.miss.SimSt:4617>main
    :     actualInput: 2.19y-6.5 <edu.cmu.pact.miss.SimSt:4618>main
    */ 
    
    public boolean isCorrectStep(String selection, String action, String input) throws TutorServerTimeoutException
    {
        
        String flaggedFeedback = null;
        while (msgFromTutoringService == null) {
            sendInquiryStepPerformed(selection, action, input);
            
            int numWait = 0;
            try {
                while (!APPROVE.equals(msgFromTutoringService) && !FLAG.equals(msgFromTutoringService)) {
                    //wait(WAIT_DURATION);
                    Thread.sleep(WAIT_DURATION);
                    if (++numWait > MAX_NUM_WAIT) {
                        resetMsgFromTutoringService();
                        // Too long. Try it again!
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Something wrong should have been happened...
        if (msgFromTutoringService == null) {
            //shutdown();
            //System.exit(-1);
            //throw new TutorServerTimeoutException("Query to TutorServer Timed Out");
        }
        
        flaggedFeedback = msgFromTutoringService;
        resetMsgFromTutoringService();
        
        if (flaggedFeedback == null) {
        	if(trace.getDebugCode("miss"))trace.out("miss", "InquiryClAlgebraTutor.isCorrectStep() got null as feedback!!!");
        }
        
        return (APPROVE.equals(flaggedFeedback)) ? true : false;
    }

    /*
     * Exception to throw when Tutor Server is not responding
     */
    class TutorServerTimeoutException extends Exception {

		public TutorServerTimeoutException(String string) {
			super(string);
		}

		private static final long serialVersionUID = 1L;
		
	}

    void sendInquiryStepPerformed(String selection, String action, String input) {
        
        if (EqFeaturePredicate.isValidSimplificationSkill(input)) {
            if (EqFeaturePredicate.canBeSimplified(getCurrentProblem().split(" = ")[0]) != null)
                input += " " + getCurrentProblem().split(" = ")[0];
            else 
                input += " " + getCurrentProblem().split(" = ")[1];
        }
        
        if (isSkillOperand(input)) {
            sendInquiryRequestCreate(selection, input);
        }
        else {
            sendInquiryNoteValueSet(selection, input);
        }
    }

    private boolean isSkillOperand(String input) {
        
        boolean isSkillOperand = false;

        if ( (input.indexOf(' ') > 0) &&
                (EqFeaturePredicate.isValidSimpleSkill(input.split(" ")[0])) ) {
            isSkillOperand = true;
        }

        return isSkillOperand;
    }

    // RequestCreate&multiply&-5a
    private void sendInquiryRequestCreate(String selection, String input) {
        String inquiryMsg = "RequestCreate" + "&";
        inquiryMsg += input.split(" ")[0] + "&";
        inquiryMsg += input.split(" ")[1];
        
        sendInquiryMsg(inquiryMsg);
    }

    // NoteValueSet&left&100p
    private void sendInquiryNoteValueSet(String selection, String input) {
        // selection -> "commTable1_C3R1"
        int indexC = selection.indexOf('C');
        int indexR = selection.indexOf('R');
        String indexStr = selection.substring(indexC +1, indexR);
        int index = Integer.parseInt(indexStr);
        String targetPlace = index == 1 ? "left" : "right";
        
        String inquiryMsg = "NoteValueSet" + "&";
        inquiryMsg += targetPlace + "&";
        inquiryMsg += input;

        sendInquiryMsg(inquiryMsg);
    }

    // CreateProblem&Solve for y&-4y = -3y+5+(-2y)
    private void sendInquiryCreateProblem(String problemName) {
        String varName = getVarName(problemName);
        String inquiryMsg = "CreateProblem" + "&";
        inquiryMsg += "Solve for " + varName + "&";
        inquiryMsg += problemName;
        sendInquiryMsg(inquiryMsg);
    }

    private String getVarName(String problemName) {
        String varName = "";
        for (int i = 0; i < problemName.length(); i++) {
            char c = problemName.charAt(i);
            if ( ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') ) {
                varName += c;
                break;
            }
        }
        return varName.toLowerCase();
    }

    protected void sendInquiryMsg(String inquiryMsg) {
    	out.println(inquiryMsg);
    }

    public void shutdown() {
        out.println(GOODBYE_GREETING);
        setDontReadSocket();
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Set the current state one step before the node
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    // Set to be true only when the method clAlgebraTutorGotoOneStateBefore() 
    // is called to gather hint messages
    private boolean askingHintOn = false;
    public void setAskingHintOn() { this.askingHintOn = true; }
    public void resetAskingHintOn() { this.askingHintOn = false; }
    private boolean isAskingHintOn() {
        return askingHintOn;
    }

    // Returns a number of steps from the "problem" to the specified node
    // The "problem" is a intermediate state of the original equation that
    // is the most recently entered full LHS and RHS
    //
    // Returns -1 for timeout
    // Returns -2 for invalid steps
    // 
    public int clAlgebraTutorGotoOneStateBefore(BR_Controller brController, ProblemNode node) {
        
        ProblemNode startNode = brController.getProblemModel().getStartNode();
        
        ProblemEdge[] edgeQueue = new ProblemEdge[3];
        String problem[] = new String[1];
        problem[0] = startNode.getName();

        int edgeCount = 0;
        // Do nothing for the start state...
        if (node != startNode) {
        	// Traverse the edges on the path but the last one
        	Vector /* ProblemEdge */ pathEdges = findPathDepthFirst(startNode, node);
        	edgeCount = searchLastEquation(problem, edgeQueue, pathEdges);
        }

        boolean hintDelivered = false;
        while (!hintDelivered) {
            setCurrentProblem(problem[0]);
            
            // right after making a new problem, three hint messages delivered from PTS Plus
            String hintMsg[] = new String[3];
            for (int i = 0; i < 3; i++) {
                hintMsg[i] = waitForHintMessage();
                if (hintMsg[i] == null) {
                    resetMsgFromTutoringService();
                    break;
                }
                if (i == 2) hintDelivered = true;
            }
            if (hintDelivered) {
                if ((!hintMsg[0].equals(hintMsg[1]) || !hintMsg[1].equals(hintMsg[2])) &&
                    (!hintMsg[0].startsWith(YOU_HAVE_SOLVED) ||
                     !hintMsg[1].startsWith(YOU_HAVE_SOLVED) ||
                     !hintMsg[2].startsWith(YOU_ARE_DONE)) ) {
                    hintDelivered = false;
                    resetMsgFromTutoringService();
                } else {
                    addHint(hintMsg[0]);
                }
            }
        }
        
        /*
        String message[] = {"clAlgebraTutorGotoOneStateBefore", "A new problem is set to", problem };
        SimSt.suspendForDebug(brController, "InquiryClAlgebraTutor", message);
        */
        
        for (int i = 0; i < edgeQueue.length; i++) {
        
            ProblemEdge edge = edgeQueue[i];
            if (edge != null) {
                
                EdgeData edgeData = edge.getEdgeData();

                String selection = (String)edgeData.getSelection().get(0);
                if (!SimSt.validSelection(selection, i)) {
                    return -2;
                }
                
                String input = (String)edgeData.getInput().get(0);
                Vector edgeSkillNames = edgeData.getSkills();
                String edgeSkillName = (String)edgeSkillNames.get(0);
                if (edgeSkillName.indexOf(' ') > 0) {
                    edgeSkillName = edgeSkillName.substring( 0, edgeSkillName.indexOf(' ') );
                }
                
                String msg = "  +++ sending stepPerformed for " + edge.getSource();
                msg += ", skillName: " + edgeSkillName + ", selection: " + selection + ", input: " + input;

                try
                {
                	boolean feedback = isCorrectStep(selection, null, input);
                }
        		catch(TutorServerTimeoutException e)
        		{
                    return -1;
        		}
                
                // This gives a hint for the step just performed by isCorrectStep() above
                // sendInquiryMsg("GetHint");
                String hintMsg = waitForHintMessage();
                if (hintMsg == null) {
                    return -1;
                }
                
                addHint(hintMsg);
            }
        }
        return edgeCount;
    }

    protected int searchLastEquation(String[] problem, ProblemEdge[] edgeQueue, Vector /* ProblemEdge */ pathEdges) {
            
    	int edgeCount = 0;

    	for (int i = 0; i < pathEdges.size(); i++) {
    		
    		edgeQueue[edgeCount++] = (ProblemEdge)pathEdges.get(i);
    		
    		if (edgeCount == 3) {
    			String[] eqSide = new String[2];
    			for (int j = 0; j < 2; j++) {
    				EdgeData edgeData = edgeQueue[j+1].getEdgeData();
    				String selection = (String)edgeData.getSelection().get(0);
    				if(selection.length() < SimSt.COMM_STEM.length()){
    					return -2;
    				}
    				char table = selection.charAt(SimSt.COMM_STEM.length());
	   				if(table != '1') //Not Single table format.  Switch multi table format to multi column, single table
	   				{
	   					int rowIndex = selection.indexOf('R')+1;
	   			    	char row = selection.charAt(rowIndex);
	   			    	selection = SimSt.COMM_STEM+"1_C"+table+"R"+row;
	   				}
    				String input = (String)edgeData.getInput().get(0);
    				if ("1".equals(getSelectionColumn(selection))) {
    					eqSide[0] = input;
    				} else if ("2".equals(getSelectionColumn(selection))) {
    					eqSide[1] = input;
    				} else {
    					return -2;
    				}
    			}
    			problem[0] = eqSide[0] + " = " + eqSide[1];
    			edgeCount = 0;
    			for (int k = 0; k < 3; k++) {
    				edgeQueue[k] = null;
    			}
    		}
    	}
    	return edgeCount;
    }
    
    protected int searchWebLastEquation(String[] problem, ProblemEdge[] edgeQueue, Vector /* ProblemEdge */ pathEdges, String runType) {
        
    	int edgeCount = 0;

    	for (int i = 0; i < pathEdges.size(); i++) {
    		
    		edgeQueue[edgeCount++] = (ProblemEdge)pathEdges.get(i);
    		
    		if (edgeCount == 3) {
    			String[] eqSide = new String[2];
    			for (int j = 0; j < 2; j++) {
    				EdgeData edgeData = edgeQueue[j+1].getEdgeData();
    				String selection = (String)edgeData.getSelection().get(0);
    				if(selection.length() < SimSt.COMM_STEM.length()){
    					return -2;
    				}
    				char table = selection.charAt(SimSt.COMM_STEM.length());
	   				if(table != '1') //Not Single table format.  Switch multi table format to multi column, single table
	   				{
	   					int rowIndex = selection.indexOf('R')+1;
	   			    	char row = selection.charAt(rowIndex);
	   			    	selection = SimSt.COMM_STEM+"1_C"+table+"R"+row;
	   				}
    				String input = (String)edgeData.getInput().get(0);
    				if ("1".equals(getSelectionColumn(selection))) {
    					eqSide[0] = input;
    				} else if ("2".equals(getSelectionColumn(selection))) {
    					eqSide[1] = input;
    				} else if(runType.equals("springBoot")) {
    					if ("0".equals(getSelectionColumn(selection))) {
        					eqSide[0] = input;
        				} else if ("1".equals(getSelectionColumn(selection))) {
        					eqSide[1] = input;
        				}
        			} else {
    					return -2;
    				}
    			}
    			problem[0] = eqSide[0] + " = " + eqSide[1];
    			edgeCount = 0;
    			for (int k = 0; k < 3; k++) {
    				edgeQueue[k] = null;
    			}
    		}
    	}
    	return edgeCount;
    }

    public static Vector /* ProblemEdge */ findPathDepthFirst(ProblemNode startNode, ProblemNode endNode) {

    	if(startNode == null || endNode == null)
    		return null;
    	
        if (startNode == endNode) 
            return null;

        ProblemEdge theEdge = null;
        if ((theEdge = startNode.isChildNode(endNode)) != null) {
            Vector path = new Vector();
            path.add(theEdge);
            return path; 
        } else {
            Vector /* ProblemNode */ childlen = startNode.getChildren();
            if (childlen.isEmpty()) {
                return null;
            } else {
                for (int i = 0; i < childlen.size(); i++) {
                    ProblemNode childNode = (ProblemNode)childlen.get(i);
                    Vector path = findPathDepthFirst(childNode, endNode);
                    if (path != null) {
                        path.add(0, startNode.isChildNode(childNode));
                        return path;
                    }
                }
                return null;
            }
        }
    }

    private Object getSelectionColumn(String selection) {
        int indexC = selection.indexOf('C');
        int indexR = selection.indexOf('R');
        String selectionColumn = selection.substring(indexC +1, indexR);
        return selectionColumn;
    }

    // When failed to connect to the Tutoring Service server, restart the server.
    // We are assuming that there three java program running -- PteServer, PtsClient dog, PtsClient cat
    /*
    public static void relaunchTutoringService() {
        
        String cmd[] = { 
                "/bin/bash",
                "/Users/mazda/mazda-on-Mac/Project/CTAT/CVS-TREE/Tutors/SimSt/SimStAlgebraI/shutdownPtsPlus.sh"
        };
        
        Process bash = null;
        try {
            bash = Runtime.getRuntime().exec(cmd);
            int j = 0;
            for (int i = 0; i < 1000000000; i++) {
                j += i;
            }
            System.out.println("j = " + j);
            InputStream stdin = bash.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stdin);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            System.out.println("<OUTPUT>");
            while ( (line = br.readLine()) != null)
                System.out.println(line);
            System.out.println("</OUTPUT>");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        // BufferedWriter cmdLine = new BufferedWriter(new OutputStreamWriter(bash.getOutputStream()));
        
//        try {
//            cmdLine.write(cmd);
//            cmdLine.flush();
//            // bash.waitFor();
//        } catch (IOException e) {
//            e.printStackTrace();
////        } catch (InterruptedException e) {
////            e.printStackTrace();
//        }
    }
    */

    /**
     * @param args
     */
    public static void main(String[] args) {
        trace.setTraceLevel(5);
        String host = "matsuda-pro.pc.cs.cmu.edu";
        String port = "7878";
        InquiryClAlgebraTutor iCAT = new InquiryClAlgebraTutor(host, port);
        iCAT.setCurrentProblem("problemName");
        boolean result = false;
        try
        {
        	result = iCAT.isCorrectStep("selection", "action", "input");
        }catch(TutorServerTimeoutException e)
        {
        	e.printStackTrace();
        }
        System.out.println(result);
    }

}
