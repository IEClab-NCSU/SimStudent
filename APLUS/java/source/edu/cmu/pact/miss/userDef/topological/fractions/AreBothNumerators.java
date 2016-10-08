package edu.cmu.pact.miss.userDef.topological.fractions;

import java.util.Iterator;
import java.util.Vector;

import edu.cmu.pact.Utilities.trace;
import jess.Defquery;
import jess.Deftemplate;
import jess.Fact;
import jess.HasLHS;
import jess.JessException;
import jess.QueryResult;
import jess.RU;
import jess.Rete;
import jess.Value;
import jess.ValueVector;

public class AreBothNumerators  extends FractionConstraint {

	public AreBothNumerators() {
		setArity(2);
		setName("are-both-numerators");
	}

	public String apply(Vector args, Rete rete) {

		try
		{	
			Fact f1=(Fact)args.get(0);
			
			
			
			//System.out.println("Def template: " + f1.getDeftemplate());
			getFactParent(f1,rete);
			//Deftemplate dt1=f1.getDeftemplate();
		
		
			//Fact f=getComplexFractionFact(rete);
			//System.out.println("**** Fact found:" + f);
			
		
			return (areBothNumerators((Fact)args.get(0),(Fact)args.get(1), rete) ? "T" : null);
		}
		catch (JessException e) 
		{
			e.printStackTrace();
			
			return null;
		}
		
		
	}
	
	protected String getMultislotName(Fact fact){
		String ret=fact.getName();
		String ret2=ret.replace("MAIN::", "")+"s";
		return ret2;
	}
	
	/* returns if current fact is parent of fact testFact).
	 * 
	 * */
	protected boolean isParent(Fact currentFact, Fact testFact, Rete rete) throws JessException{
			
		String multislotName=getMultislotName(testFact);
		Deftemplate template=currentFact.getDeftemplate();
		int numberSlots=template.getNSlots();
				for (int i=0;i<numberSlots;i++){
						
					if (template.getSlotName(i).equals(multislotName)){
						return true;
					}
							//	System.out.println("" + testFact.getName() + " has parent " + currentFact.getName());
					
				}
			
			return false;		
		
	}
	
	// low level synartisi pou nakei traverse ola ta facts kai dinei ton parent. genika.	
	protected Fact getFactParent(Fact fact, Rete rete) throws JessException{
		

		Iterator iter=rete.listFacts();
		
		while(iter.hasNext())
    	{
			Fact curFact=(Fact)iter.next();
    			  		
    		if (isParent(curFact,fact,rete)){
    			// yes, this template is a parent template, now check if it matches...
    				System.out.println("Checking if " +  curFact + " is parent of " + fact.getSlotValue("name"));
    				
    				Value tableValues=curFact.getSlotValue(getMultislotName(fact));    
    				
	    			ValueVector tables=tableValues.listValue(rete.getGlobalContext());
	    			
	    			for (int i=0;i<tables.size();i++){
	    				Fact tmpTable=tables.get(i).factValue(rete.getGlobalContext());
	    					System.out.println(tmpTable.getSlotValue("name") + "="+fact.getSlotValue("name"));
	    					if (tmpTable.getSlotValue("name").equals(fact.getSlotValue("name")))
	    						return curFact;			
	    			}
    				
    				
    				
    				
    				
    			
    				//return curFact;//System.out.println("parent is " + curFact.getName());
    			
    		}
		  
    	}	
    		return null;
	}
	
	
	protected void traverseParents(Fact fact, Rete rete) throws JessException{
		Iterator iter=rete.listFacts();
		
		while(iter.hasNext())
    	{	
			
			Fact curFact=(Fact)iter.next();
			Fact parent=getFactParent(curFact,rete);
			//System.out.println("checking for " + curFact.getName());
			if (parent!=null)
				//System.out.println("parent of "+curFact.getName()+" is " + parent.getName());
				System.out.println("parent of "+curFact +" is " + parent);
    	}
		
	}
	
	
	/**
	 * Retrieve the studentValues fact using the defquery
	 * <tt>(defquery get-studentValues "Retrieve the fact holding the student SAI." (studentValues))</tt>
	 * @return
	 */
	public Fact getComplexFractionFact(Rete rete) throws JessException {
		for(int trial = 0; trial < 2; trial++) {   // try up to 2x
			try {
				QueryResult qr = rete.runQueryStar("get-complexFraction", new ValueVector());
				if(!qr.next())
					return null;
				Value sv = qr.get("?sv");
				Fact result = sv.factValue(rete.getGlobalContext());
				if(trace.getDebugCode("sv"))
					trace.out("sv", "query found student values fact "+result);
				return result;
			} catch(JessException je) {
				if(trial > 0) {
					//trace.err("Error running defquery get-complexFraction: "+je+"; cause "+je.getCause()+
					//	".\n  "+je.getProgramText()+
					//	"\n  "+je.getDetail());
					continue;
				}
			}
			synchronized(this) {
				System.out.println("defining complex");
				HasLHS q = rete.findDefrule("complex-fraction");
				if(!(q instanceof Defquery))
					rete.eval("(defquery get-complexFraction "+
							"\"Retrieve the fact holding the fractionfacts\"" +
							"?sv <- ())");
			}
		}
		return null;
	}

	
	
}
