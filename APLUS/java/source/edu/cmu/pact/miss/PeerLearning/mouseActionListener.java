package edu.cmu.pact.miss.PeerLearning;

import edu.cmu.pact.Utilities.trace;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.JTextPane;
import javax.swing.text.JTextComponent;

public class mouseActionListener extends MouseAdapter{
	// href variables
	private HashMap <String, String> reftext;
	private HashMap <Integer, String> reftext_pos;
	private HashMap <Integer, String> reftext_pos_end;
	private List<Integer> ref_start_positions;
	private List<Integer> ref_end_positions;
	private AplusPlatform simStAplusPlatform;
	
	mouseActionListener(HashMap <String, String> reftext, HashMap <Integer, String> reftext_pos, HashMap <Integer, String> reftext_pos_end, AplusPlatform simStAplusPlatform){
		this.reftext = reftext;
		this.reftext_pos = reftext_pos;	
		Set<Integer> start_keys = reftext_pos.keySet();
		this.ref_start_positions = new ArrayList<Integer>(start_keys);
		this.reftext_pos_end = reftext_pos_end;
		Set<Integer> end_keys = reftext_pos_end.keySet();
		this.ref_end_positions = new ArrayList<Integer>(end_keys);
		this.simStAplusPlatform = simStAplusPlatform;
	}
	
	 public void mouseClicked(MouseEvent me)
     {
         int x = me.getX();
         int y = me.getY();
         trace.out("X : " + x);
         trace.out("Y : " + y);
         int startOffset = ((JTextComponent) me.getSource()).viewToModel(new Point(x, y));
         //trace.out("x: "+x+" y: "+y+" string_pos: "+startOffset);
         String redirect_to =  getHrefLocation(startOffset);
         if(redirect_to!="") {
        	 trace.out(redirect_to);
        	 String id = redirect_to.split("#")[1];
        	 //Element content = doc.getElementById("content");
        	 simStAplusPlatform.populateOverviewFromJsoupDoc(id);
        	 
        	 //setHtmlSourceFromDocument()
         }
         //JTextPane tt = (JTextPane) ((JTextComponent)me.getSource());
         
     }
	
	// If current cursor is placed on a string that has title text
	public String getHrefLocation(int startOffset) {
		String href_location = "";
		String title_text = "";
		int index = Collections.binarySearch(ref_start_positions, startOffset);
		index = (index+2)*-1;
	    if(reftext_pos.containsKey(startOffset)){
	    	title_text = reftext_pos.get(startOffset);
	    	href_location = reftext.get(title_text);
	        return href_location;
	        	
	    }
	    else if(reftext_pos_end.containsKey(startOffset)){
	        title_text = reftext_pos_end.get(startOffset);
	        href_location = reftext.get(title_text);
	        return href_location;
	    }
	    else if(index >=0 && startOffset <= ref_end_positions.get(index) && startOffset>= ref_start_positions.get(index))
	    {
	    	title_text = reftext_pos.get(ref_start_positions.get(index));
	    	href_location = reftext.get(title_text);
	        return href_location;
	    }
		return href_location;
	}
}
