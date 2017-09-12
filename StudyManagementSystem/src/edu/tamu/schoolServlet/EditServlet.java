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
import org.json.simple.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;



@WebServlet("/EditServlet")
public class EditServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	final String JDBC_DRIVER="com.mysql.jdbc.Driver";
	final String DB_URL = "jdbc:mysql://kona.education.tamu.edu:3306/studymanagement";
	final String user = "simstudent";
	final String password = "simstudent";
	
	
    public EditServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
	
    
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
		String schoolName = request.getParameter("schoolName");
    	
		try {
			Class.forName(JDBC_DRIVER);
			Connection conn = (Connection) DriverManager.getConnection(DB_URL,user,password);
			
			String sql = "select * from school where schoolname=?";
			PreparedStatement statement = (PreparedStatement) conn.prepareStatement(sql);
			
			statement.setString(1, schoolName);
			ResultSet rs = statement.executeQuery();
			
			//return json data:
			JSONObject jsonobj = new JSONObject();
			
			if(rs.next()) {
				String pretest = rs.getString("pretest");
				jsonobj.put("pretest", pretest);
				
				
				String intervention_from = rs.getString("intervention_from");
				jsonobj.put("from", intervention_from);
				
				String intervention_to = rs.getString("intervention_to");
				jsonobj.put("to", intervention_to);
				
				String posttest = rs.getString("posttest");
				jsonobj.put("posttest", posttest);
				
				
				String delayedtest = rs.getString("delayedtest");
				jsonobj.put("delayedtest", delayedtest);
				
				String windowsLog = rs.getString("windowslog_dir");
				jsonobj.put("windowslog_dir", windowsLog);
				
				String macLog = rs.getString("maclog_dir");
				jsonobj.put("maclog_dir", macLog);
			}
			rs.close();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(jsonobj.toString());
			
			// then close the database connection
			if(conn != null) {
				conn.close();
			}
			if(statement != null) {
				statement.close();
			}
			if(rs != null) {
				rs.close();
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
