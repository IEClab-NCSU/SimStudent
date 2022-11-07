package edu.cmu.old_pact.cmu.solver.ruleset;


import java.util.Hashtable;

import edu.cmu.old_pact.cmu.sm.query.Queryable;

//The Test class has a static hashtable which can be used to cache results of tests
//We use the string representation of the test as the key, rather than the test itself,
//since this allows us to create different test structures which represent the same thing
//

public abstract class Test {
	protected String[] propertyString;
	protected boolean useCache = true;
	private static Hashtable testCache = new Hashtable(100);
	
	public abstract boolean passes(Queryable info);
	
	public static void clearHash() {
		testCache.clear();
	}
	
	//passes using a second argument uses the cache, if the test so specifies
	//The second argument is ignored -- its only used to select this version of the
	//method
	public boolean passes(Queryable info,boolean cacheVersion) {
		if (useCache) {
			/*we can't cache based on the string rep of the
              equation/expression, because its entire structure is not
              reflected*/
			Key key = new Key(toString() + info.hashCode());
//trace.out("in TEST cache test "+key);
			Object cacheValue = testCache.get(key);
			if (cacheValue != null) {
//trace.out("in TEST cache hit "+key);
				return ((Boolean)cacheValue).booleanValue();
			}
			else {
				boolean result = passes(info);
				testCache.put(key,new Boolean(result));
				return result;
			}
		}
		else 
			return passes(info);		
	}

	protected String ofString(String[] s){
		StringBuffer ret = new StringBuffer(64);
		for(int i=0;i<s.length;i++){
			ret.append(s[i]);
			if(i != s.length-1){
				ret.append(" of ");
			}
		}
		return ret.toString();
	}
}

