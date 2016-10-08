/**
 * Created: Aug 31, 2014 9:49:42 PM
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
public class GetOperandTest extends GetOperand {

	GetOperand test = new GetOperand();
	
	@Test
	public void test() {
		
		assertTrue(argTest("subtract -1"));
		assertEquals("-1", this.test.apply(new ArrayList<String>(Arrays.asList(new String[]{"subtract -1"}))));
		
	}
	
	/**
	 * @param string
	 * @return
	 */
	private boolean argTest(String arg) {
		
		boolean isValidArgument = false;
		
		try {
			isValidArgument = this.test.isValidArgument(new ArrayList<String>(Arrays.asList(new String[]{arg})));
		} catch (JessException e) {
			e.printStackTrace();
		}
		
		return isValidArgument;
	}

}
