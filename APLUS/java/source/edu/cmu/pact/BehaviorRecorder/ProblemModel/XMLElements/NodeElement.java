package edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.megginson.sax.DataWriter;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;

public class NodeElement {
	private String elementName = ProblemNode.ELEMENT_NAME;

	private String attrlocked;
	private String attrdoneState;
	
	private String text;
	private String uniqueID;
	private DimensionElement dimen;	
	
	public NodeElement () {
		
	}

	// print in XML format
	public void printXML(DataWriter w) throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		if (attrlocked != null)
			atts.addAttribute("", "locked", "", "String", attrlocked);
		if (attrdoneState != null)
			atts.addAttribute("", "doneState", "", "String", attrdoneState);
		
		w.startElement("", elementName, "", atts);	
			if (text != null)   
				w.dataElement("text", text);
		
			if (uniqueID != null)   
				w.dataElement("uniqueID", uniqueID);
			
			if (dimen != null)   
				dimen.printXML(w);
										
		w.endElement(elementName);
	}
	
	// methods to add sub elements to Message	
	public void addtext(String in) {
		text = in;
	}
		
	public void adduniqueID(int in) {
		uniqueID = new Integer(in).toString();
	}
	
	public void addlocked(boolean in) {
		attrlocked = (new Boolean(in)).toString();
	}
	
	public void adddoneState(boolean in) {
		attrdoneState = (new Boolean(in)).toString();;
	}
	
	public void adddimension(int x, int y) {
		if (dimen == null) {
			dimen = new DimensionElement();
		}
		dimen.addx(x);		
		dimen.addy(y);		
	}	
	
}
