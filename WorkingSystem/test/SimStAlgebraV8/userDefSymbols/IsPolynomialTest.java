/**
 * Created: Dec 21, 2013 12:07:24 PM
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
public class IsPolynomialTest extends IsPolynomial {

	/**
	 * Test method for {@link SimStAlgebraV8.userDefSymbols.IsPolynomial#IsPolynomial()}.
	 */
	@Test
	public void testIsPolynomial() {
		
		assertNull(isPolynomial("-3x"));
		assertEquals("T", isPolynomial("-3x+2"));
	}
}
