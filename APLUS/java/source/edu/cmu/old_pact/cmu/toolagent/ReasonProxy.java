package edu.cmu.old_pact.cmu.toolagent;

import java.util.Vector;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Range;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.dormin.ToolProxy;

public class ReasonProxy extends ToolProxy {
	
	public ReasonProxy(ObjectProxy parent) {
		 super(parent, "ReasonTool");
	}
	
	public ReasonProxy(){
		super();
	}
	
	public  void create(MessageObject inEvent)  throws DorminException{
		try{
			String childType = inEvent.extractStrValue("OBJECTTYPE");
			if(childType.equalsIgnoreCase("ReasonTool")) {
				ReasonFrame reasonFrame = new ReasonFrame();
				try{
					setRealObjectProperties((Sharable)reasonFrame, inEvent);
				}catch (DorminException e) { throw e;}
				this.setRealObject(reasonFrame);
				reasonFrame.setProxyInRealObject(this);
				//reasonFrame.setVisible(true); 
			}
			else if (childType.equalsIgnoreCase("Question")) {
				ReasonQuestionProxy rqp = new ReasonQuestionProxy(this);
				rqp.mailToProxy(inEvent, (new Vector()));
			}
			else {
				super.create(inEvent);
			}
		}catch (DorminException e) { 
			throw e; 
		}
	}
	
	public  void setProperty(MessageObject mo) throws DorminException{ 
		try{
			char objType = mo.getObjectType("OBJECT");
			Vector pn = mo.extractListValue("PROPERTYNAMES");
			Vector pv = mo.extractListValue("PROPERTYVALUES");
			int s = pn.size();
			if(s == 0) return;
			ReasonFrame ps = (ReasonFrame)getObject();
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
							Range range = mo.extractRangeValue("OBJECT");
							String rangeType = range.getRangeType();
							if(rangeType.equalsIgnoreCase("CHARACTER")){
								Vector tags = range.getStartEndPairs();
								ps.setProperty(propertyName, tags);
							}
						}
						break;
			}			
		}
		catch (DorminException e) {
			throw e;
		}	
	}
	
}