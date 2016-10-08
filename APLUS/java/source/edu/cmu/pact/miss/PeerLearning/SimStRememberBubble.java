package edu.cmu.pact.miss.PeerLearning;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import edu.cmu.pact.Utilities.trace;

public class SimStRememberBubble extends JDialog{

		public static String THINK_BUBBLE_ICON_LEFT="img/speechq.png";
		public static String THINK_BUBBLE_ICON_RIGHT="img/speechqr.png";
		private final JPanel contentPanel = new JPanel();
		private JTextArea textArea;
		public static int LEFT=0;
		public static int RIGHT=1;
		
		
		
		
		public SimStRememberBubble(JComponent comp,int direction) {
		setUndecorated(true);
		ImageIcon thinkBubble=null;
		if (direction==RIGHT)
			thinkBubble=createImageIcon(THINK_BUBBLE_ICON_RIGHT);
		else 
			thinkBubble=createImageIcon(THINK_BUBBLE_ICON_LEFT);
		
		setBounds(0,0,thinkBubble.getIconWidth(),thinkBubble.getIconHeight());
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
       

        this.setLocationRelativeTo(comp);
    	//setLocation(20,40);
    
    	
    	//if (comp==null)
    	//	JOptionPane.showMessageDialog(null, "");
    		
      //  JLayeredPane layers = new JLayeredPane();
	//	layers.setPreferredSize(new Dimension(40,40));

		

		/*Create the panel to add the layers*/
	//	JPanel backPanel=new JPanel();
	//	backPanel.setOpaque(false);
		//auta ta evala sto huston
		this.getRootPane().setOpaque(false);
		
		
	//	setContentPane(new ContentPane());
		//this.getContentPane ().setBackground (new Color (255, 255, 255, AlphaComposite.CLEAR));
		this.setBackground (new Color (255, 255, 255, 128));	
		
		
		
    //    this.setBackground(Color.RED);
		
	
		
		// Image t = thinkBubble.getImage();
		// t.getScaledInstance(140, 140, Image.SCALE_SMOOTH);
		 
		// ImageIcon b=new ImageIcon(t);
		 
		/*add the Metatutor image layer*/
		JLabel metatutorLabel = new JLabel(thinkBubble);
		metatutorLabel.setLayout(new BorderLayout());
		metatutorLabel.setOpaque(false);
		//metatutorLabel.setIcon(b);
		metatutorLabel.setBounds(0,0,thinkBubble.getIconWidth(),thinkBubble.getIconHeight());
	
		
		
		//layers.add(metatutorLabel,new Integer(100));
		
		this.add(metatutorLabel);
		
		this.setAlwaysOnTop(true);
		
		
			
		setVisible(true);

	}




	public ImageIcon createImageIcon(String path) {
		String file = "/edu/cmu/pact/miss/PeerLearning"+"/"+path;
		URL url = this.getClass().getResource(file);

		return new ImageIcon(url); 

	}
	
	public class ContentPane extends JPanel {

	    public ContentPane() {

	        setOpaque(false);

	    }

	    @Override
	    protected void paintComponent(Graphics g) {

	        // Allow super to paint
	        super.paintComponent(g);

	        // Apply our own painting effect
	        Graphics2D g2d = (Graphics2D) g.create();
	        // 50% transparent Alpha
	        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

	        g2d.setColor(getBackground());
	        g2d.fill(getBounds());

	        g2d.dispose();

	    }

	}
	
	
}
