package edu.cmu.pact.miss.PeerLearning;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JProgressBar;

public class CogTutorProgressBar extends JProgressBar {
	public static final Font BAR_FONT = new Font("Comic Sans MS", Font.PLAIN, 13);

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

    
    public CogTutorProgressBar() {
        progressColor = PREFERRED_PROGRESS_COLOR;
    }

    public CogTutorProgressBar(int min, int max) {
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

        if (getValue()>95){
        	g2d.setColor(masteredColor);
        	str="Mastered";
        }
        else if (getValue()>50){
        	g2d.setColor(almostMasteredColor);
        	str="Un-Mastered";
        }
        else {
        	g2d.setColor(unMasteredColor);
        	str="Un-Mastered";
        }
        
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