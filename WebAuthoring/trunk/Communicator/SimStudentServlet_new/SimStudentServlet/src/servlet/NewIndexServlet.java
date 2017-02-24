package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class NewIndexServlet
 */
@WebServlet("/NewIndexServlet")
public class NewIndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NewIndexServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		String bundle = "/home/simstudent/SimSt Files/SimStudentServlet/build/classes/";
		String bundleName;
		String brdFileName = "";
		String wmeFileName = "";
		if((bundleName = request.getParameter("brdSelect"))!=null){
			bundle += bundleName;
			File pathName = new File(bundle);
			if(pathName.isDirectory()){
				File[] listOfFiles = pathName.listFiles();
				if(listOfFiles.length<1)
					response.getWriter().append("There is no file in the folder.");
				else{
					for(File file: listOfFiles){
						if(!file.isDirectory()){
							//response.getWriter().append(file.getAbsolutePath().toString()+"\n");
							String filePath = file.getCanonicalPath().toString();
							String[] arr;
							if(filePath.matches(".*brd$")){
								arr = filePath.split("/");
								brdFileName = arr[arr.length-1];
							}
							if(filePath.matches(".*wme$")){
								arr = filePath.split("/");
								wmeFileName = arr[arr.length-1];
								arr = wmeFileName.split("\\.");
								wmeFileName = arr[0];
							}
						}
					}
				}
			}
			else
				response.getWriter().append("there is no folder at given path");
			
			response.sendRedirect("http://kona.education.tamu.edu:2401/SimStudentServlet/tutor.html?BRD=http%3A%2F%2Fkona.education.tamu.edu%3A2401%2FSimStudentServlet%2F"+brdFileName+"&ARG=-traceOn+-folder+"+bundleName+"+-problem+"+wmeFileName+"+-ssTypeChecker+"+bundleName+".MyFeaturePredicate.valueTypeChecker&CSS=&INFO=&BRMODE=AuthorTimeTutoring&AUTORUN=on&KEYBOARDGROUP=Disabled&BACKDIR=http%3A%2F%2Fkona.education.tamu.edu%3A2401%2FSimStudentServlet%2Fbuild%2Fclasses&BACKENTRY=interaction.ModelTracerBackend&PROBLEM=xxx&DATASET=FlashLoggingTest_xxx&LEVEL1=Unit1&TYPE1=unit&USER=qa-test&GENERATED=on&SOURCE=PACT_CTAT_HTML5&USEOLI=false&SLOG=true&LOGTYPE=None&DISKDIR=.&PORT=4000&REMOTEURL=serv&SKILLS=&VAR1=xxx_xxx&VAL1=xxx&VAR2=xxx_xxx&VAL2=xxx&VAR3=xxx_xxx&VAL3=xxx&VAR4=xxx_xxx&VAL4=xxx&submit=Launch+HTML5+Tutor");
			
			//response.getWriter().append(bundleName+"<br>").append(brdFileName+"<br>").append(wmeFileName);
		}
			
//			if(brd=="if_p_or_q_then_r.brd")
//				response.sendRedirect("http://kona.education.tamu.edu:2401/SimStudentServlet%20%28Working%29/tutor.html?BRD=http%3A%2F%2Fkona.education.tamu.edu%3A2401%2FSimStudentServlet%2Fif_p_or_q_then_r.brd&ARG=-traceOn+-folder+informallogic+-problem+if_p_or_q_then_r+-ssTypeChecker+informallogic.MyFeaturePredicate.valueTypeChecker&CSS=&INFO=&BRMODE=AuthorTimeTutoring&AUTORUN=on&KEYBOARDGROUP=Disabled&BACKDIR=http%3A%2F%2Fkona.education.tamu.edu%3A2401%2FSimStudentServlet%2Fbuild%2Fclasses&BACKENTRY=interaction.ModelTracerBackend&PROBLEM=xxx&DATASET=FlashLoggingTest_xxx&LEVEL1=Unit1&TYPE1=unit&USER=qa-test&GENERATED=on&SOURCE=PACT_CTAT_HTML5&USEOLI=false&SLOG=true&LOGTYPE=None&DISKDIR=.&PORT=4000&REMOTEURL=serv&SKILLS=&VAR1=xxx_xxx&VAL1=xxx&VAR2=xxx_xxx&VAL2=xxx&VAR3=xxx_xxx&VAL3=xxx&VAR4=xxx_xxx&VAL4=xxx&submit=Launch+HTML5+Tutor");
//			else
//				response.sendRedirect("http://kona.education.tamu.edu:2401/SimStudentServlet%20%28Working%29/tutor.html?BRD=http%3A%2F%2Fkona.education.tamu.edu%3A2401%2FSimStudentServlet%2Fprob1.brd&ARG=-traceOn+-folder+digt_1_3+-problem+prob1+-ssTypeChecker+digt_1_3.MyFeaturePredicate.valueTypeChecker&CSS=&INFO=&BRMODE=AuthorTimeTutoring&AUTORUN=on&KEYBOARDGROUP=Disabled&BACKDIR=http%3A%2F%2Fkona.education.tamu.edu%3A2401%2FSimStudentServlet%2Fbuild%2Fclasses&BACKENTRY=interaction.ModelTracerBackend&PROBLEM=xxx&DATASET=FlashLoggingTest_xxx&LEVEL1=Unit1&TYPE1=unit&USER=qa-test&GENERATED=on&SOURCE=PACT_CTAT_HTML5&USEOLI=false&SLOG=true&LOGTYPE=None&DISKDIR=.&PORT=4000&REMOTEURL=serv&SKILLS=&VAR1=xxx_xxx&VAL1=xxx&VAR2=xxx_xxx&VAL2=xxx&VAR3=xxx_xxx&VAL3=xxx&VAR4=xxx_xxx&VAL4=xxx&submit=Launch+HTML5+Tutor");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
