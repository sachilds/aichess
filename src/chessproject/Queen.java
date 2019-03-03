package chessproject;
import java.util.ArrayList;
/* Queen 
	Implements the functionality for the Queen piece in chess. 

	COSC 3P71 - Chess Project
	Sarah Childs & Matt Grahlman 
	January 12, 2018 */
public class Queen extends Pieces {
    public Queen(Board.Team team, int rNum, int cNum) {
    	super(team, Type.QUEEN, rNum, cNum, 9);
		displayValue = 'Q';
    }
	
	// Generates a list of all possible actions the piece can take based on the current board configuration
	@Override
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
		// Checks the outward diagonal in all 4 directions (check move handles if its out of bounds)
		for(int i = 1; i <= 8; i++) {
			if(checkMove(rNum-i, cNum-i, board)) actions.add(new AIAction(this, rNum-i, cNum-i));
			if(checkMove(rNum-i, cNum+i, board)) actions.add(new AIAction(this, rNum-i, cNum+i));
			if(checkMove(rNum+i, cNum-i, board)) actions.add(new AIAction(this, rNum+i, cNum-i));
			if(checkMove(rNum+i, cNum+i, board)) actions.add(new AIAction(this, rNum+i, cNum+i));
		}
		return actions;
	}
	
	@Override
	// Used to determine if the piece is capable of making any legal moves
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
		// Checks the outward diagonal in all 4 directions (check move handles if its out of bounds)
		for(int i = 1; i <= 8; i++) {
			if(checkMove(rNum-i, cNum-i, board)) return true;
			else if(checkMove(rNum-i, cNum+i, board)) return true;
			else if(checkMove(rNum+i, cNum-i, board)) return true;
			else if(checkMove(rNum+i, cNum+i, board)) return true;
		}
		return false;
	}

	// Determines if the desired move is possible and does not check the king
	@Override
    public Boolean checkMove(int destR, int destC, Board board, Boolean ignoreTeam, Boolean isLegal){
		if(destR > 8 || destR < 1 || destC > 8 || destC < 1) return false; // keeps within board limits
    	int diffX = Math.abs(rNum - destR);
    	int diffY = Math.abs(cNum - destC);    	
    	
    	// Checks along the X,Y axis    	
    	if ((diffX > 0 && diffY == 0) || (diffX == 0 && diffY > 0)){
    		for(int i = 1; i <= ((Math.max(diffX, diffY)) -1); i++){   			
    			if(rNum > destR && board.pieceAt(rNum - i, cNum)) return false;	// Moving Upwards
				else if(rNum < destR && board.pieceAt(rNum + i, cNum)) return false; // Moving Downwards
				else if(cNum > destC && board.pieceAt(rNum, cNum - i)) return false; // Moving Right to Left
				else if(cNum < destC && board.pieceAt(rNum, cNum + i)) return false; // Moving Left to Right
    		}
    		if(!board.pieceAt(destR, destC)) return (isLegal)? CheckLegality(destR, destC, board) : true;		// When Not Taking Enemy Piece
    		else if (board.pieceAt(destR, destC) && (ignoreTeam || board.getPieceAt(destR, destC).GetBoardTeam() != boardTeam)){	// When Taking Enemy Piece		
				return (isLegal)? CheckLegality(destR, destC, board) : true;	  			
    		}
    	} // Checks along the diagonal
       	else if(diffX == diffY && diffX != 0){
    		// Checks All Diagonal Paths to the Destination to See if There's a Piece Blocking the Path
    		for(int i = 1; i <= (Math.abs(rNum - destR) - 1); i++){
				if(rNum > destR && cNum > destC && board.pieceAt(rNum - i, cNum - i)) return false;  // Diagonal Up to the Left
				else if(rNum < destR && cNum < destC && board.pieceAt(rNum + i, cNum + i)) return false; // Diagonal Down to the Right
				else if(rNum > destR && cNum < destC && board.pieceAt(rNum - i, cNum + i)) return false; // Diagonal Up to the Right
				else if(rNum < destR && cNum > destC && board.pieceAt(rNum + i, cNum - i)) return false; // Diagonal Down to the Left
    		}
    		
    		// Case 1: Moving to an Empty Space
    		if(!board.pieceAt(destR, destC)) return (isLegal)? CheckLegality(destR, destC, board) : true;	
    		// Case 2: Taking an Enemy Piece
    		else if (board.pieceAt(destR, destC) && (ignoreTeam || board.getPieceAt(destR, destC).GetBoardTeam() != boardTeam)){		
				return (isLegal)? CheckLegality(destR, destC, board) : true;		
    		}	
    	} 	
    	return false;  	
    }  
} // End of Queen