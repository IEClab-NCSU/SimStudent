package edu.cmu.old_pact.infodialog;


import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.htmlPanel.HtmlPanel;
import edu.cmu.old_pact.settings.Settings;

/**
* different behavoiur on mac and pc between the problems:
* On mac - the Label doesn't show up, use the direct draw from the thread, if dialog isModal, DON't start this own thread
* on pc  - direct draw doesn't work, use the Label. Also the progressbar doesn't appear correctely.
* It's been removed between the problems.
**/

public class InfoDialog extends Dialog implements Runnable,Sharable {
	protected ProgressBar bar;
	protected ProgressPanel prPanel;
	protected Panel buttonPanel;
	protected Font msgFont;
	protected Font buttonFont;
	protected HtmlPanel htmlPanel = null;
	protected Color backColor = new Color(245,245,245);  //205,205,205);
	protected Color textBg = new Color(245,245,245);
	private ObjectProxy infoProxy;
	private boolean isModal = false;
	private Label textLabel = new Label("", Label.CENTER);
	private Component currTextObject;
	private boolean isMac = false;
	private Thread ownThread = null;
	
	public InfoDialog (Frame parent, String UrlBase){
		super(parent);
		setLayout(new BorderLayout());
	   	setBackground(backColor);
    	 
    	if((System.getProperty("os.name").toUpperCase()).startsWith("MAC")){
    		msgFont = new Font("geneva", Font.PLAIN, 14);
    		buttonFont = new Font("geneva", Font.PLAIN, 12);
    		isMac = true;
    	}
    	else {
    		msgFont = new Font("arial", Font.PLAIN, 14);
    		buttonFont = new Font("arial", Font.PLAIN, 12);
    	}
    		
    	htmlPanel = new HtmlPanel(300,160,textBg,false);
		add("Center",htmlPanel);      
		htmlPanel.setURLBase(UrlBase);
		htmlPanel.setFontSize(14);
		currTextObject = htmlPanel;
		
		textLabel.setBackground(backColor);
		textLabel.setFont(msgFont);
		
		bar = new ProgressBar(150, 15, getBackground());
		prPanel = new ProgressPanel(bar);
		add("South", prPanel);
		
		buttonPanel = new Panel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.setBackground(getBackground());
		
		Button okButton = new Button("     Ok     ");
		okButton.setFont(buttonFont);
		
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setVisible(false);
			}
		});
		buttonPanel.add(okButton);
		
		parent.setIconImage(Settings.loadImage(this, Settings.cllogo40));
		
		pack();
		setResizable(false);
		setSize(400,200);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(d.width/2-200,d.height/2-100);
		
		parent.setVisible(false);
	}

	public InfoDialog (Frame parent){
		this(parent, null);
	}
	
	public void setProxyInRealObject(ObjectProxy op){
	}
	
	public void setURLBase(String base){
		htmlPanel.setURLBase(base);
	}
		
	public void displayHtmlText(String text) {
		resetTopPanel(htmlPanel);
		htmlPanel.displayHtml(text);
		setSize(400,200);
	}
	
	public void displayLabelText(String text) {
		resetTopPanel(textLabel);
		textLabel.setText(text);
		setSize(400,100);
	}
	
	
	public ObjectProxy getObjectProxy() {
		return infoProxy;
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
 		try{
		if(propertyName.equalsIgnoreCase("LabelText")){	
			setVisible(false);
		  	displayLabelText((String)propertyValue);
		}
		else if(propertyName.equalsIgnoreCase("HtmlText")){	
			setVisible(false);
		  	displayHtmlText((String)propertyValue);
		}
		else if(propertyName.equalsIgnoreCase("ISMODAL")){
			boolean mod = DataConverter.getBooleanValue(propertyName,propertyValue);
		  	setModal(mod);
		}
		else if(propertyName.equalsIgnoreCase("ISVISIBLE")){
			setVisible(DataConverter.getBooleanValue(propertyName,propertyValue));
		}
		else if(propertyName.equalsIgnoreCase("Size")){
			// size is set as width+space+height: "400 100"
			String val = propertyValue.toString();
			int del = val.indexOf(" ");
			int w = Integer.parseInt(val.substring(0,del));
			int h = Integer.parseInt(val.substring(del+1));
			setSize(w,h);
		}
		} catch (DataFormattingException ex){
			throw new NoSuchPropertyException("No such InfoDialog property: "+propertyName);
		}
	}

	public Hashtable getProperty(Vector propertyNames) throws NoSuchPropertyException{
		return null;
	}
	
	// InfoDialog has 2 modes:
	// MODAL -- contains a html panel and OK button
	// not modal -- contains a html panel and a progress bar	
	public void setModal(boolean mod){	
		if(mod && !isModal){
		  remove(prPanel);
		  add("South", buttonPanel);
		}
		else if(!mod && isModal){
		  remove(buttonPanel);
		  add("South", prPanel);
		}
		isModal = mod;
		super.setModal(mod);	
	}
	
	public void setVisible(boolean visible) {
		if(visible){
			if(isMac && !isModal)
				startThread();
			else
				start();
		}	
		else{
			super.setVisible(visible);	
		  	setModal(false);
		  	stop();
		  }
	}
	
	public void resetTopPanel(Component toset){
		if(currTextObject == toset)
			return;
		if(currTextObject != null)
			remove(currTextObject);
		
		currTextObject = toset;
		if(!isMac){
			if(currTextObject == textLabel)
				remove(prPanel);
			else
				add("South", prPanel);
		}
		else{
			if(currTextObject == textLabel)
				currTextObject = null;
		}
		if(currTextObject != null)
			add("Center", toset);
	}
		
	
	public void delete(){
	}
		
	public  void run() {
		prPanel.startThread();
		toFront();
		super.setVisible(true);
		if(isMac && currTextObject == null && !isModal){
			Graphics gr = getGraphics();
			gr.setFont(msgFont);
			try{
				gr.drawString(textLabel.getText(), 50, 50);
			} finally {
				gr.dispose();
			}
		}
	}

	public void update(Graphics g){
		paint(g);
	}
	
	public void start() {
		run();
	}	
	
	public void stop() {
		stopThread();
		prPanel.stopThread();
	}
	
	public void startThread() {
		if(ownThread != null && ownThread.isAlive()) {
			ownThread.stop();
			ownThread = null;
		}
		ownThread = new Thread(this);
		ownThread.start();
	}
	
	public void stopThread() {
		if(ownThread != null && ownThread.isAlive()){
			bar.show((double)1.1);
			ownThread.stop();
			ownThread = null;
		}
	}
	
	
	
	

}