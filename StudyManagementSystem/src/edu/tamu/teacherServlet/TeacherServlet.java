package edu.tamu.teacherServlet;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

/**
 * Servlet implementation class TeacherServlet
 */
@WebServlet("/TeacherServlet")
public class TeacherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TeacherServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		final String DB_URL = "jdbc:mysql://kona.education.tamu.edu:3306/studymanagement";
		final String user = "simstudent";
		final String password = "simstudent";
		
		//response.setContentType("text/html");
		//PrintWriter out = response.getWriter();
		//String title = "Successfully submitted";
//		String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
//		out.println(docType + "<html>\n" + "<head><title>" + title + "</title></head>\n"
//				+ "<body bgcolor=\"#f0f0f0\">\n" + "<h1 align=\"center\">" + title + "</h1>\n");
		
		try {
			String studyName = request.getParameter("studyName");
			String schoolName = request.getParameter("schoolName");
			String teacherName = request.getParameter("teacherName");
			String className = request.getParameter("className");
			String numStudents = request.getParameter("noofstudents");
			

			int study_id = -1;
			int study_school_id = -1;
			Class.forName(JDBC_DRIVER);
			Connection con = (Connection) DriverManager.getConnection(DB_URL, user, password);

			/** To get study_school_id from school table ***/
			Statement stat1 = (Statement) con.createStatement();
			String sql1 = "select study_key from study where study_name=" + "'" + studyName + "'";
			ResultSet rs1 = stat1.executeQuery(sql1);
			if (rs1.next())
				study_id = rs1.getInt("study_key");
			System.out.println("Study ID :" + study_id);
			if (study_id > 0) {
				Statement stat2 = (Statement) con.createStatement();
				String sql2 = "select study_school_key from school where study_key=" + study_id + " and schoolname="
						+ "'" + schoolName + "'";
				System.out.println("SQL : " + sql2);
				ResultSet rs2 = stat2.executeQuery(sql2);
				if (rs2.next()) {
					study_school_id = rs2.getInt("study_school_key");
				}
					
			}
			
			if(stat1 != null) {
				stat1.close();
			}
			
			if(rs1 != null) {
				rs1.close();
			}

			System.out.println("Study School ID :" + study_school_id);
			/***
			 * Insert the teacher details into the database
			 */
			if (study_school_id > 0) {
				Statement stat3 = (Statement) con.createStatement();
				String sql3 = "insert into teacher (study_school_key, teacher_name,class_name,no_of_students) values ("
						+ study_school_id + "," + "'" + teacherName + "'," + "'" + className + "'," + numStudents + ")";
				stat3.executeUpdate(sql3);
				if(stat3 != null){
					stat3.close();
				}
			}
			
			
			
			/*******
			 * Check whether the pre-test, intervention , post-test, delay-test
			 * dates are changed. If so we need to update them in school table
			 * (study_id is the primary key)
			 */
			
			//String sql = "select * from school where schoolname=?";
			//PreparedStatement statement = (PreparedStatement) conn.prepareStatement(sql);
			

			con.close();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
