package edu.cmu.hcii.ctat;

/**
 * Represents a problem set as described in a course.xml file.
 * A more detailed description of the problem set can be found in problem_set.xml and its associated class CTATProblemSet
 */
public class CTATCourseProbset {
	//public String id;
	public String name;
	public int position;
	public String subdirectory;
	public String activation_status;
	public CTATProblemSet ctatProblemSet = null; /// object that represents the actual problem set (the information in problem_set.xml)
	
	public String status; // not specified in new course.xml
}
