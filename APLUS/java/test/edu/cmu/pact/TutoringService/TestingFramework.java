package edu.cmu.pact.TutoringService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import edu.cmu.pact.Log.LogFormatUtils;
import edu.cmu.pact.Utilities.trace;

public class TestingFramework {
	
	/** Raw OLI Log of CTAT for Flash-recorded Flash input. */
	private static final String FLASH_LOG = "Weber_20070730111104485.log";
	private static final String HTML_FILE = "/home/borg/Desktop/pact-cvs-tree/AuthoringTools/java/test/edu/cmu/pact/TutoringService/TSLTtemplate.html";
	
	public TestingFramework() {}

	public static void main (String args[])
	{
		if (args.length == 0)
		{
			convertLogToHTML(FLASH_LOG, "");
		}
		else if(args.length==1){
			convertLogToHTML(args[0],"");
		}else if(args.length ==2){
			convertLogToHTML(args[0], args[1]);
		}
	}
	
	public static void convertLogToHTML(String logFile, String tag) {
		String s = "";
		String html = "";
		try {

			BufferedReader br = new BufferedReader(new FileReader(new File(logFile)));
			String line;
			char c;
			while (br.ready())
			{
				//c = (char)fr.read();
				line = br.readLine();
				s = s + line;
			}
			br.close();
			br = new BufferedReader(new FileReader(new File(HTML_FILE)));
			while (br.ready())
			{
				//c = //(char)fr.read();
				line = br.readLine();
				html = html + line;
			}
			br.close();
			String ms = LogFormatUtils.unescape(s);
			trace.out("unescape[1,250]: " + ms.substring(0,249));
			ms = ms.replace("&amp;lt;","<").replace("&amp;gt;", ">");
			trace.out("unamp[1,250]: " + ms.substring(0,249));
			String temp[] = ms.split(".brd");
			String filePath = temp[0].split(">")[temp[0].split(">").length - 1] + ".brd";
			filePath = (filePath.split("\\\\")[filePath.split("\\\\").length - 1]);
			if (!filePath.contains(".brd"))
				trace.out(filePath + ": Log does not contain .brd file path");
			else
				trace.out("filePath: " + filePath);
			
			html = html.replace("tutors/convstackcg1-7_v4.brd", filePath);
			trace.out("myLogFileName = " + logFile+tag);
			html = html.replace("myLogFileName", logFile+tag);
			
			StringBuffer f = new StringBuffer("<?xml version=\"1.0\" standalone=\"yes\"?>\n<tool_messages>");
			String a[] = LogFormatUtils.unescape(s).replace("&amp;lt;","<").replace("&amp;gt;", ">").split("<tool");
			for (int i = 1; i < a.length; i++)
			{
				String nextToolMsg = "<tool" + a[i].split("</tool")[0] + "</tool_message>"; 
				//trace.out("tool_message["+i+"]: " + nextToolMsg);
				if (nextToolMsg == null || nextToolMsg.toLowerCase().contains("tutor-performed"))
					continue;  // sewall 2/1/08: skip tutor-performed msgs
				f.append(nextToolMsg);
			}
			f.append("</tool_messages>");
			FileWriter fw1 = new FileWriter("input.xml");
			FileWriter fw2 = new FileWriter("tsltHTML.html");
			fw1.write(LogFormatUtils.unescape(f.toString()));
			fw2.write(html);
			fw1.close();
			fw2.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
