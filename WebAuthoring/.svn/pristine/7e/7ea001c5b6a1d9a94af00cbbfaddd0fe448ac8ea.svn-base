package servlet;
import interaction.Backend;
import interaction.SAI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to hold the start state xml to send back to the tutoring interface
 * @author Patrick Nguyen
 *
 */
public class StartStateResponse  extends ResponseMessage{
	private String startStateXML;
	private String brd;

	
	public String getBrd() {
		return brd;
	}
	public void setBrd(String brd) {
		this.brd = brd;
	}

	public StartStateResponse(){
		setMessageType("StateGraph");
		startStateXML = hardCodedFile();
	}
	public StartStateResponse(String questionFile){
		setMessageType("StateGraph");
		setStartStateXML(questionFile);
	}

	public StartStateResponse(String driveUrl, String driveToken){
		setMessageType("StateGraph");
		setStartStateXML(driveUrl, driveToken);
	}
	
	/**
	 * Extracts the initial interface actions in the brd
	 */
	public List<SAI> getStartStateInterfaceActions() {

		List<SAI> sais = new ArrayList<SAI>();
		int i;
		String brdCopy = brd;
		while((i = brdCopy.indexOf("<message>")) >= 0){

			int j = brdCopy.indexOf("</message>")+10;
			String message = brdCopy.substring(i,j);

			if (message.indexOf("<MessageType>InterfaceAction</MessageType>") != -1) {
				try {
					// can't parse interfaceactions that don't have input, which some brds might have
					// an interfaceaction without an input is invalid
					if (message.indexOf("<value></value>") == -1) {
						System.out.println("inital sai message: " + message);
						InterfaceActionRequest request = (InterfaceActionRequest) RequestParser.parseRequest(message);
						SAI sai = request.getSai();
						sais.add(sai);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			brdCopy = brdCopy.substring(j);
		}
		return sais;
	}

	/**
	 * Gets the start state xml
	 * @return startStateXML
	 */
	public String getStartStateXML() {

		return startStateXML;
	}

	/**
	 * Sets the start state xml
	 * @param startStateXML
	 */
	public void setStartStateXML(String questionFile) {
		System.out.println("1 arg setStartStateXML called"); 
		if (Utilities.isUrl(questionFile)) {
			try{
				URL url = new URL(questionFile);
				BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream()));
				String temp;
				while((temp=bf.readLine())!=null){
					brd+=temp.trim();
				}
				bf.close();
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
		} else {
			brd = questionFile.trim();
		}
		System.out.println("Here is the brd:");
//		System.out.println(brd);
		createStartStateMessage(brd);
	}

	public void setStartStateXML(String driveUrl, String driveToken){
		System.out.println("2 arg setStartStateXML called");
		String brd = "";
		try{
			URL url = new URL(driveUrl);
			HttpURLConnection  con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("GET");
			con.addRequestProperty("Authorization","Bearer "+driveToken);

			BufferedReader bfr = new BufferedReader(new InputStreamReader(con.getInputStream()));
			startStateXML = "";
			String temp;
			while((temp = bfr.readLine()) != null){
				brd += temp.trim();
			}
			bfr.close();
		}catch(Exception e){
			e.printStackTrace();
			return;
		}

		createStartStateMessage(brd);
	}

	private void createStartStateMessage(String brd){
		String tag = "<startNodeMessages>";
		
		int i1 = brd.indexOf(tag)+tag.length();
		tag = "</startNodeMessages>";
		int i2 = brd.lastIndexOf(tag);
		
		startStateXML = "<StartStateMessages>"+brd.substring(i1,i2) + "</StartStateMessages>";
//		Utilities.prettyPrintXMLAsString(startStateXML);
	}

	/**
	 * Method to package up the entire class into a single XML string.
	 * This is the message sent back to the tutoring interface.
	 * 
	 * @return xml representation of this object
	 */
	public String toXML()
	{
//		System.out.println(Utilities.prettyPrintXMLAsString(startStateXML));

		return startStateXML;
		//return hardCodedFile();
	}
	/*
	 * There is a hard coded response saved on disk used for demo purposes. This returns that response.
	 */
	private String hardCodedFile(){
		System.out.println("Getting Start State XML");
		String xml="";
		try(
				BufferedReader bfr = new BufferedReader(new FileReader(new File(ResponseMessage.staticFiles+"/start_state_bundle.txt")));
				){
			String temp="";
			while((temp=bfr.readLine())!=null)
				xml += temp;//we don't need to worry about readability
		}catch(Exception e)
		{
			xml="Start state io failed";
		}
		return xml;
	}


}
