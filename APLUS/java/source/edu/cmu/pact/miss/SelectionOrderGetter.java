/**
 * 
 */
package edu.cmu.pact.miss;


/**
 * @author mazda
 *
 */
public class SelectionOrderGetter {
    
   
    /**
     * must be overridden
     */
    public String nextSelection(String selection){
        new Exception("you must override SelectionOrderGetter.nextSelection() with your domain-specific implementation.").printStackTrace();
        return null;
    }
    
    public String nextSelection(String selection, String priorSelection)
    {
    	return nextSelection(selection);
    }
    
    public String startSelection()
    {
        new Exception("you must override SelectionOrderGetter.startSelection() with your domain-specific implementation.").printStackTrace();
        return null;
    }
}
