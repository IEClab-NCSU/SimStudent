package edu.cmu.old_pact.cmu.spreadsheet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.Vector;

import edu.cmu.old_pact.fastbeanssupport.FastProBeansSupport;

public abstract class MatrixElement extends CellElement implements VetoableChangeListener{
	protected Vector cells;
	protected Vector visualCells;
	protected CellMatrix cellMatrix;
	protected Vector accrossVisCells = null;
	protected Vector accrossMatrixEls = null;
	
	protected Gridable ownVisualCell = null;

	protected int pos = -1;
	private boolean canDelete = true;
		
	protected FastProBeansSupport accross = new FastProBeansSupport(this);
	
	public MatrixElement(int pos, String name, CellMatrix cellMatrix){
		super();
		this.name = name;
		this.pos = pos;
		this.cellMatrix = cellMatrix;
		cells = new Vector();
		visualCells = new Vector();
	}
	public MatrixElement(int pos, CellMatrix cellMatrix){
		this(pos, null, cellMatrix);
	}
	
	public void addAccrossChangeListener(PropertyChangeListener l){
		accross.addPropertyChangeListener(l);
	}
	
	public void removeAccrossChangeListener(PropertyChangeListener l){
		accross.removePropertyChangeListener(l);
	}
	/*
	public void delete(){
		cells.removeAllElements();
		cells = null;
		visualCells.removeAllElements();
		visualCells = null;
		cellMatrix = null;
		accross = null;
		ownVisualCell = null;
	}
	*/	
	
	public void setCanDelete(boolean b){
		canDelete = b;
	}
	
	public boolean getCanDelete(){
		return canDelete;
	}

	public int getPos(){
		return pos;
	}
	
	public void setPos(int p){
		pos = p;
	}
	
	protected void getAccrossVisCells(Vector v){
		int s = v.size();
		if(accrossVisCells != null)
			clearAccrossListener();
			
		accrossVisCells = new Vector();
		for(int i=0; i<s; i++){
    		accrossVisCells.addElement(((MatrixElement)v.elementAt(i)).getOwnVisualCell());
    	}
    }

	public void setValue(String value){
		if(!value.equalsIgnoreCase(name))
			super.setValue(value);
	}
	
	public String getValue(){
		if(value.equals(""))
			return name;
		return value;
	}
	
	public void setWidth(int n){
		width = n;
	}
	
	public void setHeight(int n){
		height = n;
	}
	
	public void setCurrentHeight(int h){
		height = h;
	}

	void addCell(Cell cell){
		cells.addElement(cell);
		cell.addPropertyChangeListener(this);
	}
	
	public void setOwnVisualCell(Gridable v_cell){
		ownVisualCell = v_cell;
		if(ownVisualCell != null)
			addVisualCell(ownVisualCell);
	}
	
	public Gridable getOwnVisualCell(){
		return ownVisualCell;
	}
	
	public void select(boolean s){
		boolean oldValue = false;
		if(!s)
			oldValue = true;
		PropertyChangeEvent evt = new PropertyChangeEvent(this,"ISSELECTED", 
														  Boolean.valueOf(String.valueOf(oldValue)), 
														  Boolean.valueOf(String.valueOf(s)));
		try{
			vetoableChange(evt);
		} catch (PropertyVetoException e){
			System.out.println("MatrixElement: can't select object");
		}
	}	
	
	void addVisualCell(Gridable v_cell){
		visualCells.addElement(v_cell);
		v_cell.addVetoableChangeListener(this);
		addPropertyChangeListener((PropertyChangeListener)v_cell);
	}
	
	void removeVisualCell(Gridable v_cell){
		visualCells.removeElement(v_cell);
		v_cell.removeVetoableChangeListener(this);
		removePropertyChangeListener((PropertyChangeListener)v_cell);

	}
	
	Vector getCells(){
		return cells;
	}
	
	Vector getVisualCells(){
		return visualCells;
	}

	public void updateView(){
		if(cellMatrix!= null)
			cellMatrix.updateView();
	}
	
	protected void clearAccrossListener(){
		if(accrossVisCells != null){
			int s = accrossVisCells.size();
			for(int  i=0; i<s; i++) {
				this.removeAccrossChangeListener((PropertyChangeListener)accrossVisCells.elementAt(i));
				this.removeAccrossChangeListener((PropertyChangeListener)accrossMatrixEls.elementAt(i));
			}
			accrossVisCells.removeAllElements();
			accrossVisCells = null;
		}
	}
	
	public void clear(){
		clearAccrossListener();
		int s = cells.size();
		for(int i=0; i<s; i++) {
			((Cell)cells.elementAt(i)).removePropertyChangeListener(this);
			//((Gridable)this_cell).removeVetoableChangeListener(this);
		}
		accross = null;
		cellMatrix = null;
	//accrossMatrixEls.removeAllElements();
	//accrossMatrixEls = null;
	}
	
	protected synchronized void addAccrossListeners(){
		int s = accrossVisCells.size();
		for(int  i=0; i<s; i++) {
			this.addAccrossChangeListener((PropertyChangeListener)accrossVisCells.elementAt(i));
			this.addAccrossChangeListener((PropertyChangeListener)accrossMatrixEls.elementAt(i));
		}
	}	

	public  void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException{

		String propertyName = evt.getPropertyName();
		if(propertyName.equalsIgnoreCase("HEIGHT")) {
			int newsize = ((Integer)evt.getNewValue()).intValue();
			int maxH = getMaxHeightAcross(visualCells);			
			   // new cell height can't be smaller than the highest cell (considering
			   // the actual size of the cell contents) in the current row
			if(newsize < maxH)
			  newsize = maxH;			  
			if(height > newsize || height < newsize) {
				changes.firePropertyChange("height", Integer.valueOf(String.valueOf(height)), 
											Integer.valueOf(String.valueOf(newsize)));
				height = newsize;
			}
			else
				throw new PropertyVetoException("Can't change height", evt);
		}
		if(propertyName.equalsIgnoreCase("WIDTH")) {
			synchronized(this){
			int newsize = ((Integer)evt.getNewValue()).intValue();
			int maxW = getMaxWidthAcross(visualCells);	
			// new cell width can't be smaller than the widest cell in the current column
			if(newsize < maxW)
			  newsize = maxW;
			if(width > newsize || width < newsize) {
				changes.firePropertyChange("width", Integer.valueOf(String.valueOf(width)),
											 Integer.valueOf(String.valueOf(newsize)));
				width = newsize;
			}
			else	
				throw new PropertyVetoException("Can't change width", evt);
		}
			
		}
		if(propertyName.equalsIgnoreCase("ISSELECTED")) 
			changes.firePropertyChange("isselected", evt.getOldValue(), evt.getNewValue());
		if(propertyName.equalsIgnoreCase("INTERNALSELECTED")) 
			changes.firePropertyChange("internalselected", evt.getOldValue(), evt.getNewValue());
	}
	
	// return TRUE if proposed height is not smaller than any cell in the row
	protected boolean canModifyHeight(int newsize, Vector el){
		int numCells = el.size();
		int curHeight;
		for(int i=0; i<numCells; i++){
			curHeight = ((Gridable)el.elementAt(i)).getMinimumHeight();	
			if(curHeight > newsize)
				return false;
		}
		return true;
	}
	
	// return TRUE if proposed width is not smaller than any cell in the row
	protected boolean canModifyWidth(int newsize, Vector el){
		int numCells = el.size();
		int curWidth;
		for(int i=0; i<numCells; i++){
			curWidth = ((Gridable)el.elementAt(i)).getMinimumWidth();
			if(curWidth > newsize)
				return false;
		}
		return true;
	}
	
	// return current height of the tallest cell in the row (consider
	// ACTUAL height of the cell content)
	protected int getMaxHeightAcross(Vector el){
		int numCells = el.size();
		int curHeight;
		int maxH = 0;
		for(int i=0; i<numCells; i++){
			curHeight = ((Gridable)el.elementAt(i)).getMinimumHeight();	
			if(curHeight > maxH)
				maxH = curHeight;
		}
		return maxH;
	}
	
		
	protected  int getMaxWidthAcross( Vector el){
		int numCells = el.size();
		int curWidth;
		int maxW = 0;
		for(int i=0; i<numCells; i++){
			curWidth = ((Gridable)el.elementAt(i)).getMinimumWidth();
			if(curWidth > maxW)
				maxW = curWidth;
		}
		return maxW;
	}

/*	protected void setMaximumSize(){
		int s = cells.size();
		if(s > 0){
			height = ((Cell)cells.elementAt(0)).getHeight();
			width = ((Cell)cells.elementAt(0)).getWidth();
			for(int i=1; i<s; i++) {
				height = Math.max(height, ((Cell)cells.elementAt(i)).getHeight());
				width = Math.max(width, ((Cell)cells.elementAt(i)).getWidth());
			}
		}		
	}
*/	
	
}
		
