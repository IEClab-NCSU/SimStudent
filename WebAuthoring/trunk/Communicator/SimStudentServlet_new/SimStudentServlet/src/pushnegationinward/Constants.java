package pushnegationinward;

import java.util.HashMap;
import java.util.Map;

public class Constants {
	
	public static final String XOR = "⊕";
	public static final String IMP = "=>";
	public static final String BIIMP = "<=>";
	public static final String NAND = "↑";
	public static final String NOT = "~";
	public static final String AND = "&";
	public static final String OR = "|";
	public static final String[] VALID_OPERATORS = {NOT,AND,OR};
	public static final String[] VALID_CONNECTIVES = {AND,OR};
	
	static final Map<String,String> INTERFACE_NAME = new HashMap<String,String>() {{
		put("step1_field"," 1st field");
	    put("step2_field"," 2nd field");
	}};	

}
