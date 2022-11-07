package edu.cmu.old_pact.cmu.sm;

import java.util.Vector;

/*this class explicitly represents expressions like -x as distinct from -1*x.*/

/*this class is used to represent negated expressions that should
  print out like -x instead of like -1*x.  The only real difference
  other than this display bit is that there is no further simplification to be done */

public class NegatedExpression extends TermExpression{
	public NegatedExpression(Expression ex){
		super(new NumberExpression(-1),ex);
		/*if(ex instanceof NumericExpression){
		  (new Exception()).printStackTrace();
		  }*/
	}

	public static Expression negate(Expression ex){
		return negate(ex,false);
	}

	public static Expression softNegate(Expression ex){
		return negate(ex,true);
	}

	public static TermExpression makeNegatedTerms(Vector terms){
		TermExpression ret = new TermExpression();
		for(int i=0;i<terms.size();i++){
			if(((Expression)terms.elementAt(i)).isNegOne() &&
			   (i < terms.size()-1)){
				i++;
				ret = ret.insert(new NegatedExpression((Expression)terms.elementAt(i)));
			}
			else{
				ret = ret.insert((Expression)terms.elementAt(i));
			}
		}

		if((ret.numSubTerms() == 1) &&
		   (ret.getTerm(0) instanceof NegatedExpression)){
			return (TermExpression)ret.getTerm(0);
		}
		else{
			return ret;
		}
	}

	public static Expression negate(Expression ex,boolean soft){
		if(ex instanceof NumericExpression &&
		   !ex.exactEqual(new NumberExpression(0))){
			if(!soft || !ex.isNegative()){
				return ex.negate();
			}
			else{
				return new NegatedExpression(ex);
			}
		}
		else if(!soft &&
				ex instanceof NegatedExpression){
			return ((TermExpression)ex).getTerm(1);
		}
		else if(ex instanceof TermExpression &&
			((TermExpression)ex).numSubTerms() > 0 &&
		   !(ex instanceof NegatedExpression)){
			TermExpression tex = (TermExpression)ex;
			TermExpression ret = new TermExpression();
			if(soft){
				ret = ret.insert(tex.getTerm(0).softNegate());
			}
			else{
				ret = ret.insert(tex.getTerm(0).negate());
			}
			for(int i=1;i<tex.numSubTerms();i++){
				ret = ret.insert(tex.getTerm(i));
			}
			return ret;
		}
		else{
			return new NegatedExpression(ex);
		}
	}

	protected Expression buildFromComponents(Vector components){
		if((components.size() == 2) &&
		   ((Expression)components.elementAt(0)).isNegOne()){
			return new NegatedExpression((Expression)components.elementAt(1));
		}
		else{
			return super.buildFromComponents(components);
		}
	}

	protected Expression buildFromComponents(ExpressionArray components){
		if((components.size() == 2) &&
		   (components.expressionAt(0)).isNegOne()){
			return new NegatedExpression(components.expressionAt(1));
		}
		else{
			return super.buildFromComponents(components);
		}
	}

	protected TermExpression uncleanBuildFromComponents(Vector components){
		if((components.size() == 2) &&
		   ((Expression)components.elementAt(0)).isNegOne()){
			return new NegatedExpression((Expression)components.elementAt(1));
		}
		else{
			return super.uncleanBuildFromComponents(components);
		}
	}

	protected TermExpression uncleanBuildFromComponents(Expression components[],int numComp){
		if(numComp == 2 && components[0].isNegOne()){
			return new NegatedExpression(components[1]);
		}
		else{
			return super.uncleanBuildFromComponents(components,numComp);
		}
	}

	public TermExpression insert(Expression ex){
		/*trace.out("NE.i: " + debugForm() + "; " + ex.debugForm());
		  return super.insert(ex);*/
		return new TermExpression(this,ex);
	}

	public String toASCII(String openParen,String closeParen){
		//trace.out("NE.tA: " + debugForm());
		//(new Exception()).printStackTrace();
		StringBuffer ret = new StringBuffer(64);
		boolean finishEnclose = false;
		ret.append("-");
		if (getTerm(1) instanceof PolyExpression ||
			getTerm(1) instanceof TermExpression ||
			(getTerm(1).isNegative() && !(getTerm(1) instanceof FencedExpression))){
			ret.append(openParen);
			finishEnclose = true;
		}
		ret.append(getTerm(1).toASCII(openParen,closeParen));
		if(finishEnclose){
			ret.append(closeParen);
		}

		//trace.out("       " + ret);
		return ret.toString();
	}

	public String toMathML(){
		StringBuffer ret = new StringBuffer(64);
		boolean finishEnclose = false;
		ret.append("<mrow><mo form=prefix>-</mo>");
		if(getTerm(1) instanceof PolyExpression ||
		   getTerm(1).isNegative()){
			ret.append("<mfenced>");
			finishEnclose = true;
		}
		ret.append(getTerm(1).toMathML());
		if(finishEnclose){
			ret.append("</mfenced>");
		}
		ret.append("</mrow>");
		return addMathMLAttributes(ret.toString());
	}

	public Expression sortTermWhole(){
		return this;
	}

	public Expression negate(){
		return getTerm(1);
	}

	/*public Expression multiplyThroughWhole(){
	  return this;
	  }

	  public boolean canMultiplyThroughWhole(){
	  return false;
	  }*/

	public String debugForm(){
		return "[Negated: " + getTerm(1).debugForm() + "]";
	}
}
