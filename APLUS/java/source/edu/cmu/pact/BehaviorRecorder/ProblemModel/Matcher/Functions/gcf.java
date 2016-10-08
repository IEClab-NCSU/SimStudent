package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

/**
 * Returns the greatest common factor of 2 numbers: e.g. gcf(12,8) outputs 4.
 */
public class gcf {
   
   /**
    * <p>Returns the greatest common factor: e.g. gcf(12,8) outputs 4.</p>
    * To find the LCD (Least Common Denominator) you can also use
    * this gcf function: LCD of a and b = a * b / gcf(a,b);
    * e.g. LCD of 6 and 9 = 6 * 9 / gcf(6,9) = 54 / 3 = 18.
    * @param aD first operand
    * @param bD second operand
    * @return gcf(aD,bD); if either argument is 0, returns the other argument.
    */
   public Double gcf(double aD, double bD) {
      return new Double(internalGcf(aD, bD));
   }
   
   private Double internalGcf(double aD, double bD) {
      long a = (long) aD;
      long b = (long) bD;
      if (a==0 || b==0)
    	  return (double) Math.abs( Math.max( Math.abs(a), Math.abs(b)) );
      long r = a % b;
      if (r != 0)
    	  return internalGcf(b, r);
      else
    	  return new Double(Math.abs(b));
   }
   
}
