package edu.cmu.pact.miss.userDef.topological.table;

import java.util.Iterator;
import java.util.Vector;

import jess.Context;
import jess.Fact;
import jess.JessException;
import jess.Rete;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.WMEConstraintPredicate;

/**
 * This class is the parent class for topological constraints specified on a comm table
 * @author ajzana
 *
 */
public abstract class TableConstraint extends WMEConstraintPredicate {

	//list of WME constants
	final String CELL_TYPE= "MAIN::cell" ;
	final String COLUMN_TYPE="MAIN::column" ;
	final String COLUMN_INDEX_SLOT="column-number";
	final String ROW_INDEX_SLOT="row-number";
	final String TABLE_INDEX_SLOT="table-number";
	final String TABLE_TYPE="MAIN::table";
	final String COLUMN_LIST_NAME="columns";
	final String CELL_LIST_NAME="cells";

	/**
	 * 
	 */
	public String isConsecutiveTextField(Fact f1, Fact f2) throws JessException{
		
		if(trace.getDebugCode("miss"))trace.out("miss", "f1.getName(): " + f1.getName() + "  f2.getName(): " + f2.getName());
		
		if(f1.toString().contains("commTextField1") && f2.toString().contains("commTextField2"))
			return "T";
		else if(f1.toString().contains("commTextField2") && f2.toString().contains("commTextField3"))
			return "T";
		else if(f1.toString().contains("commTextField3") && f2.toString().contains("commTextField4"))
			return "T";
		else if(f1.toString().contains("commTextField4") && f2.toString().contains("commTextField5"))
			return "T";

		if(trace.getDebugCode("miss"))trace.out("miss", "isConsecutiveTextField returning null");
		return null;
	}

	/**
     * N.B. This method only checks column position, not whether f1 and f2 are in the same column in the same table
     * @param f1
     * @param f2
     * @param rete
     * @return true if f1 and f2 are table cells and have the same column position, false otherwise
     * @throws JessException
     */

	protected String sameColumn( Fact f1, Fact f2,Rete rete) throws JessException {


    	if(isCell(f1)&&isCell(f2))
    	{	
    		if( getColumnPosition( rete,f1 ) == getColumnPosition( rete,f2 ))
    			return "T";
    	}
    	
    		return null;
    		
        }
    protected String consecutiveColumn( Fact f1, Fact f2,Rete rete ) throws JessException {

    	if(isCell(f1)&&isCell(f2))
    	{
    	
    	if(getColumnPosition( rete,f1 ) +1 == getColumnPosition( rete,f2 ))
    		return "T";
    	
    	}
    	return null;
        }
    /**
     * N.B. This method only checks row position, not whether f1 and f2 are in the same row in the same table
     * @param f1
     * @param f2
     * @param rete
     * @return true if f1 and f2 are table cells and have the same row position, false otherwise
     * @throws JessException
     */
    protected String sameRow( Fact f1, Fact f2,Rete rete ) throws JessException {

    	if(isCell(f1)&&isCell(f2))
    	{
    		if(getRowPosition(rete, f1 ) == getRowPosition(rete, f2 ))
    			return "T";
    	}
    	return null; 
        }

	/**
	 * 
	 * @param f1
	 * @param f2
	 * @param rete
	 * @return return "T" if f1 and f2 are cells and both in the same table, null otherwise
	 */
	protected String sameTable(Fact f1, Fact f2, Rete rete) throws JessException
	{
		if(isCell(f1)&&isCell(f2))
		{
			Vector tables=findAllTables(rete);
			Iterator tableIter=tables.iterator();
			while(tableIter.hasNext())
			{
				Fact curTable=(Fact)tableIter.next();
				if(isInTable(f1,curTable,rete) && isInTable(f2,curTable,rete))
					return "T";
			}
		}
		return null;
	}
	
    protected String consecutiveRow( Fact f1, Fact f2,Rete rete ) throws JessException {

    	if(isCell(f1)&&isCell(f2))
    	{
    	
    	if( getRowPosition(rete, f1 ) +1 == getRowPosition(rete, f2 ))
    			return "T";
    	}
    	return null;
        }
    
    protected String consecutiveTable( Fact f1, Fact f2,Rete rete ) throws JessException {

    	if(isCell(f1)&& hasTableNumber(f1)&&isCell(f2)&& hasTableNumber(f2))
    	{
    		if( getTablePosition(rete, f1 ) +1 == getTablePosition(rete, f2 ))
    		{
    			trace.out(5, "Reurning true!");
    				return "T";
    		}
    		else
    			trace.out(5, "Tables not consecutive: " + getTablePosition(rete, f1 ) +
    					" "+ getTablePosition(rete, f2 )+ "\n");
    	}
    	else {
    		if(trace.getDebugCode("rr"))
    			trace.out(5, "One arg not a table-numbered cell" +f1 + " or " + f2+ "\n");
    	}
    	return null;
        }
    
    /* HELPER METHODS*/
    
    /**
     * @param fact
     * @return return true if fact is a table cell, false otherwise
     */
    protected boolean isCell(Fact fact)
    {
    	return fact.getName().equals(CELL_TYPE);
    }
    
    
	/**
	 * this method assumes fact is a valid comm cell
	 * @param rete
	 * @param fact
	 * @return the column position of the cell represented by fact
	 * @throws JessException
	 */
	private int getColumnPosition(Rete rete,Fact fact) throws JessException
	{
		Value position=fact.getSlotValue(COLUMN_INDEX_SLOT);
		
		Context c=rete.getGlobalContext();
		return position.intValue(c);
		

		
		
	}
    /**
     * Returns a number of row of the given fact assuming that it is
     * in a table.  
     *
     * @param fact a <code>Fact</code> value
     * @return an <code>int</code> value which is the one-based row position of the fact in the table 
     **/

	protected int getRowPosition(Rete rete, Fact fact) throws JessException {
		return fact.getSlotValue(ROW_INDEX_SLOT).intValue(rete.getGlobalContext());

	}
	 /**
     * Returns the table number  of the given fact assuming that it is
     * a numbered table.  
     *
     * @param fact a <code>Fact</code> value
     * @return an <code>int</code> value which is the one-based table number
     **/

	private int getTablePosition(Rete rete, Fact fact) throws JessException {
		return fact.getSlotValue(TABLE_INDEX_SLOT).intValue(rete.getGlobalContext());

	}
    /**
     * 
     * @param fact
     * @return true if fact is a table, false otherwise
     */
	protected boolean isTable(Fact fact)
    {
    	return fact.getName().equals(TABLE_TYPE);
    }
	/**
     * 
     * @param fact
     * @return true if fact contains a table number slot, 
     * false otherwise
     */
	protected boolean hasTableNumber(Fact fact)
    {
    	return(fact.getDeftemplate().getSlotIndex(TABLE_INDEX_SLOT)>-1);
    }
    /**
     * 
     * @param f1
     * @param f2
     * @return return true if f1 and f2 are both facts representing cells and they are the same fact
     */
    protected boolean sameCell(Fact f1, Fact f2)
    {
    	if(isCell(f1)&& isCell(f2))
    	{
    		return f1.getFactId()==f2.getFactId();
    	}
    	return false;
    }
    /**
     * 
     * @param cellFact a Fact representing a commCell
     * @param tableFact a Fact representing an commTable
     * @param Rete rete
     * @return true if cellFact is in the table represented by tableFact 
     */
    protected boolean isInTable(Fact cellFact, Fact tableFact,Rete rete) throws JessException
    {
    	if(isCell(cellFact)&& isTable(tableFact))
    	{
    		Value columnValue=tableFact.getSlotValue(COLUMN_LIST_NAME);
    		if(columnValue!=null)
    		{
    			ValueVector columns=columnValue.listValue(rete.getGlobalContext());
    			//loop through the columns of the table
    			for(int colnum=0; colnum<columns.size(); colnum++)
    			{
    				Fact curColumn=columns.get(colnum).factValue(rete.getGlobalContext());
    				Value cellValue=curColumn.getSlotValue(CELL_LIST_NAME);
    				if(cellValue!=null)
    				{
    					ValueVector cells=cellValue.listValue(rete.getGlobalContext());
    					//loop through the cells in the column
    					for(int cellnum=0; cellnum<cells.size(); cellnum++)
    					{
    						Fact curCell=cells.get(cellnum).factValue(rete.getGlobalContext());
    						if(sameCell(cellFact,curCell))
    							return true;
    					}
    				}
    				
    			}
    		}
    	}
    	return false;
    }
    /**
     * 
     * @param rete
     * @return a Vector of all tables in the working memory for rete, returns an empty vector if there are no tables
     */
    protected Vector /*of Facts*/ findAllTables(Rete rete)
    {
    	Vector v=new Vector();
    	Iterator iter=rete.listFacts();
    	while(iter.hasNext())
    	{
    		Fact curFact=(Fact)iter.next();
    		if(isTable(curFact))
    			v.add(curFact);
    		
    	}
    	return v;
    }
    
}
