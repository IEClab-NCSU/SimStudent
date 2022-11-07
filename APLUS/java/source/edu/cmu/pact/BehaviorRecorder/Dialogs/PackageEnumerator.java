/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.pact.Utilities.trace;

/**
 * Find all the classes or methods in a package. A principal source for this code was
 * this posting <a href="http://stackoverflow.com/users/1996022/brainstone"/> on the
 * Stack Overflow site. See {@link #getClassesForPackage(String) and the methods it calls.
 */
public class PackageEnumerator {

	/**
	 * @param args package names
	 */
	public static void main(String[] args) throws ClassNotFoundException {
		if(args.length > 0 && "-s".equalsIgnoreCase(args[0])) {
			for(int i = 1; i < args.length; ++i)
				System.out.printf("[%d] %25s => \"%s\"\n", i, args[i], extractMethodSignature(args[i]));
			return;
		}
		Set<String> sigSet = new HashSet<String>(); 
		for(String arg : args) {
			if(arg.endsWith("Math"))
				sigSet.addAll(getSelectedMethodSignatures(Math.class, true, "[Math] "));
			else if(arg.endsWith("String"))
				sigSet.addAll(getSelectedMethodSignatures(String.class, true, "[String] "));
			else {
				for(Class<?> cls : getClassesForPackage(arg)) {
					if(!(cls.getSimpleName().contains("$")))         // exclude inner classes
						sigSet.addAll(getSelectedMethodSignatures(cls, false, null));
				}
			}
		}
		List<String> result = new ArrayList<String>(sigSet);
		Collections.sort(result, String.CASE_INSENSITIVE_ORDER);
		for(String s : result)
			System.out.printf("  %55s => %s\n", s, extractMethodSignature(s));
	}

	/** Set of methods in the {@link Object} class: omit these from {@link #showSelectedMethods(Object)}. */
    private static final Set<String> ObjectMethods = new HashSet<String>();
    static {
    	for(Method m : Object.class.getMethods())
    		ObjectMethods.add(m.getName());
    }

    /**
     * Get the signatures of public methods in the given class whose names match the class name.
     * If the method takes a variable number of arguments, represent the last formal argument type
     * as "Type..." instead of "Type[]".
     * @param cls class to scan
     * @param wantAll if true, get all methods, not just those whose name matches the class name
     * @param prefix put this string before the method name
     * @return list of signatures
     */
    public static List<String> getSelectedMethodSignatures(Class<?> cls, boolean wantAll, String prefix) {
		List<String> result = new ArrayList<String>();
		if(prefix == null)
			prefix = "";
		String clsName = cls.getSimpleName();
    	Method[] methods = cls.getMethods();
    	for(int i = 0; i < methods.length; i++) {
    		Method m = methods[i];
//    		trace.out("cls "+clsName+", m "+m.getName());
    		if((m.getModifiers() & Modifier.PUBLIC) == 0)      // public methods only
    			continue;
    		if(!wantAll && !(m.getName().equals(clsName)))     // filter on method name
    			continue;
    		if(ObjectMethods.contains(m.getName())) {          // skip methods common to every class
    			if("String".equals(clsName) && "equals".equals(m.getName()))
    				;                                          // but preserve String.equals()
    			else
    				continue;
    		}
    		Class[] mArgs = m.getParameterTypes();
    		StringBuilder mArgStr = new StringBuilder();
    		if(mArgs.length > 0) {
    			int j = 0;
    			do {
    				String argType = mArgs[j].getSimpleName();
    				if(m.isVarArgs() && j >= mArgs.length-1 && mArgs[j].isArray()) {
    					if(argType.endsWith("[]"))
    						argType = argType.substring(0, argType.length()-2)+"...";
    				}
    				mArgStr.append(argType);
    				if(j < mArgs.length-1)
    					mArgStr.append(", ");
    			} while(++j < mArgs.length);
    		}
    		String signature = String.format("%s%s(%s): %s",
    				prefix, m.getName(), mArgStr.toString(), m.getReturnType().getSimpleName());
    		result.add(signature);
    	}
    	return result;
    }
    
    /**
     * Regular expression to get just the <i>methodName(arguments)</i> from the method signature.
     * <b>N.B.:</b> this must remain consistent with {@link #getSelectedMethodSignatures(Class, boolean, String)}.
     */
    private static final Pattern ExtractMethodSignature =
    		Pattern.compile(" *(\\[[a-zA-Z]+\\] *)?([a-zA-Z_][a-zA-Z_0-9]*\\([^)]*\\)).*");
    
    /**
     * Extract just the <i>methodName(arguments)</i> from the method signature. Uses
     * {@value #ExtractMethodSignature}. <b>N.B.:</b> this must remain consistent with
     * {@link #getSelectedMethodSignatures(Class, boolean, String)}.
     * @param s string to scan
     * @return method name and arguments, if s matches the pattern; else s unchanged
     */
    public static String extractMethodSignature(String s) {
    	if(s == null)
    		return null;
		Matcher m = ExtractMethodSignature.matcher(s);
		return m.matches() ? m.group(2) : s;    	
    }
	
	/**
	 * Private helper method
	 * 
	 * @param directory
	 *            The directory to start with
	 * @param pckgname
	 *            The package name to search for. Will be needed for getting the
	 *            Class object.
	 * @param classes
	 *            if a file isn't loaded but still is in the directory
	 * @throws ClassNotFoundException
	 */
	private static void checkDirectory(File directory, String pckgname,
	        ArrayList<Class<?>> classes) {
	    File tmpDirectory;
	    final String clsExt = ".class";

	    if (directory.exists() && directory.isDirectory()) {
	        final String[] files = directory.list();

	        for (final String file : files) {
	            if (file.endsWith(clsExt)) {
	            	String clsName = pckgname + '.' + file.substring(0, file.length() - clsExt.length());
	                try {
	                    classes.add(Class.forName(clsName)); 
	                } catch (NoClassDefFoundError nodfe) {
	                    // do nothing: this class hasn't been found by the loader, and we don't care.
	                } catch (ClassNotFoundException cnfe) {
	                	trace.errStack("PackageEnumerator.checkDirectory("+directory+", "+pckgname+"):"+
	                			" error from Class.forName("+clsName+")", cnfe);
	                }
	            } else if ((tmpDirectory = new File(directory, file)).isDirectory()) {
	                checkDirectory(tmpDirectory, pckgname + "." + file, classes);
	            }
	        }
	    }
	}

	/**
	 * Private helper method. Handles exceptions.
	 * 
	 * @param connection
	 *            the connection to the jar
	 * @param pckgname
	 *            the package name to search for
	 * @param classes
	 *            the current ArrayList of all classes. This method will simply
	 *            add new classes.
	 */
	private static void checkJarFile(JarURLConnection connection,
	        String pckgname, ArrayList<Class<?>> classes) {
	    JarFile jarFile = null;
	    try {
	    	jarFile = connection.getJarFile();
	    } catch (Exception e) {
        	trace.errStack("PackageEnumerator.checkJarFile("+connection+", "+pckgname+"):"+
        			" error from JarURLConnection.getJarFile()", e);
	    	return;
	    }
	    final Enumeration<JarEntry> entries = jarFile.entries();

	    for (JarEntry jarEntry = null; entries.hasMoreElements()
	            && ((jarEntry = entries.nextElement()) != null);) {

	    	String name = jarEntry.getName();
	        if (!name.endsWith(".class"))
	        	continue;

	        name = name.substring(0, name.length() - 6).replace('/', '.');
	        if (!(name.contains(pckgname)))
	        	continue;

	        try {
	        	classes.add(Class.forName(name));
	        } catch (Throwable e) {
            	trace.errStack("PackageEnumerator.checkJarFile("+connection+", "+pckgname+"):"+
            			" error from Class.forName("+name+")", e);	        	
	        }
	    }
	}

	/**
	 * Attempts to list all the classes in the specified package as determined
	 * by the context class loader. Handles exceptions.
	 * 
	 * @param pckgname the package name to search
	 * @return a list of classes that exist within that package
	 */
	public static ArrayList<Class<?>> getClassesForPackage(String pckgname) {
	    final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
	    URL url = null;
	    try {
	        final ClassLoader cld = Thread.currentThread().getContextClassLoader();
	        if (cld == null)
	            throw new ClassNotFoundException("Can't get class loader.");

	        final Enumeration<URL> resources = cld.getResources(pckgname.replace('.', '/'));
	        URLConnection connection;

	        for (url = null; resources.hasMoreElements()
	                && ((url = resources.nextElement()) != null);) {
	            try {
	                connection = url.openConnection();

	                if (connection instanceof JarURLConnection) {
	                    checkJarFile((JarURLConnection) connection, pckgname, classes);
	                } else if (connection != null) {
	                    try {
	                        checkDirectory(new File(URLDecoder.decode(url.getPath(), "UTF-8")), pckgname, classes);
	                    } catch (UnsupportedEncodingException ex) {
	    	            	trace.errStack("PackageEnumerator.getClassesForPackage("+pckgname+
	    	            			") Unsupported encoding \"UTF-8\"?!", ex);
	                    }
	                } else 
    	            	trace.errStack("PackageEnumerator.getClassesForPackage("+pckgname+
    	            			"): cannot open connection to URL "+url, new IOException("URL.openConnection() returns null"));
	            } catch (final IOException ioe) {
	            	trace.errStack("PackageEnumerator.getClassesForPackage("+pckgname+
	            			"): error trying to get resources from URL "+url, ioe);
	            }
	        }
	    } catch (ClassNotFoundException cnfe) {
	    	trace.errStack("PackageEnumerator.getClassesForPackage("+pckgname+"): "+cnfe.getMessage(), cnfe);
	    } catch (NullPointerException npe) {
	    	trace.errStack("PackageEnumerator.getClassesForPackage() "+pckgname+" does not appear to be a valid package", npe);
	    } catch (Exception e) {
	    	trace.errStack("PackageEnumerator.getClassesForPackage("+pckgname+"): error trying to get resources", e);
	    }

	    return classes;
	}
}
