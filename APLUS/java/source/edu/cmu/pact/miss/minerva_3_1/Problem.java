/*
 * 	Andrew Lee
 * 	May 02 2009
 * 	Stores steps of a math problem and relevant statistics.
 */
package edu.cmu.pact.miss.minerva_3_1;

import java.util.*;

public class Problem extends LinkedList<Step> {
	private String problem_Name;
	private String abstractionContext/*, abstractionAbsolute, abstractionSimple;*/;
	private String signedNoContext, signedContext, noSignWithContext, noSignNoContext;
	private String signedAbstraction, unsignedAbstraction;
	
	private Map<String, String> abstractionConstants = new HashMap<String, String>();
	private Map<String, String> signedAbstractionConstants = new HashMap<String, String>();
	private Map<String, String> noSignAbstractionConstants = new HashMap<String, String>();
	private ProblemAbstractor p = new ProblemAbstractor();
	private StepAbstractor stepAbstractor = new StepAbstractor();
	
	// A variable name used for abstraction
	private char nextConstant = 'A';
	String getNextVar() {
	    return "" + nextConstant++;
	}

	public Problem(String problem_Name) {

		this.problem_Name = problem_Name;
		// trace.out("Problem: original problem_name = |" + problem_Name + "|");
		String[] str = problem_Name.split(" ");
		problem_Name = "";
		for(int i=1; i<str.length; i++) {
			problem_Name += str[i];
		}
		// trace.out("Problem: trimmed problem_name = |" + problem_Name + "|");
		
		
		signedAbstraction = stepAbstractor.signedAbstraction(problem_Name);
		unsignedAbstraction = stepAbstractor.unsignedAbstraction(problem_Name);
		
		signedNoContext = p.abstractEquationAbsolute(problem_Name);
		signedAbstractionConstants = abstractionConstantsWithSign(problem_Name, signedAbstractionConstants);
		// trace.out("SignedNoContext: " + signedNoContext);

		noSignNoContext = p.abstractEquationSimple(problem_Name);
		noSignAbstractionConstants = abstractionConstantsWithNoSign(problem_Name, noSignAbstractionConstants);
		// trace.out("NoSignNoContext: " + noSignNoContext);

		abstractionContext = p.abstractEquationContext(problem_Name /*problem_Name.substring(5)*/, abstractionConstants, (char)nextConstant);
		//abstractionAbsolute = p.abstractEquationAbsolute(problem_Name /*problem_Name.substring(5)*/);
		//abstractionSimple = p.abstractEquationSimple(problem_Name /*problem_Name.substring(5)*/);
		// substrings taken to exclude problem identification codes
	}

	public String previousStepEquationAbsolute(String stepName) {
		Iterator<Step> itr = this.iterator();
		String previousEquation = "";

		while (itr.hasNext()) {
			Step thisStep = itr.next();

			if (thisStep.getStep_Name().equals(stepName)) {
				return previousEquation;
			} else {
				previousEquation = thisStep.getSignedAbstraction();
			}
		}

		return previousEquation;
	}

	public String previousStepEquationContext(String stepName) {
		Iterator<Step> itr = this.iterator();
		String previousEquation = "";

		while (itr.hasNext()) {
			Step thisStep = itr.next();

			if (thisStep.getStep_Name().equals(stepName)) {
				return previousEquation;
			} else {
				previousEquation = thisStep.getEquationAbstractionContext();
			}
		}

		return previousEquation;
	}

	public void addStep(Step inputStep) {
		abstractionConstants = inputStep.abstractEquationContext(
				abstractionConstants, nextConstant);
		abstractionConstants = inputStep.abstractInputContext(
				abstractionConstants, nextConstant);

		add(inputStep);
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

    /*
    public Map<String, String> abstractionConstantsWithSign(String equation, Map<String, String> hm) {

        String constantized = "";
        String value = "";
        boolean flag = false;

        for (int i = 0; i < equation.length(); i++) {

            trace.out(i);
            while (i < equation.length() && Character.isDigit(equation.charAt(i))) {
                value += equation.charAt(i);
                ++i;
                trace.out("Inside while: " + i);
            }

            if (i < equation.length() && equation.charAt(i) == '-' && Character.isDigit(equation.charAt(i + 1))) {
                while (i < equation.length() && Character.isDigit(equation.charAt(++i))) {
                    value += equation.charAt(i);
                    ++i;
                    trace.out("Inside while: " + i);
                }
            }
            
            if (value != "") {
                constantized += (char) this.nextConstant;
                hm.put(constantized, value);
                flag = true;
                this.nextConstant++;
            }

            if (flag) {
                constantized = "";
                value = "";
                flag = false;
            }
        }

        return hm;
    }
     */
	
	public Map<String, String> abstractionConstantsWithNoSign(String equation, Map<String, String> hm) {
		
		String constantized = "";
		String value = "";
		boolean flag = false;

		for (int i = 0; i < equation.length(); i++) {
			// trace.out(i);
			while(i < equation.length() && Character.isDigit(equation.charAt(i))) {
				value +=  equation.charAt(i);
				++i;
				// trace.out("Inside while: " + i);
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
	
	public String getSideAbstractionContext(String expression){
		Map<String, String> sideAbstractionConstants = new HashMap<String, String>();
		return(p.abstractEquationContext(expression,sideAbstractionConstants, this.nextConstant));
	}
	
	public String getName() {
		return problem_Name;
	}

	public String getSignedNoContext() {
		return signedNoContext;
	}

	public String getSignedContext() {
		return signedContext;
	}

	public String getNoSignWithContext() {
		return noSignWithContext;
	}

	public String getNoSignNoContext() {
		return noSignNoContext;
	}

	public Map<String, String> getAbstractionConstants() {
		return abstractionConstants;
	}

	public String getSignedAbstraction() {
	    return signedAbstraction;
	}

	public String getUnsignedAbstraction() {
	    return unsignedAbstraction;
	}

	public String getAbstractionContext(){
		return abstractionContext;
	}
}
