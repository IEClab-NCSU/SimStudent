/*
 * $Id: JessParser.java 9609 2008-12-20 18:45:20Z mazda $
 */

package edu.cmu.pact.jess;

import java.io.Reader;

import jess.JessException;
import jess.Value;

/**
 * Objects that can parse Jess code.
 */
public interface JessParser {

	/**
	 * Parse an input file.
	 *
	 * @param  rdr Reader opened on file to parse
	 * @return result of the last parsed entity
	 * @exception JessException
	 */
	public Value parse(Reader rdr) throws JessException;
}
