package student_player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import boardgame.Board;
import boardgame.Move;
import pentago_twist.PentagoPlayer;
import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoBoardState.Piece;
import pentago_twist.PentagoCoord;
import pentago_twist.PentagoMove;

public class MyTools {
	private final static float loss = -10000;
	private final static float victory = 10000;
	private final static float draw =-5000;
	private static PentagoBoardState.Piece max_player_piece_color =Piece.EMPTY;
	private static PentagoBoardState.Piece min_player_piece_color=Piece.EMPTY;
	
	MyTools(){
		
	}
	/**
	 * 
	 * @param active_state
	 * @param player_num
	 * @param depth
	 * @return The best move the minimax found
	 */
	public static Move getMinMaxedMove(PentagoBoardState active_state, int player_num, int depth,PentagoBoardState.Piece max_playerPiece) {
		long startTime = System.currentTimeMillis();
		//Set The Student Player and enemy player's piece colors 
		if(max_player_piece_color==Piece.EMPTY) {
			max_player_piece_color=max_playerPiece;
			if(max_playerPiece == Piece.WHITE) {
				min_player_piece_color =Piece.BLACK;
			}
			else{
				min_player_piece_color =Piece.WHITE;
			}
		}
		List<PentagoMove> best_moves_available = new ArrayList<>();
		double best_h_val = Integer.MIN_VALUE;
		List<PentagoMove> available_moves = active_state.getAllLegalMoves();
		//Create First Batch of children, Will always be maximizing
		for (PentagoMove active_move : available_moves) {
			PentagoBoardState state_with_move = (PentagoBoardState) active_state.clone();
			state_with_move.processMove(active_move);
			TreeNode active_node = new TreeNode(null,state_with_move,active_move);
			double alpha = Integer.MIN_VALUE;
			double beta = Integer.MAX_VALUE;
			double move_val = minimax_algo(player_num,active_node,depth,alpha,beta,max_player_piece_color);
			//Better heuristic
			if (move_val >best_h_val) {
				best_moves_available.clear();
				best_moves_available.add(active_move);
				best_h_val = move_val;	
			}
			//Equivalent Heuristic
			else if(move_val == best_h_val) {
				best_moves_available.add(active_move);
			}
			long endTime = System.currentTimeMillis();
	    	long duration = (endTime - startTime);
	    	if (duration>1970) {
	    		break;
	    	}
		}
		int list_size =best_moves_available.size();
		if(best_moves_available.size()>0) {
			Random random = new Random();
            return best_moves_available.get(random.nextInt(list_size));
		}
		//In the case of no best moves use a random one
		return active_state.getRandomMove();
	}
	/**
	 * 
	 * @param max_player_num
	 * @param active_node
	 * @param depth
	 * @param alpha
	 * @param beta
	 * @return
	 */
	private static double minimax_algo(int max_player_num, TreeNode active_node, int depth, double alpha, double beta,PentagoBoardState.Piece curr_playerPiece) {
		if(active_node.state.gameOver()) {
			double h_val = 0;
			//Evaluate position
			h_val = heurisitc_evaluation(max_player_num,active_node.state,curr_playerPiece);
			active_node.heuristic_val= h_val;
			return h_val;
		}
		if(depth ==0) {
			double h_val = 0;
			//Evaluate position
			h_val = heurisitc_evaluation(max_player_num,active_node.state,curr_playerPiece);
			active_node.heuristic_val= h_val;
			return h_val;
		}
		List<PentagoMove> available_moves = active_node.state.getAllLegalMoves();
		
		if(active_node.state.getTurnPlayer()==max_player_num) {
			double best_h = Integer.MIN_VALUE;
			for (PentagoMove active_move : available_moves) {
				PentagoBoardState state_with_move = (PentagoBoardState) active_node.state.clone();
				state_with_move.processMove(active_move);
				TreeNode new_child = new TreeNode(active_node,state_with_move,active_move);
				best_h = Math.max(best_h, minimax_algo(max_player_num,new_child, depth - 1, alpha, beta, max_player_piece_color));
				alpha = Math.max(alpha,best_h);
				//Alpha Pruning
				if(beta<=alpha) {
				 break;
				}
				active_node.addChildNode(new_child);
			}
			//Set node to have heuristic value
			active_node.heuristic_val =best_h;
			return best_h;
		}
		else {
			double best_h = Integer.MAX_VALUE;
			for (PentagoMove active_move : available_moves) {
				PentagoBoardState state_with_move = (PentagoBoardState) active_node.state.clone();
				state_with_move.processMove(active_move);
				TreeNode new_child = new TreeNode(active_node,state_with_move,active_move);
				best_h = Math.min(best_h, minimax_algo(max_player_num,new_child, depth - 1, alpha, beta,min_player_piece_color));
				beta = Math.min(beta,best_h);
				//Beta Pruning
				if(beta<=alpha) {
				 break;
				}
			}
			active_node.heuristic_val =best_h;
			return best_h;
		}
	}
	/**
	 * 
	 * @param max_player_num
	 * @param state
	 * @return
	 */
	public static double heurisitc_evaluation(int max_player_num,PentagoBoardState state,PentagoBoardState.Piece curr_playerPiece) {
		if(state.gameOver()) {
			if(state.getWinner()==max_player_num) {
				return victory - state.getTurnNumber(); //Encourages 
			}
			if(state.getWinner()==Board.DRAW) {
				return draw + state.getTurnNumber();
			}
			else {
				return loss;
			}
		}
		double h = 2000;
		
		Piece[][] board = state.getBoard();
		//Look through 
		//Part 1
		
		//See how many verticals are possible
		//Row 1 top to 5
		ArrayList<Piece> row1 = new ArrayList<>();
		row1.add(board[0][0]); 
		row1.add(board[0][1]); 
		row1.add(board[0][2]); 
		row1.add(board[0][3]); 
		row1.add(board[0][4]); 
		h+=eval_line(row1,curr_playerPiece);
		//Row 1 bottom to 5
		/*
		ArrayList<Piece> row2 = new ArrayList<>();
		row2.add(board[0][1]);
		row2.add(board[0][2]);
		row2.add(board[0][3]);
		row2.add(board[0][4]);
		row2.add(board[0][5]);
		h+=eval_line(row2,curr_playerPiece);
		*/
		//Row 2 top to 5
		/*
		ArrayList<Piece> row3 = new ArrayList<>();
		row3.add(board[1][0]); 
		row3.add(board[1][1]); 
		row3.add(board[1][2]); 
		row3.add(board[1][3]); 
		row3.add(board[1][4]); 
		h+=eval_line(row3,curr_playerPiece);
		//Row 2 bottom to 5
		*/
		ArrayList<Piece> row4 = new ArrayList<>();
		row4.add(board[1][1]);
		row4.add(board[1][2]);
		row4.add(board[1][3]);
		row4.add(board[1][4]);
		row4.add(board[1][5]);
		h+=eval_line(row4,curr_playerPiece);
		
		//Row 3 top to 5
		/*
		ArrayList<Piece> row5 = new ArrayList<>();
		row5.add(board[2][0]);
		row5.add(board[2][1]);
		row5.add(board[2][2]);
		row5.add(board[2][3]);
		row5.add(board[2][4]);
		h+=eval_line(row5,curr_playerPiece);
		*/
		//Row 3 bottom to 5
		ArrayList<Piece> row6 = new ArrayList<>();
		row6.add(board[2][1]);
		row6.add(board[2][2]);
		row6.add(board[2][3]);
		row6.add(board[2][4]);
		row6.add(board[2][5]);
		h+=eval_line(row6,curr_playerPiece);
		//Row 4 top to 5
		ArrayList<Piece> row7 = new ArrayList<>();
		row7.add(board[3][0]);
		row7.add(board[3][1]);
		row7.add(board[3][2]);
		row7.add(board[3][3]);
		row7.add(board[3][4]);
		h+=eval_line(row7,curr_playerPiece);
		//Row 4 bottom to 5
		/*
		ArrayList<Piece> row8 = new ArrayList<>();
		row8.add(board[3][1]);
		row8.add(board[3][2]);
		row8.add(board[3][3]);
		row8.add(board[3][4]);
		row8.add(board[3][5]);
		h+=eval_line(row8,curr_playerPiece);
		*/
		//Row 5 top to 5
		/*
		ArrayList<Piece> row9 = new ArrayList<>();
		row9.add(board[4][0]);
		row9.add(board[4][1]);
		row9.add(board[4][2]);
		row9.add(board[4][3]);
		row9.add(board[4][4]);
		h+=eval_line(row9,curr_playerPiece);
		*/
		//Row 5 bottom to 5
		ArrayList<Piece> row10 = new ArrayList<>();
		row10.add(board[4][1]);
		row10.add(board[4][2]);
		row10.add(board[4][3]);
		row10.add(board[4][4]);
		row10.add(board[4][5]);
		h+=eval_line(row10,curr_playerPiece);
		//Row 6 top to 5
		ArrayList<Piece> row11 = new ArrayList<>();
		row11.add(board[5][0]);
		row11.add(board[5][1]);
		row11.add(board[5][2]);
		row11.add(board[5][3]);
		row11.add(board[5][4]);
		h+=eval_line(row11,curr_playerPiece);
		//Row 6 bottom to 5
		/*
		ArrayList<Piece> row12 = new ArrayList<>();
		row12.add(board[5][1]);
		row12.add(board[5][2]);
		row12.add(board[5][3]);
		row12.add(board[5][4]);
		row12.add(board[5][5]);
		h+=eval_line(row12,curr_playerPiece);
		*/
		//Column 1 left to 5
		/*
		ArrayList<Piece> row13 = new ArrayList<>();
		row13.add(board[0][0]); 
		row13.add(board[1][0]); 
		row13.add(board[2][0]); 
		row13.add(board[3][0]); 
		row13.add(board[4][0]); 
		h+=eval_line(row13,curr_playerPiece);
		*/
		//Column 1 right to 5
		ArrayList<Piece> row14 = new ArrayList<>();
		row14.add(board[1][0]); 
		row14.add(board[2][0]); 
		row14.add(board[3][0]); 
		row14.add(board[4][0]); 
		row14.add(board[5][0]); 
		h+=eval_line(row14,curr_playerPiece);
		//Column 2 left to 5
		ArrayList<Piece> row15 = new ArrayList<>();
		row15.add(board[0][1]);
		row15.add(board[1][1]); 
		row15.add(board[2][1]); 
		row15.add(board[3][1]); 
		row15.add(board[4][1]); 
		h+=eval_line(row15,curr_playerPiece);
		//Column 2 right to 5
		/*
		ArrayList<Piece> row16 = new ArrayList<>();
		row16.add(board[1][1]); 
		row16.add(board[2][1]); 
		row16.add(board[3][1]); 
		row16.add(board[4][1]); 
		row16.add(board[5][1]);
		h+=eval_line(row16,curr_playerPiece);
		/*
		//Column 3 left to 5
		/*
		ArrayList<Piece> row17 = new ArrayList<>();
		row17.add(board[0][2]);
		row17.add(board[1][2]); 
		row17.add(board[2][2]); 
		row17.add(board[3][2]); 
		row17.add(board[4][2]); 
		h+=eval_line(row17,curr_playerPiece);
		*/
		//Column 3 right to 5
		ArrayList<Piece> row18 = new ArrayList<>();
		row18.add(board[1][2]); 
		row18.add(board[2][2]); 
		row18.add(board[3][2]); 
		row18.add(board[4][2]); 
		row18.add(board[5][2]);
		h+=eval_line(row18,curr_playerPiece);		
		//Column 4 left to 5
		ArrayList<Piece> row19 = new ArrayList<>();
		row19.add(board[0][3]);
		row19.add(board[1][3]); 
		row19.add(board[2][3]); 
		row19.add(board[3][3]); 
		row19.add(board[4][3]); 
		h+=eval_line(row19,curr_playerPiece);
		//Column 4 right to 5
		/*
		ArrayList<Piece> row20 = new ArrayList<>();
		row20.add(board[1][3]); 
		row20.add(board[2][3]); 
		row20.add(board[3][3]); 
		row20.add(board[4][3]); 
		row20.add(board[5][3]);
		h+=eval_line(row20,curr_playerPiece);
		*/
		//Column 5 left to 5
		/*
		ArrayList<Piece> row21 = new ArrayList<>();
		row21.add(board[0][4]);
		row21.add(board[1][4]); 
		row21.add(board[2][4]); 
		row21.add(board[3][4]); 
		row21.add(board[4][4]); 
		h+=eval_line(row21,curr_playerPiece);
		*/
		//Column 5 right to 5
		ArrayList<Piece> row22 = new ArrayList<>();
		row22.add(board[1][4]); 
		row22.add(board[2][4]); 
		row22.add(board[3][4]); 
		row22.add(board[4][4]); 
		row22.add(board[5][4]);
		h+=eval_line(row22,curr_playerPiece);		
		//Column 6 left to 5
		ArrayList<Piece> row23 = new ArrayList<>();
		row23.add(board[0][5]);
		row23.add(board[1][5]); 
		row23.add(board[2][5]); 
		row23.add(board[3][5]); 
		row23.add(board[4][5]); 
		h+=eval_line(row23,curr_playerPiece);
		//Column 6 right to 5
		/*
		ArrayList<Piece> row24 = new ArrayList<>();
		row24.add(board[1][5]); 
		row24.add(board[2][5]); 
		row24.add(board[3][5]); 
		row24.add(board[4][5]); 
		row24.add(board[0][5]);
		h+=eval_line(row24,curr_playerPiece);
		*/
		//Diagonal Top Left Corner
		ArrayList<Piece> row25 = new ArrayList<>();
		row25.add(board[0][0]); 
		row25.add(board[1][1]); 
		row25.add(board[2][2]); 
		row25.add(board[3][3]); 
		row25.add(board[4][4]);
		h+=eval_line(row25,curr_playerPiece);
		//Diagonal Bottom Right Corner
		/*
		ArrayList<Piece> row26 = new ArrayList<>();
		row26.add(board[1][1]); 
		row26.add(board[2][2]); 
		row26.add(board[3][3]); 
		row26.add(board[4][4]);
		row26.add(board[5][5]); 
		h+=eval_line(row26,curr_playerPiece);
		*/
		//Diagonal Bottom Left Corner
		ArrayList<Piece> row27 = new ArrayList<>();
		row27.add(board[5][0]); 
		row27.add(board[4][1]); 
		row27.add(board[3][2]); 
		row27.add(board[2][3]);
		row27.add(board[1][4]); 
		h+=eval_line(row27,curr_playerPiece);
		//Diagonal Top Right Right Corner
		/*
		ArrayList<Piece> row28 = new ArrayList<>();
		row28.add(board[4][1]); 
		row28.add(board[3][2]); 
		row28.add(board[2][3]);
		row28.add(board[1][4]); 
		row28.add(board[0][5]); 
		h+=eval_line(row28,curr_playerPiece);
		*/
		//Diagonal [0][1] to 4 5 
		ArrayList<Piece> row29 = new ArrayList<>();
		row29.add(board[0][1]); 
		row29.add(board[1][2]); 
		row29.add(board[2][3]);
		row29.add(board[3][4]); 
		row29.add(board[4][5]); 
		h+=eval_line(row29,curr_playerPiece);
		//Diagonal 1 0 to 5 4
		ArrayList<Piece> row30 = new ArrayList<>();
		row30.add(board[1][0]); 
		row30.add(board[2][1]); 
		row30.add(board[3][2]);
		row30.add(board[4][3]); 
		row30.add(board[5][4]); 
		h+=eval_line(row30,curr_playerPiece);
		
		ArrayList<Piece> row31 = new ArrayList<>();
		row31.add(board[4][0]); 
		row31.add(board[3][1]); 
		row31.add(board[2][2]);
		row31.add(board[1][3]); 
		row31.add(board[0][4]); 
		h+=eval_line(row31,curr_playerPiece);
		
		ArrayList<Piece> row32 = new ArrayList<>();
		row32.add(board[5][1]); 
		row32.add(board[4][2]); 
		row32.add(board[3][3]);
		row32.add(board[2][4]); 
		row32.add(board[1][5]); 
		h+=eval_line(row32,curr_playerPiece);
				
		return h;
	}
	
	public static double eval_line(ArrayList<Piece> line,PentagoBoardState.Piece curr_playerPiece) {
		int count_blank =5;
		int count_b =0;
		int count_w =0;
		for(int x=0;x<5;x++) {
			if(Piece.WHITE ==line.get(x)) {
				count_w++;
			}
			if(Piece.BLACK ==line.get(x)) {
				count_b++;
			}
		}
		count_blank = count_blank-count_b -count_w;
		if(count_blank==5) {
			return 0;
		}
		
		Piece p1= line.get(0);
		Piece p2= line.get(1);
		Piece p3= line.get(2);
		Piece p4= line.get(3);
		Piece p5= line.get(4);
		
		boolean p1_taken =true;
		boolean p2_taken =true;
		boolean p3_taken =true;
		boolean p4_taken =true;
		boolean p5_taken =true;
		
		if(p1 == Piece.EMPTY) {
			p1_taken =false;
		}
		if(p2 == Piece.EMPTY) {
			p2_taken =false;
		}
		if(p3 == Piece.EMPTY) {
			p3_taken =false;
		}
		if(p4 == Piece.EMPTY) {
			p1_taken =false;
		}
		if(p5 == Piece.EMPTY) {
			p4_taken =false;
		}
		if(curr_playerPiece==Piece.BLACK) { //Run algorithm with Black being positive
			if(count_blank==4) { 				
				if(count_b==1) {
					//One Black
					if(p2_taken||p3_taken||p4_taken) {
						//Preferred slightly
						return 20;
					}
					else {
						return 10;
					}
				}
				else {
					//One White
					if(p2_taken||p3_taken||p4_taken) {
						//Slightly Worse
						return -10;
					}
					else {
						return -5;
					}
				}
			}
			if(count_blank==3){
				if(count_b==2) {
					//Two Black
					//Encourage them to be consecutive
					if((p2_taken&&p3_taken)||(p3_taken&&p4_taken)) {
						//Encourage them to be in the middle 
						return 50;
					}
					if((p1_taken&&p2_taken)||(p4_taken&&p5_taken)) {
						return 40;
					}
					else {
						return 30;
					}
				}
				if(count_w==2) {
					if((p2_taken&&p3_taken)||(p3_taken&&p4_taken)) {
						//Encourage them to be in the middle 
						return -40;
					}
					if((p1_taken&&p2_taken)||(p4_taken&&p5_taken)) {
						return -30;
					}
					else {
						return -20;
					}
				}
				else {
					return 0;
				}
			}
			if(count_blank==2) {
				if(count_b==3) {
					//Three Black
					if((p2_taken&&p3_taken&&p4_taken)) {
						//Encourage them to be in the middle 
						return 300;
					}
					if((p1_taken&&p2_taken&&p3_taken)||(p3_taken&&p4_taken&&p5_taken)) {
						//Connected at the ends
						return 200;
					}
					else { //Not connected
						return 100;
					}
				}
				if(count_w==3) {
					if((p2_taken&&p3_taken&&p4_taken)) {
						//Encourage them to be in the middle 
						return -250;
					}
					if((p1_taken&&p2_taken&&p3_taken)||(p3_taken&&p4_taken&&p5_taken)) {
						//Connected at the ends
						return -125;
					}
					else { //Not connected
						return -75;
					}
				}
				else {
					//Mix of 2
					return 0; 
				}
			}
			if(count_blank==1) {
				if(count_b==4) {
					return 1000;
				}
				if(count_w==4) {
					return -2000;
				}
				else {
					//Mix of 2
					return 0; 
				}
			}
			else {
				//Line is already filled. 
				return 0;
			}
		}
		else { //Run algorithm with White being positive
			if(count_blank==4) {
				if(count_b==1) {
					
					if(p2_taken||p3_taken||p4_taken) {
						//Preferred slightly
						return -10;
					}
					else {
						return -5;
					}
				}
				else {
					//One White
					if(p2_taken||p3_taken||p4_taken) {
						//Preferred slightly
						return 20;
					}
					else {
						return 10;
					}
					
				}
			}
			if(count_blank==3){
				if(count_b==2) {
					//Two Black
					//Two Black
					//Encourage them to be consecutive
					if((p2_taken&&p3_taken)||(p3_taken&&p4_taken)) {
						//Encourage them to be in the middle 
						return -40;
					}
					if((p1_taken&&p2_taken)||(p4_taken&&p5_taken)) {
						return -30;
					}
					else {
						return -20;
					}
				}
				if(count_w==2) {
					//Two White
					if((p2_taken&&p3_taken)||(p3_taken&&p4_taken)) {
						//Encourage them to be in the middle 
						return 50;
					}
					if((p1_taken&&p2_taken)||(p4_taken&&p5_taken)) {
						return 40;
					}
					else {
						return 30;
					}
				}
				else {
					//Mix of 2
					return 0;
				}
			}
			if(count_blank==2) {
				if(count_b==3) {
					//Three Black
					if((p2_taken&&p3_taken&&p4_taken)) {
						//Encourage them to be in the middle 
						return -250;
					}
					if((p1_taken&&p2_taken&&p3_taken)||(p3_taken&&p4_taken&&p5_taken)) {
						//Connected at the ends
						return -125;
					}
					else { //Not connected
						return -75;
					}
				}
				if(count_w==3) {
					//Three White
					if((p2_taken&&p3_taken&&p4_taken)) {
						//Encourage them to be in the middle 
						return 300;
					}
					if((p1_taken&&p2_taken&&p3_taken)||(p3_taken&&p4_taken&&p5_taken)) {
						//Connected at the ends
						return 200;
					}
					else { //Not connected
						return 100;
					}
				}
				else {
					//Mix of 2
					return 0; 
				}
			}
			if(count_blank==1) {
				if(count_b==4) {
					//Four Black
					return -2000;
				}
				if(count_w==4) {
					//Four White
					return 1000;
				}
				else {
					//Mix of 2
					return 0; 
				}
			}
			else {
				//Line is already filled. 
				return 0;
			}
		}	
	}
	public static Move openingMove(PentagoBoardState active_state, int player_num) {
		List<PentagoMove> available_moves = active_state.getAllLegalMoves();
		for(PentagoMove active_move :available_moves) {
			PentagoCoord coord = active_move.getMoveCoord();
			if((coord.getX()==2||coord.getX()==3)&&(coord.getY()==2||coord.getY()==3)) {
				return active_move;
			}
		}
		int list_size =available_moves.size();
		if(available_moves.size()>0) {
			Random random = new Random();
            return available_moves.get(random.nextInt(list_size));
		}
		return active_state.getRandomMove();		
		
	}
	
	public static double getSomething() {
        return Math.random();
    }
    
    
}