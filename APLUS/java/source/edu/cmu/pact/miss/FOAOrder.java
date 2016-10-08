package edu.cmu.pact.miss;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import jess.Fact;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import mylib.CombinatoricException;
import mylib.Permutations;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.MTRete;
/**
 * Class to wrap methods used to automatically reorder FOA
 * As of 8-16-06, this code is not actually used, it is left in CVS 
 * @author ajzana
 *
 */
public class FOAOrder 
{
    /**
     * Given an Instruction and a Rule, try to order the FOA of the instruction in a manner consistent with the topoplogical constraints of rule 
     * @param instr
     * @param r
     * @param mtRete
     * @return true if it was able to find an ordering, false otherwise
     */
	static boolean  orderFOA(Instruction instr,Rule r,MTRete mtRete)
    {
    	/*This function makes the assumption that the input wme is last in the path list*/
    	
    	ArrayList wmePaths=r.getLhsPath();
    	
    	Vector varNames=new Vector(); //used to order the variables
    	HashMap wmeToVar;//used to map between WMEs and variables
    	
    	for(int i=0; i<instr.numFocusOfAttention(); i++)
    	{
    		String curPath=(String)wmePaths.get(i);
    		String[] pathVars=curPath.split("\n");
    		//find the terminal variable binding for each FOA
    		String binding=pathVars[pathVars.length-1];
    		varNames.add(i,binding.substring(0,binding.indexOf(" ")));
    		
    		
    	}
    	Vector seeds=instr.getSeeds();
    	

    	
    	
    	Vector foaNames=new Vector();//names of each foa
    	//flag to indicate if the input appears in the WME, if it appears, but is not used in constraints, useInput is true, but the 
    	//variable binding is ignored
    	boolean useInput=instr.numFocusOfAttention()>instr.numSeeds(); 
    	
    	
    	if(useInput)
    	{
    	String inputName=Instruction.getNameFromFoa(instr.getFocusOfAttention(0));
    		foaNames.add(inputName);
    	}
    	Iterator iter=seeds.iterator();
    	while(iter.hasNext())
    		foaNames.add(Instruction.getNameFromFoa((String)iter.next()));

    	wmeToVar=findFoaToVarMappings(r.getLhsTopologicalConsts(),useInput,foaNames,varNames,mtRete);
    	if(wmeToVar!=null)
    	{
	    	//now we know which WMEs correspond to which variable bindings, now order the FOA
	    	String[] orderedFOA=new String[instr.numFocusOfAttention()];
	    	int foaNum=0;
	    	while(foaNum<instr.numFocusOfAttention())
	    	{

	    		String curFoa=instr.getFocusOfAttention(foaNum);
	    		String curName=Instruction.getNameFromFoa(curFoa);
	    		String variable=(String)wmeToVar.get(curName);
	    		int index=varNames.indexOf(variable);
	    		orderedFOA[index]=curFoa;
	    		
	    		foaNum++;
	    	}
	    	Vector v=new Vector(orderedFOA.length);
	    	for(int i=0; i<orderedFOA.length; i++)
	    		v.add(orderedFOA[i]);
    		instr.setFocusOfAttention(v);
    		return true;
    	}
    	else 
    		return false;
    	
    	
    		
    	
    }
	/**
	 * NOTE: In the case of tie, returns the last mapping found, this breaks these 
	 * @param constraints a list of actual constraints taken from the production rule
	 * @param useInput true if this rule contains a WMEPath to its Input
	 * @param foaNames the names of the FOA WMEs
	 * @param varNames the names of the jess variables in the production rules
	 * @param mtRete a reference to the model tracing rete
	 * @return a HashMap containing the mappings FoaName=>VarName
	 */
    private static HashMap findFoaToVarMappings(ArrayList/*of String*/ constraints, boolean useInput, Vector foaNames, Vector varNames,MTRete mtRete)
    {
    
    	HashMap theMap=new HashMap();
    	Object bestMap=null;
    	int maxSatisfied=0;
    	if(useInput)
    	{
    		String inputVar=(String)varNames.lastElement();
    		theMap.put(foaNames.firstElement(),inputVar);
    		foaNames.remove(0);
    	}
    	try
    	{
    	Permutations p=new Permutations(foaNames.toArray(),foaNames.size());
    	

    		while(p.hasMoreElements())
    		{

    		Object[] permutation=(Object[])p.nextElement();
    		for(int i=0; i< foaNames.size(); i++)
    			theMap.put(permutation[i],varNames.get(i));
    		int numSatisfied;
    		try
    		{
    			 numSatisfied=countConstrantsSatisfied(theMap,constraints,mtRete);
    			 
    		}
    		catch(JessException e)
    		{
    			e.printStackTrace();
    			continue;
    		}

    			if(numSatisfied>maxSatisfied)
    			{
    				maxSatisfied=numSatisfied;
    				bestMap=theMap.clone();
    			}
    		}
    	}
    	catch(CombinatoricException e)
    	{
    		e.printStackTrace();
    		return null;
    	}
    	return (HashMap)bestMap;
    	
    	
    }
    /**
     * 
     * @param map a mapping of WmeName=>varName
     * @param constraits a list of constraints to satisfy as (name ?varname1, ?varname2...)
     * @return the number of constraints satisfied by this mapping
     */
    private static int countConstrantsSatisfied(HashMap wmeToVarMap, ArrayList constranits, MTRete mtRete) throws JessException
    {
    	int numSatisfied=0;
    	HashMap varNameToFact=new HashMap();
    	Iterator factIter=wmeToVarMap.keySet().iterator();
    	while(factIter.hasNext()) //map var names to WME facts
    	{
    		String WMEName=(String)factIter.next();
    		String varName=(String)wmeToVarMap.get(WMEName);
    		Fact f=mtRete.getFactByName(WMEName);
    		varNameToFact.put(varName,f);
    		
    	}
    	Iterator constraintIter=constranits.iterator();
    	while(constraintIter.hasNext())
    	{
    		String curConstraint=(String)constraintIter.next();
    		//strip off parens
    		curConstraint=curConstraint.substring(curConstraint.indexOf("(")+1,curConstraint.lastIndexOf(")"));
    		
    		String[] constraintParts=curConstraint.split(" ");
    		String constraintName=constraintParts[0];
    		Userfunction function=mtRete.findUserfunction(constraintName);
    		ValueVector args=new ValueVector();
    		args.add(constraintName);
    		for(int argnum=1; argnum<constraintParts.length; argnum++)
    		{
    			Fact f=(Fact)varNameToFact.get(constraintParts[argnum]);
    			args.add(f);
    		}
  
    		if(function.call(args,mtRete.getGlobalContext()).equals(new Value(true)))
    			numSatisfied++;
    		
    	}
    	
    	
    	return numSatisfied;
    }
}
