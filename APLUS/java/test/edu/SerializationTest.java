package edu;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.ProblemAssessor;
import edu.cmu.pact.miss.SimSt;
import junit.framework.TestCase;

public class SerializationTest extends TestCase {
	private CTAT_Launcher launcher ;
	public SerializationTest(String classname) {
		super(classname);
	}
	public void setUp() {
		
		
		// instiatinate the SImSt object 
		System.out.println("setup");
	}
	
	
	public void testSerialization() { 
		String tutorArg="";
		tutorArg+=" -ssRunInPLE";
		tutorArg+=" -debugCodes miss";
		String[] argv = tutorArg.split(" ");
		
		
		
		
		
		String url = "http://kona.education.tamu.edu:2401/Servlet/stores?cmd=retrieveObj&file=simst-POST5.ser";
		try {
			URL servlet = new URL(url);
			URLConnection servletConnection = servlet.openConnection();
			
			servletConnection.setUseCaches(false);
			servletConnection.setDefaultUseCaches(false);
			ObjectInputStream ois = new ObjectInputStream(servletConnection.getInputStream());
			System.out.println(" OutputStream : "+ois);
		    if(ois != null)
				{
				   Object obj = ois.readObject();
				   ois.close();
				   SimSt simstObj = (SimSt)obj;
				   System.out.println(" SimSt : "+simstObj);
				   //launcher.getFocusedController().getMissController().getSimSt().loadInstnDeSerialize(simstObj);

				}
		    			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			trace.err(e.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		
		
		
		
	}

	public static void main(String[] args) {
		TestCase test = new SerializationTest("testSerialization");
		test.run();
	}
}
