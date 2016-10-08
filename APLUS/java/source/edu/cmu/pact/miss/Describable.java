/**
  * Describable (Terms).
  *
  * Used to describe a definition of a feature predicate
  * Can be used for further elaborating dialogs, with some background knowledge
  * from SimStudent and/or Meta-tutor.
  * 
  * Used for interactive learning dialogs
  * Initial commit: 
  * @author Huan Truong <a href="mailto:huant@andrew.cmu.edu">Huan Truong</a>
*/

package edu.cmu.pact.miss;

import java.io.IOException;
import java.io.Serializable;

public class Describable implements Serializable {

    /**
      * Constructor
      */
    public Describable() {
    
    }

    /**
      * Short-hand function to construct from a feature predicate
      */
    public Describable(String pFeatName, String pFeatDesc) {
	describable = true;
	featureName = pFeatName;
	featureDescriptions = pFeatDesc;
    }

    /**
      * See if the feature is describable.
      * @return Must return true to be considered to be added to the query "dictionary"
      */
    public boolean isDescribable() {
	return describable;
    }

    /** Description of the feature
      * @return The description of a feature e.g. "A fraction is the quotient of two rational numbers."
      */
    public String getDescriptions() {
	return featureDescriptions;
    }

    /** Name of the feature
      * @return The description of a feature e.g. "A fraction is the quotient of two rational numbers."
      */
    public String getFeatureName() {
	return featureName;
    }

    /**
      * ReadObject and WriteObject are here to implement Serializable
      * and make the compiler happy -- we don't really need this
      */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
	// Nothing here
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
	// Nothing here
    }
    
    private boolean describable = false;
    private String featureDescriptions, featureName;
    private static final long serialVersionUID =   -104491792645312644L;
}
