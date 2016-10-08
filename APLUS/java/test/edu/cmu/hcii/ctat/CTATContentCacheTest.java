/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.hcii.ctat;

import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import edu.cmu.pact.Utilities.trace;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author sewall
 *
 */
public class CTATContentCacheTest extends TestCase {
	
	private CTATContentCache cache = new CTATContentCache();
	
	public CTATContentCacheTest() {
		cache.addToCache("fred.txt", "fred".getBytes(), "Mon, 6 Feb 2012 13:04:45 GMT",true);
		cache.addToCache("nodate.txt", "no date".getBytes(), null,true);
	}
	
	public static TestSuite suite() {
		return new TestSuite(CTATContentCacheTest.class);
	}
	
	public void testMakeURL() {
		String[] goodUrls = {
				"http://preview.webmathtutor.org/tutors/problem_sets/Current/8.12/FinalBRDs/shed.brd",
				"http://preview.webmathtutor.org/tutors/problem_sets/Current/8.12/FinalBRDs/office picnic.brd",
				"https://preview.webmathtutor.org/tutors/problem;sets/Current/8*12/FinalBRDs/office\tpicnic.brd",
				"https://preview.webmathtutor.org/tutors/problem\\sets/Current/8.12/FinalBRDs/office\tpicnic.brd"
		};
		String[] badURLs = {
				"noscheme//preview.webmathtutor.org/tutors/problem_sets/Current/8.12/FinalBRDs/shed.brd",
				"1badscheme://preview.webmathtutor.org/tutors/problem_sets/Current/8.12/FinalBRDs/shed.brd",
				"http:no-leading-slashes/tutors/problem_sets/Current/8.12/FinalBRDs/shed.brd",
				"http:/one-leading-slash/tutors/problem_sets/Current/8.12/FinalBRDs/shed.brd"
		};
		String url = null;

		try {
			URL result;
			for(String s : goodUrls) {
				assertNotNull(url = s, result = cache.makeURL(s));
				trace.out("cache", result.toString());
			}
		} catch(Exception e) {
			fail("Exception on "+url+": "+e+";\n  cause "+e.getCause());
		}
		for(String s : badURLs) {
			try {
				URL result = cache.makeURL(s);
				fail(result.toString());
			} catch(Exception e) {
				trace.out("cache", "Error on bad url "+s+": "+e);
			}
		}
	}

	public void testDateFormat() {
		String lastModifiedStr = "Sun, 5 Feb 2012 21:04:45 GMT";
		try {
			Date lastModified = CTATWebTools.headerDateFmt.parse(lastModifiedStr);
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			cal.setTime(lastModified);
			assertEquals("year", 2012, cal.get(Calendar.YEAR));
			assertEquals("month", 1, cal.get(Calendar.MONTH));  // January is 0
			assertEquals("day", 5, cal.get(Calendar.DAY_OF_MONTH));
			assertEquals("hour", 21, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals("minute", 4, cal.get(Calendar.MINUTE));
			assertEquals("seconds", 45, cal.get(Calendar.SECOND));
			assertEquals("day of week", Calendar.SUNDAY, cal.get(Calendar.DAY_OF_WEEK));
			assertEquals("time zone", "UTC", cal.getTimeZone().getID());
		} catch (ParseException pe) {
			fail("Error parsing \""+lastModifiedStr+"\": "+pe);
		}
	}
	
	public void testIsFileUpToDate() {
		assertEquals("not in cache", CTATContentCache.Status.CACHE_INVALID,
				cache.isFileUpToDate("not_in_cache.txt", "Mon, 6 Feb 2012 13:04:45 GMT",true));
		assertEquals("null date in cache", CTATContentCache.Status.CACHE_INVALID,
				cache.isFileUpToDate("nodate.txt", "Mon, 6 Feb 2012 13:04:45 GMT",true));
		assertEquals("null date from caller", CTATContentCache.Status.READ_FROM_CACHE,
				cache.isFileUpToDate("fred.txt", null,true));
		assertEquals("older date from caller", CTATContentCache.Status.READ_FROM_CACHE,
				cache.isFileUpToDate("fred.txt", "Mon, 6 Feb 2012 13:04:44 GMT",true));
		assertEquals("current date from caller", CTATContentCache.Status.NOT_MODIFIED,
				cache.isFileUpToDate("fred.txt", "Mon, 6 Feb 2012 13:04:45 GMT",true));
		assertEquals("newer date from caller", CTATContentCache.Status.NOT_MODIFIED,
				cache.isFileUpToDate("fred.txt", "Mon, 6 Feb 2012 13:04:46 GMT",true));
	}
}
