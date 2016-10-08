package edu.cmu.old_pact.cl.tutors.skillometer;

import edu.cmu.pact.Utilities.trace;

/**
* Used for tutors which produce skill gradient, not a skill value.
**/


public class SkillCalculator {
	private int defKnown    = 250,
				defLearn    = 200,
				defGuess    = 200,
				defSlip     = 400;
	private int known, learn, guess, slip;
	private String skillName;
	
	public SkillCalculator(String skName) {
		skillName = skName;
		known = defKnown;
		learn = defLearn;
		guess = defGuess;
		slip = defSlip;
	}
	
	public String getName(){
		return skillName;
	}
	
	public String getSkillName(){
		return skillName;
	}
	
	double getPKnown() {
		if (known == 1000)
			return 0.999;
		else
			return (double)known/1000.0;
	}

	double getPLearn() {
		return (double)learn/1000.0;
	}

	double getPGuess() {
		return (double)guess/1000.0;
	}

	double getPSlip() {
		return (double)slip/1000.0;
	}

	int getKnown() {
		return known;
	}

	int getLearn() {
		return learn;
	}

	int getGuess() {
		return guess;
	}

	int getSlip() {
		return slip;
	}
	
	public void setDefPKnown(float f){
		known = (int)(f*1000);  // sewall 8/24/07: cast had precedence over '*'!
	}
	
	public void setDefPGuess(float f){
		guess = (int)(f*1000);
	}
	
	public void setDefPSlip(float f){
		slip = (int)(f*1000);
	}
	
	public void setDefPLearn(float f){
		learn = (int)(f*1000);
	}

	public String getAllValues() {
		String toret = String.valueOf(known)+","+String.valueOf(learn)+","+String.valueOf(guess)+","+String.valueOf(slip);
		return toret;
	}
	
//set the value as a floating point
//If the value is between 0 and 1, it represents a percentage
//Otherwise, we treat it as an integer
	void setPKnown(double newValue) {
		if (newValue <= 1.0 && newValue >= 0.0) {
			double wholePart;
			double fractPart;
			double largeVal = newValue*1000.0;
			int largeInt = (int)largeVal;
			wholePart = (double)largeInt;
			fractPart = largeVal - wholePart;
			if (fractPart >= 0.5)
				wholePart+=1;
			known = (int)wholePart;
		}
		else if (newValue > 0.0 && newValue <= 1000.0)
			known = (int)newValue;
		else
			trace.err("Illegal value to setPKnown: "+newValue);
	}

	void setPKnown(int val) {
		if (val <= 1000 && val >= 0)
			known=val;
		else
			trace.err("Illegal value to setPKnown: "+val);
	}
	
	public boolean canDisplay(){
		return true;
	}

//updateSkill updates the skill value
//success=0 indicates failure
//success=1 indicates success
//success=2 indicates the user asked for help
	public float updateSkill(boolean success) {
		double pKnown = getPKnown();
		double pLearn = getPLearn();
		double pGuess = getPGuess();
		double pSlip = getPSlip();
		double knewIt;
		trace.out("skills", ">>in SkillCalculator, updating skill "+getName()+"pKnown: "+pKnown+", plearn: "+pLearn+",pGuess: "+
				pGuess+", pSlip: "+pSlip+", success is "+success);
		if (success) { 		//correct
			double guessedIt = pGuess * (1.0 - pKnown);
			double knewAndPerformed = pKnown * (1.0 - pSlip);
			knewIt = knewAndPerformed / (knewAndPerformed + guessedIt);
			trace.out("skills", ">>SkillCalculator, success: guessedit: "+guessedIt+", knewAndPerformed: "+knewAndPerformed+", knewIt: "+knewIt);
		} 
		else {  //error
			double choked = pKnown * pSlip;
			double dontKnowDontGuess = (1.0 - pKnown) * (1.0 - pGuess);
			knewIt =  choked / (choked + dontKnowDontGuess);
			trace.out("skills", ">>SkillCalculator, error: choked: "+choked+", dontKnowDontGuess: "+dontKnowDontGuess+", knewIt: "+knewIt);
		}
		double newp = knewIt + pLearn*(1.0 - knewIt);
		trace.out("skills", ">>SkillCalculator, newp is "+newp);
		setPKnown(newp);
		return (float)getPKnown();
	}
		
}
