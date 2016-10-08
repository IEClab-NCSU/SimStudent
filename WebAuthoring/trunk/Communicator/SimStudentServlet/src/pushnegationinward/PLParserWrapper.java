package pushnegationinward;

import java.util.HashMap;
import java.util.Map;

import aima.core.logic.propositional.kb.data.ConjunctionOfClauses;
import aima.core.logic.propositional.parsing.PLParser;
import aima.core.logic.propositional.parsing.ast.ComplexSentence;
import aima.core.logic.propositional.parsing.ast.Connective;
import aima.core.logic.propositional.parsing.ast.Sentence;
import aima.core.logic.propositional.visitors.ConvertToConjunctionOfClauses;

public class PLParserWrapper {
	
	private static PLParser parser = new PLParser();
	
	public static Sentence parse(String value){
		/*value = value.replaceAll(Constants.AND, "&");
		value = value.replaceAll(Constants.OR, "|");*/
		System.out.println("@@@@@@@@@@@@@before parse: "+value);
		try{
			return parser.parse(value);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean isSyntaxValid(String value)
	{
		return (parse(value) != null);
	}
	public static boolean match(String value1, String value2){
		Sentence sen1 = parse(value1);
		Sentence sen2 = parse(value2);

		ConjunctionOfClauses transformedSen1 = ConvertToConjunctionOfClauses.convert(sen1);
		ConjunctionOfClauses transformedSen2 = ConvertToConjunctionOfClauses.convert(sen2);
		
		String[] tmp1 = sen1.toString().split(" ");
		String[] tmp2 = sen2.toString().split(" ");
		Map<String,Integer> sen1_literals = getLiteralsInParsedSen(tmp1);
		Map<String,Integer> sen2_literals = getLiteralsInParsedSen(tmp2);
				
		boolean isCnfEqual = transformedSen1.equals(transformedSen2);
		
		boolean isStructureEqual = isStructureEqual(sen1,sen2);
		boolean sameDoubleNotLiterals = isSameDoubleNotLiterals(sen1_literals,sen2_literals);

		boolean match = isCnfEqual & isStructureEqual & sameDoubleNotLiterals;
		System.out.println(" match:"+match+" ( "+"cnf: "+isCnfEqual+"   structure: "+isStructureEqual+
				"   sameNotNot: "+sameDoubleNotLiterals+" )");
		
		System.out.println("----");
		System.out.println("1: "+value1+"      2:"+value2);
		System.out.println(" match:"+match+"( "+"cnf: "+isCnfEqual+"   structure: "+isStructureEqual+"  "+"   notNots#: "+sameDoubleNotLiterals+")");
		System.out.println("----");
		
		sen1 = null; sen2 = null; transformedSen1 = null; transformedSen2 = null;
		tmp1 = null; tmp2 = null; sen1_literals = null; sen2_literals = null;
		return match;
	}
	
	private static boolean isStructureEqual(Sentence sen1, Sentence sen2) {
		if (sen1.getConnective() != null & sen2.getConnective() != null)
			return sen1.getConnective().equals(sen2.getConnective());
		else if (sen1.getConnective() == null & sen2.getConnective() == null)
			return true;
		else
			return false;	
	}

	public static boolean canMoveNotInward(String value)
	{
		Sentence sen = parse(value);
		boolean canMove = false;
	   
		if (sen.isNotSentence())
			if (sen.getSimplerSentence(0).isBinarySentence())
				canMove = true;

		else if (sen.isBinarySentence())
			if (canMoveNotInward(sen.getSimplerSentence(0).toString()) 
				| canMoveNotInward(sen.getSimplerSentence(1).toString()))
				canMove = true;

		sen = null;
		return canMove;
	}
	
	public static String getConjunction(String value1, String value2) {
	    return (new ComplexSentence(Connective.AND, new Sentence[] { parse(value1), parse(value2) })).toString();
	}
	
	public static String getDisjunction(String value1, String value2) {
	    return (new ComplexSentence(Connective.OR, new Sentence[] { parse(value1), parse(value2) })).toString();
	}
	
	public static String getLhs(String value) {
		Sentence sen = parse(value);
		String result = null;
		if (sen.isBinarySentence())
			result = sen.getSimplerSentence(0).toString();
		sen = null;
		return result;
	}
	

	public static String getRhs(String value) {
		Sentence sen = parse(value);
		String result = null;
		if (sen.isBinarySentence())
			result = sen.getSimplerSentence(1).toString();
		sen = null;
		return result;
	}
	
	public static String getNegatedRhs(String value) {
		Sentence sen = parse(value);
		String result = null;
		if (sen.isUnarySentence())
			if (sen.getSimplerSentence(0).isBinarySentence())
				result = new ComplexSentence(Connective.NOT,new Sentence[]
						{ sen.getSimplerSentence(0).getSimplerSentence(1) }).toString();
		sen = null;
		return result;
	}
	
	public static String getNegatedLhs(String value) {
		Sentence sen = parse(value);
		String result = null;
		if (sen.isUnarySentence())
			if (sen.getSimplerSentence(0).isBinarySentence())
				result = new ComplexSentence(Connective.NOT,new Sentence[]
						{ sen.getSimplerSentence(0).getSimplerSentence(0) }).toString();
		sen = null;
		return result;
	}
	
	private static Map<String, Integer> getLiteralsInParsedSen(String[] tmp) {
		Map<String,Integer> sen_literals = new HashMap<String,Integer>();
		for (String t: tmp)
		{
			if (t.trim().equals("") == false & t.trim().equals("&")==false &
					t.trim().equals("|") == false & t.trim().equals("=>")==false & t.trim().equals("<=>") == false){
				if (sen_literals.get(t)!= null)
					sen_literals.put(t, sen_literals.get(t)+1);
				else
					sen_literals.put(t,1);
			}
		}
		return sen_literals;
	}
	
	private static boolean isSameDoubleNotLiterals(
			Map<String, Integer> sen1_literals,
			Map<String, Integer> sen2_literals) {
		
		if (sen1_literals.size() != sen2_literals.size())
			return false;
		for (String l : sen1_literals.keySet())
		{
			if (sen2_literals.get(l) == null)
				return false;
			else if (sen2_literals.get(l) != sen1_literals.get(l))
				return false;
		}
		return true;
	}
	
	public static String negate(String value){
		return new ComplexSentence(Connective.NOT,new Sentence[] { parse(value) }).toString();
	}

	public static String getSentenceInsideNegation(String value) {
		return parse(value).getSimplerSentence(0).toString();
	}
	
	public static boolean isNotSentence(String value)
	{
		return parse(value).isNotSentence();
	}

	public static boolean isAndSentence(String value) {
		return parse(value).isAndSentence();
	}
	
	public static boolean isOrSentence(String value) {
		return parse(value).isOrSentence();
	}
	
	public static boolean isSimplified(String value) {
		return (parse(value).toString().matches("^.*~\\s*~.*$") == false);
	}
	
	public static String removeDoubleNegation(String value) {
		return parse(value).toString().replaceAll("~\\s*~", "");
	}

	public static String getDemorganizationAnd(String value) {
		Sentence sen = parse(value);
		Sentence leftNegated  = null, rightNegated = null;
		String result = null;
		if (sen.isBinarySentence())
		{
			leftNegated = new ComplexSentence(Connective.NOT,new Sentence[]
					{ sen.getSimplerSentence(0) });
			rightNegated = new ComplexSentence(Connective.NOT,new Sentence[]
					{ sen.getSimplerSentence(1) });
			result = new ComplexSentence(Connective.OR,new Sentence[]
					{ leftNegated,rightNegated }).toString();
		}

		sen = null; leftNegated  = null; rightNegated = null;
		result = result.replaceAll("&", Constants.AND);
		result = result.replaceAll("\\|", Constants.OR);
		return result;
	}
	
	public static String getDemorganizationOr(String value) {
		Sentence sen = parse(value);
		Sentence leftNegated  = null, rightNegated = null;
		String result = null;
		if (sen.isBinarySentence())
		{
			leftNegated = new ComplexSentence(Connective.NOT,new Sentence[]
					{ sen.getSimplerSentence(0) });
			rightNegated = new ComplexSentence(Connective.NOT,new Sentence[]
					{ sen.getSimplerSentence(1) });
			result = new ComplexSentence(Connective.AND,new Sentence[]
					{ leftNegated,rightNegated }).toString();
		}
		result = result.replaceAll("&", Constants.AND);
		result = result.replaceAll("\\|", Constants.OR);
		sen = null; leftNegated  = null; rightNegated = null;
		return result;
	}

	public static boolean isNegOnDisjunction(String value) {
		Sentence sen = parse(value);
		if (sen.isUnarySentence())
			if(sen.getSimplerSentence(0).isOrSentence())
				return true;
		return false;
	}
	
	public static boolean isNegOnConjunction(String value) {
		Sentence sen = parse(value);
		if (sen.isUnarySentence())
			if(sen.getSimplerSentence(0).isAndSentence())
				return true;
		return false;
	}
}
