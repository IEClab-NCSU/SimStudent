package edu.cmu.pact.miss.PeerLearning;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import edu.cmu.pact.Utilities.trace;

public class AplusSpotlight extends JDialog {
	private final JPanel contentPanel = new JPanel();
	SemiTransparentPanel firstPanel=null;
	SemiTransparentPanel secondPanel=null;
	SemiTransparentPanel thirdPanel=null;
	SpotlightPanel spotlight=null;
	SimStRememberBubble thinkBubble=null;
	SpotlightPanelBubble spotlightBubble=null;
	/**
	 * Constructor that does not display a think bubble
	 * @param aplusWindow
	 * @param highlightedComponent
	 * @param dialog
	 */
	public  AplusSpotlight(SimStPeerTutoringPlatform aplusWindow,JPanel highlightedComponent,JDialog dialog){	
		this(aplusWindow, highlightedComponent,-3,dialog);
	}
	
	/**
	 * Constructor that does not create a think bubble neither displayes a dialog. It just highlights the component
	 * @param aplusWindow
	 * @param highlightedComponent
	 */
	public  AplusSpotlight(SimStPeerTutoringPlatform aplusWindow,JPanel highlightedComponent){	
		this(aplusWindow, highlightedComponent,-3,null);
	}
	
	/**
	 * General constructor 
	 * @param aplusWindow
	 * @param highlightedComponent
	 * @param bubbleDirection
	 * @param dialog
	 */
	public AplusSpotlight(SimStPeerTutoringPlatform aplusWindow,JPanel highlightedComponent,int bubbleDirection,JDialog dialog){
		
		int overalWidth=aplusWindow.getWidth();
		int width=overalWidth;
		int height=aplusWindow.getHeight();
		Point location=aplusWindow.getLocationOnScreen();
	

		spotlight =new SpotlightPanel(location, width,height, highlightedComponent.getLocationOnScreen(),highlightedComponent.getWidth()/2,highlightedComponent.getWidth(),highlightedComponent.getHeight());
		
		if (bubbleDirection==SimStRememberBubble.RIGHT)
			spotlightBubble =new SpotlightPanelBubble(highlightedComponent, location, width,height, highlightedComponent.getLocationOnScreen(),highlightedComponent.getWidth()/2,highlightedComponent.getWidth(),highlightedComponent.getHeight(),SimStRememberBubble.RIGHT);
		else if (bubbleDirection==SimStRememberBubble.LEFT)
			spotlightBubble =new SpotlightPanelBubble(highlightedComponent, location, width,height, highlightedComponent.getLocationOnScreen(),highlightedComponent.getWidth()/2,highlightedComponent.getWidth(),highlightedComponent.getHeight(),SimStRememberBubble.LEFT);


			
		if (dialog!=null)	
			dialog.setVisible(true);
		

		
	}
	
	/**
	 * Method that makes the spotlight dissapear
	 */
	public void removeSpotlight(){
		/*firstPanel.setVisible(false);
		secondPanel.setVisible(false);
		thirdPanel.setVisible(false);
		if (thinkBubble!=null)
			thinkBubble.setVisible(false);
		*/
		
		if (spotlight!=null) spotlight.setVisible(false);
		if (spotlightBubble!=null) spotlightBubble.setVisible(false);
		if (thinkBubble!=null) thinkBubble.setVisible(false);

	}
	public static String THINK_BUBBLE_ICON_LEFT="img/speechq.png";
	
	/**
	 * Class for creating the spotlight. It paints a semi-transparent rectangle that covers APLUS and 
	 * a small circle that we remove its color.
	 * @author simstudent
	 *
	 */
	public class SpotlightPanel extends JDialog{ 
		
		/**
		 * Contructor to create the spotlight
		 * @param location upper left corner of the window we want to show the spotlight
		 * @param width	the width of the window we want to show the spotlight
		 * @param height the height of the window we want to show the spotlight
		 * @param spotlightLocation	upper left corner of the component we want to spotlight
		 * @param spotlightWidth width of the spotlight
		 * @param highlightedComponentWidth width of the component we want to spotlight
		 * @param highlightedComponentHeight height of the component we want to spotlight
		 */
		
		public SpotlightPanel(Point location, int width, int height, Point spotlightLocation, int spotlightWidth, int highlightedComponentWidth, int highlightedComponentHeight) {
			setUndecorated(true);			
			//setBounds(0,0,aplusWindow.getWidth()-AplusPlatform.METATUTOR_IMAGE_WIDTH-100,aplusWindow.getHeight());
			setBounds(0,0,width,height);
			
			//setBounds(0,0,aplusWindow.layeredIcon.WIDTH,aplusWindow.getHeight());
			getContentPane().setLayout(new BorderLayout());
			contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			
			this.getRootPane().setOpaque (false);
			this.getContentPane().setBackground (new Color (0, 0, 0, 0));
			this.setBackground (new Color (255, 0, 0, 0));	
			//this.setBackground(new Color(0, 0, 0, 0));
			
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	        setUndecorated(true);

	        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	        Graphics2D g = img.createGraphics();

	        	
	        int ovalX = spotlightLocation.x - location.x+highlightedComponentWidth/2;
	        int ovalY = spotlightLocation.y - location.y+highlightedComponentHeight/2;
	        int ovalRadius = spotlightWidth;

	        /* Draw the grey rectangle */
	        g.setColor(new Color(0, 0, 0, 183));
	        g.fillRect(0, 0, width, height);

	        /* Enable Anti-Alias */
	        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	        /* Clear the circle away */
	    	Composite c = AlphaComposite.getInstance(AlphaComposite.CLEAR,.1f);
	    	g.setComposite(c);
	        g.fillOval(ovalX - ovalRadius, ovalY - ovalRadius, 2 * ovalRadius, 2 * ovalRadius);
	        	        
	        
	        g.dispose();      
	        getContentPane().add(new JLabel(new ImageIcon(img)));
	                        
	        this.setLocation(location);
	        

	        
	        this.repaint();
	       
			setVisible(true);
		}
			
	}
	
	
	
public class SpotlightPanelBubble extends JDialog{ 
		
		/**
		 * Contructor to create the spotlight
		 * @param location upper left corner of the window we want to show the spotlight
		 * @param width	the width of the window we want to show the spotlight
		 * @param height the height of the window we want to show the spotlight
		 * @param spotlightLocation	upper left corner of the component we want to spotlight
		 * @param spotlightWidth width of the spotlight
		 * @param highlightedComponentWidth width of the component we want to spotlight
		 * @param highlightedComponentHeight height of the component we want to spotlight
		 */
		
		public SpotlightPanelBubble(JComponent comp,Point location, int width, int height, Point spotlightLocation, int spotlightWidth, int highlightedComponentWidth, int highlightedComponentHeight,int direction) {
			setUndecorated(true);			
			ImageIcon thinkBubble=null;
			if (direction==SimStRememberBubble.RIGHT)
				thinkBubble=createImageIcon(SimStRememberBubble.THINK_BUBBLE_ICON_RIGHT);
			else 
				thinkBubble=createImageIcon(SimStRememberBubble.THINK_BUBBLE_ICON_LEFT);
			
			
			
			setBounds(0,0,width,height);
			
			//setBounds(spotlightLocation.x,spotlightLocation.y,thinkBubble.getIconWidth(),thinkBubble.getIconHeight());
			
			//setBounds(0,0,aplusWindow.layeredIcon.WIDTH,aplusWindow.getHeight());
			getContentPane().setLayout(null);
			contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			
			this.getRootPane ().setOpaque (false);
			this.getContentPane ().setBackground (new Color (0, 0, 0, 0));
			this.setBackground (new Color (255, 0, 0, 0));	
			//this.setBackground(new Color(0, 0, 0, 0));
			
			
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	        setUndecorated(true);

	        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	        Graphics2D g = img.createGraphics();

	        	
	        int ovalX = spotlightLocation.x - location.x+highlightedComponentWidth/2;
	        int ovalY = spotlightLocation.y - location.y+highlightedComponentHeight/2;
	        int ovalRadius = spotlightWidth;

	        /* Draw the grey rectangle */
	        g.setColor(new Color(0, 0, 0, 83));
	        g.fillRect(0, 0, width, height);

	        /* Enable Anti-Alias */
	        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	        /* Clear the circle away */
	    	Composite c = AlphaComposite.getInstance(AlphaComposite.CLEAR,.1f);
	    	g.setComposite(c);
	        g.fillOval(ovalX - ovalRadius, ovalY - ovalRadius, 2 * ovalRadius, 2 * ovalRadius);
	        	        
	        
	        g.dispose();      
	        getContentPane().add(new JLabel(new ImageIcon(img)));
	                        
	        this.setLocation(location);
	        
	        
	       
			int offset = 0; 
			
			if (direction==SimStRememberBubble.RIGHT)
				offset=20;
	        JLabel bubble = new JLabel(thinkBubble);
	        bubble.setLayout(null);
	        bubble.setOpaque(false);
		
	        bubble.setBounds(ovalX-highlightedComponentWidth/2-offset ,ovalY-highlightedComponentHeight/2,thinkBubble.getIconWidth(),thinkBubble.getIconHeight());
			
			
			this.add(bubble);
		
			//this.setLocationRelativeTo(comp);
			
			
			 setVisible(true);

		}
			
	}



	public ImageIcon createImageIcon(String path) {
		String file = "/edu/cmu/pact/miss/PeerLearning"+"/"+path;
		URL url = this.getClass().getResource(file);

		return new ImageIcon(url); 

	}
	
	
	
	/**
	 * Obsolete class that displays a semi-transparent panel over a JComponent.
	 * Used initially to build the "spotlight" but eventually was not used. 
	 * @author simstudent
	 *
	 */
	private class SemiTransparentPanel extends JDialog{ 
		
		public SemiTransparentPanel(Point location, int width, int height) {
			setUndecorated(true);			
			setBounds(0,0,width,height);
			
			getContentPane().setLayout(new BorderLayout());
			contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			this.setBackground(new Color(0, 0, 0, 123));
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	        setUndecorated(true);

	        this.setLocation(location);
	        
	        
	        JLayeredPane layers = new JLayeredPane();
			layers.setPreferredSize(new Dimension(40,40));
			this.setAlwaysOnTop(true);
					
			this.add(layers);
			setVisible(true);
		}
	
		
	}
	
	
	
	
	
}
