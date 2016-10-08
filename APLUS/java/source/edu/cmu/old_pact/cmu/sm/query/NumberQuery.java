package edu.cmu.old_pact.cmu.sm.query;

//a NumberQuery is just a wrapper around a number

public class NumberQuery extends PrimitiveValueQuery {
	Number num;
	private static double epsilon = .00001; //numbers below this considered to be 0
	
	public NumberQuery(Number numb) {
		num = numb;
	}
	
	public Queryable getProperty(String prop) throws NoSuchFieldException {
		if (prop.equalsIgnoreCase("isNegative")) {
			boolean neg = num.doubleValue() < 0;
			return new BooleanQuery(neg);
		}
		else if (prop.equalsIgnoreCase("isPositive")) {
			boolean pos = num.doubleValue() > 0;
			return new BooleanQuery(pos);
		}
		else if (prop.equalsIgnoreCase("isZero")) {
			boolean zer = num.doubleValue() < epsilon;
			return new BooleanQuery(zer);
		}
		else if (prop.equalsIgnoreCase("numberType")) {
			String type;
			double val = num.floatValue();
			int intPart = (int)Math.floor(val);
			double rem = val-intPart;
			if (rem < epsilon)
				type = "Integer";
			else
				type = "Decimal";
			return new StringQuery(type);
		}
		else if (prop.equalsIgnoreCase("absolute value"))
			return new NumberQuery(new Double (Math.abs(num.doubleValue())));
		else
			throw new NoSuchFieldException("No field "+prop+" in NumberQuery");
	}
	
	public Number getNumberValue() {
		return num;
	}
	
	public String getStringValue() {
		return num.toString();
	}
}