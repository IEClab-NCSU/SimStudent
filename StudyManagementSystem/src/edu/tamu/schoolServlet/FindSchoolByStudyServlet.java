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



@WebServlet("/FindSchoolByStudyServlet")
public class FindSchoolByStudyServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static String JDBC_DRIVER="";
	private static String DB_URL = "";
	private static String user = "";
	private static String password = "";
	
	
    public FindSchoolByStudyServlet() {
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
    	
		String studyName = request.getParameter("studyName");
    	
		try {
			Class.forName(JDBC_DRIVER);
			Connection conn = (Connection) DriverManager.getConnection(DB_URL,user,password);
			
			String sql = "select study_key from study where study_name=?";
			PreparedStatement statement = (PreparedStatement) conn.prepareStatement(sql);
			
			statement.setString(1, studyName);
			ResultSet rs = statement.executeQuery();
			
			//return json data:
			
			
			String studyKey = "";
			if(rs.next()) {
				studyKey = rs.getString("study_key");
			}
			rs.close();
			
			String sql1 = "select schoolname from school where study_key=?";
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql1);
			ps.setString(1, studyKey);
			ResultSet rs1 = ps.executeQuery();
			
			JSONArray jarray = new JSONArray();
			while(rs1.next()) {
				JSONObject jsonobj = new JSONObject();
				jsonobj.put("schoolname", rs1.getString("schoolname"));
				jarray.add(jsonobj);
			}
			
			// get school name from other studys
			
			String sql2 = "select schoolname from school where study_key <> ? or study_key is null";
			PreparedStatement ps1 = (PreparedStatement)conn.prepareStatement(sql2);
			ps1.setString(1, studyKey);
			ResultSet rs2 = ps1.executeQuery();
			
			//JSONArray jsonarray = new JSONArray();
			while(rs2.next()) {
				JSONObject obj = new JSONObject();
				obj.put("allschoolname", rs2.getString("schoolname"));
				jarray.add(obj);
			}
			
			if(ps != null) {
				ps.close();
			}
			
			if(rs1 != null) {
				rs1.close();
			}
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			
			response.getWriter().write(jarray.toString());
			
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
