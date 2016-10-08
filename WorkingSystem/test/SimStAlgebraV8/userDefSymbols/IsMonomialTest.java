/**
 * Created: Mar 30, 2015 9:14:44 PM
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
public class IsMonomialTest extends IsMonomial {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		
		assertEquals(TRUE_VALUE, isMonomial("3y"));
		assertEquals(FALSE_VALUE, isMonomial("3y-1"));
	}
}
