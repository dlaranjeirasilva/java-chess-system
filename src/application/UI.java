package application;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.Color;

public class UI {

//	Credit to color in console to:
//	https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

// 	Credit to clearScreen() in console to:
// 	https://stackoverflow.com/questions/2979383/java-clear-the-console

	public static void clearScreen() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

	/*
	 * Our readChessPosition(Scanner) will convert the input receive by the user
	 * into a char for the column part and using the substring() method we will be
	 * able to form a ChessPosition to be informed to the application, in case of a
	 * input mismatch sent by the user our method is encapsulated within a try/catch
	 * block to treat this kind of erratic input
	 */
	public static ChessPosition readChessPosition(Scanner sc) {
		try {
			String s = sc.nextLine();
			char column = s.charAt(0);
			int row = Integer.parseInt(s.substring(1));
			return new ChessPosition(column, row);
		} catch (RuntimeException e) {
			throw new InputMismatchException("Error reading ChessPosition. Valid values are from a1 to h8");
		}
	}

	/*
	 * Printing the match will guide the contenders along, making it easier to know
	 * whose turn is and which number of turns already happened
	 * 
	 * As now we are controlling the captured pieces, our printMatch() will also
	 * display and guide our players to know which pieces have been captured
	 */
	public static void printMatch(ChessMatch chessMatch, List<ChessPiece> captured) {
		printBoard(chessMatch.getPieces());
		System.out.println();
		printCapturedPieces(captured);
		System.out.println();
		System.out.println("Turn : " + chessMatch.getTurn());
		
		if(!chessMatch.getCheckMate()) {
			System.out.println("Waiting player: " + chessMatch.getCurrentPlayer());
			//Alert the player of the Check State
			if(chessMatch.getCheck()) {
				System.out.println(ANSI_RED + "CHECK!" + ANSI_RESET);
			}			
		} else {
			System.out.println(ANSI_RED + "CHECKMATE!" + ANSI_RESET);
			System.out.println("Winner: " + chessMatch.getCurrentPlayer());
		}
		
	}

	/*
	 * In case of our board have no pieces selected by the players, our background
	 * will remain uncolored as a default
	 */
	public static void printBoard(ChessPiece[][] pieces) {
		for (int i = 0; i < pieces.length; i++) {
			System.out.print((8 - i) + " ");
			for (int j = 0; j < pieces.length; j++) {
				printPiece(pieces[i][j], false);
			}
			System.out.println();
		}
		System.out.println("  a b c d e f g h");
	}

	/*
	 * The printBoard is overloaded with a boolean parameter to consider the action
	 * of possible movements, and recognizing the possible path of the source piece
	 * the method will track it for the player by coloring it
	 * 
	 * There is no cheat here, it only defines the route that the piece can move, it
	 * doesn't suggests any strategic move
	 */
	public static void printBoard(ChessPiece[][] pieces, boolean[][] possibleMoves) {
		for (int i = 0; i < pieces.length; i++) {
			System.out.print((8 - i) + " ");
			for (int j = 0; j < pieces.length; j++) {
				printPiece(pieces[i][j], possibleMoves[i][j]);
			}
			System.out.println();
		}
		System.out.println("  a b c d e f g h");
	}

	/*
	 * The method printPiece is an auxiliary method to print a single piece, it will
	 * be responsible to populate the board, and to color it correctly
	 * 
	 * Considering that we want to highlight the path of our pieces based on its
	 * source our printPiece will now consider the existence of the background, and
	 * color it in case of a true statement
	 */
	private static void printPiece(ChessPiece piece, boolean background) {
		if (background) {
			System.out.print(ANSI_BLUE_BACKGROUND);
		}
		if (piece == null) {
			System.out.print("-" + ANSI_RESET);
		} else {
			if (piece.getColor() == Color.WHITE) {
				System.out.print(ANSI_WHITE + piece + ANSI_RESET);
			} else {
				System.out.print(ANSI_PURPLE + piece + ANSI_RESET);
			}
		}
		System.out.print(" ");
	}

	/*
	 * Now we treat the control of our captured pieces, considering a list of Black
	 * and White pieces, for the sake of organization and easy understanding, we are
	 * using a stream().filter() with lambda expression to organize our lists and make
	 * a cleaner-easy-to-read code
	 */
	private static void printCapturedPieces(List<ChessPiece> captured) {
		List<ChessPiece> white = 	captured.stream()
									.filter(x -> x.getColor() == Color.WHITE)
									.collect(Collectors.toList());
		
		List<ChessPiece> black = 	captured.stream()
									.filter(x -> x.getColor() == Color.BLACK)
									.collect(Collectors.toList());
		
		System.out.println("Captured pieces:");
		System.out.print("White: ");
		System.out.print(ANSI_WHITE);
		System.out.println(Arrays.toString(white.toArray()));
		System.out.print(ANSI_RESET);
		System.out.print("Black: ");
		System.out.print(ANSI_PURPLE);
		System.out.println(Arrays.toString(black.toArray()));
		System.out.print(ANSI_RESET);	
	}
}
