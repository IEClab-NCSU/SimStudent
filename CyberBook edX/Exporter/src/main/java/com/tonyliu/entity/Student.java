package com.tonyliu.entity;

import java.io.Serializable;

/**
 * Created by tao on 8/11/17.
 */
public class Student implements Serializable{
    private String studentId;
    private String skillname;
    private String probability;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSkillname() {
        return skillname;
    }

    public void setSkillname(String skillname) {
        this.skillname = skillname;
    }

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }
}
