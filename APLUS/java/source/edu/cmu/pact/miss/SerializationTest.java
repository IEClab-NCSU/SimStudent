package edu.cmu.pact.miss;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.SimSt;

public class SerializationTest {
		
	public void testSerialization() throws IOException{ 
		
		
		
		String url = "http://kona.education.tamu.edu:2401/Servlet/storeObjects?cmd=retrieveObj&file=simst-VP.ser";
		URL servlet = new URL(url);
		HttpURLConnection servletConnection = (HttpURLConnection)servlet.openConnection();
		ObjectInputStream ois = null;

		try {

			 ois = new ObjectInputStream(servletConnection.getInputStream());
			 SimSt simstObj =(SimSt) ois.readObject();
			 trace.out(" SimSt : "+simstObj);
		} 
		catch (MalformedURLException e) {
			trace.err(e.toString());
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
			trace.err(e.toString());
	    } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
		trace.err(e.toString());
		}
		finally{
				if(ois != null)
					ois.close();
				else
					trace.out("No close");
			trace.out(" Response : "+servletConnection.getResponseMessage());
		}
		
		
	}	
		public void writeSerization(){
			String tutorArg="";
			tutorArg+=" -ssRunInPLE";
			tutorArg+=" -debugCodes miss";
			String[] argv = tutorArg.split(" ");
			
			CTAT_Launcher launcher = new CTAT_Launcher(argv);
		    SimSt obj = launcher.getFocusedController().getMissController().getSimSt();
			
			String url = "http://kona.education.tamu.edu:2401/Servlet/storeObjects?cmd=saveObj&file=simst-VP.ser";
			try{
				URL servlet = new URL(url);
				HttpURLConnection servletConnection = (HttpURLConnection)servlet.openConnection();
				servletConnection.setDoInput(true);
				servletConnection.setDoOutput(true);
				servletConnection.setUseCaches(false);
				servletConnection.setDefaultUseCaches(false);
				servletConnection.setRequestProperty("Content-Type", "application/octet-stream");
				ObjectOutputStream output = new ObjectOutputStream(servletConnection.getOutputStream());
				output.writeObject(obj);
				output.flush();
				output.close();
				trace.out(" Code : "+servletConnection.getResponseMessage());
			}
			catch(Exception e){
				trace.err(e.toString());
			}
					
		}
		
		
	

	public static void main(String[] args) {
		SerializationTest test = new SerializationTest();
		//test.writeSerization();
		try {
			test.testSerialization();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
