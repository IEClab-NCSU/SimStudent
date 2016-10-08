/**
 * Created: Jan 7, 2014 9:47:32 PM
 * @author mazda
 * 
 */
package SimStudent2.LearningComponents;

import java.util.ArrayList;
import java.util.List;

import SimStudent2.TraceLog;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

/**
 * @author mazda
 *
 */
public class WmePerceptionSearchSuccessorFn implements SuccessorFunction {

	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Abstract methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	private ArrayList<Example> positiveExamples = new ArrayList<Example>();
	private ArrayList<Example> negativeExamples = new ArrayList<Example>();

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	/**
	 * The WME Perception Search 
	 * Given state <code>object</code> is expanded by generalizing a single ground WME node that, by definition,
	 * is most specific. 
	 * 
	 * @see aima.search.framework.WmePerceptionSearchSuccessorFn#getSuccessors(java.lang.Object)
	 * @param stateObject an <code>Object</code> value
	 * @return a <code>List</code> value
	 */
	@Override
	public final List<Successor> getSuccessors(final Object stateObject) {

		// The return value
		List<Successor> successors = new ArrayList<Successor>();
		
		// The current state being expanded
		WmePerceptionSearchState currentState = (WmePerceptionSearchState)stateObject;

		// Expand state only when there are more WMEs that must be involved in LHS,
		// AND the search went not too long
		boolean isTimeUp = currentState.isRunningOutOfTime();
		
		if (!isTimeUp) {

			// Make another generalization based on the current WME Perception 
			WmePerception currentWmePerception = currentState.getWmePerception();
			ArrayList<WmePerception> offsprings = generalizeSingleWmePath(currentWmePerception);
			
			ArrayList<Example> positiveExamples = getPositiveExamples();
			ArrayList<Example> negativeExamples = getNegativeExamples();

			// Make a new successor states for each of the WME Perception
			for (WmePerception newWmePerception : offsprings ) {
				
				// Then, generate LhsTest that are consistent with negative examples. 
				ArrayList<LhsTest> lhsTests = generateLhsTests(negativeExamples);
				newWmePerception.setWmeConditions(lhsTests);
				
				WmePerceptionSearchState child = makeNewWmePerceptionSearchState(newWmePerception);
				String action = actionStr(newWmePerception);
				
				// TraceLog.out("actionStr = " + action);
				
				Successor successor = new Successor(action, child);
				successors.add(0, successor);
			
			}
		}

		/*
		TraceLog.out("getSuccessors returning " + successors.size() + " successor(s)...");
		TraceLog.out("@ - @ - @ - @ - @ - @ - @ - @ - @ - @ - @ - @ -");
		for (Successor theSuccessor : successors) {
			TraceLog.out("" + ((WmePerceptionSearchState)theSuccessor.getState()).getWmePerception());
		}
		TraceLog.out("@ - @ - @ - @ - @ - @ - @ - @ - @ - @ - @ - @ -");
		*/
		
		return successors;
	}
	
	/**
	 * Given a current WME perception, this method returns variants of the current WME perception each of 
	 * which has one and only one node in a WmePath generalized by one "rank". 
	 * 
	 * For a normal Jess WME retrieval, there are only two "ranks"
	 * 		The most specific rank (cells ?var84 ? ? ? ? ?)
	 * 		The most general rank (cells $? $?var84 $?)
	 * 
	 * @param currentWmePerception
	 * @return
	 */
	@SuppressWarnings("null")
	private ArrayList<WmePerception> generalizeSingleWmePath(WmePerception currentWmePerception) {
	
		ArrayList<WmePerception> children = new ArrayList<WmePerception>();
		
		// Concatenate WME paths in FoA and Selection 
		ArrayList<WmePath> wmePaths = new ArrayList<WmePath>();
		for (WmePath foaWmePath : currentWmePerception.getFocusOfAttention()) {
			wmePaths.add(foaWmePath);
		}
		wmePaths.add(currentWmePerception.getSelection());
		
		// TraceLog.out("generalizeSingleWmePath: * * * * * * * * * * * * * * * * *\nwmePaths = " + wmePaths);
		
		// Loop through WME paths, which are either FoA or selection, and
		for (WmePath wmePath : wmePaths) {
			
			// TraceLog.out("wmePath => " + wmePath);
			
			// make all possible generalization for the WME path, by making a single
			// WME Path Node generalized, and ...
			ArrayList<WmePath> generalizedWmePaths = wmePath.generalizeSingleWmePathNode();
			
			// Make a new WmePerception by replacing the WME path with the generalized WME path
			for (WmePath genWmePath : generalizedWmePaths) {
				WmePerception genWmePerception = new WmePerception(wmePaths);
				genWmePerception.replaceWmePath(wmePath.getName(), genWmePath);
				// TraceLog.out("generalizeSingleWmePath: genWmePerception = " + genWmePerception);
				children.add(genWmePerception);
			}
		}
		
		return children;
	}

	/**
	 * @param wmePerception
	 * @return					A string representation of the given wmePerception
	 */
	private String actionStr(WmePerception wmePerception) {
		return wmePerception.toFlatString();
	}

	/**
	 * @param newWmePerception
	 * @return					Make a new state that is a derivation of the <code>currentState</code> with 
	 * 							<code>newWmePerception</code>
	 */
	private WmePerceptionSearchState makeNewWmePerceptionSearchState(WmePerception newWmePerception) {

		return new WmePerceptionSearchState(newWmePerception);
	}

	/**
	 * @param negativeExamples
	 * @return					A set of LhsTests that are consistent with given <code>negativeExamples</code>
	 */
	public ArrayList<LhsTest> generateLhsTests(ArrayList<Example> negativeExamples) {
		//ToDo @@ generateLhsTests(ArrayList<Example> negativeExamples)
		// new Exception("generateLhsTests() undefined").printStackTrace();
		return null;
	}

	/**
	 * @param examples
	 */
	public void updateExamples(Example example) {
		
		if (example.isPositiveExample()) {

			addPositiveExample(example);
		
		} else {
			
			addNegativeExample(example);
		}
	}
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Getter & Setter
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	private ArrayList<Example> getPositiveExamples() { return positiveExamples; }
	/*
	private void setPositiveExamples(Vector<Example> positiveExamples) {
		this.positiveExamples = positiveExamples;
	}
	*/
	private void addPositiveExample(Example example) {
		this.positiveExamples.add(example);
	}

	private ArrayList<Example> getNegativeExamples() { return negativeExamples; }
	/*
	private void setNegativeExamples(Vector<Example> negativeExamples) {
		this.negativeExamples = negativeExamples;
	}
	*/
	private void addNegativeExample(Example example) {
		this.negativeExamples.add(example);
	}

}
