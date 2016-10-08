/*
 * Created on Mar 16, 2005
 *
 */
package edu.cmu.old_pact.cmu.uiwidgets;

/**
 * @author kimkc
 *
 */
public interface CreatableObject {
	Object Create(String s);
	Object Create(String s,Object parent);
}
