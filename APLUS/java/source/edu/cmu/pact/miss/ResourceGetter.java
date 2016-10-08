package edu.cmu.pact.miss;

import java.util.Vector;


public class ResourceGetter {

	public String getResource(String problem){
	    	new Exception("you must override ResourceGetter.getResource() with your domain-specific implementation.").printStackTrace();
	        return null;
	    }
}
