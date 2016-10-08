package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.doublebufferedpanel.DoubleBufferedPanel;
import edu.cmu.old_pact.fastbeanssupport.FastProBeansSupport;

public class SpreadsheetPanel extends DoubleBufferedPanel // Panel
								 implements 	PropertyChangeListener, 
														ComponentListener,
														Viewable{
	protected CellMatrix cellMatrix;
	public GridBox[][] grids;
	HeaderGrid[] rowHeader, colHeader; 
	public HeaderGrid zeroRowHeader, zeroColHeader;
	private int widthPlus = 1;
	protected int numRows = 0;
	protected int numCols = 0;
	private Focusable currCell = null;
	PropertyChangeListener frameListener;
	
	protected FastProBeansSupport changes = new FastProBeansSupport(this);
	
	public ObjectProxy proxyParent;
	public static final int HEADER_BOTH 	= 0;
	public static final int HEADER_ROW 		= HEADER_BOTH+1;
	public static final int HEADER_COLUMN 	= HEADER_BOTH+2;
	public static final int HEADER_NO 	= HEADER_BOTH+3;
	public int showHeaders = HEADER_BOTH;
	public boolean canRemoveComponents = true;
	private Dimension currentDim = new Dimension(10,10);
	
	private static final int ADDROW = 0;
	private static final int ADDCOLUMN = ADDROW+1;
	private static final int REMOVEROW = ADDCOLUMN+1;
	private static final int REMOVECOLUMN = REMOVEROW+1;
	
	
	public SpreadsheetPanel(CellMatrix cellMatrix, 
							PropertyChangeListener frameListener, ObjectProxy proxyParent, int showHeaders){
		this.cellMatrix = cellMatrix;
		this.frameListener = frameListener;
		this.proxyParent = proxyParent;
		this.showHeaders = showHeaders;
		
		//sm = new SymbolManipulator();
		//sm.autoStandardize = true;
		cellMatrix.setView(this);
		
		addComponentListener(this);
		
		setLayout(new GridBagLayout());
		if(showHeaders != HEADER_NO)
			updateView();
			
//System.out.println("after updateView: zeroColHeader="+zeroColHeader+" zeroRowHeader="+zeroRowHeader);
	}
	
	public SpreadsheetPanel(CellMatrix cellMatrix, 
							PropertyChangeListener frameListener, ObjectProxy proxyParent){
		this(cellMatrix,frameListener,proxyParent,0);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		changes.removePropertyChangeListener(l);
	}
	
	public GridBox[][] getAllGrids(){
		return grids;
	}
	
	public CellMatrix getCellMatrix(){
		return cellMatrix;
	}
	
	private void clearZeroElement(HeaderGrid z_El, boolean delete_Proxy){
		z_El.removePropertyChangeListener(this);
		this.removePropertyChangeListener(z_El);
		z_El.removeFrameAsListener(frameListener);
		if(!delete_Proxy){
			z_El.clearAlways();
			z_El.deleteAll();
		}
	}
	
	public void clearSpreadsheet(boolean delete_Proxy){
		if(grids != null){
		try{
			GridBox gb = grids[0][0];
			int numC = grids[0].length;
			int numR = grids.length;
			
			if(zeroRowHeader != null){
				clearZeroElement(zeroRowHeader, delete_Proxy);
				clearZeroElement(zeroColHeader, delete_Proxy);
			}			
	
			
			for(int j=0; j< numC; j++){
				colHeader[j].removePropertyChangeListener(this);
				this.removePropertyChangeListener(colHeader[j]);
				zeroRowHeader.removePropertyChangeListener((PropertyChangeListener)colHeader[j].getGridable());
				colHeader[j].removeFrameAsListener(frameListener);
				colHeader[j].clear(delete_Proxy);
				if(!delete_Proxy){
					colHeader[j].changes = null;
					colHeader[j] = null;
				}
			}
			for(int i=0; i<numR; i++){
				rowHeader[i].removePropertyChangeListener(this);
				this.removePropertyChangeListener(rowHeader[i]);
				zeroColHeader.removePropertyChangeListener((PropertyChangeListener)rowHeader[i].getGridable());
				rowHeader[i].removeFrameAsListener(frameListener);
				rowHeader[i].clear(delete_Proxy);
				if(!delete_Proxy){
					rowHeader[i].changes = null;
					rowHeader[i] = null;
				}
			}
			for(int i=0; i<numR; i++){
				for(int j=0; j< numC; j++){
					grids[i][j].removePropertyChangeListener(this);
					this.removePropertyChangeListener(grids[i][j]);
					grids[i][j].removeFrameAsListener(frameListener);
					grids[i][j].clear(delete_Proxy);
					if(!delete_Proxy)
						grids[i][j] = null;
				}
			}
			// if delete Worksheet proxy
			if(!delete_Proxy){
				removeComponentListener(this);
				zeroRowHeader.clear(delete_Proxy);
				zeroColHeader.clear(delete_Proxy);
				//zeroRowHeader.textField.clear();
				//zeroRowHeader.textField = null;
				zeroRowHeader = null;
				zeroColHeader = null;
				colHeader = null;
				rowHeader = null;
				grids = null;
				frameListener = null;
				currCell = null;
				setLayout(null);
				cellMatrix.clear();
				cellMatrix = null;
			}	
			
		} catch (NullPointerException e) { }
		grids = null;
		} // end if(grids != null)
		removeAll();
		System.gc();
	}
	
	public int getNumOfRows(){
		return numRows;
	}
	
	public int getNumOfCols(){
		return numCols;
	}
	
	public synchronized void updateView(){
		if(canRemoveComponents)
			clearSpreadsheet(true);
 		
		int currNumRows = 0;
		int currNumCols = 0;
		
		GridBox[][] buf_grids = new GridBox[currNumRows][currNumCols];
		if(!canRemoveComponents){
			try{
				currNumRows = grids.length;
				currNumCols = grids[0].length;
				buf_grids = new GridBox[currNumRows][currNumCols];
				for(int i=0; i<currNumRows; i++)
					for(int j=0; j<currNumCols; j++)
						buf_grids[i][j] = grids[i][j];
				} catch (NullPointerException e){ }
		}
				
		addHeaders();
		numRows = cellMatrix.getNumOfRows();
		numCols = cellMatrix.getNumOfCols();
		grids = new GridBox[numRows][numCols];
		
		int x_delta = 1;
		int y_delta = 1;
		if(showHeaders == HEADER_NO){
			x_delta = 0;
			y_delta = 0;
		}
		else if(showHeaders == HEADER_ROW)
			y_delta = 0;
		else if(showHeaders == HEADER_COLUMN)
			x_delta = 0;
		for(int i=0; i<numRows; i++){
			for(int j=0; j<numCols; j++){
				if(!canRemoveComponents && i<currNumRows && j <currNumCols)
					grids[i][j] = buf_grids[i][j];
				else{
					try{
						grids[i][j] = createNewGridBox(i,j);
						grids[i][j].addPropertyChangeListener(this);
						this.addPropertyChangeListener(grids[i][j]);
						
						grids[i][j].setSize(grids[i][j].preferredSize());
						initCellGridElement(grids[i][j], i, j);
						GridbagCon.viewset(this,grids[i][j], j+x_delta, i+y_delta, 1, 1, 0, 0, 0 ,0);
							//call initTextField AFTER textField is added to the panel
						grids[i][j].initTextField();
						
					} catch (NoSuchCellException e) { }
				
				}
			}
		}
		buf_grids = null;
		validate();		
		updateFocus();
	}
	
	public GridBox createNewGridBox(int r, int c) throws NoSuchCellException {
	   try{
		   GridBox gb = new GridBox(cellMatrix.getCell(r, c),(r+2),(c+2));
		   return gb;
		} catch (NoSuchCellException e) { }
		return null;
	}
	
	public GridBox createNewGridBox(int r, int c, int minWidth) throws NoSuchCellException {
	   try{
		   GridBox gb = new GridBox(cellMatrix.getCell(r, c),(r+2),(c+2), minWidth);
		   return gb;
		} catch (NoSuchCellException e) { }
		return null;
	}
	
	/*
	public boolean keyDown(Event e, int key)  {
    	if(e.id == Event.KEY_ACTION && e.controlDown()) {
  			if(key == 1008) 
				((DorminToolFrame)frameListener).openTeacherWindow();
  		}
  		return super.keyDown(e, key);
  	}
  	*/	
	
	public void setFontSize(int s){
		changes.firePropertyChange("FontSize", Integer.valueOf("0"), Integer.valueOf(String.valueOf(s)));
	}
	
	public void setCanRemoveComponents(boolean b){
		canRemoveComponents = b;
	}
	
	public Dimension preferredSize(){
		if(numCols == 0)
			return new Dimension(300, 150);
		int x_size = 0;
		int y_size = 0;
		 		
 	  try{
		for(int j=0; j< numCols; j++){
			if(grids[0][j] != null)
			   x_size = x_size + grids[0][j].getWidth();
		}
		if(showHeaders == HEADER_BOTH || showHeaders == HEADER_ROW)
			x_size = x_size+rowHeader[0].getWidth()+10;
		
		if(numRows == 0)
			return new Dimension(x_size, 150);
		
		for(int i=0; i<numRows; i++){
			if(grids[i][0] != null)
			   y_size = y_size + grids[i][0].getHeight();
		}
		if(showHeaders == HEADER_BOTH || showHeaders == HEADER_COLUMN)
			y_size = y_size + colHeader[0].getHeight()+40;
	  } catch (NullPointerException e){ }
	  
		return new Dimension(x_size+30, y_size);
	}
/*	
	public Dimension minimumSize(){
		return preferredSize();
	}
	
*/	
	public void requestFocus(){
		if(currCell == null)
			currCell = findFocusedGrid();
		currCell.requestFocus();
	}
/*	
	public void transferFocus() {
   		//do nothing here , so parent will not get a focus
   	}
   	
   	public void nextFocus() {
   		//do nothing here , so parent will not get a focus
   	}
*/	
	public void initCellGridElement(GridElement gridEl, int rowNum, int colNum){
		 initGridElement(gridEl);
	}
	
	public void initGridElement(GridElement gridEl){
		((DorminGridElement)gridEl).createObjectProxy(proxyParent);
		addFrameAsListener(gridEl);
	}
	
	public void addFrameAsListener(GridElement gridEl){
	 	gridEl.addFrameAsListener(frameListener);
	}
	
	public void addZeroElements(){
	  boolean first = false;
	  
	  if(zeroRowHeader == null){
	  		// name zeroRowHeader "r1c1" (instead of "r-1c-1")
		zeroRowHeader = new HeaderGrid(cellMatrix.getZeroRowElement(),1,1);// -1, -1);
		zeroColHeader = new HeaderGrid(cellMatrix.getZeroColElement(),1,1);// -1, -1);
		first = true;
	  }
		zeroRowHeader.addPropertyChangeListener(this);
		this.addPropertyChangeListener(zeroRowHeader);
		
		zeroColHeader.addPropertyChangeListener(this);
		this.addPropertyChangeListener(zeroColHeader);
		
		CustomTextField textField = null;
		if(showHeaders == HEADER_BOTH || showHeaders == HEADER_ROW) {
			initGridElement(zeroRowHeader);
				// display zeroRowHeader; remove default text; remove borders; lock
			GridbagCon.viewset(this, zeroRowHeader, 0, 0, 1, 1, 0, 0, 0 ,0);
			textField = (CustomTextField)(zeroRowHeader.getMatrixElement()).getOwnVisualCell();

			if(first) {
			  textField.setValue("");		
			  textField.setHasBounds(false);
			  textField.setLock(true);
			  textField.setColor("HASFOCUSCOLOR",Color.white);
			  textField.setColor("BACKGROUNDCOLOR",Color.white);
			  textField.setHeight(zeroColHeader.getHeight());			
			}
		}			
	
		if(showHeaders == HEADER_BOTH || showHeaders == HEADER_COLUMN) {
			initGridElement(zeroColHeader);
			
			if(showHeaders == HEADER_BOTH)
					// share same text field
				(zeroColHeader.getMatrixElement()).setOwnVisualCell(textField);
			else 
				if(showHeaders == HEADER_COLUMN)	
				    GridbagCon.viewset(this, zeroColHeader, 0, 0, 1, 1, 0, 0, 0 ,0);
		}
	}
	
	public void setRowHeaders(HeaderGrid[] rh){
		int s = rh.length;
		rowHeader = new HeaderGrid[s];
		for(int i=0; i<s; i++) 
			rowHeader[i] = rh[i];
	}
	
	public void setColHeaders(HeaderGrid[] ch){
		int s = ch.length;
		colHeader = new HeaderGrid[s];
		for(int i=0; i<s; i++) 
			colHeader[i] = ch[i];
	}
	
	public HeaderGrid createRowHeaderGrid(int r, int c){
		HeaderGrid hg = new HeaderGrid(cellMatrix.getRowMatrix(r), r+2, c);
		return hg;
	}
	
	public HeaderGrid createColHeaderGrid(int r, int c){
		HeaderGrid hg = new HeaderGrid(cellMatrix.getColMatrix(c), r, c+2);
		return hg;
	}
	
	public void addHeaders(){
		//addZeroElements();
		
		Vector rowHeaders = cellMatrix.getRowHeaders();
		Vector colHeaders = cellMatrix.getColHeaders();
		int s = rowHeaders.size();
		
		int curs = 0;
		HeaderGrid[] buf_rowHeader = new HeaderGrid[curs];
		if(!canRemoveComponents){
			try{
				curs = rowHeader.length;
				buf_rowHeader = new HeaderGrid[curs];
				for(int i=0; i<curs; i++) 
					buf_rowHeader[i] = rowHeader[i];
			} catch (NullPointerException e) { }
		}
		
		if(curs == 0)
			addZeroElements();
		rowHeader = new HeaderGrid[s];
		for(int i=0; i<s; i++) {
			if(!canRemoveComponents && i<curs)
				rowHeader[i] = buf_rowHeader[i];
			else{
				rowHeader[i] = createRowHeaderGrid(i, 1);
				rowHeader[i].addPropertyChangeListener(this);
				this.addPropertyChangeListener(rowHeader[i]);
				
				if(!canRemoveComponents)
					cellMatrix.setAdded(false); // don't send NoteCreate message
				initGridElement(rowHeader[i]);			
				if(showHeaders == HEADER_BOTH || showHeaders == HEADER_ROW)
					GridbagCon.viewset(this,rowHeader[i], 0, i+1, 1, 1, 0, 0, 0 ,0);
				(rowHeader[i].getGridable()).setWidth(zeroColHeader.getWidth());
				
				zeroColHeader.addPropertyChangeListener((PropertyChangeListener)rowHeader[i].getGridable());
				rowHeader[i].setSize(rowHeader[i].preferredSize());
			}
		}
		
		s = colHeaders.size();
		curs = 0;
		HeaderGrid[] buf_colHeader = new HeaderGrid[curs];
		if(!canRemoveComponents){
			try{
				curs = colHeader.length;
				buf_colHeader = new HeaderGrid[curs];
				for(int i=0; i<curs; i++) 
					buf_colHeader[i] = colHeader[i];
			} catch (NullPointerException e) { }
		}
		
		colHeader = new HeaderGrid[s];
		for(int i=0; i<s; i++) {
			if(!canRemoveComponents && i<curs)
				colHeader[i] = buf_colHeader[i];
			else{
				colHeader[i] = createColHeaderGrid(1,i);
				colHeader[i].addPropertyChangeListener(this);
				this.addPropertyChangeListener(colHeader[i]);
				
				if(!canRemoveComponents)
					cellMatrix.setAdded(false); // don't send NoteCreate message
				initGridElement(colHeader[i]);
				if(showHeaders == HEADER_BOTH || showHeaders == HEADER_COLUMN)
					GridbagCon.viewset(this,colHeader[i], i+1, 0, 1, 1, 0, 0, 0 ,0);
					
				(colHeader[i].getGridable()).setHeight(zeroRowHeader.getHeight());
				colHeader[i].setSize(colHeader[i].preferredSize());
				zeroRowHeader.addPropertyChangeListener((PropertyChangeListener)colHeader[i].getGridable());
			}
		}
	}
	
	
//	public void update(Graphics g){
//System.out.println("SP update");
//		paint(g);
//	}
	
	public GridElement[] getColHeaders(){
		return colHeader;
	}
	
	public GridElement[] getRowHeaders(){
		return rowHeader;
	}
	
	public GridElement getCellElement(int r, int c){
		return grids[r][c];
	}	
	
	public void removeAll(){
		super.removeAll();
		numRows = 0; 
		numCols = 0;
	}	

	public void updateFocus(){
		if(isShowing())
			findFocusedGrid().requestFocus();
	}
	
	public Focusable findFocusedGrid(){
		if(currCell == null){
			if(showHeaders < HEADER_NO){
				if(showHeaders == HEADER_BOTH || showHeaders == HEADER_ROW){
					Vector rowHeaders = cellMatrix.getRowHeaders();
	
					for(int i=0; i<numRows; i++){
						if(rowHeader[i].hasFocus()) {
							currCell = rowHeader[i];
							return rowHeader[i];
						}
					}
				}
				if(showHeaders == HEADER_BOTH || showHeaders == HEADER_COLUMN){
					Vector colHeaders = cellMatrix.getColHeaders();
					for(int i=0; i<numCols; i++){
						if(colHeader[i].hasFocus()) {
							currCell = colHeader[i];
							return colHeader[i];
						}
					}
				}
			}
			for(int i=0; i<numRows; i++){
				for(int j=0; j<numCols; j++){
					if(grids[i][j].hasFocus()) {
						currCell = grids[i][j];
						return grids[i][j];
					}
				}
			}
			
			if(currCell == null){
				if(showHeaders == HEADER_BOTH || showHeaders == HEADER_COLUMN)
					currCell = colHeader[0];
				else
					currCell = grids[0][0];
			}
		}
		return currCell;
	}
	
	public Focusable getFocusedGridByName(String n){
		if(n == null){
			currCell = null;
			return findFocusedGrid();
		}
		if(showHeaders < HEADER_NO){
			if(showHeaders == HEADER_BOTH || showHeaders == HEADER_ROW){
				Vector rowHeaders = cellMatrix.getRowHeaders();
	
				for(int i=0; i<numRows; i++){
					if(rowHeader[i].getName().equalsIgnoreCase(n)) {
						currCell = rowHeader[i];
						return rowHeader[i];
					}
				}
			}
			if(showHeaders == HEADER_BOTH || showHeaders == HEADER_COLUMN){
				Vector colHeaders = cellMatrix.getColHeaders();
				for(int i=0; i<numCols; i++){
					if(colHeader[i].getName().equalsIgnoreCase(n)) {
						currCell = colHeader[i];
						return colHeader[i];
					}
				}
			}
		}
		for(int i=0; i<numRows; i++){
			for(int j=0; j<numCols; j++){
				if(grids[i][j].getName().equalsIgnoreCase(n)) {
					currCell = grids[i][j];
					return grids[i][j];
				}
			}
		}
		currCell = grids[0][0];
		return currCell;
	}
		
	
	public Focusable getCurrentCell(){
		return currCell;
	}
	
	public void setGrids(GridBox[][] g){
		grids = new GridBox[numRows][numCols];
		for(int i=0; i<numRows; i++)
			for(int j=0; j<numCols; j++)
				grids[i][j] = g[i][j];
	}
	
	public void addRemove(int status){
		String focName = null;
		if(currCell != null)
			focName = currCell.getName();
		writeToCell();
		int num;
		switch (status){
			case ADDROW: 	num = getElementPos("R");
							if(num != -1) 
								cellMatrix.addRow(num);
							else 
								cellMatrix.addLastRow();
							break;
			case ADDCOLUMN:	num = getElementPos("C");
							if(num != -1) 
								cellMatrix.addColumn(num);
							else
								cellMatrix.addLastColumn();
							break;
			case REMOVEROW:	num = getElementPos("R");
							if(num != -1)
								cellMatrix.removeRow(num);
							else
								Toolkit.getDefaultToolkit().beep();		
							break;	
			case REMOVECOLUMN: 	num = getElementPos("C");
								if(num != -1)
									cellMatrix.removeColumn(num);
								else
									Toolkit.getDefaultToolkit().beep();	
								break;
		}
		
		sendResizedEvent();
		getFocusedGridByName(focName);
		currCell.requestFocus();
	}		
		
	
	public void addNewRow(){
		addRemove(ADDROW);
	/*
		String focName = null;
		if(currCell != null)
			focName = currCell.getName();
		writeToCell();
		int num = getElementPos("R");
		if(num != -1) 
			cellMatrix.addRow(num);
		else 
			cellMatrix.addLastRow();
			
		sendResizedEvent();
		getFocusedGridByName(focName);
		currCell.requestFocus();
	*/
	}
	
	public void addNewCol(){
		addRemove(ADDCOLUMN);
	/*
		writeToCell();
		int num = getElementPos("C");
		if(num != -1) 
			cellMatrix.addColumn(num);
		else
			cellMatrix.addLastColumn();
		sendResizedEvent();
		if(currCell != null)
			currCell.requestFocus();
		else
			requestFocus();
		*/
	}		
	
	public void removeRow(){
		addRemove(REMOVEROW);
		/*
		writeToCell();
		int num = getElementPos("R");
		if(num != -1)
			cellMatrix.removeRow(num);
		else
			Toolkit.getDefaultToolkit().beep();
		sendResizedEvent();
		requestFocus();
		*/
	}
	
	public void removeColumn(){
		addRemove(REMOVECOLUMN);
		/*
		writeToCell();
		int num = getElementPos("C");
		if(num != -1)
			cellMatrix.removeColumn(num);
		else
			Toolkit.getDefaultToolkit().beep();
		sendResizedEvent();
		requestFocus();
		*/
	}
	
	protected int getElementPos(String ident){
		int toret = -1;
		//Focusable el = findFocusedGrid();
		Focusable el = currCell;
		if(el instanceof HeaderGrid) {
			if(((HeaderGrid)el).getHeaderName().startsWith(ident))
				toret = ((HeaderGrid)el).getPos();
		}
		else {
			if(ident.equals("R") && el instanceof DorminGridElement)
				toret = ((DorminGridElement)el).getRowPos()-2;
		}
				
		return toret;
	}
	
	void nextGridInColumn(){
		Focusable currGrid = findFocusedGrid();
		currCell = currGrid;
		getNextInCol(currGrid).requestFocus();
	}
	
	void prevGridInColumn(){
		Focusable currGrid = findFocusedGrid();
		currCell = currGrid;
		getPrevInCol(currGrid).requestFocus();
	}
	
	protected Focusable  getNextInCol(Focusable currGrid){
		Focusable next = null;
		int[] coor = currGrid.getPosition();
		if(coor[0] < grids.length-1)
			coor[0] = coor[0]+1;
		else if(coor[0] == grids.length-1){
			coor[0] = -1;
			if(coor[1] < grids[0].length-1)
				coor[1] = coor[1]+1;
			else
				coor[1] = -1;
		}
		if(coor[0] == -1 && coor[1] == -1) 
			coor[0] = 0;
		next = getNext(coor);
		if(next == currCell){
			if(showHeaders == HEADER_BOTH || showHeaders == HEADER_ROW)
				return rowHeader[0];
			else if(showHeaders == HEADER_COLUMN)
				return colHeader[0];
			else
				return grids[0][0];
		}
		if(!next.isEditable())
			next = getNextInCol(next);
		
		return next;
	}
	
	protected Focusable  getPrevInCol(Focusable currGrid){
		Focusable prev = null;
		int[] coor = currGrid.getPosition();
		boolean changed = false;
		int lim = -1;
		if(showHeaders == HEADER_NO)
			lim = 0;
			
		if(coor[0] == 0 && coor[1] == lim) {
			coor[0] = grids.length-1;
			coor[1] = grids[0].length-1;
			changed = true;
		}
		if(!changed){
			if(coor[0] > lim)
				coor[0] = coor[0]-1;
			else if(coor[0] == lim){
				coor[0] = grids.length-1;
				if(coor[1] > lim)
					coor[1] = coor[1]-1;
				else
					coor[1] = lim;
			}
			if(coor[0] == lim && coor[1] == lim) 
				coor[0] = 0;
		}
			
		prev = getNext(coor);
		
		if(prev == currCell){
			if(showHeaders == HEADER_BOTH || showHeaders == HEADER_ROW)
				return rowHeader[0];
			else if(showHeaders == HEADER_COLUMN)
				return colHeader[0];
			else
				return grids[0][0];
		}
		if(!prev.isEditable())
			prev = getPrevInCol(prev);
		
		return prev;
	}
	
	public void writeToCell(){
		Focusable currFocused = findFocusedGrid();
		currFocused.writeToCell();
	}
	
	void nextGridInRow(){
		Focusable currGrid = findFocusedGrid();
		currCell = currGrid; 
		getNextInRow(currGrid).requestFocus();
	}
	
	void prevGridInRow(){
		Focusable currGrid = findFocusedGrid();
		currCell = currGrid; 
		getPrevInRow(currGrid).requestFocus();
	}
	
	protected Focusable getNextInRow(Focusable currGrid){
		Focusable next = null;
		int[] coor = currGrid.getPosition();
		if(coor[1] < grids[0].length-1)
			coor[1] = coor[1]+1;
		else if(coor[1] == grids[0].length-1){
			coor[1] = -1;
			if(coor[0] < grids.length-1)
				coor[0] = coor[0]+1;
			else
				coor[0] = -1;
		}
		if(coor[0] == -1 && coor[1] == -1) 
			coor[1] = 0;
		next = getNext(coor);
		
		if(next == currCell) {
			if(showHeaders == HEADER_BOTH || showHeaders == HEADER_COLUMN)
				return colHeader[0];
			else if (showHeaders == HEADER_ROW)
				return rowHeader[0];
			else
				return grids[0][0];
		}
		if(!next.isEditable())
			next = getNextInRow(next);
		
		return next;

	}
	
	protected Focusable getPrevInRow(Focusable currGrid){
		Focusable prev = null;
		int[] coor = currGrid.getPosition();
		boolean changed = false;
		int lim = -1;
		if(showHeaders == HEADER_NO)
			lim = 0;
			
		if(coor[0] == lim && coor[1] == 0) {
			coor[0] = grids.length-1;
			coor[1] = grids[0].length-1;
			changed = true;
		}
		
		if(!changed){
			if(coor[1] > lim)
				coor[1] = coor[1]-1;
			else if(coor[1] == lim){
				coor[1] = grids[0].length-1;
				if(coor[0] > lim)
					coor[0] = coor[0]-1;
				else
					coor[0] = lim;
			}
			if(coor[0] == lim && coor[1] == lim) 
				coor[1] = 0;
		}
		prev = getNext(coor);
		
		if(prev == currCell) {
			if(showHeaders == HEADER_BOTH || showHeaders == HEADER_COLUMN)
				return colHeader[0];
			else if (showHeaders == HEADER_ROW)
				return rowHeader[0];
			else
				return grids[0][0];
		}
		if(!prev.isEditable())
			prev = getPrevInRow(prev);
		
		return prev;

	}
	
	protected Focusable getNext(int[] coor){
		Focusable next = null;
		if (coor[0] == -1 && coor[1] != -1){
			if(showHeaders == HEADER_BOTH || showHeaders == HEADER_COLUMN)
				return colHeader[coor[1]];
			else if(showHeaders == HEADER_ROW)
				return rowHeader[coor[0]];
			else
				return grids[0][coor[1]];
		}
		else if(coor[0] != -1 && coor[1] == -1){
			if(showHeaders == HEADER_BOTH || showHeaders == HEADER_ROW)
				return rowHeader[coor[0]];
			else if (showHeaders == HEADER_COLUMN)
				return colHeader[coor[1]];
			else
				return grids[coor[0]][0];
		}
		else
			return grids[coor[0]][coor[1]];
	}
	
	public  void propertyChange(PropertyChangeEvent evt){
		String eventName = evt.getPropertyName();
		boolean force_gc = false;
		if(eventName.equalsIgnoreCase("NEXTINCOLUMN")) {
			nextGridInColumn();
			force_gc = true;
		}
		
		else if(eventName.equalsIgnoreCase("NEXTINROW")){ 
			nextGridInRow();
			force_gc = true;
		}
		
		else if(eventName.equalsIgnoreCase("PREVINCOLUMN")){ 
			prevGridInColumn();
			force_gc = true;
		}
		
		else if(eventName.equalsIgnoreCase("PREVINROW")) {
			prevGridInRow();
			force_gc = true;
		}
		
		else if(evt.getPropertyName().equalsIgnoreCase("FOCUSGAINED") && isShowing()){
			//workaroud not getting focus for the "R1C2" cell on the mouse click in a spreadsheet with headers
			if(!isShowing() && currCell != null && 
				currCell	== grids[0][0] 		&& 
				currCell != (Focusable)evt.getNewValue()){
				Gridable gridable = currCell.getGridable();
				try{
					gridable.setOwnProperty("HasFocus", Boolean.valueOf("false"));
				} catch (NoSuchFieldException e) { }
			}
			
			currCell = (Focusable)evt.getNewValue();
			changes.firePropertyChange("FocusGainedByCell", null, currCell);
			repaint();
		}
		
		//if(force_gc)
		//	System.gc();
		
	}
	
	public void setColAdditionLocation(String loc) throws NoSuchFieldException{
		try{
			cellMatrix.setColAdditionLocation(loc);
		} catch (NoSuchFieldException e){
			throw e;
		}
	}
	
	public void setRowAdditionLocation(String loc) throws NoSuchFieldException{
		try{
			cellMatrix.setRowAdditionLocation(loc);
		} catch (NoSuchFieldException e){
			throw e;
		}
	}
	
	public Gridable getGridable(){
		Gridable g = findFocusedGrid().getGridable();
		g.requestFocus();
		return g;
	}
	
	public void cut(){
		getGridable().cut();
	}
	
	
	public void copy(){
		getGridable().copy();
	}
	
	public void paste(){
		getGridable().paste();
	}
	
	public void askForHint(){
		getGridable().askForHint();
	}
	
	public void componentResized(ComponentEvent e){

		sendResizedEvent();
	}
	
	public void sendResizedEvent(){
		Dimension dim = preferredSize();
		if(frameListener != null && (dim.width != currentDim.width || dim.height != currentDim.height) ){
			setSize(dim);
			PropertyChangeEvent evt = new PropertyChangeEvent(this,"COMPONENTRESIZED", currentDim, dim);	
			frameListener.propertyChange(evt);
			currentDim = dim;
		}
	}

    public void componentMoved(ComponentEvent e) {
    }
    public void componentShown(ComponentEvent e) {
    }
    public void componentHidden(ComponentEvent e){
    }
}
		
		
		