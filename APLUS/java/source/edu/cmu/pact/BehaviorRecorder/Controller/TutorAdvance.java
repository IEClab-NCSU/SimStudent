package edu.cmu.pact.BehaviorRecorder.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import edu.cmu.pact.Utilities.DelayedAction;
import edu.cmu.pact.Utilities.Utils;

public  class TutorAdvance implements ActionListener {
	
//	private String query;

	String urlForResponse = "";
	String userGuid = "";
	String schoolName = "";
	String admitCode = "";
	private String problemName = "";
	private String studentProblemId = "";
   	String advanceProblemQuery;
	String advancePageQuery;
	
	SingleSessionLauncher launcher;
	String cur_stu_int;
   	DelayedAction AutoDoneAction;
   	
	public TutorAdvance()
	{
		
		//we store the string here, do not instantiate as a URL until we are sure the student is done ...
	}
	
	public TutorAdvance(String[] argv, SingleSessionLauncher launcher, JComponent tutor)
	{
    	String[] temp = null;
    	for(int i = 0; i < argv.length; i ++)
    	{
    		temp = argv[i].split("=");
    		if(temp[0].equals("-Dproblem_name"))
    			problemName = temp[1];
    		
    		if(temp[0].equals("-Dstudent_problem_id"))
    			studentProblemId = temp[1];

    		if(temp[0].equals("-Dcurriculum_service_url"))
    			urlForResponse = temp[1];

       		if(temp[0].equals("-Dschool_name"))
       			schoolName = temp[1];
       		
    		if(temp[0].equals("-Dadmit_code"))
    			admitCode = temp[1];
    		
    		if(temp[0].equals("-Duser_guid"))
    			userGuid = temp[1];
    	}
		
		advancePageQuery = 
		"user_guid=" + userGuid + 
		"&school_name=" + schoolName + 
		"&admit_code=" + admitCode + 
		"&cmd=login";

			
		advanceProblemQuery = 
		"user_guid=" + userGuid + 
		"&school_name=" + schoolName + 
		"&admit_code=" + admitCode + 
		"&cmd=doneNextData";
		
		System.out.println("user_guid = [" + userGuid + "]");
    	System.out.println("problemName = [" + problemName + "]");
    	System.out.println("studentProblemId = [" + studentProblemId + "]");
    	System.out.println("advanceProblemQuery = [" + advanceProblemQuery + "]");
    	System.out.println("urlForResponse = [" + urlForResponse + "]");

		this.launcher = launcher;
		cur_stu_int = tutor.getClass().getName();
	}
	
	
	// http://tutors.pslc.cs.cmu.edu:8081/tutorshop/ChineseTutorProblem?user_guid=s1,+student1&school_name=Local&admit_code=pretest&cmd=doneNext

	public void advanceProblem()
	{
		String path = "", parts[], temp[];
		System.out.println("advanceProblem from " + problemName + " = " + studentProblemId);
//		System.out.println("advancePagequery = [" + query + "]");
		path = urlForResponse;
		try { 
	           // Lookup the javax.jnlp.BasicService object 
	           BasicService bs =
				   (BasicService)ServiceManager.lookup("javax.jnlp.BasicService"); 

			   // Invoke the getCodeBase method to get the prefix of the URL.
			   URL codeBase = bs.getCodeBase();
			   System.out.println("showURL() codeBase is " + codeBase +
								  ", path is " + path + ";");
			   if (!codeBase.getPath().endsWith("/") && !path.startsWith("/"))
				   path = "/" + path;
			   
			   String bsquery = codeBase.getQuery();
			   
			   System.out.println("BSquery = " + bsquery);
			   System.out.println("Host = " + codeBase.getHost());
			   System.out.println("Path = " + codeBase.getPath());

			   // Form a new URL with the path appended to the codebase.
			   
//			   URL newURL = new URL(codeBase.getProtocol(), codeBase.getHost(),
//										 codeBase.getPath() + path);
			   
//			   URL newURL = new URL(path);
//				URL newURL = new URL(codeBase.getProtocol(), codeBase.getHost(),codeBase.getPort(),
//						codeBase.getPath().substring(0, codeBase.getPath().indexOf("tutors/") ) + path);
			   
				path = codeBase.getPath().substring(0,
						codeBase.getPath().indexOf("tutors/"))
						+ urlForResponse;
				
		    	URI uri = new URI(codeBase.getProtocol(), null, codeBase.getHost(), 
		    			codeBase.getPort(), path, advancePageQuery, null);	
		    	

				// execute the request, and get the string response
				URL newURL = uri.toURL();

				System.out.println("newURL is " + newURL.toString());

	           // Invoke the showDocument method to launch the request
	           bs.showDocument(newURL); 
	       } catch(UnavailableServiceException una) {  // service is not supported
	    	   una.printStackTrace();
	       } catch(URISyntaxException uri) {  // service is not supported
	           uri.printStackTrace();

	       } catch (MalformedURLException mue) {  // bad path arg or codeBase?
			   mue.printStackTrace();

		   }

	}

	
	public void advanceProblemRepetition()
	{
		LinkedList<String> response = new LinkedList<String>();
		System.out.println("advanceProblemRepetition from " + problemName + " = " +
				studentProblemId);
		String path = "";

		try {
			BasicService bs = (BasicService) ServiceManager
					.lookup("javax.jnlp.BasicService");
			URL codeBase = bs.getCodeBase();
			System.out.println("showURL() codeBase is " + codeBase
					+ ", path is " + path + ";");
			if (!codeBase.getPath().endsWith("/") && !path.startsWith("/"))
				path = "/" + path;
			// Form a new URL with the path appended to the codebase.
			// URL newURL = new URL(path);

			path = codeBase.getPath().substring(0,
					codeBase.getPath().indexOf("tutors/"))
					+ urlForResponse;

			// URL newURL = new URL(codeBase.getProtocol(),
			// codeBase.getHost(),codeBase.getPort(),
			// path + query);
			// codeBase.getPath().substring(0,
			// codeBase.getPath().indexOf("tutors/") ) + path);
			String query = advanceProblemQuery + "&student_problem_id=" +
				(studentProblemId != null && studentProblemId.length() > 0 ?
						studentProblemId : problemName);
			URI uri = new URI(codeBase.getProtocol(), null, codeBase.getHost(),
					codeBase.getPort(), path, query, null);

			// execute the request, and get the string response
			URL newURL = uri.toURL();

			System.out.println("newURL is " + newURL.toString());

			// execute the request, and get the string response

			// String resp = new HttpURLConnection(newURL).getResponseMessage();

			URLConnection conn = newURL.openConnection();
			conn.setUseCaches(false);
			System.out.println("conn is " + conn);

			String line = "";

			BufferedReader br = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			System.out.println("br =  " + br);
			while ((line = br.readLine()) != null) {
				response.add(line);
				System.out.println("line =" + line.toString());
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		;
		// System.out.println("getContent is " + resp.getClass());
		// replace problem name with new problem name in args to pass on to
		// SingleSessionLauncher
		// this is a bit of a hack since we're parsing FlashVars for the string
		// ...
		String new_problem_name = "";
		String new_studentProblemId = "";
		String stu_int = "";
		String[] flashArgs = null;
		int val_index = -1; // reused
		for (int i = 0; i < response.size(); i++) {
			if (response.get(i).indexOf("problem_name") != -1)
				flashArgs = response.get(i).split("&");
		}
		for (int i = 0; i < flashArgs.length; i++) {
			if ((val_index = flashArgs[i].indexOf("problem_name")) != -1)
				new_problem_name = flashArgs[i].substring(val_index);
			if ((val_index = flashArgs[i].indexOf("student_problem_id")) != -1)
				new_studentProblemId = flashArgs[i].substring(val_index);
			// if((val_index = flashArgs[i].indexOf("question_file")) != -1)
			// problem_file = flashArgs[i].substring(val_index);
			if ((val_index = flashArgs[i].indexOf("student_interface")) != -1)
				stu_int = flashArgs[i].substring(val_index);
		}
		new_problem_name = new_problem_name.substring(new_problem_name.indexOf('=') + 1,
				new_problem_name.length());
		new_studentProblemId = new_studentProblemId.substring(new_studentProblemId.indexOf('=') + 1,
				new_studentProblemId.length());

		stu_int = stu_int.substring(stu_int.indexOf('/') + 1, stu_int.length());

		System.out.println("Using interface [" + stu_int + "] (old interface = " +  cur_stu_int + ")");

		// get a new instance of the student interface we'll pass into
		// SingleSessionLauncher
		// String stuInt = problem_name.replace(".brd", "");
		// Class s = this.getClass().getClassLoader().loadClass(stuInt);
		// JComponent tutor = (JComponent)s.newInstance();

		if (!stu_int.equalsIgnoreCase(cur_stu_int)) {
			System.out.println("New student interface, so open new browser, then close this one");
//			try { Thread.sleep(30000); } catch (InterruptedException ie) {}
			advanceProblem();
//			try { Thread.sleep(30000); } catch (InterruptedException ie) {}
			System.exit(0);

		} else {
			// launcher.getController().openBRFromURL(new_problem_name + ".brd");
			System.out.println(" >>> From " + problemName + " = " + studentProblemId +
					" advence to " + new_problem_name);

			if (problemName.equalsIgnoreCase(new_problem_name)) {
	            JOptionPane.showMessageDialog(launcher.getController().getActiveWindow(),
	            		"You are done with this lesson.",
	                    "congratulation", JOptionPane.INFORMATION_MESSAGE);
	            
				System.exit(0);
			}
			else {
				URL url = Utils.getURL(new_problem_name + ".brd", this);

				System.out.println("openBRFromURL = " + url.toString());
				launcher.getController().openBRFromURL(url.toString());

				this.problemName = new_problem_name;
			}
		}

		
//		return AutoDoneAction;
	}

	/**
	 * Calls {@link #advanceProblemRepetition()}.
	 * @param evt successful done action
	 */
	public void actionPerformed(ActionEvent evt) {
		System.out.println("TutorAdvance.actionPerformed("+evt+
				"): calling advanceProblem()");
		advanceProblemRepetition();
	}

/*
    public DelayedAction processAutoDone(int delayTime) {

 //   	final JCommButton DoneButton = Done;

    	DelayedAction dA = new DelayedAction(new Runnable() {
			public void run() {
				System.err.print("Auto Launch ");
				
				Done.doClick();
		        
		        System.err.println(" next Problem");

			}
	});
    	
    		dA.setDelayTime (delayTime);
    		dA.start();
    		return dA;
    }

	public DelayedAction getAutoDoneAction() {
		return AutoDoneAction;
	}
	*/
}
