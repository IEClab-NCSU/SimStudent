//The Range class defines the beginning and end of an array of ObjectProxy descriptions.

package edu.cmu.old_pact.dormin;

import java.util.Vector;

public class Range{
	private ObjectProxy parent;
	private String rangeType;
	private int[] start, finish;
	private Vector startEndPairs;
	private String parentDesc;

	public  Range(ObjectProxy parent,String type,Vector startEndPairs){
		this.startEndPairs = startEndPairs;
		this.parent = parent;
		rangeType = type;
		parentDesc = parent.toString();
	}
	
	public  Range(String parentDesc,String type,Vector startEndPairs){
		this.startEndPairs = startEndPairs;
		rangeType = type;
		this.parentDesc = parentDesc;
	}
	
	public String toString(){
		//String returnee = startObject.toString()+","+endObject.toString();
		MessageObject mo = new MessageObject("Internal");
		String returnee = "O:"+parentDesc
							+";"+"S:"+rangeType.length()+":"+rangeType
							+";"+mo.stringFromObject('L',startEndPairs);
		return returnee;
	}
	
	public int[] getIntStart() {
		return getIntVector(0);
	}
	
	
	public int[] getIntFinish(){
		return getIntVector(1);
	}
	
	public ObjectProxy getParent(){
		return parent;
	}
	
	public Vector getStartEndPairs(){
		return startEndPairs;
	}
	
	private int[] getIntVector(int start){
		int s = startEndPairs.size()/2;
		int[] toret = new int[s];
		int pos = 0;
		for(int i=start; i<s;i++){
			Integer in = (Integer)startEndPairs.elementAt(i);
			toret[pos] = in.intValue();
			pos++;
			i++;
		}
		return toret;
	}
	
	public String getRangeType() {
		return rangeType;
	}	
	
}