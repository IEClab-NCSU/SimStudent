/*
 * Created on Apr 22, 2004
 *
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author mpschnei
 * 
 */
public class MatcherFactoryTest extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite(MatcherFactoryTest.class);
        return suite;
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFactory() {
        testFactoryForType (MatcherFactory.EXACT_CLASS, ExactMatcher.class);
        testFactoryForType (MatcherFactory.ANY_CLASS, AnyMatcher.class);
        testFactoryForType (MatcherFactory.REGEX_CLASS, RegexMatcher.class);
        testFactoryForType (MatcherFactory.WILDCARD_CLASS, WildcardMatcher.class);
        testFactoryForType (MatcherFactory.RANGE_CLASS, RangeMatcher.class);
    }

    /**
     * @param classNames
     * @param classType
     */
    private void testFactoryForType(String[] classNames, Class classType) {
        for (int i = 0; i < classNames.length; i++) {
            try {
                Matcher m = MatcherFactory.buildMatcher(classNames[i]);
                assertTrue (m.getClass().getName().equals (classType.getName()));
            } catch (InstantiationException e) {
                assertTrue(false);
            } catch (IllegalAccessException e) {
                assertTrue(false);
            } catch (ClassNotFoundException e) {
                assertTrue(false);
            }
        }
    }
}
