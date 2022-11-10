import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.cmu.pact.Utilities.trace;

/**
 * Call URLDecode on stdin.
 */
public class urldecode {

	/** */
	private static String encoding = "UTF-8";

	/** Whether to format XML with indents, etc. */
	private static boolean prettyPrint = true;

	/** Pattern to match start of an XML element. */
	private static final Pattern startOfXML = Pattern.compile("(<\\?xml[^?]*\\?>)?<log_.*");

	/** Pattern to match end of an XML element. */
	private static final Pattern endOfXML = Pattern.compile(".*</log_action>.?.?$");

	/** To remove XML prologues. */
	private static final Pattern xmlPrologue = Pattern.compile("<\\?xml[^?]*\\?>");

	/** Pretty-printer. */
	private static final XMLOutputter outputter = new XMLOutputter(Format.getCompactFormat()
			.setOmitDeclaration(false).setLineSeparator("\r\n").setIndent("  "));

	/**
	 * Read stdin and decode each line.
	 * @param args optional argument is encoding; default is 
	 */
	public static void main(String[] args) {
		boolean decode = true;
		for (String arg : args) {
			if (arg.toLowerCase().startsWith("-h"))
				usageExit(null, null);
			else if (arg.toLowerCase().startsWith("-p"))
				prettyPrint = false;
			else if (arg.toLowerCase().startsWith("-d"))
				decode = false;
			else
				encoding = arg;
		}
		int lineNo = 1;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			for (String line = null; null != (line = in.readLine()); lineNo++) {
				String decoded = null;
				try {
					if(decode)
						decoded = URLDecoder.decode(line, encoding);
					else
						decoded = line;
				} catch(Exception e) {
					e.printStackTrace();
					decoded = line;
				}
//				System.err.println("decoded:\n  "+decoded);
				if(!prettyPrint)
					trace.out(decoded);
				else {
					Matcher ms = startOfXML.matcher(decoded), me = endOfXML.matcher(decoded);
					boolean s = ms.find(), e = me.find();
//					System.err.printf("ms.find %b, start %d; me.find %b, end %d; len %d\n",
//							s, (s ? ms.start() : -1), e, (e ? me.end() : -1), decoded.length());
//					if(!s || !e || ms.start() > 2 || Math.abs(decoded.length() - me.end()) > 2)
//						baos.write(decoded.getBytes("UTF-8"));
//					else
					{
						baos.write("<root>".getBytes("UTF-8"));
						int end = 0;
						for(Matcher mp = xmlPrologue.matcher(decoded); mp.find(); end = mp.end())
							baos.write(decoded.substring(end, mp.start()).getBytes("UTF-8"));
						baos.write(decoded.substring(end).getBytes("UTF-8"));
						baos.write("</root>".getBytes("UTF-8"));
					}	    			
				}
			}
			if(prettyPrint) {
				if(baos.size() <= "<root>".getBytes().length) {
					System.out.write(baos.toByteArray());
					trace.out();
				} else {
					SAXBuilder builder = new SAXBuilder();	
					ByteArrayInputStream is = new ByteArrayInputStream(baos.toByteArray());
					Document doc = builder.build(is);
					outputter.output(doc, System.out);
				}
			}
		} catch (Exception e) {
			usageExit(e, new Integer(lineNo));
		}
	}

    /**
     * Print a usage message and call {@link System#exit(int)}.
     * @param e exception
     * @param lineNo
     * @return never returns
     */
    private static void usageExit(Throwable e, Integer lineNo) {
    	if (e != null)
    		e.printStackTrace();
    	System.err.printf("%s%sUsage:\n"+
    			"  java -cp . urldecode [-h] [-d] [-p] [encoding] < {inputfile}\n"+
    			"where--\n"+
    			"  -h means print this help message and exit;\n"+
    			"  -d means do not decode the input;\n"+
    			"  -p means do not pretty-print the XML;\n"+
    			"  encoding is the character encoding (default %s).\n"+
    			"Reads stdin.\n",
    			(lineNo != null ? "At line "+lineNo+": " : ""),
    			(e != null ? e.toString()+". " : ""),
    			encoding);
    	System.exit(1);
    }
}

