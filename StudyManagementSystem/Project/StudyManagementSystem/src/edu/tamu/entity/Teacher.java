package edu.tamu.entity;

public class Teacher {

		private int teacherID;
		private String teacherName;
		private String classname;
		private int studyschoolID;
		private int numOfStudents;
		private String prefix;
		private String windowsLog;
		private String macLog;
		private String schoolName;
		
		
		public Teacher(String teacherName, String classname, int studyschoolID, String windowsLog, String macLog,
				String schoolName) {
			super();
			this.teacherName = teacherName;
			this.classname = classname;
			this.studyschoolID = studyschoolID;
			this.windowsLog = windowsLog;
			this.macLog = macLog;
			this.schoolName = schoolName;
		}
		public int getTeacherID() {
			return teacherID;
		}
		public void setTeacherID(int teacherID) {
			this.teacherID = teacherID;
		}
		public int getStudyschoolID() {
			return studyschoolID;
		}
		public Teacher(int teacherID, String teacherName, String classname, int studyschoolID, int numOfStudents,
				String prefix) {
			super();
			this.teacherID = teacherID;
			this.teacherName = teacherName;
			this.classname = classname;
			this.studyschoolID = studyschoolID;
			this.numOfStudents = numOfStudents;
			this.prefix = prefix;
		}
		public Teacher(String teacherName, String classname, int studyschoolID, String windowsLog, String macLog) {
			super();
			this.teacherName = teacherName;
			this.classname = classname;
			this.studyschoolID = studyschoolID;
			this.windowsLog = windowsLog;
			this.macLog = macLog;
		}
		public void setStudyschoolID(int studyschoolID) {
			this.studyschoolID = studyschoolID;
		}
		
		public int getNumOfStudents() {
			return numOfStudents;
		}
		public void setNumOfStudents(int numOfStudents) {
			this.numOfStudents = numOfStudents;
		}
		public String getPrefix() {
			return prefix;
		}
		public void setPrefix(String prefix) {
			this.prefix = prefix;
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
		public String getClassname() {
			return classname;
		}
		public void setClassname(String classname) {
			this.classname = classname;
		}
		public String getTeacherName() {
			return teacherName;
		}
		public void setTeacherName(String teacherName) {
			this.teacherName = teacherName;
		}
		public String getSchoolName() {
			return schoolName;
		}
		public void setSchoolName(String schoolName) {
			this.schoolName = schoolName;
		}
		

}
