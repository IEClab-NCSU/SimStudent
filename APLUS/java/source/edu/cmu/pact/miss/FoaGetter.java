/**
 * 
 */
package edu.cmu.pact.miss;

import java.util.List;
import java.util.Vector;

import pact.CommWidgets.JCommWidget;
import pact.CommWidgets.JCommTable.TableCell;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;

/**
 * @author mazda
 *
 */
public class FoaGetter {
    
    /**
     * maintains the history of calls to FoaGetter, i.e. steps learned
     */
    static Vector history;

    /**
     * must be overridden
     */
    public Vector /* Object */ foaGetter(BR_Controller brc,
                                                  String selection, String action, String input, 
                                                  Vector edgePath){
        new Exception("you must override FoaGetter.foaGetter() with your domain-specific implementation.").printStackTrace();
        return null;
    }
    
    public Vector<String> foaGetterStrings(BR_Controller brc,
            String selection, String action, String input, 
            Vector edgePath)
    { 
    	Vector<Object> foas = foaGetter(brc,selection,action,input,edgePath);
		Vector<String> foaStrings = new Vector<String>();
		for(int i=0;i<foas.size();i++)
		{
			Object f = foas.get(i);
			if(f instanceof JCommWidget)
				foaStrings.add(((JCommWidget) f).getName());
			if(f instanceof TableCell)
				foaStrings.add(((TableCell) f).getName());
				
		}
		return foaStrings;
    
    }

    public String formulateBasedOnTypeOfString(String selection, BR_Controller brController, String message){
    return message;
    }
    
    public String getTypeOfStep(String selection,BR_Controller brController){
    	return null;
    }
    
    public String getStepSkill(String selection,BR_Controller brController){
    	new Exception("you must override FoaGetter.getStepSkill() with your domain-specific implementation.").printStackTrace();
        return null;
    }
    
    public String getMTHintMessageOnFeedback(String msg, BR_Controller brController){
    	 new Exception("you must override FoaGetter.getMTFeedbackMessage() with your domain-specific implementation.").printStackTrace();
         return null;
    }
    
    
    /**
     * adds record to history. All implementations of FoaGetter.foaGetter() should call this method.
     *
     */
    public void recordQuery(String selection){
        history.add(selection);        
    }
    
    /**
     * Returns the n last selections for which FoaGetter was used. Used for recency calculations.
     * @param n
     * @return
     */
    public static List getNLastQueries(int n){
        Vector v = new Vector();
        int startIndex = history.size() - n;
        for (int i=startIndex; i<history.size(); i++){
            v.add((String) history.get(i));
        }
        return v;
    }

    /**
     * returns the number of queries ago in which this selection was used.
     * @param sel
     * @return
     */
    public static int howManyBack(String sel){
        return history.size() - history.lastIndexOf(sel);
    }

        
    // Override this method if you wish any sort of initialization of the
    // FoaGetter class you are defining would have been taken place when 
    // a new start state is created
    // 
    public void init(BR_Controller brController) {
    	if(trace.getDebugCode("miss"))trace.out("miss", "You must define init(BR_Controller) in your FoaGetter class!");
    }

    //Gustavo 25Jan2007
    //this is a setter without a field
    //it gets overridden by the implementation in the specific foaGetter subclass
    public void setGaSelections(Vector v){
        
    }
    
    

    /**
     * Method that constructs the message given for the <sai> part of SimStudent language
     * @return
     */
    public String getSaiString(String selection, String action, String input,BR_Controller brController){ 	
    	return "enter "+ input + " in the " + brController.getMissController().getSimStPLE().getComponentName(selection);
    }
    
    
    
    
    //Override this method to provide different word description of the FOA for an instruction
	public String foaDescription(Instruction inst)
	{
		Vector foa = inst.getFocusOfAttention();
    	String desc = inst.getInput()+" when I had";
    	
    	for(int i=1;i<foa.size();i++)
    	{
    		String theFoa = ((String) foa.get(i));
    		theFoa = theFoa.substring(theFoa.lastIndexOf('|')+1);
    		if(i+1 <foa.size() -1)
    		{
    			desc += " "+theFoa+",";
    		}
    		else
    		{
	    		if(i>1 )
	    		{
	    			desc += " and "+theFoa;
	    		}
	    		else
	    		{
	    			desc += " "+theFoa;
	    		}
    		}
    	}
    	
    	return desc;
		
		
	}
	
	public String foaStepDescription(Instruction inst)
	{
		Vector foa = inst.getFocusOfAttention();
    	String desc = "";
    	
    	for(int i=1;i<foa.size();i++)
    	{
    		String theFoa = ((String) foa.get(i));
    		theFoa = theFoa.substring(theFoa.lastIndexOf('|')+1);
    		if(i+1 <foa.size() -1)
    		{
    			desc += " "+theFoa+",";
    		}
    		else
    		{
	    		if(i>1 )
	    		{
	    			desc += " and "+theFoa;
	    		}
	    		else
	    		{
	    			desc += " "+theFoa;
	    		}
    		}
    	}
    	
    	return desc;
	}

	public String relevantFoaString(String input, Vector<String> foas) {
		
		if(foas.size() > 0)
			return foas.get(0).substring(foas.get(0).lastIndexOf('|')+1);
		return null;
	}

}
