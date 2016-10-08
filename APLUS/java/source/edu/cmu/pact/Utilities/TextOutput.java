/*
 * Created on Apr 19, 2005
 *
 * @author Jonathan Sewall
 */

package edu.cmu.pact.Utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Generalized text output facility for console-like text output.  Instances
 * of this class are meant to enable a process to write text messages (trace
 * output, debug diagnostics, etc.) to any of several actual outputs without
 * the process having to know the details of the i/o.  View code can choose 
 * actual i/o mechanisms to direct console output to, e.g., a
 * {@link javax.swing.JTextArea}, {@link java.io.PrintStream},
 * {@link java.io.Writer}, {@link java.lang.StringBuffer}.
 */

public abstract class TextOutput {

    /**
     * An interface for {@link TextOutput#Event} listeners.
     */
    public static interface OutputEventListener {
    	/**
    	 * To be called when an output event has occurred.
    	 * @param e output event
    	 */
    	public void outputOccurred(OutputEvent e);
    }
    
    /**
     * An event to tell listeners about output from this class.
     */
    public static class OutputEvent extends EventObject {
    	/**
		 * 
		 */
		private static final long serialVersionUID = 3235450617896682604L;
		/** The string written. */
    	private final String output;
    	/**
    	 * Constructor sets all fields.
    	 * @param source
    	 * @param output
    	 */
    	private OutputEvent(TextOutput source, String output) {
    		super(source);
    		this.output = output;
    	}
		/**
		 * @return the {@link #output}
		 */
		public String getOutput() {
			return output;
		}
    }
    
	/** Maximum length for {@link TextOutput.TextDocument} */
	private static int MAXSIZE = 32768;
	
	/** Whether a prompt was the last text written. */
	protected boolean promptShowing = false;

    /** {@link java.io.PrintWriter} for {@link #getWriter()}. */
    private PrintWriter writer;
    
    /** Listeners for output events. */
    private transient Set<OutputEventListener> listeners = new HashSet<OutputEventListener>();
    
    /**
     * Write the given String to the end of the output area. 
     * @param  s String to append
     */
    public TextOutput append(String s) {
    	//trace.out("mg", "TextOutput (append): appending s = " + s);
    	internalAppend(s);
    	fireOutputEvent(s);
    	promptShowing = false;
    	return this;
    }
    
    /**
     * @param listener listener to add to {@link #listeners}
     */
    public synchronized void addOutputEventListener(OutputEventListener listener) {
    	listeners.add(listener);
    }
    
    /**
     * @param listener listener to remove from {@link #listeners}
     */
    public synchronized void removeOutputEventListener(OutputEventListener listener) {
    	listeners.remove(listener);
    }

    /**
     * Notify all {@link #append(String)}#listeners} that a string was output.
     * @param s
     */
    private void fireOutputEvent(String s) {
    	OutputEvent e = new OutputEvent(this, s);
    	for (Iterator<OutputEventListener> it = listeners.iterator(); it.hasNext(); )
    		((OutputEventListener) it.next()).outputOccurred(e);
	}

	/**
     * Write the given String to the end of the output area. 
     * @param  s String to append
     */
    protected abstract TextOutput internalAppend(String s);
    
    /**
     * Write the given String to the end of the output area, 
     * followed by a newline ("\n").
     * @param  s String to append
     */
    public TextOutput println(String s) {
        append(s);
        append("\n");
        return this;
    }
    
    /**
     * Ensure a prompt is showing.  No-op if {@link #promptShowing} is true.
     * Else calls {@link #append(String)} and sets {@link #promptShowing} true.
     * @param s prompt string
     * @return this object
     */
    public TextOutput prompt(String s) {
    	if (promptShowing)
    		return this;
    	//trace.out("mg", "TextOutput (prompt): s = " + s);
    	append(s);
       	promptShowing = true;
    	return this;
    }
    
    /**
     * Clear the output area. For files this is a no-op.
     */
    public void clear() {
    	promptShowing = false;
    }
    
    /**
     * Return a PrintWriter instance that will output to this TextOutput.
     * Useful for exceptions, as with
     * {@link java.lang.Throwable#printStackTrace(java.io.PrintWriter)}.
     * @return {@link #writer}
     */
    public PrintWriter getWriter() {
        if (writer == null)
            writer = createWriter();
        return writer;
    }
    
    /**
     * Create a Writer instance whose i/o maps to that in this TextOutput.
     * @return {@link java.io.Writer} instance
     */
    protected abstract PrintWriter createWriter();
    
    /**
     * Return a NullOutput object.
     * @return object that discards all output given it
     */
    public static final TextOutput getNullOutput() {
        return new NullOutput();
    }

    /**
     * Use a {@link javax.swing.JTextArea} as a {@link TextOutput}. 
     * @param  ta JTextArea to use
     * @return TextOutput whose i/o methods will write to given JTextArea
     */
    

    public static TextOutput getTextOutput(Document doc) {
        return new TextDocument(doc);
    }

    /**
     * Use a {@link StringWriter} as a {@link TextOutput}. 
     * @param  sw StringWriter to use
     * @return TextOutput whose i/o methods will write to sw
     */
    public static TextOutput getTextOutput(StringWriter sw) {
        return new StringOutput(sw);
    }
    
    /**
     * Use a {@link java.io.PrintStream} as a {@link TextOutput}. 
     * @param  ta java.io.PrintStream to use
     * @return TextOutput whose i/o methods will write to given PrintStream
     */
    public static TextOutput getTextOutput(java.io.PrintStream ps) {
        return new PrintStream(ps);
    }
    
    /**
     * Abstract class implementation for {@link java.io.PrintStream}.
     */
    private static class PrintStream extends TextOutput {
        
        /** {@link java.io.PrintStream} delegate for i/o. */
        private java.io.PrintStream delegate;
        
        /**
         * Constructor stores {@link java.io.PrintStream} delegate.
         * @param  ta value for {@link #delegate}
         */
        private PrintStream(java.io.PrintStream ta) {
            delegate = ta;
        }
        
        /**
         * Return a PrintWriter whose i/o maps to that in this TextOutput.
         * @return {@link java.io.PrintWriter} instance from {@link #delegate}
         */
        protected PrintWriter createWriter() {
            return new PrintWriter(delegate);
        }
        
        public Document getDocument() {
        	return null;
        }
        
        /**
         * Implementation of superclass method. 
         * @param  s String to pass to {@link #delegate}'s method
         */
        protected TextOutput internalAppend(String s) {
        	//trace.out("mg", "PrintStream.internalAppend: s = " + s);
            delegate.print(s);
            return this;
        }
    }
    
    /**
     * Abstract class implementation for {@link javax.swing.JTextArea}.
     */
    
    /**
     * Abstract class implementation for {@link javax.swing.text.Document}.
     */
    private static class TextDocument extends TextOutput {
        
        /** {@link javax.swing.text.Document} delegate for i/o. */
        private Document delegate;
        
        /** Result for {@link #createWriter()}. */
        private PrintWriter writer;
        
        /**
         * Constructor stores {@link javax.swing.JTextArea} delegate.
         * @param  doc value for {@link #delegate}
         */
        private TextDocument(javax.swing.text.Document doc) {
            delegate = doc;
            writer = new PrintWriter(new TextDocumentWriter());
        }
        
        /**
         * Implementation of superclass method. Allows length to grow
         * to 2*{@link #MAXSIZE} before deleting MAXSIZE characters from
         * the beginning of the TextArea.
         * @param  s String to pass to {@link #delegate}'s method
         */
        protected TextOutput internalAppend(String s) {
        	//trace.out("mg", "TextOutput (TextDocument.internalAppend): appending s = " + s);
            int len = delegate.getLength() + s.length();
            try {
                // if the output gets too long, clear the console first
            	if(len > MAXSIZE*2) {
            		delegate.remove(0, delegate.getLength());
            	}
            	// in all cases, add the new text to the console
            	delegate.insertString(delegate.getLength(), s, null);
            } catch (BadLocationException e) {
            	e.printStackTrace();
            }
            return this;
        }

        /**
         * Clear the output area. Empties the {@link #delegate}.
         */
        public void clear() {
        	try {
				delegate.remove(0, delegate.getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
        }
        
        public Document getDocument() {
        	return this.delegate;
        }
        
        /**
         * Return a PrintWriter whose i/o maps to that in this TextOutput.
         * @return {@link java.io.PrintWriter} instance from {@link #delegate}
         */
        protected PrintWriter createWriter() {
            return writer;
        }
        
        /**
         * {@link java.io.Writer} subclass to direct output to
         * {@link TextOutput.TextDocument#delegate}
         */
        private class TextDocumentWriter extends Writer {
            /** No-op. */
            public void flush() {}
            /** Currently a no-op. */
            public void close() {}
			/**
			 * Set the text to the empty string.
			 */
            public synchronized void clear() {
                try {
					delegate.remove(0, delegate.getLength());
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            /** Form a String from the given args and
             * {@link TextOutput#append(String)} it */
            public void write(char[] cbuf, int off, int len) {
                String s = new String(cbuf, off, len);
				try {
					TextDocument.this.append(s);
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        }
    }
    
    /**
     * Abstract class implementation for {@link StringBuffer}.
     */
    private static class StringOutput extends TextOutput {
        
        /** Writer delegate for i/o. */
        private StringWriter delegate;
        
        /**
         * Constructor stores {@link StringWriter} delegate.
         * @param  sw value for {@link #delegate}
         */
        private StringOutput(StringWriter sw) {
            delegate = sw;
        }
        
        public Document getDocument() {
        	return null;
        }
        
        /**
         * Implementation of superclass method. Allows length to grow
         * to 2*{@link #MAXSIZE} before deleting MAXSIZE characters from
         * the beginning of the TextArea.
         * @param  s String to pass to {@link #delegate}'s method
         */
        protected TextOutput internalAppend(String s) {
        	//trace.out("mg", "StringOutput.internalAppend: s = " + s);
        	StringBuffer sb = delegate.getBuffer();
            int len = sb.length() + s.length();
            if (len > MAXSIZE*2)
                sb.replace(0, MAXSIZE, "");
            delegate.write(s);
            return this;
        }

        /**
         * Clear the output area. Empties the {@link #delegate}.
         */
        public void clear() {
        	delegate.getBuffer().setLength(0);
        }
        
        /**
         * Return a PrintWriter whose i/o maps to that in this TextOutput.
         * @return {@link java.io.PrintWriter} instance from {@link #delegate}
         */
        protected PrintWriter createWriter() {
            return new PrintWriter(delegate);
        }
    }
    
    /**
     * Abstract class implementation for a null device.
     * This class is simply a data sink: it stores no input and
     * performs no i/o.
     */
    private static class NullOutput extends TextOutput {
        
        /**
         * Default constructor.
         */
        private NullOutput() {}
        
        /**
         * Implementation of superclass method. 
         * @param  s String unused
         */
        protected TextOutput internalAppend(String s) {
        	//trace.out("mg", "NullOutput.internalAppend: s = " + s);
            return this;
        }
        
        public Document getDocument() {
        	return null;
        }
        
        /**
         * Return a PrintWriter that discards all output
         * @return PrintWriter instance based on {@link TextOutput.NullOutput}
         */
        protected PrintWriter createWriter() {
            return new PrintWriter(new NullWriter()) ;
        }
        
        /**
         * {@link java.io.Writer} subclass to discard all output. All methods
         * are no-ops.
         */
        private class NullWriter extends Writer {
            public void flush() {}
            public void close() {}
            public void write(char[] cbuf, int off, int len) {}
        }
    }

	public abstract Document getDocument();
}
