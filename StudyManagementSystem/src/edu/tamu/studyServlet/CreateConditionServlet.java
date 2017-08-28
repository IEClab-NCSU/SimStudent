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
@WebServlet("/CreateConditionServlet")
public class CreateConditionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateConditionServlet() {
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
	    	 String conditionName = request.getParameter("condition");
			
			Class.forName(JDBC_DRIVER);
			Connection con = (Connection) DriverManager.getConnection(DB_URL,user,password);
			
			
			Statement stat1 = (Statement) con.createStatement();
			String sql1 = "insert into conditions(condition_name) values ('" + conditionName + "')" ;
			stat1.executeUpdate(sql1);
			
			if(stat1 != null) {
				stat1.close();
			}
			
			if(con != null) {
				con.close();
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
