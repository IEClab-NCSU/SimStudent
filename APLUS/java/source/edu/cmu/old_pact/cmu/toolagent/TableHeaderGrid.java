//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/TableHeaderGrid.java
package edu.cmu.old_pact.cmu.toolagent;

import edu.cmu.old_pact.cmu.spreadsheet.DorminMatrixElement;
import edu.cmu.old_pact.cmu.spreadsheet.HeaderGrid;
import edu.cmu.old_pact.cmu.spreadsheet.MatrixElement;
import edu.cmu.old_pact.dormin.ObjectProxy;


public class TableHeaderGrid extends HeaderGrid{
	
	public TableHeaderGrid(MatrixElement mElement, int r, int c){
		super(mElement,r, c);
		
	}
		
	public void createRowProxy(ObjectProxy parent){
		MatrixElement mElement = getMatrixElement();
		if(mElement instanceof DorminMatrixElement) {
			ObjectProxy op = new ReasonRowProxy(parent, "Row");
			op.setRealObject((DorminMatrixElement)mElement);
			((DorminMatrixElement)mElement).setProxyInRealObject(op);
		}
	}

}