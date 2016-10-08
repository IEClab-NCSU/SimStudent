/*
 * 	Andrew Lee
 * 	May 02 2009
 * 	Stores the individual step of a math problem and relevant statistics.
 */
package edu.cmu.pact.miss.minerva_3_1;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Step {
	private String step_Name;
	private int attempt_At_Step;
	private String outcome;
	private String action;
	private String input;
	private String feedback_Classification;
	private String equationAbstractionContext, signedAbstraction;
	private String signedInputAbstractionContext, unsignedInputAbstractionContext, inputAbstractionAbsolute;
	private String unsignedAbstraction, inputAbstractionSimple;
	
	
	private Map<String, String> signedAbstractionConstants = new HashMap<String, String>();
	private Map<String, String> unsignedAbstractionConstants = new HashMap<String, String>();
	
	// A variable name used for abstraction
	private char nextConstant = 'A';
	String getNextVar() {
	    return "" + nextConstant++;
	}

	private StepAbstractor s = new StepAbstractor();

        public Step(String step_Name) {
            this(step_Name, "", "", "", "", "");
        }
        
        public Step(String step_Name, String attempt_At_Step, String outcome,
                String action, String input, String feedback) {
	    
	    // System.out.print("Step(): step_Name=" + step_Name + ", sttempt_At_Step=" + attempt_At_Step + ", outcome=" + outcome + ", action=" + action + ", input=" + input +"\n");
	    
		this.step_Name = step_Name;
		this.attempt_At_Step = 0;
		if (attempt_At_Step != "") this.attempt_At_Step = Integer.parseInt(attempt_At_Step);
		this.outcome = outcome;
		this.action = action;
		this.input = input;
		this.feedback_Classification = feedback;
		
		this.signedAbstractionConstants = abstractionConstantsWithSign(step_Name, signedAbstractionConstants);
		this.unsignedAbstractionConstants = abstractionConstantsWithNoSign(step_Name, unsignedAbstractionConstants);

		/*
		 * We abstract and store not only the 'step' equation
		 * but also the 'input' equation 
		 */
		if(step_Name.indexOf('=') != -1){
			signedAbstraction = s.signedAbstraction(step_Name);
			signedInputAbstractionContext = s.abstractSignedInputSimpleContext(signedAbstractionConstants, step_Name);
			unsignedInputAbstractionContext = s.abstractUnsignedInputSimpleContext(unsignedAbstractionConstants, step_Name);
		}
		// System.out.println("Step " + step_Name + "  " + "SignedNoContext: " + equationAbstractionAbsolute);
		//inputAbstractionAbsolute = action + " " + s.abstractInputAbsolute(input, step_Name);
		//inputAbstractionAbsolute = action + " " + s.abstractInputAbsolute(input);

		if(step_Name.indexOf('=') != -1)
			unsignedAbstraction = s.unsignedAbstraction(step_Name);
		// System.out.println("Step " + step_Name + "  " + "NoSignNoContext: " + equationAbstractionSimple);
		//inputAbstractionSimple = action + " " + s.abstractInputSimple(input);
	}
        
    public Map<String, String> abstractionConstantsWithSign(String equation, Map<String, String> hm) {
        
        boolean onTheValue = false;
        String value = "";
        
        for (int i= 0; i < equation.length(); i++ ) {
            
            char c = equation.charAt(i);
            
            if ((c=='-' && !onTheValue) || Character.isDigit(c)) {
                value += c;
                onTheValue = true;
            } else if (c=='-' && onTheValue) {
                hm.put(getNextVar(), value);
                value = "" + c;
            } else if (onTheValue) {
                hm.put(getNextVar(), value);
                value = "";
                onTheValue = false;
                }
            }
        return hm;
    }   
    
    public Map<String, String> abstractionConstantsWithNoSign(String equation, Map<String, String> hm) {
		
		String constantized = "";
		String value = "";
		boolean flag = false;

		for (int i = 0; i < equation.length(); i++) {
			// System.out.println(i);
			while(i < equation.length() && Character.isDigit(equation.charAt(i))) {
				value +=  equation.charAt(i);
				++i;
				// System.out.println("Inside while: " + i);
			}
			if(value != "") {
				constantized += (char)this.nextConstant;
				hm.put(constantized, value);
				flag = true;
				this.nextConstant++;
			}
			
			if(flag) {
				constantized = "";
				value = "";
				flag = false;
			}	
		}

		return hm;
	}
	
	public Map<String, String> abstractInputContext(
			Map<String, String> abstractionConstants, int nextConstant) {
		String newEquation = Abstractor.abstractVars(input);
		newEquation = s.abstractNumbersContext(newEquation,
				abstractionConstants, (char)nextConstant);
		signedInputAbstractionContext = action + " " + newEquation;
		System.out.println("Error Step.java line 134");
		return abstractionConstants;
	}


	public Map<String, String> abstractEquationContext(
			Map<String, String> abstractionConstants, int nextConstant) {

	    String newEquation = Abstractor.abstractVars(step_Name);
	    // System.out.println("abstractEquationContext: " + newEquation);
	    
	    newEquation = s.assignLHS(newEquation) + " = " + s.assignRHS(newEquation);
	    // System.out.println("abstractEquationContext: " + newEquation);
	    
	    newEquation = s.abstractNumbersContext(newEquation, abstractionConstants, (char)nextConstant);
	    // System.out.println("abstractEquationContext: " + newEquation);
	    
	    equationAbstractionContext = newEquation;
	    
	    return abstractionConstants;
	}

	/*
	public Step(String step_Name) {
		this.step_Name = step_Name;
	}
	*/
	
	public String getSignedSideAbstractionContext(String expression){
		Map<String, String> sideSignedAbstractionConstants = new HashMap<String, String>();	

		return(s.abstractSignedInputSimpleContext(abstractionConstantsWithSign(expression, sideSignedAbstractionConstants), expression));
		
	}
	
	public String getUnsignedSideAbstractionContext(String expression){
		Map<String, String> sideUnsignedAbstractionConstants = new HashMap<String, String>();	
		return(s.abstractUnsignedInputSimpleContext(abstractionConstantsWithNoSign(expression, sideUnsignedAbstractionConstants), expression));
	}

	public String getStep_Name() {
		return step_Name;
	}

	public int getAttempt_At_Step() {
		return attempt_At_Step;
	}

	public String getOutcome() {
		return outcome;
	}

	public String getInput() {
		return input;
	}

	public String getAction() {
		return action;
	}

	public String getFeedback_Classification() {
		return feedback_Classification;
	}

	public String getEquationAbstractionContext() {
		return equationAbstractionContext;
	}

	public String getSignedAbstraction() {
		return signedAbstraction;
	}

	public String getUnsignedAbstraction() {
		return unsignedAbstraction;
	}

	public String getSignedInputAbstractionContext() {
		return signedInputAbstractionContext;
	}
	
	public String getUnsignedInputAbstractionContext() {
		return unsignedInputAbstractionContext;
	}

	public String getInputAbstractionAbsolute() {
		return inputAbstractionAbsolute;
	}

	public String getInputAbstractionSimple() {
		return inputAbstractionSimple;
	}

	@Override
	public String toString() {
		return getStep_Name() + " " + getOutcome();
	}

	public boolean isAnomaly() {
		if (outcome.equals("BUG") || outcome.equals("ERROR"))
			return true;

		return false;
	}
	
	
	//private Map<String, String> signedAbstractionConstants = new HashMap<String, String>();
	//private Map<String, String> noSignAbstractionConstants = new HashMap<String, String>();
	private int signNextConstant = 65;
	private int noSignNextConstant = 65;

	public void abstractionConstantsWithSign(String equation) {
		
		String constantized = "";
		String value = "";
		boolean flag = false;
		equation = equation.substring(5);
		
		for (int i = 0; i < equation.length(); i++) {
			while(i < equation.length() && Character.isDigit(equation.charAt(i)) || (i < equation.length() && equation.charAt(i)== '.')) {
				value +=  equation.charAt(i);
				++i;
			}

			if(i < equation.length() && equation.charAt(i) == '-' && Character.isDigit(equation.charAt(i+1))) {
				value += equation.charAt(i);
				++i;
				while((i < equation.length() && Character.isDigit(equation.charAt(i))) || (i < equation.length() && equation.charAt(i)== '.')) {
					value +=  equation.charAt(i);
					++i;
				}	
			}
			if(value != "") {
				constantized += (char)this.signNextConstant;
				signedAbstractionConstants.put(constantized, value);
				flag = true;
				this.signNextConstant++;
			}
			
			if(flag) {
				constantized = "";
				value = "";
				flag = false;
			}	
		}

	}
	
	public void abstractionConstantsWithNoSign(String equation) {
		
		String constantized = "";
		String value = "";
		boolean flag = false;
		equation = equation.substring(5);

		for (int i = 0; i < equation.length(); i++) {
			while(i < equation.length() && Character.isDigit(equation.charAt(i)) || (i < equation.length() && equation.charAt(i) == '.')) {
				value +=  equation.charAt(i);
				++i;
			}
			if(value != "") {
				constantized += (char)this.noSignNextConstant;
				unsignedAbstractionConstants.put(constantized, value);
				flag = true;
				this.noSignNextConstant++;
			}
			
			if(flag) {
				constantized = "";
				value = "";
				flag = false;
			}	
		}
	}
	
	public String signedContextStepAbstraction(String step) {
		
		final Map<String, String> map = unsignedAbstractionConstants;
		int size = map.size();
		int nextConstant =  65 + size;
		String constantized = "";
		String digit = "";
		
		for (int i = 0; i < step.length(); i++) {
			
			if (!Character.isDigit(step.charAt(i))) {
				constantized += step.charAt(i);
			} else { 
				digit = "";
				while(i < step.length() && Character.isDigit(step.charAt(i)) || (i < step.length() && step.charAt(i) == '.')) {
					digit +=  step.charAt(i);
					if(i+1 < step.length() && (Character.isDigit(step.charAt(i+1)) || step.charAt(i+1) == '.'))
						++i;
					else
						break;
				}

				if (((i == step.length() - 1) || !Character
						.isDigit(step.charAt(i + 1)))) {
					boolean exists = isConstantInHashMap(digit, unsignedAbstractionConstants);
					if(exists) {
						Set keyset = unsignedAbstractionConstants.keySet();
						Iterator itr = keyset.iterator();
						while(itr.hasNext()) {
							Object key = itr.next();
							if(map.get(key).equalsIgnoreCase(digit))
								constantized += key.toString();
						} 
					} else {
						constantized += (char) nextConstant;
						nextConstant++; 
					}
				}
			}

		}
		
		return constantized;
	} 
	
	boolean isConstantInHashMap(String digit, Map<String, String> map) {
		
		boolean exists = false;
		Set keyset = map.keySet();
		Iterator itr = keyset.iterator();
		while(itr.hasNext()) {
			if(map.get(itr.next()).equalsIgnoreCase(digit))
				return true;
		} 
		return exists;
	}
	

	public String noSignContextStepAbstraction(String step) {
		
		final  Map<String, String> map = signedAbstractionConstants;
		int size = map.size();
		int nextConstant =  65 + size;
		String constantized = "";
		String digit = "";
		
		for (int i = 0; i < step.length(); i++) {
			
			if(step.charAt(i) == '-' && Character.isDigit(step.charAt(i+1)) || Character.isDigit(step.charAt(i))) {
				String value = "";
				value += step.charAt(i);
				++i;
				int count = 0;
				while((i < step.length() && Character.isDigit(step.charAt(i))) || (i < step.length() && step.charAt(i) == '.') ) {
					value +=  step.charAt(i);
					++i;
					++count;
				}
				if(count < 1 && value.indexOf('-') != -1) { // value is just - which means we can assume it to be -1
					value += 1;
				}
				
				boolean exists = isConstantInHashMap(value, signedAbstractionConstants);
				if(exists) {
					Set keyset = signedAbstractionConstants.keySet();
					Iterator itr = keyset.iterator();
					while(itr.hasNext()) {
						Object key = itr.next();
						if(map.get(key).equalsIgnoreCase(value)) {
							if(constantized.equalsIgnoreCase(""))
								constantized += key.toString(); 
							else {
								if(constantized.indexOf('=') == constantized.length()-1 )
									constantized += key.toString();
								else if(constantized.indexOf('(')!=constantized.length()-1 && constantized.indexOf(')')!= constantized.length()-1)
									constantized += "+" + key.toString();
								else 
									constantized += key.toString();
							}
						}
					} 
				} else {
					if(constantized.equalsIgnoreCase(""))
						constantized += (char) nextConstant;
					else {
						if(constantized.indexOf('=') == constantized.length()-1 )
							constantized += (char) nextConstant;
						else 
							constantized += "+" + (char) nextConstant;
					}
					nextConstant++;
					--i;
					continue;
				}
			}


			if (i < step.length() && !Character.isDigit(step.charAt(i))) {
				constantized += step.charAt(i);
			} else if (i < step.length()) {
				if (((i == step.length() - 1) || !Character
						.isDigit(step.charAt(i + 1)))) {
					digit += ""+step.charAt(i);
					boolean exists = isConstantInHashMap(digit, unsignedAbstractionConstants);
					if(exists) {
						Set keyset = unsignedAbstractionConstants.keySet();
						Iterator itr = keyset.iterator();
						while(itr.hasNext()) {
							Object key = itr.next();
							if(map.get(key).equalsIgnoreCase(digit)) {
								constantized += key.toString();
								digit = "";
							}
						} 
					} else 
						if(constantized.equalsIgnoreCase(""))
							constantized += (char) nextConstant;
						else {
							if(constantized.indexOf('=') == constantized.length()-1 )
								constantized += (char) nextConstant;
							else 
								constantized += "+" + (char) nextConstant;
						}
						nextConstant++;
				} else if(Character.isDigit(step.charAt(i)))
					digit += step.charAt(i);
			}

		}
		
		return constantized;
	}

}
