package edu.cmu.old_pact.skillometer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.DataFormatException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.fastbeanssupport.FastProBeansSupport;
import edu.cmu.old_pact.settings.ParameterSettings;
import edu.cmu.old_pact.settings.Settings;

//a Skill should be a panel. I don't know why it isn't
public class Skill implements ImageObserver, Sharable {
    private String		description = "";
    private float       value = (float) 0.25;
    private boolean     known;
    private int         position;
    private Image skillImage = null;
    private SkillometerPanel myPanel;
    private boolean imagePainted=false;
    private Color backgroundColor = Color.white;
    private Color skillColor = Color.green;
    private Color labelColor = Color.black;
    private Hashtable Properties;
    private SkillProxy sk_obj;
    private Font skillFont = Settings.skillLabelFont;
    protected FastProBeansSupport changes = new FastProBeansSupport(this);


    public Skill(String	     inDescription,
    		 	 int         inPosition,
    		 	 SkillometerPanel panel) 
    {
    	description = inDescription;
    	position = inPosition;
    	myPanel=panel;
    	Properties = new Hashtable();
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		changes.removePropertyChangeListener(l);
	}
    
    public void setSkillImage(String imagePath){
    	skillImage = null;
    	try{
    		skillImage = Settings.loadImage(myPanel, imagePath);
    	} catch (Exception e) { 
    		System.out.println("Skill setSkillImage: "+e);
    	}
    }
    
    public void setSkillColor(Color c){
    	skillColor = c;
    }
    
    public ObjectProxy getObjectProxy() {
		return sk_obj;
	}
	
	public void setProxyInRealObject(ObjectProxy op) {
		sk_obj = (SkillProxy)op;
	}
		
    public void setProperty(String propertyName, Object propertyValue) throws DorminException{
    	try{
		if(propertyName.equalsIgnoreCase("VALUE")){
			setValue(DataConverter.getFloatValue(propertyName, propertyValue));
		}
		else if (propertyName.equalsIgnoreCase("FONT")){
			Font f = ParameterSettings.getFont( propertyValue);
			if(f != null)
				setFont(f);
		}
		else if (propertyName.equalsIgnoreCase("LABELCOLOR")) {
			Color c = ParameterSettings.getColor(propertyValue);
			if(c != null)
				labelColor = c;
		}
		else if (propertyName.equalsIgnoreCase("SKILLCOLOR")) {
			Color c = ParameterSettings.getColor(propertyValue);
			setSkillColor(c);
		}
		else if (propertyName.equalsIgnoreCase("SKILLIMAGE")) {
			setSkillImage((String)propertyValue);
		}
		else if (propertyName.equalsIgnoreCase("ISCHECKED")) {
			boolean kn = DataConverter.getBooleanValue(propertyName, propertyValue);
			setKnownP(kn);
		}
		else {
			changes.firePropertyChange(propertyName, null, propertyValue);
			throw new NoSuchPropertyException ("Skill doesn't have property "+propertyName);
		}
		Properties.put(propertyName.toUpperCase(), propertyValue);
		repaint();
		} catch (DataFormattingException ex){
			throw new DataFormatException("For object Skill "+ex.getMessage());
		}
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
				throw new NoSuchPropertyException("Skill doesn't have property "+currName);
			toret.put(currName, ob);
		}
		return toret;
	}
	
	void repaint() {
		myPanel.repaint();
	}
	
	void setFont(Font f){
		skillFont = f;
	}
    
    public String getName(){
    	return description;
    }

    void setValue (float inValue) {
		value = inValue;
		imagePainted=false;
		if (inValue >= 0.95)
			known = true;
		else
			known = false;
		repaint();
    }

    void setKnownP (boolean newKnown) { 
		known = newKnown;
    }
    
    public boolean isValid() {
    	return imagePainted;
    }
    
    public void drawCheck(Graphics g,int centerX,int centerY, int sizeX,int sizeY)
   	{
   		g.drawLine(centerX-sizeX/2,centerY,centerX,centerY+sizeY/2+1);
   		g.drawLine(centerX,centerY+sizeY/2,centerX+sizeX/2,centerY-sizeY/2-1);
   	}	
    
    public Rectangle getBoundingBox(FontMetrics fm)
    {
		Rectangle rect=new Rectangle();
		
		rect.x=0;
		rect.y=4 + (position * 16);
		rect.height=16;
		rect.width=130+fm.stringWidth(description)+5;
		return rect;
	}
	
	//imageUpdate gets called when something happens with the image
	//we just care if the image loaded (ALLBITS)
	
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		if ((infoflags & ImageObserver.ALLBITS) != 0) {
			myPanel.repaint();
			imagePainted=true;
			return false; //don't send this event again
		}
		return true;
	}

    public void paint(Graphics g) 
    {
    	int start = 4 + (position * 16);
    	g.setColor(labelColor);
    	g.setFont(skillFont);
		g.drawString(description, 130, start+9);
		g.setColor(Settings.skillBarOutlineColor);
    	g.drawRect(20, start, 100, 12);
    	g.setColor(backgroundColor);
    	g.fillRect(20, start, 100, 12);
    	
    	if(skillImage != null )
			imagePainted = g.drawImage(skillImage,20,start, Math.round(100*value),12,this);
	
		else{
			g.setColor(skillColor);
			g.fillRect(20, start, Math.round(100*value), 12);
		}
    	if (known) {
			g.setColor(Settings.checkColor);
			drawCheck(g,8,start+6,6,8);
			drawCheck(g,9,start+6,6,8);
		}
    }
    
    public void delete(){
    	myPanel.removeSkill(this); 
    }
}
