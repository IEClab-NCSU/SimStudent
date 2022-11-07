package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.BadExpressionError;
import edu.cmu.old_pact.cmu.sm.Equation;
import edu.cmu.old_pact.cmu.sm.Expression;
import edu.cmu.old_pact.cmu.sm.ExpressionPart;
import edu.cmu.old_pact.cmu.sm.NumberExpression;
import edu.cmu.old_pact.cmu.sm.SymbolManipulator;
import edu.cmu.old_pact.cmu.sm.query.ArrayQuery;
import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.pact.Utilities.trace;

public class Rule {
	protected Test[] conditions;
	protected String[] messages;
	protected String action;
	protected String input;
	private String name;
	private static boolean traceRule=false;  //if true, trace all rules
	private static boolean exprFormatting = true;
	protected boolean traceLocal=false;    //if true, trace this rule
	protected boolean canEncapsulateVar = true; // variable is not encapsulable for x^2=a;
	protected static SymbolManipulator sm = new SymbolManipulator();
	private static boolean debug = false;

	static{
		sm.setMaintainVarList(true);
	}

	//constructor for rule with no help, no conditions
	public Rule(String nme,String act,String inp) {
		name = nme;
		conditions = null;
		messages = null;
		action = act;
		input = inp;
	}
	
	//constructor for rule with no help, multiple conditions
	public Rule (String nme, Test[] cond,String act,String inp) {
		this(nme,act,inp);
		conditions = new Test[cond.length];
		for (int i=0;i<cond.length;++i)
			conditions[i] = cond[i];
	}
	
	//constructor for rule with no help, single test
	public Rule (String nme, Test cond,String act,String inp) {
		this(nme,act,inp);
		conditions = new Test[1];
		conditions[0] = cond;
	}
	
	//constructor for rule with multiple conditions and help
	public Rule(String nme, Test[] cond,String act,String inp,String[] mess) {
		this(nme,cond,act,inp);
		messages = new String[mess.length];
		for (int i=0;i<mess.length;++i)
			messages[i]=mess[i];
		mess = null;
	}
	
	//constructor for rule with one condition and help
	public Rule(String nme, Test cond,String act,String inp,String[] mess) {
		this(nme,cond,act,inp);
		messages = new String[mess.length];
		for (int i=0;i<mess.length;++i)
			messages[i]=mess[i];
		mess = null;
	}
	
	public void setCanEncapsulateVar(boolean c){
		canEncapsulateVar = c;
	}

	public static void setDebug(boolean b){
		debug = b;
	}

	public static boolean debug(){
		return debug;
	}

	public static String[] interpretInput(Queryable info,String ruleInput) {
		String[] evaledInput = new String[] {ruleInput};
		if (ruleInput != null && ruleInput.length() > 1 && ruleInput.charAt(0) == '{') { //if ruleInput starts with a bracket, interpret it
			try {
				Queryable intermediateInput = info.evalQuery(ruleInput.substring(1,ruleInput.length()-1));
				if(intermediateInput instanceof ArrayQuery){
					//trace.out("R.iI: evaled: " + intermediateInput.evalQuery("conjunct"));
					Queryable[] qa = intermediateInput.getArrayValue();
					if(qa == null){
						throw new NoSuchFieldException();
					}
					evaledInput = new String[qa.length];
					for(int i=0;i<qa.length;i++){
						evaledInput[i] = qa[i].getStringValue();
					}
				}
				else{ //assume it's of type StringQuery
					evaledInput = new String[] {intermediateInput.getStringValue()};
				}
			}
			catch (NoSuchFieldException err) {
				/*if(debug){
				  trace.out("R.iI: " + err.toString());
				  }*/
				evaledInput = new String[] {ruleInput}; //if field doesn't exist, maybe bracket was a mistake, so try just plain input
			}
		}
		return evaledInput;
	}
	
	public static RuleMatchInfo testInput(Queryable info,String ruleInput, String userInput) {
		//trace.out("R.tI(" + info + "," + ruleInput + "," + userInput + ")");
		RuleMatchInfo tempRuleMatchInfo = null;
		boolean inputOK = false;
		String [] evaledInput = null;
		int evaledInputIndex = 0;
		if (ruleInput == null && userInput == null)
			inputOK = true;
		else if (ruleInput == null)
			inputOK = false;
		else if (userInput == null)
			inputOK = false;
		else {
			evaledInput=interpretInput(info,ruleInput);
			if(evaledInput != null){
				try {
					for(int i=0;i<evaledInput.length && !inputOK;i++){
						try{
							//trace.out("R.tI: comparing: " + evaledInput[i] + " =?= " + userInput);
							if(sm.algebraicEqual(evaledInput[i],userInput)){
								inputOK = true;
								evaledInputIndex = i;
							}
						}
						catch (ArithmeticException ae){;}
					}
				}
				//If it doesn't parse, just do a string comparison
				catch (BadExpressionError err) {
					for(int i=0;i<evaledInput.length && !inputOK;i++){
						if(evaledInput[i].equalsIgnoreCase(userInput)){
							inputOK = true;
							evaledInputIndex = i;
						}
					}
				}
			}
		}
		if(evaledInput == null || evaledInput.length == 0){
			//trace.out("R.tI: evaledInput is empty");
			tempRuleMatchInfo = new RuleMatchInfo(inputOK,null,null,null);
		}
		else{
			tempRuleMatchInfo = new RuleMatchInfo(inputOK,null,null,evaledInput[evaledInputIndex]);
		}
		return tempRuleMatchInfo;
	}
			
	protected RuleMatchInfo testActionAndInput(Queryable info,String userAction,String userInput) {
		RuleMatchInfo tempRuleMatchInfo = null;
		boolean actionOK = false;
		tempRuleMatchInfo = testInput(info,input,userInput);
		if (isTraced()){
			trace.out("  input for "+name+" matches: "+tempRuleMatchInfo.getBoolean()+"; evaled is {"+interpretInput(info,input)+", ...} ["+input+"]");
		}
		if (tempRuleMatchInfo.getBoolean()) {
			if (action == null)
				actionOK = true;
			else
				actionOK = action.equalsIgnoreCase(userAction);
			if (isTraced())
				trace.out("   action for "+name+"["+userAction+"] matches: "+actionOK);
		}
		tempRuleMatchInfo.setAction(action);
		tempRuleMatchInfo.setBoolean(tempRuleMatchInfo.getBoolean() && actionOK);
		return tempRuleMatchInfo;
	}
	
	public RuleMatchInfo canFire(Queryable info,String userAction,String userInput) {
		//trace.out("in canFire Rule name = "+name);
		RuleMatchInfo tempRuleMatchInfo = null;
		/*many rules will fail the action/input test, and it's a lot
          faster than checking the conditions since it usually doesn't
          involve performing queries and such, so we'll do it first*/
		tempRuleMatchInfo = testActionAndInput(info,userAction,userInput);
		if(tempRuleMatchInfo.getBoolean()){
			tempRuleMatchInfo.setBoolean(testConditionsForHelp(info));
		}
		return (tempRuleMatchInfo);
	}
	
	public boolean testConditionsForHelp(Queryable info) {
		boolean OK=true;
		if (isTraced())
			trace.out("  Testing rule: "+name);
		for (int i=0;i<conditions.length && OK;++i) {
			if (isTraced())
				trace.out("   testing condition "+conditions[i]+"; passes: "+conditions[i].passes(info));
			if (!(conditions[i].passes(info,true)))
				OK = false;
		}
		if (isTraced())
			trace.out("   All conditions for "+name+" pass: "+OK);
		return OK;
	}

	public String[] getMessages(Equation info) {
		if(messages != null){
			boolean oldEncapsulateLeft = (info.getLeft()).getEncapsulateVar();
			boolean oldEncapsulateRight = (info.getRight()).getEncapsulateVar();

			(info.getLeft()).setEncapsulateVar(canEncapsulateVar);
			(info.getRight()).setEncapsulateVar(canEncapsulateVar);
			String resolvedMessages[] = new String[messages.length];
			for (int i=0;i<messages.length;++i) {
				resolvedMessages[i] = resolveMessage(messages[i],info);
			}
			if (isTraced()){
				resolvedMessages[0] = name+": "+resolvedMessages[0];
			}

			(info.getLeft()).setEncapsulateVar(oldEncapsulateLeft);
			(info.getRight()).setEncapsulateVar(oldEncapsulateRight);
			return resolvedMessages;
		}
		else{
			trace.out("Rule.getMessages: Warning: rule '" + name + "' fired for help but has no messages");
			return new String[] {"Sorry, I can't help you here."};
		}
	}
	
	//messages can have embedded scripts in them (between brackets). resolveMessage returns a string evaluated in the
	//context of the problem
	public static String resolveMessage(String message,Queryable info) {
		sm.setPrintDecimalPlaces(NumberExpression.defaultMathMLDecimalPlaces);
		//resolveMessage should look for embedded properties and resolve them
		int phraseStart = message.indexOf('{');
		if (phraseStart >= 0) {
			int phraseEnd = message.indexOf('}',phraseStart+1);
			if (phraseEnd > 0) {
				String embedded = message.substring(phraseStart+1,phraseEnd);
				String result=null;
				try {
					if(exprFormatting){
						/*mmmBUG: queries like "item 1 of variables of
                          variable side expression" return a
                          StringQuery (not a VariableExpression), so
                          they won't show up as formatted expressions
                          (which for now means that they're not
                          displayed in the same font as constants in
                          the help messages).  It may be safe to
                          assume that anything that comes out of this
                          query is an expression, but it might not.*/
						Queryable res = info.evalQuery(embedded);
						if(res instanceof Expression){
							result = ((Expression)res).toIntermediateString();
							result = "<expression>" + result + "</expression>";
						}
						else if(res instanceof Equation){
							result = ((Equation)res).getLeft().toIntermediateString();
							result += " = ";
							result += ((Equation)res).getRight().toIntermediateString();
							result = "<expression>" + result + "</expression>";
						}
						else if(res instanceof ExpressionPart){
							/*there's no way to get to the Expression
                              object inside of this ExpressionPart,
                              but it probably isn't anything complex
                              (likely a TermInPoly), so we should be
                              okay not calling toIntermediateString on
                              it.*/
							result = res.getStringValue();
							result = "<expression>" + result + "</expression>";
						}
						else{
							result = res.getStringValue();
						}
					}
					else{
						result = info.evalQuery(embedded).getStringValue();
					}
				}
				catch(NoSuchFieldException err) {
					trace.out("***can't interpret "+embedded+" in message: "+err);
					result = "-something-";
				}
				sm.setPrintDecimalPlaces(NumberExpression.defaultPrintDecimalPlaces);
				return (message.substring(0,phraseStart)+result+resolveMessage(message.substring(phraseEnd+1),info));
			}
			else {
				sm.setPrintDecimalPlaces(NumberExpression.defaultPrintDecimalPlaces);
				return message;
			}
		}
		else {
			sm.setPrintDecimalPlaces(NumberExpression.defaultPrintDecimalPlaces);
			return message;
		}
	}
	
	//checkRule is useful for debugging. It just runs the rule on the given equation
	public void checkRule(String equation,String action,String input) {
		boolean holdTrace = traceLocal;
		try {
			Equation info = new Equation(equation);
			setTraceRule(true);
			RuleMatchInfo tempRuleMatchInfo = canFire(info,action,input);
			if (tempRuleMatchInfo.getBoolean() == true) {
				//trace.out("Rule "+name+" fires with "+equation+" "+action+" "+input+", messages are:");
				String[] messages = getMessages(info);
				for (int i=0;i<messages.length;++i)
					trace.out(messages[i]);
			}
			else
				trace.out("Rule "+name+" fails");
		}
		catch (BadExpressionError err) {
			trace.out("Equation "+equation+" does not parse, testing rule "+name);
		}
		traceLocal = holdTrace;
	}
	
	public boolean hasMessages(){
		return !((messages == null) || (messages.length == 0));
	}

	public String getName() {
		return name;
	}
	
	public String getAction() {
		return action;
	}
	
	public String getInput() {
		return input;
	}
	
	public static void setTraceAllRules(boolean onOrOff) {
		traceRule = onOrOff;
	}
	
	public void setTraceRule(boolean onOrOff) {
		traceLocal = onOrOff;
	}
	
	static public boolean allRulesTraced() {
		return traceRule;
	}
	
	
	/**
	   * Get the value of exprFormatting.
	   * @return Value of exprFormatting.
	   */
	public boolean getExprFormatting() {return exprFormatting;}
	
	/**
	   * Set the value of exprFormatting.
	   * @param v  Value to assign to exprFormatting.
	   */
	public void setExprFormatting(boolean  v) {Rule.exprFormatting = v;}
	

	public boolean isTraced() {
		return (traceLocal || traceRule);
	}

	public String toString(){
		return "[Rule: " + name + " (" + action + "," + input + ")]";
	}
}
