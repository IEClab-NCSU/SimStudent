package edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements;

import org.xml.sax.SAXException;

import com.megginson.sax.DataWriter;

public class MessageElement {
	
	private String elementName = "message";

	private String verb;
	private PropertiesElement prop;

	// print in XML format
	public void printXML(DataWriter w) throws SAXException {
		w.startElement(elementName);
		
			if (verb != null)   
				w.dataElement("verb", verb);
			
			if (prop != null)   
				prop.printXML(w);
										
		w.endElement(elementName);
	}
	
	
	// methods to add sub elements to Message	
	public void addverb(String in) {
		verb = in;
	}
	
	public void addproperties(PropertiesElement in) {
		prop = in;			
	}
}

