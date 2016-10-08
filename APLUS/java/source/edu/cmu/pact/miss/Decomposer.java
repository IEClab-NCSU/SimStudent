package edu.cmu.pact.miss;

import java.util.Vector;
/**
 * Abstract class for classes that decompose student input in some fashion
 * @author ajzana
 *
 */
public abstract class Decomposer 
{
	
	/**
	 * 
	 * @param foa a focus of attention or input value for simstudent
	 * @return the chucks of the input in a vector or null if the string cannot be fruther decomposed
	 */
	public abstract Vector decompose(String foa);
}
