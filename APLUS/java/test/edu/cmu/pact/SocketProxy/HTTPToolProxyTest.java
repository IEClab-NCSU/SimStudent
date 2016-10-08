package edu.cmu.pact.SocketProxy;

import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class HTTPToolProxyTest extends TestCase {

	private static final String TutoringServiceErrorXML =
			"<message><verb>SendNoteProperty</verb><properties><MessageType>TutoringServiceError</MessageType>"+
			"<ErrorType>Load Problem Error</ErrorType>"+
			"<Details>File Not Found: http://localhost/LinEq/LinEqTutor-Center-Final-Complete-NotFound.brd</Details>"+
			"</properties></message>";
	
	private static final String TutoringServiceErrorJSON =
			"{\"message\": {\n"+
			"  \"verb\": \"SendNoteProperty\",\n"+
			"  \"properties\": {\n"+
			"    \"ErrorType\": \"Load Problem Error\",\n"+
			"    \"Details\": \"File Not Found: http://localhost/LinEq/LinEqTutor-Center-Final-Complete-NotFound.brd\",\n"+
			"    \"MessageType\": \"TutoringServiceError\"\n"+
			"  }\n"+
			"}}";

	private static final String XMLPrologue = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	public void testFormatMsgString() {
		String s = TutoringServiceErrorXML;		
		assertEquals("XML format mismatch", XMLPrologue+s, HTTPToolProxy.formatMsgString(s));
		
		HTTPToolProxy.setOutputJSON(true);
		assertEquals("JSON format mismatch", TutoringServiceErrorJSON, HTTPToolProxy.formatMsgString(s));
    }

    /**
     * Call {@link HTTPToolProxy#setOutputJSON(boolean)} with default setting.
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() {
            HTTPToolProxy.setOutputJSON(false);
    }

    /** XML pretty-printer for test harness. */
    private static final XMLOutputter xmlo = new XMLOutputter(Format.getPrettyFormat().setIndent("  ")
                    .setEncoding("UTF-8").setOmitEncoding(true)
                    .setOmitDeclaration(true).setLineSeparator("\r\n"));

    /**
     * Interactive test harness: converts XML from stdin to JSON on stdout.
     * @param args ignored: reads stdin
     * @throws Exception io error or XML parse error
     */
    public static void main(String[] args) throws Exception {
            Document doc = null;
            try {
                    doc = new SAXBuilder().build(System.in);
            } catch(Exception e) {
                    System.err.printf("%s: input error %s, cause %s. Reads stdin.\n",
                                    HTTPToolProxyTest.class.getSimpleName(), e, e.getCause());
                    System.exit(2);
            }
            String xml = xmlo.outputString(doc);

            HTTPToolProxy.setOutputJSON(true);
            System.out.printf("XML:\n%s\n\nJSON:\n%s\n", xml, HTTPToolProxy.formatMsgString(xml));
    }
}
