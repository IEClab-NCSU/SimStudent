package com.tonyliu.entity;

/**
 * Created by tao on 8/14/17.
 */
public class StudentProbability {
    private String studentId;
    private String skillname;
    private String correctness;
    private String timeStamp;
    private String probability;
    private String opportunityCounts;
    private String problemName;


    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }

    public String getSkillname() {
        return skillname;
    }

    public void setSkillname(String skillname) {
        this.skillname = skillname;
    }

    public String getCorrectness() {
        return correctness;
    }

    public void setCorrectness(String correctness) {
        this.correctness = correctness;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getOpportunityCounts() {
        return opportunityCounts;
    }

    public void setOpportunityCounts(String opportunityCounts) {
        this.opportunityCounts = opportunityCounts;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }
}
