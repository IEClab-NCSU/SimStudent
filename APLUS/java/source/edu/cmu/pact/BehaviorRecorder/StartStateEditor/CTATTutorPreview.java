/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-05-13 10:54:31 -0400 (Mon, 13 May 2013) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.12  2011/09/02 16:16:29  vvelsen
 Finalized the code that sends a preview of the Flash tutor to the Start State Editor.

 Revision 1.11  2011/08/26 15:15:34  vvelsen
 Mostly finalized the new BR look and feel. Will most likely be changed drastically after first screening during S&B

 Revision 1.10  2011/08/26 14:39:38  vvelsen
 Finally figured out how to get rid of the black background in the dock manager that appeared as black areas around all the view panes. I've added documentation in the code so that people can customize this further.

 Revision 1.9  2011/08/26 13:12:13  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 Revision 1.8  2011/07/19 19:54:33  vvelsen
 Major commit that now has all of the main start state editor functionality in place. It will take more finishing touches however. Message are sent when tables are changed but values aren't properly propagated yet from cell editors back into table cells. Also we need to add functionality to properly take in interface actions in the start state, map them to existing instances and show them in the SAI list.

 Revision 1.7  2011/07/01 18:34:57  vvelsen
 Lots of small fixes that make the start stated editor integrate better with the flash code.

 Revision 1.6  2011/06/28 13:35:15  vvelsen
 Cleaned up a lot of the state management and took away some of the scaffolding.

 Revision 1.5  2011/06/28 12:50:16  vvelsen
 Fixed removal of SAIs from table. Better preview and the interface now properly responds to interfaces being disconnected and re-started.

 Revision 1.4  2011/06/27 20:10:28  vvelsen
 The tutor preview window now properly scales and shows a preview tutor.

 Revision 1.3  2011/06/27 17:18:12  vvelsen
 Added some code to connect to socket proxy to the preview window in the start state editor. This way authors can see if a Flash tutor is actually connected to the BR.

 Revision 1.2  2011/06/20 15:05:51  vvelsen
 This should properly process the Commshell and show a first proper preview version of a Flash tutor.

 Revision 1.1  2011/06/16 14:15:43  vvelsen
 Added much better intraspection support. The BR has a much better idea of the state and layout of the tutor.

 
 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.StringTokenizer;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;

public class CTATTutorPreview extends CTATJPanel implements MouseListener
{	
	private static final long serialVersionUID = 1L;
	
	private int tutorWidth=1;
	private int tutorHeight=1;
	
	private float displayWidth=(float) 1.0;
	private float displayHeight=(float) 1.0;
	
	private float scale=(float) 1.0;	
	private float zoom=(float) 0.75;
	
	private float xOffset=(float) 1.0;
	private float yOffset=(float) 1.0;
	
	float divX=(float) 1.0;
	float divY=(float) 1.0;
	
	private Boolean isConnected=false;
	
	Dimension d;
	Font f = new Font("Verdana", Font.PLAIN, 12);
	FontMetrics fm;
	int fh, ascent;
	int space;	
	
	BR_Controller controller=null;	
	
	/**
	 *
	 */
    public CTATTutorPreview () 
    {
    	//setClassName ("CTATTutorPreview");
    	debug ("CTATTutorPreview ()");    	
    	
    	this.addMouseListener(this);
    }
    
    public void updatePreview ()
    {
    	debug ("updatePreview ()");
    	
    	if (CTATSSELink.preview!=null)
    	{
    		debug ("Repaint");
    		this.repaint();
    	}
    	else
    		debug ("Nothing changed");
    }
	/**
	 *
	 */	
	public void setController (BR_Controller aController)
	{
		debug ("setController ()");
		
		controller=aController;
	}
	/**
	 *
	 */    
	public void setIsConnected(Boolean isConnected) 
	{
		debug ("setIsConnect ("+isConnected+")");
		
		this.isConnected=isConnected;
		
		if (this.isConnected==false)
			resetZoom ();
		
		repaint();
	}
	/**
	 *
	 */	
	public Boolean getIsConnected() 
	{
		return isConnected;
	}    
	/**
	 *
	 */    
	public void setTutorWidth(int aWidth) 
	{
		//debug ("setTutorWidth ("+aWidth+")");
		
		this.tutorWidth = aWidth;
	}
	/**
	 *
	 */	
	public int getTutorWidth() 
	{
		return tutorWidth;
	}
	/**
	 *
	 */	
	public void setTutorHeight(int aHeight) 
	{
		//debug ("setTutorHeight ("+aHeight+")");
		
		this.tutorHeight = aHeight;
	}
	/**
	 *
	 */	
	public int getTutorHeight() 
	{
		return tutorHeight;
	}  
	/**
	 *
	 */
	public void resetZoom ()
	{
		zoom=(float) 1.0;
	}
	/**
	 *
	 */
	public void zoomIn ()
	{
		zoom+=0.05;
		if (zoom>1.0)
			zoom=(float) 1.0;
		
		this.repaint();
	}
	/**
	 *
	 */
	public void zoomOut ()
	{
		zoom-=0.05;
		if (zoom<0.05)
			zoom=(float) 0.05;
		
		this.repaint();		
	}
	/**
	 *
	 */
	private void calcTransformation ()
	{
		//debug ("calcTransformation ("+this.getWidth()+","+this.getHeight()+","+this.tutorWidth+","+this.tutorHeight+")");
		
		if ((tutorWidth<this.getWidth ()) && (tutorHeight<this.getHeight ()))
		{
			scale=(float) 1.0;
		}
		else
		{
			if ((tutorWidth-this.getWidth ()) >= (tutorHeight-this.getHeight()))
				scale=((float) this.getWidth())/tutorWidth;
			else
				scale=((float) this.getHeight())/tutorHeight;
		}
		
		displayWidth=(tutorWidth*scale*zoom);
		displayHeight=(tutorHeight*scale*zoom);
		
		divX=(float) (this.getWidth ()/2.0);
		divY=(float) (this.getHeight()/2.0);
		
		float tutDivX=(float) (displayWidth/2.0);
		float tutDivY=(float) (displayHeight/2.0);
		
		xOffset=(divX-tutDivX);
		yOffset=(divY-tutDivY);
		
		//debug ("calcTransformation ("+displayWidth+","+displayHeight+")->("+scale+","+zoom+")");		
	}
	/**
	 *
	 */
	private float calcXPixel (float aValue)
	{
		//debug ("calcXPixel ("+aValue+")");
		
		float scaledX=(aValue*scale);
		float zoomedX=(scaledX*zoom);
		float movedX=(zoomedX+((float) xOffset));
		
		return ((float) movedX);
	}
	/**
	 *
	 */
	private float calcYPixel (float aValue)
	{
		//debug ("calcYPixel ("+aValue+")");
		
		float scaledY=(aValue*scale);
		float zoomedY=(scaledY*zoom);
		float movedY=(zoomedY+((float) yOffset));
				
		return (movedY);		
	}
	/**
	 *
	 */
	private float calcXWidth (float aValue)
	{
		//debug ("calcXWidth ("+aValue+")");
		
		float scaledX=(aValue*scale);
		float zoomedX=(scaledX*zoom);
		
		return ((float) zoomedX);
	}
	/**
	 *
	 */
	private float calcYHeight (float aValue)
	{
		//debug ("calcYHeight ("+aValue+")");
		
		float scaledY=(aValue*scale);
		float zoomedY=(scaledY*zoom);
		
		return (zoomedY);		
	}	
	/**
	 *
	 */
	private void drawAxis (Graphics g)
	{
		g.setColor(new Color (150,150,150));
	
		g.drawLine((int) divX,0,(int) divX,this.getHeight ());
		g.drawLine(0,(int) divY,this.getWidth (),(int) divY);
	}
	/**
	 *
	 */	
	private void paintLabel (Graphics g,String aLabel)
	{
		d = getSize();
		g.setFont(f);
		    
		if (fm == null) 
		{
			fm = g.getFontMetrics();
			ascent = fm.getAscent();
			fh = ascent + fm.getDescent();
			space = fm.stringWidth(" ");
		}
		    
		g.setColor(Color.black);
		StringTokenizer st = new StringTokenizer(aLabel);
		int x = 0;
		int nextx=0;
		int y = (int) (this.getHeight()/2.0);
		String word, sp;
		int wordCount = 0;
		String line = "";
		    
		while (st.hasMoreTokens()) 
		{
			word = st.nextToken();
		      
			if (word.equals("<BR>")) 
			{
				drawString (g, line, wordCount, fm.stringWidth(line), y + ascent);
		        line = "";
		        wordCount = 0;
		        x = 0;
		        y = y + (fh * 2);
			} 
			else 
			{
		        int w = fm.stringWidth(word);
		        if ((nextx = (x + space + w)) > d.width) 
		        {
		          drawString(g, line, wordCount, fm.stringWidth(line), y + ascent);
		          line = "";
		          wordCount = 0;
		          x = 0;
		          y = y + fh;
		        }
		        
		        if (x != 0) 
		        {
		          sp = " ";
		        } 
		        else 
		        {
		          sp = "";
		        }
		        
		        line = line + sp + word;
		        x = x + space + w;
		        wordCount++;
			}
		}
		
		drawString(g, line, wordCount, fm.stringWidth(line), y + ascent);		
	}
	/**
	 *
	 */	
	public void drawString(Graphics g, String line, int wc, int lineW, int y) 
	{
		g.drawString(line, (d.width - lineW) / 2, y);//center
	}	
	/**
	 *
	 */
	private CTATComponent findParent (CTATComponent aChild)
	{
		debug ("findParent ()");
		
		String testName=aChild.getInstanceName();		
		String[] parts = testName.split("\\.");
		
		debug ("Using parent: " + parts [0]);
		
		for (int i=0;i<CTATSSELink.components.size();i++)
		{
			CTATComponent ref=CTATSSELink.components.get(i);
			if (ref.getInstanceName().equals(parts [0]))
			{
				debug ("Found component");
				return (ref);
			}	
		}
		
		return (null);
	}
	/**
	 *
	 */
	private void drawComponent (Graphics2D g2d,CTATComponent component)
	{
		debug ("drawComponent ()");
		
		if (component.getClassType().equals("CTATCommShell")==true)
		{
			if ((CTATSSELink.preview!=null) && (CTATSSELink.showPreview==true))
			{
				g2d.drawImage (CTATSSELink.preview,
								component.getPreviewX(),
								component.getPreviewY(),
								component.getPreviewWidth(),
								component.getPreviewHeight(),null);
			}	
			else
			{       						
				g2d.fill3DRect (component.getPreviewX(),
								component.getPreviewY(),
								component.getPreviewWidth(),
								component.getPreviewHeight(),
								true);

				if (component.getSelected ()==false)
					g2d.setColor(new Color (0,0,0));
				else
					g2d.setColor(new Color (255,255,0));

				g2d.drawRect (component.getPreviewX(),
								component.getPreviewY(),
								component.getPreviewWidth(),
								component.getPreviewHeight());
			}	
		}		
		else
		{
			if ((CTATSSELink.preview!=null) && (CTATSSELink.showPreview==true))
			{
				if (component.getSelected ()==true)
				{
					g2d.setColor(new Color (255,255,0));

					g2d.drawRect (component.getPreviewX(),
								  component.getPreviewY(),
								  component.getPreviewWidth(),
								  component.getPreviewHeight());
				}	
			}	
			else
			{       						
				g2d.fill3DRect (component.getPreviewX(),
								component.getPreviewY(),
								component.getPreviewWidth(),
								component.getPreviewHeight(),
								true);

				if (component.getSelected ()==false)
					g2d.setColor(new Color (0,0,0));
				else
					g2d.setColor(new Color (255,255,0));

				g2d.drawRect (component.getPreviewX(),
								component.getPreviewY(),
								component.getPreviewWidth(),
								component.getPreviewHeight());
			}				
		}
	}
	/**
	 *
	 */    
    public void paint (Graphics g) 
    {
    	debug ("paint ()");
    	    	
    	Graphics2D g2d=(Graphics2D) g;
    	
    	g2d.setColor(new Color (190,190,190));
    	g2d.fillRect(1,1,this.getWidth()-2,this.getHeight ()-2);
    	
    	if (getIsConnected ()==false)
    	{
    		paintLabel (g,"No Tutor Connected");
    	}
    	else
    	{
    		paintLabel (g,"Connected");
    		
    		calcTransformation ();
    	
    		drawAxis (g);
    	                    	
    		for (int i=0;i<CTATSSELink.components.size();i++)
    		{
    			CTATComponent component=CTATSSELink.components.get(i);
    			
    			if (component.getInstanceName().contains(".")==false)
    			{    							
    				debug ("Drawing component ["+component.getInstanceName()+"] at: " + (int) calcXPixel (component.getX ())+","+(int) calcYPixel (component.getY ())+","+(int) calcXPixel (component.getWidth())+","+(int) calcXPixel (component.getHeight()));
					
    				if (component.getClassType().equals("CTATCommShell")==true)
    				{
   						g2d.setColor(new Color (255,255,255));
    				}	
    				else
    					g2d.setColor(new Color (200,200,200));
    			
    				int placeX=(int) calcXPixel (component.getX ());
    				int placeY=(int) calcYPixel (component.getY ());
    				int placeWidth=(int) calcXWidth (component.getWidth());
    				int placeHeight=(int) calcYHeight (component.getHeight());
			
    				component.setPreviewDimensions(placeX,placeY,placeWidth,placeHeight);

    				drawComponent (g2d,component);
    			}	
    		}
    		
    		for (int j=0;j<CTATSSELink.components.size();j++)
    		{
    			CTATComponent component=CTATSSELink.components.get(j);
    			
    			if (component.getInstanceName().contains(".")==true)
    			{    						
    				CTATComponent parent=findParent (component);
    				if (parent!=null)
    				{
        				drawComponent (g2d,component);
    				}	
    				else
    					debug ("Error: parent not found");
    			}	
    		}    		
    	}	
    	
    	g2d.setColor(new Color (0,0,0));
		g2d.drawRect (0,0,this.getWidth ()-1,this.getHeight ()-1);
    }
	/**
	 *
	 */    
	@Override
	public void mouseClicked(MouseEvent arg0) 
	{
		debug ("mouseClicked ()");
		
		int placeX=arg0.getX();
		int placeY=arg0.getY();
		
		debug ("X: " + placeX + ",Y: " + placeY);
		
		for (int i=0;i<CTATSSELink.components.size();i++)
		{
			CTATComponent component=CTATSSELink.components.get(i);
						
			if (component.getClassType().equals("CTATCommShell")==false)
			{
				if ((placeX>component.getPreviewX()) && (placeX<component.getPreviewX ()+component.getPreviewWidth ()) && (placeY>component.getPreviewY()) && (placeY<component.getPreviewY ()+component.getPreviewHeight ()))
				{
					debug ("Found component click target");
					
					if (component.getSelected()==true)
					{
						component.setSelected(false);
						if (component.getChecker ()!=null)
							component.getChecker().setSelected(false);						
					}
					else
					{
						component.setSelected(true);
						if (component.getChecker ()!=null)
							component.getChecker().setSelected(true);												
					}	
				}
			}
		}	
	}
	/**
	 *
	 */	
	@Override
	public void mouseEntered(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 *
	 */	
	@Override
	public void mouseExited(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 *
	 */	
	@Override
	public void mousePressed(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 *
	 */	
	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}        
}
