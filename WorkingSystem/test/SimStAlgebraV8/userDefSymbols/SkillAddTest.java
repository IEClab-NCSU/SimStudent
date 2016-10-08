/**
 * Created: Feb 28, 2015 9:41:59 PM
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
public class SkillAddTest extends SkillAdd {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		assertTrue(isArithmeticExpression("-x"));
	}

}
