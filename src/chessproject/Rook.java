package chessproject;
import java.util.ArrayList;
/* Rook 
	Implements the functionality for the Rook piece in chess. 

	COSC 3P71 - Chess Project
	Sarah Childs & Matt Grahlman 
	January 12, 2018 */
public class Rook extends Pieces {
	// Primary constructor
    public Rook(Board.Team team, int rNum, int cNum) {
    	super(team, Type.ROOK, rNum, cNum, 5);
    	hasMoved = false;
		displayValue = 'R';
    }
	
	// Used for reading in board configurations
	public Rook(Board.Team team, int rNum, int cNum, Boolean moved) {
    	super(team, Type.ROOK, rNum, cNum, 5);
    	hasMoved = moved;
		displayValue = 'R';
    }	
	
	// Generates a list of all possible actions the piece can take based on the current board configuration
	public ArrayList<AIAction> GenerateActions(Board board) {
		ArrayList<AIAction> actions = new ArrayList<>();
		
		// Check every row in the current column
		for(int row = 1; row <= 8; row++) {
			if(row == rNum) continue;
			if(checkMove(row, cNum, board)) actions.add(new AIAction(this, row, cNum));
		}
		// Check every col in the current row
		for(int col = 1; col <= 8; col++) {
			if(col == cNum) continue;
			if(checkMove(rNum, col, board)) actions.add(new AIAction(this, rNum, col));
		}
		
		return actions;
	}
	
	// Checks along the Vertical and Horizontal axis of the piece to determine if any legal moves can be made
	// Used for determining stalemates at the moment
	public Boolean AbleToMove(Board board) {
		// Check every row in the current column
		for(int row = 1; row <= 8; row++) {
			if(row == rNum) continue;
			if(checkMove(row, cNum, board)) return true;
		}
		// Check every col in the current row
		for(int col = 1; col <= 8; col++) {
			if(col == cNum) continue;
			if(checkMove(rNum, col, board)) return true;
		}
		return false;
	}

	// Checks along the X,Y axis to determine if the piece can move to the desired position 
    public Boolean checkMove(int destR, int destC, Board board, Boolean ignoreTeam, Boolean isLegal){
    	int diffX = Math.abs(rNum - destR);
    	int diffY = Math.abs(cNum - destC);    	
    	
    	if ((diffX > 0 && diffY == 0) || (diffX == 0 && diffY > 0)){
			// Look for pieces in the way
    		for(int i = 1; i <= ((Math.abs(rNum - destR)) -1); i++){   			
    			if(rNum > destR && board.pieceAt(rNum - i, cNum) == true) return false;	// Moving Upwards
				else if(rNum < destR && board.pieceAt(rNum + i, cNum) == true) return false; // Moving Downwards
				else if(cNum > destC && board.pieceAt(rNum, cNum - i) == true) return false; // Moving Right to Left
				else if(cNum < destC && board.pieceAt(rNum, cNum + i) == true) return false; // Moving Right to Left
    		}
    		if(!board.pieceAt(destR, destC)) return CheckLegality(destR, destC, board);		// When Not Taking Enemy Piece
    		else if (board.pieceAt(destR, destC) && (ignoreTeam || board.getPieceAt(destR, destC).GetBoardTeam() != boardTeam)){	// When Taking Enemy Piece		
				return CheckLegality(destR, destC, board);				
    		}
    	}
    	return false;  	
    }  
} // End of Rook