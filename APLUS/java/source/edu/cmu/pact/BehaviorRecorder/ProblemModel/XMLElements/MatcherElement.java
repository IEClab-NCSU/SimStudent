package edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements;

import java.util.ArrayList;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.megginson.sax.DataWriter;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;

public class MatcherElement {
	private String elementName = "matcher";

	/** Local storage for {@link Matcher#getReplacementFormula()}. */
	private String replacementFormula = null;

	private String matcherType;
	
	/** List of {@link MatcherElement.MatcherParameter} instances. */
	private ArrayList matcherParameterList;
	
	public MatcherElement () {
		matcherParameterList = new ArrayList();
	}

	// print in XML format
	public void printXML(DataWriter w) throws SAXException {
		if (getReplacementFormula() != null) {
			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute("", "replacementFormula", "", "String", getReplacementFormula());
			w.startElement("", elementName, "", atts);
		} else {
			w.startElement(elementName);
		}
		if (matcherType != null)   
			w.dataElement("matcherType", matcherType);

		for (int i=0; i<matcherParameterList.size(); i++) {
			Matcher.MatcherParameter mp = (Matcher.MatcherParameter) matcherParameterList.get(i);
			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute("", "name", "", "String", mp.getName());
			w.dataElement("", "matcherParameter", "", atts, mp.getValue().toString());
		}
										
		w.endElement(elementName);
	}
	
	
	// methods to add sub elements to Matcher	
	public void addmatcherType(String in) {
		matcherType = in;
	}
	
	public void addmatcherParameter(String name, Object value) {
		matcherParameterList.add(new Matcher.MatcherParameter(name, value));
	}
	
	public void addmatcherParameter(Matcher.MatcherParameter p) {
		matcherParameterList.add(p);
	}

	/**
	 * @return the {@link #replacementFormula}
	 */
	public String getReplacementFormula() {
		return replacementFormula;
	}

	/**
	 * @param replacementFormula new value for {@link #replacementFormula}
	 */
	public void setReplacementFormula(String replacementFormula) {
		this.replacementFormula = replacementFormula;
	}
}// end of Matcher
