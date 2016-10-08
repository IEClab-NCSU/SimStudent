package edu.cmu.old_pact.htmlPanel;

	public class RegionString {
		private String region;
		private int newPos;
		private boolean isAdjusted;
	
		public RegionString() {
			isAdjusted = false;
			newPos = -1; 
		}
	
		public String getRegionStr() {
			return region;
		}
		
		public void setRegionStr(String s) {
			region = s;
		}
		
		public int getNewPos() {
			return newPos;
		}
		
		public void setNewPos(int pos){
			newPos = pos;
		}
		
		public boolean getIsAdjusted() {
			return isAdjusted;
		}
		
		public void setIsAdjusted(boolean adj) {
			isAdjusted = adj;
		}
			
}	
	
		
		
		