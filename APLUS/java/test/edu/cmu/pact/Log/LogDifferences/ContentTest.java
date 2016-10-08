package edu.cmu.pact.Log.LogDifferences;

import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.TestCase;
import edu.cmu.pact.Log.LogDifferences.Content.ActionEvaluationContent;
import edu.cmu.pact.Log.LogDifferences.Content.ContentCell;
import edu.cmu.pact.Log.LogDifferences.Content.CustomContent;
import edu.cmu.pact.Log.LogDifferences.Content.NameContent;
import edu.cmu.pact.Log.LogDifferences.Content.SAIContent;
import edu.cmu.pact.Log.LogDifferences.Content.SkillContent;
import edu.cmu.pact.Log.LogDifferences.Content.TutorAdviceContent;

public class ContentTest extends TestCase {
	
	public void testName(){
		NameContent name = new NameContent("name");
		Iterator<ContentCell> iter = name.iterator();
		
		ContentCell nameCell = iter.next();
		
		assertEquals("problem_name", nameCell.getFieldType());
		assertEquals(-1, nameCell.getIndex());
		assertNull(nameCell.getPropertyName());
		assertEquals("name", nameCell.getContent());
		assertEquals("problem_name", nameCell.getColumnName());
		assertFalse(iter.hasNext());
	}
	
	public void testSAI(){
		SAIContent sai = new SAIContent();
		sai.addAction("actionContent");
		sai.addInput("inputContent");
		Iterator<ContentCell> iter = sai.iterator();
		
		ContentCell actionCell = iter.next();
		assertEquals("action", actionCell.getFieldType());
		assertEquals(0, actionCell.getIndex());
		assertNull(actionCell.getPropertyName());
		assertEquals("actionContent", actionCell.getContent());
		assertEquals("action0", actionCell.getColumnName());
		
		ContentCell inputCell = iter.next();
		assertEquals("input", inputCell.getFieldType());
		assertEquals(0, inputCell.getIndex());
		assertNull(inputCell.getPropertyName());
		assertEquals("inputContent", inputCell.getContent());
		assertEquals("input0", inputCell.getColumnName());
		
		assertFalse(iter.hasNext());
	}
	
	public void testSAIMultiple(){
		SAIContent sai = new SAIContent();
		sai.addAction("actionContent");
		sai.addAction("actionContent2");
		Iterator<ContentCell> iter = sai.iterator();
		
		ContentCell actionCell = iter.next();
		assertEquals("action", actionCell.getFieldType());
		assertEquals(0, actionCell.getIndex());
		assertNull(actionCell.getPropertyName());
		assertEquals("actionContent", actionCell.getContent());
		assertEquals("action0", actionCell.getColumnName());
		
		ContentCell actionCell2 = iter.next();
		assertEquals("action", actionCell2.getFieldType());
		assertEquals(1, actionCell2.getIndex());
		assertNull(actionCell2.getPropertyName());
		assertEquals("actionContent2", actionCell2.getContent());
		assertEquals("action1", actionCell2.getColumnName());
		
		assertFalse(iter.hasNext());
	}
	
	public void testActionEvaluation(){
		ActionEvaluationContent ae = new ActionEvaluationContent("evaluationContent", 
				"huntNum", "totalHints", "hint id", "classificationContent");

		Iterator<ContentCell> iter = ae.iterator();

		ContentCell eval = iter.next();
		assertEquals("action_evaluation", eval.getFieldType());
		assertEquals(-1, eval.getIndex());
		assertEquals("evaluation", eval.getPropertyName());
		assertEquals("evaluationContent", eval.getContent());
		assertEquals("action_evaluation evaluation", eval.getColumnName());
		
		ContentCell huntNum = iter.next();
		assertEquals("action_evaluation", huntNum.getFieldType());
		assertEquals(-1, huntNum.getIndex());
		assertEquals("current_hint_number", huntNum.getPropertyName());
		assertEquals("huntNum", huntNum.getContent());
		assertEquals("action_evaluation current_hint_number", huntNum.getColumnName());
		
		ContentCell totalHints = iter.next();
		assertEquals("action_evaluation", totalHints.getFieldType());
		assertEquals(-1, totalHints.getIndex());
		assertEquals("total_hints_available", totalHints.getPropertyName());
		assertEquals("totalHints", totalHints.getContent());
		assertEquals("action_evaluation total_hints_available", totalHints.getColumnName());
		
		ContentCell hintId = iter.next();
		assertEquals("action_evaluation", hintId.getFieldType());
		assertEquals(-1, hintId.getIndex());
		assertEquals("hintId", hintId.getPropertyName());
		assertEquals("hint id", hintId.getContent());
		assertEquals("action_evaluation hintId", hintId.getColumnName());
		
		ContentCell classification = iter.next();
		assertEquals("action_evaluation", classification.getFieldType());
		assertEquals(-1, classification.getIndex());
		assertEquals("classification", classification.getPropertyName());
		assertEquals("classificationContent", classification.getContent());
		assertEquals("action_evaluation classification", classification.getColumnName());
		
		assertFalse(iter.hasNext());
	}
	
	public void testTutorAdvice(){
		TutorAdviceContent advice = new TutorAdviceContent("advice", 0);
		Iterator<ContentCell> iter = advice.iterator();
		
		ContentCell adviceCell = iter.next();
		assertEquals("tutor_advice", adviceCell.getFieldType());
		assertEquals(0, adviceCell.getIndex());
		assertNull(adviceCell.getPropertyName());
		assertEquals("advice", adviceCell.getContent());
		assertEquals("tutor_advice0", adviceCell.getColumnName());
		
		assertFalse(iter.hasNext());
	}

	public void testSkill(){
		SkillContent skill = new SkillContent(
				0,
				"probability",
				"skillName",
				"categoryContent",
				new ArrayList<String>(),
				false);

		Iterator<ContentCell> iter = skill.iterator();

		ContentCell probabilityCell = iter.next();
		assertEquals("skill", probabilityCell.getFieldType());
		assertEquals(0, probabilityCell.getIndex());
		assertEquals("probability", probabilityCell.getPropertyName());
		assertEquals("probability", probabilityCell.getContent());
		assertEquals("skill0 probability", probabilityCell.getColumnName());
		
		ContentCell skillNameCell = iter.next();
		assertEquals("skill", skillNameCell.getFieldType());
		assertEquals(0, skillNameCell.getIndex());
		assertEquals("name", skillNameCell.getPropertyName());
		assertEquals("skillName", skillNameCell.getContent());
		assertEquals("skill0 name", skillNameCell.getColumnName());
		
		ContentCell categoryCell = iter.next();
		assertEquals("skill", categoryCell.getFieldType());
		assertEquals(0, categoryCell.getIndex());
		assertEquals("category", categoryCell.getPropertyName());
		assertEquals("categoryContent", categoryCell.getContent());
		assertEquals("skill0 category", categoryCell.getColumnName());
		
		ContentCell model_nameCell = iter.next();
		assertEquals("skill", model_nameCell.getFieldType());
		assertEquals(0, model_nameCell.getIndex());
		assertEquals("model_name", model_nameCell.getPropertyName());
		assertEquals("[]", model_nameCell.getContent());
		assertEquals("skill0 model_name", model_nameCell.getColumnName());
		
		ContentCell buggyCell = iter.next();
		assertEquals("skill", buggyCell.getFieldType());
		assertEquals(0, buggyCell.getIndex());
		assertEquals("buggy", buggyCell.getPropertyName());
		assertEquals("false", buggyCell.getContent());
		assertEquals("skill0 buggy", buggyCell.getColumnName());
		
		assertFalse(iter.hasNext());
	}
	
	public void testCustom(){
		CustomContent custom = new CustomContent(0, "name", "value");
		Iterator<ContentCell> iter = custom.iterator();
		
		ContentCell customCell = iter.next();
		assertEquals("custom_field", customCell.getFieldType());
		assertEquals(0, customCell.getIndex());
		assertEquals("name", customCell.getPropertyName());
		assertEquals("value", customCell.getContent());
		assertEquals("custom_field name", customCell.getColumnName());
		
		assertFalse(iter.hasNext());
	}
}
