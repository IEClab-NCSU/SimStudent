/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.hcii.runcc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import fri.patterns.interpreter.parsergenerator.builder.SerializedParser;

/**
 * A subclass that saves the parser to a memory buffer instead of a file.
 */
public class MemorySerializedParser extends SerializedParser {

	/** The named byte buffers to store parsers: key=identifier, value=buffer. */
	private static HashMap<String, byte[]> parserStore = new HashMap<String, byte[]>(); 

	/**
	 * Equivalent to {@link SerializedParser#SerializedParser()}
	 */
	public MemorySerializedParser() {
		super();
	}

	/**
	 * Called by {@link SerializedParser#get(Class, Object, String)} to retrieve a saved
	 * parser from a named buffer in {@link #parserStore}. 
	 * @param bufferName identifies the byte buffer in {@link #parserStore}.  
	 * @return object read from buffer; null if buffer not found
	 * @see fri.patterns.interpreter.parsergenerator.builder.SerializedObject#read(java.lang.String)
	 */
	protected Object read(String bufferName) {
		ObjectInputStream oin = null;
		try	{
			byte[] buffer = parserStore.get(bufferName);
			if (buffer == null)
				throw new IOException("read MemorySerializedParser: buffer \""+bufferName+"\" not found");
			System.err.println("deserializing from buffer "+bufferName);
			ByteArrayInputStream in = new ByteArrayInputStream(buffer);
			oin = new ObjectInputStream(in);
			return oin.readObject();
		} catch (Exception e)	{
			System.err.println();	// tolerate non-existing object
			return null;
		} finally	{
			try	{	oin.close();	}	catch (Exception e)	{}
		}
	}

	/**
	 * Called by {@link SerializedParser#get(Class, Object, String)} to serialize and save
	 * a newly-generated parser to a named buffer in {@link #parserStore}.
	 * @param bufferName identifies the byte buffer in {@link #parserStore}  
	 * @param o
	 * @return true on success
	 * @see fri.patterns.interpreter.parsergenerator.builder.SerializedObject#write(java.lang.String, java.lang.Object)
	 */
	protected boolean write(String bufferName, Object o) {
		ObjectOutputStream oout = null;
		try	{
			parserStore.remove(bufferName);      // clear any existing buffer by this name
			System.err.println("serializing to buffer "+bufferName);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			oout = new ObjectOutputStream(out);
			oout.writeObject(o);
			byte[] buffer = out.toByteArray();
			if (buffer == null || buffer.length < 1)
				throw new IOException("write MemorySerializedParser: buffer \""+bufferName+"\" empty or null");
			parserStore.put(bufferName, buffer);
			return true;
		} catch (IOException e)	{
			e.printStackTrace();
			return false;
		} finally	{
			try	{	oout.flush(); oout.close();	}	catch (Exception e)	{}
		}
	}

}
