package servlet;
import interaction.Color;
import interaction.InterfaceAttribute;
import interaction.SAI;
import interaction.InterfaceAttribute.Style;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Utility class for creating RequestMessage objects from post requests
 * @author Patrick Nguyen
 *
 */
public class RequestParser {
	public static RequestMessage[] parseMultipleRequests(String request){
		if(request.indexOf("<MessageBundle>") < 0){
			RequestMessage rm = parseRequest(request);
			return rm == null? null : new RequestMessage[]{rm};
		}
		
		request = request.substring(15,request.length()-16);
		List<String> list = new LinkedList<String>();
		while(request.indexOf("<message>") >= 0){
			int i = request.indexOf("</message>")+10;
			list.add(request.substring(0,i));
			request = request.substring(i);
		}
		RequestMessage[] rms = new RequestMessage[list.size()];
		int i = 0;
		for(String str : list)
			rms[i++] = parseRequest(str);
		return rms;
	}
	
	/**
	 * Takes in a post request and returns some kind of RequestMessage to fit the request.
	 * 
	 * We grep on unique, identifying tags to see what kinds of messages they are and from
	 * there create the correct objects. If it's a log message we go straight to creating the 
	 * object, and otherwise we attempt to parse the xml. The xml parsing makes some assumptions; see
	 * helper functions for details. If the xml goes wrong, we return null. Otherwise, the values 
	 * go into a map, whose key-value pairs are then used to create the request object.  
	 * @param request
	 */
	public static RequestMessage parseRequest(String request)
	{
//		 System.out.println("Parsing request");
		 //log messages are kept intact
		 if(request.indexOf("<log_act") != -1 || request.indexOf("<log_ses") != -1)
			 return createLogMessageRequest(request);
		 
		 
		 Document d = Utilities.loadXMLFromString(request);
		 try {
			System.out.println("printing doc");
			Utilities.printDocument(d, System.out);
		} catch (IOException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if(d==null) return null;//not valid xml
		 Map<String,XMLValue> values = parseXML(d);
		 
		 
		 
		 
		 if(request.indexOf("<MessageType>SetPreferences</MessageType>") != -1){
			 return createSetPreferencesRequest(values);
		 }
		 if(request.indexOf("<MessageType>InterfaceAction</MessageType>") != -1){
			 System.out.println("InterfaceAction request: " + request);
			 NodeList v = d.getElementsByTagName("value");
			 System.out.println("values from doc: " + v.getLength());
            return createInterfaceActionRequest(values);
		 }
		 if(request.indexOf("<MessageType>InterfaceAttribute</MessageType>") != -1){
			 return createInterfaceAttribute(values);
		 }
		System.out.println("XML unrecognized");
		return null;//xml unrecognized
	}

	private static RequestMessage createSetPreferencesRequest(Map<String,XMLValue> values)
	{
		System.out.println("Request of type setPreferences");
		System.out.println("**********************************");
		 System.out.println("XML elements are: ");
		 for(String k : values.keySet()){
			 XMLValue val= values.get(k);
			 String s;
			 if(val.stringVal != null) s=val.stringVal;
			 else if(val.stringList != null){
				 s="[";
				 for(int x=0; x<val.stringList.size();x++)
					 s+=val.stringList.get(x)+", ";
				 if(s.length()>1)s=s.substring(0,s.length()-2);
				 s+="]";
			 }
			 else s="Node";
//			 System.out.println(k+": "+s);
		 }		 
		 System.out.println("**********************************");
		SetPreferenceRequest pref = new SetPreferenceRequest();
		
		//we protect from nulls, but I don't think the request should ever have null values
		if(values.get("MessageType")!=null)
			pref.setMessageType(values.get("MessageType").stringVal);
		if(values.get("log_service_url")!=null)
			pref.setLogServiceUrl(values.get("log_service_url").stringVal);
		if(values.get("log_to_remote_server")!=null)
			pref.setLogToRemoteServer(Boolean.parseBoolean(values.get("log_to_remote_server").stringVal));
		if(values.get("log_to_disk")!=null)
			pref.setLogToDisk(Boolean.parseBoolean(values.get("log_to_disk").stringVal));
		if(values.get("log_to_disk_directory")!=null)
			pref.setLogToDiskDirectory(values.get("log_to_disk_directory").stringVal);
		if(values.get("user_guid")!=null)
			pref.setUserGuid(values.get("user_guid").stringVal);
		if(values.get("problem_name")!=null)
			pref.setProblemName(values.get("problem_name").stringVal);
		if(values.get("question_file")!=null)
			pref.setQuestionFile(values.get("question_file").stringVal);
		//Added by Shruti - to parse the Argument values
		if(values.get("Argument")!=null)
			pref.setArgument(values.get("Argument").stringVal);
		if(values.get("class_name")!=null)
			pref.setClassName(values.get("class_name").stringVal);
		if(values.get("school_name")!=null)
			pref.setSchoolName(values.get("school_name").stringVal);
		if(values.get("session_id")!=null)
			pref.setSessionId(values.get("session_id").stringVal);
		if(values.get("source_id")!=null)
			pref.setSourceId(values.get("source_id").stringVal);
		if(values.get("source_id")!=null)
			pref.setSui(values.get("sui").stringVal);
		if(values.get("problem_state_status")!=null)
			pref.setProblemStateStatus(values.get("problem_state_status").stringVal);
		if(values.get("skills")!=null)//Node object, must parse manually
			setSkills(pref,values.get("skills").element);
		if(values.get("CommShellVersion")!=null)
			pref.setCommShellVersion(values.get("CommShellVersion").stringVal);
		if(values.get("back_dir")!=null)
			pref.setBackendDirectory(values.get("back_dir").stringVal);
		if(values.get("back_entry")!=null)
			pref.setBackendEntryClass(values.get("back_entry").stringVal);
		if(values.get("drive_url")!=null)
			pref.setDriveUrl(values.get("drive_url").stringVal);
		if(values.get("drive_token")!=null)
			pref.setDriveToken(values.get("drive_token").stringVal);
		if(values.get("wmes")!=null)
			pref.setWmes(values.get("wmes").stringList);
		return pref;
	}
	
	private static InterfaceActionRequest createInterfaceActionRequest(Map<String,XMLValue> values)
	{
		System.out.println("Request of type interfaceAction");
		//we protect from nulls, but I don't think the request should ever have null values
		//certainly, if the SAI is null this program deserves to crash
		List<String> selection=values.get("Selection").stringList;
		List<String> action=values.get("Action").stringList;
		List<String> input=values.get("Input").stringList;
		System.out.println("selections: " + selection);
		System.out.println("actions: " + action);
		System.out.println("inputs: " + input);
		for (XMLValue value : values.values()) {
			System.out.println("values: " + value.stringVal);
		}
		InterfaceActionRequest request = new InterfaceActionRequest(new SAI(selection,action,input));
		if(values.get("MessageType")!=null)
			request.setMessageType(values.get("MessageType").stringVal);
		if(values.get("transaction_id")!=null)
			request.setTransactionId(values.get("transaction_id").stringVal);
		if(values.get("session_id")!=null)
			request.setSessionId(values.get("session_id").stringVal);
		return request;
	}
	private static InterfaceAttribute createInterfaceAttribute(Map<String,XMLValue> values)
	{
//		System.out.println("Request of type InterfaceAttribute");
		InterfaceAttribute ia = new InterfaceAttribute(values.get("component").stringVal);
		if(values.get("session_id")!=null)
			ia.setSessionId(values.get("session_id").stringVal);
		if(values.get("background_color") != null)
			ia.setBackgroundColor(Color.parseCSS(values.get("background_color").stringVal));
		if(values.get("border_color") != null)
			ia.setBorderColor(Color.parseCSS(values.get("border_color").stringVal));
		if(values.get("border_style")!=null){
			String style = values.get("border_style").stringVal;
			switch(style){
				case "hidden":
					ia.setBorderStyle(Style.HIDDEN);
					break;
				case "dotted":
					ia.setBorderStyle(Style.DOTTED);
					break;
				case "dashed":
					ia.setBorderStyle(Style.DASHED);
					break;
				case "solid":
					ia.setBorderStyle(Style.SOLID);
					break;
				case "double":
					ia.setBorderStyle(Style.DOUBLE);
					break;
			}
		}
		if(values.get("border_width") != null){
			String val = values.get("border_width").stringVal;
			val = val.substring(0,val.length()-2);//stripping away "px"
			ia.setBorderWidth(Integer.parseInt(val));
		}
		if(values.get("enabled") != null)
			ia.setIsEnabled(Boolean.parseBoolean(values.get("enabled").stringVal));
		if(values.get("font_color") != null)
			ia.setFontColor(Color.parseCSS(values.get("font_color").stringVal));
		if(values.get("font_size") != null)
			ia.setFontSize(Integer.parseInt(values.get("font_size").stringVal));
		if(values.get("height")!=null)
			ia.setHeight(Integer.parseInt(values.get("height").stringVal));
		if(values.get("hint_highlight")!=null)
			ia.setIsHintHighlight(Boolean.parseBoolean(values.get("hint_highlight").stringVal));
		if(values.get("width") != null)
			ia.setWidth(Integer.parseInt(values.get("width").stringVal));
		if(values.get("x_coor") != null)
			ia.setX(Integer.parseInt(values.get("x_coor").stringVal));
		if(values.get("y_coor") != null)
			ia.setY(Integer.parseInt(values.get("y_coor").stringVal));
		ia.getModifications().clear();
		return ia;
	}
	private static LogMessageRequest createLogMessageRequest(String request)
	{
		//does not take in a map but the direct request
		//not sure if we need to remove xml header
		/*if(request.indexOf("<?") != -1)//get rid of xml header
		{
			int x = request.indexOf("?>");
			request=request.substring(x+2);
		}*/
		return new LogMessageRequest(request);
	}
	
	/*
	 * This is where we enter into parsing the XML. We make the assumption that the XML is wrapped around
	 * <message></message> tags, and that the only child tag that matters is the <properties> tag. Therefore,
	 * we go down one level and parse the <properties> tag only.
	 */
	private static Map<String,XMLValue> parseXML(Document doc)
	{
		Map<String,XMLValue> map = new HashMap<String,XMLValue>();
		Node message = doc.getFirstChild();//doc itself contains nothing of interest
		//we expect the first and only child to be <message>
		Node c = message.getFirstChild();//iterate through children until we find <properties>
		if(c==null) return null;
		while(!c.getNodeName().equals("properties")){
			c=c.getNextSibling();
			if(c==null) return null;
		}
		parsePropertiesNode(c,map);//add everything into the map
		return map; 
	}
	
	/*
	 * This is where we actually parse the xml, technically just the <properties>. This code assumes that all children
	 * are either "scalars","vectors",or xml elements.
	 * Scalars: Children in the form <tag>text</tag>. This is a mapping from a string to another string.
	 * Vectors: Children in the form <tag><value>text1</value><value>text2</value>...</tag>. This is a mapping from a 
	 * 		    string to a list of strings (we preserve the order). Note that <value></value> indicates a single string
	 * Elements: Children not scalars or vectors. These are rare and can vary, so in placing them in the map we simply 
	 * 			 preserve the node itself and do any further parsing in the createXRequest methods, if necessary.
	 */
	private static void parsePropertiesNode(Node node, Map<String,XMLValue> values)
	{
		//iterate through children to put their value into the map
		for(Node prop=node.getFirstChild(); prop!=null; prop=prop.getNextSibling()){
			if(prop.getChildNodes().getLength()==0) continue;//no value
			String name = prop.getNodeName();//the key
			Node child = prop.getFirstChild();
			
			if(child.getNodeType()==Node.TEXT_NODE){//scalar
				XMLValue val = new XMLValue();//value
				val.stringVal=child.getNodeValue();
				values.put(name, val);
			}
			else if(child.getNodeName().equalsIgnoreCase("value")){//vector
				System.out.println("found value");
				List<String> list = new ArrayList<String>();
				for(Node c = child; c != null; c=c.getNextSibling()){
					Node textNode = c.getFirstChild();
					System.out.println(textNode.getNodeValue());
					if(textNode.getNodeType() != Node.TEXT_NODE) System.out.println("Something wrong at parsing of: "+name);
					list.add(textNode.getNodeValue());
				}
				XMLValue val = new XMLValue();//value
				val.stringList = list;
				values.put(name,val);
			}
			else{//element
				XMLValue val = new XMLValue();//value
				val.element = child;
				values.put(name,val);
			}
		}
		
	}
	
	/*
	 * Skills is an xml element child, so we must parse it manually. In this case, it consists of several skill xml 
	 * elements, each with their own attributes. These attributes make up their own object, so to speak. We directly set the
	 * skills and place them into pref instead of using an intermediary map because it would complicate the
	 * map otherwise. 
	 */
	private static void setSkills(SetPreferenceRequest pref, Node node){
		//SetPreferenceRequest has its own Skill class to hold this information
		List<SetPreferenceRequest.Skill> list = new ArrayList<SetPreferenceRequest.Skill>();
		for(Node c = node.getFirstChild(); c != null; c = c.getNextSibling()){
			SetPreferenceRequest.Skill skill= new SetPreferenceRequest.Skill();
			NamedNodeMap map = c.getAttributes();
			skill.setLabel(map.getNamedItem("label").getFirstChild().getNodeValue());
			skill.setpSlip(map.getNamedItem("pSlip").getFirstChild().getNodeValue());
			skill.setDescription(map.getNamedItem("description").getFirstChild().getNodeValue());
			skill.setpKnown(map.getNamedItem("pknown").getFirstChild().getNodeValue());
			skill.setCategory(map.getNamedItem("category").getFirstChild().getNodeValue());
			skill.setpLearn(map.getNamedItem("pLearn").getFirstChild().getNodeValue());
			skill.setName(map.getNamedItem("name").getFirstChild().getNodeValue());
			skill.setpGuess(map.getNamedItem("pGuess").getFirstChild().getNodeValue());
			list.add(skill);
		}
		pref.setSkills(list);
	}
	
	/* Class to hold the values taken from the xml, since they can be one and only one of 3 different things */
	private static class XMLValue{
		String stringVal;//scalar
		List<String> stringList;//vector
		Node element;//left for further parsing later
	}
	
}
