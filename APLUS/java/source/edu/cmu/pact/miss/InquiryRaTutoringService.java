package edu.cmu.pact.miss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import edu.cmu.pact.SocketProxy.SocketProxy;
import edu.cmu.pact.Utilities.trace;

/** Wed Jan 17 16:46:48 LMT 2007
 *
 * Communicating with Stoichiometry Tutor (Example-Tracing) via 
 * the Tutoring Service
 * 
 * @author mazda
 *
 */
public class InquiryRaTutoringService {

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Class Fields
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    // XML Tags and values
    private static final String SELECTION = "Selection";
    private static final String VALUE = "value";
    private static final String ACTION = "Action";
    private static final String INPUT = "Input";
    private static final String MESSAGE_TYPE = "MessageType";
    private static final String INTERFACE_ACTION = "InterfaceAction";
    private static final String PROPERTIES = "properties";
    private static final String VERB = "verb";
    private static final String NOTE_PROPERTY_SET = "NotePropertySet";
    private static final String MESSAGE = "message";
    private static final String ASSOCIATED_RULES = "AssociatedRules";
    private static final String CORRECT_ACTION = "CorrectAction";
    private static final String START_STATE_END = "StartStateEnd";
    private static final String INDICATOR = "Indicator";
    private static final String CORRECT = "Correct";
    private static final String HIGHLIGHT = "HighlightMsg";
    
    private static final String[] legalTutorResponseList = {"CorrectAction", "InCorrectAction" };
    
    // Communication ports
    // 
    private int SERVER_PORT = 1502;
    // private String SERVER_HOST = "vienna.pslc.cs.cmu.edu";
    private String SERVER_HOST = "localhost";

    private int COMM_PORT = 1503;
    
    private int POLICY_PORT = 1504;
    
    private Socket serverSocket = null;
    private Socket getServerSocket() {
        return serverSocket; 
    }
    private void setServerSocket(Socket socket) {
        this.serverSocket = socket;
    }

    /*
    private Socket policySocket = null;
    private Socket getPolicySocket() {
        return policySocket;
    }
    private void setPolicySocket(Socket policySocket) {
        this.policySocket = policySocket;
    }
    */
    
    PrintWriter out = null;
    BufferedReader in = null;

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    /**
     * 
     */
    public InquiryRaTutoringService() {
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // I/O
    // 
    
    public void init(String problemName) {
        // initPolicySocket();
        initServerSocket();
        initIO();
        sendInterfaceIdentification();
        sendProblemDescription(problemName);
    }
    
    private void initServerSocket() {
        Socket socket = null;
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setServerSocket(socket);
        if(trace.getDebugCode("miss"))trace.out("miss", "InquiryRaTutoringService: initServerSocket() done...");
    }
    
    /*
    private void initPolicySocket() {
        Socket socket = null;
        try {
            socket = new Socket(SERVER_HOST, POLICY_PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setPolicySocket(socket);
    }
    */

    private void initIO() {
        try {
            out = new PrintWriter(getServerSocket().getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(getServerSocket().getInputStream()));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    public void shutdown() {
        try {
            in.close();
            out.close();
            getServerSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Add '\0' at the end if it's missing, because the server expect to see it 
    // at the end of a message (so that a new-line can be a part of the message)
    private void sendMsgToTutoringService(String msg) {
        if (msg.charAt(msg.length() -1) != '\0') {
            msg += "\0";
        }
        out.println(msg.toString());
        if(trace.getDebugCode("miss"))trace.out("miss", "sendMsgToTutoringService sent -> " + msg);
    }
    
    // The server also send a message with '\0' at the end
    private String readMsgFromTutoringService() {
        String msg = null;
        try {
            msg = SocketProxy.readToEom(in, '\0');
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }

    // Keep reading a socket input channel until an XML message with 
    // a specified message type
    private void waitMsgTypeFromTutoringService(String targetType) {
        String msg = readMsgFromTutoringService();        
        while (!isMessageType(msg, targetType)) {
            msg = readMsgFromTutoringService();
        }
    }
    
    /*
    private void clearInputChannel() {
        try {
            while (getServerSocket().getInputStream().available() != 0) {
                SocketProxy.readToEom(in, '\0');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Initialization protocol
    // 

    String msgInterfaceID = "<message><verb>NotePropertySet</verb><properties><MessageType>InterfaceIdentification</MessageType><Guid>0E33AF97-8289-A9D4-E14E-C56E57133286</Guid></properties></message>";
    private void sendInterfaceIdentification() {
    	if(trace.getDebugCode("miss"))trace.out("miss", "InquiryRaTutoringService: sending interface ID");
        sendMsgToTutoringService(msgInterfaceID);
    }

    String msgProblemDescription = "<message><verb>NotePropertySet</verb><properties><MessageType>SetPreferences</MessageType><school_name>CMU</school_name><log_service_url>http://pslc-qa.andrew.cmu.edu/log/serverdd</log_service_url><problem_name>myGraphName</problem_name><session_id>mySessionID</session_id><user_guid>myUniqueUserIdentifier</user_guid><auth_token>myAuth_token</auth_token><source_id>PACT_CTAT_FLASH</source_id><question_file>PROBLEM_NAME</question_file><ProblemName>PROBLEM_NAME</ProblemName></properties></message>";
    private void sendProblemDescription(String problemName) {

    	if(trace.getDebugCode("miss"))trace.out("miss", "InquiryRaTutoringService: sending problem description for " + problemName);
        sendMsgToTutoringService(msgProblemDescription.replaceAll("PROBLEM_NAME", problemName));
        System.out.println("passed sendMsgToTutoringService");
        
        // After sending a problem description, the server acknowledges by sending 
        // a bunch of messages back to the client. In order for the rest of the tasks
        // to work properly, those messages must be read off the socket
        waitMsgTypeFromTutoringService(START_STATE_END);
        System.out.println("finished sendProblemDescription");
    }
    
    String msgGoToState = "<message><verb>NotePropertySet</verb><properties><MessageType>GoToState</MessageType><StateName><value>STATE_NAME</value></StateName></properties></message>";
    public void sendGoToStateMsg(String stateName) {
    	if(trace.getDebugCode("miss"))trace.out("miss", "sendGoToStateMsg(" + stateName + ")");
        sendMsgToTutoringService(msgGoToState.replaceAll("STATE_NAME", stateName));
    }
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // XML helpers
    // 

    private String getMessageElement(String msg, String name) {
        return msg.split("<" + name + ">")[1].split("</" + name + ">")[0];
    }

    private String getMessageType(String msg) {
        return getMessageElement(msg, "MessageType"); 
    }
    
    private boolean isMessageType(String msg, String type) {
        return getMessageType(msg).equals(type);
    }
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // See if the give Selection, Action, Input is sound or not
    // 
    
    public boolean testSAI(String selection, String action, String input) {
        System.out.println("entered testSAI");
        
        boolean testSAI = false;
        
        // Send an interface action that comprises SAI
        sendInterfaceAction(selection, action, input);
        System.out.println("passed sendInterfaceAction");

        // Wait for an acknowledge from the tutor
        String tutorResponse = getTutorAckOnCorrectness();
        System.out.println("passed tutorResponse");
        
        if (tutorResponse.equals(CORRECT)) {
            testSAI = true;
        }
        
        if(trace.getDebugCode("miss"))trace.out("miss", "testSAI(" + selection + ", " + action + ", " + input + ") = " + testSAI);
        return testSAI;
    }
    
    private void sendInterfaceAction(String selection, String action, String input) {
        String xmlMsg = generateXmlForSAI(selection, action, input);
        sendMsgToTutoringService(xmlMsg);
    }
    
    private String generateXmlForSAI(String selection, String action, String input) {

        Element selectionElement = new Element(SELECTION);
        selectionElement.addContent(new Element(VALUE).setText(selection));
        
        Element actionElement = new Element(ACTION);
        actionElement.addContent(new Element(VALUE).setText(action));
        
        Element inputElement = new Element(INPUT);
        inputElement.addContent(new Element(VALUE).setText(input));
        
        Element messageTypeElement = new Element(MESSAGE_TYPE);
        messageTypeElement.setText(INTERFACE_ACTION);
        
        Element propertiesElement = new Element(PROPERTIES);
        propertiesElement.addContent(messageTypeElement);
        propertiesElement.addContent(selectionElement);
        propertiesElement.addContent(actionElement);
        propertiesElement.addContent(inputElement);

        Element verbElement = new Element(VERB);
        verbElement.setText(NOTE_PROPERTY_SET);
        
        Element rootElement = new Element(MESSAGE);
        rootElement.addContent(verbElement);
        rootElement.addContent(propertiesElement);
        
        XMLOutputter output = new XMLOutputter();
        // output.setFormat(Format.getPrettyFormat());
        
        return output.outputString(rootElement);
    }

    // Waiting for an acknowledge from a tutoring service after sending in 
    // an interface action
    private String getTutorAckOnCorrectness() {

        String tutorAckOnCorrectness = null;
        boolean tutorResponseRead = false;
        boolean associatedRulesRead = false;
        
        // There are two messages expected to be sent from
        // the tutoring service.  The order is not always fixed.
        // PLUS, the tutoring service might has sent HighlightMsg for  
        // the previous error step
        while (!tutorResponseRead || !associatedRulesRead) {
            
            String msg = readMsgFromTutoringService();
            if (isMessageType(msg, ASSOCIATED_RULES)) {
                associatedRulesRead = true;
                tutorAckOnCorrectness = getMessageElement(msg, INDICATOR);
            } else if (isMessageType(msg, HIGHLIGHT)) {
                continue;
            } else if (isLegalTutorResponse(getMessageType(msg))) {
                tutorResponseRead = true;
            } else {
                System.out.println("getMessageType(msg) = " + getMessageType(msg));
                new Exception("Unknown tutor responce: " + msg).printStackTrace();
            }
        }

        return tutorAckOnCorrectness; 
    }

    // <message><verb>SendNoteProperty</verb><properties><MessageType>CorrectAction</MessageType><Selection><value>Term0Definition</value></Selection><Action><value>UpdateComboBox</value></Action><Input><value>Given Value</value></Input></properties></message> 
    // <message><verb>SendNoteProperty</verb><properties><MessageType>InCorrectAction</MessageType><Selection><value>Term1Definition</value></Selection><Input><value>Solution Concentration</value></Input></properties></message> 
    // <message><verb>SendNoteProperty</verb><properties><MessageType>AssociatedRules</MessageType><Indicator>Correct</Indicator><Selection><value>Term0Definition</value></Selection><Action><value>UpdateComboBox</value></Action><Input><value>Given Value</value></Input><Rules><value>Select-Given-Value-Reason Chemistry-Skills</value></Rules></properties></message> 
    // <message><verb>SendNoteProperty</verb><properties><MessageType>AssociatedRules</MessageType><Indicator>InCorrect</Indicator><Selection><value>Term1Definition</value></Selection><Action><value>UpdateComboBox</value></Action><Input><value>Solution Concentration</value></Input><Rules><value>Select-Unit-Conversion-Reason Chemistry-Skills</value></Rules></properties></message>
    
    // <message><verb>SendNoteProperty</verb><properties><MessageType>HighlightMsg</MessageType><HighlightMsgText>Please work on the highlighted step.</HighlightMsgText><Selection><value>Numerator4Units</value></Selection><Action><value>UpdateComboBox</value></Action></properties></message> 
    
    private boolean isLegalTutorResponse(String messageType) {
        boolean legalTutorResponse = false;
        for (int i = 0; i < legalTutorResponseList.length; i++) {
            if (legalTutorResponseList[i].equals(messageType)) {
                legalTutorResponse = true;
                break;
            }
        }
        return legalTutorResponse;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // debugging & testing
    // 
    
    /*
    String msgStep1 = "<message><verb>NotePropertySet</verb><properties><MessageType>InterfaceAction</MessageType><Selection><value>Term0Definition</value></Selection><Action><value>UpdateComboBox</value></Action><Input><value>Given Value</value></Input></properties></message>\0";
    private void sendStep1() {
        System.out.println("sending step-1");
        out.println(msgStep1);
        System.out.println("done");
    }
    */
    
    private void testTutoringService() {
        
        init("ChemPT_1T_01_IU.brd");
        
        /*
        CommChannel cc = new CommChannel(in, out);
        cc.start();
        */
        
        // Indeed, only need to wait for "StartStateEnd" 
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // sendStep1();

        boolean step1 = testSAI("Term0Definition", "UpdateComboBox", "Given Value");
        System.out.println("step1 = " + step1);
        
        while (true) {}
        
        /*
        try {
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        /*
        InquiryRaTutoringService ira = new InquiryRaTutoringService();
        String xmlDoc = ira.generateXmlForSAI("@SEL@", "@ACT@", "@INPUT@");
        System.out.println(xmlDoc);
        */
        
        trace.addDebugCode("testTS");
        trace.addDebugCode("sp");
        trace.addDebugCode("miss");
        InquiryRaTutoringService ira = new InquiryRaTutoringService();
        ira.testTutoringService();
        System.exit(1);

    }
    
    /**
     * Communication channel
     */
    class CommChannel extends Thread {

        BufferedReader in;
        PrintWriter out;
        
        public CommChannel(BufferedReader in, PrintWriter out) {
            this.in = in;
            this.out = out;
        }
        
        public void run() {
         
            System.out.println("waiting msg from the server...");
            while (true) {
                try {
                    String msgFromServer = SocketProxy.readToEom(in, '\0');
                    System.out.println("MSG -> " + msgFromServer);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
