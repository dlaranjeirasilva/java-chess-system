package chess;

import boardGame.Board;

public class ChessMatch {

	private Board board;

	// Only the ChessMatch must know the dimension of a chess board, for that reason
	// we define the ChessMatch constructor with the matrix dimension of 8 x 8 of
	// the imported board
	public ChessMatch() {
		board = new Board(8, 8);
	}

	/*
	 * The ChessPiece method must return a matrix of ChessPieces in the defined
	 * board of the ChessMatch constructor. As we know, the board has the matrix of
	 * pieces in it, but our method returns a ChessPiece because it's in the chess
	 * layer, as we are developing a layer based system, the chess match mustn't
	 * know the class Piece, so we basically are saying that our application will
	 * downcast the Piece to ChessPiece in the board matrix within the ChessMatch
	 */

	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}

}
