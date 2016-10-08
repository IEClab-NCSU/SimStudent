package edu.cmu.old_pact.skillometer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.settings.Settings;

public class SkillometerFrame extends edu.cmu.old_pact.dormin.toolframe.DorminToolFrame implements Sharable{
	edu.cmu.old_pact.scrollpanel.LightComponentScroller m_ScrollPanel;
	SkillometerPanel m_ClientPanel;
	SkillometerProxy skm_obj;

	public SkillometerFrame (String name) {
		super("Skillometer");
		setTitle(name);
		setup();
		updateSizeAndLocation("Skillometer");
		try{
			getModeLine().setProperty("ForegroundColor", Color.black);
		}
		catch (DorminException e) { }
	}

	public void setup()
	{
		setLayout(new BorderLayout());
		setCurrentLocation(new Point(0,0));
		setCurrentWidth(400);
		setCurrentHeight(250);
		reshape(0, 0, 400, 250);
		setForeground(Color.black);
		setBackground(new Color(204, 204, 204));
		setFont(new Font("", 0, 0));
		setEnabled(true);
		setResizable(true);
		
		m_ClientPanel = new SkillometerPanel();
		m_ClientPanel.setLayout(new SkillometerPanelLayout());
		m_ClientPanel.setForeground(Settings.skillTextColor);
		m_ClientPanel.setBackground(Settings.skillometerBackgroundColor);
		m_ClientPanel.setFont(Settings.skillLabelFont);
		m_ClientPanel.setEnabled(true);
		
		
		m_ScrollPanel=new edu.cmu.old_pact.scrollpanel.LightComponentScroller(m_ClientPanel);
		m_ScrollPanel.setBackground(Settings.skillometerBackgroundColor);
		m_ScrollPanel.setForeground(Settings.skillTextColor);
		//setMenuBar(new MergedToolMenuBar());

		add("West",m_ToolBarPanel);
		setupToolBar(m_ToolBarPanel);
		add("Center",m_ScrollPanel);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                setVisible(false);
	        	dispose();
            }
        });
	}
	
	public ObjectProxy getObjectProxy() {
		return skm_obj;
	}
	
	public void setProxyInRealObject(ObjectProxy op) {
		skm_obj = (SkillometerProxy)op;
		setToolFrameProxy(skm_obj);
	}
	
	private void setupToolBar(edu.cmu.old_pact.toolframe.ToolBarPanel tb) {
		tb.setBackground(Settings.skillometerToolBarColor);
		tb.setInsets(new Insets(0,0,0,0));
		tb.addSeparator();
//		tb.addToolBarImage(Settings.skillometerLabel,Settings.skillometerLabelSize);
	}
	
	public void setTitle(String userName){
		super.setTitle(userName+"'s skills");
	}
	
	public void requestFocus(){
		super.requestFocus();
		m_ScrollPanel.requestFocus();
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		try{
		if (propertyName.equalsIgnoreCase("USERNAME")){
			setTitle((String)propertyValue);
			getAllProperties().put(propertyName.toUpperCase(), propertyValue);
		}
		else
			super.setProperty(propertyName, propertyValue);
		} catch (NoSuchPropertyException e){
			throw e;
		} 
	}
	 
	public void updateSkillValue (String skill, float value) {
		m_ClientPanel.updateSkillValue (skill, value);
	}
	
	public void removeSkill(Skill skill){
		 m_ClientPanel.removeSkill(skill);
		 repaint();
	}
	
	public void clearSkills (){
	
		 m_ClientPanel.clearSkills ();
		 repaint();
	}

	
	
	public void addSkill(String skillName){
		m_ClientPanel.addSkill(skillName);
	}
	
	public Skill getSkill(String skillName){
		return m_ClientPanel.getSkill(skillName);
	}
	
	public void delete(){
		removeAll();
		super.delete();
	}
}
