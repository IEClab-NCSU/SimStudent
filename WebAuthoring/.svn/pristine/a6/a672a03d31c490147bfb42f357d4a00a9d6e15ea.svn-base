package rmconnective;

import aima.core.logic.propositional.kb.data.ConjunctionOfClauses;
import aima.core.logic.propositional.parsing.PLParser;
import aima.core.logic.propositional.parsing.ast.Sentence;
import aima.core.logic.propositional.visitors.ConvertToConjunctionOfClauses;
import aima.core.logic.propositional.visitors.ConvertToNNF;

public class Main {

	public static void main(String[] args){
		String s1 = "/\\";
		System.out.println("~~~   "+s1.replaceAll("/", "\\\\/"));
		String s2 = "\\/";
		System.out.println("~~~   "+s2.replaceAll("/", "\\\\/"));

//		boolean simplified = (s.matches("^.*~\\s*~.*$") == false);
//		
//		Sentence sen1 = PLParserWrapper.parse("~(a orr b)");
//		Sentence sen2 = PLParserWrapper.parse("~b    and (~a))");
//
//		ConjunctionOfClauses transformedSen1 = ConvertToConjunctionOfClauses.convert(sen1);
//		ConjunctionOfClauses transformedSen2 = ConvertToConjunctionOfClauses.convert(sen2);
//		
//		boolean isCNFequal = transformedSen1.equals(transformedSen2);
//		boolean isStructureEqual = sen1.getConnective().equals(sen2.getConnective());
//		boolean equivalent = isCNFequal & isStructureEqual;
//		System.out.println("~~~~~~~IsCNF: "+isCNFequal+"   isStructureEqual: "+isStructureEqual+"  "+" match: "+equivalent);
//
		
		//System.out.println("~~~~~~~~~~~IsSimplified:"+s+"  "+simplified);
//
//		String f = "~  ~ p | q";
//		f = f.replaceAll("~\\s*~", "" );
//		System.out.println("()  "+f);
//
//
//		String f1 = "~  ~ p | q";
//		String f2 = "~~p|q";
//
//	PLParser parser = new PLParser();
//	SentenceImpl sen1 = parser.parse(f1);
//	ConjunctionOfClauses transformedSen1 = ConvertToConjunctionOfClauses.convert(sen1);
//
//	
//	SentenceImpl sen2 = parser.parse(f2);
//	ConjunctionOfClauses transformedSen2 = ConvertToConjunctionOfClauses.convert(sen2);
//	
//	boolean equivalent = transformedSen1.equals(transformedSen2);
//	System.out.println("f1:"+transformedSen1.toString()+" \nf2: "+transformedSen2.toString()+" \n"+equivalent);
//	String line = "~p|q";
//	System.out.println("#####  "+line.matches("[~]*[A-Za-z]"));
//	System.out.println("#####  "+isConnectiveFree("~p|q"));
//	
//    Map<String,String[]> equiMap = new HashMap<String,String[]>();
//	equiMap.put("~p|q", new String[]{"~p|q","q|~p","(~p|q)","(q|~p)"});//for implication p=>q
//	String[] equivals = equiMap.get("~p|q");
//	System.out.println("****  "+ Arrays.asList(equivals).contains("~p|q") );
//	System.out.println("isnnf:  "+ isNNF("~~p|q") );
//	System.out.println("()  "+ parser.parse("~   ~(p)|q"));

	}

	private static boolean isConnectiveFree(String value) {
//		boolean hasNoConnective = !(value.contains(Constants.IMP_CONNECTIVE) |
//							    value.contains(Constants.BIIMP_CONNECTIVE) |
//							    value.contains(Constants.XOR_CONNECTIVE) |
//							    value.contains(Constants.NAND_CONNECTIVE));
//		return hasNoConnective;	
		if (value.matches("[~]?[~]?[A-Za-z][\\|][~]?[~]?[A-Za-z]")
				|
			value.matches("[(][~]?[~]?[A-Za-z][\\&][~]?[~]?[A-Za-z][)][|][(][~]?[~]?[A-Za-z][\\&][~]?[~]?[A-Za-z][)]"))
			
		{System.out.println("isconnfree:"+value.matches("[~]?[~]?[A-Za-z][|][~]?[~]?[A-Za-z]"));
			return true;}
		
		return false;
	}


private static boolean isNNF(String value) {
		PLParser parser = new PLParser();
		value = value.replace("and", "&");
		value = value.replace("or","|");
		Sentence sen = parser.parse(value);
		Sentence nnf = ConvertToNNF.convert(sen);
	
		ConjunctionOfClauses coc_sen = ConvertToConjunctionOfClauses.convert(sen);
		ConjunctionOfClauses coc_nnf = ConvertToConjunctionOfClauses.convert(nnf);

		boolean isNNF = coc_sen.equals(coc_nnf);
		return isNNF;
	}
//	private static boolean isNNF(String value) {
//		if (value.matches("[~]?[A-Za-z][V][~]?[A-Za-z]")
//				|
//			value.matches("[(][~]?[A-Za-z][\\^][~]?[A-Za-z][)][V][(][~]?[A-Za-z][\\^][~]?[A-Za-z][)]"))
//			return true;
//		return false;
//	}
//	
//	private static boolean isConnectiveFree(String value) {
//		if (value.matches("[~]?[~]?[A-Za-z][V][~]?[~]?[A-Za-z]")
//				|
//			value.matches("[(][~]?[~]?[A-Za-z][\\^][~]?[~]?[A-Za-z][)][V][(][~]?[~]?[A-Za-z][\\^][~]?[~]?[A-Za-z][)]"))
//			return true;
//		return false;
//	}
//
//	private static boolean isLiteral(String line) {
//		//return (line.matches("[~]?[A-Za-z]([<=>|=>|V|^|⊕][A-Za-z])?"));
//		return (line.matches("[~]?[A-Za-z]"));
//	}
}
