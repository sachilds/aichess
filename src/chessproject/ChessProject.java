package chessproject;
import java.io.File;
import java.util.*;
import javax.swing.JFileChooser;
/* ChessProject
	Handles the main game loop of the program and allowing for the turns between the 2 players
	Holds the AI opponent and calls their movement options
	Handles the I/O for picking moves/help codes/etc.

	COSC 3P71 - Chess Project
	Sarah Childs & Matt Grahlman 
	January 12, 2018 */
public class ChessProject {
	private static Boolean InPlay;
	public static Board b;
    public static Pieces enPassant = null;
	private static OpponentAI opponent;
	Scanner scanner;
    
	// Constructor initializes and introduces the game
    public ChessProject() {		
		System.out.println("COSC 3P71 Final Chess Project: ");
		System.out.println("By: Sarah Childs & Matthew Grahlman.");
		System.out.println("------------------------------------------------------");
		System.out.println("Introduction: ");		
		System.out.println("For help & instructions on how to play our game, type 'help' at any time.");
		System.out.println("To export the current board configuration, type 'export' at any time.");
		System.out.println("To resign from the game, type 'end' at any time.");
		System.out.println("------------------------------------------------------");  	
    }
	
	// Main functionality and logic of the game
	public void Run() {
		Pieces p = null;
		ChessProject.InPlay = true;
		scanner = new Scanner(System.in);
		boolean moveMade = false;
		
		if(!gameSelect()){
			JFileChooser jfc = new JFileChooser();
			jfc.setCurrentDirectory(new File("."));
			System.out.println("------------------------------------------------------");
			System.out.println("Importing a Board Configuration from a File!");			
			System.out.println("A JFileSelector Window Has Opened! (If You Do Not See It, Wait a Moment & Then Hit ALT+TAB, Then Look For a Window Named 'Open')");	
			int returnVal = jfc.showOpenDialog(null);		
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File file = jfc.getSelectedFile();
				System.out.println("Loading Board...");
				b = new Board(file);
				if(Board.loadFailed)
					b = new Board();
			}
			System.out.println("------------------------------------------------------");						
    	}
    	else{
			System.out.println("------------------------------------------------------");
			System.out.println("Starting New Game!");
			System.out.println("------------------------------------------------------");    		
    		b = new Board();
    	}
		
		// Create the AI Opponent and set the difficulty (the ply depth)
		opponent = new OpponentAI(Board.Team.BLACK, setDifficulty());
		
    	b.printBoard();
    	
    	// While the game is marked as InPlay continue the turns - MAIN GAME LOOP
    	while (ChessProject.InPlay) {
    		if (b.GetTurn() == Board.Team.WHITE) System.out.println("\nWhite Team's Turn!: "); 
    		else System.out.println("\nBlack Team's Turn!: ");
 		   	System.out.println("-------------------------------"); 		   		
			
    		// Reading Input for the Piece to be Moved
    		while(true){
				if(b.GetTurn() == Board.Team.BLACK) break;
	 		   	System.out.println("Please Enter The Row Letter, then the Col # for the Piece you Wish to Move:");    	
    			int row = pickRow();
    			int col = pickCol();	
				p = b.getPieceAt(row, col);
    			if(p != null && (b.GetTurn() == p.GetBoardTeam())) break;
    			else System.out.println("Error: You Don't Have a Piece at Specified Location!");
    			row = -1;
    			col = -1;
    		}
    	
			
    		// Reading Input for Location to Move To
    		while(true){
				if(b.GetTurn() == Board.Team.BLACK) break;
	 		   	System.out.println("Please Enter The Row Letter, then the Col # for where you wish to move:"); 
	 		   	int destRow = pickRow();
	 		   	int destCol = pickCol();    	
    			if(p != null){ 			
    				// Piece Selected is a: Pawn, Rook, Horse, Bishop or Queen
    				if(p.checkMove(destRow, destCol, b)){
						moveMade = MovingPiece(p, destRow, destCol);
    					break;
    				}																
    			}
				// In the case that the piece entered was invalid or the move was invalid
   				System.out.print("Error: Invalid Move!");
			 	// Piece Reselection
			  	boolean newPiece = moveReselect();
			 	if (newPiece) break;		   	
    		}
    		
			// Allow for the AI to move
			if(b.GetTurn() == Board.Team.BLACK) {
				AIAction action = opponent.ChooseMove(b);
				moveMade = MovingPiece(action.piece, action.destRow, action.destCol);
			}
			
			if(moveMade) EndTurn();
      		p = null; 	
    	}
    	scanner.close(); 
	}
	
	
	// Handles the functionality of moving a piece and removing opponents, en passant, castling or piece promotion
	public Boolean MovingPiece(Pieces p, int destRow, int destCol) {
		int diff = p.getRowNum() - destRow;
		Boolean moveMade;
		
		// If We Are Taking Enemy Piece
		if (b.pieceAt(destRow, destCol)) {
			Pieces tmp = b.RemovePiece(destRow, destCol);
			System.out.println("A " + tmp.GetBoardTeam() + " " + tmp.getType() + " was captured");
		} // En Passant
		else if (!(b.pieceAt(destRow, destCol)) && b.pieceAt(destRow + diff, destCol) && enPassant != null && p.getType() == Pieces.Type.PAWN) {
			if (enPassant.getType() == Pieces.Type.PAWN && b.getPieceAt(destRow + diff, destCol) == enPassant) {
				System.out.println("A " + enPassant.GetBoardTeam() + " " + enPassant.getType() + " was captured");
				b.RemovePiece(enPassant.getRowNum(), enPassant.getColNum());
			}
		}// Castling
		else if (p.getType() == Pieces.Type.KING && p.GetBoardTeam() == Board.Team.BLACK) {
			if (destRow == 1 && destCol == 3)
				b.MovePiece(b.getPieceAt(1, 1), 1, 4);
			else if (destRow == 1 && destCol == 7)
				b.MovePiece(b.getPieceAt(1, 8), 1, 6);
		} else if (p.getType() == Pieces.Type.KING && p.GetBoardTeam() == Board.Team.WHITE) {
			if (destRow == 8 && destCol == 2)
				b.MovePiece(b.getPieceAt(8, 1), 8, 3);
			else if (destRow == 8 && destCol == 6)
				b.MovePiece(b.getPieceAt(8, 8), 8, 5);
		}
		
		b.MovePiece(p, destRow, destCol);
		moveMade = true;

		// Resets/Sets the enPassant variable
		if (p.getType() == Pieces.Type.PAWN && Math.abs(diff) == 2)
			enPassant = p;
		else
			enPassant = null;

		// Pawn Promotion
		if (p.getType() == Pieces.Type.PAWN && ((p.GetBoardTeam() == Board.Team.WHITE && p.getRowNum() == 1) || (p.GetBoardTeam() == Board.Team.BLACK && p.getRowNum() == 8))) {
			Pieces.Type selection = piecePromotion(p.GetBoardTeam());
			int tRow = p.getRowNum();
			int tCol = p.getColNum();
			Board.Team team = p.GetBoardTeam();

			b.RemovePiece(p.getRowNum(), p.getColNum());
			if (selection != null) {
				switch (selection) {
					case ROOK:
						p = new Rook(team, tRow, tCol);
						break;
					case KNIGHT:
						p = new Horse(team, tRow, tCol);
						break;
					case BISHOP:
						p = new Bishop(team, tRow, tCol);
						break;
					case QUEEN:
						p = new Queen(team, tRow, tCol);
						break;
					default:
						break;
				}
			}
			b.MovePiece(p, tRow, tCol);
			System.out.println("Pawn was promoted to a " + selection);
		}

		return moveMade;
	}
	
	// Ends the turn for the side, displaying the board and checking the current status of the play
	private void EndTurn() {
		b.printBoard();
		b.UpdateCurrentState();
		if(CheckEndGame()) {
			ChessProject.InPlay = false;
			System.out.println("The game is completed " + b.GetCurrentState() + " by " + b.GetTurn());
		} else {
			if(b.GetCurrentState() == Board.State.CHECK)
				System.out.println(b.GetTurn() + " is currently in CHECK");
			b.EndTurn();
		}
	}
	
	// Checks if the current state of the board is any end game states
	private Boolean CheckEndGame() {
		switch(b.GetCurrentState()) {
			case PLAY:
			case CHECK:
				return false;
			case CHECKMATE:
				System.out.println("A " + b.GetCurrentState() + " has been reached by " + b.GetTurn());
				return true;
			case STALEMATE:
				System.out.println((b.GetTurn() == Board.Team.WHITE? Board.Team.BLACK : Board.Team.WHITE) + " has been put into a " + b.GetCurrentState());
				return true;
			default:
				break;
		}
		return false;
	}
	
    // Allows User to Select a Different Piece to Move
    private Boolean moveReselect(){
   		String temp = " ";
    	int choice = -1;
    	for( ; ; ){
    		System.out.print(" Reselect Piece to Move? (Y = 0, N = 1): ");  					 			
    		try{	
    			temp = scanner.next();
    			scanner.nextLine();     			
    			if(temp.equals("help")) userHelp();
    			else if(Integer.valueOf(temp) == 1 || Integer.valueOf(temp) == 0){
					choice = Integer.valueOf(temp);    				 
    				break;
    			}
    			else System.out.print("Invalid Selection! ");
    		}
    		catch(Exception e){System.out.print("Invalid Selection! ");}	
    	}
    	
    	if (choice == 0) return true;
    	else return false;
    		
    }
    
	// Allows User to Select Between New Game and to Load an Existing Board Configuration
    private Boolean gameSelect(){
    	String temp = " ";
    	int choice = -1;
    	for( ; ; ){
   			System.out.print("Select an Option: (New Game=0, Import Board from File=1): ");    					 			
    		try{	
    			temp = scanner.next();
    			scanner.nextLine();     			
    			choice = Integer.parseInt(temp);
    			if(choice == 1 || choice == 0) break;
    			else System.out.print("Invalid Selection! ");
    		}
    		catch(Exception e){System.out.print("Invalid Selection! ");}	
    	}
    	
    	if (choice == 0) return true;
    	else return false;
    }
	
    // Allows User to Pick the Row of the Piece They Wish to Move
    private int pickRow( ){  	
    	String temp = " ";
    	int choice = -1;
    	for( ; ; ){
   			System.out.print("R: ");    					 			
    		try{	
    			temp = scanner.next();
    			scanner.nextLine();
    			if(temp.equals("a") || temp.equals("b") || temp.equals("c") || temp.equals("d") || temp.equals("e") || temp.equals("f") || temp.equals("g") || temp.equals("h")) break;  			
    			else if(temp.equals("help")) userHelp();
    			else if(temp.equals("end")) endGame();
    			else if(temp.equals("export")){    				
    				b.exportBoard();
    			}	    						
    			else System.out.print("Invalid Row #! ");
    		}
    		catch(Exception e){System.out.print("Invalid Row #! ");}	
    	}
    	
    	if(temp.equals("a")) return 1;
    	else if(temp.equals("b")) return 2;
    	else if(temp.equals("c")) return 3;    
     	else if(temp.equals("d")) return 4;   
    	else if(temp.equals("e")) return 5;
     	else if(temp.equals("f")) return 6;   	
     	else if(temp.equals("g")) return 7;   	
       	else if(temp.equals("h")) return 8; 	
    	else return 1;    
		  	
    }
    
	// Allows User to Pick the Column of the Piece They Wish to Move
    private int pickCol( ){		
    	int choice = -1;
    	for( ; ; ){
   			System.out.print("C: ");    					 			
    		try{	
    			String temp = scanner.next();
    			scanner.nextLine();
    			if(temp.equals("help")) userHelp();
    			else if(temp.equals("end")) endGame();		
				else if(Integer.valueOf(temp) >= 1 && Integer.valueOf(temp) <= 8){
					choice = Integer.valueOf(temp);
					 break;
				}
    			else if(temp.equals("export")){   				
    				b.exportBoard();
    			}					
   				else System.out.println("Error: Invalid Column #!");
    		}
    		catch(Exception e){System.out.println("Error: Invalid Column #!");}	
    	}
    	
    	return choice;		 	
    }   
    
	// Allows User to Pick the Column of the Piece They Wish to Move
    private int setDifficulty( ){		
    	int choice = -1;
    	for( ; ; ){
   			System.out.print("Difficulty (Between the Min: 2 [Easy] and the Max:6 [Hard]): ");    					 			
    		try{	
    			String temp = scanner.next();
    			scanner.nextLine();
    			if(temp.equals("help")) userHelp();
    			else if(temp.equals("end")) endGame();		
				else if(Integer.valueOf(temp) >= 2 && Integer.valueOf(temp) <= 6){
					choice = Integer.valueOf(temp);
					break;
				}
    			else if(temp.equals("export")){  				
    				b.exportBoard();
    			}					
   				else System.out.println("Error: Invalid Difficulty Selection!");
    		}
    		catch(Exception e){System.out.println("Error: Invalid Difficulty Selection!");}	
    	}
    	
    	return choice;		 	
    } 
	
	// Handles Pawn Promotion
    private Pieces.Type piecePromotion(Board.Team team){
		// Handles the AI's choice by randomly choosing a number between 1-4
		if(team == Board.Team.BLACK) {
			Random rnd = new Random();
			int val = rnd.nextInt(4) + 1;
			switch(val) {
				case 1:
					return Pieces.Type.BISHOP;
				case 2:
					return Pieces.Type.KNIGHT;
				case 3:
					return Pieces.Type.ROOK;
				case 4: 
				default:
					return Pieces.Type.QUEEN;
			}
		}
		
		// Handles the player's input choice
	    System.out.println("Pawn was Promoted! Select New Piece Type: R, H, B,or Q. ");
	    for( ; ; ){
	    	System.out.print("Choice: ");
	    	try{
	    		String temp = scanner.next();
	    		scanner.nextLine();
	    		if(temp.equals("end")) userHelp();
	    		else if(temp.equals("R")) return Pieces.Type.ROOK;
  				else if(temp.equals("H")) return Pieces.Type.KNIGHT;
  				else if(temp.equals("B")) return Pieces.Type.BISHOP;
  				else if(temp.equals("Q")) return Pieces.Type.QUEEN;
    			else if(temp.equals("end")) endGame();
    			else if(temp.equals("export")){    				
    				b.exportBoard();
    			}	    						
  				else System.out.println("Error: Invalid Choice!");	    		
	    	}
	      	catch(Exception e){ System.out.println("Error: Invalid Choice!"); } 	
	    }  		
    }   	   	
    
	// Ends the Game
    private void endGame(){
		System.out.println("\n------------------------------------------------------");
		System.out.println("You have resigned from the game. Thank you for playing!");
		System.out.println("------------------------------------------------------");
		System.exit(1);      	
    }
	
	// Displays the user help menu 
    private void userHelp(){
	    	System.out.println("\n-------------------------");
	    	System.out.println("Help Commands: Type Any of the Following to Get Information About It:");
			System.out.println("'end' : Allows you to resign from the game.");
			System.out.println("'export' : Allows you to export the current board configuration to file (will overwrite existing files).");
	    	System.out.println("'team' : information regarding what team you are.");
	    	System.out.println("'row' : how to input a row & the accepted values");
	    	System.out.println("'col' : how to input a column & the accepted values");
	    	System.out.println("'howToMove' : information regarding how to move a piece.");
	    	System.out.println("'promotion' : the accepted values for Pawn Promotion.");
	    	System.out.println("'moveReselect' : instructions on how to change the piece you want to move after selecting it & the accepted values.");
	    	System.out.println("'gameSelect' : outlines the different options for starting the game & the accepted values.");
	    	System.out.println("'fileFormat' : instructions on how to format the file from which you wish to read in a custom board configuration.");
	    	System.out.println("'quit' : exit the help commands");   	
	    	System.out.println("-------------------------");
	    	
	    for( ; ; ){		
	    	String temp = " ";
   			System.out.print("\nYour Selection: ");    					 			
    		try{	
    			temp = scanner.next();
    			scanner.nextLine();
    			if(temp.equals("quit")) break;
				else if(temp.equals("end")) {
					System.out.println("Allows for you to resign from the game at the current stage");
				}
				else if (temp.equals("export")) {
					System.out.println("Will export the current information about the board to a file. (Will overwrite existing files)");
				}
    			else if(temp.equals("help")){
    				System.out.println("You are already in the help commands!");
    			}
    			else if(temp.equals("team")){
    				System.out.println("As the player, you will be controlling the white team's pieces. Your goal is to kill the black team's King.");
    			}
    			else if(temp.equals("row")){
    				System.out.println("The row identifier's are the characters in the leftmost column displayed on the board.");
    				System.out.println("Accepted values: 'a', 'b', 'c', 'd', 'e', 'f', 'g', and 'h'.");
    				System.out.println("When it is your turn, you will be prompted for a row. Please ensure you type one of the accepted values.");
    			}
    			else if(temp.equals("col")){
    				System.out.println("The col identifier's are the numbers in the topmost row displayed on the board.");
    				System.out.println("Accepted values: '1', '2', '3', '4', '5', '6', '7', and '8'.");
    				System.out.println("After inputting a row, you will be prompted for a column. Please ensure you type one of the accepted values.");
    			}
    			else if(temp.equals("howToMove")){
    				System.out.println("Moving a Piece occurs in two steps: ");
    				System.out.println("Step 1: First, you will be prompted for the row & column values of the piece you wish to move.");
					System.out.println("*Note: if you've selected a piece you no longer wish to move, see 'moveReselect'.");
					System.out.println("Step 2: Next, you will be prompted for the row & column values for where you wish to move the piece.");
					System.out.println("*Note: for row & column values in Step 2, you must enter where the piece will end up after the move is exected.");
					System.out.println("*Example: if you are trying to take an enemies piece with a pawn, the row & column values must match the enemy pieces row & column.");
					System.out.println("*For En Passant: the row & column values would be the empty space above (since you are white team) the enemies pawn.");
					System.out.println("*For Castling: the row & column values would be 2 spaces to the left or right of the King, when all other conditions are met.");
    			}
    			else if(temp.equals("promotion")){
    				System.out.println("When one of your team's pawns' reaches the opposite side of the board, you will be promoted to a new piece.");
    				System.out.println("Pawns are able to be promoted to: Rook's, Knight's, Bishop's, and Queen's.");
    				System.out.println("Accepted values: 'R' for a Rook, 'H' for a Knight, 'B' for a Bishop, and 'Q' for a Queen.");
    				System.out.println("Please ensure you type one of the accepted values.");
    			}    			
				else if(temp.equals("moveReselect")){
					System.out.println("If you've selected a piece you no longer wish to move, enter a row and column number which that piece would be unable to move to.");
					System.out.println("Once you've inputted an invalid destination, you will be asked if you'd like to reselect the piece you'd like to move.");
					System.out.println("Accepted values: '0' to reselect the piece you want to move, '1' if you want to move the same piece.");
					System.out.println("Please ensure you type one of the accepted values.");
				}
				else if(temp.equals("gameSelect")){
					System.out.println("When the game starts, you will have two options: you can start a new game or load a board configuration from a .txt file.");
					System.out.println("Accepted values: '0' to start a new game, '1' to read in a board configuration from a file.");
					System.out.println("*Type 'fileFormat' for more information on the formatting required to read in a board configuration.");
					System.out.println("Please ensure you type one of the accepted values.");
				}
				else if(temp.equals("fileFormat")){
					System.out.println("Each line in the .txt file represents a piece that will be added to the board.");
					System.out.println("You will need type 4 or 5 pieces of information on each line, depending on the piece type.");
					System.out.println("Information Needed: ");
					System.out.println("[1.] Team Colour: 'b' for black team, 'w' for white team.");
					System.out.println("[2.] Piece Type: 'P' for pawns, 'R' for rooks, 'H' for knights, 'B' for bishops, 'Q' for queens, and 'K' for kings.");
					System.out.println("[3.] Row #: '1' for row a, '2' for row b, and so on until '8' for row h.");
					System.out.println("[4.] Col #: '1' for column 1, '2' for column 2, and so on until '8' for column 8.");
					System.out.println("[5.] For Pawn's, Rook's and King's: 'true' if the piece has made a move, 'false' if the piece hasn't moved yet.");
					System.out.println("In the file, type the information in the same format as this example: 'bP 2 2 true' or 'bQ 1 3'.");
					System.out.println("*Important Notes: if you attempt to put more than 1 king on a team, only the first king will be put on the board.");
					System.out.println("*Important Notes: if you attempt to put more than 8 pawn's on a team, only the first 8 will be put on the board.");
					System.out.println("*Important Notes: if you attempt to put more than 1 piece on the same space, only the first piece will be placed.");
					System.out.println("*Important Notes: for testing purposes, we have allowed for more than 2 Rooks, Knights, and Bishops as well as more than 1 Queens.");
					System.out.println("*Important Notes: for testing purposes, we have also allowed for more than 16 pieces total per team.");
				}
    			else System.out.print("Invalid Selection! ");
    		}
    		catch(Exception e){System.out.print("Invalid Selection! ");}	
    	}
    	
     	System.out.println("\n-------------------------");
    	System.out.println("Exiting Help Commands");
    	System.out.println("-------------------------\n");  
		
		// Reprint the board to see better
		b.printBoard();
    }
	
	
	// Main
    public static void main(String[] args) { 		
		ChessProject cp = new ChessProject(); 
		cp.Run();
	}
} // End of ChessProject
