package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;

import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.toolframe.LineCanvas;


public class DecimalSpreadsheetPanel_money extends DecimalSpreadsheetPanel {

	Image graphicalImage;

	public DecimalSpreadsheetPanel_money(CellMatrix cellMatrix, 
							PropertyChangeListener frameListener, ObjectProxy proxyParent){
		super(cellMatrix, frameListener, proxyParent);
	}
	

	public synchronized void updateView(){
	
		CellMatrix cellMatrix = getCellMatrix();
		int currNumRows = 0;
		int currNumCols = 0;		
		GridBox[][] buf_grids = new GridBox[currNumRows][currNumCols];
		int width = 30;
		
		try{
    		graphicalImage=Toolkit.getDefaultToolkit().getImage("images/PlaceValueMoney.gif");

    		// wait for the image to load
    		MediaTracker tracker = new MediaTracker(this);
    		tracker.addImage(graphicalImage, 0);
    		tracker.waitForAll();
  			}
  		catch (Exception e)
  			{ throw new NullPointerException(e.toString());}

		
		if(!canRemoveComponents){
			try{
				currNumRows = grids.length;
				currNumCols = grids[0].length;
				buf_grids = new GridBox[currNumRows][currNumCols];
				Gridable t_field;
				for(int i=0; i<currNumRows; i++){
					for(int j=0; j<currNumCols; j++){
						buf_grids[i][j] = grids[i][j];
						t_field = grids[i][j].getGridable();
						try{
							t_field.setWidth(width);
							t_field.setMinWidth(width);	
							t_field.setOwnProperty("TEXTLENGTH", String.valueOf(numDigits));
						} catch(NoSuchFieldException e) { }
					}
				}
			} catch (NullPointerException e){ }
		}
				
		numRows = cellMatrix.getNumOfRows();
		numCols = cellMatrix.getNumOfCols();
		
		//System.out.println("!!!!!!! NATASHA: in DecimalSpreadsheetPanel.updateView!!!!!!!");
		//System.out.println("numRows=  "+numRows+"  numCols=  "+numCols+"  numDigits=  "+numDigits);	
		
		grids = new GridBox[numRows][numCols];
		
		int x_delta = 1;
		int y_delta = 1;
		if(showHeaders == HEADER_NO){
			x_delta = 0;
			y_delta = 0;
		}
			
		for(int i=0; i<numRows; i++){
			for(int j=0; j<numCols; j++){
				if(!canRemoveComponents && i<currNumRows && j <currNumCols)
					grids[i][j] = buf_grids[i][j];
				else
				     {		     
					try{
					    Gridable t_field;
						grids[i][j] = createNewGridBox(i,j);
						grids[i][j].addPropertyChangeListener(this);
					    this.addPropertyChangeListener(grids[i][j]);
						grids[i][j].setSize(grids[i][j].preferredSize());
					    initCellGridElement(grids[i][j], i, j);
				    
						if (!((j == 0) && ((i == 1)))) { 					   				    						
						  GridbagCon.viewset(this,grids[i][j], j+x_delta, i+y_delta, 1, 1, 10, 10, 10, 10);
						}
						grids[i][j].initTextField();
						
						if (i == 3 && j == 7){ 
						  y_delta++; 						 					
						  LineCanvas lc = new LineCanvas(500);
						  GridbagCon.viewset(this,lc, 0, 3, 10, 1, 0, 0, 0 ,0); 
						 }						 
						 
				 	} catch (NoSuchCellException e) {}
				}
			}
		}
		
		buf_grids = null;
		validate();		
		updateFocus();
	}
	
	public Dimension preferredSize(){
		if(numCols == 0)
			return new Dimension(300, 150);
		int x_size = 0;
		for(int j=0; j< numCols; j++){
			x_size = x_size + grids[0][j].getWidth() + 26;
		}
		if(showHeaders == HEADER_BOTH || showHeaders == HEADER_ROW)
			x_size = x_size+rowHeader[0].getWidth()+10;
		
		int y_size = 0;
		for(int i=0; i<numRows; i++){
			y_size = y_size + grids[i][0].getHeight() + 61;
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
		
		
		