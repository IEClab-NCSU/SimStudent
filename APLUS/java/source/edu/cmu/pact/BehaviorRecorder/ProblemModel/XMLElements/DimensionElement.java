package edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements;
import org.xml.sax.SAXException;

import com.megginson.sax.DataWriter;

public class DimensionElement {
	private String elementName = "dimension";
	
	private String x;
	private String y;
	private String width;
	private String height;
	
	public DimensionElement() {
		
	}

	// print in XML format
	public void printXML(DataWriter w) throws SAXException {
		w.startElement(elementName);
		
			if (x != null)   
				w.dataElement("x", x);
			
			if (y != null)   
				w.dataElement("y", y);

			if (width != null)   
				w.dataElement("width", width);
			
			if (height != null)   
				w.dataElement("height", height);
			
		w.endElement(elementName);
	}
	
	// methods to add sub elements to dimension	
	public void addx(int in) {		
		x = new Integer(in).toString();
	}
		
	public void addy(int in) {
		y = new Integer(in).toString();
	}
	
	public void addwidth(int in) {
		width = new Integer(in).toString();
	}
	
	public void addheight(int in) {
		height = new Integer(in).toString();
	}
}
