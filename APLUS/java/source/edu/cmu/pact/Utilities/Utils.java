package edu.cmu.pact.Utilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;


/**
 * Static class of utilities for String manipulation, etc.
 */
public class Utils {
	
	/** External classes can implement this interface to show error messages, etc. to the user. */
	public static interface AlertDialog {
		public void showMessage(Throwable e, String text, String title);
	}
	
	/** A mechanism to show alerts. */
	private static AlertDialog alertDialog = null;
	
	/** Default port number to listen on. */
	public static final int DEFAULT_SERVER_PORT = 1500;
	
	public static final int DEFAULT_LAUNCHER_COMM_PORT = 1503;

	/** Default destination host. */
	public static final String DEFAULT_CLIENT_HOST = "localhost";

	/** Default port number to send to. */
	public static final int DEFAULT_CLIENT_PORT = 1501;
	/**
	 * Name of system property holding absolute path of classpath directory
	 * from which the student interface was loaded.
	 */
	public static final String INTERFACE_HOME_PROPERTY = "studentInterface.url";

	/**
	 * Pattern to match ${propertyName} references.  A propertyName will
	 * match this pattern if it begins with a letter or underscore and
	 * consists only of those characters plus digits or periods.
	 */
	private static Pattern propRefPattern =
		Pattern.compile("\\$\\{[a-zA-Z_][a-zA-Z_0-9.]*\\}");

	/** Pattern to match any white space in a string. */
    private static final Pattern whiteSpace = Pattern.compile("\\s+");

	/**
	 * Singleton private instance for calls to instance methods like getClass().
	 */
	private static Utils instance = new Utils();

	/**
	 * Default constructor private to prevent outside use.
	 */
	Utils() {}

	/**
	 * Calculate the difference of sets M-S, as all elements in M but not in S.
	 * Fails for sets that contain null elements.
	 * @param m minuend, or set preceding the minus sign
	 * @param s subtrahend, or set following the minus sign
	 * @return new set with elements in difference m-s
	 */
	public static Set differenceOfSets(Set m, Set s) {
		Set d = new HashSet();
		for (Iterator it = m.iterator(); it.hasNext(); ) {
			Object elt = it.next();
			trace.out("elt "+elt+", in s "+s.contains(elt));
			if (!(s.contains(elt)))
				d.add(elt);
		}
		trace.out("d "+d.toString());
		return d;
	}
	
	/**
	 * Substitute references to system properties in a string with the
	 * property values.  A property reference is of the form
	 * "some text ${propertyName} some more text."  This method replaces
	 * the reference with value of the named system property.
	 *
	 * @param  s String to transform
	 * @return String s with substitutions
	 */
	public static String expandPropertyReferences(String s) {

		Matcher m = propRefPattern.matcher(s);
		StringBuffer result = new StringBuffer();
		while (m.find()) {
			String propRef = m.group();    // gets the entire matched reference

			// strip initial "${" and trailing "}" to get property name
			String propName = propRef.substring(2, propRef.length()-1);
			String propValue = System.getProperty(propName, "");

			// don't use propValue as arg to appendReplacement():
			// backslashes will be removed
			m.appendReplacement(result, "");
			result.append(propValue);
		}
		m.appendTail(result);

		return result.toString();
	}

	/**
	 * Retrieve the resource path used to load this class.
	 *
	 * @return URL with the resource, excluding this class's package name;
	 *             null if {@link Class#getResource()} returns null or on error
	 */
	public static URL getCodeBaseURL(Class cls) {
        try {
            String pkgName = cls.getPackage().getName();
            String path = pkgName.replace('.', '/');
            return getCodeBaseURL(path);
        } catch (NullPointerException e) {
            return null;
        }
    }

	/**
	 * Retrieve the resource path used to load this class.
	 *
	 * @return URL with the resource, excluding this class's package name;
	 *             null if {@link Class#getResource()} returns null or on error
	 */
	public static URL getCodeBaseURL() {
		String pkgName = instance.getClass().getPackage().getName();
		String path = pkgName.replace('.', '/');
		return getCodeBaseURL(path);
	}

	/**
	 * Retrieve the resource path used to load the named resource.
	 *
	 * @param  path name of the resource to find; if null
	 * @return URL with the resource, excluding this class's package name;
	 *             null if {@link Class#getResource()} returns null or on error
	 */
	public static URL getCodeBaseURL(String path) {
		URL url =
			instance.getClass().getResource("/" + path);
                if (trace.getDebugCode("util")) trace.out("util", "url = " + url);

//        trace.addDebugCode("util");
//        trace.printStack("util");
		if (trace.getDebugCode("util")) trace.out("util", "*******   path=" + path + ", url=" +
				  (url == null? "" : url.toExternalForm()) + ";");
		if (url == null)
			return null;
		String urlStr = url.toExternalForm();
		int len = urlStr.lastIndexOf(path);
		if (len < 0)
			return url;
		try {
			return new URL(urlStr.substring(0, len));
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			return null;
		}
	}

	/**
	 * Resolve a relative pathname against the URL named in the
	 * {@link #INTERFACE_HOME_PROPERTY}, whose value is understood to be a URL.
	 * 
	 * @param  projectsStr relative pathname as String; ignored if null
	 * @return absolute URL; null on error
	 */
	public static URL resolveToInterfaceHomeURL(String projectsStr) {
		String interfaceHome = System.getProperty(INTERFACE_HOME_PROPERTY);
		if (interfaceHome == null)               // use current dir if undefined
			interfaceHome = "file:" + System.getProperty("user.dir");
		StringBuffer urlStr = new StringBuffer(interfaceHome);
		if (!interfaceHome.endsWith("/") &&
				(projectsStr == null || !projectsStr.startsWith("/")))
			urlStr.append("/");
		if (projectsStr != null)
			urlStr.append(projectsStr);
		if (trace.getDebugCode("util")) trace.out("util",
				  "projectsStr " + projectsStr + ", urlStr " + urlStr + " interface home = " + interfaceHome);
		try {
			URL url = new URL(urlStr.toString());
			return url;
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
			return null;
		}
	}

	/**
	 * Resolve a pathname: if given an absolute pathname, simply return
	 * a File reference for that path. Else resolve a relative pathname
	 * as done by
	 {@link #resolveToInterfaceHomeURL(String) resolveToInterfaceHomeURL(path)}
	 * 
	 * @param  path pathname as String
	 * @return absolute path as File
	 */
	public static File resolveToInterfaceHomeFile(String path) {
		trace.out ("util", "resolveToInterfaceHomeFile path = " + path);
		
		if (path != null && path.length() > 0) {
			File file = new File(path);
			if (file.isAbsolute())
				return file;
		}
		
		URL url = resolveToInterfaceHomeURL(path);  // path is empty or relative
		if (trace.getDebugCode("util")) trace.out("util", "path " + path + ", url " + url + ";");
		if (url == null)
			return null;
		File file = getFileAsResource(url);
		return file;
	}

	/**
	 * Convert a relative path to an absolute one by finding it on
	 * the classpath.
	 *
	 * @param  path relative path to convert
	 * @param  obj object whose ClassLoader we should use
	 * @return file created; null if error
	 */
    public static File getFileAsResource(String path, Object obj)
	{
		if( path == null || path.length() < 1 )
			return null;
		URL url = getURL( path, obj );
		if( url != null )                // path found on classpath
			return getFileAsResource( url );
		
		int bs = path.length(), be = bs-1;     // baseName start, baseName end
		while( bs > 0 && ( be == ( bs = path.lastIndexOf( '/', be ) ) ) )
			be--;
		if( bs < 0 || path.length() <= bs+1 || be < bs )
			return null;             // path had no slashes or was all slashes
		String baseName = path.substring( bs+1, be+1 );
		String parentPath = path.substring( 0, bs+1 );

		File parent = getFileAsResource( parentPath, obj );
		if( parent == null )
			return null;
		File f = new File( parent, baseName );
		return f;
	}

	/**
	 * Converts the given URL to an absolute path name.
	 *
	 * @param  url URL to convert
	 * @return absolute path; null if error
	 */
    public static File getFileAsResource(URL url)
	{
		try
		{
			if( url == null )
				return null;
			String absolutePath = URLDecoder.decode(url.getFile(), "UTF-8");
			url = null;
			if (trace.getDebugCode("util")) trace.out("util", "getFileAsResource absolutePath " + absolutePath);
			if( absolutePath == null )
				return null;
			return new File( absolutePath );
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Change a simple filename into a URL whose path is the package name of this class.
	 * @param obj use the package name of this object's class as a path
	 * @param  filename simple filename
	 * @return URL for this file
	 */
	public static URL getFileInSamePackage(Object obj, String filename) {
		Class cls = obj.getClass();
		String path = cls.getPackage().getName().replace('.', '/');
	    String name = path + "/" + filename;
		URL result = getURL(name, obj);
		return result;
	}

	/**
	 * Convert a relative path to URL by finding it on the classpath. This utility method
	 * calls {@link java.lang.Class#getResource(String)} to search the classpath for the
	 * given path.  On Android, insert the prefix "/assets/" to get files from APK assets/.
	 * @param  path relative path
	 * @param  obj object whose ClassLoader we should use
	 * @return URL; null if error
	 */
    public static URL getURL(String path, Object obj) {
    	String prefix = (System.getProperty("java.vendor", "Sun").toLowerCase().contains("android") ?
    			"/assets/" : "/");
    	URL url = obj.getClass().getResource( prefix + path );
    	if (trace.getDebugCode("util"))
    		trace.out("util", "getURL url " + url + ", path " + prefix + path );
		return url;
	}
	
	public static void invokeBrowser(String url) {
		boolean isWinDowPlatform;
		String cmd = "";

		Runtime rt = Runtime.getRuntime();
		
		// find the system type
		String os = System.getProperty("os.name");
		
		if (os == null)
			return;

        if (os.toUpperCase().startsWith("WINDOWS"))
			isWinDowPlatform = true;
        else if (os.toUpperCase().startsWith("MAC"))
			isWinDowPlatform = false;
        else {
			trace.err("Unsupported system platform "+os);
			return;
        }
		
		try
        {
            if (isWinDowPlatform)
                cmd = "rundll32" + " " + "url.dll,FileProtocolHandler" + " " + url;
            else 
				cmd = "open " + url;
            if (trace.getDebugCode("util")) trace.out("util", "to exec '"+cmd+"'");
			rt.exec(cmd);
				
        } catch(IOException  ex) {
            // couldn't exec browser
            System.err.println("Could not invoke browser, command=" + cmd);
        }
	}

	/**
	 * Set this computer's native look and feel into our {@link UIManager}.
	 */
	public static void setNativeLookAndFeel() {
	    // Get the native look and feel class name
	    String nativeLF = UIManager.getSystemLookAndFeelClassName();
	
	    // Install the native look and feel
	    try {
	        UIManager.setLookAndFeel(nativeLF);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	/**
	 * Try to get a directory name from a path. If the path is a directory,
	 * returns the path as is. Else tries to get the parent of the path.
	 * @param path path specifier. If this path is not an existing file,
	 *         tries to parse the path as a {@link URI} 
	 * @return directory portion, or null if fails; throws no exceptions
	 */
	public static String getDirectory(String path) {
		String dir = null;
		try {
			File file = new File(path);
			if (file.exists()) {
				if (file.isDirectory())
					dir = file.getPath();
				else if (file.getParent() != null) 
					dir = file.getParent();
				else
					dir = ".";  // the current directory
			}
		} catch (Exception e) {
			trace.err("Error parsing path \""+path+"\" as File: "+e);
			try {
				URI uri = new URI(path);
				uri = uri.normalize();  // inserts a leading "./" if needed
				if (uri.toString().endsWith("/")) {
					StringBuffer sb = new StringBuffer(uri.toString());
					do {
						sb.deleteCharAt(sb.length()-1);
					} while (sb.length() > 1 && sb.charAt(sb.length()-1) == '/');
					dir = sb.toString();
				} else {
					int lastSlash = uri.toString().lastIndexOf('/'); 
					if (lastSlash > 0)
						dir = uri.toString().substring(0, lastSlash);
					else
						dir = uri.toString();
				}
			} catch (Exception ee) {
				trace.err("Error parsing path \"+path+\" as URI: "+ee);
				ee.printStackTrace();
			}
		}
		return dir;
	}

	/**
	 * Get the simple filename from a path.
	 * @param fullName
	 * @return last portion of pathname, minus last "." extension
	 */
	public static String getBaseName(String fullName) {
		return getBaseName(fullName, true);
	}
	
	/**
	 * Get the simple filename from a path.
	 * @param fullName
	 * @param deleteExtension if true, delete last "." extension
	 * @return last portion of pathname
	 */
	public static String getBaseName(String fullName, boolean deleteExtension) {
		File file;
		try {
			URL url = new URL(fullName);
			file = getFileAsResource(url);
		} catch (Exception e) {
			file = null; // caught below
		}
		try {
			if (file == null)
				file = new File(fullName);
		} catch (Exception e) {
			trace.err("Utils.getBaseName(): Unable to create file object for "+
					fullName+": "+e);
			return fullName;				
		}
		String result = file.getName();
		int dot = result.lastIndexOf('.');
		if (deleteExtension && dot > 0)
			result = result.substring(0, dot);
		return result;
	}

	/**
	 * Get the simple classname from a package....classname.
	 * @param fullClassName
	 * @return last portion of name
	 */
	public static String getSimpleName(String fullClassName) {
		int dot = fullClassName.lastIndexOf('.');
		if (dot > 0)
			return fullClassName.substring(dot+1);
		return fullClassName;
	}
	
	/**
	 * Copy the given file to a new temporary file.
	 * @param inf input file
	 * @return tf temporary file
	 * @throws IOException
	 */
	public static File copyToTempFile(File inf) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		File result = null;
		try {
			if (inf == null)
				throw new IOException("source file reference is null");
			long infLength = inf.length();
			if (infLength > Integer.MAX_VALUE)
				throw new IOException("source file "+inf.getName()+", length "+
						infLength+", exceeds max size for copying");
			byte[] infBytes = new byte[(int) infLength];
			is = new FileInputStream(inf);
			is.read(infBytes);
			result = File.createTempFile(inf.getName(), ".tmp");
			os = new FileOutputStream(result);
			os.write(infBytes);
			return result;
		} finally {
			try { is.close(); } catch (Exception e) {}
			try { os.close(); } catch (Exception e) {}
		}
		
	}
    
    /**
     * Replace all occurrences of white space in the given string with a single space.
     * @param s string to modify
     * @return modified string
     */
	public static String cleanup(String s) {
		Matcher m = whiteSpace.matcher(s);
		return 	m.replaceAll(" ");
	}

	/**
	 * replace the image path in the <img src="imagepath"> with
	 * getClass().getResource()
	 * @param message
	 * @return
	 */
	public static String replaceImg(String message) {
		StringBuffer messageBuffer = new StringBuffer(message);
		Matcher matcher;
		Pattern pattern = Pattern.compile("<\\s*img.*src\\s*=[^>]*");
		matcher = pattern.matcher(message);
		if (matcher.find()) {
			String imgPath = message.substring(matcher.start(), matcher.end());
			int start = matcher.start();
			pattern = Pattern.compile("\".*\"");
	
			matcher = pattern.matcher(imgPath);
			if (matcher.find()) {
				int imgStart = start + matcher.start();
				int end = imgStart + matcher.end();
				String tempStr = matcher.group();
				tempStr = tempStr.replaceAll("\"", "");
				URL url = Utils.class.getResource(tempStr);
				if (url != null) {
					//					trace.out("url: " + url.toString());
					messageBuffer.replace(imgStart, end, url.toString());
				}
			}
		}
		return messageBuffer.toString();
	}

	/**
	 * Sleep for the full time given, regardless of {@link InterruptedException}s.
	 * @param ms milliseconds to wait
	 * @return milliseconds elapsed
	 */
	public static long sleep(long ms) {
		if (ms < 1)
			return ms;
		long now = System.currentTimeMillis();
		long then = now+ms;
		do {
			try {
				Thread.sleep(then-now);
			} catch (InterruptedException ie) {
				;
			}
		} while ((now = System.currentTimeMillis()) < then);
		return ms+now-then;
	}

	/**
	 * Capitalize the initial letter of each word in a string.
	 * @param s string to modify
	 * @return edited string
	 */
	public static String upperCaseInitials(String s) {
		if (s == null)
			return null;
		StringBuilder sb = new StringBuilder(s);
		boolean upperCaseNext = true;  // capitalize 1st letter
		for (int i = 0; i < sb.length(); ++i) {
			if (upperCaseNext) {
				sb.setCharAt(i, Character.toUpperCase(sb.charAt(i)));
				upperCaseNext = false;
			}
			if (!(Character.isLetterOrDigit(sb.charAt(i))))
				upperCaseNext = true;  // capitalize initial of any new word
		}
		return sb.toString();
	}

	/**
	 * Generate the markup for an HTML table nCols columns wide to hold the given list.
	 * @param items list of items to format
	 * @param nCols number of columns in table
	 * @return table markup
	 */
	public static String listToHtmlTbl(Collection<Object> items, int nCols) {
		if (items.size() < 1)
			return "";
		StringBuilder sb = new StringBuilder("<table border=\"0\" cellpadding=\"1\">");
		int i = 0;
		for (Iterator<Object> it = items.iterator(); it.hasNext();) {
			Object item = it.next();
			if (item == null || item.toString().length() < 1)
				continue;
			if (i > 0)
				sb.append(",</td>");
			if (i % nCols == 0) {
				if (i > 0)
					sb.append("</tr>");
				sb.append("<tr>");
			}
			sb.append("<td>&nbsp;").append(item.toString());
			++i;
		}
		sb.append("</td>");
		while ((i++) % nCols != 0)
			sb.append("<td>&nbsp;</td>");
		sb.append("</tr></table>");
		return sb.toString();
	}

	/**
	 * Find all the files in a directory subtree matching a given {@link FileFilter}.
	 * @param fi
	 * @param filter
	 * @return
	 */
	public static List<File> findFiles(File fi, FileFilter filter) {
		List<File> result = new ArrayList<File>();
		findFiles(fi, filter, result);
		return result;
	}
	
	/** To sort file names so that all plain files precede all subdirectories. */
	private static class FilesBeforeDirs implements Comparator<File> {

		/**
		 * If one argument is a directory and the other is not, order the pair
		 * to make the directory second. Else use {@link File}'s natural order.
		 * @param o1
		 * @param o2
		 * @return -1, 0 or 1
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare(File o1, File o2) {
			if (o1 == null)
				return (o2 == null ? 0 : -1);  // null precedes all
			if (o2 == null)
				return 1;
			boolean d1 = o1.isDirectory(), d2 = o2.isDirectory(); 
			if (d1 ^ d2) 
				return d1 ? 1 : -1;
			else
				return o1.compareTo(o2);
		}
	}
	private static FilesBeforeDirs filesBeforeDirs = new FilesBeforeDirs();
	
	/**
	 * Recursive workhorse for {@link #findFiles(File, FileFilter)}
	 * @param fi
	 * @param filter
	 * @param result
	 */
	private static void findFiles(File fi, FileFilter filter, List<File> result) {
		if (!fi.exists())
			return;
		if (!fi.isDirectory()) {
			if (filter == null || filter.accept(fi))
				result.add(fi);
			return;
		}
		File[] files = fi.listFiles();
		Arrays.sort(files, filesBeforeDirs);
		for (File cfi : files)
			findFiles(cfi, filter, result);
	}

	/**
	 * @return the {@link #alertDialog}
	 */
	public static AlertDialog getAlertDialog() {
		return alertDialog;
	}

	/**
	 * @param alertDialog new value for {@link #alertDialog}
	 */
	public static void setAlertDialog(AlertDialog alertDialog) {
		Utils.alertDialog = alertDialog;
	}

	/**
	 * Display a warning dialog to report an error.
	 * @param e1 exception to report; if not null, prints stack trace to error
	 * @param message error description
	 * @param title title for popup window
	 */
	public static void showExceptionOccuredDialog(Exception e1, String message,
			String title) {
		showExceptionOccuredDialog(e1, message, title, 350, 200);
	}

	/**
	 * Display a warning dialog to report an error.
	 * @param e1 exception to report; if not null, prints stack trace to error
	 * @param message error description
	 * @param title title for popup window
	 * @param width
	 * @param height
	 */
	public static void showExceptionOccuredDialog(Exception e1, String message,
			String title, int width, int height) {
		if (isRuntime() || getSuppressDialogs()) {
			if (e1 != null)
				trace.errStack("Utils.showExceptionOccurredDialog "+title+":\n  "+message, e1);
			else
				trace.err("Utils.showExceptionOccurredDialog "+title+":\n  "+message);
			return;
		}
		if(trace.getDebugCode("android"))
			trace.outNT("android", "showExceptionOccuredDialog("+e1+","+message+","+title+") alertDialog "+alertDialog );

		if(alertDialog != null)
			alertDialog.showMessage(e1, message, title);
		else
			showExceptionInternal(e1, message, title, width, height);
	}

	/**
	 * Try to test whether a path name is a readable directory. If argument is a URL, adds a
	 * trailing slash to try to ensure that it's a directory.
	 * @param path filename or URL
	 * @param obj instance for {@link #getFileAsResource(String, Object)}
	 * @return true if exists and is a readable directory
	 */
	public static boolean isDirectoryReadable(String path, Object obj) {
		if (trace.getDebugCode("mt")) trace.out("mt", "MT.isDirectoryReadable("+path+")");
		if (path == null || path.length() < 1)
			return false;

		File f = new File(path);
		if (trace.getDebugCode("mt")) trace.out("mt", "MT.isDirectoryReadable() file result "+f);
		if (f != null && /* f.isAbsolute() && */ f.exists())
			return f.isDirectory() && f.canRead();

		File result = Utils.getFileAsResource((path), obj);
		if (trace.getDebugCode("mt")) trace.out("mt", "MT.isDirectoryReadable() fileAsResource result "+result);
		if (null != result && result.exists())
			return result.isDirectory() && result.canRead();

		URL url = null;
		if(!path.endsWith("/"))
			path = path+"/";
		try {
			url = new URL(path);
			InputStream is = url.openStream();
			is.close();
			if (trace.getDebugCode("")) trace.out("mt", "MT.isDirectoryReadable() URL result "+url);					
			return true;
		} catch(Exception e) {
			if (trace.getDebugCode("mt"))
				trace.out("mt", "MT.isDirectoryReadable() error reading \""+path+"\" as URL: "+
					e+"; cause "+e.getCause());
		}
		try {
			url = Utils.getURL(path, obj);
			if(url == null)
				return false;
			InputStream is = url.openStream();
			is.close();
			if (trace.getDebugCode("mt")) trace.out("mt", "MT.isDirectoryReadable() getURL result "+url);
			return true;
		} catch(Exception e) {
			if (trace.getDebugCode("mt"))
				trace.out("mt", "MT.isDirectoryReadable() error reading \""+path+"\" as URL: "+
					e+"; cause "+e.getCause());
		}
		return false;
	}

	/**
	 * Swing implementation for {@link #showExceptionOccuredDialog(Exception, String, String)}.
	 * @param e1
	 * @param message
	 * @param title
	 */
	private static void showExceptionInternal(Exception e1, String message,
			String title, int width, int height) {

		boolean html = false;
		String msgLC = message.toLowerCase();
		String endTag = "";
		if (msgLC.trim().startsWith("<html")) {
			html = true;
			int h = msgLC.lastIndexOf("</html>");
			if (h > 0) {
				endTag = message.substring(h);
				message = message.substring(0, h);
			}
		}
		JEditorPane msgLabel = new JEditorPane();
		msgLabel.setFont(new Font("", Font.PLAIN, 11));
		msgLabel.setBorder(new EmptyBorder(5,5,5,5));
		msgLabel.setBackground(new Color(235,235,235));
		msgLabel.setEditable(false);
		msgLabel.setContentType(html ? "text/html" : "text/plain");
		if (e1 == null)
			msgLabel.setText(message);
		else {
			StringWriter s = new StringWriter();
			s.write(html ? "<br /><pre>Error: " : "\nError: ");
			PrintWriter p = new PrintWriter(s);
			e1.printStackTrace(p);
			if (html) s.write("</pre>");
			msgLabel.setText(message+s.toString()+endTag);
		}
		msgLabel.setPreferredSize(new Dimension(width, height));
		JScrollPane pane = new JScrollPane(msgLabel);
		if (trace.getDebugCode("inter"))
			trace.out("inter", "showExceptionOccurred text:\n  "+msgLabel.getText());
		JOptionPane.showMessageDialog(null, pane, title,
				JOptionPane.WARNING_MESSAGE);

//        .... other ideas for formatting ....
//		  messageTextArea.setText(message);
//        messageTextArea.setEditable(false);
//
//        JOptionPane optionPane = new JOptionPane();
//        optionPane.setMessage(new Object[] { messageScrollPane });
//        optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
//        optionPane.setOptionType(JOptionPane.YES_NO_OPTION);
//
//        JDialog dialog = optionPane.createDialog(getActiveWindow(),
//                "Warning");
//
//        if (getProblemModel().getWillRemovedLinkGroups().size() > 0)
//            dialog.setSize(650, 420);
//        else
//            dialog.setSize(650, 300);
//
//        Rectangle rect = new Rectangle(0, 0, 10, 10);
//        messageTextArea.scrollRectToVisible(rect);
//
//        JViewport jViewport = messageScrollPane.getViewport();
//        jViewport.scrollRectToVisible(rect);
//
//        dialog.show();
//
//        Integer value = (Integer) optionPane.getValue();
//
//        if (value == null)
//            return -1;
//
//        return value.intValue();
		
	}

	/** Tell whether we're in the authoring tools or the student runtime. */
	private static boolean runtime = false;

	/** Whether modal dialogs are suppressed: true means suppress; false means display. */
	private static boolean suppressDialogs = false;

	/**
	 * Set whether we're in the authoring tools or the student runtime.
	 * @param b new value for {@link #runtime}
	 */
	public static void setRuntime(boolean b) {
		if(trace.getDebugCode("util"))
			trace.printStack("util", "Utils.setRuntime("+b+"); old value "+runtime);
		runtime = b;
	}

	/**
	 * Tell whether we're in the authoring tools or the student runtime.
	 * @return {@link #runtime}
	 */
	public static boolean isRuntime() {
		return runtime;
	}

	/** 
	 * Suppress modal dialogs. See, e.g.,
	 * {@link #showExceptionOccuredDialog(Exception, String, String)}.
	 * @param newSuppressDialogs true means suppress; false means display
	 */
	public static void setSuppressDialogs(boolean newSuppressDialogs) {
		suppressDialogs = newSuppressDialogs;
	}

	/** 
	 * Whether modal dialogs are suppressed. See, e.g.,
	 * {@link #showExceptionOccuredDialog(Exception, String, String)}.
	 * @return {@link #suppressDialogs}: true means suppress; false means display
	 */
	public static boolean getSuppressDialogs() {
		return suppressDialogs;
	}

	/** Regex to check whether a string already has HTML markup. */
	private static final Pattern HasHtmlMarkup =
			Pattern.compile("(\\s*<?[^>]+?>)?\\s*<[hH][tT][mM][lL].*(</[hH][tT][mM][lL]>\\s*)");
	
	/**
	 * Modify a string to include an HTML comment. If the string isn't HTML, adds
	 * &lg;html&gt; markup. Then appends comment in &lt;!-- ... --&gt; brackets.
	 * @param str string to modify
	 * @param comment text of comment to add
	 * @return modified string
	 */
	public static String addHtmlComment(String str, String comment) {
		if(!comment.trim().startsWith("<!"))
			comment = " <!-- "+comment+" --> ";
		Matcher m = HasHtmlMarkup.matcher(str);
		if(m.matches()) {
			int insAt = m.start(2);
			return str.substring(0, insAt) + comment + str.substring(insAt);
		} else {
			return "<html>"+str+comment+"</html>";
		}
			
	}

	/**
	 * Append a trailing slash, to ensure that a path is a directory name. 
	 * @param path
	 * @return path argument, with a trailing slash ("/") or {@value File#separator}
	 */
	public static String appendSlash(String path) {
		if(path.endsWith("/") || path.endsWith(File.separator))
			return path;
		if(path.contains(File.separator))
			return path+File.separator;
		else 
			return path+"/";
	}

	/**
	 * Calculate a CRC32 value
	 * @param str string to evaluate
	 * @return {@link #crc(String, String) #crc(str, "UTF-8")}
	 */
	public static long crc(String str) {
		return crc(str, "UTF-8");
	}

	/**
	 * Calculate a CRC32 value
	 * @param str string to evaluate
	 * @param encoding name of character set to use
	 * @return {@link CRC32#getValue()} result
	 */
	public static long crc(String str, String encoding) {
		byte[] bytes = null;
		try {
			bytes = str.getBytes(encoding);			
		} catch(UnsupportedEncodingException uee) {
			trace.err("Invalid character set name \""+encoding+"\"; trying default: "+uee);
			try {bytes = str.getBytes("UTF-8");} catch(Exception e) {}
		}
		CRC32 crc32 = new CRC32();
		crc32.update(bytes);
		return crc32.getValue();
	}
}
