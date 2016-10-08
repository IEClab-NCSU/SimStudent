package edu.cmu.pact.miss.PeerLearning.GameShow;

import java.net.URL;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class GameShowUtilities {

	private static char[] variables = {'a','b','c','g','h','j','k','m','n','p','q','r','s','t','u','v','w','x','y','z'};
	private static String[] patterns = {"AV=B","-AV=B","AV=-B","-AV=-B","AV+B=D","-AV+B=D","AV-B=D","-AV-B=D","AV+B=-D",
			"-AV+B=-D","B+AV=D","B-AV=D","-B+AV=-D","-B-AV=D","AV=CV","-AV=CV","AV+B=CV","AV-B=CV","-AV+B=-CV",
			"B-AV=CV","B+AV=-CV","AV=CV+B","AV=B-CV","AV=B+CV","AV+B=CV+D","AV+B=CV-D","AV+B=-CV+D","AV+B=-CV-D",
			"AV-B=CV+D","-AV+B=CV+D","-AV-B=CV+D","-AV+B=-CV-D","(AV+B)/C=D","(AV-B)/C=D","V/A=B",
			"-V/A=B","V/A=-B","AV+B+CV=D","AV-B-CV=D","(B-AV)/C=D",
			"(B+AV)/C=D","-V=B","V+B=AV","V-B=AV","V-B=-AV","V-B=D","B-V=D","V+B=D","B+V=D","B=V+D","B=V-D",
			"AV+B/C=D","AV-B/C=D","AV=B/C","B/A-CV=D","B/V=D"
	
	};
	
	/*
	 * Generate a problem based on any of the allowed patterns of equations and variables
	 */
	public static String generate()
	{
		//Choose a pattern randomly
		String problem = patterns[(int)(Math.random()*patterns.length)];

		return generate(problem);
	}
	
	public static boolean isDegenerateProblem=false;
	
	public static String generate(String problem)
	{	
		if (problem.contains("N"))
			 problem = GameShowUtilities.determinePattern(problem);
		
		GameShowUtilities.isDegenerateProblem=false;
		
		//A & C are coefficients, B & D are constants.  Coefficients can be 2-11, Constants can be 1-10
		//randomly chosen
		int A = (int) (Math.random()*10)+3;
		int B = (int) (Math.random()*10)+2;
		int C = (int) (Math.random()*10)+3;
		int D = (int) (Math.random()*10)+2;
		
		//Coefficients and constants cannot be equal to one another, increment one if randomly chosen equal
		if(A==C) C++;
		if(B==D) D++;
		
		while (A-C==1 || C-A==1){
			 A = (int) (Math.random()*10)+3;
			 B = (int) (Math.random()*10)+2;
			 C = (int) (Math.random()*10)+3;
			 D = (int) (Math.random()*10)+2;
			
			//Coefficients and constants cannot be equal to one another, increment one if randomly chosen equal
			if(A==C) C++;
			if(B==D) D++;

		}
		
		
		
		//Choose a variable randomly
		char var = variables[(int)(Math.random()*variables.length)];
		
		//Replace each of the pieces in the pattern with the randomly decided part
		//Not all patterns will have all pieces, but the ones which are present are used
		problem = problem.replaceAll("A", ""+A);
		problem = problem.replaceAll("B", ""+B);
		problem = problem.replaceAll("C", ""+C);
		problem = problem.replaceAll("D", ""+D);
		problem = problem.replaceAll("V", ""+var);
			
			
		return problem;
	}
	
	public static String[] generateExamples()
	{
		String[] examples = patterns.clone();
		
		for(int i=0;i<examples.length;i++)
		{
			examples[i] = generate(examples[i]);
		}
		
		return examples;
	}
	
	
	public static String determinePattern(String problem)
	{
		String patt = "";
		String current = "";
		boolean hasA = false;
		boolean hasB = false;
		boolean hasC = false;
		boolean hasD = false;
		String a = "a";
		String b = "b";
		String c = "c";
		String d = "d";
		
		int numberToReplace=(int) (Math.random()*10)+2;
		problem=problem.replaceAll("N", numberToReplace+"");
		
		for(int i=0;i<problem.length();i++)
		{
			boolean isNumber=(((Character) problem.charAt(i)).compareTo('N')==0)?true:false;
			
			if(!Character.isLetterOrDigit(problem.charAt(i)))
			{
				
				if(current.length() > 0)
				{
				
					if(patt.endsWith("/") && !hasA)
					{
						hasA = true;
						patt += "A";
						a = current;
					}
					else if(patt.endsWith("/") && !hasC)
					{
						hasC = true;
						patt += "C";
						c = current;
					}
					else if(!hasB)
					{
						hasB = true;
						patt += "B";
						b = current;
					}
					else if(!hasD)
					{
						hasD = true;
						patt += "D";
						d = current;
					}
					else if(b.equals(current))
					{
						patt += "B";
					}
					else if(d.equals(current))
					{
						patt += "D";
					}
					else if(a.equals(current))
					{
						patt += "A";
					}
					else if(c.equals(current))
					{
						patt += "C";
					}
					else if(!hasA)
					{
						hasA = true;
						patt += "A";
						a = current;
					}
					else if(!hasC)
					{
						hasC = true;
						patt += "C";
						c = current;
					}
					else
					{
						patt += current;
					}
				}
				patt += problem.charAt(i);
			
				current = "";
			}
			else if(Character.isLetter(problem.charAt(i)))
			{

				if(current.length() > 0)
				{
					if(!hasA)
					{
						hasA = true;
						patt += "A";
						a = current;
					}
					else if(!hasC)
					{
						hasC = true;
						patt += "C";
						c = current;
					}
					else if(a.equals(current))
					{
						patt += "A";
					}
					else if(c.equals(current))
					{
						patt += "C";
					}
					else if(b.equals(current))
					{
						patt += "B";
					}
					else if(d.equals(current))
					{
						patt += "D";
					}
					else if(!hasB)
					{
						hasB = true;
						patt += "B";
						b = current;
					}
					else if(!hasD)
					{
						hasD = true;
						patt += "D";
						d = current;
					}
					else
					{
						patt += current;
					}
				}
				patt += "V";
				current = "";
			}
			else
			{
				current += problem.charAt(i);
			}
		}
		

		if(current.length() > 0)
		{
			if(patt.endsWith("/") && !hasA)
			{
				hasA = true;
				patt += "A";
				a = current;
			}
			else if(patt.endsWith("/") && !hasC)
			{
				hasC = true;
				patt += "C";
				c = current;
			}
			else if(!hasB)
			{
				patt += "B";
			}
			else if(!hasD)
			{
				patt += "D";
			}
			else if(b.equals(current))
			{
				patt += "B";
			}
			else if(d.equals(current))
			{
				patt += "D";
			}
			else if(a.equals(current))
			{
				patt += "A";
			}
			else if(c.equals(current))
			{
				patt += "C";
			}
			else if(!hasA)
			{
				patt += "A";
			}
			else if(!hasC)
			{
				patt += "C";
			}
			else
			{
				patt += current;
			}
		}
		
		return patt;
	}
	
	/*
	 * Test if a given solution is the solution to an equation by plugging it back into the equation
	 * The solution also must be of the form V=Answer_w_no_V or Answer_w_no_V=V, eg x=5 or 5=x, but not x=2x+5
	 * and not -x=5
	 */
	public static boolean isSolution(String equation, String solution)
	{
		//split solution into sides
		String[] solutionSides = solution.split("=");
		//must have only one =
		if(solutionSides.length != 2)
			return false;
		//must have something on both sides
		if(solutionSides[0].length() == 0 || solutionSides[1].length() == 0)
			return false;
		//must have at least one side with just single character
		if(solutionSides[0].length() > 1 && solutionSides[1].length() > 1)
			return false;
		//cannot have + or * and - can only be at beginning, only one /
		if(solutionSides[0].contains("+") || solutionSides[1].contains("+"))
			return false;
		if(solutionSides[0].contains("*") || solutionSides[1].contains("*"))
			return false;
		if(solutionSides[0].substring(1).contains("-") || solutionSides[1].substring(1).contains("-"))
			return false;
		if(solutionSides[0].split("/").length > 2 || solutionSides[1].split("/").length > 2)
			return false;
		String var = "";
		String solValue = "";
		//determine if one side of solution is just variable
		if(solutionSides[0].length() == 1 && Character.isLetter(solutionSides[0].charAt(0)))
		{
			var = solutionSides[0];
			//check other side for that variable
			if(solutionSides[1].contains(var))
				return false;
			solValue = solutionSides[1];
		}
		else if(solutionSides[1].length() == 1 && Character.isLetter(solutionSides[1].charAt(0)))
		{
			var = solutionSides[1];
			//check other side for that variable
			if(solutionSides[0].contains(var))
				return false;
			solValue = solutionSides[0];
		}
		else
			return false;
		//split equation into two sides
		String[] equationSides = equation.split("=");
		if(equationSides.length != 2)
			return false;
		//plug solution into each side
		String lhs = equationSides[0].replaceAll(var, "("+solValue+")");
		String rhs = equationSides[1].replaceAll(var, "("+solValue+")");
		//evaluate each side
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("JavaScript");
		String lhsEval = null;
		String rhsEval = null;
		try {
			lhsEval = ""+engine.eval(insertMultiplySymbols(lhs));
			rhsEval = ""+engine.eval(insertMultiplySymbols(rhs));
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		//check if evaluations are the same
		double lhsVal = Double.parseDouble(lhsEval);
		double rhsVal = Double.parseDouble(rhsEval);
		//if(lhsEval != null && rhsEval != null && lhsEval.equals(rhsEval))
		if(Math.abs(lhsVal - rhsVal) < .00001)
		{
			return true;
		}
		return false;
	}
	
	/*
	 * Put multiplication symbols in place of implicit multiplication by parentheses
	 * eg 3(5) becomes 3*(5)
	 */
	public static String insertMultiplySymbols(String equation)
	{
		String[] eqParts = equation.split("\\(");
		String insertedEq = eqParts[0];
		for(int i =1;i<eqParts.length;i++)
		{
			if(insertedEq.length() == 0)
				insertedEq = "("+eqParts[i];
			else if(Character.isDigit(insertedEq.charAt(insertedEq.length()-1)))
				insertedEq += "*("+eqParts[i];
			else
				insertedEq += "("+eqParts[i];
		}
		return insertedEq;
	}
	
	/*
	 * Replace all instances of $1 in message with name
	 */
	public static String replacePiece(String message, String name)
	{
		while(message.contains("$1"))
			message = message.replace("$1", name);
		return message;
	}


	/*
	 * Replace all instances of $1 in message with name and all instance of $2 with name2
	 */
	public static String replaceTwoPieces(String message, String name, String name2)
	{
		while(message.contains("$1"))
			message = message.replace("$1", name);
		while(message.contains("$2"))
			message = message.replace("$2", name2);
		return message;
	}
	
	/** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon createImageIcon(String path) {
    	String file = "/edu/cmu/pact/miss/PeerLearning"+"/"+path;
    	URL url = GameShowUtilities.class.getResource(file);
    	
    	return new ImageIcon(url);
    }
    

	/** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon createImageIcon(String path, Object createFor) {
    	String file = "/edu/cmu/pact/miss/PeerLearning"+"/"+path;
    	URL url = createFor.getClass().getResource(file);
    	
    	return new ImageIcon(url);
    }
}
