/**
 * 
 * Curriculum Browser used in SimSt Peer Learning Environment
 * 
 */
package edu.cmu.pact.miss.PeerLearning;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.media.*;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.WebStartFileDownloader;

/**
 * @author mazda
 *
 */
public class CurriculumBrowser {

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Fields
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
	private CurriculumBrowserView cbView;
	private boolean htmlSet = false;
    
    private WebStartFileDownloader fileFinder;
  
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public WebStartFileDownloader getFileFinder() {
		return fileFinder;
	}

	public CurriculumBrowser(Dimension tutorPanelSize) {
        cbView = new CurriculumBrowserView(tutorPanelSize);
        fileFinder = new WebStartFileDownloader();
    }   
    
    public CurriculumBrowser() {
        cbView = new CurriculumBrowserView();
        fileFinder = new WebStartFileDownloader();
    }
    
    public JScrollPane getBrowserPane()
    {
    	JScrollPane panel = new JScrollPane(cbView.getBrowserPane());
    	return panel;
    }
    
    public void setBrowserPane(JEditorPane browserPane) {
    	cbView.setBrowserPane(browserPane);
    }
    
    public Container getVideoPanel()
    {
    	return cbView.getContentPane();
    }
    
    public boolean isHtmlSet()
    {
    	return htmlSet;
    }
    
    public void setVideoSource(File f)
    {
    	if(trace.getDebugCode("miss"))trace.out("miss", "setVideoSource file: " + f);
    	try {
			Player p = Manager.createRealizedPlayer(f.toURI().toURL());
	        cbView.setLayout(new BorderLayout());
			cbView.getContentPane().add(p.getVisualComponent(), BorderLayout.CENTER);
			cbView.getContentPane().add(p.getControlPanelComponent(),BorderLayout.NORTH);
		} catch (NoPlayerException e) {
			e.printStackTrace();
		} catch (CannotRealizeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	/*
    	try
    	{
    		cbView.getBrowserPane().setEditorKit(new HTMLEditorKit());
    		cbView.getBrowserPane().read(new FileReader(src), cbView.getBrowserPane().getDocument());
    	}
    	catch(IOException e)
    	{
        	e.printStackTrace();
    	}*/
    }
    
    public void setVideoSource(String src)
    {
    	String fileName = this.getFileFinder().findFile(src);
    	if(trace.getDebugCode("miss"))trace.out("miss", "setVideoSource fileName: " + fileName);
    	File f = new File(fileName);
    	try {
			Player p = Manager.createRealizedPlayer(f.toURI().toURL());
	        cbView.setLayout(new BorderLayout());
			cbView.getContentPane().add(p.getVisualComponent(), BorderLayout.CENTER);
			cbView.getContentPane().add(p.getControlPanelComponent(),BorderLayout.NORTH);
			
		} catch (NoPlayerException e) {
			e.printStackTrace();
		} catch (CannotRealizeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	/*
    	try
    	{
    		cbView.getBrowserPane().setEditorKit(new HTMLEditorKit());
    		cbView.getBrowserPane().read(new FileReader(src), cbView.getBrowserPane().getDocument());
    	}
    	catch(IOException e)
    	{
        	e.printStackTrace();
    	}*/
    }
    
    public void setHtmlSource(String src)
    {
    	
    	String fileName = this.getFileFinder().findFile(src);
    	if(trace.getDebugCode("miss"))trace.out("miss", "Inside setHtmlSource with fileName: " + fileName);
    	try
    	{
    		cbView.getBrowserPane().setEditorKit(new HTMLEditorKit());
    		//cbView.getBrowserPane().read(new FileReader(fileName), cbView.getBrowserPane().getDocument());
    		cbView.getBrowserPane().read(new FileReader(src), cbView.getBrowserPane().getDocument());
    		htmlSet = true;
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
