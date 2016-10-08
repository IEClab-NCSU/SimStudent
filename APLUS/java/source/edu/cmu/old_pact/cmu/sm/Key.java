package edu.cmu.old_pact.cmu.sm;

/*caches hash codes for strings; according to _java 2 performance &
idiom guide_, this can dramatically improve lookup performance*/
final class Key{
	private String key;
	private int hashCode;

	public Key(String key){
		setKey(key);
	}

	public void setKey(String key){
		this.key = key;
		hashCode = key.hashCode();
	}

	public int hashCode(){
		return hashCode;
	}

	public boolean equals(Object object){
		if(this == object){
			return true;
		}
		else if(object == null || getClass() != object.getClass()){
			return false;
		}
		else{
			Key other = (Key)object;
			return key.equals(other.key);
		}
	}
}
