package edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;
import org.xml.sax.SAXException;

import com.megginson.sax.DataWriter;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;

public class VectorProperty {
	
	class ValueSubElement {
		private ArrayList<String> valuelist;
		
		public ValueSubElement() {
			valuelist = new ArrayList<String>();
		}
		
		public void addValue(String in) {
			if (valuelist == null)
				valuelist = new ArrayList<String>();
			
			valuelist.add(in);
		}
		
		public void printXML(DataWriter w) throws SAXException {
			for(int i=0; i<valuelist.size(); i++)
				w.dataElement(MessageObject.VALUE_TAG, valuelist.get(i));
		}
	}

	String propertyName;

	/** Items in a list of &lt;{@value MessageObject#VALUE_TAG}&gt; elements. */
	private ValueSubElement vseList;
	
	/** Items in an Element list. */
	private List<Element> eltList;
	
	public VectorProperty(String propertyName) {
		this(propertyName, null);
	}
	
	public VectorProperty(String propertyName, Vector propertyValue) {
		this.propertyName = propertyName;
		if (propertyValue == null || propertyValue.size() < 1) {
			return;
		} else if (propertyValue.get(0) instanceof Element) {
			vseList = null;
			eltList = new ArrayList<Element>();
			for (int k = 0; k < propertyValue.size(); k++) {
				Object elt = propertyValue.get(k);
				if (!(elt instanceof Element))
					trace.err("item["+k+"] in Element list-valued property not an Element:\n  "+
							propertyValue);
				else
					eltList.add((Element) elt);
			}
		} else {
			vseList = new ValueSubElement();
			eltList = null;
	        for (int k = 0; k < propertyValue.size(); k++) {
				String inputvalue = propertyValue.elementAt(k).toString();
				vseList.addValue(inputvalue);
	        }
		}
	}
	
	public void addValue(String in) {
		if (eltList != null) {
			trace.err("addValue(\""+in+"\") illegal when Element list already started");
			return;
		}
		if (vseList == null)
			vseList = new ValueSubElement();
		vseList.addValue(in);
	}
	
	public void addValue(Element elt) {
		if (vseList != null) {
			trace.err("addValue(\""+elt+"\") illegal when <value> list already started");
			return;
		}
		if (eltList == null)
			eltList = new ArrayList<Element>();
		eltList.add(elt);
	}

	public void printXML(DataWriter w) throws SAXException {
		w.startElement(propertyName);
		if (vseList != null)
			vseList.printXML(w);
		else if (eltList != null) {
			for (Element elt : eltList)
				PropertiesElement.printElement(elt, w);
		} else
			;     // empty element
		w.endElement(propertyName);
	}
}
