package edu.cmu.old_pact.dataconverter;

public class DataFormattingException extends NoSuchFieldException{
	public DataFormattingException(){ super("DataFormatException");};
	public DataFormattingException(String s) {super(s);};
}