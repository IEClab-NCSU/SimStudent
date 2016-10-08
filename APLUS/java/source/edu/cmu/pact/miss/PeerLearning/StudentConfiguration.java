/**
 * 
 * Curriculum Browser used in SimSt Peer Learning Environment
 * 
 */
package edu.cmu.pact.miss.PeerLearning;

import java.awt.Dimension;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import edu.cmu.pact.Utilities.trace;

/**
 * @author mazda
 *
 */
public class StudentConfiguration {

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Fields
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
	private CurriculumBrowserView cbView;
    
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public StudentConfiguration(Dimension tutorPanelSize) {
        cbView = new CurriculumBrowserView(tutorPanelSize);

    }   
    
    public StudentConfiguration() {
        cbView = new CurriculumBrowserView();

    }
    
    public JEditorPane getBrowserPane()
    {
    	return cbView.getBrowserPane();
    }
    
    public void setBrowserPane(JEditorPane browserPane) {
    	cbView.setBrowserPane(browserPane);
    }
    
    public void setHTMLSource(String src)
    {
    	try
    	{
    		cbView.getBrowserPane().setEditorKit(new HTMLEditorKit());
    		cbView.getBrowserPane().read(new FileReader(src), cbView.getBrowserPane().getDocument());
    	}
    	catch(IOException e)
    	{
    		if(trace.getDebugCode("miss"))trace.out("miss", "Error setting page of curriculum browser "+e.getMessage());
        	e.printStackTrace();
    	}
    }
    
    
    public void setTitle(String title)
    {
    	cbView.setTitle(title);
    }
    
    public String getTitle()
    {
    	return cbView.getTitle();
    }
    
    public void showCB()
    {
    	cbView.setVisible(true);
    	cbView.requestFocus();
    }

}
