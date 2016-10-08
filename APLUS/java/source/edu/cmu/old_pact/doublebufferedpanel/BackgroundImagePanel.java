package edu.cmu.old_pact.doublebufferedpanel;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class BackgroundImagePanel extends Container implements PropertyChangeListener{
	Image backgroundImage = null;
	private int width=300, height=270;
	private String directory="";
	
	public BackgroundImagePanel(){
		setLayout(null);
	}
	
	public void update(Graphics g){
		paint(g);
	}
	
	public Dimension preferredSize(){
		if(backgroundImage != null)
			return new Dimension(backgroundImage.getWidth(this), backgroundImage.getHeight(this));
		else return new Dimension(width, height);
	}
	
	public void paint(Graphics g){	
		if(backgroundImage != null)
			g.drawImage(backgroundImage,0,0,this);
		setLocation(0,0);
		super.paint(g);
	}
	
	public  void propertyChange(PropertyChangeEvent evt){
		String eventName = evt.getPropertyName();
		if(eventName.equalsIgnoreCase("DIRECTORY")) 
			directory = (String)evt.getNewValue();
		else if (eventName.equalsIgnoreCase("DIAGRAMNAME"))
			 setDiagramImage((String)evt.getNewValue());
	}
	
	public void delete(){
		removeAll();
		backgroundImage = null;
	}
	
	private void setDiagramImage(String diagramName){
		String dir;
		if(!directory.equals(""))
			dir = directory+File.separator+diagramName;
		else
			dir = diagramName;
		backgroundImage = Toolkit.getDefaultToolkit().getImage(dir);
		MediaTracker tracker = new MediaTracker(this);
		try{
   			tracker.addImage(backgroundImage, 0);
   			tracker.waitForAll();
   		} catch (InterruptedException e){ }
   		setSize(preferredSize());
   		repaint();
   	}
   	
   	public void setImage(Image image){
   		backgroundImage = image;
   		setSize(preferredSize());
   		repaint();
   	}
   	
   	
}