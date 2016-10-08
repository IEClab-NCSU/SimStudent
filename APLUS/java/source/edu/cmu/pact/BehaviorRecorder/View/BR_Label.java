
package edu.cmu.pact.BehaviorRecorder.View;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;

// used as node element in ESDigraph
public class BR_Label  extends JLabel  {
	private static final long serialVersionUID = -1589866493382948539L;
	private int uniqueID;
    Border lineBorder;
    FontMetrics fontMetrics;
    final Font largeFont = null;// = new Font ("Dialog", Font.PLAIN, 12);
    protected int padX = 10;
    protected int padY = 4;
    
    protected final int MAX_WIDTH = 100;
    protected final int widthPad = 25;
    
    public BR_Label() {
        this("unnamed", null);
    }
    
    public BR_Label(String labelTextP, EdgeData edgeData) {
        super(labelTextP);
        setFont(BRPanel.SMALL_FONT);
        setHorizontalAlignment(JLabel.CENTER);
        this.setVisible(true);
        if (edgeData != null)
        	uniqueID = edgeData.getUniqueID();
        lineBorder = BorderFactory.createLineBorder(Color.black);
        setBorder(lineBorder);
        doSize();
        setOpaque(true);
        //trace.out(5, this, "create new label: text = " + labelTextP );
    }
    
    public void doSize() {
        if (getFont() == null)
            return;
        
        fontMetrics = getFontMetrics(getFont());
        
    }
    
    public void setText(String text) {
        //		trace.out (5, this, "ESE_Label.setText(): text = " + text);
        super.setText(text);
        //doSize();
        Font font = this.getFont();
 
        if (font != null) {
            fontMetrics = getFontMetrics (font);
            int width = java.lang.Math.min(MAX_WIDTH, fontMetrics.stringWidth (text));
            int height = fontMetrics.getHeight ();
            this.setSize(new java.awt.Dimension(width + widthPad, height + padX));
        }
    }

    public void resetSize()
    {
    	//String temp = "none";
    Font font = this.getFont();
 
        
        if (font == null) 
            font = BRPanel.SMALL_FONT;
        
        fontMetrics = getFontMetrics (font);
//      int width = java.lang.Math.min(MAX_WIDTH, fontMetrics.stringWidth (getText()));
        int width = fontMetrics.stringWidth (getText());
        
        if (getIcon() != null)
            width += getIcon().getIconWidth() + 2;
        
        int height = fontMetrics.getHeight ();
        this.setSize(new java.awt.Dimension(width + padX, height + padY));
        
        return;
    }
    
    public boolean equals(BR_Label label) {
        return (this.uniqueID == label.uniqueID);
    }
    
    public int getUniqueID() {
        return uniqueID;
    }
    
    public void setUniqueID(int uniqueIDValue) {
        uniqueID = uniqueIDValue;
    }
}
