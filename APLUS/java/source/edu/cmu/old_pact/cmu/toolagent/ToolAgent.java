//d:/Pact-CVS-Tree/Tutor_Java/./src/Decimal/Pc/Interface_with_tool_2/ToolAgent.java
package edu.cmu.old_pact.cmu.toolagent;

/**
* args[] settings:
*	1. 	"NoLisp"	 default: useLisp 	 = true;
*	2. 	"DoDebug"	 default: doDebug 	 = false;
*	3.	"AutoLogin"	 default: autoLogin 	 = false; Must provide userName after this parameter.
*	4. 	"UserName"	 default: userName 	 = ""; 
*	5.	"ShowMessages"   default: showMessages   = false;
*	6.	"ShowUserLogin"  default: false
*   7.      "ControlledLisp" default: controlledLisp = false;
*   8.      "remote"         default: theHost        = "127.0.0.1";
*                                          portNum        = 1001;
**/ 

import java.applet.Applet;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import edu.cmu.old_pact.cl.utilities.Startable.Startable;
import edu.cmu.old_pact.dormin.Communicator;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.StringTarget;
import edu.cmu.pact.Utilities.trace;

public class ToolAgent extends Applet implements Startable, Agentable{

	private  static boolean isWrapped = true;
	static boolean useLisp = true;
    static boolean controlledLisp = false;
	//static boolean doDebug = false;
	static boolean autoLogin = false;
	static boolean showMessages = true;
	static boolean showUserLogin = false;
	private static String FileDir="", userName="";
	private StudentInterface studentInterface = null;
	private static Hashtable Properties;
	private static URL thisURL;
	private static String theHost= "127.0.0.1";
	private boolean finished = false;
    //static int portNum = 1001;
    static int portNum = Startable.DEFAULT_PORT;
	LispJavaConnection connection;
	Communicator toolCommunicator;
	InterfaceProxy tool_proxy;
	private static boolean createLispOutput;
			
	public void init() {
		trace.out (5, this, "initializing tool agent");
		// very useful on PC
		redirectOutput();
		
		
		Properties = new Hashtable();
		setProperty("isApplication", (new Boolean(true)));
		
		trace.out (5, this, "2");
		// Tool top level 		
		studentInterface = new StudentInterface("StudentInterface");
		trace.out (5, this, "3");
		tool_proxy = createInterfaceProxy();
		trace.out (5, this, "4");
		studentInterface.setProxyInRealObject(tool_proxy);
 		tool_proxy.setRealObject(studentInterface);
		//studentInterface.createInterfaceProxy();
		//tool_proxy = (InterfaceProxy)studentInterface.getObjectProxy();
		tool_proxy.setAgent(this);
		//studentInterface.setProxyInRealObject(tool_proxy);
		//tool_proxy.setRealObject(studentInterface);
		toolCommunicator = new Communicator(tool_proxy);
		trace.out (5, this, "2");
		FileDir = "file:///"+System.getProperty("user.dir")+java.io.File.separator;
		studentInterface.setProperty("FileDir", FileDir);
		studentInterface.setProperty("urlBase", getDocBase());
		trace.out (5, this, "3");
		setProperty("StudentInterface", toolCommunicator);
		// internalTarget is used to send the messages inside the Tools 
		StringTarget internalTarget = new StringTarget(toolCommunicator); 
		//MessageObject.showMessage = showMessages;
    	//startInterface(useLisp);
    	//ToolFrame t = new ToolFrame("just a test");
		trace.out (5, this, "done initializing");
	}
	
	public InterfaceProxy createInterfaceProxy(){
		return new  DecInterfaceProxy();
	}
	
	public void setVersionString(String v){
		studentInterface.setProperty("Version", v);
	}
	
	public boolean isFinished(){
		if(studentInterface == null)
			return finished;
		return studentInterface.getIsFinished();
	}
	
	public void redirectOutput(){
		PrintStream	stdout	= null;
		try {
		//for Geom
	    	stdout = new PrintStream (new FileOutputStream ("Documents/Output.java"));
	    //for Alg 2
	    	//stdout = new PrintStream (new FileOutputStream ("Documents Alg2/Output.java"));
			System.setOut ( stdout );
			System.setErr( stdout);
	    }
		catch (Exception e) {
	    // Sigh.  Couldn't open the file.
	    	System.out.println ("Redirect:  Unable to open output file Documents/Output.java");
	    }
	}
	
 	public void setIsFinished(boolean f){};
 
 	public void startInterface(boolean useLisp){
		trace.out (5, this, "starting interface. useLisp = " + useLisp);
		
	}
	
	void logInUser(String usName, String password){
		MessageObject mo = new MessageObject("NOTELOGIN");
		mo.addParameter("NAME", usName);
		mo.addParameter("PASSWORD", password);
		mo.addParameter("OBJECT",tool_proxy);
		tool_proxy.send(mo);
	}	

	public URL getDocBase() {
		URL toret = null;
		try{
			String dir = System.getProperty("user.dir");
			String fm = "file://";
				fm = fm+"/";
			dir = fm+dir+java.io.File.separator;
			toret = new URL(dir);
		}
		catch (MalformedURLException e){
			trace.out("ToolAgent getDocBase "+e.toString());
		}
		return toret;
	}

	public String getBase() {
		return FileDir;
	}
	public void setProperty(String name, Object value) {
		Properties.put(name.toUpperCase(), value);
	}
	
	public Object getProperty(String name) {
		return (Object)Properties.get(name.toUpperCase());
	}
	
	public ToolAgent(ToolAgent ps) {
            ps = this;
        }
    
	public ToolAgent() {
		if(isActive())
			isWrapped = false;
		else	
			isWrapped = true;
		init();
    }
    
    public void run(){
    	MessageObject.showMessage = Boolean.getBoolean("showMessages");
    	startInterface(useLisp);
    }
    
    public void stop(){
    	connection.disconnect();
    	super.stop();
    }

    public URL getDocumentBase() {
    	URL toret = null;
    	if(isWrapped) {
    		try{
    			URL url = new URL(System.getProperty("user.dir"));
    			return url;
    		}
    		catch (MalformedURLException e) {
    			trace.out("ToolAgent getDocumentBase "+e);
    		}
    	}
    	return toret;
    }
    
    public void setPort(int p){
    	portNum = p;
    }
    		
}



