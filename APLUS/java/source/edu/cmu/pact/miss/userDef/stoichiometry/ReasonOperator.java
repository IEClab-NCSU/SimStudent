package edu.cmu.pact.miss.userDef.stoichiometry;
import edu.cmu.pact.miss.FeaturePredicate;
//Superclass of operators hard-coded to simply look for
//the different 'reasons' supplied by drop down boxes.
abstract class ReasonOperator extends FeaturePredicate {

	public static final String GIVEN = "Given Value";
	public static final String UNITCONV = "Unit conversion";
	public static final String AVOGADRO = "Avogadro's Number";
	public static final String MOLWEIGHT = "Molecular Weight";
	public static final String COMPSTO = "Composition Stoichiometry";
	public static final String SOLCONC = "Solution Concentration";

	/*
	public static boolean reasonFlag; //if true, then a reason is supplied; if false, then a new reason is coming
	public static String currentReason;

	public static void toggleReasonFlag() {
		reasonFlag = !reasonFlag;
	}
	*/
	/**
	 * The StoInputParser will read the input first, and if it is from the ComboBox, will
	 * preemptively tell the ReasonOperators what is expected; this way, the reasonflag can
	 * correctly be set to True to ensure that these operators do not return incorrect reasons
	 * during other steps.
	 *
	 * Basically a hacked work around.
	 *
	 * @param newReason
	 */
	/*
	public static void setCurrentReason(String newReason) {
		currentReason = newReason;
	}
	*/
	public ReasonOperator() {
	}

	//Check to make sure that incoming FOA isn't already a reason
	//that is, don't allow conversions between different reasons
	//TODO Remove FOA from all operators
	public String supplyReason(String foa, String reason) {
		//if (reasonFlag) //don't return if a reason is already set
		//	return null;
		//if (reason.equals(currentReason)) {
			//toggleReasonFlag();
			//currentReason = null;
			//return reason;
		//}
		return reason;
	}

}
