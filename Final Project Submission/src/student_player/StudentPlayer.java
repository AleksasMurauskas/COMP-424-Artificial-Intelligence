package student_player;

import boardgame.Move;

import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;

/** A player file submitted by a student. */
public class StudentPlayer extends PentagoPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260718389");
    }
    
    /**
     * Code to Launch Server
     * cd eclipse-workspace\comp424_Final
     * 
       java -cp bin boardgame.Server -p 8123 -q -t 2000 -ft 30000 -k 
       (-p) port sets the TCP port to listen on. (default=8123)
       (-ng) suppresses display of the GUI
       (-q) indicates not to dump log to console.
       (-t n) sets the timeout to n milliseconds. (default=2000)
       (-ft n) sets the first move timeout to n milliseconds. (default=30000)
       (-k) launch a new server every time a game ends (used to run multiple games without the GUI)
     */

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(PentagoBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
    	//long startTime = System.currentTimeMillis();
    	int depth =2;
    	Move myMove = boardState.getRandomMove();
        // Is random the best you can do?
    	int player_num = boardState.getTurnPlayer();
    	if(boardState.getTurnNumber()<2) {
    		myMove = MyTools.openingMove(boardState, player_num);    		
    		return myMove;
    	}
    	
    	if(boardState.getTurnNumber()>8) {
    		depth =3;
    	}
    	else if(boardState.getTurnNumber()>14) {
    		depth =4;
    	}
    	
    	PentagoBoardState.Piece max_playerPiece = (player_num == 0 ? PentagoBoardState.Piece.WHITE : PentagoBoardState.Piece.BLACK);
    	myMove = MyTools.getMinMaxedMove(boardState, player_num, depth,max_playerPiece);
        // Return your move to be processed by the server.
    	//long endTime = System.currentTimeMillis();
    	//System.out.println("That took " + (endTime - startTime) + " milliseconds");
        return myMove;
    }
    
}