package edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements;
import org.xml.sax.SAXException;

import com.megginson.sax.DataWriter;

/**
 * FocusOfAttention Element class.
 * This class holds information about pre-selected widgets
 * when Sim Student is activated.
 */
public class FocusOfAttentionElement{
	private String elementName = "focusOfAttention";
	private String target;
	
	
//	 print in XML format
	public void printXML(DataWriter w) throws SAXException {
		w.startElement(elementName);
		
		if(target != null)
			w.dataElement("target", target);
		
		w.endElement(elementName);
	}
	
	public void addTarget(String target){
		this.target = target;
	}
	
}