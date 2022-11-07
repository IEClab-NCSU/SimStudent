//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/UserLogin.java
package edu.cmu.pact.client;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.trace;

public class UserLogin extends JDialog implements KeyListener, WindowListener {
	
	/** Property name for student's personal identifier. */
	public static final String LOGIN_NAME = "loginName";
	
	/** Property name for quit button event. */
	public static final String QUIT_BUTTON = " QUIT "; 
	
	protected JTextField loginText;
    protected JPasswordField password, password2;
	JButton login, guest, ok, cancel;
	JLabel quesJLabel;
	JPanel aa;
	boolean isGuest;
	Color back= Color.lightGray;
	Color backWhite = Color.white;
	Font plain12; 
	Font bold12; 
	
	boolean wasShown = false;
	
	/** Delegate to support listener registration, etc. */
	protected PropertyChangeSupport listenerSupport = new PropertyChangeSupport(this);
	
	/**
	 * Enable listeners to get the login event.
	 * @param listener will get a {@link PropertyChangeEvent} on login
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listenerSupport.addPropertyChangeListener(listener);
	}
	
	/**
	 * Enable listeners to unsubscribe.
	 * @param listener will no longer get {@link PropertyChangeEvent}s
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listenerSupport.removePropertyChangeListener(listener);
	}
	
	public UserLogin(JFrame parent) {
////		super("Login");
		super(parent, true);
		
		trace.out ("33", "create user login window");
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC")){
			plain12 = new Font("geneva", Font.PLAIN, 12);
			bold12 = new Font("geneva", Font.BOLD, 12);
		} else {
			plain12 = new Font("arial", Font.PLAIN, 12);
			bold12 = new Font("arial", Font.BOLD, 12);
		}
		isGuest = false;
		// resize(300, 180);
		// move(100, 100);
		
////		setInitiallyVisible(false);
		setVisible(false);
		setTitle("Login");
		setBackground(backWhite);
		aa = new JPanel();
		aa.setLayout(new CardLayout());
		
////		add("Center", aa);		
		getContentPane().add("Center", aa);
		
		GridBagLayout grid = new GridBagLayout();
		setBackground(Color.lightGray);
		
		JPanel fir = new JPanel();
		fir.setLayout(grid);
		
		JLabel userLab = new JLabel("Name: ",2);
		userLab.setFont(plain12);
		JLabel passLab = new JLabel("Password: ",2);
		passLab.setFont(plain12);
		
		loginText = new JTextField(20); // ("", 30)
		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(loginText); }
		loginText.setFont(plain12);
		loginText.setBackground(backWhite);
		loginText.addKeyListener(this);
		password = new JPasswordField(20); // ("", 30)
		password.setFont(plain12);
		password.setBackground(backWhite);
		password.setEchoChar('*');
		password.addKeyListener(this);
		login = new JButton(" LOGIN ");
		login.setFont(plain12);
		login.setBackground(backWhite);
		login.addActionListener(new ActionListener() {
      		public void actionPerformed(ActionEvent e) {
      			logIn();
      		}
    	});
 //   	DefaultButton defButton = new DefaultButton(login, 0);
		
//		setDefaultButton(login);
		getRootPane().setDefaultButton(login);
		
    	JButton quit = new JButton(QUIT_BUTTON);
		quit.setFont(plain12);
		quit.setBackground(backWhite);
		quit.addActionListener(new ActionListener() {
      		public void actionPerformed(ActionEvent e) {
      			sendNoteQuit();
      		}
    	});
    	guest = new JButton("LOGIN AS GUEST");
		guest.setFont(plain12);
		guest.setBackground(backWhite);
		guest.addActionListener(new ActionListener() {
      		public void actionPerformed(ActionEvent e) {
      			guestLogin();
      		}
    	});
		JPanel a = new JPanel();
		a.setLayout(new FlowLayout(1));
		a.add(login);
////		a.add(defJButton);
		a.add(new JLabel("     "));
		a.add(quit);
		
		viewset(fir, userLab, 0, 0, 1, 1, 10, 10, 10, 0);
		viewset(fir, loginText, 1, 0, 1, 1, 10, 2, 10, 10);
		viewset(fir, passLab, 0, 1, 1, 1, 0, 10, 10, 0);
		viewset(fir, password, 1, 1, 1, 1, 0, 2, 10, 10);
		viewset(fir, a, 0, 2, 2, 1, 0, 0, 10, 0);
		aa.add("first", fir);
		
		JPanel sec = new JPanel();
		sec.setLayout(grid);
		
		quesJLabel = new JLabel("", 0);
		quesJLabel.setFont(plain12);
		JLabel quesJLabel2 = new JLabel("Would you like to create one?",0);
		quesJLabel2.setFont(plain12);
		
		JLabel pass2 = new JLabel("Password", 0);
		pass2.setFont(plain12);
		JLabel comment = new JLabel("(Password is not required)"); //// , 1);
		comment.setFont(plain12);
		
		password2 = new JPasswordField("", 20);
		password2.setFont(plain12);
		password2.setBackground(backWhite);
		password2.setEchoChar('*');
		ok = new JButton("OK");
		ok.setFont(plain12);
		ok.setBackground(backWhite);
		ok.addActionListener(new ActionListener() {
      		public void actionPerformed(ActionEvent e) {
      			createRecord();
      		}
    	});
    	cancel = new JButton("CANCEL");
		cancel.setFont(plain12);
		cancel.setBackground(backWhite);
		cancel.addActionListener(new ActionListener() {
      		public void actionPerformed(ActionEvent e) {
      			((CardLayout)aa.getLayout()).first(aa);
      		}
    	});
		JPanel b = new JPanel();
		b.setLayout(new FlowLayout(1));
		b.add(ok);
		b.add(new JLabel("     "));
		b.add(cancel);
		
		viewset(sec, quesJLabel, 0, 0, 2, 1, 15, 10, 5, 0);
		viewset(sec, quesJLabel2, 0, 1, 2, 1, 5, 10, 15, 0);
		viewset(sec, pass2, 0, 2, 1, 1, 5, 10, 10, 0);
		viewset(sec, comment, 1, 3,1, 1, 0, 5, 10, 0); 
		viewset(sec, password2, 1, 2, 1, 1, 5, 2, 10, 10);
		 
		viewset(sec, b, 0, 4, 2, 1, 10, 20, 10, 20);
		
		//aa.add("sec", sec);
		
		((CardLayout)aa.getLayout()).first(aa);
		
////		setToolFrameProxy(login_obj);
///		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
///		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(this);
		pack();
		setLocation(250, 250);
		setResizable(false);
	}
	
	public void keyTyped(KeyEvent e){ }
	
    public void keyReleased(KeyEvent e){ }
	
	public void keyPressed(KeyEvent evt){
		int key = evt.getKeyCode();
		if (key == KeyEvent.VK_ENTER)
			logIn();
	}
	
	protected void sendNoteQuit(){
		setVisible(false);
		listenerSupport.firePropertyChange(QUIT_BUTTON, "old", "new");
	}
/**
	public void actionPerformed(ActionEvent e){
		String command = e.getActionCommand();
		if( command.equalsIgnoreCase("LOGIN") ) {
			try{
				setProperty("IsVisible", "true");
			} catch (CommException ex) { }
		}
//		else
//			super.actionPerformed(e);
		    
	}
**/	
	/**
	public void setProperty(String propertyName, Object propertyValue) throws CommException{
////		getAllProperties().put(propertyName.toUpperCase(), propertyValue);
		try{
		if(propertyName.equalsIgnoreCase("ISVISIBLE")) {
			boolean vis = DataConverter.getBooleanValue(propertyName,propertyValue);
			setVisible(vis);
			if(vis){
				toFront();
			}
		}
////		else
////			super.setProperty(propertyName, propertyValue);
		}catch (NoSuchPropertyException e){
			throw new NoSuchPropertyException("UsewrLogin : "+e.getMessage());
		} catch (DataFormattingException ex){
			throw getDataFormatException(ex);
		}
	}
**/
	public void logIn() {
		String userName = loginText.getText();
		if(!(userName.trim()).equals("")){
			wasShown = true;
			setVisible(false);
			listenerSupport.firePropertyChange(LOGIN_NAME, "", userName);
		}
	}
	public void createRecord() {
		/*
			String mes = "name="+coder.encode(loginText.getText())+"&"+"password="+coder.encode(password2.getText())+"&"+"action="+"Create";
			this.setCursor(Frame.WAIT_CURSOR);
			tapplet.tryCGI(mes, true);
		*/
		}
		
		public void guestLogin() {
		/*
			String mes ="name="+""+"&"+"password="+""+"&"+"guest.x="+"59"+"&"+"guest.y="+"15"; 
			isGuest = true;
			this.setCursor(Frame.WAIT_CURSOR);
			tapplet.tryCGI(mes, true);
		*/
		}
			
		
	public void setVisible(boolean v) {
		super.setVisible(v);
		if(v) {
			//if(wasShown && BeanMenuRegistry.knownObjects.getSize() != 0) 
			//	BeanMenuRegistry.knownObjects.enableMenuItem("Tutor", "Login");
			loginText.requestFocus();
		}
	}
	
	public void componentHidden(ComponentEvent e){ 
		setVisible(false);
	}
	
	public void windowClosing(WindowEvent e) {
     //   trace.out("WindowListener method called: windowClosing.");
        sendNoteQuit();
    }
	
	public void windowClosed(WindowEvent e) {
        //This will only be seen on standard output.
        displayMessage("WindowListener method called: windowClosed.");
    }

    public void windowOpened(WindowEvent e) {
        displayMessage("WindowListener method called: windowOpened.");
    }

    public void windowIconified(WindowEvent e) {
        displayMessage("WindowListener method called: windowIconified.");
    }

    public void windowDeiconified(WindowEvent e) {
        displayMessage("WindowListener method called: windowDeiconified.");
    }

    public void windowActivated(WindowEvent e) {
        displayMessage("WindowListener method called: windowActivated.");
    }

    public void windowDeactivated(WindowEvent e) {
        displayMessage("WindowListener method called: windowDeactivated.");
    }

    public void windowGainedFocus(WindowEvent e) {
        displayMessage("WindowFocusListener method called: windowGainedFocus.");
    }

    public void windowLostFocus(WindowEvent e) {
        displayMessage("WindowFocusListener method called: windowLostFocus.");
    }

 
    void displayMessage(String msg) {
      //  trace.out(msg);
    }

    /**
     * 
     * @param cont
     * @param obj
     * @param vgridx
     * @param vgridy
     * @param vgridwidth
     * @param vgridheight
     * @param vtop
     * @param vleft
     * @param vbottom
     * @param vright
     */
    public static void viewset(Container cont,
	 					   		Component obj,
                          		int vgridx,
                          		int vgridy,
                          		int vgridwidth,
                          		int vgridheight,
                          		int vtop,
                          		int vleft,
                          		int vbottom,
                          		int vright
                          		)  { 
    
    	GridBagConstraints c = new GridBagConstraints();
       c.gridx = vgridx;
       c.gridy = vgridy;
       c.gridwidth = vgridwidth;
       c.gridheight = vgridheight;
       c.insets.top = vtop;
       c.insets.left = vleft;
       c.insets.bottom = vbottom;
       c.insets.right = vright;
       c.fill = 1;

       ((GridBagLayout)cont.getLayout()).setConstraints(obj, c);
       cont.add(obj);
   }
}
	
