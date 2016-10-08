package edu.cmu.old_pact.html.library;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import webeq3.fonts.FontBroker;
import webeq3.parser.Parser;
import webeq3.util.ErrorHandler;
import edu.cmu.old_pact.settings.ParameterSettings;

public class WebEqImage {
	
	protected AbstrPEquation theEquation = null;
	protected webeq3.app.Handler myHandler;
		
    private static Parser theParser = new webeq3.parser.mathml.mathml();
    private Font myFont = new Font("Arial",Font.PLAIN,18);
    private int pointsize = 14; 

    String phantom = //"<msubsup><mo>&InvisibleTimes;</mo>"+
    				  //"<mphantom><mi>2</mi></mphantom>"+
    				 //"<mo>&InvisibleTimes;</mo></msubsup>"+
    "<mphantom><mo>&InvisibleTimes;</mo><mfrac><mn>1</mn><mn>2</mn></mfrac></mphantom>";
	Color myForeColor = Color.black;    
    
	static {
	    FontBroker.initialize(true);
		theParser.init(new webeq3.app.Handler()); 
	}
	
	public WebEqImage() { }	
		
	public void setPointsize(int size) {
		pointsize = size;
	}
		
	public void setForeColor(Color myColor) {
		myForeColor = myColor;
	}
		
	public void createEquation(String exprMathML, Component c) {
	   ErrorHandler err = new ErrorHandler();
	
		if("".equals(exprMathML))
			return;
    	  //create the PEquation
		myHandler = new webeq3.app.Handler();
    	theEquation = new AbstrPEquation(myHandler);  
    	myHandler.setParameters(c,(String [])null);    	
    	String equationML;
    	
    		// if style is not specified add a default one
    	if(exprMathML.indexOf("<mstyle") == -1)
			equationML = getEquationML(exprMathML);
		else
			equationML = exprMathML + phantom;
		
		try{
			theParser.parse(equationML, "", theEquation.root, err);
		}
		catch(Exception e) {
			System.out.println("Error parsing: "+e);
			e.printStackTrace();
   		} 
     		
			// initialize 
		theEquation.initBG();   // sets background of the component c
   		theEquation.registerControls();	
   		refreshEquation();
	}

	public void refreshEquation() {
		if(theEquation != null){					
    		theEquation.setPointSize(pointsize);
    		theEquation.redraw(); 
    	}
	}
	
	public Dimension getEqSize() {
		return theEquation.getPreferredSize();
	}				
		
	public Image getEqImage() {
		return theEquation.getImage();
	}


	public HtmlImage getHtmlImage() {
		Dimension d = getEqSize();
		HtmlImage hi = new HtmlImage(0,0, d.width, d.height,
									 theEquation.getImage());
		return hi;
	}

	public String getEquationML(String equationML) {
		return "<mstyle fontfamily='"+myFont.getFamily()+
				"' fontcolor='"+ParameterSettings.getWebColor(myForeColor)+"'>"+
					equationML+"</mstyle>"+phantom;
		
	}
	
	public void delete(){
		if(theEquation != null)
			theEquation.destroy();
		theEquation = null;
	}	
}


