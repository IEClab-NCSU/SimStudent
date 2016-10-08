package edu.cmu.hcii.ctat;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an assignment as described in a course.xml file.
 */
public class CTATCourseAssignment {
	//public String id;
	public String name;
	public String status;
	public int position;
	public String assign_type;
	
	private List<CTATCourseProbset> probsets = new ArrayList<CTATCourseProbset>();
	
	public void addProbset(CTATCourseProbset probset)
	{
		probsets.add(probset);
	}
	
	public List<CTATCourseProbset> getProbsets()
	{
		List<CTATCourseProbset> result = new ArrayList<CTATCourseProbset>(probsets.size());
		for(CTATCourseProbset probset : probsets)
		{
			result.add(probset);
		}
		return result;
	}
	
	public int numProbsets()
	{
		return probsets.size();
	}

	/**
	 * @return "" FIXME Need condition in course.xml's &lt;assignment&gt; elements
	 */
	public String getConditionName() {
		return "";
	}
}
