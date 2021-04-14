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



@WebServlet("/FindOneTeacherServlet")
public class FindOneTeacherServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static String JDBC_DRIVER="";
	private static String DB_URL = "";
	private static String user = "";
	private static String password = "";
	
	
    public FindOneTeacherServlet() {
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
    
	@SuppressWarnings({ "unchecked" })
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
		String studyName = request.getParameter("studyName").trim();
		
		String teachername = request.getParameter("teacherName");
		
		String schoolname = request.getParameter("schoolName");
    	
		System.out.println(schoolname);
		System.out.println(teachername);
		System.out.println(studyName);
		
		try {
			Class.forName(JDBC_DRIVER);
			Connection conn = (Connection) DriverManager.getConnection(DB_URL,user,password);
			
			// base on the studyName, we find study_key first
			String sql = "select study_key from study where study_name=?";
			PreparedStatement statement = (PreparedStatement) conn.prepareStatement(sql);
			statement.setString(1, studyName);
			ResultSet rs = statement.executeQuery();
			
			int studyKey = 0;
			if(rs.next()) {
				studyKey = rs.getInt("study_key");
			}
			System.out.println("Study key: " + studyKey);
			if(rs != null) {
				rs.close();
			}
			if(statement != null) {
				statement.close();
			}
			
			// base on the study_key and schoolname we find study_school_key
			String sql1 = "select study_school_key from school where study_key=? and schoolname=?";
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql1);
			ps.setInt(1, studyKey);
			ps.setString(2, schoolname);
			ResultSet rs1 = ps.executeQuery();
			
			int studySchoolKey = 0;
			if(rs1.next()) {
				studySchoolKey = rs1.getInt("study_school_key");
			}
			System.out.println("studySchoolKey" + studySchoolKey);
			if(rs1 != null) {
				rs1.close();
			}
			if(ps != null) {
				ps.close();
			}
			
			// base on the studySchoolKey we find teacher
			String sql2 = "select count(*) from teacher where study_school_key=? and teacher=?";
			PreparedStatement ps1 = (PreparedStatement) conn.prepareStatement(sql2);
			ps1.setInt(1, studySchoolKey);
			ps1.setString(2, teachername);
			ResultSet rs2 = ps1.executeQuery();
			
			int teacherNum = 0;
			while(rs2.next()) {
				teacherNum = rs2.getInt(1);
			}
			
			if(rs2 != null) {
				rs2.close();
			}
			if(ps1 != null) {
				ps1.close();
			}
			
			JSONObject jsonobj = new JSONObject();

			jsonobj.put("teacherNumFormDB", teacherNum);
			jsonobj.put("studySchoolKey", studySchoolKey);
			
			//return json data	
			
			if(conn != null) {
				conn.close();
			}
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(jsonobj.toString());
			
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
