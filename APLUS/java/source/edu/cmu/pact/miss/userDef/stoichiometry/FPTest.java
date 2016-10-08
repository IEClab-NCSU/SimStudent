package edu.cmu.pact.miss.userDef.stoichiometry;
import java.util.Arrays;

import edu.cmu.pact.miss.FeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.SkillClt;
import edu.cmu.pact.miss.userDef.generic.weak.CopyString;

import java.util.Arrays;
import java.util.Vector;

public class FPTest 
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
		System.out.println("Number failed: "+ errorCount);
		System.out.println("Total tests run: " +test.length);
		FeaturePredicate m = new CopyString();
		Vector argV = new Vector();
		
		    argV.add( "H2O" );
		
		System.out.println(m.cachedApply(argV));
		//FeaturePredicate m = new UnitConv();
		//Vector argV = new Vector();
		
		    //argV.add( "1m1L3 s" );
		    //argV.add( "1m1L3 s" );
		
		//System.out.println(m.cachedApply(argV));
		
	    }
	
	    static Object[][] test=
	    {
	    		{ "edu.cmu.pact.miss.userDef.stoichiometry.UnitConv",
	    		  new Object[] {  }, "Unit conversion"
	    		},
		    		{ "edu.cmu.pact.miss.userDef.stoichiometry.GetIntermediateUnit",
			    		  new Object[] { "mg", "kg" }, "g"
			    		},
		    		{ "edu.cmu.pact.miss.userDef.stoichiometry.GetConversionFactor",
			    		  new Object[] { "L", "mL" }, "1"
			    		},
			    		{ "edu.cmu.pact.miss.userDef.stoichiometry.MultExact",
				    		  new Object[] { "7", "1000" }, "7.0e3"
				    		},
			    		{ "edu.cmu.pact.miss.userDef.stoichiometry.DivideExact",
				    		  new Object[] { "6", "3" }, "2.0"
				    		},
				    		{ "edu.cmu.pact.miss.userDef.stoichiometry.Round",
					    		  new Object[] { "212", "1" }, "2e2"
					    	},
	    		{
	    		"edu.cmu.pact.miss.userDef.stoichiometry.MatchObject",
	    		  new Object[] { "x/4", "x/5" }, null
	    		},
	    		{
		    		"edu.cmu.pact.miss.userDef.stoichiometry.MatchUnitConv",
		    		  new Object[] { "L" }, null
		    	}
	    		/*
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
	    		{ "edu.cmu.pact.miss.userDef.algebra.HasConstTerm",
	    			  new Object[] { "7x/7" }, null
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
	    				{ "edu.cmu.pact.miss.userDef.algebra.Homogeneous",
		    				  new Object[] { "5x" }, "T"
		    				},
		    				{ "edu.cmu.pact.miss.userDef.algebra.Homogeneous",
			    				  new Object[] { "5" }, "T"
			    				},
			    				{ "edu.cmu.pact.miss.userDef.algebra.Homogeneous",
				    				  new Object[] { "x+y+z" }, "T"
				    				},
				    				{ "edu.cmu.pact.miss.userDef.algebra.Homogeneous",
					    				  new Object[] { "1+2+3" }, "T"
					    			},
					    			{ "edu.cmu.pact.miss.userDef.algebra.Homogeneous",
					    				  new Object[] { "x+3" }, null
					    				},*/
					    				
		    				
	    				
	    };
}
