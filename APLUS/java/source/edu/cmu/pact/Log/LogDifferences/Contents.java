package edu.cmu.pact.Log.LogDifferences;

import java.util.Iterator;
import java.util.List;

import edu.cmu.pact.Log.LogDifferences.Content.ActionEvaluationContent;
import edu.cmu.pact.Log.LogDifferences.Content.ContentCell;
import edu.cmu.pact.Log.LogDifferences.Content.CustomContent;
import edu.cmu.pact.Log.LogDifferences.Content.NameContent;
import edu.cmu.pact.Log.LogDifferences.Content.SAIContent;
import edu.cmu.pact.Log.LogDifferences.Content.SkillContent;
import edu.cmu.pact.Log.LogDifferences.Content.TutorAdviceContent;

public interface Contents extends Iterable<ContentCell>{

	public Iterator<ContentCell> iterator();

	public NameContent getName();

	public SAIContent getSAI();

	public ActionEvaluationContent getActionEval();

	/** @return list of TutorAdviceContents or an empty list */
	public List<TutorAdviceContent> getTutorAdvices();

	/** @return list of SkillContents or an empty list */
	public List<SkillContent> getSkills();

	/** @return list of CustomContents or an empty list */
	public List<CustomContent> getCustomFields();

	public String getTransactionId();

}