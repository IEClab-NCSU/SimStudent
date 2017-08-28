package edu.tamu.schoolServlet;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;



@WebServlet("/DeleteSchoolServlet")
public class DeleteSchoolServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	final String JDBC_DRIVER="com.mysql.jdbc.Driver";
	final String DB_URL = "jdbc:mysql://kona.education.tamu.edu:3306/studymanagement";
	final String user = "simstudent";
	final String password = "simstudent";
	
	
    public DeleteSchoolServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
	
    
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
		
    	
		try {
			Class.forName(JDBC_DRIVER);
			Connection conn = (Connection) DriverManager.getConnection(DB_URL,user,password);
			
			String schoolname = request.getParameter("schoolname");
			System.out.println(schoolname);
			
			String sql = "delete from school where schoolname=?";
			PreparedStatement statement = (PreparedStatement) conn.prepareStatement(sql);
			statement.setString(1, schoolname);
			statement.executeUpdate();

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