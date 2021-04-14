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
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

import edu.tamu.config.Config;

/**
 * Servlet implementation class StudyServlet
 */
@WebServlet("/UpdateStudyServlet")
public class UpdateStudyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String JDBC_DRIVER="";
	private static String DB_URL = "";
	private static String user = "";
	private static String password = "";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateStudyServlet() {
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
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
//		final String JDBC_DRIVER="com.mysql.jdbc.Driver";
//		final String DB_URL = "jdbc:mysql://localhost:3506/studymanagement";
//		final String user = "root";
//		final String password = "";
		
	     try {
	    	int studyID = -1;
			Class.forName(JDBC_DRIVER);
			Connection conn = (Connection) DriverManager.getConnection(DB_URL,user,password);
			
			String studyName = request.getParameter("studyName");
			String[] condition = request.getParameterValues("condition_name[]");
			String assignment = request.getParameter("assignment");
			List<Integer> condition_id = new ArrayList<Integer>();
			//String flag = request.getParameter("addFlag");
			
			System.out.println("Assignment: " + assignment);
			
			/** To get the study ID**/
			/*int studyID = -1;*/
			Statement stat2 = (Statement) conn.createStatement();
			String sql2 = "select study_key from study where study_name=" + "'"+studyName+ "'";
			ResultSet rs1 = stat2.executeQuery(sql2);
			if(rs1.next()) {
				studyID = rs1.getInt("study_key");
			}
			rs1.close();
			
			// update the assignment first
			Statement stat0 = (Statement) conn.createStatement();
			String sql0 = "update study set level_of_assignment='" + assignment + "' where study_key='" + studyID + "'"; 
			stat0.executeUpdate(sql0);
			if(stat0 != null){
				stat0.close();
			}
			
			// After udpate level_of_assignment, delete all the condition_key from study_condition_mapping table
			Statement stat1 = (Statement) conn.createStatement();
			String sql1 = "delete from study_condition_mapping where study_key='" + studyID + "'";
			stat1.executeUpdate(sql1);
			if(stat1 != null) {
				stat1.close();
			}
			Statement st = null;
			ResultSet rs = null;
			// find all the condition_key from condition table
			for(int i = 0; i < condition.length; i++) {
				st = (Statement) conn.createStatement();
				String sql4 = "select condition_key from conditions where condition_name='" + condition[i] + "'";
				rs = st.executeQuery(sql4);
				if(rs.next()) {
					condition_id.add(rs.getInt(1));
				}
			}
			if(st != null) {
				st.close();
			}
			if(rs != null) {
				rs.close();
			}
			
			System.out.println(condition_id.size());
			
			Statement stat3 = null;
			
			// then start to insert new condition_key into study_condition_mapping table
			for(int i = 0; i < condition_id.size(); i++) {
				stat3 = (Statement) conn.createStatement();
				String sql3 = "insert into study_condition_mapping values('" + studyID + "', '" + condition_id.get(i) + "')";
				stat3.executeUpdate(sql3);
			}
			if(stat3 != null) {
				stat3.close();
			}
			
			
//			*******************************
//			/** To get the condition ID **/
//			Statement stat3 = (Statement) conn.createStatement();
//			String sql3 = "select condition_key from study_condition_mapping where study_key=" + "'"+studyID+ "'";
//			ResultSet rs2 = stat3.executeQuery(sql3);
//			while(rs2.next()){
//				condition_id.add(rs2.getInt("condition_key"));
//			}
//						
//			rs2.close();
//			// condition come from user input
//			HashMap<Integer, String> map = new HashMap<Integer, String>();
//			// how many conditions that the user selected
//			if(condition.length == 2) {
//				map.put(1, "apluscontrol");
//				map.put(2, "metatutor");
//			}
//			if(condition.length == 1) {
//				if(condition[0].equals( "apluscontrol")) {
//					map.put(1, "apluscontrol");
//				}
//				if(condition[0].equals( "metatutor" )) {
//					map.put(2, "metatutor");
//				}
//			}
//			
//			System.out.println("Map Size: " + map.size() + ", condition_id size: " + condition_id.size());
//			
//			
//				// user didn't change the condition
//				
//				if(map.size() == 1 && condition_id.size() == 1) {
//					// user didn't change the condition
//					if(map.get(condition_id.get(0)) == null) {
//						// user changed the condition
//						int value = 0;
//						if(condition_id.get(0) == 1) {
//							value = 2;
//							Statement stat4 = (Statement) conn.createStatement();
//							String sql4 = "update study_condition_mapping set condition_key = '" + value + "' where study_key='" + studyID + "'";
//							stat4.executeUpdate(sql4);
//							stat4.close();
//						} else {
//							value = 1;
//							Statement stat4 = (Statement) conn.createStatement();
//							String sql4 = "update study_condition_mapping set condition_key = '" + value + "' where study_key='" + studyID + "'";
//							stat4.executeUpdate(sql4);
//							stat4.close();
//						}
//					
//					}
//					
//				}
//				
//				// user input 1, but db contains 1, 2 -> delete
//				if(map.size() == 1 && condition_id.size() == 2) {
//					if(condition[0].equals( "apluscontrol" )) {
//						// we have to delete 'metatutor'
//						Statement stat5 = (Statement) conn.createStatement();
//						String sql5 = "delete from study_condition_mapping where condition_key = 2 and study_key='" + studyID + "'";
//						stat5.executeUpdate(sql5);
//						stat5.close();
//					} else {
//						// we have to delete 'apluscontrol'
//						Statement stat6 = (Statement) conn.createStatement();
//						String sql6 = "delete from study_condition_mapping where condition_key = 1 and study_key='" + studyID + "'";
//						stat6.executeUpdate(sql6);
//						stat6.close();
//					}
//					
//				}
//				
//				// user input 1, 2   but db contains 1 -> insert -> works
//				if(map.size() == 2 && condition_id.size() == 1) {
//					if(condition_id.get(0) == 1) {
//						// insert metatutor into db
//						Statement stat6 = (Statement) conn.createStatement();
//						String sql6 = "insert into study_condition_mapping values(" + studyID + ", 2)";
//						stat6.executeUpdate(sql6);
//						stat6.close();
//					} else {
//						Statement stat7 = (Statement) conn.createStatement();
//						String sql7 = "insert into study_condition_mapping values(" + studyID + ", 1)";
//						stat7.executeUpdate(sql7);
//						stat7.close();
//					}
//				}
//			*******************************
			
		
			
			
//			****************************
//			/** Insert the Study ID and condition into database **/
//			Statement stat4 = (Statement) conn.createStatement();
//			for (int i=0; i<condition_id.size(); i++) {
//				System.out.println(" Condition "+condition[i]+" ID : "+condition_id.get(i));
//				String sql4 = "insert into study_condition_mapping (study_key,condition_key) values (" +  "'"+studyID+ "'," +"'"+condition_id.get(i)+"')";
//				stat4.executeUpdate(sql4);
//			}
//			conn.close();
//          ****************************
			
			
			if(conn != null) {
				conn.close();
			}
			

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
