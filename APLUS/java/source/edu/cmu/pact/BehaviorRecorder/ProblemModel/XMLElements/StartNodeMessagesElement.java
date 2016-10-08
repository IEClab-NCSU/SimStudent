package edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements;
import java.util.ArrayList;

import org.xml.sax.SAXException;

import com.megginson.sax.DataWriter;


public class StartNodeMessagesElement {
	private String elementName = "startNodeMessages";

	private ArrayList messageList;
	
	public StartNodeMessagesElement() {
		messageList = new ArrayList();		
	}
	
	// print in XML format
	public void printXML(DataWriter w) throws SAXException {
		w.startElement(elementName);

		for (int i=0; i<messageList.size(); i++)   
			((MessageElement)messageList.get(i)).printXML(w);			

		w.endElement(elementName);
	}
	
	public void addmessage(MessageElement in) {
		messageList.add(in);
	}
}
