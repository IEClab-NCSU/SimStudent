package tracer;
import tracer.MTException.MTExceptionType;
import jess.*;

public class MTInputFunction extends MTUserFunction {

	private MTSAI sai;
	private MTSolver solver;
	
	public MTInputFunction(MTSolver solver) {
		this.solver = solver;
	}

	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException, MTException {
		Value v = super.call(vv, context);

		if (this.on) {

			String tutorSelection = getStringFromValue(vv.get(1), context);
			String tutorAction = getStringFromValue(vv.get(2), context);
			String tutorInput = getStringFromValue(vv.get(3), context);

			MTSAI tutorSAI = new MTSAI(tutorSelection, tutorAction, tutorInput);
			if(!this.sai.equals(tutorSAI)) {
				throw new MTException(MTExceptionType.FAIL, "Tutor Sai: " + tutorSAI);
			}
			this.solver.setStudentInput(this.sai.getInput());
		}

		return v;
	}
	

	@Override
	public String getName() {
		return "model-trace-input";
	}

	public MTSAI getSai() {
		return sai;
	}

	public void setSai(MTSAI sai) {
		this.sai = sai;
	}

}
