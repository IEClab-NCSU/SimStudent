/**
 * Created: Dec 21, 2013 6:54:12 AM
 * @author mazda
 * 
 */
package SimStAlgebraV8.userDefSymbols;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import jess.JessException;

import org.junit.Test;

/**
 * @author mazda
 *
 */
public class GetFirstIntegerWithoutSignTest extends GetFirstIntegerWithoutSign {

	
	GetFirstIntegerWithoutSign test = new GetFirstIntegerWithoutSign();
	
	/**
	 * Test method for {@link SimStAlgebraV8.userDefSymbols.GetFirstIntegerWithoutSign#GetFirstIntegerWithoutSign()}.
	 */
	@Test
	public void testGetFirstIntegerWithoutSign() {
		
		assertTrue(argTest("x+1"));
		assertEquals("1", getFirstIntegerWithoutSign("x+1"));
		
		assertTrue(argTest("-3-(2y-(5-4a))-76xyz"));
		assertEquals("3", getFirstIntegerWithoutSign("-3-(2y-(5-4a))-76xyz"));
		
		assertFalse(argTest("subtract 5"));
		
		assertTrue(argTest("x+(-2.4y-(5-4a))-76xyz"));
		assertEquals("2.4", getFirstIntegerWithoutSign("x+(-2.4y-(5-4a))-76xyz"));

		assertTrue(argTest("x+y"));
		assertNull(getFirstIntegerWithoutSign("x+y"));
	}

	/**
	 * @param string
	 * @return
	 */
	private boolean argTest(String arg) {
		
		boolean isValidArgument = false;
		
		try {
			isValidArgument = test.isValidArgument(new ArrayList<String>(Arrays.asList(new String[]{arg})));
		} catch (JessException e) {
			e.printStackTrace();
		}
		
		return isValidArgument; 
	}
	
}
