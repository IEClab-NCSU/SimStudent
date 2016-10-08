package interaction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import servlet.SimStudentBaseServlet;

/**
 * Parent Backend class that contains methods for interacting with
 * the servlet.
 * 
 * Methods that can be used for sending objects to the interface:
 * 	getComponent
 * 	sendSAI
 * 	modifyInterface
 * 
 * Methods that should be overriden by child:
 * 	processInterfaceEvent
 * 	processWME
 * 
 * Note: when packaging this class we should remove all methods not mentioned above
 * so we don't expose servlet related code to the user.
 * 
 * @author Patrick Nguyen
 *
 */
public abstract class Backend {
	private SimStudentBaseServlet servlet;//pointer to parent servlet
	private String session;//the session this backend serves
	private Set<InterfaceAttribute> components;//all the attributes
	private List<String> wmes;
	protected List<SAI> initialSAIs;// sais that can be used for initization (default values)	
	HashMap<String, SAI> initialSAIsHash; //hashmap to contain the start state elements for fast access. Added by Nick
	public String userID=""; // userid this backend serves
	public String problemName=""; //used to identify the tutor
		
	
	/** 
	 * Added a parameter to invoke the parseArgument() 
	 * before the user-defined feature-predicates are loaded in runtime.
	 * Only constructor in this class, which helps in making sure 
	 * that all the backend classes implement only this parameterised constructor.
	 * @param argV array of String arguments to invoke the parseArgument method before loading any production rules
	 * @author SHRUTI
	 */
	public Backend(String[] argV){
		System.out.println("inside Backend(String[])");
		parseArgument(argV);
	}


	public List<SAI> getInitialSAIs() {
		return initialSAIs;
	}

	public void setInitialSAIs(List<SAI> initialSAI) {
		
		this.initialSAIs = initialSAI;
		/*populate the hash map*/
		if (initialSAIsHash==null){ initialSAIsHash = new HashMap<String, SAI>(); }
		for (SAI sai : this.initialSAIs) { 
			initialSAIsHash.put(sai.getFirstSelection(), sai);
		}
	}

	
	/**
	 * Gets the session this backend serves
	 * @return The id of the session this backend serves
	 */
	public String getSession() {
		return session;
	}

	/**
	 * Sets the session this backend serves
	 * @param session The id of the session this backend serves
	 */
	public void setSession(String session) {
		this.session = session;
	}
	
	/**
	 * Gets the servlet that created this backend
	 * @return Servlet that created this backend
	 */
	public SimStudentBaseServlet getServlet() {
		return servlet;
	}

	/**
	 * Sets the servlet that created this backend
	 * @param servlet Servlet that created this backend
	 */
	public void setServlet(SimStudentBaseServlet servlet) {
		this.servlet = servlet;
	}
	/**
	 * Gets a component to modify its attributes
	 * @param name The name of the component
	 * @return InterfaceAttribute representing the component
	 */
	public InterfaceAttribute getComponent(String name){
		InterfaceAttribute ia = new InterfaceAttribute(name);
		InterfaceAttribute comp = null;
		for(InterfaceAttribute c : components)
			if(c.getName().equals(name))
				comp = c;
		if(comp == null)
			return null;
		ia.setBackgroundColor(comp.getBackgroundColor());
		ia.setBorderColor(comp.getBorderColor());
		ia.setBorderStyle(comp.getBorderStyle());
		ia.setBorderWidth(comp.getBorderWidth());
		ia.setIsEnabled(comp.getIsEnabled());
		ia.setFontColor(comp.getFontColor());
		ia.setFontSize(comp.getFontSize());
		ia.setHeight(comp.getHeight());
		ia.setIsHintHighlight(comp.getIsHintHighlight());
		ia.setWidth(comp.getWidth());
		ia.setX(comp.getX());
		ia.setY(comp.getY());
		ia.getModifications().clear();
		return ia;
	}
	/**
	 * Add a component to the existing list of components
	 * @param ia InterfaceAttribute object representing the component
	 */
	public void addComponent(InterfaceAttribute ia){
		if(components == null){
			components = new HashSet<InterfaceAttribute>();
		}
		if(ia != null){
			components.add(ia);
		}
		initializeInterfaceAttribute(ia);
	}
	/**
	 * Removes a component from the existing list of components
	 * @param ia InterfaceAttribute object representing the component
	 */
	private void removeComponent(InterfaceAttribute ia){
		if(components == null){
			components = new HashSet<InterfaceAttribute>();
		}
		InterfaceAttribute comp = null;
		for(InterfaceAttribute c : components){
			if(c.getName().equals(ia.getName()))
				comp = c;
		}	
		if(comp != null)
			components.remove(comp);
		
	}
	
	/**
	 * Send an SAI to the interface. 
	 * @param sai SAI to send to the interface
	 */
	public void sendSAI(SAI sai){
		servlet.sendSAI(sai, session);
	}
	
	/**
	 * Send an a String to the CTATHintWindow. 
	 * @param message the hint message to send to the interface
	 */
	public void sendHintMessage(ArrayList<String> message){
		servlet.sendHintMessage(message, session);
	}
	
	
	/**
	 * Modify a component on the interface.
	 * @param im InterfaceAttribute object with modifications to apply to the component on the interface
	 */
	public void modifyInterface(InterfaceAttribute im){
		servlet.modifyInterface(im,session);
		im.getModifications().clear();
		removeComponent(im);
		addComponent(im);
		
	}
	
	/**
	 * Perform any necessary initial customizations required by the brd
	 * @param im InterfaceAttribute to initialize
	 */
	public void initializeInterfaceAttribute(InterfaceAttribute im) {
		
	}

	/**
	 * Process an event on the interface. Currently this includes only SAI
	 * and double clicking.
	 * @param ie InterfaceEvent object representing the event
	 */
	public abstract void processInterfaceEvent(InterfaceEvent ie);
	
	
	/**
	 * Parses the Argument field value in SetPreferences object.
	 * @param arg
	 * @return void
	 * @author SHRUTI
	 */
	public abstract void parseArgument(String[] arg);
	
	/**
	 * Set the list of .wme files given to the backend
	 * @param wmes Text of .wme files
	 */
	public void setWME(List<String> wmes){
		System.out.println("setWME()");
		this.wmes = wmes;
		for(String wme : wmes)
			System.out.println(wme);
	}
	
	/**
	 * Gets the list of wmes
	 * @return Text of .wme files
	 */
	public List<String> getWME(){
		return wmes;
	}
	
	public void init(){
		System.out.println("init()");
	}
	
	public Set<InterfaceAttribute> getComponentList()
	{
		return components;
	}	
}
