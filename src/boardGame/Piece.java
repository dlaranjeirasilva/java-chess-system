package boardGame;

public class Piece {
	//This position refers to the matrix position
	protected Position position;
	
	private Board board;
	
	public Piece() {
		
	}

	public Piece(Board board) {
		this.board = board;
		this.position = null;
	}

	//Only classes within the same package and subclasses will be able to access the board of a piece
	//The board musn't be accessed by the chess layer, which means, the board is for internal use of
	//the board layer
	protected Board getBoard() {
		return board;
	}
	
	
}
