package edu.cmu.pact.miss.PeerLearning;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import edu.cmu.pact.miss.SimSt;
/**
 * Class that extends JProgress bar to facilitate showing the skillometer when 
 * clicking on the progress bar.
 * 
 * @author nbarba
 *
 */
public class ClickableProgressBar extends JProgressBar implements MouseListener{

	
	/*The index of the progressBar inside the quiz/section progress / skillometer */
	int index;
	void setIndex(int i){index=i;}
	int getIndex(){return index;}
	
	boolean isBarClickable=false;
	void setIsBarClickable(boolean flag){this.isBarClickable=flag;}
	boolean getIsBarClickable(){return this.isBarClickable;}
	
	SimStPLE simStPLE;
	void setSimStPLE(SimStPLE simStPLE){this.simStPLE=simStPLE;}
	SimStPLE getSimStPLE(){return this.simStPLE;}
	
	
	public ClickableProgressBar(int min, int max){
		super(min,max);
		
	/*	setValue(0);
		setStringPainted(true);
		setString("Unmastered");
		setInsets(0, 2, 2, 2);	
	    setBorder(BorderFactory.createEmptyBorder(5, 5, 1, 5));
	    setBackground(Color.WHITE);
		setToolTipText("Unmastered");
		setAlignmentX(LEFT_ALIGNMENT);
		*/
	}

	public ClickableProgressBar(int min, int max, boolean isClickable,int sectionIndex,SimStPLE simStPLE){
		super(min,max);
		setIsBarClickable(isClickable);
		setIndex(sectionIndex);
		addMouseListener(this);
		setSimStPLE(simStPLE);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
			if (isBarClickable){
					
				  //Retrieves the mouse position relative to the component origin.
			       int mouseX = e.getX();

			       //Computes how far along the mouse is relative to the component width then multiply it by the progress bar's maximum value.
			       int progressBarVal = (int)Math.round(((double)mouseX / (double)getWidth()) * getMaximum());

			       
			       JPanel parentPanel=((AplusPlatform) getSimStPLE().getSimStPeerTutoringPlatform()).getSectionMeterPanel();
			       Skillometer skillometer = new Skillometer(this.getSimStPLE().getSsCognitiveTutor().getSimStBKT().getMasteryPerSkill(),parentPanel,getSimStPLE().logger);
			       
			       skillometer.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			       skillometer.setVisible(true);
			       
			}
	     	
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	

	
}


