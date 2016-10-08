
package edu.cmu.old_pact.cmu.tutor;

import edu.cmu.old_pact.cmu.solver.ruleset.RuleMatchInfo;

public interface Tutor extends SharedObject {
	public void setTranslator(TranslatorProxy translator);
	public TranslatorProxy getTranslator();
	public void startProblem(String problem);
	public RuleMatchInfo checkStudentAction(String selection,String action,String input);
	public void startNextStep(String action);
}
