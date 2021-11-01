package boardGame;

public class Board {

	private Integer rows;
	private Integer columns;
	private Piece[][] pieces;
	
	public Board() {
		
	}
	
	/*	
	 * Within the board constructor we will prevent the creation of an invalid matrix
	 * by placing a throw declaration in case of the attempt
	*/	
	public Board(Integer rows, Integer columns) {
		if(rows < 1 || columns < 1) {
			throw new BoardException("Error creating board: thre must be at least 1 row and 1 column");
		}
		
		this.rows = rows;
		this.columns = columns;
		pieces = new Piece[rows][columns];
	}
	
	/*
	 * methods setRows() and setColumns() removed as it would not make sense to have
	 * methods that permit change the board matrix after it is created
	*/
	public Integer getRows() {
		return rows;
	}

	public Integer getColumns() {
		return columns;
	}
	
	/*
	 * Using the auxiliary method positionExists(row, column), we can prevent the
	 * null position error within the method piece(row, column)
	 * 
	 * We do the same to the piece(position) method
	*/
	public Piece piece(int row, int column) {
		if(!positionExists(row, column)) {
			throw new BoardException("Position not on the board");
		}
		
		return pieces[row][column];
	}
	
	public Piece piece(Position position) {
		if(!positionExists(position)) {
			throw new BoardException("Position not on the board");
		}
		
		return pieces[position.getRow()][position.getColumn()];
	}
	
	/*
	 * In the placePiece(piece, position) we must test if already has a piece in the
	 * requested position, and if the condition is true, a throw declaration informs
	 * it to the user
	 * 
	 * To not overload the placePiece(piece, position) method, instead of testing if
	 * the position really exists in this method, we will add this test within the
	 * thereIsAPiece(position) method, which makes more sense, we can only consider
	 * placing a piece if the thereIsAPiece(position) recognizes its existence before
	*/
	public void placePiece(Piece piece, Position position) {
		if(thereIsAPiece(position)) {
			throw new BoardException("There is already a piece on position " + position);
		}
		
		pieces[position.getRow()][position.getColumn()] = piece;
		piece.position = position;
	}
	
	/*
	 * The removePiece(position) method will test if the piece is in inputed position
	 * if it isn't, returns null, although if the piece is actually in the inputed data
	 * an auxiliary variable will store a null information to return and the pieces matrix
	 * will determinate the position informed also as null
	*/
	public Piece removePiece(Position position) {
		if(!positionExists(position)) {
			throw new BoardException("Position not on the board");
		}
		
		if(piece(position) == null) {
			return null;
		}
		
		Piece aux = piece(position);
		aux.position = null;
		pieces[position.getRow()][position.getColumn()] = null;
		return aux;
	}
	
	/*
	 * The positionExists(int row, int column) will be an auxiliary method to test if
	 * the position is inside the board, for that we test if the row is greater than
	 * 0 and less than row quantity defined in rows attribute, the same logic is applied
	 * for the columns
	 * 
	 * based on the return of the auxiliary method, the positionExists(Position position)
	 * can return an unified information of rows and columns
	*/
	private boolean positionExists(int row, int column) {
		return row >= 0 && row < rows && column >= 0 && column < columns;
	}
	
	public boolean positionExists(Position position) {
		return positionExists(position.getRow(), position.getColumn());
	}
	
	/*
	 * The thereIsAPiece(Position position) method get the information returned by the
	 * piece(Position position) and if it is not null, it returns the provided position
	*/
	public boolean thereIsAPiece(Position position) {
		if(!positionExists(position)) {
			throw new BoardException("Position not on the board");
		}
		
		return piece(position) != null;
	}
	
}
