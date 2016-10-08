package edu.cmu.pact.BehaviorRecorder.CTATStartStateEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.cmu.pact.BehaviorRecorder.Dialogs.MergeMassProductionDialogTest;
import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATComponent;
import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATSerializable;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CTATComponentTest extends TestCase {

	public static Test suite() {
		return new TestSuite(CTATComponentTest.class);
	}

	/**
	 * Key is component type, value is set of elements to expect in
	 * {@link CTATSerializable.IncludeIn#none} output.
	 */
	private static Map<String,Set<String>> MinimalElementNames = new HashMap<String,Set<String>>(); 	
	static {
		MinimalElementNames.put("CTATImageButton", new HashSet<String>());  // empty set
	}
	
	/**
	 * Key is component type, value is set of elements to expect in
	 * {@link CTATSerializable.IncludeIn#sparse} output.
	 */
	private static Map<String,Set<String>> SparseElementNames = new HashMap<String,Set<String>>(); 	
	static {
		SparseElementNames.put("CTATImageButton",
				new HashSet<String>(Arrays.asList(new String[] {
						"normalName",
						"hoverName",
						"clickName",
						"disabledName"
				})));
	}
	
	/**
	 * Key is component type, value is set of elements to expect in
	 * {@link CTATSerializable.IncludeIn#full} output.
	 */
	private static Map<String,Set<String>> FullElementNames = new HashMap<String,Set<String>>(); 	
	static {
		FullElementNames.put("CTATImageButton",
				new HashSet<String>(Arrays.asList(new String[] {
						"ShowHintHighlight",
						"DisableOnCorrect",
						"tutorComponent",
						"labelText",
						"DrawBorder",
						"borderRoundness",
						"BorderColor",
						"FontFace",
						"FontSize",
						"TextColor",
						"FontBold",
						"FontItalic",
						"FontUnderlined",
						"TextAlign",
						"inspBackgroundColor",
						"normalName",
						"hoverName",
						"clickName",
						"disabledName"
				})));
	}

	private static final XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
	
	/** Root element. */
	private Element stateGraph;

	/**
	 * Reads the named file. Sets {@link #stateGraph} to root element.
	 * @param filename
	 * @throws Exception bad XML, etc.
	 */
	private void parseBRD(String filename) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document brd = builder.build(filename);
        stateGraph = brd.getRootElement();
	}
	
	public void testToStringElement() throws Exception {
		parseBRD("test/"+getClass().getName().replace(".", "/")+".brd");
		testToStringElement(CTATSerializable.IncludeIn.minimal, MinimalElementNames);
		testToStringElement(CTATSerializable.IncludeIn.sparse, SparseElementNames);
		testToStringElement(CTATSerializable.IncludeIn.full, FullElementNames);
	}
	
	public void testOldBRDFiles() throws Exception {
		parseBRD("test/"+getClass().getName().replace(".", "/")+"Old.brd");
		testToStringElement(CTATSerializable.IncludeIn.sparse, FullElementNames);
	}
	
	private void testToStringElement(CTATSerializable.IncludeIn includeIn, Map<String, Set<String>> mapToUse)
			throws Exception {

		Element startNodeMessages = stateGraph.getChild("startNodeMessages");
		for(Object obj : startNodeMessages.getChildren("message")) {

			CTATComponent comp = getComponentFromXML(obj);
			if(comp == null || mapToUse.get(comp.getClassType()) == null)
				continue;   // don't have a list of expected elements for this component type
			
			assertNotNull("component name is null", comp.getInstanceName());
			System.out.printf("x=%f y=%f width=%f height=%f\n", comp.getX(), comp.getY(), comp.getWidth(), comp.getHeight());
			assertTrue("component x-coordinate "+comp.getX()+" is unset", 0.1 < comp.getX());
			assertTrue("component y-coordinate "+comp.getY()+" is unset", 0.1 < comp.getY());
			assertTrue("component width "+comp.getWidth()+" is unset", 0.1 < comp.getWidth());
			assertTrue("component height "+comp.getHeight()+" is unset", 0.1 < comp.getHeight());
			
			// make a copy to support remove() below
			Set<String> eltNamesExpected = new HashSet<String>(mapToUse.get(comp.getClassType()));

			Element outElt = comp.toStringElement(includeIn);  // transform by filter includeIn
			xmlOut.output(outElt, System.out);
			System.out.println(); System.out.println();
			
			List<Element> eltsToTest = new ArrayList<Element>();
			
			List<Element> paramOrStyleElts = (List<Element>) outElt.getChildren();
			for(Element paramOrStyleElt : paramOrStyleElts) {
				Element selection = paramOrStyleElt.getChild("selection");
				if(selection != null)
					eltsToTest.addAll((List<Element>) selection.getChildren());
			}

			for(Element elt : eltsToTest) {
				String name = elt.getChildText("name");
				assertTrue("Should not find "+name+" in "+includeIn+" output",
						eltNamesExpected.remove(name));
			}
			assertTrue("Did not find "+eltNamesExpected+" in "+includeIn+" output",
					eltNamesExpected.isEmpty());
		}
	}

	private CTATComponent getComponentFromXML(Object obj) {
		if(!(obj instanceof Element))
			return null;
		MessageObject mo = MessageObject.fromElement((Element) obj);
		if(!MsgType.INTERFACE_DESCRIPTION.equalsIgnoreCase(mo.getMessageType()))
			return null;

		Element compElt = (Element) mo.getProperty("serialized");
		CTATComponent comp = new CTATComponent();
		comp.fromXML(compElt);
		return comp;
	}
}
