package edu.cmu.pact.miss.userDef.algebra.test;

import java.util.Arrays;

import edu.cmu.pact.miss.FeaturePredicate;

/**
 * There is a j-unit 	test version of this inside test/edu/cmu/miss/userDef/algebra
 *
 * @author Administrator
 *
 */

public class FPTest {

    static boolean fpTest( String testName, Object[] args, String expectedVal ) {
        return FeaturePredicate.testUserDefSymbols( testName, args, expectedVal, printPasses );
    }
    
    // flag to indicate whenever passed test results should get printed on now
    static boolean printPasses=true;

    /**
     * Describe <code>main</code> method here.
     *
     * @param args a <code>String[]</code> value
     */
    public static void main(final String[] args) {
        
        int errorCount=0;

        if(args.length!=0) {
            //handle cmdline flags
            if(Arrays.asList(args).contains("-f"))
                printPasses=false;
        }
        for (int i = 0; i < test.length; i++ ) {

            if(!fpTest( (String)test[i][0],(Object[])test[i][1],(String)test[i][2] ))
                errorCount++;
        }
        System.out.println("Number failed: "+ errorCount);
        System.out.println("Total tests run: " +test.length);

    }

    static Object[][] test= {
        
        { "edu.cmu.pact.miss.userDef.algebra.IsFractionTerm",
            new Object[] { "8/8x" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.IsFractionTerm",
            new Object[] { "3x/9+3x" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.Denominator",
            new Object[] { "3x/9+3x" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.Denominator",
            new Object[] { "(7x+5x)/3" }, "3"
        },
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
        { "edu.cmu.pact.miss.userDef.algebra.Coefficient",
            new Object[] { "8/8x" }, "8/8"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Coefficient",
            new Object[] { "-x/5" }, "-1/5"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Coefficient",
            new Object[] { "1/4" }, "1/4"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Coefficient",
            new Object[] { "4" }, "4"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Coefficient",
            new Object[] { "4x/5" }, "4/5"
        },
        { "edu.cmu.pact.miss.userDef.algebra.HasCoefficient",
            new Object[] { "4x+5" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.HasCoefficient",
            new Object[] { "x" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.HasCoefficient",
            new Object[] { "8*x" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.HasCoefficient",
            new Object[] { "-8/x" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.HasCoefficient",
            new Object[] { "8x/5" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.HasCoefficient",
            new Object[] { "4x+5" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.HasCoefficient",
            new Object[] { "x" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.HasCoefficient",
            new Object[] { "-8/x" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.HasCoefficient",
            new Object[] { "8x/5" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.HasConstTerm",
            new Object[] { "7x/7" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.HasConstTerm",
            new Object[] { "3+x/5" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.HasConstTerm",
            new Object[] { "5" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.RipCoefficient",
            new Object[] { "-x/5" }, "x"
        },
//        { "edu.cmu.pact.miss.userDef.algebra.CancelCoefficient",
//            new Object[] { "8/(8x)" }, null
//        },
//        { "edu.cmu.pact.miss.userDef.algebra.CancelCoefficient",
//            new Object[] { "3x" }, "3x/3"
//        },
        { "edu.cmu.pact.miss.userDef.algebra.VarTerm",
            new Object[] { "2+(-x)/(-1)" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.VarTerm",
            new Object[] { "x/4" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.VarTerm",
            new Object[] { "(-x)/(-1)" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.VarTerm",
            new Object[] { "7x/7" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.VarTerm",
            new Object[] { "x/7" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.VarTerm",
            new Object[] { "(x/7)*7" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.HasVarTerm",
            new Object[] { "2+(-x)/(-1)" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.HasVarTerm",
            new Object[] { "-3y" }, "T"
        },
//        to fix: all 5 cases below must be debugged
//
//        { "edu.cmu.pact.miss.userDef.algebra.HasVarTerm",
//            new Object[] { "x/4" }, "T"
//        },
//        { "edu.cmu.pact.miss.userDef.algebra.HasVarTerm",
//            new Object[] { "(-x)/(-1)" }, "T"
//        },
//        { "edu.cmu.pact.miss.userDef.algebra.HasVarTerm",
//            new Object[] { "7x/7" }, "T"
//        },
//        { "edu.cmu.pact.miss.userDef.algebra.HasVarTerm",
//            new Object[] { "x/7" }, "T"
//        },
//        { "edu.cmu.pact.miss.userDef.algebra.HasVarTerm",
//            new Object[] { "(x/7)*7" }, "T"
//        },
        { "edu.cmu.pact.miss.userDef.algebra.Monomial",
            new Object[] { "-8/x" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Monomial",
            new Object[] { "(-x)/(-1)" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Monomial",
            new Object[] { "15/5" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Monomial",
            new Object[] { "5/(-1)" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Monomial",
            new Object[] { "5x+5-5" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.Monomial",
            new Object[] { "15-5" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.IsPolynomial",
            new Object[] { "-3.65y" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.IsPolynomial",
            new Object[] { "3x/9+3x" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.IsPolynomial",
            new Object[] { "-3.65y-9.75+9.75" }, "T"
        },
        // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
        { "edu.cmu.pact.miss.userDef.algebra.Homogeneous",
            new Object[] { "5x" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Homogeneous",
            new Object[] { "5" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Homogeneous",
            new Object[] { "1+2+3" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Homogeneous",
            new Object[] { "-x+2x+3x" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Homogeneous",
            new Object[] { "x+y+z" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.Homogeneous",
            new Object[] { "19.52-30.65x" }, null
        },
        //to fix: must be debugged
        //{ "edu.cmu.pact.miss.userDef.algebra.Homogeneous",
        //  new Object[] { "(-x)/(-1)" }, "T"
        //},
        //to fix: must be debugged
        //{ "edu.cmu.pact.miss.userDef.algebra.Homogeneous",
        //    new Object[] { "x+3" }, null
        //},
        // - - - - - - - - - - - - - - - - - - - - - - - - 
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "3/9x", "3x" }, "3/9x+3x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "-6-4x", "-6+4x" }, "-12"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "-6+4x", "-6-4x" }, "-12"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "-6-4x", "-(6-4x)" }, "-12"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "4x+2", "3x" }, "7x+2"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "13", "-x" }, "-x+13"
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
            new Object[] { "(-7x-5)*-6", "0" }, "42x+30"
        },    
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "(-7x-5)*-6", "1" }, "42x+31"
        },    
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "-(x+1)", "1" }, "-x"
        },    
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "-1(x+1)", "1" }, "-x"
        },        
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "20.76-20.75y","-61.86" }, "-20.75y-41.1"
        },        
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "20.76","-61.86" }, "-41.1"
        },        
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "-1", "0" }, "-1"
        },        
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "+6","2" }, "8"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
            new Object[] { "x/4+5", "-5" }, "x/4+5-5"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
            new Object[] { "4+3x", "2x" }, "4+3x+2x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
            new Object[] { "4+3x", "-2x" }, "4+3x-2x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
            new Object[] { "0", "5" }, "0+5"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
            new Object[] { "3+x/5", "-3" }, "3+x/5-3"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
            new Object[] { "4x+2", "3x" }, "4x+2+3x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
            new Object[] { "9", "-11y" }, "9-11y"
        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTermBy",
            new Object[] { "5x/7", "7" }, "(5x/7)*7"
        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTermBy",
            new Object[] { "x/3", "5" }, "(x/3)*5"
        },
        //to fix: must be debugged
        //{ "edu.cmu.pact.miss.userDef.algebra.MulTermBy",
        //  new Object[] { "-x", "-1" }, "-x*(-1)"
        //},
        { "edu.cmu.pact.miss.userDef.algebra.MulTermBy",
            new Object[] { "0", "x" }, "0*x"
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

        //for 2nd order polynomials, the result must be null
        { "edu.cmu.pact.miss.userDef.algebra.MulTerm",
            new Object[] { "2x+4+2x+4", "2x+4" }, null //"2x*(2x+4)+4*(2x+4)+2x*(2x+4)+4*(2x+4)"
        },
        //to fix: must be debugged
        //{ "edu.cmu.pact.miss.userDef.algebra.MulTerm",
        //  new Object[] { "-(x+3)", "1/6" }, "-(x+3)/6"
        //},
        { "edu.cmu.pact.miss.userDef.algebra.MulTermBy",
            new Object[] { "(x+5)/6", "6" }, "((x+5)/6)*6"
        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTen",
            new Object[] { "2" }, "20"
        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTerm",
            new Object[] { "-7x", "-1*-7-x" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTermBy",
            new Object[] { "-1","-7x" }, "-1*-7x",
        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTermBy",
            new Object[] { "-7x", "-1" }, "-7x*-1",
        },
//        { "edu.cmu.pact.miss.userDef.algebra.MulTerm",
//            new Object[] { "-1", "-7x-/(-7x)" }, "1"
//        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTerm",
            new Object[] { "y/6", "6" }, "y"
        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTerm",
            new Object[] { "1", "6" }, "6"
        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTerm",
            new Object[] { "-3", "3" }, "-9"
        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTerm",
            new Object[] { "y/3", "3" }, "y"
        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTerm",
            new Object[] { "3", "y/-4" }, "-3/4y"
        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTerm",
            new Object[] { "3/x", "x" }, "3"
        },
        { "edu.cmu.pact.miss.userDef.algebra.DivTerm",
            new Object[] { "2x", "2" }, "x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.DivTerm",
            new Object[] { "-2/y", "2" }, "-2/(2y)"
        },
        { "edu.cmu.pact.miss.userDef.algebra.DivTerm",
            new Object[] { "0", "-y/6" }, "0"
        },
        { "edu.cmu.pact.miss.userDef.algebra.DivTerm",
            new Object[] { "-1", "-7" }, "1/7"
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
            new Object[] { "-1*-7x","-1" }, "-7x" //getting "-1*-7-x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.DivTerm",
            new Object[] { "-7x*-1","-7x" }, "-1"
        },
        { "edu.cmu.pact.miss.userDef.algebra.DivTerm",
                new Object[] { "0+0", "5" }, "0"
        },
        { "edu.cmu.pact.miss.userDef.algebra.DivTerm",
            new Object[] { "-4", "-1/5" }, "20"
        },
        { "edu.cmu.pact.miss.userDef.algebra.DivTerm",
            new Object[] { "3x", "2x+1" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.DivTermBy",
            new Object[] { "-1", "-7" }, "(-1)/(-7)"
        },
        { "edu.cmu.pact.miss.userDef.algebra.DivTermBy",
            new Object[] { "-x", "-1" }, "(-x)/(-1)"
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
        { "edu.cmu.pact.miss.userDef.algebra.ArithAdd",
            new Object[] { "20", "2" }, "22"
        },
        { "edu.cmu.pact.miss.userDef.algebra.ArithDiv",
            new Object[] { "22", "3" }, "7"
        },
        // - - - - - - - - - - - - - - - - - - - - - - - - 
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "3x/9+3x" }, "3x/9+3x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "-7x*-1" }, "7x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "0.84y/0.84" }, "y"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "9+-11y" }, "-11y+9"
        }, 
        
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "5/5x" }, "x"
        }, 
        //{ "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
        //    new Object[] { "6y--3" }, "6y+3"
        //},
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "x--y" }, "x+y"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "--3+5" }, "8"
        },

        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "-2*(--6)" }, "-12"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "+6" }, "6"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "4x+2+3x" }, "7x+2"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "3+x/5" }, "x/5+3"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "(2x+3x)/7" }, "5/7x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "5x/7" }, "5/7x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "(5x/7)*7" }, "5x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "21/5" }, "21/5"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "(7x+5x)/3" }, "4x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "-(-6-4x)" }, "4x+6"
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
            new Object[] { "-(x+3)" }, "-x-3"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "x/4+5-5" }, "x/4"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "1/(4x+5)" }, "1/(4x+5)"
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
            new Object[] { "x/7+6" }, "x/7+6"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "4" }, "4"
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
        // ------------------------------------------------------------
        { "edu.cmu.pact.miss.userDef.algebra.LastConstTerm",
            new Object[] { "8/4" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.LastConstTerm",
            new Object[] { "7+x/3" }, "7"
        },
        // ------------------------------------------------------------
//        { "edu.cmu.pact.miss.userDef.algebra.CanBeSimplified",
//          new Object[] { "(7x+5x)/3" }, "T"
//        },
//      { "edu.cmu.pact.miss.userDef.algebra.CanBeSimplified",
//        new Object[] { "3+x/5" }, "null"
//      },

        // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
        { "edu.cmu.pact.miss.userDef.algebra.LastTerm",
            new Object[] { "x/5+4-3" }, "-3"
        },
        { "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
            new Object[] { "-3" }, "3"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "x/5+4-3", "3" }, "x/5+4"
        },
        // ------------------------------------------------------------
        { "edu.cmu.pact.miss.userDef.algebra.LastTerm",
            new Object[] { "-6-4x" }, "-4x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
            new Object[] { "-4x" }, "4x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "-5x-4", "4x" }, "-x-4"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
            new Object[] { "-5x-4", "4x" }, "-5x-4+4x"
        },
        // ------------------------------------------------------------
//        { "edu.cmu.pact.miss.userDef.algebra.RemoveFirstVarTerm",
//          new Object[] { "x/5" }, "0"
//        },
//        { "edu.cmu.pact.miss.userDef.algebra.RemoveLastConstTerm",
//          new Object[] { "4" }, "0"
//        },
        { "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
          new Object[] { "x/5" }, "-x/5"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
          new Object[] { "4", "-x/5" }, "4-x/5"
        },
        // ------------------------------------------------------------
//        { "edu.cmu.pact.miss.userDef.algebra.RemoveLastConstTerm",
//          new Object[] { "7+x/3" }, "x/3"
//        },
        { "edu.cmu.pact.miss.userDef.algebra.FirstVarTerm",
          new Object[] { "2x+4" }, "2x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
          new Object[] { "2x" }, "-2x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
          new Object[] { "3x", "-2x" }, "3x-2x"
        },
        // ------------------------------------------------------------
        { "edu.cmu.pact.miss.userDef.algebra.FirstVarTerm",
          new Object[] { "2x+5" }, "2x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
          new Object[] { "2x" }, "-2x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
          new Object[] { "3x-3", "-2x" }, "3x-3-2x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
          new Object[] { "3x-3-2x" }, "x-3"
        },
        // ------------------------------------------------------------
        { "edu.cmu.pact.miss.userDef.algebra.FirstVarTerm",
          new Object[] { "-3x+11" }, "-3x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
          new Object[] { "-3x" }, "3x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
          new Object[] { "2", "3x" }, "2+3x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
          new Object[] { "2+3x" }, "3x+2"
        },
        // ------------------------------------------------------------
        { "edu.cmu.pact.miss.userDef.algebra.FirstVarTerm",
          new Object[] { "x+8" }, "x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
          new Object[] { "x" }, "-x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
          new Object[] { "12", "-x" }, "12-x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
          new Object[] { "12-x" }, "-x+12"
        },
        // ------------------------------------------------------------
        { "edu.cmu.pact.miss.userDef.algebra.FirstVarTerm",
          new Object[] { "-3x+11" }, "-3x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
          new Object[] { "-3x" }, "3x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
          new Object[] { "4x+2", "3x" }, "7x+2"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTermBy",
          new Object[] { "4x+2", "3x" }, "4x+2+3x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
          new Object[] { "4x+2+3x" }, "7x+2"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
          new Object[] { "13", "-x" }, "13-x"
        },
        // ------------------------------------------------
//        { "edu.cmu.pact.miss.userDef.algebra.CancelLastConstTerm",
//          new Object[] { "3+4x" }, "3+4x-3"
//        },
//        { "edu.cmu.pact.miss.userDef.algebra.CancelDenominator",
//          new Object[] { "(x+4)/3" }, "((x+4)/3)*3"
//        },
        // TODO
        
        //to fix: must be debugged
        //{ "edu.cmu.pact.miss.userDef.algebra.InverseTerm",
        //  new Object[] { "-x/(-1)" }, "-1/(-x)"
        //},
        
        //to fix: must be debugged
        //{ "edu.cmu.pact.miss.userDef.algebra.InverseTerm",
        //  new Object[] { "8x/8" }, "8/(8x)"
        //},
        
        { "edu.cmu.pact.miss.userDef.algebra.VarTerm",
          new Object[] { "8x/8" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.VarTerm",
          new Object[] { "1/8" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.Monomial",
          new Object[] { "-8/x" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Monomial",
          new Object[] { "(-x)/(-1)" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Monomial",
          new Object[] { "15/5" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Monomial",
          new Object[] { "5/(-1)" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Monomial",
          new Object[] { "5x+5-5" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.Monomial",
          new Object[] { "15-5" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.Denominator",
          new Object[] { "3/(4x)" }, "4x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Denominator",
          new Object[] { "x/3" }, "3"
        },
        { "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
          new Object[] { "1/7*7" }, "-1/7*7"
        },
        { "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
          new Object[] { "1/7" }, "-1/7"
        },
        { "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
          new Object[] { "(2x+4)/(3x)" }, "-(2x+4)/(3x)"
        },
//        { "edu.cmu.pact.miss.userDef.algebra.RemoveFirstVarTerm",
//          new Object[] { "3-4x-3x" }, "3-3x"
//        },
//        { "edu.cmu.pact.miss.userDef.algebra.RemoveFirstVarTerm",
//          new Object[] { "3+2-4x+5-8" }, "3+2+5-8"
//        },
        { "edu.cmu.pact.miss.userDef.algebra.RipCoefficient",
          new Object[] { "4x+5-5" }, null
        },
        
        //to fix: must be debugged
        //{ "edu.cmu.pact.miss.userDef.algebra.RipCoefficient",
        //  new Object[] { "x/7" }, "x"
        //},
        { "edu.cmu.pact.miss.userDef.algebra.RipCoefficient",
          new Object[] { "(x+5)/6" }, null
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
        //to fix: must be debugged
        //{ "edu.cmu.pact.miss.userDef.algebra.LCM",
        //  new Object[] { "-5", "13" }, -65 //previously null
        //},
        { "edu.cmu.pact.miss.userDef.algebra.LCM",
          new Object[] { "5x", "13" }, null
        },
        //to fix: must be debugged
        //{ "edu.cmu.pact.miss.userDef.algebra.GCD",
        //  new Object[] { "5", "13" }, "1"
        //},
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
        { "edu.cmu.pact.miss.userDef.algebra.DivTermDecimal",
            new Object[] { "1", "7" }, "0.14285714285714285"
        },
        { "edu.cmu.pact.miss.userDef.algebra.IsEquivalent",
            new Object[] { "1/7", "0.14285714285714285" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.IsEquivalent",
            new Object[] { "1/7", "0.14385714285714285" }, null
        },        
        { "edu.cmu.pact.miss.userDef.algebra.IsEquivalent",
            new Object[] { "1/7", "0.14" }, "T"
        },        
        // - - - - - - - - - - - - - - - - - - - - -
        { "edu.cmu.pact.miss.userDef.algebra.MulTen",
            new Object[] { "2" }, "20"
        },
        { "edu.cmu.pact.miss.userDef.algebra.ArithAdd",
            new Object[] { "20", "2" }, "22"
        },
        { "edu.cmu.pact.miss.userDef.algebra.ArithDiv",
            new Object[] { "22", "3" }, "7"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "(-7x-5)*-6", "0" }, "42x+30"
        },    
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "(-7x-5)*-6", "1" }, "42x+31"
        },    
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "-(x+1)", "1" }, "-x"
        },    
        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "-1(x+1)", "1" }, "-x"
        },        

        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "0.84y/0.84" }, "y"
        },

        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "20.76-20.75y","-61.86" }, "-41.1-20.75y"
        },        

        { "edu.cmu.pact.miss.userDef.algebra.AddTerm",
            new Object[] { "20.76","-61.86" }, "-41.1"
        },        
        { "edu.cmu.pact.miss.userDef.algebra.IsEquivalent",
            new Object[] { "-41.099999999999994", "-41.1" }, "T"
        },

        { "edu.cmu.pact.miss.userDef.algebra.IsEquivalent",
            new Object[] { "-20.75y-41.099999999999994", "-20.75y-41.1" }, "T"
        },

        { "edu.cmu.pact.miss.userDef.algebra.IsEquivalent",
            new Object[] { "2", "2+2y-2y" }, "T"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.IsEquivalent",
            new Object[] { "62-(-74)", "62+74" }, "T"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.IsEquivalent",
            new Object[] { "-74-66y-(-74)", "-74-66y+74" }, "T"
        },

        { "edu.cmu.pact.miss.userDef.algebra.RemoveParens",
            new Object[] { "1+3*(x-y)" }, "1+3*x-y"
        },

        { "edu.cmu.pact.miss.userDef.algebra.AddTermBy_keepMinus",
            new Object[] { "9", "-11y" }, "9+-11y"
        },

        { "edu.cmu.pact.miss.userDef.algebra.IsEquivalent",
            new Object[] { "9+-11y", "-11y+9" }, "T"
        },


        
        //{ "edu.cmu.pact.miss.userDef.algebra.ListAddTerm",
        //    new Object[] { "[]" }, ""
        //},         

        { "edu.cmu.pact.miss.userDef.algebra.GetNumSymbolsList",
            new Object[] { "13x+12-1" }, "[13 12 -1]"
        },

        { "edu.cmu.pact.miss.userDef.algebra.GetNumSymbolsList",
            new Object[] { "-13-12+1" }, "[-13 -12 1]"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.GetNumSymbolsList",
            new Object[] { "-13x*12/1" }, "[-13 12 1]"
        },

        { "edu.cmu.pact.miss.userDef.algebra.GetNumSymbolsList",
            new Object[] { "-13x*12/1" }, "[-13 12 1]"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.GetVarTermSymbols",
            new Object[] { "-13x+3+y" }, "-13x+y"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.GetVarTermSymbols",
            new Object[] { "-13+3+y" }, "y"
        },
        
        //to fix: must be debugged
        //{ "edu.cmu.pact.miss.userDef.algebra.GetVarTermSymbols",
        //    new Object[] { "-1" }, null
        //},

        { "edu.cmu.pact.miss.userDef.algebra.GetVarTermSymbols",
            new Object[] { "-x" }, "-x"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.GetVarTermSymbols",
            new Object[] { "1-3x-3x" }, "-3x-3x"
        },
        
        // to fix, but maybe not very important
        //{ "edu.cmu.pact.miss.userDef.algebra.GetVarTermSymbols",
        //    new Object[] { "1-1x-3x" }, "-1x-3x"
        //},
        
        { "edu.cmu.pact.miss.userDef.algebra.GetConstTermSymbols",
            new Object[] { "-13x+3+y" }, "3"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.GetConstTermSymbols",
            new Object[] { "-13+3+y" }, "-13+3"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.GetConstTermSymbols",
            new Object[] { "-1" }, "-1"
        },

        //to fix: must be debugged
        //{ "edu.cmu.pact.miss.userDef.algebra.GetConstTermSymbols",
        //    new Object[] { "-x" }, ""
        //},
        
        { "edu.cmu.pact.miss.userDef.algebra.GetConstTermSymbols",
            new Object[] { "1-3x-3x" }, "1"
        },

        { "edu.cmu.pact.miss.userDef.algebra.ListFirstNegativeFunnyAdd",
            new Object[] { "[-2 10]" }, "-12"
        },
                
        { "edu.cmu.pact.miss.userDef.algebra.ButLastTerm",
            new Object[] { "1+2" }, "1"
        },

        { "edu.cmu.pact.miss.userDef.algebra.ButLastTerm",
            new Object[] { "x-10+2y" }, "x-10"
        },

        { "edu.cmu.pact.miss.userDef.algebra.ButLastTerm",
            new Object[] { "-5-2x+7" }, "-5-2x"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.DropSimpleVarSymbol",
            new Object[] { "y+2+1x" }, "2+1x"
        },

        { "edu.cmu.pact.miss.userDef.algebra.DropSimpleVarSymbol",
            new Object[] { "x-3" }, "-3"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.DropSimpleVarSymbol",
            new Object[] { "4y+3y-x" }, "4y+3y"
        },

        { "edu.cmu.pact.miss.userDef.algebra.DropSimpleVarSymbol",
            new Object[] { "4.3y+3+x" }, "4.3y+3"
        },

        { "edu.cmu.pact.miss.userDef.algebra.ButLastTerm",
            new Object[] { "-7y+3+6y" }, "-7y+3"
        },
        { "edu.cmu.pact.miss.userDef.algebra.GetNumSymbolsList",
            new Object[] { "-7y+3" }, "[-7 3]"
        },

        //Gustavo: bad model-tracing?
        /*{ "edu.cmu.pact.miss.userDef.algebra.ListFirstNegativeFunnyAdd",
            new Object[] { "[-7 3]" }, "-10"
        },
        { "edu.cmu.pact.miss.userDef.algebra.ButLastTerm",
            new Object[] { "-7y+3+6y" }, "-7y+3"
        },
        { "edu.cmu.pact.miss.userDef.algebra.GetNumSymbolsList",
            new Object[] { "-7y+3" }, "[-7 3]"
        },        
        { "edu.cmu.pact.miss.userDef.algebra.ListFirstNegativeFunnyAdd",
            new Object[] { "[-7 3]" }, "-10"
        },
        { "edu.cmu.pact.miss.userDef.algebra.IsEquivalent",
            new Object[] { "-10", "-13y"}, null
        },*/
        
        //Gustavo: BUG step #4
        { "edu.cmu.pact.miss.userDef.algebra.GetVarTermSymbols",
            new Object[] { "-7y+3+6y"}, "-7y+6y"
        },
        { "edu.cmu.pact.miss.userDef.algebra.GetNumSymbolsList",
            new Object[] { "-7y+6y" }, "[-7 6]"
        },
        { "edu.cmu.pact.miss.userDef.algebra.ListFirstNegativeFunnyAdd",
            new Object[] { "[-7 6]" }, "-13"
        },
        { "edu.cmu.pact.miss.userDef.algebra.GetVariableSymbol",
            new Object[] { "-7y+6y" }, "y"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.AppendVarSymbol",
            new Object[] { "-13", "y" }, "-13y"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.GetConstTermSymbols",
            new Object[] { "-1-3x-3x" }, "-1"
        },
        { "edu.cmu.pact.miss.userDef.algebra.GetVarTermSymbols",
            new Object[] { "-1-3x-3x" }, "-3x-3x"
        },
        { "edu.cmu.pact.miss.userDef.algebra.GetNumSymbolsList",
            new Object[] { "-3x-3x" }, "[-3 -3]"
        },
        { "edu.cmu.pact.miss.userDef.algebra.ListFirstNegativeFunnyAdd",
            new Object[] { "[-3 -3]" }, "0"
        },
        { "edu.cmu.pact.miss.userDef.algebra.AppendVarSymbol",
            new Object[] { "-7y+3+6y", "y" }, null
        },        
        
        { "edu.cmu.pact.miss.userDef.algebra.GetVariableSymbol",
            new Object[] { "y" }, "y"
        },        

        ////////////////////////////////
        
        { "edu.cmu.pact.miss.userDef.algebra.SkillDivide",
            new Object[] { "-1.65034593" }, "divide -1.65034593"
        },

        //to fix: must be debugged
        //{ "edu.cmu.pact.miss.userDef.algebra.IsEquivalent",
        //    new Object[] { "divide -1.65034593", "divide -1.6503" }, "T"
        //},
        
        { "edu.cmu.pact.miss.userDef.algebra.Coefficient",
            new Object[] { "2x" }, "2"
        },
        
        { "edu.cmu.pact.miss.userDef.oldpredicates.Coefficient",
            new Object[] { "x" }, "1"
        },
        { "edu.cmu.pact.miss.userDef.algebra.weak.Difference",
            new Object[] { "[5 3 -3]" }, "5"
        },
        { "edu.cmu.pact.miss.userDef.algebra.weak.Sum",
            new Object[] { "[5 2 -3]" }, "4"
        },
        { "edu.cmu.pact.miss.userDef.algebra.weak.FirstNumber",
            new Object[] { "4/x" }, "4"
        },
        { "edu.cmu.pact.miss.userDef.algebra.weak.FirstNumber",
            new Object[] { "-2/y" }, "2"
        },
        { "edu.cmu.pact.miss.userDef.algebra.weak.GetFirstNumberWithSign",
            new Object[] { "-2/y" }, "-2"
        },
        { "edu.cmu.pact.miss.userDef.algebra.weak.NumOpNum",
            new Object[] { "-2/(2y)" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.weak.NumOpNum",
            new Object[] { "-4+(y/-4)" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.DivTerm",
            new Object[] { "-2/y", "2" }, "-1/y"
        },
        { "edu.cmu.pact.miss.userDef.algebra.DivTerm",
            new Object[] { "0", "-y/6" }, "0"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Denominator",
            new Object[] { "9y/9" }, "9"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Denominator",
            new Object[] { "9/y*y" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.Denominator",
            new Object[] { "9y+1" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.GetOperand",
            new Object[] { "rf -10y/-10" }, "-10y/-10"
        },
        { "edu.cmu.pact.miss.userDef.algebra.SubTerm",
            new Object[] { "-10y/-10", "-10y/-10" }, "0"
        },
        { "edu.cmu.pact.miss.userDef.algebra.InverseTerm",
            new Object[] { "0" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.weak.FirstNumber",
            new Object[] { "x/(-2)" }, "2"
        },
        { "edu.cmu.pact.miss.userDef.algebra.weak.GetFirstNumberWithSign",
            new Object[] { "x/(-2)" }, "-2"
        },
        { "edu.cmu.pact.miss.userDef.algebra.weak.GetFirstNumberWithSign",
            new Object[] { "x/2" }, "2"
        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTerm",
            new Object[] { "y/6", "6" }, "y"
        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTerm",
            new Object[] { "1", "6" }, "6"
        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTerm",
            new Object[] { "-3", "3" }, "-9"
        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTerm",
            new Object[] { "y/3", "3" }, "y"
        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTerm",
            new Object[] { "3", "y/-4" }, "-3y/4"
        },
        { "edu.cmu.pact.miss.userDef.algebra.DivTerm",
        	new Object[] { "0+0", "5" }, "0"
        },
        { "edu.cmu.pact.miss.userDef.algebra.MulTerm",
            new Object[] { "3/x", "x" }, "3"
        },
        { "edu.cmu.pact.miss.userDef.algebra.HasCoefficient",
            new Object[] { "x/3" }, null
        },
        { "edu.cmu.pact.miss.userDef.algebra.Coefficient",
            new Object[] { "x/3" }, "1/3"
        },
        { "edu.cmu.pact.miss.userDef.algebra.HasCoefficient",
            new Object[] { "1/3x" }, "T"
        },
        { "edu.cmu.pact.miss.userDef.algebra.Coefficient",
            new Object[] { "1/3x" }, "1/3"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.Coefficient",
            new Object[] { "5xy" }, "5"
        },

        { "edu.cmu.pact.miss.userDef.algebra.FirstTerm",
            new Object[] { "5.5x-2.2x" }, "5.5x"
        },

        { "edu.cmu.pact.miss.userDef.algebra.LastTerm",
            new Object[] { "5.5x-2.2x" }, "-2.2x"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.Numerator",
            new Object[] { "5.3x/2.3" }, "5.3x"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.Coefficient",
            new Object[] { "x/7" }, "1/7"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.RipCoefficient",
            new Object[] { "5xy" }, "xy"
        },

        { "edu.cmu.pact.miss.userDef.algebra.DivTen",
            new Object[] { "1.5" }, "0.15"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "3/1.5" }, "2"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "1/(4x+5)" }, "1/(4x+5)"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "x/7+6" }, "x/7+6"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "4" }, "4"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "x+4" }, "x+4"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.EvalArithmetic",
            new Object[] { "2+3x" }, "3x+2"
        },
        
        {
        	"edu.cmu.pact.miss.userDef.algebra.SubTerm",
            new Object[] { "2x" , "5x" }, "-3x"
        },

        { "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
        	new Object[] {"-5"}, "5"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.SkillAdd",
        	new Object[] {"5"}, "add 5" 
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.LastConstTerm",
        	new Object[] {"x-5"}, "5"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.LastVarTerm",
        	new Object[] {"9-7x"}, "-7x" 
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.ReverseSign",
        	new Object[] {"-7x"}, "7x"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.SkillAdd",
        	new Object[] {"7x"}, "add 7x" 
        },
        
        { "LucyWeakPK.GetFirstInteger",
        	new Object[] {"-3x"}, "-3"
        },
        
        { "edu.cmu.pact.miss.userDef.algebra.Numerator",
        		new Object[] {"-q"}, "-1"
        }
    };
}
