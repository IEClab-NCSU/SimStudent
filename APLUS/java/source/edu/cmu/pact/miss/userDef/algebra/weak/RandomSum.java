package edu.cmu.pact.miss.userDef.algebra.weak;

import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import edu.cmu.pact.miss.userDef.algebra.expression.AlgExp;
import edu.cmu.pact.miss.userDef.algebra.expression.ExpParseException;

public class RandomSum extends EqFeaturePredicate 
{
    public RandomSum() 
    {
		setArity(1);
		setName("random-sum");
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply(Vector args) 
    {
    	String expString = (String)args.get(0);
    	
    	int[] numbersList = getNumbers(expString);

    	if(numbersList.length < 2)
    	{
    		return null;
    	}
    	
    	Random r = new Random();
    	int num1 = numbersList[r.nextInt(numbersList.length)];
    	int num2 = numbersList[r.nextInt(numbersList.length)];

    	while(num1 == num2)
    	{
    		num2 = numbersList[r.nextInt(numbersList.length)];
    	}
    	    	    	
    	return (new Integer(num1+num2)).toString();
    }
    
    protected int[] getNumbers(String expString)
    {
    	LinkedList<String> numbers = new LinkedList<String>();
        int count = 0;
        	
        for(int x = 0; x < expString.length(); x++)
        {
        	String number = "";
        	
        	if(Character.isDigit(expString.charAt(x)))
        	{
        		number += expString.charAt(x);
        			
        		while (x < expString.length() - 1 && (Character.isDigit(expString.charAt(x+1))))
        		{
        			x++;
        			number += expString.charAt(x);
        		}
        			
       			numbers.add(number);
       			count++;
        	}
        }
        
        if(numbers.size() > 0)
        {
        	int[] numbersArray = new int[numbers.size()];
        	
        	int i = 0;
        	
        	for(String s : numbers)
        	{
        		numbersArray[i] = Integer.parseInt(s);
        		i++;
        	}
        	
        	return numbersArray;
        }
        else
        {
        	return null;
        }
    }
 }
