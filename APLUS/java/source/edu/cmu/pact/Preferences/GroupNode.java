/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.Preferences;

import java.util.Iterator;
import java.util.LinkedList;

// import javax.swing.JPanel;  omit for Android, other non-Swing environments

import edu.cmu.pact.Preferences.PreferencesModel.Node;

/**
 * Group of preferences or categories of preferences. Application
 * object for non-leaf nodes in the tree. Associates a name with a List.
 */
class GroupNode extends LinkedList implements PreferencesModel.Node {

	/** Name for this list. */
	private final String name;
	private final String description;

	/** EditorPanel for this list, if any. Cast as {@link javax.swing.JPanel} in use. */
	private Object editorsPanel = null;

	/** Constructor calls superclass no-argument constructor, sets name, description. */
	GroupNode(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	/**
	 * For interface Node.
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * For interface Node.
	 *
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Tell whether this is a lowest-level tree node.
	 *
	 * @return true if first child is a {@link PreferencesModel.Preference}
	 */
	boolean hasPreferenceChildren() {
		return (size() > 0 &&
				get(0) instanceof PreferencesModel.Preference);
	}

	/**
	 * Return the name of this node. This must return the value to
	 * be displayed in the tree.
	 *
	 * @return name field
	 */
	public String toString() {
		return name;
	}

	/**
	 * Pretty-print the subtree at this node.
	 *
	 * @return tree listing with indent 2
	 */
	public String prettyPrint() {
		StringBuffer result = new StringBuffer();
		prettyPrint(result, 0);
		return result.toString();
	}

	/**
	 * Pretty-print the subtree at this node.  Recursive part of
	 * prettyPrint().
	 *
	 * @param  result StringBuffer to append to
	 * @param  indent level
	 * @return tree listing with indent 2 in StringBuffer
	 */
	private void prettyPrint(StringBuffer result, int indent) {
		
		for (int i = 0; i < indent; ++i)
			result.append("  ");
		result.append("Category: ");
		result.append(name);
		result.append('\n');
		for (Iterator it = iterator(); it.hasNext(); ) {
			Object child = it.next();
			if (child instanceof GroupNode)
				((GroupNode) child).prettyPrint(result, indent+1);
			else {
				for (int j = 0; j < indent+1; ++j)
					result.append("  ");
				result.append("Preference: ");
				result.append(((PreferencesModel.Preference) child).getName());
				result.append('\n');
			}
		}
	}

	/**
	 * If not null, result should be an instance of {@link javax.swing.JPanel}.
	 * @return the {@link #editorsPanel}
	 */
	Object getEditorsPanel() {
		return editorsPanel;
	}

	/**
	 * Argument should be an instance of {@link javax.swing.JPanel}.
	 * @param editorsPanel new value for {@link #editorsPanel}
	 */
	void setEditorsPanel(Object editorsPanel) {
		this.editorsPanel = editorsPanel;
	}
}
