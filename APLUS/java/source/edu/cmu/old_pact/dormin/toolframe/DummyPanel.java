//d:/Pact-CVS-Tree/Tutor_Java/./src/Middle-School/Java/dormin/toolframe/DummyPanel.java
package edu.cmu.old_pact.dormin.toolframe;


import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.Point;
import java.util.Vector;

import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;

  public class DummyPanel extends Panel 
  {
  
  DorminToolFrame frame;
  DummyCanvas dc;
     
   public  DummyPanel(DorminToolFrame fr)
   {
      frame = fr;
      dc = new DummyCanvas(frame);
      setLayout(new FlowLayout(FlowLayout.LEFT));
      add(dc);
   }
     
     public int getIntProperty(String proName) throws NoSuchPropertyException{
     	try{
     		int p=-1;
     		Object obj;
     		obj=frame.getProperty(proName);
    
        	if(obj != null){
        		try{
       				p = DataConverter.getIntValue(proName,obj);
      
       			}	catch (NumberFormatException e ){ 
       			throw new NoSuchPropertyException("Can't get integer from  "+obj+" in a DorminToolFrame");
  				}
  				catch (DataFormattingException e){
  					throw new NoSuchPropertyException(e.getMessage());
  				}
  			}
       		return p;
       		}
       	 catch (NoSuchPropertyException e) {
       	 	throw e;
  		}
  	}
  	
  	public Object getProperty(String proName) throws NoSuchPropertyException{
     	Object obj;
     	try{
     		obj=frame.getProperty(proName);
       		}
       	 catch (NoSuchPropertyException e) {
       	 	throw e;
  		}
  		return obj;
  	}

	public void arrangeFrame(boolean b){
  	    int dx = DummyFrame.lxmin;
  	    int dy = DummyFrame.lymin;
  	    Dimension d;
  		try{
  		    if(b == true) {
  			 d = getSize();
  			} else {  d = frame.preferredSize();}
  			if((System.getProperty("os.name").toUpperCase()).startsWith("MAC")){
  			frame.setProperty("Width", Integer.valueOf(String.valueOf(d.width - 12)));
  			frame.setProperty("Height", Integer.valueOf(String.valueOf(d.height - 43)));	
  			}
  			else{
  			     frame.setProperty("Width", Integer.valueOf(String.valueOf(d.width)));
  			     frame.setProperty("Height", Integer.valueOf(String.valueOf(d.height)));
  			}
  			Point loc = getLocation();
  			Vector v = new Vector();
  			v.addElement(Integer.valueOf(String.valueOf(loc.x - dx )));
  			v.addElement(Integer.valueOf(String.valueOf(loc.y - dy)));
  			frame.setProperty("Location", v);
  			  
  			// set visible only if property initiallyVisible == true
  			if(frame.getInitiallyVisible())
            	frame.setProperty("isVisible", Boolean.valueOf("true"));
            	
  			v.removeAllElements();
  			v = null;
  		} catch (DorminException e) {
  			e.printStackTrace();}
  	}
}
        
           