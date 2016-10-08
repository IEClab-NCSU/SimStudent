/**
 * Created: Aug 31, 2014 2:57:55 PM
 * @author mazda
 * 
 */
package SimStAlgebraV8.userDefSymbols;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

/**
 * @author mazda
 *
 */
public class SubtractTermTest extends SubtractTerm {

	SubtractTerm subtractTerm = new SubtractTerm();
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSubtractTerm() {
		assertEquals("-x", subtractTerm(new String[]{"2x", "3x"}));
		assertEquals("2x", subtractTerm(new String[]{"2x+2", "2"}));
		assertEquals("x", subtractTerm(new String[]{"x+5", "5"}));
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	// Helper methods
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	private String subtractTerm(String[] args) {

		String subtractTerm = this.subtractTerm.apply(new ArrayList<String>(Arrays.asList(args)));
		return subtractTerm;
	}
	
	

}
