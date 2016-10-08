//d:/Pact-CVS-Tree/Tutor_Java/./src/Middle-School/Java/dormin/toolframe/ModeLinePanel.java
package edu.cmu.old_pact.dormin.toolframe;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Panel;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.settings.ParameterSettings;

public class ModeLinePanel extends Panel implements Sharable{

	private SmallLabel modeLabel;
	private Font modeLineFont;
	private Color bgColor = new Color(250,250,210);
	private ObjectProxy mlProxy;
	
	public ModeLinePanel()
	{
		super();
		setLayout(new FlowLayout(FlowLayout.LEFT,32,0));	
				
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
		    modeLineFont = new Font("geneva", Font.PLAIN, 10); 
		else
			modeLineFont = new Font("arial", Font.PLAIN, 10);
			
		
		setBackground(bgColor);
				
		modeLabel = new SmallLabel();
		setFont(modeLineFont);
		modeLabel.setForeground(Color.white);   //black);
		modeLabel.setBackground(bgColor);
				
		this.add(modeLabel);
					
	}
	
	public void createProxy(ObjectProxy parent){
		mlProxy = new DorminToolProxy(parent, "ModeLine");
		mlProxy.setRealObject(this);
	}
	
	public void setProxyInRealObject(ObjectProxy op){
	}
	
	public Dimension  getPreferredSize() {
		Dimension dim = getSize();
		Dimension d = modeLabel.getPreferredSize();		
		return (new Dimension(dim.width, d.height));
	}
	
	
	public void setModeLineText(String text){	
		modeLabel.setText(text);
		//modeLabel.repaint();
		repaint();
	}
	
	public void setFont(Font f){
		if(f.getSize() == 0)
			f =new Font(f.getName(), f.getStyle(), getFont().getSize());
		super.setFont(f);	
		modeLabel.setFont(f);	
	}
	
	public void setBgColor(Color c){
		modeLabel.setBackground(c);
		setBackground(c);
	}
	
	public String getModeLineText(){			
		return modeLabel.getText();
	}	
	
	public ObjectProxy getObjectProxy() {
		return mlProxy;
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		if(propertyName.equalsIgnoreCase("VALUE")){	
		  ((DorminToolFrame)mlProxy.getRealParent()).setModeLine((String)propertyValue);
		}
		else if (propertyName.equalsIgnoreCase("FOREGROUNDCOLOR")){
			Color c = ParameterSettings.getColor(propertyValue);
			if(c != null)
				modeLabel.setForeground(c);
		}
		else if (propertyName.equalsIgnoreCase("BACKGROUNDCOLOR")){
			Color c = ParameterSettings.getColor(propertyValue);
			if(c != null)
				setBgColor(c);
		}
		else if(propertyName.equalsIgnoreCase("FONT")){
			Font f = ParameterSettings.getFont( propertyValue);
			if(f != null)
				setFont(f);
		}
		else if(propertyName.equalsIgnoreCase("FONTSTYLE")) {
				int intStyle = ParameterSettings.getFontStyle((String)propertyValue);
				Font f = getFont();
				if(f != null && f.getStyle() != intStyle) {				
					Font newF = new Font(f.getName(), intStyle, f.getSize());
					setFont(newF);
				}
		}		
		else {
  			throw new NoSuchPropertyException("No such ModeLine property: "+propertyName); 
  		}
	}
	
	public void delete(){
		if(getParent() != null)
			getParent().remove(this);
		mlProxy = null;
		modeLabel = null;
	}
	
	public Hashtable getProperty(Vector propertyNames) throws NoSuchPropertyException{
		return null;
	}
		
	public void clear(){
		removeAll();
		mlProxy = null;
		modeLabel = null;
	}
				
}
