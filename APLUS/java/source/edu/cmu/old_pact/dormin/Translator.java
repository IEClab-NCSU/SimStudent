package edu.cmu.old_pact.dormin;

public class Translator{
/*translator objects translate from one language to another.  The parent class
just returns the original text*/

	public Translator(){};
	public String toDestination(MessageObject inEvent){ return inEvent.toString();}
}