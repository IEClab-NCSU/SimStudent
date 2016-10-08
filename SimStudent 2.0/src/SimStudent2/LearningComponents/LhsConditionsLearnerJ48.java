/**
 * Created: Aug 21, 2014 9:04:27 PM
 * @author mazda
 * 
 */
package SimStudent2.LearningComponents;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;
import SimStudent2.TraceLog;
import SimStudent2.ProductionSystem.UserDefJessSymbol;

/**
 * @author mazda
 *
 */
public class LhsConditionsLearnerJ48 extends LhsConditionsLearner {

	Vector<UserDefJessSymbol> featurePredicates;
	
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * @param string
	 */
	public LhsConditionsLearnerJ48(String featurePredicatesFile) {
		
		setFeaturePredicates(loadFeaturePredicates(featurePredicatesFile));
	}

	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// Load feature predicates
	// 
	/**
	 * @param featurePredicatesFile
	 * @return
	 */
	private Vector<UserDefJessSymbol> loadFeaturePredicates(String featurePredicatesFile) {
		
		Vector<UserDefJessSymbol> featurePredicates = new Vector<UserDefJessSymbol>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(featurePredicatesFile));
			
			String fpNameRaw;
			while ((fpNameRaw = br.readLine()) != null) {
				
				String fpName = "";
				for (int i = 0; i < fpNameRaw.length(); i++) {
					char c = fpNameRaw.charAt(i);
					if (c == ';') {
						break;
					} else if (c != ' ') {
						fpName += c;
					}
				}
				
				if (!fpName.isEmpty()) {
					UserDefJessSymbol fp = instatiateFeaturePredicate(fpName);
					featurePredicates.add(fp);
				}
				
			}
			
			br.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return featurePredicates;
	}

	/**
	 * @param fpName
	 * @return
	 */
	private UserDefJessSymbol instatiateFeaturePredicate(String fpName) {
		
		UserDefJessSymbol userDefFp = null;
		
		try {
			@SuppressWarnings("unchecked")
			Class<UserDefJessSymbol> fpClass = (Class<UserDefJessSymbol>) Class.forName(fpName);
			userDefFp = fpClass.newInstance();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return userDefFp;
	}

	
	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// Re-run J48 classifier 
	//
	@Override
	public LhsConditions refineLhsConditions(Example example, Instances j48Instances) {

		((J48Instances) j48Instances).addInstance(example, getFeaturePredicates());
		return j48BuildTree(j48Instances);
	}
	
	
	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .
	// Initialize J48Instances for a new production 
	// 
	@Override
	public LhsConditions initialLhsConditions(Example example) {
		
		String ruleName = example.getRuleName();
		int numFoA = example.getNumFoA();
		J48Instances j48Instances = j48makeNewInstancesFor(ruleName, numFoA);
		
		return refineLhsConditions(example, j48Instances);
	}
	
	/**
	 * @param ruleName
	 * @return
	 */
	private J48Instances j48makeNewInstancesFor(String ruleName, int numFoA) {
		
		FastVector attributes = setupAttributes(ruleName, numFoA);
		
		return new J48Instances(ruleName, attributes);
	}
	
	/**
	 * @return
	 */
	private FastVector setupAttributes(String ruleName, int numFoA) {
		
		FastVector attributes = new FastVector();
		
		// Add FoA as a plane string
		attributes.addElement(new Attribute("foa", (FastVector) null));
		
		// Make attributes for each of the feature predicates
		for (UserDefJessSymbol fp : getFeaturePredicates()) {
			
			Vector<String> attrNames = listAttributeNames(fp, numFoA);
			
			// attName shows all combination of FoA as the argument(s) of the feature predicate
			// For example, if there are 3 FoA values given to a particular skill demonstrated, 
			// following attNames will be generated for feature predicates wiht one argument
			// attName -> HasCoefficient_1, HasCoefficient_2, HasCoefficient_3
			// and for a feature predicate with two arguments 
			// attName -> IsDenominatorOf_12, IsDenominatorOf_13, IsDenominatorOf_23
			// 
			for (String attrName : attrNames) {
				
				attributes.addElement(new Attribute(attrName));
				
			}
		}
		
		// Set the class attribute, which must be Nominal
		FastVector values = nominalValues(new String[]{"true", "false"});
		attributes.addElement(new Attribute(ruleName, values));
		
		return attributes;
	}

	/**
	 * @param fp
	 * @param numFoA
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Vector<String> listAttributeNames(UserDefJessSymbol fp, int numFoA) {
		
		Vector<String> attrNames = new Vector<String>();
		
		String attNameStem = fp.getClass().getSimpleName();
		int argN = fp.getArity();

		// Make all combinations of argument index (e.g., {12, 13, 23})
		Vector<String> extStr = new Vector<String>();
		for (int i = 0; i < numFoA; i++) {
			extStr.add("" + i);
		}

		Enumeration<String[]> extentions = null;
		try {
			extentions = new Permutations(extStr.toArray(), argN);
		} catch (CombinatoricException e) {
			e.printStackTrace();
		}

		while (extentions.hasMoreElements()) {
			
			Object[] extObj = extentions.nextElement();
			Vector<String> ext = arrayToVector(extObj);
			String attrName = attNameStem + "_" + ext.get(0);
			
			attrNames.add(attrName);
		}
		
		return attrNames;
	}

    private Vector<String> arrayToVector( Object[] array ) {
    	
    	Vector<String> v = new Vector<String>();
    	for (int i = 0; i < array.length; i++) {
    		v.add( (String) array[i] );
    	}
    	return v;
    }
	
	private FastVector nominalValues(String[] values) {
		
		FastVector nomialValues = new FastVector();
		
		for (String val : values) {
			nomialValues.addElement(val);
		}
		
		return nomialValues;
	}


	// . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . 
	// Build the classifier
	// 
	
	/**
	 * @param j48Instances
	 * @return
	 */
	private LhsConditions j48BuildTree(Instances j48Instances) {

		// The first attribute shows FoA strings
		Remove rm = new Remove();
		rm.setAttributeIndices("1");
		
		J48 j48 = new J48();
		FilteredClassifier fc = new FilteredClassifier();
		fc.setFilter(rm);
		fc.setClassifier(j48);
		try {
			fc.buildClassifier(j48Instances);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TraceLog.out("J48: " + j48Instances);
		
		LhsConditions lhsConditions = new LhsConditions(j48Instances, j48.toIfThen("true"));

		return lhsConditions;
	}


	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	// Getters & Setters
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	


	private Vector<UserDefJessSymbol> getFeaturePredicates() {
		return featurePredicates;
	}
	private void setFeaturePredicates(Vector<UserDefJessSymbol> featurePredicates) {
		this.featurePredicates = featurePredicates;
	}



}
