/**
 * 
 */
package edu.cmu.pact.Utilities;

import javax.swing.JComponent;

import edu.cmu.pact.ctat.MessageObject;

/**
 * Characteristics of a user interface component that need to be saved
 * in and transmitted through messages.
 */
public class ComponentDescription {
	
	/** Name for data in messages. */
	private static final Object COMPONENT_DESCRIPTION = "ComponentDescription";

	/** Label of visible property in vector. */
	private static final char VISIBLE = 'v';

	/** Label of horizontal position property. */
	private static final char X_POSITION = 'x';

	/** Label of vertical position property. */
	private static final char Y_POSITION = 'y';

	/** Delimiter between elements in string representation. */
	private static final String FIELD_DELIMITER = ",";

	/** Delimiter between label and value in string representation. */
	private static final String LABEL_DELIMITER = "=";

	/** Component we're describing. */
	private final JComponent component;

	/**
	 * Constructor.
	 * @param component object we're describing
	 */
	public ComponentDescription(JComponent component) {
		this.component = component;
	}

	/**
	 * Store the properties of interest in the vectors of a CommMessage.
	 * @param names list of property names
	 * @param values list of property values
	 */
	/*
	public void serializeGraphicalProperties(Vector names, Vector values) {
		names.add(COMPONENT_DESCRIPTION);
		values.add(toString());
	}
*/
	public void serializeGraphicalProperties(MessageObject messageObject) {
				messageObject.setProperty((String) COMPONENT_DESCRIPTION, (Object) toString());
			}
	/**
	 * Create a string containing values of the properties of interest.
	 * @return String
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(VISIBLE).append(LABEL_DELIMITER).append(component.isVisible());
		sb.append(FIELD_DELIMITER);
		sb.append(X_POSITION).append(LABEL_DELIMITER).append(component.getX());
		sb.append(FIELD_DELIMITER);
		sb.append(Y_POSITION).append(LABEL_DELIMITER).append(component.getY());
		return sb.toString();
	}

	/**
	 * Parse a string created by {@link #toString()} and set the properties
	 * described there.
	 * @param string created by {@link #toString()}
	 */
	private void execute(String s) {
		String[] fields = s.split(FIELD_DELIMITER);
		for (int i = 0; i < fields.length; ++i) {
			if (fields[i].length() < 1+LABEL_DELIMITER.length()+1)
				continue;
			char label = fields[i].charAt(0);
			String value = fields[i].substring(1+LABEL_DELIMITER.length());
			
			switch (label) {
			case VISIBLE:
				component.setVisible(Boolean.valueOf(value).booleanValue());
				break;
				
			}
		}
	}

	/**
	 * Find a serialized instance of this object in property vectors of a
	 * CommMessage and, if found, execute it.
	 * @param names list of property names
	 * @param values list of property values
	 */
/*	public void executeGraphicalProperties(Vector propertyNames, Vector propertyValues) {
		int i = 0;
		while (i < propertyNames.size() && !COMPONENT_DESCRIPTION.equals(propertyNames.get(i)))
			++i;
		if (i >= propertyValues.size())
			return;
		execute((String) propertyValues.get(i));
	}
	*/
	
	public void executeGraphicalProperties(MessageObject messageobject) {
		Object propertyValues = messageobject.getProperty((String) COMPONENT_DESCRIPTION);
		if (propertyValues != null) execute((String) propertyValues);
	}
}
