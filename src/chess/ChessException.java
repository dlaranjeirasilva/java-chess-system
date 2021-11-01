package chess;

import boardGame.BoardException;

/*
 * With this change of inheritance our application will be able to treat
 * the ChessException also as a BoardException, to be more clear, the
 * ChessException is not a simple RuntimeException that happens unexpectedly,
 * the application can consider that any ChessException is also part of
 * any other BoardException, but a little more specific to the Chess game
*/
public class ChessException extends BoardException {

	private static final long serialVersionUID = 1L;
	
	public ChessException(String msg) {
		super(msg);
	}

}
