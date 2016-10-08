package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Provide access to functions outside this package.
 */
public class extFn {

	/**
	 * Ask the bootstrap class loader to load the external function's class. Then
	 * find the method, create an instance and call the method with the given arguments.
	 * @param className full class name, with package
	 * @param methodName method name
	 * @param args arguments to method; will use type of these to look up signature
	 * @return method's return; null if method returns void
	 * @throws Exception many, many ways to fail in all this
	 */
	public Object extFn(String className, String methodName, Object... args)
			throws Exception {
		Class<?> cls = Class.forName(className, true, null);  // null=>bootstrap class loader
		List<Class<?>> argTypes = new ArrayList<Class<?>>();
		for(Object arg : args)
			argTypes.add(arg.getClass());
		Method method = cls.getMethod(methodName, argTypes.toArray(new Class[0]));
		Object instance = cls.newInstance();
		return method.invoke(instance, args);
	}
}
