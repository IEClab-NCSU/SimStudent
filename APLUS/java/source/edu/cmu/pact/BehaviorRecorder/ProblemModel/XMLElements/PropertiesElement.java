package edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.megginson.sax.DataWriter;

import edu.cmu.pact.Utilities.trace;


public class PropertiesElement {

	private String elementName = "properties";
	
	private ArrayList<PropertyPair> properties = new ArrayList<PropertyPair>();
	
    private class PropertyPair {
        private String propertyName;
        private Object propertyValue;
        public PropertyPair (String propertyName, Object propertyValue) {
            this.propertyName = propertyName;
            this.propertyValue = propertyValue;
        }
        public String getPropertyName() {
            return propertyName;
        }
        public Object getPropertyValue() {
            return propertyValue;
        }
    }
	public void addProperty(String properyName, Object obj) {
		if (!(obj instanceof Element || obj instanceof VectorProperty || obj instanceof String )) {
			trace.out(5, this, "invalid object type.");
			return;
		}
			
		properties.add(new PropertyPair(properyName, obj));
	}
	
	
	// print in XML format
	public void printXML(DataWriter w) throws SAXException {
		w.startElement(elementName);
				
		Iterator<PropertyPair> propertyPairs = properties.iterator();
		

        while (propertyPairs.hasNext()) {
            PropertyPair propertyPair = propertyPairs.next();
            
            String name = propertyPair.getPropertyName();
            Object obj = propertyPair.getPropertyValue();
			
            if (obj instanceof Element) {
            	w.startElement(name);
            	printElement((Element) obj, w);
            	w.endElement(name);
            } else if (obj instanceof String)
				w.dataElement(name, (String) obj);
			else if (obj instanceof VectorProperty) {
				VectorProperty objV = (VectorProperty)obj;
				objV.printXML(w);
			}
        }
		
		w.endElement(elementName);
	}

	/**
	 * For converting embedded XML to strings.
	 */
	private static XMLOutputter outputter =
		new XMLOutputter(
			    Format.getCompactFormat().setOmitDeclaration(true).setLineSeparator("\n").setIndent("  ")
		);

	/**
	 * Translate (!) a JDom {@link Element} for output by a {@link DataWriter}.
	 * @param elt
	 * @param w
	 */
	static void printElement(Element elt, DataWriter w) {
		if (elt == null)
			return;
		try {
			List<Attribute> attList = (List<Attribute>) elt.getAttributes();
			AttributesImpl atts = new AttributesImpl();
			for (Attribute att : attList)
				atts.addAttribute("", att.getName(), "", "String", att.getValue());
			w.startElement("", elt.getName(), "", atts);
			String text = elt.getText();
			if (text.trim().length() > 0)
				w.characters(text);
			List<Element> children = (List<Element>) elt.getChildren();
			for (Element child : children)
				printElement(child, w);
			w.endElement(elt.getName());
		} catch (SAXException se) {
			String eltXML = outputter.outputString(elt);
			throw new RuntimeException("error converting Element "+eltXML, se);
		}
	}
}
