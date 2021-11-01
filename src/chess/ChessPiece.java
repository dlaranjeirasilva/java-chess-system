package chess;

import boardGame.Board;
import boardGame.Piece;

public class ChessPiece extends Piece {

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
