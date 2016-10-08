/**
 * Created: Feb 28, 2015 8:42:12 PM
 * @author mazda
 * 
 */
package SimStAlgebraV8.userDefSymbols;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author mazda
 *
 */
public class GetConstTermTest extends GetConstTerm {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link SimStAlgebraV8.AlgebraV8UserDefJessSymbol#getConstTerm(java.lang.String)}.
	 */
	@Test
	public void testGetConstTerm() {
		assertEquals("-3", getConstTerm("2x-3"));
		assertEquals("-2", getConstTerm("-2+4y"));
		assertEquals("7", getConstTerm("7+4y"));
	}

}
