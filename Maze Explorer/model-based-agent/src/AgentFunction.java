/*
 * Class that defines the agent function.
 * 
 * Written by James P. Biagioni (jbiagi1@uic.edu)
 * for CS511 Artificial Intelligence II
 * at The University of Illinois at Chicago
 * 
 * Last modified 2/19/07 
 * 
 * DISCLAIMER:
 * Elements of this application were borrowed from
 * the client-server implementation of the Wumpus
 * World Simulator written by Kruti Mehta at
 * The University of Texas at Arlington.
 * 
 */

import java.util.HashMap;
import java.util.HashSet;

class AgentFunction {
	
	// string to store the agent's name
	// do not remove this variable
	private String agentName = "Agent Wumpus Slasher";

	private Model model;

	private boolean bump;
	private boolean glitter;
	private boolean breeze;
	private boolean stench;
	private boolean scream;

	// initiate the agent
	public AgentFunction() {
		model = new Model();
	}

	/*Percepts tuple - <glitter, stench, scream, bump, breeze>*/
    public int process(TransferPercept tp) {
		// read in the current percepts
		// if it's glitter then grab right away

		bump = tp.getBump();
		glitter = tp.getGlitter();
		breeze = tp.getBreeze();
		stench = tp.getStench();
		scream = tp.getScream();
		if(glitter){
		    return Action.GRAB;
        }
		if(model.firstStep){
			if(breeze){
				// if the initial world looks like this, the agent can't move without huge risk.
				//    |   |   |   |
				//   -----------------
				//    | P |	  |   |
				//   -----------------
				//    | > |	  |   |
				return Action.NO_OP;
			}
			model.firstStep = false;
			if(stench){
				// if the initial world looks like this,
				// the agent have one half chance of killing the wumpus,
				// and can therefore move forward safely.
				//    |   |   |   |
				//   -----------------
				//    |   |	  |   |
				//   -----------------
				//    | > |	W |   |
				// this gives about 3X more points on average.
				model.shot = true;
				model.setPrevAction(Action.SHOOT);
				return Action.SHOOT;
			}
		}
		// the agent got stuck at the current position since there is no safe place to go,
		// or the agent has visited 13 different cells which means the gold might be sharing
		// same cell with a wumpus or a pit.
		if(model.stuck() || model.visited >= 13){
			//    |   |   |   |
			//   -----------------
			//    |   |	GP| P |
			//   -----------------
			//    | > |	W |   |
			return Action.NO_OP;
		}
        // update the model based on last action
		model.updateModelOnAction();
		// update the model based on current percepts
		model.updateModelOnPercept(breeze, stench, bump, scream, glitter);

		//// Condition - Action Rules ////

		// resolve any conflicting pit and wumpus flags
		resolveCells(tp);
		// shoot arrow if the agent is facing a wumpus
		if(model.isFacingWumpus()){
			model.shot = true;
			model.setPrevAction(Action.SHOOT);
			return Action.SHOOT;
		}
		// assign next ok location to the agent
		int[] loc = model.nextOkCell();

		// stop moving if no safe cell can be found
		if (loc[0] == -1) {
			model.setPrevAction(Action.NO_OP);
			return Action.NO_OP;
		}

		// turn toward the assigned location and avoid turning toward the wall.
		int nextDirection = model.findDirection(loc);
		if (bump) {
			model.setPrevAction(nextDirection);
			return nextDirection;
		}

		// check if forward action takes the agent to the desired safe cell.
		if (isFacingNextLoc(loc)) {
			model.resetNextLoc();
			model.setPrevAction(Action.GO_FORWARD);
			return Action.GO_FORWARD;
		}

		// if the agent is not facing the next location assigned, then perform a turn operation.
		model.setPrevAction(nextDirection);
		return nextDirection;
	}

	private boolean isFacingNextLoc(int[] loc) {
    	// check if the forward action takes the agent to the next loc cell.
		switch (model.getAgentDirection()){
			case "N":
				return model.getAgLoc()[0] - 1 == loc[0] && model.getAgLoc()[1] == loc[1];
			case "S":
				return model.getAgLoc()[0] + 1 == loc[0] && model.getAgLoc()[1] == loc[1];
			case "E":
				return model.getAgLoc()[0] == loc[0] && model.getAgLoc()[1] + 1 == loc[1];
			case "W":
				return model.getAgLoc()[0] == loc[0] && model.getAgLoc()[1] - 1 == loc[1];
		}
		return false;
    }

	private void resolveCells(TransferPercept tp) {
		int[] aLoc = model.getAgLoc();
		Cell[][] world = model.getWorld();
		int m = world.length;
		int n = world[0].length;
		HashMap<String, Integer> parentFlag = world[aLoc[0]][aLoc[1]].getFlags();
		// iterate through all adjacent cells to the current agent location
		// and resolve any conflicting pit and wumpus flags
		if(aLoc[1] + 1 < n){
			resolve(aLoc[0], aLoc[1] + 1, tp, parentFlag);
		}

		if(aLoc[1] - 1 >= 0){
			resolve(aLoc[0], aLoc[1] - 1, tp, parentFlag);
		}

		if(aLoc[0] - 1 >= 0){
			resolve(aLoc[0] - 1, aLoc[1], tp, parentFlag);
		}

		if(aLoc[0] + 1 < m){
			resolve(aLoc[0] + 1, aLoc[1], tp, parentFlag);
		}
	}

	private void resolve(int r, int c, TransferPercept tp, HashMap<String, Integer> parentFlag) {
    	// get the pit and the wumpus flags set for the cell at (r, c) position
    	HashMap<String, Integer> flags = model.getWorld()[r][c].getFlags();
		Cell cell = model.getWorld()[r][c];
    	// nothing needs to be done if cell is ok.
    	if(cell.isCellOK())
			return;

    	/*
    	In order to nullify a pit/wumpus possibility, we can apply the following rules
    	1. If there is a pit and wumpus flags in one of the adjacent cells but no stench
    	or breeze in current cell then, clear the flags and mark the cell as ok.
    	2. If there is a pit flag but no breeze, then clear the flag and the cell is ok if there is no stench and wumpus flag
    	3. If there is a wumpus flag but no stench, then clear the flag and the cell is ok if there is no breeze and pit flag
    	*/
    	if(flags.get(Utils.pit) > 0 && flags.get(Utils.wumpus) > 0) {
    		// if there is a pit and wumpus flags in one of the adjacent cells but no stench or breeze in current cell
			// , where v is visited and c is the current ad adjacent cell.
			//    | W | v | P |
			//   -----------------
			//    |   |	c |   |
			//   -----------------
			//    |   |	A |   |
			if (!(tp.getBreeze() || tp.getStench())) {
				model.getWorld()[r][c].setCellOK(true);
				flags.put(Utils.pit, 0);
				flags.put(Utils.wumpus, 0);
			}
		}

		if(flags.get(Utils.pit) > 0 && !(flags.get(Utils.wumpus) > 0)){
    		if(!tp.getBreeze()){
				// a pit flag but no breeze, where cell c was marked as a pit candidate
				// like the following example.
				// then cell c must be safe if A didn't smell anything which will mark c as a wumpus candidate.
				//    |   |	v | P |
				//   -----------------
				//    |   | c |   |
				//   -----------------
				//    |   |	A |   |
    			model.getWorld()[r][c].setCellOK(true);
    			flags.put(Utils.pit, 0);
			} else if(parentFlag.get(Utils.okNeighbor).equals(3)) {
				// agent at a breezy cell with 3 ok neighbors like the following.
				// we can see that the other neighbor must be the pit.
				//    |   |	  |   |
				//   -----------------
				//    |   | A | P |
				//   -----------------
				//    |   |	  |   |
				flags.put(Utils.pit, Utils.pitCellFlag);
			}
		}

		if(!(flags.get(Utils.pit) > 0) && flags.get(Utils.wumpus) > 0){
			// if there is a wumpus flag but no stench, then clear the flag and the cell is ok
			// if there is no breeze and pit flag.
			//    |   |	  |   |
			//   -----------------
			//    |   | c |   |
			//   -----------------
			//    |   |	A |   |
			if(!tp.getStench()){
				model.getWorld()[r][c].setCellOK(true);
				flags.put(Utils.wumpus, 0);
			}
		}
	}

	// public method to return the agent's name
	// do not remove this method
	public String getAgentName() {
		return agentName;
	}
}