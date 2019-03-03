package chessproject;
import java.util.ArrayList;
/* OpponentAI
	Handles the operation of the AI Opponent. Uses the Minimax algorithm to gain insight into
	potential next moves depending on the specified depth given at the start of the game. 
	Also handles the evaluation of the board based on certain criteria (Refer to the Report please)

	COSC 3P71 - Chess Project
	Sarah Childs & Matt Grahlman 
	January 12, 2018
*/
public class OpponentAI {
	private Board.Team team;
	private final int plyDepth; // Difficulty level of AI - How far in the future it searchs
	
	// Constructor
	public OpponentAI(Board.Team team, int difficulty) {
		this.team = team;
		plyDepth = difficulty;
	}

	// Implementation of the Alpha-Beta MiniMax algorithm
	public AIAction ChooseMove(Board currBoard) {
		// Assess the current board via Minimax 
		Board tmpBoard = new Board(currBoard); // So no large changes occur to the current board
		AIAction chosenMove = MaxValue(tmpBoard, -Double.MAX_VALUE, Double.MAX_VALUE, null, 0);
		System.out.println("Chosen move was: " + chosenMove.piece.getType() + " to " + chosenMove.destRow + ", " + chosenMove.destCol);
		return chosenMove;
	}
	
	// Performs the MaxValue side of the algorithm, looks at all choices MinValue made and chooses the biggest
	// Or breaks in the case that Min's choice is < the current largest value - Pruning
	private AIAction MaxValue(Board state, double alpha, double beta, AIAction performed, int depth) {
		if(Cutoff(state, depth)) { 
			// Update the performed action's eval value 
			performed.evaluationValue = Evaluate(state);
			return performed;
		}
		int origRow, origCol;
		Pieces removed = null;
		AIAction tmpHolder;
		AIAction val = new AIAction(-Double.MAX_VALUE);
		ArrayList<AIAction> actions = GenerateActions(state);
		for(AIAction a : actions) {
			// Hold the original position
			origRow = a.piece.getRowNum();
			origCol = a.piece.getColNum();
			
			// Update the board with the action
			removed = state.RemovePiece(a.destRow, a.destCol); // Remove any pieces that might be there
			state.MovePiece(a.piece, a.destRow, a.destCol, false);
			state.EndTurn(); // Forces the check for the other opponent
			
			// Pass it along recursively
			tmpHolder = MinValue(new Board(state), alpha, beta, a, depth+1);
			
			// If the returned action has a better overall evaluation than the current best - replace it with CURRENT ACTION
			if(tmpHolder.evaluationValue > val.evaluationValue) {
				val = new AIAction(a.piece, a.destRow, a.destCol);
				val.evaluationValue = tmpHolder.evaluationValue;
			}
			
			// Return board state to what it was
			state.MovePiece(a.piece, origRow, origCol, false);
			if(removed != null) state.MovePiece(removed, a.destRow, a.destCol, false); // Replace the removed piece
			state.EndTurn(); // Should return the board to the current turn state
			
			// Prune in the case that val is larger than beta
			if (val.evaluationValue >= beta)
				return val;
			
			// Update alpha in case Max found a better target
			alpha = (val.evaluationValue > alpha)? val.evaluationValue : alpha;
		}
		return val;		
	}
	
	// Performed the recursive depth search using the MinValue pseudo code design 
	private AIAction MinValue(Board state, double alpha, double beta, AIAction performed, int depth) {
		if(Cutoff(state, depth)) { 
			// Update the performed action's eval value 
			performed.evaluationValue = Evaluate(state);
			return performed;
		}
		int origRow, origCol;
		Pieces removed = null;
		AIAction tmpHolder;
		AIAction val = new AIAction(Double.MAX_VALUE);
		ArrayList<AIAction> actions = GenerateActions(state);
		
		for(AIAction a : actions) {
			// Hold the original position
			origRow = a.piece.getRowNum();
			origCol = a.piece.getColNum();
			
			// Update the board with the action
			removed = state.RemovePiece(a.destRow, a.destCol); // Remove any pieces that might be there
			state.MovePiece(a.piece, a.destRow, a.destCol, false);
			state.EndTurn(); // Forces the check for the other opponent
			
			// Pass it along recursively
			tmpHolder = MaxValue(new Board(state), alpha, beta, a, depth+1);
			
			// If the tmp action is smaller than the current best update the best with the CURRENT ACTION
			if(tmpHolder.evaluationValue < val.evaluationValue) {
				val = new AIAction(a.piece, a.destRow, a.destCol);
				val.evaluationValue = tmpHolder.evaluationValue;
			}
			
			// Return board state to what it was
			state.MovePiece(a.piece, origRow, origCol, false);
			if(removed != null) state.MovePiece(removed, a.destRow, a.destCol, false); // Replace the removed piece
			state.EndTurn(); // Should return the board to the current turn state
			
			// Prunes in the case that val's eval is lower than the current largest number (Max won't choose it)
			if (val.evaluationValue <= alpha)
				return val;
			
			// Update the beta (min)
			beta = (val.evaluationValue < beta)? val.evaluationValue : beta; 
		}	
		return val;
	}
	
	// Tests for the terminal case and cutoff depth
	private Boolean Cutoff(Board state, int depth) {
		if(depth >= plyDepth) return true;
		state.UpdateCurrentState();
		switch (state.GetCurrentState()) {
			case CHECKMATE:
			case STALEMATE:
				return true;
			default:
				break;
		}
		return false;
	}
	
	// Evaluates the state of the board passed based on a set of criteria
    private double Evaluate(Board board) {
		double evaluationValue = 0;
		// Determine the Current State - Is it checkmate, check or stalemate?
		Board.State state = board.UpdateCurrentState();
		
		// Assign a value to the state
		switch(state) {
			case CHECK:
				evaluationValue += 25;
				break;
			case CHECKMATE: // Checkmate is the best possible situation so return it right away
				evaluationValue += Integer.MAX_VALUE;
				return evaluationValue;
			case STALEMATE: // Stalemate is good for the opponent
				evaluationValue -= 10;
				break;
			default: // During play it remains neutral
				break;
		}
		
		// Sum the weights of threatened and capturable pieces
		double threatenedWeight = 0;
		double capturableWeight = 0;
		double attackableCount = 0; // Used only for Bishop/Knight
		// Since AI plays as the Black team focus on that team for evaluation
		for(Pieces bp : board.blackPieces) {
			for(Pieces wp : board.whitePieces) {
				// Does the white piece threaten the AI?
				if(wp.checkMove(bp.getRowNum(), bp.getColNum(), board))
					threatenedWeight += bp.weight;
				
				// Can the AI capture the white piece?
				if(bp.checkMove(wp.getRowNum(), wp.getColNum(), board))
					capturableWeight += wp.weight;
			}
			
			// Prefers some piece development
			if(bp.getType() == Pieces.Type.BISHOP)
				attackableCount += ((Bishop)bp).GetAttackableCount() % bp.weight;
			else if(bp.getType() == Pieces.Type.KNIGHT)
				attackableCount += ((Horse)bp).GetAttackableCount() % bp.weight;
		}
		evaluationValue += attackableCount/3; 
		
		evaluationValue += capturableWeight*1.7 - threatenedWeight; // Added a multiplier to encourage capture
		
		// Power Squares in center
		double centerInfluence = 0;
		Pieces tmp = null;
		// Checks the four middle "power" squares to see which have stronger influence 
		for(int r = 4; r <= 5; r++) {
			for(int c = 4; c <= 5; c++) {
				if(board.pieceAt(r,c)) {
					tmp = board.getPieceAt(r,c);
					// Attempting to keep the queen from charging into the center
					if(tmp.GetBoardTeam() == Board.Team.BLACK) centerInfluence += (tmp.getType() == Pieces.Type.QUEEN) ? tmp.GetWeight()/2 : tmp.GetWeight();
					else centerInfluence -= tmp.GetWeight();
				}
			}
		}
		
		evaluationValue += centerInfluence*0.7; // Less influence for the center
		
		// The ratio of Black/White pieces -- attempt to keep more pieces on the board than the opponent
		evaluationValue *= ((board.blackPieces.size()*1.7) / board.whitePieces.size());
		return evaluationValue;
	}
	
	// Generates a list of possible moves and their corresponding piece that can be performed on the current board
	private ArrayList<AIAction> GenerateActions(Board board) {
		ArrayList<AIAction> actions = new ArrayList<>();
		// Based on whos turn in the board it is
		if(board.GetTurn() == Board.Team.WHITE) {
			for(Pieces p : board.whitePieces) {
				actions.addAll(p.GenerateActions(board));
			}
		} else {
			for(Pieces p : board.blackPieces) {
				actions.addAll(p.GenerateActions(board));
			}
		}
		return actions;
	}
	
} // End of OpponentAI
