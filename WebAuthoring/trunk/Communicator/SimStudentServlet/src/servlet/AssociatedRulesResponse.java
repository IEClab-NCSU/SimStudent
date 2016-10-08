package servlet;
import interaction.SAI;

import java.util.List;

/**
 * Class representing a response of type Associated Rules. These are usually sent as a response to interface action requests.
 * @author Patrick Nguyen
 *
 */
public abstract class AssociatedRulesResponse extends ResponseMessage{
	private String indicator;
	private SAI sai;
	private String actor;
	private List<String> rules;
	private String skillBarDelimiter;
	private String stepID;
	private boolean logAsResult;
	
	public AssociatedRulesResponse(){
		setMessageType("AssociatedRules");
	}
	
	/**
	 * Gets the indicator
	 * @return indicator
	 */
	public String getIndicator() {
		return indicator;
	}
	
	/**
	 * Sets the indicator
	 * @param indicator
	 */
	public void setIndicator(String indicator) {
		this.indicator = indicator;
	}
	
	/**
	 * Gets the SAI
	 * @return sai
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
	
	/**
	 * Gets the actor
	 * @return actor
	 */
	public String getActor() {
		return actor;
	}
	
	/**
	 * Sets the actor
	 * @param actor
	 */
	public void setActor(String actor) {
		this.actor = actor;
	}
	
	/**
	 * Gets the rules
	 * @return rules
	 */
	public List<String> getRules() {
		return rules;
	}
	
	/**
	 * Sets the rules
	 * @param rules
	 */
	public void setRules(List<String> rules) {
		this.rules = rules;
	}
	
	/**
	 * Gets the skill bar delimiter
	 * @return skillBarDelimiter
	 */
	public String getSkillBarDelimiter() {
		return skillBarDelimiter;
	}
	
	/**
	 * Sets the skill bar delimiter
	 * @param skillBarDelimiter
	 */
	public void setSkillBarDelimiter(String skillBarDelimiter) {
		this.skillBarDelimiter = skillBarDelimiter;
	}
	
	/**
	 * Gets the Step ID
	 * @return stepID
	 */
	public String getStepID() {
		return stepID;
	}
	
	/**
	 * Sets the Step ID
	 * @param stepID
	 */
	public void setStepID(String stepID) {
		this.stepID = stepID;
	}
	
	/**
	 * Whether we should log or not
	 * @return logAsResult - if we should log or not
	 */
	public boolean isLogAsResult() {
		return logAsResult;
	}
	
	/**
	 * Sets whether we should log or not
	 * @param logAsResult - if we should log or not
	 */
	public void setLogAsResult(boolean logAsResult) {
		this.logAsResult = logAsResult;
	}
}
