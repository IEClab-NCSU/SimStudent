package edu.tamu.teacherServlet;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import edu.tamu.config.Config;



@WebServlet("/FindDetailsByTeacherServlet")
public class FindDetailsByTeacherServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static String JDBC_DRIVER="";
	private static String DB_URL = "";
	private static String user = "";
	private static String password = "";
	
	
    public FindDetailsByTeacherServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
	
	public void init(ServletConfig servletConfig) {
    	System.out.println("Calling Init");
		
		Map<String, String> config = Config.getConfig(servletConfig);
		
    	JDBC_DRIVER = config.get("jdbcDriver");
    	DB_URL = config.get("database");
    	user = config.get("dbUser");
    	password = config.get("dbPassword");
    }
    
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
		String teachername = request.getParameter("teachername");
    	
		try {
			Class.forName(JDBC_DRIVER);
			Connection conn = (Connection) DriverManager.getConnection(DB_URL,user,password);
			
			String sql = "select study_school_key, class_name, no_of_students from teacher where teacher=?";
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
