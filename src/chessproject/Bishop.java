package chessproject;
import java.util.ArrayList;
/* Bishop
	Implementation for the Bishop piece and how it interacts with the board

	COSC 3P71 - Chess Project
	Sarah Childs & Matt Grahlman 
	January 12, 2018
*/
public class Bishop extends Pieces {
    public Bishop(Board.Team team, int rNum, int cNum) {
    	super(team, Type.BISHOP, rNum, cNum, 3);
		displayValue = 'B';
    }
	
	// Returns the number of spaces that piece can currently attack - Used to influence "piece development" 
	public int GetAttackableCount() {
		int count = 0;
		for(int i = 1; i <= 8; i++) {
			// Can't merge the ifs otherwise it wouldn't count if cNum-i and cNum+i were both available
			if(rNum-i >= 1 && cNum-i >= 1) count++;
			if(rNum-i >= 1 && cNum+i <= 8) count++;
			if(rNum+i <= 8 && cNum-i >= 1) count++;
			if(rNum+i <= 8&& cNum+i <= 8) count++;
		}
		return count;
	}
	
	@Override
	// Generates a list of all possible actions the piece can take based on the current board configuration
	public ArrayList<AIAction> GenerateActions(Board board) {
		ArrayList<AIAction> actions = new ArrayList<>();
		
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
		// Checks the outward diagonal in all 4 directions (check move handles if its out of bounds)
		for(int i = 1; i <= 8; i++) {
			if(checkMove(rNum-i, cNum-i, board)) return true;
			else if(checkMove(rNum-i, cNum+i, board)) return true;
			else if(checkMove(rNum+i, cNum-i, board)) return true;
			else if(checkMove(rNum+i, cNum+i, board)) return true;
		}
		return false;
	}
	
	// Checks if the desired position if possible to move to, if there is something in the way, or if that move will CHECK the king
	@Override
    public Boolean checkMove(int destR, int destC, Board board, Boolean ignoreTeam, Boolean isLegal){
		if(destR > 8 || destR < 1 || destC > 8 || destC < 1) return false; // keeps within board limits
    	int diffX = Math.abs(rNum - destR);
    	int diffY = Math.abs(cNum - destC);    	
    	
		
    	if(diffX == diffY && diffX != 0){
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
} // End of Bishop