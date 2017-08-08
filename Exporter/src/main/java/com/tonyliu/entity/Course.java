package com.tonyliu.entity;

import java.io.Serializable;

/**
 * Created by tao on 8/3/17.
 */
public class Course implements Serializable {


    private String module_type;
    private String course_id;

    public String getModule_type() {
        return module_type;
    }

    public void setModule_type(String module_type) {
        this.module_type = module_type;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }
}
