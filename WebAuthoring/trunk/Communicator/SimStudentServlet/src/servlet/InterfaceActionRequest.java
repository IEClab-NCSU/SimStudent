package servlet;

import interaction.SAI;

/**
 * Class representing a request to grade an input.
 * @author Patrick Nguyen
 *
 */
public class InterfaceActionRequest extends RequestMessage{
	private SAI sai;
	public InterfaceActionRequest(SAI sai){
		this.sai=sai;
	}
	
	/**
	 * Gets the SAI
	 * @return
	 */
	public SAI getSai() {
		return sai;
	}
	
	/**
	 * Sets the SAI
	 * @param sai
	 */
	public void setSai(SAI sai) {
		this.sai = sai;
	}
}
