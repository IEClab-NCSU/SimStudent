package SimStFractionAdditionV1;

import java.awt.Component;
import java.awt.Container;
import edu.cmu.pact.miss.Sai;
import pact.CommWidgets.JCommTable;
import pact.CommWidgets.JCommTable.TableCell;
import pact.CommWidgets.JCommTable.TableExpressionCell;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.InputChecker;
import edu.cmu.pact.miss.InquiryClSolverTutor;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import java.awt.Color;
import pact.CommWidgets.JCommTextField;

public class FractionAdditionInputChecker extends InputChecker {

	
	
	public boolean checkInput(String selection, String input) {
	
		return false;
		
	}

	public String invalidInputMessage(String selection, String input) {
		String msg = "I don't understand "+input+".\n";
		return msg;
	}
	
    //Attempts to interpret an invalid input for a given selection into a valid one.
    //If it is able to change the input so that checkInput returns true, it should
    //return the new input.  If not, it should return null.  If it is correct, it should
	//be unchanged.
	@Override
	public String interpret(String selection, String input)
	{
		return "interpret with 2 args"; 
    }
	@Override
	 public  boolean checkVariables(String cellText1, String cellText2){
	 return true;
	 }
	 
	

	
	@Override
	public String invalidVariablesMessage(String cellText1, String cellText2) {
		
		return "invalidVariablesMessage";
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
	
	
	public boolean isProblemSet(BR_Controller brController){
	
		String denominator1=getTableCell(brController,2,1).getText();
		String denominator2=getTableCell(brController,2,2).getText();
		if (denominator1.equals("") || denominator2.equals("") ) return false;
		else return true;
	}
	
	public boolean isProblemWithSameDenominators(BR_Controller brController){
	
	String denominator1=getTableCell(brController,2,1).getText();
	String denominator2=getTableCell(brController,2,2).getText();

	System.out.println("denom1="+denominator1+" and denom2=" + denominator2);
	
	if (denominator1.equals("") || denominator2.equals("") ) return false;
	
	System.out.println("result is=" + denominator1.equals(denominator2));
		
	return denominator1.equals(denominator2);
	}
	
	
	/*
	*  returns true if student entered something in a fraction (not in integer part of complex fraction addition).
	*/
	public boolean isFraction(String selection){
	
		return selection.contains(DORMIN_TABLE_STEM);
	}
	

	public int getFractionPosition(String selection){
	
		char c = selection.charAt(DORMIN_TABLE_STEM.length());
        int col = c - '1' +1;
		return col;
	}
	

	 
    
    
    private BR_Controller brController;   
    private void setBrController(BR_Controller brController){
    	this.brController=brController;
    }
    
    
    public boolean isIntermediateStepComplete(BR_Controller brController){
	
		String intDenom=getTableCell(brController,2,5).getText();
		String intNumerator=getTableCell(brController,1,5).getText();
		
		if (intDenom.equals("") || intNumerator.equals("") ) return false;
		else return true;
	
	}
	
	
    public boolean isIntermediateFractionAdditionComplete(BR_Controller brController){
	
		String f1Denom=getTableCell(brController,2,3).getText();
		String f1Numerator=getTableCell(brController,1,3).getText();
		
		String f2Denom=getTableCell(brController,2,4).getText();
		String f2Numerator=getTableCell(brController,1,4).getText();
		
		
		if (f1Denom.equals("") || f1Numerator.equals("") || f2Denom.equals("") || f2Numerator.equals("")) return false;
		else return true;
	
	}
	
	private JCommTextField getCommText(BR_Controller brController) {
		
		// dorminTable0_CxRy
		
		JCommTextField cell = (JCommTextField)brController.lookupWidgetByName( TEXTFIELD_STEM );
		
		return cell;
	}
	
    
	@Override
	public boolean checkInput(String selection, String input, String[] foas, BR_Controller brController) {
	setBrController(brController);
		
		if (isFraction(selection)){
			int fractionPosition=getFractionPosition(selection);
			
			if (fractionPosition==1 || fractionPosition==2){	
				if (isProblemSet(brController))				
					return !isProblemWithSameDenominators(brController);
				else return true; //while entering the problem do not check...
			}
			if (fractionPosition==5){
				//if problem has same denominator return false;
				return isIntermediateFractionAdditionComplete(brController);
			}
			if (fractionPosition==6){
				//if problem has same denominator return false;
				return isIntermediateStepComplete(brController);
			}
	
		}
		else{
				return isIntermediateStepComplete(brController);	
		}
				
	
		return true;
	}

	
	
	
	@Override
	public String interpret(String selection, String input, String[] foas) {
		if("done".equalsIgnoreCase(selection))
			return input;
		if(!"done".equalsIgnoreCase(selection) && selection.length() < "dorminTable".length()+1)
			return null;
			
		return null;
	}

	@Override
	public String invalidInputMessage(String selection, String input, String[] foas) {
	String returnMsg="uninitalized";
		if (isFraction(selection)){
			int fractionPosition=getFractionPosition(selection);
				if (fractionPosition==1 || fractionPosition==2){	
					returnMsg="This seems like an easy problem for "+ 	brController.getMissController().getSimStPLE().getSimStName() +". Why don't you give a fraction addition where fractions have different denominator?";
				}
				else if (fractionPosition==5){
						getTableCell(brController,2,5).setText("");
						getTableCell(brController,1,5).setText("");
					
						returnMsg="You need to solve this problem one step at a time!";
				}
				else if (fractionPosition==6){
						getTableCell(brController,2,6).setText("");
						getTableCell(brController,1,6).setText("");
					
					returnMsg="You need to solve this problem one step at a time!";
				}
				
				
			}	
		else{
			//this is the intermediate step
				getTableCell(brController,2,6).setText("");
				getTableCell(brController,1,6).setText("");
				
				getCommText(brController).setText("");
				returnMsg="You need to solve this problem one step at a time!";
		}	
		
		return returnMsg;
	}
}

