package edu.tamu.studyServlet;

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

/**
 * Servlet implementation class StudyServlet
 */
@WebServlet("/FindOneStudyServlet")
public class FindOneStudyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String JDBC_DRIVER="";
	private static String DB_URL = "";
	private static String user = "";
	private static String password = "";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FindOneStudyServlet() {
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

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
//		final String JDBC_DRIVER="com.mysql.jdbc.Driver";
//		final String DB_URL = "jdbc:mysql://localhost:3506/studymanagement";
//		final String user = "root";
//		final String password = "";
		
	     try {
			
			Class.forName(JDBC_DRIVER);
			Connection conn = (Connection) DriverManager.getConnection(DB_URL,user,password);
			
			String studyName = request.getParameter("studyName");
			
			String sql = "select count(*) from study where study_name=?";
			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
			ps.setString(1, studyName);
			ResultSet rs = ps.executeQuery();
			
			
			JSONObject jsonobj = new JSONObject();
			if(rs.next()) {
				int numbers = rs.getInt(1);
				jsonobj.put("numbers", numbers);
				
			}
			if(conn != null) {
				conn.close();
			}
			if(ps != null) {
				ps.close();
			}
			if(rs != null) {
				rs.close();
			}
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(jsonobj.toString());

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		
	}

}