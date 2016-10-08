package edu.cmu.old_pact.htmlPanel;


import java.util.Vector;

import edu.cmu.old_pact.html.library.HTMLElement;


public class StatementObject extends Object{
	private String text, htmlText = "";
	private String name, numStr = null;
	// tags format String "10 123" where numbers represent 
	// running numbers of ASCII characters in the text
	private Vector tagsV;
	
	private Vector sortedTags; // containg sorted tags, one array of 2 integers per region
	private int ins = 0;
	private String boldFontSize = "4";
	private HiddenHTMLString hidden;
	
	public StatementObject(String text) {
		hidden = new HiddenHTMLString(text);
		this.text = text;
		tagsV = new Vector();
		sortedTags = new Vector();
	}
	
	void clearStObj(){
		hidden = null;
		tagsV.removeAllElements();
		sortedTags.removeAllElements();
	}
	
	/* this methid is not used
	protected String insertNumber(String str){
		int ins = hidden.insertSet(str);
		str = str.substring(0, ins)+numStr+str.substring(ins);
		return str;
	}
	*/
	
	protected void setBoldFontSize(String s){
		boldFontSize = s;
//System.out.println("** SO boldFontSize"+boldFontSize);
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void addTag(String tag) {
		if(!tagsV.contains(tag))
			tagsV.addElement(tag);
	}
	
	public void removeTags() {
		tagsV.removeAllElements();
		sortedTags.removeAllElements();
	}
	
	protected int getFirstInt(String t) {
		int ind = t.indexOf(" ");
		int toret = 0;
		String toInt = t.substring(0, ind);
		try{
			toret = Integer.parseInt(toInt);
		} catch (NumberFormatException e){
			System.out.println("StatementObject getFirstInt "+toInt+
								" is not a number");
		} 
		return toret;
	}
	
	
	private void insertTag(int[] newTag){
		int start = newTag[0];
		int end = newTag[1];
		int after = -1;
		
		// ignore malformed tags
		if(start >= end)
			return;

		int s = sortedTags.size();
		int[] oneTag;

		for(int i=0; i<s; i++) {
			oneTag = (int[])sortedTags.elementAt(i);
				// combine overlapping regions
			if((Math.abs(start - oneTag[1]) < 2) ||  // adjacent
			   (Math.abs(end - oneTag[0]) < 2)   ||
			   ((start >= oneTag[0]) && (start <= oneTag[1])) ||
			   ((end >= oneTag[0]) && (end <= oneTag[1])) ||
			   ((oneTag[0] >= start) && (oneTag[0] <= end)) ||
			   ((oneTag[1] >= start) && (oneTag[1] <= end)) ) {
			   
			  oneTag[0] = Math.min(oneTag[0], start);
			  oneTag[1] = Math.max(oneTag[1], end);
			  	// replace current region with a combined one
			  sortedTags.setElementAt(oneTag, i);
			  return;
			}				
			if(start > oneTag[1]) 
			  after = i;
		}		
		sortedTags.insertElementAt(newTag, after+1);	
	}

	
	
	public void sortTags() {
		int s = tagsV.size();
		if(s == 0) return;
		
		sortedTags.removeAllElements();
		sortedTags = new Vector();
		int[] tagsPos;
			// add first element
		sortedTags.addElement(parseTag((String)tagsV.elementAt(0)));
		
		if(s > 1) {
		  for(int i=1; i<s; i++) {
		     tagsPos = parseTag((String)tagsV.elementAt(i));
		     insertTag(tagsPos);
		  }
		}
	}
	

	public void makeElements() {
 	  htmlText = "";
 	  StringBuffer sb = new StringBuffer("");
 	  
	  //int s = tagsV.size();
	  int s = sortedTags.size();
	  RegionString region;
	  if(s > 0) {	  
		int Pos = 0, i = 0;
		int[] tagsPos, nextTagsPos;  //array of 2 integers (start and end pos of the
									 // text region to highlight -- inclusive!!)
		while (i < s) {
		  //tagsPos = parseTag((String)tagsV.elementAt(i));
		  tagsPos = (int[])sortedTags.elementAt(i);
		  
		  	// substring before a highlighted region
		  region = hidden.getSubstring(Pos, tagsPos[0], false);
		  if (region.getIsAdjusted()) 
		  	tagsPos[0] = region.getNewPos();
		
		 // htmlText = htmlText+(new HTMLElement("TEXT", region.getRegionStr())).getHTMLText();
		 sb.append((new HTMLElement("TEXT", region.getRegionStr())).getHTMLText());
		  	// highlighted region
		  region = hidden.getSubstring(tagsPos[0],tagsPos[1]+1,true);
		  if (region.getIsAdjusted()) {
		  	tagsPos[1] = region.getNewPos();  
		  		// if overlaps with the next region combine two regions
		  	if (i < (s-1) ) {
		  	  //nextTagsPos = parseTag((String)tagsV.elementAt(i+1));
		  	  nextTagsPos = (int[])sortedTags.elementAt(i+1);
		  	  if (tagsPos[1] >= nextTagsPos[0]) {
		  	    tagsPos[1] = nextTagsPos[1];
		  	    i++;
		  	    region = hidden.getSubstring(tagsPos[0],tagsPos[1]+1,true);
		  		if (region.getIsAdjusted()) {
		  		  tagsPos[1] = region.getNewPos(); 
		  		}
		  	  }
		  	}
		  }

		  HTMLElement el = new HTMLElement("TEXT",region.getRegionStr());	
						
		 // el.setAttribute("FONT", "SIZE", boldFontSize);
		  el.setAttribute("FONT", "COLOR", "#FF0000"); // red
		  el.setAttribute("STRONG");
		
		  //htmlText = htmlText + el.getHTMLText();
		  sb.append(el.getHTMLText());
		  Pos = tagsPos[1]+1;
		  
		  	//if the last region
		  if(i == (s-1)) {
		  	region = hidden.getSubstring(Pos, -1,false);
		  	el = new HTMLElement("TEXT",region.getRegionStr());
		  	//htmlText = htmlText +"<LASTTAG>"+el.getHTMLText();
			sb.append("<LASTTAG>");
			sb.append(el.getHTMLText());	
		  }
		  
		  i++;
		}
		htmlText = sb.toString();
	  }
	  else  //empty tag vector
		htmlText = (new HTMLElement("TEXT",text)).getHTMLText();
	}
	
//------------------------------------------------------------------	
	public String getHTMLText() {
		if(htmlText.equals(""))
			makeElements();
		return htmlText;
	}

	private int[] parseTag(String toparse) {
		int[] tagsPos = new int[2];
		toparse = toparse.trim();
		int div = toparse.indexOf(" ");
		String stS = toparse.substring(0, div);
		String endS = toparse.substring(div+1);
		tagsPos[0] = Integer.parseInt(stS);
		tagsPos[1] = Integer.parseInt(endS);
		return tagsPos;
	}
	
}
