//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/URLEncodedString.java
package edu.cmu.old_pact.cmu.toolagent;


// Public Methods:
// ===============
// public URLEncodedString ( ) 					; Constructors
// public URLEncodedString ( int max )			; 		- specify miximum number of fields
// public URLEncodedString ( String enCode )	; 		- specify encoded string
// public URLEncodedString ( String enCode , int max );	- specify both
// public void decode ( String enCode )  		; initialize and decode enCode
// public String getField ( String field )		; returns value of a field by its name
//													(null means field not found)
// public String getField ( int field )         ; returns value of a field by its location
//													(null means field not initialized)
// public int getFieldNum ( String field )      ; returns location of a field
//													(-1 means field not found)
// public int getNumOfFields ( ) 				; returns number of initialized fields
// public int addField ( String name , String value); adds or modifies field indexed by
//												; the 'name' with 'value', return location
//													(-1 means too many fields initialized)
// public int reset ( )							; resets all fiels and values and
//													returns the maximum number of fields
// public String encode ( )						; return URLEncodedString according to
//											    	current field specifications
public class URLEncodedString extends Object {

	private URLCoder urlC;

	static private int MaxFields;
	private String[][] DecodedFields;
	private String EncodedString = "";	
	private int NumFields = 0;

	public URLEncodedString (String enCode) {
	    int pos = 0, 
	    	new_pos = 0, 
	    	field = 0;
	    	
	   // DecodedFields = new String[50][2];
	   DecodedFields = new String[100][2];
	    //MaxFields = 50;
	    MaxFields = 100;
	    EncodedString = enCode;
	    urlC = new URLCoder();
	    this.decode();
		}
		
   
	private void decode ( ) {
	    int pos = 0, 
	    	new_pos = 0, 
	    	field = 0;
	    String new_string;
	    	
	    if (EncodedString.length() > 0) {
			while (((new_pos = EncodedString.indexOf ('&', pos)) >=0) && (field < MaxFields)) {
				DecodedFields[field][0] = EncodedString.substring(pos, new_pos);
				//System.out.println(field + ": " + DecodedFields[field][0]);
				pos = new_pos + 1;
				field = field + 1;
				}
			
			DecodedFields[field][0] = EncodedString.substring(pos, EncodedString.length());
			//System.out.println(field + "*:* " + DecodedFields[field][0]);
			field = field + 1;
			}
			
		NumFields = field;
		
		for (field = 0; field < NumFields; field++) {
			pos = DecodedFields[field][0].indexOf('=') + 1;
			DecodedFields[field][1] = DecodedFields[field][0].substring(pos, DecodedFields[field][0].length());
			//System.out.println(field + "$:$ " + DecodedFields[field][1]);
			DecodedFields[field][0] = DecodedFields[field][0].substring(0, pos - 1);
			//System.out.println(field + "%:% " + DecodedFields[field][0]);
			}
		}
	public String getField ( String field ) {
		int i = 0;
		
		while ((i < NumFields) && (field.equals(DecodedFields[i][0]) == false)) {i++;}
		
		if (i < NumFields) 
			return URLCoder.decode(DecodedFields[i][1]);
		else
			return null;
		}
  
   public String[] getFields(String field)  {
       int i = 0;
       int k = 0;
 
       String[] fieldArr = new String[10];
       for (int j=0; j<10; j++)  {
           fieldArr[j] = "";
       }
       while (i < NumFields)  {
		     if ((i < NumFields)  && (field.equals(DecodedFields[i][0])))  {
			      fieldArr[k] =  DecodedFields[i][1];
			      k = k+1;
			  }
			  i++;
		 }
		 String[] returnArr = new String[k];
		 for (int jj=0; jj<k; jj++)  {
		     returnArr[jj]= URLCoder.decode(fieldArr[jj]);
		 }
		 return returnArr;
   } 		

	public String getField ( int field ) {
		if (field < NumFields) 
			return URLCoder.decode(DecodedFields[field][1]);
		else
			return null;
		}
	public String getFieldName ( int field ) {
		if (field < NumFields)
			return URLCoder.decode(DecodedFields[field][0]);
		else
			return null;
		}		

	public int getFieldNum ( String field ) {
		int i = 0;
		
		while ((i < NumFields) && (field.equals(DecodedFields[i][0]) == false)) {i++;}
		
		if (i < NumFields) 
			return i;
		else
			return -1;
		}		

	public int getNumOfFields ( ) {
		return NumFields;
		}		

	public int reset ( ) {
		NumFields = 0;
		return MaxFields;
		}		

	public int addField ( String name , String value) {
		int field;
		
		if (getFieldNum (name) != -1)
			field = getFieldNum (name);
		else {
			if (NumFields < MaxFields)
				field = NumFields++;
			else
				field = -1;
			}
		
		if (field >= 0) {
			DecodedFields[field][0] = name;
			DecodedFields[field][1] = value;
			}
			
		return field;
		}		
	}