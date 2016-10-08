/**
 * Copyright 2007 Carnegie Mellon University.
 */
package jess;

/**
 * Copy the data in an {@link Activation}. This is meant to be an object storeable independently
 * of the Rete, with enough information that it can uniquely identify an Activation
 * currently on the agenda. 
 * Contents were taken from the code in {@link Activation#equals(Object)}.
 */
public class ActivationData {
	
	/** Copy of the data in a {@link Token}. */
	public class TokenData {
		
		/** Number of facts. Same as {@link Token#size()}. */
		private int size = 0;

		/** Copy of Fact returned by {@link Token#topFact()}. */
		private Fact topFact = null;

		/** Pointer to data-saver for Token parent. */
		private TokenData parent = null;

		/** Used internally by {@Token#equals(Object)}.*/
		private int sortcode;

		/**
		 * Copy the data out of the given {@link Token}.
		 * @param token the source
		 */
		public TokenData(Token token) {
			size = token.size();
			sortcode = token.m_sortcode;
			if (token.topFact() != null) {
				Object copy = token.topFact().clone();
				if (copy instanceof Fact)
					topFact = (Fact) copy;
			}
			if (token.getParent() != null)
				parent = new TokenData(token.getParent());
		}
		
		/**
		 * Tell whether the data in this object match those in the given {@link Token}.
		 * @param token
		 * @return true if our data match those in token
		 */
		public boolean match(Token token) {
			if (size != token.size())
				return false;
			if (sortcode != token.m_sortcode)
	            return false;
			if (!topFact.equals(token.topFact()))
				return false;
			if (parent == null)
				return (token.getParent() == null);
			return parent.match(token.getParent());
		}
		
		/**
		 * For debugging. Cf. {@link Token#toString()}.
		 * @return dump of data
		 */
		public String toString() {
			StringBuffer sb = new StringBuffer(100);
	        sb.append("size=").append(size);			
	        sb.append(" sortcode=").append(sortcode);
	        showFact(sb);
	        return sb.toString();
		}
		
		/**
		 * Dump the facts in this object. Outputs most distant parent first.
		 * @param sb buffer to append to
		 * @return sb for further appending
		 */
		private StringBuffer showFact(StringBuffer sb) {
			if (parent != null)
				parent.showFact(sb);
			if (topFact == null)
				sb.append(" (null fact)");
			else {
				sb.append(" f-").append(topFact.getFactId());
				sb.append(" ").append(topFact.toString());
			}
			return sb.append(";");
		}
	}
	
	/** Activation saves a pointer to the {@link Defrule}; here we save the name. */
	private final String ruleName;
	
	/** True if source {@link Activation#isInactive()}==true. */
	private boolean inactive;
	
	/** Value from {@link Activation#getSalience()}. */
	private int salience;
	
	/** Copy of the original Activation's {@link Activation#getToken()}. */
	private TokenData tokenData = null;
	
	/**
	 * Set all final fields.
	 * @param act Activation to copy
	 * @throws JessException
	 */
	public ActivationData(Activation act)
			throws JessException {
		this.ruleName = act.getRule().getName();
		this.inactive = act.isInactive();
		this.salience = act.getSalience();
		if (act.getToken() != null)
			this.tokenData = new TokenData(act.getToken());
	}
	
	/**
	 * Tell whether this object matches the given {@link Activation}.
	 * @param act Activation to match
	 * @return true if matches
	 */
	public boolean match(Activation act) {
		if (!ruleName.equals(act.getRule().getName()))
			return false;
		return tokenData.match (act.getToken());
	}
	
	/**
	 * Debugging representation parrots {@link Activation#toString()}.
	 * @return
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer(100);
		sb.append("[ActivationData: ").append(ruleName);
		if (inactive)
			sb.append(" (inactive)");
		sb.append(" ").append(tokenData.toString());
        sb.append(" salience=").append(salience);
        sb.append("]");
        return sb.toString();
	}
}
