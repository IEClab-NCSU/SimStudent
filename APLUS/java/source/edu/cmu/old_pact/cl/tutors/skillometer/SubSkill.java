package edu.cmu.old_pact.cl.tutors.skillometer;

public class SubSkill extends SkillCalculator{
	private String id;
	private SectionSkill sectionSkill;
	
	public SubSkill(String name, float p_guess, 
					float p_learn, float p_slip, SectionSkill sectionSkill){
		super(name);
		setDefPGuess(p_guess);
		setDefPLearn(p_learn);
		setDefPSlip(p_slip);
		this.sectionSkill = sectionSkill;
		sectionSkill.addSubSkill(this);
	}
	
	public void setID(String id){
		this.id = id;
	}
	
	public String getID(){
		return id;
	}
	
	public String getSkillName(){
		return sectionSkill.getName();
	}
	
    /* The algorithm for updating skills implemented here is 
     * to update the main skill by the same amount by which the sub-
     * skill is advanced. There is no officially agreed upon method, but 
     * something had to be implemented. Two other options are the "mean" strategy 
     * and the "max" strategy. The mean strategy would take the mean of the pknowns
     * of the subskills, and the max strategy takes the maximum of the pknowns 
     * of the subskills. The mean strategy penalizes a student for using the 
     * same method for solving a problem every time. The max strategy penalizes
     * a student for using different methods each time for solving problems.
     * The strategy implemented below penalizes neither, but overestimates the
     * knowledge the student has about using any particular method for solving a 
     * problem. A check must be done to make sure the pknown value does not exceed
     * 1.0 using this strategy. 
     */
	public float updateSkill(boolean success) {
		if(!sectionSkill.getIsRemediated())
			return sectionSkill.getPKnown();
		else {
                double old_sub_k = getPKnown();
                float new_k = super.updateSkill(success);
                //double difference = sub_k - old_sub_k;
                //double old_k = sectionSkill.getPKnown();
                    
                //double new_k = old_k+difference;
                    
               //     if (new_k>=1)
               //         new_k = 0.999;

                    System.out.println("!DEBUG! Updating Skill: " + sectionSkill.getName());
                    System.out.println("  Old value: " + old_sub_k);
                    System.out.println("  New value: " + new_k);
                    sectionSkill.setPKnown((float)(new_k));

                    return (float)new_k;
		}
	}
	
	public boolean canDisplay(){
		return sectionSkill.getIsDisplayed();
	}
	
}
	
