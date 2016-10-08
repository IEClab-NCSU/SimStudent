package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;
public class GetIntermediateUnit extends StoFeatPredicate{

	//Gets the intermediate unit to convert to when given 
	// a destination and a source unit
		public GetIntermediateUnit(){
			setName("get-inter-unit");
			setArity(2);
			setReturnValueType(TYPE_UNIT);
			setArgValueType(new int[]{TYPE_UNIT, TYPE_UNIT});
		}

		public String apply(Vector args) {
			//trace.out("boots20", "returning [" + supplyReason((String)args.get(0), ReasonOperator.UNITCONV) + "]");
			//Get the conversion factor from a table
			return interUnit((String)args.get(0), (String)args.get(1));
		}
}