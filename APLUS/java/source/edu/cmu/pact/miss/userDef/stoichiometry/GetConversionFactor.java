package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;
public class GetConversionFactor extends StoFeatPredicate{

	//Gets the source half of the conversion factor when given a 
	// source and a destination unit
		public GetConversionFactor(){
			setName("get-conversion-factor");
			setArity(2);
			setReturnValueType(TYPE_ARITH_EXP);
			setArgValueType(new int[]{TYPE_UNIT, TYPE_UNIT});
		}

		public String apply(Vector args) {
			//trace.out("boots20", "returning [" + supplyReason((String)args.get(0), ReasonOperator.UNITCONV) + "]");
			//Get the conversion factor from a table
			return ucValue((String)args.get(0), (String)args.get(1))[1];
		}
}
