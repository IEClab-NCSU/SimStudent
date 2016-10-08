/**
 * Created: Aug 11, 2014 10:00:27 PM
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
public class AddTermTest extends AddTerm {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAddTerm() {
		
		assertEquals("4x", addTerm("2x", "2x"));
		assertEquals("3x", addTerm("2x", "x"));
		assertEquals("2x+3y", addTerm("2x", "3y"));
	}

}
