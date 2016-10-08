/**
 * 
 */
package edu.cmu.pact.miss;

import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;

/**
 * @author mazda
 *
 */
public class SsProblemEdge extends EdgeData {

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Fields
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	Vector<String> skills = new Vector<String>();
	public Vector<String> getSkills() { return skills; }

	// The edge connects source and dest
	SsProblemNode source = null;
	SsProblemNode dest = null;

	public SsProblemNode getSource() { return source; }
	public void setSource(SsProblemNode source) { this.source = source; }
	public SsProblemNode getDest() { return dest; }
	public void setDest(SsProblemNode dest) { this.dest = dest; }

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Constructor
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	public SsProblemEdge() {}
	
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

	// Returns a first skillName in the list
	public String getSkillName() {
		return (String)getSkills().get(0);
	}

}
