package edu.cmu.pact.miss;

import java.util.Vector;

public class GradedInstruction extends Instruction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GradedInstruction(String sai) {
		super(sai);
    	setFocusOfAttention( new Vector() );
    	addFocusOfAttention( sai );
     }
	
	// Tasmia adding equals function to check if same graded instruction is already present or not
    @Override
    public boolean equals(Object v) {
          if (v instanceof GradedInstruction){
        	  GradedInstruction cur_instruction_example = (GradedInstruction) v;
        	  for (int i = 0; i < cur_instruction_example.getFocusOfAttention().size(); i++) {
        		  	String prev = (String)this.getFocusOfAttention().get(i);
        		  	String cur = (String)cur_instruction_example.getFocusOfAttention().get(i);
        		    if(!prev.equals(cur)) {
        		    	return false;
        		    }
        		}
              
          }

       return true;
    }
}
