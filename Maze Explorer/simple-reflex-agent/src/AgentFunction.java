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
import java.util.Random;

class AgentFunction {
	
	// string to store the agent's name
	// do not remove this variable
	private String agentName = "Agent Smith";
	
	private Random rand;

	private HashMap<String, ProbabilisticAction[]> conditionActionRules;

	public AgentFunction()
	{

		conditionActionRules = new HashMap();

		// Initialize the condition action rules
		Utils.createCARules(conditionActionRules);

		// new random number generator, for
		// randomly picking actions to execute
		rand = new Random();
	}

	/*
	Percepts tuple - <glitter, stench, scream, bump, breeze>
	*/
    public int process(TransferPercept tp)
	{
		// read in the current percepts
		// if it's glitter then grab right away
		if(tp.getGlitter()){
		    return Action.GRAB;
        }

        //Code to convert the percepts into a binary string for comparison with the condition action rules
        StringBuilder binaryPercept = new StringBuilder();

        binaryPercept.append(tp.getGlitter() ? 1 : 0);
        binaryPercept.append(tp.getStench() ? 1 : 0);
        binaryPercept.append(tp.getScream() ? 1 : 0);
        binaryPercept.append(tp.getBump() ? 1 : 0);
        binaryPercept.append(tp.getBreeze() ? 1 : 0);

        //Get the set of probable actions for the percept
        ProbabilisticAction[] probableActions = conditionActionRules.get(binaryPercept.toString());

        // random number for selecting a probable action
        float randomProbability = (rand.nextInt(10) + 1)/10f;
        float totalProbability = 0f;

        // this loop selects the action that matches the random probability
        for(ProbabilisticAction action: probableActions){
            if(action.probability + totalProbability > randomProbability){
                return action.action;
            }
            totalProbability += action.probability;
        }

		// return NO_OP if none of the actions were selected
	    return Action.NO_OP;
	}
	
	// public method to return the agent's name
	// do not remove this method
	public String getAgentName() {
		return agentName;
	}
}