package edu.cmu.pact.miss.userDef.fractionAddition;

import java.util.Vector;

import edu.cmu.pact.miss.FeaturePredicate;

public abstract class FractionAdditionFeaturePredicate extends FeaturePredicate{
	
	public static final int TYPE_MIXED_FRACTION = 11;
	public static final int TYPE_GOAL = 12;
	public static final int TYPE_WHOLE_NUMBER = 12;
	
	
	public static int gcd(int number1, int number2) {
        if(number2 == 0){
            return number1;
        }
        return gcd(number2, number1%number2);
    }
    
	
	
}
