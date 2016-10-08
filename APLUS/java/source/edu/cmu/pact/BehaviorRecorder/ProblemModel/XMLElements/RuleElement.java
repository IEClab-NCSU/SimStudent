package edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements;
import org.xml.sax.SAXException;

import com.megginson.sax.DataWriter;


public class RuleElement {
	private String elementName = "rule";

	private String text;
	private String indicator;
	
	public RuleElement() {
	}
	
	// print in XML format
	public void printXML(DataWriter w) throws SAXException {
		w.startElement(elementName);
		
			if (text != null)   
				w.dataElement("text", text);
			
			if (indicator != null)   
				w.dataElement("indicator", indicator);
						
		w.endElement(elementName);
	}
	
	// methods to add sub elements to rule	
	public void addtext(String in) {
		text = in;
	}
	
	public void addindicator(String in) {
		indicator = in;
	}
	
}// end of rule
