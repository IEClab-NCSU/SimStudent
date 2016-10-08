/**
* Recalculates size after adding a new component.
**/
package edu.cmu.old_pact.wizard;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Rectangle;
import java.util.Vector;

public class NullLayoutPanel extends Panel{
	private int width = 300;
	private int height = 10;
	private Vector elementsV = new Vector();
	
	public NullLayoutPanel(){
		super();
	} 
	
	public void layout(){
		int w = 0; 
		int h = 0;
		Rectangle rect;
		int k = elementsV.size();
		if(k>0){
			Paintable paintable;
			for(int i=0; i<k; i++){
				paintable = (Paintable)elementsV.elementAt(i);
				rect = paintable.getBounds();
				w = (int)Math.max((double)w, (double)(rect.width+rect.x));
				h = (int)Math.max((double)h, (double)(rect.height+rect.y));
			}
		}
		/**/
		int s = getComponentCount();
		if(s > 0){
			Component comp;
			for(int i=0; i<s; i++){
				comp = getComponent(i);
				rect = comp.getBounds();
				w = (int)Math.max((double)w, (double)(rect.width+rect.x));
				h = (int)Math.max((double)h, (double)(rect.height+rect.y));
			}
		}	
			
		boolean doRepaint = false;
		if(width != w || height != h)
			doRepaint = true;
		width = w;
		height = h;
		if(doRepaint)
			repaint();
			
		super.layout();
	}
	
	public Dimension preferredSize(){
		layout();
		return new Dimension(width, height+1);
	}
	
	public Component add(Component com){
		Component toret = super.add(com);
		setSize(preferredSize());
		return toret;
	}
	
	public void removeAll(){
		elementsV.removeAllElements();
		elementsV = null;
		super.removeAll();
	}

	public void paint(Graphics g){
		try{
		super.paint(g);
		int s = elementsV.size();
		if(s != 0){
			for(int i=0; i<s; i++)
				((Paintable)elementsV.elementAt(i)).paint(g);
		}
		} catch (NullPointerException e){ }
	}
	
	public void addPaintable(Paintable p){
		elementsV.addElement(p);
		repaint();
	}
	
	public void addObject(Object obj){
		if(obj instanceof Paintable)
			addPaintable((Paintable)obj);
		else if(obj instanceof Component)
			add((Component)obj);
	}
}
			