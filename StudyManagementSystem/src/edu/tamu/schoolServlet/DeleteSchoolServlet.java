package edu.tamu.schoolServlet;

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
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.tamu.config.Config;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;



@WebServlet("/DeleteSchoolServlet")
public class DeleteSchoolServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static String JDBC_DRIVER="";
	private static String DB_URL = "";
	private static String user = "";
	private static String password = "";
	
	
    public DeleteSchoolServlet() {
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