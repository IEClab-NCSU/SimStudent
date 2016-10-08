package edu.cmu.old_pact.htmlPanel;


public class HiddenHTMLString {
	private String text;
	
	
	public HiddenHTMLString(String str) {
		text = str;
	}
	
	// inside = true when building a substring inside a region to be highlighted.
	// Contents of <mathml> tags can NOT be partially highlighted.
	// if start or end region positions fall inside tag contents they are moved
	// to the beginning or end of the tag depending on the value of the
	// INSIDE variable. if any part of a tag contents overlap with a highlighted
	// region boundaries - the entire contents of that tag will be highlighted
	// 
	public RegionString getSubstring(int start, int end, boolean inside) {
		RegionString region = new RegionString(); 
			
		if(text.indexOf("<") != -1) {
			int len = text.length(), Pos = -1, //increased only when passing ascii chars 
				PosStartMathml = -1, PosEndMathml = -1, 
				i = 0, startMathmlInd = -1, endMathmlInd = -1; 				
			char[] chArr = text.toCharArray();
	 		boolean skip = false, startset = false, endset = false;	
	 		int delta = 0, deltaTagInd = -1, goBackInd=7;							
			
				//get positions of the first MATHML tag			
			startMathmlInd = getTagStartInd("<MATHML>",i);
			endMathmlInd = getTagStartInd("</MATHML>",i);
			
				// find DELTA tag if exists (preceeding <MATHML>)
				// extract delta - difference in character counts between <expressioin>
				// <expressioin> and its mathml form
			if(startMathmlInd != -1){
				goBackInd = Math.min(7,startMathmlInd);
				deltaTagInd = getTagStartInd("<D ", startMathmlInd-goBackInd);
				if(deltaTagInd != -1 && deltaTagInd < startMathmlInd)
					delta = Integer.parseInt(text.substring(deltaTagInd+3, startMathmlInd-1)); 			
			}
			
			while(i<len) {	
				if(chArr[i] == '<') skip = true;
				if(!skip) Pos++;	
				if(chArr[i] == '>') skip = false;
				
				if(Pos == start && !startset && start != 0) {
					start = i;
					startset = true;
					
					if(((i > startMathmlInd) && (i <= endMathmlInd)) &&
				   	   (start > startMathmlInd))		         
				     start = startMathmlInd;
				      
					if(end == -1) {
					  region.setRegionStr(treatMathML(text.substring(start),inside));			  								  					 
					  return region;
					}	 					
				} // end if (Pos == start && !startset)
				
				if (i==startMathmlInd) {
					PosStartMathml = Pos;
					Pos = Pos - delta;   // adjust for char count difference in expr
				}
				if (i==endMathmlInd) {
					PosEndMathml = Pos;
				}
				
				if((i == endMathmlInd+9) && (startMathmlInd != -1)) { 
				  	startMathmlInd = getTagStartInd("<MATHML>",i);
					endMathmlInd = getTagStartInd("</MATHML>",i+1);
					delta = 0;
					
					if(startMathmlInd != -1){
						deltaTagInd = getTagStartInd("<D ", startMathmlInd-7);	
						if(deltaTagInd != -1 && deltaTagInd < startMathmlInd)
							delta = Integer.parseInt(text.substring(deltaTagInd+3, startMathmlInd-1)); 
					}
				}	
								
				if((i == endMathmlInd) && (startMathmlInd ==-1)) {
				   		// malformed Html -- WHAT TO DO??
				   	System.out.println("Malformed Html text!(end tag comes before start tag)"+
				   		text.substring(start) );
				 }					   				
				if(Pos == end && end != -1) {
				  endset = true;
				     // if region ends inside MathML tag, move region end to 
					 // exclude the entire tag (outside highlighted region)
				  if(!inside) {
					if((i > startMathmlInd) && (i < endMathmlInd)) {
					  end = startMathmlInd;
					  region.setIsAdjusted(true);
					  region.setNewPos(PosStartMathml+1);
					  break;
					}
					end = i;

					break;
				  } else {  	//inside hightlighted region 
				  				// but not in the middle of a mathml tag contents
				     if (!((i > startMathmlInd) && (i <= endMathmlInd))) {           
				        end = i;
				        break;
				     }				     
				   }
				} //end if Pos==end
				
				if(endset && (i == endMathmlInd)) {  
				  //this can be only when (inside == true). continue to count chars 
				  //until EOTag; include MathML tag with its contents into the region
				  end = endMathmlInd+9;
				  region.setIsAdjusted(true);
				  region.setNewPos(Pos+1);	
				  break;
				 }								  
				i++;
			} // while i<text.length			  	
			 	
			if(Pos == (end-1)) {
			  region.setRegionStr(treatMathML(text.substring(start,end), inside));			  
			  return region;
			}

			if(Pos == (start-1) && end == -1) {
				region.setRegionStr("");
				return region;	
			}			
		} // if "<" found in text

		if(end == -1) {
		  region.setRegionStr(text.substring(start));
		  return region;
		}
		
		String toret = "";
		try{	
			toret = text.substring(start, end);
		} catch (StringIndexOutOfBoundsException e){
			toret = text.substring(start);
		}
		region.setRegionStr(treatMathML(toret, inside));
		return region;	
	}
	
	/*----------------------------------------------------------------*/
	// when inside a hightlighted region change font color in the 
	// contents of MathML tags to red 
	/*----------------------------------------------------------------*/
	private String treatMathML(String str, boolean inside){
		String res = str;
		
		if (inside)
		  res = changeMathMLcolor(str);
		
		return res;  
	}
	
		
	private String changeMathMLcolor(String str) {	  
		
		int startMathmlInd;
		int endMathmlInd, startMstyleInd, startColorInd;
		String toret ="";
		
		startMathmlInd = str.indexOf("<MATHML>");
		if (startMathmlInd == -1) 
		  return str;
		  
		while (startMathmlInd != -1) {
		
		  toret = toret + str.substring(0, startMathmlInd);
		  str = str.substring(startMathmlInd);      // str starts now from <MATHML>
		  		 
		  endMathmlInd = str.indexOf("</MATHML>");
		
		  if (endMathmlInd == -1) {
		    System.out.println("!!! malformed Html: '</MATHML>' not found !!!");
		    return toret + str;
		  }	
		  startMstyleInd = str.indexOf("<mstyle"); 
		  
		  if ((startMstyleInd == -1) ||
		  	  (startMstyleInd > endMathmlInd))	{
			  // add <mstyle> tag with fontcolor 
		    toret= toret + "<MATHML><mstyle fontcolor='#FF0000'>" +
		  		   str.substring(8, endMathmlInd) + "</mstyle></MATHML>";
		  }
		  else { // modify mstyle tag
		    if (str.indexOf("fontcolor") == -1) {
		    	// add fontcolor
		      toret = toret + str.substring(0,startMstyleInd+7) + 
		      		  " fontcolor='#FF0000' " + 
		      		  str.substring(startMstyleInd+7,endMathmlInd+9);
		    } else {  // modify fontcolor
		    	startColorInd = str.indexOf("#");
		    	toret = toret + str.substring(0,startColorInd+1) + 
		    			"FF0000" + str.substring(startColorInd+7,endMathmlInd+9);
		     } 		
		  }
		  str = str.substring(endMathmlInd+9);
		  startMathmlInd = str.indexOf("<MATHML>");
		}		
		toret = toret + str;	
		return toret;
	}
		
	private int getTagStartInd(String tagId, int startInd){
		String str = text.substring(startInd);
		int ind = str.indexOf(tagId);
		if (ind == -1)
		  return -1;
		return (ind + startInd);
	}
	
	
	private int offSet(String tex) {
		int j = 0;
		for( int i=0; i<4; i++)
			if(tex.charAt(i) == ' ')
				j++;
		return j;
	}
	
	public void setText(String t) {
		text = t;
	}
	
	public String getFirstChar(String tex, int t) {
		int Pos = 0, i = 0, len = tex.length();
		String toret = "";
		sub:while(i<len) {
			if(tex.charAt(Pos) == ' '){
				Pos++;
				i++;
				continue sub;
			}
			else if(tex.charAt(Pos) == '<') {
				Pos = tex.indexOf(">", Pos);
				Pos++;
				i = Pos;
				continue sub;
			}
			else {
				toret = tex.substring(Pos, Pos+1);
				break;
			}
		}
		return toret;
	}
	
	public int insertSet(String tex) {
		int t = offSet(tex);
		if(tex.charAt(t) == '<') {
			t = tex.indexOf(">");
			return t+1;
		}
		return 0;
	}
			
}	
	
		
		
		