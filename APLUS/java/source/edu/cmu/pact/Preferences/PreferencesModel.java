/*
 * $Id: PreferencesModel.java 21465 2014-10-27 19:16:58Z sdemi $
 */
package edu.cmu.pact.Preferences;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.cmu.pact.Utilities.EmptyIterator;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;

/**
 * Maintains an arbitrary collection of preference values in
 * memory and on disk.
 */
public class PreferencesModel {

	//////////////////////////////////////////////////////////////////////
	//
	// Inner classes
	//
	//////////////////////////////////////////////////////////////////////

	/**
	 * Stores a single preference.
	 */
	class Preference implements PreferencesModel.Node {

		/** Key for lookup. */
		private String name;

		/** Class of value field. */
		private Class cls;

		/** Parameter value; null if undefined. */
		private Object value;

		/**
		 * Default scope for this Preference item. This is the broadest
		 * (maximum) scope at which the item can be set from the application.
		 */
		private int defaultScope = MIN_SCOPE-1;

		/** Text for editor widget. */
		private String editorLabel = null;

		/** Description for editor panel. */
		private String description = null;

		/**
		 * Constructor sets name, class, value.
		 *
		 * @param name key
		 * @param cls class
		 * @param value initial value; if null, not distuinguished from
		 *           "undefined"
		 */
		private Preference( String name, Class cls, Object value )
		{
			this.name = name;
			this.cls = cls;
			this.value = value;
		}

		/**
		 * Convenience constructor for boolean values.
		 *
		 * @param name key
		 * @param value boolean value
		 */
		private Preference(String name, boolean value)
		{
			this(name, Boolean.class, new Boolean(value));
		}

		/**
		 * Convenience constructor for integer values.
		 *
		 * @param name key
		 * @param value integer value
		 */
		private Preference(String name, int value)
		{
			this(name, Integer.class, new Integer(value));
		}

		/**
		 * Convenience constructor for enum values.
		 *
		 * @param name key
		 * @param value enum value
		 */
		public Preference(String name, Enum value) {
			this(name, value.getClass(), value);
		}

		/**
		 * Format as string <i>name</i>, "<i>value.toString()</i>".
		 *
		 * @return String in above format
		 */
		public String toString() {
			return (name + ", \"" + value.toString() + "\"");
		}

		/**
		 * For interface {@link PreferencesModel.Node}.
		 *
		 * @return name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Return the class.
		 *
		 * @return {@link PreferencesModel.Preference#cls}
		 */
		public Class getCls() {
			return cls;
		}

		/**
		 * Return the value.
		 *
		 * @return {@link PreferencesModel.Preference#value}
		 */
		public Object getValue() {
			return value;
		}

		/**
		 * Return the editorLabel. Defined only at installation scope.
		 *
		 * @return editorLabel; returns name if not defined
		 */
		public String getEditorLabel() {
			Preference p = getPreference(INSTALLATION_SCOPE, name);
			if (p == null || p.editorLabel == null)
				return name;
			return p.editorLabel;
		}

		/**
		 * Return the description. Defined only at installation scope.
		 *
		 * @return description; empty string if not defined
		 */
		public String getDescription() {
			Preference p = getPreference(INSTALLATION_SCOPE, name);
			if (p == null || p.description == null)
				return "";
			return p.description;
		}

		/**
		 * Return the default scope for this preference.
		 * Defined only at installation scope.
		 *
		 * @return defaultScope; {@link PreferencesModel#DEFAULT_SCOPE} if
		 *             not defined
		 */
		public int getDefaultScope() {
			Preference p = getPreference(INSTALLATION_SCOPE, name);
			if (p == null || p.defaultScope < MIN_SCOPE)
				return DEFAULT_SCOPE;
			return p.defaultScope;
		}
	}

	//////////////////////////////////////////////////////////////////////
	//
	// Fields
	//
	//////////////////////////////////////////////////////////////////////

	/**
	 * An application node in the tree. Could be tree node or leaf node.
	 */
	interface Node {
		public String getName();
	}

	/**
	 * Label for installation scope. See also {@link #MAX_SCOPE}.
	 * Value must remain consistent with position in
	 * {@link #scopeNameToNumberTable}.
	 */
	public static final int INSTALLATION_SCOPE = 3;

	/**
	 * Label for interface scope.
	 * Value must remain consistent with position in
	 * {@link #scopeNameToNumberTable}.
	 */
	public static final int INTERFACE_SCOPE = 2;

	/**
	 * Label for problem scope.
	 * Value must remain consistent with position in
	 * {@link #scopeNameToNumberTable}.
	 */
	public static final int PROBLEM_SCOPE = 1;

	/**
	 * Label for author scope. See also {@link #MIN_SCOPE}.
	 * Value must remain consistent with position in
	 * {@link #scopeNameToNumberTable}.
	 */
	public static final int AUTHOR_SCOPE = 0;

	/**
	 * Table for {@link #scopeNameToNumber(String)} lookup.
	 */
	public static final String[] scopeNameToNumberTable = {
		"AUTHOR", "PROBLEM", "INTERFACE", "INSTALLATION"
	};

	/**
	 * Label set to match the broadest scope defined.
	 */
	private static final int MAX_SCOPE = INSTALLATION_SCOPE;

	/**
	 * Label set to match the narrowest scope defined.
	 */
	private static final int MIN_SCOPE = AUTHOR_SCOPE;

	/**
	 * Default scope when none defined.
	 */
	private static final int DEFAULT_SCOPE = AUTHOR_SCOPE;

	/**
	 * Top-level element name for a set of Preference entries in XML.
	 */
	public static final String TOP_ELEMENT_NAME = "preferences";

	/**
	 * Element name for a category in XML.
	 */
	public static final String CAT_ELEMENT_NAME = "category";

	/**
	 * Element name for a single Preference in XML.
	 */
	public static final String PREF_ELEMENT_NAME = "preference";

	/** Element name for a Preference.name value in XML. */
	public static final String NAME_NAME = "name";

	/** Element name for a Preference.cls value in XML. */
	public static final String CLS_NAME = "class";

	/** Element name for a Preference.value value in XML. */
	public static final String VALUE_NAME = "value";

	/** Attribute name for a Preference.scope value in XML. */
	public static final String SCOPE_NAME = "scope";

	/** Element name for a Preference.editorLabel value in XML. */
	public static final String EDITOR_LABEL_NAME = "editor_label";

	/** Element name for a Preference.description value in XML. */
	public static final String DESCRIPTION_NAME = "description";

	/**
	 * Filename for installation scope preferences. Should be stored
	 * along the CLASSPATH for retrieval by Class.getResource().
	 */
	public static final String INSTALLATION_PREF_FILENAME =
		"InstallationPreferences.xml";

	private static final String DEFAULT_CATEGORY = "General";

	private static String instanceSync = "instanceSync";

	/**
	 * Static instance for transitional code: 
	 * preference values.
	 */
	private static volatile PreferencesModel instance = null;

	/**
	 * List of Maps of Preferences at different scopes. Instances at
	 * broader scopes are a source of values for Preferences not set at
	 * narrower scopes. Preference retrieval (see {@link #getValue(String)})
	 * is always attempted first at the narrowest scope available.
	 */
	private List<Map<String, Preference>> tableList = null;

	/**
	 * Root of the display tree for these preferences.
	 */
	private GroupNode displayRoot = null;

	/**
	 * Application listeners for changes to Preference items. This
	 * set of listeners is shared among all instances created with a
	 * common existingListModel (see
	 * {@link #PreferencesModel(int, String, PreferencesModel)
	    PreferencesModel(scope, fileToRead, existingListModel)}.
	 */
	private PropertyChangeSupport listeners = null;

	/**
	 * Default docFile name.
	 * 09/11/2020 - Bug in JavaAPLUS
	 * If the command line argument -ssLoadPrefsFile brPrefsStacy.xml is specified then the log files 
	 * are generated in two folders.
	 * On Launch of the application the docFile is set to brPrefs.xml and LogService.log and simstudent.log files
	 * are generated in mentioned Disk Logging Directory property. Thereafter the command line arguments are read and the docFile is set to brPrefsStacy.xml
	 * The other student-specific files are generated in the specified Disk Logging Directory property.
	 * 
	 */
	private final String PREFERENCES_FILE = "brPrefs.xml";

	/**
	 * File for {@link #loadFromDisk} or {@link #saveToDisk()}.
	 */
	private String docFile = PREFERENCES_FILE;

    /** If true, writes to disk are blocked. See {@link #saveToDisk(int, String)}. */
	private boolean preventSaves = false;

	/** The {@link Node#getName()} of the last category shown, to restore that page when resumed. */
	private String latestCategory = DEFAULT_CATEGORY;
	
    public void setPreferenceFile(String filename)
    {
    	docFile = filename;
    }

	/**
	 * @param preventSaves new value for {@link #preventSaves}
	 */
	public void setPreventSaves(boolean preventSaves) {
		if(trace.getDebugCode("applet"))
			trace.printStack("PreferencesModel.setPreventSaves("+preventSaves+") old value "+this.preventSaves);
		this.preventSaves = preventSaves;
	}

	/**
	 * Package-private creates model with given scope.
	 *
	 * @param  scope value for {@link #scope}; see e.g. {@link #AUTHOR_SCOPE}
	 */
	PreferencesModel(int scope)
	{
		this(scope, "");
	}

	/**
	 * Package-private constructor reads file at startup. Sets scope to
	 * {@link #DEFAULT_SCOPE}.
	 *
	 * @param  fileToRead passed to {@link #loadFromDisk(String)}
	 */
	PreferencesModel(String fileToRead) {
		this(DEFAULT_SCOPE, fileToRead);
	}

	/**
	 * Package-private constructor sets scope, reads file at startup.
	 *
	 * @param  scope value for {@link #scope}; see e.g. {@link #AUTHOR_SCOPE}
	 * @param  fileToRead passed to {@link #loadFromDisk(String)}
	 */
	PreferencesModel(int scope, String fileToRead) {
		init(scope);
		loadFromDisk(scope, fileToRead);
		setPreventSaves(Utils.isRuntime());
	}

	/**
	 * Package-private constructor sets scope, reads URL at startup.
	 *
	 * @param  scope value for {@link #scope}; see e.g. {@link #AUTHOR_SCOPE}
	 * @param  urlToRead passed to {@link #loadFromURL(URL)}
	 */
	PreferencesModel(int scope, URL urlToRead) {
		init(scope);
		loadFromURL(scope, urlToRead);
		setPreventSaves(Utils.isRuntime());		
	}

	/**
	 * Default constructor. Creates empty model with scope
	 * {@link #DEFAULT_SCOPE}.
	 */
	public PreferencesModel()
	{
		this(DEFAULT_SCOPE, ""); 
		createMaxScopeInstance();
		setPreventSaves(Utils.isRuntime());
	} 

	/**
	 * Common constructor actions routine.
	 *
	 * @param  scope value for {@link #scope}; see e.g. {@link #AUTHOR_SCOPE}
	 */
	private void init(int scope) {

		if (scope < MIN_SCOPE)
			throw new IllegalArgumentException("given scope " + scope +
											   " less than min " + MIN_SCOPE);
		else if (scope > MAX_SCOPE)
			throw new IllegalArgumentException("given scope " + scope +
											   " exceeds max " + MAX_SCOPE);
		if (tableList == null) {
			tableList = new ArrayList<Map<String, Preference>>(MAX_SCOPE+1);
			for (int i = 0; i <= MAX_SCOPE; ++i)
				tableList.add(null);
		}
		if (listeners == null)
			listeners = new PropertyChangeSupport(this);

		/*
		 * Use LinkedHashMap to preserve insertion order to enable testing.
		 * Each value is a Preference instance; key is Preference.name.
		 */
		tableList.set(scope, new LinkedHashMap<String, Preference>());
	}

	//////////////////////////////////////////////////////////////////////
	//
	// Methods
	//
	//////////////////////////////////////////////////////////////////////


	/**
	 * Create the MAX_SCOPE instance of the model.
	 */
	private void createMaxScopeInstance() {
		if (trace.getDebugCode("pr"))
			trace.out("pr", "createMaxScopeInstance tableList.size() " +
					tableList.size() + ", tableList.get(MAX) " +
					(tableList.size() <= MAX_SCOPE ? "" :
						tableList.get(MAX_SCOPE)));
		if (tableList.size() > MAX_SCOPE && tableList.get(MAX_SCOPE) != null)
			return;

		URL url = Utils.getFileInSamePackage(this, INSTALLATION_PREF_FILENAME);
		if (trace.getDebugCode("pr"))
			trace.out("pr", "createMaxScopeInstance: url=" + url);
		init(MAX_SCOPE);
		int nPrefs = loadFromURL(MAX_SCOPE, url);
		if (trace.getDebugCode("pr"))
			trace.out("pr", "createMaxScopeInstance: nPrefs "+nPrefs+
					"; EnableLogService "+getBooleanValue("EnableLogService"));
	}

	/**
	 * Populate a Preferences set for the given scope from {@link #docFile}.
	 * Clears any existing Preferences before loading.
	 *
	 * @return count of Preferences loaded; if 0, model is empty
     */
	public int loadFromDisk()
	{
		return loadFromDisk(DEFAULT_SCOPE, null);
	}

	/**
	 * Populate a Preferences set for the given scope from a disk file. 
	 * Clears any existing Preferences before loading.
	 * Logs diagnostic if file can't be read, but throws no exception.
	 *
	 * @param  scope scope of this Preferences set, one of
	 *             {@link #AUTHOR_SCOPE}, {@link #PROBLEM_SCOPE}, etc.
	 * @param  fileToRead if not null or empty, name of file to read;
	 *             else reads existing {@link #docFile};
	 *             saves value in {@link #docFile}
	 * @return count of Preferences loaded; if 0, model is empty
     */
	public int loadFromDisk(int scope, String fileToRead)
	{
		if (fileToRead != null && fileToRead.length() > 0)
			docFile = fileToRead;

		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			doc = builder.build(docFile);
		} catch (Exception e) {
		    if (trace.getDebugCode("pr")) trace.out("pr", "error reading file " + docFile + ": " + e );
			return 0;                          // table left empty if bad file
		}

		return loadFromElement(scope, doc.getRootElement());
	}

	/**
	 * Populate a Preferences set for the given scope from a URL.
	 * Clears any existing Preferences before loading.
	 * Logs diagnostic if file can't be read, but throws no exception.
	 *
	 * @param  scope scope of this Preferences set, one of
	 *             {@link #AUTHOR_SCOPE}, {@link #PROBLEM_SCOPE}, etc.
	 * @param  url if not null or empty, name of file to read;
	 *             else reads existing {@link #docFile};
	 *             saves value in {@link #docFile}
	 * @return count of Preferences loaded; if 0, model is empty
     */
	public int loadFromURL(int scope, URL url)
	{
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;

		if (url == null)
			return -1;
		try {
			doc = builder.build(url);
		} catch (Exception e) {
		    e.printStackTrace();
			trace.err( "error reading URL " + url + ": " + e );
			return 0;                          // table left empty if bad file
		}

		return loadFromElement(scope, doc.getRootElement());
	}

	/**
	 * Populate a Preferences set for the given scope from an XML element.
	 * Clears any existing Preferences before loading.
	 *
	 * @param  scope scope of this Preferences set, one of
	 *             {@link #AUTHOR_SCOPE}, {@link #PROBLEM_SCOPE}, etc.
	 * @param  topElt top-level element
	 * @return count of Preferences loaded; if 0, model is empty
     */
	public int loadFromElement(int scope, Element topElt)
	{
		if (scope == MAX_SCOPE) {        // build display tree at topmost scope
			if (trace.getDebugCode("pr")) trace.out("pr", "creating displayRoot");
			displayRoot = new GroupNode("Category", null);
		}
		int result = loadFromElement(scope, topElt, displayRoot);
		return result;
	}

	/**
	 * Populate a Preferences set for the given scope from an XML element.
	 * Builds a tree of elements by category.
	 * Clears any existing Preferences before loading.
	 *
	 * @param  scope scope of this Preferences set, one of
	 *             {@link #AUTHOR_SCOPE}, {@link #PROBLEM_SCOPE}, etc.
	 * @param  parentElt top-level DOM Element
	 * @param  parentNode node in tree corresponding to parentElt
	 * @return count of Preferences loaded; if 0, model is empty
     */
	private int loadFromElement(int scope, Element parentElt,
								GroupNode parentNode)
	{
		List elts = parentElt.getChildren();
		int count = 0;

		if (trace.getDebugCode("pr")) trace.out("pr", "parentElt name " + parentElt.getName());
		for (Iterator it = elts.iterator(); it.hasNext(); ) {
			Element elt = (Element) it.next();
			if (trace.getDebugCode("pr")) trace.out("pr", "elt name " + elt.getName());

			/*
			 * If this isn't a PREF_ELEMENT_NAME element, then consider it
			 * an intermediate node in the tree: add it to its parent and
			 * load from it.
			 */
			if (!PREF_ELEMENT_NAME.equals(elt.getName())) {
				String nodeId = elt.getAttributeValue("name");
				String descId = elt.getAttributeValue("description");
				GroupNode node = new GroupNode(nodeId, descId);
				if (parentNode != null)
					parentNode.add(node);
				count += loadFromElement(scope, elt, node);
				continue;
			}
			Element prefElt = elt;
			String name = prefElt.getChild(NAME_NAME).getTextTrim();
			String clsName = prefElt.getChild(CLS_NAME).getTextTrim();
			String valStr = prefElt.getChild(VALUE_NAME).getTextTrim();
			String scopeStr = prefElt.getAttributeValue(SCOPE_NAME);
			Preference pref = null;
			int defaultScope = scopeNameToNumber(scopeStr);
			if (defaultScope < MIN_SCOPE) {
				System.err.println("PreferencesModel: bad scope attribute " +
								   scopeStr + " in file " + docFile);
			}

			Class cls = null;			
			try {
				cls = Class.forName(clsName);
			} catch (ClassNotFoundException cnfe) {
				try {
					cls = getClass().getClassLoader().loadClass(clsName);
				} catch (ClassNotFoundException cnfe2) {
					System.err.println("PreferencesModel: unknown class " +
							           clsName + " in file " + docFile);
					continue;
				}
			}

			try {
				if (cls.isEnum()) {
					Method fromStringMethod = cls.getMethod("fromString", String.class);
					pref = new Preference(name, (Enum) fromStringMethod.invoke(null, valStr));
				} else if ((Integer.class.getName()).equals(clsName))
					pref = new Preference(name, Integer.valueOf(valStr).intValue());
				else if ((Boolean.class.getName()).equals(clsName))
					pref = new Preference(name, Boolean.valueOf(valStr).booleanValue());
//				else if ((FileBrowser.class.getName()).equals (clsName)) 
//					pref = new Preference(name, Boolean.valueOf(valStr).booleanValue());
				else	
					pref = new Preference(name, cls, valStr);
			} catch (Exception e) { 
				System.err.println("PreferencesModel: error parsing value \""+valStr+"\", class " +
				           clsName + " in file " + docFile + ":\n "+ e +
				           (e.getCause() == null ? "" : ";\n cause: " + e.getCause()));
				continue;
			}

			Element extraInfo = prefElt.getChild(EDITOR_LABEL_NAME);
			if (extraInfo != null)
				pref.editorLabel = extraInfo.getTextTrim();
			extraInfo = prefElt.getChild(DESCRIPTION_NAME);
			if (extraInfo != null)
				pref.description = extraInfo.getTextTrim();
			if (defaultScope >= MIN_SCOPE)
				pref.defaultScope = defaultScope;

			Map<String, Preference> table = tableList.get(scope);
			if (table == null) {
				init(scope);           // create a table at this scope if none
				table = tableList.get(scope);
			}
			if (null == table.put(pref.name, pref))
				count++;                         // count only if value is new
			if (trace.getDebugCode("pr")) trace.out("pr", "adding " + pref.name + " to " +
					(parentNode == null ? "null" : parentNode.getName()));
			if (parentNode != null)
				parentNode.add(pref);
		}
		return count;
	}

	/**
	 * Convert a symbolic scope name to its integer value.
	 *
	 * @param  scopeName a scope name, corresponding to one of the
	 *             xxx_SCOPE constants {@link #AUTHOR_SCOPE}, etc.
	 * @result the integer constant for this scope name; DEFAULT_SCOPE if
	 *             scopeName is null or blank; error value MIN_SCOPE-1 if
	 *             scopeName is not a legal scope name
	 */
	public static int scopeNameToNumber(String scopeName) {
		if (scopeName == null)
			return DEFAULT_SCOPE;
		String n = scopeName.trim();
		if (n.length() < 1)
			return DEFAULT_SCOPE;
		n = n.toUpperCase();
		for (int i = MIN_SCOPE; i < scopeNameToNumberTable.length; ++i) {
			if (n.startsWith(scopeNameToNumberTable[i]))
				return i;
		}
		return MIN_SCOPE-1;
	}

	/**
	 * Write the {@link #DEFAULT_SCOPE} Preferences set to file
	 * {@link #docFile} in XML format.
	 *
	 * @return count of Preferences loaded; if 0, model is empty
     */
	public int saveToDisk()
	{
		return saveToDisk(DEFAULT_SCOPE, null);
	}

	/**
	 * Write the {@link #DEFAULT_SCOPE} Preferences set to the given file
	 * in XML format.
	 *
	 * @param  fileToWrite name of save file
	 * @return count of Preferences loaded; if 0, model is empty
     */
	public int saveToDisk(String fileToWrite)
	{
		return saveToDisk(DEFAULT_SCOPE, fileToWrite);
	}

	/**
	 * Write the Preferences set for the given scope to the given file
	 * in XML format.
	 * Logs diagnostic if file can't be written, but throws no exception.
	 *
	 * @param  scope scope of this Preferences set, one of
	 *             {@link #AUTHOR_SCOPE}, {@link #PROBLEM_SCOPE}, etc.
	 * @param  fileToWrite if not null or empty, name of file to write;
	 *             else writes existing {@link #docFile};
	 *             saves value in {@link #docFile}
	 * @return count of Preferences written; if 0, model is empty
     */
	public int saveToDisk(int scope, String fileToWrite)
	{
		if (preventSaves)
			return 0;
		// *******begin new code************************

		try {
            BasicService bs =
                (BasicService)ServiceManager.lookup("javax.jnlp.BasicService"); 
            if (bs != null)
                return 0;
        } catch (Exception e) {} // ignore exception: means jnlp not in use 
        // *******end new code***********************
        
		if (fileToWrite != null && fileToWrite.length() > 0)
			docFile = fileToWrite;

		Element root = new Element(TOP_ELEMENT_NAME);

		int count = saveToElement(scope, root);
		PrintStream outStr = null;

		try {
			Document doc = new Document(root);
			XMLOutputter outp = new XMLOutputter(Format.getPrettyFormat());
			FileOutputStream fileOutStr = new FileOutputStream(docFile);
			outStr = new PrintStream(fileOutStr);
			outp.output(doc, outStr);
		} catch (FileNotFoundException e) {
		    if (e.getMessage().indexOf("Access is denied") >= 0) {
		        trace.err ("Save to disk(): " + e.getMessage());
                return 0;
            }
            e.printStackTrace();
        } catch (Exception e) {
			System.err.println("error writing file " + docFile + ": " + e);
			e.printStackTrace();
			count = 0;                   // ?? don't really know count in file
		} finally {
			if (outStr != null)
				outStr.close();
			outStr = null;
		}
		return count;
	}

	/**
	 * Write the Preferences set for the given scope to a single XML element.
	 *
	 * @param  scope scope of this Preferences set, one of
	 *             {@link #AUTHOR_SCOPE}, {@link #PROBLEM_SCOPE}, etc.
	 * @param  topElt top-level element to write to
	 * @return count of Preferences written; if 0, element is empty
     */
	public int saveToElement(int scope, Element topElt)
	{
		int count = 0;

		for (Iterator it = iterator(scope); it.hasNext(); count++ ) {
			Preference pref = (Preference) it.next();
			Element prefElt = new Element(PREF_ELEMENT_NAME);

			Element nameElt = new Element(NAME_NAME);
			nameElt.setText(pref.name);
			prefElt.addContent(nameElt);

			Element clsElt = new Element(CLS_NAME);
			clsElt.setText(pref.cls.getName());
			prefElt.addContent(clsElt);

			Element valueElt = new Element(VALUE_NAME);
			try {
				valueElt.setText(pref.value.toString());
			} catch (NullPointerException npe) {
				System.err.println("null pointer on pref " + pref.name +
								   ", value " + pref.value);
				npe.printStackTrace();
			}
			prefElt.addContent(valueElt);

			topElt.addContent(prefElt);
		}
		return count;
	}

	/**
	 * Return an iterator over the set of values. Iteration order matches
	 * file input order.
	 *
	 * @param  scope scope of this Preferences set, one of
	 *             {@link #AUTHOR_SCOPE}, {@link #PROBLEM_SCOPE}, etc.
	 * @return Iterator whose next() method returns an object of type
	 *             {@link PreferencesModel.Preference}; returns empty
	 *             iterator if scope legal but not defined
	 */
	public Iterator iterator(int scope) {
		if (scope < MIN_SCOPE || MAX_SCOPE < scope)
			throw new IllegalArgumentException("undefined scope " + scope);
		if (tableList.size() <= scope)
			return EmptyIterator.instance();
		Map<String, Preference> table = tableList.get(scope);
		if (table == null)
			return EmptyIterator.instance();
		return table.values().iterator();
	}

	/**
	 * Get a value of arbitrary type. Always returns value from the
	 * narrowest scope in {@link #modelList} whose {@link #table} has
	 * instance of the value defined.
	 *
	 * @param  name key for desired value
	 * @return value from retrieved {@link PreferencesModel.Preference};
	 *             null if not not found
	 */
	public Object getValue(String name) {
		Preference result = getPreference(name);
		if(trace.getDebugCode("pr"))
			trace.outNT("pr", getClass().getSimpleName()+".getValue() result "+result);
		if (result != null)
			return result.value;
		else
			return null;
	}

	/**
	 * Get the current Preference object for the given name.
	 * Always returns the object from the narrowest scope in
	 * {@link #tableList} whose {@link #table} has an
	 * instance of the value defined.
	 *
	 * @param  name key for desired value
	 * @return value of retrieved {@link PreferencesModel.Preference};
	 *             null if not not found
	 */
	Preference getPreference(String name) {
		for (int i = MIN_SCOPE; i < tableList.size(); ++i) {
			Map<String, Preference> table = tableList.get(i);
			if (null == table)
				continue;                      // some scopes may be undefined
			Preference result = table.get(name);
			if (result != null)
				return result;
		}
		return null;
	}

	/**
	 * Return the Preference object for a given key at a particular scope.
	 *
	 * @param  scope entry in {@link #tableList} to search
	 * @param  key Preference name to find
	 * @return Preference at that scope; null if not defined there
	 */
	private Preference getPreference(int scope, String name) {
		if (tableList.size() <= scope)
			return null;
		Map<String, Preference> table = tableList.get(scope);
		if (table == null)
			return null;
		return table.get(name);
	}

	/**
	 * Set the current Preference object for the given name and scope.
	 * If a Preference by this name is defined in a scope narrower than
	 * that given, then revise the Preference in that narrower scope.
	 * Otherwise set the item in the given scope's {@link #table},
	 * creating the item if not defined there; if no model exists at the
	 * given scope, create an empty one and insert this Preference there.
	 * Emits a PropertyChangeEvent with this object as the source.
	 *
	 * @param  scope intended scope to set: see above, for will set a narrower
	 *             scope if this Preference is defined there
	 * @param  name unique identifier of the Preference item
	 * @param  cls Class to record with this item
	 * @param  value new value for this item; if null, removes item from
	 *             narrowest scope where defined
	 * @return value from old Preference item, if any; else null
	 */
	public Object setValue(int scope, String name, Class cls, Object value) {
		return setValue(this, scope, name, cls, value);
	}

	/**
	 * Set the current Preference object for the given name and scope.
	 * If a Preference by this name is defined in a scope narrower than
	 * that given, then revise the Preference in that narrower scope.
	 * Otherwise set the item in the given scope's {@link #table},
	 * creating the item if not defined there; if no model exists at the
	 * given scope, create an empty one and insert this Preference there.
	 * Emits a PropertyChangeEvent with the given object as the source.
	 *
	 * @param  src source object for PropertyChangeEvents
	 * @param  scope intended scope to set: see above, for will set a narrower
	 *             scope if this Preference is defined there
	 * @param  name unique identifier of the Preference item
	 * @param  cls Class to record with this item
	 * @param  value new value for this item; if null, removes item from
	 *             narrowest scope where defined
	 * @return value from old Preference item, if any; else null
	 */
	private Object setValue(Object src, int scope, String name,
							Class cls, Object value) {
		Preference result = null;
		PropertyChangeEvent evt = null;        // event for external listeners

		for (int i = MIN_SCOPE; i <= scope && i < tableList.size(); ++i) {
			Map<String, Preference> table = tableList.get(i);
			if (null == table) {
				if (i < scope)                 // some scopes may be undefined
					continue;
				table = new LinkedHashMap<String, Preference>();   // new empty set at given scope
				tableList.set(scope, table);
			}
			result = (Preference) table.get(name);
			if (result != null && value == null) {  // null value=>remove item
				Object oldValue = getValue(name);
				table.remove(name);
				evt = new PropertyChangeEvent(src, name, oldValue, value);
				if (trace.getDebugCode("pr"))
					trace.out("pr", "1 firing change to " + name + " from " +
							oldValue + " to (null)");
				listeners.firePropertyChange(evt);
				break;
			}
			if ((result != null || i == scope) && value != null) {
				Object oldValue = getValue(name);
				Preference newPref = new Preference(name, cls, value);
				table.put(name, newPref);
				evt = new PropertyChangeEvent(src, name, oldValue, value);
				if (trace.getDebugCode("pr"))
					trace.out("pr", "2 firing change to " + name + " from " +
							oldValue + " to " + value);
				listeners.firePropertyChange(evt);
				break;
			}
		}
		return (result == null ? null : result.value);
	}
	
	/**
	 * Set a boolean value into the table.
	 * Identical to {@link #setValue(int,String,Class,Object)
	   setValue(DEFAULT_SCOPE, valueName, Boolean.class, value)}
	 *
	 * @param  valueName key for this value
	 * @param  value value to store; if null, removes key from table
	 */
	public void setBooleanValue(String valueName, Boolean value) {
		setValue(DEFAULT_SCOPE, valueName, Boolean.class, value);
	}
	
	/**
	 * Get a boolean value using {@link #getValue getValue()}.
	 *
	 * @param  valueName key for this value
	 * @return value from table; null if no value for this key
	 */
	public Boolean getBooleanValue(String valueName) {
		return (Boolean) getValue(valueName);
	}

	public void setEnumValue(String valueName, Enum value) {
		setValue(DEFAULT_SCOPE, valueName, value.getClass(), value);
	}

	/**
	 * Get an enum value using {@link #getValue getValue()}.
	 * @param valueName key for this value
	 * @return value from table; null if no value for this key
	 */
	public Enum getEnumValue(String valueName) {
		return (Enum) getValue(valueName);
	}
	
	/**
	 * Set an integer value into the table.
	 *
	 * @param  valueName key for this value
	 * @param  value value to store; if null, removes key from table
	 */ 
	public void setIntegerValue(String valueName, int value) {
		setIntegerValue (valueName, new Integer (value));
	}

	/**
	 * Set an integer value into the table.
	 * Identical to {@link #setValue(int,String,Class,Object)
	   setValue(DEFAULT_SCOPE, valueName, Integer.class, value)}
	 *
	 * @param  valueName key for this value
	 * @param  value value to store; if null, removes key from table
	 */
	public void setIntegerValue(String valueName, Integer value) {
		setValue(DEFAULT_SCOPE, valueName, Integer.class, value);
	}

	/**
	 * Get an integer value using {@link #getValue getValue()}.
	 *
	 * @param  valueName key for this value
	 * @return value from table; null if no value for this key
	 */
	public Integer getIntegerValue(String valueName) {
		return (Integer) getValue(valueName);
	}

	/**
	 * Set a string value into the table.
	 * Identical to {@link #setValue(int,String,Class,Object)
	   setValue(DEFAULT_SCOPE, valueName, String.class, value)}
	 *
	 * @param  key key for this value
	 * @param  value value to store; if null, removes key from table
	 */
	public void setStringValue(String key, String value) {
		setValue(DEFAULT_SCOPE, key, String.class, value);
	}

	/**
	 * Get a string value using {@link #getValue getValue()}.
	 *
	 * @param  valueName key for this value
	 * @return value from table; null if no value for this key
	 */
	public String getStringValue(String valueName) {
		return (String) getValue(valueName);
	}

	/**
	 * Return root of the display tree. This is effectively defined only
	 * at MAX_SCOPE, where default values for all editable parameters are
	 * defined.
	 *
	 * @return {@link #displayRoot}
	 */
	GroupNode getDisplayRoot() {
		return displayRoot;
	}

	/**
	 * Ensure a MAX_SCOPE instance is present. See {@link PreferencesWindow}.
	 */
	void checkForMaxScopeInstance() {
		if (trace.getDebugCode("pr"))
			trace.out("pr", "checkForMaxScopeInstance() tableList.size() " +
					tableList.size() + ", tableList.get(MAX) " +
					(tableList.size() <= MAX_SCOPE ? "" : tableList.get(MAX_SCOPE)));
		if (tableList.size() <= MAX_SCOPE || tableList.get(MAX_SCOPE) == null)
			createMaxScopeInstance();
	}

	/**
	 * Register a listener for all Preference items.
	 *
	 * @param  listener PropertyChangeListener to be notified of changes
	 *             to any Preference
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	/**
	 * Register a listener for changes to a particular Preference.
	 *
	 * @param  name name of the Preference item of interest
	 * @param  listener PropertyChangeListener to be notified of changes
	 *             to the named Preference
	 */
	public void addPropertyChangeListener(String name,
										  PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(name, listener);
	}

	/**
	 * Unregister a listener for all Preference items.
	 *
	 * @param  listener PropertyChangeListener to be unregistered
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	/**
	 * Unregister a listener for changes to a particular Preference.
	 *
	 * @param  name name of the Preference item of interest
	 * @param  listener PropertyChangeListener to be unregistered
	 */
	public void removePropertyChangeListener(String name,
											 PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(name, listener);
	}

	/**
	 * Broadcast the current set of editor events (Preference changes)
	 * to the listeners.
	 *
	 * @param  src source object for PropertyChangeEvents
	 * @param  changes Collection of PropertyChangeEvents; the newValue
	 *             property of each has the changed value
	 */
	void fireEditorChanges(Object src, Collection<PropertyChangeEvent> changes) {
		for (Iterator<PropertyChangeEvent> it = changes.iterator(); it.hasNext(); ) {
			
			PropertyChangeEvent evt = it.next();
			String name = evt.getPropertyName();
			Object newValue = evt.getNewValue();
			Preference pref = getPreference(name);
			if (pref == null) {
				System.err.println("PreferencesModel: Changed preference " +
								    name + " not found");
				continue;
			}
			if (trace.getDebugCode("pr"))
				trace.out("pr", "preference " + name + " was " + pref.value.toString() +
					  ", is now " + newValue.toString());
			

			int scope = pref.getDefaultScope();
			setValue(src, scope, name, pref.cls, newValue);
		}
	}

	/**
	 * Get the named preference. If undefined, set it to the given default.
	 * @param label preference name
	 * @param defaultValue default value if undefined
	 * @return preference value for label if defined, else given value
	 */
	public Boolean getOrSet(String label, Boolean defaultValue) {
        Boolean currentValue = getBooleanValue(label);
        if (currentValue != null)
        	return currentValue;
        setBooleanValue(label, defaultValue);
		return defaultValue;
	}

	/**
	 * @return {@link #latestCategory}
	 */
	String getLatestCategory() {
		return latestCategory;
	}

	/**
	 * @param nodeName new value for {@link #latestCategory}
	 */
	void setLatestCategory(String nodeName) {
		latestCategory  = nodeName;
	}
}
