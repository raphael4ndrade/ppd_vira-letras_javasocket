package model;


public class Pieces {
	
	private String flipped, unflipped;
	private boolean isFlipped, isValid;
	
	public Pieces(String flipped){
		this.flipped = flipped;
		this.unflipped = "  ";
		this.isFlipped = false;
		this.isValid = true;
	}
	
	public void setFlipped(String flipped) {
		this.flipped = flipped;
	}

	public boolean isFlipped() {
		return isFlipped;
	}

	public void setFlipped(boolean isFlipped) {
		this.isFlipped = isFlipped;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public String getUnflipped() {
		return unflipped;
	}

	public String getFlipped(){
		return this.flipped;
	}
	

}
