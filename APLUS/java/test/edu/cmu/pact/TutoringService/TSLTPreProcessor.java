/* TSLT Preprocessor, prepares log files for the Tutoring Service Load Test:
 * PreProcessLog (3 arguments: see usage)
 * Takes: a console-trace-file with "tsltsp" & "tsltstp" debug traces turned on.
 * The console-trace-file should be of exactly one session from start to finish.
 * Outputs:
 * An input file which corresponds to all the Tool request made during the session
 * A comp file which has all the socketproxy/socketoolproxy msgs of the session.
 * 
 * PreProcessHTML: (4 arguments: see usage)
 * Prepares the TSLTTemplate.html file.
 * 
 * Written by Borg "The White Shadow" Lojasiewicz 7/10/09
 */
package edu.cmu.pact.TutoringService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import edu.cmu.pact.Utilities.trace;

public class TSLTPreProcessor {
	private static final String HTML_FILE = "TSLTtemplate.html";
	private static String usage =  "To preprocess the trace file (copy/pasted from console):\n"
		+"Enter 3 arguments: console-trace-file, logname for comparison, and inputXMLname for flash\n"
		+"To preprocess the html file embedded with flash:\n"
		+"Enter 4 Arguments: the console-trace-file and the tag and the port number for TSLTListener\n"
		+"and the user_guid";
	//private static boolean makeLogForCompare = false;
	public static void main (String args[])
	{
		if(args.length == 3){
			preProcessLog(args[0], args[1], args[2]);
		}else if(args.length ==4){
			preProcessHTML(args[0], args[1],args[2],args[3]);
		}else
		{
			System.out.println(usage);	
		}
	}
	
	public static void preProcessLog(String traceFile, String logForCompName, String inputXMLName){
		String tempLogString = "";
		String logForCompare = "<?xml version=\"1.0\" standalone=\"yes\"?>\n<messages BRDFileName=\"myBRDFileName\">\n";
		String inputXML = "<?xml version=\"1.0\" standalone=\"yes\"?>\n<messages>\n";
		boolean sawStartStateEnd = false;
		boolean foundQuestionFile = false;
		String line;
		String temp;
		int i,k;
		trace.out("tslt", "Entered preprocesslog with arguments: " + traceFile + ", " + logForCompName + ", " + inputXMLName);
		try{
			BufferedReader br = new BufferedReader(new FileReader(new File(traceFile)));
			while(br.ready()){
				tempLogString = tempLogString + br.readLine() + '\n';
			}
			String[] tsltloglines = tempLogString.split("\n");
			for(i = 0; i < tsltloglines.length; i++)
			{
				line = tsltloglines[i];
				if(!foundQuestionFile){
					if((k=line.indexOf("<question_file>"))!=-1){
						temp = line.substring((k + "<question_file>".length()), line.indexOf("</question_file>"));
						if(temp.contains(".brd")){
							foundQuestionFile= true;
							trace.out("tslt", "question file found. Name = " + temp);
							logForCompare = logForCompare.replace("myBRDFileName", temp);
						}
					}else if((k=line.indexOf("ProblemName=\""))!=-1){
						temp = line.substring((k+"ProblemName=\"".length()));
						temp = temp.substring(0, temp.indexOf("\""));
						if(temp.contains(".brd")){
							foundQuestionFile= true;
							System.out.println("question file found via problemname qf=" + temp);
							trace.out("tslt", "question file found. Name = " + temp);
							logForCompare = logForCompare.replace("myBRDFileName", temp);
						}
					}
				}
				if(line.indexOf("+tslt") < 0){
					continue;
				}
				if(sawStartStateEnd){
					if(line.contains("<message>")){
						if(line.indexOf("+tsltsp+") >= 0){
							line = line.substring(line.indexOf("<message>"));
							if(line.contains(">InterfaceAction<")){
								inputXML +=  line + '\n';
							}
						}else{
							line = line.substring(line.indexOf("<message>"));
						}
						logForCompare += line + '\n';
					}
				}else{
					if(line.contains("<MessageType>StartStateEnd")){
						trace.out("tslt", "Saw startstateend");
						sawStartStateEnd = true;
					}
				}
			}
			FileWriter logForComparefw = new FileWriter(logForCompName);
			logForComparefw.write(logForCompare.trim()+"\n</messages>");
			logForComparefw.close();
			trace.out("tslt", "Wrote file: " + logForCompName);
			FileWriter inputXMLfw = new FileWriter(inputXMLName);
			inputXMLfw.write(inputXML.trim()+"\n</messages>");
			inputXMLfw.close();
			trace.out("tslt", "Wrote file: " + inputXMLName);
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(usage);
		} 
	}
	
	public static void preProcessHTML(String consoleTraceFile, String tag, String listenerPort, String user_guid) {
		String brdFileName = "";
		String html = "";
		String logForCompFile = consoleTraceFile + ".comp";
		trace.out("tslt", "Entered preprocessHTML with args : " 
				+ consoleTraceFile + ", " + tag + ", " + listenerPort + " , user_guid arg: " + user_guid);
		try {
			//Following code extracts brd filename from second line of .comp file
			BufferedReader br = new BufferedReader(new FileReader(new File(logForCompFile)));
			if(br.ready()){
				br.readLine();
				brdFileName = br.readLine();
			}
			br.close();
			if(!brdFileName.contains(".brd")){
				trace.out("tslt", "Couldn't find brd filename");
				System.out.println("Couldn't find brd filename");
				System.out.println(usage);
				return;
			}
			brdFileName=brdFileName.substring(brdFileName.indexOf("BRDFileName="));
			brdFileName=brdFileName.substring("BRDFileName=\"".length(), brdFileName.length()-2);
			trace.out("tslt", "BRDfilename = " + brdFileName);
			br = new BufferedReader(new FileReader(new File(HTML_FILE)));
			while (br.ready())
			{
				html+= br.readLine();
			}
			br.close();
			html = html.replace("my_user_guid", user_guid);
			html = html.replace("my_question_file", brdFileName);
			html = html.replace("myInputURL", "http://localhost:"+listenerPort);
			html = html.replace("myLogFileName", consoleTraceFile);
			html = html.replace("my_session_id", tag);
			FileWriter htmlFW = new FileWriter("TSLTtemplate.html");
			htmlFW.write(html);
			htmlFW.close();
			trace.out("tslt", "Done preprocessing HTML file");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(usage);
		} 
	}
}
