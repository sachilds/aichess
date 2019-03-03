package chessproject;
import java.util.ArrayList;
/* King
	Implements the functionality for the King piece in chess. 

	COSC 3P71 - Chess Project
	Sarah Childs & Matt Grahlman 
	January 12, 2018 */
public class King extends Pieces {
	// Primary constructor
    public King(Board.Team team, int rNum, int cNum) {
    	super(team, Type.KING, rNum, cNum, 9999);
		hasMoved = false;
		displayValue = 'K';
    }
	
	// Constructor used for custom boards
	public King(Board.Team team, int rNum, int cNum, Boolean moved) {
    	super(team, Type.KING, rNum, cNum, 9999);
		hasMoved = moved;
		displayValue = 'K';
    }
	
	@Override
	// Generates a list of all possible actions the piece can take based on the current board configuration
	public ArrayList<AIAction> GenerateActions(Board board) {
		ArrayList<AIAction> actions = new ArrayList<>();
		
		for(int r = -1; r <= 1; r++) {
			for(int c = -1; c <= 1; c++) {
				// Prevent it from checking its own space
				if(r == rNum && c == cNum) continue;
				if(checkMove(rNum+r, cNum+c, board)) actions.add(new AIAction(this, rNum+r, cNum+c));
			}
		}
		
		return actions;
	}
	
	// Checks all spaces in the 1 move vicinity of the King piece to determine if movement is possible
	@Override
	public Boolean AbleToMove(Board board) {
		for(int r = -1; r <= 1; r++) {
			for(int c = -1; c <= 1; c++) {
				// Prevent it from checking its own space
				if(r == rNum && c == cNum) continue;
				if(checkMove(rNum+r, cNum+c, board)) return true;
			}
		}
		return false;
	}

	// Checks that the desired move is possible and doesn't check the king, also includes cases for castling
	@Override
	public Boolean checkMove(int destR, int destC, Board board, Boolean ignoreTeam, Boolean isLegal) {
		if(destR > 8 || destR < 1 || destC > 8 || destC < 1) return false; // keeps within board limits
		int diffX = Math.abs(this.getRowNum() - destR);
    	int diffY = Math.abs(this.getColNum() - destC);
		int tempY = this.getColNum() - destC;
    	
    	if((diffX == 1 && diffY == 0) || (diffX == 0 && diffY == 1) || (diffX == 1 && diffY == 1)){			
    		if(!board.pieceAt(destR, destC) ||
					(board.pieceAt(destR, destC) && (ignoreTeam || board.getPieceAt(destR, destC).GetBoardTeam() != boardTeam))) {
				// Can the king move there without being put into check/staying in a check
				return board.IsCheck(destR, destC, boardTeam, false) == null;
			}
    	}

		// Castling
    	else if(!hasMoved && diffX == 0 && Math.abs(tempY) == 2){
    		Pieces rook = null;
    		if(boardTeam == Board.Team.BLACK){
    			// Black Team Queen Side Castling
    			if(tempY == 2){
    				for(Pieces p: board.blackPieces){
    					if(p.getRowNum() == 1 && p.getColNum() == 1 && p.getType() == Type.ROOK && p.GetBoardTeam() == Board.Team.BLACK){
    						 rook = new Rook(p.GetBoardTeam(), p.getRowNum(), p.getColNum());
    						 break;
    					}
    				}
    				if(rook != null && (!rook.hasMoved && !board.pieceAt(1,2) && !board.pieceAt(1,3) && !board.pieceAt(1,4))) return true;	 	
    			}
    			// Black Team King Side Castling
    			else if(tempY == -2){
    				for(Pieces p: board.blackPieces){
    					if(p.getRowNum() == 1 && p.getColNum() == 8 && p.getType() == Type.ROOK && p.GetBoardTeam() == Board.Team.BLACK){
    						 rook = new Rook(p.GetBoardTeam(), p.getRowNum(), p.getColNum());
    						 break;
    					}
    				}
    				if(rook != null && (!rook.hasMoved && !board.pieceAt(1,6) && !board.pieceAt(1,7))) return true;    				
    			}
    		}
    		else if(boardTeam == Board.Team.WHITE){
    			// White Team King Side Castling
    			if(tempY == 2){
    			    for(Pieces p: board.whitePieces){
    					if(p.getRowNum() == 1 && p.getColNum() == 1 && p.getType() == Type.ROOK && p.GetBoardTeam() == Board.Team.WHITE){
    						 rook = new Rook(p.GetBoardTeam(), p.getRowNum(), p.getColNum());
    						 break;
    					}
    				}
    				if(rook != null && (!rook.hasMoved && !board.pieceAt(8,2) && !board.pieceAt(8,3))) return true;    				
    			}
    			// White Team Queen Side Castling
    			else if(tempY == -2){
    				for(Pieces p: board.whitePieces){
    					if(p.getRowNum() == 1 && p.getColNum() == 1 && p.getType() == Type.ROOK && p.GetBoardTeam() == Board.Team.WHITE){
    						 rook = new Rook(p.GetBoardTeam(), p.getRowNum(), p.getColNum());
    						 if(!rook.hasMoved) return true;	  						 
    						 break;
    					}
    				}
    				if(rook != null && (!rook.hasMoved && !board.pieceAt(8,5) && !board.pieceAt(8,6) && !board.pieceAt(8,7))) return true;    				    				
    			}
    		}
    	}	    	
    	return false;
	}     
} // End of King