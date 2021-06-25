#Aleksas Murauskas 
#260718389
#Comp 424
#A1 Question 1

import copy
#Declare State Class
class State:
	def __init__(self, board, c=0, past_s =None, tot_c=0):
		self.board = board # Holds Puzzle
		self.c = c #Holds cost
		self.past_s =past_s #Holds 
		self.tot_c = tot_c

	def print_Board(self): #Print board to console 
		print(self.board[:3])
		print(self.board[3:])
		print("Number of Piece Moved: ", self.c)

	def find_possible_states(self): # Develop 
		active_board = self.board 
		empty_cell = active_board.index(0) # Find empty cell
		if empty_cell == 0: #Two possible subsequent states
			#Swap the Empty cell with the cell to the right
			board1 = active_board[:] #create a 
			board1[0] = active_board[1]
			board1[1] = 0
			#Swap the Empty cell with the cell "Below" it 
			board2 = active_board[:] #create a copy
			board2[0] = active_board[3]
			board2[3] = 0
			#Create States 
			new_s1 = State(board1, active_board[1], self)
			new_s2 = State(board2, active_board[3], self)
			return [new_s1,new_s2]
		elif empty_cell ==1:
			#Swap the Empty cell with the cell to the right
			board1 = active_board[:] #create a copy
			board1[1] = active_board[2]
			board1[2] = 0
			#Swap the Empty cell with the cell to the left
			board2 = active_board[:] #create a copy
			board2[1] = active_board[0]
			board2[0] = 0
			#Swap the Empty cell with the cell "Below" it 
			board3 = active_board[:] #create a copy
			board3[1] = active_board[4]
			board3[4] = 0
			#Create States 
			new_s1 = State(board1, active_board[2], self)
			new_s2 = State(board2, active_board[0], self)
			new_s3 = State(board3, active_board[4], self)
			return [new_s1,new_s2,new_s3]

		elif empty_cell==2:
			#Swap the Empty cell with the cell to the left
			board1 = active_board[:] #create a copy
			board1[2] = active_board[1]
			board1[1] = 0
			#Swap the Empty cell with the cell "Below" it 
			board2 = active_board[:] #create a copy
			board2[2] = active_board[5]
			board2[5] = 0
			#Create States 
			new_s1 = State(board1, active_board[1], self)
			new_s2 = State(board2, active_board[5], self)
			return [new_s1,new_s2]

		elif empty_cell ==3:
			#Swap the Empty cell with the cell to the right
			board1 = active_board[:] #create a copy
			board1[3] = active_board[4]
			board1[4] = 0
			#Swap the Empty cell with the cell "Above" it 
			board2 = active_board[:] #create a copy
			board2[3] = active_board[0]
			board2[0] = 0
			#Create States 
			new_s1 = State(board1, active_board[4], self)
			new_s2 = State(board2, active_board[0], self)
			return [new_s1,new_s2]
		elif empty_cell ==4:
			#Swap the Empty cell with the cell to the right
			board1 = active_board[:] #create a copy
			board1[4] = active_board[5]
			board1[5] = 0
			#Swap the Empty cell with the cell to the left
			board2 = active_board[:] #create a copy
			board2[4] = active_board[3]
			board2[3] = 0
			#Swap the Empty cell with the cell "Above" it 
			board3 = active_board[:] #create a copy
			board3[4] = active_board[1]
			board3[1] = 0
			#Create States 
			new_s1 = State(board1, active_board[5], self)
			new_s2 = State(board2, active_board[3], self)
			new_s3 = State(board3, active_board[1], self)
			return [new_s1,new_s2,new_s3]
		elif empty_cell ==5:
			#Swap the Empty cell with the cell to the left
			board1 = active_board[:] #create a copy
			board1[5] = active_board[4]
			board1[4] = 0
			#Swap the Empty cell with the cell "Above" it 
			board2 = active_board[:] #create a copy
			board2[5] = active_board[2]
			board2[2] = 0
			#Create States 
			new_s1 = State(board1, active_board[4], self)
			new_s2 = State(board2, active_board[2], self)
			return [new_s1,new_s2]
		else:
			exit(1)

#State Functions
def equivalent_state_check(s1,s2): #Checks if the boards are equivalent 
	b1 = s1.board
	b2 = s2.board
	for x, element in enumerate(b1):
		if element != b2[x]: #If an element value is different, they are not equal 
			return False
	return True


def display_path(s_curr,states_passed=0): #Prints the path at the end of the search
	if (s_curr):
		if s_curr.past_s:
			states_passed+=1
		display_path(s_curr.past_s, states_passed)# Iterate with the last state. 
		s_curr.print_Board()
	else:
		print("States expanded: ", states_passed)

#Search Functions
def bfs(active_state, goal_state):
	past_states = [] #Hold old states to avoid loops
	state_queue =[] #Hold new children in a queue
	loops =0 #Count number of iterations
	while not (equivalent_state_check(active_state,goal_state)): #Loop until Found
		loops=loops+1 #Count iteration 
		past_states.append(active_state)
		#active_state.print_Board()
		possible_children = active_state.find_possible_states()
		new_children=[]
		for curr in possible_children:
			tst =0
			for pst in past_states:
				if equivalent_state_check(curr,pst):
					tst=tst+1
			if tst==0:
				new_children.append(curr)
		sorted_states = sorted(new_children, key=lambda state: state.c, reverse=True)
		for next_state in sorted_states:
			next_state.tot_c = active_state.tot_c+1 #Increase Depth
			state_queue.insert(0, next_state)
		if state_queue:
			active_state = state_queue.pop()
	print("Total cost of operations: ", active_state.tot_c)
	print("Search Iterations to find solution: ", loops)
	display_path(active_state)

def ucs(active_state, goal_state):
	past_states = [] #Hold old states to avoid loops
	state_queue =[] #Hold new children in a queue
	loops =0 #Count number of iterations
	while not (equivalent_state_check(active_state,goal_state)):
		loops=loops+1
		past_states.append(active_state)
		possible_children = active_state.find_possible_states()
		new_children =[]
		for curr in possible_children:
			tst =0
			for pst in past_states:
				if equivalent_state_check(curr,pst):
					tst=tst+1
			if tst==0:
				new_children.append(curr)
		sorted_states = sorted(new_children, key=lambda state: state.c, reverse=False)
		for next_state in sorted_states:
			next_state.tot_c = active_state.tot_c+1 #Increase Depth
			state_queue.insert(0, next_state)
		if state_queue:
			active_state = state_queue.pop()
		else:
			return
	print("Total cost of operations: ", active_state.tot_c)
	print("Search Iterations to find solution: ", loops)
	display_path(active_state)
	return

def dfs(active_state, goal_state):
	past_states = [] #Hold old states to avoid loops
	state_stack = [] 
	loops =0
	while not (equivalent_state_check(active_state,goal_state)):
		loops=loops+1
		past_states.append(active_state)
		possible_children = active_state.find_possible_states()
		new_children =[]
		for curr in possible_children:
			tst =0
			for pst in past_states:
				if equivalent_state_check(curr,pst):
					tst=tst+1
			if tst==0:
				new_children.append(curr)
		sorted_states = sorted(new_children, key=lambda state: state.c, reverse=False)
		for next_state in sorted_states:
			next_state.tot_c = active_state.tot_c+1 #Increase Depth
			state_stack.append(next_state)
		if state_stack:
			active_state = state_stack.pop()
	display_path(active_state)
	print("Total cost of operations: ", active_state.tot_c)
	print("Search Iterations to find solution: ", loops)
	
	return

def ids(active_state, goal_state, maximum_depth):
	init_state = copy.copy(active_state)
	loops =1
	for active_depth in range(0, maximum_depth):
		past_states = [] #Hold old states to avoid loops
		state_stack = [] 
		active_state = init_state
		while not (equivalent_state_check(active_state,goal_state)):
			loops=loops+1
			past_states.append(active_state)
			possible_children = active_state.find_possible_states()
			new_children=[]
			for curr in possible_children:
				tst =0
				for pst in past_states:
					if equivalent_state_check(curr,pst):
						tst=tst+1
				if tst==0:
					new_children.append(curr)
			sorted_states = sorted(new_children, key=lambda state: state.c, reverse=False)
			for next_state in sorted_states:
				next_state.tot_c = active_state.tot_c+1 #Increase Depth
				if(next_state.tot_c<=active_depth): # Check for maximum depth exceeded 
					state_stack.append(next_state)
			if(state_stack):
				active_state =state_stack.pop()
			else:
				break
		if (equivalent_state_check(active_state,goal_state)):
			break
	display_path(active_state)
	print("Total cost of operations: ", active_state.tot_c)
	print("Search Iterations to find solution: ", loops)
	


if __name__ == "__main__": #Main Function
	#Declare Init and Goal states 
	s0Board = [1,4,2,5,3,0]
	sGBoard = [0,1,2,5,4,3]
	#create states
	s0_state = State(s0Board)
	sG_state = State(sGBoard)
	#Search Functions
	print("Below is the results of BFS ")
	bfs(s0_state, sG_state) #Breadth First Search Function, currently iterated on Unit cost 
	input("Press Enter to continue...")
	print("Below is the results of UCS")
	ucs(s0_state, sG_state) #Uniform Cost Search 
	input("Press Enter to continue...")
	#The above are functionally Identically, until BFS has to deal with weights 
	print("Below is the results of DFS")
	dfs(s0_state, sG_state)
	input("Press Enter to continue...")
	print("Below is the results of IDS")
	ids(s0_state,sG_state, 200)
	input("Press Enter to continue...")
	#Terminate 
	exit(0)