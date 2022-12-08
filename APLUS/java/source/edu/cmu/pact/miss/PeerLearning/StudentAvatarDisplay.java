package edu.cmu.pact.miss.PeerLearning;

import java.awt.Dimension;
import java.awt.Image;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

public class StudentAvatarDisplay extends JLayeredPane {

	private static final long serialVersionUID = 1L;
    
	private JLabel staticGraphic;
	JLabel backgroundLabel,hairLabel,/*eyeLabel,noseLabel,*/shirtLabel,faceLabel;
	String expression = SimStPLE.NORMAL_EXPRESSION;
	public static int PREFERRED_WIDTH = 125;
	public static int PREFERRED_HEIGHT = 140;
	int xOffset = 0;
	int yOffset = 0;
	
	public StudentAvatarDisplay()
	{
		super();
		setPreferredSize(new Dimension(PREFERRED_WIDTH,PREFERRED_HEIGHT));
		setUpDisplay(0, 0);
	}
	
	public StudentAvatarDisplay(String img)
	{
		super();
		setPreferredSize(new Dimension(PREFERRED_WIDTH,PREFERRED_HEIGHT));
		setUpDisplay(0, 0);
		setImage(img);
	}
	
	public StudentAvatarDisplay(int offset)
	{
		super();
		xOffset = offset;
		yOffset = offset;
		setPreferredSize(new Dimension(PREFERRED_WIDTH,PREFERRED_HEIGHT));
		setUpDisplay(offset, offset);
	}
        
        public StudentAvatarDisplay(int xOffset, int yOffset)
	{
		super();
		setPreferredSize(new Dimension(PREFERRED_WIDTH,PREFERRED_HEIGHT));
		setUpDisplay(xOffset, yOffset);
	}
	
	public StudentAvatarDisplay(String img, int offset)
	{
		super();
		xOffset = offset;
		yOffset = offset;
		setPreferredSize(new Dimension(PREFERRED_WIDTH,PREFERRED_HEIGHT));
		setUpDisplay(offset, offset);
		setImage(img);
	}
	
    protected void setUpDisplay(int xOffset, int yOffset)
    {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		
    	staticGraphic = new JLabel();
    	        
	    ImageIcon icon = createImageIcon("img/head1.png");
	    backgroundLabel = new JLabel(icon);
	    backgroundLabel.setBounds(xOffset,yOffset,icon.getIconWidth(),icon.getIconHeight());
	    add(backgroundLabel, new Integer(2));
	    icon = createImageIcon("img/hair1.png");
	    hairLabel = new JLabel(icon);
	    hairLabel.setBounds(xOffset,yOffset,icon.getIconWidth(),icon.getIconHeight());
	    add(hairLabel, new Integer(3));
	    /*icon = createImageIcon("img/happy.png");
	    eyeLabel = new JLabel(icon);
	    eyeLabel.setBounds(xOffset,yOffset,icon.getIconWidth(),icon.getIconHeight());
	    add(eyeLabel, new Integer(3));
	    icon = createImageIcon("img/nose1.png");
	    noseLabel = new JLabel(icon);
	    noseLabel.setBounds(xOffset,yOffset,icon.getIconWidth(),icon.getIconHeight());
	    add(noseLabel, new Integer(3));*/
	    icon = createImageIcon("img/shirt1.png");
	    shirtLabel = new JLabel(icon);
	    shirtLabel.setBounds(xOffset,yOffset,icon.getIconWidth(),icon.getIconHeight());
	    add(shirtLabel, new Integer(1));
	    icon = createImageIcon("img/face1.png");
	    faceLabel = new JLabel(icon);
	    faceLabel.setBounds(xOffset,yOffset,icon.getIconWidth(),icon.getIconHeight());
	    add(faceLabel, new Integer(3));
	    
            //uses teacher.png dimensions
	    staticGraphic.setBounds(0,0,PREFERRED_WIDTH,PREFERRED_HEIGHT);
	    add(staticGraphic,new Integer(4));
	    staticGraphic.setVisible(false);
    }
    
	public void setIsStaticGraphic(boolean isStatic)
	{
		staticGraphic.setVisible(isStatic);
        backgroundLabel.setVisible(!isStatic);
        hairLabel.setVisible(!isStatic);
        //eyeLabel.setVisible(!isStatic);
        //noseLabel.setVisible(!isStatic);
        shirtLabel.setVisible(!isStatic);
        faceLabel.setVisible(!isStatic);
	}
	
	public void setImage(String img, boolean on_paper) {
		Icon icon = createImageIcon(img);
		staticGraphic.setIcon(new ImageIcon(((ImageIcon) icon).getImage().getScaledInstance(130, 140, Image.SCALE_AREA_AVERAGING)));
		setStaticVisible(true);
	}
	
	public void setStaticVisible(boolean isVisible) {
		staticGraphic.setVisible(isVisible);
	}
    
    public void setImage(String img)
    {
    	if(!img.startsWith("%"))
    	{
    		//staticGraphic.setVisible(true);
    		Icon icon = createImageIcon(img);
    		staticGraphic.setIcon(icon);
    		
    	}
    	else
    	{
    		
    		//getSimStAvatorIcon().setVisible(false);
    		String[] parts = img.split("%");

            final ImageIcon icon = createImageIcon(parts[1]);
            final ImageIcon icon2 = createImageIcon(parts[2]);
            //final ImageIcon icon3 = createImageIcon(parts[3]);
            //final ImageIcon icon4 = createImageIcon(parts[4]);
            final ImageIcon icon5 = createImageIcon(parts[3]);
            final ImageIcon icon6 = createImageIcon(expression);
            
            backgroundLabel.setIcon(icon);
            hairLabel.setIcon(icon2);
            //eyeLabel.setIcon(icon3);
            //noseLabel.setIcon(icon4);
            shirtLabel.setIcon(icon5);
            faceLabel.setIcon(icon6);
    	}
    }
    
    public void setExpression(String expression)
    {
        final ImageIcon icon = createImageIcon(expression);
        faceLabel.setIcon(icon);
        this.expression = expression;
        
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected ImageIcon createImageIcon(String path) {
    	String file = "/edu/cmu/pact/miss/PeerLearning"+"/"+path;
    	URL url = this.getClass().getResource(file);
    	
    	return new ImageIcon(url);
    	
    }

	public void clearImage() {
		final ImageIcon icon = new ImageIcon();
        
        backgroundLabel.setIcon(icon);
        hairLabel.setIcon(icon);
        //eyeLabel.setIcon(icon);
        //noseLabel.setIcon(icon);
        shirtLabel.setIcon(icon);
        faceLabel.setIcon(icon);
		
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(PREFERRED_WIDTH+xOffset, PREFERRED_HEIGHT+yOffset);
	}

}
