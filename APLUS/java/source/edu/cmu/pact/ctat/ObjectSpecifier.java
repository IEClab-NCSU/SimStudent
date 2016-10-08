package edu.cmu.pact.ctat;
import java.util.Vector;

public class ObjectSpecifier{
	public Vector speclist;
	public String type; // for optimized access 
	
	public ObjectSpecifier(){
		speclist = new Vector();
	}
	
	public ObjectSpecifier(String type){
		this();
		init(type);
	}
	
	public void init(String type){
		speclist.addElement(type);
		this.type = type;
	}

	public void contain(String Container,int position){
		String tempint = new String(""+position);
		speclist.addElement(tempint);
		speclist.addElement(Container);
	}
	
	public void contain(String Container,String name){
		speclist.addElement(name);
		speclist.addElement(Container);
	}
	
	public void contain(ObjectSpecifier Container,int position){
		speclist.addElement(""+position);
		try{
			for(int i=0;i<Container.speclist.size();i++) speclist.addElement(Container.speclist.elementAt(i));
		} catch (ArrayIndexOutOfBoundsException a){};
	}
	
	public void contain(ObjectSpecifier Container,String Name){
		speclist.addElement(Name);
		try{
			for(int i=0;i<Container.speclist.size();i++) speclist.addElement(Container.speclist.elementAt(i));
		} catch (ArrayIndexOutOfBoundsException a){};
	}
	
	public boolean match(ObjectSpecifier target){
		String tempString;
		boolean returnee;
		tempString = target.toString();
		returnee = tempString.equals(this.toString());		
		return returnee;
	}
	
	public boolean sibling(ObjectSpecifier prospective){
	//A relatively simple method.  Compares everything in the list and returns false true
	//if the only iffering element is the second element.  In an object hierarchy, this would 
	//indicate that the objects are siblings - same parents, different order.
		int i;
		boolean returnee = true;
		try{
			for(i=0;i<speclist.size();i++){
				String tString = (String) speclist.elementAt(i);
				String t2String = (String) prospective.speclist.elementAt(i);
				if(!tString.equals(t2String))
					if(i!=1) returnee = false;
			}
		} catch (ArrayIndexOutOfBoundsException a){};
		return returnee;
	}
	
	public String toString(){
		int i;
		String returnee=new String();
		char tempc;
		try{
			for(i=0;i<speclist.size();i++){
				String tString = (String) speclist.elementAt(i)+":";
				if(i%2 ==1){
					tempc = tString.charAt(0);
					if((tempc>='0') && (tempc<='9')) returnee = returnee +"I:"+tString;
					else returnee = returnee + "S:"+tString;
					returnee = returnee;
				} else returnee = returnee + tString;
			}
		} catch (ArrayIndexOutOfBoundsException a){};
		return returnee.substring(0,returnee.length()-1);
	}
	
	public void deleteSpecifier(){
		speclist = null;
		type = null;
	}
}