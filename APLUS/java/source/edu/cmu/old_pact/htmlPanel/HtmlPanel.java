package edu.cmu.old_pact.htmlPanel;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Panel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import edu.cmu.old_pact.cl.utilities.Startable.Viewable;
import edu.cmu.old_pact.cmu.sm.BadExpressionError;
import edu.cmu.old_pact.cmu.sm.SymbolManipulator;
import edu.cmu.old_pact.html.library.AutoWrapper;
import edu.cmu.old_pact.html.library.DraggSourceViewer;
import edu.cmu.old_pact.html.library.HtmlDocument;
import edu.cmu.old_pact.settings.ParameterSettings;
import edu.cmu.pact.Utilities.trace;


public class HtmlPanel extends Panel implements PropertyChangeListener, Viewable {
	private DraggSourceViewer htmlViewer;	//HtmlViewer htmlViewer;
	private AutoWrapper autoWrap;
	private URL base = null;
	private String currentDir = "";
	private StatementObjectManager sObjMan;
	private static SymbolManipulator sm;
	private int width = 200;
	private int height = 180;
	//private int widthOff = 20;
	//private int heightOff = 15;
	private int fontSize = 12;
	private Color bgColor = Color.white;
	private Color fgColor = Color.black;
	private boolean justStarted = true;
	private boolean delegatePreferredSize = false;
	// to prevent the big images crash on mac for Glossary
	private boolean canDeleteDoc = true; 
	
	public HtmlPanel()
	{
	   this(200, 180, Color.white, true);
	}
	
	public HtmlPanel(int w, int h, Color bg, boolean showScrollBar){
		super();
		bgColor = bg;
		htmlViewer = new DraggSourceViewer();  
		htmlViewer.setShowScrollbar(showScrollBar);
		htmlViewer.setBgColor(bgColor);
		width = w; height = h;
		initializeHtmlPanel(); 
	}
	
	
	public HtmlPanel(int w, int h, boolean showScrollBar){
		this(w,h, Color.white, showScrollBar);
	}
	
	public HtmlPanel(int w, int h, Color bg){
		this(w,h,bg, false);
	}
	
	public HtmlPanel(int w, int h){
		this(w,h,Color.white, false);
	}
			
	public HtmlPanel(Container par, int w, int h){
		super();
		htmlViewer = new DraggSourceViewer(par);
		width = w; height = h;
		initializeHtmlPanel();
	} 

	/**
	   * Get the value of delegatePreferredSize.
	   * @return Value of delegatePreferredSize.
	   */
	public boolean getDelegatePreferredSize() {return delegatePreferredSize;}
	
	/**
	   * Set the value of delegatePreferredSize.
	   * @param v  Value to assign to delegatePreferredSize.
	   */
	public void setDelegatePreferredSize(boolean  v) {this.delegatePreferredSize = v;}
	
	private void initializeHtmlPanel() {
		setLayout(new BorderLayout());
		add("Center", htmlViewer);
		setTopMargin(8);
		autoWrap = new AutoWrapper("imageDisplay", "");
		autoWrap.setBGColor(ParameterSettings.getWebColor(bgColor));	
		autoWrap.setFGColor(ParameterSettings.getWebColor(fgColor));
		sObjMan = new StatementObjectManager(" ",ParameterSettings.getWebColor(bgColor));
		
			// initialize symbol manipulator
		sm = new SymbolManipulator();
		sm.autoStandardize = false;
		sm.autoCombineLikeTerms = false;
		sm.setOutputType(SymbolManipulator.mathMLOutput);
	}

	public void setBorder(boolean b){
		htmlViewer.setBorder(b);
	}
	
	public void scrollToBottom(){
		htmlViewer.scrollToBottom();
	}

	public void scrollToTop(){
		htmlViewer.scrollToTop();
	}

	public boolean getBorder(){
		return htmlViewer.getBorder();
	}
	
	public void setHtmlViewerParent(Container c){
		htmlViewer.setParent(c);
	}
	
	public void layoutHtmlViewer(){
		htmlViewer.layout();
	}
	
	public void setCanDeleteDoc(boolean b ){
		canDeleteDoc = b;
	}
	
	public void setURLBase(String b){
		if(base == null){
			try{
				base = new URL(b);
				
			}
			catch (MalformedURLException e) {
			}
		}
	}
		
	public void removeAll(){
		htmlViewer.removeAll();
		htmlViewer = null;
		base = null;
		autoWrap = null;
		sObjMan.delete();
		sObjMan = null;
		super.removeAll();
	}	

	public void setFgColor(Color c){
		fgColor = c;
		autoWrap.setFGColor(ParameterSettings.getWebColor(fgColor));
		sObjMan.setTextColor(ParameterSettings.getWebColor(fgColor));
	}
	
	public void setBgColor(Color c){
		bgColor = c;
		autoWrap.setBGColor(ParameterSettings.getWebColor(bgColor));
		htmlViewer.setBgColor(bgColor);
		sObjMan.setBgColor(ParameterSettings.getWebColor(bgColor));
	}
	
	public void setFgColor(String c){
		autoWrap.setFGColor(c);
		sObjMan.setTextColor(c);
	}
	/*
	public void setBgColor(String c){
		autoWrap.setBGColor(c);
		htmlViewer.setBgColor(ParameterSettings.getColor(c));
		sObjMan.setBgColor(c);
	}
*/	
	public void setSize(Dimension d){
		super.setSize(d);
		htmlViewer.setSize(width,height);
	}

	public void setHeight(int h){
		height = h;
		htmlViewer.setSize(width, height);
		setSize(preferredSize()); 
	}
	
	public void setWidth(int w){
		width = w;
		htmlViewer.setSize(width, height);
		setSize(preferredSize());
	}
	
	public void setTopMargin(int margin){
		htmlViewer.getHtmlCanvas().setYMargin(margin);
	}
	
	public void setLeftMargin(int margin){
		htmlViewer.getHtmlCanvas().setXMargin(margin);
	}
		
	public void setNewTitle(String title){
		autoWrap.newTitle(title);
	}
	
	public int getNeededWidth(){
		return htmlViewer.getNeededWidth();
	}
	
	public Dimension preferredSize(){
		if(delegatePreferredSize){
			if(htmlViewer != null){
				return htmlViewer.preferredSize();
			}
			else if(width != 0)
				return new Dimension(width, height);
			else
				return super.preferredSize();
		}
		else{ 
			if(width != 0)
				return new Dimension(width, height);
			else if(htmlViewer != null)
				return htmlViewer.preferredSize();
			else
				return super.preferredSize();
		}
	}
	
//	public Dimension superPreferredSize(){
//		return super.preferredSize();
//	}
	
	public void setHtmlSize(Dimension dim){
		setWidth(dim.width);
		setHeight(dim.height);
	}
	
	public int setHtmlWidth(int w){
		return htmlViewer.setWidth(w);
	}
	
	public void propertyChange(PropertyChangeEvent evt){
		String eventName = evt.getPropertyName();
		if(eventName.equalsIgnoreCase("DISPLAYIMAGE")) {
			resetProblem("");
			display((String)evt.getNewValue());
		}
		else if(eventName.equalsIgnoreCase("DIRECTORY"))
			setImageDir((String)evt.getNewValue());
		else if(eventName.equalsIgnoreCase("URLBASE"))
			setImageBase((String)evt.getNewValue());
	}
	
	void display(String fileName){
		if(fileName.equals("")) {
			displayHtml("");
			return;
		}
		try{
			BufferedReader instream = 
					new BufferedReader(new FileReader(new File(currentDir,fileName)));
			String line = null;
			String context = "";
			while((line = instream.readLine()) != null) 
				context = context+line+"\n";
			context = context.substring(0,context.length()-2);
			displayHtml(context);
		}catch (IOException e) { 
			trace.out("HtmlPanel display "+e.toString());
		}
	}
	
	
	public void displayHtml(String context){
		String contextWithMathML = convertExprToMathML(context);
		try{
			autoWrap.newBody(contextWithMathML);
			String htmlText = autoWrap.wrappedText();
			//trace.out (5, this, "in displayHTML: htmlText = " + htmlText );
			htmlViewer.changeDocument(new HtmlDocument(htmlText, base,fgColor),canDeleteDoc);	
			//trace.out (5, this, "repaint");
			//repaint();
			//trace.out (5, this, "width = " + this.getSize().width);
			sObjMan.treatMessage(contextWithMathML);
		}catch (IOException e) { 
			trace.out("HtmlPanel displayHtml "+e.toString());
		}
	}
	
	public void displayHtmlIfExists(){
	  	if( !sObjMan.isEmpty())
	  	  displayHtml(sObjMan.getHTMLText());
	}
			
	public void resetProblem(String problemName) {
		sObjMan.resetProblem(problemName);
		scrollToTop();
	}
				
	public int getHtmlHeight(){
		return htmlViewer.preferredHeight();
	}
	
	public void setImageBase(String b){
		try{
			if(!currentDir.equals("") &&
				!(b.toUpperCase()).endsWith(currentDir.toUpperCase()))
				b = b+currentDir;
			if(!b.endsWith(File.separator))
				b = b+File.separator;
			base = new URL(b);
			if(justStarted){
				justStarted = false;
				displayHtmlIfExists();
			}
		}catch(MalformedURLException e) {
			trace.out("HtmlPanel : Can't create URL: "+b);
		}
	}
	
	public void setFontSize(int s){
		fontSize = s;
		autoWrap.setFontSize(s);
		sObjMan.setFontSize(s);
		sObjMan.plainHTMLText = null;
		displayHtmlIfExists();
	}
	
	public void showtxt(Vector tag) { 
  		if(!sObjMan.sameTag(tag)) {   
  			sObjMan.setTags(tag);
  			displayHtml(sObjMan.getHTMLText());
  		}
  		//scrollToBottom();
  		scrollToLastTag();
  	}
  	
	private void setImageDir(String relPath){
		currentDir = relPath;
		if(base != null) {
			relPath = base.toString()+relPath;
			setImageBase(relPath);
		}	
	}
	
	public void scrollToLastTag(){
		htmlViewer.scrollToLastTag();
	}

	public int preferredHeight(){
		return htmlViewer.preferredHeight();
	}
	
	
	/*-----------------------------------------------------*/
	// convert contents of all <EXPRESSION> tags in the argument
	// string into MathML (using symbol manipulator)
	// <expression> tag can have a COLOR attribute with html
	// format color value (e.g. "<expression color=#FF0000>")
	/*-----------------------------------------------------*/
	private String convertExprToMathML(String str) {
		int startExpInd, endExpInd, endTagInd;
		String res = "";
		String expr, colorName;
		   
		startExpInd = getExprStart(str);
		if (startExpInd == -1)
		  return str;
		
		while (startExpInd != -1) {
		  res = res + str.substring(0, startExpInd);
  		  
  		  str = str.substring(startExpInd);
		  endTagInd = str.indexOf(">");
		  colorName = getColorName(str.substring(0,endTagInd));
		  
		  str = str.substring(endTagInd+1);
		  endExpInd = getExprEnd(str);	
	
		  if (endExpInd == -1) {
		    trace.out("----!!! malformed Html: '</EXPRESSION>' not found !!! EXPR='"+res+str+"'");
		    return res + str;
		  }	
		  expr = str.substring(0, endExpInd);
		  //res = res + processExpression(expr, colorName); 
		  res = res + makeMathMLFormat(expr, colorName);		  
		  		  
		  str = str.substring(endExpInd+13);
		  startExpInd = getExprStart(str);		
		 
		} // end while  
		
		res = res + str;
		return res;
	}

	
	private String getColorName(String str){
		String colorName = null;
		int ind = str.indexOf("#");
		
		if((ind != -1) & (str.length() >6))
		  colorName = str.substring(ind,ind+7);
		  	
		return colorName;
	}
	
/*	
// moved handling inequalities to noOpExprOrEquation in SymbolManipulator
//	
	private String processExpression(String str, String colorName){
	    String res, str1="", str2="", oper="", s="";
	    int ind = str.indexOf("=");
		
		if(ind > 0) {   // "=" found
		  s = str.substring(ind-1,ind);			 
		  if(s.equals(">")) { // ">=" found
		    oper = "&gt;=";
		    str1 = str.substring(0,ind-1);
		    str2 = str.substring(ind+1);
		  }		   
		  else if(s.equals("<")) { // "<=" found
		         oper = "&lt;=";
		         str1 = str.substring(0,ind-1);
		         str2 = str.substring(ind+1);		         	 
		       }
		       else {             // "="
		           oper = "=";
		           str1 = str.substring(0,ind);
		           str2 = str.substring(ind+1);
		       }
		 }
		 else {    // "=" NOT found
		    ind = str.indexOf("<");
		    if(ind > 0) {  // "<" found
		      oper = "&lt;";
		      str1 = str.substring(0,ind);
		      str2 = str.substring(ind+1);
		    } else {
		        ind = str.indexOf(">");
		        if(ind > 0) {  // ">" found
		          oper = "&gt;";
		          str1 = str.substring(0,ind);
		          str2 = str.substring(ind+1);
		        } else   // one expression
		            str1 = str;
		     }
		 }
		 
		 res = makeMathMLFormat(str1, colorName);
		 if(str2.equals("")) {			  
			  return res; 
		 }		
		 res = res + oper + makeMathMLFormat(str2, colorName);
	     return res;
   }
*/	
	
	private String makeMathMLFormat(String str, String colorName){
		String resultMathML ="";
		Font myFont = new Font("Arial",Font.PLAIN,14); 
		String res = "", MATHMLstart = "<MATHML>";
		int delta = 0, count2=0;
		int count1 = countExprLength(str);
		
		if((str.trim()).equals("")) return "";
		
		 
		if (colorName == null)
		  colorName = ParameterSettings.getWebColor(fgColor);
		
		try {
		  	resultMathML = sm.noOpExprOrEquation(str);  //sm.noOp(str);
		  	count2 = countExprLength(resultMathML);
		  }
			catch (BadExpressionError err) {
				trace.out("---- !!! SM error: can't convert to MathML... EXPR='"+str+"'");
		  }	  
		delta = count2-count1;	
		
		// if there is a difference in the length of expr and its MATHML form then
		// precede <MATHML> with a delta tag: <D #>, e.g. <D 3>, <D -5>
		if(delta != 0)
		  	MATHMLstart= "<D "+delta+">" + MATHMLstart;
		  	 
		res = MATHMLstart+"<mstyle fontfamily='"+myFont.getFamily() + "'"+
		  		" fontstyle='normal'"+ " fontweight='normal'"+
		  		" fontcolor='"+ colorName+"'>"+ 
		  		resultMathML + "</mstyle></MATHML>";

		return res;
	}				    

	
	private int getExprStart(String str) {
	   int ind = -1;
	   ind = str.indexOf("<EXPRESSION");
	   if(ind == -1)
	     ind = str.indexOf("<expression");
	   return ind;
	} 
	
	private int getExprEnd(String str) {
	   int ind = -1;
	   ind = str.indexOf("</EXPRESSION>");
	   if(ind == -1)
	     ind = str.indexOf("</expression>");
	   return ind;
	} 
	
	// replace all occurences of oldStr with newStr in str
	private String replaceSubstrings(String str, String oldStr, String newStr){
		int ind = -1;
		String res = "";
	

		ind = str.indexOf(oldStr);
		if (ind == -1)
		  return str;
		
		while (ind != -1) {
		  	res = res + str.substring(0, ind) + newStr;
		  	str = str.substring(ind + oldStr.length());
		  	// find next oldStr
		  	ind = str.indexOf(oldStr);				 
		} // end while  
		
		res = res + str;
		return res;
	}
	
	
	// counts ascii characters (skipping tags)
	private int countExprLength(String str){
		int len = str.length(),  count=0, i = 0;
		char[] chArr = str.toCharArray();
	 	boolean skip = false;
	 	
	 	while(i<len) {	
			if(chArr[i] == '<') skip = true;
			if(!skip) count++;	
			if(chArr[i] == '>') skip = false;
			
			i++;
		}
		
		chArr = null;				
		return count;		
	}
		  
}		 
	
