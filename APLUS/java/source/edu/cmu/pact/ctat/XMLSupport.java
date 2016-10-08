/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.ctat;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import edu.cmu.pact.Utilities.trace;

/**
 * Tool for serializing and deserializing a class instance to and from XML.
 */
public class XMLSupport {
	
	/**
	 * For client classes that want to use this facility. In addition to implementing
	 * this interface, the client class must define public getXxxx() and setXxxx(String)
	 * methods for each property to be saved as an XML attribute.
	 */
	public static interface Client {
		
		/**
		 * Get client's (@link XMLSupport} object. Unless client's set of fields can change
		 * during runtime, recommend this be a static field in the client.
		 * @return value of client's (@link XMLSupport} object
		 */
		public XMLSupport getXMLSupport();
		
		/**
		 * Set client's (@link XMLSupport} object. Unless client's set of fields can change
		 * during runtime, recommend this be a static field in the client.
		 * @param newXMLSupport new value for client's (@link XMLSupport} object
		 */
		public void setXMLSupport(XMLSupport newXMLSupport);
		
		/**
		 * @return true if the named property should be saved as an XML attribute.
		 */
		public boolean isAttrProperty(String propertyName);
	}
	
	/**
	 * Class like {@link java.beans.PropertyDescriptor}, but lighter weight and specialized
	 * for use with XML attributes. 
	 * <p>We would use {@link java.beans.PropertyDescriptor}, but its constructor
	 * complains when the return type of the getter doesn't match the argument type of the
	 * set method. Because we save to (string-valued) XML attributes, we require a setter
	 * that accepts a String argument, regardless of the property's actual type.</p>
	 */
	class AttributeDescriptor {

		/** Name of the attribute in XML. */
		private final String name;
		
		/** Get method, used to write the XML attribute. */
		private Method getMethod;
		
		/** Set method, used to read the XML attribute. Argument must be a String. */
		private Method setMethod;
		
		/**
		 * @param attrName
		 * @param getMethod
		 * @param setMethod
		 * @throws IllegalArgumentException if attrName null or empty
		 */
		public AttributeDescriptor(String attrName, Method getMethod, Method setMethod) {
			if (attrName == null || attrName.length() < 1)
				throw new IllegalArgumentException("attrName null or empty");
			name = attrName;
			this.getMethod = getMethod;
			this.setMethod = setMethod;
		}
		
		/**
		 * For debugging.
		 * @return strings for each Method
		 */
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer(name);
			sb.append(": ").append(getMethod.toString());
			sb.append(", ").append(setMethod.toString());
			return sb.append(';').toString();
		}
	}
	
	/** Set of fields that need to be saved in XML representations. */
	private Map<String, AttributeDescriptor> attrDescs =
		new TreeMap<String, AttributeDescriptor>();

	/**
	 * Populate the {@link #attrDescs} list from the given object's getXxxx methods.
	 * @param instance
	 */
	public XMLSupport(Client instance) {
		for (Method method : instance.getClass().getMethods()) { 
			String attrName = getMethodToPropertyName(method);
			if (attrName == null)
				continue;
			if (!instance.isAttrProperty(attrName))
				continue;
			String setMethodName = "set"+method.getName().substring("get".length());
			Method setMethod = null;
			try {
				setMethod = instance.getClass().getDeclaredMethod(setMethodName, String.class);
				attrDescs.put(attrName, new AttributeDescriptor(attrName, method, setMethod));
			} catch (NoSuchMethodException nsme) {
				throw new Error("getAttrDescriptor(): need to declare a setter method "+
						setMethodName+"(String): "+nsme);
			}
		}
		if (trace.getDebugCode("skills")) trace.out("skills", "XMLSupport attrDescs: "+attrDescs);
	}

	/**
	 * Extract the property name from a getXxxx() method name.
	 * @param methodName
	 * @return property name, by Bean conventions; null if this method name is not
	 *         of the form getXxxx
	 */
	public static String getMethodToPropertyName(Method method) {
		String methodName = method.getName();
		if (methodName.length() < 4)
			return null;
		if (!methodName.startsWith("get") || method.getParameterTypes().length > 0)
			return null;
		String attrName = methodName.substring(3,4).toLowerCase()+methodName.substring(4);
		return attrName;
	}

	/**
	 * Aid to initialize a static field with a class's XMLSupport info. Call this from
	 * an instance initializer or constructor in the client class, as follows:
	 * {
	 *     xmlSupport = XMLSupport.initialize(this);
	 * }
	 * @param client
	 * @param xmlSupport existing value of 
	 * @return xmlSupport if already non-null
	 */
	public static XMLSupport initialize(Client client) {
		synchronized(client.getClass()) {  // mutex on whole class in case client using static
			XMLSupport clientVal = client.getXMLSupport();
			if (clientVal != null)
				return clientVal;
			clientVal = new XMLSupport(client);
			client.setXMLSupport(clientVal);
			return clientVal;
		}
	}

	/**
	 * Set the XML attributes.
	 * @param client
	 * @param elt
	 */
	public void setAttributes(Client client, Element elt) {
		for (String attrName : attrDescs.keySet()) {
			AttributeDescriptor desc = null;
			try {
				desc = attrDescs.get(attrName);
				elt.setAttribute(attrName, desc.getMethod.invoke(client, new Object[0]).toString());
			} catch (NullPointerException npe) {
				trace.err("warning: field "+attrName+" is null; descriptor "+desc);
				continue;
			} catch (Exception e) {
				String err = "error saving attribute "+attrName+", descriptor "+desc+": "+e;
				trace.err(err);
				e.printStackTrace();
				throw new RuntimeException(err, e);
			}
		}
	}

	public void getAttributes(Client client, Element elt) throws Exception {
		for (AttributeDescriptor desc : attrDescs.values()) {
			String attrVal = elt.getAttributeValue(desc.name);
			if (attrVal == null)
				continue;
			desc.setMethod.invoke(client, attrVal);
		}
	}
	
	/**
	 * Create an XML {@link Document} from a string, to extract the {@link Element}s, etc.
	 * @param s string to parse
	 * @return document holding the XML structure
	 * @throws JDOMException on parse error,
	 * @throws IOException shouldn't happen with {@link StringReader}
	 */
    public static Document parse(String s) throws JDOMException, IOException {
    	Document doc;
        SAXBuilder builder = new SAXBuilder();	
		Reader rdr = new StringReader(s);
		doc = builder.build(rdr);
		rdr.close();
    	return doc;
    }
}
