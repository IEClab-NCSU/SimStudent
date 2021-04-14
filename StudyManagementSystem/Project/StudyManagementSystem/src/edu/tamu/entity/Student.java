package edu.tamu.entity;

public class Student {
	private String studentID;
	private Teacher teacher;
	private String pretestDate;
	private String posttestDate;
	private String delayedtestDate;
	private String interventionFrom;
	private String interventionTo;
	private String condition;
	private String preTestVersion;
	private String postTestVersion;
	private String delayedTestVersion;
	

	public Student(String studentID, Teacher teacher, String pretestDate, String posttestDate, String delayedtestDate,
			String interventionFrom, String interventioTo, String condition, String preTestVersion,
			String postTestVersion, String delayedTestVersion) {
		super();
		this.studentID = studentID;
		this.teacher = teacher;
		this.pretestDate = pretestDate;
		this.posttestDate = posttestDate;
		this.delayedtestDate = delayedtestDate;
		this.interventionFrom = interventionFrom;
		this.interventionTo = interventioTo;
		this.condition = condition;
		this.preTestVersion = preTestVersion;
		this.postTestVersion = postTestVersion;
		this.delayedTestVersion = delayedTestVersion;
	}
	public Student(String studentID, Teacher teacher) {
		super();
		this.studentID = studentID;
		this.teacher = teacher;
	}
	public String getStudentID() {
		return studentID;
	}
	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}
	public Teacher getTeacher() {
		return teacher;
	}
	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getPreTestVersion() {
		return preTestVersion;
	}
	public void setPreTestVersion(String preTestVersion) {
		this.preTestVersion = preTestVersion;
	}
	public String getPostTestVersion() {
		return postTestVersion;
	}
	public void setPostTestVersion(String postTestVersion) {
		this.postTestVersion = postTestVersion;
	}
	public String getDelayedTestVersion() {
		return delayedTestVersion;
	}
	public void setDelayedTestVersion(String delayedTestVersion) {
		this.delayedTestVersion = delayedTestVersion;
	}
	public String getPretestDate() {
		return pretestDate;
	}
	public void setPretestDate(String pretestDate) {
		this.pretestDate = pretestDate;
	}
	public String getPosttestDate() {
		return posttestDate;
	}
	public void setPosttestDate(String posttestDate) {
		this.posttestDate = posttestDate;
	}
	public String getDelayedtestDate() {
		return delayedtestDate;
	}
	public void setDelayedtestDate(String delayedtestDate) {
		this.delayedtestDate = delayedtestDate;
	}
	public String getInterventionFrom() {
		return interventionFrom;
	}
	public void setInterventionFrom(String interventionFrom) {
		this.interventionFrom = interventionFrom;
	}
	public String getInterventionTo() {
		return interventionTo;
	}
	public void setInterventionTo(String interventioTo) {
		this.interventionTo = interventioTo;
	}
	
}
