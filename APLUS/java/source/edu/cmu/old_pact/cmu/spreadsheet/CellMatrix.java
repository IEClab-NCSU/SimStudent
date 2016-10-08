package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;


public class CellMatrix extends Object implements Cloneable {
	protected Cell[][] cells;
	protected int numOfRows;
	protected int numOfCols;
	protected Vector rowHeaders=null;
	protected Vector colHeaders=null;
	protected int curRow = 0;
	protected int curCol = 0;
	protected Vector rowElementV = null;
	protected Vector colElementV = null;
	private Vector zeroRowV = null;
	private Vector zeroColV = null;
	private Hashtable matrixDesc;
	private boolean isInitial;
	private MatrixElement zeroRowElement, zeroColElement;
	private boolean added = false;
	public static int END = 0;
	public static int BEGINNING = END+1; //this is not implemented
	public static int AFTER = END+2;
	public static int BEFORE = END+3;
	private int columnAdditionLocation = END;
	private int rowAdditionLocation = END;
	private int width = 70;
	
	Viewable view = null;
	
	
	public CellMatrix(Hashtable matrixDesc){
		this.matrixDesc = matrixDesc;
		initializeMatrix();
    }
	
	public CellMatrix (int r, int c, Vector rowHeaders, Vector colHeaders){
		numOfRows = r;
		numOfCols = c;
		createMatrixDesc(rowHeaders, colHeaders);
		initializeMatrix();
    }
	
	public CellMatrix (int r, int c){
		this(r, c, null, null);
    }
    
    public CellMatrix(Cell[][] cells, Vector rowHeaders, Vector colHeaders){
    	this.cells = cells;
    	numOfCols = cells[ 0 ].length;
        numOfRows = cells.length;
        addToMatrix();
    }
    
    public CellMatrix(Cell[][] cells){
    	this(cells, null, null);
    }
  
      
    protected void initializeMatrix(){
    	isInitial = true;
    	numOfRows = getIntValue((String)matrixDesc.get("NUMBEROFROWS"));
		numOfCols = getIntValue((String)matrixDesc.get("NUMBEROFCOLUMNS"));
		try{
			rowAdditionLocation = getIntAdditionLocation((String)matrixDesc.get("ROWADDITIONLOCATION"));
			columnAdditionLocation = getIntAdditionLocation((String)matrixDesc.get("COLUMNADDITIONLOCATION"));
		} catch (NoSuchFieldException e) { }
		resizeMatrix();
		isInitial = false;
	}
  
    private Hashtable getGeneralProperties(){
    	//General Properties for Cells and Headers 
    	Hashtable general = new Hashtable();
		general.put("VALUE", "");
		general.put("HASFOCUS", Boolean.valueOf("false"));
		general.put("ISSELECTED", Boolean.valueOf("false"));
		general.put("INTERNALSELECTED", Boolean.valueOf("false"));
		general.put("ISCALCULATABLE", Boolean.valueOf("false"));
		general.put("ISNUMERIC", Boolean.valueOf("false"));
		general.put("ISEDITABLE", Boolean.valueOf("true"));
		general.put("WIDTH", Integer.valueOf(String.valueOf(width)));
		general.put("HEIGHT", Integer.valueOf("20"));
		return general;
	}
    
    protected void createMatrixDesc(Vector rowHeaders, Vector colHeaders){
    	matrixDesc = new Hashtable();
		//Number of rows
		matrixDesc.put("NUMBEROFROWS", String.valueOf(numOfRows));
		//Number of columns
		matrixDesc.put("NUMBEROFCOLUMNS", String.valueOf(numOfCols));
		
		matrixDesc.put("ROWADDITIONLOCATION", getStrAdditionLocation(rowAdditionLocation));
		matrixDesc.put("COLUMNADDITIONLOCATION", getStrAdditionLocation(columnAdditionLocation));
		
		String val;
		Hashtable general = getGeneralProperties();
		for( int i=0; i<numOfRows; i++) {
			if(rowHeaders != null){
				general = getGeneralProperties();
				val = (String)rowHeaders.elementAt(i);
				general.put("VALUE", val);
				general.put("ISEDITABLE", Boolean.valueOf("false"));
			}
			matrixDesc.put("Row "+String.valueOf(i+1), general);
		}
		
		for( int i=0; i<numOfCols; i++){
			if(rowHeaders != null){
				general = getGeneralProperties();
				val = (String)colHeaders.elementAt(i);
				general.put("VALUE", val);
				general.put("ISEDITABLE", Boolean.valueOf("false"));
			}
			matrixDesc.put("Column "+String.valueOf(i+1), general);
		}
		
		general = getGeneralProperties();	
		//add Cell properties
		// add to general for each Cell rownum and colnum
		for( int i=0; i<numOfRows; i++) {
			for(int j=0; j<numOfCols; j++) {
				matrixDesc.put("Cell "+String.valueOf(i)+String.valueOf(j), general);
			}
		}
	}
    
    protected int getIntValue(String v){
    	try{
    		return Integer.parseInt(v);
    	}
    	catch (NumberFormatException e) { 
    		return -1;
    	}
    }
    
    public MatrixElement getRowMatrixElement(int num){
    	return (MatrixElement)rowElementV.elementAt(num);
    }
    
    public MatrixElement getColMatrixElement(int num){
    	return (MatrixElement)colElementV.elementAt(num);
    }
    
    protected void setRowAdditionLocation(String loc) throws NoSuchFieldException{
    	try{
    		rowAdditionLocation = getIntAdditionLocation(loc);
    	} catch (NoSuchFieldException e){
    		throw e;
    	}
    }
    
    protected void setColAdditionLocation(String loc) throws NoSuchFieldException{
    	try{
    		columnAdditionLocation = getIntAdditionLocation(loc);
    	} catch (NoSuchFieldException e){
    		throw e;
    	}
    }
    
    private int getIntAdditionLocation(String loc) throws NoSuchFieldException{
    	int toret = -1;
    	if(loc.equalsIgnoreCase("END"))
    		toret = END;
    	else if(loc.equalsIgnoreCase("BEGINNING"))
    		toret = BEGINNING;
    	else if(loc.equalsIgnoreCase("BEFORE"))
    		toret = BEFORE;
    	else if(loc.equalsIgnoreCase("AFTER"))
    		toret = AFTER;
    	if(toret > -1)
    		return toret;
    	else
    		throw new NoSuchFieldException("Property value '"+loc+"' doesn't exist for ");
    }
    
    private String getStrAdditionLocation(int intLoc){
    	String toret = "END";
    	if(intLoc == BEGINNING)
    		toret = "BEGINNING";
    	else if(intLoc == BEFORE)
    		toret = "BEFORE";
    	else if(intLoc == AFTER)
    		toret = "AFTER";
    	
    	return toret;
    }
    
    public String describeMatrix(){
    	Hashtable desc = new Hashtable();
    	desc.put("NUMBEROFROWS", String.valueOf(numOfRows));
		desc.put("NUMBEROFCOLUMNS", String.valueOf(numOfCols));
		Hashtable currElement;
		for(int i=0; i<numOfRows; i++) {
			currElement = ((RowMatrixElement)rowElementV.elementAt(i)).getAllProperties();
			desc.put("Row "+String.valueOf(i+1), currElement);
		}
		for(int i=0; i<numOfCols; i++) {
			currElement = ((ColMatrixElement)colElementV.elementAt(i)).getAllProperties();
			desc.put("Column "+String.valueOf(i+1), currElement);
		}
		for(int i=0; i<numOfRows; i++) {
			for(int j=0; j<numOfCols; j++) {
				currElement = cells[i][j].getAllProperties();
				desc.put("Cell "+String.valueOf(i)+String.valueOf(j), currElement);
			}
		}
		//serialize
		String result="";
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(bo);
			objOut.writeObject(desc);
			result = new String(bo.toByteArray());
		}
		catch (IOException e) {
			System.out.println("CellMatrix: Can't serialize matrix: "+e);
		}
		return result;
	}
	
	public void clear(){
		zeroRowElement.clear();
		zeroRowElement.accrossVisCells = null;
		zeroRowElement.visualCells = null;
		zeroRowElement.changes = null;
		zeroColElement.clear();
		zeroColElement.accrossVisCells = null;
		zeroColElement.visualCells = null;
		zeroColElement.changes = null;
		RowMatrixElement curRowEl;
    	int rowS = rowElementV.size();
    	for(int i=0; i<numOfRows; i++){
    		try{
    			curRowEl = (RowMatrixElement)rowElementV.elementAt(i);
    			for(int j=0; j<numOfCols; j++)
    				cells[i][j].removeRowElement(curRowEl);
    			
    		}catch (ArrayIndexOutOfBoundsException e){ }
		}
		ColMatrixElement curColEl;
		int colS = colElementV.size();
    	for(int j=0; j<numOfCols; j++){

    		try {
    			curColEl = 	(ColMatrixElement)colElementV.elementAt(j);
    			for(int i=0; i<numOfRows; i++)
    				cells[i][j].removeColElement(curColEl);
    		}catch (ArrayIndexOutOfBoundsException e){ }
    	}
    	rowElementV.removeAllElements();
    	colElementV.removeAllElements();
    	zeroRowElement = null;
    	zeroColElement = null;
    	zeroRowV = null;
    	zeroColV = null;
		cells = null;
		view = null;
	}	
    
    public void addToMatrix(){
    	if(rowElementV == null)
    		rowElementV = new Vector();
    	
    	rowHeaders = new Vector();
    	colHeaders = new Vector();

    	zeroRowV = new Vector();
    	zeroColV = new Vector();
    	if(zeroRowElement == null){
    		zeroRowElement = new RowMatrixElement(-1, "Row 1", this);
    		zeroColElement = new ColMatrixElement(-1, "Column 1", this);
    		zeroRowElement.setCanDelete(false);
    		zeroColElement.setCanDelete(false);
    	}
    	
    	String rowHead, colHead;
    	RowMatrixElement curRowEl;
    	int rowS = rowElementV.size();
    	for(int i=0; i<numOfRows; i++){
    		rowHead = "Row "+String.valueOf(i+2);
    		rowHeaders.addElement(rowHead);
    		try{
    			curRowEl = (RowMatrixElement)rowElementV.elementAt(i);
    			if(curRowEl.getPos() != i) {
    				curRowEl.setPos(i);
    				curRowEl.setName(rowHead);
    			}
    		} catch (ArrayIndexOutOfBoundsException e){
    			curRowEl = new RowMatrixElement(i, rowHead, this);
    			
    			if(i != 0)
    				curRowEl.setFont(((CellElement)rowElementV.elementAt(i-1)).getFont());
    			//if( isInitial) {
    				addGeneralProperties((CellElement)curRowEl, "Row "+String.valueOf(i+1));
    			//}		
    			rowElementV.insertElementAt(curRowEl, i);
    		}

    		for(int j=0; j<numOfCols; j++){
    			curRowEl.addCell(cells[i][j]);
    			cells[i][j].setRowElement(curRowEl);
    		}
    		zeroColV.addElement(curRowEl);
    	}
    	
    	if(colElementV == null)
    		colElementV = new Vector();
			
		ColMatrixElement curColEl;
		int colS = colElementV.size();
    	for(int j=0; j<numOfCols; j++){
    		colHead = "Column "+String.valueOf(j+2);
    		colHeaders.addElement(colHead);

    		try {
    			curColEl = 	(ColMatrixElement)colElementV.elementAt(j);
    			if(curColEl.getPos() != j) {
    				curColEl.setPos(j);
    				curColEl.setName(colHead);
    			}
    		} catch (ArrayIndexOutOfBoundsException e){
    			curColEl = new ColMatrixElement(j, colHead, this);
    			addGeneralProperties((CellElement)curColEl, "Column "+String.valueOf(j+1));
    			if(j != 0){
    				curColEl.setFont(((CellElement)colElementV.elementAt(j-1)).getFont());
    				curColEl.setCurrentHeight(((CellElement)colElementV.elementAt(j-1)).getHeight());
    			}
    			//if( isInitial) {
    			//	addGeneralProperties((CellElement)curColEl, "Column "+String.valueOf(j+1));
    			//}
    			
    			colElementV.insertElementAt(curColEl, j);
    		}
    		for(int i=0; i<numOfRows; i++){
    			curColEl.addCell(cells[i][j]);
    			cells[i][j].setColElement(curColEl);
    		}
    		zeroRowV.addElement(curColEl);
    	}
    	updateView();
    		
    }
    
    public Vector getZeroRowElements(){
    	return zeroRowV;
    }
    
    public MatrixElement getZeroRowElement(){
    	return zeroRowElement;
    }
    
    public MatrixElement getZeroColElement(){
    	return zeroColElement;
    }		                           
    
    public Vector getZeroColElements(){
    	return zeroColV;
    }
    
    public MatrixElement getRowMatrix(int i){
    	return (MatrixElement)rowElementV.elementAt(i);
    }
    
    public MatrixElement getColMatrix(int i){
    	return (MatrixElement)colElementV.elementAt(i);
    }
    
    public void removeRow(int rowNum){
    	MatrixElement me = getRowMatrix(rowNum);
    	if(me.getCanDelete()){
    		if(me instanceof DorminMatrixElement)
    			((DorminMatrixElement)me).sendNoteDelete();
    		numOfRows--;
    		Cell[][] bufCell = new Cell[numOfRows][numOfCols];
    		MatrixElement el = (MatrixElement)rowElementV.elementAt(rowNum);
    		el.clear();
    		rowElementV.removeElement(el);
    		rowHeaders.removeElementAt(rowNum);
    		boolean toRename = false;
    		int k = 0;
    		for(int i=0; i<numOfRows; i++){
    			if(i == rowNum){
    				k++;
    				toRename = true;
    			}
    			for(int j=0; j<numOfCols; j++){
    				try{
    					bufCell[i][j] = getCell(k, j);
    					if(toRename){
    						bufCell[i][j].setName("R"+String.valueOf(i)+"C"+String.valueOf(j));
    						bufCell[i][j].setPosition(i,j);
    					}
    				} catch (NoSuchCellException e){ 
    					System.out.println("CellMatrix removeRow "+e.toString());
    				}
    			}
    			k++;
    		}
    	
    		cells = bufCell;
    		addToMatrix();
    	}
    	else {
    		(Toolkit.getDefaultToolkit()).beep();
    	}
    }
    
    public void removeColumn(int colNum){
    	MatrixElement me = getColMatrix(colNum);
    	if(me.getCanDelete()){
    		if(me instanceof DorminMatrixElement)
    			((DorminMatrixElement)me).sendNoteDelete();
    		numOfCols--;
    		Cell[][] bufCell = new Cell[numOfRows][numOfCols];
    		MatrixElement el = (MatrixElement)colElementV.elementAt(colNum);
    		el.clear();
   			colElementV.removeElement(el);
    		colHeaders.removeElementAt(colNum);
    		boolean toRename = false;
    		int k = 0;
    		for(int i=0; i<numOfCols; i++){
    			if(i == colNum){
    				k++;
    				toRename = true;
    			}
    			for(int j=0; j<numOfRows; j++){
    				try{
    					bufCell[j][i] = getCell(j, k);
    					if(toRename) {
    						bufCell[j][i].setName("R"+String.valueOf(j)+"C"+String.valueOf(i));
    						bufCell[j][i].setPosition(j,i);
    					}
    				} catch (NoSuchCellException e){ 
    					System.out.println("CellMatrix removeColumn "+e.toString());
    				}
    			}
    			k++;
    		}
    	
    		cells = bufCell;
    		addToMatrix();
    	}
    	else {
    		(Toolkit.getDefaultToolkit()).beep();
    	}
    }
    
    public Vector getRowHeaders(){
    	return rowHeaders;
    }
    
    public Vector getColHeaders(){
    	return colHeaders;
    }
    
    public void addLastRow(){
    	added = true;
    	numOfRows++;
    	resizeMatrix();
    }
    
    public void addRow(String rowHead){
    	rowHeaders.addElement(rowHead);
    	addLastRow();
    }
    
    public void addRow(int rowNum){
    	if(rowAdditionLocation == END){
    		addLastRow();
    		return;
    	}
    	if(rowAdditionLocation == AFTER)
    		rowNum++;
    	if(rowNum < numOfRows){
    		numOfRows++;
    		added = true;
    		RowMatrixElement curRowEl = new RowMatrixElement(rowNum, "Row "+String.valueOf(rowNum+2), this);
    		if(rowNum != 0)
    			curRowEl.setFont(((CellElement)rowElementV.elementAt(rowNum-1)).getFont());
    		addGeneralProperties((CellElement)curRowEl, "Row "+String.valueOf(rowNum));
    		rowElementV.insertElementAt(curRowEl, rowNum);
    		insertCellsInRow(rowNum);
    		addToMatrix();
    	}
    	else
    		addLastRow();
    }
    
     public void addLastColumn(){
     	added = true;
    	numOfCols++;
    	resizeMatrix();
    }
    
    public void addColumn(String colHead){
    	colHeaders.addElement(colHead);
    	addLastColumn();
    }
    
    public void addColumn(int colNum){
    	if(columnAdditionLocation == END){
    		addLastColumn();
    		return;
    	}
    	if(columnAdditionLocation == AFTER)
    		colNum++;
    	if(colNum < numOfCols){
    		added = true;
    		numOfCols++;
    		ColMatrixElement curColEl = new ColMatrixElement(colNum, "Column "+String.valueOf(colNum+2), this);
    		addGeneralProperties((CellElement)curColEl, "Column "+String.valueOf(colNum));
    		if(colNum != 0) {
    			curColEl.setFont(((CellElement)colElementV.elementAt(colNum-1)).getFont());
    			curColEl.setCurrentHeight(((CellElement)colElementV.elementAt(colNum-1)).getHeight());
    		}
    		//addGeneralProperties((CellElement)curColEl, "Column "+String.valueOf(colNum));
    		colElementV.insertElementAt(curColEl, colNum);
    		insertCellsInCol(colNum);
    		addToMatrix();
    	}
    	else
    		addLastColumn();
    }
    
    public Cell getCell(int r, int c) throws NoSuchCellException{
    	Cell toret = null;
    	if(r == -1 && c == -1)
    		return new Cell(r,c);
    	try{
    		toret = cells[r][c];
    		return toret;
    	} 
    	catch (NullPointerException e){
    		String cName = "R"+String.valueOf(r)+"C"+String.valueOf(c);
    		throw new NoSuchCellException(cName);
    	}
    	catch (ArrayIndexOutOfBoundsException e){
    		String cName = "R"+String.valueOf(r)+"C"+String.valueOf(c);
    		throw new NoSuchCellException(cName);
    	}
    }
    
    public int getNumOfRows(){
    	return numOfRows;
    }
    
    public int getNumOfCols(){
    	return numOfCols;
    }
    
    private void insertCellsInCol(int colNum){
    	Cell[][] bufCell = new Cell[numOfRows][numOfCols];
    	String currCell;
    	int k;
    	for(int i=0; i<numOfRows; i++){
    		k= 0;
    		for(int j=0; j<numOfCols-1; j++){
    			if(k<colNum) {
    				try{
    					bufCell[i][k] = getCell(i, j);
    				} catch (NoSuchCellException e){ }
    			}	
    			if(k==colNum){
    			    bufCell[i][k] = new Cell(i, k);
    			    currCell = "Cell "+String.valueOf(i)+String.valueOf(k);
    			    addGeneralProperties((CellElement)bufCell[i][k], currCell);
    			    k++;
    			}
    			if(k>colNum){
    				try{
    					bufCell[i][k] = getCell(i, j);
    					bufCell[i][k].setName("R"+String.valueOf(i)+"C"+String.valueOf(k));
    					bufCell[i][k].setPosition(i,k);
    				} catch (NoSuchCellException e){ }
    			}
    			k++;
    		}
    	}
    	cells = bufCell;
    }
    
    private void insertCellsInRow(int rowNum){
    	Cell[][] bufCell = new Cell[numOfRows][numOfCols];
    	String currCell;
    	int k;
    	for(int i=0; i<numOfCols; i++){
    		k= 0;
  
    		for(int j=0; j<numOfRows-1; j++){
    			if(k<rowNum){
    				try{
    					bufCell[k][i] = getCell(j, i);
    				} catch (NoSuchCellException e){ }
    			}	
    			if(k==rowNum){
    			    bufCell[k][i] = new Cell(k,i);
    			    currCell = "Cell "+String.valueOf(k)+String.valueOf(i);
    			    addGeneralProperties((CellElement)bufCell[k][i], currCell);
    			    k++;
    			}
    			if(k>rowNum){
    				try{
    					bufCell[k][i] = getCell(j, i);
    					bufCell[k][i].setName("R"+String.valueOf(k)+"C"+String.valueOf(i));
    					bufCell[k][i].setPosition(k,i);
    				} catch (NoSuchCellException e){ }
    			}
    			k++;
    		}
    	}
    	cells = bufCell;
    }
    
    private void resizeMatrix(){
    	Cell[][] bufCell = new Cell[numOfRows][numOfCols];
    	String currCell;
    	for(int i=0; i<numOfRows; i++){
    		for(int j=0; j<numOfCols; j++){
    			try{
    				bufCell[i][j] = getCell(i, j);
    			} catch (NoSuchCellException e){
    			    bufCell[i][j] = new Cell(i, j);
    			    if( isInitial ) {
    			    	currCell = "Cell "+String.valueOf(i)+String.valueOf(j);
    			    	addGeneralProperties((CellElement)bufCell[i][j], currCell);
    			    }
    			}
    		}
    	}
    	cells = bufCell;
    	addToMatrix();
    }
    
    protected void addGeneralProperties(CellElement cellEl, String currCell){
    	try{
    	Hashtable general = (Hashtable)matrixDesc.get(currCell);
    	
   		if(general == null) {
   			general = getGeneralProperties();
			matrixDesc.put(currCell, general);
		}
		
    	cellEl.setValue((String)general.get("VALUE"));
    	cellEl.setClicked(DataConverter.getBooleanValue("hasFocus",general.get("HASFOCUS")));
    	Object hb = general.get("HASBOUNDS");
    	if(hb != null)
    		cellEl.setHasBounds(DataConverter.getBooleanValue("hasBounds",hb));
    	if(hb == null && (cellEl instanceof RowMatrixElement || cellEl instanceof ColMatrixElement))
    		cellEl.setHasBounds(false);
    	Object ho = general.get("ISHIGHLIGHTED");
    	if(ho != null)
    		cellEl.setHighlighted(DataConverter.getBooleanValue("IsHighlighted",ho));
    	cellEl.setSelected(DataConverter.getBooleanValue("IsSelected",general.get("ISSELECTED")));
    	cellEl.setInternalSelected(DataConverter.getBooleanValue("InternalSelected",general.get("INTERNALSELECTED")));
    	cellEl.setCalculate(DataConverter.getBooleanValue("IsCalculatable",general.get("ISCALCULATABLE")));
    	cellEl.setNumeric(DataConverter.getBooleanValue("IsNumeric",general.get("ISNUMERIC")));
    	cellEl.setEditable(DataConverter.getBooleanValue("IsEditable",general.get("ISEDITABLE")));
    	cellEl.setWidth(DataConverter.getIntValue("Width",general.get("WIDTH")));
    	cellEl.setHeight(DataConverter.getIntValue("Height",general.get("HEIGHT")));
    	}catch (DataFormattingException e) { } 
    }
    
    public void setView(Viewable view){
    	this.view = view;
    }
    
    public void updateView(){
    	if(view != null) {
    		view.updateView();
    		view.layout();
    	}
    }
    
    public void setAdded(boolean b){
    	added = b;
    }
    public boolean isAdded(){
    	return added;
    }
}

    	 
    	