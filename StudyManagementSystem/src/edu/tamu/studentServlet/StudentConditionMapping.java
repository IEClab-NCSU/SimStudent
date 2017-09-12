package edu.tamu.studentServlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import edu.tamu.entity.Student;
import edu.tamu.entity.StudySchool;
import edu.tamu.entity.Teacher;

/**
 * Servlet implementation class GenerateUserID
 */
@WebServlet("/StudentConditionMapping")
public class StudentConditionMapping extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final String JDBC_DRIVER="com.mysql.jdbc.Driver";
	final String DB_URL = "jdbc:mysql://kona.education.tamu.edu:3306/studymanagement";
	final String user = "simstudent";
	final String password = "simstudent";   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StudentConditionMapping() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String studyName = request.getParameter("studyName");
		List<Integer> school_study_id = new ArrayList<Integer>();
		List<String> anon_prefix = new ArrayList<String>();
		List<Teacher> newTeacherList = new ArrayList<Teacher>();
		List<Teacher> oldTeacherList = new ArrayList<Teacher>();
		List<Student> studentList = new ArrayList<Student>();
		List<String> conditions = new ArrayList<String>();
		List<Integer> conditionCount = new ArrayList<Integer>();
		HashSet<String> shortID = new HashSet<String>();
		HashSet<Integer> teacherKey = new HashSet<Integer>();
		//HashMap<Integer,String> classMap = new HashMap<Integer,String>();
		HashMap<Integer,StudySchool> studyschoolMap = new HashMap<Integer,StudySchool>();
		
		String[] versions = {"ABC","ACB","BAC","BCA","CAB","CBA"};
		String assignmentType = "";
		
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String title = "Successfully generated";
	    String docType =
	        "<!doctype html public \"-//w3c//dtd html 4.0 " +
	         "transitional//en\">\n";
	         out.println(docType +
	         "<html>\n" +
	         "<head><title>" + title + "</title></head>\n" +
	         "<body bgcolor=\"#f0f0f0\">\n" +
	         "<h1 align=\"center\">" + title + "</h1>\n");
	         try {
				Class.forName(JDBC_DRIVER);
				Connection con = (Connection) DriverManager.getConnection(DB_URL,user,password);
				
				/**
				 * Get the schoolstudyID from school table using study Name
				 * 
				 */
				Statement stat1 = (Statement)con.createStatement();
				String sql1= "select * from school where study_key in (select study_key from study where study_name="
				  +"'"+studyName+"') and (pretest >= CURDATE() or intervention_to >= CURDATE() or intervention_from >= CURDATE() or posttest >= CURDATE() or delayedtest >= CURDATE())";
				ResultSet rs = stat1.executeQuery(sql1);
				while(rs.next()) {
					int id = rs.getInt("study_school_key");
					school_study_id.add(id);
					anon_prefix.add(rs.getString("school_study_anon_prefix"));
					studyschoolMap.put(id,new StudySchool(id,rs.getDate("pretest"),rs.getDate("intervention_to"),rs.getDate("intervention_from"),rs.getDate("posttest"),
							rs.getDate("delayedtest"),rs.getString("schoolname"),rs.getString("windowslog_dir"),rs.getString("maclog_dir")));
				}
				
				
				for(int i=0; i<school_study_id.size(); i++)
					System.out.println(school_study_id.get(i)+" "+anon_prefix.get(i));
				
				/**
				 * Get the conditions for the study
				 */
				Statement stat3 = (Statement) con.createStatement();
				String sql3 = "select condition_name from conditions where condition_key in ( select condition_key from study_condition_mapping where study_key in (select study_key from study where study_name='"+studyName+"'))";
				ResultSet rs3 = stat3.executeQuery(sql3);
				while(rs3.next()){
					conditions.add(rs3.getString("condition_name"));
					conditionCount.add(0);
				}
					
				
				/**
				 * Get the level of assignment
				 */
				Statement stat4 = (Statement) con.createStatement();
				String sql4 ="select level_of_assignment from study where study_name='"+studyName+"'";
				ResultSet rs4 = stat4.executeQuery(sql4);
				if(rs4.next())
					assignmentType = rs4.getString("level_of_assignment");
				
				
				
				
				/**
				 * Get the list of teachers and students whose ID are already generated  
				 * 
				 */
				 Statement stat5 = (Statement) con.createStatement();
				 for(int i=0; i<school_study_id.size(); i++) {
					 String sql5 = "select studentid, teacher_key from shortid_table where study_school_key="+school_study_id.get(i);
					 ResultSet rs5 = stat5.executeQuery(sql5);
					 while(rs5.next()) {
						 shortID.add(rs5.getString("studentid"));
						 teacherKey.add(rs5.getInt("teacher_key"));
					 }
				 }
				 /**
				 * Get teachers (whose student ID is not yet generated) details from the table
				 * 
				 * 
				 *  if user ID is not generated for the teacher class 
				 * 			then generate user ID
				 *  else 
				 * 		    check whether new students are added 
				 * 				 if 'yes' then 
				 * 						generate user ID for the new kids	
				 */
				Statement stat2 = (Statement) con.createStatement();
				for(int i=0; i<school_study_id.size(); i++) {
					String sql2 = "select * from teacher where study_school_key="+school_study_id.get(i);
					ResultSet rs2 = stat2.executeQuery(sql2);
					while(rs2.next()){
						if(!teacherKey.contains(rs2.getInt("teacher_key"))) {
							newTeacherList.add(new Teacher(rs2.getInt("teacher_key"),rs2.getString("teacher_name"),rs2.getString("class_name"),school_study_id.get(i),rs2.getInt("no_of_students"),anon_prefix.get(i)));
							//classMap.put(rs2.getInt("study_school_key"), rs2.getString("class_name"));
						}
						else
							oldTeacherList.add(new Teacher(rs2.getInt("teacher_key"),rs2.getString("teacher_name"),rs2.getString("class_name"),school_study_id.get(i),rs2.getInt("no_of_students"),anon_prefix.get(i)));
					}
						
				}
				
				
				/**
				 * check whether the teacher has new kids 
				 * 			if so then generate ID for them 
				 * 
				 *  find the no of students in shortidtable with the teacherkey
				 */
				
				Statement stat7 = (Statement)con.createStatement();
				
				for(int i=0; i < oldTeacherList.size(); i++) {
					int teacherID = oldTeacherList.get(i).getTeacherID();
					String sql7 = "select count(studentid) from shortid_table where teacher_key="+teacherID;
					ResultSet rs7 = stat7.executeQuery(sql7);
					int idCount = 0;
					if(rs7.next())
						idCount = rs7.getInt(1);
					
					int numOfStudents = oldTeacherList.get(i).getNumOfStudents();
					System.out.println(" ID generated : "+idCount+"  Now : "+numOfStudents+ " teacher ID "+teacherID);
				    oldTeacherList.get(i).setNumOfStudents(numOfStudents - idCount);
					
				}
				/*System.out.println("Teachers details");
				
				for(int i=0; i<teacherList.size(); i++) {
					System.out.println(" Teacher ID "+teacherList.get(i).getTeacherID());
					System.out.println(" StudySchool ID "+teacherList.get(i).getStudyschoolID());
					System.out.println(" Number of students "+teacherList.get(i).getNumOfStudents());

				}*/
				
				/**
				 * Generate user ID for the new teacher
				 */
				generateUserID(newTeacherList, studentList,shortID);
				
				/***
				 *  Generate user ID for the new kids in a class
				 */
				generateUserID(oldTeacherList, studentList,shortID);
				
				/**
				 *  assign condition & test version
				 */
				if(assignmentType.equalsIgnoreCase("Student"))
					studentLevelAssignment(studentList,conditions,conditionCount);
				else if(assignmentType.equalsIgnoreCase("Class"))
					classLevelAssignment(studentList,conditions,conditionCount);
				else
					schoolLevelAssignment(studentList,conditions,conditionCount);
				
				testVersionAssignment(studentList,conditions,versions);
				
				insertDatabase(studentList,con);
				
				studentList.clear();
				Statement stat6 = (Statement) con.createStatement();
				for(int i=0; i< school_study_id.size(); i++) {
					String sql6 = "select distinct s.studentid, s.conditions, s.pretest_version, s.posttest_version, s.delayedtest_version, t.teacher_name, "
							+ "t.class_name, sc.schoolname, sc.pretest, sc.intervention_from, sc.intervention_to, sc.posttest, sc.delayedtest, sc.windowslog_dir, sc.maclog_dir from "
							+ "shortid_table s, teacher t, school sc where s.study_school_key = t.study_school_key AND s.study_school_key=sc.study_school_key and t.teacher_key=s.teacher_key and sc.study_school_key= "+school_study_id.get(i)
							+ " and (sc.pretest >= CURDATE() or sc.intervention_to >= CURDATE() or sc.intervention_from >= CURDATE() or sc.posttest >= CURDATE() or sc.delayedtest >= CURDATE()) order by sc.schoolname, t.teacher_name, s.conditions asc";
					ResultSet rs6 = stat6.executeQuery(sql6);
					while(rs6.next()){
						System.out.println(rs6.getString("studentid"));
						Teacher t = new Teacher(rs6.getString("teacher_name"),rs6.getString("class_name"),school_study_id.get(i),rs6.getString("windowslog_dir"),rs6.getString("maclog_dir"),rs6.getString("schoolname"));
						Student st = new Student(rs6.getString("studentid"),t,rs6.getString("pretest"),rs6.getString("posttest"),rs6.getString("delayedtest"),rs6.getString("intervention_from"),rs6.getString("intervention_to")
								,rs6.getString("conditions"),rs6.getString("pretest_version"),rs6.getString("posttest_version"),rs6.getString("delayedtest_version"));
						studentList.add(st);
						
					}
				}
				createConfigurationFile(studentList,studyName);
				
				
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
				
	}

	

	private void insertDatabase(List<Student> studentList,Connection con) {
		
		String sql = "insert into shortid_table (studentid,study_school_key,teacher_key,conditions,pretest_version,posttest_version,delayedtest_version) values (?,?,?,?,?,?,?)";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			for(int i=0; i<studentList.size(); i++) {
				Student st = studentList.get(i);
				ps.setString(1, st.getStudentID());
				ps.setInt(2, st.getTeacher().getStudyschoolID());
				ps.setInt(3, st.getTeacher().getTeacherID());
				ps.setString(4, st.getCondition());
				ps.setString(5, st.getPreTestVersion());
				ps.setString(6, st.getPostTestVersion());
				ps.setString(7, st.getDelayedTestVersion());
			    ps.executeUpdate();
			    ps.clearParameters();
			
			}
		
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createConfigurationFile(List<Student> studentList, String studyName) {
		try {
			
			File file = new File("/usr/local/apache-tomcat-8.0.38/webapps/Servlet/StudySessionConfiguration.csv");
			if(file.exists()){
				DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
				java.util.Date date = new java.util.Date();
				file.renameTo(new File("/usr/local/apache-tomcat-8.0.38/webapps/Servlet/StudySession-Archive/StudySessionConfiguration_"+df.format(date)+".csv"));
			}
			FileWriter fw = new FileWriter(file);
			for(int i=0; i<studentList.size(); i++) {
				Student st = studentList.get(i);
				//int id = st.getTeacher().getStudyschoolID();
				System.out.println(studentList.get(i).getStudentID() + "	"+ studentList.get(i).getCondition()+"	"+studentList.get(i).getPreTestVersion()
							 +"	"+studentList.get(i).getPostTestVersion()+"	"+studentList.get(i).getDelayedTestVersion());
				fw.append(st.getStudentID());
				fw.append(",");
				fw.append(st.getInterventionFrom());
				fw.append(",");
				fw.append(st.getInterventionTo());
				fw.append(",");
				fw.append(st.getPretestDate());
				fw.append(",");
				fw.append(st.getPosttestDate());
				fw.append(",");
				fw.append(st.getDelayedtestDate());
				fw.append(",");
				fw.append(st.getCondition());
				fw.append(",");
				fw.append(st.getPreTestVersion());
				fw.append(",");
				fw.append(st.getPostTestVersion());
				fw.append(",");
				fw.append(st.getDelayedTestVersion());
				fw.append(",");
				fw.append(st.getTeacher().getSchoolName());
				fw.append(",");
				fw.append(st.getTeacher().getTeacherName()+"-"+st.getTeacher().getClassname());
				fw.append(",");
				fw.append(st.getTeacher().getWindowsLog());
				fw.append(",");
				fw.append(st.getTeacher().getMacLog());
				fw.append(",");
				fw.append(studyName+"("+st.getTeacher().getSchoolName()+")");
				fw.append("\n");

				}
			  fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		
	}

	private void testVersionAssignment(List<Student> studentList,List<String> conditions, String[] versions) {
		List<List<Student>> groups = new ArrayList<List<Student>>();
		int len = versions.length;
		for(int i=0; i<conditions.size(); i++)
			groups.add(divideStudents(studentList,conditions.get(i)));
		
		studentList.clear();
		
		for(int i=0; i<groups.size(); i++) {
	        List<Student> subgroup = groups.get(i);
			for(int j=0; j< subgroup.size() ; j++) {
				subgroup.get(j).setPreTestVersion(versions[j%len].charAt(0)+"");
				subgroup.get(j).setPostTestVersion(versions[j%len].charAt(1)+"");
				subgroup.get(j).setDelayedTestVersion(versions[j%len].charAt(2)+"");
			}
			studentList.addAll(subgroup);
		}
	}

	private List<Student> divideStudents(List<Student> studentList,String condition) {
		 List<Student> tmp = new ArrayList<Student>();
		 for(int i=0; i<studentList.size(); i++) {
			 if(studentList.get(i).getCondition().equals(condition))
				 tmp.add(studentList.get(i));
		 }
			 
		return tmp;
	}
	private void schoolLevelAssignment(List<Student> studentList, List<String> conditions,List<Integer> conditionCount) {
		
	}

	private void classLevelAssignment(List<Student> studentList, List<String> conditions,List<Integer> conditionCount) {
		
	}

	private void studentLevelAssignment(List<Student> studentList, List<String> conditions,List<Integer> conditionCount) {
		int len = conditions.size();
		for(int i=0; i<studentList.size(); i++) {
			studentList.get(i).setCondition(conditions.get(i%len));
			conditionCount.set(i%len, conditionCount.get(i%len)+1);
		}
	}

	private void generateUserID(List<Teacher> teacherList,List <Student> studentList, HashSet<String> shortID) {
		List<Integer> suffix = new ArrayList<Integer>();
		String uniqueID = "";
		int k = 0;
		for(int i= 703; i<= 18278; i++)
			 suffix.add(i);
		Collections.shuffle(suffix);
		
		for(int i=0; i<teacherList.size(); i++) { 
			System.out.println(" Teacher ID : "+teacherList.get(i).getTeacherID()+"  Size "+teacherList.get(i).getNumOfStudents());
			for(int j=0; j<teacherList.get(i).getNumOfStudents(); j++) {
				while(true) {
					uniqueID = teacherList.get(i).getPrefix()+convertNumberToString(suffix.get(k));
					k = (k == 17575) ? 0 : k+1;
					if(!shortID.contains(uniqueID))
						break;
						
				}	 
				//System.out.println("Short ID : "+uniqueID);
				studentList.add(new Student(uniqueID,teacherList.get(i)));
			}	
		}
	}
	
	

	private String convertNumberToString(int suffix) {
		int r ;
		String str = "";
		while(suffix > 0) {
			suffix--;
			r = suffix % 26;
			suffix = suffix / 26;
			str = (char)('A'+r) + str;
		}
		
		return str;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	
}
