/**
 * Copyright 2014 Carnegie Mellon University.
 */

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 */
public class ServletLogServer extends HttpServlet 
{
	private static final long serialVersionUID = -943804602262571161L;

	/**
     * Class to do the work.
     */
    class MyLogger extends LogWriterForwarder 
    {
        /**
		 * 
		 */
		private static final long serialVersionUID = -2036100505957540403L;

		/**
         * Implement the abstract method to use the response object for our reply.
         */
        protected boolean sendResponse(MessageReplyPair mrp, String responseText) {
            try {
                HttpServletResponse resp = (HttpServletResponse) mrp.getReplyMechanism();
                byte[] outBuffer = responseText.getBytes(Charset.forName("ISO-8859-1"));
                resp.setBufferSize(512);
                resp.setHeader("Cache-Control", "no-cache");
                resp.setHeader("Pragma", "no-cache");       
                resp.setContentType("text/plain; charset=ISO-8859-1");
                OutputStream outStream = resp.getOutputStream();
                resp.setContentLength(outBuffer.length);
                outStream.write(outBuffer);
                outStream.flush();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return (false);
            }
            
            return (true);
        }
    }

    /** This single object does all the work. */
    private MyLogger logger;

    /**
     * Override to set the log file and log server URL from the initialization
     * parameters LogFileName and LogServerURL, respectively. These could be
     * specified, e.g., in this servlet's element in the web.xml file.
     * Sets {@link #logger} and starts its thread with the name "MyLogger".
     */
    public void init() throws ServletException {
        logger = new MyLogger();
        logger.setFile(new File(getInitParameter("LogFileName")));
        logger.setLogServerURL(getInitParameter("LogServerURL"));
        (new Thread(logger, "MyLogger")).start();
    }

    /**
     * POST action is to log and reply.
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    {
        String msg=null;
        
		try {
			msg = req.getReader().readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        MessageReplyPair mrp = new MessageReplyPair(msg, resp);
        logger.logOrQueueAndReply(mrp);
    }
}