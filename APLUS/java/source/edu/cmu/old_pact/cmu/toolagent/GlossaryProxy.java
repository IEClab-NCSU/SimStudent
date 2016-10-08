//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/GlossaryProxy.java
package edu.cmu.old_pact.cmu.toolagent;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.dormin.ToolProxy;

public class GlossaryProxy extends ToolProxy {
	
	public GlossaryProxy(ObjectProxy parent) {
		 super(parent, "Glossary");
	}
	
	public GlossaryProxy(){
		super();
	}
	
	public  void create(MessageObject inEvent)  throws DorminException{
		try{
			String childType = inEvent.extractStrValue("OBJECTTYPE");
			if(childType.equalsIgnoreCase("GLOSSARY")) {
				Glossary glossary = new Glossary();
				this.setRealObject(glossary);
				glossary.setProxyInRealObject(this);
				setRealObjectProperties((Sharable)glossary, inEvent);
			}
			else
				super.create(inEvent);
		}catch(DorminException e) { 
			throw e; 
		} 
	}
}