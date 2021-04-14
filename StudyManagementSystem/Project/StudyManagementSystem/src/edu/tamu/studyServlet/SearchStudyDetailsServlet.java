package edu.tamu.studyServlet;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import edu.tamu.config.Config;

/**
 * Servlet implementation class StudyServlet
 */
@WebServlet("/SearchStudyDetailsServlet")
public class SearchStudyDetailsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String JDBC_DRIVER="";
	private static String DB_URL = "";
	private static String user = "";
	private static String password = "";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchStudyDetailsServlet() {
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
			
//			String sql = "select * from study s left join study_condition_mapping m on s.study_key = m.study_key "
//					+ "left join conditions c on c.condition_key = m.condition_key where study_name=?";
//			PreparedStatement ps = (PreparedStatement) conn.prepareStatement(sql);
//			ps.setString(1, studyName);
//			ResultSet rs = ps.executeQuery();
			int studyKey = 0;
			String levelOfAssignment = "";
			Statement stat2 = (Statement) conn.createStatement();
			String sql2 = "select study_key, level_of_assignment from study where study_name=" + "'"+studyName+ "'";
			ResultSet rs1 = stat2.executeQuery(sql2);
			if(rs1.next()) {
				studyKey = rs1.getInt("study_key");
				levelOfAssignment = rs1.getString("level_of_assignment");
			}
			rs1.close();
			
			List<Integer> condition_id = new ArrayList<Integer>();
			
			/** To get the condition ID **/
			Statement stat3 = (Statement) conn.createStatement();
			String sql3 = "select condition_key from study_condition_mapping where study_key=" + "'"+studyKey+ "'";
			ResultSet rs2 = stat3.executeQuery(sql3);
			while(rs2.next()){
				condition_id.add(rs2.getInt("condition_key"));
			}
						
			rs2.close();
			
			ArrayList<String> conditions = new ArrayList<String>();
			
			for(int i = 0; i < condition_id.size(); i++) {
				Statement stat4 = (Statement) conn.createStatement();
				String sql4 = "select condition_name from conditions where condition_key=" + "'"+condition_id.get(i)+ "'";
				ResultSet rs3 = stat4.executeQuery(sql4);
				if(rs3.next()) {
					conditions.add(rs3.getString("condition_name"));
				}
			}
			
			//return json data:	
			JSONObject jsonobj = new JSONObject();
			
			jsonobj.put("level_of_assignment", levelOfAssignment);
			jsonobj.put("conditions", conditions);
			
			if(conn != null) {
				conn.close();
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