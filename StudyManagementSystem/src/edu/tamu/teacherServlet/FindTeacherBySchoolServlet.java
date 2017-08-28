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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;



@WebServlet("/FindTeacherBySchoolServlet")
public class FindTeacherBySchoolServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	final String JDBC_DRIVER="com.mysql.jdbc.Driver";
	final String DB_URL = "jdbc:mysql://kona.education.tamu.edu:3306/studymanagement";
	final String user = "simstudent";
	final String password = "simstudent";
	
	
    public FindTeacherBySchoolServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
	
    
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
		String schoolname = request.getParameter("schoolname");
    	
		try {
			Class.forName(JDBC_DRIVER);
			Connection conn = (Connection) DriverManager.getConnection(DB_URL,user,password);
			
			String sql = "select study_school_key from school where schoolname=?";
			PreparedStatement statement = (PreparedStatement) conn.prepareStatement(sql);
			statement.setString(1, schoolname);
			ResultSet rs = statement.executeQuery();
			int studySchoolKey = 0;
			if(rs.next()) {
				studySchoolKey = rs.getInt("study_school_key");
			}
			if(statement != null) {
				statement.close();
			}
			if(rs != null) {
				rs.close();
			}
			
			String sql2 = "select * from teacher where study_school_key=?";
			PreparedStatement ps= (PreparedStatement) conn.prepareStatement(sql2);
			ps.setInt(1, studySchoolKey);
			ResultSet rs1 = ps.executeQuery();
			
			JSONArray jarray = new JSONArray();
			
			//return json data:
			while(rs1.next()) {
				JSONObject jsonobj = new JSONObject();
				String teacherName = rs1.getString("teacher_name");
				jsonobj.put("teacherName", teacherName);
				
				String className = rs1.getString("class_name");
				jsonobj.put("className", className);
				
				String students = rs1.getString("no_of_students");
				jsonobj.put("students", students);
				
				jarray.add(jsonobj);
			}
			
			if(ps != null) {
				ps.close();
			}
			if(rs1 != null) {
				rs1.close();
			}
			
			
			if(conn != null) {
				conn.close();
			}
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(jarray.toString());
			
			if(statement != null) {
				statement.close();
			}
			// then close the database connection
			if(conn != null) {
				conn.close();
			}
		} catch (SQLException | ClassNotFoundException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
}
