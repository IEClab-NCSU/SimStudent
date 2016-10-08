/**
 * Created: Aug 31, 2014 4:34:24 PM
 * @author mazda
 * 
 */
package SimStAlgebraV8.userDefSymbols;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

/**
 * @author mazda
 *
 */
public class RhsOperatorSearchTest {

	SubtractTerm subtractTerm = new SubtractTerm();
	GetFirstIntegerWithoutSign getFirstIntegerWithoutSign = new GetFirstIntegerWithoutSign();
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {

		// 2x+2, subtract 2
		
		ArrayList<String> argv = new ArrayList<String>();
		argv.add("subtract 2");
		String operand = getFirstIntegerWithoutSign.apply(argv);
		
		argv = new ArrayList<String>();
		argv.add("2x+2");
		argv.add(operand);
		String term = subtractTerm.apply(argv);
		
		assertEquals("2x", term);
	}
}
