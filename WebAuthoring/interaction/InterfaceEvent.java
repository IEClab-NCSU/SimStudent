package interaction;

/**
 * Class to represent an event on the interface. These events are identified
 * by the enum Event. All events are managed by SAI for convenience, but SAIs
 * sent by the interface are their own type of event, so be careful.
 * @author Patrick Nguyen
 *
 */
public class InterfaceEvent {
	private SAI event;
	private Event type;
	/**
	 * Enum identifying the events managed by this class.
	 * @author Patrick Nguyen
	 *
	 */
	public enum Event{
		DOUBLE_CLICK,SAI
	}
	
	/**
	 * Creates an InterfaceEvent object with the specified event and type
	 * @param event The SAI object representing the event
	 * @param type Event enum identifying the type of event
	 */
	public InterfaceEvent(SAI event, Event type){
		this.event = event;
		this.type = type;
	}
	
	/**
	 * Creates an InterfaceEvent object with the specified event and type
	 * @param event The SAI object representing the event
	 * @param type Event enum identifying the type of event
	 */
	public InterfaceEvent(SAI event, Event type,String transactionID){
		this.event = event;
		this.type = type;
		this.transactionID=transactionID;
	}
	
	String transactionID;
	
	public String getTransactionID(){ return this.transactionID;}
	
	public void setTransactionID(String id){this.transactionID=id;}
	
	/**
	 * Gets the event
	 * @return SAI representing the event
	 */
	public SAI getEvent() {
		return event;
	}
	/**
	 * Sets the event
	 * @param event SAI representing the event
	 */
	public void setEvent(SAI event) {
		this.event = event;
	}
	/**
	 * Gets the type of event
	 * @return Enum identifying the event
	 */
	public Event getType() {
		return type;
	}
	/**
	 * Sets the type of event
	 * @param type Enum identifying the event
	 */
	public void setType(Event type) {
		this.type = type;
	}
	
}
