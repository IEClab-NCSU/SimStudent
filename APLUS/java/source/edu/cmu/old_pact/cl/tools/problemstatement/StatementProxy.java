package edu.cmu.old_pact.cl.tools.problemstatement;

import java.util.Vector;

import edu.cmu.old_pact.dormin.DataFormatException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Range;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.dormin.toolframe.DorminToolProxy;

public class StatementProxy extends DorminToolProxy {

	// Constructor
	public StatementProxy(ObjectProxy parent) {
		 super(parent, "ProblemStatement");
	}
	
	public StatementProxy(){
		super();
	}	
		
	public void constructChildProxy(MessageObject inEvent, Vector description) { }
	
	public  void create(MessageObject inEvent) throws DorminException{
		try{
			String childType = inEvent.extractStrValue("OBJECTTYPE");
			if(childType.equalsIgnoreCase("ProblemStatement")) {
				ProblemStatement problemStatement = new ProblemStatement();
				this.setRealObject(problemStatement);
				problemStatement.setProxyInRealObject(this);
				setRealObjectProperties((Sharable)problemStatement, inEvent);
			} 
			else
				super.create(inEvent);
		}catch (DorminException e) { 
			throw e; 
		} 
	}
	
	public  void delete(MessageObject mo){ 
		this.deleteProxy();
	}
	
	public  void setProperty(MessageObject mo) throws DorminException { 
		try{
			char objType = mo.getObjectType("OBJECT");
			Vector pn = mo.extractListValue("PROPERTYNAMES");
			Vector pv = mo.extractListValue("PROPERTYVALUES");
			int s = pn.size();
			if(s == 0) return;
			ProblemStatement ps = (ProblemStatement)getObject();
		
			switch (objType){
				case 'O':
						for(int i=0; i<s; i++){
							ps.setProperty((String)pn.elementAt(i), pv.elementAt(i));
						}
						break;
				case 'R':
						String propertyName = (String)pn.elementAt(0);
						if(s == 1 && propertyName.equalsIgnoreCase("HIGHLIGHT")){
							if(((String)pv.elementAt(0)).equalsIgnoreCase("FALSE"))	{
								ps.setProperty(propertyName, (new Vector()));
								return;
							}
							//Range range = mo.extractRangeValue("OBJECT");
							Range range = (Range)(mo.getParameter("OBJECT"));
							String rangeType = range.getRangeType();
							if(rangeType.equalsIgnoreCase("CHARACTER")){
								Vector tags = range.getStartEndPairs();
								ps.setProperty(propertyName, tags);
							}
							else throw new DataFormatException("ProblemStatement doesn't know how to highlight ranges of type "+rangeType);
						}
						break;
			}			
		}
		catch (DorminException e) {
			throw e;
		}	
	}
	
    public void reset(MessageObject inEvent)
    {
        ((ProblemStatement)getObject()).reset();
    }

}
