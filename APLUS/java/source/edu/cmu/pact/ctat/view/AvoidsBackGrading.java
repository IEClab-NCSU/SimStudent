/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.ctat.view;

/**
 * Marker interface for components. A component instance should implement this
 * interface if clicking on it or tabbing into it should <i>not</i> cause
 * the component that lost focus to grade. Used by text fields, to determine
 * when to submit characters typed so far as the student's attempt.
 */
public interface AvoidsBackGrading {}
