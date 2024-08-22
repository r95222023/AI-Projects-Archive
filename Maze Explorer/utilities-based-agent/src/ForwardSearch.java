import java.util.*;

import static java.lang.Math.abs;

public class ForwardSearch {
    private WumpusWorld wumpusWorld;
    private int multiplicity = 1;
    private boolean debug = false;

    // Exhaust every possible configurations of frontier, and calculate the utility for each configuration.
    public void simulate(ArrayList<int[]> frontiers, int index, WumpusWorld trialWorld, int[] res) {
        if (index == frontiers.size()) {
            // Calculate utility for each frontier cell
            if (trialWorld.valid == 1 && verifyWorld(frontiers, trialWorld)) {
                for (int i = 0; i < frontiers.size(); i++) {
                    int X = frontiers.get(i)[0];
                    int Y = frontiers.get(i)[1];
                    WumpusCell cell = trialWorld.cells[X][Y];
                    if (debug)
                        System.out.println("Debug:" + X + "-" + Y + ":safe" + cell.safe + ":pit" + cell.pit + ":wumpus" + cell.wumpus);
                    if (trialWorld.cells[X][Y].safe == 1) {
                        // Sum up the utility of frontier cell for each configuration.
                        // Utility is designed to be close to the sum of the highest scores the agent can get
                        // from every observable wumpus world with that configuration.
                        res[i] += calcUtility(frontiers, trialWorld);
                    } else {
                        // the agent hit a wumpus or a pit.
                        res[i] -= 1000;
                    }
                }
            }
            return;
        }
        int X = frontiers.get(index)[0];
        int Y = frontiers.get(index)[1];
        StateSnapshot snap = new StateSnapshot(X, Y, trialWorld);

        if (trialWorld.valid == 1) { // If a cell is tagged as safe, having pit or wumpus will cause invalidity

            // Include i th candidate or not (k==0 include, k==1 don't include)
            for (int k = 0; k < 4; k++) {
                // Mark the flag for a configuration.
                trialWorld.tryFrontierConfiguration(X, Y, k);
                // Try next level combinations
                simulate(frontiers, index + 1, trialWorld, res);
                // Unmark the flag (backtrack) for next configuration
                trialWorld.backtrack(X, Y, snap);
            }
        } else {
            return;
        }
    }

    // Verify if a world is possible when a valid frontier configuration is found. It checks that if there exist
    // any pit/wumpus cell that has no neighbor with breeze/stench.
    private boolean verifyWorld(ArrayList<int[]> frontiers, WumpusWorld trialWorld) {
        for (int[] frontier : frontiers) {
            int fx = frontier[0];
            int fy = frontier[1];
            WumpusCell[][] cells = trialWorld.cells;
            // a jump cell means a cell with  manhattan distance from the original cell (fx, fy here) to this cell equal to 2
            for (ArrayList<int[]> jumps : trialWorld.getOneJumps(fx, fy)) {
                // this is a neighbor cell
                int nx = jumps.get(0)[0];
                int ny = jumps.get(0)[1];
                boolean breeze = cells[nx][ny].breeze == 1;
                boolean hasPit = cells[fx][fy].pit == 4;
                boolean stench = cells[nx][ny].stench == 1;
                boolean hasWumpus = cells[fx][fy].wumpus == 4;
                // check every neighbors of this neighbor that was visited.
                if (cells[nx][ny].visited == 1) {
                    for (int[] jump : jumps) {
                        int jx = jump[2];
                        int jy = jump[3];
                        if (cells[jx][jy].pit == 4 || trialWorld.pits.contains(jx + "-" + jy)) {
                            hasPit = true;
                        }

                        if (cells[jx][jy].wumpus == 4 || trialWorld.wumpus.contains(jx + "-" + jy)) {
                            hasWumpus = true;
                        }

                    }
                    // if a neighbor has breeze/stench, but all neighbors of this neighbor have no pit/wumpus,
                    // then we know this configuration is invalid.
                    if ((breeze && !hasPit) || (stench && !hasWumpus && !trialWorld.wumpusKilled)) {
                        return false;
                    }
                }

            }
        }
        return true;
    }

    // Calculate the utility for a given world state.
    public int calcUtility(ArrayList<int[]> frontiers, WumpusWorld trialWorld) {
        int u = 1;
        int macroStates = 1;
        int frontierSize = frontiers.size();
        int pitLeft = 2 - trialWorld.pitFound;
        int wumpusLeft = 1 - trialWorld.wumpusFound;
        int unknown = (16 - trialWorld.known - frontierSize);
        // no obstacle world, score should be close to 1000.
        int w0p0 = 1000;
        // one obstacle world, score should be close to 1000 but lower than an no-obstacle world.
        int w1p0 = 950;
        int w0p1 = w1p0;
        // a 1 pit 1 wumpus sub world should have higher score than ours (score ~ 500), so a heuristic weight 800 is given.
        int w1p1 = 900;
        // similar to 1p 1w, but should have slightly lower score since pit can't locate at the same cell.
        int w0p2 = 800;
        // a 2 pit 1 wumpus sub world is like ours, so a heuristic weight 500 should work.
        int w1p2 = 500;
        switch (pitLeft) {
            case 2:
                // For example, we have explored 0-0, 0-1, 1-0, the frontier (marked as F) for this world should look like below.
                // We don't feel any breeze, so the pits must be loacated at the unknown world !(known && frontier).

//                -----------------------
//               |     |     | P   |     |
//               |     |     |     |     |
//                -----------------------
//               | F   |     |     |     |
//               |     |     |     |     |
//                -----------------------
//               |     | F   |     | P   |
//               |     |     |     |     |
//                -----------------------
//               |     | <   |   F |     |
//               |     |     |     |     |
//                -----------------------
                // So there are unknown#-choose-2 ways of putting the pit in the unknown world
                macroStates *= unknown > 1 ? (unknown * (unknown - 1)) / 2 : 1;
                if (wumpusLeft == 1) {
                    // If there is no wumpus in the frontier, we have unknown# ways of putting wumpus in the unknown world.
                    macroStates *= unknown;
                    // The utility is just weight w1p2 * the number of possible world with this configuration.
                    u = w1p2 * macroStates;
                } else {
                    u = w0p2 * macroStates;
                }
                break;
            // Similar for other cases.
            case 1:
                macroStates *= unknown;
                if (wumpusLeft == 1) {
                    macroStates *= unknown;
                    u = w1p1 * macroStates;
                } else {
                    u = w0p1 * macroStates;
                }
                break;
            case 0:
                if (wumpusLeft == 1) {
                    macroStates *= unknown;
                    u = w1p0 * macroStates;
                } else {
                    u = w0p0 * macroStates;
                }
                break;
        }
        // Total number of possible worlds.
        multiplicity += macroStates;
        return u;
    }

    // Find the shortest path to frontier cells by using breadth first search algorithm.
    // A* algorithm don't give noticeable improvement here, so I keep BFS for easy understanding.
    public int[] findShortestPath(int agentX, int agentY, HashMap<String, Integer> utilities, HashMap<String, ArrayList<int[]>> shortestPaths, char direction) {
        int found = utilities.size();
        int maxUtility = Integer.MIN_VALUE;
        int maxSafeUtility = Integer.MIN_VALUE;
        int[] dest = new int[]{agentX, agentY, agentX, agentY, maxUtility, 0};
        Queue<ArrayList<int[]>> queue = new LinkedList<>();
        ArrayList<int[]> path = new ArrayList<>();
        path.add(new int[]{agentX, agentY, 0, getDirection(direction)});
        queue.add(path);
        HashSet<String> visited = new HashSet<>();


        while (!queue.isEmpty()) {
            ArrayList<int[]> current = queue.remove();
            int[] currentCell = current.get(current.size() - 1);
            int x = currentCell[0];
            int y = currentCell[1];
            int agentDirection = currentCell[3];
            String currPosStr = x + "-" + y;
            if (utilities.containsKey(currPosStr) && found > 0) {
                // Found a path lead to the frontier cell.
                ArrayList<int[]> currCopy = new ArrayList<>(current);
                currCopy.remove(0);
                int nextX = currCopy.get(0)[0];
                int nextY = currCopy.get(0)[1];
                int score = utilities.get(currPosStr) - currentCell[2] * multiplicity;  // expected score - steps
                utilities.put(currPosStr, score);
                dest[5] += score / multiplicity;
                // Keep searching until all shortest paths to each frontier cell are found.
                shortestPaths.put(currPosStr, currCopy);
                found -= 1;
                // This gives destination (safe or not) with the highest utility.
                if (score > maxUtility) {
                    maxUtility = score;
                    dest[0] = nextX;
                    dest[1] = nextY;
                    dest[4] = maxUtility;
                }
                // This gives safe destination with the highest utility.
                if (wumpusWorld.isCellSafe(x, y) && score > maxSafeUtility) {
                    maxSafeUtility = score;
                    dest[2] = nextX;
                    dest[3] = nextY;
                }
            }
            // Search every possible next step.
            for (int[] neighbor : wumpusWorld.getNeighbors(x, y)) {
                int nx = neighbor[0];
                int ny = neighbor[1];
                String posStr = nx + "-" + ny;
                if ((wumpusWorld.cells[nx][ny].safe == 1 || wumpusWorld.cells[nx][ny].frontier == 1)
                        && !visited.contains(posStr) && wumpusWorld.cells[x][y].frontier == 0) {
                    ArrayList<int[]> newPath = new ArrayList<>(current);
                    int[] stepDirection = calcSteps(x, y, nx, ny, agentDirection);
                    // calculate the total steps to the next cell.
                    newPath.add(new int[]{nx, ny, currentCell[2] + stepDirection[0], stepDirection[1]});
                    queue.add(newPath);
                    visited.add(posStr);
                }
            }
        }
//        if (debug) {
//            for (String wstr : wumpusWorld.wumpus) {
//                System.out.println("Debug:W at:" + wstr);
//            }
//            for (String pstr : wumpusWorld.pits) {
//                System.out.println("Debug:P at:" + pstr);
//            }
//            System.out.println(agentX + "" + agentY);
//            for (String key : shortestPaths.keySet()) {
//
//                if (shortestPaths.get(key).size() > 0) {
//                    for (int[] pos : shortestPaths.get(key)) {
//                        System.out.println(pos[0] + "" + pos[1]);
//                    }
//                }
//                System.out.println(key + ":" + utilities.get(key) / multiplicity + ":" + multiplicity);
//            }
//        }

        return dest;
    }
    // Wrap up the results from simulate and findShortestPath then return the best destination.
    public SearchResult fwdSearch(int agentPosX, int agentPosY, char direction, WumpusWorld wumpusWorld) {
        this.wumpusWorld = wumpusWorld;

        ArrayList<int[]> frontiers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {

                WumpusCell cell = this.wumpusWorld.cells[i][j];
                if (cell.frontier == 1) {
                    frontiers.add(new int[]{i, j});
                }
            }
        }

        int[] res = new int[frontiers.size()];
        HashMap<String, Integer> utilities = new HashMap<>();
        simulate(frontiers, 0, wumpusWorld, res);
        for (int i = 0; i < frontiers.size(); i++) {
            int X = frontiers.get(i)[0];
            int Y = frontiers.get(i)[1];
            utilities.put(X + "-" + Y, res[i]);
//            if (debug)
//                System.out.println("Debug:" + frontiers.get(i)[0] + "-" + frontiers.get(i)[1] + ":" + res[i] / multiplicity + ":" + multiplicity);
        }
        HashMap<String, ArrayList<int[]>> shortestPathes = new HashMap<>();
        int[] dest = findShortestPath(agentPosX, agentPosY, utilities, shortestPathes, direction);
        return new SearchResult(dest[0], dest[1], dest[2], dest[3], dest[4], dest[5], shortestPathes, utilities, multiplicity);
    }
    // Direction map.
    public int getDirection(Character direction) {
        switch (direction) {
            case 'N':
                return 0;
            case 'E':
                return 1;
            case 'S':
                return 2;
            case 'W':
                return 3;
        }
        return 0;
    }

    // N:0, E:1, S:2, W:3
    // Calculate the total steps including turnings to the next cell.
    public int[] calcSteps(int agentXPos, int agentYPos, int destinationX, int destinationY, int direction) {
        if (agentXPos < destinationX) {
            return new int[]{Math.abs(direction - 1) + 1, 1};
        } else if (agentXPos > destinationX) {
            return new int[]{Math.abs(direction - 3) + 1, 3};
        } else if (agentYPos < destinationY) {
            return new int[]{direction + 1, 0};
        } else if (agentYPos > destinationY) {
            return new int[]{Math.abs(direction - 2) + 1, 2};
        } else {
            return new int[]{1, Math.abs((direction + 1) % 4)};
        }
    }
}

class SearchResult {
    int destX, destY, safeX, safeY, maxUtility, multiplicity, expectedTotalScore;
    HashMap<String, Integer> utilities;
    HashMap<String, ArrayList<int[]>> shortestPaths;

    SearchResult(int x, int y, int sX, int sY, int maxUtility, int expectedTotalScore, HashMap<String,
            ArrayList<int[]>> shortestPathes, HashMap<String, Integer> utilities, int multiplicity) {
        this.destX = x;
        this.destY = y;
        this.safeX = sX;
        this.safeY = sY;
        this.maxUtility = maxUtility;
        this.shortestPaths = shortestPathes;
        this.utilities = utilities;
        this.multiplicity = multiplicity;
        this.expectedTotalScore = expectedTotalScore;
    }
}