/**
 * 
 * Curriculum Browser used in SimSt Peer Learning Environment
 * 
 */
package edu.cmu.pact.miss.PeerLearning;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import edu.cmu.pact.Utilities.trace;

/**
 * @author mazda
 *
 */
public class CurriculumBrowserView extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Fields
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    private int cbWidth = 640;
    private int cbHeight = 520;
    final String cbTitle = "Introduction to SimStudent";
    
    private JEditorPane browserPane = new JEditorPane();
    public JEditorPane getBrowserPane() { return browserPane; }
    public void setBrowserPane(JEditorPane browserPane) {
        this.browserPane = browserPane;
    }
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public CurriculumBrowserView() {
        
    	Dimension size = new Dimension(cbWidth,cbHeight);
        setBackground(Color.CYAN);
        this.setPreferredSize(size);
        this.setSize(size);
        this.setTitle(this.cbTitle);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

    	getBrowserPane().setEditable(false);
    }
    
    
    public CurriculumBrowserView(Dimension size) {
        
    	cbWidth = (int) size.getWidth();
    	cbHeight = (int) size.getHeight();
        setBackground(Color.CYAN);
        this.setPreferredSize(size);
        this.setSize(size);
        this.setTitle(this.cbTitle);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

    	getBrowserPane().setEditable(false);
    	getBrowserPane().setPreferredSize(size);
    }
    
    

}
