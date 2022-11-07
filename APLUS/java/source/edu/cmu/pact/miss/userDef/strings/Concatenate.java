package edu.cmu.pact.miss.userDef.strings;

import java.text.DecimalFormat;
import java.util.Vector;

import cl.utilities.sm.Expression;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class Concatenate extends EqFeaturePredicate{
	
	
	public Concatenate(){
		setArity(2);
		setName("concatenate");
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP,TYPE_ARITH_EXP});
		
	/*	setArity(1);
		setName("concatenate");
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP});
		
		*/
	/*	setArity(3);
		setName("concatenate");
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP,TYPE_ARITH_EXP,TYPE_ARITH_EXP});
		
		*/
	}

	public String apply(Vector args) 
	{
		String expString1 = (String)args.get(0);
		String expString2 = (String)args.get(1);
		
		return (String) ""+expString1+"-"+expString2+"";
			
	
	/*	
		String eq = (String)args.get(0);
		
		
		
		eq=eq.replace("lb/g", "");
    	eq=eq.replace("g", "");
    	eq=eq.replace("lb", "");
    	
		//trace.out("Equation: " + eq);
		
		
		
		if (eq.contains("/")){		//Contains / means we are either converting units OR computing max weight
					
			if (eq.contains("*")){		//Equation is given in format bla*1/4 
				
				String[] tmp=eq.split("/|\\*");
				
				
				int term1 = Integer.parseInt(tmp[0]);
			    int term2 =(Integer.parseInt(tmp[2]));
			      
			    float result=(float) term1/term2;
			      

			      return String.valueOf(result);
			      
			}
			else{								//Equation is given in format bla/4 
				 String[] tmp=eq.split("/");
				
				  int term1 = Integer.parseInt(tmp[0]);
			      int term2 =(Integer.parseInt(tmp[1]));
			        
			        
			      float result=(float) term1/term2;
			      
			      
			      return String.valueOf(result);
				
			}
								
		}
		
		else if (eq.contains("+")){ 					//addition 
				String[] tmp=eq.split("\\+");
				float term1 = Float.parseFloat(tmp[0]);
				float term2 = Float.parseFloat(tmp[1]);
				
				 float result=(float) term1+term2;
				 
				 DecimalFormat df = new DecimalFormat("###.###");
				 return (String) df.format(result);
				 
			
				
		}	
		else{								//	Unit Conversion (e.g. multiplication) 
			
			eq=eq.replace("/", "");
				String[] tmp=eq.split("\\*");
				float term1 = Float.parseFloat(tmp[0]);
				float term2 = Float.parseFloat(tmp[1]);
			
				float result=(float) term1*term2;
			 
				
				 DecimalFormat df = new DecimalFormat("###.###");
				 return (String) df.format(result);
				 
				 
				
		}
		*/
		
	/*	String expString1 = (String)args.get(0);
		String expString2 = (String)args.get(1);
		String expString3 = (String)args.get(2);
		
		expString3=expString3.replaceAll("1", "");		//remove 1 from 1g or 1lb
		
		
		return (String) ""+expString1+"*"+expString2+"/"+expString3;
		*/
		
		
	}
}


