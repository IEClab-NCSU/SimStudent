package rmconnective;

import aima.core.logic.propositional.kb.data.ConjunctionOfClauses;
import aima.core.logic.propositional.parsing.PLParser;
import aima.core.logic.propositional.parsing.ast.Sentence;
import aima.core.logic.propositional.visitors.ConvertToConjunctionOfClauses;

public class PLParserWrapper {
	
	private static PLParser parser = new PLParser();
	
	public static Sentence parse(String value){
		try{
			return parser.parse(value);
		}catch(Exception e){
			return null;
		}
	}
	
	public static boolean match(String value1, String value2){
		Sentence sen1 = parse(value1);
		Sentence sen2 = parse(value2);

		ConjunctionOfClauses transformedSen1 = ConvertToConjunctionOfClauses.convert(sen1);
		ConjunctionOfClauses transformedSen2 = ConvertToConjunctionOfClauses.convert(sen2);
		
		boolean isCnfEqual = transformedSen1.equals(transformedSen2);
		boolean isStructureEqual = sen1.getConnective().equals(sen2.getConnective());
		boolean match = isCnfEqual & isStructureEqual;
		
		System.out.println("----");
		System.out.println("1: "+value1+"      2:"+value2);
		System.out.println(" match:"+match+"( "+"cnf: "+isCnfEqual+"   structure: "+isStructureEqual+"  "+")");
		System.out.println("----");
		
		sen1 = null; sen2 = null; transformedSen1 = null; transformedSen2 = null;
		return match;
	}
}
