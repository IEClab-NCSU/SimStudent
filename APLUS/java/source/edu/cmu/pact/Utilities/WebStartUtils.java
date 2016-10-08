package edu.cmu.pact.Utilities;

import java.net.MalformedURLException;
import java.net.URL;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

public class WebStartUtils {

    /**
	 * Invoke the native browser to show an html file on the current codebase.
	 * @param  path a path (relative to the current codebase) to an html
	 *              page that references the new tutor
	 * @return result of attempt to invoke browser: true if succeeds
	 */
    public boolean showURL(String path) { 
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

		   // Form a new URL with the path appended to the codebase.
		   URL newURL = new URL(codeBase.getProtocol(), codeBase.getHost(),
									 codeBase.getPath() + path);
		   System.out.println("showURL() newURL is " + newURL);

           // Invoke the showDocument method to launch the request
           return bs.showDocument(newURL); 

       } catch(UnavailableServiceException ue) {  // service is not supported
           ue.printStackTrace();
           return false; 
       } catch (MalformedURLException mue) {  // bad path arg or codeBase?
		   mue.printStackTrace();
		   return false;
	   }
    } 

	/**
	 * Driver.
	 * @param  args first entry is URL for browser to invoke
	 */
	public static void main(String[] args) {
		WebStartUtils wsu = new WebStartUtils();
		String urlArg = System.getProperty("ProblemFileLocation");
		if (null == urlArg) {
			if (args.length > 0 && args[0].length() > 0)
				urlArg = args[0];
			else
				throw new IllegalArgumentException("URL must be specified");
		}
		boolean result = wsu.showURL(urlArg);
		System.out.println("showURL(" + urlArg + ") returns " + result);
	}
}
