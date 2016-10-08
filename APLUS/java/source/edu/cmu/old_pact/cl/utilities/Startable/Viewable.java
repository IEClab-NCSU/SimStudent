package edu.cmu.old_pact.cl.utilities.Startable;


import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Vector;

public interface Viewable
{
	public boolean getDelegatePreferredSize();
	
	public void setDelegatePreferredSize(boolean v);
	
	public void setBorder(boolean b);

	public void scrollToBottom();

	public void scrollToTop();

	public boolean getBorder();
	
	public void setHtmlViewerParent(Container c);
	
	public void layoutHtmlViewer();
	
	public void setURLBase(String b);
		
	public void removeAll();

	public void setFgColor(Color c);
	
	public void setBgColor(Color c);
	
	public void setFgColor(String c);
	
	public void setSize(Dimension d);

	public void setHeight(int h);
	
	public void setWidth(int w);
	
	public void setTopMargin(int margin);
	
	public void setLeftMargin(int margin);
		
	public void setNewTitle(String title);
	
	public int getNeededWidth();
	
	public Dimension preferredSize();
	
	public void setHtmlSize(Dimension dim);
	
	public int setHtmlWidth(int w);		
	
	public void displayHtml(String context);
	
	public void displayHtmlIfExists();
			
	public void resetProblem(String problemName);
				
	public int getHtmlHeight();
	
	public void setImageBase(String b);
	
	public void setFontSize(int s);
	
	public void showtxt(Vector tag);  	

	public void scrollToLastTag();

	public int preferredHeight();
	
}