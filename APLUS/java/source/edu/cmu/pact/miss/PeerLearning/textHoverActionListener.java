package edu.cmu.pact.miss.PeerLearning;

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

public class textHoverActionListener extends MouseAdapter{
	private HashMap <String, String> hovertext;
	private HashMap <Integer, String> hovertext_pos;
	private HashMap <Integer, String> hovertext_pos_end;
	private List<Integer> start_positions;
	private List<Integer> end_positions;
	
	textHoverActionListener(HashMap <String, String> hovertext, HashMap <Integer, String> hovertext_pos, HashMap <Integer, String> hovertext_pos_end){
		this.hovertext = hovertext;
		this.hovertext_pos = hovertext_pos;	
		Set<Integer> start_keys = hovertext_pos.keySet();
		this.start_positions = new ArrayList<Integer>(start_keys);
		this.hovertext_pos_end = hovertext_pos_end;
		Set<Integer> end_keys = hovertext_pos_end.keySet();
		this.end_positions = new ArrayList<Integer>(end_keys);
	}
	
	public void mouseMoved(MouseEvent me)
    {
        int x = me.getX();
        int y = me.getY();
        int startOffset = ((JTextComponent) me.getSource()).viewToModel(new Point(x, y));
        //System.out.println("x: "+x+" y: "+y+" string_pos: "+startOffset);
        String to_be_highlighted =  getTitleText(startOffset);
        JTextPane tt = (JTextPane) ((JTextComponent)me.getSource());
    	tt.setToolTipText(hovertext.get(to_be_highlighted));
       
    }
	
	// If current cursor is placed on a string that has title text
	public String getTitleText(int startOffset) {
		String title_text = "";
		int index = Collections.binarySearch(start_positions, startOffset);
		index = (index+2)*-1;
		//System.out.println(index);
	    if(hovertext_pos.containsKey(startOffset)){
	    	//System.out.println("11111");
	    	title_text = hovertext_pos.get(startOffset);
	        //System.out.println(title_text);
	        return title_text;
	        	
	    }
	    else if(hovertext_pos_end.containsKey(startOffset)){
	    	//System.out.println("22222");
	        title_text = hovertext_pos_end.get(startOffset);
	        //System.out.println(title_text);
	        return title_text;
	    }
	    else if(index >=0 && startOffset <= end_positions.get(index) && startOffset>= start_positions.get(index))
	    {
	    	//System.out.println("3333 "+index);
	    	title_text = hovertext_pos.get(start_positions.get(index));
	        //System.out.println(title_text);
	        return title_text;
	    }
		return title_text;
	}
}
