package edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements;
import java.util.ArrayList;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.megginson.sax.DataWriter;

public class ProductionRuleElement {
	
	public static final String PRODUCTION_RULE_ELEMENT_NAME = "productionRule";

	/** Number of times student has an opportunity to demonstrate this skill while solving. */
	private Integer opportunityCount;
	private String ruleName;
	private String productionSet;
	private String label;
	private String description;
	private ArrayList hintMessageList;
	
	public ProductionRuleElement() {
		hintMessageList = new ArrayList();		
	}
	
	// print in XML format
	public void printXML(DataWriter w) throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		if (opportunityCount != null && opportunityCount.intValue() > 0)
			atts.addAttribute("", "opportunities", "", "Integer", opportunityCount.toString());
		w.startElement("", PRODUCTION_RULE_ELEMENT_NAME, "", atts);

		if (ruleName != null)   
			w.dataElement("ruleName", ruleName);

		if (productionSet != null)   
			w.dataElement("productionSet", productionSet);

		if (label != null && label.length() > 0)
			w.dataElement("label", label);

		if (description != null && description.length() > 0)
			w.dataElement("description", description);

		for (int i=0; i<hintMessageList.size(); i++)   
			w.dataElement("hintMessage", (String)hintMessageList.get(i));			

		w.endElement(PRODUCTION_RULE_ELEMENT_NAME);
	}
	
	// methods to add sub elements to productionRule	
	public void addruleName(String in) {
		ruleName = in;
	}
	
	public void addproductionSet(String in) {
		productionSet = in;
	}
	
	public void addLabel(String in) {
		label = in;
	}
	
	public void addDescription(String in) {
		description = in;
	}
	
	public void addhintMessage(String in) {
		hintMessageList.add(in);
	}

	/**
	 * @param opportunityCount new value for {@link #opportunityCount}
	 */
	void setOpportunityCount(Integer opportunityCount) {
		this.opportunityCount = opportunityCount;
	}
}
