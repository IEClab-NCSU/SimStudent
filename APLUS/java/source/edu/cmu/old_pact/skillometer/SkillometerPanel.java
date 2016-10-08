package edu.cmu.old_pact.skillometer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Enumeration;
import java.util.Vector;

import edu.cmu.old_pact.scrollpanel.ScrollPanelClient;


public class SkillometerPanel extends ScrollPanelClient
{
    Vector skills = new Vector();
    
    public void clearSkills () {
		skills.removeAllElements();
		recalcLayout();
		validate();
		repaint();
	}

	public Frame getFrame() 
	{
		Component parent = getParent();
		Component root = null;
		
		while (parent != null) {
			root = parent;
			parent = parent.getParent();
		}
		return ((Frame) root);
	}
		
    public void addSkill (String description) {
    	Skill skill = getSkill(description);
    	if(skill == null){
    		skills.addElement(new Skill(description, skills.size(),this));
			repaint();
		}
    }
    
   	Skill getSkill(String name){
   		Skill toret = null;
   		int s = skills.size();
   		if(s == 0)
   			return toret;
   		String skName;
   		for(int i=0; i<s; i++){
   			skName = ((Skill)skills.elementAt(i)).getName();
   			if(skName.equalsIgnoreCase(name))
   				return (Skill)skills.elementAt(i);
   		}
   		return toret;
   	}
    
    public void updateSkillValue (String skillName, float value) {
    	Skill skill = getSkill (skillName);
    	if (skill != null) {
    		skill.setValue (value);
    		repaint();
    	}
    }
    
    public void repaint(){
    	recalcLayout();
		scrollToBottom();
		super.repaint();
    }
    
    void removeSkill(Skill skill){
    	skills.removeElement(skill);
    	repaint();
    }

    public void paint(Graphics g) {
		for (Enumeration e = skills.elements(); e.hasMoreElements();) {
			Skill s = (Skill) e.nextElement();
			s.paint(g);
		}
		//set the window title (this is a stupid place to do this -- we should probably have
		//and event triggered by the login, but...)
		//getFrame().setTitle(WordProblemTutor.getStudentName()+"'s skills");
    }
    
   
    private Image offScreenImage;
	private Dimension offScreenSize;
	private Graphics offScreenGraphics;
	
	public final synchronized void update (Graphics g) {
		Dimension d = size();
		
    	if ((offScreenImage == null) ||
    		(d.width != offScreenSize.width) ||
    		(d.height != offScreenSize.height))
    	{
			offScreenImage = createImage(d.width, d.height);
			offScreenSize = d;
			offScreenGraphics = offScreenImage.getGraphics();
			offScreenGraphics.setFont(g.getFont());
		}
		
		offScreenGraphics.setColor(getBackground());
		offScreenGraphics.fillRect(0, 0, d.width, d.height);
		offScreenGraphics.setColor(getForeground());

		paintAll(offScreenGraphics);
		g.drawImage(offScreenImage, 0, 0, null);
	}
}

