package model;


public class ViraLetrasModel {
	
	public final String LETTERS[] = { "A", "A", "A", "A", "A", "A", "A",
			"E", "E", "E", "E", "E", "E", "E", "I", "I", "I", "I", "I", "I",
			"O", "O", "O", "O", "O", "O", "O", "U", "U", "U", "U", "B", "B",
			"C", "C", "D", "D", "F", "G", "H", "J", "L", "L", "L", "M", "M",
			"N", "N", "P", "P", "Qu", "R", "R", "R", "S", "S", "S", "S", "T",
			"T", "V", "V", "X", " " };
	
	private boolean isWinner, isDicesRolled, isMyTurn;
	private Pieces boardPieces[];
	private int dices[], score[];

	public ViraLetrasModel(String name){
		this.boardPieces = new Pieces[64];
		this.score = new int[2];
		this.dices = new int[2];
		this.isWinner = false;
		this.isDicesRolled = false;
		this.setMyTurn(true);
		
		this.initGameBoard();
	}
	
	private void initGameBoard(){
		int countLetter = 0;

		for(int i=0; i<boardPieces.length; i++){
			int random = (int) (Math.random() * boardPieces.length);
			if(boardPieces[random] == null){
				boardPieces[random] = new Pieces(LETTERS[countLetter]);
				countLetter++;
			} else {
				i--;
			}
		}
	}
	
	public void resetGameBoard(){
		this.boardPieces = new Pieces[64];
		initGameBoard();
	}
	
	/**
	 * Define as flags para seu estado inicial.
	 */
	public void resetFlags(){
		this.isWinner = false;
		this.isDicesRolled = false;
		this.setMyTurn(true);
	}

	public Pieces[] getBoardPieces() {
		return boardPieces;
	}
	
	//TODO: (info) de vetor para int com parametro..
	public int getDices(int index) {
		return dices[index];
	}

	//TODO: (info) de vetor de int para dois inteiros.
	public void setDices(int a, int b) {
		this.dices[0] = a;
		this.dices[1] = b;
	}
	
	//TODO: (info) utilizada numa chamada do sendToServer
	public int getSumDices(){
		return dices[0] + dices[1];
	}
	
	public boolean isDicesRolled() {
		return isDicesRolled;
	}

	public void setDicesRolled(boolean isRolled) {
		this.isDicesRolled = isRolled;
	}

	public boolean isWinner() {
		return isWinner;
	}

	public void setWinner(boolean isWinner) {
		this.isWinner = isWinner;
	}

	public int rollDice(){
		return (int)(Math.random() * 6 + 1);
	}

	public boolean isMyTurn() {
		return isMyTurn;
	}

	public void setMyTurn(boolean isMyTurn) {
		this.isMyTurn = isMyTurn;
	}
	
	public int getScore(int index) {
		return score[index];
	}
	
	//TODO (info) modificado para atender as necessidades do protocolo...
	public void setScore(int index, int value) {
		this.score[index] = value;
	}

	public String syncSendBoardGame(){
		StringBuilder syncMessage = new StringBuilder();
		for(Pieces p : boardPieces)
			syncMessage.append(p.getFlipped() + "::");
		return syncMessage.toString();
	}
	
	public void syncReceiveBoardGame(String board){
		String newNames[] = board.split("::");
		for(int i=0; i<boardPieces.length; i++){
			boardPieces[i].setFlipped(newNames[i]);
		}
	}
	
	
}
