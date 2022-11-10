package edu.cmu.pact.Log.LogDifferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;
import edu.cmu.pact.Log.LogDifferences.Content.ActionEvaluationContent;
import edu.cmu.pact.Log.LogDifferences.Content.CustomContent;
import edu.cmu.pact.Log.LogDifferences.Content.NameContent;
import edu.cmu.pact.Log.LogDifferences.Content.SAIContent;
import edu.cmu.pact.Log.LogDifferences.Content.SkillContent;
import edu.cmu.pact.Log.LogDifferences.Content.TutorAdviceContent;
import edu.cmu.pact.Utilities.trace;

public class ColumnTest extends TestCase {
	MockTestingContents test = null;
	
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        test = new MockTestingContents();
        
        test.setName(new NameContent("name"));
        
        SAIContent sai = new SAIContent();
        sai.addSelection("selection");
        sai.addAction("action");
        sai.addInput("input");
        test.setSai(sai);
        
        test.setActionEval(new ActionEvaluationContent("evaluation", "current_hint_number", 
        		"total_hints_available", "hintId", "classification"));
        
        test.addTutorAdvice(new TutorAdviceContent("test tutor advice 0", 6));
        
        test.addSkill(new SkillContent(4, "probability", "name", "category", new ArrayList<String>(), false));
        
        test.addCustom(new CustomContent(9, "custom name", "custom value"));
        
        test.setTransactionId("TRANSACTION_ID");
    }
    
    @Override
    protected void tearDown() throws Exception{
    	test = null;
    }
    
    public void testIfColumnWorks(){
    	ArrayList<Contents> testArray = new ArrayList<Contents>();
    	testArray.add(test);
    	AlphabetizedCustomsColumn column = new AlphabetizedCustomsColumn(testArray, new ArrayList<Contents>());
//    	trace.out(Arrays.toString(column.toArray()));
    	
    	int expectedNumColumns = 4 + 1 + 3 + 5 + 1 + 5 + 1;
    	assertEquals(expectedNumColumns, column.getNumColumns());
    }
    
    public void testMultipleNames(){
    	test.resetFields();
    	test.setName(new NameContent("name1"));
    	
    	MockTestingContents test2 = new MockTestingContents();
    	test2.setName(new NameContent("name2"));
    	
    	MockTestingContents test3 = new MockTestingContents();
    	test3.setName(new NameContent("name3"));
    	
    	MockTestingContents test4 = new MockTestingContents();
    	test4.setName(new NameContent("name4"));
    	
    	ArrayList<Contents> testArray = new ArrayList<Contents>();
    	testArray.add(test);
    	testArray.add(test2);
    	testArray.add(test3);
    	
    	ArrayList<Contents> testArray2 = new ArrayList<Contents>();

    	AlphabetizedCustomsColumn column = new AlphabetizedCustomsColumn(testArray, testArray2);
//    	trace.out(Arrays.toString(column.toArray()));
    	assertEquals(4, (int) column.getIndex("problem_name"));
    	assertEquals(5, column.getNumColumns());
    }
    
    public void testMultipleSAI(){
    	test.resetFields();
    	SAIContent sai = new SAIContent();
    	sai.addSelection("selection");
    	sai.addSelection("selection");
    	sai.addSelection("selection");
    	sai.addAction("action");
    	sai.addAction("action");
    	sai.addInput("input");
    	sai.addInput("input");
    	sai.addInput("input");

    	test.setSai(sai);
    	
    	ArrayList<Contents> testArray = new ArrayList<Contents>();
    	testArray.add(test);
    	
    	AlphabetizedCustomsColumn column = new AlphabetizedCustomsColumn(testArray, Collections.<Contents>emptyList());
//    	trace.out(Arrays.toString(column.toArray()));
    	
    	assertEquals(12, (int) column.getNumColumns());
    	assertEquals(6, (int) column.getIndex("selection2"));
    	assertEquals(8, (int) column.getIndex("action1"));
    	assertEquals(11, (int) column.getIndex("input2"));
    }
    
    public void testSkill(){
    	test.resetFields();
    	test.addSkill(new SkillContent(9, null, "name", "category", null, null));
    	
    	ArrayList<Contents> testArray = new ArrayList<Contents>();
    	testArray.add(test);
    	
    	AlphabetizedCustomsColumn column = new AlphabetizedCustomsColumn(testArray, Collections.<Contents>emptyList());
//    	trace.out(Arrays.toString(column.toArray()));
    	
    	assertEquals(6, column.getNumColumns());
    	assertEquals(4, (int) column.getIndex("skill9 name"));
    	assertEquals(5, (int) column.getIndex("skill9 category"));
    	
    	assertNull(column.getIndex("skill9 probability"));
    	assertNull(column.getIndex("skill9 model_list"));
    	assertNull(column.getIndex("skill9 buggy"));
    }
    
    public void testMultipleSkills(){
    	test.resetFields();
    	new SkillContent(0, "probability", "name", "category", Collections.<String>emptyList(), true);
    	test.addSkill(new SkillContent(0, "probability", "name", "category", Collections.<String>emptyList(), true));
    	test.addSkill(new SkillContent(1, "probability", "name", "category", Collections.<String>emptyList(), true));
    	test.addSkill(new SkillContent(5, "probability", "name", "category", Collections.<String>emptyList(), true));
    	test.addSkill(new SkillContent(-1, "probability", "name", "category", Collections.<String>emptyList(), true));
    	
    	ArrayList<Contents> testArray = new ArrayList<Contents>();
    	testArray.add(test);
    	
    	AlphabetizedCustomsColumn column = new AlphabetizedCustomsColumn(testArray, Collections.<Contents>emptyList());
//    	trace.out(Arrays.toString(column.toArray()));
    	
    	assertEquals(4 + (5 * 4), column.getNumColumns());
    }
    
    public void testMultipleCustoms(){
    	test.resetFields();
    	test.addCustom(new CustomContent(0, "1", "value1"));
    	test.addCustom(new CustomContent(0, "5", "value2"));
    	test.addCustom(new CustomContent(0, "3", "value3"));
    	test.addCustom(new CustomContent(0, "9", "value4"));
    	
    	ArrayList<Contents> testArray = new ArrayList<Contents>();
    	testArray.add(test);
    	
    	AlphabetizedCustomsColumn column = new AlphabetizedCustomsColumn(testArray, Collections.<Contents>emptyList());
    	trace.out(Arrays.toString(column.toArray()));
    	
    	assertEquals(4 + 4, column.getNumColumns());
    	assertEquals(5, (int) column.getIndex("custom_field 3"));//the name should be in the title, and they should be sorted in order.
    	assertEquals(7, (int) column.getIndex("custom_field 9"));
    }
}
