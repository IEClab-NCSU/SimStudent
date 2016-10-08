package pushnegationinward;



public class _Main {

	public static void main(String[] args){
	
		String value1 = "(~p V q)";		
		
		System.out.println(getAllButFirst(value1));	
		
		System.out.println(canMoveNotInward(value1));	
		
		System.out.println(isFormula(value1));	

		
	}
	
	private static boolean isFormula(String value) {
		value = value.replaceAll(" ", "");
		if (value.contains("[a-z]([V|A][a-z])?"))
			return true;
		else return false;
		
	}
	
	private static String getAllButFirst(String arg) {
		String value = arg.replaceAll(" ", "");
		if (arg.startsWith("~("))
		{
			
			return arg.substring(1,arg.length());
		}
		else
		{
			value = value.replaceAll("\\(", "");
			value = value.replaceAll("\\)", "");
			if (value.charAt(0) != '~')
				return value.substring(0,1);
			else if (value.charAt(1) != '~')
				return value.substring(0, 2);
			else if (value.charAt(2) != '~')
				return value.substring(0, 3);
			else
				return value.substring(0, 4);
		}
	}

	private static boolean canMoveNotInward(String value) {
		value = value.replaceAll(" ", "");
		return value.startsWith("~(");
	}

}
