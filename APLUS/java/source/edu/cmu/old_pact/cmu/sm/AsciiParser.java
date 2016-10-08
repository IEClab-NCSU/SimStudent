//AsciiParser is an ExpressionParser that parses a somewhat-standard Ascii representation
//of an expression. This is the default parser used by the Symbol Manipulator

package edu.cmu.old_pact.cmu.sm;

import java.io.IOException;
import java.io.StringBufferInputStream;

public class AsciiParser implements ExpressionParser {
	private static Object mutex = new Object();
	private static SMTokenManager tokes = null;
	private static SM parser = null;
	
	public AsciiParser() {
	}

	//we don't actually initialize the parser at the beginning, since it need to be re-initialized each time
	//we parse something anyway
	public void init() {
	}
	
        public Expression parse(String theExpression) throws ParseException{
            return parse(theExpression,null,false);
        }

        public Expression parse(String theExpression,String[] vars) throws ParseException {
            return parse(theExpression,vars,true);
        }

	private Expression parse(String theExpression,String[] vars,boolean usevars) throws ParseException {
		synchronized(mutex){
			//		Expression result = (Expression)(cache.get(theExpression));
			Expression result = null;
			
			if (result == null) {

				//ASCII_CharStream instream = new ASCII_CharStream(new java.io.StringBufferInputStream(theExpression),0,0,theExpression.length());
				StringBufferInputStream stringStream= new StringBufferInputStream(theExpression+"\n");
				ASCII_CharStream instream = new ASCII_CharStream(stringStream,0,0,theExpression.length()+1);

				if (tokes == null) {
					tokes = new SMTokenManager(instream);
					parser = new SM(tokes);
				}
				else {
					SMTokenManager.ReInit(instream); //re-initialize token manager (get reused each time)
					parser.ReInit(tokes);
				}
				if (theExpression.equals(""))
					result = new BadExpression();
				else {
					if(usevars){
						result = SM.readExpression(vars);
					}
					else{
						result = SM.readExpression();
					}
				}
				SM.tokes = null;
				SM.parser = null;
				try{
					stringStream.close();
				}catch (IOException e) { }
				instream.Done();
				//tokes = null;
				//parser = null;
			}
			//		return (Expression)(result.clone()); //cloning ensures that identical objects aren't returned, since Expressions can be destructively modified
			return result;
		}
	}
}

