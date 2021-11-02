package chess;

import boardGame.Board;
import boardGame.Piece;

/*
 * As we defined the Piece Class as an abstract one, the ChessPiece is obliged to
 * implement the abstract methods defined within the Piece Class, but our ChessPiece
 * Class is still to much generic to implement the movements within itself, then we
 * determinate that our ChessPiece is also an abstract class, that way we push that
 * obligation forwards to our individual pieces, that is, to the ones who really
 * knows their own movement rules
*/
public abstract class ChessPiece extends Piece {

	private Color color;

	public ChessPiece(Board board, Color color) {
		super(board);
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
	
	/*
	 * As we are building a console based system, the pieces will have an override in the
	 * toString() to print the initial letter of the piece in the board
	 */
}
