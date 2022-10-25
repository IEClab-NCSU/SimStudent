package basilica2.side.listeners;
import java.io.IOException;
// import java.util.List;
import java.util.*;
import java.util.Scanner;
import basilica2.agents.components.InputCoordinator;
import basilica2.agents.events.MessageEvent;
import basilica2.agents.listeners.BasilicaAdapter;
import edu.cmu.cs.lti.basilica2.core.Agent;
import edu.cmu.cs.lti.basilica2.core.Event;
import basilica2.side.util.MultipartUtility;


public class LightSideMessageAnnotator
{
	String pathToModel; 
	String modelName; 
	String modelNickname;
	String predictionCommand; 
	String classificationString; 
	
	String host = "http://localhost:8000";
    String charset = "UTF-8";
    MultipartUtility mUtil; 
    Hashtable<String, Double> classify_dict = new Hashtable<String, Double>();
	
	public LightSideMessageAnnotator()
	{
		//super(a);
		/* Actual code by bazaar
		 * 
		 * 
		 * pathToModel = getProperties().getProperty("pathToModel", pathToModel);
		modelName = getProperties().getProperty("modelName", modelName);        
		modelNickname = getProperties().getProperty("modelNickname", modelNickname);
		predictionCommand = getProperties().getProperty("predictionCommand", predictionCommand);
		classificationString = getProperties().getProperty("classifications", classificationString);*/
		
		/*
		 * Added values of these variables (1) by Tasmia 
		 * that worked in her laptop perfectly.
		 * Hardcoded this part at first to  have a quick start on what is going on inside.
		 * 
		pathToModel = "⁨Users⁩/tasmiashahriar⁩/Downloads⁩/⁨bazaar/lightside/models/pos.model.xml";
		modelName = "pos.model.xml";        
		modelNickname = "pos-model";
		predictionCommand = "⁨Users⁩/tasmiashahriar⁩/Downloads⁩/⁨bazaar/lightside/scripts/prediction_server.sh";
		classificationString = "detected,30,notdetected,30";
		String[] classificationList = classificationString.split(","); 
		int listLength = classificationList.length; 
		for (int i=0; i<listLength; i+=2) {
			classify_dict.put(classificationList[i],Double.parseDouble(classificationList[i+1]));
		}*/
		
		/*
		 * Added values of these variables (2) by Tasmia
		 * 
		 * pathToModel = "⁨Users⁩/tasmiashahriar⁩/Downloads⁩/⁨bazaar/lightside/models/student_response.model.xml";
		modelName = "student_response.model.xml";        
		modelNickname = "student_response-model";
		predictionCommand = "⁨Users⁩/tasmiashahriar⁩/Downloads⁩/⁨bazaar/lightside/scripts/prediction_server.sh";
		classificationString = "N1,70,N2,70,N4,70,N7,70";*/
		
		
		pathToModel = "⁨Users⁩/tasmiashahriar/Documents/GitHub/SimStudent/⁨bazaar/lightside/models/unfamiliar_problem.model.xml";
		modelName = "unfamiliar_problem.model.xml";        
		modelNickname = "unfamiliar_problem-model";
		predictionCommand = "⁨Users⁩/tasmiashahriar/Documents/GitHub/SimStudent/⁨bazaar/lightside/scripts/prediction_server.sh";
		classificationString ="UP1,70,UP2,70,UP3,70,UP4,70,ELSE,70,AGREED,70,DISAGREED,70";
		String[] classificationList = classificationString.split(","); 
		int listLength = classificationList.length; 
		for (int i=0; i<listLength; i+=2) {
			classify_dict.put(classificationList[i],Double.parseDouble(classificationList[i+1]));
		}
		
	}
	
	public LightSideMessageAnnotator(String pathToModel,String modelName,String modelNickname,String predictionCommand,String classificationString)
	{
		/* Added constructor by Tasmia
		 * 
		 * This constructor is called from TuTalk Scenario.java file (Line 180) 
		 * All the values of the class variables are updated by the ones given inside
		 * the <configuration> block of the scenario xml files.
		 * 
		 * 
		 * */
		
		this.pathToModel = pathToModel;
		this.modelName = modelName;        
		this.modelNickname = modelNickname;
		this.predictionCommand = predictionCommand;
		this.classificationString = classificationString;
		String[] classificationList = classificationString.split(","); 
		int listLength = classificationList.length; 
		for (int i=0; i<listLength; i+=2) {
			classify_dict.put(classificationList[i],Double.parseDouble(classificationList[i+1]));
		}
		
	}

	
	
	/**
	 * @param source
	 *            the InputCoordinator - to push new events to. (Modified events
	 *            don't need to be re-pushed).
	 * @param event
	 *            an incoming event which matches one of this preprocessor's
	 *            advertised classes (see getPreprocessorEventClasses)
	 * 
	 *            Preprocess an incoming event, by modifying this event or
	 *            creating a new event in response. All original and new events
	 *            will be passed by the InputCoordinator to the second-stage
	 *            Reactors ("BasilicaListener" instances).
	 */
	public String annotateText(String text)
	{
		String path = "models/";

		try {
			MultipartUtility mUtil = new MultipartUtility(host+"/evaluate/" + modelName, charset);
            mUtil.addFormField("sample", text);
            mUtil.addFormField("model", path + modelName );
            List<String> finish = mUtil.finish();
            StringBuilder response = new StringBuilder();
            for (String line : finish) {
                response.append(line);
                response.append('\r');
            }
            //System.out.println("LightSide response: "+ response.toString());
            String classifications = parseLightSideResponse(response);
            return classifications; 
	    } catch (IOException e) {
	    	e.printStackTrace();
	    	return "LightSide returned null"; 
	    }	
	}
	
	public String parseLightSideResponse(StringBuilder response)
	{
		String startFlag = "<h3>";
		String endFlag = "</h3>";
		String classSplit = "%<br>";
		String withinClassSplit = ": ";
		String[] classificationSpec;
		String classification; 
		Double classificationPercent; 
		Double classificationThreshold;
		StringBuilder annotation = new StringBuilder(""); 
		String plus = ""; 
		String final_prediction = "";
		Double final_percent = 0.0;
		
		int start = response.indexOf(startFlag);
		int end = response.indexOf(endFlag,start);
		String classifications = response.substring((start+4),end);
		String[] classificationList = classifications.split(classSplit); 
		double highest_percent = 0.0;
		String predicted_label = "";
		int listLength = classificationList.length; 
		for (int i=0; i < listLength; i++) {
			classificationSpec = classificationList[i].split(withinClassSplit);
			classification = classificationSpec[0];
			classificationPercent = Double.parseDouble(classificationSpec[1]);
			final_percent = classificationPercent/100.0;
			final_prediction+= classification.toUpperCase()+"-"+final_percent+",";
			try {
				classificationThreshold = classify_dict.get(classification);
				if (classificationPercent >= classificationThreshold) {
					highest_percent = classificationPercent/100.0;
					predicted_label = classification.toUpperCase();
					annotation.append(plus + classification.toUpperCase());
					plus = "+"; 					
				}
			}
			catch (Exception e) {
		    	System.out.println("LightSide classification \"" + classification + "\" not used"); 
			}			
		}
		//return annotation.toString(); // Actual Bazaar returns this.
		// Added by Tasmia.
		// I have returned this instead with all labels+"-"+"prediction",labels+"-"+"prediction", ... format.
		return final_prediction;
	}

	/**
	 * @return the classes of events that this Preprocessor cares about
	 */
	/*@Override
	public Class[] getPreprocessorEventClasses()
	{
		// only MessageEvents will be delivered to this watcher.
		return new Class[] { MessageEvent.class };
	}*/

	/*public static void main(String[] args)
	{
		Scanner input = new Scanner(System.in);
		LightSideMessageAnnotator annotator = new LightSideMessageAnnotator(null);

		while (input.hasNext())
		{
			String text = input.nextLine();
			String label = annotator.annotateText(text);
			System.out.println("Label is " + label);
		}
		input.close();
	}*/

	/*@Override
	public Class[] getListenerEventClasses()
	{
		// no processing events.
		return new Class[]{};
	}*/

	/*@Override
	public void processEvent(InputCoordinator arg0, Event arg1)
	{
		//we do nothing
	}*/
	

}
