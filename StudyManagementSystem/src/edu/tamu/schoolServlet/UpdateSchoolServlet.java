package edu.tamu.schoolServlet;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

@WebServlet("/UpdateSchoolServlet")
public class UpdateSchoolServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	final String DB_URL = "jdbc:mysql://kona.education.tamu.edu:3306/studymanagement";
	final String user = "simstudent";
	final String password = "simstudent";

	public UpdateSchoolServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		final String DB_URL = "jdbc:mysql://kona.education.tamu.edu:3306/studymanagement";
		final String user = "simstudent";
		final String password = "simstudent";
//		response.setContentType("text/html");
//		PrintWriter out = response.getWriter();
//		String title = "Successfully updated.";
//		String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
//		out.println(docType + "<html>\n" + "<head><title>" + title + "</title></head>\n"
//				+ "<body bgcolor=\"#f0f0f0\">\n" + "<h1 align=\"center\">" + title + "</h1>\n");
		
		
		
		// schoolName will not change. Once they change the school name, that means need to create a new one.
        String schoolName = request.getParameter("schoolName");
        String pretest = request.getParameter("pretest");
        String inter_from = request.getParameter("inter_from");
        String inter_to = request.getParameter("inter_to");
        String posttest = request.getParameter("posttest");
        String delaytest = request.getParameter("delaytest");
        String windowsLog = request.getParameter("windowsLog");
        String macLog = request.getParameter("macLog");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
     
		try {
			Class.forName(JDBC_DRIVER);
			Connection conn = (Connection) DriverManager.getConnection(DB_URL,user,password);
			
			String sql = "update school set pretest=?, intervention_from=?, intervention_to=?, posttest=?, delayedtest=?, "
					+ "windowslog_dir=?, maclog_dir=? where schoolname=?";
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
			
			java.util.Date preDate = sdf.parse(pretest);
			ps.setDate(1, new java.sql.Date(preDate.getTime()));
			
			java.util.Date inter_fromDate = sdf.parse(inter_from);
			ps.setDate(2, new java.sql.Date(inter_fromDate.getTime()));
			
			java.util.Date inter_toDate = sdf.parse(inter_to);
			ps.setDate(3, new java.sql.Date(inter_toDate.getTime()));
			
			java.util.Date postDate = sdf.parse(posttest);
			ps.setDate(4, new java.sql.Date(postDate.getTime()));
			
			java.util.Date delayDate = sdf.parse(delaytest);
			ps.setDate(5, new java.sql.Date(delayDate.getTime()));
			
			ps.setString(6,windowsLog);
			ps.setString(7,macLog);
			
			ps.setString(8, schoolName);
			
			ps.executeUpdate();
			
			if(ps != null) {
				ps.close();
			}
			
			if(conn != null) {
				conn.close();
			}
			
		} catch (ClassNotFoundException | SQLException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		
       
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}
