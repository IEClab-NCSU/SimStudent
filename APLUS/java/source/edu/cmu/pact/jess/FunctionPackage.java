/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jess.Rete;
import jess.Userfunction;
import jess.Userpackage;
import edu.cmu.pact.Utilities.trace;

/**
 * Class to load {@link Userfunction} instances without having to code
 * references.
 */
public class FunctionPackage implements Userpackage {

	/** The functions in this package. */
	private List functionInstances = new ArrayList();

	/**
	 * Create the list of function instances given their class names.
	 * For each class name, will try to find and call first a constructor that
	 * takes a {@link JessModelTracing} argument, then a constructor that takes
	 * no arguments.
	 * @param classNames fully-qualified class names of the {@link Userfunction} classes to load
	 * @param jmt argument for constructors
	 */
	public FunctionPackage(String[] classNames, JessModelTracing jmt) {
		Class[] jmtParam = { JessModelTracing.class };
		Class[] noParam = new Class[0];
		Object[] jmtArg = { jmt };
		Object[] noArg = new Object[0];
		Class clazz;
		Constructor constructor;
		for (int i = 0; i < classNames.length; ++i) {
			try {
				clazz = Class.forName(classNames[i]);
			} catch (ClassNotFoundException cnfe) {
				trace.err("Could not load class "+classNames[i]+": "+cnfe);
				continue;
			}
			Object functionInstance = null;
			String lastNSMError = null;
			for (int j = 0; functionInstance == null && j < 2; ++j) {
				Class[] formalArgs = (j < 1 ? jmtParam : noParam);
				Object[] actualArgs = (j < 1 ? jmtArg : noArg);
				String signature = classNames[i]+"("+(j < 1 ? "JessModelTracing" : "")+")";
				try {
					constructor = clazz.getConstructor(formalArgs);
					functionInstance = constructor.newInstance(actualArgs);
					if (trace.getDebugCode("mt")) trace.outNT("mt", "FunctionPackage loaded "+signature);
				} catch (NoSuchMethodException nsme) {
					if (j < 1)
						lastNSMError = "Could not find constructor "+signature;
					else
						trace.err(lastNSMError+"or constructor "+signature+"): "+nsme);
					continue;
				} catch (Exception e) {
					trace.err("Error calling constructor "+signature+": "+e);
					continue;
				}
			}
			if (functionInstance instanceof Userfunction)
				functionInstances.add(functionInstance);
			else
				trace.err("Error loading "+classNames[i]+": class does not implement jess.Userfunction");
		}
	}

	/**
	 * Call {@link Rete#addUserfunction(Userfunction)} for all elements of
	 * {@link #functionInstances}.
	 * @param engine
	 * @see jess.Userpackage#add(jess.Rete)
	 */
	public void add(Rete engine) {
		int i = 0;
		for (Iterator it = functionInstances.iterator(); it.hasNext(); ++i)
			engine.addUserfunction((Userfunction) it.next());
	}

	/**
	 * Give the number of functions to load.
	 * @return {@link #functionInstances}.size()
	 */
	public int size() {
		return functionInstances.size();
	}

}
