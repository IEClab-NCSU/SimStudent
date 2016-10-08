/**
 * Created: Dec 21, 2013 12:02:01 PM
 * @author mazda
 * 
 */
package SimStAlgebraV8.userDefSymbols;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author mazda
 *
 */
public class HasCoefficientTest extends HasCoefficient {

	/**
	 * Test method for {@link SimStAlgebraV8.userDefSymbols.HasCoefficient#HasCoefficient()}.
	 */
	@Test
	public void testHasCoefficient() {
		
		assertEquals(TRUE_VALUE, hasCoefficient("-a"));
		assertEquals(TRUE_VALUE, hasCoefficient("3y"));
		assertEquals(FALSE_VALUE, hasCoefficient("y"));
		assertEquals(FALSE_VALUE, hasCoefficient("10"));

		assertNull(hasCoefficient("-3(x+2)"));
		
		assertNull(hasCoefficient("subtract 5"));
		
	}
}
