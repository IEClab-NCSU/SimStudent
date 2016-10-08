/**
 * Created: Dec 17, 2013
 * @author mazda
 * 
 */
package TabularPerception;

import java.util.Iterator;
import java.util.Vector;

import jess.Context;
import jess.Fact;
import jess.JessException;
import jess.Rete;
import jess.Value;
import jess.ValueVector;
import SimStudent2.ProductionSystem.UserDefWmeRetrievalJessSymbol;

/**
 * This class defines user defined symbols used in the perception part of production rule, i.e.,
 * WME path and its associated tests. 
 * 
 * This is a user-defined class tailored to work on the MWE that has a tabular structure
 * such as CTAT's DorminTable.
 * 
 * @author mazda
 *
 */
@SuppressWarnings("serial")
public abstract class TabularPerceptionUserDefJessSymbol extends UserDefWmeRetrievalJessSymbol {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Field
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	// Type of WME
	//
	final String CELL_TYPE= "MAIN::cell" ;
	final String COLUMN_TYPE="MAIN::column" ;
	final String COLUMN_INDEX_SLOT="column-number";
	final String ROW_INDEX_SLOT="row-number";
	final String TABLE_INDEX_SLOT="table-number";
	final String TABLE_TYPE="MAIN::table";
	final String COLUMN_LIST_NAME="columns";
	final String CELL_LIST_NAME="cells";
	
    boolean isCell(Fact fact) {
    	return fact.getName().equals(CELL_TYPE);
    }

    boolean isTable(Fact fact) {
    	return fact.getName().equals(TABLE_TYPE);
    }
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    /**
     * @param rete
     * @param cell, which must be a cell in a table
     * @return A column position of the cell in the table 
     */
    int getColumnPosition(Rete rete, Fact cell) {

    	int columnPosition = -1;
    	
		try {
	    	Value position = cell.getSlotValue(COLUMN_INDEX_SLOT);
			columnPosition = position.intValue(rete.getGlobalContext());
		} catch (JessException e) {
			e.printStackTrace();
		}
		
		return columnPosition;
    }
    
    /**
     * @param fact a <code>Fact</code> value
     * @return an <code>int</code> value which is the one-based row position of the fact in a table 
     **/

    int getRowPosition(Rete rete, Fact fact) {
    	
    	int rowPosition = -1;
    	
    	try {
			rowPosition = fact.getSlotValue(ROW_INDEX_SLOT).intValue(rete.getGlobalContext());
		} catch (JessException e) {
			e.printStackTrace();
		}
    	
		return rowPosition; 
	}

    
    /**
     * @param rete
     * @return a Vector of all tables in the working memory for rete, returns an empty vector if there are no tables
     */
    Vector<Fact> findAllTables(Rete rete)
    {
    	Vector<Fact> allTables=new Vector<Fact>();

    	@SuppressWarnings("unchecked")
		Iterator<Fact> iter=rete.listFacts();
    	while(iter.hasNext()) {
    		
    		Fact fact = (Fact)iter.next();

    		if(isTable(fact)) {
    			allTables.add(fact);
    		}
    	}
    	return allTables;
    }
    
    /**
     * @param cell a Fact representing a commCell
     * @param table a Fact representing an commTable
     * @param Rete rete
     * @return true if cellFact is in the table represented by tableFact 
     */
    boolean isInTable(Fact cell, Fact table, Rete rete) {

    	Context c = rete.getGlobalContext(); 
    	
		try {
			if(isCell(cell)&& isTable(table)) {
				
				Value columnValue;
				columnValue = table.getSlotValue(COLUMN_LIST_NAME);
				
				if(columnValue!=null) {
					
					ValueVector columns=columnValue.listValue(c);
					
					//loop through the columns of the table
					for(int colnum=0; colnum<columns.size(); colnum++) {
						
						Fact curColumn=columns.get(colnum).factValue(c);
						Value cellValue=curColumn.getSlotValue(CELL_LIST_NAME);
						
						if(cellValue!=null) {
							
							ValueVector cells=cellValue.listValue(c);
							
							//loop through the cells in the column
							for(int cellnum=0; cellnum<cells.size(); cellnum++) {
								
								Fact cell0=cells.get(cellnum).factValue(c);
								
								if ( sameCell(cell, cell0) )

									return true;
							}}}}}

		} catch (JessException e) {
			e.printStackTrace();
		}

    	return false;
    }
    
    /**
     * @param f1
     * @param f2
     * @param rete
     * @return T if f1 and f2 are NOT the same WME objects.
     */
    String distinctive(Fact f1, Fact f2) {
    
    	return (f1.getFactId() != f2.getFactId()) ? "T" : null;
    }
    
    /**
     * @param cell1, which must be a call
     * @param cell2, which must be a call
     * @param rete
     * @return T if cell1 and cell2 are in the same table WME
     */
	String sameTable(Fact cell1, Fact cell2, Rete rete) {

		if (isCell(cell1) && isCell(cell2)) {
			
			Vector<Fact> tables = findAllTables(rete);
			
			Iterator<Fact> tableIter=tables.iterator();
			while(tableIter.hasNext()) {
				
				Fact table = (Fact)tableIter.next();
				
				if( isInTable(cell1, table, rete) && isInTable(cell2, table, rete) )
					return "T";
			}
		}
		return null;
	}
    
	/**
	 * @param cell1
	 * @param cell2
	 * @param rete
	 * @return T if cell1 and cell2 are in the same column
	 */
	String sameColumn(Fact cell1, Fact cell2, Rete rete) {

		if ( isCell(cell1) && isCell(cell2) && (getColumnPosition(rete, cell1) == getColumnPosition(rete, cell2))) {
    		return "T";
    	} else {
    		return null;
        }
	}

    /**
     * @param cell1
     * @param cell2
     * @param rete
     * @return T if cell1 and cell2 are at the same row position, not necessarily in the same table. 
     */
    String sameRow(Fact cell1, Fact cell2,Rete rete) {

    	if (isCell(cell1) && isCell(cell2) && (getRowPosition(rete, cell1) == getRowPosition(rete, cell2))) {
    		return "T";
    	} else {
    		return null; 
        }
    }

    /**
     * @param cell1
     * @param cell2
     * @return true if f1 and f2 are both facts representing cells and they are the same fact
     */
    boolean sameCell(Fact cell1, Fact cell2) {

    	return ( isCell(cell1) && isCell(cell2) && (cell1.getFactId() == cell2.getFactId()) );
    }
    
    /**
     * @param cell1
     * @param cell2
     * @param rete
     * @return T if cell1 is immediately left to cell2 in the column position, not necessarily in the same table
     */
    String consecutiveColumn(Fact cell1, Fact cell2, Rete rete) {

    	if (isCell(cell1) && isCell(cell2) && (getColumnPosition(rete, cell1) +1 == getColumnPosition(rete, cell2))) {
    		return "T";
    	} else {
    		return null;
    	}
    }
    
    /**
     * @param cell1
     * @param cell2
     * @param rete
     * @return T if cell1 is immediately above cell2 in the row position, not necessarily in the same table
     */
    String consecutiveRow(Fact cell1, Fact cell2, Rete rete) {

    	if(isCell(cell1) && isCell(cell2) && (getRowPosition(rete, cell1) +1 == getRowPosition(rete, cell2))) {
    		return "T";
    	} else {
    		return null;
    	}
    }
}
