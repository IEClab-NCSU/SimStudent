package edu.tamu.config;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;

public class Config {
	
	
	public static Map<String, String> config;
	
	public static Map<String, String> getConfig(ServletConfig servletConfig) {
		
		System.out.println(" Initializing the servlet");
        Properties prop = new Properties();
		String propUrl = (String) servletConfig.getServletContext().getAttribute("propUrl");
		
		config = new HashMap<String, String>();
		
		try {
	        URL url = new URL(propUrl);
	        InputStream in = url.openStream();
	        
	        prop.load(in);
	        in.close();
	        
	        config.put("jdbcDriver", prop.getProperty("DB_DRIVER"));
	        config.put("database", prop.getProperty("DB_URL"));
	        config.put("dbUser", prop.getProperty("DB_USER"));
	        config.put("dbPassword", prop.getProperty("DB_PASSWORD"));
	        config.put("generatedFilePath", prop.getProperty("GENERATED_FILE_PATH"));
	        config.put("generatedFileName", prop.getProperty("GENERATED_FILE_NAME"));
	    
		} catch (Exception e) {
			
			config.put("jdbcDriver", servletConfig.getServletContext().getInitParameter("jdbcDriver"));
	        config.put("database", servletConfig.getServletContext().getInitParameter("database"));
	        config.put("dbUser", servletConfig.getServletContext().getInitParameter("dbUser"));
	        config.put("dbPassword", servletConfig.getServletContext().getInitParameter("dbPassword"));
	        config.put("generatedFilePath", servletConfig.getServletContext().getInitParameter("generatedFilePath"));
	        config.put("generatedFileName", servletConfig.getServletContext().getInitParameter("generatedFileName"));
	        
		}
		
		return config;
	}

}
