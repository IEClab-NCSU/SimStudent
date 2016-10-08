//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/TeacherOptions.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.cmu.old_pact.cmu.messageInterface.GridbagCon;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.toolframe.DorminToolFrame;
import edu.cmu.old_pact.dormin.toolframe.DorminToolProxy;


public class TeacherOptions extends DorminToolFrame {
	ObjectProxy obj_proxy;
    TextField login;
	TextField password;
	
	public TeacherOptions(ObjectProxy parent){
		super("Teacher's Options");
		obj_proxy = new DorminToolProxy("Dialog","TeacherOptions",parent);
		obj_proxy.setRealObject(this);
		setToolFrameProxy(obj_proxy);
		
		setInitiallyVisible(false);
		setBackground(new Color(205,205,205));
		setLayout(new GridBagLayout());
		Font buttonFont, labelFont;
		
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC")) {
			buttonFont = new Font("geneva", Font.PLAIN, 12);
			labelFont = new Font("geneva", Font.PLAIN, 12);
		}
		else {
			buttonFont = new Font("arial", Font.PLAIN, 12);
			labelFont = new Font("arial", Font.PLAIN, 12);
		}	
               
        Label loginLabel = new Label("Login:");
        loginLabel.setFont(labelFont);
        login = new TextField("", 30);
        login.setBackground(Color.white);
        login.setEchoCharacter('*');
		
		Label pasLabel = new Label("Password:");
		pasLabel.setFont(labelFont);

		password = new TextField("", 30);
		password.setBackground(Color.white);
		password.setEchoCharacter('*');
		
		GridbagCon.viewset(this, loginLabel, 0, 0, 1, 1, 10, 10, 10, 0);
		GridbagCon.viewset(this, login, 1, 0, 3, 1, 10, 10, 10, 0);
		GridbagCon.viewset(this, pasLabel, 0, 1, 1, 1, 10, 10, 10, 0);
		GridbagCon.viewset(this, password, 1, 1, 3, 1, 10, 10, 10, 0);
                
		Button cancel = new Button("Cancel");
		cancel.setFont(buttonFont);
		cancel.addActionListener(new ActionListener() {
      		public void actionPerformed(ActionEvent e) {
      			doCancel();
      		}
    	});
    	GridbagCon.viewset(this, cancel, 1, 2, 1, 1, 0, 0, 10, 0);
    	
    	Button resetB = new Button("Reset Problem");
    	resetB.setFont(buttonFont);
		resetB.addActionListener(new ActionListener() {
      		public void actionPerformed(ActionEvent e) {
      			hideHintWindow();
      			sendMessage("Reset Problem");
      		}
    	});
    	GridbagCon.viewset(this, resetB, 2, 2, 1, 1, 0, 5, 10, 0);
    	
    	Button advance = new Button("Advance Problem");
    	advance.setFont(buttonFont);
		advance.addActionListener(new ActionListener() {
      		public void actionPerformed(ActionEvent e) {
      			hideHintWindow();
      			sendMessage("Advance Problem");
      		}
    	});
    	GridbagCon.viewset(this, advance, 3, 2, 1, 1, 0, 5, 10, 0);
		
		pack();
		setCurrentWidth(400);
		setCurrentHeight(150);
		setCurrentLocation(new Point(300, 300));
		setSize(400, 150);
		setLocation(300, 300);
	}
	
	public void sendMessage(String m){
        String loginValue = login.getText().trim();
		String passValue = password.getText().trim();
		if(!passValue.equals("")){
			doCancel();
			MessageObject mo = new MessageObject("ActionRequest");
			mo.addParameter("OBJECT",ObjectProxy.topObjectProxy);
			mo.addParameter("Action", m);
                        mo.addParameter("Login", loginValue);
			mo.addParameter("Password", passValue);
			ObjectProxy.topObjectProxy.send(mo);
			mo = null;
			
		}	
	}
	
	public void setVisible(boolean b){
		super.setVisible(b);
		if(b){
           login.setText("");
           password.setText("");
           login.requestFocus();
		}
	}
	
	public void doCancel(){
		this.setVisible(false);
	}
	
	public void openTeacherWindow(){ }
	
}
