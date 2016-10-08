package edu.cmu.pact.miss.minerva_3_1;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class Test {

	private Map<String, String> signedAbstractionConstants = new HashMap<String, String>();
	private Map<String, String> noSignAbstractionConstants = new HashMap<String, String>();
	private int signNextConstant = 65;
	private int noSignNextConstant = 65;

	public Map<String, String> abstractionConstantsWithSign(String equation, Map<String, String> hm) {
		
		String constantized = "";
		String value = "";
		boolean flag = false;

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
				hm.put(constantized, value);
				flag = true;
				this.signNextConstant++;
			}
			
			if(flag) {
				constantized = "";
				value = "";
				flag = false;
			}	
		}

		return hm;
	}
	
	public Map<String, String> abstractionConstantsWithNoSign(String equation, Map<String, String> hm) {
		
		String constantized = "";
		String value = "";
		boolean flag = false;

		for (int i = 0; i < equation.length(); i++) {
			while(i < equation.length() && Character.isDigit(equation.charAt(i)) || (i < equation.length() && equation.charAt(i) == '.')) {
				value +=  equation.charAt(i);
				++i;
			}
			if(value != "") {
				constantized += (char)this.noSignNextConstant;
				hm.put(constantized, value);
				flag = true;
				this.noSignNextConstant++;
			}
			
			if(flag) {
				constantized = "";
				value = "";
				flag = false;
			}	
		}

		return hm;
	}
	
	public String signedContextStepAbstraction(String step, Map<String, String> map) {
		
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
					if(Character.isDigit(step.charAt(i+1)) || step.charAt(i+1) == '.')
						++i;
					else
						break;
				}

				if (((i == step.length() - 1) || !Character
						.isDigit(step.charAt(i + 1)))) {
					boolean exists = isConstantInHashMap(digit, noSignAbstractionConstants);
					if(exists) {
						Set keyset = noSignAbstractionConstants.keySet();
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
	

	public String noSignContextStepAbstraction(String step, Map<String, String> map) {
		
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
					boolean exists = isConstantInHashMap(digit, noSignAbstractionConstants);
					if(exists) {
						Set keyset = noSignAbstractionConstants.keySet();
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

	public static void main(String[] args) {

		String signedContextStepAbs = "";
		String noSignContextStepAbs = "";
		//String problem_Name = "-3.42+(-9.98y)=-5.93y+8.18";
		//String step_Name = "-11.6-9.98y=-5.93y";
		//String problem_Name = "-9x+(-5)=19+3x";
		//String step_Name = "-9x-24=3x";
		//String problem_Name = "-5.01+(-8.41x)=-4.79+7.09x";
		//String step_Name = "-0.22-8.41x=7.09x";
		String problem_Name = "14+7y=8+5y";
		String step_Name = "6=-2y"; 
		Test test = new Test();
		test.signedAbstractionConstants = test.abstractionConstantsWithSign(problem_Name, test.signedAbstractionConstants);
		test.noSignAbstractionConstants = test.abstractionConstantsWithNoSign(problem_Name, test.noSignAbstractionConstants);
		signedContextStepAbs = test.signedContextStepAbstraction(step_Name, test.noSignAbstractionConstants);
		System.out.println("signedContextStepAbs :" + signedContextStepAbs);
		noSignContextStepAbs = test.noSignContextStepAbstraction(step_Name, test.signedAbstractionConstants);
		System.out.println("noSignContextStepAbs :" + noSignContextStepAbs);
	}
	
	
/*	public static boolean validate(double v) {
		return (Math.floor(v) != v);
	}

	public static String removeNegatives(String equation) {

		String newEquation = "";

		for (int i = 0; i < equation.length(); i++) {
			if (i == 0 && equation.charAt(0) == '-'
					|| equation.charAt(i) == '(' || equation.charAt(i) == ')') {
				// ignores non-number characters at beginning of equation
				// awlee: why the inclusion of parenthesis?
			}  else if (i > 0 && equation.charAt(i) == '-') {
				if (equation.charAt(i - 1) != '+'
						&& equation.charAt(i - 1) != '/'
						&& equation.charAt(i - 1) != '(' && equation.charAt(i-1) != ' ') {
					//newEquation += equation.charAt(i);
					newEquation += '+';
				}
			} else {
				newEquation += (equation.charAt(i));
			}
		}
		System.out.println("newEquation: " + newEquation);
		return newEquation;
	}
	
	public static void main(String[] args) {
		
		if(validate(-4.1)) {
			System.out.println("Number is a decimal");
		} else {
			System.out.println("Number is not a decimal");
		}
		
		String equation = "Av+B  =  Cv+(D)";
		System.out.println(removeNegatives(equation));		
		System.out.println("4.1x-2=10".indexOf('.'));
	}
*/
}
