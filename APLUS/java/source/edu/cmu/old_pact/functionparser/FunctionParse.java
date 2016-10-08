package edu.cmu.old_pact.functionparser;
//Mike Piff, CICS, University of Sheffield (1997)
//M.Piff@shef.ac.uk


import java.util.StringTokenizer;
public class FunctionParse{
	private String st[],independentvariable,dependentvariable,
		firstderivative,firstderivativealias;
	private int cursor;

	public FunctionParse(String s){
		this(s,"x","y","z");
	}

	public FunctionParse(String s,String xstr,String ystr,String zstr){
		s = getExpression(s);
		independentvariable=xstr;
		dependentvariable=ystr;
		firstderivative="d"+dependentvariable+"d"+independentvariable;
		firstderivativealias=zstr;
		StringTokenizer sttmp;
		sttmp=new StringTokenizer(s,"E([])+-*/| ,^",true);
		int n=sttmp.countTokens();
		st=new String[n+1];
		int j=0;
		for (int i=0;i<n;i++){
			String tmp=sttmp.nextToken();
			if (!tmp.endsWith(" "))
				st[j++]=tmp;
		}
		st[j]=null;
	}
	
	public void clear(){
		st = null;
	}
	
	private String getExpression(String st) {
		st = st.toLowerCase();
		int ind = st.indexOf("x"), lastind = st.lastIndexOf("x") ;
		if(ind == -1)
			return st;
		while (ind <= lastind){
			if(ind > 0){
				char bef = st.charAt(ind-1);
				if(Character.isDigit(bef))
					st = st.substring(0, ind)+"*"+st.substring(ind);
			}
			ind = st.indexOf("x", ind+1);
			if(ind == -1)
				return st;
			lastind = st.lastIndexOf("x");
		}
		return st;
	}

	public double evaluate(double x,double y,double z){
		cursor=0;
		return readExpression(x,y,z);
	}

	public double evaluate(double x,double y) {
		return evaluate(x,y,Double.NaN);
	}

	public double evaluate(double x) {
		return evaluate(x,Double.NaN,Double.NaN);
	}

	public double evaluate() {
		return evaluate(Double.NaN,Double.NaN,Double.NaN);
	}


	private double convert(String s){
		try{
			Double d=new Double(s);
			return d.doubleValue();
		}
		catch (NumberFormatException e) {
			return Double.NaN;
		}
	}

	private boolean moreTokens(){
		if (cursor>=st.length) return false;
		return (st[cursor]!=null);
	}
	
	private double readExpression(double x,double y,double z){
		double tmp,tmp2;
		if (!moreTokens()) return 0.0;
		tmp=readTerm(x,y,z);
		while (moreTokens() &&((st[cursor].equals("+"))||(st[cursor].equals("-")))){
			if (st[cursor++].equals("+")) {
				tmp2=readTerm(x,y,z);
				tmp+=tmp2;
			} else {
				tmp2=readTerm(x,y,z);
				tmp-=tmp2;
			}
		}
		return tmp;
	}


	private double readTerm(double x,double y,double z){
		double tmp,tmp2;
		if (!moreTokens()) return 0.0;
		tmp=readEFactor(x,y,z);
		while (moreTokens()&&((st[cursor].equals("*"))||(st[cursor].equals("/")))){
			if (st[cursor++].equals("*")) {
				tmp2=readEFactor(x,y,z);
				tmp*=tmp2;
			} else {
				tmp2=readEFactor(x,y,z);
				tmp/=tmp2;
			}
		}
		return tmp;
	}

	private double readEFactor(double x,double y,double z){
		double tmp,tmp2;
		if (!moreTokens()) return 0.0;
		tmp=readFactor(x,y,z);
		while (moreTokens()&&((st[cursor].equals("^")||(st[cursor].equals("E"))))){
			if (st[cursor++].equals("^")){
				tmp2=readEFactor(x,y,z);
				tmp=pow(tmp,tmp2);
			} else {
				tmp2=readFactor(x,y,z);
				tmp*=pow(10,tmp2);
			}
		}
		return tmp;
	}


	private double readFactor(double x,double y,double z){
		double tmp,tmp2;
		if (!moreTokens()) return 0.0;
		String tkn=new String(st[cursor]);
		if (tkn.equals("(")) {
			++cursor;
			tmp=readExpression(x,y,z);
			if ((moreTokens())&&(st[cursor].equals(")"))) ++cursor;
			return tmp;
		} else if (tkn.equals("[")) {
			++cursor;
			tmp=readExpression(x,y,z);
			if ((moreTokens())&&(st[cursor].equals("]"))) ++cursor;
			return tmp;
		} else if (tkn.equals(independentvariable)){
			++cursor;
			return x;
		} else if (tkn.equals(dependentvariable)) {
			++cursor;
			return y;
		} else if (tkn.equals(firstderivative)||tkn.equals(firstderivativealias)) {
			++cursor;
			return z;
		} else if (tkn.equals("sin")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return Math.sin(tmp);
		} else if (tkn.equals("cos")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return Math.cos(tmp);
		} else if (tkn.equals("tan")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return Math.tan(tmp);
		} else if (tkn.equals("sec")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return 1.0d/Math.sin(tmp);
		} else if (tkn.equals("cosec")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return 1.0d/Math.cos(tmp);
		} else if (tkn.equals("tan")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return 1.0d/Math.tan(tmp);
		} else if (tkn.equals("exp")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return Math.exp(tmp);
		} else if (tkn.equals("sinh")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return sinh(tmp);
		} else if (tkn.equals("cosh")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return cosh(tmp);
		} else if (tkn.equals("tanh")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return tanh(tmp);
		} else if (tkn.equals("sech")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return 1.0d/sinh(tmp);
		} else if (tkn.equals("cosech")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return 1.0d/cosh(tmp);
		} else if (tkn.equals("coth")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return 1.0d/tanh(tmp);
		} else if (tkn.equals("log")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return Math.log(tmp);
		} else if (tkn.equals("abs")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return Math.abs(tmp);
		} else if (tkn.equals("asin")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return Math.asin(tmp);
		} else if (tkn.equals("acos")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return Math.acos(tmp);
		} else if (tkn.equals("atan")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return Math.atan(tmp);
		} else if (tkn.equals("asec")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return Math.asin(1.0d/tmp);
		} else if (tkn.equals("acosec")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return Math.acos(1.0d/tmp);
		} else if (tkn.equals("acot")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return Math.atan(1.0d/tmp);
		} else if (tkn.equals("asinh")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return asinh(tmp);
		} else if (tkn.equals("acosh")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return acosh(tmp);
		} else if (tkn.equals("atanh")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return atanh(tmp);
		} else if (tkn.equals("asech")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return asinh(1.0d/tmp);
		} else if (tkn.equals("acosech")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return acosh(1.0d/tmp);
		} else if (tkn.equals("acoth")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return atanh(1.0d/tmp);
		} else if (tkn.equals("ceil")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return Math.ceil(tmp);
		} else if (tkn.equals("floor")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return Math.floor(tmp);
		} else if (tkn.equals("delta")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return delta(tmp);
		} else if (tkn.equals("|")) {
			++cursor;
			tmp=readExpression(x,y,z);
			if (st[cursor].equals("|")) ++cursor;
			return Math.abs(tmp);
		} else if (tkn.equals("round")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return Math.round(tmp);
		} else if (tkn.equals("sqrt")) {
			++cursor;
			tmp=readTerm(x,y,z);
			return Math.sqrt(tmp);
		} else if (tkn.equals("random")) {
			++cursor;
			if (st[cursor].equals("(")) {
				++cursor;
				tmp=readExpression(x,y,z);
				if (st[cursor].equals(")")) ++cursor;
			}
			return Math.random();
		} else if (tkn.equals("-")) {
			++cursor;
			tmp=readFactor(x,y,z);
			return -tmp;
		} else if (tkn.equals("max")) {
			++cursor;
			if (st[cursor].equals("(")) ++cursor;
			tmp=readExpression(x,y,z);
			if (st[cursor].equals(",")) ++cursor;
			tmp2=readExpression(x,y,z);
			if (st[cursor].equals(")")) ++cursor;
			return Math.max(tmp,tmp2);
		} else if (tkn.equals("min")) {
			++cursor;
			if (st[cursor].equals("(")) ++cursor;
			tmp=readExpression(x,y,z);
			if (st[cursor].equals(",")) ++cursor;
			tmp2=readExpression(x,y,z);
			if (st[cursor].equals(")")) ++cursor;
			return Math.min(tmp,tmp2);
		} else if (tkn.equals("pow")) {
			++cursor;
			if (st[cursor].equals("(")) ++cursor;
			tmp=readExpression(x,y,z);
			if (st[cursor].equals(",")) ++cursor;
			tmp2=readExpression(x,y,z);
			if (st[cursor].equals(")")) ++cursor;
			return pow(tmp,tmp2);
		} else if (tkn.equals("e")) {
			++cursor;
			return Math.E;
		} else if (tkn.equals("pi")) {
			++cursor;
			return Math.PI;
		} else {
			return convert(st[cursor++]);
		}
	}
	

	private double pow(double x,double y){
		return Math.pow(x,y);
	}


	private double sinh(double x){
		double tmp=Math.exp(x);
		return 0.5*(tmp-1/tmp);
	}


	private double asinh(double x){
		return Math.log(x+Math.sqrt(x*x+1));
	}
	

	private double cosh(double x){
		double tmp=Math.exp(x);
		return 0.5*(tmp+1/tmp);
	}

	private double acosh(double x){
		return Math.log(x+Math.sqrt(x*x-1));
	}



	private double tanh(double x){
		double tmp=Math.exp(2*x);
		return (tmp-1)/(tmp+1);
	}

	private double atanh(double x){
		return 0.5*Math.log((1+x)/(1-x));
	}

	private double delta(double x){
		return x>=0?1:0;
	}

}


