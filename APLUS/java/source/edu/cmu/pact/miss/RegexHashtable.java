package edu.cmu.pact.miss;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/*
 * A special Hashtable in which the keys are regular expressions, and lookups can be done for all regexes
 * which match the given String 
 * Keiser - 12/14/2009
 */
public class RegexHashtable extends Hashtable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Get the first regular expression (in the keys) that the given string matches
	//@return the first regex to match
	public String firstRegexMatch(String str)
	{
		for(Object x:this.keySet())
		{
			if(x instanceof String)
			{
				String regex = (String) x;
				if(Pattern.matches(regex, str))
				{
					return regex;
				}
			}
		}
		return null;
	}

	/*Get all regular expressions (in the keys) that the given string matches
	@return a list of regexes that match
	*/
	public List<String> regexMatches(String str)
	{
		LinkedList<String> matches = new LinkedList<String>();
		for(Object x:this.keySet())
		{
			if(x instanceof String)
			{
				String regex = (String) x;
				if(Pattern.matches(regex, str))
				{
					matches.add(regex);
				}
			}
		}
		return matches;
	}

	//Check if the hashtable contains a regular expression which matches the string
	//@return true if some regex key matches the string, false otherwise
	public boolean containsRegexMatch(String str)
	{
		return firstRegexMatch(str) != null;
	}
	
	//Get the object mapped to the first regex matching the string
	//@return the value mapped to the first regex
	public Object getRegexMatch(String str)
	{
		String match = firstRegexMatch(str);
		if (match==null) 
			return null;
		else 
			return get(match);
		 
	}
	
	//Get all the objects mapped to regexes matching the string
	//@return a List of all the values mapped to the regex
	public List<Object> getRegexMatches(String str)
	{
		List<String> matches = regexMatches(str);
		List<Object> matchData = new LinkedList<Object>();
		for(String regex: matches)
		{
			Object data = get(firstRegexMatch(regex));
			matchData.add(data);
		}
		return matchData;
	}
	
}
