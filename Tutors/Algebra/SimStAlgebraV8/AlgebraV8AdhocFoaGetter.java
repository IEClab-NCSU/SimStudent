package SimStAlgebraV8;

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
import pact.CommWidgets.JCommComboBox;
import pact.CommWidgets.JCommTable.TableExpressionCell;
import pact.CommWidgets.JCommTextField;
import jess.ValueVector;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.miss.Sai;
import java.util.List;
import edu.cmu.pact.miss.jess.WorkingMemoryConstants;

public class AlgebraV8AdhocFoaGetter extends FoaGetter {

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

		//trace.out("ss", "$$$$$$$$$$ Selection: "+selection+" Action: "+action+" Input: "+input);
		//Done Button Pressed- FoA is the first two columns of the last row with info
		if(selection.equalsIgnoreCase("done"))
		{
			//Start at last row and go backwards
			for(int i=5;i>=0;i--)
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
		}
		
        char c = selection.charAt(DORMIN_TABLE_STEM.length());
        int col = c - '1' +1;
		
        int rIdx = selection.indexOf("R");
        char r = selection.charAt(rIdx +1);
        int row = r - '1' +1;

		String operand = null;
		String skill = null;
		int operandIdx = input.indexOf(' ');

		if (operandIdx > 0) {
			skill = input.substring(0, operandIdx);
			operand = input.substring(operandIdx +1);
			// Thu Dec 06 17:29:45 2007 :: Noboru
			// When the operand has a negative sign, it does not find FoA
			// -65 = 39+2x [add -39] 
			// "-39" does not match with RHS
			// Thu Feb 07 09:25:02 2008:: Noboru
			// Do this only when the skill add
			// -2 = -x [divide -1] has nothing to do with this
			if (skill.equalsIgnoreCase("add") && operand.charAt(0) == '-') {
				operand = operand.substring(1);
			}
		}

		if ( col == 3 ) {
			// The "selection" is "Skill Operand"
			if (operand != null) {
				// The "Operand" specified
				algebraI_AdhocFoa_skill(brController, vFoa, row, operand);
			} else {
				// The "Skill" does not take an "operand". E.g., clt
				vFoa.add(getTableCell(brController, row, 1));
				vFoa.add(getTableCell(brController, row, 2));
			}
		} else {
			if (row==1){
				vFoa.add(getTableCell(brController, row, col));
				vFoa.add(getTableCell(brController, row, 3));
			}
			else{
				vFoa.add(getTableCell(brController, row -1, col));
				vFoa.add(getTableCell(brController, row -1, 3));
			}
		}
		return vFoa;
	}
	
	static final String COLNUM = "1";
	static final String ROWNUM = "Y";
	static final String TABNUM = "X";
	static final String DORMIN_TABLE_STEM = "dorminTable";
	static final String DORMIN_TABLE_NAME = DORMIN_TABLE_STEM + TABNUM + "_C" + COLNUM + "R" + ROWNUM;
	
	private TableExpressionCell getTableCell(BR_Controller brController, int row, int column) {
		
		// dorminTable0_CxRy
		String cellName = DORMIN_TABLE_NAME.replaceAll(TABNUM, ""+column);
		cellName = cellName.replaceAll(ROWNUM, ""+row);

		TableExpressionCell cell = (TableExpressionCell)brController.lookupWidgetByName( cellName );
		
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
				vFoa.add(foaCell_1);
				vFoa.add(foaCell_2);
			}
		}
	}
	
	public String currentStepDescription(Vector foas, String ruleName, Sai sai)
   {
	   
   
   	
   	   	
	if(foas == null || sai == null)
   		return null;
   	
   	String foaDesc="";
   		
	for (int i=0;i<foas.size();i++){
		String foaInput="";
		String foaName="";
					
		if (foas.elementAt(i) instanceof JCommComboBox ){
			foaInput = (String) ((JCommComboBox)foas.elementAt(i)).getValue();
			foaName = (String) ((JCommComboBox)foas.elementAt(i)).getCommName();
		}
		else if (foas.elementAt(i) instanceof JCommTextField  ){
			foaInput = ((JCommTextField )foas.elementAt(i)).getText();
			foaName = (String) ((JCommTextField)foas.elementAt(i)).getCommName();
		}
		else {
			foaInput = ((TableExpressionCell)foas.elementAt(i)).getText();
			foaName = (String) ((TableExpressionCell)foas.elementAt(i)).getCommName();
		}

		foaDesc = foaDesc + " <font color=\"red\">" + foaInput + "</font> and";
	}
  

	if (foaDesc.length()>3)
		foaDesc=foaDesc.substring(0, foaDesc.length()-3);
   
      	
  	return foaDesc;
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
		
			newStr=" when I had <font color=blue>"+foa1+"="+foa2+"</font>, I did the transformation <font color=blue>"+str+"</font>";
		
		//	str += " for '"+foa1+"="+foa2+"'";
			
			
			
		}
		else
		{
			//str += " for the result of '"+foa2+"' and '"+foa1+"'";
			newStr=" I did the transformation <font color=blue>"+foa2+"</font> when I had <font color=blue>"+foa1+"</font> and got <font color=blue>"+str+"</font>";
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
	
	public static final String TYPE_TRANSFORMATION="transformation";
	public static final String TYPE_TYPEIN="type-in";
	
	
	/**
	* Method to return the type of step (i.e. transformation or type-in)
	*
	*/
	 public String getTypeOfStep(String selection,BR_Controller brController){
	 	String returnValue="";
	 
		if (selection==null || selection.equalsIgnoreCase("done")) returnValue=TYPE_TRANSFORMATION;
		else{
    	char c = selection.charAt(DORMIN_TABLE_STEM.length());
		int col = c - '1' + 1;
		
		int rIdx = selection.indexOf("R");
        char r = selection.charAt(rIdx +1);
        int row = r - '1' +1;
		
		
		if (col==3) //its about trasnformation
			returnValue=TYPE_TRANSFORMATION;
		else{
			returnValue=TYPE_TYPEIN;
			//its about typein
		 	//returnValue=getTableCell(brController,row-1,3).getText();
		}
		}
		return returnValue;	
    } 		
	
	
	
	public String formulateBasedOnTypeOfString(String selection, BR_Controller brController, String message){
	
	
		String newMessage="";
		String typeOfStep = getTypeOfStep(selection,brController);
		
			if (typeOfStep.equals(TYPE_TYPEIN)){		/*when skill is present (i.e. type-in), get second part (i.e. "How do I bla")*/ 
				String skill=getStepSkill(selection,brController);
				newMessage=message.replace("<reasoning>", "How do I " + skill);
			}
			else{						 /*when skill is not present, get the first part (i.e. "What's the next step")*/ 
				newMessage=message.replace("<reasoning>","What's the next step");
			}	
	
	return newMessage;
	}
	
	
	
	/**
	* Method to return the skill required for a step
	*
	*/
	 public String getStepSkill(String selection,BR_Controller brController){
	 	String returnValue="";
	 
		if (selection==null || selection.equalsIgnoreCase("done")) returnValue=TYPE_TRANSFORMATION;
		else{
    	
		int rIdx = selection.indexOf("R");
        char r = selection.charAt(rIdx +1);
        int row = r - '1' +1;
        
	 	returnValue=getTableCell(brController,row-1,3).getText();
		
		}
		return returnValue;
		
    } 		

	 public String getMTHintMessageOnFeedback(String msg, BR_Controller brController) {	
		String returnValue="";	
		StringBuilder sb = new StringBuilder();
		ProblemNode currentNode = brController.getCurrentNode();
		List<ProblemEdge> edges = currentNode.getIncomingEdges();	
		if(edges.size() >= 1) {
			ProblemEdge edge = edges.get(0);		
			Sai sai = edge.getSai();	
			Vector<String> foas=new Vector();	
			Vector focusOfAttn = foaGetter(brController,sai.getS(),sai.getA(),sai.getI(), null);
			for (int i=0;i<focusOfAttn.size();i++){
			String foa="";
				if (focusOfAttn.elementAt(i) instanceof JCommComboBox )
					foa = (String) ((JCommComboBox)focusOfAttn.elementAt(i)).getValue();
				else if (focusOfAttn.elementAt(i) instanceof JCommTextField  )
					foa = ((JCommTextField )focusOfAttn.elementAt(0)).getText();
				else 
					foa = ((TableExpressionCell)focusOfAttn.elementAt(i)).getText();
				foas.add(foa);
			
			}
		
			if(msg.trim().equals("TRUE")) {				
				if(sai.getS().equalsIgnoreCase("done"))
						returnValue="Saying the problem is solved is correct here.";
				else if (getTypeOfStep(sai.getS(),brController).equals(TYPE_TRANSFORMATION)){	
							returnValue="Yes, " + sai.getI() + " would be the correct transformation for " + foas.get(0) + "="+foas.get(1);
				}
				else{
							int transformationID=0;
							int typeinID=0;
							if (isFoaTransformation(foas.get(0))) {	transformationID=0; typeinID=1;	}
							else{ transformationID=1; typeinID=0;	}
							returnValue="Yes, applying the transformation " + foas.get(transformationID) + " to " + foas.get(typeinID) + " gives the result of " + sai.getI();
				}		
			} else if(msg.trim().equals("FALSE")) {		
					if(sai.getS().equalsIgnoreCase("done"))
						returnValue="Saying the problem is solved would not be correct here.";
					else
						returnValue="No, " + sai.getI() + " would not be the right thing to do.";				
			}		
		}
		return returnValue;
	}
	
	
	/*
	 * Returns the foa term which is most relevant to the operand of the last transformation 
	 * @see edu.cmu.pact.miss.FoaGetter#relevantFoaString(String, Vector)
	 */
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
		if(result == null && usedFoas.size() > 0)
			return usedFoas.get(0);
		return result;
	}
	
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
	
	private boolean isFoaTransformation(String input){
	 	return input.contains(" ");
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
