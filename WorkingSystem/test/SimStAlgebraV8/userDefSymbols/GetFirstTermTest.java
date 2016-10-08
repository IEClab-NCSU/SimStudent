/**
 * Created: Feb 28, 2015 9:25:51 PM
 * @author mazda
 * 
 */
package SimStAlgebraV8.userDefSymbols;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

/**
 * @author mazda
 *
 */
public class GetFirstTermTest extends GetFirstTerm {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		assertEquals("2x", getFirstTerm("2x-3"));
		
		/*
		ArrayList<String> falseArgV = new ArrayList<String>();
		// FALSE%fp@value
		falseArgV.add(FALSE_VALUE);
		assertFalse(isValidArgument(falseArgV));
		*/
	}

}
