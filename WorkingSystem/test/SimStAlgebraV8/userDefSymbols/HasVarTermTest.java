/**
 * Created: Dec 21, 2013 11:11:36 AM
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
public class HasVarTermTest extends HasVarTerm {

	/**
	 * Test method for {@link SimStAlgebraV8.userDefSymbols.HasVarTerm#HasVarTerm()}.
	 */
	@Test
	public void testHasVarTerm() {
		
		assertEquals(TRUE_VALUE, hasVarTerm("-3y"));
		assertEquals(TRUE_VALUE, hasVarTerm("-3y+4x"));
		assertEquals(FALSE_VALUE, hasVarTerm("-3"));
	}
}
