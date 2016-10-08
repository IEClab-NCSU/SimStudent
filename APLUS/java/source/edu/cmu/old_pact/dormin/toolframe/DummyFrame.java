//d:/Pact-CVS-Tree/Tutor_Java/./src/Middle-School/Java/dormin/toolframe/DummyFrame.java
/*  This class positions all tool frames by calculating the position and size
    and by using extensively UniqueLayout, which is an custom Layout */
     
package edu.cmu.old_pact.dormin.toolframe;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Vector;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.gridbagsupport.UniqueCon;
import edu.cmu.old_pact.gridbagsupport.UniqueLayout;


public class DummyFrame extends Frame
    {
    Vector panelsV;
    Vector panelsV1;    
    public  static int xMax ;
    public static int yMax ;
    Vector vecTile ;
    //int i;
    public static int locxMax = 0;
    public static int locyMax = 0;
    public int locfinalY = 0;
    public  static int lxmin = 0;
    public  static int lymin = 0;
    

    public DummyFrame(DorminToolFrame[] frames){
	   super ("Startup");
       setLayout(new UniqueLayout());
       panelsV = new Vector();
       panelsV1 = new Vector();      
       vecTile = new Vector();
    
    	DummyPanel panel = null;
    	xMax = 0;
    	yMax = 0;
    	int s = frames.length; 
   		Object isTileWindow;
   		Object usePS;
   		
   	main:for(int i =0; i<s  ; i++) {
       	panel = null;
      	try{  
       		isTileWindow = frames[i].getProperty("TILEDWINDOW");
        } catch(NoSuchPropertyException e){continue main;}
        try{  
       		usePS = frames[i].getProperty("USEPREFERREDSIZE");
        } catch(NoSuchPropertyException e){continue main;}  	
       	if(!(Boolean.valueOf(isTileWindow.toString())).booleanValue()){ 
          try{
          	if (frames[i].getProperty("XGridLocation") != null ) 
          		panel = new DummyPanel(frames[i]);          		   
       	  }catch(NoSuchPropertyException e){continue main;} 
      
        if(panel != null){
        	try{ 
                xMax = Math.max(xMax,panel.getIntProperty("XGridLocation"));
                yMax = Math.max(yMax,panel.getIntProperty("YGridLocation"));
            } catch(NoSuchPropertyException e){}
       	if(usePS == null || !(Boolean.valueOf(usePS.toString())).booleanValue()) {
        	try{
             	UniqueCon.viewset(this,panel,panel.getIntProperty("XGridLocation"),panel.getIntProperty("YGridLocation"),
                     panel.getIntProperty("NumOfXGrids"),panel.getIntProperty("NumOfYGrids"),0,0,0,0,0.6,1.0);
				panelsV.addElement(panel); 
 			}catch(NoSuchPropertyException e){} 
         }
         else{
              try{
                 UniqueCon.viewset(this,panel,panel.getIntProperty("XGridLocation"),panel.getIntProperty("YGridLocation"),
                 panel.getIntProperty("NumOfXGrids"),panel.getIntProperty("NumOfYGrids"),0,0,0,0,0.6,1.0);
				 panelsV1.addElement(panel);
				 }catch(NoSuchPropertyException e){}
			  } 
            }
		}
		else{
			   vecTile.addElement(frames[i]);
			 }    
       } // main:for
                  
        frames = null;
        pack();
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setLocation(1000,1000);             
      
    }
    
    // Finding Position(x,y) of the Tiled Windows 
     public void findLocation() {
     	Component[] c = getComponents();
                  
        if(c != null && c.length != 0){
            int compcount = c.length;
            Point loc; 
            int pos = 0;
            locxMax = c[0].getLocation().x; 
            for(int i = 1; i<compcount; i++){
                loc = c[i].getLocation();
                if(locxMax < loc.x){
                    locxMax = loc.x;
                    pos = i;
                }
            }       
            Dimension dm =c[pos].getSize();
            locfinalY =  dm.height;
        }

        c = null;
        tileWindows();
   	}
  
  
	public void setVisible(boolean b) {
    	DummyPanel pan = null;
        if (b) {
        	super.setVisible(b);
           	int s = panelsV.size();
            if(s > 0) {     
          		for(int i=0; i<s; i++){
               		pan = (DummyPanel) panelsV.elementAt(i);
               		Point  l = pan.getLocation();
               		if(i == 0){
                    	lxmin = l.x;
                   		lymin = l.y;
              		}
               		else{
                   		lxmin = Math.min(lxmin,l.x);
                 		lymin = Math.min(lymin,l.y);
            		}
            	}
           		for(int i=0; i<s; i++)
               		((DummyPanel) panelsV.elementAt(i)).arrangeFrame(true);
           	}
                 	
           	int s1 = panelsV1.size();
           	if(s1 > 0){
           		for(int i=0; i<s1; i++)
              		((DummyPanel) panelsV1.elementAt(i)).arrangeFrame(false);
            }
            findLocation();   	
            super.setVisible(false);
        }
       	else
      		super.setVisible(b);
     }
     
     
        // Placing all the Tiled windows in a particular tiled layout manner               
      public  void tileWindows(){ 
        int s = vecTile.size();  
        if(s == 0) return;        
              
      	Object[] toTile = new Object[s]; 
      	vecTile.copyInto(toTile);
      	Dimension d_1;
      	Dimension d_2;
      	Object buf;
      		// sort windows by their height
    	for (int i=0; i<s; i++)  {
            boolean sw = false;
            for (int j =s-1; j>i; j--)  {
            	d_1 = ((Frame)toTile[j]).getSize();    //preferredSize();
            	d_2 = ((Frame)toTile[j-1]).getSize();  //preferredSize();
                if (d_1.height > d_2.height)  {
                    buf = toTile[j];
                    toTile[j] = toTile[j-1];
                    toTile[j-1] = buf;
                    sw = true;
                }
            }
            if (sw == false )  
                 break;          
        }

        vecTile.removeAllElements();
        vecTile = null;
  		int a = 28, b = 35, screenH, y;
  		DorminToolFrame dtfr;
		screenH = Toolkit.getDefaultToolkit().getScreenSize().height - 30;
		
  		for(int i = 0; i <s; i++){  
			dtfr = (DorminToolFrame)toTile[i]; 
  			y = locfinalY + i*b;         
            dtfr.setLocation(locxMax+ i*a - lxmin,y);
        	try{                   
                 if(dtfr.getSize().height > screenH-y)	
                      //dtfr.setProperty("Height", Integer.valueOf(String.valueOf(screenH-y)));
                      dtfr.setSize(dtfr.getSize().width,screenH-y); 
                                                	  	  
  			     dtfr.setProperty("isVisible", Boolean.valueOf("true"));                   
  			}catch (DorminException e) {
  			                  e.printStackTrace();} 
  		}   		
  		toTile = null;  		                 
     }
            
                                 
     public void dispose(){
     	panelsV.removeAllElements();
     	panelsV = null;
     	panelsV1.removeAllElements();
     	panelsV1 = null;
     	super.dispose();
     }
  }
  
