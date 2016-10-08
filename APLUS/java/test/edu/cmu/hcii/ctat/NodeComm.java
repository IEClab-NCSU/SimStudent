/*
 
 */

package edu.cmu.hcii.ctat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author dhruv
 */
public class NodeComm {


    private static final String USER_AGENT = "Mozilla/5.0";

    private String urlString;
    private HttpURLConnection connection;

    /**
     * For interactive use.
     * @param args see {@link #usageExit(String)}
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
    	if(args.length < 1 || args[0].length() < 1)
    		usageExit("Missing URL.");
    	NodeComm nc = new NodeComm(args[0]);
    	BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
    	String line = null;
    	System.err.println("\nEnter lines to send as POST...\n");
    	while(null != (line = rdr.readLine())) {
    		String response = nc.sendData(line);
    		System.out.printf("%s =>\n\t\t\t%s\n\n", line, response);
    	}
    }

    /**
     * Print a usage msg and exit. See the text below for usage.
     * @param errMsg optional error message.
     * @return never
     */
    private static boolean usageExit(String errMsg) {
    	System.err.printf("%s. Usage:\n"+
    			"    java -cp ... %s url\n"+
    			"where--\n"+
    			"    url is the address of the nodejs server.\n",
    			errMsg, NodeComm.class.getName());
    	System.exit(2);
    	return false;  //not reached
	}

    /**
     * 
     * @param urlString - Url to Node server. Example: http://localhost:8888/
     * @throws MalformedURLException
     * @throws IOException 
     */
    //Use this constructor to associate NodeComm object with a target host URL. {http://localhost/}
    public NodeComm(String urlString)throws MalformedURLException, IOException
    {
        
        this.urlString = urlString;
        
    }
    
    /**
     * Start a HTTP Post connection with remote server
     * 
     * @param queryPath - query path on remote host to which POST request should be sent.
     * @throws IOException 
     */
    public void initConnection(String queryPath) throws IOException
    {
        if(queryPath!="" && urlString.charAt(urlString.length()-1)!='/')
        {
            urlString+="/";
        }
        URL url = new URL(urlString+queryPath);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    }

/**
 * Sends the string data as a POST request to the queryPath on server
 * @param queryPath - query path on remote host to which POST request should be sent.
 * @param data
 * @return
 * @throws IOException 
 */
    
    public String sendData(String data,String queryPath) throws IOException {

        initConnection(queryPath);
        connection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(data);
        wr.flush();
        wr.close();

        int responseCode = connection.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
    
    // Overload to send to host without a query path
    public String sendData(String data) throws IOException {
        return sendData(data, "");
    }

    /**
     * Reads a local file and sends file data as a POST request to the queryPath on server.
     * 
     * @param queryPath - query path on remote host to which POST request should be sent.
     * @param fileUrl - Path to local file 
     * @return Response from server
     * @throws IOException 
     */
    public String sendFile(String fileUrl,String queryPath) throws IOException {
        
        initConnection(queryPath);
        String filedata = null;
        String temp ;
        BufferedReader bf = new BufferedReader(new FileReader(fileUrl));

        while ((temp = bf.readLine()) != null) {
            filedata += temp;
        }
        

        connection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(filedata);
        wr.flush();
        wr.close();
       // int responseCode = connection.getResponseCode();
        
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
    
    public String sendFile(String fileUrl) throws IOException {
        return sendFile(fileUrl,"");
    }

}
