package SimStAlgebraV8;

import java.awt.Component;

import edu.cmu.pact.miss.Sai;

import pact.CommWidgets.JCommTable;
import pact.CommWidgets.JCommTable.TableCell;
import pact.CommWidgets.JCommTable.TableExpressionCell;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.InputChecker;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.miss.InquiryClSolverTutor;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class AlgebraV8InputChecker extends InputChecker {

	private String[] validOperators = {"add", "subtract", "multiply", "divide", "combine like terms", "distribute"};
	private String[] operatorPhrasing = {"add", "subtract", "multiply by", "divide by", "combine like terms", "distribute", "combine like terms in", "combine like term", "combine", "clt"};
	private char[] validSymbols = {'+','-','*','/','(',')','.'};
	private static char[] invalidVariables = {'d', 'e', 'f', 'l', 'D', 'E', 'F', 'L'};
	private String runType;
	
	public String getRunType() {
		return runType;
	}
	
	public void setRunType(String runType) {
		this.runType = runType;
	}

	BR_Controller controller;
	
	@Override
	public boolean checkInput(String selection, String input, String[] foa, BR_Controller brController) {
			if (brController!=null) controller=brController;
			
		
			return checkInput(selection,input);
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
		
		if (brController.getMissController().getSimSt().isSsAplusCtrlCogTutorMode()){
			
			cell=brController.getMissController().getSimStPLE().lookUpWidgetQuiz(cellName);
		}
		return cell;
	}
	
	
	/*
	*
	*
	*/	
	public boolean checkSkipStep(String selection){
		boolean returnValue=true;
		if (controller==null) return true;	
		
		if (controller.getMissController().getSimSt().isSsAplusCtrlCogTutorMode())
					return true;
			
		char c = selection.charAt(DORMIN_TABLE_STEM.length());
        int col = c - '1' +1;
		
        int rIdx = selection.indexOf("R");
        char r = selection.charAt(rIdx +1);
        int row = r - '1' +1;
			
			if (col==3){  /* for transformation, check if lhs and rhs of the same row are not emtpy.*/
				TableExpressionCell lhs=getTableCell(controller, row, 1);
				TableExpressionCell rhs=getTableCell(controller, row , 2);
				
				if (lhs.getText().isEmpty() || rhs.getText().isEmpty()){
					returnValue=false;
					}
				else
					trace.out(lhs.getText()+"="+rhs.getText());
			}
			else if (row>1){
				TableExpressionCell transformation=getTableCell(controller, row-1, 3);
			
				if (transformation.getText().isEmpty()){
					returnValue=false;
					}
				//else
					//trace.out("transformation is " + transformation.getText());
					
			}
			
			
			
		return returnValue;
	}

	public boolean checkInput(String selection, String input) {
		if("done".equalsIgnoreCase(selection))
			return true;
		if(!"done".equalsIgnoreCase(selection) && selection.length() < "dorminTable".length()+1)
			return false;
		char column = selection.charAt("dorminTable".length());
		
		if (!checkSkipStep(selection)){
			//trace.out("A step has been skipped....");
		 	return false;
		 }
		 
		if(column == '3')
			return checkInputTransformation(input);
		return checkInputExpression(input);
	}
	
	private boolean checkInputExpression(String input)
	{
		boolean variableSeen  = false;
		char variableChar = '\0';
		
		if (input.contains("/-")) return false;
				
		for(int i=0;i<input.length();i++)
		{
			char current = input.charAt(i);
						
			if(!Character.isLetterOrDigit(current))
			{
				boolean valid = false;
				for(int j=0;j<validSymbols.length;j++)
				{
					if(validSymbols[j] == current)
						valid = true;
				}
				if(!valid) return false;
			}

			/* If current character is 'd','f' or 'L' tell that they are invalid || 
			If the variables are entered successively return false Example: xx = 2x */
			if(Character.isLetter(current)) 
			{
				for(int j = 0; j < invalidVariables.length; j++) 
				{
					if(current == invalidVariables[j]) 
					{
						return false;
					}
				}

				if(!variableSeen)
				{
					variableChar = current;
					variableSeen = true;
				} 
				else 
				{
					if(current != variableChar || current == input.charAt(i-1) && i > 0)
						return false;
				}
			}
			
			/* This checks for inconsistencies when the students try to enter a problem which is not 
			structured properly (constant followed by a variable is the correct format).  Example: c10=5c+12 */	
			if(Character.isDigit(current) && i > 0) 
			{
				if(Character.isLetter(input.charAt(i-1)))
					return false;
			}
			
			if(Character.isLetter(current) && Character.isUpperCase(current))
			{
				return false;
			}		
		}
		return checkDecimals(input);
	}
	
	private boolean checkInputTransformation(String input)
	{
		if(input.toLowerCase().startsWith("combine") || input.toLowerCase().startsWith("clt"))
			return checkInputTransformationCLT(input);
		String[] parts = input.split(" ");
		
		
		if (input.equals("divide 0")) return false;
		
		
		if(parts.length != 2)
			return false;
		if(!checkInputExpression(parts[1]))
			return false;
		for(int i=0;i<validOperators.length;i++)
		{
			if(parts[0].equals(validOperators[i]))
				return true;
		}
		return false;
	}
	
	private boolean checkInputTransformationCLT(String input)
	{
		// combine like term/s 2x+4x+5
		String parts[] = input.split(" "); // split this string around the whitespace
		if(parts.length != 4)
			return false;
		if(!checkInputExpression(parts[3]))
			return false;
		if(parts[0].equalsIgnoreCase("Combine") && parts[1].equalsIgnoreCase("Like") && (parts[2].equalsIgnoreCase("Terms")))
			return true;
	
		trace.out("miss", "checkInputTransformationCLT returning false");
		return false;
	}
	
	
	
	
	
	public String missingStepReasoning(String selection){
		String returnValue=null;
			
		char c = selection.charAt(DORMIN_TABLE_STEM.length());
        int col = c - '1' +1;
		
        int rIdx = selection.indexOf("R");
        char r = selection.charAt(rIdx +1);
        int row = r - '1' +1;
			
		
			if (col==3){  /* for transformation, check if lhs and rhs of the same row are not emtpy.*/
				TableExpressionCell lhs=getTableCell(controller, row, 1);
				TableExpressionCell rhs=getTableCell(controller, row , 2);
				TableExpressionCell trans=getTableCell(controller, row, 3);
			
				if (lhs!=null && lhs.getText().isEmpty()){
					trans.requestFocus();
							
					returnValue="You cannot fill in the transformation without enter the left-hand side first. Enter the left-hand side first, then enter the transformation";
					}
				else if (rhs!=null && rhs.getText().isEmpty())
				{
					trans.requestFocus();
					returnValue="You cannot fill in the transformation without enter the right-hand side first. Enter the right-hand side first, then enter the transformation";
				}

			}
			else if (row>1){
				TableExpressionCell transformation=getTableCell(controller, row-1, 3);
				TableExpressionCell side=getTableCell(controller, row, col);
				if (transformation!=null && transformation.getText().isEmpty() && col==1){
					side.requestFocus();
					returnValue="You cannot fill in the left-hand side without entering a transformation. Enter a transformation first, then enter the left-hand side";
					}
				else if (transformation!=null && transformation.getText().isEmpty() && col==2){
					side.requestFocus();
					returnValue="You cannot fill in the left-hand side without entering a transformation. Enter a transformation first, then enter the right-hand side";
					}
				else if (transformation==null){
				returnValue="You cannot fill in the left-hand side without entering a transformation. Enter a transformation first, then enter the right-hand side";
				}
			}
			
			
			
		return returnValue;
	}
	
	
	
	
	private String identifyInputProblem(String selection, String input) {
		
		String stepMissing=missingStepReasoning(selection);
		if (stepMissing!=null){			
			return stepMissing;	
		}
			
	
			
		if("done".equalsIgnoreCase(selection))
			return "";
		if(!"done".equalsIgnoreCase(selection) && selection.length() < "dorminTable".length()+1)
			return "It seems like you used "+selection+".  Can you write in one of the columns instead?";
		char column = selection.charAt("dorminTable".length());
		if(column == '3')
			return identifyInputTransformationProblem(input);
		return identifyInputExpressionProblem(input);
	}

	private String identifyInputExpressionProblem(String input)
	{
		boolean variableSeen  = false;
		char variableChar = '\0';
			
		for(int i=0;i<input.length();i++)
		{
			char current = input.charAt(i);
			if(!Character.isLetterOrDigit(current))
			{
				boolean valid = false;
				for(int j=0;j<validSymbols.length;j++)
				{
					if(validSymbols[j] == current)
						valid = true;
				}
				if(!valid)
				{
					if(current == ' ')
						return "Don't put any spaces in the expression.";
					return "You can't use '"+current+"'.  Please try again without it.";
				}
			}
			
			/* If current character is 'd','f' or 'L' tell that they are invalid || */
			/* If the variables are entered successively return false Example: xx = 2x */
			if(Character.isLetter(current)) 
			{
				for(int j = 0; j < invalidVariables.length; j++) 
				{
					if(current == invalidVariables[j]) 
					{
						return formatInvalidVariableUsedMessage();
					}
				}

				if(!variableSeen)
				{
					variableChar = current;
					variableSeen = true;
				} 
				else 
				{
					if(current != variableChar)
						//return "You can use only one letter as a variable term in the equation."; // This error message is changed because it is confusing when tutor is entering a transformation. Detail: https://docs.google.com/document/d/1u5foxLrZaF65vCwNkeO_0NwIaeULcTQchJ0YK70de_Q/edit#bookmark=id.lr78q7hka11s
						return "Invalid input!\nIf you are entering an equation, use one type of letter as the variable (e.g 2v + 3 = 9v is valid but 2c + 3 = 9x is invalid). \nIf you are entering a transformation, write add/subtract/divide/multiply followed by a term (eq: add 2 or add 2x are valid) ";
					
					else if(current == input.charAt(i-1) && i > 0)
						return "Did you forget to put an operator between " + current + " ?";
				}
			}

			/* This checks for inconsistencies when the students try to enter a problem which is not 
			structured properly (constant followed by a variable is the correct format).  Example: c10=5c+12 */	
			if(Character.isDigit(current) && i > 0) 
			{
				if(Character.isLetter(input.charAt(i-1)))
					return "You can't use " + input + " as it's incorrect. Could you try it again ?";
			}

			
			
			if(Character.isLetter(current) && Character.isUpperCase(current))
			{
				return "Don't use capital letters.";
			}
					
		}
			
			
		if (input.equals("/-"))
			return "You can't use " + input + " as it's incorrect. Could you try it again ?";
			
		if (input.contains("/-") && !input.startsWith("-")){
			String newInput=input.replace("/-","/");
			newInput="-"+newInput;
			return "Using /- is incorrect. Enter "+newInput+" instead."; 
		}
		
	
		if (input.contains("/-")){
			
			String num=EqFeaturePredicate.numeratorStatic(input);
			String den=EqFeaturePredicate.denominatorStatic(input);
			
			String newInput=""+num+"/"+den+"";
			newInput=newInput.replaceAll("-","");
			return "Using "+input+" is incorrect. You cannot have negative sign in the denominator.";
		}
		
		
		
		return identifyDecimalsProblem(input);
	}
	
	private String identifyInputTransformationProblem(String input)
	{
			if (input.equals("divide 0")) {
				return "You can't divide by zero.";
			}

		String[] parts = input.split(" ");
		if(parts.length == 0)
			return "You must enter a valid transformation.";
		if(parts.length == 2 && !checkInputExpression(parts[1]))
			return identifyInputExpressionProblem(parts[1]);
		for(int i=0;i<validOperators.length;i++)
		{
			if(parts.length > 2 && parts[0].equalsIgnoreCase(validOperators[i]))
			{
				if(input.endsWith("to both sides"))
					return "Don't put 'to both sides'.\nIf you want to "+operatorPhrasing[i]+" 2 to both sides, for example, say '"+validOperators[i]+" 2'.";
				if(input.endsWith("on both sides"))
					return "Don't put 'on both sides'.\nIf you want to "+operatorPhrasing[i]+" 2 on both sides, for example, say '"+validOperators[i]+" 2'.";
				if(input.endsWith("from both sides"))
					return "Don't put 'from both sides'.\nIf you want to "+operatorPhrasing[i]+" 2 from both sides, for example, say '"+validOperators[i]+" 2'.";
				return "Saying '"+input+"' is incorrect. If you want to "+operatorPhrasing[i]+" 2, for example, say '"+validOperators[i]+" 2'.";
			}
			else if(input.contains("combine like terms") || input.contains("combine") || input.contains("clt") || input.contains("distribute")) { // "combine like terms" || "distribute"
				if(input.contains("combine like terms") || input.contains("combine") || input.contains("clt"))
					return "Saying "+input+" is incorrect. If you want to combine like terms 2-3b-8, for example, say 'combine like terms 2-3b-8'.";
				else if(input.contains("distribute"))
					return "Saying "+input+" is incorrect. If you want to distribute 3(x+2), for example, say 'distribute 3(x+2)'.";
			}
			else if(parts.length == 1 && parts[0].equalsIgnoreCase(validOperators[i]))
				return "Saying "+parts[0]+" is incorrect. If you want to "+operatorPhrasing[i]+" 2, for example, say '"+validOperators[i]+" 2'.";
			else if(parts[0].equalsIgnoreCase(validOperators[i]))
				return "Don't use capital letters.";
		}
		String msg = "Saying '"+parts[0]+"' is incorrect.  You can use ";
		for(int i=0;i<validOperators.length;i++)
		{
			if(i == validOperators.length-1)
				msg += " or "+validOperators[i];
			else
				msg += " "+validOperators[i]+",";
		}
		msg += " for the transformation.";
		return msg;
	}

	@Override
	public String invalidInputMessage(String selection, String input, String[] foa) {
		return invalidInputMessage(selection,input);
	}
	
	public String invalidInputMessage(String selection, String input) {
		//String msg = "I don't understand "+input+".\n";
		return identifyInputProblem(selection,input);
	}
	
    //Attempts to interpret an invalid input for a given selection into a valid one.
    //If it is able to change the input so that checkInput returns true, it should
    //return the new input.  If not, it should return null.  If it is correct, it should
	//be unchanged.
	@Override
	public String interpret(String selection, String input)
	{
		if("done".equalsIgnoreCase(selection))
			return input;
		if(!"done".equalsIgnoreCase(selection) && selection.length() < "dorminTable".length()+1)
			return null;
		char column = selection.charAt("dorminTable".length());
		if(column == '3')
			return interpretTransformation(input);
		return interpretExpression(input);
    }
	
	@Override
	public String interpret(String selection, String input, String[] foa){
		return interpret(selection,input);
	}	    
	    
	
	private String interpretExpression(String input)
	{
		input = input.replaceAll(" ", "");
		input = input.toLowerCase();
		input = interpretDecimals(input);
		if(checkInputExpression(input))
			return input;
		return null;
	}
	
	
	private String interpretTransformation(String input)
	{
		input = input.trim();
		input = input.toLowerCase();
		input = interpretDecimals(input);
		input = input.replaceAll(" {2,}", " ");
		if(checkInputTransformation(input))
			return input;
		String[] parts = input.split(" ");		
		
		if(parts.length < 2)
		{
			input = splitSmushedTransformation(input);
			if(checkInputTransformation(input))
				return input;
			else
				return null;
		}
		
		if(parts.length == 2) 
		{
			if(parts[0].equalsIgnoreCase("Combine") || parts[0].equalsIgnoreCase("clt"))
			{
				input = "combine like terms" + " " + parts[1];
				trace.out("miss", "input: " + input);
				if(checkInputTransformationCLT(input)) {
					trace.out("miss", "Returning: " + input);
					return input;
				}
			}
			//String[] singledigits = {"one","two","three","four","five","six","seven","eight","nine","ten"};
			//if(singledigits.)
			//Integer number = Integer.valueOf(parts[1].trim());
			
			// autotranslate should happen here
		}
		
		if(parts.length == 3)
		{
			if(parts[1].equalsIgnoreCase("by") || parts[1].equalsIgnoreCase("from") || parts[1].equalsIgnoreCase("to") || parts[1].equalsIgnoreCase("with"))
			{
				input = parts[0]+" "+parts[2];
				parts[1] = parts[2];
				if(checkInputTransformation(input))
					return input;
			}
			else
			{
				return null;
			}
		}
		
		if(parts.length == 4)
		{
			if(parts[0].equalsIgnoreCase("Combine") && parts[1].equalsIgnoreCase("Like") && parts[2].equalsIgnoreCase("Term"))
			{
				input = "combine like terms" + " " + parts[3];
				if(checkInputTransformation(input))
					return input;
 			}
		}
		
		if(parts.length > 4)
		{
			if(parts[2].equalsIgnoreCase("to") || parts[2].equalsIgnoreCase("on") || parts[2].equalsIgnoreCase("from"))
			{
				if(parts[3].equalsIgnoreCase("both") && parts[4].equalsIgnoreCase("sides"))
				{
					return parts[0]+" "+parts[1];
				}
			}
			return null;
		}
		String operator = nearOperatorMatch(parts[0]);
		if(operator != null)
		{
			input = operator+" "+parts[1];
		}
		if(checkInputTransformation(input))
			return input;
		return null;
	}
	
	private String splitSmushedTransformation(String input)
	{
		for(int i=0;i<validOperators.length;i++)
		{
			if(input.startsWith(validOperators[i]))
				return validOperators[i]+" "+input.substring(validOperators[i].length());
		}
		if(input.startsWith("+"))
			return "add "+input.substring(1);
		if(input.startsWith("-"))
			return "subtract "+input.substring(1);
		if(input.startsWith("*"))
			return "multiply "+input.substring(1);
		if(input.startsWith("/"))
			return "divide "+input.substring(1);
		return input;
	}
	
	private String nearOperatorMatch(String badOp)
	{
		if(badOp.length() < 1)
			return null;
		if(badOp.equals("+"))
			return "add";
		if(badOp.equals("-"))
			return "subtract";
		if(badOp.equals("*"))
			return "multiply";
		if(badOp.equals("/"))
			return "divide";
		for(int i=0;i<validOperators.length;i++)
		{
			if(badOp.charAt(0) == validOperators[i].charAt(0))
			{
				int nErrors = 0;
				int index = 1;
				if(badOp.length() > validOperators[i].length())
				{
					trace.out("ss", "Errors for too long: "+(badOp.length() - validOperators[i].length()));
					nErrors+= badOp.length() - validOperators[i].length();
				}
				else if(badOp.length() < validOperators[i].length())
				{
					trace.out("ss", "Errors for too long: "+(badOp.length() - validOperators[i].length()));
					nErrors+= validOperators[i].length() - badOp.length();
				}
				while(nErrors <= 1 && index < badOp.length() && index < validOperators[i].length())
				{
					int start = 0;
					int end = badOp.length();
					if(index > 1)
						start = index -1;
					if(index+2 < badOp.length())
						end = index+2;
					int thisIndex = badOp.substring(start, end).indexOf(validOperators[i].charAt(index));
					trace.out("ss", ""+validOperators[i].charAt(index)+" in "+badOp.substring(start, end));
					if(thisIndex == -1)
					{
						trace.out("ss", "Error for "+badOp.substring(start, end)+" not found");
						nErrors++;
					}
					index++;
				}
				if(nErrors <= 2)
					return validOperators[i];
			}
		}
		return null;
	}
	
	private boolean checkDecimals(String input)
	{
		for(int i=0;i<input.length();i++)
		{
			if(input.charAt(i) == '.')
			{
				if(i == 0)
					return false;
				if(!Character.isDigit(input.charAt(i-1)))
					return false;
				if(i+1 == input.length())
					return false;
				if(!Character.isDigit(input.charAt(i+1)))
					return false;
			}
		}
		return true;
	}
	
	private String identifyDecimalsProblem(String input)
	{
		for(int i=0;i<input.length();i++)
		{
			if(input.charAt(i) == '.')
			{
				if(i == 0)
					return "Don't start numbers with a decimal.";
				if(!Character.isDigit(input.charAt(i-1)))
					return "Don't start numbers with a decimal.";
				if(i+1 == input.length())
					return "Don't end numbers with a decimal.";
				if(!Character.isDigit(input.charAt(i+1)))
					return "Don't end numbers with a decimal.";
			}
		}
		return "";
	}
	
	private String interpretDecimals(String input)
	{
		String fixed = "";
		for(int i=0;i<input.length();i++)
		{
			if(input.charAt(i) == '.')
			{
				if(i == 0)
					fixed = "0.";
				else if(!Character.isDigit(input.charAt(i-1)))
					fixed = fixed+"0.";
				else if(i+1 == input.length())
					fixed = fixed+".0";
				else if(!Character.isDigit(input.charAt(i+1)))
					fixed = fixed+".0";
				else
					fixed = fixed + input.charAt(i);
			}
			else
				fixed = fixed + input.charAt(i);
		}
		return fixed;
	}

	public boolean checkVariables(String cellText1, String cellText2) 
	{	
		boolean variableSeen  = false;
		char variableChar = '\0';
		for(int i=0;i<cellText1.length();i++)
		{
			char current = cellText1.charAt(i);
						
			if(Character.isLetter(current)) 
			{
				if(!variableSeen)
				{
					variableChar = current;
					variableSeen = true;
				} 
				else 
				{
					if(current != variableChar)
						return false;
				}
			}
		}
		
		for(int i=0;i<cellText2.length();i++)
		{
			char current = cellText2.charAt(i);
						
			if(Character.isLetter(current)) 
			{
				if(variableSeen) 
				{
					if(Character.toLowerCase(current) != Character.toLowerCase(variableChar))
						return false;
				} 
				else 
				{
					variableSeen = true;
					variableChar = current;
				}
			}
		}
		
		/* Check if there was any variable found at all or not. Example 15+5=20 should return false */
		if(variableChar == '\0') {
			return false;
		}
			
		return true;
	}

	@Override
	public String invalidVariablesMessage(String cellText1, String cellText2) {
		boolean variableSeen  = false;
		char variableChar = '\0';
		for(int i=0;i<cellText1.length();i++)
		{
			char current = cellText1.charAt(i);
						
			if(Character.isLetter(current)) 
			{
				if(!variableSeen)
				{
					variableChar = current;
					variableSeen = true;
				} 
				else 
				{
					if(current != variableChar)
						return "You can use only one letter as a variable term in the equation.";
				}
			}
		}
		
		for(int i=0;i<cellText2.length();i++)
		{
			char current = cellText2.charAt(i);
						
			if(Character.isLetter(current)) 
			{
				if(variableSeen) 
				{
					if(Character.toLowerCase(current) != Character.toLowerCase(variableChar))
						return "You can use only one letter as a variable term in the equation.";
				} 
				else 
				{
					variableSeen = true;
					variableChar = current;
				}
			}
		}
		
		if(variableChar == '\0')
			return "The equation does not have any variables. Could you try another equation?";
		
		return "";
	}
	
	public Sai checkInputHighlightedWidget(Component component)
	{
		String action = "UpdateTable";
		if(!(component instanceof TableCell))
    		return null;
    	
    	JCommTable commTable = (JCommTable) component.getParent();
    	TableCell cell = (TableCell)component;

    	int column = cell.getColumn()+1;
    	int row = cell.getRow()+1;
    	String selection = commTable.getCommName()+"_C"+column+"R"+row;
    	String input = cell.getText();
    	
		if(!checkInput(selection, input)) // Try to rectify the input
		{
			String newInput = interpret(selection, input);
			if(newInput != null)
				input = newInput;
		}	
    	return new Sai(selection, action, input);
	}
	
	/**
	 * Returns a string representation of the invalid variables 
	 * @return
	 */
	private String formatInvalidVariableUsedMessage() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("Remember, ");
		for(int i = 0; i < invalidVariables.length/2; i++) {
			
			if(i  > 0 && (i+1) != invalidVariables.length/2)
				sb.append(", ");
			
			if((i+1) == invalidVariables.length/2)
				sb.append(" and '" + invalidVariables[i] + "' ");
			else
				sb.append("'" + invalidVariables[i] +"'");
		}
		sb.append(" cannot be used as a variable.");
		return sb.toString();
	}
}

