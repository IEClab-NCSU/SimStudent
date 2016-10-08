package SimStFractionAdditionV1;

import java.util.Vector;

import javax.swing.JOptionPane;

import pact.CommWidgets.JCommComboBox;
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
    
	private boolean isLineEmpty(int i, BR_Controller brController){
		boolean returnValue=false;
		if (i==4){
			String t1=getTableCell(brController, 1, 7).getText();
			String t2=getTableCell(brController, 2, 7).getText();
			String t3=getCommText(brController,7).getText();			
			String t4=getTableCell(brController, 1, 8).getText();
			String t5=getTableCell(brController, 2, 8).getText();
			String t6=getCommText(brController,8).getText();			
			if (t1.equals("") && t2.equals("") && t3.equals("") && t4.equals("") && t4.equals("") && t6.equals("") )
				returnValue=true;		
		}
		else if (i==3){
			String t1=getTableCell(brController, 1, 5).getText();
			String t2=getTableCell(brController, 2, 5).getText();
			String t3=getCommText(brController,5).getText();			
			String t4=getTableCell(brController, 1, 6).getText();
			String t5=getTableCell(brController, 2, 6).getText();
			String t6=getCommText(brController,6).getText();			
			if (t1.equals("") && t2.equals("") && t3.equals("") && t4.equals("") && t4.equals("") && t6.equals("") )
				returnValue=true;	
		}
		else if (i==2){
			String t1=getTableCell(brController, 1, 3).getText();
			String t2=getTableCell(brController, 2, 3).getText();
			String t3=getCommText(brController,3).getText();			
			String t4=getTableCell(brController, 1, 3).getText();
			String t5=getTableCell(brController, 2, 3).getText();
			String t6=getCommText(brController,3).getText();			
			if (t1.equals("") && t2.equals("") && t3.equals("") && t4.equals("") && t4.equals("") && t6.equals("") )
				returnValue=true;	
		}
		else{
				returnValue=true;

		}
	return returnValue;
	
	}
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

		Vector /* Object */ vFoa = new Vector();

		trace.out("ss", "$$$$$$$$$$ Selection: "+selection+" Action: "+action+" Input: "+input);
		//Done Button Pressed- FoA is the first two columns of the last row with info
		if(selection.equalsIgnoreCase("done"))
		{
					
				for (int i=4;i>1;i--){
		
					if (!isLineEmpty(i,brController)){
						if (i==4){ //an i teleytaia grammi einai i 4i 
							vFoa.add(getTableCell(brController, 1, 7));			
							vFoa.add(getTableCell(brController, 2, 7));
							vFoa.add(getCommText(brController,7));
							//vFoa.add(getConversionChunk(brController, 7));
							//vFoa.add(getConversionChunk(brController, 8));
							vFoa.add(this.getCommCombo(brController, 4));	
						}
						else if (i==3){
							vFoa.add(getTableCell(brController, 1, 5));			
							vFoa.add(getTableCell(brController, 2, 5));
							vFoa.add(getCommText(brController,5));
							//vFoa.add(getConversionChunk(brController, 5));
							//vFoa.add(getConversionChunk(brController, 6));
							vFoa.add(this.getCommCombo(brController, 3));	
						}
						else if (i==2){
							vFoa.add(getTableCell(brController, 1, 3));			
							vFoa.add(getTableCell(brController, 2, 3));
							vFoa.add(getCommText(brController,3));
							//vFoa.add(getConversionChunk(brController, 3));
							//vFoa.add(getConversionChunk(brController, 4));
							vFoa.add(this.getCommCombo(brController, 2));	
						}
								
						break;
					}				
					
				}
				

					return vFoa;					
		}

		if (selection.contains(DORMIN_TABLE_STEM)){
			char c = selection.charAt(DORMIN_TABLE_STEM.length());
			int col = c - '1' +1;
		
			int rIdx = selection.indexOf("R");
			char r = selection.charAt(rIdx +1);
			int row = r - '1' +1;      
  			if ((col==3 && row==1)){	/*second fraction addition, complex fraction 3 numerator*/	 
  			System.out.println("****" + this.getCommCombo(brController, 2).getValue());
				/*** ADD_NUM****/
				if (this.getCommCombo(brController, 2).getValue().equals("Add")){
					vFoa.add(getTableCell(brController, 1, 1));  	//denominator of 2nd fraction
					vFoa.add(getTableCell(brController, 1, 2));  	//numerator of 1st fraction
					vFoa.add(this.getCommCombo(brController, 2));	//first dropdown
				}
				/*** RED_NUM***/
				else if (this.getCommCombo(brController, 2).getValue().equals("Reduce")) {
					
					vFoa.add(getTableCell(brController, 1, 1)); 	 //numerator of 1st fraction
					vFoa.add(getTableCell(brController, 2, 1));  	//denominator of 1st fraction
					vFoa.add(getTableCell(brController, 2, 3));  	//denominator of 3rd fraction fraction num
					vFoa.add(this.getCommCombo(brController, 2));	//first drop down
				}
				/*** SIM_NUM***/
				else{
				
					vFoa.add(getTableCell(brController, 1, 1));  	//numerator of 3rd fraction
					vFoa.add(getTableCell(brController, 2, 1));  	//denominator of 3rd fraction
					vFoa.add(this.getCommCombo(brController, 2));	//first dropdown
				}
							
       	}
			
			else if ((col==4 && row==1)){	/*second fraction addition, complex fraction 4 numerator*/	 
					/*** RED_NUM***/
					if (this.getCommCombo(brController, 2).getValue().equals("Reduce")){	
					vFoa.add(getTableCell(brController, 1, 2)); 	 
					vFoa.add(getTableCell(brController, 2, 2));  	
					vFoa.add(getTableCell(brController, 2, 4));  	
					vFoa.add(this.getCommCombo(brController, 2));	//first drop down		
					}
					/*** SIM_NUM***/
					else{
					vFoa.add(getTableCell(brController, 1, 2));  	//numerator of 3rd fraction
					vFoa.add(getTableCell(brController, 2, 2));  	//denominator of 3rd fraction
					vFoa.add(this.getCommCombo(brController, 2));	//first dropdown
					}							
       	}
			
			else if ((col==3 || col==4) && row==2){	//second fraction addition, both denominators
					/*** SIN_DEN ****/
					if (this.getCommCombo(brController, 2).getValue().equals("Simplify")){
						if (col==3){
							vFoa.add(getTableCell(brController, 1, 1));  	//numerator of 5rd fraction
							vFoa.add(getTableCell(brController, 2, 1));  	//denominator of 5rd fraction
							vFoa.add(this.getCommCombo(brController, 2));  		//whole of complex of 3rth 		
						}
						else{
							vFoa.add(getTableCell(brController, 1, 2));  	//numerator of 5rd fraction
							vFoa.add(getTableCell(brController, 2, 2));  	//denominator of 5rd fraction
							vFoa.add(this.getCommCombo(brController, 2));  		//whole of complex of 3rth 		
						}
					}
					else{ /*** ADD_DEN / RED_DEN****/
					vFoa.add(getTableCell(brController, 2, 1)); 	 //first fraction den
					vFoa.add(getTableCell(brController, 2, 2));  	 //second fraction den	
					vFoa.add(this.getCommCombo(brController, 2));	 // first drop down			
					}
			}
			
			else if (col==5 && row==1){	//3rd farction addiiton, first complex fraction numerator
				/* an dropdown stin idia siera einai add, rposthese.
				 * an einai 
				 */
				/*** ADD_NUM****/
				if (this.getCommCombo(brController, 3).getValue().equals("Add")){
					vFoa.add(getTableCell(brController, 1, 3));  	//denominator of 3rd fraction
					vFoa.add(getTableCell(brController, 1, 4));  	//numerator of 4rth fraction
					vFoa.add(this.getCommCombo(brController, 3));	//first dropdown	
				}
				/*** COMPLEX_NUM****/
				else if (this.getCommCombo(brController, 3).getValue().equals("Conversion")){
					vFoa.add(getTableCell(brController, 1, 3));  	//numerator of 3rd fraction
					vFoa.add(getCommText(brController, 5));  		//whole of complex of 5rth 
					vFoa.add(getTableCell(brController, 2, 5));  	//denominator of 5th	
					vFoa.add(this.getCommCombo(brController, 3));	//first dropdown
				}
				/*** SIMPLIFY_NUM****/
				else if (this.getCommCombo(brController, 3).getValue().equals("Simplify")){
					vFoa.add(getTableCell(brController, 1, 3));  	//numerator of 3rd fraction
					vFoa.add(getTableCell(brController, 2, 3));  	//denominator of 3rd fraction
					vFoa.add(this.getCommCombo(brController, 3));	//first dropdown
				}
				/*** REDUCE_NUM****/
				if (this.getCommCombo(brController, 3).getValue().equals("Reduce")){	
					vFoa.add(getTableCell(brController, 1, 3)); 	 
					vFoa.add(getTableCell(brController, 2, 3));  	
					vFoa.add(getTableCell(brController, 2, 5));  	
					vFoa.add(this.getCommCombo(brController, 3));	//first drop down		
					}	
					
			}
			
			else if (col==5 && row==2){
				/*** ADD_DEN****/
				if (this.getCommCombo(brController, 3).getValue().equals("Add")){	

					vFoa.add(getTableCell(brController, 2, 3)); 	 //first fraction den
					vFoa.add(getTableCell(brController, 2, 4));  	 //second fraction den	
					vFoa.add(this.getCommCombo(brController, 3));	 // first drop down
				}
				/*** COMPLEX_DEN****/
				else if (this.getCommCombo(brController, 3).getValue().equals("Conversion")){
					vFoa.add(getTableCell(brController, 1, 3));  	//numerator of 3rd fraction
					vFoa.add(getTableCell(brController, 2, 3));  	//denominator of 3rd fraction
					//vFoa.add(getCommText(brController, 3));			//whole number of 3rd fraction
					vFoa.add(this.getCommCombo(brController, 3));  	//drop down option of 3rd 	
					vFoa.add(this.getCommCombo(brController, 2));  	//drop down option of 2nd 					
				}
				/*** SIMPLIFY_DEN****/
				else if (this.getCommCombo(brController, 3).getValue().equals("Simplify")){
					vFoa.add(getTableCell(brController, 1, 3));  	//numerator of 3rd fraction
					vFoa.add(getTableCell(brController, 2, 3));  	//denominator of 3rd fraction
					vFoa.add(this.getCommCombo(brController, 3)); 		//whole of complex of 3rth 
				}
				else{
					/*** REDUCE_DEN****/
					vFoa.add(getTableCell(brController, 2, 3)); 	 //first fraction den
					vFoa.add(getTableCell(brController, 2, 4));  	 //second fraction den	
					vFoa.add(this.getCommCombo(brController, 3));	 // first drop down		
				}
						
			}
			else if (col==6){  ///COMPLEX FRACTION 6
				 if (this.getCommCombo(brController, 3).getValue().equals("Reduce")){
					 if (row==1){  /*** RED_NUM */
				 			vFoa.add(getTableCell(brController, 1, 4)); 	 
							vFoa.add(getTableCell(brController, 2, 4));  	
							vFoa.add(getTableCell(brController, 2, 6));  	
							vFoa.add(this.getCommCombo(brController, 3));	//first drop down	
				 
					 }
				 else{ /**** RED_DEN ****/
					 vFoa.add(getTableCell(brController, 2, 3)); 	 //first fraction den
					 vFoa.add(getTableCell(brController, 2, 4));  	 //second fraction den	
					 vFoa.add(this.getCommCombo(brController, 3));	 // first drop down	
				 
				 }
	 			}
	 			else{
	 				/* SIMPLIFY NUM_DEN*/		
	 				vFoa.add(getTableCell(brController, 1, 4));  	//numerator of 3rd fraction
					vFoa.add(getTableCell(brController, 2, 4));  	//denominator of 3rd fraction
					vFoa.add(this.getCommCombo(brController, 3)); 		//whole of complex of 3rth 
	 			}
				 
			}
			else if (col==7 && row==2){	//3rd farction addiiton, first complex fraction numerator
				/*** COMPLEX_DEN****/
				if (this.getCommCombo(brController, 4).getValue().equals("Conversion")){
					vFoa.add(getTableCell(brController, 1, 5));  	//numerator of 3rd fraction
					vFoa.add(getTableCell(brController, 2, 5));  	//numerator of 3rd fraction
					//vFoa.add(getCommText(brController, 5));
					vFoa.add(this.getCommCombo(brController, 4));  		//whole of complex of 3rth 	
					vFoa.add(this.getCommCombo(brController, 3));  	//drop down option of 2nd 
				}
				/*** SIMPLIFY_DEN****/
				else if (this.getCommCombo(brController, 4).getValue().equals("Simplify")){
					vFoa.add(getTableCell(brController, 1, 5));  	//numerator of 5rd fraction
					vFoa.add(getTableCell(brController, 2, 5));  	//denominator of 5rd fraction
					vFoa.add(this.getCommCombo(brController, 4));  		//whole of complex of 3rth 		
					
				}
			
			}
			else if (col==7 && row==1){	//3rd farction addiiton, first complex fraction numerator
				/*** COMPLEX_NUM****/
				if (this.getCommCombo(brController, 4).getValue().equals("Conversion")){
					vFoa.add(getTableCell(brController, 1, 5));  	//numerator of 3rd fraction
					vFoa.add(getCommText(brController, 7));  		//whole of complex of 5rth 
					vFoa.add(getTableCell(brController, 2, 7));  	//denominator of 5th	
					vFoa.add(this.getCommCombo(brController, 4));	//first dropdown
				}
				/*** SIMPLIFY_NUM****/
				else if (this.getCommCombo(brController, 4).getValue().equals("Simplify")){
					vFoa.add(getTableCell(brController, 1, 5));  	//numerator of 3rd fraction
					vFoa.add(getTableCell(brController, 2, 5));  	//denominator of 3rd fraction
					vFoa.add(this.getCommCombo(brController, 4));	//first dropdown
				}		
			}
			
		}//end of DORMIN_TABLE
		else if (selection.contains(COMBOFIELD_STEM)){		
					char c = selection.charAt(COMBOFIELD_STEM.length());
					int pos = c - '1' +1;
					
					if (pos==2){	// first dropdown looks always at the first fraction addition denominators
					
						if (this.getCommCombo(brController, 2).getValue().equals("Simplify")){		
						/*SKILL SIMPLIFY*/
							vFoa.add(getConversionChunk(brController, 1)); 
							vFoa.add(getConversionChunk(brController, 2));
							vFoa.add(this.getCommCombo(brController, 1));
						}
						else{
						/*SKILL ADD / SKILL REDUCE*/
							vFoa.add(getTableCell(brController, 2, 1)); 
							vFoa.add(getTableCell(brController, 2, 2)); 
							vFoa.add(this.getCommCombo(brController, 1));
						}
					}
					else if (pos==3){   
						/*  second dropdown looks
						 * add / reduce: denominators of second fraction addition + first dropwdown
						 * complex / simplify: complex fraction 3, complex fraction 4 and first dropdown.
						 * i logiki: 
						 * - an to proigoumeno dropdown einai "add", tote to mono pou mporei na kanei einai
						 *   simplify i complex opote koita complex fraction 3, complex fraction 4 kai first dropdown
						 * - an to porigoumeno dropdown einai reduce tote koita denominators giati to mono pou 
						 * mporei na kanei einai add. 
						 * 
						 * 
						*/ 
						//an sto proigoumeno einai add tote prepei na kaneis eitei reduce / eite simplify opote
						//koita ta chunks.
						if (this.getCommCombo(brController, 2).getValue().equals("Add")){
							vFoa.add(getConversionChunk(brController, 3)); 
							vFoa.add(getConversionChunk(brController, 4));
							vFoa.add(this.getCommCombo(brController, 2));
						}
						//if previous step was reduce then add
						else if (this.getCommCombo(brController, 2).getValue().equals("Reduce")){
							vFoa.add(getTableCell(brController, 2, 3)); 
							vFoa.add(getTableCell(brController, 2, 4)); 
							vFoa.add(this.getCommCombo(brController, 2));
							
							
						}	
						else{	/*If previous step was simplify*/
							if (this.getCommCombo(brController, 3).getValue().equals("Add")){
								vFoa.add(getConversionChunk(brController, 3)); 
								vFoa.add(getConversionChunk(brController, 4));
								vFoa.add(this.getCommCombo(brController, 2));
							}
							else if (this.getCommCombo(brController, 3).getValue().equals("Reduce")){
								vFoa.add(getTableCell(brController, 2, 3)); 
								vFoa.add(getTableCell(brController, 2, 4)); 
								vFoa.add(this.getCommCombo(brController, 2));
							}
							
							
						}
						
						/*	vFoa.add(getConversionChunk(brController, 3)); 
							vFoa.add(getConversionChunk(brController, 4));
							vFoa.add(this.getCommCombo(brController, 2));
							*/
							
					}
					else if (pos==4) { //last dropdown must be either complex or simplify, so just look at 
								// complex fraciton 5, complex fraction 6 and dropdown 3
							vFoa.add(getConversionChunk(brController, 5)); 
							vFoa.add(getConversionChunk(brController, 6));
							vFoa.add(this.getCommCombo(brController, 3));
						
					}
			
		}
		else if (selection.contains(TEXTFIELD_STEM)){
			char c = selection.charAt(TEXTFIELD_STEM.length());
			int pos = c - '1' +1;

			//ypothetoume oti mono sto teleutaio bima kanoume reduction opote mono ekei mporoume na exoume
				if (pos==5){
					
					vFoa.add(getTableCell(brController, 1, 3));  	//numerator of 3rd fraction
					vFoa.add(getTableCell(brController, 2, 3));  	//numerator of 3rd fraction					
					//vFoa.add(getTableCell(brController, 1, 5));  	//numerator of 3rd fraction
					//vFoa.add(getTableCell(brController, 2, 5));  	//numerator of 3rd fraction					
					vFoa.add(this.getCommCombo(brController, 3));
					//vFoa.add(this.getCommCombo(brController, 2));  	//drop down option of 2nd 	
					vFoa.add(getCommText(brController, 3));
			
					
				}
				else if (pos==7){
					vFoa.add(getTableCell(brController, 1, 5));  	//numerator of 3rd fraction
					vFoa.add(getTableCell(brController, 2, 5));  	//numerator of 3rd fraction					
					//vFoa.add(getTableCell(brController, 1, 7));  	//numerator of 3rd fraction
					//vFoa.add(getTableCell(brController, 2, 7));  	//numerator of 3rd fraction					
					vFoa.add(this.getCommCombo(brController, 4));
					//vFoa.add(this.getCommCombo(brController, 3));  	//drop down option of 2nd 
					vFoa.add(getCommText(brController, 5));
			
						
				}
			
		}


       return vFoa;
	}
	
		
	static final String COLNUM = "1";
	static final String ROWNUM = "Y";
	static final String TABNUM = "X";
	static final String DORMIN_TABLE_STEM = "dorminTable";
	static final String DORMIN_TABLE_NAME = DORMIN_TABLE_STEM + TABNUM + "_C" + COLNUM + "R" + ROWNUM;
	static final String TEXTFIELD_STEM = "commTextField";
	static final String TEXTFIELD_NAME = TEXTFIELD_STEM + TABNUM;
	static final String COMBOFIELD_STEM = "commComboBox";
	static final String COMBOFIELD_NAME = COMBOFIELD_STEM + TABNUM;
	static final String FRACTIONCHUNK_STEM =  "complex-fraction";
	static final String FRACTIONCHUNK_NAME = FRACTIONCHUNK_STEM + TABNUM;
	
	
	private TableExpressionCell getTableCell(BR_Controller brController, int row, int column) {
		
		// dorminTable0_CxRy
		String cellName = DORMIN_TABLE_NAME.replaceAll(TABNUM, ""+column);
		cellName = cellName.replaceAll(ROWNUM, ""+row);
		
		TableExpressionCell cell = (TableExpressionCell)brController.lookupWidgetByName( cellName );
		
		return cell;
	}
	
	private JCommTextField getCommText(BR_Controller brController, int id) {
		
		String textFieldName = TEXTFIELD_NAME.replaceAll(TABNUM, ""+id);
		
		JCommTextField tf = (JCommTextField)brController.lookupWidgetByName( textFieldName );
		
		return tf;
	}
	
	
	private JCommComboBox getCommCombo(BR_Controller brController,int id) {
		
		String comboFieldName = COMBOFIELD_NAME.replaceAll(TABNUM, ""+id);
		
		JCommComboBox cf = (JCommComboBox)brController.lookupWidgetByName( comboFieldName );
		return cf;
	}



	private JCommTextField getConversionChunk(BR_Controller brController,int id) {
		
		String chunkName = FRACTIONCHUNK_NAME.replaceAll(TABNUM, ""+id);
		
		JCommTextField cf = (JCommTextField)brController.lookupWidgetByName( chunkName );
		return cf;
	}
	
	
		
	
	public String foaDescription(Instruction inst)
	{
		String newStr="";
	
		if(inst.getFocusOfAttention().size() < 3)
			super.foaDescription(inst);
		String str = "";
		
		if(((String) inst.getFocusOfAttention().get(0)).contains("done"))
		{
			str = "the problem is solved";
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
			//newStr=" I did the transformation '"+foa2+"' when I had '"+foa1+"' and got '"+str+"'";
			newStr=" I had the fraction '"+foa1+"'/'"+foa2+"' and selected '"+str+"'";
			
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