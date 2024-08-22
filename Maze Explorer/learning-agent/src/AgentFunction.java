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

import java.util.ArrayList;
import java.util.Random;

class AgentFunction {
    private String agentName = "Wumpus Slasher";
    int agentXPos;
    int agentYPos;
    int initBlock;
    int steps;
    boolean huntWumpus = false;
    char direction;

    WumpusWorld wumpusWorld;
    private int[] actionTable;
    private boolean bump;
    private boolean glitter;
    private boolean breeze;
    private boolean stench;
    private boolean scream;
    private boolean training;
    private Random rand;
    int searchDepth = 1;
    int searchParam1 = 800;
    int searchParam2 = 400;

    public AgentFunction(boolean training) {
        agentXPos = 0;
        agentYPos = 0;
        direction = 'E';
        initBlock = 0;
        steps = 0;
        actionTable = new int[6];
        actionTable[0] = Action.GO_FORWARD;
        actionTable[1] = Action.TURN_RIGHT;
        actionTable[2] = Action.TURN_LEFT;
        actionTable[3] = Action.SHOOT;
        actionTable[4] = Action.GRAB;
        actionTable[5] = Action.NO_OP;
        wumpusWorld = new WumpusWorld();
        this.training = training;
        // New random number generator, for
        // randomly picking actions to execute
        rand = new Random();
    }

    public int process(TransferPercept tp) {
        bump = tp.getBump();
        glitter = tp.getGlitter();
        breeze = tp.getBreeze();
        stench = tp.getStench();
        scream = tp.getScream();

        if (agentXPos == 0 && agentYPos == 0 && stench == true && breeze == false && glitter == false) {
            if (direction == 'E' && initBlock == 0) {
                initBlock += 1;
//                steps+=1;
                wumpusWorld.arrow = 0;
                wumpusWorld.cells[1][0].wumpus -= 1;
                return actionTable[3];
            }
        }
        wumpusWorld.steps+=1;

        if (glitter == true) {
            return actionTable[4];
        } else if (breeze == true && scream == true) {
            wumpusWorld.updateWumpusWorld(agentXPos, agentYPos, 4);
            return actionTable[nextDestination(wumpusWorld)];
        } else if (breeze == false && scream == true) {
            wumpusWorld.updateWumpusWorld(agentXPos, agentYPos, 5);
            return actionTable[nextDestination(wumpusWorld)];
        } else if (stench == false && breeze == false && scream == false) {
            wumpusWorld.updateWumpusWorld(agentXPos, agentYPos, 0);
            return actionTable[nextDestination(wumpusWorld)];
        } else if (stench == true && breeze == true) {
            wumpusWorld.updateWumpusWorld(agentXPos, agentYPos, 1);
            return actionTable[nextDestination(wumpusWorld)];
        } else if (stench == true && breeze == false) {
            wumpusWorld.updateWumpusWorld(agentXPos, agentYPos, 2);
            return actionTable[nextDestination(wumpusWorld)];
        } else if (stench == false && breeze == true && scream == false) {
            wumpusWorld.updateWumpusWorld(agentXPos, agentYPos, 3);
            return actionTable[nextDestination(wumpusWorld)];
        } else {
            return actionTable[nextDestination(wumpusWorld)];
        }
    }

    // Determine the most viable cell destination, (destX, destY), including current position based on the state of the
    // world. If shooting at a wumpus is a better choice, this function will return (-1, -1).
    int nextDestination(WumpusWorld wumpusWorld) {

        ForwardSearch search = new ForwardSearch(training);
        SearchResult result = search.fwdSearch(agentXPos, agentYPos, direction, wumpusWorld, searchDepth);

        int destX = result.destX;
        int destY = result.destY;

        if (wumpusWorld.isCellSafe(destX, destY) && !huntWumpus) {
//            destX = result.destX;
//            destY = result.destY;
        } else if (result.utilities.size() * result.maxUtility / result.multiplicity > searchParam1) { // Check if it's worthy to explore an unknown cell.
//            destX = result.destX;
//            destY = result.destY;
        } else if (wumpusWorld.arrow == 1) {
            destX = agentXPos;
            destY = agentYPos;
            huntWumpus = true;
            // If there is no where to go, then try to hunt the wumpus.
            for (String key : wumpusWorld.wumpus) { // locate the wumpus
                ArrayList<int[]> path = result.shortestPaths.get(key); // The shortest path from where the agent is to a frontier cell.
                int nx = path.get(0)[0]; // Coordinates of the next cell on the path
                int ny = path.get(0)[1];
                if (path.size() != 1) {
                    destX = nx; // Go to the cell by the wumpus to hunt it down.
                    destY = ny;
                } else {
                    destX = agentXPos;
                    destY = agentYPos;
                }
            }
            int[] target = wumpusWorld.huntWumpus(agentXPos, agentYPos, direction);
            if (target[0] != -1) {
                wumpusWorld.killWumpus(target[0], target[1]);
                // Calculate the utilities for a wumpusless world to decide whether to shoot an arrow.
                SearchResult wumpuslessResult = search.fwdSearch(agentXPos, agentYPos, direction, wumpusWorld, searchDepth);
                // Compare the utilities between worlds with and without a wumpus.
                if (wumpuslessResult.maxUtility > 0 && (wumpuslessResult.maxUtility / wumpuslessResult.multiplicity - result.maxUtility / result.multiplicity) > searchParam2) {
                    destX = -1; // Shoot arrow!
                    destY = -1;
                    huntWumpus = false;
                } else {
                    // Revive wumpus since it's unworthy to kill it.
                    wumpusWorld.reviveWumpus(target[0], target[1]);
                }
            }

        } else {
            destX = agentXPos; // Nothing to do, spin around
            destY = agentYPos;
        }

        int action = nextAction(destX, destY, wumpusWorld);
        return action;
    }

    // Output the corresponding action based on the destination cell chosen
    int nextAction(int destinationX, int destinationY, WumpusWorld wumpusWorld) {
        if (destinationX == -1 && destinationY == -1) {
            wumpusWorld.arrow = 0;
            return 3;
        }
        if (agentXPos < destinationX) {
            if (direction == 'E') {
                agentXPos = destinationX;
                wumpusWorld.cells[agentXPos][agentYPos].safe = 1;
                return 0;
            } else if (direction == 'N') {
                direction = 'E';
                return 1;
            } else if (direction == 'S') {
                direction = 'E';
                return 2;
            } else {
                direction = 'S';
                return 2;
//                if (rand.nextInt(2) + 1 == 1) {
//                    direction = 'N';
//                    return 1;
//                } else {
//                    direction = 'S';
//                    return 2;
//                }
            }
        } else if (agentXPos > destinationX) {
            if (direction == 'W') {
                agentXPos = destinationX;
                wumpusWorld.cells[agentXPos][agentYPos].safe = 1;
                return 0;
            } else if (direction == 'N') {
                direction = 'W';
                return 2;
            } else if (direction == 'S') {
                direction = 'W';
                return 1;
            } else {
                direction = 'N';
                return 2;
//                if (rand.nextInt(2) + 1 == 1) {
//                    direction = 'S';
//                    return 1;
//                } else {
//                    direction = 'N';
//                    return 2;
//                }
            }
        } else if (agentYPos < destinationY) {
            if (direction == 'N') {
                agentYPos = destinationY;
                wumpusWorld.cells[agentXPos][agentYPos].safe = 1;
                return 0;
            } else if (direction == 'W') {
                direction = 'N';
                return 1;
            } else if (direction == 'E') {
                direction = 'N';
                return 2;
            } else {
                direction = 'W';
                return 1;
//                    if (rand.nextInt(2) + 1 == 1) {
//                        direction = 'W';
//                        return 1;
//                    } else {
//                        direction = 'E';
//                        return 2;
//                    }
            }
        } else if (agentYPos > destinationY) {
            if (direction == 'S') {
                agentYPos = destinationY;
                wumpusWorld.cells[agentXPos][agentYPos].safe = 1;
                return 0;
            } else if (direction == 'W') {
                direction = 'S';
                return 2;
            } else if (direction == 'E') {
                direction = 'S';
                return 1;
            } else {
                direction = 'E';
                return 1;
//                    if (rand.nextInt(2) + 1 == 1) {
//                        direction = 'E';
//                        return 1;
//                    } else {
//                        direction = 'W';
//                        return 2;
//                    }
            }
        } else {
            // spin if there is nowhere to go
            if (direction == 'E') {
                direction = 'S';
            } else if (direction == 'N') {
                direction = 'E';
            } else if (direction == 'S') {
                direction = 'W';
            } else {
                direction = 'N';
            }
            return 1;
        }

    }

    // public method to return the agent's name
    // do not remove this method
    public String getAgentName() {
        return agentName;
    }
}

