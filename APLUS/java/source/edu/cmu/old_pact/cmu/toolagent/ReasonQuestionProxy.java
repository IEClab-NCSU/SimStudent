package edu.cmu.old_pact.cmu.toolagent;

import java.beans.PropertyChangeListener;
import java.util.Vector;

import edu.cmu.old_pact.dormin.CommonObjectProxy;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Range;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.dormin.ToolProxy;

public class ReasonQuestionProxy extends ToolProxy {
	
	public ReasonQuestionProxy(ObjectProxy parent) {
		 super(parent, "Question");
	}
	
	public  void create(MessageObject inEvent)  throws DorminException{
		try{
			String childType = inEvent.extractStrValue("OBJECTTYPE");
			if(childType.equalsIgnoreCase("Question")) {
				Object realCont = getRealParent();
				SingleQuestionPanel q_panel = new SingleQuestionPanel(((ReasonFrame)realCont).getCommonWidth());
				q_panel.setFrameListener(((PropertyChangeListener)realCont));
				q_panel.setImageBase(((ReasonFrame)realCont).getImageBase());
				try{
					setRealObjectProperties((Sharable)q_panel, inEvent);
				}catch (DorminException e) { throw e;}
				this.setRealObject(q_panel);
				q_panel.setFontSize(((ReasonFrame)realCont).getFontSize());
				q_panel.setProxyInRealObject(this);
				((ReasonFrame)realCont).addQuestion(q_panel);
			}
			else if(childType.equalsIgnoreCase("Row")) {
				((SingleQuestionPanel)getObject()).addRow();
				Vector proNames = null;
				try{
					proNames = inEvent.extractListValue("PROPERTYNAMES");
				} catch (DorminException e) { }
				if(proNames != null){
					int numRows = ((SingleQuestionPanel)getObject()).getNumOfRows();
					CommonObjectProxy child = (CommonObjectProxy)getContainedObjectBy("Row", "POSITION", String.valueOf(numRows));
					child.setProperty(inEvent);
				}
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
			SingleQuestionPanel ps = (SingleQuestionPanel)getObject();
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