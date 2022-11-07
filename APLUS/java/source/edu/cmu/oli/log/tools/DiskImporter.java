/* 
 * @(#)DiskImporter.java $Revision: 18655 $ $Date: 2013-01-24 10:59:38 -0500 (Thu, 24 Jan 2013) $
 * 
 * Copyright (c) 2002-2003 Carnegie Mellon University. 
 */ 
package edu.cmu.oli.log.tools;
import edu.cmu.pact.Utilities.trace;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;

/**
 * OLI Logging class for importing XML files from disk
 * @version $Revision: 18655 $ $Date: 2013-01-24 10:59:38 -0500 (Thu, 24 Jan 2013) $
 * @author  Bill Jerome
 * <a href="mailto:wjj@andrew.cmu.edu">(wjj@andrew.cmu.edu)</a>
 */
public class DiskImporter
{
    private URL url; 
    private FileReader infile;
    private Exception lastException;
    private HttpURLConnection conn;
    private SAXTransformerFactory tf;
    private TransformerHandler hd;
    private Transformer serializer;	
    private SimpleDateFormat dateFormat;
    private Vector xmlBuffers;
    private Boolean opened;

    /**
     * Default constructor will set a development server as a URL
     * and set up a file
     */
    public DiskImporter()
    {
    	opened=Boolean.FALSE;
    	try{
	    url=new URL("http://olidev.ote.cmu.edu/log/server");
    	}catch(MalformedURLException ex){lastException=ex;}
        try{ 
            infile = new FileReader("log.dat"); 
        }catch(Exception ex){lastException=ex;} 
    	dateFormat=new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
	xmlBuffers=new Vector();
    }

    public static void main(String[] args) throws IOException {
	if ( (args.length == 1) & (args[0].equals("--help")) ) {
	    trace.out("Usage: DiskImporter [infile [url]]");
	    return;
	}
	DiskImporter dI = new DiskImporter();
	if (args.length > 0) {
	    trace.out("Using file     : " + args[0]);
	    dI.setInfile(args[0]);
	}
	if (args.length > 1) {
	    trace.out("Sending to URL : " + args[1]);
	    dI.setURL(args[1]);
	}
	dI.send();
	trace.out(dI.getLastError().toString());
    }

    /**
     * Set the URL for OLI logging servlet to log to
     * @param  connURL  URL of the servlet
     * @return  <code>TRUE</code> on success, otheriwse <code>FALSE</code>
     */
    public Boolean setURL(String connURL) {
	try {
	    url=new URL(connURL);
	} catch(MalformedURLException ex) {
	    return Boolean.FALSE;
	}
	return Boolean.TRUE;
    }

    /**  
     * Set the input file name 
     * @param  filename  The filename to read XML from 
     * @return  <code>TRUE</code> on success, otherwise <code>FALSE</code>  
     */  
    public Boolean setInfile(String filename) {  
        try {  
            infile = new FileReader(filename); 
        } catch(Exception ex) {  
            return Boolean.FALSE;  
        }  
        return Boolean.TRUE;  
    }  

    private HttpURLConnection getConnection() 
    { 
        HttpURLConnection conn; 
        try{ 
            conn=(HttpURLConnection)url.openConnection(); 
            conn.setDoOutput(true); 
            conn.setRequestMethod("POST"); 
            conn.setRequestProperty("Content-Type", "text/xml"); 
            conn.addRequestProperty("Checksum","It's log, it's log"); 
            conn.connect(); 
        }catch(IOException ex){lastException=ex; return null;} 
        return conn; 
    } 

    public Boolean send() {
	String toSend = "";
	String line = "";
	BufferedReader in = new BufferedReader(infile);
	try {
	    while((line = in.readLine()) != null) {
		toSend = toSend + line;
	    }
	} catch (IOException ex){lastException=ex; return Boolean.FALSE;}
	String[] xmlDocs = toSend.split("<\\?xml");
	//trace.out("FILE:\n" + toSend);
	trace.out("Sending "+xmlDocs.length+" documents ");
	for(int i=1; i<xmlDocs.length; i++) {
            xmlDocs[i] = "<?xml" + xmlDocs[i];
	    //trace.out(xmlDocs[i]);
	   
	    InputStream is;
	    OutputStream os;
	    HttpURLConnection conn=getConnection();
		if(conn==null) {
			System.err.println("\nError opening connection to " + url +
							   ": " + getLastError());
			return Boolean.FALSE;
		}
	    try {
		os = conn.getOutputStream();
	    }catch(IOException ex){lastException=ex;return Boolean.FALSE;} 
	    //trace.out("\n" + xmlDocs[i]);
	    try {
		os.write(xmlDocs[i].getBytes("ISO-8859-1"));
		os.flush();
		is=conn.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		for(int c = -1; 0 <= (c = is.read()); baos.write(c));
		String response = new String(baos.toByteArray());
		if(response != null && response.toLowerCase().contains("success"))
			trace.out(".");
		else
			throw new IOException("Unsuccessful response from server: \""+response+"\"");
		conn.disconnect();
		conn = null;
	    } catch(IOException ex){
	    	lastException=ex;
	    	if(conn != null)
	    		conn.disconnect();
	    	return Boolean.FALSE;	    	
	    }
	}
	trace.out();
	return Boolean.TRUE;
    }

    /**
     * Return the last exception from the class
     * @return Exception object
     */
    public Exception getLastError()
    {
    	return lastException;
    }
    

}
