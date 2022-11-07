package edu.cmu.pact.miss.BrdAnalyzer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


/**
 * This program creates an Excel-readable report of steps that appear on BRD files.
 * 
 * first argument: the path to the BRD file or the directory containing BRD files
 * second argument: the path to output file
 */

public class StepAnalysis {

    String logOutputFile;

	public String getLogOutputFile() {
        return logOutputFile;
    }

    public void setLogOutputFile(String logOutputFile) {
        this.logOutputFile = logOutputFile;
    }

    PrintStream outP; // declare a print stream object
    boolean isBadBrd = false;
    String buffer = null;

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    public StepAnalysis(String logOutputFile) {
    	setLogOutputFile(logOutputFile);
	}

    public class EdgeProps {
        
        String sourceID;        
        String stateName;
        String selectionStr;
        String actionStr;
        String inputStr;
        String ruleName; 
        String correctness;
        
        public EdgeProps(Element edge, Vector nodes) {
            
            try{
                sourceID = edge.getChild("sourceID").getValue();
            }
            catch(Exception e){ handleBadBrdException(e); }  //edge refers to non-existent node

            try{
                stateName = getStateName(nodes, sourceID); //look up the state-name for sourceID
            }
            catch(Exception e){ handleBadBrdException(e); }

            Element actionLabel = null;
            try{ actionLabel = edge.getChild("actionLabel"); }
            catch(Exception e){ handleBadBrdException(e); }

            Element properties = null;
            try{ properties = actionLabel.getChild("message").getChild("properties"); }
            catch(Exception e){ handleBadBrdException(e); }

            try{
                selectionStr = properties.getChild("Selection").getChild("value").getValue();
                actionStr = properties.getChild("Action").getChild("value").getValue();
                inputStr = properties.getChild("Input").getChild("value").getValue();
            }
            catch(Exception e){ handleBadBrdException(e); }
            
            Element rule = edge.getChild("rule");
            ruleName = rule.getChild("text").getValue();

            try{
                correctness = actionLabel.getChild("actionType").getValue();
            }
            catch(Exception e){ handleBadBrdException(e); }
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public void analyzeStepsForBrd(String brdPath){

    	//initializing
    	isBadBrd = false;
    	buffer = "";
    	String prevSourceID = null;
    	int numAttempts=-100000; //how many previous edges have the same parent ID.
    	//	int buggyEdgeCount = 0;

    	String studentName = studentName(brdPath);
    	String brdName = lastToken(brdPath);

    	Vector nodes = new Vector();
    	Vector edges = new Vector();
    	gatherNodeAndEdge(brdPath, nodes, edges);

    	for (int i=0; i<edges.size() && !isBadBrd; i++){

    		Element edge = (Element) edges.get(i);
    		EdgeProps ep = new EdgeProps(edge, nodes);

    		//	    if (correctness!=null && correctness.equals("Buggy Action")){
    		//	        buggyEdgeCount++;
    		//	        trace.out("Buggy step ID = " + actionLabel.getChild("uniqueID").getValue());
    		//	    }

    		// this code is assuming that sourceID never decreases
    		if (ep.sourceID.equals(prevSourceID)) 
    			numAttempts++;
    		else
    			numAttempts=1;
    		prevSourceID = ep.sourceID;

    		printLineToBuffer(studentName, brdName, ep.stateName, ep.selectionStr, ep.actionStr, ep.inputStr, 
    				ep.ruleName, ep.correctness, numAttempts);
    	}

    	if (!isBadBrd)
    		printBufferToFile();
    	else
    		System.err.println("The file " + brdPath + " is a bad BRD, and was not printed to the log.");
    }

    //student's name is the name of the directory
    private String studentName(String brdPath) {
        
        int lastSlash = java.lang.Math.max(brdPath.lastIndexOf("/"), brdPath.lastIndexOf("\\"));	
        String studentPath = brdPath.substring(0,lastSlash-1);
        return lastToken(studentPath);
    }
    
    private String getStateName(Vector nodes, String destID) {
        String stateName = null;

        Element node;
        for (int j=0; j<nodes.size(); j++){
            node = (Element) nodes.get(j);
            String id = node.getChild("uniqueID").getValue();
            if (id.equals(destID)){
                stateName = node.getChild("text").getValue();
                break;
            }
        }
        return stateName;
    }

    // Returns the problem name, which is shown in ProblemName slot in a StartNodeMessage element
    private String gatherNodeAndEdge(String brdPath, Vector /* Element */ nodes, Vector /* Element */ edges) {
        
        String problemName = "";
        
        Document doc = buildDocument(brdPath);

        //build up list of nodes and edges
        Element rootElement = null;
        try{
            rootElement = doc.getRootElement();
        }
        catch(Exception e){
            handleBadBrdException(e);
        }

        List els = null;
        try {
            els = rootElement.getChildren();
        }
        catch(Exception e){
            handleBadBrdException(e);
        }

        for (Iterator it = els.iterator(); it.hasNext();) {
            Element e = (Element) it.next();
            if (e.getName().equalsIgnoreCase("node"))
                nodes.add(e);
            else if (e.getName().equalsIgnoreCase("edge"))
                edges.add(e);
            else if (e.getName().equalsIgnoreCase("startNodeMessages"))
                problemName = getProblemName(e);
        }
        //trace.out("edges.size() = " + edges.size());
        
        return problemName;
    }


    private String getProblemName(Element e) {

        List messages = e.getChildren();
        
        for (int i = 0; i < messages.size(); i++) {
            
            Element message = (Element)messages.get(i);
            
            Element properties = message.getChild("properties");
            if (properties == null) continue;
            
            Element problemName = properties.getChild("ProblemName");
            if (problemName == null) continue;
            
            return problemName.getValue();
        }
        return null;
    }

    private Document buildDocument(String brdPath) {

        //load all steps
        SAXBuilder builder = new SAXBuilder();

        Document doc = null;
        try{
            doc = builder.build(brdPath);
        }
        catch(Exception e){
            handleBadBrdException(e);
            //trace.out("error parsing XML file");
        }
        return doc;
    }
    
    //prints this as a line in the log file
    public void printLineToBuffer(String studentName, String brdName, String stateName,
            String selection, String action, String input, String ruleName, String correctness, int numAttempts){

        if (input.charAt(0) == '-') input = "\"" + input + "\"";
        
        String logStr = studentName + "\t"
        + brdName + "\t"
        + stateName + "\t"
        + selection + "\t"
        + action + "\t"
        + input + "\t"
        + ruleName + "\t"
        + correctness + "\t"
        + ""+numAttempts + "\n";
        
        buffer+=logStr;
    }

    private void printBufferToFile() {
	outP.print(buffer);
    }

    private void handleBadBrdException(Exception e) {
	isBadBrd = true;
	e.printStackTrace();
    }

    // For a mixed-slash path, we need to do 2 splits.
    // Better way: find a regular expression that matches either '/' or ' \'.    
    private String lastToken(String path) {
        
	String[] backslashTokens = path.split("\\\\"); //i.e. split on "\"
	int size = backslashTokens.length;
	String[] forwardslashTokens = backslashTokens[size-1].split("/");
	size = forwardslashTokens.length;
	return forwardslashTokens[size-1];
    }

    public void analyzeStepsForSubDir(String input){
	
	File f = new File(input);
	String brdFiles[] = f.list();
	for (int i = 0; i < brdFiles.length; i++) { //call analyzeStepsForBrd for each BRD in the directory
	    String childFileName = brdFiles[i];
	    File childFile = new File(f, childFileName);
	    String childFilePath = childFile.getAbsolutePath();
	    if (childFile.isDirectory())
		analyzeStepsForSubDir(childFilePath); 	                     	                
	    if (childFileName.matches(".*brd$")){
		analyzeStepsForBrd(childFilePath);
	    }
	}
    }

    //this code could reuse the code for analyzeStepsForSubDir
    public void analyzeStepsForTopDir(String input){
        try {
            // Create a new file output stream connected to output
            FileOutputStream out = new FileOutputStream(getLogOutputFile());
            // Connect print stream to the output stream
            outP = new PrintStream( out );

            String header="student_name\tbrd_name\tstate_name\tselection\taction\tinput\trule_name\tcorrectness\tnum_attempts\n";
            outP.print(header);

            File f = new File(input);

            if (f.isDirectory()){

                String brdFiles[] = f.list();
                for (int i = 0; i < brdFiles.length; i++) { //call analyzeStepsForBrd for each BRD in the directory
                    String childFileName = brdFiles[i];
                    File childFile = new File(f, childFileName);
                    String childFilePath = childFile.getAbsolutePath();
                    if (childFile.isDirectory())
                        analyzeStepsForSubDir(childFilePath); 	                     	                
                    if (childFileName.matches(".*brd$")){
                        analyzeStepsForBrd(childFilePath);
                    }
                }
                outP.close();
            }
            else if (input.matches(".*brd$")){
                analyzeStepsForBrd(input);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // Main method
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public static void main(final String[] args) {
        
    	StepAnalysis stepAnalysis = new StepAnalysis(args[1]);
    	stepAnalysis.analyzeStepsForTopDir(args[0]);
    }
}
