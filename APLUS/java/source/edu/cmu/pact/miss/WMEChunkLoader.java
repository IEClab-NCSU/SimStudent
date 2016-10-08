package edu.cmu.pact.miss;

import java.util.Iterator;
import java.util.Vector;

import jess.Context;
import jess.Deftemplate;
import jess.Fact;
import jess.JessException;
import jess.RU;
import jess.Rete;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.MTRete;

/**
 * a SimStudentModelTracerFunction used by the model tracing hook to load facts about decomposed elements into working memory
 * @author ajzana
 *
 */

public class WMEChunkLoader extends SimStudentModelTracerFunction {
    /**
     * a list of decomposers which are applied in a chain to all input for clearing the facts
     */
    private Vector /* of Decomposer*/ decomposers;
    /**
     * used to store the def templates between working memory resets (needed to handle model tracing)
     * (currently not used)
     */
    private Vector /*of DefTemplate*/ defTemplateCache=new Vector();
    public static final String DEFAULT_ARG_PREFIX="arg";
    public Value javaCall(ValueVector vv, Context context) throws JessException 
    {
        if(vv==null)
            return new Value(false);
        Rete rete=context.getEngine();

        Vector initValues=new Vector();
        Vector allInputs=new Vector();
        Vector decomposedValues=new Vector();

        for(int i=0; i<vv.size(); i++)
            initValues.add(vv.get(i).stringValue(context));

        allInputs.addAll(initValues);
        decomposedValues=SimSt.chainDecomposedValues(initValues,decomposers);
        allInputs.addAll(decomposedValues);
        Iterator predIter=predicates.iterator();

        if(!decomposedValues.isEmpty())
        {
            //assertCachedTemplates(rete);
            while(predIter.hasNext())
            {
                FeaturePredicate curPred=(FeaturePredicate)predIter.next();
                if(curPred.isDecomposedRelationship())
                {
                    Vector argSets=Relation.permuteArgs(allInputs,curPred.getArity());
                    String curName=curPred.getName();
                    Iterator argIter=argSets.iterator();

                    while(argIter.hasNext())
                    {
                        Vector curArgs=(Vector)argIter.next();
                        if(curPred.apply(curArgs)!=null)
                            assertUnOrderedWME(curName,curPred.getArgNames(),curArgs,rete);
                    }
                }
            }
        }
        return new Value(true);
    }

    public String getName() {
        return "wme-chunk-loader";
    }

    /**
     * return the arguments for use by a call to this function
     * filters out any SAIs whose selection does not have a value slot
     * returns null in such a case
     */
    public ValueVector getArguments(String selection, String action, String input,Rete rete) throws JessException {
        //filters out selections which do not have a value slot
        ValueVector v=new ValueVector();
        MTRete mtRete=(MTRete)rete;
        Fact f=mtRete.getFactByName(selection);
        if(f==null)
            return null;
        Deftemplate dt=f.getDeftemplate();
        if(dt.getSlotIndex("value")==-1)
            return null;
        v.add(input);
        return v;
    }

    /**
     * assert an unordered fact into working memory
     * @param name the head of the fact to assert
     * @param args the slot values of the fact
     * @param rete a reference to the appropriate rete
     * @throws JessException
     */
    public void  assertUnOrderedWME(String name, Vector argNames, Vector args, Rete rete) throws JessException
    {
        //check if the template for this fact type exists, if not create a new one
        Deftemplate curType=rete.findDeftemplate(name);
        /*
        8-15-06 This code for automatically supporting automatically generating defTemplates is not supported by CTAT
	at the moment, but might be useful someday
	if(curType==null)
	curType=makeDefTemplate(name,argNames,rete);
        */

        Fact curFact=new Fact(curType);
        Value v;	
        for(int argnum=0; argnum<args.size(); argnum++)
        {
            String curArgName=(String)argNames.get(argnum);
            v=MTRete.stringToValue((String)args.get(argnum),rete.getGlobalContext());
            //convert argument to a proper jess value
            curFact.setSlotValue(curArgName,v);
        }
        rete.assertFact(curFact);
    }

    //currently unused see note in assertUnOrderedWME
    private static Deftemplate makeDefTemplate(String name, Vector argNames, Rete rete) throws JessException{
        Deftemplate type=new Deftemplate(name,"",rete);
        for(int i=0; i<argNames.size(); i++)
            type.addSlot((String)argNames.get(i),new Value("",RU.STRING),"STRING");

        rete.addDeftemplate(type);
        return type;
    }

    /**
     * 
     * @return the list of decomposers
     */
    public Vector getDecomposers() {
        return decomposers;
    }

    /**
     * set the list of decomposers
     * @param decomposers
     */
    public void setDecomposers(Vector decomposers) {
        this.decomposers = decomposers;
    }

    public void addDecomposer(Decomposer d)
    {
        if(decomposers==null)
            decomposers=new Vector();
        decomposers.add(d);
    }
}
