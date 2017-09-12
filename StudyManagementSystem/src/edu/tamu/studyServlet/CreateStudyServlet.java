package edu.tamu.studyServlet;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

/**
 * Servlet implementation class StudyServlet
 */
@WebServlet("/CreateStudyServlet")
public class CreateStudyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateStudyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		final String JDBC_DRIVER="com.mysql.jdbc.Driver";
		final String DB_URL = "jdbc:mysql://kona.education.tamu.edu:3306/studymanagement";
		final String user = "simstudent";
		final String password = "simstudent";
		
	     try {
	    	 int studyID = -1;
	    	 String studyName = request.getParameter("studyName");
			 String[] condition = request.getParameterValues("condition_name[]");
			 String assignment = request.getParameter("assignment");
			 List<Integer> condition_id = new ArrayList<Integer>();
			 Date javaDate = new Date();
			 java.sql.Date date = new java.sql.Date(javaDate.getTime());
			
			Class.forName(JDBC_DRIVER);
			Connection con = (Connection) DriverManager.getConnection(DB_URL,user,password);
			
			
			Statement stat1 = (Statement) con.createStatement();
			String sql1 = "insert into study (creation_time,study_name,level_of_assignment) values (" + "'"+ date+ "'," + "'" +studyName+ "',"+"'"+assignment+"')";
			stat1.executeUpdate(sql1, PreparedStatement.RETURN_GENERATED_KEYS);
		
			
			/** To get the study ID**/
			/*int studyID = -1;*/
			Statement stat2 = (Statement) con.createStatement();
			String sql2 = "select study_key from study where study_name=" + "'"+studyName+ "'";
			ResultSet rs = stat2.executeQuery(sql2);
			if(rs.next())
				studyID = rs.getInt("study_key");
			rs.close();
			
			/** To get the condition ID **/
			Statement stat3 = (Statement) con.createStatement();
			
			for (int i=0; i<condition.length; i++) {
				System.out.println(condition[i]);
				String sql3 = "select condition_key from conditions where condition_name=" + "'"+condition[i]+ "'";
				ResultSet rs2 = stat3.executeQuery(sql3);
				if(rs2.next())
						condition_id.add(rs2.getInt("condition_key"));
				rs2.close();
			}
			
			/** Insert the Study ID and condition into database **/
			Statement stat4 = (Statement) con.createStatement();
			for (int i=0; i<condition_id.size(); i++) {
				System.out.println(" Condition "+condition[i]+" ID : "+condition_id.get(i));
				String sql4 = "insert into study_condition_mapping (study_key,condition_key) values (" +  "'"+studyID+ "'," +"'"+condition_id.get(i)+"')";
				stat4.executeUpdate(sql4);
			}
			con.close();
			
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
