package tracer;

import java.util.List;

import tracer.MTException.MTExceptionType;
import jess.Context;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;

/**
 * Userfunction called to compare buggy tutor sai to a student sai.
 * @author Alex Xiao
 *
 */
public class BuggyFunction extends MTUserFunction {
	
	private MTSAI sai;

	
	public MTSAI getSai() {
		return sai;
	}

	public void setSai(MTSAI sai) {
		this.sai = sai;
	}


	@Override
	public Value call(ValueVector vv, Context context) throws JessException, MTException {
		Value v = super.call(vv, context);
		if (this.on) {
			
			String tutorSelection = getStringFromValue(vv.get(1), context);
			String tutorAction = getStringFromValue(vv.get(2), context);
			String tutorInput = getStringFromValue(vv.get(3), context);
			String message = getStringFromValue(vv.get(4), context);

			MTSAI tutorSAI = new MTSAI(tutorSelection, tutorAction, tutorInput);
			if(this.sai.equals(tutorSAI)) {
				System.out.println("buggy caught!");
				throw new MTException(MTExceptionType.BUGGYMATCH, message);
			} else {
				throw new MTException(MTExceptionType.BUGGYFAIL, "");
			}
		}

		return v;
	}

	@Override
	public String getName() {
		return "buggy";
	}

}
