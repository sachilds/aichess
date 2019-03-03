package chessproject;
/* AIAction
	Designed to perform like a struct. It contains the information passed in the Minimax
	algorithm by the OpponentAI class. Holds the move position, the piece, and the evaluation
	score that the move potentially led to. 

	COSC 3P71 - Chess Project
	Sarah Childs & Matt Grahlman 
	January 12, 2018
*/
public class AIAction {
	public Pieces piece;
	public int destRow, destCol;
	public double evaluationValue;
	
	// Allows for a basic Action only containing a value
	public AIAction(double val) {
		piece = null;
		destRow = -1;
		destCol = -1;
		evaluationValue = val;
	}
	
	// Primary Constructor
	public AIAction(Pieces p, int destR, int destC) {
		piece = p;
		destRow = destR;
		destCol = destC;
		evaluationValue = 0;
	}
	
} // End of AIAction
