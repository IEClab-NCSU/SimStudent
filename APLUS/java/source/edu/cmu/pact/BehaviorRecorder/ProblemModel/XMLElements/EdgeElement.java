package edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements;
import java.util.ArrayList;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.megginson.sax.DataWriter;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;


public class EdgeElement {
	private String elementName = ProblemEdge.ELEMENT_NAME;

	private ActionLabelElement actionLbl;
	private String preCheckedStatus;
	private ArrayList ruleslist;
	private String destID;
	private String sourceID;
	private String traversalCount;
	private ArrayList assocList;

	//added SimSt Element 
	private SimStElement simSt;
	
	public EdgeElement() {
		ruleslist = new ArrayList();
		assocList = new ArrayList();
	}
	
	// print in XML format
	public void printXML(DataWriter w) throws SAXException {
		w.startElement(elementName);
			if (actionLbl != null) 
				actionLbl.printXML(w);
			
			if (preCheckedStatus != null) 
				w.dataElement("preCheckedStatus", preCheckedStatus);
			
			for (int i=0; i<ruleslist.size(); i++)   
				((RuleElement)ruleslist.get(i)).printXML(w);
			
			if (sourceID != null)   
				w.dataElement("sourceID", sourceID);			
			
			if (destID != null)   
				w.dataElement("destID", destID);
						
			if (traversalCount != null)   
				w.dataElement("traversalCount", traversalCount);
			
			// added SimSt element
			if(simSt != null){
				simSt.printXML(w);
			}
			
			for (int i=0; i<assocList.size(); i++)   
				((element)assocList.get(i)).printXML(w);
			
		w.endElement(elementName);
	}
	
	// methods to add sub elements to edge
	public void addactionLabel(ActionLabelElement in) {		
		actionLbl = in;
	}
	
	public void addprecheckedStatus(String in) {
		preCheckedStatus = in;
	}
	
	public void addrule(String text, int indicator) {
		RuleElement tmp = new RuleElement();

		tmp.addindicator((new Integer(indicator)).toString());
		tmp.addtext(text);
		ruleslist.add(tmp);		
	}
	
	public void addsourceID(int in) {
		sourceID = new Integer(in).toString();
	}
	
	public void adddestID(int in) {
		destID = new Integer(in).toString();
	}
	
	public void addtraversalCount(int in) {
		traversalCount = new Integer(in).toString();
	}
	
	public void addassociation(String name, String value) {
		assocList.add(new element(name, value));
	}
	
	// add SimSt Element 
	public void addSimSt(SimStElement simSt){
		this.simSt = simSt;
	}
	
	class element {
		private String elementName = "element";
		
		private String attrname;
		private String attrvalue;
		
		element(String attrname, String attrvalue) {
			this.attrname = attrname;
			this.attrvalue = attrvalue;			
		}
		
		// print in XML format
		public void printXML(DataWriter w) throws SAXException {
			AttributesImpl atts = new AttributesImpl();
			if (attrname != null)
				atts.addAttribute("", "name", "", "String", attrname);
			if (attrvalue != null)
				atts.addAttribute("", "value", "", "String", attrvalue);
			
			w.dataElement("", elementName, "", atts, "");																	
		}		
	}// end of element
}// end of edge
