package edu.tamu.entity;

import java.sql.Date;

public class StudySchool {
	private int study_school_key;
	private Date pretest;
	private Date interventionTo;
	private Date interventionFrom;
	private Date posttest;
	private Date delayedtest;
	private String schoolName;
	private String windowsLog;
	private String macLog;
	public int getStudy_school_key() {
		return study_school_key;
	}
	public void setStudy_school_key(int study_school_key) {
		this.study_school_key = study_school_key;
	}
	public Date getPretest() {
		return pretest;
	}
	public StudySchool(int study_school_key, Date pretest, Date interventionTo, Date interventionFrom,
			Date posttest, Date delayedtest, String schoolName, String windowsLog, String macLog) {
		super();
		this.study_school_key = study_school_key;
		this.pretest = pretest;
		this.interventionTo = interventionTo;
		this.interventionFrom = interventionFrom;
		this.posttest = posttest;
		this.delayedtest = delayedtest;
		this.schoolName = schoolName;
		this.windowsLog = windowsLog;
		this.macLog = macLog;
	}
	public void setPretest(Date pretest) {
		this.pretest = pretest;
	}
	public Date getInterventionTo() {
		return interventionTo;
	}
	public void setInterventionTo(Date interventionTo) {
		this.interventionTo = interventionTo;
	}
	public Date getInterventionFrom() {
		return interventionFrom;
	}
	public void setInterventionFrom(Date interventionFrom) {
		this.interventionFrom = interventionFrom;
	}
	public Date getPosttest() {
		return posttest;
	}
	public void setPosttest(Date posttest) {
		this.posttest = posttest;
	}
	public Date getDelayedtest() {
		return delayedtest;
	}
	public void setDelayedtest(Date delayedtest) {
		this.delayedtest = delayedtest;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getWindowsLog() {
		return windowsLog;
	}
	public void setWindowsLog(String windowsLog) {
		this.windowsLog = windowsLog;
	}
	public String getMacLog() {
		return macLog;
	}
	public void setMacLog(String macLog) {
		this.macLog = macLog;
	}
	
}
