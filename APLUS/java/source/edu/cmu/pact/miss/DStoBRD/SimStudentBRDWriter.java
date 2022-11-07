/*
* Carnegie Mellon Univerity, Human Computer Interaction Institute
* Copyright 2005
* All Rights Reserved
*/

package edu.cmu.pact.miss.DStoBRD;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.cmu.pact.Utilities.trace;
import org.xml.sax.SAXException;

import com.megginson.sax.DataWriter;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.Groups.DefaultGroupModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.ActionLabelElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.EdgeElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.MatcherElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.MessageElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.NodeElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.PropertiesElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.StartNodeMessagesElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.StateGraphElement;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements.VectorProperty;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

public class SimStudentBRDWriter{
	
    private String workingDir;
    private String getWorkingDir() { return workingDir; }
    protected void setWorkingDir(String workingDir) {  
        this.workingDir = workingDir;
    }
    
    private int numStudents = 0;
    private void resetNumStudents() { numStudents = 0; }
    protected void incNumStudents() { numStudents++; }
    private int getNumStudents() { return numStudents; }

    private int numBrdTransactions = 0;
    protected void resetNumBrdTransactions() { numBrdTransactions = 0; }
    protected void incNumBrdTransactions() { numBrdTransactions++; }
    private int getNumBrdTransactions() { return numBrdTransactions; }

    private int numBrdFiles = 0;
    protected void resetNumBrdFiles() { numBrdFiles = 0; }
    private void incNumBrdFiles() { numBrdFiles++; }
    private int getNumBrdFiles() { return numBrdFiles; }

    private int numBrokenBrdk = 0;
    private void resetNumBrokenBrd() { numBrokenBrdk = 0; }
    private void incNumBrokenBrd() { numBrokenBrdk++; }
    private int getNumBrokenBrd() { return numBrokenBrdk; }
   
    /*ID of the source node of the last correct node*/
    private int lastCorrectNodeIDSource=1;
    public void setLastCorrectNodeIDSource(int value){ lastCorrectNodeIDSource=value;} 
    public int getLastCorrectNodeIDSource() { return lastCorrectNodeIDSource; }
    
    /*ID of the last correct node  */
    private int lastCorrectNodeID=1;
    public void setLastCorrectNodeID(int value){ lastCorrectNodeID=value;} 
    public int getLastCorrectNodeID() { return lastCorrectNodeID; }
        
    /**
     * Default constructor which uses a pooled connection factory.
     * @see PooledConnectionFactory
     */
    public SimStudentBRDWriter(){}
    
    /**
     * @return
     */
    private StateGraphElement writeRootElement() {
        StateGraphElement xmlstategraph = new StateGraphElement();
        xmlstategraph.addcaseInsensitive(true);
        xmlstategraph.addfirstCheckAllStates(true);
        xmlstategraph.addlockWidget(true);
        xmlstategraph.addversion("1.0");
        xmlstategraph.addunordered(true);
        return xmlstategraph;
    }
    
  
 
    protected NodeEdgePairs nodeList(String problemName, ArrayList /* StudentTransactionItems */ actionList){
        ArrayList nodeElemList = new ArrayList();
        ArrayList edgeElemList = new ArrayList();
        int id = 1;
        int nodeID = 1;
        int edgeID = 1;
     //   int lastCorrNodeID = nodeID;  
        int x = 200;
        int y = 30;
        int startNodeID=1;
        
        /*Initialize the lastCorrectNodeID and its source to 1 (start of brd graph)*/
        setLastCorrectNodeID(nodeID);
        setLastCorrectNodeIDSource(nodeID);
        
        NodeElement startState = new NodeElement();
        
        //startState.addtext(problemName.substring(5)); //eliminates "Eg##"
        startState.addtext(parseProblemName(problemName));
        
        startState.addlocked(false);
        startState.adddoneState(false);
        startState.adddimension(x, y); 
        startState.adduniqueID(nodeID);
        nodeElemList.add(startState);
        nodeID++;
        
       
        for(int i=0; i<actionList.size(); i++){
            StudentTransactionItems item = (StudentTransactionItems)actionList.get(i);
                  
            	/*Check if current action should be ignored (e.g. problem restarted, step undone)*/
            	if (transactionNotActualBrdEdge(item)){
            		continue;          		
            	}
            
  
            if (!item.validBrdTransaction()) { return null; }
                        
            ActionLabelElement actionLabel = new ActionLabelElement();

            MessageElement message = new MessageElement();
            message.addverb("NotePropertySet");
            PropertiesElement properties = new PropertiesElement();
            properties.addProperty("MessageType", "InterfaceAction");

            VectorProperty selectionProp = new VectorProperty("Selection");
            selectionProp.addValue(item.selection);
            properties.addProperty("Selection", selectionProp);
            VectorProperty actionProp = new VectorProperty("Action");
            actionProp.addValue(item.action);
            properties.addProperty("Action", actionProp);
            VectorProperty inputProp = new VectorProperty("Input");
            inputProp.addValue(item.input);
            properties.addProperty("Input", inputProp);
            message.addproperties(properties);

            MatcherElement matcher = new MatcherElement();
            matcher.addmatcherType("ExactMatcher");
            matcher.addmatcherParameter("selection", item.selection);
            matcher.addmatcherParameter("action", item.action);
            matcher.addmatcherParameter("input", item.input);


            actionLabel.addmessage(message);
            actionLabel.addMatcher(matcher);
//            actionLabel.addtext("[" + item.input+"], ["+item.selection +"], [" + item.action + "]" );

            EdgeElement edge = new EdgeElement();
            edge.addactionLabel(actionLabel);
            edge.addrule(item.subskillName + " eq", -1); //change skill category
            edge.addprecheckedStatus("No-Applicable");

            NodeElement node = new NodeElement();
            String stateText = "state" + (new Integer(i+1)).toString();
            node.addtext(stateText);

            if(proceedToNextNode(item)){
                actionLabel.addpreferPathMark(true);
                actionLabel.addstudentHintRequest("false");
                actionLabel.addstepSuccessfulCompletion("false");
                actionLabel.addstepStudentError("false");
                actionLabel.addbuggyMessage("");
                actionLabel.addsuccessMessage("");
                actionLabel.addhintMessage("");
                //actionLabel.addactionType("Correct Action");    
                actionLabel.addactionType(decodeCorrectness(item.outcome));
                actionLabel.adduniqueID(edgeID);

                //TODO: make sure what are the properties
                actionLabel.addoldActionType("Correct Action");
                actionLabel.addcheckedStatus("Never Checked");
                
                /* keep the last correct node before its updated so at all times we 
                 * know the source of the last correct node (in case we have "step undo") */
                setLastCorrectNodeIDSource(getLastCorrectNodeID());
         
                //edge.addsourceID(lastCorrNodeID);
                edge.addsourceID(getLastCorrectNodeID());
                edge.adddestID(nodeID);
                //lastCorrNodeID = nodeID;
                setLastCorrectNodeID(nodeID);
                node.adduniqueID(nodeID);
                nodeID++;
                node.adddimension(x, y + (100 * nodeID));

            }else{
                //TODO: differentiate BUG and ERROR actions
                actionLabel.addpreferPathMark(false);
                actionLabel.addstudentHintRequest("false");
                actionLabel.addstepSuccessfulCompletion("false");
                actionLabel.addstepStudentError("false");
                actionLabel.addbuggyMessage("");
                actionLabel.addsuccessMessage("");
                actionLabel.addhintMessage("");

                
             /*   if (item.outcome.equalsIgnoreCase("BUG")) {
                    actionLabel.addactionType(EdgeData.BUGGY_ACTION);
                } else if (item.outcome.equalsIgnoreCase("ERROR")) {
                    actionLabel.addactionType(EdgeData.CLT_ERROR_ACTION);
                } else if (item.subskillName.equalsIgnoreCase("hint")) {
                    actionLabel.addactionType(EdgeData.HINT_ACTION);
                }
              */  
                actionLabel.addactionType(decodeCorrectness(item.outcome));
                
                actionLabel.addoldActionType("Correct Action");
                actionLabel.addcheckedStatus("Never Checked");
                actionLabel.adduniqueID(edgeID);

               // edge.addsourceID(lastCorrNodeID);
                edge.addsourceID(getLastCorrectNodeID());
                edge.adddestID(nodeID);
                node.adduniqueID(nodeID);
                nodeID++;
                node.adddimension(x + 100 , y + (100 * nodeID));
            }

            edgeID++;

            edge.addtraversalCount(0);
            
            edgeElemList.add(edge);

            node.addlocked(false);
            node.adddoneState(false);
            nodeElemList.add(node);
        }
        //}
        return new NodeEdgePairs(nodeElemList, edgeElemList);
    }
    
    /**
 	 * Method used to construct the actual string that will mark the correctness of a brd edge. It maps
 	 * the BRDWritter input file outcome column string (which is either "OK" or "ERROR" or "BUG" or "UNTRACEABLE_ERROR), 
 	 * to a "CTAT compliant" string (i.e. Correct Action, Buggy Action, Error Action, Untraceable Error). 
 	 * Cannot understand why input follow outcome column wasn't "CTAT compliant" from the beginning...
 	 * 
 	 * @param outcome  
 	 * @return CTAT compliant string about the correctness of an edge
 	 */
	protected String decodeCorrectness(String outcome){
 		String returnString="";
 				 
 		if (outcome.equals("OK")){
 			returnString=EdgeData.CORRECT_ACTION;
 		}
 		else if (outcome.equalsIgnoreCase("BUG")) {
             returnString=EdgeData.BUGGY_ACTION;
        } else if (outcome.equalsIgnoreCase("ERROR")) {
        		return EdgeData.CLT_ERROR_ACTION;
        } 
        else if (outcome.equalsIgnoreCase("UNTRACEABLE_ERROR")) {
        		return EdgeData.UNTRACEABLE_ERROR;
        }else if (outcome.equalsIgnoreCase("hint")) {
             returnString=EdgeData.HINT_ACTION;
        }
 		 
 		 return returnString;		
 	}
	
	
    /***
     * Method that determines if a current transaction does not actually correspond ton a brd "edge" but
     * to a transaction that "points" to the next current node (e.g. new problem entered transaction or 
     * action step undone transaction). Original SimStudentBRDWritter assumed input file contained only
     * transactions that are edges, so this returns false at all times. Must be overriden to extend BRDWritter
     * functionality.
     *  
     * @param item 
     * @return
     */
    protected boolean transactionNotActualBrdEdge(Object item){	
    	return false;
    }
    

    /***
 	 *  Method that determines if we should advance to next node or not. A typical brd advances
 	 *  to next node only if transaction is correct.
 	 * 
 	 * @param item Object that holds a single transaction information to create a brd edge.
 	 * @retunrs true if transaction corresponds to a correct action.
 	 */
	protected boolean proceedToNextNode(Object item) {
		return ((StudentTransactionItems) item).outcome.equalsIgnoreCase("OK");
	}
    
    /**
     * Method created to abstract the process of geting the problem name from the input file. 
     * In this class, this method removes "Eq##" from the end of the problem name.
     * 
     * @param probName
     * @return 
     */
    protected String parseProblemName(String probName){
    	return probName.substring(5);	
    }
    

    private StateGraphElement buildGraphfromFile(ArrayList /* StudentTransactionItems */ actionList, String probName){ 	
    	
        StateGraphElement xmlstategraph = writeRootElement(); 
        
        try{
        	
            // trace.out("buildGraphfromFile: problemName = " + probName);
        	//used to be hardcoded problName.substring(5). Now it gets value from parseProblemName which can be overriden
           // StartNodeMessagesElement startMessage = startNodeMessages("StartNodeMsgTemplate.txt", probName.substring(5)); //eliminates "Eg##"
            StartNodeMessagesElement startMessage = startNodeMessages("StartNodeMsgTemplate.txt", parseProblemName(probName)); //eliminates "Eg##"
            
            //String probName = "-5x-4 = -6-4x";
            //ArrayList actionList = modifyCLActions(transList, student, probName);
            NodeEdgePairs nodeEdgePairs = nodeList(probName, actionList);
            if (nodeEdgePairs == null) { return null; } //!!!!
            
            			/*this practically means that brd has no edges*/
                        if (nodeEdgePairs.edgeList.size()==0){ 
                        	return null;
                        	}
                        
            xmlstategraph.addStartNodeMessages(startMessage);

            for(int j=0; j < nodeEdgePairs.nodeList.size(); j++){
                NodeElement node = (NodeElement)nodeEdgePairs.nodeList.get(j);
                xmlstategraph.addNode(node);
            }

            for(int k = 0; k < nodeEdgePairs.edgeList.size(); k++){
                EdgeElement edge = (EdgeElement)nodeEdgePairs.edgeList.get(k);
                xmlstategraph.addEdge(edge);
            }
            
        } catch( IOException e ){
            e.printStackTrace();
        }

        return xmlstategraph;
    }
    
    protected StartNodeMessagesElement startNodeMessages(String inputTemplate, String probName)
    throws IOException{
        StartNodeMessagesElement StartmessageList = new StartNodeMessagesElement();
        BufferedReader in = new BufferedReader(new FileReader(inputTemplate));
        in.readLine(); //header of the file
        String fileLine = in.readLine(); //start of the data

        while(fileLine != null){
            if(fileLine.endsWith("<message>")){
                MessageElement message = new MessageElement();
                fileLine = in.readLine();

                if(fileLine.endsWith("</verb>")){
                    message.addverb(fileLine.substring(fileLine.indexOf(">")+1, 
                            fileLine.lastIndexOf("<")));
                }
                fileLine = in.readLine();

                if(fileLine.endsWith("<properties>")){
                    fileLine = in.readLine();

                    PropertiesElement properties = new PropertiesElement();
                    while(!fileLine.endsWith("</properties>")){
                        int firstIndex = fileLine.indexOf(">");
                        int lastIndex = fileLine.lastIndexOf("<");
                        if(firstIndex < lastIndex){ //addProperty
                            String propName = fileLine.substring(fileLine.indexOf("<")+1,  firstIndex);
                            String value = fileLine.substring(firstIndex+1, lastIndex);
                            
                            if(value.equalsIgnoreCase("$(PROB_NAME)$")){
                                value = probName;
                            } else if(value.equalsIgnoreCase("$(GIVEN_1)$")){
                                int LHSIndex = (probName).indexOf("=");
                                if(probName.charAt(LHSIndex-1) == ' ')
                                    value = probName.substring(0, LHSIndex-1);
                                else
                                    value = probName.substring(0, LHSIndex);

                            } else if(value.equalsIgnoreCase("$(GIVEN_2)$")){
                                int LHSIndex = (probName).indexOf("=");
                                if(probName.charAt(LHSIndex+1) == ' ')
                                    value = probName.substring(LHSIndex+2);
                                else
                                    value = probName.substring(LHSIndex+1);
                            }
                            properties.addProperty(propName, value);
                        }else{ // add vector property	    					
                            String propName = fileLine.substring(fileLine.indexOf("<")+1, firstIndex);
                            VectorProperty vectorProp = new VectorProperty(propName);
                            fileLine = in.readLine();

                            while(!fileLine.endsWith("</"+propName+">")){
                                int begin = fileLine.indexOf(">");
                                int end = fileLine.lastIndexOf("<");
                                String value = fileLine.substring(begin+1, end);
                                if(value.equalsIgnoreCase("$(PROB_NAME)$")){
                                    value = probName;
                                } else if(value.equalsIgnoreCase("$(GIVEN_1)$")){
                                    int LHSIndex = (probName).indexOf("=");
                                    if(probName.charAt(LHSIndex-1) == ' ')
                                        value = probName.substring(0, LHSIndex-1);
                                    else
                                        value = probName.substring(0, LHSIndex);

                                } else if(value.equalsIgnoreCase("$(GIVEN_2)$")){
                                    int LHSIndex = (probName).indexOf("=");
                                    if(probName.charAt(LHSIndex+1) == ' ')
                                        value = probName.substring(LHSIndex+2);
                                    else
                                        value = probName.substring(LHSIndex+1);
                                }

                                vectorProp.addValue(value);
                                fileLine = in.readLine();
                            }
                            properties.addProperty(propName, vectorProp);
                        }
                        fileLine = in.readLine();
                    }
                    message.addproperties(properties);
                }
                StartmessageList.addmessage(message);
            }
            fileLine = in.readLine();
        }
        return StartmessageList;
    }
    
    /***
     * Method that creates parses the input file and creates a "transaction item" object, that holds
     * all the necessary information about an edge of a brd.
     * 
     * @param fileLine
     * @return Object that holds all the necessary transaction information about a brd edge. 
     */
    protected Object createTransactionItem(String fileLine){
    		String[] tokens = fileLine.split("\t");
    		/*
         	0. id  
         	1. student_name    
         	2. problem_name    
         	3. selection       
         	4. action  
         	5. input   
         	6. skill   
         	7. outcome
    		 */
    	 
    	   int base = 1;
           String student = tokens[base+0];
           String problem = tokens[base+1];
           String selection = tokens[base+2];
           String action = tokens[base+3];
           String input = tokens[base+4];
           String skill = tokens[base+5];
           String outcome = tokens[base+6];
           
           
        /*   trace.out();
           trace.out(student);
           trace.out(problem);
           trace.out(selection);
           trace.out(action);
           trace.out(input);
           trace.out(skill);
           trace.out(outcome);
           trace.out();
           */
           StudentTransactionItems item = 
                   new StudentTransactionItems(student, problem, selection, action, input, skill, outcome);
           return item;
           
    }
    
    
    protected void printBRD(String fileName) throws IOException, SAXException {
        
        ArrayList /* StudentTransactionItems */ transactionList = new ArrayList();

        BufferedReader in = new BufferedReader(new FileReader(fileName));
        String fileLine = null;
        
        String previousProblemName = null;
        String lastStudent = null;

        in.readLine(); // header of the file
        while((fileLine = in.readLine()) != null){
            incNumBrdTransactions();
            
             /*get the transaction item*/
             Object item= createTransactionItem(fileLine);
                    
            if (lastStudent == null){
            	lastStudent = ((StudentTransactionItems) item).getStudentID();
            	incNumStudents();
            }
            
            if (!((StudentTransactionItems) item).getStudentID().equals(lastStudent)) {
            	lastStudent = ((StudentTransactionItems) item).getStudentID();
            	incNumStudents();
                }
            
            if (previousProblemName == null) previousProblemName = ((StudentTransactionItems) item).getProblemName();
           
            //  if (!problem.equals(previousProblemName)) {
            if (newProblemTransaction(item, transactionList)){
        	
                // Read a transaction for a new (i.e., different) problem hence flush the queue
            	
            	//if (((StudentTransactionItems) transactionList.get(0)).action.equals("") && transactionList.size()==1){
            	//	trace.out("Generating brd for ... " + previousProblemName + " with transaction " + transactionList.size());
            		
            	//	}
                generateBRD(transactionList, previousProblemName);

                transactionList.clear();
                previousProblemName = ((StudentTransactionItems) item).getProblemName();
            }
            
            /*add transaction in the transaction list only if it is usefull in creating the brd graph*/
            if (transactionImportant(item))
            	transactionList.add(item);
        }
        
       // if (((StudentTransactionItems) transactionList.get(0)).action.equals("") && transactionList.size()==1){
    //		trace.out("Generating brd for ... " + previousProblemName + " with transaction " + transactionList.size());
    		
    	//	}
        generateBRD(transactionList, previousProblemName);
        /*
        StateGraphElement xmlstategraph = buildGraphfromFile(transactionList, previousProblemName);
        String studentName = ((StudentTransactionItems)transactionList.get(0)).studentID;
        if ( xmlstategraph != null ) {
            String brdFileName = brdFileName(previousProblemName);
            // trace.out("Writing BRD to " + brdFileName);
            writeBrdToFile(xmlstategraph, brdFileName, studentName);
        } else {
            incNumBrokenBrd();
        }
        */
    }
    
    /***
     * Method that determines if a transaction is important (i.e. contains useful information related to the creation of the brd file) 
     * in order to be added to the transaction list (that is used to create the actual brd file). 
     * @param item Object that holds all the necessary transaction information about a brd edge. 
     * @return
     */
    protected boolean transactionImportant(Object item){
    	//returns true because original version of SimStudentBRDWriter (i.e. this file) 
    	// assumed input file contained only useful transactions  (e.g. "New Problem Entered" was not taken into account at all).
    	return true;    	
    }
       
    /***
     * Method that given an transaction line (inputItem) and a list of transactions that all belong to the same problem, 
     * determines if inputItem is a new problem. It is assumed that all the items of the list are for the same transaction 
     * 
     * @param inputItem
     * @param transactionList
     * @return true if item and transactions in list belong to the same item. 
     */
    protected boolean newProblemTransaction(Object inputItem, ArrayList transactionList){
    	boolean returnValue=false;

    	if (!transactionList.isEmpty()){
    		//since all items in the list belong to the same problem, we can get the problem name from any of them. 
    		String previousProblemName=((StudentTransactionItems) transactionList.get(transactionList.size()-1)).getProblemName();
    		returnValue=!((StudentTransactionItems) inputItem).getProblemName().equals(previousProblemName);
    	}
    	
    	return returnValue;
    }
    
    
    protected void generateBRD(ArrayList transactionList, String previousProblemName)
    throws FileNotFoundException, SAXException {
	StateGraphElement xmlstategraph = buildGraphfromFile(transactionList, previousProblemName);
	String studentName = ((StudentTransactionItems)transactionList.get(0)).studentID;
	if ( xmlstategraph != null ) {
	    String brdFileName = brdFileName(previousProblemName);
	    
	    // trace.out("Writing BRD to " + brdFileName);
	    
	    writeBrdToFile(xmlstategraph, brdFileName, studentName);
	} else {
	    incNumBrokenBrd();
	}
    }

    private void writeBrdToFile(StateGraphElement xmlstategraph, String outputFile, String studentName)

    throws FileNotFoundException, SAXException {

        String filePathName = getWorkingDir() + "/" + studentName + "/" + getBrdNo() + "_" + outputFile;
        String parentDir = new File(filePathName).getParent();
        new File(parentDir).mkdirs();
        
    //    if (flag==true)
	 //   	trace.out("Writing BRD to " + filePathName);
        
        PrintWriter pw = new PrintWriter(new FileOutputStream(filePathName));
        DataWriter w = new DataWriter(pw);
        w.setIndentStep(4);
        w.startDocument();
       
        xmlstategraph.printXML(w);
        w.endDocument();
        pw.close();
        incNumBrdFiles();
    }
    
    private String getBrdNo() {
	String brdNo = "000000" + getNumBrdFiles();
	return brdNo.substring(brdNo.length()-6);
    }

    private String brdFileName(String problemName) {
        
        String outputFile = problemName.replaceAll(" ", "") + ".brd";

        //modify the problem names due to data import problem
        // - in the beginning becomes M
        if(outputFile.indexOf('-') == 0){
            outputFile = 'm' + outputFile.substring(1);
        }
        // = would be replaced with _
        outputFile = outputFile.replaceAll("=", "_");
        outputFile = outputFile.replaceAll("/", "I");
        outputFile = outputFile.replaceAll("[()]", "C");
    	outputFile = outputFile.substring(0, 4) + "_" + outputFile.substring(4);	
        
        char[] outputChar = outputFile.toCharArray();
        for (int i = 0; i < outputChar.length; i++) {
            if (outputChar[i] == '*') 
                outputChar[i] = 'T';
        }

        /*
        int index = outputFile.indexOf("=");
        if(index != -1){
            outputFile = outputFile.substring(0, index) + "_" 
            + outputFile.substring(index + 1);
        }

        // / would be replaced with I
        index = outputFile.indexOf('/');
        if(index != -1){
            outputFile = outputFile.substring(0, index) + "I" 
            + outputFile.substring(index + 1);
        }

        // ( ) would be replaced with C C
        index = outputFile.indexOf('(');
        if(index != -1){
            outputFile = outputFile.substring(0, index) + "C" 
            + outputFile.substring(index + 1);
        }

        index = outputFile.indexOf(')');
        if(index != -1){
            outputFile = outputFile.substring(0, index) + "C" 
            + outputFile.substring(index + 1);
        }
        */
        
        return new String(outputChar);
    }
    
    public void testFunc(String fileName, String workingDir) throws IOException {

        setWorkingDir(workingDir);
        
        try{
            resetNumBrdTransactions();
            resetNumBrdFiles();
            resetNumBrokenBrd();
            resetNumStudents();
            
            printBRD(fileName);
            
            trace.out(getNumBrdTransactions() + " BRD transactions read.");
            trace.out(getNumBrdFiles() + " BRD files generated for " + getNumStudents() + " students.");
            trace.out(getNumBrokenBrd() + " potential broken BRD files.");
            
            //ArrayList testList = getStudentActions("Sim_data_1.txt");

        }catch(SAXException e){

            String message = getClass().getName() + " : "
            + "testFunc : "
            + e.getMessage();
            throw new IOException(message);
        }
    }
    
    public static void main(String[] args) {
        
        SimStudentBRDWriter ssBRDTest = new SimStudentBRDWriter();
        
        if (args.length != 2) {
            trace.out("Usage: SimStBRDWriter <input_file> <output_dir>");
            trace.out("Specify both the input file to read and a directory where the files would be saved.");
            System.exit(-1);
        }
        
       	try{
       	    ssBRDTest.testFunc(args[0], args[1]);
       	}catch(IOException e){
       	    e.printStackTrace();
       	}
    }
    
    /** Internal class to hold a transaction information of a student on a problem */
    public static class StudentTransactionItems{
        protected String studentID;
        protected String getStudentID(){return studentID;}
        protected String probName;
        protected String getProblemName(){return probName;}
        protected String selection;
        protected String action;
        protected String input;
        protected String subskillName;
        protected String outcome;

        public StudentTransactionItems(String studentID, String probName, String selection,
                String action, String input, String subskillName, String outcome){
        	
            this.studentID = studentID;
            this.probName = probName;
            this.selection = selection;
            this.action = action;
            this.input = input;
            this.subskillName = subskillName;
            this.outcome = outcome;
        }

       public  boolean validBrdTransaction() {
            return (subskillName.equalsIgnoreCase("hint") || 
                    (selection.matches("commTable1_C.R.") &&
                    		input != null &&
                            subskillName != null ) );
        }
       
     
       
    }
    
    private static class NodeEdgePairs{
        ArrayList nodeList;
        ArrayList edgeList;

        public NodeEdgePairs(ArrayList nodeList, ArrayList edgeList){
            this.nodeList = nodeList;
            this.edgeList = edgeList;
        }
    }
}

//private ArrayList getStudentActions(String inputFile) throws IOException {
//
//    ArrayList transactionList = new ArrayList();
//
//    BufferedReader in = new BufferedReader(new FileReader(inputFile));
//    String fileLine = null;
//
//    in.readLine(); // header of the file
//    while((fileLine = in.readLine()) != null){
//        
//        String[] tokens = fileLine.split("\t");
//        /*
//        0. id  
//        1. student_name    
//        2. problem_name    
//        3. selection       
//        4. action  
//        5. input   
//        6. skill   
//        7. outcome
//        */
//
//        String student = tokens[0];
//        String problem = tokens[1];
//        String selection = tokens[2];
//        String action = tokens[3];
//        String input = tokens[4];
//        String skill = tokens[5];
//        String outcome = tokens[6];
//
//        StudentTransactionItems item = 
//            new StudentTransactionItems(student, problem, selection, action, input, skill, outcome);
//
//        transactionList.add(item);
//    }
//
//    //trace.out("action list size = " + transactionList.size());
//    return transactionList;
//}
//

//private ArrayList problemList(ArrayList transactionList){
//    ArrayList problemList = new ArrayList();
//    StudentTransactionItems item = (StudentTransactionItems)transactionList.get(0);
//    String probName = item.probName;
//    problemList.add(probName);
//
//    for(int i=1; i < transactionList.size(); i++){
//        item = (StudentTransactionItems)transactionList.get(i);
//        String prob = item.probName;
//        if(! prob.equalsIgnoreCase(probName)){
//            problemList.add(prob);
//            probName = prob;
//        }
//    }
//    return problemList;
//}
