package edu.cmu.pact.miss.SimStAlgebraV8.LucyWeakPK;


import java.lang.*;
import java.util.*;

public class TermGrabber
{
	public static void main(String Args[])
	{
		String s = "33 + -51x + x + 7x = 5x + 4";
		String s1 = "33+-51x+7x = 5x + 4 +3/x + x/5 + 1/3x + 7x/9";
		String s2 = "1/2 + 4 + 4.3 + -51 + -5/4x";
		String s3 = "x/2 + x/3 + 4x - 5/6x -4x -4/3x -x/3 -9";
		String s4 = "4x+8";
		
		String expString="4x+8=8";
		
		
		System.out.println(TermGrabber.stripSign(TermGrabber.findNthNearestInteger(expString, 0)));
	        
	        
	        
	      
	        
	        
/*		System.out.println();

		System.out.println(findNthInteger(s2, 0));
		System.out.println(findNthNumber(s2, 0));
		System.out.println(findNthNumber(s2, 1));
		System.out.println(findNthNumber(s2, 2));
		System.out.println(findNthNumber(s2, 3));
		System.out.println(findNthNumber(s2, 4));
		System.out.println(findNthNumber(s2, 5));
		System.out.println(findNthVarTermWithCoefficient(s2, 0));
		System.out.println(findNthIntegerBeforeLetter(s2, 0));
		System.out.println(findNthNumberBeforeLetter(s2, 0));
		System.out.println(findNthNumberBeforeLetter(s2, 1));
		System.out.println("-5/4x".matches("^(-)?((\\d)+|((\\d)+)/((\\d)+)|((\\d){0,})\\.(\\d)+)([a-z|A-Z]){1}"));

		System.out.println();


		System.out.println(findNthInteger(s1, 0));
		System.out.println(findNthInteger(s1, 1));
		System.out.println(findNthInteger(s1, 2));
		System.out.println(findNthInteger(s1, 3));
		System.out.println(findNthInteger(s1, 4));
		System.out.println(findNthInteger(s1, 5));
		System.out.println(findNthInteger(s1, 6));
		System.out.println(findNthInteger(s1, 7));
		System.out.println(findNthInteger(s1, 8));
		System.out.println(findNthInteger(s1, 9));
		System.out.println();

		System.out.println(findNthIntegerWithoutSign(s1, 0));
		System.out.println(findNthIntegerWithoutSign(s1, 1));
		System.out.println(findNthIntegerWithoutSign(s1, 2));
		System.out.println(findNthIntegerWithoutSign(s1, 3));
		System.out.println(findNthIntegerWithoutSign(s1, 4));
		System.out.println(findNthIntegerWithoutSign(s1, 5));
		System.out.println(findNthIntegerWithoutSign(s1, 6));
		System.out.println(findNthIntegerWithoutSign(s1, 7));
		System.out.println(findNthIntegerWithoutSign(s1, 8));
		System.out.println(findNthIntegerWithoutSign(s1, 9));
		System.out.println();

		System.out.println(findNthNearestInteger(s1, 0));
		System.out.println(findNthNearestInteger(s1, 1));
		System.out.println(findNthNearestInteger(s1, 2));
		System.out.println(findNthNearestInteger(s1, 3));
		System.out.println(findNthNearestInteger(s1, 4));
		System.out.println(findNthNearestInteger(s1, 5));
		System.out.println(findNthNearestInteger(s1, 6));
		System.out.println(findNthNearestInteger(s1, 7));
		System.out.println(findNthNearestInteger(s1, 8));
		System.out.println();
/*
		System.out.println(findNthInteger(s, 0));
		System.out.println(findNthInteger(s1, 0));
		System.out.println(findNthInteger(s, 1));
		System.out.println(findNthInteger(s1, 1));
		System.out.println(findNthInteger(s, 2));
		System.out.println(findNthInteger(s1, 2));
		System.out.println(findNthInteger(s, 3));
		System.out.println(findNthInteger(s1, 3));
		System.out.println(findNthInteger(s, 4));
		System.out.println(findNthInteger(s1, 4));

		System.out.println();
		System.out.println(findNthVarTermWithCoefficient(s1, 0));
		System.out.println(findNthVarTermWithCoefficient(s1, 1));
		System.out.println(findNthVarTermWithCoefficient(s1, 2));
		System.out.println(findNthVarTermWithCoefficient(s1, 3));
		System.out.println(findNthVarTermWithCoefficient(s1, 4));
		System.out.println(findNthVarTermWithCoefficient(s1, 5));
		System.out.println(findNthVarTermWithCoefficient(s1, 6));
		System.out.println(findNthVarTermWithCoefficient(s1, 7));
		System.out.println(findNthVarTermWithCoefficient(s1, 8));
		System.out.println(findNthVarTermWithCoefficient(s1, 9));

		System.out.println();
		System.out.println(findNthIntegerBeforeLetter(s1, 0));
		System.out.println(findNthIntegerBeforeLetter(s1, 1));
		System.out.println(findNthIntegerBeforeLetter(s1, 2));
		System.out.println(findNthIntegerBeforeLetter(s1, 3));
		System.out.println(findNthIntegerBeforeLetter(s1, 4));
		System.out.println(findNthIntegerBeforeLetter(s1, 5));
		System.out.println(findNthIntegerBeforeLetter(s1, 6));
		System.out.println(findNthIntegerBeforeLetter(s1, 7));
		System.out.println(findNthIntegerBeforeLetter(s1, 8));
		System.out.println(findNthIntegerBeforeLetter(s1, 9));

*/		//System.out.println(stripSign(null));
			
	//	System.out.println(findNthToken("4x+8", " =+,*", 9, "^(-)?(((\\d)+/(\\d)+)([a-z|A-Z]){1}|((\\d)+)([a-z|A-Z]){1}|((\\d)+)/([a-z|A-Z]){1}|([a-z|A-Z]){1}/((\\d)+)|((\\d)+)([a-z|A-Z]){1}/((\\d)+))"));
		
		
		
	}	

	/*
		TermGrabber.findNthNumber(String, n)

		Description:
			returns the nth signed rational number (fractions, decimals & integers) in the target string
	*/

	public static String findNthNumber(String expString, int t)
    	{			
		return findNthToken(expString, " =+,*abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", t, "^(-)?((\\d)+|((\\d)+)/((\\d)+)|((\\d){0,})\\.(\\d)+)");
   	}


	/*
		TermGrabber.findLastInteger(String)

		Description:
			returns the last Integer in the string
	*/

	public static String findLastInteger(String expString)
    	{			
		return findLastToken(expString, " =+/,*abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", "^(-)?(\\d)+");
   	}


	/*
		TermGrabber.findNthIntegerWithoutSign(String, n)

		Description:
			returns the nth integer in the string with its negative sign (if any) stripped
	*/

	public static String findNthIntegerWithoutSign(String expString, int t)
    	{			
		return stripSign(findNthToken(expString, " =+/,*abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", t, "^(-)?(\\d)+"));
   	}


	/*
		TermGrabber.findNthNearestInteger(String, n)

		Description:
			returns the nth signed integer in the string
	*/

	public static String findNthNearestInteger(String expString, int t)
    	{			
		// ((\\d)+|((\\d)+)/((\\d)+)|((\\d){0,})\\.(\\d)+)(/)?([a-z|A-Z]){1}(/(\\d)+)?
		// fractions at the front: ((\\d)+/(\\d)+)([a-z|A-Z]){1}
		// integer at the front: ((\\d)+)([a-z|A-Z]){1}
		// integer/x: ((\\d)+)/([a-z|A-Z]){1}
		// x/integer: ([a-z|A-Z]){1}/((\\d)+)
		// integer x/integer: ((\\d)+)([a-z|A-Z]){1}/((\\d)+)

		return findLastInteger(findNthToken(expString, " =+,*", t, "^(-)?(((\\d)+/(\\d)+)([a-z|A-Z]){1}|((\\d)+)([a-z|A-Z]){1}|((\\d)+)/([a-z|A-Z]){1}|([a-z|A-Z]){1}/((\\d)+)|((\\d)+)([a-z|A-Z]){1}/((\\d)+))"));
   	}


	/*
		TermGrabber.findNthInteger(String, n)

		Description:
			returns the nth signed integer in the string
	*/

	public static String findNthInteger(String expString, int t)
    	{			
		return findNthToken(expString, " =+/,*abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", t, "^(-)?(\\d)+");
   	}


	/*
		TermGrabber.findNthVarTermWithCoefficient(String, n)

		Description:
			returns the nth variable term in the string that has a signed coefficient
	*/

	public static String findNthVarTermWithCoefficient(String expString, int t)
	{
		// ((\\d)+|((\\d)+)/((\\d)+)|((\\d){0,})\\.(\\d)+)(/)?([a-z|A-Z]){1}(/(\\d)+)?
		// fractions at the front: ((\\d)+/(\\d)+)([a-z|A-Z]){1}
		// integer at the front: ((\\d)+)([a-z|A-Z]){1}
		// x/integer: ([a-z|A-Z]){1}/((\\d)+)
		// integer x/integer: ((\\d)+)([a-z|A-Z]){1}/((\\d)+)

		return findNthToken(expString, " =+,*", t, "^(-)?(((\\d)+/(\\d)+)([a-z|A-Z]){1}|((\\d)+)([a-z|A-Z]){1}|([a-z|A-Z]){1}/((\\d)+)|((\\d)+)([a-z|A-Z]){1}/((\\d)+))");
	}



	/*
		TermGrabber.findNthNumberBeforeLetter(String, n)

		Description:
			returns the nth integer before Letter
	*/

	public static String findNthNumberBeforeLetter(String expString, int t)
    	{
		return findNthNumber(findNthVarTermWithCoefficient(expString, t), 0);
   	}



	/*
		TermGrabber.findNthIntegerBeforeLetter(String, n)

		Description:
			returns the nth integer before Letter
	*/

	public static String findNthIntegerBeforeLetter(String expString, int t)
    	{
		return findNthInteger(findNthVarTermWithCoefficient(expString, t),0);
   	}



	/*
		TermGrabber.stripSign(String)

		Description:
			returns a new version of the target string with the negative sign (if it had one) stripped
	*/

	public static String stripSign(String expString)
    	{			
		return expString.replace("-","");
   	}


	/*
		TermGrabber.findLastToken(String, Delimiter, RegEx)

		Description:
			returns the last token after separating a target string with designated delimiters.
	*/

	public static String findLastToken(String expString, String delim, String pat)
	{

		if(expString == null)
			return null;

		StringTokenizer st = new StringTokenizer(expString, delim);
		
		String numString = "";
		int count = 0;
		String lastToken = null;
	        
		while(st.hasMoreTokens())
		{
			numString = st.nextToken();
			//System.out.print(numString + " ");
	
			if(numString.matches(pat))
			{	
				lastToken = numString;
				count++;
			}
		}
				
		return lastToken;
	}


	/*
		TermGrabber.findNthToken(String, Delimiter, n, RegEx)

		Description:
			returns the nth token after seperating a target string with designated delimiters.
	*/

	public static String findNthToken(String expString, String delim, int t, String pat)
	{

		if(expString == null)
			return null;

		StringTokenizer st = new StringTokenizer(expString, delim);
		
		String numString = "";
		int count = 0;
	        
		while(st.hasMoreTokens())
		{
	
			
			numString = st.nextToken();
			//System.out.println("\t" + numString + " " + numString.matches(pat));
	
			if(numString.matches(pat))
			{
				if(count == t)
				{
					return numString;
				}	

				count++;
			}
		}
				
		return null;
	}





/*
####################################################################################
Old Implementation (Ignore content below this line)
####################################################################################
*/

/*
public static String findNthIntegerOld(String expString, int t)
    	{
		StringTokenizer st = new StringTokenizer(expString," =+/,*-abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");

		String numString = "";
		int count = 0;
	        
		while(st.hasMoreTokens())
		{
			numString = st.nextToken();
			System.out.println(numString);
			try
			{
				int newNum = Integer.parseInt(numString);
			}
			catch(NumberFormatException e)
			{
				count--;
			}
			

			if(count == t)
			{
				return numString;
			}	

			count++;
		}
				
		return null;
   	 }
    */
}