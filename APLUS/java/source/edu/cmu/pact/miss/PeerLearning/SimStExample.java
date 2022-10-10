package edu.cmu.pact.miss.PeerLearning;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SimSt;

public class SimStExample implements Serializable {


	/**	Default serialVersionUID */
	private static final long serialVersionUID = 1L;
	public static String QUIZ_LOCKED = "lock";
	public static String QUIZ_CORRECT = "correct";
	public static String QUIZ_INCORRECT = "incorrect";
	public static String QUIZ_INCOMPLETE = "incomplete";
	public static String QUIZ_OLD = "old";
	public static String COGTUTOR_QUIZ_NOT_TAKEN = "cogTutorNotTaken";
	public static String EXAMPLE = "example";
	
	private int index = 0;
	private int sectionNumber = 0;
	private String title = "Example";
	private String explanation = "";//"This is an example.";
	private Hashtable<String, StringPair> steps;
	private Hashtable<String, StringPair> steps_hover;
	
	// Tasmia: on paper image names
	private HashMap<String, String> on_paper_images;
	
	public static final Color CORRECT_COLOR = Color.green.darker();
	public static final Color INCORRECT_COLOR = Color.red;
	
	private String status = EXAMPLE;
	
	
	private String shortDescription;
	private Queue<String> stepOrder;
	
	/*Used in case of Aplus Control so we can resume the example solution*/
	private Map<String, Sai> quizSolutionHash;
 	public void initQuizSolutionHash(){
 		if (quizSolutionHash==null){
 			quizSolutionHash = new LinkedHashMap<String, Sai>();
 		}
 		else{
 			quizSolutionHash.clear();
 		}	
 	}
 	public Map<String, Sai> getQuizSolutionHash(){return this.quizSolutionHash;}

 	
	public SimStExample(String title, String explanation)
	{
		this.title = title;
		this.explanation = explanation;
		steps = new Hashtable<String,StringPair>();
		steps_hover = new Hashtable<String,StringPair>();
		stepOrder=new LinkedList<String>();
		initQuizSolutionHash();
	}
	
	public void addOnPaperImageNames(String selection, String name) {
		on_paper_images.put(selection, name);
	}
	
	public HashMap<String, String> getOnPaperImageNames(){
		return on_paper_images;
	} 
	public String getOnPaperImageNames(String selection){
		return on_paper_images.get(selection);
	} 
	
	public SimStExample()
	{
		steps = new Hashtable<String,StringPair>();
		steps_hover = new Hashtable<String,StringPair>();
		stepOrder=new LinkedList<String>();
		// Tasmia 
		on_paper_images = new HashMap<String, String>();
		initQuizSolutionHash();
	}
	
	SimSt simSt;
	
	public void setSimSt(SimSt simSt){
		this.simSt=simSt;
	}
	
	public SimSt getSimSt(){
		return this.simSt;
	}
	
	
	public int getIndex()
	{
		return index;
	}
	
	public void setIndex(int index)
	{
		this.index = index;
	}
	
	public int getSection()
	{
		return sectionNumber;
	}
	
	public void setSection(int section)
	{
		sectionNumber = section;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}
	
		
	public String getExplanation()
	{
		return explanation;
	}
	
	public void setExplanation(String explanation)
	{
		this.explanation = explanation;
	}
	
	public String getShortDescription()
	{
		return shortDescription;
	}
	
	public void setShortDescription(String shortDescription)
	{
		this.shortDescription = shortDescription;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public void setStatus(String status)
	{
		this.status = status;
	}
	
	public Queue<String> getStepOrder() {
		return this.stepOrder;
	}
	
	public Hashtable<String, StringPair> getSteps() {
		return this.steps;
	};
	
	public Hashtable<String, StringPair> getSteps_hover() {
		return this.steps_hover;
	};

	public void addStartStateFromProblemName(String problemName,ArrayList<String> startStateElements){
		String[] sp = problemName.split("=");
		for (int i=0;i<sp.length;i++)
			addStep(startStateElements.get(i), sp[i].trim() , "", "");
	}
	
	
	
	
	/** function that returns the next foa that is to be displayed
	 *  Logic: iterate the step order queue until you find the current
	 *  step. If you find it, return the next step (if ti exists), else 
	 *  return current step.
	 * */
	public String getNextStep4Display(String currentStep){

			Iterator it=stepOrder.iterator();
			/*find the current step*/
		 while(it.hasNext()){		 

			  String foaName= (String)it.next();
			 	if (currentStep.equals(foaName))
			 		break;
			 	
	
			 	
    }
		 String foaName;
		 /*if the next step exists, return it, else return currentStep*/
		 if (it.hasNext())
			 		foaName= (String)it.next();
		 else foaName=currentStep;
		  
		 return foaName;	
	}
	
	/**Function that returns the previous step in the step order queue.
	 * The minimum it can return is second element of the list (1st is lhs, 2nd is rhs)
	 * */
	
	public String getPreviousStep4Display(String currentStep){
		
		/*get the minimum number of foa's it can display (usually lhs and rhs)*/	
		String lowestLimit=getMinimumStep();
		
		if (currentStep.equals(lowestLimit))
			return currentStep;
		
		String previous=currentStep;
		
		Iterator it=stepOrder.iterator();
		/*find the current step*/
		while(it.hasNext()){		 
			String foaName= (String)it.next();
		 	if (currentStep.equals(foaName))
		 		break;
		 	previous=foaName;
		}
	  return previous;	
	}

	public String getLastStep(){
		String foaName="";
		Iterator it=stepOrder.iterator();
		 while(it.hasNext()){	
			 
			 	foaName= (String)it.next();
			 if (foaName.equals("done"))
				 break;
			 
		 }
		
		 return foaName;
		
	}
	
	
	public HashSet getValidSteps4display(String currentStep){	 
		 HashSet valid4display=new HashSet();
		 Iterator it=stepOrder.iterator();
	 
		 while(it.hasNext()) {		 
			  String foaName= (String)it.next();
			  valid4display.add(foaName);
			 	if (currentStep.equals(foaName))
			 		break;	 	
		 }
		 
		return valid4display;	
	}
	
	
	 public String getMinimumStep()
	    {
		 
		 
//		 String file = WebStartFileDownloader.SimStAlgebraPackage+"/"+SimStPLE.CONFIG_FILE;
		 String file = SimSt.getProjectDir() + "/" + SimStPLE.CONFIG_FILE;
	    	
//	    	ClassLoader cl = this.getClass().getClassLoader();
//	    	InputStream is = cl.getResourceAsStream(file);
//	    	InputStreamReader isr = new InputStreamReader(is);	
	    	BufferedReader reader=null;
	    
	    		
	    		
	    	ArrayList<String> startStateElements = new ArrayList<String>();
	    /*	String file = SimStPLE.CONFIG_FILE;
	    	ClassLoader cl = this.getClass().getClassLoader();
	    	InputStream is = cl.getResourceAsStream(file);
	    	InputStreamReader isr = new InputStreamReader(is);
	    	BufferedReader reader=null;*/
	    	try
	    	{
	    		//reader = new BufferedReader(new FileReader(CONFIG_FILE));
//	        	reader = new BufferedReader(isr);
	    		reader = new BufferedReader(new FileReader(file));
	    		String line = reader.readLine();
	    		
	    		while(line != null)
	    		{
	    			if(line.equals(SimStPLE.START_STATE_ELEMENTS_HEADER))
	    			{
						line = reader.readLine();
	    				while(line != null && line.length() > 0) //Blank line starts next section
	    				{
	    					startStateElements.add(line);
	    					line = reader.readLine();
	    				}
	    			}
	    			else
	    			{
	    				line = reader.readLine();
	    			}
	    		}

	    	}catch(Exception e)
	    	{
	    		
	    	}
	    	String[] startStateArray = new String[startStateElements.size()];
	    	for(int i=0;i<startStateElements.size();i++)
	    		startStateArray[i] = startStateElements.get(i);
	    	
	    	
	    	return startStateArray[startStateElements.size()-1];

	    }
	 
	 
	public boolean isExampleFilled=false;
	
	public void addStep(String selection, String input, String tooltip,String hover)
	{

		StringPair info = new StringPair(input, tooltip);
		steps.put(selection, info);
		
		StringPair info1 = new StringPair(input, hover);
		steps_hover.put(selection, info1);
		stepOrder.add(selection);
	}
	
	public void addStep(String selection, String input, String tooltip,  String hover, boolean correct)
	{
	
		isExampleFilled=true;
		
		
		StringPair info = new StringPair(input, tooltip);
		if(correct)
			info.color = CORRECT_COLOR;
		else
			info.color = INCORRECT_COLOR;
		steps.put(selection, info);
		
		StringPair info1 = new StringPair(input, hover);
		steps_hover.put(selection, info1);
		stepOrder.add(selection);
		
		
	}
	
	public void addStep(String selection, String input, String tooltip,  String hover, int correct, String on_paper_image_path)
	{
	
		isExampleFilled=true;
		
		
		StringPair info = new StringPair(input, tooltip);
		if(correct==1)
			info.color = CORRECT_COLOR;
		else if(correct==0)
			info.color = INCORRECT_COLOR;
		steps.put(selection, info);
		
		StringPair info1 = new StringPair(input, hover);
		steps_hover.put(selection, info1);
		stepOrder.add(selection);
		addOnPaperImageNames(selection.trim(), on_paper_image_path.trim());	
		
	}
	
	public String getStepInput(String selection)
	{
		StringPair input = steps.get(selection);
		if(input != null)
			return input.value;
		return "";
	}
	

	public String getStepTooltip(String selection)
	{
		StringPair input = steps.get(selection);
		if(input != null)
			return input.extended;
		return "";
	}
	
	
	public String getStepTooltipHover(String selection)
	{
		StringPair input = steps_hover.get(selection);
		if(input != null)
			return input.extended;
		return "";
	}
	
	public Color getStepColor(String selection)
	{
		StringPair input = steps.get(selection);
		if(input != null)
			return input.color;
		return Color.gray;
	}
	
	private class StringPair implements Serializable{
		String value;
		String extended;
		Color color = Color.black;
		
		StringPair(String val, String ext)
		{
			value = val;
			extended = ext;
		}
		
		@Override
		public String toString()
		{
			return value+" - "+extended;
		}
	}
	
	public String getStringPairValue (StringPair sp) {
		return sp.value;
	}
	
	public String getStringPairExtended (StringPair sp) {
		return sp.extended;
	}
	
	public Color getStringPairColor (StringPair sp) {
		return sp.color;
	}
	
	@Override
	public String toString()
	{
		String example = "("+sectionNumber+") "+title+": "+explanation+"\n";
		for(String key:steps.keySet())
		{
			example+= key+"="+steps.get(key)+"\n";
		}
		return example;
	}
}
