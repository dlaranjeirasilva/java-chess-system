package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardGame.Board;
import boardGame.Piece;
import boardGame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {

	private Integer turn;
	private Color currentPlayer;
	private Board board;
	private Boolean check;
	private Boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<Piece>();
	private List<Piece> capturedPieces = new ArrayList<Piece>();

	/*
	 * Only the ChessMatch must know the dimension of a chess board, for that reason
	 * we define the ChessMatch constructor with the matrix dimension of 8 x 8 of
	 * the imported board
	 * 
	 * As the ChessMatch is called, the constructor will instantiate a board with a
	 * quadratic matrix of 8 x 8 and also call the initialSetup() to build the
	 * ChessPiece over the board
	 * 
	 * We now consider the player switching, and for that we define the turn that of
	 * course starts by turn 1, and in the chess match, the Whites always starts
	 */

	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		check = false;
		checkMate = false;
		initialSetup();
	}

	public Integer getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public Boolean getCheck() {
		return check;
	}
	
	public Boolean getCheckMate() {
		return checkMate;
	}
	
	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}
	
	public ChessPiece getPromoted() {
		return promoted;
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
	 * The possibleMoves(source) matrix method will grant us the ability to
	 * highlight the valid movements of our pieces based on its source, so that our
	 * UI become more user friendly and more dynamic
	 */
	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position position = sourcePosition.toPosition();

		validateSourcePosition(position);

		return board.piece(position).possibleMoves();

	}

	/*
	 * To perform a chess movement, it is necessary to validate the existence of
	 * piece in the source position and if the piece exists, it must validate if
	 * there is any possible movement for that piece. In case of both tests return
	 * true, it is necessary to validate if the target position, in relation with
	 * the source, is valid, in case of a true statement, the makeMove(source,
	 * target) takes place
	 * 
	 * After a movement takes place, the player switches
	 * 
	 * With the testCheck() implemented, now we update the performChessMove() in
	 * order to test if the movement made caused the match to be in a Check State,
	 * also, we consider if the currentPlayer didn't put himself under Check State,
	 * in case of it, the movement must be undone
	 * 
	 * performChessMove now evaluates the possibility of the special movement
	 * en passant
	 */
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();

		validateSourcePosition(source);
		validateTargetPosition(source, target);

		Piece capturedPiece = makeMove(source, target);
		
		if(testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("You can't put yourself under check");
		}
		
		ChessPiece movedPiece = (ChessPiece)board.piece(target);
		
		//#Special Move - Promotion
		promoted = null;
		if(movedPiece instanceof Pawn) {
			if((movedPiece.getColor() == Color.WHITE && target.getRow() == 0) || (movedPiece.getColor() == Color.BLACK && target.getRow() == 7)) {
				promoted = (ChessPiece)board.piece(target);
				promoted = replacePromotedPiece("Q");
			}
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		
		if(testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		} else {
			nextTurn();			
		}
		
		//#Special Move - En Passant (source with -2 or +2 to consider both colors)
		if(movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		} else {
			enPassantVulnerable = null;
		}
		
		return (ChessPiece) capturedPiece;
	}
	
	//#Special Move - Promotion
	public ChessPiece replacePromotedPiece(String type) {
		if(promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		
		if(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
			return promoted;
		}
		
		//Remove the instanceof Pawn found in the performChessMove test
		Position pos = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove(p);
		
		//Add the new piece instance according to the selected type
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		
		return newPiece;
	}
	
	//Aux. method for promotion
	private ChessPiece newPiece(String type, Color color) {
		if(type.equals("B")) return new Bishop(board, color);
		if(type.equals("N")) return new Knight(board, color);
		if(type.equals("R")) return new Rook(board, color);
		return new Queen(board, color);
	}

	/*
	 * Our makeMove(source, target) will need the source position and a target one,
	 * based on the Chess rules, every time we make a move to capture a piece we
	 * must remove the piece from its source position, also remove the captured
	 * piece from its source (which is actually the target of the source position of
	 * the moved piece) and placePiece(piece, position) in its defined target, after
	 * that we return the capturedPiece as the result of the movement
	 * 
	 * Now with the lists, we can remove the piece on the board and consider it as a
	 * captured one on our printMatch() within the UI
	 */
	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece)board.removePiece(source);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);

		board.placePiece(p, target);
		
		if(capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}
		
		//#Special Move - King side Rook ("Small Castling")
		if(p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}
		
		//#Special Move - Queen side Rook ("Great Castling")
		if(p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removePiece(sourceT);
			board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		}
		
		//#Special Move - En Passant
		/*
		 * As the En Passant move is an exception move that doesn't follow the common
		 * capture logic rule, we must perform the capture logic manually
		 * 
		 * So, if out pawn moved from its source column and doesn't find an opponent
		 * on its target, then it is an En Passant type capture
		 * 
		 * And, we check the pawn color to define the position of its opponent
		*/
		if(p instanceof Pawn) {
			if(source.getColumn() != target.getColumn() && capturedPiece == null) {
				Position pawnPosition;
				if(p.getColor() == Color.WHITE) {
					pawnPosition = new Position(target.getRow() + 1, target.getColumn());
				} else {
					pawnPosition = new Position(target.getRow() - 1, target.getColumn());
				}
				
				capturedPiece = board.removePiece(pawnPosition);
				capturedPieces.add(capturedPiece);
				piecesOnTheBoard.remove(capturedPiece);
			}
		}
		
		return capturedPiece;
	}
	
	/*
	 * The undoMove() will take action whenever a player make an invalid move, for
	 * instance, put itself under CHECK
	*/
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece)board.removePiece(target);
		p.decreaseMoveCount();
		board.placePiece(p, source);
		
		if(capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
		
		//#Special Move - King side Rook ("Small Castling")
		if(p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece)board.removePiece(targetT);
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}
		
		//#Special Move - Queen side Rook ("Great Castling")
		if(p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece)board.removePiece(targetT);
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}
		
		//#Special Move - En Passant
		/*
		 * Here we follow basically the same logic of the makeMove, but following
		 * the inverse logic to undoMove, note that the common undoMove return the
		 * piece to its source position, considering a normal capture, and again
		 * we must undo the movement manually, as it doesn't follow the common
		 * capture logic
		*/
		if(p instanceof Pawn) {
			if(source.getColumn() != target.getColumn() && capturedPiece == enPassantVulnerable) {
				//We remove it from the wrong source (the old target, as the general logic
				//of the undoMove informs)
				ChessPiece pawn = (ChessPiece)board.removePiece(target);
				Position pawnPosition;
				if(p.getColor() == Color.WHITE) {
					pawnPosition = new Position(3, target.getColumn());
				} else {
					pawnPosition = new Position(4, target.getColumn());
				}
				
				board.placePiece(pawn, pawnPosition);
				/*
				 * We don't need to do the remove and add in the list because our general
				 * undoMove logic already deals with this, placing the piece in the "wrong"
				 * source, our job is just to move it to the correct position according to the
				 * en passant logic
				 */
			}
		}
		
	}

	/*
	 * The thereIsAPiece() method can throw a BoardException, the
	 * validateSourcePosition throw a ChessException, we can consider that a
	 * ChessException can also be considered a BoardException, but in this case is
	 * an exception specific to the Chess game, to make this coherent and simple we
	 * will now make our ChessException extends the BoardException, instead of
	 * extends a RuntimeException, by doing that we guarantee that our
	 * ChessException will also be seen as a BoardException too
	 * 
	 * As we are starting to implement the pieces movements, our position validation
	 * must consider the case if the piece existence is valid but also if the piece
	 * selected has possible moves to perform, this is where the board checks the
	 * piece position and confirms if isn't there a movement to happen
	 * "!board.piece(position).isThereAnyPossibleMove()"
	 * 
	 * Our validation must consider if the current player is not trying to move an
	 * opponent's piece, which of course would brake the rules, for that we now
	 * implement another ChessExcepetion considering that the player can only select
	 * the source of its own pieces
	 */
	private void validateSourcePosition(Position position) {
		if (!board.thereIsAPiece(position)) {
			throw new ChessException("There is no piece on source position");
		}
		
		if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
			throw new ChessException(currentPlayer + " you can't select an opponent's piece");
		}

		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("There is no possible moves for the chosen piece");
		}
	}

	/*
	 * To validate a target position, we check if the piece position (source) within
	 * the defined board have a possibility to move to its target
	 */
	private void validateTargetPosition(Position source, Position target) {
		if (!board.piece(source).possibleMove(target)) {
			throw new ChessException("The chosen piece can't move to target position");
		}
	}
	
	/*
	 * Simple implementation of a player switching, considering a ternary condition,
	 * cleaner and simpler
	*/
	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	/*
	 * To create the CHECK logic, a sum of auxiliary methods and properties were
	 * considered, as it shows:
	 * private Boolean check 	- Will determinate if a match is in check or not
	 * 						
	 * opponent(color) 	- The objective is to return the color of the opponent's
	 * 					pieces in general
	 * 
	 * king(color) 		- The objective is to return the king of the opponent
	 * 					color
	 * 
	 * testCheck(color)	- The method will check the position of the player's
	 * 					king and verify if any of the opponent's pieces can move
	 * 					towards the currentPlayer's King
	*/
	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king(Color color) {
		List<Piece> list = 	piecesOnTheBoard.stream()
							.filter(x -> ((ChessPiece)x).getColor() == color)
							.collect(Collectors.toList());
		
		for(Piece p : list) {
			if(p instanceof King) {
				return (ChessPiece)p;
			}
		}
		/*
		 * This kind of exception MUST NEVER happen, although it is necessary to throw
		 * it to prevent application crash
		 */
		throw new IllegalStateException("There is no " + color + " king on the board");
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces =	piecesOnTheBoard.stream()
										.filter(x -> ((ChessPiece)x).getColor() == opponent(color))
										.collect(Collectors.toList());
		for(Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if(mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testCheckMate(Color color) {
		//First test if it isn't in CHECK state
		if(!testCheck(color)) {
			return false;
		}
		
		//Gather all pieces of the player's color in the board
		List<Piece> list =	 piecesOnTheBoard.stream()
							.filter(x -> ((ChessPiece)x).getColor() == color)
							.collect(Collectors.toList());
		
		//Now go through all pieces in the list
		for(Piece p : list) {
			//All possibleMoves() of p in the matrix
			boolean[][] mat = p.possibleMoves();
			//Go through all lines
			for(int i = 0; i<board.getRows(); i++) {
				//Go through all columns
				for(int j = 0; j<board.getColumns(); j++) {
					//Is the position in the board a possibleMove()?
					if(mat[i][j]) {
						//If yes:
						//Gather the source and target positions
						Position source = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						
						//Make the move to test if the move put the player in CHECK state
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						//Undo the move after the test
						undoMove(source, target, capturedPiece);
						//Does this possibleMove() removes the player's King of CHECK state?
						if(!testCheck) {
							//If yes:
							//CHECK state vanishes
							return false;
						}
					}
				}
			}
		}
		//If my loop ends, and no possibleMove() removes the CHECK state, it is CHECK MATE!
		//So, returns true
		return true;
	}
	
	/*
	 * To our match understands the board position as the board shows it, we create
	 * a method that gets the input of the player and converts it to a board
	 * position with the toPosition() method created within the ChessPosition Class
	 * 
	 * In addition, our list of pieces on the board will also receive the information
	 * of the piece and populate our piece control
	 */

	private void placeNewPiece(char column, int row, ChessPiece piece) {
		board.placePiece(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}
	
	/*
	 * 8 R N B - K B N R 
	 * 7 P P P P P P P P 
	 * 6 - - - - - - - - 
	 * 5 - - - - - - - - 
	 * 4 - - - - - - - - 
	 * 3 - - - - - - - - 
	 * 2 P P P P P P P P 
	 * 1 R N B - K B N R 
	 *   a b c d e f g h
	 */   
	private void initialSetup() {
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('b', 1, new Knight(board, Color.WHITE));
		placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('d', 1, new Queen(board, Color.WHITE));
		placeNewPiece('e', 1, new King(board, Color.WHITE, this));
		placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
		placeNewPiece('g', 1, new Knight(board, Color.WHITE));
		placeNewPiece('h', 1, new Rook(board, Color.WHITE));
		placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
		placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
		placeNewPiece('b', 8, new Knight(board, Color.BLACK));
		placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('d', 8, new Queen(board, Color.BLACK));
		placeNewPiece('e', 8, new King(board, Color.BLACK, this));
		placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
		placeNewPiece('g', 8, new Knight(board, Color.BLACK));
		placeNewPiece('h', 8, new Rook(board, Color.BLACK));
		placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
		placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
	}

}
