package edu.cmu.old_pact.cmu.solver.ruleset;



/*This class contains all rules, bug rules, skill rules, typein bug
  rules, and typein skill rules.  (At some point it would be a good
  idea to split these up.)  The RuleDefiner class allows us to
  initialize the rule sets asynchronously, so that startup is
  faster.*/

public class RuleDefiner implements Runnable{
	private static Object mutex = new Object();
	private static RuleSet strategicRules = null;
	private static BugRuleSet strategicBugRules = null;
	private static TypeinBugRuleSet typeinBugRules = null;
	private static SkillRuleSet skillRules = null;
	private static TypeinSkillRuleSet typeinSkillRules = null;
	private static Thread definerThread = null;

	/*get*Rules returns the appropriate rule set.  If wait is true and
      the rules are still being defined, it will wait until the
      definition thread is finished; otherwise it returns null
      immediately.*/
	public static RuleSet getStrategicRules(boolean wait){
		synchronized(mutex){
			if(definerThread != null && definerThread.isAlive()){
				if(wait){
					try{
						//dT might have died already ...
						while(definerThread.isAlive()){
							mutex.wait(100);
						}
					}
					catch(InterruptedException ie){
						return null;
					}
				}
				else{
					return null;
				}
			}
			return strategicRules;
		}
	}

	public static BugRuleSet getStrategicBugRules(boolean wait){
		synchronized(mutex){
			if(definerThread != null && definerThread.isAlive()){
				if(wait){
					try{
						//dT might have died already ...
						while(definerThread.isAlive()){
							mutex.wait(100);
						}
					}
					catch(InterruptedException ie){
						return null;
					}
				}
				else{
					return null;
				}
			}
			return strategicBugRules;
		}
	}

	public static TypeinBugRuleSet getTypeinBugRules(boolean wait){
		synchronized(mutex){
			if(definerThread != null && definerThread.isAlive()){
				if(wait){
					try{
						//dT might have died already ...
						while(definerThread.isAlive()){
							mutex.wait(100);
						}
					}
					catch(InterruptedException ie){
						return null;
					}
				}
				else{
					return null;
				}
			}
			return typeinBugRules;
		}
	}

	public static SkillRuleSet getSkillRules(boolean wait){
		synchronized(mutex){
			if(definerThread != null && definerThread.isAlive()){
				if(wait){
					try{
						//dT might have died already ...
						while(definerThread.isAlive()){
							mutex.wait(100);
						}
					}
					catch(InterruptedException ie){
						return null;
					}
				}
				else{
					return null;
				}
			}
			return skillRules;
		}
	}

	public static TypeinSkillRuleSet getTypeinSkillRules(boolean wait){
		synchronized(mutex){
			if(definerThread != null && definerThread.isAlive()){
				if(wait){
					try{
						//dT might have died already ...
						while(definerThread.isAlive()){
							mutex.wait(100);
						}
					}
					catch(InterruptedException ie){
						return null;
					}
				}
				else{
					return null;
				}
			}
			return typeinSkillRules;
		}
	}

	/*public RuleSet getstrategicRules(){
	  synchronized(mutex){
	  if(strategicRules == null && isRunning){
	  try{
	  mutex.wait();
	  }
	  catch(InterruptedException ie){	}
	  }
	  return strategicRules;
	  }
	  }*/

	/*returns true if calling get*Rules(false) will return non-null*/
	public static boolean rulesReady(){
		synchronized(mutex){
			if(definerThread != null && definerThread.isAlive()){
				return false;
			}
			else{
				return strategicRules != null &&
					strategicBugRules != null &&
					typeinBugRules != null &&
					skillRules != null &&
					typeinSkillRules != null;
			}
		}
	}

	/*starts a separate thread to define the rules*/
	public static void defineRules(){
		RuleDefiner r = new RuleDefiner();
		synchronized(mutex){
			definerThread = new Thread(r);
			definerThread.setPriority(Thread.MIN_PRIORITY);
			definerThread.start();
		}
		return;
	}

	/*just calls each of the define*Rules in turn*/
	public void run(){
		/*System.out.println("RD.r: sleeping ...");
		  try{
		  Thread.sleep(7000);
		  }
		  catch(Exception e){}*/

		if(Rule.debug()){
			System.out.println("RD.r: defining rules ...");
		}
		RuleSet temp;
		temp = defineStrategicRules();
		synchronized(mutex){
			strategicRules = temp;
		}
		if(Rule.debug()){
			System.out.println("      strategic: " + temp.numRules());
		}

		temp = defineStrategicBugRules();
		synchronized(mutex){
			strategicBugRules = (BugRuleSet)temp;
		}
		if(Rule.debug()){
			System.out.println("      bug: " + temp.numRules());
		}

		temp = defineTypeinBugRules();
		synchronized(mutex){
			typeinBugRules = (TypeinBugRuleSet)temp;
		}
		if(Rule.debug()){
			System.out.println("      typein bug: " + temp.numRules());
		}

		SkillRuleSet t2;
		t2 = defineSkillRules();
		synchronized(mutex){
			skillRules = t2;
		}
		if(Rule.debug()){
			System.out.println("      skill: " + t2.numRules());
		}

		t2 = defineTypeinSkillRules();
		synchronized(mutex){
			typeinSkillRules = (TypeinSkillRuleSet)t2;
			mutex.notifyAll();
		}
		if(Rule.debug()){
			System.out.println("      typein skill: " + t2.numRules());
			System.out.println("RD.r: finished defining rules");
		}
	}

	//Common property accessors
	/*these next two are handled by EquationHistory*/
	private static String variable = "{target variable}";
	private static String origEq = "{original equation}";

	private static String onlyCoefficient = "{coefficient of item 1 of variable terms of variable side expression}";
	private static String constantOnVarSide = "{item 1 of constant terms of variable side expression}";
	//private static String variable = "{item 1 of variables of variable side expression}";
	private static String denominatorConstant = "{coefficient of denominator of variable side expression}";
	private static String numeratorConstant = "{coefficient of numerator of variable side expression}";
	private static String denominatorRecip = "{reciprocal of coefficient of denominator of variable side expression}";
	private static String numerator = "{numerator of variable side expression}";
	private static String denominator = "{denominator of variable side expression}";
	private static String negativeConstant = "{negative of [unfence] [item 1 of constant terms of variable side expression]}";
	private static String varSide = "{variable side expression}";
	private static String variableTerm = "{item 1 of variable terms of variable side expression}";
	private static String varCoeffDenominator ="{denominator of coefficient of item 1 of variable terms of variable side expression}";

	//Common tests
	private static BooleanTest positiveConstantP = new BooleanTest(new String[] {"isPositive","item 1","constant terms","variable side expression"},true);
    private static AnyTest anyPositiveConstantP = new AnyTest(new String[] {"constant terms","variable side expression"},
                                                                  "isPositive");
    private static AnyTest anyNegativeConstantP = new AnyTest(new String[] {"constant terms","variable side expression"},
                                                                  "isNegative");
	private static BooleanTest negativeConstantP = new BooleanTest(new String[] {"isNegative","item 1","constant terms","variable side expression"},true);
	private static BooleanTest negativeCoefficientP = new BooleanTest(new String[] {"isNegative","coefficient","item 1","variable terms","variable side expression"},true);

	//not sure I need all of these variants, but it can't hurt...
	private static Test[] simpleOrderOpsTest = new Test[] {new OrTest(new Test[] {new FormTest("x=a*b+c"),
																				  new FormTest("x=a*b-c"),
																				  new FormTest("x=c-a*b"),
																				  new FormTest("x=-a*b+c"),
																				  new FormTest("x=-a*b-c")})};

	/*this tests whether distributing will increase the number of
	  variable terms (which is bad: we don't want to, eg, go from
	  x(3+4) to 3x+4x).  up here because they're used in strategic and
	  bug rules*/
	private static Test distIncrVarTermsLeft =
		new GreaterThanTest(new String[] {"length","variable terms","[distribute] [left side]"},
							new String[] {"length","variable terms","left side"});
	private static Test distIncrVarTermsRight =
		new GreaterThanTest(new String[] {"length","variable terms","[distribute] [right side]"},
							new String[] {"length","variable terms","right side"});

	public static RuleSet defineStrategicRules() {
		//first, define common tests
//		FormTest axEqB = new FormTest("ax=b");
//		FormTest xPlusAEqB = new FormTest("x+a=b");
//		FormTest axPlusBEqC = new FormTest("ax+b=c");
		EveryTest allIntegers = new EveryTest("all numbers","isNotDecimal");
		
	
		//Now, define the rules
		Rule[] allRules = new Rule[300]; //max 300 rules
		int i=0;
		
		allRules[i++] = new SideRule("Simplify",
							   new BooleanTest("canSimplify"),
							   "Simplify",
							   new String[] {"Put the equation in its simplest form.",
							   				 "Simplify on {*side*}."});
		/*
		Rule ruleDistribute = new SideRule("Distribute",
							   new BooleanTest("canDistribute"),
							   "Distribute",
							   new String[] {"Put the equation in its simplest form.",
							   				 "Distribute on {*side*}."});
		*/
		allRules[i++] = new SideRule("MT",
									 new Test[] {new BooleanTest("canMultiplyThrough"),
												 new NotTest(new DblStringTest("[sort] [[multiplyThrough] [self]]",
																			   "[sort] [self]"))},
									 "MT",
									 new String[] {"Put the equation in its simplest form.",
												   "Perform multiplication on {*side*}"});

		/*reducing fractions on something like x/-3 changes it to -x/3
		  -- this is no good because it adds an extra step.  So we
		  want something for which reducing means more than just
		  negating the top and bottom of the fraction.*/
		Test rfOkay = new AnyTest("components with property canReduceFractionsWhole",
								  new NotTest(new OrTest(new Test[] {new ExactEqualTest(new String[] {"negative","numerator","[reduceFractions] [self]"},
																						"numerator"),
																	 new ExactEqualTest(new String[] {"negative","denominator","[reduceFractions] [self]"},
																						"denominator")})));
		Test rfOkayV = new AnyTest(new String[] {"components with property canReduceFractionsWhole","variable side expression"},
								   new NotTest(new OrTest(new Test[] {new ExactEqualTest(new String[] {"negative","numerator","[reduceFractions] [self]"},
																						 "numerator"),
																	  new ExactEqualTest(new String[] {"negative","denominator","[reduceFractions] [self]"},
																						 "denominator")})));
		allRules[i++] = new SideRule("RF",
									 new Test[] {new BooleanTest("canReduceFractions"),
												 rfOkay},
									 "RF",
									 new String[] {"Put the equation in its simplest form.",
												   "Reduce fractions on {*side*}"});
		/*CLT needs to be after MT because for x=3+2*3 we want the
          hint to suggest MT, even though 3 and 2*3 are like terms.*/
		allRules[i++] = new SideRule("CLT nested",
							   new Test[] {new BooleanTest("canCombineLikeTerms"),
							   			   new NotTest(new BooleanTest("canCombineLikeTermsWhole"))},
							   "CLT",
							   new String[] {"Put the equation in its simplest form.",
							   				 "Be sure to do operations within parentheses first.",
							   				 "You can combine {term 1 of item 1 of components with property canCombineLikeTermsWhole} "+
							   				 "and {term 2 of item 1 of components with property canCombineLikeTermsWhole} on {*side*}",
							   				 "Combine like terms on {*side*}"});
		allRules[i++] = new SideRule("CLT",
							   new BooleanTest("canCombineLikeTerms"),
							   "CLT",
							   new String[] {"Put the equation in its simplest form.",
							   				 "Combine like terms on {*side*}."});
		// ALLEN
		allRules[i++] = new Rule("CM",
								new Test[] {new OrTest(new Test[] {new BooleanTest(new String[] {"isRatio","left side"}),new BooleanTest(new String[] {"isFraction","left side"})}),
											new OrTest(new Test[] {new BooleanTest(new String[] {"isRatio","right side"}),new BooleanTest(new String[] {"isFraction","right side"})}),
											new NotTest(new StringTest(new String[] {"denominator","left side"},"1")),
											new NotTest(new StringTest(new String[] {"denominator","left side"},"1"))},
								"CM", null,
								new String[] {"There is a special operation you can perform when there are fractions on both sides of the equation.",
											  "Try to cross multiply.",
											  "Choose cross multiply from the solver menu."});
		// end ALLEN
		allRules[i++] = new StdOpRule("ax/b=c, reciprocal",new FormTest("ax/b=c"), "Divide",onlyCoefficient,
									new String[] {"What can you do to both sides to get "+variable+" by itself?",
												  "In {variable side expression}, "+variable+" is multiplied by "+onlyCoefficient+". How do you change the "+onlyCoefficient+" into a <B>1</B>?",
												  "Multiplying something by its reciprocal changes it into 1. Multiply both sides by {reciprocal of coefficient of item 1 of variable terms of variable side expression}"});
		allRules[i++] = new StdOpRule("ax/b=c", new FormTest("ax/b=c"), "Multiply", denominatorConstant,
									new String[] {"What can you do to both sides to get "+variable+" by itself?",
							 		"In {variable side expression}, "+variable+" is divided by "+denominatorConstant+". How do you undo division?",
							 		"Multiply both sides by "+denominatorConstant+"."});
		allRules[i++] = new StdOpRule("ax/b=c-remove numerator",
									  new Test[] {new FormTest("ax/b=c"),
												  new NotTest(new AlgebraicEqualTest(new String[] {"coefficient","numerator","variable side expression"},
																					 new String[] {"coefficient","denominator","variable side expression"}))},
									  "Divide",
									  numeratorConstant,
									  new String[] {"What can you do to both sides to get "+variable+" by itself?",
													"In {variable side expression}, "+variable+
													" is multiplied by "+numeratorConstant+
													". How do you undo multiplication?",
													"Divide both sides by "+numeratorConstant+"."});

		allRules[i++] = new StdOpRule("x/a=b", new FormTest("x/a=b"),"Multiply",denominatorConstant,
									new String[] {"What can you do to both sides to get "+variable+" by itself?",
							 		"In {variable side expression}, "+variable+" is divided by "+denominatorConstant+". How do you undo division?",
							 		"Multiply both sides by "+denominatorConstant+"."});

		allRules[i++] = new StdOpRule("x/-a=b", new FormTest("x/-a=b"),"Multiply",denominatorConstant,
									new String[] {"What can you do to both sides to get "+variable+" by itself?",
							 		"In {variable side expression}, "+variable+" is divided by "+denominatorConstant+". How do you undo division?",
							 		"Multiply both sides by "+denominatorConstant+"."});

		allRules[i++] = new StdOpRule("(+/-x +/-a)/b=c, mult",
									/*perhaps we should modify
                                      FormTest to test the same form
                                      with & without fences?*/
									new OrTest(new Test[] {new FormTest("[x+a]/b=c"),
														   new FormTest("(x+a)/b=c"),
														   new FormTest("[x-a]/b=c"),
														   new FormTest("(x-a)/b=c"),
														   new FormTest("[-x+a]/b=c"),
														   new FormTest("(-x+a)/b=c"),
														   new FormTest("[-x-a]/b=c"),
														   new FormTest("(-x-a)/b=c")}),
									"Multiply",
									denominatorConstant,
									new String[] {"What can you do to both sides to get " + variable + " by itself?",
												  "In {variable side expression}, {numerator of variable side expression} is divided by " +
												  denominatorConstant + ".  How do you undo division?",
												  "Multiply both sides by " + denominatorConstant + "."});
		
		allRules[i++] = new StdOpRule("(+/-x +/-a)*b=c, div",
									/*perhaps we should modify
                                      FormTest to test the same form
                                      with & without fences?*/
									new OrTest(new Test[] {new FormTest("[x+a]*b=c"),
														   new FormTest("(x+a)*b=c"),
														   new FormTest("[x-a]*b=c"),
														   new FormTest("(x-a)*b=c"),
														   new FormTest("[-x+a]*b=c"),
														   new FormTest("(-x+a)*b=c"),
														   new FormTest("[-x-a]*b=c"),
														   new FormTest("(-x-a)*b=c")}),
									  "Divide",
									"{coefficient of variable side expression}",
									new String[] {"What can you do to both sides to get " + variable + " by itself?",
												  "In {variable side expression}, {variable factor of variable side expression} is multiplied by " +
												  "{coefficient of variable side expression}" +
												  ".  How do you undo multiplication?",
												  "Divide both sides by " +
												  "{coefficient of variable side expression}" + "."});
		
		allRules[i++] = new StdOpRule("add x in -x",new FormTest("-x=a"),"Add",variable); //for some reason, we allow this...

		/*this is only okay if the complexity of the side to which
          we're moving the variable is less than or equal to the side
          from which we're taking the variable: adding x to a-x=b is
          okay, but adding x to a-x=b+c/d+3ef is no good*/
		//mmmBUG: for now we'll just write the simple, specific version
		allRules[i++] = new StdOpRule("move neg var to other side",
									  new OrTest(new Test[] {new FormTest("a-x=b"),
															 new FormTest("a-x=-b")}),
									  "add",
									  variable);
		
//		Rule rule3 = new Rule("x=a",new FormTest("x=a"),"Done",null,new String[] {"The equation is solved "+variable+" equals {constant side expression}",
//							  														"Choose DONE from the menu."});

		/*String[] donemsgs = new String[] {"Your last step in the Solver window is {equation}. What does this convey about the number of solutions?",
		  "Go to the Tutor menu and pull down to a selection of Done that describes the solutions for this equation. How many values of " +
		  variable +
		  " does {equation} indicate?",
		  "Since your last step is {equation}, and since it shows that {constant side expression} is the only solution for " +
		  variable +
		  ", there is only one unique solution for " + variable +". Go to the solver menu and choose accordingly."};*/

		/*Rule rule3 = new Rule("Done",
		  new FormTest("x=a", false),
		  "Done",
		  null,
		  donemsgs);*/

		// ALLEN - If the equation is solved, but the Done menu item is unavailable, we still want to tell the student that the equation is solved.
		//         We place this rule after the Done rule so that it does not fire if the Done menu item is indeed available.					  														
//		Rule rule3b = new Rule("Solved", new FormTest("x=a", false),"Add",null,new String[] {"The equation is solved. "+variable+" equals {constant side expression}."});
							  														
		allRules[i++] = new StdOpRule("a/bx=c",new FormTest("a/(bx)=c"),"Multiply",variable,
										new String[] {"What can you do to both sides to get the variable out of the denominator?",
							  							"{variable side expression} is "+numerator+" divided by "+denominator+". How can you move "+variable+" out of the denominator?",
							  							"Multiply both sides by "+variable});
		allRules[i++] = new StdOpRule("a/bx=c, mult",new FormTest("a/(bx)=c"),"Multiply",denominator); //e.g. allow multiply by 4x
		
		allRules[i++] = new StdOpRule("a/bx+c=d",new Test[] {positiveConstantP,new FormTest("a/(bx)+c=d")},"Subtract",constantOnVarSide,
									new String[] {"What can you do to both sides to get the "+variable+" by itself?",
													"The "+constantOnVarSide+" in "+varSide+" is positive. How do you remove a positive?",
							  						"Subtract "+constantOnVarSide+" from both sides."});
		allRules[i++] = new StdOpRule("ax/b+c=d",new Test[] {positiveConstantP,new FormTest("ax/b+c=d")},"Subtract",constantOnVarSide,
									new String[] {"What can you do to both sides to get the "+variable+" by itself?",
													"The "+constantOnVarSide+" in "+varSide+" is positive. How do you remove a positive?",
							  						"Subtract "+constantOnVarSide+" from both sides."});
		
 		allRules[i++] = new StdOpRule("a/bx-c=d",new Test[] {negativeConstantP,new FormTest("a/(bx)-c=d")},"Add",negativeConstant,
									new String[] {"What can you do to both sides to get the "+variable+" by itself?",
							  						constantOnVarSide+" is negative. How do you remove a negative?",
							  						"Add "+negativeConstant+" to both sides."});
		allRules[i++] = new StdOpRule("ax/b-c=d",new Test[] {negativeConstantP,new FormTest("ax/b-c=d")},"Add",negativeConstant,
									new String[] {"What can you do to both sides to get the "+variable+" by itself?",
							  						constantOnVarSide+" is negative. How do you remove a negative?",
							  						"Add "+negativeConstant+" to both sides."});
		
		String[] messages7 = {"What can you do to both sides to get the "+variable+" by itself?",
								"{variable side expression} is "+onlyCoefficient+" times "+variable+". How do you undo multiplication?",
								"Divide both sides by "+onlyCoefficient+"."};
								
		//Rule rule7 = new StdOpRule("ax=b",new FormTest("ax=b"),"Divide",onlyCoefficient,messages7);
		allRules[i++] = new StdOpRule("ax=b",new Test[] {	new FormTest("ax=b"),
														new NotTest(new BooleanTest(new String[] {"canSimplify","right side"})),
														new NotTest(new BooleanTest(new String[] {"canSimplify","left side"}))},
									"Divide",onlyCoefficient,messages7);
								
		String[] messages8 = {"What can you do to both sides to get the "+variable+" by itself?",
							  "The "+constantOnVarSide+" in "+varSide+" is positive. How do you remove a positive?",
							  "Subtract "+constantOnVarSide+" from both sides."};
		Test[] test8 = {new FormTest("x+a=b"),positiveConstantP};
		allRules[i++] = new StdOpRule("x+a=b, positive",test8,"Subtract",constantOnVarSide,messages8);
        allRules[i++] = new StdOpRule("a-x=b, positive",new FormTest("a-x=b"),"Subtract",constantOnVarSide,messages8);

		String[] messages9 = {"What can you do to both sides to get the "+variable+" by itself?",
							  "The "+constantOnVarSide+" in "+varSide+" is negative. How do you remove a negative?",
							  "Add "+negativeConstant+" to both sides."};
							  
		//For some reason, -5+y=2 matches x+a=b, but x-5=9 matches x-a=b, so we let the form be either one
		//This should really be cleaned up. I'm not sure when positive and negative are significant in patterns...
		Test[] test9 = {new OrTest(new Test[] {new FormTest("x-a=b"),new FormTest("x+a=b")}),negativeConstantP};
		allRules[i++] = new StdOpRule("x+a=b, negative",test9,"Add",negativeConstant,messages9);
                //Rule rule9b = new StdOpRule("a-x=b, negative",new FormTest("-a-x=b"),"Add",negativeConstant,messages9);

		Test[] test10 = {new FormTest("ax+b=c"),positiveConstantP};
		allRules[i++] = new StdOpRule("ax+b=c, positive",test10,"Subtract",constantOnVarSide,messages8);
		Test[] test10b = {new FormTest("a/x+b=c"),positiveConstantP};
		allRules[i++] = new StdOpRule("a/x+b=c, positive",test10b,"Subtract",constantOnVarSide,messages8);
                /*Rule rule10gen = new StdOpRule("x+[const expr]=[const expr], positive",
                  anyPositiveConstantP,
                  "Subtract",
                  "{constant terms of variable side expression}",
                  messages8);*/

			
		Test[] test11 = {new FormTest("ax-b=c"),negativeConstantP};
		allRules[i++] = new StdOpRule("ax+b=c, negative",test11,"Add",negativeConstant,messages9);
		Test[] test11b = {new FormTest("a/x-b=c"),negativeConstantP};
		allRules[i++] = new StdOpRule("a/x+b=c, negative",test11b,"Add",negativeConstant,messages9);
		
		
		allRules[i++] = new StdOpRule("x/a+b=c, multiply", 
									 new Test[] {new FormTest("x/a+b=c"),
												 new NotTest(new StringTest("variable side","both")),
												 new NotTest(new StringTest(new String[] {"denominator","coefficient","item 1","variable terms","variable side expression"},"1")),
												 new NumberTest(new String[] {"length","variables","variable side expression"},1)},
									 "Multiply",varCoeffDenominator);
		/*new String[] {"What can you do to both sides to get "+variable+" by itself?",
		  "In {variable side expression}, "+variable+" is divided by "+varCoeffDenominator+". How do you undo division?",
		  "Multiply both sides by "+varCoeffDenominator+"."});*/

		String varDenom = "{constant factor of denominator of item 1 of variable terms of variable side expression}";
		String constVarTermFact = "constant factor of item 1 of variable terms of variable side expression";
		String varVarTermFact = "variable factor of item 1 of variable terms of variable side expression";
		allRules[i++] = new StdOpRule("[var expr]/[const expr] = [const expr], multiply",
									  new Test[] {new NotTest(new StringTest("variable side","both")),
												  new NumberTest(new String[] {"length","variable terms","variable side expression"},1),
												  new NumberTest(new String[] {"length","constant terms","variable side expression"},0),
												  new NotTest(new DblStringTest(new String[] {"item 1","variable terms","variable side expression"},
																				new String[] {"variable factor","item 1","variable terms","variable side expression"})),
												  /*new DblStringTest("item 1 of variables of variable side expression",
													"variable factor of item 1 of variable terms of variable side expression"),*/
												  new NotTest(new StringTest(new String[] {"constant factor","denominator","item 1","variable terms","variable side expression"},"1")),
												  new NotTest(new AndTest(new Test[] {new BooleanTest(new String[] {"canReduceFractions","variable side expression"},false),
																					  rfOkayV}))},
									  "Multiply",
									  varDenom,
									  new String[] {"What can you do to both sides to get the " +
													variable + " by itself?",
													"In {variable side expression}, " +
													"{numerator of item 1 of variable terms of variable side expression}" +
													" is divided by " + varDenom + ". How do you undo division?",
													"Multiply both sides by " +
													varDenom + "."});

		allRules[i++] = new Rule("Distribute Division left",
								 new Test[] {new BooleanTest(new String[] {"canDistributeDivision","left side"}),
											 new NotTest(distIncrVarTermsLeft)},
							   "Distribute",
							   "left",
							   new String[] {"Divide {denominator of item 1 of components with property canDistributeDivisionWhole of left side} into each part of {numerator of item 1 of components with property canDistributeDivisionWhole of left side}.",
							   				 "Distribute on the left side"});
		allRules[i++] = new Rule("Distribute Division right",
								 new Test[] {new BooleanTest(new String[] {"canDistributeDivision","right side"}),
											 new NotTest(distIncrVarTermsRight)},
							   "Distribute",
							   "right",
							   new String[] {"Divide {denominator of item 1 of components with property canDistributeDivisionWhole of right side} into each part of {numerator of item 1 of components with property canDistributeDivisionWhole of right side}.",
							   				 "Distribute on the right side"});

		/*it's okay to subtract any positive constant (or add any
          negative), but when we're giving hints we need to be a bit
          more specific*/
		/*mmmBUG: these rules are a bit too permissive: for example,
          they say it's okay to add c on "x-b+c=d".  They also fire on
          "5=2".*/
		allRules[i++] = new StdOpRule("[var expr]+[const expr]=[const expr], positive",
											  new Test[] {new NotTest(new StringTest("variable side","both")),
														  new GreaterThanTest(new String[] {"length","variables","variable side expression"},0),
														  new StringTest(new String[] {"sign word","item 1","constant terms","uncombinable terms","variable side expression"},"positive")},
											  "Subtract",
											  "{item 1 of constant terms of uncombinable terms of variable side expression}",
											  messages8);

		allRules[i++] = new StdOpRule("[var expr]+[const expr]=[const expr], any positive",
										  new Test[] {new NotTest(new StringTest("variable side","both")),
													  new GreaterThanTest(new String[] {"length","variables","variable side expression"},0),
													  anyPositiveConstantP},
										  "Subtract",
										  "{constant terms of uncombinable terms of variable side expression}",
										  messages8);

		allRules[i++] = new StdOpRule("[var expr]+[const expr]=[const expr], negative",
											new Test[] {new NotTest(new StringTest("variable side","both")),
														new GreaterThanTest(new String[] {"length","variables","variable side expression"},0),
														new StringTest(new String[] {"sign word","item 1","constant terms","uncombinable terms","variable side expression"},"negative")},
											"Add",
											"{item 1 of negative of constant terms of uncombinable terms of variable side expression}",
											messages9);

		allRules[i++] = new StdOpRule("[var expr]+[const expr]=[const expr], any negative",
										new Test[] {new NotTest(new StringTest("variable side","both")),
													new GreaterThanTest(new String[] {"length","variables","variable side expression"},0),
													anyNegativeConstantP},
										"Add",
										"{negative of constant terms of uncombinable terms of variable side expression}",
										messages9);

                /*Rule ruleMT = new SideRule("MT",
                  new BooleanTest("canMultiplyThrough"),
                  "MT",
                  new String[] {"Put the equation in its simplest form.",
                  "Multiply through on {*side*}"});*/

                /*Rule factor = new Rule("factor",
                  new BooleanTest("canFactor"),
                  "fact",
                  "",
                  new String[] {"factor the thing"});*/

        allRules[i++] = new Rule("factor",
                                       /*factoring is okay when there
                                         is more than one variable
                                         term on a given side of the
                                         equation and no variable
                                         terms on the other side; we
                                         only hint towards it when the
                                         multiple variable terms are
                                         not joined by any pesky
                                         constant terms*/
                                       new Test[] {new OrTest(new Test[]{new AndTest(new Test[] {new GreaterThanTest(new String[] {"length","variable terms","right side"},1),
																								 new LessThanTest(new String[] {"length","variable terms","left side"},1)}),
																		 new AndTest(new Test[] {new GreaterThanTest(new String[] {"length","variable terms","left side"},1),
																								 new LessThanTest(new String[] {"length","variable terms","right side"},1)})}),
												   new NumberTest(new String[] {"length","constant terms","variable side expression"},0)},
                                       "fact",
                                       variable,
                                       new String[] {"Your goal is to isolate " + variable + ", but " +
													 variable +
													 " is in all of the terms on the same side of the equation.",
													 "Since " + variable +
													 " is a factor in more than one term, factor it out from each term.",
													 "Select Factor: common factor from the Solver menu."});

          allRules[i++] = new Rule("factorb",
										new OrTest(new Test[]{new AndTest(new Test[] {new GreaterThanTest(new String[] {"length","variable terms","right side"},1),
																					  new LessThanTest(new String[] {"length","variable terms","left side"},1)}),
															  new AndTest(new Test[] {new GreaterThanTest(new String[] {"length","variable terms","left side"},1),
																					  new LessThanTest(new String[] {"length","variable terms","right side"},1)})}),
										"fact",
										variable,
										new String[] {"Your goal is to isolate " + variable + ", but " +
													  variable +
													  " is in all of the terms on the same side of the equation.",
													  "Since " + variable +
													  " is a factor in more than one term, factor it out from each term.",
													  "Select Factor: common factor from the Solver menu."});

                //Rule factorR = new Rule("factor",
                                       /*the variable occurs in more
                                         than one term on the same
                                         side.  Should also check that
                                         the variable does not occur
                                         in the denominator of any of
                                         those terms*/
                /*new GreaterThanTest("length of variable terms of right side",1),
                  "factr",
                  variable,
                  new String[] {"factor on the right"});*/
                
                /*mmmBUG: this rule also matches a/x=b+c, and says "a/x is a times x"*/
		allRules[i++] = new StdOpRule("ax+b=c, divide",
									new Test[] {new FormTest("ax+b=c"),
												new NotTest(new FormTest("a/x=b")),
												new NotTest(new StringTest("variable side","both")),
												new NumberTest(new String[] {"length","variables","variable side expression"},1),
												new NotTest(new StringTest(new String[] {"coefficient","item 1","variable terms","variable side expression"},"1"))},
									"Divide",onlyCoefficient, messages7);
		
		allRules[i++] = new StdOpRule("x/a+b=c, negative",new FormTest("x/a-b=c"),"Add",negativeConstant,messages9);
		
		allRules[i++] = new StdOpRule("x/a+b=c, positive",new Test[]{new FormTest("x/a+b=c"),positiveConstantP},"Subtract",constantOnVarSide,messages8);

		/*this rule allows the student to, for example, divide
          "a(x+b)=c" by a (rather than distributing)*/
		/*Rule rule12d = new StdOpRule("[const expr]*[var poly] = [const expr], divide",
		  );*/
									 
		
		String[] messages13 = {"Put all of the terms referring to "+variable+" on the same side of the equation",
							   "Move {item 1 of variable terms of form matching 2x+3} to the {side matching 4x} side",
							   "Subtract {item 1 of variable terms of form matching 2x+3} from both sides"};
		
		allRules[i++] = new StdOpRule("ax+b=cx",new FormTest("ax+b=cx"),"Subtract","{item 1 of variable terms of form matching 2x+3}",messages13);
		allRules[i++] = new StdOpRule("ax+b=cx, move left",new FormTest("ax+b=cx"),"Subtract","{item 1 of variable terms of form matching 4x}");

		String[] messages14 = {"Put all of the terms referring to "+variable+" on the same side of the equation",
							   "Move {item 1 of variable terms of left side} to the right side",
							   "Subtract {item 1 of variable terms of left side} from both sides"};

		/*
		Rule rule14 = new StdOpRule("ax+b=cx+d",new FormTest("ax+b=cx+d"),"Subtract","{item 1 of variable terms of left side}",messages14);
		Rule rule14b = new StdOpRule("ax+b=cx+d, move right",new FormTest("ax+b=cx+d"),"Subtract","{item 1 of variable terms of right side}");
		Rule rule14c = new StdOpRule("ax+b=cx+d, move constant left",new FormTest("ax+b=cx+d"),"Subtract","{item 1 of constant terms of left side}");
		Rule rule14d = new StdOpRule("ax+b=cx+d, move constant right",new FormTest("ax+b=cx+d"),"Subtract","{item 1 of constant terms of right side}");
		Rule rule14e = new StdOpRule("ax+b=cx+d, move two",new FormTest("ax+b=cx+d"),"Subtract","{[add] [item 1 of variable terms of left side] [item 1 of constant terms of right side]}");
		Rule rule14f = new StdOpRule("ax+b=cx+d, move two2",new FormTest("ax+b=cx+d"),"Subtract","{[add] [item 1 of constant terms of left side] [item 1 of variable terms of right side]}");
		*/
		allRules[i++]  = new StdOpRule("ax+b=cx+d",new FormTest("ax+b=cx+d"),"Subtract","{item 1 of variable terms of left side}",messages14);
		allRules[i++] = new StdOpRule("ax+b=cx+d, move right",new FormTest("ax+b=cx+d"),"Subtract","{item 1 of variable terms of right side}");
		allRules[i++] = new StdOpRule("ax+b=cx+d, move constant left",new FormTest("ax+b=cx+d"),"Subtract","{item 1 of constant terms of left side}");
		allRules[i++] = new StdOpRule("ax+b=cx+d, move constant right",new FormTest("ax+b=cx+d"),"Subtract","{item 1 of constant terms of right side}");
		allRules[i++] = new StdOpRule("ax+b=cx+d, move two",new FormTest("ax+b=cx+d"),"Subtract","{[add] [item 1 of variable terms of left side] [item 1 of constant terms of right side]}");
		allRules[i++] = new StdOpRule("ax+b=cx+d, move two2",new FormTest("ax+b=cx+d"),"Subtract","{[add] [item 1 of constant terms of left side] [item 1 of variable terms of right side]}");

		allRules[i++] = new StdOpRule("ax+b=cx+d, move constant left",new FormTest("ax-b=cx+d"),"Subtract","{item 1 of constant terms of left side}");

		allRules[i++] = new StdOpRule("ax+b=cx+d, move constant right",new FormTest("ax+b=cx-d"),"Subtract","{item 1 of constant terms of right side}");

		allRules[i++] = new StdOpRule("ax+b=cx+d, move constant left",new FormTest("ax-b=cx-d"),"Subtract","{item 1 of constant terms of left side}");
		allRules[i++] = new StdOpRule("ax+b=cx+d, move constant right",new FormTest("ax-b=cx-d"),"Subtract","{item 1 of constant terms of right side}");
		
		allRules[i++]  = new StdOpRule("x+b=cx+d",new FormTest("x+b=cx+d"),"Subtract","{item 1 of variable terms of left side}",messages14);
		allRules[i++] = new StdOpRule("x+b=cx+d, move right",new FormTest("x+b=cx+d"),"Subtract","{item 1 of variable terms of right side}");
		allRules[i++] = new StdOpRule("x+b=cx+d, move constant left",new FormTest("x+b=cx+d"),"Subtract","{item 1 of constant terms of left side}");
		allRules[i++] = new StdOpRule("x+b=cx+d, move constant right",new FormTest("x+b=cx+d"),"Subtract","{item 1 of constant terms of right side}");
		allRules[i++] = new StdOpRule("x+b=cx+d, move two",new FormTest("x+b=cx+d"),"Subtract","{[add] [item 1 of variable terms of left side] [item 1 of constant terms of right side]}");
		allRules[i++] = new StdOpRule("x+b=cx+d, move two2",new FormTest("x+b=cx+d"),"Subtract","{[add] [item 1 of constant terms of left side] [item 1 of variable terms of right side]}");

		allRules[i++] = new StdOpRule("x+b=cx+d, move constant left",new FormTest("x-b=cx+d"),"Subtract","{item 1 of constant terms of left side}");

		allRules[i++] = new StdOpRule("x+b=cx+d, move constant right",new FormTest("x+b=cx-d"),"Subtract","{item 1 of constant terms of right side}");

		allRules[i++] = new StdOpRule("x+b=cx+d, move constant left",new FormTest("x-b=cx-d"),"Subtract","{item 1 of constant terms of left side}");
		allRules[i++] = new StdOpRule("x+b=cx+d, move constant right",new FormTest("x-b=cx-d"),"Subtract","{item 1 of constant terms of right side}");
		
		allRules[i++]  = new StdOpRule("ax+b=x+d",new FormTest("ax+b=x+d"),"Subtract","{item 1 of variable terms of left side}",messages14);
		allRules[i++] = new StdOpRule("ax+b=x+d, move right",new FormTest("ax+b=x+d"),"Subtract","{item 1 of variable terms of right side}");
		allRules[i++] = new StdOpRule("ax+b=x+d, move constant left",new FormTest("ax+b=x+d"),"Subtract","{item 1 of constant terms of left side}");
		allRules[i++] = new StdOpRule("ax+b=x+d, move constant right",new FormTest("ax+b=x+d"),"Subtract","{item 1 of constant terms of right side}");
		allRules[i++] = new StdOpRule("ax+b=x+d, move two",new FormTest("ax+b=x+d"),"Subtract","{[add] [item 1 of variable terms of left side] [item 1 of constant terms of right side]}");
		allRules[i++] = new StdOpRule("ax+b=x+d, move two2",new FormTest("ax+b=x+d"),"Subtract","{[add] [item 1 of constant terms of left side] [item 1 of variable terms of right side]}");

		allRules[i++] = new StdOpRule("ax+b=x+d, move constant left",new FormTest("ax-b=x+d"),"Subtract","{item 1 of constant terms of left side}");

		allRules[i++] = new StdOpRule("ax+b=x+d, move constant right",new FormTest("ax+b=x-d"),"Subtract","{item 1 of constant terms of right side}");

		allRules[i++] = new StdOpRule("ax+b=x+d, move constant left",new FormTest("ax-b=x-d"),"Subtract","{item 1 of constant terms of left side}");
		allRules[i++] = new StdOpRule("ax+b=x+d, move constant right",new FormTest("ax-b=x-d"),"Subtract","{item 1 of constant terms of right side}");
		
		allRules[i++]  = new StdOpRule("x+b=x+d",new FormTest("x+b=x+d"),"Subtract","{item 1 of variable terms of left side}",messages14);
		allRules[i++] = new StdOpRule("x+b=x+d, move right",new FormTest("x+b=x+d"),"Subtract","{item 1 of variable terms of right side}");
		allRules[i++] = new StdOpRule("x+b=x+d, move two",new FormTest("x+b=x+d"),"Subtract","{[add] [item 1 of variable terms of left side] [item 1 of constant terms of right side]}");
		allRules[i++] = new StdOpRule("x+b=x+d, move two2",new FormTest("x+b=x+d"),"Subtract","{[add] [item 1 of constant terms of left side] [item 1 of variable terms of right side]}");

		allRules[i++] = new StdOpRule("x+b=x+d, move constant left",new FormTest("x+b=cx+d"),"Subtract","{item 1 of constant terms of left side}");
		allRules[i++] = new StdOpRule("x+b=x+d, move constant right",new FormTest("x+b=cx+d"),"Subtract","{item 1 of constant terms of right side}");

		allRules[i++] = new StdOpRule("x+b=x+d, move constant left",new FormTest("x-b=x+d"),"Subtract","{item 1 of constant terms of left side}");

		allRules[i++] = new StdOpRule("x+b=x+d, move constant right",new FormTest("x+b=x-d"),"Subtract","{item 1 of constant terms of right side}");

		allRules[i++] = new StdOpRule("x+b=x+d, move constant left",new FormTest("x-b=x-d"),"Subtract","{item 1 of constant terms of left side}");
		allRules[i++] = new StdOpRule("x+b=x+d, move constant right",new FormTest("x-b=x-d"),"Subtract","{item 1 of constant terms of right side}");
		
		String[] messages15 = {"What can you do to both sides to get the variable out of the denominator?",
		    					"{variable side expression} is "+onlyCoefficient+" divided by "+variable+". How do you undo division?",
		    					"Multiply both sides by "+variable+"."};
		
		allRules[i++] = new StdOpRule("a/x=b",new FormTest("a/x=b"),"Multiply",variable,messages15);
		// for a/x=b/c
		allRules[i++] = new StdOpRule("a/x=b/c",new Test[]{new FormTest("a/x=b"),
														new NotTest(new StringTest(new String[] {"denominator","constant side expression"},"1"))},
														"Multiply","{denominator of constant side expression}");
        allRules[i++] = new StdOpRule("a/x=[anything]",
                                           /*the variable is on one side,
                                             and in only one term on that side,
                                             and in the denominator of that term*/
                                           new Test[]{new NotTest(new StringTest("variable side expression","both")),
                                           			  new StringTest(new String[] {"length","variable terms","variable side expression"},"1"),
                                                      new StringTest(new String[] {"length","variable terms","denominator","item 1","variable terms","variable side expression"},"1")},
                                           "Multiply",
                                           variable,
                                           messages15);
        
        //allow multiply by x/7 in 7=5/x                
        allRules[i++] = new StdOpRule("a/x=b, sophisticated",
        										 new Test[] {new NotTest(new StringTest("variable side expression","both")),
                                           			  		 new StringTest(new String[] {"length","variable terms","variable side expression"},"1"),
                                                             new StringTest(new String[] {"length","variable terms","denominator","item 1","variable terms","variable side expression"},"1")},
                                                 "Multiply",
                                                 "{[divide] [target variable] [constant side expression]}");
                                               

		allRules[i++] = new Rule("Square root alone",
								new Test[] {new FormTest("x^2=b",false),
											new NotTest(new BooleanTest(new String[] {"canSimplify","right side"})),
											new NotTest(new BooleanTest(new String[] {"canSimplify","left side"}))},
								"squareroot",
								null, //no input
								new String[] {"Remove the square on the {variable side} side.",
											  "Take the square root of both sides."});
		allRules[i-1].setCanEncapsulateVar(false);

		allRules[i++] = new Rule("Square root both sides",
								 new Test[] {new FormTest("x^2=b^2",false),
											 new NotTest(new BooleanTest(new String[] {"canSimplify","variable side"}))},
								 "squareroot",
								 null, //no input
								 new String[] {"Remove the square on the {variable side} side.",
											   "Take the square root of both sides."});
		allRules[i-1].setCanEncapsulateVar(false);

		/*mmmBUG: these are ad-hoc rules that need to be here for
          geometry.  Once we support expanding exponents in the
          interface, these will need to be removed.  These rules are
          only valid with autosimplify turned on.*/
		allRules[i++] = new Rule("Pythag, var alone (add)",
								 new Test[] {new FormTest("x^2=a^2+b^2",false),
											 new NotTest(new BooleanTest(new String[] {"canSimplify","variable side"}))},
								 "squareroot",
								 null, //no input
								 new String[] {"Remove the square on the {variable side} side.",
											   "Take the square root of both sides."});
		allRules[i-1].setCanEncapsulateVar(false);

		allRules[i++] = new Rule("Pythag, var alone (sub)",
								 new Test[] {new FormTest("x^2=a^2-b^2",false),
											 new NotTest(new BooleanTest(new String[] {"canSimplify","variable side"}))},
								 "squareroot",
								 null, //no input
								 new String[] {"Remove the square on the {variable side} side.",
											   "Take the square root of both sides."});
		allRules[i-1].setCanEncapsulateVar(false);

		allRules[i++] = new Rule("Law of cosines",
								 new OrTest(new Test[] {new FormTest("x^2=a^2+b^2-c",false),
														new FormTest("x^2=a^2+b^2+c",false)}),
								 "squareroot",
								 null, //no input
								 new String[] {"Remove the square on the {variable side} side.",
											   "Take the square root of both sides."});
		allRules[i-1].setCanEncapsulateVar(false);

		/*allRules[i++] = new Rule("Pythag, var with term",
		  new FormTest("x^2+a^2=b^2",false),
		  "subtract",
		  "
		  new String[] {"Remove the square on the {variable side} side.",
		  "Take the square root of both sides."});
		  allRules[i-1].setCanEncapsulateVar(false);*/

		String[] donemsgs = new String[] {"You have solved the equation. " + variable +
										  " is {constant side expression}.",
										  "Select 'Done: Unique Solution'."};

		String[] donemsgsNoMenu = new String[] {"You have solved the equation. " + variable +
												" is {constant side expression}."};

		allRules[i++] = new Rule("doneleft",
								new Test[] {new BooleanTest(new String[] {"CanSimplify","right side"},false),
											new BooleanTest(new String[] {"CanSimplify","left side"},false),
											new ExactEqualTest("left side","target variable"),
											new NotTest(new MemberTest("target variable",new String[] {"variables","right side"}))},
								"Done",
								null,
								donemsgs);

		allRules[i++] = new Rule("doneleft, no menu",
								 new Test[] {new BooleanTest(new String[] {"CanSimplify","right side"},false),
											 new BooleanTest(new String[] {"CanSimplify","left side"},false),
											 new ExactEqualTest("left side","target variable"),
											 new NotTest(new MemberTest("target variable",new String[] {"variables","right side"}))},
								 "nil",
								 null,
								 donemsgsNoMenu);

		allRules[i++] = new Rule("doneright",
								new Test[] {new BooleanTest(new String[] {"CanSimplify","left side"},false),
											new BooleanTest(new String[] {"CanSimplify","right side"},false),
											new ExactEqualTest("right side","target variable"),
											new NotTest(new MemberTest("target variable",new String[] {"variables","left side"}))},
								"Done",
								null,
								donemsgs);
								 
		allRules[i++] = new Rule("doneright, nomenu",
								 new Test[] {new BooleanTest(new String[] {"CanSimplify","left side"},false),
											 new BooleanTest(new String[] {"CanSimplify","right side"},false),
											 new ExactEqualTest("right side","target variable"),
											 new NotTest(new MemberTest("target variable",new String[] {"variables","left side"}))},
								 "nil",
								 null,
								 donemsgsNoMenu);
								 
		allRules[i++] = new Rule("Done No Solution",
							   new Test[] {new NotTest(new AlgebraicEqualTest("right side","left side")),
										   new NumberTest(new String[] {"length","variables","left side"},0),
										   new NumberTest(new String[] {"length","variables","right side"},0)},
							   "donenosolution",
							   null,
							   new String[] {"Your equation reads {equation}. For what values of " +
											 variable + " will this be true?",
											 "Since {left side} never equals {right side}, no value of " +
											 variable + " will make this equation true. {left side of original equation} is never equal to {right side of original equation}.",
											 "Select 'Done: No Solution'."});

		allRules[i++] = new Rule("Done No Solution, no menu",
								 new Test[] {new NotTest(new AlgebraicEqualTest("right side","left side")),
											 new NumberTest(new String[] {"length","variables","left side"},0),
											 new NumberTest(new String[] {"length","variables","right side"},0)},
								 "nil",
								 null,
								 new String[] {"Your equation reads {equation}. For what values of " +
											   variable + " will this be true?",
											   "Since {left side} never equals {right side}, no value of " +
											   variable + " will make this equation true. {left side of original equation} is never equal to {right side of original equation}."});

		/*"Your last step in the Solver window is {equation}. What does this convey about the number of solutions?",
		  "Go to the Tutor menu and pull down to a selection of Done that describes the solutions for this equation. What value of the variable could possibly make {equation} be true?",
		  "Since your last step is {equation}, and since this is a false statement, you can conclude that there are no solutions to the equation. Go to the Solver menu and choose accordingly."});*/
								 
		allRules[i++] = new Rule("Done Infinite Solutions",
							   new AlgebraicEqualTest("right side","left side"),
							   "doneinfinitesolutions",
							   null,
							   new String[] {"Your equation reads {equation}. For what values of " +
											 variable + " is this true?",
											 "{left side} is equal to {right side} regardless of the value of " +
											 variable + ", so " + variable +
											 " can be any number. {left side of original equation} is equal to {right side of original equation} for any value of " +
											 variable + ".",
											 "Select 'Done: Infinite Solutions'."});

		allRules[i++] = new Rule("Done Infinite Solutions, no menu",
								 new AlgebraicEqualTest("right side","left side"),
								 "nil",
								 null,
								 new String[] {"Your equation reads {equation}. For what values of " +
											   variable + " is this true?",
											   "{left side} is equal to {right side} regardless of the value of " +
											   variable + ", so " + variable +
											   " can be any number. {left side of original equation} is equal to {right side of original equation} for any value of " +
											   variable + "."});

		/*"Your last step in the solver window is {equation}. What does this convey about the number of solutions?",
		  "Go to the Tutor menu and pull down to a selection of Done that describes the solution for this equation. What values of the variable would make {equation} be true?",
		  "Since your last step is {equation}, and since this is a statement that will always be true, no matter what value of the variable you put into it, you can conclude that all values of the variable are solutions. Go to the Solver menu and choose accordingly."});*/

		allRules[i++] = new SideRule("RF, unconstrained",
									 new BooleanTest("canReduceFractions"),
									 "RF");
		/*new String[] {"Put the equation in its simplest form.",
		  "Reduce fractions on {*side*}"});*/

		/*if the variable only appears outside the parenthesis (as
          e.g. "a=x(b+c)"), we don't want to distribute; instead we
          want to divide by the whole thing in parens*/
		allRules[i++] = new StdOpRule("a=x*(b+c)",
										 new FormTest("a=x*(b+c)"),
										 "Divide",
										 "{factor matching b+c of variable side expression}",
										 new String [] {"What can you do to both sides to get the " +
														variable + " by itself?",
														"{variable side expression} is " +
														"{factor matching b+c of variable side expression}" +
														" times " +	variable + ". How do you undo multiplication?",
														"Divide both sides by " +
														"{factor matching b+c of variable side expression}" + "."});
		
		allRules[i++] = new StdOpRule("a=x*(b*c+d)",
										  new FormTest("a=x*(b*c+d)"),
										  "Divide",
										  "{factor matching b*c+d of variable side expression}",
										  new String [] {"What can you do to both sides to get the " +
														 variable + " by itself?",
														 "{variable side expression} is " +
														 "{factor matching b*c+d of variable side expression}" +
														 " times " + variable + ". How do you undo multiplication?",
														 "Divide both sides by " +
														 "{factor matching b*c+d of variable side expression}" + "."});
		
		allRules[i++] = new StdOpRule("a=x*(b-c)",
										  new FormTest("a=x*(b-c)"),
										  "Divide",
										  "{factor matching b-c of variable side expression}",
										  new String [] {"What can you do to both sides to get the " +
														 variable + " by itself?",
														 "{variable side expression} is " +
														 "{factor matching b-c of variable side expression}" +
														 " times " + variable + ". How do you undo multiplication?",
														 "Divide both sides by " +
														 "{factor matching b-c of variable side expression}" + "."});

		
		allRules[i++] = new StdOpRule("[var expr]*[const expr] = [const expr], divide",
									  new Test[] {new NotTest(new StringTest("variable side","both")),
												  new NumberTest(new String[] {"length","variable terms","variable side expression"},1),
												  new NumberTest(new String[] {"length","constant terms","variable side expression"},0),
												  new NotTest(new DblStringTest(new String[] {"item 1","variable terms","variable side expression"},
																				new String[] {"variable factor","item 1","variable terms","variable side expression"})),
												  //just to make sure that there actually *is* a constant factor
												  new DblStringTest(constVarTermFact,constVarTermFact)},
												  /*new DblStringTest("target variable",
													new String[] {"variable factor","item 1","variable terms","variable side expression"})},*/
									  "Divide",
									  "{" + constVarTermFact + "}",
									  new String[] {"What can you do to both sides to get the " +
													variable + " by itself?",
													"{variable side expression} is " +
													"{" + constVarTermFact + "}" +
													" times {" + varVarTermFact + "}. How do you undo multiplication?",
													"Divide both sides by " +
													"{" + constVarTermFact + "}" + "."});

		allRules[i++] = new StdOpRule("[const expr]*[var fact] + [const expr] = [const expr], divide",
									   new Test[] {new NotTest(new StringTest("variable side","both")),
												   new NumberTest(new String[] {"length","variable terms","variable side expression"},1),
												   new GreaterThanTest(new String[] {"length","terms","variable side expression"},
																	   new String[] {"length","variable terms","variable side expression"}),
												   //just to make sure that there actually *is* a constant factor
												   new DblStringTest(constVarTermFact,constVarTermFact),
												   new NotTest(new DblStringTest(new String[] {"item 1","variable terms","variable side expression"},
																				 new String[] {"variable factor","item 1","variable terms","variable side expression"}))
												   /*new DblStringTest("item 1 of variables of variable side expression",
													 "variable factor of item 1 of variable terms of variable side expression")*/
									   },
									   "Divide",
									   "{" + constVarTermFact + "}");



		allRules[i++] = new Rule("Distribute both mult left",
							   new Test[] {new BooleanTest(new String[] {"canDistributeMultiplication","left side"}),
										   new NotTest(distIncrVarTermsLeft),
							   			   new BooleanTest(new String[] {"canDistribute","right side"}),
										   new NotTest(distIncrVarTermsRight)},
							   "Distribute",
							   "both",
							   new String[] {"Remove the parentheses from the equation.",
							   				 "Distribute on both sides"});
		allRules[i++] = new Rule("Distribute both mult right",
							   new Test[] {new BooleanTest(new String[] {"canDistribute","left side"}),
										   new NotTest(distIncrVarTermsLeft),
							   			   new BooleanTest(new String[] {"canDistributeMultiplication","right side"}),
										   new NotTest(distIncrVarTermsRight)},
							   "Distribute",
							   "both",
							   new String[] {"Remove the parentheses from the equation.",
							   				 "Distribute on both sides"});
		allRules[i++] = new Rule("Distribute both divide left",
							   new Test[] {new BooleanTest(new String[] {"canDistributeDivision","left side"}),
										   new NotTest(distIncrVarTermsLeft),
							   			   new BooleanTest(new String[] {"canDistribute","right side"}),
										   new NotTest(distIncrVarTermsRight)},
							   "Distribute",
							   "both",
							   new String[] {"Divide {denominator of left side} into each part of {numerator of left side}.",
							   				 "Distribute on both sides"});
		allRules[i++] = new Rule("Distribute both divide right",
							   new Test[] {new BooleanTest(new String[] {"canDistribute","left side"}),
										   new NotTest(distIncrVarTermsLeft),
							   			   new BooleanTest(new String[] {"canDistributeDivision","right side"}),
										   new NotTest(distIncrVarTermsRight)},
							   "Distribute",
							   "both",
							   new String[] {"Divide {denominator of right side} into each part of {numerator of right side}.",
							   				 "Distribute on both sides"});
		allRules[i++] = new Rule("Distribute Mult left",
								 new Test[] {new BooleanTest(new String[] {"canDistributeMultiplication","left side"}),
											 new NotTest(distIncrVarTermsLeft)},
								 "Distribute",
								 "left",
								 new String[] {"Remove the parentheses from the equation.",
											   "Distribute on the left side"});
		allRules[i++] = new Rule("Distribute Mult right",
								 new Test[] {new BooleanTest(new String[] {"canDistributeMultiplication","right side"}),
											 new NotTest(distIncrVarTermsRight)},
							   "Distribute",
							   "right",
							   new String[] {"Remove the parentheses from the equation.",
							   				 "Distribute on the right side"});

		/*we prefer to gather variable terms on the left, unless things are messy over there*/
		allRules[i++] = new StdOpRule("Combine variables to right, sub",
									   new Test[] {new GreaterThanTest(new String[] {"length","variables","left side"},0),
												   new GreaterThanTest(new String[] {"length","variables","right side"},0),
												   new GreaterThanTest(new String[] {"complexity","left side"},new String[] {"complexity","right side"}),
												   new NotTest(new BooleanTest(new String[] {"canCombineLikeTerms","left side"})),
												   new StringTest(new String[] {"sign word","item 1","variable terms","left side"},"positive")},
									   "Subtract",
									   "{item 1 of variable terms of left side}",
									   new String[] {"Put all of the terms referring to "+variable+" on the same side of the equation",
													 "Move {item 1 of variable terms of left side} to the right side",
													 "Subtract {item 1 of variable terms of left side} from both sides"});
		allRules[i++] = new StdOpRule("Combine variables to right, add",
									   new Test[] {new GreaterThanTest(new String[] {"length","variables","left side"},0),
												   new GreaterThanTest(new String[] {"length","variables","right side"},0),
												   new GreaterThanTest(new String[] {"complexity","left side"},new String[] {"complexity","right side"}),
												   new NotTest(new BooleanTest(new String[] {"canCombineLikeTerms","left side"})),
												   new StringTest(new String[] {"sign word","item 1","variable terms","left side"},"negative")},
									   "Add",
									   "{negative of item 1 of variable terms of left side}",
									   new String[] {"Put all of the terms referring to "+variable+" on the same side of the equation",
													 "Move {negative of item 1 of variable terms of left side} to the right side",
													 "Add {negative of item 1 of variable terms of left side} to both sides"});
		allRules[i++] = new StdOpRule("Combine variables to right, gen",
									   new Test[] {new GreaterThanTest(new String[] {"length","variables","left side"},0),
												   new GreaterThanTest(new String[] {"length","variables","right side"},0),
												   new GreaterThanTest(new String[] {"complexity","left side"},new String[] {"complexity","right side"}),
												   new NotTest(new BooleanTest(new String[] {"canCombineLikeTerms","left side"}))},
									   "Subtract",
									   "{variable terms of left side}",
									   new String[] {"Put all of the terms referring to "+variable+" on the same side of the equation"});

		allRules[i++] = new StdOpRule("Combine variables to left, add",
									   new Test[] {new GreaterThanTest(new String[] {"length","variables","left side"},0),
												   new GreaterThanTest(new String[] {"length","variables","right side"},0),
												   new NotTest(new BooleanTest(new String[] {"canCombineLikeTerms","right side"})),
												   new StringTest(new String[] {"sign word","item 1","variable terms","right side"},"negative")},
									   "Add",
									   "{negative of item 1 of variable terms of right side}",
									   new String[] {"Put all of the terms referring to "+variable+" on the same side of the equation",
													 "Move {negative of item 1 of variable terms of right side} to the left side",
													 "Add {negative of item 1 of variable terms of right side} to both sides"});
		allRules[i++] = new StdOpRule("Combine variables to left, sub",
									   new Test[] {new GreaterThanTest(new String[] {"length","variables","left side"},0),
												   new GreaterThanTest(new String[] {"length","variables","right side"},0),
												   new NotTest(new BooleanTest(new String[] {"canCombineLikeTerms","right side"})),
												   new StringTest(new String[] {"sign word","item 1","variable terms","right side"},"positive")},
									   "Subtract",
									   "{item 1 of variable terms of right side}",
									   new String[] {"Put all of the terms referring to "+variable+" on the same side of the equation",
													 "Move {item 1 of variable terms of right side} to the left side",
													 "Subtract {item 1 of variable terms of right side} from both sides"});
		allRules[i++] = new StdOpRule("Combine variables to left, gen",
									   new Test[] {new GreaterThanTest(new String[] {"length","variables","left side"},0),
												   new GreaterThanTest(new String[] {"length","variables","right side"},0),
												   new NotTest(new BooleanTest(new String[] {"canCombineLikeTerms","right side"}))},
									   "Subtract",
									   "{variable terms of right side}",
									   new String[] {"Put all of the terms referring to "+variable+" on the same side of the equation"});

		/* ... but gathering on the right is still allowable*/
		allRules[i++] = new StdOpRule("Combine variables to right",
								new Test[] {new GreaterThanTest(new String[] {"length","variables","left side"},0),
											new GreaterThanTest(new String[] {"length","variables","right side"},0),
											new NotTest(new BooleanTest(new String[] {"canCombineLikeTerms","left side"}))},
								"Subtract",
								"{item 1 of variable terms of left side}",
								new String[] {"Put all of the terms referring to "+variable+" on the same side of the equation",
							   					"Move {item 1 of variable terms of left side} to the right side",
							   					"Subtract {item 1 of variable terms of left side} from both sides"});

		/*Rule gatherTermsLeft = new StdOpRule("gather terms left, subtr",
		  new Test[] {new StringTest("variable side","both"),
		  new StringTest("sign word of item 1 of variable terms of right side","positive")},
		  "Subtract",
		  "{item 1 of variable terms of right side}",
		  new String[] {"Put all of the terms referring to " + variable +
		  "on the same side of the equation",
		  "Move {item 1 of variable terms of right side*/

        allRules[i++] = new Rule("substitute constants",
                                               new OrTest(new Test[] {new BooleanTest(new String[] {"canSubstConstants","left side"}),
                                                                      new BooleanTest(new String[] {"canSubstConstants","right side"})}),
                                               "sc",
                                               null,//no input
                                               new String[] {"Some of the symbols in the equation stand for known quantities",
                                                             "Substitute Constants will replace those symbols with their numeric equivalents",
                                                             "Choose \"Substitute Constants\" from the menu"});
		
		allRules[i++] = new Rule("Square root",
								new Test[] {new FormTest("ax^2=b",false),
											new NotTest(new BooleanTest(new String[] {"canSimplify","right side"})),
											new NotTest(new BooleanTest(new String[] {"canSimplify","left side"})),
											new NotTest(new BooleanTest(new String[] {"isNegative","variable side"}))},
								"squareroot",
								null, //no input
								new String[] {"Remove the square on the {variable side} side.",
											  "Take the square root of both sides."});
		allRules[i-1].setCanEncapsulateVar(false);
		allRules[i++] = new CatchallRule("CatchAll");

		//System.out.println("defined "+(i-1)+" strategic rules");
		RuleSet theRules = new RuleSet(allRules,i);
												  
		return theRules;
	}
 	public static BugRuleSet defineStrategicBugRules() {
		BugRule[] bugRules = new BugRule[100];
		int i=0;

		Test rfOkayL = new AnyTest(new String[] {"components with property canReduceFractionsWhole","left side"},
								   new OrTest(new Test[] {new NotTest(new DblStringTest("numerator",
																						new String[] {"variable factor","numerator"})),
														  new LessThanTest(new String[] {"length","variables","numerator"},1)}));
		Test rfOkayR = new AnyTest(new String[] {"components with property canReduceFractionsWhole","right side"},
								   new OrTest(new Test[] {new NotTest(new DblStringTest("numerator",
																						new String[] {"variable factor","numerator"})),
														  new LessThanTest(new String[] {"length","variables","numerator"},1)}));

		//		bugRules[i++] = new BugRule("add, not subtract","Add",null,"Subtract","=","You should subtract, not add");
		//		bugRules[i++] = new BugRule("subtract, not add","Subtract",null,"Add","=","You should add, not subtract");
		bugRules[i++] = new BugRule("mult by coeff",
									"Multiply",
									onlyCoefficient,
									"To remove the coefficient of "+variableTerm+", you need to divide."+
									" Erase your last step and then divide both sides by "+onlyCoefficient+".");
		bugRules[i++] = new BugRule("divide by inv coeff",
									"Divide",
									"{reciprocal of coefficient of item 1 of variable terms of variable side expression}",
									"To remove the coefficient of "+variableTerm+", you need to divide"+
									" by "+onlyCoefficient+" or multiply by"+
									" {reciprocal of coefficient of item 1 of variable terms of variable side expression}.");
		bugRules[i++] = new BugRule("divide by whole",
									new FormTest("3x=4"),
									"Divide",
									"{variable side expression}",
									"Dividing anything by itself will leave 1. "+
									"If you divide {variable side expression} by "+onlyCoefficient+", "+
									variable+" will be left."+
									"Erase your last step and then divide by "+onlyCoefficient+".");
		bugRules[i++] = new BugRule("factor as term negative",
									new FormTest("3x=4"),
									"Add",
									"{negative of coefficient of variable side expression}",
									"In {variable side expression}, "+variable+" is multiplied by "+onlyCoefficient+". "+
									"How do you undo multiplication?");
		bugRules[i++] = new BugRule("factor as term positive",
									new FormTest("3x=4"),
									"Subtract",
									"{coefficient of variable side expression}",
									"In {variable side expression}, "+variable+" is multiplied by "+onlyCoefficient+". "+
									"How do you undo multiplication?");
		bugRules[i++] = new BugRule("manipulate wrong side",
									new Test[]{new NotTest(new NumberTest(new String[] {"length","variables","{variable side expression}"},0))},
									null,
									"{constant side expression}",
									"Focus on the side of the equation with the variable. The variable is on the {variable side} side.");
		bugRules[i++] = new BugRule("done with negative",
									new OrTest(new Test[] {new AlgebraicEqualTest("left side",new String[] {"negative","item 1","variables","variable terms"}),
														   new AlgebraicEqualTest("right side",new String[] {"negative","item 1","variables","variable terms"})},
											   true),
									"Done",
									null,
									"You have not solved this equation. There is a negative sign in front of the "+variable+".");
		bugRules[i++] = new BugRule("result not simplified",
									new OrTest(new Test[] {new BooleanTest(new String[] {"canSimplify","left side"}),
														   new BooleanTest(new String[] {"canSimplify","right side"})}),
									"Done",
									null,
									"The equation is not fully simplified.");

		Test DTest = new OrTest(new Test[] {new AndTest(new Test[] {new BooleanTest(new String[] {"CanSimplify","right side"},false),
																	new BooleanTest(new String[] {"CanSimplify","left side"},false),
																	new AlgebraicEqualTest("left side","target variable"),
																	new NotTest(new MemberTest("target variable",new String[] {"variables","right side"}))}),
											new AndTest(new Test[] {new BooleanTest(new String[] {"CanSimplify","left side"},false),
																	new BooleanTest(new String[] {"CanSimplify","right side"},false),
																	new AlgebraicEqualTest("right side","target variable"),
																	new NotTest(new MemberTest("target variable",new String[] {"variables","left side"}))})});

		Test DNSTest = new AndTest(new Test[] {new NotTest(new AlgebraicEqualTest("right side","left side")),
											   new NumberTest(new String[] {"length","variables","left side"},0),
											   new NumberTest(new String[] {"length","variables","right side"},0)});

		Test DISTest = new AlgebraicEqualTest("right side","left side");

		String Dmesg = "This equation has a unique solution. " +
			variable + " is {constant side expression}.";

		String DNSmesg = "{left side} is never equal to {right side}. Since no value of " +
			variable + " will make this equation true, there is no solution.";

		String DISmesg = "{left side} is always equal to {right side}, so " +
			variable + " can have any value.";

		bugRules[i++] = new BugRule("not Done",
									new NotTest(new OrTest(new Test[] {DTest,DNSTest,DISTest})),
									"done",
									null,
									"You are not done.");
									  
		bugRules[i++] = new BugRule("not DIS",
									new NotTest(new OrTest(new Test[] {DTest,DNSTest,DISTest})),
									"doneinfinitesolutions",
									null,
									"You are not done.");

		bugRules[i++] = new BugRule("not DNS",
									new NotTest(new OrTest(new Test[] {DTest,DNSTest,DISTest})),
									"donenosolution",
									null,
									"You are not done.");
									  
		bugRules[i++] = new BugRule("DNS when D",
									new Test[] {DTest,new NotTest(DNSTest)},
									"donenosolution",
									null,
									Dmesg);
		/*"By choosing 'Done: No Solution', you have indicated that there is no value of " +
		  variable +
		  " that can make the equation true. One knows that an equation has no solution when the last equation-solving step is clearly a false statement, like <expression>3 = 5</expression>. Your last step in the solver window, however, is {equation}. What does this convey about the number of solutions?"});*/

		bugRules[i++] = new BugRule("DNS when DIS",
									new Test[] {DISTest,new NotTest(DNSTest)},
									"donenosolution",
									null,
									DISmesg);
			
		bugRules[i++] = new BugRule("DIS when D",
									new Test[] {DTest,new NotTest(DISTest)},
									"doneinfinitesolutions",
									null,
									Dmesg);

		/*"By choosing 'Done: All Solutions', you have indicated an infinite # of solutions, so any value of " +
		  variable +
		  " will make the equation true. An equation has all solutions when the last step is true and only relates equal expressions, like <expression>2=2</expression>. Your last step in the solver window, however, is {equation}. What does this convey about the number of solutions?",
		  "By choosing 'Done: All Solutions', you have indicated that " +
		  variable +
		  " has an infinite number of solutions. Having an infinite number of solutions means that any value of " +
		  variable +
		  " will make the equation true. One knows that an equation has infinite solutions when the last equation-solving step is a true statement that only relates equivalent expressions, like <expression>2 = 2</expression>. Your last step in the Solver window, however, is {equation}. What does this convey about the number of solutions?",
		  "Go to the Tutor menu and pull down to a selection of Done that describes the solutions for this equation. How many values of " +
		  variable +
		  " does {equation} indicate?",
		  "Since your last step is {equation}, and since it shows that {constant side expression} is the only solution for x, there is only one unique solution for " +
		  variable +
		  ". Go to the Solver menu and choose accordingly."});*/
			
		bugRules[i++] = new BugRule("DIS when DNS",
									new Test[] {DNSTest,new NotTest(DISTest)},
									"doneinfinitesolutions",
									null,
									DNSmesg);
			
		bugRules[i++] = new BugRule("D when DNS",
									new Test[] {DNSTest,new NotTest(DTest)},
									"done",
									null,
									DNSmesg);
			
		bugRules[i++] = new BugRule("D when DIS",
									new Test[] {DISTest,new NotTest(DTest)},
									"done",
									null,
									DISmesg);

		
		bugRules[i++] = new BugRule("add positive term",
									positiveConstantP,
									"Add",
									constantOnVarSide,
									"Since "+constantOnVarSide+" is positive, you should subtract to remove it from the {variable side} side."+
									" Erase your last step and subtract "+constantOnVarSide+" from both sides.");
		bugRules[i++] = new BugRule("subtract negative term",
									negativeConstantP,
									"Subtract",
									"{negative of item 1 of constant terms of variable side expression}",
									"Since "+constantOnVarSide+" is negative, you should add to remove it from the {variable side} side."+
									" Erase your last step and add " + negativeConstant + " to both sides.");
		bugRules[i++] = new BugRule("add negative to negative, variable on one side",
									new Test[] {negativeConstantP,
												new OrTest(new Test[] {new StringTest("variable side","left"),
																	   new StringTest("variable side","right")})},
									"Add",
									"{item 1 of constant terms of variable side expression}",
									"To remove "+constantOnVarSide+" from the {variable side} side, you can add a positive number to it."+
									" Erase your last step and add " + negativeConstant + " to both sides.");
		bugRules[i++] = new BugRule("add negative to negative, variable both sides, left",
									new Test[] {new BooleanTest(new String[] {"isNegative","item 1","constant terms","left side"}),
												new StringTest("variable side","both")},
									"Add",
									"{item 1 of constant terms of left side}",
									"To remove {item 1 of constant terms of left side} from the left side, you can add a positive number to it. Erase your last step and add {negative of [unfence] [item 1 of constant terms of left side]} to both sides.");
		bugRules[i++] = new BugRule("add negative to negative, variable both sides, right",
									new Test[] {new BooleanTest(new String[] {"isNegative","item 1","constant terms","right side"}),
												new StringTest("variable side","both")},
									"Add",
									"{item 1 of constant terms of right side}",
									"To remove {item 1 of constant terms of right side} from the right side, you can add a positive number to it. Erase your last step and add {negative of [unfence] [item 1 of constant terms of right side]} to both sides.");

		bugRules[i++] = new BugRule("subtract negative from positive",
									positiveConstantP,
									"Subtract",
									"{negative of item 1 of constant terms of variable side expression}",
									"To remove "+constantOnVarSide+" from the {variable side} side, you can subtract a positive number from it."+
									" Erase your last step and subtract {item 1 of constant terms of variable side expression} from both sides.");
		bugRules[i++] = new BugRule("term as factor negative",
									negativeConstantP,
									"Divide",
									"{negative of item 1 of constant terms of variable side expression}",
									"You want to isolate "+variableTerm+" by removing "+constantOnVarSide+
									" from the {variable side} side. "+
									"{negative of item 1 of constant terms of variable side expression} is subtracted from "+variableTerm+". What is the opposite of subtraction?");
		bugRules[i++] = new BugRule("term as factor positive",
									positiveConstantP,
									"Divide",
									"{item 1 of constant terms of variable side expression}",
									"You want to isolate "+variableTerm+" by removing "+constantOnVarSide+
									" from the {variable side} side. "+
									"{item 1 of constant terms of variable side expression} is added to "+variableTerm+". What is the opposite of addition?");
		bugRules[i++] = new BugRule("divide negative coeff",
									negativeCoefficientP,
									"Divide",
									"{negative of coefficient of item 1 of variable terms of variable side expression}",
									"In this equation, "+variable+" is multiplied by "+onlyCoefficient+". "+
									"Dividing by {negative of coefficient of item 1 of variable terms of variable side expression} leaves -"+variable+
									", so you still need to remove the negative sign. It is better to divide by "+onlyCoefficient+
									", since that would leave "+variable+".");
		bugRules[i++] = new BugRule("multiply negative coeff",
									negativeCoefficientP,
									"Multiply",
									"{negative of reciprocal of coefficient of item 1 of variable terms of variable side expression}",
									"Multiplying by {negative of reciprocal of coefficient of item 1 of variable terms of variable side expression} "+
									"will leave -"+variable+". It is better to multiply by {reciprocal of coefficient of item 1 of variable terms of variable side expression}, "+
									"because that will leave "+variable+".");
		bugRules[i++] = new BugRule("combine one not both",
									new OrTest(new Test[] {new BooleanTest(new String[] {"canCombineLikeTerms","left side"}),
														   new BooleanTest(new String[] {"canCombineLikeTerms","right side"})},
											   true), //do XOR of two tests
									"CLT",
									"both",
									"You can combine like terms on the {side having property canCombineLikeTerms} side only.");

		bugRules[i++] = new BugRule("CLT when Factor, right",
									new Test[] {new GreaterThanTest(new String[] {"length","variable terms","right side"},1),
												new LessThanTest(new String[] {"length","variable terms","left side"},1)},
									"CLT",
									"right",
									"{exprconjunct of variable terms of right side} are like terms, but their coefficients are not alike. Please use the common factor technique instead.");

		bugRules[i++] = new BugRule("CLT when Factor, left",
									new Test[] {new GreaterThanTest(new String[] {"length","variable terms","left side"},1),
												new LessThanTest(new String[] {"length","variable terms","right side"},1)},
									"CLT",
									"left",
									"{exprconjunct of variable terms of left side} are like terms, but their coefficients are not alike. Please use the common factor technique instead.");
		//"Erase your last step and choose Factor: common factor instead of Combine Like Terms");

		bugRules[i++] = new BugRule("mt one not both",
									new OrTest(new Test[] {new BooleanTest(new String[] {"canMultiplyThrough","left side"}),
														   new BooleanTest(new String[] {"canMultiplyThrough","right side"})},
											   true), //do XOR of two tests
									"MT",
									"both",
									"You can perform multiplication on the {side having property canMultiplyThrough} side only.");
		bugRules[i++] = new BugRule("reduce one not both",
									new OrTest(new Test[] {new AndTest(new Test[] {new BooleanTest(new String[] {"canReduceFractions","left side"}),
																				   rfOkayL}),
														   new AndTest(new Test[] {new BooleanTest(new String[] {"canReduceFractions","right side"}),
																				   rfOkayR})},
											   true), //do XOR of two tests
									"RF",
									"both",
									"You can reduce fractions on the {side having property canReduceFractions} side only.");
		bugRules[i++] = new BugRule("simplify one not both",
									new OrTest(new Test[] {new BooleanTest(new String[] {"canSimplify","left side"}),
														   new BooleanTest(new String[] {"canSimplify","right side"})},
											   true), //do XOR of two tests
									"Simplify",
									"both",
									"You can simplify on the {side having property canSimplify} side only.");
		bugRules[i++] = new BugRule("distribute one not both",
									new OrTest(new Test[] {new BooleanTest(new String[] {"canDistribute","left side"}),
														   new BooleanTest(new String[] {"canDistribute","right side"})},
											   true), //do XOR of two tests
									"Distribute",
									"both",
									"You can distribute on the {side having property canDistribute} side only.");
		String distMsg = "Follow the order of operations to select a more efficient step. Operations inside parentheses should be performed first when possible.";
		bugRules[i++] = new BugRule("distribute var over numbers, left",
									new Test[] {new OrTest(new Test[] {new BooleanTest(new String[] {"canDistributeMultiplication","left side"}),
																	   new BooleanTest(new String[] {"canDistribute","left side"})}),
												distIncrVarTermsLeft,
												new BooleanTest(new String[] {"canCombineLikeTerms","left side"})},
									"Distribute",
									"left",
									distMsg);
		bugRules[i++] = new BugRule("distribute var over numbers, right",
									new Test[] {new OrTest(new Test[] {new BooleanTest(new String[] {"canDistributeMultiplication","right side"}),
																	   new BooleanTest(new String[] {"canDistribute","right side"})}),
												distIncrVarTermsRight,
												new BooleanTest(new String[] {"canCombineLikeTerms","right side"})},
									"Distribute",
									"right",
									distMsg);
		bugRules[i++] = new BugRule("distribute var over numbers, both",
									new Test[] {new OrTest(new Test[] {new BooleanTest(new String[] {"canDistributeMultiplication","left side"}),
																	   new BooleanTest(new String[] {"canDistribute","left side"})}),
												new OrTest(new Test[] {new BooleanTest(new String[] {"canDistributeMultiplication","right side"}),
																	   new BooleanTest(new String[] {"canDistribute","right side"})}),
												new OrTest(new Test[] {distIncrVarTermsLeft,distIncrVarTermsRight}),
												new OrTest(new Test[] {new BooleanTest(new String[] {"canCombineLikeTerms","left side"}),
																	   new BooleanTest(new String[] {"canCombineLikeTerms","right side"})})},
									"Distribute",
									"both",
									distMsg);

		String distMsgLit = "Your goal is to isolate " + variable +" on one side.  Determine what is in the way of " + variable + " being isolated.";
		bugRules[i++] = new BugRule("distribute var over literals, left",
									new Test[] {new OrTest(new Test[] {new BooleanTest(new String[] {"canDistributeMultiplication","left side"}),
																	   new BooleanTest(new String[] {"canDistribute","left side"})}),
												distIncrVarTermsLeft},
									"Distribute",
									"left",
									distMsgLit);
		bugRules[i++] = new BugRule("distribute var over literals, right",
									new Test[] {new OrTest(new Test[] {new BooleanTest(new String[] {"canDistributeMultiplication","right side"}),
																	   new BooleanTest(new String[] {"canDistribute","right side"})}),
												distIncrVarTermsRight},
									"Distribute",
									"right",
									distMsgLit);
		bugRules[i++] = new BugRule("distribute var over literals, both",
									new Test[] {new OrTest(new Test[] {new BooleanTest(new String[] {"canDistributeMultiplication","left side"}),
																	   new BooleanTest(new String[] {"canDistribute","left side"})}),
												new OrTest(new Test[] {new BooleanTest(new String[] {"canDistributeMultiplication","right side"}),
																	   new BooleanTest(new String[] {"canDistribute","right side"})}),
												new OrTest(new Test[] {distIncrVarTermsLeft,distIncrVarTermsRight})},
									"Distribute",
									"both",
									distMsgLit);

		/*this will let the student know why we don't allow CLT on
          things like 2*4+3*4*/
		bugRules[i++] = new BugRule("numeric clt, left",
									new CommonFactorsTest(new String[] {"terms of uncombinable terms of left side"},false),
									"CLT",
									"left",
									new String[] {"Some of the terms on the left side, {exprconjunct of terms of uncombinable terms of left side}, are like terms, but combining like terms is reserved for simplified terms. Please follow the order of operations to choose a correct simplification step."});
		bugRules[i++] = new BugRule("numeric clt, right",
									new CommonFactorsTest(new String[] {"terms of uncombinable terms of right side"},false),
									"CLT",
									"right",
									new String[] {"Some of the terms on the right side, {exprconjunct of terms of uncombinable terms of right side}, are like terms, but combining like terms is reserved for simplified terms. Please follow the order of operations to choose a correct simplification step."});

		bugRules[i++] = new BugRule("orderOpsBug",simpleOrderOpsTest,"CLT",null,"When there are no parentheses, all multiplications and divisions must be done before any additions or subtractions.");
			
		bugRules[i++] = new BugRule("action after done",
									new Test[] {new ExactEqualTest("variable side expression","target variable"),
												new BooleanTest(new String[] {"isNumber","constant side expression"},true),
												new BooleanTest(new String[] {"canSimplify","left side"},false),
												new BooleanTest(new String[] {"canSimplify","right side"},false)},
									null,
									null,
									"You already solved the equation. "+variable+" is "+"{constant side expression}. Erase your last step and pick 'done'.");
		
		bugRules[i++] = new BugRule("action after done",
									new Test[] {new NotTest(new AlgebraicEqualTest("right side","left side")),
												new NumberTest(new String[] {"length","variables","left side"},0),
												new NumberTest(new String[] {"length","variables","right side"},0)},
									null,null,
									"You already solved the equation. {left side} can never be equal to {right side}, so there is no solution to this equation. "+
									"Erase your last step and pick 'Done No Solution' from the menu.");
		
		bugRules[i++] = new BugRule("mt when dist, left",
									new BooleanTest("canDistribute of left side"),
									"mt",
									"left",
									"There are two factors that can be multiplied. However, since one of them is a quantity, it is preferable that you specify distribution as the type of multiplication.");
		bugRules[i++] = new BugRule("mt when dist, right",
									new BooleanTest("canDistribute of right side"),
									"mt",
									"right",
									"There are two factors that can be multiplied. However, since one of them is a quantity, it is preferable that you specify distribution as the type of multiplication.");
		bugRules[i++] = new BugRule("mt when dist, both",
									new Test[] {new BooleanTest("canDistribute of left side"),
												new BooleanTest("canDistribute of right side")},
									"mt",
									"both",
									"There are two factors that can be multiplied. However, since one of them is a quantity, it is preferable that you specify distribution as the type of multiplication.");

		/*student tries to factor out "a+b" from "ax+bx"*/
		bugRules[i++] = new BugRule("Factor wrong factor, right",
									new Test[] {new GreaterThanTest(new String[] {"length","variable terms","right side"},1),
												new LessThanTest(new String[] {"length","variable terms","left side"},1)},
									"fact",
									"{[standardize] [[divide] [right] [target variable]]}",
									"{[standardize] [[divide] [right] [target variable]]} is a factor, but it is not a factor of each term. Factor out the factor that is common to both terms.");
		bugRules[i++] = new BugRule("Factor wrong factor, left",
									new Test[] {new GreaterThanTest(new String[] {"length","variable terms","left side"},1),
												new LessThanTest(new String[] {"length","variable terms","right side"},1)},
									"fact",
									"{[standardize] [[divide] [left] [target variable]]}",
									"{[standardize] [[divide] [left] [target variable]]} is a factor, but it is not a factor of each term. Factor out the factor that is common to both terms.");

		BugRuleSet theRules = new BugRuleSet(bugRules,i);
		return theRules;
	}
	
	public static RuleSet defineTypeinRules() {
		return new RuleSet(null);
	}

	public static TypeinBugRuleSet defineTypeinBugRules() {
		TypeinBugRule[] tbr = new TypeinBugRule[100];
		int i=0;

		tbr[i++] = new TypeinBugRule("nothing changed",
									 new SimilarTest("left",
													 "input"),
									 null,
									 null,
									 new String[] {"You haven't performed any operations."});


		/*swapping operations (add vs subtract, mult vs divide)*/
		tbr[i++] = new TypeinBugRule("add instead of subtract",
									 new AlgebraicEqualTest("[add] [previnput] [left]",
															"input"),
									 "subtract",
									 null,
									 new String[] {"{input} is equal to {left} plus {previnput}.  You need to calculate {left} minus {previnput}."});
		tbr[i++] = new TypeinBugRule("subtract instead of add",
									 new AlgebraicEqualTest("[subtract] [left] [previnput]",
															"input"),
									 "add",
									 null,
									 new String[] {"{input} is equal to {left} minus {previnput}.  You need to calculate {left} plus {previnput}."});

		tbr[i++] = new TypeinBugRule("multiply instead of divide",
									 new AlgebraicEqualTest("[multiply] [left] [previnput]",
															"input"),
									 "divide",
									 null,
									 new String[] {"{input} is equal to {left} times {previnput}.  You need to calculate {left} divided by {previnput}."});
		tbr[i++] = new TypeinBugRule("divide instead of multiply",
									 new AlgebraicEqualTest("[divide] [left] [previnput]",
															"input"),
									 "multiply",
									 null,
									 new String[] {"{input} is equal to {left} divided by {previnput}.  You need to calculate {left} times {previnput}."});


		/*extra simplification during add, subtr, mult, or div*/
		tbr[i++] = new TypeinBugRule("extra simplification, add",
									 new SimilarTest("[standardize] [[add] [left] [previnput]]",
													 "[standardize] [input]"),
									 "add",
									 null,
									 new String[] {"Just add {left} to {previnput}.  You can simplify in the next step."});
		tbr[i++] = new TypeinBugRule("extra simplification, subtract",
									 new SimilarTest("[standardize] [[subtract] [left] [previnput]]",
													 "[standardize] [input]"),
									 "subtract",
									 null,
									 new String[] {"Just subtract {previnput} from {left}.  You can simplify in the next step."});

		tbr[i++] = new TypeinBugRule("extra simplification, multiply",
									 new SimilarTest("[standardize] [[multiply] [left] [previnput]]",
													 "[standardize] [input]"),
									 "multiply",
									 null,
									 new String[] {"Just multiply {left} by {previnput}.  You can simplify in the next step."});
		tbr[i++] = new TypeinBugRule("extra simplification, divide",
									 new SimilarTest("[standardize] [[divide] [left] [previnput]]",
													 "[standardize] [input]"),
									 "divide",
									 null,
									 new String[] {"Just divide {left} by {previnput}.  You can simplify in the next step."});


		/*reversal of args to subtraction or division*/
		tbr[i++] = new TypeinBugRule("reversed subtraction args",
									 new AlgebraicEqualTest("[subtract] [previnput] [left]",
															"input"),
									 "subtract",
									 null,
									 new String[] {"You need to calculate {left} minus {previnput}, not {previnput} minus {left}."});

		tbr[i++] = new TypeinBugRule("reversed division args",
									 new AlgebraicEqualTest("[divide] [previnput] [left]",
															"input"),
									 "divide",
									 null,
									 new String[] {"You need to calculate {left} divided by {previnput}, not {previnput} divided by {left}."});


		/*subtraction sign reversal*/
		tbr[i++] = new TypeinBugRule("subtraction sign reversal",
									 new AlgebraicEqualTest("[subtract] [negative of left] [previnput]",
															"input"),
									 "subtract",
									 null,
									 new String[] {"{input} is equal to {negative of left} minus {previnput}.  You need to calculate {left} minus {previnput}."});


		/*sign errors when multiplying & dividing*/
		/*both pos or both neg*/
		Test posMultSign = new OrTest(new Test[] {new AndTest(new Test[] {new StringTest(new String[] {"sign word","left"},
																						 "positive"),
																		  new StringTest(new String[] {"sign word","previnput"},
																						 "positive")}),
												  new AndTest(new Test[] {new StringTest(new String[] {"sign word","left"},
																						 "negative"),
																		  new StringTest(new String[] {"sign word","previnput"},
																						 "negative")})});

		/*neg & pos or pos & neg*/
		Test negMultSign = new OrTest(new Test[] {new AndTest(new Test[] {new StringTest(new String[] {"sign word","left"},
																						 "negative"),
																		  new StringTest(new String[] {"sign word","previnput"},
																						 "positive")}),
												  new AndTest(new Test[] {new StringTest(new String[] {"sign word","left"},
																						 "positive"),
																		  new StringTest(new String[] {"sign word","previnput"},
																						 "negative")})});

		tbr[i++] = new TypeinBugRule("sign error, division, positive",
									 new Test[] {new AlgebraicEqualTest("[divide] [negative of left] [previnput]",
																		"input"),
												 posMultSign},
									 "divide",
									 null,
									 new String[] {"You are dividing a {sign word of left} by a {sign word of previnput}.  The result should be positive."});
		tbr[i++] = new TypeinBugRule("sign error, division, negative",
									 new Test[] {new AlgebraicEqualTest("[divide] [negative of left] [previnput]",
																		"input"),
												 negMultSign},
									 "divide",
									 null,
									 new String[] {"You are dividing a {sign word of left} by a {sign word of previnput}.  The result should be negative."});

		tbr[i++] = new TypeinBugRule("sign error, multiplication, positive",
									 new Test[] {new AlgebraicEqualTest("[multiply] [negative of left] [previnput]",
																		"input"),
												 posMultSign},
									 "multiply",
									 null,
									 new String[] {"You are multiplying a {sign word of left} by a {sign word of previnput}.  The result should be positive."});
		tbr[i++] = new TypeinBugRule("sign error, multiplication, negative",
									 new Test[] {new AlgebraicEqualTest("[multiply] [negative of left] [previnput]",
																		"input"),
												 negMultSign},
									 "multiply",
									 null,
									 new String[] {"You are multiplying a {sign word of left} by a {sign word of previnput}.  The result should be negative."});


		/*generic negative sign errors*/
		tbr[i++] = new TypeinBugRule("forgot neg sign",
									 new Test[] {new BooleanTest(new String[] {"isNegative","expectedinput"}),
												 new AlgebraicEqualTest("[multiply] [input] ['-1']",
																		"expectedinput")},
									 null,
									 null,
									 new String[] {"You forgot the negative sign."});

		tbr[i++] = new TypeinBugRule("added extra neg sign",
									 new Test[] {new BooleanTest(new String[] {"isNegative","expectedinput"},false),
												 new AlgebraicEqualTest("[multiply] [input] ['-1']",
																		"expectedinput")},
									 null,
									 null,
									 new String[] {"The result should be positive."});


		/*mis-placed decimal point*/
		tbr[i++] = new TypeinBugRule("misplaced decimal point",
									 new OrTest(new Test[] {new AlgebraicEqualTest("[multiply] [input] ['10']",
																				   "expectedInput"),
															new AlgebraicEqualTest("[divide] [input] ['10']",
																				   "expectedInput")}),
									 null,
									 null,
									 new String[] {"You misplaced the decimal point."});


		/*forgot the variable*/
		tbr[i++] = new TypeinBugRule("forgot variable, ax",
									 new AlgebraicEqualTest(new String[] {"coefficient","item 1","variable terms","expectedInput"},
															"input"),
									 null,
									 null,
									 new String[] {"You forgot the " + variable});
		tbr[i++] = new TypeinBugRule("forgot variable, x+a",
									 new Test[] {new AlgebraicEqualTest(new String[] {"item 1","constant terms","expectedInput"},
																		"input"),
												 /*and make sure we were actually expecting a variable in the
                                                   first place ... :) */
												 new GreaterThanTest(new String[] {"length","variables","input"},0)},
									 null,
									 null,
									 new String[] {"You forgot the " + variable});


		/*combine like terms losing distribution: for example, typing
          3+7 after CLT on 3(5+2)*/
		tbr[i++] = new TypeinBugRule("clt loses distrib",
									 new OrTest(new Test[] {new AlgebraicEqualTest("[add] [[combineLikeTerms] [item 1 of components with property CanCombineLikeTerms of left]] [[divide] [left] [item 1 of components with property canCombineLikeTerms of left]]",
																				   "input"),
															new AlgebraicEqualTest("[subtract] [[combineLikeTerms] [item 1 of components with property CanCombineLikeTerms of left]] [[divide] [left] [item 1 of components with property canCombineLikeTerms left]]",
																				   "input"),
															new AlgebraicEqualTest("[subtract] [[divide] [left] [item 1 of components with property canCombineLikeTerms of left]] [[combineLikeTerms] [item 1 of components with property CanCombineLikeTerms left]]",
																				   "input")}),
									 null,
									 null,
									 new String[] {"When you combine {item 1 of components with property canCombineLikeTerms of left}, you get {[combineLikeTerms] [item 1 of components with property canCombineLikeTerms of left]}, but remember that this is multiplied by {[simplify] [[divide] [left] [item 1 of components with property canCombineLikeTerms of left]]}"});


		/*dropping the zero produced when peforming multiplication:
          for example, 3+0*3 --> 3 (should be 3+0)*/
		/*actually this will work for any CLT done after a MT*/
		tbr[i++] = new TypeinBugRule("MT and CLT at same time",
									 new SimilarTest("[combineliketerms] [[multiplythrough] [expectedInput]]",
													 "[combineliketerms] [input]"),
									 "MT",
									 null,
									 new String[] {"The value you typed, {input}, is equivalent to {left}. However, please only perform the simplification you specified, perform multiplication."});

		/*MT done after a CLT: e.g. (3+4)*5 --> 35 when it should be 7*5*/
		tbr[i++] = new TypeinBugRule("CLT and MT at same time",
									 new SimilarTest("[multiplythrough] [[combineliketerms] [expectedInput]]",
													 "[multiplythrough] [input]"),
									 "CLT",
									 null,
									 new String[] {"The value you typed, {input}, is equivalent to {left}. However, please only perform the simplification you specified, combine like terms."});


		/*rox's other desired typein bug rules (03/23/01)*/
		/*3+4*5, mt --> 7*5*/
		/*3+4*5, mt --> 12*5*/
		/*3x+4=9, div by 3 --> x+4=3*/

		/*tbr[i++] = new TypeinBugRule("forgot negative sign",
		  new 
		  null,
		  null,
		  new String[] {""});*/

		TypeinBugRuleSet ret = new TypeinBugRuleSet(tbr,i);
		return ret;
	}

	public static SkillRuleSet defineSkillRules() {
		//define common tests
		Test allIntegers = new EveryTest("all numbers","isNotDecimal");
		Test hasDecimals = new AnyTest("all numbers","isDecimal");
		Test smallNumbers = new LessThanTest(new String[] {"absolute value","all numbers"},100.0);
		Test largeNumbers = new AnyGreaterThanTest(new String[] {"absolute value","all numbers"},100.0);
		Test negative = new AnyLessThanTest("all numbers",0.0);
		Test positive = new NotTest(negative);
		

		//common test sets
		NamedTestSet smallPosInt = new NamedTestSet("small positive integers",new Test[] {allIntegers,smallNumbers,positive});
		NamedTestSet largePosInt = new NamedTestSet("large positive integers",new Test[] {allIntegers,largeNumbers,positive});
		NamedTestSet smallNegInt = new NamedTestSet("small negative integers",new Test[] {allIntegers,smallNumbers,negative});
		NamedTestSet largeNegInt = new NamedTestSet("large negative integers",new Test[] {allIntegers,largeNumbers,negative});
		NamedTestSet smallDec = new NamedTestSet("small decimals",new Test[] {hasDecimals,smallNumbers});
		NamedTestSet largeDec = new NamedTestSet("large decimals",new Test[] {hasDecimals,largeNumbers});
		
		int i=0;
		SkillRule[] rules = new SkillRule[100]; //max 100 skill rules...

//		rules[i++] = new SkillRule("x+a=b, positive");
//		rules[i++] = new SkillRule("x+a=b, negative");
		
		SkillRule[] simpleCoeffRules = SkillRule.defineRuleSet("Remove coefficient","ax=b",new NamedTestSet[] {smallPosInt,largePosInt,smallNegInt,largeNegInt,smallDec,largeDec});
//		SkillRule rule3 = new SkillRule("Remove coefficient, small int","ax=b",smallInt);
//		SkillRule rule3b = new SkillRule("Remove coefficient, large int","ax=b",largeInt);
//		SkillRule rule3c = new SkillRule("Remove coefficient, small decimal","ax=b",smallDec);
//		SkillRule rule3d = new SkillRule("Remove coefficient, large decimal","ax=b",largeDec);
		
		for (int j=0;j<simpleCoeffRules.length;++j)
			rules[i++] = simpleCoeffRules[j];

		rules[i++] = new SkillRule("ax+b=c, positive");
		rules[i++] = new SkillRule("ax+b=c, negative");
		//use Select Eliminate Parens when one side of the equation is the variable: e.g. "x=3(4+5)" -- see Eliminate Parens
		rules[i++] = new SkillRule(new String[] {"CLT nested","Distribute Mult right","Distribute Mult left"},"Select Eliminate Parens",
												 new Test[] {new AlgebraicEqualTest("target variable","variable side expression"),
												 			 new NotTest(new StringTest("variable side","both"))});
		rules[i++] = new SkillRule("MT","Select Multiply/Divide, nested",simpleOrderOpsTest);
		rules[i++] = new SkillRule("MT","Select Multiply",new Test[] {new FormTest("x=a*b")});
		rules[i++] = new SkillRule("CLT","Select Combine Terms",new Test[] {new OrTest(new Test[] {new FormTest("x=a+b"),new FormTest("x=a-b")})});
		rules[i++] = new SkillRule(new String[] {"doneleft","doneright","done no solutions","done infinite solutions"},"Done?");
		rules[i++] = new SkillRule("x+a=b, positive","Isolate positive");
		rules[i++] = new SkillRule("x+a=b, negative","Isolate negative");
		rules[i++] = new SkillRule(new String[] {"ax/b=c, reciprocal","ax/b=c","ax=b"},
								   "Remove negative coefficient",
								   new Test[] {new BooleanTest(new String[] {"isNumber","coefficient","item 1","variable terms","variable side expression"},true),
											   new LessThanTest(new String[] {"coefficient","item 1","variable terms","variable side expression"},0)});
		rules[i++] = new SkillRule(new String[] {"ax/b=c, reciprocal","ax/b=c","x/a=b","ax=b"},
								   "Remove positive coefficient",
								   new Test[] {new BooleanTest(new String[] {"isNumber","coefficient","item 1","variable terms","variable side expression"},true),
											   new GreaterThanTest(new String[] {"coefficient","item 1","variable terms","variable side expression"},0)});
		rules[i++] = new SkillRule(new String[] {"add x in -x",
												 "move neg var to other side"},
								   "Make variable positive");
		String[] remCoeffRules = new String[] {"ax+b=c, divide","ax=b",
											   "[const expr]*[var fact] + [const expr] = [const expr], divide",
											   "[var expr]*[const expr] = [const expr], divide",
											   "ax/b=c, reciprocal","ax/b=c","x/a=b","ax=b",
											   "(+/-x +/-a)/b=c, mult","a=x*(b+c)","a=x*(b-c)","a=x*(b*c+d)"};
		rules[i++] = new SkillRule(remCoeffRules,"Remove coefficient");
		rules[i++] = new SkillRule(remCoeffRules,"Make variable positive",
								   new Test[] {new BooleanTest(new String[] {"isNumber","coefficient","item 1","variable terms","variable side expression"},true),
											   new NumberTest(new String[] {"coefficient","item 1","variable terms","variable side expression"},-1)});
		rules[i++] = new SkillRule(new String[] {"ax+b=c, positive","ax+b=c, negative","x+a=b, positive","x+a=b, negative","[var expr]+[const expr]=[const expr], positive","[var expr]+[const expr]=[const expr], negative","a-x=b, positive","a/x+b=c, positive","a/x+b=c, negative"},
									"Remove constant");
		rules[i++] = new SkillRule(new String[] {"a/x=[anything]","a/x=b","a/x=b, sophisticated"},"Variable in denominator");
		rules[i++] = new SkillRule("CLT","Consolidate vars, no coeff",
									new Test[] {new MemberTest("target variable",new String[] {"variable terms","variable side expression"})});
		rules[i++] = new SkillRule("CLT","Consolidate vars with coeff",
									new Test[] {new NotTest(new MemberTest("target variable",new String[] {"variable terms","variable side expression"}))});
		//use Eliminate Parens when either:
		// 1 - there are variables on both sides of the equation
		// 2 - one side of the equation is not the variable
		//This ensures that we're simplifying a side of the equation that includes the variable, e.g. 3(5+x)=12
		//CLT nested handles X(3+5)=4, but maybe this should be a different skill
		rules[i++] = new SkillRule(new String[] {"CLT nested",
												 "Distribute Mult right",
												 "Distribute Mult left",
												 "(+/-x +/-a)/b=c, mult",
												 "(+/-x +/-a)*b=c, div",
												 "Distribute Division left",
												 "Distribute Division right",
												 "Distribute both mult left",
												 "Distribute both mult right",
												 "Distribute both divide left",
												 "Distribute both divide right"},
												 /*this rule used to be too broadly defined,
												   so it was firing in some cases that were
												   appropriate for eliminate parens.  that's
												   been fixed, so we don't need to list it
												   here any more.*/
												 //"[const expr]*[var fact] + [const expr] = [const expr], divide"},
								   "Eliminate Parens",
								   new Test[] {new OrTest(new Test[] {new NotTest(new AlgebraicEqualTest("target variable","variable side expression")),
																	  new StringTest("variable side","both")})});

		/*these actions only eliminate parens if the var expr has more
          than one term*/
		rules[i++] = new SkillRule(new String[] {"[var expr]/[const expr] = [const expr], multiply",
												 "[var expr]*[const expr] = [const expr], divide"},
								   "Eliminate Parens",
								   new Test[] {new OrTest(new Test[] {new NotTest(new AlgebraicEqualTest("target variable","variable side expression")),
																	  new StringTest("variable side","both")}),
											   new OrTest(new Test[] {new GreaterThanTest(new String[] {"length","terms","variable factor","item 1","variable terms","variable side expression"},
																						  1),
																	  new GreaterThanTest(new String[] {"length","terms","negative","variable factor","item 1","variable terms","variable side expression"},
																						  1)})});

		//I'm not clear on when all the variants of "combine variables" get used, so I've added all variants here
		//I suspect that some variants would never get used, though.
		rules[i++] = new SkillRule(new String[] {"Combine variables to right, sub","Combine variables to right, add","Combine variables to right, gen","Combine variables to left, add",
												 "Combine variables to left, sub","Combine variables to left, gen","Combine variables to right"},
								   "Consolidate vars, any");
		
		rules[i++] = new SkillRule(new String[] {"factor","factorb"},
								   "Extract to consolidate vars");

		//aposteriory test set for sides
		Test wasDistributed = new NotTest(new BooleanTest("canDistribute"));

//		SkillRule[] rules = {rule1,rule2,rule3,rule3b,rule3c,rule3d,rule4,rule5};
		return new SkillRuleSet(rules,i);
	}
	
	//define Typein skills
	//I don't really like how this works -- typein skill rules should probably have a different format than strategic skill rules
	//since they generally just mimic one or more strategic skills (it'd be nice to say "if this strategic skill fired, then the typein skill is this)
	//Also, we should be able to specify the side of the equation that the typein skill applies to, instead of just crudely monitoring the first side
	//the student types on.
	public static TypeinSkillRuleSet defineTypeinSkillRules() {
		int i=0;
		TypeinSkillRule[] rules = new TypeinSkillRule[50]; //max 50 skill rules...
		
		//Do Eliminate Parens is bascially the same as Select Eliminate Parens but applied to the typein action
		rules[i++] = new TypeinSkillRule("Select Eliminate Parens","Do Eliminate Parens - whole");
		
		//Do Multiply - Whole (typein-expression-2) is the same as select multiply, applied to typein
		rules[i++] = new TypeinSkillRule("Select Multiply","Do Multiply - Whole (typein-expression-2)");

		//Do Combine Terms - Whole is the same as select combine terms, applied to typein
		rules[i++] = new TypeinSkillRule("Select Combine Terms","Do Combine Terms - Whole");
		
		// Add/Subtract combines: isolate positive, isolate negative, remove constant
		rules[i++] = new TypeinSkillRule(new String[] {"Isolate positive",
													   "Isolate negative",
													   "Remove constant",
													   "Consolidate vars, any"},
										 "Add/Subtract");
		
		// Multiply/Divide corresponds to remove coefficient
		rules[i++] = new TypeinSkillRule(new String[] {"Remove coefficient",
													   "Variable in denominator"},
										 "Multiply/Divide");
									
		//Calculate negative coefficient corresponds to make variable positive
		rules[i++] = new TypeinSkillRule("Make variable positive","Calculate negative coefficient");

		//Calculate Eliminate Parens is the same as Eliminate Parens
		rules[i++] = new TypeinSkillRule("Eliminate Parens","Calculate Eliminate Parens");

		rules[i++] = TypeinSkillRule.makeTISkill("Extract to consolidate vars");

		return new TypeinSkillRuleSet(rules,i);
	}

}
