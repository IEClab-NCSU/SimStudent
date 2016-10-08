package edu.cmu.pact.miss;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.cmu.cs.lti.tutalk.script.Concept;
import edu.cmu.cs.lti.tutalk.script.Response;
import edu.cmu.cs.lti.tutalk.script.Scenario;
import edu.cmu.cs.lti.tutalk.slim.FuzzyTurnEvaluator;
import edu.cmu.cs.lti.tutalk.slim.TuTalkAutomata;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;

public class SimStTutalk {

    public static final int TUTALK_STATE_INITIALIZING = 0;
    public static final int TUTALK_STATE_EXCEPTION = -1;
    public static final int TUTALK_STATE_DIALOG = 1;
    public static final int TUTALK_STATE_DONE = 4;
    
    public static final String FINAL_OKAY = "Okay.";
    
    public boolean connect(String pScenario, SimStTutalkContextVariables contextVariables, String type) {
	
		int i, j;
		tutalkState = TUTALK_STATE_DIALOG;
		String scFilename = pScenario + ".xml";
		
		File scFile=new File(scFilename);
		boolean scExists = scFile.exists();
		if (!scExists) {
		    if(trace.getDebugCode("sstt"))trace.out("sstt", "Oooops! The scenario " + scFilename + " isn't here. Nevermind!");
		    tutalkState = TUTALK_STATE_DONE;
		    return false;
		}
	
		sc = Scenario.loadScenario(scFilename);
		
	        ttClient = new TuTalkAutomata("SimStudent", "InteractiveLearning");
	        ttClient.setScenario(sc);
	        ttClient.setEvaluator(new FuzzyTurnEvaluator());
	
		for(i=0;i<contextVariables.size();i++) {
			ttClient.addReplacementVariable(contextVariables.getNthVarName(i), contextVariables.getNthVarValue(i));
		}
	        
	        List<String> turns = ttClient.start();
	        
	        while (true) {
		    String finalQuestion = "";
		    
	            for (i = 0; i < turns.size(); i++) {
			if(trace.getDebugCode("sstt"))trace.out("sstt", "\tResponse: " + turns.get(i));
			// Do not add a new line for the first question.
	                finalQuestion = ((i==0) ? "" : finalQuestion + "\n") + turns.get(i);
	            }
	            trace.out("sstt", "Current Concept: "+ttClient.getLastConcept().getLabel());
	            if(confusionStates.contains(ttClient.getLastConcept().getLabel()))
	            {
	            	if(interactiveActivity.getSimSt().getMissController().getSimStPLE() != null ) 
	            	{
	            		interactiveActivity.getSimSt().getMissController().getSimStPLE().setAvatarConfused(true);
	            	}
	            }
	            List<Response> expected = ttClient.getState().getExpected();
	            if (expected.size() == 0) {
			if(trace.getDebugCode("sstt"))trace.out("sstt", "\tExpectedSize = 0; gonna break out!");
					// Need to display the "bye, thank you" message.
					interactiveActivity.getSimSt().displayMessage("",finalQuestion);
	                break;
	            }
	            
	            if(finalQuestion.length() == 0)
	            {
	            	if(trace.getDebugCode("sstt"))trace.out("sstt", "\tQuestion length = 0; gonna break out!");
					// Need to display the "bye, thank you" message.
					interactiveActivity.getSimSt().displayMessage("",FINAL_OKAY);
	                break;
	            }
	
	            /*for (i = 0; i < expected.size(); i++) {
	                List<String> phrases = expected.get(i).getConcept().getPhrases();
	                for (j = 0; j < phrases.size(); j++) {
			    if(trace.getDebugCode("sstt"))trace.out("sstt", "\t\tValid answer: " + phrases.get(j));
	                }
	            }*/
	            
	            List<Concept> matchingConcepts = new ArrayList<Concept>();
	            String input = "";
	            while (matchingConcepts.size() == 0) {
	                //Get Input
	                input = getAnswer(finalQuestion, type);
	                
		            if(interactiveActivity.getSimSt().getMissController().isPLEon())
		            {
		            	interactiveActivity.getSimSt().getMissController().getSimStPLE().setAvatarThinking();
		            }
		            
	                //System.out.println("Evaluating: " + input.trim());
	                matchingConcepts = ttClient.evaluateTuteeTurn(input);
	            }
	            	            
	            //System.out.println("Matched Concept " + matchingConcepts.get(0).getLabel());
	            turns = ttClient.progress(matchingConcepts.get(0));
                
				interactiveActivity.getLogger().simStLog( SimStLogger.SIM_STUDENT_EXPLANATION, SimStLogger.EXPLANATION_CATEGORIZE_ACTION,
						problemName, ttClient.getLastConcept().getLabel(), finalQuestion, 0, input);
	            
				if(!type.endsWith(SimStLogger.FOLLOW_UP_EXPLAIN_SUFFIX))
					type += SimStLogger.FOLLOW_UP_EXPLAIN_SUFFIX;
	        }
	
	        // Need to check if we need to see if we need to ask about other concepts as well
		while (!queuedConcepts.isEmpty()) {
		    SimStTutalkContextVariables localContextVariables = new SimStTutalkContextVariables();
		    String theConcept = queuedConcepts.remove(0);
		    localContextVariables.addVariable("%concept%", theConcept);
		    connect("what_concept", localContextVariables, "concept");
		}
	
		tutalkState = TUTALK_STATE_DONE;
		
		return true;
    }

    private void updatePathValue(String filepath) {

		long startTime = System.currentTimeMillis();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filepath);
			
			Node company = doc.getFirstChild();
			Node configuration = doc.getElementsByTagName("configuration").item(0);
			if(configuration != null) {
				NodeList moduleNodes = ((Element)configuration).getElementsByTagName("module");
				if(moduleNodes != null && moduleNodes.getLength() > 0) {
					for(int i=0; i < moduleNodes.getLength(); i++) {
						Element module = (Element)moduleNodes.item(i);
						if(module.getAttribute("kind").equals("model")) {
							NodeList params = module.getElementsByTagName("param");
							if(params != null) {
								for(int j=0; j < params.getLength(); j++) {
									Element param = (Element)params.item(j);
									if(param.getAttribute("key").equals("path")) {
										Node nodeAttr = param.getAttributes().getNamedItem("value");
										String text = nodeAttr.getTextContent();
										if(!text.contains(WebStartFileDownloader.SimStWebStartDir)) {
											text = text.replace("./", "");
											nodeAttr.setTextContent(WebStartFileDownloader.SimStWebStartDir+text);
										}
									}
								}
							}
						}
					}
				}
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filepath));
			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	/* Sets up SimStTutalk */
    public SimStTutalk(String pTutalkUid, SimStInteractiveLearning pInteractiveActivity) {
		interactiveActivity = pInteractiveActivity;
		confusionStates = new LinkedList<String>();
		confusionStates.add("response-simst-undo");
		confusionStates.add("N5-response-simst");
    }

    /* Sets up the problem name */
    public void setProblemName(String pProblemName) {
    	problemName = pProblemName;
    }

    public void initialize() {
	// Get the interactive learning instance's descriptions
		describableFeatures = (interactiveActivity.getSimSt()).getAllFeatureDescriptions();
	
		// Debug code
		for(int i=0;i<describableFeatures.size();i++) {
	            System.out.println("sstt: Description #"+i+": "+(describableFeatures.get(i)).getFeatureName() + " -> " + (describableFeatures.get(i)).getDescriptions());
        }

        // Get all unlearned concepts
        curriculumConcepts =  (interactiveActivity.getSimSt()).getUnlearnedConcepts();
    }

    public int getState() {
    	return tutalkState;
    }

    public void insertFeatureDescription(Describable featureDesc) {
    	if(trace.getDebugCode("sstt")) trace.out("sstt", "Got a feature description for " + featureDesc.getFeatureName());
    }

    // End TuTalk Handlers
    // ---------------------

    /* Poses a freeform question. Students are free to enter any answer. */
    public String getAnswer(String question, String type) {
		if(trace.getDebugCode("sstt"))trace.out("sstt", "Got question: " + question);
		
		String explanation = "";
		//Explanation duration includes time from prompt to explanation received
		long explainRequestTime = Calendar.getInstance().getTimeInMillis();
	
		if(interactiveActivity.getSimSt().getMissController().getSimStPLE() == null ) {
		    //If no PLE, support selfExplanation through JOptionPanes
		    explanation = JOptionPane.showInputDialog(null,
			question,
			"Please Provide an Explanation",
			JOptionPane.PLAIN_MESSAGE );
		} else {
			explanation = interactiveActivity.getSimSt().getMissController().getSimStPLE().giveMessageFreeTextResponse(question);
		}
	
		int explainDuration = (int) (Calendar.getInstance().getTimeInMillis() - explainRequestTime);
	
		if(trace.getDebugCode("sstt"))trace.out("sstt", "Got answer, submitting: " + explanation);
	
		if(explanation != null && explanation.length() > 0) {
			//Log non-empty explanation
			interactiveActivity.getLogger().simStLog( SimStLogger.SIM_STUDENT_EXPLANATION, type,
					problemName, explanation, question,explainDuration,question);
	
			// See if it matches anything we want to know about.
			for(int i=0;i<curriculumConcepts.size();i++) {
			    if (explanation.indexOf(curriculumConcepts.get(i)) > -1) {
				// Matched a concept in the curriculum
				// We should ask about it.
				if(trace.getDebugCode("sstt"))trace.out("sstt", "Need to acquire knowledge about: " + curriculumConcepts.get(i));
				queuedConcepts.add(curriculumConcepts.get(i));
			    }
			}
		} else {
			//Log empty explanation as no explanation given - specify requested explanation as problem choice
			interactiveActivity.getLogger().simStLog( SimStLogger.SIM_STUDENT_EXPLANATION, type,
					problemName, SimStLogger.NO_EXPLAIN_ACTION, question, explainDuration,question);
			explanation = "No answer given";
		}
	
		return explanation;
    }

    private int tutalkState = TUTALK_STATE_INITIALIZING;

    // Debug mode for TuTalk
    private boolean tutalkDebug = false;

    // Problem name
    private String problemName = "Undefined";

    private Scenario sc;
    // The actual tutalk automata
    private TuTalkAutomata ttClient;

    private SimStInteractiveLearning interactiveActivity;
    
    private List<String> confusionStates;
    
    // Storage to hold all describable Features;
    private Vector<Describable> describableFeatures;



    // Storage to hold the concepts that SimSt is curious about
    // Will be asked eventually
    private Vector<String> queuedConcepts = new Vector();
    
    private Vector<String> curriculumConcepts;


    

    // ------------------------------------------------------------------------
    // End TuTalk Integration
    // ------------------------------------------------------------------------
}
