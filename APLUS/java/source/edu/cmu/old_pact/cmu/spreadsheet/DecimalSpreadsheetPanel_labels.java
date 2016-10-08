package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;

import edu.cmu.old_pact.dormin.ObjectProxy;


public class DecimalSpreadsheetPanel_labels extends DecimalSpreadsheetPanel {

	Image graphicalImage;

	public DecimalSpreadsheetPanel_labels(CellMatrix cellMatrix, 
							PropertyChangeListener frameListener, ObjectProxy proxyParent){
		super(cellMatrix, frameListener, proxyParent);
	}
	

	public synchronized void updateView(){
	
	     super.updateView();
		
		try{
    		graphicalImage=Toolkit.getDefaultToolkit().getImage("images/PlaceValueLabels2.gif");

    		// wait for the image to load
    		MediaTracker tracker = new MediaTracker(this);
    		tracker.addImage(graphicalImage, 0);
    		tracker.waitForAll();
  			}
  		catch (Exception e)
  			{ throw new NullPointerException(e.toString());}

	}
	
	public Dimension preferredSize(){
		if(numCols == 0)
			return new Dimension(300, 150);
		int x_size = 0;
		for(int j=0; j< numCols; j++){
			x_size = x_size + grids[0][j].getWidth() + 25;
		}
		if(showHeaders == HEADER_BOTH || showHeaders == HEADER_ROW)
			x_size = x_size+rowHeader[0].getWidth()+10;
		
		int y_size = 0;
		for(int i=0; i<numRows; i++){
			y_size = y_size + grids[i][0].getHeight() + 60;
		}
		if(showHeaders == HEADER_BOTH || showHeaders == HEADER_COLUMN)
			y_size = y_size + colHeader[0].getHeight()+40;
		return new Dimension(x_size+10, y_size);
	}
	
	public void paint(Graphics g){
	    super.paint(g);
  		g.drawImage(graphicalImage, 85, 0, null);
	}
	
}
		
		
		