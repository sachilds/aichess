package chessproject;
import java.util.ArrayList;
/* Horse / Knight
	Implements the functionality for the Knight piece in chess. 

	COSC 3P71 - Chess Project
	Sarah Childs & Matt Grahlman 
	January 12, 2018 */
public class Horse extends Pieces {
    public Horse(Board.Team team, int rNum, int cNum) {
    	super(team, Type.KNIGHT, rNum, cNum, 3);
		displayValue = 'H';
    }

	// Based on the position of the piece in the board how many squares can it attack?
	public int GetAttackableCount() {
		int count = 0;
		// Upper squares 
		if(rNum-1 >= 1) {
			if(cNum-2 >= 1) count++;
			if(cNum+2 <= 8) count++;
			if(rNum-2 >= 1) {
				if(cNum-1 >= 1) count++;
				if(cNum+1 <= 8) count++;
			}
		} // Lower squares
		if(rNum+1 <= 8) {
			if(cNum-2 >= 1) count++;
			if(cNum+2 <= 8) count++;
			if(rNum+2 <= 8) {
				if(cNum-1 >= 1) count++;
				if(cNum+1 <= 8) count++;
			}
		}
		return count;
	}
	
	@Override
	// Generates a list of all possible actions the piece can take based on the current board configuration
	public ArrayList<AIAction> GenerateActions(Board board) {
		ArrayList<AIAction> actions = new ArrayList<>();
		// Up left movements
		if(checkMove(rNum-2, cNum-1, board)) actions.add(new AIAction(this, rNum-2, cNum-1)); 
		if(checkMove(rNum-1, cNum-2, board)) actions.add(new AIAction(this, rNum-1, cNum-2));
		// Up right movements
		if(checkMove(rNum-2, cNum+1, board)) actions.add(new AIAction(this, rNum-2, cNum+1));
		if(checkMove(rNum-1, cNum+2, board)) actions.add(new AIAction(this, rNum-1, cNum+2));
		// Down left movements
		if(checkMove(rNum+2, cNum-1, board)) actions.add(new AIAction(this, rNum+2, cNum-1)); 
		if(checkMove(rNum+1, cNum-2, board)) actions.add(new AIAction(this, rNum+1, cNum-2));
		// Down right movements
		if(checkMove(rNum+2, cNum+1, board)) actions.add(new AIAction(this, rNum+2, cNum+1));
		if(checkMove(rNum+1, cNum+2, board)) actions.add(new AIAction(this, rNum+1, cNum+2));
		return actions;
	}
	
	// Checks if the knight is able to move to any of the specific spots around the piece
	@Override
	public Boolean AbleToMove(Board board) {
		// Up left movements
		if(checkMove(rNum-2, cNum-1, board)) return true; 
		else if(checkMove(rNum-1, cNum-2, board)) return true;
		// Up right movements
		else if(checkMove(rNum-2, cNum+1, board)) return true; 
		else if(checkMove(rNum-1, cNum+2, board)) return true;
		// Down left movements
		else if(checkMove(rNum+2, cNum-1, board)) return true; 
		else if(checkMove(rNum+1, cNum-2, board)) return true;
		// Down right movements
		else if(checkMove(rNum+2, cNum+1, board)) return true; 
		else if(checkMove(rNum+1, cNum+2, board)) return true;
		return false;
	}
	
	// Checks if the desired position if possible to move to, if there is something in the way, or if that move will CHECK the king
	@Override
    public Boolean checkMove(int destR, int destC, Board board, Boolean ignoreTeam, Boolean isLegal){
		if(destR > 8 || destR < 1 || destC > 8 || destC < 1) return false; // keeps within board limits
    	int diffX = Math.abs(rNum - destR);
    	int diffY = Math.abs(cNum - destC);    	
    	if((diffX == 2 && diffY == 1) || (diffX == 1 && diffY == 2)){
    		// Case 1: Moving to an Empty Space
    		if(!board.pieceAt(destR, destC)) return (isLegal)? CheckLegality(destR, destC, board) : true;	
    		// Case 2: Moving to a Space with an Enemy Piece
    		else if(board.pieceAt(destR, destC) && (ignoreTeam || board.getPieceAt(destR, destC).GetBoardTeam() != boardTeam))		
				return (isLegal)? CheckLegality(destR, destC, board) : true;	
    	}
    	return false;
    }    
} // End of Knight/Horse