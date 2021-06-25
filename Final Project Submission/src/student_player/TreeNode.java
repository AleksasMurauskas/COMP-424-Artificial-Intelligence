package student_player;

import java.util.ArrayList;
import java.util.List;

import pentago_twist.PentagoBoardState;
import pentago_twist.PentagoMove;

/**
 * 
 * @author aleks
 * This class represents a node in  the minimax tree I will be developing
 * Each node holds a h value, a list of children nodes, a parent node, and a board state and move. 
 */
public class TreeNode {
	TreeNode parent_node;
	List<TreeNode> children; 
	double heuristic_val;
	PentagoBoardState state;
	PentagoMove move;
	
	//Contructor for tree nodes
	public TreeNode(TreeNode parent_node,PentagoBoardState state,PentagoMove move) {
		this.parent_node = parent_node;
		this.state = state;
		this.move =move;
		this.heuristic_val=0;
		this.children = new ArrayList<>();
	}
	public void addChildNode(TreeNode child_node) {
		this.children.add(child_node);
	}
	
	public TreeNode get_parent() {
		return this.parent_node;
	}
	
}
