package edu.cmu.old_pact.cmu.solver.uiwidgets;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import edu.cmu.old_pact.htmlPanel.HtmlPanel;

//import java.awt.*;

public class HtmlSolverPanel extends SolverPanel implements ComponentListener{
	protected static final int widthBuffer = 0;
	//replace MultiLineLabel with HtmlPanel to display formatted expressions
	protected HtmlPanel htmlPanel=null;
	boolean firstRedraw = false;
	int delta = 1;
	
	public HtmlSolverPanel(PanelParameters parms) {
		super(parms);
		addComponentListener(this);
	}
	
	public void setParams(PanelParameters params){
		super.setParams(params);
		if(htmlPanel != null){
			Dimension d = getSize();
			d.width -= widthBuffer;
			//System.out.println("  HSP.sP: setting html size: " + d);
			htmlPanel.setSize(d);
		}
	}

	public Dimension getPreferredSize(){
		Dimension d;
		if(htmlPanel != null){
			d = htmlPanel.getPreferredSize();
			d.width += widthBuffer;
		}
		else{
			d = super.getPreferredSize();
		}

		/*System.out.println("  HSP.gPS: returning: " + d);
		  if(htmlPanel != null){
		  System.out.println("                     from htmlPanel");
		  }
		  else{
		  System.out.println("                     from super");
		  }*/
		return d;
	}

	public Dimension getMinimumSize(){
		Dimension d;
		if(htmlPanel != null){
			d = htmlPanel.getMinimumSize();
			d.width += widthBuffer;
		}
		else{
			d = super.getMinimumSize();
		}

		return d;
	}

	public void setSize(Dimension d){
		super.setSize(d);
		if(htmlPanel != null){
			d.width -= widthBuffer;
			//System.out.println("  HSP.sS: setting html size: " + d);
			htmlPanel.setSize(d);
		}
		else{
			//System.out.println("  HSP.sS: htmlPanel is null");
		}
	}

	public void paint(Graphics g){
		//System.out.println("  IP.paint: " + g);
		if(htmlPanel != null){
			htmlPanel.paint(g);
		}
		super.paint(g);
	}

	public void componentResized(ComponentEvent e){
		/*System.out.println("HSP.cR: " + e);
		  System.out.println("        sizes: this: " + getBounds());
		  System.out.println("               html: " + htmlPanel.getBounds());*/
		//System.out.println("              inner: " + htmlPanel.htmlViewer.getBounds());
		redraw();
		/*System.out.println("HSP.cR: end");
		  System.out.println("        sizes: this: " + getBounds());
		  System.out.println("               html: " + htmlPanel.getBounds());*/
		//System.out.println("              inner: " + htmlPanel.htmlViewer.getBounds());
	}

	/*based on the redraw method in Diagram.java (in the geometry
      interface)*/
	public void redraw(){
		if(htmlPanel != null){
			//Dimension dim = m_ScrollPanel.getSize();
			Dimension dim = getSize();
			Dimension html_dim = htmlPanel.preferredSize();
			//System.out.println("HSP.r: sizes: " + dim);
			//System.out.println("              " + html_dim);
			htmlPanel.setHtmlWidth(dim.width);
			//System.out.println("HSP.r: calling HP.sW");
			htmlPanel.setWidth(dim.width);
			//System.out.println("HSP.r: calling HP.sH");
			htmlPanel.setHeight(htmlPanel.getHtmlHeight());
			//System.out.println("HSP.r: calling HP.layout");
			htmlPanel.layout();
			//System.out.println("HSP.r: calling HP.pS");
			Dimension cur_dim = htmlPanel.preferredSize();
			//System.out.println("HSP.r: HP.pS: " + cur_dim);
			/*if(	Math.abs(html_dim.width-cur_dim.width)>1 ||
				Math.abs(html_dim.height-cur_dim.height)>1)
				firstRedraw = true;
			if(firstRedraw){
				firstRedraw = false;
				Dimension d = getSize();
				System.out.println("HSP.r: current size: " + d);
				System.out.println("HSP.r: changing to : " +
								   new Dimension(d.width,d.height+delta));
				setSize(d.width, d.height+delta);
				delta = (-1)*delta;
				}*/
		}
	}

	public void componentMoved(ComponentEvent e) { }

    public void componentShown(ComponentEvent e) { }
    
    public void componentHidden(ComponentEvent e){ }
}
