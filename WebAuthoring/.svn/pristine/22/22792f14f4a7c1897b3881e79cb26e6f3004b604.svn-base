import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Gatekeeper extends Backend{

	HashMap<String, HashMap<String, HashMap<String, Double[]>>> dict; 		//stores Student > Lesson/KC Model > KC > (L,G,S,T)
	HashMap<String, ArrayList<String>> KCmodels = new HashMap<String, ArrayList<String>>(); //CAN GET FROM THE PRODUCTION RULES
	String INIT;
	String TYPES;
	String PRODUCTION;
	
	public Gatekeeper(String init, String types, String production) {
		//INITIALIZE KCmodels WITH LIST OF KCs FOR EACH MODEL
		dict = new HashMap<String, HashMap<String, HashMap<String, Double[]>>>();
		INIT = init;
		TYPES = types;
		PRODUCTION = production;
	}


	@Override
	public void processInterfaceEvent(InterfaceEvent ie) {
		String id = ""; //SHOULD BE ATTRIBUTE OF INTERFACE EVENT
		String lesson = ""; //SHOULD BE ATTRIBUTE OF INTERFACE EVENT
		if (!dict.containsKey(id)) {										//New Student
			dict.put(id, new HashMap<String,HashMap<String,Double[]>>());
		}
		if (!dict.get(id).containsKey(lesson)) {							//New Lesson/KC Model for Student
			dict.get(id).put(lesson, new HashMap<String, Double[]>());
			for (String currSkill : KCmodels.get(lesson)) {					//Looping through each KC in model
				//TRAIN KC (EM) OR USING OTHER DATA
			}
		}
		MetaBackend newBackend = new MetaBackend(INIT, TYPES, PRODUCTION, dict.get(id).get(lesson)); 
		//NEED TO DIRECT INTERFACE TO USE newBackend
	}
	
	//NEED TO MAKE THIS FUNCTION GET CALLED WHEN STUDENTS CLOSES TUTOR
	public void processFinalEvent(InterfaceEvent ie) {
		Map<String, Double[]> vals = null; //SENT WITH FINAL IE mapping each KC to L,G,S,T values
		String id = "";
		String lesson = "";
		for (String currSkill : KCmodels.get(lesson)) {					//Looping through each KC in model
			dict.get(id).get(lesson).get(currSkill)[0] = vals.get(currSkill)[0];
			dict.get(id).get(lesson).get(currSkill)[0] = vals.get(currSkill)[1];
			dict.get(id).get(lesson).get(currSkill)[0] = vals.get(currSkill)[2];
			dict.get(id).get(lesson).get(currSkill)[0] = vals.get(currSkill)[3];
		}
		
	}

}
