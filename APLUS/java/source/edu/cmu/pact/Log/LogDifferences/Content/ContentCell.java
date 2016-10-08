package edu.cmu.pact.Log.LogDifferences.Content;

public class ContentCell {
	//=========================================================================
	//	Cell Builder Inner Class
	//=========================================================================

	public static class ContentCellBuilder{
		private String fieldType = null;
		private int index = -1;
		private String propertyName = null;
		private String content = null;
		private ColumnFormatter formatter = null;
		
		public ContentCellBuilder fieldType(String fieldType){
			this.fieldType = fieldType;
			return this;
		}
		public ContentCellBuilder index(int index){
			this.index = index;
			return this;
		}
		public ContentCellBuilder propertyName(String propertyName){
			this.propertyName = propertyName;
			return this;
		}
		public ContentCellBuilder content(String content){
			this.content = content;
			return this;
		}
		public ContentCellBuilder formatter(ColumnFormatter formatter){
			this.formatter = formatter;
			return this;
		}
		
		public ContentCell buildCell(){
			if(fieldType == null){ throw new IllegalStateException("Missing required field type"); }
			if(content == null){ throw new IllegalStateException("Missing required content"); }
			if(formatter == null){ throw new IllegalStateException("Missing required ColumnFormatter"); }
			
			return new ContentCell(fieldType, index, propertyName, content, formatter);
		}
	}
	
	//=========================================================================
	//	Fields
	//=========================================================================
	
	private final String fieldType;
	private final int index;
	private final String propertyName;
	private final String content;
	private final ColumnFormatter formatter;
	
	//=========================================================================
	//	Constructor
	//=========================================================================

	/** Use the {@link ContentCell.ContentCellBuilder} to create instances of this.
	 *  The builder handles error checking, but not this constructor.
	 *  Don't use the constructor.
	 */
	ContentCell(String fieldType, int index, String propertyName,
			String content, ColumnFormatter formatter) {
		this.fieldType = fieldType;
		this.index = index;
		this.propertyName = propertyName;
		this.content = content;
		this.formatter = formatter;
	}

	//=========================================================================
	//	Accessors
	//=========================================================================
	
	public String getFieldType() {
		return fieldType;
	}

	public int getIndex() {
		return index;
	}

	public String getContent() {
		return content;
	}

	public String getPropertyName() {
		return propertyName;
	}
	
	public String getColumnName(){
		return formatter.makeColumnName(fieldType, index, propertyName);
	}
}
