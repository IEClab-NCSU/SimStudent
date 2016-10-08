package edu.cmu.pact.miss.SimStAlgebraV8;

import edu.cmu.pact.miss.SelectionOrderGetter;

public class AlgebraV8AdhocSelectionGetter extends SelectionOrderGetter {


	   
    /**
     * Returns the name of the next selection which would occur after the given selection
     */
    public String nextSelection(String selection){

    	
    	if(selection.length() < DORMIN_TABLE_STEM.length())
    		return null;
    	if(!selection.startsWith(DORMIN_TABLE_STEM))
    		return null;
    	
        char c = selection.charAt(DORMIN_TABLE_STEM.length());
        int col = c - '1' +1;
		
        int rIdx = selection.indexOf("R");
        char r = selection.charAt(rIdx +1);
        int row = r - '1' +1;

        //If the last selection was the third column, move to the first column of the next row down
        if(col == 3)
        {
        	col = 1;
        	row++;
        }
        //Otherwise move to the next column in that row
        else
        {
        	col++;
        }
        
        String cellName = DORMIN_TABLE_NAME.replaceAll(TABNUM, ""+col);
		cellName = cellName.replaceAll(ROWNUM, ""+row);
		
    	
    	return cellName;
    }
    
    /**
     * Returns the name of the next selection which would occur after the given selection
     */
    public String nextSelection(String selection, String priorSelection){

    	
    	if(selection.length() < DORMIN_TABLE_STEM.length())
    		return null;
    	if(!selection.startsWith(DORMIN_TABLE_STEM))
    		return null;
    	
        char c = selection.charAt(DORMIN_TABLE_STEM.length());
        int col = c - '1' +1;
		
        int rIdx = selection.indexOf("R");
        char r = selection.charAt(rIdx +1);
        int row = r - '1' +1;
        
        char cP = priorSelection.charAt(DORMIN_TABLE_STEM.length());
        int colP = cP - '1' +1;
		
        //If the last selection was the third column, move to the first column of the next row down
        if(col == 3)
        {
        	col = 1;
        	row++;
        }
        //If the last selection was RHS, but the one before that was transformation, go back and do LHS
        else if(col == 2 && colP == 3)
        {
        	col = 1;
        }
        //If the last selection was LHS, but the one before that was RHS, go on to transformation
        else if(col == 1 && colP == 2)
        {
        	col = 3;
        }
        //Otherwise move to the next column in that row
        else
        {
        	col++;
        }
        
        String cellName = DORMIN_TABLE_NAME.replaceAll(TABNUM, ""+col);
		cellName = cellName.replaceAll(ROWNUM, ""+row);
		
    	
    	return cellName;
    }
    
    public String startSelection()
    {
    	String cellName = DORMIN_TABLE_NAME.replaceAll(TABNUM, ""+3);
		cellName = cellName.replaceAll(ROWNUM, ""+1);
		    	
    	return cellName;
    }
    
	
	
	static final String COLNUM = "1";
	static final String ROWNUM = "Y";
	static final String TABNUM = "X";
	static final String DORMIN_TABLE_STEM = "dorminTable";
	static final String DORMIN_TABLE_NAME = DORMIN_TABLE_STEM + TABNUM + "_C" + COLNUM + "R" + ROWNUM;
	

}

