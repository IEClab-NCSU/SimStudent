package digt_1_3;

import java.util.HashMap;
import java.util.Map;

public class Constants {
	
	//syntax in propositional logic
	public static final String PL_OR = "p | q";
	public static final String PL_AND = "p & q";
	public static final String PL_IMP = "p => q";
	public static final String PL_BIIMP = "p <=> q";

	public static final String[] PL_OPERATORS = {PL_OR,PL_AND,PL_IMP,PL_BIIMP};
	static final Map<String,String> INTERFACE_NAME = new HashMap<String,String>() {{
	    put("table_R1C0","2nd row");
	    put("table_R2C0","3rd row");
	    put("table_R3C0","4th row");
	    put("table_R4C0","5th row");
	}};				
}
