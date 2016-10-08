package edu.cmu.pact.Log;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jess.Deffacts;
import jess.Deffunction;
import jess.Defglobal;
import jess.Defquery;
import jess.Defrule;
import jess.Deftemplate;
import jess.Fact;
import jess.HasLHS;
import jess.JessException;
import jess.Named;
import jess.PrettyPrinter;
import jess.Rete;
import jess.Value;
import edu.cmu.pact.Utilities.EventLogger;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.MTRete;

/**
 * @author Dan Tasse
 * This class contains one public method (logReteChanges) to log the
 * changes between two retes.  It is used in loadJessFiles() in MT.
 */
public class LogReteChanges {
	
	private EventLogger eventLogger = null;
	
//	Dummy variable created for the PrettyPrinter constructor.
//	Why does the constructor require a visitable?
	private PrettyPrinter pp = new PrettyPrinter(new Defglobal("", new Value(true)));
	
	private MTRete oldRete;
	private MTRete newRete;
	
	/**
	 * Constructor for the LogReteChanges class
	 * @param el An EventLogger that contains the target logging location
	 * @param oldRete The old rete that will be compared to
	 * @param newRete The new rete to compare to the old rete
	 */
	public LogReteChanges(EventLogger el, MTRete oldRete, MTRete newRete)
	{
		this.eventLogger = el;
		this.oldRete = oldRete;
		this.newRete = newRete;
	}
	
	
	public void logReteChanges() 
	{	

		logDefruleChanges();
		logDeftemplateChanges();
		logDeffactsChanges();
		logDefglobalChanges();
		logDeffunctionChanges();
		
		//facts are not loaded at the same time as the other items?
		logFactChanges();

		//TODO: get facts working
	}
	
	// "Private" methods are instead package private for access in the JUnit test
	/*private*/ void logDefglobalChanges()
	{
		Iterator iter = newRete.listDefglobals();
		while (iter.hasNext())
		{
			Defglobal dg = (Defglobal) iter.next();

			// check for newly added defglobals
			if (oldRete.findDefglobal(dg.getName()) == null)
				logAddedConstruct(dg);
			else
			{
				// check for defglobals that exist in both retes but were changed
				Defglobal oldDg = oldRete.findDefglobal(dg.getName());
				if (!pp.visitDefglobal(dg).equals(pp.visitDefglobal(oldDg)))
					logChangedConstruct(dg, oldDg);
			}
		}
		// check for defglobals that were in the old rete but not the new
		iter = oldRete.listDefglobals();
		while (iter.hasNext())
		{
			Defglobal dg = (Defglobal) iter.next();
			
			//These 3 defglobals are automatically created
			if (dg.getName().equals("*sSelection*")
					||dg.getName().equals("*sAction*")
					||dg.getName().equals("*sInput*"))
				continue;

			if (newRete.findDefglobal(dg.getName()) == null)
				logDeletedConstruct(dg);
		}
	}
	
	/*private*/ String prettyPrint(Named con)
	{
		if (con instanceof Defrule)
			return (String)(pp.visitDefrule((Defrule) con));
		if (con instanceof Deftemplate)
			return (String)(pp.visitDeftemplate((Deftemplate) con));
		if (con instanceof Deffunction)
			return (String)(pp.visitDeffunction((Deffunction) con));
		if (con instanceof Deffacts)
			return (String)(pp.visitDeffacts((Deffacts) con));
		if (con instanceof Defglobal)
			return (String)(pp.visitDefglobal((Defglobal) con));
		return null;
	}
	
	/** Logs a construct that was in the old rete but isn't in the new rete */
	/*private*/ void logDeletedConstruct(Named con)
	{
		eventLogger.log(true, AuthorActionLog.EXTERNAL_EDITOR, 
				"DELETE_" + con.getConstructType(), con.getName(), "", "");
	}

	/** Logs a construct that's been changed between the old and new retes */
	/*private*/ void logChangedConstruct(Named newCon, Named oldCon)
	{
		eventLogger.log(true, AuthorActionLog.EXTERNAL_EDITOR, 
				"EDIT_" + newCon.getConstructType(), newCon.getName(), 
				prettyPrint(oldCon), prettyPrint(newCon));
	}

	/** Logs a construct that's been added to the new rete */
	/*private*/ void logAddedConstruct(Named con)
	{
		eventLogger.log(true, AuthorActionLog.EXTERNAL_EDITOR, 
				"ADD_" + con.getConstructType(), con.getName(), 
				prettyPrint(con), "");
	}

	/*private*/ void logDeffunctionChanges()
	{

		Iterator iter = newRete.listFunctions();
		while (iter.hasNext())
		{
			Object ob  = iter.next();
			//listFunctions lists all functions, not just user-defined deffunctions
			if (!(ob instanceof Deffunction))
				continue;
			Deffunction df = (Deffunction) ob;
			
			// check for newly added defglobals
			if (oldRete.findUserfunction(df.getName()) == null)
				logAddedConstruct(df);
			else
			{
				// check for defglobals that exist in both retes but were changed
				Deffunction oldDf = (Deffunction)(oldRete.findUserfunction(df.getName()));
				if (!pp.visitDeffunction(df).equals(pp.visitDeffunction(oldDf)))
					logChangedConstruct(df, oldDf);
			}
		}
		// check for defglobals that were in the old rete but not the new
		iter = oldRete.listFunctions();
		while (iter.hasNext())
		{
			Object ob  = iter.next();
			//listFunctions lists all functions, not just user-defined deffunctions
			if (!(ob instanceof Deffunction))
				continue;
			Deffunction df = (Deffunction) ob;
			
			if (newRete.findUserfunction(df.getName()) == null)
				logDeletedConstruct(df);
		}
	}

	/*private*/ void logDeffactsChanges()
	{
		Iterator iter = newRete.listDeffacts();
		while (iter.hasNext())
		{
			Deffacts df = (Deffacts) iter.next();

			// check for newly added deffacts
			if (oldRete.findDeffacts(df.getName()) == null)
				logAddedConstruct(df);
			else
			{
				// check for deffacts that exist in both retes but were changed
				Deffacts oldDf = oldRete.findDeffacts(df.getName());
				if (!pp.visitDeffacts(df).equals(pp.visitDeffacts(oldDf)))
					logChangedConstruct(df, oldDf);
			}

		}
		// check for deffacts that were in the old rete but not the new
		iter = oldRete.listDeffacts();
		while (iter.hasNext())
		{
			Deffacts df = (Deffacts) iter.next();
			if (newRete.findDeffacts(df.getName()) == null)
				logDeletedConstruct(df);
		}
	}


	/**
	 * @param label
	 * @param rete
	 * @return set of facts
	 */
	private Set getFactsSet(String label, Rete rete) {
		Set set = new LinkedHashSet();
		Iterator iter = rete.listFacts();
		while(iter.hasNext()) {
			Fact f = (Fact) iter.next();
			Integer id = new Integer(f.getFactId());
			set.add(f);
		}
		return set;
	}
	
	/**
	 * This doesn't accurately log fact changes.
	/*private*/ void logFactChanges() {
		try {
			Iterator iter = oldRete.listFacts();
			while(iter.hasNext()) {
				Fact f = (Fact) iter.next();
				if (newRete.findFactByFact(f) == null)
					logDeletedFact(f);
			}
			iter = newRete.listFacts();
			while(iter.hasNext()) {
				Fact f = (Fact) iter.next();
				if (oldRete.findFactByFact(f) == null)
					logAddedFact(f);
			}
		} catch (JessException je) {
			trace.err("Error finding facts for logFactChanges(): "+je+
					(je.getCause() == null ? "" : ": "+je.getCause().toString()));
		}
	}		
	
	/**
	 * Log a fact addition.
	 * @param f
	 */
	private void logAddedFact(Fact f) {
		eventLogger.log(true, AuthorActionLog.EXTERNAL_EDITOR, 
				"ADD_" + f.getConstructType(), f.getName(), f.toString(), "");
	}
	
	/**
	 * Log a fact deletion.
	 * @param f
	 */
	private void logDeletedFact(Fact f) {
		eventLogger.log(true, AuthorActionLog.EXTERNAL_EDITOR, 
				"DELETE_" + f.getConstructType(), f.getName(), f.toString(), "");
	}


/*
		Set oldFacts = getFactsMap("old Rete", oldRete);
		for (Iterator it = oldFacts.iterator(); it.hasNext(); ) {

			String label = "new Rete";
			Rete newRete = new Rete();
			getFactsMap(label, newRete);
			// check for newly added facts
			if (oldRete.findFactByFact(f) == null)
				logAddedConstruct(f);
			else {
				// check for facts that exist in both retes but were changed
				Fact oldF = oldRete.findFactByFact(f);
				if(!f.toString().equals(oldF.toString()))
					logChangedConstruct(f, oldF);
			}
		}


		// check for facts that were in the old rete but not the new
		iter = oldRete.listFacts();
		ArrayList newfacts = newRete.getFacts();
		while (iter.hasNext())
		{
			Fact f = (Fact)iter.next();
			boolean fIsInNewFacts = false;
			for(int i = 0; i < newfacts.size(); i++)
			{
				if(newfacts.get(i).toString().equals(f.toString()))
					fIsInNewFacts = true;
			}

			if (!fIsInNewFacts)
//				if (!newRete.getFacts().contains(f))
				logDeletedConstruct(f);
		}
	} catch (JessException je) {je.printStackTrace();}
}
*/


	/**
	 * Logs any changes between the rules in the old and new retes
	 */
	/*private*/ void logDefruleChanges()
	{
		Iterator iter = newRete.listDefrules();
		Map oldRules = oldRete.allRulesMap();
		while(iter.hasNext())
		{
			HasLHS dr = (HasLHS) iter.next();
			if (oldRules.containsKey(dr.getName()))
			{
				//all $?'s get "_blank_mf" and then a number appended for Jess's 
				//internal representation so we must remove this before comparing 
				//the two strings.
				HasLHS oldDr = (HasLHS)(oldRules.get(dr.getName()));
				String oldRulePP, newRulePP;
				if(oldDr instanceof Defquery)
					oldRulePP = ((String) pp.visitDefquery((Defquery) oldDr)).replaceAll("_blank_mf\\d*", "");
				else
					oldRulePP = ((String) pp.visitDefrule((Defrule) oldDr)).replaceAll("_blank_mf\\d*", "");
				if(dr instanceof Defquery)
					newRulePP = ((String) pp.visitDefquery((Defquery) dr)).replaceAll("_blank_mf\\d*", "");
				else
					newRulePP = ((String) pp.visitDefrule((Defrule) dr)).replaceAll("_blank_mf\\d*", "");
				if(!oldRulePP.equals(newRulePP))
				{	
					logChangedConstruct(dr, oldDr);
				}
			} 
			else 
			{
				logAddedConstruct(dr);
			}
		}
		Map newRules = newRete.allRulesMap();
		Iterator oldRulesIterator = oldRules.values().iterator();
		while(oldRulesIterator.hasNext())
		{
			Defrule oldRule = (Defrule) oldRulesIterator.next();
			if (!newRules.containsKey(oldRule.getName()))
				logDeletedConstruct(oldRule);
		}
	}
	
	/*private*/ void logDeftemplateChanges() 
	{
		try
		{
			Iterator iter = newRete.listDeftemplates();
			while(iter.hasNext())
			{
				Deftemplate dt = (Deftemplate) iter.next();
				
				// check for newly added deftemplates
				if (oldRete.findDeftemplate(dt.getName()) == null)
					logAddedConstruct(dt);
				else
				{
					// check for deftemplates that exist in both retes but were changed
					Deftemplate oldDt = oldRete.findDeftemplate(dt.getName());
					
					// each Deftemplate automatically gets " extends MAIN::__fact" appended
					String oldDtString = pp.visitDeftemplate(oldDt).toString()
						.replaceAll(" extends MAIN::__fact", "");
					if (!pp.visitDeftemplate(dt).equals(oldDtString))
						logChangedConstruct(dt, oldDt);
				}

			}
			// check for deftemplates that were in the old rete but not the new
			iter = oldRete.listDeftemplates();
			while (iter.hasNext())
			{
				Deftemplate dt = (Deftemplate)iter.next();
				if (newRete.findDeftemplate(dt.getName()) == null &&
						!dt.getName().equals("MAIN::studentValues"))
					//The studentValues template is automatically created
					logDeletedConstruct(dt);
			}
		} catch (JessException je) {je.printStackTrace();}
	}
}
