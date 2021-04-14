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
		
		config = new HashMap<String, String>();
		
		if (System.getenv("DB_DRIVER") != null && System.getenv("DB_DRIVER").toString().trim().length() > 0)    
	        config.put("jdbcDriver", System.getenv("DB_DRIVER"));
		else
			config.put("jdbcDriver", servletConfig.getServletContext().getInitParameter("jdbcDriver"));
		
		if (System.getenv("DB_URL") != null && System.getenv("DB_URL").toString().trim().length() > 0)    
	        config.put("database", System.getenv("DB_URL"));
		else
			config.put("database", servletConfig.getServletContext().getInitParameter("database"));
		
		if (System.getenv("DB_USER") != null && System.getenv("DB_USER").toString().trim().length() > 0)    
	        config.put("dbUser", System.getenv("DB_USER"));
		else
			config.put("dbUser", servletConfig.getServletContext().getInitParameter("dbUser"));
		
		if (System.getenv("DB_PASSWORD") != null && System.getenv("DB_PASSWORD").toString().trim().length() > 0)    
	        config.put("dbPassword", System.getenv("DB_PASSWORD"));
		else
			config.put("dbPassword", servletConfig.getServletContext().getInitParameter("dbPassword"));
		
		if (System.getenv("GENERATED_FILE_PATH") != null && System.getenv("GENERATED_FILE_PATH").toString().trim().length() > 0)    
	        config.put("generatedFilePath", System.getenv("GENERATED_FILE_PATH"));
		else
			config.put("generatedFilePath", servletConfig.getServletContext().getInitParameter("generatedFilePath"));
		
		if (System.getenv("GENERATED_FILE_NAME") != null && System.getenv("GENERATED_FILE_NAME").toString().trim().length() > 0)    
	        config.put("generatedFileName", System.getenv("GENERATED_FILE_NAME"));
		else
			config.put("generatedFileName", servletConfig.getServletContext().getInitParameter("generatedFileName"));
		
		return config;
	}

}
