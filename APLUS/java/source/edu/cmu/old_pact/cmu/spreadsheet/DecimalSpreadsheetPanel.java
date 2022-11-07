package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.Dimension;
import java.beans.PropertyChangeListener;

import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.toolframe.LineCanvas;


public class DecimalSpreadsheetPanel extends SpreadsheetPanel {

	protected int numDigits = 1;
		

	public DecimalSpreadsheetPanel(CellMatrix cellMatrix, 
							PropertyChangeListener frameListener, ObjectProxy proxyParent){
		super(cellMatrix, frameListener, proxyParent, 3);
		canRemoveComponents = false;
	}
	

	public synchronized void updateView(){
		
		CellMatrix cellMatrix = getCellMatrix();
		int currNumRows = 0;
		int currNumCols = 0;
		int width = 30;
		
		GridBox[][] buf_grids = new GridBox[currNumRows][currNumCols];
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
					    	t_field.setHasBounds(true);	
							t_field.setOwnProperty("TEXTLENGTH", String.valueOf(numDigits));
						} catch(NoSuchFieldException e) { }
					}
				}
			} catch (NullPointerException e){ }
		}
				
		numRows = cellMatrix.getNumOfRows();
		numCols = cellMatrix.getNumOfCols();
		
//trace.out("!!!!!!! NATASHA: in DecimalSpreadsheetPanel.updateView!!!!!!!");
//trace.out("  -----numRows= "+numRows+" (cur="+currNumRows+")  numCols= "+numCols+
//" (cur="+currNumCols+")  numDigits= "+numDigits);	
		
		grids = new GridBox[numRows][numCols];		
		int x_delta = 0;
		int y_delta = 0;
			
		for(int i=0; i<numRows; i++){
			for(int j=0; j<numCols; j++){
				if(!canRemoveComponents && i<currNumRows && j <currNumCols)
					grids[i][j] = buf_grids[i][j];
				else {			     
					try{
					    Gridable t_field; 
						grids[i][j] = createNewGridBox(i,j,width);
						grids[i][j].addPropertyChangeListener(this);
					    this.addPropertyChangeListener(grids[i][j]);	
						grids[i][j].setSize(grids[i][j].preferredSize()); 
						initCellGridElement(grids[i][j], i, j);
						
						if (!((j == 0) && (i == 1))) { 					   				    						
						  GridbagCon.viewset(this,grids[i][j], j+x_delta, i+y_delta, 1, 1, 10, 10, 10 ,10);
						}
						grids[i][j].initTextField();
								
						if (i == 3 && j == 9){ 
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
			x_size = x_size + grids[0][j].getWidth() + 25;
		}
		//x_size = x_size + 250;
		//trace.out("Natasha:In DSP.preferredSize; x_size= " +x_size);
		if(showHeaders == HEADER_BOTH || showHeaders == HEADER_ROW)
			x_size = x_size+rowHeader[0].getWidth()+10;
		
		int y_size = 0;
		for(int i=0; i<numRows; i++){
			y_size = y_size + grids[i][0].getHeight() + 25;
		}
		//y_size = y_size + 250;		
				
		if(showHeaders == HEADER_BOTH || showHeaders == HEADER_COLUMN)
			y_size = y_size + colHeader[0].getHeight()+40;
		return new Dimension(x_size+10, y_size);
	}

}
		
		
		