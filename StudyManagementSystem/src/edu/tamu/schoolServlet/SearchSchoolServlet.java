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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.tamu.config.Config;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;



@WebServlet("/SearchSchoolServlet")
public class SearchSchoolServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static String JDBC_DRIVER="";
	private static String DB_URL = "";
	private static String user = "";
	private static String password = "";
	
	
    public SearchSchoolServlet() {
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
