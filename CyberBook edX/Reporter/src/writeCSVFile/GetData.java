package writeCSVFile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.jcraft.jsch.Session;
import dbConnection.GetConnection;
import entity.ConnectionAndSession;


public class GetData {
	
	
	public static void getDataFromOpenEdx() throws Exception {
		
		ConnectionAndSession cs;
		Connection conn = null;
		Session session = null;
		PreparedStatement ps = null;
		ArrayList<String[]> strArrays = new ArrayList<String[]>();
		
		try {
			cs = GetConnection.getConnection();
		
		
			conn = cs.getConn();
			session = cs.getSession();
			
			String sql = "select s.id as anony_table_id, s.anonymous_user_id, s.course_id, s.user_id, t.module_type, t.module_id, t.course_id, "
					+ "t.created, t.modified, t.student_id, p.state, p.student_module_id  from (edxapp.student_anonymoususerid s join "
					+ "edxapp.courseware_studentmodule t on s.user_id=t.student_id) right join "
					+ "edxapp_csmh.coursewarehistoryextended_studentmodulehistoryextended p on p.student_module_id=t.id "
					+ "group by p.id order by t.created;";
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			
			
			while(rs.next()) {
				
				String[] strArray = new String[29];
				// 1. Anon Student Id
				String anonyId = rs.getString("anonymous_user_id");
				// Student Id
				//String userId = rs.getString("user_id");
				String sessionId = rs.getString("student_module_id");
				String created = rs.getString("created");
				String state = rs.getString("state") + "";
				
				strArray[0] = anonyId;
				strArray[1] = sessionId;
				strArray[2] = created;
				
				// try to get json format data: for state column
				JSONParser parser = new JSONParser();
				JSONObject jobj = (JSONObject) parser.parse(state);
				JSONObject stateObj = null;
				if(jobj.containsKey("correct_map") || state.indexOf("correct_map") != -1) {
					if(jobj.get("question_details") != null) {
						stateObj = (JSONObject) parser.parse(jobj.get("question_details").toString());
						System.out.println(stateObj.toString());
						
						// 4. Time Zone 
						strArray[3] = (String) stateObj.get("time zone");
						
						// 5. Student Response Type
						strArray[4] = (String) stateObj.get("student response type");
						
						// 6. Student Response SubType
						strArray[5] = (String) stateObj.get("student response subtype");
						
						// 7. Tutor Response Type
						strArray[6] = (String) stateObj.get("tutor response type");
						
						// 8. Tutor Response Subtype
						strArray[7] = (String) stateObj.get("tutor response subtype");
						
						// 9. Level()
						strArray[8] = (String) stateObj.get("level");
						
						// 10. Problem Name
						strArray[9] = (String) stateObj.get("problemId");
						
						// 10. Problem View
						strArray[10] = (String) stateObj.get("problem view");
						
						// 11. Problem State Time
						strArray[11] = created;
						  
						
						// 13. Step Name
						strArray[12] = (String) stateObj.get("step name");  
						
						// 14. Attempt At Step
						strArray[13] = (String) stateObj.get("attemp at step");
						
						JSONParser parserForCorrect = new JSONParser();
						JSONObject correctMap = (JSONObject) parserForCorrect.parse(jobj.get("correct_map").toString());
						JSONObject mcqsId = (JSONObject) parserForCorrect.parse(correctMap.get("mcqs_id").toString());
						// 15. Outcome
						if( mcqsId.get("correctness").equals("True")) {
							strArray[14] = "correct";
						} else if(mcqsId.get("correctness").equals("False")) {
							strArray[14] = "incorrect";
						} else {
							strArray[14] = "hint";
						}
						
						// 16. Selection
						strArray[15] = (String) stateObj.get("selection");  
						
						//17. Action
						strArray[16] = (String) stateObj.get("Action");  
						
						// 18. input
						strArray[17] = (String) stateObj.get("input"); 
						
						//19. Feedback Text
						strArray[18] = (String) stateObj.get("feedback text"); 
						
						// 20. Feedback Classification
						strArray[19] = (String) stateObj.get("feedback classification"); 
						
						// 21. Help Level
						strArray[20] = (String) stateObj.get("help level");  
						
						// 22. Total Num Hints
						strArray[21] = (String) stateObj.get("total number hints");
						
						// 23. Condition Name
						strArray[22] = (String) stateObj.get("condition name");  
						
						// 24. Condition Type
						strArray[23] = (String) stateObj.get("condition type"); 
						
						// 25. KC ()
						strArray[24] = (String) stateObj.get("kc");  
						
						// 26. KC Category()
						strArray[25] = (String) stateObj.get("kc category");  
						
						// 27. School
						strArray[26] = (String) stateObj.get("school"); 
						
						// 28. Class
						strArray[27] = (String) stateObj.get("class");  // Class
						
						// 29. CF
						strArray[28] = (String) stateObj.get("cf");  // CF ()
						
						strArrays.add(strArray);
					
					} 
					
					else {
						System.out.println("I am inside this loop!");
						// the default XBlock template but not our own XBlock
						strArray[4] = "N/A"; // Student Response Type: DataShop-expected values are ATTEMPT or HINT_REQUEST. 
						strArray[5] = "N/A";  // Student Response Subtype
						strArray[6] = "N/A";  // Tutor Response Type : DataShop-expected values are RESULT or HINT_MSG. 
						strArray[7] = "N/A";  // Tutor Response Subtype
						strArray[8] = "N/A";  // Level ()
						strArray[9] = "N/A";  // Problem Name
						
						// for state column:
						if(state.indexOf("attempts") != -1) {
							strArray[10] = "" + state.charAt(state.indexOf("attempts") + 11); // Problem View
						} else {
							strArray[10] = "0";// Problem View
						}
						
						strArray[11] = created;  // Problem Start Time
						strArray[12] = "N/A";   // Step Name
						strArray[13] = "N/A";   // Attempt At Step
						
						// for outcome column:
						if(state.indexOf("correctness") != -1) {
							if(state.charAt(state.indexOf("correctness") + 15) != 'c') {
								// which means incorrectness
								
								strArray[14] = "incorrect"; // Outcome
							} else {
								strArray[14] = "correct";  // Outcome
							}
						} else {
							strArray[14] = "incorrect";  // Outcome
						}
						
						strArray[15] = "N/A";  // Selection
						strArray[16] = "N/A";  // Action
						strArray[17] = "N/A";  // Input
						
						
						// for Feedback column:
						if(state.indexOf("msg") != -1) {
							// if msg is empty:
							int count = state.indexOf("msg");
							if(state.charAt(count + 6) == '\"' && state.charAt(count + 7) == '\"') {
								strArray[18] = "";  // Feedback Text
							} else {
								strArray[18] = state.substring(count + 7, state.indexOf("answervariable") - 4);  // Feedback Text
							}
						}
						
						strArray[19] = "N/A";  // Feedback Classification
						strArray[20] = "N/A";  // Help Level
						strArray[21] = "N/A";  // Total Num Hints
						strArray[22] = "N/A";  // Condition Name
						strArray[23] = "N/A";  // Condition Type
						strArray[24] = "N/A";  // KC ()
						strArray[25] = "N/A";  // KC Category()
						strArray[26] = "N/A";  // School
						strArray[27] = "N/A";  // Class
						strArray[28] = "N/A";  // CF ()
						
						strArrays.add(strArray);
						
					}
					
				} else {
					// doesn't contain any useful information, just a default XBlock log record:
					// data like these:
					// {"input_state":{"e8a56ca705584c4794658295f63eedb5_2_1":{}},"seed":1}
					System.out.println("This data doesn't contain any useful information.");
				}
				
				
				
			}  
			
			
			//System.out.println(strArrays.size());
			// write all the data to csv file
			CsvFileGenerater.csvFileGenerate(strArrays);

		
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			GetConnection.closeConnection(conn, ps);
			GetConnection.closeSession(session);
		} finally {
			GetConnection.closeConnection(conn, ps);
			GetConnection.closeSession(session);
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		GetData.getDataFromOpenEdx();
	}
}
