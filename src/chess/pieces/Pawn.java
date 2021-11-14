package chess.pieces;

import boardGame.Board;
import boardGame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {
	
	//To check the en passant conditions, the Pawn must have access to the ChessMatch
	private ChessMatch chessMatch;

	public Pawn(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}
	
	@Override
	public String toString() {
		return "P";
	}
	
	/*
	 * Implementation of movement rules for the Pawn
	 */

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

		Position p = new Position(0, 0);

		// White Pawn Movement Logic
		if (getColor() == Color.WHITE) {
			p.setValues(position.getRow() - 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			//First move
			p.setValues(position.getRow() - 2, position.getColumn());
			Position p2 = new Position(position.getRow() - 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) &&
				getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2) && getMoveCount() == 0 ) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			//Diagonal left capture movement
			p.setValues(position.getRow() - 1, position.getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			//Diagonal right capture movement
			p.setValues(position.getRow() - 1, position.getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			//#Special move - En Passant (Whites)
			if(position.getRow() == 3) {
				Position left = new Position(position.getRow(), position.getColumn() - 1);
				//Check if is there a position on the left, if is there an opponent and if the
				//opponent is with the property enPassantVulnerable as true
				if(getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable()) {
					mat[left.getRow() - 1][left.getColumn()] = true;
				}
				Position right = new Position(position.getRow(), position.getColumn() + 1);
				//Check if is there a position on the right, if is there an opponent and if the
				//opponent is with the property enPassantVulnerable as true
				if(getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable()) {
					mat[right.getRow() - 1][right.getColumn()] = true;
				}
			}
			
		} else {
			// Black Pawn Movement Logic
			p.setValues(position.getRow() + 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			//First move
			p.setValues(position.getRow() + 2, position.getColumn());
			Position p2 = new Position(position.getRow() + 1, position.getColumn());
			if (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) &&
				getBoard().positionExists(p2) && !getBoard().thereIsAPiece(p2) && getMoveCount() == 0 ) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			//Diagonal left capture movement
			p.setValues(position.getRow() + 1, position.getColumn() - 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			//Diagonal right capture movement
			p.setValues(position.getRow() + 1, position.getColumn() + 1);
			if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			//#Special move - En Passant (Blacks)
			if(position.getRow() == 4) {
				Position left = new Position(position.getRow(), position.getColumn() - 1);
				//Check if is there a position on the left, if is there an opponent and if the
				//opponent is with the property enPassantVulnerable as true
				if(getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable()) {
					mat[left.getRow() + 1][left.getColumn()] = true;
				}
				Position right = new Position(position.getRow(), position.getColumn() + 1);
				//Check if is there a position on the right, if is there an opponent and if the
				//opponent is with the property enPassantVulnerable as true
				if(getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable()) {
					mat[right.getRow() + 1][right.getColumn()] = true;
				}
			}
			
		}

		return mat;
	}

}
