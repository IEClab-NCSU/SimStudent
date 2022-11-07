package edu.cmu.pact.miss.userDef.algebra.expression;


import edu.cmu.pact.Utilities.trace;

/**
 * A class to parse Strings into AlgExps, functions as a combination lexer and parse
 * @author ajzana
 *
 */

public class AlgExpParser
{
	//states in the finite state machine for lexing
    private final static int INTSTATE=6;
    private final static int FLOATSTATE=1;
    private final static int RIGHTPARENSTATE=2;
    private final static int MULSTATE=3;
    private final static int INITSTATE=0;
    private final static int ADDSTATE=4;
    private final static int DIVSTATE=5;
    private final static int ERRORSTATE=-1;
    private static final int VARSTATE=7;
    private static final int NEGSTATE=8;
    private final static int LEFTPARENSTATE=9;
    private final static int MULOPSTATE=10;

    public static String stateName (int i){
    	switch(i){
    	case -1:
    		return "ERRORSTATE";
    	case 0:
    		return "INITSTATE";
    	case 1:
    		return "FLOATSTATE";
    	case 2:
    		return "RIGHTPARENSTATE";
    	case 3:
    		return "MULSTATE";
    	case 4:
    		return "ADDSTATE";
    	case 5:
    		return "DIVSTATE";
    	case 6:
    		return "INTSTATE";
    	case 7:
    		return "VARSTATE";
    	case 8:
    		return "NEGSTATE";
    	case 9:
    		return "LEFTPARENSTATE";
    	case 10:
    		return "MULOPSTATE";    		
    	}
    	return "OOPS_STATE";
    }
    
    private static boolean debugFlag = false;//true;
    
	public static int charCount(String s, char c){
		char[] ca = s.toCharArray();
		int chCount = 0;
		for (int i=0; i < s.length(); i++){
			if (ca[i] == c)
				chCount++;
		}
//                trace.out("returning chCount = " + chCount);
		return chCount;
	}
	
	public static void debugPrintln (String s){
		if (debugFlag){
			trace.out("(gustavo): " + s);
		}			
	}
    
    public static boolean member (String s, String[] coll){
    	for (int i=0; i<coll.length; i++)
    		if (s.equals(coll[i]))
    			return true;
		return false;
    }
    
    //Gustavo 20Nov2006: This function makes sure that SimSt works with exprs like
    //"--x" and "+6"
    public static String cancelDoubleMinus(String s){
	String res = s.replaceAll("--", "+");
	
	if(res.charAt(0)=='+')
	    res = res.substring(1);

	res = res.replaceAll("\\(\\+", "(");
	
	return res;
    }    
    
    //This parser has 2 parts.
    //PART 1: use a finite-state machine to identify lastOp, leftOperand, rightOperand
    //Expressions get parsed as [ leftOperand | lastOp | rightOperand ]
    //PART 2: call 'parse' recursively
    public static AlgExp parse(String s) throws ExpParseException
    {

//        trace.out("s = " + s);
        
        //if more than one '/' or unbalanced parentheses, return null
    	//Gustavo 21Oct2006: should we deal with empty strings?
    	if (//(charCount(s,'/') > 1) || //charCount(s,'(')!=charCount(s,')') ||
    					s.length() < 1 ||
    	    			s.substring(0, 1).equals("*") || s.substring(0, 1).equals("/")
    	    			//s.substring(0, 1).equals("+"))
    	    			)
    	    return null;

	s = cancelDoubleMinus(s);
    	
    	//This counter is our ad-hoc way of pruning the search space,
    	//by eliminating strings with more than one '/'. It should be removed eventually,
    	//once the parser is fixed. -Gustavo
//    	int divstateCounter = 0;
    	
    	/*String[] debugCases = {"(-6+10x)/(3x-5)",
    			"(-7x-5)*-6",
    			"2+(-x)/(-1)",
    			"/(3x-5)",
    			"(a+b)/(c+d)",
    			"(a+b)/c+d",
    			"((x+3)*2)/5",
    			"(-7x-5)*-6"
    			//"a*b+c*d",
    			//"-(x+1)
    			//"-1(x+1)"
    	};
    	
    	debugFlag = false;//member(s, debugCases);

    	debugPrintln("------ parsing " + s);
    	*/
    	
    	s=s.toLowerCase();

        if(s.equals("-"))//treat a single negative sign as negative on as in -x
            return AlgExp.NEGONE;
        char[] exp=s.toCharArray();
        int curState=INITSTATE;
        AlgExp leftOperand;
        AlgExp rightOperand;
        int leftLength=0;//length of the left operand
        int rightStart=0;//index where the right operand starts
        int rightLength=0;//length of the right operand
        int parenCount=0;//used for paren matching, parens are balanced when zero
        int stringStart=0;//index in the string where the actual expression starts (may be different from start of String if outer parens are stripped)
        int stringEnd=exp.length;//index in the string where the actual expression ends (may be different from end of String if outer parens are stripped)
        int stringLength=exp.length;
        
        /*debugPrintln("\n ==INITIAL VALUES== \n  ---------- s = [" + s + "]   " +
    				"  leftLength = " + leftLength + 
    				"  rightStart = " + rightStart + 		
    				"  rightLength = " + rightLength + 
    				"  stringLength = " + stringLength);
        */
        
        //defaults:
        char lastOp='+';
        boolean isPoly=false;//flag indicating whether this expression is a polynomial
        boolean isComplex=false;//false if the expression is just a constant or a variable (or a constant fraction), true otherwise
        boolean hasParens=false;
        
        if(IsSurroundedByParens(exp,stringStart,stringEnd-1))
        {
            //ignore outer most set of parens, but remember they were there
            stringStart++;
            stringEnd--;
            stringLength-=2;
            hasParens=true;
        }
        
        parseLoop: for(int charIndex=stringStart; charIndex<stringEnd; charIndex++)
        {
            char curChar=exp[charIndex];
            
            //code to balance parens
            if(curChar=='(')
                parenCount++;
            if(curChar==')')
                parenCount--;

            /*debugPrintln("s=[" + s + "]   curState=" + stateName(curState) +  "   curChar='" + curChar +
            		"'   charIndex=" + charIndex + "   parenCount=" + parenCount + "   isPoly=" + isPoly);
            */
            int nextState=transition(curState,curChar,parenCount);

            //debugPrintln("nextState=" + stateName(nextState)); 
            
            curState=nextState;

            switch(nextState)
            {
            case ERRORSTATE:
                throw new ExpParseException(s+" "+curChar);

            case ADDSTATE:
                lastOp='+';
                isPoly=true;
                isComplex=true;
                if(curChar=='+') //normal addition, e.g. [a+b] --> [a|b],+
                {
                    leftLength=charIndex-stringStart;

                    rightStart=charIndex+1;
                    rightLength=stringLength-leftLength-1;
                }
                else //subtraction, e.g. [a-b] --> [a|-b],+
                {
                    leftLength=charIndex-stringStart;

                    rightStart=charIndex;
                    rightLength=stringLength-leftLength;
                }
                break;

            case DIVSTATE:
                // TODO
//            	divstateCounter++;

            	//this is our ad-hoc way of pruning the search space. -Gustavo
//            	if (divstateCounter > 1){
//            		return null;
//            	}
            	
                if(!isPoly && parenCount == 0)
                {
                    lastOp='/';
                    leftLength=charIndex-stringStart;
                    rightStart=charIndex+1;
                    rightLength=stringLength-leftLength-1;
                    isComplex=true;
                }
                break;

            case MULSTATE:
                isComplex=true;
                // TODO
                
                //This special case is needed because of cases like -1(x+1)
                //in which the operands need to be set.   -Gustavo 20/Sep/2006
                if(!isPoly && parenCount == 1 && curChar=='('){
                	lastOp='*';
                	leftLength=charIndex-stringStart;
                	rightStart=charIndex;
                    rightLength=stringLength-leftLength;
                }

                if(!isPoly && parenCount == 0)
                {
                    lastOp='*';
                    if(curChar=='*')
                    {
                        leftLength=charIndex-stringStart;
                        rightStart=charIndex+1;
                        rightLength=stringLength-leftLength-1;
                    }
                    else
                    {
                        leftLength=charIndex-stringStart;
                        rightStart=charIndex;
                        rightLength=stringLength-leftLength;
                    }
                }
                
                if(curChar=='(')
                    curState=LEFTPARENSTATE;
                
                break;

            case MULOPSTATE:
                debugPrintln("MULOPSTATE entered");
                
                isComplex=true;
                if(curChar=='(')
                    curState=LEFTPARENSTATE;

                // TODO
                if(!isPoly && parenCount == 0)
                {
                    lastOp='*';
                    if(curChar=='*')
                    {
                        leftLength=charIndex-stringStart;
                        rightStart=charIndex+1;
                        rightLength=stringLength-leftLength-1;
                        /* debugPrintln("==ELSE== \n  ---------- s = [" + s + "]   " +
                				"  leftLength = " + leftLength + 
                				"  rightStart = " + rightStart + 		
                				"  rightLength = " + rightLength + 
                				"  stringLength = " + stringLength +
                				"  parenCount = " + parenCount); */
                    }
                    else
                    {
                        leftLength=charIndex-stringStart;
                        rightStart=charIndex;
                        rightLength=stringLength-leftLength;
                        /*debugPrintln("==ELSE== \n  ---------- s = [" + s + "]   " +
                				"  leftLength = " + leftLength + 
                				"  rightStart = " + rightStart + 		
                				"  rightLength = " + rightLength + 
                				"  stringLength = " + stringLength +
                				"  parenCount = " + parenCount);*/
                    }
                }
                break;

            } // end of switch(nextState)
        } // end of for(int charIndex=stringStart; charIndex<=stringEnd; charIndex++)

        
        /*debugPrintln("==VALUES AFTER LOOP== \n  ---------- s = [" + s + "]   " +
    				"  leftLength = " + leftLength + 
    				"  rightStart = " + rightStart + 		
    				"  rightLength = " + rightLength + 
    				"  stringLength = " + stringLength +
    				"  parenCount = " + parenCount); */
        
        //assert(rightStart == stringStart + leftLength);
        //assert(stringLength == leftLength + rightLength + 1);
        
        if(parenCount!=0) {
            throw new ExpParseException(s);
        }

        if(isComplex) {
            //if expression is complex, parse the left and right halves recursively
            //debugPrintln("isComplex entered");
            
            String leftStr = new String(exp,stringStart,leftLength);
            String rightStr = new String(exp,rightStart,rightLength);
            debugPrintln("   [" + s + "] --> [" + leftStr + "|" +  rightStr + "]    lastOp = '" + lastOp +"'");
            
            leftOperand=parse(leftStr);
            rightOperand=parse(rightStr);
            
            return makeExp(lastOp,leftOperand,rightOperand,hasParens);
        }
        //base cases for constants and variables 
        if(curState==INTSTATE) {
            return new IntConst(Integer.parseInt(s.substring(stringStart,stringEnd)));
        }
        if(curState==FLOATSTATE) {
            return new DoubleConst(Double.parseDouble(s.substring(stringStart,stringEnd)));
        }
        if(curState==VARSTATE) {
            return new Variable(s.substring(stringStart,stringEnd));
        }
        //if you get here something went wrong
        throw new ExpParseException(s);
    }

    /**
     * transition function for the finite state machince
     * @param startState int representing the current state
     * @param c char which is the next input
     * @return  int constant representing the next state 
     */
    private static int transition(int startState,char c, int parenCount)
    {    	
        switch(startState)
        {        
        //'-' could either mean NEGSTATE (negative number/expression) or ADDSTATE (subtraction)
        //You can tell by the context, i.e. the current state. -Gustavo 19Sep2006
        case INITSTATE:
        	//debugPrintln("entered INITSTATE");
            if(c>='0'&&c<='9')
                return INTSTATE;
            if(c>='a'&&c<='z')
                return VARSTATE;
            if(c=='-')
                return NEGSTATE; //as the first thing means 
            if(c=='.')
                return FLOATSTATE;
            if(c=='(')
                return LEFTPARENSTATE;
            
        case LEFTPARENSTATE:
            if((c==')') && parenCount==0)
                return RIGHTPARENSTATE;
            
                return LEFTPARENSTATE;
                  
        case INTSTATE:
            if(c>='0'&&c<='9')
                return INTSTATE;
            if(c>='a'&&c<='z')
                return MULSTATE;
            if(c=='(')
                return MULSTATE;
            if(c=='-')
                return ADDSTATE;
            if(c=='.')
                return FLOATSTATE;
            if(c==')')
                return RIGHTPARENSTATE;
            if(c=='+')
                return ADDSTATE;
            if(c=='/')
                return DIVSTATE;
            if(c=='*')
                return MULOPSTATE;
            return ERRORSTATE;
            
        case FLOATSTATE:
            if(c>='0'&&c<='9')
                return FLOATSTATE;
            if(c>='a'&&c<='z')
                return MULSTATE;
            if(c=='(')
                return MULSTATE;
            if(c=='-')
                return ADDSTATE;
            if(c=='+')
                return ADDSTATE;
            if(c=='/')
                return DIVSTATE;
            if(c==')')
                return RIGHTPARENSTATE;
            if(c=='*')
                return MULOPSTATE;
            
            return ERRORSTATE;
            
            //the purpose of NEGSTATE         -Gustavo, 19/Sep/2006
        case NEGSTATE:
            if(c>='0'&&c<='9')
                return INTSTATE;
            if(c>='a'&&c<='z')
                return MULSTATE;
            if(c=='(')
                return MULSTATE;
            if(c=='.')
                return FLOATSTATE;
            return ERRORSTATE;
         
        case VARSTATE:
            if(c>='0'&&c<='9')
                return MULSTATE;
            if(c>='a'&&c<='z')
                return MULSTATE;
            if(c=='(')
                return MULSTATE;
            if(c=='-')
                return ADDSTATE;
            if(c==')')
                return RIGHTPARENSTATE;
            if(c=='+')
                return ADDSTATE;
            if(c=='/')
                return DIVSTATE;
            if(c=='*')
                return MULOPSTATE;
            return ERRORSTATE;
            
        case RIGHTPARENSTATE:
            if(c>='0'&&c<='9')
                return MULSTATE;
            if(c>='a'&&c<='z')
                return MULSTATE;
            if(c=='(')
                return MULSTATE;
            if(c=='-')
                return ADDSTATE;
            if(c==')')
                return RIGHTPARENSTATE;
            if(c=='+')
                return ADDSTATE;
            if(c=='/')
                return DIVSTATE;
            if(c=='*')
                return MULOPSTATE;
            return ERRORSTATE;
            
        case ADDSTATE:
        	if(c>='0'&&c<='9')
                return INTSTATE;
            if(c>='a'&&c<='z')
                return VARSTATE;
            if(c=='.')
            	return FLOATSTATE;
            if(c=='(')
                return LEFTPARENSTATE;
            if(c=='-')
                return INTSTATE;  //Gustavo 21Oct2006: a+-b should parse as [a|-b]
            if(c=='/')
                return DIVSTATE;
            
            
        case DIVSTATE:
        	if(c>='0'&&c<='9')
                return INTSTATE;
            if(c>='a'&&c<='z')
                return VARSTATE;
            if(c=='.')
            	return FLOATSTATE;
            if(c=='(')
                return LEFTPARENSTATE;
            if(c=='-')
                return NEGSTATE;
            if(c=='+')
            	return ADDSTATE;
            if(c=='/')
                return DIVSTATE;
            
        case MULSTATE:
        	if(c>='0'&&c<='9')
                return INTSTATE;
            if(c>='a'&&c<='z')
                return MULSTATE;
            if(c=='.')
            	return FLOATSTATE;
            if(c=='(')
                return LEFTPARENSTATE;
            if(c=='-')
                return ADDSTATE;
            if(c=='+')
            	return ADDSTATE;
            if(c=='/')
                return DIVSTATE;            
            if(c=='*')
                return MULOPSTATE;
            return ERRORSTATE;
            

        case MULOPSTATE:
        	if(c>='0'&&c<='9')
                return INTSTATE;
            if(c>='a'&&c<='z')
                return VARSTATE;
            if(c=='.')
            	return FLOATSTATE;
            if(c=='(')
                return LEFTPARENSTATE;
            if(c=='-')
                return NEGSTATE;
            
        	
        default:
               return ERRORSTATE;
            
       
        }
    }
    private static boolean IsSurroundedByParens(char[] exp,int startIndex,int endIndex)
    {
        if(exp.length==0||!(exp[startIndex]=='(' && exp[endIndex]==')'))
            return false;
        int parenCount=1;
        
        //this returns false if the parenCount is ever 0, i.e. the first paren does not match the 
        //e.g. an expression like (1+2)*(x+3) is not considered "surrounded by parens".
        for(int i=startIndex+1; i<=endIndex; i++)
        {
            if(exp[i]=='(')
                parenCount++;
            if(exp[i]==')')
                parenCount--;
            if(parenCount==0 && i!=endIndex)
                return false;
        }
        return true;
    }

    private static AlgExp makeExp(char lastOp, AlgExp leftOperand, AlgExp rightOperand, boolean hasParens)
    throws ExpParseException
    {
    	//trace.out("makeExp entered");
    	//debugPrintln("makeExp entered");

    	//trace.out("lastOp='" + lastOp + "'   leftOperand=" + leftOperand.toString() + "   rightOperand=" + rightOperand.toString());
    	
    	AlgExp result;
        switch(lastOp)
        {
        case '+':
            result= new Polynomial(leftOperand,rightOperand);
            result.setParenBit(hasParens);
            return result;

        case '*':
        	//trace.out("case *");
            if(leftOperand.isConstant() && rightOperand.isVariable())
            {
            	//trace.out("const-var");
                result=new SimpleTerm(leftOperand,rightOperand);
                result.setParenBit(hasParens);
                //trace.out("result="+result);
                return result;
            }

            if(leftOperand.isVariable() && rightOperand.isConstant())
            {
            	//trace.out("var-const");
            	result= new SimpleTerm(rightOperand,leftOperand);
                result.setParenBit(hasParens);
                return result;
            }
        	//trace.out("neither");
            result= new ComplexTerm(leftOperand,rightOperand);
            result.setParenBit(hasParens);
            return result;

        case '/':
            if(leftOperand.isConstant() && rightOperand.isConstant() )
            {
                result=new ConstantFraction(leftOperand,rightOperand);
                result.setParenBit(hasParens);
                return result;
            }
            /* Noboru :: "x/3" must be a complexFraction? 
            if(leftOperand.isVariable()  && rightOperand.isConstant())
            {
                result= new SimpleTerm(rightOperand.invert(),leftOperand);
                result.setParenBit(hasParens);
                trace.out("makeExp: SimpleTerm...");
                return result;
            }
            */
            if(leftOperand instanceof SimpleTerm && ! leftOperand.hasParens() && rightOperand.isConstant())
            {
                // trace.out("SimpleTerm(ConstantFraction)");
                SimpleTerm t=(SimpleTerm)leftOperand;
                result= new SimpleTerm(new ConstantFraction(t.getConstant(),rightOperand),t.getVariable());
                result.setParenBit(hasParens);
                return result;
            }

            // trace.out("ComplexFraction");
            result= new ComplexFraction(leftOperand,rightOperand);
            result.setParenBit(hasParens);
            return result;

        default:
            throw new ExpParseException(String.valueOf(lastOp));
        }
    }
    
}
