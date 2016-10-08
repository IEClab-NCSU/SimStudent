package edu.cmu.old_pact.htmlPanel;

import java.beans.PropertyChangeListener;

import edu.cmu.old_pact.fastbeanssupport.FastProBeansSupport;


public class AdjustableHtmlPanel extends HtmlPanel {
	private FastProBeansSupport changes = new FastProBeansSupport(this);
	public final static int ADJUST_NO = 0;
	public final static int ADJUST_WIDTH = 1;
	public final static int ADJUST_HEIGHT = 2;
	public final static int ADJUST_BOTH = 3;
	private int adjust = 0;
	private int width = 0; 
	
	public AdjustableHtmlPanel(int width, int height, int adjust){
		// create HtmlPanel with scroll bar
		super(width, height, true);
		this.adjust = adjust;
		this.width = width;
	}
	
	public void setAdjustment(int ad){
		adjust = ad;
	}
	
	public void displayHtml(String context){
	
		super.displayHtml(context);
		int h = getHtmlHeight();
		int curh = getSize().height;


		switch(adjust){
			case ADJUST_HEIGHT:	
					setHeight(h);
					changes.firePropertyChange("Height", Integer.valueOf(String.valueOf(curh)),
												Integer.valueOf(String.valueOf(h)) );
					break;
			case ADJUST_BOTH:
					break;
			default: if(h<curh){
						setHeight(h);
						setSize(preferredSize());
						changes.firePropertyChange("Height",Integer.valueOf(String.valueOf(curh)),
												Integer.valueOf(String.valueOf(h)) ); 
					}
					break;
		}
	}
	 	 
	public void removeAll(){
		changes = null;
		super.removeAll();
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		changes.removePropertyChangeListener(l);
	}
	
	
}