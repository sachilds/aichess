# Chess Project
This project was created for the COSC 3P71 - Introduction to Artificial Intelligence course at BrockU by [Sarah](https://github.com/sachilds) and [Matthew](https://github.com/GrahlmanMatthew). The purpose of this project was to create a Human vs AI chess game using a MiniMax tree algorithm with Alpha-Beta pruning to improve performance. 

## Requirements
- Use of a Minimax algorithm + Alpha-Beta pruning
- Ability for a user to play against the AI
- Respecting the traditional rules of chess
- Allow for Castling and En Passant 
- Allow for importing/exporting a chess board from a file

## AI Evaluation
It was necessary for the AI to be able to evaluate each potential state that it generated for the tree. The heuristic to measure the states was based on the state of the board, for example if it was in Play, Check, Checkmae or Stalemate and the respective player for winning/losing states. When in Play the heuristic would measure the number of pieces remaining and their "value". This AI placed a preferrence on valuable pieces compared to pawns. 

The following formula was used to evaluate the boards. 
> EVAL  =(Current  State  value  +  (#  of  Attackable  Spaces for H/B) + (Summed weights of Capturable Pieces - Summed weights of Threatened pieces) + Center Influence) * Ratio of Black/White pieces

###### Current State
The Current State value is the value of which the current state of the board has associated with it. Play is considered a neutral state so it has no influence over the AI, whereas Checkmate has a very high value associated with it to encourage the AI to steer towards it. Check is similar, however its value is much less of Checkmates to reflect the relationship between the two states. Stalemate however, is represented as a negative value because traditionally it is considered to be draw. A draw is not inherently good or bad, however winning is the better option so that is reflected by the value of stalemate. 

###### Attackable Spaces
The number of attackable spaces is calculated solely for the Bishop and Knight, and modifies the returned value based on their weight to prevent the value from taking over the equation. This portion of the evaluation is meant to favour piece development, an idea where the more spaces your stronger pieces can reach the better your odds of winning or controlling the board are. 
 
###### Capturable & Threatened Pieces
These were summations of the weights for pieces that can be captured, and are threatened given the current board. These weights influence what boards have more potential to capture or protect stronger pieces. Capturable pieces are pieces on the opponent's side that are within range of at least one other piece, their weight is summed together with any other capturable pieces. Threatened pieces are those on the playing side that are within range of at least one of the opponent's pieces, their weight is then summed together as well with any other threatened pieces. 

###### Pieces Ratio
The ratio of black to white pieces would be calculated as a decimal (Num of Black / Num of White). This discourages the AI from choosing moves that would put it at a disadvantage from having fewer pieces.

**For further information please refer to the report(Report/chessReport.pdf)**
