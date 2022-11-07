package edu.cmu.pact.miss.userDef.topological.fractions;

import java.util.Iterator;
import java.util.Vector;

import jess.Context;
import jess.Deftemplate;
import jess.Fact;
import jess.JessException;
import jess.Rete;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.userDef.topological.table.TableConstraint;
import edu.cmu.pact.miss.AmlRete;

public abstract class FractionConstraint extends TableConstraint  {
	
		final String FRACTION_ADDITION_TYPE="MAIN::fraction-addition"; //defines order within fraction addition.
		final String FRACTION_ADDITION_LINE_TYPE="MAIN::fraction-addition-line"; //defines order within fraction addition.
		final String FRACTION_TYPE= "MAIN::fraction" ;
		final String COLUMN_TYPE= "MAIN::column" ;
		final String TEXT_TYPE= "MAIN::textField" ;
		final String COMPLEX_FRACTION_TYPE= "MAIN::complex-fraction" ;
		final String CELL_LIST_NAME = "cells";
		final String TEXT_LIST_NAME = "textFields";
		final String TABLE_LIST_NAME = "tables";
		final String COLUMN_LIST_NAME = "columns";
		final String FRACTION_LIST_NAME = "fractions";
		final String COMPLEX_FRACTION_LIST_NAME="complex-fractions";

		/**
	     * 
	     * @param fact
	     * @return true if fact is a column, false otherwise
	     */
		protected boolean isColumn(Fact fact)
	    {
	    	return fact.getName().equals(COLUMN_TYPE);
	    }
		

		/**
	     * 
	     * @param fact
	     * @return true if fact is a fraction chunk, false otherwise
	     */
		 protected boolean isFraction(Fact fact)
		    {
		    	return fact.getName().equals(FRACTION_TYPE);
		    }
		  
		 protected boolean isComplexFraction(Fact fact)
		    {
		    	return fact.getName().equals(COMPLEX_FRACTION_TYPE);
		    }
		  
		 
		 protected boolean isFractionAdditionLine(Fact fact)
		    {
		    	return fact.getName().equals(FRACTION_ADDITION_LINE_TYPE);
		    }
		 
		 
		/**
		 * 
		 * @param fact
		 * @return true if fact is a fraction-addition chunk, false otherwise
		 */
		 protected boolean isFractionAddition(Fact fact)
		    {
		    	return fact.getName().equals(FRACTION_ADDITION_TYPE);
		    }
	
		 
		 
		/**
		 * this method assumes fact is a valid comm cell
		 * @param rete
		 * @param fact
		 * @return the column fact that "points" to the cell represented by input fact
		 * @throws JessException
		 */
		protected Fact getParentColumn(Fact cellFact, Rete rete) throws JessException{
			
			Iterator iter=rete.listFacts();
			
			while(iter.hasNext())
	    	{
				Fact curFact=(Fact)iter.next();
	    			    		
	    		if (isColumn(curFact)){
	    			Value cellsValues=curFact.getSlotValue(CELL_LIST_NAME);    		
	    			ValueVector cells=cellsValues.listValue(rete.getGlobalContext());

	    			for (int i=0;i<cells.size();i++){
	    				Fact tmpCell=cells.get(i).factValue(rete.getGlobalContext());
	    					if (tmpCell.getSlotValue("name").equals(cellFact.getSlotValue("name")))
	    						return curFact;			
	    			}
	    		}
	    	}	

	    		return null;
		}
		
		
		protected Fact getParentComplexFractionFromText(Fact cellFact, Rete rete) throws JessException{
			
			Iterator iter=rete.listFacts();
			
			while(iter.hasNext())
	    	{
				Fact curFact=(Fact)iter.next();
	    			    		
	    		if (isComplexFraction(curFact)){
	    			Value cellsValues=curFact.getSlotValue(TEXT_LIST_NAME);    		
	    			ValueVector cells=cellsValues.listValue(rete.getGlobalContext());

	    			for (int i=0;i<cells.size();i++){
	    				Fact tmpCell=cells.get(i).factValue(rete.getGlobalContext());
	    					if (tmpCell.getSlotValue("name").equals(cellFact.getSlotValue("name")))
	    						return curFact;			
	    			}
	    		}
	    	}	

	    		return null;
		}


		
		/**
		 * this method assumes fact is a column
		 * @param rete
		 * @param fact
		 * @return the table fact that "points" to the column represented by input fact
		 * @throws JessException
		 */	
		protected Fact getParentTable(Fact columnFact, Rete rete) throws JessException{
			
			if (columnFact==null) return null;
		
			Iterator iter=rete.listFacts();
			
			while(iter.hasNext())
	    	{
	    		Fact curFact=(Fact)iter.next();
	    			    		
	    		if (isTable(curFact)){
	    			Value colValues=curFact.getSlotValue(COLUMN_LIST_NAME);    		
	    			ValueVector cols=colValues.listValue(rete.getGlobalContext());
	    			
	    			for (int i=0;i<cols.size();i++){
	    				Fact tmpCol=cols.get(i).factValue(rete.getGlobalContext());
	    					if (tmpCol.getSlotValue("name").equals(columnFact.getSlotValue("name"))){
	    						return curFact;			
	    					}
	    			}
	    		}
	    		//trace.out("ok, all cheks are complete, moving on....");
	    	}	
	    			
			//trace.out("perase!!");
	    		return null;
		}
		
		/**
		 * this method assumes fact is a comm table
		 * @param rete
		 * @param fact
		 * @return the fraction chunk fact that "points" to the table represented by input fact
		 * @throws JessException
		 */	
		protected Fact getParentFraction(Fact tableFact, Rete rete) throws JessException{
			
			Iterator iter=rete.listFacts();
			
			while(iter.hasNext())
	    	{
	    		Fact curFact=(Fact)iter.next();
	    			    		
	    		if (isFraction(curFact)){
	    			Value tableValues=curFact.getSlotValue(TABLE_LIST_NAME);    		
	    			ValueVector tables=tableValues.listValue(rete.getGlobalContext());
	    			
	    			for (int i=0;i<tables.size();i++){
	    				Fact tmpTable=tables.get(i).factValue(rete.getGlobalContext());
	    					if (tmpTable.getSlotValue("name").equals(tableFact.getSlotValue("name")))
	    						return curFact;			
	    			}
	    		}
	    	}	
	    			
	
	    		return null;
		}
		
			protected Fact getParentComplexFraction(Fact tableFact, Rete rete) throws JessException{
			
			Iterator iter=rete.listFacts();
			
			while(iter.hasNext())
	    	{
	    		Fact curFact=(Fact)iter.next();
	    			    		
	    		if (isComplexFraction(curFact)){
	    			Value tableValues=curFact.getSlotValue(FRACTION_LIST_NAME);    		
	    			ValueVector tables=tableValues.listValue(rete.getGlobalContext());
	    			
	    			for (int i=0;i<tables.size();i++){
	    				Fact tmpTable=tables.get(i).factValue(rete.getGlobalContext());
	    					if (tmpTable.getSlotValue("name").equals(tableFact.getSlotValue("name")))
	    						return curFact;			
	    			}
	    		}
	    	}	
	    			
	
	    		return null;
		}
		
		
		/**
		 * this method assumes fact is a fraction chunk fact
		 * @param rete
		 * @param fact
		 * @return the position of the fraction chunk fact in the fraction addition (0 if its the 1st fraction, 1 if its the second). -1 is returned if fraction fact does not belong to a fraction-addition chunk
		 * @throws JessException
		 */	
		protected int getOrderFractionAddition(Fact fractionFact, Rete rete) throws JessException{			
			Iterator iter=rete.listFacts();
			
			while(iter.hasNext())
	    	{
	    		Fact curFact=(Fact)iter.next();
	    			    		
	    		if (isFractionAddition(curFact)){
	    			Value fractionValues=curFact.getSlotValue(COMPLEX_FRACTION_LIST_NAME);    		
	    			ValueVector fractions=fractionValues.listValue(rete.getGlobalContext());
	    			for (int i=0;i<fractions.size();i++){
	    				Fact tmpFraction=fractions.get(i).factValue(rete.getGlobalContext());
	    					if (tmpFraction.getSlotValue("name").equals(fractionFact.getSlotValue("name")))
	    						return i;			
	    			}
	    		}
	    	}	
	    			
	
	    		return -1;
		}

		
		/**
		 * this method assumes fact is a valid comm cell 
	     * @param rete
	     * @param fact
	     * @return the position of the fraction chunk fact in the fraction addition (0 if its the 1st fraction, 1 if its the second). -1 is returned if fraction fact does not belong to a fraction-addition chunk
	     */
		 private int getFractionPosition(Rete rete, Fact cellFact) throws JessException {
						
			 Fact complexFractionFact;
			
			 	if (cellFact.getName().contains("textField")){
			 		complexFractionFact=this.getParentComplexFractionFromText(cellFact, rete);
			 	}
			 	else{
			 		Fact columnFact=getParentColumn(cellFact, rete);
				
					if (columnFact==null) return  -1;
				
					Fact tableFact=getParentTable(columnFact, rete);
		   
		    		Fact fractionFact=getParentFraction(tableFact, rete);  
		    	
		    		complexFractionFact=getParentComplexFraction(fractionFact, rete);
			 	}
		 
		    	return getOrderFractionAddition(complexFractionFact, rete);
		 }
		 
		
		 private Fact getComplexFractionFromCell(Fact cellFact, Rete rete) throws JessException{
			 
			 	Fact columnFact=getParentColumn(cellFact, rete);
			
				Fact tableFact=getParentTable(columnFact, rete);
	   
	    		Fact fractionFact=getParentFraction(tableFact, rete);  
	    	
	    		Fact complexFraction= getParentComplexFraction(fractionFact, rete);
	    		
				return complexFraction;
			 
			 
		 }
		 
		 
		 protected String samePosition( Fact f1, Fact f2,Rete rete) throws JessException {  
			 
				Value f1Name=f1.getSlotValue("name");
			 	Value f2Name=f2.getSlotValue("name");
			 //	trace.out("**** samePosition : " + f1Name + " and " + f2Name + "");
			 	
			 	
			 	// trace.out(getFactParentRecursive(f1,rete));
			 	int f1_position=getFractionPosition( rete,f1 );
			 	int f2_position=getFractionPosition( rete,f2 );
			 	//Value f1Name=f1.getSlotValue("name");
			 	//Value f2Name=f2.getSlotValue("name");
			 	//trace.out("****" + f1Name + " is in " + f1_position + " and " + f2Name + " is in " + f2_position );
			 	
			    return (f1_position == f2_position && f1_position!=-1 &&  f2_position!=-1) ? "T" : null;
		 }
		 
		 
		 
		
		 
		 protected String getMultislotName(Fact fact){
			 String ret=fact.getName();
			 String ret2=ret.replace("MAIN::", "")+"s";
			 return ret2;
		 }

			
		 
	
		
			protected boolean isNumerator(Rete rete, Fact f1){
				
				int row=0;
				try {
					row = getRowPosition(rete, f1);
				} catch (JessException e) {
					
					e.printStackTrace();
				}
				return (row==1) ? true : false;
			}
			
			
			protected boolean isDenominator(Rete rete, Fact f1){	
				return !(isNumerator(rete,f1));	
			}
			
			
			
			
			protected boolean areBothNumerators(Fact f1, Fact f2, Rete rete) throws JessException{
				
				if (isCell(f1) && isCell(f2) ){
			 		int f1pos=this.getFractionPosition(rete, f1);
			 		int f2pos=this.getFractionPosition(rete, f2);
			 			if (f1pos==f2pos && f1pos==0){
			 			
			 				/*boolean is1den=isDenominator(rete,f1);
			 				boolean is2den=isDenominator(rete,f2);

			 				if (is1den && is2den) 
			 					trace.out("" + f1Name + " and " + f2Name + " are both denominators of 1st fraction");
			 				*/
			 				
			 				return (isNumerator(rete,f1) && isNumerator(rete,f2)) ? true : false;
			 				
			 			}
					}
			 	
				 return false;
			 		
				
			}
			
			/* define which complex fractions are in which row in the fraction addition interface
			 @param rete
			 * @param fact
			 * @return the row number, -1 in case something went wrong

			 * */
			private int getRowNumber(Fact f1) throws JessException{
				
				Value name=f1.getSlotValue("name");
				String nameString=name.toString();
				
				String fractionID=nameString.substring(nameString.length()-1);
				int id=Integer.parseInt(fractionID);
				
				/*if (id<=2) return 0;
				else if (id==3 || id==4) return 1;
				else if (id==5 || id==6) return 2;
				else if (id==7 || id==8)	return 3;
				*/
				return id;				
				
				//return -1;
			}
			
			
			
			
			/* 
			 * this recursively retracts the parent of a fact. It recurses until it finds the fact with the name "factToStop". 
			 * @param rete
			 * @param fact
			 * @return the parent fact

			 *  
			 * */
			protected Fact getFactParent(Fact f1,Rete rete,String factToStop) throws JessException{
				
			if (f1==null) return null;
			if (isFractionAdditionLine(f1)) return f1;
		
					Fact f=getFactParent(f1,rete);
						if (f==null) return null;
					if (f.getName().equals(factToStop)){
					//	trace.out("			we got: " + f.getSlotValue("name") + " exiting...");
						return f;
					}
					else {
						//trace.out("			we got: " + f.getSlotValue("name") + " recusing...");
						return getFactParent(f,rete,factToStop);
					}
			
					
				
				
			}
			
			/*this checks if two facts are in the same row in the new fraction addition interface
			 * @param fact2
			 * @param fact1
			 * @param rete
			 * @return true if they are on the same row, false otherwise

			 * */
			protected boolean isSameRow(Fact f1, Fact f2, Rete rete) throws JessException{
				
				Value f1Name=f1.getSlotValue("name");
			 	Value f2Name=f2.getSlotValue("name");
			 	trace.out("**** isSameRow : " + f1Name + " and " + f2Name + "");
			 	
			 	
				
				Fact fact1CF=getFactParent(f1,rete,this.FRACTION_ADDITION_LINE_TYPE);
				Fact fact2CF=getFactParent(f2,rete,this.FRACTION_ADDITION_LINE_TYPE);
				
				
				
				
				int complexFraction1Row=getRowNumber(fact1CF);
				int complexFraction2Row=getRowNumber(fact2CF);
				
				return (complexFraction1Row==complexFraction2Row? true : false);

			}
				
			
			/*	this checks if two facts are in consecutive rows in the new fraction addition interface 
			 * @param fact2
			 * @param fact1
			 * @param rete
			 * @return true if they are on consecutive rows same row, false otherwise 
			 * */
			protected boolean isConsecuitiveRow(Fact f1, Fact f2, Rete rete) throws JessException{
				
				Fact fact1CF=getFactParent(f1,rete,this.FRACTION_ADDITION_LINE_TYPE);
				Fact fact2CF=getFactParent(f2,rete,this.FRACTION_ADDITION_LINE_TYPE);
				
				
				
				if (fact1CF==null || fact2CF==null) return false; 
					
				int complexFraction1Row=getRowNumber(fact1CF);
				int complexFraction2Row=getRowNumber(fact2CF);
				
				
				trace.out("****Checking: "+ f1.getSlotValue("name") + " --> " + fact1CF.getSlotValue("name") + " and "+ f2.getSlotValue("name")+" -->  " + fact2CF.getSlotValue("name"));
				
				
				if (complexFraction1Row+1==complexFraction2Row)
					trace.out("**** 		CONSECUTIVE ROW DETECTED!");
					
			//	trace.out(fact1CF.getSlotValue("name") + " is on row " + complexFraction1Row + " and " + fact2CF.getSlotValue("name") + " is on row " + complexFraction2Row);
				
				
				//trace.out("miss", "***row : " + complexFraction1Row + " and " + complexFraction2Row);
				
				return (((complexFraction1Row+1==complexFraction2Row))? true : false);

			}
				
			
			
			/* returns if current fact is parent of fact testFact).
			 * 
			 * */
			protected boolean isParent(Fact currentFact, Fact testFact, Rete rete) throws JessException{
					
				String multislotName=getMultislotName(testFact);
				Deftemplate template=currentFact.getDeftemplate();
				int numberSlots=template.getNSlots();
						for (int i=0;i<numberSlots;i++){
								
							if (template.getSlotName(i).equals(multislotName)){
								return true;
							}
									//	trace.out("" + testFact.getName() + " has parent " + currentFact.getName());
							
						}
					
					return false;		
				
			}
			
			
			// low level synartisi pou nakei traverse ola ta facts kai dinei ton parent. genika.	
			protected Fact getFactParent(Fact fact, Rete rete) throws JessException{
				

				Iterator iter=rete.listFacts();
				
				while(iter.hasNext())
		    	{
					Fact curFact=(Fact)iter.next();
		    			  		
		    		if (isParent(curFact,fact,rete)){
		    			// yes, this template is a parent template, now check if it matches...
		    				//trace.out("Checking if " +  curFact + " is parent of " + fact.getSlotValue("name"));
		    				
		    				Value tableValues=curFact.getSlotValue(getMultislotName(fact));    
		    				
			    			ValueVector tables=tableValues.listValue(rete.getGlobalContext());
			    			
			    			for (int i=0;i<tables.size();i++){
			    				Fact tmpTable=tables.get(i).factValue(rete.getGlobalContext());
			    					
			    					if (tmpTable.getSlotValue("name").equals(fact.getSlotValue("name"))){
			    						//trace.out("FOUND: " + tmpTable.getSlotValue("name") + "="+fact.getSlotValue("name"));
			    						return curFact;			
			    					}
			    			}
		    				 			
		    				//return curFact;//trace.out("parent is " + curFact.getName());
		    			
		    		}
				  
		    	}	
		    		return null;
			}
			
			
			/*Checks if they are denominators of first fraction.*/
			 protected boolean areBothDenominators( Fact f1, Fact f2, Rete rete) throws JessException {  
				 	 	
				 	if (isCell(f1) && isCell(f2) ){
				 		int f1pos=this.getFractionPosition(rete, f1);
				 		int f2pos=this.getFractionPosition(rete, f2);
				 			if (f1pos==f2pos && f1pos==0){
				 			
				 				/*boolean is1den=isDenominator(rete,f1);
				 				boolean is2den=isDenominator(rete,f2);

				 				if (is1den && is2den) 
				 					trace.out("" + f1Name + " and " + f2Name + " are both denominators of 1st fraction");
				 				*/
				 				
				 				return (isDenominator(rete,f1) && isDenominator(rete,f2)) ? true : false;
				 				
				 			}
				 		
				 	}
				 	
				 return false;
			 }
			 
			 
			 
			 
		
					

		
		 
		 
}
