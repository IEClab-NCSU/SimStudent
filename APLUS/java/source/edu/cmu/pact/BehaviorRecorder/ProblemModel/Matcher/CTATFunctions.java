package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import edu.cmu.hcii.runcc.MemorySerializedParser;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.UsesProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.UsesVariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.equals;
import edu.cmu.pact.Utilities.trace;
import fri.patterns.interpreter.parsergenerator.Parser;
import fri.patterns.interpreter.parsergenerator.Token;
import fri.patterns.interpreter.parsergenerator.semantics.ReflectSemantic;

public class CTATFunctions extends ReflectSemantic {
    private static final String [][] rules = {

        { "EXPRESSION", "TERM" },
        { "EXPRESSION", "EXPRESSION", "'+'", "TERM" },
        { "EXPRESSION", "EXPRESSION", "'-'", "TERM" },
        { "TERM", "FACTOR" },
        { "TERM", "TERM", "'*'", "FACTOR" },
        { "TERM", "TERM", "'/'", "FACTOR" },
        { "FACTOR", "SFACTOR" },
        { "FACTOR", "'-'", "FACTOR" },	// need LALRParserTables instead of SLRParserTables because of this rule
        { "SFACTOR", "'('", "EXPRESSION", "')'" },
        { "SFACTOR", "FUNCALL" },
        { "SFACTOR", "VARREF" },
        { "SFACTOR", "LITERAL", },
        { "FUNCALL", "`identifier`", "'('", "')'" },
        { "FUNCALL", "`identifier`", "'('", "ARGS", "')'" },
        { "ARGS", "ARGS", "','", "ARG" },
        { "ARGS", "ARG" },
//        { "ARG", "FUNCALL" },
//        { "ARG", "LITERAL" },
//        { "ARG", "VARREF" },
        { "ARG", "EXPRESSION" },
        { "LITERAL", "RESERVED" },
        { "LITERAL", "NUMBER" },
        { "LITERAL", "STRING" },
        { "RESERVED", "\"null\"" },
        { "RESERVED", "\"true\"" },
        { "RESERVED", "\"false\"" },
        { "STRING", "`stringdef`" },
        { "NUMBER", "`number`", },
        { "VARREF", "VAR" },
        { "VAR", "VAR", "'.'", "SIMPLEVAR" },
        { "VAR", "SIMPLEVAR" },
        { "SIMPLEVAR", "`identifier`" },
        { Token.IGNORED, "`whitespaces`" },
    };

    private boolean _validateMode = false, _valid = true;
    private String _validMsg;
    private Class _returnType = null;
    private String _selection = null, _action = null, _input = null;
    private VariableTable _variableTable;
    private ProblemModel _problemModel;

    /**
     * Constructor will need {@link Parser} later.
     * @param variableTable
     * @param problemModel
     */
    public CTATFunctions(VariableTable variableTable, ProblemModel problemModel) {
        this(variableTable, problemModel, null);
    }

    /**
     * Constructor accepting an externally-supplied {@link Parser}. 
     * @param variableTable
     * @param problemModel
     * @param parser value for {@link #_parser}
     */
    public CTATFunctions(VariableTable variableTable, ProblemModel problemModel, Parser parser) {
        _variableTable = variableTable;
        _problemModel = problemModel;
        _parser = parser;
        if (_parser != null)
        	_parser.setPrintStream(new PrintStream(parserStream()));
    }

    private static final String NO_VALUE = "NULL"; // "No reference value.";  
    
    private Object getVariableReference(Object variable) {
        if (trace.getDebugCode("functions")) trace.outln("functions", "looking up variable reference "+
        		variable+" in vt #"+_variableTable.getInstance());
        if ("selection".equals(variable))
            return _selection;
        if ("action".equals(variable))
            return _action;
        if ("input".equals(variable))
            return _input;
        Object o = _variableTable.get(variable, _problemModel);
        return (o==null && _validateMode) ? NO_VALUE : o;
    }

    protected Class paramClass(Object param) {
        if (param instanceof Integer)
            return int.class;
        else if (param instanceof Double)
            return double.class;
        else if (param instanceof Boolean)
            return boolean.class;
        else if (param==null)
            return null;
        return param.getClass();
    }

    /**
     * Tell whether class c1 is a superclass of class c2.
     * @param c1
     * @param c2
     * @return true if c1 is a superclass of c2
     */
    protected static boolean isSuperclass(Class c1, Class c2) {
        Class superClass = c2;
        while (superClass!=null) {
            if (superClass==c1)
                return true;
            superClass = superClass.getSuperclass();
        }
        return false;
    }
	
    /** Array element type letters in function signatures; use with {@link #elementTypes}. */
    private static final char[] elementTypeEncodings = new char[] {
        'Z', 'B', 'C', 'D', 'F', 'I', 'J', 'S'
    };
	
    /** Primitive class instances; use with {@link #elementTypeEncodings}. */
    private static final Class[] elementTypes = new Class[] {
        boolean.class, byte.class, char.class, double.class, float.class,
        int.class, long.class, short.class
    };

    /**
     * Return the element {@link Class} of the variably-numbered argument of a varargs method.
     * @param m method that takes a variable number of arguments
     * @return class of the argument with variable arity
     */
    public static Class varArgClass(Method m) {
        if (m.getParameterTypes().length==0)
            return null;
        Class lastParam = m.getParameterTypes()[m.getParameterTypes().length-1];
        if (!m.isVarArgs() || !lastParam.isArray())
            return null;
	
        String lastParamName = lastParam.getName();
        int lastParamLen = lastParamName.length();
        int i;

        for (i=0; i<lastParamLen && lastParamName.charAt(i)=='['; i++);
        char lastParamChar = lastParamName.charAt(i);
        if (lastParamChar=='L') {
            String className = lastParamName.substring(i+1, lastParamLen-1);
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                System.err.println("no class found: " + className);
                return null;
            }
        }
        for (int eltTypeIdx = 0; eltTypeIdx<elementTypes.length; eltTypeIdx++) {
            if (elementTypeEncodings[eltTypeIdx]==lastParamChar)
                return elementTypes[eltTypeIdx];
        }
        return null;
    }
    
    protected boolean matchSignature(Method m, String name, Class[] params, List args) {
        if (!name.equals(m.getName()))
            return false;
        if (!argCountOk(m, params))
            return false;
        if (_validateMode)
            return true;

        Class varArgClass = varArgClass(m);
        for (int i=0; i<m.getParameterTypes().length; i++) {
            if (varArgClass!=null && i == m.getParameterTypes().length-1) {
                for (int j=i; j<params.length; j++) {
                    if (isAssignableFrom(varArgClass, params[j]))
                    	continue;
                    if (canCastArg(varArgClass, args, j))
                    	continue;
                    if (!canConvertToNumber(varArgClass, args, j))
                    	return false;
                }
            } else {
            	Class argClass = m.getParameterTypes()[i];
            	if (isAssignableFrom(argClass, params[i]))
            		continue;
            	if (canCastArg(argClass, args, i))
            		continue;
                if (!canConvertToNumber(argClass, args, i))
                	return false;
            }
        }

        return true;
    }

    /**
     * Test whether the given class is numeric and the argument value can be numeric.
     * @param cls target class to test
     * @param args list of actual arguments
     * @param i index of argument to test
     * @return true if cls is numeric and args[i] can be converted by {@link #toNumber(Object)}
     */
    private boolean canConvertToNumber(Class cls, List args, int i) {
    	if (trace.getDebugCode("functions")) trace.outNT("functions", "cls "+cls+", arg["+i+"] "+args.get(i)+
    			", isInstance(Number) "+cls.isInstance(Number.class)+", toNumber() "+toNumber(args.get(i)));
		if (!(cls.isPrimitive() && cls != Boolean.TYPE && cls != Character.TYPE)  // cls neither a primitive
				&& !Number.class.isAssignableFrom(cls))                      // nor a reference numeric type
			return false;
		return toNumber(args.get(i)) != null;
	}

	/**
     * Return true if a given argument in a list can be cast to an instance of the target class.
     * @param tgtClass
     * @param args list of arguments
     * @param j index of wanted argument in args
     * @return true if cast is legal
     */
    private boolean canCastArg(Class tgtClass, List args, int j) {
        if (args == null)
        	return false;
        return canCast(tgtClass, args.get(j));
    }

	/**
     * Return true if it's legal to cast the given arg to an instance of the target class.
     * @param tgtClass
     * @param arg
     * @return obj, or, if boxed, value in the box
     */
    private boolean canCast(Class tgtClass, Object arg) {
    	try {
        	if (arg == null)
        		return !tgtClass.isPrimitive();
        	if (!tgtClass.isPrimitive())
        		return tgtClass.isInstance(arg);
        	else if (arg instanceof Boolean)
        		return tgtClass.equals(Boolean.TYPE);
        	else if (arg instanceof Character)
        		return tgtClass.equals(Character.TYPE);
        	else if (!(arg instanceof Number))
        		return false;                       // tgtclass is a primitive numeric type
        	else if (tgtClass.equals(Double.TYPE))
        		return true;                        // can widen any numeric to double
        	else if (arg instanceof Double)
        		return false;                       // can't narrow double to anything else
        	else if (tgtClass.equals(Float.TYPE))
        		return true;                        // can widen any non-double to float
        	else if (arg instanceof Float)
        		return false;                       // can't narrow float to anything else
        	else if (tgtClass.equals(Long.TYPE))
        		return true;                        // can widen any non-float to long
        	else if (arg instanceof Long)
        		return false;                       // can't narrow long to any other integer
        	else
        		return true;                        // accept all ints and smaller
        } catch (ClassCastException cce) {
            return false;
        }
	}

	/**
     * Tell whether a reference to an object of the lhsClass can be assigned a value which is a
     * reference to an object of the rhsClass. This mimics {@link Class#isAssignableFrom(Class)}
     * except that it doesn't throw an exception if the rhsClass is null.
     * @param lhsClass the class to assign to
     * @param rhsClass the class to assign from
     * @return false if lhsClass is null;
     *  true if rhsClass is null, unless {@link Class#isPrimitive() lhsClass#isPrimitive()} is true;
     *  else the result of {@link Class#isAssignableFrom(Class) lhsClass#isAssignableFrom(rhsClass)}
     */
    private boolean isAssignableFrom(Class lhsClass, Class rhsClass) {
    	if (lhsClass == null)
    		return false;
    	if (rhsClass == null)
    		return !(lhsClass.isPrimitive());
    	return lhsClass.isAssignableFrom(rhsClass);
    }

    /**
     * Check that a list of parameter types has the right number of arguments for a method.
     * @param m method to test
     * @param actualParamClasses array of types of actual parameters
     * @return true if 
     */
    private static boolean argCountOk(Method m, Class[] actualParamClasses) {
    	if (m.isVarArgs())  // for varargs, ensure there are enough args
    		return m.getParameterTypes().length <= actualParamClasses.length;
    	else                // else require exact number
    		return m.getParameterTypes().length == actualParamClasses.length;
	}

    /**
     * Call method having a variable number of arguments. 
     * @param m
     * @param o
     * @param args
     * @return
     * @throws Exception
     */
	protected Object varArgsInvoke(Method m, Object o, List args) throws Exception {
		Class[] paramTypes = m.getParameterTypes(); 
        int lastParamIdx = paramTypes.length-1;
        Class varArgCls = varArgClass(m);
        Object varArgs = Array.newInstance(varArgCls, (args.size()-lastParamIdx));
        
        for (int i=lastParamIdx; i<args.size(); i++)
        	Array.set(varArgs, i-lastParamIdx, convertArg(varArgCls, args.get(i)));

        if (args.size() > lastParamIdx) {
            switch (lastParamIdx) {
                case 0:
                    return m.invoke(o, varArgs);
                case 1:
                    return m.invoke(o, convertArg(paramTypes[0], args.get(0)), varArgs);
                case 2:
                    return m.invoke(o, convertArg(paramTypes[0], args.get(0)),
                    		convertArg(paramTypes[1], args.get(1)), varArgs);
                case 3:
                    return m.invoke(o, convertArg(paramTypes[0], args.get(0)),
                    		convertArg(paramTypes[1], args.get(1)),
                    		convertArg(paramTypes[2], args.get(2)), varArgs);
                case 4:
                    return m.invoke(o, convertArg(paramTypes[0], args.get(0)),
                    		convertArg(paramTypes[1], args.get(1)),
                    		convertArg(paramTypes[2], args.get(2)),
                    		convertArg(paramTypes[3], args.get(3)), varArgs);
                default:
                    throw new IllegalStateException("var args method with more than " + lastParamIdx + " fixed arguments: " + m);
            }
        }
        return null;
    }

	/**
	 * Invoke the given instance method with the given arguments, converting strings to numbers if needed.
	 * @param m
	 * @param o
	 * @param args actual arguments
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	Object argsInvoke(Method m, Object o, List args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object[] actualArgs = new Object[args.size()];
		Class[] formalArgTypes = m.getParameterTypes();
		for (int i = 0; i < Math.min(actualArgs.length, formalArgTypes.length); ++i) {
			actualArgs[i] = convertArg(formalArgTypes[i], args.get(i));
		}
		return m.invoke(o, actualArgs);
	}

	/**
	 * 
	 * @param cls
	 * @param obj
	 * @return
	 */
    private Object convertArg(Class cls, Object obj) {
    	if (trace.getDebugCode("functions")) trace.outNT("functions", "cls "+cls+", obj "+obj+
    			(obj == null ? "" : ", type "+obj.getClass().getSimpleName())+
    			(obj == null ? "" : ", assignable "+cls.isAssignableFrom(obj.getClass())));
    	if (obj == null)
    		return obj;
		if (cls.isAssignableFrom(obj.getClass()))
			return obj;
		if (!cls.isPrimitive())
			return obj;
		else if (cls == Boolean.TYPE)
			return Boolean.parseBoolean(obj.toString());
		else if (cls == Character.TYPE)
			return obj.toString().charAt(0);
		Number result = toNumber(obj);
		if (result == null)
			return null;
		if (cls == Float.TYPE && result.getClass() == Double.class)  // can't auto-shrink
			return Float.valueOf((float) result.doubleValue());
		return result;
	}

    /**
     * For matching method signatures, try to choose the best argument type match.
     * @param desiredParams types of the desired method's formal arguments
     * @param m1 one method to compare
     * @param m2 other method to compare
     * @return m1 or m2
     */
	protected static Method compareMethods(Class[] desiredTypes, Method m1, Method m2) {
        if (m1==null)
            return m2;
        Class[] m1Types = m1.getParameterTypes();
        Class[] m2Types = m2.getParameterTypes();
        for (int i=0; i<m1Types.length; i++) {    // 1st scan for exact argument type match
            if (m1Types[i] == m2Types[i])
            	continue;
            if (m1Types[i] == desiredTypes[i])
            	return m1;
            if (m2Types[i] == desiredTypes[i])
            	return m2;
        }
        for (int i=0; i<m1Types.length; i++) {    // then scan for more specific argument type
        	if (m1Types[i] == m2Types[i])
        		continue;
        	if (isSuperclass(m1Types[i], m2Types[i]))
        		return m2;
        	if (isSuperclass(m2Types[i], m1Types[i]))
        		return m1;
        }
        return m1;
    }
    
    protected Method lookupMethod(Class c, String name, Class[] params, List args) {
        Method[] methods = c.getMethods();
        Method method = null;
        for (int i=0; i<methods.length; i++) {
            if (matchSignature(methods[i], name, params, args))
                method = compareMethods(params, method, methods[i]);
        }
        return method;
    }
    
    protected Method lookupMethod(Class c, String name, List args) {
        Class[] params = new Class[args.size()];
	
        for (int i=0; i<params.length; i++)
            params[i] = paramClass(args.get(i));

        return lookupMethod(c, name, params, args);
    }
    
    // { "FUNCALL", "`identifier`", "'('", "')'" },
    public Object FUNCALL(Object IDENTIFIER, Object leftParen, Object rightParen) throws IllegalStateException {
    	if (trace.getDebugCode("functions")) trace.outln("functions", "parsing function "+IDENTIFIER+"() w/ no arguments");
    	return FUNCALL(IDENTIFIER, leftParen, new ArrayList(), rightParen);
    }
        
    // { "FUNCALL", "`identifier`", "'('", "ARGS", "')'" },
    public Object FUNCALL(Object IDENTIFIER, Object leftParen, Object ARGS, Object rightParen) throws IllegalStateException {
    	String id = (String)IDENTIFIER;
        List args = (List)ARGS;
        Object[] obj = (args.size() > 0 ? new Object[1] : null); 
        if (obj != null)
        	obj[0] = args.get(0);
        List paramArgs = (args.size() > 0 ? args.subList(1, args.size()) : new ArrayList());

        if (trace.getDebugCode("functions")) trace.outln("functions", "function name: " + IDENTIFIER + ", args: " + showArgs(args));

        try {
            Object o = null;
            // lookup method on class of first arg; skip java.lang.Object#equals(Object)
            Method m = (obj == null || obj[0] == null ? null :
            	lookupMethod(obj[0].getClass(), id, paramArgs));
            m = omitObjectEquals(m);
            if (m!=null) {
                o = obj[0];
                args = paramArgs;
            } else {
                // lookup method on Math class
                m = lookupMethod(Class.forName("java.lang.Math"), id, args);
                m = omitObjectEquals(m);
                if (m==null) {
                    // lookup custom method defined in Functions
                	String functionId = "edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions." + id;
                	Class functionClass = null;
                	try {
                		functionClass = Class.forName(functionId);
                    } catch (ClassNotFoundException cnfe) {
                		if (trace.getDebugCode("functions"))
                			trace.out("functions", "no such class: "+functionId);
                		functionClass = null;
                	}
                    if (functionClass != null) {
                        m = lookupMethod(functionClass, id, args);
                        if (m==null) {
                        	m = lookupStringArgsMethod(functionClass, id, args.size());
    						if (m!=null)
                        		args = stringify(args);
                        }
                        if (m!=null) {
                            o = functionClass.newInstance();
                            setProblemModelVariableTableInFunction(o);
                        }
                    }
                }
                if (m==null) {  // sewall 2012/01/26 if 1st arg null, try with String "null"
                	if (_validateMode || (obj != null && obj[0] == null)) {
                		m = lookupMethod(String.class, id, paramArgs);
                		o = new String("null");   // create instance for argsInvoke() below
                        args = paramArgs;
                	}
                }
            }
            if (trace.getDebugCode("functions")) trace.outNT("functions", "CTATFunctions.FUNCALL(ID,(,ARGs,)) found method\n "+
            		(m == null ? "null" : m.toGenericString()));

            if (m!=null) {
                setReturnType(m.getReturnType());
                if (m.isVarArgs())
                    return _validateMode ? NO_VALUE : varArgsInvoke(m, o, args);
                else
                    return _validateMode ? NO_VALUE : argsInvoke(m, o, args);
            } else
                throw new IllegalStateException("no method found: "+id+"("+showArgs(args)+")");
        } catch (Exception e) {
            System.err.println(e);
            if (_validateMode) {
                _valid = false;
                _validMsg = "Function not found: " + id + "(";
                try {
                    if (args!=null) {
                        for (int i=0; i<args.size(); i++) {
                            if (i>0)
                                _validMsg += ", ";
                            _validMsg += args.get(i).getClass().getName();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
                _validMsg += ")";
                // _validMsg = e.toString();
                return NO_VALUE;
            } else
                throw new IllegalStateException(e);
        }
    }
    
    /**
     * If an author function implements {@link UsesProblemModel}, provide a reference
     * to the {@link #_problemModel}.
     * If an author function implements {@link UsesVariableTable}, provide a reference
     * to the {@link #_variableTable}.
     * @param fn author function instance
     */
    private void setProblemModelVariableTableInFunction(Object fn) {
    	if (fn instanceof UsesProblemModel)
    		((UsesProblemModel) fn).setProblemModel(_problemModel);
    	if (fn instanceof UsesVariableTable)
    		((UsesVariableTable) fn).setVariableTable(_variableTable);
	}

	/**
     * This method returns null if the passed method matches the signature for
     * {@link Object#equals(Object)}. The intent is to have {@link equals#equals(Object, Object...)}
     * handle that test; that method will return true when numeric objects have the same value
     * but differ in type.
     * @param m method to check 
     * @return null if m is {@link Object#equals(Object)} or an override; else m
     */
    private Method omitObjectEquals(Method m) {
        if (m == null)
        	return null;
        if (!"equals".equals(m.getName()))
        	return m;
        Class[] params = m.getParameterTypes();
        if (params.length != 1)
        	return m;
        if (Object.class.equals(params[0]))
        	return null;
        else
        	return m;
	}

	/**
     * Dump a list of arguments as class names-value pairs.
     * @param args List to dump
     * @return String with "class value, class value, ...";
     *         just "(null)" if args null
     */
    private String showArgs(List args) {
    	if (args == null)
    		return "**null list**";
    	StringBuffer sb = new StringBuffer("(");
    	for (Object arg : args) {
    		if (arg == null)
    			sb.append("null");
    		else
    			sb.append(arg.getClass().getSimpleName()).append(' ').append(arg.toString());
    		sb.append(", ");
    	}
    	if (args.size() > 0)
    		sb.replace(sb.length()-2,sb.length(),")");  // remove trailing ", "
    	else
    		sb.append(')');
		return sb.toString();
	}

	/**
     * Replace each non-null element in the list with its {@link #stringify(Object)} result. 
     * @param args List to alter
     * @return args
     */
    public static  List<String> stringify(List args) {
    	List<String> result = new ArrayList<String>(args.size());
    	for (Object element : args)
    		result.add(stringify(element));
    	return result;
	}
    
    /**
     * Format an object as s String: {@link #stringifyNumber(Number)} if a number,
     * else {@link Object#toString()}.
     * @param obj
     * @return null if null, else string as above.
     */
    public static String stringify(Object obj) {
    	if (obj == null)
    		return(null);
    	else if (obj instanceof Number)
    		return(stringifyNumber((Number) obj));
    	else
    		return(obj.toString());
    }
    
    /**
     * Format a number as a string. Doubles having no fractional part are formatted as integers.
     * @param n number as object; must not be null
     * @return n.toString() unless n is floating point; then modified as above
     */
    public static String stringifyNumber(Number n) {
		String result = n.toString();
		if (n instanceof Double || n instanceof Float)
			return (result.endsWith(".0") ? result.substring(0, result.length()-2) : result);
		else
			return result;
	}

	/**
     * Call {@link #lookupMethod(Class, String, Class[])} to find a method whose arguments are all
     * Strings.
     * @param functionClass method class
     * @param id method name
     * @param nArgs number of arguments
     * @return result of {@link #lookupMethod(Class, String, Class[])}
     */
    private Method lookupStringArgsMethod(Class functionClass, String id, int nArgs) {
    	Class<String>[] stringArgs = new Class[nArgs];
    	Arrays.fill(stringArgs, String.class);
		return lookupMethod(functionClass, id, stringArgs, null);
	}

	// { "ARGS", "ARGS", "','", "ARG" },
    public Object ARGS(Object ARGS, Object comma, Object ARG) {
        ((List)ARGS).add(ARG);
        return ARGS;
    }
    
    // { "ARGS", "ARG" }
    public Object ARGS(Object ARG) {
        ArrayList list = new ArrayList();
        list.add(ARG);
        return list;
    }

    // { "ARG", "FUNCALL" },
    // { "ARG", "LITERAL" },
    public Object ARG(Object o) {
        return o;
    }
    
    // { "LITERAL", "STRING" }
    // { "LITERAL", "INT" }
    // { "LITERAL", "FLOAT" }
    public Object LITERAL(Object o) {
        if (trace.getDebugCode("functions"))
        	trace.outNT("functions",
        			"LITERAL("+(o == null ? "" : o.getClass().getSimpleName())+" "+o+")");
        setReturnType(o);
        return o;
    }
    
//    { "RESERVED", "\"null\"" },
//    { "RESERVED", "\"true\"" },
//    { "RESERVED", "\"false\"" },
   public Object RESERVED(Object o) {
        if (trace.getDebugCode("functions"))
        	trace.outNT("functions",
        			"RESERVED("+(o == null ? "" : o.getClass().getSimpleName())+" "+o+")");
        if (o instanceof String) {
        	if ("null".equals(o))
        		o = null;
        	else if ("true".equals(o))
        		o = Boolean.TRUE;
        	else if ("false".equals(o)) 
        		o = Boolean.FALSE;
        }
        return o;
    }

    // { "NUMBER", "`number`" },
    public Object NUMBER(Object numdef) {
        try {
        	Integer result = Integer.valueOf(numdef.toString());
            if (trace.getDebugCode("functions")) trace.outNT("functions", "NUMBER("+(numdef == null ? "" : numdef.getClass().getSimpleName())+
            		" "+numdef+") rtns (Integer) "+result);
        	setReturnType(Integer.class);
        	return result;
        } catch (NumberFormatException nfe) {
        	Double result = Double.valueOf(numdef.toString());
            if (trace.getDebugCode("functions")) trace.outNT("functions", "NUMBER("+(numdef == null ? "" : numdef.getClass().getSimpleName())+
            		" "+numdef+") rtns (Double) "+result);
        	setReturnType(Double.class);
        	return result;
        }
    }

    // { "STRING", "`stringdef`" },
    public Object STRING(Object stringdef) {
        if (trace.getDebugCode("functions")) trace.outNT("functions", "STRING("+(stringdef == null ? "" : stringdef.getClass().getSimpleName())+
        		" "+stringdef+")");
        setReturnType(String.class);
        return ((String)stringdef).substring(1, ((String)stringdef).length()-1);
    }

    // return getVariableReference(VAR);
    // { "VARREF", "VAR" },
    public Object VARREF(Object VAR) {
        Object value = getVariableReference(VAR);
        if (trace.getDebugCode("functions")) trace.outNT("functions", "VARREF("+VAR+") -> "+
        		(value == null ? "null" : value.getClass().getSimpleName())+" "+value);
        setReturnType(value);
        return value;
    }
    
    // { "VAR", "VAR", "'.'", "SIMPLEVAR" },
    public Object VAR(Object VAR, Object dot, Object SIMPLEVAR) {
        if (trace.getDebugCode("functions"))
        	trace.outNT("functions",
        			"VAR("+(VAR == null ? "" : VAR.getClass().getSimpleName())+" "+VAR+") '"+
        			dot+"' "+(SIMPLEVAR == null ? "" : SIMPLEVAR.getClass().getSimpleName())+" "+SIMPLEVAR+")");
        return ((String)VAR) + "." + ((String)SIMPLEVAR);
    }
    // { "VAR", "SIMPLEVAR" },
    public Object VAR(Object SIMPLEVAR) {
        if (trace.getDebugCode("functions"))
        	trace.outNT("functions",
        			"VAR("+(SIMPLEVAR == null ? "" : SIMPLEVAR.getClass().getSimpleName())+" "+SIMPLEVAR+")");
        return SIMPLEVAR;
    }

    public Object EXPRESSION(Object TERM)	{
        return TERM;	// do not really need this method as ReflectSemantic.fallback() does this
    }
    // *** SUBCLASS FOR VALIDATION ***
    public Object EXPRESSION(Object EXPRESSION, Object operator, Object TERM)	{
        if (trace.getDebugCode("functions")) trace.outln("functions", "EXPRESSION(" + EXPRESSION + ", " + operator + ", " + TERM + ")");
        if (trace.getDebugCode("functions")) trace.outln("functions", "EXPRESSION is a " +
        		(EXPRESSION == null ? "null" : EXPRESSION.getClass().getName()));
        if (trace.getDebugCode("functions")) trace.outln("functions", "TERM is a " + 
				(TERM == null ? "null" : TERM.getClass().getName()));
        setReturnType(Double.class);
        if (_validateMode)
            return NO_VALUE;
        return binaryArithmeticOperation((String) operator, EXPRESSION, TERM);
    }

    /**
     * Perform a binary arithmetic operation.
     * @param operator first char '+', '-', '*' or '/' determines operation
     * @param operand1 left-hand operand
     * @param operand2 right-hand operand
     * @return result of the operation, in type (Integer, Long or Double) of the widest operand
     */
	private Object binaryArithmeticOperation(Object operator, Object expression, Object term) {
        char op = ((String) operator).charAt(0);
        Integer i1 = toInteger(expression);
        Integer i2 = toInteger(term);
        if (trace.getDebugCode("functions")) trace.outNT("functions", "binaryArithmeticOp("+operator+","+expression+","+term+"): i1 "+i1+",i2 "+i2);
        if (i1 != null && i2 != null) {
            setReturnType(Integer.class);
            switch (op) {
            case '+':
                return new Integer(i1.intValue() + i2.intValue());
            case '-':
            	return new Integer(i1.intValue() - i2.intValue());
            case '*':
                return new Integer(i1.intValue() * i2.intValue());
            case '/':
            	Number result = divideInteger(i1, i2);
                setReturnType(result.getClass());
            	return result;
            }
        	throw new IllegalArgumentException("CTATFunctions: undefined arithmetic operator \""+op+"\"");
        }
        Double d1 = toDouble(expression);
        Double d2 = toDouble(term);
        if (d1 != null && d2 != null) {
        	Double result = null;
            switch (op) {
            case '+':
                result = new Double(d1.doubleValue() + d2.doubleValue()); break;
            case '-':
            	result = new Double(d1.doubleValue() - d2.doubleValue()); break;
            case '*':
                result = new Double(d1.doubleValue() * d2.doubleValue()); break;
            case '/':
            	if (d2.doubleValue() == 0)
                	throw new IllegalArgumentException("CTATFunctions: divide by zero \""+op+"\"");
            	result = new Double(d1.doubleValue() / d2.doubleValue()); break;
            default:
            	throw new IllegalArgumentException("CTATFunctions: undefined arithmetic operator \""+op+"\"");
            }
            Integer intResult = toInteger(result);
            if (intResult != null) {
                setReturnType(Integer.class);
            	return intResult;
            } else {
                setReturnType(Double.class);
            	return result;
            }
        }
        setReturnType(null);
		return null;             // at least one operand is not numeric
	}

	/**
	 * Divide integers as a desk calculator would: return an Integer if the result has no remainder;
	 * return a Double otherwise.
	 * @param dividend
	 * @param divisor
	 * @return quotient as Integer or Double
	 * @throws IllegalArgumentException if divisor is zero
	 */
	private Number divideInteger(Integer dividend, Integer divisor) {
		int n = dividend.intValue();
		int d = divisor.intValue();
    	if (d == 0)
        	throw new IllegalArgumentException("CTATFunctions: divide by zero \""+dividend+"/"+divisor+"\"");
		if (n%d == 0)
			return new Integer(n/d);
        return new Double(dividend.doubleValue()/divisor.doubleValue());
	}

	public Object TERM(Object FACTOR)	{
        return FACTOR;	// do not really need this method as ReflectSemantic.fallback() does this
    }
    // *** SUBCLASS FOR VALIDATION ***
    public Object TERM(Object TERM, Object operator, Object FACTOR)	{
        setReturnType(Double.class);
        if (_validateMode)
            return NO_VALUE;
        return binaryArithmeticOperation((String) operator, TERM, FACTOR);
    }
    // *** SUBCLASS FOR VALIDATION ***
    // value can be a string, variable reference, or function call evaluation
    public Object FACTOR(Object SFACTOR)	{
        if (trace.getDebugCode("functions")) trace.outNT("functions", "FACTOR("+(SFACTOR == null ? "" : SFACTOR.getClass().getSimpleName())+
        		" "+SFACTOR+")");
        setReturnType(SFACTOR);
        return SFACTOR;
    }
    // *** SUBCLASS FOR VALIDATION ***
    // value can be a string, variable reference, or function call evaluation
    public Object SFACTOR(Object value)	{
        if (trace.getDebugCode("functions")) trace.outNT("functions", "SFACTOR("+(value == null ? "" : value.getClass().getSimpleName())+
        		" "+value+")");
        setReturnType(value);
        return value;
    	// sewall 2009/05/07: removed following, since callers are all converting args for themselves
//        try {
//            boolean isFP = VariableTable.isFloatingPoint(value);
//            if (isFP)
//                setReturnType(Double.class);
//            Object result = (value instanceof String) ? Double.valueOf((String)value) :  value;
//            trace.outNT("functions", "FACTOR("+value.getClass().getSimpleName()+" "+value+") isFP "+isFP+
//            		" -> "+result.getClass().getSimpleName()+" "+result);
//            return result;
//        } catch (NumberFormatException ex) {
//            setReturnType(value);
//            return value;
//        }
    }
    // *** SUBCLASS FOR VALIDATION ***
    public Object FACTOR(Object minus, Object FACTOR)	{
        setReturnType(Double.class);
        if (_validateMode)
            return NO_VALUE;
        Double factor = toDouble(FACTOR);
        if (factor == null) {
        	setReturnType(null);
        	return null;  // sewall 2009/01/15: don't use NO_VALUE since not _validateMode?
        }
        return new Double(-factor.doubleValue());
    }
    public Object SFACTOR(Object leftParenthesis, Object EXPRESSION, Object rightParenthesis)	{
        setReturnType(EXPRESSION);
        return EXPRESSION;
    }

    public String getValidMsg() {
        return _validMsg;
    }

    public Class getReturnType() {
        return _returnType;
    }
    public void setReturnType(Object o) {
        if (NO_VALUE==o)
            return;
        if (o==null || o instanceof Class)
            _returnType = (Class)o;
        else
            _returnType = o.getClass();
    }

    private ByteArrayOutputStream _parserStream;
    private ByteArrayOutputStream parserStream() {
        if (_parserStream==null)
            _parserStream = new ByteArrayOutputStream();
        return _parserStream;
    }

    public String errorString() {
        String err = parserStream().toString();
        if (err==null || err.length()==0)
            err = _validMsg;
        return err;
    }
    
    private Parser _parser;
    private Parser parser() throws Exception {
    	long now = new Date().getTime();
    	if (trace.getDebugCode("functions")) trace.out("functions", "before parser constr: _parser "+_parser);
        if (_parser==null) {
            _parser = new MemorySerializedParser().get(getRules(), "CTATFunctions");
            _parser.setPrintStream(new PrintStream(parserStream()));
        }
    	if (trace.getDebugCode("functions")) trace.out("functions", " after parser constr: took (ms) "+
    			((new Date().getTime())-now));
        return _parser;
    }

    /** Pattern to break strings into formula & non-formula substrings. */
    private static final Pattern interpolateSplitPattern = Pattern.compile("<%=|%>");
    
    private static String[] interpolateSplit(String message) {
    	return interpolateSplitPattern.split(message);  // sewall 2/3/09: was message.split()
    }

    public static boolean interpolatable(String message) {
        if (message==null)
            return false;
        String[] parts = interpolateSplit(message);
        return parts!=null && parts.length>1;
    }
    
    public String interpolate(String message, String selection, String action, String input) {
        if (trace.getDebugCode("interpolate")) trace.outln("interpolate", "will interploate: " + message);
        String[] parts = interpolateSplit(message);
        StringBuffer interpolated = new StringBuffer();

        for (int i=0; i<parts.length; i++) {
            Object nextPart;

            try {
                nextPart = ((i % 2)==1 ? evaluate(parts[i], selection, action, input) : parts[i]);
            } catch (Exception e) {
                System.err.println("Error evaluating " + parts[i] + ": " + e);
                e.printStackTrace(System.err);
                nextPart = "#!ERROR";
            }
            interpolated.append(nextPart);
        }
        
        if (trace.getDebugCode("interpolate")) trace.outln("interpolate", "interploated: " + interpolated);
        return interpolated.toString();
    }
    

    public String interpolate(String message) {
        return interpolate(message, null, null, null);
    }
    
    public Object evaluate(String expression, String selection, String action, String input) throws Exception {
        _selection = selection;
        _action = action;
        _input = input;
        if (trace.getDebugCode("functions"))
        	trace.out("functions", "evaluate to parse expression [" + expression + "]");
        boolean ok = parser().parse(expression, this);
        Object result = (ok ? parser().getResult() : null);
        if (trace.getDebugCode("functions"))
        	trace.out("functions", "evaluated expression [" + expression + "]: ok? "+ok+", result "+result);
        if (ok)
            return result;
        if (trace.getDebugCode("functions")) trace.outln("functions", "error string is " + errorString());
        throw new IllegalStateException(errorString());
    }

    public Object evaluate(String expression) throws Exception {
        return evaluate(expression, null, null, null);
    }

    private String rootCause(Throwable e) {
        if (e.getCause()!=null)
            return rootCause(e.getCause());
        return e.getMessage();
    }

    public boolean validate(String input) {
        try {
            _validateMode = true;
            _valid = true;
            evaluate(input);
            _validateMode = false;
            if (!_valid) {
                if (trace.getDebugCode("functions")) trace.outln("functions", input + " is not valid");
                if (trace.getDebugCode("functions")) trace.outln("functions", _validMsg);
            } else
                if (trace.getDebugCode("functions")) trace.outln("functions", input + " is valid");
            if (trace.getDebugCode("functions")) trace.outln("functions", "return type: " + getReturnType());
            return _valid;
        } catch (Exception e) {
            _validMsg = input + " is not valid:\n" + e.getMessage();
            System.err.println(_validMsg);
            return false;
        } finally {
            _validateMode = false;
        }
    }
	
    /** SYNTAX: java fri.patterns.interpreter.parsergenerator.examples.Calculator '(4+2.3) *(2 - -6) + 3*2' ... 56.4. */
    public static void main(String [] args) throws Exception {
        if (args.length <= 0)	{
            System.err.println("SYNTAX: java "+CTATFunctions.class.getName()+" \"function(arg1, arg2, ...)\"");
            System.exit(1);
        }
		
        String input = args[0];
        System.err.println("Processing input >"+input+"<");

        VariableTable vt = new VariableTable();
        vt.put("x", new Double(2));
        vt.put("y", new Double(3));
        vt.put("str", "abcdefg");
        CTATFunctions ctat = new CTATFunctions(vt, null);
        try {
            trace.out(input+" = "+ctat.evaluate(input));
        } catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace(System.err);
        }
    }
    
    /**
     * Convert the argument to a Double if possible. Will parse String arguments.
     * @param o String or Number
     * @return Double if can be interpreted as double; else null
     */
    public static Double toDouble(Object o) {
    	Number n = toNumber(o);
    	if (n instanceof Double)
    		return (Double) n;
    	else if (n instanceof Number)
    		return new Double(n.doubleValue());
    	else
    		return null;
    }
    
    /**
     * Convert the argument to an Integer if possible. Will parse String arguments.
     * Will return null if argument is numeric but not integral.
     * @param o String or Number
     * @return Integer if can be interpreted as int; else null
     */
    public static Integer toInteger(Object o) {
    	Number n = toNumber(o);
    	if (n instanceof Integer)
    		return (Integer) n;
    	else if (n instanceof Double) {
    		Long nL = new Long(Math.round(n.doubleValue()));
    		if (nL.doubleValue() == n.doubleValue())
    			return new Integer(nL.intValue()); 
    	}
    	return null;
    }
    
    /**
     * Convert the argument to a Integer or Double if possible. Will parse String arguments.
     * @param o String or Number
     * @return Integer if can be interpreted as int; Double if numeric but not int; else null
     */
    public static Number toNumber(Object o) {
		if (o == null)
			return null;
		if (o instanceof Number) {
			return (Number) o;
//			return intOrDouble((Number) o);
		}
		if (o instanceof String) {
			try {
				Integer i = Integer.valueOf((String) o);
				return i;
			} catch (NumberFormatException nfe) {
				try {
					Double d = Double.valueOf((String) o);
					return d;
				} catch (NumberFormatException nfe2) {
					return null;
				}
			}
		}
		return null;
	}

    /**
     * If the given argument's value is an integer (regardless of its type) and
     * within {@link Integer#MIN_VALUE}, {@link Integer#MAX_VALUE}, then return an Integer. 
     * Else return a Double.
     * @param n
     * @return n as Integer if integral, else as Double; null if n null.
     *         
     */
    private static Number intOrDouble(Number n) {
    	if (n == null)
    		return null;
    	double d = n.doubleValue();
    	if (d < Integer.MIN_VALUE || Integer.MAX_VALUE < d)
    		return new Double(d);
    	double i = (d < 0 ? Math.ceil(d) : Math.floor(d));
    	if (d == i)
    		return new Integer((int) i);
    	else
    		return new Double(d);
    }

	/**
	 * @return the {@link #rules}
	 */
	public static String[][] getRules() {
		return rules;
	}
	public Parser getParser(){
		return _parser;
	}
//	public VariableTable getVariableTable(){
//		return _variableTable;
//	}

	/**
	 * @return the {@link #_variableTable}: use only for cloning
	 */
	VariableTable getVariableTable() {
		return _variableTable;
	}

	/**
	 * @param vt new value for {@link #_variableTable}
	 */
	public void setVariableTable(VariableTable vt) {
		_variableTable = vt;
	}
}
