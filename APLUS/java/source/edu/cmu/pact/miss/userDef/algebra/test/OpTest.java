package edu.cmu.pact.miss.userDef.algebra.test;
import java.util.Arrays;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.FeaturePredicate;

public class OpTest 
{
    static boolean fpTest( String testName, Object[] args, String expectedVal ) {

	return FeaturePredicate.testUserDefSymbols( testName, args, expectedVal, printPasses );
    }
    static boolean printPasses=true;//flag to indicate whenever passed test results should get printed on now
    /**
     * Describe <code>main</code> method here.
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(final String[] args) {
	int errorCount=0;
	if(args.length!=0)
	{
	    //handle cmdline flags
	    if(Arrays.asList(args).contains("-f"))
		printPasses=false;
	}
	for (int i = 0; i < test.length; i++ ) {

	    if(!fpTest( (String)test[i][0],(Object[])test[i][1],(String)test[i][2] ))
		errorCount++;
	}
	trace.out("Number failed: "+ errorCount);
	trace.out("Total tests run: " +test.length);

    }

    static Object[][] test = 
    {
	// ------------------------------------------------------------
	{
	    "edu.cmu.pact.miss.userDef.algebra.GetOperand",
	    new Object[] { "add 338" }, "338"
	},
	{
	    "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "-338-171/x", "338" }, "-171/x"
	},
	{
	    "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
	    new Object[] { "-171/x" }, "171/x"
	},

	{
	    "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "-6", "-6" }, "-12"
	},
	{
	    "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "-6.5", "-6.5" }, "-13"
	},
	{
	    "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "-6x", "-6x" }, "-12x"
	},
	{
	    "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "x", "3" }, "x+3"
	},
	{
	    "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "-x", "3" }, "-x+3"
	},
	{
	    "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "5x", "3" }, "5x+3"
	},

	{
	    "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "1/3", "1/3" }, "2/3"
	},
	{
	    "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "1/6", "1/3" }, "1/2"
	},


	{ "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "-6-4x", "-6+4x" }, "-12"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "-6+4x", "-6-4x" }, "-12"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "6+4x", "-6-4x" }, "0"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "6+4x+3", "-6-4x" }, "3"
	},
	{
	    "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "-x", "13" }, "-x+13"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "x", "3" }, "x+3"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "x", "3x" }, "4x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "x", "-x" }, "0"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "6", "3" }, "9"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "x/7", "x/7" }, "2x/7"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "1/7", "1/7" }, "2/7"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "19/2", "25/3" }, "107/6"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "19x/2", "25/3" }, "19x/2+25/3"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "2(x+1)","3(x+1)" }, "5x+5"

	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "2(x+1)","(x+1)" }, "3x+3"

	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "x+1","x+1" }, "2x+2"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "5.3x","2.7x"}, "8x"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "7x+5.3x+3","2.7x"}, "15x+3"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "(x+3)/6","(x+3)/6"}, "x/3+1"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.AddTerm",
	    new Object[] { "3(x+3)/6","2(x+3)/6"}, "5x/6+5/2"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "3x", "4" }, "12x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "x", "0" }, "0"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "0", "x" }, "0"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "1", "x" }, "x"
	},

	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "1/6", "6x" }, "x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "1/2", "1/2x" }, "x/4"
	},



	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "6", "-(x+3)" }, "-6x-18"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "-(x+3)", "6" }, "-6x-18"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "(x+3)", "6" }, "x*6+18"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] {"6" , "-(x+2x+3)" }, "-18x-18"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "-(x+2x+3)", "6" }, "-18x-18"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "(x+3)", "1/6" }, "x/6+1/2"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "-(x+3)", "1/6" }, "-x/6-1/2"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "1/6", "-(x+3)" }, "-x/6-1/2"
	},



	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "(x+3)", "5" }, "x*5+15"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "x+3", "6" }, "x*6+18"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "1/6x", "y" }, "xy/6" //NOTE this should be consistent(parsing issue)
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "1/2x", "1/2y" }, "xy/4"
	},//NOTE this should be consistent

	{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
	    new Object[] { "(2x+3)/(3x+2)", "4" }, "8x/(3x+2)+12/(3x+2)"
	},

	{ "edu.cmu.pact.miss.userDef.algebra.DivTerm",
	    new Object[] { "-4", "-1/5" }, "20"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.DivTerm",
	    new Object[] { "3", "9x" }, "1/(3x)"
	},

	{

	    "edu.cmu.pact.miss.userDef.algebra.DivTerm",
	    new Object[] { "3x","1.5"}, "2x"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.DivTerm",
	    new Object[] { "-(x+3.5)","2.2"}, "-0.45454545x-1.59090909" //"(-x/2.2-3.5/2.2)"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.DivTerm",
	    new Object[] { "x+1.5","x+1.5"}, "1"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.DivTerm",
	    new Object[] { "3x","1.5y"}, "2*(x/y)"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.DivTerm",
	    new Object[] { "x","y"}, "x/y"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.DivTerm",
	    new Object[] { "2x","4x"}, "1/2"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.DivTerm",
	    new Object[] { "3x", "3" }, "x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.DivTerm",
	    new Object[] { "3", "9x" }, "1/(3x)"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.DivTerm",
	    new Object[] { "(2x+4)", "(2x+4)" }, "1"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.DivTerm",
	    new Object[] { "(2x+4)", "(3x+4)" }, null
	},

	{ "edu.cmu.pact.miss.userDef.algebra.Coefficient",
	    new Object[] { "-x/5" }, "-1/5"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.Coefficient",
	    new Object[] { "1/4" }, null
	},
	{ "edu.cmu.pact.miss.userDef.algebra.Coefficient",
	    new Object[] { "4" }, null
	},
	{ "edu.cmu.pact.miss.userDef.algebra.Coefficient",
	    new Object[] { "4x/5" }, "4/5"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.Coefficient",
	    new Object[] { "5.532x"}, "5.532"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.Coefficient",
	    new Object[] { "x"}, "1"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.Coefficient",
	    new Object[] { "5x"}, "5"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.Coefficient",
	    new Object[] { "5xy"}, "5"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.Coefficient",
	    new Object[] { "5(x+3)"}, null
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.Coefficient",
	    new Object[] { "x"}, "1"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.Coefficient",
	    new Object[] { "-x"}, "-1"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.Coefficient",
	    new Object[] { "5"}, null
	},

	{

	    "edu.cmu.pact.miss.userDef.algebra.Coefficient",
	    new Object[] { "1/3x"}, "1/3"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.FirstVarTerm",
	    new Object[] { "-3x+11" }, "-3x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.FirstVarTerm",
	    new Object[] { "x+8" }, "x"
	},

	{

	    "edu.cmu.pact.miss.userDef.algebra.FirstVarTerm",
	    new Object[] { "3.6+5.5x-2.2x"}, "5.5x"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.FirstVarTerm",
	    new Object[] { "3.6+5.5xy-2.2x"}, "5.5xy"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.FirstVarTerm",
	    new Object[] { "5xy+3"}, "5xy"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.FirstVarTerm",
	    new Object[] { "5xy+x"}, "5xy"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.FirstVarTerm",
	    new Object[] { "5x"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.FirstVarTerm",
	    new Object[] { "x"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.FirstVarTerm",
	    new Object[] { "5"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.FirstVarTerm",
	    new Object[] { "5xy"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastVarTerm",
	    new Object[] { "5x"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastVarTerm",
	    new Object[] { "x"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastVarTerm",
	    new Object[] { "5"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastVarTerm",
	    new Object[] { "5xy"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastVarTerm",
	    new Object[] { "3.6+5.5x-2.2x+z"}, "z"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastVarTerm",
	    new Object[] { "3.6+5.5xy-2.2x"}, "-2.2x"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastVarTerm",
	    new Object[] { "5xy+3"}, "5xy"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastVarTerm",
	    new Object[] { "5xy+x"}, "x"

	},

	{ "edu.cmu.pact.miss.userDef.algebra.LastVarTerm",
	    new Object[] { "-3x+11" }, "-3x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.LastVarTerm",
	    new Object[] { "x+8" }, "x"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastVarTerm",
	    new Object[] { "3x+4y+5z+4+6w"}, "6w"

	},
	{

	    "edu.cmu.pact.miss.userDef.FirstTerm",
	    new Object[] { "5.5x-2.2x"}, "5.5x"

	},
	{ "edu.cmu.pact.miss.userDef.algebra.FirstTerm",
	    new Object[] { "-3x+11" }, "-3x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.FirstTerm",
	    new Object[] { "x+8" }, "x"
	},


	{

	    "edu.cmu.pact.miss.userDef.algebra.FirstTerm",
	    new Object[] { "3.6+5.5xy-2.2x"}, "3.6"

	},

	{

	    "edu.cmu.pact.miss.userDef.algebra.FirstTerm",
	    new Object[] { "6+5xy+x"}, "6"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.FirstTerm",
	    new Object[] { "5x"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.FirstTerm",
	    new Object[] { "x"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.FirstTerm",
	    new Object[] { "5"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.FirstTerm",
	    new Object[] { "5xy"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.FirstTerm",
	    new Object[] { "3x+4y+5z+6w+4"}, "3x"

	},

	{

	    "edu.cmu.pact.miss.userDef.LastTerm",
	    new Object[] { "5.5x-2.2x"}, "-2.2x"

	},
	{ "edu.cmu.pact.miss.userDef.algebra.LastTerm",
	    new Object[] { "-3x+11" }, "11"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.LastTerm",
	    new Object[] { "x+8" }, "8"
	},


	{

	    "edu.cmu.pact.miss.userDef.algebra.LastTerm",
	    new Object[] { "3.6+5.5xy-2.2x"}, "-2.2x"

	},

	{

	    "edu.cmu.pact.miss.userDef.algebra.LastTerm",
	    new Object[] { "6+5xy+x"}, "x"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastTerm",
	    new Object[] { "5x"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastTerm",
	    new Object[] { "x"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastTerm",
	    new Object[] { "5"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastTerm",
	    new Object[] { "5xy"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastTerm",
	    new Object[] { "3x+4y+5z+6w+4"}, "4"

	},
	{ "edu.cmu.pact.miss.userDef.LastConstTerm",
	    new Object[] { "7+x/3" }, "7"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastConstTerm",
	    new Object[] { "3x+4y+5z+6w+4"}, "4"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastConstTerm",
	    new Object[] { "3x+4y+4+5z+6w"}, "4"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastConstTerm",
	    new Object[] { "6(x+3)"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.LastConstTerm",
	    new Object[] { "10x"}, null

	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
	    new Object[] { "-5x-4", "4x" }, "-5x-4+4x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
	    new Object[] { "3+x/5", "-3" }, "3+x/5-3"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
	    new Object[] { "0", "5" }, "0+5"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
	    new Object[] { "4", "-x/5" }, "4-x/5"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
	    new Object[] { "3x", "-2x" }, "3x-2x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
	    new Object[] { "3x-3", "-2x" }, "3x-3-2x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
	    new Object[] { "2", "3x" }, "2+3x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
	    new Object[] { "12", "-x" }, "12-x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
	    new Object[] { "4x+2", "3x" }, "4x+2+3x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
	    new Object[] { "4x+2x+3y+4z+6w", "-6(3x+2)" }, "4x+2x+3y+4z+6w-6(3x+2)"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTermBy",
	    new Object[] { "(x+5)/6", "6" }, "((x+5)/6)*6"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTermBy",
	    new Object[] { "x/3", "5" }, "(x/3)*5"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTermBy",
	    new Object[] { "-x", "-1" }, "-x*-1"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTermBy",
	    new Object[] { "0", "x" }, "0*x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTermBy",
	    new Object[] { "3", "x+3" }, "3*(x+3)"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTermBy",
	    new Object[] { "x+3", "x+3" }, "(x+3)*(x+3)"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTermBy",
	    new Object[] { "wxy", "z" }, "wxy*z"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.MulTermBy",
	    new Object[] { "5(x+3)", "2" }, "5(x+3)*2"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.DivTermBy",
	    new Object[] {"2.3x","2.7"}, "2.3x/2.7"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.DivTermBy",
	    new Object[] { "-x", "-1" }, "(-x)/(-1)" //negatives get parens
	},
	{ "edu.cmu.pact.miss.userDef.algebra.DivTermBy",
	    new Object[] { "1", "x" }, "1/x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.DivTermBy", 
	    new Object[] { "8", "8x" }, "8/(8x)"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.DivTermBy", 
	    new Object[] { "3x", "3" }, "3x/3"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
	    new Object[] { "-4x" }, "4x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
	    new Object[] { "x/5" }, "-x/5"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
	    new Object[] { "2.325" }, "-2.325"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
	    new Object[] { "x" }, "-x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
	    new Object[] { "x+3" }, "-(x+3)"

	},
	{ "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
	    new Object[] { "-(x+3)" }, "x+3"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.VarName",
	    new Object[] { "3x+y"}, null
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.VarName",
	    new Object[] { "3x"}, "x"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.VarName",
	    new Object[] { "3xy"}, null
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.Denominator",
	    new Object[] { "5x/2.3"}, "2.3"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.Numerator",
	    new Object[] { "5.3x/2.3"}, "5.3x"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.Denominator",
	    new Object[] { "5x/2.3"}, "2.3"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.Numerator",
	    new Object[] { "(x+4)/(y-3)"}, "x+4"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.Denominator",
	    new Object[] { "(x+4)/(y-3)"}, "y-3"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.Numerator",
	    new Object[] { "1/3"}, "1"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.Denominator",
	    new Object[] { "1/3"}, "3"
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.Numerator",
	    new Object[] { "6"}, null
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.Denominator",
	    new Object[] { "6"}, null
	},

	{ "edu.cmu.pact.miss.userDef.algebra.RipCoefficient",
	    new Object[] { "-x/5" }, "x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.RipCoefficient",
	    new Object[] { "4x+5-5" }, null
	},
	{ "edu.cmu.pact.miss.userDef.algebra.RipCoefficient",
	    new Object[] { "x/7" }, "x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.RipCoefficient",
	    new Object[] { "(x+5)/6" }, null
	},
	{ "edu.cmu.pact.miss.userDef.algebra.RipCoefficient",
	    new Object[] { "5xy" }, "xy"
	},

	{ "edu.cmu.pact.miss.userDef.algebra.GCD",
	    new Object[] { "0", "13" }, null
	},
	{ "edu.cmu.pact.miss.userDef.algebra.GCD",
	    new Object[] { "0", "0" }, null
	},
	{ "edu.cmu.pact.miss.userDef.algebra.GCD",
	    new Object[] { "-5", "13" }, null
	},
	{ "edu.cmu.pact.miss.userDef.algebra.GCD",
	    new Object[] { "5x", "13" }, null
	},
	{ "edu.cmu.pact.miss.userDef.algebra.GCD",
	    new Object[] { "15", "90" }, "15"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.LCM",
	    new Object[] { "5", "13" }, "65"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.LCM",
	    new Object[] { "0", "13" }, null
	},
	{ "edu.cmu.pact.miss.userDef.algebra.LCM",
	    new Object[] { "0", "0" }, null
	},
	{ "edu.cmu.pact.miss.userDef.algebra.LCM",
	    new Object[] { "-5", "13" }, "-65"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.LCM",
	    new Object[] { "5x", "13" }, null
	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.DivTen",
	    new Object[] { "1.5"}, "0.15"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.ModTen",
	    new Object[] { "12"}, "2"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.ModTen",
	    new Object[] { "3x"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.InverseTerm",
	    new Object[] { "x"}, "1/x"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.InverseTerm",
	    new Object[] { "3"}, "1/3"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.InverseTerm",
	    new Object[] { "5x"}, "1/(5x)"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.InverseTerm",
	    new Object[] { "x+3"}, null

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "5.2+3.8"}, "9"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "5.5-2.2"}, "3.3"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "5*1.5"}, "7.5"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "3/1.5"}, "2"

	},
	{

	    "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "5.5x-2.2x"}, "3.3x"

	},

	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "-(6+4x)" }, "-4x-6"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "-(-6-4x)-5x-4" }, "-x+2"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "3+x/5-3" }, "x/5"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "4-x/5" }, "-x/5+4"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "3x-2x" }, "x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "3x-3-2x" }, "x-3"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "2+3x" }, null
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "4x+2+3x" }, "7x+2"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "-(x+3)" }, "-x-3"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "x/4+5-5" }, "x/4"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "1/(4x+5)" }, null
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "2+(4x+5)" }, "4x+7"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "4+3x+2x" }, "5x+4"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "3x+4x" }, "7x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "x/7+6" }, null
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "4" }, null
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "x+4" }, null
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "3x/3" }, "x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "(-x)*(-1)" }, "x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "(-x)/(-1)" }, "x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "9x-4x+6" }, "5x+6"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "9x-4+4" }, "9x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "((x+4)/3)*3" }, "x+4"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "(-x)/(-1)" }, "x"
	},
	{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
	    new Object[] { "-5/(-1)" }, "5"
	},
  	{
	    "edu.cmu.pact.miss.userDef.algebra.FirstVarTerm",
	    new Object[] { "614/x" }, "614/x"
	},
    };
}
