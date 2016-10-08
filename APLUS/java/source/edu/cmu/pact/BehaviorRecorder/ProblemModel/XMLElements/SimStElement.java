package edu.cmu.pact.BehaviorRecorder.ProblemModel.XMLElements;
import java.util.ArrayList;

import org.xml.sax.SAXException;

import com.megginson.sax.DataWriter;


public class SimStElement{
	private String elementName = "SimSt";
	private ArrayList foAList;
	
	public SimStElement(){
		foAList = new ArrayList();
	}
	
	
//	 print in XML format
	public void printXML(DataWriter w) throws SAXException {
		w.startElement(elementName);
		
		if (foAList != null){
			for(int i=0; i<foAList.size(); i++){
				FocusOfAttentionElement foA =
					(FocusOfAttentionElement)foAList.get(i);
				foA.printXML(w);
			}
		}
		
		w.endElement(elementName);
	}
	
	public void addFoA(FocusOfAttentionElement foA){
		foAList.add(foA);
	}
}