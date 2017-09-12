package edu.tamu.schoolServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

/**
 * Servlet implementation class SchoolServlet
 */
@WebServlet("/SchoolServlet")
public class SchoolServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final String JDBC_DRIVER="com.mysql.jdbc.Driver";
	final String DB_URL = "jdbc:mysql://kona.education.tamu.edu:3306/studymanagement";
	final String user = "simstudent";
	final String password = "simstudent";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SchoolServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         String studyName = request.getParameter("studyName");
	     String schoolName = request.getParameter("schoolName");
	         String pretest = request.getParameter("pretest");
	         String inter_from = request.getParameter("inter_from");
	         String inter_to = request.getParameter("inter_to");
	         String posttest = request.getParameter("posttest");
	         String delaytest = request.getParameter("delaytest");
	         String windowsLog = request.getParameter("windowsLog");
	         String macLog = request.getParameter("macLog");
	         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	         System.out.println(" Log : "+windowsLog+" "+macLog + " "+pretest);
	         int school_study_id = -1;
	         int exist = 0;
	         
	         try {
				Class.forName(JDBC_DRIVER);
				Connection con = (Connection) DriverManager.getConnection(DB_URL,user,password);
				
				/** To get the study ID**/
				int studyID = -1;
				Statement stat1 = (Statement) con.createStatement();
				String sql1 = "select study_key from study where study_name=" + "'"+studyName+ "'";
				ResultSet rs1 = stat1.executeQuery(sql1);
				if(rs1.next())
					studyID = rs1.getInt("study_key");
				rs1.close();
				
				/**
				 * Check whether the school exist in the table  
				 * */
				Statement statschool = (Statement) con.createStatement();
				String sqlSchool = "select count(*) from school where schoolname="+ "'"+schoolName+ "'";
				ResultSet rs = statschool.executeQuery(sqlSchool);
				if(rs.next())
					exist = rs.getInt(1);
				
				
				
				
				if(exist > 0){
					
					String sql3 ="update school set study_key=?, pretest=?, intervention_from=?, intervention_to=?, posttest=?, delayedtest=? ,windowslog_dir=? ,maclog_dir=? where schoolname=?";
					PreparedStatement ps = (PreparedStatement)con.prepareStatement(sql3);
					ps.setInt(1, studyID);
					
					java.util.Date preDate = sdf.parse(pretest);
					ps.setDate(2, new java.sql.Date(preDate.getTime()));
					
					java.util.Date inter_fromDate = sdf.parse(inter_from);
					ps.setDate(3, new java.sql.Date(inter_fromDate.getTime()));
					
					java.util.Date inter_toDate = sdf.parse(inter_to);
					ps.setDate(4, new java.sql.Date(inter_toDate.getTime()));
					
					java.util.Date postDate = sdf.parse(posttest);
					ps.setDate(5, new java.sql.Date(postDate.getTime()));
					
					java.util.Date delayDate = sdf.parse(delaytest);
					ps.setDate(6, new java.sql.Date(delayDate.getTime()));
					
					ps.setString(7,windowsLog);
					ps.setString(8,macLog);
					ps.setString(9,schoolName);
					ps.executeUpdate();
					ps.close();
					
				}
				else{
					Statement stat2 = (Statement) con.createStatement();
					String sql2 = "select auto_increment from information_schema.tables where table_name='school' and table_schema=DATABASE()";
					ResultSet rs2=stat2.executeQuery(sql2);
					if(rs2.next())
						school_study_id = rs2.getInt("auto_increment");
					
					String school_study_anon_prefix = GeneratePrefix(school_study_id);
					String sql3 = "insert into school (study_key,schoolname,pretest,intervention_from,intervention_to,posttest,delayedtest,school_study_anon_prefix,windowslog_dir,maclog_dir) values (?,?,?,?,?,?,?,?,?,?)";
					PreparedStatement ps = (PreparedStatement) con.prepareStatement(sql3);
					ps.setInt(1, studyID);
					ps.setString(2,schoolName);
					
					java.util.Date preDate = sdf.parse(pretest);
					ps.setDate(3, new java.sql.Date(preDate.getTime()));
					
					java.util.Date inter_fromDate = sdf.parse(inter_from);
					ps.setDate(4, new java.sql.Date(inter_fromDate.getTime()));
					
					java.util.Date inter_toDate = sdf.parse(inter_to);
					ps.setDate(5, new java.sql.Date(inter_toDate.getTime()));
					
					java.util.Date postDate = sdf.parse(posttest);
					ps.setDate(6, new java.sql.Date(postDate.getTime()));
					
					java.util.Date delayDate = sdf.parse(delaytest);
					ps.setDate(7, new java.sql.Date(delayDate.getTime()));
					
					ps.setString(8, school_study_anon_prefix);
					ps.setString(9,windowsLog);
					ps.setString(10,macLog);
					System.out.println(sql3);
					ps.executeUpdate();
					ps.close();
				}
				
				
				con.close();
				
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	         
	        String submittedStatus = "Successfully submitted!";
	 		HttpSession session = request.getSession();
	 		session.setAttribute("status", submittedStatus);
	 		response.sendRedirect("/StudyManagementSystem/school.jsp");
	        
	}

	private String GeneratePrefix(int school_study_id) {
		
		int r, q;
		if(school_study_id < 0)
			 return "";
		else {
			q = (school_study_id%26 == 0) ? (school_study_id/26)-1   :(school_study_id/26);
			r = (school_study_id%26 == 0) ? 26 : (school_study_id%26) ;
			
			return ""+(char)('A'+q)+(char)(r-1+'A');
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
