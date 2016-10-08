package edu.cmu.pact.miss.PeerLearning;


import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;


public class Skillometer extends JDialog implements WindowListener{
	public static final Font BAR_FONT = new Font("Comic Sans MS", Font.PLAIN, 13);
	public static final Font TITLE_FONT = new Font("Comic Sans MS", Font.PLAIN, 18);
	public static final String SKILLOMETER_TITLE="Skillometer";
	
	
	ArrayList<CustomProgressBar> skillBars;
	private final JPanel contentPanel = new JPanel();
	Color barColor = new Color(123,167,157);
	
	SimStLogger simStLogger;
	void setSimStLogger(SimStLogger logger){this.simStLogger=logger;}
	SimStLogger getSimStLogger(){return this.simStLogger;}
	
	
	long openTime;
	
	
    /**
     * Main constructor
     * @param skills a linked hash map (so we maintain insertion order) containing skill name --> skill score
     * @param parent the parnt JComponent used to align the Skillometer window.
     */
	public Skillometer(Map<String, Integer> skills, JComponent parent,SimStLogger logger) {
	
		setSimStLogger(logger);
		createSkillBars(skills);	
		init(parent);
		
		openTime = (new Date()).getTime();
		
		/*Log the fact that students clicked on to see the skillometer. */
	    getSimStLogger().simStLog(SimStLogger.SIM_STUDENT_SKILLOMETER, SimStLogger.SKILLOMETER_OPEN, "");
	       	
	}
	
	/**
	 * Method to create the skill bars.
	 * @param skills
	 */
	void createSkillBars(Map<String, Integer> skills){
		

		skillBars = new ArrayList<CustomProgressBar>();
					
		for (Map.Entry<String,Integer> entry : skills.entrySet()) {
			CustomProgressBar bar=new CustomProgressBar(0,100);	
			bar.setValue(entry.getValue());
			bar.setStringPainted(true);
			bar.setString(entry.getKey());
			
			
			bar.setInsets(0, 2, 2, 2);
			
		    bar.setBorder(BorderFactory.createEmptyBorder(5, 5, 1, 5));
		    bar.setBackground(Color.WHITE);
			bar.setToolTipText(""+entry.getValue() + "%");
			bar.setAlignmentX(LEFT_ALIGNMENT);
			skillBars.add(bar);
			
		}
		
	}

	
	
	/**
	 * Method to initalize and show the window. It adds the bars to the window, 
	 * and sets the window listener. 
	 * @param parent
	 */
	void init(JComponent parent){
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		setLocationRelativeTo(parent);
		
		contentPanel.setLayout(new GridLayout(0,1));
		contentPanel.setBackground(Color.WHITE);
		
		/*add the title*/
		JLabel title = new JLabel(SKILLOMETER_TITLE,SwingConstants.CENTER);
		title.setFont(TITLE_FONT);
		contentPanel.add(title);
		
		for (JProgressBar bar : skillBars)
			contentPanel.add(bar);
		
		contentPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		this.addWindowListener(this);
		setUndecorated(true);
		
	}
	
	@Override
	public void windowDeactivated(WindowEvent e) {
		
		/*Log the fact that students closed the skillometer. */
		long endTime = (new Date()).getTime();
		int duration = (int) (endTime - openTime);
		 
	    getSimStLogger().simStLog(SimStLogger.SIM_STUDENT_SKILLOMETER, SimStLogger.SKILLOMETER_CLOSED, "", "", "", duration);
	  
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		
	}
	
		
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	
	
	
	public class CustomProgressBar extends JProgressBar {

	    Color borderColor = new Color(0,0,0);

	    Composite nonTransparent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
	    

	    GradientPaint gradient;

	    private int oldWidth;
	    private int oldHeight;

	    private int displayWidth;
	    private int displayHeight;

	    private int insets[] = new int[4];
	    private static final int TOP_INSET = 0;
	    private static final int LEFT_INSET = 1;
	    private static final int BOTTOM_INSET = 2;
	    private static final int RIGHT_INSET = 3;

	    
	    private static final int LEFT_ALIGN_OFFSET = 10;
	    
	    
	    
	    int PREFERRED_PERCENT_STRING_MARGIN_WIDTH = 3;

	    Color PREFERRED_PROGRESS_COLOR =  new Color(123,167,157);
	    Color masteredColor = new Color(180,233,70);
	    Color unMasteredColor = new Color(250,190,76);
	    Color almostMasteredColor = new Color(250,221,79);
	    
	    private boolean percentStringVisible = true;	

	    private Color progressColor;

	    private String maxPercentString;

	    
	    public CustomProgressBar() {
	        progressColor = PREFERRED_PROGRESS_COLOR;
	    }

	    public CustomProgressBar(int min, int max) {
	    	super(min,max);
	        progressColor = PREFERRED_PROGRESS_COLOR;
	    }
	    
	    
	    public void updateGraphics() {
	        update(getGraphics());
	    }

	    @Override
	    protected void paintComponent(Graphics g) {
	        int w = displayWidth != 0 ? displayWidth - 1 : getWidth() - 1;
	        int h = displayHeight != 0 ? displayHeight - 1 : getHeight() - 1;

	        int x = insets[LEFT_INSET];
	        int y = insets[TOP_INSET];
	        w -= (insets[RIGHT_INSET] << 1);
	        h -= (insets[BOTTOM_INSET] << 1);

	        Graphics2D g2d = (Graphics2D) g;
	        // Clean background
	        if (isOpaque()) {
	            g2d.setColor(getBackground());
	            g2d.fillRect(0, 0, getWidth(), getHeight());
	        }

	        g2d.translate(x, y);

	        // Paint border
	        g2d.setColor(borderColor);
	        g2d.drawLine(0, 0, w , 0);
	        g2d.drawLine(0, h, w , h);
	        g2d.drawLine(0, 0, 0, h);
	        g2d.drawLine(w, 0, w, h);

	        // Fill in the progress
	        int min = getMinimum();
	        int max = getMaximum();
	        int total = max - min;
	        float dx = (float) (w - 2) / (float) total;
	        int value = getValue();
	        int progress = 0; 
	        if (value == max) {
	            progress = w ;
	        } else {
	            progress = (int) (dx * getValue());            
	        }

	        if (getValue()>=95)
	        	g2d.setColor(masteredColor);
	        else if (getValue()>50)
	        	g2d.setColor(almostMasteredColor);
	        else g2d.setColor(unMasteredColor);
	        
	        g2d.fillRect(1, 1, progress, h - 1);

	       
	        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	        g2d.setComposite(nonTransparent);
	        g2d.setColor(Color.black);
	        g2d.setFont(BAR_FONT);
	        g2d.drawString(str, LEFT_ALIGN_OFFSET, this.getHeight()/2+5);
	            
	    }
	    
	    
	    String str;
	    public void setString(String str){
	    	this.str=str;
	    }
	    public void setInsets(int top, int left, int bottom, int right) {
	        insets[TOP_INSET] = top;
	        insets[LEFT_INSET] = left;
	        insets[BOTTOM_INSET] = bottom;
	        insets[RIGHT_INSET] = right;
	    }

	    public void setPercentStringVisible(boolean percentStringVisible) {
	        this.percentStringVisible = percentStringVisible;
	    }

	    @Override
	    protected void paintBorder(Graphics g) {
	    }

	    @Override
	    public void validate() {
	        int w = getWidth();
	        int h = getHeight();

	        super.validate();
	        if (oldWidth != w || oldHeight != h) {
	            oldWidth = w;
	            oldHeight = h;
	            gradient = null;
	        }
	    }

	    @Override
	    public void setMaximum(int n) {
	        super.setMaximum(n);
	        maxPercentString = Integer.toString(n, 10) + "%";
	    }

	    public void setDisplaySize(int width, int height) {
	        displayWidth = width;
	        displayHeight = height;
	    }

	    public Color getProgressColor() {
	        return progressColor;
	    }

	    public void setProgressColor(Color progressColor) {
	        this.progressColor = progressColor;
	    }

}

}