package edu.cmu.old_pact.cmu.sm;


/*we keep the parser tests here so that we don't have to re-run javacc
  every time we add or change a test.*/

public class TestSM{
	/*public static void testParser(){
	  try{
	  char[] b = new char[4];
	  int i;
	  String s;
	  do{
	  for(i=0;i<4 && !(b[i] == '\n' || b[i] == '\r' || b[i] == '\010');b[i++] = (char)System.in.read());
	  
	  s = new String(b,0,i);
	  trace.out("TestSM: " + s);
	  }while(s.length() <= 1);
	  SM.testParse(s);
	  }
	  catch(IOException ioe){
	  trace.out("TestSM.tP: " + ioe);
	  }
	  }*/

	//testParser is a routine to test out the parser
	public static void testParser(){
        SM.testParse("2+3");
		SM.testParse("2-3");
		SM.testParse("2+3+4-5");
		SM.testParse("1000");
		SM.testParse("1,000","1000");
		SM.testParse("1000000");
		SM.testParse("1,000,000","1000000");
		SM.testParse("1,000000");
		//SM.testParse("1000,000");
		SM.testParse("10,000","10000");
		SM.testParse("100,000","100000");
		//SM.testParse("10,00");
		SM.testParse("1");
		SM.testParse("+1","1");
		SM.testParse("-1");
		SM.testParse("--1","-(-1)");
		SM.testParse("-(-1)");
		SM.testParse("-1*-1");
		SM.testParse("5");
		SM.testParse("+5","5");
    	SM.testParse("-5");
		SM.testParse("--5","-(-5)");
		SM.testParse("-(-5)");
		SM.testParse("-1*-5");
		SM.testParse("x");
		SM.testParse("+x","x");
		SM.testParse("-x");
		SM.testParse("--x","-(-x)");
		SM.testParse("-(-x)");
		SM.testParse("-1*-x");
		SM.testParse("--xy","-(-x)*y");
		SM.testParse("--x/y","-(-x)/y");
    	SM.testParse("2+x");
    	SM.testParse("x-y");
    	SM.testParse("3x");
    	SM.testParse("3*x","3x");
    	SM.testParse("3x-4");
    	SM.testParse("3*x-4","3x-4");
    	SM.testParse("2*x*y","2x*y");
    	SM.testParse("2xy","2x*y");
    	SM.testParse("4.5");
    	SM.testParse("4.5*x","4.5x");
    	SM.testParse("3/x");
    	SM.testParse("3x/5");
    	SM.testParse("-2x");
    	SM.testParse("-2x*-5y");
    	SM.testParse("5-2x");
        SM.testParse("4x+3.5x");
        SM.testParse("4*x+3.5*x","4x+3.5x");
        SM.testParse("2.1x+6.9x");
        SM.testParse("2.2x-3.79x");
        SM.testParse("2/4 + 1.5x","2/4+1.5x");
        SM.testParse("2/4x + -3.5x","2/4*x+(-3.5x)");
        SM.testParse("(x+1)*(x+2)*(x+3)","(x+1)(x+2)(x+3)");
        SM.testParse("1661-624/(1991-1985)");
        SM.testParse("3x^2 - x^2","3x^2-x^2");
        SM.testParse("3x");
        SM.testParse("0-x");
        SM.testParse("-x+5");
        SM.testParse("0-(-x+5)");
        SM.testParse("x*3-4");
        SM.testParse("(125+60)+5545");
        SM.testParse("-x*(125+60)+5545","-x(125+60)+5545");
        SM.testParse("(-x*(125+60)+5545)","(-x(125+60)+5545)");
        SM.testParse("0-(-x*(125+60)+5545)","0-(-x(125+60)+5545)");
        SM.testParse("x^2");
        SM.testParse("x^-1");
        SM.testParse("3x^-1");
        SM.testParse("3xy^-2","3x*(y^-2)");
        SM.testParse("(x+2)^2");
        SM.testParse("3x + 4x + 5 + 7*8","3x+4x+5+7*8");
        SM.testParse("3^2");
        SM.testParse("(1/2)^2");
        SM.testParse("sqrt(3x+4)");
        SM.testParse("sqrt(4x^2)");
        SM.testParse("root(3x+4,3)");
        SM.testParse("(x+y+z)^-1");
        SM.testParse("(x*y*z)^-1");
        SM.testParse("[x*y*z]^-1","(x*y*z)^-1");
        SM.testParse("2(x+4)");
        SM.testParse("a/bx");
        SM.testParse("a/3x");
        SM.testParse("3/bx","3/b*x");
        SM.testParse("a/bx+c");
        SM.testParse("ab+cd","a*b+c*d");
        SM.testParse("ab*c","a*b*c");
        SM.testParse("ab*cd","a*b*c*d");
        SM.testParse("ab/cd");
        SM.testParse("[ab]/[cd]","a*b/(c*d)");
        SM.testParse("(ab)/(cd)","(a*b)/(c*d)");
        SM.testParse("[3x]/[4y]","3x/(4y)");
        SM.testParse("[21]/[43]","21/43");
        SM.testParse("1/2/3","(1/2)/3");
        SM.testParse("(x^2)(y^2)");
        SM.testParse("x^2*y^2","(x^2)(y^2)");
        SM.testParse("x^2y^2","(x^2)(y^2)");
        SM.testParse("2^3^4","(2^3)^4");
        SM.testParse("a^b^c","(a^b)^c");
        SM.testParse("2x^2");
        SM.testParse("2*x^2","2x^2");
        SM.testParse("ax^2","a*(x^2)");
        SM.testParse("a*x^2","a*(x^2)");
        SM.testParse("a*x^2+bx+c","a*(x^2)+b*x+c");
        SM.testParse("ax^2+bx+c","a*(x^2)+b*x+c");
        SM.testParse("c+ax^2+bx","c+a*(x^2)+b*x");
        SM.testParse("3x^2+4x+5");
        SM.testParse("3x^2+5+4x");
        SM.testParse("4x+3x^2+5");
        SM.testParse("4x+5+3x^2");
        SM.testParse("5+3x^2+4x");
        SM.testParse("5+4x+3x^2");
        SM.testParse("2bc*d","2b*c*d");
        SM.testParse("2(y^2)x*1/3","2(y^2)*x*1/3");
        //there doesn't seem to be a really good way to test constants
        //at this level, other than making sure it doesn't insert a *
        //(explicit multipilcation) where it normally would ... for
        //one-char constants, I'm pretty clueless; we'll prolly have
        //to rely on the symbol manipulator test suite for those.
		//Luckily I'm just doing pi at the moment.  :)
        SM.testParse("ab*(r^2)","a*b*(r^2)");
        SM.testParse("pi*(r^2)");
        SM.testParse("a/bc+3de/d","a/b*c+3d*e/d");
        SM.testParse("a/pi+3pi/p");
		SM.testParse("a^b");
		SM.testParse("a^b+c");
		SM.testParse("a^bc","(a^b)c");
		SM.testParse("a^bc+d","(a^b)c+d");
		SM.testParse("a^(b+c)");
		SM.testParse("(a+b)^c");
		SM.testParse("(a+b)^(c+d)");
		SM.testParse("ax^m+bx^n","a*(x^m)+b*(x^n)");
		SM.testParse("0*2+1");
		SM.testParse("1*2+0");
		SM.testParse("x(a+b)","x*(a+b)");
		SM.testParse("(a+b)x","(a+b)*x");
		SM.testParse("-0");
		SM.testParse("+0","0");
		SM.testParse("-0x","-0*x");
		SM.testParse("1-0");
		SM.testParse("1-0x");
		SM.testParse("(40+-9)/8","(40+(-9))/8");
		SM.testParse("(x*y)^-1");
		SM.testParse("abc*def","a*b*c*d*e*f");
		SM.testParse("ab*cd*ef","a*b*c*d*e*f");
		SM.testParse("abc*def*ghi","a*b*c*d*e*f*g*h*i");
		SM.testParse("|2","sqrt(2)");
		//option-v on the mac
		SM.testParse("\u001A(3x+4)","sqrt(3x+4)");
		SM.testParse("|x","sqrt(x)");
		SM.testParse("|(4+5)","sqrt(4+5)");
		SM.testParse("|((4+5))","sqrt((4+5))");
		SM.testParse("|[4+5]","sqrt(4+5)");
		SM.testParse("|(b^2-4ac)","sqrt(b^2-4a*c)");

		NumberExpression.setUseSigFigs(true);
		SM.testParse("100");
		SM.testParse("100.0");
		SM.testParse("100.00");
		SM.testParse("1,000","1000");
		SM.testParse("1,000.0","1000.0");
		SM.testParse("1,000.00","1000.00");
		NumberExpression.setUseSigFigs(false);

		SM.testParse("(5)");
		SM.testParse("((5))");
		SM.testParse("(((5)))");

		SM.testParse("3/4^2");
		SM.testParse("3/a^2");

		//Most folks think this sort of thing shouldn't parse ...
		SM.testParse("x3","x*3");
		SM.testParse("f0.8","f*0.8");
		SM.testParse("3x4","3x*4"); //not 3*4 :)
		SM.testParse("3a4b5c6","3a*4b*5c*6");

		//the following expressions are expected to fail; they're in
		//here because they used to take astronomically long to do so.
		//In fact, if you just prepend a few more parens you can still
		//make them take quite a while, but now you have to have five
		//or six left parens, whereas before it only took two or
		//three.

		//SM.testParse("(5");
		//SM.testParse("((5");
		//SM.testParse("(((5");
		//SM.testParse("((((5");
		//SM.testParse("(((((5");
		//SM.testParse("((((((5");
		//SM.testParse("13.99((x-12)");
		//SM.testParse("3((x*(3+((4");
	}
}
