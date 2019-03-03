package chessproject;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;
/* Board 
	Handles the representation of the board configuration and other methods 
	that operate on the board

	COSC 3P71 - Chess Project
	Sarah Childs & Matt Grahlman 
	January 12, 2018 */
public class Board {
	public enum State { PLAY, CHECK, CHECKMATE, STALEMATE  };
	public enum Team { BLACK, WHITE }; // Prevents passing other characters when desiring the team 
	
	public static boolean loadFailed = false;
	public Pieces[][] board = new Pieces[8][8];
	public ArrayList<Pieces> whitePieces;
    public ArrayList<Pieces> blackPieces;
	
	private Team turn;
	private State currState;
	
	// Default Constructor - Initiliazes the Board to the Starting Configuration
    public Board() {
		turn = Team.WHITE;
		currState = State.PLAY;
		whitePieces = new ArrayList<>();
		blackPieces = new ArrayList<>();
    	initBoard();
    }

	// 'Copy constructor'
	public Board(Board b) {
		turn = b.turn;
		currState = b.currState;
		
		// Copy over the board details
		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {
				board[i][j] = b.board[i][j];
			}
		}
		
		// Arraylists have 'copy constructors' according to Javadocs apparently
		whitePieces = new ArrayList<>(b.whitePieces); 
		blackPieces = new ArrayList<>(b.blackPieces); 
	}
	
	public Board(File inputFile){
      	turn = Team.WHITE; 
		currState = State.PLAY;
		whitePieces = new ArrayList<>();
		blackPieces = new ArrayList<>();
      	int numWP = 0;
      	int numBP = 0;  	
      	try{
    	Scanner scanner = new Scanner(inputFile);
			while(scanner.hasNextLine()){
				String addPiece = scanner.next();
				char team = addPiece.charAt(0);
				Team bt = (team == 'b') ? Team.BLACK : Team.WHITE;
				char type = addPiece.charAt(1);
				int row = scanner.nextInt();
				int col = scanner.nextInt();
				
				if(type == 'P' || type == 'K' || type == 'R'){
					Boolean moved = scanner.nextBoolean();
					Pieces p = null;
				
					if(type == 'P' && bt == Team.WHITE){
						numWP = 0;
						for(Pieces wp : whitePieces){
							if(wp.getType() == Pieces.Type.PAWN) numWP++;
						}
						if(numWP < 8){
							p = new Pawn(bt, row, col, moved);
							whitePieces.add(p);
						}						
					}
					else if(type == 'P' && bt == Team.BLACK){
						numBP = 0;
						for(Pieces bp : blackPieces){
							if(bp.getType() == Pieces.Type.PAWN) numBP++;
						}
						if(numBP < 8){
							p = new Pawn(bt, row, col, moved);
							blackPieces.add(p);
						}						
					}
					else if(type == 'R'){ 
						p = new Rook(bt, row, col, moved);
						p.hasMoved = moved;

					}
					else if(type == 'K'){
						if(bt == Team.BLACK && row == 1 && col == 5)	p = new King(bt, row, col, moved);
						else if(team == 'b'){
							p = new King(bt, row, col, true);	
						}
						
						if(bt == Team.WHITE && row == 8 && col == 4)	p = new King(bt, row, col, moved);
						else if(bt == Team.WHITE){
							p = new King(bt, row, col, true);	
						}
					}
										
					if(team == 'w' && type != 'P'){
						Boolean hasKing = false;
						for(Pieces wp : whitePieces){
							if(type == 'K' && wp.getType() == Pieces.Type.KING) hasKing = true;
							if(wp.getColNum() == col && wp.getRowNum() == row) hasKing = true;
						}
						if(!hasKing) whitePieces.add(p);
					}
					else if(bt == Team.BLACK && type != 'P'){
						Boolean hasKing = false;
						for(Pieces bp : blackPieces){
							if(type == 'K' && bp.getType() == Pieces.Type.KING) hasKing = true;
							if(bp.getRowNum() == row && bp.getColNum() == col) blackPieces.remove(bp);
						}
						if(!hasKing) blackPieces.add(p);
					}
				}
				else{
					Pieces p = null;
					switch (type) {
						case 'H':
							p = new Horse(bt, row, col);
							break;
						case 'B':
							p = new Bishop(bt, row, col);
							break;
						case 'Q':
							p = new Queen(bt, row, col);
							break;
						default:
							break;
					}
			
					if(bt == Team.WHITE) whitePieces.add(p);
					else if(bt == Team.BLACK) blackPieces.add(p);		
				}	
			}
      	}
      	catch(Exception e){ 
			System.out.println("An expection was encountered when creating a board from the input file: " + e.getLocalizedMessage());
			loadFailed = true;
			return;
		}
		
		// Update the board 
		for (int a = 1; a <= 8; a++){
    		for (int b = 1; b <= 8; b++){
    			board[b-1][a-1] = null;
    		}
    	}   		
   			
   		for (Pieces p : blackPieces){
			board[p.getRowNum() - 1][p.getColNum() - 1] = p;
   		}
   		
   		for (Pieces p : whitePieces){   			
			board[p.getRowNum() - 1][p.getColNum() - 1] = p;
   		}	   		
	}
    
   /**********
	 * Board Operations
	 ***********/
   
	// Initializes the default board configuration
	public void initBoard() {
		// Initializing Board to Null Values 
		for (int a = 1; a <= 8; a++) {
			for (int b = 1; b <= 8; b++) {
				board[b - 1][a - 1] = null;
			}
		}
		Pieces p = null;
		for (int row = 1; row <= 8; row++) {
			for (int col = 1; col <= 8; col++) {
				switch (row) {
					case 2:
						p = new Pawn(Team.BLACK, row, col);
						blackPieces.add(p);
						break;
					case 7:
						p = new Pawn(Team.WHITE, row, col);
						whitePieces.add(p);
						break;
					case 1:
						switch (col) {
							case 1:
							case 8:
								p = new Rook(Team.BLACK, row, col);		// Placing Black Rooks
								break;
							case 2:
							case 7:
								p = new Horse(Team.BLACK, row, col);		// Placing Black Horsies
								break;
							case 3:
							case 6:
								p = new Bishop(Team.BLACK, row, col);		// Placing Black Bishop
								break;
							default:
								break;
						}
						if (col == 4) {
							p = new Queen(Team.BLACK, row, col);		// Placing Black Queen
						} else if (col == 5) {
							p = new King(Team.BLACK, row, col);		// Placing Black King
						}
						if (p != null) {
							blackPieces.add(p);
						}
						break;
					case 8:
						switch (col) {
							case 1:
							case 8:
								p = new Rook(Team.WHITE, row, col);		// Placing White Rooks
								break;
							case 2:
							case 7:
								p = new Horse(Team.WHITE, row, col);		// Placing White Horsies
								break;
							case 3:
							case 6:
								p = new Bishop(Team.WHITE, row, col);		// Placing White Bishop
								break;
							default:
								break;
						}
						if (col == 4) {
							p = new King(Team.WHITE, row, col);		// Placing White King
						} else if (col == 5) {
							p = new Queen(Team.WHITE, row, col);		// Placing White Queen
						}
						if (p != null) {
							whitePieces.add(p);
						}
						break;
					default:
						break;
				}
				board[row - 1][col - 1] = p;
				p = null;
			}
		}
	}
	
	// Prints the board to the console 
	public void printBoard( ){
    	System.out.println("Printing Board: ");
    	System.out.println("      1    2    3    4    5    6    7    8 ");
   		System.out.print("--------------------------------------------\n"); 		
  		
   		for (int j = 1; j <= 8; j++){ 	// COL
   		
			// Avoided switch bc it takes up a lot of space (Printing on paper)
   			if(j == 1) System.out.print("a" + "  ");
   			else if(j == 2) System.out.print("b" + "  ");   		
   			else if(j == 3) System.out.print("c" + "  "); 
    		else if(j == 4) System.out.print("d" + "  ");
   			else if(j == 5) System.out.print("e" + "  ");
   			else if(j == 6) System.out.print("f" + "  ");   			   		
   			else if(j == 7) System.out.print("g" + "  ");   			   		
   			else if(j == 8) System.out.print("h" + "  ");   			   		
 	
    		for (int i = 1; i <= 8; i++){		// ROW
    			if (board[j-1][i-1] != null){
    				System.out.print("| " + board[j-1][i-1].getCharTeam() + board[j-1][i-1].GetDisplayValue() + " "); 		
    			}
    			else {
    				System.out.print("|    ");
    			}
    		}
    		System.out.print("|\n--------------------------------------------\n");
   		}
    }
	
	// Exports the current board to "boardConfig.txt"
	public void exportBoard(){
		char c = ' ';
		
		System.out.println("\n------------------------------------------------------");
		System.out.println("Exporting 'boardConfig.txt' - Check the chessproject folder inside the src folder!");
		System.out.println("------------------------------------------------------");   
		
		try{
			PrintWriter pw = new PrintWriter("boardConfig.txt", "UTF-8");
			for(Pieces bp : blackPieces){				
				if(null != bp.getType()) switch (bp.getType()) {
					case PAWN:
						c = 'P';
						break;
					case ROOK:
						c = 'R';
						break;
					case KNIGHT:
						c = 'H';
						break;
					case BISHOP:
						c = 'B';
						break;
					case QUEEN:
						c = 'Q';
						break;
					case KING:
						c = 'K';
						break;
					default:
						break;
				}
					
				if( c == 'P' || c == 'R' || c == 'K') pw.println(bp.getCharTeam() + "" + c + " " + bp.getRowNum() + " " + bp.getColNum() + " " + bp.getHasMoved());
				else pw.println(bp.getCharTeam() + "" + c + " " + bp.getRowNum() + " " + bp.getColNum());
			}
			
			for(Pieces wp : whitePieces){
				if(wp.getType() == Pieces.Type.PAWN) c = 'P';
				else if(wp.getType() == Pieces.Type.ROOK) c = 'R';
				else if(wp.getType() == Pieces.Type.KNIGHT) c = 'H';
				else if(wp.getType() == Pieces.Type.BISHOP) c = 'B';
				else if(wp.getType() == Pieces.Type.QUEEN) c = 'Q';
				else if(wp.getType() == Pieces.Type.KING) c = 'K';
				
				if( c == 'P' || c == 'R' || c == 'K') pw.println(wp.getCharTeam() + "" + c + " " + wp.getRowNum() + " " + wp.getColNum() + " " + wp.getHasMoved());
				else pw.println(wp.getCharTeam() + "" + c + " " + wp.getRowNum() + " " + wp.getColNum());
			}
			
			pw.close();
			
		}
		catch(Exception e){ 
			System.out.println("An expection was encountered when exporting the board: " + e.getLocalizedMessage());
		}	
	}
    
	// Switches the turns between players
	public void EndTurn() {
		if(turn == Team.BLACK) turn = Team.WHITE;
		else turn = Team.BLACK;
	}
	
	// Returns the Piece at row/col
    public Pieces getPieceAt(int row, int col){
    	return board[row-1][col-1];
    }
    
    // Returns a Boolean Which Represents Whether or Not There is a Piece at row/col
    public Boolean pieceAt(int row, int col){    	
    	return (board[row-1][col-1] != null);
    }
    
	// To be used when officially changing the location of the piece on a board
	public void MovePiece(Pieces p, int row, int col) {
		MovePiece(p, row, col, true);
	}
	
	// To be used by the AI ONLY - allows for the piece to not mark the position as moved
	// This way AI can check the position without removing the ability to En Passant or Castle
	public void MovePiece(Pieces p, int row, int col, Boolean moved) {
		board[p.rNum-1][p.cNum-1] = null;
		board[row-1][col-1] = p;
		p.setPosition(row, col, moved);
	}
	
	// Removes the piece at the given coordinates from the board and lists
	public Pieces RemovePiece(int row, int col) {
		if(!pieceAt(row, col)) return null;
		// Remove from board
		Pieces p = board[row-1][col-1];
		board[row-1][col-1] = null;
		
		// Remove from lists
		if(p.boardTeam == Team.WHITE) {
			whitePieces.remove(p);
		} else 
			blackPieces.remove(p);
		
		return p;
	}
	
	// Checks if the desired move is legal without putting the king into check by temporarily trying the position
	public Boolean CheckLegalMove(Pieces p, int row, int col) {
		Pieces tmp = (pieceAt(row, col)? getPieceAt(row, col) : null);
		Boolean legalMove = true;
		int origRow = p.getRowNum();
		int origCol = p.getColNum();
		// Set the new position
		MovePiece(p, row, col);

		// Check for a check
		King tKing = GetKing(p.GetBoardTeam());
		if(IsCheck(tKing, p.GetBoardTeam()) != null)
			legalMove = false;
		
		MovePiece(p, origRow, origCol);
		if(tmp != null) MovePiece(tmp, tmp.getRowNum(), tmp.getColNum());
		
		return legalMove;
	}
	
	// Determines if the current state is regular play, check, checkmate, or a stalemate
	public State UpdateCurrentState() {
		// Assume the game is in play unless other conditions are met
		currState = State.PLAY;
		
		King tKing = GetKing(((turn == Team.BLACK)? Team.WHITE : Team.BLACK)); // Select the opposing king
		Pieces threat = IsCheck(tKing.rNum, tKing.cNum, tKing.GetBoardTeam(), true);
		
		// If there is a check then need to check for Checkmate
		if(threat != null) {
			// Checkmate - Can the opposing king not move out of check somehow?
			currState = State.CHECK;
			
			// Run through each possible square the king can move to - Can it move there without staying/moving into check (Do through the King piece's CheckMove)
			for(int row = -1; row <= 1; row++) { // -1, 0, +1
				if(tKing.rNum+row < 1 || tKing.rNum+row > 8)
					continue;
				for(int col = -1; col <= 1; col++) {
					if(tKing.cNum+col < 1 || tKing.cNum+col > 8) continue;
					// If there is a position to move to where the king is NOT in check, then return the currState of CHECK
					if(tKing.checkMove(tKing.rNum+row, tKing.cNum+col, this)) { 
						return currState;
					}
				}
			}
			// If King can't move proceed to see if the threat is capturable
			// Check if any piece on Playing side can take the threat piece 
			if(tKing.boardTeam == Team.WHITE) {
				for (Pieces p : whitePieces) {
					if(p.checkMove(threat.rNum, threat.cNum, this))
						return currState;
				}
			} else {
				for(Pieces p : blackPieces) {
					if(p.checkMove(threat.rNum, threat.cNum, this))
						return currState;
				}
			}
			
			// Guarding - Can a piece move to protect the king?
			// If the piece is a knight it's checkmate (it can jump over other pieces) (Pawn would be captured, king would be in check, and knight would be checkmate)
			if(threat.getType() == Pieces.Type.KNIGHT) {
				currState = State.CHECKMATE;
				return currState;
			} else if(threat.getType() == Pieces.Type.KING) {
				currState = State.STALEMATE;
				return currState;
			}
			
			// Rooks + Queen (X,Y) 
			if(threat.getType() == Pieces.Type.ROOK || threat.getType() == Pieces.Type.QUEEN) {
				// Get the common row/col & from the rooks pos to the kings pos can any piece move there?
				Boolean sameCol = false;
				if(threat.cNum == tKing.cNum) sameCol = true; // Are they the same column
				
				if(sameCol) {
					// Check along the column
					int diff = Math.abs(threat.cNum - tKing.cNum); // How many spaces are in between
					int direction = ((int) Math.signum(tKing.cNum - threat.cNum)); // Which direction is the threat moving in to get to king (invert)
					
					ArrayList<Pieces> tmpPieces = (turn == Team.BLACK)? blackPieces : whitePieces;
					for(int rowCount = 1; rowCount < diff; rowCount++) {
						for(Pieces p : tmpPieces) {
							if(p.checkMove(threat.rNum+(rowCount*direction), threat.cNum, this))
								return currState;
						}
					}
				} else {
					// Check along the row
					int diff = Math.abs(threat.rNum - tKing.rNum); // How many spaces between 
					int direction = ((int)Math.signum(tKing.cNum - threat.cNum));
					
					ArrayList<Pieces> tmpPieces = (turn == Team.BLACK)? blackPieces : whitePieces;
					for(int colCount = 1; colCount < diff; colCount++) {
						for(Pieces p : tmpPieces) {
							if(p.checkMove(threat.rNum, threat.cNum+(colCount*direction), this))
								return currState;
						}
					}
				}
			} 
				
			// Bioshops + Queen(Diagonal) 
			if(threat.getType() == Pieces.Type.BISHOP || threat.getType() == Pieces.Type.QUEEN) {
				// Get the threats position, get direction (norm vector) towards the king, check each square along the vector from bioshop to king if an ally piece can move there
				int rowDir = ((int)Math.signum(threat.rNum - tKing.rNum)) * -1;
				int colDir = ((int) Math.signum(threat.cNum - tKing.cNum)) * -1;
				
				int diff = Math.abs(threat.rNum - tKing.rNum); // How many spaces between, can check via row or col - diagonals must be equal
				ArrayList<Pieces> tmpPieces = (turn == Team.BLACK)? blackPieces : whitePieces;
				for(int spaceDiff = 1; spaceDiff < diff; spaceDiff++) {
					for(Pieces p : tmpPieces) {
						if(p.checkMove(threat.rNum+(spaceDiff*rowDir), threat.cNum+(spaceDiff * colDir), this))
							return currState;
					}
				}
			}
			// If it could not return through Moving/Capture/Guarding then the side is in Checkmate
			currState = State.CHECKMATE;
			return currState;
		}
		
		// Stalemate - When no legal moves can be taken by a side 
		// Will check for a stalemate of the following turn (can the next player make a legal move?)
		if(turn == Team.WHITE) {
			// Check if black side is in stalemate
			for(Pieces p : blackPieces) {
				if(p.AbleToMove(this)) 
					return currState;
			}
			currState = State.STALEMATE;
		} else {
			// Check if white side is in stalemate - If able to move then not in stalemate
			for(Pieces p : whitePieces) {
				if(p.AbleToMove(this))
					return currState;
			}
			currState = State.STALEMATE;
		}
		return currState;
	}
	
	// Determines if the specified king is in check - does NOT ignore the team of pieces
	public Pieces IsCheck(King tKing, Team team) {
		return IsCheck(tKing.rNum, tKing.cNum, team, false);
	}
	
	// Determines if the specified king is in check - allows for ignoring the team of pieces
	public Pieces IsCheck(int destRow, int destCol, Team team, Boolean ignoreTeam) {
		Boolean isLegal = false; // We don't want subsequent calls to IsCheck - Weird loop
		// Perform checkMove on that position for all pieces on the opposing side
		if (team == Team.WHITE) {
			for (int i = 0; i < blackPieces.size(); i++) {
				if (blackPieces.get(i).checkMove(destRow, destCol, this, ignoreTeam, isLegal))
					return blackPieces.get(i);
			}
		} else {
			for (int i = 0; i < whitePieces.size(); i++) {
				if (whitePieces.get(i).checkMove(destRow, destCol, this, ignoreTeam, isLegal))
					return whitePieces.get(i);
			}
		}
		return null;
	}
	
	/**********
	 * Getters
	 ***********/
	// Returns the current turn
	public Team GetTurn() {
		return turn;
	}
	
	
	// Returns the current state of the board
	public State GetCurrentState() {
		return currState;
	}

	// Returns the king of the specified team
	private King GetKing(Team team) {
		King tKing = null;		
		
		// Find the king for the desired team
		if (team == Team.BLACK) {
			for (int i = 0; i < blackPieces.size(); i++) {
				if (blackPieces.get(i).pieceType == Pieces.Type.KING) {
					tKing = (King) blackPieces.get(i);
					break;
				}
			}
		} else {
			for (int i = 0; i < whitePieces.size(); i++) {
				if (whitePieces.get(i).pieceType == Pieces.Type.KING) {
					tKing = (King) whitePieces.get(i);
					break;
				}
			}
		}
		if (tKing == null) { 
			System.out.println("Unable to find the king");
			return null;
		}
		return tKing;
	}	
} // End of Board