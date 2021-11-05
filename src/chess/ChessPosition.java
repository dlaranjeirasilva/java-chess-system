package chess;

import boardGame.Position;

public class ChessPosition {

	private Character column;
	private Integer row;
	
	public ChessPosition() {
		
	}

	public ChessPosition(Character column, Integer row) {
		if(column < 'a' || column > 'h' || row < 1 || row > 8) {
			throw new ChessException("Error instantiating ChessPosition. Valid values are from a1 to h8");
		}

		this.column = column;
		this.row = row;
	}

	public Character getColumn() {
		return column;
	}

	public Integer getRow() {
		return row;
	}
	
	/*
	 * As the matrix sees the index of 0 in row and column, we must reconfigure the
	 * understanding of these inputs to comply with the board organization:
	 * 
	 * 8 - - - - - - - - 
	 * 7 - - - - - - - - 
	 * 6 - - - - - - - - 
	 * 5 - - - - - - - - 
	 * 4 - - - - - - - - 
	 * 3 - - - - - - - - 
	 * 2 - - - - - - - - 
	 * 1 - - - - - - - - 
	 *   a b c d e f g h
	 * 
	 * As we can see, the application must be able to read the information input of
	 * a1 to h8 as an unified board position, to make that happen we transform this
	 * understanding with an auxiliary method that converts the input row 1 to 0 and
	 * the column 'a' to 0
	 * 
	 * After that, the application gets this matrix position and places it in a chess
	 * board position, it means, considering the column first and the row after, based
	 * on the return of the toPosition() method
	*/
	protected Position toPosition() {
		return new Position(8 - row, column - 'a');
	}
	
	protected static ChessPosition fromPosition(Position position) {
		return new ChessPosition((char)('a' + position.getColumn()), 8 - position.getRow());
	}
	
	/*
	 * This override consists on the correct printing of the chess position, the empty
	 * String before the column and row is for the compiler understands that this is
	 * a concatenation and not a sum
	*/
	@Override
	public String toString() {
		return	"" + column + row;
	}
}
