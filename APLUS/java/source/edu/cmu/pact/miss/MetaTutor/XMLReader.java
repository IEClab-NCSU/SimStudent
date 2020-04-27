package edu.cmu.pact.miss.MetaTutor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.SimSt;

public class XMLReader {

	private static final long serialVersionUID = 1L;

	/**	File name containing the messages associated with the production-rules  */
	public static final String PR_MESSAGES_FILE = "prMsgs.xml";
	public static final String PR_MESSAGESCOG_FILE = "prMsgsCognitive.xml";
	public static final String PR_MESSAGESMETACOG_FILE = "prMsgsMetaCognitive.xml";
	public static final String PR_MESSAGESAPLUSCTRL_FILE = "prMsgsAplusControl.xml";

	
	/**	 */
	private String prMsgsFile = "";
	
	private static final Pattern rulePrefix = Pattern.compile("^[Mm][Aa][Ii][Nn][:][:]");
	
	/**	 */
	private DocumentBuilderFactory docBuilderFactory;
	private DocumentBuilder docBuilder;
	private Document doc;
	
	/**	SimSt instance included to have a reference to the project directory */
	private SimSt simSt;

	public SimSt getSimSt() {
		return simSt;
	}

	public void setSimSt(SimSt simSt) {
		this.simSt = simSt;
	}

	
	public XMLReader() {
		this(null);
	}
	
	public XMLReader(SimSt ss) {
		simSt = ss;
		init();
	}

	public XMLReader(SimSt ss, String path) {
		simSt = ss;
		
		prMsgsFile = path;
		init();
	}
	
	void init(){
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			String mode=simSt.getSsMetaTutorModeLevel();
			String actualFile=PR_MESSAGES_FILE;
			if (mode.equals(SimSt.METACOGNITIVE))
				actualFile=PR_MESSAGESMETACOG_FILE;
			else if (mode.equals(SimSt.COGNITIVE))
				actualFile=PR_MESSAGESCOG_FILE;
			
			if (simSt.isSsAplusCtrlCogTutorMode())
				actualFile=PR_MESSAGESAPLUSCTRL_FILE;
				
			//actualFile=this.PR_MESSAGESCOG_FILE;
			
			if(prMsgsFile.isEmpty())
				doc = docBuilder.parse(new DataInputStream(new FileInputStream(new File
						(simSt.getProjectDir(), actualFile/*PR_MESSAGES_FILE*/))));
			else {
				doc = docBuilder.parse(new DataInputStream(new FileInputStream(new File
						(simSt.getProjectDir(), prMsgsFile))));
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(doc != null) {
			doc.getDocumentElement().normalize();
		}
	}
	
	/**
	 * @return
	 */
	public void parseXMLFile(String ruleName, ArrayList<ArrayList<String>> list) {
		//System.out.println(" Rule Name : "+ruleName);
		NodeList messageSet = doc.getElementsByTagName("message-set");
		//System.out.println(messageSet.getLength());
		for(int i=0; i < messageSet.getLength(); i++) {
			
			Node node = messageSet.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				
				Element message = (Element) node;
				NodeList nameList = message.getElementsByTagName("name");
				Element name = (Element) nameList.item(0);

				ruleName = ruleName.replaceAll("&\\d$", "");
				if ((ruleName.replaceAll("^[Mm][Aa][Ii][Nn][:][:]", "")).equals(
						((Node) name.getChildNodes().item(0)).getNodeValue().trim())) {

					NodeList menuOptionList = message.getElementsByTagName("menu-option");
					Element menuOption = (Element) menuOptionList.item(0);

					for (int j = 0; j < menuOption.getChildNodes().getLength(); j++) {
						
						Node option = menuOption.getChildNodes().item(j);
						if (option instanceof Element) {
							
							Element childElement = (Element) option;
							Text text = (Text) childElement.getFirstChild();
							String msg = text.getData().trim();
							setMenuOptionMessage(msg, list);
						}
					}
				}
			}
		}
		
	}

	
	
	private void setMenuOptionMessage(String msg, ArrayList<ArrayList<String>> list) {
		//System.out.println(" Message : "+msg);
		if(msg != null)
			msg = msg.trim();
		if(msg.length() > 0) {
			//TODO add the menuOption msg here
			// msg format is "CognitiveHint : Hint msg"
			String[] tokens = msg.split(":");
			if(tokens.length == 2){
				ArrayList<String> msgContent = new ArrayList<String>();
				msgContent.add(tokens[0]);
				tokens[1] = resolveWMValues(tokens[1]);
				msgContent.add(tokens[1]);
				list.add(msgContent);
				//simSt.getBrController().getAmt().setMenuOptionMessages(list);
			} else {
				trace.err("Format of menuMessages is incorrect");
			}
		}
	}
	
	public static String resolveWMValues(String msg) {
		
		char c = '$';
		int beginIndex, endIndex;
		String value = null, field = null;
		
		beginIndex = msg.indexOf(c);
		if(beginIndex == -1) {
			return msg;
		} else {
			endIndex = msg.indexOf(c, beginIndex+1);
			if(endIndex == -1) {
				return msg;
			} else {
				try {
					field = msg.substring(beginIndex+1, endIndex);
					msg = msg.replaceAll("\\$", "");
					msg = msg.replace(field, SimSt.SimStName);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
		
		return msg;
	}

}
