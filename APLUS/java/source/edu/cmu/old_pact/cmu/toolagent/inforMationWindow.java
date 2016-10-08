//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/inforMationWindow.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import edu.cmu.old_pact.cmu.uiwidgets.DefaultButton;
import edu.cmu.old_pact.settings.Settings;

public class inforMationWindow extends Frame  implements KeyListener{
   private StudentInterface studentInterface;
   private LispJavaConnection ljc;
    
    public inforMationWindow(StudentInterface studentInterface, LispJavaConnection ljc, String what)  {
    	super("Information");
       
       	this.studentInterface = studentInterface;
       	this.ljc = ljc;
       	Font buttonFont, labelFont;
		
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC")) {
			buttonFont = new Font("geneva", Font.PLAIN, 12);
			labelFont = new Font("geneva", Font.PLAIN, 12);
		}
		else {
			buttonFont = new Font("arial", Font.PLAIN, 12);
			labelFont = new Font("arial", Font.PLAIN, 12);
		}
       	
        setLayout(new GridLayout(2, 1));
        setBackground(Settings.ratioBackground);
        Panel a = new Panel();
        a.setLayout(new FlowLayout());
        Label lab = new Label(what, 1);
        lab.setFont(labelFont);
        a.add(lab);
        add(a);
        Panel b = new Panel();
        b.setLayout(new FlowLayout(1));
       	Button  okB = new Button("Continue");
       	okB.setFont(buttonFont);
       	DefaultButton defButton = new DefaultButton(okB,0);
        b.add(defButton);
        okB.addKeyListener(this);
        
        okB.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent evt){
        		tryAgain();
        	}
        });
        Button  quitB = new Button("  Quit  ");
        quitB.setFont(buttonFont);
        quitB.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent evt){
        		quit();
        	}
        });
        
        b.add( new Label("    "));
        b.add(quitB);
        add(b);
        
        pack();
        setLocation(200, 200);
        setSize(400, 150);
         
    }
    
    public void tryAgain()  {
    	setVisible(false);
    	try{
    		ljc.firstConnection(true, studentInterface);
    	
    	} catch (java.io.IOException e) { }
        disposeIt();
    }
    
    public void quit(){
    	studentInterface.setIsFinished(true);
    	this.dispose();
    }
    
    public void disposeIt(){
    	this.setVisible(false);
        studentInterface = null;
        ljc = null;
        this.dispose();
    }
    
    public void keyTyped(KeyEvent e){ }
	
    public void keyReleased(KeyEvent e){ }
    
    public void keyPressed(KeyEvent evt){
		int key = evt.getKeyCode();
		if (key == KeyEvent.VK_ENTER)
			tryAgain();
	}
}