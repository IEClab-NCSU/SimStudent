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
			//trace.out("  HSP.sP: setting html size: " + d);
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

		/*trace.out("  HSP.gPS: returning: " + d);
		  if(htmlPanel != null){
		  trace.out("                     from htmlPanel");
		  }
		  else{
		  trace.out("                     from super");
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
			//trace.out("  HSP.sS: setting html size: " + d);
			htmlPanel.setSize(d);
		}
		else{
			//trace.out("  HSP.sS: htmlPanel is null");
		}
	}

	public void paint(Graphics g){
		//trace.out("  IP.paint: " + g);
		if(htmlPanel != null){
			htmlPanel.paint(g);
		}
		super.paint(g);
	}

	public void componentResized(ComponentEvent e){
		/*trace.out("HSP.cR: " + e);
		  trace.out("        sizes: this: " + getBounds());
		  trace.out("               html: " + htmlPanel.getBounds());*/
		//trace.out("              inner: " + htmlPanel.htmlViewer.getBounds());
		redraw();
		/*trace.out("HSP.cR: end");
		  trace.out("        sizes: this: " + getBounds());
		  trace.out("               html: " + htmlPanel.getBounds());*/
		//trace.out("              inner: " + htmlPanel.htmlViewer.getBounds());
	}

	/*based on the redraw method in Diagram.java (in the geometry
      interface)*/
	public void redraw(){
		if(htmlPanel != null){
			//Dimension dim = m_ScrollPanel.getSize();
			Dimension dim = getSize();
			Dimension html_dim = htmlPanel.preferredSize();
			//trace.out("HSP.r: sizes: " + dim);
			//trace.out("              " + html_dim);
			htmlPanel.setHtmlWidth(dim.width);
			//trace.out("HSP.r: calling HP.sW");
			htmlPanel.setWidth(dim.width);
			//trace.out("HSP.r: calling HP.sH");
			htmlPanel.setHeight(htmlPanel.getHtmlHeight());
			//trace.out("HSP.r: calling HP.layout");
			htmlPanel.layout();
			//trace.out("HSP.r: calling HP.pS");
			Dimension cur_dim = htmlPanel.preferredSize();
			//trace.out("HSP.r: HP.pS: " + cur_dim);
			/*if(	Math.abs(html_dim.width-cur_dim.width)>1 ||
				Math.abs(html_dim.height-cur_dim.height)>1)
				firstRedraw = true;
			if(firstRedraw){
				firstRedraw = false;
				Dimension d = getSize();
				trace.out("HSP.r: current size: " + d);
				trace.out("HSP.r: changing to : " +
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
