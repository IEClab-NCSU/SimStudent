package edu.cmu.old_pact.toolframe;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import edu.cmu.old_pact.settings.Settings;


public class FeedbackFrame extends Frame implements WindowListener{
	private TextArea ta;
	
	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	//if true, menus for this tool are accessible through the MergedToolMenuBar
	private boolean publishedMenuBar=false;

	public FeedbackFrame (String title) {
		setBackground(new Color(204, 204, 204));
		setLayout(new BorderLayout());
		ta = new TextArea("", 15, 60, TextArea.SCROLLBARS_VERTICAL_ONLY);
		add("Center", ta);
		
		Panel bot = new Panel();
		bot.setLayout(new FlowLayout());
		
		Button sendButton = new Button("Send");
		sendButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sendFeedback();
			}
		});
		
		Button cancelButton = new Button("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				cancel();
			}
		});
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC")){
			bot.add(cancelButton);
			bot.add(new Label("  "));
			bot.add(sendButton);			
		} else { 
			bot.add(sendButton);
			bot.add(new Label("  "));
			bot.add(cancelButton);
		}
		
		add("South", bot);		
		addWindowListener(this);
		
		setIconImage(Settings.loadImage(this, Settings.cllogo40));
		pack();
		setSize(300, 250);
		setLocation(10,10);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		changes.removePropertyChangeListener(l);
	}
	
	public void firePropertyChange(String name, Object oldValue, Object newValue){
		changes.firePropertyChange(name, oldValue, newValue);
	}
	
	public void cancel(){
		ta.setText("");
		setVisible(false);
	}
	
	public void sendFeedback(){
		firePropertyChange("Feedback", "", ta.getText());
		ta.setText("");
	}	
	
	
	
	public void windowOpened(WindowEvent e) { }

    public void windowClosing(WindowEvent e) { 
    	cancel();
    }

    public void windowClosed(WindowEvent e) { 
    	cancel();
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e){
    }

    public void windowActivated(WindowEvent e){ }

    public void windowDeactivated(WindowEvent e){ }		
}
