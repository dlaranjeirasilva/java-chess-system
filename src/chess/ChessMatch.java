package chess;

import boardGame.Board;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

	private Board board;

	/*
	 * Only the ChessMatch must know the dimension of a chess board, for that reason
	 * we define the ChessMatch constructor with the matrix dimension of 8 x 8 of
	 * the imported board
	 * 
	 * As the ChessMatch is called, the constructor will instantiate a board with a
	 * quadratic matrix of 8 x 8 and also call the initialSetup() to build the
	 * ChessPiece over the board
	 */

	public ChessMatch() {
		board = new Board(8, 8);
		initialSetup();
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

	/*
	 * To our match understands the board position as the board shows it, we create
	 * a method that gets the input of the player and converts it to a board
	 * position with the toPosition() method created within the ChessPosition Class
	 */
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
	}

	private void initialSetup() {
		placeNewPiece('c', 1, new Rook(board, Color.WHITE));
		placeNewPiece('c', 2, new Rook(board, Color.WHITE));
		placeNewPiece('d', 2, new Rook(board, Color.WHITE));
		placeNewPiece('e', 2, new Rook(board, Color.WHITE));
		placeNewPiece('e', 1, new Rook(board, Color.WHITE));
		placeNewPiece('d', 1, new King(board, Color.WHITE));
		
		placeNewPiece('c', 7, new Rook(board, Color.BLACK));
		placeNewPiece('c', 8, new Rook(board, Color.BLACK));
		placeNewPiece('d', 7, new Rook(board, Color.BLACK));
		placeNewPiece('e', 7, new Rook(board, Color.BLACK));
		placeNewPiece('e', 8, new Rook(board, Color.BLACK));
		placeNewPiece('d', 8, new King(board, Color.BLACK));
	}

}
