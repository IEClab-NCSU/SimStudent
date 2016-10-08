package edu.cmu.pact.miss.MetaTutor;

public class ListSelection {

	/**	 */
	private String description;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ListSelection(String msg) {
		description = msg;
	}
	
	public String toString(){
		return description;
	}
}
