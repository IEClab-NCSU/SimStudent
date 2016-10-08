//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/StudentInterface.java
package edu.cmu.old_pact.cmu.toolagent;


import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.old_pact.cl.coreInterface.CoreInterface;
import edu.cmu.old_pact.cl.util.menufactory.MenuFactory;
import edu.cmu.old_pact.cmu.messageInterface.ToolPointerVector;
import edu.cmu.old_pact.cmu.messageInterface.UserMessage;
import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.dormin.toolframe.DorminToolFrame;
import edu.cmu.old_pact.dormin.toolframe.DummyFrame;
import edu.cmu.old_pact.infodialog.InfoDialog;
import edu.cmu.old_pact.infodialog.InfoDialogProxy;
import edu.cmu.old_pact.objectregistry.ObjectRegistry;
import edu.cmu.old_pact.toolframe.FeedbackFrame;

/** 
*	PROPERTY  		DEF VALUE		TYPE
*	Name			"Application"	S
*	ProblemNane		from Lisp/TRE 	S
*	FileDir			null			S
*	canSendFeedback	false			B
**/

public class StudentInterface implements Sharable, Runnable, ActionListener, 
										PropertyChangeListener, CoreInterface{
	ObjectProxy tool_obj;
	Hashtable Properties;
	public static String fileDir = null;
	private FeedbackFrame feedbackFrame = null;
	private boolean isFinished = false;
	private InfoDialog infoDialog = null;
	private Thread thread = null;

	static UniversalToolProxy utp = null;
	
	public StudentInterface(){
		this("StudentInterface");
	}
	 
	public StudentInterface(String name){
		Properties = new Hashtable();
		Properties.put("NAME", name);
		Properties.put("PROBLEMNAME", "");
		Properties.put("CANSENDFEEDBACK", Boolean.valueOf("false"));
		Properties.put("VERSION", "Default version");
		ObjectRegistry.registerObject("Application", this);				
	}

	public void setUTP(UniversalToolProxy _utp){
		utp = _utp;
	}
	
	public void createInterfaceProxy(){
		tool_obj = new InterfaceProxy();
		tool_obj.setRealObject(this);
		createInfoDialog();		
	}
	
	public void createInfoDialog(){	
		Frame dummy = new Frame();
		dummy.setLocation(-1000,-1000);
		dummy.setVisible(true);
		infoDialog = new InfoDialog(dummy,fileDir);
	
		InfoDialogProxy infoProxy = new InfoDialogProxy();
		infoProxy.init(tool_obj);				
		infoProxy.setRealObject(infoDialog);
		infoDialog.setProxyInRealObject(infoProxy);		
	}
	
	public void setRealObject() {
		tool_obj.setRealObject(this);
	}
	
	public void setProxyInRealObject(ObjectProxy op){
		tool_obj = op;
	}
	
	public ObjectProxy getObjectProxy() {
		return tool_obj;
	}
	
	public void addTeacherOptions(){
		TeacherOptions op = new TeacherOptions(tool_obj);
	}
	
	public void delete() { 	
		MenuUserMessageWindow userMessageWindow = (MenuUserMessageWindow)ObjectRegistry.getObject("UserMessageWindow");
		if(userMessageWindow != null)
		if(userMessageWindow != null)
			userMessageWindow.removeFromRegistries();
		userMessageWindow = null;
		tool_obj = null;
		Properties.clear();
		Properties = null;
		isFinished = true;
	}
	
	public boolean getIsFinished(){
		return isFinished;
	}
	
	public void setIsFinished(boolean b){
		isFinished = b;
	}
	
	public void actionPerformed(ActionEvent e){
		try{
			String command = e.getActionCommand();
			if(command.equalsIgnoreCase("QUIT")){
				//ObjectRegistry.knownObjects.sendValueOnQuit();
				((InterfaceProxy)tool_obj).hideAll();
				//infoDialog.setModal(false);
				//infoDialog.displayHtmlText("<center><IMG SRC=\"Images/disks.gif\" aligh=top><br>"+
				//						"Please be patient! Saving Student Data.</center>");						
				//infoDialog.setVisible(true);
				sendNoteQuit();
			}
			else if(command.equalsIgnoreCase("ABOUT"))
				MenuFactory.showAboutWindow();
			
			else if(command.equalsIgnoreCase("SHOWFEEDBACKFRAME"))
				showFeedbackFrame();
		} catch (NullPointerException ex) { }

	}
	
	public void showFeedbackFrame(){
		if(feedbackFrame == null){
			feedbackFrame = new FeedbackFrame("Send Feedback");
			feedbackFrame.addPropertyChangeListener(this);
		}
		feedbackFrame.setVisible(true); 
		feedbackFrame.toFront();
	}
	
	public void propertyChange(PropertyChangeEvent evt){
		if(evt.getPropertyName().equalsIgnoreCase("FEEDBACK")) {
			String val = (String)evt.getNewValue();
			MessageObject mo = new MessageObject("FEEDBACK");
			mo.addParameter("OBJECT", tool_obj);
			mo.addParameter("MESSAGE", val);
			tool_obj.send(mo);
		}	
	}
	
	public void logInUser(String usName, String password){
		MessageObject mo = new MessageObject("NOTELOGIN");
		mo.addParameter("NAME", usName);
		mo.addParameter("PASSWORD", password);
		mo.addParameter("OBJECT",tool_obj);
		tool_obj.send(mo);
	}
	
	public void startThread(){
		if(thread != null && thread.isAlive()){
			thread.stop();
			thread=null;
		}
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.start();
	}
	
	public void sendNoteQuit(){
		startThread();
	}
	
	public void run(){
		MessageObject mo = new MessageObject("NoteQuit");
		mo.addParameter("OBJECT", tool_obj);
		tool_obj.send(mo);
	}
	
	public void quit(){
		System.out.println("Application. Got quit request");
	}
	//
	public String getName(){
		return (String)Properties.get("NAME");
	}
	
	private String getValue(String name, MessageObject mo) {
    	String toret = "";
    	try{
    		toret = mo.extractStrValue(name);
    	} catch (DorminException e) { 
    		System.out.println("StudentInterface getValue "+e);
    	}
    	
    	return toret;
    }
    
    public Object getProperty(String name) {
    	return Properties.get(name.toUpperCase());
    }
    
    public Hashtable getProperty(Vector proNames) throws NoSuchPropertyException{
		int s = proNames.size();
		if(s == 1 && ((String)proNames.elementAt(0)).equalsIgnoreCase("ALL"))
			return Properties;
		Hashtable toret = new Hashtable();
		String currName;
		for(int i=0; i<s; i++){
			currName = ((String)proNames.elementAt(i)).toUpperCase();
			Object ob = Properties.get(currName);
			if(ob == null)
				throw new NoSuchPropertyException("Application doesn't have property "+currName);
			toret.put(currName, ob);
		}
		return toret;
	}
    
    public void setProperty(String name, Object obj) {
    	Properties.put(name.toUpperCase(), obj);
		try{
    	if(name.equalsIgnoreCase("FileDir")) {
    		fileDir = (String)obj;
    		MenuFactory.fileDir = fileDir;
    		if(infoDialog != null)
    			infoDialog.setURLBase(fileDir);
		}
    	else if(name.equalsIgnoreCase("ArrangeWindows") && DataConverter.getBooleanValue(name, obj))
    	       arrangeWindows();
    	else if(name.equalsIgnoreCase("ArrangeAndDisplayWindows")){
			boolean b = DataConverter.getBooleanValue(name,obj);
			if(b)
			  	arrangeWindows();
			else
			  	displayWindows(); 
		} 
    	else if(name.equalsIgnoreCase("Version")) 
    		MenuFactory.version = obj.toString(); 
    	else if(name.equalsIgnoreCase("hideAll")) 	
    		((InterfaceProxy)tool_obj).hideAll();
        else if(name.equalsIgnoreCase("hideAllButSkillometer"))
            ((InterfaceProxy)tool_obj).hideAllButSkillometer();
    	} catch (DataFormattingException e){ }
    }
	
	public  void arrangeWindows(){
	    Hashtable framesHash = ObjectRegistry.getAllObjects();
        DorminToolFrame[] arr = new DorminToolFrame[framesHash.size()-1];
        int count = 0;
        Enumeration values = framesHash.elements();
        boolean isSolverLesson = false;
        Object solvObj = (DorminToolFrame)ObjectRegistry.getObject("Solver");
		if(solvObj != null){
		  try{
		  		String subtype = (String)((DorminToolFrame)solvObj).getProperty("SUBTYPE");
		  		if(subtype.equalsIgnoreCase("lesson"))
		  			isSolverLesson = true;
          } catch (DorminException e) {}
        }
        Object frame;
        while (values.hasMoreElements()) {
            frame = values.nextElement();
            if(frame instanceof DorminToolFrame) {
                 arr[count]= (DorminToolFrame)frame;
                 count +=1;
                 if(isSolverLesson){}
                 	//((DorminToolFrame)frame).disablePreferencesMenu();
            }                      
       }
       DummyFrame df = new DummyFrame(arr);
       df.setVisible(true);
       df.dispose();           
    }
     
    public void displayWindows(){
		Hashtable hash = ObjectRegistry.getAllObjects();
		Enumeration objects = hash.elements();
		Object obj;
		while(objects.hasMoreElements()) {
  			obj = objects.nextElement(); 
  				// set visible only if property initiallyVisible == true 	
   			if((obj instanceof DorminToolFrame) && !(obj instanceof MenuUserMessageWindow) &&
				((DorminToolFrame)obj).getInitiallyVisible())
  			  ((DorminToolFrame)obj).setVisible(true);
		}
	}
	
    
	 static public MenuUserMessageWindow getUserMessageWindow(String imageName){
    	MenuUserMessageWindow userMessageWindow = (MenuUserMessageWindow)ObjectRegistry.getObject("UserMessageWindow");
		if(userMessageWindow != null)
			userMessageWindow.removeFromRegistries();
		else 
			userMessageWindow = new MenuUserMessageWindow(fileDir, imageName, utp);
		return userMessageWindow;
	}
    
    public void showMessage(Vector mess, String imageName, String title, Vector pointersV, 
    						String nam, int startFrom) {
    
    	//trace.out (5, this, "now showing message");
   		MenuUserMessageWindow userMessageWindow = getUserMessageWindow(imageName);  
   		userMessageWindow.setTitle(title);
   		userMessageWindow.referenceByPosition();
   		if(!nam.equals(""))
   			userMessageWindow.setName(nam);
   		String urlBase = (String)getProperty("FileDir");
   		String imageBase = null;
		if(imageName != "")
			imageBase = urlBase+imageName;
		ToolPointerVector pV = new ToolPointerVector(mess,pointersV);
		UserMessage[] userMessage = pV.getUserMessages();
		userMessageWindow.setURLBase(urlBase);
		userMessageWindow.presentMessages(userMessage, startFrom);
		userMessageWindow.addImage(imageName);
	}
	
}


