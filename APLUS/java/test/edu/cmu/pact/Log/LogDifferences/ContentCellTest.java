package edu.cmu.pact.Log.LogDifferences;

import junit.framework.TestCase;
import edu.cmu.pact.Log.LogDifferences.Content.ColumnFormatter;
import edu.cmu.pact.Log.LogDifferences.Content.ContentCell;

public class ContentCellTest extends TestCase {
	//Normally, content cells are built using the ContentCellBuilder by Content objects.
	//They skip cells that don't contain the necessary information, 
	//so testing ContentCell directly isn't completely necessary.
	//Testing anyway for completeness.
	
	static class TestColumnFormatter implements ColumnFormatter{
		@Override
		public String makeColumnName(String fieldType, int index,
				String propertyName) {
			return fieldType+index+" "+propertyName;
		}
	}
	
	public void testCellConstructorFull(){
		ContentCell cell = new ContentCell.ContentCellBuilder()
		.fieldType("field")
		.index(0)
		.propertyName("property")
		.content("content")
		.formatter(new TestColumnFormatter())
		.buildCell();

		assertEquals("field0 property", cell.getColumnName());
		assertEquals("field", cell.getFieldType());
		assertEquals(0, cell.getIndex());
		assertEquals("property", cell.getPropertyName());
		assertEquals("content", cell.getContent());
	}
	
	public void testCellConstructorNoFieldType(){
		try{
			@SuppressWarnings("unused")
			ContentCell cell = new ContentCell.ContentCellBuilder()
			//.fieldType("field")
			.index(0)
			.propertyName("property")
			.content("content")
			.formatter(new TestColumnFormatter())
			.buildCell();
			
			fail("Did not generate an exception");
		}
		catch(IllegalStateException e){
			assertEquals("Missing required field type", e.getMessage());
		}
	}
	
	public void testCellConstructorNoIndex(){
		ContentCell cell = new ContentCell.ContentCellBuilder()
		.fieldType("field")
		//.index(0)
		.propertyName("property")
		.content("content")
		.formatter(new TestColumnFormatter())
		.buildCell();
		
		//-1 is the default value. Won't throw an exception, but it will be obvious something is wrong
		assertEquals("field-1 property", cell.getColumnName());
		assertEquals("content", cell.getContent());
	}
	
	public void testCellConstructorNoPropertyName(){
		ContentCell cell = new ContentCell.ContentCellBuilder()
		.fieldType("field")
		.index(0)
		//.propertyName("property")
		.content("content")
		.formatter(new TestColumnFormatter())
		.buildCell();
		
		assertEquals("field0 null", cell.getColumnName());//null won't throw an error when outputting to string
		assertEquals("content", cell.getContent());
	}
	
	public void testCellConstructorNoContent(){
		try{
			@SuppressWarnings("unused")
			ContentCell cell = new ContentCell.ContentCellBuilder()
			.fieldType("field")
			.index(0)
			.propertyName("property")
			//.content("content")
			.formatter(new TestColumnFormatter())
			.buildCell();
			
			fail("Did not generate an exception");
		}
		catch(IllegalStateException e){
			assertEquals("Missing required content", e.getMessage());
		}
	}
	
	public void testCellConstructorNoFormatter(){
		try{
			@SuppressWarnings("unused")
			ContentCell cell = new ContentCell.ContentCellBuilder()
			.fieldType("field")
			.index(0)
			.propertyName("property")
			.content("content")
			//.formatter(new TestColumnFormatter())
			.buildCell();
			
			fail("Did not generate an exception");
		}
		catch(IllegalStateException e){
			assertEquals("Missing required ColumnFormatter", e.getMessage());
		}
	}
}
