package edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements;

import java.util.List;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.megginson.sax.DataWriter;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemStateWriter;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Matcher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.VectorMatcher;

public class VectorMatcherElement {
	
	protected String elementName = "matchers";
	
	private boolean concat;
	
	private VectorElement selection, action, input;
	
	private String actor;

	private boolean linkTriggered;
	
	public VectorMatcherElement(VectorMatcher vm)
	{
		this.concat = vm.isConcat();
		selection = new VectorElement(vm.getMatchers(VectorMatcher.SELECTION), "Selection");
		action = new VectorElement(vm.getMatchers(VectorMatcher.ACTION), "Action");
		input = new VectorElement(vm.getMatchers(VectorMatcher.INPUT), "Input");
		actor = vm.getDefaultActor();
		linkTriggered = vm.isLinkTriggered();
	}
	
	//print in XML format
	public void printXML(DataWriter w) throws SAXException {
		w.startElement("", elementName, "", getAttributes());
		
		selection.printXML(w);
		action.printXML(w);
		input.printXML(w);
		{
			AttributesImpl atts = new AttributesImpl();
			atts.addAttribute("", Matcher.TRIGGER_ATTR, "", "String", Boolean.toString(linkTriggered));
			w.startElement("", "Actor", "", atts);
			w.characters(actor);
			w.endElement("Actor");
		}
		w.endElement(elementName);
	}
	
	protected AttributesImpl getAttributes() {
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "Concatenation", "", "String", Boolean.toString(concat));
		return atts;
	}

	private class VectorElement
	{
		String elementName;
		List<Matcher> matchers;
		
		public VectorElement(List<Matcher> matchers, String elementName)
		{
			this.elementName = elementName;
			this.matchers = matchers;
		}
		
		public void printXML(DataWriter w) throws SAXException
		{
			w.startElement(elementName);
			
			for(int i = 0; i < matchers.size(); i ++)
				ProblemStateWriter.getMatcherElement(matchers.get(i)).printXML(w);
			
			w.endElement(elementName);
		}
	}
}