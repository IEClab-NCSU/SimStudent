package edu.cmu.old_pact.dataconverter;

import java.util.Vector;

public class DataConverter {
	
	public static boolean getBooleanValue(String ident,Object obj) throws DataFormattingException{
	
		String s = obj.toString();
		if(s.equalsIgnoreCase("TRUE") || s.equalsIgnoreCase("T"))
			return true;
		else if(s.equalsIgnoreCase("FALSE")|| s.equalsIgnoreCase("F"))
			return false;
		else
			throw new DataFormattingException("Property "+ident+" must be of type Boolean");
	}
	public static int getIntValue(String ident,Object obj) throws DataFormattingException{
		try{
			String s = obj.toString();
			int b = (Integer.valueOf(s)).intValue();
			return b;
		} catch (NumberFormatException e){
			throw new DataFormattingException("Property "+ident+" must be of type Integer");
		}
	}
	public static float getFloatValue(String ident,Object obj) throws DataFormattingException{
		try{
			String s = obj.toString();
			float b = (Float.valueOf(s)).floatValue();
			return b;
		} catch (NumberFormatException e){
			throw new DataFormattingException("Property "+ident+" must be of type Float");
		}
	}
	public static double getDoubleValue(String ident,Object obj) throws DataFormattingException{
		try{
			String s = obj.toString();
			double b = (Double.valueOf(s)).doubleValue();
			return b;
		} catch (NumberFormatException e){
			throw new DataFormattingException("Property "+ident+" must be of type Double");
		}
	}
	public static Vector getListValue(String ident,Object obj) throws DataFormattingException{
		try{
			Vector b = (Vector)obj;
			int s = b.size();
			return b;
		} catch (ClassCastException e){
			throw new DataFormattingException("Property "+ident+" must be of type List");
		}
	}
	
	
}
