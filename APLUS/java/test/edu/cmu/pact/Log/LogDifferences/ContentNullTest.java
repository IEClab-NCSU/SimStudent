package edu.cmu.pact.Log.LogDifferences;

import java.util.Iterator;

import junit.framework.TestCase;
import edu.cmu.pact.Log.LogDifferences.Content.ActionEvaluationContent;
import edu.cmu.pact.Log.LogDifferences.Content.ContentCell;
import edu.cmu.pact.Log.LogDifferences.Content.CustomContent;
import edu.cmu.pact.Log.LogDifferences.Content.NameContent;
import edu.cmu.pact.Log.LogDifferences.Content.SAIContent;
import edu.cmu.pact.Log.LogDifferences.Content.SkillContent;
import edu.cmu.pact.Log.LogDifferences.Content.TutorAdviceContent;

public class ContentNullTest extends TestCase {
	//I believe that by design, classes that use the ContentCellBuilder to build
	//cells will skip incomplete data by themselves.
	//This means that it is not possible for a ContentCell to have missing data
	//that it needs.
	
	public void testNameNull(){
		NameContent test = new NameContent(null);
		Iterator<ContentCell> iter = test.iterator();

		assertFalse(iter.hasNext());
	}
	
	public void testSAIEmpty(){
		SAIContent sai = new SAIContent();
		Iterator<ContentCell> iter = sai.iterator();
		
		assertFalse(iter.hasNext());
	}
	
	public void testAEPartlyEmpty(){
		ActionEvaluationContent ae = new ActionEvaluationContent("evaluation", null, null, "id", null);
		Iterator<ContentCell> iter = ae.iterator();
		
		assertTrue(iter.hasNext());
		iter.next();
		assertTrue(iter.hasNext());
		iter.next();
		assertFalse(iter.hasNext());
	}
	
	public void testTutorAdviceEmpty(){
		TutorAdviceContent advice = new TutorAdviceContent(null, 0);
		Iterator<ContentCell> iter = advice.iterator();

		assertFalse(iter.hasNext());
	}
	
	public void testSkillEmpty(){
		SkillContent skill = new SkillContent(0, null, null, null, null, null);
		Iterator<ContentCell> iter = skill.iterator();

		assertFalse(iter.hasNext());
	}
	
	public void testCustomEmpty(){
		CustomContent custom = new CustomContent(0, null, null);
		Iterator<ContentCell> iter = custom.iterator();
		
		assertFalse(iter.hasNext());
	}
}
