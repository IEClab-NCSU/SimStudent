/*
 * Created on Mar 10, 2005
 *
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.model.CtatModeModel;
import edu.cmu.pact.ctat.view.CtatFrame;

/**
 * @author mpschnei
 * 
 * Created on: Mar 10, 2005
 * 
 * Holds methods that read BRD files.
 * 
 */
public class ProblemStateReader {

	private BR_Controller controller;

    //psrJDom object 
    private ProblemStateReaderJDom psrJDom;

    /** Contents of the last brd file read by {@link #openBRDiagramFile(String)}. */
	private byte[] savedImage = null;

    /** The current version of the graph file format. */
	public static final String CURRENT_BRD_VERSION = "2.1";

	/** Attribute name of BRD version attribute in root element. */
	public static final String VERSION_ATTR = "version";

	public ProblemStateReader(BR_Controller controller) {
        this.controller = controller;
    }
    
    
    // CurriculumProblem.setProblemRules(Vector problemRules)
    public Vector<RuleProduction> loadProblemRules(String problemFullName) {
        Vector<RuleProduction> problemRules = new Vector<RuleProduction>();

        try {
            DefaultHandler handler = new ProblemRuleParser(problemRules);
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(new File(problemFullName), handler);
        } catch (java.io.IOException e) {
            trace.err("Error reading file: " + problemFullName);
            return null;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (FactoryConfigurationError e) {
            e.printStackTrace();
            return null;
        }

        return problemRules;
    }

    /**
     * Finish initialization after reading a graph.
     * @param authorMode
     */
    private void doConclusionPart(String authorMode) {

//      initializePseudoTutor() sends start state messages            
//  	controller.sendCommMsgs(problemModel.getStartNode(), problemModel.getStartNode());
//    	controller.getPseudoTutorMessageHandler().initializePseudoTutor();

    	getProblemModel().setStartNodeCreatedFlag(true);
        if (authorMode != null && !Utils.isRuntime())  // author mode fixed as TestTutor at runtime
        	controller.getCtatModeModel().setAuthorMode(authorMode);

        Enumeration<ProblemEdge> enumeration = getProblemModel().getProblemGraph().edges();
        while(enumeration.hasMoreElements()){
        	ProblemEdge temp = enumeration.nextElement();
        	temp.getEdgeData().updateToolTip();
        }
        // update linksGroups edge tooltips
       /* Vector singleGroup;
        ProblemEdge tempedge;
        EdgeData tempMyEdge;
        for (int i = 0; i < problemModel.getLinksGroups().size(); i++) {
            singleGroup = (Vector) problemModel.getLinksGroups()
                    .elementAt(i);
            // trace this group edges
            for (int j = 1; j < singleGroup.size(); j++) {
                tempedge = (ProblemEdge) singleGroup.elementAt(j);
                tempMyEdge = tempedge.getEdgeData();
                tempMyEdge.getActionLabel().updateToolTip();
            }
        }*/

        // send start state msgs to Lisp
//        for (int i = 0; i < getProblemModel().getStartNodeMessageVector()
//                .size(); i++) {
//            controller.getUniversalToolProxy().sendProperty(
//                    (MessageObject) (getProblemModel().getStartNodeMessageVector().get(i)));
//        }

    }

    /**
     * Common entry point to load a BRD file. If no student interface
     * is loaded, calls {@link BR_Controller#setBRDDirectory(String)}.
     * @param filename path to load
     */
    public boolean openBRDiagramFile(String filename) {
        if (filename == null || filename.length() <= 0)
            return false;

        ProblemModelEvent event = new NewProblemEvent(controller,
        		controller.getPreferencesModel().getBooleanValue(BR_Controller.COMMUTATIVITY));
        getProblemModel().fireProblemModelEvent(event);

        InputStream is = null;
    	File f = null;
    	String absolutePath = null;
        String title = "Error loading graph file";  // for exceptions
        URL url = null;

        try {
            trace.out("br", "READ FILE WITH JDOM: filename " + filename);
            url = new URL(filename);
            trace.out("br", "READ FILE WITH JDOM: url " + url);
            URLConnection conn = url.openConnection();
            trace.out("br", "READ FILE WITH JDOM: conn " + conn);
            is = conn.getInputStream();
            trace.out("br", "READ FILE WITH JDOM: is " + is);
            absolutePath = filename;
        } catch(Exception e) {
        	//trace.err("Error opening "+filename+" or reading as URL: "+e+"; cause "+e.getCause());
        	//trace.errStack("Error reading URL: "+e, e);
            try {
                f = Utils.getFileAsResource(url);
                absolutePath = f.getCanonicalPath();
                is = new FileInputStream(f);
            } catch (Exception d) {
            	//trace.err("Error on getFileAsResource("+url+") or getCanonicalPath(): "+ d+"; cause "+d.getCause());
            	absolutePath = filename; // if getCanonicalPath fails because we're
            							 // reading a .brd from a jar, then nasty but ok
            							 // since we'll never save the .brd in student use
            	try {
            		f = new File(filename);
            		is = new FileInputStream(f);
            		absolutePath = f.getCanonicalPath();
            	} catch (Exception e2) {
                	trace.errStack("Error on new File("+filename+") or getCanonicalPath(): "+e2, e2);
					if(trace.getDebugCode("mg"))
						trace.out("mg", "ProblemStateReader (openBRDDiagramFile): Exception "+e2);
            		String message = "<html>Error opening file " + filename + ":<br/>"+
            				e2+(e2.getCause() == null ? "" : ".<br/>Cause: "+e2.getCause());
            		Utils.showExceptionOccuredDialog(null, message, title);
            		controller.getProblemModel().reset("", "");
            		return false; // table left empty if bad file
            	}
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(64*1024); 
        long count = 0;
        try {
//        	BufferedInputStream bis = new BufferedInputStream(is);
        	for (int c = -1; 0 <= (c = is.read()); baos.write(c))
        		++count;
        } catch (Exception e2) {
        	String message = "<html>Error reading file " + filename + " at " + count +
        			":<br/>"+ e2+(e2.getCause() == null ? "" : ".<br/>Cause: "+e2.getCause());
        	e2.printStackTrace();
        	Utils.showExceptionOccuredDialog(null, message, title);
        	return false; // table left empty if bad file
        }
		savedImage = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(savedImage);
		if (trace.getDebugCode("psw"))
			trace.out("psw", "PSR.openBRDiagramFile("+filename+") image length "+savedImage.length);
      	psrJDom = new ProblemStateReaderJDom(controller);
	    boolean result = psrJDom.loadBRDFileIntoMainProblemState(bais, filename, absolutePath);
        doConclusionPart(CtatModeModel.TESTING_TUTOR);
      	if (controller != null && controller.getStudentInterface() == null) {
      		if (result)
      			controller.setBRDDirectory(filename);
      	}
      	if (trace.getDebugCode("br")) trace.out("br", "+++++return from doConclusionPart");
    	return result;
    }
    
    private ProblemModel getProblemModel()
    {
    		return this.controller.getProblemModel();
    }

    
    /** ********************** UNDO TEST 1337 ***********************
     * Entry point to load a graph from a stream.
     * @param filename
     * @param authorMode authorMode to enter after loading
     */
    public boolean openBRDiagramFile1337(InputStream inputStream, String authorMode) {
    	trace.out("***1337*** OPENBRDIAGRAMFILE");
        ProblemModelEvent event = new NewProblemEvent(controller,
        		controller.getPreferencesModel().getBooleanValue(BR_Controller.COMMUTATIVITY));
        getProblemModel().fireProblemModelEvent(event);
        
    	String absolutePath = null;
      	psrJDom = new ProblemStateReaderJDom(controller);
	    boolean result = psrJDom.loadBRDFileIntoMainProblemState(inputStream, "DEFAULT", absolutePath);
	    
	    //************* NO LONGER CHANGES AUTHOR STATE 1337 ********/
        this.doConclusionPart(null);
        
    	return result;
    }
    
    
    
    /********** END: UNDO TEST 1337 ******************/

    /**
     * Returns ProblemStateReaderJDom object
     * @return
     */
    public ProblemStateReaderJDom getProblemStateReaderJDom(){
    	return psrJDom;
    }

    /** Temp file name for {@link #trySaveAndTestForChange(ProblemStateWriter, ByteArrayOutputStream)}. */
    private final File tmpFile = new File("./junk.txt");
    
    /**
     * Try to serialize the current graph to a byte buffer and check whether it has changed.
     * If {@link trace#getDebugCode("psw")} is set, then diffs the images and dumps to file
     * {@value #tmpFile}.
     * @param psw writer to use
     * @param baos buffer to write to
     * @return JOptionPane.NO_OPTION if no need to save; JOptionPane.YES_OPTION need to save
     *         JOptionPane.CANCEL_OPTION if error and should abort operation
     */
    public int trySaveAndTestForChange(ProblemStateWriter psw, ByteArrayOutputStream baos) {
        try {
        	CtatFrame dockedFrame = (controller.getCtatFrameController() == null ? null :
        		controller.getCtatFrameController().getDockedFrame());
        	String oldTTLabel = (dockedFrame == null ? null : dockedFrame.getTutorTypeLabel());
        	String errMsg = psw.saveBRDFile(baos);
        	//reset label because not really saving - epfeifer 7/19/11
        	if (oldTTLabel != null && dockedFrame != null)
        		dockedFrame.setTutorTypeLabel(oldTTLabel);
            if (errMsg != null)
            	return errorDialog(errMsg, true);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            String errMsg = e.toString() + (cause == null ? "" : "; cause "+cause.toString());
        	trace.errStack(errMsg, e);
        	return errorDialog(errMsg, true);
        }
    	if (trace.getDebugCode("psw")) trace.out("psw", "PSR.trySaveAndTest() savedImage length "+
    			(savedImage == null ? -1 : savedImage.length)+", baos length "+baos.size());
        if (savedImage != null) {
        	byte[] imageToWrite = baos.toByteArray();
        	boolean imageSame = Arrays.equals(savedImage, imageToWrite);
        	if (imageSame)
        		return JOptionPane.NO_OPTION;      // no need to save
    		if (trace.getDebugCode("psw")) {
    			try {
    				String diffLines = diff(savedImage, imageToWrite, 3);
    				tmpFile.delete();
    				FileOutputStream fos = new FileOutputStream(tmpFile);
    				fos.write(imageToWrite);
    				fos.close();
    				trace.out("psw", "PSR.trySave found difference; see "+tmpFile+":"+diffLines);
    			} catch (Exception e) {
    				trace.out("psw", "error trying to save to "+tmpFile+": "+e+
    						(e.getCause() == null ? "" : "; cause "+e.getCause()));
    			}
        	}
        }
        return JOptionPane.YES_OPTION;         // need to save
    }

    /**
     * Do a simple-minded diff of 2 byte arrays as strings.
     * @param prev first array
     * @param curr second array
     * @param maxLines maximum number of diff lines to output
     * @return diff message (multiline string)
     */
    private String diff(byte[] prev, byte[] curr, int maxLines) {
    	StringBuffer sb = new StringBuffer();
    	int len = Math.min(prev.length, curr.length);
    	if (len < prev.length)
    		sb.append("\nprev is longer; ");
    	else if (len < curr.length)
    		sb.append("\ncurr is longer; ");
    	int lineNo = 1;
		for (int i = 0; i < len; ++i) {
			if (prev[i] == '\n')
				lineNo++;
			if (prev[i] != curr[i]) {
				if (--maxLines < 0)
					break;
				int j = 0, k = 0;
				try {
					for (j = i; j >= 0 && prev[j] != '\n'; --j);
					for (k = j+1; k < len && prev[k] != '\n'; ++k);
					String prevLine = new String(Arrays.copyOfRange(prev, j+1, k));
					for (j = i; j >= 0 && curr[j] != '\n'; --j);
					for (k = j+1; k < len && curr[k] != '\n'; ++k);
					String currLine = new String(Arrays.copyOfRange(curr, j+1, k));
					sb.append("\nat line ").append(lineNo).append(":\n");
					sb.append(prevLine).append('\n').append(currLine);
					i = k;
				} catch (Exception e) {
					sb.append("\nat line ").append(lineNo).append("mismatch at byte ").append(i);
				}
			}
		}
		return sb.toString();
	}


	/**
     * Show an error dialog for {@link #trySaveAndTestForChange(ProblemStateWriter, ByteArrayOutputStream)}
     * @param errMsg description of error
     * @param getUserResponse whether to get a user response
     * @return JOptionPane.CANCEL_OPTION if should cancel operation; else JOptionPane.NO_OPTION to proceed
     */
	private int errorDialog(String errMsg, boolean getUserResponse) {
    	int result = -1;
    	if (Utils.getSuppressDialogs())  // don't show anything
    		;
    	else if (getUserResponse)
    		result = JOptionPane.showConfirmDialog(controller.getDockedFrame(),
    				errMsg+"\nDo you want to continue?",
    				"Error trying to save graph", JOptionPane.YES_NO_OPTION,
    				JOptionPane.WARNING_MESSAGE);
    	else
    		JOptionPane.showMessageDialog(controller.getDockedFrame(), 
    				"Warning: "+errMsg, "Error trying to serialize graph",
    				JOptionPane.WARNING_MESSAGE);
    	if (result == JOptionPane.YES_OPTION)
    		return JOptionPane.NO_OPTION;      // means "yes I want to continue, so don't cancel"
    	else
    		return JOptionPane.CANCEL_OPTION;
	}
	
	/**
	 * Set {@link #savedImage} from {@link ProblemStateWriter#saveBRDFile(java.io.OutputStream)}. 
	 * @param psw writer to use
	 * @return {@value JOptionPane#NO_OPTION} if no error encountered; else
	 *         {@value JOptionPane#CANCEL_OPTION} from {@link #errorDialog(String, boolean)}
	 */
	public int saveImage(ProblemStateWriter psw) {
		savedImage = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
        	String errMsg = psw.saveBRDFile(baos);
            if (errMsg != null)
            	return errorDialog(errMsg, false);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            String errMsg = e.toString() + (cause == null ? "" : "; cause "+cause.toString());
        	return errorDialog(errMsg, false);
        }
    	savedImage = baos.toByteArray();
    	if (trace.getDebugCode("psw")) trace.out("psw", "PSR.saveImage() savedImage length "+
    			(savedImage == null ? -1 : savedImage.length));
    	return JOptionPane.NO_OPTION;      // means "no error"
	}

	/**
	 * @param  new content for {@link #savedImage}
	 * @return old {@link #savedImage}
	 */
	byte[] setSavedImage(byte[] savedImage) {
		byte[] oldImage = this.savedImage;
		this.savedImage = savedImage;
		if (trace.getDebugCode("psw"))
			trace.out("psw", "PSR.setSavedImage() oldImage.length "+oldImage.length+
					", savedImage.length "+savedImage.length);
		return oldImage;
	}
}
