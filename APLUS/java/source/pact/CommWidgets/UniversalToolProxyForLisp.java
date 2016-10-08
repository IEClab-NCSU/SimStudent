/**
 * 
 */
package pact.CommWidgets;

import java.util.Vector;

import javax.swing.JFrame;

import edu.cmu.old_pact.cmu.toolagent.InterfaceProxy;
import edu.cmu.old_pact.cmu.toolagent.LispJavaConnection;
import edu.cmu.old_pact.cmu.toolagent.LispTarget;
import edu.cmu.old_pact.cmu.toolagent.StudentInterface;
import edu.cmu.old_pact.cmu.toolagent.UserLogInProxy;
import edu.cmu.old_pact.dormin.Communicator;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.Target;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.SocketProxy.XMLConverter;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.client.UserLogin;

/**
 * Isolates methods and fields dependent on edu.cmu.old_pact.* from main {@link UniversalToolProxy}
 */
public class UniversalToolProxyForLisp extends UniversalToolProxy {

	/**
	 *  
	 */
	//////////////////////////////////////////////////////////////
	public static class CommWidgetsInterfaceProxy extends InterfaceProxy {

	}

	protected JCommWidgetsToolProxy toolProxy;
	protected CommWidgetsInterfaceProxy interfaceProxy;
	protected LispJavaConnection connection;
	Target target = null;
	private static String theHost = "127.0.0.1";
	protected int portNum = 1501;
	protected Communicator toolCommunicator;
	private StudentInterface studentInterface = null;

	/**
	 * 
	 */
	public UniversalToolProxyForLisp() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * To be run by TutorWindow.java when the interface is run.
	 * @param controller2 
	 */
	//////////////////////////////////////////////////////
	public void init(BR_Controller controller) {
		super.init(controller);
        
		interfaceProxy = new CommWidgetsInterfaceProxy();

		toolProxy = new JCommWidgetsToolProxy(interfaceProxy, applicationName,
				this);

		MessageObject.showMessage = true;
// !!		if(!controller.inTutoringServiceMode()){
			studentInterface = new StudentInterface("StudentInterface");
			studentInterface.setProxyInRealObject(toolProxy);
			studentInterface.setUTP(this);
			toolProxy.setRealObject(studentInterface);
			interfaceProxy.setRealObject(studentInterface);
			toolCommunicator = new Communicator(interfaceProxy);
// !!		}else{
// !!			Communicator.reset();
// !!		}
		connection = new LispJavaConnection(theHost, portNum, toolCommunicator, controller);
		target = new LispTarget(connection);
        toolProxy.addTarget(target);
	}
	

	/**
	 * @return {@link #theHost}
	 */
	public String getTutorHost() {
		return theHost;
	}

	/**
	 *  @param host hostname
	 */
	public void setTutorHost(String host) {
		if (host == null || host.equals(""))
			return;
		theHost = host;
	}

	//////////////////////////////////////////////////////
	/**
	 * Send a NotePropertySet message via Comm
	 * 
	 * @param pNames
	 *            A vector of property names
	 * @param pValues
	 *            A vector of property values
	 */

	//////////////////////////////////////////////////////
	public void sendProperty(MessageObject mo) {
		
		if (mo.getOptionalParameter("Object") == null)   // sewall 2011/06/14: non-Comm
			mo.addParameter("Object", getToolProxy());   // callers shouldn't set Object

		if (toolProxy == null) {
			throw (new RuntimeException ("Internal Error: toolProxy == null. The application name of the UniversalToolProxy must be set before sending a message to Lisp"));
		}

		if (!connectedToProductionSystem)
			connectToTutor();

		if (connectedToProductionSystem) {
		    trace.out ("m", "sending: " + mo.toString());
//            trace.printStack ("m");
            toolProxy.send(mo);
			
		} 
	}
	
	public void sendProperty(edu.cmu.pact.ctat.MessageObject msg) {
		BR_Controller ctlr = getController();
		
		if (ctlr!=null)
			
		if (ctlr != null && ctlr.getCtatModeModel().isJessTracing()){

			super.sendProperty(msg);
		}
		else { // if here, then will pass this msg to Lisp
			sendProperty(XMLConverter.xmlObjectToCommObject(msg));
		}
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public boolean connectToTutor() {
	    if (controller.getCtatModeModel().isJessTracing()) {
            connectedToProductionSystem = true;
            return true;
        }

        if (!controller.getCtatModeModel().isTDKMode())
			return false;

		if (connectedToProductionSystem) {
			trace.out ("lll", "Already connected");
			return true;
		}
        
		try {
			connection.firstConnection(true, studentInterface, 1, false);
		} catch (java.io.IOException e) {
			trace.err("Error connecting to lisp: e = " + e);
			return false;
		}
        
		connectedToProductionSystem = true;
		return true;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public boolean checkConnection() {

		trace.out("send empty message");
		Vector pNames = new Vector();
		Vector pValues = new Vector();

		if (getToolProxy() == null || !connectedToProductionSystem)
			return false;

		MessageObject mo = new MessageObject("Ping");

		mo.addParameter("PROPERTYNAMES", pNames);
		mo.addParameter("PROPERTYVALUES", pValues);

		sendProperty(mo);
		return true;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public JCommWidgetsToolProxy getToolProxy() {
		//		trace.out ("tool proxy = " + toolProxy);
		return toolProxy;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public StudentInterface getStudentInterface() {
		return studentInterface;
	}

	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public CommWidgetsInterfaceProxy getInterfaceProxy() {
		return interfaceProxy;
	}

	/**
	 * Generate a {@link UserLogin} dialog with reference to the {@link #studentInterface}.
	 * @param frame
	 * @return
	 */
	protected UserLogin createUserLogin(JFrame frame) {
		
		/**
		 * Local class preserves original Comm signalling to
		 * studentInterface in logIn(), sendNoteQuit().
		 */
		class CL_UserLogin extends UserLogin {
			
			protected StudentInterface st_interface;  // if not null, of type StudentInterface

			CL_UserLogin(JFrame frame, StudentInterface studentInterface) {
				super(frame);
				this.st_interface = studentInterface;
				UserLogInProxy login_obj = new UserLogInProxy(st_interface.getObjectProxy());
				login_obj.setRealObject(this);
			}
			
			protected void sendNoteQuit(){
				try {
					st_interface.sendNoteQuit();
				} catch (Exception e) {
					trace.err("error on "+getClass().getName()+".sendNoteQuit(): "+e);
				}
				super.sendNoteQuit();
			}
			
			public void logIn() {
				String userName = loginText.getText();
				if(!(userName.trim()).equals("")){
					try {
						((StudentInterface) st_interface).logInUser(loginText.getText(), password.getText());
					} catch (Exception e) {
						trace.err("error on "+getClass().getName()+".logIn(): "+e);
					}
					//if(wasShown && BeanMenuRegistry.knownObjects.getSize() != 0)
					//	BeanMenuRegistry.knownObjects.disableMenuItem("Tutor", "Login");
					super.logIn();
				}
			}
		}
		return new CL_UserLogin(frame, studentInterface);
	}

    public LispJavaConnection getLispJavaConnection() {
        return connection;
    }
}
