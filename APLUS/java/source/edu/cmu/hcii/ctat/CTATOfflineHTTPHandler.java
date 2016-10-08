/** 
 $Author$ 
 $Date$ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 $RCSfile$ 
 $Revision$ 
 $Source$ 
 $State$ 

 -
 License:
 -
 ChangeLog:
 -
 Notes:
   
*/

package edu.cmu.hcii.ctat;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Map;

/**
 * This is what makes "offline mode" possible. It handles all HTTP communication with 
 * browser when running for FIRE in no-internet-needed ("offline") mode.
 */
public class CTATOfflineHTTPHandler extends CTATHTTPLocalHandler implements CTATHTTPHandlerInterface
{		
	private final String filesep;
	private final String templatesPath; // path to directory containing HTML templates
	//private UUID session_id = null;
	
	//private Boolean useTutorShopFormat=true;
	
	/**
	 * @param upd
	 * @param aCurriculum
	 */
	public CTATOfflineHTTPHandler (String aLogFile,
								   UserProgressDatabase upd, 
								   CTATCurriculum aCurriculum)
	{
		super (aLogFile,upd);
		
    	setClassName ("CTATOfflineHTTPHandler");
    	debug ("CTATOfflineHTTPHandler ()");
    	
		this.setCurriculum(aCurriculum);
				
		filesep = System.getProperty("file.separator");
		templatesPath = CTATLink.htdocs + filesep + "templates" + filesep; // path to directory containing HTML templates
		
		if(upd != null)
		{
			CTATLink.userProgress = upd;
		}
		else
		{
			UserProgressDatabase userProgressTemp;
			
			try
			{
				userProgressTemp = new UserProgressDatabase();
			}
			catch(IOException e)
			{
				userProgressTemp = null;
				//OnlineOfflineManager.remainOnline();
			}
			
			CTATLink.userProgress = userProgressTemp;
		}
		
		//session_id = UUID.randomUUID();
	}
		
	/**
	 * Handle an HTTP exchange. This method assumes that the user has already logged into FIRE (which cannot be done while "offline").
	 * @param exchange A CTATHTTPExchange object representing a request that this method will respond to
	 * @return whether or not this method could handle the exchange
	 */
	public boolean handle (CTATHTTPExchange exchange)
	{
		debug ("handle ()");
		
		if(this.getCurriculum() == null)
		{
			debug ("Error: this.getCurriculum() object is null");			
			return false;
		}
		
		if(CTATLink.userProgress == null)
		{
			debug ("Error: user progress database is null");
			return false;
		}		
		
		String requestMethod = exchange.getRequestMethod();
		URI requestURI = exchange.getRequestURI();
		String path = requestURI.getPath();
		
		debug ("Request path: " + path);
				
		//>--------------------------------------------------------------------------
		
		if(path.startsWith("/run_assignment") && !path.endsWith("getpush.cgi"))
		{
			currentAssignment = path.substring(path.lastIndexOf('/')+1);
		}
		
		//>--------------------------------------------------------------------------
		
		if(path.startsWith("/run_student_assignment") && !path.endsWith("getpush.cgi"))
		{			
			String details = path.substring("run_student_assignment".length());
			String[] split = details.split("/");
			
			if (split.length == 3)  // "/studentassignment/problem" splits to { "", "studentassignment", "problem" }
			{
				String studentAssignment = split[1]; // student assignment includes info on user, assignment, and problem set
				String problem = split[2];
				
				// Assume that studentAssignment is also the problem set name. This is true for offline mode 
				// assignments, but not for assignments that originate from the remote server. A change has to 
				// be made either here or on the server to allow for more seamless integration of online and 
				// offline mode.
				
				CTATUserData ua=CTATLink.userProgress.getUser (CTATLink.userID);
				
				if (CTATLink.userID == null)
				{
					exchange.send404("Error: user " + CTATLink.userID + " does not exist");
					return (true);					
				}
								
				if (ua==null)
				{
					exchange.send404("Error: user " + CTATLink.userID + " does not exist");
					return (true);
				}				
				
				if (CTATLink.userProgress==null)
				{
					exchange.send404("Error: internal user database does not exist");
					return (true);					
				}
				
				if (currentAssignment == null)
				{
					debug("received run_student_assignment request when currentAssignment was null; cannot update userProgressDatabase");
				}
					
				try 
				{
					CTATLink.userProgress.setCurrentProblem (CTATLink.userID, currentAssignment, studentAssignment, Integer.valueOf(problem), true);	
				}
				catch (Exception e)
				{ 
					exchange.send404(e.getMessage());
					return (true);
				}
			}
		}		
		
		//>--------------------------------------------------------------------------
		
		boolean handled = false;
		
		if(requestMethod.equalsIgnoreCase("POST"))
		{
			debug ("Processing POST request");
			
			if(path.startsWith("/process_student_assignment"))
			{
				// read in the request body
				byte[] requestBody;
				InputStream in = new BufferedInputStream(exchange.getRequestBody());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int b;
				try
				{
					while((b = in.read()) != -1)
					{
						baos.write(b);
					}
					in.close();
				}
				catch(IOException e) { return false; }
				requestBody = baos.toByteArray();
				
				// queue up the request body to be sent to the server when in online mode
				// OnlineOfflineManager.sendStuffWhenOnline("http://"+CTATLink.remoteHost+path, requestBody);
				
				String currentStudentAssignment = null;
				int currentProblemNumber;
				String[] split = path.split("/");
				
				if(split.length == 4) // should be of the form "/process_student_assignment/studentassignment/problemnumber"
				{
					currentStudentAssignment = split[2];
					
					try
					{
						currentProblemNumber = Integer.valueOf(split[3]);
					} 
					catch (NumberFormatException e)
					{ 
						e.printStackTrace(); return false; 
					}
				} 
				else 
					return false; // URL was not of the proper form
				
				// get the "summary" and "problem_state" fields
				CTATWebTools webTools = new CTATWebTools();
				Map<String, String> map = webTools.parseQuery(new String(requestBody));
				String summary = map.get("summary");
				String skills = map.get("skills");
				String problem_state = map.get("problem_state");
				
				if (skills!=null)
				{
					debug ("We have skills: " + skills);
				}
				
				try
				{
					if(summary != null)
					{
						debug ("We have a problem summary! " + summary);
						
						CTATLink.userProgress.setProblemSummary (CTATLink.userID, 
														this.getCurriculum().getAssignment(currentStudentAssignment),
														currentStudentAssignment,
														currentProblemNumber,
														summary,
														false);
 
						CTATLink.userProgress.setProblemState (CTATLink.userID,
													  this.getCurriculum().getAssignment(currentStudentAssignment),
													  currentStudentAssignment,
													  currentProblemNumber,
													  problem_state,
													  false);
					}	
					else
						debug ("Warning: no problem summary provided by tutor");
					
					CTATLink.userProgress.saveUserProgress (CTATLink.userID);
				} 
				catch(IOException e) 
				{ 
					sendString(exchange, "An error occurred when trying to write user information to disk."); 
				}
				
				// if the problem is incomplete, simply respond with "ok"
				boolean problemComplete = false;
				
				if (summary != null)
				{
					try
					{
						summary = URLDecoder.decode(summary, "UTF-8");
						if(summary.contains("CompletionStatus=\"complete\""))
						{
							problemComplete = true;
						}
					} 
					catch(UnsupportedEncodingException e) { /* ignore; UTF-8 is always supported */ }
				}
				
				if(!problemComplete)
				{
					return sendString(exchange, "ok");
				}
				
				// if the problem is complete, decide which problem (set) to run next and redirect
				int numProblems = this.getCurriculum().getNumberOfProblemsInProbset(currentStudentAssignment);
				
				if(currentProblemNumber + 1 <= numProblems)
				{
					// advance to next problem in this same problem set (sequential problem sequencing).
					try
					{
						CTATLink.userProgress.setCurrentProblem(CTATLink.userID, 
																this.getCurriculum().getAssignment(currentStudentAssignment),
																currentStudentAssignment,
																currentProblemNumber+1,
																true);
					} 
					catch(IOException e) 
					{ 
						e.printStackTrace(); return false; 
					}
					
					handled = redirectTo(exchange, "/start_student_assignment/"+currentStudentAssignment);
				}
				else
				{
					CTATUserData aUser=CTATLink.userProgress.getUser(CTATLink.userID);
					
					// need to go to the next problem set

					// first mark the current problem set as completed by increasing the index of the "current problem"
					try
					{
						CTATLink.userProgress.setCurrentProblem(CTATLink.userID,
																this.getCurriculum().getAssignment(currentStudentAssignment), 
																currentStudentAssignment,
																currentProblemNumber+1,
																true);
						if(aUser != null)
						{
							aUser.milestoneManager.checkProblemSetMilestone(currentStudentAssignment); // current problem set has been completed
							debug("Checking milestone for completion of problem set " + currentStudentAssignment);
						}
					} 
					catch(IOException e) 
					{ 
						e.printStackTrace(); 
						return false; 
					}

					// then go to the next problem set
					String next = this.getCurriculum().getNextStudentAssignment(currentStudentAssignment);
					
					if(next == null) // if this is the last problem set in the assignment
					{	
						if (aUser!=null)
						{
							String assignmentName = this.getCurriculum().getAssignment(currentStudentAssignment);
							aUser.milestoneManager.checkAssignmentMilestone(assignmentName);
							debug("Checking milestone for completion of assignment " + assignmentName);
						}
						else
							debug ("Internal error: unable to find user to check off assignment milestone");
						
						next = "completed"; // if there is no next problem set, the assignment is done.
					}
					
					handled = redirectTo(exchange, "/start_student_assignment/"+next);
				}
			}
			else if(path.equals("/log"))
			{
				if ((CTATLink.remoteHost.isEmpty()==false) && (CTATLink.remoteHost.equalsIgnoreCase("local")==false)) 
				{
					// put the log message in a queue to be sent to the remote server when connectivity (online mode) is regained
					byte[] messageBody;
				
					InputStream in = new BufferedInputStream(exchange.getRequestBody());
				
					int b;
				
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
					try 
					{
						while((b = in.read()) != -1)
						baos.write((byte)b);
					} catch(IOException e) { return false; }
				
					messageBody = baos.toByteArray();
				
					OnlineOfflineManager.sendStuffWhenOnline("http://"+CTATLink.remoteHost+"/log", messageBody);
				
					// return the string "ok" to the client
					handled = sendString(exchange, "ok");
				}
				else
					handled=false;
			}
		}
		else if(requestMethod.equalsIgnoreCase("GET"))
		{
			debug ("Processing GET request");
			
			if(path.equals("/student/home"))
			{
				debug ("Processing /student/home");
				
				String response = CTATLink.fManager.getContents(templatesPath+"home.html");
				handled = sendString(exchange, response);
			}
			else if(path.equals("/student/basics"))
			{
				debug ("Processing /student/basics");
				
				String response = CTATLink.fManager.getContents(templatesPath+"basics.html");
				handled = sendString(exchange, response);
			}
			else if(path.equals("/student/bug"))
			{
				debug ("Processing /student/bug");
				
				String response = CTATLink.fManager.getContents(templatesPath+"bug.html");
				handled = sendString(exchange, response);
			}
			else if(path.equals("/student/asteroid"))
			{
				debug ("Processing /student/asteroid");
				
				String response = CTATLink.fManager.getContents(templatesPath+"asteroid.html");
				handled = sendString(exchange, response);
			}
			else if(path.equals("/student/tutorial"))
			{
				debug ("Processing /student/tutorial");
				
				String response = CTATLink.fManager.getContents(templatesPath+"tutorial.html");
				handled = sendString(exchange, response);
			}			
			else if(path.equals("/student/botsinsync"))
			{
				debug ("Processing /student/botsinsync");
				
				String response = CTATLink.fManager.getContents(templatesPath+"botsinsync.html");
				handled = sendString(exchange, response);
			}
			else if(path.equals("/active")) // browser polls periodically; respond with "true"
			{
				debug ("Processing /active");
				
				handled = sendString(exchange, "true");
			}
			else if(path.equals("/logout"))
			{
				debug ("Processing /logout");
				
				// for now, on logout exit the system (by redirecting to the exit page).
				handled = redirectTo(exchange, "/exittutorshop.cgi");
			}
			else if(path.contains("run_assignment"))
			{
				debug ("Processing run_assignment");
				
				String[] pathSplit = path.split("/");
				String assignmentName = pathSplit[pathSplit.length-1];
				String template = CTATLink.fManager.getContents(templatesPath+"run_assignment.html");
				String studentAssignmentName;

				PositionWithinAssignment pwa = CTATLink.userProgress.getCurrentProblem(CTATLink.userID, assignmentName);
				
				if(pwa != null)
				{
					studentAssignmentName = pwa.problemSet;
				}
				else
				{
					// assume this is the user's first time on this assignment. Start with the assignment's first problem set.
					try
					{
						studentAssignmentName = this.getCurriculum().getFirstProblemSet(assignmentName);
					}
					catch (NumberFormatException e)
					{
						return false;
					}
				}
				
				if(studentAssignmentName == null)
				{
					exchange.sendResponseHeaders(404, 0); 
					exchange.close();
					return true;
				}

				String response = template.replace("STUDENT_ASSIGNMENT_ID", studentAssignmentName);
				response = response.replace("ASSIGNMENT_NAME", assignmentName);
				String exitURL = exchange.getRequestHeaderConcatenated("Referer");
				
				if(exitURL == null) 
					exitURL = "http://"+CTATLink.hostName+":"+CTATLink.wwwPort+"/"; // default to the home page if there is no "referer"
				
				response = response.replace("EXIT_URL", exitURL);
														
				handled = sendString(exchange, response);
			}
			else if(path.contains("start_student_assignment"))
			{
				debug ("Processing start_student_assignment");
				
				String studentAssignmentName = path.substring(path.lastIndexOf("/")+1); // get everything after the last slash
				int problemNumber;
				
				if(studentAssignmentName.equals("completed"))
				{
					String response = CTATLink.fManager.getContents(templatesPath+"student_assignment_completed.html");
					
					if (response==null)
					{
						exchange.send404("Error: template " + templatesPath+"student_assignment_completed.html not found");
						return (true);
					}
					
					CTATUserData aUser=CTATLink.userProgress.getUser(CTATLink.userID);
					
					boolean milestoneHtmlGenerated = false;
					boolean milestonesExist = false;
					
					if (aUser!=null)
					{
						debug("Completed assignment; getting milestone HTML");
												
						String milestoneString=aUser.getMilestoneManager().generateMilestoneHTML();
						
						milestonesExist = (milestoneString != null);
						
						if(milestonesExist)
						{
							String milestonesEncoded=CTATObjectTagDriver.encodeHTML (milestoneString);

							response = response.replace ("MILESTONES", milestonesEncoded);
							milestoneHtmlGenerated = true;

							debug ("Generated html: " + response);
						}
					}
					else
					{
						// leave data-milestones tag as-is so it can be removed later
						
						debug ("Generated html: " + response);					
					}
					
					if(!milestonesExist)
					{
						debug("No milestones; removing data-milestones tag");
						response = response.replaceAll("data-milestones=\'MILESTONES\'", "");
					}
					
					handled = sendString(exchange, response);
					
					if(milestoneHtmlGenerated && handled && aUser != null)
					{
						aUser.milestoneManager.markShown();
					}
					
					return handled;
				}
				
				problemNumber = CTATLink.userProgress.getPositionWithinProblemSet(CTATLink.userID, studentAssignmentName);
				
				if(problemNumber < 0)
					problemNumber = 1; // default to the first problem in the problem set
				
				int numProblemsInSet = this.getCurriculum().getNumberOfProblemsInProbset(studentAssignmentName);
				
				if(numProblemsInSet == 0)
				{
					return sendString(exchange, "Problem set "+studentAssignmentName+" does not include any problems. This may be due to a missing XML file.");
				}
				else if(problemNumber > numProblemsInSet)
				{
					return redirectTo(exchange, "/start_student_assignment/completed");
				}
				
				String template = CTATLink.fManager.getContents(templatesPath+"start_student_assignment.html");
				String response = template.replace("STUDENT_ASSIGNMENT_ID", studentAssignmentName);
				response = response.replace("PROBLEM_NUMBER", ""+problemNumber);
				response = response.replace("HOST_NAME", CTATLink.hostName);
				response = response.replace("PORT_NUMBER", ""+CTATLink.wwwPort);
				
				handled = sendString(exchange, response);
			}
			else if(path.contains("run_student_assignment"))
			{
				debug ("Processing run_student_assignment");
				
				String[] pathSplit = path.split("/");
				String problemSetName = pathSplit[pathSplit.length-2];
								
				int problemNumber;
				
				try
				{
					problemNumber = Integer.valueOf(pathSplit[pathSplit.length-1]);
				}
				catch(NumberFormatException e)
				{
					debug ("NumberFormatException: " + e.getMessage());
					
					return false;
				}
				
				debug ("The student just finished problem number "+problemNumber+" from problem set: "  + problemSetName);

				// don't allow the user to run any problems past the next one they are assigned
				int highestLegalProblemNumber = CTATLink.userProgress.getPositionWithinProblemSet(CTATLink.userID, problemSetName);
				
				if(highestLegalProblemNumber <= 0)
					highestLegalProblemNumber = 1;
				
				if(problemNumber > highestLegalProblemNumber)
				{
					exchange.send404("Not found");
					return true;
				}
				
				String template = CTATLink.fManager.getContents(templatesPath+"run_student_assignment.html");
				
				CTATProblemSet probset=null;
				CTATProblem problem=null;
				
				String problemSetActivationStatus = this.getCurriculum().getProblemSetActivationStatus(problemSetName);
				
				if(problemSetActivationStatus == null)
				{
					sendString(exchange, "<html><body>Error: Problem Set name \"" + problemSetName + "\" is not recognized.<br/>" +
							"<a href=\"http://"+CTATLink.hostName+":"+CTATLink.wwwPort+"/\">Return to main page</a></body></html>");
					return true;
				}
				
				probset = this.getCurriculum().getProblemSet(problemSetName);
				probset.setCurrentIndex(problemNumber-2); // -1 because list of problems starts at index 0
				problem = probset.getNextProblem();
				
				probset.deActivate ();
				
				if (problem == null)
				{
					// assume that the problem set has been completed
					String next = this.getCurriculum().getNextStudentAssignment(problemSetName);
					
					if(next == null) 
						next = "completed"; // if there is no next problem set, the assignment is done.
					return 
						redirectTo(exchange, "/start_student_assignment/"+next);
				}
				else
					problem.setActive(true);

				debug("Replacing tags in template: allowWriting "+CTATLink.allowWriting+", showNavButtons "+CTATLink.showNavButtons);
				
				String problemMenu=directoryToMenu ();
				
				String flashTags=fTagGenerator.generateObjectTags (problem,probset,problemNumber,problemSetActivationStatus);
								
				template=template.replaceFirst ("flashtags",flashTags);
				template=template.replaceFirst ("ProblemSetTitle",problem.name +" : " + probset.getDescription() + " : " + probset.getName());		
				template=template.replaceFirst ("directory.txt",problemMenu);				
									
				handled = sendString(exchange, template);
			}
			else if(path.contains("restore_student_assignment"))
			{
				debug ("Processing restore_student_assignment");
				
				String[] pathSplit = path.split("/");
				
				String studentAssignmentName = pathSplit[pathSplit.length-2];
				
				int problemNumber;
				
				try
				{
					problemNumber = Integer.valueOf(pathSplit[pathSplit.length-1]);
				}
				catch(NumberFormatException e)
				{
					return false;
				}
				
				// don't allow the user to run any problems past the next one they are assigned
				int highestLegalProblemNumber = CTATLink.userProgress.getPositionWithinProblemSet(CTATLink.userID, studentAssignmentName);
				
				if(highestLegalProblemNumber <= 0)
					highestLegalProblemNumber = 1;
				
				if(problemNumber > highestLegalProblemNumber)
				{
					//send404(exchange);
					exchange.send404("Not found");
					return true;
				}
				
				String problem_state = CTATLink.userProgress.getProblemState(CTATLink.userID, this.getCurriculum().getAssignment(studentAssignmentName), studentAssignmentName, problemNumber);
				
				if(problem_state == null)
					return false;
				
				else handled = sendString(exchange, problem_state);
			}
		}
		else if(requestMethod.equalsIgnoreCase("HEAD"))
		{
			debug ("Processing HEAD request (not implemented)");
			
			exchange.sendResponseHeaders(501, 0);
			//exchange.close();
			handled = true;
		}
		else
		{
			handled = false;
		}
		
		debug ("Handled: " + handled);
		
		if (handled==false)
		{
			handled=super.handle(exchange);
		}
		else
			exchange.close();
		
		return handled;
	}
}
