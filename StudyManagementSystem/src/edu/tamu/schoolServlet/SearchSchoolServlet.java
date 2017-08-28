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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;



@WebServlet("/SearchSchoolServlet")
public class SearchSchoolServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	final String JDBC_DRIVER="com.mysql.jdbc.Driver";
	final String DB_URL = "jdbc:mysql://kona.education.tamu.edu:3306/studymanagement";
	final String user = "simstudent";
	final String password = "simstudent";
	
	
    public SearchSchoolServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
	
    
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
		
    	
		try {
			Class.forName(JDBC_DRIVER);
			Connection conn = (Connection) DriverManager.getConnection(DB_URL,user,password);
			
			String sql = "select schoolname from school";
			PreparedStatement statement = (PreparedStatement) conn.prepareStatement(sql);
			
			ResultSet rs = statement.executeQuery();
			
			//return json data:
			JSONArray jarray = new JSONArray();
			
			while(rs.next()) {
				JSONObject jsonobj = new JSONObject();
				String schoolName = rs.getString("schoolname");
				jsonobj.put("schoolName", schoolName);
				jarray.add(jsonobj);
			}
			if(conn != null) {
				conn.close();
			}
			if(statement != null) {
				statement.close();
			}
			if(rs != null) {
				rs.close();
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
	
}
