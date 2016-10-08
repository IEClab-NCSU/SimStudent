package edu.cmu.old_pact.htmlPanel;


import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.html.library.AutoWrapper;


public class StatementObjectManager {
	Vector tagsV;
	Hashtable statementH;
	AutoWrapper autoWrap;
	public String plainHTMLText = null, problemName;
	String  htmlHeader;
	private boolean empty;
	private String textColor = "#000000";
	private String bgColor = "#FFFFFF";

	public StatementObjectManager(String problemName) {
		tagsV = new Vector();
		statementH = new Hashtable();
		this.problemName = problemName;
		autoWrap = new AutoWrapper(problemName, "");
		initialize();
	}
	
	//another constructor that allows to set different background color	
	public StatementObjectManager(String problemName, String c) {
		this(problemName);
		bgColor = c;
		initialize();
	}
		
	private void initialize() {
		htmlHeader = "<BR><P>";				
		autoWrap.setBGColor(bgColor);
		autoWrap.setFGColor(textColor);
		empty = true;
	}
	
	public void setPlainHtml(String s){
		plainHTMLText = s;
	}
	
	public void delete(){
		tagsV.removeAllElements();
		tagsV = null;
		autoWrap.delete();
		autoWrap = null;
		clearStObjects();
		statementH.clear();
		statementH = null;
	}
		
	
	public void resetProblem(String problemName){
		this.problemName = problemName;
		plainHTMLText = null;
		clearStObjects();
		tagsV.removeAllElements();
		statementH.clear();
		empty = true;
	}
	
	void clearStObjects() {
		String key;
		StatementObject sobj;
    	for (Enumeration e = statementH.keys() ; e.hasMoreElements() ;) {
			key = (String)e.nextElement();
			sobj = (StatementObject)statementH.get(key);
			sobj.clearStObj();
    		sobj = null;
    	}
    	System.gc();
    }
	
	public void treatMessage(String mes_text) {
		StatementObject toadd = (StatementObject)statementH.get("PROBLEM");
		if(toadd == null)
			toadd = new StatementObject(mes_text); 
		statementH.put("PROBLEM",toadd);
		empty = false;
	}
	
	public boolean isEmpty(){
		return empty;
	}
	
	public boolean sameTag(Vector newTags){
		return (tagsV.toString()).equals(newTags.toString());
	}
	
	public void setTags(Vector lightV) {
		removeTags();  //need to remove old tags
		int s = lightV.size();
		if(s == 0){
			if(plainHTMLText == null) 
        		sortMakeTags();
			return;
		}
		for(int i=0; i<s; i++)
			tagsV.addElement(lightV.elementAt(i));
    	reDirectTags();
    	sortMakeTags();
        
        if(plainHTMLText == null) 
        	sortMakeTags();

    }
    
    public void setFontSize(int s){
    	autoWrap.setFontSize(s);
    	String boldFontSize = String.valueOf(autoWrap.getHtmlFontSize());//+1);
    	String key;
    	for (Enumeration e = statementH.keys() ; e.hasMoreElements() ;) {
			key = (String)e.nextElement();
    		StatementObject st_obj = (StatementObject)statementH.get(key);
    		st_obj.setBoldFontSize(boldFontSize);
    		st_obj.makeElements();
    	}	
    }
    
    public void setTextColor(String c) {
    	textColor = c;
    	autoWrap.setFGColor(textColor);
    }
    
    
    public void setBgColor(String c) {
    	bgColor = c;
    	autoWrap.setBGColor(bgColor);
    }
    
    public String getHTMLText() {
    	if(tagsV.size() == 0 && plainHTMLText != null) {
    		return plainHTMLText;
    	}
    	StringBuffer  buf = new StringBuffer();
    	buf.append(htmlHeader);
    	buf.append(((StatementObject)statementH.get("PROBLEM")).getHTMLText());
    	//buf.append("<BR><P>");
    	autoWrap.newBody(buf.toString());
		String toret = autoWrap.wrappedText();
    	if(tagsV.size() == 0 && plainHTMLText == null)
    		plainHTMLText = toret;
    	return toret;
    }
    

    
    public void removeTags() {
    	tagsV.removeAllElements();
    	String key;
    	for (Enumeration e = statementH.keys() ; e.hasMoreElements() ;) {
			key = (String)e.nextElement();
    		((StatementObject)statementH.get(key)).removeTags();
    	}
    }
    
    private void reDirectTags() {
    	int s = tagsV.size();
    	if( s > 0) {
    		StatementObject stObj = (StatementObject)statementH.get("PROBLEM");
    		String value = "";
    		
    		for(int i=0; i<(s-1); i++) {
    			value = ((Integer)tagsV.elementAt(i)).toString()+" "+
    						((Integer)tagsV.elementAt(i+1)).toString();				
        		stObj.addTag(value);
        		i++;
        	}
        }
    }
      
    private void sortMakeTags() {  
    	String key;
    	for (Enumeration e = statementH.keys() ; e.hasMoreElements() ;) {
			key = (String)e.nextElement();
    		StatementObject st_obj = (StatementObject)statementH.get(key);
    		st_obj.sortTags();
    		st_obj.makeElements();
    	}
	}
	
	 
}  			 
	