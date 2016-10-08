package edu.cmu.old_pact.cl.util.menufactory;


import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;

import edu.cmu.old_pact.htmlPanel.HtmlPanel;
import edu.cmu.old_pact.settings.Settings;



public class AboutTutorWindow extends Frame {
	protected Font buttonFont; 	
	protected Color bgColor = new Color(240,240,245);
	private String tutorVersion;
	private HtmlPanel htmlPanel_other;
	private Checkbox showDetailsCB;
		
	public AboutTutorWindow (String urlBase, String version) { 
		super();
		
		tutorVersion = version;

		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC")){
		  buttonFont = new Font("geneva", Font.PLAIN, 12);
		} else {
		    buttonFont = new Font("arial", Font.PLAIN, 12);
		}
		
		setTitle("About Tutor");
		setLayout(new BorderLayout());		
		setBackground(bgColor);
		
		HtmlPanel htmlPanel_cl = new HtmlPanel(350, 100, bgColor, false);	
		add("North",htmlPanel_cl); 
		htmlPanel_cl.setURLBase(urlBase);    
		htmlPanel_cl.displayHtml(createClText());
		
		Panel buttonPanel = createOkPanel();
 		add("Center", buttonPanel);
	
		htmlPanel_other = new HtmlPanel(350, 250, bgColor, false);	
		add("South",htmlPanel_other); 
		htmlPanel_other.setURLBase(urlBase);    
		htmlPanel_other.displayHtml(createOtherText());
		
		// set CarnegieLearning logo as icon
		setIconImage(Settings.loadImage(this, Settings.cllogo40));

		pack();
		setSize(350, 445);
		setLocation(300,200);
	}	
	
	private Panel createOkPanel() {
		Panel p = new Panel();
		p.setSize(350,40);
		p.setLayout(new FlowLayout());
		p.setBackground(bgColor);
		
		showDetailsCB = new Checkbox("Show Details",false);
		showDetailsCB.setFont(buttonFont);
		showDetailsCB.addItemListener (new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() != ItemEvent.SELECTED) 
					setShowDetails(true);
				else 
					setShowDetails(false);
				repaint();
			}
		});
		p.add(showDetailsCB);
		
		p.add(new Label("         "));	 	 
		Button okButton = createButton("     Ok     ");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setVisible(false);
				showDetailsCB.setState(false);
			}
		});
		p.add(okButton);
		return p;
	}
	
	public void setShowDetails(boolean val) {
		if(val) {
			showDetailsCB.setLabel("Show Details");
			remove(htmlPanel_other);
			setSize(350,170);	
		}
		else {
			showDetailsCB.setLabel("Hide Details");
			add("South",htmlPanel_other);
			setSize(350, 445);
		}
	}
	
	private Button createButton(String label){
		Button b = new Button(label);
		b.setFont(buttonFont);
		b.setSize(60,20);
		return b;
	}
	
    private String createClText() {
    	
    	String tutorName = "Cognitive Tutor";
        String logoImage = "<IMG SRC=\"Images/cllogo.jpg\" align=middle>"; 
        String copyrightNotice = "<FONT SIZE=1> Copyright 2001, Carnegie Learning, Inc.</font>";
 //"<IMG SRC=\"Images/Done.gif\" WIDTH=\"30\" HEIGHT=\"30\">" 
        
		String html = logoImage+"<B>&nbsp;<FONT SIZE=5> "+tutorName+
		  "</FONT></B> <center><FONT SIZE=4> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Version "+
					tutorVersion+"</FONT></center>"+copyrightNotice;
		return html;
    }


	private String createOtherText() {    	
        String webEQLogo = "<IMG SRC=\"Images/poweredWebEq.gif\" aligh=top>";
        String webEQNotice = 
        	"WebEQ is a proprietary product of Design Science, Inc. with all rights reserved.";
		String scrollNotice = "BDDScrollbar copyright 1996 by Timothy W Macinta.";
		String apacheLogo = "<IMG SRC=\"Images/apache_pb.gif\" aligh=top>";
		String apacheNotice = "This product includes software developed by the Apache Software Foundation (http://www.apache.org)";
//http://www.mathtype.com/company/licensing/webeqlogos.stm
        String htmlNotice = "HTML Viewer by Frans van Gool <br>Magnoliastraat 7"+
							"2651 TD Berkel en Rodenrijs<br>The Netherlands<br>"+
							"http://www.xs4all.nl/~griffel/java";
		String html = "<FONT SIZE=1><HR>"+webEQLogo+" &nbsp;"+webEQNotice+"<HR>"+scrollNotice+
					  "<HR>"+apacheLogo+apacheNotice+"<HR>"+htmlNotice+"<HR> <br> </font>";
		return html;
    }
    

	public void windowOpened(WindowEvent e) { }

    public void windowClosing(WindowEvent e) { 
    	setVisible(false);
    }

    public void windowClosed(WindowEvent e) { 
    	setVisible(false);
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e){
    }

    public void windowActivated(WindowEvent e){ }

    public void windowDeactivated(WindowEvent e){ }		
}
