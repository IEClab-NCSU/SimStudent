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
import org.json.simple.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;



@WebServlet("/FindDetailsByTeacherServlet")
public class FindDetailsByTeacherServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	final String JDBC_DRIVER="com.mysql.jdbc.Driver";
	final String DB_URL = "jdbc:mysql://kona.education.tamu.edu:3306/studymanagement";
	final String user = "simstudent";
	final String password = "simstudent";
	
	
    public FindDetailsByTeacherServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
	
    
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
		String teachername = request.getParameter("teachername");
    	
		try {
			Class.forName(JDBC_DRIVER);
			Connection conn = (Connection) DriverManager.getConnection(DB_URL,user,password);
			
			String sql = "select study_school_key, class_name, no_of_students from teacher where teacher_name=?";
			PreparedStatement statement = (PreparedStatement) conn.prepareStatement(sql);
			statement.setString(1, teachername);
			ResultSet rs = statement.executeQuery();
			
			JSONObject jsonobj = null;
			if(rs.next()) {
				jsonobj = new JSONObject();
				int studySchoolKey = rs.getInt("study_school_key");
				jsonobj.put("studySchoolKey", studySchoolKey);
				
				String className = rs.getString("class_name");
				jsonobj.put("className", className);
				
				String numbers = rs.getString("no_of_students");
				jsonobj.put("numbers", numbers);
				
			}
			
			
			if(statement != null) {
				statement.close();
			}
			if(rs != null) {
				rs.close();
			}
			
		
			
			//return json data	
			
			if(conn != null) {
				conn.close();
			}
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(jsonobj.toString());
			
		
		} catch (SQLException | ClassNotFoundException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
    }
	
}
