package edu.cmu.old_pact.cmu.spreadsheet;
//a LineEnumeration is just a list of lines in the AltTextField	

import java.util.Enumeration;

final class LineEnumeration implements Enumeration {
	int lineNo;
	AltTextField atf;
	
	public LineEnumeration(AltTextField inAtf) {
		lineNo = 0;
		atf = inAtf;
	}
	
	public boolean hasMoreElements() {
		return (lineNo < atf.nLines());
	}
	
	public Object nextElement () {
		return (new StartEndPair (atf.nthStart(lineNo), atf.nthEnd(lineNo), lineNo++));
	}
}

