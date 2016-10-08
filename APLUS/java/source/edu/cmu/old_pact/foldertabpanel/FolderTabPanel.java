package edu.cmu.old_pact.foldertabpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Panel;
import java.util.Vector;

public class FolderTabPanel extends Panel {
    protected Vector labels = new Vector();
    protected Vector panels = new Vector();
    protected int[] tabWidths;
    protected String selectedPanel;
    protected Insets insets;
    protected int tabHeight = 21;
    protected int tabWidthBuffer = 13;
    private Color leftColor = Color.white;
    
    private boolean waitRedraw = false, resizeIt = false;
    private String prePanel = "";
    
    
    public FolderTabPanel() {
    		//Insets(top,left,bottom,right)
    	insets = new Insets(12 + tabHeight, 4,12,12); //14+tabHeight,14, 14, 14);
    	setLayout(new BorderLayout());
    }
    
     public void setWaitRedraw(boolean wh)  {
    	waitRedraw = wh;
    }
    
    public boolean getWaitRedraw()  {
    	return waitRedraw;
    }
    
    public void setLeftColor(Color c){
    	leftColor = c;
    }
    
    public Container getPanel(String name) {
    	int n = labels.indexOf(name);
    	return (Container)panels.elementAt(n);
    }
    
	public String returnprePanel()  {
		return prePanel;
	}

    public void addPanel(Container panel, String label) {
    	panels.addElement(panel);
    	labels.addElement(label);
    	add(panel);
    	panel.reshape(1, 22, size().width , size().height - tabHeight);
    	panel.hide();
    	if (panels.size() == 1)
        	setPanel(label);
    }
    
    public void removePanel(String label) {
    	int index = labels.indexOf(label);
    	if (index == -1)
        	throw new IllegalArgumentException(label + " is not a panel in this FolderTabPanel");
    	labels.removeElementAt(index);
    	panels.removeElementAt(index);
    	if (label.equals(selectedPanel)) {
        	if (labels.size() > 0)
            	setPanel((String) labels.elementAt(0));
       		else {
            	selectedPanel = null;
            	repaint();
        	}
    	}
    }


    public void setPanel(String label) {
    	int index = labels.indexOf(label);
    	if (index == -1)
        	throw new IllegalArgumentException(label + " is not a panel in this FolderTabPanel");
    	selectedPanel = label;
   		Container panel = (Container) panels.elementAt(index);
    	for (int i = 0; i < labels.size(); i++) {
        	if (i != index) {
            	((Container) panels.elementAt(i)).hide();
        	}
    	}
    	panel.reshape(insets().left, insets().top, size().width - insets().left - insets().right, size().height - insets().top - insets().bottom);
    	//panel.layout();
    	//panel.show();
    	panel.setVisible(true);
    panel.requestFocus();
    }

    public void layout() {
    	super.layout();
    	getSelectedPanel().reshape(insets().left, insets().top, size().width - insets().left - insets().right, size().height - insets().top - insets().bottom);
    	//getSelectedPanel().layout();
    	getSelectedPanel().repaint();
    }

    public Dimension preferredSize() {
    	int largestWidth = 0;
    	int largestHeight = 0;
    	for (int i = 0; i < panels.size(); i++) {
        	Dimension thisSize = ((Container)panels.elementAt(i)).preferredSize();
        	largestWidth = Math.max(thisSize.width, largestWidth);
 	    	largestHeight = Math.max(thisSize.height, largestHeight);
    	}
    	return new Dimension(largestWidth + insets().left + insets().right, largestHeight + insets().top + insets().bottom);
    }

    public String getSelectedPanelLabel() {
    	return this.selectedPanel;
    }

    public Container getSelectedPanel() {
    	return (Container) panels.elementAt(labels.indexOf(selectedPanel));
    }

    public Insets insets() {
    	return insets;
    }

    public void setInsets(Insets insets) {
    	this.insets = insets;
    }

    public int getTabHeight() {
    	return this.tabHeight;
    }

    public void setTabHeight(int tabHeight) {
    	this.tabHeight = tabHeight;
    }

    public int getTabWidthBuffer() {
    	return this.tabWidthBuffer;
    }

    public void setTabWidthBuffer(int tabWidthBuffer) {
    	this.tabWidthBuffer = tabWidthBuffer;
    }

    public void paint(Graphics g) {
    	//super.paint(g);
    	int top = tabHeight - 1;
    	int left = 0;
    	int bottom = size().height - 1;
    	int right = left + size().width - 1;
    	g.setColor(Color.darkGray);
    	g.setColor(Color.gray);
    	g.setColor(Color.white);
    	tabWidths = new int[labels.size()];
    	int selected = -1;
    	int selectedLoc = 0;
    	int xLoc = 2;
    	for (int i = 0; i < labels.size(); i++) {
        	String label = (String) labels.elementAt(i);
        	FontMetrics metrics = g.getFontMetrics(getFont());
        	tabWidths[i] = metrics.stringWidth(label) + tabWidthBuffer;
        	if (labels.elementAt(i).equals(selectedPanel)) {
            	selected = i;
            	selectedLoc = xLoc;
        	}
        	else
            	paintTab(g, false, xLoc, 0, tabWidths[i], top, label);
        	xLoc += tabWidths[i] - 1;
    	}
    	if (selected > -1) {
        	paintTab(g, true, selectedLoc, 0, tabWidths[selected], top, (String) labels.elementAt(selected));
        	g.setColor(leftColor);
        	g.drawLine(left, top, selectedLoc - 2, top); // Topleft to topright - left side
        	g.drawLine(selectedLoc + tabWidths[selected] + 2, top, right - 1, top);       // Topleft to topright - right side
    	}
   		else {
        	g.setColor(leftColor);
        	g.drawLine(left, top, right - 1, top); // Topleft to topright
    	}
    }


    private void paintTab(Graphics g, boolean selected, int x, int y, int width, int height, String label) {
    	int left = x;
    	int top = y + 2;
    	int right = x + width - 1;
    	int bottom = y + height - 1;
    	height -= 2;

    	if (selected) {
        	top -= 2;
        	left -= 2;
        	right += 2;
        	bottom += 1;
    	}

    
    	g.clearRect(x, y, width + 2, height);

    	g.setColor(Color.darkGray);
    	g.drawLine(right - 1, top + 2, right - 1, bottom); // Topright to bottomright
    	g.drawRect(right - 2, top + 1, 0, 0);              // Topright corner

    	g.setColor(Color.gray);
    	g.drawLine(right - 2, top + 2, right - 2, bottom); // Topright to bottomright

    	g.setColor(leftColor);
    	g.drawLine(left, top + 2, left, bottom);   // Topleft to bottomleft
    	g.drawLine(left + 2, top, right - 3, top); // Topleft to topright
    	g.drawRect(left + 1, top + 1, 0, 0);       // Topleft corner

    	g.setColor(Color.black);
    	Font font = getFont();
    	if(selected && font.getStyle() == Font.PLAIN)
    		font = new Font(font.getFamily(), Font.BOLD,font.getSize());
    	else if(!selected && font.getStyle() == Font.BOLD)
    		font = new Font(font.getFamily(),Font.PLAIN,font.getSize());
    	g.setFont(font);
    	FontMetrics metrics = g.getFontMetrics(font);
    	
    		
    	g.drawString(label, x + ((width - metrics.stringWidth(label)) / 2), metrics.getHeight() - metrics.getDescent() + top + 3);
    	if (selected) {
        	g.setColor(getBackground());
        	g.drawLine(left + 1, bottom, right - 3, bottom); // Bottomleft to bottomright
        	g.drawLine(left + 1, top + 3, left + 1, bottom); // Topleft to bottomleft indented by one
    	}
    }
    public int labsize()  {
    	return labels.size();
    }
    public int tabh()  {
    	return tabHeight;
    }
    public int[] tabw() {
    	return tabWidths;
    }
    public boolean waitr()  {
    	return waitRedraw;
    }

    public boolean mouseDown(Event event, int x, int y) {
    	if (y < tabHeight) {
        	int xLoc = 0;
        	for (int i = 0; i < labels.size(); i++) {
            	xLoc += tabWidths[i];
            	if (x < xLoc  && !waitRedraw) {
                	setPanel((String) labels.elementAt(i));
                	//postEvent(new Event(this, event.ACTION_EVENT, null));
                	deliverEvent(new Event(this, Event.ACTION_EVENT, null));
                	if(!resizeIt) {	
     					resize(this.size().width+1, this.size().height+1);
     					resizeIt = true;
     				}
     				if(resizeIt) {	
     					resize(this.size().width-1, this.size().height-1);
     					resizeIt = false;
     				}
     				validate();
               		update(getGraphics());
                	return true;
            	}
            	
            	if (x < xLoc  && waitRedraw) {
            		prePanel = (String)labels.elementAt(i);
            		return super.mouseDown(event, x, y);
            	}
            	
        	}
    	}
    	return super.mouseDown(event, x, y);
    }
}