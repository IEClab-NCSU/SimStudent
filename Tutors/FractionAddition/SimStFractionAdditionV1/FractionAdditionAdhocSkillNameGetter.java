package SimStFractionAdditionV1;

import java.util.Vector;

import pact.CommWidgets.JCommComboBox;
import pact.CommWidgets.JCommTable;
import pact.CommWidgets.JCommTable.TableCell;
import pact.CommWidgets.JCommTable.TableExpressionCell;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.miss.SkillNameGetter;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

/**
 * @author jinyul
 */
public class FractionAdditionAdhocSkillNameGetter extends SkillNameGetter {

    /**
     * Task specific ad-hoc method to identify skill name based on current selection
     * This is for enumerating the Fraction Addition I tutor  
     * @param brController
     * @param selection
     * @param action
     * @param input
     * @return
     */
    public String /* Object */ skillNameGetter(BR_Controller brController, 
                                         String selection, String action, String input
                                         ) {
    	
    	if(selection.equals("done"))
    	{
    		trace.out("miss", "selection: "+selection+", skill name: done");
    		return "done";
    	}
    	
		
	  String skill="";
				if (selection.contains(DORMIN_TABLE_STEM)){
			char c = selection.charAt(DORMIN_TABLE_STEM.length());
			int col = c - '1' +1;
		
			int rIdx = selection.indexOf("R");
			char r = selection.charAt(rIdx +1);
			int row = r - '1' +1;


  			if ((col==3 && row==1)){	/*second fraction addition, complex fraction 3 numerator*/	 
				/*** ADD_NUM****/
				if (this.getCommCombo(brController, 2).getValue().equals("Add")){
					skill="add_numerator";
				}
				/*** RED_NUM***/
				else if (this.getCommCombo(brController, 2).getValue().equals("Reduce")) {
					skill="reduce_anumerator";
				}
				/*** SIM_NUM***/
				else{
					skill="simplify_numerator";
				}
							
       	}		
			else if ((col==4 && row==1)){	/*second fraction addition, complex fraction 4 numerator*/	 
					/*** RED_NUM***/
					if (this.getCommCombo(brController, 2).getValue().equals("Reduce")){	
					skill="reduce_anumerator";	
					}
					/*** SIM_NUM***/
					else{
					skill="simplify_numerator";
					}							
       	}
			
			else if ((col==3 || col==4) && row==2){	//second fraction addition, both denominators
					/*** SIN_DEN ****/
					if (this.getCommCombo(brController, 2).getValue().equals("Simplify")){
							skill="simplify_denominator";		
					}
					else if (this.getCommCombo(brController, 2).getValue().equals("Add"))
					{ /*** ADD_DEN / RED_DEN****/
						skill="add_denominator";		
					}
					else{
						skill="reduce_denominator";
					}
			}
			
			else if (col==5 && row==1){	//3rd farction addiiton, first complex fraction numerator
				/*** ADD_NUM****/
				if (this.getCommCombo(brController, 3).getValue().equals("Add")){
					skill="add_numerator";	
				}
				/*** COMPLEX_NUM****/
				else if (this.getCommCombo(brController, 3).getValue().equals("Conversion")){
					skill="complex_numerator";
				}
				/*** SIMPLIFY_NUM****/
				else if (this.getCommCombo(brController, 3).getValue().equals("Simplify")){
					skill="simplify_numerator";
				}
				/*** REDUCE_NUM****/
				if (this.getCommCombo(brController, 3).getValue().equals("Reduce")){	
					skill="reduce_anumerator";		
				}	
					
			}
			
			else if (col==5 && row==2){
				trace.out("******* col5, row2 and combo is " + this.getCommCombo(brController, 3).getValue());
				/*** ADD_DEN****/
				if (this.getCommCombo(brController, 3).getValue().equals("Add")){	
					skill="add_denominator";
				}
				/*** COMPLEX_DEN****/
				else if (this.getCommCombo(brController, 3).getValue().equals("Conversion")){
					skill="reduce_denominator";				
				}
				/*** SIMPLIFY_DEN****/
				else if (this.getCommCombo(brController, 3).getValue().equals("Simplify")){
					skill="simplify_denominator";
				}
				else{
					/*** REDUCE_DEN****/
					skill="reduce_denominator";
				}
						
			}
			else if (col==6){  ///COMPLEX FRACTION 6
				 if (this.getCommCombo(brController, 3).getValue().equals("Reduce")){
					 if (row==1){  /*** RED_NUM */
				 			skill="reduce_anumerator";	
					 }
				 else{ /**** RED_DEN ****/
							skill="reduce_denominator";
				 }
	 			}
	 			else{
	 				if (row==1){
	 				skill="simplify_numerator";
	 				}
	 				else{
	 				skill="simplify_denominator";
	 				}
	 			}
				 
			}
			else if (col==7 && row==2){	//3rd farction addiiton, first complex fraction numerator
				/*** COMPLEX_DEN****/
				if (this.getCommCombo(brController, 4).getValue().equals("Conversion")){
					skill="complex_denominator";
				}
				/*** SIMPLIFY_DEN****/
				else if (this.getCommCombo(brController, 4).getValue().equals("Simplify")){
					trace.out("******* col7, row2, 4rd dropdown complex");
						skill="simplify_denominator";	
					
				}
			
			}
			else if (col==7 && row==1){	//3rd farction addiiton, first complex fraction numerator
				/*** COMPLEX_NUM****/
				if (this.getCommCombo(brController, 4).getValue().equals("Conversion")){
					skill="complex_numerator";
				}
				/*** SIMPLIFY_NUM****/
				else if (this.getCommCombo(brController, 4).getValue().equals("Simplify")){
					skill="simplify_numerator";
				}		
			}
			
		}//end of DORMIN_TABLE
		else if (selection.contains(COMBOFIELD_STEM)){		
					char c = selection.charAt(COMBOFIELD_STEM.length());
					int pos = c - '1' +1;
					if (pos==2){	// first dropdown looks always at the first fraction addition denominators
					
						if (this.getCommCombo(brController, 2).getValue().equals("Simplify")){		//AN EINAI SIMPLIFY 
							skill="simplify";
						}
						else if (this.getCommCombo(brController, 2).getValue().equals("Add")){
						skill="add";
						}
						else if (this.getCommCombo(brController, 2).getValue().equals("Reduce")){
						skill="reduce";
						}		
					}
					else if (pos==3){  
						//an sto proigoumeno einai add tote prepei na kaneis eitei reduce / eite simplify opote
						//koita ta chunks.
						if (this.getCommCombo(brController, 3).getValue().equals("Add")){
							skill="add";
						}
						//an to proigoumeno einia reduce tote prepei na kaneis add opote koita
						// oti kai to SKILL_ADD
						else if (this.getCommCombo(brController, 3).getValue().equals("Reduce")){
							skill="reduce";	
						}	
						else if (this.getCommCombo(brController, 3).getValue().equals("Simplify")){
									skill="simplify";
						}
							//an to proigoumeno simplify kai dialekse reduce
						else if (this.getCommCombo(brController, 3).getValue().equals("Conversion")){
								skill="complex";
						}		
					}
					else if (pos==4) { //last dropdown must be either complex or simplify, so just look at 
						if (this.getCommCombo(brController, 4).getValue().equals("Simplify")){
									skill="simplify";
						}
						else if (this.getCommCombo(brController, 4).getValue().equals("Conversion")){
								skill="complex";
						}
						
					}
			
		}
		else if (selection.contains(TEXTFIELD_STEM)){
			char c = selection.charAt(TEXTFIELD_STEM.length());
			int pos = c - '1' +1;
				if (pos==5){	
					skill="complex_whole";
					
				}
				else if (pos==7){
					skill="complex_whole";		
				}
			
		}





        return skill;
    }
		
		private JCommComboBox getCommCombo(BR_Controller brController,int id) {
		
		String comboFieldName = COMBOFIELD_NAME.replaceAll(TABNUM, ""+id);
		
		JCommComboBox cf = (JCommComboBox)brController.lookupWidgetByName( comboFieldName );
		return cf;
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
}
