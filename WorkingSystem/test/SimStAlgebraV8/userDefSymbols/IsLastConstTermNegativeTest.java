/**
 * Created: Dec 21, 2013 11:27:21 AM
 * @author mazda
 * 
 */
package SimStAlgebraV8.userDefSymbols;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

/**
 * @author mazda
 *
 */
public class IsLastConstTermNegativeTest extends IsLastConstTermNegative {

	@Test
	public void testIsLastConstTermNegative() {
		
		IsLastConstTermNegative test = new IsLastConstTermNegative();
		ArrayList<String> argv = new ArrayList<String>(Arrays.asList(new String[]{"3x"}));
		assertNull(test.apply(argv));
		
		assertEquals(TRUE_VALUE, isLastConstTermNegative("-3+2y-5+x"));
		assertEquals(TRUE_VALUE, isLastConstTermNegative("-3-(2y-(5-4a))"));
		assertEquals(FALSE_VALUE, isLastConstTermNegative("43-(2y-(5-4a))"));
		assertEquals(TRUE_VALUE, isLastConstTermNegative("-3-(2y-(5-4a))-76xyz"));
	}
}
