package chessproject;
import java.util.ArrayList;
/*  Pieces
	Abstract Class which represents the information and common methods 
	used by the pieces in chess. 

	COSC 3P71 - Chess Project
	Sarah Childs & Matt Grahlman 
	January 12, 2018 */
public abstract class Pieces {
	public enum Type { PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING };
	
	public Boolean hasMoved;
	public Type pieceType;
	protected int rNum;
	protected int cNum;
	protected char displayValue; // Char to be displayed on the board
	protected Board.Team boardTeam; 
	protected double weight;
	
	// Constructor
    public Pieces(Board.Team t, Type type, int r, int c, double weightVal) {
    	pieceType = type;
    	rNum = r;
    	cNum = c;
		hasMoved = false;
		boardTeam = t;
		weight = weightVal;
    }
    
	// 'Copy constructor'
	public Pieces(Pieces p) {
		pieceType = p.pieceType;
		rNum = p.rNum;
		cNum = p.cNum;
		hasMoved = p.hasMoved;
		boardTeam = p.boardTeam;
		weight = p.weight;
		displayValue = p.displayValue;
	}
	
	// Used to determine if the piece is capable of making any legal moves
	public abstract Boolean AbleToMove(Board board);
	
	// Generates a list of all possible actions the piece can take based on the current board configuration
	public abstract ArrayList<AIAction> GenerateActions(Board board);
	
	// Determines if the desired move is possible and does not check the king
	// Allows for the system to ignore a piece if there is one on a space, and allows for bypassing the 
	// legality checks to prevent infinite/long loops from occuring during IsCheck
	public abstract Boolean checkMove(int destR, int destC, Board board, Boolean ignoreTeam, Boolean isLegal);
	
	// Determines if the desired move is possible and does not check the king
    public Boolean checkMove(int destR, int destC, Board board) {
		return checkMove(destR, destC, board, false, true);
	}
     
	// Checks if the desired move is possible without checking the king
	protected Boolean CheckLegality(int destR, int destC, Board board) {
		return board.CheckLegalMove(this, destR, destC);
	}
	
	// Sets the position of the piece to the desired row/col 
	// Allows for the AI to "move" the piece when looking at future boards
	public void setPosition(int row, int col, Boolean moved) {
		rNum = row;
    	cNum = col;
		hasMoved = moved;
	}
	
	// Sets the position of the piece to the desired row/col
	public void setPosition(int row, int col){
    	setPosition(row, col, true);
	}	
	
	// Getters
	public double GetWeight() { return weight; 	}
	
	public Boolean getHasMoved(){ return hasMoved; }
	
	public char getCharTeam() { return (boardTeam == Board.Team.BLACK)? 'b': 'w'; }
	
	public Board.Team GetBoardTeam() { return boardTeam; }
    
    public Type getType() { return pieceType; }
        
    public int getRowNum( ){ return rNum; }
    
    public int getColNum( ){ return cNum; }
    
	public char GetDisplayValue() { return displayValue; }
} // End of Pieces