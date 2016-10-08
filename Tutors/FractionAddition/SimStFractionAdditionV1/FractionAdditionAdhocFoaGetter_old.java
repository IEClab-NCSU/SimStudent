package SimStFractionAdditionV1;

import java.util.Vector;

import javax.swing.JOptionPane;

import pact.CommWidgets.JCommTable;
import pact.CommWidgets.JCommWidget;
import pact.CommWidgets.JCommTable.TableCell;
import pact.CommWidgets.JCommTable.TableExpressionCell;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.miss.FoaGetter;
import edu.cmu.pact.miss.Instruction;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.ParseExp;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExpParser;
import edu.cmu.pact.miss.userDef.algebra.expression.ComplexTerm;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;
import edu.cmu.pact.miss.userDef.algebra.expression.Polynomial;
import pact.CommWidgets.JCommTextField;
public class FractionAdditionAdhocFoaGetter extends FoaGetter {

	/**
	 * Task specific ad-hoc method to identify focus of attention
	 * This is for emurating the Algebra I tutor with a 3-column table 
	 * 
	 * @param selection
	 * @param action
	 * @param input
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Vector /* Object */ foaGetter(BR_Controller brController, 
			String selection, String action, String input, Vector dummy) {

		// selection :: dorminTable0_CxRy
	
		
		
		Vector /* Object */ vFoa = new Vector();

		trace.out("ss", "$$$$$$$$$$ Selection: "+selection+" Action: "+action+" Input: "+input);
		//Done Button Pressed- FoA is the first two columns of the last row with info
		if(selection.equalsIgnoreCase("done"))
		{
			//Start at last row and go backwards
		/*	for(int i=5;i>=0;i--)
			{
				if(getTableCell(brController, i, 1).getText().length() > 0
						|| getTableCell(brController, i, 2).getText().length() > 0)
				{
					trace.out("ss", "Foa: "+getTableCell(brController, i, 1).getText()+","+getTableCell(brController, i, 2).getText());
					trace.out("ss", "Input: "+input);
					vFoa.add(getTableCell(brController, i, 1));
					vFoa.add(getTableCell(brController, i, 2));
					return vFoa;
				}
			}
			*/
			/*for (int i=5;i>=0;i--){
				if(getTableCell(brController, 1, i).getText().length() > 0
							|| getTableCell(brController, 2, i).getText().length() > 0){
							
					vFoa.add(getTableCell(brController, 1, 5));
					vFoa.add(getTableCell(brController, 2, 5));
					return vFoa;
				
				}
			
			}
			*/
					vFoa.add(getTableCell(brController, 1, 5));
					vFoa.add(getTableCell(brController, 2, 5));
					vFoa.add(getTableCell(brController, 1, 6));
					vFoa.add(getTableCell(brController, 2, 6));
					vFoa.add(getCommText(brController));
					return vFoa;
					
					
		}
		
		
		if (selection.contains("Text")){
			vFoa.add(getTableCell(brController, 1, 5));    	 
        	vFoa.add(getTableCell(brController, 2, 5));  
		}
		else{
		
        char c = selection.charAt(DORMIN_TABLE_STEM.length());
        int col = c - '1' +1;
		
        int rIdx = selection.indexOf("R");
        char r = selection.charAt(rIdx +1);
        int row = r - '1' +1;
        
   	
        
        //FOA sequence specific for the Fraction Addition.
       
        String denominator1=getTableCell(brController, 2, 1).getText();
     	String denominator2=getTableCell(brController, 2, 2).getText();
       
       
       
       /* Denominators of next step are estimated from the lcm of problem denominators*/
        if ((col==3  || col==4) && row==2){		
        	vFoa.add(getTableCell(brController, 2, 1));  
        	vFoa.add(getTableCell(brController, 2, 2)); 
        }
        /* First numerator can be estimated from denominator (i.e. LCM) and first fraction*/
        if (col==3 && row==1){
        	
        	/*If problem fractions have common denominators, then numerator is the addition of problem numerators so FOA's should be these two.
       		if(getTableCell(brController, 2, 1).getText()==getTableCell(brController, 2, 2).getText()){
       				vFoa.add(getTableCell(brController, 1, 1)); 
        			vFoa.add(getTableCell(brController, 1, 2));   		
       		}
        	else{*/
        		vFoa.add(getTableCell(brController, 1, 1)); 
        		vFoa.add(getTableCell(brController, 2, 1));
        		vFoa.add(getTableCell(brController, 2, 3));  //to alaksa giati den kseroume me pia seira tha dialeksei
        		//vFoa.add(getTableCell(brController, 2, 2));  
        //	}
        	
        	
        }
         /* Second numerator can be estimated from denominator (i.e. LCM) and second fraction*/
        if (col==4 && row==1){
        	vFoa.add(getTableCell(brController, 1, 2)); 
        	vFoa.add(getTableCell(brController, 2, 2));
        	vFoa.add(getTableCell(brController, 2, 4)); //to alaksa giati den kseroume me pia siera tha dialeksei
        	//vFoa.add(getTableCell(brController, 2, 1));    	
        }
        
         
       
       
    	/* Result numerator can be estimated by the two intermediate numerators (its their addition)*/
        if (col==5 && row==1){
        
        	 if (denominator1.equals(denominator2)){
        	 		//If fractions have the same denominator, then FOA is first fraction addition
       		 		vFoa.add(getTableCell(brController, 1, 1));  
        			vFoa.add(getTableCell(brController, 1, 2));
        			vFoa.add(getTableCell(brController, 2, 1));  
        			vFoa.add(getTableCell(brController, 2, 2)); 
      		
      		 }else{      		
     				//If fractions have the different denominator, then FOA is second fraction addition
     				vFoa.add(getTableCell(brController, 1, 3));   
        			vFoa.add(getTableCell(brController, 1, 4)); 
        			vFoa.add(getTableCell(brController, 2, 3));   
        			vFoa.add(getTableCell(brController, 2, 4));     		
     		}
        
  	
        }
        /* Result denominator can be estimated from the intermediate denominators (its just a copy of them)*/
        if (col==5 && row==2){      	
        	if (denominator1.equals(denominator2)){      	 
        		vFoa.add(getTableCell(brController, 2, 1));  
        		vFoa.add(getTableCell(brController, 2, 2));   
 			}
 			else{
 				vFoa.add(getTableCell(brController, 2, 3)); 
        		vFoa.add(getTableCell(brController, 2, 4)); 
 			}
		
        }
        
        
        /* Result denominator can be estimated from the intermediate denominators (its just a copy of them)*/
        if (col==6 && row==2){      	
        	vFoa.add(getTableCell(brController, 1, 5));     	 
        	vFoa.add(getTableCell(brController, 2, 5));  		
        }
        
        	if (col==6 && row==1){  
        		System.out.println("eimaste sto swsto...");
        		vFoa.add(getTableCell(brController, 1, 5));     	 
        		vFoa.add(getTableCell(brController, 2, 6));  
        		vFoa.add(getCommText(brController));
      	  }
        
        }

    //    System.out.println("FOAAAAAAA" + vFoa);
        return vFoa;
	}
	
	
	static final String COLNUM = "1";
	static final String ROWNUM = "Y";
	static final String TABNUM = "X";
	static final String DORMIN_TABLE_STEM = "dorminTable";
	static final String DORMIN_TABLE_NAME = DORMIN_TABLE_STEM + TABNUM + "_C" + COLNUM + "R" + ROWNUM;
	static final String TEXTFIELD_STEM = "commTextField";
	
	private TableExpressionCell getTableCell(BR_Controller brController, int row, int column) {
		
		// dorminTable0_CxRy
		String cellName = DORMIN_TABLE_NAME.replaceAll(TABNUM, ""+column);
		cellName = cellName.replaceAll(ROWNUM, ""+row);
		
		TableExpressionCell cell = (TableExpressionCell)brController.lookupWidgetByName( cellName );
		
		return cell;
	}
	
	private JCommTextField getCommText(BR_Controller brController) {
		
		// dorminTable0_CxRy
		
		JCommTextField cell = (JCommTextField)brController.lookupWidgetByName( TEXTFIELD_STEM );
		
		return cell;
	}
	
	
	
	private void algebraI_AdhocFoa_skill(BR_Controller brController, Vector vFoa, int row, String operand) {

		TableCell foaCell_1 = getTableCell(brController, row, 1);
		TableCell foaCell_2 = getTableCell(brController, row, 2);
		String foaValue_1 = foaCell_1.getValue().toLowerCase();
		String foaValue_2 = foaCell_2.getValue().toLowerCase();
		operand = operand.toLowerCase();

		int foaIndex_1 = foaValue_1.replaceAll("\\*", "").indexOf(operand);   
		int foaIndex_2 = foaValue_2.replaceAll("\\*", "").indexOf(operand);

		if ( foaIndex_2 >= 0 && foaIndex_2 >= foaIndex_1 ) {

			// RHS has the operand
			vFoa.add(foaCell_2);
			vFoa.add(foaCell_1);

		} else if ( foaIndex_1 >= 0 && foaIndex_1 >= foaIndex_2 ) {
			
			// LHS has the operand
			vFoa.add(foaCell_1);
			vFoa.add(foaCell_2);

		} else { //neither FOA has the operand
			// either foaCell_1 or foaCell_2 is a variable term, and 
			// the skill is something to do with its coefficient

			//assuming foaValue_1 and foaValue_2 are monomial terms
			String coefficient_1 = EqFeaturePredicate.coefficient(foaValue_1);
			String coefficient_2 = EqFeaturePredicate.coefficient(foaValue_2);

			if ( (coefficient_1 != null) && coefficient_1.equals(operand)) {
				//(EqFeaturePredicate.inputMatcher(coefficient_1, operand).equals("T"))) {
				vFoa.add(foaCell_1);
				vFoa.add(foaCell_2);

			} else if ( (coefficient_2 != null) && coefficient_2.equals(operand)) {
				//(EqFeaturePredicate.inputMatcher(coefficient_2, operand).equals("T"))) {
				vFoa.add(foaCell_2);
				vFoa.add(foaCell_1);

			} else {
				trace.out("miss", "###### No FoA identified");
				vFoa.add(foaCell_1);
				vFoa.add(foaCell_2);
			}
		}
	}
	
	
	public String foaDescription(Instruction inst)
	{
		String newStr="";
	
		if(inst.getFocusOfAttention().size() < 3)
			super.foaDescription(inst);
		String str = "";
		
		if(((String) inst.getFocusOfAttention().get(0)).contains("done"))
		{
			str = "that the problem is solved";
		}
		else
		{
			str = (String) inst.getFocusOfAttention().get(0);
		}
		str = str.substring(str.lastIndexOf('|')+1);
        String foa1 = ((String) inst.getFocusOfAttention().get(1));
		char c = foa1.charAt(foa1.indexOf(DORMIN_TABLE_STEM)+DORMIN_TABLE_STEM.length());
        int col1 = c - '1' +1;
		foa1 = foa1.substring(foa1.lastIndexOf('|')+1);

        String foa2 = ((String) inst.getFocusOfAttention().get(2));
		c = foa2.charAt(foa2.indexOf(DORMIN_TABLE_STEM)+DORMIN_TABLE_STEM.length());
        int col2 = c - '1' +1;
		foa2 = foa2.substring(foa2.lastIndexOf('|')+1);
				
		if(col1 < 3 && col2 < 3)
		{
		
			newStr=" when I had '"+foa1+"/"+foa2+"', I did the transformation '"+str+"'";
		
		//	str += " for '"+foa1+"="+foa2+"'";
			
			
			
		}
		else
		{
			//str += " for the result of '"+foa2+"' and '"+foa1+"'";
			newStr=" I did the transformation '"+foa2+"' when I had '"+foa1+"' and got '"+str+"'";
		}
		
		
		
		
		return newStr;
		//return str;
	}
	
	public String foaStepDescription(Instruction inst)
	{
		if(inst.getFocusOfAttention().size() < 3)
			super.foaDescription(inst);
		String str = "";
		
        String foa1 = ((String) inst.getFocusOfAttention().get(1));
		char c = foa1.charAt(foa1.indexOf(DORMIN_TABLE_STEM)+DORMIN_TABLE_STEM.length());
        int col1 = c - '1' +1;
		foa1 = foa1.substring(foa1.lastIndexOf('|')+1);

        String foa2 = ((String) inst.getFocusOfAttention().get(2));
		c = foa2.charAt(foa2.indexOf(DORMIN_TABLE_STEM)+DORMIN_TABLE_STEM.length());
        int col2 = c - '1' +1;
		foa2 = foa2.substring(foa2.lastIndexOf('|')+1);
				
		if(col1 < 3 && col2 < 3)
		{
			str += "'"+foa1+"="+foa2+"'";
		}
		else
		{
			str += "the result of '"+foa2+"' and '"+foa1+"'";
		}
		
		return str;
	}
	
	/*
	 * Returns the foa term which is most relevant to the operand of the last transformation 
	 * @see edu.cmu.pact.miss.FoaGetter#relevantFoaString(String, Vector)
	
	public String relevantFoaString(String input, Vector<String> foas) {
		
		String result = null;
		String relevantTo = input;
		Vector<String> usedFoas = new Vector<String>();
		if(foas.size() == 0)
			return null;

		//We want the term relevant to the last transformation.  If this is the input, use this and
		//find it from amongst the foas
		if(relevantTo.contains(" "))
		{
			relevantTo = relevantTo.substring(relevantTo.lastIndexOf(' ')+1);

			for(int i=0;i<foas.size();i++)
			{
				usedFoas.add(foas.get(i));
			}
		}
		else
		{
			//If the input is not the last transformation, the last transformation should be one of
			//the foas and the remaining foas are where the relevant term should be found
			for(int i=0;i<foas.size();i++)
			{
				if(foas.get(i).contains(" "))
				{
					input = foas.get(i);
					relevantTo = input.substring(input.lastIndexOf(' ')+1);
				}
				else
				{
					usedFoas.add(foas.get(i));
				}
			}
		}
		Vector<String> expressions = new Vector<String>();
		for(int i=0;i<usedFoas.size();i++)
		{
			expressions.addAll(parseExpressionStrings(usedFoas.get(i)));
		}
		if(input.startsWith("distribute")||input.startsWith("combine"))
		{
			//distribute and combine like terms use full side
			for(int i=0;i<foas.size();i++)
			{
				if(foas.get(i).contains(relevantTo))
				{
					result = foas.get(i);
				}
			}
		}
		else if(input.startsWith("multiply")||input.startsWith("divide")) 
		{  //Multiplication and division prefers the relevance of negatives over inclusion of variable
			result = findMatch(relevantTo, expressions, true,true);
			if(result == null)
				result = findMatch(relevantTo, expressions, false,true);
			if(result == null)
				result = findMatch(relevantTo, expressions, true,false);
			if(result == null)
				result = findMatch(relevantTo, expressions, false,false);
		}
		else
		{
			//Addition and subtraction prefer inclusion of variable over swapped negatives +default behavior
			result = findMatch(relevantTo, expressions, true,true);
			if(result == null)
				result = findMatch(relevantTo, expressions, true,false);
			if(result == null)
				result = findMatch(relevantTo, expressions, false,true);
			if(result == null)
				result = findMatch(relevantTo, expressions, false,false);
		}
				
		//If none of the terms are relevant, then it is probably a balancing move because of something done
		//on the other side that is relevant to the value.  The whole non-transformation foa is just as relevant
		//Return it all
		if(result == null && usedFoas.size() > 0)
			return usedFoas.get(0);
		return result;
	}
	 */
	/*
	 * find a string in matchIn which matches toMatch based on the conditions
	 * if matchFullText is off, a letter at the end of each matchIn is ignored, eg: 3 matches 3x, but 3x does not match 3
	 * if matchFullText is on, the variable has to be included in the match as well, 3 does not match 3x, 3 matches 3
	 * if matchNeg is off, the negative signs are removed on both sides and compared like that, eg: -3 matches 3
	 * if matchNeg is on, negative signs have to be included in the match as well.  -3 matches -3.
	 * with matchNeg and matchFullText off, -3 matches 3x.
	 */
	private String findMatch(String toMatch, Vector<String> matchIn, boolean matchFullText, boolean matchNeg)
	{
		if(!matchNeg && toMatch.startsWith("-"))
		{
			toMatch = toMatch.substring(1);
		}
		String backup = null;
		for(int i=0;i<matchIn.size();i++)
		{
			String compareTo = matchIn.get(i);
			if(!matchNeg && compareTo.startsWith("-"))
			{
				compareTo = compareTo.substring(1);
			}
			if(!matchFullText && Character.isLetter(compareTo.charAt(compareTo.length()-1)))
			{
				compareTo = compareTo.substring(0, compareTo.length()-1);
			}
			if(toMatch.equals(compareTo))
				return matchIn.get(i);
			
		}
		
		
		return null;
	}
	
	/*
	 * Parse an expression string into its parts (~terms)
	 * Items which separate parts:  +,-,(,),' '
	 * These are not included in the part itself, except -
	 * Also a part cannot start with /.  This is not included if 
	 * it would start a part.  Ex: 1/3 is a part.  (1)/3 results in two parts: 1 and 3 (not /3).
	 */
	private Vector<String> parseExpressionStrings(String poly)
	{
		String temp = "";
		Vector<String> expressions = new Vector<String>();
		
		for(int i=0;i<poly.length();i++)
		{
			char c = poly.charAt(i);
			if(c == '+' )
			{
				if(temp.length() > 0)
					expressions.add(temp);
				temp = "";
			}
			else if(c == '-' )
			{
				if(temp.length() > 0)
					expressions.add(temp);
				temp = "-";
			}
			else if(c == '(' )
			{
				if(temp.equals("-"))
					temp += "1";
				if(temp.length() > 0)
					expressions.add(temp);
				temp = "";
			}
			else if(c == ')' )
			{
				if(temp.length() > 0)
					expressions.add(temp);
				temp = "";
			}
			else if(c == ' ' )
			{
				if(temp.length() > 0)
					expressions.add(temp);
				temp = "";
			}
			else if(c == '/' )
			{
				if(temp.length() > 0)
					temp += c;
			}
			else
			{
				temp += c;
			}
		}
		
		expressions.add(temp);
		return expressions;
	}

	public Vector<String> foaGetterStrings(BR_Controller brController,String selection, String action, String input, 
            Vector edgePath)
    { 

    	Vector<Object> foas = foaGetter(brController,selection,action,input,edgePath);
		Vector<String> foaStrings = new Vector<String>();
		for(int i=0;i<foas.size();i++)
		{
			Object f = foas.get(i);
			if(f instanceof JCommWidget)
				foaStrings.add(((JCommWidget) f).getCommName());
			if(f instanceof TableCell)
				foaStrings.add(((TableCell) f).getCommName());
				
		}
		return foaStrings;
    }
	
}
