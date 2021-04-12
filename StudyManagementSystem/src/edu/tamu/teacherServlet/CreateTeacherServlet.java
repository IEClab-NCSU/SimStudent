package edu.tamu.teacherServlet;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import edu.tamu.config.Config;



@WebServlet("/CreateTeacherServlet")
public class CreateTeacherServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static String JDBC_DRIVER="";
	private static String DB_URL = "";
	private static String user = "";
	private static String password = "";
	
	
    public CreateTeacherServlet() {
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
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
		String teachername = request.getParameter("teachername");
		
		String className = request.getParameter("classname");
		
		int students = Integer.parseInt( request.getParameter("students") );
		
		int studySchoolKey = Integer.parseInt( request.getParameter("studySchoolKey") );
    	
		try {
			Class.forName(JDBC_DRIVER);
			Connection conn = (Connection) DriverManager.getConnection(DB_URL,user,password);
			
			String sql = "insert into teacher (study_school_key, teacher, class_name, no_of_students) values (?,?,?,?)";
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setInt(1, studySchoolKey);
			ps.setString(2, teachername);
			ps.setString(3, className);
			ps.setInt(4, students);
			
			ps.executeUpdate();
			if(ps != null) {
				ps.close();
			}
			
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

