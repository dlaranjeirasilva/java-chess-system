package boardGame;

public abstract class Piece {
	// This position refers to the matrix position
	protected Position position;

	private Board board;

	public Piece() {

	}

	public Piece(Board board) {
		this.board = board;
		this.position = null;
	}

	/*
	 * Only classes within the same package and subclasses will be able to access
	 * the board of a piece The board mustn't be accessed by the chess layer, which
	 * means, the board is for internal use of the board layer
	 */
	protected Board getBoard() {
		return board;
	}

	public abstract boolean[][] possibleMoves();

	/*
	 * Here we are applying a hook method, defining a concrete method possibleMove()
	 * which "hooks" the abstract action defined to happen in the possibleMoves(),
	 * notice that our abstract method requires a matrix of rows and columns, and
	 * our concrete method provides it with the position.getRow() and
	 * position.getColumn()
	 */
	public boolean possibleMove(Position position) {
		return possibleMoves()[position.getRow()][position.getColumn()];
	}

	/*
	 * The isThereAnyPossibleMove() will be responsible to identify if the provided
	 * matrix of the board, required by the possibleMoves() method, is a valid route
	 * for our piece to use, with this implementation we can determinate if our piece
	 * is constrained between its allies or have clear passage to capture an enemy
	 * along the way or to stop within its own movement definition
	*/
	public boolean isThereAnyPossibleMove() {
		boolean[][] mat = possibleMoves();

		for (int i = 0; i < mat.length; i++) {
			for (int j = 0; j < mat.length; j++) {
				if(mat[i][j]) {
					return true;
				}
			}
		}
		return false;
	}
}
