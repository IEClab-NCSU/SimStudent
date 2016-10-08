/**
 * Created: Aug 25, 2014 8:52:16 PM
 * @author mazda
 * 
 */
package SimStudent2.LearningComponents;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import SimStudent2.ProductionSystem.UserDefJessSymbol;

/**
 * @author mazda
 *
 */
@SuppressWarnings("serial")
public class J48Instances extends Instances {
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	// Used for attribute values
	static final int TRUE_VALUE = 1;
	static final int FALSE_VALUE = -1;
	static final int NULL_VALUE = 0;
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructors
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * @param ruleName
	 * @param attributes
	 * @param i
	 */
	public J48Instances(String ruleName, FastVector attributes) {
		super(ruleName, attributes, 0);
		setClassIndex(numAttributes() - 1);
		
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * @param example
	 */
	public void addInstance(Example example, Vector<UserDefJessSymbol>featurePredicates) {
		
		int numAttr = numAttributes();
		double[] tmpAttrVals = new double[numAttr];
		int i = 0;
		
		ArrayList<String> foaValues = example.getFoaValues();

		// FoA value
		//
		String foaStr = "";
		for (int k = 0; k < foaValues.size(); k++) {
			String foa = foaValues.get(k);
			foaStr += foa + (k < foaValues.size()-1 ? ", " : "");
		}
		tmpAttrVals[i] = attribute(i).addStringValue(foaStr);
		i++;
				
		// Feature Predicates
		// 
		@SuppressWarnings("unchecked")
		Enumeration<Attribute> attributes = enumerateAttributes();

		// Skip the first attribute, which is FoA
		attributes.nextElement();
		
		while (attributes.hasMoreElements()) {
			
			Attribute attribute = attributes.nextElement();
			// attrName -> IsDenominatorOf_12, IsDenominatorOf_13, IsDenominatorOf_23
			String attrName = attribute.name();
			
			int attrValue = evalAttr(attrName, foaValues, featurePredicates);
			tmpAttrVals[i++] = attrValue;
		}
		
		// Target concept
		// The attribute values for target are {"true", "false"}
		//
		tmpAttrVals[i] = example.isPositiveExample() ? 0 : 1;
		
		add(new Instance(1.0, tmpAttrVals));
		
	}

	/**
	 * @param attrName		E.g., IsDenominatorOf_12, IsDenominatorOf_13, IsDenominatorOf_23
	 * @param foaValues
	 * @return				1 if true, -1 if false, and 0 if the return from the featurePredicate is "null"
	 */
	private int evalAttr(String attrName, ArrayList<String> foaValues, Vector<UserDefJessSymbol> featurePredicates) {

		// TraceLog.out("attrName == " + attrName);		
		
		String fpName = attrName.substring(0, attrName.indexOf('_'));
		UserDefJessSymbol featurePredicate = lookupFeaturePredicate(fpName, featurePredicates);

		// Get the "12" part at the tail of the attrName
		String argIdx = attrName.substring(attrName.indexOf('_')+1);
		
		ArrayList<String> argV = new ArrayList<String>();
		for (int i = 0; i < argIdx.length(); i++) {
			int idx = argIdx.charAt(i) - '0';
			argV.add(foaValues.get(idx));
		}
		
		String value = featurePredicate.apply(argV);

		// TraceLog.out("value == " + value);
		
		// Need to differentiate true, false, null, and something else (i.e., actual value) 
		
		return parsePredicateValue(value);
	}
		
	/**
	 * @param value
	 * @return
	 */
	private int parsePredicateValue(String value) {
		
		int returnValue = NULL_VALUE;
		
		if (value != null) {
			if (value.equals(UserDefJessSymbol.TRUE_VALUE)) {
				returnValue = TRUE_VALUE;
			} else if (value.equals(UserDefJessSymbol.FALSE_VALUE)) {
				returnValue = FALSE_VALUE;
			} else {
				new Exception("Invalid predicate return value: " + value).printStackTrace();
			}
		}
		return returnValue;
	}

	/**
	 * @param simpleFpName			A class name obtained by getSimpleName()
	 * @param featurePredicates		A list of UserDefJessSymbol objects
	 * @return		A UserDefJessSymbol object that corresponds to the simpleFpName
	 */
	private UserDefJessSymbol lookupFeaturePredicate(String simpleFpName, Vector<UserDefJessSymbol> featurePredicates) {

		UserDefJessSymbol targetFp = null;
		
		for (UserDefJessSymbol fp : featurePredicates) {
			
			if (simpleFpName.equals(fp.getClass().getSimpleName())) {
				targetFp = fp;
				break;
			}
		}
		
		return targetFp;
	}
	
	/*
	@SuppressWarnings("unchecked")
	private UserDefJessSymbol convertFeaturePredicate(String fpName) {
		
		UserDefJessSymbol featurePredicate = convertFeaturePredicateCache(fpName);
				
		if (featurePredicate == null) {
			
			
			Class<UserDefJessSymbol> fpClass;
			try {
				fpClass = (Class<UserDefJessSymbol>) Class.forName(fpName);
				featurePredicate = fpClass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			convertFeaturePredicateCacheAdd(fpName, featurePredicate);
		}
		
		return featurePredicate;
	}
	*/

	/**
	 * @param attrName
	 * @param featurePredicate
	 */
	/*
	private void convertFeaturePredicateCacheAdd(String attrName, UserDefJessSymbol featurePredicate) {
		
		getAttributeFeaturePredicateHash().put(attrName, featurePredicate);
	}
	*/

	/**
	 * @param attrName
	 * @return
	 */
	/*
	private HashMap<String, UserDefJessSymbol>	attributeFeaturePredicateHash = new HashMap<String, UserDefJessSymbol>();
	
	private UserDefJessSymbol convertFeaturePredicateCache(String attrName) {
		
		UserDefJessSymbol featurePredicate = null;
		
		if (getAttributeFeaturePredicateHash().containsKey(attrName)) {
			featurePredicate = getAttributeFeaturePredicateHash().get(attrName);
		}
		
		return featurePredicate;
	}
	*/

	
	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// Set up Attributes
	// 
	
	/*
	private HashMap<String, UserDefJessSymbol> getAttributeFeaturePredicateHash() {
		return attributeFeaturePredicateHash;
	}
	*/
	
	/*
	private void setAttributeFeaturePredicateHash(
			HashMap<String, UserDefJessSymbol> attributeFeaturePredicateHash) {
		this.attributeFeaturePredicateHash = attributeFeaturePredicateHash;
	}
	*/
	
}
