package chessproject;
import java.util.ArrayList;
/* 
	COSC 3P71 - Chess Project
	Sarah Childs & Matt Grahlman 
	January 12, 2018 */
public class Pawn extends Pieces {	
	// Primary constructor
    public Pawn(Board.Team team, int rNum, int cNum) {
    	super(team, Type.PAWN, rNum, cNum, 1);
    	hasMoved = false;
		displayValue = 'P';
    }	
    
	// Constructor used for custom board configurations
	public Pawn(Board.Team team, int rNum, int cNum, Boolean moved) {
    	super(team, Type.PAWN, rNum, cNum, 1);
    	hasMoved = moved;
		displayValue = 'P';
    }  
	
	@Override
	// Generates a list of all possible actions the piece can take based on the current board configuration
	public ArrayList<AIAction> GenerateActions(Board board) {
		ArrayList<AIAction> actions = new ArrayList<>();
		
		int dir = (boardTeam == Board.Team.BLACK)? 1 : -1; // Which way is the pawn moving on the board
		// If it hasn't moved check the 2 spaces in front
		if(!hasMoved && checkMove(rNum+(2*dir), cNum, board))
			actions.add(new AIAction(this, rNum+(2*dir), cNum));
		
		if(checkMove(rNum+dir, cNum, board)) actions.add(new AIAction(this, rNum+dir, cNum)); // In front
		if(checkMove(rNum+dir, cNum+1, board)) actions.add(new AIAction(this, rNum+dir, cNum+1)); // Right diagonal space
		if(checkMove(rNum+dir, cNum-1, board)) actions.add(new AIAction(this, rNum+dir, cNum-1)); // Left diagonal space
		
		return actions;
	}
	
	// Uses the movements of the pawn to determine if there is a space it can move to
	@Override
	public Boolean AbleToMove(Board board) {
		int dir = (boardTeam == Board.Team.BLACK)? 1 : -1; // Which way is the pawn moving on the board
		// If it hasn't moved check the 2 spaces in front
		if(!hasMoved && checkMove(rNum+(2*dir), cNum, board))
			return true;
		
		if(checkMove(rNum+dir, cNum, board)) return true; // In front
		else if(checkMove(rNum+dir, cNum+1, board)) return true; // Right diagonal space
		else if(checkMove(rNum+dir, cNum-1, board)) return true; // Left diagonal space
		
		return false;
	}
	
	// Determines if the desired move is possible and does not check the king
	@Override
    public Boolean checkMove(int destR, int destC, Board board, Boolean ignoreTeam, Boolean isLegal){
		if(destR > 8 || destR < 1 || destC > 8 || destC < 1) return false; // keeps within board limits
    	int diffX = rNum - destR;
    	int diffY = cNum - destC;    	
    	
		// Case 1: Moving Pawn Forward 1 Space
		if(Math.abs(diffX) == 1 && diffY == 0 && !board.pieceAt(destR, destC)){
			if ((diffX == -1 && boardTeam == Board.Team.BLACK) || (diffX == 1 && boardTeam == Board.Team.WHITE)){
				return (isLegal)? CheckLegality(destR, destC, board) : true;		
			}
		}
		// Case 2: Moving Pawn Forward 2 Spaces
		else if(!hasMoved && Math.abs(diffX) == 2 && diffY == 0){
			if(!board.pieceAt(destR, destC)) {
				if((boardTeam == Board.Team.WHITE && !board.pieceAt(rNum - diffX + 1, cNum)) || boardTeam == Board.Team.BLACK) {
					return (isLegal)? CheckLegality(destR, destC, board) : true;	
				}
			}
		}
		// Case 3: Attacking Enemy Piece Diagonal From Pawn (& Only 1 Row and 1 Column Away)
		else if(Math.abs(diffX) == 1 && Math.abs(diffY) == 1 && board.pieceAt(destR, destC)){
			if ((diffX == -1 && boardTeam == Board.Team.BLACK) || (diffX == 1 && boardTeam == Board.Team.WHITE)){
				if(ignoreTeam || board.getPieceAt(destR, destC).GetBoardTeam() != GetBoardTeam()) {
					return (isLegal)? CheckLegality(destR, destC, board) : true;	
				}
			}
		}	
		// Case 4: Handles En Passant	
		else if(ChessProject.enPassant != null && board.pieceAt(rNum, cNum - diffY) && Math.abs(diffX) == 1 && Math.abs(diffY) == 1){
			//System.out.println("Starting En Passant: ");
			if(boardTeam == Board.Team.BLACK && diffX == -1 && board.getPieceAt(rNum, cNum - diffY).GetBoardTeam() == Board.Team.WHITE){
				//System.out.println("Do En Passant ");
				return (isLegal)? CheckLegality(destR, destC, board) : true;	
			}
			else if(boardTeam == Board.Team.WHITE && diffX == 1 && board.getPieceAt(rNum, cNum - diffY).GetBoardTeam() == Board.Team.BLACK){
				//System.out.println("Do En Passant ");
				return (isLegal)? CheckLegality(destR, destC, board) : true;	
			}
		}
    	return false;
    }
} // End of Pawn