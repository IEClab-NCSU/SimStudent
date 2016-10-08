package jess;

import java.applet.Applet;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Loads user classes and resources. Jess uses this to manage "import"
 * function calls and to cache loaded class objects.
 *
 * <P>
 * (C) 2005 Sandia National Laboratories<br>
 */

public class ClassSource implements Serializable {
    private transient Object m_appObject;
    private transient ClassLoader m_classLoader;
    private Hashtable m_classImports = new Hashtable();
    private ArrayList m_packageImports = new ArrayList();
    private transient HashMap m_loadedClasses = new HashMap();
    private transient Rete m_engine;

    ClassSource(Object appObject, Rete engine) throws JessException {
        m_appObject = appObject;
        m_engine = engine;
        importPackage("java.lang.");
    }

    /**
     * Returns the applet this Rete is installed in. Returns null if none.
     *
     * @return The applet
     */

    public Applet getApplet() {
        if (m_appObject instanceof Applet)
            return (Applet) m_appObject;
        else
            return null;
    }

    /**
     * Returns the "application object" for this Rete instance
     *
     * @see Rete#Rete
     */

    public Class getAppObjectClass() {
        if (m_appObject != null)
            return m_appObject.getClass();
        else
            return Rete.class;
    }

    /**
     * Associates this Rete with an applet so that, for instance, the
     * (batch) commands will look for scripts using the applet's
     * document base URL.
     *
     * @param applet The applet
     */
    public void setApplet(Applet applet) {
        m_appObject = applet;
    }

    /**
     * Associates this Rete with an object so that, for instance, the
     * (batch) commands will look for scripts using the object's
     * class loader.
     *
     * @param appObject The app object
     */
    public void setAppObject(Object appObject) {
        m_appObject = appObject;
    }

    /**
     * Associates this Rete with a specific class loader; the loader
     * will be used to find batch files and load classes.
     *
     * @param loader The class loader
     */
    public void setClassLoader(ClassLoader loader) {
        m_classLoader = loader;
    }

    /**
     * Loading classes and resources
     */

    private Class classForName(String name) throws ClassNotFoundException {

        Class clazz = (Class) m_loadedClasses.get(name);
        if (clazz != null)
            return clazz;

        ClassLoader appLoader = getAppObjectClass().getClassLoader();
        if (appLoader != null) {
            try {
                clazz = Class.forName(name, true, appLoader);
                m_loadedClasses.put(name, clazz);
                return clazz;
            } catch (ClassNotFoundException silentlyIgnore) {
            }
        }

        if (m_classLoader != null) {
            try {
                clazz = Class.forName(name, true, m_classLoader);
                m_loadedClasses.put(name, clazz);
                return clazz;
            } catch (ClassNotFoundException silentlyIgnore) {
            }
        }


        try {
            ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            if (contextLoader != null) {
                try {
                    clazz = Class.forName(name, true, contextLoader);
                    m_loadedClasses.put(name, clazz);
                    return clazz;
                } catch (ClassNotFoundException silentlyIgnore) {
                }
            }
        } catch (SecurityException silentlyIgnore) {
        }

        clazz = Class.forName(name);
        m_loadedClasses.put(name, clazz);
        return clazz;
    }

    public Class findClass(String className) throws ClassNotFoundException {
        Class clazz = (Class) m_loadedClasses.get(className);
        if (clazz != null)
            return clazz;

        if (className.indexOf(".") == -1) {
            String s = (String) m_classImports.get(className);
            if (s != null)
                className = s;

            else {
                for (Iterator e = m_packageImports.iterator(); e.hasNext();) {
                    s = e.next() + className;
                    try {
                        Class c = classForName(s);
                        m_classImports.put(className, s);
                        return c;
                    } catch (ClassNotFoundException ex) {
                        /* Just try again */
                    }
                }
            }
        }
        return classForName(className);
    }

    public URL getResource(String name) {

        if (m_appObject != null) {
            URL u = m_appObject.getClass().getResource(name);
            if (u != null)
                return u;
        } else if (m_classLoader != null) {
            URL u = m_classLoader.getResource(name);
            if (u != null)
                return u;
        }

        try {
            ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            if (contextLoader != null) {
                URL u = contextLoader.getResource(name);
                if (u != null)
                    return u;
            }
        } catch (SecurityException silentlyIgnore) {
        }

        return Rete.class.getResource(name);
    }

    public void importPackage(String pack) {
        m_packageImports.add(pack);
    }

    public void importClass(String clazz) throws JessException {
        m_classImports.put(clazz.substring(clazz.lastIndexOf(".") + 1,
                clazz.length()),
                clazz);
        try {
            Class aClass = findClass(clazz);
            StaticMemberImporter importer = new StaticMemberImporter(aClass);
            importer.addAllStaticFields(m_engine);
            importer.addAllStaticMethods(m_engine);
        } catch (ClassNotFoundException e) {
            throw new JessException("import", "Class not found", e);
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        m_loadedClasses = new HashMap();
    }

    public void clear() {
        m_packageImports.clear();
        m_classImports.clear();
        m_loadedClasses.clear();
        importPackage("java.lang.");
    }

    public ClassLoader getClassLoader() {
        return m_classLoader;
    }

    public static String classNameOnly(String name) {
        if (name.indexOf('.') > -1)
            name = name.substring(name.lastIndexOf('.') + 1);
        return name;
    }

    void setEngine(Rete engine) {
        m_engine = engine;
    }
}
